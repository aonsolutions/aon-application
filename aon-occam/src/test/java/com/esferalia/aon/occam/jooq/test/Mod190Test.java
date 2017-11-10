package com.esferalia.aon.occam.jooq.test;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.Record4;
import org.junit.Test;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;


public class Mod190Test {

	private static String DOMAIN_NAME = "serviciosdac.ecastellano.dev";
	private static int DOMAIN_ID = 3626;
	private static String USER = "admin";
	private static int YEAR = 2015;

	@Test
	public void testDelete() throws IOException {
		FISCAL.deleteMod190(DOMAIN_NAME, DOMAIN_ID, USER, getYearMod190());		
	}

	@Test
	public void testInsert() throws IOException {
		Mod190 mod190 = FISCAL.initializeMod190(DOMAIN_NAME, DOMAIN_ID, USER, YEAR);
		FISCAL.saveMod190(DOMAIN_NAME, DOMAIN_ID, USER, mod190);
	}
		
	@Test
	public void testSalary() throws IOException {
		AONContext ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, USER );
		java.sql.Date from = AonDateUtils.toSql( AonDateUtils.getYearFirstDay(YEAR));	
		java.sql.Date to   = AonDateUtils.toSql( AonDateUtils.getYearLastDay(YEAR));
		
		double perception = 0.0;
		double perceptionInkind = 0.0;
		double retention = 0.0;
		double retentionInkind = 0.0;
		Mod190 mod190 = getYearMod190();
		for (Mod190Detail detail :  mod190.getDetails()) {
			double salperception = 0.0;
			double salperceptionInkind = 0.0;
			double salretention = 0.0;
			double salretentionInkind = 0.0;
			
			LinkedList<Record4<Double,Double,Double,Double>> list = ctx.getDslContext()
				.select(SALARY.IRPF_BASE,SALARY.MONEY_IRPF_BASE,SALARY.INKIND_IRPF_BASE,SALARY.TOTAL_IRPF)
				.from(SALARY)
				.join(CONTRACT).on(SALARY.CONTRACT.equal(CONTRACT.ID))
				.join(WORKPLACE).on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
				.join(PERSON).on(PERSON.REGISTRY.equal(CONTRACT.PERSON))
				.where(SALARY.ISSUE_DATE.between(AonDateUtils.toSql(from),AonDateUtils.toSql(to)))
				.and(WORKPLACE.ENTERPRISE.equal(mod190.getEnterprise()))
				.and(WORKPLACE.ECONOMICAGREEMENT.equal(mod190.getAdministration().getValue()))
				.and(SALARY.EMPLOYEE_DOCUMENT.equal(detail.getDocument()))
				.fetch()
				.stream()
				.collect(Collectors.toCollection(LinkedList::new));
			
			System.out.println( " ------------------- " + detail.getDocument() + " - " + detail.getName());			
			for (Record4<Double,Double,Double,Double> rec : list ) { 
				double base = rec.getValue(SALARY.IRPF_BASE);
				double quota = rec.getValue(SALARY.TOTAL_IRPF);
				double moneyBase = 0.0;
				double inKindBase = rec.getValue(SALARY.INKIND_IRPF_BASE);
				double moneyQuota = 0.0;
				double inKindQuota = 0.0;
				if (AonMathUtils.isNotZero(inKindBase)) {
					moneyBase = rec.getValue(SALARY.MONEY_IRPF_BASE);
					moneyQuota = AonMathUtils.round( moneyBase * quota  / base ); 	
					inKindQuota = AonMathUtils.round( quota - moneyQuota);
				} else {
					moneyBase = base;
					moneyQuota = quota;
				}
//				System.out.println(base +" / " + moneyBase + " / " +  inKindBase + " / " + moneyQuota + " / " + inKindQuota  + " ---> " + quota);	
				salperception = salperception + moneyBase;
				salperceptionInkind = salperceptionInkind + inKindBase;
				
				salretention = salretention + moneyQuota;
				salretentionInkind = salretentionInkind + inKindQuota;
			};
			
			System.out.println( " ------------------- ");
			System.out.println( "MN BASE  " + AonMathUtils.round(salperception) + " \t " + detail.getPerception()  + " --> " + (AonMathUtils.round(salperception) - detail.getPerception()) );				
			System.out.println( "IK BASE  " + AonMathUtils.round(salperceptionInkind) + " \t " + detail.getInKindPerception() + " --> " + ( AonMathUtils.round(salperceptionInkind) - detail.getInKindPerception() ));				
			System.out.println( "MN QUOTA " + AonMathUtils.round(salretention)  + " \t " + detail.getRetention()+ " --> " + (  AonMathUtils.round(salretention)  - detail.getRetention()  ));
			System.out.println( "IK QUOTA " + AonMathUtils.round(salretentionInkind) + " \t " + detail.getInKindDeposit()+ " --> " + (  AonMathUtils.round(salretentionInkind) - detail.getInKindDeposit()  ));
			System.out.println( );
			
			perception = perception + detail.getPerception();
			perceptionInkind = perceptionInkind + detail.getInKindPerception();
			
			retention = retention + detail.getRetention();
			retentionInkind = retentionInkind + detail.getInKindDeposit();
		}
	}

	/*
	@Test
	public void testSalary() throws IOException {
		Mod180 mod180 = AON.getMod180(DOMAIN_NAME, DOMAIN_ID, USER, 36);
		mod180.getId(); // Not NullPointer
	}
	*/

	private Mod190 getYearMod190() {
		List<Mod190> mod190s = FISCAL.getMod190s(DOMAIN_NAME, DOMAIN_ID, USER);
		for (Mod190 mod190 : mod190s) {
			if (mod190.getYear() == YEAR) {
				return mod190;		
			}
		}
		return null;
	}
	
}
