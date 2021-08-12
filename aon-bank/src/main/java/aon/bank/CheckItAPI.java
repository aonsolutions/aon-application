package aon.bank;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class CheckItAPI {

	private static final String API_URL = "https://www.checkitbancario.com/openapi/";
	private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd", new Locale("es", "ES"));

	private static Object post(String url, JSONObject params) {
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpPost post = new HttpPost(url);
			StringEntity entity = new StringEntity(params.toString());
			post.addHeader("Content-Type", "application/json");
			post.setEntity(entity);
			try (CloseableHttpResponse resp = client.execute(post)) {
				if (resp.getEntity() != null) {
					String str = EntityUtils.toString(resp.getEntity());
					if (str != null && str.charAt(0) == '[') {
						return new JSONArray(str);
					} else if (str != null && str.charAt(0) == '{') {
						return new JSONObject(str);
					} else {
						return str;
					}
				}
			}
		} catch (IOException e) {
		}
		return null;
	}

	private static JSONArray parseJSONArray(Object json) throws Exception {
		try {
			return (JSONArray) json;
		} catch (ClassCastException e) {
			throw new Exception(json.toString());
		}
	}

	private static JSONObject parseJSONObject(Object json) throws Exception {
		try {
			JSONObject jsonObj = (JSONObject) json;
			String result = jsonObj.optString("result");
			if (result.isEmpty() || jsonObj.optString("result").equalsIgnoreCase("Success")) {
				return jsonObj;
			}
		} catch (ClassCastException e) {
		}
		throw new Exception(json.toString());

	}

//----------------------------------------GET BANKS-----------------------------------------
	/**
	 * Función encargada de devolver el listado de los bancos que continen robots
	 * 
	 * @param params
	 *               <ul>
	 *               <li><strong>claveApi</strong>: <em>required (string)</em>
	 *               <p>
	 *               Token que identifica el despacho
	 *               </p>
	 *               </li>
	 *               </ul>
	 * @return JSONArray
	 * @throws Exception exception containing the error JSON as String as message
	 */
	public static JSONArray getBanks(JSONObject params) throws Exception {
		Object json = post(API_URL + "bancos", params);
		return parseJSONArray(json);
	}

	/**
	 * Función encargada de devolver el listado de los bancos que continen robots
	 * 
	 * @param claveApi <em>required (string)</em>
	 *                 <p>
	 *                 Token que identifica el despacho
	 *                 </p>
	 * @return JSONArray
	 * @throws Exception exception containing the error JSON as String as message
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
	 * 
	 * @param params
	 *               <ul>
	 *               <li><strong>banco_id</strong>: <em>required (integer)</em>
	 *               <p>
	 *               Campo único que identifica el banco. Se obtiene de /bancos
	 *               </p>
	 *               </li>
	 *               </ul>
	 * @return JSONArray
	 * @throws Exception exception containing the error JSON as String as message
	 */
	public static JSONArray getLogins(JSONObject params) throws Exception {
		Object json = post(API_URL + "bancos/logins", params);
		return parseJSONArray(json);
	}

	/**
	 * Función encgarada de devolver el listado con los tipos de logins
	 * 
	 * @param bancoId <em>required (integer)</em>
	 *                <p>
	 *                Campo único que identifica el banco. Se obtiene de /bancos
	 *                </p>
	 * @return JSONArray
	 * @throws Exception exception containing the error JSON as String as message
	 */
	public static JSONArray getLogins(Integer bancoId) throws Exception {
		JSONObject params = new JSONObject();
		params.put("banco_id", bancoId);
		return getLogins(params);
	}

//------------------------------------------------------------------------------------------

