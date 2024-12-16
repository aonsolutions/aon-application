package com.esferalia.aon.occam.impl.jooq.dao.mod390_2024;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.esferalia.aon.occam.api.model.fiscal.ActivityType;
import com.esferalia.aon.occam.api.model.fiscal.Address;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Activity;
import com.esferalia.aon.occam.api.model.fiscal.mod390.DeductionRegime;
import com.esferalia.aon.occam.api.model.fiscal.mod390.FarmerRegimeActivity;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902024;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902024.Mod390Detail;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902024DetailKey;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Prorrata;
import com.esferalia.aon.occam.api.model.fiscal.mod390.SimpliedRegimeActivity;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.Administraciones;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.DatEstadisticos;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.DatEstadisticos.Otras;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.DatIdent;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.Devengo;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.LiqAnual;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.OpEspecificas;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.Prorratas;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.Prorratas.Pro;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.BaseImponibleyCuota;
import com.esferalia.aon.occam.impl.jooq.dao.mod390_2024.AEATIVA2024.RegGeneral.Deducciones;
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
import com.esferalia.aon.watson.util.AonStringUtils;

public class AEATIVA2024toMod390 {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	private AEATIVA2024toMod390() {

	}

	public static void populate(Mod3902024 mod390, AEATIVA2024 iva) throws ParseException {
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
			mod390.setGroupDependent(tge.getDominante() == null);
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
			activity.setType( ActivityType.ensure( dat.getPral().getClave() ));
			activity.setDescription(dat.getPral().getDescripcion());
			activity.setEpigraph(dat.getPral().getEpigrafe());
			mod390.setMainActivity(activity);
		}
		int i = 1;
		for (Otras otras : dat.getOtras()) {
			if (otras != null) {
				Activity activity = new Activity();
				activity.setType(ActivityType.ensure( otras.getClave()));
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
		if (list != null && !list.isEmpty()) {
			for (TipoRepresentanteJuridica trj : list) {
				LegalRepresentative lg = new LegalRepresentative();
				lg.setDocument(trj.getNIF());
				lg.setName(trj.getNombre());
				lg.setNotary(trj.getNotaria());

				String fec = trj.getFechaPoder();
				lg.setNotaryDate(AonStringUtils.isBlank(fec) ? null : DATE_FORMAT.parse(fec));

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

		Map<Mod3902024DetailKey, Mod390Detail> map = new TreeMap<>();
		mod390.setGeneralRegime(map);

		if (iva.getRegGeneral() != null) {
			BaseImponibleyCuota b = iva.getRegGeneral().getBaseImponibleyCuota();
			if (b != null) {
				if (b.getRegOrdinario() != null) {
					put(mod390, Mod3902024DetailKey.C0701, b.getRegOrdinario().getTipo0());
					put(mod390, Mod3902024DetailKey.C0668, b.getRegOrdinario().getTipo2());
					put(mod390, Mod3902024DetailKey.C0002, b.getRegOrdinario().getTipo4());
					put(mod390, Mod3902024DetailKey.C0703, b.getRegOrdinario().getTipo5());
					put(mod390, Mod3902024DetailKey.C0670, b.getRegOrdinario().getTipo75());
					put(mod390, Mod3902024DetailKey.C0004, b.getRegOrdinario().getTipo10());
					put(mod390, Mod3902024DetailKey.C0006, b.getRegOrdinario().getTipo21());
				}
				if (b.getOpIntragrupo() != null) {
					put(mod390, Mod3902024DetailKey.C0705, b.getOpIntragrupo().getTipo0());
					put(mod390, Mod3902024DetailKey.C0672, b.getOpIntragrupo().getTipo2());
					put(mod390, Mod3902024DetailKey.C0501, b.getOpIntragrupo().getTipo4());
					put(mod390, Mod3902024DetailKey.C0707, b.getOpIntragrupo().getTipo5());
					put(mod390, Mod3902024DetailKey.C0674, b.getOpIntragrupo().getTipo75());
					put(mod390, Mod3902024DetailKey.C0503, b.getOpIntragrupo().getTipo10());
					put(mod390, Mod3902024DetailKey.C0505, b.getOpIntragrupo().getTipo21());
				}
				if (b.getRegCriterioCaja() != null) {
					put(mod390, Mod3902024DetailKey.C0709, b.getRegCriterioCaja().getTipo0());
					put(mod390, Mod3902024DetailKey.C0676, b.getRegCriterioCaja().getTipo2());
					put(mod390, Mod3902024DetailKey.C0644, b.getRegCriterioCaja().getTipo4());
					put(mod390, Mod3902024DetailKey.C0711, b.getRegCriterioCaja().getTipo5());
					put(mod390, Mod3902024DetailKey.C0678, b.getRegCriterioCaja().getTipo75());
					put(mod390, Mod3902024DetailKey.C0646, b.getRegCriterioCaja().getTipo10());
					put(mod390, Mod3902024DetailKey.C0648, b.getRegCriterioCaja().getTipo21());
				}
				if (b.getRegBienesUsados() != null) {
					put(mod390, Mod3902024DetailKey.C0713, b.getRegBienesUsados().getTipo0());
					put(mod390, Mod3902024DetailKey.C0680, b.getRegBienesUsados().getTipo2());
					put(mod390, Mod3902024DetailKey.C0008, b.getRegBienesUsados().getTipo4());
					put(mod390, Mod3902024DetailKey.C0715, b.getRegBienesUsados().getTipo5());
					put(mod390, Mod3902024DetailKey.C0682, b.getRegBienesUsados().getTipo75());
					put(mod390, Mod3902024DetailKey.C0010, b.getRegBienesUsados().getTipo10());
					put(mod390, Mod3902024DetailKey.C0012, b.getRegBienesUsados().getTipo21());
				}
				if (b.getRegAgViajes() != null) {
					put(mod390, Mod3902024DetailKey.C0014, b.getRegAgViajes().getTipo21());
				}
				if (b.getAdqIntracomBienes() != null) {
					put(mod390, Mod3902024DetailKey.C0717, b.getAdqIntracomBienes().getTipo0());
					put(mod390, Mod3902024DetailKey.C0684, b.getAdqIntracomBienes().getTipo2());
					put(mod390, Mod3902024DetailKey.C0022, b.getAdqIntracomBienes().getTipo4());
					put(mod390, Mod3902024DetailKey.C0719, b.getAdqIntracomBienes().getTipo5());
					put(mod390, Mod3902024DetailKey.C0686, b.getAdqIntracomBienes().getTipo75());
					put(mod390, Mod3902024DetailKey.C0024, b.getAdqIntracomBienes().getTipo10());
					put(mod390, Mod3902024DetailKey.C0026, b.getAdqIntracomBienes().getTipo21());
				}
				if (b.getAdqIntracomServicios() != null) {
					put(mod390, Mod3902024DetailKey.C0721, b.getAdqIntracomServicios().getTipo0());
					put(mod390, Mod3902024DetailKey.C0688, b.getAdqIntracomServicios().getTipo2());
					put(mod390, Mod3902024DetailKey.C0546, b.getAdqIntracomServicios().getTipo4());
					put(mod390, Mod3902024DetailKey.C0723, b.getAdqIntracomServicios().getTipo5());
					put(mod390, Mod3902024DetailKey.C0690, b.getAdqIntracomServicios().getTipo75());
					put(mod390, Mod3902024DetailKey.C0548, b.getAdqIntracomServicios().getTipo10());
					put(mod390, Mod3902024DetailKey.C0552, b.getAdqIntracomServicios().getTipo21());
				}
				if (b.getIVAdevengadoInversionSP() != null) {
					put(mod390, Mod3902024DetailKey.C0028, b.getIVAdevengadoInversionSP().getTipoX());
				}
				if (b.getModBasesyCuotas() != null) {
					put(mod390, Mod3902024DetailKey.C0030, b.getModBasesyCuotas().getTipoX());
				}
				if (b.getModBasesyCuotasConcursoAcreedores() != null) {
					put(mod390, Mod3902024DetailKey.C0032, b.getModBasesyCuotasConcursoAcreedores().getTipoX());
				}
				if (b.getTotalBasesyCuotasIVA() != null) {
					put(mod390, Mod3902024DetailKey.C0034, b.getTotalBasesyCuotasIVA().getTipoX());
				}
				if (b.getRecargoEquivalencia() != null) {
					put(mod390, Mod3902024DetailKey.C0664, b.getRecargoEquivalencia().getTipo0());
					put(mod390, Mod3902024DetailKey.C0692, b.getRecargoEquivalencia().getTipo026());
					put(mod390, Mod3902024DetailKey.C0036, b.getRecargoEquivalencia().getTipo05());
					put(mod390, Mod3902024DetailKey.C0666, b.getRecargoEquivalencia().getTipo062());
					put(mod390, Mod3902024DetailKey.C0694, b.getRecargoEquivalencia().getTipo1());
					put(mod390, Mod3902024DetailKey.C0600, b.getRecargoEquivalencia().getTipo14());
					put(mod390, Mod3902024DetailKey.C0602, b.getRecargoEquivalencia().getTipo52());
					put(mod390, Mod3902024DetailKey.C0042, b.getRecargoEquivalencia().getTipo175());					
				}
				if (b.getModRecargoEquivalencia() != null) {
					put(mod390, Mod3902024DetailKey.C0044, b.getModRecargoEquivalencia().getTipoX());
				}
				if (b.getModRecargoEquivalenciaConcursoAcreedores() != null) {
					put(mod390, Mod3902024DetailKey.C0046, b.getModRecargoEquivalenciaConcursoAcreedores().getTipoX());
				}
				if (b.getTotalCuotasIVA() != null) {
					put(mod390, Mod3902024DetailKey.C0047, b.getTotalCuotasIVA());
				}
			}
			
			if (iva.getRegGeneral().getDeducciones() != null) {
				Deducciones d = iva.getRegGeneral().getDeducciones();
				if (d.getOpInterioresBienesServiciosCorrientes() != null) {
					put(mod390, Mod3902024DetailKey.C0696, d.getOpInterioresBienesServiciosCorrientes().getTipo2());
					put(mod390, Mod3902024DetailKey.C0191, d.getOpInterioresBienesServiciosCorrientes().getTipo4());
					put(mod390, Mod3902024DetailKey.C0725, d.getOpInterioresBienesServiciosCorrientes().getTipo5());
					put(mod390, Mod3902024DetailKey.C0698, d.getOpInterioresBienesServiciosCorrientes().getTipo75());
					put(mod390, Mod3902024DetailKey.C0604, d.getOpInterioresBienesServiciosCorrientes().getTipo10());
					put(mod390, Mod3902024DetailKey.C0606, d.getOpInterioresBienesServiciosCorrientes().getTipo21());
					put(mod390, Mod3902024DetailKey.C0049, d.getOpInterioresBienesServiciosCorrientes().getTotal());
				}
				if (d.getOpIntragrupoCorrientes() != null) {
					put(mod390, Mod3902024DetailKey.C0746, d.getOpIntragrupoCorrientes().getTipo2());
					put(mod390, Mod3902024DetailKey.C0507, d.getOpIntragrupoCorrientes().getTipo4());
					put(mod390, Mod3902024DetailKey.C0727, d.getOpIntragrupoCorrientes().getTipo5());
					put(mod390, Mod3902024DetailKey.C0748, d.getOpIntragrupoCorrientes().getTipo75());
					put(mod390, Mod3902024DetailKey.C0608, d.getOpIntragrupoCorrientes().getTipo10());
					put(mod390, Mod3902024DetailKey.C0610, d.getOpIntragrupoCorrientes().getTipo21());
					put(mod390, Mod3902024DetailKey.C0513, d.getOpIntragrupoCorrientes().getTotal());
				}
				if (d.getOpInterioresBienesInversion() != null) {
					put(mod390, Mod3902024DetailKey.C0750, d.getOpInterioresBienesInversion().getTipo2());
					put(mod390, Mod3902024DetailKey.C0197, d.getOpInterioresBienesInversion().getTipo4());
					put(mod390, Mod3902024DetailKey.C0729, d.getOpInterioresBienesInversion().getTipo5());
					put(mod390, Mod3902024DetailKey.C0752, d.getOpInterioresBienesInversion().getTipo75());
					put(mod390, Mod3902024DetailKey.C0612, d.getOpInterioresBienesInversion().getTipo10());
					put(mod390, Mod3902024DetailKey.C0614, d.getOpInterioresBienesInversion().getTipo21());
					put(mod390, Mod3902024DetailKey.C0051, d.getOpInterioresBienesInversion().getTotal());
				}
				if (d.getOpIntragrupoBienesInversion() != null) {
					put(mod390, Mod3902024DetailKey.C0754, d.getOpIntragrupoBienesInversion().getTipo2());
					put(mod390, Mod3902024DetailKey.C0515, d.getOpIntragrupoBienesInversion().getTipo4());
					put(mod390, Mod3902024DetailKey.C0731, d.getOpIntragrupoBienesInversion().getTipo5());
					put(mod390, Mod3902024DetailKey.C0756, d.getOpIntragrupoBienesInversion().getTipo75());
					put(mod390, Mod3902024DetailKey.C0616, d.getOpIntragrupoBienesInversion().getTipo10());
					put(mod390, Mod3902024DetailKey.C0618, d.getOpIntragrupoBienesInversion().getTipo21());
					put(mod390, Mod3902024DetailKey.C0521, d.getOpIntragrupoBienesInversion().getTotal());
				}
				if (d.getImportacionesBienesCorrientes() != null) {
					put(mod390, Mod3902024DetailKey.C0758, d.getImportacionesBienesCorrientes().getTipo2());
					put(mod390, Mod3902024DetailKey.C0203, d.getImportacionesBienesCorrientes().getTipo4());
					put(mod390, Mod3902024DetailKey.C0733, d.getImportacionesBienesCorrientes().getTipo5());
					put(mod390, Mod3902024DetailKey.C0760, d.getImportacionesBienesCorrientes().getTipo75());
					put(mod390, Mod3902024DetailKey.C0620, d.getImportacionesBienesCorrientes().getTipo10());
					put(mod390, Mod3902024DetailKey.C0622, d.getImportacionesBienesCorrientes().getTipo21());
					put(mod390, Mod3902024DetailKey.C0053, d.getImportacionesBienesCorrientes().getTotal());
				}
				if (d.getImportacionesBienesInversion() != null) {
					put(mod390, Mod3902024DetailKey.C0762, d.getImportacionesBienesInversion().getTipo2());
					put(mod390, Mod3902024DetailKey.C0209, d.getImportacionesBienesInversion().getTipo4());
					put(mod390, Mod3902024DetailKey.C0735, d.getImportacionesBienesInversion().getTipo5());
					put(mod390, Mod3902024DetailKey.C0764, d.getImportacionesBienesInversion().getTipo75());
					put(mod390, Mod3902024DetailKey.C0624, d.getImportacionesBienesInversion().getTipo10());
					put(mod390, Mod3902024DetailKey.C0626, d.getImportacionesBienesInversion().getTipo21());
					put(mod390, Mod3902024DetailKey.C0055, d.getImportacionesBienesInversion().getTotal());
				}
				if (d.getAdqIntracomunitariasBienesCorrientes() != null) {
					put(mod390, Mod3902024DetailKey.C0766, d.getAdqIntracomunitariasBienesCorrientes().getTipo2());
					put(mod390, Mod3902024DetailKey.C0215, d.getAdqIntracomunitariasBienesCorrientes().getTipo4());
					put(mod390, Mod3902024DetailKey.C0737, d.getAdqIntracomunitariasBienesCorrientes().getTipo5());
					put(mod390, Mod3902024DetailKey.C0768, d.getAdqIntracomunitariasBienesCorrientes().getTipo75());
					put(mod390, Mod3902024DetailKey.C0628, d.getAdqIntracomunitariasBienesCorrientes().getTipo10());
					put(mod390, Mod3902024DetailKey.C0630, d.getAdqIntracomunitariasBienesCorrientes().getTipo21());
					put(mod390, Mod3902024DetailKey.C0057, d.getAdqIntracomunitariasBienesCorrientes().getTotal());
				}
				if (d.getAdqIntracomunitariasBienesInversion() != null) {
					put(mod390, Mod3902024DetailKey.C0770, d.getAdqIntracomunitariasBienesInversion().getTipo2());
					put(mod390, Mod3902024DetailKey.C0221, d.getAdqIntracomunitariasBienesInversion().getTipo4());
					put(mod390, Mod3902024DetailKey.C0739, d.getAdqIntracomunitariasBienesInversion().getTipo5());
					put(mod390, Mod3902024DetailKey.C0772, d.getAdqIntracomunitariasBienesInversion().getTipo75());
					put(mod390, Mod3902024DetailKey.C0632, d.getAdqIntracomunitariasBienesInversion().getTipo10());
					put(mod390, Mod3902024DetailKey.C0634, d.getAdqIntracomunitariasBienesInversion().getTipo21());
					put(mod390, Mod3902024DetailKey.C0059, d.getAdqIntracomunitariasBienesInversion().getTotal());
				}
				if (d.getAdqIntracomunitariasServicios() != null) {
					put(mod390, Mod3902024DetailKey.C0774, d.getAdqIntracomunitariasServicios().getTipo2());
					put(mod390, Mod3902024DetailKey.C0588, d.getAdqIntracomunitariasServicios().getTipo4());
					put(mod390, Mod3902024DetailKey.C0741, d.getAdqIntracomunitariasServicios().getTipo5());
					put(mod390, Mod3902024DetailKey.C0776, d.getAdqIntracomunitariasServicios().getTipo75());
					put(mod390, Mod3902024DetailKey.C0636, d.getAdqIntracomunitariasServicios().getTipo10());
					put(mod390, Mod3902024DetailKey.C0638, d.getAdqIntracomunitariasServicios().getTipo21());
					put(mod390, Mod3902024DetailKey.C0598, d.getAdqIntracomunitariasServicios().getTotal());
				}
				if (d.getComRegAgricGanadPesca() != null) {
					put(mod390, Mod3902024DetailKey.C0061, d.getComRegAgricGanadPesca().getTipoX());
				}
				if (d.getRectifDeducciones() != null) {
					put(mod390, Mod3902024DetailKey.C0062, d.getRectifDeducciones().getTipoX());
				}
				if (d.getRectifOpIntragrupo() != null) {
					put(mod390, Mod3902024DetailKey.C0652, d.getRectifOpIntragrupo().getTipoX());
				}
				if (d.getRegularizInversiones() != null) {
					put(mod390, Mod3902024DetailKey.C0063, d.getRegularizInversiones());
				}
				if (d.getRegularizPorcProrrata() != null) {
					put(mod390, Mod3902024DetailKey.C0522, d.getRegularizPorcProrrata());
				}
				if (d.getSumDeducciones() != null) {
					put(mod390, Mod3902024DetailKey.C0064, d.getSumDeducciones());
				}

			}
			String res = iva.getRegGeneral().getResRegGeneral();
			if (AonStringUtils.isNotEmpty(res)) {
				try {
					double val = Double.parseDouble(res);
					put(mod390, Mod3902024DetailKey.C0065, BigDecimal.valueOf(val));
				} catch (NumberFormatException e) {
					// Nothing
				}
			}
		}
		RegSimplificado reg = iva.getRegSimplificado();
		if (reg != null) {
			if (!reg.getActividad().isEmpty()) {
				Actividad actividad = reg.getActividad().get(0);
				SimpliedRegimeActivity sra = getSimpliedRegimeActivity(actividad);
				mod390.setSimpRegime1(sra);
			}
			if (reg.getActividad().size() > 1) {
				Actividad actividad = reg.getActividad().get(1);
				SimpliedRegimeActivity sra = getSimpliedRegimeActivity(actividad);
				mod390.setSimpRegime2(sra);
			}
			if (!reg.getActAgricGanadForest().isEmpty()) {
				ActAgricGanadForest actividad = reg.getActAgricGanadForest().get(0);
				FarmerRegimeActivity sra = getFarmerRegimeActivity(actividad);
				mod390.setFarmerRegime1(sra);
			}
			if (reg.getActAgricGanadForest().size() > 1) {
				ActAgricGanadForest actividad = reg.getActAgricGanadForest().get(1);
				FarmerRegimeActivity sra = getFarmerRegimeActivity(actividad);
				mod390.setFarmerRegime2(sra);
			}
			if (reg.getActAgricGanadForest().size() > 2) {
				ActAgricGanadForest actividad = reg.getActAgricGanadForest().get(2);
				FarmerRegimeActivity sra = getFarmerRegimeActivity(actividad);
				mod390.setFarmerRegime3(sra);
			}
			if (reg.getActAgricGanadForest().size() > 3) {
				ActAgricGanadForest actividad = reg.getActAgricGanadForest().get(3);
				FarmerRegimeActivity sra = getFarmerRegimeActivity(actividad);
				mod390.setFarmerRegime4(sra);
			}
			if (reg.getActAgricGanadForest().size() > 4) {
				ActAgricGanadForest actividad = reg.getActAgricGanadForest().get(4);
				FarmerRegimeActivity sra = getFarmerRegimeActivity(actividad);
				mod390.setFarmerRegime5(sra);
			}
			IvaDevengado ivaDev = reg.getIvaDevengado();
			if (ivaDev != null) {
				mod390.setBox74(ensureBigDecimal(ivaDev.getSumaCuotasNoAgric()));
				mod390.setBox75(ensureBigDecimal(ivaDev.getSumaCuotasAgric()));
				mod390.setBox76(ensureBigDecimal(ivaDev.getAdqIntracomunitarias()));
				mod390.setBox77(ensureBigDecimal(ivaDev.getInversionSujetoPasivo()));
				mod390.setBox78(ensureBigDecimal(ivaDev.getEntregasActivosFijos()));
				mod390.setBox79(ensureBigDecimal(ivaDev.getTotalCuota()));
			}
			IvaDeducible ivaDed = reg.getIvaDeducible();
			if (ivaDed != null) {
				mod390.setBox80(ensureBigDecimal(ivaDed.getIVASoportadoAdqActivosFijos()));
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
				mod390.setBox96(ensureBigDecimal(perNo.getTotDevIVASPRegDevMensual()));
				mod390.setBox524(ensureBigDecimal(perNo.getTotDevAdqElemTrans()));
				mod390.setBox97(ensureBigDecimal(perNo.getImporteACompensarUltimoPeriodo()));
				mod390.setBox98(ensureBigDecimal(perNo.getImporteADevolverUltimoPeriodo()));
				mod390.setBox662(ensureBigDecimal(perNo.getCuotasPendCompensar()));
			}
			PerSiRegGrupos perSi = res.getPerSiRegGrupos();
			if (perNo != null) {
				mod390.setBox525(ensureBigDecimal(perSi.getTotResulPositivos322()));
				mod390.setBox526(ensureBigDecimal(perSi.getTotResulNegativos322()));
			}
		}
		VolOperaciones vol = iva.getVolOperaciones();
		if (vol != null) {
			mod390.setBox99(ensureBigDecimal(vol.getOpRegGeneral()));
			mod390.setBox653(ensureBigDecimal(vol.getOpRegEspCriterioCaja()));
			mod390.setBox103(ensureBigDecimal(vol.getEntregasIntracomunitariasExentas()));
			mod390.setBox104(ensureBigDecimal(vol.getExportacionesExentasConDrchoDeduccion()));
			mod390.setBox105(ensureBigDecimal(vol.getOpExentasSinDrchoDeduccion()));
			mod390.setBox110(ensureBigDecimal(vol.getOpNoSujetas()));
			mod390.setBox125(ensureBigDecimal(vol.getBox125()));
			mod390.setBox126(ensureBigDecimal(vol.getBox126()));
			mod390.setBox127(ensureBigDecimal(vol.getBox127()));
			mod390.setBox128(ensureBigDecimal(vol.getBox128()));
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
			mod390.setBox109(ensureBigDecimal(op.getAdqIntracomunitariasExentas()));
			mod390.setBox231(ensureBigDecimal(op.getImportacionesExentas()));
			mod390.setBox232(ensureBigDecimal(op.getBasesIVASoportadoNoDeducible()));
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
		if (prorratas != null) {
			for (Pro pro : prorratas.getPro()) {
				Prorrata pr = new Prorrata();
				pr.setActivity(pro.getActividad());
				pr.setCnae(pro.getCNAE());
				pr.setAmount(ensureBigDecimal(pro.getImpOper()));
				pr.setAmountWithRight(ensureBigDecimal(pro.getImpOperConDrchoDed()));
				pr.setPercent(ensureBigDecimal(pro.getPorc()));
				pr.setType(pro.getTipo());
				mod390.getProrratas().add(pr);
			}
		}

		if (iva.getIVADeducibleGrupo1() != null) {
			DeductionRegime regime = new DeductionRegime();
			mod390.setRegime1(regime);
			if (iva.getIVADeducibleGrupo1().getOpInteriores() != null) {
				if (iva.getIVADeducibleGrupo1().getOpInteriores().getBienesyServiciosCorrientes() != null) {
					regime.setBase1(ensureBigDecimal(
							iva.getIVADeducibleGrupo1().getOpInteriores().getBienesyServiciosCorrientes().getBI()));
					regime.setQuota1(ensureBigDecimal(
							iva.getIVADeducibleGrupo1().getOpInteriores().getBienesyServiciosCorrientes().getCuota()));
				}
				if (iva.getIVADeducibleGrupo1().getOpInteriores().getBienesInversion() != null) {
					regime.setBase2(ensureBigDecimal(
							iva.getIVADeducibleGrupo1().getOpInteriores().getBienesInversion().getBI()));
					regime.setQuota2(ensureBigDecimal(
							iva.getIVADeducibleGrupo1().getOpInteriores().getBienesInversion().getCuota()));
				}
			}
			if (iva.getIVADeducibleGrupo1().getImportaciones() != null) {
				if (iva.getIVADeducibleGrupo1().getImportaciones().getBienesCorrientes() != null) {
					regime.setBase3(ensureBigDecimal(
							iva.getIVADeducibleGrupo1().getImportaciones().getBienesCorrientes().getBI()));
					regime.setQuota3(ensureBigDecimal(
							iva.getIVADeducibleGrupo1().getImportaciones().getBienesCorrientes().getCuota()));
				}
				if (iva.getIVADeducibleGrupo1().getImportaciones().getBienesInversion() != null) {
					regime.setBase4(ensureBigDecimal(
							iva.getIVADeducibleGrupo1().getImportaciones().getBienesInversion().getBI()));
					regime.setQuota4(ensureBigDecimal(
							iva.getIVADeducibleGrupo1().getImportaciones().getBienesInversion().getCuota()));
				}
			}
			if (iva.getIVADeducibleGrupo1().getAdqIntracomunitarias() != null) {
				if (iva.getIVADeducibleGrupo1().getAdqIntracomunitarias().getBienesCorrientes() != null) {
					regime.setBase5(ensureBigDecimal(
							iva.getIVADeducibleGrupo1().getAdqIntracomunitarias().getBienesCorrientes().getBI()));
					regime.setQuota5(ensureBigDecimal(
							iva.getIVADeducibleGrupo1().getAdqIntracomunitarias().getBienesCorrientes().getCuota()));
				}
				if (iva.getIVADeducibleGrupo1().getAdqIntracomunitarias().getBienesInversion() != null) {
					regime.setBase6(ensureBigDecimal(
							iva.getIVADeducibleGrupo1().getAdqIntracomunitarias().getBienesInversion().getBI()));
					regime.setQuota6(ensureBigDecimal(
							iva.getIVADeducibleGrupo1().getAdqIntracomunitarias().getBienesInversion().getCuota()));
				}
			}
			if (iva.getIVADeducibleGrupo1().getCompRegEspAgricGanadPesca() != null) {
				regime.setBase7(ensureBigDecimal(iva.getIVADeducibleGrupo1().getCompRegEspAgricGanadPesca().getBI()));
				regime.setQuota7(
						ensureBigDecimal(iva.getIVADeducibleGrupo1().getCompRegEspAgricGanadPesca().getCuota()));
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
					regime.setBase1(ensureBigDecimal(
							iva.getIVADeducibleGrupo2().getOpInteriores().getBienesyServiciosCorrientes().getBI()));
					regime.setQuota1(ensureBigDecimal(
							iva.getIVADeducibleGrupo2().getOpInteriores().getBienesyServiciosCorrientes().getCuota()));
				}
				if (iva.getIVADeducibleGrupo2().getOpInteriores().getBienesInversion() != null) {
					regime.setBase2(ensureBigDecimal(
							iva.getIVADeducibleGrupo2().getOpInteriores().getBienesInversion().getBI()));
					regime.setQuota2(ensureBigDecimal(
							iva.getIVADeducibleGrupo2().getOpInteriores().getBienesInversion().getCuota()));
				}
			}
			if (iva.getIVADeducibleGrupo2().getImportaciones() != null) {
				if (iva.getIVADeducibleGrupo2().getImportaciones().getBienesCorrientes() != null) {
					regime.setBase3(ensureBigDecimal(
							iva.getIVADeducibleGrupo2().getImportaciones().getBienesCorrientes().getBI()));
					regime.setQuota3(ensureBigDecimal(
							iva.getIVADeducibleGrupo2().getImportaciones().getBienesCorrientes().getCuota()));
				}
				if (iva.getIVADeducibleGrupo2().getImportaciones().getBienesInversion() != null) {
					regime.setBase4(ensureBigDecimal(
							iva.getIVADeducibleGrupo2().getImportaciones().getBienesInversion().getBI()));
					regime.setQuota4(ensureBigDecimal(
							iva.getIVADeducibleGrupo2().getImportaciones().getBienesInversion().getCuota()));
				}
			}
			if (iva.getIVADeducibleGrupo2().getAdqIntracomunitarias() != null) {
				if (iva.getIVADeducibleGrupo2().getAdqIntracomunitarias().getBienesCorrientes() != null) {
					regime.setBase5(ensureBigDecimal(
							iva.getIVADeducibleGrupo2().getAdqIntracomunitarias().getBienesCorrientes().getBI()));
					regime.setQuota5(ensureBigDecimal(
							iva.getIVADeducibleGrupo2().getAdqIntracomunitarias().getBienesCorrientes().getCuota()));
				}
				if (iva.getIVADeducibleGrupo2().getAdqIntracomunitarias().getBienesInversion() != null) {
					regime.setBase6(ensureBigDecimal(
							iva.getIVADeducibleGrupo2().getAdqIntracomunitarias().getBienesInversion().getBI()));
					regime.setQuota6(ensureBigDecimal(
							iva.getIVADeducibleGrupo2().getAdqIntracomunitarias().getBienesInversion().getCuota()));
				}
			}
			if (iva.getIVADeducibleGrupo1().getCompRegEspAgricGanadPesca() != null) {
				regime.setBase7(ensureBigDecimal(iva.getIVADeducibleGrupo2().getCompRegEspAgricGanadPesca().getBI()));
				regime.setQuota7(
						ensureBigDecimal(iva.getIVADeducibleGrupo2().getCompRegEspAgricGanadPesca().getCuota()));
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
					regime.setBase1(ensureBigDecimal(
							iva.getIVADeducibleGrupo3().getOpInteriores().getBienesyServiciosCorrientes().getBI()));
					regime.setQuota1(ensureBigDecimal(
							iva.getIVADeducibleGrupo3().getOpInteriores().getBienesyServiciosCorrientes().getCuota()));
				}
				if (iva.getIVADeducibleGrupo3().getOpInteriores().getBienesInversion() != null) {
					regime.setBase2(ensureBigDecimal(
							iva.getIVADeducibleGrupo3().getOpInteriores().getBienesInversion().getBI()));
					regime.setQuota2(ensureBigDecimal(
							iva.getIVADeducibleGrupo3().getOpInteriores().getBienesInversion().getCuota()));
				}
			}
			if (iva.getIVADeducibleGrupo3().getImportaciones() != null) {
				if (iva.getIVADeducibleGrupo3().getImportaciones().getBienesCorrientes() != null) {
					regime.setBase3(ensureBigDecimal(
							iva.getIVADeducibleGrupo3().getImportaciones().getBienesCorrientes().getBI()));
					regime.setQuota3(ensureBigDecimal(
							iva.getIVADeducibleGrupo3().getImportaciones().getBienesCorrientes().getCuota()));
				}
				if (iva.getIVADeducibleGrupo3().getImportaciones().getBienesInversion() != null) {
					regime.setBase4(ensureBigDecimal(
							iva.getIVADeducibleGrupo3().getImportaciones().getBienesInversion().getBI()));
					regime.setQuota4(ensureBigDecimal(
							iva.getIVADeducibleGrupo3().getImportaciones().getBienesInversion().getCuota()));
				}
			}
			if (iva.getIVADeducibleGrupo3().getAdqIntracomunitarias() != null) {
				if (iva.getIVADeducibleGrupo3().getAdqIntracomunitarias().getBienesCorrientes() != null) {
					regime.setBase5(ensureBigDecimal(
							iva.getIVADeducibleGrupo3().getAdqIntracomunitarias().getBienesCorrientes().getBI()));
					regime.setQuota5(ensureBigDecimal(
							iva.getIVADeducibleGrupo3().getAdqIntracomunitarias().getBienesCorrientes().getCuota()));
				}
				if (iva.getIVADeducibleGrupo3().getAdqIntracomunitarias().getBienesInversion() != null) {
					regime.setBase6(ensureBigDecimal(
							iva.getIVADeducibleGrupo3().getAdqIntracomunitarias().getBienesInversion().getBI()));
					regime.setQuota6(ensureBigDecimal(
							iva.getIVADeducibleGrupo3().getAdqIntracomunitarias().getBienesInversion().getCuota()));
				}
			}
			if (iva.getIVADeducibleGrupo3().getCompRegEspAgricGanadPesca() != null) {
				regime.setBase7(ensureBigDecimal(iva.getIVADeducibleGrupo3().getCompRegEspAgricGanadPesca().getBI()));
				regime.setQuota7(
						ensureBigDecimal(iva.getIVADeducibleGrupo3().getCompRegEspAgricGanadPesca().getCuota()));
			}
			if (iva.getIVADeducibleGrupo3().getRectDeducciones() != null) {
				regime.setBase8(ensureBigDecimal(iva.getIVADeducibleGrupo3().getRectDeducciones().getBI()));
				regime.setQuota8(ensureBigDecimal(iva.getIVADeducibleGrupo3().getRectDeducciones().getCuota()));
			}
			regime.setQuota9(ensureBigDecimal(iva.getIVADeducibleGrupo3().getRegInversiones()));
			regime.setQuota10(ensureBigDecimal(iva.getIVADeducibleGrupo3().getSumaDeducciones()));
		}

	}

	private static FarmerRegimeActivity getFarmerRegimeActivity(ActAgricGanadForest actividad) {
		FarmerRegimeActivity ac = new FarmerRegimeActivity();
		ac.setCodigo(actividad.getCodigo());
		ac.setIncomes(ensureBigDecimal(actividad.getVolIngresos()));
		ac.setQuotaIndex(ensureBigDecimal(actividad.getIndCuota()));
		ac.setAccrualQuota(ensureBigDecimal(actividad.getCuotaDevengada()));
		ac.setDanaReduction(ensureBigDecimal(actividad.getDana()));		
		ac.setInputQuotas(ensureBigDecimal(actividad.getCuotasSoportadas()));
		ac.setQuota(ensureBigDecimal(actividad.getCuotaRegSimplificado()));
		return ac;
	}

	private static SimpliedRegimeActivity getSimpliedRegimeActivity(Actividad act) {
		SimpliedRegimeActivity sra = new SimpliedRegimeActivity();
		sra.setEpigrafe(act.getEpigrafe());
		List<Modulo> modulos = act.getModulo();
		
		for (Modulo modulo : modulos) {
			switch (modulo.getNumModulo()) {
				case "1" -> {
					sra.setUnit1(ensureBigDecimal(modulo.getUnidades()));
					sra.setAmount1(ensureBigDecimal(modulo.getImporte()));
				}
				case "2" -> {
					sra.setUnit2(ensureBigDecimal(modulo.getUnidades()));
					sra.setAmount2(ensureBigDecimal(modulo.getImporte()));
				}
				case "3" -> {
					sra.setUnit3(ensureBigDecimal(modulo.getUnidades()));
					sra.setAmount3(ensureBigDecimal(modulo.getImporte()));
				}
				case "4" -> {
					sra.setUnit4(ensureBigDecimal(modulo.getUnidades()));
					sra.setAmount4(ensureBigDecimal(modulo.getImporte()));
				}
				case "5" -> {
					sra.setUnit5(ensureBigDecimal(modulo.getUnidades()));
					sra.setAmount5(ensureBigDecimal(modulo.getImporte()));
				}
				case "6" -> {
					sra.setUnit6(ensureBigDecimal(modulo.getUnidades()));
					sra.setAmount6(ensureBigDecimal(modulo.getImporte()));
				}
				case "7" -> {
					sra.setUnit7(ensureBigDecimal(modulo.getUnidades()));
					sra.setAmount7(ensureBigDecimal(modulo.getImporte()));
				}
				default -> {
					// do nothing					
				}
			}
		}
		
		sra.setBoxC(ensureBigDecimal(act.getCuotaDevengada()));
		sra.setBoxC1(ensureBigDecimal(act.getLorca()));
		sra.setBoxC2(ensureBigDecimal(act.getDana()));
		sra.setBoxD(ensureBigDecimal(act.getCuotaSoportada()));
		sra.setBoxE(ensureBigDecimal(act.getIndiceCorrector()));
		sra.setBoxF(ensureBigDecimal(act.getResultado()));
		sra.setBoxG(ensureBigDecimal(act.getPorcCuotaMinima()));
		sra.setBoxH(ensureBigDecimal(act.getDevCuotaSopOtrosPaises()));
		sra.setBoxI(ensureBigDecimal(act.getCuotaMinima()));
		sra.setBoxJ(ensureBigDecimal(act.getCuotaRegSimplificado()));
		return sra;
	}

	private static void put(Mod3902024 mod390, Mod3902024DetailKey key, TipoBaseImponibleYCuota tipo) {
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

	private static void put(Mod3902024 mod390, Mod3902024DetailKey key, BigDecimal quota) {
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
