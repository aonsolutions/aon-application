package net.aonsolutions.aon.bank.nordigen;

import static net.aonsolutions.aon.bank.nordigen.NordigenConstants.RADD_INFO_REQUISITION_ATTRIBUTE_PATTERN;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenBalanceType;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisitionStatus;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenAccountBalance;
import com.esferalia.aon.occam.api.model.finance.nordigen.NordigenRequisition;
import com.esferalia.aon.occam.api.model.registry.RegistryAddInfo;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class NordigenUtils {
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
			return NordigenRequisitionStatus.LN.equals(requisition.getStatus());
		}
			return false;
	}
	
	public static NordigenAccountBalance getLastAccountBalance(List<NordigenAccountBalance> balances) {
		if (balances != null) {
			if (balances.size() > 1) {
				Optional<NordigenAccountBalance> balance = balances.stream()
				.filter(b -> b.getReferenceDate() != null && NordigenBalanceType.CLOSING_BOOKED.equals(b.getBalanceType()))
				.sorted((b1, b2) -> b1.getReferenceDate().compareTo(b2.getReferenceDate())).findFirst();
				if (balance.isPresent()) {
					return balance.get();
				}
			}
			return balances.get(0);
		}
		return null;
	}
	
}
