package net.aonsolutions.aon.bank.nordigen.utils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountMetadata;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountTransactions;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankAccount;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBankStatement;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenException;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenInstitution;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.impl.jooq.dao.NordigenDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.bank.nordigen.NordigenAPI;
import net.aonsolutions.aon.bank.nordigen.NordigenUtils;

public class NordigenTransactionsUtils {

	// **************************************//
	// **Funciones transacciones de cuentas Nordigen*//
	// **************************************//
	// **************************************//

	public static NordigenAccountTransactions getTransactions(NordigenAccessToken token, String nordigenAccountId,
			Date dateFrom) {
		return NordigenAPI.getTransactions(token.getAccess(), nordigenAccountId, null, null);
	}

	public static List<NordigenBankStatement> getPendingAccountTransactions(NordigenBankAccount account,
			NordigenAccountTransactions transactions) {
		if (transactions != null) {
			return AonCollectionUtils.stream(transactions.getPending())
					.map(t -> NordigenBankAccount.toBankStatement(account, t)).filter(Objects::nonNull)
					.map(st -> st.setPending(true)).collect(Collectors.toCollection(LinkedList::new));
		}
		return new LinkedList<>();
	}

	public static List<NordigenBankStatement> getBookedAccountTransactions(NordigenBankAccount account,
			NordigenAccountTransactions transactions, Date dateFrom) {
		if (transactions != null) {
			return AonCollectionUtils.stream(transactions.getBooked()).filter(Objects::nonNull)
					.filter(tr -> AonDateUtils.compare(AonDateUtils.todayIfNull(tr.getBookingDate()), dateFrom) >= 0)
					.map(t -> NordigenBankAccount.toBankStatement(account, t)).map(st -> st.setPending(false))
					.collect(Collectors.toCollection(LinkedList::new));
		}
		return new LinkedList<>();
	}

	public static LinkedList<NordigenBankStatement> getNotInsertedTransactions(NordigenAccessToken token,
			NordigenBankAccount account) {
		NordigenAccountMetadata metadata = account.getMetadata();
		String accId = metadata != null ? AonStringUtils.trimToNull(metadata.getId()) : null;
		Date dateFrom = guessDateForm(account);
		if (dateFrom != null) {
			StringBuilder exceptionMessage = new StringBuilder();
			NordigenAccountTransactions transactions = getTransactions(token, accId, dateFrom);
			LinkedList<NordigenBankStatement> stList = new LinkedList<>();
			stList.addAll(getPendingAccountTransactions(account, transactions));
			stList.addAll(getBookedAccountTransactions(account, transactions, dateFrom));
			if (!exceptionMessage.isEmpty()) {
				throw new NordigenException(exceptionMessage.toString());
			}

			NordigenAccountBalance consBalance = NordigenBalancesUtils.filterConsolidado(account.getBalances());
			if (consBalance != null && consBalance.getBalanceAmount() != null) {
				double amount = consBalance.getBalanceAmount().getAmount();
				for (NordigenBankStatement statement : stList) {
					statement.setCurrentBalance(amount);
					int factor = (statement.isPayment() ? (-1) : 1);
					amount = AonMathUtils.round(amount - (statement.getAmount() * factor));
				}
			}
			return stList;
		}
		return new LinkedList<>();
	}

	public static Date guessDateForm(NordigenBankAccount account) {
		Date lastMovDate = account.getLastMovementDate();
		Date today = new Date();
		Date dateFrom = null;
		if (lastMovDate != null) {
			dateFrom = AonDateUtils.addDays(lastMovDate, 1);
		} else {
			NordigenInstitution institution = account.getInstitution();
			if (institution != null && institution.getTransactionTotalDays() != null) {
				Integer days = institution.getTransactionTotalDays();
				dateFrom = AonDateUtils.addDays(today, -days);
			}
		}
		if (dateFrom != null && (AonDateUtils.isSameDay(today, dateFrom) || today.compareTo(dateFrom) >= 0)) {
			return dateFrom;
		}
		return null;
	}

	public static List<NordigenBankStatement> getStoredBankStatements(Occam occam, RegistryBank rbank, Date dateFrom) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return NordigenDAO.getBankStatements(ctx, rbank, dateFrom, new Date());
		}
	}
}
