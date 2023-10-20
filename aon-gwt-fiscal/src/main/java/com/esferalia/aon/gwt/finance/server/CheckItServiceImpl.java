package com.esferalia.aon.gwt.finance.server;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.finance.checkit.CheckItService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBank;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankAccount;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankStatement;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItConfiguration;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItLoginFields;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckitUnlinkedBankAccount;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.impl.jooq.dao.CheckItDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import net.aonsolutions.aon.bank.checkit.CheckItAPI;
import net.aonsolutions.aon.bank.checkit.CheckItException;
import net.aonsolutions.aon.bank.checkit.CheckItNoConnectionException;
import net.aonsolutions.aon.bank.checkit.IParamNames;

@WebServlet(name = "CheckIt Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/CheckIt" })
public class CheckItServiceImpl extends AonStatelessRemoteServiceServlet implements CheckItService {

	private static final long serialVersionUID = 1249978088517559976L;

	@Override
	public CheckItConfiguration getConfiguration(String domainName, int domain, String user) throws AonCoreException {
		List<CheckItBank> bankIds = new LinkedList<>();
		ApplicationParameter appParam = AON.getApplicationParameter(domainName, domain, user, AppParam.CHECK_IT_ENTERPRISE_ID);
		Integer enterpriseId = null;
		if (appParam != null) {
			String value = appParam.getValue();
			try {
				enterpriseId = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				enterpriseId = null;
			}
		}
		LinkedList<CheckItBankAccount> checkitAccounts = null;
		List<CheckitUnlinkedBankAccount> checkitUnlinkedAccounts = null;
		try {
			checkitAccounts =  CheckItAPI.getLinkedAccountsToDisplay(domainName, domain, user, enterpriseId);
		} catch (CheckItException e) {
			if (AonStringUtils.equalsIgnoreCase(e.getMessage(), CheckItException.NO_CONNECTION_MSG)) {
				throw new AonCoreException(e.getMessage());
			}
		}
		try {
			checkitUnlinkedAccounts = CheckItAPI.getUnlinkedActive(domainName, domain, user, enterpriseId);
		} catch (CheckItException e) {
		}

		CheckItAPI.getBanksMap().forEach((k, v) -> bankIds.add(new CheckItBank(v, k)));
	
		Integer empresaId = enterpriseId;
		if (checkitAccounts != null) {
			checkitAccounts.forEach(acc -> {
				try {
					List<CheckItBankStatement> movs = CheckItAPI.getNewMovements(domainName, domain, user, empresaId, acc.getCcc());
					acc.setPending(movs);
				} catch (CheckItException e) {
					throw new AonCoreException(e.getMessage());
				}
				
			});
		}
		
		
		
		 AonCollectionUtils.stream( checkitAccounts )
		 	.forEach( a -> CheckItDAO.updateRegistryBank(domainName, domain, user, a ));
		
		return new CheckItConfiguration()
			.setConfiguration( AON.getConfiguration(domainName, domain,user) )
			.setEnterpriseId( enterpriseId )
			.setCheItBanks(checkitAccounts)
			.setCheckItUnlinkedBanks(checkitUnlinkedAccounts)
			.setBankIds(bankIds)
		;
		
	}
	
	
	@Override
	public Integer saveEnterpriseData(String currentDomainName, int currentDomain, String user)
			throws AonCoreException {
		String baseErr = "Se produjo un error y no se pudo registrar la empresa";
		// LLama al cheitApi
		Enterprise enterprise = CheckItDAO.getEnterprise(currentDomain, currentDomainName, user);
		String name = enterprise.getName();
		String cif = enterprise.getDocument();
		Integer parentDomain = CheckItDAO.getParentDomain(currentDomainName, currentDomain, user);
		String shortName = parentDomain != null ? parentDomain + "@" + currentDomain : "aon@" + currentDomain;
		String email = currentDomainName != null ? currentDomainName.replaceFirst("\\.", "@") : null;
		String url = currentDomainName;
		String address = enterprise.getAddress();
		String phone = enterprise.getPhone();
		if (AonStringUtils.isEmpty(name)) {
			throw new AonCoreException(baseErr + ": Falta el nombre de la empresa en su configuraci\u00F3n");
		}
		if (AonStringUtils.isEmpty(cif)) {			
			throw new AonCoreException(baseErr + ": Falta el CIF de la empresa en su configuraci\u00F3n");
		}
		if (AonStringUtils.isEmpty(email)) {
			throw new AonCoreException(baseErr + ": Falta el correo electr\u00F3nico de la empresa en su configuraci\u00F3n");
		}
		
		try {
			Integer id = null;
			JSONArray enterpriseArr = CheckItAPI.getEnterprise(cif);
			if (enterpriseArr.isEmpty()) {
				JSONObject json = CheckItAPI.addEnterprise(name, cif, email, shortName, url, phone, address);
				id =  json.optInt("id_empresa");				
			} else {
				JSONObject enterpriseJson = enterpriseArr.optJSONObject(0);
				id =  enterpriseJson.optInt("id_empresa");				
			}
			
			
			if (CheckItDAO.saveCheckItEnterpriseId(currentDomainName, currentDomain, user, id)) {
				return id;
			} else {
				throw new AonCoreException("Se produjo un error y no se pudo registrar la empresa");
			}
		} catch (CheckItException e) {
			throw new AonCoreException(e.getMessage());
		}
	}


