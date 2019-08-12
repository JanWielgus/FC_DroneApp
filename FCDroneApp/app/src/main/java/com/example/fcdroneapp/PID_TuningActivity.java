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





    // First PID window
    Spinner controllerToTuneSpinner1;
    EditText pValueEditText1, iValueEditText1, iMaxValueEditText1, dValueEditText1;
    SeekBar pPidSeekBar1, iPidSeekBar1, iMaxPidSeekBar1, dPidSeekBar1;
    CheckBox pSetZeroCheckBox1, iSetZeroCheckBox1, iMaxSetZeroCheckBox1, dSetZeroCheckBox1;
    Button pMinusButton1, iMinusButton1, iMaxMinusButton1, dMinusButton1;
    Button pPlusButton1, iPlusButton1, iMaxPlusButton1, dPlusButton1;


    // Second PID window

    Spinner controllerToTuneSpinner2;
    EditText pValueEditText2, iValueEditText2, iMaxValueEditText2, dValueEditText2;
    SeekBar pPidSeekBar2, iPidSeekBar2, iMaxPidSeekBar2, dPidSeekBar2;
    CheckBox pSetZeroCheckBox2, iSetZeroCheckBox2, iMaxSetZeroCheckBox2, dSetZeroCheckBox2;
    Button pMinusButton2, iMinusButton2, iMaxMinusButton2, dMinusButton2;
    Button pPlusButton2, iPlusButton2, iMaxPlusButton2, dPlusButton2;


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
        // FIRST PID WINDOW
            controllerToTuneSpinner1 = (Spinner) findViewById(R.id.controllerToTuneSpinner1);

            // value showing boxes
            pValueEditText1 = (EditText) findViewById(R.id.pValueEditText1);
            iValueEditText1 = (EditText) findViewById(R.id.iValueEditText1);
            iMaxValueEditText1 = (EditText) findViewById(R.id.iMaxValueEditText1);
            dValueEditText1 = (EditText) findViewById(R.id.dValueEditText1);


            // seek bars
            pPidSeekBar1 = (SeekBar) findViewById(R.id.p_pidSeekBar1);
            iPidSeekBar1 = (SeekBar) findViewById(R.id.i_pidSeekBar1);
            iMaxPidSeekBar1 = (SeekBar) findViewById(R.id.iMax_pidSeekBar1);
            dPidSeekBar1 = (SeekBar) findViewById(R.id.d_pidSeekBar1);


            // zero value check boxes
            pSetZeroCheckBox1 = findViewById(R.id.pSetZeroCheckBox1);
            iSetZeroCheckBox1 = findViewById(R.id.iSetZeroCheckBox1);
            iMaxSetZeroCheckBox1 = findViewById(R.id.iMaxSetZeroCheckBox1);
            dSetZeroCheckBox1 = findViewById(R.id.dSetZeroCheckBox1);


            // plus and minus buttons
            pMinusButton1 = findViewById(R.id.pMinusButton1);
            iMinusButton1 = findViewById(R.id.iMinusButton1);
            iMaxMinusButton1 = findViewById(R.id.iMaxMinusButton1);
            dMinusButton1 = findViewById(R.id.dMinusButton1);
            pPlusButton1 = findViewById(R.id.pPlusButton1);
            iPlusButton1 = findViewById(R.id.iPlusButton1);
            iMaxPlusButton1 = findViewById(R.id.iMaxPlusButton1);
            dPlusButton1 = findViewById(R.id.dPlusButton1);






        // SECOND PID WINDOW

            controllerToTuneSpinner2 = (Spinner) findViewById(R.id.controllerToTuneSpinner2);

            // value showing boxes
            pValueEditText2 = (EditText) findViewById(R.id.pValueEditText2);
            iValueEditText2 = (EditText) findViewById(R.id.iValueEditText2);
            iMaxValueEditText2 = (EditText) findViewById(R.id.iMaxValueEditText2);
            dValueEditText2 = (EditText) findViewById(R.id.dValueEditText2);


            // seek bars
            pPidSeekBar2 = (SeekBar) findViewById(R.id.p_pidSeekBar2);
            iPidSeekBar2 = (SeekBar) findViewById(R.id.i_pidSeekBar2);
            iMaxPidSeekBar2 = (SeekBar) findViewById(R.id.iMax_pidSeekBar2);
            dPidSeekBar2 = (SeekBar) findViewById(R.id.d_pidSeekBar2);


            // zero value check boxes
            pSetZeroCheckBox2 = findViewById(R.id.pSetZeroCheckBox2);
            iSetZeroCheckBox2 = findViewById(R.id.iSetZeroCheckBox2);
            iMaxSetZeroCheckBox2 = findViewById(R.id.iMaxSetZeroCheckBox2);
            dSetZeroCheckBox2 = findViewById(R.id.dSetZeroCheckBox2);


            // plus and minus buttons
            pMinusButton2 = findViewById(R.id.pMinusButton2);
            iMinusButton2 = findViewById(R.id.iMinusButton2);
            iMaxMinusButton2 = findViewById(R.id.iMaxMinusButton2);
            dMinusButton2 = findViewById(R.id.dMinusButton2);
            pPlusButton2 = findViewById(R.id.pPlusButton2);
            iPlusButton2 = findViewById(R.id.iPlusButton2);
            iMaxPlusButton2 = findViewById(R.id.iMaxPlusButton2);
            dPlusButton2 = findViewById(R.id.dPlusButton2);





        // decimal and integer edit texts
        decimalStepEditText = findViewById(R.id.decimalStepEditText);
        integerStepEditText = findViewById(R.id.integerStepEditText);

        autoSendingSwitch = findViewById(R.id.autoSendingSwitch);


        connectingProgressBar = (ProgressBar) findViewById(R.id.connectingProgressBar);

        // fill spinner with values
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(PID_TuningActivity.this, R.array.pid_controllers_list, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        controllerToTuneSpinner1.setAdapter(adapter);
        controllerToTuneSpinner2.setAdapter(adapter);


        // Set showed values to the default
        // 1
        pValueEditText1.setText((Float.toString(0.0f)));
        iValueEditText1.setText((Float.toString(0.0f)));
        iMaxValueEditText1.setText(Integer.toString(0));
        dValueEditText1.setText((Float.toString(0.0f)));
        // 2
        pValueEditText2.setText((Float.toString(0.0f)));
        iValueEditText2.setText((Float.toString(0.0f)));
        iMaxValueEditText2.setText(Integer.toString(0));
        dValueEditText2.setText((Float.toString(0.0f)));


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
        ///////
        // CONTROLLER 1
        ///////

        // set spinner listener
        controllerToTuneSpinner1.setOnItemSelectedListener(onControllerToTuneSpinner1ItemSelected); // This listener is made in a different way

        // P seek bar listener
        pPidSeekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float value = map(i, 0, sliderMax, 0f, pMaxValue);
                value = setFloatPrecision(value, 2);
                pValueEditText1.setText(Float.toString(value));
                pilcom.updateP1(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });




        // I seek bar listener
        iPidSeekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float value = map(i, 0, sliderMax, 0f, iMaxValue);
                value = setFloatPrecision(value, 2);
                iValueEditText1.setText(Float.toString(value));
                pilcom.updateI1(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });




        // I_Max seek bar listener
        iMaxPidSeekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int value = map(i, 0, sliderMax, 0, iMaxMaxValue);
                iMaxValueEditText1.setText(Integer.toString(value));
                pilcom.updateImax1(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });




        // D seek bar listener
        dPidSeekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float value = map(i, 0, sliderMax, 0f, dMaxValue);
                value = setFloatPrecision(value, 2);
                dValueEditText1.setText(Float.toString(value));
                pilcom.updateD1(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });




        // P value editText
        pValueEditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });




        // I value editText
        iValueEditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });




        // I_Max value exitText
        iMaxValueEditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });




        // D value exitText
        dValueEditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });




        /////////
        // CONTROLLER 2
        /////////


        // set spinner listener
        //controllerToTuneSpinner2.setOnItemSelectedListener(onControllerToTuneSpinner1ItemSelected); // This listener is made in a different way
        controllerToTuneSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                pilcom.changeController2ID(i);
                /*
                    0 - leveling
                    1 - yaw
                    ...
                 */
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // P seek bar listener
        pPidSeekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float value = map(i, 0, sliderMax, 0f, pMaxValue);
                value = setFloatPrecision(value, 2);
                pValueEditText2.setText(Float.toString(value));
                pilcom.updateP2(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });




        // I seek bar listener
        iPidSeekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float value = map(i, 0, sliderMax, 0f, iMaxValue);
                value = setFloatPrecision(value, 2);
                iValueEditText2.setText(Float.toString(value));
                pilcom.updateI2(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });




        // I_Max seek bar listener
        iMaxPidSeekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int value = map(i, 0, sliderMax, 0, iMaxMaxValue);
                iMaxValueEditText2.setText(Integer.toString(value));
                pilcom.updateImax2(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });




        // D seek bar listener
        dPidSeekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float value = map(i, 0, sliderMax, 0f, dMaxValue);
                value = setFloatPrecision(value, 2);
                dValueEditText2.setText(Float.toString(value));
                pilcom.updateD2(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });




        // P value editText
        pValueEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });




        // I value editText
        iValueEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });




        // I_Max value exitText
        iMaxValueEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });




        // D value exitText
        dValueEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });







        //////////////////////////////////////////
        /// OTHER ///
        //////////////////////////////////////////

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
    private AdapterView.OnItemSelectedListener onControllerToTuneSpinner1ItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
        {
            //Toast.makeText(PID_TuningActivity.this, Integer.toString(i), Toast.LENGTH_SHORT).show();


            pilcom.changeController1ID(i);
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






    public void plusMinusButtons1OnClicks(View view)
    {
        float fromValue;

        switch (view.getId())
        {
            case R.id.pMinusButton1:
                fromValue = Float.parseFloat(pValueEditText1.getText().toString()) - decimalStepValue;
                pPidSeekBar1.setProgress(map(fromValue, 0f, pMaxValue, 0, sliderMax));
                break;
            case R.id.pPlusButton1:
                fromValue = Float.parseFloat(pValueEditText1.getText().toString()) + decimalStepValue;
                pPidSeekBar1.setProgress(map(fromValue, 0f, pMaxValue, 0, sliderMax));
                break;

            case R.id.iMinusButton1:
                fromValue = Float.parseFloat(iValueEditText1.getText().toString()) - decimalStepValue;
                iPidSeekBar1.setProgress(map(fromValue, 0f, iMaxValue, 0, sliderMax));
                break;
            case R.id.iPlusButton1:
                fromValue = Float.parseFloat(iValueEditText1.getText().toString()) + decimalStepValue;
                iPidSeekBar1.setProgress(map(fromValue, 0f, iMaxValue, 0, sliderMax));
                break;

            case R.id.iMaxMinusButton1:
                fromValue = Float.parseFloat(iMaxValueEditText1.getText().toString()) - integerStepValue;
                iMaxPidSeekBar1.setProgress(map(fromValue, 0f, (float)iMaxMaxValue, 0, sliderMax));
                break;
            case R.id.iMaxPlusButton1:
                fromValue = Float.parseFloat(iMaxValueEditText1.getText().toString()) + integerStepValue;
                iMaxPidSeekBar1.setProgress(map(fromValue, 0f, (float)iMaxMaxValue, 0, sliderMax));
                break;

            case R.id.dMinusButton1:
                fromValue = Float.parseFloat(dValueEditText1.getText().toString()) - decimalStepValue;
                dPidSeekBar1.setProgress(map(fromValue, 0f, dMaxValue, 0, sliderMax));
                break;
            case R.id.dPlusButton1:
                fromValue = Float.parseFloat(dValueEditText1.getText().toString()) + decimalStepValue;
                dPidSeekBar1.setProgress(map(fromValue, 0f, dMaxValue, 0, sliderMax));
                break;
        }



    }





    public void plusMinusButtons2OnClicks(View view)
    {
        float fromValue;

        switch (view.getId())
        {
            case R.id.pMinusButton2:
                fromValue = Float.parseFloat(pValueEditText2.getText().toString()) - decimalStepValue;
                pPidSeekBar2.setProgress(map(fromValue, 0f, pMaxValue, 0, sliderMax));
                break;
            case R.id.pPlusButton2:
                fromValue = Float.parseFloat(pValueEditText2.getText().toString()) + decimalStepValue;
                pPidSeekBar2.setProgress(map(fromValue, 0f, pMaxValue, 0, sliderMax));
                break;

            case R.id.iMinusButton2:
                fromValue = Float.parseFloat(iValueEditText2.getText().toString()) - decimalStepValue;
                iPidSeekBar2.setProgress(map(fromValue, 0f, iMaxValue, 0, sliderMax));
                break;
            case R.id.iPlusButton2:
                fromValue = Float.parseFloat(iValueEditText2.getText().toString()) + decimalStepValue;
                iPidSeekBar2.setProgress(map(fromValue, 0f, iMaxValue, 0, sliderMax));
                break;

            case R.id.iMaxMinusButton2:
                fromValue = Float.parseFloat(iMaxValueEditText2.getText().toString()) - integerStepValue;
                iMaxPidSeekBar2.setProgress(map(fromValue, 0f, (float)iMaxMaxValue, 0, sliderMax));
                break;
            case R.id.iMaxPlusButton2:
                fromValue = Float.parseFloat(iMaxValueEditText2.getText().toString()) + integerStepValue;
                iMaxPidSeekBar2.setProgress(map(fromValue, 0f, (float)iMaxMaxValue, 0, sliderMax));
                break;

            case R.id.dMinusButton2:
                fromValue = Float.parseFloat(dValueEditText2.getText().toString()) - decimalStepValue;
                dPidSeekBar2.setProgress(map(fromValue, 0f, dMaxValue, 0, sliderMax));
                break;
            case R.id.dPlusButton2:
                fromValue = Float.parseFloat(dValueEditText2.getText().toString()) + decimalStepValue;
                dPidSeekBar2.setProgress(map(fromValue, 0f, dMaxValue, 0, sliderMax));
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
