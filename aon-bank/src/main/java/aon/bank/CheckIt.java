package aon.bank;

import java.io.IOException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class CheckIt {
	
	private static Object post(String url, JSONObject params) {
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpPost post = new HttpPost(url);
			StringEntity entity = new StringEntity(params.toString());
			post.addHeader("Content-Type", "application/json");
			post.setEntity(entity);
			try (CloseableHttpResponse resp = client.execute(post)) {
				if (resp.getEntity() != null) {
					String str =  EntityUtils.toString(resp.getEntity());
					if (str != null && str.charAt(0) == '[') {
						return new JSONArray(str);
					} else if (str != null && str.charAt(0) == '{') {
						return new JSONObject(str);
					}
				}
			}
		} catch (IOException e) {
		}
		return null;
	}
	
	private static JSONArray parseJSONArray (Object json) throws Exception {
		try {
			return (JSONArray) json;			
		} catch (ClassCastException e) {
			throw new Exception(json.toString());
		}
	}

	private static JSONObject parseJSONObject(Object json) throws Exception {
		try {	
			JSONObject jsonObj = (JSONObject) json;
			if (jsonObj.optString("result").isEmpty()) {
				return jsonObj;
			}
		} catch (ClassCastException e) {
		}
		throw new Exception(json.toString());
			
	}
	
	
	
//----------------------------------------GET BANKS-----------------------------------------
	/**
	 * Función encargada de devolver el listado de los bancos que continen robots
	 * @param params <ul><li><strong>claveApi</strong>: <em>required (string)</em><p>Token que identifica el despacho</p></li></ul>
	 * @return JSONArray
	 * @throws Exception 
	 */
	public static JSONArray getBanks(JSONObject params) throws Exception {
		Object json = post("https://www.checkitbancario.com/openapi/bancos", params);
		return parseJSONArray(json);
	}
	
	/**
	 * Función encargada de devolver el listado de los bancos que continen robots
	 * @param claveApi <em>required (string)</em><p>Token que identifica el despacho</p>
	 * @return JSONArray
	 * @throws Exception 
	 */
	public static JSONArray getBanks(String claveApi) throws Exception {
		JSONObject params = new JSONObject();
		params.put("claveApi", claveApi);
		return getBanks(params);
	}
	
//------------------------------------------------------------------------------------------
	
//----------------------------------------GET LOGINS----------------------------------------	
	/**
	 * Función encgarada de devolver el listado con los tipos de logins
	 * @param params <ul><li><strong>banco_id</strong>: <em>required (integer)</em><p>Campo único que identifica el banco. Se obtiene de /bancos</p></li></ul>
	 * @return JSONArray
	 * @throws Exception 
	 */
	public static JSONArray getLogins(JSONObject params) throws Exception {
		Object json = post("https://www.checkitbancario.com/openapi/bancos/logins", params);
		return parseJSONArray(json);
	}
	
	/**
	 * Función encgarada de devolver el listado con los tipos de logins
	 * @param bancoId <em>required (integer)</em><p>Campo único que identifica el banco. Se obtiene de /bancos</p>
	 * @return JSONArray
	 * @throws Exception 
	 */
	public static JSONArray getLogins(Integer bancoId) throws Exception {
		JSONObject params = new JSONObject();
		params.put("banco_id", bancoId);
		return getLogins(params);
	}
	
//------------------------------------------------------------------------------------------
	
//-------------------------------------GET LOGIN FIELDS-------------------------------------
	
	/**
	 * Función encargada de devolver los campos necesarios para el login según el tipo_login_id
	 * @param params <ul><li><strong>tipo_login_banco_id</strong>: <em>required (integer)</em><p>Campo único que identifica el tipo login del banco. Se obtiene de /bancos/logins</p></li></ul>
	 * @return JSONArray
	 * @throws Exception
	 */
	public static JSONArray getLoginFields(JSONObject params) throws Exception {
		Object json = post("https://www.checkitbancario.com/openapi/bancos/logins/campos", params);
		return parseJSONArray(json);
	}
	
	/**
	 * Función encargada de devolver los campos necesarios para el login según el tipo_login_id
	 * @param tipoLoginBancoId <em>required (integer)</em><p>Campo único que identifica el tipo login del banco. Se obtiene de /bancos/logins</p>
	 * @return JSONArray
	 * @throws Exception
	 */
	public static JSONArray getLoginFields(Integer tipoLoginBancoId) throws Exception {
		JSONObject params = new JSONObject();
		params.put("tipo_login_banco_id", tipoLoginBancoId);
		return getLoginFields(params);
	}
	
