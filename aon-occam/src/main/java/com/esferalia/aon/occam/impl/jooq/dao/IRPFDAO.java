package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.util.Date;
import java.util.LinkedList;
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

	// -------------------------------------------------------------------- SALARY
	public static Stream<IrpfBreakdown> getSalaryIrpfBreakdown(final AONContext ctx, final FiscalModel fm) {
		return getSalaryIrpfBreakdown(ctx, fm, false);	
	}
	public static Stream<IrpfBreakdown> getSalaryDiffIrpfBreakdown(final AONContext ctx, final FiscalModel fm) {
		return getSalaryIrpfBreakdown(ctx, fm, true);	
	}

	private static Stream<IrpfBreakdown> getSalaryIrpfBreakdown(final AONContext ctx, final FiscalModel fm, final boolean diff) {
		java.sql.Date dateFrom = diff
				?AonDateUtils.toSql( AonDateUtils.getYearFirstDay(fm.getYear()))
				:AonDateUtils.toSql( FiscalUtils.getPeriodStart(fm));	
		java.sql.Date dateTo = AonDateUtils.toSql( FiscalUtils.getPeriodEnd(fm));
		final LinkedList<IrpfBreakdown> list = new LinkedList<IrpfBreakdown>();
		ctx.getDslContext()
			.select(SALARY.ISSUE_DATE
				,SALARY.EMPLOYEE_DOCUMENT,SALARY.EMPLOYEE_NAME
				,SALARY.IRPF_BASE, SALARY.MONEY_IRPF_BASE
				,SALARY.INKIND_IRPF_BASE,SALARY.TOTAL_IRPF)
			.from(SALARY)
			.join(CONTRACT).on(SALARY.CONTRACT.equal(CONTRACT.ID))
			.join(WORKPLACE).on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
			.where(SALARY.DOMAIN.equal(fm.getDomain()))
				.and(SALARY.ISSUE_DATE.between(dateFrom,dateTo))
				.and(WORKPLACE.ECONOMICAGREEMENT.equal(fm.getAdministration().getValue()))
			.fetch()
			.stream()
			.forEach( rec -> {
				Date issueDate = rec.field(SALARY.ISSUE_DATE) != null ? rec.getValue(SALARY.ISSUE_DATE): null;
				IrpfBreakdown br = new IrpfBreakdown()
						.setFromSalary(true)
						.setDocument(rec.getValue(SALARY.EMPLOYEE_DOCUMENT))
						.setName(rec.field(SALARY.EMPLOYEE_NAME) != null ? rec.getValue(SALARY.EMPLOYEE_NAME) : null)
						.setIssueDate(issueDate)
						.setTaxDate(issueDate)
						.setInsidePeriod( FiscalUtils.isInPeriodRange(fm, issueDate) );
				double base = rec.getValue(SALARY.IRPF_BASE);
				if (AonMathUtils.isNotZero(base)) {
					double quota = rec.getValue(SALARY.TOTAL_IRPF);
					double inKindBase = rec.getValue(SALARY.INKIND_IRPF_BASE);
					if (AonMathUtils.isZero(inKindBase)) {
						br.setInKind(false)
						.setBase(AonMathUtils.round( base))
						.setQuota(AonMathUtils.round( quota));
						list.add(br);
					} else {	
						double moneyBase = base;
						double moneyQuota = quota;
						double inKindQuota = 0;
						moneyBase = rec.getValue(SALARY.MONEY_IRPF_BASE);
						moneyQuota = AonMathUtils.round( moneyBase * quota  / base ); 	
						inKindQuota = AonMathUtils.round( quota - moneyQuota);
						br.setInKind(true)
						  .setBase(AonMathUtils.round( inKindBase))
						  .setQuota(inKindQuota);
						list.add(br);
						if (AonMathUtils.isNotZero(moneyBase) || AonMathUtils.isNotZero(moneyQuota)) {
							br = new IrpfBreakdown()
									.setFromSalary(true)
									.setDocument(rec.getValue(SALARY.EMPLOYEE_DOCUMENT))
									.setName(rec.field(SALARY.EMPLOYEE_NAME) != null ? rec.getValue(SALARY.EMPLOYEE_NAME) : null)
									.setIssueDate(issueDate)
									.setTaxDate(issueDate)
									.setInsidePeriod( FiscalUtils.isInPeriodRange(fm, issueDate) )
									.setInKind(false)
									.setBase(AonMathUtils.round( moneyBase))
									.setQuota(moneyQuota);
							list.add(br);
						}
					}
				}

			});
		return list.stream();
		
	}
	
//	private static class IrpfSalaryBreakdown implements Function<Record, IrpfBreakdown> {
//		@Override
//		public IrpfBreakdown apply(Record rec) {
//			IrpfBreakdown br = new IrpfBreakdown()
//					.setFromSalary(true)
//					.setDocument(rec.getValue(SALARY.EMPLOYEE_DOCUMENT))
//					.setName(rec.field(SALARY.EMPLOYEE_NAME) != null ? rec.getValue(SALARY.EMPLOYEE_NAME) : null)
//					.setIssueDate(rec.field(SALARY.ISSUE_DATE) != null ? rec.getValue(SALARY.ISSUE_DATE): null); 
//
//			
//			double base = rec.getValue(SALARY.IRPF_BASE);
//			double quota = rec.getValue(SALARY.TOTAL_IRPF);
//			double inKindBase = rec.getValue(SALARY.INKIND_IRPF_BASE);
//			if (inKindBase != 0) {
//				double moneyBase = base;
//				double moneyQuota = quota;
//				double inKindQuota = 0;
//				moneyBase = rec.getValue(SALARY.MONEY_IRPF_BASE);
//				moneyQuota = AonMathUtils.round( moneyBase * quota  / base ); 	
//				inKindQuota = AonMathUtils.round( quota - moneyQuota);
//				inKindBase = AonMathUtils.round( inKindBase );
//				br.setInKind(true)
//				  .setBase(inKindBase)
//				  .setQuota(inKindQuota);
//			} else {
//				br.setInKind(false)
//				  .setBase(AonMathUtils
//				  .round( base))
//				  .setQuota(AonMathUtils.round( quota));
//			}
//			return br;
//		}
//	}

	// -------------------------------------------------------------------- STREAM FUNCTIONS

	// -------------------------------------------------------------------- INVOICE

	public static Stream<IrpfBreakdown> getInvoiceIrpfBreakdown(final AONContext ctx, final FiscalModel fm) {
		return getInvoiceIrpfBreakdown(ctx, fm, false);
	}
	
	public static Stream<IrpfBreakdown> getInvoiceDiffIrpfBreakdown(final AONContext ctx, final  FiscalModel fm) {
		return getInvoiceIrpfBreakdown(ctx, fm, true);	
	}

	private static Stream<IrpfBreakdown> getInvoiceIrpfBreakdown(final AONContext ctx, final FiscalModel fm, final boolean diff) {
		java.sql.Date dateFrom = diff
				?AonDateUtils.toSql( AonDateUtils.getYearFirstDay(fm.getYear()))
				:AonDateUtils.toSql( FiscalUtils.getPeriodStart(fm));
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
				.orderBy(INVOICE.ISSUE_DATE,INVOICE.ID,INVOICE.RDOCUMENT)
				.fetch()
				.stream()
				.map( new IrpfInvoiceBreakdown() )
				.peek( br -> br.setInsidePeriod( FiscalUtils.isInPeriodRange(fm, br.getTaxDate() )));
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

