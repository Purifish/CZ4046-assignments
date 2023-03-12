#include <iostream>
#include <cstdio>
#include <string>
#include <iomanip> // for std::setw
#include <random>  // for RNG
#include <memory>

#include "mdp.hpp"

namespace iaAssignment
{
    void printUtilities(char **U, int M, int N); // TODO: complete definition

    int main()
    {
        /*
            Constants
        */
        const int M = 6; // num of rows
        const int N = 6; // num of cols

        const double MAX_ERROR = 0.01;
        const double G = 0.99; // discount factor, gamma

        const std::string DIRECTIONS[] = {"NORTH", "EAST", "SOUTH", "WEST", "NONE"};

        /*
            Local variables
        */
        double U[M][N] = {0}; // Utilities array
        int PI[M][N];         // Policies array

        /*
            Random Number Generator Initialisation (for policy iteration)
        */
        std::random_device dev;
        std::mt19937 rng(dev());
        std::uniform_int_distribution<std::mt19937::result_type> randomAction(0, 3);

        for (int r = 0; r < M; r++)
            std::fill_n(PI[r], N, 4);

        /*
            Grid initialisation.

            Legend:
                'w': white
                'g': green
                'o': orange
                '0': wall
        */
        const char grid[M][N] = {
            {'g', '0', 'g', 'w', 'w', 'g'},
            {'w', 'o', 'w', 'g', '0', 'o'},
            {'w', 'w', 'o', 'w', 'g', 'w'},
            {'w', 'w', 'w', 'o', 'w', 'g'},
            {'w', '0', '0', '0', 'o', 'w'},
            {'w', 'w', 'w', 'w', 'w', 'w'}};

        /*
            Hash map representing the rewards for each type of cell
        */
        HashMap<char, double> rewards;
        rewards['g'] = 1.0;
        rewards['w'] = -0.04;
        rewards['o'] = -1.0;

        std::unique_ptr<Mdp> mdp = std::make_unique<Mdp>(M, N, G, MAX_ERROR, grid, U, PI, rewards);

        // grid check
        for (int r = 0; r < M; r++)
        {
            for (int c = 0; c < N; c++)
                std::cout << grid[r][c] << " ";
            std::cout << "\n";
        }
        std::cout << "\n";

        mdp->valueIteration();

        for (int r = 0; r < M; r++)
        {
            for (int c = 0; c < N; c++)
                std::cout << std::setw(8) << DIRECTIONS[PI[r][c]];
            std::cout << "\n";
        }
        std::cout << "\n";

        for (int r = 0; r < M; r++)
        {
            for (int c = 0; c < N; c++)
                std::printf("%8.3f ", U[r][c]);
            std::cout << "\n";
        }
        std::cout << "\n";

        // reset utility and policy arrays for policy iteration
        std::cout << "\nInitial (random) Policy:\n\n";
        for (int r = 0; r < M; r++)
        {
            for (int c = 0; c < N; c++)
            {
                U[r][c] = 0.0;
                PI[r][c] = grid[r][c] == '0' ? 4 : randomAction(rng);
                std::cout << std::setw(8) << DIRECTIONS[PI[r][c]];
            }
            std::cout << "\n";
        }
        std::cout << "\n";

        mdp->policyIteration();

        for (int r = 0; r < M; r++)
        {
            for (int c = 0; c < N; c++)
                std::cout << std::setw(8) << DIRECTIONS[PI[r][c]];
            std::cout << "\n";
        }
        std::cout << "\n";

        for (int r = 0; r < M; r++)
        {
            for (int c = 0; c < N; c++)
                std::printf("%8.3f ", U[r][c]);
            std::cout << "\n";
        }
        std::cout << "\n";

        return 0;
    }

} // namespace iaAssignment
