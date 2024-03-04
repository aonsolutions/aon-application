package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod111;

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
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO.Alcatraz;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.ExplainRowManager;
import com.esferalia.aon.occam.impl.jooq.dao.irpf.IRPFDAO;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;

public abstract class Mod111Declaration {
	enum ComplementaryBeahaviour {
		COMPLEMENTARY,
		REPLACEMENT;
	}
	
	@FunctionalInterface
	static interface IValueAccepter {
		boolean accept(Mod111 mod,IrpfBreakdown rc);
	}
	@FunctionalInterface
	static interface IValueIntializer {
		void initialize(AONContext ctx,Mod111 mod,Map<Mod111Key,Set<String>> docs,Map<Mod111Key, Set<String>> pdocs,IrpfBreakdown br);
	}
	@FunctionalInterface
	static interface IValueUniqueIntializer {
		void initialize(AONContext ctx,Mod111 mod);
	}
	
	private enum Declarations {
		 AEAT_2023 {
			@Override boolean accept(Mod111 mod) { return Mod111AEAT2023Declaration.accept(mod);}
			@Override Mod111Declaration get() {return new Mod111AEAT2023Declaration();}
		}
		,BIZKAIA_110_2023 {
			@Override boolean accept(Mod111 mod) { return Mod110Bizkaia2023Declaration.accept(mod);}
			@Override Mod111Declaration get() {return new Mod110Bizkaia2023Declaration();}
		}
		,BIZKAIA_111_2023 {
			@Override boolean accept(Mod111 mod) { return Mod111Bizkaia2023Declaration.accept(mod);}
			@Override Mod111Declaration get() {return new Mod111Bizkaia2023Declaration();}
		}
		,ARABA_2023 {
			@Override boolean accept(Mod111 mod) { return Mod111Araba2023Declaration.accept(mod);}
			@Override Mod111Declaration get() {return new Mod111Araba2023Declaration();}
		}
		,GIPUZKOA_2023 {
			@Override boolean accept(Mod111 mod) { return Mod111Gipuzkoa2023Declaration.accept(mod);}
			@Override Mod111Declaration get() {return new Mod111Gipuzkoa2023Declaration();}
		}
		,NAVARRA_2023 {
			@Override boolean accept(Mod111 mod) { return Mod111Navarra2023Declaration.accept(mod);}
			@Override Mod111Declaration get() {return new Mod111Navarra2023Declaration();}
		}

