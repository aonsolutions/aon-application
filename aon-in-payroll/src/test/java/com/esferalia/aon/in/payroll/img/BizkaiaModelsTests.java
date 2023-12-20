package com.esferalia.aon.in.payroll.img;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.printing.Orientation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.InputStream;

import com.esferalia.aon.in.payroll.pdf.modBizkaia.Mod1102023Bizkaia;
import com.esferalia.aon.in.payroll.pdf.modBizkaia.Mod1152023Bizkaia;
import com.esferalia.aon.in.payroll.pdf.modBizkaia.Mod1802023Bizkaia;
import com.esferalia.aon.in.payroll.pdf.modBizkaia.Mod1902023Bizkaia;
import com.esferalia.aon.in.payroll.pdf.modBizkaia.Mod3032023Bizkaia;
import com.esferalia.aon.in.payroll.pdf.modBizkaia.Mod3492023Bizkaia;
import com.esferalia.aon.in.payroll.pdf.modBizkaia.Mod3902023Bizkaia;

public class BizkaiaModelsTests {
	PDFExtracter pdfExtracter = new PDFExtracter();

	@Test
	public void mod110BizkaiaTest() throws Exception {
		Mod1102023Bizkaia mod110 = new Mod1102023Bizkaia();
		InputStream is = new FileInputStream(
				"/home/jmortega/Documentos/Modelos/Bizkaia/2 - MOD 110 ELDORADO 2T 2023 BIZKAIA.PDF");
		byte[] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		String nif = mod110.setNif(text);
		String declarant = mod110.setDeclarant(text);
		String presenter = mod110.setPresenter(text);
		String presenterNif = mod110.setPresenterNif(text);
		String email = mod110.setEmail(text);
		String phoneNumber = mod110.setPhoneNumber(text);
		String amount = mod110.setAmount(text);
		String yearAndperiod = mod110.setYearAndPeriod(text);
		String model = mod110.setModel(text);

		assertEquals("B95541520", nif);
		assertEquals("ELDORADO AUTOMOTIVE SL", declarant);
		assertEquals("VERA NUÑEZ MARIA SILVERIA", presenter);
		assertEquals("50604707S", presenterNif);
		assertEquals("notificacionesforales@ayudatpymes.com", email);
		assertEquals("655044226", phoneNumber);
		assertEquals("6.089,28", amount);
		assertEquals("Año : 2023 Periodo : TRIM2", yearAndperiod);
		assertEquals("110", model);
	}

	@Test
	public void mod1152023BizkaiaTest() throws Exception {
		Mod1152023Bizkaia mod115 = new Mod1152023Bizkaia();
		InputStream is = new FileInputStream(
				"/home/jmortega/Documentos/Modelos/Bizkaia/MOD 115 2T 2023 LAETITIA FERREIRO.PDF");
		byte[] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		
		String nif = mod115.setNif(text);
		assertEquals("X8657717B", nif);
		String email = mod115.setEmail(text);
		assertEquals("NOTIFICACIONESFORALES@AYUDATPYMES.ES", email);
		String amount = mod115.setAmount(text);
		assertEquals("171,00", amount);
		String declarant = mod115.setDeclarant(text);
		assertEquals("FERREIRO LAETITIA VANESSA", declarant);
		String presenter = mod115.setPresenter(text);
		assertEquals("VERA NUÑEZ MARIA SILVERIA" , presenter);
		String hacienda = mod115.setHacienda(text);
		assertEquals("Diputacion foral de Bizkaia" , hacienda);
		String year = mod115.setYear(text);
		assertEquals("2023" , year);
		String period = mod115.setPeriod(text);
		assertEquals("TRIM2", period);
	}

	@Test
	public void mod1802023BizkaiaTest() throws Exception {
		Mod1802023Bizkaia mod180 = new Mod1802023Bizkaia();
		InputStream is = new FileInputStream(
				"/home/jmortega/Documentos/Modelos/Bizkaia/4 - MOD 180 ELDORADO 2022 BIZKAIA.PDF");
		byte[] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		String nif = mod180.setNifDeclarant(text);
		assertEquals("B95541520", nif);
		String issueDate = mod180.setIssueDate(text);
		assertEquals("30 DE ENERO DE 2023",issueDate);
		String declarantName = mod180.setNameDeclarant(text);
		assertEquals("ELDORADO AUTOMOTIVE SL", declarantName);
		String contactPerson = mod180.setContactPerson(text);
		assertEquals("MUÑOZ GARCIA JESUS" , contactPerson);
		String phoneNumber = mod180.setPhoneNumber(text);
		assertEquals("691568973" , phoneNumber);
		String exercise = mod180.setExercise(text);
		assertEquals("2022" ,exercise);
		String amount = mod180.setAmount(text);
		assertEquals("4.280,74", amount);
		String model = mod180.setModel(text);
		assertEquals("180",model);
	}

