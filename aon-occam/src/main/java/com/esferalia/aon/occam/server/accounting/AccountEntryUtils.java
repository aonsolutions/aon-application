package com.esferalia.aon.occam.server.accounting;

import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.AccountEntryDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.AccountEntryProperties;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountEntryUtils {

	public static Filter getFilter(AccountEntryProperties p, AccountEntryParams params) {
		Filter prop = p.getDomainProperty().eq(params.getDomain());
		if (params.getPeriod()  != null && params.getPeriod().intValue() != 0 ) {
			prop = prop.and(p.getAccountPeriodProperty().eq(params.getPeriod()));
		}
		if (params.getJournal()  != null && params.getJournal().intValue() != 0 ) {
			prop = prop.and(p.getJournalProperty().eq(params.getJournal()));
		}
		if (params.getActivity()  != null && params.getActivity().intValue() != 0 ) {
			prop = prop.and(p.getActivityProperty().eq(params.getActivity()));
		}
		if (params.getFrom() != null) {
			prop = prop.and(p.getEntryDateProperty().ge(params.getFrom()));
		}
		if (params.getTo() != null) {
			prop = prop.and(p.getEntryDateProperty().le(params.getTo()));
		}
		if (params.getType() != null) {
			prop = prop.and(p.getEntryTypeProperty().eq((byte) params.getType().ordinal()));
		}
		if (!params.hasConfidentialityRole()) {
			prop = prop.and(p.getConfidentialProperty().eq(
					SecurityLevel.OFFICIAL.value()));
		} else {
			if (params.isConfidential()) {
				prop = prop.and(p.getConfidentialProperty().eq(
						SecurityLevel.CONFIDENTIAL.value()));
			}
		}
		if (AonStringUtils.isNotBlank(params.getComments())) {
			prop = prop.and(p.getCommentsProperty().like(AonStringUtils.SQLlike(params.getComments())));
		}
		return prop;
	}

	public static Filter getFilterByLines(AccountEntryDetailProperties p,
			AccountEntryParams params) {
		Filter prop = getFilter(p, params);

		if (params.getAccount() != null) {
			prop = prop.and(
				p.getAccountProperty().eq(params.getAccount())
					.or(p.getBalancingAccountProperty().eq(params.getAccount()))
				);
		}
		if (AonStringUtils.isNotBlank(params.getConcept())) {
			prop = prop.and(p.getConceptProperty().like(
					AonStringUtils.SQLlike(params.getConcept())));
		}
		if (params.getDebit() != null && params.getDebit() != 0.0 ) {
			prop = prop.and(p.getDebitProperty().eq(params.getDebit()));
		}
		if (params.getCredit() != null  && params.getCredit() != 0.0 ) {
			prop = prop.and(p.getCreditProperty().eq(params.getCredit()));
		}
		if (AonStringUtils.isNotBlank(params.getDocument())) {
			prop = prop.and(p.getDocumentNumber().like(
					AonStringUtils.SQLlike(params.getDocument())));
		}
		return prop;
	}
}
