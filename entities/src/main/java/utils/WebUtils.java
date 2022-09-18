package utils;

import java.io.*;
import java.net.*;

public final class WebUtils {
	
	private WebUtils() {}
	
	public static URLConnection connect(String adress) throws IOException {
		URL url = new URL(adress);

		URLConnection connection = url.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);

		return connection;
	}
	
	public static InputStream get(String adress) throws MalformedURLException, IOException {
		InputStream in = new URL(adress).openStream();
		return in;
	}
}