	@Test
	public void mod1902023BizkaiaTest() throws Exception {
		Mod1902023Bizkaia mod190 = new Mod1902023Bizkaia();
		InputStream is = new FileInputStream(
				"/home/jmortega/Documentos/Modelos/Bizkaia/5 - MOD 190 ELDORADO 2022 BIZKAIA.PDF");
		byte [] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		String nif = mod190.setNifDeclarant(text);
		assertEquals("B95541520" , nif);
		String name = mod190.setNameDeclarant(text);
		assertEquals("ELDORADO AUTOMOTIVE SL" , name);
		String contactPerson = mod190.setContactPerson(text);
		assertEquals("MUÑOZ GARCIA JESUS", contactPerson);
		String email = mod190.setEmail(text);
		assertEquals("JESUSM.GARCIA@AYUDATPYMES.ES", email);
		String phonenumber = mod190.setPhoneNumber(text);
		assertEquals("691568973", phonenumber);
		String amount = mod190.setAmount(text);
		assertEquals("24.562,77" , amount);
		String issueDate = mod190.setIssueDate(text);
		assertEquals("31 DE ENERO DE 2023", issueDate);

	}
	
	@Test
	public void mod3032023BizkaiaTest() throws Exception{
		Mod3032023Bizkaia mod303 = new Mod3032023Bizkaia();
		InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Bizkaia/1 - MOD 303 ELDORADO 2T 2023 - BIZKAIA.PDF");
		byte [] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		String nif = mod303.setPersonNif(text);
		assertEquals("B95541520", nif);
		String name = mod303.setPersonName(text);
		assertEquals("ELDORADO AUTOMOTIVE SL" , name);
		String email = mod303.setEmail(text);
		assertEquals("GESTION@AUTOSELDORADO.COM", email);
		String phoneNumber  =mod303.setPhoneNumber(text);

		assertEquals("658825867", phoneNumber);
		String principalActivity = mod303.setPrincipalActivity(text);

		assertEquals("COMERCIO AL POR MAYOR DE", principalActivity);
		String presentatorNif = mod303.setPresentatorNif(text);
		assertEquals("50604707S" , presentatorNif);
		String presentatorName = mod303.setPresentatorName(text);
		assertEquals("VERA NUÑEZ MARIA SILVERIA", presentatorName);
		String amount = mod303.setAmount(text);
		assertEquals("1.510,06", amount);
		String year = mod303.setYear(text);
		assertEquals("2023" , year);
		String period = mod303.setPeriod(text);
		assertEquals("TRIM2",period);
		String issueDate = mod303.setIssueDate(text);
		assertEquals("25 DE JULIO DE 2023" , issueDate);
		String hacienda = mod303.setHacienda(text);
		assertEquals("Bizkaia" , hacienda);
		
		
		
	}
	
	@Test
	public void mod3492023BizkaiaTest() throws Exception{
		Mod3492023Bizkaia mod349 = new Mod3492023Bizkaia();
		InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Bizkaia/7 - 7. MOD 349 JULIO 2021 ELDORADO BIZKAIA.PDF");
		byte [] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		String nif = mod349.setNifDeclarant(text);
		assertEquals("B95541520", nif);
		String nameDeclarant = mod349.setNameDeclarant(text);
		assertEquals("ELDORADO AUTOMOTIVE SL" , nameDeclarant);
		String contactPerson = mod349.setContactPerson(text);
		assertEquals("MUÑOZ GARCIA JESUS", contactPerson);
		String email = mod349.setEmail(text);
		assertEquals("JESUSM.GARCIA@AYUDATPYMES.ES" , email);
		String phonenumber = mod349.setPhoneNumber(text);
		assertEquals("691568973", phonenumber);
		String representativeNif = mod349.setRepresentativeNif(text);
		assertEquals("4976686W", representativeNif);
		String representativeName = mod349.setRepresentativeName(text);
		assertEquals("HERRERO VALLINAS YOLANDA", representativeName);
 	}
	
	@Test
	public void mod3902023BizkaiaTest() throws Exception {
		Mod3902023Bizkaia mod390 = new Mod3902023Bizkaia();
		InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Bizkaia/6 - MOD 390 ELDORADO 2022 BIZKAIA.PDF");
		byte[] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		System.out.println(text);
		String nif = mod390.setNif(text);
		assertEquals("B95541520", nif);
		String name = mod390.setName(text);
		assertEquals("ELDORADO AUTOMOTIVE SL", name);
		String phoneNumber = mod390.setPhoneNumber(text);
		assertEquals("658825867" , phoneNumber);
		String email = mod390.setEmail(text);
		assertEquals("GESTION@AUTOSELDORADO.COM" , email);
		String presentatorName = mod390.setPresentatorName(text);
		assertEquals("VERA NUÑEZ MARIA SILVERIA", presentatorName);
		String amount = mod390.setAmount(text);
		assertEquals("18.369,75", amount);
		
	}
	

}
