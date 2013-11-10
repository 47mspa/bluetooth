package com.example.bluetooth;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

//Currently for testing, this sends and recieves a byte buffer 
public class BTDataManager implements Runnable {

	private final BluetoothSocket m_socket; //The Bluetooth socket that is connected
	private final InputStream m_instream; 
	private final OutputStream m_outstream;
	private DataInputStream m_instreamReader;
	
	private final ObjectInputStream m_objinstream; //send and recieve serialized objects instead of files 
	private final ObjectOutputStream m_objoutstream;

	private static final Object m_socketLock = new Object(); // uh I don't know how many objects can use the bluetooth socket.. this may screw up
	private final ArrayBlockingQueue<Object> m_dataPackets; //The data packets that have been read

	public BTDataManager(BluetoothSocket socket){
		m_socket = socket;
		InputStream tmpIn = null;
		OutputStream tmpOut = null;
		ObjectInputStream tmpObjIn = null;
		ObjectOutputStream tmpObjOut = null;

		try {
			tmpIn = m_socket.getInputStream();
			tmpOut = m_socket.getOutputStream();
			tmpObjIn = new ObjectInputStream(new BufferedInputStream(tmpIn));
			tmpObjOut = new ObjectOutputStream(new BufferedOutputStream(tmpOut));

		} catch (IOException e) {
			e.printStackTrace();
		}

		m_instream = tmpIn;

			m_instreamReader = new DataInputStream(m_instream);
		
		
		m_outstream = tmpOut;
		m_objinstream = tmpObjIn;
		m_objoutstream = tmpObjOut;
		m_dataPackets = new ArrayBlockingQueue<Object>(10); //At most 10 objects queued 


	}

	@Override
	public void run() {
		Log.d("DEBUG","ZERO");

		while (true) { 
			/*
			try {
				if(m_instreamReader.available() <= 0)
					continue;
				byte[] numBytes = new byte [4];
				m_instreamReader.readFully(numBytes, 0, 4);
				int result = (numBytes[3] & 0xFF) | (numBytes[2] & 0xFF) << 8 | (numBytes[1] & 0xFF) << 16 | (numBytes[0] & 0xFF) << 24;
				byte[] buffer = new byte[result];
				m_instreamReader.readFully(buffer);
				m_dataPackets.add(buffer);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
			
			try {
				if(m_objinstream.available() <= 0)
					continue;
				
				Object obj = m_objinstream.readObject();
				m_dataPackets.add(obj);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			
		}

	}

	/**
	 * Retrieve the object data read in from the bluetooth socket from the queue
	 * @author Max 
	 */
	public synchronized Object getLatestData(){
		return this.m_dataPackets.poll(); // returns null if empty
	}

	public void write(byte [] bytes) {

		try {
			m_outstream.write(bytes);

		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
	
	public void write(BTFile btFile) throws IOException{
		Log.d("WRITE", "Write BTFile 1");
		FileManager.writeFile(btFile);
		Log.d("WRITE", "Write BTFile 2");

	}

	/* Call this from the main activity to shutdown the connection */
	public void cancel() {
		try {
			m_socket.close();
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}


}
