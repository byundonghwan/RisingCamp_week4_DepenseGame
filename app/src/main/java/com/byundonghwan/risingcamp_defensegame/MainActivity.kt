package com.byundonghwan.risingcamp_defensegame

import android.app.Activity
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Window
import android.view.WindowManager


// 메인 액티비티
class MainActivity : Activity() {
    // GLSurfaceView
    private var mGLSurfaceView: GLSurfaceView? = null

    // 액티비티 생성
    override fun onCreate(savedInstanceState: Bundle?) {
        // 타이틀바를 제거함
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        // 화면 최대화
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        // 서피스뷰 생성을 위한 매트릭스
        val displaymetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displaymetrics)
        val height = displaymetrics.heightPixels
        val width = displaymetrics.widthPixels
        mGLSurfaceView = MainGLSurfaceView(this, width, height)
        setContentView(mGLSurfaceView)
    }

    override fun onPause() {
        super.onPause()
        mGLSurfaceView!!.onPause()
    }

    override fun onResume() {
        super.onResume()
        mGLSurfaceView!!.onResume()
    }
}