package net.aonsolutions.aon.api.checkit;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.BankStatement;
import com.esferalia.aon.occam.api.model.finance.CheckItParams;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.StatementConcept;
import com.esferalia.aon.occam.api.model.type.StatementStatus;
import com.esferalia.aon.occam.impl.jooq.dao.CheckItDAO;

import net.aonsolutions.aon.api.checkit.exceptions.BankException;
import net.aonsolutions.aon.api.checkit.exceptions.CheckItException;

public class CheckItAPI implements IParamNames{
	
	private CheckItAPI() {
		throw new IllegalStateException("Utility class");
	}

	private static final String API_URL = "https://www.checkitbancario.com/openapi/";
	public static final String API_KEY = "84d9ee44e457ddef7f2c4f25dc8fa865";
	public static final String CHECKIT_R1 = "CHECKIT";
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
			return null;
		} catch (IOException e) {			
			return null;
		}
	}

	private static JSONArray parseJSONArray(Object json) throws CheckItException {
		if (json == null)
			return null;
		try {
			return (JSONArray) json;
		} catch (ClassCastException e) {
			throw new CheckItException(json.toString());
		}
	}

	private static JSONObject parseJSONObject(Object json) throws CheckItException {
		if (json == null)
			return null;
		try {
			JSONObject jsonObj = (JSONObject) json;
			String result = jsonObj.optString("result");
			if (result.isEmpty() || jsonObj.optString("result").equalsIgnoreCase("Success")) {
				return jsonObj;
			} else {				
				throw new CheckItException(json.toString());
			}
		} catch (ClassCastException e) {
			throw new CheckItException(json.toString());
		}

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
	 * @throws CheckItException exception containing the error JSON as String as
	 *                          message
	 */
	public static JSONArray getBanks(JSONObject params) throws CheckItException {
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
	 * @throws CheckItException exception containing the error JSON as String as
	 *                          message
	 */
	public static JSONArray getBanks(String claveApi) throws CheckItException {
		JSONObject params = new JSONObject();
		params.put(API_KEY_PARAM, claveApi);
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
	 * @throws CheckItException exception containing the error JSON as String as
	 *                          message
	 */
	public static JSONArray getLogins(JSONObject params) throws CheckItException {
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
	 * @throws CheckItException exception containing the error JSON as String as
	 *                          message
	 */
	public static JSONArray getLogins(Integer bancoId) throws CheckItException {
		JSONObject params = new JSONObject();
		params.put(BANK_ID_PARAM, bancoId);
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
	 * @throws CheckItException exception containing the error JSON as String as
	 *                          message
	 */
	public static JSONArray getLoginFields(JSONObject params) throws CheckItException {
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
	 * @throws CheckItException exception containing the error JSON as String as
	 *                          message
	 */
	public static JSONArray getLoginFields(Integer tipoLoginBancoId) throws CheckItException {
		JSONObject params = new JSONObject();
		params.put(LOGIN_TYPE_ID_PARAM, tipoLoginBancoId);
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
	 * @throws CheckItException exception containing the error JSON as String as
	 *                          message
	 */
	public static JSONObject getCredentials(JSONObject params) throws CheckItException {
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
	 * @throws CheckItException exception containing the error JSON as String as
	 *                          message
	 */
	public static JSONObject getCredentials(String claveApi, Integer empresaId, Integer tipoLoginBancoId)
			throws CheckItException {
		JSONObject params = new JSONObject();
		params.put(API_KEY_PARAM, claveApi);
		params.put(ENTERPRISE_ID_PARAM, empresaId);
		params.put(LOGIN_TYPE_ID_PARAM, tipoLoginBancoId);
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
	 * @throws CheckItException exception containing the error JSON as String as
	 *                          message
	 */
	public static JSONObject addCredentials(JSONObject params) throws CheckItException {
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
	 * @throws CheckItException exception containing the error JSON as String as
	 *                          message
	 */
	public static JSONObject addCredentials(String claveApi, Integer empresaId, Integer tipoLoginBancoId, String userID,
			String userPassword, String userPIN) throws CheckItException {
		JSONObject params = new JSONObject();
		params.put(API_KEY_PARAM, claveApi);
		params.put(ENTERPRISE_ID_PARAM, empresaId);
		params.put(LOGIN_TYPE_ID_PARAM, tipoLoginBancoId);
		params.put(USER_ID_PARAM, userID);
		params.put(USER_PASSWORD_PARAM, userPassword);
		params.put(USER_PIN_PARAM, userPIN);
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
	 * @throws CheckItException exception containing the error JSON as String as
	 *                          message
	 */
	public static JSONArray getAccounts(JSONObject params) throws CheckItException {
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
	 * @throws CheckItException exception containing the error JSON as String as
	 *                          message
	 */
	public static JSONArray getAccounts(String claveApi, Integer empresaId, Integer tipocuentaBancariaId, String iban)
			throws CheckItException {
		JSONObject params = new JSONObject();
		params.put(API_KEY_PARAM, claveApi);
		params.put(ENTERPRISE_ID_PARAM, empresaId);
		params.put(ACCOUNT_TYPE_ID_PARAM, tipocuentaBancariaId);
		params.put(IBAN_PARAM, iban);
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
	 * @throws CheckItException Exception exception containing the error JSON as
	 *                          String as message
	 */
	public static JSONObject addAccount(JSONObject params) throws CheckItException {
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
	 * @throws CheckItException Exception exception containing the error JSON as
	 *                          String as message
	 */
	public static JSONObject addAccount(String claveApi, Integer empresaId, Integer bancoId, Integer tipoLoginBancoId,
			String iban, Integer tipoCuentaBancariaId) throws CheckItException {
		JSONObject params = new JSONObject();
		params.put(API_KEY_PARAM, claveApi);
		params.put(ENTERPRISE_ID_PARAM, empresaId);
		params.put(BANK_ID_PARAM, bancoId);
		params.put(LOGIN_TYPE_ID_PARAM, tipoLoginBancoId);
		params.put(IBAN_PARAM, iban);
		params.put(ACCOUNT_TYPE_ID_PARAM, tipoCuentaBancariaId);
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
	 * @throws CheckItException exception containing the error JSON as String as
	 *                          message
	 */
	public static JSONObject addAccountApi(JSONObject params) throws CheckItException {
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
	 * @throws CheckItException Exception exception containing the error JSON as
	 *                          String as message
	 */
	public static JSONObject addAccountApi(String claveApi, Integer empresaId, Integer bancoId, String iban,
			Double saldo, Double disponible, Date fechaSaldo, String identificador, Integer apiServicioId)
			throws CheckItException {
		JSONObject params = new JSONObject();
		params.put(API_KEY_PARAM, claveApi);
		params.put(ENTERPRISE_ID_PARAM, empresaId);
		params.put(BANK_ID_PARAM, bancoId);
		params.put(IBAN_PARAM, iban);
		params.put(BALANCE_PARAM, saldo);
		params.put(AVAILABLE_PARAM, disponible);
		params.put(BALANCE_DATE_PARAM, fechaSaldo);
		params.put(ID_PARAM, identificador);
		params.put(SERVICE_API_ID_PARAM, apiServicioId);
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
	 * @throws BankException
	 */
	public static JSONObject addEnterprise(JSONObject params) throws BankException {
		String emailField = "email";
		if (params.optString(emailField) == null || params.optString(emailField).isEmpty()) {
			if (params.optString("url") == null || params.optString("url").isEmpty()) {
				throw new BankException(
						"If email is null, the domain must be sent as <<url>> parameter in order to create a fake one");
			} else {
				String domain = params.optString("url");
				String emailDomain = domain.lastIndexOf('-') != -1 ? domain.substring(domain.lastIndexOf('-') + 1)
						: domain;
				String emailUser = domain.lastIndexOf('-') != -1 ? domain.substring(0, domain.lastIndexOf('-'))
						: "checkit";

				String email = emailUser + "@" + emailDomain;
				params.put(emailField, email);
				
			}
		}

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
	 * @throws BankException 
	 */
	public static JSONObject addEnterprise(String claveApi, String nombre, String cif, String email, String nombrecorto,
			String url, String telefono, String direccion) throws BankException {
		JSONObject params = new JSONObject();
		params.put(API_KEY_PARAM, claveApi);
		params.put(NAME_PARAM, nombre);
		params.put(CIF_PARAM, cif);
		params.put(EMAIL_PARAM, email);
		params.put(SHORT_NAME_PARAM, nombrecorto);
		params.put(URL_PARAM, url);
		params.put(PHONE_PARAM, telefono);
		params.put(ADDRESS_PARAM, direccion);
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
	 * @throws CheckItException Exception exception containing the error JSON as
	 *                          String as message
	 */
	public static JSONArray getTransactions(JSONObject params) throws CheckItException {
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
	 * @throws CheckItException Exception exception containing the error JSON as
	 *                          String as message
	 */
	public static JSONArray getTransactions(String claveApi, Integer empresaId, Date fechaDesde, Date fechaHasta,
			String cuentaBancariaId) throws CheckItException {
		JSONObject params = new JSONObject();
		params.put(API_KEY_PARAM, claveApi);
		params.put(ENTERPRISE_ID_PARAM, empresaId);
		params.put(DATE_FROM_PARAM, formatDateForTransactions(fechaDesde));
		params.put(DATE_TO_PARAM, formatDateForTransactions(fechaHasta));
		params.put(ACCOUNT_ID_PARAM, cuentaBancariaId);
		return getTransactions(params);
	}
//------------------------------------------------------------------------------------------
	
	public static String getAccountIdByIBAN(String claveApi, Integer empresaId, String iban) throws CheckItException {
		String errMsg = "The requested account could not be found";
		JSONArray accountsJson = CheckItAPI.getAccounts(claveApi, empresaId, null, iban);
		if (accountsJson == null) {
			throw new CheckItException(errMsg);
		}
		JSONObject accountJson = accountsJson.optJSONObject(0);
		if (accountJson == null)
			throw new CheckItException(errMsg);

		String accountId = accountJson.optString("id_cuentabancaria");
		if (accountId == null || accountId.isEmpty())
			throw new CheckItException(errMsg);
		return accountId;
	}
	
	public static List<BankStatement> getBankStatements(Integer empresaId, String accountId, Date lastOperationDate, Integer maximumId) throws BankException {
		Date today = new Date();

		JSONObject requestParams = new JSONObject();
		
		requestParams.put(API_KEY_PARAM, API_KEY);
		requestParams.put(ENTERPRISE_ID_PARAM, empresaId);
		requestParams.put(DATE_TO_PARAM, formatDateForTransactions(today)); // HOY
		requestParams.put(ACCOUNT_ID_PARAM, accountId);
		requestParams.put(DATE_FROM_PARAM, formatDateForTransactions(lastOperationDate)); // REQUEST PARAMS COMPLETED

		JSONArray transactionsArray = CheckItAPI.getTransactions(requestParams);
		
		List<BankStatement> bankStatements = new LinkedList<>();
		
		for (int i = 0; i < transactionsArray.length(); i++) {

			JSONObject transactionJson = transactionsArray.optJSONObject(i);

			int movementId = transactionJson.optInt("id_movimiento");
			
			if (maximumId == null)
				maximumId = 0;
			
			if (movementId > maximumId) {
				Date operationDate = parseTZDate(transactionJson.optString("fecha_operacion"));

				String description = transactionJson.optString("descripcion");

				if (description != null)
					description = description.length() > 80 ? description.substring(0, 80) : description;

				Double amount = transactionJson.optDouble("importe");
				amount = amount.isNaN() ? 0.00 : amount;

				boolean bpayment = amount < 0;
				
				
				BankStatement bankStatement = new BankStatement();
				bankStatement.setOperationDate(operationDate);
				bankStatement.setCommonConcept(StatementConcept.UNKNOWN);
				bankStatement.setPayment(bpayment);
				bankStatement.setAmount(Math.abs(amount));
				bankStatement.setDescription(description);
				bankStatement.setStatus(StatementStatus.PENDING);
				bankStatement.setReference1(CHECKIT_R1);
				bankStatement.setReference2(leadingZeros(movementId, 16));
				
				bankStatements.add(bankStatement);
			}

		}
		bankStatements.sort((b1, b2) -> b1.getReference2().compareTo(b2.getReference2()));
		return bankStatements;

	}
	
	/**
	 * Method which picks up bank transactions from CheckIt and records them into
	 * the DB.
	 * 
	 * @param params CheckItParams object
	 * @return The number of rows inserted into the DB
	 * @throws BankException
	 */
	public static int insertTransactions(CheckItParams params)
			throws BankException {
		
		
		String domainName = params.getDomainName();
		String user = params.getUser();
		Integer domainId = params.getDomainId();

		String iban = params.getIban();
		Integer empresaId = params.getCheckitEmpresaId();
		try (AONContext aonContext = AONContext.getAONContext(domainName, domainId, user)) {
					RegistryBank rBank = CheckItDAO.getRbankByIban(aonContext, iban);
					Date lastDate = CheckItDAO.getLastOperationDateDB(aonContext, domainId, rBank);
					Map<String, Date> idAndDate = CheckItDAO.getMaxMovementIdAndDate(aonContext, domainId, rBank);
					Integer movId = Integer.valueOf(idAndDate.keySet().stream().findFirst().orElse("0"));
					List<BankStatement> bankStatements = getBankStatements(empresaId, getAccountIdByIBAN(API_KEY, empresaId, iban), lastDate, movId);
					CheckItDAO.completeBankStatements(aonContext, domainId, iban, bankStatements);
					return 	aonContext.getDslContext().transactionResult( 
						confi -> CheckItDAO.insertStatements(aonContext, bankStatements)
					);		
		}

		

		
		
	}
	
	/**
	 * Method which picks up bank transactions from CheckIt and records them into
	 * the DB.
	 * 
	 * @param domainName The <u>AON DB</u> domain name
	 * @param user       The <u>AON DB</u> user name
	 * @param empresaId  The <u>CheckIt</u> Enterprise ID
	 * @param iban       The bank account number
	 * @return The number of rows inserted into the DB
	 * @throws BankException
	 */
	public static int insertTransactions(String domainName, Integer domainId, String user, Integer empresaId, String iban)
			throws BankException {
		CheckItParams params = new CheckItParams();
		params.setDomainName(domainName);
		params.setDomainId(domainId);
		params.setUser(user);
		params.setCheckitEmpresaId(empresaId);
		params.setIban(iban);
		
		return insertTransactions(params);
	}
	
	private static String formatDateForTransactions(Date date) {
		try {
			return DF.format(date);
		} catch (Exception e) {
			return null;
		}
	}

	private static Date parseTZDate(String dateStr) throws CheckItException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", new Locale("es", "ES"));
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			return format.parse(dateStr);
		} catch (ParseException e) {
			throw new CheckItException("Date could not be parsed: " + dateStr);
		}
	}
	
	private static String leadingZeros(Integer id, int fieldSize) {
		if (id != null) {
			StringBuilder sb = new StringBuilder(String.valueOf(id));
			while (sb.length() < fieldSize) {
				sb.insert(0, 0);
			}
			return sb.toString();
		} else
			return null;
	}
	
}
