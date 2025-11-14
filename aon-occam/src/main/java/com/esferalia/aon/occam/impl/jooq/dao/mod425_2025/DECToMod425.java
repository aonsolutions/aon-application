package com.esferalia.aon.occam.impl.jooq.dao.mod425_2025;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.esferalia.aon.occam.api.model.fiscal.Address;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Activity425;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025.Mod425Detail;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025DetailKey;
import com.esferalia.aon.occam.api.model.fiscal.mod425.SimpliedRegimeActivity425;
import com.esferalia.aon.occam.api.model.type.CanariasIAE;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class DECToMod425 {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	private DECToMod425() {
	}
	
	public static void populate(Mod4252025 mod, DEC dec) throws ParseException {
		
		// FALTA - ESTOS DATOS YA VIENEN DEL MODELO PUES ESTAN EN CAMPOS DE LA BASE DE DATOS
//		dec.setMOD("425");  // Identificador del modelo                                  
//		dec.setANY(AonNumberUtils.toString(mod.getYear())); // Ejercicio al que se refiere la autoliquidación
//		dec.setPER("0A");   // Período al que se refiere la autoliquidación: "0A" Anual
//		mod.setReplacement(dec.getSUS() != null && dec.getSUS().equals("X")); // Sustitutiva
//		mod.setReplacedReceipt(dec.getNJA()); // Número anterior de justificante		
		mod.setReplacementDueInsolvencyState(dec.getSUA() != null && dec.getSUA().equals("X")); // Sustitutiva por rectificación de cuotas en caso de concurso de acreedores
		
		getIde(mod, dec.getIDE());  // Datos identificativos y opciones tributarias
		getEst(mod, dec.getEST());  // Datos estadísticos
		addRep(mod, dec.getREP());  // Representantes
		getReg(mod, dec.getREG());  // Régimen General
		getRes(mod, dec.getRES());  // Régimen Simplificado
		getLiq(mod, dec.getLIQ());  // Resultado de la liquidación anual
		getAut(mod, dec.getAUT());  // Resultado de las autoliquidaciones
		getOpe(mod, dec.getOPE());  // Operaciones especificas
		getRcc(mod, dec.getRCC());  // Régimen especial del criterio de caja
		getRpe(mod, dec.getRPE());  // Régimen especial del pequeño empresario o profesional
		
	}
	
	//	Datos identificativos y opciones tributarias
	private static void getIde(Mod4252025 mod, TIDENTIGIC ide) {

		mod.setTaxRefund(ide.getRDM() != null && ide.getRDM() == SINOType.S);  // Inscrito en el Registro de devolución mensual
		mod.setSpecialRegime(  ide.getRPE() != null && ide.getRPE() == SINOType.S ); // Régimen especial pequeño empresario o profesional
		
		DATOSPERSONALES dp = ide.getOTP();
		if (dp != null) {
			mod.setDocument(dp.getNIF());        // Nif de la persona
			mod.setName(dp.getNRS());        	 // Nombre o razón social
			mod.setStreetInitial(dp.getSVP());   // Siglas vía pública
			mod.setStreetName( dp.getNVP());     // Nombre de la vía pública
			mod.setStreetNumber(dp.getNPK());    // Número de edificio/pto kilométrico
			mod.setStreetStair(dp.getESC());     // Escalera
			mod.setStreetFloor(dp.getPIS());     // Piso
			mod.setStreetDoor(dp.getPUE());      // Puerta
			mod.setTown( dp.getLOC());       	 // Localidad
			mod.setContactPhone(dp.getTEL());    // Teléfono
			mod.setProvinceCode(dp.getPOP());    // Código de provincia 
			mod.setTownCode(dp.getCMU());        // Código de municipio
			mod.setZip(dp.getCP());              // Código Postal
		}
		
	}
	
	private static void getEst(Mod4252025 mod, TDATOSESTADISTICOS425 est) {
      	
		mod.setMod415(est.getDTP() != null && est.getDTP() == SINOType.S); 				  // ¿Está obligado a presentar el modelo 415 por realizar operaciones con terceras personas por importe superior a 3.005,06 euros?
		mod.setAccrualRegimeTarget(est.getRECC() != null && est.getRECC() == SINOType.S); // ¿Ha sido destinatario de operaciones a las que se aplica el régimen especial de criterio de caja?
		
		// Datos estadísticos
		int i = 0;
		for (OPERACIONDATOSESTADISTICOS op : est.getACT()) {
			Activity425 activity = new Activity425();
			activity.setKey(op.getCLA());                            // Clave de la actividad
			activity.setEpigraph(op.getEPI());                       // Epígrafe
			activity.setRegime(op.getREG());                         // Régimen
			activity.setProvisionalProrate(getAmount(op.getPRO()));  // Prorrata provisional
			activity.setFinalProrate(getAmount(op.getDEF()));        // Prorrata definitiva
			activity.setSpecialProrate(op.getESP() != null && op.getESP().equals("X")); // Prorrata especial
			activity.setDescription(CanariasIAE.getDescription(op.getEPI())); // Descripción del epígrafe (se obtiene de CanariasIAE, porque no se graba en el XML)
			switch (i) {
				case 0: mod.setMainActivity(activity); break;
				case 1: mod.setActivity1(activity); break;
				case 2: mod.setActivity2(activity); break;
				case 3: mod.setActivity3(activity); break;
				case 4: mod.setActivity4(activity); break;
				case 5: mod.setActivity5(activity); break;
			}			
			i++;
		}
		
		// Declaración de sujeto pasivo incluido en autoliquidaciones conjuntas
		TDATOSPERSONALES dp = est.getPER();
		if (dp != null) {
			mod.setMergedDeclarationDocument(dp.getNIF());
			mod.setMergedDeclarationName(dp.getNRS());
		}
		
	}
	
	// Añadir Representantes
	private static void addRep(Mod4252025 mod, List<TREPRESENTANTE> repList) throws ParseException {

		int i = 0;
		for (TREPRESENTANTE rep : repList) {
			TPERSONA per = rep.getOTP();
			if (rep.getTIP() != null && rep.getTIP().equals("PF")) {
				
				// Representante persona física
				Address address = new Address();
				
				if (per != null) {
					TDATOSPERSONALES dp = per.getPER();
					if (dp != null) {
						address.setRdocument(dp.getNIF());                // Nif de la persona
						address.setRname(dp.getNRS());  // Nombre o razón social
					}
					TDIRECCION dir = per.getDIR();
					if (dir != null) {
						address.setRstreetType(dir.getSVP());      // Siglas vía pública
						address.setRstreetName(dir.getNVP());      // Nombre de la vía pública
						address.setRstreetNumber(dir.getNPK());    // Número de edificio/pto kilométrico
						address.setRstreetStair(dir.getESC());     // Escalera
						address.setRstreetFloor(dir.getPIS());     // Piso
						address.setRstreetDoor(dir.getPUE());      // Puerta
						address.setRtown(dir.getLOC());            // Localidad
						address.setRprovince(AonNumberUtils.toint(dir.getPOP())); // Código de provincia // FALTA - SE GUARDA EL CODIGO O SE GUARDA EL NOMBRE DE LA PROVINCIA
//						address.setRtownCode(dir.getCMU());                        // Código de municipio // FALTA - NO LO TENGO EN ESTE MOMENTO, VER SI ES NECESARIO	
						address.setRzip(dir.getCP());                                // Código Postal
						address.setRphone(dir.getTEL());       	                     // Teléfono  )
					}
				}
				mod.setAddress(address);
			} else if (rep.getTIP() != null && rep.getTIP().equals("PJ")) {
				
				// Representante persona jurídica
				LegalRepresentative legalRepr = new LegalRepresentative();
				legalRepr.setNotary(rep.getNOT());
				legalRepr.setNotaryDate(rep.getFPO() == null ? null : DATE_FORMAT.parse(rep.getFPO()));
				
				if (per != null) {
					TDATOSPERSONALES dp = per.getPER();
					if (dp != null) {
						legalRepr.setDocument(dp.getNIF()); // Nif de la persona
						legalRepr.setName(dp.getNRS());  	// Nombre o razón social
					}
				}
				
				switch (i) {
					case 0: mod.setLegalRepr1(legalRepr); break;
					case 1: mod.setLegalRepr2(legalRepr); break;
					case 2: mod.setLegalRepr3(legalRepr); break;
				}				
				i++;				
			}
		}
		
	}
	
	// Régimen general
	private static void getReg(Mod4252025 mod, TREGIMENGENERAL425 reg) {
		
		if (mod.getGeneralRegime() != null)
			mod.getGeneralRegime().clear();
		
		if (reg != null) {
		
			// Devengado
			
			TDEVENGADO425 dev = reg.getDEV();
			if (dev != null) {
				addRow(dev.getRGO(), mod
						, Mod4252025DetailKey.C003
						, Mod4252025DetailKey.C006	
						, Mod4252025DetailKey.C009	
						, Mod4252025DetailKey.C012	
						, Mod4252025DetailKey.C015	
						, Mod4252025DetailKey.C018	
						, Mod4252025DetailKey.C018B);
				
				addRow(dev.getRBU(), mod
						, Mod4252025DetailKey.C021
						, Mod4252025DetailKey.C024	
						, Mod4252025DetailKey.C027	
						, Mod4252025DetailKey.C030	
						, Mod4252025DetailKey.C033);
				
				addRow(dev.getROA(), mod
						, Mod4252025DetailKey.C036
						, Mod4252025DetailKey.C039	
						, Mod4252025DetailKey.C042
						, Mod4252025DetailKey.C045
						, Mod4252025DetailKey.C048);
				
				addRow(dev.getRCC(), mod
						, Mod4252025DetailKey.C051
						, Mod4252025DetailKey.C054	
						, Mod4252025DetailKey.C057
						, Mod4252025DetailKey.C060
						, Mod4252025DetailKey.C063
						, Mod4252025DetailKey.C066
						, Mod4252025DetailKey.C066B);
				
				getBaseCuota3col(mod, Mod4252025DetailKey.C069, dev.getREA());
				getBaseCuota3col(mod, Mod4252025DetailKey.C071, dev.getMBC());
				getBaseCuota3col(mod, Mod4252025DetailKey.C073, dev.getMCA());
				getBaseCuota3col(mod, Mod4252025DetailKey.C076, dev.getOIN());
				getBaseCuota3col(mod, Mod4252025DetailKey.C078, dev.getCRV());
				
				// Total bases imponibles
				mod.ensure(Mod4252025DetailKey.C074).setTaxableBase(getAmount(dev.getTBA()));
				
				// Total cuotas devengadas
				mod.ensure(Mod4252025DetailKey.C079).setQuota(getAmount(dev.getTCU()));
			}
				
			// Deducible
			
			TDEDUCIBLE ded = reg.getDED();
			if (ded != null) {
				getBaseCuota3col(mod, Mod4252025DetailKey.C081, ded.getOIC());
				getBaseCuota3col(mod, Mod4252025DetailKey.C083, ded.getOII());
				getBaseCuota3col(mod, Mod4252025DetailKey.C085, ded.getIMC());
				getBaseCuota3col(mod, Mod4252025DetailKey.C087, ded.getIMI());
				getBaseCuota3col(mod, Mod4252025DetailKey.C089, ded.getRED());
				getBaseCuota3col(mod, Mod4252025DetailKey.C090, ded.getCRA());
				getBaseCuota3col(mod, Mod4252025DetailKey.C091, ded.getRBI());
				getBaseCuota3col(mod, Mod4252025DetailKey.C092, ded.getRIA());
				getBaseCuota3col(mod, Mod4252025DetailKey.C093, ded.getRPP());
				mod.ensure(Mod4252025DetailKey.C094).setQuota(getAmount(ded.getTOT()));
			}
			
			// Resultado del régimen general
			mod.ensure(Mod4252025DetailKey.C095).setQuota(getAmount(reg.getRES()));
		
		}
		
	}
	
	// Régimen simplificado
	private static void getRes(Mod4252025 mod, TREGIMENSIMPLIFICADO425 res) {
		
		if (res != null) {
			addRS(mod, res.getMOD()); // Módulos del régimen simplificado
			getCrs(mod, res.getCRS()); // Cuotas del régimen simplificado
			mod.setBox111(getAmount(res.getRES())); // Resultado del régimen simplificado
		}
		
	}

	// Módulos del régimen simplificado
	private static void addRS(Mod4252025 mod, List<TMODULO> modList) {
		
		int i = 0;
		for (TMODULO module : modList) {			
			if (module != null && AonStringUtils.isNotBlank(module.getEPI())) {
				SimpliedRegimeActivity425 simpRegime = new SimpliedRegimeActivity425();
				simpRegime.setEpigrafe(module.getEPI()); // Código del epígrafe
				simpRegime.setUnit1(getAmount(module.getMOD1())); // Módulo 1
				simpRegime.setUnit2(getAmount(module.getMOD2())); // Módulo 2
				simpRegime.setUnit3(getAmount(module.getMOD3())); // Módulo 3
				simpRegime.setUnit4(getAmount(module.getMOD4())); // Módulo 4
				simpRegime.setUnit5(getAmount(module.getMOD5())); // Módulo 5
				simpRegime.setUnit6(getAmount(module.getMOD6())); // Módulo 6
				simpRegime.setUnit7(getAmount(module.getMOD7())); // Módulo 7
				// FALTA - SI PONEMOS TAMBIEN LOS IMPORTES POR UNIDAD DE MEDIDA Y TOTAL POR MODULO, HABRIA QUE CALCULARLO AQUI SACANDOLO DE ALGUN SITIO
				simpRegime.setBoxA(getAmount(module.getCASA()));   // Cuota anual devengada por operaciones corrientes
				simpRegime.setBoxB(getAmount(module.getCASB()));  // Cuotas soportadas por operaciones corrientes
				simpRegime.setBoxC(getAmount(module.getCASC()));  // Índice corrector
				simpRegime.setBoxD(getAmount(module.getCASD()));  // Diferencia
				simpRegime.setBoxE(getAmount(module.getCASE()));  // Porcentaje cuota mínima operaciones corrientes
				simpRegime.setBoxF(getAmount(module.getCASF()));  // Cuota mínima
				simpRegime.setBoxG(getAmount(module.getCASG()));  // Cuota anual derivada de régimen simplificado
				switch (i) {
					case 0: mod.setSimpRegime1(simpRegime); break;
					case 1: mod.setSimpRegime2(simpRegime); break;
				}
				i++;			
			}
		}
		
	}
	
	// Cuotas del régimen simplificado
	private static void getCrs(Mod4252025 mod, TSIMPLIFICADO crs) {
		
		if (crs != null) {
			mod.setBox103(getAmount(crs.getTRS())); // Total cuota anual derivada del régimen simplificado
			mod.setBox104(getAmount(crs.getCAF())); // Cuotas devengadas por entregas o transmisiones de activos fijos o por inversión del sujeto pasivo
			mod.setBox105(getAmount(crs.getCBI())); // Cuotas devengadas por arrendamientos de bienes inmuebles
			mod.setBox106(getAmount(crs.getRCU())); // Rectificación de cuotas impositivas repercutidas
			mod.setBox107(getAmount(crs.getTOT())); // Total cuotas
			                           
			mod.setBox108(getAmount(crs.getDAF())); // Cuotas deducibles por adquisición o importación de activos fijos
			mod.setBox109(getAmount(crs.getDBI())); // Cuotas deducibles por arrendamientos de bienes inmuebles
			mod.setBox110(getAmount(crs.getTOD())); // Total cuotas deducibles 
		}
		
	}

	private static void addRow(List<TBASECUOTA> list, Mod4252025 mod, Mod4252025DetailKey... detailKeys) {
		
		for (TBASECUOTA baseCuota : list) {
			for (Mod4252025DetailKey detailKey : detailKeys) {
				if (addDetailKey(mod, detailKey, baseCuota))
					break;
			}
		}
		
	}
	
	private static boolean addDetailKey(Mod4252025 mod, Mod4252025DetailKey detailKey, TBASECUOTA baseCuota) {
		
		double percent = getAmount(baseCuota.getTIP());
		if (AonMathUtils.equals(percent, detailKey.getPercent())) {
			getBaseCuota3col(mod, detailKey, baseCuota);
			return true;
		}
		return false;
		
	}

	private static void getBaseCuota3col(Mod4252025 mod, Mod4252025DetailKey detailKey, TBASECUOTA baseCuota) {
		if (baseCuota != null) {
			Mod425Detail detail = mod.ensure(detailKey);
			detail.setTaxableBase(getAmount(baseCuota.getBAS()));
			detail.setPercent(getAmount(baseCuota.getTIP()));
			detail.setQuota(getAmount(baseCuota.getCUO()));
		}
			
	}
	
	// Resultado de la liquidación anual
	private static void getLiq(Mod4252025 mod, TLIQUIDACION liq) {
		
		if (liq != null) {
			mod.setBox112(getAmount(liq.getRCU())); // Regularización cuotas artículo 22.8.5ª Ley 20/1991
			mod.setBox113(getAmount(liq.getSUT())); // Suma de resultados
			mod.setBox114(getAmount(liq.getCPA())); // Cuota de IGIC a compensar de períodos anteriores
			mod.setBox115(getAmount(liq.getRLI())); // Resultado de la liquidación anual
		}
		
	}
	
	// Resultado de las autoliquidaciones
	private static void getAut(Mod4252025 mod, TAUTOLIQUIDACION425 aut) {
		
		if (aut != null) {
			mod.setBox116(getAmount(aut.getTOA())); // Total de ingresos realizados en las autoliquidaciones por IGIC del ejercicio
			mod.setBox117(getAmount(aut.getTOM())); // Total devoluciones mensuales por IGIC a sujetos pasivos inscritos en el registro de devolución mensual
			mod.setBox118(getAmount(aut.getIMC())); // Importe cuando el resultado de la autoliquidación última del año es a compensar
			mod.setBox119(getAmount(aut.getIMD())); // Importe cuando el resultado de la autoliquidación última del año es a devolver
		}
		
	}
	
	// Operaciones específicas
	private static void getOpe(Mod4252025 mod, TOPERACIONESEJERCICIO ope) {
		
		if (ope != null) {
			mod.setBox120(getAmount(ope.getREG()));  // Operaciones en régimen general
			mod.setBox121(getAmount(ope.getREC()));  // Operaciones en las que habiéndoles sido aplicado el régimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el artículo 18 Ley 20/1991
			mod.setBox122(getAmount(ope.getEXP()));  // Exportaciones definitivas y operaciones asimiladas a la exportación
			mod.setBox123(getAmount(ope.getEXE()));  // Operaciones relativas a áreas exentas
			mod.setBox124(getAmount(ope.getOIE()));  // Operaciones interiores exentas por el artículo 25 de la Ley 19/1994 realizadas por el sujeto pasivo.
			mod.setBox125(getAmount(ope.getECD()));  // Otras operaciones exentas con derecho a deducción
			mod.setBox126(getAmount(ope.getESD()));  // Operaciones exentas sin derecho a deducción
			mod.setBox127(getAmount(ope.getRES()));  // Operaciones en régimen simplificado
			mod.setBox128(getAmount(ope.getNSJ()));  // Operaciones no sujetas por reglas de localización o con inversión del sujeto pasivo.
			mod.setBox129(getAmount(ope.getREAG())); // Operaciones en régimen especial de la agricultura, ganadería y pesca.
			mod.setBox130(getAmount(ope.getREBU())); // Operaciones en régimen especial de bienes usados, objetos de arte, antigüedades o colección.
			mod.setBox131(getAmount(ope.getREAV())); // Operaciones en régimen especial de Agencia de Viajes.
			mod.setBox132(getAmount(ope.getEBI()));  // Entregas de bienes inmuebles y operaciones financieras no habituales.
			mod.setBox133(getAmount(ope.getEBT()));  // Entregas de bienes de inversión para el transmitente.
			mod.setBox134(getAmount(ope.getTOT()));  // Total volumen operaciones
			mod.setBox135(getAmount(ope.getIBE()));  // Importaciones de bienes de inversión exentos por el artículo 25 de la Ley 19/1994
			mod.setBox136(getAmount(ope.getCND()));  // Cuotas de I.G.I.C. soportado no deducible
			mod.setBox137(getAmount(ope.getODD()));  // Otras operaciones no sujetas con derecho a deducción (artículo 29.4.1ªg) Ley 20/1991)
		}
		
	}
	
	//	Régimen especial del criterio de caja
	private static void getRcc(Mod4252025 mod, TOPERACIONESRECC rcc) {
		
		if (rcc != null) {
			TBASECUOTA ieb = rcc.getIEB();
			if (ieb != null) {
				// Importes de las entregas de bienes y prestaciones de servicios a las que habiéndoles sido aplicado el RECC, hubieran resultado devengadas conforme a la regla general de devengo contenida en el artículo 18 de	la Ley 20/1991
				mod.setBox138(getAmount(ieb.getBAS()));
				mod.setBox139(getAmount(ieb.getCUO())); 	
			}
			TBASECUOTA iab = rcc.getIAB();
			if (iab != null) {
				// Importes de las adquisiciones de bienes y prestaciones de servicios a las que sea de aplicacion o afecte el RECC conforme a la regla general de devengo contenida en el artículo 18 de la Ley 20/1991
				mod.setBox140(getAmount(iab.getBAS()));
				mod.setBox141(getAmount(iab.getCUO())); 	
			}
		}
		
	}

	// Régimen especial del pequeño empresario o profesional
	private static void getRpe(Mod4252025 mod, TOPERACIONESREPEP rpe) {
		
		if (rpe != null) {
			mod.setBox142(getAmount(rpe.getEPE())); // Importe de operaciones habituales u ocasionales sujetas al IGIC exentas por Régimen especial del pequeño empresario o profesional
			mod.setBox143(getAmount(rpe.getECM())); // Importe de operaciones sujetas al IGIC exentas por Régimen especial del comerciante minorista
			mod.setBox144(getAmount(rpe.getONS())); // Importe de entrega de bienes y prestaciones de servicios no sujetas al IGIC imputables a la sede de la actividad	económica situada en Canarias
			mod.setBox145(getAmount(rpe.getOFC())); // Importe de entregas de bienes y prestaciones de servicios no sujetas al IGIC imputables a otras sedes o establecimientos situados fuera de Canarias
			mod.setBox146(getAmount(rpe.getIST())); // Importe en el supuesto de transmisión de la totalidad o parte del patrimonio empresarial o profesional
			mod.setBox147(getAmount(rpe.getTOT())); // Total volumen de operaciones en el REPEP
		}
		
	}
	
	// --- METODOS AUXILIARES ---------------------------------------------------------------------------------------------------
	
	private static double getAmount(String amount) {
		return AonMathUtils.round(AonNumberUtils.todouble(amount) / 100.0);
	}

}
