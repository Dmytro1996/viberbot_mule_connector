package org.mule.extension.ViberBot.internal;

import org.mule.runtime.extension.api.annotation.Expression;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.Sources;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Placement;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

/**
 * This class represents an extension configuration, values set in this class are commonly used across multiple
 * operations since they represent something core from the extension.
 */
@Operations(ViberBotOperations.class)
@Sources(onNewMessage.class)
@ConnectionProviders(ViberBotConnectionProvider.class)
public class ViberBotConfiguration {
  
  @Parameter
  @Placement(tab="General")
  @DisplayName("Token")
  private String token;
  
  @Parameter
  @Placement(tab="General")
  @DisplayName("Avatar URL")
  @Optional
  private String avatarUrl;
  
  @Parameter
  @Placement(tab="General")
  @DisplayName("Display name")
  @Optional
  private String displayName;
  
  @Parameter
  @Placement(tab="General")
  @DisplayName("Webhook port")
  private int port;
  
  @Parameter
  @Placement(tab="General")
  @DisplayName("Keystore Path")
  private String keystore;
  
  @Parameter
  @Placement(tab="General")
  @DisplayName("Keystore Password")
  private String keystorePass;
  
  @Parameter
  @Placement(tab="General")
  @DisplayName("Keystore Type")
  @Summary("Options: JKS, JCEKS, PKCS12")
  @Expression(org.mule.runtime.api.meta.ExpressionSupport.NOT_SUPPORTED)
  private String keystoreType;
  
  @Parameter
  @Placement(tab="General")
  @DisplayName("Webhook Host")
  private String webhookHostName;
  
  public String getToken(){
	    return token;
  }
  
  public String getAvatarUrl(){
	    return avatarUrl;
  }
  
  public String getDisplayName(){
	    return displayName;
  }
  
  public int getPort() {
	  return port;
  }
  
  public String getKeystore(){
	    return keystore;
  }
  
  public String getKeystorePass(){
	    return keystorePass;
  }
  
  public String getKeystoreType(){
	  return keystoreType;
  }
  
  public String getWebhookHostName() {
	  return webhookHostName;
  }
}
