package com.medialink.deco20workmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.work.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var periodicWorkRequest: PeriodicWorkRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_one_time_task.setOnClickListener(this)
        btn_periodic_task.setOnClickListener(this)
        btn_cancel_task.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_one_time_task -> startOneTimeTask()
            R.id.btn_periodic_task -> startPeriodicTask()
            R.id.btn_cancel_task -> cancelPeriodicTask()
        }
    }

    private fun startPeriodicTask() {
        tv_status.text = getString(R.string.status)
        val data = Data.Builder()
            .putString(MyWorker.EXTRA_CITY, ed_city.text.toString())
            .build()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        periodicWorkRequest = PeriodicWorkRequest.Builder(MyWorker::class.java, 15, TimeUnit.MINUTES)
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance().enqueue(periodicWorkRequest)
        WorkManager.getInstance().getWorkInfoByIdLiveData(periodicWorkRequest.id).observe(this@MainActivity, object : Observer<WorkInfo> {
            override fun onChanged(t: WorkInfo) {
                val status = t.state.name
                tv_status.append("\n" + status)
                btn_cancel_task.isEnabled = false
                if (t.state == WorkInfo.State.ENQUEUED) {
                    btn_cancel_task.isEnabled = true
                }
            }
        })

    }

    private fun cancelPeriodicTask() {
        WorkManager.getInstance().cancelWorkById(periodicWorkRequest.id)
    }

    private fun startOneTimeTask() {
        tv_status.text = getString(R.string.status)
        val data = Data.Builder()
            .putString(MyWorker.EXTRA_CITY, ed_city.text.toString())
            .build()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val oneTimeWorkRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance().enqueue(oneTimeWorkRequest)
        WorkManager.getInstance().getWorkInfoByIdLiveData(oneTimeWorkRequest.id)
            .observe(this@MainActivity, object : Observer<WorkInfo> {
                override fun onChanged(t: WorkInfo) {
                    val status = t.state.name
                    tv_status.append("\n" + status)
                }
            })
    }
}
