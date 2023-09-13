package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod111;

import java.util.Map;
import java.util.Set;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class Mod111Gipuzkoa2021Declaration extends Mod111Declaration {
	
	public static boolean accept(Mod111 mod) {
		return mod.isGipuzkoa() && mod.getYear() < 2023; 
	}

	private enum Mod111KeyDAO  implements IMod111KeyDAO{
		 GP_X00 (Mod111Key.GP_X00,false
			, null
			, null
			, Mod111Declaration::addDeponentDocument
			, null,null)
		,GP_C01(Mod111Key.GP_C01,false
			, (mod,br) -> br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.GP_C01,mod,docs,pdocs,br)
			,null,null,null)
		,GP_C02(Mod111Key.GP_C02,true
			, (mod,br) -> br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.GP_C02,mod,br)
			,null,null,null)
		,GP_C03(Mod111Key.GP_C03,true
			, (mod,br) -> br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.GP_C03,mod,br)
			,null,null,null)
		,GP_C04(Mod111Key.GP_C04,false
			, (mod,br) -> isProfessional(br) || isTransportOperator(br)
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.GP_C04,mod,docs,pdocs,br)
			,null,null,null)
		,GP_C05(Mod111Key.GP_C05,true
			, (mod,br) -> isProfessional(br) || isTransportOperator(br)
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.GP_C05,mod,br)
			,null,null,null)
		,GP_C06(Mod111Key.GP_C06,true
			, (mod,br) -> isProfessional(br) || isTransportOperator(br)
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.GP_C06,mod,br)
			,null,null,null)
		,GP_C07(Mod111Key.GP_C07,false
			, (mod,br) -> isFarmer(br)
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.GP_C07,mod,docs,pdocs,br)
			,null,null,null)
		,GP_C08(Mod111Key.GP_C08,true
			, (mod,br) -> isFarmer(br)
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.GP_C08,mod,br)
			,null,null,null)
		,GP_C09(Mod111Key.GP_C09,true
			, (mod,br) -> isFarmer(br)
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.GP_C09,mod,br)
			,null,null,null)
		,GP_C10(Mod111Key.GP_C10,false,null,null,null,null,null)
		,GP_C11(Mod111Key.GP_C11,true ,null,null,null,null,null)
		,GP_C12(Mod111Key.GP_C12,true ,null,null,null,null,null)
		,GP_C13(Mod111Key.GP_C13,false,null,null,null, "GP_C02+GP_C05+GP_C08+GP_C11",null)
		,GP_C14(Mod111Key.GP_C14,false,null,null,null, "GP_C03+GP_C06+GP_C09+GP_C12",null)
		,GP_C15(Mod111Key.GP_C15,false
			, (mod,br) -> br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.GP_C15,mod,docs,pdocs,br)
			,null,null,null)
		,GP_C16(Mod111Key.GP_C16,true
			, (mod,br) -> br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.GP_C16,mod,br)
			,null,null,null)
		,GP_C17(Mod111Key.GP_C17,true
			, (mod,br) -> br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.GP_C17,mod,br)
			,null,null,null)
		,GP_C18(Mod111Key.GP_C18,false,null,null,null,null,null)
		,GP_C19(Mod111Key.GP_C19,true ,null,null,null,null,null)
		,GP_C20(Mod111Key.GP_C20,true ,null,null,null,null,null)
		,GP_C21(Mod111Key.GP_C21,false,null,null,null,null,null)
		,GP_C22(Mod111Key.GP_C22,true ,null,null,null,null,null)
		,GP_C23(Mod111Key.GP_C23,true ,null,null,null,null,null)
		,GP_C24(Mod111Key.GP_C24,false,null,null,null,null,null)
		,GP_C25(Mod111Key.GP_C25,true ,null,null,null,null,null)
		,GP_C26(Mod111Key.GP_C26,true ,null,null,null,null,null)
		,GP_C27(Mod111Key.GP_C27,false,null,null,null, "GP_C16+GP_C19+GP_C22+GP_C25",null)
		,GP_C28(Mod111Key.GP_C28,false,null,null,null, "GP_C17+GP_C20+GP_C23+GP_C26",null)
		,GP_C29(Mod111Key.GP_C29,false,null,null,null, "GP_C14+GP_C28",null)
		,GP_TIP(Mod111Key.GP_TIP,false,null,null,null,null,null)
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
		return mod.getAmount(Mod111Key.GP_C29);
	}
	
	@Override
	ComplementaryBeahaviour getComplementaryBehaviour(Mod111 mod) {
		return ComplementaryBeahaviour.COMPLEMENTARY;
	}

	@Override
	Mod111 initialize(AONContext ctx, Mod111 mod111) {
		mod111.setComplementaryDeclarationAvailable(true);
		mod111.setReplacementDeclarationAvailable(false);
		return super.initializeModel(ctx, mod111);
	}
	
	@Override
	public Mod111Key[] getSamePeriodExplainKeys() {
		return new Mod111Key[] {};
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