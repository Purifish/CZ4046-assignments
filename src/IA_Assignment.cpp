#include <iostream>
#include <cstdio>
#include <vector>
#include <unordered_map>
#include <fstream>
#include <cmath>
#include <climits>
#include <string>
#include <iomanip> // std::setw

/*
    Global constants
*/
const int M = 6;          // num of rows
const int N = 6;          // num of cols
const int STATES = M * N; // num of states
const int ACTIONS = 4;    // num of actions

const int SR = 3; // start row
const int SC = 2; // start col

const double MAX_ERROR = 0.01;
const double G = 0.99; // discount factor, gamma
const double THRESH = MAX_ERROR * (1.0 - G) / G;

const std::string DIRECTIONS[] = {"NORTH", "EAST", "SOUTH", "WEST", "NONE"};

/*
    StateProbability struct
*/
typedef struct
{
    std::pair<int, int> cell; // cell (state)
    double p;                 // probability
} StateProbability;

/*
    Function declarations
*/
std::pair<int, int> getNextState(const char[][N], int, int, int);
std::vector<StateProbability> getProbabilities(const char[][N], int, int, int);
double expectedUtil(const char[][N], double[][N], int, int, int);
void valueIteration(const char[][N], double U[][N], int[][N], std::unordered_map<char, double> &);
void policyIteration(const char[][N], double U[][N], int[][N], std::unordered_map<char, double> &);

int main()
{
    double U[M][N] = {0}; // Utilities array
    int PI[M][N];         // Policies array

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
    std::unordered_map<char, double> rewards;
    rewards['g'] = 1.0;
    rewards['w'] = -0.04;
    rewards['o'] = -1.0;

    // grid check
    for (int r = 0; r < M; r++)
    {
        for (int c = 0; c < N; c++)
            std::cout << grid[r][c] << " ";
        std::cout << "\n";
    }
    std::cout << "\n";

    valueIteration(grid, U, PI, rewards);

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
    for (int r = 0; r < M; r++)
    {
        for (int c = 0; c < N; c++)
        {
            U[r][c] = 0.0;
            PI[r][c] = grid[r][c] == '0' ? 4 : 0;
        }
    }
    policyIteration(grid, U, PI, rewards);

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

void valueIteration(const char grid[][N], double U[][N], int PI[][N], std::unordered_map<char, double> &rewards)
{
    double delta;
    double newUtil[M][N] = {0};
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
                newUtil[r][c] = expectedUtil(grid, U, r, c, 0);
                PI[r][c] = 0;

                // Iterate thru rest of actions
                for (int a = 1; a < ACTIONS; a++)
                {
                    double currentExpectedUtil = expectedUtil(grid, U, r, c, a);
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

void policyIteration(const char grid[][N], double U[][N], int PI[][N], std::unordered_map<char, double> &rewards)
{
    double newUtil[M][N] = {0};
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

                    newUtil[r][c] = expectedUtil(grid, U, r, c, PI[r][c]) * G + rewards[grid[r][c]];
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
                newUtil[r][c] = expectedUtil(grid, U, r, c, 0);

                // Iterate thru rest of actions
                for (int a = 1; a < ACTIONS; a++)
                {
                    double currentExpectedUtil = expectedUtil(grid, U, r, c, a);
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
