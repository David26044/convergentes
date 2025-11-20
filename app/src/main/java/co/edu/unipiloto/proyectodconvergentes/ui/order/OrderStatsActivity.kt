// co.edu.unipiloto.proyectodconvergentes.ui.stats.OrdersStatsActivity.kt
package co.edu.unipiloto.proyectodconvergentes.ui.stats

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import co.edu.unipiloto.proyectodconvergentes.R
import co.edu.unipiloto.proyectodconvergentes.ui.net.RetrofitModule
import co.edu.unipiloto.proyectodconvergentes.ui.order.CountOrdersByState
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch

class OrdersStatsActivity : AppCompatActivity() {

    private val backendService by lazy { RetrofitModule.backendService(this) }

    private lateinit var progressBar: ProgressBar
    private lateinit var barChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_stats)

        progressBar = findViewById(R.id.progressStats)
        barChart = findViewById(R.id.barChartStates)

        loadDataAndRender()
    }

    private fun loadDataAndRender() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val resp = backendService.getCountOrdersByState()
                progressBar.visibility = View.GONE

                if (!resp.isSuccessful) {
                    Toast.makeText(this@OrdersStatsActivity, "Error ${resp.code()}", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val data = resp.body().orEmpty()
                if (data.isEmpty()) {
                    Toast.makeText(this@OrdersStatsActivity, "Sin datos para graficar", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                renderBarChart(data)
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@OrdersStatsActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun renderBarChart(items: List<CountOrdersByState>) {
        val labels = items.map { it.stateName }
        val entries = items.mapIndexed { index, item ->
            BarEntry(index.toFloat(), item.count.toFloat())
        }

        val dataSet = BarDataSet(entries, "Órdenes por estado")
        val barData = BarData(dataSet)
        barData.barWidth = 0.6f

        barChart.data = barData
        barChart.description.isEnabled = false
        barChart.axisRight.isEnabled = false

        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setDrawGridLines(false)
            valueFormatter = IndexAxisValueFormatter(labels)
            labelRotationAngle = 0f
        }

        barChart.axisLeft.apply {
            axisMinimum = 0f
            granularity = 1f
        }

        barChart.setFitBars(true)
        barChart.invalidate()
        barChart.animateY(800)
    }
}
