package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Asset;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Mod347Key;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod347Writer {

	@FunctionalInterface
	private interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod347 mod347, Mod347Declared declared, Mod347Asset asset) throws IOException;
	}

	private enum Mod347File2014 {
		
		// TERRITORIO COMUN -----------------------------
		
		// Registro de Declarante
		TYPE_1 (new IPropertyFiller[] { 
			(line, mod347, declared, asset) -> line.append("1") 											       // TIPO DE REGISTRO
		   ,(line, mod347, declared, asset) -> line.append("347") 										           // MODELO DECLARACIÓN
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(mod347.getYear(), 4,0)) 	   // EJERCICIO
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(mod347.getDocument(),9))	 	   // N.I.F. DEL DECLARANTE
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(mod347.getName(),40))	 	   // APELLIDOS Y NOMBRE O RAZÓN SOCIAL DEL DECLARANTE
		   ,(line, mod347, declared, asset) -> line.append("T") 											       // TIPO DE SOPORTE
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(mod347.getContactPhone(),9))	   // CONTACTO. TELÉFONO
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(mod347.getContactPerson(),40))  // CONTACTO. APELLIDOS Y NOMBRE
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(mod347.getNumber(),13,0))   // NUMERO DE IDENTIFICATIVO DE LA DECLARACION 
		   ,(line, mod347, declared, asset) -> line.append(mod347.isComplementary()?"C":" ")                       // DECLARACIÓN COMPLEMENTARIA
		   ,(line, mod347, declared, asset) -> line.append(mod347.isReplacement()?"S":" ")						   // DECLARACIÓN SUSTITUTIVA:
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(mod347.isComplementary()||mod347.isReplacement()?mod347.getReplacedNumber():"",13,0))	// NUMERO IDENTIFICATIVO ANTERIOR
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(mod347.getDeclared()==null?0:mod347.getDeclared().size(),9,0))                                               // Número total de personas o entidades		   
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace((mod347.getDeclared()==null?0.0:mod347.getDeclared().stream().mapToDouble(det -> det.getAmount()).sum()),16,2))	// Importe anual de las operaciones
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(mod347.getAssets()==null?0:mod347.getAssets().size(),9,0))                                                   // Número total de inmuebles 		   
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace((mod347.getAssets()==null?0:mod347.getAssets().stream().mapToDouble(det -> det.getAmount()).sum()),16,2))	    // Importe de las operaciones de arrendamiento de locales de negocio
		   ,(line, mod347, declared, asset) -> line.append(AonStringUtils.repeat(' ', 205))						           // BLANCOS		
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(mod347.getRepresentativeDocument(),9))  // NIF Representante legal
		   ,(line, mod347, declared, asset) -> line.append(AonStringUtils.repeat(' ',88))	     				   		   // BLANCOS
		   ,(line, mod347, declared, asset) -> line.append(AonStringUtils.repeat(' ',13))		     			           // SELLO ELECTRONICO
		   ,(line, mod347, declared, asset) -> line.append("\r\n") 												           // FIN DE REGISTRO. CONSTANTE CRLF
		})
		
		// Registro de Declarado 
		,TYPE_2_DEC (new IPropertyFiller[] { 
			(line, mod347, declared, asset) -> line.append("2") 													         // TIPO DE REGISTRO
		   ,(line, mod347, declared, asset) -> line.append("347") 													         // MODELO DECLARACIÓN
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(mod347.getYear(), 4,0)) 		         // EJERCICIO
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(mod347.getDocument(),9))	 		         // N.I.F. DEL DECLARANTE
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(declared.getDocument(),9))	 		     // NIF del declarado
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(declared.getRepresentativeDocument(),9))	 // NIF del representante legal
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(declared.getName(),40))	 			     // Apellidos y nombre o Razón Social del declarado
		   ,(line, mod347, declared, asset) -> line.append("D") 													         // Tipo de Hoja		   
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned((declared.getProvince() == Province.NO_RESIDENTE ? 99 : Province.safeValue(declared.getProvince())),2,0))  // Código de Provincia
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text( declared.getProvince() == Province.DESCONOCIDO || declared.getProvince() == Province.NO_RESIDENTE ? Country.safeIso2(declared.getCountry()) : "",2)) // Código de País (solo se pone, si la provincia es desconocida o no residente) 
		   ,(line, mod347, declared, asset) -> line.append(" ") 													                    // Blancos
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(Mod347Key.safeValue(declared.getType()),1))          // Clave operación
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace(declared.getAmount(),16,2))                   // Importe anual de las operaciones
		   ,(line, mod347, declared, asset) -> line.append(declared.isInsuranceOperation()?"X":" ")                                     // Operación seguro
		   ,(line, mod347, declared, asset) -> line.append(declared.isBusinessPremiseRental()?"X":" ")                                  // Arrendamiento local de negocio
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(declared.getCashAmount(),15,2))                  // Importe percibido en metálico
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace(declared.getAssetAmount(),16,2))              // Importe anual percibido por transmisiones de inmuebles sujetas a IVA
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(declared.getCashYear(),4,0))                     // Ejercicio
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace(declared.getFirstQuarterAmount(),16,2))       // Importe operaciones primer trimestre
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace(declared.getAssetFirstQuarterAmount(),16,2))  // Importe transmisiones inmuebles primer trimestre  
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace(declared.getSecondQuarterAmount(),16,2))      // Importe operaciones segundo trimestre
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace(declared.getAssetSecondQuarterAmount(),16,2)) // Importe transmisiones inmuebles segundo trimestre  
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace(declared.getThirdQuarterAmount(),16,2))       // Importe operaciones tercer trimestre
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace(declared.getAssetThirdQuarterAmount(),16,2))  // Importe transmisiones inmuebles tercer trimestre  
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace(declared.getFourthQuarterAmount(),16,2))      // Importe operaciones cuarto trimestre
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace(declared.getAssetFourthQuarterAmount(),16,2)) // Importe transmisiones inmuebles cuarto trimestre  
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(declared.getOperatorNif(),17))	 		            // NIF operador intracomunitario
		   ,(line, mod347, declared, asset) -> line.append(declared.isVatAccrual()?"X":" ")                                             // Operaciones régimen especial criterio de caja
		   ,(line, mod347, declared, asset) -> line.append(declared.isIsp()?"X":" ")                                                    // Operación con inversión del sujeto pasivo
		   ,(line, mod347, declared, asset) -> line.append(declared.isDepositRegime()?"X":" ")                                          // Operación con bienes vinculados o destinados a vincularse al régimen de depósito distinto del aduanero
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace(declared.getVatAccrualAmount(),16,2))         // Importe anual de las operaciones devengadas conforme al criterio de caja del IVA
		   ,(line, mod347, declared, asset) -> line.append(AonStringUtils.repeat(' ',201))							                    // Blancos
		   ,(line, mod347, declared, asset) -> line.append("\r\n") 												                    	// FIN DE REGISTRO. CONSTANTE CRLF
		})
		
		// Registro de Inmueble
		,TYPE_2_ASS (new IPropertyFiller[] {
			(line, mod347, declared, asset) -> line.append("2") 														    // TIPO DE REGISTRO
		   ,(line, mod347, declared, asset) -> line.append("347") 													        // MODELO DECLARACIÓN
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(mod347.getYear(), 4,0)) 		        // EJERCICIO
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(mod347.getDocument(),9))	 		        // N.I.F. DEL DECLARANTE
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getDocument(),9))	 		        // NIF del arrendatario
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getRepresentativeDocument(),9))    // NIF del representante legal
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getName(),40))	 			        // Apellidos y nombre o Razón Social del declarado
		   ,(line, mod347, declared, asset) -> line.append("I") 													        // Tipo de Hoja		   
		   ,(line, mod347, declared, asset) -> line.append(AonStringUtils.repeat(' ',23))								    // BLANCOS
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace(asset.getAmount(),16,2))          // Importe de la operación
		   ,(line, mod180, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(asset.getAssetLocation(),1,0))       // SITUACIÓN DEL INMUEBLE
		   ,(line, mod180, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getCadasdralReference(),25))       // REFERENCIA CATASTRAL
		   ,(line, mod180, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetType(),5))           // TIPO DE VÍA
		   ,(line, mod180, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreet(),50))              // NOMBRE VÍA PÚBLICA
		   ,(line, mod180, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetNumberType(),3))     // TIPO DE NUMERACIÓN
		   ,(line, mod180, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(asset.getAssetStreetNumber(),5,0))   // NÚMERO DE CASA
		   ,(line, mod180, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetNumberSuffix(),3))   // CALIFICADOR DEL NÚMERO
		   ,(line, mod180, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetBlock(),3))	        // BLOQUE
		   ,(line, mod180, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetHall(),3))	        // PORTAL
		   ,(line, mod180, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetStair(),3))	        // ESCALERA
		   ,(line, mod180, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetFloor(),3))	        // PLANTA O PISO
		   ,(line, mod180, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetDoor(),3))	        // PUERTA
		   ,(line, mod180, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetComplement(),40))    // COMPLEMENTO
		   ,(line, mod180, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetCity(),30))	        // LOCALIDAD O POBLACIÓN
		   ,(line, mod180, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetTown(),30))	        // MUNICIPIO
		   ,(line, mod180, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(asset.getAssetStreetTownCode(),5,0)) // CÓDIGO DE MUNICIPIO
		   ,(line, mod180, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(asset.getAssetStreetProvince(),2,0)) // CÓDIGO PROVINCIA
		   ,(line, mod180, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(asset.getAssetStreetZip(),5,0))      // CÓDIGO POSTAL
		   ,(line, mod347, declared, asset) -> line.append(AonStringUtils.repeat(' ',167))								    // BLANCOS
		   ,(line, mod347, declared, asset) -> line.append("\r\n") 													        // FIN DE REGISTRO. CONSTANTE CRLF

		})
		
		;

		private IPropertyFiller[] propertyFillers;

		private Mod347File2014(IPropertyFiller[] pf) {
			this.propertyFillers = pf;
		}

		private void fillPage(Mod347 mod347, Mod347Declared declared,  Mod347Asset asset, Writer line) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(line, mod347, declared, asset);
			}
		}
		
	}
	
	public static void fillWriter(Mod347 mod347, Writer writer) throws IOException {
		
		// Territorio Comun
		// FALTA - Alava, Bizkaia, Gipuzkoa, Navarra
		Mod347File2014.TYPE_1.fillPage(mod347, null, null, writer);		
		for (Mod347Declared declared : mod347.getDeclared()) {
			Mod347File2014.TYPE_2_DEC.fillPage(mod347, declared, null, writer);
		}
		for (Mod347Asset asset : mod347.getAssets()) {
			Mod347File2014.TYPE_2_ASS.fillPage(mod347, null, asset, writer);
		}
		writer.flush();
		
	}
}
