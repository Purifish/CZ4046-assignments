#include "mdp.hpp"

Mdp::Mdp(double _G, double _MAX_ERROR, vect2d<char> &_grid, vect2d<double> &_U, vect2d<int> &_PI, HashMap<char, double> &_rewards)
    : M(_U.size()), N(_U[0].size()), G(_G), MAX_ERROR(_MAX_ERROR), grid(_grid), U(_U), PI(_PI), rewards(_rewards), THRESH(MAX_ERROR * (1.0 - G) / G)
{
    rng = std::mt19937(dev());
    randomAction = std::uniform_int_distribution<std::mt19937::result_type>(0, 3);
}

/**
 * Resets the utility and policy arrays.
 * @param random
 * If set to true, a random policy will be generated.
 * Otherwise, the policy will be set to 'NONE'
 */
void Mdp::reset(bool random)
{
    for (int r = 0; r < M; r++)
    {
        for (int c = 0; c < N; c++)
        {
            U[r][c] = 0.0;
            PI[r][c] = grid[r][c] == '0' || !random ? 4 : randomAction(rng);
        }
    }
}

/**
 * Returns the intended next state given the current state and action
 */
std::pair<int, int> Mdp::getNextState(int r, int c, int a)
{
    int nextR, nextC;

    switch (a)
    {
    case 0: // North
        nextR = (r == 0 || grid[r - 1][c] == '0') ? r : r - 1;
        nextC = c;
        break;
    case 1: // East
        nextC = (c == N - 1 || grid[r][c + 1] == '0') ? c : c + 1;
        nextR = r;
        break;
    case 2: // South
        nextR = (r == M - 1 || grid[r + 1][c] == '0') ? r : r + 1;
        nextC = c;
        break;
    default: // West
        nextC = (c == 0 || grid[r][c - 1] == '0') ? c : c - 1;
        nextR = r;
    }

    return std::make_pair(nextR, nextC);
}

double Mdp::expectedUtil(int r, int c, int a)
{
    double util = 0.0;

    for (auto &sp : getProbabilities(r, c, a))
        util += sp.p * U[sp.cell.first][sp.cell.second];
    return util;
}

vect<StateProbability> Mdp::getProbabilities(int r, int c, int a)
{
    int toSkip = (a + 2) % 4; // skip the opposite action
    double p;                 // probability
    vect<StateProbability> probabilities;

    for (int curAction = 0; curAction < 4; curAction++)
    {
        if (curAction == toSkip)
            continue;

        p = curAction == a ? 0.8 : 0.1;
        probabilities.push_back(StateProbability{
            getNextState(r, c, curAction),
            p});
    }

    return probabilities;
}

void Mdp::valueIteration(const std::string &outputFileName)
{
    double delta;
    int i;
    vect2d<double> newUtil(M, vect<double>(N, 0));
    std::ofstream outputFile(outputFileName);

    outputFile << M << "\n"
               << N << "\n"
               << "*\n";

    for (i = 1; i <= INT_MAX; i++)
    {
        delta = 0.0;
        for (int r = 0; r < M; r++)
        {
            for (int c = 0; c < N; c++)
            {
                // Skip walls
                if (grid[r][c] == '0')
                    continue;

                // Select first action's expected utility as max utility
                newUtil[r][c] = expectedUtil(r, c, 0);
                PI[r][c] = 0;

                // Iterate thru rest of actions
                for (int a = 1; a < 4; a++)
                {
                    double currentExpectedUtil = expectedUtil(r, c, a);
                    if (currentExpectedUtil > newUtil[r][c])
                    {
                        newUtil[r][c] = currentExpectedUtil;
                        PI[r][c] = a;
                    }
                }
                newUtil[r][c] = newUtil[r][c] * G + rewards[grid[r][c]];
            }
        }

        // Update the values before proceeding to the next iteration
        for (int r = 0; r < M; r++)
        {
            for (int c = 0; c < N; c++)
            {
                delta = std::max(delta, fabs(newUtil[r][c] - U[r][c]));
                U[r][c] = newUtil[r][c];
                outputFile << U[r][c] << " ";
            }
            outputFile << "\n";
        }
        outputFile << "*\n";

        if (delta < THRESH)
            break;
    }
    std::cout << "Iterations: " << i << "\n";
    outputFile << "-";
    outputFile.close();
}

void Mdp::policyIteration()
{
    vect2d<double> newUtil(M, vect<double>(N, 0));
    int i;
    bool unchanged;

    for (i = 1; i <= INT_MAX; i++)
    {
        for (int k = 20; k > 0; k--)
        {
            for (int r = 0; r < M; r++)
            {
                for (int c = 0; c < N; c++)
                {
                    // Skip walls
                    if (grid[r][c] == '0')
                        continue;

                    newUtil[r][c] = expectedUtil(r, c, PI[r][c]) * G + rewards[grid[r][c]];
                }
            }
            for (int r = 0; r < M; r++)
            {
                for (int c = 0; c < N; c++)
                    U[r][c] = newUtil[r][c];
            }
        }

        unchanged = true;

        for (int r = 0; r < M; r++)
        {
            for (int c = 0; c < N; c++)
            {
                // Skip walls
                if (grid[r][c] == '0')
                    continue;

                int newPolicy = 0;

                // Select first action's expected utility as max utility
                newUtil[r][c] = expectedUtil(r, c, 0);

                // Iterate thru rest of actions
                for (int a = 1; a < 4; a++)
                {
                    double currentExpectedUtil = expectedUtil(r, c, a);
                    if (currentExpectedUtil > newUtil[r][c])
                    {
                        newUtil[r][c] = currentExpectedUtil;
                        newPolicy = a;
                    }
                }

                // Update policy
                if (newPolicy != PI[r][c])
                {
                    PI[r][c] = newPolicy;
                    unchanged = false;
                }
            }
        }

        if (unchanged)
            break;
    }
    std::cout << "Iterations: " << i << "\n";
}