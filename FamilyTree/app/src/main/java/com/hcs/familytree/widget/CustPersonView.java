package com.hcs.familytree.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hcs.familytree.R;
import com.hcs.familytree.model.FamilyMemberModel;


/**
 * Created by hcs on 2017/4/20.
 * 人节点view
 */

public class CustPersonView extends CombinedBaseView {

    public FamilyMemberModel familyMemberModel;

    public String parentId;

    public FamilyMemberModel getFamilyMemberModel() {
        return familyMemberModel;
    }

    public void setFamilyMemberModel(FamilyMemberModel familyMemberModel) {
        this.familyMemberModel = familyMemberModel;

        setTitle(familyMemberModel.getMemberEntity().getName());
        setImage(familyMemberModel.getMemberEntity().getSex(), familyMemberModel.getMemberEntity().getImagePath());

    }

    public CustPersonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustPersonView(Context context) {
        super(context);
    }

    @Override
    protected int layoutResource() {
        return R.layout.layout_person_view;
    }

    @Override
    protected void onCreate(Context context) {

    }

    public static CustPersonView getPersonView(Context context) {
        return new CustPersonView(context);
    }

    public void setTitle(String title) {
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(title);
    }

    public void setTitleColor(int color) {
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setTextColor(ContextCompat.getColor(getContext(), color));
    }

    public void setImage(int gender, String image) {
        ImageView ivHead = findViewById(R.id.ivHead);
        if (TextUtils.isEmpty(image)) {
            if (1 == gender) {
                ivHead.setImageResource(R.drawable.img_head_default_man);
            } else {
                ivHead.setImageResource(R.drawable.img_head_default_woman);
            }
        } else {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.override(100, 100).centerCrop();
            if (1 == gender) {
                requestOptions.placeholder(R.drawable.img_head_default_man);
            } else {
                requestOptions.placeholder(R.drawable.img_head_default_woman);
            }
            Glide.with(this)
                    .load(image)
                    .apply(requestOptions)
                    .into(ivHead);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("hcs","id:"+familyMemberModel.getMemberEntity().getId());
    }
}
