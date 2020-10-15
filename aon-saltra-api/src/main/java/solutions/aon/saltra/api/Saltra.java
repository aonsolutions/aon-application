package solutions.aon.saltra.api;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;

import javax.security.sasl.AuthenticationException;

import org.json.JSONObject;

public class Saltra implements AutoCloseable{
	
	
	private static final String DEV = "dev";
	
	
	public static final String DNI = "dni";
	public static final String CCC2 = "ccc2";
	public static final String CCC1 = "ccc1";
	public static final String NAF = "naf";
	public static final String NAF2 = "naf2";
	public static final String NAF1 = "naf1";
	public static final String SEXO = "sexo";
	public static final String REGIMEN = "regimen";
	public static final String TIPO_CONTRATO = "tipo_contrato";
	public static final String IDENTIFICACION = "identificacion";
	public static final String GRUPO_COTIZACION = "grupo_cotizacion";
	public static final String GRUPO_COTIZACION_TEXT = "grupo_cotizacion_text";
	public static final String CERT_KEY = "cert_key";
	public static final String CERT_SECRET = "cert_secret";
	public static final String FECHA_ALTA = "fecha_alta";
	public static final String FECHA_BAJA = "fecha_baja";
	public static final String FECHA_REAL = "fecha_real";
	public static final String COEF = "coef";
	public static final String OCUPACION = "ocupacion";
	public static final String EMPLEADOS = "empleados";
	public static final String SUCCESS = "success";
	public static final String MESSAGE = "message";
	public static final String NOMBRES = "nombres";
	public static final String EMPRESA = "empressa";
	
	public static final String ANACIMIENTO = "anacimiento";
	public static final String MNACIMIENTO = "mnacimiento";
	public static final String DNACIMIENTO = "dnacimiento";
	
	
	private static Map<String, Supplier<? extends SaltraException>> EXCEPTIONS = 
	new HashMap<String, Supplier<? extends SaltraException>>(){
		{
			put("^403\\*.*", () -> new ForbiddenException()); // ACCESO NO AUTORIZADO

			put("^3543\\*.*", () -> new NoSuchDataException()); // NO EXISTEN DATOS PARA ESTA CONSULTA"
			put("^3065\\*.*", () -> new NoSuchDataException()); // CLAVE INEXISTENTE EN BASE DE DATOS"
			put("^3001\\*.*", () -> new InvalidArgumentException()); // ERROR DE VALIDACION U OBLIGATORIEDAD NO CUMPLIDA
			

		}
	};
	


	private URL url;
	private String token;
	private String certKey;
	private String certSecret;
	
	private byte dev = 0;

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
	
	public Saltra(String url, String certKey, String certSecret) throws MalformedURLException {
		this(new URL(url), certKey, certSecret);
	}
	
	public Saltra(String url, String certKey, String certSecret, byte dev) throws MalformedURLException {
		this(new URL(url), certKey, certSecret);
	}

	public Saltra setDev(boolean dev) {
		this.dev = dev ? (byte)1 : (byte)0;
		return this;
	}
	
	@Override
	public void close() throws SaltraException {
	}

	public void login(String email, String password) throws IOException, SaltraException {
		URL loginURL = getURL("login");
		JSONObject requestJsonObject = 
		new JSONObject()
		.put("email", email)
		.put("password", password);
		JSONObject responseJsonObject = post(loginURL, requestJsonObject);
		setToken( responseJsonObject.getString("token") );
	}
	
	
	
	public void saveEmpresa(String name, String cif ) throws IOException, SaltraException {
		URL saveEmpresaURL = getURL("empresa/save");
		JSONObject requestJsonObject = 
		new JSONObject()
		.put("ide", cif)
		.put("cif", cif)
		.put("razon_social", name);
		post(saveEmpresaURL, requestJsonObject, getToken());
	}
	
