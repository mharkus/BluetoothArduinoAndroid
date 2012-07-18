package com.mlst.bluetootharduino;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class Controls extends Activity {

    private static final UUID MAGIC_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // magic
												 // UUID
    private static final String TAG = Main.class.getName();

    private BluetoothAdapter bluetoothAdapter;
    private BTConnectionThread btConnectionThread;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.controls);

	bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	bindViews();

	BluetoothDevice device = (BluetoothDevice) getIntent().getParcelableExtra("device");
	if(device != null){
	    connectToBTDevice(device);
	}else{
	    Toast.makeText(this, "No device selected", Toast.LENGTH_SHORT).show();
	}
    }

    private void bindViews() {
	((ToggleButton) findViewById(R.id.toggleLED1)).setOnCheckedChangeListener(changeListener);
	((ToggleButton) findViewById(R.id.toggleLED2)).setOnCheckedChangeListener(changeListener);
	((ToggleButton) findViewById(R.id.toggleLED3)).setOnCheckedChangeListener(changeListener);
	((ToggleButton) findViewById(R.id.toggleLED4)).setOnCheckedChangeListener(changeListener);
	((ToggleButton) findViewById(R.id.toggleLED5)).setOnCheckedChangeListener(changeListener);
	((ToggleButton) findViewById(R.id.toggleLED6)).setOnCheckedChangeListener(changeListener);
	((ToggleButton) findViewById(R.id.allRed)).setOnCheckedChangeListener(changeListener);
	((ToggleButton) findViewById(R.id.allGreen)).setOnCheckedChangeListener(changeListener);
    }

    protected void connectToBTDevice(final BluetoothDevice device) {
	new AlertDialog.Builder(this).setMessage("Connect to " + device.getName() + " ?").setNegativeButton("No", null).setPositiveButton("Yes", new OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {

		pd = new ProgressDialog(Controls.this);
		pd.setMessage("Waiting for BT device");
		pd.setCancelable(true);
		pd.setOnCancelListener(new OnCancelListener() {

		    @Override
		    public void onCancel(DialogInterface dialog) {
			if (btConnectionThread != null) {
			    btConnectionThread.cancel();
			}
		    }
		});
		pd.show();

		btConnectionThread = new BTConnectionThread(device);
		btConnectionThread.start();
	    }
	}).create().show();

    }

    private OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	    String pin = "";
	    if (buttonView.getId() == R.id.toggleLED1) {
		pin = "p1";
	    } else if (buttonView.getId() == R.id.toggleLED2) {
		pin = "p2";
	    } else if (buttonView.getId() == R.id.toggleLED3) {
		pin = "p3";
	    }
	    if (buttonView.getId() == R.id.toggleLED4) {
		pin = "p4";
	    } else if (buttonView.getId() == R.id.toggleLED5) {
		pin = "p5";
	    } else if (buttonView.getId() == R.id.toggleLED6) {
		pin = "p6";
	    } else if (buttonView.getId() == R.id.allRed) {
		pin = "ar";
	    } else if (buttonView.getId() == R.id.allGreen) {
		pin = "ag";
	    }

	    if (isChecked) {
		btConnectionThread.write((pin + "1").getBytes());
	    } else {
		btConnectionThread.write((pin + "0").getBytes());
	    }

	}
    };

    class BTChannelThread extends Thread {

	private boolean keepAlive = true;
	private OutputStream outStream;
	private BluetoothSocket btSocket;

	public BTChannelThread(BluetoothSocket btSocket) {

	    this.btSocket = btSocket;

	    try {
		outStream = btSocket.getOutputStream();
	    } catch (IOException e) {
	    }
	}

	@Override
	public void run() {
	    while (keepAlive) {
		// do nothing
		try {
		    Thread.sleep(100);
		} catch (InterruptedException e) {
		}
	    }
	}

	public void sendCommand(byte[] bytes) {
	    try {
		outStream.write(bytes);

	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

	public void cancel() {

	    keepAlive = false;

	    try {
		btSocket.close();
	    } catch (IOException e) {
	    }
	}
    }

    class BTConnectionThread extends Thread {

	private BluetoothDevice device;
	private BluetoothSocket btSocket;
	private BTChannelThread btChannelThread;

	public BTConnectionThread(BluetoothDevice device) {
	    this.device = device;

	}

	@Override
	public void run() {
	    if (bluetoothAdapter.isDiscovering()) {
		bluetoothAdapter.cancelDiscovery();
	    }

	    try {
		btSocket = device.createRfcommSocketToServiceRecord(MAGIC_UUID);
		btChannelThread = new BTChannelThread(btSocket);

		btSocket.connect();

		if (pd != null) {
		    pd.dismiss();
		}

		btChannelThread.start();
	    } catch (IOException e) {
		Log.e(TAG, "Unable to connect to bluetooth device " + device.getName(), e);
		try {
		    btSocket.close();
		} catch (IOException e1) {
		    // supress
		}
	    }

	}

	public void cancel() {
	    try {
		btSocket.close();
	    } catch (IOException e) {
		// supress
	    }
	}

	public void write(byte[] bytes) {
	    btChannelThread.sendCommand(bytes);
	}
    }

    @Override
    protected void onDestroy() {
	if (btConnectionThread != null) {
	    btConnectionThread.cancel();
	}
	super.onDestroy();
    }
}
