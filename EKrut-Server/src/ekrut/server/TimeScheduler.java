package ekrut.server;

import java.util.Timer;

/**
 * This class wraps a single global {@link Timer} that's used to schedule time related tasks.
 * @author almog
 *
 */
public final class TimeScheduler {
	
	// Ensure no instances are created
	private TimeScheduler() {
	}

	private static Timer timer;
	
	/**
	 * Starts the timer thread. This must be called once before the timer can be used.
	 */
	public static void startTimer() {
		if (timer != null)
			return;
		timer = new Timer();
	}
	
	/**
	 * Stops the timer thread and cancels any pending scheduled tasks.
	 * This should be called before shutdown since the timer thread is running as a non-daemon
	 * thread which means it can hang the program if not stopped explicitly.
	 */
	public static void stopTimer() {
		if (timer == null)
			return;
		timer.cancel();
		timer = null;
	}
	
	/**
	 * Retrieves the timer instance.
	 * 
	 * @return the timer instance
	 */
	public static Timer getTimer() {
		return timer;
	}
}
