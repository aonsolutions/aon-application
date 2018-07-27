package net.aonsolutions.rest.api.function;

import java.util.Map;

import com.amazonaws.util.StringUtils;

import net.aonsolutions.rest.api.model.ServerlessInput;

public class CORSUtils {

	public static String getOrigin(ServerlessInput serverlessInput ) {
		Map<String,String >headers = serverlessInput.getHeaders();
	
		String origin =headers.get("origin");
		if ( StringUtils.isNullOrEmpty(origin) )
			origin =headers.get("Origin");
		
		return origin;
	}
	
	public static void addOriginHeader(ServerlessInput serverlessInput, Map<String,String> headers) {
		headers.put("Access-Control-Allow-Origin", getOrigin(serverlessInput));
	}

}
