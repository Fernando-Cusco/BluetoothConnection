package org.ups.giiata.bluetooth_connection;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private Set<android.bluetooth.BluetoothDevice> pairedDevices;
    private BluetoothSocket BTSocket;

    private android.widget.Button btnListar;
    private android.widget.Button btnConectar;
    private android.widget.Button btnEnviar;
    private android.widget.ListView lstView;
    private android.widget.EditText txtMAC;

    private java.io.DataOutputStream dou;

    private android.app.ProgressDialog progressDialog;

    private boolean isConnected;
    private final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter==null){
            Toast.makeText(getApplicationContext(), "Bluetooth Adapter Not Available", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            if(bluetoothAdapter.isEnabled()){
                Toast.makeText(getApplicationContext(), "Bluetooth Act: OK!", Toast.LENGTH_SHORT).show();
            }else{
                Intent turnOnBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnOnBT,1);
            }
        }

        btnListar = (android.widget.Button) findViewById(R.id.btnListar);
        btnConectar = (android.widget.Button) findViewById(R.id.btnConnectar);
        btnEnviar = (android.widget.Button) findViewById(R.id.btnEnviar);
        lstView = (android.widget.ListView) findViewById(R.id.lstDevices);
        txtMAC = (android.widget.EditText) findViewById(R.id.edtMAC);


        isConnected = false;

        btnListar.setOnClickListener(new android.widget.Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                pairedDevicesList();
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    dou.write((int) 'a');

                }catch(Exception e){
                    Log.e("Erro: ", e.getMessage());

                }

            }
        });

        btnConectar.setOnClickListener(new android.widget.Button.OnClickListener(){
            @Override
            public void onClick(View v) { ();
            }
        });
    }

    private void pairedDevicesList(){
        pairedDevices = bluetoothAdapter.getBondedDevices();
        java.util.ArrayList<String> list = new java.util.ArrayList<>();

        if (pairedDevices.size()>0){
            for(BluetoothDevice btD : pairedDevices){
                list.add(btD.getName()+"\n"+btD.getAddress());
            }
        }else{
            Toast.makeText(getApplicationContext(),"Not paired devices found!",Toast.LENGTH_SHORT).show();
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, list);
        lstView.setAdapter(adapter);

        Toast.makeText(getApplicationContext(),txtMAC.getText().toString(),Toast.LENGTH_SHORT).show();
    }

    private void connect(){
        new ConnectBT().execute();
    }


    private class ConnectBT extends AsyncTask<Void, Void, Void>{
        private boolean connectSuccess = true;



        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if(BTSocket == null || !isConnected){
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice btD = bluetoothAdapter.getRemoteDevice(txtMAC.getText().toString());
                    BTSocket = btD.createInsecureRfcommSocketToServiceRecord(myUUID);
                    bluetoothAdapter.cancelDiscovery();

                    BTSocket.connect();

                    dou = new java.io.DataOutputStream(BTSocket.getOutputStream());
                    


                }
            }catch (Exception e){
                connectSuccess = false;
                Log.e("Error: " ,e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!connectSuccess){
                Toast.makeText(getApplicationContext(),"Can't Connect!!!",Toast.LENGTH_SHORT).show();
                try {

                }catch(Exception e){
                    Log.e("Error: ", e.getMessage());
                }
            }else{
                isConnected = true;
                Toast.makeText(getApplicationContext(),"Connection Successfully stablished!",Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }
}
