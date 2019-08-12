package com.example.fcdroneapp;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import android.os.Handler;
import android.widget.Toast;

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

    // controller 1
    int controller1ID; // which PID controller is tuned as controller 1
    int pidP1, pidI1, pidD1; // pid values multiplied by 100
    int pidImax1;
    // controller 2
    int controller2ID; // which PID controller is tuned as controller 2
    int pidP2, pidI2, pidD2; // pid values multiplied by 100
    int pidImax2;
    boolean needToSendValues = false;

    int currentController = 1; // current controller (controller 1 or 2) is something different to controller ID   !!!!!!!!!!!!!!!!!!!!!!


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
            byte[] dataToSend = new byte[21];
            byte[] temp = new byte[4];


            // first byte is packer ID
            dataToSend[0] = (byte)123;




            if (currentController == 1)
            {
                // controller ID
                temp = intToByteArray(controller1ID);
                for (int i=0; i<4; i++)
                    dataToSend[i+1] = temp[i];

                // pid P
                temp = intToByteArray(pidP1);
                for (int i=0; i<4; i++)
                    dataToSend[i+5] = temp[i];

                // pid I
                temp = intToByteArray(pidI1);
                for (int i=0; i<4; i++)
                    dataToSend[i+9] = temp[i];

                // pid I max
                temp = intToByteArray(pidImax1);
                for (int i=0; i<4; i++)
                    dataToSend[i+13] = temp[i];

                // pid D
                temp = intToByteArray(pidD1);
                for (int i=0; i<4; i++)
                    dataToSend[i+17] = temp[i];
            }
            else if (currentController == 2)
            {
                // controller ID
                temp = intToByteArray(controller2ID);
                for (int i=0; i<4; i++)
                    dataToSend[i+1] = temp[i];

                // pid P
                temp = intToByteArray(pidP2);
                for (int i=0; i<4; i++)
                    dataToSend[i+5] = temp[i];

                // pid I
                temp = intToByteArray(pidI2);
                for (int i=0; i<4; i++)
                    dataToSend[i+9] = temp[i];

                // pid I max
                temp = intToByteArray(pidImax2);
                for (int i=0; i<4; i++)
                    dataToSend[i+13] = temp[i];

                // pid D
                temp = intToByteArray(pidD2);
                for (int i=0; i<4; i++)
                    dataToSend[i+17] = temp[i];
            }



            // send data if bluetooth socket exists AND IF NEED TO SEND DATA
            // remember to check result of isBtSocketExist() method
            if (needToSendValues && isBtSocketExist()) {
                try {
                    outputStream.write(dataToSend);
                    needToSendValues = false;
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

    // controller 1

    void changeController1ID(int controllerID) {
        this.controller1ID = controllerID;
    }

    void updateP1(float value) {
        pidP1 = (int)(value * 100);
        needToSendValues = true;
        currentController = 1;
    }

    void updateI1(float value) {
        pidI1 = (int)(value * 100);
        needToSendValues = true;
        currentController = 1;
    }

    void updateImax1(int value) {
        pidImax1 = value;
        needToSendValues = true;
        currentController = 1;
    }

    void updateD1(float value) {
        pidD1 = (int)(value * 100);
        needToSendValues = true;
        currentController = 1;
    }


    // controller 2

    void changeController2ID(int controllerID) {
        this.controller2ID = controllerID;
    }

    void updateP2(float value) {
        pidP2 = (int)(value * 100);
        needToSendValues = true;
        currentController = 2;
    }

    void updateI2(float value) {
        pidI2 = (int)(value * 100);
        needToSendValues = true;
        currentController = 2;
    }

    void updateImax2(int value) {
        pidImax2 = value;
        needToSendValues = true;
        currentController = 2;
    }

    void updateD2(float value) {
        pidD2 = (int)(value * 100);
        needToSendValues = true;
        currentController = 2;
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
