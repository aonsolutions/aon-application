package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod130;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.mvel2.MVEL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.AccountingBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.AlcatrazDAO.Alcatraz;
import com.esferalia.aon.occam.impl.jooq.dao.irpf.IRPFDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public abstract class Mod130Declaration {
	enum ComplementaryBeahaviour {
		COMPLEMENTARY,
		REPLACEMENT;
	}
	
	private static class KeyedIrpfBreakdown {
		private IMod130KeyDAO key;
		private IrpfBreakdown br;
		private KeyedIrpfBreakdown(IMod130KeyDAO key,IrpfBreakdown br) {
			this.key = key;
			this.br = br;
		}
		public IMod130KeyDAO getKey() {
			return key;
		}
		public IrpfBreakdown getIrpfBreakdown() {
			return br;
		}
	}

	@FunctionalInterface
	static interface IValueAccepter {
		boolean accept(Mod130 mod, IrpfBreakdown br);
	}

	@FunctionalInterface
	static interface IValueIntializer {
		void initialize(AONContext ctx,Mod130 mod);
	}
	@FunctionalInterface
	static interface IValueInfo{
		String info(AONContext ctx,Mod130 mod);
	}
	private enum Declarations {
		AEAT_2024 {
			@Override boolean accept(Mod130 mod) { return Mod130AEAT2024Declaration.accept(mod);}
			@Override Mod130Declaration get() {return new Mod130AEAT2024Declaration();}
		}
		,AEAT_2023 {
			@Override boolean accept(Mod130 mod) { return Mod130AEAT2023Declaration.accept(mod);}
			@Override Mod130Declaration get() {return new Mod130AEAT2023Declaration();}
		}
		,AEAT_2015 {
			@Override boolean accept(Mod130 mod) { return Mod130AEAT2015Declaration.accept(mod);}
			@Override Mod130Declaration get() {return new Mod130AEAT2015Declaration();}
		}
		,BIZKAIA_2015 {
			@Override boolean accept(Mod130 mod) { return Mod130BIZKAIA2015Declaration.accept(mod);}
			@Override Mod130Declaration get() {return new Mod130BIZKAIA2015Declaration();}
		}
		;
		abstract boolean accept(Mod130 mod);
		abstract Mod130Declaration get();
	}

	
	public static Mod130Declaration getInstance( Mod130 mod) {
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
	
	public IMod130KeyDAO getKey(Mod130Key key) {
		for (IMod130KeyDAO keyDAO : getKeys()) {
			if (keyDAO.getKey() == key) {
				return keyDAO;
			}
		}
		return null;
	}

	Mod130MVELContext getMVELcontext(AONContext ctx,Mod130 mod130) {
		Mod130MVELContext mvelCtx = new Mod130MVELContext( mod130 );
		for (String key : mod130.getMap().keySet()) {
			Mod130Key mod130Key = Mod130Key.getKey(key);
			if (mod130Key != null) {
				FiscalModelDetail detail = mod130.getMap().get(key);
				mvelCtx.put(mod130Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}
		return mvelCtx;
	}

	Mod130 basicCalculate(AONContext ctx, Mod130 mod130) {
		Mod130MVELContext mvelCtx = getMVELcontext(ctx,mod130);
		for (IMod130KeyDAO key : getKeys()) {
			if (AonStringUtils.isNotEmpty( key.getExpression())) {
				Object ret =  MVEL.eval( key.getExpression() , mvelCtx , mvelCtx);
				Double amount = (Double) ret;
				mvelCtx.put(key.getKey().toString(), amount);
				mod130.ensureDetail(key.getKey()).setAmount(AonMathUtils.round( amount) );
			}
		}
		mod130.setDeclarationResult(getResult(mod130));
		return mod130; 
	}

	public void ensureDetails(Mod130 mod130) {
		Arrays.stream( getKeys() )
			.forEach(key -> mod130.ensureDetail(key.getKey()).setExpression(key.getExpression()));
	}

	public void uniqueInitialize(AONContext ctx, Mod130 mod130) {
		Arrays.stream( getKeys() )
			.forEach(key -> key.initialize(ctx, mod130));
	}

	public Set<Alcatraz> createFromInvoices(AONContext ctx, final Mod130 mod130) {
		final Set<Alcatraz> invoices = new HashSet<>();
		IRPFDAO.getOutputInvoicesIrpfBreakdown(ctx, mod130)
			.flatMap( br -> Arrays.stream( getKeys() ).map( key -> new KeyedIrpfBreakdown(key, br)))
			.filter(kbr -> kbr.getKey().acceptValue(mod130,kbr.getIrpfBreakdown()))
			.map( kbr -> addInvoice(invoices, kbr))
			.map( kbr -> {
				double participationPercent = mod130.getAmount(Mod130Key.P1);
				double participationQuota = AonMathUtils.round( kbr.getIrpfBreakdown().getDeductibleQuota() * participationPercent / 100 );
				kbr.getIrpfBreakdown()
					.setParticipationPercent(participationPercent)
					.setParticipationQuota(participationQuota);
				return kbr;
			})
			.forEach( kbr -> mod130.addAmount(kbr.getKey().getKey() , kbr.getIrpfBreakdown().getParticipationQuota()))
		;
		return invoices;
	}
	
	private KeyedIrpfBreakdown addInvoice( Set<Alcatraz> invoices, KeyedIrpfBreakdown br) {
		invoices.add(new Alcatraz().setInvoice(br.getIrpfBreakdown().getInvoice()));
		return br;	
	}
	
	public Mod130 initializeModel(AONContext ctx, Mod130 mod130) {
		initializeComplementaryAndReplacement(ctx,mod130);
		return mod130;
	}
	
	private void initializeComplementaryAndReplacement(AONContext ctx, Mod130 mod) {
		mod.setReplacedNumber(null);
		if ( mod.isComplementaryDeclarationAvailable() || mod.isReplacementDeclarationAvailable()) {
			Mod130 previous = Mod130DAO.getSamePeriodFiscalModels(ctx, mod)
				.filter( m -> AonStringUtils.equals(m.getDocument(), mod.getDocument()))
				.findFirst()
				.orElse(null);
			if (previous != null) {
				mod.setComplementary( mod.isComplementaryDeclarationAvailable() );
				mod.setReplacement( mod.isReplacementDeclarationAvailable() && !mod.isComplementary() );
				mod.setReplacedNumber(previous.getNumber());
			} else {
				mod.setComplementary( false );
				mod.setReplacement( false );
			}
		}
	}
	
	protected void initializeDeclarationType(Mod130 mod130) {
		if (AonMathUtils.isGreatherThanZero(mod130.getDeclarationResult() )) {
			mod130.setDeclarationResultType(FiscalModelDeclarationType.DEPOSIT);
		} else {
			if (mod130.isLastPeriod() || AonMathUtils.isZero(mod130.getDeclarationResult()) ) {
				mod130.setDeclarationResultType(FiscalModelDeclarationType.NEGATIVE);
			} else {
				mod130.setDeclarationResultType(FiscalModelDeclarationType.TO_DEDUCE);
			}
		}
	}

	// *****************************************
	// ****** abstract methods *****************
	// *****************************************
	abstract Mod130 initialize(AONContext ctx, Mod130 mod130);
	abstract Double getResult(Mod130 mod130);
	abstract IMod130KeyDAO[] getKeys();
	abstract Mod130 calculate(AONContext ctx, Mod130 mod130);
	abstract Mod130MVELContext getMVELcontextForComputeKey(AONContext ctx, Mod130 mod130);
	abstract Stream<AccountingBreakdown> getAccountInfoInfo(AONContext ctx, Mod130 mod, IModelScript<Mod130Key> script,IMod130KeyDAO keyDAO);
	protected abstract ComplementaryBeahaviour getComplementaryBehaviour(Mod130 mod);
	
}
