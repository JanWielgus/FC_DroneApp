package com.example.fcdroneapp;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import android.os.Handler;
import android.widget.Toast;

import java.util.logging.LogRecord;

public class PilotCommunication
{
    private final static String TAG = PilotCommunication.class.getSimpleName();

    private Handler mHandler = new Handler();

    static final int sendingPeriod = 500; // In ms, time between each sending


    private BluetoothSocket btSocket = null;

    // communication values


/*
    public PilotCommunication()
    {

    }*/


    public void setBluetoothSocket(BluetoothSocket bts)
    {
        this.btSocket = bts;
    }


    private boolean isBtSocketExist()
    {
        if (btSocket == null)
            return false;
        return true;
    }


    public void enableAutoSending()
    {
        mSendingRunnable.run();
    }


    public void disableAutoSaving()
    {
        mHandler.removeCallbacks(mSendingRunnable);
    }


    // Periodically called task
    private Runnable mSendingRunnable = new Runnable() {
        @Override
        public void run()
        {
            // send

            // remember to check result of isBtSocketExist() method


            // at the end (plan next call)
            mHandler.postDelayed(this, sendingPeriod);
        }
    };


    // getters and setters
}
