#!/usr/bin/env python3

import matplotlib.pyplot as plt
import numpy as np


def read_data():
    # TODO: Add try-except
    input_file = open("output.txt", "r")
    data = input_file.read()
    input_file.close()

    components = data.split("*")
    print(components[0], end="")
    print(components[1], end="")
    print(components[2], end="")
    print(components[3], end="")
    print(components[4], end="")

    rc = components[0].split("\n")
    ROWS, COLS = rc[0], rc[1]


def main():
    read_data()
    # x = np.array([95, 96, 97, 98, 99, 100, 101])
    # y = np.array([1, 2, 3, 4, 5, 6, 7])
    # _, ax = plt.subplots()
    # ax.plot(x, y, linewidth=2.0)
    # plt.show()


if __name__ == "__main__":
    main()
