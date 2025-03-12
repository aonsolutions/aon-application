package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod202;

import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.DEC2;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.bold;
import static com.esferalia.aon.watson.j2html.TagCreator.li;
import static com.esferalia.aon.watson.j2html.TagCreator.ul;

import java.util.Date;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.AccountingBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.ExplainRowManager;
import com.esferalia.aon.watson.j2html.tags.specialized.UlTag;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

public class Mod202AEATDeclaration extends Mod202Declaration {
	
	public static boolean accept(Mod202 mod) {
		return mod.isAEAT() && mod.getYear() < 2025;
	}
	
	private enum Mod202KeyDAO  implements IMod202KeyDAO {
		 P00(Mod202Key.P00)
		,P01(Mod202Key.P01)
		,P02(Mod202Key.P02)
		,P03(Mod202Key.P03
			,null
			,(ctx,mod) -> mod.putDescription(Mod202Key.P03,Mod202DAO.getMainActivityCNAE( ctx, mod ))
			,null
			,null)
		,X01(Mod202Key.X01)
		,X02(Mod202Key.X02)
		,X03(Mod202Key.X03
			,(mod -> mod.getYear() < 2017)
			,null
			,null
			,null)
		,X04(Mod202Key.X04)
		,X05(Mod202Key.X05)
		,X06(Mod202Key.X06)
		,X07(Mod202Key.X07
			,(mod -> mod.getYear() < 2017)
			,null
			,null
			,null)
		,X08(Mod202Key.X08)
		,X09(Mod202Key.X09
			,(mod -> mod.getYear() < 2017)
			,null
			,null
			,null)
		,X10(Mod202Key.X10)
		,X11(Mod202Key.X11)
		,X12(Mod202Key.X12
			,(mod -> mod.getYear() > 2016)
			,null
			,null
			,null)
		,X13(Mod202Key.X13
			,(mod -> mod.getYear() > 2016)
			,null
			,null
			,null)
		,X14(Mod202Key.X14,(mod -> mod.getYear() > 2016)
			,null
			,null
			,null)
		
