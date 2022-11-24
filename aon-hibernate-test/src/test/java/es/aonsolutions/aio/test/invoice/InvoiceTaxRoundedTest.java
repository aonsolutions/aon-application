package es.aonsolutions.aio.test.invoice;

import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;
import static com.esferalia.aon.jooq.tables.AccountEntryInvoice.ACCOUNT_ENTRY_INVOICE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.DateRange;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AccountStatementDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303DAO;
import com.esferalia.aon.occam.impl.jooq.dao.vat.VATDAO;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import es.aonsolutions.aio.test.AonHibernateTestBasic;

class InvoiceTaxRoundedTest extends AonHibernateTestBasic {
	
	private Date today = new Date();
	
	@Test
	void testOutputVAT() throws Exception {
		DateRange range = DateRange.of(AonDateUtils.getYearFirstDay(today), AonDateUtils.getYearLastDay(today));
		Mod303 mod303 = getMod303(range.getTo());
		
		double salesVatDAOQuota = VATDAO.getVatBreakdown(ctx, mod303)
			.filter( vat -> 
			
			vat.isSales() || 
				(!vat.isVatSurchargeRegime()
				&& (vat.isOtherISPPurchase() || vat.isOtherISPExpenses() || vat.isExtracommunityExpenses()
					|| vat.isCanCeuMelExpenses() || (vat.isExtracommunityPurchase() && vat.isService())
					|| (vat.isCanCeuMelPurchase() && vat.isService())))
					)
			.mapToDouble(vt -> vt.getQuota() + (vt.isSurcharge()?vt.getSurchargeQuota():0.0) )	
			.sum();
		System.out.println( AonStringUtils.rightPad( "VatDAO Quota (VENTAS)",80,"-") 
				+ "> " +  salesVatDAOQuota);
		
		double vatDAOQuota = VATDAO.getVatBreakdown(ctx, mod303)
				.filter( vt -> !vt.isSales() )
				.mapToDouble(vt -> vt.getQuota() + (vt.isSurcharge()?vt.getSurchargeQuota():0.0) )	
				.sum();
		System.out.println( AonStringUtils.rightPad( "VatDAO Quota (NO VENTAS)",80,"-") 
					+ "> " +  vatDAOQuota);

		System.out.println( AonStringUtils.rightPad( "Resultado VatDAO VENTAS - (NO VENTAS)",80,"-") 
				+ "> " +  AonMathUtils.round(salesVatDAOQuota - vatDAOQuota));

		Account outputVatAccount = AccountDAO.getAccounts(ctx, p -> p.getCodeProperty().eq("477000000") )
			.findFirst()
			.orElse(null);
		
		AccountingReportParams outputVatParams = new AccountingReportParams()
			.setFromDate(range.getFrom())
			.setToDate(range.getTo())
			.setClosingEntriesExcluded(true)
			.setOperatingEntriesExcluded(true)
			.setAccount(outputVatAccount == null ? null : outputVatAccount)
		;
		Double outputVatAmount = AccountStatementDAO.balance(ctx, outputVatParams, true)
			.map( b -> b.getUnpaidBalance() )
			.findFirst()
			.orElse(0.0);
		System.out.println(AonStringUtils.rightPad(outputVatAccount.getCode() + " - " + outputVatAccount.getDescription(),80,"-") + "> " + outputVatAmount );
		
		Account inputVatAccount = AccountDAO.getAccounts(ctx, p -> p.getCodeProperty().eq("472000000") )
				.findFirst()
				.orElse(null);
		AccountingReportParams inputVatParams = new AccountingReportParams()
				.setFromDate(range.getFrom())
				.setToDate(range.getTo())
				.setClosingEntriesExcluded(true)
				.setOperatingEntriesExcluded(true)
				.setAccount(outputVatAccount == null ? null : inputVatAccount)
			;
		Double inputVatAmount = AccountStatementDAO.balance(ctx, inputVatParams, true)
				.map( b -> b.getDebitBalance() )
				.findFirst()
				.orElse(0.0);
		System.out.println(AonStringUtils.rightPad(inputVatAccount.getCode() + " - " + inputVatAccount.getDescription(),80,"-") + "> " + inputVatAmount );
		
		Double accountResult = AonMathUtils.round(outputVatAmount - inputVatAmount);
		System.out.println(AonStringUtils.rightPad("Resultado contable 477 - 472 ",80,"-") + "> " + accountResult );
				

		Mod303DAO.simulate(ctx, mod303);
		double amount = mod303.getAmount( Mod303Key.CT_C46  );
		System.out.println( 
				AonStringUtils.rightPad( "Mod303. Casilla [" 
					+ Mod303Key.CT_C46.getBoxCode()
					+ "] ("+ Mod303Key.CT_C46.getDescription() +")", 80, "-")
					+ "> " + amount);
	}
	
