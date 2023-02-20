package com.byundonghwan.risingcamp_defensegame

class NumberPanel(programImage: Int, programSolidColor: Int) {
    private var mIsActive = true
    private var mNumber = 0
    private var mNumberHeight = 0
    private var mNumberWidth = 0
    private lateinit var mHandleNumber: IntArray
    private val mPanel = arrayOfNulls<Unit>(5) // 99999까지 가능

    // 숫자패널 생성자 (숫자를 나타낼 패널을 전달받음)
    init {
        for (i in 0..4) mPanel[i] = Unit(programImage, programSolidColor)
    }

    // 패널의 숫자크기 설정
    fun setNumberSize(width: Int, height: Int) {
        mNumberWidth = width
        mNumberHeight = height
    }

    // 핸들설정
    fun setBitmap(handleNumber: IntArray) {
        mHandleNumber = handleNumber
    }

    // 숫자반환
    fun getNumber(): Int {
        return mNumber
    }

    // 숫자설정 - 자리수별로 계산하여 표현할 숫자 이미지를 설정한다.
    fun setNumber(number: Int) {
        mNumber = number
        var i = 0
        i = (mNumber % 100000 / 10000)
        mPanel[0]!!.setBitmap(mHandleNumber[i], mNumberWidth, mNumberHeight)
        i = (mNumber % 10000 / 1000)
        mPanel[1]!!.setBitmap(mHandleNumber[i], mNumberWidth, mNumberHeight)
        i = (mNumber % 1000 / 100)
        mPanel[2]!!.setBitmap(mHandleNumber[i], mNumberWidth, mNumberHeight)
        i = (mNumber % 100 / 10)
        mPanel[3]!!.setBitmap(mHandleNumber[i], mNumberWidth, mNumberHeight)
        i = (mNumber % 10)
        mPanel[4]!!.setBitmap(mHandleNumber[i], mNumberWidth, mNumberHeight)
    }

    // 숫자감소
    fun addNumber(number: Int) {
        mNumber += number
        setNumber(mNumber)
    }

    // 숫자 위치
    fun setPos(x: Int, y: Int) {
        for (i in 0..4) mPanel[i]!!.setPos((x + i * 50).toFloat(), y.toFloat())
    }

    // 활성화
    fun setIsActive(isActive: Boolean) {
        mIsActive = isActive
        for (i in 0..4) mPanel[i]!!.setIsActive(true)
    }

    // 그리기
    fun draw(m: FloatArray?) {
        for (i in 0..4) mPanel[i]!!.draw(m)
    }
}
