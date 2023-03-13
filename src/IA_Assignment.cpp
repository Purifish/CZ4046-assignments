#include <iostream>
#include <cstdio>
#include <string>
#include <iomanip> // for std::setw
#include <memory>

#include "mdp.hpp"

void printGrid(vect2d<char> &grid);     //
void printUtilities(vect2d<double> &U); //
void printPolicy(vect2d<int> &PI);      //
void generateRandomGrid(vect2d<char> &grid, size_t M, size_t N);

/*
    Constants
*/
const double MAX_ERROR = 0.01; // epsilon value
const double G = 0.99;         // discount factor, gamma
const std::string DIRECTIONS[] = {"NORTH", "EAST", "SOUTH", "WEST", "NONE"};
const char CELL_TYPES[] = {'w', 'g', 'o', '0'};

int main()
{
    /*
        Legend:
            'w': white
            'g': green
            'o': orange
            '0': wall
    */
    vect2d<char> grid;
    bool part1 = false;
    bool random = false;

    if (part1)
    {
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
        grid = {
            {'0', '0', 'o', 'g', '0', '0', 'g', 'o', 'o', 'o', 'o', '0'},
            {'0', '0', 'o', '0', '0', '0', 'o', '0', 'g', 'o', '0', 'g'},
            {'g', 'o', 'o', '0', 'g', '0', '0', 'o', 'g', '0', 'w', 'g'},
            {'0', '0', '0', '0', 'w', 'o', 'o', 'w', 'g', 'o', 'o', 'w'},
            {'0', '0', 'o', 'o', 'g', '0', 'o', 'o', '0', 'g', 'g', '0'},
            {'0', '0', '0', 'g', 'w', 'o', 'o', 'g', '0', 'w', '0', 'w'},
            {'g', '0', '0', 'g', 'g', 'o', 'g', 'g', 'w', '0', 'g', 'w'},
            {'o', '0', 'w', 'o', 'o', 'o', '0', '0', 'o', 'g', 'w', 'g'},
            {'0', '0', '0', 'o', 'g', '0', 'o', '0', '0', 'o', '0', 'g'},
            {'0', '0', '0', 'o', 'o', 'o', 'o', 'g', '0', '0', 'g', 'w'},
            {'0', 'g', 'o', '0', 'g', '0', '0', 'o', '0', '0', 'o', 'o'},
            {'0', '0', 'g', 'o', 'g', '0', '0', 'g', 'o', '0', 'o', 'g'}};
    }
    else
    {
        generateRandomGrid(grid, 12, 12);
    }

    printGrid(grid);

    int M = grid.size();    // number of rows
    int N = grid[0].size(); // number of cols

    /*
        Local variables
    */
    vect2d<double> U(M, vect<double>(N, 0)); // Utilities array
    vect2d<int> PI(M, vect<int>(N, 4));      // policy array, initalized with NONE

    /*
        Hash map representing the rewards for each type of cell
    */
    HashMap<char, double> rewards = {
        {'g', 1.0},
        {'w', -0.04},
        {'o', -1.0}};

    auto mdp = std::make_unique<Mdp>(G, MAX_ERROR, grid, U, PI, rewards);

    mdp->valueIteration("value-iteration-out.txt");
    printPolicy(PI);
    printUtilities(U);

    mdp->reset(true);

    std::cout << "\nInitial (random) Policy:\n\n";
    printPolicy(PI);
    mdp->policyIteration();
    printPolicy(PI);
    printUtilities(U);

    return 0;
}

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
                grid[r][c] = '0';
            else if (randNum <= 50)
                grid[r][c] = 'w';
            else if (randNum <= 75)
                grid[r][c] = 'g';
            else
                grid[r][c] = 'o';
        }
    }
}

void printGrid(vect2d<char> &grid)
{
    std::cout << "The Grid:\n\n";
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
