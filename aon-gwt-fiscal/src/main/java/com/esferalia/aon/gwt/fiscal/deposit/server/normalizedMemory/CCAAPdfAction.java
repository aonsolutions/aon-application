package com.esferalia.aon.gwt.fiscal.deposit.server.normalizedMemory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryItem;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2Deposit;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositDescription;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2PDepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.Provinces;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public abstract class CCAAPdfAction {
	
	private static final Logger LOGGER  = Logger.getLogger(CCAAPdfAction.class.getName());

	protected Document document;

	public CCAAPdfAction(D2Deposit d2Deposit) {
		this.d2Deposit = d2Deposit;
	}

	protected  static final String[] IMAGES = new String[] {
		"/com/esferalia/aon/gwt/common/client/css/images/aon-registro-mercantil-image.png"
	};

	protected static final String TIC = "Si";

	protected abstract String getTitle();
	D2Deposit d2Deposit;

	public void initialize(ByteArrayOutputStream output, String options, Boolean isMemory) {
		this.document = new Document(PageSize.A4);
		try {
			PdfWriter.getInstance(document, output);
			document.open();
			Integer index = 0;
			if(options.substring(index, index+1).equals("T")) IDA();index++;
			if(d2Deposit.getYear() >= 2016){
				if(options.substring(index, index+1).equals("T")) AP3();index++;
			}
			if(options.substring(index, index+1).equals("T")) BA();index++;
			if(options.substring(index, index+1).equals("T")) PYG();index++;
			if(d2Deposit.getYear() < 2016){
		// TODO		if(options.substring(index, index+1).equals("T")) ECPN();index++;
			}
			if(options.substring(index, index+1).equals("T")) DM();index++;
			
			if(!isMemory){
				for(Integer pos = 0; pos < MemoryItem.getInstance().getApartadosSize(d2Deposit.getYear()); pos++){	
					if(options.substring(index, index+1).equals("T")) actionMemory(d2Deposit.getYear(), pos);index++;
				}
			}
	/*
			if(options.substring(index, index+1).equals("T")) MA();index++;
			if(options.substring(index, index+1).equals("T")) IP();index++;
			if(options.substring(index, index+1).equals("T")) CHD();
	*/	
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		document.close();
	}

	public void actionMemory(Integer year, Integer pos) throws BadElementException, MalformedURLException, DocumentException, IOException{
		switch (MemoryItem.getInstance().getApartadoName2(year, pos)) {
		case MemoryItem.ACTIVIDAD_EMPRESA: AP1();break;
		case MemoryItem.BASES_PRESENTACION: AP2();break;	
		case MemoryItem.APLICACION_RESULTADOS: AP3();break;
		case MemoryItem.NORMAS_REGISTRO: AP4();break;
		case MemoryItem.INMOVILIZADO: AP5();break;
		case MemoryItem.ACTIVOS_FINANCIEROS: AP6();break;
		case MemoryItem.PASIVOS_FINANCIEROS: AP7();break;
		case MemoryItem.FONDOS_PROPIOS: AP8();break;
		case MemoryItem.SITUACION_FISCAL: AP9();break;
	/*	case MemoryItem.INGRESOS_GASTOS: AP10();break;
		case MemoryItem.SUBVENCIONES: AP11();break;
		case MemoryItem.PARTES_VINCULANTES: AP12();break;
	*/	case MemoryItem.OTRA_INFORMACION: AP13();break;
	/*	case MemoryItem.MEDIOAMBIENTE: AP14();break;
		case MemoryItem.APLAZAMIENTOS: AP15();break;
	*/	default: break; 
		}
	}
	
	
	public void IDA() throws BadElementException, MalformedURLException, DocumentException, IOException {
		document.add(header());
		document.add(subHeader());
		
		PdfPTable identification = new PdfPTable(8);
		identification.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		identification.setWidthPercentage(100);
  
		identification.addCell(tableHeader("Identificaci\u00f3n", 8, 10));
		identification.addCell(tableCell("N.I.F. " + d2Deposit.getMap().get(D2DepositHeaderKey.IDA01010.getCode()), 4));
		
		String sa = d2Deposit.getMap().get(D2DepositHeaderKey.IDA01011.getCode());
		String sl = d2Deposit.getMap().get(D2DepositHeaderKey.IDA01012.getCode());
		String other = d2Deposit.getMap().get(D2DepositHeaderKey.IDA01013.getCode());
		if(other == null || other.equalsIgnoreCase("null")) other = "";
		identification.addCell(tableCell("SA " + getBoolText(sa) + " SL " + getBoolText(sl) + " Otras " + other, 4));
		
		if(d2Deposit.getYear() >= 2015){
			identification.addCell(tableCell("LEI", 1));
			identification.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA01009.getCode()), 1));
			identification.addCell(tableCell("Solo para las empresas que dispongan de c\u00f3digo LEI (Legal Entity Identifier)", 6));
		}
		identification.addCell(tableCell("Raz\u00f3n social " + d2Deposit.getMap().get(D2DepositHeaderKey.IDA01020.getCode()), 4));
		identification.addCell(tableCell("Domicilio social "+ d2Deposit.getMap().get(D2DepositHeaderKey.IDA01022.getCode()), 4));
		
		String province = "";
		for(Provinces pr : Provinces.values()){
			if(pr.getId().equals(d2Deposit.getMap().get(D2DepositHeaderKey.IDA01025.getCode())))
				province = pr.getName();
		}
		identification.addCell(tableCell("Municipio " + d2Deposit.getMap().get(D2DepositHeaderKey.IDA01023.getCode()), 4));
		identification.addCell(tableCell("Provincia "+ province, 4));

		identification.addCell(tableCell("C\u00f3digo postal " + d2Deposit.getMap().get(D2DepositHeaderKey.IDA01024.getCode()),4));
		identification.addCell(tableCell("Tel\u00e9fono " + d2Deposit.getMap().get(D2DepositHeaderKey.IDA01031.getCode()), 4));
		
		identification.addCell(tableCell("Direcci\u00f3n de e-mail de contacto de la empresa", 4));
		identification.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA01037.getCode()), 4));
		
		identification.addCell(tableCell(" ", 8));
		
		if(!isPymes()){
			identification.addCell(tableCell("Pertenencia a un grupo de sociedades", 4));
			identification.addCell(tableCell("Denominaci\u00f3n social",2));
			identification.addCell(tableCell("NIF",2));

			identification.addCell(tableCell("Sociedad dominante directa", 4));
			identification.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA01041.getCode()),2));
			identification.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA01040.getCode()),2));

			identification.addCell(tableCell("Sociedad dominante \u00faltima del grupo", 4));
			identification.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA01061.getCode()),2));
			identification.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA01060.getCode()),2));
		}
		document.add(new Paragraph(" "));
		document.add(identification);
		
		PdfPTable activity = new PdfPTable(8);
		activity.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		activity.setWidthPercentage(100);
        
		activity.addCell(tableHeader("Actividad", 8, 10));
		
		activity.addCell(tableCell("C\u00f3digo CNAE", 1));
		activity.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA02001.getCode()) +"-"+
				d2Deposit.getMap().get(D2DepositHeaderKey.IDA02009.getCode()), 7));

		document.add(new Paragraph(" "));
		document.add(activity);
		
		PdfPTable salariedPersonal = new PdfPTable(8);
		salariedPersonal.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		salariedPersonal.setWidthPercentage(100);
        
		salariedPersonal.addCell(tableHeader("Personal asalariado", 8, 10));

		salariedPersonal.addCell(tableCell("a) N\\u00famero medio de personas empleadas en el curso del ejercicio, por tipo de contrato y empleo con discapacidad", 8));
		
		salariedPersonal.addCell(tableCell(" ", 2));
		salariedPersonal.addCell(tableCell("Ejercicio " + d2Deposit.getYear(), 2));
		salariedPersonal.addCell(tableCell("Ejercicio " + (d2Deposit.getYear() - 1), 2));
		salariedPersonal.addCell(tableCell(" ", 2));
		
		salariedPersonal.addCell(tableCell("FIJO", 2));
		salariedPersonal.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA04001.getCode()), 2));
		salariedPersonal.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA040019.getCode()), 2));
		salariedPersonal.addCell(tableCell(" ", 2));
		
		salariedPersonal.addCell(tableCell("NO FIJO", 2));
		salariedPersonal.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA04002.getCode()), 2));
		salariedPersonal.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA040029.getCode()), 2));
		salariedPersonal.addCell(tableCell(" ", 2));
		
		salariedPersonal.addCell(tableCell("Del cual: Personas empleadas con discapacidad mayor o igual al 33% (o calificaci\u00f3n equivalente local):", 8));
		
		salariedPersonal.addCell(tableCell(" ", 2));
		salariedPersonal.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA04010.getCode()), 2));
		salariedPersonal.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA040109.getCode()), 2));
		salariedPersonal.addCell(tableCell(" ", 2));
		
		salariedPersonal.addCell(tableCell("b) Personal asalariado al t\u00e9rmino del ejercicio, por tipo de contrato y por sexo", 8));
		
		salariedPersonal.addCell(tableCell(" ", 2));
		salariedPersonal.addCell(tableCell("Ejercicio " + d2Deposit.getYear(), 2));
		salariedPersonal.addCell(tableCell("Ejercicio " + (d2Deposit.getYear() - 1), 2));
		salariedPersonal.addCell(tableCell(" ", 2));
		
		salariedPersonal.addCell(tableCell(" ", 2));
		salariedPersonal.addCell(tableCell("Hombres", 1));
		salariedPersonal.addCell(tableCell("Mujeres", 1));
		salariedPersonal.addCell(tableCell("Hombres", 1));
		salariedPersonal.addCell(tableCell("Mujeres", 1));
		salariedPersonal.addCell(tableCell(" ", 2));
		
		salariedPersonal.addCell(tableCell("FIJO", 2));
		salariedPersonal.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA04120.getCode()), 1));
		salariedPersonal.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA04121.getCode()), 1));
		salariedPersonal.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA041209.getCode()), 1));
		salariedPersonal.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA041219.getCode()), 1));
		salariedPersonal.addCell(tableCell(" ", 2));
		
		salariedPersonal.addCell(tableCell("NO FIJO", 2));
		salariedPersonal.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA04122.getCode()), 1));
		salariedPersonal.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA04123.getCode()), 1));
		salariedPersonal.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA041229.getCode()), 1));
		salariedPersonal.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA041239.getCode()), 1));
		salariedPersonal.addCell(tableCell(" ", 2));

		document.add(new Paragraph(" "));
		document.add(salariedPersonal);
		
		PdfPTable accountPresentation = new PdfPTable(8);
		accountPresentation.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		accountPresentation.setWidthPercentage(100);
        
		accountPresentation.addCell(tableHeader("Presentaci\u00f3n de cuentas", 8, 10));
		
		accountPresentation.addCell(tableCell(" ", 4));
		accountPresentation.addCell(tableCell("Ejercicio " + d2Deposit.getYear(),2));
		accountPresentation.addCell(tableCell("Ejercicio " + (d2Deposit.getYear() - 1),2));
		
		accountPresentation.addCell(tableCell("Fecha de inicio a la que van referidas las cuentas", 4));
		accountPresentation.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA01102.getCode()),2));
		accountPresentation.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA011029.getCode()),2));
		
		accountPresentation.addCell(tableCell("Fecha de cierre a la que van referidas las cuentas", 4));
		accountPresentation.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA01101.getCode()),2));
		accountPresentation.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA011019.getCode()),2));
		
		accountPresentation.addCell(tableCell("N\u00famero de p\u00e1ginas presentadas al dep\u00f3sito", 4));
		accountPresentation.addCell(tableCell(" ", 1));		
		accountPresentation.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA01901.getCode()),3));
		
		accountPresentation.addCell(tableCell("En caso de no figurar consignadas cifras en alguno de los ejercicios, indique la causa", 4));
		accountPresentation.addCell(tableCell(" ", 1));
		accountPresentation.addCell(tableCell(d2Deposit.getMap().get(D2DepositHeaderKey.IDA01903.getCode()),3));

		document.add(new Paragraph(" "));
		document.add(accountPresentation);
		if(!isPymes()){
			PdfPTable unity = new PdfPTable(8);
			unity.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
			unity.setWidthPercentage(100);
	        
			unity.addCell(tableHeader("Unidades", 8, 10));
			
			String euros = d2Deposit.getMap().get(D2DepositHeaderKey.IDA09001.getCode());
			String milesEuros = d2Deposit.getMap().get(D2DepositHeaderKey.IDA09002.getCode());
			String millonesEuros = d2Deposit.getMap().get(D2DepositHeaderKey.IDA09003.getCode());
			
			unity.addCell(tableCell("Euros " + getBoolText(euros) + " Miles de euros " + getBoolText(milesEuros)
				+" Millones de euros " + getBoolText(millonesEuros), 8));

			document.add(new Paragraph(" "));
			document.add(unity);
		} else {
			PdfPTable microEnterprise = new PdfPTable(8);
			microEnterprise.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
			microEnterprise.setWidthPercentage(100);
	        
			microEnterprise.addCell(tableHeader("Microempresas", 8, 10));
			microEnterprise.addCell(tableCell("Marque con una X si la empresa ha optado por la adopci\u00f3n conjunta de los criterios espec\u00edficos, aplicables por microempresas, previstos en el Plan General de Contabilidad de PYMES (6)",7));
			microEnterprise.addCell(tableCell(" ", 1));
			microEnterprise.addCell(tableCell(getBoolText(d2Deposit.getMap().get(D2DepositHeaderKey.IDA01902.getCode())), 8));

			document.add(new Paragraph(" "));
			document.add(microEnterprise);
		}
		document.newPage();
	}
	
	public void BA() throws BadElementException, MalformedURLException, DocumentException, IOException{
		BA1();
		BA2();
	}
	
	public void BA1() throws BadElementException, MalformedURLException, DocumentException, IOException {
		document.add(header());
		document.add(subHeader());
		
		D2DepositHeaderKey[][] keys;
		if (d2Deposit.getType().equalsIgnoreCase("PYMES"))
			keys = D2PDepositConstants.BALANCE_ACTIVE_PYMES_KEYS;
		else keys = D2DepositConstants.BA_ABREVIATE_KEYS_1;
		
		three("Balance: Activo", keys, null);
		document.newPage();
	}

	public void BA2() throws BadElementException, MalformedURLException, DocumentException, IOException {
		document.add(header());
		document.add(subHeader());
		
		D2DepositHeaderKey[][] keys;
		D2DepositHeaderKey[][] keys2;
		if (isPymes()){
			if(d2Deposit.getYear().equals(2014))
				keys = D2PDepositConstants.BALANCE_PASIVE_PYMES_KEYS_1;
			else keys = D2PDepositConstants.BALANCE_PASIVE_PYMES_KEYS_1_2015;
			keys2 = D2PDepositConstants.BALANCE_PASIVE_PYMES_KEYS_2;
		}
		else{
			if(d2Deposit.getYear().equals(2014))
				keys = D2DepositConstants.BA_ABREVIATE_KEYS_2;
			else keys = D2DepositConstants.BA_ABREVIATE_KEYS_2_2015;
			keys2 = D2DepositConstants.BA_ABREVIATE_KEYS_3;
		}
		
		three("Balance: Patrimonio Neto y Pasivo", keys, keys2);
		document.newPage();
	}
	
	public void PYG() throws BadElementException, MalformedURLException, DocumentException, IOException {
		document.add(header());
		document.add(subHeader());
		
		D2DepositHeaderKey[][] keys;
		if (d2Deposit.getType().equalsIgnoreCase("PYMES"))
			keys = D2PDepositConstants.PYG_PYMES_KEYS;
		else keys = D2DepositConstants.PYG_ABREVIATE_KEYS;

		three("(Debe)/ Haber", keys, null);
		document.newPage();
	}

	public void DM() throws BadElementException, MalformedURLException, DocumentException, IOException {
		document.add(header());
		document.add(subHeader());
		
		PdfPTable dm = new PdfPTable(1);
		dm.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		dm.setWidthPercentage(100);
        
		dm.addCell(tableHeader("Declaraci\u00f3n medioambiental", 1, 10));
		
		String as = d2Deposit.getMap().get(D2DepositHeaderKey.IMA8099000.getCode());
		String bs = d2Deposit.getMap().get(D2DepositHeaderKey.IMA8099010.getCode());
		Boolean a = as != null && as.equals("1");
		Boolean b = bs != null && bs.equals("1");
	
		String text1 = "Los abajo firmantes, como Administradores de la Sociedad citada,"
				+ " manifiestan que en la contabilidad correspondiente a las presentes "
				+ "cuentas anuales NO existe ninguna partida de naturaleza medioambiental"
				+ " que deba ser inclu\u00edda de acuerdo a la norma de elaboraci\u00f3n '4º Cuentas"
				+ " anuales abreviadas' en su punto 5, de la tercera parte del Plan General"
				+ " de Contabilidad (Real Decreto 1514/2007 de 16 de Noviembre).";
		String text2 = "Los abajo firmantes, como Administradores de la Sociedad citada,"
				+ " manifiestan que en la contabilidad correspondiente a las presentes"
				+ " cuentas anuales SI existen partidas de naturaleza medioambiental,"
				+ " y han sido inclu\u00eddas en un Apartado adiciones de la Memoria de"
				+ " acuerdo a la norma de elaboraci\u00f3n '4º Cuentas anuales abreviadas'"
				+ " en su punto 5, de la tercera parte del Plan General de Contabilidad "
				+ "(Real Decreto 1514/2007 de 16 de Noviembre).";
		
		dm.addCell(tableCell(text1, 1));
		dm.addCell(tableCell((a ? TIC : "-"), 1));
		dm.addCell(tableCell(text2, 1));
		dm.addCell(tableCell((b ? TIC : "-"), 1));
		document.newPage();
	}
	
	public void AP1() throws BadElementException, MalformedURLException, DocumentException, IOException{
		freeText("Apartado 1 - Actividad de la empresa", D2DepositKey.MAT19019001);
	}
	
	public void AP2() throws BadElementException, MalformedURLException, DocumentException, IOException{
		freeText("Apartado 2 - Bases de presentaci\u00f3n de las cuentas anuales", D2DepositKey.MAT29029001);
	}
	
	public void AP3() throws BadElementException, MalformedURLException, DocumentException, IOException{
		if(d2Deposit.getYear() < 2016){
			AP3A();
			AP3B();
		} else {
			AP3B();
		}
	}
	
	public void AP3A() throws BadElementException, MalformedURLException, DocumentException, IOException{
		freeText("Apartado 3 - Aplicaci\u00f3n de resultados", D2DepositKey.MAT39039001);
	}
	
	public void AP3B() throws BadElementException, MalformedURLException, DocumentException, IOException{
		document.add(header());
		document.add(subHeader());
		
		D2DepositKey[][] keys;
		D2DepositKey[][] keys2;
		if (d2Deposit.getType().equalsIgnoreCase("PYMES")){
			keys = D2PDepositConstants.MRN_PYMES_KEYS_1;
			keys2 = D2PDepositConstants.MRN_PYMES_KEYS_2;
		}
		else{
			keys = D2DepositConstants.MRN_ABREVIATE_KEYS_1;
			keys2 = D2DepositConstants.MRN_ABREVIATE_KEYS_2;		
		}
		two("BASES DE REPARTO", keys, 10);
		two("APLICACI\u00d3N A", keys2, 10);
		
		if(d2Deposit.getYear() >= 2016) {
			D2DepositKey[][] keys3 = D2DepositConstants.MRN_ABREVIATE_KEYS_3;
			two("INFORMACI\u00d3N SOBRE EL PER\u00cdODO MEDIO DE PAGO A PROVEEDORES DURANTE EL EJERCICIO", keys3, 10);
		}	
		document.newPage();
	}
	
	public void AP4() throws BadElementException, MalformedURLException, DocumentException, IOException{
		freeText("Apartado " + (d2Deposit.getYear() < 2016 ? "4" : "3") + " - Normas de registro y valoraci\u00f3n", D2DepositKey.MAT49049001);
	}
	
	public void AP5() throws BadElementException, MalformedURLException, DocumentException, IOException{
		AP5A();
		AP5B();
	}
	
	public void AP5A() throws BadElementException, MalformedURLException, DocumentException, IOException{
		freeText("Apartado " + (d2Deposit.getYear() < 2016 ? "5" : "4") + " - Inmovilizado material, intangible, e inversiones inmobiliarias", D2DepositKey.MAT59059001);
	}
	
	public void AP5B() throws BadElementException, MalformedURLException, DocumentException, IOException{
		document.add(header());
		document.add(subHeader());
		
		D2DepositKey[][] keys = D2DepositConstants.MRN5_ABREVIATE_PYMES_KEYS_1;
		D2DepositKey[][] keys2 = D2DepositConstants.MRN5_ABREVIATE_PYMES_KEYS_2;
		D2DepositKey[][] keys3 = D2DepositConstants.MRN5_ABREVIATE_PYMES_KEYS_3;
		
		three("Estado de movimientos del inmovilizado material, intangible e inversiones inmobiliarias del ejercicio actual",
				null, null, keys, "Inmovilizado Intangible", "Inmovilizado Material", "Inversiones Inmobiliarias", 8);
		
		three("Estado de movimientos del inmovilizado material, intangible e inversiones inmobiliarias del ejercicio anterior",
				null, null, keys2, "Inmovilizado Intangible", "Inmovilizado Material", "Inversiones Inmobiliarias", 8);

		one("Arrendamientos financieros y otras operaciones de naturaleza similar sobre activos no corrientes", keys3, "Total Contratos", 8);
		document.newPage();
	}
	
	public void AP6() throws BadElementException, MalformedURLException, DocumentException, IOException{
		AP6A();
		// TODO
//		if(d2Deposit.getYear() < 2016) AP6B();
//		AP6C();
	}
	
	public void AP6A() throws BadElementException, MalformedURLException, DocumentException, IOException{
		freeText("Apartado " + (d2Deposit.getYear() < 2016 ? "6" : "5") + " - Activos financieros", D2DepositKey.MAT69069001);
	}
	
	public void AP7() throws BadElementException, MalformedURLException, DocumentException, IOException{
		AP7A();
		AP7B();
	}
	
	public void AP7A() throws BadElementException, MalformedURLException, DocumentException, IOException{
		freeText("Apartado " + (d2Deposit.getYear() < 2016 ? "7" : "6") + " - Pasivos financieros", D2DepositKey.MAT79079001);	
	}
	
	public void AP7B() throws BadElementException, MalformedURLException, DocumentException, IOException{
		document.add(header());
		document.add(subHeader());
		document.setPageSize(PageSize.A4_LANDSCAPE);
		D2DepositKey[][] keys, keys2, keys3;
		if (d2Deposit.getType().equalsIgnoreCase("PYMES")){
			keys = D2PDepositConstants.MRN7_PYMES_KEYS_1;
			keys2 = D2PDepositConstants.MRN7_PYMES_KEYS_2;
			keys3 = D2PDepositConstants.MRN7_PYMES_KEYS_3;
		} else {
			keys = D2DepositConstants.MRN7_ABREVIATE_KEYS_1;
			keys2 = D2DepositConstants.MRN7_ABREVIATE_KEYS_2;
			keys3 = D2DepositConstants.MRN7_ABREVIATE_KEYS_3;
		}
		D2DepositKey[][] keys4 = D2DepositConstants.MRN7_ABREVIATE_KEYS_4;

	/*	if(d2Deposit.getYear() < 2016){
			// 3 2
			special(2, pageMaxNumber, 8, new String[]{"Pasivos financieros a largo plazo", "Deudas con entidades de cr\u00e9dito",
				"Obligaciones y otros valores negociables", "Derivados y otros", "TOTAL"},
				new String[]{"", current, previous, current, previous, current, previous, current, previous}, keys, 3, 2, 4);
			sheet.createRow(rowCount++);
		
			// 3 2
			special(2, pageMaxNumber, 8, new String[]{"Pasivos financieros a corto plazos", "Deudas con entidades de cr\u00e9dito",
				"Obligaciones y otros valores negociables", "Derivados y otros", "TOTAL"},
				new String[]{"", current, previous, current, previous, current, previous, current, previous}, keys2, 3, 2, 4);
			sheet.createRow(rowCount++);
		}
		// 1
		general(pageMaxNumber, 7, new String[]{"Vencimiento de las deudas al cierre del ejercicio"+getD2Deposit().getYear(),
				"Uno", "Dos", "Tres", "Cuatro", "Cinco", "M\u00e1s de 5", "TOTAL"}, keys3, 1, 4, null);
		sheet.createRow(rowCount++);
		// 3
		if(d2Deposit.getYear() < 2016){
			general(pageMaxNumber, 3, new String[]{"Lineas de descuento y p\u00f3lizas al cierre del ejercicio"+ getD2Deposit().getYear(),
				"L\u00ed�mite concedido", "Dispuesto", "Disponible"},keys4, 3, 4, null);
		}
*/
	}
	
	public void AP8() throws BadElementException, MalformedURLException, DocumentException, IOException{
		freeText("Apartado " + (d2Deposit.getYear() < 2016 ? "8" : "7") + " - Fondos propios", D2DepositKey.MAT89089001);	
	}
	
	public void AP9() throws BadElementException, MalformedURLException, DocumentException, IOException{
		freeText("Apartado " + (d2Deposit.getYear() < 2016 ? "9" : "8") + " - Situaci\u00f3n fiscal", D2DepositKey.MAT99099001);	
	}
	
	public void AP13() throws BadElementException, MalformedURLException, DocumentException, IOException{
		AP13A();
		AP13B();
	}
	
	public void AP13A() throws BadElementException, MalformedURLException, DocumentException, IOException{
		freeText("Apartado " + (d2Deposit.getYear() < 2016 ? "13" : "10") + " - Otra informaci\u00f3n", D2DepositKey.MAT139139001);	
	}
	
	public void AP13B() throws BadElementException, MalformedURLException, DocumentException, IOException{
		document.add(header());
		document.add(subHeader());

		D2DepositKey[][] keys = null;
		if(d2Deposit.getYear() < 2016){
			keys = D2DepositConstants.MRN13_ABREVIATE_KEYS;
		} else {
			keys = D2DepositConstants.MRN13_ABREVIATE_KEYS_2016;
		}

		two("N\u00famero medio de personas empleadas en el curso del ejercicio, por categor\u00edas (adaptadas a la CNO-11)", keys, 8);
	}
	
	private void one(String title, D2DepositKey[][] keys, String column, Integer size) throws DocumentException {
		PdfPTable table = new PdfPTable(8);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		table.setWidthPercentage(100);
        
		table.addCell(tableHeader(title, 7, size));
		table.addCell(tableHeader(column, 1, size));
		
		for (D2DepositKey[] innerKeys : keys) {
			String description = getDescription(D2DepositDescription.DESCRIPTION_MAP.get(innerKeys[0]));
			table.addCell(tableCell(description, 6));
			table.addCell(tableCell(innerKeys[0].getCode(), 1));
			table.addCell(tableCell(number(d2Deposit.getMap().get(innerKeys[0].getCode())), 1));
		}
		
		document.add(new Paragraph(" "));
		document.add(table);
	}
	
	private void two(String title, D2DepositKey[][] keys, Integer size) throws DocumentException {
		PdfPTable table = new PdfPTable(8);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		table.setWidthPercentage(100);
        
		table.addCell(tableHeader(title, 5, size));
		table.addCell(tableHeader(" ", 1, size));
		table.addCell(tableHeader("Ejercicio " + d2Deposit.getYear(), 1, size));
		table.addCell(tableHeader("Ejercicio " + (d2Deposit.getYear() - 1), 1, size));
		
		for (D2DepositKey[] innerKeys : keys) {
			String description = getDescription(D2DepositDescription.DESCRIPTION_MAP.get(innerKeys[0]));
			table.addCell(tableCell(description, 4));
			table.addCell(tableCell(" ", 1));
			table.addCell(tableCell(innerKeys[0].getCode(), 1));
			table.addCell(tableCell(number(d2Deposit.getMap().get(innerKeys[0].getCode())), 1));
			table.addCell(tableCell(number(d2Deposit.getMap().get(innerKeys[1].getCode())), 1));
		}
		
		document.add(new Paragraph(" "));
		document.add(table);
	}
	
	private void three(String title, D2DepositHeaderKey[][] keys, D2DepositHeaderKey[][] keys2) throws DocumentException {
		three(title, keys, keys2, null, "notas", "Ejercicio " + d2Deposit.getYear(), "Ejercicio " + (d2Deposit.getYear() -1), 10);
	}
	
	private void three(String title, D2DepositHeaderKey[][] keys, D2DepositHeaderKey[][] keys2, 
			D2DepositKey[][] keys3, String column1, String column2, String column3, Integer size) throws DocumentException {
		PdfPTable table = new PdfPTable(8);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		table.setWidthPercentage(100);
        
		table.addCell(tableHeader(title, 4, size));
		table.addCell(tableHeader(" ", 1, size));
		table.addCell(tableHeader(column1, 1, size));
		table.addCell(tableHeader(column2, 1, size));
		table.addCell(tableHeader(column3, 1, size));
		if(keys != null) {
			for (D2DepositHeaderKey[] innerKeys : keys) {
				String description = getDescription(D2DepositDescription.DESCRIPTION_MAP_HEADER.get(innerKeys[0]));
				table.addCell(tableCell(description, 3));
				table.addCell(tableCell(" ", 1));
				table.addCell(tableCell(innerKeys[0].getCode(), 1));
				table.addCell(tableCell(d2Deposit.getMap().get(innerKeys[2].getCode()), 1));
				table.addCell(tableCell(number(d2Deposit.getMap().get(innerKeys[0].getCode())), 1));
				table.addCell(tableCell(number(d2Deposit.getMap().get(innerKeys[1].getCode())), 1));
			}
		}
		if(keys2 != null) {
			for (D2DepositHeaderKey[] innerKeys : keys2) {
				String description = getDescription(D2DepositDescription.DESCRIPTION_MAP_HEADER.get(innerKeys[0]));
				table.addCell(tableCell(description, 3));
				table.addCell(tableCell(" ", 1));
				table.addCell(tableCell(innerKeys[0].getCode(), 1));
				table.addCell(tableCell(d2Deposit.getMap().get(innerKeys[2].getCode()), 1));
				table.addCell(tableCell(number(d2Deposit.getMap().get(innerKeys[0].getCode())), 1));
				table.addCell(tableCell(number(d2Deposit.getMap().get(innerKeys[1].getCode())), 1));
			}
		}
		if(keys3 != null) {
			for (D2DepositKey[] innerKeys : keys3) {
				String description = getDescription(D2DepositDescription.DESCRIPTION_MAP.get(innerKeys[0]));
				table.addCell(tableCell(description, 3));
				table.addCell(tableCell(" ", 1));
				table.addCell(tableCell(innerKeys[0].getCode(), 1));
				table.addCell(tableCell(d2Deposit.getMap().get(innerKeys[2].getCode()), 1));
				table.addCell(tableCell(number(d2Deposit.getMap().get(innerKeys[0].getCode())), 1));
				table.addCell(tableCell(number(d2Deposit.getMap().get(innerKeys[1].getCode())), 1));
			}
		}

		document.add(new Paragraph(" "));
		document.add(table);
	}
	
	private String number(String data) {
		if(data != null) {
			try {
				return Double.toString(AonMathUtils.round(Double.parseDouble(data)));
			} catch (Exception e) {
				return data;
			}
		}else return "0.0";
	}
	
	private void freeText(String title, D2DepositKey key) throws BadElementException, MalformedURLException, DocumentException, IOException {
		document.add(header());
		document.add(subHeader());
		
		PdfPTable free = new PdfPTable(1);
		free.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		free.setWidthPercentage(100);
        
		free.addCell(tableHeader(title, 1, 10));
		
		String text = d2Deposit.getMap().get(key.getCode());
		if(text == null) text = "";
		
		free.addCell(tableCell(text, 1));
		
		document.add(new Paragraph(" "));
		document.add(free);
		
		document.newPage();
	}

	private PdfPCell tableHeader(String text, Integer colspan, Integer size) {
		Paragraph p = new Paragraph(text, getFont1(size));
		p.setAlignment(Element.ALIGN_CENTER);
		PdfPCell cell = new PdfPCell(p);
		cell.setBackgroundColor(new BaseColor(Integer.valueOf("fa", 16 ),Integer.valueOf("58", 16 ),Integer.valueOf("58", 16 )));
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setColspan(colspan);
		return cell;
	}
	
	private PdfPCell tableCell(String text, Integer colspan) {
		PdfPCell cell = new PdfPCell(new Paragraph(text, getFont3()));
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setColspan(colspan);
		return cell;
	}
	
	private PdfPTable header() throws BadElementException, MalformedURLException, IOException{
		InputStream inputStream = CCAAPdfAction.class.getResourceAsStream(IMAGES[0]);
		byte[] image = AonIOUtils.toByteArray(inputStream);

		PdfPTable header = new PdfPTable(3);
        float[] medidaCeldas = {0.5f, 4f, 1f};
		try {
			header.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
        header.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        header.setWidthPercentage(100);
      
        Image i = Image.getInstance(image);
        PdfPCell c1 = new PdfPCell(i, false);
        c1.setBorder(PdfPCell.NO_BORDER);
        header.addCell(c1);
        
		header.addCell(headerCell(getTitle(), 10));
		
		PdfPTable t = new PdfPTable(1);
		t.addCell(headerCell(d2Deposit.getMap().get(D2DepositConstants.DEPOSIT_TYPE),10));
		t.addCell(headerCell(d2Deposit.getYear().toString(), 10));
		header.addCell(t);
	
		return header;
	}
	
	private Paragraph subHeader() {
		Paragraph p = new Paragraph(d2Deposit.getCif()  + "-" +  d2Deposit.getRazonSocial(), getFont2());
		p.setAlignment(Element.ALIGN_CENTER);
		return p;
	}
	
	private PdfPCell headerCell(String text, Integer size) {
		Paragraph p = new Paragraph(text, getFont1(size));
		p.setAlignment(Element.ALIGN_CENTER);
		PdfPCell cell = new PdfPCell(p);
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setBackgroundColor(new BaseColor(Integer.valueOf("fa", 16 ),Integer.valueOf("58", 16 ),Integer.valueOf("58", 16 )));
		return cell;
	}

	public static Font getTitleFont(){
		Font font = new Font();
		font.setSize(16);
		font.setStyle(Font.BOLD);
		return font;
	}
	
	public static Font getFont2(){
		Font font = new Font();
		font.setSize(13);
		font.setStyle(Font.BOLD);
		return font;
	}
	
	public static Font getFont1(Integer size){
		Font font = new Font();
		font.setSize(size);
		font.setStyle(Font.BOLD);
		font.setColor(BaseColor.WHITE);
		return font;
	}
	
	public static Font getFont3(){
		Font font1 = new Font();
		font1.setSize(8);
		return font1;
	}
	
	public static Font getFont4(){
		Font font1 = new Font();
		font1.setSize(12);
		return font1;
	}
	
	public static PdfPCell emptyCell() {
		PdfPCell cell = new PdfPCell(new Phrase("",getFont2()));
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}
	
	public static PdfPCell stringCell(String str) {
		PdfPCell cell = new PdfPCell(new Phrase(str,getFont2()));
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}
	
	public static PdfPCell stringCell(String str, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(str,font));
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}
	
	public static PdfPCell boldCell(String str, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(str,font));
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}
	
	private String getDescription(String desc) {
		if(desc.contains("@")){
			Integer pos = desc.indexOf("@");
			return desc.substring(0, pos) + d2Deposit.getYear() + desc.substring(pos+1);
		} else if(desc.contains("¬")){
			Integer pos = desc.indexOf("¬");
			return desc.substring(0, pos) + (d2Deposit.getYear()-2) + desc.substring(pos+1);
		} else if(desc.contains("#")){
			Integer pos = desc.indexOf("#");
			return desc.substring(0, pos) + (d2Deposit.getYear()-1) + desc.substring(pos+1);

		}else return desc;	
	}
	
	private String getBoolText(String a) {
		if(a != null && !a.equalsIgnoreCase("null")
				&& (a.equals("1") || a.equalsIgnoreCase("true")))
			return TIC;
		else return "-";
	}
	private Boolean isPymes() {
		return d2Deposit.getMap().get(D2DepositConstants.DEPOSIT_TYPE).equalsIgnoreCase("Pymes");
	}
	
}
