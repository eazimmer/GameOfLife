# GameOfLife
A Kotlin-based Android app simulation of John Conway's 1970 "A Game Of Life". Project for CSI-319 completed in Fall 2020.

"A grid of square cells will be presented. Each cell has eight neighbors. Each cell can be in one of two states—alive or dead. The cells
experience generations. Each generation, a living cell with two or three living neighbors stays alive. A cell
with any other number of neighbors (less or more) dies. A dead cell with three living neighbors comes to
life (Dart for Absolute Beginners p 138, Kopec).”

The implementation supports the following features:
- Allow the user to change the colors of alive and dead cells
- Allow the user to save grids to disk and reopen them
- Allow the user to clone a grid into a new activity
- Create a pulsing animation for living cells
