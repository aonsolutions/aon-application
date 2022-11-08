package es.aonsolutions.aio.test.invoice;

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
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import es.aonsolutions.aio.test.AonHibernateTestBasic;

class InvoiceTaxRoundedTest extends AonHibernateTestBasic {
	
	private Date today = new Date();
	
	@Test
	void testOutputVAT() throws Exception {
		DateRange range = DateRange.of(AonDateUtils.getYearFirstDay(today), AonDateUtils.getYearLastDay(today));
		Mod303 mod303 = getMod303(range.getTo());
		
		double vatDAOQuota = VATDAO.getVatBreakdown(ctx, mod303)
			.filter( vt -> vt.isSales() )
			.mapToDouble(vt -> vt.getQuota() + (vt.isSurcharge()?vt.getSurchargeQuota():0.0) )	
			.sum();
		System.out.println( AonStringUtils.rightPad( "VatDAO Quota (VENTAS)",100,"-") 
				+ "> " +  vatDAOQuota);
		
		Account outputVatAccount = AccountDAO.getAccounts(ctx, p -> p.getCodeProperty().eq("477000000") )
			.findFirst()
			.orElse(null);
		
		AccountingReportParams params = new AccountingReportParams()
			.setFromDate(range.getFrom())
			.setToDate(range.getTo())
			.setClosingEntriesExcluded(true)
			.setOperatingEntriesExcluded(true)
			.setAccount(outputVatAccount == null ? null : outputVatAccount)
		;
		
		AccountStatementDAO.balance(ctx, params, true)
			.forEach( b -> System.out.println(
					AonStringUtils.rightPad( 			
					
					outputVatAccount.getCode() 
					+ " - " + outputVatAccount.getDescription(),100,"-")
					
					+ "> " + b.getUnpaidBalance() ) );
		
		Mod303DAO.simulate(ctx, mod303);
		double amount = mod303.getAmount( Mod303Key.CT_C27  );
		System.out.println( 
				AonStringUtils.rightPad( "Mod303 Cuota devengada. Casilla [" 
					+ Mod303Key.CT_C27.getBoxCode()
					+ "] ("+ Mod303Key.CT_C27.getDescription() +")", 100, "-")
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
}
