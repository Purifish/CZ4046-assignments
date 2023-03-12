#ifndef IA_MDP_H
#define IA_MDP_H

#include <iostream>
#include <cstdio>
#include <vector>
#include <unordered_map>
#include <fstream>
#include <cmath>
#include <climits>
#include <string>
#include <random> // for RNG

namespace iaAssignment
{
    template <class A, class B>
    using HashMap = std::unordered_map<A, B>;

    /*
        StateProbability struct
    */
    typedef struct
    {
        std::pair<int, int> cell; // cell (state)
        double p;                 // probability
    } StateProbability;

    class Mdp
    {
    public:
        // Constructor
        Mdp(const int _M, const int _N, const double _G, const double _MAX_ERROR, const char **_grid, double **_U, int **_PI, HashMap<char, double> &_rewards);

        // Destructor
        // ~Mdp();

        // Public member functions
        void valueIteration();
        void policyIteration();

    private:
        // Private member variables
        const char **grid;
        HashMap<char, double> &rewards;
        double **U;
        int **PI;
        const int M;
        const int N;
        const double G;
        const double MAX_ERROR;
        const double THRESH;

        double expectedUtil(int r, int c, int a);
        std::vector<StateProbability> getProbabilities(int r, int c, int a);
        std::pair<int, int> getNextState(int, int, int);
    };
}

#endif