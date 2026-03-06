package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod421;

import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.DEC2;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.border;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.fontLarger;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.paddingLeft;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.styledTag;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.textCenter;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.textRight;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.width150;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.width500;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.fiscal.Mod421Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod421ActivityModule;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.modules.IEpigraphCanarias;
import com.esferalia.aon.occam.api.model.fiscal.modules.ModulesCanarias2026;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod421Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.ExplainRowManager;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonObjectUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

class Mod421ATC2026Declaration extends Mod421ATC {
	
	@FunctionalInterface
	private interface ISimplifiedRegimeActivityFiller {
		void fill(Mod421 mod);
	}

	@FunctionalInterface
	private interface ISimplifiedRegimeActivityPopulator {
		void populate(Mod421 mod);
	}

	@FunctionalInterface
	private interface ISimplifiedRegimeCopier {
		void copy(Mod421 prev, Mod421 current);
	}

	protected Mod421ATC2026Declaration() {

	}
	
	public static boolean accept(Mod421 mod) {
		return (mod.isCanarias() && mod.getYear() >= 2026);
	}
	
	private static final Mod421Key[] COMPENSATION_EXPLAIN_KEYS = new Mod421Key[] { Mod421Key.C17 };
	private static final Mod421Key[] SAME_PERIOD_EXPLAIN_KEYS = new Mod421Key[] { Mod421Key.C18 };
//	private static final Mod421Key[] INGRESO_CUENTA_ANTERIOR_EXPLAIN_KEYS = new Mod421Key[] { Mod421Key.C10T1, Mod421Key.C10T2, Mod421Key.C10T3 };
	private static final Mod421Key[] INGRESO_CUENTA_ANTERIOR_EXPLAIN_KEYS = new Mod421Key[] { Mod421Key.C10 };
	
	private enum Mod421KeyDAO implements IMod421KeyDAO {

		 X02(Mod421Key.X02) // Código Municipio
		,X03(Mod421Key.X03) // Concurso de acreedores
		
		// (1) Actividades en régimen simplificado. Epigrafe IAE
		,A1EP1(Mod421Key.A1EP1, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A1EP1, ensureActivity(mod, 0).getEpigraph())
			,mod -> ensureActivity(mod, 0).setEpigraph(mod.getDescription(Mod421Key.A1EP1))
			,(prev,cur) -> copyKey(prev, cur, Mod421Key.A1EP1))
		// (1) Actividades en régimen simplificado. Epigrafe IAE - Descripción
		,A1EPD(Mod421Key.A1EPD, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A1EPD, ensureActivity(mod, 0).getDescription())
			,mod -> ensureActivity(mod, 0).setDescription(mod.getDescription(Mod421Key.A1EPD))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1EPD))
		// (1) Actividades en régimen simplificado. Epigrafe IAE - Indicador auxiliar de actividad en el caso de epígrafes 691.9 y 722
		,A1EP2(Mod421Key.A1EP2, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A1EP2, ensureActivity(mod, 0).getSpecialEpigraph())
			,mod -> ensureActivity(mod, 0).setSpecialEpigraph(checkSpecialEpigraph(mod, Mod421Key.A1EP2, Mod421Key.A1EP1, Mod421Key.A1EPD))
		    ,(prev,cur) -> copyKey(prev, cur, Mod421Key.A1EP2))
		// (1) Actividades en régimen simplificado. Actividad de Temporada. Nº Días en los que se ejerció la actividad en el año anterior
		,A1TEM(Mod421Key.A1TEM, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A1TEM, ensureActivity(mod, 0).getTem())
			,mod -> ensureActivity(mod, 0).setTem((int) mod.getAmount(Mod421Key.A1TEM))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1TEM))
		// (1) Actividades en régimen simplificado. Número de días de ejercicio de la actividad en el trimestre
		,A1DIA(Mod421Key.A1DIA, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A1DIA, ensureActivity(mod, 0).getDia())
			,mod -> ensureActivity(mod, 0).setDia((int) mod.getAmount(Mod421Key.A1DIA))
			,(prev,cur) -> ensureActivityDays(prev,cur, Mod421Key.A1EP1, Mod421Key.A1DIA))
		
		,A1M1D(Mod421Key.A1M1D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A1M1D, ensureModule(mod, 0, 0).getDescription())
			,mod -> ensureModule(mod, 0, 0).setDescription(mod.getDescription(Mod421Key.A1M1D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M1D))
		,A1M1I(Mod421Key.A1M1I, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A1M1I, ensureModule(mod, 0, 0).getValue())
			,mod -> ensureModule(mod, 0, 0).setValue(mod.getAmount(Mod421Key.A1M1I))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M1I, Mod421Key.A1EP1))
		,A1M1U(Mod421Key.A1M1U, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A1M1U, ensureModule(mod, 0, 0).getUnit())
			,mod -> ensureModule(mod, 0, 0).setUnit(mod.getDescription(Mod421Key.A1M1U))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M1U))
		,A1M1F(Mod421Key.A1M1F, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A1M1F, ensureModule(mod, 0, 0).getFactor())
			,mod -> ensureModule(mod, 0, 0).setFactor(mod.getAmount(Mod421Key.A1M1F))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M1F))
		,A1M1R(Mod421Key.A1M1R, null, null, null
			,"calculateResult(0,A1M1I,A1M1F)"
			, null
			,mod -> mod.putAmount(Mod421Key.A1M1R, ensureModule(mod, 0, 0).getResult())
			,mod -> ensureModule(mod, 0, 0).setResult(mod.getAmount(Mod421Key.A1M1R))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M1R))
		
		,A1M2D(Mod421Key.A1M2D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A1M2D, ensureModule(mod, 0, 1).getDescription())
			,mod -> ensureModule(mod, 0, 1).setDescription(mod.getDescription(Mod421Key.A1M2D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M2D))
		,A1M2I(Mod421Key.A1M2I, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A1M2I, ensureModule(mod, 0, 1).getValue())
			,mod -> ensureModule(mod, 0, 1).setValue(mod.getAmount(Mod421Key.A1M2I))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M2I))
		,A1M2U(Mod421Key.A1M2U, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A1M2U, ensureModule(mod, 0, 1).getUnit())
			,mod -> ensureModule(mod, 0, 1).setUnit(mod.getDescription(Mod421Key.A1M2U))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M2U))
		,A1M2F(Mod421Key.A1M2F, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A1M2F, ensureModule(mod, 0, 1).getFactor())
			,mod -> ensureModule(mod, 0, 1).setFactor(mod.getAmount(Mod421Key.A1M2F))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M2F))
		,A1M2R(Mod421Key.A1M2R, null, null, null
			,"calculateResult(0,A1M2I,A1M2F)"
			, null
			,mod -> mod.putAmount(Mod421Key.A1M2R, ensureModule(mod, 0, 1).getResult())
			,mod -> ensureModule(mod, 0, 1).setResult(mod.getAmount(Mod421Key.A1M2R))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M2R))
		
		,A1M3D(Mod421Key.A1M3D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A1M3D, ensureModule(mod, 0, 2).getDescription())
			,mod -> ensureModule(mod, 0, 2).setDescription(mod.getDescription(Mod421Key.A1M3D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M3D))
		,A1M3I(Mod421Key.A1M3I, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A1M3I, ensureModule(mod, 0, 2).getValue())
			,mod -> ensureModule(mod, 0, 2).setValue(mod.getAmount(Mod421Key.A1M3I))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M3I))
		,A1M3U(Mod421Key.A1M3U, null, null, null, null, null
				,mod -> mod.putDescription(Mod421Key.A1M3U, ensureModule(mod, 0, 2).getUnit())
			,mod -> ensureModule(mod, 0, 2).setUnit(mod.getDescription(Mod421Key.A1M3U))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M3U))
		,A1M3F(Mod421Key.A1M3F, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A1M3F, ensureModule(mod, 0, 2).getFactor())
				,mod -> ensureModule(mod, 0, 2).setFactor(mod.getAmount(Mod421Key.A1M3F))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M3F))
		,A1M3R(Mod421Key.A1M3R, null, null, null
				,"calculateResult(0,A1M3I,A1M3F)"
				, null
				,mod -> mod.putAmount(Mod421Key.A1M3R, ensureModule(mod, 0, 2).getResult())
				,mod -> ensureModule(mod, 0, 2).setResult(mod.getAmount(Mod421Key.A1M3R))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M3R))
		
		,A1M4D(Mod421Key.A1M4D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A1M4D, ensureModule(mod, 0, 3).getDescription())
			,mod -> ensureModule(mod, 0, 3).setDescription(mod.getDescription(Mod421Key.A1M4D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M4D))
		,A1M4I(Mod421Key.A1M4I, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A1M4I, ensureModule(mod, 0, 3).getValue())
			,mod -> ensureModule(mod, 0, 3).setValue(mod.getAmount(Mod421Key.A1M4I))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M4I))
		,A1M4U(Mod421Key.A1M4U, null, null, null, null, null
				,mod -> mod.putDescription(Mod421Key.A1M4U, ensureModule(mod, 0, 3).getUnit())
				,mod -> ensureModule(mod, 0, 3).setUnit(mod.getDescription(Mod421Key.A1M4U))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M4U))
		,A1M4F(Mod421Key.A1M4F, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A1M4F, ensureModule(mod, 0, 3).getFactor())
				,mod -> ensureModule(mod, 0, 3).setFactor(mod.getAmount(Mod421Key.A1M4F))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M4F))
		,A1M4R(Mod421Key.A1M4R, null, null, null
				,"calculateResult(0,A1M4I,A1M4F)"
				, null
				,mod -> mod.putAmount(Mod421Key.A1M4R, ensureModule(mod, 0, 3).getResult())
				,mod -> ensureModule(mod, 0, 3).setResult(mod.getAmount(Mod421Key.A1M4R))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M4R))
		
		,A1M5D(Mod421Key.A1M5D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A1M5D, ensureModule(mod, 0, 4).getDescription())
			,mod -> ensureModule(mod, 0, 4).setDescription(mod.getDescription(Mod421Key.A1M5D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M5D))
		,A1M5I(Mod421Key.A1M5I, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A1M5I, ensureModule(mod, 0, 4).getValue())
				,mod -> ensureModule(mod, 0, 4).setValue(mod.getAmount(Mod421Key.A1M5I))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M5I))
		,A1M5U(Mod421Key.A1M5U, null, null, null, null, null
				,mod -> mod.putDescription(Mod421Key.A1M5U, ensureModule(mod, 0, 4).getUnit())
				,mod -> ensureModule(mod, 0, 4).setUnit(mod.getDescription(Mod421Key.A1M5U))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M5U))
		,A1M5F(Mod421Key.A1M5F, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A1M5F, ensureModule(mod, 0, 4).getFactor())
				,mod -> ensureModule(mod, 0, 4).setFactor(mod.getAmount(Mod421Key.A1M5F))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M5F))
		,A1M5R(Mod421Key.A1M5R, null, null, null
				,"calculateResult(0,A1M5I,A1M5F)"
				, null
				,mod -> mod.putAmount(Mod421Key.A1M5R, ensureModule(mod, 0, 4).getResult())
				,mod -> ensureModule(mod, 0, 4).setResult(mod.getAmount(Mod421Key.A1M5R))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M5R))
		
		,A1M6D(Mod421Key.A1M6D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A1M6D, ensureModule(mod, 0, 5).getDescription())
			,mod -> ensureModule(mod, 0, 5).setDescription(mod.getDescription(Mod421Key.A1M6D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M6D))
		,A1M6I(Mod421Key.A1M6I, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A1M6I, ensureModule(mod, 0, 5).getValue())
			,mod -> ensureModule(mod, 0, 5).setValue(mod.getAmount(Mod421Key.A1M6I))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M6I))
		,A1M6U(Mod421Key.A1M6U, null, null, null, null, null
				,mod -> mod.putDescription(Mod421Key.A1M6U, ensureModule(mod, 0, 5).getUnit())
				,mod -> ensureModule(mod, 0, 5).setUnit(mod.getDescription(Mod421Key.A1M6U))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M6U))
		,A1M6F(Mod421Key.A1M6F, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A1M6F, ensureModule(mod, 0, 5).getFactor())
				,mod -> ensureModule(mod, 0, 5).setFactor(mod.getAmount(Mod421Key.A1M6F))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M6F))
		,A1M6R(Mod421Key.A1M6R, null, null, null
				,"calculateResult(0,A1M6I,A1M6F)"
				, null
				,mod -> mod.putAmount(Mod421Key.A1M6R, ensureModule(mod, 0, 5).getResult())
				,mod -> ensureModule(mod, 0, 5).setResult(mod.getAmount(Mod421Key.A1M6R))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M6R))
		
		,A1M7D(Mod421Key.A1M7D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A1M7D, ensureModule(mod, 0, 6).getDescription())
			,mod -> ensureModule(mod, 0, 6).setDescription(mod.getDescription(Mod421Key.A1M7D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M7D))
		,A1M7I(Mod421Key.A1M7I, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A1M7I, ensureModule(mod, 0, 6).getValue())
				,mod -> ensureModule(mod, 0, 6).setValue(mod.getAmount(Mod421Key.A1M7I))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M7I))
		,A1M7U(Mod421Key.A1M7U, null, null, null, null, null
				,mod -> mod.putDescription(Mod421Key.A1M7U, ensureModule(mod, 0, 6).getUnit())
				,mod -> ensureModule(mod, 0, 6).setUnit(mod.getDescription(Mod421Key.A1M7U))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M7U))
		,A1M7F(Mod421Key.A1M7F, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A1M7F, ensureModule(mod, 0, 6).getFactor())
				,mod -> ensureModule(mod, 0, 6).setFactor(mod.getAmount(Mod421Key.A1M7F))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M7F))
		,A1M7R(Mod421Key.A1M7R, null, null, null
				,"calculateResult(0,A1M7I,A1M7F)"
				, null
				,mod -> mod.putAmount(Mod421Key.A1M7R, ensureModule(mod, 0, 6).getResult())
				,mod -> ensureModule(mod, 0, 6).setResult(mod.getAmount(Mod421Key.A1M7R))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A1M7R))
		
		// (1) Actividades en régimen simplificado. C Cuota devengada operaciones corrientes
		,A1DEV(Mod421Key.A1DEV, null, null, null, "round(A1M1R+A1M2R+A1M3R+A1M4R+A1M5R+A1M6R+A1M7R)", null
			,mod -> mod.putAmount(Mod421Key.A1DEV, ensureActivity(mod, 0).getDev())
			,mod -> ensureActivity(mod, 0).setDev(mod.getAmount(Mod421Key.A1DEV))
			,null)		
		// (1) Actividades en régimen simplificado. Indice corrector actividades de temporada
		,A1ICT(Mod421Key.A1ICT, null, null, null, "calculateIndiceTemporada(0, A1TEM)", null
			,mod -> mod.putAmount(Mod421Key.A1ICT, ensureActivity(mod, 0).getIct())
			,mod -> ensureActivity(mod, 0).setIct(mod.getAmount(Mod421Key.A1ICT))
			,null)
		// (1) Actividades en régimen simplificado. Porcentaje de ingreso a cuenta (1T, 2T, 3T)
		,A1POR(Mod421Key.A1POR, null, null, null
			,"calculatePorcentajeIngresoCuenta(0)", null
			,mod -> mod.putAmount(Mod421Key.A1POR, ensureActivity(mod, 0).getPor())
			,mod -> ensureActivity(mod, 0).setPor(mod.isLastPeriod() ? 0.0 : mod.getAmount(Mod421Key.A1POR))
			,null)
		// (1) Actividades en régimen simplificado. Ingreso a cuenta (1T, 2T, 3T)
		,A1ING(Mod421Key.A1ING, null, null, null
			,"calculateIngresoCuenta(0, A1TEM, A1DIA, A1ICT, A1POR)", null
			,mod -> mod.putAmount(Mod421Key.A1ING, ensureActivity(mod, 0).getIng())
			,mod -> ensureActivity(mod, 0).setIng(mod.isLastPeriod() ? 0.0 : mod.getAmount(Mod421Key.A1ING))
			,null)
		// (1) Actividades en régimen simplificado. 1% de la cuota devengada por operaciones corrientes (4T)
		,A1SO1(Mod421Key.A1SO1, null, null, null, "isLastPeriod()?round(A1DEV * 1 / 100):0.0", null
			,mod -> mod.putAmount(Mod421Key.A1SO1, ensureActivity(mod, 0).getSop1())
			,mod -> ensureActivity(mod, 0).setSop1(mod.getAmount(Mod421Key.A1SO1))
			,null)
		// (1) Actividades en régimen simplificado. Resto de cuotas soportadas (4T)
		,A1SOR(Mod421Key.A1SOR, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A1SOR, ensureActivity(mod, 0).getSopR())
			,mod -> ensureActivity(mod, 0).setSopR(mod.getAmount(Mod421Key.A1SOR))
			,null)
		// (1) Actividades en régimen simplificado. Cuotas soportadas operaciones corrientes (4T)
		,A1SOP(Mod421Key.A1SOP, null, null, null, "isLastPeriod()?(A1SO1+A1SOR):0.0", null
			,mod -> mod.putAmount(Mod421Key.A1SOP, ensureActivity(mod, 0).getSop())
			,mod -> ensureActivity(mod, 0).setSop(mod.getAmount(Mod421Key.A1SOP))
			,null)
		// (1) Actividades en régimen simplificado. RESULTADO 
		,A1RES(Mod421Key.A1RES, null, null, null, "calculateResultadoAnual( A1DEV, A1SOP, A1ICT)",null
			,mod -> mod.putAmount(Mod421Key.A1RES, ensureActivity(mod, 0).getRes())
			,mod -> ensureActivity(mod, 0).setRes(mod.getAmount(Mod421Key.A1RES))
			,null)
		// (1) Actividades en régimen simplificado. Porcentaje cuota mínima (4T)
		,A1PCM(Mod421Key.A1PCM, null, null, null, "isLastPeriod()?A1PCM:0.0", null
			,mod -> mod.putAmount(Mod421Key.A1PCM, ensureActivity(mod, 0).getPcm())
			,mod -> ensureActivity(mod, 0).setPcm(mod.getAmount(Mod421Key.A1PCM))
			,null)
		// (1) Actividades en régimen simplificado. Cuota mínima (4T)
		,A1CMN(Mod421Key.A1CMN, null, null, null, "calculateCuotaMinima(A1DEV,A1PCM,A1ICT)",null
			,mod -> mod.putAmount(Mod421Key.A1CMN, ensureActivity(mod, 0).getCmn())
			,mod -> ensureActivity(mod, 0).setCmn(mod.getAmount(Mod421Key.A1CMN))
			,null)
		// (1) Actividades en régimen simplificado. Cuota anual derivada régimen simplificado (4T)
		,A1CAD(Mod421Key.A1CAD, null, null, null, "isLastPeriod()?((A1CMN>A1RES)?A1CMN:A1RES):0.0", null
			,mod -> mod.putAmount(Mod421Key.A1CAD, ensureActivity(mod, 0).getCad())
			,mod -> ensureActivity(mod, 0).setCad(mod.getAmount(Mod421Key.A1CAD))
			,null)
		
