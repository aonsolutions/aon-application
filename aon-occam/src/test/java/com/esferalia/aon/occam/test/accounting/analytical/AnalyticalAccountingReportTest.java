package com.esferalia.aon.occam.test.accounting.analytical;

import static com.esferalia.aon.occam.test.OccamAssertions.assertNotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountOperatingAccount;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalColumn;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalReport;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalStatement;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.impl.jooq.dao.AnalyticalAccountingDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.util.AonConsoleUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class AnalyticalAccountingReportTest extends AbstractOccamTest {

	private static final int LINE_LENGTH = 170;
	
	private static final String pipe() {
		return AonConsoleUtils.blueBright(" | ");
	}
	private static final String line() {
		return AonConsoleUtils.blueBright(AonStringUtils.repeat( "-",LINE_LENGTH)) + pipe()+ "\n";
	}
	  
	public static final DecimalFormat FMT = new DecimalFormat("#,##0.00;(#,##0.00)");
	
	private static File FILE = new File("/home/ecastellano/TRABAJO/analytical/report.txt");
	
	@Test
	public void testGet() throws IOException {
		AccountPeriod period = ACCOUNTING.getPeriodByYear(ctx,  2020);
		assertNotNull(period);
		AccountingReportParams params = new AccountingReportParams();
		params.setPeriod(period.getId());
		params.setPreviousPeriods(0);
		params.setPercentsEnabled(false);
		params.setByMonth(false);

		try (FileWriter writer = new FileWriter( FILE )) {
			
			AccountingAnalyticalReport report = AnalyticalAccountingDAO.analyticalReport(ctx, params);
			writer.write("\n");
			writer.write(line());
			writer.write(AonConsoleUtils.blueBold(AonStringUtils.center( "CUENTA DE EXPLOTACION ANALITICA",LINE_LENGTH)) + pipe());
			writer.write("\n");
			writer.write(line());
			writer.write(
					AonStringUtils.rightPad("CUENTA", 10) + pipe() + 
					AonStringUtils.rightPad("DESCRIPCION", 55) + pipe() 
			);
			
			for (AccountingAnalyticalColumn column: report.getColumns() ) {
				writer.write(
					(column.isTotalColumn()?"":(AonStringUtils.leftPad( FMT.format(column.getPercent()) + "%", 7)) + pipe() ) + 
					AonStringUtils.center((column.getName() + (column.isMain()?"*":"")), 15) + pipe());
			}
			writer.write("\n");
			writer.write(line());
			for (AccountOperatingAccount account : report.getAccounts() ) {
				String code = account.getType().isCalculated()?"":account.getCode();
				writer.write(
					AonStringUtils.rightPad(code, 10) + pipe() +
					(account.getType().isCalculated()
						?AonConsoleUtils.blackBold( AonStringUtils.leftPad(account.getDescription(), 55))
						:AonConsoleUtils.blackBright( AonStringUtils.rightPad(account.getDescription(), 55))
					) + pipe()
				);
				for (AccountingAnalyticalColumn column : report.getColumns() ) {
					AccountingAnalyticalStatement bal = report.get(account.getCode(), column);
					if (column.isTotalColumn()) {
						double value = (bal == null)?0:bal.getBalance();
						writer.write(
							AonStringUtils.leftPad(FMT.format(value),15) + pipe()
						);
					} else {
						if (account.getType().isCalculated()) {
							double value = (bal == null)?0:bal.getBalance();
							writer.write(AonStringUtils.repeat(" ", 7) + pipe() +
								AonConsoleUtils.blackBold( AonStringUtils.leftPad(FMT.format(value),15))
								+ pipe());
						} else {
							double percent = (bal == null)?0:bal.getPercent();
							double value = (bal == null)?0:bal.getAmount();
							writer.write(
								AonStringUtils.leftPad( (FMT.format(percent) + "%"), 7) + pipe() +
								AonStringUtils.leftPad(FMT.format(value),15) + pipe()
								);
						}
						
					}
				}
				writer.write("\n");
			}
			writer.write(line());
			writer.flush();
		}
		
//		
//		Analytical analytical = AnalyticalAccountingDAO.get(ctx);
//		assertEquals(analytical.getName(), NAME);
//		assertNotNull(analytical.getCostCenters());
//		assertEquals(analytical.getCostCenters().size(), COST_CENTERS.length);
//		for (int i = 0; i < COST_CENTERS.length; i++) {
//			AnalyticalCostCenter cc = analytical.getCostCenters().get(COST_CENTERS[i]); 
//			assertEquals(cc.getName(), COST_CENTERS[i]);	
//			assertEquals(cc.getPercent(), PERCENTS[i],0);
//			assertNotNull(cc.getAccounts());
//			assertEquals(cc.getAccounts().size(), ACCOUNTS.length);
//			for (int x = 0; x < ACCOUNTS.length; x++) {
//				AnalyticalAccount account = cc.getAccounts().get(ACCOUNTS[x].getCode());
//				assertEquals(account.getCode(), ACCOUNTS[x].getCode());	
//				assertEquals(account.getPercent(), PERCENTS[i],0);
//			}
//		}
	}
	
}
