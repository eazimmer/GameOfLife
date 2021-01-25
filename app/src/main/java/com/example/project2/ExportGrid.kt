package com.example.project2

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.Gson

class ExportGridFragment(private val flattened: ArrayList<Cell>, private var preferences: SharedPreferences) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate view for access to views
        val v = inflater.inflate(R.layout.fragment_export_grid, container, false)

        // Initiate references to views
        val submitButton = v.findViewById<Button>(R.id.submit_button)
        val clearButton = v.findViewById<Button>(R.id.clear_button)
        val homeButton = v.findViewById<Button>(R.id.home_button)

        // Display stored data on initial launch
        displayUsedSlots(v, preferences)

        // Save current grid to an available slot
        submitButton.setOnClickListener {
            // Save off the current grid
            displayUsedSlots(v, preferences)
            updateSaveSlots(preferences, flattened)
            activity?.finish()
        }

        // Clear all save slots
        clearButton.setOnClickListener {
            updateSaveSlots(preferences, flattened, true)
            displayUsedSlots(v, preferences)
        }

        // Abort and return to main activity
        homeButton.setOnClickListener {
            activity?.finish()
        }

        return v
    }


    // Display currently stored scores to screen
    private fun displayUsedSlots(v: View, preferences: SharedPreferences) {

        // Get references to save slots
        val save1 = v.findViewById<TextView>(R.id.save1)
        val save2 = v.findViewById<TextView>(R.id.save2)
        val save3 = v.findViewById<TextView>(R.id.save3)
        val save4 = v.findViewById<TextView>(R.id.save4)
        val save5 = v.findViewById<TextView>(R.id.save5)

        // Printing currently persistently stored data to screen
        val savedList = preferences.getString("saves", null) // Access persistent data
        if (savedList != null) {
            val items = savedList.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray() // Split string back to array

            // Modify each view based on associated array index
            if (items[0] == "EMPTY") {
                save1.text = items[0]
            } else {
                save1.text = "USED"
            }

            if (items[1] == "EMPTY") {
                save2.text = items[1]
            } else {
                save2.text = "USED"
            }

            if (items[2] == "EMPTY") {
                save3.text = items[2]
            } else {
                save3.text = "USED"
            }

            if (items[3] == "EMPTY") {
                save4.text = items[3]
            } else {
                save4.text = "USED"
            }

            if (items[4] == "EMPTY") {
                save5.text = items[4]
            } else {
                save5.text = "USED"
            }
        }
    }


    // Upload new grid into persistent storage
    private fun updateSaveSlots(preferences: SharedPreferences, flattened: ArrayList<Cell>, wipe: Boolean = false) {

        // Used as solution to store ArrayList<Cell> as JSON string for storage in sharedPreferences
        // https://stackoverflow.com/questions/60280804/android-store-arraylist-persistent
        val gson = Gson()

        // Used to convert ArrayList<Cell> into JSON string
        // https://bezkoder.com/kotlin-parse-json-gson/
        // https://medium.com/nplix/how-to-read-and-write-json-data-in-kotlin-with-gson-c2971fd2d124
        var flattenedString: String = gson.toJson(flattened)

        // If NOT clearing saves and normally updating:
        if (!wipe) {

            // Create placeholder data structure
            var numGrids = 0 // Count variable to reflect which save slot grid should be saved to
            var pairs = mutableListOf<String>() // Placeholder list to modify before data placement back into sharedPreferences
            val savedData = preferences.getString("saves", null) // Access persistently stored data (grids)
            if (savedData != null) {
                val items = savedData.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray() // Split string into array

                // Add all saved items into modifiable structure
                for (item in items) {
                    if (item == "EMPTY") {
                        numGrids +=  1 // Count towards which save slot is being used
                    }
                    pairs.add(item)
                }

                // Add new item normally without removal of an items if below size limit
                if (pairs.size < 5) {
                    // Add current grid
                    numGrids -= 1
                    flattenedString = numGrids.toString() + flattenedString + numGrids.toString() // Surround string representing grid with index for splicing later
                    pairs.add(flattenedString)
                } else { // Remove an item from the save list (an EMPTY) if length > 5
                    // Add current grid
                    numGrids -= 1
                    flattenedString = numGrids.toString() + flattenedString + numGrids.toString()

                    val newPairs = mutableListOf<String>()
                    newPairs.addAll(pairs.slice(1 until (pairs.size)))
                    newPairs.add(flattenedString)
                    pairs = newPairs
                }

                // Save all data which should persist
                val savedList = StringBuilder()
                for (save in pairs) {
                    savedList.append(save)
                    savedList.append(",")
                }
                preferences.edit().putString("saves", savedList.toString()).apply()
            }
        }

        // Intending to clear all saved grids:
        else {
            val empty = listOf("EMPTY", "EMPTY", "EMPTY", "EMPTY", "EMPTY")

            val savedList = StringBuilder()
            for (save in empty) {
                savedList.append(save)
                savedList.append(",")
            }
            preferences.edit().putString("saves", savedList.toString()).apply()
        }
    }
}


class ExportGridActivity : SingleFragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
        val fm = supportFragmentManager
        var fragment = fm.findFragmentById(R.id.fragment_container)
        if (fragment == null) {
            fragment = createFragment()
            fm.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }


    override fun createFragment() : Fragment {
        val flattened: ArrayList<Cell> = intent.getParcelableArrayListExtra<Cell>("grid") as ArrayList<Cell> // Flattened array
        val preferences = getSharedPreferences("saves", MODE_PRIVATE)
        return ExportGridFragment(flattened, preferences)
    }
}