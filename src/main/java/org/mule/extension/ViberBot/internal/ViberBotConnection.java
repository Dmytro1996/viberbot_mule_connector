package org.mule.extension.ViberBot.internal;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents an extension connection just as example (there is no real connection with anything here c:).
 */
public final class ViberBotConnection {
  
  private HttpURLConnection connection;
  
  
  private static Logger logger=LoggerFactory.getLogger(ViberBotConnection.class); 

  public ViberBotConnection(/*String id*/ViberBotConfiguration configuration) {
    //this.id = id;	
    try {
		URL url=new URL("https://chatapi.viber.com/pa/");
		connection=(HttpURLConnection)url.openConnection();
		logger.info(configuration.getToken());
		connection.setRequestProperty("X-Viber-Auth-Token", configuration.getToken());
	} catch(IOException e) {
		logger.info("An error occured during connection creation");
		e.printStackTrace();
	}
  }
  
  public HttpURLConnection getConnection() {
	  return connection;
  }

  public void invalidate() {
	  if(connection!=null) {
		  logger.info("Invalidating HttpURLConnection");
		  connection.disconnect();
		  logger.info("HttpURLConnection invalidated");
	  }
  }
}
