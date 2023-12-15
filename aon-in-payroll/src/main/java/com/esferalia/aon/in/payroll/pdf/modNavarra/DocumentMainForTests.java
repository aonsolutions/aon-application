package com.esferalia.aon.in.payroll.pdf.modNavarra;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class DocumentMainForTests {

	public static void main(String[] args) {
		try {

			ParserUtils pu = new ParserUtils();

			Mod7152023Navarra md = new Mod7152023Navarra();
			Mod1302023Navarra md130N = new Mod1302023Navarra();
			ModF692023Navarra mf69N = new ModF692023Navarra();

//			InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Navarra/1 - MODELO 130 3T 2023 OLIVER - NAVARRA.pdf");
			InputStream is = new FileInputStream(
					"/home/jmortega/Documentos/Modelos/Navarra/2 - MODELO F69 3T 2023 OLIVER - NAVARRA.pdf");
//			InputStream is = new FileInputStream(
//					"/home/jmortega/Documentos/Modelos/Navarra/MOD 715 3T 2023 BLAS OLIVA.pdf");

			byte[] bytes = IOUtils.toByteArray(is);

			PDFExtracter pdf = new PDFExtracter();
			String text = pdf.extract(bytes);
			System.out.println(text);

//TODO		FUNCIONES SETMODEL, SETHACIENDA, SETYEARANDPERIOD EN CADA CLASE
//TODO      LIMPIAR CODIGO (funciones, variables, orden)
//TODO      CAMBIAR VOID POR STRING O OBJETO 
//TODO 		CAMBIAR EN REGEX LOS ACENTOS POR NOMENCLATURA CORRESPONDIENTE
//TODO 		QUITAR AVISOS SONARLINT REGEX

			// MODELO F69 NAVARRA
			String name = mf69N.setSocialReasonName(text);
			System.out.println(name);
			String nif = mf69N.setNif(text);
			System.out.println(nif);
			String email = mf69N.setEmail(text);
			System.out.println(email);
			String phoneNumber = mf69N.setPhoneNumber(text);
			System.out.println(phoneNumber);
			mf69N.setPeriodAndYear(text);
			String amount = mf69N.setAmount(text);
			System.out.println(amount);
			mf69N.setHacienda(text);
			String model = mf69N.setModel(text);
			System.out.println(model);		
			String resultado = pu.obtenerTrimestre("T3");
		    System.out.println(resultado);
			
			// MODELO 130 NAVARRA
//			String nif = md130N.setNif(text);
//			System.out.println(nif);
//			String email = md130N.setEmail(text);
//			System.out.println(email);
//			String phoneNumber = md130N.setPhoneNumber(text);
//			System.out.println(phoneNumber);
//			md130N.setPeriodAndYear(text);
//			md130N.setAmount(text);
//			md130N.setHacienda(text);
//			md130N.setModel(text);

			// MODELO 715 NAVARRA
//			md.setSocialReasonName(text);
//			md.setRegistryNumber(text);
//			md.setNif(text);
//			md.setEmail(text);
//			md.setPhoneNumber(text);
//			md.setIBAN(text);
//			md.setPeriodAndYear(text);
//			md.setIssueDate(text);
//			md.setSign(text);
//			md.setCsv(text);
//			md.setAmount(text);
//			md.setHacienda(text);
//			md.setModel(text);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
