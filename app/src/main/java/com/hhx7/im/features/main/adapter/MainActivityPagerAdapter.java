package com.hhx7.im.features.main.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.hhx7.im.App;
import com.hhx7.im.features.main.CustomDialogsFragment;
import com.hhx7.im.features.main.ScanFragment;

/*
 * Created by troy379 on 11.04.17.
 */
public class MainActivityPagerAdapter extends FragmentStatePagerAdapter {

    public static final int MESSAGE_BOX = 0;
    public static final int SCAN = 1;
    public static final int ID_CUSTOM_LAYOUT = 2;
    public static final int ID_CUSTOM_VIEW_HOLDER = 3;
    public static final int ID_CUSTOM_CONTENT = 4;



    App app;
    public MainActivityPagerAdapter(App app,FragmentManager fm) {
        super(fm);

        this.app=app;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case MESSAGE_BOX:
                return CustomDialogsFragment.newInstance();
            case SCAN:
                return new ScanFragment();
        }
        return CustomDialogsFragment.newInstance();
    }

    @Override
    public int getCount() {
        return 2;
    }
}