package ekrut.server.gui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import java.io.OutputStream;

/**
 * The Console class is an output stream that redirects the console output to a
 * TextArea in a JavaFX application.
 * 
 * @author Yovel Gabay
 */
public class Console extends OutputStream {
	private TextArea console;

	private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	private long sslException = 2;

	/**
	 * Constructor
	 * 
	 * @param console the text area to which the console output will be redirected
	 */
	public Console(final TextArea console) {
		this.console = console;
	}

	/**
	 * Appends text to the console TextArea
	 * 
	 * @param valueOf the text to be appended
	 */
	public void appendText(final String valueOf) {
		Platform.runLater(() -> this.console.appendText(valueOf));
	}

	/**
	 * Overrides the write method in the OutputStream class
	 * 
	 * @param b the integer to write to the stream
	 */
	@Override
	public void write(final int b) throws IOException {
		buffer.write(b);
	}

	/**
	 * Overrides the flush method in the OutputStream class
	 */
	@Override
	public void flush() {
		String s = buffer.toString();
		if (!s.contains("closing inbound before receiving peer's close_notify")) {
			if (sslException++ >= 2)
				appendText(s);
		} else {
			sslException = 0;
		}
		buffer.reset();
	}
}