	public void updateEmpresa(String name, String cif ) throws IOException, SaltraException {
		URL saveEmpresaURL = getURL("empresa/update");
		JSONObject requestJsonObject = 
		new JSONObject()
		.put("ide", cif)
		.put("cif", cif)
		.put("razon_social", name);
		post(saveEmpresaURL, requestJsonObject, getToken());
	}
	
	public void deleteEmpresa(String cif ) throws IOException, SaltraException {
		URL deleteEmpresaURL = getURL("empresa/delete");
		JSONObject requestJsonObject = 
		new JSONObject()
		.put("ide", cif);
		post(deleteEmpresaURL, requestJsonObject, getToken());
	}

	public JSONObject saveCertificado(String cif, String password, InputStream is ) throws IOException, SaltraException {
		URL saveEmpresaURL = getURL("certificado/save");
		Map<String, String> fields = new HashMap<String, String>();
		fields.put("ide", cif);
		fields.put("password", password);
		JSONObject responseObject = post(saveEmpresaURL, fields, is, getToken());
		setCertKey(responseObject.getString(CERT_KEY));
		setCertSecret(responseObject.getString(CERT_SECRET));
		return orElseThrow(responseObject);
	}

	public OutputStream getTA(String regime, String ccc) {
		return null;
	}

	public String getITA(String regime, String ccc) throws IOException, SaltraException {
		URL itaURL = getURL("movimientos/informes-afiliado-ccc");
		JSONObject requestJsonObject = 
		new JSONObject()
		.put(CERT_KEY, getCertKey())
		.put(CERT_SECRET, getCertSecret())
		.put(CCC1, ccc.substring(0,2))
		.put(CCC2, ccc.substring(2))
		.put(REGIMEN, regime)
		;
		JSONObject responseJsonObject = post(itaURL, requestJsonObject);
		return responseJsonObject.getString("pdf");
	}
	
	public OutputStream getHistory(String naf, String regime, String ccc) {
		return null;
	}

	public String getHistory(String regime, String ccc, String from, String to) throws IOException, SaltraException {
		URL historyURL = getURL("movimientos/informe-laboral");
		JSONObject requestJsonObject = 
		new JSONObject()
		.put(CERT_KEY, getCertKey())
		.put(CERT_SECRET, getCertSecret())
		.put(CCC1, ccc.substring(0,2))
		.put(CCC2, ccc.substring(2))
		.put("fechaini", from)
		.put("fechafin", to)
		.put(REGIMEN, regime)
		;
		JSONObject responseJsonObject = post(historyURL, requestJsonObject);
		return responseJsonObject.getString("pdf");	}
	
	public String getIDC(String naf, String regime, String ccc, Date date) throws IOException, SaltraException {
		return getIDC(naf, regime, ccc, new SimpleDateFormat("dd-MM-yyyy").format(date));
	}


	
	public String getIDC(String naf, String regime, String ccc, String date) throws IOException, SaltraException {
		URL idcURL = getURL("movimientos/duplicado-idc");
		JSONObject requestJsonObject = 
		new JSONObject()
		.put(CERT_KEY, getCertKey())
		.put(CERT_SECRET, getCertSecret())
		.put(NAF1, naf.substring(0, 2))
		.put(NAF2, naf.substring(2))
		.put(CCC1, ccc.substring(0,2))
		.put(CCC2, ccc.substring(2))
		.put(REGIMEN, regime)
		.put("movimiento", "Alta")
		.put("fecha_real", date)
		;
		JSONObject responseJsonObject = post(idcURL, requestJsonObject);
		return responseJsonObject.getString("pdf");
	}
	
	public JSONObject getStatus(String regime, String ccc, String nif, Date date) throws IOException, SaltraException {
		return getStatus(regime, ccc, nif, new SimpleDateFormat("dd-MM-yyyy").format(date));
	}
	
