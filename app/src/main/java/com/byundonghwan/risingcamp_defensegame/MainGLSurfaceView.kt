package com.byundonghwan.risingcamp_defensegame

import android.opengl.GLSurfaceView
import android.view.MotionEvent


// 서피스뷰 클래스
class MainGLSurfaceView(activity: MainActivity, width: Int, height: Int) :
    GLSurfaceView(activity.applicationContext) {
    // 랜더러
    private val mGLRenderer: MainGLRenderer

    //생성자
    init {
        // OpenGL ES 2.0 context를 생성한다.
        setEGLContextClientVersion(2)
        // GLSerfaceView를 사용하기 위해 Context를 이용해 랜더러를 생성한다.
        mGLRenderer = MainGLRenderer(activity, width, height)
        setRenderer(mGLRenderer)
        // 랜더모드를 변경될 경우 그린다.
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    override fun onPause() {
        super.onPause()
        mGLRenderer.onPause()
    }

    override fun onResume() {
        super.onResume()
        mGLRenderer.onResume()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mGLRenderer.onTouchEvent(event)
        return true
    }
}
