package com.esferalia.aon.occam.server.fiscal.format.mod180;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.watson.error.AonCoreException;

public class Mod180Writer {

	@FunctionalInterface
	interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod180 mod180, Mod180Detail detail) throws IOException;
	}
	private enum Writers {
		ALL_2014 {

			@Override
			void fill(Mod180 mod180, Writer wr) throws IOException {
				Mod180File2014.fill(mod180, wr);
			}
			
			@Override
			boolean accept(Mod180 mod180) {
				return true;				
			}
		},
		;

		abstract boolean accept(Mod180 mod180);
		abstract void fill(Mod180 mod180, Writer wr) throws IOException;
	}
	
	public static void fillWriter(Mod180 mod180, Writer wr) throws IOException {
		boolean filled = false;
		for (Writers writer : Writers.values()) {
			if (writer.accept(mod180)) {
				writer.fill(mod180, wr);
				filled = true;
			}
		}
		wr.flush();
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}

/*	
	
	
	private enum Mod180File2014 {
		// REGISTRO DE DECLARANTE.
		TYPE_1 (new IPropertyFiller[] { 
			(line, mod180,detail) -> line.append("1") 											// TIPO DE REGISTRO
		   ,(line, mod180,detail) -> line.append("180") 										// MODELO DECLARACIÓN
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.unsigned(mod180.getYear(), 4,0)) 		// EJERCICIO
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(mod180.getDocument(),9))	 		// N.I.F. DEL DECLARANTE
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(mod180.getName(),40))	 			// APELLIDOS Y NOMBRE O RAZÓN SOCIAL DEL DECLARANTE
		   ,(line, mod180,detail) -> line.append("T") 											// TIPO DE SOPORTE
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(mod180.getContactPhone(),9))		// CONTACTO. TELÉFONO
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(mod180.getContactPerson(),40))		// CONTACTO. APELLIDOS Y NOMBRE
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.unsigned(mod180.getReceipt(),13,0))			// NUMERO DE IDENTIFICATIVO DE LA DECLARACION
		   ,(line, mod180,detail) -> line.append(" ") 											// DECLARACIÓN COMPLEMENTARIA
		   ,(line, mod180,detail) -> line.append(mod180.isReplacement()?"S":" ")				// DECLARACIÓN SUSTITUTIVA:
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.unsigned(					// NUMERO IDENTIFICATIVO ANTERIOR
				   mod180.getReplacedReceipt(),13,0))
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.unsigned(					// NÚMERO TOTAL DE PERCEPTORES
				   mod180.getDetails().size(),9,0))
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.signedSpace(				// BASE RETENCIONES E INGRESOS A CUENTA
				   mod180.getDetails().stream().mapToDouble(det -> det .getPerception()).sum(),16,2))
		   ,(line, mod180,detail) ->  line.append(AonFiscalFileUtils.unsigned(					// RETENCIONES E INGRESOS A CUENTA
				   mod180.getDetails().stream().mapToDouble( det -> det.getRetention() ).sum(),15,2))
		   ,(line, mod180,detail) -> line.append(AonStringUtils.repeat(' ', 62))				// BLANCOS		
		   ,(line, mod180,detail) -> line.append(AonStringUtils.repeat(' ', 263))				// SELLO ELECTRONICO
		   ,(line, mod180,detail) -> line.append("\r\n") 										// FIN DE REGISTRO. CONSTANTE CRLF
		})
		,TYPE_2 (new IPropertyFiller[] { 
			(line, mod180,detail) -> line.append("2") 											// TIPO DE REGISTRO
		   ,(line, mod180,detail) -> line.append("180") 										// MODELO DECLARACIÓN
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.unsigned(mod180.getYear(), 4,0)) 		// EJERCICIO
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(mod180.getDocument(),9))	 		// N.I.F. DEL DECLARANTE
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(detail.getDocument(),9))	 		// N.I.F. DEL DECLARANTE
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(									// N.I.F. DEL REPRESENTANTE LEGAL
				   detail.getRepresentativeDocument(),9))	 		
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(detail.getName(),40))	 			// APELLIDOS Y NOMBRE, RAZÓN DENOMINACIÓN DEL PERCEPTOR
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.unsigned(detail.getProvince(),2,0))		// CÓDIGO PROVINCIA
		   ,(line, mod180,detail) -> line.append(detail.isInKind()?"2":"1")						// MODALIDAD
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.signedSpace(				// BASE RETENCIONES E INGRESOS A CUENTA
				   detail.getPerception(),14,2))
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.unsigned(					// % RETENCIÓN
				   detail.getPercent(),4,2))
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.unsigned(					// RETENCIONES E INGRESOS A CUENTA
				   detail.getRetention(),13,2))
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.unsigned(					// EJERCICIO DE DEVENGO
				   detail.getAccrualYear(), 4,0))	
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.unsigned(					// SITUACIÓN DEL INMUEBLE
				   detail.getLocation(),1,0))
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(						// REFERENCIA CATASTRAL
				   detail.getCadasdralReference(),20))
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(						// TIPO DE VÍA
				   detail.getStreetType(),5))
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(						// NOMBRE VÍA PÚBLICA
				   detail.getStreetName(),50))
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(						// TIPO DE NUMERACIÓN
				   detail.getNumberType(),3))
		   ,(line, mod180,detail) -> line.append(												// NÚMERO DE CASA
				   AonFiscalFileUtils.unsigned(detail.getNumber(),5,0))
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(						// CALIFICADOR DEL NÚMERO
				   detail.getNumberSuffix(),3))
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(detail.getBlock(),3))	// BLOQUE
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(detail.getHall(),3))	// PORTAL
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(detail.getStair(),3))	// ESCALERA
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(detail.getFloor(),3))	// PLANTA O PISO
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(detail.getDoor(),3))	// PUERTA
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(						// COMPLEMENTO
				   detail.getComplement(),40))
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(detail.getCity(),30))	// LOCALIDAD O POBLACIÓN
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(detail.getTown(),30))	// MUNICIPIO
		   ,(line, mod180,detail) -> line.append(AonFiscalFileUtils.text(						// CÓDIGO DE MUNICIPIO
			 detail.getTownCode(),5))
		   ,(line, mod180,detail) -> line.append(												// CÓDIGO PROVINCIA
				   AonFiscalFileUtils.unsigned(detail.getProvinceCode(),2,0))
		   ,(line, mod180,detail) -> line.append(												// CÓDIGO POSTAL
				   AonFiscalFileUtils.unsigned(detail.getZip(),5,0))
		   ,(line, mod180,detail) -> line.append(AonStringUtils.repeat(' ', 173))				// BLANCOS		
		   ,(line, mod180,detail) -> line.append("\r\n") 										// FIN DE REGISTRO. CONSTANTE CRLF
		})
		;

		private IPropertyFiller[] propertyFillers;

		private Mod180File2014(IPropertyFiller[] pf) {
			this.propertyFillers = pf;
		}

		private void fillPage(Mod180 mod180, Mod180Detail detail, Writer line) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(line, mod180, detail);
			}
		}

	}

	public static void fillWriter(Mod180 mod180, Writer writer) throws IOException {
		Mod180File2014.TYPE_1.fillPage(mod180, null, writer);
		for (Mod180Detail detail : mod180.getDetails()) {
			Mod180File2014.TYPE_2.fillPage(mod180, detail, writer);	
		}
		writer.flush();
	}
*/
}
