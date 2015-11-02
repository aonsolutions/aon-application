package com.esferalia.aon.occam.server.accounting;

import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.accounting.AccountEntryProperties;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;

public class AccountEntryUtils {

	public static Filter getFilter(AccountEntryProperties p , AccountEntryParams params) {
		Filter prop = p.getDomainProperty().eq(params.getDomain());
		if (params.getFrom() != null) {
			prop = prop.and(p.getEntryDateProperty().ge(params.getFrom()));
		}
		if (params.getTo() != null) {
			prop = prop.and(p.getEntryDateProperty().le(params.getTo()));
		}
		if (!params.hasConfidentialityRole()) {
			prop = prop.and(p.getConfidentialProperty().eq( SecurityLevel.OFFICIAL.value() ));
		} else {
			if (params.isConfidential()) {
				prop = prop.and(p.getConfidentialProperty().eq( SecurityLevel.CONFIDENTIAL.value() ));
			}
		}
		return prop;
	}
}
