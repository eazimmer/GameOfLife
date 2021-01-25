package com.example.project2

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Grid(var rows: Int,
           var columns: Int) : Parcelable { // Used as solution to pass objects through intent https://stackoverflow.com/questions/47593205/how-to-pass-custom-object-via-intent-in-kotlin

    // 2D Array of Arrays containing cell objects
    var cells: MutableList<MutableList<Cell>> = mutableListOf()

    init {
        // Produce Grid of cells
        for (row in 0 until rows) { // Cycle through desired number of rows

            // Create a list representing a row of cells
            var rowOfCells: MutableList<Cell> = mutableListOf()

            // Add the necessary number of cells
            for (col in 0 until columns) {
                rowOfCells.add(Cell(row, col, false))
            }

            // Append row of cells into the grid
            cells.add(rowOfCells)
        }
    }

    // TEST: Display contents of grid
    fun printGrid() {
        println(cells)
    }

    // Return current grid of cells
    fun exportGrid(): MutableList<MutableList<Cell>> {
        return cells.map{ it -> it.map { it.copy() }} as MutableList<MutableList<Cell>>
    }

    // Load a grid of cells
    fun loadGrid(newCells: MutableList<MutableList<Cell>>) {
        cells = newCells
    }

    fun nextGeneration() {
        val livingNeighborsCount = MutableList(20) {MutableList(20) { 0 }}

        // Counts the number of neighbors a cell has and stores it in the array
        for(i in 0 until rows) {
            for(j in 0 until columns){

                // Variables to save positions left and right of row and column
                val leftOfRow: Int = i + rows - 1;
                val rightOfRow: Int = i + 1;
                val leftOfColumn: Int = j + columns - 1;
                val rightOfColumn: Int = j + 1;

                // Checks to see if the cells are alive or dead. If they are alive
                // it increments the count for living neighbors.
                if ( cells[i][j].status ) {
                    livingNeighborsCount[leftOfRow % rows][leftOfColumn % columns]++;
                    livingNeighborsCount[leftOfRow % rows][j % columns]++;
                    livingNeighborsCount[(i + rows - 1) % rows][rightOfColumn % columns]++;
                    livingNeighborsCount[i % rows][leftOfColumn % columns]++;
                    livingNeighborsCount[i % rows][rightOfColumn % columns]++;
                    livingNeighborsCount[rightOfRow % rows][leftOfColumn % columns]++;
                    livingNeighborsCount[rightOfRow % rows][j % columns]++;
                    livingNeighborsCount[rightOfRow % rows][rightOfColumn % columns]++;
                }
            }
        }

        // Changes the status of the cell based on the number of living
        // neighbors it has.
        for(i in 0 until rows) {
            for(j in 0 until columns){
                // If the cell has 4 or more living neighbors, it dies
                // by overcrowding.
                if (livingNeighborsCount[i][j] >= 4){
                    cells[i][j].assignStatus(false);
                }

                // A cell dies by exposure if it has 0 or 1 living neighbors.
                if (livingNeighborsCount[i][j] < 2){
                    cells[i][j].assignStatus(false);
                }

                // A cell is born if it has 3 living neighbors.
                if (livingNeighborsCount[i][j] == 3){
                    cells[i][j].assignStatus(true);
                }
            }
        }
    }
}