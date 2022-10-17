package org.mule.extension.ViberBot.internal;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;


/**
 * This class is a container for operations, every public method in this class will be taken as an extension operation.
 */
public class ViberBotOperations {
	
  private static final Logger logger=LoggerFactory.getLogger(ViberBotOperations.class);
  
  @MediaType(value = ANY, strict = false)
  public InputStream getSubscribers(@Config ViberBotConfiguration configuration) {
	  try {
		  URL url = new URL("https://chatapi.viber.com/pa/get_account_info");
		  HttpURLConnection conn=(HttpURLConnection)url.openConnection();
		  conn.setRequestProperty("X-Viber-Auth-Token", 
                  configuration.getToken());
		  return conn.getInputStream();
	  } catch (IOException e) {
		  logger.info("An error occured during retrieaval of subscribers.");
		  e.printStackTrace();
		  return null;
	  } 
  }
  
  @MediaType(value = ANY, strict = false)
  public InputStream sendMessage(@Config ViberBotConfiguration configuration, 
		  String receiverId, String message) {
	  try {
		  URL url=new URL("https://chatapi.viber.com/pa/send_message");
		  HttpURLConnection conn=(HttpURLConnection)url.openConnection();
		  conn.setRequestProperty("X-Viber-Auth-Token", 
                  configuration.getToken());
		  conn.setDoOutput(true);
		  byte[] input=("{\n" +
	                "\"receiver\": \"" + receiverId + "\",\n" +
	                "\"min_api_version\":1,\n" +
	                "\"sender\":{\n" +
	                "\"name\":\"" + configuration.getDisplayName() + "\",\n" +
	                "\"avatar\":\"" + configuration.getAvatarUrl() + "\"\n" +
	                "},\n" +
	                "\"tracking_data\":\"tracking data\",\n" +
	                "\"type\":\"text\",\n" +
	                "\"text\":\"" + message + "\"\n" +
	                "}").getBytes("utf-8");
	            conn.getOutputStream().write(input,0,input.length);
		  conn.getOutputStream().write(input);
		  return conn.getInputStream();
	  } catch(IOException e) {
		  logger.info("An error occured during message send.");
		  e.printStackTrace();
	  }
	  return null;
  }
  
  @MediaType(value= ANY, strict=false)
  public void broadcast(@Config ViberBotConfiguration configuration, String message) {
	  StringBuilder accountInfo=new StringBuilder();
	  BufferedReader br=new BufferedReader(
			  new InputStreamReader(getSubscribers(configuration)));
	  String line="";
	  try {
	      while((line=br.readLine())!=null) {
		      accountInfo.append(line);
	      }
	      JSONArray members=new JSONObject(accountInfo.toString()).getJSONArray("members");
	      for(Object member:members) {
	    	  sendMessage(configuration, ((JSONObject)member).getString("id"), message);
	      }
	      br.close();
	  } catch(IOException e) {
		  logger.info("Something went wrong while broadcasting messages");
		  e.printStackTrace();
	  }
  }
}
