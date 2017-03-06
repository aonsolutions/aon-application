package com.esferalia.aon.occam.impl.jooq.dao;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mvel2.MVEL;
import org.mvel2.templates.TemplateRuntime;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivity;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfo;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfoKey;
import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfoKeyType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod131ActivityModule;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2016.Epigraph;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod131DAO extends FiscalModelDAO {
	

	@FunctionalInterface
	private static interface IModelInfoProvider {
		String obtain(AONContext ctx, Mod131 mod,IModelScript<Mod131Key> script,Mod131KeyDAO keyDAO);
	}

	private static final String INFO_MSG = "<pre class='aon-fixed-font aon-font-medium aon-margin-bottom'>{0}<pre>";
	private static final String NONE_INFO = "No hay datos";

	private static enum Mod131KeyInfoDAO {
		 NONE   	 ( (ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, NONE_INFO))
		,COMPUTE	 ( (ctx, mod, script,keyDAO) -> getCompute(ctx,mod, script))
		,COMPUTE_KEY ( (ctx, mod, script,keyDAO) -> getComputeKey(ctx,mod, script,keyDAO))
		,INVOICE	 ( (ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getInvoicesInfo(ctx, mod, script,keyDAO)))
		,DIFF_INVOICE( (ctx, mod, script,keyDAO) -> MessageFormat.format(INFO_MSG, getDiffInvoicesInfo(ctx, mod, script,keyDAO)))
		;
		private IModelInfoProvider provider;
		
		private Mod131KeyInfoDAO (IModelInfoProvider provider) {
			this.provider = provider;
		}
		public String getInfo(AONContext ctx, Mod131 mod,IModelScript<Mod131Key> script,Mod131KeyDAO keyDAO) {
			return provider.obtain(ctx, mod, script,keyDAO);
		}
	}
	
	@FunctionalInterface
	private static interface IModelAccepter {
		boolean accept(Mod131 mod);
	}

	@FunctionalInterface
	private static interface IMapFiller {
		void fill(Mod131 mod,String key);
	}
	@FunctionalInterface
	private static interface IActvityFiller {
		void fill(Mod131 mod,String key);
	}
	@FunctionalInterface
	public static interface IValueInitializer {
		void initialize(AONContext ctx,Mod131 mod);
	}

	private static enum Mod131KeyDAO {
		 P2 ( Mod131Key.P2.getValue(),(mod -> mod.isAEAT()),null,null,null,null)
		,AC1_EPI ( Mod131ActDAOKey.AC1_EPI.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureActivity(mod,0).getEpigraph())
			,(mod,key) -> ensureActivity(mod,0).setEpigraph(mod.getDescription(key))
			,null,null)
		,AC1_EPD ( Mod131ActDAOKey.AC1_EPD.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureActivity(mod,0).getDescription())
			,(mod,key) -> ensureActivity(mod,0).setDescription(mod.getDescription(key))
			,null,null)
		,AC1_COM ( Mod131ActDAOKey.AC1_COM.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getCom())
			,(mod,key) -> ensureActivity(mod,0).setCom(mod.getAmount(key))
			,null,null)
		,AC1_TEM ( Mod131ActDAOKey.AC1_TEM.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getTem())
			,(mod,key) -> ensureActivity(mod,0).setTem((int) mod.getAmount(key))
			,null,null)
		,AC1_NUE ( Mod131ActDAOKey.AC1_NUE.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getNue())
			,(mod,key) -> ensureActivity(mod,0).setNue((int) mod.getAmount(key))
			,null,null)
		,AC1_CEU ( Mod131ActDAOKey.AC1_CEU.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).isCeu()?1:0)
			,(mod,key) -> ensureActivity(mod,0).setCeu(mod.getAmount(key)==1)
			,null,null)
		,AC1_LOC ( Mod131ActDAOKey.AC1_LOC.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).isLoc()?1:0)
			,(mod,key) -> ensureActivity(mod,0).setLoc(mod.getAmount(key)==1)
			,null,null)
		,AC1_VEH ( Mod131ActDAOKey.AC1_VEH.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getVeh())
			,(mod,key) -> ensureActivity(mod,0).setVeh((int) mod.getAmount(key))
			,null,null)
		,AC1_CAP ( Mod131ActDAOKey.AC1_CAP.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).isCap()?1:0)
			,(mod,key) -> ensureActivity(mod,0).setCap(mod.getAmount(key)==1)
			,null,null)
		,AC1_TNS ( Mod131ActDAOKey.AC1_TNS.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).isTns()?1:0)
			,(mod,key) -> ensureActivity(mod,0).setTns(mod.getAmount(key)==1)
			,null,null)
		,AC1_TSS ( Mod131ActDAOKey.AC1_TSS.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).isTss()?1:0)
			,(mod,key) -> ensureActivity(mod,0).setTss(mod.getAmount(key)==1)
			,null,null)
		,AC1_BAT ( Mod131ActDAOKey.AC1_BAT.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getBat())
			,(mod,key) -> ensureActivity(mod,0).setBat((int)(int) mod.getAmount(key))
			,null,null)
		,AC1_MUN ( Mod131ActDAOKey.AC1_MUN.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getMun())
			,(mod,key) -> ensureActivity(mod,0).setMun((int) mod.getAmount(key))
			,null,null)
		,AC1_EMP ( Mod131ActDAOKey.AC1_EMP.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getEmp())
			,(mod,key) -> ensureActivity(mod,0).setEmp( (int)(int) mod.getAmount(key))
			,null,null)
		,AC1_LOR ( Mod131ActDAOKey.AC1_LOR.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getLor())
			,(mod,key) -> ensureActivity(mod,0).setLor((int) mod.getAmount(key))
			,null,null)
		,AC1_PRC ( Mod131ActDAOKey.AC1_PRC.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getPrc())
			,(mod,key) -> ensureActivity(mod,0).setPrc(mod.getAmount(key))
			,null,null)
		,AC1_M1D ( Mod131ActDAOKey.AC1_M1D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,0).getDescription())
			,(mod,key) -> ensureModule(mod,0,0).setDescription(mod.getDescription(key))
			,null,null)
		,AC1_M1V ( Mod131ActDAOKey.AC1_M1V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,0).getValue())
			,(mod,key) -> ensureModule(mod,0,0).setValue(mod.getAmount(key))
			,null,null)
		,AC1_M1U ( Mod131ActDAOKey.AC1_M1U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,0).getUnit())
			,(mod,key) -> ensureModule(mod,0,0).setUnit(mod.getDescription(key))
			,null,null)
		,AC1_M1F ( Mod131ActDAOKey.AC1_M1F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,0).getFactor())
			,(mod,key) -> ensureModule(mod,0,0).setFactor(mod.getAmount(key))
			,null,null)
		,AC1_M1R ( Mod131ActDAOKey.AC1_M1R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,0).getResult())
			,(mod,key) -> ensureModule(mod,0,0).setResult(mod.getAmount(key))
			,null,null)
		,AC1_M2D ( Mod131ActDAOKey.AC1_M2D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,1).getDescription())
			,(mod,key) -> ensureModule(mod,0,1).setDescription(mod.getDescription(key))
			,null,null)
		,AC1_M2V ( Mod131ActDAOKey.AC1_M2V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,1).getValue())
			,(mod,key) -> ensureModule(mod,0,1).setValue(mod.getAmount(key))
			,null,null)
		,AC1_M2U ( Mod131ActDAOKey.AC1_M2U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,1).getUnit())
			,(mod,key) -> ensureModule(mod,0,1).setUnit(mod.getDescription(key))
			,null,null)
		,AC1_M2F ( Mod131ActDAOKey.AC1_M2F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,1).getFactor())
			,(mod,key) -> ensureModule(mod,0,1).setFactor(mod.getAmount(key))
			,null,null)
		,AC1_M2R ( Mod131ActDAOKey.AC1_M2R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,1).getResult())
			,(mod,key) -> ensureModule(mod,0,1).setResult(mod.getAmount(key))
			,null,null)
		,AC1_M3D ( Mod131ActDAOKey.AC1_M3D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,2).getDescription())
			,(mod,key) -> ensureModule(mod,0,2).setDescription(mod.getDescription(key))
			,null,null)
		,AC1_M3V ( Mod131ActDAOKey.AC1_M3V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,2).getValue())
			,(mod,key) -> ensureModule(mod,0,2).setValue(mod.getAmount(key))
			,null,null)
		,AC1_M3U ( Mod131ActDAOKey.AC1_M3U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,2).getUnit())
			,(mod,key) -> ensureModule(mod,0,2).setUnit(mod.getDescription(key))
			,null,null)
		,AC1_M3F ( Mod131ActDAOKey.AC1_M3F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,2).getFactor())
			,(mod,key) -> ensureModule(mod,0,2).setFactor(mod.getAmount(key))
			,null,null)
		,AC1_M3R ( Mod131ActDAOKey.AC1_M3R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,2).getResult())
			,(mod,key) -> ensureModule(mod,0,2).setResult(mod.getAmount(key))
			,null,null)
		,AC1_M4D ( Mod131ActDAOKey.AC1_M4D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,3).getDescription())
			,(mod,key) -> ensureModule(mod,0,3).setDescription(mod.getDescription(key))
			,null,null)
		,AC1_M4V ( Mod131ActDAOKey.AC1_M4V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,3).getValue())
			,(mod,key) -> ensureModule(mod,0,3).setValue(mod.getAmount(key))
			,null,null)
		,AC1_M4U ( Mod131ActDAOKey.AC1_M4U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,3).getUnit())
			,(mod,key) -> ensureModule(mod,0,3).setUnit(mod.getDescription(key))
			,null,null)
		,AC1_M4F ( Mod131ActDAOKey.AC1_M4F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,3).getFactor())
			,(mod,key) -> ensureModule(mod,0,3).setFactor(mod.getAmount(key))
			,null,null)
		,AC1_M4R ( Mod131ActDAOKey.AC1_M4R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,3).getResult())
			,(mod,key) -> ensureModule(mod,0,3).setResult(mod.getAmount(key))
			,null,null)
		,AC1_M5D ( Mod131ActDAOKey.AC1_M5D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,4).getDescription())
			,(mod,key) -> ensureModule(mod,0,4).setDescription(mod.getDescription(key))
			,null,null)
		,AC1_M5V ( Mod131ActDAOKey.AC1_M5V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,4).getValue())
			,(mod,key) -> ensureModule(mod,0,4).setValue(mod.getAmount(key))
			,null,null)
		,AC1_M5U ( Mod131ActDAOKey.AC1_M5U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,4).getUnit())
			,(mod,key) -> ensureModule(mod,0,4).setUnit(mod.getDescription(key))
			,null,null)
		,AC1_M5F ( Mod131ActDAOKey.AC1_M5F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,4).getFactor())
			,(mod,key) -> ensureModule(mod,0,4).setFactor(mod.getAmount(key))
			,null,null)
		,AC1_M5R ( Mod131ActDAOKey.AC1_M5R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,4).getResult())
			,(mod,key) -> ensureModule(mod,0,4).setResult(mod.getAmount(key))
			,null,null)
		,AC1_M6D ( Mod131ActDAOKey.AC1_M6D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,5).getDescription())
			,(mod,key) -> ensureModule(mod,0,5).setDescription(mod.getDescription(key))
			,null,null)
		,AC1_M6V ( Mod131ActDAOKey.AC1_M6V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,5).getValue())
			,(mod,key) -> ensureModule(mod,0,5).setValue(mod.getAmount(key))
			,null,null)
		,AC1_M6U ( Mod131ActDAOKey.AC1_M6U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,5).getUnit())
			,(mod,key) -> ensureModule(mod,0,5).setUnit(mod.getDescription(key))
			,null,null)
		,AC1_M6F ( Mod131ActDAOKey.AC1_M6F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,5).getFactor())
			,(mod,key) -> ensureModule(mod,0,5).setFactor(mod.getAmount(key))
			,null,null)
		,AC1_M6R ( Mod131ActDAOKey.AC1_M6R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,5).getResult())
			,(mod,key) -> ensureModule(mod,0,5).setResult(mod.getAmount(key))
			,null,null)
		,AC1_M7D ( Mod131ActDAOKey.AC1_M7D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,6).getDescription())
			,(mod,key) -> ensureModule(mod,0,6).setDescription(mod.getDescription(key))
			,null,null)
		,AC1_M7V ( Mod131ActDAOKey.AC1_M7V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,6).getValue())
			,(mod,key) -> ensureModule(mod,0,6).setValue(mod.getAmount(key))
			,null,null)
		,AC1_M7U ( Mod131ActDAOKey.AC1_M7U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,6).getUnit())
			,(mod,key) -> ensureModule(mod,0,6).setUnit(mod.getDescription(key))
			,null,null)
		,AC1_M7F ( Mod131ActDAOKey.AC1_M7F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,6).getFactor())
			,(mod,key) -> ensureModule(mod,0,6).setFactor(mod.getAmount(key))
			,null,null)
		,AC1_M7R ( Mod131ActDAOKey.AC1_M7R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,6).getResult())
			,(mod,key) -> ensureModule(mod,0,6).setResult(mod.getAmount(key))
			,null,null)
		,AC1_RNP ( Mod131ActDAOKey.AC1_RNP.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getRnp())
			,(mod,key) -> ensureActivity(mod,0).setRnp(mod.getAmount(key))
			,null,null)
		,AC1_IEM ( Mod131ActDAOKey.AC1_IEM.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getIem())
			,(mod,key) -> ensureActivity(mod,0).setIem(mod.getAmount(key))
			,null,null)
		,AC1_IIN ( Mod131ActDAOKey.AC1_IIN.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getIin())
			,(mod,key) -> ensureActivity(mod,0).setIin(mod.getAmount(key))
			,null,null)
		,AC1_RNM ( Mod131ActDAOKey.AC1_RNM.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getRnm())
			,(mod,key) -> ensureActivity(mod,0).setRnm(mod.getAmount(key))
			,null,null)
		,AC1_IC1 ( Mod131ActDAOKey.AC1_IC1.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getIc1())
			,(mod,key) -> ensureActivity(mod,0).setIc1(mod.getAmount(key))
			,null,null)
		,AC1_IC2 ( Mod131ActDAOKey.AC1_IC2.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getIc2())
			,(mod,key) -> ensureActivity(mod,0).setIc2(mod.getAmount(key))
			,null,null)
		,AC1_IC3 ( Mod131ActDAOKey.AC1_IC3.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getIc3())
			,(mod,key) -> ensureActivity(mod,0).setIc3(mod.getAmount(key))
			,null,null)
		,AC1_IC4 ( Mod131ActDAOKey.AC1_IC4.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getIc4())
			,(mod,key) -> ensureActivity(mod,0).setIc4(mod.getAmount(key))
			,null,null)
		,AC1_IC5 ( Mod131ActDAOKey.AC1_IC5.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getIc5())
			,(mod,key) -> ensureActivity(mod,0).setIc5(mod.getAmount(key))
			,null,null)
		,AC1_RPF ( Mod131ActDAOKey.AC1_RPF.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getRpf())
			,(mod,key) -> ensureActivity(mod,0).setRpf(mod.getAmount(key))
			,null,null)
		,AC1_RLO ( Mod131ActDAOKey.AC1_RLO.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getRlo())
			,(mod,key) -> ensureActivity(mod,0).setRlo(mod.getAmount(key))
			,null
			,null)
		,AC1_RDR ( Mod131ActDAOKey.AC1_RDR.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getRdr())
			,(mod,key) -> ensureActivity(mod,0).setRdr(mod.getAmount(key))
			,null,null)
		,AC1_DIA ( Mod131ActDAOKey.AC1_DIA.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getDia())
			,(mod,key) -> ensureActivity(mod,0).setDia((int) mod.getAmount(key))
			,null,null)
		,AC1_NET ( Mod131ActDAOKey.AC1_NET.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getNet())
			,(mod,key) -> ensureActivity(mod,0).setNet(mod.getAmount(key))
			,null,null)
		,AC1_POR ( Mod131ActDAOKey.AC1_POR.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getPor())
			,(mod,key) -> ensureActivity(mod,0).setPor(mod.getAmount(key))
			,null,null)
		,AC1_RES ( Mod131ActDAOKey.AC1_RES.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getRes())
			,(mod,key) -> ensureActivity(mod,0).setRes(mod.getAmount(key))
			,null,null)
		,AC2_EPI ( Mod131ActDAOKey.AC2_EPI.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureActivity(mod,1).getEpigraph())
			,(mod,key) -> ensureActivity(mod,1).setEpigraph(mod.getDescription(key))
			,null,null)
		,AC2_EPD ( Mod131ActDAOKey.AC2_EPD.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureActivity(mod,1).getDescription())
			,(mod,key) -> ensureActivity(mod,1).setDescription(mod.getDescription(key))
			,null,null)
		,AC2_COM ( Mod131ActDAOKey.AC2_COM.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getCom())
			,(mod,key) -> ensureActivity(mod,1).setCom(mod.getAmount(key))
			,null,null)
		,AC2_TEM ( Mod131ActDAOKey.AC2_TEM.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getTem())
			,(mod,key) -> ensureActivity(mod,1).setTem((int)mod.getAmount(key))
			,null,null)
		,AC2_NUE ( Mod131ActDAOKey.AC2_NUE.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getNue())
			,(mod,key) -> ensureActivity(mod,1).setTem((int) mod.getAmount(key))
			,null,null)
		,AC2_CEU ( Mod131ActDAOKey.AC2_CEU.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).isCeu()?1:0)
			,(mod,key) -> ensureActivity(mod,1).setCeu(mod.getAmount(key)==0)
			,null,null)
		,AC2_LOC ( Mod131ActDAOKey.AC2_LOC.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).isLoc()?1:0)
			,(mod,key) -> ensureActivity(mod,1).setLoc(mod.getAmount(key)==1)
			,null,null)
		,AC2_VEH ( Mod131ActDAOKey.AC2_VEH.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getVeh())
			,(mod,key) -> ensureActivity(mod,1).setVeh((int)mod.getAmount(key))
			,null,null)
		,AC2_CAP ( Mod131ActDAOKey.AC2_CAP.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).isCap()?1:0)
			,(mod,key) -> ensureActivity(mod,1).setCap(mod.getAmount(key)==1)
			,null,null)
		,AC2_TNS ( Mod131ActDAOKey.AC2_TNS.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).isTns()?1:0)
			,(mod,key) -> ensureActivity(mod,1).setTns(mod.getAmount(key)==1)
			,null,null)
		,AC2_TSS ( Mod131ActDAOKey.AC2_TSS.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).isTss()?1:0)
			,(mod,key) -> ensureActivity(mod,1).setTss(mod.getAmount(key)==1)
			,null,null)
		,AC2_BAT ( Mod131ActDAOKey.AC2_BAT.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getBat())
			,(mod,key) -> ensureActivity(mod,1).setBat((int)mod.getAmount(key))
			,null,null)
		,AC2_MUN ( Mod131ActDAOKey.AC2_MUN.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getMun())
			,(mod,key) -> ensureActivity(mod,1).setMun((int) mod.getAmount(key))
			,null,null)
		,AC2_EMP ( Mod131ActDAOKey.AC2_EMP.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getEmp())
			,(mod,key) -> ensureActivity(mod,1).setEmp( (int) mod.getAmount(key))
			,null,null)
		,AC2_LOR ( Mod131ActDAOKey.AC2_LOR.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getLor())
			,(mod,key) -> ensureActivity(mod,1).setLor( (int) mod.getAmount(key))
			,null,null)
		,AC2_PRC ( Mod131ActDAOKey.AC2_PRC.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getPrc())
			,(mod,key) -> ensureActivity(mod,1).setPrc(mod.getAmount(key))
			,null,null)
		,AC2_M1D ( Mod131ActDAOKey.AC2_M1D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,0).getDescription())
			,(mod,key) -> ensureModule(mod,1,0).setDescription(mod.getDescription(key))
			,null,null)
		,AC2_M1V ( Mod131ActDAOKey.AC2_M1V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,0).getValue())
			,(mod,key) -> ensureModule(mod,1,0).setValue(mod.getAmount(key))
			,null,null)
		,AC2_M1U ( Mod131ActDAOKey.AC2_M1U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,0).getUnit())
			,(mod,key) -> ensureModule(mod,1,0).setUnit(mod.getDescription(key))
			,null,null)
		,AC2_M1F ( Mod131ActDAOKey.AC2_M1F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,0).getFactor())
			,(mod,key) -> ensureModule(mod,1,0).setFactor(mod.getAmount(key))
			,null,null)
		,AC2_M1R ( Mod131ActDAOKey.AC2_M1R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,0).getResult())
			,(mod,key) -> ensureModule(mod,1,0).setResult(mod.getAmount(key))
			,null,null)
		,AC2_M2D ( Mod131ActDAOKey.AC2_M2D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,1).getDescription())
			,(mod,key) -> ensureModule(mod,1,1).setDescription(mod.getDescription(key))
			,null,null)
		,AC2_M2V ( Mod131ActDAOKey.AC2_M2V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,1).getValue())
			,(mod,key) -> ensureModule(mod,1,1).setValue(mod.getAmount(key))
			,null,null)
		,AC2_M2U ( Mod131ActDAOKey.AC2_M2U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,1).getUnit())
			,(mod,key) -> ensureModule(mod,1,1).setUnit(mod.getDescription(key))
			,null,null)
		,AC2_M2F ( Mod131ActDAOKey.AC2_M2F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,1).getFactor())
			,(mod,key) -> ensureModule(mod,1,1).setFactor(mod.getAmount(key))
			,null,null)
		,AC2_M2R ( Mod131ActDAOKey.AC2_M2R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,1).getResult())
			,(mod,key) -> ensureModule(mod,1,1).setResult(mod.getAmount(key))
			,null,null)
		,AC2_M3D ( Mod131ActDAOKey.AC2_M3D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,2).getDescription())
			,(mod,key) -> ensureModule(mod,1,2).setDescription(mod.getDescription(key))
			,null,null)
		,AC2_M3V ( Mod131ActDAOKey.AC2_M3V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,2).getValue())
			,(mod,key) -> ensureModule(mod,1,2).setValue(mod.getAmount(key))
			,null,null)
		,AC2_M3U ( Mod131ActDAOKey.AC2_M3U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,2).getUnit())
			,(mod,key) -> ensureModule(mod,1,2).setUnit(mod.getDescription(key))
			,null,null)
		,AC2_M3F ( Mod131ActDAOKey.AC2_M3F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,2).getFactor())
			,(mod,key) -> ensureModule(mod,1,2).setFactor(mod.getAmount(key))
			,null,null)
		,AC2_M3R ( Mod131ActDAOKey.AC2_M3R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,2).getResult())
			,(mod,key) -> ensureModule(mod,1,2).setResult(mod.getAmount(key))
			,null,null)
		,AC2_M4D ( Mod131ActDAOKey.AC2_M4D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,3).getDescription())
			,(mod,key) -> ensureModule(mod,1,3).setDescription(mod.getDescription(key))
			,null,null)
		,AC2_M4V ( Mod131ActDAOKey.AC2_M4V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,3).getValue())
			,(mod,key) -> ensureModule(mod,1,3).setValue(mod.getAmount(key))
			,null,null)
		,AC2_M4U ( Mod131ActDAOKey.AC2_M4U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,3).getUnit())
			,(mod,key) -> ensureModule(mod,1,3).setUnit(mod.getDescription(key))
			,null,null)
		,AC2_M4F ( Mod131ActDAOKey.AC2_M4F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,3).getFactor())
			,(mod,key) -> ensureModule(mod,1,3).setFactor(mod.getAmount(key))
			,null,null)
		,AC2_M4R ( Mod131ActDAOKey.AC2_M4R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,3).getResult())
			,(mod,key) -> ensureModule(mod,1,3).setResult(mod.getAmount(key))
			,null,null)
		,AC2_M5D ( Mod131ActDAOKey.AC2_M5D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,4).getDescription())
			,(mod,key) -> ensureModule(mod,1,4).setDescription(mod.getDescription(key))
			,null,null)
		,AC2_M5V ( Mod131ActDAOKey.AC2_M5V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,4).getValue())
			,(mod,key) -> ensureModule(mod,1,4).setValue(mod.getAmount(key))
			,null,null)
		,AC2_M5U ( Mod131ActDAOKey.AC2_M5U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,4).getUnit())
			,(mod,key) -> ensureModule(mod,1,4).setUnit(mod.getDescription(key))
			,null,null)
		,AC2_M5F ( Mod131ActDAOKey.AC2_M5F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,4).getFactor())
			,(mod,key) -> ensureModule(mod,1,4).setFactor(mod.getAmount(key))
			,null,null)
		,AC2_M5R ( Mod131ActDAOKey.AC2_M5R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,4).getResult())
			,(mod,key) -> ensureModule(mod,1,4).setResult(mod.getAmount(key))
			,null,null)
		,AC2_M6D ( Mod131ActDAOKey.AC2_M6D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,5).getDescription())
			,(mod,key) -> ensureModule(mod,1,5).setDescription(mod.getDescription(key))
			,null,null)
		,AC2_M6V ( Mod131ActDAOKey.AC2_M6V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,5).getValue())
			,(mod,key) -> ensureModule(mod,1,5).setValue(mod.getAmount(key))
			,null,null)
		,AC2_M6U ( Mod131ActDAOKey.AC2_M6U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,5).getUnit())
			,(mod,key) -> ensureModule(mod,1,5).setUnit(mod.getDescription(key))
			,null,null)
		,AC2_M6F ( Mod131ActDAOKey.AC2_M6F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,5).getFactor())
			,(mod,key) -> ensureModule(mod,1,5).setFactor(mod.getAmount(key))
			,null,null)
		,AC2_M6R ( Mod131ActDAOKey.AC2_M6R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,5).getResult())
			,(mod,key) -> ensureModule(mod,1,5).setResult(mod.getAmount(key))
			,null,null)
		,AC2_M7D ( Mod131ActDAOKey.AC2_M7D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,6).getDescription())
			,(mod,key) -> ensureModule(mod,1,6).setDescription(mod.getDescription(key))
			,null,null)
		,AC2_M7V ( Mod131ActDAOKey.AC2_M7V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,6).getValue())
			,(mod,key) -> ensureModule(mod,1,6).setValue(mod.getAmount(key))
			,null,null)
		,AC2_M7U ( Mod131ActDAOKey.AC2_M7U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,6).getUnit())
			,(mod,key) -> ensureModule(mod,1,6).setUnit(mod.getDescription(key))
			,null,null)
		,AC2_M7F ( Mod131ActDAOKey.AC2_M7F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,6).getFactor())
			,(mod,key) -> ensureModule(mod,1,6).setFactor(mod.getAmount(key))
			,null,null)
		,AC2_M7R ( Mod131ActDAOKey.AC2_M7R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,6).getResult())
			,(mod,key) -> ensureModule(mod,1,6).setResult(mod.getAmount(key))
			,null,null)
		,AC2_RNP ( Mod131ActDAOKey.AC2_RNP.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getRnp())
			,(mod,key) -> ensureActivity(mod,1).setRnp(mod.getAmount(key))
			,null,null)
		,AC2_IEM ( Mod131ActDAOKey.AC2_IEM.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getIem())
			,(mod,key) -> ensureActivity(mod,1).setIem(mod.getAmount(key))
			,null,null)
		,AC2_IIN ( Mod131ActDAOKey.AC2_IIN.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getIin())
			,(mod,key) -> ensureActivity(mod,1).setIin(mod.getAmount(key))
			,null,null)
		,AC2_RNM ( Mod131ActDAOKey.AC2_RNM.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getRnm())
			,(mod,key) -> ensureActivity(mod,1).setRnm(mod.getAmount(key))
			,null,null)
		,AC2_IC1 ( Mod131ActDAOKey.AC2_IC1.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getIc1())
			,(mod,key) -> ensureActivity(mod,1).setIc1(mod.getAmount(key))
			,null,null)
		,AC2_IC2 ( Mod131ActDAOKey.AC2_IC2.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getIc2())
			,(mod,key) -> ensureActivity(mod,1).setIc2(mod.getAmount(key))
			,null,null)
		,AC2_IC3 ( Mod131ActDAOKey.AC2_IC3.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getIc3())
			,(mod,key) -> ensureActivity(mod,1).setIc3(mod.getAmount(key))
			,null,null)
		,AC2_IC4 ( Mod131ActDAOKey.AC2_IC4.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getIc4())
			,(mod,key) -> ensureActivity(mod,1).setIc4(mod.getAmount(key))
			,null,null)
		,AC2_IC5 ( Mod131ActDAOKey.AC2_IC5.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getIc5())
			,(mod,key) -> ensureActivity(mod,1).setIc5(mod.getAmount(key))
			,null,null)
		,AC2_RPF ( Mod131ActDAOKey.AC2_RPF.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getRpf())
			,(mod,key) -> ensureActivity(mod,1).setRpf(mod.getAmount(key))
			,null,null)
		,AC2_RLO ( Mod131ActDAOKey.AC2_RLO.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getRlo())
			,(mod,key) -> ensureActivity(mod,1).setRlo(mod.getAmount(key))
			,null,null)
		,AC2_RDR ( Mod131ActDAOKey.AC2_RDR.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getRdr())
			,(mod,key) -> ensureActivity(mod,1).setRdr(mod.getAmount(key))
			,null,null)
		,AC2_DIA ( Mod131ActDAOKey.AC2_DIA.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getDia())
			,(mod,key) -> ensureActivity(mod,1).setDia((int)mod.getAmount(key))
			,null,null)
		,AC2_NET ( Mod131ActDAOKey.AC2_NET.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getNet())
			,(mod,key) -> ensureActivity(mod,1).setNet(mod.getAmount(key))
			,null,null)
		,AC2_POR ( Mod131ActDAOKey.AC2_POR.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getPor())
			,(mod,key) -> ensureActivity(mod,1).setPor(mod.getAmount(key))
			,null,null)
		,AC2_RES ( Mod131ActDAOKey.AC2_RES.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getRes())
			,(mod,key) -> ensureActivity(mod,1).setRes(mod.getAmount(key))
			,null,null)
		,AC3_EPI ( Mod131ActDAOKey.AC3_EPI.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureActivity(mod,2).getEpigraph())
			,(mod,key) -> ensureActivity(mod,2).setEpigraph(mod.getDescription(key))
			,null,null)
		,AC3_EPD ( Mod131ActDAOKey.AC3_EPD.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureActivity(mod,2).getDescription())
			,(mod,key) -> ensureActivity(mod,2).setDescription(mod.getDescription(key))
			,null,null)
		,AC3_COM ( Mod131ActDAOKey.AC3_COM.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getCom())
			,(mod,key) -> ensureActivity(mod,2).setCom(mod.getAmount(key))
			,null,null)
		,AC3_TEM ( Mod131ActDAOKey.AC3_TEM.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getTem())
			,(mod,key) -> ensureActivity(mod,2).setTem((int)mod.getAmount(key))
			,null,null)
		,AC3_NUE ( Mod131ActDAOKey.AC3_NUE.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getNue())
			,(mod,key) -> ensureActivity(mod,2).setTem((int) mod.getAmount(key))
			,null,null)
		,AC3_CEU ( Mod131ActDAOKey.AC3_CEU.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).isCeu()?1:0)
			,(mod,key) -> ensureActivity(mod,2).setCeu(mod.getAmount(key)==1)
			,null,null)
		,AC3_LOC ( Mod131ActDAOKey.AC3_LOC.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).isLoc()?1:0)
			,(mod,key) -> ensureActivity(mod,2).setLoc(mod.getAmount(key)==1)
			,null,null)
		,AC3_VEH ( Mod131ActDAOKey.AC3_VEH.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getVeh())
			,(mod,key) -> ensureActivity(mod,2).setVeh((int)mod.getAmount(key))
			,null,null)
		,AC3_CAP ( Mod131ActDAOKey.AC3_CAP.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).isCap()?1:0)
			,(mod,key) -> ensureActivity(mod,2).setCap(mod.getAmount(key)==1)
			,null,null)
		,AC3_TNS ( Mod131ActDAOKey.AC3_TNS.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).isTns()?1:0)
			,(mod,key) -> ensureActivity(mod,2).setTns(mod.getAmount(key)==1)
			,null,null)
		,AC3_TSS ( Mod131ActDAOKey.AC3_TSS.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).isTss()?1:0)
			,(mod,key) -> ensureActivity(mod,2).setTss(mod.getAmount(key)==1)
			,null,null)
		,AC3_BAT ( Mod131ActDAOKey.AC3_BAT.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getBat())
			,(mod,key) -> ensureActivity(mod,2).setBat((int)mod.getAmount(key))
			,null,null)
		,AC3_MUN ( Mod131ActDAOKey.AC3_MUN.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getMun())
			,(mod,key) -> ensureActivity(mod,2).setMun( (int) mod.getAmount(key))
			,null,null)
		,AC3_EMP ( Mod131ActDAOKey.AC3_EMP.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getEmp())
			,(mod,key) -> ensureActivity(mod,2).setEmp( (int) mod.getAmount(key))
			,null,null)
		,AC3_LOR ( Mod131ActDAOKey.AC3_LOR.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getLor())
			,(mod,key) -> ensureActivity(mod,2).setLor( (int) mod.getAmount(key))
			,null,null)
		,AC3_PRC ( Mod131ActDAOKey.AC3_PRC.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getPrc())
			,(mod,key) -> ensureActivity(mod,2).setPrc(mod.getAmount(key))
			,null,null)
		,AC3_M1D ( Mod131ActDAOKey.AC3_M1D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,0).getDescription())
			,(mod,key) -> ensureModule(mod,2,0).setDescription(mod.getDescription(key))
			,null,null)
		,AC3_M1V ( Mod131ActDAOKey.AC3_M1V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,0).getValue())
			,(mod,key) -> ensureModule(mod,2,0).setValue(mod.getAmount(key))
			,null,null)
		,AC3_M1U ( Mod131ActDAOKey.AC3_M1U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,0).getUnit())
			,(mod,key) -> ensureModule(mod,2,0).setUnit(mod.getDescription(key))
			,null,null)
		,AC3_M1F ( Mod131ActDAOKey.AC3_M1F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,0).getFactor())
			,(mod,key) -> ensureModule(mod,2,0).setFactor(mod.getAmount(key))
			,null,null)
		,AC3_M1R ( Mod131ActDAOKey.AC3_M1R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,0).getResult())
			,(mod,key) -> ensureModule(mod,2,0).setResult(mod.getAmount(key))
			,null,null)
		,AC3_M2D ( Mod131ActDAOKey.AC3_M2D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,1).getDescription())
			,(mod,key) -> ensureModule(mod,2,1).setDescription(mod.getDescription(key))
			,null,null)
		,AC3_M2V ( Mod131ActDAOKey.AC3_M2V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,1).getValue())
			,(mod,key) -> ensureModule(mod,2,1).setValue(mod.getAmount(key))
			,null,null)
		,AC3_M2U ( Mod131ActDAOKey.AC3_M2U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,1).getUnit())
			,(mod,key) -> ensureModule(mod,2,1).setUnit(mod.getDescription(key))
			,null,null)
		,AC3_M2F ( Mod131ActDAOKey.AC3_M2F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,1).getFactor())
			,(mod,key) -> ensureModule(mod,2,1).setFactor(mod.getAmount(key))
			,null,null)
		,AC3_M2R ( Mod131ActDAOKey.AC3_M2R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,1).getResult())
			,(mod,key) -> ensureModule(mod,2,1).setResult(mod.getAmount(key))
			,null,null)
		,AC3_M3D ( Mod131ActDAOKey.AC3_M3D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,2).getDescription())
			,(mod,key) -> ensureModule(mod,2,2).setDescription(mod.getDescription(key))
			,null,null)
		,AC3_M3V ( Mod131ActDAOKey.AC3_M3V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,2).getValue())
			,(mod,key) -> ensureModule(mod,2,2).setValue(mod.getAmount(key))
			,null,null)
		,AC3_M3U ( Mod131ActDAOKey.AC3_M3U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,2).getUnit())
			,(mod,key) -> ensureModule(mod,2,2).setUnit(mod.getDescription(key))
			,null,null)
		,AC3_M3F ( Mod131ActDAOKey.AC3_M3F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,2).getFactor())
			,(mod,key) -> ensureModule(mod,2,2).setFactor(mod.getAmount(key))
			,null,null)
		,AC3_M3R ( Mod131ActDAOKey.AC3_M3R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,2).getResult())
			,(mod,key) -> ensureModule(mod,2,2).setResult(mod.getAmount(key))
			,null,null)
		,AC3_M4D ( Mod131ActDAOKey.AC3_M4D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,3).getDescription())
			,(mod,key) -> ensureModule(mod,2,3).setDescription(mod.getDescription(key))
			,null,null)
		,AC3_M4V ( Mod131ActDAOKey.AC3_M4V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,3).getValue())
			,(mod,key) -> ensureModule(mod,2,3).setValue(mod.getAmount(key))
			,null,null)
		,AC3_M4U ( Mod131ActDAOKey.AC3_M4U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,3).getUnit())
			,(mod,key) -> ensureModule(mod,2,3).setUnit(mod.getDescription(key))
			,null,null)
		,AC3_M4F ( Mod131ActDAOKey.AC3_M4F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,3).getFactor())
			,(mod,key) -> ensureModule(mod,2,3).setFactor(mod.getAmount(key))
			,null,null)
		,AC3_M4R ( Mod131ActDAOKey.AC3_M4R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,3).getResult())
			,(mod,key) -> ensureModule(mod,2,3).setResult(mod.getAmount(key))
			,null,null)
		,AC3_M5D ( Mod131ActDAOKey.AC3_M5D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,4).getDescription())
			,(mod,key) -> ensureModule(mod,2,4).setDescription(mod.getDescription(key))
			,null,null)
		,AC3_M5V ( Mod131ActDAOKey.AC3_M5V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,4).getValue())
			,(mod,key) -> ensureModule(mod,2,4).setValue(mod.getAmount(key))
			,null,null)
		,AC3_M5U ( Mod131ActDAOKey.AC3_M5U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,4).getUnit())
			,(mod,key) -> ensureModule(mod,2,4).setUnit(mod.getDescription(key))
			,null,null)
		,AC3_M5F ( Mod131ActDAOKey.AC3_M5F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,4).getFactor())
			,(mod,key) -> ensureModule(mod,2,4).setFactor(mod.getAmount(key))
			,null,null)
		,AC3_M5R ( Mod131ActDAOKey.AC3_M5R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,4).getResult())
			,(mod,key) -> ensureModule(mod,2,4).setResult(mod.getAmount(key))
			,null,null)
		,AC3_M6D ( Mod131ActDAOKey.AC3_M6D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,5).getDescription())
			,(mod,key) -> ensureModule(mod,2,5).setDescription(mod.getDescription(key))
			,null,null)
		,AC3_M6V ( Mod131ActDAOKey.AC3_M6V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,5).getValue())
			,(mod,key) -> ensureModule(mod,2,5).setValue(mod.getAmount(key))
			,null,null)
		,AC3_M6U ( Mod131ActDAOKey.AC3_M6U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,5).getUnit())
			,(mod,key) -> ensureModule(mod,2,5).setUnit(mod.getDescription(key))
			,null,null)
		,AC3_M6F ( Mod131ActDAOKey.AC3_M6F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,5).getFactor())
			,(mod,key) -> ensureModule(mod,2,5).setFactor(mod.getAmount(key))
			,null,null)
		,AC3_M6R ( Mod131ActDAOKey.AC3_M6R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,5).getResult())
			,(mod,key) -> ensureModule(mod,2,5).setResult(mod.getAmount(key))
			,null,null)
		,AC3_M7D ( Mod131ActDAOKey.AC3_M7D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,6).getDescription())
			,(mod,key) -> ensureModule(mod,2,6).setDescription(mod.getDescription(key))
			,null,null)
		,AC3_M7V ( Mod131ActDAOKey.AC3_M7V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,6).getValue())
			,(mod,key) -> ensureModule(mod,2,6).setValue(mod.getAmount(key))
			,null,null)
		,AC3_M7U ( Mod131ActDAOKey.AC3_M7U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,6).getUnit())
			,(mod,key) -> ensureModule(mod,2,6).setUnit(mod.getDescription(key))
			,null,null)
		,AC3_M7F ( Mod131ActDAOKey.AC3_M7F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,6).getFactor())
			,(mod,key) -> ensureModule(mod,2,6).setFactor(mod.getAmount(key))
			,null,null)
		,AC3_M7R ( Mod131ActDAOKey.AC3_M7R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,6).getResult())
			,(mod,key) -> ensureModule(mod,2,6).setResult(mod.getAmount(key))
			,null,null)
		,AC3_RNP ( Mod131ActDAOKey.AC3_RNP.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getRnp())
			,(mod,key) -> ensureActivity(mod,2).setRnp(mod.getAmount(key))
			,null,null)
		,AC3_IEM ( Mod131ActDAOKey.AC3_IEM.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getIem())
			,(mod,key) -> ensureActivity(mod,2).setIem(mod.getAmount(key))
			,null,null)
		,AC3_IIN ( Mod131ActDAOKey.AC3_IIN.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getIin())
			,(mod,key) -> ensureActivity(mod,2).setIin(mod.getAmount(key))
			,null,null)
		,AC3_RNM ( Mod131ActDAOKey.AC3_RNM.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getRnm())
			,(mod,key) -> ensureActivity(mod,2).setRnm(mod.getAmount(key))
			,null,null)
		,AC3_IC1 ( Mod131ActDAOKey.AC3_IC1.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getIc1())
			,(mod,key) -> ensureActivity(mod,2).setIc1(mod.getAmount(key))
			,null,null)
		,AC3_IC2 ( Mod131ActDAOKey.AC3_IC2.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getIc2())
			,(mod,key) -> ensureActivity(mod,2).setIc2(mod.getAmount(key))
			,null,null)
		,AC3_IC3 ( Mod131ActDAOKey.AC3_IC3.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getIc3())
			,(mod,key) -> ensureActivity(mod,2).setIc3(mod.getAmount(key))
			,null,null)
		,AC3_IC4 ( Mod131ActDAOKey.AC3_IC4.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getIc4())
			,(mod,key) -> ensureActivity(mod,2).setIc4(mod.getAmount(key))
			,null,null)
		,AC3_IC5 ( Mod131ActDAOKey.AC3_IC5.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getIc5())
			,(mod,key) -> ensureActivity(mod,2).setIc5(mod.getAmount(key))
			,null,null)
		,AC3_RPF ( Mod131ActDAOKey.AC3_RPF.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getRpf())
			,(mod,key) -> ensureActivity(mod,2).setRpf(mod.getAmount(key))
			,null,null)
		,AC3_RLO ( Mod131ActDAOKey.AC3_RLO.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getRlo())
			,(mod,key) -> ensureActivity(mod,2).setRlo(mod.getAmount(key))
			,null,null)
		,AC3_RDR ( Mod131ActDAOKey.AC3_RDR.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getRdr())
			,(mod,key) -> ensureActivity(mod,2).setRdr(mod.getAmount(key))
			,null,null)
		,AC3_DIA ( Mod131ActDAOKey.AC3_DIA.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getDia())
			,(mod,key) -> ensureActivity(mod,2).setDia((int)mod.getAmount(key))
			,null,null)
		,AC3_NET ( Mod131ActDAOKey.AC3_NET.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getNet())
			,(mod,key) -> ensureActivity(mod,2).setNet(mod.getAmount(key))
			,null,null)
		,AC3_POR ( Mod131ActDAOKey.AC3_POR.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getPor())
			,(mod,key) -> ensureActivity(mod,2).setPor(mod.getAmount(key))
			,null,null)
		,AC3_RES ( Mod131ActDAOKey.AC3_RES.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getRes())
			,(mod,key) -> ensureActivity(mod,2).setRes(mod.getAmount(key))
			,null,null)
		,AC4_EPI ( Mod131ActDAOKey.AC4_EPI.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureActivity(mod,3).getEpigraph())
			,(mod,key) -> ensureActivity(mod,3).setEpigraph(mod.getDescription(key))
			,null,null)
		,AC4_EPD ( Mod131ActDAOKey.AC4_EPD.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureActivity(mod,3).getDescription())
			,(mod,key) -> ensureActivity(mod,3).setDescription(mod.getDescription(key))
			,null,null)
		,AC4_COM ( Mod131ActDAOKey.AC4_COM.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getCom())
			,(mod,key) -> ensureActivity(mod,3).setCom(mod.getAmount(key))
			,null,null)
		,AC4_TEM ( Mod131ActDAOKey.AC4_TEM.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getTem())
			,(mod,key) -> ensureActivity(mod,3).setTem((int)mod.getAmount(key))
			,null,null)
		,AC4_NUE ( Mod131ActDAOKey.AC4_NUE.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getNue())
			,(mod,key) -> ensureActivity(mod,3).setTem((int) mod.getAmount(key))
			,null,null)
		,AC4_CEU ( Mod131ActDAOKey.AC4_CEU.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).isCeu()?1:0)
			,(mod,key) -> ensureActivity(mod,3).setCeu(mod.getAmount(key)==1)
			,null,null)
		,AC4_LOC ( Mod131ActDAOKey.AC4_LOC.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).isLoc()?1:0)
			,(mod,key) -> ensureActivity(mod,3).setLoc(mod.getAmount(key)==1)
			,null,null)
		,AC4_VEH ( Mod131ActDAOKey.AC4_VEH.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getVeh())
			,(mod,key) -> ensureActivity(mod,3).setVeh((int)mod.getAmount(key))
			,null,null)
		,AC4_CAP ( Mod131ActDAOKey.AC4_CAP.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).isCap()?1:0)
			,(mod,key) -> ensureActivity(mod,3).setCap(mod.getAmount(key)==1)
			,null,null)
		,AC4_TNS ( Mod131ActDAOKey.AC4_TNS.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).isTns()?1:0)
			,(mod,key) -> ensureActivity(mod,3).setTns(mod.getAmount(key)==1)
			,null,null)
		,AC4_TSS ( Mod131ActDAOKey.AC4_TSS.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).isTss()?1:0)
			,(mod,key) -> ensureActivity(mod,3).setTss(mod.getAmount(key)==1)
			,null,null)
		,AC4_BAT ( Mod131ActDAOKey.AC4_BAT.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getBat())
			,(mod,key) -> ensureActivity(mod,3).setBat((int)mod.getAmount(key))
			,null,null)
		,AC4_MUN ( Mod131ActDAOKey.AC4_MUN.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getMun())
			,(mod,key) -> ensureActivity(mod,3).setMun( (int) mod.getAmount(key))
			,null,null)
		,AC4_EMP ( Mod131ActDAOKey.AC4_EMP.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getEmp())
			,(mod,key) -> ensureActivity(mod,3).setEmp( (int)mod.getAmount(key))
			,null,null)
		,AC4_LOR ( Mod131ActDAOKey.AC4_LOR.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getLor())
			,(mod,key) -> ensureActivity(mod,3).setLor( (int) mod.getAmount(key))
			,null,null)
		,AC4_PRC ( Mod131ActDAOKey.AC4_PRC.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getPrc())
			,(mod,key) -> ensureActivity(mod,3).setPrc(mod.getAmount(key))
			,null,null)
		,AC4_M1D ( Mod131ActDAOKey.AC4_M1D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,0).getDescription())
			,(mod,key) -> ensureModule(mod,3,0).setDescription(mod.getDescription(key))
			,null,null)
		,AC4_M1V ( Mod131ActDAOKey.AC4_M1V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,0).getValue())
			,(mod,key) -> ensureModule(mod,3,0).setValue(mod.getAmount(key))
			,null,null)
		,AC4_M1U ( Mod131ActDAOKey.AC4_M1U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,0).getUnit())
			,(mod,key) -> ensureModule(mod,3,0).setUnit(mod.getDescription(key))
			,null,null)
		,AC4_M1F ( Mod131ActDAOKey.AC4_M1F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,0).getFactor())
			,(mod,key) -> ensureModule(mod,3,0).setFactor(mod.getAmount(key))
			,null,null)
		,AC4_M1R ( Mod131ActDAOKey.AC4_M1R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,0).getResult())
			,(mod,key) -> ensureModule(mod,3,0).setResult(mod.getAmount(key))
			,null,null)
		,AC4_M2D ( Mod131ActDAOKey.AC4_M2D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,1).getDescription())
			,(mod,key) -> ensureModule(mod,3,1).setDescription(mod.getDescription(key))
			,null,null)
		,AC4_M2V ( Mod131ActDAOKey.AC4_M2V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,1).getValue())
			,(mod,key) -> ensureModule(mod,3,1).setValue(mod.getAmount(key))
			,null,null)
		,AC4_M2U ( Mod131ActDAOKey.AC4_M2U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,1).getUnit())
			,(mod,key) -> ensureModule(mod,3,1).setUnit(mod.getDescription(key))
			,null,null)
		,AC4_M2F ( Mod131ActDAOKey.AC4_M2F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,1).getFactor())
			,(mod,key) -> ensureModule(mod,3,1).setFactor(mod.getAmount(key))
			,null,null)
		,AC4_M2R ( Mod131ActDAOKey.AC4_M2R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,1).getResult())
			,(mod,key) -> ensureModule(mod,3,1).setResult(mod.getAmount(key))
			,null,null)
		,AC4_M3D ( Mod131ActDAOKey.AC4_M3D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,2).getDescription())
			,(mod,key) -> ensureModule(mod,3,2).setDescription(mod.getDescription(key))
			,null,null)
		,AC4_M3V ( Mod131ActDAOKey.AC4_M3V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,2).getValue())
			,(mod,key) -> ensureModule(mod,3,2).setValue(mod.getAmount(key))
			,null,null)
		,AC4_M3U ( Mod131ActDAOKey.AC4_M3U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,2).getUnit())
			,(mod,key) -> ensureModule(mod,3,2).setUnit(mod.getDescription(key))
			,null,null)
		,AC4_M3F ( Mod131ActDAOKey.AC4_M3F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,2).getFactor())
			,(mod,key) -> ensureModule(mod,3,2).setFactor(mod.getAmount(key))
			,null,null)
		,AC4_M3R ( Mod131ActDAOKey.AC4_M3R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,2).getResult())
			,(mod,key) -> ensureModule(mod,3,2).setResult(mod.getAmount(key))
			,null,null)
		,AC4_M4D ( Mod131ActDAOKey.AC4_M4D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,3).getDescription())
			,(mod,key) -> ensureModule(mod,3,3).setDescription(mod.getDescription(key))
			,null,null)
		,AC4_M4V ( Mod131ActDAOKey.AC4_M4V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,3).getValue())
			,(mod,key) -> ensureModule(mod,3,3).setValue(mod.getAmount(key))
			,null,null)
		,AC4_M4U ( Mod131ActDAOKey.AC4_M4U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,3).getUnit())
			,(mod,key) -> ensureModule(mod,3,3).setUnit(mod.getDescription(key))
			,null,null)
		,AC4_M4F ( Mod131ActDAOKey.AC4_M4F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,3).getFactor())
			,(mod,key) -> ensureModule(mod,3,3).setFactor(mod.getAmount(key))
			,null,null)
		,AC4_M4R ( Mod131ActDAOKey.AC4_M4R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,3).getResult())
			,(mod,key) -> ensureModule(mod,3,3).setResult(mod.getAmount(key))
			,null,null)
		,AC4_M5D ( Mod131ActDAOKey.AC4_M5D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,4).getDescription())
			,(mod,key) -> ensureModule(mod,3,4).setDescription(mod.getDescription(key))
			,null,null)
		,AC4_M5V ( Mod131ActDAOKey.AC4_M5V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,4).getValue())
			,(mod,key) -> ensureModule(mod,3,4).setValue(mod.getAmount(key))
			,null,null)
		,AC4_M5U ( Mod131ActDAOKey.AC4_M5U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,4).getUnit())
			,(mod,key) -> ensureModule(mod,3,4).setUnit(mod.getDescription(key))
			,null,null)
		,AC4_M5F ( Mod131ActDAOKey.AC4_M5F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,4).getFactor())
			,(mod,key) -> ensureModule(mod,3,4).setFactor(mod.getAmount(key))
			,null,null)
		,AC4_M5R ( Mod131ActDAOKey.AC4_M5R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,4).getResult())
			,(mod,key) -> ensureModule(mod,3,4).setResult(mod.getAmount(key))
			,null,null)
		,AC4_M6D ( Mod131ActDAOKey.AC4_M6D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,5).getDescription())
			,(mod,key) -> ensureModule(mod,3,5).setDescription(mod.getDescription(key))
			,null,null)
		,AC4_M6V ( Mod131ActDAOKey.AC4_M6V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,5).getValue())
			,(mod,key) -> ensureModule(mod,3,5).setValue(mod.getAmount(key))
			,null,null)
		,AC4_M6U ( Mod131ActDAOKey.AC4_M6U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,5).getUnit())
			,(mod,key) -> ensureModule(mod,3,5).setUnit(mod.getDescription(key))
			,null,null)
		,AC4_M6F ( Mod131ActDAOKey.AC4_M6F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,5).getFactor())
			,(mod,key) -> ensureModule(mod,3,5).setFactor(mod.getAmount(key))
			,null,null)
		,AC4_M6R ( Mod131ActDAOKey.AC4_M6R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,5).getResult())
			,(mod,key) -> ensureModule(mod,3,5).setResult(mod.getAmount(key))
			,null,null)
		,AC4_M7D ( Mod131ActDAOKey.AC4_M7D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,6).getDescription())
			,(mod,key) -> ensureModule(mod,3,6).setDescription(mod.getDescription(key))
			,null,null)
		,AC4_M7V ( Mod131ActDAOKey.AC4_M7V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,6).getValue())
			,(mod,key) -> ensureModule(mod,3,6).setValue(mod.getAmount(key))
			,null,null)
		,AC4_M7U ( Mod131ActDAOKey.AC4_M7U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,6).getUnit())
			,(mod,key) -> ensureModule(mod,3,6).setUnit(mod.getDescription(key))
			,null,null)
		,AC4_M7F ( Mod131ActDAOKey.AC4_M7F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,6).getFactor())
			,(mod,key) -> ensureModule(mod,3,6).setFactor(mod.getAmount(key))
			,null,null)
		,AC4_M7R ( Mod131ActDAOKey.AC4_M7R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,6).getResult())
			,(mod,key) -> ensureModule(mod,3,6).setResult(mod.getAmount(key))
			,null,null)
		,AC4_RNP ( Mod131ActDAOKey.AC4_RNP.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getRnp())
			,(mod,key) -> ensureActivity(mod,3).setRnp(mod.getAmount(key))
			,null,null)
		,AC4_IEM ( Mod131ActDAOKey.AC4_IEM.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getIem())
			,(mod,key) -> ensureActivity(mod,3).setIem(mod.getAmount(key))
			,null,null)
		,AC4_IIN ( Mod131ActDAOKey.AC4_IIN.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getIin())
			,(mod,key) -> ensureActivity(mod,3).setIin(mod.getAmount(key))
			,null,null)
		,AC4_RNM ( Mod131ActDAOKey.AC4_RNM.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getRnm())
			,(mod,key) -> ensureActivity(mod,3).setRnm(mod.getAmount(key))
			,null,null)
		,AC4_IC1 ( Mod131ActDAOKey.AC4_IC1.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getIc1())
			,(mod,key) -> ensureActivity(mod,3).setIc1(mod.getAmount(key))
			,null,null)
		,AC4_IC2 ( Mod131ActDAOKey.AC4_IC2.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getIc2())
			,(mod,key) -> ensureActivity(mod,3).setIc2(mod.getAmount(key))
			,null,null)
		,AC4_IC3 ( Mod131ActDAOKey.AC4_IC3.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getIc3())
			,(mod,key) -> ensureActivity(mod,3).setIc3(mod.getAmount(key))
			,null,null)
		,AC4_IC4 ( Mod131ActDAOKey.AC4_IC4.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getIc4())
			,(mod,key) -> ensureActivity(mod,3).setIc4(mod.getAmount(key))
			,null,null)
		,AC4_IC5 ( Mod131ActDAOKey.AC4_IC5.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getIc5())
			,(mod,key) -> ensureActivity(mod,3).setIc5(mod.getAmount(key))
			,null,null)
		,AC4_RPF ( Mod131ActDAOKey.AC4_RPF.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getRpf())
			,(mod,key) -> ensureActivity(mod,3).setRpf(mod.getAmount(key))
			,null,null)
		,AC4_RLO ( Mod131ActDAOKey.AC4_RLO.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getRlo())
			,(mod,key) -> ensureActivity(mod,3).setRlo(mod.getAmount(key))
			,null,null)
		,AC4_RDR ( Mod131ActDAOKey.AC4_RDR.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getRdr())
			,(mod,key) -> ensureActivity(mod,3).setRdr(mod.getAmount(key))
			,null,null)
		,AC4_DIA ( Mod131ActDAOKey.AC4_DIA.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getDia())
			,(mod,key) -> ensureActivity(mod,3).setDia((int)mod.getAmount(key))
			,null,null)
		,AC4_NET ( Mod131ActDAOKey.AC4_NET.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getNet())
			,(mod,key) -> ensureActivity(mod,3).setNet(mod.getAmount(key))
			,null,null)
		,AC4_POR ( Mod131ActDAOKey.AC4_POR.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getPor())
			,(mod,key) -> ensureActivity(mod,3).setPor(mod.getAmount(key))
			,null,null)
		,AC4_RES ( Mod131ActDAOKey.AC4_RES.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getRes())
			,(mod,key) -> ensureActivity(mod,3).setRes(mod.getAmount(key))
			,null,null)
		,AC5_EPI ( Mod131ActDAOKey.AC5_EPI.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureActivity(mod,4).getEpigraph())
			,(mod,key) -> ensureActivity(mod,4).setEpigraph(mod.getDescription(key))
			,null,null)
		,AC5_EPD ( Mod131ActDAOKey.AC5_EPD.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureActivity(mod,4).getDescription())
			,(mod,key) -> ensureActivity(mod,4).setDescription(mod.getDescription(key))
			,null,null)
		,AC5_COM ( Mod131ActDAOKey.AC5_COM.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getCom())
			,(mod,key) -> ensureActivity(mod,4).setCom(mod.getAmount(key))
			,null,null)
		,AC5_TEM ( Mod131ActDAOKey.AC5_TEM.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getTem())
			,(mod,key) -> ensureActivity(mod,4).setTem((int)mod.getAmount(key))
			,null,null)
		,AC5_NUE ( Mod131ActDAOKey.AC5_NUE.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getNue())
			,(mod,key) -> ensureActivity(mod,4).setTem((int) mod.getAmount(key))
			,null,null)
		,AC5_CEU ( Mod131ActDAOKey.AC5_CEU.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).isCeu()?1:0)
			,(mod,key) -> ensureActivity(mod,4).setCeu(mod.getAmount(key)==1)
			,null,null)
		,AC5_LOC ( Mod131ActDAOKey.AC5_LOC.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).isLoc()?1:0)
			,(mod,key) -> ensureActivity(mod,4).setLoc(mod.getAmount(key)==1)
			,null,null)
		,AC5_VEH ( Mod131ActDAOKey.AC5_VEH.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getVeh())
			,(mod,key) -> ensureActivity(mod,4).setVeh((int)mod.getAmount(key))
			,null,null)
		,AC5_CAP ( Mod131ActDAOKey.AC5_CAP.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).isCap()?1:0)
			,(mod,key) -> ensureActivity(mod,4).setCap(mod.getAmount(key)==1)
			,null,null)
		,AC5_TNS ( Mod131ActDAOKey.AC5_TNS.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).isTns()?1:0)
			,(mod,key) -> ensureActivity(mod,4).setTns(mod.getAmount(key)==1)
			,null,null)
		,AC5_TSS ( Mod131ActDAOKey.AC5_TSS.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).isTss()?1:0)
			,(mod,key) -> ensureActivity(mod,4).setTss(mod.getAmount(key)==1)
			,null,null)
		,AC5_BAT ( Mod131ActDAOKey.AC5_BAT.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getBat())
			,(mod,key) -> ensureActivity(mod,4).setBat((int)mod.getAmount(key))
			,null,null)
		,AC5_MUN ( Mod131ActDAOKey.AC5_MUN.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getMun())
			,(mod,key) -> ensureActivity(mod,4).setMun( (int) mod.getAmount(key))
			,null,null)
		,AC5_EMP ( Mod131ActDAOKey.AC5_EMP.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getEmp())
			,(mod,key) -> ensureActivity(mod,4).setEmp( (int)mod.getAmount(key))
			,null,null)
		,AC5_LOR ( Mod131ActDAOKey.AC5_LOR.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getLor())
			,(mod,key) -> ensureActivity(mod,4).setLor( (int) mod.getAmount(key))
			,null,null)
		,AC5_PRC ( Mod131ActDAOKey.AC5_PRC.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getPrc())
			,(mod,key) -> ensureActivity(mod,4).setPrc(mod.getAmount(key))
			,null,null)
		,AC5_M1D ( Mod131ActDAOKey.AC5_M1D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,0).getDescription())
			,(mod,key) -> ensureModule(mod,4,0).setDescription(mod.getDescription(key))
			,null,null)
		,AC5_M1V ( Mod131ActDAOKey.AC5_M1V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,0).getValue())
			,(mod,key) -> ensureModule(mod,4,0).setValue(mod.getAmount(key))
			,null,null)
		,AC5_M1U ( Mod131ActDAOKey.AC5_M1U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,0).getUnit())
			,(mod,key) -> ensureModule(mod,4,0).setUnit(mod.getDescription(key))
			,null,null)
		,AC5_M1F ( Mod131ActDAOKey.AC5_M1F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,0).getFactor())
			,(mod,key) -> ensureModule(mod,4,0).setFactor(mod.getAmount(key))
			,null,null)
		,AC5_M1R ( Mod131ActDAOKey.AC5_M1R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,0).getResult())
			,(mod,key) -> ensureModule(mod,4,0).setResult(mod.getAmount(key))
			,null,null)
		,AC5_M2D ( Mod131ActDAOKey.AC5_M2D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,1).getDescription())
			,(mod,key) -> ensureModule(mod,4,1).setDescription(mod.getDescription(key))
			,null,null)
		,AC5_M2V ( Mod131ActDAOKey.AC5_M2V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,1).getValue())
			,(mod,key) -> ensureModule(mod,4,1).setValue(mod.getAmount(key))
			,null,null)
		,AC5_M2U ( Mod131ActDAOKey.AC5_M2U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,1).getUnit())
			,(mod,key) -> ensureModule(mod,4,1).setUnit(mod.getDescription(key))
			,null,null)
		,AC5_M2F ( Mod131ActDAOKey.AC5_M2F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,1).getFactor())
			,(mod,key) -> ensureModule(mod,4,1).setFactor(mod.getAmount(key))
			,null,null)
		,AC5_M2R ( Mod131ActDAOKey.AC5_M2R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,1).getResult())
			,(mod,key) -> ensureModule(mod,4,1).setResult(mod.getAmount(key))
			,null,null)
		,AC5_M3D ( Mod131ActDAOKey.AC5_M3D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,2).getDescription())
			,(mod,key) -> ensureModule(mod,4,2).setDescription(mod.getDescription(key))
			,null,null)
		,AC5_M3V ( Mod131ActDAOKey.AC5_M3V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,2).getValue())
			,(mod,key) -> ensureModule(mod,4,2).setValue(mod.getAmount(key))
			,null,null)
		,AC5_M3U ( Mod131ActDAOKey.AC5_M3U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,2).getUnit())
			,(mod,key) -> ensureModule(mod,4,2).setUnit(mod.getDescription(key))
			,null,null)
		,AC5_M3F ( Mod131ActDAOKey.AC5_M3F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,2).getFactor())
			,(mod,key) -> ensureModule(mod,4,2).setFactor(mod.getAmount(key))
			,null,null)
		,AC5_M3R ( Mod131ActDAOKey.AC5_M3R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,2).getResult())
			,(mod,key) -> ensureModule(mod,4,2).setResult(mod.getAmount(key))
			,null,null)
		,AC5_M4D ( Mod131ActDAOKey.AC5_M4D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,3).getDescription())
			,(mod,key) -> ensureModule(mod,4,3).setDescription(mod.getDescription(key))
			,null,null)
		,AC5_M4V ( Mod131ActDAOKey.AC5_M4V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,3).getValue())
			,(mod,key) -> ensureModule(mod,4,3).setValue(mod.getAmount(key))
			,null,null)
		,AC5_M4U ( Mod131ActDAOKey.AC5_M4U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,3).getUnit())
			,(mod,key) -> ensureModule(mod,4,3).setUnit(mod.getDescription(key))
			,null,null)
		,AC5_M4F ( Mod131ActDAOKey.AC5_M4F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,3).getFactor())
			,(mod,key) -> ensureModule(mod,4,3).setFactor(mod.getAmount(key))
			,null,null)
		,AC5_M4R ( Mod131ActDAOKey.AC5_M4R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,3).getResult())
			,(mod,key) -> ensureModule(mod,4,3).setResult(mod.getAmount(key))
			,null,null)
		,AC5_M5D ( Mod131ActDAOKey.AC5_M5D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,4).getDescription())
			,(mod,key) -> ensureModule(mod,4,4).setDescription(mod.getDescription(key))
			,null,null)
		,AC5_M5V ( Mod131ActDAOKey.AC5_M5V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,4).getValue())
			,(mod,key) -> ensureModule(mod,4,4).setValue(mod.getAmount(key))
			,null,null)
		,AC5_M5U ( Mod131ActDAOKey.AC5_M5U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,4).getUnit())
			,(mod,key) -> ensureModule(mod,4,4).setUnit(mod.getDescription(key))
			,null,null)
		,AC5_M5F ( Mod131ActDAOKey.AC5_M5F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,4).getFactor())
			,(mod,key) -> ensureModule(mod,4,4).setFactor(mod.getAmount(key))
			,null,null)
		,AC5_M5R ( Mod131ActDAOKey.AC5_M5R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,4).getResult())
			,(mod,key) -> ensureModule(mod,4,4).setResult(mod.getAmount(key))
			,null,null)
		,AC5_M6D ( Mod131ActDAOKey.AC5_M6D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,5).getDescription())
			,(mod,key) -> ensureModule(mod,4,5).setDescription(mod.getDescription(key))
			,null,null)
		,AC5_M6V ( Mod131ActDAOKey.AC5_M6V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,5).getValue())
			,(mod,key) -> ensureModule(mod,4,5).setValue(mod.getAmount(key))
			,null,null)
		,AC5_M6U ( Mod131ActDAOKey.AC5_M6U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,5).getUnit())
			,(mod,key) -> ensureModule(mod,4,5).setUnit(mod.getDescription(key))
			,null,null)
		,AC5_M6F ( Mod131ActDAOKey.AC5_M6F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,5).getFactor())
			,(mod,key) -> ensureModule(mod,4,5).setFactor(mod.getAmount(key))
			,null,null)
		,AC5_M6R ( Mod131ActDAOKey.AC5_M6R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,5).getResult())
			,(mod,key) -> ensureModule(mod,4,5).setResult(mod.getAmount(key))
			,null,null)
		,AC5_M7D ( Mod131ActDAOKey.AC5_M7D.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,6).getDescription())
			,(mod,key) -> ensureModule(mod,4,6).setDescription(mod.getDescription(key))
			,null,null)
		,AC5_M7V ( Mod131ActDAOKey.AC5_M7V.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,6).getValue())
			,(mod,key) -> ensureModule(mod,4,6).setValue(mod.getAmount(key))
			,null,null)
		,AC5_M7U ( Mod131ActDAOKey.AC5_M7U.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,6).getUnit())
			,(mod,key) -> ensureModule(mod,4,6).setUnit(mod.getDescription(key))
			,null,null)
		,AC5_M7F ( Mod131ActDAOKey.AC5_M7F.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,6).getFactor())
			,(mod,key) -> ensureModule(mod,4,6).setFactor(mod.getAmount(key))
			,null,null)
		,AC5_M7R ( Mod131ActDAOKey.AC5_M7R.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,6).getResult())
			,(mod,key) -> ensureModule(mod,4,6).setResult(mod.getAmount(key))
			,null,null)
		,AC5_RNP ( Mod131ActDAOKey.AC5_RNP.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getRnp())
			,(mod,key) -> ensureActivity(mod,4).setRnp(mod.getAmount(key))
			,null,null)
		,AC5_IEM ( Mod131ActDAOKey.AC5_IEM.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getIem())
			,(mod,key) -> ensureActivity(mod,4).setIem(mod.getAmount(key))
			,null,null)
		,AC5_IIN ( Mod131ActDAOKey.AC5_IIN.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getIin())
			,(mod,key) -> ensureActivity(mod,4).setIin(mod.getAmount(key))
			,null,null)
		,AC5_RNM ( Mod131ActDAOKey.AC5_RNM.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getRnm())
			,(mod,key) -> ensureActivity(mod,4).setRnm(mod.getAmount(key))
			,null,null)
		,AC5_IC1 ( Mod131ActDAOKey.AC5_IC1.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getIc1())
			,(mod,key) -> ensureActivity(mod,4).setIc1(mod.getAmount(key))
			,null,null)
		,AC5_IC2 ( Mod131ActDAOKey.AC5_IC2.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getIc2())
			,(mod,key) -> ensureActivity(mod,4).setIc2(mod.getAmount(key))
			,null,null)
		,AC5_IC3 ( Mod131ActDAOKey.AC5_IC3.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getIc3())
			,(mod,key) -> ensureActivity(mod,4).setIc3(mod.getAmount(key))
			,null,null)
		,AC5_IC4 ( Mod131ActDAOKey.AC5_IC4.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getIc4())
			,(mod,key) -> ensureActivity(mod,4).setIc4(mod.getAmount(key))
			,null,null)
		,AC5_IC5 ( Mod131ActDAOKey.AC5_IC5.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getIc5())
			,(mod,key) -> ensureActivity(mod,4).setIc5(mod.getAmount(key))
			,null,null)
		,AC5_RPF ( Mod131ActDAOKey.AC5_RPF.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getRpf())
			,(mod,key) -> ensureActivity(mod,4).setRpf(mod.getAmount(key))
			,null,null)
		,AC5_RLO ( Mod131ActDAOKey.AC5_RLO.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getRlo())
			,(mod,key) -> ensureActivity(mod,4).setRlo(mod.getAmount(key))
			,null,null)
		,AC5_RDR ( Mod131ActDAOKey.AC5_RDR.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getRdr())
			,(mod,key) -> ensureActivity(mod,4).setRdr(mod.getAmount(key))
			,null,null)
		,AC5_DIA ( Mod131ActDAOKey.AC5_DIA.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getDia())
			,(mod,key) -> ensureActivity(mod,4).setDia((int)mod.getAmount(key))
			,null,null)
		,AC5_NET ( Mod131ActDAOKey.AC5_NET.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getNet())
			,(mod,key) -> ensureActivity(mod,4).setNet(mod.getAmount(key))
			,null,null)
		,AC5_POR ( Mod131ActDAOKey.AC5_POR.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getPor())
			,(mod,key) -> ensureActivity(mod,4).setPor(mod.getAmount(key))
			,null,null)
		,AC5_RES ( Mod131ActDAOKey.AC5_RES.getValue(),(mod -> mod.isAEAT())
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getRes())
			,(mod,key) -> ensureActivity(mod,4).setRes(mod.getAmount(key))
			,null,null)

		,C01 ( Mod131Key.C01.getValue()
			,(mod -> mod.isAEAT())
			,null
			,null
			, "ret = 0.0;"
			+ "foreach (act : activities) {"
			+ "	 	ret = ret + act.getNet();"
			+ "}"
			+ "return round(ret);"
			,"<li>Sumatorio de los rendimientos netos de las actividades"
			+"@code{c01Sum = 0.0;}"
			+"<ul style=\"padding-left: 20px;\">"
			+"@foreach{act : activities}"
				+"@if{ act.getEpigraph() != null}"
					+"@code{c01Sum=com.esferalia.aon.watson.util.AonMathUtils.round(c01Sum + act.getNet())}"					
					+"<li>@{act.getEpigraph()}			Rendimiento neto --> 	@{act.getNet()}</li>"
				+"@end{}"		
			+"@end{}"
			+"</ul><li>Resultado					@{c01Sum}</li>"				
		)
		,C02 ( Mod131Key.C02.getValue()
			,(mod -> mod.isAEAT())
			,null
			,null
			, "ret = 0.0;"
			+ "foreach (act : activities) {"
			+ "	 	ret = ret + act.getRes();"
			+ "}"
			+ "return round(ret);"
			,"<li>Sumatorio de los resultados"
			+"@code{c02Sum = 0.0;}"
			+"@foreach{act : activities}"
				+"@if{ act.getEpigraph() != null}"
					+"@code{c02Sum=com.esferalia.aon.watson.util.AonMathUtils.round(c02Sum + act.getRes())}"					
					+"<li>@{act.getEpigraph()}			Rendimiento neto --> 	@{act.getRes()}</li>"
				+"@end{}"		
			+"@end{}"
			+"</ul><li>Resultado					@{c02Sum}</li>"				
		)
		,C03 ( Mod131Key.C03.getValue()
			,(mod -> mod.isAEAT())
			,null
			,null
			,null
			,null
		)
		,C04 ( Mod131Key.C04.getValue()
			,(mod -> mod.isAEAT())
			,null
			,null
			,"C03 * 2 / 100"
			,"<li>2% de @{C03} igual <b>@{C04}</b></li>"
		)
		,C05 ( Mod131Key.C05.getValue()
			,(mod -> mod.isAEAT())
			,null
			,null
			,(ctx,mod) -> mod.putAmount(Mod131Key.C05, getInitialC05(ctx,mod))
			,null
			,null
		)
		,C06 ( Mod131Key.C06.getValue(),(mod -> mod.isAEAT()),null,null
			,"C05 * 2 / 100"
			,"<li>2% de @{C05} igual <b>@{C06}</b></li>"
		)
		,C07 ( Mod131Key.C07.getValue(),(mod -> mod.isAEAT()),null,null
			,"C02 + C04 + C06"
			,"<li>@{C02} mas @{C04} mas @{C06} igual <b>@{C07}</b></li>"
		)
		,C08 ( Mod131Key.C08.getValue() ,(mod -> mod.isAEAT()),null,null
			,(ctx,mod) -> mod.putAmount(Mod131Key.C08, 
					IRPFDAO.getSalesInvoiceIrpfBreakdown(ctx, mod)
						.mapToDouble(br -> br.getQuota())
						.sum())
			,null,null)
		,C09 ( Mod131Key.C09.getValue() ,(mod -> mod.isAEAT() && mod.getYear() < 2015),null,null,null,null)
		,C091( Mod131Key.C091.getValue(),(mod -> mod.isAEAT() && mod.getYear() > 2014),null,null
			,(ctx,mod) -> mod.putAmount(Mod131Key.C091, getInitialC09(ctx,mod))				
			,null,null)
		,C10 ( Mod131Key.C10.getValue() ,(mod -> mod.isAEAT()),null,null
			,"C07 - C08 - C091"
			,"<li>@{C07} menos @{C08} menos @{C091} igual <b>@{C10}</b></li>"
		)
		,C11 ( Mod131Key.C11.getValue(),(mod -> mod.isAEAT()),null,null
			,(ctx,mod) -> mod.putAmount(Mod131Key.C11, getInitialC11(ctx,mod))
			,null
			,"<li>Trimestres anteriores:<ul style=\"padding-left: 20px;\">" 
			+"<li>cantidades negativas [015]:<ul style=\"padding-left: 20px;\">"
			+"@code{c15Sum = 0.0;}"
			+"@foreach{fm : previousModels}" 
				+"@code{X15 =  fm.getResult()>0?0:(fm.getResult()*(-1));}"
				+"@if{ X15 > 0 }"
					+ "@code{c15Sum = c15Sum + X15;}"
					+"<li>@{fm.getPeriod().getDescription()}			Casilla [015] --> @{X15}</li>"
				+"@end{}"
			+"@end{}"
			+"</ul></li>"
			+"<li>Sumatorio de las casillas [015] --> @{c15Sum}</li>"
			+"<li>Resultado: <b>@{C11}</b></li>"
			)
		,C12 ( Mod131Key.C12.getValue(),(mod -> mod.isAEAT())
			,null
			,null
			, "ret = 0.0;"
			+ "if (P2 > 0) {"
				+ "if (C05 != 0 ) {"
					+ "ret = C05 * 2 / 100;"
				+ "}"
				+ "if (C01 != 0 || C03 != 0) {"
					+ "ret = (C01 * 0.5 / 100) + (C03 * 2 / 100);"
				+ "}"
			+ "}"
			+ "ret = ret > (C10 - C11)?(C10 - C11):ret;"
			+ "return round(ret);"
			, "@code{ret=0.0;}"
			+ "@if{P2 > 0}"
				+ "<li>SI se han destinado cantidades al pago de pr\u00E9stamos por adquisici\u00F3n o rehabilitaci\u00F3n de vivienda habitual.</li>"
				+ "@if{C05 != 0}"
					+ "<li>Casilla [005] mayor diferente de cero.</li>"
					+ "@code{ret = C05 * 2 / 100;}"
					+ "<li>2% de @{C05} igual <b>@{ret}</b></li>"
				+ "@end{}"
				+ "@if{C01 != 0 || C03 != 0}"
					+ "<li>Casilla [001] \u00F3 [003] diferente de cero.</li>"
					+ "@code{ret = (C01 * 0.5 / 100) + (C03 * 2 / 100);}"
					+ "<li>0.5% de [001] m\u00E1s  2% de [003] igual <b>@{ret}</b></li>"
					+ "<li>0.5% de @{C01} m\u00E1s  2% de @{C03} igual <b>@{ret}</b></li>"
				+"@end{}"
			+"@else{}"
				+ "<li>NO Se han destinado cantidades al pago de pr\u00E9stamos por adquisici\u00F3n o rehabilitaci\u00F3n de vivienda habitual.</li>"
			+"@end{}"
			)
		,C13 ( Mod131Key.C13.getValue(),(mod -> mod.isAEAT()),null,null
			,"C10 - C11 - C12"
			,"<li>@{C10} menos @{C11} menos @{C12} igual <b>@{C13}</b></li>"
		)
		,C14 ( Mod131Key.C14.getValue(),(mod -> mod.isAEAT()),null,null
			,(ctx,mod) -> mod.putAmount(Mod131Key.C14,
					mod.isComplementary()
						?getSamePeriodModels(ctx, mod).mapToDouble(fm -> fm.getResult()).sum()
						:0.0)
			,null
			,"<li>Declarciones en el mismo periodo/ejercicio:<ul style=\"padding-left: 20px;\">" 
					+"@foreach{fm : periodModels}" 
						+"<li>Resultado:	Casilla [015] --> @{fm.getResult()}</li>"
					+"@end{}"
					+"</ul></li>"
					+"<li>Resultado: <b>@{C14}</b></li>"

		)
		,C15 ( Mod131Key.C15.getValue(),(mod -> mod.isAEAT()),null,null
			,"C13 - C14"
			,"<li>@{C13} menos @{C14} igual <b>@{C15}</b></li>"
		)
		,CT_TIP ( Mod131Key.CT_TIP.getValue(),(mod -> mod.isAEAT()),null,null,null,null)
		;
		
		private String key;
		private IModelAccepter acceptModel;
		private IMapFiller mapFiller;
		private IActvityFiller activityFiller;
		private IValueInitializer initializer;
		private String expression;
		private String template;
		
		
		private Mod131KeyDAO(String key,IModelAccepter acceptModel,
				IMapFiller mapFiller, IActvityFiller activityFiller
				,IValueInitializer initializer
				,String expression,String template) {
			this.key =  key;
			this.acceptModel =  acceptModel;
			this.mapFiller =  mapFiller;
			this.activityFiller =  activityFiller;
			this.initializer = initializer;
			this.expression =  expression;
			this.template =  template;
		}

		private Mod131KeyDAO(String key,IModelAccepter acceptModel,
				IMapFiller mapFiller, IActvityFiller activityFiller
				,String expression,String template) {
			this(key, acceptModel, mapFiller, activityFiller, null, expression, template);
		}

		public String getKey() {
			return key;
		}

		public boolean acceptModel(Mod131 mod) {
			return  (acceptModel.accept(mod));
		}
		public IActvityFiller getActivityFiller() {
			return activityFiller;
		}
		public IMapFiller getMapFiller() {
			return mapFiller;
		}
		public String getExpression() {
			return expression;
		}
		
		public String getTemplate() {
			return template;
		}
		
		public static Mod131KeyDAO safeValueOf(Mod131 mod, String key) {
			if (AonStringUtils.isBlank(key)) return null; 
			for (Mod131KeyDAO keyDAO : Mod131KeyDAO.values()) {	
				if (keyDAO.acceptModel(mod) && keyDAO.getKey().equals(key) ) {
					return keyDAO;
				}
			}
			return null;
		}

		public void initialize(AONContext ctx, Mod131 mod) {
			if (initializer != null) {
				initializer.initialize(ctx, mod);
			}
		}
	}
	
	public static Stream<Mod131> getMod131s(AONContext ctx,int domain) {
		return getModelRecords(ctx, domain,FiscalModelType.M131)
				.map( record -> map131(new Mod131(),record))
				.peek(fm -> getModelDetails(ctx,fm).forEach( detail -> fm.put( detail)))
				.peek(fm -> fillActivities(fm))
				;
	}
	
	private static double getInitialC05(AONContext ctx, Mod131 mod) {
		return AccountEntryDAO.getAccountingBreakdown(ctx,
				p -> p.getDomainProperty().eq(ctx.getDomainId())
					.and(p.getEntryDateProperty().ge(FiscalUtils.getPeriodStart(mod)))
					.and(p.getEntryDateProperty().le(FiscalUtils.getPeriodEnd(mod)))
					.and(p.getAccountCodeProperty().like("70%")
						.or(p.getAccountCodeProperty().like("71%"))
						.or(p.getAccountCodeProperty().like("72%"))
						.or(p.getAccountCodeProperty().like("73%"))
						.or(p.getAccountCodeProperty().like("75%"))
						.or(p.getAccountCodeProperty().like("76%"))
						.or(p.getAccountCodeProperty().like("77%"))
						.or(p.getAccountCodeProperty().like("78%"))
						.or(p.getAccountCodeProperty().like("79%"))
					))
				.filter( br -> (br.isFarmer()))
				.mapToDouble(br -> br.getCreditBalance())
				.sum();
	}
	private static double getInitialC09(AONContext ctx, final Mod131 mod) {
		double c03 = 0.0;
		double c08 = 0.0;
		double rn = 0.0;
		double c09 = 0.0;
		Mod131 previous = getMod131s(ctx, mod.getDomain())
		 .filter(model -> model.getYear() == (mod.getYear() - 1))
		 .filter(model -> model.getPeriod() == Period.T4)
		 .findFirst()
		 .orElse(null);
		
		if (previous != null) {
			c03 = previous.getAmount(Mod131Key.C01);
			rn = AonMathUtils.round(c03 + c08);
			if (rn <= 9000) {
				c09 = 100;
			} else if (rn > 9000 && rn <= 10000) {
				c09 = 75;
			} else if (rn > 10000 && rn <= 11000) {
				c09 = 50;
			} else if (rn > 11000 && rn <= 12000) {
				c09 = 25;
			}
		}
		
		return c09;
	}
	private static double getInitialC11(AONContext ctx, final Mod131 mod) {
		double c10 = mod.getAmount(Mod131Key.C10);
		double c11 = 0.0; 
		if (c10 > 0) {
			c11 = getPreviousModels(ctx, mod)
				.mapToDouble(fm -> AonMathUtils.round(fm.getResult())>0?0:AonMathUtils.absRounded(fm.getResult()))
				.sum()
			;
			c11 = c11>c10?c10:c11;
		}
		return c11;
	}

	public static Mod131ActivityModule ensureModule(Mod131 mod, int i, int m) {
		Mod131Activity act = ensureActivity(mod, i);
		if (act.getModules() == null) {
			act.setModules( new LinkedList<Mod131ActivityModule>());
		}
		for (int x = 0; x <= m; x++) {
			if (x >= act.getModules().size()) {
				act.getModules().add(new Mod131ActivityModule());	
			}
		}
		return act.getModules().get(m);
	}

	public static Mod131Activity ensureActivity(Mod131 mod, int i) {
		if (mod.getActivities() == null) {
			mod.setActivities( new LinkedList<Mod131Activity>());
		}
		for (int x = 0; x <= i; x++) {
			if (x >= mod.getActivities().size()) {
				Mod131Activity act = new Mod131Activity();
				act.setYear(mod.getYear());
				act.setPeriod(mod.getPeriod());
				mod.getActivities().add(act);	
			}
		}
		return mod.getActivities().get(i);
	}

	private static Mod131 fillActivities(Mod131 fm) {
		for (Mod131KeyDAO key : Mod131KeyDAO.values()) {
			if( key.getActivityFiller() != null) {
				key.getActivityFiller().fill(fm,key.getKey());
			}
		}
		return fm;
	}

	public static Mod131 getMod131(AONContext ctx,int id) {
		ctx.checkRead();
		final Mod131 mod = getModelRecord(ctx, id)
				.map( record -> map131(new Mod131(),record));
		if (mod != null) {
			getModelDetails(ctx,mod).forEach( detail -> mod.put( detail));	
			fillActivities(mod);
		}
		return mod;
	}
	
	public static Mod131 saveMod131(AONContext ctx, Mod131 mod) {
		calculateMod131(ctx, mod);
		for (Mod131KeyDAO key : Mod131KeyDAO.values()) {
			if( key.getMapFiller() != null) {
				key.getMapFiller().fill(mod,key.getKey());
			}
		}
		FiscalModel fm = save(ctx, mod);
		return getMod131(ctx, fm.getId());
	}
	
	public static Mod131 saveCommentsMod131(AONContext ctx, Mod131 mod) {
		saveComments(ctx, mod);
		return mod;
	}

	private static Mod131MVELContext getMVELcontext(AONContext ctx,Mod131 mod131) {
		
		Mod131MVELContext mvelCtx = new Mod131MVELContext();
		mvelCtx.put("activities", mod131.getActivities());
		for (String key : mod131.getMap().keySet()) {
			Mod131Key mod131Key = Mod131Key.getKey(key);
			if (mod131Key != null) {
				FiscalModelDetail detail = mod131.getMap().get(key);
				mvelCtx.put(mod131Key.toString(), detail==null?0.0:detail.getAmount());
			}
		}
		return mvelCtx;
	}
	
	public static Mod131 calculateMod131(AONContext ctx, Mod131 mod) {
		Mod131MVELContext mvelCtx = getMVELcontext(ctx,mod);
		for (Mod131KeyDAO key : Mod131KeyDAO.values()) {
			if (AonStringUtils.isNotEmpty( key.getExpression()) && key.acceptModel(mod)) {
				Object ret =  MVEL.eval( key.getExpression() , mvelCtx , mvelCtx);
				Double amount = (Double) ret;
				mvelCtx.put(key.toString(), amount);
				mod.ensureDetail(key.getKey()).setAmount(AonMathUtils.round( amount) );
			}
		}
		return mod; 
	}
	
	public static Mod131Activity calculateMod131Activity(AONContext ctx, Mod131Activity activity) {
		return Mod131Aeat2016Calculator.calculate(ctx, activity);
	}
	
	
	
	public static Mod131 initializeMod131(AONContext ctx,Mod131 mod) {
		if (mod == null) {
			mod = new Mod131();
		}
		initializeFiscalModel(ctx, mod);
		mod.putAmount(Mod131Key.P2, (AppParamDAO.isPermAddressChanges(ctx)?1:0) );
		initializeDeponents(ctx, mod);
		return mod;
	}
	
	private static void initializeDeponents(AONContext ctx,final Mod131 mod) {
		
		getMod131s(ctx, mod.getDomain())
			.forEach(fm -> {
				if (mod.getDeponents() == null || !mod.getDeponents().containsKey(fm.getDocument())) {
					if (mod.getDeponents() == null) mod.setDeponents(new LinkedHashMap<String,Mod131>());
					mod.getDeponents().put(fm.getDocument(), fm);
				}
			});
			;
		
		
	}

	public static Mod131 createMod131(AONContext ctx,Mod131 mod) {
		for (Mod131KeyDAO key : Mod131KeyDAO.values()) {
			if (key.acceptModel(mod)) {
				FiscalModelDetail detail = mod.ensureDetail(key.getKey());
				detail.setExpression(key.getExpression());
			}
		}
		mod.setActivities(
				FiscalActivityDAO.getActivities(ctx, mod.getDomain())
				.filter( fa -> fa.getYear() == mod.getYear() )
				.map( new Mod131ActivityFiller() )
				.filter( act -> act != null )
				.peek( act -> act
						.setYear(mod.getYear())
						.setPeriod(mod.getPeriod())
						.setDia((int) AonDateUtils.getDaysBetweenDates(
								 FiscalUtils.getPeriodStart(mod)
								,FiscalUtils.getPeriodEnd(mod)))
					)
				.collect(Collectors.toCollection(LinkedList::new))
			);
		if (mod.getActivities() == null) {
			mod.setActivities(new LinkedList<Mod131Activity>() ); 	
		}
		for (int i = 0 ; i < 5 ; i++) {
			if (i >= mod.getActivities().size()) {
				mod.getActivities().add(new Mod131Activity()
						.setYear(mod.getYear())
						.setPeriod(mod.getPeriod())
						.setDia((int) AonDateUtils.getDaysBetweenDates(
								 FiscalUtils.getPeriodStart(mod)
								,FiscalUtils.getPeriodEnd(mod)))
						.setModules( new LinkedList<Mod131ActivityModule>()));
						
			}
		}
		calculateMod131(ctx, mod);
		for (Mod131KeyDAO key : Mod131KeyDAO.values()) {
			key.initialize(ctx, mod);
		}
		
		return calculateMod131(ctx, mod);
	}

	public static String getMod131Info(AONContext ctx, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey) {
		Mod131KeyInfoDAO k = Mod131KeyInfoDAO.valueOf(infoKey.toString());
		for (Mod131KeyDAO keyDAO : Mod131KeyDAO.values()) {
			if (keyDAO.getKey().equals( script.getKeys()[0].getValue())) {
				return k.getInfo(ctx, mod131, script, keyDAO);
			}
		}
		return null; 
	}
	
	// -------------------------------------------------------------------- INFO
	private static String getCompute(AONContext ctx, Mod131 mod131, IModelScript<Mod131Key> script) {
		return getCompute(ctx, mod131, script, getMVELcontext(ctx,mod131));
	}
	private static String getComputeKey(AONContext ctx, Mod131 mod131, IModelScript<Mod131Key> script,Mod131KeyDAO keyDAO) {
		Mod131MVELContext mvelCtx = getMVELcontext(ctx,mod131);
		mvelCtx.put("yearStartDate", IRPFFormatter.FMT.format(AonDateUtils.getYearFirstDay(mod131.getYear())));
		mvelCtx.put("periodStartDate",IRPFFormatter.FMT.format(FiscalUtils.getPeriodStart(mod131)));
		mvelCtx.put("periodEndDate",IRPFFormatter.FMT.format(FiscalUtils.getPeriodEnd(mod131)));
		mvelCtx.put("previousModels", getPreviousModels(ctx, mod131).collect(Collectors.toCollection(LinkedList::new)));
		mvelCtx.put("periodModels", getSamePeriodModels(ctx, mod131).collect(Collectors.toCollection(LinkedList::new)));
		return getCompute(ctx, mod131, script,mvelCtx);
	}
	
	private static String getCompute(AONContext ctx, Mod131 mod131, IModelScript<Mod131Key> script,Mod131MVELContext mvelCtx) {
		StringBuilder buf = new StringBuilder();
		buf.append("<pre style=\"font-family: Fixed, monospace;font-size: 0.9em; margin-bottom: 1em; padding: 1em;\">");
		for (Mod131Key key : script.getKeys() ) {
			Mod131KeyDAO keyDAO = Mod131KeyDAO.safeValueOf(mod131, key.getValue());
			if (keyDAO != null) {
				String box = " [" + AonStringUtils.leftPad(keyDAO.getKey(), 3, '0')+"] ";
				buf.append(AonStringUtils.CR_LF);
				buf.append("<b>DETALLE DEL C\u00C1LCULO DE LA CASILLA: " + box + " - " + script.getLabel() + "</b>");
				buf.append(AonStringUtils.CR_LF);
				buf.append(AonStringUtils.CR_LF);
				buf.append("<ul style=\"padding-left: 20px;\">");
				if (AonStringUtils.isNotBlank( keyDAO.getExpression()) && keyDAO != Mod131KeyDAO.C12 ) {
					String exp = keyDAO.getExpression();
					if (keyDAO == Mod131KeyDAO.C10) exp =  "C07 - C08 - C09";
					buf.append("<li><b>F\u00F3rmula:</b> " + exp + "</li>" );
				}
				String template = keyDAO.getTemplate();
				if (AonStringUtils.isNotBlank( template )) {
					Object result = TemplateRuntime.eval(template, mvelCtx);
					buf.append(result != null ? result.toString() : null);
				}
				buf.append("</ul>");
			}
		}
		buf.append("</pre>");
		return buf.toString();
	}
	
	private static String getInvoicesInfo(AONContext ctx, final Mod131 mod131
			, final IModelScript<Mod131Key> script, Mod131KeyDAO keyDAO) {
		
		String title = "FACTURAS CON RETENCIONES QUE AFECTAN A LA CONFECCI\u00D3N DEL MODELO " 
				+ mod131.getModelName() 
				+ " DEL " + mod131.getPeriod().getDescription()
				+ " DE " + mod131.getYear();
		return IRPFFormatter.formatInvoices(title,script.getLabel()
			,IRPFDAO.getSalesInvoiceDiffIrpfBreakdown(ctx, mod131)
					.collect(Collectors.toCollection(LinkedList::new))
		);
	}
	
	private static String getDiffInvoicesInfo(AONContext ctx, final Mod131 mod131
			, final IModelScript<Mod131Key> script, Mod131KeyDAO keyDAO) {
		String title = "DETALLE DEL C\u00C1LCULO POR DIFERENCIA DEL MODELO "
			+ mod131.getModelName() 
			+ " DEL " + mod131.getPeriod().getDescription()
			+ " DE " + mod131.getYear();
		return IRPFFormatter.formatDiffInvoices(title
			,script.getLabel()
			,script.getKeys()
			, getPreviousModels(ctx,mod131)
			 	.collect(Collectors.toCollection(LinkedList::new))	
			,IRPFDAO.getSalesInvoiceDiffIrpfBreakdown(ctx, mod131)
				.collect(Collectors.toCollection(LinkedList::new))
		);
	}
	// -------------------------------------------------------------------- UTIL
	public static Mod131 finish(AONContext ctx,Mod131 mod) {
		mod = FiscalModelDAO.finish(ctx, mod);
		return saveMod131(ctx, mod);
	}
	
	public static Mod131 reopen(AONContext ctx,Mod131 mod) {
		mod.setDeclarationType((String) null);
		mod.setStatus(FiscalStatus.PENDING);
		Finance finance = mod.getFinance();
		mod.setFinance(null);
		mod= saveMod131(ctx, mod);
		if (finance != null) {
			FinanceDAO.delete(ctx, finance.getId());
		}
		return mod;
	}
	
	
	private static class Mod131ActivityFiller implements Function<FiscalActivity, Mod131Activity> {
		@Override
		public Mod131Activity apply(FiscalActivity fa) {
			if (!fa.hasIRPFModules()) return null;
			String epi1 = AonStringUtils.trim(AonStringUtils.substringBefore(
					 fa.getEpigraph(),AonStringUtils.HYPHEN));
			Epigraph epigraph = Epigraph.getEpigraph(epi1);
			Mod131Activity act = new Mod131Activity()
					.setEpi( epigraph )
					.setEpigraph( epigraph == null?fa.getEpigraph():epigraph.getEpigraph())
					.setDescription(epigraph == null?fa.getDescription():epigraph.getDescription())
					.setMaxImport(epigraph == null?Double.MAX_VALUE:epigraph.getLimExceso())
					.setDis( fa.getDoubleValue(FiscalActivityInfoKey.A13) == 1)
					.setCom( fa.getDoubleValue(FiscalActivityInfoKey.A02))
					.setTem( (int) fa.getDoubleValue(FiscalActivityInfoKey.A03))
					.setNue( (int) fa.getDoubleValue(FiscalActivityInfoKey.A04))
					.setCeu( fa.getDoubleValue(FiscalActivityInfoKey.A05) == 1)
					.setLoc( fa.getDoubleValue(FiscalActivityInfoKey.A06) == 1)
					.setVeh( (int) fa.getDoubleValue(FiscalActivityInfoKey.A07))
					.setCap( fa.getDoubleValue(FiscalActivityInfoKey.A08) == 1)
					.setBat( (int) fa.getDoubleValue(FiscalActivityInfoKey.B06))
					.setTns( fa.getDoubleValue(FiscalActivityInfoKey.C10) == 1)
					.setTss( fa.getDoubleValue(FiscalActivityInfoKey.C11) == 1)
					.setMun( (int) fa.getDoubleValue(FiscalActivityInfoKey.A09))
					.setEmp( (int) (int) fa.getDoubleValue(FiscalActivityInfoKey.A10))
					.setLor( (int) fa.getDoubleValue(FiscalActivityInfoKey.A11))
					.setRnp( fa.getDoubleValue(FiscalActivityInfoKey.I01))
					.setIem( fa.getDoubleValue(FiscalActivityInfoKey.I02))
					.setIin( fa.getDoubleValue(FiscalActivityInfoKey.I03))
					.setRnm( fa.getDoubleValue(FiscalActivityInfoKey.I04))
					.setIc1( fa.getDoubleValue(FiscalActivityInfoKey.I06))
					.setIc2( fa.getDoubleValue(FiscalActivityInfoKey.I07))
					.setIc3( fa.getDoubleValue(FiscalActivityInfoKey.I08))
					.setIc4( fa.getDoubleValue(FiscalActivityInfoKey.I09))
					.setIc5( fa.getDoubleValue(FiscalActivityInfoKey.I10))
					.setRpf( fa.getDoubleValue(FiscalActivityInfoKey.I11))
					.setRlo( fa.getDoubleValue(FiscalActivityInfoKey.I12))
					.setRdr( fa.getDoubleValue(FiscalActivityInfoKey.I13))
					.setPor( fa.getDoubleValue(FiscalActivityInfoKey.I14))
					.setNet( fa.getDoubleValue(FiscalActivityInfoKey.I13))
					.setPrc( fa.getDoubleValue(FiscalActivityInfoKey.I14))
					.setRes( fa.getDoubleValue(FiscalActivityInfoKey.I15))
			;
			act.setModules(new LinkedList<Mod131ActivityModule>());
			for (FiscalActivityInfo info : fa.getMap().get(FiscalActivityInfoKeyType.IRPF_MODULE.ordinal()).values()) {
				Mod131ActivityModule module = new Mod131ActivityModule()
						.setDescription(info.getInfoKey().getDescription())
						.setValue( AonNumberUtils.todouble( info.getValue()) )
						.setUnit( info.getUnit() ) 
						.setFactor( info.getFactor() )
						.setResult( info.getBase() )
						.setSalariedStaff(false)
						.setNoSalariedStaff(false);
				// PERSONAL Asalariado.
				if (info.getInfoKey() == FiscalActivityInfoKey.M01) {
					module.setSalariedStaff(true);
					module.setMay19Hours(fa.getDoubleValue(FiscalActivityInfoKey.M011));
					module.setMen19Hours(fa.getDoubleValue(FiscalActivityInfoKey.M012)); 	
					module.setDisHours(fa.getDoubleValue(FiscalActivityInfoKey.M013)); 		
					module.setYearHours(fa.getDoubleValue(FiscalActivityInfoKey.M014));
				} else if (info.getInfoKey() == FiscalActivityInfoKey.M02) {
					module.setNoSalariedStaff(true);
					module.setOwnerHours(fa.getDoubleValue(FiscalActivityInfoKey.M021)); 		
					module.setSpouseHours(fa.getDoubleValue(FiscalActivityInfoKey.M022));
					module.setSpouseDis(AonMathUtils.isNotZero(fa.getDoubleValue(FiscalActivityInfoKey.M023)));
					module.setChildMen18Hours(fa.getDoubleValue(FiscalActivityInfoKey.M024)); 
					module.setChildDisHours(fa.getDoubleValue(FiscalActivityInfoKey.M025)); 	
				} else if (info.getInfoKey() == FiscalActivityInfoKey.M15) {
					module.setSalariedStaff(true);
				} else if (info.getInfoKey() == FiscalActivityInfoKey.M16) {
					module.setSalariedStaff(true);
				}
				act.getModules().add( module );
			}
			return act;
		}
	}


	// --------------------------------------------------- KEY INTITIALIZATION
}
