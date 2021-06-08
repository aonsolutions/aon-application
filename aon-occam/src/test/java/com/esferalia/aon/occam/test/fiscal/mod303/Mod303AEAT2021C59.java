package com.esferalia.aon.occam.test.fiscal.mod303;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.IsoFields;
import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod303DAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.occam.test.faker.InvoiceFaker.InvoiceFakerParams;

public class Mod303AEAT2021C59 extends AbstractOccamTest {
	
	private static int YEAR = 2021;
	
	@Test
	public void test() {
		Date date = AonRandom.getRandomYearDay( YEAR );
		boolean monthly = AonRandom.gt(70);
		
		LocalDate myLocal = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		AonConfiguration config = ConfigurationDAO.getConfiguration(ctx); 
		InvoiceFakerParams params = new InvoiceFakerParams(ctx, config)
				.setIssueDate(date); 
		
		ctx.transaction(conf -> {
			int times = AonRandom.number(1, 25);
			for (int i = 0; i < times; i++) {
				InvoiceDAO.insert(ctx, InvoiceFaker.getSalesCanCeuService(params));
				InvoiceDAO.insert(ctx, InvoiceFaker.getSalesCanCeu(params));
			}

			Mod303 mod303 = new Mod303();
			mod303.setDomain(ctx.getDomainId());
			mod303.setAdministration(Administration.COMMON_TERRITORY);
			mod303.setYear(YEAR);
			mod303.setPeriod(monthly
					?Period.getMonthlyPeriod(myLocal.getMonthValue() - 1)
							:Period.getQuarterlyPeriod(myLocal.get(IsoFields.QUARTER_OF_YEAR)));
			mod303 = Mod303DAO.initializeMod303(ctx, mod303);
			mod303 = Mod303DAO.createMod303(ctx, mod303);
			Mod303DAO.save(ctx, mod303);
//			Mod303 mod = null; 
//			Mod303Declaration dec = Mod303Declaration.getInstance(mod);
		});
		
	}

}

