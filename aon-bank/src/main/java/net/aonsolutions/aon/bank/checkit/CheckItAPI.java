package net.aonsolutions.aon.bank.checkit;

import static com.esferalia.aon.occam.impl.jooq.dao.CheckItDAO.CHECKIT_R1;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankAccount;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankStatement;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItLog;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItParams;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckitUnlinkedBankAccount;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.StatementConcept;
import com.esferalia.aon.occam.api.model.type.StatementStatus;
import com.esferalia.aon.occam.impl.jooq.dao.CheckItDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

public class CheckItAPI implements IParamNames{
	
	private CheckItAPI() {
		throw new IllegalStateException("Utility class");
	}

	private static final DateFormat DF_TIME = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", new Locale("es", "ES") );
	
	private static final String BASE_URL = "https://www.checkitbancario.com/";
	private static final String API_URL = BASE_URL + "openapi/";
	private static final String LOGO_BASE_URL = BASE_URL + "login/img/logos/bancos/";
	private static final String API_KEY = "84d9ee44e457ddef7f2c4f25dc8fa865";
	private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd", new Locale("es", "ES"));

	private static Object post(String url, JSONObject params) throws CheckItException {
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpPost post = new HttpPost(url);
			StringEntity entity = new StringEntity(params.toString());
			post.addHeader("Content-Type", "application/json");
			post.setEntity(entity);
			try (CloseableHttpResponse resp = client.execute(post)) {
				if (resp.getStatusLine().getStatusCode() != 200)
					throw new CheckItException(CheckItException.NO_CONNECTION_MSG);
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
			String message= jsonObj.optString("message");
			if (result.isEmpty() || result.equalsIgnoreCase("Success") || AonStringUtils.containsIgnoreCase(message, "cuenta creada") || AonStringUtils.containsIgnoreCase(result, "cuenta creada")) {
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
	 * @return JSONArray
	 * @throws CheckItException exception containing the error JSON as String as
	 *                          message
	 */
	public static JSONArray getBanks() throws CheckItException {
		JSONObject params = new JSONObject();
		params.put(API_KEY_PARAM, API_KEY);
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
	 * 
	 * @return JSONObject
	 * @throws CheckItException exception containing the error JSON as String as
	 *                          message
	 */
	public static JSONObject getCredentials(Integer empresaId, Integer tipoLoginBancoId)
			throws CheckItException {
		JSONObject params = new JSONObject();
		params.put(API_KEY_PARAM, API_KEY);
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
	 * 
	 * @return JSONObject
	 * @throws CheckItException exception containing the error JSON as String as
	 *                          message
	 */
	public static JSONObject addCredentials(Integer empresaId, Integer tipoLoginBancoId, String userID, String userPassword,
			String userPIN) throws CheckItException {
		JSONObject params = new JSONObject();
		params.put(API_KEY_PARAM, API_KEY);
		params.put(ENTERPRISE_ID_PARAM, empresaId);
		params.put(LOGIN_TYPE_ID_PARAM, tipoLoginBancoId);
		params.put(USER_ID_PARAM, userID);
		params.put(USER_PASSWORD_PARAM, userPassword);
		params.put(USER_PIN_PARAM, userPIN);
		return addCredentials(params);
	}

//------------------------------------------------------------------------------------------
	
//------------------------------------ADD EXTRA FIELD---------------------------------------
	/**
	 * Función que inserta el campo extra en credenciales
	 * para la empresa y tipo_login_banco_id
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
	 *               <li><strong>campo_extra</strong>: <em>(string)</em>
	 *               <p>
	 *               campo_extra
	 *               </p>
	 *               </li>
	 *               </ul>
	 * @return JSONObject
	 * @throws CheckItException exception containing the error JSON as String as
	 *                          message
	 */
	public static JSONObject addExtraField(JSONObject params) throws CheckItException {
		Object json = post(API_URL + "credenciales/campoextra", params);
		return parseJSONObject(json);
	}
	
	public static JSONObject addExtraField(Integer empresaId, Integer tipoLoginBancoId, String extraField) throws CheckItException {
		JSONObject params = new JSONObject();
		params.put(API_KEY_PARAM, API_KEY);
		params.put(ENTERPRISE_ID_PARAM, empresaId);
		params.put(LOGIN_TYPE_ID_PARAM, tipoLoginBancoId);
		params.put(EXTRA_FIELD, extraField);
		return addExtraField(params);
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
	 * 
	 * @return JSONArray
	 * @throws CheckItException exception containing the error JSON as String as
	 *                          message
	 */
	public static JSONArray getAccounts(Integer empresaId, Integer tipocuentaBancariaId, String iban)
			throws CheckItException {
		JSONObject params = new JSONObject();
		params.put(API_KEY_PARAM, API_KEY);
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
	 * 
	 * @return JSONObject
	 * @throws CheckItException Exception exception containing the error JSON as
	 *                          String as message
	 */
	public static JSONObject addAccount(Integer empresaId, Integer bancoId, Integer tipoLoginBancoId, String iban,
			Integer tipoCuentaBancariaId) throws CheckItException {
		JSONObject params = new JSONObject();
		params.put(API_KEY_PARAM, API_KEY);
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
	 * 
	 * @return JSONObject
	 * @throws CheckItException Exception exception containing the error JSON as
	 *                          String as message
	 */
	public static JSONObject addAccountApi(Integer empresaId, Integer bancoId, String iban, Double saldo,
			Double disponible, Date fechaSaldo, String identificador, Integer apiServicioId)
			throws CheckItException {
		JSONObject params = new JSONObject();
		params.put(API_KEY_PARAM, API_KEY);
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
	
//---------------------------------------GET LOGS-------------------------------------------
	
	public static JSONArray getLogs(JSONObject params) throws CheckItException {
		Object json = post(API_URL + "logs/robot/list", params);
		return parseJSONArray(json);
	}
	
	public static JSONArray getLogs(Integer empresaId, Integer cuentabancariaId) throws CheckItException {
		JSONObject params = new JSONObject();
		params.put(API_KEY_PARAM, API_KEY);
		params.put(ENTERPRISE_ID_PARAM, empresaId);
		params.put("cuentabancaria_id", cuentabancariaId);
		return getLogs(params);
	}
	
//------------------------------------------------------------------------------------------
	
//------------------------------------GET ENTERPRISE----------------------------------------
	
	public static JSONArray getEnterprise(JSONObject params) throws CheckItException {
		Object json = post(API_URL + "empresas", params);
		return parseJSONArray(json);
	}
	
	public static JSONArray getEnterprise(String cif) throws CheckItException {
		JSONObject params = new JSONObject();
		params.put(API_KEY_PARAM, API_KEY);
		params.put(CIF_PARAM, cif);
		return getEnterprise(params);
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
	public static JSONObject addEnterprise(JSONObject params) throws CheckItException {
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
	public static JSONObject addEnterprise(String nombre, String cif, String email, String nombrecorto,
			String url, String telefono, String direccion) throws CheckItException {
		JSONObject params = new JSONObject();
		params.put(API_KEY_PARAM, API_KEY);
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
	 * 
	 * @return JSONArray
	 * @throws CheckItException Exception exception containing the error JSON as
	 *                          String as message
	 */
	public static JSONArray getTransactions(Integer empresaId, Date fechaDesde, Date fechaHasta, String cuentaBancariaId) throws CheckItException {
		JSONObject params = new JSONObject();
		params.put(API_KEY_PARAM, API_KEY);
		params.put(ENTERPRISE_ID_PARAM, empresaId);
		params.put(DATE_FROM_PARAM, formatDateForTransactions(fechaDesde));
		params.put(DATE_TO_PARAM, formatDateForTransactions(fechaHasta));
		params.put(ACCOUNT_ID_PARAM, cuentaBancariaId);
		return getTransactions(params);
	}
//------------------------------------------------------------------------------------------
	
	public static Integer getAccountIdByIBAN(Integer empresaId, String iban) throws CheckItException {
		String errMsg = "The requested account could not be found";
		JSONArray accountsJson = CheckItAPI.getAccounts(empresaId, null, iban);
		if (accountsJson == null) {
			throw new CheckItException(errMsg);
		}
		JSONObject accountJson = accountsJson.optJSONObject(0);
		if (accountJson == null)
			throw new CheckItException(errMsg);

		Integer accountId = accountJson.optInt("id_cuentabancaria", 0);
		if (accountId == 0)
			throw new CheckItException(errMsg);
		return accountId;
	}
	
	public static List<CheckItLog> getCheckItLogs(Integer empresaId, Integer cuentabancariaId) throws CheckItException {
		JSONArray logsJson = getLogs(empresaId, cuentabancariaId);
		List<CheckItLog> logsList = new LinkedList<>();
		if (logsJson != null) {
			for (int i=0; i<logsJson.length(); i++) {
				CheckItLog log = logFromJsonToObject(logsJson.optJSONObject(i));
				if (log != null) {
					logsList.add(log);
				}
			}
			return logsList;
		} else
			return Collections.emptyList();
	}
	
	private static CheckItLog logFromJsonToObject(JSONObject json) {
		if (json == null)
			return null;
		Date created;
		try {		
			created = parseTZDate(json.optString("created", ""));
		} catch (CheckItException e) {
			created = null;
		}
		
		return new CheckItLog()
				.setBankAccountId(json.optInt("cuentabancaria_id"))
				.setErrorMessage(json.optString("error_message"))
				.setUserError(json.optInt("is_user_error", 0) == 1)
				.setPending(json.optInt("is_pending", 0) == 1)
				.setCreated(created);
				
	}
	
	public static List<CheckItBankStatement> getAllBankStatements(Integer empresaId, Integer accountId) throws CheckItException {
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 1900);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		
		return getBankStatementsFromTo(empresaId, accountId, new Date(), cal.getTime());
		
		/*JSONObject requestParams = new JSONObject();
		requestParams.put(API_KEY_PARAM, API_KEY);
		requestParams.put(ENTERPRISE_ID_PARAM, empresaId);
		requestParams.put(DATE_TO_PARAM, formatDateForTransactions(new Date())); // HOY
		requestParams.put(ACCOUNT_ID_PARAM, accountId);
		requestParams.put(DATE_FROM_PARAM, "1900-01-01"); // REQUEST PARAMS COMPLETED

		JSONArray transactionsArray = CheckItAPI.getTransactions(requestParams);
		
		List<CheckItBankStatement> bankStatements = new LinkedList<>();
		
		for (int i = 0; i < transactionsArray.length(); i++) {

			JSONObject transactionJson = transactionsArray.optJSONObject(i);

			CheckItBankStatement bankStatement = bankStatementFromJson(transactionJson);
			bankStatements.add(bankStatement);

		}
		bankStatements.sort((b1, b2) -> b2.getReference2().compareTo(b1.getReference2()));
		return bankStatements;*/
	}
	
	public static List<CheckItBankStatement> getBankStatementsFromTo(Integer empresaId, Integer accountId, Date startDate, Date endDate) throws CheckItException {
		JSONObject requestParams = new JSONObject();
		
		requestParams.put(API_KEY_PARAM, API_KEY);
		requestParams.put(ENTERPRISE_ID_PARAM, empresaId);
		requestParams.put(ACCOUNT_ID_PARAM, accountId);
		requestParams.put(DATE_FROM_PARAM, formatDateForTransactions(startDate));
		requestParams.put(DATE_TO_PARAM, formatDateForTransactions(endDate)); // REQUEST PARAMS COMPLETED
		
		JSONArray transactionsArray = CheckItAPI.getTransactions(requestParams);
		
		List<CheckItBankStatement> bankStatements = new LinkedList<>();
		
		for (int i = 0; i < transactionsArray.length(); i++) {
			
			JSONObject transactionJson = transactionsArray.optJSONObject(i);
			
			CheckItBankStatement bankStatement = bankStatementFromJson(transactionJson);
			bankStatements.add(bankStatement);
			
		}
		bankStatements.sort((b1, b2) -> b2.getReference2().compareTo(b1.getReference2()));
		return bankStatements;
	}
	
	public static List<CheckItBankStatement> getBankStatements(Integer empresaId, Integer accountId, Date lastOperationDate, Integer maximumId) throws CheckItException {
		Date today = new Date();
		
		
		Calendar nextOperationCalendar= Calendar.getInstance();
		nextOperationCalendar.setTime(lastOperationDate);
		nextOperationCalendar.add(Calendar.DATE, 1);
		Date nextOperationDate = clearDate(nextOperationCalendar.getTime());
		
		
		JSONObject requestParams = new JSONObject();
		
		requestParams.put(API_KEY_PARAM, API_KEY);
		requestParams.put(ENTERPRISE_ID_PARAM, empresaId);
		requestParams.put(DATE_TO_PARAM, formatDateForTransactions(today)); // HOY
		requestParams.put(ACCOUNT_ID_PARAM, accountId);
		requestParams.put(DATE_FROM_PARAM, formatDateForTransactions(lastOperationDate)); // REQUEST PARAMS COMPLETED

		JSONArray transactionsArray = CheckItAPI.getTransactions(requestParams);
		
		List<CheckItBankStatement> bankStatements = new LinkedList<>();
		
		for (int i = 0; i < transactionsArray.length(); i++) {

			JSONObject transactionJson = transactionsArray.optJSONObject(i);

			int movementId = transactionJson.optInt("id_movimiento");
			
			Date operationDate = clearDate(parseTZDate(transactionJson.optString("fecha_operacion")));
			
			if (
					 (maximumId == null && operationDate.after(nextOperationDate)) ||
					 (maximumId != null && maximumId != 0 && movementId > maximumId)
			) {
				CheckItBankStatement bankStatement = bankStatementFromJson(transactionJson);
				bankStatements.add(bankStatement);
			}

		}
		bankStatements.sort((b1, b2) -> b1.getReference2().compareTo(b2.getReference2()));
		return bankStatements;

	}

	private static CheckItBankStatement bankStatementFromJson(JSONObject transactionJson) throws CheckItException {
		Date operationDate = parseTZDate(transactionJson.optString("fecha_operacion"));

		String description = transactionJson.optString("descripcion");

		if (description != null)
			description = description.length() > 80 ? description.substring(0, 80) : description;

		Double amount = transactionJson.optDouble("importe");
		amount = amount.isNaN() ? 0.00 : amount;
		
		Double currentBalance = transactionJson.optDouble("saldo");
		currentBalance = currentBalance.isNaN() ? 0.00 : currentBalance;

		Integer checkitMovementId = transactionJson.optInt("id_movimiento");

		
		boolean bpayment = amount < 0;
		
		
		CheckItBankStatement bankStatement = new CheckItBankStatement();
		bankStatement
			.setOperationDate(operationDate)
			.setCommonConcept(StatementConcept.UNKNOWN)
			.setPayment(bpayment)
			.setAmount(Math.abs(amount))
			.setDescription(description)
			.setStatus(StatementStatus.PENDING)
			.setReference1(CHECKIT_R1)
			.setReference2(leadingZeros(transactionJson.optInt("id_movimiento"), 16));
		bankStatement
			.setCurrentBalance(currentBalance)
			.setCheckitMovementId(checkitMovementId);
		
		return bankStatement;
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
			throws CheckItException {
		
		
		String domainName = params.getDomainName();
		String user = params.getUser();
		Integer domainId = params.getDomainId();

		String iban = params.getIban();
		Integer empresaId = params.getCheckitEmpresaId();
		List<CheckItBankStatement> bankStatements = getNewMovements(domainName, domainId, user, empresaId, iban);
		try (CloseableAONContext aonContext = AONContext.getAONContext(domainName, domainId, user)) {
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
			throws CheckItException {
		CheckItParams params = new CheckItParams();
		params.setDomainName(domainName);
		params.setDomainId(domainId);
		params.setUser(user);
		params.setCheckitEmpresaId(empresaId);
		params.setIban(iban);
		
		return insertTransactions(params);
	}
	
	public static List<CheckItBankStatement> getAllMovements(String domainName, Integer domainId, String user, Integer empresaId, String iban) throws CheckItException {
		try (CloseableAONContext aonContext = AONContext.getAONContext(domainName, domainId, user)) {		
//			RegistryBank rBank = CheckItDAO.getRbankByIban(aonContext, iban);
			List<CheckItBankStatement> bankStatements = getAllBankStatements(empresaId, getAccountIdByIBAN(empresaId, iban));
//			CheckItDAO.completeBankStatements(aonContext, domainId, iban, bankStatements);
			return bankStatements;
		}
	}
	
	public static List<CheckItBankStatement> getNewMovements(String domainName, Integer domainId, String user, Integer empresaId, String iban) throws CheckItException {
		try (CloseableAONContext aonContext = AONContext.getAONContext(domainName, domainId, user)) {		
			RegistryBank rBank = CheckItDAO.getRbankByIban(aonContext, iban);
			Date lastDate = CheckItDAO.getLastOperationDateDB(aonContext, domainId, rBank);
			Pair<String, Date> idAndDate = CheckItDAO.getMaxMovementIdAndDate(aonContext, domainId, rBank);
			Integer movId = null;
			if (AonDateUtils.isSameDay(lastDate, idAndDate != null ? idAndDate.getValue() : null)) {				
				movId = idAndDate != null ? Integer.valueOf(idAndDate.getKey()) : null;
			} else {
				lastDate = AonDateUtils.addDays(lastDate, 1);
			}
			List<CheckItBankStatement> bankStatements = getBankStatements(empresaId, getAccountIdByIBAN(empresaId, iban), lastDate, movId);
			CheckItDAO.completeBankStatements(aonContext, domainId, iban, bankStatements);
			return bankStatements;
		}
	}
	
	public static List<CheckItBankStatement> getMovements(String domainName, Integer domainId, String user, Integer empresaId, String iban, Date startDate, Date endDate) throws CheckItException {
		try (CloseableAONContext aonContext = AONContext.getAONContext(domainName, domainId, user)) {		
//			RegistryBank rBank = CheckItDAO.getRbankByIban(aonContext, iban);
			List<CheckItBankStatement> bankStatements = getAllBankStatements(empresaId, getAccountIdByIBAN(empresaId, iban));
//			CheckItDAO.completeBankStatements(aonContext, domainId, iban, bankStatements);
			return bankStatements;
		}
	}
	
	public static int getNewMovementsNumber(String domainName, Integer domainId, String user, Integer empresaId, String iban) {
		try {
			return getNewMovements(domainName, domainId, user, empresaId, iban).size();
		} catch (Exception e) {
			return 0;
		}
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

	
	
	
	public static LinkedList<CheckItBankAccount> getLinkedAccountsToDisplay(String domainName, Integer domainId, String user, Integer empresaId ) throws CheckItException {
		List<String> activeIbans= CheckItDAO.getActiveIbans(domainName, domainId, user);
		return getAccounts(empresaId, activeIbans);
	}
	
//	public static LinkedList<CheckItBankAccount> getAccountsFromDB(String domainName, Integer domainId, String user) throws CheckItException {
//		LinkedList<RegistryBank> activeAccounts = CheckItDAO.getActiveAccounts(domainName, domainId, user);
//		activeAccounts.stream().map(acc -> {
//			
//			Aon.
//			
//			return null;
//		}
//		).forEach(System.out::println);
//	}
	
	public static LinkedList<CheckItBankAccount> getAccounts( Integer empresaId, List<String> activeIbans ) throws CheckItException {
		JSONArray accounts = getAccounts(empresaId, 1, null);
		JSONArray allBanks = getBanks();
		
		for (int i=0; i<accounts.length(); i++) {
			String iban = accounts.optJSONObject(i).optString(CCC);
			if (!activeIbans.contains(iban))
				accounts.remove(i--);
			if(i+1 >= accounts.length())
				break;
		}
		
		LinkedList<CheckItBankAccount> acc = new LinkedList<>();
		for (int i = 0; i < (accounts != null ? accounts.length() : 0) ; i++)  {
			JSONObject obj = accounts.getJSONObject(i);
			String fecha = obj.optString( BALANCE_DATE_PARAM );
			acc.add( new CheckItBankAccount()
				.setCcc( obj.optString(CCC))
				.setAtDate( (AonStringUtils.isNotBlank(fecha)? parseDateFromJSON(fecha):null) ) 
				.setBankId(obj.optInt(BANK_ID_PARAM, 0))
				.setBank( obj.optString(BANK_NAME_PARAM))
				.setBankAccountId(obj.optInt("id_cuentabancaria", 0))
				.setBalance(obj.optDouble("saldo", 0))
				.setRemainder(obj.optDouble("disponible", 0))
				.setBankAccountType(obj.optInt("tipo_cuenta_bancaria_id", 0))
				.setBankLoginType(obj.optInt("tipo_login_banco_id", 0))
				.setLogs(getCheckItLogs(empresaId, obj.optInt("id_cuentabancaria", 0)))
				.setLogo(getLogo(allBanks, obj.optInt(BANK_ID_PARAM, 0)))
			);
		}
		return acc;
	}
	
	private static String getLogo(JSONArray allBanks, int bankId) {
		if (bankId < 1)
			return null;
		JSONObject obj = getBank(allBanks, bankId);
		if (obj != null) {
			String png = obj.optString("logo");
			if (png == null || png.isEmpty())
				return null;
			else
				return LOGO_BASE_URL + png;
		} else {
			return null;
		}
	}
	
	
	private static JSONObject getBank(JSONArray allBanks, int id) {
		if (id < 1)
			return null;
		for (int i=0;i<allBanks.length(); i++) {
			JSONObject obj = allBanks.optJSONObject(i);
			if (obj != null) {
				if (obj.optInt(BANK_ID) == id)
					return obj;
			}
		}
		return null;
	}
	
	public static  List<CheckitUnlinkedBankAccount> getUnlinkedActive(String domainName, Integer domainId, String user, Integer empresaId) throws CheckItException {
		
		JSONArray accounts = getAccounts(empresaId, 1, null);
		List<String> unlinkedIbans = new LinkedList<>();
		if (accounts != null) {
			for (int i=0; i<accounts.length(); i++) {
				unlinkedIbans.add(accounts.optJSONObject(i).optString(CCC));
			}
			
			AON.getCompanyBanks(domainName, domainId, user);
			
			Company company = AON.getCompany(domainName, domainId, user, f -> f.getDomainProperty().eq(domainId));
			LinkedList<RegistryBank> activeBanks = AON.getRBankList(domainName
					, domainId
					, user
					, f -> f.getDomainProperty().eq(domainId)
					.and(f.getRegistryProperty().eq(company.getId()))
					.and(f.getActiveProperty().eq(AonEnumUtils.getByte(true)))
					.and(f.getBankAccountProperty().notIn(unlinkedIbans.toArray(new String[unlinkedIbans.size()]))));
			
			
			return activeBanks
			.stream()
			.map(b -> new CheckitUnlinkedBankAccount().setIban(b.getBankAccount().getIban()).setBank(b.getAlias()).setCredentials(true))
			.collect(Collectors.toList());
		}
		return Collections.emptyList();
		
	}
	

	private static Date parseDateFromJSON(String date) {
		try {
			return DF_TIME.parse(date);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Map<String, Integer> getBanksMap() {
		try {
			JSONArray banksJson = getBanks();
			LinkedHashMap<String, Integer> bankMap = new LinkedHashMap<String, Integer>();
			if (banksJson != null) {
				for(int i=0; i<banksJson.length(); i++) {
					JSONObject bank = banksJson.getJSONObject(i);
					int bankId = bank.optInt(BANK_ID);
					String bankName = bank.optString(BANK_NAME_PARAM);
					bankMap.put(bankName, bankId);
				}
			}
			return bankMap;
		} catch (CheckItException e) {
			return Collections.emptyMap();
		}
		
	}
	
	public static Date clearDate(Date date) {
		if (date == null)
			return null;
		Calendar calendar = Calendar.getInstance(new Locale("es", "ES"));
		calendar.setTime(date);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR, 0);
		return calendar.getTime();
	}
	
}
