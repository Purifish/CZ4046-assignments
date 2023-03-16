#ifndef IA_MDP_H
#define IA_MDP_H

#include <iostream>
#include <cstdio>
#include <vector>
#include <unordered_map>
#include <fstream>
#include <cmath>
#include <climits>
#include <random>

/*
 * Alias definitions for convenience
 * See https://en.cppreference.com/w/cpp/language/type_alias for info about aliasing in C++
 */
template <class A, class B>
using HashMap = std::unordered_map<A, B>; // hashmap alias

template <class T>
using vect2d = std::vector<std::vector<T>>; // 2D vector alias

template <class T>
using vect = std::vector<T>; // 1D vector alias

/**
 * StateProbability Struct
 * Represents the probability of transitioning to a state
 * @param cell
 * The state
 * @param p
 * The probability
 */
typedef struct
{
    std::pair<int, int> cell; // cell (state)
    double p;                 // probability
} StateProbability;

/**
 * Mdp Class
 * Encapsulates the value and policy iteration algorithms
 */
class Mdp
{
public:
    // constructor
    Mdp(double _G, double _MAX_ERROR, vect2d<char> &_grid, vect2d<double> &_U, vect2d<int> &_PI, HashMap<char, double> &_rewards);

    // public methods
    void valueIteration(const std::string &outputFileName = "");
    void policyIteration();
    void reset(bool random = true);

private:
    // private member variables
    int M;
    int N;
    double G;
    double MAX_ERROR;
    double THRESH;
    HashMap<char, double> &rewards;
    vect2d<char> &grid;
    vect2d<double> &U;
    vect2d<int> &PI;

    // Random Number Generator Initialisation (for policy iteration)
    std::random_device dev;
    std::mt19937 rng;
    std::uniform_int_distribution<std::mt19937::result_type> randomAction;

    // private methods
    double expectedUtil(int r, int c, int a);
    vect<StateProbability> getProbabilities(int r, int c, int a);
    std::pair<int, int> getNextState(int r, int c, int a);
};

#endif