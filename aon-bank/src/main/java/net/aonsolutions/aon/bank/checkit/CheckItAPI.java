package net.aonsolutions.aon.bank.checkit;


import static com.esferalia.aon.occam.impl.jooq.dao.CheckItDAO.CHECKIT_R1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBank;
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

import net.aonsolutions.aon.bank.CheckItException;

public class CheckItAPI implements IParamNames{
	private static final Logger LOGGER = Logger.getLogger(CheckItAPI.class.getName()); 

	private CheckItAPI() {
	}

	private static final DateFormat DF_TIME = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	
	private static final String HOST = "www.checkitbancario.com";
	private static final String BASE_URL = "https://"+HOST+"/";
	private static final String API_URL = BASE_URL + "openapi/";
	private static final String LOGO_BASE_URL = BASE_URL + "login/img/logos/bancos/";
	private static final String API_KEY = "84d9ee44e457ddef7f2c4f25dc8fa865";
	private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd");
	private static final String APPLICATION_JSON = "application/json";

	
    public static boolean isCheckItAvailabilitySocketAlive() {
        boolean isAlive = false;
        int timeout = 2000;
        try (Socket socket = new Socket()) {
        	SocketAddress socketAddress = new InetSocketAddress(HOST, 80);
            socket.connect(socketAddress, timeout);
            isAlive = true;
        } catch (SocketTimeoutException exception) {
        	LOGGER.severe("SocketTimeoutException " + HOST + ":80. " + exception.getMessage() );
        } catch (IOException exception) {
        	LOGGER.severe("IOException - Unable to connect to " + HOST + ":80. " + exception.getMessage());
        }
        return isAlive;
    }
    
    private static <T> T post(String url, JSONObject postData, Function<String,T> responseBuilder) throws CheckItException {
		try {
			HttpRequest request = HttpRequest.newBuilder()
				.uri( URI.create(url) )
				.header("accept", APPLICATION_JSON)
				.header("Content-Type", APPLICATION_JSON)
				.POST( HttpRequest.BodyPublishers.ofString(postData.toString()) )
				.build();
			HttpResponse<String> resp = HttpClient.newBuilder()
				.build()
				.send(request, BodyHandlers.ofString());
			if (resp.statusCode() != 200) {
				throw new CheckItException( CheckItException.NO_CONNECTION_MSG );
			}
			return responseBuilder.apply(resp.body());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new CheckItException(e.getMessage());
		} catch (IOException e) {
			throw new CheckItException(e.getMessage());
		}
	}
	
	public static JSONArray getBanks() throws CheckItException {
		return getBanks( new JSONObject().put(API_KEY_PARAM, API_KEY) );
	}

	public static List<CheckItBank>  getBankList() {
		try {
			return JsonUtils.stream( getBanks() )
				.map( bank -> new CheckItBank()
						.setId(bank.optInt(BANK_ID))
						.setName(bank.optString(BANK_NAME_PARAM)))
				.collect(Collectors.toCollection(LinkedList::new));
		} catch (CheckItException e) {
			return Collections.emptyList();
		}
	}

	public static JSONArray getLogins(Integer bankId) throws CheckItException {
		return getLogins( new JSONObject().put(BANK_ID_PARAM, bankId) );
	}
	
	public static JSONArray getLoginFields(Integer bankLoginTypeId) throws CheckItException {
		return getLoginFields(new JSONObject().put(LOGIN_TYPE_ID_PARAM, bankLoginTypeId));
	}
	
	public static JSONObject getCredentials(Integer enterpriseId, Integer bankLoginTypeId) throws CheckItException {
		return getCredentials(new JSONObject()
			.put(API_KEY_PARAM, API_KEY)
			.put(ENTERPRISE_ID_PARAM, enterpriseId)
			.put(LOGIN_TYPE_ID_PARAM, bankLoginTypeId)
		);
	}
	
	public static JSONObject addCredentials(Integer enterpriseId, Integer bankLoginTypeId, String userID, String userPassword, String userPIN) throws CheckItException {
		return addCredentials(new JSONObject()
			.put(API_KEY_PARAM, API_KEY)
			.put(ENTERPRISE_ID_PARAM, enterpriseId)
			.put(LOGIN_TYPE_ID_PARAM, bankLoginTypeId)
			.put(USER_ID_PARAM, userID)
			.put(USER_PASSWORD_PARAM, userPassword)
			.put(USER_PIN_PARAM, userPIN)
		);
	}

	public static JSONObject addExtraField(Integer enterpriseId, Integer bankLoginTypeId, String extraField) throws CheckItException {
		return addExtraField(new JSONObject()
			.put(API_KEY_PARAM, API_KEY)
			.put(ENTERPRISE_ID_PARAM, enterpriseId)
			.put(LOGIN_TYPE_ID_PARAM, bankLoginTypeId)
			.put(EXTRA_FIELD, extraField)
		);
	}
	
	public static JSONArray getAccounts(Integer enterpriseId, Integer accountTypeId, String iban) throws CheckItException {
		return getAccounts(new JSONObject()
			.put(API_KEY_PARAM, API_KEY)
			.put(ENTERPRISE_ID_PARAM, enterpriseId)
			.put(ACCOUNT_TYPE_ID_PARAM, accountTypeId)
			.put(IBAN_PARAM, iban));
	}
	
