package com.code.aon.file.tax.model.MOD303;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.code.aon.file.tax.model.MOD303.data.Declaration;

public class Alava2010MOD303XMLFactory implements IMOD303XMLFactory {
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
	public void createDocument(Declaration declaration, Writer out) {
		try {
			DocumentFactory factory = DocumentFactory.getInstance();
			Document doc = factory.createDocument( ENCODING );
			Element root = factory.createElement( ROOT );
			doc.setRootElement(root);
			Element dec = root.addElement(DECLARACION);
			
			Element datos = dec.addElement(DATOSDEC);
			fillDatosDec(datos,declaration);
			
			Element decl = dec.addElement(DECLARANTE);
			fillDeclarante(decl,declaration);

			Element mod = dec.addElement(MODELO);
			fillModelo(mod,declaration);

			OutputFormat outformat = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter(out, outformat);
			writer.write(doc);
			writer.flush();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			//TODO tratar
		} catch (IOException e) {
			e.printStackTrace();
			//TODO tratar
		}
	}

	private void fillDatosDec(Element datos, Declaration declaration) {
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

	private void fillDeclarante(Element decl, Declaration declaration) {
		decl.addElement(DATO).addAttribute(NOMBRE,NIF).addAttribute(VALOR, declaration.getDocument());
		decl.addElement(DATO).addAttribute(NOMBRE,RSOCIAL).addAttribute(VALOR, declaration.getName());
		decl.addElement(DATO).addAttribute(NOMBRE,CALLE).addAttribute(VALOR, declaration.getAddress());
		decl.addElement(DATO).addAttribute(NOMBRE,NUM).addAttribute(VALOR, declaration.getAddressNumber().toString());
		decl.addElement(DATO).addAttribute(NOMBRE,LETRA).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,ESC).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,PISO).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,MANO).addAttribute(VALOR, EMPTY);
		decl.addElement(DATO).addAttribute(NOMBRE,CPROVINCIA).addAttribute(VALOR, declaration.getProvince());
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


	private void fillModelo(Element mod, Declaration declaration) {
		if ( declaration.isWithoutActivity()) {
			mod.addElement(CLAVE).addAttribute(NUMERO,"90").addAttribute(VALOR, TRUE );	
		}
		if ( declaration.isReplacement() || declaration.isComplementary()) {
			mod.addElement(CLAVE).addAttribute(NUMERO,"91").addAttribute(VALOR, TRUE );
		}
		mod.addElement(CLAVE).addAttribute(NUMERO,"92").addAttribute(VALOR, declaration.isTaxRefundRegistry()?TRUE:FALSE );
		mod.addElement(CLAVE).addAttribute(NUMERO,"93").addAttribute(VALOR, declaration.isTaxRefundRegistry()?FALSE:TRUE );
		
		addClave(mod,"001",declaration.getBaseOutputVat4());
		addClave(mod,"001",declaration.getBaseOutputVat4());
		addClave(mod,"002",declaration.getPercentOutputVat4());
		addClave(mod,"003",declaration.getQuotaOutputVat4());
		
		addClave(mod,"104",declaration.getBaseOutputVat8());
		addClave(mod,"105",declaration.getPercentOutputVat8());
		addClave(mod,"106",declaration.getQuotaOutputVat8());
		
		addClave(mod,"107",declaration.getBaseOutputVat18());
		addClave(mod,"108",declaration.getPercentOutputVat18());
		addClave(mod,"109",declaration.getQuotaOutputVat18());
		
		addClave(mod,"004",declaration.getBaseOutputVat7());
		addClave(mod,"005",declaration.getPercentOutputVat7());
		addClave(mod,"006",declaration.getQuotaOutputVat7());
		
		addClave(mod,"007",declaration.getBaseOutputVat16());
		addClave(mod,"008",declaration.getPercentOutputVat16());
		addClave(mod,"009",declaration.getQuotaOutputVat16());
		
		addClave(mod,"010",declaration.getBaseSurcharge05());
		addClave(mod,"011",declaration.getPercentSurcharge05());
		addClave(mod,"012",declaration.getQuotaSurcharge05());
		
		addClave(mod,"013",declaration.getBaseSurcharge1());
		addClave(mod,"014",declaration.getPercentSurcharge1());
		addClave(mod,"015",declaration.getQuotaSurcharge1());
		
		addClave(mod,"016",declaration.getBaseSurcharge4());
		addClave(mod,"017",declaration.getPercentSurcharge4());
		addClave(mod,"018",declaration.getQuotaSurcharge4());
		
		addClave(mod,"019",declaration.getBaseIntracommunitary4());
		addClave(mod,"020",declaration.getPercentIntracommunitary4());
		addClave(mod,"021",declaration.getQuotaIntracommunitary4());
		
		addClave(mod,"122",declaration.getBaseIntracommunitary8());
		addClave(mod,"123",declaration.getPercentIntracommunitary8());
		addClave(mod,"124",declaration.getQuotaIntracommunitary8());
		
		addClave(mod,"125",declaration.getBaseIntracommunitary18());
		addClave(mod,"126",declaration.getPercentIntracommunitary18());
		addClave(mod,"127",declaration.getQuotaIntracommunitary18());
				
		addClave(mod,"022",declaration.getBaseIntracommunitary7());
		addClave(mod,"023",declaration.getPercentIntracommunitary7());
		addClave(mod,"024",declaration.getQuotaIntracommunitary7());
		
		addClave(mod,"025",declaration.getBaseIntracommunitary16());
		addClave(mod,"026",declaration.getPercentIntracommunitary16());
		addClave(mod,"027",declaration.getQuotaIntracommunitary16());
		
		addClave(mod,"028",declaration.getOutputTotal());
		
		addClave(mod,"030",declaration.getInnerCommonOperationsQuota() + declaration.getInnerExpensesOperationsQuota() );
		addClave(mod,"031",declaration.getInnerInvestmentOperationsQuota());
		addClave(mod,"032",declaration.getImportedCommonOperationsQuota());
		addClave(mod,"033",declaration.getImportedInvestmentOperationsQuota());
		addClave(mod,"034",declaration.getIntracommunitaryCommonOperationsQuota());
		addClave(mod,"035",declaration.getInvestmentCommonOperationsQuota());
		addClave(mod,"036",declaration.getAgriculturalRegimeCompensation());
		addClave(mod,"037",declaration.getInvestmentNormalization());
		addClave(mod,"038",declaration.getDeductTotal());
		
		addClave(mod,"039",declaration.getDifference());
		
		addClave(mod,"050",declaration.getIntracommunitaryDeliveries());
		addClave(mod,"051",declaration.getExportationTotal());
		addClave(mod,"052",declaration.getNonTaxableTotal());
		
		addClave(mod,"040",declaration.getAlavaPercent());
		addClave(mod,"041",declaration.getGipuzkoaPercent());
		addClave(mod,"042",declaration.getBizkaiaPercent());
		addClave(mod,"043",declaration.getNavarraPercent() + declaration.getCommonTerritoryPercent());
		
		addClave(mod,"044",declaration.getQuota());
		addClave(mod,"045",declaration.getPreviousYearCompensateQuota());
		addClave(mod,"060",declaration.getResult());
		addClave(mod,"061",declaration.getExtraCharge());
		addClave(mod,"062",declaration.getDelayInterest());
		
		//addClave(mod,"063", ??????????? ); // TODO
		
		addClave(mod,"080",declaration.getDeposit());
		addClave(mod,"081",declaration.getPayBack());
		addClave(mod,"082",declaration.getCompensate());
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

	private void addClave(Element mod, String clave, double value) {
		if (value != 0) {
			mod.addElement(CLAVE).addAttribute(NUMERO,clave).addAttribute(VALOR, formatNumber(value) );	
		}
	}

}