		,AEAT_2021 {
			@Override boolean accept(Mod111 mod) { return Mod111AEAT2021Declaration.accept(mod);}
			@Override Mod111Declaration get() {return new Mod111AEAT2021Declaration();}
		}
		,ARABA_2021 {
			@Override boolean accept(Mod111 mod) { return Mod111Araba2021Declaration.accept(mod);}
			@Override Mod111Declaration get() {return new Mod111Araba2021Declaration();}
		}
		,BIZKAIA_111_2021 {
			@Override boolean accept(Mod111 mod) { return Mod111Bizkaia2021Declaration.accept(mod);}
			@Override Mod111Declaration get() {return new Mod111Bizkaia2021Declaration();}
		}
		,BIZKAIA_110_2021 {
			@Override boolean accept(Mod111 mod) { return Mod110Bizkaia2021Declaration.accept(mod);}
			@Override Mod111Declaration get() {return new Mod110Bizkaia2021Declaration();}
		}
		,GIPUZKOA_2021 {
			@Override boolean accept(Mod111 mod) { return Mod111Gipuzkoa2021Declaration.accept(mod);}
			@Override Mod111Declaration get() {return new Mod111Gipuzkoa2021Declaration();}
		}
		,NAVARRA_2021 {
			@Override boolean accept(Mod111 mod) { return Mod111Navarra2021Declaration.accept(mod);}
			@Override Mod111Declaration get() {return new Mod111Navarra2021Declaration();}
		}
		;
		abstract boolean accept(Mod111 mod);
		abstract Mod111Declaration get();
	}

	
	public static Mod111Declaration getInstance( Mod111 mod) {
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

	IMod111KeyDAO getKey(Mod111Key key) {
		for (IMod111KeyDAO keyDAO : getKeys()) {
			if (keyDAO.getKey() == key) {
				return keyDAO;
			}
		}
		return null;
	}

	public Mod111 initializeModel(AONContext ctx, Mod111 mod111) {
		initializeComplementaryAndReplacement(ctx,mod111);
		initializePreviousData(ctx,mod111);
		return mod111;
	}

	private void initializePreviousData(AONContext ctx, Mod111 mod111) {
		mod111.getMessages().clear();		
		mod111.setGenerateFromYearStartAvailable(!mod111.isFirstPeriod());
		if (mod111.isGenerateFromYearStartAvailable()) {
			Map<Integer, Long> invoices = checkPreviousInvoices(ctx, mod111);
			boolean existsInvoices = invoices != null && !invoices.isEmpty();
			if (existsInvoices) {
				mod111.addMessage("Se encontraron " + invoices.size() + " facturas no declaradas anteriores a la fecha "
						+ "de inicio de la declaraci\u00F3n.");
			}
			Map<Integer, Long> salaries = checkPreviousSalaries(ctx, mod111);
			boolean existsSalaries = salaries != null && !salaries.isEmpty();			
			if (existsSalaries) {
				mod111.addMessage("Se encontraron " + salaries.size() + " n\u00F3minas no declaradas anteriores a la fecha "
						+ "de inicio de la declaraci\u00F3n.");
			}
			mod111.setGenerateFromYearStartAvailable(existsInvoices || existsSalaries);
			
		}
	}

	private void initializeComplementaryAndReplacement(AONContext ctx, Mod111 mod111) {
		mod111.setReplacedNumber(null);
		if ( mod111.isComplementaryDeclarationAvailable() || mod111.isReplacementDeclarationAvailable()) {
			Mod111 previous = Mod111DAO.getSamePeriodFiscalModels(ctx, mod111).findFirst().orElse(null);
			if (previous != null) {
				mod111.setComplementary( mod111.isComplementaryDeclarationAvailable() );
				mod111.setReplacement( mod111.isReplacementDeclarationAvailable() && !mod111.isComplementary() );
				mod111.setReplacedNumber(previous.getNumber());
			} else {
				mod111.setComplementary( false );
				mod111.setReplacement( false );
			}
		}
	}

	void initializeKeys(AONContext ctx,Mod111 mod,Map<Mod111Key,Set<String>> docs,Map<Mod111Key,Set<String>> pdocs,IrpfBreakdown  br) {
		Arrays.stream(getKeys())
			.filter(key -> key.acceptValue(mod,br))
			.forEach(key -> key.initialize(ctx, mod, docs, pdocs, br));
	}

	void specificInitialization(Mod111 mod111) {
		// Nothing
	}

	static void addPerceptor(Mod111Key key,Mod111 mod
			,Map<Mod111Key,Set<String>> docs
			,Map<Mod111Key,Set<String>> pdocs
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
	static void addBase(Mod111Key key,Mod111 mod,IrpfBreakdown br) {
		mod.ensureDetail(key).addAccumulatedAmount(br.getBase());
	}
	static void addQuota(Mod111Key key,Mod111 mod,IrpfBreakdown br) {
		mod.ensureDetail(key).addAccumulatedAmount(br.getQuota());
	}
	static void addDeponentDocument(AONContext ctx, Mod111 mod) {
		Domain domain = DomainDAO.getDomain(ctx, ctx.getDomainId());
		if (!domain.isStandalone()) {
			Company parentCompany = CompanyDAO.getCompany(ctx, domain.getParentId());
			if (parentCompany != null) {
				mod.ensureDetail(Mod111Key.GP_X00).setDescription(parentCompany.getDocument());	
			}
		}
	}

	void ensureDetails(Mod111 mod111) {
		Arrays.stream( getKeys() )
			.forEach(key -> mod111.ensureDetail(key.getKey()).setExpression(key.getExpression()));
	}

	void uniqueInitialize(AONContext ctx, Mod111 mod111) {
		Arrays.stream( getKeys() )
			.forEach(key -> key.uniqueInitialize(ctx, mod111));
	}
	
	Map<Integer, Long>  checkPreviousSalaries(final AONContext ctx, final Mod111 mod111) {
		return IRPFDAO.getPreviousNotInModelSalaryIrpfBreakdown(ctx, mod111)
			.flatMap(br -> Arrays.stream( getKeys() ).map( key -> new KeyedIrpfBreakdown(key, br)))
			.filter(kbr -> kbr.getKey().acceptValue(mod111,kbr.getIrpfBreakdown()))
			.collect(Collectors.groupingBy(kbr -> kbr.getIrpfBreakdown().getSalary() 
					, Collectors.counting()));
	}

	Set<Integer> createFromSalary(final AONContext ctx, final Mod111 mod111) {
		final Map<Mod111Key,Set<String>> docs = new EnumMap<>(Mod111Key.class); 
		final Map<Mod111Key,Set<String>> pdocs = new EnumMap<>(Mod111Key.class);
		final Set<Integer> salaries = new HashSet<>();

		Stream<IrpfBreakdown> stream = null;
		if (mustApplyReplacementSearch(mod111)) {
			Mod111 previous = Mod111DAO.getSamePeriodEffectiveModels( ctx, mod111 )
				.findFirst()
				.orElse(null);
			stream = Stream.concat(
				((previous == null) ? Stream.empty(): IRPFDAO.getModelSalaryIrpfBreakdown(ctx, previous))
				,IRPFDAO.getNotInModelSalaryIrpfBreakdown(ctx, mod111));
		} else {
			stream = IRPFDAO.getNotInModelSalaryIrpfBreakdown(ctx, mod111);
		}
		stream
			.flatMap(br -> Arrays.stream( getKeys() ).map( key -> new KeyedIrpfBreakdown(key, br)))
			.filter(kbr -> kbr.getKey().acceptValue(mod111,kbr.getIrpfBreakdown()))
			.map( kbr -> addSalary(salaries, kbr))
			.forEach(kbr -> kbr.getKey().initialize(ctx, mod111, docs, pdocs, kbr.getIrpfBreakdown()))
		;
		return salaries; 
	}
	
	private static class KeyedIrpfBreakdown {
		private IMod111KeyDAO key;
		private IrpfBreakdown br;
		private KeyedIrpfBreakdown(IMod111KeyDAO key,IrpfBreakdown br) {
			this.key = key;
			this.br = br;
		}
		public IMod111KeyDAO getKey() {
			return key;
		}
		public IrpfBreakdown getIrpfBreakdown() {
			return br;
		}
	}

	KeyedIrpfBreakdown addSalary( Set<Integer> salaries, KeyedIrpfBreakdown br) {
		salaries.add(br.getIrpfBreakdown().getSalary());
		return br;	
	}

	KeyedIrpfBreakdown addInvoice( Set<Alcatraz> invoices, KeyedIrpfBreakdown br) {
		invoices.add(new Alcatraz().setInvoice(br.getIrpfBreakdown().getInvoice()));
		return br;	
	}
	
	Map<Integer, Long>  checkPreviousInvoices(final AONContext ctx, final Mod111 mod111) {
		return IRPFDAO.getPreviousNotInModelInputInvoicesIrpfBreakdown(ctx, mod111)
			.flatMap(br -> Arrays.stream( getKeys() ).map( key -> new KeyedIrpfBreakdown(key, br)))
			.filter(kbr -> kbr.getKey().acceptValue(mod111,kbr.getIrpfBreakdown()))
			.collect(Collectors.groupingBy(kbr -> kbr.getIrpfBreakdown().getInvoice() 
					, Collectors.counting()));
	}

	Set<Alcatraz> createFromInvoices(final AONContext ctx, final Mod111 mod111) {
		final Map<Mod111Key,Set<String>> docs = new EnumMap<>(Mod111Key.class); 
		final Map<Mod111Key,Set<String>> pdocs = new EnumMap<>(Mod111Key.class);
		final Set<Alcatraz> invoices = new HashSet<>();
		Stream<IrpfBreakdown> stream = null;
		if (mustApplyReplacementSearch(mod111)) {
			stream =  IRPFDAO.getInputInvoicesIrpfBreakdown(ctx, mod111);
		} else {
			stream = IRPFDAO.getNotInModelInputInvoicesIrpfBreakdown(ctx, mod111);	
		}
		stream
			.flatMap(br -> Arrays.stream( getKeys() ).map( key -> new KeyedIrpfBreakdown(key, br)))
			.filter(kbr -> kbr.getKey().acceptValue(mod111,kbr.getIrpfBreakdown()))
			.map( kbr -> addInvoice(invoices, kbr))
			.forEach(kbr -> kbr.getKey().initialize(ctx, mod111, docs, pdocs, kbr.getIrpfBreakdown()))
			;
		return invoices;
	}
	
	protected void initializeDeclarationType(Mod111 mod111) {
		if (AonMathUtils.isGreatherThanZero(mod111.getDeclarationResult() )) {
			mod111.setDeclarationResultType(FiscalModelDeclarationType.DEPOSIT);
		} else {
			mod111.setDeclarationResultType(FiscalModelDeclarationType.NEGATIVE);
		}
	}

	protected boolean mustApplyReplacementSearch( Mod111 mod111 ) {
		return (mod111.isReplacement()
			|| (mod111.isComplementary() && getComplementaryBehaviour(mod111) == ComplementaryBeahaviour.REPLACEMENT)); 
	}
  
	protected String getSamePeriodExplain(AONContext ctx, Mod111 mod111, Mod111Key key) {
		return DeclarationInfoUtil.getExplain( ctx, mod111, key, Mod111DAO.getSamePeriodEffectiveModels(ctx, mod111), new ExplainRowManager());	
	}

	abstract Mod111 initialize(AONContext ctx, Mod111 mod111);
	abstract IMod111KeyDAO valueOf(String string);
	abstract IMod111KeyDAO[] getKeys();
	abstract double getResult(final Mod111 mod111);
	abstract ComplementaryBeahaviour getComplementaryBehaviour(final Mod111 mod111);
	public abstract Mod111Key[] getSamePeriodExplainKeys();
	


//	public static Stream<IrpfBreakdown> getReplacementSalaryIrpfBreakdown(final AONContext ctx, final ISalaryFiscalModel fm) {
//		Integer replacedId = ctx.getDslContext()
//			.select(FS_MODEL.ID)
//				.from(FS_MODEL)
//				.join(DOMAIN).on(DOMAIN.ID.equal(FS_MODEL.DOMAIN))
//				.where(FS_MODEL.DOMAIN.eq(fm.getDomain()))
//				.and(FS_MODEL.MODEL.eq(fm.getModel().getValue()))
//				.and(FS_MODEL.YEAR.eq(fm.getYear()))
//				.and(FS_MODEL.ADMINISTRATION.eq(fm.getAdministration().value()))
//				.and(FS_MODEL.PERIOD.eq(fm.getPeriod().value()))
//				.and(fm.getId()==null?DSL.trueCondition():FS_MODEL.ID.lt(fm.getId()))
//				.orderBy(FS_MODEL.ID.desc())
//				.fetch()
//				.stream()
//				.map(rec -> rec.getValue(FS_MODEL.ID))
//				.findFirst()
//				.orElse(null)
//				;
//		return replacedId==null
//			? Stream.empty()
//			: getSalaryIrpfBreakdownSelect(ctx)
//				.innerJoin(ALCATRAZ).on(ALCATRAZ.SALARY.equal(SALARY.ID))
//				.where(SALARY.DOMAIN.equal(fm.getDomain()))
//					.and(ALCATRAZ.FS_MODEL.eq( replacedId ))
//					.and(SALARY.IRPF_BASE.ne( 0.0 ))
//					.and(WORKPLACE.ECONOMICAGREEMENT.equal(fm.getAdministration().value()))
//					.and(SALARY.TYPE.in(SalaryType.IRPF_SALARIES )) // Skip SLD ( L00, L13... )
//				.orderBy(getSalaryDateField(fm),SALARY.ID,SALARY.EMPLOYEE_NAME)
//				.fetch()
//				.stream()
//				.map(rec -> new IrpfSalaryBreakdownFiller().apply(rec) )
//				.flatMap(List::stream);
//	}
}