//-------------------------------------GET LOGIN FIELDS-------------------------------------

	/**
	 * Función encargada de devolver los campos necesarios para el login según el
	 * tipo_login_id
	 * 
	 * @param params
	 *               <ul>
	 *               <li><strong>tipo_login_banco_id</strong>: <em>required
	 *               (integer)</em>
	 *               <p>
	 *               Campo único que identifica el tipo login del banco. Se obtiene
	 *               de /bancos/logins
	 *               </p>
	 *               </li>
	 *               </ul>
	 * @return JSONArray
	 * @throws Exception exception containing the error JSON as String as message
	 */
	public static JSONArray getLoginFields(JSONObject params) throws Exception {
		Object json = post(API_URL + "bancos/logins/campos", params);
		return parseJSONArray(json);
	}

	/**
	 * Función encargada de devolver los campos necesarios para el login según el
	 * tipo_login_id
	 * 
	 * @param tipoLoginBancoId <em>required (integer)</em>
	 *                         <p>
	 *                         Campo único que identifica el tipo login del banco.
	 *                         Se obtiene de /bancos/logins
	 *                         </p>
	 * @return JSONArray
	 * @throws Exception exception containing the error JSON as String as message
	 */
	public static JSONArray getLoginFields(Integer tipoLoginBancoId) throws Exception {
		JSONObject params = new JSONObject();
		params.put("tipo_login_banco_id", tipoLoginBancoId);
		return getLoginFields(params);
	}

//------------------------------------------------------------------------------------------

//-------------------------------------GET CREDENTIALS--------------------------------------
	/**
	 * Función encargada de devolver las credenciales y el tipo de login al que
	 * pertenecen.
	 * 
	 * @param params
	 *               <ul>
	 *               <li><strong>claveApi</strong>: <em>required (string)</em>
	 *               <p>
	 *               Token que identifica el despacho
	 *               </p>
	 *               </li>
	 *               <li><strong>empresa_id</strong>: <em>required (integer)</em>
	 *               <p>
	 *               Campo único que identifica la empresa. Se obtiene de /empresas
	 *               </p>
	 *               </li>
	 *               <li><strong>tipo_login_banco_id</strong>: <em>required
	 *               (integer)</em>
	 *               <p>
	 *               Campo único que identifica el tipo del login del banco. Se
	 *               obtiene de /bancos/logins
	 *               </p>
	 *               </li>
	 *               </ul>
	 * @return JSONObject
	 * @throws Exception exception containing the error JSON as String as message
	 */
	public static JSONObject getCredentials(JSONObject params) throws Exception {
		Object json = post(API_URL + "credenciales", params);
		return parseJSONObject(json);
	}

	/**
	 * Función encargada de devolver las credenciales y el tipo de login al que
	 * pertenecen.
	 * 
	 * @param claveApi         <em>required (string)</em>
	 *                         <p>
	 *                         Token que identifica el despacho
	 *                         </p>
	 * @param empresaId        <em>required (integer)</em>
	 *                         <p>
	 *                         Campo único que identifica la empresa. Se obtiene de
	 *                         /empresas
	 *                         </p>
	 * @param tipoLoginBancoId <em>required (integer)</em>
	 *                         <p>
	 *                         Campo único que identifica el tipo del login del
	 *                         banco. Se obtiene de /bancos/logins
	 *                         </p>
	 * @return JSONObject
	 * @throws Exception exception containing the error JSON as String as message
	 */
	public static JSONObject getCredentials(String claveApi, Integer empresaId, Integer tipoLoginBancoId)
			throws Exception {
		JSONObject params = new JSONObject();
		params.put("claveApi", claveApi);
		params.put("empresa_id", empresaId);
		params.put("tipo_login_banco_id", tipoLoginBancoId);
		return getCredentials(params);
	}

//------------------------------------------------------------------------------------------

