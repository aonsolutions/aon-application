package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod111;

import java.util.Map;
import java.util.Set;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class Mod111Araba2021Declaration extends Mod111Declaration {
	
	public static boolean accept(Mod111 mod) {
		return mod.isAraba() && mod.getYear() < 2023; 
	}

	private enum Mod111KeyDAO  implements IMod111KeyDAO{
		 AR_907(Mod111Key.AR_907,false,null,null,null,null,null)
		,AR_908(Mod111Key.AR_908,false,null,null,null,null,null)
		,AR_909(Mod111Key.AR_909,false,null,null,null,null,null)
		,AR_C50(Mod111Key.AR_C50,false
			, (mod,br) -> br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.AR_C50,mod,docs,pdocs,br)
			,null,null,null)
		,AR_C60(Mod111Key.AR_C60,true
			, (mod,br) -> br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.AR_C60,mod,br)
			,null,null,null)
		,AR_C70(Mod111Key.AR_C70,true
			, (mod,br) -> br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.AR_C70,mod,br)
			,null,null,null)
		,AR_C51(Mod111Key.AR_C51,false,null,null,null,null,null)
		,AR_C61(Mod111Key.AR_C61,true ,null,null,null,null,null)
		,AR_C71(Mod111Key.AR_C71,true ,null,null,null,null,null)
		,AR_C52(Mod111Key.AR_C52,false,null,null,null,null,null)
		,AR_C62(Mod111Key.AR_C62,true ,null,null,null,null,null)
		,AR_C72(Mod111Key.AR_C72,true ,null,null,null,null,null)
		,AR_C53(Mod111Key.AR_C53,false,null,null,null,null,null)
		,AR_C63(Mod111Key.AR_C63,true ,null,null,null,null,null)
		,AR_C73(Mod111Key.AR_C73,true ,null,null,null,null,null)
		,AR_C54(Mod111Key.AR_C54,false
			, (mod,br) -> br.isNotObjectiveRegime() && (isProfessional(br) || isTransportOperator(br))
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.AR_C54,mod,docs,pdocs,br)
			,null,null,null)
		,AR_C64(Mod111Key.AR_C64,true
			, (mod,br) -> br.isNotObjectiveRegime() && (isProfessional(br) || isTransportOperator(br))
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.AR_C64,mod,br)
			,null,null,null)
		,AR_C74(Mod111Key.AR_C74,true
			, (mod,br) -> br.isNotObjectiveRegime() && (isProfessional(br) || isTransportOperator(br))
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.AR_C74,mod,br)
			,null,null,null)
		,AR_C58(Mod111Key.AR_C58,false
			, (mod,br) -> br.isObjectiveRegime() && (isProfessional(br) || isTransportOperator(br))
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.AR_C58,mod,docs,pdocs,br)
			,null,null,null)
		,AR_C68(Mod111Key.AR_C68,true
			, (mod,br) -> br.isObjectiveRegime() && (isProfessional(br) || isTransportOperator(br))
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.AR_C68,mod,br)
			,null,null,null)
		,AR_C78(Mod111Key.AR_C78,true
			, (mod,br) -> br.isObjectiveRegime() && (isProfessional(br) || isTransportOperator(br))
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.AR_C78,mod,br)
			,null,null,null)
		,AR_C55(Mod111Key.AR_C55,false
			, (mod,br) -> isFarmer(br)
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.AR_C55,mod,docs,pdocs,br)
			,null,null,null)
		,AR_C65(Mod111Key.AR_C65,true
			, (mod,br) -> isFarmer(br)
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.AR_C65,mod,br)
			,null,null,null)
		,AR_C75(Mod111Key.AR_C75,true
			, (mod,br) -> isFarmer(br)
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.AR_C75,mod,br)
			,null,null,null)
		,AR_C56(Mod111Key.AR_C56,false,null,null,null,null,null)
		,AR_C66(Mod111Key.AR_C66,true ,null,null,null,null,null)
		,AR_C76(Mod111Key.AR_C76,true ,null,null,null,null,null)
		,AR_C57(Mod111Key.AR_C57,false
			, (mod,br) -> br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.AR_C56,mod,docs,pdocs,br)
			,null,null,null)
		,AR_C67(Mod111Key.AR_C67,true
			, (mod,br) -> br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.AR_C66,mod,br)
			,null,null,null)
		,AR_C77(Mod111Key.AR_C77,true
			, (mod,br) -> br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.AR_C76,mod,br)
			,null,null,null)
		,AR_C80(Mod111Key.AR_C80,false
			, null,null,null, "AR_C50+AR_C51+AR_C52+AR_C53+AR_C54+AR_C58+AR_C55+AR_C56+AR_C57",null)
		,AR_C81(Mod111Key.AR_C81,false
			, null,null,null, "AR_C60+AR_C61+AR_C62+AR_C63+AR_C64+AR_C68+AR_C65+AR_C66+AR_C67",null)
		,AR_C82(Mod111Key.AR_C82,false
			, null,null,null, "AR_C70+AR_C71+AR_C72+AR_C73+AR_C74+AR_C78+AR_C75+AR_C76+AR_C77",null)
		,AR_C83(Mod111Key.AR_C83,false,null,null
			, (ctx,mod) -> mod.putAmount(Mod111Key.AR_C83,
				mod.isReplacement() 
				?Mod111DAO.getSamePeriodModels(ctx, mod).mapToDouble(Mod111::getDeclarationResult).sum()
				:0.0)
			,null
			,null
			)
		,AR_C84(Mod111Key.AR_C84,false,null,null,null,null,null)
		,AR_C85(Mod111Key.AR_C85,false,null,null,null,null,null)
		,AR_C87(Mod111Key.AR_C87,false,null,null,null, "AR_C82-AR_C83+AR_C84+AR_C85",null)
		,AR_TIP(Mod111Key.AR_TIP,false,null,null,null,null,null)
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
			this.diffEnabled =  diffEnabled;
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
		return mod.getAmount(Mod111Key.AR_C87);
	}

	@Override
	ComplementaryBeahaviour getComplementaryBehaviour(Mod111 mod) {
		return  mod.isComplementary()
			?ComplementaryBeahaviour.COMPLEMENTARY
			:ComplementaryBeahaviour.REPLACEMENT;
	}
	
	@Override
	Mod111 initialize(AONContext ctx, Mod111 mod111) {
		mod111.setComplementaryDeclarationAvailable(true);
		mod111.setReplacementDeclarationAvailable(true);
		return super.initializeModel(ctx, mod111);
	}

	@Override
	public Mod111Key[] getSamePeriodExplainKeys() {
		return new Mod111Key[] {Mod111Key.AR_C83};
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