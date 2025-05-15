package net.aonsolutions.aon.api.servlet.documental;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
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
		String longURL = externalStorage.visit(new ExternalStorageVisitor<String>() {

			@Override
			public String visitAon() {
				String source = JsonUtils.getString(json, IJsonNames.SOURCE);
				String aonId = JsonUtils.getString(json, IJsonNames.AON_ID);

				JSONObject data = new JSONObject();
				data.put("domain_name", domainName);
				data.put("domain_id", domain);
				data.put("id", aonId);
				data.put("attach_type", source);
				String result = Base64.getEncoder().encodeToString(data.toString().getBytes(StandardCharsets.UTF_8));
				return "https://" + domainName + "/ms/api/file/" +  result;
			}

			@Override
			public String visitDrive() {
				String driveId = JsonUtils.getString(json, IJsonNames.DRIVE_ID);
				DomainGserviceaccount g = AON.getDomainGserviceaccount(domainName, domain, "");
				Drive drive = AonDrive.getInstace().serviceInitialize(g);
				return AonDrive.getInstace().getFile(drive, driveId).getWebContentLink();
			}

			@Override
			public String visitAws() {
				String s3Bucket = JsonUtils.getString(json, IJsonNames.S3_BUCKET);
				String s3Key = JsonUtils.getString(json, IJsonNames.S3_KEY);
				String aonTable = JsonUtils.getString(json, IJsonNames.AON_TABLE);
				return s3Bucket != null
					? S3.getInstance().getURL(s3Bucket, s3Key).toExternalForm()
					: S3.getInstance().getAonTableDownloadURL(aonTable, s3Key).toExternalForm();
			}

			@Override
			public String visitScaleway() {
				String s3Bucket = JsonUtils.getString(json, IJsonNames.S3_BUCKET);
				String s3Key = JsonUtils.getString(json, IJsonNames.S3_KEY);
				String aonTable = JsonUtils.getString(json, IJsonNames.AON_TABLE);
				return s3Bucket != null
					? SCALEWAY.getInstance().getURL(s3Bucket, s3Key).toExternalForm()
					: SCALEWAY.getInstance().getAonTableDownloadURL(aonTable, s3Key).toExternalForm();
			}
			
		});
		System.out.println(longURL);
		new ForwardHttpServletRequestWrapper(req, longURL).forward(resp);
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
