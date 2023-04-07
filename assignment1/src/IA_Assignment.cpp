#include <iostream>
#include <cstdio>
#include <string>
#include <iomanip>
#include <memory>

#include "../include/mdp.hpp"

/*
    Function declarations
*/
void printGrid(vect2d<char> &grid);
void printUtilities(vect2d<double> &U);
void printPolicy(vect2d<int> &PI);
void generateRandomGrid(vect2d<char> &grid, size_t M, size_t N);

/*
    Constants
*/
const double MAX_ERROR = 0.01; // epsilon value (same as c value)
const double G = 0.99;         // discount factor, gamma
const std::string DIRECTIONS[] = {"NORTH", "EAST", "SOUTH", "WEST", "NONE"};

int main()
{
    vect2d<char> grid;  // 'w': white, 'g': green, 'o': orange, '0': wall
    bool part1 = false; // set this to true to use pre-defined maze for part 1
    bool random = true; // set this to true to use a randomly-generated maze for part 2

    if (part1)
    {
        // pre-defined maze for part 1
        grid = {
            {'g', '0', 'g', 'w', 'w', 'g'},
            {'w', 'o', 'w', 'g', '0', 'o'},
            {'w', 'w', 'o', 'w', 'g', 'w'},
            {'w', 'w', 'w', 'o', 'w', 'g'},
            {'w', '0', '0', '0', 'o', 'w'},
            {'w', 'w', 'w', 'w', 'w', 'w'}};
    }
    else if (!random)
    {
        // pre-defined maze for part 2 (randomly-generated previously)
        grid = {
            {'w', 'w', 'o', 'g', 'w', 'w', 'g', 'o', 'o', 'o', 'o', 'w'},
            {'w', 'w', 'o', 'w', 'w', 'w', 'o', 'w', 'g', 'o', 'w', 'g'},
            {'g', 'o', 'o', 'w', 'g', 'w', 'w', 'o', 'g', 'w', '0', 'g'},
            {'w', 'w', 'w', 'w', '0', 'o', 'o', '0', 'g', 'o', 'o', '0'},
            {'w', 'w', 'o', 'o', 'g', 'w', 'o', 'o', 'w', 'g', 'g', 'w'},
            {'w', 'w', 'w', 'g', '0', 'o', 'o', 'g', 'w', '0', 'w', '0'},
            {'g', 'w', 'w', 'g', 'g', 'o', 'g', 'g', '0', 'w', 'g', '0'},
            {'o', 'w', '0', 'o', 'o', 'o', 'w', 'w', 'o', 'g', '0', 'g'},
            {'w', 'w', 'w', 'o', 'g', 'w', 'o', 'w', 'w', 'o', 'w', 'g'},
            {'w', 'w', 'w', 'o', 'o', 'o', 'o', 'g', 'w', 'w', 'g', '0'},
            {'w', 'g', 'o', 'w', 'g', 'w', 'w', 'o', 'w', 'w', 'o', 'o'},
            {'w', 'w', 'g', 'o', 'g', 'w', 'w', 'g', 'o', 'w', 'o', 'g'}};
    }
    else
    {
        generateRandomGrid(grid, 12, 12);
    }

    printGrid(grid);

    /*
        Local variables
    */
    size_t M = grid.size();                  // number of rows
    size_t N = grid[0].size();               // number of cols
    vect2d<double> U(M, vect<double>(N, 0)); // Utilities array
    vect2d<int> PI(M, vect<int>(N, 4));      // policy array, initalized with NONE

    // Hash map representing the rewards for each type of cell
    HashMap<char, double> rewards = {
        {'g', 1.0},
        {'w', -0.04},
        {'o', -1.0}};

    // Mdp instantiation
    auto mdp = std::make_unique<Mdp>(G, MAX_ERROR, grid, U, PI, rewards);

    // perform value iteration
    std::cout << "[Value Iteration]\n";
    std::cout << "=======================================================\n";
    mdp->valueIteration();
    std::cout << "\nOptimal Policy:\n";
    printPolicy(PI);
    std::cout << "Utility Values:\n";
    printUtilities(U);
    std::cout << "=======================================================\n";

    // reset U and PI, choose random option for PI. (pass false as argument for non-random policy)
    mdp->reset();
    std::cout << "\n[Policy Iteration]\n";
    std::cout << "=======================================================\n";
    std::cout << "Initial (random) Policy:\n\n";
    printPolicy(PI);

    // perform policy iteration
    mdp->policyIteration();
    std::cout << "\nOptimal Policy:\n";
    printPolicy(PI);
    std::cout << "Utility Values:\n";
    printUtilities(U);
    std::cout << "=======================================================\n";

    return 0;
}

/**
 * Generates a random maze and stores it in reference vector
 * @param grid
 * The 2D-vector ref to store the randome maze
 * @param M
 * The number of rows of the maze
 * @param N
 * The number of columns of the maze
 */
void generateRandomGrid(vect2d<char> &grid, size_t M, size_t N)
{
    static std::random_device dev;
    static std::mt19937 rng(dev());
    static std::uniform_int_distribution<std::mt19937::result_type> randomCellType(1, 100);

    int randNum;
    grid = vect2d<char>(M, vect<char>(N));

    for (size_t r = 0; r < M; r++)
    {
        for (size_t c = 0; c < N; c++)
        {
            randNum = randomCellType(rng);
            if (randNum <= 40)
                grid[r][c] = 'w'; // 40% chance
            else if (randNum <= 50)
                grid[r][c] = '0'; // 10% chance
            else if (randNum <= 75)
                grid[r][c] = 'g'; // 25% chance
            else
                grid[r][c] = 'o'; // 25% chance
        }
    }
}

void printGrid(vect2d<char> &grid)
{
    std::cout << "The Maze:\n\n";
    for (size_t r = 0; r < grid.size(); r++)
    {
        for (size_t c = 0; c < grid[0].size(); c++)
            std::cout << grid[r][c] << " ";
        std::cout << "\n";
    }
    std::cout << "\n\n";
}

void printUtilities(vect2d<double> &U)
{
    for (size_t r = 0; r < U.size(); r++)
    {
        for (size_t c = 0; c < U[0].size(); c++)
            std::printf("%8.3f ", U[r][c]);
        std::cout << "\n";
    }
    std::cout << "\n";
}

void printPolicy(vect2d<int> &PI)
{
    for (size_t r = 0; r < PI.size(); r++)
    {
        for (size_t c = 0; c < PI[0].size(); c++)
            std::cout << std::setw(8) << DIRECTIONS[PI[r][c]];
        std::cout << "\n";
    }
    std::cout << "\n";
}
