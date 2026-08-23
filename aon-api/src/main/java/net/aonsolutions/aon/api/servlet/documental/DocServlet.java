package net.aonsolutions.aon.api.servlet.documental;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.doc.ExternalStorage;
import com.esferalia.aon.occam.api.model.doc.ExternalStorage.ExternalStorageVisitor;
import com.esferalia.aon.watson.server.http.AonURIBuilder;
import com.google.api.services.drive.Drive;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.google.apis.drive.AonDrive;
import solutions.aon.aws.s3.S3;
import solutions.aon.aws.s3.SCALEWAY;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiDocServlet", urlPatterns = { "/ms/api/doc/*",
														"/aon_gwt_aio/ms/api/doc/*",
														"/aon_gwt_fiscal/ms/api/doc/*"})
public class DocServlet extends HttpServlet {
	
	private static class ForwardHttpServletRequestWrapper extends HttpServletRequestWrapper {
		private URI uri;
		private Map<String, String[]> parameterMap;

		public ForwardHttpServletRequestWrapper(HttpServletRequest request, String url) {
			super(request);
			this.uri = URI.create(url);
			this.parameterMap = new AonURIBuilder(uri).getQueryParamsMap();
		}

		@Override
		public String getQueryString() {
			return uri.getQuery();
		}

		@Override
		public Map<String, String[]> getParameterMap() {
			return Collections.unmodifiableMap(parameterMap);
		}
		
		@Override
		public String getParameter(String name) {
			return parameterMap.getOrDefault(name, new String[]{null})[0];
		}
		
		@Override
		public String[] getParameterValues(String name) {
			return parameterMap.getOrDefault(name, null);
		}
		
		@Override
		public Enumeration<String> getParameterNames() {
			return new Vector<String>(parameterMap.keySet()).elements();
		}

		public void forward(ServletResponse response) throws ServletException, IOException {
			super.getRequestDispatcher(uri.getPath()).forward(this, response);
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject json = getParamsJSON(req);
		String domainName = req.getServerName();
		Integer domain = JsonUtils.getInteger(json, IJsonNames.DOMAIN_ID);
		ExternalStorage externalStorage = ExternalStorage.safeValueOf(JsonUtils.getbyte(json, IJsonNames.STORAGE));	
		
		switch (externalStorage) {
		case AON:
		case DRIVE: {
			String source = JsonUtils.getString(json, IJsonNames.SOURCE);
			String aonId = JsonUtils.getString(json, IJsonNames.AON_ID);

			JSONObject data = new JSONObject();
			data.put("id", aonId);
			data.put("domain_id", domain);
			data.put("attach_type", source);
			data.put("domain_name", domainName);
			String result = Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));
			new ForwardHttpServletRequestWrapper(req, "https://" + domainName + "/ms/api/file/" +  result).forward(resp);
			return;
		}
		case AWS: {
			S3 s3 = S3.getInstance();
			String s3Key = JsonUtils.getString(json, IJsonNames.S3_KEY );
			String aonTable = JsonUtils.getString(json, IJsonNames.AON_TABLE);
			String s3Bucket = Optional.ofNullable(JsonUtils.getString(json, IJsonNames.S3_BUCKET)).orElseGet(() -> s3.getAonTableBucket(aonTable, false));
			forwardToAws(req, resp, s3.download(s3Bucket, s3Key));
			return;
		}
		case SCALEWAY:{
			SCALEWAY scaleway = SCALEWAY.getInstance();
			String s3Key = JsonUtils.getString(json, IJsonNames.S3_KEY );
			String aonTable = JsonUtils.getString(json, IJsonNames.AON_TABLE);
			String s3Bucket = Optional.ofNullable(JsonUtils.getString(json, IJsonNames.S3_BUCKET)).orElseGet(() -> scaleway.getAonTableBucket(aonTable, false));
			forwardToAws(req, resp, scaleway.download(s3Bucket, s3Key));
			return;
		}
			
		default:
			throw new IllegalArgumentException("Unexpected value: " + externalStorage);
		}
		
	}
	
	public void forwardToAws(HttpServletRequest request, HttpServletResponse response, byte[] data) throws IOException {
        
        response.addHeader(IConstants.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.addHeader(IConstants.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader(IConstants.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        response.addHeader(IConstants.ACCESS_CONTROL_MAX_AGE, "1728000");
        
        response.getOutputStream().write(data);
        response.setStatus(HttpServletResponse.SC_OK);
 
    }
	
	public void forwardToExternal(HttpServletRequest request, HttpServletResponse response, String longUrl) throws IOException {
        URL url = new URL(longUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(request.getMethod());
        conn.setDoOutput(true);

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            if (headerValue != null) {
                conn.setRequestProperty(headerName, headerValue);
            }
        }

        if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) {
            try (InputStream in = request.getInputStream();
                 OutputStream out = conn.getOutputStream()) {
                in.transferTo(out);
            }
        }
        
        response.addHeader(IConstants.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        response.addHeader(IConstants.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader(IConstants.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        response.addHeader(IConstants.ACCESS_CONTROL_MAX_AGE, "1728000");
        
        response.setStatus(conn.getResponseCode());
        conn.getHeaderFields().forEach((key, values) -> {
            if (key != null) {
                for (String value : values) {
                    response.addHeader(key, value);
                }
            }
        });

        try (InputStream in = conn.getInputStream();
             OutputStream out = response.getOutputStream()) {
            in.transferTo(out);
        }
    }
	
	public static JSONObject getParamsJSON(ServletRequest req) {
	    JSONObject jsonObj = new JSONObject();
		Map<String,String[]> params = req.getParameterMap();
	    for (Map.Entry<String,String[]> entry : params.entrySet()) {
	      String[] v = entry.getValue();
	      Object o = (v.length == 1) ? v[0] : new JSONArray(v);
	      jsonObj.put(entry.getKey(), o);
	    }
	    return jsonObj;
	}	
}
