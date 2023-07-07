package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod130;

import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.DEC2;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.bold;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.fontMedium;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.fullStyledTag;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.marginTop;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.noWrap;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.paddingLeft;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.styledTag;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.textCenter;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.textLeft;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.textRight;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.textUnderline;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.AccountingBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.IRPFFormatter;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.ExplainRowManager;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class Mod130AEAT2023Declaration extends Mod130Declaration {
	
	private static final int LIMITE_GASTOS_DIF_JUST = 2000;
	private static final double C16_MAX_VALUE = 660.14;

	public static boolean accept(Mod130 mod) {
		return mod.isAEAT() && mod.getYear() >= 2023; 
	}
	@Override
	IMod130KeyDAO[] getKeys() {
		return Mod130KeyDAO.values();
	}
	@Override
	Double getResult(Mod130 mod130) {
		return mod130.getAmount(Mod130Key.C19);
	}
	
	@Override
	protected ComplementaryBeahaviour getComplementaryBehaviour(Mod130 mod) {
		return ComplementaryBeahaviour.REPLACEMENT;
	}
	
	private enum Mod130KeyDAO implements IMod130KeyDAO {
		 P0  (Mod130Key.P0)
		,P1  (Mod130Key.P1)
		,P2  (Mod130Key.P2)
		,C01 (Mod130Key.C01 
			,(ctx,mod) -> mod.putAmount(Mod130Key.C01, getInitialC01(ctx,mod))
			, null, null
			, (ctx,mod) -> getC01ComputeKeyInfo( ctx, mod ))
		,C02 (Mod130Key.C02 ,(ctx,mod) -> mod.putAmount(Mod130Key.C02, getInitialC02(ctx,mod))
			, null, null
			, (ctx,mod) -> getC02ComputeKeyInfo( ctx, mod ))
		,C03 (Mod130Key.C03 ,"C01-C02")
		,C04 (Mod130Key.C04
			, null, null  
			,"C03>0?(C03 * 20 / 100):(0.0)"
			, (ctx,mod) -> getC04ComputeKeyInfo( ctx, mod )) 
		,C05 (Mod130Key.C05 
			,(ctx,mod) -> mod.putAmount(Mod130Key.C05, getInitialC05(ctx,mod))
			, null, null
			, (ctx,mod) -> getC05ComputeKeyInfo( ctx, mod ))
		,C06 (Mod130Key.C06 , null, (mod,br) -> isNotFarmer(br)) 
		,C07 (Mod130Key.C07 ,"C04 - C05 - C06")
		,C08 (Mod130Key.C08 
			,(ctx,mod) -> mod.putAmount(Mod130Key.C08, getInitialC08(ctx,mod))
			, null, null
			, (ctx,mod) -> 	getC08ComputeKeyInfo( ctx, mod ))
		,C09 (Mod130Key.C09 ,"C08 * 2 / 100")
		,C10 (Mod130Key.C10 , null, (mod,br) -> isFarmer(br)) 
		,C11 (Mod130Key.C11 ,"C09 - C10")
		,C12 (Mod130Key.C12 
			, null, null  
			,"(C07 + C11)<0?0.0:(C07 + C11)"
			, (ctx,mod) -> getC12ComputeKeyInfo( ctx, mod ))
		,C131(Mod130Key.C131,(ctx,mod) -> mod.putAmount(Mod130Key.C131, getInitialC13(ctx,mod)))
		,C14 (Mod130Key.C14 ,"C12 - C131")
		,C15 (Mod130Key.C15 
			,(ctx,mod) -> mod.putAmount(Mod130Key.C15, getInitialC15(ctx,mod))
			, null, null
			, (ctx,mod) -> getC15ComputeKeyInfo( ctx, mod ))
		,C16 (Mod130Key.C16
			, null, null
			,"computeC16()"
			, (ctx,mod) -> getC16ComputeKeyInfo( ctx, mod ))
		,C17 (Mod130Key.C17,"C14 - C15 - C16")
		,C18 (Mod130Key.C18 
			,(ctx,mod) ->  mod.putAmount(Mod130Key.C18,mod.isComplementary()?Mod130DAO.getSamePeriodFiscalModels(ctx, mod).mapToDouble(fm -> fm.getDeclarationResult()).sum():0.0)
			, null, null
			, (ctx,mod) -> getC18ComputeKeyInfo( ctx, mod ))
		,C19 (Mod130Key.C19, "C17 - C18")
		,TIP (Mod130Key.CT_TIP)
		;
		
		private Mod130Key key;
		private IValueIntializer initializer;
		private IValueAccepter accepter;
		private String expression;
		private IValueInfo info;
		
		private Mod130KeyDAO(Mod130Key key) {
			this(key,null,null,null,null);
		}
		private Mod130KeyDAO(Mod130Key key,String expression) {
			this(key,null,null,expression,null);
		}
		private Mod130KeyDAO(Mod130Key key,IValueIntializer initializer) {
			this(key,initializer,null,null,null);
		}
		private Mod130KeyDAO(Mod130Key key,IValueIntializer initializer,IValueAccepter accepter) {
			this(key,initializer,accepter,null,null);
		}

		private Mod130KeyDAO(Mod130Key key,IValueIntializer initializer,String expression,IValueInfo info) {
			this(key,initializer,null,expression,info);
		}
		
		private Mod130KeyDAO(Mod130Key key,IValueIntializer initializer,IValueAccepter accepter,String expression,IValueInfo info) {
			this.key = key;
			this.initializer = initializer;
			this.accepter= accepter;
			this.expression =  expression;
			this.info =  info;
		}
		@Override
		public Mod130Key getKey() {
			return key;
		}
		@Override
		public void initialize(AONContext ctx,Mod130 mod) {
			if (initializer != null) {
				initializer.initialize(ctx, mod);
			}
		}
		@Override
		public boolean acceptValue(Mod130 mod,IrpfBreakdown  br) {
			return this.accepter != null && this.accepter.accept(mod, br); 
		}
		
		@Override
		public String info(AONContext ctx, Mod130 mod) {
			return (this.info != null)?this.info.info(ctx, mod):null;
		}
		
		@Override
		public String getExpression() {
			return expression;
		}
	}
	
	@Override
	Mod130MVELContext getMVELcontext(AONContext ctx,Mod130 mod130) {
		Mod130MVELContext mvelCtx = super.getMVELcontext(ctx, mod130);
		mvelCtx.put("C16_MAX_VALUE",660.14);
		return mvelCtx;
	}
	
	@Override
	Mod130MVELContext getMVELcontextForComputeKey(AONContext ctx,Mod130 mod130) {
		Mod130MVELContext mvelCtx = super.getMVELcontext(ctx, mod130);
		mvelCtx.put("RAW_C01", getRawC01(ctx, mod130));
		mvelCtx.put("RAW_C02", getRawC02(ctx, mod130));
		mvelCtx.put("RAW_C08", getRawC08(ctx, mod130));
		mvelCtx.put("LMT_GDJ", LIMITE_GASTOS_DIF_JUST);
		mvelCtx.put("yearStartDate", IRPFFormatter.FMT.format(AonDateUtils.getYearFirstDay(mod130.getYear())));
		mvelCtx.put("periodStartDate",IRPFFormatter.FMT.format(FiscalUtils.getPeriodStart(mod130)));
		mvelCtx.put("periodEndDate",IRPFFormatter.FMT.format(FiscalUtils.getPeriodEnd(mod130)));
		mvelCtx.put("previousModels", Mod130DAO.getPreviousModels(ctx, mod130).collect(Collectors.toCollection(LinkedList::new)));
		mvelCtx.put("periodModels", Mod130DAO.getSamePeriodModels(ctx, mod130).collect(Collectors.toCollection(LinkedList::new)));
		return mvelCtx;
	}
	
	@Override
	Mod130 initialize(AONContext ctx, Mod130 mod130) {
		mod130.setComplementaryDeclarationAvailable(true);
		mod130.setReplacementDeclarationAvailable(false);
		mod130.putAmount(Mod130Key.P1, 100.0);
		mod130.setRegime(AppParamDAO.getDefaultIRPFRegime(ctx));
		mod130.putAmount(Mod130Key.P2, (AppParamDAO.isPermAddressChanges(ctx)?1:0) );
		return super.initializeModel(ctx, mod130);
	}
	
	Mod130 calculate(AONContext ctx, Mod130 mod130) {
		basicCalculate(ctx,mod130);
		return mod130;
	}

	private static boolean isFarmer(IrpfBreakdown br) {
		return br.isFromInvoice() 
			&& br.getWithholdingType() == WithholdingType.FARMER
			&& (br.getIRPFRegime() == null 
				|| br.getIRPFRegime() == IRPFRegime.NORMAL 
				|| br.getIRPFRegime() == IRPFRegime.SIMPLIFIED);
	}
	private static boolean isNotFarmer(IrpfBreakdown br) {
		return br.isFromInvoice() 
			&& br.getWithholdingType() != WithholdingType.FARMER
			&& (br.getIRPFRegime() == null 
				|| br.getIRPFRegime() == IRPFRegime.NORMAL 
				|| br.getIRPFRegime() == IRPFRegime.SIMPLIFIED);
	}

	private static Stream<AccountingBreakdown> getInitialBaseC01(AONContext ctx, final Mod130 mod) {
		return AccountEntryDAO.getAccountingBreakdown(ctx,
				p -> p.getDomainProperty().eq(ctx.getDomainId())
					.and(p.getEntryDateProperty().ge(AonDateUtils.getYearFirstDay(mod.getYear())))
					.and(p.getEntryDateProperty().le(FiscalUtils.getPeriodEnd(mod)))
					.and(p.getAccountCodeProperty().like("7%"))
					)
			.filter( br -> (!br.hasActivity() || (!br.isFarmer() && (br.isNormalRegime() || br.isSimplifiedRegime())) ));
	}
	private static double getRawC01(AONContext ctx, final Mod130 mod) {
		double rawC01 = getInitialBaseC01(ctx, mod)
				.mapToDouble(br -> br.getCreditBalance())
				.sum();
		return AonMathUtils.round(rawC01);
	}
	private static double getInitialC01(AONContext ctx, final Mod130 mod) {
		double c01 = getRawC01(ctx, mod);  
		double percent = mod.getAmount(Mod130Key.P1);
		c01 = AonMathUtils.round(c01 * percent / 100 );
		return c01; 
	}
	
	private static Stream<AccountingBreakdown> getInitialBaseC02(AONContext ctx, final Mod130 mod) {
		return AccountEntryDAO.getAccountingBreakdown(ctx,
				p -> p.getDomainProperty().eq(ctx.getDomainId())
					.and(p.getEntryDateProperty().ge(AonDateUtils.getYearFirstDay(mod.getYear())))
					.and(p.getEntryDateProperty().le(FiscalUtils.getPeriodEnd(mod)))
					.and(p.getAccountCodeProperty().like("6%"))
					)
			.filter( br -> (!br.hasActivity() || (!br.isFarmer() && (br.isNormalRegime() || br.isSimplifiedRegime()))));
	}
	private static double getRawC02(AONContext ctx, final Mod130 mod) {
		return getInitialBaseC02(ctx, mod)
				.mapToDouble(br -> br.getDebitBalance())
				.sum();
	}
	
	private static double getInitialC02(AONContext ctx, final Mod130 mod) {
		double c02 = getRawC02(ctx, mod);
		if (mod.getRegime() != null && mod.getRegime() == IRPFRegime.SIMPLIFIED) { 
			double c01 = getRawC01(ctx, mod);
			double c02_ = AonMathUtils.round( c01 - c02);
			if (c02_ > 0 ) {
				double dif = (c02_* 7 /100);
				if (dif > LIMITE_GASTOS_DIF_JUST ) {
					dif = LIMITE_GASTOS_DIF_JUST;
				}
				c02 = AonMathUtils.round( c02 + dif ); 
			}
		}
		double percent = mod.getAmount(Mod130Key.P1);
		c02 = AonMathUtils.round(c02 * percent / 100 );
		return c02;
	}

	private static double getInitialC05(AONContext ctx, final Mod130 mod) {
		LinkedList<FiscalModel> list = Mod130DAO.getPreviousEffectiveModels(ctx, mod)
				.collect(Collectors.toCollection(LinkedList::new));
		double c05 = 0;
		for (FiscalModel fm : list) {
			double c007 = fm.getAmount(Mod130Key.C07);
			c007 = c007 < 0 ? 0.0 : c007;
			c05 = AonMathUtils.round( c05 + ( c007 - fm.getAmount(Mod130Key.C16)));
		}
		return c05;
	}
	
	private static Stream<AccountingBreakdown> getInitialBaseC08(AONContext ctx, final Mod130 mod) {
		return AccountEntryDAO.getAccountingBreakdown(ctx,
				p -> p.getDomainProperty().eq(ctx.getDomainId())
				.and(p.getEntryDateProperty().ge(FiscalUtils.getPeriodStart(mod)))
				.and(p.getEntryDateProperty().le(FiscalUtils.getPeriodEnd(mod)))
				.and(p.getAccountCodeProperty().like("7%"))
				)
			.filter( br -> (br.isFarmer() && !br.isObjectiveRegime()));
	}
	private static double getRawC08(AONContext ctx, final Mod130 mod) {
		return getInitialBaseC08(ctx, mod)
			.mapToDouble(br -> br.getCreditBalance())
			.sum();
	}
	private static double getInitialC08(AONContext ctx, final Mod130 mod) {
		double c08 = getRawC08(ctx, mod); 
		double percent = mod.getAmount(Mod130Key.P1);
		c08 = AonMathUtils.round(c08 * percent / 100 );
		return c08; 
	}

	private static double getInitialC13(AONContext ctx, final Mod130 mod) {
		double c13 = 0.0;
		Mod130 previousModels = Mod130DAO.getMod130s(ctx, mod.getDomain())
		 .filter(model -> model.getYear() == (mod.getYear() - 1))
		 .filter(model -> model.getPeriod() == Period.T4)
		 .findFirst()
		 .orElse(null);
		
		if (previousModels != null) {
			Mod130 previous = Mod130DAO.get(ctx, previousModels.getId());
			double c03 = previous.getAmount(Mod130Key.C03);
			double c08 = (previous.getAmount(Mod130Key.C08) * 25 / 100);
			double rn = AonMathUtils.round(c03 + c08);
			if (rn <= 9000) {
				c13 = 100;
			} else if (rn > 9000 && rn <= 10000) {
				c13 = 75;
			} else if (rn > 10000 && rn <= 11000) {
				c13 = 50;
			} else if (rn > 11000 && rn <= 12000) {
				c13 = 25;
			}
		}
		return c13;
	}
	
	private static double getInitialC15(AONContext ctx, final Mod130 mod) {
		double c14 = mod.getAmount(Mod130Key.C14);
		double c15 = 0.0; 
		if (c14 > 0) {
			MutableDouble x19 = new MutableDouble();
			MutableDouble x15 = new MutableDouble();
			Mod130DAO.getPreviousModels(ctx, mod)
				.forEach(fm -> {
					x19.add( AonMathUtils.zeroIfPositive(fm.getAmount(Mod130Key.C19)) );
					x15.add( fm.getAmount(Mod130Key.C15) );
				})
			;
			double t19 = AonMathUtils.absRounded(x19.doubleValue());
			double t15 = x15.doubleValue(); 
			c15 = t19 - t15;
			c15 = AonMathUtils.zeroIfNegative(c15);
			c15 = c15>c14?c14:c15;
		}
		return AonMathUtils.absRounded(c15);
	}
	
	@Override
	Stream<AccountingBreakdown> getAccountInfoInfo(AONContext ctx, Mod130 mod, IModelScript<Mod130Key> script, IMod130KeyDAO keyDAO) {
		if (keyDAO.getKey() == Mod130Key.C01) {
			return getInitialBaseC01(ctx, mod);
		} else if (keyDAO.getKey() == Mod130Key.C02) {
			return getInitialBaseC02(ctx, mod);
		} else if (keyDAO.getKey() == Mod130Key.C08) {
			return getInitialBaseC08(ctx, mod);
		}
		return Stream.empty();
	}

	private static String getC01ComputeKeyInfo(AONContext ctx, Mod130 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod130Key.C01, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				return new StringBuilder()
				.append("<table>")
					.append("<tr>")
						.append( MessageFormat.format(styledTag, "td", noWrap) )
							.append( "<li>" )
								.append("Desde contabilidad, saldo acreedor de las cuentas del grupo 7 desde el <b>")
								.append( DeclarationInfoUtil.FMT.format(AonDateUtils.getYearFirstDay(mod.getYear())) )
								.append("</b> al <b>")
								.append( DeclarationInfoUtil.FMT.format(FiscalUtils.getPeriodEnd(mod)))
								.append("</b>")
							.append("</li>")
						.append("</td>")
						.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+fontMedium+bold, DEC2.format( getRawC01(ctx, mod))))
					.append("</tr>")
					
					.append("<tr>")
						.append( MessageFormat.format(styledTag, "td", noWrap) )
							.append("<li>Porcentaje de participación</li>")
							.append("</td>")
						.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+fontMedium+bold, DEC2.format( mod.getAmount( Mod130Key.P1 )) + "%"))
					.append("</tr>")
					
					.append("<tr>")
						.append( MessageFormat.format(styledTag, "td", noWrap) )
							.append("<li>Resultado</li>")
							.append("</td>")
						.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+textUnderline+fontMedium+bold, DEC2.format( mod.getAmount( Mod130Key.C01 ))))
					.append("</tr>")
				.append("</table>")
				.toString();
			}
		});	
	}
	
	private static String getC02ComputeKeyInfo(AONContext ctx, Mod130 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod130Key.C02, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				StringBuilder buf = new StringBuilder();
				double rawC02 = getRawC02(ctx,mod);
				buf.append("<table>")
					.append("<tr>")
						.append( MessageFormat.format(styledTag, "td", noWrap) )
							.append( "<li>" )
								.append("Desde contabilidad, saldo deudor de las cuentas del grupo 6 desde el <b>")
								.append( DeclarationInfoUtil.FMT.format(AonDateUtils.getYearFirstDay(mod.getYear())) )
								.append("</b> al <b>")
								.append( DeclarationInfoUtil.FMT.format(FiscalUtils.getPeriodEnd(mod)))
								.append("</b>")
							.append("</li>")
						.append("</td>")
						.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+fontMedium+bold, DEC2.format(rawC02)))
					.append("</tr>");
				
				if (AonNumberUtils.equals(1,mod.getAmount( Mod130Key.P0))) {
					double rawC01 = getRawC01(ctx,mod);
					double c02p = AonMathUtils.round(rawC01 - rawC02);
					buf.append("<tr>")
						.append(MessageFormat.format(fullStyledTag, "td colspan=\"2\"", textUnderline,
							"R\u00E9gimen de determinaci\u00F3n de rendimientos: Estimaci\u00F3n directa simplificada."))
					.append("</tr>")
					
					.append("<tr>")
						.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+noWrap,
							"<li>Rendimiento neto previo  ("+ DEC2.format( rawC01 )+ " - "  + DEC2.format( rawC02)+ ")</li>"))
						.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+fontMedium+bold, DEC2.format(c02p)))
					.append("</tr>")
					;
					if ( AonMathUtils.isLessThanZero(c02p) ) {
						buf.append("<tr>")
							.append(MessageFormat.format(fullStyledTag, "td colspan=\"2\"", textUnderline,
								"Al ser el rendimiento neto previo menor que cero, no se aplican los gastos de dif\u00EDcil justificaci\u00F3n"))
						.append("</tr>");
					} else {
						buf.append("<tr>")
							.append(MessageFormat.format(fullStyledTag, "td", textCenter,
								"- Al no ser negativo el rendimiento neto previo, se procede a la aplicaci\u00F3n del 7% de gastos de dif\u00EDcil justificaci\u00F3n"))
						.append("</tr>");
						
						double c02a = AonMathUtils.round(c02p * 7 / 100);
						buf.append("<tr>")
							.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+noWrap,
									"<li>7% de "+ DEC2.format( c02p ) + "</li>"))
							.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+fontMedium+bold, DEC2.format(c02a)))
						.append("</tr>");
						if (AonMathUtils.isGreatherThan(c02a, LIMITE_GASTOS_DIF_JUST) ) {
							c02a = LIMITE_GASTOS_DIF_JUST;
							buf.append("<tr>")
								.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+noWrap,
										"<li>Se supera el l\u00EDmite de "+ DEC2.format( LIMITE_GASTOS_DIF_JUST ) + " euros. Se aplica el l\u00EDmite.</li>"))
								.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+fontMedium+bold, DEC2.format(c02a)))
							.append("</tr>");
						}
						double c02b = AonMathUtils.round(c02a + rawC02);
						buf.append("<tr>")
							.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+noWrap,
									"<li>Saldo más gastos de dif\u00EDcil justificaci\u00F3n (" + DEC2.format( rawC02 ) + " + " + DEC2.format( c02a ) +")</li>"))
							.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+fontMedium+bold, DEC2.format(c02b)))
						.append("</tr>");
					}
				}
				
				buf.append("<tr>")
						.append( MessageFormat.format(styledTag, "td", noWrap) )
							.append("<li>Porcentaje de participación</li>")
							.append("</td>")
						.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+fontMedium+bold, DEC2.format( mod.getAmount( Mod130Key.P1 )) + "%"))
					.append("</tr>")
					
					.append("<tr>")
						.append(MessageFormat.format(fullStyledTag, "td", noWrap,"<li>Resultado</li>"))
						.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+textUnderline+fontMedium+bold, DEC2.format( mod.getAmount( Mod130Key.C02 ))))
					.append("</tr>")
				.append("</table>");
				
				return buf.toString();
			}
		});	
	}
	
	private static String getC04ComputeKeyInfo(AONContext ctx, Mod130 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod130Key.C02, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				StringBuilder buf = new StringBuilder();
				if ( AonMathUtils.isGreatherThanZero( mod.getAmount(Mod130Key.C03) )) {
					buf.append("<table>")
						.append("<tr>")
							.append( MessageFormat.format(styledTag, "td", noWrap) )
								.append( "<li>" )
									.append("Al ser la casilla [003] mayor que cero, el 20% de <b>")
									.append( DEC2.format( mod.getAmount( Mod130Key.C03 )) )
									.append("</b>")
								.append("</li>")
							.append("</td>")
							.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+fontMedium+bold, DEC2.format( mod.getAmount( Mod130Key.C04 ))))
						.append("</tr>")
					.append("</table>");
				} else {
					buf.append("<table>")
						.append("<tr>")
							.append( MessageFormat.format(styledTag, "td", noWrap) )
								.append( "<li>" )
									.append("Al no ser la casilla [003] mayor que cero, el resultado es cero.")
								.append("</li>")
							.append("</td>")
							.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+fontMedium+bold, DEC2.format( mod.getAmount( Mod130Key.C04 ))))
						.append("</tr>")
					.append("</table>");
				}
				
				return buf.toString();
			}
		});	
	}
	
	private static String getC05ComputeKeyInfo(AONContext ctx, Mod130 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod130Key.C05, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				StringBuilder buf = new StringBuilder();
				buf.append("<table>")
					.append("<tr>")
						.append(MessageFormat.format(fullStyledTag, "td colspan=\"2\"", textUnderline,
								"Declaraciones de trimestres anteriores:"))
						.append("</tr>");
				long count = Mod130DAO.getPreviousEffectiveModels(ctx, (Mod130) fm)
				 .map(m -> {
					 double c007 = AonMathUtils.zeroIfNegative(m.getAmount(Mod130Key.C07));
					 double c016 = m.getAmount(Mod130Key.C16);
					 double c05 = AonMathUtils.round(c007 - m.getAmount(Mod130Key.C16));
					 buf.append(MessageFormat.format(styledTag, "tr", marginTop))
					 	.append(MessageFormat.format(fullStyledTag, "td colspan=\"2\"", noWrap, "<li> " + m.getModelFullName()+"</li>"))
					.append("</tr>")
					.append("<tr>")
						.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+noWrap,
								"<li> Casilla 007: </li>"))
						.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+bold, DEC2.format(m.getAmount(Mod130Key.C07))))
					.append("</tr>")
					.append("<tr>")
						.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+noWrap,
							"<li> Casilla 016: </li>"))
						.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+bold, DEC2.format(c016)))
					.append("</tr>")
					.append("<tr>")
						.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+noWrap,
							"<li> A deducir trimestre: </li>"))
						.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+bold, DEC2.format(c05)))
					.append("</tr>");
					 return m;
				 })
				 .count();
				if (count == 0) {
					buf.append("<tr>")
						.append(MessageFormat.format(fullStyledTag, "td colspan=\"2\"", paddingLeft+noWrap,
							"<li> No hay declaraciones anteriores</li>"))
					.append("</tr>");
				}
				buf.append("<tr>")
					.append( MessageFormat.format(styledTag, "td", noWrap+fontMedium) )
						.append("<li>Resultado</li>")
						.append("</td>")
					.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+textUnderline+fontMedium+bold, DEC2.format( mod.getAmount( Mod130Key.C05 ))))
				.append("</tr>")
				;
				return buf
					.append("</table>")
					.toString();
			}
		});	
	}
	
	
	private static String getC08ComputeKeyInfo(AONContext ctx, Mod130 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod130Key.C08, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				return new StringBuilder()
				.append("<table>")
					.append("<tr>")
						.append( MessageFormat.format(styledTag, "td", textLeft) )
							.append( "<li>" )
								.append("Desde contabilidad, saldo acreedor de las cuentas del grupo 7 desde el <b>")
								.append( DeclarationInfoUtil.FMT.format(AonDateUtils.getYearFirstDay(mod.getYear())) )
								.append("</b> al <b>")
								.append( DeclarationInfoUtil.FMT.format(FiscalUtils.getPeriodEnd(mod)))
								.append("</b> de las actividades agr\u00EDcolas (la actividad del apunte contable debe ser agr\u00EDcola)")
							.append("</li>")
						.append("</td>")
						.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+fontMedium+bold, DEC2.format( getRawC08(ctx, mod))))
					.append("</tr>")
					
					.append("<tr>")
						.append( MessageFormat.format(styledTag, "td", noWrap) )
							.append("<li>Porcentaje de participación</li>")
							.append("</td>")
						.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+fontMedium+bold, DEC2.format( mod.getAmount( Mod130Key.P1 )) + "%"))
					.append("</tr>")
					
					.append("<tr>")
						.append( MessageFormat.format(styledTag, "td", noWrap) )
							.append("<li>Resultado</li>")
							.append("</td>")
						.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+textUnderline+fontMedium+bold, DEC2.format( mod.getAmount( Mod130Key.C08 ))))
					.append("</tr>")
				.append("</table>")
				.toString();
			}
		});	
	}
	
	private static String getC12ComputeKeyInfo(AONContext ctx, Mod130 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod130Key.C12, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				StringBuilder buf = new StringBuilder();
				double c07 = fm.getAmount(Mod130Key.C07);
				double c11 = fm.getAmount(Mod130Key.C11);
				double c12 = AonMathUtils.round(c07+c11);
				buf.append("<table>")
					.append("<tr>");
				if ( AonMathUtils.isGreatherThanZero( c12 )) {
					buf.append( MessageFormat.format(styledTag, "td", noWrap) )
						.append( "<li>" )
							.append("Suma de la casilla 07 (<b>")
							.append( DEC2.format( mod.getAmount( Mod130Key.C07 )) )
							.append(") </b>")
							.append(" y la casilla 11 (<b>")
							.append( DEC2.format( mod.getAmount( Mod130Key.C11 )) )
							.append(") </b>")
						.append("</li>")
					.append("</td>");
				} else {
					buf.append( MessageFormat.format(styledTag, "td", noWrap) )
						.append( "<li>" )
							.append("Al ser la suma de la casilla 07 (<b>")
							.append( DEC2.format( mod.getAmount( Mod130Key.C07 )) )
							.append(") </b>")
							.append(" y la casilla 11 (<b>")
							.append( DEC2.format( mod.getAmount( Mod130Key.C11 )) )
							.append(") negativa, se consigna cero</b>")
						.append("</li>")
					.append("</td>");
				}
				buf.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+fontMedium+bold, DEC2.format( mod.getAmount( Mod130Key.C12 ))))
					.append("</tr>")
				.append("</table>");
				
				return buf.toString();
			}
		});	
	}
	
	private static String getC15ComputeKeyInfo(AONContext ctx, Mod130 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod130Key.C15, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				StringBuilder buf = new StringBuilder();
				
				buf.append("<table>")
				.append("<tr>")
					.append(MessageFormat.format(fullStyledTag, "td colspan=\"2\"", textUnderline,
							"Declaraciones de trimestres anteriores:"))
					.append("</tr>");
				double c14 = fm.getAmount(Mod130Key.C14);
				if ( !AonMathUtils.isGreatherThanZero(c14) ) {
					buf.append("<tr>")
						.append( MessageFormat.format(styledTag, "td colspan=\"2\"", noWrap) )
							.append("<li>Al ser la casilla 14 " + (AonMathUtils.isZero(c14)?"cero":"negativa") +" , se consigna cero</li>")
						.append("</td>");
				} else {
					MutableDouble x19 = new MutableDouble();
					MutableDouble x15 = new MutableDouble();
					// long count = 
					Mod130DAO.getPreviousModels(ctx, (Mod130) fm)
						.forEach(m -> {
							 double c19 = m.getAmount(Mod130Key.C19);
							 double c15 = m.getAmount(Mod130Key.C15);
							 x19.add( AonMathUtils.zeroIfPositive(c19) );
							 x15.add( c15 );
							 buf.append(MessageFormat.format(styledTag, "tr", marginTop))
							 	.append(MessageFormat.format(fullStyledTag, "td colspan=\"2\"", noWrap, "<li> " + m.getModelFullName()+"</li>"))
							.append("</tr>")
							.append("<tr>")
								.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+noWrap,"<li> Casilla 019: </li>"))
								.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+bold, DEC2.format(c19)))
							.append("</tr>")
							.append("<tr>")
								.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+noWrap,"<li> Casilla 015: </li>"))
								.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+bold, DEC2.format(c15)))
							.append("</tr>");
							 //return m;
						})
						//.count()
						;
					int count = 3;
					if (count == 0) {
						buf.append("<tr>")
							.append(MessageFormat.format(fullStyledTag, "td colspan=\"2\"", paddingLeft+noWrap,"<li> No hay declaraciones anteriores</li>"))
						.append("</tr>");
					} else {
						buf.append("<tr>")
							.append( MessageFormat.format(fullStyledTag, "td", noWrap+fontMedium,"<li>Suma de los valores negativos de las casillas 19</li>"))
							.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+bold, DEC2.format( x19.doubleValue() )))
						.append("</tr>");
						buf.append("<tr>")
							.append( MessageFormat.format(fullStyledTag, "td", noWrap+fontMedium,"<li>Suma de los valores de las casillas 15</li>"))
							.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+bold, DEC2.format( x15.doubleValue() )))
						.append("</tr>");
						double c15 = AonMathUtils.zeroIfNegative(x19.getValue() - x15.getValue());
						buf.append("<tr>")
							.append( MessageFormat.format(fullStyledTag, "td", noWrap+fontMedium,"<li>Sumatorio [019] - Sumatorio [015] ("
								+ DEC2.format( x19.getValue() )
								+ " - "
								+ DEC2.format( x15.getValue() )
								+ ") </li>"))
							.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+textUnderline+fontMedium+bold, DEC2.format( c15 )))
						.append("</tr>");
						if (c15 > c14) {
							c15 = c14;
							buf.append("<tr>")
								.append( MessageFormat.format(fullStyledTag, "td", noWrap+fontMedium,"<li>Al ser el c\u00E1lculo mayor que [014] se asigna el valor de [014]</li>"))
								.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+bold, DEC2.format( c15 )))
							.append("</tr>");
						}
					}
				}
				buf.append("<tr>")
					.append( MessageFormat.format(fullStyledTag, "td", noWrap+fontMedium,"<li>Resultado</li>"))
					.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+bold, DEC2.format( mod.getAmount( Mod130Key.C15 ))))
				.append("</tr>")
				;
				return buf.toString();
				}
			});	
	}
	
	private static String getC16ComputeKeyInfo(AONContext ctx, Mod130 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod130Key.C16, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				StringBuilder buf = new StringBuilder();
				double p2 = mod.getAmount(Mod130Key.P2);
				double c14 = mod.getAmount(Mod130Key.C14);
				buf.append("<table>");
				if (p2 == 0) {
					buf.append("<tr>")
						.append(MessageFormat.format(fullStyledTag, "td colspan=\"2\"", noWrap,
							"<li> NO se han realizado pagos por pr\u00E9stamos destinados a la adquisici\u00F3n o rehabilitaci\u00F3n de su vivienda habitual</li>"))
					.append("</tr>");
				} else {
					buf.append("<tr>")
						.append(MessageFormat.format(fullStyledTag, "td colspan=\"2\"", noWrap,
							"<li> Se han realizado pagos por pr\u00E9stamos destinados a la adquisici\u00F3n o rehabilitaci\u00F3n de su vivienda habitual</li>"))
					.append("</tr>");
					if (AonMathUtils.isNegative(c14)) {
						buf.append("<tr>")
							.append(MessageFormat.format(fullStyledTag, "td colspan=\"2\"", noWrap,
							"<li> La casilla [014] tiene valor negativo</li>"))
						.append("</tr>");
					} else {
						double c03 = mod.getAmount(Mod130Key.C03);
						double c08 = mod.getAmount(Mod130Key.C08);
//						if (c03 > 0 || c08 > 0) {
							double cXX = c03>c08?c03:c08; 
							buf.append("<tr>")
								.append( MessageFormat.format(fullStyledTag, "td", noWrap+fontMedium,
									"<li>Se toma el mayor valor entre las casillas [003] y [008]:" 
									+ DEC2.format( cXX )
									+"</li>"))
								.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+bold, DEC2.format( cXX )))
							.append("</tr>");
							double cYY = AonMathUtils.round( cXX * 2 / 100);
							buf.append("<tr>")
								.append( MessageFormat.format(fullStyledTag, "td", noWrap+fontMedium,
									"<li>2% de " + DEC2.format( cXX ) +"</li>"))
								.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+bold, DEC2.format( cYY )))
							.append("</tr>");
							double c15 = mod.getAmount(Mod130Key.C15);
							if (cYY > AonMathUtils.round(c14 - c15)) {
								cYY = AonMathUtils.round(c14 - c15);
								cYY = AonMathUtils.isLessThanZero(cYY)?0.0:cYY;
								buf.append("<tr>")
									.append( MessageFormat.format(fullStyledTag, "td", noWrap+fontMedium,
										"<li>El importe consignado en la casilla [016] no podr\u00E1 ser superior a la diferencia positiva entre las casillas [014] y [015].</li>"))
									.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+bold, DEC2.format( cYY )))
								.append("</tr>");
							}
							if (cYY > C16_MAX_VALUE) {
								cYY = C16_MAX_VALUE;
								buf.append("<tr>")
									.append( MessageFormat.format(fullStyledTag, "td", noWrap+fontMedium,
										"<li>Se aplica el l\u00EDmite m\u00E1ximo de "+ DEC2.format( C16_MAX_VALUE ) +"</li>"))
									.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+bold, DEC2.format( cYY )))
								.append("</tr>");
							}
//						}
					}
				}
				buf.append("<tr>")
					.append( MessageFormat.format(fullStyledTag, "td", noWrap+fontMedium,"<li>Resultado</li>"))
					.append(MessageFormat.format(fullStyledTag, "td", paddingLeft+textRight+bold, DEC2.format( mod.getAmount( Mod130Key.C16 ))))
				.append("</tr>")
				;
				buf.append("</table>");
				return buf.toString();
			}
		});	
	}
	
	private static String getC18ComputeKeyInfo(AONContext ctx, Mod130 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod130Key.C18, Mod130DAO.getSamePeriodFiscalModels(ctx, mod), new ExplainRowManager());	
	}
	
}
