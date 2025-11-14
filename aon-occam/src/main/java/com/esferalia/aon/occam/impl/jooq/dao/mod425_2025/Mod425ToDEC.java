package com.esferalia.aon.occam.impl.jooq.dao.mod425_2025;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.esferalia.aon.occam.api.model.fiscal.Address;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Activity425;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025.Mod425Detail;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025DetailKey;
import com.esferalia.aon.occam.api.model.fiscal.mod425.SimpliedRegimeActivity425;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod425ToDEC {
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");	
	
	private Mod425ToDEC() {
	}
	
	public static String getDeclaration(Mod4252025 mod425) throws JAXBException {
		System.out.println("Mod425ToDEC getDeclaration");
		DEC dec = getDEC(mod425);
		StringWriter writer = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(DEC.class);
		Marshaller um = context.createMarshaller();
		um.setProperty("jaxb.encoding", "ISO-8859-1");
		um.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		um.marshal(dec, writer);
		return writer.toString();		
	}

	private static DEC getDEC(Mod4252025 mod) {
		
		DEC dec = new DEC();
		
		dec.setMOD("425");  // Identificador del modelo                                  
		dec.setANY(AonNumberUtils.toString(mod.getYear())); // Ejercicio al que se refiere la autoliquidación
		dec.setPER("0A");   // Período al que se refiere la autoliquidación: "0A" Anual
		
		if (mod.isReplacement()) {
			dec.setSUS("X");  // Sustitutiva
		}
		if (mod.isReplacementDueInsolvencyState()) {
			dec.setSUA("X");  // Sustitutiva por rectificación de cuotas en caso de concurso de acreedores
		}
		if (mod.isReplacement() || mod.isReplacementDueInsolvencyState()) {
			dec.setNJA(mod.getReplacedReceipt()); // Número anterior de justificante
		}
		
		dec.setIDE(getIde(mod)); // Datos identificativos y opciones tributarias
		dec.setEST(getEst(mod)); // Datos estadísticos
		
		addRepPF(dec.getREP(), mod.getAddress());    // Representante persona física
		addRepPJ(dec.getREP(), mod.getLegalRepr1()); // Representante persona juridica (1)
		addRepPJ(dec.getREP(), mod.getLegalRepr2()); // Representante persona juridica (2)
		addRepPJ(dec.getREP(), mod.getLegalRepr3()); // Representante persona juridica (3)

		dec.setREG(getReg(mod)); // Régimen General
		
		if (mod.isSimplifiedRegime()) {
			dec.setRES(getRes(mod)); // Régimen Simplificado
		}
		
		dec.setLIQ(getLiq(mod)); // Resultado de la liquidación anual
		dec.setAUT(getAut(mod)); // Resultado de las autoliquidaciones
		dec.setOPE(getOpe(mod)); // Operaciones especificas
		dec.setRCC(getRcc(mod)); // Régimen especial del criterio de caja
		dec.setRPE(getRpe(mod)); // Régimen especial del pequeño empresario o profesional
	
		return dec;
		
	}
	
	//	Datos identificativos y opciones tributarias
	private static TIDENTIGIC getIde(Mod4252025 mod) {

		TIDENTIGIC ide = new TIDENTIGIC();
		
		ide.setRDM(getSiNo(mod.isTaxRefund()));  	// Inscrito en el Registro de devolución mensual
		ide.setRPE(getSiNo(mod.isSpecialRegime())); // Régimen especial pequeño empresario o profesional
		
		DATOSPERSONALES dp = new DATOSPERSONALES();
		dp.setNIF(mod.getDocument());                          // Nif de la persona
		dp.setNRS(changeCharacters(mod.getName()));        	   // Nombre o razón social
		dp.setSVP(changeCharacters(mod.getStreetInitial()));   // Siglas vía pública
		dp.setNVP(changeCharacters(mod.getStreetName()));      // Nombre de la vía pública
		dp.setNPK(changeCharacters(mod.getStreetNumber()));    // Número de edificio/pto kilométrico
		dp.setESC(changeCharacters(mod.getStreetStair()));     // Escalera
		dp.setPIS(changeCharacters(mod.getStreetFloor()));     // Piso
		dp.setPUE(changeCharacters(mod.getStreetDoor()));      // Puerta
		dp.setLOC(changeCharacters(mod.getTown()));            // Localidad
		dp.setTEL(mod.getContactPhone());       	           // Teléfono
//		dp.setMOV(mod.getContactCellular());                   // Teléfono móvil
//		dp.setEMA(mod.getContactEmail());                      // Email
		dp.setPOP(AonStringUtils.leftPad(mod.getProvinceCode(), 2, '0')); // Código de provincia
		dp.setCMU(mod.getTownCode());                          // Código de municipio
		dp.setCP(mod.getZip());                                // Código Postal
		ide.setOTP(dp);
		
		return ide;
	}
	
	private static TDATOSESTADISTICOS425 getEst(Mod4252025 mod) {
      	
		TDATOSESTADISTICOS425 est = new TDATOSESTADISTICOS425();
		est.setDTP(getSiNo(mod.isMod415())); 				// ¿Está obligado a presentar el modelo 415 por realizar operaciones con terceras personas por importe superior a 3.005,06 euros?
		est.setRECC(getSiNo(mod.isAccrualRegimeTarget())); 	// ¿Ha sido destinatario de operaciones a las que se aplica el régimen especial de criterio de caja?
		
		addActEst(est.getACT(), mod); 	// Datos estadísticos
		
		// Declaración de sujeto pasivo incluido en autoliquidaciones conjuntas
		if (AonStringUtils.isNotBlank(mod.getMergedDeclarationDocument()) && AonStringUtils.isNotBlank(mod.getMergedDeclarationName())) {
			TDATOSPERSONALES dp = new TDATOSPERSONALES();
			dp.setNIF(mod.getMergedDeclarationDocument());
			dp.setNRS(changeCharacters(mod.getMergedDeclarationName()));
			est.setPER(dp);
		}	
		
		return est;
	}
	
	// Datos estadísticos
	private static void addActEst(List<OPERACIONDATOSESTADISTICOS> actEst, Mod4252025 mod) {
		
		addActEstRow(actEst, mod.getMainActivity()); // Principal
		addActEstRow(actEst, mod.getActivity1()); 	 // Otras 1
		addActEstRow(actEst, mod.getActivity2()); 	 // Otras 2
		addActEstRow(actEst, mod.getActivity3()); 	 // Otras 3
		addActEstRow(actEst, mod.getActivity4()); 	 // Otras 4
		addActEstRow(actEst, mod.getActivity5()); 	 // Otras 5
		
	}

	// Añade una fila a datos estadísticos, si contiene datos
	private static void addActEstRow(List<OPERACIONDATOSESTADISTICOS> actEst, Activity425 act) {
		
		if (act != null && AonStringUtils.isNotBlank(act.getKey()) && AonStringUtils.isNotBlank(act.getEpigraph())) {
			OPERACIONDATOSESTADISTICOS ope = new OPERACIONDATOSESTADISTICOS();
			ope.setCLA(act.getKey());
			ope.setEPI(act.getEpigraph());
			ope.setREG(act.getRegime());
			ope.setPRO(getAmount(act.getProvisionalProrate()));
			ope.setDEF(getAmount(act.getFinalProrate()));
			ope.setESP(act.isSpecialProrate() ? "X" : "");
			actEst.add(ope);
		}
		
	}
	
	// Añadir Representante persona física
	private static void addRepPF(List<TREPRESENTANTE> repList, Address address) {
		
		if (address != null && AonStringUtils.isNotBlank(address.getRdocument()) && AonStringUtils.isNotBlank(address.getRname())) {
			
			TREPRESENTANTE rep = new TREPRESENTANTE();
			
			rep.setTIP("PF"); // Tipo de representante: PF - Persona física
			
			TDATOSPERSONALES dp = new TDATOSPERSONALES();
			dp.setNIF(address.getRdocument());                // Nif de la persona
			dp.setNRS(changeCharacters(address.getRname()));  // Nombre o razón social
			
			TDIRECCION dir = new TDIRECCION();
			dir.setSVP(changeCharacters(address.getRstreetType()));      // Siglas vía pública
			dir.setNVP(changeCharacters(address.getRstreetName()));      // Nombre de la vía pública
			dir.setNPK(changeCharacters(address.getRstreetNumber()));    // Número de edificio/pto kilométrico
			dir.setESC(changeCharacters(address.getRstreetStair()));     // Escalera
			dir.setPIS(changeCharacters(address.getRstreetFloor()));     // Piso
			dir.setPUE(changeCharacters(address.getRstreetDoor()));      // Puerta
			dir.setLOC(changeCharacters(address.getRtown()));            // Localidad
			dir.setPOP(AonFiscalFileUtils.unsigned(address.getRprovince(), 2)); // Código de provincia
//			dir.setCMU(mod.getAddress().getRtownCode());                        // Código de municipio // FALTA - NO LO TENGO EN ESTE MOMENTO, VER SI ES NECESARIO	
			dir.setCP(address.getRzip());                                // Código Postal
			dir.setTEL(address.getRphone());       	                     // Teléfono  )
			
			TPERSONA per = new TPERSONA();
			per.setPER(dp);
			per.setDIR(dir);
			
			rep.setOTP(per);
			
			repList.add(rep);
		}
	
	}
	
	// Añadir Representante persona jurídica
	private static void addRepPJ(List<TREPRESENTANTE> repList, LegalRepresentative legalRepr) {
		
		if (legalRepr != null && AonStringUtils.isNotBlank(legalRepr.getDocument()) && AonStringUtils.isNotBlank(legalRepr.getName())) {
			
			TREPRESENTANTE rep = new TREPRESENTANTE();
			
			rep.setTIP("PJ"); // Tipo de representante: PJ - Persona jurídica
			rep.setNOT(legalRepr.getNotary());
			rep.setFPO(legalRepr.getNotaryDate() == null ? null : DATE_FORMAT.format(legalRepr.getNotaryDate()));
			
			TDATOSPERSONALES dp = new TDATOSPERSONALES();
			dp.setNIF(legalRepr.getDocument());                // Nif de la persona
			dp.setNRS(changeCharacters(legalRepr.getName()));  // Nombre o razón social
			
			TPERSONA per = new TPERSONA();
			per.setPER(dp);
			
			rep.setOTP(per);
			
			repList.add(rep);
		}
	
	}
	
	// Régimen general
	private static TREGIMENGENERAL425 getReg(Mod4252025 mod) {
		
		TREGIMENGENERAL425 reg = new TREGIMENGENERAL425();
		
		// Devengado
		
		TDEVENGADO425 dev = new TDEVENGADO425();
		
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
		
		dev.setREA(getBaseCuota3col(mod, Mod4252025DetailKey.C069));
		dev.setMBC(getBaseCuota2col(mod, Mod4252025DetailKey.C071));
		dev.setMCA(getBaseCuota2col(mod, Mod4252025DetailKey.C073));
		dev.setOIN(getBaseCuota2col(mod, Mod4252025DetailKey.C076));
		dev.setCRV(getBaseCuota2col(mod, Mod4252025DetailKey.C078));
		
		// Total bases imponibles
		dev.setTBA(getAmount(mod.ensure(Mod4252025DetailKey.C074).getTaxableBase()));
		
		// Total cuotas devengadas
		dev.setTCU(getAmount(mod.ensure(Mod4252025DetailKey.C079).getQuota()));
		
		// Asignar devengado al régimen general
		reg.setDEV(dev);
		
		// Deducible
		
		TDEDUCIBLE ded = new TDEDUCIBLE();
		ded.setOIC(getBaseCuota2col(mod, Mod4252025DetailKey.C081));
		ded.setOII(getBaseCuota2col(mod, Mod4252025DetailKey.C083));
		ded.setIMC(getBaseCuota2col(mod, Mod4252025DetailKey.C085));
		ded.setIMI(getBaseCuota2col(mod, Mod4252025DetailKey.C087));
		ded.setRED(getBaseCuota2col(mod, Mod4252025DetailKey.C089));
		ded.setCRA(getBaseCuota1col(mod, Mod4252025DetailKey.C090));
		ded.setRBI(getBaseCuota1col(mod, Mod4252025DetailKey.C091));
		ded.setRIA(getBaseCuota1col(mod, Mod4252025DetailKey.C092));
		ded.setRPP(getBaseCuota1col(mod, Mod4252025DetailKey.C093));
		ded.setTOT(getAmount(mod.ensure(Mod4252025DetailKey.C094).getQuota()));
		reg.setDED(ded);
		
		// Resultado del régimen general
		reg.setRES(getAmount(mod.ensure(Mod4252025DetailKey.C095).getQuota()));
		
		return reg;
		
	}
	
	// Régimen simplificado
	private static TREGIMENSIMPLIFICADO425 getRes(Mod4252025 mod) {
		
		TREGIMENSIMPLIFICADO425 res = new TREGIMENSIMPLIFICADO425();
		addRS(res.getMOD(), mod.getSimpRegime1()); // Módulos del régimen simplificado (1)
		addRS(res.getMOD(), mod.getSimpRegime2()); // Módulos del régimen simplificado (2)
		res.setCRS(getCrs(mod));                   // Cuotas del régimen simplificado
		res.setRES(getAmount(mod.getBox111()));    // Resultado del régimen simplificado
		return res;
		
	}

	// Módulos del régimen simplificado
	private static void addRS(List<TMODULO> modList, SimpliedRegimeActivity425 simpRegime) {
		
		if (simpRegime != null && AonStringUtils.isNotBlank(simpRegime.getEpigrafe())) {
			TMODULO module = new TMODULO();
			module.setEPI(simpRegime.getEpigrafe());          // Código del epígrafe
			//module.setSEC(simpRegime1.getSection());        // Sección del epígrafe // FALTA - VER SI ES NECESARIO
			module.setMOD1(getAmount(simpRegime.getUnit1())); // Módulo 1
			module.setMOD2(getAmount(simpRegime.getUnit2())); // Módulo 2
			module.setMOD3(getAmount(simpRegime.getUnit3())); // Módulo 3
			module.setMOD4(getAmount(simpRegime.getUnit4())); // Módulo 4
			module.setMOD5(getAmount(simpRegime.getUnit5())); // Módulo 5
			module.setMOD6(getAmount(simpRegime.getUnit6())); // Módulo 6
			module.setMOD7(getAmount(simpRegime.getUnit7())); // Módulo 7
			module.setTOT(getAmount(simpRegime.getBoxA()));   // Total
			module.setCASA(getAmount(simpRegime.getBoxA()));  // Cuota anual devengada por operaciones corrientes
			module.setCASB(getAmount(simpRegime.getBoxB()));  // Cuotas soportadas por operaciones corrientes
			module.setCASC(getAmount(simpRegime.getBoxC()));  // Índice corrector
			module.setCASD(getAmount(simpRegime.getBoxD()));  // Diferencia
			module.setCASE(getAmount(simpRegime.getBoxE()));  // Porcentaje cuota mínima operaciones corrientes
			module.setCASF(getAmount(simpRegime.getBoxF()));  // Cuota mínima
			module.setCASG(getAmount(simpRegime.getBoxG()));  // Cuota anual derivada de régimen simplificado
			modList.add(module);
		}
		
	}
	
	// Cuotas del régimen simplificado
	private static TSIMPLIFICADO getCrs(Mod4252025 mod) {

		TSIMPLIFICADO crs = new TSIMPLIFICADO();
		
		crs.setTRS(getAmount(mod.getBox103())); // Total cuota anual derivada del régimen simplificado
		crs.setCAF(getAmount(mod.getBox104())); // Cuotas devengadas por entregas o transmisiones de activos fijos o por inversión del sujeto pasivo
		crs.setCBI(getAmount(mod.getBox105())); // Cuotas devengadas por arrendamientos de bienes inmuebles
		crs.setRCU(getAmount(mod.getBox106())); // Rectificación de cuotas impositivas repercutidas
		crs.setTOT(getAmount(mod.getBox107())); // Total cuotas
		
		crs.setDAF(getAmount(mod.getBox108())); // Cuotas deducibles por adquisición o importación de activos fijos
		crs.setDBI(getAmount(mod.getBox109())); // Cuotas deducibles por arrendamientos de bienes inmuebles
		crs.setTOD(getAmount(mod.getBox110())); // Total cuotas deducibles 
		
		return crs;
	}

	// Añade una fila al IGIC devengado (base, tipo, cuota) 
	private static void addRow(List<TBASECUOTA> list, Mod4252025 mod, Mod4252025DetailKey... detailKeys) {
		for (Mod4252025DetailKey detailKey : detailKeys) {
			TBASECUOTA baseCuota = getBaseCuota3col(mod, detailKey);  
			if (baseCuota != null) {
				list.add(baseCuota);
			}		
		}
	}
	
	private static TBASECUOTA getBaseCuota3col(Mod4252025 mod, Mod4252025DetailKey detailKey) {
		Mod425Detail detail = mod.ensure(detailKey);			
		if (detail.getTaxableBase() != 0.0 || detail.getQuota() != 0.0) {
			return getBaseCuota(detail.getTaxableBase(), detail.getPercent(), detail.getQuota());
		} else {		
			return null;
		}
	}
	
	private static TBASECUOTA getBaseCuota2col(Mod4252025 mod, Mod4252025DetailKey detailKey) {
		Mod425Detail detail = mod.ensure(detailKey);			
		if (detail.getTaxableBase() != 0.0 || detail.getQuota() != 0.0) {
			return getBaseCuota(detail.getTaxableBase(), detail.getQuota());
		} else {		
			return null;
		}
	}
	
	private static TBASECUOTA getBaseCuota1col(Mod4252025 mod, Mod4252025DetailKey detailKey) {
		Mod425Detail detail = mod.ensure(detailKey);			
		if (detail.getQuota() != 0.0) {
			return getBaseCuota(detail.getQuota());
		} else {		
			return null;
		}
	}

	// Resultado de la liquidación anual
	private static TLIQUIDACION getLiq(Mod4252025 mod) {
		
		TLIQUIDACION liq = new TLIQUIDACION();
		liq.setRCU(getAmount(mod.getBox112())); // Regularización cuotas artículo 22.8.5ª Ley 20/1991
		liq.setSUT(getAmount(mod.getBox113())); // Suma de resultados
		liq.setCPA(getAmount(mod.getBox114())); // Cuota de IGIC a compensar de períodos anteriores
		liq.setRLI(getAmount(mod.getBox115())); // Resultado de la liquidación anual
		return liq;
		
	}
	
	// Resultado de las autoliquidaciones
	private static TAUTOLIQUIDACION425 getAut(Mod4252025 mod) {
		
		TAUTOLIQUIDACION425 aut = new TAUTOLIQUIDACION425();
		aut.setTOA(getAmount(mod.getBox116())); // Total de ingresos realizados en las autoliquidaciones por IGIC del ejercicio
		aut.setTOM(getAmount(mod.getBox117())); // Total devoluciones mensuales por IGIC a sujetos pasivos inscritos en el registro de devolución mensual
		aut.setIMC(getAmount(mod.getBox118())); // Importe cuando el resultado de la autoliquidación última del año es a compensar
		aut.setIMD(getAmount(mod.getBox119())); // Importe cuando el resultado de la autoliquidación última del año es a devolver
		return aut;
		
	}
	
	// Operaciones especifícas
	private static TOPERACIONESEJERCICIO getOpe(Mod4252025 mod) {
		
		TOPERACIONESEJERCICIO ope = new TOPERACIONESEJERCICIO();
		ope.setREG(getAmount(mod.getBox120()));  // Operaciones en régimen general
		ope.setREC(getAmount(mod.getBox121()));  // Operaciones en las que habiéndoles sido aplicado el régimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el artículo 18 Ley 20/1991
		ope.setEXP(getAmount(mod.getBox122()));  // Exportaciones definitivas y operaciones asimiladas a la exportación
		ope.setEXE(getAmount(mod.getBox123()));  // Operaciones relativas a áreas exentas
		ope.setOIE(getAmount(mod.getBox124()));  // Operaciones interiores exentas por el artículo 25 de la Ley 19/1994 realizadas por el sujeto pasivo.
		ope.setECD(getAmount(mod.getBox125()));  // Otras operaciones exentas con derecho a deducción
		ope.setESD(getAmount(mod.getBox126()));  // Operaciones exentas sin derecho a deducción
		ope.setRES(getAmount(mod.getBox127()));  // Operaciones en régimen simplificado
		ope.setNSJ(getAmount(mod.getBox128()));  // Operaciones no sujetas por reglas de localización o con inversión del sujeto pasivo.
		ope.setREAG(getAmount(mod.getBox129())); // Operaciones en régimen especial de la agricultura, ganadería y pesca.
		ope.setREBU(getAmount(mod.getBox130())); // Operaciones en régimen especial de bienes usados, objetos de arte, antigüedades o colección.
		ope.setREAV(getAmount(mod.getBox131())); // Operaciones en régimen especial de Agencia de Viajes.
		ope.setEBI(getAmount(mod.getBox132()));  // Entregas de bienes inmuebles y operaciones financieras no habituales.
		ope.setEBT(getAmount(mod.getBox133()));  // Entregas de bienes de inversión para el transmitente.
		ope.setTOT(getAmount(mod.getBox134()));  // Total volumen operaciones
		ope.setIBE(getAmount(mod.getBox135()));  // Importaciones de bienes de inversión exentos por el artículo 25 de la Ley 19/1994
		ope.setCND(getAmount(mod.getBox136()));  // Cuotas de I.G.I.C. soportado no deducible
		ope.setODD(getAmount(mod.getBox137()));  // Otras operaciones no sujetas con derecho a deducción (artículo 29.4.1ªg) Ley 20/1991)
		return ope;
		
	}
	
	//	Régimen especial del criterio de caja
	private static TOPERACIONESRECC getRcc(Mod4252025 mod) {
		
		TOPERACIONESRECC rcc = new TOPERACIONESRECC();
		rcc.setIEB(getBaseCuota(mod.getBox138(), mod.getBox139())); // Importes de las entregas de bienes y prestaciones de servicios a las que habiéndoles sido aplicado el RECC, hubieran resultado devengadas conforme a la regla general de devengo contenida en el artículo 18 de	la Ley 20/1991
		rcc.setIAB(getBaseCuota(mod.getBox140(), mod.getBox141())); // Importes de las adquisiciones de bienes y prestaciones de servicios a las que  sea de aplicacion o afecte el RECC conforme a la regla general de devengo contenida en el artículo 18 de la Ley 20/1991
		return rcc;
		
	}

	// Régimen especial del pequeño empresario o profesional
	private static TOPERACIONESREPEP getRpe(Mod4252025 mod) {
		
		TOPERACIONESREPEP rpe = new TOPERACIONESREPEP();
		rpe.setEPE(getAmount(mod.getBox142())); // Importe de operaciones habituales u ocasionales sujetas al IGIC exentas por Régimen especial del pequeño empresario o profesional
		rpe.setECM(getAmount(mod.getBox143())); // Importe de operaciones sujetas al IGIC exentas por Régimen especial del comerciante minorista
		rpe.setONS(getAmount(mod.getBox144())); // Importe de entrega de bienes y prestaciones de servicios no sujetas al IGIC imputables a la sede de la actividad	económica situada en Canarias
		rpe.setOFC(getAmount(mod.getBox145())); // Importe de entregas de bienes y prestaciones de servicios no sujetas al IGIC imputables a otras sedes o establecimientos situados fuera de Canarias
		rpe.setIST(getAmount(mod.getBox146())); // Importe en el supuesto de transmisión de la totalidad o parte del patrimonio empresarial o profesional
		rpe.setTOT(getAmount(mod.getBox147())); // Total volumen de operaciones en el REPEP
		return rpe;
		
	}
	
	// --- METODOS AUXILIARES ---------------------------------------------------------------------------------------------------
	
	private static SINOType getSiNo(boolean value) {
		return value ? SINOType.S : SINOType.N; 
	}
	
	private static TBASECUOTA getBaseCuota(double quota) {
		return getBaseCuota(null, null, quota);
	}
	private static TBASECUOTA getBaseCuota(double base, double quota) {
		return getBaseCuota(base, null, quota);
	}
	private static TBASECUOTA getBaseCuota(Double base, Double percent, double quota) {
		TBASECUOTA baseQuota = new TBASECUOTA();
		baseQuota.setCUO(getAmount(quota));
		if (percent != null)
			baseQuota.setTIP(getAmount(percent));
		if (base != null)
			baseQuota.setBAS(getAmount(base));
		return baseQuota;
	}
	
	private static String getAmount(double amount) {
		return AonFiscalFileUtils.signed(amount, ' ', '-', 4).trim();
	}
	
	// FALTA - VER SI ES NECESARIO, PUES SE GUARDARAN ASI EN LA BASE DE DATOS
	// CAMBIAR CARACTERES NO PERMITIDOS (ACENTOS, &, ', ETC.) Y PONER EN MAYUSCULAS
	private static String changeCharacters(String fileString) {
		fileString = AonStringUtils.trimToEmpty(fileString);
		fileString = AonStringUtils.upperCase(fileString);
//		fileString = fileString.replace("'", " ");
//		fileString = fileString.replace("&", "Y");	
//		fileString = fileString.replace("Á", "A");
//		fileString = fileString.replace("É", "E");
//		fileString = fileString.replace("Í", "I");
//		fileString = fileString.replace("Ó", "O");
//		fileString = fileString.replace("Ú", "U");
//		fileString = fileString.replace("Ü", "U");		
		return fileString;
	}

}
