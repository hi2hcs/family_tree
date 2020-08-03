package com.hcs.familytree.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.hcs.familytree.model.FamilyDataBaseHelper
import com.hcs.familytree.model.FamilyMemberEntity
import com.hcs.familytree.model.FamilyMemberModel
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Environment
import com.hcs.familytree.R
import com.hcs.familytree.utils.Preference
import java.io.File
import java.io.FileOutputStream
import java.util.*


class MainActivity : AppCompatActivity() {

    val TAG = "FamilyMemberEntity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

    }

    private fun initView() {
        initData()
    }

    private fun initData() {

        Flowable.create(FlowableOnSubscribe<FamilyMemberModel> {

            // sharePreference存储firstIn，标记当前是否第一次启动
            var firstIn by Preference<String>("firstIn", "1")
            println("firstIn = $firstIn")
            if ("1" == firstIn) {
                // 应用第一次启动，则往数据库插入一些数据

                // 插入第一条数据，同时也是整个家族树的跟节点
                var familyMember = FamilyMemberEntity("王根")//1
                familyMember.imagePath = "111.jpg"
                familyMember.phone = "18156094171"
                familyMember.sex = 1
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                familyMember = FamilyMemberEntity("王明")//2
                familyMember.imagePath = "222.jpg"
                familyMember.phone = "18156096666"
                familyMember.fatherId = 1 // 父亲id为1，表示其父亲未第1条插入的数据，也就是上面的王根
                familyMember.sex = 1
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                familyMember = FamilyMemberEntity("王芸")//3
                familyMember.imagePath = "222.jpg"
                familyMember.phone = "18156096666"
                familyMember.fatherId = 1 // 父亲id为1，表示其父亲未第1条插入的数据，也就是上面的王根
                familyMember.sex = 0

                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                familyMember = FamilyMemberEntity("王康")//4
                familyMember.imagePath = "222.jpg"
                familyMember.phone = "18156096677"
                familyMember.fatherId = 1 // 父亲id为1，表示其父亲未第1条插入的数据，也就是上面的王根
                familyMember.sex = 0
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                familyMember = FamilyMemberEntity("王恩")//5
                familyMember.imagePath = "222.jpg"
                familyMember.phone = "18156096666"
                familyMember.fatherId = 2 // 父亲id为2，表示其父亲未第2条插入的数据，也就是上面的王明
                familyMember.sex = 0
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                familyMember = FamilyMemberEntity("王都")//6
                familyMember.imagePath = "222.jpg"
                familyMember.phone = "18156096667"
                familyMember.fatherId = 2 // 父亲id为2，表示其父亲未第2条插入的数据，也就是上面的王明
                familyMember.sex = 1
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                familyMember = FamilyMemberEntity("王样")//7
                familyMember.imagePath = "222.jpg"
                familyMember.phone = "18156096668"
                familyMember.fatherId = 2 // 父亲id为2，表示其父亲未第2条插入的数据，也就是上面的王明
                familyMember.sex = 0
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                familyMember = FamilyMemberEntity("王斗")//8
                familyMember.imagePath = "222.jpg"
                familyMember.phone = "18156096699"
                familyMember.fatherId = 6 // 父亲id为2，表示其父亲未第2条插入的数据，也就是上面的王明
                familyMember.sex = 0
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                familyMember = FamilyMemberEntity("王哦")//9
                familyMember.imagePath = "222.jpg"
                familyMember.phone = "18156096600"
                familyMember.fatherId = 3 // 父亲id为2，表示其父亲未第2条插入的数据，也就是上面的王明
                familyMember.sex = 0
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                familyMember = FamilyMemberEntity("王逗")//10
                familyMember.imagePath = "222.jpg"
                familyMember.phone = "18156096600"
                familyMember.fatherId = 4 // 父亲id为2，表示其父亲未第2条插入的数据，也就是上面的王明
                familyMember.sex = 0
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                familyMember = FamilyMemberEntity("王跟")//11
                familyMember.imagePath = "222.jpg"
                familyMember.phone = "18156096600"
                familyMember.fatherId = 10 // 父亲id为2，表示其父亲未第2条插入的数据，也就是上面的王明
                familyMember.sex = 0
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                familyMember = FamilyMemberEntity("王因")//12
                familyMember.imagePath = "222.jpg"
                familyMember.phone = "18156096600"
                familyMember.fatherId = 1 // 父亲id为2，表示其父亲未第2条插入的数据，也就是上面的王明
                familyMember.sex = 0
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                familyMember = FamilyMemberEntity("王学")//13
                familyMember.imagePath = "222.jpg"
                familyMember.phone = "18156096600"
                familyMember.fatherId = 8 // 父亲id为2，表示其父亲未第2条插入的数据，也就是上面的王明
                familyMember.sex = 0
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)


                firstIn = "0"
            }

            var familyMember = FamilyDataBaseHelper.getInstance(this).getFamilyMember(1)

            if (familyMember != null) {
                var familyMemberModel = familyMember.generateMember(this, 0)
                it.onNext(familyMemberModel)
            } else {
                it.onError(Throwable("空的"))
            }
            it.onComplete()

        }, BackpressureStrategy.ERROR)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                familyMemberLayout.displayUI(it)
            }

    }

    fun save(view: View) {
        Toast.makeText(this@MainActivity, "保存", Toast.LENGTH_SHORT).show()
        Thread(Runnable {
            viewSaveToImage(familyMemberLayout)
        }).start()
    }

    private fun loadBitmapFromView(v: View): Bitmap {
        val w = v.width
        val h = v.height
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        c.drawColor(Color.WHITE)
        /** 如果不设置canvas画布为白色，则生成透明  */
        v.draw(c)
        return bmp
    }

    private fun viewSaveToImage(view: View): String {
        view.isDrawingCacheEnabled = true
        view.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        view.drawingCacheBackgroundColor = Color.WHITE
        // 把一个View转换成图片
        val cachebmp = loadBitmapFromView(view)

        val fos: FileOutputStream
        var imagePath = ""
        try {
            // 判断手机设备是否有SD卡
            val isHasSDCard = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED
            )
            if (isHasSDCard) {
//             SD卡根目录
                val sdRoot = Environment.getExternalStorageDirectory()
                val file =
                    File(sdRoot, Calendar.getInstance().getTimeInMillis().toString() + ".png")
                fos = FileOutputStream(file)
                imagePath = file.getAbsolutePath()
//            } else {
//                throw Exception("创建文件失败!")
                cachebmp.compress(Bitmap.CompressFormat.PNG, 90, fos)
                fos.flush()
                fos.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        println("imagePath=$imagePath")
        view.destroyDrawingCache()
        return imagePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            initData()
        }
    }
}
