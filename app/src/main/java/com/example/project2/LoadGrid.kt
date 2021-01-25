package com.example.project2

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class LoadGridFragment(private var preferences: SharedPreferences) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate view for reference to objects
        val v = inflater.inflate(R.layout.fragment_load_grid, container, false)

        // Initialize base view with existing data
        displayUsedSlots(v, preferences)

        // Get references to relevant views
        val save1 = v.findViewById<TextView>(R.id.save1)
        val save2 = v.findViewById<TextView>(R.id.save2)
        val save3 = v.findViewById<TextView>(R.id.save3)
        val save4 = v.findViewById<TextView>(R.id.save4)
        val save5 = v.findViewById<TextView>(R.id.save5)
        val homeButton = v.findViewById<Button>(R.id.home_button)

        // Prepare to receive stored data
        val savedList = preferences.getString("saves", null)
        if (savedList != null) {

            // Used to translate string representation back to ArrayList<Cell> (was having issues with fromJSON() function)
            // https://beginnersbook.com/2015/05/java-string-to-arraylist-conversion/#:~:text=1)%20First%20split%20the%20string,asList()%20method.
            // Based on which save is selected, grab the appropriate grid
            save1.setOnClickListener {
                val grid: String = savedList.slice((savedList.indexOf("0[")+1)..savedList.indexOf("]0")) // Slice out from the right string representing the right grid
                val intent = Intent()
                intent.putExtra("FragmentResult", grid)
                activity?.setResult(1, intent)
                activity?.finish()
            }

            save2.setOnClickListener {
                val grid: String = savedList.slice((savedList.indexOf("1[")+1)..savedList.indexOf("]1"))
                val intent = Intent()
                intent.putExtra("FragmentResult", grid)
                activity?.setResult(1, intent)
                activity?.finish()
            }

            save3.setOnClickListener {
                val grid: String = savedList.slice((savedList.indexOf("2[")+1)..savedList.indexOf("]2"))
                val intent = Intent()
                intent.putExtra("FragmentResult", grid)
                activity?.setResult(1, intent)
                activity?.finish()
            }

            save4.setOnClickListener {
                val grid: String = savedList.slice((savedList.indexOf("3[")+1)..savedList.indexOf("]3"))
                val intent = Intent()
                intent.putExtra("FragmentResult", grid)
                activity?.setResult(1, intent)
                activity?.finish()
            }

            save5.setOnClickListener {
                val grid: String = savedList.slice((savedList.indexOf("4[")+1)..savedList.indexOf("]4"))
                val intent = Intent()
                intent.putExtra("FragmentResult", grid)
                activity?.setResult(1, intent)
                activity?.finish()
            }
        }

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
        val savedList = preferences.getString("saves", null)
        if (savedList != null) {
            val items = savedList.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            // Update screen based on whether or not save slot is available
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
}


class LoadGridActivity : SingleFragmentActivity() {
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
        return LoadGridFragment(getSharedPreferences("saves", MODE_PRIVATE))
    }
}