package com.hcs.familytree.widget

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.LongSparseArray
import android.view.ViewGroup
import com.hcs.familytree.utils.DensityUtil
import com.hcs.familytree.model.FamilyDataBaseHelper
import com.hcs.familytree.model.FamilyMemberModel
import kotlin.math.max
import android.support.v4.app.ActivityCompat
import android.util.Log
import com.hcs.familytree.ui.InfoActivity
import com.hcs.familytree.R

/**
 * 核心类，负责view的测量，布局，线条的绘制
 * TODO：初步完成基本功能，后期会将对递归算法替换成非递归形式及进行其他优化
 */
class FMLayout : ViewGroup {
    val mItemWidth = DensityUtil.dip2px(context, 60f)
    val mItemSpace = DensityUtil.dip2px(context, 10f)
    val mItemHeight = DensityUtil.dip2px(context, 80f)
    lateinit var mPaint: Paint
    lateinit var mPath: Path

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init()
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPath = Path()
        mPaint.strokeWidth = 5f
        mPaint.style = Paint.Style.STROKE
        mPaint.color = ActivityCompat.getColor(context, R.color.blue)
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (i in 0 until childCount) {
            getChildAt(i).measure(
                MeasureSpec.makeMeasureSpec(mItemWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mItemHeight, MeasureSpec.EXACTLY)
            )
        }
        var width = max(mTotalWidth + 2 * mItemSpace, DensityUtil.getScreenW(context))
        var height = max(mTotalHeight + 2 * mItemSpace, DensityUtil.getScreenH(context))
        setMeasuredDimension(width, height)
    }

    var halfItemSpace = mItemSpace / 2f
    var halfItemWidth = mItemWidth / 2f
    var halfItemHeight = mItemHeight / 2f

    fun moveXDistance(itemNum: Float, spaceNum: Float): Int {
        return (itemNum * mItemWidth + spaceNum * mItemSpace).toInt()
    }

    fun moveYDistance(itemNum: Float, spaceNum: Float): Int {
        return (itemNum * mItemHeight + spaceNum * mItemSpace).toInt()
    }

    var leftStartX = 0
    var topStartY = 0
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (mTopParentModel != null) {
            topStartY = (b / 2f - mTotalHeight / 2f).toInt()
            leftStartX = (r / 2f - (total * mItemWidth + (total - 1) * mItemSpace) / 2f).toInt()
            layoutSelfAndSpouse(mTopParentModel!!, 0, 0, totalNum = total)
        }
    }

    fun layoutSelfAndSpouse(
        model: FamilyMemberModel,
        offsetX: Int,
        lessThanTop: Int = 0,
        totalNum: Int = 1,
        topMost: Boolean = false,
        preStartX: Int = 0,
        equalParentNum: Boolean = false
    ) {//递归
        var startCenterY =
            topStartY + (model.level) * mItemHeight + ((model.level)) * mItemSpace
//        val num = totalMoreNumFromMine(model!!)//又是递归
        val num = totalNum
        Log.e("hcs", "id:" + model.memberEntity.id + " offsetX:" + offsetX)
        var startCenterX = 0
        if (lessThanTop < 0 && topMost) {
            startCenterX =
                leftStartX + (num * mItemWidth + (num - 1) * mItemSpace) / 2 + offsetX +
                        ((halfItemSpace + halfItemWidth).toInt() * Math.abs(
                            lessThanTop
                        ))
        } else if (lessThanTop == 0 && topMost) {
            if (equalParentNum) {//与父亲一样多就与父亲一样对齐
                startCenterX = preStartX
            } else {
                startCenterX =
                    leftStartX + (num * mItemWidth + (num - 1) * mItemSpace) / 2 + offsetX
            }
//            startCenterX = max(startCenterX, preStartX)
        } else {
            startCenterX = leftStartX + (num * mItemWidth + (num - 1) * mItemSpace) / 2 + offsetX
        }
        if (havaSpouse(model)) {
            model!!.toParentX = startCenterX + moveXDistance(0.5f, 0.5f)
            model!!.toParentY = startCenterY
            val index = mSparseArray.get(model.memberEntity.id)

            val personView2 = getChildAt(index) as CustPersonView
            model.startCenterX = startCenterX
            personView2.layout(
                startCenterX + halfItemSpace.toInt(),
                startCenterY,
                startCenterX + halfItemSpace.toInt() + mItemWidth,
                startCenterY + mItemHeight
            )
            personView2.setOnClickListener(memberClick)
            personView2.setTitle(model.memberEntity.id.toString())
            Log.e(
                "hcs",
                "child00:" + index + " id:" + model.memberEntity.id + " left:" + (startCenterX + halfItemSpace) + " top:" + startCenterY + " right:" + (startCenterX + halfItemSpace + mItemWidth) + "bottom:" + (startCenterY + mItemHeight)
            )

            val index1 = mSparseArray.get(model?.memberEntity?.spouseId!!)
            val spaseView = getChildAt(index1) as CustPersonView
            spaseView.familyMemberModel.toParentX = model!!.toParentX - moveXDistance(1f, 1f)
            spaseView.familyMemberModel.toParentY = model!!.toParentY
            val left = startCenterX - halfItemSpace.toInt() - mItemWidth

            Log.e(
                "hcs",
                "child11:" + index1 + " id:" + model!!.memberEntity!!.spouseId + " left:" + left + "top:" + startCenterY + " right:" + (left + mItemWidth) + "bottom:" + (startCenterY + mItemHeight)
            )
            spaseView.setOnClickListener(memberClick)
            spaseView.setTitle(model!!.memberEntity.spouseId.toString())
            spaseView.layout(
                left,
                startCenterY,
                left + mItemWidth,
                startCenterY + mItemHeight
            )
            if (model.childModels != null && model.childModels!!.isNotEmpty()) {
                model.toChildX = model!!.toParentX - halfItemWidth.toInt() - halfItemSpace.toInt()
                model.toChildY = model!!.toParentY + halfItemHeight.toInt()

                spaseView.familyMemberModel.toChildX =
                    spaseView.familyMemberModel.toParentX + halfItemWidth.toInt() + halfItemSpace.toInt()
                spaseView.familyMemberModel.toChildY =
                    spaseView.familyMemberModel.toParentY + halfItemHeight.toInt()

            }
        } else {
            val index = mSparseArray.get(model.memberEntity.id)
            val view2 = getChildAt(index) as CustPersonView
            model.startCenterX = startCenterX
            view2.familyMemberModel.toParentX = startCenterX
            view2.familyMemberModel.toParentY = startCenterY
            view2.layout(
                startCenterX - halfItemWidth.toInt(),
                startCenterY,
                startCenterX + mItemWidth,
                startCenterY + mItemHeight
            )
            view2.setOnClickListener(memberClick)
            view2.setTitle(model.memberEntity.id.toString())
            Log.e(
                "hcs",
                "child22:" + index + " id:" + model.memberEntity.id + " left:" + (startCenterX - halfItemWidth) + " top:" + startCenterY + " right:" + (startCenterX + mItemWidth) + " bottom:" + (startCenterY + mItemHeight)
            )

            if (model.childModels != null && model.childModels!!.isNotEmpty()) {
                model.toChildX = model!!.toParentX
                model.toChildY = model!!.toParentY + mItemHeight
            }
        }

        if (model.childModels != null && model.childModels!!.isNotEmpty()) {
            var offset = offsetX
            var preNum = 1
            val mostTop = getParentsNum(model) == totalNum//只有等于或小于
            var lessThan = 1
            var list: List<Int>? = null
            if (mostTop) {
                list = ArrayList()
                var childTotalNum = 0
                for (i in model.childModels!!.indices) {
                    val totalMoreNumFromMine = totalMoreNumFromMine(model.childModels!![i])
                    list.add(totalMoreNumFromMine)
                    childTotalNum += totalMoreNumFromMine
                }
                lessThan = childTotalNum - totalNum
            }
            for (i in model.childModels!!.indices) {
                val totalMoreNumFromMine =
                    if (list == null) totalMoreNumFromMine(model.childModels!![i]) else list[i]
                if (i > 0) {
                    offset += preNum * (mItemSpace + mItemWidth)
                }
                preNum = totalMoreNumFromMine
                layoutSelfAndSpouse(
                    model.childModels!![i],
                    offset,
                    lessThan,
                    totalMoreNumFromMine,
                    mostTop,
                    model.startCenterX,
                    totalMoreNumFromMine == totalNum
                )
            }
        }


    }

    fun getParentsNum(model: FamilyMemberModel?): Int {
        return if (model?.memberEntity?.spouseId != null) 2 else 1
    }

    private fun onlyOneChild(model: FamilyMemberModel?): Boolean {
        return model?.childModels?.size == 1
    }


    fun havaSpouse(model: FamilyMemberModel?): Boolean {
        return null != model?.memberEntity?.spouseId
    }

    //判断是否是配偶
    var mDialog: Dialog? = null
    private var memberClick = OnClickListener {
        if (mDialog != null && mDialog!!.isShowing) {
            return@OnClickListener
        }
        val id = (it as CustPersonView).familyMemberModel?.memberEntity?.id
        Log.e(
            "hcs",
            "id:" + id
        )
        mDialog = AlertDialog.Builder(context).setItems(
            R.array.dialog_menu
        ) { dialog, which ->
            dialog.dismiss()
            if (which == 3) {

            } else {
                val intent = Intent(context, InfoActivity::class.java)
                when (which) {
                    0 -> {
                        intent.putExtra(
                            "fatherId",
                            id
                        )

                    }
                    1 -> {
                        intent.putExtra(
                            "spouseId",
                            id
                        )
                    }
                }
                (context as Activity).startActivityForResult(intent, 111)
            }
        }.create()
        mDialog?.show()
    }

    var mLevel: Int = 1
    var mTotalHeight = 0
    var mTotalWidth = 0
    var mTopParentModel: FamilyMemberModel? = null
    var mSparseArray = LongSparseArray<Int>()
    var mModels = ArrayList<FamilyMemberModel>()
    var index = 0
    var total = 1
    fun displayUI(model0: FamilyMemberModel) {
        if (model0 == null) {
            return
        }
        removeAllViews()
        mModels.clear()
        mLevel = 1
        toList(model0)
        mTopParentModel = model0
        mTopParentModel?.flag = FamilyMemberModel.MOST_TOP_FLAG
        total = totalMoreNumFromMine(model0)
        mTotalWidth = total * mItemWidth + (total - 1) * mItemSpace
        mSparseArray.clear()
        index = 0
        for (i in 0 until mModels!!.size) {
            val model = mModels[i]
            val personView2 = CustPersonView(context)
            personView2.familyMemberModel = model
            Log.e("hcs", "id:" + mModels[i].memberEntity.id + " index:" + index)
            mSparseArray.put(mModels[i].memberEntity.id, index)//每个id对应的index
            personView2.parentId = model.memberEntity.fatherId.toString()
            mLevel = if (model.level > mLevel) model.level else mLevel

            addView(personView2)
            if (personView2.familyMemberModel.memberEntity.spouseId != null) {//增加配偶
                val spousePersonView2 = CustPersonView(context)
                index += 1
                Log.e(
                    "hcs",
                    "id:" + personView2.familyMemberModel.memberEntity.spouseId + " index:" + index
                )
                mSparseArray.put(
                    personView2.familyMemberModel.memberEntity.spouseId!!,
                    index
                )//每个id对应的index
                addView(spousePersonView2)
                Thread(
                    Runnable {
                        var familyMember = FamilyDataBaseHelper.getInstance(context)
                            .getFamilyMember(personView2.familyMemberModel.memberEntity.spouseId!!)
                        spousePersonView2.familyMemberModel =
                            FamilyMemberModel(familyMember)
                    }
                ).start()
                Thread.sleep(50)
            }
            index += 1

        }
        if (mLevel > 1) {
            mLevel += 1//model level从零开始
        }
        mTotalHeight = mLevel * mItemHeight + (mLevel - 1) * mItemSpace
        requestLayout()
    }

    fun toList(model0: FamilyMemberModel) {
        if (model0 != null) {
            mModels.add(model0)
        }
        if (model0.childModels != null && model0.childModels!!.isNotEmpty()) {
            for (i in model0.childModels!!.indices) {
                toList(model0.childModels!![i])
            }
        }
    }

    fun findMostLeft(model: FamilyMemberModel): FamilyMemberModel {
        if (model.childModels != null && model.childModels!!.isNotEmpty()) {
            findMostLeft(model.childModels!![0])
        }
        return model
    }

    //核心代码,除了自己和配偶外下一代比自己多出多少
    fun nextGen(model: FamilyMemberModel, num0: Int = 1, maxNum: Int = 1): Int {
        //父代
        var parentLevelNum = if (model.memberEntity.spouseId != null) 2 else 1
        var maxN = max(parentLevelNum, maxNum)
        //孩子一代
        var childLevelNum = 0
        if (model.childModels != null && model.childModels!!.size == 1) {//只有一个孩子，不分叉
            childLevelNum = if (model.childModels!![0].memberEntity.spouseId != null) 2 else 1
            maxN = max(maxN, childLevelNum)
            return nextGen(model.childModels!![0], maxNum = maxN)
        } else if (model.childModels != null && model.childModels!!.size > 1) {//多子的情况
            var num = 0
            for (i in model.childModels!!.indices) {
                num += nextGen(model.childModels!![i])
            }
            return max(num, maxN)
        }
        return maxN

    }

    //连自己一起共多少
    fun totalMoreNumFromMine(model: FamilyMemberModel): Int {
        var total = nextGen(model)
        Log.e("hcs", "id:" + model.memberEntity.id + " children Count:" + total)
        return total
    }


