package ekrut.server.gui;

import java.io.IOException;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import java.io.OutputStream;

public class Console extends OutputStream {
	private TextArea console;

	public Console(final TextArea console) {
		this.console = console;
	}

	public void appendText(final String valueOf) {
		Platform.runLater(() -> this.console.appendText(valueOf));
	}

	@Override
	public void write(final int b) throws IOException {
		this.appendText(String.valueOf((char) b));
	}
}