	public JSONObject getStatus(String regime, String ccc, String nif, String date) throws IOException, SaltraException {
		URL statusURL = getURL("movimientos/situacion");
		JSONObject requestJsonObject = 
		new JSONObject()
		.put(CERT_KEY, getCertKey())
		.put(CERT_SECRET, getCertSecret())
		.put(CCC1, ccc.substring(0,2))
		.put(CCC2, ccc.substring(2))
		.put(REGIMEN, regime)
		.put("fecha", date)
		.put(DNI, nif)
		.put(IDENTIFICACION, getIdentificacion(nif))
		;
		return post(statusURL, requestJsonObject);
	}	
	
	public JSONObject getActivity(String regime, String ccc, String from, String to) throws IOException, SaltraException {
		URL idcURL = getURL("sincronizar/movimientos");
		JSONObject requestJsonObject = 
		new JSONObject()
		.put(CERT_KEY, getCertKey())
		.put(CERT_SECRET, getCertSecret())
		.put(CCC1, ccc.substring(0,2))
		.put(CCC2, ccc.substring(2))
		.put(REGIMEN, regime)
		.put("fechaini", from)
		.put("fechafin", to)
		;
		return post(idcURL, requestJsonObject);
	}
	
	public JSONObject getActiveEmployees(String regime, String ccc ) throws IOException, SaltraException {
		URL activeURL = getURL("movimientos/movreales");
		JSONObject requestJsonObject = 
		new JSONObject()
		.put(CERT_KEY, getCertKey())
		.put(CERT_SECRET, getCertSecret())
		.put(CCC1, ccc.substring(0,2))
		.put(CCC2, ccc.substring(2))
		.put(REGIMEN, regime)
		.put("options", 1)
		;
		return post(activeURL, requestJsonObject);
	}

	public JSONObject getOldEmployees(String regime, String ccc ) throws IOException, SaltraException {
		URL activeURL = getURL("movimientos/movreales");
		JSONObject requestJsonObject = 
		new JSONObject()
		.put(CERT_KEY, getCertKey())
		.put(CERT_SECRET, getCertSecret())
		.put(CCC1, ccc.substring(0,2))
		.put(CCC2, ccc.substring(2))
		.put(REGIMEN, regime)
		.put("options", 2)
		;
		return post(activeURL, requestJsonObject);
	}

	public String getNaf(String regime, String ccc, String nif, String apellido1, String apellido2) throws IOException, SaltraException {
		URL nafURL = getURL("movimientos/naf");
		JSONObject requestJsonObject = 
		new JSONObject()
		.put(CERT_KEY, getCertKey())
		.put(CERT_SECRET, getCertSecret())
		.put(CCC1, ccc.substring(0,2))
		.put(CCC2, ccc.substring(2))
		.put(REGIMEN, regime)
		.put(DNI, nif)
		.put("apellido1", apellido1)
		.put("apellido2", apellido2)
		.put(IDENTIFICACION, getIdentificacion(nif))
		;
		JSONObject responseJson = post(nafURL, requestJsonObject);
		return responseJson.getString(NAF1) + responseJson.getString(NAF2);
	}	
	
	public String getNif(String naf) throws IOException, SaltraException {
		URL nifURL = getURL("movimientos/nifxnaf");
		JSONObject requestJsonObject = 
		new JSONObject()
		.put(CERT_KEY, getCertKey())
		.put(CERT_SECRET, getCertSecret())
		.put("naf", naf)
		;
		JSONObject responseJson = post(nifURL, requestJsonObject);
		return responseJson.getString(DNI);
	}	
	
	public JSONObject register(
			String naf, 
			String dni, 
			String regime, 
			String ccc, 
			String agreement, 
			String tc2, 
			String group, 
			String date, 
			String factor,
			String ocupation,
			String quote,
			String ta ) throws IOException, SaltraException {
		return register(naf, dni, regime, ccc, agreement, tc2, group, date, Optional.ofNullable(factor), Optional.ofNullable(ocupation), Optional.ofNullable(quote),Optional.ofNullable(ta));
	}
	
