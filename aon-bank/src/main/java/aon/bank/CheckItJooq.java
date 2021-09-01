package aon.bank;

import static com.esferalia.aon.jooq.tables.BankStatement.BANK_STATEMENT;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jooq.InsertValuesStep11;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.jooq.tables.records.BankStatementRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.BankStatement;
import com.esferalia.aon.occam.api.model.finance.CheckItParams;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.StatementConcept;
import com.esferalia.aon.occam.api.model.type.StatementStatus;

import aon.bank.exceptions.BankException;
import aon.bank.exceptions.BankJooqException;

public class CheckItJooq {

	public static final String CHECKIT_R1 = "CHECKIT";
	public static final String API_KEY = "84d9ee44e457ddef7f2c4f25dc8fa865";
	
	private CheckItJooq() {
	    throw new IllegalStateException("Utility class");
	  }
	
	private static List<BankStatement> getBankStatements(AONContext aonContext, Integer domainId, Integer empresaId, String iban) throws BankException {
		Date today = new Date();

		String accountId = Utilities.getAccountIdByIBAN(API_KEY, empresaId, iban);

		JSONObject requestParams = new JSONObject();
		
		requestParams.put("claveApi", API_KEY);
		requestParams.put("empresa_id", empresaId);
		requestParams.put("fecha_hasta", Utilities.formatDateForTransactions(today)); // HOY
		requestParams.put("cuenta_bancaria_id", accountId);

		RegistryBank rbank = Utilities.getRbankByIban(aonContext, iban);

		if (rbank == null || rbank.getId() == null) {
			throw new BankJooqException("Account does not exist in DB");
		}

		int lotNumber = Utilities.getNextLotNumber(aonContext, domainId, rbank);

		Object[] max = Utilities.getMaxMovementIdAndDate(aonContext, rbank);
		String maxId = null;
		Date lastOperationDate = null;

		if (max == null || max.length == 0) {
			lastOperationDate = Utilities.getLastOperationDateDB(aonContext, domainId, rbank);
		} else {
			maxId = (String) max[0];
			lastOperationDate = new Date(((java.util.Date) max[1]).getTime());
			// AÑADIR 1 DÍA MÁS PARA QUE NO HAYA DUPLICADOS
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(lastOperationDate);
			calendar.add(Calendar.DATE, 1);
			lastOperationDate = calendar.getTime();
		}

		if (maxId == null && lastOperationDate == null) {
			lastOperationDate = Utilities.cleanDate(1, Calendar.JANUARY, Calendar.getInstance().get(Calendar.YEAR));
		}

		requestParams.put("fecha_desde", Utilities.formatDateForTransactions(lastOperationDate)); // REQUEST PARAMS COMPLETED

		JSONArray transactionsArray = CheckItAPI.getTransactions(requestParams);

		Integer maximumId = maxId != null ? Integer.parseInt(maxId) : 0;

		List<BankStatement> bankStatements = new LinkedList<>();
		
		for (int i = 0; i < transactionsArray.length(); i++) {

			JSONObject transactionJson = transactionsArray.optJSONObject(i);

			int movementId = transactionJson.optInt("id_movimiento");

			if (movementId > maximumId) {
				java.sql.Date operationDate = Utilities
						.toSqlDate(Utilities.parseTZDate(transactionJson.optString("fecha_operacion")));

				String description = transactionJson.optString("descripcion");

				if (description != null)
					description = description.length() > 80 ? description.substring(0, 80) : description;

				Double amount = transactionJson.optDouble("importe");
				amount = amount.isNaN() ? 0.00 : amount;

				boolean bpayment = amount < 0;
				
				
				BankStatement bankStatement = new BankStatement();
				bankStatement.setDomain(aonContext.getDomainId());
				bankStatement.setRegistryBank(rbank);
				bankStatement.setLotNumber(lotNumber);
				bankStatement.setOperationDate(operationDate);
				bankStatement.setCommonConcept(StatementConcept.UNKNOWN);
				bankStatement.setPayment(bpayment);
				bankStatement.setAmount(Math.abs(amount));
				bankStatement.setDescription(description);
				bankStatement.setStatus(StatementStatus.PENDING);
				bankStatement.setReference1(CHECKIT_R1);
				bankStatement.setReference2(Utilities.leadingZeros(movementId, 16));
				
				bankStatements.add(bankStatement);
			}

		}
		return bankStatements;

	}
	
	private static int insertStatements(AONContext aonContext, List<BankStatement> bankStatements) {
		
		InsertValuesStep11<BankStatementRecord, Integer, Integer, Integer, java.sql.Date, Byte, Byte, Double, String, Byte, String, String> query =
				aonContext.getDslContext()
				.insertInto(BANK_STATEMENT
						, BANK_STATEMENT.DOMAIN
						, BANK_STATEMENT.RBANK
						, BANK_STATEMENT.LOT_NUMBER
						, BANK_STATEMENT.OPERATION_DATE
						, BANK_STATEMENT.COMMON_CONCEPT
						, BANK_STATEMENT.PAYMENT
						, BANK_STATEMENT.AMOUNT
						, BANK_STATEMENT.DESCRIPTION
						, BANK_STATEMENT.STATUS
						, BANK_STATEMENT.REFERENCE1
						, BANK_STATEMENT.REFERENCE2
					);
		for (BankStatement bankStatement : bankStatements) {			
			query = query.values(
					bankStatement.getDomain()
					, bankStatement.getRegistryBank() != null ? bankStatement.getRegistryBank().getId() : null
							, bankStatement.getLotNumber()
							, bankStatement.getOperationDate() != null ? new java.sql.Date(bankStatement.getOperationDate().getTime()) : null
									, bankStatement.getCommonConcept().value()
									, bankStatement.isPayment() ? (byte) 1 : 0
											, bankStatement.getAmount()
											, bankStatement.getDescription()
											, bankStatement.getStatus().value()
											, bankStatement.getReference1()
											, bankStatement.getReference2());
		}
		
		return query.execute();
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
			List<BankStatement> bankStatements = getBankStatements(aonContext, domainId, empresaId, iban);
			return insertStatements(aonContext, bankStatements);
		}
		
	}
}
