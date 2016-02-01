

public class Printing {

	
	/* Members */
	
	private static boolean mBoVerbose = false;

	
	/* Methods */
	
	public static void error(String msg)
   	{
       	System.out.println("ERROR: " + msg);
   	}
	

   	/**
	* Simple function to echo info to terminal. 
	* Verbose parameter can be set to 0 or >0 to indicate different verbose levels.
	* 0 means always print, 1 means only print if verbose is set to on. 
	* @param msg The message to print.
	* @param chVerbosity Verbose message or not
	*/
	public static void info(String msg, int verbosity)
	{
	   if ((verbosity > 0) && mBoVerbose){
		   System.out.println("INFO: " + msg);
	   }
	   else if(verbosity == 0){
		   System.out.println("INFO: " + msg);
	   }
	}
	
	public static void setVerbosity(boolean boVerbose){ mBoVerbose = boVerbose; }
}
