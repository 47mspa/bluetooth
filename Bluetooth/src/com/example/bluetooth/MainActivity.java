package com.example.bluetooth;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.content.DialogInterface.OnClickListener;;

public class MainActivity extends Activity {

	BlueToothView bView;
	File selectedFile;
	Object t;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//useFilePicker();
		bView = new BlueToothView(this);
		setContentView(bView);
	}
	public void useFilePicker(Object t) {
		this.t = t;
		Intent myIntent = new Intent(MainActivity.this, AndroidExplorer.class);
		MainActivity.this.startActivity(myIntent);
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println(requestCode + " " + resultCode);
		  if (requestCode == BlueToothView.REQUEST_ENABLE_BT) {

		     if(resultCode == BlueToothView.REQUEST){      
		         BlueToothView.bluetoothOn = true;         
		     }
		     else {		    	 
		    	 AlertDialog.Builder builder = new AlertDialog.Builder(this);
		    	 builder.setMessage("Bluetooth must be turned on in order for file transfer. Would you like to turn it on now?");
		    	 builder.setTitle("Error");
		    	 builder.setPositiveButton("OK", new ButtonListener());
		    	 builder.setNegativeButton("Cancel", null);
		    	 AlertDialog dialog = builder.create();
		    	 dialog.show();
		     }
		  }
		}//onActivityResult
	
	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    Log.d("MyApp", "Bringing this Activity back!");

	    Bundle extras = intent.getExtras();
	    System.out.println("Extras: " + extras);
	    if (extras != null) {
	    	selectedFile = (File)extras.getSerializable("File");
	    }
	    synchronized(t) {
		t.notify();
	    }
	}
	
	public File getSelectedFile() {
		return selectedFile;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onStop(){
		super.onStop();
		if(bView != null)
			bView.unregister();
	}
	class ButtonListener implements OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			bView.requestBlueToothOn();
			
		}
		
	}

}


