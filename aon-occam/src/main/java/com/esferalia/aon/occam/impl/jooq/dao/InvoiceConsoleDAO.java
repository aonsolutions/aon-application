package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsole;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class InvoiceConsoleDAO {
	
	private InvoiceConsoleDAO() {

	}

	// ---------------------------------------------------------------------	
	// --------------------------------------------------------- [PUBLIC] --	
	// ---------------------------------------------------------------------
	
	public static Stream<InvoiceConsole> getInvoiceHeaders(AONContext ctx, InvoiceConsoleParams params) {
		ctx.checkRead();
		Field<Integer> orderedType = getOrderedType();
		return ctx.getDslContext()
			.select(
				 INVOICE.ID
				,INVOICE.DOMAIN
				,orderedType
				,INVOICE.ACTIVITY
				,INVOICE.TYPE
				,INVOICE.TRANSACTION
				,INVOICE.SERIES
				,INVOICE.NUMBER
				,INVOICE.REFERENCE_CODE
				,INVOICE.ISSUE_DATE
				,INVOICE.TAX_DATE
				,INVOICE.REGISTRY
				,INVOICE.RDOCUMENT
				,INVOICE.RDOCUMENT_TYPE
				,INVOICE.RDOCUMENT_COUNTRY
				,INVOICE.RNAME
				,INVOICE.SECURITY_LEVEL
				
				,ENTERPRISE_ACTIVITY.DESCRIPTION
				,IAE.EPIGRAPH				
			)
			.from(INVOICE)
			.join(REGISTRY).on(REGISTRY.ID.equal(INVOICE.REGISTRY))
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.equal(INVOICE.ACTIVITY))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
			.where(getWhere(params))
			.orderBy(getOrderBy(params))
			.limit(params.getOffset() , params.getLimit())
			.fetch()
			.stream()
			.map(new HeaderInvoiceFiller())
			.map(i -> new InvoiceConsole().setInvoice(i))
			;
	}

	// ---------------------------------------------------------------------	
	// -------------------------------------------------------- [PRIVATE] --	
	// ---------------------------------------------------------------------
	
	private static class HeaderInvoiceFiller  extends Filler implements Function<Record,Invoice> {

		@Override
		public Invoice apply(Record r) {
			return new Invoice()
				.setId(getValue(r,INVOICE.ID))
				.setDomain(getValue(r,INVOICE.DOMAIN))
				.setType(AonEnumUtils.enumValue(InvoiceType.class,getValue(r,INVOICE.TYPE)))
				.setSeries(getValue(r,INVOICE.SERIES))
				.setNumber(getValue(r,INVOICE.NUMBER))
				.setReferenceCode(getValue(r,INVOICE.REFERENCE_CODE))
				.setIssueDate(getValue(r,INVOICE.ISSUE_DATE))
				.setTaxDate(getValue(r,INVOICE.TAX_DATE))
				.setSecurityLevel(AonEnumUtils.enumValue(SecurityLevel.class,getValue(r,INVOICE.SECURITY_LEVEL)))
				.setRegistry(getValue(r,INVOICE.REGISTRY))
				.setRegistryDocument(getValue(r,INVOICE.RDOCUMENT))
				.setRegistryDocumentType(AonEnumUtils.enumValue(DocumentType.class,getValue(r,INVOICE.RDOCUMENT_TYPE)))
				.setRegistryDocumentCountry(Country.safeValueOf(getValue(r,INVOICE.RDOCUMENT_COUNTRY)))
				.setRegistryName(getValue(r,INVOICE.RNAME))
				.setActivity(
					getValue(r,INVOICE.ACTIVITY) == null
						?null
						:new EnterpriseActivity()
							.setId(getValue(r,INVOICE.ACTIVITY))
							.setDescription(getValue(r,ENTERPRISE_ACTIVITY.DESCRIPTION))
							.setEpigraph(getValue(r,IAE.EPIGRAPH)))
			;
		}
		
	}
	
	private static Field<Integer> getOrderedType() {
		return DSL.decode()
		   .when(INVOICE.TYPE.equal((byte) 0), 0)
		   .when(INVOICE.TYPE.equal((byte) 1), 1)
		   .when(INVOICE.TYPE.equal((byte) 2), 0)
		   .when(INVOICE.TYPE.equal((byte) 3), 0);

	}
	
	public static Condition getWhere(InvoiceConsoleParams params) {
		
		Condition condition = INVOICE.DOMAIN.equal( params.getDomain() );
		
		if (params.getActivity() != null) {
			if (AonMathUtils.isNegative(params.getActivity())) {
				// S�lo las comunes. Los "sin activdad".
				condition = condition.and( INVOICE.ACTIVITY.isNull());
			} else {
				condition = condition.and( INVOICE.ACTIVITY.eq( params.getActivity() ));
			}
		}
			
		if (params.getFromDate() != null){
			condition = condition.and( INVOICE.ISSUE_DATE.ge( AonDateUtils.toSql(params.getFromDate())));
		}
		if(params.getToDate() != null){
			condition = condition.and( INVOICE.ISSUE_DATE.le( AonDateUtils.toSql(params.getToDate())));
		}

		if(params.getActivity() != null){
			condition = condition.and( INVOICE.ACTIVITY.eq( params.getActivity()));
		}

		if (params.getRegistry()  != null && params.getRegistry().intValue() != 0 ) {
			condition = condition.and( INVOICE.REGISTRY.eq( params.getRegistry() ));
		}
		
		if (params.getAccrualRegime() != null) {
			condition = condition.and( INVOICE.VAT_ACCRUAL_PAYMENT.eq( AonEnumUtils.getByte(params.getAccrualRegime())));
		}
		
		if (params.getInvestment() != null) {
			condition = condition.and( INVOICE.INVESTMENT.eq( AonEnumUtils.getByte(params.getInvestment())));
		}
		
		if (params.getRectificationType() != null) {
			condition = condition.and( INVOICE.RECTIFICATION_TYPE.eq( params.getRectificationType().value()));
		}
		
		if (params.getService() != null) {
			if ( params.getService().booleanValue() ) {
				condition = condition.and( INVOICE.SERVICE.eq((byte)1).or( INVOICE.TYPE.eq( InvoiceType.EXPENSES.value())));
			} else {
				condition = condition.and( INVOICE.SERVICE.ne((byte)1).and( INVOICE.TYPE.ne( InvoiceType.EXPENSES.value())));
			}
		}
		
		if (params.getOutput() != null) {
			if (params.getOutput().booleanValue()) {
				condition = condition.and( INVOICE.TYPE.eq( InvoiceType.SALES.value() ));
			} else {
				condition = condition.and( INVOICE.TYPE.in( InvoiceType.EXPENSES.value(), InvoiceType.PURCHASE.value() ));
			}
		}
			
		if (params.getVatSummaryType() != null) {
			if (params.getVatSummaryType() == VatSummaryType.NATIONAL) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.NATIONAL.value() ));
				condition = condition.and( INVOICE.WITHHOLDING_FARMER.eq( AonEnumUtils.getByte(false)));
			} else if (params.getVatSummaryType() == VatSummaryType.SURCHARGE){	
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.NATIONAL.value() ));
				condition = condition.and( INVOICE.SURCHARGE.eq( AonEnumUtils.getByte(true)));
			} else if (params.getVatSummaryType() == VatSummaryType.FARMER) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.NATIONAL.value() ));
				condition = condition.and( INVOICE.WITHHOLDING_FARMER.eq( AonEnumUtils.getByte(true)));
			} else if (params.getVatSummaryType() == VatSummaryType.INTRACOMMUNITY) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.INTRACOMMUNITY.value() ));
			} else if (params.getVatSummaryType() == VatSummaryType.EXTRACOMMUNITY) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.EXTRACOMMUNITY.value() ));
			} else if (params.getVatSummaryType() == VatSummaryType.CAN_CEU_MEL) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.CAN_CEU_MEL.value() ));
			} else if (params.getVatSummaryType() == VatSummaryType.OTHER_ISP) {
				condition = condition.and( INVOICE.TRANSACTION.eq( InvoiceTransactionType.OTHER_ISP.value() ));
			}
		}
		return condition;
	}
	
	private static OrderField<?>[] getOrderBy(InvoiceConsoleParams params) {
		// TODO implement order field in params and ask user.
		return new OrderField<?>[] {
			 getOrderedType()
			,INVOICE.TYPE
			,INVOICE.ISSUE_DATE.desc()
			,INVOICE.REFERENCE_CODE
		};
	}
	
}
