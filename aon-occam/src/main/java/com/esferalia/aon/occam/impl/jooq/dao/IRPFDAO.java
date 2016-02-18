package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;

import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class IRPFDAO extends FiscalModelDAO {
	
	// -------------------------------------------------------------------- STREAM FUNCTIONS
	public static Stream<IrpfBreakdown> getInvoiceIrpfBreakdown(final AONContext ctx, final FiscalModel fm) {
		java.sql.Date dateFrom = AonDateUtils.toSql( FiscalUtils.getPeriodStart(fm));	
		java.sql.Date dateTo = AonDateUtils.toSql( FiscalUtils.getPeriodEnd(fm));
		return 	ctx.getDslContext()
			.select(INVOICE.ID
					,INVOICE.TYPE,INVOICE.SERIES,INVOICE.NUMBER,INVOICE.REFERENCE_CODE
					,INVOICE.ISSUE_DATE,INVOICE.TAX_DATE
					,INVOICE.RDOCUMENT,INVOICE.RNAME
					,INVOICE_TAX.WITHHOLDING_TYPE,ENTERPRISE_ACTIVITY.RETENTION_REGIME
					,INVOICE_TAX.BASE,INVOICE_TAX.PERCENTAGE,INVOICE_TAX.QUOTA)
				.from(INVOICE)
				.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
				.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
				.leftOuterJoin(ENTERPRISE_ACTIVITY).on(INVOICE.ACTIVITY.equal(ENTERPRISE_ACTIVITY.ID))
				.where(INVOICE.DOMAIN.equal(fm.getDomain()))
					.and(INVOICE.TAX_DATE.between(dateFrom,dateTo))
					.and(INVOICE.TYPE.notEqual(InvoiceType.SALES.value() ))
					.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.RETENTION.value()))
				.orderBy(INVOICE.RDOCUMENT,INVOICE.ISSUE_DATE)
				.fetch()
				.stream()
				.map( new IrpfInvoiceBreakdown() );
	}

	public static class IrpfInvoiceBreakdown implements Function<Record, IrpfBreakdown> {
		@Override
		public IrpfBreakdown apply(Record rec) {
			double base = rec.getValue(INVOICE_TAX.BASE);
			double percent = rec.getValue(INVOICE_TAX.PERCENTAGE);
			double quota = rec.getValue(INVOICE_TAX.QUOTA);
			if (AonMathUtils.isZero(quota)) {
				quota =  AonMathUtils.round(base * percent / 100);
			}
			Byte regime = rec.getValue(ENTERPRISE_ACTIVITY.RETENTION_REGIME);
			return new IrpfBreakdown()
					.setFromSalary(false)
					.setInvoice(rec.getValue(INVOICE.ID))
					.setInvoiceType(AonEnumUtils.enumValue(InvoiceType.class,rec.getValue(INVOICE.TYPE)))
					.setSeries(rec.getValue(INVOICE.SERIES))
					.setNumber(rec.getValue(INVOICE.NUMBER))
					.setReferenceCode(rec.getValue(INVOICE.REFERENCE_CODE))
					.setDocument(rec.getValue(INVOICE.RDOCUMENT))
					.setName(rec.getValue(INVOICE.RNAME))
					.setIssueDate(rec.getValue(INVOICE.ISSUE_DATE))
					.setTaxDate(rec.getValue(INVOICE.TAX_DATE))
					.setWithholdingType(AonEnumUtils.enumValue(WithholdingType.class,rec.getValue(INVOICE_TAX.WITHHOLDING_TYPE)))
					.setIRPFRegime(regime == null? null : IRPFRegime.values()[regime])
					.setBase(base)
					.setPercent(percent)
					.setQuota(quota)
					;
		}
	}
}

