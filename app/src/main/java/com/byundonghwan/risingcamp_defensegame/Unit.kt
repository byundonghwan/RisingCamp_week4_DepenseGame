package com.byundonghwan.risingcamp_defensegame

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

// 유닛
open class Unit( var mProgramImage: Int, var mProgramSolidColor: Int) {
    protected var mPositionHandle: Int
    protected var mTexCoordLoc: Int
    protected var mtrxhandle: Int
    protected var mSamplerLoc: Int
    protected var mVertexBuffer: FloatBuffer? = null
    protected var mDrawListBuffer: ShortBuffer? = null
    protected var mUvBuffer: FloatBuffer? = null

    // 매트릭스변환을 위한 변수
    protected val mMVPMatrix = FloatArray(16)
    protected val mMVPMatrix2 = FloatArray(16)
    protected var mRotationMatrix = FloatArray(16)
    protected var mScaleMatrix = FloatArray(16)
    protected var mTranslationMatrix = FloatArray(16)

    // 비트맵 이미지 핸들관리 (여러건 처리를 위해 배열로 정의)
    protected lateinit var mHandleBitmap: IntArray
    protected var mBitmapCount = 0
    protected lateinit var mBitmap: Array<Bitmap>

    // 현재의 위치정보
    protected var mPosX = 0f
    protected var mPosY = 0f

    // 이동하려는 위치정보
    protected var mTargetX = 0f
    protected var mTargetY = 0f

    // 게임의 맵에서 이동하려는 좌표
    protected var mTargetBlockRow = 0
    protected var mTargetBlockCol = 0

    // 객체의 폭을 반환함
    // 이미지의 가로, 세로 설정
    protected var mWidth: Float = 0f
    protected var mHeight = 0f

    // 이미지의 기울기 설정
    protected var mAngle = 0

    // 이미지의 확대, 축소 설정
    protected var mScaleX = 1.0f
    protected var mScaleY = 1.0f

    // 유닛의 움직임을 관리하는 변수
    protected open var mCount = 0

    // 여러개의 이미지 중 화면에 표시할 인덱스번호
    protected var mBitmapState = 0

    // 객체의 활성화여부를 설정함
    // 객체의 활성화여부를 반환함
    // 현재 객체의 활성화 여부
    protected var mIsActive: Boolean = false

    // 생성자
    init {
        mPositionHandle = GLES20.glGetAttribLocation(mProgramImage, "vPosition")
        mTexCoordLoc = GLES20.glGetAttribLocation(mProgramImage, "a_texCoord")
        mtrxhandle = GLES20.glGetUniformLocation(mProgramImage, "uMVPMatrix")
        mSamplerLoc = GLES20.glGetUniformLocation(mProgramImage, "s_texture")
    }

    // 이미지핸들 배열, 가로,세로 값을 받아와 설정
    fun setBitmap(handle: IntArray, width: Int, height: Int) {
        mBitmapCount = handle.size
        this.mWidth = width.toFloat()
        this.mHeight = height.toFloat()
        setupBuffer()
        mHandleBitmap = IntArray(mBitmapCount)
        mHandleBitmap = handle
        mBitmapState = 0
    }

    // 이미지핸들, 가로, 세로 값을 받아와 설정
    fun setBitmap(handle: Int, width: Int, height: Int) {
        mBitmapCount = 1
        this.mWidth = width.toFloat()
        this.mHeight = height.toFloat()
        setupBuffer()
        mHandleBitmap = IntArray(mBitmapCount)
        mHandleBitmap[0] = handle
        mBitmapState = 0
    }

    // 위치정보를 설정함
    fun setPos(posX: Float, posY: Float) {
        mPosX = posX
        mPosY = posY
        mTargetX = posX
        mTargetY = posY
    }

    // 현재의 X 좌표를 설정함
    fun setPosX(posX: Float) {
        mPosX = posX
        mTargetX = mPosX
    }

    // 현재의 Y좌표를 설정함
    fun setPosY(posY: Float) {
        mPosY = posY
        mTargetY = mPosY
    }

    // 객체의 크기를 계산하여 하단의 X 위치를 설정함
    fun setPosBottomX(posX: Float) {
        mPosX = posX
        mTargetX = mPosX
    }

    // 객체의 크기를 계산하여 하단의 Y 위치를 설정함
    fun setPosBottomY(posY: Float) {
        mPosY = posY + mHeight / 3
        mTargetY = mPosY
    }

    // 기울기를 설정함
    fun setAngle(angle: Int) {
        mAngle = angle
    }

    // 객체의 활성화여부를 설정함
    open fun setIsActive(isActive: Boolean) {
        mIsActive = isActive
    }

