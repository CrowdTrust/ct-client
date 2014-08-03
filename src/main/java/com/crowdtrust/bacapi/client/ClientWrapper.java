package com.crowdtrust.bacapi.client;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Run the client from command line
 * @author cerberus
 *
 */
public class ClientWrapper {

	static Logger logger = LoggerFactory.getLogger(ClientWrapper.class);
	
	public static void main(String[] args) throws IOException {
		logger.info("Running ct client...");
		
		if(StringUtils.equalsIgnoreCase(args[0], "account:create")){
			logger.info("Executing account create.");
			CTClient client = new CTClient();
			String content = FileUtils.readFileToString(new File(args[1]));
			client.executeAccountRequest("https://www.crowdtrust.com/bacapi/account", content);
		}
		else
		if(StringUtils.equalsIgnoreCase(args[0], "execute")){
			String verb = args[1];
			if(!verb.equalsIgnoreCase("POST") && !verb.equalsIgnoreCase("GET")){
				throw new IllegalArgumentException("(Invalid verb.");
			}
			else
				if(verb.equalsIgnoreCase("POST")){
					String content = FileUtils.readFileToString(new File(args[2]));
					String url = args[3];
					String apiKey = args[4];
					String secretKey = args[5];
					CTClient client = new CTClient(apiKey, secretKey);
					client.execute(verb, content, url);
				}
				else 
					if(verb.equalsIgnoreCase("GET")){
						String url = args[2];
						String apiKey = args[3];
						String secretKey = args[4];
						CTClient client = new CTClient(apiKey, secretKey);
						client.execute(verb, "", url);
				}
		}
	logger.info("Execution complete...");

	}
}
