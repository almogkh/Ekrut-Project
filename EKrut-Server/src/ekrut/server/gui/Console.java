package ekrut.server.gui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import java.io.OutputStream;

public class Console extends OutputStream {
	private TextArea console;
	
	private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	private long sslException = 2;

	public Console(final TextArea console) {
		this.console = console;
	}

	public void appendText(final String valueOf) {
		Platform.runLater(() -> this.console.appendText(valueOf));
	}

	@Override
	public void write(final int b) throws IOException {
		buffer.write(b);
	}
	
	@Override
	public void flush() {
		String s = buffer.toString();
		// Fixes console being flooded by messages due to a Java/MySQL Connector bug.
		// See also:
		// https://bugs.mysql.com/bug.php?id=93590
		// https://github.com/netty/netty/blob/41b02368153af86b1ddb19020ebf5e4f7c69aecd/handler/src/main/java/io/netty/handler/ssl/SslHandler.java#L1779-L1786
		if (!s.contains("closing inbound before receiving peer's close_notify")) {
			if (sslException++ >= 2)
				appendText(s);
		} else {
			sslException = 0;
		}
		buffer.reset();
	}
}
