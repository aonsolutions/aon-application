package com.code.aon.accounting.mvel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.accounting.Period;
import com.code.aon.accounting.freemarker.Freemarker;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class BalanceTest {

	public static void main(String[] args) throws Throwable {
		//System.out.println("START " + BalanceSheet.PYG_ABR);
		//testBalance (BalanceSheet.PYG_ABR);
		//System.out.println("END");
		
		//System.out.println("START " + BalanceSheet.BAL_ABR);
		//testBalance (BalanceSheet.BAL_ABR);
		//System.out.println("END");
		
		System.out.println("START " + BalanceSheet.BAL_NOR);
		testBalance (BalanceSheet.BAL_NOR);
		System.out.println("END");
}
	
	public static void testBalance(BalanceSheet sheet) throws Throwable { 
//		SummaryProviderParameters params = new SummaryProviderParameters();
//		params.setExcludeClosingEntry(true);
//		params.setExcludeOperatingEntry(true);
//		IManagerBean bean = BeanManager.getManagerBean(Period.class);
//		Criteria c = new Criteria();
//		c.addEqualExpression(bean.getFieldName(IEntityAlias.PERIOD_NAME), "2012");
//		List<ITransferObject> periods = bean.getList(c);   
//		params.setPeriod( (Period) periods.get(0) );
//		
//		BalanceMVELContext ctx = new BalanceMVELContext(params);
//		BalanceMVELContext previousCtx = null;
//		boolean mustShowPreviousYear = false;
//		if (params.isPreviousPeriodVisible()) {
//			SummaryProviderParameters previousParams = new SummaryProviderParameters();
//			previousParams = params.getPreviousPeriodParameters();
//			if (previousParams != null) {
//				previousCtx = new BalanceMVELContext(previousParams);
//				mustShowPreviousYear = true;
//			} else {
//				params.setPreviousPeriodVisible(false);
//			}
//		}
//		for (BalanceKey aee : sheet.getKeys()) {
//			ctx.put(aee.getCode(), new BalanceItem(aee) );
//			if (previousCtx != null) {
//				previousCtx.put(aee.getCode(), new BalanceItem(aee) );
//			}
//		}
//		List<BalanceMVELContext> list = new LinkedList<BalanceMVELContext>();
//		list.add(ctx);
//		if (mustShowPreviousYear) {
//			list.add(previousCtx);
//		}
//		BalanceContext balanceContext = new BalanceContext( list );
//		balanceContext.intilizeContextMap( sheet );
//		
//		Freemarker aef = new Freemarker( balanceContext );
		File tempFile = new File("/tmp/pyg.xml");
//		FileOutputStream fos = new FileOutputStream(tempFile);
//		OutputStreamWriter writer = new OutputStreamWriter(fos);
//		aef.process("xml_report.ftl",writer);
//		writer.flush();
//		writer.close();
		
		File outputFile = new File("/tmp/balance.html");
		OutputStream output = new FileOutputStream(outputFile);
		BalanceTransformer viewer = new BalanceTransformer();
		FileInputStream fis =  new FileInputStream(tempFile);
		//viewer.transformXML2HTML( fis, output, sheet );
		viewer.transformXML2AonComponents( fis, output, sheet );
		output.flush();
		output.close();
		fis.close();
		
	}

}