// ACTIVIDAD 2 -------------------------------------------------------------------------------------------------------------------------
		
		// (2) Actividades en régimen simplificado. Epigrafe IAE
		,A2EP1(Mod421Key.A2EP1, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A2EP1, ensureActivity(mod, 1).getEpigraph())
			,mod -> ensureActivity(mod, 1).setEpigraph(mod.getDescription(Mod421Key.A2EP1))
			,(prev,cur) -> copyKey(prev, cur, Mod421Key.A2EP1))
		// (2) Actividades en régimen simplificado. Epigrafe IAE - Descripción
		,A2EPD(Mod421Key.A2EPD, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A2EPD, ensureActivity(mod, 1).getDescription())
			,mod -> ensureActivity(mod, 1).setDescription(mod.getDescription(Mod421Key.A2EPD))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2EPD))
		// (2) Actividades en régimen simplificado. Epigrafe IAE - Indicador auxiliar de actividad en el caso de epígrafes 691.9 y 722
		,A2EP2(Mod421Key.A2EP2, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A2EP2, ensureActivity(mod, 1).getSpecialEpigraph())
			,mod -> ensureActivity(mod, 1).setSpecialEpigraph(checkSpecialEpigraph(mod, Mod421Key.A2EP2, Mod421Key.A2EP1, Mod421Key.A2EPD))
		    ,(prev,cur) -> copyKey(prev, cur, Mod421Key.A2EP2))
		// (2) Actividades en régimen simplificado. Actividad de Temporada. Nº Días en los que se ejerció la actividad en el año anterior
		,A2TEM(Mod421Key.A2TEM, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A2TEM, ensureActivity(mod, 1).getTem())
			,mod -> ensureActivity(mod, 1).setTem((int) mod.getAmount(Mod421Key.A2TEM))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2TEM))
		// (2) Actividades en régimen simplificado. Número de días de ejercicio de la actividad en el trimestre
		,A2DIA(Mod421Key.A2DIA, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A2DIA, ensureActivity(mod, 1).getDia())
			,mod -> ensureActivity(mod, 1).setDia((int) mod.getAmount(Mod421Key.A2DIA))
			,(prev,cur) -> ensureActivityDays(prev,cur, Mod421Key.A2EP1, Mod421Key.A2DIA))

		,A2M1D(Mod421Key.A2M1D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A2M1D, ensureModule(mod, 1, 0).getDescription())
			,mod -> ensureModule(mod, 1, 0).setDescription(mod.getDescription(Mod421Key.A2M1D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M1D))
		,A2M1I(Mod421Key.A2M1I, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A2M1I, ensureModule(mod, 1, 0).getValue())
			,mod -> ensureModule(mod, 1, 0).setValue(mod.getAmount(Mod421Key.A2M1I))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M1I, Mod421Key.A2EP1))
		,A2M1U(Mod421Key.A2M1U, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A2M1U, ensureModule(mod, 1, 0).getUnit())
			,mod -> ensureModule(mod, 1, 0).setUnit(mod.getDescription(Mod421Key.A2M1U))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M1U))
		,A2M1F(Mod421Key.A2M1F, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A2M1F, ensureModule(mod, 1, 0).getFactor())
			,mod -> ensureModule(mod, 1, 0).setFactor(mod.getAmount(Mod421Key.A2M1F))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M1F))
		,A2M1R(Mod421Key.A2M1R, null, null, null
			,"calculateResult(1,A2M1I,A2M1F)"
			, null
			,mod -> mod.putAmount(Mod421Key.A2M1R, ensureModule(mod, 1, 0).getResult())
			,mod -> ensureModule(mod, 1, 0).setResult(mod.getAmount(Mod421Key.A2M1R))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M1R))
		
		,A2M2D(Mod421Key.A2M2D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A2M2D, ensureModule(mod, 1, 1).getDescription())
			,mod -> ensureModule(mod, 1, 1).setDescription(mod.getDescription(Mod421Key.A2M2D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M2D))
		,A2M2I(Mod421Key.A2M2I, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A2M2I, ensureModule(mod, 1, 1).getValue())
			,mod -> ensureModule(mod, 1, 1).setValue(mod.getAmount(Mod421Key.A2M2I))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M2I))
		,A2M2U(Mod421Key.A2M2U, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A2M2U, ensureModule(mod, 1, 1).getUnit())
			,mod -> ensureModule(mod, 1, 1).setUnit(mod.getDescription(Mod421Key.A2M2U))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M2U))
		,A2M2F(Mod421Key.A2M2F, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A2M2F, ensureModule(mod, 1, 1).getFactor())
			,mod -> ensureModule(mod, 1, 1).setFactor(mod.getAmount(Mod421Key.A2M2F))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M2F))
		,A2M2R(Mod421Key.A2M2R, null, null, null
			,"calculateResult(1,A2M2I,A2M2F)"
			, null
			,mod -> mod.putAmount(Mod421Key.A2M2R, ensureModule(mod, 1, 1).getResult())
			,mod -> ensureModule(mod, 1, 1).setResult(mod.getAmount(Mod421Key.A2M2R))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M2R))
		
		,A2M3D(Mod421Key.A2M3D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A2M3D, ensureModule(mod, 1, 2).getDescription())
			,mod -> ensureModule(mod, 1, 2).setDescription(mod.getDescription(Mod421Key.A2M3D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M3D))
		,A2M3I(Mod421Key.A2M3I, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A2M3I, ensureModule(mod, 1, 2).getValue())
			,mod -> ensureModule(mod, 1, 2).setValue(mod.getAmount(Mod421Key.A2M3I))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M3I))
		,A2M3U(Mod421Key.A2M3U, null, null, null, null, null
				,mod -> mod.putDescription(Mod421Key.A2M3U, ensureModule(mod, 1, 2).getUnit())
			,mod -> ensureModule(mod, 1, 2).setUnit(mod.getDescription(Mod421Key.A2M3U))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M3U))
		,A2M3F(Mod421Key.A2M3F, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A2M3F, ensureModule(mod, 1, 2).getFactor())
				,mod -> ensureModule(mod, 1, 2).setFactor(mod.getAmount(Mod421Key.A2M3F))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M3F))
		,A2M3R(Mod421Key.A2M3R, null, null, null
				,"calculateResult(1,A2M3I,A2M3F)"
				, null
				,mod -> mod.putAmount(Mod421Key.A2M3R, ensureModule(mod, 1, 2).getResult())
				,mod -> ensureModule(mod, 1, 2).setResult(mod.getAmount(Mod421Key.A2M3R))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M3R))
		
		,A2M4D(Mod421Key.A2M4D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A2M4D, ensureModule(mod, 1, 3).getDescription())
			,mod -> ensureModule(mod, 1, 3).setDescription(mod.getDescription(Mod421Key.A2M4D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M4D))
		,A2M4I(Mod421Key.A2M4I, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A2M4I, ensureModule(mod, 1, 3).getValue())
			,mod -> ensureModule(mod, 1, 3).setValue(mod.getAmount(Mod421Key.A2M4I))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M4I))
		,A2M4U(Mod421Key.A2M4U, null, null, null, null, null
				,mod -> mod.putDescription(Mod421Key.A2M4U, ensureModule(mod, 1, 3).getUnit())
				,mod -> ensureModule(mod, 1, 3).setUnit(mod.getDescription(Mod421Key.A2M4U))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M4U))
		,A2M4F(Mod421Key.A2M4F, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A2M4F, ensureModule(mod, 1, 3).getFactor())
				,mod -> ensureModule(mod, 1, 3).setFactor(mod.getAmount(Mod421Key.A2M4F))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M4F))
		,A2M4R(Mod421Key.A2M4R, null, null, null
				,"calculateResult(1,A2M4I,A2M4F)"
				, null
				,mod -> mod.putAmount(Mod421Key.A2M4R, ensureModule(mod, 1, 3).getResult())
				,mod -> ensureModule(mod, 1, 3).setResult(mod.getAmount(Mod421Key.A2M4R))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M4R))
		
		,A2M5D(Mod421Key.A2M5D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A2M5D, ensureModule(mod, 1, 4).getDescription())
			,mod -> ensureModule(mod, 1, 4).setDescription(mod.getDescription(Mod421Key.A2M5D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M5D))
		,A2M5I(Mod421Key.A2M5I, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A2M5I, ensureModule(mod, 1, 4).getValue())
				,mod -> ensureModule(mod, 1, 4).setValue(mod.getAmount(Mod421Key.A2M5I))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M5I))
		,A2M5U(Mod421Key.A2M5U, null, null, null, null, null
				,mod -> mod.putDescription(Mod421Key.A2M5U, ensureModule(mod, 1, 4).getUnit())
				,mod -> ensureModule(mod, 1, 4).setUnit(mod.getDescription(Mod421Key.A2M5U))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M5U))
		,A2M5F(Mod421Key.A2M5F, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A2M5F, ensureModule(mod, 1, 4).getFactor())
				,mod -> ensureModule(mod, 1, 4).setFactor(mod.getAmount(Mod421Key.A2M5F))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M5F))
		,A2M5R(Mod421Key.A2M5R, null, null, null
				,"calculateResult(1,A2M5I,A2M5F)"
				, null
				,mod -> mod.putAmount(Mod421Key.A2M5R, ensureModule(mod, 1, 4).getResult())
				,mod -> ensureModule(mod, 1, 4).setResult(mod.getAmount(Mod421Key.A2M5R))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M5R))
		
		,A2M6D(Mod421Key.A2M6D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A2M6D, ensureModule(mod, 1, 5).getDescription())
			,mod -> ensureModule(mod, 1, 5).setDescription(mod.getDescription(Mod421Key.A2M6D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M6D))
		,A2M6I(Mod421Key.A2M6I, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A2M6I, ensureModule(mod, 1, 5).getValue())
			,mod -> ensureModule(mod, 1, 5).setValue(mod.getAmount(Mod421Key.A2M6I))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M6I))
		,A2M6U(Mod421Key.A2M6U, null, null, null, null, null
				,mod -> mod.putDescription(Mod421Key.A2M6U, ensureModule(mod, 1, 5).getUnit())
				,mod -> ensureModule(mod, 1, 5).setUnit(mod.getDescription(Mod421Key.A2M6U))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M6U))
		,A2M6F(Mod421Key.A2M6F, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A2M6F, ensureModule(mod, 1, 5).getFactor())
				,mod -> ensureModule(mod, 1, 5).setFactor(mod.getAmount(Mod421Key.A2M6F))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M6F))
		,A2M6R(Mod421Key.A2M6R, null, null, null
				,"calculateResult(1,A2M6I,A2M6F)"
				, null
				,mod -> mod.putAmount(Mod421Key.A2M6R, ensureModule(mod, 1, 5).getResult())
				,mod -> ensureModule(mod, 1, 5).setResult(mod.getAmount(Mod421Key.A2M6R))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M6R))
		
		,A2M7D(Mod421Key.A2M7D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A2M7D, ensureModule(mod, 1, 6).getDescription())
			,mod -> ensureModule(mod, 1, 6).setDescription(mod.getDescription(Mod421Key.A2M7D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M7D))
		,A2M7I(Mod421Key.A2M7I, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A2M7I, ensureModule(mod, 1, 6).getValue())
				,mod -> ensureModule(mod, 1, 6).setValue(mod.getAmount(Mod421Key.A2M7I))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M7I))
		,A2M7U(Mod421Key.A2M7U, null, null, null, null, null
				,mod -> mod.putDescription(Mod421Key.A2M7U, ensureModule(mod, 1, 6).getUnit())
				,mod -> ensureModule(mod, 1, 6).setUnit(mod.getDescription(Mod421Key.A2M7U))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M7U))
		,A2M7F(Mod421Key.A2M7F, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A2M7F, ensureModule(mod, 1, 6).getFactor())
				,mod -> ensureModule(mod, 1, 6).setFactor(mod.getAmount(Mod421Key.A2M7F))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M7F))
		,A2M7R(Mod421Key.A2M7R, null, null, null
				,"calculateResult(1,A2M7I,A2M7F)"
				, null
				,mod -> mod.putAmount(Mod421Key.A2M7R, ensureModule(mod, 1, 6).getResult())
				,mod -> ensureModule(mod, 1, 6).setResult(mod.getAmount(Mod421Key.A2M7R))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A2M7R))
		
		// (2) Actividades en régimen simplificado. Cuota devengada operaciones corrientes
		,A2DEV(Mod421Key.A2DEV, null, null, null, "round(A2M1R+A2M2R+A2M3R+A2M4R+A2M5R+A2M6R+A2M7R)", null
			,mod -> mod.putAmount(Mod421Key.A2DEV, ensureActivity(mod, 1).getDev())
			,mod -> ensureActivity(mod, 1).setDev(mod.getAmount(Mod421Key.A2DEV))
			,null)		
		// (2) Actividades en régimen simplificado. Indice corrector actividades de temporada
		,A2ICT(Mod421Key.A2ICT, null, null, null, "calculateIndiceTemporada(1,A2TEM)", null
			,mod -> mod.putAmount(Mod421Key.A2ICT, ensureActivity(mod, 1).getIct())
			,mod -> ensureActivity(mod, 1).setIct(mod.getAmount(Mod421Key.A2ICT))
			,null)
		// (2) Actividades en régimen simplificado. Porcentaje de ingreso a cuenta (1T, 2T, 3T)
		,A2POR(Mod421Key.A2POR, null, null, null
			,"calculatePorcentajeIngresoCuenta(1)", null
			,mod -> mod.putAmount(Mod421Key.A2POR, ensureActivity(mod, 1).getPor())
			,mod -> ensureActivity(mod, 1).setPor(mod.isLastPeriod() ? 0.0 : mod.getAmount(Mod421Key.A2POR))
			,null)
		// (2) Actividades en régimen simplificado. Ingreso a cuenta (1T, 2T, 3T)
		,A2ING(Mod421Key.A2ING, null, null, null
			,"calculateIngresoCuenta(1, A2TEM, A2DIA, A2ICT, A2POR)", null
			,mod -> mod.putAmount(Mod421Key.A2ING, ensureActivity(mod, 1).getIng())
			,mod -> ensureActivity(mod, 1).setIng(mod.isLastPeriod() ? 0.0 : mod.getAmount(Mod421Key.A2ING))
			,null)
		// (2) Actividades en régimen simplificado. 1% de la cuota devengada por operaciones corrientes (4T)
		,A2SO1(Mod421Key.A2SO1, null, null, null, "isLastPeriod()?round(A2DEV * 1 / 100):0.0", null
			,mod -> mod.putAmount(Mod421Key.A2SO1, ensureActivity(mod, 1).getSop1())
			,mod -> ensureActivity(mod, 1).setSop1(mod.getAmount(Mod421Key.A2SO1))
			,null)
		// (2) Actividades en régimen simplificado. Resto de cuotas soportadas (4T)
		,A2SOR(Mod421Key.A2SOR, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A2SOR, ensureActivity(mod, 1).getSopR())
			,mod -> ensureActivity(mod, 1).setSopR(mod.getAmount(Mod421Key.A2SOR))
			,null)
		// (2) Actividades en régimen simplificado. Cuotas soportadas operaciones corrientes (4T)
		,A2SOP(Mod421Key.A2SOP, null, null, null, "isLastPeriod()?(A2SO1+A2SOR):0.0", null
			,mod -> mod.putAmount(Mod421Key.A2SOP, ensureActivity(mod, 1).getSop())
			,mod -> ensureActivity(mod, 1).setSop(mod.getAmount(Mod421Key.A2SOP))
			,null)
		// (2) Actividades en régimen simplificado. RESULTADO 
		,A2RES(Mod421Key.A2RES, null, null, null, "calculateResultadoAnual( A2DEV, A2SOP, A2ICT)",null
			,mod -> mod.putAmount(Mod421Key.A2RES, ensureActivity(mod, 1).getRes())
			,mod -> ensureActivity(mod, 1).setRes(mod.getAmount(Mod421Key.A2RES))
			,null)
		// (2) Actividades en régimen simplificado. Porcentaje cuota mínima (4T)
		,A2PCM(Mod421Key.A2PCM, null, null, null, "isLastPeriod()?A2PCM:0.0", null
			,mod -> mod.putAmount(Mod421Key.A2PCM, ensureActivity(mod, 1).getPcm())
			,mod -> ensureActivity(mod, 1).setPcm(mod.getAmount(Mod421Key.A2PCM))
			,null)
		// (2) Actividades en régimen simplificado. Cuota mínima (4T)
		,A2CMN(Mod421Key.A2CMN, null, null, null, "calculateCuotaMinima(A2DEV,A2PCM,A2ICT)",null
			,mod -> mod.putAmount(Mod421Key.A2CMN, ensureActivity(mod, 1).getCmn())
			,mod -> ensureActivity(mod, 1).setCmn(mod.getAmount(Mod421Key.A2CMN))
			,null)
		// (2) Actividades en régimen simplificado. Cuota anual derivada régimen simplificado (4T)
		,A2CAD(Mod421Key.A2CAD, null, null, null, "isLastPeriod()?((A2CMN>A2RES)?A2CMN:A2RES):0.0", null
			,mod -> mod.putAmount(Mod421Key.A2CAD, ensureActivity(mod, 1).getCad())
			,mod -> ensureActivity(mod, 1).setCad(mod.getAmount(Mod421Key.A2CAD))
			,null)
		
