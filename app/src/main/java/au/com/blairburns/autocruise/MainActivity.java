package au.com.blairburns.autocruise;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import java.util.Set;

import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import android.widget.ArrayAdapter;
import java.io.IOException;
import java.util.UUID;
import android.bluetooth.BluetoothSocket;

public class MainActivity extends AppCompatActivity {

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket btSocket = null;
    private String address = null;

    private TextView lblStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enableBT();


        final Button btnConnect = findViewById(R.id.btnConnect);
        lblStatus = findViewById(R.id.lblStatus);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               lblStatus.setText("Connecting...");
               connectCAN();
            }
        });

    }

    private void connectCAN(){
        pairedDevicesList();
        connect();
    }

    public BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;

    void enableBT() {
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if (myBluetooth == null) {
            //Show a mensag. that thedevice has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            //finish apk
            finish();
        } else {
            if (myBluetooth.isEnabled()) {
            } else {
                //Ask to the user turn the bluetooth on
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon, 1);
            }
        }
    }

    private void pairedDevicesList()
    {
        Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size()>0)
        {
            for(BluetoothDevice bt : pairedDevices)
            {
                address = bt.getAddress();
                Log.i("Device",bt.getName() + ", " + bt.getAddress() ); //Get the device's name and the address
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        //final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        //devicelist.setAdapter(adapter);
        //devicelist.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }

    void connect(){

        //Intent newint = getIntent();
       // address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);

        try {
            BluetoothDevice dispositive = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
            btSocket = dispositive.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            btSocket.connect();//start connection
            if (btSocket.isConnected()) {
                lblStatus.setText("Connected");
                Log.i("is connected", "true");
            }
        }
        catch (IOException e){
            //Log.d("Connection Error", e);
        }
    }

}
