#!/usr/bin/env python3

import matplotlib.pyplot as plt
import numpy as np


def read_data(file_name: str):
    try:
        with open(file_name, "r") as input_file:
            data = input_file.read()
    except IOError:
        try:
            with open("../" + file_name, "r") as input_file:
                data = input_file.read()
        except IOError:
            return None

    components = data.split("*\n")

    rc = components[0].split("\n")
    rows, cols = int(rc[0]), int(rc[1])

    util = np.empty([rows, cols, len(components) - 2])

    for i in range(1, len(components) - 1):
        lines = components[i].strip().split("\n")
        for r in range(rows):
            values = list(map(float, lines[r].strip().split()))
            util[r, :, i-1] = values

    x = np.arange(1, len(components)-1)

    return util, x, rows, cols


def main():
    try:
        util, x, M, N = read_data("value-iteration-output.txt")
    except:
        print("Error: File not found!")
        return

    for r in range(M):
        for c in range(N):
            util[r][c] = np.array(util[r][c])

    plt.figure(figsize=(14, 8))

    for r in range(M):
        for c in range(N):
            if util[r][c][0] != 0:
                plt.plot(x, util[r][c], label=str(r) + "," + str(c))

    plt.legend(
        title="States",
        loc='center left',
        bbox_to_anchor=(1.03, 0.5)
    )

    plt.yticks([0, 20, 40, 60, 80, 100])
    plt.xlabel('Number of Iterations')
    plt.ylabel('Utility Values')
    plt.show()


if __name__ == "__main__":
    main()
