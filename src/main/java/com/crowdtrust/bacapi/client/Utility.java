package com.crowdtrust.bacapi.client;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Utility {
	
	public static String KEY_ALGORITHM = "HmacSHA256";
	public static String MD5_ALGORITHM = "MD5";
	
	
	//TODO: (Stephen) include this function in the documentation.
	/**
	 * The first method called to create a valid HMAC request.  The json content is
	 * converted to a Byte Array.  The byte array is then hashed by the digest.  The hashed
	 * byte array is then base64Encoded and returned as a String.  If contentToEncode is an empty 
	 * string it is assumed that the Http Verb is GET and function will return an empty String, not the
	 * hash of an empty String.
	 * @param contentToEncode
	 * @return
	 */
	public static String encodeToBase64MD5(String contentToEncode)  {
		if(contentToEncode == null || contentToEncode.isEmpty())
			return "";
		System.out.println("Content to encode: "+contentToEncode);
		String toRet = "";
		try{
			MessageDigest digest = MessageDigest.getInstance(MD5_ALGORITHM);
			digest.update(contentToEncode.getBytes());
			toRet = new String(Base64.encodeBase64(digest.digest()));
		} catch(NoSuchAlgorithmException ex) { }
		
		return toRet;
	}
	
	public static String encodeToBase64MD5Alt(String input) throws NoSuchAlgorithmException{
		String result = input;
	    if(input != null) {
	        MessageDigest md = MessageDigest.getInstance("MD5"); //or "SHA-1"
	        md.update(input.getBytes());
	        BigInteger hash = new BigInteger(1, md.digest());
	        result = hash.toString(16);
	        while(result.length() < 32) { //40 for SHA-1
	            result = "0" + result;
	        }
	    }
	    return result;
	}
	
	/**
	 * The second method called to create a valid HMAC Request.  This method converts
	 * the current time to a properly formatted Date.  An example of the date-time format 
	 * as specified by RFC 1123: Sun, 06 Nov 1994 08:49:37 GMT.
	 * @param date
	 * @return
	 */
	public static String convertTimeStamp(Date date){
		  Calendar calendar = Calendar.getInstance();
		    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		    return dateFormat.format(calendar.getTime());
	}
	/**
	 * The third method called to create a valid HMAC request.  Creates the (apiKey,Signature) pair that will be eventually assigned to the HTTP Authorization header.
	 * @param httpVerb
	 * @param contentMD5
	 * @param contentType
	 * @param timestamp
	 * @param path
	 * @param apiKey
	 * @param secret
	 * @return
	 */
	public static String createAuthHeader(String httpVerb, String contentMD5, String contentType, String timestamp, String path, String apiKey, String secret) {

		  // Generate 'toSign' String
		  String toSign = createHMACStringToSign(httpVerb, contentMD5, contentType, timestamp, path);
		  System.out.println("toSign: "+toSign);
		  // Build Java SecretKey for signing
		  SecretKey secretKey = buildSecretKey(secret);
		  
		  // Create Base64 Encoded, Hmac-SHA256 Hashed Signature using secret key
		  String signature = encodeToBase64HMAC(secretKey, toSign);
		  System.out.println("signature: "+signature);
		  String authHeader = apiKey + ":" + signature;

		  return authHeader;
		}
	
	public static String createHMACStringToSign(String httpVerb, String contentMD5, String contentType, String timestamp, String path) {

		  StringBuilder toSign = new StringBuilder();
		  toSign.append(httpVerb).append("\n")
		        .append(contentMD5).append("\n")
		        .append(contentType).append("\n")
		        .append(timestamp).append("\n")
		        .append(path);
		  System.out.println("ToSign: "+toSign);
		  return toSign.toString();
		}

		public static SecretKey buildSecretKey(String base64String) {
		  return new SecretKeySpec(base64String.getBytes(), KEY_ALGORITHM);
		}

		public static String encodeToBase64HMAC(SecretKey secretKey, String data) {

		  String toRet = "";

		  try {
		    Mac mac = Mac.getInstance("HmacSHA256");
		    mac.init(secretKey);
		    
		    byte[] rawHmac = mac.doFinal(data.getBytes());
		    System.out.println("RAW HMAC: "+Arrays.toString(rawHmac));
		    toRet = new String(Base64.encodeBase64(rawHmac));
		    System.out.println("Base64 encoded string: "+toRet);
		    
		    
		    //other test stuff
		    Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		    
		     sha256_HMAC.init(secretKey);
		    String hash = Base64.encodeBase64String(sha256_HMAC.doFinal(data.getBytes()));
		    System.out.println("Is this hash the same hash: "+hash+" toRet: "+toRet);
		  }
		  catch(NoSuchAlgorithmException ex) { }
		  catch(InvalidKeyException ex) { }

		  return toRet;
		}
}
