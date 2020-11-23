package com.esferalia.aon.in.payroll.tgss.report;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.watson.util.AonStringUtils;

public class CCCVidaLaboral {

	public static void parse( InputStream is ) throws IOException , UnknownPDFException {
		try (PDDocument doc = PDDocument.load(is))
		{
			parser(doc);
		}
	}
	
	private static void parser(PDDocument doc) throws IOException, UnknownPDFException {
        AccessPermission ap = doc.getCurrentAccessPermission();
		if (!ap.canExtractContent())
		{
			throw new IOException("You do not have permission to extract text");
		}
		
		PDFTextStripper stripper= new PDFTextStripper();
		
		stripper.setSortByPosition(true);
		
		
		for (int p = 1; p <= doc.getNumberOfPages(); p++) {
            // Set the page interval to extract. 
			// If we don't, then all pages would be extracted.
			stripper.setStartPage(p);
			stripper.setEndPage(p);
			String text = stripper.getText(doc);
			if ( AonStringUtils.isBlank(text) ) 
				continue;
			
			System.out.println(text);
			
			parse(text);
			
		}
	}
	
	public static void parse(String text) throws IOException, UnknownPDFException {
		
	}

	//	0111 01 105360062 9 0B01487271
	private static final Pattern CCC_CIF = 
	Pattern.compile("^\\s*(?<regime>[0-9]+)\\s+(?<province>[0-9]+)\\s+(?<ccc>[0-9]+)\\s+[0-9]\\s+(?<cif>[A-Z,0-9]+)\\s*$"
	, Pattern.CASE_INSENSITIVE);

	

	private static Matcher check(Pattern pattern, String str) {
		Matcher matcher =
		pattern.matcher(str);
		matcher.matches();
		return matcher;		
	}
	