		,X15(Mod202Key.X15
			,(mod -> (mod.getYear() > 2018 || (mod.getYear() == 2018 && mod.getPeriod().ordinal() >= Period.T2.ordinal())))
			,null
			,null
			,null)
		,X16(Mod202Key.X16
			,(mod -> (mod.getYear() > 2018 || (mod.getYear() == 2018 && mod.getPeriod().ordinal() >= Period.T2.ordinal())))
			,null
			,null
			,null)
		,X17(Mod202Key.X17
			,(mod -> (mod.getYear() > 2018 || (mod.getYear() == 2018 && mod.getPeriod().ordinal() >= Period.T2.ordinal())))
			,null
			,null
			,null)
		,X18(Mod202Key.X18
			,(mod -> (mod.getYear() > 2018 || (mod.getYear() == 2018 && mod.getPeriod().ordinal() >= Period.T2.ordinal())))
			,null
			,null
			,null)
		,X19(Mod202Key.X19
			,(mod -> (mod.getYear() > 2018 || (mod.getYear() == 2018 && mod.getPeriod().ordinal() >= Period.T2.ordinal())))
			,null
			,null
			,null)
		,X20(Mod202Key.X20
			,(mod -> (mod.getYear() >= 2023))
			,null
			,null
			,null)
		,X00(Mod202Key.X00)
		,C01(Mod202Key.C01
			,null
			,(ctx,mod) -> mod.putAmount(Mod202Key.C01, getInitialC01(ctx,mod))
			,"isMethodA()?C01:0.0"
			,Mod202AEATDeclaration::getC01ComputeKeyInfo )
		,C02(Mod202Key.C02
			,null
			,null
			,"isMethodA()?C02:0.0"
			,null)
		,C03(Mod202Key.C03
			,null
			,null
			,"isMethodA()?((C01*18/100)-C02):0.0"  
			, Mod202AEATDeclaration::getC03ComputeKeyInfo )
		,C04(Mod202Key.C04
			,null
			,(ctx,mod) -> mod.putAmount(Mod202Key.C04, getInitialC04(ctx,mod))
			,"isMethodB()?C04:0.0"
			, Mod202AEATDeclaration::getC04ComputeKeyInfo )
		,C05(Mod202Key.C05
			,null
			,null
			,"isMethodB()?C05:0.0"
			,null)
		,C06(Mod202Key.C06
			,null
			,null
			,"isMethodB()?C06:0.0"
			,null)
		,C36(Mod202Key.C36
			,(mod -> mod.getYear() < 2017)
			,null
			,null
			,null)
		,C37(Mod202Key.C37
			,null
			,null
			,"isMethodB()?C37:0.0"
			,null)
		,C07(Mod202Key.C07
			,null
			,null
			,"isMethodB()?C07:0.0"
			,null)
		,C08(Mod202Key.C08
			,null
			,null
			,"isMethodB()?C08:0.0"
			,null)
		,C38(Mod202Key.C38
			,null
			,null
			,"isMethodB()?(C05+C07):0.0"
			, Mod202AEATDeclaration::getC38ComputeKeyInfo )
		,C39(Mod202Key.C39
			,null
			,null
			,"isMethodB()?(C06+C37+C08):0.0"
			, Mod202AEATDeclaration::getC39ComputeKeyInfo )
		,C09(Mod202Key.C09
			,(mod -> mod.getYear() < 2017)
			,null
			,null
			,null)
		,C43(Mod202Key.C43
			,(mod -> mod.getYear() < 2017)
			,null
			,null
			,null)
		,C13(Mod202Key.C13
			,null
			,null
			,"isMethodB()?(C04+C38-C39):0.0"
			, Mod202AEATDeclaration::getC13ComputeKeyInfo )
		,C44(Mod202Key.C44
			,null
			,null
			,"isMethodB()?C44:0.0"
			,null)
		,C14(Mod202Key.C14
			,null
			,null
			,"isMethodB()?C14:0.0"
			,null)
		,C45(Mod202Key.C45
			,null
			,null
			,"isMethodB()?C45:0.0"
			,null)
		,C46(Mod202Key.C46
			,null
			,null
			,"isMethodB()?C46:0.0"
			,null)
		,C16(Mod202Key.C16
			,null
			,null
			,"isMethodB1()?((C13-C14+C45-C46)>0?(C13-C14+C45-C46):0.0):0.0"
			, Mod202AEATDeclaration::getC16ComputeKeyInfo )
		,C17(Mod202Key.C17
			,null
			,null
			,"isMethodB1()?computeC17():0.0"
			, Mod202AEATDeclaration::getC17ComputeKeyInfo )
		,C47(Mod202Key.C47
			,null
			,null
			,"isMethodB1()?C47:0.0"
			,null)
		,C40(Mod202Key.C40
			,null
			,null
			,"isMethodB1()?C40:0.0"
			,null)
		,C48(Mod202Key.C48
			,null
			,null
			,"isMethodB1()?C48:0.0"
			,null)
		,C49(Mod202Key.C49
			,null
			,null
			,"isMethodB1()?C49:0.0"
			,null)
		,C18(Mod202Key.C18
			,null
			,null
			,"isMethodB1()?((C16*C17/100)+C47-C40+C48-C49):0.0"
			, Mod202AEATDeclaration::getC18ComputeKeyInfo )
		,C19(Mod202Key.C19
			,null
			,null
			,"isMethodB2()?((C13-C44-C14+C45)>0?(C13-C44-C14+C45):0.0):0.0"
			, Mod202AEATDeclaration::getC19ComputeKeyInfo )
		,C20(Mod202Key.C20
			,null
			,null
			,"isMethodB2()?(C20):0.0"
			,null)
		,C21(Mod202Key.C21
			,null
			,null
			,"isMethodB2()?computeC21():0.0"
			, Mod202AEATDeclaration::getC21ComputeKeyInfo )
		,C22(Mod202Key.C22
			,null
			,null
			,"isMethodB2()?(C20*C21/100):0.0"
			, Mod202AEATDeclaration::getC22ComputeKeyInfo )
		,C23(Mod202Key.C23
			,null
			,null
			,"isMethodB2()?(C19-C20):0.0"
			, Mod202AEATDeclaration::getC23ComputeKeyInfo )
		,C24(Mod202Key.C24
			,null
			,null
			,"isMethodB2()?computeC24():0.0"
			, Mod202AEATDeclaration::getC24ComputeKeyInfo )
		,C25(Mod202Key.C25
			,null
			,null
			,"isMethodB2()?(C23*C24/100):0.0"
			, Mod202AEATDeclaration::getC25ComputeKeyInfo )
		,C50(Mod202Key.C50
			,null
			,null
			,"isMethodB2()?C50:0.0"
			,null)
		,C42(Mod202Key.C42
			,null
			,null
			,"isMethodB2()?C42:0.0"
			,null)
		,C51(Mod202Key.C51
			,null
			,null
			,"isMethodB2()?C51:0.0"
			,null)
		,C52(Mod202Key.C52
			,null
			,null
			,"isMethodB2()?C52:0.0"
			,null)
		,C26(Mod202Key.C26
			,null
			,null			
			,"isMethodB2()?(C22+C25+C50-C42+C51-C52):0.0"
			, Mod202AEATDeclaration::getC26ComputeKeyInfo )
		,C27(Mod202Key.C27
			,null
			,null
			,"isMethodB()?C27:0.0"
			,null)
		,C28(Mod202Key.C28
			,null
			,null
			,"isMethodB()?C28:0.0"
			,null)
		,C29(Mod202Key.C29
			,null
			,null
			,"isMethodB()?C29:0.0"
			,null)
		,C30(Mod202Key.C30
			,null
			,null
			,"isMethodB()?C30:0.0"
			,null)
		,C31(Mod202Key.C31
			,null
			,null
			,"isMethodB()?C31:0.0"
			,null)
		,C32(Mod202Key.C32
			,null
			,null
			,"computeC32()"
			, Mod202AEATDeclaration::getC32ComputeKeyInfo )
		,C33(Mod202Key.C33
			,null
			,null
			,"isMethodB()?C33:0.0"
			,null)
		,C34(Mod202Key.C34
			,null
			,null
			,"isMethodB()?(C32>C33?C32:C33):0.0"
			, Mod202AEATDeclaration::getC34ComputeKeyInfo )
		,A01(Mod202Key.A01)
		,A02(Mod202Key.A02)
		,A03(Mod202Key.A03)
		,A04(Mod202Key.A04)
		,A05(Mod202Key.A05)
		,A06(Mod202Key.A06)
		,A07(Mod202Key.A07)
		,A08(Mod202Key.A08)
		,A09(Mod202Key.A09)
		,A10(Mod202Key.A10)
		,A11(Mod202Key.A11)
		,A12(Mod202Key.A12)
		;
		
