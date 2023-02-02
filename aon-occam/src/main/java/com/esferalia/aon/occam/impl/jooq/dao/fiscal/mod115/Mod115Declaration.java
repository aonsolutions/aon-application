package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod115;

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
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.ExplainRowManager;
import com.esferalia.aon.occam.impl.jooq.dao.irpf.IRPFDAO;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;

public abstract class Mod115Declaration {
	
	enum ComplementaryBeahaviour {
		COMPLEMENTARY,
		REPLACEMENT;
	}
	
	private enum Declarations {
		 AEAT_2021 {
			@Override boolean accept(Mod115 mod) { return Mod115AEAT2021Declaration.accept(mod);}
			@Override Mod115Declaration get() {return new Mod115AEAT2021Declaration();}
		}
		,ARABA_2021 {
			@Override boolean accept(Mod115 mod) { return Mod115Araba2021Declaration.accept(mod);}
			@Override Mod115Declaration get() {return new Mod115Araba2021Declaration();}
		}
		,BIZKAIA_2021 {
			@Override boolean accept(Mod115 mod) { return Mod115Bizkaia2021Declaration.accept(mod);}
			@Override Mod115Declaration get() {return new Mod115Bizkaia2021Declaration();}
		}
		,GIPUZKOA_2021 {
			@Override boolean accept(Mod115 mod) { return Mod115Gipuzkoa2021Declaration.accept(mod);}
			@Override Mod115Declaration get() {return new Mod115Gipuzkoa2021Declaration();}
		}
		,NAVARRA_2021 {
			@Override boolean accept(Mod115 mod) { return Mod115Navarra2021Declaration.accept(mod);}
			@Override Mod115Declaration get() {return new Mod115Navarra2021Declaration();}
		}
		;
		abstract boolean accept(Mod115 mod);
		abstract Mod115Declaration get();
	}
	
	
	@FunctionalInterface
	static interface IValueAccepter {
		boolean accept(Mod115 mod,IrpfBreakdown rc);
	}
	@FunctionalInterface
	static interface IValueIntializer {
		void initialize(AONContext ctx,Mod115 mod,Map<Mod115Key,Set<String>> docs
				,Map<Mod115Key,Set<String>> pdocs,IrpfBreakdown br);
	}
	@FunctionalInterface
	static interface IValueUniqueIntializer {
		void initialize(AONContext ctx,Mod115 mod);
	}
	
