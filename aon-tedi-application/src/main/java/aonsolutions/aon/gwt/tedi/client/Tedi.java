package aonsolutions.aon.gwt.tedi.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xhr.client.XMLHttpRequest;


public class Tedi {
	
	private static class DataUrl {
		/**
	     * Payload of this data url
	     */
	    private final String data;
	    
	    /**
	     * Encoding method
	     */
	    private final String encoding;

	    /**
	     * MIME-Type of this data urls content
	     */
	    private final String mimeType;

	    /**
	     * Headers/parameters of this data url
	     */
	    private final Map<String, String> headers;

		private DataUrl(String data, String encoding, String mimeType, Map<String, String> headers) {
			this.data = data;
			this.encoding = encoding;
			this.mimeType = mimeType;
			this.headers = headers;
		}
	    
	    
	}
	
    

	public  static void parse(String urlString, AsyncCallback<JsInvoice> callback) {		
		try {
			DataUrl dataUrl = parse(urlString);
			
			XMLHttpRequest request = XMLHttpRequest.create();
			request.setOnReadyStateChange( xhr -> {
				
				
				int state = xhr.getReadyState();
				
				if (state != XMLHttpRequest.DONE)
					return;

				int status = xhr.getStatus();
				
				// Successful 2xx
				if (status >= 200 && status < 300)
					callback.onSuccess(JsonUtils.safeEval(xhr.getResponseText()));
				else
					callback.onFailure(new RuntimeException(xhr.getResponseText()));
				
				
			});
            request.open("POST", "https://55evus1cy8.execute-api.eu-west-1.amazonaws.com/default/invoice/parse");
            request.setRequestHeader("Content-Type", "application/base64");
            request.send(dataUrl.data);
			
			
		} catch (NullPointerException | IllegalArgumentException  e) {
			callback.onFailure(e);
		}
	
	}
	
	
	//
	//
	//    The "data" URL scheme
	//
	//    dataurl    := "data:" [ mediatype ] [ ";base64" ] "," data
	//    mediatype  := [ type "/" subtype ] *( ";" parameter )
	//    data       := *urlchar
	//    parameter  := attribute "=" value
    private static DataUrl parse ( String urlString ) {
        
    	if (urlString == null) {
            throw new NullPointerException();
        }

        String data = null;
        String mimeType = null;
        HashMap<String, String> headers = new HashMap<>();
        

        if (!urlString.startsWith("data:")) {
            throw new IllegalArgumentException("Wrong protocol");
        }

        int colon = urlString.indexOf(':');
        int comma = urlString.indexOf(',');

        String metaString = urlString.substring(colon + 1, comma);
        String dataString = urlString.substring(comma + 1);
        String encoding = "";

        String[] metaArray = metaString.split(";");
        for (int i = 0; i < metaArray.length; i++) {
            String meta = metaArray[i];
            if (i == 0) {
                if (meta.matches("^[a-z\\-0-9]+\\/[a-z\\-0-9]+$")) {
                    mimeType = meta;
                    continue;
                }
            }

            if (i + 1 == metaArray.length) {
                if (meta.indexOf('=') == -1) {
                    encoding = meta;
                    continue;
                }
            }

            int equals = meta.indexOf('=');
            if (equals < 1) {
            }

            String name = meta.substring(0, equals);
            String value = meta.substring(equals + 1);

            headers.put(name, decode(value));
        }
        
//        IEncoder encoder = getAppliedEncoder(encoding);
//        String appliedCharset = getAppliedCharset(headers);
//        
//        try {
//            data = encoder.decode(appliedCharset, dataString);
//        } catch (Exception e) {
//            throw new MalformedURLException("");
//        }
        data = dataString;
        
        DataUrl dataUrl = new DataUrl(data, encoding, mimeType, headers);

        return dataUrl;	
	}

    private static String decode(String s)  {
    	return URL.decode(s.replace("%20", "+"));
        
    }

 }
