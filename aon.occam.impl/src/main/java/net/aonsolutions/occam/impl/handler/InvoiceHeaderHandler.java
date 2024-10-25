package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Iae.IAE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static net.aonsolutions.occam.impl.handler.SellerHandler.REGISTRY_SELLER;

import java.util.Optional;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

import net.aonsolutions.occam.api.model.Filter.InvoiceFilter;
import net.aonsolutions.occam.api.model.InvoiceHeader;
import net.aonsolutions.occam.api.model.type.Country;
import net.aonsolutions.occam.api.model.type.DocumentType;
import net.aonsolutions.occam.api.model.type.InvoiceTransactionType;
import net.aonsolutions.occam.api.model.type.InvoiceType;
import net.aonsolutions.occam.api.model.type.RectificationType;
import net.aonsolutions.occam.api.model.type.SecurityLevel;
import net.aonsolutions.occam.impl.AONContext;
import net.aonsolutions.occam.impl.handler.ActivityHandler.ActivityFiller;
import net.aonsolutions.occam.impl.handler.SellerHandler.SellerFiller;

class InvoiceHeaderHandler {
	
	private InvoiceHeaderHandler() {
	}
	
	// -------------------------------------------------------
	// ------------------------------------------- [PROTECTED]
	// -------------------------------------------------------
	static Stream<InvoiceHeader> stream(AONContext ctx, int domain,InvoiceFilter filter) {
		return stream(ctx,domain,filter,0,Integer.MAX_VALUE);
	}
	
	static Stream<InvoiceHeader> stream(AONContext ctx, int domain,InvoiceFilter filter, int offset , int numberOfRows) {
		ctx.checkRead();
		return getInvoiceBaseSelect(ctx,domain)
			.and(InvoiceHandler.INVOICE_PROPERTIES.getCondition(filter))
			.orderBy(InvoiceHandler.ORDERED_TYPE,INVOICE.TYPE,INVOICE.ISSUE_DATE.desc(),INVOICE.REFERENCE_CODE)
			.limit(offset,numberOfRows)
			.fetch()
			.stream()
			.map(new InvoiceHeaderFiller());
	}

	static Optional<InvoiceHeader> get(AONContext ctx, int domain, Integer id) {
		ctx.checkRead();
		if (id == null) throw new AonCoreException( AonError.NULL_FILTER.getMessage());
		return getInvoiceBaseSelect(ctx,domain)
			.and(INVOICE.ID.eq(id))
			.fetch()
			.stream()
			.map( new InvoiceHeaderFiller() )
			.findFirst();
	}
	
	// -------------------------------------------------------
	// ------------------------------------------- [PROTECTED]
	// -------------------------------------------------------
	
	static class InvoiceHeaderFiller extends Filler<InvoiceHeader> {

		@Override
		public InvoiceHeader apply(Record r) {
			return build(r);
		}

	    static InvoiceHeader build(Record r) {
	        return build(r, INVOICE);
	    }
	     