//-------------------------------------ADD CREDENTIALS--------------------------------------	

	/**
	 * Función que inserta o modifca credenciales para la empresa y
	 * tipo_login_banco_id
	 * 
	 * @param params
	 *               <ul>
	 *               <li><strong>claveApi</strong>: <em>required (string)</em>
	 *               <p>
	 *               Token que identifica el despacho
	 *               </p>
	 *               </li>
	 *               <li><strong>empresa_id</strong>: <em>required (integer)</em>
	 *               <p>
	 *               Campo único que identifica la empresa. Se obtiene de /empresas
	 *               </p>
	 *               </li>
	 *               <li><strong>tipo_login_banco_id</strong>: <em>required
	 *               (integer)</em>
	 *               <p>
	 *               Campo único que identifica el tipo del login del banco. Se
	 *               obtiene de /bancos/logins
	 *               </p>
	 *               </li>
	 *               <li><strong>userID</strong>: <em>(string)</em>
	 *               <p>
	 *               Campo del login del tipo_login_banco_id
	 *               </p>
	 *               </li>
	 *               <li><strong>userPassword</strong>: <em>(string)</em>
	 *               <p>
	 *               Campo del login del tipo_login_banco_id
	 *               </p>
	 *               </li>
	 *               <li><strong>userPIN</strong>: <em>(string)</em>
	 *               <p>
	 *               Campo del login del tipo_login_banco_id
	 *               </p>
	 *               </li>
	 *               </ul>
	 * @return JSONObject
	 * @throws Exception exception containing the error JSON as String as message
	 */
	public static JSONObject addCredentials(JSONObject params) throws Exception {
		Object json = post(API_URL + "credenciales/add", params);
		return parseJSONObject(json);
	}

	/**
	 * Función que inserta o modifca credenciales para la empresa y
	 * tipo_login_banco_id
	 * 
	 * @param claveApi         <em>required (string)</em>
	 *                         <p>
	 *                         Token que identifica el despacho
	 *                         </p>
	 * @param empresaId        <em>required (integer)</em>
	 *                         <p>
	 *                         Campo único que identifica la empresa. Se obtiene de
	 *                         /empresas
	 *                         </p>
	 * @param tipoLoginBancoId <em>required (integer)</em>
	 *                         <p>
	 *                         Campo único que identifica el tipo del login del
	 *                         banco. Se obtiene de /bancos/logins
	 *                         </p>
	 * @param userID           <em>(string)</em>
	 *                         <p>
	 *                         Campo del login del tipo_login_banco_id
	 *                         </p>
	 * @param userPassword     <em>(string)</em>
	 *                         <p>
	 *                         Campo del login del tipo_login_banco_id
	 *                         </p>
	 * @param userPIN          <em>(string)</em>
	 *                         <p>
	 *                         Campo del login del tipo_login_banco_id
	 *                         </p>
	 * @return JSONObject
	 * @throws Exception exception containing the error JSON as String as message
	 */
	public static JSONObject addCredentials(String claveApi, Integer empresaId, Integer tipoLoginBancoId, String userID,
			String userPassword, String userPIN) throws Exception {
		JSONObject params = new JSONObject();
		params.put("claveApi", claveApi);
		params.put("empresa_id", empresaId);
		params.put("tipo_login_banco_id", tipoLoginBancoId);
		params.put("userID", userID);
		params.put("userPassword", userPassword);
		params.put("userPIN", userPIN);
		return addCredentials(params);
	}

//------------------------------------------------------------------------------------------

//--------------------------------------GET ACCOUNTS----------------------------------------
	/**
	 * Función encargada de mostrar todas las cuentas bancarias de una empresa.
	 * 
	 * @param params
	 *               <ul>
	 *               <li><strong>claveApi</strong>: <em>required (string)</em>
	 *               <p>
	 *               Token que identifica el despacho
	 *               </p>
	 *               </li>
	 *               <li><strong>empresa_id</strong>: <em>required (integer)</em>
	 *               <p>
	 *               Campo único que identifica la empresa. Se obtiene de /empresas
	 *               </p>
	 *               </li>
	 *               <li><strong>tipo_cuenta_bancaria_id</strong>:
	 *               <em>(integer)</em>
	 *               <p>
	 *               Si es cuenta corriente '1', si es tarjeta '2'
	 *               </p>
	 *               </li>
	 *               <li><strong>iban</strong>: <em>(string)</em>
	 *               <p>
	 *               Si se envia tipo_cuenta_bancaria_id e iban, Se buscara la
	 *               cuenta por el Iban
	 *               </p>
	 *               </li>
	 *               </ul>
	 * @return JSONArray
	 * @throws Exception exception containing the error JSON as String as message
	 */
	public static JSONArray getAccounts(JSONObject params) throws Exception {
		Object json = post(API_URL + "cuentas", params);
		return parseJSONArray(json);
	}

	/**
	 * Función encargada de mostrar todas las cuentas bancarias de una empresa.
	 * 
	 * @param claveApi             <em>required (string)</em>
	 *                             <p>
	 *                             Token que identifica el despacho
	 *                             </p>
	 * @param empresaId            <em>required (integer)</em>
	 *                             <p>
	 *                             Campo único que identifica la empresa. Se obtiene
	 *                             de /empresas
	 *                             </p>
	 * @param tipocuentaBancariaId <em>(integer)</em>
	 *                             <p>
	 *                             Si es cuenta corriente '1', si es tarjeta '2'
	 *                             </p>
	 * @param iban                 <em>(string)</em>
	 *                             <p>
	 *                             Si se envia tipo_cuenta_bancaria_id e iban, Se
	 *                             buscara la cuenta por el Iban
	 *                             </p>
	 * @return JSONArray
	 * @throws Exception exception containing the error JSON as String as message
	 */
	public static JSONArray getAccounts(String claveApi, Integer empresaId, Integer tipocuentaBancariaId, String iban)
			throws Exception {
		JSONObject params = new JSONObject();
		params.put("claveApi", claveApi);
		params.put("empresa_id", empresaId);
		params.put("tipo_cuenta_bancaria_id", tipocuentaBancariaId);
		params.put("iban", iban);
		return getAccounts(params);
	}
