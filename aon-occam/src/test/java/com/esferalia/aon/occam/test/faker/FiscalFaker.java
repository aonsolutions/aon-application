package com.esferalia.aon.occam.test.faker;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.github.javafaker.Faker;

public class FiscalFaker {
	private static Faker faker = new Faker(new Locale("es"));

	public static class FiscalFakerParams {
		private AONContext ctx;
		private Occam occam;
		private Date issueDate;
		private Administration administration;
		private boolean monthly;
		
		public FiscalFakerParams(AONContext ctx, Occam occam) {
			this.ctx = ctx;
			this.occam = occam;
		}
		
		public AONContext getCtx() {
			return ctx;
		}
		public Occam getOccam() {
			return occam;
		}
		public Date getIssueDate() {
			return issueDate;
		}
		public FiscalFakerParams setIssueDate(Date issueDate) {
			this.issueDate = issueDate;
			return this;
		}
		public boolean isMonthly() {
			return monthly;
		}
		public FiscalFakerParams setMonthly(boolean monthly) {
			this.monthly = monthly;
			return this;
		}
		
		public Administration getAdministration() {
			return administration;
		}
		public FiscalFakerParams setAdministration(Administration administration) {
			this.administration = administration;
			return this;
		}
		
	}
	
	public static Period getRandomPeriod() {
    	return Period.values()[faker.random().nextInt(Period.values().length)];
	}
	public static Period getRandomMonthPeriod() {
    	return Period.getMonthlyPeriod(faker.random().nextInt(11));
	}
	public static Period getRandomQuarterPeriod() {
		return Period.getQuarterlyPeriod(faker.random().nextInt(11));
	}
	public static Administration getRandomAdministration() {
    	return Administration.values()[faker.random().nextInt(Administration.values().length - 1)];
	}
	
	public static Mod111 getMod111( FiscalFakerParams params) {
		Mod111 mod111 = new Mod111();
		mod111.setDomain(params.getOccam().getDomain());
		MODEL111.initializeMod111( params.getOccam(), mod111);
		mod111.setYear(AonDateUtils.getYear(params.getIssueDate()));
		mod111.setPeriod( params.isMonthly()
			? Period.getMonthlyPeriod(AonDateUtils.getMonth(params.getIssueDate()))
			: Period.getQuarterlyPeriod(AonDateUtils.getMonth(params.getIssueDate())) );
		mod111.setAdministration(Objects.requireNonNullElse(params.getAdministration(), getRandomAdministration()));
		MODEL111.createMod111(params.getOccam(), mod111);
		return mod111;
	}
	
	public static Mod303 getMod303( FiscalFakerParams params) {
		Mod303 mod303 = new Mod303();
		mod303.setDomain(params.getOccam().getDomain());
		MODEL303.initialize( params.getOccam(), mod303);
		mod303.setYear(AonDateUtils.getYear(params.getIssueDate()));
		mod303.setPeriod( params.isMonthly()
			? Period.getMonthlyPeriod(AonDateUtils.getMonth(params.getIssueDate()))
			: Period.getQuarterlyPeriod(AonDateUtils.getMonth(params.getIssueDate())) );
		mod303.setAdministration(Objects.requireNonNullElse(params.getAdministration(), getRandomAdministration()));
		MODEL303.create(params.getOccam(), mod303);
		return mod303;
	}
	
}
