package com.esferalia.aon.occam.impl.jooq.dao.mod425_2025;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.occam.api.model.fiscal.ActivityType;
import com.esferalia.aon.occam.api.model.fiscal.Address;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Activity;
import com.esferalia.aon.occam.api.model.fiscal.mod390.DeductionRegime;
import com.esferalia.aon.occam.api.model.fiscal.mod390.FarmerRegimeActivity;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Prorrata;
import com.esferalia.aon.occam.api.model.fiscal.mod390.SimpliedRegimeActivity;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025.Mod425Detail;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025DetailKey;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.Administraciones;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.DatEstadisticos;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.DatEstadisticos.Conjunta;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.DatEstadisticos.OpTercerasPax;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.DatEstadisticos.Otras;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.DatEstadisticos.Pral;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.DatIdent;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.Devengo;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.Devengo.ConcursoAcreedoresNO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.Devengo.DecSustitutiva;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.Devengo.DestRegCriterioCajaNO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.Devengo.DestRegCriterioCajaSI;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.Devengo.RegCriterioCajaNO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.Devengo.RegCriterioCajaSI;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.Devengo.RegDevMensual;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.IVADeducibleGrupo1;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.IVADeducibleGrupo1.AdqIntracomunitarias;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.IVADeducibleGrupo1.Importaciones;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.IVADeducibleGrupo1.OpInteriores;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.IVADeducibleGrupo2;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.IVADeducibleGrupo3;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.LiqAnual;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.OpEspecificas;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.OpEspecificas.AdqCriterioCajaBase;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.OpEspecificas.EntregasCriterioCajaBase;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.Prorratas;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.Prorratas.Pro;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.BaseImponibleyCuota;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.BaseImponibleyCuota.AdqIntracomBienes;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.BaseImponibleyCuota.AdqIntracomServicios;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.BaseImponibleyCuota.IVAdevengadoInversionSP;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.BaseImponibleyCuota.ModBasesyCuotas;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.BaseImponibleyCuota.ModBasesyCuotasConcursoAcreedores;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.BaseImponibleyCuota.ModRecargoEquivalencia;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.BaseImponibleyCuota.ModRecargoEquivalenciaConcursoAcreedores;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.BaseImponibleyCuota.OpIntragrupo;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.BaseImponibleyCuota.RecargoEquivalencia;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.BaseImponibleyCuota.RegAgViajes;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.BaseImponibleyCuota.RegBienesUsados;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.BaseImponibleyCuota.RegCriterioCaja;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.BaseImponibleyCuota.RegOrdinario;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.BaseImponibleyCuota.TotalBasesyCuotasIVA;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.Deducciones;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.Deducciones.AdqIntracomunitariasBienesCorrientes;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.Deducciones.AdqIntracomunitariasBienesInversion;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.Deducciones.AdqIntracomunitariasServicios;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.Deducciones.ComRegAgricGanadPesca;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.Deducciones.ImportacionesBienesCorrientes;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.Deducciones.ImportacionesBienesInversion;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.Deducciones.OpInterioresBienesInversion;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.Deducciones.OpInterioresBienesServiciosCorrientes;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.Deducciones.OpIntragrupoBienesInversion;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.Deducciones.OpIntragrupoCorrientes;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.Deducciones.RectifDeducciones;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.Deducciones.RectifOpIntragrupo;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegSimplificado;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegSimplificado.ActAgricGanadForest;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegSimplificado.Actividad;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegSimplificado.Actividad.Modulo;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegSimplificado.IvaDeducible;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegSimplificado.IvaDevengado;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.ResLiquidaciones;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.ResLiquidaciones.PerNoRegGrupos;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.ResLiquidaciones.PerSiRegGrupos;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.VolOperaciones;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.TipoBaseImponibleYCuota;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.TipoConcursoUltPer;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.TipoConcursoUltPer.ConcursoUltPerNO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.TipoConcursoUltPer.ConcursoUltPerSI;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.TipoDoc;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.TipoDomicilio;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.TipoGrupoEntidades;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.TipoGrupoEntidades.Art65NO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.TipoGrupoEntidades.Art65SI;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.TipoGrupoEntidades.Dependiente;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.TipoGrupoEntidades.Dominante;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.TipoGrupoEntidades.UltAutoliquidNO;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.TipoGrupoEntidades.UltAutoliquidSI;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.TipoIdentificacionPersonaFisica;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.TipoIdentificacionPersonaJuridica;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.TipoPersonaFisica;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.TipoPersonaJuridica;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.TipoRepresentanteFisica;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.TipoRepresentanteJuridica;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod425toATC4252025 {
	

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	private Mod425toATC4252025() {
	}

	public static AEATIVA2024 getAEATIVA2024(Mod4252025 mod425) {
		AEATIVA2024 iva = new AEATIVA2024();
		TipoDoc tipoDoc = new TipoDoc();
		tipoDoc.setCodModelo("390");
		tipoDoc.setEjercicio(mod425.getYear());
		iva.setIdDoc(tipoDoc);
		
		DatIdent datIdent = new DatIdent();
		
		if ( mod425.isLegalEntity() ) {
			TipoPersonaJuridica tpj = new TipoPersonaJuridica();
			TipoIdentificacionPersonaJuridica tipj = new TipoIdentificacionPersonaJuridica();
			tipj.setNIF(toUppercase(mod425.getDocument()));
			tipj.setRazonSocial(toUppercase(mod425.getName()));
			tpj.setIdentPersJuridica(tipj);
			datIdent.setPersJuridica(tpj);
		} else {
			TipoPersonaFisica tp = new TipoPersonaFisica();
			TipoIdentificacionPersonaFisica tipf = new TipoIdentificacionPersonaFisica();
			tipf.setNIF(mod425.getDocument());
			tipf.setNombre(toUppercase(mod425.getName()));
			tipf.setApe1(toUppercase(mod425.getFirstSurname()));
			if (AonStringUtils.isNotBlank(toUppercase(mod425.getSecondSurname()))) {
				tipf.setApe2(toUppercase(mod425.getSecondSurname()));
			}
			tp.setIdent(tipf);
			datIdent.setPersFisica(tp);
		}
		if (AonStringUtils.isNotEmpty(mod425.getContactPhone())) {
			datIdent.setTelefono(mod425.getContactPhone());
		}
		iva.setDatIdent(datIdent);
		
		iva.setDevengo(getDevengo(mod425));
		iva.setDatEstadisticos(getStatisticalData(mod425));
		iva.setRepresentanteFisica( getRepresentanteFisica(mod425) );
		iva.getRepresentanteJuridica().addAll( getRepresentanteJuridica(mod425) );
		iva.setRegGeneral(getRegGeneral(mod425));
		
		iva.setRegSimplificado(getRegSimplificado(mod425));
		
		Administraciones adm = getAdministraciones(mod425);
		if (adm == null) {
			iva.setLiqAnual(getLiqAnual(mod425));
		} else {
			iva.setAdministraciones(adm);
		}
		iva.setResLiquidaciones(getResLiquidaciones(mod425));
		iva.setVolOperaciones(getVolOperaciones(mod425));
		iva.setOpEspecificas(getOpEspecificas(mod425));
		
		// PRORRATAS
		if (mod425.getProrratas() != null && !mod425.getProrratas().isEmpty()) {
			Prorratas prorratas = new Prorratas();
			for (Prorrata pro : mod425.getProrratas()) {
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
		if (mod425.getRegime1() != null) {
			DeductionRegime regime = mod425.getRegime1();
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
		if (mod425.getRegime2() != null) {
			DeductionRegime regime = mod425.getRegime2();
			IVADeducibleGrupo2 ivad = new IVADeducibleGrupo2();
			ivad.setOpInteriores(new com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.IVADeducibleGrupo2.OpInteriores());
			ivad.getOpInteriores().setBienesyServiciosCorrientes( getTipoBaseImponibleYCuota(regime.getBase1(), regime.getQuota1()) );
			ivad.getOpInteriores().setBienesInversion( getTipoBaseImponibleYCuota(regime.getBase2(), regime.getQuota2()) );
			ivad.setImportaciones(new com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.IVADeducibleGrupo2.Importaciones());
			ivad.getImportaciones().setBienesCorrientes( getTipoBaseImponibleYCuota(regime.getBase3(), regime.getQuota3()) );
			ivad.getImportaciones().setBienesInversion( getTipoBaseImponibleYCuota(regime.getBase4(), regime.getQuota4()) );
			ivad.setAdqIntracomunitarias(new com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.IVADeducibleGrupo2.AdqIntracomunitarias());
			ivad.getAdqIntracomunitarias().setBienesCorrientes( getTipoBaseImponibleYCuota(regime.getBase5(), regime.getQuota5()) );
			ivad.getAdqIntracomunitarias().setBienesInversion( getTipoBaseImponibleYCuota(regime.getBase6(), regime.getQuota6()) );
			ivad.setCompRegEspAgricGanadPesca( getTipoBaseImponibleYCuota(regime.getBase7(), regime.getQuota7()));
			ivad.setRectDeducciones( getTipoBaseImponibleYCuota(regime.getBase8(), regime.getQuota8()));
			ivad.setRegInversiones(ensureBigDecimal(regime.getQuota9()));
			ivad.setSumaDeducciones(ensureBigDecimal(regime.getQuota10()));
			iva.setIVADeducibleGrupo2( ivad );
		}
		if (mod425.getRegime3() != null) {
			DeductionRegime regime = mod425.getRegime3();
			IVADeducibleGrupo3 ivad = new IVADeducibleGrupo3();
			ivad.setOpInteriores(new com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.IVADeducibleGrupo3.OpInteriores());
			ivad.getOpInteriores().setBienesyServiciosCorrientes( getTipoBaseImponibleYCuota(regime.getBase1(), regime.getQuota1()) );
			ivad.getOpInteriores().setBienesInversion( getTipoBaseImponibleYCuota(regime.getBase2(), regime.getQuota2()) );
			ivad.setImportaciones(new com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.IVADeducibleGrupo3.Importaciones());
			ivad.getImportaciones().setBienesCorrientes( getTipoBaseImponibleYCuota(regime.getBase3(), regime.getQuota3()) );
			ivad.getImportaciones().setBienesInversion( getTipoBaseImponibleYCuota(regime.getBase4(), regime.getQuota4()) );
			ivad.setAdqIntracomunitarias(new com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.IVADeducibleGrupo3.AdqIntracomunitarias());
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

	private static RegSimplificado getRegSimplificado(Mod4252025 mod425) {
		RegSimplificado reg = null;
		boolean something = false;
		if (mod425.getSimpRegime1() != null && AonStringUtils.isNotBlank( mod425.getSimpRegime1().getEpigrafe())) {
			Actividad actividad = getActividad(mod425.getSimpRegime1());
			reg = new RegSimplificado();
			reg.getActividad().add(actividad);
			something = true;
		}
		if (mod425.getSimpRegime2() != null && AonStringUtils.isNotBlank( mod425.getSimpRegime2().getEpigrafe())) {
			Actividad actividad = getActividad(mod425.getSimpRegime2());
			reg = reg==null?new RegSimplificado():reg;
			reg.getActividad().add(actividad);
			something = true;
		}
		if (mod425.getFarmerRegime1() != null && AonStringUtils.isNotBlank( mod425.getFarmerRegime1().getCodigo()) ) {
			ActAgricGanadForest act = getActAgricGanadForest(mod425.getFarmerRegime1());
			reg = reg==null?new RegSimplificado():reg;
			reg.getActAgricGanadForest().add(act);
			something = true;
		}
		if (mod425.getFarmerRegime2() != null && AonStringUtils.isNotBlank( mod425.getFarmerRegime2().getCodigo())) {
			ActAgricGanadForest act = getActAgricGanadForest(mod425.getFarmerRegime2());
			reg = reg==null?new RegSimplificado():reg;
			reg.getActAgricGanadForest().add(act);
			something = true;
		}
		if (mod425.getFarmerRegime3() != null && AonStringUtils.isNotBlank( mod425.getFarmerRegime3().getCodigo())) {
			ActAgricGanadForest act = getActAgricGanadForest(mod425.getFarmerRegime3());
			reg = reg==null?new RegSimplificado():reg;
			reg.getActAgricGanadForest().add(act);
			something = true;
		}
		if (mod425.getFarmerRegime4() != null && AonStringUtils.isNotBlank( mod425.getFarmerRegime4().getCodigo())) {
			ActAgricGanadForest act = getActAgricGanadForest(mod425.getFarmerRegime4());
			reg = reg==null?new RegSimplificado():reg;
			reg.getActAgricGanadForest().add(act);
			something = true;
		}
		if (mod425.getFarmerRegime5() != null && AonStringUtils.isNotBlank( mod425.getFarmerRegime5().getCodigo())) {
			ActAgricGanadForest act = getActAgricGanadForest(mod425.getFarmerRegime5());
			reg = reg==null?new RegSimplificado():reg;
			reg.getActAgricGanadForest().add(act);
			something = true;
		}
		if (something) {
			IvaDevengado ivaDev = new IvaDevengado();
			if ( mod425.getBox74() != 0.00 ) {
				ivaDev.setSumaCuotasNoAgric(ensureBigDecimal(mod425.getBox74()));
			}
			ivaDev.setSumaCuotasAgric(ensureBigDecimal(mod425.getBox75()));
			ivaDev.setAdqIntracomunitarias(ensureBigDecimal(mod425.getBox76()));
			ivaDev.setInversionSujetoPasivo(ensureBigDecimal(mod425.getBox77()));
			ivaDev.setEntregasActivosFijos(ensureBigDecimal(mod425.getBox78()));
			ivaDev.setTotalCuota(ensureBigDecimal(mod425.getBox79()));
			reg.setIvaDevengado(ivaDev);
			IvaDeducible ivaDed = new IvaDeducible();
			ivaDed.setIVASoportadoAdqActivosFijos(ensureBigDecimal(mod425.getBox80()));
			ivaDed.setRegBienesInversion(ensureBigDecimal(mod425.getBox81()));
			ivaDed.setSumaDeducciones(ensureBigDecimal(mod425.getBox82()));
			reg.setIvaDeducible(ivaDed);
			
			reg.setResRegimenSimplificado(ensureBigDecimal(mod425.getBox83()));
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
		if (ac.getDanaReduction() != 0.0) {
			act.setDana(ensureBigDecimal(ac.getDanaReduction()));
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
			actividad.setLorca(ensureBigDecimal(sr.getBoxC1()));
		}
		if ( sr.getBoxC2() != 0.00 ) {
			actividad.setDana(ensureBigDecimal(sr.getBoxC2()));
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

	private static Devengo getDevengo(Mod4252025 mod425) {
		Devengo devengo = new Devengo();
		devengo.setEjercicio(mod425.getYear());
		
		if (mod425.isInsolvencyStateThisYear()) {
			TipoConcursoUltPer tipo = new TipoConcursoUltPer();
			if (mod425.isInsolvencyStateLastPeriod()) {
				tipo.setConcursoUltPerSI( new ConcursoUltPerSI());	
			} else {
				tipo.setConcursoUltPerNO( new ConcursoUltPerNO());
			}
			devengo.setConcursoAcreedoresSI( tipo );	
		} else {
			devengo.setConcursoAcreedoresNO( new ConcursoAcreedoresNO());
		}
        
		if (mod425.isAccrualRegime()) {
			devengo.setRegCriterioCajaSI(new RegCriterioCajaSI());
		} else {
			devengo.setRegCriterioCajaNO(new RegCriterioCajaNO());
		}
		if (mod425.isAccrualRegimeTarget()) {
			devengo.setDestRegCriterioCajaSI(new DestRegCriterioCajaSI());
		} else {
			devengo.setDestRegCriterioCajaNO(new DestRegCriterioCajaNO());
		}

		if (mod425.isTaxRefund()) {
			devengo.setRegDevMensual(new RegDevMensual());
		}
		if (mod425.isReplacement()) {
			devengo.setDecSustitutiva( new DecSustitutiva() );
			devengo.setJustDecAnterior( mod425.getReplacedReceipt() );
		}
		if (mod425.isSpecialGroupRegime()) {
			TipoGrupoEntidades tge = new TipoGrupoEntidades();
			tge.setNumGrupo(mod425.getGroupNumber());
			if (mod425.isGroupDependent()) {
				tge.setDependiente(new Dependiente());	
			} else {
				tge.setDominante(new Dominante());
			}
			if (mod425.isGroupRegimeType()) {
				tge.setArt65SI( new Art65SI());
				tge.setNIFEntidadDominante(mod425.getGroupDocument());
			} else {
				tge.setArt65NO( new Art65NO());
			}
			if (mod425.isGroupDeclarations()) {
				tge.setUltAutoliquidSI(new UltAutoliquidSI());
			} else {
				tge.setUltAutoliquidNO(new UltAutoliquidNO());
			}
			devengo.setRegGrupoEntidades(tge);
		}
		if (mod425.isAccrualRegime()) {
			devengo.setRegCriterioCajaSI(new RegCriterioCajaSI());
		} else {
			devengo.setRegCriterioCajaNO(new RegCriterioCajaNO());
		}
		if (mod425.isAccrualRegimeTarget()){
			devengo.setDestRegCriterioCajaSI(new DestRegCriterioCajaSI());
		} else {
			devengo.setDestRegCriterioCajaNO(new DestRegCriterioCajaNO());
		}
		return devengo;
	}
	
	private static DatEstadisticos getStatisticalData(Mod4252025 mod425) {
		DatEstadisticos datEstadisticos = new DatEstadisticos();
		
		if (mod425.getMainActivity() != null) {
			Pral pral = new Pral();
			pral.setClave(ActivityType.toString(mod425.getMainActivity().getType()));
			pral.setDescripcion(mod425.getMainActivity().getDescription());
			pral.setEpigrafe(mod425.getMainActivity().getEpigraph());
			datEstadisticos.setPral(pral);
		}
		Activity[] activities = new Activity[]{
				mod425.getActivity1(),
				mod425.getActivity2(),
				mod425.getActivity3(),
				mod425.getActivity4(),
				mod425.getActivity5()
		};
		for (Activity activity : activities) {
			if (activity != null) {
				Otras otras = new Otras();
				otras.setClave(ActivityType.toString(activity.getType()));
				otras.setDescripcion(activity.getDescription());
				otras.setEpigrafe(activity.getEpigraph());
				datEstadisticos.getOtras().add(otras);
			}
		}
		if (mod425.isMod347()) {
			datEstadisticos.setOpTercerasPax(new OpTercerasPax());
		}
		if (AonStringUtils.isNotEmpty(mod425.getMergedDeclarationDocument())) {
			Conjunta conjunta = new Conjunta();
			conjunta.setNIF(mod425.getMergedDeclarationDocument());
			conjunta.setRazonSocial(mod425.getMergedDeclarationName());
			datEstadisticos.setConjunta(conjunta);
		}
		return datEstadisticos;
	}

	private static ArrayList<TipoRepresentanteJuridica> getRepresentanteJuridica(Mod4252025 mod425) {
		ArrayList<TipoRepresentanteJuridica> list = new ArrayList<>();
		LegalRepresentative[] lrs = new LegalRepresentative[] {
			mod425.getLegalRepr1(),	
			mod425.getLegalRepr2(),
			mod425.getLegalRepr3()
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

	private static TipoRepresentanteFisica getRepresentanteFisica(Mod4252025 mod425) {
		Address address = mod425.getAddress();
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
			}
		}
		return trf;
	}

	private static RegGeneral getRegGeneral(Mod4252025 mod425) {
		RegGeneral regGeneral = new RegGeneral();
		BaseImponibleyCuota bases = getBaseImponibleyCuota(mod425);
		regGeneral.setBaseImponibleyCuota(bases);
		Deducciones ded = getDeducciones(mod425); 
		regGeneral.setDeducciones(ded);
		regGeneral.setResRegGeneral(getResRegGeneral(mod425));
		return regGeneral;
	}
	
	private static String getResRegGeneral(Mod4252025 mod425) {
		Mod425Detail detail = getKey(mod425,Mod4252025DetailKey.C0065);
		String total = null;
		if (detail != null) {
			total = Double.toString( AonMathUtils.round(detail.getQuota()) ); 			
		}
		return total;
	}


	private static Deducciones getDeducciones(Mod4252025 mod425) {
		Deducciones deducciones = new Deducciones();
		deducciones.setOpInterioresBienesServiciosCorrientes(getOpInterioresBienesServiciosCorrientes(mod425));
		deducciones.setOpIntragrupoCorrientes(getOpIntragrupoCorrientes(mod425));
		deducciones.setOpInterioresBienesInversion(getOpInterioresBienesInversion(mod425));
		deducciones.setOpIntragrupoBienesInversion(getOpIntragrupoBienesInversion(mod425));
		deducciones.setImportacionesBienesCorrientes(getImportacionesBienesCorrientes(mod425));
		deducciones.setImportacionesBienesInversion(getImportacionesBienesInversion(mod425));
		deducciones.setAdqIntracomunitariasBienesCorrientes(getAdqIntracomunitariasBienesCorrientes(mod425));
		deducciones.setAdqIntracomunitariasBienesInversion(getAdqIntracomunitariasBienesInversion(mod425));
		deducciones.setAdqIntracomunitariasServicios(getAdqIntracomunitariasServicios(mod425));
		deducciones.setComRegAgricGanadPesca(getComRegAgricGanadPesca(mod425));
		deducciones.setRectifDeducciones(getRectifDeducciones(mod425));
		deducciones.setRectifOpIntragrupo(getRectifOpIntragrupo(mod425));
		deducciones.setRegularizInversiones(getRegularizInversiones(mod425));
		deducciones.setRegularizPorcProrrata(getRegularizPorcProrrata(mod425));
		deducciones.setSumDeducciones(getSumDeducciones(mod425));
		return deducciones;
	}

	private static BigDecimal getSumDeducciones(Mod4252025 mod425) {
		BigDecimal op = null;
		Mod425Detail detail = getKey(mod425,Mod4252025DetailKey.C0064);
		if (detail != null) {
			op = ensureBigDecimal(detail.getQuota());
		}
		return op;
	}


	private static BigDecimal getRegularizPorcProrrata(Mod4252025 mod425) {
		BigDecimal op = null;
		Mod425Detail detail = getKey(mod425,Mod4252025DetailKey.C0522);
		if (detail != null) {
			op = ensureBigDecimal(detail.getQuota());
		}
		return op;
	}


	private static BigDecimal getRegularizInversiones(Mod4252025 mod425) {
		BigDecimal op = null;
		Mod425Detail detail = getKey(mod425,Mod4252025DetailKey.C0063);
		if (detail != null) {
			op = ensureBigDecimal(detail.getQuota());
		}
		return op;
	}


	private static RectifDeducciones getRectifDeducciones(Mod4252025 mod425) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0062));
		if (tipo != null) {
			RectifDeducciones op = new RectifDeducciones();
			op.setTipoX(tipo);
			return op;
		}
		return null;
	}

	private static RectifOpIntragrupo getRectifOpIntragrupo(Mod4252025 mod425) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0652));
		if (tipo != null) {
			RectifOpIntragrupo op = new RectifOpIntragrupo();
			op.setTipoX(tipo);
			return op;
		}
		return null;
	}

	private static ComRegAgricGanadPesca getComRegAgricGanadPesca(Mod4252025 mod425) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0061));
		if (tipo != null) {
			ComRegAgricGanadPesca op = new ComRegAgricGanadPesca();
			op.setTipoX(tipo);
			return op;
		}
		return null;
	}


	private static AdqIntracomunitariasServicios getAdqIntracomunitariasServicios(Mod4252025 mod425) {
		AdqIntracomunitariasServicios op = new AdqIntracomunitariasServicios();
		op.setTipo2(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0774)));
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0588)));
		op.setTipo5(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0741)));
		op.setTipo75(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0776)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0636)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0638)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0598)));
		return op;
	}


	private static AdqIntracomunitariasBienesInversion getAdqIntracomunitariasBienesInversion(Mod4252025 mod425) {
		AdqIntracomunitariasBienesInversion op = new AdqIntracomunitariasBienesInversion();
		op.setTipo2(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0770)));
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0221)));
		op.setTipo5(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0739)));
		op.setTipo75(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0772)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0632)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0634)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0059)));
		return op;
	}


	private static AdqIntracomunitariasBienesCorrientes getAdqIntracomunitariasBienesCorrientes(Mod4252025 mod425) {
		AdqIntracomunitariasBienesCorrientes op = new AdqIntracomunitariasBienesCorrientes();
		op.setTipo2(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0766)));
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0215)));
		op.setTipo5(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0737)));
		op.setTipo75(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0768)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0628)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0630)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0057)));
		return op;
	}


	private static ImportacionesBienesInversion getImportacionesBienesInversion(Mod4252025 mod425) {
		ImportacionesBienesInversion op = new ImportacionesBienesInversion();
		op.setTipo2(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0762)));
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0209)));
		op.setTipo5(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0735)));
		op.setTipo75(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0764)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0624)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0626)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0055)));
		return op;
	}


	private static ImportacionesBienesCorrientes getImportacionesBienesCorrientes(Mod4252025 mod425) {
		ImportacionesBienesCorrientes op = new ImportacionesBienesCorrientes();
		op.setTipo2(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0758)));
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0203)));
		op.setTipo5(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0733)));
		op.setTipo75(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0760)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0620)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0622)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0053)));
		return op;
	}


	private static OpIntragrupoBienesInversion getOpIntragrupoBienesInversion(Mod4252025 mod425) {
		OpIntragrupoBienesInversion op = new OpIntragrupoBienesInversion();
		op.setTipo2(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0754)));
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0515)));
		op.setTipo5(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0731)));
		op.setTipo75(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0756)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0616)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0618)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0521)));
		return op;
	}


	private static OpInterioresBienesInversion getOpInterioresBienesInversion(Mod4252025 mod425) {
		OpInterioresBienesInversion op = new OpInterioresBienesInversion();
		op.setTipo2(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0750)));
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0197)));
		op.setTipo5(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0729)));
		op.setTipo75(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0752)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0612)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0614)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0051)));
		return op;
	}


	private static OpIntragrupoCorrientes getOpIntragrupoCorrientes(Mod4252025 mod425) {
		OpIntragrupoCorrientes op = new OpIntragrupoCorrientes();
		op.setTipo2(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0746)));
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0507)));
		op.setTipo5(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0727)));
		op.setTipo75(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0748)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0608)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0610)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0513)));
		return op;
	}


	private static OpInterioresBienesServiciosCorrientes getOpInterioresBienesServiciosCorrientes(Mod4252025 mod425) {
		OpInterioresBienesServiciosCorrientes op = new OpInterioresBienesServiciosCorrientes();
		op.setTipo2(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0696)));
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0191)));
		op.setTipo5(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0725)));
		op.setTipo75(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0698)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0604)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0606)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0049)));
		return op;
	}


	private static BaseImponibleyCuota getBaseImponibleyCuota(Mod4252025 mod425) {
		BaseImponibleyCuota b = new BaseImponibleyCuota();
		b.setRegOrdinario(getRegOrdinario(mod425));
		b.setRegCriterioCaja(getRegCriterioCaja(mod425));
		b.setOpIntragrupo(getOpIntragrupo(mod425));
		b.setRegBienesUsados(getRegBienesUsados(mod425));
		b.setRegAgViajes(getRegAgViajes(mod425));
		b.setAdqIntracomBienes(getAdqIntracomBienes(mod425));
		b.setAdqIntracomServicios(getAdqIntracomServicios(mod425));
		b.setIVAdevengadoInversionSP(getIVAdevengadoInversionSP(mod425));
		b.setModBasesyCuotas(getModBasesyCuotas(mod425));
		b.setModBasesyCuotasConcursoAcreedores(getModBasesyCuotasConcursoAcreedores(mod425));
		b.setTotalBasesyCuotasIVA(getTotalBasesyCuotasIVA(mod425));
		b.setRecargoEquivalencia(getRecargoEquivalencia(mod425));
		b.setModRecargoEquivalencia(getModRecargoEquivalencia(mod425));
		b.setModRecargoEquivalenciaConcursoAcreedores(getModRecargoEquivalenciaConcursoAcreedores(mod425));
		b.setTotalCuotasIVA(getTotalCuotasIVA(mod425));
		return b;
	}


	private static BigDecimal getTotalCuotasIVA(Mod4252025 mod425) {
		Mod425Detail detail = getKey(mod425,Mod4252025DetailKey.C0047);
		BigDecimal totalCuotasIVA = null; 
		if (detail != null) {
			totalCuotasIVA = ensureBigDecimal(detail.getQuota()); 			
		}
		return totalCuotasIVA;
	}


	private static ModRecargoEquivalenciaConcursoAcreedores getModRecargoEquivalenciaConcursoAcreedores(Mod4252025 mod425) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0046));
		if (tipo != null) {
			ModRecargoEquivalenciaConcursoAcreedores modRecargoEquivalenciaConcursoAcreedores = new ModRecargoEquivalenciaConcursoAcreedores();
			modRecargoEquivalenciaConcursoAcreedores.setTipoX(tipo);
			return modRecargoEquivalenciaConcursoAcreedores;
		}
		return null;
	}


	private static ModRecargoEquivalencia getModRecargoEquivalencia(Mod4252025 mod425) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0044));
		if (tipo != null) {
			ModRecargoEquivalencia modRecargoEquivalencia = new ModRecargoEquivalencia();
			modRecargoEquivalencia.setTipoX(tipo);
			return modRecargoEquivalencia;
		}
		return null;
	}


	private static RecargoEquivalencia getRecargoEquivalencia(Mod4252025 mod425) {
		RecargoEquivalencia recargoEquivalencia = new RecargoEquivalencia();
		recargoEquivalencia.setTipo0(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0664)));
		recargoEquivalencia.setTipo026(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0692)));
		recargoEquivalencia.setTipo05(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0036)));
		recargoEquivalencia.setTipo062(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0666)));
		recargoEquivalencia.setTipo1(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0694)));
		recargoEquivalencia.setTipo14(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0600)));
		recargoEquivalencia.setTipo52(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0602)));
		recargoEquivalencia.setTipo175(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0042)));		
		return recargoEquivalencia;
	}


	private static TotalBasesyCuotasIVA getTotalBasesyCuotasIVA(Mod4252025 mod425) {
		TotalBasesyCuotasIVA totalBasesyCuotasIVA = new TotalBasesyCuotasIVA();
		totalBasesyCuotasIVA.setTipoX(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0034)));
		return totalBasesyCuotasIVA;
	}


	private static ModBasesyCuotasConcursoAcreedores getModBasesyCuotasConcursoAcreedores(
			Mod4252025 mod425) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0032));
		if (tipo != null) {
			ModBasesyCuotasConcursoAcreedores modBases = new ModBasesyCuotasConcursoAcreedores();
			modBases.setTipoX(tipo);
			return modBases;
		}
		return null;
	}


	private static ModBasesyCuotas getModBasesyCuotas(Mod4252025 mod425) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0030));
		if (tipo != null) {
			ModBasesyCuotas modBasesyCuotas = new ModBasesyCuotas();
			modBasesyCuotas.setTipoX(tipo);
			return modBasesyCuotas;
		}
		return null;
	}


	private static IVAdevengadoInversionSP getIVAdevengadoInversionSP(Mod4252025 mod425) {
		IVAdevengadoInversionSP iVAdevengadoInversionSP = new IVAdevengadoInversionSP();
		iVAdevengadoInversionSP.setTipoX(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0028)));
		return iVAdevengadoInversionSP;
	}


	private static AdqIntracomServicios getAdqIntracomServicios(Mod4252025 mod425) {
		AdqIntracomServicios adqIntracomServicios = new AdqIntracomServicios();
		adqIntracomServicios.setTipo0(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0721)));
		adqIntracomServicios.setTipo2(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0688)));
		adqIntracomServicios.setTipo4(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0546)));
		adqIntracomServicios.setTipo5(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0723)));
		adqIntracomServicios.setTipo75(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0690)));
		adqIntracomServicios.setTipo10(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0548)));
		adqIntracomServicios.setTipo21(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0552)));
		return adqIntracomServicios;
	}


	private static AdqIntracomBienes getAdqIntracomBienes(Mod4252025 mod425) {
		AdqIntracomBienes adqIntracomBienes = new AdqIntracomBienes();
		adqIntracomBienes.setTipo0(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0717)));
		adqIntracomBienes.setTipo2(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0684)));
		adqIntracomBienes.setTipo4(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0022)));
		adqIntracomBienes.setTipo5(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0719)));
		adqIntracomBienes.setTipo75(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0686)));
		adqIntracomBienes.setTipo10(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0024)));
		adqIntracomBienes.setTipo21(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0026)));
		return adqIntracomBienes;
	}


	private static RegAgViajes getRegAgViajes(Mod4252025 mod425) {
		RegAgViajes regAgViajes = new RegAgViajes();
		regAgViajes.setTipo21(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0014)));
		return regAgViajes;
	}


	private static RegBienesUsados getRegBienesUsados(Mod4252025 mod425) {
		RegBienesUsados regBienesUsados = new RegBienesUsados();
		regBienesUsados.setTipo0(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0713)));
		regBienesUsados.setTipo2(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0680)));
		regBienesUsados.setTipo4(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0008)));
		regBienesUsados.setTipo5(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0715)));
		regBienesUsados.setTipo75(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0682)));
		regBienesUsados.setTipo10(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0010)));
		regBienesUsados.setTipo21(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0012)));
		return regBienesUsados;
	}
	
	private static RegCriterioCaja getRegCriterioCaja(Mod4252025 mod425) {
		RegCriterioCaja regCriterioCaja = new RegCriterioCaja();
		regCriterioCaja.setTipo0(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0709)));
		regCriterioCaja.setTipo2(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0676)));
		regCriterioCaja.setTipo4(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0644)));
		regCriterioCaja.setTipo5(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0711)));
		regCriterioCaja.setTipo75(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0678)));
		regCriterioCaja.setTipo10(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0646)));
		regCriterioCaja.setTipo21(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0648)));
		return regCriterioCaja;
	}

	private static OpIntragrupo getOpIntragrupo(Mod4252025 mod425) {
		OpIntragrupo opIntragrupo = new OpIntragrupo();
		opIntragrupo.setTipo0(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0705)));
		opIntragrupo.setTipo2(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0672)));
		opIntragrupo.setTipo4(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0501)));
		opIntragrupo.setTipo5(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0707)));
		opIntragrupo.setTipo75(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0674)));
		opIntragrupo.setTipo10(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0503)));
		opIntragrupo.setTipo21(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0505)));
		return opIntragrupo;
	}

	private static RegOrdinario getRegOrdinario(Mod4252025 mod425) {
		RegOrdinario regOrdinario = new RegOrdinario();
		regOrdinario.setTipo0(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0701)));
		regOrdinario.setTipo2(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0668)));
		regOrdinario.setTipo4(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0002)));
		regOrdinario.setTipo5(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0703)));
		regOrdinario.setTipo75(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0670)));
		regOrdinario.setTipo10(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0004)));
		regOrdinario.setTipo21(getTipoBaseImponibleYCuota(getKey(mod425,Mod4252025DetailKey.C0006)));
		return regOrdinario;
	}

	private static Mod425Detail getKey(Mod4252025 mod425, Mod4252025DetailKey key) {
		if (mod425 != null && mod425.getGeneralRegime() != null) {
			return mod425.getGeneralRegime().get(key);
		}
		return null;
	}

	private static TipoBaseImponibleYCuota getTipoBaseImponibleYCuota(Mod425Detail detail) {
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

	private static Administraciones getAdministraciones(Mod4252025 mod425) {
		if (mod425.getBox87() > 0.0 && mod425.getBox87() < 100.0) {
			Administraciones adm = new Administraciones();
			adm.setRegCuotas(ensureBigDecimal(mod425.getBox658()));
			adm.setComun(ensureBigDecimal(mod425.getBox87()));
			if (mod425.getBox88()>0) {
				adm.setArabaAlava(ensureBigDecimal(mod425.getBox88()));
			}
			if (mod425.getBox89()>0) {
				adm.setGipuzkoa(ensureBigDecimal(mod425.getBox89()));
			}
			if (mod425.getBox90()>0) {
				adm.setBizkaia(ensureBigDecimal(mod425.getBox90()));
			}
			if (mod425.getBox91()>0) {
				adm.setNavarra(ensureBigDecimal(mod425.getBox91()));
			}
			adm.setSumResultados(ensureBigDecimal(mod425.getBox84()));
			adm.setIvaAduana(ensureBigDecimal(mod425.getBox659()));
			adm.setResTerrComun(ensureBigDecimal(mod425.getBox92()));
			if (mod425.getBox93() != 0.0) {
				adm.setComCuotasEjercicioAnteriorTerrComun(ensureBigDecimal(mod425.getBox93()));
			}
			adm.setResLiqAnualTerrComun(ensureBigDecimal(mod425.getBox94()));
			return adm;
		}
		return null;
	}


	private static LiqAnual getLiqAnual(Mod4252025 mod425) {
		LiqAnual liq = new LiqAnual();
		liq.setRegCuotas(ensureBigDecimal(mod425.getBox658()) );
		liq.setSumResultados(ensureBigDecimal(mod425.getBox84()) );
		if (mod425.getBox85() > 0) {
			liq.setCompCuotasEjercicioAnterior(ensureBigDecimal(mod425.getBox85()));
		}
		liq.setIvaAduana(ensureBigDecimal(mod425.getBox659()));
		liq.setResLiquidacion(ensureBigDecimal(mod425.getBox86()));
        return liq;
	}

	private static ResLiquidaciones getResLiquidaciones(Mod4252025 mod425) {
		ResLiquidaciones res = new ResLiquidaciones();
        
		PerNoRegGrupos perNoRegGrupos = new PerNoRegGrupos();
		if (mod425.getBox95() != 0) {
			perNoRegGrupos.setTotIngresosIVA(ensureBigDecimal(mod425.getBox95()));
		}
		if (mod425.getBox96() != 0) {
			perNoRegGrupos.setTotDevIVASPRegDevMensual(ensureBigDecimal(mod425.getBox96()));
		}
		// ???????????????
        // AEATIVA2013 .ResLiquidaciones.PerNoRegGrupos.ExclusionBaja exclusionBaja;
		if (mod425.getBox524() != 0) {
			perNoRegGrupos.setTotDevAdqElemTrans(ensureBigDecimal(mod425.getBox524()));
		}
		if (mod425.getBox97() != 0) {
			perNoRegGrupos.setImporteACompensarUltimoPeriodo(ensureBigDecimal(mod425.getBox97()));
		}
		if (mod425.getBox98() != 0) {
			perNoRegGrupos.setImporteADevolverUltimoPeriodo(ensureBigDecimal(mod425.getBox98()));
		}
		if (mod425.getBox662() != 0) {
			perNoRegGrupos.setCuotasPendCompensar(ensureBigDecimal(mod425.getBox662()));
		}
        res.setPerNoRegGrupos(perNoRegGrupos);
        
        PerSiRegGrupos perSiRegGrupos = new PerSiRegGrupos();
		if (mod425.getBox525() != 0) {
			perSiRegGrupos.setTotResulPositivos322(ensureBigDecimal(mod425.getBox525()));
		}
		if (mod425.getBox526() != 0) {
			perSiRegGrupos.setTotResulNegativos322(ensureBigDecimal(mod425.getBox526()));
		}
        res.setPerSiRegGrupos(perSiRegGrupos);
		return res;
	}

	private static VolOperaciones getVolOperaciones(Mod4252025 mod425) {
		VolOperaciones vol = new VolOperaciones();
		if (mod425.getBox99()>0) {
			vol.setOpRegGeneral(ensureBigDecimal(mod425.getBox99()));
		}
		if (mod425.getBox653()>0) {
			vol.setOpRegEspCriterioCaja(ensureBigDecimal(mod425.getBox653()));
		}
		if (mod425.getBox103()>0) {
			vol.setEntregasIntracomunitariasExentas(ensureBigDecimal(mod425.getBox103()));
		}
		if (mod425.getBox104()>0) {
			vol.setExportacionesExentasConDrchoDeduccion(ensureBigDecimal(mod425.getBox104()));
		}
		if (mod425.getBox105()>0) {
			vol.setOpExentasSinDrchoDeduccion(ensureBigDecimal(mod425.getBox105()));
		}
		if (mod425.getBox110()>0) {
			vol.setOpNoSujetas(ensureBigDecimal(mod425.getBox110()));
		}
		if (mod425.getBox125()>0) {
			vol.setBox125(ensureBigDecimal(mod425.getBox125()));
		}
		if (mod425.getBox126()>0) {
			vol.setBox126(ensureBigDecimal(mod425.getBox126()));
		}
		if (mod425.getBox127()>0) {
			vol.setBox127(ensureBigDecimal(mod425.getBox127()));
		}
		if (mod425.getBox128()>0) {
			vol.setBox128(ensureBigDecimal(mod425.getBox128()));
		}
		if (mod425.getBox100()>0) {
			vol.setOpRegSimplificado(ensureBigDecimal(mod425.getBox100()));
		}
		if (mod425.getBox101()>0) {
			vol.setOpRegEspAgricPescGanad(ensureBigDecimal(mod425.getBox101()));
		}
		if (mod425.getBox102()>0) {
			vol.setOpRegEspRecEquivalencia(ensureBigDecimal(mod425.getBox102()));
		}
		if (mod425.getBox227()>0) {
			vol.setOpRegEspBienesUsados(ensureBigDecimal(mod425.getBox227()));
		}
		if (mod425.getBox228()>0) {
			vol.setOpRegEspAgViajes(ensureBigDecimal(mod425.getBox228()));
		}
		if (mod425.getBox106()>0) {
			vol.setEntregasBienesInmuebles(ensureBigDecimal(mod425.getBox106()));
		}
		if (mod425.getBox107()>0) {
			vol.setEntregasBienesInversion(ensureBigDecimal(mod425.getBox107()));
		}
		vol.setTotalVolOp(ensureBigDecimal(mod425.getBox108()));
		return vol;
	}

	private static OpEspecificas getOpEspecificas(Mod4252025 mod425) {
		OpEspecificas op = new OpEspecificas();
		if (mod425.getBox230()>0) {
			op.setAdqInterioresExentas(ensureBigDecimal(mod425.getBox230()));
		}
		if (mod425.getBox109()>0) {
			op.setAdqIntracomunitariasExentas(ensureBigDecimal(mod425.getBox109()));
		}
		if (mod425.getBox231()>0) {
			op.setImportacionesExentas(ensureBigDecimal(mod425.getBox231()));
		}
		if (mod425.getBox232()>0) {
			op.setBasesIVASoportadoNoDeducible(ensureBigDecimal(mod425.getBox232()));
		}
		if (mod425.getBox111()>0) {
			op.setOpSujetas(ensureBigDecimal(mod425.getBox111()));
		}
		if (mod425.getBox113()>0) {
			op.setEntregasInteriores(ensureBigDecimal(mod425.getBox113()));
		}
		if (mod425.getBox523()>0) {
			op.setServInversionSP(ensureBigDecimal(mod425.getBox523()));
		}
		if (mod425.getBox654()>0 || mod425.getBox655()>0) {
			EntregasCriterioCajaBase eccj =  new EntregasCriterioCajaBase();
			eccj.setTipoX(new TipoBaseImponibleYCuota());
			eccj.getTipoX().setBI(ensureBigDecimal(mod425.getBox654()));
			eccj.getTipoX().setCuota(ensureBigDecimal(mod425.getBox655()));
			op.setEntregasCriterioCajaBase(eccj);
		}
		if (mod425.getBox656()>0 || mod425.getBox657()>0) {
			AdqCriterioCajaBase accj =  new AdqCriterioCajaBase();
			accj.setTipoX(new TipoBaseImponibleYCuota());
			accj.getTipoX().setBI(ensureBigDecimal(mod425.getBox656()));
			accj.getTipoX().setCuota(ensureBigDecimal(mod425.getBox657()));
			op.setAdqCriterioCajaBase(accj);
		}
		return op;
	}

}
