package com.ivancic.explorengv3.activities;



import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import com.example.x.circlelayout.CircleLayout;
import com.example.x.circlelayout.CircleLayoutAdapter;
import com.ivancic.explorengv3.R;

import CircleLayout.*;


public class Mainou extends Activity implements SeekBar.OnSeekBarChangeListener{
    int progressChanged = 0;
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        progressChanged = i;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        /*if(seekBar.getId()==R.id.skRad) {
            circularLayout.setRadius(progressChanged);
            circularLayout.init();
        }
        if(seekBar.getId()==R.id.skChildCount) {
            circularLayout.setChildrenCount(progressChanged);
            circularLayout.init();
            circularLayout.balanceRotate();
        }
        if(seekBar.getId()==R.id.skOffsetX) {
            circularLayout.setOffsetX(progressChanged-90);
            circularLayout.init();
        }
        if(seekBar.getId()==R.id.skOffsetY) {
            circularLayout.setOffsetY(progressChanged-60);
            circularLayout.init();
        }*/
    }

    CircleLayout circularLayout ;

    public void tglClick(View v)
    {
        ToggleButton tg=(ToggleButton)findViewById(R.id.tglBtn);
        circularLayout.setChildrenPinned(tg.isChecked());
        circularLayout.init();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainou);


        /*SeekBar skRad = (SeekBar) findViewById(R.id.skRad);
        skRad.setOnSeekBarChangeListener(this);

        SeekBar skOffsetX = (SeekBar) findViewById(R.id.skOffsetX);
        skOffsetX.setOnSeekBarChangeListener(this);

        SeekBar skOffsetY = (SeekBar) findViewById(R.id.skOffsetY);
        skOffsetY.setOnSeekBarChangeListener(this);

        SeekBar skChildCount = (SeekBar) findViewById(R.id.skChildCount);
        skChildCount.setOnSeekBarChangeListener(this);*/



        circularLayout =(CircleLayout) findViewById(R.id.circularLayout);

        MyCircleLayoutAdapter ad=new MyCircleLayoutAdapter();
        ad.add(R.drawable.quiz);
        ad.add(R.drawable.edit_profile);
        ad.add(R.drawable.leaderboard);
        ad.add(R.drawable.gallery);
        ad.add(R.drawable.reward);
        ad.add(R.drawable.about_app);
        ad.add(R.drawable.about_project);

        circularLayout.setAdapter(ad);
        circularLayout.setChildrenCount(7);
        circularLayout.setRadius(120);
        //circularLayout.setChildrenPinned(true);


    }


}
