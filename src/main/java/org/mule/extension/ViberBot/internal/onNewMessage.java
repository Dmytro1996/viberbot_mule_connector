package org.mule.extension.ViberBot.internal;

import org.mule.extension.httphandler.HttpHandlerImpl;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.extension.api.annotation.execution.OnError;
import org.mule.runtime.extension.api.annotation.execution.OnSuccess;
import org.mule.runtime.extension.api.annotation.execution.OnTerminate;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.source.EmitsResponse;
import org.mule.runtime.api.message.Error;

import static org.mule.runtime.extension.api.annotation.param.MediaType.APPLICATION_JSON;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;
import org.mule.runtime.extension.api.runtime.source.SourceResult;
import com.sun.net.httpserver.HttpsServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;

@EmitsResponse
@MediaType(value=APPLICATION_JSON)
public class onNewMessage extends Source<InputStream, InputStream> {
	
	@Config
	private ViberBotConfiguration configuration;
	
	@Parameter
	@Optional(defaultValue = "/")
	private String path;
	
	private HttpsServer httpsServer;
	
	private HttpHandlerImpl httpHandler;

	@Override
	public void onStart(SourceCallback arg0) throws MuleException {
		httpHandler=new HttpHandlerImpl(arg0);
		try {
			httpsServer=HttpsServer.create(new InetSocketAddress("localhost", 
					configuration.getPort()), 0);
			SSLContext sslContext=SSLContext.getInstance("TLS");
			char[] keystorePass=configuration.getKeystorePass().toCharArray();
			KeyStore keystore=KeyStore.getInstance(configuration.getKeystoreType());
			keystore.load(new FileInputStream(configuration.getKeystore()), keystorePass);
			KeyManagerFactory keyFactory=KeyManagerFactory.getInstance("SunX509");
			keyFactory.init(keystore, keystorePass);
			sslContext.init(keyFactory.getKeyManagers(), null, null);
			httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
				public void configure(HttpsParameters params){
			        SSLContext context=getSSLContext();
			        SSLEngine sslEngine=context.createSSLEngine();
			        params.setNeedClientAuth(false);
			        params.setCipherSuites(sslEngine.getEnabledCipherSuites());
			        params.setProtocols(sslEngine.getEnabledProtocols());
			        params.setSSLParameters(context.getSupportedSSLParameters());
				}
			});
			httpsServer.createContext("/callback", httpHandler);
	        httpsServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}        
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		if(httpsServer!=null) {
			httpsServer.stop(0);
		}
	}
	
	@OnSuccess
	public void onSuccess(SourceCallbackContext context) {
	    HttpExchange exchange=(HttpExchange) context.getVariable("httpExchange").get();
		httpHandler.sendResponse(exchange, 
				"{\"message\":\"Message received and will be processed soon\"}", 
				200);
	}
	
	@OnError
	public void onError(SourceCallbackContext context, Error error) {
		HttpExchange exchange=(HttpExchange)context.getVariable("httpExchange").get();
		String errorMessage="{ \"errorMessage\":" + error.getDescription() + "}";
		httpHandler.sendResponse(exchange, errorMessage, 500);
	}
	
	@OnTerminate
	public void onTerminate(SourceResult sourceResult) {
		boolean sendingResponse = (Boolean) sourceResult.getSourceCallbackContext()
				.getVariable("responseSendAttempt").orElse(false);
	    if (!sendingResponse) {
	      sourceResult
	          .getInvocationError()
	          .ifPresent(error -> { 
	        	  onError(sourceResult.getSourceCallbackContext(), error);
	        	  });
	    }
	}

}
