package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.occam.server.fiscal.format.Mod123Writer.IMod123Writer;
import com.esferalia.aon.occam.server.fiscal.format.Mod123Writer.IModelAccepter;
import com.esferalia.aon.occam.server.fiscal.format.Mod123Writer.IPropertyFiller;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod123WriterARABA2016 implements IMod123Writer{ 

	private static enum Mod123File {
		
		ARABA_2016 ( mod123 -> true ,new IPropertyFiller[] {
			(wr, mod) -> new Mod123Araba2016().propertyFill(wr, mod)
		})
		;
			
		private IModelAccepter accepter;
		private IPropertyFiller[] propertyFillers;
	
		private Mod123File(IModelAccepter accepter,IPropertyFiller[] pf) {
			this.accepter = accepter; 
			this.propertyFillers = pf;
		}
		public boolean accept(Mod123 mod123) {
			return accepter.accept(mod123);
		}
		private void fillPage(Mod123 mod123, Writer wr) throws IOException {
			for (IPropertyFiller propertyFiller : this.propertyFillers) {
				propertyFiller.propertyFill(wr, mod123);
			}
		}
	}

	public void fillWriter(Mod123 mod123, Writer wr) throws IOException {
		boolean filled = false;
		for (Mod123File format : Mod123File.values()) {
			if (format.accept(mod123)) {
				format.fillPage(mod123, wr);
				filled = true;
			}
		}
		if (!filled) {
			throw new AonCoreException("La generaci\u00F3n de el modelo no est\u00E1 soportada.");
		}
	}
	
	private static class Mod123Araba2016 implements IPropertyFiller {
		private static final String ENCODING = "ISO-8859-15";
		private static final String ROOT = "AFADFA";
		private static final String DECLARACION = "DECLARACION";
		private static final String EMPTY = "";
		private static final String TRUE = "true";
		private static final String FALSE = "true";

		private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols();
		static {
			SYMBOLS.setDecimalSeparator(',');	
		}
		private static final DecimalFormat NF = new DecimalFormat("00000000000.00",SYMBOLS);
		
		private static final String DATOSDEC = "DATOSDEC";
		private static final String DECLARANTE = "DECLARANTE";
		private static final String MODELO = "MODELO"; 
		private static final String DATO = "DATO"; 
		private static final String NOMBRE = "nombre"; 
		private static final String VALOR = "valor"; 

		
		private static final String MODELO_ATT = "modelo";
		private static final String EJERCICIO = "ejercicio";
		private static final String RESULTADO = "resultado";
		private static final String CCC1 = "ccc1";
		private static final String CCC2 = "ccc2";
		private static final String CCC3 = "ccc3";
		private static final String CCC4 = "ccc4";

		
		private static final String NIF	= "nif"; 
		private static final String RSOCIAL = "rsocial";
		private static final String CALLE = "calle"; 
		private static final String NUM = "num"; 
		private static final String LETRA = "letra";
		private static final String ESC = "esc"; 
		private static final String PISO = "piso"; 
		private static final String MANO = "mano"; 
		private static final String CPROVINCIA = "cprovincia"; 
		private static final String MUNICIPIO = "municipio"; 
		private static final String ENTIDAD = "entidad"; 
		private static final String CPOSTAL = "cpostal";
		private static final String TELEFONO1 = "telefono1";
		private static final String TELEFONO2 = "telefono2";
		private static final String FAX = "fax"; 
		private static final String EMAIL = "email";
		private static final String NIFCONTACTO = "nifcontacto";
		private static final String NOMBRECONTACTO = "nombrecontacto";
		private static final String TELECONTACTO = "telecontacto";
		private static final String EMAILCONTACTO = "emailcontacto";
		private static final String NUMIBAN = "numiban";
		private static final String NUMBIC = "numbic"; 
		private static final String CCEXT = "ccext"; 
		private static final String NIFTCC = "niftcc"; 
		private static final String NOMBRETCC = "nombretcc"; 
		
		private static final String CLAVE = "CLAVE"; 
		private static final String NUMERO = "numero"; 
		
		public void propertyFill(Writer writer, Mod123 mod123) throws IOException {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc  = builder.newDocument();
				
				Element root = doc.createElement(ROOT);
				createDocument(mod123, doc,  root);
				doc.appendChild(root);
				
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty(OutputKeys.ENCODING, ENCODING);
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(writer);
				transformer.transform(source, result);
				writer.flush();
			} catch (TransformerConfigurationException e){
				throw new IOException(e);
			} catch (TransformerException e) {
				throw new IOException(e);
			} catch (ParserConfigurationException e) {
				throw new IOException(e);
			}
		}
		
		private void createDocument(Mod123 mod123, Document doc, Element root) {
			Element dec = doc.createElement(DECLARACION);
			root.appendChild(dec);	
			addDatosDec(mod123,doc,dec);
			addDeclarante(mod123,doc,dec);
			addModelo(mod123,doc,dec);
		}
		private String formatNumber(Double number) {
			return number==null?NF.format(0):NF.format(number);
		}

		private void addDatosDec(Mod123 mod123,Document doc, Element dec) {
			Element datos = doc.createElement(DATOSDEC);
			dec.appendChild(datos);
			
			Element e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE, MODELO_ATT);
			e1.setAttribute(VALOR, mod123.getPeriod().isMonthPeriod()?"123":"110");
			datos.appendChild(e1);
			Element e3 = doc.createElement(DATO);
			e3.setAttribute(NOMBRE,EJERCICIO);
			e3.setAttribute(VALOR, AonFiscalFileUtils.unsigned(mod123.getYear(), 4,0));
			datos.appendChild(e3);
			Element e4 = doc.createElement(DATO);
			e4.setAttribute(NOMBRE,RESULTADO);
			e4.setAttribute(VALOR, formatNumber( mod123.getResult() ));
			datos.appendChild(e4);
			Element e5 = doc.createElement(DATO);
			e5.setAttribute(NOMBRE,CCC1);
			e5.setAttribute(VALOR, AonStringUtils.substring(mod123.getFinanceCCC(), 0, 4));
			datos.appendChild(e5);
			Element e6 = doc.createElement(DATO);
			e6.setAttribute(NOMBRE,CCC2);
			e6.setAttribute(VALOR, AonStringUtils.substring(mod123.getFinanceCCC(), 4, 8));
			datos.appendChild(e6);
			Element e7 = doc.createElement(DATO);
			e7.setAttribute(NOMBRE,CCC3);
			e7.setAttribute(VALOR, AonStringUtils.substring(mod123.getFinanceCCC(), 8, 10));
			datos.appendChild(e7);
			Element e8 = doc.createElement(DATO);
			e8.setAttribute(NOMBRE,CCC4);
			e8.setAttribute(VALOR, AonStringUtils.substring(mod123.getFinanceCCC(), 10, 20));
			datos.appendChild(e8);
		}
		
		private void addDeclarante(Mod123 mod123, Document doc, Element dec) {
			Element datos = doc.createElement(DECLARANTE);
			dec.appendChild(datos);
			
			Element e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,NIF);
			e1.setAttribute(VALOR, mod123.getDocument());
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,RSOCIAL);
			e1.setAttribute(VALOR, AonStringUtils.join(new String[] {mod123.getName(),mod123.getSurname()},' '));
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,CALLE);
			e1.setAttribute(VALOR, mod123.getStreetName());
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,NUM);
			e1.setAttribute(VALOR, mod123.getStreetNumber());
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,LETRA);
			e1.setAttribute(VALOR, mod123.getStreetDoor());
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,ESC);
			e1.setAttribute(VALOR, mod123.getStreetStair());
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,PISO);
			e1.setAttribute(VALOR, mod123.getStreetFloor());
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,MANO);
			e1.setAttribute(VALOR, EMPTY);
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,CPROVINCIA);
			e1.setAttribute(VALOR, EMPTY);
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,MUNICIPIO);
			e1.setAttribute(VALOR, mod123.getTown());
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,ENTIDAD);
			e1.setAttribute(VALOR, mod123.getTown());
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,CPOSTAL);
			e1.setAttribute(VALOR, mod123.getZip());
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,TELEFONO1);
			e1.setAttribute(VALOR, mod123.getPhone());
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,TELEFONO2);
			e1.setAttribute(VALOR, EMPTY);
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,FAX);
			e1.setAttribute(VALOR, EMPTY);
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,EMAIL);
			e1.setAttribute(VALOR, EMPTY);
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,NIFCONTACTO);
			e1.setAttribute(VALOR, mod123.getDocument());
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,NOMBRECONTACTO);
			e1.setAttribute(VALOR, mod123.getContactPerson());
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,TELECONTACTO);
			e1.setAttribute(VALOR, mod123.getContactPhone());
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,EMAILCONTACTO);
			e1.setAttribute(VALOR, EMPTY);
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,NUMIBAN);
			e1.setAttribute(VALOR, mod123.getFinanceIban());
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,NUMBIC);
			e1.setAttribute(VALOR, EMPTY);
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,CCEXT);
			e1.setAttribute(VALOR, EMPTY);
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,NIFTCC);
			e1.setAttribute(VALOR, EMPTY);
			datos.appendChild(e1);
			e1 = doc.createElement(DATO);
			e1.setAttribute(NOMBRE,NOMBRETCC);
			e1.setAttribute(VALOR, EMPTY);
			datos.appendChild(e1);
			
		}

		private void addModelo(Mod123 mod123, Document doc, Element dec) {
			Element mod = doc.createElement(MODELO);
			dec.appendChild(mod);
			Element e = null;
			if ( mod123.isReplacement() || mod123.isComplementary()) {
				e = doc.createElement(CLAVE);
				e.setAttribute(NUMERO,"901");
				e.setAttribute(VALOR, TRUE );
				mod.appendChild(e);
			}
			Mod123Key[] keys = new Mod123Key[]{
			Mod123Key.AR_907,Mod123Key.AR_908,Mod123Key.AR_909
				,Mod123Key.AR_C01,Mod123Key.AR_C02,Mod123Key.AR_C03
				,Mod123Key.AR_C04,Mod123Key.AR_C05,Mod123Key.AR_C06
				,Mod123Key.AR_C07,Mod123Key.AR_C08,Mod123Key.AR_C09
				,Mod123Key.AR_C10
			};
			for (Mod123Key key : keys) {	
				e = doc.createElement(CLAVE);
				e.setAttribute(NUMERO, AonNumberUtils.toString( key.getBox()) );
				if (key == Mod123Key.AR_909
				 || key == Mod123Key.AR_C01) {
					e.setAttribute(VALOR, Integer.toString( (int) mod123.getAmount(key)) );	
				} else if (key == Mod123Key.AR_907) {
					e.setAttribute(VALOR, mod123.getAmount(key) == 1?TRUE:FALSE );
				} else if (key == Mod123Key.AR_908) {
					e.setAttribute(VALOR, mod123.getDescription(key));
				} else {
					e.setAttribute(VALOR, formatNumber(mod123.getAmount(key)) );
				}
						
				mod.appendChild(e);
			}
		}
	}
}
