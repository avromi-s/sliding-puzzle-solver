# Sliding Puzzle Solver
Sliding Puzzle Solver is an app that finds solutions for any unsolved [sliding puzzle](https://en.wikipedia.org/wiki/Sliding_puzzle).

A sliding puzzle is a game consisting of a grid of mixed-up numbers that need to be ordered sequentially by sliding them into the correct order. For example:

Unsolved                                                                                  |  Solved
:----------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------------------:
![image](https://github.com/user-attachments/assets/3e33bba3-4c38-45d7-ae78-b1f26ceedbfa) |  ![image](https://github.com/user-attachments/assets/29a9bdc1-316c-41a8-bc20-80b0e78119e8)

The app allows users to input any mixed-up board and find a solution using any of 6 available algorithms, namely: __Best-first__, __A*__, __BFS__, __DFS__, __DDFS__, or __IDDFS__.

## How to use
Enter in the numbers for a mixed-up board and click 'Solve Board'. Once a solution is found, you can cycle through the solution steps using the button bar on the bottom. The board size or algorithm can be changed in the app's settings.

Solving a board                                                                              |  Changing algorithm or board size
:-------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------:
![portrait](https://github.com/user-attachments/assets/944ed0c9-a62c-440e-9f48-6c252cfca56d) |  ![settings](https://github.com/user-attachments/assets/0b83bf9a-a64d-4c03-a25c-2438d37b9aea)


## Limitations
Time and space complexity constraints limit the practical use of some of the algorithms as the board size increases.
Within an immediate or reasonable time frame:
- 2x2 or 3x3 boards can be solved with all algorithms
- 4x4 boards can be solved with Best-first search, sometimes with A*, and occasionally with BFS or IDDFS
- 5x5 boards or bigger can sometimes be solved with best-first search, but are usually unable to be solved with the other methods
The above also depends on the difficulty of the board supplied, but that is the general trend.
