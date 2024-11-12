package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.fiscal.Mod369;
import com.esferalia.aon.occam.api.model.fiscal.Mod369Detail;
import com.esferalia.aon.occam.api.model.fiscal.Mod369DetailCorrection;
import com.esferalia.aon.occam.api.model.fiscal.Mod369DetailOther;
import com.esferalia.aon.occam.api.model.fiscal.Mod369Regime;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod369Writer {

	@FunctionalInterface
	protected interface IModelAccepter {
		public boolean accept(Mod369 mod369);
	}
	
	@FunctionalInterface
	private interface IPropertyFiller {
		public void propertyFill(Writer writer, Mod369 mod369) throws IOException;
	}

	private enum Mod369File {
		
		T369_START (mod369 -> true, new IPropertyFiller[] {
				 (wr, mod) -> wr.append("<T")    // Inicio del identificador de modelo y página
				,(wr, mod) -> wr.append("369")   // Modelo
				,(wr, mod) -> wr.append("0")     // Discriminante
				,(wr, mod) -> wr.append(AonFiscalFileUtils.year(mod.getYear()))        // Ejercicio de devengo (EEEE)  
				,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))  // Periodo
				,(wr, mod) -> wr.append("0000>")  // Tipo y cierre
				,(wr, mod) -> wr.append(AonStringUtils.spaces(92))                 // Reservado
				,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getDocument(),9))   // NIF del titular
				,(wr, mod) -> wr.append(AonStringUtils.spaces(210))                // Reservado
		})
		
		,T36900 (mod369 -> true, new IPropertyFiller[] {				
				 (wr, mod) -> wr.append("<T36900>")      
				,(wr, mod) -> wr.append(AonStringUtils.spaces(93))  // Reservado
				,(wr, mod) -> wr.append("</T36900>")  
		})
		
		,T36901 (mod369 -> mod369.getRegime() == Mod369Regime.OUTSIDE, new IPropertyFiller[] {
				(wr, mod) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int a = 0; // Contador para lineas de detalle
					while (!isComplementary || a < mod.getDetails3().size() ) {
						wr.append("<T36901>");
//						wr.append(AonFiscalFileUtils.text(mod.getRegime().getContent(),5));       // Régimen
//						wr.append("D");                                                           // Categoría
//						wr.append(AonFiscalFileUtils.text(mod.getPayType().getContent(),1));      // Tipo de pago
//						wr.append(AonFiscalFileUtils.text(mod.getNrc(),22));                      // NRC Pago
//						wr.append(AonFiscalFileUtils.signed(mod.getAmountPaid(),17));             // Importe pagado
//						wr.append(isComplementary ? "C" : " ");                                   // Complementaria
//						wr.append(AonFiscalFileUtils.text(Country.safeIso2(mod.getCountry()),2)); // 1. Declarante. País
//						wr.append(AonFiscalFileUtils.text(mod.getDocument(),15));                 // 1. Declarante. NIF
//						wr.append(AonFiscalFileUtils.text(mod.getOperatorNumber(),15));           // 1. Declarante. Número de operador en el régimen (NEUOSS)
//						wr.append(AonFiscalFileUtils.text(mod.getName(),125));                    // 1. Declarante . Apellidos y nombre o razón social.
//						wr.append(AonFiscalFileUtils.year(mod.getYear()));                        // 2. Ejercicio y período. Ejercicio
//						wr.append(AonFiscalFileUtils.text(mod.getPeriod().toString(), 3)); // FALTA - ESTO NO SE SI ME DEVOLVERA LO QUE YO QUIERO       // 2. Ejercicio y período. Tipo de periodo y Periodo
//						wr.append(mod.isWithoutActivity() ? "1" : "0");                           // 2. Ejercicio y período. Declaración sin actividad
						addHeader(wr, mod, isComplementary);
				 
						for (int i = 1; i <= 28; i++) {
							addDetail(wr, mod.getDetails3(), a++);  // 3. Prestaciones de servicios
						}
						
						wr.append(AonStringUtils.spaces(17));  // Reservado
						wr.append("</T36901>");
						isComplementary = true;
					}
				}
		})
		
		,T36902 (mod369 -> mod369.getRegime() == Mod369Regime.OUTSIDE && !mod369.getCorrections().isEmpty(), new IPropertyFiller[] {
				(wr, mod) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int a = 0; // Contador para lineas de detalle
					while (!isComplementary || a < mod.getCorrections().size() ) {
						wr.append("<T36902>");
						wr.append(isComplementary ? "C" : " "); // Complementaria
				 
						for (int i = 1; i <= 28; i++) {
							addCorrection(wr, mod.getCorrections(), a++);  // 4. Correcciones
						}
						
						wr.append(AonStringUtils.spaces(17));  // Reservado
						wr.append("</T36902>");
						isComplementary = true;
					}
				}
		})
		
		,T36903 (mod369 -> mod369.getRegime() == Mod369Regime.OUTSIDE, new IPropertyFiller[] {				
				 (wr, mod) -> wr.append("<T36903>")     
				,(wr, mod) -> wr.append(AonStringUtils.spaces(2929))  // Reservado
				,(wr, mod) -> wr.append("</T36903>")  
		})
		
		,T36904 (mod369 -> mod369.getRegime() == Mod369Regime.UNION, new IPropertyFiller[] {
				(wr, mod) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int a = 0; // Contador para lineas de detalle
					while (!isComplementary || a < mod.getDetails3().size() ) {
						wr.append("<T36904>");
						addHeader(wr, mod, isComplementary);
				 
						for (int i = 1; i <= 28; i++) {
							addDetail(wr, mod.getDetails3(), a++);  // 3. Prestaciones de servicios desde el EMID España y desde establecimientos permanentes situados fuera de la UE
						}
						
						wr.append(AonStringUtils.spaces(17));  // Reservado
						wr.append("</T36904>");
						isComplementary = true;
					}
				}
		})
		
		,T36905 (mod369 -> mod369.getRegime() == Mod369Regime.UNION && !mod369.getDetails4().isEmpty(), new IPropertyFiller[] {
				(wr, mod) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int a = 0; // Contador para lineas de detalle
					while (!isComplementary || a < mod.getDetails4().size() ) {
						wr.append("<T36905>");
				 
						for (int i = 1; i <= 28; i++) {
							addDetail(wr, mod.getDetails4(), a++);  // 4. Entregas de bienes expedidos o transportados desde EMID España
						}
						
						wr.append(AonStringUtils.spaces(17));  // Reservado
						wr.append("</T36905>");
						isComplementary = true;
					}
				}
		})
		
		,T36906 (mod369 -> mod369.getRegime() == Mod369Regime.UNION && !mod369.getDetails5().isEmpty(), new IPropertyFiller[] {
				(wr, mod) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int a = 0; // Contador para lineas de detalle
					while (!isComplementary || a < mod.getDetails5().size() ) {
						wr.append("<T36906>");
				 
						for (int i = 1; i <= 28; i++) {
							addDetailOther(wr, mod.getDetails5(), a++);  // 5. Prestaciones de servicios desde establecimientos permanentes en otros EM distintos de España
						}
						
						wr.append(AonStringUtils.spaces(17));  // Reservado
						wr.append("</T36906>");
						isComplementary = true;
					}
				}
		})
		
		,T36907 (mod369 -> mod369.getRegime() == Mod369Regime.UNION && !mod369.getDetails6().isEmpty(), new IPropertyFiller[] {
				(wr, mod) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int a = 0; // Contador para lineas de detalle
					while (!isComplementary || a < mod.getDetails6().size() ) {
						wr.append("<T36907>");
				 
						for (int i = 1; i <= 28; i++) {
							addDetailOther(wr, mod.getDetails6(), a++);  // 6. Entregas de bienes expedidos o transportados desde otros EM distintos de España
						}
						
						wr.append(AonStringUtils.spaces(17));  // Reservado
						wr.append("</T36907>");
						isComplementary = true;
					}
				}
		})
		
		,T36908 (mod369 -> mod369.getRegime() == Mod369Regime.UNION && !mod369.getCorrections().isEmpty(), new IPropertyFiller[] {
				(wr, mod) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int a = 0; // Contador para lineas de detalle
					while (!isComplementary || a < mod.getDetails6().size() ) {
						wr.append("<T36908>");
				 
						for (int i = 1; i <= 28; i++) {
							addCorrection(wr, mod.getCorrections(), a++);  // Correcciones
						}
						
						wr.append(AonStringUtils.spaces(17));  // Reservado
						wr.append("</T36908>");
						isComplementary = true;
					}
				}
		})
		
		,T36909 (mod369 -> mod369.getRegime() == Mod369Regime.UNION, new IPropertyFiller[] {				
				 (wr, mod) -> wr.append("<T36909>")     
				,(wr, mod) -> wr.append(AonStringUtils.spaces(5785))  // Reservado
				,(wr, mod) -> wr.append("</T36909>")  
		})
		
		,T36910 (mod369 -> mod369.getRegime() == Mod369Regime.IMPORT, new IPropertyFiller[] {
				(wr, mod) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int a = 0; // Contador para lineas de detalle
					while (!isComplementary || a < mod.getDetails3().size() ) {
						wr.append("<T36910>");
						addHeader(wr, mod, isComplementary);
				 
						for (int i = 1; i <= 28; i++) {
							addDetail(wr, mod.getDetails3(), a++);  // 3. Importaciones de bienes de menos de 150 euros
						}
						
						wr.append(AonStringUtils.spaces(17));  // Reservado
						wr.append("</T36910>");
						isComplementary = true;
					}
				}
		})
		
		,T36911 (mod369 -> mod369.getRegime() == Mod369Regime.IMPORT && !mod369.getCorrections().isEmpty(), new IPropertyFiller[] {
				(wr, mod) -> {
					boolean isComplementary = false; // Indicador de pagina complementaria
					int a = 0; // Contador para lineas de detalle
					while (!isComplementary || a < mod.getDetails6().size() ) {
						wr.append("<T36911>");
				 
						for (int i = 1; i <= 28; i++) {
							addCorrection(wr, mod.getCorrections(), a++);  // Correcciones
						}
						
						wr.append(AonStringUtils.spaces(17));  // Reservado
						wr.append("</T36911>");
						isComplementary = true;
					}
				}
		})

		,T36912 (mod369 -> mod369.getRegime() == Mod369Regime.IMPORT, new IPropertyFiller[] {				
				 (wr, mod) -> wr.append("<T36912>")     
				,(wr, mod) -> wr.append(AonStringUtils.spaces(2929))  // Reservado
				,(wr, mod) -> wr.append("</T36912>")  
		})
		
		,T369_END (mod369 -> true, new IPropertyFiller[] {
				 (wr, mod) -> wr.append("</T")    // Cierre del identificador de modelo y página
				,(wr, mod) -> wr.append("369")    // Modelo
				,(wr, mod) -> wr.append("0")      // Discriminante
				,(wr, mod) -> wr.append(AonFiscalFileUtils.year(mod.getYear()))                 // Ejercicio de devengo (EEEE)  
				,(wr, mod) -> wr.append(AonFiscalFileUtils.text(mod.getPeriod().getName(), 2))  // Periodo
				,(wr, mod) -> wr.append("0000>")  // Tipo y cierre
		})
		;

		private IModelAccepter accepter;
		private IPropertyFiller[] propertyFillers;

		private Mod369File(IModelAccepter accepter, IPropertyFiller[] pf) {			
			this.accepter = accepter; 
			this.propertyFillers = pf;			
		}
		public boolean accept(Mod369 mod369) {
			return accepter.accept(mod369);
		}
		private void fillPage(Mod369 mod369, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod369);
			}
		}
		
	}
	
	private static void addHeader(Writer wr, Mod369 mod, boolean isComplementary) throws IOException {
		
		wr.append(AonFiscalFileUtils.text(mod.getRegime().getContent(),5));       // Régimen
		wr.append("D");                                                           // Categoría
		wr.append(AonFiscalFileUtils.text(mod.getPayType().getContent(),1));      // Tipo de pago
		wr.append(AonFiscalFileUtils.text(mod.getNrc(),22));                      // NRC Pago
		wr.append(AonFiscalFileUtils.signed(mod.getAmountPaid(),17));             // Importe pagado
		wr.append(isComplementary ? "C" : " ");                                   // Complementaria
		wr.append(AonFiscalFileUtils.text(Country.safeIso2(mod.getCountry()),2)); // 1. Declarante. País
		wr.append(AonFiscalFileUtils.text(mod.getDocument(),15));                 // 1. Declarante. NIF
		
		if (mod.getRegime() == Mod369Regime.OUTSIDE || mod.getRegime() == Mod369Regime.IMPORT)
			wr.append(AonFiscalFileUtils.text(mod.getOperatorNumber(),15));       // 1. Declarante. Número de operador en el régimen (solo Regimen Exterior o Importación)
		
		wr.append(AonFiscalFileUtils.text(mod.getName(),125));                    // 1. Declarante . Apellidos y nombre o razón social.
		
		if (mod.getRegime() == Mod369Regime.IMPORT) { 
			wr.append(mod.isIntermediary() ? "1" : "0"); // Actúa a través de intermediario (solo Régimen de Importación)
			wr.append(AonFiscalFileUtils.text(mod.getIntermediaryNumber(),15)); // Nº de identificación del intermediario (NIOSSIn) (solo Régimen de Importación)
		}

		wr.append(AonFiscalFileUtils.year(mod.getYear()));                        // 2. Ejercicio y período. Ejercicio
		wr.append(AonFiscalFileUtils.text(mod.getPeriod().toString(), 3)); // FALTA - ESTO NO SE SI ME DEVOLVERA LO QUE YO QUIERO       // 2. Ejercicio y período. Tipo de periodo y Periodo
		
		if (mod.getRegime() == Mod369Regime.UNION || mod.getRegime() == Mod369Regime.IMPORT) {
			wr.append(AonFiscalFileUtils.date(mod.getFromDate())); // Fecha desde (solo Régimen de la Unión o Importación)
			wr.append(AonFiscalFileUtils.date(mod.getToDate()));   // Fecha hasta (solo Régimen de la Unión o Importación)
		}
		
		wr.append(mod.isWithoutActivity() ? "1" : "0");                           // Declaración sin actividad

	}
	
	private static void addDetail(Writer wr, LinkedList<Mod369Detail> details, int index) throws IOException {

		if (index < details.size()) {
			wr.append(AonFiscalFileUtils.text(Country.safeIso2(details.get(index).getCountry()),2));  // Código de país/EM de consumo 
			wr.append(AonFiscalFileUtils.unsigned(details.get(index).getVatPercent(),5,2));           // Tipo (%) de IVA              
			wr.append(AonFiscalFileUtils.text(details.get(index).getVatType().getContent(),1));       // Tipo IVA                     
			wr.append(AonFiscalFileUtils.signed(details.get(index).getBase(),17));                    // Base imponible               
			wr.append(AonFiscalFileUtils.signed(details.get(index).getQuota(),17));                   // Cuota IVA                    
		} else {
			// El formato del fichero dice que si no lleva contenido se cumplimentan con blancos, sean del tipo que sean
			wr.append(AonStringUtils.spaces(2));  // Código de país/EM de consumo
			wr.append(AonStringUtils.spaces(5));  // Tipo (%) de IVA
			wr.append(AonStringUtils.spaces(1));  // Tipo IVA
			wr.append(AonStringUtils.spaces(17)); // Base imponible 
			wr.append(AonStringUtils.spaces(17)); // Cuota IVA
		}

	}
	
	private static void addDetailOther(Writer wr, LinkedList<Mod369DetailOther> details, int index) throws IOException {

		if (index < details.size()) {
			wr.append(AonFiscalFileUtils.text(Country.safeIso2(details.get(index).getOtherCountry()),2));  // Código de país envío o EP
			wr.append(AonFiscalFileUtils.text(details.get(index).getOtherDocument(),15));                  // NIVA/otro código identificativo
			wr.append(AonFiscalFileUtils.text(Country.safeIso2(details.get(index).getCountry()),2));       // Código de país/EM de consumo 
			wr.append(AonFiscalFileUtils.unsigned(details.get(index).getVatPercent(),5,2));                // Tipo (%) de IVA              
			wr.append(AonFiscalFileUtils.text(details.get(index).getVatType().getContent(),1));            // Tipo IVA                     
			wr.append(AonFiscalFileUtils.signed(details.get(index).getBase(),17));                         // Base imponible               
			wr.append(AonFiscalFileUtils.signed(details.get(index).getQuota(),17));                        // Cuota IVA                    
		} else {
			// El formato del fichero dice que si no lleva contenido se cumplimentan con blancos, sean del tipo que sean
			wr.append(AonStringUtils.spaces(2));  // Código de país envío o EP
			wr.append(AonStringUtils.spaces(15)); // NIVA/otro código identificativo
			wr.append(AonStringUtils.spaces(2));  // Código de país/EM de consumo
			wr.append(AonStringUtils.spaces(5));  // Tipo (%) de IVA
			wr.append(AonStringUtils.spaces(1));  // Tipo IVA
			wr.append(AonStringUtils.spaces(17)); // Base imponible 
			wr.append(AonStringUtils.spaces(17)); // Cuota IVA
		}

	}
	
	private static void addCorrection(Writer wr, LinkedList<Mod369DetailCorrection> corrections, int index) throws IOException {

		if (index < corrections.size()) {
			wr.append(AonFiscalFileUtils.text(Country.safeIso2(corrections.get(index).getCountry()),2)); // Código País EM de consumo
			wr.append(AonFiscalFileUtils.year(corrections.get(index).getYear()));                        // Ejercicio
			wr.append(AonFiscalFileUtils.text(corrections.get(index).getPeriod().toString(),3));         // FALTA - ESTO NO SE SI ME DEVOLVERA LO QUE YO QUIERO Tipo de periodo y Periodo
			wr.append(AonFiscalFileUtils.signed(corrections.get(index).getQuota(),17));                  // Cuota IVA corregida
		} else {
			// El formato del fichero dice que si no lleva contenido se cumplimentan con blancos, sean del tipo que sean
			wr.append(AonStringUtils.spaces(2));  // Código de país/EM de consumo
			wr.append(AonStringUtils.spaces(4));  // Ejercicio
			wr.append(AonStringUtils.spaces(3));  // Tipo Periodo y Periodo
			wr.append(AonStringUtils.spaces(17)); // Cuota IVA corregida 
		}

	}
	
	public static void fillWriter(Mod369 mod369, Writer wr) throws IOException {
		for (Mod369File format : Mod369File.values()) {
			if (format.accept(mod369)) {
				format.fillPage(mod369, wr);
			}
		}
	}
	
}
