package com.unstoppable.timeoflife.ui.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.unstoppable.timeoflife.DataArrayClass
import com.unstoppable.timeoflife.MainActivity
import com.unstoppable.timeoflife.R
import java.lang.Exception

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment(context: Context) : Fragment() {

    private val nContext = context
    private lateinit var pageViewModel: PageViewModel

    private var sharedPreferences: SharedPreferences? = null
    private var sharedPreferencesEdit: SharedPreferences.Editor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        val titlePickerLayout = root.findViewById<RelativeLayout>(R.id.TopicPickerLayoutID)
        val titleEnterEditText: EditText = root.findViewById(R.id.topicPickerEditTextID)
        val titleEnterButton: Button = root.findViewById(R.id.TopicPickerButtonID)

        val topicTextView: TextView = root.findViewById(R.id.TopicTextViewID)
        val imageView: ImageView = root.findViewById(R.id.ImageViewerID)

        sharedPreferences = nContext.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)
        sharedPreferencesEdit = sharedPreferences!!.edit()

        val dataArrayClassObj = DataArrayClass()
        val yearsArray = dataArrayClassObj.getYearsArray()
        val monthsArray = dataArrayClassObj.getMonthsArray()
        val yearsIndex = (nContext as MainActivity).getYearsIndex()
        val monthsIndex = nContext.getMonthsIndex()

        var isLayoutVisible = false

        val tabSection = arguments?.getInt(ARG_SECTION_NUMBER)

        titleEnterButton.setOnClickListener {
            val inputTitleText = titleEnterEditText.text.toString()
            if(tabSection!! <= 1) {
                if (inputTitleText == "") {
                    topicTextView.text = resources.getString(R.string.text)
                    saveString("TitleStringYears",resources.getString(R.string.text))
                } else {
                    topicTextView.text = inputTitleText
                    saveString("TitleStringYears",inputTitleText)
                }
            }else{
                if (inputTitleText == "") {
                    topicTextView.text = resources.getString(R.string.text)
                    saveString("TitleStringMonths",resources.getString(R.string.text))
                } else {
                    topicTextView.text = inputTitleText
                    saveString("TitleStringMonths",inputTitleText)
                }
            }
            titlePickerLayout.visibility = View.GONE
        }

        topicTextView.setOnLongClickListener {
            if (isLayoutVisible) {
                titlePickerLayout.visibility = View.GONE
                isLayoutVisible = false
            } else {
                titlePickerLayout.visibility = View.VISIBLE
                isLayoutVisible = true
            }
            false
        }

//        pageViewModel.text.observe(viewLifecycleOwner, Observer<String> {
//            textView.text = it
//        })

        try {
            if (tabSection!! <= 1) {
                if (getString("TitleStringYears") == "") {
                    topicTextView.text = resources.getString(R.string.text)
                }else{
                    topicTextView.text = getString("TitleStringYears")
                }
                imageView.setImageResource(yearsArray[yearsIndex])
            } else {
                if (getString("TitleStringMonths") == "") {
                    topicTextView.text = resources.getString(R.string.text)
                }else{
                    topicTextView.text = getString("TitleStringMonths")
                }
                imageView.setImageResource(monthsArray[monthsIndex])
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return root
    }

    private fun saveString(strKey : String,str: String) {
        sharedPreferencesEdit?.putString(strKey, str)
        sharedPreferencesEdit?.apply()
    }

    private fun getString(strKey : String): String {
        return sharedPreferences?.getString(strKey, "").toString()
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(context: Context, sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment(context).apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}
