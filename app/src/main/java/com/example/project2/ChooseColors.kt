package com.example.project2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

abstract class SingleFragmentActivity : AppCompatActivity() {
    protected abstract fun createFragment(): Fragment
}


class ChooseColorsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate view for referencing widgets
        val v = inflater.inflate(R.layout.fragment_choose_colors, container, false)


        // Code to return data out of a fragment like an activity
        // https://stackoverflow.com/questions/63385959/how-to-return-data-to-the-calling-activity-in-kotlin
        // https://stackoverflow.com/questions/36495842/is-there-a-method-like-setresult-in-fragment

        // Return data out of fragment based on color scheme selection
        val colors1 = v.findViewById<Button>(R.id.colors1)
        colors1.setOnClickListener {
            val intent = Intent()
            intent.putExtra("FragmentResult", "colors1")
            activity?.setResult(0, intent)
            activity?.finish()
        }

        val colors2 = v.findViewById<Button>(R.id.colors2)
        colors2.setOnClickListener {
            val intent = Intent()
            intent.putExtra("FragmentResult", "colors2")
            activity?.setResult(0, intent)
            activity?.finish()
        }

        val colors3 = v.findViewById<Button>(R.id.colors3)
        colors3.setOnClickListener {
            val intent = Intent()
            intent.putExtra("FragmentResult", "colors3")
            activity?.setResult(0, intent)
            activity?.finish()
        }

        return v
    }
}


class FragmentActivity : SingleFragmentActivity() {
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
        return ChooseColorsFragment()
    }
}