	public JSONObject register(
			String naf, 
			String dni, 
			String regime, 
			String ccc, 
			String agreement, 
			String tc2, 
			String group, 
			String date, 
			Optional<String> factor,
			Optional<String> ocupation,
			Optional<String> quote,
			Optional<String> ta) throws IOException, SaltraException {
		URL registerURL = getURL("movimientos/altbaj");
		JSONObject requestJsonObject = 
		new JSONObject()
		.put(CERT_KEY, getCertKey())
		.put(CERT_SECRET, getCertSecret())
		.put("movimiento", "Alta")
		.put("situacion", "01")
		.put(NAF1, naf.substring(0, 2))
		.put(NAF2, naf.substring(2))
		.put(DNI, dni)
		.put(IDENTIFICACION, getIdentificacion(dni))
		.put(REGIMEN, regime)
		.put(CCC1, ccc.substring(0,2))
		.put(CCC2, ccc.substring(2))
		.put("convenio", agreement)
		.put(TIPO_CONTRATO, tc2)
		.put(GRUPO_COTIZACION, group)
		.put("fecha_real", date)
		;
		ta.ifPresent(str -> requestJsonObject.put("obtener_ta", str ));
		ocupation.ifPresent(str -> requestJsonObject.put("ocupacion", str ));
		factor.ifPresent(str -> requestJsonObject.put("coef_parcial", str ));
		quote.ifPresent(str -> requestJsonObject.put("modalidad_ctz", str ));
		
		return post(registerURL, requestJsonObject);
	}

	public JSONObject unregister(
			String naf, 
			String dni, 
			String regime, 
			String ccc, 
			String agreement, 
			String date, 
			Optional<String> holidaysDate,
			Optional<String> ta) throws IOException, SaltraException {
		URL unRegisterURL = getURL("movimientos/altbaj");
		JSONObject requestJsonObject = 
		new JSONObject()
		.put(CERT_KEY, getCertKey())
		.put(CERT_SECRET, getCertSecret())
		.put("movimiento", "Baja")
		.put("situacion", "93")
		.put(NAF1, naf.substring(0, 2))
		.put(NAF2, naf.substring(2))
		.put(DNI, dni)
		.put(IDENTIFICACION, getIdentificacion(dni))
		.put(REGIMEN, regime)
		.put(CCC1, ccc.substring(0,2))
		.put(CCC2, ccc.substring(2))
		.put("convenio", agreement)
		.put("fecha_real", date)
		;
		ta.ifPresent(str -> requestJsonObject.put("obtener_ta", str ));
		ta.ifPresent(str -> requestJsonObject.put("fecha_vacaciones", holidaysDate ));
		
		return post(unRegisterURL, requestJsonObject);
	}

	public JSONObject delete(
			String naf, 
			String dni, 
			String regime, 
			String ccc, 
			String agreement, 
			String date,
			String move) throws IOException, SaltraException {
		URL unRegisterURL = getURL("movimientos/movdelete");
		JSONObject requestJsonObject = 
		new JSONObject()
		.put(CERT_KEY, getCertKey())
		.put(CERT_SECRET, getCertSecret())
		.put("movimiento", move)
		.put(NAF1, naf.substring(0, 2))
		.put(NAF2, naf.substring(2))
		.put(REGIMEN, regime)
		.put(CCC1, ccc.substring(0,2))
		.put(CCC2, ccc.substring(2))
		.put("fecha_real", date)
		;
		
		return post(unRegisterURL, requestJsonObject);
	}	
	
	public JSONObject deleteRegister(
			String naf, 
			String dni, 
			String regime, 
			String ccc, 
			String agreement, 
			String date) throws IOException, SaltraException {
		return delete(naf, dni, regime, ccc, agreement, date, "Alta");
	}

	public JSONObject deleteUnRegister(
			String naf, 
			String dni, 
			String regime, 
			String ccc, 
			String agreement, 
			String date) throws IOException, SaltraException {
		return delete(naf, dni, regime, ccc, agreement, date, "Baja");
	}
	
