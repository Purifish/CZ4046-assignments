#include "mdp.hpp"

namespace iaAssignment
{
    Mdp::Mdp(const int _M, const int _N, const double _G, const double _MAX_ERROR, const char **_grid, double **_U, int **_PI, HashMap<char, double> &_rewards)
        : M(_M), N(_N), G(_G), MAX_ERROR(_MAX_ERROR), grid(_grid), U(_U), PI(_PI), rewards(_rewards), THRESH(MAX_ERROR * (1.0 - G) / G)
    {
    }

    /*
        Returns the intended next state given the current state and action
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
        {
            util += sp.p * U[sp.cell.first][sp.cell.second];
        }
        // returns summation of P * S, for each s'
        // e.g. return 0.8 * -1 + 0.1 * 1 + 0.1 * 0
        return util;
    }

    std::vector<StateProbability> Mdp::getProbabilities(int r, int c, int a)
    {
        int toSkip = (a + 2) % 4; // skip the opposite action
        double p;                 // probability
        std::vector<StateProbability> probabilities;

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

    void Mdp::valueIteration()
    {
        double delta;
        std::vector<std::vector<double>> newUtil(M, std::vector<double>(N, 0));
        int i;
        std::ofstream outputFile("output.txt");

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

            // std::cout << "Iteration " << i << ":\n";
            // std::cout << "==========================\n\n";

            // Update the values before proceeding to the next iteration
            for (int r = 0; r < M; r++)
            {
                for (int c = 0; c < N; c++)
                {
                    delta = std::max(delta, fabs(newUtil[r][c] - U[r][c]));
                    U[r][c] = newUtil[r][c];
                    // std::printf("%8.3f ", U[r][c]);
                    // std::printf("%3d ", PI[r][c]);
                    outputFile << U[r][c] << " ";
                }
                // std::cout << "\n";
                outputFile << "\n";
            }
            // std::cout << "\n";
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
        std::vector<std::vector<double>> newUtil(M, std::vector<double>(N, 0));
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

}