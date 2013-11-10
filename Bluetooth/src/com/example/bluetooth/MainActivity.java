package com.example.bluetooth;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	BlueToothView bView;
	File selectedFile;
	Object t;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//useFilePicker();
		bView = new BlueToothView(this);
		setContentView(R.layout.view);//setContentView(bView);
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
	    if(t != null) {
	    synchronized(t) {
		t.notify();
	    }
	    }
	}
	
	public void doneSearching (View v) {
		//bView.bluetooth.cancelDiscovery();
		v.setEnabled(false);
	    final ListView listview = (ListView) findViewById(R.id.listView1);
	    final ArrayList<String> list = bView.receiver.devices;
	    
	    final StableArrayAdapter adapter = new StableArrayAdapter(this,
	            android.R.layout.simple_list_item_1, list);
	        listview.setAdapter(adapter);

	        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	          @Override
	          public void onItemClick(AdapterView<?> parent, final View view,
	              int position, long id) {
	            final String item = bView.receiver.devices.get(position);//(String) parent.getItemAtPosition(position);
	        
             	bView.deviceSelected(item);
	            adapter.notifyDataSetChanged();
	            view.setAlpha(1);
	          }

	        });
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
	
	 private class StableArrayAdapter extends ArrayAdapter<String> {

		    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		    public StableArrayAdapter(Context context, int textViewResourceId,List<String> objects) {
		      super(context, textViewResourceId, objects);
		      for (int i = 0; i < objects.size(); ++i) {
		        mIdMap.put(objects.get(i), i);
		      }
		    }

		    @Override
		    public long getItemId(int position) {
		      String item = getItem(position);
		      return mIdMap.get(item);
		    }

		    @Override
		    public boolean hasStableIds() {
		      return true;
		    }

		  }
	
}