		private Mod202Key key;
		private IValueAccepter accepter;
		private IValueIntializer initializer;
		private String expression;
		private IValueInfo info;
		
		private Mod202KeyDAO(Mod202Key key) {
			this(key, null, null, null, null);
		}
		
		private Mod202KeyDAO(Mod202Key key
				, IValueAccepter accepter
				, IValueIntializer initializer
				, String expression
				, IValueInfo info) {
			this.key = key;
			this.accepter = accepter;			
			this.initializer = initializer;
			this.expression =  expression;
			this.info =  info;
		}
		@Override
		public Mod202Key getKey() {
			return key;
		}
		@Override
		public boolean accept(Mod202 mod) {
			return this.accepter == null || this.accepter.accept(mod);
		}
		@Override
		public void initialize(AONContext ctx,Mod202 mod) {
			if (initializer != null) {
				initializer.initialize(ctx, mod);
			}
		}
		@Override
		public String getExpression() {
			return expression;
		}
		
		@Override
		public String info(AONContext ctx, Mod202 mod) {
			return (this.info != null)?this.info.info(ctx, mod):null;
		}
		
	}

	// **************************************************************
	// **************************************************************
	// **************************************************************
	// **************************************************************
	// **************************************************************
	// **************************************************************
	
	@Override
	IMod202KeyDAO valueOf(String string) {
		return Mod202KeyDAO.valueOf(string);
	}

