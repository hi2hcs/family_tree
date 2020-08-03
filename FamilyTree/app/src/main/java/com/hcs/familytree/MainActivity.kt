package com.hcs.familytree

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hcs.familytree.model.FamilyDataBaseHelper
import com.hcs.familytree.model.FamilyMemberEntity
import com.hcs.familytree.widget.FamilyTreeAdapter
import com.hcs.familytree.widget.PersonEntity
import com.hcs.familytree.R
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG = "FamilyMemberEntity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

    }

    private fun initView() {
        familyTree.familyTreeAdapter = FamilyTreeAdapter()

//        familyTree.familyTreeAdapter = com.hcs.familytree.FamilyTreeAdapter()

        initData()
    }

    private fun initData() {

        Flowable.create(FlowableOnSubscribe<PersonEntity> {

            // sharePreference存储firstIn，标记当前是否第一次启动
            var firstIn by Preference<String>("firstIn", "1")
            println("firstIn = $firstIn")
            if ("1" == firstIn) {

                // 应用第一次启动，则王数据库插入一些数据

                // 插入第一条数据，同时也是整个家族树的跟节点
                var familyMember = FamilyMemberEntity("王根")
                familyMember.imagePath = "111.jpg"
                familyMember.phone = "18156094171"
                familyMember.sex = 1
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                familyMember = FamilyMemberEntity("王明")
                familyMember.imagePath = "222.jpg"
                familyMember.phone = "18156096666"
                familyMember.fatherId = 1 // 父亲id为1，表示其父亲未第1条插入的数据，也就是上面的王根
                familyMember.sex = 1
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                familyMember = FamilyMemberEntity("王芸")
                familyMember.imagePath = "222.jpg"
                familyMember.phone = "18156096666"
                familyMember.fatherId = 1 // 父亲id为1，表示其父亲未第1条插入的数据，也就是上面的王根
                familyMember.sex = 0
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                familyMember = FamilyMemberEntity("王恩")
                familyMember.imagePath = "222.jpg"
                familyMember.phone = "18156096666"
                familyMember.fatherId = 2 // 父亲id为2，表示其父亲未第2条插入的数据，也就是上面的王明
                familyMember.sex = 0
                FamilyDataBaseHelper.getInstance(this).insertMember(familyMember)

                firstIn = "0"
            }

            var familyMember = FamilyDataBaseHelper.getInstance(this).getFamilyMember(1)

            if (familyMember != null) {
//                var familyMemberModel = familyMember.generateMember(this, 0)
//                var adapter = com.hcs.familytree.FamilyTreeAdapter()
//                adapter.dealWithData(familyMemberModel)
                var personEntity = familyMember.genFamilyMember(this, 0)
                it.onNext(personEntity)
            } else {
                it.onError(Throwable("空的"))
            }
            it.onComplete()

        }, BackpressureStrategy.ERROR)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                familyTree.familyTreeAdapter.addData(it)
                familyTree.refreshUI()
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK) {
            initData()
        }
    }
}
