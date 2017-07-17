package com.esferalia.aon.occam.impl.jooq.dao;

import java.text.MessageFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mvel2.MVEL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod303DAO extends FiscalModelDAO {
	
	@FunctionalInterface
	private static interface IModelInfoProvider {
		String obtain(AONContext ctx, Mod303 mod,IModelScript<Mod303Key> script,Mod303KeyDAO keyDAO);
	}
	private static final String INFO_MSG = "<pre class='aon-fixed-font aon-font-medium aon-margin-bottom'>{0}<pre>";
	private static final String NONE_INFO = "No hay datos";
	private static enum Mod303KeyInfoDAO {
		 NONE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, NONE_INFO)) )
		,INVOICE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getInvoicesInfo(ctx, mod, script,keyDAO))))
		,DIFF_INVOICE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getDiffInvoicesInfo(ctx, mod, script,keyDAO))))
		,COMPUTE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getExpression(mod, script,keyDAO))))
		;
		private IModelInfoProvider provider;
		
		private Mod303KeyInfoDAO (IModelInfoProvider provider) {
			this.provider = provider;
		}
		public String getInfo(AONContext ctx, Mod303 mod,IModelScript<Mod303Key> script,Mod303KeyDAO keyDAO) {
			return provider.obtain(ctx, mod, script,keyDAO);
		}
	}
	
	@FunctionalInterface
	private static interface IModelAccepter {
		boolean accept(Mod303 mod);
	}
	@FunctionalInterface
	public static interface IValueAccepter {
		boolean accept(Mod303 mod, VatContext vat);
	}
	@FunctionalInterface
	public static interface IValueIntializer {
		void initialize(AONContext ctx,Mod303 mod, VatContext vat);
	}
	@FunctionalInterface
	public static interface IValueFirstIntializer {
		void initialize(AONContext ctx,Mod303 mod);
	}

	private static enum Mod303KeyDAO {
		// *************************************************************************
		// **************************************************** COMMON TERRITORY ***
		// ************************************************************************* 
		 CT_A01(Mod303Key.CT_A01,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_A02(Mod303Key.CT_A02,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_A03(Mod303Key.CT_A03,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_A04(Mod303Key.CT_A04,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_A05(Mod303Key.CT_A05,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_A06(Mod303Key.CT_A06,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_A07(Mod303Key.CT_A07,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_A08(Mod303Key.CT_A08,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_A09(Mod303Key.CT_A09,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_A10(Mod303Key.CT_A10,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_A11(Mod303Key.CT_A11,(mod -> mod.isAEAT()),null,null,null,null)

		,CT_C01(Mod303Key.CT_C01
			 ,(mod) -> mod.isAEAT()
			 ,(mod,vat) -> (vat.isNationalSales() && !vat.isRectification() && vat.getPercentage() ==  getPercent1(mod))
			 ,(ctx,mod,vat) -> add(Mod303Key.CT_C01,mod,vat.getBase())
			 ,null
			 ,null)
		,CT_C02(Mod303Key.CT_C02
			,mod -> mod.isAEAT()
			,null
			,null
			,(ctx,mod) -> add(Mod303Key.CT_C02,mod,getPercent1(mod))
			,null)
		,CT_C03(Mod303Key.CT_C03
			,(mod) -> mod.isAEAT()
			,(mod,vat) -> (vat.isNationalSales() && !vat.isRectification() && vat.getPercentage() ==  getPercent1(mod))
			,(ctx,mod,vat) -> add(Mod303Key.CT_C03,mod,vat.getQuota())
			,null
			,null)
		,CT_C04(Mod303Key.CT_C04
			,(mod) -> mod.isAEAT()
			,(mod,vat) -> (vat.isNationalSales() && !vat.isRectification() && vat.getPercentage() ==  getPercent2(mod))
			,(ctx,mod,vat) -> add(Mod303Key.CT_C04,mod,vat.getBase())
			,null
			,null)
		,CT_C05(Mod303Key.CT_C05
			,mod -> mod.isAEAT()
			,null
			,null
			,(ctx,mod) -> add(Mod303Key.CT_C05,mod,getPercent2(mod))
			,null)
		,CT_C06(Mod303Key.CT_C06
			,(mod) -> mod.isAEAT()
			,(mod,vat) -> (vat.isNationalSales() && !vat.isRectification() && vat.getPercentage() ==  getPercent2(mod))
			,(ctx,mod,vat) -> add(Mod303Key.CT_C06,mod,vat.getQuota())
			,null
			,null)
		,CT_C07(Mod303Key.CT_C07
			,(mod) -> mod.isAEAT()
			,(mod,vat) -> (vat.isNationalSales() && !vat.isRectification() && vat.getPercentage() ==  getPercent3(mod))
			,(ctx,mod,vat) -> add(Mod303Key.CT_C07,mod,vat.getBase())
			,null
			,null)
		,CT_C08(Mod303Key.CT_C08
			,mod -> mod.isAEAT()
			,null
			,null
			,(ctx,mod) -> add(Mod303Key.CT_C08,mod,getPercent3(mod))
			,null)
		,CT_C09(Mod303Key.CT_C09
			,(mod) -> mod.isAEAT()
			,(mod,vat) -> (vat.isNationalSales() && !vat.isRectification() && vat.getPercentage() ==  getPercent3(mod))
			,(ctx,mod,vat) -> add(Mod303Key.CT_C09,mod,vat.getQuota())
			,null
			,null)
		,CT_C10(Mod303Key.CT_C10
			,(mod -> mod.isAEAT())
			,(mod,vat) -> (vat.isIntracommunityPurchase() && vat.isIntracommunityExpenses())
			,(ctx,mod,vat) -> add(Mod303Key.CT_C10,mod,vat.getBase())
			,null
			,null)
		,CT_C11(Mod303Key.CT_C11
			,(mod -> mod.isAEAT())
			,(mod,vat) -> (vat.isIntracommunityPurchase() && vat.isIntracommunityExpenses())
			,(ctx,mod,vat) -> add(Mod303Key.CT_C11,mod,vat.getQuota())
			,null
			,null)
		,CT_C12(Mod303Key.CT_C12
			,(mod -> mod.isAEAT())
			,(mod,vat) -> (vat.isOtherISPPurchase() || vat.isOtherISPExpenses())
			,(ctx,mod,vat) -> add(Mod303Key.CT_C12,mod,vat.getBase())
			,null
			,null)
		,CT_C13(Mod303Key.CT_C13
			,(mod -> mod.isAEAT())
			,(mod,vat) -> (vat.isOtherISPPurchase() || vat.isOtherISPExpenses())
			,(ctx,mod,vat) -> add(Mod303Key.CT_C13,mod,vat.getQuota() + vat.getSurchargeQuota())
			,null
			,null)
		,CT_C14(Mod303Key.CT_C14
			,(mod -> mod.isAEAT())
			,(mod,vat) -> (vat.isNationalSales() && vat.isRectification())
			,(ctx,mod,vat) -> add(Mod303Key.CT_C14,mod,vat.getBase())
			,null
			,null)
		,CT_C15(Mod303Key.CT_C15
			,(mod -> mod.isAEAT())
			,(mod,vat) -> (vat.isNationalSales() && vat.isRectification())
			,(ctx,mod,vat) -> add(Mod303Key.CT_C15,mod,vat.getQuota())
			,null
			,null)
		,CT_C16(Mod303Key.CT_C16
			,(mod) -> mod.isAEAT()
			,(mod,vat) -> (vat.isNationalSales() && vat.isSurcharge() && !vat.isRectification() && vat.getSurchargePercent() ==  getSurchargePercent1(mod))
			,(ctx,mod,vat) -> add(Mod303Key.CT_C16,mod,vat.getBase())
			,null
			,null)
		,CT_C17(Mod303Key.CT_C17
			,(mod) -> mod.isAEAT()
			,null
			,null
			,(ctx,mod) -> add(Mod303Key.CT_C17,mod,getSurchargePercent1(mod))
			,null)
		,CT_C18(Mod303Key.CT_C18
			,(mod) -> mod.isAEAT()
			,(mod,vat) -> (vat.isNationalSales() && vat.isSurcharge() && !vat.isRectification() && vat.getSurchargePercent() ==  getSurchargePercent1(mod))
			,(ctx,mod,vat) -> add(Mod303Key.CT_C18,mod,vat.getSurchargeQuota())
			,null
			,null)
		,CT_C19(Mod303Key.CT_C19
			,(mod) -> mod.isAEAT()
			,(mod,vat) -> (vat.isNationalSales() && vat.isSurcharge() && !vat.isRectification() && vat.getSurchargePercent() ==  getSurchargePercent2(mod))
			,(ctx,mod,vat) -> add(Mod303Key.CT_C19,mod,vat.getBase())
			,null
			,null)
		,CT_C20(Mod303Key.CT_C20
			,(mod) -> mod.isAEAT()
			,null
			,null
			,(ctx,mod) -> add(Mod303Key.CT_C20,mod,getSurchargePercent2(mod))
			,null)
		,CT_C21(Mod303Key.CT_C21
			,(mod) -> mod.isAEAT()
			,(mod,vat) -> (vat.isNationalSales() && vat.isSurcharge() && !vat.isRectification() && vat.getSurchargePercent() ==  getSurchargePercent2(mod))
			,(ctx,mod,vat) -> add(Mod303Key.CT_C21,mod,vat.getSurchargeQuota())
			,null
			,null)
		,CT_C22(Mod303Key.CT_C22
			,(mod) -> mod.isAEAT()
			,(mod,vat) -> (vat.isNationalSales() && vat.isSurcharge() && !vat.isRectification() && vat.getSurchargePercent() ==  getSurchargePercent3(mod))
			,(ctx,mod,vat) -> add(Mod303Key.CT_C22,mod,vat.getBase())
			,null
			,null)
		,CT_C23(Mod303Key.CT_C23
			,(mod) -> mod.isAEAT()
			,null
			,null
			,(ctx,mod) -> add(Mod303Key.CT_C23,mod,getSurchargePercent3(mod))
			,null)
		,CT_C24(Mod303Key.CT_C24
			,(mod) -> mod.isAEAT()
			,(mod,vat) -> (vat.isNationalSales() && vat.isSurcharge() && !vat.isRectification() && vat.getSurchargePercent() ==  getSurchargePercent3(mod))
			,(ctx,mod,vat) -> add(Mod303Key.CT_C24,mod,vat.getSurchargeQuota())
			,null
			,null)
		,CT_C25(Mod303Key.CT_C25
			,(mod) -> mod.isAEAT()
			,(mod,vat) -> (vat.isNationalSales() && vat.isSurcharge() && vat.isRectification())
			,(ctx,mod,vat) -> add(Mod303Key.CT_C25,mod,vat.getBase())
			,null
			,null)
		,CT_C26(Mod303Key.CT_C26
			,(mod) -> mod.isAEAT()
			,(mod,vat) -> (vat.isNationalSales() && vat.isSurcharge() && vat.isRectification())
			,(ctx,mod,vat) -> add(Mod303Key.CT_C26,mod,vat.getSurchargeQuota())
			,null
			,null)
		,CT_C27(Mod303Key.CT_C27
			,(mod -> mod.isAEAT())
			,null
			,null
			,null
			,"CT_C03+CT_C06+CT_C09+CT_C11+CT_C13+CT_C15+CT_C18+CT_C21+CT_C24+CT_C26")
		
		,CT_C28(Mod303Key.CT_C28
			,(mod -> mod.isAEAT())
			,(mod,vat) -> ( !vat.isInvestment() 
//					&& !vat.isRectification()     
					&& (vat.isNationalExpenses() 
					|| (vat.isNationalPurchase() && !vat.isFarmerRegime())
					|| vat.isOtherISPExpenses() 
					|| (vat.isOtherISPPurchase() && !vat.isFarmerRegime())
						) )
			,(ctx,mod,vat) -> prorate(Mod303Key.CT_C28,mod,vat.getBase())
			,null
			,null)
		,CT_C29(Mod303Key.CT_C29
			,(mod -> mod.isAEAT())
			,(mod,vat) -> ( !vat.isInvestment() 
//					&& !vat.isRectification()     
					&& (vat.isNationalExpenses() 
					|| (vat.isNationalPurchase() && !vat.isFarmerRegime())
					|| vat.isOtherISPExpenses() 
					|| (vat.isOtherISPPurchase() && !vat.isFarmerRegime())
						) )
			,(ctx,mod,vat) -> prorate(Mod303Key.CT_C29,mod,vat.getDeductibleQuota())
			,null
			,null)
		,CT_C30(Mod303Key.CT_C30
			,(mod -> mod.isAEAT())
			,(mod,vat) -> ( vat.isInvestment() 
//					&& !vat.isRectification() 
					&& (vat.isNationalExpenses() 
					|| (vat.isNationalPurchase() && !vat.isFarmerRegime())
					|| vat.isOtherISPExpenses() 
					|| (vat.isOtherISPPurchase() && !vat.isFarmerRegime())
						) )
			,(ctx,mod,vat) -> prorate(Mod303Key.CT_C30,mod,vat.getBase())
			,null
			,null)
		,CT_C31(Mod303Key.CT_C31
			,(mod -> mod.isAEAT())
			,(mod,vat) -> ( vat.isInvestment() 
//					&& !vat.isRectification()     
					&& (vat.isNationalExpenses() 
					|| (vat.isNationalPurchase() && !vat.isFarmerRegime())
					|| vat.isOtherISPExpenses() 
					|| (vat.isOtherISPPurchase() && !vat.isFarmerRegime())
						) )
			,(ctx,mod,vat) -> prorate(Mod303Key.CT_C31,mod,vat.getDeductibleQuota())
			,null
			,null)
		,CT_C32(Mod303Key.CT_C32
			,(mod -> mod.isAEAT())
			,(mod,vat) -> ( !vat.isInvestment() && !vat.isRectification() && (vat.isExtracommunityPurchase() 
					|| vat.isExtracommunityExpenses() || vat.isCanCeuMelPurchase() || vat.isCanCeuMelExpenses()))
			,(ctx,mod,vat) -> prorate(Mod303Key.CT_C32,mod,vat.getBase())
			,null
			,null)
		,CT_C33(Mod303Key.CT_C33
			,(mod -> mod.isAEAT())
			,(mod,vat) -> ( !vat.isInvestment() && !vat.isRectification() && (vat.isExtracommunityPurchase() 
					|| vat.isExtracommunityExpenses() || vat.isCanCeuMelPurchase() || vat.isCanCeuMelExpenses()))
			,(ctx,mod,vat) -> prorate(Mod303Key.CT_C33,mod,vat.getDeductibleQuota())
			,null
			,null)
		,CT_C34(Mod303Key.CT_C34
			,(mod -> mod.isAEAT())
			,(mod,vat) -> ( vat.isInvestment() && !vat.isRectification() && (vat.isExtracommunityPurchase() 
					|| vat.isExtracommunityExpenses() || vat.isCanCeuMelPurchase() || vat.isCanCeuMelExpenses()))
			,(ctx,mod,vat) -> prorate(Mod303Key.CT_C34,mod,vat.getBase())
			,null
			,null)
		,CT_C35(Mod303Key.CT_C35
			,(mod -> mod.isAEAT())
			,(mod,vat) -> ( vat.isInvestment() && !vat.isRectification() && (vat.isExtracommunityPurchase() 
					|| vat.isExtracommunityExpenses() || vat.isCanCeuMelPurchase() || vat.isCanCeuMelExpenses()))
			,(ctx,mod,vat) -> prorate(Mod303Key.CT_C35,mod,vat.getDeductibleQuota())
			,null
			,null)
		,CT_C36(Mod303Key.CT_C36
			,(mod -> mod.isAEAT())
			,(mod,vat) -> ( !vat.isInvestment() && !vat.isRectification() && (vat.isIntracommunityPurchase() || vat.isIntracommunityExpenses()))
			,(ctx,mod,vat) -> prorate(Mod303Key.CT_C36,mod,vat.getBase())
			,null
			,null)
		,CT_C37(Mod303Key.CT_C37
			,(mod -> mod.isAEAT())
			,(mod,vat) -> ( !vat.isInvestment() && !vat.isRectification() && (vat.isIntracommunityPurchase() || vat.isIntracommunityExpenses()))
			,(ctx,mod,vat) -> prorate(Mod303Key.CT_C37,mod,vat.getDeductibleQuota())
			,null
			,null)
		,CT_C38(Mod303Key.CT_C38
			,(mod -> mod.isAEAT())
			,(mod,vat) -> ( vat.isInvestment() && !vat.isRectification() && (vat.isIntracommunityPurchase() || vat.isIntracommunityExpenses()))
			,(ctx,mod,vat) -> prorate(Mod303Key.CT_C38,mod,vat.getBase())
			,null
			,null)
		,CT_C39(Mod303Key.CT_C39
			,(mod -> mod.isAEAT())
			,(mod,vat) -> ( vat.isInvestment() && !vat.isRectification() && (vat.isIntracommunityPurchase() || vat.isIntracommunityExpenses()))
			,(ctx,mod,vat) -> prorate(Mod303Key.CT_C39,mod,vat.getDeductibleQuota())
			,null
			,null)
		,CT_C40(Mod303Key.CT_C40
			,(mod -> mod.isAEAT())
			,(mod,vat) -> ( vat.isRectification() && (vat.isPurchase() || vat.isExpenses()))
			,(ctx,mod,vat) -> prorate(Mod303Key.CT_C40,mod,vat.getBase())
			,null
			,null)
		,CT_C41(Mod303Key.CT_C41
			,(mod -> mod.isAEAT())
			,(mod,vat) -> ( vat.isRectification() && (vat.isPurchase() || vat.isExpenses()))
			,(ctx,mod,vat) -> prorate(Mod303Key.CT_C41,mod,vat.getDeductibleQuota())
			,null
			,null)
		,CT_C42(Mod303Key.CT_C42
			,(mod -> mod.isAEAT())
			,(mod,vat) -> ( vat.isFarmerRegime() && !vat.isRectification() && (vat.isNationalPurchase() || vat.isOtherISPPurchase()) )
			,(ctx,mod,vat) -> prorate(Mod303Key.CT_C41,mod,vat.getDeductibleQuota())
			,null
			,null)
				
		,CT_C43(Mod303Key.CT_C43,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C44(Mod303Key.CT_C44,(mod -> mod.isAEAT()),null,null,null,null)
		
		,CT_C45(Mod303Key.CT_C45
			,(mod -> mod.isAEAT())
			,null
			,null
			,null
			,"CT_C29+CT_C31+CT_C33+CT_C35+CT_C37+CT_C39+CT_C41+CT_C42+CT_C43+CT_C44")
		,CT_C46(Mod303Key.CT_C46
			,(mod -> mod.isAEAT())
			,null
			,null
			,null
			,"CT_C27-CT_C45")

		,CT_C59(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C60(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C61(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C62(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C63(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C74(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C75(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C76(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C64(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C65(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C66(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C77(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C67(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C68(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C69(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null) 
		,CT_C70(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C71(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C80(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C81(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C82(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C83(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C84(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C85(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C86(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C87(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_C88(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		,CT_U14(Mod303Key.CT_U14,(mod -> mod.isAEAT()),null,null,null,null)
		;
		
		private Mod303Key key;
		private IModelAccepter acceptModel;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private IValueFirstIntializer firstInitializer;
		private String expression;

		private Mod303KeyDAO(Mod303Key key
				, IModelAccepter acceptModel
				, IValueAccepter acceptValue
				, IValueIntializer initializer
				, IValueFirstIntializer firstInitializer
				, String expression) {
			this.key = key;
			this.acceptModel =  acceptModel;
			this.acceptValue =  acceptValue;
			this.initializer = initializer;
			this.firstInitializer = firstInitializer;
			this.expression =  expression;
		}
		
		
		public Mod303Key getKey() {
			return key;
		}
		public boolean acceptModel(Mod303 mod) {
			return  acceptModel != null && acceptModel.accept(mod);
		}
		public boolean acceptValue(Mod303 mod,VatContext vctx) {
			return  acceptValue != null && acceptModel(mod) &&  acceptValue.accept(mod,vctx);
		}
		public void initialize(AONContext ctx,Mod303 mod,VatContext vctx) {
			if (initializer != null) {
				initializer.initialize(ctx, mod, vctx);
			}
		}
		public void firstInitialize(AONContext ctx,Mod303 mod) {
			if (firstInitializer != null) {
				firstInitializer.initialize(ctx, mod);
			}
		}
		public String getExpression() {
			return expression;
		}
		public static Mod303KeyDAO safeValueOf(Mod303 mod, String key) {
			if (AonStringUtils.isBlank(key)) return null; 
			for (Mod303KeyDAO keyDAO : Mod303KeyDAO.values()) {	
				if (keyDAO.acceptModel(mod) && keyDAO.getKey().getValue().equals(key) ) {
					return keyDAO;
				}
			}
			return null;
		}
	}

	public static Stream<Mod303> getMod303s(AONContext ctx,int domain) {
		return getModelRecords(ctx, domain,FiscalModelType.M303)
				.map( record -> map303(new Mod303(),record))
				.peek(fm -> getModelDetails(ctx,fm).forEach( detail -> fm.put( detail)))
				;
	}
	
	
	public static Mod303 getMod303(AONContext ctx,int id) {
		ctx.checkRead();
		final Mod303 mod303 = getModelRecord(ctx, id)
				.map( record -> map303(new Mod303(),record));
		if (mod303 != null) {
			getModelDetails(ctx,mod303).forEach( detail -> mod303.put( detail));	
		}
		return mod303;
		
	}
	
	public static Mod303 saveMod303(AONContext ctx, Mod303 mod303) {
		calculateMod303(ctx, mod303);
		FiscalModel fm = save(ctx, mod303);
		return getMod303(ctx, fm.getId());
	}
	
	public static Mod303 saveCommentsMod303(AONContext ctx, Mod303 mod303) {
		saveComments(ctx, mod303);
		return mod303;
	}

	public static Mod303 calculateMod303(AONContext ctx, Mod303 mod303) {
		LinkedHashMap<String, Object> mvelCtx = new LinkedHashMap<String, Object>();
		for (String key : mod303.getMap().keySet()) {
			Mod303Key mod303Key = Mod303Key.getKey(key);
			if (mod303Key != null) {
				FiscalModelDetail detail = mod303.getMap().get(key);
				mvelCtx.put(mod303Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}
		for (Mod303KeyDAO key : Mod303KeyDAO.values()) {
			if (AonStringUtils.isNotEmpty( key.getExpression()) && key.acceptModel(mod303)) {
				Object ret =  MVEL.eval( key.getExpression() , mvelCtx , mvelCtx);
				Double amount = (Double) ret;
				mvelCtx.put(key.getKey().toString(), amount);
				mod303.ensureDetail(key.getKey()).setAmount(AonMathUtils.round( amount) );
			}
		}
		return mod303; 
	}
	
	public static Mod303 initializeMod303(AONContext ctx,Mod303 mod303) {
		if (mod303 == null) {
			mod303 = new Mod303();
		}
		initializeFiscalModel(ctx, mod303);
		return mod303;
	}
	
	public static Mod303 createMod303(AONContext ctx,Mod303 mod303) {
		for (Mod303KeyDAO key : Mod303KeyDAO.values()) {
			if (key.acceptModel(mod303)) {
				FiscalModelDetail detail = mod303.ensureDetail(key.getKey());
				detail.setExpression(key.getExpression());
				key.firstInitialize(ctx, mod303);
			}
		}
		getVatBreakdown(ctx,mod303)
			.forEach( vat -> {  
				for (Mod303KeyDAO key : Mod303KeyDAO.values()) 
					if (key.acceptValue(mod303,vat)) key.initialize(ctx, mod303, vat);
			
				});
		for (FiscalModelDetail detail : mod303.getMap().values()) {
			Mod303KeyDAO key = Mod303KeyDAO.safeValueOf(mod303, detail.getType());
			if (key != null) {
				detail.setResultAmount( AonMathUtils.round(detail.getAccumulatedAmount() - detail.getDeclaredAmount()));	
				detail.setAmount( AonMathUtils.round(detail.getResultAmount() - detail.getAdjustAmount()));
			}
		}
		return calculateMod303(ctx, mod303);
	}
	public static String getMod303Info(AONContext ctx, Mod303 mod303, IModelScript<Mod303Key> script, FiscalModelKeyInfo infoKey) {
		Mod303KeyInfoDAO k = Mod303KeyInfoDAO.valueOf(infoKey.toString());
		for (Mod303KeyDAO keyDAO : Mod303KeyDAO.values()) {
			if (keyDAO.getKey() == script.getKeys()[0]) {
				return k.getInfo(ctx, mod303, script, keyDAO);
			}
		}
		return null; 
	}

	private static String getExpression(Mod303 mod303
			, IModelScript<Mod303Key> script, Mod303KeyDAO keyDAO0) {
		StringBuilder buf = new StringBuilder();
		int headerLength = 100;
		buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG,AonStringUtils.repeat(" ", headerLength)));
		buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG_BOLD,AonStringUtils.center("DETALLE DEL C\u00C1LCULO", headerLength)));
		buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG_BOLD,AonStringUtils.repeat("-", headerLength)));

		final StringBuilder expr = new StringBuilder();
		final StringBuilder resu = new StringBuilder();

		LinkedHashMap<String, Object> mvelCtx = new LinkedHashMap<String, Object>() {
			private static final long serialVersionUID = -4910560506222174407L;
			@Override
			public Object get(Object key) {
				Mod303KeyDAO keyDAO = Mod303KeyDAO.valueOf(key.toString());
				String box = " [" + AonStringUtils.leftPad(Integer.toString(keyDAO.getKey().getBox()), 3, '0')+"] ";
				String exprCopy = expr.toString();
				expr.delete(0, expr.length());
				expr.append(AonStringUtils.replace(exprCopy
						, key.toString()
						, box));
				Object value = super.get(key);
				exprCopy = resu.toString();
				resu.delete(0, resu.length());
				resu.append(AonStringUtils.replace(exprCopy
						, key.toString()
						," " + value.toString() + " "
						));
				return value;
			}
		};

		for (String keyValue : mod303.getMap().keySet()) {
			Mod303Key mod303Key = Mod303Key.getKey(keyValue);
			if (mod303Key != null) {
				FiscalModelDetail detail = mod303.getMap().get(keyValue);
				mvelCtx.put(mod303Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}


		for (Mod303Key key : script.getKeys() ) {
			Mod303KeyDAO keyDAO = Mod303KeyDAO.safeValueOf(mod303, key.getValue());
			if (keyDAO != null) {
				resu.delete(0, resu.length());
				resu.append(keyDAO.getExpression());
				expr.delete(0, expr.length());
				expr.append(keyDAO.getExpression());
				
				String box = " [" + AonStringUtils.leftPad(Integer.toString(keyDAO.getKey().getBox()), 3, '0')+"] ";
				buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG,AonStringUtils.repeat(" ", headerLength)));
				buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG_BOLD,AonStringUtils.center("Casilla: " + box + " - " + script.getLabel(), headerLength)));
				Object ret = MVEL.eval(keyDAO.getExpression(), mvelCtx, mvelCtx);
				resu.append(" = ");
				resu.append(AonMathUtils.round((Double) ret));
				expr.append(" = ");
				expr.append(box);
				buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG,"<b>F\u00F3rmula:</b> " + expr.toString()));		
				buf.append(MessageFormat.format(IRPFFormatter.DIV_MSG_BLUE_BORDER_BOTTOM,"<b>Resultado:</b> " + resu.toString()));
			}
			
		}
		return buf.toString();
	}
	
	// -------------------------------------------------------------------- INVOICES
	
	private static String getInvoicesInfo(AONContext ctx, final Mod303 mod303
			, final IModelScript<Mod303Key> script, Mod303KeyDAO keyDAO) {
		String title = "FACTURAS QUE AFECTAN A LA CONFECCI\u00D3N DEL MODELO " 
				+ mod303.getModelName() 
				+ " DEL " + mod303.getPeriod().getDescription()
				+ " DE " + mod303.getYear();
		return VATFormatter.formatInvoices(title,script.getLabel()
			,getVatBreakdown(ctx, mod303)
					.filter( br ->  keyDAO.acceptValue(mod303, br) )	
					.collect(Collectors.toCollection(LinkedList::new))
		);
	}
	
	private static String getDiffInvoicesInfo(AONContext ctx, final Mod303 mod303
			, final IModelScript<Mod303Key> script, Mod303KeyDAO keyDAO) {
//		String title = "DETALLE DEL C\u00C1LCULO POR DIFERENCIA DEL MODELO "
//			+ mod303.getModelName() 
//			+ " DEL " + mod303.getPeriod().getDescription()
//			+ " DE " + mod303.getYear();
//		return IRPFFormatter.formatDiffInvoices(title
//			,script.getLabel()
//			,script.getKeys()
//			,getEffectivePreviousModels(ctx, mod303)
////			,getPreviousModels(ctx,mod303)
//			 	.collect(Collectors.toCollection(LinkedList::new))	
//			,IRPFDAO.getInvoiceDiffIrpfBreakdown(ctx, mod303)
//				.filter( br ->  keyDAO.acceptValue(mod303, br) )	
//				.collect(Collectors.toCollection(LinkedList::new))
//		);
		return null;
	}


	// -------------------------------------------------------------------- UTIL
	public static Mod303 finish(AONContext ctx,Mod303 mod303) {
		mod303 = FiscalModelDAO.finish(ctx, mod303);
		return saveMod303(ctx, mod303);
	}
	
	public static Mod303 reopen(AONContext ctx,Mod303 mod303) {
		mod303.setDeclarationType( (String) null);
		mod303.setStatus(FiscalStatus.PENDING);
		Finance finance = mod303.getFinance();
		mod303.setFinance(null);
		mod303 = saveMod303(ctx, mod303);
		if (finance != null) {
			FinanceDAO.delete(ctx, finance.getId());
		}
		return mod303;
	}

	private static void add(Mod303Key key,Mod303 mod,double amount) {
		mod.ensureDetail(key).addAccumulatedAmount(amount);	
	}
	private static void prorate(Mod303Key key,Mod303 mod,double amount) {
		amount = AonMathUtils.round( amount * mod.getProratePercent() / 100 );
		mod.ensureDetail(key).addAccumulatedAmount(amount);	
	}
	
	private static Stream<VatContext> getVatBreakdown(final AONContext ctx, final Mod303 mod303) {
		Date fromDate = AonDateUtils.getYearFirstDay(mod303.getYear());
		Date toDate = FiscalUtils.getPeriodEnd(mod303);
		return VATDAO.getVatBreakdown(ctx,fromDate,toDate);
	}
	
	public static double getPercent1( FiscalModel model ) {
		return 4.0;
	}
	public static double getPercent2( FiscalModel model ) {
		return 10.0;
	}
	public static double getPercent3( FiscalModel model ) {
		return 21.0;
	}
	public static double getSurchargePercent1( FiscalModel model ) {
		return 0.5;
	}
	public static double getSurchargePercent2( FiscalModel model ) {
		return 1.4;
	}
	public static double getSurchargePercent3( FiscalModel model ) {
		return 5.2;
	}

}