	@Override
	public Integer insertTransactions(String currentDomainName, int currentDomain, String user,
			Integer checkitEnterpriseId, CheckItBankAccount checkItBankAccount) throws AonCoreException {
		String iban = checkItBankAccount.getCcc();
		try {
			return CheckItAPI.insertTransactions(currentDomainName, currentDomain, user, checkitEnterpriseId, iban);
		} catch (CheckItException e) {
			throw new AonCoreException(e.getMessage());
		}
	}


	@Override
	public List<CheckItLoginFields> getLogins(Integer bankId) {
		if (bankId == null)
			return Collections.emptyList();
		
		List<CheckItLoginFields> fieldList = new LinkedList<>();
		
		try {
			JSONArray logins = CheckItAPI.getLogins(bankId);
			
			for (int i=0; i<logins.length(); i++) {
				
				JSONObject login = logins.optJSONObject(i);
				if (null == login)
					continue;
				
				String loginName = login.optString("tipo_login_tipo");
				Integer loginId = login.optInt("id_tipo_login_banco");
				
				JSONArray loginFields = CheckItAPI.getLoginFields(loginId);
				
				if (null != loginFields && !loginFields.isEmpty()) {
					JSONObject fields = loginFields.optJSONObject(0);
					
					CheckItLoginFields checkItLoginFields = new CheckItLoginFields()
							.setId(loginId)
							.setType(loginName)
							.setUserID(fields.optString(IParamNames.USER_ID_PARAM))
							.setUserPassword(fields.optString(IParamNames.USER_PASSWORD_PARAM))
							.setUserPIN(fields.optString(IParamNames.USER_PIN_PARAM));
					fieldList.add(checkItLoginFields);
				}
				
			}
			
			return fieldList;
		} catch (CheckItException e) {
			return Collections.emptyList();
		}
	}
	
	@Override
	public String addAccount(Integer enterpriseId, CheckitUnlinkedBankAccount checkitUnlinkedBankAccount,
			String userID, String userPassword, String userPIN) {
		if (checkitUnlinkedBankAccount != null) {
			String error = null;
			
			boolean credentials = checkitUnlinkedBankAccount.isCredentials();
			CheckItLoginFields login = checkitUnlinkedBankAccount.getLogin();
			Integer bankId = checkitUnlinkedBankAccount.getBankId();
			String iban = checkitUnlinkedBankAccount.getIban();
			
			if (enterpriseId == null) {
				error = "No hay ninguna empresa seleccionada";
			} else if (credentials && (login == null || login.isEmpty())) {
				error = "No hay ningún tipo de login seleccionado";				
			} else if (bankId == null) {
				error = "No hay ningún banco seleccionado";
			} else if (iban == null || iban.isEmpty()) {
				error = "No hay ningún IBAN";
			} else if (credentials){
				if (login.getUserID() != null && !login.getUserID().isEmpty() && (userID == null || userID.isEmpty())) {
					error = "Debe rellenar el campo '" + login.getUserID() + "'";
				} else if (login.getUserPassword() != null && !login.getUserPassword().isEmpty() && (userPassword == null || userPassword.isEmpty())) {
					error = "Debe rellenar el campo '" + login.getUserPassword() + "'";
				} else if (login.getUserPIN() != null && !login.getUserPIN().isEmpty() && (userPIN == null || userPIN.isEmpty())) {
					error = "Debe rellenar el campo '" + login.getUserPIN() + "'";					
				}
			}
			
			if (error != null) {
				throw new IllegalArgumentException(error);
			} else {	
				try {
					if (credentials)
						CheckItAPI.addCredentials(enterpriseId, login.getId(), userID, userPassword, userPIN);
					JSONObject johnson = CheckItAPI.addAccount(enterpriseId, bankId, login.getId(), iban, 1);
					
					String msg = johnson.optString("message");
					msg += johnson.optString("result");
					
					if (AonStringUtils.containsIgnoreCase(msg, "campoextra")) {
						Pattern pattern = Pattern.compile("(\\[\\{)(?:(?!\\}\\,?\\]).)+(\\}\\,?\\])$", Pattern.CASE_INSENSITIVE);
						Matcher matcher = pattern.matcher(msg);
						if (matcher.find()) {
							String jsonArrStr = matcher.group();
							return "{\"extrafield\": " + jsonArrStr + "}";
						}
					}
					
					String code = johnson.optString("code");
					return msg + ((code != null && !code.isEmpty()) ? ", código: " + code : "");
				} catch (Exception e) {
					throwException(e);
					return null;
				}
			}
		} else {
			throw new IllegalArgumentException("No hay ninguna cuenta seleccionada");
		}
	}


