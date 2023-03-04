#!/usr/bin/env python3

import matplotlib.pyplot as plt
import numpy as np


def read_data():
    # TODO: Add try-except
    input_file = open("output.txt", "r")
    data = input_file.read()
    input_file.close()

    components = data.split("*\n")

    x = np.array([i for i in range(1, len(components) - 1)])
    # x = np.array([i for i in range(1, 650)])

    rc = components[0].split("\n")
    ROWS, COLS = int(rc[0]), int(rc[1])

    util = [[[] for j in range(COLS)] for i in range(ROWS)]

    for i in range(1, len(components) - 1):
        # for i in range(1, 650):
        lines = components[i].split("\n")
        for r in range(ROWS):
            values = lines[r].split(" ")
            for c in range(COLS):
                util[r][c].append(float(values[c]))

    return util, x, ROWS, COLS


def main():
    util, x, M, N = read_data()
    for r in range(M):
        for c in range(N):
            util[r][c] = np.array(util[r][c])
            # print(str(r) + "," + str(c) + ": " + str(util[r][c]))

    plt.figure(figsize=(14, 10))

    for r in range(M):
        for c in range(N):
            plt.plot(x, util[r][c])

    plt.legend([str(x // M) + "," + str(x % M) for x in range(M * N)],
               loc='center right',
               bbox_to_anchor=(1, 0.5))
    plt.yticks([0, 20, 40, 60, 80, 100])
    plt.xlabel('Number of Iterations')
    plt.ylabel('Utility Values')
    plt.show()


if __name__ == "__main__":
    main()
