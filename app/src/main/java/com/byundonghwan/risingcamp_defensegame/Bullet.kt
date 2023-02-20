package com.byundonghwan.risingcamp_defensegame

import com.byundonghwan.risingcamp_defensegame.Map.getPosX
import com.byundonghwan.risingcamp_defensegame.Map.getPosY

// Unit으로부터 상속받아 총알를 구현하기 위한 객체
class Bullet(programImage: Int, programSolidColor: Int) :
    Unit(programImage, programSolidColor) {
    // 총알의 타입 0~1까지 사용한다.
    var mType = 0
    var mIndex = 0
    var mEnergy = 10
    var mAttackSoldier: Soldier? = null
    var mTargetZombie: Zombie? = null
    var mMainGLRenderer: MainGLRenderer? = null

    // 총알의 속성을 설정한다.타입, 종류, 인덱스번호
    fun setType(type: Int) {
        mType = type
        if (type == 0) {
            mEnergy = 5
        } else if (type == 1) {
            mEnergy = 10
        }
    }

    var mGapX = 0f
    var mGapY = 0f

    // 해당 블럭에 위치를 지정함
    fun setToBlock(row: Int, col: Int) {
        mTargetX = getPosX(row, col)
        mTargetY = getPosY(row, col) + this.mHeight / 3
        mPosX = mTargetX
        mPosY = mTargetY
    }

    // 해당 블럭이로 이동함.
    fun moveToBlock(
        soldier: Soldier?,
        zombie: Zombie?,
        startRow: Int, startCol: Int, endRow: Int, endCol: Int,
        mainGlRenderer: MainGLRenderer?
    ) {
        mAttackSoldier = soldier
        mTargetZombie = zombie
        mMainGLRenderer = mainGlRenderer
        // 이동하려는 위치중 현재 블럭을 제외함.
        mPosX = getPosX(startRow, startCol)
        mPosY = getPosY(startRow, startCol)
        mTargetX = getPosX(endRow, endCol)
        mTargetY = getPosY(endRow, endCol) + 50
        mGapX = (mTargetX - mPosX) / 10
        mGapY = (mTargetY - mPosY) / 10
        mIsActive = true
    }

    // 병사의 생각을 관리함.
    override var mCount = 0

    // 길찾기 알고리즘에서 사용하는 블럭개수 관리
    var mPathCount = 0
    var mLastTime: Long = 0

    //----------
    // 생성자
    init { //, MainGLRenderer mainGLRenderer) {
        //mMainGLRenderer = mainGLRenderer;
        // mCount는 매번 루프를 돌때마다 호출된다. 병사의 움직임에 관여하는데 병사마다
        // 다른 시작점을 줌으로써 각기 다른 움직임을 갖도록 처리한다.
        mCount = (Math.random() * 100).toInt()
    }

    // 병사가 생각하도록 만드는 함수
    fun think() {
        if (mIsActive === false) {
            return
        }
        mPosX = mPosX + mGapX
        mPosY = mPosY + mGapY


        // mPath배열의 마지막 인덱스까지 계산했다면 최종 목적지에 도착한 상태임
        if (Math.abs(mTargetX - mPosX) < 10 &&
            Math.abs(mTargetY - mPosY) < 10
        ) {
            mAttackSoldier!!.mIsAttack = false
            mMainGLRenderer?.let { mTargetZombie!!.decreaseEnergy(mEnergy, it) }
            destroy()
        }
    }

    fun destroy() {
        setIsActive(false)
    }
}
