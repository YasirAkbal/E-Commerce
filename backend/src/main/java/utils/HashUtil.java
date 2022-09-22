package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashUtil {
	
	private static final String SHA3_256 = "SHA3-256";
	
	private HashUtil() {}
	
	private static String bytesToHex(byte[] hash) {
	    StringBuilder hexString = new StringBuilder(2 * hash.length);
	    for (int i = 0; i < hash.length; i++) {
	        String hex = Integer.toHexString(0xff & hash[i]);
	        if(hex.length() == 1) {
	            hexString.append('0');
	        }
	        hexString.append(hex);
	    }
	    return hexString.toString();
	}
	
	public static String toSha3_256(String text) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance(SHA3_256);
		} catch (NoSuchAlgorithmException e) { //bu catchin calismasi mumkun degil
			e.printStackTrace();
			return null;
		}
		
		final byte[] hashbytes = digest.digest(
				text.getBytes(StandardCharsets.UTF_8));
		String sha3Hex = bytesToHex(hashbytes);
		
		return sha3Hex;
	}
}
