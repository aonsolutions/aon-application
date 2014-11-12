package com.esferalia.aon.core.impl.jooq.test;


import org.junit.Test;

import com.esferalia.aon.core.api.AON;
import com.esferalia.aon.core.api.AONContext;
import com.esferalia.aon.core.api.model.SalaryAccountEntry;
import com.esferalia.aon.core.api.model.SalaryAccountEntry.SalaryAccountEntryLine;
import com.esferalia.aon.core.api.model.SalaryAccountEntry.SalaryAccountEntryLineType;
import com.esferalia.aon.core.commons.util.AonDateUtils;


public class SalaryAccountEntryTest {

	private static String DOMAIN_NAME = "alhymotion-mac.ecastellano.dev";
	private static int DOMAIN_ID = 3034;

	@Test
	public void testPeriodEmptyInitiaionDate() {
		AONContext ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID);
		
		SalaryAccountEntry sae = new SalaryAccountEntry();
		sae.setDate( AonDateUtils.getDate(2014, 0, 1));
		
		SalaryAccountEntryLine line = new SalaryAccountEntryLine();
		line.setType(SalaryAccountEntryLineType.SALARY);
		line.setAmount(1000);
		sae.addLine(line);
		line = new SalaryAccountEntryLine();
		line.setType(SalaryAccountEntryLineType.SALARY_IN_KIND);
		line.setAmount(2000);
		sae.addLine(line);
		line = new SalaryAccountEntryLine();
		line.setType(SalaryAccountEntryLineType.ALLOWANCE);
		line.setAmount(3000);
		sae.addLine(line);
		line = new SalaryAccountEntryLine();
		line.setType(SalaryAccountEntryLineType.COMPENSATION);
		line.setAmount(4000);
		sae.addLine(line);
		line = new SalaryAccountEntryLine();
		line.setType(SalaryAccountEntryLineType.COMPANY_SOC_INS);
		line.setAmount(5000);
		sae.addLine(line);
		line = new SalaryAccountEntryLine();
		line.setType(SalaryAccountEntryLineType.EMPLOYEE_SOC_INS);
		line.setAmount(6000);
		sae.addLine(line);
		line = new SalaryAccountEntryLine();
		line.setType(SalaryAccountEntryLineType.RETENTION);
		line.setAmount(7000);
		sae.addLine(line);
		line = new SalaryAccountEntryLine();
		line.setType(SalaryAccountEntryLineType.RETENTION_IN_KIND);
		line.setAmount(8000);
		sae.addLine(line);
		AON.insertSalaryEntry(ctx , sae);
	}
	
}
