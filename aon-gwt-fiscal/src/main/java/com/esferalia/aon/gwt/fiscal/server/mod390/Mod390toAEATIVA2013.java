package com.esferalia.aon.gwt.fiscal.server.mod390;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.gwt.common.shared.LegalRepresentative;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.Administraciones;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.DatEstadisticos;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.DatEstadisticos.Conjunta;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.DatEstadisticos.OpTercerasPax;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.DatEstadisticos.Otras;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.DatEstadisticos.Pral;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.DatIdent;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.Devengo;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.Devengo.ConcursoUltPerNO;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.Devengo.ConcursoUltPerSI;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.Devengo.DecSustitutiva;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.Devengo.RegDevMensual;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.LiqAnual;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.OpEspecificas;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.AdqIntracomBienes;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.AdqIntracomServicios;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.IVAdevengadoInversionSP;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.ModBasesyCuotas;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.ModBasesyCuotasConcursoAcreedores;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.ModRecargoEquivalencia;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.ModRecargoEquivalenciaConcursoAcreedores;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.OpIntragrupo;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.RecargoEquivalencia;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.RegAgViajes;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.RegBienesUsados;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.RegOrdinario;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.BaseImponibleyCuota.TotalBasesyCuotasIVA;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.AdqIntracomunitariasBienesCorrientes;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.AdqIntracomunitariasBienesInversion;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.AdqIntracomunitariasServicios;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.ComRegAgricGanadPesca;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.ImportacionesBienesCorrientes;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.ImportacionesBienesInversion;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.OpInterioresBienesInversion;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.OpInterioresBienesServiciosCorrientes;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.OpIntragrupoBienesInversion;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.OpIntragrupoCorrientes;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegGeneral.Deducciones.RectifDeducciones;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegSimplificado;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegSimplificado.ActAgricGanadForest;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegSimplificado.Actividad;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegSimplificado.Actividad.Modulo;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegSimplificado.IvaDeducible;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.RegSimplificado.IvaDevengado;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.ResLiquidaciones;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.ResLiquidaciones.PerNoRegGrupos;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.ResLiquidaciones.PerSiRegGrupos;
import com.esferalia.aon.gwt.fiscal.server.mod390.AEATIVA2013.VolOperaciones;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoGrupoEntidades.Art65NO;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoGrupoEntidades.Art65SI;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoGrupoEntidades.Dependiente;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoGrupoEntidades.Dominante;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoGrupoEntidades.UltAutoliquidNO;
import com.esferalia.aon.gwt.fiscal.server.mod390.TipoGrupoEntidades.UltAutoliquidSI;
import com.esferalia.aon.gwt.fiscal.shared.Activity;
import com.esferalia.aon.gwt.fiscal.shared.Address;
import com.esferalia.aon.gwt.fiscal.shared.FarmerRegimeActivity;
import com.esferalia.aon.gwt.fiscal.shared.FiscalEnum.Mod390DetailKey;
import com.esferalia.aon.gwt.fiscal.shared.Mod390;
import com.esferalia.aon.gwt.fiscal.shared.Mod390Detail;
import com.esferalia.aon.gwt.fiscal.shared.SimpliedRegimeActivity;

public class Mod390toAEATIVA2013 {

	public static AEATIVA2013 getAEATIVA2013(Mod390 mod390) {
		AEATIVA2013 iva = new AEATIVA2013();
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
			tipf.setApe2(toUppercase(mod390.getSecondSurname()));;
			tp.setIdent(tipf);
			datIdent.setPersFisica(tp);
		}
		if (AonUtil.isNotEmpty(mod390.getContactPhone())) {
			datIdent.setTelefono(mod390.getContactPhone());
		}
		iva.setDatIdent(datIdent);
		
		iva.setDevengo(getDevengo(mod390));
		iva.setDatEstadisticos(getStatisticalData(mod390));
		if ( mod390.isLegalEntity() ) {
			iva.getRepresentanteJuridica().addAll( getRepresentanteJuridica(mod390) );
		} else {
			iva.setRepresentanteFisica( getRepresentanteFisica(mod390) );
		}
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
		
