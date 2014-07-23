package com.crowdtrust.bacapi.client;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
			String content = FileUtils.readFileToString(new File(args[2]));
			String verb = args[1];
			String url = args[3];
			String apiKey = args[4];
			String secretKey = args[5];
			CTClient client = new CTClient(apiKey, secretKey);
			client.execute(verb, content, url);
		}
	logger.info("Execution complete...");

	}
}
