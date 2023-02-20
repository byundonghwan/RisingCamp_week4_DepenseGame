package com.byundonghwan.risingcamp_defensegame

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils

// 리소스 관리
class ResourceManager(
    private val mActivity: Activity,
    private val mContext: Context,
    scale: Float
) {
    private val mHandleBtnSoldier = IntArray(2)
    private val mHandleBtnBigSoldier = IntArray(2)
    private val mHandleZombie = IntArray(8)
    

    // 화면확대축소관리
    private var mScale = 0f

    // 리소스로딩 생성자
    init {
        mScale = scale
    }

    // 리소스 로딩
    fun loadResource(
        title: Unit,
        land: Array<Array<Unit?>>,
        buttons: Array<Button?>,
        zombie: Array<Zombie?>,
        bullet: Array<Bullet?>
    ): kotlin.Unit {
        val bmpTitle = BitmapFactory.decodeResource(
            mContext.resources,
            mContext.resources.getIdentifier("drawable/title", null, mContext.packageName)
        )
        mHandleTitle = getImageHandle(bmpTitle)
        bmpTitle.recycle()
        title.setBitmap(mHandleTitle, 600, 600)

        // Land
        val bmpLand0 = BitmapFactory.decodeResource(
            mContext.resources,
            mContext.resources.getIdentifier("drawable/land0", null, mContext.packageName)
        )
        mHandleLand0 = getImageHandle(bmpLand0)
        bmpLand0.recycle()
        val bmpLand1 = BitmapFactory.decodeResource(
            mContext.resources,
            mContext.resources.getIdentifier("drawable/land1", null, mContext.packageName)
        )
        mHandleLand1 = getImageHandle(bmpLand1)
        bmpLand1.recycle()
        //land.setBitmap(mHandleLand,200,120);
        for (i in 0 until Map.mInfoSizeRow) {
            for (j in 0 until Map.mInfoSizeCol) {
                if (Map.mInfo[i][j] == 0) {
                    land[i][j]!!.setBitmap(mHandleLand0, 100, 60)
                } else {
                    land[i][j]!!.setBitmap(mHandleLand1, 100, 100)
                }
            }
        }
        // 버튼1
        val bmpBtnSoldier0 = BitmapFactory.decodeResource(
            mContext.resources,
            mContext.resources.getIdentifier(
                "drawable/button_soldier0", null,
                mContext.packageName
            )
        )
        mHandleBtnSoldier[0] = getImageHandle(bmpBtnSoldier0)
        bmpBtnSoldier0.recycle()
        val bmpBtnSoldier1 = BitmapFactory.decodeResource(
            mContext.resources,
            mContext.resources.getIdentifier(
                "drawable/button_soldier1", null,
                mContext.packageName
            )
        )
        mHandleBtnSoldier[1] = getImageHandle(bmpBtnSoldier1)
        bmpBtnSoldier1.recycle()
        buttons[0]!!.setBitmap(mHandleBtnSoldier, 200, 200)
        // 버튼2
        val bmpBtnSoldier2 = BitmapFactory.decodeResource(
            mContext.resources,
            mContext.resources.getIdentifier(
                "drawable/button_soldier2", null,
                mContext.packageName
            )
        )
        mHandleBtnBigSoldier[0] = getImageHandle(bmpBtnSoldier2)
        bmpBtnSoldier2.recycle()
        val bmpBtnSoldier3 = BitmapFactory.decodeResource(
            mContext.resources,
            mContext.resources.getIdentifier(
                "drawable/button_soldier3", null,
                mContext.packageName
            )
        )
        mHandleBtnBigSoldier[1] = getImageHandle(bmpBtnSoldier3)
        bmpBtnSoldier3.recycle()
        buttons[1]!!.setBitmap(mHandleBtnBigSoldier, 200, 200)
        // 버튼(Exit);
        val bmpBtnExit = BitmapFactory.decodeResource(
            mContext.resources,
            mContext.resources.getIdentifier(
                "drawable/button_exit", null,
                mContext.packageName
            )
        )
        mHandleBtnExit = getImageHandle(bmpBtnExit)
        bmpBtnExit.recycle()
        buttons[2]!!.setBitmap(mHandleBtnExit, 200, 200)
        for (i in 0..7) {
            val bmpZombie = BitmapFactory.decodeResource(
                mContext.resources,
                mContext.resources.getIdentifier("drawable/zombie$i", null, mContext.packageName)
            )
            mHandleZombie[i] = getImageHandle(bmpZombie)
            bmpZombie.recycle()
        }
        for (i in 0..99) {
            zombie[i]!!.setBitmap(mHandleZombie, 100, 150)
        }

        // 군인
        // soldier
        val soldier0 = BitmapFactory.decodeResource(
            mContext.resources,
            mContext.resources.getIdentifier(
                "drawable/soldier0", null,
                mContext.packageName
            )
        )
        mHandleSoldier[0] = getImageHandle(soldier0)
        soldier0.recycle()
        val soldier1 = BitmapFactory.decodeResource(
            mContext.resources,
            mContext.resources.getIdentifier(
                "drawable/soldier1", null,
                mContext.packageName
            )
        )
        mHandleSoldier[1] = getImageHandle(soldier1)
        soldier1.recycle()
        //[0].setBitmap(mHandleSoldier,200,200);
        val soldier2 = BitmapFactory.decodeResource(
            mContext.resources,
            mContext.resources.getIdentifier(
                "drawable/soldier2", null,
                mContext.packageName
            )
        )
        mHandleBigSoldier[0] = getImageHandle(soldier2)
        soldier2.recycle()
        val soldier3 = BitmapFactory.decodeResource(
            mContext.resources,
            mContext.resources.getIdentifier(
                "drawable/soldier3", null,
                mContext.packageName
            )
        )
        mHandleBigSoldier[1] = getImageHandle(soldier3)
        soldier3.recycle()
        val bmpBullet = BitmapFactory.decodeResource(
            mContext.resources,
            mContext.resources.getIdentifier(
                "drawable/bullet", null,
                mContext.packageName
            )
        )
        mHandleBullet = getImageHandle(bmpBullet)
        bmpBullet.recycle()
        for (i in 0..399) {
            bullet[i]!!.setBitmap(mHandleBullet, 20, 20)
        }
        val bmpNumber = arrayOfNulls<Bitmap>(10)
        for (i in 0..9) {
            bmpNumber[i] = BitmapFactory.decodeResource(
                mContext.resources,
                mContext.resources.getIdentifier(
                    "drawable/num$i", null,
                    mContext.packageName
                )
            )
            mHandleNumber[i] = getImageHandle(bmpNumber[i])
            bmpNumber[i]!!.recycle()
        }
    }
    fun getHandleNumber(): IntArray? {
        return mHandleNumber
    }
   

    // 이미지 핸들 반환
    private fun getImageHandle(bitmap: Bitmap?): Int {
        val texturenames = IntArray(1)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glGenTextures(1, texturenames, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0])
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR
        )
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        return texturenames[0]
    }

    companion object {
        private val mHandleSoldier = IntArray(2)
        private val mHandleBigSoldier = IntArray(2)
        private var mHandleBullet = 0
        private val mHandleNumber = IntArray(10)
        
        fun getHandleSoldier(): IntArray? {
            return mHandleSoldier
        }

        fun getHandleBigSoldier(): IntArray? {
            return mHandleBigSoldier
        }

        private var mHandleTitle = 0
        private var mHandleLand0 = 0
        private var mHandleLand1 = 0
        private var mHandleBtnExit = 0

    }
}