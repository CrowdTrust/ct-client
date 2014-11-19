package com.crowdtrust.bacapi.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class CTClient {
	
	Logger logger = LoggerFactory.getLogger(CTClient.class);
	
	private String apiKey;
	private String secretKey;
	
	public  Date requestTimeStamp;
	public static String CONTENT_TYPE = "application/json";
	
	public CTClient(){
		
	}
	public CTClient(String apiKey, String secretKey){
		this.apiKey = apiKey;
		this.secretKey = secretKey;
	}
	
	/**
	 * For command line use
	 * @param url
	 * @param content
	 * @return
	 */
	public String executeAccountRequest(String url, String content){
		 String result = "";
		 Client client = Client.create();
		 System.out.println("content: "+content+"url: "+url);
		 ClientResponse r = client.resource(url).type("application/json").accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, content);
		 logger.info("Received response from server.");
		 result = r.getEntity(String.class);
		
		logger.info("result: "+result);
		return result;
	}
	/**
	 * For command line use
	 * @param verb
	 * @param content
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 * @throws NoSuchAlgorithmException 
	 */
	public String execute(String verb, String content, String url ) throws MalformedURLException, NoSuchAlgorithmException{
		logger.info(">>> Executing request with content: "+content);
		//Step 1: generate content MD5
		String contentMd5 =  Utility.encodeToBase64MD5(content);
		
		//NOTE: if the verb is a get we only need to send an empty string, not the hash of an empty string
		if(verb.equalsIgnoreCase("GET")){
			contentMd5 = "";
		}
		logger.info(">>> Created contentMd5: "+contentMd5);
		
		//Step 2: generate the date
		this.requestTimeStamp = new Date();
		String currTimeStamp = Utility.convertTimeStamp(this.requestTimeStamp);
		logger.info(">>> Created time stamp: "+currTimeStamp);
		
		//currTimeStamp = "Tue, 11 Nov 2014 18:49:00 GMT";
		
		//Step 3: extract the Path
		// Extract the path of the URL
		URL requestUrl = new URL(url);
		String path = requestUrl.getPath();
		logger.info(">>> Extracted path: "+path);
		
		//Step 4: create the authorizationHeader.  Generates a string in the format of apiKey:signature
		String tempContentType = "";
		 if(verb.equalsIgnoreCase("POST")){
			 tempContentType = CONTENT_TYPE;
		 }
		 
		String authorizationHeader = Utility.createAuthHeader(verb, contentMd5, tempContentType, currTimeStamp, path, apiKey, secretKey);
		logger.info(">>> Authorization Header: "+authorizationHeader);
		
		//Step 5: create request
		 Client client = Client.create();
		 WebResource.Builder builder = client.resource(url)
		        .header("Date",  currTimeStamp)
		        .header("Authorization", authorizationHeader)
		 		.header("Content-MD5", contentMd5);

		 
		 
		 ClientResponse response = null;
		  if (StringUtils.equalsIgnoreCase(verb, "POST")) {
			  builder = builder.type(tempContentType);
			  response = builder.post(ClientResponse.class, content);
		  }
		  else
			  if(StringUtils.equalsIgnoreCase(verb, "GET")){
				  response = builder.get(ClientResponse.class);
			  }
		  else{
			  throw new RuntimeException("This sample client only does POST/GET methods.");
		  }
		
		  String result = response.getEntity(String.class);
		  logger.info(">>> result: "+result);
		return result;
	}
	
}
