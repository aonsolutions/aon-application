package com.esferalia.aon.occam.test.faker;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.fiscal.MODEL111;
import com.esferalia.aon.occam.api.fiscal.MODEL115;
import com.esferalia.aon.occam.api.fiscal.MODEL123;
import com.esferalia.aon.occam.api.fiscal.MODEL130;
import com.esferalia.aon.occam.api.fiscal.MODEL190;
import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.fiscal.MODEL349;
import com.esferalia.aon.occam.api.fiscal.MODEL390HF;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.github.javafaker.Faker;

public class FiscalFaker {
	private static Faker faker = new Faker( new Locale("es") );

	public static class FiscalFakerParams {
		private AONContext ctx;
		private Occam occam;
		private Date issueDate;
		private Administration administration;
		private boolean monthly;
		private boolean complementary;
		private boolean replacement;
		private boolean generateFromYearStart;
		private double prorratePercent;
		private boolean specialProrrate;
		private boolean diffEnabled;
		private boolean manualEnabled;
		
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

		public boolean isComplementary() {
			return complementary;
		}
		public FiscalFakerParams setComplementary(boolean complementary) {
			this.complementary = complementary;
			return this;
		}
		
		public boolean isReplacement() {
			return replacement;
		}
		public FiscalFakerParams setReplacement(boolean replacement) {
			this.replacement = replacement;
			return this;
		}
		public boolean isGenerateFromYearStart() {
			return generateFromYearStart;
		}
		public FiscalFakerParams setGenerateFromYearStart(boolean generateFromYearStart) {
			this.generateFromYearStart = generateFromYearStart;
			return this;
		}
		public double getProrratePercent() {
			return prorratePercent;
		}
		public FiscalFakerParams setProrratePercent(double prorratePercent) {
			this.prorratePercent = prorratePercent;
			return this;
		}
		public boolean isSpecialProrrate() {
			return specialProrrate;
		}
		public FiscalFakerParams setSpecialProrrate(boolean specialProrrate) {
			this.specialProrrate = specialProrrate;
			return this;
		}

		public boolean isDiffEnabled() {
			return diffEnabled;
		}

		public FiscalFakerParams setDiffEnabled(boolean diffEnabled) {
			this.diffEnabled = diffEnabled;
			return this;
		}

		public boolean isManualEnabled() {
			return manualEnabled;
		}

		public FiscalFakerParams setManualEnabled(boolean manualEnabled) {
			this.manualEnabled = manualEnabled;
			return this;
		}
	}
	
