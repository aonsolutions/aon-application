package com.esferalia.aon.in.payroll.pdf.modGipuzkoa;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.mchange.v2.sql.filter.SynchronizedFilterDataSource;

public class DocumentMainForTests {

	public static void main(String[] args) {
		try {

			ParserUtils pu = new ParserUtils();

			Mod1152023Gipuzkoa mdG = new Mod1152023Gipuzkoa();
			Mod1302023Gipuzkoa md130G = new Mod1302023Gipuzkoa();
			Mod1802023Gipuzkoa md180G = new Mod1802023Gipuzkoa();
			Mod3492023Gipuzkoa md349G = new Mod3492023Gipuzkoa();
			Mod1102023Gipuzkoa md110G = new Mod1102023Gipuzkoa();
			Mod3002023Gipuzkoa md300G = new Mod3002023Gipuzkoa();
			Mod39020223Gipuzkoa md390G = new Mod39020223Gipuzkoa();
			Mod2002023Gipuzkoa md200G = new Mod2002023Gipuzkoa();
			Mod1902023Gipuzkoa md190 = new Mod1902023Gipuzkoa();

//			InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Guipuzkoa/1 - MOD 130 ROBERTO 4T 2022 GIPUZKOA.pdf");
//			InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Guipuzkoa/2 - MOD 180 ROBERTO 2022 GIPUZKOA.pdf");
//			InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Guipuzkoa/2 - MOD 349 FIDCOMMERCE 2T 2023 GIPUZKOA.pdf");
//			InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Guipuzkoa/3 - MOD 190 FIDCOMMERCE 2022 GIPUZKOA.pdf");
//			InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Guipuzkoa/4 - 110 3T 2021 ROBERTO GIPUZKOA.pdf");
//			InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Guipuzkoa/6 - 300 3T 2021 ROBERTO GIPUZKOA.pdf");
//			InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Guipuzkoa/4 - MOD 390 FIDCOMMERCE 2022 GIPUZKOA.pdf");
			InputStream is = new FileInputStream(
					"/home/jmortega/Documentos/Modelos/Guipuzkoa/3 - MOD 190 FIDCOMMERCE 2022 GIPUZKOA.pdf");

			byte[] bytes = IOUtils.toByteArray(is);

			PDFExtracter pdf = new PDFExtracter();
			String text = pdf.extract(bytes);
			System.out.println(text);

//TODO		FUNCIONES SETMODEL, SETHACIENDA, SETYEARANDPERIOD EN CADA CLASE
//TODO      LIMPIAR CODIGO (funciones, variables, orden)
//TODO      CAMBIAR VOID POR STRING O OBJETO 
//TODO 		CAMBIAR EN REGEX LOS ACENTOS POR NOMENCLATURA CORRESPONDIENTE
//TODO 		QUITAR AVISOS SONARLINT REGEX

			
			
			//MODELO 190 GIPUZKOA
			String nif = md190.setNif(text);
			System.out.println(nif);
			String name = md190.setSocialReasonName(text);
			System.out.println(name);
			String phoneNumber = md190.setPhoneNumber(text);
			System.out.println(phoneNumber);
			String email = md190.setEmail(text);
			System.out.println(email);
			String amount = md190.setAmount(text);
			System.out.println(amount);
			String issueDate = md190.setIssueDate(text);
			System.out.println(issueDate);
			List<String> lista = md190.setNifs(text);
			for (int i = 0; i < lista.size(); i++) {
//					System.out.println(lista.get(i));	
			}
			
			List<String> lista2 = md190.setNames(text);
			for (int i = 0; i < lista2.size(); i++) {
					System.out.println(lista2.get(i));
				
			}
			
			String exercise = md190.setExercise(text);
			System.out.println(exercise);
			
			
			// MODELO 200 GIPUZKOA
//			String nif = md200G.setDeclarantNif(text);
//			System.out.println(nif);
//			String declarantName = md200G.setDeclarantName(text);
//			System.out.println(declarantName);
//			String relationPersonNif = md200G.setRelationPersonNif(text);
//			System.out.println(relationPersonNif);
//			String relationPersonName = md200G.setRelationPersonName(text);
//			System.out.println(relationPersonName);
//			String email = md200G.setEmail(text);
//			System.out.println(email);
//			String phoneNumber = md200G.setPhoneNumber(text);
//			System.out.println(phoneNumber);
//			String legalRepresentatorName = md200G.setLegalRepresntators(text);
			
			
			// MODELO 390 GIPUZKOA
//			String nif = md390G.setNif(text);
//			System.out.println(nif);
//			String name = md390G.setname(text);
//			System.out.println(name);

			// MODELO 300 GIPUZKOA
//			String nif  = md300G.setNif(text);
//			System.out.println(nif);
//			String name = md300G.setName(text);
//			System.out.println(name);
//			String period = md300G.setPeriod(text);
//			System.out.println(period);
//			String exercise = md300G.setExercise(text);
//			System.out.println(exercise);
//			String accruedFee = md300G.setAccruedFee(text);
//			System.out.println(accruedFee);
//			String deduct = md300G.setDeduct(text);
//			System.out.println(deduct);
//			String amount = md300G.setAmount(text);
//			System.out.println(amount);
//			String issueDate = md300G.setIssueDate(text);
//			System.out.println(issueDate);

			// MODELO 110 GIPUZKOA
//			String nif = md110G.setIdentifyNif(text);
//			System.out.println(nif);
//			String identifyName = md110G.setIdentifyName(text);
//			System.out.println(identifyName);
//			String year = md110G.setYear(text);
//			System.out.println(year);
//			String period = md110G.setPeriod(text);
//			System.out.println(period);
//			String amount = md110G.setAmount(text);
//			System.out.println(amount);
//			String issueDate = md110G.setIssueDate(text);
//			System.out.println(issueDate);

			// MODELO 349 GIPUZKOA
//			String nif = md349G.setIdentifyNif(text);
//			System.out.println(nif);
//			String name = md349G.setIdentifyName(text);
//			System.out.println(name);
//			String exercise = md349G.setExercise(text);
//			System.out.println(exercise);
//			String period = md349G.setPeriod(text);
//			System.out.println(period);
//			String amount = md349G.setAmount(text);
//			System.out.println(amount);
//			
//			 List<String> lineasDatos = md349G.operationsLines(text);
//			 for (String linea : lineasDatos) {
//		            System.out.println(linea);
//		        }

			// MODELO 180 GIPUZKOA
//			String identifyNif = md180G.setIdentifyNif(text);
//			System.out.println(identifyNif);
//			String identifyName = md180G.setIdentifyName(text);
//			System.out.println(identifyName);
//			String phoneNumber = md180G.setPhoneNumber(text);
//			System.out.println(phoneNumber);
//			String relatedPerson = md180G.setRelatedPersonName(text);
//			System.out.println(relatedPerson);
//			String declarantNif = md180G.setDeclarantNif(text);
//			System.out.println(declarantNif);
//			String exercise = md180G.setExercise(text);
//			System.out.println(exercise);
//			String amount = md180G.setAmount(text);
//			System.out.println(amount);
//			String perceivers = md180G.setPerceivers(text);
//			System.out.println(perceivers);
//			String amountsPaid = md180G.setAmountsPaid(text);
//			System.out.println(amountsPaid);

			// MODELO 130 GIPUZKOA
//			String declarantNif = md130G.setDeclarantNif(text);
//			System.out.println(declarantNif);
//			String declarantName = md130G.setDeclarantName(text);
//			System.out.println(declarantName);
//			String activity = md130G.setPrincipalActivity(text);
//			System.out.println(activity);
//			String amount = md130G.setAmount(text);
//			System.out.println(amount);
//			String hacienda = md130G.setHacienda(text);
//			System.out.println(hacienda);
//			String model = md130G.setModel(text);
//			System.out.println(model);
//			String exercise = md130G.setExercise(text);
//			System.out.println(exercise);

			// MODELO 115 GUIPUZKOA
//			mdG.setExercise(text);
//			mdG.setPeriod(text);
//			mdG.setNif(text);
//			mdG.setSocialReason(text);
//			mdG.setHacienda(text);
//			mdG.setModel(text);
//			mdG.setAmount(text);
//			mdG.setPresentationDate(text);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
