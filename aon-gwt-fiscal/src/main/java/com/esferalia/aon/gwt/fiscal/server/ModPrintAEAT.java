package com.esferalia.aon.gwt.fiscal.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

public abstract class ModPrintAEAT extends HttpServlet{

	private static final long serialVersionUID = -3800066351932431787L;

	Integer id;
	String domainName;
	Integer domainId;
	String user;
	
	byte[] cert;
	String pass;
	String name;
	String document;
	String nrc;
	
	Boolean print;
	
	public ModPrintAEAT() {
	
	}
	
	protected void init(JSONObject json) throws JSONException, UnsupportedEncodingException {
		this.print = json.opt("print") != null;
		
		this.id = json.getInt("mod");
		this.domainName = json.getString("domainName");
		this.domainId = json.getInt("domainId");
		this.user = json.getString("user");	
		if(json.opt("cert") != null && !"null".equals(json.optString("cert"))) {
			Integer c = json.getInt("cert");
			Attach attach = AON.getAttach(domainName, domainId, user, f ->
				f.getIdProperty().eq(c)
				.and(f.getTypeProperty().eq(RegistryAttachmentType.DIGITAL_CERTIFICATE.value())), AttachType.REGISTRY, true);
			if(attach.getData() == null){
				DomainGserviceaccount g = AON.getDomainGserviceaccount(domainName, domainId, user);
				Drive drive = AonDrive.getInstace().serviceInitialize(g);
				attach.setData(AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId()));
			}
			this.cert = attach.getData();
			this.pass = URLDecoder.decode(json.getString("pass"),  "UTF-8");
			this.name = URLDecoder.decode(json.getString("name"),  "UTF-8");
			this.document = json.getString("document");
			this.nrc = json.opt("nrc") != null ? json.getString("nrc") : null;
		}			
	}	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public Integer getDomainId() {
		return domainId;
	}
	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public byte[] getCert() {
		return cert;
	}
	public void setCert(byte[] cert) {
		this.cert = cert;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getNrc() {
		return nrc;
	}
	public void setNrc(String nrc) {
		this.nrc = nrc;
	}
	public Boolean isCert() {
		return getCert() != null;
	}
	
	public Boolean isPrint() {
		return print;
	}
	
	protected String getEncodedFile(byte[] content) throws UnsupportedEncodingException {
		String fileString = new String(content, "ISO-8859-1");
		fileString = fileString.replace("'", " ");
		fileString = fileString.replace("&", " ");
		fileString = fileString.replace("\n", "");
		fileString = fileString.replace("\r", "");
		return URLEncoder.encode(fileString, "ISO-8859-1");
	}	
	
	protected void send(HttpServletRequest req, HttpServletResponse resp, String request, String urlParameters, Boolean isI) throws NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException, KeyStoreException, CertificateException, IOException, JSONException, ScriptException {
		if(isI && !isPrint()) {
			JSONObject json = new JSONObject();
			json.put("E00", "La declaración del modelo es de tipo ingreso (I). Realice la operación desde la pantalla del modelo.");	
			giveBack(req, resp, json, new JSONObject());
		} else {
			URL url = new URL(request);
   		
			SSLContext ctx = SSLContext.getInstance("TLS");
			ctx.init(isCert() ? getKeyManagers(cert, pass) : new KeyManager[0],
				new TrustManager[] { new DefaultTrustManager() },
				new SecureRandom());
			SSLContext.setDefault(ctx);

			HttpsURLConnection connection = (HttpsURLConnection) url
				.openConnection();
			connection.setHostnameVerifier(new HostnameVerifier() {
				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			});
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setInstanceFollowRedirects(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
			connection.setRequestProperty("charset", "ISO-8859-1");
			connection.setRequestProperty("Content-Length",
				"" + Integer.toString(urlParameters.getBytes().length));
			connection.setUseCaches(false);

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
		
			if(isPrint()) {
				if(isCert()) {
					String html = readFullyAsString(connection.getInputStream(), "ISO-8859-1");
					JSONObject json = parseHTML(html);
					json.put("nrc", getNrc());
					saveHistory(json);
					ByteArrayInputStream input = new ByteArrayInputStream(html.getBytes());
					AonIOUtils.copy(input, resp.getOutputStream());	
				} else {
					DataInputStream input = new DataInputStream(connection.getInputStream());
					AonIOUtils.copy(input, resp.getOutputStream());
				} 	
			} else {
				JSONObject json = new JSONObject();
				if(isCert()) {
					String html = readFullyAsString(connection.getInputStream(), "ISO-8859-1");
					json = parseHTML(html);
					saveHistory(json);
				} else {
					if(MimeType.PDF.getName().equals(connection.getContentType())) {
						json.put("CEL", "CEL");
					} else {
						String html = readFullyAsString(connection.getInputStream(), "ISO-8859-1");
						json = parseHTML(html);
					}
				}
				giveBack(req, resp, json, new JSONObject());
			}
			resp.flushBuffer();
			connection.disconnect();
		}
	}
	public JSONObject parseHTML(String html) throws JSONException, ScriptException, IOException {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");
		JSONObject json = new JSONObject();
		String[] scripts = html.split("<script type=\"text/javascript\">");
			
		if(isCert() && html.contains("var CEL") && scripts.length > 4) {
			String[] vars = scripts[3].split("\n");
			for(String h : vars) {
				if(h.contains("var")) {
					Integer x = h.indexOf("var");
					Integer a = h.indexOf("=");
					Integer z = h.lastIndexOf(";");
					String key = h.substring(x + 4, a);				
				
					String value =h.substring(a + 1, z).contains("&amp;") 
						? engine.eval(h.substring(a + 1, z).replace("&amp;", "").replace(";", "")).toString() 
						: engine.eval(h.substring(a + 1, z)).toString(); 
				
					if(!value.isEmpty()) {
						json.put(key, value);
					}
				}
			}
			String[] urls = scripts[4].split("\n");
		
			for(Integer i = 0 ; i < urls.length; i++) {
				if(urls[i].contains("ENR") && urls[i].contains(json.getString("ENR"))) {
					Integer a = urls[i+1].indexOf("=");
					Integer z = urls[i+1].indexOf(";");
					json.put("url", engine.eval(urls[i+1].substring(a + 1, z)).toString());
				}
			}
		} else if(scripts.length > 4){
			String[] html2 = scripts[4].split("\n");
			for(String h : html2) {
				if(h.contains("var")) {
					Integer x = h.indexOf("var");
					Integer a = h.indexOf("=");
					Integer z = h.indexOf(";");
					String key = h.substring(x + 4, a);
					String value = engine.eval(h.substring(a + 1, z)).toString();
				
					if(!value.isEmpty()) {
						json.put(key, value);
					}
				}
			}
		}
		return json;
	}
	
	public void saveHistory(JSONObject json) throws IOException, JSONException {
		Boolean ok = json.opt("CEL")!= null;
		DataResponse dr = new DataResponse()
				.setSource(getDataResponseSource())
				.setSourceId(getId())
				.setCode(isCert() ? (ok ? "Presentación Correcta": "Presentación Fallida"):(ok ? "Validacion Correcta": "Validacion Fallida"))
				.setDomain(getDomainId())
				.setResponseDate(new Date());
		dr = AON.insertDataResponse(getDomainName(), getDomainId(), getUser(), dr);
		for (Iterator<String> keys = json.keys(); keys.hasNext(); ) {
		    String key = keys.next();
		    DataResponseDetail drd = new DataResponseDetail()
		    		.setDomain(getDomainId())
		    		.setDataResponse(dr.getId())
		    		.setDataVariable(key)
		    		.setDataValue(json.getString(key));
		    AON.insertDataResponseDetail(getDomainName(), getDomainId(), getUser(), drd);
		}
		if(ok && json.opt("url") != null) {
			Attach attach = AON.getAttach(getDomainName(), getDomainId(), getUser(), f-> 
				f.getDomainProperty().eq(getDomainId())
				.and(f.getSourceTypeProperty().eq(getDataAttachSource().value()))
				.and(f.getSourceBatchProperty().eq(getId()))
				.and(f.getDescriptionProperty().eq("Presentacion AEAT"))
				,AttachType.DATA, false);
			if(attach.getId() != null) {
				AON.updateAttachData(getDomainName(), getDomainId(), getUser(), 
						attach.setData(getUrlFile(json.get("url").toString())));
				json.put("data", attach.getId());
			} else {
				Integer attachId = AON.insertAttach(getDomainName(), getDomainId(), getUser(), new Attach()
						.setSourceType(getDataAttachSource().value())
						.setSourceBatch(getId())
						.setType(DataAttachType.RESPONSE_OK.value())
						.setAttachModule(dr.getId())
						.setAttachType(AttachType.DATA)
						.setDomain(new Domain().setName(getDomainName()).setId(getDomainId()))
						.setData(getUrlFile(json.get("url").toString()))
						.setMimeType(MimeType.PDF)
						.setDescription("Presentacion AEAT"));
				json.put("data", attachId);
			}
			updateMod(json);
		}
		
	}
	
	public JSONObject getRequestJSON(HttpServletRequest req) throws JSONException{
		String line = "";
		StringBuilder bld = new StringBuilder();
		try {
			while((line = req.getReader().readLine()) != null){
				bld.append(" " + line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String s = checkString(bld.toString());
	
		JSONObject json = new JSONObject();
		try {
			json = new JSONObject(s);
		} catch (JSONException e) {
			String toJson = "{" + s.replace("=", ":").replace("&", ",") + ",print:true}";
			json = new JSONObject(toJson);
		}
		return json;
	}
	
	public String checkString(String str) {
		return new String(str.getBytes(Charset.forName("ISO-8859-1")), Charset.forName("UTF-8") );
	}
	
	public void giveBack(HttpServletRequest req, HttpServletResponse resp,
			Object object, JSONObject meta) {
		try {
			String js = req.getParameter("callback");
			if(js != null){
				resp.setContentType("application/javascript; charset=utf-8");     
				PrintWriter out = resp.getWriter();
				out.print(js + "({" +"\"meta\":"+ meta +", \"data\":" + object +"});");
				out.flush();
			} else {
				resp.setContentType("application/json");
				PrintWriter out = resp.getWriter();
				out.print(object);
				out.flush();
			}
		} catch (IOException e) {

		}
	}
	
	public String readFullyAsString(InputStream inputStream, String encoding) throws IOException {
        return readFully(inputStream).toString(encoding);
    }

    private ByteArrayOutputStream readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos;
    }
	
	private KeyManager[] getKeyManagers(byte[] cert, String pass) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException{
		ByteArrayInputStream key = new ByteArrayInputStream(cert);
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
	    keyStore.load(key, pass.toCharArray());
    	KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
   		kmf.init(keyStore, pass.toCharArray());
   		return kmf.getKeyManagers();
	}
	
	public byte[] getUrlFile(String pdfUrl) throws IOException {
		URL url = new URL(pdfUrl);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = null;
		byte[] b = null; 
		try {
		  is = url.openStream ();
		  byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
		  int n;

		  while ( (n = is.read(byteChunk)) > 0 ) {
		    baos.write(byteChunk, 0, n);
		  }
		  b = baos.toByteArray();
		} catch (IOException e) {
		  System.err.printf ("Failed while reading bytes from %s: %s", url.toExternalForm(), e.getMessage());
		  e.printStackTrace ();
		  // Perform any other exception handling that's appropriate.
		} finally {
		  if (is != null) { is.close(); }
		}	
		return b;
	}
	
	protected void exceptionErrors(HttpServletRequest req, HttpServletResponse resp, String error) {
		if("keystore password was incorrect".equals(error)){
			try {
				JSONObject json = new JSONObject();
				json.put("E00", "La contraseña introducida es incorrecta.");
				if(!isPrint()) giveBack(req, resp, json, new JSONObject());
			} catch (JSONException e1) {e1.printStackTrace();}
		}
	}
	
	protected abstract DataResponseSource getDataResponseSource();
	protected abstract DataAttachSource getDataAttachSource();
	protected abstract void updateMod(JSONObject json) throws JSONException;
	
}
