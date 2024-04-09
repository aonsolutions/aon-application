package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod131;

import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.DEC2;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.blockCenter;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.bold;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.border;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.marginLeft1em;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.marginTop;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.noWrap;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.textCenter;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.textRight;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.textUnderline;
import static com.esferalia.aon.watson.j2html.TagCreator.div;
import static com.esferalia.aon.watson.j2html.TagCreator.each;
import static com.esferalia.aon.watson.j2html.TagCreator.iff;
import static com.esferalia.aon.watson.j2html.TagCreator.span;
import static com.esferalia.aon.watson.j2html.TagCreator.table;
import static com.esferalia.aon.watson.j2html.TagCreator.tbody;
import static com.esferalia.aon.watson.j2html.TagCreator.td;
import static com.esferalia.aon.watson.j2html.TagCreator.tr;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.AccountingBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod131ActivityModule;
import com.esferalia.aon.occam.api.model.fiscal.modules.ModuleInfo;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2016;
import com.esferalia.aon.occam.api.model.fiscal.modules.Modules2016.Epigraph;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.DeclarationInfoUtil.ExplainRowManager;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.j2html.tags.specialized.DivTag;
import com.esferalia.aon.watson.j2html.tags.specialized.TableTag;
import com.esferalia.aon.watson.mutable.MutableBoolean;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class Mod131AEAT2024Declaration extends Mod131Declaration {
	
	private static final double LIM_C12_1 = 660.14;
	private static final double LIM_C12_2 = 33007.20;

	enum Mod131KeyDAO implements IMod131KeyDAO {
		 P2 ( Mod131Key.P2)
		 
		 // ************************************ [ACTIVIDAD 1]
		,AC1_EPI ( Mod131Key.AC1_EPI
			,(mod,key) -> mod.putDescription(key,ensureActivity(mod,0).getEpigraph())
			,(mod,key) -> ensureActivity(mod,0).setEpigraph(mod.getDescription(key)))
		,AC1_EPD ( Mod131Key.AC1_EPD
			,(mod,key) -> mod.putDescription(key,ensureActivity(mod,0).getDescription())
			,(mod,key) -> ensureActivity(mod,0).setDescription(mod.getDescription(key)))
		,AC1_COM ( Mod131Key.AC1_COM
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getCom())
			,(mod,key) -> ensureActivity(mod,0).setCom(mod.getAmount(key)))
		,AC1_TEM ( Mod131Key.AC1_TEM
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getTem())
			,(mod,key) -> ensureActivity(mod,0).setTem((int) mod.getAmount(key)))
		,AC1_NUE ( Mod131Key.AC1_NUE
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getNue())
			,(mod,key) -> ensureActivity(mod,0).setNue((int) mod.getAmount(key)))
		,AC1_CEU ( Mod131Key.AC1_CEU
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).isCeu()?1:0)
			,(mod,key) -> ensureActivity(mod,0).setCeu(mod.getAmount(key)==1))
		,AC1_LOC ( Mod131Key.AC1_LOC
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).isLoc()?1:0)
			,(mod,key) -> ensureActivity(mod,0).setLoc(mod.getAmount(key)==1))
		,AC1_VEH ( Mod131Key.AC1_VEH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getVeh())
			,(mod,key) -> ensureActivity(mod,0).setVeh((int) mod.getAmount(key)))
		,AC1_CAP ( Mod131Key.AC1_CAP
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).isCap()?1:0)
			,(mod,key) -> ensureActivity(mod,0).setCap(mod.getAmount(key)==1))
		,AC1_TNS ( Mod131Key.AC1_TNS
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).isTns()?1:0)
			,(mod,key) -> ensureActivity(mod,0).setTns(mod.getAmount(key)==1))
		,AC1_TSS ( Mod131Key.AC1_TSS
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).isTss()?1:0)
			,(mod,key) -> ensureActivity(mod,0).setTss(mod.getAmount(key)==1))
		,AC1_BAT ( Mod131Key.AC1_BAT
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getBat())
			,(mod,key) -> ensureActivity(mod,0).setBat((int) mod.getAmount(key)))
		,AC1_MUN ( Mod131Key.AC1_MUN
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getMun())
			,(mod,key) -> ensureActivity(mod,0).setMun((int) mod.getAmount(key)))
		,AC1_EMP ( Mod131Key.AC1_EMP
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getEmp())
			,(mod,key) -> ensureActivity(mod,0).setEmp( (int) mod.getAmount(key)))
		,AC1_LOR ( Mod131Key.AC1_LOR
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getLor())
			,(mod,key) -> ensureActivity(mod,0).setLor((int) mod.getAmount(key)))
		,AC1_PAL ( Mod131Key.AC1_PAL
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getPal())
			,(mod,key) -> ensureActivity(mod,0).setPal((int) mod.getAmount(key)))
		,AC1_PRC ( Mod131Key.AC1_PRC
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getPrc())
			,(mod,key) -> ensureActivity(mod,0).setPrc(mod.getAmount(key)))
		,AC1_M1D ( Mod131Key.AC1_M1D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,0).getDescription())
			,(mod,key) -> ensureModule(mod,0,0).setDescription(mod.getDescription(key)))
		,AC1_M1V ( Mod131Key.AC1_M1V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,0).getValue())
			,(mod,key) -> ensureModule(mod,0,0).setValue(mod.getAmount(key)))
		,AC1_M1U ( Mod131Key.AC1_M1U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,0).getUnit())
			,(mod,key) -> ensureModule(mod,0,0).setUnit(mod.getDescription(key)))
		,AC1_M1F ( Mod131Key.AC1_M1F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,0).getFactor())
			,(mod,key) -> ensureModule(mod,0,0).setFactor(mod.getAmount(key)))
		,AC1_M1R ( Mod131Key.AC1_M1R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,0).getResult())
			,(mod,key) -> ensureModule(mod,0,0).setResult(mod.getAmount(key)))
		,AC1_M2D ( Mod131Key.AC1_M2D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,1).getDescription())
			,(mod,key) -> ensureModule(mod,0,1).setDescription(mod.getDescription(key)))
		,AC1_M2V ( Mod131Key.AC1_M2V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,1).getValue())
			,(mod,key) -> ensureModule(mod,0,1).setValue(mod.getAmount(key)))
		,AC1_M2U ( Mod131Key.AC1_M2U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,1).getUnit())
			,(mod,key) -> ensureModule(mod,0,1).setUnit(mod.getDescription(key)))
		,AC1_M2F ( Mod131Key.AC1_M2F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,1).getFactor())
			,(mod,key) -> ensureModule(mod,0,1).setFactor(mod.getAmount(key)))
		,AC1_M2R ( Mod131Key.AC1_M2R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,1).getResult())
			,(mod,key) -> ensureModule(mod,0,1).setResult(mod.getAmount(key)))
		,AC1_M3D ( Mod131Key.AC1_M3D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,2).getDescription())
			,(mod,key) -> ensureModule(mod,0,2).setDescription(mod.getDescription(key)))
		,AC1_M3V ( Mod131Key.AC1_M3V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,2).getValue())
			,(mod,key) -> ensureModule(mod,0,2).setValue(mod.getAmount(key)))
		,AC1_M3U ( Mod131Key.AC1_M3U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,2).getUnit())
			,(mod,key) -> ensureModule(mod,0,2).setUnit(mod.getDescription(key)))
		,AC1_M3F ( Mod131Key.AC1_M3F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,2).getFactor())
			,(mod,key) -> ensureModule(mod,0,2).setFactor(mod.getAmount(key)))
		,AC1_M3R ( Mod131Key.AC1_M3R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,2).getResult())
			,(mod,key) -> ensureModule(mod,0,2).setResult(mod.getAmount(key)))
		,AC1_M4D ( Mod131Key.AC1_M4D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,3).getDescription())
			,(mod,key) -> ensureModule(mod,0,3).setDescription(mod.getDescription(key)))
		,AC1_M4V ( Mod131Key.AC1_M4V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,3).getValue())
			,(mod,key) -> ensureModule(mod,0,3).setValue(mod.getAmount(key)))
		,AC1_M4U ( Mod131Key.AC1_M4U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,3).getUnit())
			,(mod,key) -> ensureModule(mod,0,3).setUnit(mod.getDescription(key)))
		,AC1_M4F ( Mod131Key.AC1_M4F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,3).getFactor())
			,(mod,key) -> ensureModule(mod,0,3).setFactor(mod.getAmount(key)))
		,AC1_M4R ( Mod131Key.AC1_M4R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,3).getResult())
			,(mod,key) -> ensureModule(mod,0,3).setResult(mod.getAmount(key)))
		,AC1_M5D ( Mod131Key.AC1_M5D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,4).getDescription())
			,(mod,key) -> ensureModule(mod,0,4).setDescription(mod.getDescription(key)))
		,AC1_M5V ( Mod131Key.AC1_M5V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,4).getValue())
			,(mod,key) -> ensureModule(mod,0,4).setValue(mod.getAmount(key)))
		,AC1_M5U ( Mod131Key.AC1_M5U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,4).getUnit())
			,(mod,key) -> ensureModule(mod,0,4).setUnit(mod.getDescription(key)))
		,AC1_M5F ( Mod131Key.AC1_M5F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,4).getFactor())
			,(mod,key) -> ensureModule(mod,0,4).setFactor(mod.getAmount(key)))
		,AC1_M5R ( Mod131Key.AC1_M5R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,4).getResult())
			,(mod,key) -> ensureModule(mod,0,4).setResult(mod.getAmount(key)))
		,AC1_M6D ( Mod131Key.AC1_M6D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,5).getDescription())
			,(mod,key) -> ensureModule(mod,0,5).setDescription(mod.getDescription(key)))
		,AC1_M6V ( Mod131Key.AC1_M6V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,5).getValue())
			,(mod,key) -> ensureModule(mod,0,5).setValue(mod.getAmount(key)))
		,AC1_M6U ( Mod131Key.AC1_M6U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,5).getUnit())
			,(mod,key) -> ensureModule(mod,0,5).setUnit(mod.getDescription(key)))
		,AC1_M6F ( Mod131Key.AC1_M6F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,5).getFactor())
			,(mod,key) -> ensureModule(mod,0,5).setFactor(mod.getAmount(key)))
		,AC1_M6R ( Mod131Key.AC1_M6R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,5).getResult())
			,(mod,key) -> ensureModule(mod,0,5).setResult(mod.getAmount(key)))
		,AC1_M7D ( Mod131Key.AC1_M7D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,6).getDescription())
			,(mod,key) -> ensureModule(mod,0,6).setDescription(mod.getDescription(key)))
		,AC1_M7V ( Mod131Key.AC1_M7V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,6).getValue())
			,(mod,key) -> ensureModule(mod,0,6).setValue(mod.getAmount(key)))
		,AC1_M7U ( Mod131Key.AC1_M7U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,0,6).getUnit())
			,(mod,key) -> ensureModule(mod,0,6).setUnit(mod.getDescription(key)))
		,AC1_M7F ( Mod131Key.AC1_M7F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,6).getFactor())
			,(mod,key) -> ensureModule(mod,0,6).setFactor(mod.getAmount(key)))
		,AC1_M7R ( Mod131Key.AC1_M7R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,0,6).getResult())
			,(mod,key) -> ensureModule(mod,0,6).setResult(mod.getAmount(key)))
		,AC1_RNP ( Mod131Key.AC1_RNP
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getRnp())
			,(mod,key) -> ensureActivity(mod,0).setRnp(mod.getAmount(key)))
		,AC1_IEM ( Mod131Key.AC1_IEM
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getIem())
			,(mod,key) -> ensureActivity(mod,0).setIem(mod.getAmount(key)))
		,AC1_IIN ( Mod131Key.AC1_IIN
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getIin())
			,(mod,key) -> ensureActivity(mod,0).setIin(mod.getAmount(key)))
		,AC1_RNM ( Mod131Key.AC1_RNM
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getRnm())
			,(mod,key) -> ensureActivity(mod,0).setRnm(mod.getAmount(key)))
		,AC1_IC1 ( Mod131Key.AC1_IC1
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getIc1())
			,(mod,key) -> ensureActivity(mod,0).setIc1(mod.getAmount(key)))
		,AC1_IC2 ( Mod131Key.AC1_IC2
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getIc2())
			,(mod,key) -> ensureActivity(mod,0).setIc2(mod.getAmount(key)))
		,AC1_IC3 ( Mod131Key.AC1_IC3
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getIc3())
			,(mod,key) -> ensureActivity(mod,0).setIc3(mod.getAmount(key)))
		,AC1_IC4 ( Mod131Key.AC1_IC4
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getIc4())
			,(mod,key) -> ensureActivity(mod,0).setIc4(mod.getAmount(key)))
		,AC1_IC5 ( Mod131Key.AC1_IC5
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getIc5())
			,(mod,key) -> ensureActivity(mod,0).setIc5(mod.getAmount(key)))
		,AC1_RPF ( Mod131Key.AC1_RPF
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getRpf())
			,(mod,key) -> ensureActivity(mod,0).setRpf(mod.getAmount(key)))
		,AC1_RLO ( Mod131Key.AC1_RLO
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getRlo())
			,(mod,key) -> ensureActivity(mod,0).setRlo(mod.getAmount(key)))
		,AC1_RDR ( Mod131Key.AC1_RDR
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getRdr())
			,(mod,key) -> ensureActivity(mod,0).setRdr(mod.getAmount(key)))
		,AC1_DIA ( Mod131Key.AC1_DIA
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getDia())
			,(mod,key) -> ensureActivity(mod,0).setDia((int) mod.getAmount(key)))
		,AC1_NET ( Mod131Key.AC1_NET
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getNet())
			,(mod,key) -> ensureActivity(mod,0).setNet(mod.getAmount(key)))
		,AC1_POR ( Mod131Key.AC1_POR
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getPor())
			,(mod,key) -> ensureActivity(mod,0).setPor(mod.getAmount(key)))
		,AC1_RES ( Mod131Key.AC1_RES
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getRes())
			,(mod,key) -> ensureActivity(mod,0).setRes(mod.getAmount(key)))
		
		,AC1_GH ( Mod131Key.AC1_GH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getMay19Hours())
			,(mod,key) -> ensureActivity(mod,0).setMay19Hours(mod.getAmountAsInt(key)))
		,AC1_LH ( Mod131Key.AC1_LH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getMen19Hours())
			,(mod,key) -> ensureActivity(mod,0).setMen19Hours(mod.getAmountAsInt(key)))
		,AC1_DH ( Mod131Key.AC1_DH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getDisHours())
			,(mod,key) -> ensureActivity(mod,0).setDisHours(mod.getAmountAsInt(key)))
		,AC1_YH ( Mod131Key.AC1_YH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getYearHours())
			,(mod,key) -> ensureActivity(mod,0).setYearHours(mod.getAmountAsInt(key)))
		,AC1_RGH ( Mod131Key.AC1_RGH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getRsMay19Hours())
			,(mod,key) -> ensureActivity(mod,0).setRsMay19Hours(mod.getAmountAsInt(key)))
		,AC1_RLH ( Mod131Key.AC1_RLH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getRsMen19Hours())
			,(mod,key) -> ensureActivity(mod,0).setRsMen19Hours(mod.getAmountAsInt(key)))
		,AC1_RDH ( Mod131Key.AC1_RDH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getRsDisHours())
			,(mod,key) -> ensureActivity(mod,0).setRsDisHours(mod.getAmountAsInt(key)))
		,AC1_RYH ( Mod131Key.AC1_RYH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getRsYearHours())
			,(mod,key) -> ensureActivity(mod,0).setRsYearHours(mod.getAmountAsInt(key)))
		,AC1_OH ( Mod131Key.AC1_OH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getOwnerHours())
			,(mod,key) -> ensureActivity(mod,0).setOwnerHours(mod.getAmountAsInt(key)))
		,AC1_SH ( Mod131Key.AC1_SH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getSpouseHours())
			,(mod,key) -> ensureActivity(mod,0).setSpouseHours(mod.getAmountAsInt(key)))
		,AC1_CMH ( Mod131Key.AC1_CMH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getChildMen18Hours())
			,(mod,key) -> ensureActivity(mod,0).setChildMen18Hours(mod.getAmountAsInt(key)))
		,AC1_D1 ( Mod131Key.AC1_D1
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getDesks1())
			,(mod,key) -> ensureActivity(mod,0).setDesks1(mod.getAmountAsInt(key)))
		,AC1_DC1 ( Mod131Key.AC1_DC1
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getDeskCapacity1())
			,(mod,key) -> ensureActivity(mod,0).setDeskCapacity1(mod.getAmountAsInt(key)))
		,AC1_D2 ( Mod131Key.AC1_D2
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getDesks2())
			,(mod,key) -> ensureActivity(mod,0).setDesks2(mod.getAmountAsInt(key)))
		,AC1_DC2 ( Mod131Key.AC1_DC2
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getDeskCapacity2())
			,(mod,key) -> ensureActivity(mod,0).setDeskCapacity2(mod.getAmountAsInt(key)))
		,AC1_D3 ( Mod131Key.AC1_D3
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getDesks3())
			,(mod,key) -> ensureActivity(mod,0).setDesks3(mod.getAmountAsInt(key)))
		,AC1_DC3 ( Mod131Key.AC1_DC3
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getDeskCapacity3())
			,(mod,key) -> ensureActivity(mod,0).setDeskCapacity3(mod.getAmountAsInt(key)))
		,AC1_D4 ( Mod131Key.AC1_D4
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getDesks4())
			,(mod,key) -> ensureActivity(mod,0).setDesks4(mod.getAmountAsInt(key)))
		,AC1_DC4 ( Mod131Key.AC1_DC4
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,0).getDeskCapacity4())
			,(mod,key) -> ensureActivity(mod,0).setDeskCapacity4(mod.getAmountAsInt(key)))
		
		
		
		 // ************************************ [ACTIVIDAD 2]
		,AC2_EPI ( Mod131Key.AC2_EPI
			,(mod,key) -> mod.putDescription(key,ensureActivity(mod,1).getEpigraph())
			,(mod,key) -> ensureActivity(mod,1).setEpigraph(mod.getDescription(key)))
		,AC2_EPD ( Mod131Key.AC2_EPD
			,(mod,key) -> mod.putDescription(key,ensureActivity(mod,1).getDescription())
			,(mod,key) -> ensureActivity(mod,1).setDescription(mod.getDescription(key)))
		,AC2_COM ( Mod131Key.AC2_COM
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getCom())
			,(mod,key) -> ensureActivity(mod,1).setCom(mod.getAmount(key)))
		,AC2_TEM ( Mod131Key.AC2_TEM
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getTem())
			,(mod,key) -> ensureActivity(mod,1).setTem((int)mod.getAmount(key)))
		,AC2_NUE ( Mod131Key.AC2_NUE
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getNue())
			,(mod,key) -> ensureActivity(mod,1).setTem((int) mod.getAmount(key)))
		,AC2_CEU ( Mod131Key.AC2_CEU
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).isCeu()?1:0)
			,(mod,key) -> ensureActivity(mod,1).setCeu(mod.getAmount(key)==0))
		,AC2_LOC ( Mod131Key.AC2_LOC
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).isLoc()?1:0)
			,(mod,key) -> ensureActivity(mod,1).setLoc(mod.getAmount(key)==1))
		,AC2_VEH ( Mod131Key.AC2_VEH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getVeh())
			,(mod,key) -> ensureActivity(mod,1).setVeh((int)mod.getAmount(key)))
		,AC2_CAP ( Mod131Key.AC2_CAP
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).isCap()?1:0)
			,(mod,key) -> ensureActivity(mod,1).setCap(mod.getAmount(key)==1))
		,AC2_TNS ( Mod131Key.AC2_TNS
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).isTns()?1:0)
			,(mod,key) -> ensureActivity(mod,1).setTns(mod.getAmount(key)==1))
		,AC2_TSS ( Mod131Key.AC2_TSS
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).isTss()?1:0)
			,(mod,key) -> ensureActivity(mod,1).setTss(mod.getAmount(key)==1))
		,AC2_BAT ( Mod131Key.AC2_BAT
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getBat())
			,(mod,key) -> ensureActivity(mod,1).setBat((int)mod.getAmount(key)))
		,AC2_MUN ( Mod131Key.AC2_MUN
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getMun())
			,(mod,key) -> ensureActivity(mod,1).setMun((int) mod.getAmount(key)))
		,AC2_EMP ( Mod131Key.AC2_EMP
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getEmp())
			,(mod,key) -> ensureActivity(mod,1).setEmp( (int) mod.getAmount(key)))
		,AC2_LOR ( Mod131Key.AC2_LOR
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getLor())
			,(mod,key) -> ensureActivity(mod,1).setLor( (int) mod.getAmount(key)))
		,AC2_PAL ( Mod131Key.AC2_PAL
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getPal())
			,(mod,key) -> ensureActivity(mod,1).setPal((int) mod.getAmount(key)))
		,AC2_PRC ( Mod131Key.AC2_PRC
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getPrc())
			,(mod,key) -> ensureActivity(mod,1).setPrc(mod.getAmount(key)))
		,AC2_M1D ( Mod131Key.AC2_M1D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,0).getDescription())
			,(mod,key) -> ensureModule(mod,1,0).setDescription(mod.getDescription(key)))
		,AC2_M1V ( Mod131Key.AC2_M1V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,0).getValue())
			,(mod,key) -> ensureModule(mod,1,0).setValue(mod.getAmount(key)))
		,AC2_M1U ( Mod131Key.AC2_M1U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,0).getUnit())
			,(mod,key) -> ensureModule(mod,1,0).setUnit(mod.getDescription(key)))
		,AC2_M1F ( Mod131Key.AC2_M1F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,0).getFactor())
			,(mod,key) -> ensureModule(mod,1,0).setFactor(mod.getAmount(key)))
		,AC2_M1R ( Mod131Key.AC2_M1R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,0).getResult())
			,(mod,key) -> ensureModule(mod,1,0).setResult(mod.getAmount(key)))
		,AC2_M2D ( Mod131Key.AC2_M2D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,1).getDescription())
			,(mod,key) -> ensureModule(mod,1,1).setDescription(mod.getDescription(key)))
		,AC2_M2V ( Mod131Key.AC2_M2V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,1).getValue())
			,(mod,key) -> ensureModule(mod,1,1).setValue(mod.getAmount(key)))
		,AC2_M2U ( Mod131Key.AC2_M2U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,1).getUnit())
			,(mod,key) -> ensureModule(mod,1,1).setUnit(mod.getDescription(key)))
		,AC2_M2F ( Mod131Key.AC2_M2F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,1).getFactor())
			,(mod,key) -> ensureModule(mod,1,1).setFactor(mod.getAmount(key)))
		,AC2_M2R ( Mod131Key.AC2_M2R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,1).getResult())
			,(mod,key) -> ensureModule(mod,1,1).setResult(mod.getAmount(key)))
		,AC2_M3D ( Mod131Key.AC2_M3D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,2).getDescription())
			,(mod,key) -> ensureModule(mod,1,2).setDescription(mod.getDescription(key)))
		,AC2_M3V ( Mod131Key.AC2_M3V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,2).getValue())
			,(mod,key) -> ensureModule(mod,1,2).setValue(mod.getAmount(key)))
		,AC2_M3U ( Mod131Key.AC2_M3U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,2).getUnit())
			,(mod,key) -> ensureModule(mod,1,2).setUnit(mod.getDescription(key)))
		,AC2_M3F ( Mod131Key.AC2_M3F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,2).getFactor())
			,(mod,key) -> ensureModule(mod,1,2).setFactor(mod.getAmount(key)))
		,AC2_M3R ( Mod131Key.AC2_M3R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,2).getResult())
			,(mod,key) -> ensureModule(mod,1,2).setResult(mod.getAmount(key)))
		,AC2_M4D ( Mod131Key.AC2_M4D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,3).getDescription())
			,(mod,key) -> ensureModule(mod,1,3).setDescription(mod.getDescription(key)))
		,AC2_M4V ( Mod131Key.AC2_M4V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,3).getValue())
			,(mod,key) -> ensureModule(mod,1,3).setValue(mod.getAmount(key)))
		,AC2_M4U ( Mod131Key.AC2_M4U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,3).getUnit())
			,(mod,key) -> ensureModule(mod,1,3).setUnit(mod.getDescription(key)))
		,AC2_M4F ( Mod131Key.AC2_M4F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,3).getFactor())
			,(mod,key) -> ensureModule(mod,1,3).setFactor(mod.getAmount(key)))
		,AC2_M4R ( Mod131Key.AC2_M4R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,3).getResult())
			,(mod,key) -> ensureModule(mod,1,3).setResult(mod.getAmount(key)))
		,AC2_M5D ( Mod131Key.AC2_M5D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,4).getDescription())
			,(mod,key) -> ensureModule(mod,1,4).setDescription(mod.getDescription(key)))
		,AC2_M5V ( Mod131Key.AC2_M5V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,4).getValue())
			,(mod,key) -> ensureModule(mod,1,4).setValue(mod.getAmount(key)))
		,AC2_M5U ( Mod131Key.AC2_M5U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,4).getUnit())
			,(mod,key) -> ensureModule(mod,1,4).setUnit(mod.getDescription(key)))
		,AC2_M5F ( Mod131Key.AC2_M5F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,4).getFactor())
			,(mod,key) -> ensureModule(mod,1,4).setFactor(mod.getAmount(key)))
		,AC2_M5R ( Mod131Key.AC2_M5R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,4).getResult())
			,(mod,key) -> ensureModule(mod,1,4).setResult(mod.getAmount(key)))
		,AC2_M6D ( Mod131Key.AC2_M6D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,5).getDescription())
			,(mod,key) -> ensureModule(mod,1,5).setDescription(mod.getDescription(key)))
		,AC2_M6V ( Mod131Key.AC2_M6V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,5).getValue())
			,(mod,key) -> ensureModule(mod,1,5).setValue(mod.getAmount(key)))
		,AC2_M6U ( Mod131Key.AC2_M6U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,5).getUnit())
			,(mod,key) -> ensureModule(mod,1,5).setUnit(mod.getDescription(key)))
		,AC2_M6F ( Mod131Key.AC2_M6F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,5).getFactor())
			,(mod,key) -> ensureModule(mod,1,5).setFactor(mod.getAmount(key)))
		,AC2_M6R ( Mod131Key.AC2_M6R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,5).getResult())
			,(mod,key) -> ensureModule(mod,1,5).setResult(mod.getAmount(key)))
		,AC2_M7D ( Mod131Key.AC2_M7D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,6).getDescription())
			,(mod,key) -> ensureModule(mod,1,6).setDescription(mod.getDescription(key)))
		,AC2_M7V ( Mod131Key.AC2_M7V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,6).getValue())
			,(mod,key) -> ensureModule(mod,1,6).setValue(mod.getAmount(key)))
		,AC2_M7U ( Mod131Key.AC2_M7U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,1,6).getUnit())
			,(mod,key) -> ensureModule(mod,1,6).setUnit(mod.getDescription(key)))
		,AC2_M7F ( Mod131Key.AC2_M7F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,6).getFactor())
			,(mod,key) -> ensureModule(mod,1,6).setFactor(mod.getAmount(key)))
		,AC2_M7R ( Mod131Key.AC2_M7R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,1,6).getResult())
			,(mod,key) -> ensureModule(mod,1,6).setResult(mod.getAmount(key)))
		,AC2_RNP ( Mod131Key.AC2_RNP
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getRnp())
			,(mod,key) -> ensureActivity(mod,1).setRnp(mod.getAmount(key)))
		,AC2_IEM ( Mod131Key.AC2_IEM
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getIem())
			,(mod,key) -> ensureActivity(mod,1).setIem(mod.getAmount(key)))
		,AC2_IIN ( Mod131Key.AC2_IIN
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getIin())
			,(mod,key) -> ensureActivity(mod,1).setIin(mod.getAmount(key)))
		,AC2_RNM ( Mod131Key.AC2_RNM
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getRnm())
			,(mod,key) -> ensureActivity(mod,1).setRnm(mod.getAmount(key)))
		,AC2_IC1 ( Mod131Key.AC2_IC1
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getIc1())
			,(mod,key) -> ensureActivity(mod,1).setIc1(mod.getAmount(key)))
		,AC2_IC2 ( Mod131Key.AC2_IC2
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getIc2())
			,(mod,key) -> ensureActivity(mod,1).setIc2(mod.getAmount(key)))
		,AC2_IC3 ( Mod131Key.AC2_IC3
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getIc3())
			,(mod,key) -> ensureActivity(mod,1).setIc3(mod.getAmount(key)))
		,AC2_IC4 ( Mod131Key.AC2_IC4
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getIc4())
			,(mod,key) -> ensureActivity(mod,1).setIc4(mod.getAmount(key)))
		,AC2_IC5 ( Mod131Key.AC2_IC5
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getIc5())
			,(mod,key) -> ensureActivity(mod,1).setIc5(mod.getAmount(key)))
		,AC2_RPF ( Mod131Key.AC2_RPF
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getRpf())
			,(mod,key) -> ensureActivity(mod,1).setRpf(mod.getAmount(key)))
		,AC2_RLO ( Mod131Key.AC2_RLO
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getRlo())
			,(mod,key) -> ensureActivity(mod,1).setRlo(mod.getAmount(key)))
		,AC2_RDR ( Mod131Key.AC2_RDR
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getRdr())
			,(mod,key) -> ensureActivity(mod,1).setRdr(mod.getAmount(key)))
		,AC2_DIA ( Mod131Key.AC2_DIA
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getDia())
			,(mod,key) -> ensureActivity(mod,1).setDia((int)mod.getAmount(key)))
		,AC2_NET ( Mod131Key.AC2_NET
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getNet())
			,(mod,key) -> ensureActivity(mod,1).setNet(mod.getAmount(key)))
		,AC2_POR ( Mod131Key.AC2_POR
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getPor())
			,(mod,key) -> ensureActivity(mod,1).setPor(mod.getAmount(key)))
		,AC2_RES ( Mod131Key.AC2_RES
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getRes())
			,(mod,key) -> ensureActivity(mod,1).setRes(mod.getAmount(key)))
		,AC2_GH ( Mod131Key.AC2_GH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getMay19Hours())
			,(mod,key) -> ensureActivity(mod,1).setMay19Hours(mod.getAmountAsInt(key)))
		,AC2_LH ( Mod131Key.AC2_LH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getMen19Hours())
			,(mod,key) -> ensureActivity(mod,1).setMen19Hours(mod.getAmountAsInt(key)))
		,AC2_DH ( Mod131Key.AC2_DH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getDisHours())
			,(mod,key) -> ensureActivity(mod,1).setDisHours(mod.getAmountAsInt(key)))
		,AC2_YH ( Mod131Key.AC2_YH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getYearHours())
			,(mod,key) -> ensureActivity(mod,1).setYearHours(mod.getAmountAsInt(key)))
		,AC2_RGH ( Mod131Key.AC2_RGH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getRsMay19Hours())
			,(mod,key) -> ensureActivity(mod,1).setRsMay19Hours(mod.getAmountAsInt(key)))
		,AC2_RLH ( Mod131Key.AC2_RLH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getRsMen19Hours())
			,(mod,key) -> ensureActivity(mod,1).setRsMen19Hours(mod.getAmountAsInt(key)))
		,AC2_RDH ( Mod131Key.AC2_RDH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getRsDisHours())
			,(mod,key) -> ensureActivity(mod,1).setRsDisHours(mod.getAmountAsInt(key)))
		,AC2_RYH ( Mod131Key.AC2_RYH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getRsYearHours())
			,(mod,key) -> ensureActivity(mod,1).setRsYearHours(mod.getAmountAsInt(key)))
		,AC2_OH ( Mod131Key.AC2_OH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getOwnerHours())
			,(mod,key) -> ensureActivity(mod,1).setOwnerHours(mod.getAmountAsInt(key)))
		,AC2_SH ( Mod131Key.AC2_SH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getSpouseHours())
			,(mod,key) -> ensureActivity(mod,1).setSpouseHours(mod.getAmountAsInt(key)))
		,AC2_CMH ( Mod131Key.AC2_CMH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getChildMen18Hours())
			,(mod,key) -> ensureActivity(mod,1).setChildMen18Hours(mod.getAmountAsInt(key)))
		,AC2_D1 ( Mod131Key.AC2_D1
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getDesks1())
			,(mod,key) -> ensureActivity(mod,1).setDesks1(mod.getAmountAsInt(key)))
		,AC2_DC1 ( Mod131Key.AC2_DC1
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getDeskCapacity1())
			,(mod,key) -> ensureActivity(mod,1).setDeskCapacity1(mod.getAmountAsInt(key)))
		,AC2_D2 ( Mod131Key.AC2_D2
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getDesks2())
			,(mod,key) -> ensureActivity(mod,1).setDesks2(mod.getAmountAsInt(key)))
		,AC2_DC2 ( Mod131Key.AC2_DC2
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getDeskCapacity2())
			,(mod,key) -> ensureActivity(mod,1).setDeskCapacity2(mod.getAmountAsInt(key)))
		,AC2_D3 ( Mod131Key.AC2_D3
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getDesks3())
			,(mod,key) -> ensureActivity(mod,1).setDesks3(mod.getAmountAsInt(key)))
		,AC2_DC3 ( Mod131Key.AC2_DC3
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getDeskCapacity3())
			,(mod,key) -> ensureActivity(mod,1).setDeskCapacity3(mod.getAmountAsInt(key)))
		,AC2_D4 ( Mod131Key.AC2_D4
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getDesks4())
			,(mod,key) -> ensureActivity(mod,1).setDesks4(mod.getAmountAsInt(key)))
		,AC2_DC4 ( Mod131Key.AC2_DC4
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,1).getDeskCapacity4())
			,(mod,key) -> ensureActivity(mod,1).setDeskCapacity4(mod.getAmountAsInt(key)))
		
		
		
		 // ************************************ [ACTIVIDAD 3]
		,AC3_EPI ( Mod131Key.AC3_EPI
			,(mod,key) -> mod.putDescription(key,ensureActivity(mod,2).getEpigraph())
			,(mod,key) -> ensureActivity(mod,2).setEpigraph(mod.getDescription(key)))
		,AC3_EPD ( Mod131Key.AC3_EPD
			,(mod,key) -> mod.putDescription(key,ensureActivity(mod,2).getDescription())
			,(mod,key) -> ensureActivity(mod,2).setDescription(mod.getDescription(key)))
		,AC3_COM ( Mod131Key.AC3_COM
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getCom())
			,(mod,key) -> ensureActivity(mod,2).setCom(mod.getAmount(key)))
		,AC3_TEM ( Mod131Key.AC3_TEM
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getTem())
			,(mod,key) -> ensureActivity(mod,2).setTem((int)mod.getAmount(key)))
		,AC3_NUE ( Mod131Key.AC3_NUE
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getNue())
			,(mod,key) -> ensureActivity(mod,2).setTem((int) mod.getAmount(key)))
		,AC3_CEU ( Mod131Key.AC3_CEU
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).isCeu()?1:0)
			,(mod,key) -> ensureActivity(mod,2).setCeu(mod.getAmount(key)==1))
		,AC3_LOC ( Mod131Key.AC3_LOC
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).isLoc()?1:0)
			,(mod,key) -> ensureActivity(mod,2).setLoc(mod.getAmount(key)==1))
		,AC3_VEH ( Mod131Key.AC3_VEH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getVeh())
			,(mod,key) -> ensureActivity(mod,2).setVeh((int)mod.getAmount(key)))
		,AC3_CAP ( Mod131Key.AC3_CAP
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).isCap()?1:0)
			,(mod,key) -> ensureActivity(mod,2).setCap(mod.getAmount(key)==1))
		,AC3_TNS ( Mod131Key.AC3_TNS
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).isTns()?1:0)
			,(mod,key) -> ensureActivity(mod,2).setTns(mod.getAmount(key)==1))
		,AC3_TSS ( Mod131Key.AC3_TSS
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).isTss()?1:0)
			,(mod,key) -> ensureActivity(mod,2).setTss(mod.getAmount(key)==1))
		,AC3_BAT ( Mod131Key.AC3_BAT
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getBat())
			,(mod,key) -> ensureActivity(mod,2).setBat((int)mod.getAmount(key)))
		,AC3_MUN ( Mod131Key.AC3_MUN
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getMun())
			,(mod,key) -> ensureActivity(mod,2).setMun( (int) mod.getAmount(key)))
		,AC3_EMP ( Mod131Key.AC3_EMP
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getEmp())
			,(mod,key) -> ensureActivity(mod,2).setEmp( (int) mod.getAmount(key)))
		,AC3_LOR ( Mod131Key.AC3_LOR
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getLor())
			,(mod,key) -> ensureActivity(mod,2).setLor( (int) mod.getAmount(key)))
		,AC3_PAL ( Mod131Key.AC3_PAL
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getPal())
			,(mod,key) -> ensureActivity(mod,2).setPal((int) mod.getAmount(key)))
		,AC3_PRC ( Mod131Key.AC3_PRC
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getPrc())
			,(mod,key) -> ensureActivity(mod,2).setPrc(mod.getAmount(key)))
		,AC3_M1D ( Mod131Key.AC3_M1D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,0).getDescription())
			,(mod,key) -> ensureModule(mod,2,0).setDescription(mod.getDescription(key)))
		,AC3_M1V ( Mod131Key.AC3_M1V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,0).getValue())
			,(mod,key) -> ensureModule(mod,2,0).setValue(mod.getAmount(key)))
		,AC3_M1U ( Mod131Key.AC3_M1U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,0).getUnit())
			,(mod,key) -> ensureModule(mod,2,0).setUnit(mod.getDescription(key)))
		,AC3_M1F ( Mod131Key.AC3_M1F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,0).getFactor())
			,(mod,key) -> ensureModule(mod,2,0).setFactor(mod.getAmount(key)))
		,AC3_M1R ( Mod131Key.AC3_M1R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,0).getResult())
			,(mod,key) -> ensureModule(mod,2,0).setResult(mod.getAmount(key)))
		,AC3_M2D ( Mod131Key.AC3_M2D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,1).getDescription())
			,(mod,key) -> ensureModule(mod,2,1).setDescription(mod.getDescription(key)))
		,AC3_M2V ( Mod131Key.AC3_M2V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,1).getValue())
			,(mod,key) -> ensureModule(mod,2,1).setValue(mod.getAmount(key)))
		,AC3_M2U ( Mod131Key.AC3_M2U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,1).getUnit())
			,(mod,key) -> ensureModule(mod,2,1).setUnit(mod.getDescription(key)))
		,AC3_M2F ( Mod131Key.AC3_M2F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,1).getFactor())
			,(mod,key) -> ensureModule(mod,2,1).setFactor(mod.getAmount(key)))
		,AC3_M2R ( Mod131Key.AC3_M2R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,1).getResult())
			,(mod,key) -> ensureModule(mod,2,1).setResult(mod.getAmount(key)))
		,AC3_M3D ( Mod131Key.AC3_M3D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,2).getDescription())
			,(mod,key) -> ensureModule(mod,2,2).setDescription(mod.getDescription(key)))
		,AC3_M3V ( Mod131Key.AC3_M3V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,2).getValue())
			,(mod,key) -> ensureModule(mod,2,2).setValue(mod.getAmount(key)))
		,AC3_M3U ( Mod131Key.AC3_M3U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,2).getUnit())
			,(mod,key) -> ensureModule(mod,2,2).setUnit(mod.getDescription(key)))
		,AC3_M3F ( Mod131Key.AC3_M3F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,2).getFactor())
			,(mod,key) -> ensureModule(mod,2,2).setFactor(mod.getAmount(key)))
		,AC3_M3R ( Mod131Key.AC3_M3R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,2).getResult())
			,(mod,key) -> ensureModule(mod,2,2).setResult(mod.getAmount(key)))
		,AC3_M4D ( Mod131Key.AC3_M4D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,3).getDescription())
			,(mod,key) -> ensureModule(mod,2,3).setDescription(mod.getDescription(key)))
		,AC3_M4V ( Mod131Key.AC3_M4V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,3).getValue())
			,(mod,key) -> ensureModule(mod,2,3).setValue(mod.getAmount(key)))
		,AC3_M4U ( Mod131Key.AC3_M4U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,3).getUnit())
			,(mod,key) -> ensureModule(mod,2,3).setUnit(mod.getDescription(key)))
		,AC3_M4F ( Mod131Key.AC3_M4F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,3).getFactor())
			,(mod,key) -> ensureModule(mod,2,3).setFactor(mod.getAmount(key)))
		,AC3_M4R ( Mod131Key.AC3_M4R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,3).getResult())
			,(mod,key) -> ensureModule(mod,2,3).setResult(mod.getAmount(key)))
		,AC3_M5D ( Mod131Key.AC3_M5D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,4).getDescription())
			,(mod,key) -> ensureModule(mod,2,4).setDescription(mod.getDescription(key)))
		,AC3_M5V ( Mod131Key.AC3_M5V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,4).getValue())
			,(mod,key) -> ensureModule(mod,2,4).setValue(mod.getAmount(key)))
		,AC3_M5U ( Mod131Key.AC3_M5U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,4).getUnit())
			,(mod,key) -> ensureModule(mod,2,4).setUnit(mod.getDescription(key)))
		,AC3_M5F ( Mod131Key.AC3_M5F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,4).getFactor())
			,(mod,key) -> ensureModule(mod,2,4).setFactor(mod.getAmount(key)))
		,AC3_M5R ( Mod131Key.AC3_M5R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,4).getResult())
			,(mod,key) -> ensureModule(mod,2,4).setResult(mod.getAmount(key)))
		,AC3_M6D ( Mod131Key.AC3_M6D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,5).getDescription())
			,(mod,key) -> ensureModule(mod,2,5).setDescription(mod.getDescription(key)))
		,AC3_M6V ( Mod131Key.AC3_M6V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,5).getValue())
			,(mod,key) -> ensureModule(mod,2,5).setValue(mod.getAmount(key)))
		,AC3_M6U ( Mod131Key.AC3_M6U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,5).getUnit())
			,(mod,key) -> ensureModule(mod,2,5).setUnit(mod.getDescription(key)))
		,AC3_M6F ( Mod131Key.AC3_M6F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,5).getFactor())
			,(mod,key) -> ensureModule(mod,2,5).setFactor(mod.getAmount(key)))
		,AC3_M6R ( Mod131Key.AC3_M6R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,5).getResult())
			,(mod,key) -> ensureModule(mod,2,5).setResult(mod.getAmount(key)))
		,AC3_M7D ( Mod131Key.AC3_M7D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,6).getDescription())
			,(mod,key) -> ensureModule(mod,2,6).setDescription(mod.getDescription(key)))
		,AC3_M7V ( Mod131Key.AC3_M7V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,6).getValue())
			,(mod,key) -> ensureModule(mod,2,6).setValue(mod.getAmount(key)))
		,AC3_M7U ( Mod131Key.AC3_M7U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,2,6).getUnit())
			,(mod,key) -> ensureModule(mod,2,6).setUnit(mod.getDescription(key)))
		,AC3_M7F ( Mod131Key.AC3_M7F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,6).getFactor())
			,(mod,key) -> ensureModule(mod,2,6).setFactor(mod.getAmount(key)))
		,AC3_M7R ( Mod131Key.AC3_M7R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,2,6).getResult())
			,(mod,key) -> ensureModule(mod,2,6).setResult(mod.getAmount(key)))
		,AC3_RNP ( Mod131Key.AC3_RNP
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getRnp())
			,(mod,key) -> ensureActivity(mod,2).setRnp(mod.getAmount(key)))
		,AC3_IEM ( Mod131Key.AC3_IEM
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getIem())
			,(mod,key) -> ensureActivity(mod,2).setIem(mod.getAmount(key)))
		,AC3_IIN ( Mod131Key.AC3_IIN
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getIin())
			,(mod,key) -> ensureActivity(mod,2).setIin(mod.getAmount(key)))
		,AC3_RNM ( Mod131Key.AC3_RNM
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getRnm())
			,(mod,key) -> ensureActivity(mod,2).setRnm(mod.getAmount(key)))
		,AC3_IC1 ( Mod131Key.AC3_IC1
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getIc1())
			,(mod,key) -> ensureActivity(mod,2).setIc1(mod.getAmount(key)))
		,AC3_IC2 ( Mod131Key.AC3_IC2
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getIc2())
			,(mod,key) -> ensureActivity(mod,2).setIc2(mod.getAmount(key)))
		,AC3_IC3 ( Mod131Key.AC3_IC3
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getIc3())
			,(mod,key) -> ensureActivity(mod,2).setIc3(mod.getAmount(key)))
		,AC3_IC4 ( Mod131Key.AC3_IC4
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getIc4())
			,(mod,key) -> ensureActivity(mod,2).setIc4(mod.getAmount(key)))
		,AC3_IC5 ( Mod131Key.AC3_IC5
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getIc5())
			,(mod,key) -> ensureActivity(mod,2).setIc5(mod.getAmount(key)))
		,AC3_RPF ( Mod131Key.AC3_RPF
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getRpf())
			,(mod,key) -> ensureActivity(mod,2).setRpf(mod.getAmount(key)))
		,AC3_RLO ( Mod131Key.AC3_RLO
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getRlo())
			,(mod,key) -> ensureActivity(mod,2).setRlo(mod.getAmount(key)))
		,AC3_RDR ( Mod131Key.AC3_RDR
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getRdr())
			,(mod,key) -> ensureActivity(mod,2).setRdr(mod.getAmount(key)))
		,AC3_DIA ( Mod131Key.AC3_DIA
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getDia())
			,(mod,key) -> ensureActivity(mod,2).setDia((int)mod.getAmount(key)))
		,AC3_NET ( Mod131Key.AC3_NET
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getNet())
			,(mod,key) -> ensureActivity(mod,2).setNet(mod.getAmount(key)))
		,AC3_POR ( Mod131Key.AC3_POR
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getPor())
			,(mod,key) -> ensureActivity(mod,2).setPor(mod.getAmount(key)))
		,AC3_RES ( Mod131Key.AC3_RES
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getRes())
			,(mod,key) -> ensureActivity(mod,2).setRes(mod.getAmount(key)))
		,AC3_GH ( Mod131Key.AC3_GH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getMay19Hours())
			,(mod,key) -> ensureActivity(mod,2).setMay19Hours(mod.getAmountAsInt(key)))
		,AC3_LH ( Mod131Key.AC3_LH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getMen19Hours())
			,(mod,key) -> ensureActivity(mod,2).setMen19Hours(mod.getAmountAsInt(key)))
		,AC3_DH ( Mod131Key.AC3_DH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getDisHours())
			,(mod,key) -> ensureActivity(mod,2).setDisHours(mod.getAmountAsInt(key)))
		,AC3_YH ( Mod131Key.AC3_YH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getYearHours())
			,(mod,key) -> ensureActivity(mod,2).setYearHours(mod.getAmountAsInt(key)))
		,AC3_RGH ( Mod131Key.AC3_RGH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getRsMay19Hours())
			,(mod,key) -> ensureActivity(mod,2).setRsMay19Hours(mod.getAmountAsInt(key)))
		,AC3_RLH ( Mod131Key.AC3_RLH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getRsMen19Hours())
			,(mod,key) -> ensureActivity(mod,2).setRsMen19Hours(mod.getAmountAsInt(key)))
		,AC3_RDH ( Mod131Key.AC3_RDH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getRsDisHours())
			,(mod,key) -> ensureActivity(mod,2).setRsDisHours(mod.getAmountAsInt(key)))
		,AC3_RYH ( Mod131Key.AC3_RYH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getRsYearHours())
			,(mod,key) -> ensureActivity(mod,2).setRsYearHours(mod.getAmountAsInt(key)))
		,AC3_OH ( Mod131Key.AC3_OH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getOwnerHours())
			,(mod,key) -> ensureActivity(mod,2).setOwnerHours(mod.getAmountAsInt(key)))
		,AC3_SH ( Mod131Key.AC3_SH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getSpouseHours())
			,(mod,key) -> ensureActivity(mod,2).setSpouseHours(mod.getAmountAsInt(key)))
		,AC3_CMH ( Mod131Key.AC3_CMH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getChildMen18Hours())
			,(mod,key) -> ensureActivity(mod,2).setChildMen18Hours(mod.getAmountAsInt(key)))
		,AC3_D1 ( Mod131Key.AC3_D1
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getDesks1())
			,(mod,key) -> ensureActivity(mod,2).setDesks1(mod.getAmountAsInt(key)))
		,AC3_DC1 ( Mod131Key.AC3_DC1
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getDeskCapacity1())
			,(mod,key) -> ensureActivity(mod,2).setDeskCapacity1(mod.getAmountAsInt(key)))
		,AC3_D2 ( Mod131Key.AC3_D2
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getDesks2())
			,(mod,key) -> ensureActivity(mod,2).setDesks2(mod.getAmountAsInt(key)))
		,AC3_DC2 ( Mod131Key.AC3_DC2
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getDeskCapacity2())
			,(mod,key) -> ensureActivity(mod,2).setDeskCapacity2(mod.getAmountAsInt(key)))
		,AC3_D3 ( Mod131Key.AC3_D3
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getDesks3())
			,(mod,key) -> ensureActivity(mod,2).setDesks3(mod.getAmountAsInt(key)))
		,AC3_DC3 ( Mod131Key.AC3_DC3
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getDeskCapacity3())
			,(mod,key) -> ensureActivity(mod,2).setDeskCapacity3(mod.getAmountAsInt(key)))
		,AC3_D4 ( Mod131Key.AC3_D4
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getDesks4())
			,(mod,key) -> ensureActivity(mod,2).setDesks4(mod.getAmountAsInt(key)))
		,AC3_DC4 ( Mod131Key.AC3_DC4
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,2).getDeskCapacity4())
			,(mod,key) -> ensureActivity(mod,1).setDeskCapacity4(mod.getAmountAsInt(key)))
		
		
		
		 // ************************************ [ACTIVIDAD 4]
		,AC4_EPI ( Mod131Key.AC4_EPI
			,(mod,key) -> mod.putDescription(key,ensureActivity(mod,3).getEpigraph())
			,(mod,key) -> ensureActivity(mod,3).setEpigraph(mod.getDescription(key)))
		,AC4_EPD ( Mod131Key.AC4_EPD
			,(mod,key) -> mod.putDescription(key,ensureActivity(mod,3).getDescription())
			,(mod,key) -> ensureActivity(mod,3).setDescription(mod.getDescription(key)))
		,AC4_COM ( Mod131Key.AC4_COM
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getCom())
			,(mod,key) -> ensureActivity(mod,3).setCom(mod.getAmount(key)))
		,AC4_TEM ( Mod131Key.AC4_TEM
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getTem())
			,(mod,key) -> ensureActivity(mod,3).setTem((int)mod.getAmount(key)))
		,AC4_NUE ( Mod131Key.AC4_NUE
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getNue())
			,(mod,key) -> ensureActivity(mod,3).setTem((int) mod.getAmount(key)))
		,AC4_CEU ( Mod131Key.AC4_CEU
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).isCeu()?1:0)
			,(mod,key) -> ensureActivity(mod,3).setCeu(mod.getAmount(key)==1))
		,AC4_LOC ( Mod131Key.AC4_LOC
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).isLoc()?1:0)
			,(mod,key) -> ensureActivity(mod,3).setLoc(mod.getAmount(key)==1))
		,AC4_VEH ( Mod131Key.AC4_VEH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getVeh())
			,(mod,key) -> ensureActivity(mod,3).setVeh((int)mod.getAmount(key)))
		,AC4_CAP ( Mod131Key.AC4_CAP
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).isCap()?1:0)
			,(mod,key) -> ensureActivity(mod,3).setCap(mod.getAmount(key)==1))
		,AC4_TNS ( Mod131Key.AC4_TNS
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).isTns()?1:0)
			,(mod,key) -> ensureActivity(mod,3).setTns(mod.getAmount(key)==1))
		,AC4_TSS ( Mod131Key.AC4_TSS
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).isTss()?1:0)
			,(mod,key) -> ensureActivity(mod,3).setTss(mod.getAmount(key)==1))
		,AC4_BAT ( Mod131Key.AC4_BAT
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getBat())
			,(mod,key) -> ensureActivity(mod,3).setBat((int)mod.getAmount(key)))
		,AC4_MUN ( Mod131Key.AC4_MUN
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getMun())
			,(mod,key) -> ensureActivity(mod,3).setMun( (int) mod.getAmount(key)))
		,AC4_EMP ( Mod131Key.AC4_EMP
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getEmp())
			,(mod,key) -> ensureActivity(mod,3).setEmp( (int)mod.getAmount(key)))
		,AC4_LOR ( Mod131Key.AC4_LOR
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getLor())
			,(mod,key) -> ensureActivity(mod,3).setLor( (int) mod.getAmount(key)))
		,AC4_PAL ( Mod131Key.AC4_PAL
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getPal())
			,(mod,key) -> ensureActivity(mod,3).setPal((int) mod.getAmount(key)))
		,AC4_PRC ( Mod131Key.AC4_PRC
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getPrc())
			,(mod,key) -> ensureActivity(mod,3).setPrc(mod.getAmount(key)))
		,AC4_M1D ( Mod131Key.AC4_M1D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,0).getDescription())
			,(mod,key) -> ensureModule(mod,3,0).setDescription(mod.getDescription(key)))
		,AC4_M1V ( Mod131Key.AC4_M1V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,0).getValue())
			,(mod,key) -> ensureModule(mod,3,0).setValue(mod.getAmount(key)))
		,AC4_M1U ( Mod131Key.AC4_M1U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,0).getUnit())
			,(mod,key) -> ensureModule(mod,3,0).setUnit(mod.getDescription(key)))
		,AC4_M1F ( Mod131Key.AC4_M1F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,0).getFactor())
			,(mod,key) -> ensureModule(mod,3,0).setFactor(mod.getAmount(key)))
		,AC4_M1R ( Mod131Key.AC4_M1R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,0).getResult())
			,(mod,key) -> ensureModule(mod,3,0).setResult(mod.getAmount(key)))
		,AC4_M2D ( Mod131Key.AC4_M2D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,1).getDescription())
			,(mod,key) -> ensureModule(mod,3,1).setDescription(mod.getDescription(key)))
		,AC4_M2V ( Mod131Key.AC4_M2V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,1).getValue())
			,(mod,key) -> ensureModule(mod,3,1).setValue(mod.getAmount(key)))
		,AC4_M2U ( Mod131Key.AC4_M2U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,1).getUnit())
			,(mod,key) -> ensureModule(mod,3,1).setUnit(mod.getDescription(key)))
		,AC4_M2F ( Mod131Key.AC4_M2F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,1).getFactor())
			,(mod,key) -> ensureModule(mod,3,1).setFactor(mod.getAmount(key)))
		,AC4_M2R ( Mod131Key.AC4_M2R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,1).getResult())
			,(mod,key) -> ensureModule(mod,3,1).setResult(mod.getAmount(key)))
		,AC4_M3D ( Mod131Key.AC4_M3D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,2).getDescription())
			,(mod,key) -> ensureModule(mod,3,2).setDescription(mod.getDescription(key)))
		,AC4_M3V ( Mod131Key.AC4_M3V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,2).getValue())
			,(mod,key) -> ensureModule(mod,3,2).setValue(mod.getAmount(key)))
		,AC4_M3U ( Mod131Key.AC4_M3U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,2).getUnit())
			,(mod,key) -> ensureModule(mod,3,2).setUnit(mod.getDescription(key)))
		,AC4_M3F ( Mod131Key.AC4_M3F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,2).getFactor())
			,(mod,key) -> ensureModule(mod,3,2).setFactor(mod.getAmount(key)))
		,AC4_M3R ( Mod131Key.AC4_M3R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,2).getResult())
			,(mod,key) -> ensureModule(mod,3,2).setResult(mod.getAmount(key)))
		,AC4_M4D ( Mod131Key.AC4_M4D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,3).getDescription())
			,(mod,key) -> ensureModule(mod,3,3).setDescription(mod.getDescription(key)))
		,AC4_M4V ( Mod131Key.AC4_M4V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,3).getValue())
			,(mod,key) -> ensureModule(mod,3,3).setValue(mod.getAmount(key)))
		,AC4_M4U ( Mod131Key.AC4_M4U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,3).getUnit())
			,(mod,key) -> ensureModule(mod,3,3).setUnit(mod.getDescription(key)))
		,AC4_M4F ( Mod131Key.AC4_M4F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,3).getFactor())
			,(mod,key) -> ensureModule(mod,3,3).setFactor(mod.getAmount(key)))
		,AC4_M4R ( Mod131Key.AC4_M4R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,3).getResult())
			,(mod,key) -> ensureModule(mod,3,3).setResult(mod.getAmount(key)))
		,AC4_M5D ( Mod131Key.AC4_M5D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,4).getDescription())
			,(mod,key) -> ensureModule(mod,3,4).setDescription(mod.getDescription(key)))
		,AC4_M5V ( Mod131Key.AC4_M5V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,4).getValue())
			,(mod,key) -> ensureModule(mod,3,4).setValue(mod.getAmount(key)))
		,AC4_M5U ( Mod131Key.AC4_M5U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,4).getUnit())
			,(mod,key) -> ensureModule(mod,3,4).setUnit(mod.getDescription(key)))
		,AC4_M5F ( Mod131Key.AC4_M5F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,4).getFactor())
			,(mod,key) -> ensureModule(mod,3,4).setFactor(mod.getAmount(key)))
		,AC4_M5R ( Mod131Key.AC4_M5R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,4).getResult())
			,(mod,key) -> ensureModule(mod,3,4).setResult(mod.getAmount(key)))
		,AC4_M6D ( Mod131Key.AC4_M6D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,5).getDescription())
			,(mod,key) -> ensureModule(mod,3,5).setDescription(mod.getDescription(key)))
		,AC4_M6V ( Mod131Key.AC4_M6V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,5).getValue())
			,(mod,key) -> ensureModule(mod,3,5).setValue(mod.getAmount(key)))
		,AC4_M6U ( Mod131Key.AC4_M6U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,5).getUnit())
			,(mod,key) -> ensureModule(mod,3,5).setUnit(mod.getDescription(key)))
		,AC4_M6F ( Mod131Key.AC4_M6F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,5).getFactor())
			,(mod,key) -> ensureModule(mod,3,5).setFactor(mod.getAmount(key)))
		,AC4_M6R ( Mod131Key.AC4_M6R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,5).getResult())
			,(mod,key) -> ensureModule(mod,3,5).setResult(mod.getAmount(key)))
		,AC4_M7D ( Mod131Key.AC4_M7D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,6).getDescription())
			,(mod,key) -> ensureModule(mod,3,6).setDescription(mod.getDescription(key)))
		,AC4_M7V ( Mod131Key.AC4_M7V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,6).getValue())
			,(mod,key) -> ensureModule(mod,3,6).setValue(mod.getAmount(key)))
		,AC4_M7U ( Mod131Key.AC4_M7U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,3,6).getUnit())
			,(mod,key) -> ensureModule(mod,3,6).setUnit(mod.getDescription(key)))
		,AC4_M7F ( Mod131Key.AC4_M7F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,6).getFactor())
			,(mod,key) -> ensureModule(mod,3,6).setFactor(mod.getAmount(key)))
		,AC4_M7R ( Mod131Key.AC4_M7R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,3,6).getResult())
			,(mod,key) -> ensureModule(mod,3,6).setResult(mod.getAmount(key)))
		,AC4_RNP ( Mod131Key.AC4_RNP
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getRnp())
			,(mod,key) -> ensureActivity(mod,3).setRnp(mod.getAmount(key)))
		,AC4_IEM ( Mod131Key.AC4_IEM
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getIem())
			,(mod,key) -> ensureActivity(mod,3).setIem(mod.getAmount(key)))
		,AC4_IIN ( Mod131Key.AC4_IIN
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getIin())
			,(mod,key) -> ensureActivity(mod,3).setIin(mod.getAmount(key)))
		,AC4_RNM ( Mod131Key.AC4_RNM
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getRnm())
			,(mod,key) -> ensureActivity(mod,3).setRnm(mod.getAmount(key)))
		,AC4_IC1 ( Mod131Key.AC4_IC1
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getIc1())
			,(mod,key) -> ensureActivity(mod,3).setIc1(mod.getAmount(key)))
		,AC4_IC2 ( Mod131Key.AC4_IC2
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getIc2())
			,(mod,key) -> ensureActivity(mod,3).setIc2(mod.getAmount(key)))
		,AC4_IC3 ( Mod131Key.AC4_IC3
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getIc3())
			,(mod,key) -> ensureActivity(mod,3).setIc3(mod.getAmount(key)))
		,AC4_IC4 ( Mod131Key.AC4_IC4
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getIc4())
			,(mod,key) -> ensureActivity(mod,3).setIc4(mod.getAmount(key)))
		,AC4_IC5 ( Mod131Key.AC4_IC5
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getIc5())
			,(mod,key) -> ensureActivity(mod,3).setIc5(mod.getAmount(key)))
		,AC4_RPF ( Mod131Key.AC4_RPF
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getRpf())
			,(mod,key) -> ensureActivity(mod,3).setRpf(mod.getAmount(key)))
		,AC4_RLO ( Mod131Key.AC4_RLO
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getRlo())
			,(mod,key) -> ensureActivity(mod,3).setRlo(mod.getAmount(key)))
		,AC4_RDR ( Mod131Key.AC4_RDR
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getRdr())
			,(mod,key) -> ensureActivity(mod,3).setRdr(mod.getAmount(key)))
		,AC4_DIA ( Mod131Key.AC4_DIA
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getDia())
			,(mod,key) -> ensureActivity(mod,3).setDia((int)mod.getAmount(key)))
		,AC4_NET ( Mod131Key.AC4_NET
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getNet())
			,(mod,key) -> ensureActivity(mod,3).setNet(mod.getAmount(key)))
		,AC4_POR ( Mod131Key.AC4_POR
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getPor())
			,(mod,key) -> ensureActivity(mod,3).setPor(mod.getAmount(key)))
		,AC4_RES ( Mod131Key.AC4_RES
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getRes())
			,(mod,key) -> ensureActivity(mod,3).setRes(mod.getAmount(key)))
		,AC4_GH ( Mod131Key.AC4_GH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getMay19Hours())
			,(mod,key) -> ensureActivity(mod,3).setMay19Hours(mod.getAmountAsInt(key)))
		,AC4_LH ( Mod131Key.AC4_LH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getMen19Hours())
			,(mod,key) -> ensureActivity(mod,3).setMen19Hours(mod.getAmountAsInt(key)))
		,AC4_DH ( Mod131Key.AC4_DH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getDisHours())
			,(mod,key) -> ensureActivity(mod,3).setDisHours(mod.getAmountAsInt(key)))
		,AC4_YH ( Mod131Key.AC4_YH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getYearHours())
			,(mod,key) -> ensureActivity(mod,3).setYearHours(mod.getAmountAsInt(key)))
		,AC4_RGH ( Mod131Key.AC4_RGH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getRsMay19Hours())
			,(mod,key) -> ensureActivity(mod,3).setRsMay19Hours(mod.getAmountAsInt(key)))
		,AC4_RLH ( Mod131Key.AC4_RLH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getRsMen19Hours())
			,(mod,key) -> ensureActivity(mod,3).setRsMen19Hours(mod.getAmountAsInt(key)))
		,AC4_RDH ( Mod131Key.AC4_RDH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getRsDisHours())
			,(mod,key) -> ensureActivity(mod,3).setRsDisHours(mod.getAmountAsInt(key)))
		,AC4_RYH ( Mod131Key.AC4_RYH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getRsYearHours())
			,(mod,key) -> ensureActivity(mod,3).setRsYearHours(mod.getAmountAsInt(key)))
		,AC4_OH ( Mod131Key.AC4_OH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getOwnerHours())
			,(mod,key) -> ensureActivity(mod,3).setOwnerHours(mod.getAmountAsInt(key)))
		,AC4_SH ( Mod131Key.AC4_SH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getSpouseHours())
			,(mod,key) -> ensureActivity(mod,3).setSpouseHours(mod.getAmountAsInt(key)))
		,AC4_CMH ( Mod131Key.AC4_CMH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getChildMen18Hours())
			,(mod,key) -> ensureActivity(mod,3).setChildMen18Hours(mod.getAmountAsInt(key)))
		,AC4_D1 ( Mod131Key.AC4_D1
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getDesks1())
			,(mod,key) -> ensureActivity(mod,3).setDesks1(mod.getAmountAsInt(key)))
		,AC4_DC1 ( Mod131Key.AC4_DC1
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getDeskCapacity1())
			,(mod,key) -> ensureActivity(mod,3).setDeskCapacity1(mod.getAmountAsInt(key)))
		,AC4_D2 ( Mod131Key.AC4_D2
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getDesks2())
			,(mod,key) -> ensureActivity(mod,3).setDesks2(mod.getAmountAsInt(key)))
		,AC4_DC2 ( Mod131Key.AC4_DC2
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getDeskCapacity2())
			,(mod,key) -> ensureActivity(mod,3).setDeskCapacity2(mod.getAmountAsInt(key)))
		,AC4_D3 ( Mod131Key.AC4_D3
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getDesks3())
			,(mod,key) -> ensureActivity(mod,3).setDesks3(mod.getAmountAsInt(key)))
		,AC4_DC3 ( Mod131Key.AC4_DC3
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getDeskCapacity3())
			,(mod,key) -> ensureActivity(mod,3).setDeskCapacity3(mod.getAmountAsInt(key)))
		,AC4_D4 ( Mod131Key.AC4_D4
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getDesks4())
			,(mod,key) -> ensureActivity(mod,3).setDesks4(mod.getAmountAsInt(key)))
		,AC4_DC4 ( Mod131Key.AC4_DC4
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,3).getDeskCapacity4())
			,(mod,key) -> ensureActivity(mod,1).setDeskCapacity4(mod.getAmountAsInt(key)))
		
		
		
		 // ************************************ [ACTIVIDAD 5]
		,AC5_EPI ( Mod131Key.AC5_EPI
			,(mod,key) -> mod.putDescription(key,ensureActivity(mod,4).getEpigraph())
			,(mod,key) -> ensureActivity(mod,4).setEpigraph(mod.getDescription(key)))
		,AC5_EPD ( Mod131Key.AC5_EPD
			,(mod,key) -> mod.putDescription(key,ensureActivity(mod,4).getDescription())
			,(mod,key) -> ensureActivity(mod,4).setDescription(mod.getDescription(key)))
		,AC5_COM ( Mod131Key.AC5_COM
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getCom())
			,(mod,key) -> ensureActivity(mod,4).setCom(mod.getAmount(key)))
		,AC5_TEM ( Mod131Key.AC5_TEM
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getTem())
			,(mod,key) -> ensureActivity(mod,4).setTem((int)mod.getAmount(key)))
		,AC5_NUE ( Mod131Key.AC5_NUE
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getNue())
			,(mod,key) -> ensureActivity(mod,4).setTem((int) mod.getAmount(key)))
		,AC5_CEU ( Mod131Key.AC5_CEU
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).isCeu()?1:0)
			,(mod,key) -> ensureActivity(mod,4).setCeu(mod.getAmount(key)==1))
		,AC5_LOC ( Mod131Key.AC5_LOC
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).isLoc()?1:0)
			,(mod,key) -> ensureActivity(mod,4).setLoc(mod.getAmount(key)==1))
		,AC5_VEH ( Mod131Key.AC5_VEH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getVeh())
			,(mod,key) -> ensureActivity(mod,4).setVeh((int)mod.getAmount(key)))
		,AC5_CAP ( Mod131Key.AC5_CAP
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).isCap()?1:0)
			,(mod,key) -> ensureActivity(mod,4).setCap(mod.getAmount(key)==1))
		,AC5_TNS ( Mod131Key.AC5_TNS
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).isTns()?1:0)
			,(mod,key) -> ensureActivity(mod,4).setTns(mod.getAmount(key)==1))
		,AC5_TSS ( Mod131Key.AC5_TSS
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).isTss()?1:0)
			,(mod,key) -> ensureActivity(mod,4).setTss(mod.getAmount(key)==1))
		,AC5_BAT ( Mod131Key.AC5_BAT
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getBat())
			,(mod,key) -> ensureActivity(mod,4).setBat((int)mod.getAmount(key)))
		,AC5_MUN ( Mod131Key.AC5_MUN
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getMun())
			,(mod,key) -> ensureActivity(mod,4).setMun( (int) mod.getAmount(key)))
		,AC5_EMP ( Mod131Key.AC5_EMP
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getEmp())
			,(mod,key) -> ensureActivity(mod,4).setEmp( (int)mod.getAmount(key)))
		,AC5_LOR ( Mod131Key.AC5_LOR
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getLor())
			,(mod,key) -> ensureActivity(mod,4).setLor( (int) mod.getAmount(key)))
		,AC5_PAL ( Mod131Key.AC5_PAL
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getPal())
			,(mod,key) -> ensureActivity(mod,4).setPal((int) mod.getAmount(key)))
		,AC5_PRC ( Mod131Key.AC5_PRC
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getPrc())
			,(mod,key) -> ensureActivity(mod,4).setPrc(mod.getAmount(key)))
		,AC5_M1D ( Mod131Key.AC5_M1D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,0).getDescription())
			,(mod,key) -> ensureModule(mod,4,0).setDescription(mod.getDescription(key)))
		,AC5_M1V ( Mod131Key.AC5_M1V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,0).getValue())
			,(mod,key) -> ensureModule(mod,4,0).setValue(mod.getAmount(key)))
		,AC5_M1U ( Mod131Key.AC5_M1U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,0).getUnit())
			,(mod,key) -> ensureModule(mod,4,0).setUnit(mod.getDescription(key)))
		,AC5_M1F ( Mod131Key.AC5_M1F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,0).getFactor())
			,(mod,key) -> ensureModule(mod,4,0).setFactor(mod.getAmount(key)))
		,AC5_M1R ( Mod131Key.AC5_M1R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,0).getResult())
			,(mod,key) -> ensureModule(mod,4,0).setResult(mod.getAmount(key)))
		,AC5_M2D ( Mod131Key.AC5_M2D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,1).getDescription())
			,(mod,key) -> ensureModule(mod,4,1).setDescription(mod.getDescription(key)))
		,AC5_M2V ( Mod131Key.AC5_M2V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,1).getValue())
			,(mod,key) -> ensureModule(mod,4,1).setValue(mod.getAmount(key)))
		,AC5_M2U ( Mod131Key.AC5_M2U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,1).getUnit())
			,(mod,key) -> ensureModule(mod,4,1).setUnit(mod.getDescription(key)))
		,AC5_M2F ( Mod131Key.AC5_M2F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,1).getFactor())
			,(mod,key) -> ensureModule(mod,4,1).setFactor(mod.getAmount(key)))
		,AC5_M2R ( Mod131Key.AC5_M2R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,1).getResult())
			,(mod,key) -> ensureModule(mod,4,1).setResult(mod.getAmount(key)))
		,AC5_M3D ( Mod131Key.AC5_M3D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,2).getDescription())
			,(mod,key) -> ensureModule(mod,4,2).setDescription(mod.getDescription(key)))
		,AC5_M3V ( Mod131Key.AC5_M3V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,2).getValue())
			,(mod,key) -> ensureModule(mod,4,2).setValue(mod.getAmount(key)))
		,AC5_M3U ( Mod131Key.AC5_M3U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,2).getUnit())
			,(mod,key) -> ensureModule(mod,4,2).setUnit(mod.getDescription(key)))
		,AC5_M3F ( Mod131Key.AC5_M3F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,2).getFactor())
			,(mod,key) -> ensureModule(mod,4,2).setFactor(mod.getAmount(key)))
		,AC5_M3R ( Mod131Key.AC5_M3R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,2).getResult())
			,(mod,key) -> ensureModule(mod,4,2).setResult(mod.getAmount(key)))
		,AC5_M4D ( Mod131Key.AC5_M4D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,3).getDescription())
			,(mod,key) -> ensureModule(mod,4,3).setDescription(mod.getDescription(key)))
		,AC5_M4V ( Mod131Key.AC5_M4V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,3).getValue())
			,(mod,key) -> ensureModule(mod,4,3).setValue(mod.getAmount(key)))
		,AC5_M4U ( Mod131Key.AC5_M4U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,3).getUnit())
			,(mod,key) -> ensureModule(mod,4,3).setUnit(mod.getDescription(key)))
		,AC5_M4F ( Mod131Key.AC5_M4F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,3).getFactor())
			,(mod,key) -> ensureModule(mod,4,3).setFactor(mod.getAmount(key)))
		,AC5_M4R ( Mod131Key.AC5_M4R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,3).getResult())
			,(mod,key) -> ensureModule(mod,4,3).setResult(mod.getAmount(key)))
		,AC5_M5D ( Mod131Key.AC5_M5D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,4).getDescription())
			,(mod,key) -> ensureModule(mod,4,4).setDescription(mod.getDescription(key)))
		,AC5_M5V ( Mod131Key.AC5_M5V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,4).getValue())
			,(mod,key) -> ensureModule(mod,4,4).setValue(mod.getAmount(key)))
		,AC5_M5U ( Mod131Key.AC5_M5U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,4).getUnit())
			,(mod,key) -> ensureModule(mod,4,4).setUnit(mod.getDescription(key)))
		,AC5_M5F ( Mod131Key.AC5_M5F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,4).getFactor())
			,(mod,key) -> ensureModule(mod,4,4).setFactor(mod.getAmount(key)))
		,AC5_M5R ( Mod131Key.AC5_M5R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,4).getResult())
			,(mod,key) -> ensureModule(mod,4,4).setResult(mod.getAmount(key)))
		,AC5_M6D ( Mod131Key.AC5_M6D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,5).getDescription())
			,(mod,key) -> ensureModule(mod,4,5).setDescription(mod.getDescription(key)))
		,AC5_M6V ( Mod131Key.AC5_M6V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,5).getValue())
			,(mod,key) -> ensureModule(mod,4,5).setValue(mod.getAmount(key)))
		,AC5_M6U ( Mod131Key.AC5_M6U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,5).getUnit())
			,(mod,key) -> ensureModule(mod,4,5).setUnit(mod.getDescription(key)))
		,AC5_M6F ( Mod131Key.AC5_M6F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,5).getFactor())
			,(mod,key) -> ensureModule(mod,4,5).setFactor(mod.getAmount(key)))
		,AC5_M6R ( Mod131Key.AC5_M6R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,5).getResult())
			,(mod,key) -> ensureModule(mod,4,5).setResult(mod.getAmount(key)))
		,AC5_M7D ( Mod131Key.AC5_M7D
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,6).getDescription())
			,(mod,key) -> ensureModule(mod,4,6).setDescription(mod.getDescription(key)))
		,AC5_M7V ( Mod131Key.AC5_M7V
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,6).getValue())
			,(mod,key) -> ensureModule(mod,4,6).setValue(mod.getAmount(key)))
		,AC5_M7U ( Mod131Key.AC5_M7U
			,(mod,key) -> mod.putDescription(key,ensureModule(mod,4,6).getUnit())
			,(mod,key) -> ensureModule(mod,4,6).setUnit(mod.getDescription(key)))
		,AC5_M7F ( Mod131Key.AC5_M7F
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,6).getFactor())
			,(mod,key) -> ensureModule(mod,4,6).setFactor(mod.getAmount(key)))
		,AC5_M7R ( Mod131Key.AC5_M7R
			,(mod,key) -> mod.putAmount(key,ensureModule(mod,4,6).getResult())
			,(mod,key) -> ensureModule(mod,4,6).setResult(mod.getAmount(key)))
		,AC5_RNP ( Mod131Key.AC5_RNP
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getRnp())
			,(mod,key) -> ensureActivity(mod,4).setRnp(mod.getAmount(key)))
		,AC5_IEM ( Mod131Key.AC5_IEM
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getIem())
			,(mod,key) -> ensureActivity(mod,4).setIem(mod.getAmount(key)))
		,AC5_IIN ( Mod131Key.AC5_IIN
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getIin())
			,(mod,key) -> ensureActivity(mod,4).setIin(mod.getAmount(key)))
		,AC5_RNM ( Mod131Key.AC5_RNM
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getRnm())
			,(mod,key) -> ensureActivity(mod,4).setRnm(mod.getAmount(key)))
		,AC5_IC1 ( Mod131Key.AC5_IC1
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getIc1())
			,(mod,key) -> ensureActivity(mod,4).setIc1(mod.getAmount(key)))
		,AC5_IC2 ( Mod131Key.AC5_IC2
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getIc2())
			,(mod,key) -> ensureActivity(mod,4).setIc2(mod.getAmount(key)))
		,AC5_IC3 ( Mod131Key.AC5_IC3
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getIc3())
			,(mod,key) -> ensureActivity(mod,4).setIc3(mod.getAmount(key)))
		,AC5_IC4 ( Mod131Key.AC5_IC4
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getIc4())
			,(mod,key) -> ensureActivity(mod,4).setIc4(mod.getAmount(key)))
		,AC5_IC5 ( Mod131Key.AC5_IC5
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getIc5())
			,(mod,key) -> ensureActivity(mod,4).setIc5(mod.getAmount(key)))
		,AC5_RPF ( Mod131Key.AC5_RPF
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getRpf())
			,(mod,key) -> ensureActivity(mod,4).setRpf(mod.getAmount(key)))
		,AC5_RLO ( Mod131Key.AC5_RLO
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getRlo())
			,(mod,key) -> ensureActivity(mod,4).setRlo(mod.getAmount(key)))
		,AC5_RDR ( Mod131Key.AC5_RDR
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getRdr())
			,(mod,key) -> ensureActivity(mod,4).setRdr(mod.getAmount(key)))
		,AC5_DIA ( Mod131Key.AC5_DIA
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getDia())
			,(mod,key) -> ensureActivity(mod,4).setDia((int)mod.getAmount(key)))
		,AC5_NET ( Mod131Key.AC5_NET
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getNet())
			,(mod,key) -> ensureActivity(mod,4).setNet(mod.getAmount(key)))
		,AC5_POR ( Mod131Key.AC5_POR
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getPor())
			,(mod,key) -> ensureActivity(mod,4).setPor(mod.getAmount(key)))
		,AC5_RES ( Mod131Key.AC5_RES
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getRes())
			,(mod,key) -> ensureActivity(mod,4).setRes(mod.getAmount(key)))
		,AC5_GH ( Mod131Key.AC5_GH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getMay19Hours())
			,(mod,key) -> ensureActivity(mod,4).setMay19Hours(mod.getAmountAsInt(key)))
		,AC5_LH ( Mod131Key.AC5_LH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getMen19Hours())
			,(mod,key) -> ensureActivity(mod,4).setMen19Hours(mod.getAmountAsInt(key)))
		,AC5_DH ( Mod131Key.AC5_DH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getDisHours())
			,(mod,key) -> ensureActivity(mod,4).setDisHours(mod.getAmountAsInt(key)))
		,AC5_YH ( Mod131Key.AC5_YH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getYearHours())
			,(mod,key) -> ensureActivity(mod,4).setYearHours(mod.getAmountAsInt(key)))
		,AC5_RGH ( Mod131Key.AC5_RGH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getRsMay19Hours())
			,(mod,key) -> ensureActivity(mod,4).setRsMay19Hours(mod.getAmountAsInt(key)))
		,AC5_RLH ( Mod131Key.AC5_RLH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getRsMen19Hours())
			,(mod,key) -> ensureActivity(mod,4).setRsMen19Hours(mod.getAmountAsInt(key)))
		,AC5_RDH ( Mod131Key.AC5_RDH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getRsDisHours())
			,(mod,key) -> ensureActivity(mod,4).setRsDisHours(mod.getAmountAsInt(key)))
		,AC5_RYH ( Mod131Key.AC5_RYH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getRsYearHours())
			,(mod,key) -> ensureActivity(mod,4).setRsYearHours(mod.getAmountAsInt(key)))
		,AC5_OH ( Mod131Key.AC5_OH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getOwnerHours())
			,(mod,key) -> ensureActivity(mod,4).setOwnerHours(mod.getAmountAsInt(key)))
		,AC5_SH ( Mod131Key.AC5_SH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getSpouseHours())
			,(mod,key) -> ensureActivity(mod,4).setSpouseHours(mod.getAmountAsInt(key)))
		,AC5_CMH ( Mod131Key.AC5_CMH
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getChildMen18Hours())
			,(mod,key) -> ensureActivity(mod,4).setChildMen18Hours(mod.getAmountAsInt(key)))
		,AC5_D1 ( Mod131Key.AC5_D1
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getDesks1())
			,(mod,key) -> ensureActivity(mod,4).setDesks1(mod.getAmountAsInt(key)))
		,AC5_DC1 ( Mod131Key.AC5_DC1
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getDeskCapacity1())
			,(mod,key) -> ensureActivity(mod,4).setDeskCapacity1(mod.getAmountAsInt(key)))
		,AC5_D2 ( Mod131Key.AC5_D2
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getDesks2())
			,(mod,key) -> ensureActivity(mod,4).setDesks2(mod.getAmountAsInt(key)))
		,AC5_DC2 ( Mod131Key.AC5_DC2
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getDeskCapacity2())
			,(mod,key) -> ensureActivity(mod,4).setDeskCapacity2(mod.getAmountAsInt(key)))
		,AC5_D3 ( Mod131Key.AC5_D3
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getDesks3())
			,(mod,key) -> ensureActivity(mod,4).setDesks3(mod.getAmountAsInt(key)))
		,AC5_DC3 ( Mod131Key.AC5_DC3
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getDeskCapacity3())
			,(mod,key) -> ensureActivity(mod,4).setDeskCapacity3(mod.getAmountAsInt(key)))
		,AC5_D4 ( Mod131Key.AC5_D4
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getDesks4())
			,(mod,key) -> ensureActivity(mod,4).setDesks4(mod.getAmountAsInt(key)))
		,AC5_DC4 ( Mod131Key.AC5_DC4
			,(mod,key) -> mod.putAmount(key,ensureActivity(mod,4).getDeskCapacity4())
			,(mod,key) -> ensureActivity(mod,1).setDeskCapacity4(mod.getAmountAsInt(key)))

		// Suma de rendimientos netos
		,C01 ( Mod131Key.C01
			,"computeC01()"
			,Mod131AEAT2024Declaration::getC01ComputeKeyInfo
		)
		// Pago fraccionado previo del trimestre. Suma de resultados.
		,C02 ( Mod131Key.C02
			,"computeC02()"
			,Mod131AEAT2024Declaration::getC02ComputeKeyInfo
		)
		// I. Actividades econ\u00F3micas en estimaci\u00F3n objetiva distintas de las agr\u00EDcolas, ganaderas y forestales.
		// Volumen de ventas o  ingresos del trimestre (excluidas las subvenciones de capital y las indemnizaciones)
		,C03 ( Mod131Key.C03,null,null,null
			,Mod131AEAT2024Declaration::initializeC03
			,null
			,Mod131AEAT2024Declaration::getC03ComputeKeyInfo
		)
		//	C03 - Deducción por rentas obtenidas en Ceuta y Melilla (S/N)
		,C03_1 ( Mod131Key.C03_1)
		//	C03 - Volumen de ventas o ingresos del trimestre
		,C03_2 ( Mod131Key.C03_2)
		//	C03 - Si para el cálculo del pago fraccionado desea aplicar un porcentaje superior al que establece la normativa, indique el porcentaje que desea aplicar
		,C03_3 ( Mod131Key.C03_3)
		//	C03 - Volumen de ingresos del primer trimestre o, si la actividad se ha iniciado en 2024, del trimestre en el que se haya comenzado su ejercicio
		,C03_4 ( Mod131Key.C03_4)
		//	C03 - Número de días en los que ha ejercido la actividad en el primer trimestre, o si la actividad se ha iniciado en 2024, del trimestre en el que se haya comenzado su ejercicio
		,C03_5 ( Mod131Key.C03_5)
		//	C03 - Número de días en los que previsiblemente ejercerá la actividad durante el año
		,C03_6 ( Mod131Key.C03_6)
		
		
		// Pago fraccionado previo del trimestre. 2 por 100 del importe de la casilla 03.
		,C04 ( Mod131Key.C04 ,"C03 * 2 / 100" )
		
		// III. Actividades agr\u00EDcolas, ganaderas y forestales, en estimaci\u00F3n objetiva.
		// Volumen de ingresos del trimestre (excluidas las subvenciones de capital y las indemnizaciones)
		,C05 ( Mod131Key.C05,null,null,null
			,(ctx,mod) -> mod.putAmount(Mod131Key.C05, getInitialC05(ctx,mod))
			,null
			,Mod131AEAT2024Declaration::getC05ComputeKeyInfo
		)
		// Pago fraccionado previo del trimestre. 2 por 100 del importe de la casilla 05.
		,C06 ( Mod131Key.C06, "C05 * 2 / 100" )
		// IV. Total liquidaci\u00F3n.
		// Suma de los pagos fraccionados previos de trimestre (02 + 04 + 06) 
		,C07 ( Mod131Key.C07 ,"C02 + C04 + C06" )
		// A deducir. Retenciones e ingresos a cuenta soportados correspondientes al trimestre.
		,C08 ( Mod131Key.C08 ,(ctx,br) -> !br.isExempt() )
		
		// , (mod -> mod.isAEAT() && mod.getYear() < 2015)
		,C09 ( Mod131Key.C09)
		
		// A deducir. Minoraci\u00F3n por aplicaci\u00F3n de la deducci\u00F3n a que se refiere el art\u00EDculo 110.3 C) del reglamento del impuesto.
		// ,(mod -> mod.isAEAT() && mod.getYear() > 2014)
		,C091( Mod131Key.C091,null,null,null
			,Mod131AEAT2024Declaration::initializeC091
			,null
			,Mod131AEAT2024Declaration::getC091ComputeKeyInfo)

		// "Diferencia 		([07] - [08] - [09]). Si se obtiene una cantidad negativa, cons\u00EDgnela con signo menos (-)
		,C10 ( Mod131Key.C10, "C07 - C08 - C091" )
		
		// "Resultados negativos de trimestres anteriores."
		,C11 ( Mod131Key.C11,null,null,null,null
			,"computeC11()"
			,Mod131AEAT2024Declaration::getC11ComputeKeyInfo
			)
		,C12 ( Mod131Key.C12
			,"computeC12()"
			,Mod131AEAT2024Declaration::getC12ComputeKeyInfo
			)
		,C13 ( Mod131Key.C13
			,"C10 - C11 - C12"
			, (ctx,mod) -> "<li>@{C10} menos @{C11} menos @{C12} igual <b>@{C13}</b></li>"
		)
		,C14 ( Mod131Key.C14,null,null,null
			,(ctx,mod) ->  mod.putAmount(Mod131Key.C14,mod.isComplementary()?Mod131DAO.getSamePeriodFiscalModels(ctx, mod).mapToDouble(fm -> fm.getDeclarationResult()).sum():0.0)
			,null
			, Mod131KeyDAO::getC14ComputeKeyInfo)
		,C15 ( Mod131Key.C15
			,"C13 - C14"
			, (ctx,mod) -> "<li>@{C13} menos @{C14} igual <b>@{C15}</b></li>"
		)
		,CT_TIP ( Mod131Key.CT_TIP )
		;
		private static String getC14ComputeKeyInfo(AONContext ctx, Mod131 mod) {
			return DeclarationInfoUtil.getExplain( ctx, mod, Mod131Key.C14, Mod131DAO.getSamePeriodFiscalModels(ctx, mod), new ExplainRowManager());	
		}

		
		private Mod131Key key;
		private IIrpfBreakdownAccepter irpfBreakdownAccepter;
		private IActivityFromMapFiller activityFromMapFiller;
		private IMapFromActivityFiller mapFromActivityFiller;
		private IValueInitializer initializer;
		private String expression;
		private IValueInfo info;
		
		private Mod131KeyDAO(Mod131Key key) {
			this(key, null, null, null, null, null, null );
		}
		
		private Mod131KeyDAO(Mod131Key key
			,IActivityFromMapFiller activityFromMapFiller
			,IMapFromActivityFiller mapFromActivityFiller) {
			this(key, null, activityFromMapFiller, mapFromActivityFiller, null, null, null );
		}

		private Mod131KeyDAO(Mod131Key key,String expression) {
			this(key, null, null, null, null, expression, null );
		}

		private Mod131KeyDAO(Mod131Key key
			,String expression
			,IValueInfo info) {
			this(key, null, null, null, null, expression, info );
		}
		
		private Mod131KeyDAO(Mod131Key key, IIrpfBreakdownAccepter irpfBreakdownAccepter) {
			this(key, irpfBreakdownAccepter, null, null, null, null, null);
		}

		private Mod131KeyDAO(Mod131Key key
				,IIrpfBreakdownAccepter irpfBreakdownAccepter
				,IActivityFromMapFiller activityFromMapFiller
				,IMapFromActivityFiller mapFromActivityFiller
				,IValueInitializer initializer
				,String expression
				,IValueInfo info) {
			
			this.key =  key;
			this.irpfBreakdownAccepter = irpfBreakdownAccepter; 
			this.activityFromMapFiller = activityFromMapFiller;
			this.mapFromActivityFiller = mapFromActivityFiller;
			this.initializer = initializer;
			this.expression =  expression;
			this.info =  info;
		}

		public Mod131Key getKey() {
			return key;
		}

		public String getExpression() {
			return expression;
		}
		
		@Override
		public boolean acceptIrpfBreakdown(Mod131 mod,IrpfBreakdown  br) {
			return this.irpfBreakdownAccepter != null && this.irpfBreakdownAccepter.accept(mod, br); 
		}
		
		@Override
		public String info(AONContext ctx, Mod131 mod) {
			return (this.info != null)?this.info.info(ctx, mod):null;
		}
		@Override
		public void fillActivityFromMap(Mod131 mod, Mod131Key key) {
			if (this.mapFromActivityFiller != null) {
				mapFromActivityFiller.fill(mod,key);
			}
		}
		@Override
		public void fillMapFromActivity(Mod131 mod, Mod131Key key) {
			if (this.activityFromMapFiller != null) {
				activityFromMapFiller.fill(mod,key);
			}
		}
		
		public static Mod131KeyDAO safeValueOf(Mod131 mod, String key) {
			if (AonStringUtils.isBlank(key)) return null;
			return AonCollectionUtils.stream(Mod131KeyDAO.values())
				.filter( k -> k.getKey().getValue().equals(key))
				.findFirst()
				.orElse(null);
		}

		public void initialize(AONContext ctx, Mod131 mod) {
			if (initializer != null) {
				initializer.initialize(ctx, mod);
			}
		}
	}
	
	// -----------------------------------------------------------------------
	private static Stream<AccountingBreakdown> getIncomesStream(AONContext ctx, Mod131 mod) {
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
					));
	}
	
	
	// ----------------------------------------------------------------------------------------------------- [C01]
	private static String getC01ComputeKeyInfo(AONContext ctx, Mod131 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod131Key.C03, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				return table(
						tr(
							td("Epi")			.withStyle( border+bold+textUnderline),
							td("Actividad")		.withStyle( border+bold+textUnderline),
							td("Rend. Neto.")	.withStyle( border+bold+textRight+textUnderline)
						)
						,tbody(
							each(mod.getEffectiveActivities(), (index, act) -> 
								tr(
									td(act.getEpigraph())			.withStyle( border),
									td(act.getDescription())		.withStyle( border),
									td(DEC2.format( act.getNet()))	.withStyle( border+textRight )
							))
						)
						,tr(
							td("")			.withStyle( border+bold ),
							td("Resultado")	.withStyle( border+bold+textRight ),
							td(DEC2.format( AonMathUtils.round(AonCollectionUtils.stream(mod.getEffectiveActivities()).mapToDouble( a -> a.getNet()).sum())))
											.withStyle( border+bold+textRight)
						)
					).render();
			}
		});	
	}
	
	// ----------------------------------------------------------------------------------------------------- [C02]
	private static String getC02ComputeKeyInfo(AONContext ctx, Mod131 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod131Key.C03, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				double c02Sum = AonMathUtils.round(AonCollectionUtils.stream(mod.getEffectiveActivities()).mapToDouble( a -> a.getRes()).sum());
				return div( 
					table(
						tr(
							td("Epi").withStyle( border+bold+textUnderline),
							td("Actividad").withStyle( border+bold+textUnderline),
							td("Resultado").withStyle( border+bold+textRight+textUnderline)
						)
						,tbody(
							each(mod.getEffectiveActivities(), (index, act) -> 
								tr(
									td(   act.getEpigraph()).withStyle( border),
									td(act.getDescription()).withStyle( border),
									td(DEC2.format( act.getRes())).withStyle( border+textRight)
							))
						)
						,tr(
							td("")						.withStyle( border+bold),
							td("Resultado")				.withStyle( border+bold+textRight),
							td(DEC2.format( c02Sum ))	.withStyle( border+bold+textRight)
						)
					)
					,iff ( AonMathUtils.isLessThanZero(c02Sum), div( "Al ser el sumatorio negativo, resultado 0" ).withStyle( marginTop ))
				).render();
			}
		});	
	}
	
	// ----------------------------------------------------------------------------------------------------- [C03]
	private static void initializeC03(AONContext ctx, Mod131 mod) {
		mod.putAmount(Mod131Key.C03, getInitialC03(ctx,mod));
	}
	private static Stream<AccountingBreakdown> getInitialC03Stream(AONContext ctx, Mod131 mod) {
		return getIncomesStream(ctx, mod)		
			.filter( br -> (!br.isFarmer() && br.isObjectiveRegime()));
	}
	private static double getInitialC03(AONContext ctx, Mod131 mod) {
		return getInitialC03Stream(ctx, mod)
			.mapToDouble(br -> br.getCreditBalance())
			.sum();
	}
	private static String getC03ComputeKeyInfo(AONContext ctx, Mod131 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod131Key.C03, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				return div( 
					span( "Desde contabilidad, saldo acreedor de las cuentas de los grupos "
				 		+ "70, 71, 72, 73, 75, 76, 77, 78 y 79, cuando la actividad económica vinculada "
				 		+ "al asiento contable sea \"Régimen de Estimación objetiva\" y no sea "
				 		+ " agrícola, ganadera y/o forestal.").withStyle( textCenter )
					,div(					 
						span( "Saldo acreedor desde el" )
						,span( DeclarationInfoUtil.FMT.format(AonDateUtils.getYearFirstDay(mod.getYear()))).withStyle( marginLeft1em+bold)
						,span("al").withStyle( marginLeft1em)
						,span( DeclarationInfoUtil.FMT.format(FiscalUtils.getPeriodEnd(mod)) ).withStyle( marginLeft1em+bold)
						,span(":").withStyle( marginLeft1em)
						,span(DEC2.format( mod.getAmount( Mod130Key.C03 ) )).withStyle( marginLeft1em+bold)
					).withStyle( textCenter+marginTop )
				).render();
			}
		});	
	}
	
	
	// ----------------------------------------------------------------------------------------------------- [C05]
	private static Stream<AccountingBreakdown> getInitialC05Stream(AONContext ctx, Mod131 mod) {
		return getIncomesStream(ctx, mod)		
			.filter( br -> (br.isFarmer() && br.isObjectiveRegime()));
	}
	private static double getInitialC05(AONContext ctx, Mod131 mod) {
		return getInitialC05Stream(ctx, mod)
			.mapToDouble(br -> br.getCreditBalance())
			.sum();
	}
	private static String getC05ComputeKeyInfo(AONContext ctx, Mod131 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod131Key.C05, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				return div( 
					span( "Desde contabilidad, saldo acreedor de las cuentas de los grupos "
				 		+ "70, 71, 72, 73, 75, 76, 77, 78 y 79, cuando la actividad económica vinculada "
				 		+ "al asiento contable sea \"Régimen de Estimación objetiva\" y sea "
				 		+ " agrícola, ganadera y/o forestal.").withStyle( textCenter )
					,div(					 
						span( "Saldo acreedor desde el" )
						,span( DeclarationInfoUtil.FMT.format(AonDateUtils.getYearFirstDay(mod.getYear()))).withStyle( marginLeft1em+bold)
						,span("al").withStyle( marginLeft1em)
						,span( DeclarationInfoUtil.FMT.format(FiscalUtils.getPeriodEnd(mod)) ).withStyle( marginLeft1em+bold)
						,span(":").withStyle( marginLeft1em)
						,span(DEC2.format( mod.getAmount( Mod130Key.C05 ) )).withStyle( marginLeft1em+bold)
					).withStyle( textCenter+marginTop )
				).render();
			}
		});	
	}
	
	// ----------------------------------------------------------------------------------------------------- [C051]
	private static void initializeC091(AONContext ctx, Mod131 mod) {
		mod.putAmount(Mod131Key.C091, getInitialC091(ctx,mod));
	}
	private static double getInitialC091(AONContext ctx, final Mod131 mod) {
		double c08 = 0.0;
		double c09 = 0.0;
		Mod131 previous = Mod131DAO.getMod131s(ctx, mod.getDomain())
			 .filter(model -> model.getYear() == (mod.getYear() - 1))
			 .filter(model -> model.getPeriod() == Period.T4)
			 .findFirst()
			 .orElse(null);
		if (previous != null) {
			double c01 = previous.getAmount(Mod131Key.C01);
			double rn = AonMathUtils.round(c01 + c08);
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
	
	private static String getC091ComputeKeyInfo(AONContext ctx, Mod131 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod131Key.C091, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				Mod131 previous = Mod131DAO.getMod131s(ctx, mod.getDomain())
						 .filter(model -> model.getYear() == (mod.getYear() - 1))
						 .filter(model -> model.getPeriod() == Period.T4)
						 .findFirst()
						 .orElse(null);
				if (previous == null) {
					return div()
							.with( span( "No se ha encontrado un modelo 131 declarado en el último trimestre del ejercicio anterior:").withStyle( textCenter ) )
							.render();
				} else {
					double c01 = previous.getAmount(Mod131Key.C01);
					DivTag divTag = div() 
						.with( span( "Casilla [003] del modelo 131 declarado en el último trimestre dek ejercicio anterior:").withStyle( textCenter ))
						.with( span(DEC2.format( c01 )).withStyle( marginLeft1em+bold) )
					;
					if (c01 <= 9000) {
						divTag.with( div( " Al ser menor de 9.000 : 100 euros") );
					} else if (c01 > 9000 && c01 <= 10000) {
						divTag.with( div( " Al ser menor de 10.000 y mayor de 9.000 : 75 euros") );
					} else if (c01 > 10000 && c01 <= 11000) {
						divTag.with( div( " Al ser menor de 11.000 y mayor de 10.000 : 50 euros") );
					} else if (c01 > 11000 && c01 <= 12000) {
						divTag.with( div( " Al ser menor de 12.000 y mayor de 11.000 : 25 euros") );
					} else {
						divTag.with( div( " Al ser mayor de 12.000 : 0 euros") );
					}
					return divTag.render();
				}
			}
		});	
	}

	// ----------------------------------------------------------------------------------------------------- [C011]
	private static String getC11ComputeKeyInfo(AONContext ctx, Mod131 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod131Key.C11, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				double c10 = fm.getAmount(Mod131Key.C10);
				if ( AonMathUtils.isLessThanZero( c10 )) {
					return div().withStyle( textCenter )
						.with(span("Al ser la casilla [010] menor cero, resultado:"))
						.with(span(DEC2.format( mod.getAmount(Mod131Key.C11) )).withStyle( marginLeft1em+bold) )
						.render();
				}
				MutableDouble c11Sum = new MutableDouble(0.0);
				MutableDouble c15Sum = new MutableDouble(0.0);
				TableTag tab = 
					table(
						tr (
							td("Modelo").withStyle( border+bold+textUnderline)
							,td("Casilla [011]").withStyle( border+bold+textRight+textUnderline)
							,td("Casilla [015]").withStyle( border+bold+textRight+textUnderline)
						)
					).withStyle( blockCenter );
				MutableBoolean models = new MutableBoolean( false );
				Mod131DAO.getPreviousModels(ctx, mod)
					.forEach( pmod -> {
						models.setValue(true);
						// Cantidades deducidas
						double c11 = pmod.getAmount(Mod131Key.C11);						
						c11Sum.add( c11 );
						// Resultado negativos
						double c15 = pmod.getDeclarationResult();
						if ( AonMathUtils.isLessThanZero(c15) ) {
							c15Sum.add( pmod.getDeclarationResult() );
						}
						tab.with( 
							tr (
								 td( pmod.getModelFullName() ).withStyle( border)
								,td( DEC2.format( c11 ) ).withStyle( border+textRight)
								,td( DEC2.format( c15 ) ).withStyle( border+textRight)
							)
						);
					});
				DivTag div = div().withStyle( textCenter );
				if (!models.getValue().booleanValue()) {
					return div
						.with( span( "No se han encontrado modelos anteriores con resultados negativos") )
						.render();
				}
				div.with(tab);
				div.with( 
					div().withStyle( textCenter )
						.with( span( "Suma de declaraciones (casillas [015]) con resultados negativos: ") )
						.with(span(DEC2.format( c15Sum.doubleValue() )).withStyle( marginLeft1em+bold)));
				
				div.with( 
					div().withStyle( textCenter )
						.with( span( "Suma de cantidades deducidas casillas [011]: ") )
						.with(span(DEC2.format( c11Sum.doubleValue() )).withStyle( marginLeft1em+bold)));
				
				double total = AonMathUtils.round(c15Sum.doubleValue() - c11Sum.doubleValue());
				if (AonMathUtils.isLessThanZero(total)) {
					div.with( 
						div().withStyle( textCenter )
							.with( span( "Cantidad que se podría deducir: ") )
							.with(span(DEC2.format( c15Sum.doubleValue() )).withStyle( marginLeft1em+bold))
							.with( span( " - ").withStyle( marginLeft1em+bold)) 
							.with(span(DEC2.format( c11Sum.doubleValue() )).withStyle( marginLeft1em+bold))
							.with( span( " = ").withStyle( marginLeft1em+bold))
							.with(span(DEC2.format( total )).withStyle( marginLeft1em+bold)));
					// Si el importe "total" es negativo. Quiere decir que hay importes deducibles 
					// por lo que en la presente declaración se podrá deducir.
					total = AonMathUtils.absRounded(total); 
					div.with( 
						div().withStyle( textCenter )
							.with( span( "Cambio de signo: ") )
							.with(span(DEC2.format( total )).withStyle( marginLeft1em+bold)));
				} else {
					div.with( 
						div().withStyle( textCenter )
							.with( span( "La diferencia es positiva: ") )
							.with(span(DEC2.format( c15Sum.doubleValue() )).withStyle( marginLeft1em+bold))
							.with( span( " - ").withStyle( marginLeft1em+bold)) 
							.with(span(DEC2.format( c11Sum.doubleValue() )).withStyle( marginLeft1em+bold))
							.with( span( " = ").withStyle( marginLeft1em+bold))
							.with(span(DEC2.format( total )).withStyle( marginLeft1em+bold)));
					// Si el importe "total" es positivo. Quiere decir que ya no hay nada por deducir.
					total = 0.0;
					div.with( 
						div().withStyle( textCenter )
							.with( span( "No se puede deducir nada: ") )
							.with(span(DEC2.format( total )).withStyle( marginLeft1em+bold)));
				}
				if ( total>c10) {
					div.with( 
						div().withStyle( textCenter )
							.with( span( "Al ser la cantidad deducible (") )
							.with(span(DEC2.format( total )).withStyle( marginLeft1em+bold))
							.with( span( ") mayor que lo indicado en la casilla [010] (").withStyle( marginLeft1em+bold)) 
							.with(span(DEC2.format( c10 )).withStyle( marginLeft1em+bold))
							.with( span( " se asifgna la casilla [010]: ").withStyle( marginLeft1em+bold))
							.with(span(DEC2.format( c10 )).withStyle( marginLeft1em+bold)));
				} else {
					div.with( 
						div().withStyle( textCenter )
							.with( span( "Resultado: ").withStyle( marginLeft1em+bold))
							.with(span(DEC2.format( total )).withStyle( marginLeft1em+bold)));
				}
				return div.render();
			}
		});	
	}
	
	// ----------------------------------------------------------------------------------------------------- [C011]
	private static String getC12ComputeKeyInfo(AONContext ctx, Mod131 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod131Key.C11, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				DivTag div = div();
				double p2 = fm.getAmount(Mod131Key.P2);
				if ( AonMathUtils.isZero( p2 )) {
					return div
						.with(div("\u2022 NO se han destinado cantidades al pago de préstamos por adquisición o rehabilitación de vivienda habitual."))
						.with(div(MessageFormat.format( "\u2022 Resultado: {0}", DEC2.format( mod.getAmount(Mod131Key.C12) )))).withStyle(bold)
					.render();
				}
				
				div.with(
					div("\u2022 SI se han destinado cantidades al pago de préstamos por adquisición o rehabilitación de vivienda habitual."));
				
				double c10 = fm.getAmount(Mod131Key.C10);
				if ( !AonMathUtils.isGreatherThanZero( c10 )) {
					return div
						.with(div("\u2022 Casilla [010] cero o menor que cero."))
						.with(div(MessageFormat.format( "\u2022 Resultado: {0}", DEC2.format( mod.getAmount(Mod131Key.C12) )))).withStyle(bold)
					.render();
					
				}
				
				div.with(div("\u2022 Casilla [010] mayor que cero."));
				
				double c05 = fm.getAmount(Mod131Key.C05);
				double c01 = fm.getAmount(Mod131Key.C01);
				double c03 = fm.getAmount(Mod131Key.C03);
				double c12Temp = 0.0;
				
				if (AonMathUtils.isNotZero(c05) && AonMathUtils.isNotZero(c01 + c03) ) {
					return div
						.with(div("\u2022 El contribuyente realiza simultáneamente actividades agrícolas, ganaderas o forestales y actividades distintas de éstas."))
						.with(div("\u2022 Existe valor en las casillas [005] y [001] o [003]")
						.with(div(MessageFormat.format( "\u2022 Resultado: {0}", DEC2.format( mod.getAmount(Mod131Key.C12) )))).withStyle(bold)
					).render();
				}
				
				if (AonMathUtils.isNotZero(c05) && AonMathUtils.isZero(c01 + c03)) {
					
					div.with(div("\u2022 El contribuyente realiza actividades agrícolas, ganaderas o forestales."));
					
					if (AonMathUtils.isGreatherThan(c05, LIM_C12_2)) {
						return div
							.with(div(MessageFormat.format("\u2022 La deducción no procede. Casilla [005] igual o superior a la cantidad de {0} euros",DEC2.format(LIM_C12_2)))
							.with(div(MessageFormat.format( "\u2022 Resultado: {0}", DEC2.format( mod.getAmount(Mod131Key.C12) )))).withStyle(bold)
						).render();
					}
					if (mod.getPeriod() == Period.T1) {
						double c05Year = AonMathUtils.round(c05 * 4);
						if (AonMathUtils.isGreatherThan(c05Year, LIM_C12_2)) {
							return div
								.with(div(MessageFormat.format( 
									"\u2022 La deducción no procede. Casilla [005] ( {0} ) elevada "
									+ "al año ( {1} ) igual o superior a la cantidad de {2} euros"
									,DEC2.format(c05)
									,DEC2.format(c05Year)
									,DEC2.format(LIM_C12_2))))
								.with(div(MessageFormat.format( "\u2022 Resultado: {0}", DEC2.format( mod.getAmount(Mod131Key.C12) ))).withStyle(bold))
								.render();
						}
					}
					double c05T1 = Mod131DAO.getPreviousModels(ctx, mod)
						.filter( md -> md.getPeriod() == Period.T1)
						.mapToDouble( md -> md.getAmount(Mod131Key.C05))
						.findFirst()
						.orElse(0.0);
					double c05Year = AonMathUtils.round(c05T1 * 4);
					if (AonMathUtils.isGreatherThan(c05Year, LIM_C12_2)) {
						return div
							.with(div(MessageFormat.format(
								"\u2022 La deducción no procede. Casilla [005] del primer trimestre ( {0} ) elevada al año igual o superior a la cantidad de {1} euros"
								,DEC2.format(c05T1)
								,DEC2.format(LIM_C12_2))))
							.with(div(MessageFormat.format( "\u2022 Resultado: {0}", DEC2.format( mod.getAmount(Mod131Key.C12) ))).withStyle(bold))	
							.render();
					}
					
					c12Temp = c05 * 2 / 100;
					div
						.with(div("\u2022 La deducción está constituida por el importe "
							+ "resultante de aplicar el porcentaje del 2 por 100 "
							+ "sobre la cantidad consignada en la casilla [005].")).withStyle( noWrap )
						.with(div(MessageFormat.format("\u2022 Deducción {0}",DEC2.format( c12Temp ))).withStyle(bold))
						.render();
				}
				if (AonMathUtils.isZero(c05) && AonMathUtils.isNotZero(c01 + c03)) {
					div.with(div("\u2022 El contribuyente realiza actividades distintas a las agrícolas, ganaderas o forestales."));
					if (AonMathUtils.isGreatherThan(c05, LIM_C12_2)) {
						return div
							.with(div(MessageFormat.format("\u2022 La deducción no procede. Casilla [005] igual o superior a la cantidad de {0} euros",DEC2.format(LIM_C12_2))))
							.with(div(MessageFormat.format( "\u2022 Resultado: {0}", DEC2.format( mod.getAmount(Mod131Key.C12) ))).withStyle(bold))	
							.render();
					}
					
					if (AonMathUtils.isGreatherThan(c03, LIM_C12_2)) {
						return div
							.with(div(MessageFormat.format("\u2022 La deducción no procede. Casilla [003] igual o superior a la cantidad de {0} euros",DEC2.format(LIM_C12_2)))
							.with(div(MessageFormat.format( "\u2022 Resultado: {0}", DEC2.format( mod.getAmount(Mod131Key.C12) )))).withStyle(bold)
						).render();
					}

					if (mod.getPeriod() == Period.T1) {
						double c03Year = AonMathUtils.round(c03 * 4);
						if (AonMathUtils.isGreatherThan(c03Year, LIM_C12_2)) {
							return div
								.with(div(MessageFormat.format( 
									"\u2022 La deducción no procede. Casilla [003] ( {0} ) elevada "
									+ "al año ( {1} ) igual o superior a la cantidad de {2} euros"
									,DEC2.format(c03)
									,DEC2.format(c03Year)
									,DEC2.format(LIM_C12_2))))
								.with(div(MessageFormat.format( "\u2022 Resultado: {0}", DEC2.format( mod.getAmount(Mod131Key.C12) ))).withStyle(bold))
								.render();
						}
					}
					double c03T1 = Mod131DAO.getPreviousModels(ctx, mod)
						.filter( md -> md.getPeriod() == Period.T1)
						.mapToDouble( md -> md.getAmount(Mod131Key.C03))
						.findFirst()
						.orElse(0.0);
					double c03Year = AonMathUtils.round(c03T1 * 4);
					if (AonMathUtils.isGreatherThan(c03Year, LIM_C12_2)) {
						return div
							.with(div(MessageFormat.format(
								"\u2022 La deducción no procede. Casilla [003] del primer trimestre ( {0} ) elevada al año igual o superior a la cantidad de {1} euros"
								,DEC2.format(c03T1)
								,DEC2.format(LIM_C12_2))))
							.with(div(MessageFormat.format( "\u2022 Resultado: {0}", DEC2.format( mod.getAmount(Mod131Key.C12) ))).withStyle(bold))	
							.render();
					}
					c12Temp = (c01 * 0.5 / 100) + (c03 * 2 / 100);
					
					div
						.with(div("\u2022 La deducción está constituida por la suma de los importes resultantes de aplicar el porcentaje del 0,5 por 100 sobre la cantidad consignada en la casilla [001] y el porcentaje del 2 por 100 sobre la cantidad consignada en la casilla [003]."))
						.with(div(MessageFormat.format("\u2022 Deducción {0}",DEC2.format( c12Temp ))).withStyle(bold))
						.render();
				
					double c11 = fm.getAmount(Mod131Key.C11);
					double c12;
					double positiveDiff = (c10 - c11);
					if ( c12Temp > positiveDiff ) {
						c12 = positiveDiff;
						div
							.with(div(MessageFormat.format(
								"\u2022 El importe de la deducción no podrá ser superior a la diferencia positiva entre las casillas [010] y [011] (dif: {0})."
								,DEC2.format(positiveDiff))))
							.with(div(MessageFormat.format("\u2022 Deducción {0}",DEC2.format( c12))).withStyle(bold))
							.render();
								
					} else {
						c12 = c12Temp;
					}

					
					double c12Sum = Mod131DAO.getPreviousModels(ctx, mod)
						.mapToDouble( md -> md.getAmount(Mod131Key.C12))
						.sum();
					double c12Year = c12 + c12Sum;
					if (c12Year > LIM_C12_1 ) {
						div
							.with(div(MessageFormat.format(
									"\u2022 La suma de cantidades deducidas en la casilla [012] en trimestres anteriores ({0}) )."
									+ " y lo indicado en la casilla [012] {1} supera el límite anual de la deducción {2}"
									,DEC2.format(c12Sum)
									,DEC2.format(c12)
									,DEC2.format(LIM_C12_1)
									)))
							.render();
						c12 = LIM_C12_1 - c12Sum;
						div
							.with(div(MessageFormat.format("\u2022 Deducción posible {0}",DEC2.format( c12 ))).withStyle(bold))
							.render();
						
					}
				}
				return div.render(); 
			}
		});	
	}
	

	Stream<AccountingBreakdown> getAccountInfoInfo(AONContext ctx, Mod131 mod, IModelScript<Mod131Key> script, IMod131KeyDAO keyDAO) {
		if (keyDAO.getKey() == Mod131Key.C03) {
			return getInitialC03Stream(ctx, mod);
		} else if (keyDAO.getKey() == Mod131Key.C05) {
			return getInitialC05Stream(ctx, mod);
		} 
		return Stream.empty();
	}

	// ------------------------------------------------------------ [ASBTRACT]
	IMod131KeyDAO[] getKeys() {
		return Mod131KeyDAO.values();
	}
	@Override
	IMod131KeyDAO getKey(Mod131Key key) {
		return Arrays.stream( getKeys() )
			.filter(k -> k.getKey() == key)
			.findFirst()
			.orElse(null);
	}


	@Override
	Double getResult(Mod131 mod131) {
		return mod131.getAmount(Mod131Key.C15);
	}
	
	private static final double DEFAULT_YEAR_HOURS = 1800;

	static boolean accept(Mod131 mod131) {
		return mod131.isAEAT() && mod131.getYear() > 2022; 
	}
	
	@Override
	Mod131Activity calculateActivity(AONContext ctx, Mod131Activity act) {
		calculateModules(ctx,act);
		calcRendimientoNetoPrevio(ctx,act);
		calcIncentivosAlEmpleo(ctx,act);
		calcRendimientoNetoMinorado(ctx,act);
		calcIndiceCorrectorEspecial(ctx,act);
		calcIndiceCorrectorEmpresaPequenaDimension(ctx,act);
		calcIndiceCorrectorTemporada(ctx,act);
		calcIndiceCorrectorExceso(ctx,act);
		calcIndiceCorrectorNuevaActividad(ctx,act);
		calcRendimientoEfectosPagoFraccionado(ctx,act);
		calcReduccionLorca(ctx,act);
		calcRendimientoEfectosPagoFraccionadoDespuesReduccion(ctx,act);
		calcResultadoPagoTrimestral(ctx,act);
		return act;
	}
	
	private static void calculateModules(AONContext ctx, Mod131Activity act) {
		for (Mod131ActivityModule mod : act.getModules()) {
			if (mod.isSalariedStaff() || AonStringUtils.contains(mod.getDescription(),"PERSONAL ASALARIADO")) {
				calculateSalariedStaff(ctx,mod);
			}
			if (mod.isNoSalariedStaff() || AonStringUtils.contains(mod.getDescription(),"PERSONAL NO ASALARIADO")) {
				calculateNoSalariedStaff(ctx,act,mod);		
			}
			mod.setResult( AonMathUtils.round(mod.getValue() * mod.getFactor() ) );
		}
		
	}

	private static void calculateSalariedStaff(AONContext ctx, Mod131ActivityModule act) {
		double salariedStaff = 0.0;
		// Mayores de 19 años
		double may19Hours = act.getMay19Hours();
		// Menores de 19 años y trabajadores con contratos de aprendizaje o formación, que no sean discapacitados.
		double men19Hours = act.getMen19Hours();
		// Discapacitados con grado de minusvalía igual o superior al 33 por 100
		double disHours = act.getDisHours();
		// Horas anuales
		double yearHours = act.getYearHours();
		
		if (AonMathUtils.isZero(yearHours)) {
			yearHours = DEFAULT_YEAR_HOURS;
		}
		if (AonMathUtils.isNotZero(may19Hours)) {
			salariedStaff = AonMathUtils.round(may19Hours / yearHours);	
		}
		if (AonMathUtils.isNotZero(men19Hours)) {
			salariedStaff = salariedStaff + AonMathUtils.round((men19Hours / yearHours) * 0.60);
		}
		if (AonMathUtils.isNotZero(disHours)) {
			salariedStaff = salariedStaff + AonMathUtils.round((disHours / yearHours) * 0.40);
		}
	}
	
	private static void calculateNoSalariedStaff(AONContext ctx, Mod131Activity act, Mod131ActivityModule mod) {
		double noSalariedStaff = 0.0;
		// Horas anuales del titular.
		double ownerHours = mod.getOwnerHours();
		// Horas anuales del cónyuge.
		double spouseHours = mod.getSpouseHours();
		// Horas anuales de los hijos menores de 18 años.
		double childMen18Hours = mod.getChildMen18Hours();
		// Horas anuales de los hijos menores de 18 años con discapacidad en grado igual o superior al 33%
		double childDisHours = mod.getChildDisHours();
		
		boolean titularFullTime = AonMathUtils.round(ownerHours) >= DEFAULT_YEAR_HOURS;
		boolean moreThanOne = (AonMathUtils.round(spouseHours + childMen18Hours + childDisHours) > DEFAULT_YEAR_HOURS);
		if (AonMathUtils.isNotZero(ownerHours)) {
			noSalariedStaff = (ownerHours>=DEFAULT_YEAR_HOURS?DEFAULT_YEAR_HOURS:ownerHours) / DEFAULT_YEAR_HOURS;
		}
		if (AonMathUtils.isNotZero(spouseHours)) {
			double d = ((spouseHours>=DEFAULT_YEAR_HOURS?DEFAULT_YEAR_HOURS:spouseHours) / DEFAULT_YEAR_HOURS);
			// Indique si el cónyuge es discapacitado en grado igual o superior al 33%
			if ( mod.isSpouseDis() ) {
				d = AonMathUtils.round(d * 0.75);
			}
			if (titularFullTime && !moreThanOne) {
				d = AonMathUtils.round(d / 2);
			}
			noSalariedStaff = noSalariedStaff + d;	
		}
		if (AonMathUtils.isNotZero(childMen18Hours)) {
			double d = AonMathUtils.round(childMen18Hours / 1800);
			if (titularFullTime && !moreThanOne) {
				d = AonMathUtils.round(d/ 2);
			}
			noSalariedStaff = noSalariedStaff + d;	
		}
		if (AonMathUtils.isNotZero(childDisHours)) {
			double d = ((childDisHours / 1800) * 0.75);
			if (titularFullTime && !moreThanOne) {
				d = AonMathUtils.round(d / 2);
			}
			noSalariedStaff = noSalariedStaff + d;	
		}
	}

	// **************************************************************************
	
	private static void calcRendimientoNetoPrevio(AONContext ctx, Mod131Activity act) {
		double rnp= 0.0;
		for (Mod131ActivityModule mod : act.getModules()) {
			rnp = AonMathUtils.round(rnp + mod.getResult());
		}
		act.setRnp(rnp);
	}
	
	private static void calcIncentivosAlEmpleo(AONContext ctx, Mod131Activity act) {
		double salariedStaff = 0.0;
		for (Mod131ActivityModule mod : act.getModules()) {
			String desc = mod.getDescription();
			if (mod.isSalariedStaff() 
				|| AonStringUtils.equals(desc,ModuleInfo.M01.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M15.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M26.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M27.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M56.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M59.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M62.getDescription())
				|| AonStringUtils.equals(desc,ModuleInfo.M16.getDescription())) {
				salariedStaff = AonMathUtils.round(salariedStaff + mod.getValue());
			}
		}

		double coef = 0.0;
		double iem = 0;
		double as = salariedStaff;
//		if (act.getEmp() != 0 && salariedStaff >= act.getEmp() ) {
//			as = AonMathUtils.round(as - act.getEmp());
//			coef = AonMathUtils.round( as * 0.40 );
//		}
		if (AonMathUtils.round(as) > 0 ) {
			coef = coef + ( (as>1?1:as) * 0.10 );	
			as = AonMathUtils.round(as - 1);	
		} 
		if (AonMathUtils.round(as) > 0 ) {
			coef = coef + ( (as>2?2:as) * 0.15 );
			as = AonMathUtils.round(as - 2);
		} 
		if (AonMathUtils.round(as) > 0 ) {
			coef = coef + ( (as>2?2:as) * 0.20 );
			as = AonMathUtils.round(as - 2);
		} 
		if (AonMathUtils.round(as) > 0 ) {
			coef = coef + ( (as>3?3:as) * 0.25 );
			as = AonMathUtils.round(as - 3);
		} 
		if (AonMathUtils.round(as) > 0 ) {
			coef = coef + ( as * 0.30 );
		}
		
		if (coef != 0 ) {
			for (Mod131ActivityModule mod : act.getModules()) {
					String desc = mod.getDescription();
				if (mod.isSalariedStaff() 
					|| AonStringUtils.equals(desc,ModuleInfo.M01.getDescription())
					|| AonStringUtils.equals(desc,ModuleInfo.M15.getDescription())
					|| AonStringUtils.equals(desc,ModuleInfo.M26.getDescription())
					|| AonStringUtils.equals(desc,ModuleInfo.M27.getDescription())
					|| AonStringUtils.equals(desc,ModuleInfo.M56.getDescription())
					|| AonStringUtils.equals(desc,ModuleInfo.M59.getDescription())
					|| AonStringUtils.equals(desc,ModuleInfo.M62.getDescription())
					|| AonStringUtils.equals(desc,ModuleInfo.M16.getDescription())) {
					double m01 = mod.getValue();
					double ratioPersonalAsalariado = (salariedStaff != 0 )?m01 / salariedStaff:1;
					iem = iem + (coef * ratioPersonalAsalariado * mod.getFactor());
				}
			}
			iem = AonMathUtils.round( iem );
		}
		act.setIem(iem);
	}
	
	private static void calcRendimientoNetoMinorado(AONContext ctx, Mod131Activity act) {
		act.setRnm(AonMathUtils.round(act.getRnp() - act.getIem() - act.getIin()));
	}
	
	private static void calcIndiceCorrectorEspecial(AONContext ctx, Mod131Activity act) {
		// N?mero de vehículos afectos de la actividad.
		double veh = act.getVeh();
		// Municipio donde se ejerce la actividad.
		double mun = act.getMun();
		
		
		// Los índices correctores especiales sólo se aplicarán en aquellas 
		// actividades concretas que se citan a continuación:
		double ic1 = 0.0;
		act.setIndiceEmpresasPequenaDimensionAplicable(true); 
		if (Epigraph.E_659_4B.getEpigraph().equals(act.getEpigraph())) {

			// PREGUNTA para diferenciar de Epigraph.E_659_4A, puesto que el epigrafe es el mismo 
			if (act.getModules() != null && act.getModules().size() == 4) {
				
				// Actividad de comercio al por menor de prensa, revistas y libros 
				// en quioscos situados en la v?a p?blica:
				//	Ubicaci?n de los quioscos					?ndice
				//  --------------------------------------------------
				//	Madrid y Barcelona							  1,00
				//	Municipios de m?s de 100.000 habitantes		  0,95
				//	Resto de municipios							  0,80
				if (AonMathUtils.round(mun) == 6.0) {
					ic1 = 1.0;
				} else if (AonMathUtils.round(mun) == 5.0) {
					ic1 = 0.95;
				} else {
					ic1 = 0.80;
				}
			}
		} else if (Epigraph.E_721_1.getEpigraph().equals(act.getEpigraph()) 
				|| Epigraph.E_721_3.getEpigraph().equals(act.getEpigraph())) {
			// Actividad de transporte urbano colectivo y de viajeros por carretera:
			// Se aplicar? el ?ndice 0,80 cuando el titular disponga de un ?nico veh?culo.
			if (AonMathUtils.round(veh) == 1.0) {
				ic1 = 0.80;
				act.setIndiceEmpresasPequenaDimensionAplicable(false);	
			}
		} else if (Epigraph.E_721_2.getEpigraph().equals(act.getEpigraph())) {
			//	Actividad de transporte por autotaxis.
			//		Poblaci?n del municipio				  ?ndice
			//		--------------------------------------------
			//		Hasta 2.000 habitantes					0,75
			//		De 2.001 hasta 10.000 habitantes		0,80
			//		De 10.001 hasta 50.000 habitantes		0,85
			//		De 50.001 hasta 100.000 habitantes		0,90
			//		M?s de 100.000 habitantes				1,00
			act.setIndiceEmpresasPequenaDimensionAplicable(false);
			if (AonMathUtils.round(mun) == 0.0) {
				ic1 = 0.75;
			} else if (AonMathUtils.round(mun) == 1.0 || AonMathUtils.round(mun) == 2.0) {
				ic1 = 0.80;
			} else if (AonMathUtils.round(mun) == 3.0) {
				ic1 = 0.85;
			} else if (AonMathUtils.round(mun) == 4.0) {
				ic1 = 0.90;
			} else {
				ic1 = 1.00;
			}
		} else if (Epigraph.E_722A.getEpigraph().equals(act.getEpigraph())) {
			//Actividades de transporte de mercanc?as por carretera y servicios de mudanzas:
			// Se aplicar? el ?ndice 0,80 cuando el titular disponga de un ?nico veh?culo.
			// Se aplicar? el ?ndice 0,90 cuando la actividad se realice con tractocamiones
			// y el titular carezca de semirremolques. Cuando la actividad se desarrolle con 
			// un ?nico tractocami?n y sin semirremolques, se aplicar?, exclusivamente, el ?ndice 0,75.

			if (AonMathUtils.round(veh) == 1.0) {
				ic1 = 0.80;
				act.setIndiceEmpresasPequenaDimensionAplicable(false);
			}
			// Indique si la actividad se realiza con un ?nico tractocami?n y sin semirremolques.
			if (act.isTss()) {
				ic1 = 0.75;
				act.setIndiceEmpresasPequenaDimensionAplicable(false);
			}
			// Indique si la actividad se realiza con tractocamiones y el titular carece de semirremolques.
			if (act.isTns()) {
				ic1 = 0.90;
				act.setIndiceEmpresasPequenaDimensionAplicable(false);
			}
		} else if (Epigraph.E____.getEpigraph().equals(act.getEpigraph())) {
			// Actividad de producci?n de mejill?n en batea:
			//	- Empresa con una sola batea y sin barco auxiliar: 0,75.
			//	- Empresa con una sola batea y con un barco auxiliar de 
			//	  menos de 15 toneladas de registro bruto (T.R.B.): 0,85.
			//	- Empresa con una sola batea y con un barco auxiliar de 15 
			//	  a 30 T.R.B.; y empresa con dos bateas y sin barco auxiliar: 0,90.
			//	- Empresa con una sola batea y con un barco auxiliar de m?s 
			//	  de 30 T.R.B.; y empresa con dos bateas y un barco auxiliar 
			//    de menos de 15 T.R.B.: 0,95.
			act.setIndiceEmpresasPequenaDimensionAplicable(false);
			// N?mero de bateas y de barcos auxiliares de la empresa.
			double bat = act.getBat();
			if (AonMathUtils.round(bat) == 1.0) {
				ic1 = 0.75;
			} else if (AonMathUtils.round(bat) == 2.0) {
				ic1 = 0.85;
			} else if (AonMathUtils.round(bat) == 3.0 || AonMathUtils.round(bat) == 5.0) {
				ic1 = 0.90;
			} else if (AonMathUtils.round(bat) == 4.0 || AonMathUtils.round(bat) == 6.0) {
				ic1 = 0.95;
			} else {
				// Otros: numero de bateas, barcos o TRB distintos de los anteriores.
				act.setIndiceEmpresasPequenaDimensionAplicable(false);	
			}
		}
		act.setIc1(ic1);
	}
	
	private static void calcIndiceCorrectorEmpresaPequenaDimension(AONContext ctx, Mod131Activity act) {
		double ic2 = 0.0;
		if (act.isIndiceEmpresasPequenaDimensionAplicable() 
			&& AonMathUtils.round(act.getCom()) > 0) {
			act.setIndiceEmpresasPequenaDimensionAplicable(false);
		}
		if (act.isIndiceEmpresasPequenaDimensionAplicable()) {
			// En ning?n caso ser? aplicable el ?ndice corrector para empresas de peque?a 
			// dimensi?n (b.1) a las actividades para las que est?n previstos los ?ndices 
			// correctores especiales enumerados en las letras a.2), a.3), a.4) y a.5).
			if (act.isLoc() && act.getRnm() > 0) {
				if (AonMathUtils.round(act.getVeh()) <= 1.0) {
					if (!act.isCap()) {
						ic2 = 0.7; 
						if (AonMathUtils.round(act.getMun()) == 1.0) {
							ic2 = 0.75;
						} else if (AonMathUtils.round(act.getMun()) >= 2.0) {
							ic2 = 0.80;
						}
						if (act.getEmp() > 0.0 && act.getEmp() <= 2.0) {
							ic2 = 0.90;
						}
						if (act.getEmp() > 2) {
							ic2 = 0.0;
						}
					}
				}
			}
		}
		act.setIc2(ic2);
	}
	
	private static void calcIndiceCorrectorTemporada(AONContext ctx, Mod131Activity act) {
		act.setIc3(0);
		if (AonMathUtils.round(act.getTem()) > 0.0 && AonMathUtils.round(act.getTem()) <=60.0) {
			act.setIc3(1.5);
		} else if (AonMathUtils.round(act.getTem()) > 60.0 && AonMathUtils.round(act.getTem()) <= 120.0) {
			act.setIc3(1.35);
		} else if (AonMathUtils.round(act.getTem()) > 120.0 && AonMathUtils.round(act.getTem()) <= 180.0) {
			act.setIc3(1.25);
		}
	}
	
	private static void calcIndiceCorrectorExceso(AONContext ctx, Mod131Activity act) {
		double ic4 = 0.0;
		if (AonMathUtils.isZero(act.getIc2())) {
			double tope = AonMathUtils.round(act.getMaxImport());
			if (AonMathUtils.isZero(tope)) {
				Epigraph epi = Modules2016.Epigraph.getEpigraph(act.getEpigraph());
				if (epi != null) {
					tope = epi.getLimExceso();
				}
			}
			double baseIndice = act.getRnm();
			if (AonMathUtils.round(act.getIc1()) != 0.0) {
				baseIndice = AonMathUtils.round(baseIndice * act.getIc1());	
			}
			if (AonMathUtils.round(act.getIc2()) != 0.0) {
				baseIndice = AonMathUtils.round(baseIndice * act.getIc2());	
			}
			if (AonMathUtils.round(act.getIc3()) != 0.0) {
				baseIndice = AonMathUtils.round(baseIndice * act.getIc3());	
			}
			if (baseIndice > tope) {
				 ic4 = 1.3;
			}
		}
		act.setIc4(ic4);
	}
	
	private static void calcIndiceCorrectorNuevaActividad(AONContext ctx, Mod131Activity act) {
		act.setIc5(0);
		if (AonMathUtils.isZero(act.getIc3())) {
			if (act.isLoc()) {
				if (AonMathUtils.round(act.getNue()) != 0.0) {
					
					if (!act.isDis()) { 	// No discapacitado
						if (act.getYear() == (int) act.getNue()) { // Primer año
							act.setIc5( 0.80 ); 
						}
						if (( act.getYear() - 1) == (int) act.getNue()) { // Segundo año
							act.setIc5( 0.90 ); 
						}
					} else {	// Discapacitado
						if (act.getYear() == (int) act.getNue()) {	// Primer año
							act.setIc5( 0.60 ); 
						}
						if (( act.getYear() - 1) == (int) act.getNue()) {	// Segundo año
							act.setIc5( 0.70 ); 
						}
					}
//					if (act.getYear() == (int) act.getNue()) {
//						act.setIc5( act.isDis()?0.60:0.70 ); 
//					}
//					// Segundo
//					if (( act.getYear() - 1) == (int) act.getNue()) {
//						act.setIc5( act.isDis()?0.80:0.90);
//					}
				}
			}
		}
	}
	
	private static void calcRendimientoEfectosPagoFraccionado(AONContext ctx, Mod131Activity act) {
		// *****************************************************************
		// 		RENDIMIENTO A EFECTOS DE PAGOS FRACCIONADOS (i.R.P.F.)
		// *****************************************************************
		double rpf = 0.0;
		rpf = act.getRnm();
		if (AonMathUtils.isNotZero(act.getIc1())) {
			rpf = rpf * act.getIc1();	
		}
		if (AonMathUtils.isNotZero(act.getIc2())) {
			rpf = rpf * act.getIc2();	
		}
		if (AonMathUtils.isNotZero(act.getIc3())) {
			rpf = rpf * act.getIc3();	
		}
		if (AonMathUtils.isNotZero(act.getIc4())) {
			double baseExceso = rpf - act.getMaxImport();
			baseExceso = baseExceso * act.getIc4(); 
			rpf = baseExceso + act.getMaxImport();	
		}
		if (AonMathUtils.isNotZero(act.getIc5())) {
			rpf = rpf * act.getIc5();	
		}
		
		// Disposición adicional primera. Reducción en 2024 del rendimiento neto calculado por el método de estimación objetiva. 
		//		1. Los contribuyentes que determinen el rendimiento neto de sus actividades
		//		económicas por el método de estimación objetiva, podrán reducir el rendimiento neto de
		//		módulos obtenido en 2024 en un 5 por ciento.
		rpf = rpf - (rpf * 5 / 100);
		
		
		// Comunidad, Sociedad Civil o Similar. Porcentaje de participaci?n.
		if (AonMathUtils.isNotZero(act.getCom())) {
			rpf = (rpf * act.getCom() / 100);
			
		}
		rpf = AonMathUtils.round(rpf);
		act.setRpf(rpf);
	}

	private static void calcReduccionLorca(AONContext ctx, Mod131Activity act) {
		// *****************************************************************
		// Reducci?n para actividades econ?micas realizadas en el t?rmino municipal de Lorca
		// *****************************************************************
		
		// Los contribuyentes del Impuesto sobre la Renta de las Personas F?sicas que
		// desarrollen actividades econ?micas incluidas en el anexo II de esta Orden en el t?rmino
		// municipal de Lorca y determinen el rendimiento neto por el m?todo de estimaci?n objetiva,
		// podr?n reducir el rendimiento neto de m?dulos de 2013 correspondiente a tales
		// actividades en un 20 por ciento.
		double rlo = 0.0;
		if (AonMathUtils.isNotZero(act.getLor())) {
			rlo = AonMathUtils.round(act.getRpf() * 20 / 100); 	
		}
		act.setRlo(rlo);		
	}
	
	private static void calcRendimientoEfectosPagoFraccionadoDespuesReduccion(AONContext ctx, Mod131Activity act) {
		act.setRdr(AonMathUtils.round(act.getRpf() - act.getRlo()));
	}
	
	private static void calcResultadoPagoTrimestral(AONContext ctx, Mod131Activity act) {
		double salariedStaff = act.getEmp();
		if (AonMathUtils.isZero(salariedStaff)) {
			for (Mod131ActivityModule mod : act.getModules()) {
				String desc = mod.getDescription();
				if (mod.isSalariedStaff() 
					|| AonStringUtils.equals(desc,ModuleInfo.M01.getDescription())
					|| AonStringUtils.equals(desc,ModuleInfo.M15.getDescription())
					|| AonStringUtils.equals(desc,ModuleInfo.M26.getDescription())
					|| AonStringUtils.equals(desc,ModuleInfo.M27.getDescription())
					|| AonStringUtils.equals(desc,ModuleInfo.M56.getDescription())
					|| AonStringUtils.equals(desc,ModuleInfo.M59.getDescription())
					|| AonStringUtils.equals(desc,ModuleInfo.M62.getDescription())
					|| AonStringUtils.equals(desc,ModuleInfo.M16.getDescription())) {
					salariedStaff = AonMathUtils.round(salariedStaff + mod.getValue());
				}
			}
		}
		
		int periodDays = (int) AonDateUtils.getDaysBetweenDates(
				 FiscalUtils.getPeriodStart(act.getYear(),act.getPeriod())
				,FiscalUtils.getPeriodEnd(act.getYear(),act.getPeriod()));
		periodDays = periodDays + 1;
		
		double daysFactor = 1;
		if (act.getDia() < periodDays ) {
			daysFactor = ((double)act.getDia()) / ((double) periodDays);
		}
		double net = AonMathUtils.round(act.getRdr() * daysFactor);
		act.setNet(net);
		
		double por = 4.0;
		if (AonMathUtils.round(salariedStaff) <= 1.0) por = 3.0; 
		if (AonMathUtils.round(salariedStaff) == 0.0) por = 2.0;
		
		if (act.getPrc() > por) {
			por = act.getPrc();
		}
		if ( net < 0) {
			por = 0.0;
		}
		act.setPor(por);
		
		double res = AonMathUtils.round(net *  por / 100 );
		act.setRes(res);
	}

}
