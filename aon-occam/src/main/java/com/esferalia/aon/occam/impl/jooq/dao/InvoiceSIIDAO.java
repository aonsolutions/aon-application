package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.DataResponse.DATA_RESPONSE;
import static com.esferalia.aon.jooq.tables.DataResponseDetail.DATA_RESPONSE_DETAIL;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceFiscal.INVOICE_FISCAL;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilterOLD;
import com.esferalia.aon.occam.api.model.finance.InvoiceFiscal;
import com.esferalia.aon.occam.api.model.finance.InvoiceMin;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO.EnterpriseActivityFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.InvoicePropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO.ScopeFiller;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceFiscalDAO;
import com.esferalia.aon.watson.util.AonEnumUtils;

public class InvoiceSIIDAO {
	
	private InvoiceSIIDAO() {
	}

	private static final InvoicePropertiesDAO INVOICE_PROPERTIES = new InvoicePropertiesDAO();

	public static Stream<Invoice> getSiiInvoiceStream(AONContext ctx, InvoiceFilterOLD filter,Boolean pending,  Boolean aceptada, Boolean aceptadaErrores, Boolean incorrecta, Boolean anulada, String sii){
		Boolean intracomunitaria = "intracomunitarias".equals(sii);
		Boolean bienes = "bienes".equals(sii);
		Boolean cp = "cp_cobros_pagos".equals(sii) || "cp_cobros".equals(sii) || "cp_pagos".equals(sii);
		
		if(pending && !intracomunitaria && !cp && !bienes
				&& !aceptada && !aceptadaErrores && !incorrecta && !anulada){
			FilterDAO d = (FilterDAO) filter.filter(INVOICE_PROPERTIES);			
			return ctx.getDslContext().select()
				.from(INVOICE)
				.leftOuterJoin(DATA_RESPONSE).on(INVOICE.ID.eq(DATA_RESPONSE.SOURCE_ID)
						.and(DATA_RESPONSE.SOURCE.eq(DataResponseSource.SII_INVOICE.value())))
				.where(INVOICE_PROPERTIES.getConditions(filter))
				.and(DATA_RESPONSE.SOURCE_ID.isNull())
				.limit(d.getPerPage())
				.offset(d.getPerPage() * (d.getPage() -1))
				.fetch().stream().map(new SiiInvoiceFiller(true));
		} else {
			Condition c = DATA_RESPONSE_DETAIL.DATA_VALUE.eq(""); 
			if(pending) c = c.or(DATA_RESPONSE_DETAIL.DATA_VALUE.eq("Pendiente"));
			if(aceptada) c = cp ? c.or(DATA_RESPONSE_DETAIL.DATA_VALUE.eq("Parcial")) : c.or(DATA_RESPONSE_DETAIL.DATA_VALUE.eq("Correcto"));
			if(aceptadaErrores) c = cp ? c.or(DATA_RESPONSE_DETAIL.DATA_VALUE.eq("Pagado")) : c.or(DATA_RESPONSE_DETAIL.DATA_VALUE.eq("AceptadoConErrores"));
			if(incorrecta) c = c.or(DATA_RESPONSE_DETAIL.DATA_VALUE.eq("Incorrecto"));
			if(anulada) c = c.or(DATA_RESPONSE_DETAIL.DATA_VALUE.eq("Anulada"));
			String status = "status";
			if(intracomunitaria)status =  "status_intra";
			else if(cp) status = "status_cp";
			else if(bienes) status = "status_bienes";
			return INVOICE_PROPERTIES.build(ctx.getDslContext().select().from(INVOICE)
					.join(DATA_RESPONSE).on(DATA_RESPONSE.SOURCE.eq(DataResponseSource.SII_INVOICE.value()).and(DATA_RESPONSE.SOURCE_ID.eq(INVOICE.ID)))
					.join(DATA_RESPONSE_DETAIL).on(DATA_RESPONSE_DETAIL.DATA_VARIABLE.eq(status).and(DATA_RESPONSE_DETAIL.DATA_RESPONSE.eq(DATA_RESPONSE.ID))
						.and(c))
				, filter)
				.fetch().stream().map(new SiiInvoiceFiller(false));
		}
	}

	/**
	 *  @deprecated USE InvoiceDAO suitable FILLER
	 */
	@Deprecated 
	private static class SiiInvoiceFiller extends InvoiceFiller implements Function<Record,Invoice> {
		Boolean pending;
		private SiiInvoiceFiller(Boolean pending) {
			this.pending = pending;
		}
		