		// TODO
		// iva.prorratas
		// TODO
		// iva.ivaDeducibleGrupo1
		// TODO
		// iva.ivaDeducibleGrupo2
		// TODO
		// iva.ivaDeducibleGrupo3
		
		return iva;
	}

	private static RegSimplificado getRegSimplificado(Mod390 mod390) {
		RegSimplificado reg = null;
		boolean something = false;
		if (mod390.getSimpRegime1() != null) {
			Actividad actividad = getActividad(mod390.getSimpRegime1());
			reg = new RegSimplificado();
			reg.getActividad().add(actividad);
			something = true;
		}
		if (mod390.getSimpRegime2() != null) {
			Actividad actividad = getActividad(mod390.getSimpRegime2());
			reg = reg==null?new RegSimplificado():reg;
			reg.getActividad().add(actividad);
			something = true;
		}
		if (mod390.getFarmerRegime1() != null) {
			ActAgricGanadForest act = getActAgricGanadForest(mod390.getFarmerRegime1());
			reg = reg==null?new RegSimplificado():reg;
			reg.getActAgricGanadForest().add(act);
			something = true;
		}
		if (mod390.getFarmerRegime2() != null) {
			ActAgricGanadForest act = getActAgricGanadForest(mod390.getFarmerRegime2());
			reg = reg==null?new RegSimplificado():reg;
			reg.getActAgricGanadForest().add(act);
			something = true;
		}
		if (mod390.getFarmerRegime3() != null) {
			ActAgricGanadForest act = getActAgricGanadForest(mod390.getFarmerRegime3());
			reg = reg==null?new RegSimplificado():reg;
			reg.getActAgricGanadForest().add(act);
			something = true;
		}
		if (mod390.getFarmerRegime4() != null) {
			ActAgricGanadForest act = getActAgricGanadForest(mod390.getFarmerRegime4());
			reg = reg==null?new RegSimplificado():reg;
			reg.getActAgricGanadForest().add(act);
			something = true;
		}
		if (mod390.getFarmerRegime5() != null) {
			ActAgricGanadForest act = getActAgricGanadForest(mod390.getFarmerRegime5());
			reg = reg==null?new RegSimplificado():reg;
			reg.getActAgricGanadForest().add(act);
			something = true;
		}
		if (something) {
			IvaDevengado ivaDev = new IvaDevengado();
			ivaDev.setSumaCuotasNoAgric(ensureBigDecimal(mod390.getBox74()));
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
		act.setVolIngresos(ensureBigDecimal( ac.getIncomes()));
		act.setIndCuota(ensureBigDecimal( ac.getQuotaIndex(),4));
		act.setCuotaDevengada(ensureBigDecimal( ac.getAccrualQuota()));
		act.setCuotasSoportadas(ensureBigDecimal( ac.getInputQuotas()));
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
		actividad.setCuotaDevengada(ensureBigDecimal(sr.getBoxC()));
		actividad.setCuotaSoportada(ensureBigDecimal(sr.getBoxD()));
		actividad.setIndiceCorrector(ensureBigDecimal(sr.getBoxE()));
		actividad.setResultado(ensureBigDecimal(sr.getBoxF()));
		actividad.setPorcCuotaMinima(ensureBigDecimal(sr.getBoxG()));
		actividad.setDevCuotaSopOtrosPaises(ensureBigDecimal(sr.getBoxH()));
		actividad.setCuotaMinima(ensureBigDecimal(sr.getBoxI()));
		actividad.setCuotaRegSimplificado(ensureBigDecimal(sr.getBoxJ()));
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

	private static Devengo getDevengo(Mod390 mod390) {
		Devengo devengo = new Devengo();
		devengo.setEjercicio(mod390.getYear());
		if (mod390.isInsolvencyDeclarations()) {
			devengo.setConcursoUltPerSI( new ConcursoUltPerSI());	
		} else {
			devengo.setConcursoUltPerNO( new ConcursoUltPerNO());
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
		return devengo;
	}
	
	private static DatEstadisticos getStatisticalData(Mod390 mod390) {
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
		if (!AonUtil.isEmpty(mod390.getMergedDeclarationDocument())) {
			Conjunta conjunta = new Conjunta();
			conjunta.setNIF(mod390.getMergedDeclarationDocument());
			conjunta.setRazonSocial(mod390.getMergedDeclarationName());
			datEstadisticos.setConjunta(conjunta);
		}
		return datEstadisticos;
	}

	private static ArrayList<TipoRepresentanteJuridica> getRepresentanteJuridica(Mod390 mod390) {
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
				// TODO
				trj.setFechaPoder( lr.getNotaryDate().toString() );
				list.add(trj);
			}
		}
		return list;
	}

	private static TipoRepresentanteFisica getRepresentanteFisica(Mod390 mod390) {
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
			if (AonUtil.isNotEmpty(address.getRstreetName())) {
				domicilio.setViaPublica(toUppercase(address.getRstreetName()));
				something = true;
			}
			if (AonUtil.isNotEmpty(address.getRstreetType())) { 
				domicilio.setSG(toUppercase(address.getRstreetType()));
				something = true;
			}
			if (AonUtil.isNotEmpty(address.getRstreetNumber())) {
				domicilio.setNum(toUppercase(address.getRstreetNumber()));
				something = true;
			}
			if (AonUtil.isNotEmpty(address.getRstreetStair())) {
				domicilio.setEsc(toUppercase(address.getRstreetStair()));
				something = true;
			}
			if (AonUtil.isNotEmpty(address.getRstreetFloor())) {
				domicilio.setPiso(toUppercase(address.getRstreetFloor()));
				something = true;
			}
			if (AonUtil.isNotEmpty(address.getRstreetDoor())) {
				domicilio.setPuerta(toUppercase(address.getRstreetDoor()));
				something = true;
			}
			if (AonUtil.isNotEmpty(address.getRphone())) {
				domicilio.setTelefono(toUppercase(address.getRphone()));
				something = true;
			}
			if (AonUtil.isNotEmpty(address.getRzip())) {
				domicilio.setCPostal(toUppercase(address.getRzip()));
				something = true;
			}
			if (AonUtil.isNotEmpty(address.getRtown())) {
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

	private static RegGeneral getRegGeneral(Mod390 mod390) {
		RegGeneral regGeneral = new RegGeneral();
		BaseImponibleyCuota bases = getBaseImponibleyCuota(mod390);
		regGeneral.setBaseImponibleyCuota(bases);
		Deducciones ded = getDeducciones(mod390); 
		regGeneral.setDeducciones(ded);
		regGeneral.setResRegGeneral(getResRegGeneral(mod390));
		return regGeneral;
	}
	
	private static String getResRegGeneral(Mod390 mod390) {
		Mod390Detail detail = getKey(mod390,Mod390DetailKey.K37);
		String total = null;
		if (detail != null) {
			total = Double.toString( AonUtil.round(detail.getQuota()) ); 			
		}
		return total;
	}


	private static Deducciones getDeducciones(Mod390 mod390) {
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
		deducciones.setRegularizInversiones(getRegularizInversiones(mod390));
		deducciones.setRegularizPorcProrrata(getRegularizPorcProrrata(mod390));
		deducciones.setSumDeducciones(getSumDeducciones(mod390));
		return deducciones;
	}

	private static BigDecimal getSumDeducciones(Mod390 mod390) {
		BigDecimal op = null;
		Mod390Detail detail = getKey(mod390,Mod390DetailKey.K36);
		if (detail != null) {
			op = ensureBigDecimal(detail.getQuota());
		}
		return op;
	}


	private static BigDecimal getRegularizPorcProrrata(Mod390 mod390) {
		BigDecimal op = null;
		Mod390Detail detail = getKey(mod390,Mod390DetailKey.K35);
		if (detail != null) {
			op = ensureBigDecimal(detail.getQuota());
		}
		return op;
	}


	private static BigDecimal getRegularizInversiones(Mod390 mod390) {
		BigDecimal op = null;
		Mod390Detail detail = getKey(mod390,Mod390DetailKey.K34);
		if (detail != null) {
			op = ensureBigDecimal(detail.getQuota());
		}
		return op;
	}


	private static RectifDeducciones getRectifDeducciones(Mod390 mod390) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K33));
		if (tipo != null) {
			RectifDeducciones op = new RectifDeducciones();
			op.setTipoX(tipo);
			return op;
		}
		return null;
	}


	private static ComRegAgricGanadPesca getComRegAgricGanadPesca(Mod390 mod390) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K32));
		if (tipo != null) {
			ComRegAgricGanadPesca op = new ComRegAgricGanadPesca();
			op.setTipoX(tipo);
			return op;
		}
		return null;
	}


	private static AdqIntracomunitariasServicios getAdqIntracomunitariasServicios(Mod390 mod390) {
		AdqIntracomunitariasServicios op = new AdqIntracomunitariasServicios();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K30_04)));
		op.setTipo7(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K30_07)));
		op.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K30_08)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K30_10)));
		op.setTipo16(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K30_16)));
		op.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K30_18)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K30_21)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K31)));
		return op;
	}


	private static AdqIntracomunitariasBienesInversion getAdqIntracomunitariasBienesInversion(Mod390 mod390) {
		AdqIntracomunitariasBienesInversion op = new AdqIntracomunitariasBienesInversion();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K28_04)));
		op.setTipo7(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K28_07)));
		op.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K28_08)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K28_10)));
		op.setTipo16(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K28_16)));
		op.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K28_18)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K28_21)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K29)));
		return op;
	}


	private static AdqIntracomunitariasBienesCorrientes getAdqIntracomunitariasBienesCorrientes(Mod390 mod390) {
		AdqIntracomunitariasBienesCorrientes op = new AdqIntracomunitariasBienesCorrientes();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K26_04)));
		op.setTipo7(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K26_07)));
		op.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K26_08)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K26_10)));
		op.setTipo16(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K26_16)));
		op.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K26_18)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K26_21)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K27)));
		return op;
	}


	private static ImportacionesBienesInversion getImportacionesBienesInversion(Mod390 mod390) {
		ImportacionesBienesInversion op = new ImportacionesBienesInversion();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K24_04)));
		op.setTipo7(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K24_07)));
		op.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K24_08)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K24_10)));
		op.setTipo16(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K24_16)));
		op.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K24_18)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K24_21)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K25)));
		return op;
	}


	private static ImportacionesBienesCorrientes getImportacionesBienesCorrientes(Mod390 mod390) {
		ImportacionesBienesCorrientes op = new ImportacionesBienesCorrientes();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K22_04)));
		op.setTipo7(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K22_07)));
		op.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K22_08)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K22_10)));
		op.setTipo16(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K22_16)));
		op.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K22_18)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K22_21)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K23)));
		return op;
	}


	private static OpIntragrupoBienesInversion getOpIntragrupoBienesInversion(Mod390 mod390) {
		OpIntragrupoBienesInversion op = new OpIntragrupoBienesInversion();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K20_04)));
		op.setTipo7(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K20_07)));
		op.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K20_08)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K20_10)));
		op.setTipo16(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K20_16)));
		op.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K20_18)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K20_21)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K21)));
		return op;
	}


	private static OpInterioresBienesInversion getOpInterioresBienesInversion(Mod390 mod390) {
		OpInterioresBienesInversion op = new OpInterioresBienesInversion();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K18_04)));
		op.setTipo7(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K18_07)));
		op.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K18_08)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K18_10)));
		op.setTipo16(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K18_16)));
		op.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K18_18)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K18_21)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K19)));
		return op;
	}


	private static OpIntragrupoCorrientes getOpIntragrupoCorrientes(Mod390 mod390) {
		OpIntragrupoCorrientes op = new OpIntragrupoCorrientes();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K16_04)));
		op.setTipo7(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K16_07)));
		op.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K16_08)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K16_10)));
		op.setTipo16(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K16_16)));
		op.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K16_18)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K16_21)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K17)));
		return op;
	}


	private static OpInterioresBienesServiciosCorrientes getOpInterioresBienesServiciosCorrientes(Mod390 mod390) {
		OpInterioresBienesServiciosCorrientes op = new OpInterioresBienesServiciosCorrientes();
		op.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K14_04)));
		op.setTipo7(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K14_07)));
		op.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K14_08)));
		op.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K14_10)));
		op.setTipo16(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K14_16)));
		op.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K14_18)));
		op.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K14_21)));
		op.setTotal(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K15)));
		return op;
	}


	private static BaseImponibleyCuota getBaseImponibleyCuota(Mod390 mod390) {
		BaseImponibleyCuota b = new BaseImponibleyCuota();
		b.setRegOrdinario(getRegOrdinario(mod390));
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


	private static BigDecimal getTotalCuotasIVA(Mod390 mod390) {
		Mod390Detail detail = getKey(mod390,Mod390DetailKey.K13);
		BigDecimal totalCuotasIVA = null; 
		if (detail != null) {
			totalCuotasIVA = ensureBigDecimal(detail.getQuota()); 			
		}
		return totalCuotasIVA;
	}


	private static ModRecargoEquivalenciaConcursoAcreedores getModRecargoEquivalenciaConcursoAcreedores(Mod390 mod390) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K12));
		if (tipo != null) {
			ModRecargoEquivalenciaConcursoAcreedores modRecargoEquivalenciaConcursoAcreedores = new ModRecargoEquivalenciaConcursoAcreedores();
			modRecargoEquivalenciaConcursoAcreedores.setTipoX(tipo);
			return modRecargoEquivalenciaConcursoAcreedores;
		}
		return null;
	}


	private static ModRecargoEquivalencia getModRecargoEquivalencia(Mod390 mod390) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K11));
		if (tipo != null) {
			ModRecargoEquivalencia modRecargoEquivalencia = new ModRecargoEquivalencia();
			modRecargoEquivalencia.setTipoX(tipo);
			return modRecargoEquivalencia;
		}
		return null;
	}


	private static RecargoEquivalencia getRecargoEquivalencia(Mod390 mod390) {
		RecargoEquivalencia recargoEquivalencia = new RecargoEquivalencia();
		recargoEquivalencia.setTipo05(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K10_05)));
		recargoEquivalencia.setTipo1(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K10_1)));
		recargoEquivalencia.setTipo14(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K10_14)));
		recargoEquivalencia.setTipo175(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K10_175)));
		recargoEquivalencia.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K10_4)));
		recargoEquivalencia.setTipo52(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K10_52)));
		return recargoEquivalencia;
	}


	private static TotalBasesyCuotasIVA getTotalBasesyCuotasIVA(Mod390 mod390) {
		TotalBasesyCuotasIVA totalBasesyCuotasIVA = new TotalBasesyCuotasIVA();
		totalBasesyCuotasIVA.setTipoX(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K09)));
		return totalBasesyCuotasIVA;
	}


	private static ModBasesyCuotasConcursoAcreedores getModBasesyCuotasConcursoAcreedores(
			Mod390 mod390) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K08));
		if (tipo != null) {
			ModBasesyCuotasConcursoAcreedores modBases = new ModBasesyCuotasConcursoAcreedores();
			modBases.setTipoX(tipo);
			return modBases;
		}
		return null;
	}


	private static ModBasesyCuotas getModBasesyCuotas(Mod390 mod390) {
		TipoBaseImponibleYCuota tipo = getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K07));
		if (tipo != null) {
			ModBasesyCuotas modBasesyCuotas = new ModBasesyCuotas();
			modBasesyCuotas.setTipoX(tipo);
			return modBasesyCuotas;
		}
		return null;
	}


	private static IVAdevengadoInversionSP getIVAdevengadoInversionSP(Mod390 mod390) {
		IVAdevengadoInversionSP iVAdevengadoInversionSP = new IVAdevengadoInversionSP();
		iVAdevengadoInversionSP.setTipoX(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K06)));
		return iVAdevengadoInversionSP;
	}


	private static AdqIntracomServicios getAdqIntracomServicios(Mod390 mod390) {
		AdqIntracomServicios adqIntracomServicios = new AdqIntracomServicios();
		adqIntracomServicios.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K05_04)));
		adqIntracomServicios.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K05_08)));
		adqIntracomServicios.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K05_10)));
		adqIntracomServicios.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K05_18)));
		adqIntracomServicios.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K05_21)));
		return adqIntracomServicios;
	}


	private static AdqIntracomBienes getAdqIntracomBienes(Mod390 mod390) {
		AdqIntracomBienes adqIntracomBienes = new AdqIntracomBienes();
		adqIntracomBienes.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K04_04)));
		adqIntracomBienes.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K04_08)));
		adqIntracomBienes.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K04_10)));
		adqIntracomBienes.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K04_18)));
		adqIntracomBienes.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K04_21)));
		return adqIntracomBienes;
	}


	private static RegAgViajes getRegAgViajes(Mod390 mod390) {
		RegAgViajes regAgViajes = new RegAgViajes();
		regAgViajes.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K03_18)));
		regAgViajes.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K03_21)));
		return regAgViajes;
	}


	private static RegBienesUsados getRegBienesUsados(Mod390 mod390) {
		RegBienesUsados regBienesUsados = new RegBienesUsados();
		regBienesUsados.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K02_04)));
		regBienesUsados.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K02_08)));
		regBienesUsados.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K02_10)));
		regBienesUsados.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K02_18)));
		regBienesUsados.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K02_21)));
		return regBienesUsados;
	}


	private static OpIntragrupo getOpIntragrupo(Mod390 mod390) {
		OpIntragrupo opIntragrupo = new OpIntragrupo();
		opIntragrupo.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K01_04)));
		opIntragrupo.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K01_08)));
		opIntragrupo.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K01_10)));
		opIntragrupo.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K01_18)));
		opIntragrupo.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K01_21)));
		return opIntragrupo;
	}


	private static RegOrdinario getRegOrdinario(Mod390 mod390) {
		RegOrdinario regOrdinario = new RegOrdinario();
		regOrdinario.setTipo4(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K00_04)));
		regOrdinario.setTipo8(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K00_08)));
		regOrdinario.setTipo10(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K00_10)));
		regOrdinario.setTipo18(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K00_18)));
		regOrdinario.setTipo21(getTipoBaseImponibleYCuota(getKey(mod390,Mod390DetailKey.K00_21)));
		return regOrdinario;
	}

	private static Mod390Detail getKey(Mod390 mod390, Mod390DetailKey key) {
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

	private static Administraciones getAdministraciones(Mod390 mod390) {
		if (mod390.getBox87() > 0.0 && mod390.getBox87() < 100.0) {
			Administraciones adm = new Administraciones();
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
			adm.setResTerrComun(ensureBigDecimal(mod390.getBox92()));
			adm.setComCuotasEjercicioAnteriorTerrComun(ensureBigDecimal(mod390.getBox93()));
			adm.setResLiqAnualTerrComun(ensureBigDecimal(mod390.getBox94()));
			return adm;
		}
		return null;
	}


	private static LiqAnual getLiqAnual(Mod390 mod390) {
		LiqAnual liq = new LiqAnual();
		liq.setSumResultados(ensureBigDecimal(mod390.getBox84()) );
		if (mod390.getBox85() > 0) {
			liq.setCompCuotasEjercicioAnterior(ensureBigDecimal(mod390.getBox85()));
		}
		liq.setResLiquidacion(ensureBigDecimal(mod390.getBox86()));
        return liq;
	}

	private static ResLiquidaciones getResLiquidaciones(Mod390 mod390) {
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

	private static VolOperaciones getVolOperaciones(Mod390 mod390) {
		VolOperaciones vol = new VolOperaciones();
		if (mod390.getBox99()>0) {
			vol.setOpRegGeneral(ensureBigDecimal(mod390.getBox99()));
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

	private static OpEspecificas getOpEspecificas(Mod390 mod390) {
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
		return op;
	}

}
