package com.esferalia.aon.occam.impl.jooq.dao.mod417_2026;

import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod417ToDEC2026 {
	
	private static final Mod303Key[] IGIC_DEVENGADO_KEYS = {
           Mod303Key.CA_DB01, Mod303Key.CA_DC01
		 , Mod303Key.CA_DB02, Mod303Key.CA_DC02
		 , Mod303Key.CA_DB03, Mod303Key.CA_DC03
		 , Mod303Key.CA_DB04, Mod303Key.CA_DC04
		 , Mod303Key.CA_DB05, Mod303Key.CA_DC05
		 , Mod303Key.CA_DB06, Mod303Key.CA_DC06
		 , Mod303Key.CA_DB07, Mod303Key.CA_DC07
		 , Mod303Key.CA_DB08, Mod303Key.CA_DC08
		 , Mod303Key.CA_C019, Mod303Key.CA_C020
		 , Mod303Key.CA_C021, Mod303Key.CA_C022
//		 , Mod303Key.CA_C023, Mod303Key.CA_C024
	};
	
	private static final Mod303Key[] IGIC_DEDUCIBLE_KEYS = {
		   Mod303Key.CA_C026, Mod303Key.CA_C027				
		 , Mod303Key.CA_C028, Mod303Key.CA_C029
		 , Mod303Key.CA_C030, Mod303Key.CA_C031
		 , Mod303Key.CA_C032, Mod303Key.CA_C033
		 , Mod303Key.CA_C034, Mod303Key.CA_C035    
		 , Mod303Key.CA_C036
		 , Mod303Key.CA_C037
		 , Mod303Key.CA_C038
		 , Mod303Key.CA_C039
	};
	
	private Mod417ToDEC2026() {
	}
	
	public static String getDeclaration(Mod303 mod303) throws JAXBException {
		System.out.println("Mod417ToDEC2026 getDeclaration");
		DEC dec = Mod417ToDEC2026.getDEC(mod303);
		StringWriter writer = new StringWriter();
		JAXBContext context = JAXBContext.newInstance(DEC.class);
		Marshaller um = context.createMarshaller();
		um.setProperty("jaxb.encoding", "ISO-8859-1");
		um.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		um.marshal(dec, writer);
		return writer.toString();		
	}

	private static DEC getDEC(Mod303 mod) {
		DEC dec = new DEC();
		
		// Identificador del modelo
		dec.setMOD("417");                                  
		
		dec.setANY(AonNumberUtils.toString(mod.getYear())); // Ejercicio al que se refiere la autoliquidación
		dec.setPER(mod.getPeriod().getName());              // Período al que se refiere la autoliquidación: 1T, 2T, 3T, 4T
		
		if (mod.isComplementary()) {
			dec.setCOM("X");                     // Se establece con "X" cuando esta autoliquidación sea complementaria
			dec.setNJA(mod.getReplacedNumber()); // Para una declaración complementaria se declara el número anterior de justificante
		}
		
		dec.setIDE(getIde(mod)); // Datos identificativos y opciones tributarias 
		
		if (!mod.isWithoutActivity()) {
			dec.setIGIDEV(getIgiDev(mod));	// IGIC devengado
			dec.setIGIDED(getIgiDed(mod));  // IGIC deducible
			dec.setLIQ(getLiq(mod));	    // Liquidación
		}
		
		dec.setRES(getRes(mod));  // Resultado de la declaración
		
		if (!mod.isWithoutActivity()) {
			dec.setADI(getAdi(mod));  // Información adicional
			dec.setRCC(getRcc(mod));  // Datos exclusivos para sujetos acogidos al régimen especial de criterio de caja y/o destinatarios de operaciones afectadas por el mismo.
			
			// Ultimo periodo Exonerados Resumen Anual
			if (mod.isLastPeriod()) {				
				addActEst(dec.getACTEST(), mod); 	// Datos estadísticos
				dec.setOPE(getOpe(mod)); 			// Operaciones realizadas en el ejercicio
				addActPro(dec.getACTPRO(), mod); 	// Prorrata
				addActDed(dec.getACTDED(), mod); 	// Actividades con regímenes de deducción diferenciados
			}
		}
		
		return dec;
		
	}

	//	Datos identificativos y opciones tributarias.
    //	Modelo 417: RDM, RECC, DRECC, EOP, ACR, FAC y TAC.
	private static TIDENTIGIC getIde(Mod303 mod) {

		TIDENTIGIC ide = new TIDENTIGIC();
		
		ide.setRDM(getSiNo(mod, Mod303Key.CM_002));  	// Inscrito en el Registro de devolución mensual
		ide.setRECC(getSiNo(mod, Mod303Key.CA_X02));  	// Opta por Régimen Especial Criterio Caja 
		ide.setDRECC(getSiNo(mod, Mod303Key.CA_X03));  	// Destinatario de operaciones a las que se aplique el Régimen Especial Criterio Caja
		ide.setEOP(getSiNo(mod, Mod303Key.CA_X04));  	// Entidad no establecida con obligaciones periódicas
		ide.setACR(getSiNo(mod, Mod303Key.CA_X05));  	// Declarado en concurso de acreedores en el período
		
		if (mod.getCheck(Mod303Key.CA_X05)) {
			if (mod.getDescription(Mod303Key.CA_X06) != null)
				ide.setFAC(mod.getDescription(Mod303Key.CA_X06));  				// Fecha en que se dictó el auto de declaración de concurso (se guarda en formato dd/mm/aaaa)
			if (mod.getAmount(Mod303Key.CA_X07) != 0)
				ide.setTAC(mod.getAmount(Mod303Key.CA_X07) == 1 ? "PR" : "PT"); // Tipo de autoliquidación si se encuentra en concurso
		}
		
		DATOSPERSONALES dp = new DATOSPERSONALES();
		dp.setNIF(mod.getDocument());                          // Nif de la persona
		dp.setNRS(changeCharacters(mod.getFullName()));        // Nombre o razón social
		dp.setSVP(changeCharacters(mod.getStreetInitial()));   // Siglas vía pública
		dp.setNVP(changeCharacters(mod.getStreetName()));      // Nombre de la vía pública
		dp.setNPK(changeCharacters(mod.getStreetNumber()));    // Número de edificio/pto kilométrico
		dp.setESC(changeCharacters(mod.getStreetStair()));     // Escalera
		dp.setPIS(changeCharacters(mod.getStreetFloor()));     // Piso
		dp.setPUE(changeCharacters(mod.getStreetDoor()));      // Puerta
		dp.setLOC(changeCharacters(mod.getTown()));            // Localidad
		dp.setTEL(mod.getPhone());       	                   // Teléfono
		dp.setMOV(mod.getContactCellular());                   // Teléfono móvil
		dp.setEMA(mod.getContactEmail());                      // Email
		dp.setPOP(AonFiscalFileUtils.unsigned(Province.getByName(mod.getProvince()).ordinal(), 2)); // Código de provincia
		dp.setCMU(mod.getTownCode());                          // Código de municipio
		dp.setCP(mod.getZip());                                // Código Postal
		ide.setOTP(dp);
		
		return ide;
	}

	// IGIC devengado
	private static TDEVENGADO getIgiDev(Mod303 mod) {
		
		TDEVENGADO igicDev = new TDEVENGADO();
		
		// Bases, tipos y cuotas devengadas
		addRow(igicDev.getDEV(), mod, Mod303Key.CA_DB01,Mod303Key.CA_DT01,Mod303Key.CA_DC01);
		addRow(igicDev.getDEV(), mod, Mod303Key.CA_DB02,Mod303Key.CA_DT02,Mod303Key.CA_DC02);
		addRow(igicDev.getDEV(), mod, Mod303Key.CA_DB03,Mod303Key.CA_DT03,Mod303Key.CA_DC03);
		addRow(igicDev.getDEV(), mod, Mod303Key.CA_DB04,Mod303Key.CA_DT04,Mod303Key.CA_DC04);
		addRow(igicDev.getDEV(), mod, Mod303Key.CA_DB05,Mod303Key.CA_DT05,Mod303Key.CA_DC05);
		addRow(igicDev.getDEV(), mod, Mod303Key.CA_DB06,Mod303Key.CA_DT06,Mod303Key.CA_DC06);
		addRow(igicDev.getDEV(), mod, Mod303Key.CA_DB07,Mod303Key.CA_DT07,Mod303Key.CA_DC07);
		addRow(igicDev.getDEV(), mod, Mod303Key.CA_DB08,Mod303Key.CA_DT08,Mod303Key.CA_DC08);
		
		igicDev.setOIN(getBaseCuota(mod, Mod303Key.CA_C019, Mod303Key.CA_C020)); // Operaciones con inversión del sujeto pasivo
		igicDev.setMBC(getBaseCuota(mod, Mod303Key.CA_C021, Mod303Key.CA_C022)); // Modificación bases y cuotas
//		igicDev.setCRV(getBaseCuota(mod, Mod303Key.CA_C023, Mod303Key.CA_C024)); // Cuotas devueltas en Régimen de viajeros
		
		// Total cuotas devengadas (solo si hay algo en las casillas del IVA devengado)
		if (isNotZero(mod, IGIC_DEVENGADO_KEYS)) {
			igicDev.setTOT(getAmount(mod, Mod303Key.CA_C025)); 	
		}
		
		return igicDev;
	}
	
	// Añade una fila al IVA devengado (solo si tiene base o cuota) 
	private static void addRow(List<TBASECUOTA> list, Mod303 mod, Mod303Key baseKey, Mod303Key percentKey, Mod303Key quotaKey) {
		if (mod.getAmount(baseKey) != 0.0 || mod.getAmount(quotaKey) != 0.0) {
			list.add(getBaseCuota(mod, baseKey, percentKey, quotaKey));
		}		
	}

	// IGIC deducible
	private static TDEDUCIBLE getIgiDed(Mod303 mod) {

		TDEDUCIBLE igicDed = new TDEDUCIBLE();
	
		igicDed.setOIC(getBaseCuota(mod, Mod303Key.CA_C026, Mod303Key.CA_C027));  // IGIC deducible en operaciones interiores con bienes corrientes
		igicDed.setOII(getBaseCuota(mod, Mod303Key.CA_C028, Mod303Key.CA_C029));  // IGIC deducible en operaciones interiores con bienes de inversión
		igicDed.setIMC(getBaseCuota(mod, Mod303Key.CA_C030, Mod303Key.CA_C031));  // IGIC deducible por importación de bienes corrientes
		igicDed.setIMI(getBaseCuota(mod, Mod303Key.CA_C032, Mod303Key.CA_C033));  // IGIC deducible por importación de bienes de inversión
		igicDed.setRED(getBaseCuota(mod, Mod303Key.CA_C034, Mod303Key.CA_C035));  // Rectificación de deducciones
		igicDed.setCRA(getBaseCuota(mod, Mod303Key.CA_C036));                     // Compensaciones en régimen especial de la agricultura, ganadería y pesca
		igicDed.setRBI(getBaseCuota(mod, Mod303Key.CA_C037));                     // Regularización de cuotas soportadas por bienes de inversión
		igicDed.setRIA(getBaseCuota(mod, Mod303Key.CA_C038));                     // Regularización de cuotas soportadas antes del inicio de la actividad
		igicDed.setRPP(getBaseCuota(mod, Mod303Key.CA_C039));                     // Regularización por aplicación del porcentaje definitivo de prorrata

		// Total cuotas deducibles (solo si hay algo en las casillas del IVA deducible)
		if (isNotZero(mod, IGIC_DEDUCIBLE_KEYS)) {
			igicDed.setTOT(getAmount(mod,Mod303Key.CA_C040));  	
		}
		
		return igicDed;
		
	}
	
	// Liquidacion
	private static TLIQUIDACION getLiq(Mod303 mod) {
		
		TLIQUIDACION liq = new TLIQUIDACION();
		
		// Diferencia (solo si hay algo en las casillas del IVA devengado o del IVA deducible)
		if (isNotZero(mod, IGIC_DEVENGADO_KEYS) || isNotZero(mod, IGIC_DEDUCIBLE_KEYS)) {			
			liq.setDIF(getAmount(mod, Mod303Key.CA_C041)); 
		}
		
		liq.setRCU(getAmount(mod, Mod303Key.CA_C042)); // Regularización cuotas artículo 22.8.5ª Ley 20/1991
		liq.setCPA(getAmount(mod, Mod303Key.CA_C043)); // Cuota de IGIC a compensar de períodos anteriores
		liq.setDAC(getAmount(mod, Mod303Key.CA_C044)); // A deducir (exclusivamente en caso de autoliquidación complementaria)
		liq.setRLI(getAmount(mod, Mod303Key.CA_C045)); // Resultado de la autoliquidación

		return liq;
	}
	
	// Resultado de la declaración
	private static RESULTADOLIQUIDACION getRes(Mod303 mod) {
		
		RESULTADOLIQUIDACION res = new RESULTADOLIQUIDACION();
		
		// Tipo de resultado de la liquidación
		//  	I = Ingreso
		//  	D = Devolución
		//  	C = A Compensar,
		//  	S = Sin Actividad
		// En caso de resultado de la liquidación 0, cuando
		// se ha producido actividad en el período, el tipo
		// de resultado será "C"		
		String resultType = "";
		if (mod.isWithoutActivity()) {
			resultType = "S"; // Sin actividad 
		} else if (mod.getDeclarationResult() == 0 || mod.getDeclarationResultType() == FiscalModelDeclarationType.COMPENSATE) {
			resultType = "C"; // Resultado cero o A compensar
		} else if (mod.isToDeposit()) {
			resultType = "I"; // Ingreso
		} else {
			resultType = "D"; // Devolución
		}
		res.setTIP(resultType); // Tipo de resultado de la liquidación
		
		// Importe a Ingresar, Compensar o Devolver
		if (!mod.isWithoutActivity()) {
			res.setIMP(AonFiscalFileUtils.unsigned(mod.getDeclarationResult(),3));	
		}
		
		// SI LE PONGO FORMA DE PAGO 2 ADEUDO EN CUENTA EN INGRESO, ME DA EL SIGUIENTE ERROR (AUNQUE LE PONGA IBAN), POR ESO LE PONGO PAGO TELEMATICO:
		// [forma de pago] erróneo (incoherente con [opción de presentación elegida en los parámetros de entrada])
		
		// Forma de pago ingresos
		// 1 - Efectivo
		// 2 - Adeudo en cuenta 
		// 3 - Pago fraccionado
		// 4 - Domiciliación bancaria
		// 5 - Pago telemático
		// 6 - Aplazamiento 6 meses medidas Covid
		if (mod.getDeclarationResult() > 0) {
//			String paymentMethod = "2"; // Adeudo en cuenta
			String paymentMethod = "5"; // Pago telemático
			if (mod.getDeclarationResultType() == FiscalModelDeclarationType.BANK) {
				paymentMethod = "4"; // Domiciliación
			}
			res.setFPA(paymentMethod); // Forma de pago ingresos
		}
		
		// Código internacional de cuenta bancaria (IBAN) (Ingreso o Devolución)
		if (mod.getDeclarationResult() > 0 || mod.getDeclarationResultType() == FiscalModelDeclarationType.PAYBACK) {
			if (AonStringUtils.isNotEmpty(mod.getFinanceIban())) {
				res.setIBAN(mod.getFinanceIban()); 
			}
		}
		
		return res;
	}
	
	// Información adicional
	private static TINFOADICIONAL getAdi(Mod303 mod) {
		
		TINFOADICIONAL adi = new TINFOADICIONAL();
		
		adi.setEOA(getAmount(mod, Mod303Key.CA_C046)); // Exportaciones y otras operaciones exentas con derecho a deducción
		adi.setODD(getAmount(mod, Mod303Key.CA_C047)); // Operaciones no sujetas o con inversión del sujeto pasivo que originan el derecho a deducción

		return adi;
	}
	
	//	Datos exclusivos para sujetos acogidos al régimen especial de criterio de caja y/o destinatarios de operaciones afectadas por el mismo.
	private static TOPERACIONESRECC getRcc(Mod303 mod) {
		
		TOPERACIONESRECC rcc = new TOPERACIONESRECC();

		rcc.setIEB(getBaseCuota(mod, Mod303Key.CA_C048, Mod303Key.CA_C049)); // Importes de las entregas de bienes y prestaciones de servicios a las que habiéndoles sido aplicado el RECC, hubieran resultado devengadas conforme a la regla general de devengo contenida en el artículo 18 de	la Ley 20/1991
		rcc.setIAB(getBaseCuota(mod, Mod303Key.CA_C050, Mod303Key.CA_C051)); // Importes de las adquisiciones de bienes y prestaciones de servicios a las que  sea de aplicacion o afecte el RECC conforme a la regla general de devengo contenida en el artículo 18 de la Ley 20/1991

		return rcc;
	}

	// Datos estadísticos
	private static void addActEst(List<OPERACIONDATOSESTADISTICOS> actEst, Mod303 mod) {
		
		addActEstRow(actEst, mod, Mod303Key.CA_U1C, Mod303Key.CA_U1E, Mod303Key.CA_U1R); // Principal
		addActEstRow(actEst, mod, Mod303Key.CA_U2C, Mod303Key.CA_U2E, Mod303Key.CA_U2R); // Otras 1
		addActEstRow(actEst, mod, Mod303Key.CA_U3C, Mod303Key.CA_U3E, Mod303Key.CA_U3R); // Otras 2
		addActEstRow(actEst, mod, Mod303Key.CA_U4C, Mod303Key.CA_U4E, Mod303Key.CA_U4R); // Otras 3
		addActEstRow(actEst, mod, Mod303Key.CA_U5C, Mod303Key.CA_U5E, Mod303Key.CA_U5R); // Otras 4
		
	}

	// Añade una fila a datos estadísticos, si contiene datos
	private static void addActEstRow(List<OPERACIONDATOSESTADISTICOS> actEst, Mod303 mod, Mod303Key caUc, Mod303Key caUe, Mod303Key caUr) {
		
		if (isNotBlank(mod, caUc, caUe, caUr)) {
			OPERACIONDATOSESTADISTICOS ope = new OPERACIONDATOSESTADISTICOS();
			ope.setCLA(mod.getDescription(caUc));
			ope.setEPI(mod.getDescription(caUe));
			ope.setREG(mod.getDescription(caUr));
			actEst.add(ope);
		}
		
	}
	
	// Operaciones realizadas en el ejercicio
	private static TOPERACIONESEJERCICIO getOpe(Mod303 mod) {
		
		if (isNotZero(mod
				, Mod303Key.CA_C052
				, Mod303Key.CA_C053
				, Mod303Key.CA_C054
				, Mod303Key.CA_C055
				, Mod303Key.CA_C056
				, Mod303Key.CA_C057
				, Mod303Key.CA_C058
				, Mod303Key.CA_C059
				, Mod303Key.CA_C060
				, Mod303Key.CA_C061
				, Mod303Key.CA_C062
				, Mod303Key.CA_C063
				, Mod303Key.CA_C064
				, Mod303Key.CA_C065)) {
			TOPERACIONESEJERCICIO ope = new TOPERACIONESEJERCICIO();
			ope.setREG(getAmount(mod, Mod303Key.CA_C052));  // Operaciones en régimen general
			ope.setREC(getAmount(mod, Mod303Key.CA_C053));  // Operaciones en las que habiéndoles sido aplicado el régimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el artículo 18 Ley 20/1991
			ope.setEXP(getAmount(mod, Mod303Key.CA_C054));  // Exportaciones definitivas y operaciones asimiladas a la exportación
			ope.setEXE(getAmount(mod, Mod303Key.CA_C055));  // Operaciones relativas a áreas exentas
			ope.setOIE(getAmount(mod, Mod303Key.CA_C056));  // Operaciones interiores exentas por el artículo 25 de la Ley 19/1994 realizadas por el sujeto pasivo.
			ope.setECD(getAmount(mod, Mod303Key.CA_C057));  // Otras operaciones exentas con derecho a deducción
			ope.setESD(getAmount(mod, Mod303Key.CA_C058));  // Operaciones exentas sin derecho a deducción
			ope.setNSJ(getAmount(mod, Mod303Key.CA_C059));  // Operaciones no sujetas por reglas de localización o con inversión del sujeto pasivo.
			ope.setREAG(getAmount(mod, Mod303Key.CA_C060)); // Operaciones en régimen especial de la agricultura, ganadería y pesca.
			ope.setREBU(getAmount(mod, Mod303Key.CA_C061)); // Operaciones en régimen especial de bienes usados, objetos de arte, antigüedades o colección.
			ope.setREAV(getAmount(mod, Mod303Key.CA_C062)); // Operaciones en régimen especial de Agencia de Viajes.
			ope.setEBI(getAmount(mod, Mod303Key.CA_C063));  // Entregas de bienes inmuebles y operaciones financieras no habituales.
			ope.setEBT(getAmount(mod, Mod303Key.CA_C064));  // Entregas de bienes de inversión para el transmitente.
			ope.setTOT(getAmount(mod, Mod303Key.CA_C065));  // Total volumen operaciones
			return ope;
		}
		return null;
		
	}
	
	// Prorrata
	private static void addActPro(List<OPERACIONDATOSESTADISTICOS> actPro, Mod303 mod) {

		addActEstPro(actPro, mod, Mod303Key.CA_P1C, Mod303Key.CA_P1I, Mod303Key.CA_P1D, Mod303Key.CA_P1T, Mod303Key.CA_P1P);
		addActEstPro(actPro, mod, Mod303Key.CA_P2C, Mod303Key.CA_P2I, Mod303Key.CA_P2D, Mod303Key.CA_P2T, Mod303Key.CA_P2P);      
		addActEstPro(actPro, mod, Mod303Key.CA_P3C, Mod303Key.CA_P3I, Mod303Key.CA_P3D, Mod303Key.CA_P3T, Mod303Key.CA_P3P);      
		addActEstPro(actPro, mod, Mod303Key.CA_P4C, Mod303Key.CA_P4I, Mod303Key.CA_P4D, Mod303Key.CA_P4T, Mod303Key.CA_P4P);      
		addActEstPro(actPro, mod, Mod303Key.CA_P5C, Mod303Key.CA_P5I, Mod303Key.CA_P5D, Mod303Key.CA_P5T, Mod303Key.CA_P5P);
		
	}

	// Añade una fila a prorrata, si contiene datos
	private static void addActEstPro(List<OPERACIONDATOSESTADISTICOS> actPro, Mod303 mod, Mod303Key caPc, Mod303Key caPi, Mod303Key caPd, Mod303Key caPt, Mod303Key caPp) {

		if (isNotBlank(mod, caPc, caPt) && isNotZero(mod, caPi, caPd, caPp)) {
			OPERACIONDATOSESTADISTICOS ope = new OPERACIONDATOSESTADISTICOS();
			ope.setEPI(AonStringUtils.left(mod.getDescription(caPc),3)); // CNAE (Solo los tres primeros dígitos)
			ope.setIMP(getAmount(mod, caPi));     // Importe total operaciones
			ope.setIMD(getAmount(mod, caPd));     // Importe operaciones derecho deducción
			ope.setTIP(mod.getDescription(caPt)); // Tipo Prorrata. Modelo 417: G - General, E - Especial
			ope.setDEF(getAmount(mod, caPp));     // Porcentaje definitivo prorrata general. En el modelo 417, contiene el valor de prorrata.
			actPro.add(ope);
		}
		
	}
	
	// Actividades con regímenes de deducción diferenciados
	private static void addActDed(List<TDEDUCIBLE> actDed, Mod303 mod) {
		
		addActEstDed(actDed, mod, Mod303Key.CA_C200, Mod303Key.CA_C201 
								, Mod303Key.CA_C202, Mod303Key.CA_C203 
								, Mod303Key.CA_C204, Mod303Key.CA_C205 
								, Mod303Key.CA_C206, Mod303Key.CA_C207 
								, Mod303Key.CA_C208, Mod303Key.CA_C209 
								, Mod303Key.CA_C210, Mod303Key.CA_C211 
								, Mod303Key.CA_C212 
								, Mod303Key.CA_C213); // Grupo 1
		addActEstDed(actDed, mod, Mod303Key.CA_C214, Mod303Key.CA_C215
								, Mod303Key.CA_C216, Mod303Key.CA_C217
								, Mod303Key.CA_C218, Mod303Key.CA_C219                         
								, Mod303Key.CA_C220, Mod303Key.CA_C221                                           
								, Mod303Key.CA_C222, Mod303Key.CA_C223
								, Mod303Key.CA_C224, Mod303Key.CA_C225
								, Mod303Key.CA_C226
								, Mod303Key.CA_C227); // Grupo 2
		addActEstDed(actDed, mod, Mod303Key.CA_C228, Mod303Key.CA_C229
								, Mod303Key.CA_C230, Mod303Key.CA_C231
								, Mod303Key.CA_C232, Mod303Key.CA_C233                         
								, Mod303Key.CA_C234, Mod303Key.CA_C235                                           
								, Mod303Key.CA_C236, Mod303Key.CA_C237
								, Mod303Key.CA_C238, Mod303Key.CA_C239
								, Mod303Key.CA_C240
								, Mod303Key.CA_C241); // Grupo 3
		
	}
	
	// Añade una fila a actividades con regímenes de deducción diferenciados, si contiene datos
	private static void addActEstDed(List<TDEDUCIBLE> actDed, Mod303 mod, Mod303Key... keys) {
		
		if (isNotZero(mod, keys)) {
			TDEDUCIBLE ded = new TDEDUCIBLE();
			ded.setOIC(getBaseCuota(mod, keys[ 0], keys[ 1])); // IGIC deducible en operaciones interiores con bienes corrientes
			ded.setOII(getBaseCuota(mod, keys[ 2], keys[ 3])); // IGIC deducible en operaciones interiores con bienes de inversión
			ded.setIMC(getBaseCuota(mod, keys[ 4], keys[ 5])); // IGIC deducible por importación de bienes corrientes
			ded.setIMI(getBaseCuota(mod, keys[ 6], keys[ 7])); // IGIC deducible por importación de bienes de inversión
			ded.setCRA(getBaseCuota(mod, keys[ 8], keys[ 9])); // Compensaciones en régimen especial de la agricultura, ganadería y pesca.
			ded.setRED(getBaseCuota(mod, keys[10], keys[11])); // Rectificación de deducciones
			ded.setRBI(getBaseCuota(mod, keys[12]));		   // Regularización de cuotas soportadas por bienes de inversión.
			ded.setTOT(getAmount(mod, keys[13])); 			   // Total cuotas deducibles
			actDed.add(ded);
		}
		
	}
	
	// --- METODOS AUXILIARES ---------------------------------------------------------------------------------------------------
	
	private static SINOType getSiNo(Mod303 mod, Mod303Key key) {
		return mod.getCheck(key) ? SINOType.S : SINOType.N; 
	}
	
	private static TBASECUOTA getBaseCuota(Mod303 mod, Mod303Key quotaKey) {
		return getBaseCuota(mod, null, null, quotaKey);
	}
	private static TBASECUOTA getBaseCuota(Mod303 mod, Mod303Key baseKey, Mod303Key quotaKey) {
		return getBaseCuota(mod, baseKey, null, quotaKey);
	}
	private static TBASECUOTA getBaseCuota(Mod303 mod, Mod303Key baseKey, Mod303Key percentKey, Mod303Key quotaKey) {
		TBASECUOTA baseQuota = new TBASECUOTA();
		baseQuota.setCUO(getAmount(mod, quotaKey));
		if (percentKey != null)
			baseQuota.setTIP(getAmount(mod, percentKey));
		if (baseKey != null)
			baseQuota.setBAS(getAmount(mod, baseKey));
		return baseQuota;
	}
	
	private static String getAmount(Mod303 mod, Mod303Key key) {
		return AonFiscalFileUtils.signed(mod.getAmount(key), ' ', '-', 4).trim();
	}
	
	private static boolean isNotZero(Mod303 mod, Mod303Key... keys) {
		for (Mod303Key key : keys) {
			if (mod.getAmount(key) != 0.0) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isNotBlank(Mod303 mod, Mod303Key... keys) {
		for (Mod303Key key : keys) {
			if (AonStringUtils.isNotBlank(mod.getDescription(key))) {
				return true;
			}
		}
		return false;
	}
	
	// CAMBIAR CARACTERES NO PERMITIDOS (ACENTOS, &, ', ETC.) Y PONER EN MAYUSCULAS
	private static String changeCharacters(String fileString) {
		fileString = AonStringUtils.trimToEmpty(fileString);
		fileString = AonStringUtils.upperCase(fileString);
		fileString = fileString.replace("'", " ");
		fileString = fileString.replace("&", "Y");	
		fileString = fileString.replace("Á", "A");
		fileString = fileString.replace("É", "E");
		fileString = fileString.replace("Í", "I");
		fileString = fileString.replace("Ó", "O");
		fileString = fileString.replace("Ú", "U");
		fileString = fileString.replace("Ü", "U");		
		return fileString;
	}

}
