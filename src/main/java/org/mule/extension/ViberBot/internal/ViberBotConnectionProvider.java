package org.mule.extension.ViberBot.internal;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.PoolingConnectionProvider;
import org.mule.runtime.api.connection.ConnectionProvider;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.mule.runtime.api.connection.CachedConnectionProvider;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class (as it's name implies) provides connection instances and the funcionality to disconnect and validate those
 * connections.
 * <p>
 * All connection related parameters (values required in order to create a connection) must be
 * declared in the connection providers.
 * <p>
 * This particular example is a {@link PoolingConnectionProvider} which declares that connections resolved by this provider
 * will be pooled and reused. There are other implementations like {@link CachedConnectionProvider} which lazily creates and
 * caches connections or simply {@link ConnectionProvider} if you want a new connection each time something requires one.
 */
public class ViberBotConnectionProvider implements PoolingConnectionProvider<ViberBotConnection> {

  private final Logger LOGGER = LoggerFactory.getLogger(ViberBotConnectionProvider.class);
  
  @ParameterGroup(name="Connection")
  private ViberBotConfiguration configuration;

  @Override
  public ViberBotConnection connect() throws ConnectionException {
    return new ViberBotConnection(configuration);
  }

  @Override
  public void disconnect(ViberBotConnection connection) {
    try {
      connection.invalidate();
    } catch (Exception e) {
      LOGGER.error("Error while disconnecting: " + e.getMessage(), e);
    }
  }

  @Override
  public ConnectionValidationResult validate(ViberBotConnection connection) {
	  HttpURLConnection conn=connection.getConnection();	  
	  if(conn!=null) {
		  try {
			return (conn.getResponseCode()==200)?ConnectionValidationResult.success():
				  ConnectionValidationResult.failure("HTTP Test Failed", new Exception());
		} catch (IOException e) {
			LOGGER.info("An error occured during connection validation");
			e.printStackTrace();
		}
	  }
    return ConnectionValidationResult.failure("HTTP Test Failed", new Exception());
  }
}