	public Mod303  getMod303(Date date) {
		Mod303 t = new Mod303();
		t.setDomain(getDomain());
		t.setYear(AonDateUtils.getYear(date));
		t.setPeriod(  Period.getQuarterlyPeriod(AonDateUtils.getMonth(date)) );
		t.setAdministration( Administration.COMMON_TERRITORY );
		t.setComplementary( false );
		t.setReplacement(false );
		t.setGenerateFromYearStart( true );
		return t; 
	}
	
//	@Test
	void testSQL() throws Exception {
		Account  acc472 = AccountDAO.get(ctx, "472000000");
		//Account  acc477 = AccountDAO.get(ctx, "477000000");
		ctx.getDslContext().select( ACCOUNT_ENTRY_DETAIL.DEBIT, ACCOUNT_ENTRY_DETAIL.CREDIT, ACCOUNT_ENTRY_INVOICE.INVOICE)
			.from(ACCOUNT_ENTRY_DETAIL)
			.innerJoin(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY.eq(ACCOUNT_ENTRY.ID))
			.innerJoin(ACCOUNT_ENTRY_INVOICE).on(ACCOUNT_ENTRY.ID.eq(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY))
			.where(ACCOUNT_ENTRY_DETAIL.ACCOUNT.eq(acc472.getId()))
			.fetch()
			.stream()
			.forEach(rec -> {
				Double debit = rec.getValue(ACCOUNT_ENTRY_DETAIL.DEBIT);
				Double credit = rec.getValue(ACCOUNT_ENTRY_DETAIL.CREDIT);
				double accountAmount = AonMathUtils.absRounded(debit - credit);
				Integer invoice = rec.getValue(ACCOUNT_ENTRY_INVOICE.INVOICE);
				
				MutableDouble q = new MutableDouble();
				MutableDouble sq = new MutableDouble();
				MutableDouble dq = new MutableDouble();
				
				ctx.getDslContext().select( INVOICE_TAX.QUOTA, INVOICE_TAX.SURCHARGE_QUOTA, INVOICE_TAX.DEDUCTIBLE_QUOTA)
					.from( INVOICE_TAX )
					.innerJoin(INVOICE_DETAIL).on(INVOICE_TAX.INVOICE_DETAIL.eq(INVOICE_DETAIL.ID))		
					.innerJoin(INVOICE).on(INVOICE_DETAIL.INVOICE.eq(INVOICE.ID))
					.where(INVOICE.ID.eq(invoice))
					.and(INVOICE_TAX.TAX_TYPE.eq((byte) 1))
					.forEach( it -> {
						q.add( it.getValue(INVOICE_TAX.QUOTA));
						sq.add( it.getValue(INVOICE_TAX.SURCHARGE_QUOTA));
						dq.add( it.getValue(INVOICE_TAX.DEDUCTIBLE_QUOTA));
					});
				// double invoiceAmount = AonMathUtils.round(q.getValue() + sq.getValue());
				double invoiceAmount = AonMathUtils.round(dq.getValue());
				if (AonMathUtils.notEquals(accountAmount, invoiceAmount) ) {
					System.out.print( accountAmount );
					System.out.print( " -- " + invoiceAmount);
					System.out.println( "  done");
				}
			});
			;
		
	}
	
//	@Test
	void testSQL2() throws Exception {
		int acc472 = 435;
		int acc477 = 464;
		ctx.getDslContext().select( INVOICE.ID )
			.from( INVOICE )
			.where(INVOICE.TYPE.ne( (byte) 3 ))
			.and(INVOICE.TYPE.ne( (byte) 1 ))
			.forEach( i -> {
				Integer invoice = i.getValue(INVOICE.ID);
				
				System.out.print( "Invoice ,: " +  invoice);
				
				MutableDouble q = new MutableDouble();
				MutableDouble sq = new MutableDouble();
				MutableDouble dq = new MutableDouble();

				ctx.getDslContext().select( INVOICE_TAX.QUOTA, INVOICE_TAX.SURCHARGE_QUOTA, INVOICE_TAX.DEDUCTIBLE_QUOTA)
					.from( INVOICE_TAX )
					.innerJoin(INVOICE_DETAIL).on(INVOICE_TAX.INVOICE_DETAIL.eq(INVOICE_DETAIL.ID))
					.where(INVOICE_DETAIL.INVOICE.eq(invoice))
					.and(INVOICE_TAX.TAX_TYPE.eq((byte) 1))
					.forEach( it -> {
						q.add( it.getValue(INVOICE_TAX.QUOTA));
						sq.add( it.getValue(INVOICE_TAX.SURCHARGE_QUOTA));
						dq.add( it.getValue(INVOICE_TAX.DEDUCTIBLE_QUOTA));
					});
				double invoiceAmount = AonMathUtils.round(q.getValue() + sq.getValue());
				System.out.print( " -- " + invoiceAmount);
				
				ctx.getDslContext().select( ACCOUNT_ENTRY_DETAIL.DEBIT, ACCOUNT_ENTRY_DETAIL.CREDIT)
					.from(ACCOUNT_ENTRY_INVOICE)
					.innerJoin(ACCOUNT_ENTRY).on(ACCOUNT_ENTRY_INVOICE.ACCOUNT_ENTRY.eq(ACCOUNT_ENTRY.ID))
					.innerJoin(ACCOUNT_ENTRY_DETAIL).on(ACCOUNT_ENTRY.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
					.where(ACCOUNT_ENTRY_INVOICE.INVOICE.eq(invoice))
					.and(ACCOUNT_ENTRY_DETAIL.ACCOUNT.eq(acc472))
					.fetch()
					.stream()
					.forEach(rec -> {
						Double debit = rec.getValue(ACCOUNT_ENTRY_DETAIL.DEBIT);
						Double credit = rec.getValue(ACCOUNT_ENTRY_DETAIL.CREDIT);
						double accountAmount = AonMathUtils.absRounded(debit - credit);
						System.out.print( " -- " + accountAmount);
						if (AonMathUtils.notEquals(accountAmount, invoiceAmount) ) {
							System.out.print( " ************** ");	
						}

					});
				System.out.println();
			});
	}
	
}