	public static void main(String[] args) throws IOException, UnknownPDFException {
		Matcher matcher = check(CCC_CIF, "	0111 01 105360062 9 0B01487271");
		System.out.println("regime : "+matcher.group("regime"));
		System.out.println("province : " + matcher.group("province"));
		System.out.println("ccc : " + matcher.group("ccc"));
		System.out.println("cif : " + matcher.group("cif"));
		
	}

	 
//	INFORME DE VIDA LABORAL DE UN CÓDIGO CUENTA DE COTIZACIÓN
//	DATOS IDENTIFICATIVOS DE LA EMPRESA
//	   
//	RAZÓN SOCIAL	 CÓDIGO CUENTA DE COTIZACIÓN EMPRESARIO
//	AON SOLUTIONS S.L.
//	0111 01 105360062 9 0B01487271
//	DOMICILIO	 LOCALIDAD C.P. PERIODO SOLICITADO
//	CL DUQUE DE WELLINGTON 52 B
//	VITORIA-GASTEIZ 01010 01 01 2015 / 23 10 2020
//	CNAE TIPOS AT: IT IMS TOTAL
//	6209  Otros servicios relacionados con las tec
//	0,80 0,70 1,50
//	 
//	DATOS LABORALES
//	   
//	NÚMERO DE AFILIACION DOCUMENTO IDENTIFICATIVO NOMBRE Y APELLIDOS
//	CLV
//	SITUACIÓN F.REAL ALTA F.EFECTO ALTA F.REAL SIT. F.EFECTO SIT. G.C/M T.C. C.T.P. EP/OC TIPOS AT: IT  IMS  TOTAL DIAS COT.
//	01 0019805355 1 016271678Y ANA DIAZ PEREZ WM4
//	 ALTA 01-08-2020 01-08-2020   01 100  A 0,80 0,70 1,50 115 E8L
//	01 1000572259 1 015383469B MARTA AREVALILLO UBIERNA BNS
//	 BAJA 01-12-2014 01-12-2014 13-07-2018 13-07-2018 07 100  A 0,65 0,35 1,00 1321 9Z1
//	01 1001022503 1 044671367P EUGENIO CASTELLANO HURTADO 9AZ
//	 ALTA 01-08-2020 01-08-2020   02 100  A 0,80 0,70 1,50 115 N8C
//	 BAJA 30-03-2015 30-03-2015 30-09-2019 30-09-2019 01 100  A 0,80 0,70 1,50 1646 2TW
//	01 1001022705 1 016303084V INMACULADA GOMEZ DE LA IGLESIA YBN
//	 BAJA 02-10-2014 02-10-2014 15-08-2015 15-08-2015 07 502 0,500 A 0,65 0,35 1,00 159 3JF
//	01 1003191057 1 072742661D EKAIN AGUIRREZABAL DIAZ AE0
//	 BAJA 10-08-2015 13-04-2018 31-08-2018 31-08-2018 02 100   0,65 0,70 1,35 141 C9P
//	01 1004767006 1 016302727M MARTA FERNANDEZ CORDOBA A00
//	 BAJA 05-06-2014 05-06-2014 04-06-2016 04-06-2016 07 501 0,750 A 0,65 0,35 1,00 548 V45
//	01 1005151164 1 018599575G GORKA IRAZU SOPEñA NNN
//	 BAJA 30-03-2015 30-03-2015 02-04-2018 02-04-2018 01 100  A 0,65 0,35 1,00 1100 O7K
//	 VAC.RETRIB.NO  03-04-2018 07-04-2018     A 0,65 0,35 1,00 5 1B8
//	01 1005185924 1 044679529M RAUL TREPIANA ZARATE YCS
//	 ALTA 01-08-2020 01-08-2020   01 100  A 0,80 0,70 1,50 115 E8L
//	 BAJA 30-03-2015 30-03-2015 30-09-2019 30-09-2019 01 100  A 0,80 0,70 1,50 1646 2TW
//	01 1006256964 1 072750379E SHEILA RUESGAS GARCIA D5K
//	 ALTA 01-08-2020 01-08-2020   07 100  A 0,80 0,70 1,50 115 N8S
//	 BAJA 05-06-2017 05-06-2017 31-07-2018 31-07-2018 07 420   0,65 0,70 1,35 300 E0O
//	01 1007308507 1 072751122Y PATRICIA COCA FUENTES J4I
//	 ALTA 04-09-2013 04-09-2013   07 100  A 0,80 0,70 1,50 2638 33N
//	01 1007421166 1 072740703Y ALVARO MARTINEZ DE LAGOS ESPINOSA M35
//	 BAJA 02-06-2015 02-06-2015 06-05-2016 06-05-2016 01 420  A 0,65 0,35 1,00 340 346
//	01 1008923050 1 072825086W NILDA MANSO MARTINEZ 05R
//	 BAJA 05-06-2017 05-06-2017 26-04-2018 26-04-2018 07 520 0,630  0,65 0,70 1,35 205 7X3
//	01 1011187190 1 072828005T ANDER IBAÑEZ DE GAUNA NAVAZO OY6
//	 ALTA 01-08-2020 01-08-2020   02 100  A 0,80 0,70 1,50 115 N8C
//	 BAJA 15-09-2014 15-09-2014 30-09-2019 30-09-2019 01 109  A 0,80 0,70 1,50 1842 YMP
//	28 1468615302 1 047227931F SERGIO VALDEPEÑAS DEL POZO 1B3
//	 ALTA 01-08-2020 01-08-2020   02 100  A 0,80 0,70 1,50 115 N8C
//	 BAJA 18-11-2017 18-11-2017 17-11-2019 17-11-2019 05 420   0,80 0,70 1,50 730 H32
//	29 1136796369 6 0Y7514970X RAY DE JESUS VASQUEZ BEAUPERTHUY --- Q01
//	 ALTA 09-09-2020 09-09-2020   02 401  A 0,80 0,70 1,50 76 6BJ
//	48 1018581850 1 016297188D AIMAR TELLITU MEJIA NN3
//	 BAJA 04-05-2015 04-05-2015 15-07-2015 15-07-2015 01 100  A 0,65 0,35 1,00 73 874
//	  
//	CODIFICACIONES INFORMÁTICAS
//	   
//	REFERENCIA: FECHA:	 HORA:	 HUELLA: PÁGINA:
//	 23-11-2020 18:25:51  1 de 2
//	Este documento no será válido sin las codificaciones informáticas A157
//
//	 
//	INFORME DE VIDA LABORAL DE UN CÓDIGO CUENTA DE COTIZACIÓN
//	DATOS IDENTIFICATIVOS DE LA EMPRESA
//	   
//	RAZÓN SOCIAL	 CÓDIGO CUENTA DE COTIZACIÓN EMPRESARIO
//	AON SOLUTIONS S.L.
//	0111 01 105360062 9 0B01487271
//	DOMICILIO	 LOCALIDAD C.P. PERIODO SOLICITADO
//	CL DUQUE DE WELLINGTON 52 B
//	VITORIA-GASTEIZ 01010 01 01 2015 / 23 10 2020
//	CNAE TIPOS AT: IT IMS TOTAL
//	6209  Otros servicios relacionados con las tec
//	0,80 0,70 1,50
//	 
//	DATOS LABORALES
//	   
//	NÚMERO DE AFILIACION DOCUMENTO IDENTIFICATIVO NOMBRE Y APELLIDOS
//	CLV
//	SITUACIÓN F.REAL ALTA F.EFECTO ALTA F.REAL SIT. F.EFECTO SIT. G.C/M T.C. C.T.P. EP/OC TIPOS AT: IT  IMS  TOTAL DIAS COT.
//	FIN..INFORME.    
//	            
//	NUMERO TOTAL DE TRABAJADORES: 16 Total CLV GUP
//	De Conformidad con los términos de la autorización número 228115, concedida en fecha 22/01/2015 a AON SOLUTIONS S.L. por la Tesorería 
//	General de la Seguridad Social, certifico que estos datos han sido transmitidos y validados por la misma e impresos de forma autorizada, surtiendo 
//	efectos en relación con el cumplimiento de las obligaciones conforme al artículo uno de la Orden ESS/484/2013 de 26 de marzo (BOE de 28 de marzo).
//	El usuario principal,
//	Fdo:
//	CODIFICACIONES INFORMÁTICAS
//	   
//	REFERENCIA: FECHA:	 HORA:	 HUELLA: PÁGINA:
//	A1572011000001 23-11-2020 18:25:51 7652KEGM 2 de 2
//	Este documento no será válido sin las codificaciones informáticas A157

	

}
