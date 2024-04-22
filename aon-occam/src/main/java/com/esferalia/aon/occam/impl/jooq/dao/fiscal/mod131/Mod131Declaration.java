package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod131;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mvel2.MVEL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.AccountingBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod131ActivityModule;
import com.esferalia.aon.occam.api.model.fiscal.modules.Module;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2018.Epigraph;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO.Alcatraz;
import com.esferalia.aon.occam.impl.jooq.dao.irpf.IRPFDAO;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public abstract class Mod131Declaration {
	
	@FunctionalInterface
	static interface IActivityFromMapFiller {
		void fill(Mod131 mod,Mod131Key key);
	}
	@FunctionalInterface
	static interface IMapFromActivityFiller {
		void fill(Mod131 mod,Mod131Key key);
	}
	@FunctionalInterface
	static interface IValueInitializer {
		void initialize(AONContext ctx,Mod131 mod);
	}
	@FunctionalInterface
	static interface IIrpfBreakdownAccepter {
		boolean accept(Mod131 mod, IrpfBreakdown br);
	}
	@FunctionalInterface
	static interface IValueInfo{
		String info(AONContext ctx,Mod131 mod);
	}
	
	private static class KeyedIrpfBreakdown {
		private IMod131KeyDAO key;
		private IrpfBreakdown br;
		private KeyedIrpfBreakdown(IMod131KeyDAO key,IrpfBreakdown br) {
			this.key = key;
			this.br = br;
		}
		public IMod131KeyDAO getKey() {
			return key;
		}
		public IrpfBreakdown getIrpfBreakdown() {
			return br;
		}
	}
	
	private enum Declarations {
		AEAT_2024 {
			@Override boolean accept(Mod131 mod) { return Mod131AEAT2024Declaration.accept(mod);}
			@Override Mod131Declaration get() {return new Mod131AEAT2024Declaration();}
		}
		,AEAT_2023 {
			@Override boolean accept(Mod131 mod) { return Mod131AEAT2023Declaration.accept(mod);}
			@Override Mod131Declaration get() {return new Mod131AEAT2023Declaration();}
		}
		,AEAT_2022_4T {
			@Override boolean accept(Mod131 mod) { return Mod131AEAT20224TDeclaration.accept(mod);}
			@Override Mod131Declaration get() {return new Mod131AEAT20224TDeclaration();}
		}
		,AEAT_2020_4T {
			@Override boolean accept(Mod131 mod) { return Mod131AEAT20204TDeclaration.accept(mod);}
			@Override Mod131Declaration get() {return new Mod131AEAT20204TDeclaration();}
		}
		,AEAT_2016 {
			@Override boolean accept(Mod131 mod) { return Mod131AEAT2016Declaration.accept(mod);}
			@Override Mod131Declaration get() {return new Mod131AEAT2016Declaration();}
		}
		;
		abstract boolean accept(Mod131 mod);
		abstract Mod131Declaration get();
	}
	
	public static Mod131Declaration getInstance( Mod131 mod) {
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

	Mod131MVELContext getMVELcontext(AONContext ctx,Mod131 mod131) {
		Mod131MVELContext mvelCtx = new Mod131MVELContext( ctx, mod131 );
		mvelCtx.put("activities", mod131.getActivities());
		for (String key : mod131.getMap().keySet()) {
			Mod131Key mod131Key = Mod131Key.getKey(key);
			if (mod131Key != null) {
				FiscalModelDetail detail = mod131.getMap().get(key);
				mvelCtx.put(mod131Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}
		return mvelCtx;
	}

	Mod131 calculate(AONContext ctx, Mod131 mod131) {
		Mod131MVELContext mvelCtx = getMVELcontext(ctx,mod131);
		for (IMod131KeyDAO key : getKeys()) {
			if (AonStringUtils.isNotEmpty( key.getExpression())) {
				Object ret =  MVEL.eval( key.getExpression() , mvelCtx , mvelCtx);
				Double amount = (Double) ret;
				mvelCtx.put(key.toString(), amount);
				mod131.ensureDetail(key.getKey()).setAmount(AonMathUtils.round( amount) );
			}
		}
		mod131.setDeclarationResult(getResult(mod131));
		return mod131; 
	}
	
	Mod131 fillActivitiesFromMap(Mod131 mod131) {
		AonCollectionUtils.stream( getKeys() )
			.filter( k -> k != null )
			.forEach( k -> k.fillActivityFromMap(mod131,k.getKey()));
		return mod131;	
	}
	Mod131 fillMapFromActivities(Mod131 mod131) {
		AonCollectionUtils.stream( getKeys() )
			.filter( k -> k != null )
			.forEach( k -> k.fillMapFromActivity(mod131,k.getKey()));
		return mod131;	
	}

	protected static Mod131Activity ensureActivity(Mod131 mod131, int idx) {
		if (mod131.getActivities() == null) {
			mod131.setActivities( new LinkedList<>());
		}
		for (int x = 0; x <= idx; x++) {
			if (x >= mod131.getActivities().size()) {
				mod131.getActivities().add(new Mod131Activity()
					.setYear(mod131.getYear())
					.setPeriod(mod131.getPeriod()));	
			}
		}
		return mod131.getActivities().get(idx);
	}
	
	protected static Mod131ActivityModule ensureModule(Mod131 mod131, int act, int mod) {
		Mod131Activity activity = ensureActivity(mod131, act);
		if (activity.getModules() == null) {
			activity.setModules( new LinkedList<>());
		}
		for (int x = 0; x <= mod; x++) {
			if (x >= activity.getModules().size()) {
				activity.getModules().add(new Mod131ActivityModule());	
			}
		}
		return activity.getModules().get(mod);
	}
	
	void ensureDetails(Mod131 mod131) {
		Arrays.stream( getKeys() )
			.forEach(key -> mod131.ensureDetail(key.getKey()).setExpression(key.getExpression()));
	}
	
	Mod131 initialize(AONContext ctx, Mod131 mod131) {
		mod131.setComplementaryDeclarationAvailable(true);
		mod131.setReplacementDeclarationAvailable(false);
		mod131.putAmount(Mod130Key.P1, 100.0);
		mod131.putAmount(Mod131Key.P2, (AppParamDAO.isPermAddressChanges(ctx)?1:0));
		return initializeModel(ctx, mod131);
	}
	Mod131 initializeModel(AONContext ctx, Mod131 mod131) {
		initializeComplementaryAndReplacement(ctx,mod131);
		return mod131;
	}
	
	private void initializeComplementaryAndReplacement(AONContext ctx, Mod131 mod131) {
		mod131.setReplacedNumber(null);
		if ( mod131.isComplementaryDeclarationAvailable() || mod131.isReplacementDeclarationAvailable()) {
			Mod131 previous = Mod131DAO.getSamePeriodFiscalModels(ctx, mod131)
				.filter( m -> AonStringUtils.equals(m.getDocument(), mod131.getDocument()))
				.findFirst()
				.orElse(null);
			if (previous != null) {
				mod131.setComplementary( mod131.isComplementaryDeclarationAvailable() );
				mod131.setReplacement( mod131.isReplacementDeclarationAvailable() && !mod131.isComplementary() );
				mod131.setReplacedNumber(previous.getNumber());
			} else {
				mod131.setComplementary( false );
				mod131.setReplacement( false );
			}
		}
	}
	
	public Set<Alcatraz> createFromInvoices(AONContext ctx, final Mod131 mod131) {
		final Set<Alcatraz> invoices = new HashSet<>();
		IRPFDAO.getOutputInvoicesIrpfBreakdown(ctx, mod131)
			.flatMap( br -> Arrays.stream( getKeys() ).map( key -> new KeyedIrpfBreakdown(key, br)))
			.filter(kbr -> kbr.getKey().acceptIrpfBreakdown(mod131,kbr.getIrpfBreakdown()))
			.map( kbr -> addInvoice(invoices, kbr))
			.forEach( kbr -> addAmount(kbr.getKey().getKey() , mod131, kbr.getIrpfBreakdown().getQuota()))
		;
		return invoices;
	}
	
	private void addAmount(Mod131Key key,Mod131 mod,double amount) {
		FiscalModelDetail detail = mod.ensureDetail(key);
		detail.addAccumulatedAmount(amount);
		detail.addResultAmount( amount );	
		detail.addAmount( amount );
	}
	
	
	private KeyedIrpfBreakdown addInvoice( Set<Alcatraz> invoices, KeyedIrpfBreakdown br) {
		invoices.add(new Alcatraz().setInvoice(br.getIrpfBreakdown().getInvoice()));
		return br;	
	}

	void copyActivities(AONContext ctx, Mod131 mod131) {
		Mod131 prev131 = Mod131DAO.getMod131s(ctx, mod131.getDomain())
			.findFirst()
			.orElse(null);
		if (prev131 != null) {
			mod131.setActivities(copyActivities(prev131, mod131));
		} else {
			mod131.setActivities( new LinkedList<>());
		}
		
		// Ensure 5 activities
		while ( AonCollectionUtils.size( mod131.getActivities() ) < 5) {
			mod131.getActivities().add(new Mod131Activity()
				.setYear(mod131.getYear())
				.setPeriod(mod131.getPeriod())
				.setDia((int) (AonDateUtils.getDaysBetweenDates(FiscalUtils.getPeriodStart(mod131),FiscalUtils.getPeriodEnd(mod131)) + 1))
				.setModules( new LinkedList<>()));
		}
	}

	private LinkedList<Mod131Activity> copyActivities(Mod131 prev131, Mod131 mod131) {
		return AonCollectionUtils.stream( prev131.getActivities() )
			.filter( prevAct -> prevAct.getEpigraph() != null )
			.filter( prevAct -> Epigraph.hasEpigraph(prevAct.getEpigraph()))
			.map( prevAct -> {
				prevAct.setYear(mod131.getYear())
					.setPeriod(mod131.getPeriod());
				Epigraph epi = Epigraph.getEpigraph(prevAct.getEpigraph());
				prevAct.setMaxImport(epi.getLimExceso());
				int idx = 0;
				for (Module m : epi.getIRPFModules()) {
					prevAct.getModules().get(idx).setSalariedStaff(m.isSalariedStaff());
					prevAct.getModules().get(idx).setNoSalariedStaff(m.isNoSalariedStaff());
					idx++;
				}
				long prevDias = AonDateUtils.getDaysBetweenDates(FiscalUtils.getPeriodStart(prev131),FiscalUtils.getPeriodEnd(prev131)) + 1;
				if (prevAct.getDia() == prevDias) {
					long newDias = 	AonDateUtils.getDaysBetweenDates(FiscalUtils.getPeriodStart(mod131),FiscalUtils.getPeriodEnd(mod131)) + 1;
					prevAct.setDia((int) newDias);
				}
				return prevAct;
			})
			.collect(Collectors.toCollection(LinkedList::new));
	}

	protected void initializeDeclarationType(Mod131 mod131) {
		if (AonMathUtils.isGreatherThanZero(mod131.getDeclarationResult() )) {
			mod131.setDeclarationResultType(FiscalModelDeclarationType.DEPOSIT);
		} else {
			if (mod131.isLastPeriod() || AonMathUtils.isZero(mod131.getDeclarationResult()) ) {
				mod131.setDeclarationResultType(FiscalModelDeclarationType.NEGATIVE);
			} else {
				mod131.setDeclarationResultType(FiscalModelDeclarationType.TO_DEDUCE);
			}
		}
	}

	// ------------------------------------------------------------ [ABSTRACT]
	IMod131KeyDAO getKey(Mod131Key key) {
		return Arrays.stream( getKeys() )
			.filter(k -> k.getKey() == key)
			.findFirst()
			.orElse(null);
	}
	abstract Double getResult(Mod131 mod131);
	abstract Mod131Activity calculateActivity(AONContext ctx, Mod131Activity act);
	abstract IMod131KeyDAO[] getKeys();
	abstract Stream<AccountingBreakdown> getAccountInfoInfo(AONContext ctx, Mod131 mod, IModelScript<Mod131Key> script, IMod131KeyDAO keyDAO);
}
