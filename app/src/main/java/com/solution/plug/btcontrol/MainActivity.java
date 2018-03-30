package com.solution.plug.btcontrol;

        import android.annotation.SuppressLint;
        import android.app.AlertDialog;
        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.bluetooth.BluetoothServerSocket;
        import android.bluetooth.BluetoothSocket;
        import android.content.DialogInterface;
        import android.content.DialogInterface.OnClickListener;
        import android.content.Intent;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Looper;
        import android.os.Message;
        import android.os.Vibrator;
        import android.support.design.widget.FloatingActionButton;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.DefaultItemAnimator;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;
        import java.io.BufferedReader;
        import java.io.FileNotFoundException;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.OutputStream;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    int REQUEST_ENABLE_BT = 42;
    private String[] SensorNames;
    String TAG = "MainActivity";
    private String address;
    private int amount_measures = 0;
    FloatingActionButton btn_connect;
    private int bytes_in;
    private int con_state = 0;
    private int count_letter = 0;
    private int counter = 0;
    private int counter_2 = 0;
    private int counter_msg_in = 0;
    private int counter_threads = 0;
    private String deviceName;
    private int disconnable = 0;
    private int first_1 = 1;
    ImageView image;
    private int lastX = 0;
    private SensorAdapter mAdapter;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBtDevice;
    BluetoothServerSocket mBtServerSocket;
    BluetoothSocket mBtSocket;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message inputMessage) {
            switch (inputMessage.what) {
                case R.styleable.AppCompatTheme_buttonBarStyle /*43*/:
                    int i;
                    String readMessage = new String((char[]) inputMessage.obj, 0, inputMessage.arg1);
                    readMessage.trim();
                    Log.i(MainActivity.this.TAG, readMessage);
                    MainActivity.this.counter = 0;
                    for (i = 0; i < readMessage.length(); i++) {
                        if (readMessage.charAt(i) == ',') {
                            MainActivity.this.counter = MainActivity.this.counter + 1;
                        }
                    }
                    MainActivity.this.counter = MainActivity.this.counter + 1;
                    if (readMessage != null) {
                        String[] slot = new String[MainActivity.this.maxvalues];
                        float[] slotfloat = new float[MainActivity.this.maxvalues];
                        if (MainActivity.this.counter <= MainActivity.this.maxvalues) {
                            String[] seperateValues = readMessage.split(",");
                            for (i = 0; i < MainActivity.this.counter; i++) {
                                slot[i] = seperateValues[i];
                            }
                            MainActivity.this.sensorList.clear();
                            for (i = 0; i < MainActivity.this.counter; i++) {
                                MainActivity.this.sensorList.add(new Sensor(MainActivity.this.SensorNames[i] + ":", slot[i]));
                            }
                            MainActivity.this.mAdapter.notifyDataSetChanged();
                            return;
                        }
                        MainActivity.this.streamopen = false;
                        MainActivity.this.openAlertDialog(5);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    };

    private int maxvalues = 10;
    byte[] mesg_out;
    private long millis = 0;
    private int rate = 0;
    private int recently_closed = 0;
    private RecyclerView recyclerView;
    private String remoteDeviceMac;
    TextView sensor1;
    TextView sensor2;
    TextView sensor3;
    TextView sensor4;
    TextView sensor5;
    TextView sensor6;
    private List<Sensor> sensorList = new ArrayList();
    private String[] slot;
    private int startTime = 0;
    private int stopTime = 1;
    private boolean streamopen = true;
    private int time_diff = 0;
    Toolbar toolbar;
    private String usedchars = "1234567890,";
    Vibrator vibe;

    class AnonymousClass5 implements OnClickListener {
        final /* synthetic */ int val$bluetooth_status;

        AnonymousClass5(int i) {
            this.val$bluetooth_status = i;
        }

        public void onClick(DialogInterface dialog, int id) {
            dialog.cancel();
            if (this.val$bluetooth_status == 5) {
                MainActivity.this.onBackPressed();
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mBtSocket;
        private final InputStream mInStream;
        private final OutputStream mOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            this.mBtSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                if (this.mBtSocket.isConnected()) {
                    Log.i(MainActivity.this.TAG, "getInputStream");
                    tmpIn = socket.getInputStream();
                    tmpOut = socket.getOutputStream();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.mInStream = tmpIn;
            this.mOutStream = tmpOut;
        }

        public void run() {
            if (this.mBtSocket.isConnected()) {
                Log.i(MainActivity.this.TAG, "Connected Thread started - looking for incomming massages");
                char[] buffer = new char[1024];
                while (MainActivity.this.streamopen) {
                    try {
                        buffer[MainActivity.this.bytes_in] = (char) this.mInStream.read();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (buffer[MainActivity.this.bytes_in] == ';') {
                        MainActivity.this.mHandler.obtainMessage(43, MainActivity.this.bytes_in, -1, buffer).sendToTarget();
                        MainActivity.this.bytes_in = 0;
                        buffer = new char[1024];
                    } else if (MainActivity.this.bytes_in > buffer.length - 2) {
                        MainActivity.this.mHandler.obtainMessage(43, MainActivity.this.bytes_in, -1, buffer).sendToTarget();
                        MainActivity.this.bytes_in = 0;
                        buffer = new char[1024];
                        Log.e(MainActivity.this.TAG, "Message too long");
                    } else {
                        MainActivity.this.bytes_in = MainActivity.this.bytes_in + 1;
                    }
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                this.mOutStream.write(bytes);
                Log.e(MainActivity.this.TAG, "Message out: " + bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                this.mBtSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        this.mAdapter = new SensorAdapter(this.sensorList);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        this.recyclerView.setItemAnimator(new DefaultItemAnimator());
        this.recyclerView.setAdapter(this.mAdapter);
        this.SensorNames = new String[this.maxvalues];
        this.SensorNames = readFromFileAndUpdateSensorNames();
        this.sensorList.clear();
        for (int i = 0; i < 6; i++) {
            this.sensorList.add(new Sensor(this.SensorNames[i] + ":", "    -   "));
        }
        this.mAdapter.notifyDataSetChanged();

        String info = getDeviceListData();
        this.address = info.substring(info.length() - 17);
        this.deviceName = info.substring(0, info.length() - 17);

        this.btn_connect = (FloatingActionButton) findViewById(R.id.btn_connect);   
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBluetooth();
    }

    protected void checkBluetooth() {
        if (this.mBluetoothAdapter == null) {
            openAlertDialog(0);
        } else if (!this.mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), this.REQUEST_ENABLE_BT);
        }
    }

    private String getSampleRate() {
        String rate_string = BuildConfig.FLAVOR;
        this.millis = System.currentTimeMillis();
        this.stopTime = (int) this.millis;
        this.time_diff = this.stopTime - this.startTime;
        if (this.time_diff != 0) {
            this.rate = 1000 / this.time_diff;
            rate_string = Integer.toString(this.rate);
        } else {
            rate_string = " --- ";
        }
        this.millis = System.currentTimeMillis();
        this.startTime = (int) this.millis;
        return rate_string;
    }

    public void btnConnect(View view) {
        this.remoteDeviceMac = this.address;
        this.mBtDevice = this.mBluetoothAdapter.getRemoteDevice(this.remoteDeviceMac);
        Log.e(this.TAG, this.remoteDeviceMac);
        try {
            this.mBtSocket = this.mBtDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.mBtSocket.connect();
        } catch (IOException e2) {
            e2.printStackTrace();
            Log.e(this.TAG, "mBtSocket cannot connect");
            if (this.disconnable != 1) {
                makeToast("Bluetooth cannot connect");
                this.recently_closed = 0;
            }
            if (this.disconnable == 1) {
                this.disconnable = 0;
                this.recently_closed = 1;
            }
            try {
                this.mBtSocket.close();
                Log.e(this.TAG, "Socket closed");
            } catch (IOException e3) {
                Log.e(this.TAG, "Socket cannot be closed");
            }
        }
        if (this.mBtSocket.isConnected()) {
            this.con_state = 1;

            if (this.disconnable == 0) {
                this.btn_connect.setEnabled(false);
                this.disconnable = 1;
            }
            makeToast("Bluetooth-Device connected");
            if (this.recently_closed == 0) {
                Log.e(this.TAG, "Socket opened again");
                startConnThread();
            }
        }
    }

    private void startConnThread() {
        new Thread(new Runnable() {
            public void run() {
                do {
                } while (!MainActivity.this.mBtSocket.isConnected());
                new ConnectedThread(MainActivity.this.mBtSocket).start();
                Log.e(MainActivity.this.TAG, "Thread startet again");
            }
        }).start();
    }

    @SuppressLint("WrongConstant")
    private void makeToast(String topping) {
        Toast.makeText(getApplicationContext(), topping, 1).show();
    }

    private void openAlertDialog(final int bluetooth_status) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Buetooth status");
        switch (bluetooth_status) {
            case R.styleable.View_theme /*4*/:
                alertDialogBuilder.setMessage(R.string.bt_connection_lost);
                break;
            case R.styleable.Toolbar_contentInsetLeft /*5*/:
                alertDialogBuilder.setMessage("The maximum number (" + Integer.toString(this.maxvalues) + ") of Sensors is exceeded. Please reduce number of sensors and make sure the end of each Message is confirmed by a ';'");
                break;
        }
        alertDialogBuilder.setPositiveButton("OK", new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                if (bluetooth_status == 5) {
                    MainActivity.this.streamopen = true;
                    MainActivity.this.onBackPressed();
                }
            }
        });
        alertDialogBuilder.create().show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 42) {
            if (resultCode == 0) {
                openAlertDialog(1);
            }
            if (resultCode == -1) {
                openAlertDialog(2);
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.con_state == 1) {
            try {
                this.mBtSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("WrongConstant")
    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(65536);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    protected void onStop() {
        super.onStop();
    }

    public void onResume() {
        super.onResume();
        this.sensorList.clear();

        for (int i = 0; i < 6; i++) {
            this.sensorList.add(new Sensor(this.SensorNames[i] + ":", "    -   "));
        }
        this.mAdapter.notifyDataSetChanged();
    }

    private String[] getCleanStrings(String strg) {
        return strg.split(",");
    }


}