// ACTIVIDAD 3 -------------------------------------------------------------------------------------------------------------------------
		
		// (3) Actividades en régimen simplificado. Epigrafe IAE
		,A3EP1(Mod421Key.A3EP1, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A3EP1, ensureActivity(mod, 2).getEpigraph())
			,mod -> ensureActivity(mod, 2).setEpigraph(mod.getDescription(Mod421Key.A3EP1))
			,(prev,cur) -> copyKey(prev, cur, Mod421Key.A3EP1))
		// (3) Actividades en régimen simplificado. Epigrafe IAE - Descripción
		,A3EPD(Mod421Key.A3EPD, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A3EPD, ensureActivity(mod, 2).getDescription())
			,mod -> ensureActivity(mod, 2).setDescription(mod.getDescription(Mod421Key.A3EPD))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3EPD))
		// (3) Actividades en régimen simplificado. Epigrafe IAE - Indicador auxiliar de actividad en el caso de epígrafes 691.9 y 722
		,A3EP2(Mod421Key.A3EP2, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A3EP2, ensureActivity(mod, 2).getSpecialEpigraph())
			,mod -> ensureActivity(mod, 2).setSpecialEpigraph(checkSpecialEpigraph(mod, Mod421Key.A3EP2, Mod421Key.A3EP1, Mod421Key.A3EPD))
		    ,(prev,cur) -> copyKey(prev, cur, Mod421Key.A3EP2))
		// (3) Actividades en régimen simplificado. Actividad de Temporada. Nº Días en los que se ejerció la actividad en el año anterior
		,A3TEM(Mod421Key.A3TEM, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A3TEM, ensureActivity(mod, 2).getTem())
			,mod -> ensureActivity(mod, 2).setTem((int) mod.getAmount(Mod421Key.A3TEM))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3TEM))
		// (3) Actividades en régimen simplificado. Número de días de ejercicio de la actividad en el trimestre
		,A3DIA(Mod421Key.A3DIA, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A3DIA, ensureActivity(mod, 2).getDia())
			,mod -> ensureActivity(mod, 2).setDia((int) mod.getAmount(Mod421Key.A3DIA))
			,(prev,cur) -> ensureActivityDays(prev,cur, Mod421Key.A3EP1, Mod421Key.A3DIA))

		,A3M1D(Mod421Key.A3M1D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A3M1D, ensureModule(mod, 2, 0).getDescription())
			,mod -> ensureModule(mod, 2, 0).setDescription(mod.getDescription(Mod421Key.A3M1D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M1D))
		,A3M1I(Mod421Key.A3M1I, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A3M1I, ensureModule(mod, 2, 0).getValue())
			,mod -> ensureModule(mod, 2, 0).setValue(mod.getAmount(Mod421Key.A3M1I))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M1I, Mod421Key.A3EP1))
		,A3M1U(Mod421Key.A3M1U, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A3M1U, ensureModule(mod, 2, 0).getUnit())
			,mod -> ensureModule(mod, 2, 0).setUnit(mod.getDescription(Mod421Key.A3M1U))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M1U))
		,A3M1F(Mod421Key.A3M1F, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A3M1F, ensureModule(mod, 2, 0).getFactor())
			,mod -> ensureModule(mod, 2, 0).setFactor(mod.getAmount(Mod421Key.A3M1F))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M1F))
		,A3M1R(Mod421Key.A3M1R, null, null, null
			,"calculateResult(2,A3M1I,A3M1F)"
			, null
			,mod -> mod.putAmount(Mod421Key.A3M1R, ensureModule(mod, 2, 0).getResult())
			,mod -> ensureModule(mod, 2, 0).setResult(mod.getAmount(Mod421Key.A3M1R))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M1R))
		
		,A3M2D(Mod421Key.A3M2D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A3M2D, ensureModule(mod, 2, 1).getDescription())
			,mod -> ensureModule(mod, 2, 1).setDescription(mod.getDescription(Mod421Key.A3M2D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M2D))
		,A3M2I(Mod421Key.A3M2I, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A3M2I, ensureModule(mod, 2, 1).getValue())
			,mod -> ensureModule(mod, 2, 1).setValue(mod.getAmount(Mod421Key.A3M2I))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M2I))
		,A3M2U(Mod421Key.A3M2U, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A3M2U, ensureModule(mod, 2, 1).getUnit())
			,mod -> ensureModule(mod, 2, 1).setUnit(mod.getDescription(Mod421Key.A3M2U))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M2U))
		,A3M2F(Mod421Key.A3M2F, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A3M2F, ensureModule(mod, 2, 1).getFactor())
			,mod -> ensureModule(mod, 2, 1).setFactor(mod.getAmount(Mod421Key.A3M2F))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M2F))
		,A3M2R(Mod421Key.A3M2R, null, null, null
			,"calculateResult(2,A3M2I,A3M2F)"
			, null
			,mod -> mod.putAmount(Mod421Key.A3M2R, ensureModule(mod, 2, 1).getResult())
			,mod -> ensureModule(mod, 2, 1).setResult(mod.getAmount(Mod421Key.A3M2R))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M2R))
		
		,A3M3D(Mod421Key.A3M3D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A3M3D, ensureModule(mod, 2, 2).getDescription())
			,mod -> ensureModule(mod, 2, 2).setDescription(mod.getDescription(Mod421Key.A3M3D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M3D))
		,A3M3I(Mod421Key.A3M3I, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A3M3I, ensureModule(mod, 2, 2).getValue())
			,mod -> ensureModule(mod, 2, 2).setValue(mod.getAmount(Mod421Key.A3M3I))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M3I))
		,A3M3U(Mod421Key.A3M3U, null, null, null, null, null
				,mod -> mod.putDescription(Mod421Key.A3M3U, ensureModule(mod, 2, 2).getUnit())
			,mod -> ensureModule(mod, 2, 2).setUnit(mod.getDescription(Mod421Key.A3M3U))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M3U))
		,A3M3F(Mod421Key.A3M3F, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A3M3F, ensureModule(mod, 2, 2).getFactor())
				,mod -> ensureModule(mod, 2, 2).setFactor(mod.getAmount(Mod421Key.A3M3F))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M3F))
		,A3M3R(Mod421Key.A3M3R, null, null, null
				,"calculateResult(2,A3M3I,A3M3F)"
				, null
				,mod -> mod.putAmount(Mod421Key.A3M3R, ensureModule(mod, 2, 2).getResult())
				,mod -> ensureModule(mod, 2, 2).setResult(mod.getAmount(Mod421Key.A3M3R))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M3R))
		
		,A3M4D(Mod421Key.A3M4D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A3M4D, ensureModule(mod, 2, 3).getDescription())
			,mod -> ensureModule(mod, 2, 3).setDescription(mod.getDescription(Mod421Key.A3M4D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M4D))
		,A3M4I(Mod421Key.A3M4I, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A3M4I, ensureModule(mod, 2, 3).getValue())
			,mod -> ensureModule(mod, 2, 3).setValue(mod.getAmount(Mod421Key.A3M4I))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M4I))
		,A3M4U(Mod421Key.A3M4U, null, null, null, null, null
				,mod -> mod.putDescription(Mod421Key.A3M4U, ensureModule(mod, 2, 3).getUnit())
				,mod -> ensureModule(mod, 2, 3).setUnit(mod.getDescription(Mod421Key.A3M4U))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M4U))
		,A3M4F(Mod421Key.A3M4F, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A3M4F, ensureModule(mod, 2, 3).getFactor())
				,mod -> ensureModule(mod, 2, 3).setFactor(mod.getAmount(Mod421Key.A3M4F))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M4F))
		,A3M4R(Mod421Key.A3M4R, null, null, null
				,"calculateResult(2,A3M4I,A3M4F)"
				, null
				,mod -> mod.putAmount(Mod421Key.A3M4R, ensureModule(mod, 2, 3).getResult())
				,mod -> ensureModule(mod, 2, 3).setResult(mod.getAmount(Mod421Key.A3M4R))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M4R))
		
		,A3M5D(Mod421Key.A3M5D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A3M5D, ensureModule(mod, 2, 4).getDescription())
			,mod -> ensureModule(mod, 2, 4).setDescription(mod.getDescription(Mod421Key.A3M5D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M5D))
		,A3M5I(Mod421Key.A3M5I, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A3M5I, ensureModule(mod, 2, 4).getValue())
				,mod -> ensureModule(mod, 2, 4).setValue(mod.getAmount(Mod421Key.A3M5I))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M5I))
		,A3M5U(Mod421Key.A3M5U, null, null, null, null, null
				,mod -> mod.putDescription(Mod421Key.A3M5U, ensureModule(mod, 2, 4).getUnit())
				,mod -> ensureModule(mod, 2, 4).setUnit(mod.getDescription(Mod421Key.A3M5U))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M5U))
		,A3M5F(Mod421Key.A3M5F, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A3M5F, ensureModule(mod, 2, 4).getFactor())
				,mod -> ensureModule(mod, 2, 4).setFactor(mod.getAmount(Mod421Key.A3M5F))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M5F))
		,A3M5R(Mod421Key.A3M5R, null, null, null
				,"calculateResult(2,A3M5I,A3M5F)"
				, null
				,mod -> mod.putAmount(Mod421Key.A3M5R, ensureModule(mod, 2, 4).getResult())
				,mod -> ensureModule(mod, 2, 4).setResult(mod.getAmount(Mod421Key.A3M5R))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M5R))
		
		,A3M6D(Mod421Key.A3M6D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A3M6D, ensureModule(mod, 2, 5).getDescription())
			,mod -> ensureModule(mod, 2, 5).setDescription(mod.getDescription(Mod421Key.A3M6D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M6D))
		,A3M6I(Mod421Key.A3M6I, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A3M6I, ensureModule(mod, 2, 5).getValue())
			,mod -> ensureModule(mod, 2, 5).setValue(mod.getAmount(Mod421Key.A3M6I))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M6I))
		,A3M6U(Mod421Key.A3M6U, null, null, null, null, null
				,mod -> mod.putDescription(Mod421Key.A3M6U, ensureModule(mod, 2, 5).getUnit())
				,mod -> ensureModule(mod, 2, 5).setUnit(mod.getDescription(Mod421Key.A3M6U))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M6U))
		,A3M6F(Mod421Key.A3M6F, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A3M6F, ensureModule(mod, 2, 5).getFactor())
				,mod -> ensureModule(mod, 2, 5).setFactor(mod.getAmount(Mod421Key.A3M6F))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M6F))
		,A3M6R(Mod421Key.A3M6R, null, null, null
				,"calculateResult(2,A3M6I,A3M6F)"
				, null
				,mod -> mod.putAmount(Mod421Key.A3M6R, ensureModule(mod, 2, 5).getResult())
				,mod -> ensureModule(mod, 2, 5).setResult(mod.getAmount(Mod421Key.A3M6R))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M6R))
		
		,A3M7D(Mod421Key.A3M7D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A3M7D, ensureModule(mod, 2, 6).getDescription())
			,mod -> ensureModule(mod, 2, 6).setDescription(mod.getDescription(Mod421Key.A3M7D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M7D))
		,A3M7I(Mod421Key.A3M7I, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A3M7I, ensureModule(mod, 2, 6).getValue())
				,mod -> ensureModule(mod, 2, 6).setValue(mod.getAmount(Mod421Key.A3M7I))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M7I))
		,A3M7U(Mod421Key.A3M7U, null, null, null, null, null
				,mod -> mod.putDescription(Mod421Key.A3M7U, ensureModule(mod, 2, 6).getUnit())
				,mod -> ensureModule(mod, 2, 6).setUnit(mod.getDescription(Mod421Key.A3M7U))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M7U))
		,A3M7F(Mod421Key.A3M7F, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A3M7F, ensureModule(mod, 2, 6).getFactor())
				,mod -> ensureModule(mod, 2, 6).setFactor(mod.getAmount(Mod421Key.A3M7F))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M7F))
		,A3M7R(Mod421Key.A3M7R, null, null, null
				,"calculateResult(2,A3M7I,A3M7F)"
				, null
				,mod -> mod.putAmount(Mod421Key.A3M7R, ensureModule(mod, 2, 6).getResult())
				,mod -> ensureModule(mod, 2, 6).setResult(mod.getAmount(Mod421Key.A3M7R))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A3M7R))
		
		// (3) Actividades en régimen simplificado. Cuota devengada operaciones corrientes
		,A3DEV(Mod421Key.A3DEV, null, null, null, "round(A3M1R+A3M2R+A3M3R+A3M4R+A3M5R+A3M6R+A3M7R)", null
			,mod -> mod.putAmount(Mod421Key.A3DEV, ensureActivity(mod, 2).getDev())
			,mod -> ensureActivity(mod, 2).setDev(mod.getAmount(Mod421Key.A3DEV))
			,null)		
		// (3) Actividades en régimen simplificado. Indice corrector actividades de temporada
		,A3ICT(Mod421Key.A3ICT, null, null, null, "calculateIndiceTemporada(2,A3TEM)", null
			,mod -> mod.putAmount(Mod421Key.A3ICT, ensureActivity(mod, 2).getIct())
			,mod -> ensureActivity(mod, 2).setIct(mod.getAmount(Mod421Key.A3ICT))
			,null)
		// (3) Actividades en régimen simplificado. Porcentaje de ingreso a cuenta (1T, 2T, 3T)
		,A3POR(Mod421Key.A3POR, null, null, null
			,"calculatePorcentajeIngresoCuenta(2)", null
			,mod -> mod.putAmount(Mod421Key.A3POR, ensureActivity(mod, 2).getPor())
			,mod -> ensureActivity(mod, 2).setPor(mod.isLastPeriod() ? 0.0 : mod.getAmount(Mod421Key.A3POR))
			,null)
		// (3) Actividades en régimen simplificado. Ingreso a cuenta (1T, 2T, 3T)
		,A3ING(Mod421Key.A3ING, null, null, null
			,"calculateIngresoCuenta(2, A3TEM, A3DIA, A3ICT, A3POR)", null
			,mod -> mod.putAmount(Mod421Key.A3ING, ensureActivity(mod, 2).getIng())
			,mod -> ensureActivity(mod, 2).setIng(mod.isLastPeriod() ? 0.0 : mod.getAmount(Mod421Key.A3ING))
			,null)
		// (3) Actividades en régimen simplificado. 1% de la cuota devengada por operaciones corrientes (4T)
		,A3SO1(Mod421Key.A3SO1, null, null, null, "isLastPeriod()?round(A3DEV * 1 / 100):0.0", null
			,mod -> mod.putAmount(Mod421Key.A3SO1, ensureActivity(mod, 2).getSop1())
			,mod -> ensureActivity(mod, 2).setSop1(mod.getAmount(Mod421Key.A3SO1))
			,null)
		// (3) Actividades en régimen simplificado. Resto de cuotas soportadas (4T)
		,A3SOR(Mod421Key.A3SOR, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A3SOR, ensureActivity(mod, 2).getSopR())
			,mod -> ensureActivity(mod, 2).setSopR(mod.getAmount(Mod421Key.A3SOR))
			,null)
		// (3) Actividades en régimen simplificado. Cuotas soportadas operaciones corrientes (4T)
		,A3SOP(Mod421Key.A3SOP, null, null, null, "isLastPeriod()?(A3SO1+A3SOR):0.0", null
			,mod -> mod.putAmount(Mod421Key.A3SOP, ensureActivity(mod, 2).getSop())
			,mod -> ensureActivity(mod, 2).setSop(mod.getAmount(Mod421Key.A3SOP))
			,null)
		// (3) Actividades en régimen simplificado. RESULTADO 
		,A3RES(Mod421Key.A3RES, null, null, null, "calculateResultadoAnual( A3DEV, A3SOP, A3ICT)",null
			,mod -> mod.putAmount(Mod421Key.A3RES, ensureActivity(mod, 2).getRes())
			,mod -> ensureActivity(mod, 2).setRes(mod.getAmount(Mod421Key.A3RES))
			,null)
		// (3) Actividades en régimen simplificado. Porcentaje cuota mínima (4T)
		,A3PCM(Mod421Key.A3PCM, null, null, null, "isLastPeriod()?A3PCM:0.0", null
			,mod -> mod.putAmount(Mod421Key.A3PCM, ensureActivity(mod, 2).getPcm())
			,mod -> ensureActivity(mod, 2).setPcm(mod.getAmount(Mod421Key.A3PCM))
			,null)
		// (3) Actividades en régimen simplificado. Cuota mínima (4T)
		,A3CMN(Mod421Key.A3CMN, null, null, null, "calculateCuotaMinima(A3DEV,A3PCM,A3ICT)",null
			,mod -> mod.putAmount(Mod421Key.A3CMN, ensureActivity(mod, 2).getCmn())
			,mod -> ensureActivity(mod, 2).setCmn(mod.getAmount(Mod421Key.A3CMN))
			,null)
		// (3) Actividades en régimen simplificado. Cuota anual derivada régimen simplificado (4T)
		,A3CAD(Mod421Key.A3CAD, null, null, null, "isLastPeriod()?((A3CMN>A3RES)?A3CMN:A3RES):0.0", null
			,mod -> mod.putAmount(Mod421Key.A3CAD, ensureActivity(mod, 2).getCad())
			,mod -> ensureActivity(mod, 2).setCad(mod.getAmount(Mod421Key.A3CAD))
			,null)

