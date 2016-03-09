package com.esferalia.aon.occam.impl.jooq.dao;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mvel2.MVEL;
import org.mvel2.templates.TemplateRuntime;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelDetail;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod131ActivityModule;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
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

	private static enum Mod131KeyDAO {
		// *************************************************************************
		// **************************************************** COMMON TERRITORY ***
		// *************************************************************************
		 P1	(Mod131Key.P1				, (mod -> mod.isAEAT())	, null	, null)
		,P2	(Mod131Key.P2				, (mod -> mod.isAEAT())	, null	, null)
		,P3	(Mod131Key.P3				, (mod -> mod.isAEAT())	, null	, null)
		,AC1_EPI	(Mod131Key.AC1_EPI	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_EPD	(Mod131Key.AC1_EPD	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_COM	(Mod131Key.AC1_COM	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_TEM	(Mod131Key.AC1_TEM	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_NUE	(Mod131Key.AC1_NUE	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_CEU	(Mod131Key.AC1_CEU	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_LOC	(Mod131Key.AC1_LOC	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_VEH	(Mod131Key.AC1_VEH	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_CAP	(Mod131Key.AC1_CAP	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_MUN	(Mod131Key.AC1_MUN	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_EMP	(Mod131Key.AC1_EMP	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_LOR	(Mod131Key.AC1_LOR	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_PRC	(Mod131Key.AC1_PRC	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M1D	(Mod131Key.AC1_M1D	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M1U	(Mod131Key.AC1_M1U	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M1F	(Mod131Key.AC1_M1F	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M1R	(Mod131Key.AC1_M1R	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M2D	(Mod131Key.AC1_M2D	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M2U	(Mod131Key.AC1_M2U	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M2F	(Mod131Key.AC1_M2F	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M2R	(Mod131Key.AC1_M2R	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M3D	(Mod131Key.AC1_M3D	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M3U	(Mod131Key.AC1_M3U	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M3F	(Mod131Key.AC1_M3F	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M3R	(Mod131Key.AC1_M3R	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M4D	(Mod131Key.AC1_M4D	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M4U	(Mod131Key.AC1_M4U	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M4F	(Mod131Key.AC1_M4F	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M4R	(Mod131Key.AC1_M4R	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M5D	(Mod131Key.AC1_M5D	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M5U	(Mod131Key.AC1_M5U	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M5F	(Mod131Key.AC1_M5F	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M5R	(Mod131Key.AC1_M5R	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M6D	(Mod131Key.AC1_M6D	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M6U	(Mod131Key.AC1_M6U	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M6F	(Mod131Key.AC1_M6F	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M6R	(Mod131Key.AC1_M6R	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M7D	(Mod131Key.AC1_M7D	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M7U	(Mod131Key.AC1_M7U	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M7F	(Mod131Key.AC1_M7F	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_M7R	(Mod131Key.AC1_M7R	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_RNP	(Mod131Key.AC1_RNP	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_IEM	(Mod131Key.AC1_IEM	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_IIN	(Mod131Key.AC1_IIN	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_RNM	(Mod131Key.AC1_RNM	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_IC1	(Mod131Key.AC1_IC1	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_IC2	(Mod131Key.AC1_IC2	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_IC3	(Mod131Key.AC1_IC3	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_IC4	(Mod131Key.AC1_IC4	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_IC5	(Mod131Key.AC1_IC5	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_RPF	(Mod131Key.AC1_RPF	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_RLO	(Mod131Key.AC1_RLO	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_RDR	(Mod131Key.AC1_RDR	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_DIA	(Mod131Key.AC1_DIA	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_NET	(Mod131Key.AC1_NET	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_POR	(Mod131Key.AC1_POR	, (mod -> mod.isAEAT())	, null	, null)
		,AC1_RES	(Mod131Key.AC1_RES	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_EPI	(Mod131Key.AC2_EPI	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_EPD	(Mod131Key.AC2_EPD	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_COM	(Mod131Key.AC2_COM	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_TEM	(Mod131Key.AC2_TEM	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_NUE	(Mod131Key.AC2_NUE	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_CEU	(Mod131Key.AC2_CEU	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_LOC	(Mod131Key.AC2_LOC	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_VEH	(Mod131Key.AC2_VEH	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_CAP	(Mod131Key.AC2_CAP	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_MUN	(Mod131Key.AC2_MUN	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_EMP	(Mod131Key.AC2_EMP	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_LOR	(Mod131Key.AC2_LOR	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_PRC	(Mod131Key.AC2_PRC	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M1D	(Mod131Key.AC2_M1D	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M1U	(Mod131Key.AC2_M1U	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M1F	(Mod131Key.AC2_M1F	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M1R	(Mod131Key.AC2_M1R	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M2D	(Mod131Key.AC2_M2D	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M2U	(Mod131Key.AC2_M2U	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M2F	(Mod131Key.AC2_M2F	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M2R	(Mod131Key.AC2_M2R	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M3D	(Mod131Key.AC2_M3D	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M3U	(Mod131Key.AC2_M3U	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M3F	(Mod131Key.AC2_M3F	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M3R	(Mod131Key.AC2_M3R	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M4D	(Mod131Key.AC2_M4D	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M4U	(Mod131Key.AC2_M4U	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M4F	(Mod131Key.AC2_M4F	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M4R	(Mod131Key.AC2_M4R	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M5D	(Mod131Key.AC2_M5D	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M5U	(Mod131Key.AC2_M5U	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M5F	(Mod131Key.AC2_M5F	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M5R	(Mod131Key.AC2_M5R	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M6D	(Mod131Key.AC2_M6D	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M6U	(Mod131Key.AC2_M6U	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M6F	(Mod131Key.AC2_M6F	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M6R	(Mod131Key.AC2_M6R	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M7D	(Mod131Key.AC2_M7D	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M7U	(Mod131Key.AC2_M7U	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M7F	(Mod131Key.AC2_M7F	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_M7R	(Mod131Key.AC2_M7R	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_RNP	(Mod131Key.AC2_RNP	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_IEM	(Mod131Key.AC2_IEM	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_IIN	(Mod131Key.AC2_IIN	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_RNM	(Mod131Key.AC2_RNM	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_IC1	(Mod131Key.AC2_IC1	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_IC2	(Mod131Key.AC2_IC2	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_IC3	(Mod131Key.AC2_IC3	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_IC4	(Mod131Key.AC2_IC4	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_IC5	(Mod131Key.AC2_IC5	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_RPF	(Mod131Key.AC2_RPF	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_RLO	(Mod131Key.AC2_RLO	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_RDR	(Mod131Key.AC2_RDR	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_DIA	(Mod131Key.AC2_DIA	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_NET	(Mod131Key.AC2_NET	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_POR	(Mod131Key.AC2_POR	, (mod -> mod.isAEAT())	, null	, null)
		,AC2_RES	(Mod131Key.AC2_RES	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_EPI	(Mod131Key.AC3_EPI	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_EPD	(Mod131Key.AC3_EPD	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_COM	(Mod131Key.AC3_COM	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_TEM	(Mod131Key.AC3_TEM	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_NUE	(Mod131Key.AC3_NUE	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_CEU	(Mod131Key.AC3_CEU	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_LOC	(Mod131Key.AC3_LOC	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_VEH	(Mod131Key.AC3_VEH	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_CAP	(Mod131Key.AC3_CAP	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_MUN	(Mod131Key.AC3_MUN	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_EMP	(Mod131Key.AC3_EMP	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_LOR	(Mod131Key.AC3_LOR	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_PRC	(Mod131Key.AC3_PRC	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M1D	(Mod131Key.AC3_M1D	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M1U	(Mod131Key.AC3_M1U	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M1F	(Mod131Key.AC3_M1F	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M1R	(Mod131Key.AC3_M1R	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M2D	(Mod131Key.AC3_M2D	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M2U	(Mod131Key.AC3_M2U	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M2F	(Mod131Key.AC3_M2F	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M2R	(Mod131Key.AC3_M2R	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M3D	(Mod131Key.AC3_M3D	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M3U	(Mod131Key.AC3_M3U	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M3F	(Mod131Key.AC3_M3F	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M3R	(Mod131Key.AC3_M3R	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M4D	(Mod131Key.AC3_M4D	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M4U	(Mod131Key.AC3_M4U	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M4F	(Mod131Key.AC3_M4F	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M4R	(Mod131Key.AC3_M4R	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M5D	(Mod131Key.AC3_M5D	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M5U	(Mod131Key.AC3_M5U	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M5F	(Mod131Key.AC3_M5F	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M5R	(Mod131Key.AC3_M5R	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M6D	(Mod131Key.AC3_M6D	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M6U	(Mod131Key.AC3_M6U	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M6F	(Mod131Key.AC3_M6F	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M6R	(Mod131Key.AC3_M6R	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M7D	(Mod131Key.AC3_M7D	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M7U	(Mod131Key.AC3_M7U	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M7F	(Mod131Key.AC3_M7F	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_M7R	(Mod131Key.AC3_M7R	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_RNP	(Mod131Key.AC3_RNP	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_IEM	(Mod131Key.AC3_IEM	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_IIN	(Mod131Key.AC3_IIN	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_RNM	(Mod131Key.AC3_RNM	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_IC1	(Mod131Key.AC3_IC1	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_IC2	(Mod131Key.AC3_IC2	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_IC3	(Mod131Key.AC3_IC3	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_IC4	(Mod131Key.AC3_IC4	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_IC5	(Mod131Key.AC3_IC5	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_RPF	(Mod131Key.AC3_RPF	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_RLO	(Mod131Key.AC3_RLO	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_RDR	(Mod131Key.AC3_RDR	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_DIA	(Mod131Key.AC3_DIA	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_NET	(Mod131Key.AC3_NET	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_POR	(Mod131Key.AC3_POR	, (mod -> mod.isAEAT())	, null	, null)
		,AC3_RES	(Mod131Key.AC3_RES	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_EPI	(Mod131Key.AC4_EPI	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_EPD	(Mod131Key.AC4_EPD	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_COM	(Mod131Key.AC4_COM	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_TEM	(Mod131Key.AC4_TEM	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_NUE	(Mod131Key.AC4_NUE	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_CEU	(Mod131Key.AC4_CEU	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_LOC	(Mod131Key.AC4_LOC	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_VEH	(Mod131Key.AC4_VEH	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_CAP	(Mod131Key.AC4_CAP	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_MUN	(Mod131Key.AC4_MUN	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_EMP	(Mod131Key.AC4_EMP	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_LOR	(Mod131Key.AC4_LOR	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_PRC	(Mod131Key.AC4_PRC	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M1D	(Mod131Key.AC4_M1D	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M1U	(Mod131Key.AC4_M1U	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M1F	(Mod131Key.AC4_M1F	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M1R	(Mod131Key.AC4_M1R	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M2D	(Mod131Key.AC4_M2D	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M2U	(Mod131Key.AC4_M2U	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M2F	(Mod131Key.AC4_M2F	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M2R	(Mod131Key.AC4_M2R	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M3D	(Mod131Key.AC4_M3D	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M3U	(Mod131Key.AC4_M3U	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M3F	(Mod131Key.AC4_M3F	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M3R	(Mod131Key.AC4_M3R	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M4D	(Mod131Key.AC4_M4D	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M4U	(Mod131Key.AC4_M4U	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M4F	(Mod131Key.AC4_M4F	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M4R	(Mod131Key.AC4_M4R	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M5D	(Mod131Key.AC4_M5D	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M5U	(Mod131Key.AC4_M5U	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M5F	(Mod131Key.AC4_M5F	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M5R	(Mod131Key.AC4_M5R	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M6D	(Mod131Key.AC4_M6D	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M6U	(Mod131Key.AC4_M6U	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M6F	(Mod131Key.AC4_M6F	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M6R	(Mod131Key.AC4_M6R	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M7D	(Mod131Key.AC4_M7D	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M7U	(Mod131Key.AC4_M7U	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M7F	(Mod131Key.AC4_M7F	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_M7R	(Mod131Key.AC4_M7R	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_RNP	(Mod131Key.AC4_RNP	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_IEM	(Mod131Key.AC4_IEM	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_IIN	(Mod131Key.AC4_IIN	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_RNM	(Mod131Key.AC4_RNM	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_IC1	(Mod131Key.AC4_IC1	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_IC2	(Mod131Key.AC4_IC2	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_IC3	(Mod131Key.AC4_IC3	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_IC4	(Mod131Key.AC4_IC4	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_IC5	(Mod131Key.AC4_IC5	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_RPF	(Mod131Key.AC4_RPF	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_RLO	(Mod131Key.AC4_RLO	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_RDR	(Mod131Key.AC4_RDR	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_DIA	(Mod131Key.AC4_DIA	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_NET	(Mod131Key.AC4_NET	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_POR	(Mod131Key.AC4_POR	, (mod -> mod.isAEAT())	, null	, null)
		,AC4_RES	(Mod131Key.AC4_RES	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_EPI	(Mod131Key.AC5_EPI	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_EPD	(Mod131Key.AC5_EPD	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_COM	(Mod131Key.AC5_COM	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_TEM	(Mod131Key.AC5_TEM	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_NUE	(Mod131Key.AC5_NUE	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_CEU	(Mod131Key.AC5_CEU	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_LOC	(Mod131Key.AC5_LOC	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_VEH	(Mod131Key.AC5_VEH	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_CAP	(Mod131Key.AC5_CAP	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_MUN	(Mod131Key.AC5_MUN	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_EMP	(Mod131Key.AC5_EMP	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_LOR	(Mod131Key.AC5_LOR	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_PRC	(Mod131Key.AC5_PRC	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M1D	(Mod131Key.AC5_M1D	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M1U	(Mod131Key.AC5_M1U	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M1F	(Mod131Key.AC5_M1F	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M1R	(Mod131Key.AC5_M1R	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M2D	(Mod131Key.AC5_M2D	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M2U	(Mod131Key.AC5_M2U	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M2F	(Mod131Key.AC5_M2F	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M2R	(Mod131Key.AC5_M2R	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M3D	(Mod131Key.AC5_M3D	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M3U	(Mod131Key.AC5_M3U	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M3F	(Mod131Key.AC5_M3F	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M3R	(Mod131Key.AC5_M3R	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M4D	(Mod131Key.AC5_M4D	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M4U	(Mod131Key.AC5_M4U	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M4F	(Mod131Key.AC5_M4F	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M4R	(Mod131Key.AC5_M4R	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M5D	(Mod131Key.AC5_M5D	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M5U	(Mod131Key.AC5_M5U	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M5F	(Mod131Key.AC5_M5F	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M5R	(Mod131Key.AC5_M5R	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M6D	(Mod131Key.AC5_M6D	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M6U	(Mod131Key.AC5_M6U	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M6F	(Mod131Key.AC5_M6F	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M6R	(Mod131Key.AC5_M6R	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M7D	(Mod131Key.AC5_M7D	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M7U	(Mod131Key.AC5_M7U	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M7F	(Mod131Key.AC5_M7F	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_M7R	(Mod131Key.AC5_M7R	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_RNP	(Mod131Key.AC5_RNP	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_IEM	(Mod131Key.AC5_IEM	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_IIN	(Mod131Key.AC5_IIN	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_RNM	(Mod131Key.AC5_RNM	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_IC1	(Mod131Key.AC5_IC1	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_IC2	(Mod131Key.AC5_IC2	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_IC3	(Mod131Key.AC5_IC3	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_IC4	(Mod131Key.AC5_IC4	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_IC5	(Mod131Key.AC5_IC5	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_RPF	(Mod131Key.AC5_RPF	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_RLO	(Mod131Key.AC5_RLO	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_RDR	(Mod131Key.AC5_RDR	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_DIA	(Mod131Key.AC5_DIA	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_NET	(Mod131Key.AC5_NET	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_POR	(Mod131Key.AC5_POR	, (mod -> mod.isAEAT())	, null	, null)
		,AC5_RES	(Mod131Key.AC5_RES	, (mod -> mod.isAEAT())	, null	, null)
		,C01		(Mod131Key.C01		, (mod -> mod.isAEAT())	
			, "AC1_NET+AC2_NET+AC3_NET+AC4_NET+AC5_NET"	
			, null)
		,C02		(Mod131Key.C02		, (mod -> mod.isAEAT())	
			, "AC1_RES+AC2_RES+AC3_RES+AC4_RES+AC5_RES"	
			, null)
		,C03		(Mod131Key.C03		, (mod -> mod.isAEAT())	, null	, null)
		,C04		(Mod131Key.C04		, (mod -> mod.isAEAT())	
			, "C03 * 2 / 100"	
			, null)
		,C05		(Mod131Key.C05		, (mod -> mod.isAEAT())	, null	, null)
		,C06		(Mod131Key.C06		, (mod -> mod.isAEAT())	
			, "C05 * 2 / 100"	
			, null)
		,C07		(Mod131Key.C07		, (mod -> mod.isAEAT())	
			, "C02 + C04 + C06"	
			, null)
		,C08		(Mod131Key.C08		, (mod -> mod.isAEAT())	, null	, null)
		,C09 		(Mod131Key.C09 		, (mod -> mod.isAEAT() && mod.getYear() < 2015)	
			, null	, null)
		,C091		(Mod131Key.C091		, (mod -> mod.isAEAT() && mod.getYear() > 2014)	
			, null	, null)
		,C10		(Mod131Key.C10		, (mod -> mod.isAEAT())	
			, "C07 - C08 - C091"	
			, null)
		,C11		(Mod131Key.C11		, (mod -> mod.isAEAT())	
			, null	
			, null)
		,C12		(Mod131Key.C12		, (mod -> mod.isAEAT())	, null	, null)
		,C13		(Mod131Key.C13		, (mod -> mod.isAEAT())	
			, "C10 - C11 - C12"	
			, null)
		,C14		(Mod131Key.C14		, (mod -> mod.isAEAT())	, null	, null)
		,C15		(Mod131Key.C15		, (mod -> mod.isAEAT())	
			, "C13 - C14"	
			, null)
		,CT_TIP		(Mod131Key.CT_TIP	, (mod -> mod.isAEAT())	, null	, null)
		;
		
		private Mod131Key key;
		private IModelAccepter acceptModel;
		private String expression;
		private String template;

		private Mod131KeyDAO(Mod131Key key,IModelAccepter acceptModel,String expression,String template) {
			this.key =  key;
			this.acceptModel =  acceptModel;
			this.expression =  expression;
			this.template =  template;
		}

		public Mod131Key getKey() {
			return key;
		}

		public boolean acceptModel(Mod131 mod) {
			return  (acceptModel.accept(mod));
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
				if (keyDAO.acceptModel(mod) && keyDAO.getKey().getValue().equals(key) ) {
					return keyDAO;
				}
			}
			System.out.println(key);
			return null;
		}
	}
	
	public static Stream<Mod131> getMod131s(AONContext ctx,int domain) {
		return getModelRecords(ctx, domain,FiscalModelType.M131)
				.map( record -> map131(new Mod131(),record))
				.peek(fm -> getModelDetails(ctx,fm).forEach( detail -> fm.put( detail)))
				.peek(fm -> fillActivities(fm))
				;
	}
	
	private static Mod131 fillActivities(Mod131 fm) {
		fm.setActivities(new LinkedList<Mod131Activity>());
		for (int i = 1 ; i < 6 ; i ++) {
			fillActivity(fm,i);		
		}
		return fm;
	}

	private static void fillActivity(Mod131 fm, int i) {
		Mod131Activity act = new Mod131Activity();
		act.setEpigraph( fm.getDescription(Mod131Key.valueOf("AC"+i+"_EPI"))); 
		act.setDescription(fm.getDescription(Mod131Key.valueOf("AC"+i+"_EPD"))); 
		act.setCom(fm.getAmount(Mod131Key.valueOf("AC"+i+"_COM"))); 
		act.setTem((int) fm.getAmount(Mod131Key.valueOf("AC"+i+"_TEM"))); 
		act.setNue((int) fm.getAmount(Mod131Key.valueOf("AC"+i+"_NUE"))); 
		act.setCeu(fm.getAmount(Mod131Key.valueOf("AC"+i+"_CEU"))==1); 
		act.setLoc(fm.getAmount(Mod131Key.valueOf("AC"+i+"_LOC"))==1); 
		act.setVeh((int) fm.getAmount(Mod131Key.valueOf("AC"+i+"_VEH"))); 
		act.setCap(fm.getAmount(Mod131Key.valueOf("AC"+i+"_CAP"))==1); 
		act.setMun((int) fm.getAmount(Mod131Key.valueOf("AC"+i+"_MUN"))); 
		act.setEmp((int) fm.getAmount(Mod131Key.valueOf("AC"+i+"_EMP"))); 
		act.setLor((int) fm.getAmount(Mod131Key.valueOf("AC"+i+"_LOR"))); 
		act.setPrc(fm.getAmount(Mod131Key.valueOf("AC"+i+"_PRC"))); 
		act.setRnp(fm.getAmount(Mod131Key.valueOf("AC"+i+"_RNP"))); 
		act.setIem(fm.getAmount(Mod131Key.valueOf("AC"+i+"_IEM"))); 
		act.setIin(fm.getAmount(Mod131Key.valueOf("AC"+i+"_IIN"))); 
		act.setRnm(fm.getAmount(Mod131Key.valueOf("AC"+i+"_RNM"))); 
		act.setIc1(fm.getAmount(Mod131Key.valueOf("AC"+i+"_IC1"))); 
		act.setIc2(fm.getAmount(Mod131Key.valueOf("AC"+i+"_IC2"))); 
		act.setIc3(fm.getAmount(Mod131Key.valueOf("AC"+i+"_IC3"))); 
		act.setIc4(fm.getAmount(Mod131Key.valueOf("AC"+i+"_IC4"))); 
		act.setIc5(fm.getAmount(Mod131Key.valueOf("AC"+i+"_IC5"))); 
		act.setRpf(fm.getAmount(Mod131Key.valueOf("AC"+i+"_RPF"))); 
		act.setRlo(fm.getAmount(Mod131Key.valueOf("AC"+i+"_RLO"))); 
		act.setRdr(fm.getAmount(Mod131Key.valueOf("AC"+i+"_RDR"))); 
		act.setDia((int) fm.getAmount(Mod131Key.valueOf("AC"+i+"_DIA"))); 
		act.setNet(fm.getAmount(Mod131Key.valueOf("AC"+i+"_NET"))); 
		act.setPor(fm.getAmount(Mod131Key.valueOf("AC"+i+"_POR"))); 
		act.setRes(fm.getAmount(Mod131Key.valueOf("AC"+i+"_RES")));
		LinkedList<Mod131ActivityModule> modules = new LinkedList<Mod131ActivityModule>();
		modules.add( new Mod131ActivityModule());
		modules.add( new Mod131ActivityModule());
		modules.add( new Mod131ActivityModule());
		modules.add( new Mod131ActivityModule());
		modules.add( new Mod131ActivityModule());
		modules.add( new Mod131ActivityModule());
		modules.add( new Mod131ActivityModule());
		act.setModules(modules);
		fm.getActivities().add(act);
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
		FiscalModel fm = save(ctx, mod);
		return getMod131(ctx, fm.getId());
	}
	
	public static Mod131 saveCommentsMod131(AONContext ctx, Mod131 mod) {
		saveComments(ctx, mod);
		return mod;
	}

	private static Mod131MVELContext getMVELcontext(AONContext ctx,Mod131 mod131) {
		Mod131MVELContext mvelCtx = new Mod131MVELContext();
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
				mvelCtx.put(key.getKey().toString(), amount);
				mod.ensureDetail(key.getKey()).setAmount(AonMathUtils.round( amount) );
			}
		}
		return mod; 
	}
	
	public static Mod131 initializeMod131(AONContext ctx,Mod131 mod) {
		if (mod == null) {
			mod = new Mod131();
		}
		if (mod.getDeponents() == null || mod.getDeponents().size() == 0) {
			initializeFiscalModel(ctx, mod);
			mod.putAmount(Mod131Key.P1, 100.0);
			mod.putAmount(Mod131Key.P2, (AppParamDAO.isPermAddressChanges(ctx)?1:0) );
		}
		return mod;
	}
	
	public static Mod131 createMod131(AONContext ctx,Mod131 mod) {
		for (Mod131KeyDAO key : Mod131KeyDAO.values()) {
			if (key.acceptModel(mod)) {
				FiscalModelDetail detail = mod.ensureDetail(key.getKey());
				detail.setExpression(key.getExpression());
			}
		}
//		for (Mod131KeyDAO key : Mod131KeyDAO.values()) {
//			key.initialize(ctx, mod);
//		}
		return calculateMod131(ctx, mod);
	}

	public static String getMod131Info(AONContext ctx, Mod131 mod131, IModelScript<Mod131Key> script, FiscalModelKeyInfo infoKey) {
		Mod131KeyInfoDAO k = Mod131KeyInfoDAO.valueOf(infoKey.toString());
		for (Mod131KeyDAO keyDAO : Mod131KeyDAO.values()) {
			if (keyDAO.getKey() == script.getKeys()[0]) {
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
				String box = " [" + AonStringUtils.leftPad(Integer.toString(keyDAO.getKey().getBox()), 3, '0')+"] ";
				buf.append(AonStringUtils.CR_LF);
				buf.append("<b>DETALLE DEL C\u00C1LCULO DE LA CASILLA: " + box + " - " + script.getLabel() + "</b>");
				buf.append(AonStringUtils.CR_LF);
				buf.append(AonStringUtils.CR_LF);
				buf.append("<ul style=\"padding-left: 20px;\">");
				if (AonStringUtils.isNotBlank( keyDAO.getExpression())) {
					buf.append("<li><b>F\u00F3rmula:</b> " + keyDAO.getExpression() + "</li>" );
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

	// --------------------------------------------------- KEY INTITIALIZATION
}
