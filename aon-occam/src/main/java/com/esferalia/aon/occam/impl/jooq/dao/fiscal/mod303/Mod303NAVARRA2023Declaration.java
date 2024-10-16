package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303;

import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.DEC2;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.blockCenter;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.bold;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.border;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.fontLarger;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.marginTop;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.noWrap;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.paddingLeft;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.styledTag;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.textCenter;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.textRight;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.width150;

import java.text.MessageFormat;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.watson.mutable.MutableBoolean;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.server.AonObjectUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

class Mod303NAVARRA2023Declaration extends Mod303NAVARRA {
	
	protected Mod303NAVARRA2023Declaration() {
		
	}
	
	public static final double PERCENT_21 = 21.0;
	public static final double PERCENT_10 = 10.0;
	public static final double PERCENT_4 = 4.0;
	public static final double PERCENT_5 = 5.0;
	public static final double PERCENT_0 = 0.0;
	public static final double SURCHARGE_PERCENT_52 = 5.2;
	public static final double SURCHARGE_PERCENT_175 = 1.75;
	public static final double SURCHARGE_PERCENT_14 = 1.4;
	public static final double SURCHARGE_PERCENT_05 = 0.5;
	public static final double SURCHARGE_PERCENT_062 = 0.62;
	public static final double SURCHARGE_PERCENT_0 = 0.0;
	
	public static boolean accept(Mod303 mod) {
		return mod.isNavarra() 
			&& (mod.getYear() == 2023
			|| (mod.getYear() == 2024
			&& (mod.getPeriod() == Period.M01 || mod.getPeriod() == Period.M02 || mod.getPeriod() == Period.M03 
			 || mod.getPeriod() == Period.M04 || mod.getPeriod() == Period.M05 || mod.getPeriod() == Period.M06 
			 || mod.getPeriod() == Period.M07 || mod.getPeriod() == Period.M08   
			 || mod.getPeriod() == Period.T1 || mod.getPeriod() == Period.T2)
		   ))
		;
	}
	
	private static final Mod303Key[] PRORATE_KEYS = new Mod303Key[]{
		 Mod303Key.NF_041
		,Mod303Key.NF_141
		,Mod303Key.NF_042
		,Mod303Key.NF_049
		,Mod303Key.NF_043
		,Mod303Key.NF_175
		,Mod303Key.NF_179
	};
	
	private enum Mod303KeyDAO implements IMod303KeyDAO {

		 CM_020(Mod303Key.CM_020)
		,CM_021(Mod303Key.CM_021)
		,NF_I00(Mod303Key.NF_I00
			 ,null,null,Mod303NAVARRA2023Declaration::addDeponentDocument,null,null)
		,NF_I01(Mod303Key.NF_I01)

		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEVENGADO
		// ---------------------------------------------------------------
		,NF_010(Mod303Key.NF_010
			,(mod,vat) -> isIntracommunitySales(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_010,mod,vat.getBase())
			,null,null,null)
		,NF_001(Mod303Key.NF_001
			,(mod,vat) -> isExportation(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_001,mod,vat.getBase())
			,null,null,null)
		,NF_002(Mod303Key.NF_002)
		,NF_194(Mod303Key.NF_194, (mod, vat) -> vat.isSales() && vat.isVatAccrualRegime(), null, null, null, null)
		,NF_171(Mod303Key.NF_171)
		
