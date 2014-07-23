package com.crowdtrust.bacapi.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
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
	
	public String execute(String verb, String content, String url ) throws MalformedURLException{
		logger.info(">>> Executing request with content: "+content);
		//Step 1: generate content MD5
		String contentMd5 =  Utility.encodeToBas64MD5(content);
		logger.info(">>> Created contentMd5: "+contentMd5);
		
		//Step 2: generate the date
		this.requestTimeStamp = new Date();
		String currTimeStamp = Utility.convertTimeStamp(this.requestTimeStamp);
		logger.info(">>> Created time stamp: "+currTimeStamp);
		
		//Step 3: extract the Path
		// Extract the path of the URL
		URL requestUrl = new URL(url);
		String path = requestUrl.getPath();
		logger.info(">>> Extracted path: "+path);
		
		//Step 4: create the authorizationHeader.  Generates a string in the format of apiKey:signature
		String authorizationHeader = Utility.createAuthHeader(verb, contentMd5, CONTENT_TYPE, currTimeStamp, path, apiKey, secretKey);
		logger.info(">>> Authorization Header: "+authorizationHeader);
		
		//Step 5: send request
		 Client client = Client.create();
		 WebResource.Builder builder = client.resource(url)
		        .header("Date",  currTimeStamp)
		        .header("Authorization", authorizationHeader)
		 		.header("Content-MD5", contentMd5);

		 
		 builder = builder.type(CONTENT_TYPE);
		 
		 ClientResponse response = null;

		  if (StringUtils.equalsIgnoreCase(verb, "POST")) {
		    response = builder.post(ClientResponse.class, content);
		  }
		  else{
			  throw new RuntimeException("This sample client only does POST");
		  }
		
		  String result = response.getEntity(String.class);
		  logger.info(">>> result: "+result);
		return result;
	}

}
