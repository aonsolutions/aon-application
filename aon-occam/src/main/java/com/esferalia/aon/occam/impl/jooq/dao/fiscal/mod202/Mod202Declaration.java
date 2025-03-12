package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod202;

import static com.esferalia.aon.jooq.tables.FsModel200.FS_MODEL200;
import static com.esferalia.aon.jooq.tables.FsModel200Detail.FS_MODEL200_DETAIL;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Stream;

import org.mvel2.MVEL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.AccountingBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.ExplainRowManager;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

abstract class Mod202Declaration {
	
	private static final String BN599 = "BN599";
	
	enum ComplementaryBeahaviour {
		COMPLEMENTARY,
		REPLACEMENT;
	}
	
	private enum Declarations {
		 AEAT_2025 {
			@Override boolean accept(Mod202 mod) { return Mod202AEAT2025Declaration.accept(mod);}
			@Override Mod202Declaration get() {return new Mod202AEAT2025Declaration();}
		},
		 AEAT {
			@Override boolean accept(Mod202 mod) { return Mod202AEATDeclaration.accept(mod);}
			@Override Mod202Declaration get() {return new Mod202AEATDeclaration();}
		}
		;
		abstract boolean accept(Mod202 mod);
		abstract Mod202Declaration get();
	}
	
	
	@FunctionalInterface
	static interface IValueAccepter {
		boolean accept(Mod202 mod);
	}
	@FunctionalInterface
	static interface IValueIntializer {
		void initialize(AONContext ctx,Mod202 mod);
	}
	@FunctionalInterface
	static interface IValueInfo{
		String info(AONContext ctx,Mod202 mod);
	}
	
