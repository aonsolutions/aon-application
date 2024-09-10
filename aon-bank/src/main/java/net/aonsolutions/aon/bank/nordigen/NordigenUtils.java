package net.aonsolutions.aon.bank.nordigen;

import static net.aonsolutions.aon.bank.nordigen.NordigenConstants.RADD_INFO_REQUISITION_ATTRIBUTE_PATTERN;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.NoSuchFileException;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
//import java.util.logging.ConsoleHandler;
//import java.util.logging.FileHandler;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBalanceType;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisitionStatus;
import com.esferalia.aon.occam.api.json.nordigen.NordigenAccountBalanceJSON;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.BankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;



public class NordigenUtils {

//	private static final Logger LOGGER = Logger.getLogger(NordigenUtils.class.getName());

	public static Integer getRbankIdFromRaddinfo(RegistryAddInfo raddinfo) {
		if (raddinfo == null) {
			return null;
		}
		final Pattern aatrRegex = Pattern.compile(RADD_INFO_REQUISITION_ATTRIBUTE_PATTERN);
		Matcher matcher = aatrRegex.matcher(AonStringUtils.trimToEmpty(raddinfo.getAttribute()));
		if (matcher.matches()) {
			String id = matcher.group("rbank");
			if (AonStringUtils.isNotBlank(id)) {
				return AonNumberUtils.toInteger(id);
			}
		}
		return null;
	}

	public static boolean isRequisitionLinked(NordigenRequisition requisition) {
		if (requisition != null) {
			return NordigenRequisitionStatus.LINKED.equals(requisition.getStatus());
		}
		return false;
	}

	public static NordigenAccountBalance getLastAccountBalance(List<NordigenAccountBalance> balances) {
		if (balances != null) {
			if (balances.size() > 1) {
				Optional<NordigenAccountBalance> balance = balances.stream()
						.filter(b -> b.getReferenceDate() != null
								&& NordigenBalanceType.CLOSING_BOOKED.equals(b.getBalanceType()))
						.sorted((b1, b2) -> b1.getReferenceDate().compareTo(b2.getReferenceDate())).findFirst();
				if (balance.isPresent()) {
					return balance.get();
				}
			}
			return balances.get(0);
		}
		return null;
	}
	
	
	// NUEVOS METODOS PROCESAR INFO DE BANCOS

	public static JSONObject movementsToJson(List<NordigenBankStatement> movements,
			NordigenBankAccount nordigenTrueAccount) {
		JSONArray movementsJsonArray = new JSONArray();
		for (NordigenBankStatement movement : movements) {
			JSONObject movementJson = new JSONObject();
			movementJson.put("operationDate", movement.getOperationDate());
			movementJson.put("description", movement.getDescription());
			movementJson.put("status", movement.getStatus());
			movementJson.put("amount", movement.getAmount());
			movementJson.put("totalAmount", movement.getCurrentBalance());
			movementsJsonArray.put(movementJson);
		}
		JSONObject movementsJson = new JSONObject();
		movementsJson.put("movements", movementsJsonArray);
		movementsJson.put("iban",  nordigenTrueAccount.getIban());
		return movementsJson;
	}
	
	public static JSONObject bankStatementJSON(BankStatement statement) {
		JSONObject statementJSON = new JSONObject();
		statementJSON.put("id", statement.getId());
		statementJSON.put("operation_date", statement.getOperationDate());
		statementJSON.put("amount", statement.getAmount());
		statementJSON.put("description", statement.getDescription());
		statementJSON.put("status", statement.getStatus());
		return statementJSON;
	}

	public static JSONObject nordigenBankToJson(NordigenBankAccount account) {

		JSONObject bankJson = new JSONObject();
		bankJson.put("iban", account.getIban());
		bankJson.put("alias", account.getBankAlias());
		bankJson.put("lastBalanceDate", account.getRbank().getBalanceDate());
		bankJson.put("amount", account.getRbank().getBalance());
		bankJson.put("lastMovementDate", account.getLastMovementDate());
		if (account.getInstitution() != null) {
			bankJson.put("logo", account.getInstitution().getLogo());
		}
		if (account.isLinked()) {
			bankJson.put("syncStatus", "linked");
		}
		return bankJson;
	}

	public static JSONArray convertAccountsMovementsToJsonArray(List<NordigenBankAccount> linkedAccountList,
			NordigenAccessToken token, Occam occam) throws Exception {
		JSONArray movementsJsonArray = new JSONArray();

		for (NordigenBankAccount account : linkedAccountList) {
			NordigenBankAccount nordigenTrueAccount = AonNordigen.setBankAccountValues(occam, token, account);
			Date lastAccessedDate = nordigenTrueAccount.getLastMovementDate();
			boolean linked = nordigenTrueAccount.isLinked();

			List<NordigenBankStatement> movements = AonNordigen.getMovements(token, occam, nordigenTrueAccount,
					lastAccessedDate, linked);
			JSONObject movementsJson = movementsToJson(movements, nordigenTrueAccount);
			movementsJsonArray.put(movementsJson);
		}

		return movementsJsonArray;
	}

