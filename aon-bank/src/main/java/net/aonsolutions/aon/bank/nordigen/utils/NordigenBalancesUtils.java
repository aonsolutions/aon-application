package net.aonsolutions.aon.bank.nordigen.utils;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBalanceType;

import net.aonsolutions.aon.bank.nordigen.NordigenAPI;

public class NordigenBalancesUtils {

	// **************************************//
	// **Funciones balances de cuentas Nordigen*//
	// **************************************//
	// **************************************//

	public static LinkedList<NordigenAccountBalance> getAccountBalances(NordigenAccessToken token,
			String nordigenAccountId) {
		return NordigenAPI.getBalances(token.getAccess(), nordigenAccountId);
	}

	public static NordigenAccountBalance filterConsolidado(List<NordigenAccountBalance> balances) {
		NordigenAccountBalance consolidado = null;
		consolidado = balances.stream().filter(bal -> NordigenBalanceType.CLOSING_BOOKED.equals(bal.getBalanceType()))
				.findFirst().orElse(null);

		if (consolidado == null && balances.size() > 0) {
			return balances.get(0);
		}

		return consolidado;
	}

	public static NordigenAccountBalance filterReal(List<NordigenAccountBalance> balances) {
		NordigenAccountBalance real = null;

		real = balances.stream().filter(bal -> !NordigenBalanceType.CLOSING_BOOKED.equals(bal.getBalanceType()))
				.findFirst().orElse(null);

		if (real == null) {
			return filterConsolidado(balances);
		}

		return real;
	}
}
