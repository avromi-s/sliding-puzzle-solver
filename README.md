# sliding-puzzle-solver

This program takes a sliding puzzle board and returns 1 or more solutions to it.

The program allows any or all of 6 informed and uninformed search methods to be used, namely:
[Best-first](https://github.com/avrohom-schneierson/sliding-puzzle-solver/blob/d9bb56c91acf60e0fe4e4361cf0b3ef0ea0c7c79/src/Classes/SearchMethods/BestFirst.java),
[A*](https://github.com/avrohom-schneierson/sliding-puzzle-solver/blob/d9bb56c91acf60e0fe4e4361cf0b3ef0ea0c7c79/src/Classes/SearchMethods/AStar.java),
[BFS](https://github.com/avrohom-schneierson/sliding-puzzle-solver/blob/d9bb56c91acf60e0fe4e4361cf0b3ef0ea0c7c79/src/Classes/SearchMethods/BFS.java),
[DFS](https://github.com/avrohom-schneierson/sliding-puzzle-solver/blob/d9bb56c91acf60e0fe4e4361cf0b3ef0ea0c7c79/src/Classes/SearchMethods/DFS.java),
[DDFS](https://github.com/avrohom-schneierson/sliding-puzzle-solver/blob/d9bb56c91acf60e0fe4e4361cf0b3ef0ea0c7c79/src/Classes/SearchMethods/DDFS.java),
or [IDDFS](https://github.com/avrohom-schneierson/sliding-puzzle-solver/blob/d9bb56c91acf60e0fe4e4361cf0b3ef0ea0c7c79/src/Classes/SearchMethods/IDDFS.java).

The program can accept boards of any size, however, due to complexity constraints bigger boards may run out of memory or take significant time to solve.

Within an immediate or reasonable time frame:
* **2x2** or **3x3** boards can be solved with all algorithms
* **4x4** boards can be solved with Best-first search, sometimes with A*, and rarely with BFS
* **5x5** boards or bigger can sometimes be solved with best-first search, but are unable to be solved with the other methods

The above also depends on the difficulty of the specific board, but that is the general trend.
