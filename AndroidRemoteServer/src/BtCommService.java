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

	InputStream mInputStream = null;

	
	/* Methods */
	
	public void init() throws BluetoothStateException, IOException {
		
		LocalDevice local = null;
		StreamConnectionNotifier notifier;
		StreamConnection connection = null;
		
        local = LocalDevice.getLocalDevice();
        local.setDiscoverable(DiscoveryAgent.GIAC);

        UUID uuid = new UUID(80087355); // "04c6093b-0000-1000-8000-00805f9b34fb"
        String url = "btspp://localhost:" + uuid.toString() + ";name=RemoteBluetooth";
        notifier = (StreamConnectionNotifier)Connector.open(url);
        
		Printing.info("Waiting for BT connection...", 0);
		// Below is blocking until connection is established.
		connection = notifier.acceptAndOpen();
		Printing.info("Connected.", 0);
		mInputStream = connection.openInputStream();
	}
	
	public byte[] receive() throws IOException{
		
		short len = 4*6;
		byte[] bMessage = new byte[len];
		
		// Below might not be the best implementation
		mInputStream.read(bMessage, 0, len);
		
		return bMessage;
	}
}
