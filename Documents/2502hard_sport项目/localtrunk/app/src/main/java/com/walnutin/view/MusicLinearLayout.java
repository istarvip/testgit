package com.walnutin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.walnutin.hard.R;


public class MusicLinearLayout extends LinearLayout implements
        OnClickListener {

    private Context context;
    ImageView playImg;
    ImageView progressImg;
    ImageView playListImg;
    private CircleImageView album;

    public MusicLinearLayout(Context context) {
        super(context);
        this.context = context;
        initView(context);
        initCreateEvent();
    }

    public MusicLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
        initCreateEvent();
    }

    private void initCreateEvent() {


    }

    private void initView(Context context) {
        LinearLayout contener = (LinearLayout) View.inflate(context,
                R.layout.music_bottom, null);
        album = (CircleImageView) contener.findViewById(R.id.album);
        playImg = (ImageView) contener.findViewById(R.id.mucic_play);
        progressImg = (ImageView) contener.findViewById(R.id.music_progress);
        playListImg = (ImageView) contener.findViewById(R.id.mucic_list);
        addView(contener);

    }

    public MusicLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initView(context);
        initCreateEvent();
    }

    int currentState = 0; // 0 为时速 1 为 电量

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mucic_list:
                break;
            case R.id.mucic_play:
            default:
                break;
        }

    }


}
