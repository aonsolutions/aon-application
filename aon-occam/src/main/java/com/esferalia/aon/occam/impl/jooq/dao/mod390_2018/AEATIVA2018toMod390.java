package com.esferalia.aon.occam.impl.jooq.dao.mod390_2018;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.esferalia.aon.occam.api.model.fiscal.Activity;
import com.esferalia.aon.occam.api.model.fiscal.Address;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018.DeductionRegime;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018.FarmerRegimeActivity;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018.Mod390Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018.Prorrata;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018.SimpliedRegimeActivity;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018DetailKey;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.Administraciones;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.DatEstadisticos;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.DatEstadisticos.Otras;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.DatIdent;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.Devengo;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.LiqAnual;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.OpEspecificas;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.Prorratas;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.Prorratas.Pro;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.BaseImponibleyCuota;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2018.AEATIVA2018.RegGeneral.Deducciones;
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
import com.esferalia.aon.watson.util.AonStringUtils;

public class AEATIVA2018toMod390 {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	public static void populate(Mod3902018 mod390, AEATIVA2018 iva)
			throws ParseException {
		DatIdent datIdent = iva.getDatIdent();
		if (!mod390.isLegalEntity()) {
			TipoPersonaFisica tpf = datIdent.getPersFisica();
			TipoIdentificacionPersonaFisica tipf = tpf.getIdent();
			mod390.setName(tipf.getNombre());
			mod390.setFirstSurname(tipf.getApe1());
			mod390.setSecondSurname(tipf.getApe2());
		} else {
			TipoPersonaJuridica tpj = datIdent.getPersJuridica();
			TipoIdentificacionPersonaJuridica tipj = tpj.getIdentPersJuridica();
			mod390.setName(tipj.getRazonSocial());
		}
		mod390.setContactPhone(datIdent.getTelefono());

		Devengo devengo = iva.getDevengo();
		TipoConcursoUltPer tipo = devengo.getConcursoAcreedoresSI();
		if (tipo != null) {
			mod390.setInsolvencyStateThisYear(true);
			if (tipo.getConcursoUltPerSI() != null) {
				mod390.setInsolvencyStateLastPeriod(true);
			}
		}
		
		mod390.setTaxRefund(devengo.getRegDevMensual() != null);
		TipoGrupoEntidades tge = devengo.getRegGrupoEntidades();
		if (tge != null) {
			mod390.setSpecialGroupRegime(true);
			mod390.setGroupNumber(tge.getNumGrupo());
			mod390.setGroupDependent(tge.getDependiente() != null);
			mod390.setGroupDependent(!(tge.getDominante() != null));
			if (tge.getArt65SI() != null) {
				mod390.setGroupRegimeType(true);
				mod390.setGroupDocument(tge.getNIFEntidadDominante());
			} else {
				mod390.setGroupRegimeType(false);
			}
			mod390.setGroupDeclarations(tge.getUltAutoliquidSI() != null);
		}
		if (devengo.getRegCriterioCajaSI() != null) {
			mod390.setAccrualRegime(true);
		}
		if (devengo.getDestRegCriterioCajaSI() != null) {
			mod390.setAccrualRegimeTarget(true);
		}
		
		DatEstadisticos dat = iva.getDatEstadisticos();
		if (dat.getPral() != null) {
			Activity activity = new Activity();
			activity.setKey(dat.getPral().getClave());
			activity.setDescription(dat.getPral().getDescripcion());
			activity.setEpigraph(dat.getPral().getEpigrafe());
			mod390.setMainActivity(activity);
		}
		int i = 1;
		for (Otras otras : dat.getOtras()) {
			if (otras != null) {
				Activity activity = new Activity();
				activity.setKey(otras.getClave());
				activity.setDescription(otras.getDescripcion());
				activity.setEpigraph(otras.getEpigrafe());
				if (i == 1) {
					mod390.setActivity1(activity);
				} else if (i == 2) {
					mod390.setActivity2(activity);
				} else if (i == 3) {
					mod390.setActivity3(activity);
				} else if (i == 4) {
					mod390.setActivity4(activity);
				} else if (i == 5) {
					mod390.setActivity5(activity);
				}
			}
			++i;
		}
		mod390.setMod347(dat.getOpTercerasPax() != null);
		if (dat.getConjunta() != null) {
			mod390.setMergedDeclarationDocument(dat.getConjunta().getNIF());
			mod390.setMergedDeclarationName(dat.getConjunta().getRazonSocial());
		}
		if (iva.getRepresentanteFisica() != null) {
			TipoRepresentanteFisica trf = iva.getRepresentanteFisica();
			TipoIdentificacionPersonaJuridica tipj = trf.getIdent();
			Address adr = new Address();
			if (tipj != null) {
				adr.setRdocument(tipj.getNIF());
				adr.setRname(tipj.getRazonSocial());
			}
			TipoDomicilio dom = trf.getDomicilio();
			if (dom != null) {
				adr.setRstreetType(dom.getSG());
				adr.setRstreetName(dom.getViaPublica());
				adr.setRstreetNumber(dom.getNum());
				adr.setRstreetStair(dom.getEsc());
				adr.setRstreetFloor(dom.getPiso());
				adr.setRstreetDoor(dom.getPuerta());
				adr.setRphone(dom.getTelefono());
				adr.setRtown(dom.getMunicipio());
				try {
					adr.setRprovince(Integer.parseInt(dom.getCodProv()));
				} catch (NumberFormatException e) {
					// Nothing.
				}
				adr.setRzip(dom.getCPostal());
			}
			mod390.setAddress(adr);
		}
		List<TipoRepresentanteJuridica> list = iva.getRepresentanteJuridica();
		i = 1;
		if (list != null && list.size() > 0) {
			for (TipoRepresentanteJuridica trj : list) {
				LegalRepresentative lg = new LegalRepresentative();
				lg.setDocument(trj.getNIF());
				lg.setName(trj.getNombre());
				lg.setNotary(trj.getNotaria());

				String fec = trj.getFechaPoder();
				lg.setNotaryDate(AonStringUtils.isBlank(fec)? null : DATE_FORMAT.parse(fec));

				if (i == 1) {
					mod390.setLegalRepr1(lg);
				} else if (i == 2) {
					mod390.setLegalRepr2(lg);
				} else if (i == 3) {
					mod390.setLegalRepr3(lg);
				}
				i++;
			}
		}

		Map<Mod3902018DetailKey, Mod390Detail> map = new TreeMap<Mod3902018DetailKey, Mod390Detail>();
		mod390.setGeneralRegime(map);

		if (iva.getRegGeneral() != null) {
			BaseImponibleyCuota b = iva.getRegGeneral()
					.getBaseImponibleyCuota();
			if (b != null) {
				if (b.getRegOrdinario() != null) {
					put(mod390, Mod3902018DetailKey.C0002, b.getRegOrdinario()
							.getTipo4());
					put(mod390, Mod3902018DetailKey.C0004, b.getRegOrdinario()
							.getTipo10());
					put(mod390, Mod3902018DetailKey.C0006, b.getRegOrdinario()
							.getTipo21());
				}
				if (b.getRegCriterioCaja() != null) {
					put(mod390, Mod3902018DetailKey.C0644, b.getRegCriterioCaja().getTipo4());
					put(mod390, Mod3902018DetailKey.C0646, b.getRegCriterioCaja().getTipo10());
					put(mod390, Mod3902018DetailKey.C0648, b.getRegCriterioCaja().getTipo21());
				}
				if (b.getOpIntragrupo() != null) {
					put(mod390, Mod3902018DetailKey.C0501, b.getOpIntragrupo()
							.getTipo4());
					put(mod390, Mod3902018DetailKey.C0503, b.getOpIntragrupo()
							.getTipo10());
					put(mod390, Mod3902018DetailKey.C0505, b.getOpIntragrupo()
							.getTipo21());
				}
				if (b.getRegBienesUsados() != null) {
					put(mod390, Mod3902018DetailKey.C0008, b.getRegBienesUsados()
							.getTipo4());
					put(mod390, Mod3902018DetailKey.C0010, b.getRegBienesUsados()
							.getTipo10());
					put(mod390, Mod3902018DetailKey.C0012, b.getRegBienesUsados()
							.getTipo21());
				}
				if (b.getRegAgViajes() != null) {
					put(mod390, Mod3902018DetailKey.C0014, b.getRegAgViajes()
							.getTipo21());
				}
				if (b.getAdqIntracomBienes() != null) {
					put(mod390, Mod3902018DetailKey.C0022, b
							.getAdqIntracomBienes().getTipo4());
					put(mod390, Mod3902018DetailKey.C0024, b
							.getAdqIntracomBienes().getTipo10());
					put(mod390, Mod3902018DetailKey.C0026, b
							.getAdqIntracomBienes().getTipo21());
				}
				if (b.getAdqIntracomServicios() != null) {
					put(mod390, Mod3902018DetailKey.C0546, b
							.getAdqIntracomServicios().getTipo4());
					put(mod390, Mod3902018DetailKey.C0548, b
							.getAdqIntracomServicios().getTipo10());
					put(mod390, Mod3902018DetailKey.C0552, b
							.getAdqIntracomServicios().getTipo21());
				}
				if (b.getIVAdevengadoInversionSP() != null) {
					put(mod390, Mod3902018DetailKey.C0028, b
							.getIVAdevengadoInversionSP().getTipoX());
				}
				if (b.getModBasesyCuotas() != null) {
					put(mod390, Mod3902018DetailKey.C0030, b.getModBasesyCuotas()
							.getTipoX());
				}
				if (b.getModBasesyCuotasConcursoAcreedores() != null) {
					put(mod390, Mod3902018DetailKey.C0032, b
							.getModBasesyCuotasConcursoAcreedores().getTipoX());
				}
				if (b.getTotalBasesyCuotasIVA() != null) {
					put(mod390, Mod3902018DetailKey.C0034, b
							.getTotalBasesyCuotasIVA().getTipoX());
				}
				if (b.getRecargoEquivalencia() != null) {
					put(mod390, Mod3902018DetailKey.C0036, b
							.getRecargoEquivalencia().getTipo05());
					put(mod390, Mod3902018DetailKey.C0600, b
							.getRecargoEquivalencia().getTipo14());
					put(mod390, Mod3902018DetailKey.C0042, b
							.getRecargoEquivalencia().getTipo175());
					put(mod390, Mod3902018DetailKey.C0602, b
							.getRecargoEquivalencia().getTipo52());
				}
				if (b.getModRecargoEquivalencia() != null) {
					put(mod390, Mod3902018DetailKey.C0044, b
							.getModRecargoEquivalencia().getTipoX());
				}
				if (b.getModRecargoEquivalenciaConcursoAcreedores() != null) {
					put(mod390, Mod3902018DetailKey.C0046, b
							.getModRecargoEquivalenciaConcursoAcreedores()
							.getTipoX());
				}
				if (b.getTotalCuotasIVA() != null) {
					put(mod390, Mod3902018DetailKey.C0047, b.getTotalCuotasIVA());
				}
			}
			if (iva.getRegGeneral().getDeducciones() != null) {
				Deducciones d = iva.getRegGeneral().getDeducciones();
				if (d.getOpInterioresBienesServiciosCorrientes() != null) {
					put(mod390, Mod3902018DetailKey.C0191, d
							.getOpInterioresBienesServiciosCorrientes()
							.getTipo4());
					put(mod390, Mod3902018DetailKey.C0604, d
							.getOpInterioresBienesServiciosCorrientes()
							.getTipo10());
					put(mod390, Mod3902018DetailKey.C0606, d
							.getOpInterioresBienesServiciosCorrientes()
							.getTipo21());
					put(mod390, Mod3902018DetailKey.C0049, d
							.getOpInterioresBienesServiciosCorrientes()
							.getTotal());
				}
				if (d.getOpIntragrupoCorrientes() != null) {
					put(mod390, Mod3902018DetailKey.C0507, d
							.getOpIntragrupoCorrientes().getTipo4());
					put(mod390, Mod3902018DetailKey.C0608, d
							.getOpIntragrupoCorrientes().getTipo10());
					put(mod390, Mod3902018DetailKey.C0610, d
							.getOpIntragrupoCorrientes().getTipo21());
					put(mod390, Mod3902018DetailKey.C0513, d
							.getOpIntragrupoCorrientes()
							.getTotal());
				}
				if (d.getOpInterioresBienesInversion() != null) {
					put(mod390, Mod3902018DetailKey.C0197, d
							.getOpInterioresBienesInversion().getTipo4());
					put(mod390, Mod3902018DetailKey.C0612, d
							.getOpInterioresBienesInversion().getTipo10());
					put(mod390, Mod3902018DetailKey.C0614, d
							.getOpInterioresBienesInversion().getTipo21());
					put(mod390, Mod3902018DetailKey.C0051, d
							.getOpInterioresBienesInversion()
							.getTotal());
				}
				if (d.getOpIntragrupoBienesInversion() != null) {
					put(mod390, Mod3902018DetailKey.C0515, d
							.getOpIntragrupoBienesInversion().getTipo4());
					put(mod390, Mod3902018DetailKey.C0616, d
							.getOpIntragrupoBienesInversion().getTipo10());
					put(mod390, Mod3902018DetailKey.C0618, d
							.getOpIntragrupoBienesInversion().getTipo21());
					put(mod390, Mod3902018DetailKey.C0521, d
							.getOpIntragrupoBienesInversion()
							.getTotal());
				}
				if (d.getImportacionesBienesCorrientes() != null) {
					put(mod390, Mod3902018DetailKey.C0203, d
							.getImportacionesBienesCorrientes().getTipo4());
					put(mod390, Mod3902018DetailKey.C0620, d
							.getImportacionesBienesCorrientes().getTipo10());
					put(mod390, Mod3902018DetailKey.C0622, d
							.getImportacionesBienesCorrientes().getTipo21());
					put(mod390, Mod3902018DetailKey.C0053, d
							.getImportacionesBienesCorrientes()
							.getTotal());
				}
				if (d.getImportacionesBienesInversion() != null) {
					put(mod390, Mod3902018DetailKey.C0209, d
							.getImportacionesBienesInversion().getTipo4());
					put(mod390, Mod3902018DetailKey.C0624, d
							.getImportacionesBienesInversion().getTipo10());
					put(mod390, Mod3902018DetailKey.C0626, d
							.getImportacionesBienesInversion().getTipo21());
					put(mod390, Mod3902018DetailKey.C0055, d
							.getImportacionesBienesInversion()
							.getTotal());
				}
				if (d.getAdqIntracomunitariasBienesCorrientes() != null) {
					put(mod390, Mod3902018DetailKey.C0215, d
							.getAdqIntracomunitariasBienesCorrientes()
							.getTipo4());
					put(mod390, Mod3902018DetailKey.C0628, d
							.getAdqIntracomunitariasBienesCorrientes()
							.getTipo10());
					put(mod390, Mod3902018DetailKey.C0630, d
							.getAdqIntracomunitariasBienesCorrientes()
							.getTipo21());
					put(mod390, Mod3902018DetailKey.C0057, d
							.getAdqIntracomunitariasBienesCorrientes()
							.getTotal());
				}
				if (d.getAdqIntracomunitariasBienesInversion() != null) {
					put(mod390, Mod3902018DetailKey.C0221, d
							.getAdqIntracomunitariasBienesInversion()
							.getTipo4());
					put(mod390, Mod3902018DetailKey.C0632, d
							.getAdqIntracomunitariasBienesInversion()
							.getTipo10());
					put(mod390, Mod3902018DetailKey.C0634, d
							.getAdqIntracomunitariasBienesInversion()
							.getTipo21());
					put(mod390, Mod3902018DetailKey.C0059, d
							.getAdqIntracomunitariasBienesInversion()
							.getTotal());
				}
				if (d.getAdqIntracomunitariasServicios() != null) {
					put(mod390, Mod3902018DetailKey.C0588, d
							.getAdqIntracomunitariasServicios().getTipo4());
					put(mod390, Mod3902018DetailKey.C0636, d
							.getAdqIntracomunitariasServicios().getTipo10());
					put(mod390, Mod3902018DetailKey.C0638, d
							.getAdqIntracomunitariasServicios().getTipo21());
					put(mod390, Mod3902018DetailKey.C0598, d
							.getAdqIntracomunitariasServicios()
							.getTotal());
				}
				if (d.getComRegAgricGanadPesca() != null) {
					put(mod390, Mod3902018DetailKey.C0061, d
							.getComRegAgricGanadPesca().getTipoX());
				}
				if (d.getRectifDeducciones() != null) {
					put(mod390, Mod3902018DetailKey.C0062, d.getRectifDeducciones()
							.getTipoX());
				}
				if (d.getRectifOpIntragrupo() != null) {
					put(mod390, Mod3902018DetailKey.C0652, d.getRectifOpIntragrupo()
							.getTipoX());
				}
				if (d.getRegularizInversiones() != null) {
					put(mod390, Mod3902018DetailKey.C0063,
							d.getRegularizInversiones());
				}
				if (d.getRegularizPorcProrrata() != null) {
					put(mod390, Mod3902018DetailKey.C0522,
							d.getRegularizPorcProrrata());
				}
				if (d.getSumDeducciones() != null) {
					put(mod390, Mod3902018DetailKey.C0064, d.getSumDeducciones());
				}

			}
			String res = iva.getRegGeneral().getResRegGeneral();
			if (AonStringUtils.isNotEmpty(res)) {
				try {
					double val = Double.parseDouble(res);
					put(mod390, Mod3902018DetailKey.C0065, new BigDecimal(val));
				} catch (NumberFormatException e) {
					// Nothing
				}
			}
		}
		RegSimplificado reg = iva.getRegSimplificado();
		if (reg != null) {
			if (reg.getActividad().size() > 0) {
				Actividad actividad = reg.getActividad().get(0);
				SimpliedRegimeActivity sra = getSimpliedRegimeActivity(actividad);
				mod390.setSimpRegime1(sra);
			}
			if (reg.getActividad().size() > 1) {
				Actividad actividad = reg.getActividad().get(1);
				SimpliedRegimeActivity sra = getSimpliedRegimeActivity(actividad);
				mod390.setSimpRegime2(sra);
			}
			if (reg.getActAgricGanadForest().size() > 0) {
				ActAgricGanadForest actividad = reg.getActAgricGanadForest()
						.get(0);
				FarmerRegimeActivity sra = getFarmerRegimeActivity(actividad);
				mod390.setFarmerRegime1(sra);
			}
			if (reg.getActAgricGanadForest().size() > 1) {
				ActAgricGanadForest actividad = reg.getActAgricGanadForest()
						.get(1);
				FarmerRegimeActivity sra = getFarmerRegimeActivity(actividad);
				mod390.setFarmerRegime2(sra);
			}
			if (reg.getActAgricGanadForest().size() > 2) {
				ActAgricGanadForest actividad = reg.getActAgricGanadForest()
						.get(2);
				FarmerRegimeActivity sra = getFarmerRegimeActivity(actividad);
				mod390.setFarmerRegime3(sra);
			}
			if (reg.getActAgricGanadForest().size() > 3) {
				ActAgricGanadForest actividad = reg.getActAgricGanadForest()
						.get(3);
				FarmerRegimeActivity sra = getFarmerRegimeActivity(actividad);
				mod390.setFarmerRegime4(sra);
			}
			if (reg.getActAgricGanadForest().size() > 4) {
				ActAgricGanadForest actividad = reg.getActAgricGanadForest()
						.get(4);
				FarmerRegimeActivity sra = getFarmerRegimeActivity(actividad);
				mod390.setFarmerRegime5(sra);
			}
			IvaDevengado ivaDev = reg.getIvaDevengado();
			if (ivaDev != null) {
				mod390.setBox74(ensureBigDecimal(ivaDev.getSumaCuotasNoAgric()));
				mod390.setBox75(ensureBigDecimal(ivaDev.getSumaCuotasAgric()));
				mod390.setBox76(ensureBigDecimal(ivaDev
						.getAdqIntracomunitarias()));
				mod390.setBox77(ensureBigDecimal(ivaDev
						.getInversionSujetoPasivo()));
				mod390.setBox78(ensureBigDecimal(ivaDev
						.getEntregasActivosFijos()));
				mod390.setBox79(ensureBigDecimal(ivaDev.getTotalCuota()));
			}
			IvaDeducible ivaDed = reg.getIvaDeducible();
			if (ivaDed != null) {
				mod390.setBox80(ensureBigDecimal(ivaDed
						.getIVASoportadoAdqActivosFijos()));
				mod390.setBox81(ensureBigDecimal(ivaDed.getRegBienesInversion()));
				mod390.setBox82(ensureBigDecimal(ivaDed.getSumaDeducciones()));
			}
			mod390.setBox83(ensureBigDecimal(reg.getResRegimenSimplificado()));
		} else {
			mod390.setSimpRegime1(new SimpliedRegimeActivity());
			mod390.setSimpRegime2(new SimpliedRegimeActivity());
			mod390.setFarmerRegime1(new FarmerRegimeActivity());
			mod390.setFarmerRegime2(new FarmerRegimeActivity());
			mod390.setFarmerRegime3(new FarmerRegimeActivity());
			mod390.setFarmerRegime4(new FarmerRegimeActivity());
			mod390.setFarmerRegime5(new FarmerRegimeActivity());
		}

		Administraciones adm = iva.getAdministraciones();
		if (adm == null) {
			LiqAnual liq = iva.getLiqAnual();
			if (liq != null) {
				mod390.setBox658(ensureBigDecimal(liq.getRegCuotas()));
				mod390.setBox84(ensureBigDecimal(liq.getSumResultados()));
				mod390.setBox659(ensureBigDecimal(liq.getIvaAduana()));
				mod390.setBox85(ensureBigDecimal(liq.getCompCuotasEjercicioAnterior()));
				mod390.setBox86(ensureBigDecimal(liq.getResLiquidacion()));
			}
		} else {
			if (adm != null) {
				mod390.setBox658(ensureBigDecimal(adm.getRegCuotas()));
				mod390.setBox87(ensureBigDecimal(adm.getComun()));
				mod390.setBox88(ensureBigDecimal(adm.getArabaAlava()));
				mod390.setBox89(ensureBigDecimal(adm.getGipuzkoa()));
				mod390.setBox90(ensureBigDecimal(adm.getBizkaia()));
				mod390.setBox91(ensureBigDecimal(adm.getNavarra()));
				mod390.setBox84(ensureBigDecimal(adm.getSumResultados()));
				mod390.setBox659(ensureBigDecimal(adm.getIvaAduana()));
				mod390.setBox92(ensureBigDecimal(adm.getResTerrComun()));
				mod390.setBox93(ensureBigDecimal(adm.getComCuotasEjercicioAnteriorTerrComun()));
				mod390.setBox94(ensureBigDecimal(adm.getResLiqAnualTerrComun()));
			}
		}
		ResLiquidaciones res = iva.getResLiquidaciones();
		if (res != null) {
			PerNoRegGrupos perNo = res.getPerNoRegGrupos();
			if (perNo != null) {
				mod390.setBox95(ensureBigDecimal(perNo.getTotIngresosIVA()));
				mod390.setBox96(ensureBigDecimal(perNo
						.getTotDevIVASPRegDevMensual()));
				mod390.setBox524(ensureBigDecimal(perNo.getTotDevAdqElemTrans()));
				mod390.setBox97(ensureBigDecimal(perNo
						.getImporteACompensarUltimoPeriodo()));
				mod390.setBox98(ensureBigDecimal(perNo
						.getImporteADevolverUltimoPeriodo()));
				mod390.setBox662(ensureBigDecimal(perNo
						.getCuotasPendCompensar()));
			}
			PerSiRegGrupos perSi = res.getPerSiRegGrupos();
			if (perNo != null) {
				mod390.setBox525(ensureBigDecimal(perSi
						.getTotResulPositivos322()));
				mod390.setBox526(ensureBigDecimal(perSi
						.getTotResulNegativos322()));
			}
		}
		VolOperaciones vol = iva.getVolOperaciones();
		if (vol != null) {
			mod390.setBox99(ensureBigDecimal(vol.getOpRegGeneral()));
			mod390.setBox653(ensureBigDecimal(vol.getOpRegEspCriterioCaja()));
			mod390.setBox103(ensureBigDecimal(vol
					.getEntregasIntracomunitariasExentas()));
			mod390.setBox104(ensureBigDecimal(vol
					.getExportacionesExentasConDrchoDeduccion()));
			mod390.setBox105(ensureBigDecimal(vol
					.getOpExentasSinDrchoDeduccion()));
			mod390.setBox110(ensureBigDecimal(vol.getOpNoSujetas()));
			mod390.setBox112(ensureBigDecimal(vol
					.getEntregasBienesInstalacionOtrosEM()));
			mod390.setBox100(ensureBigDecimal(vol.getOpRegSimplificado()));
			mod390.setBox101(ensureBigDecimal(vol.getOpRegEspAgricPescGanad()));
			mod390.setBox102(ensureBigDecimal(vol.getOpRegEspRecEquivalencia()));
			mod390.setBox227(ensureBigDecimal(vol.getOpRegEspBienesUsados()));
			mod390.setBox228(ensureBigDecimal(vol.getOpRegEspAgViajes()));
			mod390.setBox106(ensureBigDecimal(vol.getEntregasBienesInmuebles()));
			mod390.setBox107(ensureBigDecimal(vol.getEntregasBienesInversion()));
			mod390.setBox108(ensureBigDecimal(vol.getTotalVolOp()));
		}
		OpEspecificas op = iva.getOpEspecificas();
		if (op != null) {
			mod390.setBox230(ensureBigDecimal(op.getAdqInterioresExentas()));
			mod390.setBox109(ensureBigDecimal(op
					.getAdqIntracomunitariasExentas()));
			mod390.setBox231(ensureBigDecimal(op.getImportacionesExentas()));
			mod390.setBox232(ensureBigDecimal(op
					.getBasesIVASoportadoNoDeducible()));
			mod390.setBox111(ensureBigDecimal(op.getOpSujetas()));
			mod390.setBox113(ensureBigDecimal(op.getEntregasInteriores()));
			mod390.setBox523(ensureBigDecimal(op.getServInversionSP()));
			if (op.getEntregasCriterioCajaBase() != null && op.getEntregasCriterioCajaBase().getTipoX() != null) {
				mod390.setBox654(ensureBigDecimal(op.getEntregasCriterioCajaBase().getTipoX().getBI()));
				mod390.setBox655(ensureBigDecimal(op.getEntregasCriterioCajaBase().getTipoX().getCuota()));
			}
			if (op.getAdqCriterioCajaBase() != null && op.getAdqCriterioCajaBase().getTipoX() != null) {
				mod390.setBox656(ensureBigDecimal(op.getAdqCriterioCajaBase().getTipoX().getBI()));
				mod390.setBox657(ensureBigDecimal(op.getAdqCriterioCajaBase().getTipoX().getCuota()));
			}
		}
		
		// PRORRATAS
		Prorratas prorratas = iva.getProrratas();
		if (prorratas != null ) {
			for (Pro pro : prorratas.getPro()) {
				Prorrata pr = new Prorrata();
				pr.setActivity( pro.getActividad() );
				pr.setCnae( pro.getCNAE() );
				pr.setAmount( ensureBigDecimal( pro.getImpOper() ) );
				pr.setAmountWithRight( ensureBigDecimal( pro.getImpOperConDrchoDed() ));
				pr.setPercent( ensureBigDecimal( pro.getPorc() ));
				pr.setType( pro.getTipo() );
				mod390.getProrratas().add(pr);
			}
		}
		
		if (iva.getIVADeducibleGrupo1() != null) {
			DeductionRegime regime = new DeductionRegime();
			mod390.setRegime1(regime);
			if (iva.getIVADeducibleGrupo1().getOpInteriores() != null) {
				if (iva.getIVADeducibleGrupo1().getOpInteriores().getBienesyServiciosCorrientes() != null) {
					regime.setBase1(ensureBigDecimal(iva.getIVADeducibleGrupo1().getOpInteriores().getBienesyServiciosCorrientes().getBI()));
					regime.setQuota1(ensureBigDecimal(iva.getIVADeducibleGrupo1().getOpInteriores().getBienesyServiciosCorrientes().getCuota()));
				}
				if (iva.getIVADeducibleGrupo1().getOpInteriores().getBienesInversion() != null) {
					regime.setBase2(ensureBigDecimal(iva.getIVADeducibleGrupo1().getOpInteriores().getBienesInversion().getBI()));
					regime.setQuota2(ensureBigDecimal(iva.getIVADeducibleGrupo1().getOpInteriores().getBienesInversion().getCuota()));
				}
			}
			if (iva.getIVADeducibleGrupo1().getImportaciones() != null) {
				if (iva.getIVADeducibleGrupo1().getImportaciones().getBienesCorrientes() != null) {
					regime.setBase3(ensureBigDecimal(iva.getIVADeducibleGrupo1().getImportaciones().getBienesCorrientes().getBI()));
					regime.setQuota3(ensureBigDecimal(iva.getIVADeducibleGrupo1().getImportaciones().getBienesCorrientes().getCuota()));
				}
				if (iva.getIVADeducibleGrupo1().getImportaciones().getBienesInversion() != null) {
					regime.setBase4(ensureBigDecimal(iva.getIVADeducibleGrupo1().getImportaciones().getBienesInversion().getBI()));
					regime.setQuota4(ensureBigDecimal(iva.getIVADeducibleGrupo1().getImportaciones().getBienesInversion().getCuota()));
				}
			}
			if (iva.getIVADeducibleGrupo1().getAdqIntracomunitarias() != null) {
				if (iva.getIVADeducibleGrupo1().getAdqIntracomunitarias().getBienesCorrientes() != null) {
					regime.setBase5(ensureBigDecimal(iva.getIVADeducibleGrupo1().getAdqIntracomunitarias().getBienesCorrientes().getBI()));
					regime.setQuota5(ensureBigDecimal(iva.getIVADeducibleGrupo1().getAdqIntracomunitarias().getBienesCorrientes().getCuota()));
				}
				if (iva.getIVADeducibleGrupo1().getAdqIntracomunitarias().getBienesInversion() != null) {
					regime.setBase6(ensureBigDecimal(iva.getIVADeducibleGrupo1().getAdqIntracomunitarias().getBienesInversion().getBI()));
					regime.setQuota6(ensureBigDecimal(iva.getIVADeducibleGrupo1().getAdqIntracomunitarias().getBienesInversion().getCuota()));
				}
			}
			if (iva.getIVADeducibleGrupo1().getCompRegEspAgricGanadPesca() != null) {
				regime.setBase7(ensureBigDecimal(iva.getIVADeducibleGrupo1().getCompRegEspAgricGanadPesca().getBI()));
				regime.setQuota7(ensureBigDecimal(iva.getIVADeducibleGrupo1().getCompRegEspAgricGanadPesca().getCuota()));
			}
			if (iva.getIVADeducibleGrupo1().getRectDeducciones() != null) {
				regime.setBase8(ensureBigDecimal(iva.getIVADeducibleGrupo1().getRectDeducciones().getBI()));
				regime.setQuota8(ensureBigDecimal(iva.getIVADeducibleGrupo1().getRectDeducciones().getCuota()));
			}
			regime.setQuota9(ensureBigDecimal(iva.getIVADeducibleGrupo1().getRegInversiones()));
			regime.setQuota10(ensureBigDecimal(iva.getIVADeducibleGrupo1().getSumaDeducciones()));
		}

		if (iva.getIVADeducibleGrupo2() != null) {
			DeductionRegime regime = new DeductionRegime();
			mod390.setRegime2(regime);
			if (iva.getIVADeducibleGrupo2().getOpInteriores() != null) {
				if (iva.getIVADeducibleGrupo2().getOpInteriores().getBienesyServiciosCorrientes() != null) {
					regime.setBase1(ensureBigDecimal(iva.getIVADeducibleGrupo2().getOpInteriores().getBienesyServiciosCorrientes().getBI()));
					regime.setQuota1(ensureBigDecimal(iva.getIVADeducibleGrupo2().getOpInteriores().getBienesyServiciosCorrientes().getCuota()));
				}
				if (iva.getIVADeducibleGrupo2().getOpInteriores().getBienesInversion() != null) {
					regime.setBase2(ensureBigDecimal(iva.getIVADeducibleGrupo2().getOpInteriores().getBienesInversion().getBI()));
					regime.setQuota2(ensureBigDecimal(iva.getIVADeducibleGrupo2().getOpInteriores().getBienesInversion().getCuota()));
				}
			}
			if (iva.getIVADeducibleGrupo2().getImportaciones() != null) {
				if (iva.getIVADeducibleGrupo2().getImportaciones().getBienesCorrientes() != null) {
					regime.setBase3(ensureBigDecimal(iva.getIVADeducibleGrupo2().getImportaciones().getBienesCorrientes().getBI()));
					regime.setQuota3(ensureBigDecimal(iva.getIVADeducibleGrupo2().getImportaciones().getBienesCorrientes().getCuota()));
				}
				if (iva.getIVADeducibleGrupo2().getImportaciones().getBienesInversion() != null) {
					regime.setBase4(ensureBigDecimal(iva.getIVADeducibleGrupo2().getImportaciones().getBienesInversion().getBI()));
					regime.setQuota4(ensureBigDecimal(iva.getIVADeducibleGrupo2().getImportaciones().getBienesInversion().getCuota()));
				}
			}
			if (iva.getIVADeducibleGrupo2().getAdqIntracomunitarias() != null) {
				if (iva.getIVADeducibleGrupo2().getAdqIntracomunitarias().getBienesCorrientes() != null) {
					regime.setBase5(ensureBigDecimal(iva.getIVADeducibleGrupo2().getAdqIntracomunitarias().getBienesCorrientes().getBI()));
					regime.setQuota5(ensureBigDecimal(iva.getIVADeducibleGrupo2().getAdqIntracomunitarias().getBienesCorrientes().getCuota()));
				}
				if (iva.getIVADeducibleGrupo2().getAdqIntracomunitarias().getBienesInversion() != null) {
					regime.setBase6(ensureBigDecimal(iva.getIVADeducibleGrupo2().getAdqIntracomunitarias().getBienesInversion().getBI()));
					regime.setQuota6(ensureBigDecimal(iva.getIVADeducibleGrupo2().getAdqIntracomunitarias().getBienesInversion().getCuota()));
				}
			}
			if (iva.getIVADeducibleGrupo1().getCompRegEspAgricGanadPesca() != null) {
				regime.setBase7(ensureBigDecimal(iva.getIVADeducibleGrupo2().getCompRegEspAgricGanadPesca().getBI()));
				regime.setQuota7(ensureBigDecimal(iva.getIVADeducibleGrupo2().getCompRegEspAgricGanadPesca().getCuota()));
			}
			if (iva.getIVADeducibleGrupo2().getRectDeducciones() != null) {
				regime.setBase8(ensureBigDecimal(iva.getIVADeducibleGrupo2().getRectDeducciones().getBI()));
				regime.setQuota8(ensureBigDecimal(iva.getIVADeducibleGrupo2().getRectDeducciones().getCuota()));
			}
			regime.setQuota9(ensureBigDecimal(iva.getIVADeducibleGrupo2().getRegInversiones()));
			regime.setQuota10(ensureBigDecimal(iva.getIVADeducibleGrupo2().getSumaDeducciones()));
		}

		if (iva.getIVADeducibleGrupo3() != null) {
			DeductionRegime regime = new DeductionRegime();
			mod390.setRegime3(regime);
			if (iva.getIVADeducibleGrupo3().getOpInteriores() != null) {
				if (iva.getIVADeducibleGrupo3().getOpInteriores().getBienesyServiciosCorrientes() != null) {
					regime.setBase1(ensureBigDecimal(iva.getIVADeducibleGrupo3().getOpInteriores().getBienesyServiciosCorrientes().getBI()));
					regime.setQuota1(ensureBigDecimal(iva.getIVADeducibleGrupo3().getOpInteriores().getBienesyServiciosCorrientes().getCuota()));
				}
				if (iva.getIVADeducibleGrupo3().getOpInteriores().getBienesInversion() != null) {
					regime.setBase2(ensureBigDecimal(iva.getIVADeducibleGrupo3().getOpInteriores().getBienesInversion().getBI()));
					regime.setQuota2(ensureBigDecimal(iva.getIVADeducibleGrupo3().getOpInteriores().getBienesInversion().getCuota()));
				}
			}
			if (iva.getIVADeducibleGrupo3().getImportaciones() != null) {
				if (iva.getIVADeducibleGrupo3().getImportaciones().getBienesCorrientes() != null) {
					regime.setBase3(ensureBigDecimal(iva.getIVADeducibleGrupo3().getImportaciones().getBienesCorrientes().getBI()));
					regime.setQuota3(ensureBigDecimal(iva.getIVADeducibleGrupo3().getImportaciones().getBienesCorrientes().getCuota()));
				}
				if (iva.getIVADeducibleGrupo3().getImportaciones().getBienesInversion() != null) {
					regime.setBase4(ensureBigDecimal(iva.getIVADeducibleGrupo3().getImportaciones().getBienesInversion().getBI()));
					regime.setQuota4(ensureBigDecimal(iva.getIVADeducibleGrupo3().getImportaciones().getBienesInversion().getCuota()));
				}
			}
			if (iva.getIVADeducibleGrupo3().getAdqIntracomunitarias() != null) {
				if (iva.getIVADeducibleGrupo3().getAdqIntracomunitarias().getBienesCorrientes() != null) {
					regime.setBase5(ensureBigDecimal(iva.getIVADeducibleGrupo3().getAdqIntracomunitarias().getBienesCorrientes().getBI()));
					regime.setQuota5(ensureBigDecimal(iva.getIVADeducibleGrupo3().getAdqIntracomunitarias().getBienesCorrientes().getCuota()));
				}
				if (iva.getIVADeducibleGrupo3().getAdqIntracomunitarias().getBienesInversion() != null) {
					regime.setBase6(ensureBigDecimal(iva.getIVADeducibleGrupo3().getAdqIntracomunitarias().getBienesInversion().getBI()));
					regime.setQuota6(ensureBigDecimal(iva.getIVADeducibleGrupo3().getAdqIntracomunitarias().getBienesInversion().getCuota()));
				}
			}
			if (iva.getIVADeducibleGrupo3().getCompRegEspAgricGanadPesca() != null) {
				regime.setBase7(ensureBigDecimal(iva.getIVADeducibleGrupo3().getCompRegEspAgricGanadPesca().getBI()));
				regime.setQuota7(ensureBigDecimal(iva.getIVADeducibleGrupo3().getCompRegEspAgricGanadPesca().getCuota()));
			}
			if (iva.getIVADeducibleGrupo3().getRectDeducciones() != null) {
				regime.setBase8(ensureBigDecimal(iva.getIVADeducibleGrupo3().getRectDeducciones().getBI()));
				regime.setQuota8(ensureBigDecimal(iva.getIVADeducibleGrupo3().getRectDeducciones().getCuota()));
			}
			regime.setQuota9(ensureBigDecimal(iva.getIVADeducibleGrupo3().getRegInversiones()));
			regime.setQuota10(ensureBigDecimal(iva.getIVADeducibleGrupo3().getSumaDeducciones()));
		}

	}

