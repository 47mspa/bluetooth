package com.example.bluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.content.DialogInterface.OnClickListener;;

public class MainActivity extends Activity {

	BlueToothView bView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bView = new BlueToothView(this);
		setContentView(bView);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onStop(){
		super.onStop();
		bView.unregister();
	}
	class ButtonListener implements OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			bView.requestBlueToothOn();
			
		}
		
	}

}


