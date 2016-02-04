package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Record;
import org.mvel2.MVEL;

import com.esferalia.aon.jooq.tables.records.FsModelRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Mod111KeyInfo;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod111DAO extends FiscalModelDAO {
	
	@FunctionalInterface
	private static interface IModelInfoProvider {
		String obtain(AONContext ctx, Mod111 mod,Mod111Key key,Mod111KeyDAO keyDAO);
	}
	private static final String INFO_MSG = "<pre class='aon-fixed-font aon-font-medium aon-margin-bottom'>{0}<pre>";
	private static final String NONE_INFO = "No hay datos";
	private static enum Mod111KeyInfoDAO {
		 NONE( ((ctx, mod, key,keyDAO) -> MessageFormat.format(INFO_MSG, NONE_INFO)) )
		,INVOICE( ((ctx, mod, key,keyDAO) -> MessageFormat.format(INFO_MSG, getInvoicesInfo(ctx, mod, key,keyDAO))))
		,SALARY( ((ctx, mod, key,keyDAO) -> MessageFormat.format(INFO_MSG, getSalaryInfo(ctx, mod, key,keyDAO))) )
		,COMPUTE( ((ctx, mod, key,keyDAO) -> MessageFormat.format(INFO_MSG, NONE_INFO)) )
		;
		private IModelInfoProvider provider;
		
		private Mod111KeyInfoDAO (IModelInfoProvider provider) {
			this.provider = provider;
		}
		public String getInfo(AONContext ctx, Mod111 mod,Mod111Key key,Mod111KeyDAO keyDAO) {
			return provider.obtain(ctx, mod, key,keyDAO);
		}
	}
	
	@FunctionalInterface
	private static interface IModelAccepter {
		boolean accept(Mod111 mod);
	}
	@FunctionalInterface
	public static interface IValueAccepter {
		boolean accept(Mod111 mod,IrpfBreakdown rc);
	}
	@FunctionalInterface
	public static interface IValueIntializer {
		void initialize(AONContext ctx,Mod111 mod,Set<String> docs,IrpfBreakdown br);
	}
	
	private static void addPerceptor(Mod111Key key,Mod111 mod,Set<String> docs,IrpfBreakdown br) {
		if (!docs.contains(br.getDocument())) {
			docs.add(br.getDocument());
			mod.ensureDetail(key).addAmount(1);
		}
	}

	private static enum Mod111KeyDAO {
		// *************************************************************************
		// *************************************************************** ALAVA ***
		// *************************************************************************
		 AR_907(Mod111Key.AR_907
			, (mod -> mod.isAraba() && mod.getYear() > 2015)
			,null,null,null)
		,AR_908(Mod111Key.AR_908
			, (mod -> mod.isAraba() && mod.getYear() > 2015)
			,null,null,null)
		,AR_909(Mod111Key.AR_909
			, (mod -> mod.isAraba() && mod.getYear() > 2015)
			,null,null,null)
		,AR_C50(Mod111Key.AR_C50
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isSalaryRetention()
			, (ctx,mod,docs,br) -> addPerceptor(Mod111Key.AR_C50,mod,docs,br)
			,null)
		,AR_C60(Mod111Key.AR_C60
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isSalaryRetention()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.AR_C60).addAmount(br.getBase())
			,null)
		,AR_C70(Mod111Key.AR_C70
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isSalaryRetention()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.AR_C70).addAmount(br.getQuota())
			,null)
		,AR_C51(Mod111Key.AR_C51, (mod -> mod.isAraba()),null,null,null)
		,AR_C61(Mod111Key.AR_C61, (mod -> mod.isAraba()),null,null,null)
		,AR_C71(Mod111Key.AR_C71, (mod -> mod.isAraba()),null,null,null)
		,AR_C52(Mod111Key.AR_C52, (mod -> mod.isAraba()),null,null,null)
		,AR_C62(Mod111Key.AR_C62, (mod -> mod.isAraba()),null,null,null)
		,AR_C72(Mod111Key.AR_C72, (mod -> mod.isAraba()),null,null,null)
		,AR_C53(Mod111Key.AR_C53, (mod -> mod.isAraba()),null,null,null)
		,AR_C63(Mod111Key.AR_C63, (mod -> mod.isAraba()),null,null,null)
		,AR_C73(Mod111Key.AR_C73, (mod -> mod.isAraba()),null,null,null)
		,AR_C54(Mod111Key.AR_C54 
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isNotObjectiveRegime() && (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,br) -> addPerceptor(Mod111Key.AR_C54,mod,docs,br)
			,null)
		,AR_C64(Mod111Key.AR_C64
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isNotObjectiveRegime() && (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.AR_C64).addAmount(br.getBase())
			,null)
		,AR_C74(Mod111Key.AR_C74
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isNotObjectiveRegime() && (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.AR_C74).addAmount(br.getQuota())
			,null)
			
		,AR_C58(Mod111Key.AR_C58 
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isObjectiveRegime() && (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,br) -> addPerceptor(Mod111Key.AR_C58,mod,docs,br)
			,null)
		,AR_C68(Mod111Key.AR_C68 
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isObjectiveRegime() && (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.AR_C68).addAmount(br.getBase())
			,null)
		,AR_C78(Mod111Key.AR_C78 
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isObjectiveRegime() && (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.AR_C78).addAmount(br.getQuota())
			,null)
		,AR_C55(Mod111Key.AR_C55
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isFarmer()
			, (ctx,mod,docs,br) -> addPerceptor(Mod111Key.AR_C55,mod,docs,br)
			,null)
		,AR_C65(Mod111Key.AR_C65
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isFarmer()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.AR_C65).addAmount(br.getBase())
			,null)
		,AR_C75(Mod111Key.AR_C75
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isFarmer()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.AR_C75).addAmount(br.getQuota())
			,null)
		,AR_C56(Mod111Key.AR_C56
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isSalaryInKindRetention()
			, (ctx,mod,docs,br) -> addPerceptor(Mod111Key.AR_C56,mod,docs,br)
			,null)
		,AR_C66(Mod111Key.AR_C66
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isSalaryInKindRetention()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.AR_C66).addAmount(br.getBase())
			,null)
		,AR_C76(Mod111Key.AR_C76
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isSalaryInKindRetention()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.AR_C76).addAmount(br.getQuota())
			,null)
		,AR_C57(Mod111Key.AR_C57, (mod -> mod.isAraba()),null,null,null)
		,AR_C67(Mod111Key.AR_C67, (mod -> mod.isAraba()),null,null,null)
		,AR_C77(Mod111Key.AR_C77, (mod -> mod.isAraba()),null,null,null)
		,AR_C80(Mod111Key.AR_C80
			, (mod -> mod.isAraba() && mod.getYear() > 2015)
			, null,null, "AR_C50+AR_C51+AR_C52+AR_C53+AR_C54+AR_C58+AR_C55+AR_C56+AR_C57")
		,AR_C81(Mod111Key.AR_C81
			, (mod -> mod.isAraba() && mod.getYear() > 2015)
			, null,null, "AR_C60+AR_C61+AR_C62+AR_C63+AR_C64+AR_C68+AR_C65+AR_C66+AR_C67")
		,AR_C82(Mod111Key.AR_C82
			, (mod -> mod.isAraba())
			, null,null, "AR_C70+AR_C71+AR_C72+AR_C73+AR_C74+AR_C78+AR_C75+AR_C76+AR_C77")
		,AR_C83(Mod111Key.AR_C83, (mod -> mod.isAraba() && mod.getYear() > 2015),null,null,null)
		,AR_C84(Mod111Key.AR_C84, (mod -> mod.isAraba()),null,null,null)
		,AR_C85(Mod111Key.AR_C85, (mod -> mod.isAraba()),null,null,null)
		,AR_C87(Mod111Key.AR_C87
			, (mod -> mod.isAraba() && mod.getYear() <= 2015)
			, null,null, "AR_C82+AR_C84+AR_C85")
		,AR_C87B(Mod111Key.AR_C87
			, (mod -> mod.isAraba() && mod.getYear() > 2015)
			, null,null, "AR_C82-AR_C83+AR_C84+AR_C85")
		
		// *************************************************************************
		// ************************************************************* BIZKAIA ***
		// *************************************************************************
		,BZ_C01 (Mod111Key.BZ_C01 
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  br.isSalaryRetention()
			, (ctx,mod,docs,br) -> addPerceptor(Mod111Key.BZ_C01,mod,docs,br)
			,null)
		,BZ_C12 (Mod111Key.BZ_C12 
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  br.isSalaryRetention()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.BZ_C12).addAmount(br.getBase())
			,null)
		,BZ_C23 (Mod111Key.BZ_C23 
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  br.isSalaryRetention()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.BZ_C23).addAmount(br.getQuota())
			,null)
		,BZ_C02 (Mod111Key.BZ_C02 , (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C13 (Mod111Key.BZ_C13 , (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C24 (Mod111Key.BZ_C24 , (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C03 (Mod111Key.BZ_C03 , (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C14 (Mod111Key.BZ_C14 , (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C25 (Mod111Key.BZ_C25 , (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C04 (Mod111Key.BZ_C04 , (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C15 (Mod111Key.BZ_C15 , (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C26 (Mod111Key.BZ_C26 , (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C05 (Mod111Key.BZ_C05 , (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C16 (Mod111Key.BZ_C16 , (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C27 (Mod111Key.BZ_C27 , (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C06 (Mod111Key.BZ_C06 , (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C17 (Mod111Key.BZ_C17 , (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C28 (Mod111Key.BZ_C28 , (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C07 (Mod111Key.BZ_C07 
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,br) -> addPerceptor(Mod111Key.BZ_C07,mod,docs,br)
			,null)
		,BZ_C18 (Mod111Key.BZ_C18 
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.BZ_C18).addAmount(br.getBase())
			,null)
		,BZ_C29 (Mod111Key.BZ_C29 
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.BZ_C29).addAmount(br.getQuota())
			,null)
		,BZ_C50 (Mod111Key.BZ_C50 
			, (mod ->  mod.isBizkaia() && mod.getPeriod().isQuarterPeriod())
			, (mod,br) ->  (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,br) -> addPerceptor(Mod111Key.BZ_C50,mod,docs,br)
			,null)
		,BZ_C51 (Mod111Key.BZ_C51 
			, (mod ->  mod.isBizkaia() && mod.getPeriod().isQuarterPeriod())
			,null,null,null)
		,BZ_C52 (Mod111Key.BZ_C52 
			, (mod ->  mod.isBizkaia() && mod.getPeriod().isQuarterPeriod())
			,null,null,null)
		,BZ_C08 (Mod111Key.BZ_C08 
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  br.isFarmer()
			, (ctx,mod,docs,br) -> addPerceptor(Mod111Key.BZ_C08,mod,docs,br)
			,null)
		,BZ_C19 (Mod111Key.BZ_C19 
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  br.isFarmer()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.BZ_C19).addAmount(br.getBase())
			,null)
		,BZ_C30 (Mod111Key.BZ_C30 
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  br.isFarmer()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.BZ_C30).addAmount(br.getQuota())
			,null)
		,BZ_C09 (Mod111Key.BZ_C09 
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  br.isSalaryInKindRetention()
			, (ctx,mod,docs,br) -> addPerceptor(Mod111Key.BZ_C09,mod,docs,br)
			,null)
		,BZ_C20 (Mod111Key.BZ_C20 
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  br.isSalaryInKindRetention()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.BZ_C20).addAmount(br.getBase())
			,null)
		,BZ_C31 (Mod111Key.BZ_C31 
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  br.isSalaryInKindRetention()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.BZ_C31).addAmount(br.getQuota())
			,null)
		,BZ_C10 (Mod111Key.BZ_C10 , (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C21 (Mod111Key.BZ_C21 , (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C32 (Mod111Key.BZ_C32 , (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C11 (Mod111Key.BZ_C11 , (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C22 (Mod111Key.BZ_C22 , (mod -> mod.isBizkaia()),null,null,null)
		,BZ_C33 (Mod111Key.BZ_C33 , (mod -> mod.isBizkaia()),null,null,null)
		
		,BZ_C34M(Mod111Key.BZ_C34T
			, (mod ->  mod.isBizkaia() && mod.getPeriod().isMonthPeriod())
			, null,null, "BZ_C01+BZ_C02+BZ_C03+BZ_C04+BZ_C05+BZ_C06+BZ_C07+BZ_C08+BZ_C09+BZ_C10+BZ_C11")
		,BZ_C35M(Mod111Key.BZ_C35T
			, (mod ->  mod.isBizkaia() && mod.getPeriod().isMonthPeriod())
			, null,null, "BZ_C12+BZ_C13+BZ_C14+BZ_C15+BZ_C16+BZ_C17+BZ_C18+BZ_C19+BZ_C20+BZ_C21+BZ_C22")
		,BZ_C36M(Mod111Key.BZ_C36T
			, (mod ->  mod.isBizkaia() && mod.getPeriod().isMonthPeriod())
			, null,null, "BZ_C23+BZ_C24+BZ_C25+BZ_C26+BZ_C27+BZ_C28+BZ_C29+BZ_C30+BZ_C31+BZ_C32+BZ_C33")
		
		,BZ_C34T(Mod111Key.BZ_C34T
			, (mod ->  mod.isBizkaia() && mod.getPeriod().isQuarterPeriod())
			, null,null, "BZ_C01+BZ_C02+BZ_C03+BZ_C04+BZ_C05+BZ_C06+BZ_C07+BZ_C50+BZ_C08+BZ_C09+BZ_C10+BZ_C11")
		,BZ_C35T(Mod111Key.BZ_C35T
			, (mod ->  mod.isBizkaia() && mod.getPeriod().isQuarterPeriod())
			, null,null, "BZ_C12+BZ_C13+BZ_C14+BZ_C15+BZ_C16+BZ_C17+BZ_C18+BZ_C51+BZ_C19+BZ_C20+BZ_C21+BZ_C22")
		,BZ_C36T(Mod111Key.BZ_C36T
			, (mod ->  mod.isBizkaia() && mod.getPeriod().isQuarterPeriod())
			, null,null, "BZ_C23+BZ_C24+BZ_C25+BZ_C26+BZ_C27+BZ_C28+BZ_C29+BZ_C52+BZ_C30+BZ_C31+BZ_C32+BZ_C33")
		
		,BZ_C39T(Mod111Key.BZ_C39 , (mod -> mod.isBizkaia())
			, null,null, "BZ_C36T")
		
		// *************************************************************************
		// **************************************************** COMMON TERRITORY ***
		// *************************************************************************
		,CT_C01(Mod111Key.CT_C01
			, (mod -> mod.isAEAT())
			, (mod,br) ->  br.isSalaryRetention()
			, (ctx,mod,docs,br) -> addPerceptor(Mod111Key.CT_C01,mod,docs,br)
			,null)
		,CT_C02(Mod111Key.CT_C02
			, (mod -> mod.isAEAT())
			, (mod,br) ->  br.isSalaryRetention()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.CT_C02).addAmount(br.getBase())
			,null)
		,CT_C03(Mod111Key.CT_C03
			, (mod -> mod.isAEAT())
			, (mod,br) ->  br.isSalaryRetention()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.CT_C03).addAmount(br.getQuota())
			,null)
		,CT_C04(Mod111Key.CT_C04
			, (mod -> mod.isAEAT())
			, (mod,br) ->  br.isSalaryInKindRetention()
			, (ctx,mod,docs,br) -> addPerceptor(Mod111Key.CT_C04,mod,docs,br)
			,null)
		,CT_C05(Mod111Key.CT_C05
			, (mod -> mod.isAEAT())
			, (mod,br) ->  br.isSalaryInKindRetention()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.CT_C05).addAmount(br.getBase())
			,null)
		,CT_C06(Mod111Key.CT_C06
			, (mod -> mod.isAEAT())
			, (mod,br) ->  br.isSalaryInKindRetention()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.CT_C06).addAmount(br.getQuota())
			,null)
		,CT_C07(Mod111Key.CT_C07
			, (mod -> mod.isAEAT())
			, (mod,br) -> br.isProfessional() || br.isFarmer() || br.isTransportOperator()
			, (ctx,mod,docs,br) -> addPerceptor(Mod111Key.CT_C07,mod,docs,br)
			,null)
		,CT_C08(Mod111Key.CT_C08
			, (mod -> mod.isAEAT())
			, (mod,br) -> br.isProfessional() || br.isFarmer() || br.isTransportOperator()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.CT_C08).addAmount(br.getBase())
			,null)
		,CT_C09(Mod111Key.CT_C09
			, (mod -> mod.isAEAT())
			, (mod,br) -> br.isProfessional() || br.isFarmer() || br.isTransportOperator()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.CT_C09).addAmount(br.getQuota())
			,null)
		,CT_C10(Mod111Key.CT_C10, (mod -> mod.isAEAT()),null,null,null)
		,CT_C11(Mod111Key.CT_C11, (mod -> mod.isAEAT()),null,null,null)
		,CT_C12(Mod111Key.CT_C12, (mod -> mod.isAEAT()),null,null,null)
		,CT_C13(Mod111Key.CT_C13, (mod -> mod.isAEAT()),null,null,null)
		,CT_C14(Mod111Key.CT_C14, (mod -> mod.isAEAT()),null,null,null)
		,CT_C15(Mod111Key.CT_C15, (mod -> mod.isAEAT()),null,null,null)
		,CT_C16(Mod111Key.CT_C16, (mod -> mod.isAEAT()),null,null,null)
		,CT_C17(Mod111Key.CT_C17, (mod -> mod.isAEAT()),null,null,null)
		,CT_C18(Mod111Key.CT_C18, (mod -> mod.isAEAT()),null,null,null)
		,CT_C19(Mod111Key.CT_C19, (mod -> mod.isAEAT()),null,null,null)
		,CT_C20(Mod111Key.CT_C20, (mod -> mod.isAEAT()),null,null,null)
		,CT_C21(Mod111Key.CT_C21, (mod -> mod.isAEAT()),null,null,null)
		,CT_C22(Mod111Key.CT_C22, (mod -> mod.isAEAT()),null,null,null)
		,CT_C23(Mod111Key.CT_C23, (mod -> mod.isAEAT()),null,null,null)
		,CT_C24(Mod111Key.CT_C24, (mod -> mod.isAEAT()),null,null,null)
		,CT_C25(Mod111Key.CT_C25, (mod -> mod.isAEAT()),null,null,null)
		,CT_C26(Mod111Key.CT_C26, (mod -> mod.isAEAT()),null,null,null)
		,CT_C27(Mod111Key.CT_C27, (mod -> mod.isAEAT()),null,null,null)
		,CT_C28(Mod111Key.CT_C28
			, (mod -> mod.isAEAT())
			, null,null, "CT_C03+CT_C06+CT_C09+CT_C12+CT_C15+CT_C18+CT_C21+CT_C24" )
		,CT_C29(Mod111Key.CT_C29, (mod -> mod.isAEAT()),null,null,null)
		,CT_C30(Mod111Key.CT_C30
			, (mod -> mod.isAEAT())
			, null,null, "CT_C28-CT_C29" )
		
		// *************************************************************************
		// ************************************************************ GIPUZKOA ***
		// *************************************************************************
		,GP_C01(Mod111Key.GP_C01
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isSalaryRetention()
			, (ctx,mod,docs,br) -> addPerceptor(Mod111Key.GP_C01,mod,docs,br)
			,null)
		,GP_C02(Mod111Key.GP_C02
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isSalaryRetention()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.GP_C02).addAmount(br.getBase())
			,null)
		,GP_C03(Mod111Key.GP_C03
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isSalaryRetention()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.GP_C03).addAmount(br.getQuota())
			,null)
		,GP_C04(Mod111Key.GP_C04
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isProfessional() || br.isTransportOperator()
			, (ctx,mod,docs,br) -> addPerceptor(Mod111Key.GP_C04,mod,docs,br)
			,null)
		,GP_C05(Mod111Key.GP_C05
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isProfessional() || br.isTransportOperator()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.GP_C05).addAmount(br.getBase())
			,null)
		,GP_C06(Mod111Key.GP_C06
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isProfessional() || br.isTransportOperator()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.GP_C06).addAmount(br.getQuota())
			,null)
		,GP_C07(Mod111Key.GP_C07
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isFarmer()
			, (ctx,mod,docs,br) -> addPerceptor(Mod111Key.GP_C07,mod,docs,br)
			,null)
		,GP_C08(Mod111Key.GP_C08
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isFarmer()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.GP_C08).addAmount(br.getBase())
			,null)
		,GP_C09(Mod111Key.GP_C09
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isFarmer()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.GP_C09).addAmount(br.getQuota())
			,null)
		,GP_C10(Mod111Key.GP_C10, (mod -> mod.isGipuzkoa()),null,null,null)
		,GP_C11(Mod111Key.GP_C11, (mod -> mod.isGipuzkoa()),null,null,null)
		,GP_C12(Mod111Key.GP_C12, (mod -> mod.isGipuzkoa()),null,null,null)
		,GP_C13(Mod111Key.GP_C13
			, (mod -> mod.isGipuzkoa())
			,null,null, "GP_C02+GP_C05+GP_C08+GP_C11" )
		,GP_C14(Mod111Key.GP_C14
			, (mod -> mod.isGipuzkoa())
			,null,null, "GP_C03+GP_C06+GP_C09+GP_C12" )
		,GP_C15(Mod111Key.GP_C15
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isSalaryInKindRetention()
			, (ctx,mod,docs,br) -> addPerceptor(Mod111Key.GP_C15,mod,docs,br)
			,null)
		,GP_C16(Mod111Key.GP_C16
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isSalaryInKindRetention()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.GP_C16).addAmount(br.getBase())
			,null)
		,GP_C17(Mod111Key.GP_C17
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isSalaryInKindRetention()
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.GP_C17).addAmount(br.getQuota())
			,null)
		,GP_C18(Mod111Key.GP_C18, (mod -> mod.isGipuzkoa()),null,null,null)
		,GP_C19(Mod111Key.GP_C19, (mod -> mod.isGipuzkoa()),null,null,null)
		,GP_C20(Mod111Key.GP_C20, (mod -> mod.isGipuzkoa()),null,null,null)
		,GP_C21(Mod111Key.GP_C21, (mod -> mod.isGipuzkoa()),null,null,null)
		,GP_C22(Mod111Key.GP_C22, (mod -> mod.isGipuzkoa()),null,null,null)
		,GP_C23(Mod111Key.GP_C23, (mod -> mod.isGipuzkoa()),null,null,null)
		,GP_C24(Mod111Key.GP_C24, (mod -> mod.isGipuzkoa()),null,null,null)
		,GP_C25(Mod111Key.GP_C25, (mod -> mod.isGipuzkoa()),null,null,null)
		,GP_C26(Mod111Key.GP_C26, (mod -> mod.isGipuzkoa()),null,null,null)
		,GP_C27(Mod111Key.GP_C27
			, (mod -> mod.isGipuzkoa())
			,null,null, "GP_C16+GP_C19+GP_C22+GP_C25")
		,GP_C28(Mod111Key.GP_C28
			, (mod -> mod.isGipuzkoa())
			,null,null, "GP_C17+GP_C20+GP_C23+GP_C26" )
		,GP_C29(Mod111Key.GP_C29
			, (mod -> mod.isGipuzkoa())
			,null,null, "GP_C14+GP_C28" )

		// *************************************************************************
		// ************************************************************* NAVARRA ***
		// *************************************************************************
		,NF_A1(Mod111Key.NF_A1
			, (mod -> mod.isNavarra())
			, (mod,br) -> (br.isProfessional() || br.isTransportOperator() || br.isFarmer() || br.isSalaryRetention() || br.isSalaryInKindRetention()) 
			, (ctx,mod,docs,br) -> mod.ensureDetail(Mod111Key.NF_A1).addAmount(br.getQuota())
			,null)
		;
		
		private Mod111Key key;
		private IModelAccepter acceptModel;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private String expression;

		private Mod111KeyDAO(Mod111Key key, IModelAccepter acceptModel, IValueAccepter acceptValue
				,IValueIntializer initializer,String expression) {
			this.key = key;
			this.acceptModel =  acceptModel;
			this.acceptValue =  acceptValue;
			this.initializer = initializer;
			this.expression =  expression;
		}
		
		
		public Mod111Key getKey() {
			return key;
		}
		public boolean acceptModel(Mod111 mod) {
			return  (acceptModel.accept(mod));
		}
		public boolean acceptValue(Mod111 mod,IrpfBreakdown  br) {
			return  acceptValue != null && acceptModel(mod) &&  acceptValue.accept(mod,br);
		}
		public void initialize(AONContext ctx,Mod111 mod,Set<String> docs,IrpfBreakdown  br) {
			if (initializer != null) {
				initializer.initialize(ctx, mod, docs, br);
			}
		}
		public String getExpression() {
			return expression;
		}
	}

	public static Stream<Mod111> getMod111s(AONContext ctx,int domain) {
		return ctx.getDslContext().selectFrom(FS_MODEL)
				.where(FS_MODEL.DOMAIN.eq(domain))
				.and(FS_MODEL.MODEL.eq( FiscalModelType.M111.getValue() ))
				.orderBy(FS_MODEL.YEAR.desc(),FS_MODEL.MODEL.asc(),FS_MODEL.PERIOD.desc())
				.fetch()
				.stream()
				.map( record -> Mod111DAO.map(new Mod111(),record));
	}
	
	public static Mod111 getMod111(AONContext ctx,int id) {
		ctx.checkRead();
		FsModelRecord record = ctx.getDslContext().selectFrom(FS_MODEL)
			.where(FS_MODEL.ID.eq(id))
			.fetchOne();
		if (record != null) {
			Mod111 mod111 = new Mod111(); 
			populate(mod111,record);
			fillModelDetails(ctx,mod111);
			return mod111;
		}
		return null;
	}
	
	public static Mod111 saveMod111(AONContext ctx, Mod111 mod111) {
		calculateMod111(ctx, mod111);
		FiscalModel fm = save(ctx, mod111);
		return getMod111(ctx, fm.getId());
	}
	
	public static Mod111 calculateMod111(AONContext ctx, Mod111 mod111) {
		LinkedHashMap<String, Object> mvelCtx = new LinkedHashMap<String, Object>();
		for (String key : mod111.getMap().keySet()) {
			Mod111Key mod111Key = Mod111Key.getKey(key);
			if (mod111Key != null) {
				FiscalModelDetail detail = mod111.getMap().get(key);
				mvelCtx.put(mod111Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}
		for (Mod111KeyDAO key : Mod111KeyDAO.values()) {
			if (AonStringUtils.isNotEmpty( key.getExpression()) && key.acceptModel(mod111)) {
				Object ret =  MVEL.eval( key.getExpression() , mvelCtx , mvelCtx);
				Double amount = (Double) ret;
				mvelCtx.put(key.getKey().toString(), amount);
				mod111.ensureDetail(key.getKey()).setAmount(AonMathUtils.round( amount) );
			}
		}
		return mod111; 
	}
	
	public static Mod111 initializeMod111(AONContext ctx,Mod111 mod111) {
		if (mod111 == null) {
			mod111 = new Mod111();
		}
		initializeFiscalModel(ctx, mod111);
		return mod111;
	}
	
	public static Mod111 createMod111(AONContext ctx,Mod111 mod111) {
		for (Mod111KeyDAO key : Mod111KeyDAO.values()) {
			if (key.acceptModel(mod111)) {
				FiscalModelDetail detail = mod111.ensureDetail(key.getKey());
				detail.setExpression(key.getExpression());
			}
		}
		createFromInvoices(ctx,mod111);
		createFromSalary(ctx,mod111);
		return calculateMod111(ctx, mod111);
	}

	public static String getMod111Info(AONContext ctx, Mod111 mod111, Mod111Key key, Mod111KeyInfo infoKey) {
		Mod111KeyInfoDAO k = Mod111KeyInfoDAO.valueOf(infoKey.toString());
		for (Mod111KeyDAO keyDAO : Mod111KeyDAO.values()) {
			if (keyDAO.getKey() == key) {
				return k.getInfo(ctx, mod111, key, keyDAO);
			}
		}
		return null; 
	}

	// -------------------------------------------------------------------- SALARIES
	private static void createFromSalary(final AONContext ctx, final Mod111 mod111) {
		final Set<String> docs = new HashSet<String>();
		getSalaryIrpfBreakdown(ctx, mod111)
			.forEach(
					br -> {
						for (Mod111KeyDAO key : Mod111KeyDAO.values()) {
							if (key.acceptValue(mod111,br)) {
								key.initialize(ctx, mod111, docs, br);
							};
						}
						
					});
	}

	private static String getSalaryInfo(AONContext ctx, final Mod111 mod111, final Mod111Key key, Mod111KeyDAO keyDAO) {
		return IRPFFormatter.formatSalaries(
				getSalaryIrpfBreakdown(ctx, mod111)
					.filter( br ->  keyDAO.acceptValue(mod111, br) )	
					.collect(Collectors.toCollection(LinkedList::new))
				);
	}
	
	// -------------------------------------------------------------------- INVOICES
	private static void createFromInvoices(final AONContext ctx, final Mod111 mod111) {
		final Set<String> docs = new HashSet<String>();
		getInvoiceIrpfBreakdown(ctx, mod111)
				.forEach(br -> {
						for (Mod111KeyDAO key : Mod111KeyDAO.values()) {
							if (key.acceptValue(mod111,br)) {
								key.initialize(ctx, mod111, docs, br);
							};
						}
				});
	}
	
	private static String getInvoicesInfo(AONContext ctx, final Mod111 mod111, final Mod111Key key, Mod111KeyDAO keyDAO) {
		return IRPFFormatter.formatInvoices(
				getInvoiceIrpfBreakdown(ctx, mod111)
					.filter( br ->  keyDAO.acceptValue(mod111, br) )	
					.collect(Collectors.toCollection(LinkedList::new))
			);
	}

	// -------------------------------------------------------------------- STREAM FUNCTIONS
	private static Stream<IrpfBreakdown> getSalaryIrpfBreakdown(final AONContext ctx, final Mod111 mod111) {
		java.sql.Date dateFrom = AonDateUtils.toSql( getPeriodStart(mod111));	
		java.sql.Date dateTo = AonDateUtils.toSql( getPeriodEnd(mod111));
		return ctx.getDslContext()
			.select(SALARY.ISSUE_DATE
				,SALARY.EMPLOYEE_DOCUMENT,SALARY.EMPLOYEE_NAME
				,SALARY.IRPF_BASE, SALARY.MONEY_IRPF_BASE
				,SALARY.INKIND_IRPF_BASE,SALARY.TOTAL_IRPF)
			.from(SALARY)
			.join(CONTRACT).on(SALARY.CONTRACT.equal(CONTRACT.ID))
			.join(WORKPLACE).on(CONTRACT.WORKPLACE.equal(WORKPLACE.ID))
			.where(SALARY.DOMAIN.equal(mod111.getDomain()))
				.and(SALARY.ISSUE_DATE.between(dateFrom,dateTo))
				.and(WORKPLACE.ECONOMICAGREEMENT.equal(mod111.getAdministration().getValue()))
			.fetch()
			.stream()
		.map( new IrpfSalaryBreakdown() );
		
	}
	private static Stream<IrpfBreakdown> getInvoiceIrpfBreakdown(final AONContext ctx, final Mod111 mod111) {
		java.sql.Date dateFrom = AonDateUtils.toSql( getPeriodStart(mod111));	
		java.sql.Date dateTo = AonDateUtils.toSql( getPeriodEnd(mod111));
		return 	ctx.getDslContext()
			.select(INVOICE.ID
					,INVOICE.TYPE,INVOICE.SERIES,INVOICE.NUMBER,INVOICE.REFERENCE_CODE
					,INVOICE.ISSUE_DATE,INVOICE.TAX_DATE
					,INVOICE.RDOCUMENT,INVOICE.RNAME
					,INVOICE_TAX.WITHHOLDING_TYPE,ENTERPRISE_ACTIVITY.RETENTION_REGIME
					,INVOICE_TAX.BASE,INVOICE_TAX.PERCENTAGE,INVOICE_TAX.QUOTA)
				.from(INVOICE)
				.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
				.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
				.leftOuterJoin(ENTERPRISE_ACTIVITY).on(INVOICE.ACTIVITY.equal(ENTERPRISE_ACTIVITY.ID))
				.where(INVOICE.DOMAIN.equal(mod111.getDomain()))
					.and(INVOICE.TAX_DATE.between(dateFrom,dateTo))
					.and(INVOICE.TYPE.notEqual(InvoiceType.SALES.value() ))
					.and(INVOICE_TAX.TAX_TYPE.equal(TaxType.RETENTION.value()))
				.orderBy(INVOICE.RDOCUMENT,INVOICE.ISSUE_DATE)
				.fetch()
				.stream()
				.map( new IrpfInvoiceBreakdown() );
	}

	// -------------------------------------------------------------------- MAP FUNCTIONS
	
	private static class IrpfSalaryBreakdown implements Function<Record, IrpfBreakdown> {
		@Override
		public IrpfBreakdown apply(Record rec) {
			double base = rec.getValue(SALARY.IRPF_BASE);
			double quota = rec.getValue(SALARY.TOTAL_IRPF);
			double moneyBase = base;
			double moneyQuota = quota;
			double inKindBase = rec.getValue(SALARY.INKIND_IRPF_BASE);
			double inKindQuota = 0;
			if (inKindBase != 0) {
				moneyBase = rec.getValue(SALARY.MONEY_IRPF_BASE);
				moneyQuota = AonMathUtils.round( moneyBase * quota  / base ); 	
				inKindQuota = AonMathUtils.round( quota - moneyQuota);
			} else {
				moneyBase = base;
				moneyQuota = quota;
			}
			
			return new IrpfBreakdown()
					.setFromSalary(true)
					.setDocument(rec.getValue(SALARY.EMPLOYEE_DOCUMENT))
					.setName(rec.field(SALARY.EMPLOYEE_NAME) != null ? rec.getValue(SALARY.EMPLOYEE_NAME) : null)
					.setIssueDate(rec.field(SALARY.ISSUE_DATE) != null ? rec.getValue(SALARY.ISSUE_DATE): null)
					.setMoneyBase(moneyBase)
					.setMoneyQuota(moneyQuota)
					.setInKindBase(inKindBase)
					.setInKindQuota(inKindQuota)
					;
		}
	}

	private static class IrpfInvoiceBreakdown implements Function<Record, IrpfBreakdown> {
		@Override
		public IrpfBreakdown apply(Record rec) {
			double base = rec.getValue(INVOICE_TAX.BASE);
			double percent = rec.getValue(INVOICE_TAX.PERCENTAGE);
			double quota = rec.getValue(INVOICE_TAX.QUOTA);
			if (AonMathUtils.isZero(quota)) {
				quota =  AonMathUtils.round(base * percent / 100);
			}
			Byte regime = rec.getValue(ENTERPRISE_ACTIVITY.RETENTION_REGIME);
			return new IrpfBreakdown()
					.setFromSalary(false)
					.setInvoice(rec.getValue(INVOICE.ID))
					.setInvoiceType(AonEnumUtils.enumValue(InvoiceType.class,rec.getValue(INVOICE.TYPE)))
					.setSeries(rec.getValue(INVOICE.SERIES))
					.setNumber(rec.getValue(INVOICE.NUMBER))
					.setReferenceCode(rec.getValue(INVOICE.REFERENCE_CODE))
					.setDocument(rec.getValue(INVOICE.RDOCUMENT))
					.setName(rec.getValue(INVOICE.RNAME))
					.setIssueDate(rec.getValue(INVOICE.ISSUE_DATE))
					.setTaxDate(rec.getValue(INVOICE.TAX_DATE))
					.setWithholdingType(AonEnumUtils.enumValue(WithholdingType.class,rec.getValue(INVOICE_TAX.WITHHOLDING_TYPE)))
					.setIRPFRegime(regime == null? null : IRPFRegime.values()[regime])
					.setBase(base)
					.setPercent(percent)
					.setQuota(quota)
					;
		}
	}

	// -------------------------------------------------------------------- UTIL

	private static Date getPeriodStart( FiscalModel fiscalModel) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, fiscalModel.getYear());
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.MONTH, fiscalModel.getPeriod().getStartMonth());
		return c.getTime();
	}
	
	private static Date getPeriodEnd( FiscalModel fiscalModel) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, fiscalModel.getPeriod().getDueMonth() + 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.YEAR, fiscalModel.getYear());
		c.add(Calendar.DAY_OF_MONTH, -1);
		return c.getTime();
	}
}
