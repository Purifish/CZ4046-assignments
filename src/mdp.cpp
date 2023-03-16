#include "../include/mdp.hpp"

Mdp::Mdp(double _G, double _MAX_ERROR, vect2d<char> &_grid, vect2d<double> &_U, vect2d<int> &_PI, HashMap<char, double> &_rewards)
    : M(_U.size()), N(_U[0].size()), G(_G), MAX_ERROR(_MAX_ERROR), grid(_grid), U(_U), PI(_PI), rewards(_rewards), THRESH(MAX_ERROR * (1.0 - G) / G)
{
    rng = std::mt19937(dev());
    randomAction = std::uniform_int_distribution<std::mt19937::result_type>(0, 3);
}

/**
 * Resets the utility and policy vectors
 * @param random
 * If set to true (which is the default value), a random policy will be generated.
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
 * @param r The row of the state
 * @param c The column of the state
 * @param a The action (an int between 1 and 4)
 * @return A pair of ints; the row and column of the next state
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

/**
 * Returns the expected value* of an action in a particular state
 * @param r The row of the state
 * @param c The column of the state
 * @param a The action (an int between 1 and 4)
 * @note The value* is NOT actually the utility, but rather the summation in equation 17.5 of the reference book.
 * @return A double holding the expected value of the action
 */
double Mdp::expectedUtil(int r, int c, int a)
{
    double util = 0.0;

    for (auto &sp : getProbabilities(r, c, a))
        util += sp.p * U[sp.cell.first][sp.cell.second];
    return util;
}

/**
 * Returns the possible resultant states and their probabilities, given a state and action
 * @param r The row of the state
 * @param c The column of the state
 * @param a The action (an int between 1 and 4)
 * @return A StateProbability vector containing at most 3 elements
 */
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

/**
 * The value iteration algorithm, following the reference book
 * @param outputFileName The name of the file that will contain the utility values of each iteration.
 * Leave blank to skip file-writing.
 */
void Mdp::valueIteration(const std::string &outputFileName)
{
    double delta;
    int iterations;
    vect2d<double> newUtil(M, vect<double>(N, 0));
    std::ofstream outputFile;

    if (outputFileName != "")
    {
        outputFile = std::ofstream(outputFileName);
        outputFile << M << "\n"
                   << N << "\n"
                   << "*\n";
    }

    for (iterations = 1; iterations <= INT_MAX; iterations++)
    {
        delta = 0.0;
        for (int r = 0; r < M; r++)
        {
            for (int c = 0; c < N; c++)
            {
                // skip walls
                if (grid[r][c] == '0')
                    continue;

                // select first action's expected utility as max utility
                newUtil[r][c] = expectedUtil(r, c, 0);
                PI[r][c] = 0;

                // iterate thru rest of actions
                for (int a = 1; a < 4; a++)
                {
                    double currentExpectedUtil = expectedUtil(r, c, a);
                    if (currentExpectedUtil > newUtil[r][c])
                    {
                        newUtil[r][c] = currentExpectedUtil; // update utility if needed
                        PI[r][c] = a;                        // likewise for policy
                    }
                }
                newUtil[r][c] = newUtil[r][c] * G + rewards[grid[r][c]];
            }
        }

        // update the values before proceeding to the next iteration
        for (int r = 0; r < M; r++)
        {
            for (int c = 0; c < N; c++)
            {
                delta = std::max(delta, fabs(newUtil[r][c] - U[r][c]));
                U[r][c] = newUtil[r][c];

                if (outputFileName != "")
                    outputFile << U[r][c] << " ";
            }
            if (outputFileName != "")
                outputFile << "\n";
        }
        if (outputFileName != "")
            outputFile << "*\n";

        if (delta < THRESH)
            break;
    }

    std::cout << "Iterations: " << iterations << "\n";

    if (outputFileName != "")
    {
        outputFile << "-";
        outputFile.close();
    }
}

/**
 * The modified policy iteration algorithm, following the reference book
 */
void Mdp::policyIteration()
{
    vect2d<double> newUtil(M, vect<double>(N, 0));
    int iterations;
    bool unchanged;

    for (iterations = 1; iterations <= INT_MAX; iterations++)
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
    std::cout << "Iterations: " << iterations << "\n";
}