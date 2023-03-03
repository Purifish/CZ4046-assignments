#include <iostream>
#include <cstdio>
#include <vector>
#include <unordered_map>
#include <fstream>

const int M = 6;          // num of rows
const int N = 6;          // num of cols
const int STATES = M * N; // num of states
const int ACTIONS = 4;    // num of actions

const int SR = 3; // start row
const int SC = 2; // start col

const double G = 0.99; // discount factor, gamma
// const double G = 0.94606; // discount factor, gamma

typedef struct
{
    std::pair<int, int> cell; // cell (state)
    double p;                 // probability
} StateProbability;

std::pair<int, int> getNextState(const char[][N], int, int, int);
std::vector<StateProbability> getProbabilities(const char[][N], int, int, int);
double expectedUtil(const char[][N], double[][N], int, int, int);
void valueIteration(const char[][N], double U[][N], std::unordered_map<char, double> &);

int main()
{
    double U[M][N] = {0}; // Utilities array

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
    std::unordered_map<char, double> rewards;
    rewards['g'] = 1.0;
    rewards['w'] = -0.04;
    rewards['o'] = -1.0;

    // grid check
    for (int r = 0; r < M; r++)
    {
        for (int c = 0; c < N; c++)
        {
            std::cout << grid[r][c] << " ";
        }
        std::cout << "\n";
    }
    std::cout << "\n";

    valueIteration(grid, U, rewards);

    return 0;
}

std::pair<int, int> getNextState(const char grid[][N], int r, int c, int a)
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

std::vector<StateProbability> getProbabilities(const char grid[][N], int r, int c, int a)
{
    int toSkip = (a + 2) % 4; // skip the opposite action
    double p;                 // probability
    std::vector<StateProbability> probabilities;

    for (int curAction = 0; curAction < ACTIONS; curAction++)
    {
        if (curAction == toSkip)
            continue;

        p = curAction == a ? 0.8 : 0.1;
        probabilities.push_back(StateProbability{
            getNextState(grid, r, c, curAction),
            p});
    }

    return probabilities;
}

/*
    Actions Legend:

    0: North
    1: East
    2: South
    3: West
*/
double expectedUtil(const char grid[][N], double U[][N], int r, int c, int a)
{
    double util = 0.0;

    for (auto &sp : getProbabilities(grid, r, c, a))
    {
        util += sp.p * U[sp.cell.first][sp.cell.second];
    }
    // returns summation of P * S, for each s'
    // e.g. return 0.8 * -1 + 0.1 * 1 + 0.1 * 0
    return util;
}

void valueIteration(const char grid[][N], double U[][N], std::unordered_map<char, double> &rewards)
{
    double newUtil[M][N] = {0};
    std::ofstream outputFile("output.txt");

    outputFile << M << "\n"
               << N << "\n"
               << "*\n";

    // Actual terminating condition is from the Book
    for (int i = 1; i <= 688; i++)
    {
        for (int r = 0; r < M; r++)
        {
            for (int c = 0; c < N; c++)
            {
                // Skip walls
                if (grid[r][c] == '0')
                    continue;

                // Select first action's expected utility as max utility
                newUtil[r][c] = expectedUtil(grid, U, r, c, 0);

                // Iterate thru rest of actions
                for (int a = 1; a < ACTIONS; a++)
                {
                    newUtil[r][c] = std::max(newUtil[r][c], expectedUtil(grid, U, r, c, a));
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
                U[r][c] = newUtil[r][c];
                // std::printf("%8.3f ", U[r][c]);
                outputFile << U[r][c] << " ";
            }
            // std::cout << "\n";
            outputFile << "\n";
        }
        // std::cout << "\n";
        outputFile << "*\n";
    }
    outputFile << "-";
    outputFile.close();
}