		@Override
		public Invoice apply(Record r) {
			return buildInvoice(r)
				.setSiiStatus(pending ? "Pendiente" : r.getValue(DATA_RESPONSE_DETAIL.DATA_VALUE));				
		}	
	}

	private static class InvoiceFiller extends Filler implements Function<Record,Invoice> {

		@Override
		public Invoice apply(Record r) {
			return buildInvoice(r);
		}
		
		protected static Invoice buildInvoice(Record r) {
			return new Invoice()
				.setId(r.getValue(INVOICE.ID))
				.setDomain(r.getValue(INVOICE.DOMAIN))
				.setType(AonEnumUtils.enumValue(InvoiceType.class, r.getValue(INVOICE.TYPE)))
				.setSeries(r.getValue(INVOICE.SERIES))
				.setNumber(r.getValue(INVOICE.NUMBER))
				.setReferenceCode(r.getValue(INVOICE.REFERENCE_CODE))
				.setIssueDate(r.getValue(INVOICE.ISSUE_DATE))
				.setTaxDate(r.getValue(INVOICE.TAX_DATE))
				.setSecurityLevel(AonEnumUtils.enumValue(SecurityLevel.class, r.getValue(INVOICE.SECURITY_LEVEL)))
				.setRegistry( r.getValue(INVOICE.REGISTRY))
				.setRegistryDocument(r.getValue(INVOICE.RDOCUMENT))
				.setRegistryDocumentType(AonEnumUtils.enumValue(DocumentType.class,r.getValue(INVOICE.RDOCUMENT_TYPE)))
				.setRegistryDocumentCountry(Country.safeValueOf(r.getValue(INVOICE.RDOCUMENT_COUNTRY)))
				.setRegistryName(r.getValue(INVOICE.RNAME))
				.setRegistryAddress(r.getValue(INVOICE.RADDRESS))
				.setScope(checkField(r, SCOPE.ID)
						? ScopeFiller.buildScope(r)
						: new Scope().setId(r.getValue(INVOICE.SCOPE)))
				.setActivity(checkField(r, ENTERPRISE_ACTIVITY.ID)
						? EnterpriseActivityFiller.build(r)
						: new EnterpriseActivity().setId(r.getValue(INVOICE.ACTIVITY)))	
				.setInvestAsset(r.getValue(INVOICE.INVEST_ASSET))
				.setProject(r.getValue(INVOICE.PROJECT))
				.setRectificationType(AonEnumUtils.enumValue(RectificationType.class, r.getValue(INVOICE.RECTIFICATION_TYPE)))	
				.setRectificationInvoice(
						Optional.ofNullable( r.getValue(INVOICE.RECTIFICATION_INVOICE) ).map( rid -> new InvoiceMin().setId(rid)).orElse(null)
					)	
				.setTransaction(InvoiceTransactionType.safeValueOf(r.getValue(INVOICE.TRANSACTION)))
				.setRecorded(r.getValue(INVOICE.STATUS) != null && r.getValue(INVOICE.STATUS) == 1 )	
				.setSurcharge(r.getValue(INVOICE.SURCHARGE) == 1 )	
				.setWithholding(r.getValue(INVOICE.WITHHOLDING) == 1 )	
				.setWithholdingFarmer(r.getValue(INVOICE.WITHHOLDING_FARMER) == 1 )	
				.setVatAccrualPayment(r.getValue(INVOICE.VAT_ACCRUAL_PAYMENT) == 1 )	
				.setInvestment(r.getValue(INVOICE.INVESTMENT) == 1 )	
				.setService(r.getValue(INVOICE.SERVICE) == 1 )	
				.setAdvance(r.getValue(INVOICE.ADVANCE) == 1 )	
				.setTaxableBase(r.getValue(INVOICE.TAXABLE_BASE))	
				.setVatQuota(r.getValue(INVOICE.VAT_QUOTA))	
				.setRetentionQuota(r.getValue(INVOICE.RETENTION_QUOTA))	
				.setTotal(r.getValue(INVOICE.TOTAL))	
				.setComments(r.getValue(INVOICE.COMMENTS))
				.setFiscal(checkField(r, INVOICE_FISCAL.INVOICE)
						? InvoiceFiscalDAO.InvoiceFiscalFiller.buildInvoiceFiscal(r)
						: new InvoiceFiscal())
				.setSeller(getValue(r, INVOICE.SELLER))
				.setCreationDate(r.getValue(INVOICE.CREATION_DATE))
				.setCreationUser(r.getValue(INVOICE.CREATION_USER))
				.setModificationDate(r.getValue(INVOICE.MODIFICATION_DATE))
				.setModificationUser(r.getValue(INVOICE.MODIFICATION_USER));
		}
	}
}
