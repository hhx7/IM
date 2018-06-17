package com.hhx7.im;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;


import com.hhx7.im.features.main.adapter.MainActivityPagerAdapter;
import com.stfalcon.chatkit.sample.R;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;

/*
 * Created by troy379 on 04.04.17.
 */
public class MainActivity extends AppCompatActivity {


    private BluetoothAdapter mBluetoothAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setContentView(R.layout.activity_main);

        ViewPager pager = (ViewPager) findViewById(R.id.pager);



        pager.setAdapter(new MainActivityPagerAdapter((App)getApplication(),getSupportFragmentManager()));



        PageNavigationView tab = (PageNavigationView) findViewById(R.id.tab);

        NavigationController navigationController = tab.material()
                .addItem(android.R.drawable.ic_menu_info_details, "Message")
                .addItem(android.R.drawable.ic_menu_compass, "Scan")
                .addItem(android.R.drawable.ic_menu_search, "Resource")
                .addItem(android.R.drawable.ic_menu_help, "User")
                .build();

        navigationController.setupWithViewPager(pager);
    }

    @Override
    public void onResume(){
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 0);
        }else{
            //start service
            Intent startServer=new Intent(this,Node.class);
            startService(startServer);
        }
    }





}
