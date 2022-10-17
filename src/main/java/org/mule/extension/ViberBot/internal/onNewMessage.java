package org.mule.extension.ViberBot.internal;

import org.mule.extension.httphandler.HttpHandlerImpl;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.extension.api.annotation.execution.OnError;
import org.mule.runtime.extension.api.annotation.execution.OnSuccess;
import org.mule.runtime.extension.api.annotation.execution.OnTerminate;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Content;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.source.EmitsResponse;
import org.mule.runtime.api.message.Error;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;
import static org.mule.runtime.extension.api.annotation.param.MediaType.APPLICATION_JSON;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;
import org.mule.runtime.extension.api.runtime.source.SourceResult;
import org.mule.runtime.http.api.domain.request.HttpRequestContext;
import org.mule.runtime.http.api.server.async.HttpResponseReadyCallback;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

@EmitsResponse
@MediaType(value=APPLICATION_JSON)
public class onNewMessage extends Source<InputStream, InputStream> {
	
	@Config
	private ViberBotConfiguration configuration;
	
	@Parameter
	@Optional(defaultValue = "/")
	private String path;
	
	private HttpServer httpServer;
	
	private HttpHandlerImpl httpHandler;

	@Override
	public void onStart(SourceCallback arg0) throws MuleException {
		// TODO Auto-generated method stub
		/*httpServer=connectionProvider.connect();
		httpServer.addRequestHandler(path, new RequestHandler() {

			@Override
			public void handleRequest(HttpRequestContext requestContext, HttpResponseReadyCallback responseCallback) {
				// TODO Auto-generated method stub
			}});*/
		httpHandler=new HttpHandlerImpl(arg0);
		try {
			httpServer=HttpServer.create(new InetSocketAddress("localhost", 
					configuration.getPort()), 0);
			httpServer.createContext(path, httpHandler);
	        httpServer.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		if(httpServer!=null) {
			httpServer.stop(0);
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
