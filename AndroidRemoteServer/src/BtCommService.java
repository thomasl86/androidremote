import java.io.IOException;
import java.io.InputStream;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;


public class BtCommService {

	
	/* Members */

	private InputStream mInputStream 			= null;
	private StreamConnection mConnection 		= null;
	private StreamConnectionNotifier mNotifier 	= null;

	
	/* Methods */
	
	public void init() throws BluetoothStateException, IOException {
		
		LocalDevice local = null;
		
        local = LocalDevice.getLocalDevice();
        local.setDiscoverable(DiscoveryAgent.GIAC);

        UUID uuid = new UUID(80087355); // "04c6093b-0000-1000-8000-00805f9b34fb"
        String url = "btspp://localhost:" + uuid.toString() + ";name=RemoteBluetooth";
        mNotifier = (StreamConnectionNotifier)Connector.open(url);
        
        if (mInputStream == null)
        	Printing.info("Waiting for BT connection...", 0);
        else
        	Printing.info("Waiting for BT connection...", 1);
        
		// Below is blocking until connection is established.
		mConnection = mNotifier.acceptAndOpen();
		
		if (mInputStream == null)
			Printing.info("Connected.", 0);
		else
			Printing.info("Connected.", 1);
		
		mInputStream = mConnection.openInputStream(); 
	}
	
	
	public byte[] receive() throws IOException{
		
		short len = 4*6;
		byte[] bMessage = new byte[len];
		
		// Below might not be the best implementation
		mInputStream.read(bMessage, 0, len);
		
		return bMessage;
	}
	
	
	public void close() throws IOException{
		mNotifier.close();
		mConnection.close();
	}
}
