package com.hhx7.im.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.Toast;

import net.vidageek.mirror.dsl.Mirror;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

/*
 * Created by troy379 on 04.04.17.
 */
public class AppUtils {

    public static void showToast(Context context, @StringRes int text, boolean isLong) {
        showToast(context, context.getString(text), isLong);
    }

    public static void showToast(Context context, String text, boolean isLong) {
        Toast.makeText(context, text, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public static String getRandomId() {
        return Long.toString(UUID.randomUUID().getLeastSignificantBits());
    }

    public static String getBtAddressMac() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        try {

            Field mServiceField = bluetoothAdapter.getClass().getDeclaredField("mService");
            mServiceField.setAccessible(true);

            Object btManagerService = mServiceField.get(bluetoothAdapter);
            return (String) btManagerService.getClass().getMethod("getAddress").invoke(btManagerService);
        } catch (NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Log.e("ConnectionManagerTest: ", "getBluetoothMacAddress: Failed to get Bluetooth address " + e.getMessage(), e);
        }
        return null;
    }
}