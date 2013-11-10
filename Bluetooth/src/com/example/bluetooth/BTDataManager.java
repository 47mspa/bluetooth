package com.example.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;

import android.bluetooth.BluetoothSocket;

//Currently for testing, this sends and recieves a byte buffer 
public class BTDataManager implements Runnable {

	private final BluetoothSocket m_socket; //The Bluetooth socket that is connected
	private final InputStream m_instream; 
	private final OutputStream m_outstream;
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
			tmpObjIn = new ObjectInputStream(tmpIn);
			tmpObjOut = new ObjectOutputStream(tmpOut);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		m_instream = tmpIn;
		m_outstream = tmpOut;
		m_objinstream = tmpObjIn;
		m_objoutstream = tmpObjOut;
		m_dataPackets = new ArrayBlockingQueue<Object>(10); //At most 10 objects queued lol

	}

	@Override
	public void run() {
		
		synchronized(m_socketLock) {
			
			byte[] buffer = new byte[1024];  // buffer store for the stream
			byte[] resultBuff = new byte[0]; // the total data read so far
			int bytes = 0; // bytes returned from read()
			int totalBytes = 0;
			while (bytes != -1) { // bytes = -1 when done sending 
				try {
					// Read from the InputStream
					bytes = m_instream.read(buffer);
					totalBytes += bytes;
					byte[] tempBuff = new byte[totalBytes];
					System.arraycopy(resultBuff, 0, tempBuff, 0, resultBuff.length);
					System.arraycopy(buffer, 0, tempBuff, resultBuff.length, bytes); //copy the data to the buffer
					resultBuff = tempBuff; // reference the new buffer

				} catch (IOException e) {
					break;
				}
			}
			
			/*
			try {
				Object obj = m_objinstream.readObject();
				m_dataPackets.add(obj);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			*/
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
		synchronized(m_socketLock)
		{
			try {
				m_outstream.write(bytes);
			} catch (IOException e) { 
				e.printStackTrace();
			}
		}
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
