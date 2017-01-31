package com.esferalia.aon.occam.server.finance;

import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.finance.FinanceProperties;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FinanceUtils {

	public static Filter getPendingFilter(FinanceProperties p,
			FinanceParams params) {
		Filter prop = getFilter(p, params);
		prop = prop.and(
				p.getStatusProperty().eq(FinanceStatus.PENDING.value())
				.or(p.getStatusProperty().eq(FinanceStatus.RETURNED.value()))
				);
		return prop;
	}
	
	public static Filter getFilter(FinanceProperties p,
			FinanceParams params) {
		Filter prop = p.getDomainProperty().eq(params.getDomain());
		if (params.getFrom() != null) {
			prop = prop.and(p.getDueDateProperty().ge(params.getFrom()));
		}
		if (params.getTo() != null) {
			prop = prop.and(p.getDueDateProperty().le(params.getTo()));
		}
		if (params.getRegistry() != null) {
			prop = prop.and(p.getRegistryProperty().eq(params.getRegistry()));
		}
		if (AonStringUtils.isNotEmpty(params.getConcept())) {
			prop = prop.and(p.getConceptProperty().like(
					AonStringUtils.SQLlike(params.getConcept())));
		}
		if (params.getAmount() != null && AonMathUtils.isNotZero(params.getAmount())) {
			if (params.isNearbyNumbers()) {
				double factor = params.getAmount() * params.getFactor() / 100;
				prop = prop.and(p.getAmountProperty().between((params.getAmount()-factor), (params.getAmount()+factor)));
			} else {
				prop = prop.and(p.getAmountProperty().eq(params.getAmount()));
			}
				
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
		return prop;
	}

}
