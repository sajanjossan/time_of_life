package com.unstoppable.timeoflife

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.unstoppable.timeoflife.ui.main.SectionsPagerAdapter
import java.util.*

class MainActivity : AppCompatActivity() {

    private var dobDDPickerEditText: EditText? = null
    private var dobMMPickerEditText: EditText? = null
    private var dobYYPickerEditText: EditText? = null
    private var dobEnter: Button? = null
    private var floatingActionButton: FloatingActionButton? = null
    private var floatingRefreshButton: FloatingActionButton? = null
    private var datePickerLayout: LinearLayout? = null
    private var calendar: Calendar? = null
    private var sharedPreferences: SharedPreferences? = null
    private var sharedPreferencesEdit: SharedPreferences.Editor? = null

    private var dobMonths: Int = 0
    private var dobYears: Int = 0
    private var datePickerLayoutVisibility = false

    private var tabLayout: TabLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadViewPagerAdapter()

        initializeImageViewFromDate()
    }

    private fun loadViewPagerAdapter() {
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        tabLayout = findViewById(R.id.tabs)
        tabLayout?.setupWithViewPager(viewPager)
    }

    private fun initializeImageViewFromDate() {
        dobDDPickerEditText = findViewById(R.id.dobDD_ID)
        dobMMPickerEditText = findViewById(R.id.dobMM_ID)
        dobYYPickerEditText = findViewById(R.id.dobYY_ID)
        dobEnter = findViewById(R.id.enterBtnID)
        floatingActionButton = findViewById(R.id.fabID)
        floatingRefreshButton = findViewById(R.id.refreshID)
        datePickerLayout = findViewById(R.id.DatePickerLinearLayoutID)

        //loading saved preferences
        sharedPreferences = getSharedPreferences("SharedPreferencesKey", Context.MODE_PRIVATE)
        sharedPreferencesEdit = sharedPreferences!!.edit()
        val dD = sharedPreferences!!.getInt("ddDays", 0)
        val mM = sharedPreferences!!.getInt("mmMonths", 0)
        val yY = sharedPreferences!!.getInt("yyYears", 0)
        dobDDPickerEditText?.setText(dD.toString())
        dobMMPickerEditText?.setText(mM.toString())
        dobYYPickerEditText?.setText(yY.toString())
        loadTimeData(yY,mM,dD)
        loadViewPagerAdapter()

        val tabSection = loadTabSection()!!.toInt()
        tabLayout?.selectTab(tabLayout?.getTabAt(tabSection))

        floatingActionButton?.setOnClickListener {
            if (datePickerLayoutVisibility) {
                datePickerLayout?.visibility = View.GONE
                datePickerLayoutVisibility = false
            } else {
                datePickerLayout?.visibility = View.VISIBLE
                datePickerLayoutVisibility = true
            }
        }
        floatingRefreshButton?.setOnClickListener {
            loadTimeData(yY,mM,dD)
            loadViewPagerAdapter()
            Toast.makeText(applicationContext,"Refreshed",Toast.LENGTH_LONG).show()
        }

        dobDDPickerEditText?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    if (s!!.length == 2) {
                        if (s[0].toInt() == 51 && s[1].toInt() > 49) {
                            dobDDPickerEditText!!.setText(resources.getString(R.string._0).plus(s[1].toString()))
                            Toast.makeText(applicationContext, "date can't be greater then 31", Toast.LENGTH_LONG).show()
                        }
                        changeFocus(dobMMPickerEditText!!)
                    } else if (s.length > 2) {
                        dobDDPickerEditText!!.setText(s[0].plus(s[1].toString()))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })
        dobMMPickerEditText?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    if (s!!.length == 2) {
                        if (s[0].toInt() == 49 && s[1].toInt() > 50) {
                            dobMMPickerEditText!!.setText(resources.getString(R.string._0).plus(s[1].toString()))
                            Toast.makeText(applicationContext, "months can't be greater then 12", Toast.LENGTH_LONG).show()
                        }
                        changeFocus(dobYYPickerEditText!!)
                    } else if (s.length > 2) {
                        dobMMPickerEditText!!.setText(s[0].plus(s[1].toString()))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        dobEnter?.setOnClickListener {
            val dD: Int = dobDDPickerEditText?.text.toString().toInt()
            val mM: Int = dobMMPickerEditText?.text.toString().toInt()
            val yY: Int = dobYYPickerEditText?.text.toString().toInt()
            //saving
            sharedPreferencesEdit?.putInt("ddDays", dD)
            sharedPreferencesEdit?.apply()
            sharedPreferencesEdit?.putInt("mmMonths", mM)
            sharedPreferencesEdit?.apply()
            sharedPreferencesEdit?.putInt("yyYears", yY)
            sharedPreferencesEdit?.apply()

            loadTimeData(yY,mM,dD)
            loadViewPagerAdapter()
        }
    }

    private fun loadTimeData(yY : Int,mM : Int,dD : Int){
        calendar = Calendar.getInstance()
        //getting dobMonths count
        val temp = calendar!!.get(Calendar.YEAR) - (yY + 1)
        dobMonths = (temp * 12)
        //year from months
        dobMonths += (12 - mM)
        //year now coming months
        dobMonths += if (calendar!!.get(Calendar.DAY_OF_MONTH) > dD)
            calendar!!.get(Calendar.MONTH) + 1
        else
            calendar!!.get(Calendar.MONTH)
        //getting dobYears count
        dobYears = dobMonths / 12
        //refresh fragment to load current tab automatically
    }

    fun getYearsIndex(): Int {
        return dobYears - 1
    }

    fun getMonthsIndex(): Int {
        return dobMonths - 1
    }

    private fun saveTabSection(tSec: Int) {
        sharedPreferencesEdit?.putInt("TabSection", tSec)
        sharedPreferencesEdit?.commit()
    }

    private fun loadTabSection(): Int? {
        return sharedPreferences?.getInt("TabSection", 0)
    }

    private fun changeFocus(editText: EditText) {
        editText.requestFocus()
        val inputMethod = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethod.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onDestroy() {
        super.onDestroy()
        saveTabSection(tabLayout?.selectedTabPosition!!.toInt())
    }
}