	static Mod202Declaration getInstance( Mod202 mod) {
		if (mod.getAdministration() == null) {
			throw new AonCoreException("No se ha indicado administraci\u00F3n para la declaraci\u00F3n");
		}
		if (mod.getYear() < 2010 && mod.getYear() > 2025) {
			throw new AonCoreException("No se ha indicado una ejercicio v\u00E1lido para la declaraci\u00F3n");
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

	IMod202KeyDAO getKey(Mod202Key key) {
		for (IMod202KeyDAO keyDAO : getKeys()) {
			if (keyDAO.getKey() == key) {
				return keyDAO;
			}
		}
		return null;
	}

	Mod202 initializeModel(AONContext ctx, Mod202 mod202) {
		initializeComplementaryAndReplacement(ctx,mod202);
		initializePreviousData(ctx,mod202);
		return mod202;
	}

	private void initializePreviousData(AONContext ctx, Mod202 mod202) {
		mod202.getMessages().clear();		
		mod202.setGenerateFromYearStartAvailable(!mod202.isFirstPeriod());
	}

	private void initializeComplementaryAndReplacement(AONContext ctx, Mod202 mod202) {
		mod202.setReplacedNumber(null);
		if ( mod202.isComplementaryDeclarationAvailable() || mod202.isReplacementDeclarationAvailable()) {
			Mod202 previous = Mod202DAO.getSamePeriodFiscalModels(ctx, mod202).findFirst().orElse(null);
			if (previous != null) {
				mod202.setComplementary( mod202.isComplementaryDeclarationAvailable() );
				mod202.setReplacement( mod202.isReplacementDeclarationAvailable() 
					&& !mod202.isComplementary() );
				mod202.setReplacedNumber(previous.getNumber());
			} else {
				mod202.setComplementary( false );
				mod202.setReplacement( false );
			}
		}
	}

	void ensureDetails(Mod202 mod202) {
		Arrays.stream( getKeys() )
			.forEach(key -> mod202.ensureDetail(key.getKey()).setExpression(key.getExpression()));
	}

	void initializeDeclarationType(Mod202 mod202) {
		if (AonMathUtils.isGreatherThanZero(mod202.getDeclarationResult() )) {
			mod202.setDeclarationResultType(FiscalModelDeclarationType.DEPOSIT);
		} else {
			mod202.setDeclarationResultType(FiscalModelDeclarationType.NEGATIVE);
		}
	}
	
	static String getSamePeriodExplain(AONContext ctx, Mod202 mod202, Mod202Key key) {
		return DeclarationInfoUtil.getExplain( ctx, mod202, key, Mod202DAO.getSamePeriodEffectiveModels(ctx, mod202), new ExplainRowManager());	
	}
	
	static double getInitialC01(AONContext ctx,Mod202 mod) {
		double x00 = mod.getAmount(Mod202Key.X00);
		if (x00 == 0) {
			int year = mod.getYear() - ((mod.getPeriod() == Period.T1)?2:1);
			return ctx.getDslContext().select(FS_MODEL200_DETAIL.VALUE)
				.from(FS_MODEL200)
				.innerJoin(FS_MODEL200_DETAIL).on(FS_MODEL200.ID.eq(FS_MODEL200_DETAIL.FS_MODEL200))
				.where(FS_MODEL200.DOMAIN.eq(ctx.getDomainId()))
				.and(FS_MODEL200.YEAR.eq(year))
				.and(FS_MODEL200_DETAIL.KEY.eq(BN599))
				.orderBy(FS_MODEL200.ID)
				.stream()
				.map(r -> r.getValue(FS_MODEL200_DETAIL.VALUE))
				.findFirst()
				.orElse(0.0)
			;
		}
		return 0.0;
	}

	static double getInitialC04(AONContext ctx, final Mod202 mod) {
		return getRawC04(ctx, mod); 
	}
	private static double getRawC04(AONContext ctx, final Mod202 mod) {
		return getInitialBaseC04(ctx, mod)
				.mapToDouble(br -> br.getCreditBalance())
				.sum();  
	}
	
	static Pair<Date,Date> getAccountingRangePeriod( Mod202 mod ) {
		Date startDate = AonDateUtils.getYearFirstDay(mod.getYear());
		Date endDate = null;
		if (mod.getPeriod() == Period.T1) {
			endDate = FiscalUtils.getPeriodEnd(mod.getYear(),Period.T1); // Hasta el 31 de marzo
		}
		if (mod.getPeriod() == Period.T2) {
			endDate = FiscalUtils.getPeriodEnd(mod.getYear(),Period.T3); // Hasta el 30 de septiembre
		}
		if (mod.getPeriod() == Period.T3) {
			endDate = FiscalUtils.getPeriodEnd(mod.getYear(),Period.M11); // Hasta el 30 de Noviembre.
		}
		return new Pair<>( startDate, endDate );
	}
	
	static Stream<AccountingBreakdown> getInitialBaseC04(AONContext ctx, final Mod202 mod) {
		Pair<Date,Date> dates = getAccountingRangePeriod(mod );
		return AccountEntryDAO.getAccountingBreakdown(ctx,
				p -> p.getDomainProperty().eq(ctx.getDomainId())
					.and(p.getEntryDateProperty().ge( dates.getLeft() ))
					.and(p.getEntryDateProperty().le( dates.getRight() ))
					.and(
							p.getAccountCodeProperty().like("6%")
							.or(p.getAccountCodeProperty().like("7%"))
						)
					)
			.filter( br -> (!br.hasActivity() || (!br.isFarmer() && (br.isNormalRegime() || br.isSimplifiedRegime())) ));
	}
	
	Mod202 calculate(AONContext ctx, Mod202 mod202) {
		Mod202MVELContext mvelCtx = getMvelContext(mod202);
		for (IMod202KeyDAO key : getKeys()) {
			if (AonStringUtils.isNotEmpty( key.getExpression())) {
				Object ret =  MVEL.eval( key.getExpression() , mvelCtx , mvelCtx);
				Double amount = (Double) ret;
				mvelCtx.put(key.getKey().toString(), amount);
				mod202.ensureDetail(key.getKey()).setAmount(AonMathUtils.round( amount) );
			}
		}
		mod202.setDeclarationResult(getResult(mod202));
		return mod202;
	}

	private Mod202MVELContext getMvelContext(Mod202 mod202) {
		Mod202MVELContext mvelCtx = new Mod202MVELContext( mod202 );
		for (String key : mod202.getMap().keySet()) {
			Mod202Key mod202Key = Mod202Key.getKey(key);
			if (mod202Key != null) {
				FiscalModelDetail detail = mod202.getMap().get(key);
				double amount;
				if (mod202Key == Mod202Key.X08) {
					String x08 = detail.getDescription();
					amount = AonNumberUtils.todouble(x08);
					double x081 = 0.0;
					double x082 = 0.0;
					double x083 = 0.0;
					double x084 = 0.0;
					String[] percent = AonStringUtils.split(AonStringUtils.replace(x08,"N",""), '/');
					if (percent != null && percent.length > 0) {
						x081 = AonNumberUtils.todouble(percent[0]);
						if (percent.length > 1) {
							x082 = AonNumberUtils.todouble(percent[1]);
						}
						if (percent.length > 2) {
							x083 = AonNumberUtils.todouble(percent[2]);
						}
						if (percent.length > 3) {
							x084 = AonNumberUtils.todouble(percent[3]);
						}
					} 
					mvelCtx.put(Mod202MVELContext.X08_1, x081);
					mvelCtx.put(Mod202MVELContext.X08_2, x082);
					mvelCtx.put(Mod202MVELContext.X08_3, x083);
					mvelCtx.put(Mod202MVELContext.X08_4, x084);
				} else {
					amount = detail==null?0.0:detail.getAmount();
				}
				mvelCtx.put(mod202Key.toString(), amount);
			}
		}
		return mvelCtx;
	}
	
	abstract Mod202 initialize(AONContext ctx, Mod202 mod202);
	abstract Mod202 uniqueInitialize(AONContext ctx, Mod202 mod202);
	abstract IMod202KeyDAO valueOf(String string);
	abstract IMod202KeyDAO[] getKeys();
	abstract double getResult(final Mod202 mod202);
	abstract ComplementaryBeahaviour getComplementaryBehaviour(final Mod202 mod202);
	abstract Mod202Key[] getSamePeriodExplainKeys();
	abstract Stream<AccountingBreakdown> getAccountInfoInfo(AONContext ctx, Mod202 mod, IModelScript<Mod202Key> script,IMod202KeyDAO keyDAO);

}
