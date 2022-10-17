package org.mule.extension.httphandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class HttpHandlerImpl implements HttpHandler {
	
	//private HttpExchange exchange;
	private SourceCallback sourceCallback;
	
	public HttpHandlerImpl(SourceCallback sourceCallback) {
		this.sourceCallback=sourceCallback;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		// TODO Auto-generated method stub
		//this.exchange=exchange;
		SourceCallbackContext context=sourceCallback.createContext();
		context.addVariable("httpExchange", exchange);
		Result<InputStream, InputStream> result=Result.<InputStream, InputStream>builder()
				.attributes(null).output(exchange.getRequestBody())
				.mediaType(MediaType.APPLICATION_JSON).build();
		sourceCallback.handle(result, context);
		
	}
	
	public void sendResponse(HttpExchange exchange, String responseBody, int statusCode) {
		OutputStream out=exchange.getResponseBody();
		try {
			exchange.sendResponseHeaders(statusCode, responseBody.length());
			out.write(responseBody.getBytes());
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
