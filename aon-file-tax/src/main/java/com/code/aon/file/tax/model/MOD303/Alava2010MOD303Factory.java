package com.code.aon.file.tax.model.MOD303;

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

import com.code.aon.file.tax.model.MOD303.data.Breakdown;
import com.code.aon.file.tax.model.MOD303.data.Declaration;

public class Alava2010MOD303Factory implements IMOD303Factory {
	private static final String ENCODING = "ISO-8859-15";
	private static final String ROOT = "AFADFA";
	private static final String DECLARACION = "DECLARACION";
	
	private static final String MODEL = "303";
	private static final String EMPTY = "";
	private static final String TRUE = "true";
	private static final String FALSE = "false";

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
		datos.addElement(DATO).addAttribute(NOMBRE,CCC1).addAttribute(VALOR, declaration.getCcc1());
		datos.addElement(DATO).addAttribute(NOMBRE,CCC2).addAttribute(VALOR, declaration.getCcc2());
		datos.addElement(DATO).addAttribute(NOMBRE,CCC3).addAttribute(VALOR, declaration.getCcc3());
		datos.addElement(DATO).addAttribute(NOMBRE,CCC4).addAttribute(VALOR, declaration.getCcc4());
	}
	
	private String formatNumber(Double number) {
		return number==null?NF.format(0):NF.format(number);
	}

	private void addDeclarante(Element dec, Declaration declaration) {
		Element decl = dec.addElement(DECLARANTE);
		decl.addElement(DATO).addAttribute(NOMBRE,NIF).addAttribute(VALOR, declaration.getDocument());
		decl.addElement(DATO).addAttribute(NOMBRE,RSOCIAL).addAttribute(VALOR, declaration.getSurname());
		decl.addElement(DATO).addAttribute(NOMBRE,CALLE).addAttribute(VALOR, declaration.getAddress());
		decl.addElement(DATO).addAttribute(NOMBRE,NUM).addAttribute(VALOR, declaration.getAddressNumber().toString());
		decl.addElement(DATO).addAttribute(NOMBRE,LETRA).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,ESC).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,PISO).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,MANO).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,CPROVINCIA).addAttribute(VALOR, declaration.getProvinceID());
		decl.addElement(DATO).addAttribute(NOMBRE,MUNICIPIO).addAttribute(VALOR, declaration.getEntity());
		decl.addElement(DATO).addAttribute(NOMBRE,ENTIDAD).addAttribute(VALOR, declaration.getEntity());
		decl.addElement(DATO).addAttribute(NOMBRE,CPOSTAL).addAttribute(VALOR, declaration.getZip().toString());
		decl.addElement(DATO).addAttribute(NOMBRE,TELEFONO1).addAttribute(VALOR, declaration.getTelephone()!=null?declaration.getTelephone().toString():EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,TELEFONO2).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,FAX).addAttribute(VALOR, declaration.getFax());
		decl.addElement(DATO).addAttribute(NOMBRE,EMAIL).addAttribute(VALOR, declaration.getEmail() );
		decl.addElement(DATO).addAttribute(NOMBRE,NIFCONTACTO).addAttribute(VALOR, declaration.getDocument());
		decl.addElement(DATO).addAttribute(NOMBRE,NOMBRECONTACTO).addAttribute(VALOR, declaration.getName());
		decl.addElement(DATO).addAttribute(NOMBRE,TELECONTACTO).addAttribute(VALOR, declaration.getTelephone());
		decl.addElement(DATO).addAttribute(NOMBRE,EMAILCONTACTO).addAttribute(VALOR, declaration.getEmail() );
		decl.addElement(DATO).addAttribute(NOMBRE,NUMIBAN).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,NUMBIC).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,CCEXT).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,NIFTCC).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,NOMBRETCC).addAttribute(VALOR, EMPTY);
	}


	private void addModelo(Element dec, Declaration declaration) {
		Element mod = dec.addElement(MODELO);
		if ( declaration.isWithoutActivity()) {
			mod.addElement(CLAVE).addAttribute(NUMERO,"90").addAttribute(VALOR, TRUE );	
		}
		if ( declaration.isReplacement() || declaration.isComplementary()) {
			mod.addElement(CLAVE).addAttribute(NUMERO,"91").addAttribute(VALOR, TRUE );
		}
		mod.addElement(CLAVE).addAttribute(NUMERO,"92").addAttribute(VALOR, declaration.isTaxRefundRegistry()?TRUE:FALSE );
		mod.addElement(CLAVE).addAttribute(NUMERO,"93").addAttribute(VALOR, declaration.isTaxRefundRegistry()?FALSE:TRUE );
		
		String[] percents = new String[]{"4.0","10.0","21.0","8.0","18.0","7.0","16.0"};
		int[] keys = new int[]{1,204,207,104,107,4,7};
		for (int i = 0; i < percents.length; ++i ) {
			Breakdown bd = declaration.getOutputVat().get(percents[i]);
			if (bd != null) {
				int k = keys[i];
				addClave(mod,k,bd.getTaxableBase());
				addClave(mod,++k,bd.getPercent());
				addClave(mod,++k,bd.getQuota());
			}
		}
		
		percents = new String[]{"0.5","1.4","5.2","1.0","4.0"};
		keys = new int[]{10,213,216,13,16};
		for (int i = 0; i < percents.length; ++i ) {
			Breakdown bd = declaration.getSurcharge().get(percents[i]);
			if (bd != null) {
				int k = keys[i];
				addClave(mod,k,bd.getTaxableBase());
				addClave(mod,++k,bd.getPercent());
				addClave(mod,++k,bd.getQuota());
			}
		}
		
		percents = new String[]{"4.0","10.0","21.0","8.0","18.0","7.0","16.0"};
		keys = new int[]{19,222,225,122,125,22,25};
		for (int i = 0; i < percents.length; ++i ) {
			Breakdown bd = declaration.getIntracommunitary().get(percents[i]);
			if (bd != null) {
				int k = keys[i];
				addClave(mod,k,bd.getTaxableBase());
				addClave(mod,++k,bd.getPercent());
				addClave(mod,++k,bd.getQuota());
			}
			bd = declaration.getInvPasive().get(percents[i]);
			if (bd != null) {
				int k = keys[i];
				addClave(mod,k,bd.getTaxableBase());
				addClave(mod,++k,bd.getPercent());
				addClave(mod,++k,bd.getQuota());
			}
			
		}
		
		addClave(mod,28,declaration.getOutputTotal());
		
		addClave(mod,30,declaration.getInnerCommonOperationsQuota() + declaration.getInnerExpensesOperationsQuota() );
		addClave(mod,31,declaration.getInnerInvestmentOperationsQuota());
		addClave(mod,32,declaration.getImportedCommonOperationsQuota());
		addClave(mod,33,declaration.getImportedInvestmentOperationsQuota());
		addClave(mod,34,declaration.getIntracommunitaryCommonOperationsQuota());
		addClave(mod,35,declaration.getIntracommunitaryInvestmentOperationsQuota());
		addClave(mod,36,declaration.getAgriculturalRegimeCompensation());
		addClave(mod,37,declaration.getInvestmentNormalization());
		addClave(mod,38,declaration.getDeductTotal());
		
		addClave(mod,39,declaration.getDifference());
		
		addClave(mod,50,declaration.getIntracommunitaryDeliveries());
		addClave(mod,51,declaration.getExportationTotal());
		addClave(mod,52,declaration.getNonTaxableTotal());
		
		addClave(mod,40,declaration.getAlavaPercent());
		addClave(mod,41,declaration.getGipuzkoaPercent());
		addClave(mod,42,declaration.getBizkaiaPercent());
		addClave(mod,43,declaration.getNavarraPercent() + declaration.getCommonTerritoryPercent());
		
		addClave(mod,44,declaration.getQuota());
		addClave(mod,45,declaration.getPreviousYearCompensateQuota());
		addClave(mod,60,declaration.getResult());
		addClave(mod,61,declaration.getExtraCharge());
		addClave(mod,62,declaration.getDelayInterest());
		
		//addClave(mod,"063", ??????????? ); // TODO
		
		addClave(mod,80,declaration.getDeposit());
		addClave(mod,81,declaration.getPayBack());
		addClave(mod,82,declaration.getCompensate());
		if (declaration.getDeposit() > 0) {
			if (StringUtils.isNotBlank( declaration.getDepositBankEntity())) {
				mod.addElement(CLAVE).addAttribute(NUMERO,"301").addAttribute(VALOR, declaration.getDepositBankEntity() );	
			}
			if (StringUtils.isNotBlank( declaration.getDepositBankOffice())) {
				mod.addElement(CLAVE).addAttribute(NUMERO,"302").addAttribute(VALOR, declaration.getDepositBankOffice() );	
			}
			if (StringUtils.isNotBlank( declaration.getDepositBankControl())) {
				mod.addElement(CLAVE).addAttribute(NUMERO,"303").addAttribute(VALOR, declaration.getDepositBankControl() );	
			}
			if (StringUtils.isNotBlank( declaration.getDepositBankAccount())) {
				mod.addElement(CLAVE).addAttribute(NUMERO,"304").addAttribute(VALOR, declaration.getDepositBankAccount() );	
			}
		}
		if (declaration.getPayBack() > 0) {
			if (StringUtils.isNotBlank( declaration.getPayBackBankEntity())) {
				mod.addElement(CLAVE).addAttribute(NUMERO,"301").addAttribute(VALOR, declaration.getPayBackBankEntity() );	
			}
			if (StringUtils.isNotBlank( declaration.getPayBackBankOffice())) {
				mod.addElement(CLAVE).addAttribute(NUMERO,"302").addAttribute(VALOR, declaration.getPayBackBankOffice() );	
			}
			if (StringUtils.isNotBlank( declaration.getPayBackBankControl())) {
				mod.addElement(CLAVE).addAttribute(NUMERO,"303").addAttribute(VALOR, declaration.getPayBackBankControl() );	
			}
			if (StringUtils.isNotBlank( declaration.getPayBackBankAccount())) {
				mod.addElement(CLAVE).addAttribute(NUMERO,"304").addAttribute(VALOR, declaration.getPayBackBankAccount() );	
			}
		}
	}

	private void addClave(Element mod, int clave, double value) {
		if (value != 0) {
			
			mod.addElement(CLAVE).addAttribute(NUMERO,Integer.toString(clave)).addAttribute(VALOR, formatNumber(value) );	
		}
	}

}