		,NF_003(Mod303Key.NF_003
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_003,mod,vat.getBase())
			,null,null,null)
		,NF_X03(Mod303Key.NF_X03,null,null,(ctx,mod) -> add(Mod303Key.NF_X03,mod,PERCENT_21),null,null)
		,NF_013(Mod303Key.NF_013
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent21(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_013,mod,vat.getQuota())
			,null,null,null)
		,NF_004(Mod303Key.NF_004
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_004,mod,vat.getBase())
			,null,null,null)
		,NF_X04(Mod303Key.NF_X04,null,null,(ctx,mod) -> add(Mod303Key.NF_X04,mod,PERCENT_10),null,null)
		,NF_014(Mod303Key.NF_014
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent10(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_014,mod,vat.getQuota())
			,null,null,null)
		,NF_005(Mod303Key.NF_005
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_005,mod,vat.getBase())
			,null,null,null)
		,NF_X05(Mod303Key.NF_X05,null,null,(ctx,mod) -> add(Mod303Key.NF_X05,mod,PERCENT_4),null,null)
		,NF_015(Mod303Key.NF_015
			,(mod,vat) -> isCommonNationalSales(vat) && hasPercent4(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_015,mod,vat.getQuota())
			,null,null,null)
				
		
		,NF_006(Mod303Key.NF_006
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent52(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_006,mod,vat.getBase())
			,null,null,null)
		,NF_X06(Mod303Key.NF_X06,null,null,(ctx,mod) -> add(Mod303Key.NF_X06,mod,SURCHARGE_PERCENT_52),null,null)
		,NF_016(Mod303Key.NF_016
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent52(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_016,mod,vat.getSurchargeQuota())
			,null,null,null)
		,NF_051(Mod303Key.NF_051
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent175(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_051,mod,vat.getBase())
			,null,null,null)
		,NF_X51(Mod303Key.NF_X51,null,null,(ctx,mod) -> add(Mod303Key.NF_X51,mod,SURCHARGE_PERCENT_175),null,null)
		,NF_052(Mod303Key.NF_052
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent175(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_052,mod,vat.getSurchargeQuota())
			,null,null,null)
		,NF_007(Mod303Key.NF_007
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent14(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_007,mod,vat.getBase())
			,null,null,null)
		,NF_X07(Mod303Key.NF_X07,null,null,(ctx,mod) -> add(Mod303Key.NF_X07,mod,SURCHARGE_PERCENT_14),null,null)
		,NF_017(Mod303Key.NF_017
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent14(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_017,mod,vat.getSurchargeQuota())
			,null,null,null)
		,NF_008(Mod303Key.NF_008
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent05(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_008,mod,vat.getBase())
			,null,null,null)
		,NF_X08(Mod303Key.NF_X08,null,null,(ctx,mod) -> add(Mod303Key.NF_X08,mod,SURCHARGE_PERCENT_05),null,null)
		,NF_018(Mod303Key.NF_018
			,(mod,vat) -> isCommonNationalSales(vat) && vat.isSurcharge() && hasSurchargePercent05(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_018,mod,vat.getSurchargeQuota())
			,null,null,null)
		
		,NF_009(Mod303Key.NF_009
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification()
			,(ctx,mod,vat) -> add(Mod303Key.NF_009,mod,vat.getBase())
			,null,null,null)
		,NF_019(Mod303Key.NF_019
			,(mod,vat) -> adqIntracomunitariasFilter(vat) && !vat.isRectification()
			,(ctx,mod,vat) -> add(Mod303Key.NF_019,mod,vat.getQuota())
			,null,null,null)
		,NF_172(Mod303Key.NF_172
			,(mod,vat) -> operacionesISPFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_172,mod,vat.getBase())
			,null,null,null)
		,NF_173(Mod303Key.NF_173
			,(mod,vat) -> operacionesISPFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_173,mod,vat.getQuota())
			,null,null,null)
		,NF_176(Mod303Key.NF_176
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat) 
			,(ctx,mod,vat) -> add(Mod303Key.GP_C039,mod,vat.getBase())
			,null,null,null)
		,NF_177(Mod303Key.NF_177
			,(mod,vat) -> modificacionBasesYCuotasFilter(vat) 
			,(ctx,mod,vat) -> add(Mod303Key.NF_177,mod,vat.getQuota())
			,null,null,null)
		,NF_020(Mod303Key.NF_020,null,null,null,
			"NF_013+NF_014+NF_015+NF_016+NF_052+NF_017+NF_018+NF_019+NF_173+NF_177"
			,null)
		
		// ---------------------------------------------------------------
		// ------------------------------------------------- IVA DEDUCIBLE
		// ---------------------------------------------------------------

		,NF_031(Mod303Key.NF_031
			,(mod,vat) -> operacionesInterioresCorrientesFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_031,mod,vat.getBase())
			,null,null,null)
		,NF_041(Mod303Key.NF_041
			,(mod,vat) -> operacionesInterioresCorrientesFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.NF_041,mod,vat)
			,null,null,null)
		,NF_131(Mod303Key.NF_131
			,(mod,vat) -> operacionesInterioresInversionFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_131,mod,vat.getBase())
			,null,null,null)
		,NF_141(Mod303Key.NF_141
			,(mod,vat) -> operacionesInterioresInversionFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.NF_141,mod,vat)
			,null,null,null)
		,NF_032(Mod303Key.NF_032
			,(mod,vat) -> importacionesFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_032,mod,vat.getBase())
			,null,null,null)
		,NF_042(Mod303Key.NF_042
			,(mod,vat) -> importacionesFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.NF_042,mod,vat)
			,null,null,null)
		,NF_039(Mod303Key.NF_039
			,(mod,vat) -> adqIntracomunitariasFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_039,mod,vat.getBase())
			,null,null,null)
		,NF_049(Mod303Key.NF_049
			,(mod,vat) -> adqIntracomunitariasFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.NF_049,mod,vat)
			,null,null,null)
		,NF_170(Mod303Key.NF_170
			,(mod,vat) -> compensacionesRegAgrarioFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_170,mod,vat.getBase())
			,null,null,null)
		,NF_043(Mod303Key.NF_043
			,(mod,vat) -> compensacionesRegAgrarioFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.NF_043,mod,vat)
			,null,null,null)
		,NF_174(Mod303Key.NF_174
			,(mod,vat) -> operacionesISPFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_174,mod,vat.getBase())
			,null,null,null)
		,NF_175(Mod303Key.NF_175
			,(mod,vat) -> operacionesISPFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.NF_175,mod,vat)
			,null,null,null)
		,NF_178(Mod303Key.NF_178
			,(mod,vat) -> rectificationDeduccionesFilter(vat)
			,(ctx,mod,vat) -> add(Mod303Key.NF_178,mod,vat.getBase())
			,null,null,null)
		,NF_179(Mod303Key.NF_179
			,(mod,vat) -> rectificationDeduccionesFilter(vat)
			,(ctx,mod,vat) -> addProrrated(Mod303Key.NF_179,mod,vat)
			,null,null,null)
				
		,NF_045(Mod303Key.NF_045)
		,NF_450(Mod303Key.NF_450)
		,NF_050(Mod303Key.NF_050,null,null,null,"NF_041+NF_141+NF_042+NF_049+NF_043+NF_175+NF_179+NF_045+NF_450",null)
		,NF_055(Mod303Key.NF_055,null,null
			,(ctx,mod) -> add( Mod303Key.NF_055, mod, 100.0)  
			,null,null)
		,NF_061(Mod303Key.NF_061,null,null,null,"round((NF_020-NF_050)*NF_055/100)",null)
		,NF_062(Mod303Key.NF_062,null,null ,(ctx,mod) -> add( Mod303Key.NF_062, mod, getPendingCompesateAmounts( ctx, mod )) ,null,null)				
		,NF_063(Mod303Key.NF_063,null,null,null,"NF_061-NF_062",null)
		;
		
		private Mod303Key key;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private IValueFirstIntializer firstInitializer;
		private String expression;
		private String template;

		private Mod303KeyDAO(Mod303Key key) {
			this(key,null,null,null,null,null);			
		}
		private Mod303KeyDAO(Mod303Key key
				, IValueAccepter acceptValue
				, IValueIntializer initializer
				, IValueFirstIntializer firstInitializer
				, String expression
				, String template) {
			this.key = key;
			this.acceptValue =  acceptValue;
			this.initializer = initializer;
			this.firstInitializer = firstInitializer;
			this.expression =  expression;
			this.template = template;
		}
		
		
		@Override
		public Mod303Key getKey() {
			return key;
		}
		@Override
		public String getExpression() {
			return expression;
		}
		@Override
		public String getTemplate() {
			return template;
		}
		@Override
		public boolean acceptValue(Mod303 mod,VatContext vctx) {
			return  acceptValue != null && acceptValue.accept(mod,vctx);
		}
		@Override
		public boolean hasAccepter() {
			return acceptValue != null;
		}
		@Override
		public void initialize(AONContext ctx,Mod303 mod,VatContext vctx) {
			if (initializer != null) {
				initializer.initialize(ctx, mod, vctx);
			}
		}
		@Override
		public void firstInitialize(AONContext ctx,Mod303 mod) {
			if (firstInitializer != null) {
				firstInitializer.initialize(ctx, mod);
			}
		}
		public static Mod303KeyDAO safeValueOf(String key) {
			if (AonStringUtils.isBlank(key)) return null; 
			for (Mod303KeyDAO keyDAO : Mod303KeyDAO.values()) {	
				if (keyDAO.getKey().getValue().equals(key) ) {
					return keyDAO;
				}
			}
			return null;
		}
	}
	@Override
	public IMod303KeyDAO[] getKeys() {
		return Mod303KeyDAO.values();
	}
	
	@Override
	public IMod303KeyDAO safeValueOf(Mod303 mod, String key) {
		return Mod303KeyDAO.safeValueOf(key);
	}
	
	@Override
	public IMod303KeyDAO valueOf(String keyValue) {
		return Mod303KeyDAO.valueOf(keyValue);
	}
	@Override
	public Mod303Key[] getProrateKeys() {
		return PRORATE_KEYS;
	}
	@Override
	public boolean hasSimplifiedRegime() {
		return false;
	}
	
	private static void addDeponentDocument(AONContext ctx, Mod303 mod) {
		Domain domain = DomainDAO.getDomain(ctx, ctx.getDomainId());
		if (!domain.isStandalone()) {
			Company parentCompany = CompanyDAO.getCompany(ctx, domain.getParentId());
			if (parentCompany != null) {
				mod.ensureDetail(Mod303Key.NF_I00).setDescription(parentCompany.getDocument());	
				mod.ensureDetail(Mod303Key.NF_I01).setDescription(parentCompany.getName());
			}
		}
	}

	//	-----------------------------------------------------------------------	
	//	--------------------------------------------------------------- FILTROS	
	//	-----------------------------------------------------------------------
	private static boolean isCommonNationalSales(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime()
			&& vat.isNational() 
			&& vat.isSales() 
			&& !vat.isRectification();
	}
	private static boolean hasPercent21(VatContext vat) {
		return vat.getPercentage() ==  PERCENT_21;	
	}
	private static boolean hasPercent10(VatContext vat) {
		return vat.getPercentage() ==  PERCENT_10; 	
	}
	private static boolean hasPercent4(VatContext vat) {
		return vat.getPercentage() ==  PERCENT_4
			|| vat.getPercentage() ==  PERCENT_5; 	
	}
	private static boolean isIntracommunitySales(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime() && vat.isIntracommunitySales();	
	}
	private static boolean isExportation(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& (vat.isExtracommunitySales() || vat.isCanCeuMelSales());
	}
	private static boolean modificacionBasesYCuotasFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime()
			&& vat.isRectification() 
			&& (vat.isNationalSales() || operacionesISPFilter(vat));		
	}
	private static boolean operacionesISPFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& (vat.isOtherISPPurchase() 
			|| vat.isOtherISPExpenses() 
			|| vat.isExtracommunityExpenses() 
			|| vat.isCanCeuMelExpenses()
			|| (vat.isExtracommunityPurchase() && vat.isService()) 
			|| (vat.isCanCeuMelPurchase() && vat.isService()));
	}
			
	private static boolean hasSurchargePercent52(VatContext vat) {
		return vat.getSurchargePercent() ==  SURCHARGE_PERCENT_52; 
	}
	private static boolean hasSurchargePercent175(VatContext vat) {
		return vat.getSurchargePercent() ==  SURCHARGE_PERCENT_175; 
	}
	private static boolean hasSurchargePercent14(VatContext vat) {
		return vat.getSurchargePercent() ==  SURCHARGE_PERCENT_14;
	}
	private static boolean hasSurchargePercent05(VatContext vat) {
		return vat.getSurchargePercent() ==  SURCHARGE_PERCENT_05
			|| vat.getSurchargePercent() ==  SURCHARGE_PERCENT_062
			|| vat.getSurchargePercent() ==  SURCHARGE_PERCENT_0;
	}
	private static boolean adqIntracomunitariasFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime()
			&& (vat.isIntracommunityPurchase() || vat.isIntracommunityExpenses());
	}
	
	private static boolean operacionesInterioresCorrientesFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& !vat.isInvestment()
			&& !vat.isRectification() 
			&& !vat.isFarmerRegime() 
			&& AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses());
	}

	private static boolean operacionesInterioresInversionFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& vat.isInvestment()
			&& !vat.isRectification() 
			&& !vat.isFarmerRegime() 
			&& AonMathUtils.isNotZero(vat.getPercentage())
			&& (vat.isNationalPurchase() || vat.isNationalExpenses());
	}
	
	private static boolean importacionesFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime() 
			&& !vat.isService()
			&& (vat.isExtracommunityPurchase() || vat.isCanCeuMelPurchase())
			&& (vat.hasDuaLinked() || vat.isVatImportation());
	}

	private static boolean compensacionesRegAgrarioFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) 
			&& !vat.isVatSurchargeRegime()
			&& vat.isFarmerRegime() 
			&& vat.isNationalPurchase();		
	}
	
	private static boolean rectificationDeduccionesFilter(VatContext vat) {
		return vat.isVatGeneralRegime(VATRegime.GENERAL) && !vat.isVatSurchargeRegime()
			&& vat.isRectification() && (vat.isPurchase() || vat.isExpenses()); 
	}

	@Override
	public Mod303Key[] getCompensationExplainKeys() {
		return new Mod303Key[] {Mod303Key.NF_062};
	}
	
	@Override
	protected String getCompensationExplain( AONContext ctx, Mod303 mod303, Mod303Key key) {
		StringBuilder buf = new StringBuilder();
		buf.append("<div style=\"" +
				  "padding-right: 15px; padding-left: 15px; margin-right: auto; "
				+ "margin-left: auto; width:100%; display: flex;flex-wrap: wrap; "
				+ "justify-content: center; box-sizing: border-box"
				+ "\">")
			.append("<div style=\"" 
				+ "border-radius: 4px; background: #fff; box-shadow: 0 6px 10px rgba(0,0,0,.08), 0 0 6px rgba(0,0,0,.05);"
				+ "transition: .3s transform cubic-bezier(.155,1.105,.295,1.12),.3s box-shadow,.3s -webkit-transform cubic-bezier(.155,1.105,.295,1.12);"
				+ "padding: 4px 5px 5px 10px; margin: 20px 10px 10px 10px; cursor: pointer;"
				+ "flex: 0 1 40%; min-height: 120px; min-width: 350px;"
				+ "\">");
		MutableDouble sum = new MutableDouble();
		MutableBoolean something = new MutableBoolean(false);
		buf.append( MessageFormat.format(styledTag, "table cellspacing=\"0\"",  blockCenter+marginTop+border ) )
			.append("<tr>")
				.append( MessageFormat.format(styledTag, "td colspan=\"2\"",  textCenter+bold+fontLarger+border) )
					.append("Casilla " + key.getBoxFormatted())
				.append("</td>")
			.append("</tr>");
		Mod303DAO.getLastPeriodEffectiveModels(ctx, mod303)
			.filter(Mod303::isToCompensate)
			.map(fm -> { 
				something.setValue(true); 
				sum.add(fm.getDeclarationResult());
				return fm;
			})
			.forEach( fm -> painTableRow(buf,fm) )
		;
		if (something.getValue().booleanValue()) {
			buf.append("<tr>")
				.append( MessageFormat.format(styledTag, "td",  bold+fontLarger+border) )
					.append("Total")
				.append("</td>")
				.append( MessageFormat.format(styledTag, "td",  bold+textRight+width150+fontLarger+border) )
					.append(DEC2.format(AonMathUtils.round(sum.doubleValue())))
				.append("</td>")
			.append("</tr>");
		} else {
			buf.append("<tr>")
				.append( MessageFormat.format(styledTag, "td colspan=\"2\"",  textCenter+fontLarger+border) )
					.append("No se ha encontrado cantidad a compensar en el modelo anterior.")
				.append("</td>")
			.append("</tr>");
		}
		buf.append("</div>");
		buf.append("</div>");
		return buf.toString();
	}
	private <T extends FiscalModel> void painTableRow(StringBuilder buf, T fm) {
		buf.append("<tr>")
			.append( MessageFormat.format(styledTag, "td", paddingLeft+border+noWrap) )
				.append("Resultado de la liquidaci\u00F3n " 
					+ fm.getModelFullName()
					+ AonObjectUtils.defaultIfNull(fm.getDeclarationResultType(), t -> " (" + t.getDescription() + ")"))
			.append("</td>")
			.append( MessageFormat.format(styledTag, "td", textRight+width150+border) )				
				.append(DEC2.format(fm.getDeclarationResult()))
			.append("</td>")
		.append("</tr>");
	}

}
