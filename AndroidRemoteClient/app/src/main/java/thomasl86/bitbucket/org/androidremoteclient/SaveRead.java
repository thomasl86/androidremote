package thomasl86.bitbucket.org.androidremoteclient;

/**
 * Code taken from:
 * http://www.juje.me/2011/03/reading-and-writing-objects-with-android/
 *
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Calendar;



import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class SaveRead<T>{
	
	public static boolean saveFile(Context context, Calendar calendar, String fileName){

		// check if available and not read only
		if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
			Log.w("FileUtils", "Storage not available or read only");
			return false;
		}
	
		// Create a path where we will place our List of objects on external storage
		File file = new File(context.getExternalFilesDir(null), fileName);
		ObjectOutputStream oos = null;
		boolean success = false;
	
		try {
			OutputStream os = new FileOutputStream(file);
			oos = new ObjectOutputStream(os);
			oos.writeObject(calendar);
			success = true;
		} catch (IOException e) {
			Log.w("FileUtils",
					"Error writing "
							+ file, e);
		} catch (Exception e) {
			Log.w("FileUtils", "Failed to save file", e);
		} finally {
			try {
				if (null != oos)
					oos.close();
			} 
			catch (IOException ex) {
			}
		}
	
		return success;
	}
	
	public boolean saveFile(Context context, T configs, String fileName) {

		// check if available and not read only
		if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
			Log.w("FileUtils", "Storage not available or read only");
			return false;
		}
	
		// Create a path where we will place our List of objects on external storage
		File file = new File(context.getExternalFilesDir(null), fileName);
		ObjectOutputStream oos = null;
		boolean success = false;
	
		try {
			OutputStream os = new FileOutputStream(file);
			oos = new ObjectOutputStream(os);
			oos.writeObject(configs);
			success = true;
		} catch (IOException e) {
			Log.w("FileUtils",
					"Error writing "
							+ file, e);
		} catch (Exception e) {
			Log.w("FileUtils", "Failed to save file", e);
		} finally {
			try {
				if (null != oos)
					oos.close();
			} 
			catch (IOException ex) {
			}
		}
	
		return success;
	}

	@SuppressWarnings("unchecked")
	public T readFile(Context context, String filename) {

	    ObjectInputStream ois = null;
	    T result = null;

		if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
		{
			Log.w("FileUtils", "Storage not available or read only");
			return null;
		}

	    try
	    {
	    	File file = new File(context.getExternalFilesDir(null),
	    			filename);
			FileInputStream fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			result =  (T) ois.readObject();
			ois.close();
	    }
	    catch (Exception ex) {
	        Log.e("SaveRead", "failed to load file", ex);
	    }
	    finally {
	        try {if (null != ois) ois.close();} catch (IOException ex) {}
	    }

	    return result;
	}

	
	public static Calendar readCalendar(Context context, String filename) {

	    ObjectInputStream ois = null;
	    Calendar result = null;

		if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
		{
			Log.w("FileUtils", "Storage not available or read only");
			return null;
		}

	    try
	    {
	    	File file = new File(context.getExternalFilesDir(null),
	    			filename);
			FileInputStream fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			result =  (Calendar) ois.readObject();
			ois.close();
	    }
	    catch (Exception ex) {
	        Log.e("FileUtils", "failed to load file", ex);
	    }
	    finally {
	        try {if (null != ois) ois.close();} catch (IOException ex) {}
	    }

	    return result;
	}

	public static boolean isExternalStorageReadOnly() {
		String extStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
			return true;
		}
		return false;
	}

	public static boolean isExternalStorageAvailable() {
		String extStorageState = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
			return true;
		}
		return false;
	}

}
