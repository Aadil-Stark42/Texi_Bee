package com.mindnotix.texibee.activitys.settings.staticstic

import android.os.Bundle
import androidx.databinding.DataBindingUtil
 import com.mindnotix.texibee.databinding.ActivityStaticsticBinding
import com.mindnotix.texibee.support.ToolbarSupport
import com.mindnotix.texibee.utils.DayAxisValueFormatter
import com.mindnotix.texibee.utils.MyAxisValueFormatter
import com.mindnotix.texibee.utils.XYMarkerView
import com.mindnotix.retailers.components.Legend.LegendForm

import com.mindnotix.retailers.components.Legend

import com.mindnotix.retailers.components.YAxis

import com.mindnotix.retailers.components.YAxis.YAxisLabelPosition

import com.mindnotix.retailers.formatter.IAxisValueFormatter

import com.mindnotix.retailers.components.XAxis.XAxisPosition

import com.mindnotix.retailers.components.XAxis
import com.mindnotix.retailers.charts.BarChart
import com.mindnotix.retailers.data.Entry
import com.mindnotix.retailers.highlight.Highlight
import com.mindnotix.retailers.listener.OnChartValueSelectedListener
import com.mindnotix.retailers.data.BarData

import com.mindnotix.retailers.interfaces.datasets.IBarDataSet

import com.mindnotix.retailers.utils.Fill

import com.mindnotix.retailers.data.BarDataSet

import com.mindnotix.retailers.data.BarEntry

 import androidx.core.content.ContextCompat
import com.mindnotix.texibee.R
import com.mindnotix.texibee.activitys.BaseActivity


class StaticsticActivity : BaseActivity(), OnChartValueSelectedListener {
    lateinit var binding: ActivityStaticsticBinding
    private lateinit var chart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_staticstic)
        ToolbarSupport(getString(R.string.STATISTICS), binding.toolbar, this)
        ChartSetup()
        setData(6, 50F)
    }

    private fun ChartSetup() {
        chart = binding.chart1
        chart.setOnChartValueSelectedListener(this)

        chart.setDrawBarShadow(false)
        chart.setDrawValueAboveBar(true)

        chart.description.isEnabled = false

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60)

        // scaling can now only be done on x- and y-axis separately

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false)

        chart.setDrawGridBackground(false)
        // chart.setDrawYLabels(false);

        // chart.setDrawYLabels(false);
        val xAxisFormatter: IAxisValueFormatter = DayAxisValueFormatter(chart)

        val xAxis: XAxis = chart.xAxis
        xAxis.position = XAxisPosition.BOTTOM
        /*xAxis.typeface = tfLight*/
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f // only intervals of 1 day

        xAxis.labelCount = 7
        xAxis.valueFormatter = xAxisFormatter

        val custom: IAxisValueFormatter = MyAxisValueFormatter()

        val leftAxis: YAxis = chart.axisLeft
        /*leftAxis.typeface = tfLight*/
        leftAxis.setLabelCount(8, false)
        leftAxis.valueFormatter = custom
        leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.spaceTop = 15f
        leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)


        val rightAxis: YAxis = chart.axisRight
        rightAxis.setDrawGridLines(false)
        /*rightAxis.typeface = tfLight*/
        rightAxis.setLabelCount(8, false)
        rightAxis.valueFormatter = custom
        rightAxis.spaceTop = 15f
        rightAxis.axisMinimum = 0f // this replaces setStartAtZero(true)


        val l: Legend = chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.form = LegendForm.SQUARE
        l.formSize = 9f
        l.textSize = 11f
        l.xEntrySpace = 4f

        val mv = XYMarkerView(this, xAxisFormatter)
        mv.setChartView(chart) // For bounds control

        chart.marker = mv // Set the marker to the chart


        // setting data


    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {

    }

    override fun onNothingSelected() {

    }

    private fun setData(count: Int, range: Float) {
        val start = 1f
        val values: ArrayList<BarEntry> = ArrayList()
        var i = start.toInt()
        while (i < start + count) {
            val `val` = (Math.random() * (range + 1)).toFloat()
            if (Math.random() * 100 < 25) {
                values.add(BarEntry(i.toFloat(), `val`, resources.getDrawable(R.drawable.star)))
            } else {
                values.add(BarEntry(i.toFloat(), `val`))
            }
            i++
        }
        val set1: BarDataSet
        if (chart.data != null &&
            chart.data.dataSetCount > 0
        ) {
            set1 = chart.data.getDataSetByIndex(0) as BarDataSet
            set1.values = values
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(values, "The year 2017")
            set1.setDrawIcons(false)
            val startColor1: Int = ContextCompat.getColor(this, android.R.color.holo_orange_light)
            val startColor2: Int = ContextCompat.getColor(this, android.R.color.holo_blue_light)
            val startColor3: Int = ContextCompat.getColor(this, android.R.color.holo_orange_light)
            val startColor4: Int = ContextCompat.getColor(this, android.R.color.holo_green_light)
            val startColor5: Int = ContextCompat.getColor(this, android.R.color.holo_red_light)
            val endColor1: Int = ContextCompat.getColor(this, android.R.color.holo_blue_dark)
            val endColor2: Int = ContextCompat.getColor(this, android.R.color.holo_purple)
            val endColor3: Int = ContextCompat.getColor(this, android.R.color.holo_green_dark)
            val endColor4: Int = ContextCompat.getColor(this, android.R.color.holo_red_dark)
            val endColor5: Int = ContextCompat.getColor(this, android.R.color.holo_orange_dark)
            val gradientFills: MutableList<Fill> = ArrayList()
            gradientFills.add(Fill(startColor1, endColor1))
            gradientFills.add(Fill(startColor2, endColor2))
            gradientFills.add(Fill(startColor3, endColor3))
            gradientFills.add(Fill(startColor4, endColor4))
            gradientFills.add(Fill(startColor5, endColor5))
            set1.fills = gradientFills
            val dataSets: ArrayList<IBarDataSet> = ArrayList()
            dataSets.add(set1)
            val data = BarData(dataSets)
            data.setValueTextSize(10f)
/*
            data.setValueTypeface(tfLight)
*/
            data.barWidth = 0.9f
            chart.data = data
        }
    }


}