// ACTIVIDAD 4 -------------------------------------------------------------------------------------------------------------------------		

		// (4) Actividades en régimen simplificado. Epigrafe IAE
		,A4EP1(Mod421Key.A4EP1, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A4EP1, ensureActivity(mod, 3).getEpigraph())
			,mod -> ensureActivity(mod, 3).setEpigraph(mod.getDescription(Mod421Key.A4EP1))
			,(prev,cur) -> copyKey(prev, cur, Mod421Key.A4EP1))
		// (4) Actividades en régimen simplificado. Epigrafe IAE - Descripción
		,A4EPD(Mod421Key.A4EPD, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A4EPD, ensureActivity(mod, 3).getDescription())
			,mod -> ensureActivity(mod, 3).setDescription(mod.getDescription(Mod421Key.A4EPD))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4EPD))
		// (4) Actividades en régimen simplificado. Epigrafe IAE - Indicador auxiliar de actividad en el caso de epígrafes 691.9 y 722
		,A4EP2(Mod421Key.A4EP2, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A4EP2, ensureActivity(mod, 3).getSpecialEpigraph())
			,mod -> ensureActivity(mod, 3).setSpecialEpigraph(checkSpecialEpigraph(mod, Mod421Key.A4EP2, Mod421Key.A4EP1, Mod421Key.A4EPD))
		    ,(prev,cur) -> copyKey(prev, cur, Mod421Key.A4EP2))
		// (4) Actividades en régimen simplificado. Actividad de Temporada. Nº Días en los que se ejerció la actividad en el año anterior
		,A4TEM(Mod421Key.A4TEM, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A4TEM, ensureActivity(mod, 3).getTem())
			,mod -> ensureActivity(mod, 3).setTem((int) mod.getAmount(Mod421Key.A4TEM))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4TEM))
		// (4) Actividades en régimen simplificado. Número de días de ejercicio de la actividad en el trimestre
		,A4DIA(Mod421Key.A4DIA, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A4DIA, ensureActivity(mod, 3).getDia())
			,mod -> ensureActivity(mod, 3).setDia((int) mod.getAmount(Mod421Key.A4DIA))
			,(prev,cur) -> ensureActivityDays(prev,cur, Mod421Key.A4EP1, Mod421Key.A4DIA))

		,A4M1D(Mod421Key.A4M1D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A4M1D, ensureModule(mod, 3, 0).getDescription())
			,mod -> ensureModule(mod, 3, 0).setDescription(mod.getDescription(Mod421Key.A4M1D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M1D))
		,A4M1I(Mod421Key.A4M1I, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A4M1I, ensureModule(mod, 3, 0).getValue())
			,mod -> ensureModule(mod, 3, 0).setValue(mod.getAmount(Mod421Key.A4M1I))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M1I, Mod421Key.A4EP1))
		,A4M1U(Mod421Key.A4M1U, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A4M1U, ensureModule(mod, 3, 0).getUnit())
			,mod -> ensureModule(mod, 3, 0).setUnit(mod.getDescription(Mod421Key.A4M1U))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M1U))
		,A4M1F(Mod421Key.A4M1F, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A4M1F, ensureModule(mod, 3, 0).getFactor())
			,mod -> ensureModule(mod, 3, 0).setFactor(mod.getAmount(Mod421Key.A4M1F))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M1F))
		,A4M1R(Mod421Key.A4M1R, null, null, null
			,"calculateResult(3,A4M1I,A4M1F)"
			, null
			,mod -> mod.putAmount(Mod421Key.A4M1R, ensureModule(mod, 3, 0).getResult())
			,mod -> ensureModule(mod, 3, 0).setResult(mod.getAmount(Mod421Key.A4M1R))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M1R))
		
		,A4M2D(Mod421Key.A4M2D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A4M2D, ensureModule(mod, 3, 1).getDescription())
			,mod -> ensureModule(mod, 3, 1).setDescription(mod.getDescription(Mod421Key.A4M2D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M2D))
		,A4M2I(Mod421Key.A4M2I, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A4M2I, ensureModule(mod, 3, 1).getValue())
			,mod -> ensureModule(mod, 3, 1).setValue(mod.getAmount(Mod421Key.A4M2I))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M2I))
		,A4M2U(Mod421Key.A4M2U, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A4M2U, ensureModule(mod, 3, 1).getUnit())
			,mod -> ensureModule(mod, 3, 1).setUnit(mod.getDescription(Mod421Key.A4M2U))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M2U))
		,A4M2F(Mod421Key.A4M2F, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A4M2F, ensureModule(mod, 3, 1).getFactor())
			,mod -> ensureModule(mod, 3, 1).setFactor(mod.getAmount(Mod421Key.A4M2F))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M2F))
		,A4M2R(Mod421Key.A4M2R, null, null, null
			,"calculateResult(3,A4M2I,A4M2F)"
			, null
			,mod -> mod.putAmount(Mod421Key.A4M2R, ensureModule(mod, 3, 1).getResult())
			,mod -> ensureModule(mod, 3, 1).setResult(mod.getAmount(Mod421Key.A4M2R))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M2R))
		
		,A4M3D(Mod421Key.A4M3D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A4M3D, ensureModule(mod, 3, 2).getDescription())
			,mod -> ensureModule(mod, 3, 2).setDescription(mod.getDescription(Mod421Key.A4M3D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M3D))
		,A4M3I(Mod421Key.A4M3I, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A4M3I, ensureModule(mod, 3, 2).getValue())
			,mod -> ensureModule(mod, 3, 2).setValue(mod.getAmount(Mod421Key.A4M3I))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M3I))
		,A4M3U(Mod421Key.A4M3U, null, null, null, null, null
				,mod -> mod.putDescription(Mod421Key.A4M3U, ensureModule(mod, 3, 2).getUnit())
			,mod -> ensureModule(mod, 3, 2).setUnit(mod.getDescription(Mod421Key.A4M3U))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M3U))
		,A4M3F(Mod421Key.A4M3F, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A4M3F, ensureModule(mod, 3, 2).getFactor())
				,mod -> ensureModule(mod, 3, 2).setFactor(mod.getAmount(Mod421Key.A4M3F))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M3F))
		,A4M3R(Mod421Key.A4M3R, null, null, null
				,"calculateResult(3,A4M3I,A4M3F)"
				, null
				,mod -> mod.putAmount(Mod421Key.A4M3R, ensureModule(mod, 3, 2).getResult())
				,mod -> ensureModule(mod, 3, 2).setResult(mod.getAmount(Mod421Key.A4M3R))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M3R))
		
		,A4M4D(Mod421Key.A4M4D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A4M4D, ensureModule(mod, 3, 3).getDescription())
			,mod -> ensureModule(mod, 3, 3).setDescription(mod.getDescription(Mod421Key.A4M4D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M4D))
		,A4M4I(Mod421Key.A4M4I, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A4M4I, ensureModule(mod, 3, 3).getValue())
			,mod -> ensureModule(mod, 3, 3).setValue(mod.getAmount(Mod421Key.A4M4I))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M4I))
		,A4M4U(Mod421Key.A4M4U, null, null, null, null, null
				,mod -> mod.putDescription(Mod421Key.A4M4U, ensureModule(mod, 3, 3).getUnit())
				,mod -> ensureModule(mod, 3, 3).setUnit(mod.getDescription(Mod421Key.A4M4U))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M4U))
		,A4M4F(Mod421Key.A4M4F, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A4M4F, ensureModule(mod, 3, 3).getFactor())
				,mod -> ensureModule(mod, 3, 3).setFactor(mod.getAmount(Mod421Key.A4M4F))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M4F))
		,A4M4R(Mod421Key.A4M4R, null, null, null
				,"calculateResult(3,A4M4I,A4M4F)"
				, null
				,mod -> mod.putAmount(Mod421Key.A4M4R, ensureModule(mod, 3, 3).getResult())
				,mod -> ensureModule(mod, 3, 3).setResult(mod.getAmount(Mod421Key.A4M4R))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M4R))
		
		,A4M5D(Mod421Key.A4M5D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A4M5D, ensureModule(mod, 3, 4).getDescription())
			,mod -> ensureModule(mod, 3, 4).setDescription(mod.getDescription(Mod421Key.A4M5D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M5D))
		,A4M5I(Mod421Key.A4M5I, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A4M5I, ensureModule(mod, 3, 4).getValue())
				,mod -> ensureModule(mod, 3, 4).setValue(mod.getAmount(Mod421Key.A4M5I))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M5I))
		,A4M5U(Mod421Key.A4M5U, null, null, null, null, null
				,mod -> mod.putDescription(Mod421Key.A4M5U, ensureModule(mod, 3, 4).getUnit())
				,mod -> ensureModule(mod, 3, 4).setUnit(mod.getDescription(Mod421Key.A4M5U))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M5U))
		,A4M5F(Mod421Key.A4M5F, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A4M5F, ensureModule(mod, 3, 4).getFactor())
				,mod -> ensureModule(mod, 3, 4).setFactor(mod.getAmount(Mod421Key.A4M5F))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M5F))
		,A4M5R(Mod421Key.A4M5R, null, null, null
				,"calculateResult(3,A4M5I,A4M5F)"
				, null
				,mod -> mod.putAmount(Mod421Key.A4M5R, ensureModule(mod, 3, 4).getResult())
				,mod -> ensureModule(mod, 3, 4).setResult(mod.getAmount(Mod421Key.A4M5R))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M5R))
		
		,A4M6D(Mod421Key.A4M6D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A4M6D, ensureModule(mod, 3, 5).getDescription())
			,mod -> ensureModule(mod, 3, 5).setDescription(mod.getDescription(Mod421Key.A4M6D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M6D))
		,A4M6I(Mod421Key.A4M6I, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A4M6I, ensureModule(mod, 3, 5).getValue())
			,mod -> ensureModule(mod, 3, 5).setValue(mod.getAmount(Mod421Key.A4M6I))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M6I))
		,A4M6U(Mod421Key.A4M6U, null, null, null, null, null
				,mod -> mod.putDescription(Mod421Key.A4M6U, ensureModule(mod, 3, 5).getUnit())
				,mod -> ensureModule(mod, 3, 5).setUnit(mod.getDescription(Mod421Key.A4M6U))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M6U))
		,A4M6F(Mod421Key.A4M6F, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A4M6F, ensureModule(mod, 3, 5).getFactor())
				,mod -> ensureModule(mod, 3, 5).setFactor(mod.getAmount(Mod421Key.A4M6F))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M6F))
		,A4M6R(Mod421Key.A4M6R, null, null, null
				,"calculateResult(3,A4M6I,A4M6F)"
				, null
				,mod -> mod.putAmount(Mod421Key.A4M6R, ensureModule(mod, 3, 5).getResult())
				,mod -> ensureModule(mod, 3, 5).setResult(mod.getAmount(Mod421Key.A4M6R))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M6R))
		
		,A4M7D(Mod421Key.A4M7D, null, null, null, null, null
			,mod -> mod.putDescription(Mod421Key.A4M7D, ensureModule(mod, 3, 6).getDescription())
			,mod -> ensureModule(mod, 3, 6).setDescription(mod.getDescription(Mod421Key.A4M7D))
			,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M7D))
		,A4M7I(Mod421Key.A4M7I, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A4M7I, ensureModule(mod, 3, 6).getValue())
				,mod -> ensureModule(mod, 3, 6).setValue(mod.getAmount(Mod421Key.A4M7I))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M7I))
		,A4M7U(Mod421Key.A4M7U, null, null, null, null, null
				,mod -> mod.putDescription(Mod421Key.A4M7U, ensureModule(mod, 3, 6).getUnit())
				,mod -> ensureModule(mod, 3, 6).setUnit(mod.getDescription(Mod421Key.A4M7U))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M7U))
		,A4M7F(Mod421Key.A4M7F, null, null, null, null, null
				,mod -> mod.putAmount(Mod421Key.A4M7F, ensureModule(mod, 3, 6).getFactor())
				,mod -> ensureModule(mod, 3, 6).setFactor(mod.getAmount(Mod421Key.A4M7F))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M7F))
		,A4M7R(Mod421Key.A4M7R, null, null, null
				,"calculateResult(3,A4M7I,A4M7F)"
				, null
				,mod -> mod.putAmount(Mod421Key.A4M7R, ensureModule(mod, 3, 6).getResult())
				,mod -> ensureModule(mod, 3, 6).setResult(mod.getAmount(Mod421Key.A4M7R))
				,(prev,cur) -> copyKey(prev,cur, Mod421Key.A4M7R))
		
		// (4) Actividades en régimen simplificado. Cuota devengada operaciones corrientes
		,A4DEV(Mod421Key.A4DEV, null, null, null, "round(A4M1R+A4M2R+A4M3R+A4M4R+A4M5R+A4M6R+A4M7R)", null
			,mod -> mod.putAmount(Mod421Key.A4DEV, ensureActivity(mod, 3).getDev())
			,mod -> ensureActivity(mod, 3).setDev(mod.getAmount(Mod421Key.A4DEV))
			,null)		
		// (4) Actividades en régimen simplificado. Indice corrector actividades de temporada
		,A4ICT(Mod421Key.A4ICT, null, null, null, "calculateIndiceTemporada(3,A4TEM)", null
			,mod -> mod.putAmount(Mod421Key.A4ICT, ensureActivity(mod, 3).getIct())
			,mod -> ensureActivity(mod, 3).setIct(mod.getAmount(Mod421Key.A4ICT))
			,null)
		// (4) Actividades en régimen simplificado. Porcentaje de ingreso a cuenta (1T, 2T, 3T)
		,A4POR(Mod421Key.A4POR, null, null, null
			,"calculatePorcentajeIngresoCuenta(3)", null
			,mod -> mod.putAmount(Mod421Key.A4POR, ensureActivity(mod, 3).getPor())
			,mod -> ensureActivity(mod, 3).setPor(mod.isLastPeriod() ? 0.0 : mod.getAmount(Mod421Key.A4POR))
			,null)
		// (4) Actividades en régimen simplificado. Ingreso a cuenta (1T, 2T, 3T)
		,A4ING(Mod421Key.A4ING, null, null, null
			,"calculateIngresoCuenta(3, A4TEM, A4DIA, A4ICT, A4POR)", null
			,mod -> mod.putAmount(Mod421Key.A4ING, ensureActivity(mod, 3).getIng())
			,mod -> ensureActivity(mod, 3).setIng(mod.isLastPeriod() ? 0.0 : mod.getAmount(Mod421Key.A4ING))
			,null)
		// (4) Actividades en régimen simplificado. 1% de la cuota devengada por operaciones corrientes (4T)
		,A4SO1(Mod421Key.A4SO1, null, null, null, "isLastPeriod()?round(A4DEV * 1 / 100):0.0", null
			,mod -> mod.putAmount(Mod421Key.A4SO1, ensureActivity(mod, 3).getSop1())
			,mod -> ensureActivity(mod, 3).setSop1(mod.getAmount(Mod421Key.A4SO1))
			,null)
		// (4) Actividades en régimen simplificado. Resto de cuotas soportadas (4T)
		,A4SOR(Mod421Key.A4SOR, null, null, null, null, null
			,mod -> mod.putAmount(Mod421Key.A4SOR, ensureActivity(mod, 3).getSopR())
			,mod -> ensureActivity(mod, 3).setSopR(mod.getAmount(Mod421Key.A4SOR))
			,null)
		// (4) Actividades en régimen simplificado. Cuotas soportadas operaciones corrientes (4T)
		,A4SOP(Mod421Key.A4SOP, null, null, null, "isLastPeriod()?(A4SO1+A4SOR):0.0", null
			,mod -> mod.putAmount(Mod421Key.A4SOP, ensureActivity(mod, 3).getSop())
			,mod -> ensureActivity(mod, 3).setSop(mod.getAmount(Mod421Key.A4SOP))
			,null)
		// (4) Actividades en régimen simplificado. RESULTADO 
		,A4RES(Mod421Key.A4RES, null, null, null, "calculateResultadoAnual( A4DEV, A4SOP, A4ICT)",null
			,mod -> mod.putAmount(Mod421Key.A4RES, ensureActivity(mod, 3).getRes())
			,mod -> ensureActivity(mod, 3).setRes(mod.getAmount(Mod421Key.A4RES))
			,null)
		// (4) Actividades en régimen simplificado. Porcentaje cuota mínima (4T)
		,A4PCM(Mod421Key.A4PCM, null, null, null, "isLastPeriod()?A4PCM:0.0", null
			,mod -> mod.putAmount(Mod421Key.A4PCM, ensureActivity(mod, 3).getPcm())
			,mod -> ensureActivity(mod, 3).setPcm(mod.getAmount(Mod421Key.A4PCM))
			,null)
		// (4) Actividades en régimen simplificado. Cuota mínima (4T)
		,A4CMN(Mod421Key.A4CMN, null, null, null, "calculateCuotaMinima(A4DEV,A4PCM,A4ICT)",null
			,mod -> mod.putAmount(Mod421Key.A4CMN, ensureActivity(mod, 3).getCmn())
			,mod -> ensureActivity(mod, 3).setCmn(mod.getAmount(Mod421Key.A4CMN))
			,null)
		// (4) Actividades en régimen simplificado. Cuota anual derivada régimen simplificado (4T)
		,A4CAD(Mod421Key.A4CAD, null, null, null, "isLastPeriod()?((A4CMN>A4RES)?A4CMN:A4RES):0.0", null
			,mod -> mod.putAmount(Mod421Key.A4CAD, ensureActivity(mod, 3).getCad())
			,mod -> ensureActivity(mod, 3).setCad(mod.getAmount(Mod421Key.A4CAD))
			,null)		
		
		// RESULTADO ---------------------------------------------------------------------------------------------------------------------------------
		
		// 06 Cantidad a cuenta de acuerdo con los datos-base provisionales (1T/2T/3T)
		, C06(Mod421Key.C06, null, null, null, "A1ING+A2ING+A3ING+A4ING", null)
		// 07 Cuota anual devengada por operaciones corrientes (4T)
		, C07(Mod421Key.C07, null, null, null, "isLastPeriod()?(A1DEV+A2DEV+A3DEV+A4DEV):0.0", null)
		// 08 Cuotas soportadas o satisfechas en el ejercicio por operaciones corrientes (4T)
		, C08(Mod421Key.C08, null, null, null, "isLastPeriod()?(A1SOP+A2SOP+A3SOP+A4SOP):0.0", null)
		// 09 Cuota anual derivada del régimen simplificado (4T)
		, C09(Mod421Key.C09, null, null, null, "isLastPeriod()?(A1CAD+A2CAD+A3CAD+A4CAD):0.0", null)

		// Desglose Cantidad a cuenta autoliquidaciones trimestrales anteriores (4T) 
		, C10T1(Mod421Key.C10T1, null, null, (ctx,mod) -> add( Mod421Key.C10T1, mod, getIngresoCuentaAnterior(ctx, mod, Period.T1)), null, null)
		, C10T2(Mod421Key.C10T2, null, null, (ctx,mod) -> add( Mod421Key.C10T2, mod, getIngresoCuentaAnterior(ctx, mod, Period.T2)), null, null)
		, C10T3(Mod421Key.C10T3, null, null, (ctx,mod) -> add( Mod421Key.C10T3, mod, getIngresoCuentaAnterior(ctx, mod, Period.T3)), null, null)

		// 10 Cantidad a cuenta autoliquidaciones trimestrales anteriores (4T)
		, C10(Mod421Key.C10, null, null, null, "isLastPeriod()?(C10T1+C10T2+C10T3):0.0", null)
		// 11 Diferencia (4T)
		, C11(Mod421Key.C11, null, null, null, "isLastPeriod()?(C09-C10):0.0", null)
		
		// 12 Cuotas devengadas por entregas o transmisiones de activos fijos y por inversión del sujeto pasivo
		,C12(Mod421Key.C12,
				(mod, vat) -> (entregasActivosFijosFilterSimp(vat, mod) || operacionesISPFilterSimp(vat, mod)),
				(ctx, mod, vat) -> add(Mod421Key.C12, mod, vat.getDeductibleQuota()), null, null, null)
		
		// 13 Cuotas devengadas por arrendamiento de bienes inmuebles
		,C13(Mod421Key.C13)  // FALTA - VER SI SE PUEDE OBTENER DE ALGUNA FORMA
		
		// 14 Rectificación de cuotas impositivas repercutidas
		,C14(Mod421Key.C14)  // FALTA - VER SI SE PUEDE OBTENER DE ALGUNA FORMA
		
		// 15 Cuotas deducibles por adquisiciones o importaciones de activos fijos		
		,C15(Mod421Key.C15,
				(mod, vat) -> vat.isVatSimplifiedRegime(VATRegime.SIMPLIFIED) && !vat.isSales() && vat.isInvestment(),
				(ctx, mod, vat) -> add(Mod421Key.C15, mod, vat.getDeductibleQuota()), null, null, null)
		
		// 16 Cuotas deducibles correspondientes a la actividad de arrendamiento de bienes inmuebles
		,C16(Mod421Key.C16)  // FALTA - VER SI SE PUEDE OBTENER DE ALGUNA FORMA
		
		// 17 Cuotas del I.G.I.C. a compensar de peridos anteriores
		,C17(Mod421Key.C17, null, null, (ctx,mod) -> add( Mod421Key.C17, mod, getPendingCompesateAmounts( ctx, mod )), null, null)
		
		// 18 A deducir (exclusivamente en caso de autoliquidación complementaria)
		,C18(Mod421Key.C18, null, null, (ctx, mod) -> {
			if (mod.isComplementary()) {
				add(Mod421Key.C18, mod, Mod421DAO.getSamePeriodEffectiveModels(ctx, mod)
													.filter(fm -> fm.isToDeposit() || mod.isToPayback())	
													.mapToDouble(fm -> fm.getAmount(Mod421Key.C19)).sum());
			}
		  }
		,null
		,null)

		// 19 Resultado de la autoliquidación
		,C19(Mod421Key.C19, null, null, null, "isLastPeriod()?(C11+C12+C13-C14-C15-C16-C17-C18):(C06+C12+C13-C14-C15-C16-C17-C18)", null)
		
		;
		
		private Mod421Key key;
		private IValueAccepter acceptValue;
		private IValueIntializer initializer;
		private IValueFirstIntializer firstInitializer;
		private String expression;
		private String template;
		private ISimplifiedRegimeActivityPopulator populator;
		private ISimplifiedRegimeActivityFiller filler;
		private ISimplifiedRegimeCopier copier;

		private Mod421KeyDAO(Mod421Key key) {
			this(key, null, null, null, null, null);
		}

		private Mod421KeyDAO(Mod421Key key, IValueAccepter acceptValue, IValueIntializer initializer,
				IValueFirstIntializer firstInitializer, String expression, String template) {
			this(key, acceptValue, initializer, firstInitializer, expression, template, null, null, null);
		}

		private Mod421KeyDAO(Mod421Key key
			,IValueAccepter acceptValue
			,IValueIntializer initializer,
			IValueFirstIntializer firstInitializer
			,String expression, String template,
			ISimplifiedRegimeActivityPopulator populator
			,ISimplifiedRegimeActivityFiller filler,
			ISimplifiedRegimeCopier copier) {
			
			this.key = key;
			this.acceptValue = acceptValue;
			this.initializer = initializer;
			this.firstInitializer = firstInitializer;
			this.expression = expression;
			this.template = template;
			this.populator = populator;
			this.filler = filler;
			this.copier = copier;
		}

		@Override
		public Mod421Key getKey() {
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

		public boolean hasCopier() {
			return copier != null;
		}

		@Override
		public boolean acceptValue(Mod421 mod, VatContext vctx) {
			return acceptValue != null /* && acceptModel(mod) */ && acceptValue.accept(mod, vctx);
		}

		@Override
		public boolean hasAccepter() {
			return acceptValue != null;
		}

		@Override
		public void initialize(AONContext ctx, Mod421 mod, VatContext vctx) {
			if (initializer != null) {
				initializer.initialize(ctx, mod, vctx);
			}
		}

		@Override
		public void firstInitialize(AONContext ctx, Mod421 mod) {
			if (firstInitializer != null) {
				firstInitializer.initialize(ctx, mod);
			}
		}

		public void copy(Mod421 previous, Mod421 current) {
			if (hasCopier()) {
				copier.copy(previous, current);
			}
		}

		public void populate(Mod421 mod) {
			if (populator != null)
				populator.populate(mod);
		}

		public void fill(Mod421 mod) {
			if (filler != null)
				filler.fill(mod);
		}

		public static Mod421KeyDAO safeValueOf(String key) {
			if (AonStringUtils.isBlank(key))
				return null;
			for (Mod421KeyDAO keyDAO : Mod421KeyDAO.values()) {
				if (keyDAO.getKey().getValue().equals(key)) {
					return keyDAO;
				}
			}
			return null;
		}

	}

	@Override
	public IMod421KeyDAO[] getKeys() {
		return Mod421KeyDAO.values();
	}

	@Override
	public IMod421KeyDAO safeValueOf(Mod421 mod, String key) {
		return Mod421KeyDAO.safeValueOf(key);
	}

	@Override
	public IMod421KeyDAO valueOf(String keyValue) {
		return Mod421KeyDAO.valueOf(keyValue);
	}


	// -----------------------------------------------------------------------
	// --------------------------------------------------------------- FILTROS
	// -----------------------------------------------------------------------

	private static boolean entregasActivosFijosFilterSimp(VatContext vat, Mod421 mod) {
		return vat.isVatSimplifiedRegime(VATRegime.SIMPLIFIED) && vat.isSales() && vat.isInvestment();
	}

	private static boolean operacionesISPFilter(VatContext vat, Mod421 mod) {
		return !vat.isVatSurchargeRegime()
				&& (vat.isOtherISPPurchase() || vat.isOtherISPExpenses() || vat.isExtracommunityExpenses()
						|| vat.isCanCeuMelExpenses() || (vat.isExtracommunityPurchase() && vat.isService())
						|| (vat.isCanCeuMelPurchase() && vat.isService()));
	}

	private static boolean operacionesISPFilterSimp(VatContext vat, Mod421 mod) {
		return vat.isVatSimplifiedRegime(VATRegime.SIMPLIFIED) && (operacionesISPFilter(vat, mod)
				|| vat.isIntracommunityExpenses() || (vat.isService() && vat.isIntracommunityPurchase()));
	}

	public static boolean  ventasIntracomunitarias(VatContext vat, Mod421 mod) {
		return !vat.isVatSurchargeRegime() && vat.isIntracommunitySales();
	}
	public static boolean  ventasExtraComunitariasCanCeuBienes(VatContext vat, Mod421 mod) {
		return !vat.isVatSurchargeRegime() && !vat.isService() && (vat.isExtracommunitySales() || vat.isCanCeuMelSales()) && !vat.isSalesOSS();
	}
	public static boolean  ventasExtraComunitariasCanCeuServicios(VatContext vat, Mod421 mod) {
		boolean add = !vat.isVatSurchargeRegime() 
			&& ((vat.isService() && (vat.isExtracommunitySales() || vat.isCanCeuMelSales()))) && !vat.isSalesOSS();
		if ( add && mod.getYear() == 2021) {
			add = FiscalUtils.isInPeriodRange(mod, vat.getTaxDate());
		} 
		return add;
	}
	public static boolean  ventasISP(VatContext vat, Mod421 mod) {
		boolean add = !vat.isVatSurchargeRegime() && vat.isOtherISPSales();
		if ( add && mod.getYear() == 2021) {
			add = FiscalUtils.isInPeriodRange(mod, vat.getTaxDate());
		} 
		return add;
	}

	private static Mod421Activity ensureActivity(Mod421 mod, int idx) {
		if (idx < 0 || idx > 3)
			throw new IllegalArgumentException("0, 1, 2, o 3");

		if (mod.getActivityList() == null) {
			mod.setActivityList(new LinkedList<>());
		}
		for (int i = 0; i <= idx; i++) {
			if (idx == mod.getActivityList().size()) {
				mod.getActivityList().add(new Mod421Activity());
			}
		}
		return mod.getActivityList().get(idx);
	}

	private static Mod421ActivityModule ensureModule(Mod421 mod, int act, int idx) {
		if (idx < 0 || idx > 6) throw new IllegalArgumentException("0, 1, 2, 3, 4, 5, o 6");
		Mod421Activity a = ensureActivity(mod, act);
		if (a.getModules() == null) {
			a.setModules(new LinkedList<>());
		}
		for (int i = 0; i <= idx; i++) {
			if (idx == a.getModules().size()) {
				a.getModules().add(new Mod421ActivityModule());
			}
		}
		return a.getModules().get(idx);
	}

	@Override
	public Mod421Key[] getCompensationExplainKeys() {
		return COMPENSATION_EXPLAIN_KEYS;
	}
	@Override
	protected String getCompensationExplain( AONContext ctx, Mod421 mod421, Mod421Key key) {
		return DeclarationInfoUtil.getExplain( ctx, mod421, key, Mod421DAO.getLastPeriodEffectiveModels(ctx, mod421)
			, new ExplainRowManager() {
				@Override
				public String apply(FiscalModel fm) {
					setSomething(true);

					boolean isToCompensate = (fm.canBeSent() || fm.isSent()) && fm.getDeclarationResultType() == FiscalModelDeclarationType.COMPENSATE;
					if (isToCompensate) {
						sum(AonMathUtils.absRounded(fm.getDeclarationResult()));
					}
					
					StringBuilder sb = new StringBuilder().append("<tr>")
						.append( MessageFormat.format(styledTag, "td colspan=\"2\"",  textCenter+fontLarger+border+width500) )
							.append(fm.getModelFullName())
						.append("</td>")
					.append("</tr>")
					;
					if (isToCompensate) {
						sb.append("<tr>")
							.append( MessageFormat.format(styledTag, "td", paddingLeft+border) )
								.append("Resultado " +
									AonObjectUtils.defaultIfNull(fm.getDeclarationResultType(), t -> "(" + t.getDescription() + ")"))
							.append("</td>")
							.append( MessageFormat.format(styledTag, "td", textRight+width150+border) )				
								.append(DEC2.format(AonMathUtils.absRounded(fm.getDeclarationResult())))
							.append("</td>")
						.append("</tr>")					
						;
					}
					return sb.toString();
				}
			});	
	}
	
	@Override
	public Mod421Key[] getSamePeriodExplainKeys() {
		return SAME_PERIOD_EXPLAIN_KEYS;
	}
		
	@Override
	protected String getSamePeriodExplain( AONContext ctx, Mod421 mod421, Mod421Key key) {
		return DeclarationInfoUtil.getExplain( ctx, mod421, key, mod421.isComplementary() ? Mod421DAO.getSamePeriodEffectiveModels(ctx, mod421) : Stream.empty(), new ExplainRowManager());
	}
	
	// -----------------------------------------------------------------------
	// ----------------------------------------------- REGIMEN SIMPLIFICADO --
	// -----------------------------------------------------------------------
	@Override
	public void initializeSimplifiedRegime(AONContext ctx, Mod421 mod421) {
		Integer previousId = Mod421DAO.getMod421s(ctx, mod421.getDomain())
			.map( m -> m.getId())
			.findFirst().orElse(null);
		if (previousId != null) {
			Mod421 previous = Mod421DAO.get(ctx, previousId);
			Arrays.stream(Mod421KeyDAO.values())
					.filter( key -> key.hasCopier() )
					.forEach( key -> key.copy(previous, mod421));
			ensureSimplifiedRegimeActivities(mod421);
			fillSimplifiedRegime(mod421);
		}
	}

	private IEpigraphCanarias getEpigraph(Mod421 mod421, Mod421Key codeKey, Mod421Key specialKey) {
		return ModulesCanarias2026.EpigraphCanarias.getEpigraph(mod421.getDescription(codeKey), (int) mod421.getAmount(specialKey));
    }

	@Override
	public void fillSimplifiedRegime(Mod421 mod421) {
		for (Mod421KeyDAO key : Mod421KeyDAO.values()) {
			key.fill(mod421);
		}
	}

	@Override
	public void populateSimplifiedRegime(Mod421 mod421) {
		for (Mod421KeyDAO key : Mod421KeyDAO.values()) {
			key.populate(mod421);
		}
	}

	private static void copyKey(Mod421 previous, Mod421 current, Mod421Key key) {
		copyKey(previous, current, key, null);
	}
	
	private static void copyKey(Mod421 previous, Mod421 current, Mod421Key key, Mod421Key epigraphKey) {
		
		// Los epigrafes agrarios no copian el dato del modelo anterior (se usa en el importe del único módulo de los agrarios)
		// solo si no estamos haciendo una complementaria
		if (epigraphKey != null) {
			String epigraphCode = current.getDescription(epigraphKey);
			if (AonStringUtils.isNotBlank(epigraphCode) && epigraphCode.trim().startsWith("0") && (current.getYear() != previous.getYear() || current.getPeriod() != previous.getPeriod())) {
				return;
			}
		}
		
		current.putAmount(key, previous.getAmount(key));		
		current.putDescription(key, previous.getDescription(key));
		
	}
	
	private static void ensureActivityDays(Mod421 previous, Mod421 current, Mod421Key epiKey, Mod421Key daysKey) {
		if (AonStringUtils.isNotBlank(current.getDescription(epiKey))) {
			Date curStart = FiscalUtils.getPeriodStart(current);
			Date curEnd = FiscalUtils.getPeriodEnd(current);
			int curMaxDias = AonNumberUtils.toint(AonDateUtils.getDaysBetweenDates(curStart, curEnd)) + 1;
			
			// INICIALIZAR SIEMPRE CON LOS DIAS DEL TRIMESTRE, AUNQUE EN EL TRIMESTRE ANTERIOR SE MODIFICARA MANUALMENTE
			current.ensureDetail(daysKey).setAmount(curMaxDias);			
		}
	}

	private void ensureSimplifiedRegimeActivities(Mod421 mod421) {
		
		Mod421Key[][] actKeys = new Mod421Key[][]{
			new Mod421Key[] {Mod421Key.A1EP1,Mod421Key.A1EP2,Mod421Key.A1PCM,Mod421Key.A1POR},
			new Mod421Key[] {Mod421Key.A2EP1,Mod421Key.A2EP2,Mod421Key.A2PCM,Mod421Key.A2POR},
			new Mod421Key[] {Mod421Key.A3EP1,Mod421Key.A3EP2,Mod421Key.A3PCM,Mod421Key.A3POR},
			new Mod421Key[] {Mod421Key.A4EP1,Mod421Key.A4EP2,Mod421Key.A4PCM,Mod421Key.A4POR},
		};
		IntStream.range(0, actKeys.length)
			.boxed()
			.map(i -> actKeys[i])
			.filter(actActivity -> AonStringUtils.isNotBlank(mod421.getDescription(actActivity[0])))
			.forEach(actActivity -> {
				IEpigraphCanarias epi = getEpigraph(mod421, actActivity[0], actActivity[1]);
				if (epi != null) {
					mod421.ensureDetail(actActivity[2]).setAmount(epi.getPorcMin());
					if (!mod421.isLastPeriod()) {
						mod421.ensureDetail(actActivity[3]).setAmount(epi.getPorcIng());
					}
				}
			});
		
	}

	private static int checkSpecialEpigraph(Mod421 mod, Mod421Key specialEpigraphKey, Mod421Key codeKey, Mod421Key descriptionKey) {
		int specialEpigraph = (int) mod.getAmount(specialEpigraphKey);
		String code = mod.getDescription(codeKey);
		String description = mod.getDescription(descriptionKey);
		// Epígrafes que llevan indicador auxiliar, porque tienen el mismo código de epígrafe
		if (("16919".equals(code) || "1722".equals(code)) && (specialEpigraph == 0)) {
			return ModulesCanarias2026.EpigraphCanarias.getSpecialEpigraph(code, description);
		} else {
			return specialEpigraph;
		}
	}
	
	@Override
	public Mod421Key[] getIngresoCuentaAnteriorExplainKeys() {
		return INGRESO_CUENTA_ANTERIOR_EXPLAIN_KEYS;
	}

	@Override
	protected String getIngresoCuentaAnteriorExplain(AONContext ctx, Mod421 mod421, Mod421Key key) {
		return DeclarationInfoUtil.getExplain( ctx, mod421, key, 
				Mod421DAO.getPreviousEffectiveModels(ctx, mod421)
						 .sorted(Comparator.comparing(FiscalModel::getPeriod))
			, new ExplainRowManager() {
				@Override
				public String apply(FiscalModel fm) {
					setSomething(true);
					sum(fm.getAmount(Mod421Key.C06));
					StringBuilder sb = new StringBuilder();
					sb.append("<tr>")
						.append( MessageFormat.format(styledTag, "td", paddingLeft+border) )
							.append("Ingreso a Cuenta M" + fm.getModelFullName() + " [06]")
						.append("</td>")
						.append( MessageFormat.format(styledTag, "td", textRight+width150+border) )				
							.append(DEC2.format(AonMathUtils.round(fm.getAmount(Mod421Key.C06))))
						.append("</td>")
					.append("</tr>")					
					;
					return sb.toString();
				}
			});	
	}
	
}