	@Override
	public CheckItLoginFields getCredentials(Integer enterpriseId, Integer loginId) throws IllegalArgumentException {
		try {
			JSONObject credentialsJson = CheckItAPI.getCredentials(enterpriseId, loginId);
			JSONObject entJson = credentialsJson.optJSONObject("CredencialesEmpresa");
			JSONObject bankLoginJson = credentialsJson.optJSONObject("TipoLoginBanco");
			
			String userId = entJson != null ? entJson.optString(IParamNames.USER_ID_PARAM) : null;
			String loginName = bankLoginJson != null ? bankLoginJson.optString("tipo_login_tipo") : null;
			
			
			JSONArray loginJson = CheckItAPI.getLoginFields(loginId);
			
			
			if (loginJson.length() != 1)
				return null;
			
			JSONObject fields = loginJson.optJSONObject(0);
			
			
			
			return new CheckItLoginFields()
					.setId(loginId)
					.setType(loginName)
					.setUserID(fields.optString("userID"))
					.setUserPassword(fields.optString("userPassword"))
					.setUserPIN(fields.optString("userPIN"))
					.setUserIDInput(userId);
			
		} catch (CheckItException e) {
			throwException(e);
			return null;
		}
	}


	@Override
	public Boolean editCredentials(Integer enterpriseId, CheckItLoginFields checkItLoginFields) throws IllegalArgumentException {
		validateCheckItLogin(checkItLoginFields);
		try {
			JSONObject response = CheckItAPI.addCredentials(enterpriseId
					, checkItLoginFields.getId()
					, checkItLoginFields.getUserIDInput()
					, checkItLoginFields.getUserPasswordInput()
					, checkItLoginFields.getUserPINInput());
			if (response != null && AonStringUtils.equalsIgnoreCase(response.optString("result"), "Success"))
				return true;
			else {
				throw new IllegalArgumentException("Se produjo un error desconocido");
			}
		} catch (Exception e) {
			throwException(e);
		}
		return false;
	}
	
	private static void validateCheckItLogin(CheckItLoginFields checkItLoginFields) {
		String userID = checkItLoginFields.getUserIDInput();
		String userPassword = checkItLoginFields.getUserPasswordInput();
		String userPIN = checkItLoginFields.getUserPINInput();
		String error = null;
		if (checkItLoginFields.getUserID() != null && !checkItLoginFields.getUserID().isEmpty() && (userID == null || userID.isEmpty())) {
			error = "Debe rellenar el campo '" + checkItLoginFields.getUserID() + "'";
		} else if (checkItLoginFields.getUserPassword() != null && !checkItLoginFields.getUserPassword().isEmpty() && (userPassword == null || userPassword.isEmpty())) {
			error = "Debe rellenar el campo '" + checkItLoginFields.getUserPassword() + "'";
		} else if (checkItLoginFields.getUserPIN() != null && !checkItLoginFields.getUserPIN().isEmpty() && (userPIN == null || userPIN.isEmpty())) {
			error = "Debe rellenar el campo '" + checkItLoginFields.getUserPIN() + "'";					
		}
		if (error != null)
			throw new IllegalArgumentException(error);
	}
	
	private static <T extends Exception> void throwException (T exception) throws IllegalArgumentException{
		try {
			JSONObject errJson = new JSONObject(exception.getMessage());
			String err = errJson.getString("result");
			throw new IllegalArgumentException(err);						
		} catch (Exception subE) {
			throw new IllegalArgumentException(exception.getMessage());
		}
	}


	@Override
	public CheckItLoginFields getFields(Integer loginId) throws IllegalArgumentException {
		try {
			JSONArray loginFields = CheckItAPI.getLoginFields(loginId);
			JSONObject json = loginFields.optJSONObject(0);
			String userID = json.optString("userID");
			String userPassword = json.optString("userPassword");
			String userPIN = json.optString("userPIN");
			
			return new CheckItLoginFields()
					.setId(loginId)
					.setUserID(userID)
					.setUserPassword(userPassword)
					.setUserPIN(userPIN);
		} catch (Exception e) {
			throwException(e);
			return null;
		}
	}


	@Override
	public List<CheckItBankStatement> getMovements(String domainName, int domain, String user, Integer empresaId,
			CheckItBankAccount checkItBankAccount, Date startDate, Date endDate) throws IllegalArgumentException {
		try {
			return CheckItAPI.getBankStatementsFromTo(empresaId, checkItBankAccount.getBankAccountId(), startDate, endDate);
		} catch (Exception e) {
			throwException(e);
			return null;
		}
	}


	@Override
	public Boolean addExtraField(Integer enterpriseId, String iban, String extraField) throws AonCoreException {
		try {
			JSONArray accs = CheckItAPI.getAccounts(enterpriseId, 1, iban);
			if (accs.isEmpty())
				return false;
			JSONObject acc = accs.getJSONObject(0);
			int loginType = acc.getInt("tipo_login_banco_id");
			CheckItAPI.addExtraField(enterpriseId, loginType, extraField);
			return true;
		} catch (Exception e) {
			throwException(e);
			return false;			
		}
	}

}
