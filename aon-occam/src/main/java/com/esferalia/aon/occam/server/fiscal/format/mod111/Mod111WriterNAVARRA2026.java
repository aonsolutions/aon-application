package com.esferalia.aon.occam.server.fiscal.format.mod111;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.occam.server.fiscal.format.mod111.Mod111Writer.IMod111Writer;
import com.esferalia.aon.occam.server.fiscal.format.mod111.Mod111Writer.IModelAccepter;
import com.esferalia.aon.occam.server.fiscal.format.mod111.Mod111Writer.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod111WriterNAVARRA2026 implements IMod111Writer{ 

	private static enum Mod111File {
		NAVARRA_1_2026 ( mod111 -> true ,new IPropertyFiller[] {
			 (wr, mod) -> wr.append("1")                                    												// TIPO DE REGISTRO Constante "1" (uno)
			,(wr, mod) -> wr.append(mod.getPeriod().isMonthPeriod() ? "745" : "715") 										// MODELO DE PRESENTACIÓN Constante Trimestral "715" o Mensual "745"
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(),4,0)) 										// EJERCICIO
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().isMonthPeriod() ? Period.getQuarterlyPeriod(mod.getPeriod().getStartMonth()).getFormatName(mod.getAdministration()) : mod.getPeriod().getFormatName(mod.getAdministration()), 1)) 	// PERIODO - TRIMESTRE Valores: 1, 2, 3, o 4
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().isMonthPeriod() ? mod.getPeriod().getName() : "00", 2)) 																															// PERIODO - MES valores 1, 2, 3, 4..12, TRIMESTRAL 00
			,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(6)) 															// RELLENO A CEROS (6)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9)) 											// NIF DEL DECLARANTE
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(AonStringUtils.trimToEmpty(AonStringUtils.trimToEmpty(mod.getSurname()) + AonStringUtils.SPACE + mod.getName()),40)) // APELLIDOS Y NOMBRE O RAZÓN SOCIAL DEL DECLARANTE
			,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(14)) 															// RELLENO A CEROS (14)
			,(wr, mod) -> wr.append("T") 																					// TIPO PRESENTACIÓN "T": telemática
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getContactPhone(),9)) 										// PERSONA CON QUIEN RELACIONARSE - TELÉFONO 	
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getContactPerson(),40)) 									// PERSONA CON QUIEN RELACIONARSE - APELLIDOS Y NOMBRE
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getNumber(),13)) 											// RELLENO A CEROS (13)
			,(wr, mod) -> wr.append(" ") 																					// RELLENO A BLANCOS (1)
			,(wr, mod) -> wr.append((mod.isReplacement() ? "S" : (mod.isComplementary() ? "C" : " "))) 						// DECLARACIÓN ORDINARIA, SUSTITUTIVA O COMPLEMENTARIA (BLANCO, S, C)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getReplacedNumber(),13)) 									// NÚMERO DE JUSTIFICANTE DE LA SUSTITUIDA
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinanceCCC(),20)) 										// CUENTA BANCARIA PARA DEVOLUCIONES Y PAGOS
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDeclarationResultType() == FiscalModelDeclarationType.BANK ? "7" : mod.getDeclarationResultType() == FiscalModelDeclarationType.DEPOSIT ? "1" : "0",1)) // SOLICITUD TRAMITACIÓN DEL PAGO POR HACIENDA (0-NO SOLICITA, 1-SOLICITA PAGO ONLINE DECLARANTE, 7-DOMICILIACION)
			,(wr, mod) -> wr.append(AonStringUtils.spaces(22)) 																// RELLENO A BLANCOS (22)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(8)) 															// RELLENO A CEROS (8)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinance() != null && mod.getFinance().getBankAccount() != null && mod.getFinance().getBankAccount().getCountry() != null ? mod.getFinance().getBankAccount().getCountry().getIso2() : AonStringUtils.spaces(2), 2))	// CÓDIGO PAÍS IBAN (2)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getFinance() != null && mod.getFinance().getBankAccount() != null ? mod.getFinance().getBankAccount().getCheck() : AonStringUtils.spaces(2), 2)) 																		// CÓDIGO CONTROL IBAN (2)
			,(wr, mod) -> wr.append(AonStringUtils.spaces(9)) 																// RELLENO A BLANCOS (9)
			,(wr, mod) -> wr.append(AonStringUtils.spaces(10)) 																// RESTO CUENTA BANCARIA (no española) (10) (NO SE USA, SE ASUME CUENTA BANCARIA ESPAÑOLA) 
			,(wr, mod) -> wr.append(AonStringUtils.spaces(5)) 																// RELLENO A BLANCOS (5)
			,(wr, mod) -> wr.append(AonStringUtils.spaces(13)) 																// VERSIÓN PROGRAMA DE AYUDA (13)
			,(wr, mod) -> wr.append(AonStringUtils.CR_LF) 																	// TOTAL 250
		})
		,NAVARRA_2_2026 ( mod111 -> true ,new IPropertyFiller[] {
			 (wr, mod) -> wr.append("2") 																					// TIPO DE REGISTRO Constante "2" (dos)
			,(wr, mod) -> wr.append(mod.getPeriod().isMonthPeriod() ? "745" : "715") 										// MODELO DE PRESENTACIÓN Constante Trimestral "715" o Mensual "745"
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(),4,0)) 										// EJERCICIO
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().isMonthPeriod() ? Period.getQuarterlyPeriod(mod.getPeriod().getStartMonth()).getFormatName(mod.getAdministration()) : mod.getPeriod().getFormatName(mod.getAdministration()), 1)) 	// PERIODO - TRIMESTRE Valores: 1, 2, 3, o 4
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().isMonthPeriod() ? mod.getPeriod().getName() : "00", 2)) 																															// PERIODO - MES valores 1, 2, 3, 4..12, TRIMESTRAL 00
			,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(6)) 															// RELLENO A CEROS (6)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9)) 											// NIF DEL DECLARANTE
			,(wr, mod) -> wr.append("0001") 																				// CÓDIGO DE LA CASILLA EN EL IMPRESO - 0001: Cantidad a ingresar 
			,(wr, mod) -> wr.append(AonFiscalFileUtils.signedSpace(mod.getAmount(Mod111Key.NF_A1),16,2)) 					// SIGNO DEL VALOR (en blanco si es positivo y una "N" si negativo) + VALOR (numérico de 15 posiciones)
			,(wr, mod) -> wr.append(AonStringUtils.repeat("0000 000000000000000", 9)) 										// CASILLA, SIGNO Y VALOR (nueve repeticiones)
			,(wr, mod) -> wr.append(AonStringUtils.spaces(24)) 																// RELLENO A BLANCOS (24)
			,(wr, mod) -> wr.append(AonStringUtils.CR_LF) 																	// TOTAL 250
		})
		,NAVARRA_3_2026 ( mod111 -> true ,new IPropertyFiller[] {
			 (wr, mod) -> wr.append("3") 																					// TIPO DE REGISTRO Constante "3" (tres)
			,(wr, mod) -> wr.append(mod.getPeriod().isMonthPeriod() ? "745" : "715") 										// MODELO DE PRESENTACIÓN Constante Trimestral "715" o Mensual "745"
			,(wr, mod) -> wr.append(AonFiscalFileUtils.unsigned(mod.getYear(),4,0)) 										// EJERCICIO
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().isMonthPeriod() ? Period.getQuarterlyPeriod(mod.getPeriod().getStartMonth()).getFormatName(mod.getAdministration()) : mod.getPeriod().getFormatName(mod.getAdministration()), 1)) 	// PERIODO - TRIMESTRE Valores: 1, 2, 3, o 4
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().isMonthPeriod() ? mod.getPeriod().getName() : "00", 2)) 																															// PERIODO - MES valores 1, 2, 3, 4..12, TRIMESTRAL 00
			,(wr, mod) -> wr.append(AonFiscalFileUtils.zeros(6)) 															// RELLENO A CEROS (6)
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9)) 											// NIF DEL DECLARANTE
			,(wr, mod) -> wr.append("9000") 																				// CÓDIGO DE LA CASILLA EN EL IMPRESO Numérico de 4 posiciones - 9000: Correo electrónico
			,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getContactEmail(), 50)) 									// DETALLE DEL DATO. Alfanumérico de 50 posiciones
			,(wr, mod) -> wr.append("0000")																					// CÓDIGO DE LA CASILLA EN EL IMPRESO Numérico de 4 posiciones
			,(wr, mod) -> wr.append(AonStringUtils.spaces(50))																// DETALLE DEL DATO. Alfanumérico de 50 posiciones
			,(wr, mod) -> wr.append("0000")																					// CÓDIGO DE LA CASILLA EN EL IMPRESO Numérico de 4 posiciones
			,(wr, mod) -> wr.append(AonStringUtils.spaces(50))																// DETALLE DEL DATO. Alfanumérico de 50 posiciones
			,(wr, mod) -> wr.append(AonStringUtils.spaces(62)) 																// RELLENO A BLANCOS (62)
			,(wr, mod) -> wr.append(AonStringUtils.CR_LF)		    														// TOTAL 250
		})
		;
		
		private IModelAccepter accepter;
		private IPropertyFiller[] propertyFillers;

		private Mod111File(IModelAccepter accepter,IPropertyFiller[] pf) {
			this.accepter = accepter; 
			this.propertyFillers = pf;
		}
		public boolean accept(Mod111 mod111) {
			return accepter.accept(mod111);
		}
		private void fillPage(Mod111 mod111, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod111);
			}
		}
	}

	public void fillWriter(Mod111 mod111, Writer wr) throws IOException {
		boolean filled = false;
		for (Mod111File format : Mod111File.values()) {
			if (format.accept(mod111)) {
				format.fillPage(mod111, wr);
				filled = true;
			}
		}
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
	
}
