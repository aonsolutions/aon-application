package com.esferalia.aon.gwt.finance.server;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.finance.nordigen.NordigenService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItBankAccount;
import com.esferalia.aon.occam.api.model.finance.checkit.CheckItLoginFields;
import com.esferalia.aon.occam.api.model.finance.nordigen.NORDIGEN_REQUISITION_STATUS;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountTransaction;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAgreement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenConfiguration;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.impl.jooq.dao.CheckItDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.bank.checkit.CheckItAPI;
import net.aonsolutions.aon.bank.checkit.CheckItException;
import net.aonsolutions.aon.bank.checkit.IParamNames;
import nordigen.AonNordigen;

@WebServlet(name = "Nordigen Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/nordigen" })
public class NordigenServiceImpl extends AonStatelessRemoteServiceServlet implements NordigenService {

	private static final long serialVersionUID = 1249978088517559976L;

	@Override
	public NordigenConfiguration getConfiguration(String domainName, int domain, String user) throws Exception {
		NordigenAccessToken token = AonNordigen.getNewAccessToken();
		Domain dmn = new Domain().setId(domain).setName(domainName);
//		clearIncompleteRequisitions(token, domainName, domain, user);
		List<NordigenBankAccount> accounts = AonNordigen.getAllAccounts(token, dmn, user);
		return new NordigenConfiguration()
			.setConfiguration(AON.getConfiguration(domainName, domain,user))
			.setToken(token)
			.setAccounts(accounts)
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
	public NordigenRequisition addAccount(String currentDomainName, int currentDomain, String user, NordigenAccessToken token, NordigenBankAccount nordigenBankAccount) throws Exception {
		RegistryBank rbank = nordigenBankAccount.getRbank();
		Pattern bicPattern = Pattern.compile("^(?<bic>.*?)X*$", Pattern.CASE_INSENSITIVE);
		Matcher bicMatcher = bicPattern.matcher(AonStringUtils.trimToEmpty(rbank.getBic()));
		StringBuilder bicBuilder = new StringBuilder();
		if (bicMatcher.matches()) {
			bicBuilder.append(AonStringUtils.trimToEmpty(bicMatcher.group("bic")));
		}
		final String bic = bicBuilder.toString();
		
		Optional<NordigenInstitution> optInstitution = AonNordigen.getInstitutions(token, null, null).stream().filter(inst -> AonStringUtils.equalsIgnoreCase(inst.getBic(), bic)).findFirst();
		StringBuilder instIdBuilder = new StringBuilder();
		if (optInstitution.isPresent()) {
			instIdBuilder.append(AonStringUtils.trimToEmpty(optInstitution.get().getId()));
		}
		final String institutionId = instIdBuilder.toString();
		
		//EN CASO DE QUE HAYA PROBLEMAS CON EL BIC:
		NordigenInstitution inst = nordigenBankAccount.getInstitution();
		
		NordigenAgreement agreement = AonNordigen.createAgreement(token, inst != null ? inst.getId() : institutionId);
		NordigenRequisition requisition = AonNordigen.createRequisition(token, agreement, "https://" + currentDomainName);
		
		AonNordigen.insertNewRequisitionId(new Domain().setName(currentDomainName).setId(currentDomain), institutionId, requisition, rbank.getId());
		
		return requisition;
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
	public List<NordigenBankStatement> getMovements(NordigenAccessToken token, String domainName, int domain, String user, NordigenBankAccount nordigenBankAccount, Date endDate) throws Exception {
		try {
			NordigenAccountBalance balance = nordigenBankAccount.getBalance();
			List<NordigenBankStatement> list = new LinkedList<>();
			if (nordigenBankAccount != null && nordigenBankAccount.getMetadata() != null) {
				String id = nordigenBankAccount.getMetadata().getId();
				List<NordigenAccountTransaction> pendingTransactions = AonNordigen.getPendingAccountTransactions(token, id, endDate);
				List<NordigenAccountTransaction> transactions = AonNordigen.getBookedAccountTransactions(token, id, endDate);
				
				if (transactions != null) {
					transactions.stream().map(AonNordigen::nordigenToBankStatement).forEach(bs -> {
						bs.setPending(false);
						list.add(bs);
					});
				}
				if (pendingTransactions != null) {
					pendingTransactions.stream().map(AonNordigen::nordigenToBankStatement).forEach(bs -> {
						bs.setPending(true);
						list.add(bs);
					});
				}
			}
			List<NordigenBankStatement> orderedList = list.stream().filter(Objects::nonNull).sorted((bs1, bs2) -> {
				
				return AonNumberUtils.compare(bs2.getNordigenMovementId(), bs1.getNordigenMovementId());
			}).collect(Collectors.toList());
			
			Double remaining = balance.getBalanceAmount() != null ? balance.getBalanceAmount().getAmount() : 0;
			
			for(NordigenBankStatement bs : orderedList) {
				if (!bs.isPending()) {
					bs.setCurrentBalance(remaining);
					remaining += (bs.getAmount() * (bs.isPayment() ? 1 : -1));
				}				
			}
			
			return orderedList;
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


	@Override
	public List<NordigenInstitution> getNordigenInstitutions(NordigenAccessToken token, Country country) throws Exception {
		return AonNordigen.getInstitutions(token, country, null);
	}


	@Override
	public List<NordigenInstitution> getNordigenInstitutionsByBic(NordigenAccessToken token, String bic)
			throws Exception {
		if (AonStringUtils.isNotBlank(bic)) {
			List<NordigenInstitution> institutions = AonNordigen.getInstitutions(token, null, null);
			List<NordigenInstitution> matchedInstitutions = institutions.stream().filter(inst -> AonStringUtils.containsIgnoreCase(inst.getBic(), bic)).collect(Collectors.toList());
			
			if (matchedInstitutions != null && !matchedInstitutions.isEmpty()) {
				return matchedInstitutions;
			}
			
		}
		throw new Exception("No available institutions");
	}


	@Override
	public Integer clearIncompleteRequisitions(NordigenAccessToken token, String currentDomainName, int currentDomain,
			String user) throws Exception {
		Domain domain = new Domain().setName(currentDomainName).setId(currentDomain);
		Map<Integer, NordigenRequisition> storedReqs = AonNordigen.getStoredRequisitions(token, domain, user);
		List<NORDIGEN_REQUISITION_STATUS> excludedList = Arrays.asList(NORDIGEN_REQUISITION_STATUS.LN, NORDIGEN_REQUISITION_STATUS.EX);
		if (storedReqs != null) {
			int counter = 0;
			for (Entry<Integer, NordigenRequisition> entry : storedReqs.entrySet()) {
				if (entry.getValue() == null || !excludedList.contains(entry.getValue().getStatus())) {
					if (entry.getValue() != null) {
						AonNordigen.deleteRequisitionByRbank(token, domain, user, entry.getKey());
						counter++;
					} else {
						AON.deleteRegistryAddInfo(domain, user, entry.getKey());
						counter++;
					}
				}
			}
			return counter;
		}
		
		return 0;
	}


	@Override
	public Boolean cancelRequisition(NordigenAccessToken token, String currentDomainName, int currentDomain,
			String user, Integer rbankId) throws Exception {
		AonNordigen.deleteRequisitionByRbank(token, new Domain().setName(currentDomainName).setId(currentDomain), user, rbankId);
		return true;
	}


	@Override
	public NordigenRequisition getRequisition(NordigenAccessToken token, String requisitionId) throws Exception {
		return AonNordigen.getRequisition(token, requisitionId);
	}


	

}