//------------------------------------------------------------------------------------------
	
//-------------------------------------GET CREDENTIALS--------------------------------------
	/**
	 * Función encargada de devolver las credenciales y el tipo de login al que pertenecen.
	 * @param params <ul><li><strong>claveApi</strong>: <em>required (string)</em><p>Token que identifica el despacho</p></li><li><strong>empresa_id</strong>: <em>required (integer)</em><p>Campo único que identifica la empresa. Se obtiene de /empresas</p></li><li><strong>tipo_login_banco_id</strong>: <em>required (integer)</em><p>Campo único que identifica el tipo del login del banco. Se obtiene de /bancos/logins</p></li></ul>
	 * @return JSONObject
	 * @throws Exception
	 */
	public static JSONObject getCredentials(JSONObject params) throws Exception {
		Object json = post("https://www.checkitbancario.com/openapi/credenciales", params);
		return parseJSONObject(json);
	}
	
	/**
	 * Función encargada de devolver las credenciales y el tipo de login al que pertenecen.
	 * @param claveApi <em>required (string)</em><p>Token que identifica el despacho</p>
	 * @param empresaId <em>required (integer)</em><p>Campo único que identifica la empresa. Se obtiene de /empresas</p>
	 * @param tipoLoginBancoId <em>required (integer)</em><p>Campo único que identifica el tipo del login del banco. Se obtiene de /bancos/logins</p>
	 * @return JSONObject
	 * @throws Exception
	 */
	public static JSONObject getCredentials(String claveApi, Integer empresaId, Integer tipoLoginBancoId) throws Exception {
		JSONObject params = new JSONObject();
		params.put("claveApi", claveApi);
		params.put("empresa_id", empresaId);
		params.put("tipo_login_banco_id", tipoLoginBancoId);
		return getCredentials(params);
	}
	
//------------------------------------------------------------------------------------------
	
//-------------------------------------GET LOGIN FIELDS-------------------------------------	
	
	
	
//------------------------------------------------------------------------------------------	
	
	
	public static void main(String[] args) throws Exception {
//		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//		HttpPost httpPost = new HttpPost("https://www.checkitbancario.com/openapi/empresas/add");
//		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair("claveApi", "84d9ee44e457ddef7f2c4f25dc8fa865"));
//		params.add(new BasicNameValuePair("nombre", "Rayson & Co."));
//		params.add(new BasicNameValuePair("cif", "58025118M"));
//		params.add(new BasicNameValuePair("email", "09iker05@gmail.com"));
//		params.add(new BasicNameValuePair("nombrecorto", "Rayson"));
//		httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
//		CloseableHttpResponse response = httpClient.execute(httpPost);
//		if (response.getEntity() != null) {
//			String body = EntityUtils.toString(response.getEntity(), "UTF-8");
//			System.out.println(body);
//		}
//	}
//		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//			// [{"userID":"Código de
//			// empresa","userPassword":"Usuario","userPIN":"Contraseña"}]
//			HttpPost httpPost = new HttpPost("https://www.checkitbancario.com/openapi/cuentas");
//			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//			params.add(new BasicNameValuePair("claveApi", "84d9ee44e457ddef7f2c4f25dc8fa865"));
//			params.add(new BasicNameValuePair("empresa_id", "11413"));
//			params.add(new BasicNameValuePair("tipo_cuenta_bancaria_id", "1"));
//			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
//			CloseableHttpResponse response = httpClient.execute(httpPost);
//			if (response.getEntity() != null) {
//				String body = EntityUtils.toString(response.getEntity(), "UTF-8");
//				System.out.println(body);
//			}
//		}
		System.out.println(getBanks((String)null));
	}

}