	public static JSONArray getEnterprise(String cif) throws CheckItException {
		return getEnterprise(new JSONObject()
			.put(API_KEY_PARAM, API_KEY)
			.put(CIF_PARAM, cif));
	}
	public static JSONArray getLogs(Integer enterpriseId, Integer accountId) throws CheckItException {
		return getLogs(new JSONObject()
			.put(API_KEY_PARAM, API_KEY)
			.put(ENTERPRISE_ID_PARAM, enterpriseId)
			.put(ACCOUNT_ID, accountId)
		);
	}

	public static JSONObject addAccount(Integer enterpriseId, Integer bankId, Integer bankLoginTypeId, String iban, Integer accountTypeId) throws CheckItException {
		return addAccount(new JSONObject()
			.put(API_KEY_PARAM, API_KEY)
			.put(ENTERPRISE_ID_PARAM, enterpriseId)
			.put(BANK_ID_PARAM, bankId)
			.put(LOGIN_TYPE_ID_PARAM, bankLoginTypeId)
			.put(IBAN_PARAM, iban)
			.put(ACCOUNT_TYPE_ID_PARAM, accountTypeId));
	}
	
	public static JSONObject addAccountApi(Integer enterpriseId, Integer bankId, String iban, Double balance, Double available, Date balanceDate, String id, Integer apiServiceId) throws CheckItException {
		return addAccountApi(new JSONObject()
			.put(API_KEY_PARAM, API_KEY)
			.put(ENTERPRISE_ID_PARAM, enterpriseId)
			.put(BANK_ID_PARAM, bankId)
			.put(IBAN_PARAM, iban)
			.put(BALANCE_PARAM, balance)
			.put(AVAILABLE_PARAM, available)
			.put(BALANCE_DATE_PARAM, balanceDate)
			.put(ID_PARAM, id)
			.put(SERVICE_API_ID_PARAM, apiServiceId));
	}
	
	public static JSONObject addEnterprise(String name, String cif, String email, String abbrName,String url, String phone, String address) throws CheckItException { 
		return addEnterprise(new JSONObject()
			.put(API_KEY_PARAM, API_KEY)
			.put(NAME_PARAM, name)
			.put(CIF_PARAM, cif)
			.put(EMAIL_PARAM, email)
			.put(SHORT_NAME_PARAM, abbrName)
			.put(URL_PARAM, url)
			.put(PHONE_PARAM, phone)
			.put(ADDRESS_PARAM, address));
	}
	public static JSONArray getTransactions(Integer enterpriseIdId, Date fromDate, Date toDate, String accountId) throws CheckItException {
		return getTransactions(new JSONObject()
			.put(API_KEY_PARAM, API_KEY)
			.put(ENTERPRISE_ID_PARAM, enterpriseIdId)
			.put(DATE_FROM_PARAM, formatDateForTransactions(fromDate))
			.put(DATE_TO_PARAM, formatDateForTransactions(toDate))
			.put(ACCOUNT_ID_PARAM, accountId));
	}
	
	// -------------------------------------------------- [PRIVATE]
	
	private static JSONArray getTransactions(JSONObject params) throws CheckItException {
		return post(API_URL + "movimientos", params, JSONArray::new);
	}
	
	private static JSONObject addEnterprise(JSONObject params) throws CheckItException {
		return post(API_URL + "empresas/add", params, JSONObject::new);
	}

	private static JSONObject addAccountApi(JSONObject params) throws CheckItException {
		return post(API_URL + "cuentas/add/api", params, JSONObject::new);
	}
	
	private static JSONObject addAccount(JSONObject params) throws CheckItException {
		return post(API_URL + "cuentas/add", params, JSONObject::new);
	}

	private static JSONArray getLogs(JSONObject params) throws CheckItException {
		return post(API_URL + "logs/robot/list", params, JSONArray::new);
	}

	private static JSONArray getEnterprise(JSONObject params) throws CheckItException {
		return post(API_URL + "empresas", params, JSONArray::new);
	}

	private static JSONArray getAccounts(JSONObject params) throws CheckItException {
		return post(API_URL + "cuentas", params, JSONArray::new);
	}

	private static JSONObject addExtraField(JSONObject params) throws CheckItException {
		return post(API_URL + "credenciales/campoextra", params, JSONObject::new);
	}

	private static JSONObject addCredentials(JSONObject params) throws CheckItException {
		return post(API_URL + "credenciales/add", params, JSONObject::new);
	}
	
	private static JSONObject getCredentials(JSONObject params) throws CheckItException {
		return post(API_URL + "credenciales", params, JSONObject::new);
	}

	private static JSONArray getLoginFields(JSONObject params) throws CheckItException {
		return post(API_URL + "bancos/logins/campos", params, JSONArray::new);
	}
	
	private static JSONArray getBanks(JSONObject params) throws CheckItException {
		return post(API_URL + "bancos", params, JSONArray::new);
	}

	private static JSONArray getLogins(JSONObject params) throws CheckItException {
		return post(API_URL + "bancos/logins", params, JSONArray::new);
	}

	// ***************************************************************************
	// ***************************************************************************
	// ***************************************************************************
	// ***************************************************************************
	// ***************************************************************************
	// ***************************************************************************
	// ***************************************************************************
	// ***************************************************************************
	// ***************************************************************************
	// ***************************************************************************
	// ***************************************************************************
	// ***************************************************************************
	// ***************************************************************************
	

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
				.setBankAccountId(json.optInt(ACCOUNT_ID))
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
	public static int insertTransactions(CheckItParams params) throws CheckItException {
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
			List<CheckItBankStatement> bankStatements = getAllBankStatements(empresaId, getAccountIdByIBAN(empresaId, iban));
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
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
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
	
	private static Date clearDate(Date date) {
		if (date == null) return null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

}