	public JSONObject updateRegister(
			String naf, 
			String dni, 
			String regime, 
			String ccc, 
			String date, 
			String updateDate, 
			Optional<String> agreement, 
			Optional<String> tc2, 
			Optional<String> group, 
			Optional<String> factor,
			Optional<String> ocupation,
			Optional<String> quote,
			Optional<String> ta) throws IOException, SaltraException {
		URL updateURL = getURL("movimientos/movupdate");
		JSONObject requestJsonObject = 
		new JSONObject()
		.put(CERT_KEY, getCertKey())
		.put(CERT_SECRET, getCertSecret())
		.put("movimiento", "Alta")
		.put("situacion", "01")
		.put(NAF1, naf.substring(0, 2))
		.put(NAF2, naf.substring(2))
		.put(DNI, dni)
		.put(IDENTIFICACION, getIdentificacion(dni))
		.put(REGIMEN, regime)
		.put(CCC1, ccc.substring(0,2))
		.put(CCC2, ccc.substring(2))
		.put("fecha_real", date)
		.put("fecha_situacion", updateDate)
		;

		ta.ifPresent(str -> requestJsonObject.put("obtener_ta", str ));
		tc2.ifPresent(str -> requestJsonObject.put(TIPO_CONTRATO, str ));
		group.ifPresent(str -> requestJsonObject.put(GRUPO_COTIZACION, str ));
		agreement.ifPresent(str -> requestJsonObject.put("convenio", str ));
		ocupation.ifPresent(str -> requestJsonObject.put("ocupacion", str ));
		factor.ifPresent(str -> requestJsonObject.put("coef_parcial", str ));
		quote.ifPresent(str -> requestJsonObject.put("modalidad_ctz", str ));
		
		return post(updateURL, requestJsonObject);
	}
	
	public JSONObject updateRegister(
			String naf, 
			String dni, 
			String regime, 
			String ccc, 
			String date, 
			String updateDate, 
			String agreement, 
			String tc2, 
			String group, 
			String factor,
			String ocupation,
			String quote,
			String ta) throws IOException, SaltraException {
		return updateRegister(naf, dni, regime, ccc, date, updateDate, Optional.ofNullable(agreement), Optional.ofNullable(tc2),Optional.ofNullable(group), Optional.ofNullable(factor), Optional.ofNullable(ocupation), Optional.ofNullable(quote),  Optional.ofNullable(ta));
	}
	// ------------------------------------------------------------------------
	
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
	
	
	// ------------------------------------------------------------------------

	// ------------------------------------------------------------------------
	
	private static int getIdentificacion(String cif) {
		if ( cif.matches("0?[0-9]{8}[A-Z]") ) 
			return 1; // DNI
		if ( cif.matches("[KL][0-9]{7}[A-Z]") )
			return 1; // K,L DNI
		if ( cif.matches("[MXYZ][0-9]{7}[A-Z]") )
			return 3; // NIE 
		return 2;
	}
	
	private static JSONObject post(URL url, JSONObject jsonObject) throws IOException, SaltraException {
		return post(url, jsonObject, Optional.empty());
	}
	
	private static JSONObject post(URL url, JSONObject jsonObject, String token) throws IOException, SaltraException{
		return post(url, jsonObject, Optional.ofNullable(token));
	}

	private static JSONObject post(URL url, JSONObject jsonObject, Optional<String> token) throws IOException, SaltraException {
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
			
			return orElseThrow(new JSONObject(responseJson));
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
	
	private static JSONObject orElseThrow(JSONObject jsonObject ) throws SaltraException {

		if ( jsonObject.getBoolean(SUCCESS) )
			return jsonObject;
		
		String message = jsonObject.getString(MESSAGE);
		
		for ( Entry<String,Supplier<? extends SaltraException>> entry : EXCEPTIONS.entrySet()) {
			String regex = entry.getKey();
			if ( message.matches(regex) ) {
				Supplier<? extends SaltraException> supplier = entry.getValue();
				throw supplier.get();
			}		
		}
		
		throw new SaltraException(message);
	}
	
	
		
	
}
