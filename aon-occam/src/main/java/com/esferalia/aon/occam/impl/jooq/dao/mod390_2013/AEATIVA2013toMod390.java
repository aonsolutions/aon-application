package com.esferalia.aon.occam.impl.jooq.dao.mod390_2013;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014.Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014.Address;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014.FarmerRegimeActivity;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014.Mod390Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014.Mod390DetailKey;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014.SimpliedRegimeActivity;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.AEATIVA2013.Administraciones;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.AEATIVA2013.DatEstadisticos;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.AEATIVA2013.DatEstadisticos.Otras;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.AEATIVA2013.DatIdent;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.AEATIVA2013.Devengo;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.AEATIVA2013.LiqAnual;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.AEATIVA2013.OpEspecificas;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.AEATIVA2013.RegGeneral.BaseImponibleyCuota;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.AEATIVA2013.RegGeneral.Deducciones;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.AEATIVA2013.RegSimplificado;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.AEATIVA2013.RegSimplificado.ActAgricGanadForest;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.AEATIVA2013.RegSimplificado.Actividad;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.AEATIVA2013.RegSimplificado.Actividad.Modulo;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.AEATIVA2013.RegSimplificado.IvaDeducible;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.AEATIVA2013.RegSimplificado.IvaDevengado;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.AEATIVA2013.ResLiquidaciones;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.AEATIVA2013.ResLiquidaciones.PerNoRegGrupos;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.AEATIVA2013.ResLiquidaciones.PerSiRegGrupos;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2013.AEATIVA2013.VolOperaciones;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AEATIVA2013toMod390 {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	public static void populate(Mod3902014 mod390, AEATIVA2013 iva)
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
		mod390.setInsolvencyDeclarations((devengo.getConcursoUltPerSI() != null));
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

				// lg.setNotaryDate(trj.getFechaPoder());
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

		Map<Mod390DetailKey, Mod390Detail> map = new TreeMap<Mod390DetailKey, Mod390Detail>();
//		ArrayList<Mod390Detail> mapList = initializeList(true);
//		for (Mod390Detail detail : mapList) {
//			map.put(detail.getKey(), detail);
//		}
		mod390.setGeneralRegime(map);

		if (iva.getRegGeneral() != null) {
			BaseImponibleyCuota b = iva.getRegGeneral()
					.getBaseImponibleyCuota();
			if (b != null) {
				if (b.getRegOrdinario() != null) {
					put(mod390, Mod390DetailKey.K00_04, b.getRegOrdinario()
							.getTipo4());
					put(mod390, Mod390DetailKey.K00_08, b.getRegOrdinario()
							.getTipo8());
					put(mod390, Mod390DetailKey.K00_10, b.getRegOrdinario()
							.getTipo10());
					put(mod390, Mod390DetailKey.K00_18, b.getRegOrdinario()
							.getTipo18());
					put(mod390, Mod390DetailKey.K00_21, b.getRegOrdinario()
							.getTipo21());
				}
				if (b.getOpIntragrupo() != null) {
					put(mod390, Mod390DetailKey.K01_04, b.getOpIntragrupo()
							.getTipo4());
					put(mod390, Mod390DetailKey.K01_08, b.getOpIntragrupo()
							.getTipo8());
					put(mod390, Mod390DetailKey.K01_10, b.getOpIntragrupo()
							.getTipo10());
					put(mod390, Mod390DetailKey.K01_18, b.getOpIntragrupo()
							.getTipo18());
					put(mod390, Mod390DetailKey.K01_21, b.getOpIntragrupo()
							.getTipo21());
				}
				if (b.getRegBienesUsados() != null) {
					put(mod390, Mod390DetailKey.K02_04, b.getRegBienesUsados()
							.getTipo4());
					put(mod390, Mod390DetailKey.K02_08, b.getRegBienesUsados()
							.getTipo8());
					put(mod390, Mod390DetailKey.K02_10, b.getRegBienesUsados()
							.getTipo10());
					put(mod390, Mod390DetailKey.K02_18, b.getRegBienesUsados()
							.getTipo18());
					put(mod390, Mod390DetailKey.K02_21, b.getRegBienesUsados()
							.getTipo21());
				}
				if (b.getRegAgViajes() != null) {
					put(mod390, Mod390DetailKey.K03_18, b.getRegAgViajes()
							.getTipo18());
					put(mod390, Mod390DetailKey.K03_21, b.getRegAgViajes()
							.getTipo21());
				}
				if (b.getAdqIntracomBienes() != null) {
					put(mod390, Mod390DetailKey.K04_04, b
							.getAdqIntracomBienes().getTipo4());
					put(mod390, Mod390DetailKey.K04_08, b
							.getAdqIntracomBienes().getTipo8());
					put(mod390, Mod390DetailKey.K04_10, b
							.getAdqIntracomBienes().getTipo10());
					put(mod390, Mod390DetailKey.K04_18, b
							.getAdqIntracomBienes().getTipo18());
					put(mod390, Mod390DetailKey.K04_21, b
							.getAdqIntracomBienes().getTipo21());
				}
				if (b.getAdqIntracomServicios() != null) {
					put(mod390, Mod390DetailKey.K05_04, b
							.getAdqIntracomServicios().getTipo4());
					put(mod390, Mod390DetailKey.K05_08, b
							.getAdqIntracomServicios().getTipo8());
					put(mod390, Mod390DetailKey.K05_10, b
							.getAdqIntracomServicios().getTipo10());
					put(mod390, Mod390DetailKey.K05_18, b
							.getAdqIntracomServicios().getTipo18());
					put(mod390, Mod390DetailKey.K05_21, b
							.getAdqIntracomServicios().getTipo21());
				}
				if (b.getIVAdevengadoInversionSP() != null) {
					put(mod390, Mod390DetailKey.K06, b
							.getIVAdevengadoInversionSP().getTipoX());
				}
				if (b.getModBasesyCuotas() != null) {
					put(mod390, Mod390DetailKey.K07, b.getModBasesyCuotas()
							.getTipoX());
				}
				if (b.getModBasesyCuotasConcursoAcreedores() != null) {
					put(mod390, Mod390DetailKey.K08, b
							.getModBasesyCuotasConcursoAcreedores().getTipoX());
				}
				if (b.getTotalBasesyCuotasIVA() != null) {
					put(mod390, Mod390DetailKey.K09, b
							.getTotalBasesyCuotasIVA().getTipoX());
				}
				if (b.getRecargoEquivalencia() != null) {
					put(mod390, Mod390DetailKey.K10_05, b
							.getRecargoEquivalencia().getTipo05());
					put(mod390, Mod390DetailKey.K10_1, b
							.getRecargoEquivalencia().getTipo1());
					put(mod390, Mod390DetailKey.K10_14, b
							.getRecargoEquivalencia().getTipo14());
					put(mod390, Mod390DetailKey.K10_175, b
							.getRecargoEquivalencia().getTipo175());
					put(mod390, Mod390DetailKey.K10_4, b
							.getRecargoEquivalencia().getTipo4());
					put(mod390, Mod390DetailKey.K10_52, b
							.getRecargoEquivalencia().getTipo52());
				}
				if (b.getModRecargoEquivalencia() != null) {
					put(mod390, Mod390DetailKey.K11, b
							.getModRecargoEquivalencia().getTipoX());
				}
				if (b.getModRecargoEquivalenciaConcursoAcreedores() != null) {
					put(mod390, Mod390DetailKey.K12, b
							.getModRecargoEquivalenciaConcursoAcreedores()
							.getTipoX());
				}
				if (b.getTotalCuotasIVA() != null) {
					put(mod390, Mod390DetailKey.K13, b.getTotalCuotasIVA());
				}
			}
			if (iva.getRegGeneral().getDeducciones() != null) {
				Deducciones d = iva.getRegGeneral().getDeducciones();
				if (d.getOpInterioresBienesServiciosCorrientes() != null) {
					put(mod390, Mod390DetailKey.K14_04, d
							.getOpInterioresBienesServiciosCorrientes()
							.getTipo4());
					put(mod390, Mod390DetailKey.K14_07, d
							.getOpInterioresBienesServiciosCorrientes()
							.getTipo7());
					put(mod390, Mod390DetailKey.K14_08, d
							.getOpInterioresBienesServiciosCorrientes()
							.getTipo8());
					put(mod390, Mod390DetailKey.K14_10, d
							.getOpInterioresBienesServiciosCorrientes()
							.getTipo10());
					put(mod390, Mod390DetailKey.K14_16, d
							.getOpInterioresBienesServiciosCorrientes()
							.getTipo16());
					put(mod390, Mod390DetailKey.K14_18, d
							.getOpInterioresBienesServiciosCorrientes()
							.getTipo18());
					put(mod390, Mod390DetailKey.K14_21, d
							.getOpInterioresBienesServiciosCorrientes()
							.getTipo21());
				}
				if (d.getOpIntragrupoCorrientes() != null) {
					put(mod390, Mod390DetailKey.K16_04, d
							.getOpIntragrupoCorrientes().getTipo4());
					put(mod390, Mod390DetailKey.K16_07, d
							.getOpIntragrupoCorrientes().getTipo7());
					put(mod390, Mod390DetailKey.K16_08, d
							.getOpIntragrupoCorrientes().getTipo8());
					put(mod390, Mod390DetailKey.K16_10, d
							.getOpIntragrupoCorrientes().getTipo10());
					put(mod390, Mod390DetailKey.K16_16, d
							.getOpIntragrupoCorrientes().getTipo16());
					put(mod390, Mod390DetailKey.K16_18, d
							.getOpIntragrupoCorrientes().getTipo18());
					put(mod390, Mod390DetailKey.K16_21, d
							.getOpIntragrupoCorrientes().getTipo21());
				}
				if (d.getOpInterioresBienesInversion() != null) {
					put(mod390, Mod390DetailKey.K18_04, d
							.getOpInterioresBienesInversion().getTipo4());
					put(mod390, Mod390DetailKey.K18_07, d
							.getOpInterioresBienesInversion().getTipo7());
					put(mod390, Mod390DetailKey.K18_08, d
							.getOpInterioresBienesInversion().getTipo8());
					put(mod390, Mod390DetailKey.K18_10, d
							.getOpInterioresBienesInversion().getTipo10());
					put(mod390, Mod390DetailKey.K18_16, d
							.getOpInterioresBienesInversion().getTipo16());
					put(mod390, Mod390DetailKey.K18_18, d
							.getOpInterioresBienesInversion().getTipo18());
					put(mod390, Mod390DetailKey.K18_21, d
							.getOpInterioresBienesInversion().getTipo21());
				}
				if (d.getOpIntragrupoBienesInversion() != null) {
					put(mod390, Mod390DetailKey.K20_04, d
							.getOpIntragrupoBienesInversion().getTipo4());
					put(mod390, Mod390DetailKey.K20_07, d
							.getOpIntragrupoBienesInversion().getTipo7());
					put(mod390, Mod390DetailKey.K20_08, d
							.getOpIntragrupoBienesInversion().getTipo8());
					put(mod390, Mod390DetailKey.K20_10, d
							.getOpIntragrupoBienesInversion().getTipo10());
					put(mod390, Mod390DetailKey.K20_16, d
							.getOpIntragrupoBienesInversion().getTipo16());
					put(mod390, Mod390DetailKey.K20_18, d
							.getOpIntragrupoBienesInversion().getTipo18());
					put(mod390, Mod390DetailKey.K20_21, d
							.getOpIntragrupoBienesInversion().getTipo21());
				}
				if (d.getImportacionesBienesCorrientes() != null) {
					put(mod390, Mod390DetailKey.K22_04, d
							.getImportacionesBienesCorrientes().getTipo4());
					put(mod390, Mod390DetailKey.K22_07, d
							.getImportacionesBienesCorrientes().getTipo7());
					put(mod390, Mod390DetailKey.K22_08, d
							.getImportacionesBienesCorrientes().getTipo8());
					put(mod390, Mod390DetailKey.K22_10, d
							.getImportacionesBienesCorrientes().getTipo10());
					put(mod390, Mod390DetailKey.K22_16, d
							.getImportacionesBienesCorrientes().getTipo16());
					put(mod390, Mod390DetailKey.K22_18, d
							.getImportacionesBienesCorrientes().getTipo18());
					put(mod390, Mod390DetailKey.K22_21, d
							.getImportacionesBienesCorrientes().getTipo21());
				}
				if (d.getImportacionesBienesInversion() != null) {
					put(mod390, Mod390DetailKey.K24_04, d
							.getImportacionesBienesInversion().getTipo4());
					put(mod390, Mod390DetailKey.K24_07, d
							.getImportacionesBienesInversion().getTipo7());
					put(mod390, Mod390DetailKey.K24_08, d
							.getImportacionesBienesInversion().getTipo8());
					put(mod390, Mod390DetailKey.K24_10, d
							.getImportacionesBienesInversion().getTipo10());
					put(mod390, Mod390DetailKey.K24_16, d
							.getImportacionesBienesInversion().getTipo16());
					put(mod390, Mod390DetailKey.K24_18, d
							.getImportacionesBienesInversion().getTipo18());
					put(mod390, Mod390DetailKey.K24_21, d
							.getImportacionesBienesInversion().getTipo21());
				}
				if (d.getAdqIntracomunitariasBienesCorrientes() != null) {
					put(mod390, Mod390DetailKey.K26_04, d
							.getAdqIntracomunitariasBienesCorrientes()
							.getTipo4());
					put(mod390, Mod390DetailKey.K26_07, d
							.getAdqIntracomunitariasBienesCorrientes()
							.getTipo7());
					put(mod390, Mod390DetailKey.K26_08, d
							.getAdqIntracomunitariasBienesCorrientes()
							.getTipo8());
					put(mod390, Mod390DetailKey.K26_10, d
							.getAdqIntracomunitariasBienesCorrientes()
							.getTipo10());
					put(mod390, Mod390DetailKey.K26_16, d
							.getAdqIntracomunitariasBienesCorrientes()
							.getTipo16());
					put(mod390, Mod390DetailKey.K26_18, d
							.getAdqIntracomunitariasBienesCorrientes()
							.getTipo18());
					put(mod390, Mod390DetailKey.K26_21, d
							.getAdqIntracomunitariasBienesCorrientes()
							.getTipo21());
				}
				if (d.getAdqIntracomunitariasBienesInversion() != null) {
					put(mod390, Mod390DetailKey.K28_04, d
							.getAdqIntracomunitariasBienesInversion()
							.getTipo4());
					put(mod390, Mod390DetailKey.K28_07, d
							.getAdqIntracomunitariasBienesInversion()
							.getTipo7());
					put(mod390, Mod390DetailKey.K28_08, d
							.getAdqIntracomunitariasBienesInversion()
							.getTipo8());
					put(mod390, Mod390DetailKey.K28_10, d
							.getAdqIntracomunitariasBienesInversion()
							.getTipo10());
					put(mod390, Mod390DetailKey.K28_16, d
							.getAdqIntracomunitariasBienesInversion()
							.getTipo16());
					put(mod390, Mod390DetailKey.K28_18, d
							.getAdqIntracomunitariasBienesInversion()
							.getTipo18());
					put(mod390, Mod390DetailKey.K28_21, d
							.getAdqIntracomunitariasBienesInversion()
							.getTipo21());
				}
				if (d.getAdqIntracomunitariasServicios() != null) {
					put(mod390, Mod390DetailKey.K30_04, d
							.getAdqIntracomunitariasServicios().getTipo4());
					put(mod390, Mod390DetailKey.K30_07, d
							.getAdqIntracomunitariasServicios().getTipo7());
					put(mod390, Mod390DetailKey.K30_08, d
							.getAdqIntracomunitariasServicios().getTipo8());
					put(mod390, Mod390DetailKey.K30_10, d
							.getAdqIntracomunitariasServicios().getTipo10());
					put(mod390, Mod390DetailKey.K30_16, d
							.getAdqIntracomunitariasServicios().getTipo16());
					put(mod390, Mod390DetailKey.K30_18, d
							.getAdqIntracomunitariasServicios().getTipo18());
					put(mod390, Mod390DetailKey.K30_21, d
							.getAdqIntracomunitariasServicios().getTipo21());
				}
				if (d.getComRegAgricGanadPesca() != null) {
					put(mod390, Mod390DetailKey.K32, d
							.getComRegAgricGanadPesca().getTipoX());
				}
				if (d.getRectifDeducciones() != null) {
					put(mod390, Mod390DetailKey.K33, d.getRectifDeducciones()
							.getTipoX());
				}
				if (d.getRegularizInversiones() != null) {
					put(mod390, Mod390DetailKey.K34,
							d.getRegularizInversiones());
				}
				if (d.getRegularizPorcProrrata() != null) {
					put(mod390, Mod390DetailKey.K35,
							d.getRegularizPorcProrrata());
				}
				if (d.getSumDeducciones() != null) {
					put(mod390, Mod390DetailKey.K36, d.getSumDeducciones());
				}

			}
			String res = iva.getRegGeneral().getResRegGeneral();
			if (AonStringUtils.isNotEmpty(res)) {
				try {
					double val = Double.parseDouble(res);
					put(mod390, Mod390DetailKey.K37, new BigDecimal(val));
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
		}

		Administraciones adm = iva.getAdministraciones();
		if (adm == null) {
			LiqAnual liq = iva.getLiqAnual();
			if (liq != null) {
				mod390.setBox84(ensureBigDecimal(liq.getSumResultados()));
				mod390.setBox85(ensureBigDecimal(liq
						.getCompCuotasEjercicioAnterior()));
				mod390.setBox86(ensureBigDecimal(liq.getResLiquidacion()));
			}
		} else {
			if (adm != null) {
				mod390.setBox87(ensureBigDecimal(adm.getComun()));
				mod390.setBox88(ensureBigDecimal(adm.getArabaAlava()));
				mod390.setBox89(ensureBigDecimal(adm.getGipuzkoa()));
				mod390.setBox90(ensureBigDecimal(adm.getBizkaia()));
				mod390.setBox91(ensureBigDecimal(adm.getNavarra()));
				mod390.setBox84(ensureBigDecimal(adm.getSumResultados()));
				mod390.setBox92(ensureBigDecimal(adm.getResTerrComun()));
				mod390.setBox93(ensureBigDecimal(adm
						.getComCuotasEjercicioAnteriorTerrComun()));
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
		}

		// TODO
		// iva.prorratas
		// TODO
		// iva.ivaDeducibleGrupo1
		// TODO
		// iva.ivaDeducibleGrupo2
		// TODO
		// iva.ivaDeducibleGrupo3

	}
/*
	private static ArrayList<Mod390Detail> initializeList(boolean onlyPage5) {
		ArrayList<Mod390Detail> list = new ArrayList<Mod390Detail>();
		Mod390Detail detail = null;
		for (Mod390DetailKey key : Mod390DetailKey.values()) {
			if (!onlyPage5 || (onlyPage5 && key.isPage5Key())) {
				detail = new Mod390Detail();
				detail.setKey(key);
				detail.setPercent(key.getPercent());
				list.add(detail);
			}
		}
		return list;
	}
*/
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
		sra.setBoxD(ensureBigDecimal(act.getCuotaSoportada()));
		sra.setBoxE(ensureBigDecimal(act.getIndiceCorrector()));
		sra.setBoxF(ensureBigDecimal(act.getResultado()));
		sra.setBoxG(ensureBigDecimal(act.getPorcCuotaMinima()));
		sra.setBoxH(ensureBigDecimal(act.getDevCuotaSopOtrosPaises()));
		sra.setBoxI(ensureBigDecimal(act.getCuotaMinima()));
		sra.setBoxJ(ensureBigDecimal(act.getCuotaRegSimplificado()));
		return sra;
	}

	private static void put(Mod3902014 mod390, Mod390DetailKey key,
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

	private static void put(Mod3902014 mod390, Mod390DetailKey key, BigDecimal quota) {
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