	    static InvoiceHeader build(Record r, com.esferalia.aon.jooq.tables.Invoice inv) {
	    	if (isNull(r,inv.ID)) return null;
	    	return new InvoiceHeader()
				.setId(getValue(r,inv.ID))
				.setDomain(getValue(r,inv.DOMAIN))
				.setType(InvoiceType.value(getValue(r, inv.TYPE)).orElse(null))
				.setSeries(getValue(r,inv.SERIES))
				.setNumber(getValue(r,inv.NUMBER))
				.setReferenceCode(getValue(r,inv.REFERENCE_CODE))
				.setIssueDate(getValue(r,inv.ISSUE_DATE))
				.setTaxDate(getValue(r,inv.TAX_DATE))
				.setConfidential(SecurityLevel.confidential(getValue(r,inv.SECURITY_LEVEL)))
				.setRegistry( getValue(r,inv.REGISTRY))
				.setRegistryDocument(getValue(r,inv.RDOCUMENT))
				.setRegistryDocumentType(DocumentType.value(getValue(r, inv.RDOCUMENT_TYPE)).orElse(null))
				.setRegistryDocumentCountry(Country.value(getValue(r,inv.RDOCUMENT_COUNTRY)).orElse(null))
				.setRegistryName(getValue(r,inv.RNAME))
				.setScope(getValue(r,inv.SCOPE))
				.setActivity(ActivityFiller.build(r))	
				.setProject(getValue(r,inv.PROJECT))
				.setRectificationType(RectificationType.value(getValue(r, inv.RECTIFICATION_TYPE)).orElse(null))
				.setRectificationInvoiceId(getValue(r,inv.RECTIFICATION_INVOICE))
				.setTransaction(InvoiceTransactionType.value(getValue(r,inv.TRANSACTION)).orElse(null))
				.setRecorded(getBoolean(r,inv.STATUS))	
				.setSurcharge(getBoolean(r,inv.SURCHARGE))	
				.setWithholding(getBoolean(r,inv.WITHHOLDING))	
				.setWithholdingFarmer(getBoolean(r,inv.WITHHOLDING_FARMER))	
				.setVatAccrualPayment(getBoolean(r,inv.VAT_ACCRUAL_PAYMENT))	
				.setInvestment(getBoolean(r,inv.INVESTMENT))	
				.setService(getBoolean(r,inv.SERVICE))	
				.setTaxableBase(getDouble(r,inv.TAXABLE_BASE))	
				.setVatQuota(getDouble(r,inv.VAT_QUOTA))	
				.setRetentionQuota(getDouble(r,inv.RETENTION_QUOTA))	
				.setTotal(getDouble(r,inv.TOTAL))	
				.setComments(getValue(r,inv.COMMENTS))
				.setRemarks(getValue(r,inv.REMARKS))
				.setSeller(SellerFiller.build(r, REGISTRY_SELLER))
				.setCreationDate(getValue(r,inv.CREATION_DATE))
				.setCreationUser(getValue(r,inv.CREATION_USER))
				.setModificationDate(getValue(r,inv.MODIFICATION_DATE))
				.setModificationUser(getValue(r,inv.MODIFICATION_USER))
				.markAsClean()
			;
		}
	}
	
	// -----------------------------------------------------
	// ------------------------------------------- [PRIVATE]
	// -----------------------------------------------------

	private static SelectConditionStep<Record> getInvoiceBaseSelect(AONContext ctx, int domain) {
		return ctx.getDslContext()
			.select(
				 INVOICE.ID
				,INVOICE.DOMAIN
				,INVOICE.ACTIVITY
				,INVOICE.TYPE
				,INVOICE.SERIES
				,INVOICE.NUMBER
				,INVOICE.REFERENCE_CODE
				,INVOICE.TRANSACTION
				,INVOICE.ISSUE_DATE
				,INVOICE.TAX_DATE
				,INVOICE.REGISTRY
				,INVOICE.RDOCUMENT
				,INVOICE.RDOCUMENT_TYPE
				,INVOICE.RDOCUMENT_COUNTRY
				,INVOICE.RNAME
				,INVOICE.SECURITY_LEVEL
				,INVOICE.STATUS
				,INVOICE.RECTIFICATION_TYPE
				,INVOICE.RECTIFICATION_INVOICE
				,INVOICE.TOTAL
			)
			.select(
				 IAE.EPIGRAPH
				,ENTERPRISE_ACTIVITY.DESCRIPTION
				,InvoiceHandler.ORDERED_TYPE
			)
			.from(INVOICE)
			.leftOuterJoin(ENTERPRISE_ACTIVITY).on(INVOICE.ACTIVITY.eq(ENTERPRISE_ACTIVITY.ID))
			.leftOuterJoin(IAE).on(IAE.ID.equal(ENTERPRISE_ACTIVITY.IAE))
			.leftOuterJoin(CNAE2009).on(CNAE2009.ID.eq(ENTERPRISE_ACTIVITY.CNAE2009))
			.where(INVOICE.DOMAIN.eq(domain))
			;		
	}

	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	static Optional<InvoiceHeader> getRandom(AONContext ctx, int domain, InvoiceFilter filter) {
		return getInvoiceBaseSelect(ctx,domain)
			.and(InvoiceHandler.INVOICE_PROPERTIES.getCondition(filter))
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new InvoiceHeaderFiller())
			.findFirst();
	}
	
}




