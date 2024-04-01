package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod123;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO.Alcatraz;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.ExplainRowManager;
import com.esferalia.aon.occam.impl.jooq.dao.irpf.IRPFDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;

public abstract class Mod123Declaration {
	enum ComplementaryBeahaviour {
		COMPLEMENTARY,
		REPLACEMENT;
	}
	
	private enum Declarations {
		 AEAT_2024 {
			@Override boolean accept(Mod123 mod) { return Mod123AEAT2024Declaration.accept(mod);}
			@Override Mod123Declaration get() {return new Mod123AEAT2024Declaration();}
		}		
		,AEAT_2021 {
			@Override boolean accept(Mod123 mod) { return Mod123AEAT2021Declaration.accept(mod);}
			@Override Mod123Declaration get() {return new Mod123AEAT2021Declaration();}
		}
		,ARABA_2024 {
			@Override boolean accept(Mod123 mod) { return Mod123Araba2024Declaration.accept(mod);}
			@Override Mod123Declaration get() {return new Mod123Araba2024Declaration();}
		}
		,ARABA_2021 {
			@Override boolean accept(Mod123 mod) { return Mod123Araba2021Declaration.accept(mod);}
			@Override Mod123Declaration get() {return new Mod123Araba2021Declaration();}
		}
		,BIZKAIA_2024 {
			@Override boolean accept(Mod123 mod) { return Mod123Bizkaia2024Declaration.accept(mod);}
			@Override Mod123Declaration get() {return new Mod123Bizkaia2024Declaration();}
		}
		,BIZKAIA_2021 {
			@Override boolean accept(Mod123 mod) { return Mod123Bizkaia2021Declaration.accept(mod);}
			@Override Mod123Declaration get() {return new Mod123Bizkaia2021Declaration();}
		}
		,GIPUZKOA_2024 {
			@Override boolean accept(Mod123 mod) { return Mod123Gipuzkoa2024Declaration.accept(mod);}
			@Override Mod123Declaration get() {return new Mod123Gipuzkoa2024Declaration();}
		}
		,GIPUZKOA_2021 {
			@Override boolean accept(Mod123 mod) { return Mod123Gipuzkoa2021Declaration.accept(mod);}
			@Override Mod123Declaration get() {return new Mod123Gipuzkoa2021Declaration();}
		}
		,NAVARRA_2021 {
			@Override boolean accept(Mod123 mod) { return Mod123Navarra2021Declaration.accept(mod);}
			@Override Mod123Declaration get() {return new Mod123Navarra2021Declaration();}
		}
		;
		abstract boolean accept(Mod123 mod);
		abstract Mod123Declaration get();
	}
	
	@FunctionalInterface
	static interface IValueAccepter {
		boolean accept(Mod123 mod,IrpfBreakdown rc);
	}
	@FunctionalInterface
	static interface IValueIntializer {
		void initialize(AONContext ctx,Mod123 mod,Map<Mod123Key,Set<String>> docs,IrpfBreakdown br);
	}
	@FunctionalInterface
	static interface IValueUniqueIntializer {
		void initialize(AONContext ctx,Mod123 mod);
	}
	
	public static Mod123Declaration getInstance( Mod123 mod) {
		if (mod.getAdministration() == null) {
			throw new AonCoreException("No se ha indicado administraci\u00F3n para la declaraci\u00F3n");
		}
		if (mod.getYear() < 2010 && mod.getYear() > 2025) {
			throw new AonCoreException("No se ha indicado una ejercicio vÃ¡lido para la declaraci\u00F3n");
		}
		if (mod.getPeriod() == null) {
			throw new AonCoreException("No se ha indicado periodo para la declaraci\u00F3n");	
		}
		return Arrays.stream(Declarations.values())
				.filter(dec -> dec.accept(mod))
				.map(Declarations::get)
				.findFirst()
				.orElseThrow( () -> new AonCoreException(MessageFormat.format(
					"No existe una declaración para el modelo solicitado ({0} - {1} - {2})",
					mod.getAdministration().getDescription()
					,mod.getYear()
					,mod.getPeriod().getDescription())));
	}

	IMod123KeyDAO getKey(Mod123Key key) {
		for (IMod123KeyDAO keyDAO : getKeys()) {
			if (keyDAO.getKey() == key) {
				return keyDAO;
			}
		}
		return null;
	}

	public Mod123 initializeModel(AONContext ctx, Mod123 mod123) {
		initializeComplementaryAndReplacement(ctx,mod123);
		initializePreviousData(ctx,mod123);
		return mod123;
	}

	private void initializePreviousData(AONContext ctx, Mod123 mod123) {
		mod123.getMessages().clear();		
		mod123.setGenerateFromYearStartAvailable(!mod123.isFirstPeriod());
		if (mod123.isGenerateFromYearStartAvailable()) {
			Map<Integer, Long> invoices = checkPreviousInvoices(ctx, mod123);
			boolean existsInvoices = invoices != null && !invoices.isEmpty();
			if (existsInvoices) {
				mod123.addMessage("Se encontraron " + invoices.size() + " facturas no declaradas anteriores a la fecha "
						+ "de inicio de la declaraci\u00F3n.");
			}
			mod123.setGenerateFromYearStartAvailable(existsInvoices);
		}
	}

	private void initializeComplementaryAndReplacement(AONContext ctx, Mod123 mod123) {
		mod123.setReplacedNumber(null);
		if ( mod123.isComplementaryDeclarationAvailable() || mod123.isReplacementDeclarationAvailable()) {
			Mod123 previous = Mod123DAO.getSamePeriodFiscalModels(ctx, mod123).findFirst().orElse(null);
			if (previous != null) {
				mod123.setComplementary( mod123.isComplementaryDeclarationAvailable() );
				mod123.setReplacement( mod123.isReplacementDeclarationAvailable() 
					&& !mod123.isComplementary() );
				mod123.setReplacedNumber(previous.getNumber());
			} else {
				mod123.setComplementary( false );
				mod123.setReplacement( false );
			}
		}
	}

