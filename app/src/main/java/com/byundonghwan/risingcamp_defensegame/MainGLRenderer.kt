package com.byundonghwan.risingcamp_defensegame

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.view.MotionEvent
import com.byundonghwan.risingcamp_defensegame.Map.getPosX
import com.byundonghwan.risingcamp_defensegame.Map.getPosY
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// 랜더링
class MainGLRenderer(// 주 액티비티
    var mActivity: MainActivity, width: Int, height: Int
) :
    GLSurfaceView.Renderer {
    // 매트릭스
    private val mMtrxProjection = FloatArray(16)
    private val mMtrxView = FloatArray(16)
    private val mMtrxProjectionAndView = FloatArray(16)
    var mLastTime: Long
    var mContext: Context

    // 화면 설정
    var mScreenConfig: ScreenConfig? = null

    // 리소스 매니저
    var mResourceManager: ResourceManager? = null

    // 객체로 사용할 객체
    var mTitle: Unit? = null
    var mButtons = arrayOfNulls<Button>(3)
    var mZombie = arrayOfNulls<Zombie>(100)
    var mSoldier = arrayOfNulls<Soldier>(40)
    var mBullet = arrayOfNulls<Bullet>(400)
    var mScore = 0
    var mScorePanel: NumberPanel? = null

    // 터치포인터
    private var mPointerId = 0
    private var mPointerId2 // 포인터 ID2 (핀치 기능으로 2개의 터치까지 체크함)
            = 0
    private var mStartX // 터치시 시작 위치
            = 0f
    private var mStartY = 0f
    private var mEndX // 터치가 종료 위치
            = 0f
    private var mEndY = 0f
    private var mSizeRatio = 1f // 화면 비율을 관리함
    private var mIsTap = true // 탭이었는지를 체크함
    private var mIsMove = false // 이동이었는지를 체크함
    private var mIsExpend = false // 확대,축소 였는지를 체크함
    private var mMoveInputX // 화면 이동시 X축 시작점
            = 0
    private var mMoveInputY // 화면 이동시 Y축 시작점
            = 0
    private var mStartExpandLength // 핀치 기능을 사용시 시작점 길이
            = 0f
    private var mEndExpandLength // 핀치 기능을 사용시 종료점 길이
            = 0f
    private var mBfEndExpandLength // 핀치 기능을 사용시 이전 종료점 길이
            = 0f

    // 멈춤
    fun onPause() {}

    // 재시작
    fun onResume() {
        mLastTime = System.currentTimeMillis()
    }

    // 서피스뷰 변경
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(
            0, 0, mDeviceWidth,
            mDeviceHeight
        )
        Matrix.setIdentityM(mMtrxProjection, 0)
        Matrix.setIdentityM(mMtrxView, 0)
        Matrix.setIdentityM(mMtrxProjectionAndView, 0)
        Matrix.orthoM(mMtrxProjection, 0, 0f, 2000f, 0.0f, 1200f, 0f, 50f)
        Matrix.setLookAtM(mMtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
    }

    // 서피스뷰 생성
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        mScreenConfig = ScreenConfig(mDeviceWidth, mDeviceHeight)
        mScreenConfig!!.setSize(2000, 1200)
        GLES20.glClearColor(0.0f, 0.5f, 0.0f, 1f)
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vs_Image)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fs_Image)
        mProgramImage = GLES20.glCreateProgram()
        GLES20.glAttachShader(mProgramImage, vertexShader)
        GLES20.glAttachShader(mProgramImage, fragmentShader)
        GLES20.glLinkProgram(mProgramImage)
        GLES20.glUseProgram(mProgramImage)
        val scale = mContext.resources.displayMetrics.density
        mResourceManager = ResourceManager(mActivity, mContext, scale)

        // 유닛을 생성하고 이미지, 크기, 좌표를 설정한다.  -- 자원관리
        mTitle = Unit(mProgramImage, mProgramSolidColor)
        Map.getMap()
        for (i in 0 until Map.mInfoSizeRow) {
            for (j in 0 until Map.mInfoSizeCol) {
                Map.mLand[i][j] = Unit(mProgramImage, mProgramSolidColor)
            }
        }
        for (i in 0..2) {
            mButtons[i] = Button(mProgramImage, mProgramSolidColor)
        }
        for (i in 0..99) {
            mZombie[i] = Zombie(mProgramImage, mProgramSolidColor)
        }
        for (i in 0..39) {
            mSoldier[i] = Soldier(mProgramImage, mProgramSolidColor)
        }
        for (i in 0..399) {
            mBullet[i] = Bullet(mProgramImage, mProgramSolidColor)
        }
        mScorePanel = NumberPanel(mProgramImage, mProgramSolidColor)
        mResourceManager!!.loadResource(mTitle!!, Map.mLand, mButtons, mZombie, mBullet)
        mTitle!!.setIsActive(true)
        mTitle!!.setPos(1000f, 600f)
        //ConstManager.SCREEN_MODE = ConstManager.SCREEN_GAME;
        for (i in 0 until Map.mInfoSizeRow) {
            for (j in 0 until Map.mInfoSizeRow) {
                Map.mLand[i][j]!!.setIsActive(true)
                //Map.mLand[i][j].setPos(110 + i * 200, 110);
                if (Map.mInfo[i]!![j] == 0) {
                    Map.mLand[i][j]!!
                        .setPos(getPosX(i, j), getPosY(i, j))
                } else {
                    Map.mLand[i][j]!!
                        .setPos(getPosX(i, j), getPosY(i, j) + 20)
                }
            }
        }
        for (i in 0..2) {
            mButtons[i]!!.setIsActive(true)
            mButtons[i]!!.setPos((110 + i * 200).toFloat(), 110f)
        }
        for (i in 0..9) {
            for (j in 0..9) {
                mZombie[i * 10 + j]!!.setToBlock(i, j)
                //mZombie[i*10 + j].setIsActive(true);
            }
        }
        mScorePanel!!.setPos(50, 1200 - 50)
        mScorePanel!!.setNumberSize(25, 50)
        mScorePanel!!.setIsActive(true)
        mResourceManager!!.getHandleNumber()?.let { mScorePanel!!.setBitmap(it) }
        mScorePanel!!.setNumber(0)
    }

    // 그리기 호출
    override fun onDrawFrame(unused: GL10?) {
        val now = System.currentTimeMillis()
        if (mLastTime > now) return
        val elapsed = now - mLastTime
        // 그리기를 시작한다.
        if (ConstManager.SCREEN_MODE == ConstManager.SCREEN_INTRO) {
            renderIntro(mMtrxProjectionAndView)
        } else if (ConstManager.SCREEN_MODE == ConstManager.SCREEN_GAME) {
            renderGame(mMtrxProjectionAndView)
        }
        mLastTime = now
    }

    private fun renderIntro(m: FloatArray) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1f)
        Matrix.orthoM(mMtrxProjection, 0, 0f, 2000f, 0.0f, 1200f, 0f, 50f)
        Matrix.multiplyMM(mMtrxProjectionAndView, 0, mMtrxProjection, 0, mMtrxView, 0)
        mTitle!!.draw(mMtrxProjectionAndView)
    }

    private fun renderGame(m: FloatArray) {
        think()
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1f)
        //Matrix.orthoM(mMtrxProjection, 0, 0f, 2000, 0.0f, 1200, 0, 50);
        Matrix.orthoM(
            mMtrxProjection, 0, mMoveInputX * mSizeRatio,
            mMoveInputX * mSizeRatio + 2000 * mSizeRatio,
            mMoveInputY * mSizeRatio,
            mMoveInputY * mSizeRatio + 1200 * mSizeRatio, 0f, 50f
        )
        Matrix.multiplyMM(mMtrxProjectionAndView, 0, mMtrxProjection, 0, mMtrxView, 0)

        for (i in 0 until Map.mInfoSizeRow) {
            for (j in 0 until Map.mInfoSizeCol) {
                if (Map.mInfo[i]!![j] == 0) {
                    Map.mLand[i][j]!!.draw(mMtrxProjectionAndView)
                }
            }
        }
        for (i in 0 until Map.mInfoSizeRow) {
            for (j in 0 until Map.mInfoSizeCol) {
                if (Map.mInfo[i]!![j] == 1) {
                    Map.mLand[i][j]!!.draw(mMtrxProjectionAndView)
                }
                for (k in 0 until Schedule.count) {
                    if (mZombie[k] != null && mZombie[k]!!.getIsActive() === true) {
                        if (mZombie[k]!!.getCurrRow() === i && mZombie[k]!!.getCurrCol() === j) {
                            mZombie[k]!!.draw(mMtrxProjectionAndView)
                        }
                    }
                }
                for (k in 0 until mSoldierIndex) {
                    if (mSoldier[k] != null && mSoldier[k]!!.getIsActive() === true) {
                        if (mSoldier[k]!!.getCurrRow() === i && mSoldier[k]!!.getCurrCol() === j) {
                            mSoldier[k]!!.draw(mMtrxProjectionAndView)
                            break
                        }
                    }
                }
            }
        }
        for (i in 0..399) {
            mBullet[i]!!.draw(mMtrxProjectionAndView)
        }
        Matrix.orthoM(mMtrxProjection, 0, 0f, 2000f, 0.0f, 1200f, 0f, 50f)
        Matrix.multiplyMM(mMtrxProjectionAndView, 0, mMtrxProjection, 0, mMtrxView, 0)
        for (i in 0..2) {
            mButtons[i]!!.draw(mMtrxProjectionAndView)
        }
        mScorePanel!!.draw(mMtrxProjectionAndView)
    }

    private var mStartTime: Long = -1
    private fun startSchedule() {
        mStartTime = System.currentTimeMillis()
    }

    fun think() {
        val now = System.currentTimeMillis()
        val gab = ((now - mStartTime) / 1000).toInt()
        for (i in 0 until Schedule.count) {
            if (mZombie[i]!!.getIsActive() === false) {
                if (Schedule.mTimer.get(i).toInt() === gab) {
                    mZombie[i]!!.setToBlock(19, 9)
                    mZombie[i]!!.moveToPosBlock(0, 10)
                    mZombie[i]!!.setSpeed(Schedule.mSpeed.get(i))
                    mZombie[i]!!.setEnerge(Schedule.mEnerge.get(i))
                    mZombie[i]!!.setIsActive(true)
                }
            }
        }
        for (i in 0 until Schedule.count) {
            if (mZombie[i]!!.getIsActive() === true) {
                mZombie[i]!!.think()
            }
        }
        for (i in 0..39) {
            mSoldier[i]!!.think()
            attackZombie(mSoldier[i])
        }
        for (i in 0..399) {
            mBullet[i]!!.think()
        }
    }

    fun attackZombie(soldier: Soldier?) {
        if (soldier!!.mIsAttack === true) {
            return
        }
        for (i in mZombie.indices) {
            if (mZombie[i]!!.getIsActive() === true) {
                if (Math.abs(mZombie[i]!!.mPosBlockRow - soldier!!.mPosBlockRow) < 5 &&
                    Math.abs(mZombie[i]!!.mPosBlockCol - soldier!!.mPosBlockCol) < 5
                ) {
                    for (j in 0..399) {
                        if (mBullet[j]!!.getIsActive() == false) {
                            if (soldier != null) {
                                soldier.mIsAttack = true
                            }
                            if (soldier != null) {
                                mBullet[j]!!.setType(soldier.mType)
                            }
                            mBullet[j]!!.moveToBlock(
                                soldier,
                                mZombie[i],
                                soldier!!.mPosBlockRow,
                                soldier!!.mPosBlockCol,
                                mZombie[i]!!.mPosBlockRow,
                                mZombie[i]!!.mPosBlockCol,
                                this
                            )
                            return
                        }
                    }
                }
            }
        }
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        //if (mIsDraw == false) {
        //    return true;
        //}
        // 좌표
        val x = event.x.toInt()
        val y = event.y.toInt()
        // 변환된 좌표
        val chgX = mScreenConfig!!.getX(x) // 디바이스에 터치된 위치를 OpenGL 프로젝션 크기로 변환한다.
        val chgY = mScreenConfig!!.getY(y)
        val action = event.action // 터치 이벤트의 종류를 받는다.
        mPointerId = event.getPointerId(0) // 터치 이벤트의 첫번째 포인터다.
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                // 터치 다운할 경우
                mStartX = chgX.toFloat() // 시작위치
                mStartY = chgY.toFloat()
                mIsTap = true // 우선 탭으로 간주한다.
                mIsMove = false // 우선 화면이동으로 간주하지 않는다.
                mIsExpend = false // 우선 화면확대, 축소로 간주하지 않는다.
            }
            MotionEvent.ACTION_MOVE -> {
                // 터치한 곳이 이동할 경우
                mEndX = chgX.toFloat() // 이동중에도 좌표를 읽는다.
                mEndY = chgY.toFloat()
                if (!mIsExpend) { // 핀치기능이 아닐 경우
                    if (mIsTap) { // 탭을 하고나서 움직인 거라면
                        if (Math.abs(mEndX - mStartX) > mScreenConfig!!.getX(5) ||
                            Math.abs(mEndY - mStartY) > mScreenConfig!!.getY(5)
                        ) {
                            mIsMove = true // 5보다 크게 움직였다면 움직인 것으로 간주한다.
                        }
                        moveScreenX((mEndX - mStartX).toInt().toFloat()) // 화면을 이동시킨다.
                        moveScreenY((mEndY - mStartY).toInt().toFloat())
                        mStartX = mEndX // 시작점은 움직인 점으로 대체한다.
                        mStartY = mEndY
                    }
                } else { // 핀치기능일 경우
                    val x2 = event.getX(mPointerId2) // 두번째 터치한 좌표를 읽는다.
                    val y2 = event.getY(mPointerId2)
                    mEndExpandLength = Math.abs(x - x2) // 손가락을 벌린 폭을 계산한다.
                    // 확장한 비유을 계산한다.
                    mSizeRatio -= (mEndExpandLength - mStartExpandLength -
                            mBfEndExpandLength) / mStartExpandLength
                    if (mSizeRatio < 0.5) { // 너무 작게 축소할 경우 축소 비율을 제한한다.
                        mSizeRatio = 0.5f
                    }
                    if (mSizeRatio > 1) { // 너무 크게 확대할 경우 제한한다.
                        mSizeRatio = 1f
                    }
                    scaleScreen(mSizeRatio) // 화면을 축소, 확대하도록 호출한다.
                    mBfEndExpandLength = mEndExpandLength - mStartExpandLength
                }
            }
            MotionEvent.ACTION_UP -> {
                // 터치 업할 경우
                if (!mIsExpend) { // 핀치기능이 아니라면
                    if (!mIsMove) { // 이동이 아니라면
                        selectTouch(chgX, chgY) // 터치(탭)한 것으로 간주한다.
                    }
                    mIsTap = false // 탭을 취소한다.
                    mIsMove = false // 이동을 취소한다.
                    mEndX = chgX.toFloat()
                    mEndY = chgY.toFloat()
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                // 터치한 좌표가 화면 밖으로 이동하거나 할 경우
                mIsTap = false
                mIsMove = false
                mIsExpend = false
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                // 두번째 터치 다운이 발생할 경우
                mIsMove = false // 이동이 아님
                mIsTap = false // 탭이 아님
                mIsExpend = true // 핀치기능임
                // 터치된 포인터가 꼭 두번째가 아닐 수 있기 때문에 이를 계산하기 위해 쉬프트 연산한다.
                val pointerIndex = (event.action and
                        MotionEvent.ACTION_POINTER_INDEX_MASK
                        shr MotionEvent.ACTION_POINTER_INDEX_SHIFT)
                event.pointerCount
                try {
                    mPointerId2 = event.getPointerId(pointerIndex) // 두번째 터치 포인터를 얻는다.
                    val x2 = event.getX(mPointerId2).toInt().toFloat() // 터치 좌표를 읽는다.
                    val y2 = event.getY(mPointerId2).toInt().toFloat()
                    mStartExpandLength = Math.abs(x - x2) // 확대한 폭을 계산한다.
                } catch (ex: Exception) {
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                // 두번째 터치 업의 경우
                try {
                    mStartExpandLength = 0f // 확대, 축소의 시작길이을 초기화한다.
                    mEndExpandLength = 0f // 확대, 축소의 종료길이를 초기화 한다.
                    mBfEndExpandLength = 0f
                    mIsExpend = false // 핀치 기능이 아님
                } catch (ex: Exception) {
                }
            }
        }
        return true
    }

    fun moveScreenX(input: Float) {
        mMoveInputX -= input.toInt()
    }

    fun moveScreenY(input: Float) {
        mMoveInputY -= input.toInt()
    }

    fun scaleScreen(ratio: Float) {
        mSizeRatio = ratio
    }

    private fun selectTouch(x: Int, y: Int) {
        if (ConstManager.SCREEN_MODE == ConstManager.SCREEN_INTRO) {
            selectTouchIntro(x, y)
        } else if (ConstManager.SCREEN_MODE == ConstManager.SCREEN_GAME) {
            selectTouchGame(x, y)
        }
    }

    private fun selectTouchIntro(x: Int, y: Int) {
        ConstManager.SCREEN_MODE = ConstManager.SCREEN_GAME
        startSchedule()
    }

    private var mSelectedButtonIndex = -1
    private fun selectTouchGame(x: Int, y: Int) {
        if (mButtons[0]!!.isSelected(x, y)) {
            mSelectedButtonIndex = 0
        } else if (mButtons[1]!!.isSelected(x, y)) {
            mSelectedButtonIndex = 1
        } else if (mButtons[2]!!.isSelected(x, y)) {
            //ConstManager.SCREEN_MODE = ConstManager.SCREEN_INTRO;
            mActivity.finish()
        } else if (mSelectedButtonIndex != -1) { // 버튼을 터치한 경우 -1이면 터치하지 않았다.
            var selectedRow = -1
            var selectedCol = -1
            val ratioX = ((mMoveInputX + x) * mSizeRatio).toInt()
            val ratioY = ((mMoveInputY + y) * mSizeRatio).toInt()
            for (j in Map.mInfoSizeCol - 1 downTo 0) {
                for (i in Map.mInfoSizeRow - 1 downTo 0) {
                    if (Map.mLand[i][j]!!
                            .isSelected(ratioX, ratioY)
                    ) {
                        if (Map.mInfo[i]!![j] == 1) {
                            selectedRow = i
                            selectedCol = j
                            addSoldier(mSelectedButtonIndex, selectedRow, selectedCol)
                            return
                        }
                    }
                }
            }
        }
    }

    var mSoldierIndex = 0

    // 생성자
    init {
        mContext = mActivity.applicationContext
        mLastTime = System.currentTimeMillis() + 100
        mDeviceWidth = width
        mDeviceHeight = height
    }

    // 터치시 아군 추가
    private fun addSoldier(type: Int, row: Int, col: Int) {
        val tempCount = 0
        if (type == 0) {
            ResourceManager.getHandleSoldier()?.let {
                mSoldier[mSoldierIndex]!!.setBitmap(
                    it,
                    mSoldier[mSoldierIndex]!!.Width,
                    mSoldier[mSoldierIndex]!!.Height
                )
            }
        } else if (type == 1) {
            ResourceManager.getHandleBigSoldier()?.let {
                mSoldier[mSoldierIndex]!!.setBitmap(
                    it,
                    mSoldier[mSoldierIndex]!!.Width,
                    mSoldier[mSoldierIndex]!!.Height
                )
            }
        }
        mSoldier[mSoldierIndex]!!.setProperty(type, mSoldierIndex)
        mSoldier[mSoldierIndex]!!.setToBlock(row, col)
        mSoldier[mSoldierIndex]!!.setIsActive(true)
        mSoldierIndex++
    }

    companion object {
        // 프로그램색상, 이미지
        private const val mProgramSolidColor = 0
        private var mProgramImage = 0

        // 디바이스의 넓이, 높이
        var mDeviceWidth = 0
        var mDeviceHeight = 0

        // 쉐이더 이미지
        const val vs_Image = "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "attribute vec2 a_texCoord;" +
                "varying vec2 v_texCoord;" +
                "void main() {" +
                " gl_Position = uMVPMatrix * vPosition;" +
                " v_texCoord = a_texCoord;" +
                "}"
        const val fs_Image = "precision mediump float;" +
                "varying vec2 v_texCoord;" +
                "uniform sampler2D s_texture;" +
                "void main() {" +
                " gl_FragColor = texture2D( s_texture, v_texCoord );" +
                "}"

        // 쉐이더 로딩
        fun loadShader(type: Int, shaderCode: String?): Int {
            val shader = GLES20.glCreateShader(type)
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
            return shader
        }
    }
}