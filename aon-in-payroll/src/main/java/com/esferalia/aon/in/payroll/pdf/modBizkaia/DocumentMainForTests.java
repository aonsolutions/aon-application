package com.esferalia.aon.in.payroll.pdf.modBizkaia;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class DocumentMainForTests {

	public static void main(String[] args) {
		try {

			ParserUtils pu = new ParserUtils();

			Mod1152023Bizkaia mdB = new Mod1152023Bizkaia();
			Mod1102023Bizkaia md110b = new Mod1102023Bizkaia();
			Mod1802023Bizkaia md180b = new Mod1802023Bizkaia();
			Mod1902023Bizkaia md190b = new Mod1902023Bizkaia();
			Mod3492023Bizkaia md349b = new Mod3492023Bizkaia();
			Mod3902023Bizkaia md390b = new Mod3902023Bizkaia();
			Mod3032023Bizkaia md303b = new Mod3032023Bizkaia();
			Mod2002023Bizkaia md200b = new Mod2002023Bizkaia();

//			InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Bizkaia/MOD 115 2T 2023 LAETITIA FERREIRO.PDF");
//			InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Bizkaia/2 - MOD 110 ELDORADO 2T 2023 BIZKAIA.PDF");
//			InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Bizkaia/4 - MOD 180 ELDORADO 2022 BIZKAIA.PDF");
//			InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Bizkaia/5 - MOD 190 ELDORADO 2022 BIZKAIA.PDF");
//			InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Bizkaia/6 - MOD 390 ELDORADO 2022 BIZKAIA.PDF");
//			InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Bizkaia/1 - MOD 303 ELDORADO 2T 2023 - BIZKAIA.PDF");
//			InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Bizkaia/3 - MOD 200 ELDORADO 2022 BIZKAIA.pdf");
//			InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Bizkaia/7 - 7. MOD 349 JULIO 2021 ELDORADO BIZKAIA.PDF");
			InputStream is = new FileInputStream(
					"/home/jmortega/Documentos/Modelos/Bizkaia/MOD 115 2T 2023 LAETITIA FERREIRO.PDF");

			byte[] bytes = IOUtils.toByteArray(is);

			PDFExtracter pdf = new PDFExtracter();
			String text = pdf.extract(bytes);
			System.out.println(text);

//TODO		FUNCIONES SETMODEL, SETHACIENDA, SETYEARANDPERIOD EN CADA CLASE
//TODO      LIMPIAR CODIGO (funciones, variables, orden)
//TODO      CAMBIAR VOID POR STRING O OBJETO 
//TODO 		CAMBIAR EN REGEX LOS ACENTOS POR NOMENCLATURA CORRESPONDIENTE
//TODO 		QUITAR AVISOS SONARLINT REGEX

			// MODELO 200 BIZKAIA
//			String nif = md200b.setDeclarantNif(text);
//			System.out.println(nif);
//			String name = md200b.setDeclarantName(text);
//			System.out.println(name);
//			String amount = md200b.setAmount(text);
//			System.out.println(amount);
//			String iban = md200b.setIban(text);
//			System.out.println(iban);
//			String principalActivity = md200b.setPrincipalActivity(text);
//			System.out.println(principalActivity);
//			String model = md200b.setModel(text);
//			System.out.println(model);

			// MODELO 303 BIZKAIA
//			String nif = md303b.setPersonNif(text);
//			System.out.println(nif);
//			String personName = md303b.setPersonName(text);
//			System.out.println(personName);
//			String email = md303b.setEmail(text);
//			System.out.println(email);
//			String phoneNumber = md303b.setPhoneNumber(text);
//			System.out.println(phoneNumber);
//			String principalActivity = md303b.setPrincipalActivity(text);
//			System.out.println(principalActivity);
//			String presentatorNif = md303b.setPresentatorNif(text);
//			System.out.println(presentatorNif);
//			String presentatorName = md303b.setPresentatorName(text);
//			System.out.println(presentatorName);
//			String amount = md303b.setAmount(text);
//			System.out.println(amount);
//			String model = md303b.setModel(text);
//			System.out.println(model);
//			md303b.setYearAndPeriod(text);
//			String issueDate = md303b.setIssueDate(text);
//			System.out.println(issueDate);
//			String hacienda = md303b.setHacienda(text);
//			System.out.println(hacienda);

			// MODELO 390 BIZKAIA
//			String nif = md390b.setNif(text);
//			System.out.println(nif);
//			
//			String name = md390b.setName(text);
//			System.out.println(name);
//			
//			String phoneNumber = md390b.setPhoneNumber(text);
//			System.out.println(phoneNumber);
//			
//			String email = md390b.setEmail(text);
//			System.out.println(email);
//			
//			String principalActivity = md390b.setPrincipalActivity(text);
//			System.out.println(principalActivity);
//			
//			String presentatorName = md390b.setPresentatorName(text);
//			System.out.println(presentatorName);
//			
//			String amount = md390b.setAmount(text);
//			System.out.println(amount);					
//			md390b.setYearAndPeriod(text);

			// MODELO 349 BIZKAIA
//			String nif = md349b.setNifDeclarant(text);
//			System.out.println(nif);
//			String nameDeclarant = md349b.setNameDeclarant(text);
//			System.out.println(nameDeclarant);
//			String nameContact = md349b.setContactPerson(text);
//			System.out.println(nameContact);
//			String phoneNumber = md349b.setPhoneNumber(text);
//			System.out.println(phoneNumber);
//			String email = md349b.setEmail(text);
//			System.out.println(email);
//			String representativeNif = md349b.setRepresentativeNif(text);
//			System.out.println(representativeNif);
//			String representativeName = md349b.setRepresentativeName(text);
//			System.out.println(representativeName);

			// MODELO 190 BIZKAIA
//			String nif = md190b.setNifDeclarant(text);
//			System.out.println(nif);
//			String name = md190b.setNameDeclarant(text);
//			System.out.println(name);
//			String contactPerson = md190b.setContactPerson(text);
//			System.out.println(contactPerson);
//			String email = md190b.setEmail(text);
//			System.out.println(email);
//			String phoneNumber = md190b.setPhoneNumber(text);
//			System.out.println(phoneNumber);
//			String amount = md190b.setAmount(text);
//			System.out.println(amount);
//			String issueDate = md190b.setIssueDate(text);
//			System.out.println(issueDate);

			// MODELO 118 BIZKAIA
//			String nif = md180b.setNifDeclarant(text);
//			System.out.println(nif);
//			String declarantName = md180b.setNameDeclarant(text);
//			System.out.println(declarantName);
//			String contactPerson = md180b.setContactPerson(text);
//			System.out.println(contactPerson);
//			String phoneNumber = md180b.setPhoneNumber(text);
//			System.out.println(phoneNumber);
//			String amount = md180b.setAmount(text);
//			System.out.println(amount);
//			md180b.setModel(text);
//			String exercise = md180b.setExercise(text);
//			System.out.println(exercise);
//			String issueDate = md180b.setIssueDate(text);
//			System.out.println(issueDate);

			// MODELO 110 BIZKAIA
//			String nif = md110b.setNif(text);
//			System.out.println(nif);
//			String email = md110b.setEmail(text);
//			System.out.println(email);
//			
//			String phoneNumber = md110b.setPhoneNumber(text);
//			System.out.println(phoneNumber);
//			
//			String presenterNif = md110b.setPresenterNif(text);
//			System.out.println(presenterNif);
//			
//			md110b.setAmount(text);
//			md110b.setYearAndPeriod(text);
//			md110b.setDeclarant(text);
//			md110b.setPresenter(text);
//			md110b.setModel(text);

			// MODELO 115 BIZKAIA
//			mdB.setEmail(text);
//			mdB.setNif(text);
//			mdB.setYearAndPeriod(text);
//			mdB.setAmount(text);
//			mdB.setDeclarant(text);
//			mdB.setPresenter(text);
//			mdB.setEmail(text);
//			mdB.setHacienda(text);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
