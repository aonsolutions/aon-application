package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod111;

import java.util.Map;
import java.util.Set;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class Mod110Bizkaia2023Declaration extends Mod111Declaration {
	
	public static boolean accept(Mod111 mod) {
		return mod.isBizkaia() && mod.getYear() >= 2023 && mod.getPeriod().isQuarterPeriod(); 
	}

	private enum Mod111KeyDAO  implements IMod111KeyDAO{
		 BZ_C01 (Mod111Key.BZ_C01,false
			, (mod,br) ->  br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.BZ_C01,mod,docs,pdocs,br)
			,null,null,null)
		,BZ_C12 (Mod111Key.BZ_C12,true
			, (mod,br) ->  br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.BZ_C12,mod,br)
			,null,null,null)
		,BZ_C23 (Mod111Key.BZ_C23,true
			, (mod,br) ->  br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.BZ_C23,mod,br)
			,null,null,null)
		,BZ_C02 (Mod111Key.BZ_C02,false,null,null,null,null,null)
		,BZ_C13 (Mod111Key.BZ_C13,true ,null,null,null,null,null)
		,BZ_C24 (Mod111Key.BZ_C24,true ,null,null,null,null,null)
		,BZ_C03 (Mod111Key.BZ_C03,false,null,null,null,null,null)
		,BZ_C14 (Mod111Key.BZ_C14,true ,null,null,null,null,null)
		,BZ_C25 (Mod111Key.BZ_C25,true ,null,null,null,null,null)
		,BZ_C04 (Mod111Key.BZ_C04,false,null,null,null,null,null)
		,BZ_C15 (Mod111Key.BZ_C15,true ,null,null,null,null,null)
		,BZ_C26 (Mod111Key.BZ_C26,true ,null,null,null,null,null)
		,BZ_C05 (Mod111Key.BZ_C05,false,null,null,null,null,null)
		,BZ_C16 (Mod111Key.BZ_C16,true ,null,null,null,null,null)
		,BZ_C27 (Mod111Key.BZ_C27,true ,null,null,null,null,null)
		,BZ_C06 (Mod111Key.BZ_C06,false,null,null,null,null,null)
		,BZ_C17 (Mod111Key.BZ_C17,true,null,null,null,null,null)
		,BZ_C28 (Mod111Key.BZ_C28,true,null,null,null,null,null)
		,BZ_C07 (Mod111Key.BZ_C07,false
			, (mod,br) ->  br.isNotObjectiveRegime() && (isProfessional(br) || isTransportOperator(br))
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.BZ_C07,mod,docs,pdocs,br)
			,null,null,null)
		,BZ_C18 (Mod111Key.BZ_C18,true
			, (mod,br) ->  br.isNotObjectiveRegime() && (isProfessional(br) || isTransportOperator(br))
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.BZ_C18,mod,br)
			,null,null,null)
		,BZ_C29 (Mod111Key.BZ_C29,true
			, (mod,br) ->  br.isNotObjectiveRegime() && (isProfessional(br) || isTransportOperator(br))
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.BZ_C29,mod,br)
			,null,null,null)
		,BZ_C50 (Mod111Key.BZ_C50,false
			, (mod,br) ->  br.isObjectiveRegime() && (isProfessional(br) || isTransportOperator(br))
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.BZ_C50,mod,docs,pdocs,br)
			,null,null,null)
		,BZ_C51 (Mod111Key.BZ_C51,true
			, (mod,br) ->  br.isObjectiveRegime() && (isProfessional(br) || isTransportOperator(br))
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.BZ_C51,mod,br)
			,null,null,null)
		,BZ_C52 (Mod111Key.BZ_C52,true
			, (mod,br) ->  br.isObjectiveRegime() && (isProfessional(br) || isTransportOperator(br))
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.BZ_C52,mod,br)
			,null,null,null)
		,BZ_C08 (Mod111Key.BZ_C08,false
			, (mod,br) ->  isFarmer(br)
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.BZ_C08,mod,docs,pdocs,br)
			,null,null,null)
		,BZ_C19 (Mod111Key.BZ_C19,true
			, (mod,br) ->  isFarmer(br)
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.BZ_C19,mod,br)
			,null,null,null)
		,BZ_C30 (Mod111Key.BZ_C30,true
			, (mod,br) ->  isFarmer(br)
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.BZ_C30,mod,br)
			,null,null,null)
		,BZ_C09 (Mod111Key.BZ_C09,false
			, (mod,br) ->  br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.BZ_C09,mod,docs,pdocs,br)
			,null,null,null)
		,BZ_C20 (Mod111Key.BZ_C20,true
			, (mod,br) ->  br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.BZ_C20,mod,br)
			,null,null,null)
		,BZ_C31 (Mod111Key.BZ_C31,true
			, (mod,br) ->  br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.BZ_C31,mod,br)
			,null,null,null)
		,BZ_C10 (Mod111Key.BZ_C10,false,null,null,null,null,null)
		,BZ_C21 (Mod111Key.BZ_C21,true ,null,null,null,null,null)
		,BZ_C32 (Mod111Key.BZ_C32,true ,null,null,null,null,null)
		,BZ_C11 (Mod111Key.BZ_C11,false,null,null,null,null,null)
		,BZ_C22 (Mod111Key.BZ_C22,true ,null,null,null,null,null)
		,BZ_C33 (Mod111Key.BZ_C33,true ,null,null,null,null,null)
		
		,BZ_C34T(Mod111Key.BZ_C34T,false
			,null,null,null, "BZ_C01+BZ_C02+BZ_C03+BZ_C04+BZ_C05+BZ_C06+BZ_C07+BZ_C50+BZ_C08+BZ_C09+BZ_C10+BZ_C11"
			,null)
		,BZ_C35T(Mod111Key.BZ_C35T,false
			,null,null,null, "BZ_C12+BZ_C13+BZ_C14+BZ_C15+BZ_C16+BZ_C17+BZ_C18+BZ_C51+BZ_C19+BZ_C20+BZ_C21+BZ_C22"
			,null)
		,BZ_C36T(Mod111Key.BZ_C36T,false
			,null,null,null, "BZ_C23+BZ_C24+BZ_C25+BZ_C26+BZ_C27+BZ_C28+BZ_C29+BZ_C52+BZ_C30+BZ_C31+BZ_C32+BZ_C33"
			,null)
		
		,BZ_C39T(Mod111Key.BZ_C39,false, null,null,null, "BZ_C36T",null)
		,BZ_TIP (Mod111Key.BZ_TIP,false, null,null,null,null,null)
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
				, String expression, String template) {
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
		return mod.getAmount(Mod111Key.BZ_C39);
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