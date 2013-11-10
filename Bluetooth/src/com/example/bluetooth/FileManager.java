package com.example.bluetooth;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

//writes to a byte array the contents of a File
public class FileManager {

    public static BTFile readFile(String file) throws IOException {
        return readFile(new File(file));
    }
    
    public static BTFile readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return new BTFile(file, data);
            
        } finally {
            f.close();
        }
    }	
    
    public static void writeFile(BTFile btFile, Activity activity) throws IOException {
    	
    	String root = Environment.getExternalStorageDirectory().toString();
    	//String path = root + btFile.fileName;
    	try {
    		File myDir = new File(root + "/bluetooth");
    		myDir.mkdirs();
    		System.out.println("MADE BLUETOOTH: " + myDir);
    		File file = new File(myDir, btFile.fileName);
    		if(file.exists())
    			file.delete();
        	System.out.println("file write!! "+file);

			FileOutputStream stream = new FileOutputStream (file);
			stream.write(btFile.contents);
			stream.flush();
			stream.close();
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));

    }
    
}