//    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
//        return MarginLayoutParams(context, attrs)
//    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mPath.reset()
        if (mTopParentModel != null) {
            drawPath(mTopParentModel)
        }
        canvas!!.drawPath(mPath, mPaint)

    }

    private fun drawPath(model: FamilyMemberModel?) {
        drawSpouseLine(model)
        drawChildrenLine(model)
        drawBroLine(model)
        drawParentLine(model)
    }

    private fun drawSpouseLine(model: FamilyMemberModel?) {
        if (model != null && model!!.memberEntity.spouseId != null) {
            val x = getChildAt(mSparseArray.get(model.memberEntity.id)).left
            val y = getChildAt(mSparseArray.get(model.memberEntity.id)).top + halfItemHeight
            mPath.moveTo(x.toFloat(), y)
            mPath.lineTo((x - mItemSpace).toFloat(), y)
        }

    }

    private fun drawChildrenLine(model: FamilyMemberModel?) {
        if (model!!.childModels != null && model.childModels!!.isNotEmpty()) {
            if (model?.memberEntity?.spouseId != null) {//有配偶的情况
                mPath.moveTo(model!!.toChildX.toFloat(), model!!.toChildY.toFloat())
                mPath.lineTo(
                    model!!.toChildX.toFloat(),
                    model!!.toChildY.toFloat() + halfItemSpace + halfItemHeight
                )
            } else {
                mPath.moveTo(model!!.toChildX.toFloat(), model!!.toChildY.toFloat())
                mPath.lineTo(
                    model!!.toChildX.toFloat(),
                    model!!.toChildY.toFloat() + halfItemSpace
                )
            }

        }


    }

    private fun drawBroLine(model: FamilyMemberModel?) {
        if (model!!.childModels != null && model.childModels!!.isNotEmpty()) {
            val size = model!!.childModels!!.size
            if (size > 1) {
                mPath.moveTo(
                    model!!.childModels!![0].toParentX.toFloat(),
                    model!!.childModels!![0].toParentY.toFloat() - halfItemSpace
                )
                mPath.lineTo(
                    model!!.childModels!![size - 1].toParentX.toFloat(),
                    model!!.childModels!![size - 1].toParentY.toFloat() - halfItemSpace
                )
            } else if (size == 1 && model!!.childModels!![0].memberEntity?.spouseId != null) {//单个有配偶

                mPath.moveTo(
                    model!!.childModels!![0].toParentX.toFloat(),
                    model!!.childModels!![0].toParentY.toFloat() - halfItemSpace
                )
                mPath.lineTo(
                    model.toChildX.toFloat(),
                    model!!.childModels!![0].toParentY.toFloat() - halfItemSpace
                )

            }
        }
    }

    private fun drawParentLine(model: FamilyMemberModel?) {
        if (model!!.childModels != null && model.childModels!!.isNotEmpty()) {
            for (i in model!!.childModels!!.indices) {
                mPath.moveTo(
                    model!!.childModels!![i].toParentX.toFloat(),
                    model!!.childModels!![i].toParentY.toFloat()
                )
                mPath.lineTo(
                    model!!.childModels!![i].toParentX.toFloat(),
                    model!!.childModels!![i].toParentY.toFloat() - halfItemSpace
                )
                drawPath(model!!.childModels!![i])
            }
        } else if (model?.flag != FamilyMemberModel.MOST_TOP_FLAG) {//最后的一代
            mPath.moveTo(
                model?.toParentX.toFloat(),
                model?.toParentY.toFloat()
            )
            mPath.lineTo(
                model?.toParentX.toFloat(),
                model?.toParentY.toFloat() - halfItemSpace
            )
        }
    }
}