	@Override
	IMod202KeyDAO[] getKeys() {
		return Mod202KeyDAO.values();
	}

	@Override
	double getResult(Mod202 mod) {
		double x00 = mod.getAmount(Mod202Key.X00);
		if (x00 == 0 ) {
			return mod.getAmount(Mod202Key.C03);
		} 
		return mod.getAmount(Mod202Key.C34);			
	}
	
	@Override
	ComplementaryBeahaviour getComplementaryBehaviour(Mod202 mod) {
		return ComplementaryBeahaviour.REPLACEMENT;
	}

	@Override
	Mod202 initialize(AONContext ctx, Mod202 mod202) {
		mod202.setComplementaryDeclarationAvailable(true);
		mod202.setReplacementDeclarationAvailable(false);
		return super.initializeModel(ctx, mod202);
	}
	
	@Override
	Mod202 uniqueInitialize(AONContext ctx, Mod202 mod202) {
		AonCollectionUtils.stream(getKeys())
			.forEach( k -> k.initialize(ctx, mod202));
		return mod202;
	}
	
	@Override
	public Mod202Key[] getSamePeriodExplainKeys() {
		return new Mod202Key[] {};
	}
	private static String noMethod(String msg) {
		return ul()
			.with( li( msg ).withStyle( bold ) )
			.with( li( "Resultado : 0.0" ) )
			.render();
	}
	private static String noMethodA() 	{return noMethod("Tipo C\u00E1lculo no es A) Art\u00EDculo LIS 40.2 LIS");}
	private static String noMethodB() 	{return noMethod("Tipo C\u00E1lculo no es B.1) ni B.2)");}
	private static String noMethodB1() 	{return noMethod("Tipo C\u00E1lculo no es B.1)");}
	private static String noMethodB2() 	{return noMethod("Tipo C\u00E1lculo no es B.2)");}
	
	private static String fmt( double d ) {
		return DEC2.format( d );
	}

