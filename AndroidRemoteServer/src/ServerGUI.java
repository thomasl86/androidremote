
import java.awt.*;        // Using AWT container and component classes
import java.awt.event.*;  // Using AWT event classes and listener interfaces

class ServerGUI extends Frame implements ActionListener, WindowListener {

	/* Members */
	
	private static final long serialVersionUID = 1L;
	// Buttons
	private Button mBtnStart;
	private Button mBtnStop;
	private Button mBtnVerbosity; 
	private boolean mBoIsVerbose = false;
	private static TextArea mTaDisplay;
	
	
	/* Constructors */
	
	public ServerGUI(){
		setLayout(new FlowLayout());
		
		mBtnStart = new Button("Start");
		add(mBtnStart);
		mBtnStart.addActionListener(this);
		/*
		mBtnStop = new Button("Stop");
		add(mBtnStop);
		mBtnStop.addActionListener(this);
		*/
		mBtnVerbosity = new Button("Toggle verbosity");
		add(mBtnVerbosity);
		mBtnVerbosity.addActionListener(this);
		
		mTaDisplay = new TextArea(8, 50); // 8 rows, 50 columns
		add(mTaDisplay);
		
		setTitle("AndroidRemoteServer");
		setSize(450, 200);
	    setVisible(true);      
	    
	    addWindowListener(this);
	}
	
	
	/* Methods */
	
	public static void print(String stMessage){
		if (mTaDisplay != null){
			mTaDisplay.append("\n"+stMessage);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == mBtnStart){
			AndroidRemoteServer.startServerThread();
		}
		else if (e.getSource() == mBtnStop){
			AndroidRemoteServer.stopServerThread();
		}
		else if (e.getSource() == mBtnVerbosity){
			mBoIsVerbose = !mBoIsVerbose;
			Printing.setVerbosity(mBoIsVerbose);
			if(mBoIsVerbose) Printing.info("Set to verbose", 0);
			else Printing.info("Set to none-verbose", 0);
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void windowClosing(WindowEvent e) {
		dispose();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		AndroidRemoteServer.stopServerThread();
		System.exit(0);
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}