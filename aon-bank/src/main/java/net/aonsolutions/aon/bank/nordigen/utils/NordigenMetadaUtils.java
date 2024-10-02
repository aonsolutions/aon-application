package net.aonsolutions.aon.bank.nordigen.utils;

import java.util.List;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountDetail;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountMetadata;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.bank.nordigen.NordigenAPI;

public class NordigenMetadaUtils {

	// **************************************//
	// **Funciones metadata de cuentas Nordigen*//
	// **************************************//
	// **************************************//

	public static NordigenAccountMetadata getAccountMetadata(NordigenAccessToken token, String nordigenAccountId) {
		return NordigenAPI.getAccountMetadata(token.getAccess(), nordigenAccountId);
	}

	public static NordigenAccountMetadata getNordigenAccountMetadata(NordigenAccessToken token,
			NordigenRequisition requisition, RegistryBank rbank) {
		if (requisition != null && rbank != null && rbank.getBankAccount() != null) {
			String iban = rbank.getBankAccount().getIban();
			Occam occam = new Occam();
			occam.setDomain(rbank.getDomain());

			if (AonStringUtils.isBlank(iban)) {
				return null;
			}
			List<String> accs = requisition.getAccounts();
			if (accs != null) {
				for (String accId : accs) {
					try {
						NordigenAccountMetadata metadata = getAccountMetadata(token, accId);
						NordigenAccountDetail detail = NordigenAPI.getDetail(token.getAccess(), accId);
						if (metadata != null && AonStringUtils.equalsIgnoreCase(iban, metadata.getIban())
								&& detail.getCurrency().equals("EUR")) {
							return metadata;
						}
					} catch (Exception e) {
						// Replantear
//				AON_SOLUTIONS.insertLogData(occam, new LogData(rbank.getDomain(), e));
					}
				}
			}
		}
		return null;
	}
}