	private static String getC01ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C01, new ExplainRowManager() {
				@Override
				public String apply(FiscalModel fm) {
					setSomething(true);
					if (mod.isMethodA()) {
						int year = mod.getYear()- ((mod.getPeriod() == Period.T1)?2:1); 
						return ul()
							.with( li( "Tipo C\u00E1lculo: A) Art\u00EDculo LIS 40.2 LIS").withStyle( bold ) )
							.with( li( "Valor de la casilla [599] del Impuesto de Sociedades del ejercicio "
									+year
									+" : "
									+ fmt( getInitialC01(ctx, mod) )).withStyle( bold )
									)
							.render();
					}
					return noMethodA();
				}
		});	
	}

	private static String getC03ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C03, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodA()) {
					return ul()
						.with( li( "Resultado del 18% de [001] menos [002] ").withStyle( bold ) )
						.with( li(" 18% de " 
								+ fmt( mod.getAmount( Mod202Key.C01 ))
								+ " menos "
								+ fmt( mod.getAmount( Mod202Key.C02 ))
								+ " igual "
								+ fmt( mod.getAmount( Mod202Key.C03 ))))
						.render();
				} 
				return noMethodA();
			}
		});	
	}
	
	private static String getC38ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C38, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB()) {
					return ul()
						.with( li( "Resultado de [005] m\u00E1s [007] ").withStyle( bold ) )
						.with( li(fmt( mod.getAmount( Mod202Key.C05 ))
								+ " m\u00E1s "
								+ fmt( mod.getAmount( Mod202Key.C07 ))
								+ " igual "
								+ fmt( mod.getAmount( Mod202Key.C38 ))))
						.render();
				} 
				return noMethodB();
			}
		});	
	}

	private static String getC39ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C39, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB()) {
					return ul()
						.with( li( "Resultado de [006] m\u00E1s [037] m\u00E1s [008]").withStyle( bold ) )
						.with( li(fmt( mod.getAmount( Mod202Key.C06 ))
								+ " m\u00E1s "
								+ fmt( mod.getAmount( Mod202Key.C37 ))
								+ " m\u00E1s "
								+ fmt( mod.getAmount( Mod202Key.C08 ))
								+ " igual "
								+ fmt( mod.getAmount( Mod202Key.C39 ))))
						.render();
				} 
				return noMethodB();
			}
		});	
	}
	
	private static String getC04ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C04, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				Pair<Date,Date> dates = getAccountingRangePeriod(mod );
				if (mod.isMethodB()) {
					return ul()
						.with( li( "Desde contabilidad.").withStyle( bold ) )
						.with( li( "Saldos acreedor del sumatorio de cuentas que afectan a la confecci\u00F3n del modelo"
								+ " desde el "
								+ DeclarationInfoUtil.FMT.format(dates.getLeft())
								+ " al "
								+ DeclarationInfoUtil.FMT.format(dates.getRight())))
						.render();
				}
				return noMethodB();
			}
		});	
	}
	
	@Override
	Stream<AccountingBreakdown> getAccountInfoInfo(AONContext ctx, Mod202 mod, IModelScript<Mod202Key> script,IMod202KeyDAO keyDAO) {
		if (mod.isMethodB() && keyDAO.getKey() == Mod202Key.C04) {
			return getInitialBaseC04(ctx, mod);
		}
		return Stream.empty(); 
	}
	
	private static String getC13ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C13, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB()) {
					return ul()
						.with( li( "Resultado de [004] m\u00E1s [038] menos [039]").withStyle( bold ) )
						.with( li(fmt( mod.getAmount( Mod202Key.C04 ))
								+ " m\u00E1s "
								+ fmt( mod.getAmount( Mod202Key.C38 ))
								+ " menos "
								+ fmt( mod.getAmount( Mod202Key.C39 ))
								+ " igual "
								+ fmt( mod.getAmount( Mod202Key.C13 ))))
						.render();
				}
				return noMethodB();
			}
		});
	}

	private static String getC16ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C16, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB1()) {
					double c13 = mod.getAmount( Mod202Key.C13); 
					double c14 = mod.getAmount( Mod202Key.C14);
					double c45 = mod.getAmount( Mod202Key.C45);
					double c46 = mod.getAmount( Mod202Key.C46);
					double c16 = AonMathUtils.round(c13-c14+c45-c46);
					if ( AonMathUtils.isGreatherThanZero(c16)) {
						return ul()
							.with( li( "Resultado de [013] menos [014] m\u00E1s [045] menos [046]").withStyle( bold ) )
							.with( li(fmt( c13 )
									+ " menos "
									+ fmt( c14 )
									+ " m\u00E1s "
									+ fmt( c45 )
									+ " menos "
									+ fmt( c46 )
									+ " igual "
									+ fmt( c16 )))
							.render();
					} else {
						return ul()
							.with( li( "Al ser el resultado de [013] menos [014] m\u00E1s [045] menos [046] menor que cero, el resultado es cero").withStyle( bold ) )
							.with( li(fmt( c13 )
									+ " menos "
									+ fmt( c14 )
									+ " m\u00E1s "
									+ fmt( c45 )
									+ " menos "
									+ fmt( c46 )
									+ " igual "
									+ fmt( c16 )
									+ ". Menor que cero, por lo tanto cero."))
							.render();
					}
				}
				return noMethodB1();
			}
		});
	}
	
	private static String getC17ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C17, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB1()) {
					Double x08 = getPercent(mod);
					Double x04 = mod.getAmount(Mod202Key.X04);
					Double x09 = mod.getAmount(Mod202Key.X09);
					double c17 = mod.getAmount(Mod202Key.C17);
					if (AonMathUtils.isNotZero(x04) &&  mod.getYear() >= 2017  ) {
						return ul()
							.with( li( "Entidad que aplica el r\u00E9gimen de las entidades navieras en funci\u00F3n del tonelaje: " + fmt( c17 )).withStyle( bold ) )
							.render();
					} else if (AonMathUtils.isZero(x09)) {
						return ul()
							.with( li( "Resultado de aplicar 5/7 al tipo de gravamen del Impuesto sobre Sociedades del ejercicio en curso (" + fmt( x08 ) 
									+ ") es decir, ("+ AonMathUtils.floor((double)5/7) 
									+"), redondeado a la baja, igual: "+ fmt( c17 )).withStyle( bold ) )
							.render();
					} else {
						return ul()
							.with( li( "Resultado de aplicar 19/20 al tipo de gravamen del Impuesto sobre Sociedades del ejercicio en curso (" + fmt( x08 ) 
									+ ") es decir, ("+ AonMathUtils.ceil((double)19/20) 
									+ "), redondeado al alza, igual: "+ fmt( c17 )).withStyle( bold ) )
							.render();
					}
				}
				return noMethodB1();
			}
		});
	}
	
	private static String getC18ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C18, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB1()) {
					// (C16*C17/100)+C47-C40+C48-C49"
					Double c16 = mod.getAmount(Mod202Key.C16);
					Double c17 = mod.getAmount(Mod202Key.C17);
					Double c47 = mod.getAmount(Mod202Key.C47);
					Double c40 = mod.getAmount(Mod202Key.C40);
					Double c48 = mod.getAmount(Mod202Key.C48);
					Double c49 = mod.getAmount(Mod202Key.C49);
					Double c18 = mod.getAmount(Mod202Key.C18);
					return ul()
						.with( li( "Resultado de la f\u00F3rmula ([016] * [017] / 100 ) + [047] - [040] + [048] - [049] = [018]").withStyle( bold ) )
						.with( li( "("+fmt(c16) +" * "+fmt(c17)+" / 100 ) + "+fmt(c47)+" - "+fmt(c40)+" + "+fmt(c48)+" - "+fmt(c49)+" = "+fmt(c18)).withStyle( bold ) )
						.render();
				}
				return noMethodB1();
			}
		});
	}

	private static String getC19ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C19, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB2()) {
					Double c13 = mod.getAmount(Mod202Key.C13);
					Double c44 = mod.getAmount(Mod202Key.C44);
					Double c14 = mod.getAmount(Mod202Key.C14);
					Double c45 = mod.getAmount(Mod202Key.C45);
					Double temp = AonMathUtils.round(c13-c44-c14+c45);
					Double c19 = mod.getAmount(Mod202Key.C19);
					if (AonMathUtils.isLessThanZero(temp)) {
						return ul()
							.with( li( "Al ser la casilla [013]-[044]-[014]+[045] menor que cero, "
								+ " ("
								+ fmt(temp)
								+") "
								+"el resultado es cero.").withStyle( bold ) )				
							.render();
					} else {
						return ul()
							.with( li( "Resultado de la f\u00F3rmula [013]-[044]-[014]+[045] = [018]").withStyle( bold ) )
							.with( li( fmt(c13)+"-"+fmt(c44)+"-"+fmt(c14)+"+"+fmt(c45)+" = "+fmt(c19)).withStyle( bold ) )
							.render();
					}
				}
				return noMethodB2();
			}
		});
	}

	private static String getC21ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C21 , new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB2()) {
					double x081 = getPercent1( mod );
					double x09 = mod.getAmount(Mod202Key.X09);
					double c21 = mod.getAmount(Mod202Key.C21);
					if (AonMathUtils.isZero(x09)) {
						return ul()
							.with( li( "Resultado de aplicar 5/7 al primer tipo de gravamen del Impuesto sobre Sociedades del ejercicio en curso (" + fmt( x081 ) 
									+ ") es decir, ("+ AonMathUtils.floor((double)5/7) 
									+"), redondeado a la baja, igual: "+ fmt( c21 )).withStyle( bold ) )
							.render();
					} else {
						return ul()
							.with( li( "Resultado de aplicar 19/20 al primer tipo de gravamen del Impuesto sobre Sociedades del ejercicio en curso (" + fmt( x081 ) 
									+ ") es decir, ("+ AonMathUtils.ceil((double)19/20) 
									+ "), redondeado al alza, igual: "+ fmt( c21 )).withStyle( bold ) )
							.render();
					}
				}
				return noMethodB2();
			}
		});
	}
	
	private static String getC22ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C22 , new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB2()) {
					double c20 = mod.getAmount(Mod202Key.C20);
					double c21 = mod.getAmount(Mod202Key.C21);
					double c22 = mod.getAmount(Mod202Key.C22);
					return ul()
						.with( li( "Resultado de la f\u00F3rmula [020] * [021] / 100 ").withStyle( bold ) )
						.with( li( fmt(c20)+" * "+fmt(c21)+" / 100 = "+fmt(c22)).withStyle( bold ) )
						.render();
				}
				return noMethodB2();
			}
		});
	}

	private static String getC23ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C23 , new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB2()) {
					double c19 = mod.getAmount(Mod202Key.C19);
					double c20 = mod.getAmount(Mod202Key.C20);
					double c23 = mod.getAmount(Mod202Key.C23);
					return ul()
						.with( li( "Resultado de la f\u00F3rmula [019] - [020] ").withStyle( bold ) )
						.with( li( fmt(c19)+" - "+fmt(c20)+" = "+fmt(c23)).withStyle( bold ) )
						.render();
				}
				return noMethodB2();
			}
		});
	}

	private static String getC24ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C24 , new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB2()) {
					double x082 = getPercent2( mod );
					double x09 = mod.getAmount(Mod202Key.X09);
					double c24 = mod.getAmount(Mod202Key.C21);
					if (AonMathUtils.isZero(x09)) {
						return ul()
							.with( li( "Resultado de aplicar 5/7 al segundo tipo de gravamen del Impuesto sobre Sociedades del ejercicio en curso (" + fmt( x082 ) 
									+ ") es decir, ("+ AonMathUtils.floor((double)5/7) 
									+"), redondeado a la baja, igual: "+ fmt( c24 )).withStyle( bold ) )
							.render();
					} else {
						return ul()
							.with( li( "Resultado de aplicar 19/20 al segundo tipo de gravamen del Impuesto sobre Sociedades del ejercicio en curso (" + fmt( x082 ) 
									+ ") es decir, ("+ AonMathUtils.ceil((double)19/20) 
									+ "), redondeado al alza, igual: "+ fmt( c24 )).withStyle( bold ) )
							.render();
					}
				}
				return noMethodB2();
			}
		});
	}

	private static String getC25ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C25 , new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB2()) {
					double c23 = mod.getAmount(Mod202Key.C23);
					double c24 = mod.getAmount(Mod202Key.C24);
					double c25 = mod.getAmount(Mod202Key.C25);
					return ul()
						.with( li( "Resultado de la f\u00F3rmula [023] * [024] / 100 ").withStyle( bold ) )
						.with( li( fmt(c23)+" * "+fmt(c24)+" / 100 = "+fmt(c25)).withStyle( bold ) )
						.render();
				}
				return noMethodB2();
			}
		});
	}	

	private static String getC26ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C26 , new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB2()) {
					double c22 = mod.getAmount(Mod202Key.C22);
					double c25 = mod.getAmount(Mod202Key.C25);
					double c50 = mod.getAmount(Mod202Key.C50);
					double c42 = mod.getAmount(Mod202Key.C42);
					double c51 = mod.getAmount(Mod202Key.C51);
					double c52 = mod.getAmount(Mod202Key.C52);
					double c26 = mod.getAmount(Mod202Key.C26);
					return ul()
						.with( li( "Resultado de la f\u00F3rmula [022] + [025] + [050] - [042] + [051] - [052] ").withStyle( bold ) )
						.with( li( fmt(c22)+" + "+fmt(c25)+" + "+fmt(c50)+" - "+fmt(c42)+" + "+fmt(c51)+" - "+fmt(c52)+" = "+fmt(c26) ).withStyle( bold ) )
						.render();
				}
				return noMethodB2();
			}
		});
	}	

	private static String getC32ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C32 , new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB()) {
					double c18 = mod.getAmount(Mod202Key.C18);
					double c26 = mod.getAmount(Mod202Key.C26);
					double c27 = mod.getAmount(Mod202Key.C27);
					double c28 = mod.getAmount(Mod202Key.C28);
					double c29 = mod.getAmount(Mod202Key.C29);
					double c30 = mod.getAmount(Mod202Key.C30);
					double c31 = mod.getAmount(Mod202Key.C31);
					double c32 = mod.getAmount(Mod202Key.C32);
					double rp = mod.isMethodB1()?c18:c26;
					UlTag ul = ul();
					if (mod.isMethodB1()) {
						ul.with( li( "Si la modalidad de c\u00E1lculo es B.1) : Resultado Previo [018] --> "+fmt(c18)).withStyle( bold ) );
					} else {
						ul.with( li( "Si la modalidad de c\u00E1lculo es B.2) : Resultado Previo [026] --> "+fmt(c26)).withStyle( bold ) );
					}
					double c32T= ((rp-c27-c28)*c29/100)-c30-c31;
					ul
						.with( li( "Resultado de la f\u00F3rmula (([Resultado Previo] - [027] - [028] ) * [029] / 100 ) - [030] - [031]").withStyle( bold ) )
						.with( li( "(("+fmt(rp)+" - "+fmt(c27)+" - "+fmt(c28)+" ) * "+fmt(c29)+" / 100 ) - "+fmt(c30)+" - "+fmt(c31)+" = "+fmt(c32T)).withStyle( bold ) );
					if (AonMathUtils.isLessThanZero( c32T )) {
						ul.with( li( "Al ser resultado negativo, cero: "+fmt(c32)).withStyle( bold ) );
					}
					return ul.render();
				}
				return noMethodB2();
			}
		});
	}
	
	private static String getC34ComputeKeyInfo(AONContext ctx, Mod202 mod) {
//	,""
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C32 , new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB()) {
					double c34 = mod.getAmount(Mod202Key.C34);
					return ul()
						.with( li("Si la modalidad de c\u00E1lculo es B.1) o B.2) : La cantidad mayor entre [032] y  [033]").withStyle( bold ) )
						.with( li( "Resultado: " + fmt(c34)).withStyle( bold ) )
						.render();
				}
				return noMethodB2();
			}
		});
	}
	
	public static double getPercent(Mod202 mod) {
		return AonNumberUtils.todouble( mod.getDescription(Mod202Key.X08) );
	}
	public static double getPercent1(Mod202 mod) {
		return AonNumberUtils.todouble(AonStringUtils.substringBefore(mod.getDescription(Mod202Key.X08), "/"));
	}
	public static double getPercent2(Mod202 mod) {
		return AonNumberUtils.todouble(AonStringUtils.substringAfter(mod.getDescription(Mod202Key.X08), "/"));
	}

	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
}
