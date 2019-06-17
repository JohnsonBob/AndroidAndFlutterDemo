package com.johnson.androidandflutter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.flutter.facade.Flutter
import io.flutter.view.FlutterView
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.FrameLayout
import android.widget.Toast
import io.flutter.plugin.common.EventChannel
import android.content.Intent
import io.flutter.plugin.common.MethodChannel


val FlutterToAndroidCHANNEL = "com.johnson.toandroid/plugin"
val AndroidToFlutterCHANNEL = "com.johnson.toflutter/plugin"

class MainActivity : AppCompatActivity() {

    private lateinit var flutterView: FlutterView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        flutterView = Flutter.createView(this, lifecycle, "route3")
        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        rl_flutter.addView(flutterView, layoutParams)

        intent.getStringExtra("test")?.run {
            Toast.makeText(this@MainActivity, this, Toast.LENGTH_SHORT).show()
            tv_params.setText("flutter 传参:$this")
        }

        EventChannel(flutterView, AndroidToFlutterCHANNEL).setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(o: Any, eventSink: EventChannel.EventSink) {
                val androidParmas = "来自android原生的参数"
                eventSink.success(androidParmas)
            }

            override fun onCancel(o: Any) {

            }
        })

        MethodChannel(flutterView, FlutterToAndroidCHANNEL).setMethodCallHandler { methodCall, result ->
            //接收来自flutter的指令oneAct
            if (methodCall.method == "withoutParams") {

                //跳转到指定Activity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                //返回给flutter的参数
                result.success("success")
            } else if (methodCall.method == "withParams") {

                //解析参数
                val text = methodCall.argument<String>("flutter")

                //带参数跳转到指定Activity
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("test", text)
                startActivity(intent)

                //返回给flutter的参数
                result.success("success")
            } else {
                result.notImplemented()
            }//接收来自flutter的指令twoAct
        }
    }
}
