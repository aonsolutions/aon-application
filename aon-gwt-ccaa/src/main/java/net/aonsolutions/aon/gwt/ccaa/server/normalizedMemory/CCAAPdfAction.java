package net.aonsolutions.aon.gwt.ccaa.server.normalizedMemory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2Deposit;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositDescription;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositFooterKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2PDepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.Provinces;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
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

import net.aonsolutions.aon.gwt.ccaa.shared.MemoryItem;

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
			if(d2Deposit.getYear() >= 2020) {
				if(options.substring(index, index+1).equals("T"))  CVA();index++;
			}
			
	    	if(d2Deposit.getYear() >= 2017) {
	    		if(options.substring(index, index+1).equals("T")) ITR();index++;
	    	}
	    	
	    	if(d2Deposit.getYear() >= 2018) {
	    		if(options.substring(index, index+1).equals("T")) SRA();index++;
	    	}
			if(options.substring(index, index+1).equals("T")) BA();index++;
			if(options.substring(index, index+1).equals("T")) PYG();index++;
			if(d2Deposit.getYear() < 2016){
				if(options.substring(index, index+1).equals("T")) ECPN();index++;
			}
			if(options.substring(index, index+1).equals("T")) DM();index++;
			
			if(!isMemory){
				for(Integer pos = 0; pos < MemoryItem.getInstance().getApartadosSize(d2Deposit.getYear()); pos++){	
					if(options.substring(index, index+1).equals("T")) actionMemory(d2Deposit.getYear(), pos);index++;
				}
			}
	
			if(options.substring(index, index+1).equals("T")) MA();index++;
			if(options.substring(index, index+1).equals("T")) IP();index++;
			if(options.substring(index, index+1).equals("T")) CHD();	
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
	*/	case MemoryItem.PARTES_VINCULANTES: AP12();break;
		case MemoryItem.OTRA_INFORMACION: AP13();break;
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
		String str = "SA";
		if(getBool(sl)) str = "SL";
		else if(AonStringUtils.isNotBlank((other))) str = "Otras " + other;
		
		identification.addCell(tableCell(str, 4));
		
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
			
			String unit = "Euros";
			if(getBool(milesEuros)) unit = "Miles de Euros";
			else if(getBool(millonesEuros)) unit = "Millones de Euros";
			unity.addCell(tableCell(unit, 8));

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
	
	public void CVA() throws BadElementException, MalformedURLException, DocumentException, IOException {
		document.add(header());
		document.add(subHeader());
		
		PdfPTable cva1 = new PdfPTable(8);
		cva1.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		cva1.setWidthPercentage(100);
  
		cva1.addCell(tableHeader("Declaraci\u00f3n Covid 19", 8, 10));
		cva1.addCell(tableCell("Sociedad: " + d2Deposit.getMap().get(D2DepositHeaderKey.IDA01020.getCode()), 4));
		cva1.addCell(tableCell("NIF: " + d2Deposit.getMap().get(D2DepositHeaderKey.IDA01010.getCode()), 4));
		cva1.addCell(tableCell("Domicilio Social: " + d2Deposit.getMap().get(D2DepositHeaderKey.IDA01022.getCode()), 8));
		cva1.addCell(tableCell("Municipio: " + d2Deposit.getMap().get(D2DepositHeaderKey.IDA01023.getCode()), 4));
		cva1.addCell(tableCell("Provincia: " + d2Deposit.getMap().get(D2DepositHeaderKey.IDA01025.getCode()), 4));
		document.add(new Paragraph(" "));
		document.add(cva1);
		
		PdfPTable cva2 = new PdfPTable(8);
		cva2.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		cva2.setWidthPercentage(100);
  
		cva2.addCell(tableHeader("Medidas laborales aplicadas a la empresa", 8, 10));
		String CVA8220000 = d2Deposit.getMap().get(D2DepositHeaderKey.CVA8220000.getCode());
		cva2.addCell(tableCell("Solicitud de ERTE durante el ejercicio y motivado por la pandemia: " + getBoolText(CVA8220000), 8));
	
		String CVA8220010 = d2Deposit.getMap().get(D2DepositHeaderKey.CVA8220010.getCode());
		String CVA8220010Str = "";
		if("1".equals(CVA8220010)) CVA8220010Str = "Por causa de fuerza mayor";
		else if("2".equals(CVA8220010)) CVA8220010Str = "Por causas técnicas-económicas-organizativas";
		else if("3".equals(CVA8220010)) CVA8220010Str = "Otras causas";
		cva2.addCell(tableCell("Ha sido motivado: " + CVA8220010Str, 4));

 		String CVA8220020 = d2Deposit.getMap().get(D2DepositHeaderKey.CVA8220020.getCode());
		String CVA8220020Str = "";
		if("1".equals(CVA8220020)) CVA8220020Str = "Suspensión de contratos";
		else if("2".equals(CVA8220020)) CVA8220020Str = "Reducción de jornada";
		else if("3".equals(CVA8220020)) CVA8220020Str = "Ambos";
		cva2.addCell(tableCell("Ha determinado: " + CVA8220020Str, 4));

		String CVA8220030 = d2Deposit.getMap().get(D2DepositHeaderKey.CVA8220030.getCode());
		cva2.addCell(tableCell("Número de trabajadores en plantilla antes del ERTE: " + CVA8220030, 4));
		
		String CVA8220035 = d2Deposit.getMap().get(D2DepositHeaderKey.CVA8220035.getCode());
		cva2.addCell(tableCell("Número de trabajadores afectados por el ERTE: " + CVA8220035, 4));

		String CVA8220040 = d2Deposit.getMap().get(D2DepositHeaderKey.CVA8220040.getCode());
		cva2.addCell(tableCell("Fecha Inicio: " + CVA8220040, 4));
		
		String CVA8220050 = d2Deposit.getMap().get(D2DepositHeaderKey.CVA8220050.getCode());
		cva2.addCell(tableCell("Fecha Fin: " + CVA8220050, 4));

		cva2.addCell(tableCell("Permiso retribuido recuperable (Real Decreto-Ley 10/2020, de 29 de marzo)", 8));
		
		String CVA8220060 = d2Deposit.getMap().get(D2DepositHeaderKey.CVA8220060.getCode());
		cva2.addCell(tableCell("Porcentaje de personal acogido a permiso retribuido recuperable: " + CVA8220060, 4));
		
		String CVA8220065 = d2Deposit.getMap().get(D2DepositHeaderKey.CVA8220065.getCode());
		cva2.addCell(tableCell("Duración (Número de días): " + CVA8220065, 4));

		cva2.addCell(tableCell("Baja Laboral por el CORONAVIRUS", 4));
		String CVA8220070 = d2Deposit.getMap().get(D2DepositHeaderKey.CVA8220070.getCode());
		cva2.addCell(tableCell("Porcentaje de personal fijo afectado: " + CVA8220070, 4));
		
		document.add(new Paragraph(" "));
		document.add(cva2);

		
		PdfPTable cva3 = new PdfPTable(8);
		cva3.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		cva3.setWidthPercentage(100);
  
		cva3.addCell(tableHeader("Alquileres (artículos 1 al 15 Real Decreto-Ley11/2020)", 8, 10));
			
		String CVA8220080 = d2Deposit.getMap().get(D2DepositHeaderKey.CVA8220080.getCode());
		String CVA8220080Str = "";
		if("0".equals(CVA8220080)) CVA8220080Str = "No aplica";
		else if("1".equals(CVA8220080)) CVA8220080Str = "Rebaja de rentas a los arrendatarios";
		else if("2".equals(CVA8220080)) CVA8220080Str = "Reestructuración de deudas";
		else if("3".equals(CVA8220080)) CVA8220080Str = "Ambos";
		else if("4".equals(CVA8220080)) CVA8220080Str = "Ninguno de los anteriores";
		cva3.addCell(tableCell("Alquileres a terceros (Grandes arrendadores). Ha concedido: " + CVA8220080Str, 8));

		String CVA8220090 = d2Deposit.getMap().get(D2DepositHeaderKey.CVA8220090.getCode());
		cva3.addCell(tableCell("Pequeños arrendadores. Ha concedido moratorias voluntarias a los arrendatarios: " + getBoolText(CVA8220090), 8));

		String CVA8220100 = d2Deposit.getMap().get(D2DepositHeaderKey.CVA8220100.getCode());
		cva3.addCell(tableCell("Ha recibido ayudas financieras públicas(incluidos avales) al alquiler del local de negocios: " + getBoolText(CVA8220100), 8));

		document.add(new Paragraph(" "));
		document.add(cva3);
		
		PdfPTable cva4 = new PdfPTable(8);
		cva4.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		cva4.setWidthPercentage(100);
  
		cva4.addCell(tableHeader("Avales ICO", 8, 10));
		cva4.addCell(tableCell("Importe del aval concedido por el ICO en aplicación de los establecido en los articulos 29 y 30 del Real Decreto-Ley 8/2020, de 17 de marzo", 8));

		String CVA8220110 = d2Deposit.getMap().get(D2DepositHeaderKey.CVA8220110.getCode());
		cva4.addCell(tableCell("Cantidad: " + CVA8220110, 8));

		String CVA8220120 = d2Deposit.getMap().get(D2DepositHeaderKey.CVA8220120.getCode());
		cva4.addCell(tableCell("¿Que porcentaje representa el importe concedido sobre el importe total solicitado? " + CVA8220120, 8));
		
		document.add(new Paragraph(" "));
		document.add(cva4);
		

		PdfPTable cva5 = new PdfPTable(8);
		cva5.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		cva5.setWidthPercentage(100);
 
		cva5.addCell(tableHeader("Ayudas públicas", 8, 10));
		cva5.addCell(tableCell("Describir el plan o programa al que se acoge, el concedente y el sistema(avales, moratoria, aplazamiento, interés bonificado etc.)", 8));

		document.add(new Paragraph(" "));
		document.add(cva5);

		String CVA8220130 = d2Deposit.getMap().get(D2DepositHeaderKey.CVA8220130.getCode());

		document.add(new Paragraph(CVA8220130));

		PdfPTable cva6 = new PdfPTable(8);
		cva6.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		cva6.setWidthPercentage(100);
  
		String CVA8220140 = d2Deposit.getMap().get(D2DepositHeaderKey.CVA8220140.getCode());
		cva6.addCell(tableCell("Moratoria hipotecaria (artículos 16 a 19 Real Decreto-Ley 11/2020): " + getBoolText(CVA8220140), 8));
		
		String CVA8220150 = d2Deposit.getMap().get(D2DepositHeaderKey.CVA8220150.getCode());
		cva6.addCell(tableCell("Moratoria no hipotecaria (artículo 18, 21 a 26 Real Decreto-Ley 11/2020): " + getBoolText(CVA8220150), 8));

		String CVA8220160 = d2Deposit.getMap().get(D2DepositHeaderKey.CVA8220160.getCode());
		cva6.addCell(tableCell("Se ha solicitado flexifibilización y suspensión de suministros(artículos 42 a 44 Real Decreto-Ley 11/2020): " + getBoolText(CVA8220160), 8));

		String CVA8220170 = d2Deposit.getMap().get(D2DepositHeaderKey.CVA8220170.getCode());
		cva6.addCell(tableCell("Se ha acogido a las medidas de apoyo del sector del Turismo de los artículos 12 y 13 del Real Decreto-Ley 7/2020, de 12 de marzo: " + getBoolText(CVA8220170), 8));

		document.add(new Paragraph(" "));
		document.add(cva6);
		document.newPage();
	}
	
	public void ITR() throws BadElementException, MalformedURLException, DocumentException, IOException {
		document.add(header());
		document.add(subHeader());
		
		PdfPTable itr1 = new PdfPTable(8);
		itr1.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		itr1.setWidthPercentage(100);
  
		itr1.addCell(tableHeader("Identificador del Titular Real", 8, 10));
		String ITR8080829 = d2Deposit.getMap().get(D2DepositHeaderKey.ITR8080829.getCode());
		itr1.addCell(tableCell("La entidad está sujeta a la obligación de identificar al titular real proque no cotiza en mercados regulados: " + getBoolText(ITR8080829), 8));

		document.add(new Paragraph(" "));
		document.add(itr1);
		
		PdfPTable itr0 = new PdfPTable(7);
		itr0.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		itr0.setWidthPercentage(100);
  
		itr0.addCell(tableHeader("Titular real persona física con porcentaje de participación superior al 25%", 8, 10));
		itr0.addCell(tableHeader("Nombre y Apellidos", 1, 10));
		itr0.addCell(tableHeader("DNI / Código de Identificación Extranjero", 1, 10));
		itr0.addCell(tableHeader("Fecha de Nacimiento", 1, 10));
		itr0.addCell(tableHeader("Nacionalidad", 1, 10));
		itr0.addCell(tableHeader("Pais de Residencia", 1, 10));
		itr0.addCell(tableHeader("% Participación Directa", 1, 10));
		itr0.addCell(tableHeader("% Participación Indirecta", 1, 10));
		
		for(Integer i = 0; i< D2DepositConstants.ITR_KEYS_1.length; i+=7){
			D2DepositHeaderKey[] d2 = new D2DepositHeaderKey[]{
					D2DepositConstants.ITR_KEYS_1[i],
					D2DepositConstants.ITR_KEYS_1[i+1],
					D2DepositConstants.ITR_KEYS_1[i+2],
					D2DepositConstants.ITR_KEYS_1[i+3],
					D2DepositConstants.ITR_KEYS_1[i+4],
					D2DepositConstants.ITR_KEYS_1[i+5],
					D2DepositConstants.ITR_KEYS_1[i+6]
			};

			
			for (Integer j = 0; j < d2.length ;j++) {
				String txt = d2Deposit.getMap().get(d2[j].getCode());
				itr0.addCell(tableCell(txt, 1));
			}
		}

		document.add(new Paragraph(" "));
		document.add(itr0);
		
		PdfPTable itr2 = new PdfPTable(5);
		itr2.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		itr2.setWidthPercentage(100);
  
		itr2.addCell(tableHeader("Titular real persona física con porcentaje de participación superior al 25%", 8, 10));
		itr2.addCell(tableHeader("Nombre y Apellidos", 1, 10));
		itr2.addCell(tableHeader("DNI / Código de Identificación Extranjero", 1, 10));
		itr2.addCell(tableHeader("Fecha de Nacimiento", 1, 10));
		itr2.addCell(tableHeader("Nacionalidad", 1, 10));
		itr2.addCell(tableHeader("Pais de Residencia", 1, 10));
		
		for(Integer i = 0; i< D2DepositConstants.ITR_KEYS_2.length; i+=5){
			D2DepositHeaderKey[] d2 = new D2DepositHeaderKey[]{
					D2DepositConstants.ITR_KEYS_2[i],
					D2DepositConstants.ITR_KEYS_2[i+1],
					D2DepositConstants.ITR_KEYS_2[i+2],
					D2DepositConstants.ITR_KEYS_2[i+3],
					D2DepositConstants.ITR_KEYS_2[i+4]
			};
			
			for (Integer j = 0; j < d2.length ;j++) {
				String txt = d2Deposit.getMap().get(d2[j].getCode());
				itr2.addCell(tableCell(txt, 1));
			}
		}

		document.add(new Paragraph(" "));
		document.add(itr2);
		
		PdfPTable itr3 = new PdfPTable(7);
		itr3.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		itr3.setWidthPercentage(100);
		
		itr3.addCell(tableHeader("Detalle de las sociedades intervinientes en la cadena de control", 8, 10));
		itr3.addCell(tableHeader("DNI / Código de Identificación Extranjero", 1, 10));
		itr3.addCell(tableHeader("Nivel en la cadena de control", 1, 10));
		itr3.addCell(tableHeader("Denominacion Social", 1, 10));
		itr3.addCell(tableHeader("NIF / Código de Identificación Extranjero", 1, 10));
		itr3.addCell(tableHeader("Nacionalidad", 1, 10));
		itr3.addCell(tableHeader("Domicilio Social", 1, 10));
		itr3.addCell(tableHeader("Datos Registrales / LEI", 1, 10));
		
		for(Integer i = 0; i< D2DepositConstants.ITR_KEYS_3.length; i+=7){
			D2DepositHeaderKey[] d2 = new D2DepositHeaderKey[]{
					D2DepositConstants.ITR_KEYS_3[i],
					D2DepositConstants.ITR_KEYS_3[i+1],
					D2DepositConstants.ITR_KEYS_3[i+2],
					D2DepositConstants.ITR_KEYS_3[i+3],
					D2DepositConstants.ITR_KEYS_3[i+4],
					D2DepositConstants.ITR_KEYS_3[i+5],
					D2DepositConstants.ITR_KEYS_3[i+6]
			};
			
			for (Integer j = 0; j < d2.length ;j++) {
				String txt = d2Deposit.getMap().get(d2[j].getCode());
				itr3.addCell(tableCell(txt, 1));
			}
		}

		document.add(new Paragraph(" "));
		document.add(itr3);
		
		document.newPage();
	}

	public void SRA() throws BadElementException, MalformedURLException, DocumentException, IOException {
		document.add(header());
		document.add(subHeader());
		
		PdfPTable sra1 = new PdfPTable(8);
		sra1.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		sra1.setWidthPercentage(100);
  
		sra1.addCell(tableHeader("Documento Sobre Servicios a Terceros", 8, 10));
		sra1.addCell(tableCell("Sociedad: " + d2Deposit.getMap().get(D2DepositHeaderKey.IDA01020.getCode()), 4));
		sra1.addCell(tableCell("NIF: " + d2Deposit.getMap().get(D2DepositHeaderKey.IDA01010.getCode()), 4));
		sra1.addCell(tableCell("Domicilio Social: " + d2Deposit.getMap().get(D2DepositHeaderKey.IDA01022.getCode()), 8));
		sra1.addCell(tableCell("Municipio: " + d2Deposit.getMap().get(D2DepositHeaderKey.IDA01023.getCode()), 4));
		sra1.addCell(tableCell("Provincia: " + d2Deposit.getMap().get(D2DepositHeaderKey.IDA01025.getCode()), 4));
		String value = getBoolText(d2Deposit.getMap().get(D2DepositHeaderKey.SRP8080831.getCode()));
		sra1.addCell(tableCell("Esta hoja SÓLO debe rellenarse si la sociedad ha realizado, durante el presente ejercicio, alguna operación de prestación de servicios a terceros de los contemplados en el articulo 2.1 o) de la Ley 10/2010, de 28 de abril, de prevención del blanqueo de capitales y de la financiación del terrorismo. ¿Rellenar hoja?" + value, 8));
		
		sra1.addCell(tableCell("Ámbito territorial de operaciones: ", 4));
		sra1.addCell(tableCell(getValue(D2DepositHeaderKey.SRP831001), 4));
		sra1.addCell(tableCell("Municipios donde realiza operaciones:", 4));
		sra1.addCell(tableCell(getValue(D2DepositHeaderKey.SRP831002), 4));
		sra1.addCell(tableCell("Provincias donde realiza operaciones:", 4));
		sra1.addCell(tableCell(getValue(D2DepositHeaderKey.SRP831003), 4));
		sra1.addCell(tableCell("¿Ha prestado servicios a no residentes?", 4));
		sra1.addCell(tableCell(getBoolText(getValue(D2DepositHeaderKey.SRP831004)), 4));

		document.add(new Paragraph(" "));
		document.add(sra1);
		String[] columns = { "Ejercicio " + d2Deposit.getYear(), 
				"Ejercicio " + (d2Deposit.getYear()-1), "Número de Operaciones"};
		numberTable("Servicios por cuenta de terceros", columns, D2PDepositConstants.SRP_KEYS, null, null, 10);

		document.newPage();
	}
	
	private String getValue(D2DepositHeaderKey key) {
		String str = d2Deposit.getMap().get(key.getCode());
		if(str == null || str.equalsIgnoreCase("null")) return "";
		return str;
	}
	
	public void ECPN() throws BadElementException, MalformedURLException, DocumentException, IOException {
		document.add(header());
		document.add(subHeader());
		document.newPage();
	}
	
	public void MA() throws BadElementException, MalformedURLException, DocumentException, IOException {
		document.add(header());
		document.add(subHeader());
		document.newPage();
	}
	
	public void IP() throws BadElementException, MalformedURLException, DocumentException, IOException {
		document.add(header());
		document.add(subHeader());
		document.newPage();
	}
	
	public void CHD() throws BadElementException, MalformedURLException, DocumentException, IOException {
		document.add(header());
		document.add(subHeader());
		
		PdfPTable chd = new PdfPTable(8);
		chd.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		chd.setWidthPercentage(100);
		chd.addCell(tableHeader("Certificación de huella digital", 8, 10));
		
		document.add(new Paragraph(" "));
		document.add(chd);
		
		PdfPTable chd2 = new PdfPTable(8);
		chd2.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		chd2.setWidthPercentage(100);
		chd2.addCell(tableHeader("Nombre de las personas que expiden la certificación", 8, 10));
		
		D2DepositFooterKey[] keys = D2DepositConstants.H_ABREVIATE_KEYS;
		for (D2DepositFooterKey d2DepositFooterKey : keys) {
			String text = d2Deposit.getMap().get(d2DepositFooterKey.getCode());
			chd2.addCell(tableCell(text, 8));
		}
		document.add(new Paragraph(" "));
		document.add(chd2);
		
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
				+ " que deba ser inclu\u00edda de acuerdo a la norma de elaboraci\u00f3n '4Âº Cuentas"
				+ " anuales abreviadas' en su punto 5, de la tercera parte del Plan General"
				+ " de Contabilidad (Real Decreto 1514/2007 de 16 de Noviembre).";
		String text2 = "Los abajo firmantes, como Administradores de la Sociedad citada,"
				+ " manifiestan que en la contabilidad correspondiente a las presentes"
				+ " cuentas anuales SI existen partidas de naturaleza medioambiental,"
				+ " y han sido inclu\u00eddas en un Apartado adiciones de la Memoria de"
				+ " acuerdo a la norma de elaboraci\u00f3n '4Âº Cuentas anuales abreviadas'"
				+ " en su punto 5, de la tercera parte del Plan General de Contabilidad "
				+ "(Real Decreto 1514/2007 de 16 de Noviembre).";
		
		dm.addCell(tableCell(text1, 1));
		dm.addCell(tableCell((a ? TIC : "-"), 1));
		dm.addCell(tableCell(text2, 1));
		dm.addCell(tableCell((b ? TIC : "-"), 1));
		
		document.add(new Paragraph(" "));
		document.add(dm);
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
// TODO	if(d2Deposit.getYear() < 2016) AP6B();
		AP6C();
	}
	
	public void AP6A() throws BadElementException, MalformedURLException, DocumentException, IOException{
		freeText("Apartado " + (d2Deposit.getYear() < 2016 ? "6" : "5") + " - Activos financieros", D2DepositKey.MAT69069001);
	}
	
	public void AP6C() throws BadElementException, MalformedURLException, DocumentException, IOException{
		document.add(header());
		document.add(subHeader());
		document.setPageSize(PageSize.A4_LANDSCAPE);
		
		D2DepositKey[][] keys, keys2;
		if (d2Deposit.getType().equalsIgnoreCase("PYMES")){
			keys = D2PDepositConstants.MRN6_PYMES_KEYS_4;
			keys2 = D2PDepositConstants.MRN6_PYMES_KEYS_5;
		}
		else{
			keys = D2DepositConstants.MRN6_ABREVIATE_KEYS_4;
			keys2 = D2DepositConstants.MRN6_ABREVIATE_KEYS_5;		
		}
		D2DepositKey[][] keys3 = D2DepositConstants.MRN6_ABREVIATE_KEYS_6;
		
		String current = "Largo plazo";
		String previous = "Corto plazo";
		
		String title = "Correcciones por deterioro del valor originadas por el riesgo de cr\u00e9dito";
		String[] columns = {"Valores representativos de deuda", "Créditos, derivados y otros (3)", "TOTAL"};
		String[] subcolumns = {current, previous, current, previous, current, previous};
		
		general(title, columns, subcolumns, keys, 10);

		String title2 = "Valoración y variaciones de valor de inversiones financieras valoradas a valor razonable";
		String[] columns2 = { "Activos a valor razonable con cambios en p\u00e9rdidas y ganancias", "Activos mantenidos para negociar", "Activos disponibles para la venta", "TOTAL"};
		
		general(title2, columns2, null, keys2, 10);

		String title3 = "Correcciones valorativas por deterioro registradas en las distintas participaciones";
		String[] columns3 = {"P\u00e9rdidas por deteriodo al final del ejercicio X", "(+/-) Variaci\u00f3n deteriodo a p\u00e9rdidas y ganancias",
				"(+) Variaci\u00f3n contra patrimonio neto", "(-) Salidas y reducciones", "(+/-) Traspasos y otras variaciones (combinaciones de negocio, etc.)",
				"P\u00e9rdida por deteriodo al final del ejercicio Y"};
		
		general(title3, columns3, null, keys3, 10);

		document.newPage();
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
		D2DepositKey[][] keys = d2Deposit.getType().equalsIgnoreCase("PYMES")
			? D2PDepositConstants.MRN7_PYMES_KEYS_3 : D2DepositConstants.MRN7_ABREVIATE_KEYS_3;

		String title = "Vencimientos de las deudas al cierre del ejercicio 2021";
		String[] columns = {"Uno", "Dos", "Tres", "Cuatro", "Cinco", "Más de 5", "TOTAL"};

		general(title, columns, null, keys, 10);
	
		document.newPage();
	}

	
	public void AP8() throws BadElementException, MalformedURLException, DocumentException, IOException{
		freeText("Apartado " + (d2Deposit.getYear() < 2016 ? "8" : "7") + " - Fondos propios", D2DepositKey.MAT89089001);	
	}
	
	public void AP9() throws BadElementException, MalformedURLException, DocumentException, IOException{
		freeText("Apartado " + (d2Deposit.getYear() < 2016 ? "9" : "8") + " - Situaci\u00f3n fiscal", D2DepositKey.MAT99099001);	
	}

	public void AP12() throws BadElementException, MalformedURLException, DocumentException, IOException{
		AP12A();
		AP12B();
		AP12C();
		AP12D();
		AP12E();
		AP12F();
	}
	
	public void AP12A() throws BadElementException, MalformedURLException, DocumentException, IOException{
		freeText("Apartado " + (d2Deposit.getYear() < 2016 ? "12" : "9") + " - Otra informaci\u00f3n", D2DepositKey.MAT139139001);	
	}
	
	public void AP12B() throws BadElementException, MalformedURLException, DocumentException, IOException{
		document.add(header());
		document.add(subHeader());

		D2DepositKey[][] keys =  d2Deposit.getYear() < 2016 ? D2DepositConstants.MRN12_ABREVIATE_PYMES_KEYS_1 : D2DepositConstants.MRN12_ABREVIATE_PYMES_KEYS_1_2016;

		String title = "Operaciones con partes vinculadas en el ejercicio " + d2Deposit.getYear();
		if(d2Deposit.getYear() < 2016){
			String[] columns = {"Entidad Dominante", "Otras empresas del grupo", "Negocios conjuntos en los que la empresa sea uno de los participantes",
					"Empresas Asociadas", "Empresas con control conjunto o influencia significativa sobre la empresa",
					"Personal clave de la direcci\u00f3 de la empresa o de la entidad dominante", "Otras partes vinculadas"};
			general(title, columns, null, keys, 10);
		} else {
			String[] columns = {"Entidad Dominante", "Empresas Dependientes", "Negocios conjuntos en los que la empresa sea uno de los participantes",
				"Empresas Asociadas", "Empresas con control conjunto o influencia significativa sobre la empresa",
				"Miembros de los \u00f3rganos de administraci\u00f3n y personal clave de la direcci\u00f3n de la empresa"};
			general(title, columns, null, keys, 10);
		}
		
		document.newPage();
	}
	
	public void AP12C() throws BadElementException, MalformedURLException, DocumentException, IOException{
		document.add(header());
		document.add(subHeader());
		
		D2DepositKey[][] keys =  d2Deposit.getYear() < 2016 ? D2DepositConstants.MRN12_ABREVIATE_PYMES_KEYS_2 : D2DepositConstants.MRN12_ABREVIATE_PYMES_KEYS_2_2016;

		String title = "Operaciones con partes vinculadas en el ejercicio " + (d2Deposit.getYear() - 1);
		if(d2Deposit.getYear() < 2016){
			String[] columns = {"Entidad Dominante", "Otras empresas del grupo", "Negocios conjuntos en los que la empresa sea uno de los participantes",
					"Empresas Asociadas", "Empresas con control conjunto o influencia significativa sobre la empresa",
					"Personal clave de la direcci\u00f3 de la empresa o de la entidad dominante", "Otras partes vinculadas"};
			general(title, columns, null, keys, 10);
		} else {
			String[] columns = {"Entidad Dominante", "Empresas Dependientes", "Negocios conjuntos en los que la empresa sea uno de los participantes",
				"Empresas Asociadas", "Empresas con control conjunto o influencia significativa sobre la empresa",
				"Miembros de los \u00f3rganos de administraci\u00f3n y personal clave de la direcci\u00f3n de la empresa"};
			general(title, columns, null, keys, 10);
		}
		document.newPage();
	}
	
	public void AP12D() throws BadElementException, MalformedURLException, DocumentException, IOException{
		document.add(header());
		document.add(subHeader());
		
		D2DepositKey [][] keys;
		if (d2Deposit.getType().equalsIgnoreCase("PYMES"))
			 keys =  d2Deposit.getYear() < 2016 ? D2PDepositConstants.MRN12_PYMES_KEYS_3 :  D2PDepositConstants.MRN12_PYMES_KEYS_3_2016;
		else keys =  d2Deposit.getYear() < 2016 ? D2DepositConstants.MRN12_ABREVIATE_KEYS_3 : D2DepositConstants.MRN12_ABREVIATE_KEYS_3_2016;

		String title = "Saldos pendientes con partes vinculadas en el ejercicio " + d2Deposit.getYear();
		if(d2Deposit.getYear() < 2016){
			String[] columns = {"Entidad Dominante", "Otras empresas del grupo", "Negocios conjuntos en los que la empresa sea uno de los participantes",
					"Empresas Asociadas", "Empresas con control conjunto o influencia significativa sobre la empresa",
					"Personal clave de la direcci\u00f3 de la empresa o de la entidad dominante", "Otras partes vinculadas"};
			general(title, columns, null, keys, 10);
		} else {
			String[] columns = {"Entidad Dominante", "Empresas Dependientes", "Negocios conjuntos en los que la empresa sea uno de los participantes",
					"Empresas Asociadas", "Empresas con control conjunto o influencia significativa sobre la empresa",
					"Miembros de los \u00f3rganos de administraci\u00f3n y personal clave de la direcci\u00f3n de la empresa"};
			general(title, columns, null, keys, 10);
		}
		document.newPage();
	}
	
	public void AP12E() throws BadElementException, MalformedURLException, DocumentException, IOException{
		document.add(header());
		document.add(subHeader());

		D2DepositKey [][] keys;
		if (d2Deposit.getType().equalsIgnoreCase("PYMES"))
			keys = d2Deposit.getYear() < 2016 ? D2PDepositConstants.MRN12_PYMES_KEYS_4 : D2PDepositConstants.MRN12_PYMES_KEYS_4_2016;
		else keys = d2Deposit.getYear() < 2016 ? D2DepositConstants.MRN12_ABREVIATE_PYMES_KEYS_4 : D2DepositConstants.MRN12_ABREVIATE_PYMES_KEYS_4_2016;

		String title = "Saldos pendientes con partes vinculadas en el ejercicio " + (d2Deposit.getYear()-1);
		if(d2Deposit.getYear() < 2016){
			String[] columns = {"Entidad Dominante", "Otras empresas del grupo", "Negocios conjuntos en los que la empresa sea uno de los participantes",
					"Empresas Asociadas", "Empresas con control conjunto o influencia significativa sobre la empresa",
					"Personal clave de la direcci\u00f3 de la empresa o de la entidad dominante", "Otras partes vinculadas"};
			general(title, columns, null, keys, 10);
		} else {
			String[] columns = {"Entidad Dominante", "Empresas Dependientes", "Negocios conjuntos en los que la empresa sea uno de los participantes",
					"Empresas Asociadas", "Empresas con control conjunto o influencia significativa sobre la empresa",
					"Miembros de los \u00f3rganos de administraci\u00f3n y personal clave de la direcci\u00f3n de la empresa"};
			general(title, columns, null, keys, 10);
		}
		document.newPage();
	}
	
	public void AP12F() throws BadElementException, MalformedURLException, DocumentException, IOException{
		document.add(header());
		document.add(subHeader());

		D2DepositKey [][] keys, keys2;
		if(d2Deposit.getYear() < 2016){
			if (d2Deposit.getType().equalsIgnoreCase("PYMES")){
				keys = D2PDepositConstants.MRN12_PYMES_KEYS_5;
				keys2 = D2PDepositConstants.MRN12_PYMES_KEYS_6;
			}
			else{
				keys = D2DepositConstants.MRN12_ABREVIATE_KEYS_5;
				keys2 = D2DepositConstants.MRN12_ABREVIATE_KEYS_6;		
			}
		} else {
			keys = D2DepositConstants.MRN12_ABREVIATE_KEYS_5_2016;
			keys2 = D2DepositConstants.MRN12_ABREVIATE_KEYS_6_2016;		
		}
		String title = "Importes recibidos por el personal de alta direcci\u00f3n";
		two(title, keys, 10);
		
		String title2 = "Importes recibidos por los miembros de los \u00f3rganos de administraci\u00f3n";
		two(title2, keys2, 10);
		
		document.newPage();
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
		document.newPage();
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
	
	private void general(String title, String[] columns, String[] subcolumns, D2DepositKey[][] keys, Integer size) throws DocumentException {
		boolean hasSubcolums = subcolumns != null && subcolumns.length > 0;
		Integer columnLength = hasSubcolums ? subcolumns.length : columns.length; 
		PdfPTable table = new PdfPTable(columnLength + 4);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		table.setWidthPercentage(100);

		if(hasSubcolums) {
			table.addCell(tableHeader("", 4, size));
			Integer dif = subcolumns.length / columns.length;
			for(Integer i = 0; i < columns.length; i++) {
				table.addCell(tableHeader(columns[i], dif, size));
			}
			table.addCell(tableHeader(title, 3, size));
			table.addCell(tableHeader("", 1, size));
			for(Integer i = 0; i < subcolumns.length; i++) {
				table.addCell(tableHeader(subcolumns[i], 1, size));
			}
			
			if(keys != null) {
				for (D2DepositKey[] innerKeys : keys) {
					String description = getDescription(D2DepositDescription.DESCRIPTION_MAP.get(innerKeys[0]));
					table.addCell(tableCell(description, 3));
					table.addCell(tableCell(innerKeys[0].getCode(), 1));
					for(Integer j = 0; j < subcolumns.length; j++) {
						table.addCell(tableCell(number(d2Deposit.getMap().get(innerKeys[j].getCode())), 1));
					}
				}
			}

		} else {
			table.addCell(tableHeader(title, 3, size));
			table.addCell(tableHeader("", 1, size));
			for(Integer i = 0; i < columns.length; i++) {
				table.addCell(tableHeader(columns[i], 1, size));
			}
			
			if(keys != null) {
				for (D2DepositKey[] innerKeys : keys) {
					String description = getDescription(D2DepositDescription.DESCRIPTION_MAP.get(innerKeys[0]));
					table.addCell(tableCell(description, 3));
					table.addCell(tableCell(innerKeys[0].getCode(), 1));
					for(Integer j = 0; j < columns.length; j++) {
						table.addCell(tableCell(number(d2Deposit.getMap().get(innerKeys[j].getCode())), 1));
					}
				}
			}

		}
		
		document.add(new Paragraph(" "));
		document.add(table);
	}
	
	private void numberTable(String title, String[] columns, D2DepositHeaderKey[][] keys, D2DepositHeaderKey[][] keys2, 
			D2DepositKey[][] keys3, Integer size) throws DocumentException {
		PdfPTable table = new PdfPTable(8);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		table.setWidthPercentage(100);
		Integer titleWidth = 8 - columns.length;
		table.addCell(tableHeader(title, titleWidth, size));
		for(Integer i = 0; i < columns.length; i++) {
			table.addCell(tableHeader(columns[i], 1, size));
		}

		if(keys != null) {
			for (D2DepositHeaderKey[] innerKeys : keys) {
				String description = getDescription(D2DepositDescription.DESCRIPTION_MAP_HEADER.get(innerKeys[0]));
				table.addCell(tableCell(description, titleWidth));
				for(Integer i = 0; i < columns.length; i++) {
					table.addCell(tableCell(number(d2Deposit.getMap().get(innerKeys[i].getCode())), 1));
				}
			}
		}
		if(keys2 != null) {
			for (D2DepositHeaderKey[] innerKeys : keys2) {
				String description = getDescription(D2DepositDescription.DESCRIPTION_MAP_HEADER.get(innerKeys[0]));
				table.addCell(tableCell(description, titleWidth));
				for(Integer i = 0; i < columns.length; i++) {
					table.addCell(tableCell(number(d2Deposit.getMap().get(innerKeys[i].getCode())), 1));
				}
			}
		}
		if(keys3 != null) {
			for (D2DepositKey[] innerKeys : keys3) {
				String description = getDescription(D2DepositDescription.DESCRIPTION_MAP_HEADER.get(innerKeys[0]));
				table.addCell(tableCell(description, titleWidth));
				for(Integer i = 0; i < columns.length; i++) {
					table.addCell(tableCell(number(d2Deposit.getMap().get(innerKeys[i].getCode())), 1));
				}
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
		cell.setBackgroundColor(new BaseColor(Integer.valueOf("c4", 16 ),Integer.valueOf("12", 16 ),Integer.valueOf("30", 16 )));
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
		cell.setBackgroundColor(new BaseColor(Integer.valueOf("c4", 16 ),Integer.valueOf("12", 16 ),Integer.valueOf("30", 16 )));
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
		} else if(desc.contains("Â¬")){
			Integer pos = desc.indexOf("Â¬");
			return desc.substring(0, pos) + (d2Deposit.getYear()-2) + desc.substring(pos+1);
		} else if(desc.contains("#")){
			Integer pos = desc.indexOf("#");
			return desc.substring(0, pos) + (d2Deposit.getYear()-1) + desc.substring(pos+1);

		}else return desc;	
	}
	
	private boolean getBool(String a) {
		return a != null && !a.equalsIgnoreCase("null")
				&& (a.equals("1") || a.equalsIgnoreCase("true"));
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
