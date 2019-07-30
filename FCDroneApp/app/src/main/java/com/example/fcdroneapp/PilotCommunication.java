package com.example.fcdroneapp;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PilotCommunication
{
    private final static String TAG = PilotCommunication.class.getSimpleName();

    private Handler mHandler = new Handler();

    static final int sendingPeriod = 500; // In ms, time between each sending


    private BluetoothSocket btSocket = null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    // communication values
    int controllerID; // which PID controller is tuned
    int pidP, pidI, pidD; // pid values multiplied by 100
    int pidImax;
    boolean needToSendValues = false;


/*
    public PilotCommunication()
    {

    }*/


    public void setBluetoothSocket(BluetoothSocket bts)
    {
        this.btSocket = bts;

        try {
            inputStream = btSocket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }

        try {
            outputStream = btSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating output stream", e);
        }
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
            byte[] dataToSend = new byte[15];
            byte[] temp = new byte[4];


            // first byte is packer ID
            dataToSend[0] = (byte)123;


            // controller ID
            temp = intToByteArray(controllerID);
            for (int i=0; i<4; i++)
                dataToSend[i+1] = temp[i];

            // pid P
            temp = intToByteArray(pidP);
            for (int i=0; i<4; i++)
                dataToSend[i+5] = temp[i];

            // pid I
            temp = intToByteArray(pidI);
            for (int i=0; i<4; i++)
                dataToSend[i+9] = temp[i];

            // pid I max
            temp = intToByteArray(pidImax);
            for (int i=0; i<4; i++)
                dataToSend[i+13] = temp[i];

            // pid D
            temp = intToByteArray(pidD);
            for (int i=0; i<4; i++)
                dataToSend[i+17] = temp[i];



            // send data if bluetooth socket exists
            // remember to check result of isBtSocketExist() method
            if (isBtSocketExist()) {
                try {
                    outputStream.write(dataToSend);
                } catch (IOException e) {
                    Log.e(TAG, "Error occurred when sending data", e);
                }
            }



            // at the end (plan next call)
            mHandler.postDelayed(this, sendingPeriod);
        }
    };




    void tempSendData()
    {
        byte[] bytes = new byte[10];
        bytes[0] = 15;
        for (int i=0; i<9; i++)
            bytes[i+1] = (byte)(i*2);

        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);
        }
        Log.e(TAG, "Data was sent");
    }




    // getters and setters (data update methods)
    void udpateControllerID(int controllerID) {
        this.controllerID = controllerID;
        needToSendValues = true;
    }

    void udpateP(float value) {
        pidP = (int)(value * 100);
        needToSendValues = true;
    }

    void updateI(float value) {
        pidI = (int)(value * 100);
        needToSendValues = true;
    }

    void updateImax(int value) {
        pidImax = value;
        needToSendValues = true;
    }

    void updateD(float value) {
        pidD = (int)(value * 100);
        needToSendValues = true;
    }







    // other methods
    private byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >> 24), (byte)(value >> 16), (byte)(value >> 8), (byte)(value) };
    }


    private int byteArrayToInt(byte[] arr) {
        int result = arr[0] << 24 | (arr[1] & 0xFF) << 16 | (arr[2] & 0xFF) << 8 | (arr[3] & 0xFF);
        return result;
    }


}