	public static FiscalFakerParams getRandomParams(AONContext ctx, Occam occam) {
		double prorratePercent = AonRandom.gt(10)? 0 : AonRandom.getPercent();
		return new FiscalFakerParams(ctx,occam)
			.setIssueDate(AonRandom.getYearDay(new Date()))
			.setMonthly(AonRandom.gt(80))
			.setProrratePercent( prorratePercent )
			.setSpecialProrrate( AonMathUtils.isNotZero(prorratePercent) && AonRandom.gt(60) )
			;
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
	public static <T extends FiscalModel> T getFiscalModel(FiscalFakerParams params,Supplier<T> modelSupplier) {
		return getFiscalModel(params,modelSupplier,null);
	}
	
	public static <T extends FiscalModel> T getFiscalModel(FiscalFakerParams params
			,Supplier<T> modelSupplier
			,Consumer<T> initializer) {
		T t = modelSupplier.get();
		t.setDomain(params.getOccam().getDomain());
		t.setYear(AonDateUtils.getYear(params.getIssueDate()));
		if (t.getModel() != null && t.getModel().isYearly()) {
			t.setPeriod( Period.YEAR );
		} else {
			t.setPeriod( params.isMonthly()
				? Period.getMonthlyPeriod(AonDateUtils.getMonth(params.getIssueDate()))
				: Period.getQuarterlyPeriod(AonDateUtils.getMonth(params.getIssueDate())) );
		}
		t.setAdministration(Objects.requireNonNullElse(params.getAdministration(), getRandomAdministration()));
		if (initializer != null)  {
			initializer.accept(t);
		}
		t.setComplementary(params.isComplementary());
		t.setReplacement(params.isReplacement());
		t.setGenerateFromYearStart(t.isGenerateFromYearStartAvailable() && params.isGenerateFromYearStart());
		return t; 
	}
	
	public static Mod111 getMod111( FiscalFakerParams params) {
		return getFiscalModel(params,Mod111::new,
			(m) -> MODEL111.initialize( params.getOccam(), m));
	}

	public static Mod111 createMod111( FiscalFakerParams params) {
		Mod111 mod111 = getMod111( params );
		MODEL111.create(params.getOccam(), mod111);
		return mod111;
	}
	
	public static Mod115 getMod115( FiscalFakerParams params) {
		return getFiscalModel(params,Mod115::new,
			(m) -> MODEL115.initialize( params.getOccam(), m));
	}

	public static Mod115 createMod115( FiscalFakerParams params) {
		Mod115 mod115 = getMod115( params );
		MODEL115.create(params.getOccam(), mod115);
		return mod115;
	}

	public static Mod123 getMod123( FiscalFakerParams params) {
		return getFiscalModel(params,Mod123::new,
			(m) -> MODEL123.initialize( params.getOccam(), m));
	}

	public static Mod123 createMod123( FiscalFakerParams params) {
		Mod123 mod123 = getMod123( params );
		MODEL123.create(params.getOccam(), mod123);
		return mod123;
	}

	public static Mod130 getMod130( FiscalFakerParams params) {
		return getFiscalModel(params,Mod130::new,
			(m) -> MODEL130.initialize( params.getOccam(), m));
	}
	
	public static Mod130 createMod130( FiscalFakerParams params) {
		Mod130 mod130 = getMod130( params );
		MODEL130.create(params.getOccam(), mod130);
		return mod130;
	}

	public static Mod303 getMod303( FiscalFakerParams params) {
		return  getFiscalModel(params,Mod303::new,
			(m) -> {
				m.setProratePercent( params.getProrratePercent() );
				if (m.hasProrate()) {
					m.setSpecialProrateValue(params.isSpecialProrrate());	
				}
				MODEL303.initialize( params.getOccam(), m);
			}
		);
	}
	public static Mod303 createMod303( FiscalFakerParams params) {
		Mod303 mod303 = getMod303( params );
		MODEL303.create(params.getOccam(), mod303);
		return mod303;
	}

	public static Mod390HF getMod390HF( FiscalFakerParams params) {
		return  getFiscalModel(params,Mod390HF::new,
			(m) -> {
				m.setProratePercent( params.getProrratePercent() );
				if (m.hasProrate()) {
					m.setSpecialProrateValue(params.isSpecialProrrate());	
				}
				MODEL390HF.initialize( params.getOccam(), m);
			}
		);
	}
	public static Mod390HF createMod390HF( FiscalFakerParams params) {
		Mod390HF mod = getMod390HF( params );
		MODEL390HF.create(params.getOccam(), mod);
		return mod;
	}

	public static Mod190 createMod190(FiscalFakerParams params) {
		Mod190 mod190 = MODEL190.initialize(params.getOccam(), AonDateUtils.getYear(params.getIssueDate()));
		mod190.setAdministration(Objects.requireNonNullElse(params.getAdministration(), getRandomAdministration()));
		mod190.setComplementary(params.isComplementary());
		mod190.setReplacement(params.isReplacement());
		return MODEL190.save(params.getOccam(), mod190);
	}
	
	public static Mod349 createMod349(FiscalFakerParams params) {
		Mod349 mod349 = MODEL349.initialize(params.getOccam());
		mod349.setAdministration(Objects.requireNonNullElse(params.getAdministration(), getRandomAdministration()));
		mod349.setComplementary(params.isComplementary());
		mod349.setReplacement(params.isReplacement());				
		mod349.setYear(AonDateUtils.getYear(params.getIssueDate()));
		mod349.setPeriod( params.isMonthly()
				       		? Period.getMonthlyPeriod(AonDateUtils.getMonth(params.getIssueDate()))
				       		: Period.getQuarterlyPeriod(AonDateUtils.getMonth(params.getIssueDate())) );
		mod349.setManualDeclaration(params.isManualEnabled());
		mod349.setDiffEnabled(params.isDiffEnabled());		
		return MODEL349.save(params.getOccam(), mod349);
	}
	

	
}
