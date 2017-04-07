package com.esferalia.aon.occam.server.finance;

import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.FinanceParams;
import com.esferalia.aon.occam.api.model.finance.FinanceProperties;
import com.esferalia.aon.occam.api.model.finance.VATProperties;
import com.esferalia.aon.occam.api.model.fiscal.VatParams;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryType;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FinanceUtils {

	private static final Byte[] INPUT_TYPES = new Byte[]{ InvoiceType.PURCHASE.value(),InvoiceType.EXPENSES.value()};
	
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
		if (params.getPayment() != null) {
			prop = prop.and(p.getPaymentProperty().eq( AonEnumUtils.getByte( params.getPayment()) ));
		}
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
		if (AonStringUtils.isNotEmpty(params.getReferenceCode())) {
			prop = prop.and(p.getInvoiceReferenceCode().like(AonStringUtils.SQLlike(params.getReferenceCode())));
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

	public static Filter getVATFilter(VATProperties p, VatParams params) {
		Filter prop = p.getDomainProperty().eq(params.getDomain());
		if (params.getRegistry()  != null && params.getRegistry().intValue() != 0 ) {
			prop = prop.and(p.getRegistryProperty().eq(params.getRegistry()));
		}
		if (params.getActivity()  != null && params.getActivity().intValue() != 0 ) {
			prop = prop.and(p.getActivityProperty().eq(params.getActivity()));
		}
		if (params.getAccrualRegime() != null) {
			prop = prop.and(p.getAccrualRegimeProperty().eq( AonEnumUtils.getByte(params.getAccrualRegime())));
		}
		if (params.getInvestment() != null) {
			prop = prop.and(p.getInvestmentProperty().eq( AonEnumUtils.getByte(params.getInvestment())));
		}
		if (params.getService() != null) {
			if ( params.getService() ) {
				prop = prop.and(
					p.getServiceProperty().eq((byte)1).or( p.getInvoiceTypeProperty().eq( InvoiceType.EXPENSES.value())) 
						);
			} else {
				prop = prop.and(
					p.getServiceProperty().ne((byte)1).and(p.getInvoiceTypeProperty().ne( InvoiceType.EXPENSES.value()))
						);
			}
		}
		if (params.getPercent() != null) {
			if (params.getVatSummaryType() == VatSummaryType.SURCHARGE) {
				prop = prop.and(p.getSurchargePercentProperty().eq( params.getPercent()));
			} else {
				prop = prop.and(p.getPercentProperty().eq( params.getPercent()));
			}
		}
		if (params.getOutput() != null) {
			if (params.getOutput()) {
				prop = prop.and(p.getInvoiceTypeProperty().eq( InvoiceType.SALES.value()));
			} else {
				prop = prop.and(p.getInvoiceTypeProperty().in( INPUT_TYPES ));
			}
		}
		if (params.getVatSummaryType() != null) {
			if (params.getVatSummaryType() == VatSummaryType.NATIONAL) {
				prop = prop.and(p.getInvoiceTransactionProperty().eq( InvoiceTransactionType.NATIONAL.value()));
//				prop = prop.and(p.getSurchargeProperty().eq( AonEnumUtils.getByte(false)));
				prop = prop.and(p.getFarmerRegimeProperty().eq( AonEnumUtils.getByte(false)));
			} else if (params.getVatSummaryType() == VatSummaryType.SURCHARGE){	
				prop = prop.and(p.getInvoiceTransactionProperty().eq( InvoiceTransactionType.NATIONAL.value()));
				prop = prop.and(p.getSurchargeProperty().eq( AonEnumUtils.getByte(true)));
			} else if (params.getVatSummaryType() == VatSummaryType.FARMER) {
				prop = prop.and(p.getInvoiceTransactionProperty().eq( InvoiceTransactionType.NATIONAL.value()));
				prop = prop.and(p.getFarmerRegimeProperty().eq( AonEnumUtils.getByte(true)));
			} else if (params.getVatSummaryType() == VatSummaryType.INTRACOMMUNITY) {
				prop = prop.and(p.getInvoiceTransactionProperty().eq( InvoiceTransactionType.INTRACOMMUNITY.value()));
			} else if (params.getVatSummaryType() == VatSummaryType.EXTRACOMMUNITY) {
				prop = prop.and(p.getInvoiceTransactionProperty().eq( InvoiceTransactionType.EXTRACOMMUNITY.value()));
			} else if (params.getVatSummaryType() == VatSummaryType.CAN_CEU_MEL) {
				prop = prop.and(p.getInvoiceTransactionProperty().eq( InvoiceTransactionType.CAN_CEU_MEL.value()));
			} else if (params.getVatSummaryType() == VatSummaryType.OTHER_ISP) {
				prop = prop.and(p.getInvoiceTransactionProperty().eq( InvoiceTransactionType.OTHER_ISP.value()));
			}
		}
		return prop;
	}

}
