package com.byundonghwan.risingcamp_defensegame

import com.byundonghwan.risingcamp_defensegame.Map.getPosX
import com.byundonghwan.risingcamp_defensegame.Map.getPosY

// Unit으로부터 상속받아 병사를 구현하기 위한 객체
class Soldier(programImage: Int, programSolidColor: Int) :
    Unit(programImage, programSolidColor) {
    // 병사의 타입 0~8까지 사용한다.
    var mType = 0
    var mAttackPoint = 5 //

    // 1:아군, 2:적군을 나타낸다.
    var mIndex = 0

    // 현재 맵의 위치를 반환함
    // 병사의 게임맵의 블럭 위치를 나타낸다.
    var mPosBlockRow = 0
    var mPosBlockCol = 0
    var mDir = 0
    var Width : Int = 150
    var Height : Int = 150
    var mIsAttack = false

    // MainGLRenderer를 참조한다.
    //private MainGLRenderer mMainGLRenderer;
    // 생성자
    init { //, MainGLRenderer mainGLRenderer) {
        //mMainGLRenderer = mainGLRenderer;
        // mCount는 매번 루프를 돌때마다 호출된다. 병사의 움직임에 관여하는데 병사마다
        // 다른 시작점을 줌으로써 각기 다른 움직임을 갖도록 처리한다.
        mCount = (Math.random() * 100).toInt()
    }

    // 병사의 속성을 설정한다.타입, 종류, 인덱스번호
    fun setProperty(type: Int, index: Int) {
        mType = type
        mIndex = index
        mBitmapState = 0
        setType(type)
    }

    // 병사의 타입을 설정한다.
    fun setType(type: Int) {
        mType = type
        if (type == 0) {
            mAttackPoint = 5
        } else if (type == 1) {
            mAttackPoint = 20
        }
    }

    // 현재 맵의 위치를 반환함
    fun getBlockRow(): Int {
        return mPosBlockRow
    }

    fun getBlockCol(): Int {
        return mPosBlockCol
    }

    fun getCurrRow(): Int {
        return mPosBlockRow
    }

    fun getCurrCol(): Int {
        return mPosBlockCol
    }

    // 해당 블럭에 위치를 지정함
    fun setToBlock(row: Int, col: Int) {
        mPosBlockRow = row
        mPosBlockCol = col
        mTargetX = getPosX(row, col)
        mTargetY = getPosY(row, col) + Height / 2
        mPosX = mTargetX
        mPosY = mTargetY
    }

    fun think() {
        if (mIsActive === false) {
            return
        }
        if (mPosX > mTargetX + 10) {
            mPosX -= 9
        } else if (mPosX < mTargetX - 10) {
            mPosX += 9
        }
        if (mPosY > mTargetY + 10) {
            mPosY -= 9
        } else if (mPosY < mTargetY - 10) {
            mPosY += 9
        }
    }
}