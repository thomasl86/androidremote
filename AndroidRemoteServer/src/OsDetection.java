
public class OsDetection {
	
	
	/* Members */
	
	private static String OS = System.getProperty("os.name").toLowerCase();
	public static final int OS_NIX = 0;
	public static final int OS_WIN = 1;
	public static final int OS_OSX = 2;
	public static final int OS_SOL = 3;

	
	/* Methods */
	
	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}

	public static boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}

	public static boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
	}

	public static boolean isSolaris() {
		return (OS.indexOf("sunos") >= 0);
	}
	
	/**
	 * Determines the operating system
	 * @return Returns identifiers for the detectable operating systems (see public members).
	 */
	public static int getOS(){
		if (isWindows()) {
			return OS_WIN;
		} else if (isMac()) {
			return OS_OSX;
        } else if (isUnix()) {
            return OS_NIX;
        } else if (isSolaris()) {
            return OS_SOL;
        } else {
            return -1;
        }
    }
}
