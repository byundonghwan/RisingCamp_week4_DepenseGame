package com.byundonghwan.risingcamp_defensegame

// 화면 설정 클래스
class ScreenConfig     // 기본설정
    (var mDeviceWidth: Int, var mDeviceHeight: Int) {
    var mVirtualWidth = 0
    var mVirtualHeight = 0

    // 가상 폭 설정
    fun setSize(width: Int, height: Int) {
        mVirtualWidth = width
        mVirtualHeight = height
    }

    // X 좌표 설정
    fun getX(x: Int): Int {
        return (x * mVirtualWidth / mDeviceWidth)
    }

    // Y좌표 설정
    fun getY(y: Int): Int {
        return mVirtualHeight - (y * mVirtualHeight / mDeviceHeight)
    }
}