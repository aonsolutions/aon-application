package com.esferalia.aon.occam.impl.jooq.dao.mod390_2018;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.occam.api.model.fiscal.Activity;
import com.esferalia.aon.occam.api.model.fiscal.Address;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.mod390.DeductionRegime;
import com.esferalia.aon.occam.api.model.fiscal.mod390.FarmerRegimeActivity;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902018;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902018.Mod390Detail;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902018DetailKey;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Prorrata;
import com.esferalia.aon.occam.api.model.fiscal.mod390.SimpliedRegimeActivity;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.Administraciones;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.DatEstadisticos;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.DatEstadisticos.Conjunta;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.DatEstadisticos.OpTercerasPax;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.DatEstadisticos.Otras;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.DatEstadisticos.Pral;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.DatIdent;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.Devengo;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.Devengo.ConcursoAcreedoresNO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.Devengo.DecSustitutiva;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.Devengo.DestRegCriterioCajaNO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.Devengo.DestRegCriterioCajaSI;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.Devengo.RegCriterioCajaNO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.Devengo.RegCriterioCajaSI;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.Devengo.RegDevMensual;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.IVADeducibleGrupo1;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.IVADeducibleGrupo1.AdqIntracomunitarias;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.IVADeducibleGrupo1.Importaciones;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.IVADeducibleGrupo1.OpInteriores;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.IVADeducibleGrupo2;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.IVADeducibleGrupo3;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.LiqAnual;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.OpEspecificas;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.OpEspecificas.AdqCriterioCajaBase;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.OpEspecificas.EntregasCriterioCajaBase;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.Prorratas;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.Prorratas.Pro;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.BaseImponibleyCuota;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.BaseImponibleyCuota.AdqIntracomBienes;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.BaseImponibleyCuota.AdqIntracomServicios;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.BaseImponibleyCuota.IVAdevengadoInversionSP;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.BaseImponibleyCuota.ModBasesyCuotas;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.BaseImponibleyCuota.ModBasesyCuotasConcursoAcreedores;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.BaseImponibleyCuota.ModRecargoEquivalencia;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.BaseImponibleyCuota.ModRecargoEquivalenciaConcursoAcreedores;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.BaseImponibleyCuota.OpIntragrupo;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.BaseImponibleyCuota.RecargoEquivalencia;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.BaseImponibleyCuota.RegAgViajes;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.BaseImponibleyCuota.RegBienesUsados;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.BaseImponibleyCuota.RegCriterioCaja;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.BaseImponibleyCuota.RegOrdinario;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.BaseImponibleyCuota.TotalBasesyCuotasIVA;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.Deducciones;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.Deducciones.AdqIntracomunitariasBienesCorrientes;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.Deducciones.AdqIntracomunitariasBienesInversion;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.Deducciones.AdqIntracomunitariasServicios;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.Deducciones.ComRegAgricGanadPesca;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.Deducciones.ImportacionesBienesCorrientes;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.Deducciones.ImportacionesBienesInversion;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.Deducciones.OpInterioresBienesInversion;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.Deducciones.OpInterioresBienesServiciosCorrientes;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.Deducciones.OpIntragrupoBienesInversion;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.Deducciones.OpIntragrupoCorrientes;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.Deducciones.RectifDeducciones;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.Deducciones.RectifOpIntragrupo;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegSimplificado;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegSimplificado.ActAgricGanadForest;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegSimplificado.Actividad;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegSimplificado.Actividad.Modulo;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegSimplificado.IvaDeducible;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegSimplificado.IvaDevengado;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.ResLiquidaciones;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.ResLiquidaciones.PerNoRegGrupos;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.ResLiquidaciones.PerSiRegGrupos;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.VolOperaciones;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.TipoConcursoUltPer.ConcursoUltPerNO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.TipoConcursoUltPer.ConcursoUltPerSI;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.TipoGrupoEntidades.Art65NO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.TipoGrupoEntidades.Art65SI;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.TipoGrupoEntidades.Dependiente;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.TipoGrupoEntidades.Dominante;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.TipoGrupoEntidades.UltAutoliquidNO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.TipoGrupoEntidades.UltAutoliquidSI;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod390toAEATIVA2018 {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	public static AEATIVA2018 getAEATIVA2018(Mod3902018 mod390) {
		AEATIVA2018 iva = new AEATIVA2018();
		TipoDoc tipoDoc = new TipoDoc();
		tipoDoc.setCodModelo("390");
		tipoDoc.setEjercicio(mod390.getYear());
		iva.setIdDoc(tipoDoc);
		
		DatIdent datIdent = new DatIdent();
		
		if ( mod390.isLegalEntity() ) {
			TipoPersonaJuridica tpj = new TipoPersonaJuridica();
			TipoIdentificacionPersonaJuridica tipj = new TipoIdentificacionPersonaJuridica();
			tipj.setNIF(toUppercase(mod390.getDocument()));
			tipj.setRazonSocial(toUppercase(mod390.getName()));
			tpj.setIdentPersJuridica(tipj);
			datIdent.setPersJuridica(tpj);
		} else {
			TipoPersonaFisica tp = new TipoPersonaFisica();
			TipoIdentificacionPersonaFisica tipf = new TipoIdentificacionPersonaFisica();
			tipf.setNIF(mod390.getDocument());
			tipf.setNombre(toUppercase(mod390.getName()));
			tipf.setApe1(toUppercase(mod390.getFirstSurname()));
			if (AonStringUtils.isNotBlank(toUppercase(mod390.getSecondSurname()))) {
				tipf.setApe2(toUppercase(mod390.getSecondSurname()));
			}
			tp.setIdent(tipf);
			datIdent.setPersFisica(tp);
		}
		if (AonStringUtils.isNotEmpty(mod390.getContactPhone())) {
			datIdent.setTelefono(mod390.getContactPhone());
		}
		iva.setDatIdent(datIdent);
		
		iva.setDevengo(getDevengo(mod390));
		iva.setDatEstadisticos(getStatisticalData(mod390));
//		if (!mod390.isLegalEntity() || AonStringUtils.startsWith(mod390.getDocument(),"E")) {
			iva.setRepresentanteFisica( getRepresentanteFisica(mod390) );
//		} else {
			iva.getRepresentanteJuridica().addAll( getRepresentanteJuridica(mod390) );
//		}
		iva.setRegGeneral(getRegGeneral(mod390));
		
		iva.setRegSimplificado(getRegSimplificado(mod390));
		
		Administraciones adm = getAdministraciones(mod390);
		if (adm == null) {
			iva.setLiqAnual(getLiqAnual(mod390));
		} else {
			iva.setAdministraciones(adm);
		}
		iva.setResLiquidaciones(getResLiquidaciones(mod390));
		iva.setVolOperaciones(getVolOperaciones(mod390));
		iva.setOpEspecificas(getOpEspecificas(mod390));
		
		// PRORRATAS
		if (mod390.getProrratas() != null && mod390.getProrratas().size() > 0) {
			Prorratas prorratas = new Prorratas();
			for (Prorrata pro : mod390.getProrratas()) {
				if (AonStringUtils.isNotBlank( pro.getCnae())) {
					Pro p = new Pro();
					p.setActividad(  pro.getActivity()  );
					p.setCNAE( pro.getCnae() );
					if (pro.getAmount() != 0.0) {
						p.setImpOper( ensureBigDecimal( pro.getAmount() ) );
					}
					if (pro.getAmountWithRight() != 0.0) {
						p.setImpOperConDrchoDed( ensureBigDecimal( pro.getAmountWithRight() ));
					}
					p.setPorc( ensureBigDecimal( pro.getPercent() ));
					p.setTipo( pro.getType() );
					prorratas.getPro().add(p);	
				}
			}
			if (!prorratas.getPro().isEmpty())
				iva.setProrratas(prorratas);
		}
		if (mod390.getRegime1() != null) {
			DeductionRegime regime = mod390.getRegime1();
			IVADeducibleGrupo1 ivad = new IVADeducibleGrupo1();
			ivad.setOpInteriores(new OpInteriores());
			ivad.getOpInteriores().setBienesyServiciosCorrientes( getTipoBaseImponibleYCuota(regime.getBase1(), regime.getQuota1()) );
			ivad.getOpInteriores().setBienesInversion( getTipoBaseImponibleYCuota(regime.getBase2(), regime.getQuota2()) );
			ivad.setImportaciones(new Importaciones());
			ivad.getImportaciones().setBienesCorrientes( getTipoBaseImponibleYCuota(regime.getBase3(), regime.getQuota3()) );
			ivad.getImportaciones().setBienesInversion( getTipoBaseImponibleYCuota(regime.getBase4(), regime.getQuota4()) );
			ivad.setAdqIntracomunitarias(new AdqIntracomunitarias());
			ivad.getAdqIntracomunitarias().setBienesCorrientes( getTipoBaseImponibleYCuota(regime.getBase5(), regime.getQuota5()) );
			ivad.getAdqIntracomunitarias().setBienesInversion( getTipoBaseImponibleYCuota(regime.getBase6(), regime.getQuota6()) );
			ivad.setCompRegEspAgricGanadPesca( getTipoBaseImponibleYCuota(regime.getBase7(), regime.getQuota7()));
			ivad.setRectDeducciones( getTipoBaseImponibleYCuota(regime.getBase8(), regime.getQuota8()));
			ivad.setRegInversiones(ensureBigDecimal(regime.getQuota9()));
			ivad.setSumaDeducciones(ensureBigDecimal(regime.getQuota10()));
			iva.setIVADeducibleGrupo1( ivad );
		}
		if (mod390.getRegime2() != null) {
			DeductionRegime regime = mod390.getRegime2();
			IVADeducibleGrupo2 ivad = new IVADeducibleGrupo2();
			ivad.setOpInteriores(new com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.IVADeducibleGrupo2.OpInteriores());
			ivad.getOpInteriores().setBienesyServiciosCorrientes( getTipoBaseImponibleYCuota(regime.getBase1(), regime.getQuota1()) );
			ivad.getOpInteriores().setBienesInversion( getTipoBaseImponibleYCuota(regime.getBase2(), regime.getQuota2()) );
			ivad.setImportaciones(new com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.IVADeducibleGrupo2.Importaciones());
			ivad.getImportaciones().setBienesCorrientes( getTipoBaseImponibleYCuota(regime.getBase3(), regime.getQuota3()) );
			ivad.getImportaciones().setBienesInversion( getTipoBaseImponibleYCuota(regime.getBase4(), regime.getQuota4()) );
			ivad.setAdqIntracomunitarias(new com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.IVADeducibleGrupo2.AdqIntracomunitarias());
			ivad.getAdqIntracomunitarias().setBienesCorrientes( getTipoBaseImponibleYCuota(regime.getBase5(), regime.getQuota5()) );
			ivad.getAdqIntracomunitarias().setBienesInversion( getTipoBaseImponibleYCuota(regime.getBase6(), regime.getQuota6()) );
			ivad.setCompRegEspAgricGanadPesca( getTipoBaseImponibleYCuota(regime.getBase7(), regime.getQuota7()));
			ivad.setRectDeducciones( getTipoBaseImponibleYCuota(regime.getBase8(), regime.getQuota8()));
			ivad.setRegInversiones(ensureBigDecimal(regime.getQuota9()));
			ivad.setSumaDeducciones(ensureBigDecimal(regime.getQuota10()));
			iva.setIVADeducibleGrupo2( ivad );
		}
		if (mod390.getRegime3() != null) {
			DeductionRegime regime = mod390.getRegime3();
			IVADeducibleGrupo3 ivad = new IVADeducibleGrupo3();
			ivad.setOpInteriores(new com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.IVADeducibleGrupo3.OpInteriores());
			ivad.getOpInteriores().setBienesyServiciosCorrientes( getTipoBaseImponibleYCuota(regime.getBase1(), regime.getQuota1()) );
			ivad.getOpInteriores().setBienesInversion( getTipoBaseImponibleYCuota(regime.getBase2(), regime.getQuota2()) );
			ivad.setImportaciones(new com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.IVADeducibleGrupo3.Importaciones());
			ivad.getImportaciones().setBienesCorrientes( getTipoBaseImponibleYCuota(regime.getBase3(), regime.getQuota3()) );
			ivad.getImportaciones().setBienesInversion( getTipoBaseImponibleYCuota(regime.getBase4(), regime.getQuota4()) );
			ivad.setAdqIntracomunitarias(new com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.IVADeducibleGrupo3.AdqIntracomunitarias());
			ivad.getAdqIntracomunitarias().setBienesCorrientes( getTipoBaseImponibleYCuota(regime.getBase5(), regime.getQuota5()) );
			ivad.getAdqIntracomunitarias().setBienesInversion( getTipoBaseImponibleYCuota(regime.getBase6(), regime.getQuota6()) );
			ivad.setCompRegEspAgricGanadPesca( getTipoBaseImponibleYCuota(regime.getBase7(), regime.getQuota7()));
			ivad.setRectDeducciones( getTipoBaseImponibleYCuota(regime.getBase8(), regime.getQuota8()));
			ivad.setRegInversiones(ensureBigDecimal(regime.getQuota9()));
			ivad.setSumaDeducciones(ensureBigDecimal(regime.getQuota10()));
			iva.setIVADeducibleGrupo3( ivad );
		}
		
		return iva;
	}

	private static RegSimplificado getRegSimplificado(Mod3902018 mod390) {
		RegSimplificado reg = null;
		boolean something = false;
		if (mod390.getSimpRegime1() != null && AonStringUtils.isNotBlank( mod390.getSimpRegime1().getEpigrafe())) {
			Actividad actividad = getActividad(mod390.getSimpRegime1());
			reg = new RegSimplificado();
			reg.getActividad().add(actividad);
			something = true;
		}
		if (mod390.getSimpRegime2() != null && AonStringUtils.isNotBlank( mod390.getSimpRegime2().getEpigrafe())) {
			Actividad actividad = getActividad(mod390.getSimpRegime2());
			reg = reg==null?new RegSimplificado():reg;
			reg.getActividad().add(actividad);
			something = true;
		}
		if (mod390.getFarmerRegime1() != null && AonStringUtils.isNotBlank( mod390.getFarmerRegime1().getCodigo()) ) {
			ActAgricGanadForest act = getActAgricGanadForest(mod390.getFarmerRegime1());
			reg = reg==null?new RegSimplificado():reg;
			reg.getActAgricGanadForest().add(act);
			something = true;
		}
		if (mod390.getFarmerRegime2() != null && AonStringUtils.isNotBlank( mod390.getFarmerRegime2().getCodigo())) {
			ActAgricGanadForest act = getActAgricGanadForest(mod390.getFarmerRegime2());
			reg = reg==null?new RegSimplificado():reg;
			reg.getActAgricGanadForest().add(act);
			something = true;
		}
		if (mod390.getFarmerRegime3() != null && AonStringUtils.isNotBlank( mod390.getFarmerRegime3().getCodigo())) {
			ActAgricGanadForest act = getActAgricGanadForest(mod390.getFarmerRegime3());
			reg = reg==null?new RegSimplificado():reg;
			reg.getActAgricGanadForest().add(act);
			something = true;
		}
		if (mod390.getFarmerRegime4() != null && AonStringUtils.isNotBlank( mod390.getFarmerRegime4().getCodigo())) {
			ActAgricGanadForest act = getActAgricGanadForest(mod390.getFarmerRegime4());
			reg = reg==null?new RegSimplificado():reg;
			reg.getActAgricGanadForest().add(act);
			something = true;
		}
		if (mod390.getFarmerRegime5() != null && AonStringUtils.isNotBlank( mod390.getFarmerRegime5().getCodigo())) {
			ActAgricGanadForest act = getActAgricGanadForest(mod390.getFarmerRegime5());
			reg = reg==null?new RegSimplificado():reg;
			reg.getActAgricGanadForest().add(act);
			something = true;
		}
		if (something) {
			IvaDevengado ivaDev = new IvaDevengado();
			if ( mod390.getBox74() != 0.00 ) {
				ivaDev.setSumaCuotasNoAgric(ensureBigDecimal(mod390.getBox74()));
			}
			ivaDev.setSumaCuotasAgric(ensureBigDecimal(mod390.getBox75()));
			ivaDev.setAdqIntracomunitarias(ensureBigDecimal(mod390.getBox76()));
			ivaDev.setInversionSujetoPasivo(ensureBigDecimal(mod390.getBox77()));
			ivaDev.setEntregasActivosFijos(ensureBigDecimal(mod390.getBox78()));
			ivaDev.setTotalCuota(ensureBigDecimal(mod390.getBox79()));
			reg.setIvaDevengado(ivaDev);
			IvaDeducible ivaDed = new IvaDeducible();
			ivaDed.setIVASoportadoAdqActivosFijos(ensureBigDecimal(mod390.getBox80()));
			ivaDed.setRegBienesInversion(ensureBigDecimal(mod390.getBox81()));
			ivaDed.setSumaDeducciones(ensureBigDecimal(mod390.getBox82()));
			reg.setIvaDeducible(ivaDed);
			
			reg.setResRegimenSimplificado(ensureBigDecimal(mod390.getBox83()));
		}
		return reg;
	}

	private static ActAgricGanadForest getActAgricGanadForest(FarmerRegimeActivity ac) {
		ActAgricGanadForest act = new ActAgricGanadForest();
		act.setCodigo(ac.getCodigo() );
		if (ac.getIncomes() != 0.0) {
			act.setVolIngresos(ensureBigDecimal( ac.getIncomes()));
		}
		act.setIndCuota(ensureBigDecimal( ac.getQuotaIndex(),4));
		if (ac.getAccrualQuota() != 0.0) {
			act.setCuotaDevengada(ensureBigDecimal( ac.getAccrualQuota()));
		}
		if (ac.getInputQuotas() != 0.0) {
			act.setCuotasSoportadas(ensureBigDecimal( ac.getInputQuotas()));
		}
		act.setCuotaRegSimplificado(ensureBigDecimal( ac.getQuota()));
		return act;
	}

	private static Actividad getActividad(SimpliedRegimeActivity sr) {
		Actividad actividad = new Actividad();
		actividad.setEpigrafe(sr.getEpigrafe());
		List<Modulo> modulos = actividad.getModulo();
		Modulo modulo;
		if (sr.getAmount1() != 0) {
			modulo = new Modulo();
			modulo.setNumModulo("1");
			modulo.setUnidades( ensureBigDecimal(sr.getUnit1()));
			modulo.setImporte( ensureBigDecimal(sr.getAmount1()));
			modulos.add(modulo);
		}
		if (sr.getAmount2() != 0) {
			modulo = new Modulo();
			modulo.setNumModulo("2");
			modulo.setUnidades( ensureBigDecimal(sr.getUnit2()));
			modulo.setImporte( ensureBigDecimal(sr.getAmount2()));
			modulos.add(modulo);
		}
		if (sr.getAmount3() != 0) {
			modulo = new Modulo();
			modulo.setNumModulo("3");
			modulo.setUnidades( ensureBigDecimal(sr.getUnit3()));
			modulo.setImporte( ensureBigDecimal(sr.getAmount3()));
			modulos.add(modulo);
		}
		if (sr.getAmount4() != 0) {
			modulo = new Modulo();
			modulo.setNumModulo("4");
			modulo.setUnidades( ensureBigDecimal(sr.getUnit4()));
			modulo.setImporte( ensureBigDecimal(sr.getAmount4()));
			modulos.add(modulo);
		}
		if (sr.getAmount5() != 0) {
			modulo = new Modulo();
			modulo.setNumModulo("5");
			modulo.setUnidades( ensureBigDecimal(sr.getUnit5()));
			modulo.setImporte( ensureBigDecimal(sr.getAmount5()));
			modulos.add(modulo);
		}
		if (sr.getAmount6() != 0) {
			modulo = new Modulo();
			modulo.setNumModulo("6");
			modulo.setUnidades( ensureBigDecimal(sr.getUnit6()));
			modulo.setImporte( ensureBigDecimal(sr.getAmount6()));
			modulos.add(modulo);
		}
		if (sr.getAmount7() != 0) {
			modulo = new Modulo();
			modulo.setNumModulo("7");
			modulo.setUnidades( ensureBigDecimal(sr.getUnit7()));
			modulo.setImporte( ensureBigDecimal(sr.getAmount7()));
			modulos.add(modulo);
		}
		if ( sr.getBoxC() != 0.00 ) {
			actividad.setCuotaDevengada(ensureBigDecimal(sr.getBoxC()));
		}
		if ( sr.getBoxC1() != 0.00 ) {
			actividad.setLorca2013(ensureBigDecimal(sr.getBoxC1()));
		}
		if ( sr.getBoxD() != 0.00 ) {
			actividad.setCuotaSoportada(ensureBigDecimal(sr.getBoxD()));
		}
		actividad.setIndiceCorrector(ensureBigDecimal(sr.getBoxE()));
		actividad.setResultado(ensureBigDecimal(sr.getBoxF()));
		actividad.setPorcCuotaMinima(ensureBigDecimal(sr.getBoxG()));
		if ( sr.getBoxH() != 0.00 ) {
			actividad.setDevCuotaSopOtrosPaises(ensureBigDecimal(sr.getBoxH()));
		}
		if ( sr.getBoxI() != 0.00 ) {
			actividad.setCuotaMinima(ensureBigDecimal(sr.getBoxI()));
		}
		if ( sr.getBoxJ() != 0.00 ) {
			actividad.setCuotaRegSimplificado(ensureBigDecimal(sr.getBoxJ()));
		}
		return actividad;
	}

	private static String toUppercase(String data) {
		return data==null?null:data.toUpperCase();
	}
	private static BigDecimal ensureBigDecimal(double d) {
		return ensureBigDecimal(d,2);
	}
	private static BigDecimal ensureBigDecimal(double d, int scale) {
		return new BigDecimal(Double.toString(d)).setScale(scale,RoundingMode.HALF_UP);
	}

	private static Devengo getDevengo(Mod3902018 mod390) {
		Devengo devengo = new Devengo();
		devengo.setEjercicio(mod390.getYear());
		
		if (mod390.isInsolvencyStateThisYear()) {
			TipoConcursoUltPer tipo = new TipoConcursoUltPer();
			if (mod390.isInsolvencyStateLastPeriod()) {
				tipo.setConcursoUltPerSI( new ConcursoUltPerSI());	
			} else {
				tipo.setConcursoUltPerNO( new ConcursoUltPerNO());
			}
			devengo.setConcursoAcreedoresSI( tipo );	
		} else {
			devengo.setConcursoAcreedoresNO( new ConcursoAcreedoresNO());
		}
        
		if (mod390.isAccrualRegime()) {
			devengo.setRegCriterioCajaSI(new RegCriterioCajaSI());
		} else {
			devengo.setRegCriterioCajaNO(new RegCriterioCajaNO());
		}
		if (mod390.isAccrualRegimeTarget()) {
			devengo.setDestRegCriterioCajaSI(new DestRegCriterioCajaSI());
		} else {
			devengo.setDestRegCriterioCajaNO(new DestRegCriterioCajaNO());
		}

		if (mod390.isTaxRefund()) {
			devengo.setRegDevMensual(new RegDevMensual());
		}
		if (mod390.isReplacement()) {
			devengo.setDecSustitutiva( new DecSustitutiva() );
			devengo.setJustDecAnterior( mod390.getReplacedReceipt() );
		}
		if (mod390.isSpecialGroupRegime()) {
			TipoGrupoEntidades tge = new TipoGrupoEntidades();
			tge.setNumGrupo(mod390.getGroupNumber());
			if (mod390.isGroupDependent()) {
				tge.setDependiente(new Dependiente());	
			} else {
				tge.setDominante(new Dominante());
			}
			if (mod390.isGroupRegimeType()) {
				tge.setArt65SI( new Art65SI());
				tge.setNIFEntidadDominante(mod390.getGroupDocument());
			} else {
				tge.setArt65NO( new Art65NO());
			}
			if (mod390.isGroupDeclarations()) {
				tge.setUltAutoliquidSI(new UltAutoliquidSI());
			} else {
				tge.setUltAutoliquidNO(new UltAutoliquidNO());
			}
			devengo.setRegGrupoEntidades(tge);
		}
		if (mod390.isAccrualRegime()) {
			devengo.setRegCriterioCajaSI(new RegCriterioCajaSI());
		} else {
			devengo.setRegCriterioCajaNO(new RegCriterioCajaNO());
		}
		if (mod390.isAccrualRegimeTarget()){
			devengo.setDestRegCriterioCajaSI(new DestRegCriterioCajaSI());
		} else {
			devengo.setDestRegCriterioCajaNO(new DestRegCriterioCajaNO());
		}
		return devengo;
	}
	
	private static DatEstadisticos getStatisticalData(Mod3902018 mod390) {
		DatEstadisticos datEstadisticos = new DatEstadisticos();
		
		if (mod390.getMainActivity() != null) {
			Pral pral = new Pral();
			pral.setClave(mod390.getMainActivity().getKey());
			pral.setDescripcion(mod390.getMainActivity().getDescription());
			pral.setEpigrafe(mod390.getMainActivity().getEpigraph());
			datEstadisticos.setPral(pral);
		}
		Activity[] activities = new Activity[]{
				mod390.getActivity1(),
				mod390.getActivity2(),
				mod390.getActivity3(),
				mod390.getActivity4(),
				mod390.getActivity5()
		};
		for (Activity activity : activities) {
			if (activity != null) {
				Otras otras = new Otras();
				otras.setClave(activity.getKey());
				otras.setDescripcion(activity.getDescription());
				otras.setEpigrafe(activity.getEpigraph());
				datEstadisticos.getOtras().add(otras);
			}
		}
		if (mod390.isMod347()) {
			datEstadisticos.setOpTercerasPax(new OpTercerasPax());
		}
		if (AonStringUtils.isNotEmpty(mod390.getMergedDeclarationDocument())) {
			Conjunta conjunta = new Conjunta();
			conjunta.setNIF(mod390.getMergedDeclarationDocument());
			conjunta.setRazonSocial(mod390.getMergedDeclarationName());
			datEstadisticos.setConjunta(conjunta);
		}
		return datEstadisticos;
	}

	private static ArrayList<TipoRepresentanteJuridica> getRepresentanteJuridica(Mod3902018 mod390) {
		ArrayList<TipoRepresentanteJuridica> list = new ArrayList<TipoRepresentanteJuridica>();
		LegalRepresentative[] lrs = new LegalRepresentative[] {
			mod390.getLegalRepr1(),	
			mod390.getLegalRepr2(),
			mod390.getLegalRepr3()
		};
		for (LegalRepresentative lr : lrs) {
			if (lr != null) {
				TipoRepresentanteJuridica trj = new TipoRepresentanteJuridica();
				trj.setNIF(toUppercase(lr.getDocument()));
				trj.setNombre(toUppercase(lr.getName()));
				trj.setNotaria(toUppercase(lr.getNotary()));
				if (lr.getNotaryDate() != null) {
					trj.setFechaPoder( DATE_FORMAT.format(lr.getNotaryDate()) );	
				} else {
					trj.setFechaPoder( null );
				}
				list.add(trj);
			}
		}
		return list;
	}

	private static TipoRepresentanteFisica getRepresentanteFisica(Mod3902018 mod390) {
		Address address = mod390.getAddress();
		TipoRepresentanteFisica trf = null;
		if (address != null) {
			trf = new TipoRepresentanteFisica();
			TipoIdentificacionPersonaJuridica tipf = new TipoIdentificacionPersonaJuridica();
			tipf.setNIF(address.getRdocument());
			tipf.setRazonSocial(toUppercase(address.getRname()));
			trf.setIdent(tipf);
			TipoDomicilio domicilio = new TipoDomicilio();
			boolean something = false;
			if (AonStringUtils.isNotEmpty(address.getRstreetName())) {
				domicilio.setViaPublica(toUppercase(address.getRstreetName()));
				something = true;
			}
			if (AonStringUtils.isNotEmpty(address.getRstreetType())) { 
				domicilio.setSG(toUppercase(address.getRstreetType()));
				something = true;
			}
			if (AonStringUtils.isNotEmpty(address.getRstreetNumber())) {
				domicilio.setNum(toUppercase(address.getRstreetNumber()));
				something = true;
			}
			if (AonStringUtils.isNotEmpty(address.getRstreetStair())) {
				domicilio.setEsc(toUppercase(address.getRstreetStair()));
				something = true;
			}
			if (AonStringUtils.isNotEmpty(address.getRstreetFloor())) {
				domicilio.setPiso(toUppercase(address.getRstreetFloor()));
				something = true;
			}
			if (AonStringUtils.isNotEmpty(address.getRstreetDoor())) {
				domicilio.setPuerta(toUppercase(address.getRstreetDoor()));
				something = true;
			}
			if (AonStringUtils.isNotEmpty(address.getRphone())) {
				domicilio.setTelefono(toUppercase(address.getRphone()));
				something = true;
			}
			if (AonStringUtils.isNotEmpty(address.getRzip())) {
				domicilio.setCPostal(toUppercase(address.getRzip()));
				something = true;
			}
			if (AonStringUtils.isNotEmpty(address.getRtown())) {
				domicilio.setMunicipio(toUppercase(address.getRtown()));
				something = true;
			}
			if (address.getRprovince() != 0) {
				domicilio.setCodProv(Integer.toString(address.getRprovince()));
				something = true;
			}
			if (something) {
				trf.setDomicilio(domicilio);
				something = true;
			}
		}
		return trf;
	}

	private static RegGeneral getRegGeneral(Mod3902018 mod390) {
		RegGeneral regGeneral = new RegGeneral();
		BaseImponibleyCuota bases = getBaseImponibleyCuota(mod390);
		regGeneral.setBaseImponibleyCuota(bases);
		Deducciones ded = getDeducciones(mod390); 
		regGeneral.setDeducciones(ded);
		regGeneral.setResRegGeneral(getResRegGeneral(mod390));
		return regGeneral;
	}
	
	private static String getResRegGeneral(Mod3902018 mod390) {
		Mod390Detail detail = getKey(mod390,Mod3902018DetailKey.C0065);
		String total = null;
		if (detail != null) {
			total = Double.toString( AonMathUtils.round(detail.getQuota()) ); 			
		}
		return total;
	}


	private static Deducciones getDeducciones(Mod3902018 mod390) {
		Deducciones deducciones = new Deducciones();
		deducciones.setOpInterioresBienesServiciosCorrientes(getOpInterioresBienesServiciosCorrientes(mod390));
		deducciones.setOpIntragrupoCorrientes(getOpIntragrupoCorrientes(mod390));
		deducciones.setOpInterioresBienesInversion(getOpInterioresBienesInversion(mod390));
		deducciones.setOpIntragrupoBienesInversion(getOpIntragrupoBienesInversion(mod390));
		deducciones.setImportacionesBienesCorrientes(getImportacionesBienesCorrientes(mod390));
		deducciones.setImportacionesBienesInversion(getImportacionesBienesInversion(mod390));
		deducciones.setAdqIntracomunitariasBienesCorrientes(getAdqIntracomunitariasBienesCorrientes(mod390));
		deducciones.setAdqIntracomunitariasBienesInversion(getAdqIntracomunitariasBienesInversion(mod390));
		deducciones.setAdqIntracomunitariasServicios(getAdqIntracomunitariasServicios(mod390));
		deducciones.setComRegAgricGanadPesca(getComRegAgricGanadPesca(mod390));
		deducciones.setRectifDeducciones(getRectifDeducciones(mod390));
		deducciones.setRectifOpIntragrupo(getRectifOpIntragrupo(mod390));
		deducciones.setRegularizInversiones(getRegularizInversiones(mod390));
		deducciones.setRegularizPorcProrrata(getRegularizPorcProrrata(mod390));
		deducciones.setSumDeducciones(getSumDeducciones(mod390));
		return deducciones;
	}

	private static BigDecimal getSumDeducciones(Mod3902018 mod390) {
		BigDecimal op = null;
		Mod390Detail detail = getKey(mod390,Mod3902018DetailKey.C0064);
		if (detail != null) {
			op = ensureBigDecimal(detail.getQuota());
		}
		return op;
	}


	private static BigDecimal getRegularizPorcProrrata(Mod3902018 mod390) {
		BigDecimal op = null;
		Mod390Detail detail = getKey(mod390,Mod3902018DetailKey.C0522);
		if (detail != null) {
			op = ensureBigDecimal(detail.getQuota());
		}
		return op;
	}


	private static BigDecimal getRegularizInversiones(Mod3902018 mod390) {
		BigDecimal op = null;
		Mod390Detail detail = getKey(mod390,Mod3902018DetailKey.C0063);
		if (detail != null) {
			op = ensureBigDecimal(detail.getQuota());
		}
		return op;
	}


	private static RectifDeducciones getRectifDeducciones(Mod3902018 mod390) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0062));
		if (tipo != null) {
			RectifDeducciones op = new RectifDeducciones();
			op.setTipoX(tipo);
			return op;
		}
		return null;
	}

	private static RectifOpIntragrupo getRectifOpIntragrupo(Mod3902018 mod390) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0652));
		if (tipo != null) {
			RectifOpIntragrupo op = new RectifOpIntragrupo();
			op.setTipoX(tipo);
			return op;
		}
		return null;
	}

	private static ComRegAgricGanadPesca getComRegAgricGanadPesca(Mod3902018 mod390) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0061));
		if (tipo != null) {
			ComRegAgricGanadPesca op = new ComRegAgricGanadPesca();
			op.setTipoX(tipo);
			return op;
		}
		return null;
	}


	private static AdqIntracomunitariasServicios getAdqIntracomunitariasServicios(Mod3902018 mod390) {
		AdqIntracomunitariasServicios op = new AdqIntracomunitariasServicios();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0588)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0636)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0638)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0598)));
		return op;
	}


	private static AdqIntracomunitariasBienesInversion getAdqIntracomunitariasBienesInversion(Mod3902018 mod390) {
		AdqIntracomunitariasBienesInversion op = new AdqIntracomunitariasBienesInversion();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0221)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0632)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0634)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0059)));
		return op;
	}


	private static AdqIntracomunitariasBienesCorrientes getAdqIntracomunitariasBienesCorrientes(Mod3902018 mod390) {
		AdqIntracomunitariasBienesCorrientes op = new AdqIntracomunitariasBienesCorrientes();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0215)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0628)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0630)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0057)));
		return op;
	}


	private static ImportacionesBienesInversion getImportacionesBienesInversion(Mod3902018 mod390) {
		ImportacionesBienesInversion op = new ImportacionesBienesInversion();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0209)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0624)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0626)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0055)));
		return op;
	}


	private static ImportacionesBienesCorrientes getImportacionesBienesCorrientes(Mod3902018 mod390) {
		ImportacionesBienesCorrientes op = new ImportacionesBienesCorrientes();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0203)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0620)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0622)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0053)));
		return op;
	}


	private static OpIntragrupoBienesInversion getOpIntragrupoBienesInversion(Mod3902018 mod390) {
		OpIntragrupoBienesInversion op = new OpIntragrupoBienesInversion();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0515)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0616)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0618)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0521)));
		return op;
	}


	private static OpInterioresBienesInversion getOpInterioresBienesInversion(Mod3902018 mod390) {
		OpInterioresBienesInversion op = new OpInterioresBienesInversion();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0197)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0612)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0614)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0051)));
		return op;
	}


	private static OpIntragrupoCorrientes getOpIntragrupoCorrientes(Mod3902018 mod390) {
		OpIntragrupoCorrientes op = new OpIntragrupoCorrientes();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0507)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0608)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0610)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0513)));
		return op;
	}


	private static OpInterioresBienesServiciosCorrientes getOpInterioresBienesServiciosCorrientes(Mod3902018 mod390) {
		OpInterioresBienesServiciosCorrientes op = new OpInterioresBienesServiciosCorrientes();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0191)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0604)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0606)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0049)));
		return op;
	}


	private static BaseImponibleyCuota getBaseImponibleyCuota(Mod3902018 mod390) {
		BaseImponibleyCuota b = new BaseImponibleyCuota();
		b.setRegOrdinario(getRegOrdinario(mod390));
		b.setRegCriterioCaja(getRegCriterioCaja(mod390));
		b.setOpIntragrupo(getOpIntragrupo(mod390));
		b.setRegBienesUsados(getRegBienesUsados(mod390));
		b.setRegAgViajes(getRegAgViajes(mod390));
		b.setAdqIntracomBienes(getAdqIntracomBienes(mod390));
		b.setAdqIntracomServicios(getAdqIntracomServicios(mod390));
		b.setIVAdevengadoInversionSP(getIVAdevengadoInversionSP(mod390));
		b.setModBasesyCuotas(getModBasesyCuotas(mod390));
		b.setModBasesyCuotasConcursoAcreedores(getModBasesyCuotasConcursoAcreedores(mod390));
		b.setTotalBasesyCuotasIVA(getTotalBasesyCuotasIVA(mod390));
		b.setRecargoEquivalencia(getRecargoEquivalencia(mod390));
		b.setModRecargoEquivalencia(getModRecargoEquivalencia(mod390));
		b.setModRecargoEquivalenciaConcursoAcreedores(getModRecargoEquivalenciaConcursoAcreedores(mod390));
		b.setTotalCuotasIVA(getTotalCuotasIVA(mod390));
		return b;
	}


	private static BigDecimal getTotalCuotasIVA(Mod3902018 mod390) {
		Mod390Detail detail = getKey(mod390,Mod3902018DetailKey.C0047);
		BigDecimal totalCuotasIVA = null; 
		if (detail != null) {
			totalCuotasIVA = ensureBigDecimal(detail.getQuota()); 			
		}
		return totalCuotasIVA;
	}


	private static ModRecargoEquivalenciaConcursoAcreedores getModRecargoEquivalenciaConcursoAcreedores(Mod3902018 mod390) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0046));
		if (tipo != null) {
			ModRecargoEquivalenciaConcursoAcreedores modRecargoEquivalenciaConcursoAcreedores = new ModRecargoEquivalenciaConcursoAcreedores();
			modRecargoEquivalenciaConcursoAcreedores.setTipoX(tipo);
			return modRecargoEquivalenciaConcursoAcreedores;
		}
		return null;
	}


	private static ModRecargoEquivalencia getModRecargoEquivalencia(Mod3902018 mod390) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0044));
		if (tipo != null) {
			ModRecargoEquivalencia modRecargoEquivalencia = new ModRecargoEquivalencia();
			modRecargoEquivalencia.setTipoX(tipo);
			return modRecargoEquivalencia;
		}
		return null;
	}


	private static RecargoEquivalencia getRecargoEquivalencia(Mod3902018 mod390) {
		RecargoEquivalencia recargoEquivalencia = new RecargoEquivalencia();
		recargoEquivalencia.setTipo05(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0036)));
		recargoEquivalencia.setTipo14(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0600)));
		recargoEquivalencia.setTipo175(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0042)));
		recargoEquivalencia.setTipo52(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0602)));
		return recargoEquivalencia;
	}


	private static TotalBasesyCuotasIVA getTotalBasesyCuotasIVA(Mod3902018 mod390) {
		TotalBasesyCuotasIVA totalBasesyCuotasIVA = new TotalBasesyCuotasIVA();
		totalBasesyCuotasIVA.setTipoX(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0034)));
		return totalBasesyCuotasIVA;
	}


	private static ModBasesyCuotasConcursoAcreedores getModBasesyCuotasConcursoAcreedores(
			Mod3902018 mod390) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0032));
		if (tipo != null) {
			ModBasesyCuotasConcursoAcreedores modBases = new ModBasesyCuotasConcursoAcreedores();
			modBases.setTipoX(tipo);
			return modBases;
		}
		return null;
	}


	private static ModBasesyCuotas getModBasesyCuotas(Mod3902018 mod390) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0030));
		if (tipo != null) {
			ModBasesyCuotas modBasesyCuotas = new ModBasesyCuotas();
			modBasesyCuotas.setTipoX(tipo);
			return modBasesyCuotas;
		}
		return null;
	}


	private static IVAdevengadoInversionSP getIVAdevengadoInversionSP(Mod3902018 mod390) {
		IVAdevengadoInversionSP iVAdevengadoInversionSP = new IVAdevengadoInversionSP();
		iVAdevengadoInversionSP.setTipoX(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0028)));
		return iVAdevengadoInversionSP;
	}


	private static AdqIntracomServicios getAdqIntracomServicios(Mod3902018 mod390) {
		AdqIntracomServicios adqIntracomServicios = new AdqIntracomServicios();
		adqIntracomServicios.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0546)));
		adqIntracomServicios.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0548)));
		adqIntracomServicios.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0552)));
		return adqIntracomServicios;
	}


	private static AdqIntracomBienes getAdqIntracomBienes(Mod3902018 mod390) {
		AdqIntracomBienes adqIntracomBienes = new AdqIntracomBienes();
		adqIntracomBienes.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0022)));
		adqIntracomBienes.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0024)));
		adqIntracomBienes.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0026)));
		return adqIntracomBienes;
	}


	private static RegAgViajes getRegAgViajes(Mod3902018 mod390) {
		RegAgViajes regAgViajes = new RegAgViajes();
		regAgViajes.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0014)));
		return regAgViajes;
	}


	private static RegBienesUsados getRegBienesUsados(Mod3902018 mod390) {
		RegBienesUsados regBienesUsados = new RegBienesUsados();
		regBienesUsados.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0008)));
		regBienesUsados.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0010)));
		regBienesUsados.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0012)));
		return regBienesUsados;
	}


	private static OpIntragrupo getOpIntragrupo(Mod3902018 mod390) {
		OpIntragrupo opIntragrupo = new OpIntragrupo();
		opIntragrupo.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0501)));
		opIntragrupo.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0503)));
		opIntragrupo.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0505)));
		return opIntragrupo;
	}


	private static RegOrdinario getRegOrdinario(Mod3902018 mod390) {
		RegOrdinario regOrdinario = new RegOrdinario();
		regOrdinario.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0002)));
		regOrdinario.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0004)));
		regOrdinario.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0006)));
		return regOrdinario;
	}

	private static RegCriterioCaja getRegCriterioCaja(Mod3902018 mod390) {
		RegCriterioCaja regCriterioCaja = new RegCriterioCaja();
		regCriterioCaja.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0644)));
		regCriterioCaja.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0646)));
		regCriterioCaja.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod3902018DetailKey.C0648)));
		return regCriterioCaja;
	}

	private static Mod390Detail getKey(Mod3902018 mod390, Mod3902018DetailKey key) {
		if (mod390 != null && mod390.getGeneralRegime() != null) {
			return mod390.getGeneralRegime().get(key);
		}
		return null;
	}

	private static TipoBaseImponibleYCuota getTipoBaseImponibleYCuota(Mod390Detail detail) {
		TipoBaseImponibleYCuota tipo = new TipoBaseImponibleYCuota();
		if (detail!= null && (detail.getTaxableBase() != 0 || detail.getQuota() != 0)) {
			tipo.setBI(ensureBigDecimal(detail.getTaxableBase()));
			tipo.setCuota(ensureBigDecimal(detail.getQuota()));
		} else {
			tipo.setBI(ensureBigDecimal(0));
			tipo.setCuota(ensureBigDecimal(0));
		}
		return tipo;
	}

	private static TipoBaseImponibleYCuota getTipoBaseImponibleYCuota(double base, double quota) {
		TipoBaseImponibleYCuota tipo = new TipoBaseImponibleYCuota();
		if (base != 0 || quota != 0) {
			tipo.setBI(ensureBigDecimal(base));
			tipo.setCuota(ensureBigDecimal(quota));
		} else {
			tipo.setBI(ensureBigDecimal(0));
			tipo.setCuota(ensureBigDecimal(0));
		}
		return tipo;
	}

	private static Administraciones getAdministraciones(Mod3902018 mod390) {
		if (mod390.getBox87() > 0.0 && mod390.getBox87() < 100.0) {
			Administraciones adm = new Administraciones();
			adm.setRegCuotas(ensureBigDecimal(mod390.getBox658()));
			adm.setComun(ensureBigDecimal(mod390.getBox87()));
			if (mod390.getBox88()>0) {
				adm.setArabaAlava(ensureBigDecimal(mod390.getBox88()));
			}
			if (mod390.getBox89()>0) {
				adm.setGipuzkoa(ensureBigDecimal(mod390.getBox89()));
			}
			if (mod390.getBox90()>0) {
				adm.setBizkaia(ensureBigDecimal(mod390.getBox90()));
			}
			if (mod390.getBox91()>0) {
				adm.setNavarra(ensureBigDecimal(mod390.getBox91()));
			}
			adm.setSumResultados(ensureBigDecimal(mod390.getBox84()));
			adm.setIvaAduana(ensureBigDecimal(mod390.getBox659()));
			adm.setResTerrComun(ensureBigDecimal(mod390.getBox92()));
			if (mod390.getBox93() != 0.0) {
				adm.setComCuotasEjercicioAnteriorTerrComun(ensureBigDecimal(mod390.getBox93()));
			}
			adm.setResLiqAnualTerrComun(ensureBigDecimal(mod390.getBox94()));
			return adm;
		}
		return null;
	}


	private static LiqAnual getLiqAnual(Mod3902018 mod390) {
		LiqAnual liq = new LiqAnual();
		liq.setRegCuotas(ensureBigDecimal(mod390.getBox658()) );
		liq.setSumResultados(ensureBigDecimal(mod390.getBox84()) );
		if (mod390.getBox85() > 0) {
			liq.setCompCuotasEjercicioAnterior(ensureBigDecimal(mod390.getBox85()));
		}
		liq.setIvaAduana(ensureBigDecimal(mod390.getBox659()));
		liq.setResLiquidacion(ensureBigDecimal(mod390.getBox86()));
        return liq;
	}

	private static ResLiquidaciones getResLiquidaciones(Mod3902018 mod390) {
		ResLiquidaciones res = new ResLiquidaciones();
        
		PerNoRegGrupos perNoRegGrupos = new PerNoRegGrupos();
		if (mod390.getBox95() != 0) {
			perNoRegGrupos.setTotIngresosIVA(ensureBigDecimal(mod390.getBox95()));
		}
		if (mod390.getBox96() != 0) {
			perNoRegGrupos.setTotDevIVASPRegDevMensual(ensureBigDecimal(mod390.getBox96()));
		}
		// ???????????????
        // AEATIVA2013 .ResLiquidaciones.PerNoRegGrupos.ExclusionBaja exclusionBaja;
		if (mod390.getBox524() != 0) {
			perNoRegGrupos.setTotDevAdqElemTrans(ensureBigDecimal(mod390.getBox524()));
		}
		if (mod390.getBox97() != 0) {
			perNoRegGrupos.setImporteACompensarUltimoPeriodo(ensureBigDecimal(mod390.getBox97()));
		}
		if (mod390.getBox98() != 0) {
			perNoRegGrupos.setImporteADevolverUltimoPeriodo(ensureBigDecimal(mod390.getBox98()));
		}
		if (mod390.getBox662() != 0) {
			perNoRegGrupos.setCuotasPendCompensar(ensureBigDecimal(mod390.getBox662()));
		}
        res.setPerNoRegGrupos(perNoRegGrupos);
        
        PerSiRegGrupos perSiRegGrupos = new PerSiRegGrupos();
		if (mod390.getBox525() != 0) {
			perSiRegGrupos.setTotResulPositivos322(ensureBigDecimal(mod390.getBox525()));
		}
		if (mod390.getBox526() != 0) {
			perSiRegGrupos.setTotResulNegativos322(ensureBigDecimal(mod390.getBox526()));
		}
        res.setPerSiRegGrupos(perSiRegGrupos);
		return res;
	}

	private static VolOperaciones getVolOperaciones(Mod3902018 mod390) {
		VolOperaciones vol = new VolOperaciones();
		if (mod390.getBox99()>0) {
			vol.setOpRegGeneral(ensureBigDecimal(mod390.getBox99()));
		}
		if (mod390.getBox653()>0) {
			vol.setOpRegEspCriterioCaja(ensureBigDecimal(mod390.getBox653()));
		}
		if (mod390.getBox103()>0) {
			vol.setEntregasIntracomunitariasExentas(ensureBigDecimal(mod390.getBox103()));
		}
		if (mod390.getBox104()>0) {
			vol.setExportacionesExentasConDrchoDeduccion(ensureBigDecimal(mod390.getBox104()));
		}
		if (mod390.getBox105()>0) {
			vol.setOpExentasSinDrchoDeduccion(ensureBigDecimal(mod390.getBox105()));
		}
		if (mod390.getBox110()>0) {
			vol.setOpNoSujetas(ensureBigDecimal(mod390.getBox110()));
		}
		if (mod390.getBox112()>0) {
			vol.setEntregasBienesInstalacionOtrosEM(ensureBigDecimal(mod390.getBox112()));
		}
		if (mod390.getBox100()>0) {
			vol.setOpRegSimplificado(ensureBigDecimal(mod390.getBox100()));
		}
		if (mod390.getBox101()>0) {
			vol.setOpRegEspAgricPescGanad(ensureBigDecimal(mod390.getBox101()));
		}
		if (mod390.getBox102()>0) {
			vol.setOpRegEspRecEquivalencia(ensureBigDecimal(mod390.getBox102()));
		}
		if (mod390.getBox227()>0) {
			vol.setOpRegEspBienesUsados(ensureBigDecimal(mod390.getBox227()));
		}
		if (mod390.getBox228()>0) {
			vol.setOpRegEspAgViajes(ensureBigDecimal(mod390.getBox228()));
		}
		if (mod390.getBox106()>0) {
			vol.setEntregasBienesInmuebles(ensureBigDecimal(mod390.getBox106()));
		}
		if (mod390.getBox107()>0) {
			vol.setEntregasBienesInversion(ensureBigDecimal(mod390.getBox107()));
		}
		vol.setTotalVolOp(ensureBigDecimal(mod390.getBox108()));
		return vol;
	}

	private static OpEspecificas getOpEspecificas(Mod3902018 mod390) {
		OpEspecificas op = new OpEspecificas();
		if (mod390.getBox230()>0) {
			op.setAdqInterioresExentas(ensureBigDecimal(mod390.getBox230()));
		}
		if (mod390.getBox109()>0) {
			op.setAdqIntracomunitariasExentas(ensureBigDecimal(mod390.getBox109()));
		}
		if (mod390.getBox231()>0) {
			op.setImportacionesExentas(ensureBigDecimal(mod390.getBox231()));
		}
		if (mod390.getBox232()>0) {
			op.setBasesIVASoportadoNoDeducible(ensureBigDecimal(mod390.getBox232()));
		}
		if (mod390.getBox111()>0) {
			op.setOpSujetas(ensureBigDecimal(mod390.getBox111()));
		}
		if (mod390.getBox113()>0) {
			op.setEntregasInteriores(ensureBigDecimal(mod390.getBox113()));
		}
		if (mod390.getBox523()>0) {
			op.setServInversionSP(ensureBigDecimal(mod390.getBox523()));
		}
		if (mod390.getBox654()>0 || mod390.getBox655()>0) {
			EntregasCriterioCajaBase eccj =  new EntregasCriterioCajaBase();
			eccj.setTipoX(new TipoBaseImponibleYCuota());
			eccj.getTipoX().setBI(ensureBigDecimal(mod390.getBox654()));
			eccj.getTipoX().setCuota(ensureBigDecimal(mod390.getBox655()));
			op.setEntregasCriterioCajaBase(eccj);
		}
		if (mod390.getBox656()>0 || mod390.getBox657()>0) {
			AdqCriterioCajaBase accj =  new AdqCriterioCajaBase();
			accj.setTipoX(new TipoBaseImponibleYCuota());
			accj.getTipoX().setBI(ensureBigDecimal(mod390.getBox656()));
			accj.getTipoX().setCuota(ensureBigDecimal(mod390.getBox657()));
			op.setAdqCriterioCajaBase(accj);
		}
		return op;
	}

}
