package com.byundonghwan.risingcamp_defensegame

// 버튼 클래스
class Button  // 버튼 생성자
    (programImage: Int, programSolidColor: Int) :
    Unit(programImage, programSolidColor) {
    fun setDisable(b: Boolean) {
        mBitmapState = if (b == false) 0 else 1
    }
}