package com.esferalia.aon.occam.server.accounting;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.AccountEntryDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.AccountEntryProperties;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountEntryUtils {

	public static Filter getFilterByHeader(AONContext ctx, AccountEntryProperties p, AccountEntryParams params) {
		Filter prop = p.getDomainProperty().eq(params.getDomain());
		if (params.getPeriod()  != null && params.getPeriod().intValue() != 0 ) {
			prop = prop.and(p.getAccountPeriodProperty().eq(params.getPeriod()));
		}
		if (params.getAccountEntryId() != null && params.getAccountEntryId().intValue() != 0 ) {
			prop = prop.and(p.getIdProperty().eq(params.getAccountEntryId()));
		}
		if (params.getJournal()  != null && params.getJournal().intValue() != 0 ) {
			prop = prop.and(p.getJournalProperty().eq(params.getJournal()));
		}
		if (params.getFromJournal()  != null && params.getFromJournal().intValue() != 0 ) {
			prop = prop.and(p.getJournalProperty().ge(params.getFromJournal()));
		}
		if (params.getToJournal()  != null && params.getToJournal().intValue() != 0 ) {
			prop = prop.and(p.getJournalProperty().le(params.getToJournal()));
		}
		
		if (params.getActivity()  != null && params.getActivity().intValue() != 0 ) {
			if (params.getActivity().intValue() == -1) {
				prop = prop.and(p.getActivityProperty().isNull());
			} else {
				prop = prop.and(p.getActivityProperty().eq(params.getActivity()));
			}
		}
		if (params.getFromDate() != null) {
			prop = prop.and(p.getEntryDateProperty().ge(params.getFromDate()));
		}
		if (params.getToDate() != null) {
			prop = prop.and(p.getEntryDateProperty().le(params.getToDate()));
		}
		if (params.getType() != null) {
			prop = prop.and(p.getEntryTypeProperty().eq((byte) params.getType().ordinal()));
		}
		User user = SecurityDAO.getUser(ctx);
		if (user == null || !user.hasConfidentialityRole()) {
			prop = prop.and(p.getConfidentialProperty().eq(SecurityLevel.OFFICIAL.value()));
		} else {
			if (params.getSecurityLevel() != null ) {
				prop = prop.and(p.getConfidentialProperty().eq(params.getSecurityLevel().value()));
			}
		}
		if (AonStringUtils.isNotBlank(params.getComments())) {
			prop = prop.and(p.getCommentsProperty().like(AonStringUtils.SQLlike(params.getComments())));
		}
		if (params.getFromCreationDate() != null) {
			prop = prop.and(p.getCreationDateProperty().ge(new java.sql.Timestamp(params.getFromCreationDate().getTime())));
		}
		if (params.getToCreationDate() != null) {
			prop = prop.and(p.getCreationDateProperty().le(new java.sql.Timestamp(params.getToCreationDate().getTime())));
		}
		if (AonStringUtils.isNotBlank(params.getCreationUser())) {
			prop = prop.and(p.getCreationUserProperty().like(AonStringUtils.SQLlike(params.getCreationUser())));
		}
		if (params.getFromModificationDate() != null) {
			prop = prop.and(p.getModificationDateProperty().ge(new java.sql.Timestamp(params.getFromModificationDate().getTime())));
		}
		if (params.getToModificationDate() != null) {
			prop = prop.and(p.getModificationDateProperty().le(new java.sql.Timestamp(params.getToModificationDate().getTime())));
		}
		if (AonStringUtils.isNotBlank(params.getModificationUser())) {
			prop = prop.and(p.getModificationUserProperty().like(AonStringUtils.SQLlike(params.getModificationUser())));
		}
		
		if (params.getType() != null) {
			prop = prop.and(p.getEntryTypeProperty().eq((byte) params.getType().ordinal()));
		}
		return prop;
	}

	public static Filter getFilterByLines(AONContext ctx, AccountEntryDetailProperties p, AccountEntryParams params) {
		Filter prop = getFilterByHeader(ctx, p, params);

		if (params.getAccount() != null) {
			if (params.getApplyAccount() == 1) {
				prop = prop.and(p.getAccountProperty().eq(params.getAccount()));	
			} else if (params.getApplyAccount() == 2) {
				prop = prop.and(p.getBalancingAccountProperty().eq(params.getAccount()));
			} else {
				prop = prop.and(
						p.getAccountProperty().eq(params.getAccount())
						.or(p.getBalancingAccountProperty().eq(params.getAccount()))
						);
			}
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
