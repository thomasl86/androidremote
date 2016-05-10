
public class Utils {

	public static void waitMs(int timeMs){
		long time = System.currentTimeMillis();
		long dt = 0;
		while (dt < timeMs){
			dt = System.currentTimeMillis() - time;
		}
	}
}