//------------------------------------------------------------------------------------------

//--------------------------------------ADD ACCOUNT-----------------------------------------
	/**
	 * Funcion encargada de añadir nuevas cuentas.
	 * 
	 * @param params
	 *               <ul>
	 *               <li><strong>claveApi</strong>: <em>required (string)</em>
	 *               <p>
	 *               Token que identifica el despacho
	 *               </p>
	 *               </li>
	 *               <li><strong>empresa_id</strong>: <em>required (integer)</em>
	 *               <p>
	 *               Campo único que identifica la empresa. Se obtiene de /empresas
	 *               </p>
	 *               </li>
	 *               <li><strong>banco_id</strong>: <em>required (integer)</em>
	 *               <p>
	 *               Campo único que identifica el banco. Se obtiene de /bancos
	 *               </p>
	 *               </li>
	 *               <li><strong>tipo_login_banco_id</strong>: <em>required
	 *               (integer)</em>
	 *               <p>
	 *               Campo único que identifica el tipo del login del banco. Se
	 *               obtiene de /bancos/logins
	 *               </p>
	 *               </li>
	 *               <li><strong>iban</strong>: <em>required (string)</em>
	 *               <p>
	 *               Iban de la cuenta bancaria para dar de alta
	 *               </p>
	 *               </li>
	 *               <li><strong>tipo_cuenta_bancaria_id</strong>: <em>(integer -
	 *               default: 1)</em>
	 *               <p>
	 *               Si es cuenta corriente '1', si es tarjeta '2'
	 *               </p>
	 *               </li>
	 *               </ul>
	 * @return JSONObject
	 * @throws Exception Exception exception containing the error JSON as String as
	 *                   message
	 */
	public static JSONObject addAccount(JSONObject params) throws Exception {
		Object json = post(API_URL + "cuentas/add", params);
		return parseJSONObject(json);
	}

	/**
	 * Funcion encargada de añadir nuevas cuentas.
	 * 
	 * @param claveApi             <em>required (string)</em>
	 *                             <p>
	 *                             Token que identifica el despacho
	 *                             </p>
	 * @param empresaId            <em>required (integer)</em>
	 *                             <p>
	 *                             Campo único que identifica la empresa. Se obtiene
	 *                             de /empresas
	 *                             </p>
	 * @param bancoId              <em>required (integer)</em>
	 *                             <p>
	 *                             Campo único que identifica el banco. Se obtiene
	 *                             de /bancos
	 *                             </p>
	 * @param tipoLoginBancoId     <em>required (integer)</em>
	 *                             <p>
	 *                             Campo único que identifica el tipo del login del
	 *                             banco. Se obtiene de /bancos/logins
	 *                             </p>
	 * @param iban                 <em>(string)</em>
	 *                             <p>
	 *                             Si se envia tipo_cuenta_bancaria_id e iban, Se
	 *                             buscara la cuenta por el Iban
	 *                             </p>
	 * @param tipoCuentaBancariaId <em>(integer - default: 1)</em>
	 *                             <p>
	 *                             Si es cuenta corriente '1', si es tarjeta '2'
	 *                             </p>
	 * @return JSONObject
	 * @throws Exception Exception exception containing the error JSON as String as
	 *                   message
	 */
	public static JSONObject addAccount(String claveApi, Integer empresaId, Integer bancoId, Integer tipoLoginBancoId,
			String iban, Integer tipoCuentaBancariaId) throws Exception {
		JSONObject params = new JSONObject();
		params.put("claveApi", claveApi);
		params.put("empresa_id", empresaId);
		params.put("banco_id", bancoId);
		params.put("tipo_login_banco_id", tipoLoginBancoId);
		params.put("iban", iban);
		params.put("tipo_cuenta_bancaria_id", tipoCuentaBancariaId);
		return addAccount(params);

	}
