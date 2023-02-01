package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod111;

import java.util.Map;
import java.util.Set;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class Mod111AEAT2021Declaration extends Mod111Declaration {
	
	public static boolean accept(Mod111 mod) {
		return mod.isAEAT() && mod.getYear() < 2023; 
	}
	
	private enum Mod111KeyDAO  implements IMod111KeyDAO{
		 CT_C01(Mod111Key.CT_C01,false
			, (mod,br) ->  br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.CT_C01,mod,docs,pdocs,br)
			,null,null,null)
		,CT_C02(Mod111Key.CT_C02,true
			, (mod,br) ->  br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.CT_C02,mod,br)
			,null,null,null)
		,CT_C03(Mod111Key.CT_C03,true
			, (mod,br) ->  br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.CT_C03,mod,br)
			,null,null,null)
		,CT_C04(Mod111Key.CT_C04,false
			, (mod,br) ->  br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.CT_C04,mod,docs,pdocs,br)
			,null,null,null)
		,CT_C05(Mod111Key.CT_C05,true
			, (mod,br) ->  br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.CT_C05,mod,br)
			,null,null,null)
		,CT_C06(Mod111Key.CT_C06,true
			, (mod,br) ->  br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.CT_C06,mod,br)
			,null,null,null)
		,CT_C07(Mod111Key.CT_C07,false
			, (mod,br) -> isProfessional(br) || isFarmer(br) || isTransportOperator(br)
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.CT_C07,mod,docs,pdocs,br)
			,null,null,null)
		,CT_C08(Mod111Key.CT_C08,true
			, (mod,br) -> isProfessional(br) || isFarmer(br) || isTransportOperator(br)
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.CT_C08,mod,br)
			,null,null,null)
		,CT_C09(Mod111Key.CT_C09,true
			, (mod,br) -> isProfessional(br) || isFarmer(br) || isTransportOperator(br)
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.CT_C09,mod,br)
			,null,null,null)
		,CT_C10(Mod111Key.CT_C10,false,null,null,null,null,null)
		,CT_C11(Mod111Key.CT_C11,true ,null,null,null,null,null)
		,CT_C12(Mod111Key.CT_C12,true ,null,null,null,null,null)
		,CT_C13(Mod111Key.CT_C13,false,null,null,null,null,null)
		,CT_C14(Mod111Key.CT_C14,true ,null,null,null,null,null)
		,CT_C15(Mod111Key.CT_C15,true ,null,null,null,null,null)
		,CT_C16(Mod111Key.CT_C16,false,null,null,null,null,null)
		,CT_C17(Mod111Key.CT_C17,true ,null,null,null,null,null)
		,CT_C18(Mod111Key.CT_C18,true ,null,null,null,null,null)
		,CT_C19(Mod111Key.CT_C19,false,null,null,null,null,null)
		,CT_C20(Mod111Key.CT_C20,true ,null,null,null,null,null)
		,CT_C21(Mod111Key.CT_C21,true ,null,null,null,null,null)
		,CT_C22(Mod111Key.CT_C22,false,null,null,null,null,null)
		,CT_C23(Mod111Key.CT_C23,true ,null,null,null,null,null)
		,CT_C24(Mod111Key.CT_C24,true ,null,null,null,null,null)
		,CT_C25(Mod111Key.CT_C25,false,null,null,null,null,null)
		,CT_C26(Mod111Key.CT_C26,true ,null,null,null,null,null)
		,CT_C27(Mod111Key.CT_C27,true ,null,null,null,null,null)
		,CT_C28(Mod111Key.CT_C28,false,null,null,null, "CT_C03+CT_C06+CT_C09+CT_C12+CT_C15+CT_C18+CT_C21+CT_C24",null)
		,CT_C29(Mod111Key.CT_C29,false, null, null
			, (ctx,mod) -> mod.putAmount(Mod111Key.CT_C29,mod.isComplementary()
				?Mod111DAO.getSamePeriodModels(ctx, mod).mapToDouble(Mod111::getDeclarationResult).sum()
				:0.0)
			,null,null)
		,CT_C30(Mod111Key.CT_C30,false,null,null,null,"CT_C28-CT_C29",null)
		,CT_TIP(Mod111Key.CT_TIP,false,null,null,null,null,null)
		;
		
		private Mod111Key key;
		private boolean diffEnabled;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private IValueUniqueIntializer uniqueInitializer;
		private String expression;
		private String template;
	
		private Mod111KeyDAO(Mod111Key key
				, boolean diffEnabled
				, IValueAccepter acceptValue
				, IValueIntializer initializer
				, IValueUniqueIntializer uniqueInitializer
				, String expression
				, String template) {
			this.key = key;
			this.diffEnabled = diffEnabled;
			this.acceptValue =  acceptValue;
			this.initializer = initializer;
			this.uniqueInitializer = uniqueInitializer;
			this.expression =  expression;
			this.template =  template;
		}
		
		@Override
		public Mod111Key getKey() {
			return key;
		}
		@Override
		public boolean isDiffEnabled() {
			return diffEnabled;
		}
		@Override
		public boolean acceptValue(Mod111 mod,IrpfBreakdown  br) {
			return  acceptValue != null &&  acceptValue.accept(mod,br);
		}
		@Override
		public void initialize(AONContext ctx,Mod111 mod,Map<Mod111Key,Set<String>> docs
				,Map<Mod111Key,Set<String>> pdocs,IrpfBreakdown  br) {
			if (initializer != null) {
				initializer.initialize(ctx, mod, docs, pdocs, br);
			}
		}
		@Override
		public void uniqueInitialize(AONContext ctx,Mod111 mod) {
			if (uniqueInitializer != null) {
				uniqueInitializer.initialize(ctx, mod);
			}
		}
		@Override
		public String getExpression() {
			return expression;
		}
		
		@Override
		public String getTemplate() {
			return template;
		}
	}

	@Override
	IMod111KeyDAO valueOf(String string) {
		return Mod111KeyDAO.valueOf(string);
	}

	@Override
	IMod111KeyDAO[] getKeys() {
		return Mod111KeyDAO.values();
	}

	@Override
	double getResult(Mod111 mod) {
		return mod.getAmount(Mod111Key.CT_C30);
	}
	
	@Override
	ComplementaryBeahaviour getComplementaryBehaviour(Mod111 mod) {
		return ComplementaryBeahaviour.REPLACEMENT;
	}

	@Override
	Mod111 initialize(AONContext ctx, Mod111 mod111) {
		mod111.setComplementaryDeclarationAvailable(true);
		mod111.setReplacementDeclarationAvailable(false);
		return super.initializeModel(ctx, mod111);
	}

	@Override
	public Mod111Key[] getSamePeriodExplainKeys() {
		return new Mod111Key[] {Mod111Key.CT_C29};
	}
	
	private static boolean isProfessional(IrpfBreakdown br) {
		return br.isFromInvoice() && br.getWithholdingType() == WithholdingType.PROFESSIONAL;
	}
	private static boolean isFarmer(IrpfBreakdown br) {
		return br.isFromInvoice() && br.getWithholdingType() == WithholdingType.FARMER;
	}
	private static boolean isTransportOperator(IrpfBreakdown br) {
		return br.isFromInvoice() &&  br.getWithholdingType() == WithholdingType.TRANSPORT_OPERATOR;
	}

}