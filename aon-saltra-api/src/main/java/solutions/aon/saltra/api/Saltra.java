package solutions.aon.saltra.api;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.json.JSONObject;

public class Saltra {
	
	private URL url;
	private String token;
	private String certKey;
	private String certSecret;
	

	public Saltra(String url) throws MalformedURLException {
		this(new URL(url));
	}

	public Saltra(URL url) {
		this.url = url;
	}

	public Saltra(URL url, String certKey, String certSecret) {
		this.url = url;
		this.certKey = certKey;
		this.certSecret = certSecret;
	}
	
	public void login(String email, String password) throws IOException {
		URL loginURL = getURL("login");
		JSONObject requestJsonObject = 
		new JSONObject()
		.put("email", email)
		.put("password", password);
		JSONObject responseJsonObject = post(loginURL, requestJsonObject);
		setToken( responseJsonObject.getString("token") );
	}
	
	public void saveEmpresa(String name, String cif ) throws IOException {
		URL saveEmpresaURL = getURL("empresa/save");
		JSONObject requestJsonObject = 
		new JSONObject()
		.put("ide", cif)
		.put("cif", cif)
		.put("razon_social", name);
		post(saveEmpresaURL, requestJsonObject, getToken());
	}
	
	public void deleteEmpresa(String cif ) throws IOException {
		URL deleteEmpresaURL = getURL("empresa/delete");
		JSONObject requestJsonObject = 
		new JSONObject()
		.put("ide", cif);
		post(deleteEmpresaURL, requestJsonObject, getToken());
	}

	public void saveCertificado(String cif, String password, InputStream is ) throws IOException {
		URL saveEmpresaURL = getURL("certificado/save");
		Map<String, String> fields = new HashMap<String, String>();
		fields.put("ide", cif);
		fields.put("password", password);
		JSONObject responseObject = post(saveEmpresaURL, fields, is, getToken());
		setCertKey(responseObject.getString("cert_key"));
		setCertSecret(responseObject.getString("cert_secret"));
	}

	public OutputStream getTA(String regime, String ccc) {
		return null;
	}

	public OutputStream getITA(String regime, String ccc) {
		return null;
	}
	
	public OutputStream getHistory(String naf, String regime, String ccc) {
		return null;
	}

	public OutputStream getHistory(String regime, String ccc, Date from, Date to) {
		return null;
	}
	
	public OutputStream getIDC(String naf, String regime, String ccc, Date date) {
		return null;
	}

	public byte[] getIDC(String naf, String regime, String ccc, String date) throws IOException {
		URL idcURL = getURL("movimientos/duplicado-idc");
		JSONObject requestJsonObject = 
		new JSONObject()
		.put("cert_key", getCertKey())
		.put("cert_secret", getCertSecret())
		.put("naf1", naf.substring(0, 2))
		.put("naf2", naf.substring(2))
		.put("ccc1", ccc.substring(0,2))
		.put("ccc2", ccc.substring(2))
		.put("regimen", regime)
		.put("movimiento", "Alta")
		.put("fecha_real", date)
		;
		JSONObject responseJsonObject = post(idcURL, requestJsonObject);
		return Base64.getDecoder().decode(responseJsonObject.getString("pdf"));
	}
	
	private String getToken() {
		return token;
	}
	
	
	private Saltra setToken(String token) {
		this.token = token;
		return this;
	}
	
	public String getCertKey() {
		return certKey;
	}
	
	public Saltra setCertKey(String certKey) {
		this.certKey = certKey;
		return this;

	}
	
	public String getCertSecret() {
		return certSecret;
	}
	
	public Saltra setCertSecret(String certSecret) {
		this.certSecret = certSecret;
		return this;

	}
	
	private URL getURL(String path) throws MalformedURLException {
		return new URL(
				url.getProtocol(),
				url.getHost(),
				url.getPort(),
				String.format("%s/%s", url.getFile(), path)
				);
	}
	
	private static JSONObject post(URL url, JSONObject jsonObject) throws IOException {
		return post(url, jsonObject, Optional.empty());
	}
	
	private static JSONObject post(URL url, JSONObject jsonObject, String token) throws IOException {
		return post(url, jsonObject, Optional.ofNullable(token));
	}

	private static JSONObject post(URL url, JSONObject jsonObject, Optional<String> token) throws IOException {
		String requestJson = jsonObject.toString();
		byte content [] = requestJson.getBytes("utf-8");
		
		HttpURLConnection connection = ( HttpURLConnection ) url.openConnection();
		
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		connection.setRequestProperty("Content-Length", String.valueOf(content.length));
		token.ifPresent( str -> connection.setRequestProperty("Authorization", String.format("Bearer %s", str)));
		
		connection.setDoOutput(true);			
		try(OutputStream os = connection.getOutputStream()){
		    os.write(content, 0, content.length);	
		}
		
		int responseCode = connection.getResponseCode();
		if ( responseCode != HttpURLConnection.HTTP_OK )
			throw new IOException(connection.getResponseMessage());
		
		
		
		try (InputStream is = connection.getInputStream() ;
			ByteArrayOutputStream os = new ByteArrayOutputStream()){
			
			byte buffer [] = new byte  [1024]; 
			for ( int read = is.read( buffer ); read > -1 ; read = is.read( buffer ) )
				os.write(buffer, 0, read);
			
			String responseJson = os.toString("utf-8");
			
			return new JSONObject(responseJson);
		}
		
			
	}
	

	private static JSONObject post(URL url, Map<String, String> fields, InputStream in, String token) throws IOException {
		
		String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
		
		HttpURLConnection connection = ( HttpURLConnection ) url.openConnection();
		
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
		connection.setRequestProperty("Authorization", String.format("Bearer %s", token));
		
		connection.setUseCaches(false);
		connection.setDoOutput(true);	
		try(DataOutputStream os = new DataOutputStream(connection.getOutputStream())){
			os.writeBytes("--" + boundary + "\r\n");
			os.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"certificado.p12\"\r\n");
			os.writeBytes("Content-Type: application/x-pkcs12\r\n");
			os.writeBytes("Content-Transfer-Encoding: binary\r\n");
			os.writeBytes("\r\n");
			byte buffer [] = new byte  [1024]; 
			for ( int read = in.read( buffer ); read > -1 ; read = in.read( buffer ) )
				os.write(buffer, 0, read);
			os.writeBytes("\r\n");
			
			for (Map.Entry<String,String>  entry: fields.entrySet()) {				
				os.writeBytes("--" + boundary + "\r\n");				
				os.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n");		
				os.writeBytes("Content-Type: text/plain\r\n");
				os.writeBytes("\r\n");
				os.writeBytes(entry.getValue());
				os.writeBytes("\r\n");								
			};
			os.writeBytes("--" + boundary + "--\r\n");
		}

		int responseCode = connection.getResponseCode();
		if ( responseCode != HttpURLConnection.HTTP_OK )
			throw new IOException(connection.getResponseMessage());
		
		
		
		try (InputStream is = connection.getInputStream() ;
			ByteArrayOutputStream os = new ByteArrayOutputStream()){
			
			byte buffer [] = new byte  [1024]; 
			for ( int read = is.read( buffer ); read > -1 ; read = is.read( buffer ) )
				os.write(buffer, 0, read);
			
			String responseJson = os.toString("utf-8");
			
			return new JSONObject(responseJson);
		}		
			
	}
		
	
}
