package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletResponse;

import results.Result;

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
	
	public static void setResponseStatus(Result result, HttpServletResponse response,
			int successTrueStatus, int successFalseStatus) {
		final int status = result.isSuccess() ? successTrueStatus : successFalseStatus;
		response.setStatus(status);
	}
}