package com.example.fcdroneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class PID_TuningActivity extends AppCompatActivity {
    private final static String TAG = PID_TuningActivity.class.getSimpleName();

    Spinner controllerToTuneSpinner;
    EditText pValueEditText, iValueEditText, iMaxValueEditText, dValueEditText;
    SeekBar pPidSeekBar, iPidSeekBar, iMaxPidSeekBar, dPidSeekBar;

    ProgressBar connectingProgressBar;

    // bluetooth variables
    String address = null;
    //private ProgressDialog progress; // depreciated, use progress bar instead
    BluetoothAdapter bluetoothAdapter = null;
    BluetoothSocket bluetoothSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // MAYBE USE THERE RANDOM UUID !!!!
    //static final UUID myUUID = UUID.randomUUID();

    // seekBar's values


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // receive the address of the bluetooth device
        Intent newIntent = getIntent();
        address = newIntent.getStringExtra("EXTRA_ADDRESS");
        Log.d(TAG, " aaaaaaaaaaaa-----        sdfasdfasdfasdffffffffffffffffffffffffff   " + address);

        // View the layout
        setContentView(R.layout.activity_pid__tuning);

        // init the activity components
        initComponents();


        // Call the class to connect
        new ConnectBT().execute();

        setUpListeners();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        try
        {
            bluetoothSocket.close();
        }
        catch (IOException e)
        {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }

    private void initComponents()
    {
        controllerToTuneSpinner = (Spinner) findViewById(R.id.controllerToTuneSpinner);

        pValueEditText = (EditText) findViewById(R.id.pValueEditText);
        iValueEditText = (EditText) findViewById(R.id.iValueEditText);
        iMaxValueEditText = (EditText) findViewById(R.id.iMaxValueEditText);
        dValueEditText = (EditText) findViewById(R.id.dValueEditText);

        pPidSeekBar = (SeekBar) findViewById(R.id.p_pidSeekBar);
        iPidSeekBar = (SeekBar) findViewById(R.id.i_pidSeekBar);
        iMaxPidSeekBar = (SeekBar) findViewById(R.id.iMax_pidSeekBar);
        dPidSeekBar = (SeekBar) findViewById(R.id.d_pidSeekBar);

        connectingProgressBar = (ProgressBar) findViewById(R.id.connectingProgressBar);

        // fill spinner with values
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(PID_TuningActivity.this, R.array.pid_controllers_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        controllerToTuneSpinner.setAdapter(adapter);
    }


    private void setUpListeners()
    {
        // set spinner listener
        controllerToTuneSpinner.setOnItemSelectedListener(onControllerToTuneSpinnerItemSelected);

        // P seek bar listener
        pPidSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                pValueEditText.setText(Integer.toString(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                Toast.makeText(PID_TuningActivity.this, Integer.toString(seekBar.getProgress()), Toast.LENGTH_SHORT).show();
            }
        });



    }


    // spinner on item selected method
    private AdapterView.OnItemSelectedListener onControllerToTuneSpinnerItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
        {
            //Toast.makeText(PID_TuningActivity.this, Integer.toString(i), Toast.LENGTH_SHORT).show();



            ////////////////////////////////////////////////////////

            // EDIT ON ITEM SELECTED METHOD HERE !!!!!!!!!!

            // /////////////////////////////////////////////////////

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };




    private class ConnectBT extends AsyncTask<Void, Void, Void>
    {
        private boolean ConnectSuccess = true;


        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            connectingProgressBar.setVisibility(View.VISIBLE);
        }


        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            if (!ConnectSuccess)
            {
                Toast.makeText(PID_TuningActivity.this, "Connection Failed.", Toast.LENGTH_SHORT).show();
                finish();
            }
            else
            {
                Toast.makeText(PID_TuningActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                isBtConnected = true;
            }

            connectingProgressBar.setVisibility(View.INVISIBLE);
        }


        @Override
        protected Void doInBackground(Void... voids)
        {
            BluetoothSocket temp = null;

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);

            try
            {
                temp = bluetoothDevice.createRfcommSocketToServiceRecord(myUUID);
            }
            catch (IOException e)
            {
                Log.e(TAG, "Socket's create() method failed", e);
            }

            bluetoothSocket = temp;

            bluetoothAdapter.cancelDiscovery();

            try
            {
                bluetoothSocket.connect();
            }
            catch (IOException e)
            {
                Log.e(TAG, "Socket's connect method failed", e);
                ConnectSuccess = false; // If the try failed you can check the exception here

                try
                {
                    bluetoothSocket.close();
                }
                catch (IOException closeException)
                {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
            }

            return null;
        }
    }

}
