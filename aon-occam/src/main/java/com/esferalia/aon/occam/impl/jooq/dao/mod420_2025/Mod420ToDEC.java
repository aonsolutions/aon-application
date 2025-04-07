package com.esferalia.aon.occam.impl.jooq.dao.mod420_2025;

import java.util.List;

import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod420ToDEC {
	
	private static final Mod303Key[] IGIC_DEVENGADO_KEYS = {
           Mod303Key.CA_DB01, Mod303Key.CA_DC01
		 , Mod303Key.CA_DB02, Mod303Key.CA_DC02
		 , Mod303Key.CA_DB03, Mod303Key.CA_DC03
		 , Mod303Key.CA_DB04, Mod303Key.CA_DC04
		 , Mod303Key.CA_DB05, Mod303Key.CA_DC05
		 , Mod303Key.CA_DB06, Mod303Key.CA_DC06
		 , Mod303Key.CA_DB07, Mod303Key.CA_DC07
		 , Mod303Key.CA_C019, Mod303Key.CA_C020
		 , Mod303Key.CA_C021, Mod303Key.CA_C022
		 , Mod303Key.CA_C023, Mod303Key.CA_C024};
	
	private static final Mod303Key[] IGIC_DEDUCIBLE_KEYS = {
		   Mod303Key.CA_C026, Mod303Key.CA_C027				
		 , Mod303Key.CA_C028, Mod303Key.CA_C029
		 , Mod303Key.CA_C030, Mod303Key.CA_C031
		 , Mod303Key.CA_C032, Mod303Key.CA_C033
		 , Mod303Key.CA_C034, Mod303Key.CA_C035    
		 , Mod303Key.CA_C036
		 , Mod303Key.CA_C037
		 , Mod303Key.CA_C038
		 , Mod303Key.CA_C039};
	
	private Mod420ToDEC() {
	}

	public static DEC getDEC(Mod303 mod) {
		DEC dec = new DEC();
		
		// Identificador del modelo
		if (mod.isMonthPeriod())
			dec.setMOD("417");                                  
		else
			dec.setMOD("420");                                  
		
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
		}
		
		return dec;
		
	}
	
	//	Datos identificativos y opciones tributarias.
	//	Modelo 420: ACO, RECC, DRECC, EOP, ACR, FAC y TAC.
    //	Modelo 417: RDM, RECC, DRECC, EOP, ACR, FAC y TAC.
	private static TIDENTIGIC getIde(Mod303 mod) {

		TIDENTIGIC ide = new TIDENTIGIC();
		
		if (mod.isMonthPeriod())
			ide.setRDM(getSiNo(mod, Mod303Key.CM_002));  // Inscrito en el Registro de devolución mensual (solo modelo 417)
		else
			ide.setACO(getSiNo(mod, Mod303Key.CA_X01));  // Autoliquidación conjunta (solo modelo 420)
		
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
		dp.setNIF(mod.getDocument());        // Nif de la persona
		dp.setNRS(mod.getFullName());        // Nombre o razón social
		dp.setSVP(mod.getStreetInitial());   // Siglas vía pública
		dp.setNVP(mod.getStreetName());      // Nombre de la vía pública
		dp.setNPK(mod.getStreetNumber());    // Número de edificio/pto kilométrico
		dp.setESC(mod.getStreetStair());     // Escalera
		dp.setPIS(mod.getStreetFloor());     // Piso
		dp.setPUE(mod.getStreetDoor());      // Puerta
		dp.setLOC(mod.getTown());            // Localidad
		dp.setTEL(mod.getPhone());       	 // Teléfono
		dp.setMOV(mod.getContactCellular()); // Teléfono móvil
		dp.setEMA(mod.getContactEmail());    // Email
		dp.setPOP(AonFiscalFileUtils.unsigned(Province.getByName(mod.getProvince()).ordinal(), 2)); // Código de provincia
		dp.setCMU(mod.getTownCode());        // Código de municipio
		dp.setCP(mod.getZip());              // Código Postal
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
		
		igicDev.setOIN(getBaseCuota(mod, Mod303Key.CA_C019, Mod303Key.CA_C020)); // Operaciones con inversión del sujeto pasivo
		igicDev.setMBC(getBaseCuota(mod, Mod303Key.CA_C021, Mod303Key.CA_C022)); // Modificación bases y cuotas
		igicDev.setCRV(getBaseCuota(mod, Mod303Key.CA_C023, Mod303Key.CA_C024)); // Cuotas devueltas en Régimen de viajeros
		
		// Total cuotas devengadas (solo si hay algo en las casillas del IVA devengado)
		if (isNotZero(mod, IGIC_DEVENGADO_KEYS)) {
			igicDev.setTOT(getAmount(mod, Mod303Key.CA_C025)); 	
		}
		
		return igicDev;
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
		
		// FALTA - SI LE PONGO FORMA DE PAGO 2 ADEUDO EN CUENTA EN INGRESO, ME DA EL SIGUIENTE ERROR (AUQUE LE PONGA IBAN):
		// [forma de pago] erróneo (incoherente con [opción de presentación elegida en los parámetros de entrada])
		
		// Forma de pago ingresos
		// 1 - Efectivo
		// 2 - Adeudo en cuenta 
		// 3 - Pago fraccionado
		// 4 - Domiciliación bancaria
		// 5 - Pago telemático
		// 6 - Aplazamiento 6 meses medidas Covid
		if (mod.getDeclarationResult() > 0) {
			String paymentMethod = "2"; // Adeudo en cuenta
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
	
	// --- METODOS AUXILIARES ---------------------------------------------------------------------------------------------------
	
	private static SINOType getSiNo(Mod303 mod, Mod303Key key) {
		return mod.getCheck(key) ? SINOType.S : SINOType.N; 
	}
	
	// Añade una fila al IVA devengado (solo si tiene base o cuota) 
	private static void addRow(List<TBASECUOTA> list, Mod303 mod, Mod303Key baseKey, Mod303Key percentKey, Mod303Key quotaKey) {
		if (mod.getAmount(baseKey) != 0.0 || mod.getAmount(quotaKey) != 0.0) {
			list.add(getBaseCuota(mod, baseKey, percentKey, quotaKey));
		}		
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

}