//------------------------------------------------------------------------------------------

//------------------------------------ADD ACCOUNT API---------------------------------------
	/**
	 * Funcion encargada de añadir nuevas cuentas desde Api Servicios.
	 * 
	 * @param params
	 *               <ul>
	 *               <li><strong>claveApi</strong>: <em>required (string)</em>
	 *               <p>
	 *               Token que identifica el despacho
	 *               </p>
	 *               </li>
	 *               <li><strong>empresa_id</strong>: <em>required (integer)</em>
	 *               <p>
	 *               Campo único que identifica la empresa. Se obtiene de /empresas
	 *               </p>
	 *               </li>
	 *               <li><strong>banco_id</strong>: <em>required (integer)</em>
	 *               <p>
	 *               Campo único que identifica el banco. Se obtiene de /bancos
	 *               </p>
	 *               </li>
	 *               <li><strong>iban</strong>: <em>required (string)</em>
	 *               <p>
	 *               Iban de la cuenta bancaria para dar de alta
	 *               </p>
	 *               </li>
	 *               <li><strong>saldo</strong>: <em>required (number)</em>
	 *               <p>
	 *               Saldo inicial al dar de alta la cuenta
	 *               </p>
	 *               </li>
	 *               <li><strong>disponible</strong>: <em>required (number)</em>
	 *               <p>
	 *               Saldo disponible al dar de alta la cuenta
	 *               </p>
	 *               </li>
	 *               <li><strong>fecha_saldo</strong>: <em>required (date)</em>
	 *               <p>
	 *               Fecha de la extaccion del saldo
	 *               </p>
	 *               </li>
	 *               <li><strong>identificador</strong>: <em>required (string)</em>
	 *               <p>
	 *               Identificador del la cuenta con el que la Api Servicios va a
	 *               hacer la consulta de movimientos, en ocasiones puede coincidir
	 *               con el iban de la cuenta.
	 *               </p>
	 *               </li>
	 *               <li><strong>api_servicio_id</strong>: <em>required
	 *               (integer)</em>
	 *               <p>
	 *               La id del servicio (#Id tabla apiServicios)
	 *               </p>
	 *               </li>
	 *               </ul>
	 * @return
	 * @throws Exception exception containing the error JSON as String as message
	 */
	public static JSONObject addAccountApi(JSONObject params) throws Exception {
		Object json = post(API_URL + "cuentas/add/api", params);
		return parseJSONObject(json);
	}

	/**
	 * Funcion encargada de añadir nuevas cuentas desde Api Servicios.
	 * 
	 * @param claveApi      <em>required (string)</em>
	 *                      <p>
	 *                      Token que identifica el despacho
	 *                      </p>
	 * @param empresaId     <em>required (integer)</em>
	 *                      <p>
	 *                      Campo único que identifica la empresa. Se obtiene de
	 *                      /empresas
	 *                      </p>
	 * @param bancoId       <em>required (integer)</em>
	 *                      <p>
	 *                      Campo único que identifica el banco. Se obtiene de
	 *                      /bancos
	 *                      </p>
	 * @param iban          <em>required (string)</em>
	 *                      <p>
	 *                      Iban de la cuenta bancaria para dar de alta
	 *                      </p>
	 * @param saldo         <em>required (number)</em>
	 *                      <p>
	 *                      Saldo inicial al dar de alta la cuenta
	 *                      </p>
	 * @param disponible    <em>required (number)</em>
	 *                      <p>
	 *                      Saldo disponible al dar de alta la cuenta
	 *                      </p>
	 * @param fechaSaldo    <em>required (date)</em>
	 *                      <p>
	 *                      Fecha de la extaccion del saldo
	 *                      </p>
	 * @param identificador <em>required (string)</em>
	 *                      <p>
	 *                      Identificador del la cuenta con el que la Api Servicios
	 *                      va a hacer la consulta de movimientos, en ocasiones
	 *                      puede coincidir con el iban de la cuenta.
	 *                      </p>
	 * @param apiServicioId <em>required (integer)</em>
	 *                      <p>
	 *                      La id del servicio (#Id tabla apiServicios)
	 *                      </p>
	 * @return JSONObject
	 * @throws Exception Exception exception containing the error JSON as String as
	 *                   message
	 */
	public static JSONObject addAccountApi(String claveApi, Integer empresaId, Integer bancoId, String iban,
			Double saldo, Double disponible, Date fechaSaldo, String identificador, Integer apiServicioId)
			throws Exception {
		JSONObject params = new JSONObject();
		params.put("claveApi", claveApi);
		params.put("empresa_id", empresaId);
		params.put("banco_id", bancoId);
		params.put("iban", iban);
		params.put("saldo", saldo);
		params.put("disponible", disponible);
		params.put("fecha_saldo", fechaSaldo);
		params.put("identificador", identificador);
		params.put("api_servicio_id", apiServicioId);
		return addAccountApi(params);
	}

