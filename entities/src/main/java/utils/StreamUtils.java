package utils;

import java.io.*;
import java.net.*;


public final class StreamUtils {
	
	private StreamUtils() {}

	public static String read(InputStream in) throws UnsupportedEncodingException, IOException {

		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));

		String line;
		StringBuilder builder = new StringBuilder();

		while ((line = reader.readLine()) != null) {
			builder.append(line).append("\r\n");
		}
		reader.close();
		String text = builder.toString();
		return text;
	}

	public static String get(String adress) throws UnsupportedEncodingException, IOException {
		URL url = new URL(adress);
		URLConnection connection = url.openConnection();
		InputStream in = connection.getInputStream();
		String text = read(in);
		return text;
	}

	public static void write(OutputStream out, String Output) throws UnsupportedEncodingException, IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		writer.write(Output);
		writer.flush();
		out.flush();
		out.close();
		writer.close();
	}

	public static String post(String adress, String Output) throws MalformedURLException, IOException, UnsupportedEncodingException {
		URL url = new URL(adress);

		URLConnection connection = url.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);

		write(connection.getOutputStream(), Output);

		String input = read(connection.getInputStream());
		return input;
	}
}