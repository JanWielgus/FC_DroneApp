package com.example.fcdroneapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Set;

public class DeviceList extends AppCompatActivity {

    Button refreshPairedDevicesButton;
    ListView pairedDevicesListView;

    // bluetooth variables
    private BluetoothAdapter bluetoothAdapter = null;
    private Set<BluetoothDevice> pairedDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        // open login activity at the beginning
        startActivity(new Intent(DeviceList.this, LoginActivity.class));

        refreshPairedDevicesButton = (Button) findViewById(R.id.refreshPairedDevicesButton);
        pairedDevicesListView = (ListView) findViewById(R.id.pairedDevicesListView);


        // init bluetooth
        initBluetooth();

        refreshPairedDevicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                refreshPairedDeviceList();
            }
        });


        // click the button at the beginning to refresh devices list for the user
        refreshPairedDevicesButton.callOnClick();
    }


    private void initBluetooth()
    {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null)
        {
            // This device has no bluetooth
            Toast.makeText(DeviceList.this, "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();

            // close the app
            finish();
        }
        else
        {
            if (!bluetoothAdapter.isEnabled())
            {
                // Ask used to turn on the bluetooth
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon, 1);
            }
        }
    }


    private void refreshPairedDeviceList()
    {
        pairedDevices = bluetoothAdapter.getBondedDevices();
        ArrayList deviceList = new ArrayList();

        if (pairedDevices.size() > 0)
        {
            // Get the device's name and address
            for (BluetoothDevice bt : pairedDevices)
                deviceList.add(bt.getName() + "\n" + bt.getAddress());
        }
        else
        {
            Toast.makeText(DeviceList.this, "No Paired Bluetooth Devices Found", Toast.LENGTH_LONG).show();
        }

        ArrayAdapter adapter = new ArrayAdapter(DeviceList.this, android.R.layout.simple_list_item_1, deviceList);
        pairedDevicesListView.setAdapter(adapter);

        pairedDevicesListView.setOnItemClickListener(onPairedDeviceInListClickListener);
    }


    // Method called when device in the paired devices list was clicked
    private AdapterView.OnItemClickListener onPairedDeviceInListClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            // Get the device MAC address - the last 17 chars in the View
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);

            // Make an intent to start next activity
            Intent intent = new Intent(DeviceList.this, PID_TuningActivity.class);
            // Change the activity
            intent.putExtra("EXTRA_ADDRESS", address);
            startActivity(intent);
        }
    };



    static void msg(Object sender, String message)
    {
        Toast.makeText((Context) sender, message, Toast.LENGTH_SHORT).show();
    }
}
