package aon.bank;

import static com.esferalia.aon.jooq.tables.BankStatement.BANK_STATEMENT;

import java.util.Calendar;
import java.util.Date;

import org.jooq.InsertValuesStep11;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.jooq.tables.records.BankStatementRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;

import aon.bank.exceptions.BankException;
import aon.bank.exceptions.BankJooqException;

public class CheckItJooq {

	public static final String CHECKIT_R1 = "CHECKIT";

	/**
	 * 
	 * @param params
	 *               <ul>
	 *               <li><Strong>domainName</strong>: The <u>AON DB</u> domain name</li>
	 *               <li><Strong>user</strong>: The <u>AON DB</u> user name</li>
	 *               <li><Strong>claveApi</strong>: The <u>CheckIt</u> API key</li>
	 *               <li><Strong>empresa_id</strong>: The <u>CheckIt</u> Enterprise ID</li>
	 *               <li><Strong>iban</strong>: The bank account number</li>
	 *               </ul>
	 * @return
	 * @throws BankException
	 */
	public static int insertTransactions(JSONObject params) throws BankException {
		Date today = new Date();
		String domainName = params.optString("domainName");
		String user = params.optString("user");
		JSONObject requestParams = new JSONObject(params.toString());

		String iban = params.optString("iban");

		String accountId = Utilities.getAccountIdByIBAN(params.optString("claveApi"), params.optInt("empresa_id"),
				iban);

		requestParams.put("fecha_hasta", Utilities.formatDateForTransactions(today)); // HOY
		requestParams.remove("domainName");
		requestParams.remove("user");
		requestParams.remove("iban");
		requestParams.put("cuenta_bancaria_id", accountId);

		try (AONContext aonContext = AONContext.getAONContext(domainName, user)) {

			RegistryBank rbank = Utilities.getRbankByIban(aonContext, iban);

			if (rbank == null || rbank.getId() == null) {
				throw new BankJooqException("Account does not exist in DB");
			}

			int lotNumber = Utilities.getNextLotNumber(aonContext, rbank);

			Object[] max = Utilities.getMaxMovementIdAndDate(aonContext, rbank);
			String maxId = null;
			Date lastOperationDate = null;

			if (max == null) {
				lastOperationDate = Utilities.getLastOperationDate(aonContext, rbank);
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

			Integer maximumId = maxId != null ? Integer.parseInt(maxId) : 0;

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

					Byte payment = (byte) (amount < 0 ? 1 : 0);

					query = query.values(
							aonContext.getDomainId()
							, rbank.getId()
							, lotNumber
							, operationDate
							, (byte) 0
							, payment
							, Math.abs(amount)
							, description
							, (byte) 0
							, CHECKIT_R1,
							Utilities.leadingZeros(movementId, 16));
				}

			}
			return query.execute();
//			System.out.println(query.getSQL());
		}

	}

	/**
	 * Method which picks up bank transactions from CheckIt and records them into
	 * the DB.
	 * 
	 * @param domainName The <u>AON DB</u> domain name
	 * @param user       The <u>AON DB</u> user name
	 * @param claveApi   The <u>CheckIt</u> API key
	 * @param empresaId  The <u>CheckIt</u> Enterprise ID
	 * @param iban       The bank account number
	 * @return The number of rows inserted into the DB
	 * @throws BankException
	 */
	public static int insertTransactions(String domainName, String user, String claveApi, Integer empresaId,
			String iban) throws BankException {
		JSONObject params = new JSONObject();
		params.put("domainName", domainName);
		params.put("user", user);
		params.put("claveApi", claveApi);
		params.put("empresa_id", empresaId);
		params.put("iban", iban);
		return insertTransactions(params);
	}
}