//------------------------------------------------------------------------------------------

//------------------------------------ADD ENTERPRISE----------------------------------------
	/**
	 * Funcion que da de alta una empresa, la petición se hace por POST.
	 * Automáticamente se dará de alta un usuario para dicha empresa con los
	 * siguientes datos; Username - Email de la empresa. Password - <strike>Serán
	 * los 5 primeros caracteres de la id_empresa hasheada con MD5.</strike> Llegará
	 * un correo de confirmación al email de la empresa para establecer la
	 * contraseña.
	 * 
	 * @param params
	 *               <ul>
	 *               <li><strong>claveApi</strong>: <em>required (string)</em>
	 *               <p>
	 *               ClaveApi del despacho
	 *               </p>
	 *               </i>
	 *               <li><strong>nombre</strong>: <em>required (string)</em>
	 *               <p>
	 *               Nombre de la nueva empresa
	 *               </p>
	 *               </li>
	 *               <li><strong>cif</strong>: <em>required (string)</em>
	 *               <p>
	 *               Cif de la nueva empresa
	 *               </p>
	 *               </li>
	 *               <li><strong>email</strong>: <em>required (string)</em>
	 *               <p>
	 *               Email de la empresa, este campo sera el 'username' del usuario
	 *               creado pararelamente
	 *               </p>
	 *               </li>
	 *               <li><strong>nombrecorto</strong>: <em>required (string)</em>
	 *               <p>
	 *               Nombre corto de la empresa
	 *               </p>
	 *               </li>
	 *               <li><strong>url</strong>: <em>(string)</em>
	 *               <p>
	 *               Url de la web de la empresa
	 *               </p>
	 *               </li>
	 *               <li><strong>telefono</strong>: <em>(integer)</em>
	 *               <p>
	 *               Telefono de la empresa
	 *               </p>
	 *               </li>
	 *               <li><strong>direccion</strong>: <em>(string)</em>
	 *               <p>
	 *               Direccion de la empresa
	 *               </p>
	 *               </li>
	 *               </ul>
	 * @return JSONObject
	 * @throws Exception exception containing the error JSON as String as message
	 */
	public static JSONObject addEnterprise(JSONObject params) throws Exception {
		Object json = post(API_URL + "empresas/add", params);
		return parseJSONObject(json);
	}

	/**
	 * Funcion que da de alta una empresa, la petición se hace por POST.
	 * Automáticamente se dará de alta un usuario para dicha empresa con los
	 * siguientes datos; Username - Email de la empresa. Password - <strike>Serán
	 * los 5 primeros caracteres de la id_empresa hasheada con MD5.</strike> Llegará
	 * un correo de confirmación al email de la empresa para establecer la
	 * contraseña.
	 * 
	 * @param claveApi    <em>required (string)</em>
	 *                    <p>
	 *                    ClaveApi del despacho
	 *                    </p>
	 * @param nombre      <em>required (string)</em>
	 *                    <p>
	 *                    Nombre de la nueva empresa
	 *                    </p>
	 * @param cif         <em>required (string)</em>
	 *                    <p>
	 *                    Nombre de la nueva empresa
	 *                    </p>
	 * @param email       <em>required (string)</em>
	 *                    <p>
	 *                    Email de la empresa, este campo sera el 'username' del
	 *                    usuario creado pararelamente
	 *                    </p>
	 * @param nombrecorto <em>required (string)</em>
	 *                    <p>
	 *                    Nombre corto de la empresa
	 *                    </p>
	 * @param url         <em>(string)</em>
	 *                    <p>
	 *                    Url de la web de la empresa
	 *                    </p>
	 * @param telefono    <em>(integer)</em>
	 *                    <p>
	 *                    Telefono de la empresa
	 *                    </p>
	 * @param direccion   <em>(string)</em>
	 *                    <p>
	 *                    Direccion de la empresa
	 *                    </p>
	 * @return JSONObject
	 * @throws Exception exception containing the error JSON as String as message
	 */
	public static JSONObject addEnterprise(String claveApi, String nombre, String cif, String email, String nombrecorto,
			String url, String telefono, String direccion) throws Exception {
		JSONObject params = new JSONObject();
		params.put("claveApi", claveApi);
		params.put("nombre", nombre);
		params.put("cif", cif);
		params.put("email", email);
		params.put("nombrecorto", nombrecorto);
		params.put("url", url);
		params.put("telefono", telefono);
		params.put("direccion", direccion);
		return addEnterprise(params);
	}
