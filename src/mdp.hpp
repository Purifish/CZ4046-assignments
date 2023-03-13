#ifndef IA_MDP_H
#define IA_MDP_H

#include <iostream>
#include <cstdio>
#include <vector>
#include <unordered_map>
#include <fstream>
#include <cmath>
#include <climits>
#include <random> // for RNG

/*
    Alias definitions for convenience
*/
template <class A, class B>
using HashMap = std::unordered_map<A, B>;

template <class T>
using vect2d = std::vector<std::vector<T>>;

template <class T>
using vect = std::vector<T>;

/*
    StateProbability struct
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
    void valueIteration(const std::string &outputFileName);
    void policyIteration();
    void reset(bool random);

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

    /*
        Random Number Generator Initialisation (for policy iteration)
    */
    std::random_device dev;
    std::mt19937 rng;
    std::uniform_int_distribution<std::mt19937::result_type> randomAction;

    // private methods
    double expectedUtil(int r, int c, int a);
    vect<StateProbability> getProbabilities(int r, int c, int a);
    std::pair<int, int> getNextState(int r, int c, int a);
};

#endif