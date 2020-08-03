package com.hcs.familytree.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hcs.familytree.R;


/**
 * Created by wg on 2017/4/20.
 * 人节点view
 */

public class PersonView extends CombinedBaseView {

    PersonEntity personEntity;

    public PersonEntity getPersonEntity() {
        return personEntity;
    }

    public void setPersonEntity(PersonEntity personEntity) {
        this.personEntity = personEntity;
    }

    public PersonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PersonView(Context context) {
        super(context);
    }

    @Override
    protected int layoutResource() {
        return R.layout.layout_person_view;
    }

    @Override
    protected void onCreate(Context context) {
        this.setOnClickListener(personEntity.clickListener);
    }

    public static PersonView getPersonView(Context context) {
        return new PersonView(context);
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
            int headDefaultRes = 1 == gender ? R.drawable.img_head_default_man : R.drawable.img_head_default_woman;
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(headDefaultRes)
                    .error(headDefaultRes)
                    .override(100, 100)
                    .centerCrop();
            Glide.with(this)
                    .load(image)
                    .apply(requestOptions)
                    .into(ivHead);
        }
    }
}