	void specificInitialization(Mod123 mod123) {
	}

	static void addPerceptor(Mod123Key key,Mod123 mod
			,Map<Mod123Key,Set<String>> docs
			,IrpfBreakdown br) {
		// Se suman todos los perceptores (acumulado).
		docs.computeIfAbsent(key, k -> new HashSet<String>());
		if (!docs.get(key).contains(br.getRegistryDocument())) {
			docs.get(key).add(br.getRegistryDocument());
			mod.ensureDetail(key).addAccumulatedAmount(1);
		}
	}
	static void addBase(Mod123Key key,Mod123 mod,IrpfBreakdown br) {
		mod.ensureDetail(key).addAccumulatedAmount(br.getBase());
	}
	static void addQuota(Mod123Key key,Mod123 mod,IrpfBreakdown br) {
		mod.ensureDetail(key).addAccumulatedAmount(br.getQuota());
	}
	static void addDeponentDocument(AONContext ctx, Mod123 mod) {
		Domain domain = DomainDAO.getDomain(ctx, ctx.getDomainId());
		if (!domain.isStandalone()) {
			Company parentCompany = CompanyDAO.getCompany(ctx, domain.getParentId());
			if (parentCompany != null) {
				mod.ensureDetail(Mod123Key.GP_X00).setDescription(parentCompany.getDocument());	
			}
		}
	}

	void ensureDetails(Mod123 mod123) {
		Arrays.stream( getKeys() )
			.forEach(key -> mod123.ensureDetail(key.getKey()).setExpression(key.getExpression()));
	}

	void uniqueInitialize(AONContext ctx, Mod123 mod123) {
		Arrays.stream( getKeys() )
			.forEach(key -> key.uniqueInitialize(ctx, mod123));
	}
	
	private static class KeyedIrpfBreakdown {
		private IMod123KeyDAO key;
		private IrpfBreakdown br;
		private KeyedIrpfBreakdown(IMod123KeyDAO key,IrpfBreakdown br) {
			this.key = key;
			this.br = br;
		}
		public IMod123KeyDAO getKey() {
			return key;
		}
		public IrpfBreakdown getIrpfBreakdown() {
			return br;
		}
	}

	KeyedIrpfBreakdown addInvoice( Set<Alcatraz> invoices, KeyedIrpfBreakdown br) {
		invoices.add(new Alcatraz().setInvoice(br.getIrpfBreakdown().getInvoice()));
		return br;	
	}
	
	Map<Integer, Long>  checkPreviousInvoices(final AONContext ctx, final Mod123 mod123) {
		return IRPFDAO.getPreviousNotInModelInputInvoicesIrpfBreakdown(ctx, mod123)
			.flatMap(br -> Arrays.stream( getKeys() ).map( key -> new KeyedIrpfBreakdown(key, br)))
			.filter(kbr -> kbr.getKey().acceptValue(mod123,kbr.getIrpfBreakdown()))
			.collect(Collectors.groupingBy(kbr -> kbr.getIrpfBreakdown().getInvoice() 
					, Collectors.counting()));
	}

	Set<Alcatraz> createFromInvoices(final AONContext ctx, final Mod123 mod123) {
		final Map<Mod123Key,Set<String>> docs = new EnumMap<>(Mod123Key.class); 
		final Set<Alcatraz> invoices = new HashSet<>();
		Stream<IrpfBreakdown> stream = null;
		if (mustApplyReplacementSearch(mod123)) {
			stream =  IRPFDAO.getInputInvoicesIrpfBreakdown(ctx, mod123);
		} else {
			stream = IRPFDAO.getNotInModelInputInvoicesIrpfBreakdown(ctx, mod123);	
		}
		stream
			.flatMap(br -> Arrays.stream( getKeys() ).map( key -> new KeyedIrpfBreakdown(key, br)))
			.filter(kbr -> kbr.getKey().acceptValue(mod123,kbr.getIrpfBreakdown()))
			.map( kbr -> addInvoice(invoices, kbr))
			.forEach(kbr -> kbr.getKey().initialize(ctx, mod123, docs, kbr.getIrpfBreakdown()))
			;
		return invoices;
	}
	
	protected void initializeDeclarationType(Mod123 mod123) {
		if (AonMathUtils.isGreatherThanZero(mod123.getDeclarationResult() )) {
			mod123.setDeclarationResultType(FiscalModelDeclarationType.DEPOSIT);
		} else {
			mod123.setDeclarationResultType(FiscalModelDeclarationType.NEGATIVE);
		}
	}
	
	protected boolean mustApplyReplacementSearch( Mod123 mod123 ) {
		return (mod123.isReplacement()
			|| (mod123.isComplementary() && getComplementaryBehaviour(mod123) == ComplementaryBeahaviour.REPLACEMENT)); 
	}
	
	protected String getSamePeriodExplain(AONContext ctx, Mod123 mod123, Mod123Key key) {
		return DeclarationInfoUtil.getExplain( ctx, mod123, key, Mod123DAO.getSamePeriodEffectiveModels(ctx, mod123), new ExplainRowManager());	
	}

	abstract Mod123 initialize(AONContext ctx, Mod123 mod123);
	abstract IMod123KeyDAO valueOf(String string);
	abstract IMod123KeyDAO[] getKeys();
	abstract double getResult(final Mod123 mod123);
	abstract ComplementaryBeahaviour getComplementaryBehaviour(final Mod123 mod123);
	public abstract Mod123Key[] getSamePeriodExplainKeys();

}