	public static Mod115Declaration getInstance( Mod115 mod) {
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

	IMod115KeyDAO getKey(Mod115Key key) {
		for (IMod115KeyDAO keyDAO : getKeys()) {
			if (keyDAO.getKey() == key) {
				return keyDAO;
			}
		}
		return null;
	}

	public Mod115 initializeModel(AONContext ctx, Mod115 mod115) {
		initializeComplementaryAndReplacement(ctx,mod115);
		initializePreviousData(ctx,mod115);
		return mod115;
	}

	private void initializePreviousData(AONContext ctx, Mod115 mod115) {
		mod115.getMessages().clear();		
		mod115.setGenerateFromYearStartAvailable(!mod115.isFirstPeriod());
		if (mod115.isGenerateFromYearStartAvailable()) {
			Map<Integer, Long> invoices = checkPreviousInvoices(ctx, mod115);
			boolean existsInvoices = invoices != null && !invoices.isEmpty();
			if (existsInvoices) {
				mod115.addMessage("Se encontraron " + invoices.size() + " facturas no declaradas anteriores a la fecha "
						+ "de inicio de la declaraci\u00F3n.");
			}
			mod115.setGenerateFromYearStartAvailable(existsInvoices);
		}
	}

	private void initializeComplementaryAndReplacement(AONContext ctx, Mod115 mod115) {
		mod115.setReplacedNumber(null);
		if ( mod115.isComplementaryDeclarationAvailable() || mod115.isReplacementDeclarationAvailable()) {
			Mod115 previous = Mod115DAO.getSamePeriodFiscalModels(ctx, mod115).findFirst().orElse(null);
			if (previous != null) {
				mod115.setComplementary( mod115.isComplementaryDeclarationAvailable() );
				mod115.setReplacement( mod115.isReplacementDeclarationAvailable() 
					&& !mod115.isComplementary() );
				mod115.setReplacedNumber(previous.getNumber());
			} else {
				mod115.setComplementary( false );
				mod115.setReplacement( false );
			}
		}
	}

	void initializeKeys(AONContext ctx,Mod115 mod,Map<Mod115Key,Set<String>> docs,Map<Mod115Key,Set<String>> pdocs,IrpfBreakdown  br) {
		for (IMod115KeyDAO key : getKeys()) {
			if (key.acceptValue(mod,br)) {
				key.initialize(ctx, mod, docs, pdocs, br); 
			}
		}
	}

	void specificInitialization(Mod115 mod115) {
	}

	static void addPerceptor(Mod115Key key,Mod115 mod
			,Map<Mod115Key,Set<String>> docs
			,Map<Mod115Key,Set<String>> pdocs
			,IrpfBreakdown br) {
		// Se suman todos los perceptores (acumulado).
		docs.computeIfAbsent(key, k -> new HashSet<String>());
		if (!docs.get(key).contains(br.getRegistryDocument())) {
			docs.get(key).add(br.getRegistryDocument());
			mod.ensureDetail(key).addAccumulatedAmount(1);
		}
		// Se suman los perceptores del periodo que se esta haciendo.
		if (FiscalUtils.isInPeriodRange(mod, br.getTaxDate())) {
			pdocs.computeIfAbsent(key, k -> new HashSet<String>());
			if (!pdocs.get(key).contains(br.getRegistryDocument())) {
				pdocs.get(key).add(br.getRegistryDocument());
				mod.ensureDetail(key).addAmount(1);
			}
		}
	}
	static void addBase(Mod115Key key,Mod115 mod,IrpfBreakdown br) {
		mod.ensureDetail(key).addAccumulatedAmount(br.getBase());
	}
	static void addQuota(Mod115Key key,Mod115 mod,IrpfBreakdown br) {
		mod.ensureDetail(key).addAccumulatedAmount(br.getQuota());
	}
	static void addDeponentDocument(AONContext ctx, Mod115 mod) {
		Domain domain = DomainDAO.getDomain(ctx, ctx.getDomainId());
		if (!domain.isStandalone()) {
			Company parentCompany = CompanyDAO.getCompany(ctx, domain.getParentId());
			if (parentCompany != null) {
				mod.ensureDetail(Mod115Key.GP_X00).setDescription(parentCompany.getDocument());	
			}
		}
	}

	void ensureDetails(Mod115 mod115) {
		Arrays.stream( getKeys() )
			.forEach(key -> mod115.ensureDetail(key.getKey()).setExpression(key.getExpression()));
	}

	void uniqueInitialize(AONContext ctx, Mod115 mod115) {
		Arrays.stream( getKeys() )
			.forEach(key -> key.uniqueInitialize(ctx, mod115));
	}
	
	private static class KeyedIrpfBreakdown {
		private IMod115KeyDAO key;
		private IrpfBreakdown br;
		private KeyedIrpfBreakdown(IMod115KeyDAO key,IrpfBreakdown br) {
			this.key = key;
			this.br = br;
		}
		public IMod115KeyDAO getKey() {
			return key;
		}
		public IrpfBreakdown getIrpfBreakdown() {
			return br;
		}
	}

	KeyedIrpfBreakdown addInvoice( Set<Integer> invoices, KeyedIrpfBreakdown br) {
		invoices.add(br.getIrpfBreakdown().getInvoice());
		return br;	
	}
	
	Map<Integer, Long>  checkPreviousInvoices(final AONContext ctx, final Mod115 mod115) {
		return IRPFDAO.getPreviousNotInModelInputInvoicesIrpfBreakdown(ctx, mod115)
			.flatMap(br -> Arrays.stream( getKeys() ).map( key -> new KeyedIrpfBreakdown(key, br)))
			.filter(kbr -> kbr.getKey().acceptValue(mod115,kbr.getIrpfBreakdown()))
			.collect(Collectors.groupingBy(kbr -> kbr.getIrpfBreakdown().getInvoice() 
					, Collectors.counting()));
	}

	Set<Integer> createFromInvoices(final AONContext ctx, final Mod115 mod115) {
		final Map<Mod115Key,Set<String>> docs = new EnumMap<>(Mod115Key.class); 
		final Map<Mod115Key,Set<String>> pdocs = new EnumMap<>(Mod115Key.class);
		final Set<Integer> invoices = new HashSet<>();
		Stream<IrpfBreakdown> stream = null;
		if (mustApplyReplacementSearch(mod115)) {
			stream =  IRPFDAO.getInputInvoicesIrpfBreakdown(ctx, mod115);
		} else {
			stream = IRPFDAO.getNotInModelInputInvoicesIrpfBreakdown(ctx, mod115);	
		}
		stream
			.flatMap(br -> Arrays.stream( getKeys() ).map( key -> new KeyedIrpfBreakdown(key, br)))
			.filter(kbr -> kbr.getKey().acceptValue(mod115,kbr.getIrpfBreakdown()))
			.map( kbr -> addInvoice(invoices, kbr))
			.forEach(kbr -> kbr.getKey().initialize(ctx, mod115, docs, pdocs, kbr.getIrpfBreakdown()))
			;
		return invoices;
	}
	
	protected void initializeDeclarationType(Mod115 mod115) {
		if (AonMathUtils.isGreatherThanZero(mod115.getDeclarationResult() )) {
			mod115.setDeclarationResultType(FiscalModelDeclarationType.DEPOSIT);
		} else {
			mod115.setDeclarationResultType(FiscalModelDeclarationType.NEGATIVE);
		}
	}
	
	protected boolean mustApplyReplacementSearch( Mod115 mod115 ) {
		return (mod115.isReplacement()
			|| (mod115.isComplementary() && getComplementaryBehaviour(mod115) == ComplementaryBeahaviour.REPLACEMENT)); 
	}
	
	protected String getSamePeriodExplain(AONContext ctx, Mod115 mod115, Mod115Key key) {
		return DeclarationInfoUtil.getExplain( ctx, mod115, key, Mod115DAO.getSamePeriodEffectiveModels(ctx, mod115), new ExplainRowManager());	
	}
	
	abstract Mod115 initialize(AONContext ctx, Mod115 mod115);
	abstract IMod115KeyDAO valueOf(String string);
	abstract IMod115KeyDAO[] getKeys();
	abstract double getResult(final Mod115 mod115);
	abstract ComplementaryBeahaviour getComplementaryBehaviour(final Mod115 mod115);
	public abstract Mod115Key[] getSamePeriodExplainKeys();

}