//------------------------------------------------------------------------------------------

//------------------------------------ADD ENTERPRISE----------------------------------------
	/**
	 * Funcion que devuleve movimientos de un rango de fechas y cuenta bancaria
	 * 
	 * @param params
	 *               <ul>
	 *               <li><strong>claveApi</strong>: <em>required (string)</em>
	 *               <p>
	 *               ClaveApi del despacho
	 *               </p>
	 *               </li>
	 *               <li><strong>empresa_id</strong>: <em>required (integer)</em>
	 *               <p>
	 *               Campo único que identifica la empresa. Se obtiene de /empresas
	 *               </p>
	 *               </li>
	 *               <li><strong>fecha_desde</strong>: <em>required (date)</em>
	 *               <p>
	 *               Fecha inicio de la extracción
	 *               </p>
	 *               </li>
	 *               <li><strong>fecha_hasta</strong>: <em>required (date)</em>
	 *               <p>
	 *               Fecha final de la extracción
	 *               </p>
	 *               </li>
	 *               <li><strong>cuenta_bancaria_id</strong>: <em>required
	 *               (string)</em>
	 *               <p>
	 *               Id de la cuenta bancaria. Se obtiene de /cuentas
	 *               </p>
	 *               </li>
	 *               </ul>
	 * @return JSONArray
	 * @throws Exception Exception exception containing the error JSON as String as
	 *                   message
	 */
	public static JSONArray getTransactions(JSONObject params) throws Exception {
		Object json = post(API_URL + "movimientos", params);
		return parseJSONArray(json);
	}

	/**
	 * Funcion que devuleve movimientos de un rango de fechas y cuenta bancaria
	 * 
	 * @param claveApi         <em>required (string)</em>
	 *                         <p>
	 *                         ClaveApi del despacho
	 *                         </p>
	 * @param empresaId        <em>required (integer)</em>
	 *                         <p>
	 *                         Campo único que identifica la empresa. Se obtiene de
	 *                         /empresas
	 *                         </p>
	 * @param fechaDesde       <em>required (date)</em>
	 *                         <p>
	 *                         Fecha inicio de la extracción
	 *                         </p>
	 * @param fechaHasta       <em>required (date)</em>
	 *                         <p>
	 *                         Fecha final de la extracción
	 *                         </p>
	 * @param cuentaBancariaId <em>required (string)</em>
	 *                         <p>
	 *                         Id de la cuenta bancaria. Se obtiene de /cuentas
	 *                         </p>
	 * @return JSONArray
	 * @throws Exception Exception exception containing the error JSON as String as
	 *                   message
	 */
	public static JSONArray getTransactions(String claveApi, Integer empresaId, Date fechaDesde, Date fechaHasta,
			String cuentaBancariaId) throws Exception {
		JSONObject params = new JSONObject();
		params.put("claveApi", claveApi);
		params.put("empresa_id", empresaId);
		params.put("fecha_desde", DF.format(fechaDesde));
		params.put("fecha_hasta", DF.format(fechaHasta));
		params.put("cuenta_bancaria_id", cuentaBancariaId);
		return getTransactions(params);
	}
//------------------------------------------------------------------------------------------
}
