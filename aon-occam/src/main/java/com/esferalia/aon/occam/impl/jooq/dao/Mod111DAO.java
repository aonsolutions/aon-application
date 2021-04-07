package com.esferalia.aon.occam.impl.jooq.dao;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mvel2.MVEL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod111DAO extends FiscalModelDAO {
	
	@FunctionalInterface
	private static interface IModelInfoProvider {
		String obtain(AONContext ctx, Mod111 mod,IModelScript<Mod111Key> script,Mod111KeyDAO keyDAO);
	}
	private static final String INFO_MSG = "<pre class='aon-fixed-font aon-font-medium aon-margin-bottom'>{0}<pre>";
	private static final String NONE_INFO = "No hay datos";
	private static enum Mod111KeyInfoDAO {
		 NONE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, NONE_INFO)) )
		,INVOICE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getInvoicesInfo(ctx, mod, script,keyDAO))))
		,DIFF_INVOICE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getDiffInvoicesInfo(ctx, mod, script,keyDAO))))
		,SALARY( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getSalaryInfo(ctx, mod, script,keyDAO))) )
		,SALARY_IN_KIND( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getSalaryInKindInfo(ctx, mod, script,keyDAO))) )
		,DIFF_SALARY( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getDiffSalaryInfo(ctx, mod, script,keyDAO))))
		,COMPUTE( ((ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getExpression(mod, script,keyDAO))))
		;
		private IModelInfoProvider provider;
		
		private Mod111KeyInfoDAO (IModelInfoProvider provider) {
			this.provider = provider;
		}
		public String getInfo(AONContext ctx, Mod111 mod,IModelScript<Mod111Key> script,Mod111KeyDAO keyDAO) {
			return provider.obtain(ctx, mod, script,keyDAO);
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
		void initialize(AONContext ctx,Mod111 mod,Map<Mod111Key,Set<String>> docs
				,Map<Mod111Key,Set<String>> pdocs,IrpfBreakdown br);
	}
	
	@FunctionalInterface
	public static interface IValueUniqueIntializer {
		void initialize(AONContext ctx,Mod111 mod);
	}
	
	private static void addDeponentDocument(AONContext ctx, Mod111 mod) {
		Domain domain = DomainDAO.getDomain(ctx, ctx.getDomainId());
		if (!domain.isStandalone()) {
			Company parentCompany = CompanyDAO.getCompany(ctx, domain.getParentId());
			if (parentCompany != null) {
				mod.ensureDetail(Mod111Key.GP_X00).setDescription(parentCompany.getDocument());	
			}
		}
	}

	private static void addPerceptor(Mod111Key key,Mod111 mod
			,Map<Mod111Key,Set<String>> docs
			,Map<Mod111Key,Set<String>> pdocs
			,IrpfBreakdown br) {
		// Se suman todos los perceptores (acumulado).
		if (!docs.containsKey(key)) {
			docs.put(key, new HashSet<String>());
		}
		if (!docs.get(key).contains(br.getRegistryDocument())) {
			docs.get(key).add(br.getRegistryDocument());
			mod.ensureDetail(key).addAccumulatedAmount(1);
		}
		// Se suman los perceptores del periodo que se esta haciendo.
		if (FiscalUtils.isInPeriodRange(mod, br.getTaxDate())) {
			if (!pdocs.containsKey(key)) {
				pdocs.put(key, new HashSet<String>());
			}
			if (!pdocs.get(key).contains(br.getRegistryDocument())) {
				pdocs.get(key).add(br.getRegistryDocument());
				mod.ensureDetail(key).addAmount(1);
			}
		}
	}
	private static void addBase(Mod111Key key,Mod111 mod,IrpfBreakdown br) {
		Mod111KeyDAO keyDAO = Mod111KeyDAO.safeValueOf(mod, key.getValue());
		if (keyDAO.isDiffEnabled()) {
			mod.ensureDetail(key).addAccumulatedAmount(br.getBase());	
		} else {
			mod.ensureDetail(key).addAmount(br.getBase());
		}
	}
	private static void addQuota(Mod111Key key,Mod111 mod,IrpfBreakdown br) {
		Mod111KeyDAO keyDAO = Mod111KeyDAO.safeValueOf(mod, key.getValue());
		if (keyDAO.isDiffEnabled()) {
			mod.ensureDetail(key).addAccumulatedAmount(br.getQuota());	
		} else {
			mod.ensureDetail(key).addAmount(br.getQuota());
		}
	}

	private static enum Mod111KeyDAO {
		// *************************************************************************
		// *************************************************************** ALAVA ***
		// *************************************************************************
		 AR_907(Mod111Key.AR_907,false
			, (mod -> mod.isAraba() && mod.getYear() > 2015)
			,null,null,null,null)
		,AR_908(Mod111Key.AR_908,false
			, (mod -> mod.isAraba() && mod.getYear() > 2015)
			,null,null,null,null)
		,AR_909(Mod111Key.AR_909,false
			, (mod -> mod.isAraba() && mod.getYear() > 2015)
			,null,null,null,null)
		,AR_C50(Mod111Key.AR_C50,false
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.AR_C50,mod,docs,pdocs,br)
			,null,null)
		,AR_C60(Mod111Key.AR_C60,true
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.AR_C60,mod,br)
			,null,null)
		,AR_C70(Mod111Key.AR_C70,true
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.AR_C70,mod,br)
			,null,null)
		,AR_C51(Mod111Key.AR_C51,false, (mod -> mod.isAraba()),null,null,null,null)
		,AR_C61(Mod111Key.AR_C61,true, (mod -> mod.isAraba()),null,null,null,null)
		,AR_C71(Mod111Key.AR_C71,true, (mod -> mod.isAraba()),null,null,null,null)
		,AR_C52(Mod111Key.AR_C52,false, (mod -> mod.isAraba()),null,null,null,null)
		,AR_C62(Mod111Key.AR_C62,true, (mod -> mod.isAraba()),null,null,null,null)
		,AR_C72(Mod111Key.AR_C72,true, (mod -> mod.isAraba()),null,null,null,null)
		,AR_C53(Mod111Key.AR_C53,false, (mod -> mod.isAraba()),null,null,null,null)
		,AR_C63(Mod111Key.AR_C63,true, (mod -> mod.isAraba()),null,null,null,null)
		,AR_C73(Mod111Key.AR_C73,true, (mod -> mod.isAraba()),null,null,null,null)
		,AR_C54(Mod111Key.AR_C54,false 
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isNotObjectiveRegime() && (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.AR_C54,mod,docs,pdocs,br)
			,null,null)
		,AR_C64(Mod111Key.AR_C64,true
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isNotObjectiveRegime() && (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.AR_C64,mod,br)
			,null,null)
		,AR_C74(Mod111Key.AR_C74,true
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isNotObjectiveRegime() && (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.AR_C74,mod,br)
			,null,null)
		,AR_C58(Mod111Key.AR_C58,false
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isObjectiveRegime() && (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.AR_C58,mod,docs,pdocs,br)
			,null,null)
		,AR_C68(Mod111Key.AR_C68,true 
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isObjectiveRegime() && (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.AR_C68,mod,br)
			,null,null)
		,AR_C78(Mod111Key.AR_C78,true
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isObjectiveRegime() && (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.AR_C78,mod,br)
			,null,null)
		,AR_C55(Mod111Key.AR_C55,false
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isFarmer()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.AR_C55,mod,docs,pdocs,br)
			,null,null)
		,AR_C65(Mod111Key.AR_C65,true
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isFarmer()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.AR_C65,mod,br)
			,null,null)
		,AR_C75(Mod111Key.AR_C75,true
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isFarmer()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.AR_C75,mod,br)
			,null,null)
		,AR_C56(Mod111Key.AR_C56,false, (mod -> mod.isAraba()),null,null,null,null)
		,AR_C66(Mod111Key.AR_C66,true, (mod -> mod.isAraba()),null,null,null,null)
		,AR_C76(Mod111Key.AR_C76,true, (mod -> mod.isAraba()),null,null,null,null)
		,AR_C57(Mod111Key.AR_C57,false
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.AR_C56,mod,docs,pdocs,br)
			,null,null)
		,AR_C67(Mod111Key.AR_C67,true
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.AR_C66,mod,br)
			,null,null)
		,AR_C77(Mod111Key.AR_C77,true
			, (mod -> mod.isAraba())
			, (mod,br) -> br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.AR_C76,mod,br)
			,null,null)
		,AR_C80(Mod111Key.AR_C80,false
			, (mod -> mod.isAraba() && mod.getYear() > 2015)
			, null,null,null, "AR_C50+AR_C51+AR_C52+AR_C53+AR_C54+AR_C58+AR_C55+AR_C56+AR_C57")
		,AR_C81(Mod111Key.AR_C81,false
			, (mod -> mod.isAraba() && mod.getYear() > 2015)
			, null,null,null, "AR_C60+AR_C61+AR_C62+AR_C63+AR_C64+AR_C68+AR_C65+AR_C66+AR_C67")
		,AR_C82(Mod111Key.AR_C82,false
			, (mod -> mod.isAraba())
			, null,null,null, "AR_C70+AR_C71+AR_C72+AR_C73+AR_C74+AR_C78+AR_C75+AR_C76+AR_C77")
		,AR_C83(Mod111Key.AR_C83,false, (mod -> mod.isAraba() && mod.getYear() > 2015),null,null,null,null)
		,AR_C84(Mod111Key.AR_C84,false, (mod -> mod.isAraba()),null,null,null,null)
		,AR_C85(Mod111Key.AR_C85,false, (mod -> mod.isAraba()),null,null,null,null)
		,AR_C87(Mod111Key.AR_C87,false
			, (mod -> mod.isAraba() && mod.getYear() <= 2015)
			, null,null,null, "AR_C82+AR_C84+AR_C85")
		,AR_C87B(Mod111Key.AR_C87,false
			, (mod -> mod.isAraba() && mod.getYear() > 2015)
			, null,null,null, "AR_C82-AR_C83+AR_C84+AR_C85")
		,AR_TIP(Mod111Key.AR_TIP,false
			, (mod -> mod.isAraba() && mod.getYear() > 2015)
			, null,null,null,null)
		// *************************************************************************
		// ************************************************************* BIZKAIA ***
		// *************************************************************************
		,BZ_C01 (Mod111Key.BZ_C01,false 
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.BZ_C01,mod,docs,pdocs,br)
			,null,null)
		,BZ_C12 (Mod111Key.BZ_C12,true 
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.BZ_C12,mod,br)
			,null,null)
		,BZ_C23 (Mod111Key.BZ_C23,true
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.BZ_C23,mod,br)
			,null,null)
		,BZ_C02 (Mod111Key.BZ_C02,false , (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C13 (Mod111Key.BZ_C13,true, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C24 (Mod111Key.BZ_C24,true, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C03 (Mod111Key.BZ_C03,false, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C14 (Mod111Key.BZ_C14,true, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C25 (Mod111Key.BZ_C25,true, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C04 (Mod111Key.BZ_C04,false, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C15 (Mod111Key.BZ_C15,true, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C26 (Mod111Key.BZ_C26,true, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C05 (Mod111Key.BZ_C05,false, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C16 (Mod111Key.BZ_C16,true, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C27 (Mod111Key.BZ_C27,true, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C06 (Mod111Key.BZ_C06,false, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C17 (Mod111Key.BZ_C17,true, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C28 (Mod111Key.BZ_C28,true, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C07 (Mod111Key.BZ_C07,false 
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  br.isNotObjectiveRegime() && (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.BZ_C07,mod,docs,pdocs,br)
			,null,null)
		,BZ_C18 (Mod111Key.BZ_C18,true 
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  br.isNotObjectiveRegime() && (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.BZ_C18,mod,br)
			,null,null)
		,BZ_C29 (Mod111Key.BZ_C29,true 
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  br.isNotObjectiveRegime() && (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.BZ_C29,mod,br)
			,null,null)
		,BZ_C50 (Mod111Key.BZ_C50,false
			, (mod ->  mod.isBizkaia() && mod.getPeriod().isQuarterPeriod())
			, (mod,br) ->  br.isObjectiveRegime() && (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.BZ_C50,mod,docs,pdocs,br)
			,null,null)
		,BZ_C51 (Mod111Key.BZ_C51,true
			, (mod ->  mod.isBizkaia() && mod.getPeriod().isQuarterPeriod())
			, (mod,br) ->  br.isObjectiveRegime() && (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.BZ_C51,mod,br)
			,null,null)
		,BZ_C52 (Mod111Key.BZ_C52,true
			, (mod ->  mod.isBizkaia() && mod.getPeriod().isQuarterPeriod())
			, (mod,br) ->  br.isObjectiveRegime() && (br.isProfessional() || br.isTransportOperator())
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.BZ_C52,mod,br)
			,null,null)
		,BZ_C08 (Mod111Key.BZ_C08,false
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  br.isFarmer()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.BZ_C08,mod,docs,pdocs,br)
			,null,null)
		,BZ_C19 (Mod111Key.BZ_C19,true
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  br.isFarmer()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.BZ_C19,mod,br)
			,null,null)
		,BZ_C30 (Mod111Key.BZ_C30,true
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  br.isFarmer()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.BZ_C30,mod,br)
			,null,null)
		,BZ_C09 (Mod111Key.BZ_C09,false 
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.BZ_C09,mod,docs,pdocs,br)
			,null,null)
		,BZ_C20 (Mod111Key.BZ_C20,true
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.BZ_C20,mod,br)
			,null,null)
		,BZ_C31 (Mod111Key.BZ_C31,true
			, (mod -> mod.isBizkaia())
			, (mod,br) ->  br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.BZ_C31,mod,br)
			,null,null)
		,BZ_C10 (Mod111Key.BZ_C10,false, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C21 (Mod111Key.BZ_C21,true, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C32 (Mod111Key.BZ_C32,true, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C11 (Mod111Key.BZ_C11,false, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C22 (Mod111Key.BZ_C22,true, (mod -> mod.isBizkaia()),null,null,null,null)
		,BZ_C33 (Mod111Key.BZ_C33,true, (mod -> mod.isBizkaia()),null,null,null,null)
		
		,BZ_C34M(Mod111Key.BZ_C34T,false
			, (mod ->  mod.isBizkaia() && mod.getPeriod().isMonthPeriod())
			, null,null,null, "BZ_C01+BZ_C02+BZ_C03+BZ_C04+BZ_C05+BZ_C06+BZ_C07+BZ_C08+BZ_C09+BZ_C10+BZ_C11")
		,BZ_C35M(Mod111Key.BZ_C35T,false
			, (mod ->  mod.isBizkaia() && mod.getPeriod().isMonthPeriod())
			, null,null,null, "BZ_C12+BZ_C13+BZ_C14+BZ_C15+BZ_C16+BZ_C17+BZ_C18+BZ_C19+BZ_C20+BZ_C21+BZ_C22")
		,BZ_C36M(Mod111Key.BZ_C36T,false
			, (mod ->  mod.isBizkaia() && mod.getPeriod().isMonthPeriod())
			, null,null,null, "BZ_C23+BZ_C24+BZ_C25+BZ_C26+BZ_C27+BZ_C28+BZ_C29+BZ_C30+BZ_C31+BZ_C32+BZ_C33")
		
		,BZ_C34T(Mod111Key.BZ_C34T,false
			, (mod ->  mod.isBizkaia() && mod.getPeriod().isQuarterPeriod())
			, null,null,null, "BZ_C01+BZ_C02+BZ_C03+BZ_C04+BZ_C05+BZ_C06+BZ_C07+BZ_C50+BZ_C08+BZ_C09+BZ_C10+BZ_C11")
		,BZ_C35T(Mod111Key.BZ_C35T,false
			, (mod ->  mod.isBizkaia() && mod.getPeriod().isQuarterPeriod())
			, null,null,null, "BZ_C12+BZ_C13+BZ_C14+BZ_C15+BZ_C16+BZ_C17+BZ_C18+BZ_C51+BZ_C19+BZ_C20+BZ_C21+BZ_C22")
		,BZ_C36T(Mod111Key.BZ_C36T,false
			, (mod ->  mod.isBizkaia() && mod.getPeriod().isQuarterPeriod())
			, null,null,null, "BZ_C23+BZ_C24+BZ_C25+BZ_C26+BZ_C27+BZ_C28+BZ_C29+BZ_C52+BZ_C30+BZ_C31+BZ_C32+BZ_C33")
		
		,BZ_C39T(Mod111Key.BZ_C39 ,false, (mod -> mod.isBizkaia()), null,null,null, "BZ_C36T")
		,BZ_TIP (Mod111Key.BZ_TIP ,false, (mod -> mod.isBizkaia()), null,null,null,null)
		// *************************************************************************
		// **************************************************** COMMON TERRITORY ***
		// *************************************************************************
		,CT_C01(Mod111Key.CT_C01,false
			, (mod -> mod.isAEAT())
			, (mod,br) ->  br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.CT_C01,mod,docs,pdocs,br)
			,null,null)
		,CT_C02(Mod111Key.CT_C02,true
			, (mod -> mod.isAEAT())
			, (mod,br) ->  br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.CT_C02,mod,br)
			,null,null)
		,CT_C03(Mod111Key.CT_C03,true
			, (mod -> mod.isAEAT())
			, (mod,br) ->  br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.CT_C03,mod,br)
			,null,null)
		,CT_C04(Mod111Key.CT_C04,false
			, (mod -> mod.isAEAT())
			, (mod,br) ->  br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.CT_C04,mod,docs,pdocs,br)
			,null,null)
		,CT_C05(Mod111Key.CT_C05,true
			, (mod -> mod.isAEAT())
			, (mod,br) ->  br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.CT_C05,mod,br)
			,null,null)
		,CT_C06(Mod111Key.CT_C06,true
			, (mod -> mod.isAEAT())
			, (mod,br) ->  br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.CT_C06,mod,br)
			,null,null)
		,CT_C07(Mod111Key.CT_C07,false
			, (mod -> mod.isAEAT())
			, (mod,br) -> br.isProfessional() || br.isFarmer() || br.isTransportOperator()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.CT_C07,mod,docs,pdocs,br)
			,null,null)
		,CT_C08(Mod111Key.CT_C08,true
			, (mod -> mod.isAEAT())
			, (mod,br) -> br.isProfessional() || br.isFarmer() || br.isTransportOperator()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.CT_C08,mod,br)
			,null,null)
		,CT_C09(Mod111Key.CT_C09,true
			, (mod -> mod.isAEAT())
			, (mod,br) -> br.isProfessional() || br.isFarmer() || br.isTransportOperator()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.CT_C09,mod,br)
			,null,null)
		,CT_C10(Mod111Key.CT_C10,false, (mod -> mod.isAEAT()),null,null,null,null)
		,CT_C11(Mod111Key.CT_C11,true, (mod -> mod.isAEAT()),null,null,null,null)
		,CT_C12(Mod111Key.CT_C12,true, (mod -> mod.isAEAT()),null,null,null,null)
		,CT_C13(Mod111Key.CT_C13,false, (mod -> mod.isAEAT()),null,null,null,null)
		,CT_C14(Mod111Key.CT_C14,true, (mod -> mod.isAEAT()),null,null,null,null)
		,CT_C15(Mod111Key.CT_C15,true, (mod -> mod.isAEAT()),null,null,null,null)
		,CT_C16(Mod111Key.CT_C16,false, (mod -> mod.isAEAT()),null,null,null,null)
		,CT_C17(Mod111Key.CT_C17,true, (mod -> mod.isAEAT()),null,null,null,null)
		,CT_C18(Mod111Key.CT_C18,true, (mod -> mod.isAEAT()),null,null,null,null)
		,CT_C19(Mod111Key.CT_C19,false, (mod -> mod.isAEAT()),null,null,null,null)
		,CT_C20(Mod111Key.CT_C20,true, (mod -> mod.isAEAT()),null,null,null,null)
		,CT_C21(Mod111Key.CT_C21,true, (mod -> mod.isAEAT()),null,null,null,null)
		,CT_C22(Mod111Key.CT_C22,false, (mod -> mod.isAEAT()),null,null,null,null)
		,CT_C23(Mod111Key.CT_C23,true, (mod -> mod.isAEAT()),null,null,null,null)
		,CT_C24(Mod111Key.CT_C24,true, (mod -> mod.isAEAT()),null,null,null,null)
		,CT_C25(Mod111Key.CT_C25,false, (mod -> mod.isAEAT()),null,null,null,null)
		,CT_C26(Mod111Key.CT_C26,true, (mod -> mod.isAEAT()),null,null,null,null)
		,CT_C27(Mod111Key.CT_C27,true, (mod -> mod.isAEAT()),null,null,null,null)
		,CT_C28(Mod111Key.CT_C28,false
			, (mod -> mod.isAEAT())
			, null,null,null, "CT_C03+CT_C06+CT_C09+CT_C12+CT_C15+CT_C18+CT_C21+CT_C24" )
		,CT_C29(Mod111Key.CT_C29,false
			, (mod -> mod.isAEAT())
			, null
			, null
			, (ctx,mod) -> mod.putAmount(Mod111Key.CT_C29,mod.isComplementary()
								?getSamePeriodModels(ctx, mod).mapToDouble(fm -> fm.getResult()).sum()
								:0.0)
			,null)
		,CT_C30(Mod111Key.CT_C30,false
			, (mod -> mod.isAEAT())
			, null,null,null, "CT_C28-CT_C29" )
		,CT_TIP (Mod111Key.CT_TIP,false, (mod -> mod.isAEAT()), null,null,null,null)
		// *************************************************************************
		// ************************************************************ GIPUZKOA ***
		// *************************************************************************
		,GP_X00 (Mod111Key.GP_X00,false
			, (mod -> mod.isGipuzkoa())	
			, null
			, null
			, (ctx,mod) -> addDeponentDocument(ctx,mod)
			, null)
		,GP_C01(Mod111Key.GP_C01,false
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.GP_C01,mod,docs,pdocs,br)
			,null,null)
		,GP_C02(Mod111Key.GP_C02,true
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.GP_C02,mod,br)
			,null,null)
		,GP_C03(Mod111Key.GP_C03,true
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isSalaryRetention()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.GP_C03,mod,br)
			,null,null)
		,GP_C04(Mod111Key.GP_C04,false
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isProfessional() || br.isTransportOperator()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.GP_C04,mod,docs,pdocs,br)
			,null,null)
		,GP_C05(Mod111Key.GP_C05,true
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isProfessional() || br.isTransportOperator()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.GP_C05,mod,br)
			,null,null)
		,GP_C06(Mod111Key.GP_C06,true
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isProfessional() || br.isTransportOperator()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.GP_C06,mod,br)
			,null,null)
		,GP_C07(Mod111Key.GP_C07,false
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isFarmer()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.GP_C07,mod,docs,pdocs,br)
			,null,null)
		,GP_C08(Mod111Key.GP_C08,true
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isFarmer()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.GP_C08,mod,br)
			,null,null)
		,GP_C09(Mod111Key.GP_C09,true
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isFarmer()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.GP_C09,mod,br)
			,null,null)
		,GP_C10(Mod111Key.GP_C10,false, (mod -> mod.isGipuzkoa()),null,null,null,null)
		,GP_C11(Mod111Key.GP_C11,true, (mod -> mod.isGipuzkoa()),null,null,null,null)
		,GP_C12(Mod111Key.GP_C12,true, (mod -> mod.isGipuzkoa()),null,null,null,null)
		,GP_C13(Mod111Key.GP_C13,false
			, (mod -> mod.isGipuzkoa())
			,null,null,null, "GP_C02+GP_C05+GP_C08+GP_C11" )
		,GP_C14(Mod111Key.GP_C14,false
			, (mod -> mod.isGipuzkoa())
			,null,null,null, "GP_C03+GP_C06+GP_C09+GP_C12" )
		,GP_C15(Mod111Key.GP_C15,false
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addPerceptor(Mod111Key.GP_C15,mod,docs,pdocs,br)
			,null,null)
		,GP_C16(Mod111Key.GP_C16,true
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addBase(Mod111Key.GP_C16,mod,br)
			,null,null)
		,GP_C17(Mod111Key.GP_C17,true
			, (mod -> mod.isGipuzkoa())
			, (mod,br) -> br.isSalaryInKindRetention()
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.GP_C17,mod,br)
			,null,null)
		,GP_C18(Mod111Key.GP_C18,false, (mod -> mod.isGipuzkoa()),null,null,null,null)
		,GP_C19(Mod111Key.GP_C19,true, (mod -> mod.isGipuzkoa()),null,null,null,null)
		,GP_C20(Mod111Key.GP_C20,true, (mod -> mod.isGipuzkoa()),null,null,null,null)
		,GP_C21(Mod111Key.GP_C21,false, (mod -> mod.isGipuzkoa()),null,null,null,null)
		,GP_C22(Mod111Key.GP_C22,true, (mod -> mod.isGipuzkoa()),null,null,null,null)
		,GP_C23(Mod111Key.GP_C23,true, (mod -> mod.isGipuzkoa()),null,null,null,null)
		,GP_C24(Mod111Key.GP_C24,false, (mod -> mod.isGipuzkoa()),null,null,null,null)
		,GP_C25(Mod111Key.GP_C25,true, (mod -> mod.isGipuzkoa()),null,null,null,null)
		,GP_C26(Mod111Key.GP_C26,true, (mod -> mod.isGipuzkoa()),null,null,null,null)
		,GP_C27(Mod111Key.GP_C27,false
			, (mod -> mod.isGipuzkoa())
			,null,null,null, "GP_C16+GP_C19+GP_C22+GP_C25")
		,GP_C28(Mod111Key.GP_C28,false
			, (mod -> mod.isGipuzkoa())
			,null,null,null, "GP_C17+GP_C20+GP_C23+GP_C26" )
		,GP_C29(Mod111Key.GP_C29,false
			, (mod -> mod.isGipuzkoa())
			,null,null,null, "GP_C14+GP_C28" )
		,GP_TIP (Mod111Key.GP_TIP,false, (mod -> mod.isGipuzkoa()), null,null,null,null)
		
		// *************************************************************************
		// ************************************************************* NAVARRA ***
		// *************************************************************************
		,NF_A1(Mod111Key.NF_A1,true
			, (mod -> mod.isNavarra())
			, (mod,br) -> (br.isProfessional() || br.isTransportOperator() || br.isFarmer() || br.isSalaryRetention() || br.isSalaryInKindRetention()) 
			, (ctx,mod,docs,pdocs,br) -> addQuota(Mod111Key.NF_A1,mod,br)
			,null,null)
		,NF_TIP (Mod111Key.NF_TIP,false , (mod -> mod.isNavarra()), null,null,null,null)
		;
		
		private Mod111Key key;
		private boolean diffEnabled;
		private IModelAccepter acceptModel;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private IValueUniqueIntializer uniqueInitializer;
		private String expression;

		private Mod111KeyDAO(Mod111Key key
				, boolean diffEnabled
				, IModelAccepter acceptModel
				, IValueAccepter acceptValue
				, IValueIntializer initializer
				, IValueUniqueIntializer uniqueInitializer
				, String expression) {
			this.key = key;
			this.diffEnabled = diffEnabled;
			this.acceptModel =  acceptModel;
			this.acceptValue =  acceptValue;
			this.initializer = initializer;
			this.uniqueInitializer = uniqueInitializer;
			this.expression =  expression;
		}
		
		
		public Mod111Key getKey() {
			return key;
		}
		public boolean isDiffEnabled() {
			return diffEnabled;
		}
		public boolean acceptModel(Mod111 mod) {
			return  (acceptModel.accept(mod));
		}
		public boolean acceptValue(Mod111 mod,IrpfBreakdown  br) {
			return  acceptValue != null && acceptModel(mod) &&  acceptValue.accept(mod,br);
		}
		public void initialize(AONContext ctx,Mod111 mod,Map<Mod111Key,Set<String>> docs
				,Map<Mod111Key,Set<String>> pdocs,IrpfBreakdown  br) {
			if (initializer != null) {
				initializer.initialize(ctx, mod, docs, pdocs, br);
			}
		}
		public void uniqueInitialize(AONContext ctx,Mod111 mod) {
			if (uniqueInitializer != null) {
				uniqueInitializer.initialize(ctx, mod);
			}
		}
		public String getExpression() {
			return expression;
		}
		public static Mod111KeyDAO safeValueOf(Mod111 mod, String key) {
			if (AonStringUtils.isBlank(key)) return null; 
			for (Mod111KeyDAO keyDAO : Mod111KeyDAO.values()) {	
				if (keyDAO.acceptModel(mod) && keyDAO.getKey().getValue().equals(key) ) {
					return keyDAO;
				}
			}
			return null;
		}
	}

	public static Stream<Mod111> getMod111s(AONContext ctx,int domain) {
		return getModelRecords(ctx, domain,FiscalModelType.M111)
				.map( record -> map111(new Mod111(),record))
				.peek(fm -> getModelDetails(ctx,fm).forEach( detail -> fm.put( detail)))
				;
	}
	
	public static Mod111 getMod111(AONContext ctx,int id) {
		ctx.checkRead();
		final Mod111 mod111 = getModelRecord(ctx, id)
				.map( record -> map111(new Mod111(),record));
		if (mod111 != null) {
			getModelDetails(ctx,mod111).forEach( detail -> mod111.put( detail));	
		}
		return mod111;
		
	}
	
	public static Mod111 saveMod111(AONContext ctx, Mod111 mod111) {
		calculateMod111(ctx, mod111);
		FiscalModel fm = save(ctx, mod111);
		return getMod111(ctx, fm.getId());
	}
	
	public static Mod111 saveCommentsMod111(AONContext ctx, Mod111 mod111) {
		saveComments(ctx, mod111);
		return mod111;
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
			mod111.setDomain(ctx.getDomainId());
		}
		initializeFiscalModel(ctx, mod111);
		
		// Cálculo por diferencia.
		String diff = AppParamDAO.fetchValue(ctx, AppParam.FS_MOD303_BY_DIFFERENCE_DISABLED);
		mod111.setDiffCalculationDisabled(AonStringUtils.equals(diff, AonStringUtils.ONE));
		
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
		for (FiscalModelDetail detail : mod111.getMap().values()) {
			Mod111KeyDAO key = Mod111KeyDAO.safeValueOf(mod111, detail.getType());
			if (key != null && key.isDiffEnabled()) {
				detail.setResultAmount( AonMathUtils.round(detail.getAccumulatedAmount() - detail.getDeclaredAmount()));	
				detail.setAmount( AonMathUtils.round(detail.getResultAmount() - detail.getAdjustAmount()));
			}
		}
		for (Mod111KeyDAO key : Mod111KeyDAO.values()) {
			if (key.acceptModel(mod111)) {
				key.uniqueInitialize(ctx, mod111);
			};
		}
		return calculateMod111(ctx, mod111);
	}
	public static String getMod111Info(AONContext ctx, Mod111 mod111, IModelScript<Mod111Key> script, FiscalModelKeyInfo infoKey) {
		Mod111KeyInfoDAO k = Mod111KeyInfoDAO.valueOf(infoKey.toString());
		for (Mod111KeyDAO keyDAO : Mod111KeyDAO.values()) {
			if (keyDAO.getKey() == script.getKeys()[0]) {
				return k.getInfo(ctx, mod111, script, keyDAO);
			}
		}
		return null; 
	}

	// -------------------------------------------------------------------- SALARIES
	private static void createFromSalary(final AONContext ctx, final Mod111 mod111) {
		final Map<Mod111Key,Set<String>> docs = new HashMap<Mod111Key,Set<String>>(); 
		final Map<Mod111Key,Set<String>> pdocs = new HashMap<Mod111Key,Set<String>>();
		
		if (mod111.isDiffCalculationDisabled()) {
			// Cálculo por diferencias NO
			IRPFDAO.getSalaryIrpfBreakdown(ctx, mod111)
				.forEach(
						br -> {
							System.out.println( br.getBase() +  " / " + br.getQuota());
							for (Mod111KeyDAO key : Mod111KeyDAO.values()) {
								if (key.acceptValue(mod111,br)) {
									key.initialize(ctx, mod111, docs, pdocs, br);
								};
							}
							
						});			
		}
		else {
			// Cálculo por diferencias SI
			IRPFDAO.getSalaryDiffIrpfBreakdown(ctx, mod111)
				.forEach(
						br -> {
							System.out.println( br.getBase() +  " / " + br.getQuota());
							for (Mod111KeyDAO key : Mod111KeyDAO.values()) {
								if (key.acceptValue(mod111,br)) {
									key.initialize(ctx, mod111, docs, pdocs, br);
								};
							}
							
						});
		}
		
	}

	private static String getSalaryInKindInfo(AONContext ctx, final Mod111 mod111
			, final IModelScript<Mod111Key> script, Mod111KeyDAO keyDAO) {
		return IRPFFormatter.formatSalaries(
				"INFORME RETENCIONES EN ESPECIE EN N\u00D3MINAS"
						+ " DEL " + mod111.getPeriod().getDescription()
						+ " DE " + mod111.getYear()
				,script.getLabel()
				,IRPFDAO.getSalaryIrpfBreakdown(ctx, mod111)
					.filter( br ->  keyDAO.acceptValue(mod111, br) )	
					.collect(Collectors.toCollection(LinkedList::new))
				);
	}
	private static String getSalaryInfo(AONContext ctx, final Mod111 mod111
			, final IModelScript<Mod111Key> script, Mod111KeyDAO keyDAO) {
		return IRPFFormatter.formatSalaries(
				"INFORME RETENCIONES DINERARIAS EN N\u00D3MINAS"
						+ " DEL " + mod111.getPeriod().getDescription()
						+ " DE " + mod111.getYear()
				,script.getLabel()
				,IRPFDAO.getSalaryIrpfBreakdown(ctx, mod111)
					.filter( br ->  keyDAO.acceptValue(mod111, br) )	
					.collect(Collectors.toCollection(LinkedList::new))
				);
	}
	private static String getDiffSalaryInfo(AONContext ctx, final Mod111 mod111
			, final IModelScript<Mod111Key> script, Mod111KeyDAO keyDAO) {
		return IRPFFormatter.formatDiffInvoices(
				"DETALLE DEL C\u00C1LCULO POR DIFERENCIA DEL MODELO "
				,script.getLabel()
				,script.getKeys()
//				,getPreviousModels(ctx,mod111)
				,getMod111EffectivePreviousModels(ctx,mod111)
				 .collect(Collectors.toCollection(LinkedList::new))	
				,IRPFDAO.getSalaryDiffIrpfBreakdown(ctx, mod111)
					.filter( br ->  keyDAO.acceptValue(mod111, br) )	
					.collect(Collectors.toCollection(LinkedList::new))
				);
	}
	private static String getExpression(Mod111 mod111
			, IModelScript<Mod111Key> script, Mod111KeyDAO keyDAO0) {
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
				Mod111KeyDAO keyDAO = Mod111KeyDAO.valueOf(key.toString());
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

		for (String keyValue : mod111.getMap().keySet()) {
			Mod111Key mod111Key = Mod111Key.getKey(keyValue);
			if (mod111Key != null) {
				FiscalModelDetail detail = mod111.getMap().get(keyValue);
				mvelCtx.put(mod111Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}


		for (Mod111Key key : script.getKeys() ) {
			Mod111KeyDAO keyDAO = Mod111KeyDAO.safeValueOf(mod111, key.getValue());
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
	private static void createFromInvoices(final AONContext ctx, final Mod111 mod111) {
		final Map<Mod111Key,Set<String>> docs = new HashMap<Mod111Key,Set<String>>(); 
		final Map<Mod111Key,Set<String>> pdocs = new HashMap<Mod111Key,Set<String>>();
				
		if (mod111.isDiffCalculationDisabled()) {
			// Cálculo por diferencias NO
			IRPFDAO.getInputInvoicesIrpfBreakdown(ctx, mod111)
			.forEach(br -> {
					for (Mod111KeyDAO key : Mod111KeyDAO.values()) {
						if (key.acceptValue(mod111,br)) {
							key.initialize(ctx, mod111, docs, pdocs, br);
						};
					}
			});		
		}
		else {
            // Cálculo por diferencias SI
			IRPFDAO.getInputInvoicesDiffIrpfBreakdown(ctx, mod111)
			.forEach(br -> {
					for (Mod111KeyDAO key : Mod111KeyDAO.values()) {
						if (key.acceptValue(mod111,br)) {
							key.initialize(ctx, mod111, docs, pdocs, br);
						};
					}
			});
		
			getMod111EffectivePreviousModels(ctx, mod111)
				.forEach(mod -> {
					for (String keyString : mod.getMap().keySet()) {
						double amount = mod.getAmount(keyString);
						mod111.ensureDetail(keyString).addDeclaredAmount(amount);
					}
				});
		}
	}
	
	private static String getInvoicesInfo(AONContext ctx, final Mod111 mod111
			, final IModelScript<Mod111Key> script, Mod111KeyDAO keyDAO) {
		
		String title = "FACTURAS CON RETENCIONES QUE AFECTAN A LA CONFECCI\u00D3N DEL MODELO " 
				+ FiscalModelUtils.getModelName(mod111) 
				+ " DEL " + mod111.getPeriod().getDescription()
				+ " DE " + mod111.getYear();
		return IRPFFormatter.formatInvoices(title,script.getLabel()
			,IRPFDAO.getInputInvoicesIrpfBreakdown(ctx, mod111)
					.filter( br ->  keyDAO.acceptValue(mod111, br) )	
					.collect(Collectors.toCollection(LinkedList::new))
		);
	}
	private static String getDiffInvoicesInfo(AONContext ctx, final Mod111 mod111
			, final IModelScript<Mod111Key> script, Mod111KeyDAO keyDAO) {
		String title = "DETALLE DEL C\u00C1LCULO POR DIFERENCIA DEL MODELO "
			+ FiscalModelUtils.getModelName(mod111) 
			+ " DEL " + mod111.getPeriod().getDescription()
			+ " DE " + mod111.getYear();
		return IRPFFormatter.formatDiffInvoices(title
			,script.getLabel()
			,script.getKeys()
			,getMod111EffectivePreviousModels(ctx, mod111)
			 	.collect(Collectors.toCollection(LinkedList::new))	
			,IRPFDAO.getInputInvoicesDiffIrpfBreakdown(ctx, mod111)
				.filter( br ->  keyDAO.acceptValue(mod111, br) )	
				.collect(Collectors.toCollection(LinkedList::new))
		);
	}


	// -------------------------------------------------------------------- UTIL
	public static Mod111 markAsFinished(AONContext ctx,Mod111 mod111) {
		mod111 = FiscalModelDAO.finish(ctx, mod111);
		return saveMod111(ctx, mod111);
	}
	
	public static Mod111 markAsPending(AONContext ctx,Mod111 mod111) {
		mod111.setDeclarationType( (String) null);
		mod111.setStatus(FiscalStatus.PENDING);
		Finance finance = mod111.getFinance();
		mod111.setFinance(null);
		mod111 = saveMod111(ctx, mod111);
		if (finance != null) {
			FinanceDAO.delete(ctx, finance.getId());
		}
		return mod111;
	}
	
	public static Mod111 markAsSent(AONContext ctx,Mod111 mod111) {
		mod111.setStatus(FiscalStatus.SENT);
		mod111 = saveMod111(ctx, mod111);
		return mod111;
	}
	
	public static Mod111 markAsCustomerCheck(AONContext ctx,Mod111 mod111) {
		mod111.setStatus(FiscalStatus.CUSTOMER_CHECK);
		mod111 = saveMod111(ctx, mod111);
		return mod111;
	}

	private static Stream<FiscalModel> getMod111EffectivePreviousModels(AONContext ctx,FiscalModel fiscalModel) {
		if (fiscalModel.isBizkaia()) {
			LinkedList<FiscalModel> previousModels = getPreviousModels(ctx, fiscalModel) .collect(Collectors.toCollection(LinkedList::new));
			if (fiscalModel.isComplementary()) { 
				getSamePeriodModels(ctx, fiscalModel)
					.forEach(fm ->  previousModels.add(fm));
			}
			return previousModels.stream(); 
		} 
		return getEffectivePreviousModels(ctx,fiscalModel);
	}
}

