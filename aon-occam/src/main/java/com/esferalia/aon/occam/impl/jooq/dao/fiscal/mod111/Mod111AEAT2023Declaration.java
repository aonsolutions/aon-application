package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod111;

import java.util.Map;
import java.util.Set;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class Mod111AEAT2023Declaration extends Mod111Declaration {
	
	public static boolean accept(Mod111 mod) {
		return mod.isAEAT() && mod.getYear() >= 2023; 
	}
	
	private enum Mod111KeyDAO  implements IMod111KeyDAO{
		 CM_002(Mod111Key.CM_002,false,null,null,null,null,null)
		// Rendimientos del trabajo. Rendimientos dinerarios.
		,CT_C01(Mod111Key.CT_C01,false
			, Mod111AEAT2023Declaration::isSalaryRetention  
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.CT_C01,mod,docs,pdocs,br))
		,CT_C02(Mod111Key.CT_C02,true
			, Mod111AEAT2023Declaration::isSalaryRetention
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.CT_C02,mod,br))
		,CT_C03(Mod111Key.CT_C03,true
			, Mod111AEAT2023Declaration::isSalaryRetention
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.CT_C03,mod,br))
		// Rendimientos del trabajo y rendimientos de actividades económicas. Rendimientos especie.
		,CT_C04(Mod111Key.CT_C04,false
			, Mod111AEAT2023Declaration::isSalaryInKindRetention
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.CT_C04,mod,docs,pdocs,br))
		,CT_C05(Mod111Key.CT_C05,true
			, Mod111AEAT2023Declaration::isSalaryInKindRetention
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.CT_C05,mod,br))
		,CT_C06(Mod111Key.CT_C06,true
			, Mod111AEAT2023Declaration::isSalaryInKindRetention
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.CT_C06,mod,br))
		
		
		// Rendimientos de actividades económicas. Rendimientos dinerarios.		
		,CT_C07(Mod111Key.CT_C07,false
			, Mod111AEAT2023Declaration::isEconomicActivityRetention
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.CT_C07,mod,docs,pdocs,br))
		,CT_C08(Mod111Key.CT_C08,true
			, Mod111AEAT2023Declaration::isEconomicActivityRetention
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.CT_C08,mod,br))
		,CT_C09(Mod111Key.CT_C09,true
			, Mod111AEAT2023Declaration::isEconomicActivityRetention
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.CT_C09,mod,br))
		// Rendimientos de actividades económicas. Rendimientos especie.
		,CT_C10(Mod111Key.CT_C10,false,null,null,null,null,null)
		,CT_C11(Mod111Key.CT_C11,true ,null,null,null,null,null)
		,CT_C12(Mod111Key.CT_C12,true ,null,null,null,null,null)
		
		// Premios por la participación en juegos, concursos, rifas o combinaciones aleatorias. Dinerarios.
		,CT_C13(Mod111Key.CT_C13,false
			, Mod111AEAT2023Declaration::isGameRetention
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.CT_C13,mod,docs,pdocs,br))
		,CT_C14(Mod111Key.CT_C14,true 
			, Mod111AEAT2023Declaration::isGameRetention
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.CT_C14,mod,br))
		,CT_C15(Mod111Key.CT_C15,true 
			, Mod111AEAT2023Declaration::isGameRetention
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.CT_C15,mod,br))

		// Premios por la participación en juegos, concursos, rifas o combinaciones aleatorias. Especie.
		,CT_C16(Mod111Key.CT_C16,false,null,null,null,null,null)
		,CT_C17(Mod111Key.CT_C17,true ,null,null,null,null,null)
		,CT_C18(Mod111Key.CT_C18,true ,null,null,null,null,null)
		
		// Ganancias patrimoniales derivadas de los aprovechamientos forestales de los vecinos en montes públicos. Dinerarios.
		,CT_C19(Mod111Key.CT_C19,false
			, Mod111AEAT2023Declaration::isForestalRetention
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.CT_C19,mod,docs,pdocs,br))
		,CT_C20(Mod111Key.CT_C20,true 
			, Mod111AEAT2023Declaration::isForestalRetention
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.CT_C20,mod,br))
		,CT_C21(Mod111Key.CT_C21,true 
			, Mod111AEAT2023Declaration::isForestalRetention
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.CT_C21,mod,br))
		// Ganancias patrimoniales derivadas de los aprovechamientos forestales de los vecinos en montes públicos. Dinerarios.
		,CT_C22(Mod111Key.CT_C22,false,null,null,null,null,null)
		,CT_C23(Mod111Key.CT_C23,true ,null,null,null,null,null)
		,CT_C24(Mod111Key.CT_C24,true ,null,null,null,null,null)
		
		// Contraprestaciones por la cesión de derechos de imagen: ingresos a cuenta previstos en el artículo 92.8 de la Ley del Impuesto.
		,CT_C25(Mod111Key.CT_C25,false
			, Mod111AEAT2023Declaration::isImageRetention
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.CT_C25,mod,docs,pdocs,br))
		,CT_C26(Mod111Key.CT_C26,true
			, Mod111AEAT2023Declaration::isImageRetention
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.CT_C26,mod,br))
		,CT_C27(Mod111Key.CT_C27,true
			, Mod111AEAT2023Declaration::isImageRetention
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.CT_C27,mod,br))
		
		,CT_C28(Mod111Key.CT_C28,false,null,null,null, "CT_C03+CT_C06+CT_C09+CT_C12+CT_C15+CT_C18+CT_C21+CT_C24+CT_C27",null)
		,CT_C29(Mod111Key.CT_C29
			, false
			, null
			, null
			, (ctx,mod) -> mod.putAmount(Mod111Key.CT_C29,mod.isComplementary()
				?Mod111DAO.getSamePeriodEffectiveModels(ctx, mod).mapToDouble(Mod111::getDeclarationResult).sum()
				:0.0)
			,null
			,"{messages : ["
				+ "\"Declaraciones en el mismo periodo/ejercicio:\","
				+ "@foreach{fm : periodModels}"
				+ "\" \u2022 Resultado del modelo @{fm.getModelFullName()} : @{java.text.DecimalFormat.getInstance().format(fm.getDeclarationResult())}\","
				+ "@end{}"
				+ "\" - Resultado de la casilla: @{java.text.DecimalFormat.getInstance().format(CT_C29)}\""
			+"]}"
			)
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
				, IValueIntializer initializer) {
			this(key, diffEnabled, acceptValue, initializer, null, null, null);
		}
		
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
	
	// ----------------------------------------------- -------
	// ----------------------------------------------- FILTROS
	// ----------------------------------------------- -------
	private static boolean isSalaryRetention(Mod111 mod111, IrpfBreakdown br) {
		return (br.isFromSalary() && !br.isInKind()) || isInvoiceWorkRetention(br);
		
	}
	public static boolean isSalaryInKindRetention(Mod111 mod111, IrpfBreakdown br) {
		return br.isFromSalary() && br.isInKind();
	}
	private static boolean isInvoiceWorkRetention(IrpfBreakdown br) {
		return br.isFromInvoice() && 
			(br.getWithholdingType() == WithholdingType.M190_F_01  
			 ||	br.getWithholdingType() == WithholdingType.M190_F_02_1
			 ||	br.getWithholdingType() == WithholdingType.M190_F_02_2);
	}
	private static boolean isEconomicActivityRetention(Mod111 mod111, IrpfBreakdown br) {
		return br.isFromInvoice() && 
			(br.getWithholdingType() == WithholdingType.PROFESSIONAL  
			 ||	br.getWithholdingType() == WithholdingType.M190_G_02
			 ||	br.getWithholdingType() == WithholdingType.M190_G_03
			 ||	br.getWithholdingType() == WithholdingType.FARMER
			 ||	br.getWithholdingType() == WithholdingType.TRANSPORT_OPERATOR
			 ||	br.getWithholdingType() == WithholdingType.M190_H_02
			 ||	br.getWithholdingType() == WithholdingType.M190_H_03
			 ||	br.getWithholdingType() == WithholdingType.M190_I_01
			 ||	br.getWithholdingType() == WithholdingType.M190_I_02);
	}
	private static boolean isGameRetention(Mod111 mod111, IrpfBreakdown br) {
		return br.isFromInvoice() && 
			(br.getWithholdingType() == WithholdingType.M190_K_01  
			 ||	br.getWithholdingType() == WithholdingType.M190_K_03);
	}
	private static boolean isForestalRetention(Mod111 mod111, IrpfBreakdown br) {
		return br.isFromInvoice() && br.getWithholdingType() == WithholdingType.M190_K_02;
	}
	private static boolean isImageRetention(Mod111 mod111, IrpfBreakdown br) {
		return br.isFromInvoice() && br.getWithholdingType() == WithholdingType.M190_J;
	}
	
}
