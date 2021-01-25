package com.example.project2

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    // Initialization of RecyclerView and associated functionality
    private lateinit var gridRecyclerView: RecyclerView
    private var adapter: RecyclerViewAdapter? = null
    private var grid = Grid(20, 20)
    private var handler: Handler = Handler(Looper.getMainLooper()) // https://stackoverflow.com/questions/61023968/how-to-solve-handler-deprecated
    private lateinit var runnable: Runnable
    private lateinit var va: ValueAnimator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Use Intent-passed grid if this activity represents a cloned grid
        if (intent.extras != null) {

            val flattened = intent.getParcelableArrayListExtra<Cell>("grid") // Flattened array
            val result: MutableList<MutableList<Cell>> = mutableListOf() // Re-built 2D MutableList<MutableList<Cell>>
            val sublist: MutableList<Cell> = mutableListOf() // Single row of the board

            // Translate flattened array back into 2D
            for (index in 0 until flattened?.size!!) {
                sublist.add(flattened[index]) // Built up row to be appended as a MutableList<Cell> into result
                if (sublist.size == 20) {
                    result.add(sublist.toMutableList())
                    sublist.clear() // Wipe row container after appending previous row
                }
            }

            // Inflate this grid instead of default grid
            grid.cells = result
        }

        // Find RecyclerView
        gridRecyclerView = findViewById(R.id.recycler_view)
        gridRecyclerView.layoutManager = GridLayoutManager(applicationContext, 20)
        updateUI()

        // Start Game
        // Solution for timer found here: https://android--code.blogspot.com/2018/02/android-kotlin-handler-and-runnable.html
        val startButton = findViewById<Button>(R.id.start_button)
        startButton.setOnClickListener {
            runnable = Runnable {
                grid.nextGeneration()
                adapter?.notifyDataSetChanged()

                // Schedule the task to repeat after 1 second
                handler.postDelayed(
                    runnable,
                    1000 // Delay in milliseconds
                )
            }

            // Schedule the task to repeat after 1 second
            handler.postDelayed(
                runnable,
                1000 // Delay in milliseconds
            )
        }

        // Stop game
        val stopButton = findViewById<Button>(R.id.stop_button)
        stopButton.setOnClickListener {
            handler.removeCallbacks(runnable) //deschedule repeating process
        }

        // Load an existing grid from disk
        val loadButton = findViewById<Button>(R.id.load_button)
        loadButton.setOnClickListener {
            val intent = Intent(this, LoadGridActivity::class.java) // Start cloned activity
            startActivityForResult(intent, 1)
        }

        // Save current grid to disk
        val exportButton = findViewById<Button>(R.id.export_button)
        exportButton.setOnClickListener {
            val flattened = ArrayList<Cell>(grid.cells.flatten())
            val intent = Intent(this, ExportGridActivity::class.java).apply { putExtra("grid", flattened) } // Pass current grid to new activity for storage
            startActivity(intent)
        }

        // Clone current grid to a new activity
        val cloneButton = findViewById<Button>(R.id.clone_button)
        cloneButton.setOnClickListener {
            val flattened = ArrayList<Cell>(grid.cells.flatten())
            val intent = Intent(this, MainActivity::class.java).apply { putExtra("grid", flattened) }
            startActivity(intent)
        }

        // Change alive and dead cell colors
        val changeColorsButton = findViewById<Button>(R.id.change_colors_button)
        changeColorsButton.setOnClickListener {
            val intent = Intent(this, FragmentActivity::class.java)
            startActivityForResult(intent, 0)
        }

        // Animate the living cells on the grid
        // Used this as guide: https://www.techrepublic.com/blog/software-engineer/more-fun-with-androids-property-animation-class/
        val animateButton = findViewById<Button>(R.id.animate_button)
        animateButton.setOnClickListener {
            val start: Int = Color.rgb(0x99, 0x99, 0x00) // Start color
            val end: Int = Color.rgb(0xff, 0xff, 0x00) // End color

            // Target view
            var holder: SquareHolder
            var arrayIndex: Int
            var subArrayIndex: Int

            // Modify each view
            for (i in 0 until 400) {
                holder = gridRecyclerView.findViewHolderForAdapterPosition(i) as SquareHolder
                arrayIndex = i / 20
                subArrayIndex = i % 20

                // Animate living cells
                if (grid.cells[arrayIndex][subArrayIndex].status) {
                    va = ObjectAnimator.ofInt(holder.mButton, "backgroundColor", start, end)
                    va.duration = 750 // Time elapsed between start and end transition
                    va.setEvaluator(ArgbEvaluator())
                    va.repeatCount = ValueAnimator.INFINITE // Repeat continuously
                    va.repeatMode = ValueAnimator.REVERSE // Reverse end -> start once start -> end completes
                    va.start() // Begin animation
                }
            }
        }
    }


    // Receive results from SingleActivityFragments
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Tangible data returned via Intent
        val result = data?.extras?.getString("FragmentResult")

        // Switch based on resultCode
        when (resultCode) {
            // Change Color result
            0 -> {
                if (data != null) {
                    changeColors(result)
                }
            }

            // Loading an existing grid to a new clone
            1 -> {
                if (data != null) {
                    // Receive flattened array in String form and inflate it back to ArrayList<Cell> for loading into new activity
                    val gson = Gson()
                    val arrayTutorialType = object : TypeToken<ArrayList<Cell>>() {}.type
                    val flattenedArray: ArrayList<Cell> = gson.fromJson(result, arrayTutorialType)

                    val intent = Intent(this, MainActivity::class.java).apply { putExtra("grid", flattenedArray) }
                    startActivity(intent)
                }
            }
        }
    }


    // Change colors of current alive and dead cells
    private fun changeColors(scheme: String?) {
        // Target each view
        var arrayIndex: Int
        var subArrayIndex: Int
        var holder: SquareHolder

        // Cycle through all views
        for (i in 0 until 400) {

            // Used this to find out how to iterate over each RecyclerView view: https://stackoverflow.com/questions/32811156/how-to-iterate-over-recyclerview-items
            holder = gridRecyclerView.findViewHolderForAdapterPosition(i) as SquareHolder // Reference to each view

            arrayIndex = i / 20
            subArrayIndex = i % 20

            // Switch to designated color scheme based on GUI selection
            when (scheme) {
                "colors1" -> {
                    if (grid.cells[arrayIndex][subArrayIndex].status) {
                        holder.mButton.setBackgroundResource(R.drawable.yellow_foreground)
                    } else {
                        holder.mButton.setBackgroundResource(R.drawable.gray_foreground)
                    }
                }

                "colors2" -> {
                    if (grid.cells[arrayIndex][subArrayIndex].status) {
                        holder.mButton.setBackgroundResource(R.drawable.white_foreground)
                    } else {
                        holder.mButton.setBackgroundResource(R.drawable.black_foreground)
                    }
                }

                "colors3" -> {
                    if (grid.cells[arrayIndex][subArrayIndex].status) {
                        holder.mButton.setBackgroundResource(R.drawable.green_foreground)
                    } else {
                        holder.mButton.setBackgroundResource(R.drawable.red_foreground)
                    }
                }
            }
        }
    }


    // Assign class variables
    private fun updateUI() {
        adapter = RecyclerViewAdapter()
        gridRecyclerView.adapter = adapter
    }


    // RecyclerViewHolder
    private inner class SquareHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mButton: Button = itemView.findViewById<View>(R.id.grid_square) as Button
        private var mPosition = 0

        fun bindPosition(p: Int) {
            mPosition = p
            val arrayIndex: Int = mPosition / 20
            val subArrayIndex = mPosition % 20

            // Assign alive color
            if (grid.cells[arrayIndex][subArrayIndex].status) {
                mButton.setBackgroundResource(R.drawable.yellow_foreground)

            } else { // Assign dead color
                mButton.setBackgroundResource(R.drawable.gray_foreground)
            }
        }

        // Flip status of cell when clicked
        init {
            mButton.setOnClickListener {
                val arrayIndex: Int = mPosition / 20
                val subArrayIndex = mPosition % 20
                grid.cells[arrayIndex][subArrayIndex].changeStatus()
                adapter?.notifyItemChanged(mPosition)
            }
        }
    }


    // RecyclerViewAdapter
    private inner class RecyclerViewAdapter : RecyclerView.Adapter<SquareHolder>() {
        override fun onBindViewHolder(holder: SquareHolder, position: Int) {
            holder.bindPosition(position)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SquareHolder {
            val inflater = LayoutInflater.from(applicationContext)
            return SquareHolder(inflater.inflate(R.layout.grid_square, parent, false))
        }

        override fun getItemCount(): Int {
            return 400
        }
    }
}