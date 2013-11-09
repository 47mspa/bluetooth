package com.example.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;

public class BlueToothView extends View{
	public static final int REQUEST_ENABLE_BT = 1;
	public static final int REQUEST = -1;
	public static final int RESULT_CANCELED = 0;
	public static boolean bluetoothOn = false;
	
	public BlueToothView(Context context) {
		super(context);
		BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
		if (bluetooth == null){
			System.out.println("Device not bluetooth compatible");
		}
		else if (!bluetooth.isEnabled()){
			 requestBlueToothOn();
		}
		if(bluetoothOn){

			MyBroadcastReceiver receiver = new MyBroadcastReceiver();
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			this.getContext().registerReceiver(receiver, filter);
			bluetooth.startDiscovery();
			
		}
			
	}
	
	public void requestBlueToothOn() {
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		((Activity)this.getContext()).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	}

}

class MyBroadcastReceiver extends BroadcastReceiver{

	public MyBroadcastReceiver () {
		
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			System.out.println("Found device!");
            System.out.println(device.getName() + "\n" + device.getAddress());

		}
		
	}
	
}
