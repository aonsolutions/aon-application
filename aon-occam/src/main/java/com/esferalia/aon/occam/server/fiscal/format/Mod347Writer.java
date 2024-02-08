package com.esferalia.aon.occam.server.fiscal.format;

import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Asset;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Mod347Key;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod347Writer {

	@FunctionalInterface
	private interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod347 mod347, Mod347Declared declared, Mod347Asset asset) throws IOException;
	}

	private enum Mod347File2014 {
		
		// TERRITORIO COMUN - ALAVA - BIZKAIA - NAVARRA --------------------------
		
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
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(mod347.getAdministration()==Administration.BIZKAIA?"":mod347.getNumber(),13,0))   // NUMERO DE IDENTIFICATIVO DE LA DECLARACION 
		   ,(line, mod347, declared, asset) -> line.append(mod347.isComplementary() && mod347.getAdministration()!=Administration.BIZKAIA?"C":" ")                       // DECLARACIÓN COMPLEMENTARIA
		   ,(line, mod347, declared, asset) -> line.append(mod347.isReplacement()?"S":" ")						   // DECLARACIÓN SUSTITUTIVA:
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned((((mod347.isComplementary() && mod347.getAdministration()!=Administration.NAVARRA) || mod347.isReplacement()) && mod347.getAdministration()!=Administration.BIZKAIA) ? mod347.getReplacedNumber() : "",13,0))	// NUMERO IDENTIFICATIVO ANTERIOR
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(mod347.getDeclared()==null?0:mod347.getDeclared().size(),9,0))                                                     // Número total de personas o entidades		   
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace((mod347.getDeclared()==null?0.0:mod347.getDeclared().stream().mapToDouble(det -> det.getAmount()).sum()),16,2)) // Importe anual de las operaciones
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(mod347.getAssets()==null?0:mod347.getAssets().size(),9,0))                                                         // Número total de inmuebles 		   
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace((mod347.getAssets()==null?0:mod347.getAssets().stream().mapToDouble(det -> det.getAmount()).sum()),16,2))	      // Importe de las operaciones de arrendamiento de locales de negocio
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
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(declared.getDocument(),9))                // N.I.F. DEL DECLARADO
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
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(getOperatorNif(declared.getOperatorNif()),17))	 		            // NIF operador intracomunitario
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
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getName(),40))	 			        // Apellidos y nombre o Razón Social del arrendatario
		   ,(line, mod347, declared, asset) -> line.append("I") 													        // Tipo de Hoja		   
		   ,(line, mod347, declared, asset) -> line.append(AonStringUtils.repeat(' ',22))								    // BLANCOS
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace(asset.getAmount(),16,2))          // Importe de la operación
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(asset.getAssetLocation(),1,0))       // SITUACIÓN DEL INMUEBLE
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getCadasdralReference(),25))       // REFERENCIA CATASTRAL
		   
		   // Para Navarra el programa de ayuda usa el código de 2 caracteres, en vez de 5, si se carga el fichero con el 
		   // programa de ayuda, no se carga el codigo de 5 caracteres
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text( mod347.getAdministration() == Administration.NAVARRA ? getStreetType2(asset.getAssetStreetType()) : asset.getAssetStreetType(),5))   // TIPO DE VÍA
		   
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreet(),50))              // NOMBRE VÍA PÚBLICA
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetNumberType(),3))     // TIPO DE NUMERACIÓN
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(asset.getAssetStreetNumber(),5,0))   // NÚMERO DE CASA
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetNumberSuffix(),3))   // CALIFICADOR DEL NÚMERO
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetBlock(),3))	        // BLOQUE
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetHall(),3))	        // PORTAL
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetStair(),3))	        // ESCALERA
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetFloor(),3))	        // PLANTA O PISO
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetDoor(),3))	        // PUERTA
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetComplement(),40))    // COMPLEMENTO
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetCity(),30))	        // LOCALIDAD O POBLACIÓN
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetTown(),30))	        // MUNICIPIO
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(asset.getAssetStreetTownCode(),5,0)) // CÓDIGO DE MUNICIPIO
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(asset.getAssetStreetProvince(),2,0)) // CÓDIGO PROVINCIA
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(asset.getAssetStreetZip(),5,0))      // CÓDIGO POSTAL
		   ,(line, mod347, declared, asset) -> line.append(AonStringUtils.repeat(' ',167))								    // BLANCOS
		   ,(line, mod347, declared, asset) -> line.append("\r\n") 													        // FIN DE REGISTRO. CONSTANTE CRLF

		})
		
		// GIPUZKOA ---------------------------------------------------------		
		
		// Registro de Declarante - Gipuzkoa
		,TYPE_1_GIPUZKOA (new IPropertyFiller[] { 
			(line, mod347, declared, asset) -> line.append("1") 											       // TIPO DE REGISTRO
		   ,(line, mod347, declared, asset) -> line.append("347") 										           // MODELO DECLARACIÓN
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(mod347.getYear(), 4,0)) 	   // EJERCICIO
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(provinceCodeDeponent,2))    // CODIGO DE PROVINCIA DECLARANTE
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(mod347.getDocument(),9))	 	   // N.I.F. DEL DECLARANTE		   
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(mod347.getName(),40))	 	   // APELLIDOS Y NOMBRE O RAZÓN SOCIAL DEL DECLARANTE
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned((int)(mod347.getDeclared()==null?0:mod347.getDeclared().stream().filter(det -> det.getType() == Mod347Key.A || det.getType() == Mod347Key.G).count()),9,0))                                    // Compras - Número total de personas o entidades
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace((mod347.getDeclared()==null?0.0:mod347.getDeclared().stream().filter(det -> det.getType() == Mod347Key.A || det.getType() == Mod347Key.G).mapToDouble(det -> det.getAmount()).sum()),16,2)) // Compras - Importe anual de las operaciones
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned((int)(mod347.getDeclared()==null?0:mod347.getDeclared().stream().filter(det -> det.getType() == Mod347Key.B || det.getType() == Mod347Key.F).count()),9,0))                                    // Ventas - Número total de personas o entidades
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace((mod347.getDeclared()==null?0.0:mod347.getDeclared().stream().filter(det -> det.getType() == Mod347Key.B || det.getType() == Mod347Key.F).mapToDouble(det -> det.getAmount()).sum()),16,2)) // Ventas - Importe anual de las operaciones
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned((int)(mod347.getDeclared()==null?0:mod347.getDeclared().stream().filter(det -> det.getType() == Mod347Key.C).count()),9,0))                                    // Pagos mediacion - Número total de personas o entidades
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace((mod347.getDeclared()==null?0.0:mod347.getDeclared().stream().filter(det -> det.getType() == Mod347Key.C).mapToDouble(det -> det.getAmount()).sum()),16,2)) // Pagos mediacion - Importe anual de las operaciones
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned((int)(mod347.getDeclared()==null?0:mod347.getDeclared().stream().filter(det -> det.getType() == Mod347Key.D).count()),9,0))                                    // Compras entidades públicas - Número total de personas o entidades
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace((mod347.getDeclared()==null?0.0:mod347.getDeclared().stream().filter(det -> det.getType() == Mod347Key.D).mapToDouble(det -> det.getAmount()).sum()),16,2)) // Compras entidades públicas - Importe anual de las operaciones
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned((int)(mod347.getDeclared()==null?0:mod347.getDeclared().stream().filter(det -> det.getType() == Mod347Key.E).count()),9,0))                                    // Subvenciones, auxilios o ayudas - Número total de personas o entidades
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace((mod347.getDeclared()==null?0.0:mod347.getDeclared().stream().filter(det -> det.getType() == Mod347Key.E).mapToDouble(det -> det.getAmount()).sum()),16,2)) // Subvenciones, auxilios o ayudas - Importe anual de las operaciones
		   ,(line, mod347, declared, asset) -> line.append(AonStringUtils.repeat(' ', 316))						   // BLANCOS		
		   ,(line, mod347, declared, asset) -> line.append("\r\n") 												   // FIN DE REGISTRO. CONSTANTE CRLF
		})
		
		// Registro de Declarado - Gipuzkoa 
		,TYPE_2_DEC_GIPUZKOA (new IPropertyFiller[] { 
			(line, mod347, declared, asset) -> line.append("2") 													            // TIPO DE REGISTRO
		   ,(line, mod347, declared, asset) -> line.append("347") 													            // MODELO DECLARACIÓN
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(mod347.getYear(), 4,0)) 		            // EJERCICIO
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(provinceCodeDeponent,2))                 // CODIGO DE PROVINCIA DECLARANTE
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(mod347.getDocument(),9))	 		            // N.I.F. DEL DECLARANTE
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(Mod347Key.safeValue(declared.getType()),1))  // Clave operación
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(AonStringUtils.isBlank(declared.getDocument()) && AonStringUtils.isNotBlank(declared.getRepresentativeDocument()) ? declared.getRepresentativeDocument() : declared.getDocument() ,9)) // N.I.F. DEL DECLARADO (Se pone el NIF del representante si está cumplimentado y el del declarante está vacio)
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(declared.getName(),40))	 			        // Apellidos y nombre o Razón Social del declarado
		   ,(line, mod347, declared, asset) -> line.append(AonStringUtils.isBlank(declared.getDocument()) && AonStringUtils.isNotBlank(declared.getRepresentativeDocument()) ? "R" : " ") // Representante Legal (Se pone una R cuando en NIF del declarado hemos puesto el NIF del representante legal, por que el NIF del declarado estaba vacio)
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned((declared.getProvince() == Province.NO_RESIDENTE ? 99 : Province.safeValue(declared.getProvince())),2,0))  // Código de Provincia
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text( declared.getProvince() == Province.DESCONOCIDO || declared.getProvince() == Province.NO_RESIDENTE ? Country.safeIso2(declared.getCountry()) : "",2)) // Código de País (solo se pone, si la provincia es desconocida o no residente) 
		   ,(line, mod347, declared, asset) -> line.append(" ") 													                    // Blancos
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
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(getOperatorNif(declared.getOperatorNif()),17))	 		            // NIF operador intracomunitario
		   ,(line, mod347, declared, asset) -> line.append(declared.isVatAccrual()?"X":" ")                                             // Operaciones régimen especial criterio de caja
		   ,(line, mod347, declared, asset) -> line.append(declared.isIsp()?"X":" ")                                                    // Operación con inversión del sujeto pasivo
		   ,(line, mod347, declared, asset) -> line.append(declared.isDepositRegime()?"X":" ")                                          // Operación con bienes vinculados o destinados a vincularse al régimen de depósito distinto del aduanero
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace(declared.getVatAccrualAmount(),16,2))         // Importe anual de las operaciones devengadas conforme al criterio de caja del IVA
		   ,(line, mod347, declared, asset) -> line.append(AonStringUtils.repeat(' ',208))							                    // Blancos
		   ,(line, mod347, declared, asset) -> line.append("\r\n") 												                    	// FIN DE REGISTRO. CONSTANTE CRLF
		})
		
		// Registro de Inmueble - Gipuzkoa
		,TYPE_2_ASS_GIPUZKOA (new IPropertyFiller[] {
			(line, mod347, declared, asset) -> line.append("3") 														    // TIPO DE REGISTRO
		   ,(line, mod347, declared, asset) -> line.append("347") 													        // MODELO DECLARACIÓN
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(mod347.getYear(), 4,0)) 		        // EJERCICIO
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(provinceCodeDeponent,2))  	        // CODIGO DE PROVINCIA DECLARANTE
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(mod347.getDocument(),9))	 		        // N.I.F. DEL DECLARANTE
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getDocument(),9))	 		        // NIF del arrendatario
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getName(),40))	 			        // Apellidos y nombre o Razón Social del arrendatario
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.signedSpace(asset.getAmount(),16,2))          // Importe de la operación
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(asset.getAssetStreetProvince(),2,0)+"000") // CÓDIGO PROVINCIA
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetTown(),24))	        // MUNICIPIO
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getCadasdralReference(),25))       // REFERENCIA CATASTRAL
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(getStreetType2(asset.getAssetStreetType()),2)) // TIPO DE VÍA
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreet(),25))              // NOMBRE VÍA PÚBLICA
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.unsigned(asset.getAssetStreetNumber(),5,0))   // NÚMERO DE CASA
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetStair(),2))	        // ESCALERA
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetFloor(),2))	        // PISO
		   ,(line, mod347, declared, asset) -> line.append(AonFiscalFileUtils.text(asset.getAssetStreetDoor(),2))	        // PUERTA
		   ,(line, mod347, declared, asset) -> line.append(AonStringUtils.repeat(' ',324))								    // BLANCOS
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
	
	// Código provincia del declarante, para Gipuzkuoa
	private static Integer provinceCodeDeponent = 0;
	
	public static void fillWriter(Mod347 mod347, Writer writer) throws IOException {
		
		if (mod347.getAdministration() == Administration.GIPUZKOA) {
			// Gipuzkoa
			
			// Necesitamos el codigo de provincia del declarante para el fichero, se obtiene de la dirección principal
			provinceCodeDeponent = getRegistryMainAddressProvince(mod347);
			
			Mod347File2014.TYPE_1_GIPUZKOA.fillPage(mod347, null, null, writer);		
			for (Mod347Declared declared : mod347.getDeclared()) {
				Mod347File2014.TYPE_2_DEC_GIPUZKOA.fillPage(mod347, declared, null, writer);
			}
			for (Mod347Asset asset : mod347.getAssets()) {
				Mod347File2014.TYPE_2_ASS_GIPUZKOA.fillPage(mod347, null, asset, writer);
			}
		}
		else {
			// Territorio Comun - Alava - Bizkaia - Navarra
			Mod347File2014.TYPE_1.fillPage(mod347, null, null, writer);		
			for (Mod347Declared declared : mod347.getDeclared()) {
				Mod347File2014.TYPE_2_DEC.fillPage(mod347, declared, null, writer);
			}
			for (Mod347Asset asset : mod347.getAssets()) {
				Mod347File2014.TYPE_2_ASS.fillPage(mod347, null, asset, writer);
			}
		}
		writer.flush();
		
	}
	
	// Obtiene las siglas de la via (2 caracteres), según el Tipo de VIA (INE) que se le pasa
	private static String getStreetType2(String ineCode) {
		if (AonStringUtils.isBlank(ineCode))
			return ineCode;
		else {
			StreetType st = StreetType.getForIneCode(ineCode);
			if (st != null)
				return st.getAeatCode();
			else return null;				
		}
	}
	
	private static byte ZERO_BYTE = 0;
	
	// Obtiene el código de provincia de la dirección principal del declarante (se utiliza solo en Gipuzkoa)
	private static Integer getRegistryMainAddressProvince(Mod347 mod347) {
	
		CloseableAONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(mod347.getDomainName(), mod347.getDomain(), null);
			
			// Obtenemos la provincia de la direccion principal de registry de la empresa (declarante)
			Integer provinceCode = ctx.getDslContext()
				.select(GEOZONE.CODE)
				.from(RADDRESS)
				.join(GEOZONE).on(RADDRESS.GEOZONE.equal(GEOZONE.ID))
				.join(COMPANY).on(RADDRESS.REGISTRY.equal(COMPANY.REGISTRY))
				.where(COMPANY.DOMAIN.equal(mod347.getDomain()))
				.and(RADDRESS.TYPE.equal(ZERO_BYTE))		// Dirección principal.
				.limit(1)
				.fetch()
				.stream()
				.mapToInt(rec -> Integer.parseInt(rec.getValue(GEOZONE.CODE) ))
				.findFirst()
				.orElse(0);
			
			
			// Ceuta y Melilla deben ir con el codigo 55 y 56 respectivamente (segun el formato del fichero)
			// El programa de ayuda lo hace con los codigos 51 y 52, incluso cuando 
			// genera el fichero, así que lo pongo igual que el programa de ayuda
//			Province province = Province.safeValueOf(provinceCode);			
//			if (province != null) {				
//				if (province == Province.CEUTA)
//					provinceCode = 55;
//				else if (province == Province.MELILLA)
//					provinceCode = 56;				
//			}			
			return provinceCode;
			
		} finally {
			if (ctx != null)
				ctx.close();
		}
		
	}
	
	// Devuelve el mismo nif que se le pasa, si el NIF es de un pais intracomunitario, en caso contrario devuelve una cadena vacia
	// Se hace así por los registros de los servicios extracomunitarios, pues en la linea del 347 se guarda el NIF del cliente/proveedor en 
	// el campo NIF operador intracomunitario, pero en ese campo solo se admiten NIF-IVA comunitarios, por eso al presentarlo se deja vacio
	private static String getOperatorNif(String nif) {
		if (AonStringUtils.isNotBlank(nif)) {
			Country country = Country.safeValueOf(AonStringUtils.left(nif,2));
			if (country != null && !country.isIntracommunityCountry()) {
				return "";
			}			
		}
		return nif;
	}


}
