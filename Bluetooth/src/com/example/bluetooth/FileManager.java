package com.example.bluetooth;
import java.io.*;

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
    
    public static void writeFile(BTFile btFile) throws IOException {
    	
    	String root = Environment.getExternalStorageDirectory().toString();
    	String path = root + btFile.fileName;
    	
    	try {
			FileOutputStream stream = new FileOutputStream (path);
			stream.write(btFile.contents);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
    }
    
}
