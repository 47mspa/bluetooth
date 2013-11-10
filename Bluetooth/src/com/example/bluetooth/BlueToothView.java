package com.example.bluetooth;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;

public class BlueToothView extends View{
	public static final int REQUEST_ENABLE_BT = 1;
	public static final int REQUEST = -1;
	public static final int RESULT_CANCELED = 0;
	public static boolean bluetoothOn = false;
	public BluetoothAdapter bluetooth;
	public static String NAME = "Bluetooth";
	public static UUID MY_UUID;
	private Activity activity;
	public MyBroadcastReceiver receiver;
	private BluetoothServerSocket mmServerSocket;
	
	public BlueToothView(Context context) {
		super(context);
		activity = (Activity)context;
		String uuid ="566156c0-49a8-11e3-8f96-0800200c9a66";
		MY_UUID = UUID.fromString(uuid);
		
		bluetooth = BluetoothAdapter.getDefaultAdapter();
		if (bluetooth == null){
			System.out.println("Device not bluetooth compatible");
			System.exit(0);
		}
		while(!bluetooth.isEnabled()){
			 requestBlueToothOn();
		}
		
		Intent discoverableIntent = new
		Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		activity.startActivity(discoverableIntent);
		
		receiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.getContext().registerReceiver(receiver, filter);
		bluetooth.startDiscovery();		
		
		setup();
	}
	
	private void setup() {
		
		AcceptThread acceptThread = new AcceptThread();
		acceptThread.start();
		
		 AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    	 builder.setMessage("Press to Connect");
    	 builder.setTitle("Connect");
    	 builder.setPositiveButton("OK", new ButtonListener(acceptThread));
    	 builder.setNegativeButton("Cancel", null);
    	 AlertDialog dialog = builder.create();
    	 dialog.show();
	}
	
	public void requestBlueToothOn() {
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		((Activity)this.getContext()).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	}
	
	private class ButtonListener implements DialogInterface.OnClickListener {

		AcceptThread thread;
		ButtonListener(AcceptThread thread) {
			this.thread = thread;
		}
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			System.out.println("HEREzwer");
			thread.cancel();

			ConnectThread conThread = new ConnectThread(receiver.myDevice);
			conThread.start();
		}
		
	}
	
	private class AcceptThread extends Thread {
    public AcceptThread() {
        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = bluetooth.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
        } catch (IOException e) { }
        mmServerSocket = tmp;        
    }
 
    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                break;
            }
            // If a connection was accepted
            if (socket != null) {
                // Do work to manage the connection (in a separate thread)
                manageConnectedSocket(socket);
                try {
					mmServerSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                break;
            }
        }
    }
 
    private void manageConnectedSocket(BluetoothSocket socket) {
    	System.out.println("SOCKEEKEEEEETTT!!!!");
    	
    	/*
    	BTDataManager manager = new BTDataManager(socket);
		byte[] buffer = new byte[1028];
		int size = 1024;
		byte[] arr = ByteBuffer.allocate(4).putInt(size).array();
		buffer[0] = arr[0];
		buffer[1] = arr[1];
		buffer[2] = arr[2];
		buffer[3] = arr[3];
		
		for(int i = 4; i < buffer.length; i++) {
			byte num = (byte)(Math.random()*2);
			buffer[i] = num;
		}

    	manager.write(buffer);	
    	*/
    	BTDataManager manager = new BTDataManager(socket);
    	File sFile = ((MainActivity)activity).getSelectedFile();
    	try {
			BTFile btFile = FileManager.readFile(sFile);
			manager.write(btFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    			
			
    	
	}

	/** Will cancel the listening socket, and cause the thread to finish */
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) { }
    }
}

private class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
 
    public ConnectThread(BluetoothDevice device) {
    	System.out.println("asdfasdf");

        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;
 
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
        	e.printStackTrace();
        }
        mmSocket = tmp;
    }
 
    public void run() {
    	System.out.println("asdf3");

        // Cancel discovery because it will slow down the connection
        bluetooth.cancelDiscovery();
 
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException e) { 
            	e.printStackTrace();

            }
            return;
        }
 
        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket(mmSocket);
    }
 
    private void manageConnectedSocket(BluetoothSocket mmSocket2) {
    	System.out.println("asdf");
    	BTDataManager manager = new BTDataManager(mmSocket2);
    	Thread t = new Thread(manager);
    	t.start();
    	
    	while(true) {
    		Object obj = manager.getLatestData();
    		if(obj != null) {
    			byte [] data = (byte [])obj;
    			for(int i = 0;i<data.length;i++){
    				System.out.println("DATA!!!!!");
    				System.out.println(data[i]);
    			}
    		}
    	}
		
	}

	/** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}

public void unregister() {
	activity.unregisterReceiver(receiver);
	
}
	
	

}

class MyBroadcastReceiver extends BroadcastReceiver{
	
	public BluetoothDevice myDevice;
	
	public MyBroadcastReceiver () {

	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if(device != null && (device.getName().equals("SGH-T999V") || device.getName().equals("Nexus 4") || device.getName().equals("Nexus S"))){
				myDevice = device;
				System.out.println("Found device!");
	            System.out.println(device.getName() + "\n" + device.getAddress());
			}
			

		}
		
	}
	
}
