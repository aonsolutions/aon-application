package com.code.aon.file.tax.model.MOD123;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.code.aon.fiscal.enumeration.Mod123Key;


public class Alava2011MOD123Factory implements IMOD123Factory {
	private static final String ENCODING = "ISO-8859-15";
	private static final String ROOT = "AFADFA";
	private static final String DECLARACION = "DECLARACION";
	
	private static final String MODEL = "123";
	private static final String EMPTY = "";
	private static final String TRUE = "true";

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

	@Override
	public List<Exception> createDocument(List<Declaration> declarations, Writer out) {
		List<Exception> exceptions = new LinkedList<Exception>();
		try {
			DocumentFactory factory = DocumentFactory.getInstance();
			Document doc = factory.createDocument( ENCODING );
			Element root = factory.createElement( ROOT );
			doc.setRootElement(root);
	
			for (Declaration declaration : declarations) {
				createDocument(declaration, exceptions, root);
			}
			
			OutputFormat outformat = OutputFormat.createPrettyPrint();
			outformat.setEncoding(ENCODING);
			XMLWriter writer = new XMLWriter(out, outformat);
			writer.write(doc);
			writer.flush();
		} catch (UnsupportedEncodingException e) {
			exceptions.add(e);
		} catch (IOException e) {
			exceptions.add(e);
		}
		return exceptions;
	}
	
	private void createDocument(Declaration declaration, List<Exception> exceptions, Element root) {
		Element dec = root.addElement(DECLARACION);
		addDatosDec(dec,declaration);
		addDeclarante(dec,declaration);
		addModelo(dec,declaration);
	}

	private void addDatosDec(Element dec, Declaration declaration) {
		Element datos = dec.addElement(DATOSDEC);
		datos.addElement(DATO).addAttribute(NOMBRE,MODELO_ATT).addAttribute(VALOR, MODEL);
		datos.addElement(DATO).addAttribute(NOMBRE,EJERCICIO).addAttribute(VALOR, declaration.getYear().toString());
		datos.addElement(DATO).addAttribute(NOMBRE,RESULTADO).addAttribute(VALOR, formatNumber( declaration.getResult() ));
		datos.addElement(DATO).addAttribute(NOMBRE,CCC1).addAttribute(VALOR, StringUtils.substring(declaration.getCcc(), 0, 4));
		datos.addElement(DATO).addAttribute(NOMBRE,CCC2).addAttribute(VALOR, StringUtils.substring(declaration.getCcc(), 4, 8));
		datos.addElement(DATO).addAttribute(NOMBRE,CCC3).addAttribute(VALOR, StringUtils.substring(declaration.getCcc(), 8, 10));
		datos.addElement(DATO).addAttribute(NOMBRE,CCC4).addAttribute(VALOR, StringUtils.substring(declaration.getCcc(), 10, 20));
	}
	
	private String formatNumber(Double number) {
		return number==null?NF.format(0):NF.format(number);
	}

	private void addDeclarante(Element dec, Declaration declaration) {
		Element decl = dec.addElement(DECLARANTE);
		decl.addElement(DATO).addAttribute(NOMBRE,NIF).addAttribute(VALOR, declaration.getDocument());
		decl.addElement(DATO).addAttribute(NOMBRE,RSOCIAL).addAttribute(VALOR, declaration.getName());
		decl.addElement(DATO).addAttribute(NOMBRE,CALLE).addAttribute(VALOR, declaration.getStreetName());
		decl.addElement(DATO).addAttribute(NOMBRE,NUM).addAttribute(VALOR, declaration.getStreetNumber());
		decl.addElement(DATO).addAttribute(NOMBRE,LETRA).addAttribute(VALOR, declaration.getStreetDoor());
		decl.addElement(DATO).addAttribute(NOMBRE,ESC).addAttribute(VALOR, declaration.getStreetStair());
		decl.addElement(DATO).addAttribute(NOMBRE,PISO).addAttribute(VALOR, declaration.getStreetFloor());
		decl.addElement(DATO).addAttribute(NOMBRE,MANO).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,CPROVINCIA).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,MUNICIPIO).addAttribute(VALOR, declaration.getTown());
		decl.addElement(DATO).addAttribute(NOMBRE,ENTIDAD).addAttribute(VALOR, declaration.getTown());
		decl.addElement(DATO).addAttribute(NOMBRE,CPOSTAL).addAttribute(VALOR, declaration.getZip());
		decl.addElement(DATO).addAttribute(NOMBRE,TELEFONO1).addAttribute(VALOR, declaration.getPhone());
		decl.addElement(DATO).addAttribute(NOMBRE,TELEFONO2).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,FAX).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,EMAIL).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,NIFCONTACTO).addAttribute(VALOR, declaration.getDocument());
		decl.addElement(DATO).addAttribute(NOMBRE,NOMBRECONTACTO).addAttribute(VALOR, declaration.getContactPerson());
		decl.addElement(DATO).addAttribute(NOMBRE,TELECONTACTO).addAttribute(VALOR, declaration.getContactPhone());
		decl.addElement(DATO).addAttribute(NOMBRE,EMAILCONTACTO).addAttribute(VALOR, declaration.getContactMail() );
		decl.addElement(DATO).addAttribute(NOMBRE,NUMIBAN).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,NUMBIC).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,CCEXT).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,NIFTCC).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,NOMBRETCC).addAttribute(VALOR, EMPTY);
	}


	private void addModelo(Element dec, Declaration declaration) {
		Element mod = dec.addElement(MODELO);
		if ( declaration.isReplacement() || declaration.isComplementary()) {
			mod.addElement(CLAVE).addAttribute(NUMERO,"901").addAttribute(VALOR, TRUE );
		}
		addClave(mod, 1,declaration.getBoxes().get(Mod123Key.C01.getValue()),true);	
		addClave(mod, 2,declaration.getBoxes().get(Mod123Key.C02.getValue()));
		addClave(mod, 3,declaration.getBoxes().get(Mod123Key.C03.getValue()));
		addClave(mod, 4,declaration.getBoxes().get(Mod123Key.C07.getValue()));
		addClave(mod, 5,declaration.getBoxes().get(Mod123Key.C08.getValue()));
		addClave(mod, 6,declaration.getBoxes().get(Mod123Key.C09.getValue()));
		addClave(mod, 8,declaration.getBoxes().get(Mod123Key.C11.getValue()));
		addClave(mod, 9,declaration.getBoxes().get(Mod123Key.C12.getValue()));
		addClave(mod,10,declaration.getBoxes().get(Mod123Key.C13.getValue()));
	}

	private void addClave(Element mod, int clave, double value) {
		addClave(mod, clave, value, false);
	}
	
	private void addClave(Element mod, int clave, double value, boolean integer) {
		if (value != 0.0) {
			if (integer) {
				mod.addElement(CLAVE).addAttribute(NUMERO,Integer.toString(clave)).addAttribute(VALOR, Integer.toString( (int) value) );				
			} else {
				mod.addElement(CLAVE).addAttribute(NUMERO,Integer.toString(clave)).addAttribute(VALOR, formatNumber(value) );	
			}
				
		}
	}

}