	private static FarmerRegimeActivity getFarmerRegimeActivity(
			ActAgricGanadForest actividad) {
		FarmerRegimeActivity ac = new FarmerRegimeActivity();
		ac.setCodigo(actividad.getCodigo());
		ac.setIncomes(ensureBigDecimal(actividad.getVolIngresos()));
		ac.setQuotaIndex(ensureBigDecimal(actividad.getIndCuota()));
		ac.setAccrualQuota(ensureBigDecimal(actividad.getCuotaDevengada()));
		ac.setInputQuotas(ensureBigDecimal(actividad.getCuotasSoportadas()));
		ac.setQuota(ensureBigDecimal(actividad.getCuotaRegSimplificado()));
		return ac;
	}

	private static SimpliedRegimeActivity getSimpliedRegimeActivity(
			Actividad act) {
		SimpliedRegimeActivity sra = new SimpliedRegimeActivity();
		sra.setEpigrafe(act.getEpigrafe());
		List<Modulo> modulos = act.getModulo();
		if (modulos.size() > 0) {
			sra.setUnit1(ensureBigDecimal(modulos.get(0).getUnidades()));
			sra.setAmount1(ensureBigDecimal(modulos.get(0).getImporte()));
		}
		if (modulos.size() > 1) {
			sra.setUnit2(ensureBigDecimal(modulos.get(1).getUnidades()));
			sra.setAmount2(ensureBigDecimal(modulos.get(1).getImporte()));
		}
		if (modulos.size() > 2) {
			sra.setUnit3(ensureBigDecimal(modulos.get(2).getUnidades()));
			sra.setAmount3(ensureBigDecimal(modulos.get(2).getImporte()));
		}
		if (modulos.size() > 3) {
			sra.setUnit4(ensureBigDecimal(modulos.get(3).getUnidades()));
			sra.setAmount4(ensureBigDecimal(modulos.get(3).getImporte()));
		}
		if (modulos.size() > 4) {
			sra.setUnit5(ensureBigDecimal(modulos.get(4).getUnidades()));
			sra.setAmount5(ensureBigDecimal(modulos.get(4).getImporte()));
		}
		if (modulos.size() > 5) {
			sra.setUnit6(ensureBigDecimal(modulos.get(5).getUnidades()));
			sra.setAmount6(ensureBigDecimal(modulos.get(5).getImporte()));
		}
		if (modulos.size() > 6) {
			sra.setUnit7(ensureBigDecimal(modulos.get(6).getUnidades()));
			sra.setAmount7(ensureBigDecimal(modulos.get(6).getImporte()));
		}
		sra.setBoxC(ensureBigDecimal(act.getCuotaDevengada()));
		sra.setBoxC1(ensureBigDecimal(act.getLorca2013()));
		sra.setBoxD(ensureBigDecimal(act.getCuotaSoportada()));
		sra.setBoxE(ensureBigDecimal(act.getIndiceCorrector()));
		sra.setBoxF(ensureBigDecimal(act.getResultado()));
		sra.setBoxG(ensureBigDecimal(act.getPorcCuotaMinima()));
		sra.setBoxH(ensureBigDecimal(act.getDevCuotaSopOtrosPaises()));
		sra.setBoxI(ensureBigDecimal(act.getCuotaMinima()));
		sra.setBoxJ(ensureBigDecimal(act.getCuotaRegSimplificado()));
		return sra;
	}

	private static void put(Mod3902018 mod390, Mod3902018DetailKey key,
			TipoBaseImponibleYCuota tipo) {
		if (tipo != null) {
			Mod390Detail detail = new Mod390Detail();
			detail.setKey(key);
			detail.setPercent(key.getPercent());
			if (tipo.getBI() != null) {
				detail.setTaxableBase(tipo.getBI().doubleValue());
			}
			if (tipo.getCuota() != null) {
				detail.setQuota(tipo.getCuota().doubleValue());
			}
			mod390.getGeneralRegime().put(key, detail);
		}

	}

	private static void put(Mod3902018 mod390, Mod3902018DetailKey key, BigDecimal quota) {
		if (quota != null) {
			Mod390Detail detail = new Mod390Detail();
			detail.setKey(key);
			detail.setPercent(key.getPercent());
			detail.setQuota(quota.doubleValue());
			mod390.getGeneralRegime().put(key, detail);
		}

	}

	private static double ensureBigDecimal(BigDecimal bigDecimal) {
		return bigDecimal == null ? 0.0 : bigDecimal.doubleValue();
	}
}