    // 객체의 활성화여부를 반환함
    open fun getIsActive(): Boolean {
        return mIsActive
    }

    // 이미지 처리를 위한 버퍼를 설정함.
    fun setupBuffer() {
        mVertices = floatArrayOf(
            mWidth / -2, mHeight / 2, 0.0f,
            mWidth / -2, mHeight / -2, 0.0f,
            mWidth / 2, mHeight / -2, 0.0f,
            mWidth / 2, mHeight / 2, 0.0f
        )
        mIndices = shortArrayOf(0, 1, 2, 0, 2, 3) // The order of vertexrendering.
        val bb = ByteBuffer.allocateDirect(mVertices.size * 4)
        bb.order(ByteOrder.nativeOrder())
        mVertexBuffer = bb.asFloatBuffer()
        mVertexBuffer!!.put(mVertices)
        mVertexBuffer!!.position(0)
        val dlb = ByteBuffer.allocateDirect(mIndices.size * 2)
        dlb.order(ByteOrder.nativeOrder())
        mDrawListBuffer = dlb.asShortBuffer()
        mDrawListBuffer!!.put(mIndices)
        mDrawListBuffer!!.position(0)
        mUvs = floatArrayOf(
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
        )
        val bbUvs = ByteBuffer.allocateDirect(mUvs.size * 4)
        bbUvs.order(ByteOrder.nativeOrder())
        mUvBuffer = bbUvs.asFloatBuffer()
        mUvBuffer!!.put(mUvs)
        mUvBuffer!!.position(0)
    }

    // 현재 객체가 선택되었는지를 반환함
    fun isSelected(x: Int, y: Int): Boolean {
        var isSelected = false
        if (mIsActive) {
            if (x >= mPosX - mWidth / 2 && x <= mPosX + mWidth / 2 &&
                y >= mPosY - mHeight / 2 && y <= mPosY + mHeight / 2
            ) {
                isSelected = true
            }
        }
        return isSelected
    }

    // 그리기
    fun draw(m: FloatArray?) {
        // 활성화 상태가 아니라면 그리지 않는다.
        if (!mIsActive) {
            return
        }
        // 회전, 가로/세로 확대,축소를 변환한다.
        // 변환이 없을 경우 호출하지 않도록 미리 구분했다.
        if (mAngle != 0) {
            Matrix.setIdentityM(mTranslationMatrix, 0)
            Matrix.setIdentityM(mRotationMatrix, 0)
            Matrix.translateM(mTranslationMatrix, 0, mPosX, mPosY, 0f)
            Matrix.setRotateM(mRotationMatrix, 0, mAngle.toFloat(), 0f, 0f, -1.0f)
            Matrix.multiplyMM(mMVPMatrix, 0, m, 0, mTranslationMatrix, 0)
            Matrix.multiplyMM(mMVPMatrix2, 0, mMVPMatrix, 0, mRotationMatrix, 0)
        } else if (mScaleX != 1.0f || mScaleY != 1.0f) {
            Matrix.setIdentityM(mTranslationMatrix, 0)
            Matrix.setIdentityM(mScaleMatrix, 0)
            Matrix.translateM(mTranslationMatrix, 0, mPosX, mPosY, 0f)
            Matrix.scaleM(mScaleMatrix, 0, mScaleX, mScaleY, 1.0f)
            Matrix.multiplyMM(mMVPMatrix, 0, m, 0, mTranslationMatrix, 0) //
            Matrix.multiplyMM(mMVPMatrix2, 0, mMVPMatrix, 0, mScaleMatrix, 0)
        } else {
            Matrix.setIdentityM(mTranslationMatrix, 0)
            Matrix.translateM(mTranslationMatrix, 0, mPosX, mPosY, 0f)
            Matrix.multiplyMM(mMVPMatrix2, 0, m, 0, mTranslationMatrix, 0)
        }
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer)
        GLES20.glEnableVertexAttribArray(mTexCoordLoc)
        GLES20.glVertexAttribPointer(mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, mUvBuffer)
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, mMVPMatrix2, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mHandleBitmap[mBitmapState])
        GLES20.glUniform1i(mSamplerLoc, 0)
        // 투명한 배경을 처리한다.
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        // 이미지 핸들을 출력한다.
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES, mIndices.size,
            GLES20.GL_UNSIGNED_SHORT, mDrawListBuffer
        )
        GLES20.glDisableVertexAttribArray(mPositionHandle)
        GLES20.glDisableVertexAttribArray(mTexCoordLoc)
    }

    companion object {
        // 기본적인 이미지 처리를 위한 변수
        protected lateinit var mVertices: FloatArray
        protected lateinit var mIndices: ShortArray
        protected lateinit var mUvs: FloatArray
    }
}