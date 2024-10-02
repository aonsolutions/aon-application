package net.aonsolutions.aon.bank.nordigen.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccessToken;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisitions;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenResponse;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.watson.util.AonNumberUtils;

import net.aonsolutions.aon.bank.nordigen.NordigenAPI;
import net.aonsolutions.aon.bank.nordigen.RequisitionParams;

public class NordigenRequisitionUtils {

	// **************************************//
	// **Funciones requisition de Nordigen***//
	// **************************************//
	// **************************************//

	public static NordigenRequisition createRequisition(NordigenAccessToken token, String agreementId,
			String institutionId, String redirect) {
		RequisitionParams params = new RequisitionParams().setAgreement(agreementId).setRedirect(redirect)
				.setInstitutionId(institutionId).setUserLanguage(AonLanguage.SPANISH).setRedirectImmediate(true);
		return NordigenAPI.createRequisition(token.getAccess(), params);
	}

	public static NordigenResponse deleteRequisition(NordigenAccessToken token, String requisitionId) {
		return NordigenAPI.deleteRequisition(token.getAccess(), requisitionId);
	}

	private static void addRequisitions(List<NordigenRequisition> list, NordigenRequisitions requisitions) {
		if (requisitions != null && requisitions.getResult() != null) {
			list.addAll(requisitions.getResult());
		}
	}

	public static List<NordigenRequisition> getAllRequisitions(NordigenAccessToken token) {
		List<NordigenRequisition> list = new LinkedList<>();
		NordigenRequisitions requisitions = NordigenAPI.getRequisitions(token.getAccess(), 1000000, 0);
		addRequisitions(list, requisitions);
		String nextUrl = requisitions == null ? null : requisitions.getNext();
		while (nextUrl != null) {
			Pattern offsetPattern = Pattern.compile("offset=(?<offset>\\d+)", Pattern.CASE_INSENSITIVE);
			Matcher matcher = offsetPattern.matcher(nextUrl);
			String offsetStr = null;
			if (matcher.find()) {
				offsetStr = matcher.group("offset");
			}
			int offset = AonNumberUtils.toint(offsetStr);
			if (offset > 0) {
				requisitions = NordigenAPI.getRequisitions(token.getAccess(), 1000000, offset);
				addRequisitions(list, requisitions);
				nextUrl = requisitions == null ? null : requisitions.getNext();
			} else {
				nextUrl = null;
			}
		}
		return list;
	}

	public static List<RegistryBank> getRbanksByRequisition(Occam occam, String requisitionId) {
		List<RegistryBank> list = new ArrayList<>();
		AON.getRegistryBankStream(occam, f -> f.getRequisitionProperty().eq(requisitionId)).filter(Objects::nonNull)
				.forEach(list::add);
		return list;
	}

	public static void deleteRequisition(NordigenAccessToken token, NordigenRequisition requisition) {
		deleteRequisition(token, requisition != null ? requisition.getId() : null);
	}

	public static RegistryBank updateRequisitionId(Occam occam, NordigenRequisition requisition, Integer rbank) {
		if (requisition != null && requisition.getId() != null && AonNumberUtils.zeroIfNull(rbank) > 0) {
			String requisitionId = requisition.getId();
			RegistryBank registryBank = AON.getRegistryBank(occam, f -> f.getIdProperty().eq(rbank));
			if (registryBank != null) {
				registryBank.setRequisition(requisitionId);
				return AON.saveRegistryBank(occam, registryBank);
			}
		}
		return null;
	}

}
