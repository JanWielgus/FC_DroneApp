package com.example.fcdroneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class PID_TuningActivity extends AppCompatActivity {
    private final static String TAG = PID_TuningActivity.class.getSimpleName();



    // config values
    private static final int sliderMax = 400;
    private static float decimalStepValue = 0.2f;
    private static int integerStepValue = 20;
    private static float pMaxValue = 8f;
    private static float iMaxValue = 20.0f;
    private static int iMaxMaxValue = 200;
    private static float dMaxValue = 0.25f;





    Spinner controllerToTuneSpinner;
    EditText pValueEditText, iValueEditText, iMaxValueEditText, dValueEditText;
    SeekBar pPidSeekBar, iPidSeekBar, iMaxPidSeekBar, dPidSeekBar;
    CheckBox pSetZeroCheckBox, iSetZeroCheckBox, iMaxSetZeroCheckBox, dSetZeroCheckBox;
    Button pMinusButton, iMinusButton, iMaxMinusButton, dMinusButton;
    Button pPlusButton, iPlusButton, iMaxPlusButton, dPlusButton;
    EditText decimalStepEditText, integerStepEditText;
    Switch autoSendingSwitch;

    ProgressBar connectingProgressBar;

    // bluetooth variables
    String address = null;
    //private ProgressDialog progress; // depreciated, use progress bar instead
    BluetoothAdapter bluetoothAdapter = null;
    BluetoothSocket bluetoothSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // MAYBE USE THERE RANDOM UUID !!!!
    //static final UUID myUUID = UUID.randomUUID();


    PilotCommunication pilcom = new PilotCommunication();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // receive the address of the bluetooth device
        Intent newIntent = getIntent();
        address = newIntent.getStringExtra("EXTRA_ADDRESS");

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


        // value showing boxes
        pValueEditText = (EditText) findViewById(R.id.pValueEditText);
        iValueEditText = (EditText) findViewById(R.id.iValueEditText);
        iMaxValueEditText = (EditText) findViewById(R.id.iMaxValueEditText);
        dValueEditText = (EditText) findViewById(R.id.dValueEditText);


        // seek bars
        pPidSeekBar = (SeekBar) findViewById(R.id.p_pidSeekBar);
        iPidSeekBar = (SeekBar) findViewById(R.id.i_pidSeekBar);
        iMaxPidSeekBar = (SeekBar) findViewById(R.id.iMax_pidSeekBar);
        dPidSeekBar = (SeekBar) findViewById(R.id.d_pidSeekBar);


        // zero value check boxes
        pSetZeroCheckBox = findViewById(R.id.pSetZeroCheckBox);
        iSetZeroCheckBox = findViewById(R.id.iSetZeroCheckBox);
        iMaxSetZeroCheckBox = findViewById(R.id.iMaxSetZeroCheckBox);
        dSetZeroCheckBox = findViewById(R.id.dSetZeroCheckBox);


        // plus and minus buttons
        pMinusButton = findViewById(R.id.pMinusButton);
        iMinusButton = findViewById(R.id.iMinusButton);
        iMaxMinusButton = findViewById(R.id.iMaxMinusButton);
        dMinusButton = findViewById(R.id.dMinusButton);
        pPlusButton = findViewById(R.id.pPlusButton);
        iPlusButton = findViewById(R.id.iPlusButton);
        iMaxPlusButton = findViewById(R.id.iMaxPlusButton);
        dPlusButton = findViewById(R.id.dPlusButton);


        // decimal and integer edit texts
        decimalStepEditText = findViewById(R.id.decimalStepEditText);
        integerStepEditText = findViewById(R.id.integerStepEditText);

        autoSendingSwitch = findViewById(R.id.autoSendingSwitch);


        connectingProgressBar = (ProgressBar) findViewById(R.id.connectingProgressBar);

        // fill spinner with values
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(PID_TuningActivity.this, R.array.pid_controllers_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        controllerToTuneSpinner.setAdapter(adapter);


        // Set showed values to the default
        pValueEditText.setText((Float.toString(0.0f)));
        iValueEditText.setText((Float.toString(0.0f)));
        iMaxValueEditText.setText(Integer.toString(0));
        dValueEditText.setText((Float.toString(0.0f)));
        decimalStepEditText.setText(Float.toString(decimalStepValue));
        integerStepEditText.setText(Integer.toString(integerStepValue));
    }







    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Set up listeners method
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




    private void setUpListeners()
    {
        // set spinner listener
        controllerToTuneSpinner.setOnItemSelectedListener(onControllerToTuneSpinnerItemSelected); // This listener is made in a different way



        // P seek bar listener
        pPidSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float value = map(i, 0, sliderMax, 0f, pMaxValue);
                value = setFloatPrecision(value, 2);
                pValueEditText.setText(Float.toString(value));
                pilcom.updateP(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });




        // I seek bar listener
        iPidSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float value = map(i, 0, sliderMax, 0f, iMaxValue);
                value = setFloatPrecision(value, 2);
                iValueEditText.setText(Float.toString(value));
                pilcom.updateI(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });




        // I_Max seek bar listener
        iMaxPidSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int value = map(i, 0, sliderMax, 0, iMaxMaxValue);
                iMaxValueEditText.setText(Integer.toString(value));
                pilcom.updateImax(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });




        // D seek bar listener
        dPidSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float value = map(i, 0, sliderMax, 0f, dMaxValue);
                value = setFloatPrecision(value, 2);
                dValueEditText.setText(Float.toString(value));
                pilcom.updateD(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });




        // P value editText
        pValueEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });




        // I value editText
        iValueEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });




        // I_Max value exitText
        iMaxValueEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });




        // D value exitText
        dValueEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });




        // decimal step editText
        decimalStepEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i > 0)
                    decimalStepValue = Float.parseFloat(charSequence.toString());
                else
                    decimalStepValue = 0f;
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });




        // integer step editText
        integerStepEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i > 0)
                    integerStepValue = Integer.parseInt(charSequence.toString());
                else
                    integerStepValue = 0;
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });





        autoSendingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    pilcom.enableAutoSending();
                else
                    pilcom.disableAutoSaving();
            }
        });





    }






    // spinner on item selected method
    private AdapterView.OnItemSelectedListener onControllerToTuneSpinnerItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
        {
            //Toast.makeText(PID_TuningActivity.this, Integer.toString(i), Toast.LENGTH_SHORT).show();


            pilcom.updateControllerID(i);
                /*
                    0 - leveling
                    1 - yaw
                    ...
                 */

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };






    public void plusMinusButtonsOnClicks(View view)
    {
        float fromValue;

        switch (view.getId())
        {
            case R.id.pMinusButton:
                fromValue = Float.parseFloat(pValueEditText.getText().toString()) - decimalStepValue;
                pPidSeekBar.setProgress(map(fromValue, 0f, pMaxValue, 0, sliderMax));
                break;
            case R.id.pPlusButton:
                fromValue = Float.parseFloat(pValueEditText.getText().toString()) + decimalStepValue;
                pPidSeekBar.setProgress(map(fromValue, 0f, pMaxValue, 0, sliderMax));
                break;

            case R.id.iMinusButton:
                fromValue = Float.parseFloat(iValueEditText.getText().toString()) - decimalStepValue;
                iPidSeekBar.setProgress(map(fromValue, 0f, iMaxValue, 0, sliderMax));
                break;
            case R.id.iPlusButton:
                fromValue = Float.parseFloat(iValueEditText.getText().toString()) + decimalStepValue;
                iPidSeekBar.setProgress(map(fromValue, 0f, iMaxValue, 0, sliderMax));
                break;

            case R.id.iMaxMinusButton:
                fromValue = Float.parseFloat(iMaxValueEditText.getText().toString()) - integerStepValue;
                iMaxPidSeekBar.setProgress(map(fromValue, 0f, (float)iMaxMaxValue, 0, sliderMax));
                break;
            case R.id.iMaxPlusButton:
                fromValue = Float.parseFloat(iMaxValueEditText.getText().toString()) + integerStepValue;
                iMaxPidSeekBar.setProgress(map(fromValue, 0f, (float)iMaxMaxValue, 0, sliderMax));
                break;

            case R.id.dMinusButton:
                fromValue = Float.parseFloat(dValueEditText.getText().toString()) - decimalStepValue;
                dPidSeekBar.setProgress(map(fromValue, 0f, dMaxValue, 0, sliderMax));
                break;
            case R.id.dPlusButton:
                fromValue = Float.parseFloat(dValueEditText.getText().toString()) + decimalStepValue;
                dPidSeekBar.setProgress(map(fromValue, 0f, dMaxValue, 0, sliderMax));
                break;
        }



    }












    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //      Other methods
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////











    // Map the value x from range of in_min:in_max to range out_min:out+max
    private float map (int x, int in_min, int in_max, float out_min, float out_max)
    {
        return (float)(x - in_min) * (out_max - out_min) / (float)(in_max - in_min) + out_min;
    }

    private int map (float x, float in_min, float in_max, int out_min, int out_max)
    {
        return (int)(  (x - in_min) * (float)(out_max - out_min) / (in_max - in_min) + out_min + 0.5  );
    }


    private int map (int x, int in_min, int in_max, int out_min, int out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }


    private float setFloatPrecision(float value, int decimalPlaces)
    {
        if (decimalPlaces<0)
            return value;
        if (value == 0)
            return (int)value;

        String sValue = String.format("%." + decimalPlaces + "f", value);
        return Float.parseFloat(sValue);
    }



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

                // set the bluetooth socket in the communication class object
                pilcom.setBluetoothSocket(bluetoothSocket);
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
