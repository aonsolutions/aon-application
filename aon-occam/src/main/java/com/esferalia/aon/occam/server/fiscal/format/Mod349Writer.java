package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod349Detail;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Mod349Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod349Writer {

	@FunctionalInterface
	private interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod349 mod349, Mod349Detail detail) throws IOException;
	}

	private enum Mod349File2011 {
		
		// TERRITORIO COMUN - ALAVA - BIZKAIA - NAVARRA -----------------------------
		
		// Registro de Declarante
		TYPE_1 (new IPropertyFiller[] { 
			(line, mod349,detail) -> line.append("1") 											        // TIPO DE REGISTRO
		   ,(line, mod349,detail) -> line.append("349") 										        // MODELO DECLARACIÓN
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(mod349.getYear(), 4,0)) 	// EJERCICIO
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(mod349.getDocument(),9))	 	// N.I.F. DEL DECLARANTE
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(mod349.getName(),40))	 		// APELLIDOS Y NOMBRE O RAZÓN SOCIAL DEL DECLARANTE
		   ,(line, mod349,detail) -> line.append("T") 											        // TIPO DE SOPORTE
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(mod349.getContactPhone(),9))	// CONTACTO. TELÉFONO
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(mod349.getContactPerson(),40))	// CONTACTO. APELLIDOS Y NOMBRE
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(mod349.getAdministration()==Administration.BIZKAIA?"":mod349.getNumber(),13,0))	// NUMERO DE IDENTIFICATIVO DE LA DECLARACION (En Bizkaia no se pone)
		   ,(line, mod349,detail) -> line.append(mod349.isComplementary()?"C":" ")                      // DECLARACIÓN COMPLEMENTARIA
		   ,(line, mod349,detail) -> line.append(mod349.isReplacement()?"S":" ")						// DECLARACIÓN SUSTITUTIVA:
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(mod349.isReplacement()||mod349.getAdministration()!=Administration.NAVARRA?mod349.getReplacedNumber():"",13,0))	// NUMERO IDENTIFICATIVO ANTERIOR (En Navarra solo si es sustitutiva)
		   ,(line, mod349,detail) -> line.append(getPeriodName(mod349.getPeriod()))  // Periodo
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(mod349.getDetails().stream().mapToDouble(det -> det.isRectification()?0.0:1.0).sum(),9,0))  // Número total de operadores intracomunitarios		   
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(mod349.getDetails().stream().mapToDouble(det -> det.isRectification()?0.0:det.getAmount()).sum(),15,2))	// Importe de las operaciones intracomunitarias
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(mod349.getDetails().stream().mapToDouble(det -> det.isRectification()?1.0:0.0).sum(),9,0))  // Número total de operadores intracomunitarios con rectificaciones		   
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(mod349.getDetails().stream().mapToDouble(det -> det.isRectification()?det.getAmount():0.0).sum(),15,2))	// Importe de las rectificaciones
		   ,(line, mod349,detail) -> line.append(mod349.isPeriodicityChange()?"X":" ")			        // Cambio en la periodicidad obligación de declarar
		   ,(line, mod349,detail) -> line.append(AonStringUtils.repeat(' ', 204))						// BLANCOS		
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(mod349.getRepresentativeDocument(),9))	 // NIF Representante legal
		   ,(line, mod349,detail) -> line.append(AonStringUtils.repeat(' ',88))	     					// BLANCOS
		   ,(line, mod349,detail) -> line.append(AonStringUtils.repeat(' ',13))		     				// SELLO ELECTRONICO
		   ,(line, mod349,detail) -> line.append("\r\n") 												// FIN DE REGISTRO. CONSTANTE CRLF
		})
		// Registro de Operaciones Intracomunitarias
		,TYPE_2_OPE (new IPropertyFiller[] { 
			(line, mod349,detail) -> line.append("2") 														// TIPO DE REGISTRO
		   ,(line, mod349,detail) -> line.append("349") 													// MODELO DECLARACIÓN
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(mod349.getYear(), 4,0)) 		// EJERCICIO
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(mod349.getDocument(),9))	 		// N.I.F. DEL DECLARANTE
		   ,(line, mod349,detail) -> line.append(AonStringUtils.repeat(' ',9))								// BLANCOS
		   ,(line, mod349,detail) -> line.append(AonStringUtils.repeat(' ',9))								// BLANCOS
		   ,(line, mod349,detail) -> line.append(AonStringUtils.repeat(' ',40))							    // BLANCOS
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(Country.safeIso2(detail.getCountry()),2))  // NIF operador intracomunitario - País
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(detail.getDocument(),15))	 		// NIF operador intracomunitario - Número
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(detail.getName(),40))	 			// Apellidos y nombre o Razón Social del operador intracomunitario
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(Mod349Key.safeValue(detail.getType()),1))	// Clave de Operación
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(detail.getAmount(),13,2))      // Base imponible
		   ,(line, mod349,detail) -> line.append(AonStringUtils.repeat(' ',354))							// BLANCOS		
		   ,(line, mod349,detail) -> line.append("\r\n") 													// FIN DE REGISTRO. CONSTANTE CRLF
		})
		// Registro de Rectificaciones
		,TYPE_2_REC (new IPropertyFiller[] {
			(line, mod349,detail) -> line.append("2") 														 // TIPO DE REGISTRO
		   ,(line, mod349,detail) -> line.append("349") 													 // MODELO DECLARACIÓN
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(mod349.getYear(), 4,0)) 		 // EJERCICIO
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(mod349.getDocument(),9))	 		 // N.I.F. DEL DECLARANTE
		   ,(line, mod349,detail) -> line.append(AonStringUtils.repeat(' ',9))								 // BLANCOS
		   ,(line, mod349,detail) -> line.append(AonStringUtils.repeat(' ',9))								 // BLANCOS
		   ,(line, mod349,detail) -> line.append(AonStringUtils.repeat(' ',40))								 // BLANCOS
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(Country.safeIso2(detail.getCountry()),2))   // NIF operador intracomunitario - País
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(detail.getDocument(),15))	 		 // NIF operador intracomunitario - Número
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(detail.getName(),40))	 			 // Apellidos y nombre o Razón Social del operador intracomunitario
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(Mod349Key.safeValue(detail.getType()),1))	 // Clave de Operación
		   ,(line, mod349,detail) -> line.append(AonStringUtils.repeat(' ',13))							     // BLANCOS
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(detail.getRectifiedYear(),4,0)) // Rectificaciones - Ejercicio
		   ,(line, mod349,detail) -> line.append(getPeriodName(detail.getRectifiedPeriod()))  // Periodo
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(detail.getAmount(),13,2))       // Base imponible rectificada
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(detail.getRectifiedAmount(),13,2))  // Base imponible declarada anteriormente
		   ,(line, mod349,detail) -> line.append(AonStringUtils.repeat(' ',322))							 // BLANCOS		
		   ,(line, mod349,detail) -> line.append("\r\n") 													 // FIN DE REGISTRO. CONSTANTE CRLF

		})
		
		// GIPUZKOA ----------------------------------------------------
		
		// Registro de Declarante
		,TYPE_1_GIPUZKOA (new IPropertyFiller[] { 
			(line, mod349,detail) -> line.append("1") 											        // TIPO DE REGISTRO
		   ,(line, mod349,detail) -> line.append("349") 										        // MODELO DECLARACIÓN
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(mod349.getYear(), 4,0)) 	// EJERCICIO
		   ,(line, mod349,detail) -> line.append(getPeriodName(mod349.getPeriod()))  // Periodo		   
		   ,(line, mod349,detail) -> line.append("20") 										            // Hacienda Foral de Gipuzkoa		   		   
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(mod349.getDocument(),9))	 	// N.I.F. DEL DECLARANTE
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(mod349.getName(),40))	 		// APELLIDOS Y NOMBRE O RAZÓN SOCIAL DEL DECLARANTE		   
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(mod349.getDetails().stream().mapToDouble(det -> det.isRectification()?0.0:1.0).sum(),9,0))  // Número total de operadores intracomunitarios		   
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(mod349.getDetails().stream().mapToDouble(det -> det.isRectification()?0.0:det.getAmount()).sum(),13,2))	// Importe de las operaciones intracomunitarias
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(mod349.getDetails().stream().mapToDouble(det -> det.isRectification()?1.0:0.0).sum(),9,0))  // Número total de operadores intracomunitarios con rectificaciones		   
		   ,(line, mod349,detail) -> line.append(mod349.isPeriodicityChange()?"X":" ")			        // Cambio en la periodicidad obligación de declarar
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(mod349.getContactPhone(),9))	// CONTACTO. TELÉFONO
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(mod349.getContactPerson(),40))	// CONTACTO. APELLIDOS Y NOMBRE
		   ,(line, mod349,detail) -> line.append(AonStringUtils.repeat(' ',105))						// BLANCOS
		   ,(line, mod349,detail) -> line.append("G10") 										        // Marca
		   ,(line, mod349,detail) -> line.append("\r\n") 												// FIN DE REGISTRO. CONSTANTE CRLF
		})
		// Registro de Operaciones Intracomunitarias
		,TYPE_2_OPE_GIPUZKOA (new IPropertyFiller[] { 
			(line, mod349,detail) -> line.append("2") 														// TIPO DE REGISTRO
		   ,(line, mod349,detail) -> line.append("349") 													// MODELO DECLARACIÓN
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(mod349.getYear(), 4,0)) 		// EJERCICIO
		   ,(line, mod349,detail) -> line.append(getPeriodName(mod349.getPeriod()))  // Periodo
		   ,(line, mod349,detail) -> line.append("20") 										            	// Hacienda Foral de Gipuzkoa		   
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(mod349.getDocument(),9))	 		// N.I.F. DEL DECLARANTE
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(Mod349Key.safeValue(detail.getType()),1))	// Clave de Operación
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(Country.safeIso2(detail.getCountry()),2))  // NIF operador intracomunitario - País
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(detail.getDocument(),12))	 		// NIF operador intracomunitario - Número
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(detail.getName(),40))	 			// Apellidos y nombre o Razón Social del operador intracomunitario
		   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(detail.getAmount(),13,2))      // Base imponible
		   ,(line, mod349,detail) -> line.append(AonStringUtils.repeat(' ',158))							// BLANCOS
		   ,(line, mod349,detail) -> line.append("G10") 										            // Marca
		   ,(line, mod349,detail) -> line.append("\r\n") 													// FIN DE REGISTRO. CONSTANTE CRLF
		})
		// Registro de Rectificaciones
		,TYPE_2_REC_GIPUZKOA (new IPropertyFiller[] { 
				(line, mod349,detail) -> line.append("2") 														// TIPO DE REGISTRO
			   ,(line, mod349,detail) -> line.append("349") 													// MODELO DECLARACIÓN
			   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(mod349.getYear(), 4,0)) 		// EJERCICIO
			   ,(line, mod349,detail) -> line.append(getPeriodName(mod349.getPeriod()))  // Periodo
			   ,(line, mod349,detail) -> line.append("20") 										            	// Hacienda Foral de Gipuzkoa		   
			   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(mod349.getDocument(),9))	 		// N.I.F. DEL DECLARANTE
			   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(Mod349Key.safeValue(detail.getType()),1))	// Clave de Operación
			   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(Country.safeIso2(detail.getCountry()),2))  // NIF operador intracomunitario - País
			   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(detail.getDocument(),12))	 		// NIF operador intracomunitario - Número
			   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.text(detail.getName(),40))	 			// Apellidos y nombre o Razón Social del operador intracomunitario			   
			   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(Math.abs(detail.getAmount()),13,2))  // Base imponible: Importe en valor absoluto de la rectificación			   
			   ,(line, mod349,detail) -> line.append(detail.getAmount()<0?"-":"+")   					              // Signo 			   
			   ,(line, mod349,detail) -> line.append(AonFiscalFileUtils.unsigned(detail.getRectifiedYear(),4,0))      // Rectificaciones - Ejercicio
			   ,(line, mod349,detail) -> line.append(getPeriodName(detail.getRectifiedPeriod()))  // Periodo
			   ,(line, mod349,detail) -> line.append(AonStringUtils.repeat(' ',151))							// BLANCOS
			   ,(line, mod349,detail) -> line.append("G10") 										            // Marca
			   ,(line, mod349,detail) -> line.append("\r\n") 													// FIN DE REGISTRO. CONSTANTE CRLF
			})
		
		;

		private IPropertyFiller[] propertyFillers;

		private Mod349File2011(IPropertyFiller[] pf) {
			this.propertyFillers = pf;
		}

		private void fillPage(Mod349 mod349, Mod349Detail detail, Writer line) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(line, mod349, detail);
			}
		}
		
	}
	
	// Utilizo este metodo local para devolver el periodo, por que getName() de 
	// Period en periodo anual me devuelve "An" y necesito "0A". No lo cambio en Period, 
	// por si se está utilizando en algún otro sitio
	private static String getPeriodName(Period period) {
		return period == null ? "  " : period == Period.YEAR ? "0A" : period.getName();
	}

	public static void fillWriter(Mod349 mod349, Writer writer) throws IOException {				 
		if (mod349.getAdministration() == Administration.GIPUZKOA) {
			// Gipuzkoa
			Mod349File2011.TYPE_1_GIPUZKOA.fillPage(mod349, null, writer);		
			for (Mod349Detail detail : mod349.getDetails()) {
				if (detail.isRectification())
					Mod349File2011.TYPE_2_REC_GIPUZKOA.fillPage(mod349, detail, writer);
				else Mod349File2011.TYPE_2_OPE_GIPUZKOA.fillPage(mod349, detail, writer);	
			}			
		}
		else {
			// Territorio Comun - Alava - Bizkaia - Navarra
			Mod349File2011.TYPE_1.fillPage(mod349, null, writer);		
			for (Mod349Detail detail : mod349.getDetails()) {
				if (detail.isRectification())
					Mod349File2011.TYPE_2_REC.fillPage(mod349, detail, writer);
				else Mod349File2011.TYPE_2_OPE.fillPage(mod349, detail, writer);	
			}
		}
		writer.flush();
	}
}