	//MOVIMIENTOS
	public static JSONArray convertAcccountMovementsToJsonArray(List<NordigenBankAccount> linkedAccountList, Integer id,
			NordigenAccessToken token, Occam occam) {
		JSONArray movementsJsonArray = new JSONArray();

		linkedAccountList.stream().filter(account -> account.getRbank().getId().equals(id)).findFirst().ifPresent(account -> {
			try {
				NordigenBankAccount nordigenTrueAccount = AonNordigen.setBankAccountValues(occam, token, account);
				Date lastAccessedDate = nordigenTrueAccount.getLastMovementDate();
				boolean linked = nordigenTrueAccount.isLinked();
				List<NordigenBankStatement> movements = AonNordigen.getMovements(token, occam, nordigenTrueAccount,
						lastAccessedDate, linked);
				JSONObject movementsJson = movementsToJson(movements, nordigenTrueAccount);
				movementsJsonArray.put(movementsJson);
			} catch (Exception e) {
				throw new RuntimeException("Error al procesar la cuenta", e);
			}
		});

		return movementsJsonArray;
	}

	public static JSONArray processAccountBalances(List<NordigenBankAccount> linkedAccountList,
			NordigenAccessToken token, Occam occam) throws Exception {
		JSONArray balancesArray = new JSONArray();

		for (NordigenBankAccount account : linkedAccountList) {
			NordigenBankAccount nordigenTrueAccount = AonNordigen.setBankAccountValues(occam, token, account);
			List<NordigenAccountBalance> balances = nordigenTrueAccount.getBalances();
			JSONObject balanceObject = new JSONObject();
			balanceObject.put("iban", account.getIban());
			balanceObject.put("balances", NordigenAccountBalanceJSON.to(balances));
			balancesArray.put(balanceObject);
		}

		return balancesArray;
	}

	public static JSONArray processSingleAccountBalance(List<NordigenBankAccount> linkedAccountList, Integer id,
			NordigenAccessToken token, Occam occam) throws Exception {
		JSONArray result = new JSONArray();

		linkedAccountList.stream().filter(account -> account.getRbank().getId().equals(id)).findFirst().ifPresent(account -> {
			try {
				NordigenBankAccount nordigenTrueAccount = AonNordigen.setBankAccountValues(occam, token, account);
				List<NordigenAccountBalance> balances = nordigenTrueAccount.getBalances();

				JSONObject balanceObject = new JSONObject();
				balanceObject.put("iban", account.getIban());
				balanceObject.put("balances", NordigenAccountBalanceJSON.to(balances));

				result.put(balanceObject);
			} catch (Exception e) {
				throw new RuntimeException("Error al procesar la cuenta", e);
			}
		});

		return result;
	}
	
	

	public static void processAccountLinking(List<NordigenBankAccount> rAccounts, Integer id,
			NordigenAccessToken token, JSONObject jsonLink, Occam occam) throws Exception {
		for (NordigenBankAccount account : rAccounts) {
			if (account.getRbank().getId().equals(id)) {
				if (account.isLinked()) {
					jsonLink.put("link", "Already linked");
				} else {
					NordigenRequisition requisition = AonNordigen.addAccount(token, occam, account);
					jsonLink.put("link", requisition.getLink());
				}
				return;
			}
		}
		jsonLink.put("link", "IBAN no encontrado");
	}
	
//    private static final Logger LOGGER = configureLogger();
//	
//	private static Logger configureLogger() {
//        Logger logger = Logger.getLogger(NordigenUtils.class.getName());
//        try {
//            String logFilePath = "C:/Program Files/Apache Software Foundation/tomcat/webapps/aon-solutions/WEB-INF/logs/bankErrorsLog.log";
//
//
//            PatternLayout layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1} - %m%n");
//            FileAppender fileAppender = new FileAppender(layout, logFilePath, true);
//            logger.addAppender(fileAppender);
//            logger.setLevel(org.apache.log4j.Level.ALL);
//        } catch (IOException e) {
//            logger.error("Failed to initialize file appender for logger", e);
//        }
//        return logger;
//    }
//	
//	public static void closeNordigenLogger() {
//	    Enumeration<?> appenders = LOGGER.getAllAppenders();
//	    while (appenders.hasMoreElements()) {
//	        Appender appender = (Appender) appenders.nextElement();
//	        if (appender instanceof FileAppender) {
//	            FileAppender fileAppender = (FileAppender) appender;
//	            LOGGER.removeAppender(fileAppender);
//	            fileAppender.close();
//	        }
//	    }
//	}
//	
//	public static void exceptionAddInfo(String info) {
//		//Extraer y registrar informacion adicional util para la excepcion
//		LOGGER.error("Additional information about error : " + info);
//
//	}
//
//	
//	public static void logException(Exception e) {
//        // Extraer y registrar el nombre de la excepción
//        String exceptionName = e.getClass().getName();
//        LOGGER.error("Exception Type: " + exceptionName);
//
//        // Registrar el mensaje de la excepción
//        String errorMessage = e.getMessage();
//        LOGGER.error("Error Message: " + (errorMessage != null ? errorMessage : "No message available"));        
//        // Registrar la causa de la excepción (si existe)
//        Throwable cause = e.getCause();
//        if (cause != null) {
//            LOGGER.error("Cause: " + cause.toString());
//        } else {
//            LOGGER.error("Cause: No cause available");
//        }
//
//        // Información adicional, como la clase y línea donde ocurrió la excepción
//        StackTraceElement[] stackTrace = e.getStackTrace();
//        if (stackTrace.length > 0) {
//            StackTraceElement element = stackTrace[0];
//            LOGGER.error("Occurred in Class: " + element.getClassName());
//            LOGGER.error("Occurred in Method: " + element.getMethodName());
//            LOGGER.error("Occurred at Line: " + element.getLineNumber());
//        }
//    }

}
