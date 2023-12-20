package com.esferalia.aon.in.payroll.img;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.modGipuzkoa.Mod1102023Gipuzkoa;
import com.esferalia.aon.in.payroll.pdf.modGipuzkoa.Mod1152023Gipuzkoa;
import com.esferalia.aon.in.payroll.pdf.modGipuzkoa.Mod1302023Gipuzkoa;
import com.esferalia.aon.in.payroll.pdf.modGipuzkoa.Mod1802023Gipuzkoa;
import com.esferalia.aon.in.payroll.pdf.modGipuzkoa.Mod2002023Gipuzkoa;
import com.esferalia.aon.in.payroll.pdf.modGipuzkoa.Mod3002023Gipuzkoa;
import com.esferalia.aon.in.payroll.pdf.modGipuzkoa.Mod3492023Gipuzkoa;
import com.esferalia.aon.in.payroll.pdf.modGipuzkoa.Mod39020223Gipuzkoa;
import com.esferalia.aon.in.payroll.pdf.modGipuzkoa.ParserUtils;

public class GuipuzkoaModelsTests {
	
	PDFExtracter pdfExtracter = new PDFExtracter();
	ParserUtils pu = new ParserUtils();

	
	@Test
	public void mod110GipuzkoaTest() throws Exception {
		Mod1102023Gipuzkoa mod110 = new Mod1102023Gipuzkoa();
		InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Guipuzkoa/4 - 110 3T 2021 ROBERTO GIPUZKOA.pdf");

		byte[] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		System.out.println(text);
		
		String identifyNif = mod110.setIdentifyNif(text);
		assertEquals("15387202H", identifyNif);
		String identifyName = mod110.setIdentifyName(text);
		assertEquals("DE BLAS BOAL ROBERTO", identifyName);
		String year = mod110.setYear(text);
		assertEquals("2021", year);
		String period = mod110.setPeriod(text); 
		assertEquals("3 Trimestre", period);
		String amount = mod110.setAmount(text);
		assertEquals("6,79", amount);
		String issueDate = mod110.setIssueDate(text);
		assertEquals("25/10/2021", issueDate);
		System.out.println(issueDate);
	}
	
	@Test
	public void mod115GipuzkoaTest() throws Exception {
		Mod1152023Gipuzkoa mod115 = new Mod1152023Gipuzkoa();
		InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Guipuzkoa/MOD 115 3T 2023 GOENA.pdf");
		byte[] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		System.out.println(text);

		String nif = mod115.setNif(text);
		assertEquals("B20098919" , nif);
		String name = mod115.setSocialReason(text);
		assertEquals("GOENA SL" , name);
		String amount = mod115.setAmount(text);
		assertEquals("741,0007" , amount);
		String hacienda = mod115.setHacienda(text);
		assertEquals("Diputacion foral de Gipuzkoa", hacienda);
		String presentationDate = mod115.setPresentationDate(text);
		assertEquals("13/10/2023", presentationDate);
		String exercise = mod115.setExercise(text);
		assertEquals("2023" , exercise);
		String period = mod115.setPeriod(text);
		assertEquals("3er Trimestre", period);
		
	}
	
	@Test
	public void mod130GipuzkoaTest() throws Exception{
		Mod1302023Gipuzkoa mod130 = new Mod1302023Gipuzkoa();
		InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Guipuzkoa/1 - MOD 130 ROBERTO 4T 2022 GIPUZKOA.pdf");
		byte[] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		System.out.println(text);
		String nif = mod130.setDeclarantNif(text);
		assertEquals("15387202H" , nif);
		String name = mod130.setDeclarantName(text);
		assertEquals("DE BLAS BOAL ROBERTO", name);
		String principalActivity = mod130.setPrincipalActivity(text);
		assertEquals("VENTA MENOR LABORES DEL TABACO EN EXPEND" , principalActivity);
		String amount = mod130.setAmount(text);
		assertEquals("54,71" , amount);
		String hacienda = mod130.setHacienda(text);
		assertEquals("gipuzkoa", hacienda);
		String model = mod130.setModel(text);
		assertEquals("130", model);
		String exercise = mod130.setExercise(text);
		assertEquals("4º Trimestre" , exercise);
	}
	
	
	//REVISAR 
	@Test
	public void mod180GipuzkoaTest() throws Exception{
		Mod1802023Gipuzkoa mod180 = new Mod1802023Gipuzkoa();
		InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Guipuzkoa/2 - MOD 180 ROBERTO 2022 GIPUZKOA.pdf");
		byte[] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		System.out.println(text);
		String declarantNif = mod180.setDeclarantNif(text);
		assertEquals("15387202H" , declarantNif);
		String identifyName = mod180.setIdentifyName(text);
		assertEquals("DE BLAS BOAL ROBERTO" , identifyName);
		String phoneNumber = mod180.setPhoneNumber(text);
		assertEquals("691568973" , phoneNumber);
		String exercise = mod180.setExercise(text);
		assertEquals("2022", exercise);
		String amount = mod180.setAmount(text);
		assertEquals("570,00" , amount);
		String perceivers = mod180.setPerceivers(text);
		assertEquals("1", perceivers);
		String amountsPaid = mod180.setAmountsPaid(text);
		assertEquals("3.000,00" , amountsPaid);
	}
	
	@Test
	public void mod200GipuzkoaTest() throws Exception{
		Mod2002023Gipuzkoa mod200 = new Mod2002023Gipuzkoa();
		InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Guipuzkoa/1 - MOD 200 FIDCOMMERCE 2022 GIPUZKOA.pdf");
		byte[] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		System.out.println(text);
		
		String declarantNif = mod200.setDeclarantNif(text);
		assertEquals("B75121103" , declarantNif);
		String declarantName = mod200.setDeclarantName(text);
		assertEquals("FIDCOMMERCE SL", declarantName);
		String relationPersonNif = mod200.setRelationPersonNif(text);
		assertEquals("B93011708" , relationPersonNif);
		String relationPersonName = mod200.setRelationPersonName(text);
		assertEquals("JESUS MUÑOZ", relationPersonName);
		String email = mod200.setEmail(text);
		assertEquals("JESUSM.GARCIA@AYUDATPYMES.ES", email);
		String phoneNumber = mod200.setPhoneNumber(text);
		assertEquals("691568973" , phoneNumber);
		String legalRepresentator = mod200.setLegalRepresntators(text);
		assertEquals("NOTARIA : JAVIER OÑATE CUADROS", legalRepresentator);
	}
	
	@Test
	public void mod300GipuzkoaTest() throws Exception {
		Mod3002023Gipuzkoa mod300 = new Mod3002023Gipuzkoa();
		InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Guipuzkoa/6 - 300 3T 2021 ROBERTO GIPUZKOA.pdf");
		byte [] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		System.out.println(text);
		
		String nif = mod300.setNif(text);
		assertEquals("15387202H", nif);
		String name = mod300.setName(text);
		assertEquals("DE BLAS BOAL ROBERTO" , name);
		String period = mod300.setPeriod(text);
		assertEquals("3 Trimestre", period);
		String exercise = mod300.setExercise(text);
		assertEquals("2021" , exercise);
		String amount = mod300.setAmount(text);
		assertEquals("199,39", amount);
		String issueDate = mod300.setIssueDate(text);
		assertEquals("25/10/2021" , issueDate);
		
 	}
	
	@Test
	public void mod349GipuzkoaTest() throws Exception{
		Mod3492023Gipuzkoa mod349 = new Mod3492023Gipuzkoa();
		InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Guipuzkoa/2 - MOD 349 FIDCOMMERCE 2T 2023 GIPUZKOA.pdf");
		byte [] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		System.out.println(text);
		
		String identifyNif = mod349.setIdentifyNif(text);
		assertEquals("B75121103" , identifyNif);
		String identifyName = mod349.setIdentifyName(text);
		assertEquals("FIDCOMMERCE SL" , identifyName);
		String exercise = mod349.setExercise(text);
		assertEquals("23", exercise);
		String period = mod349.setPeriod(text);
		assertEquals("2T" , period);
		String amount = mod349.setAmount(text);
		assertEquals("2.424,29" , amount);
		List<String> lista = mod349.operationsLines(text);
		System.out.println(lista);
	}
	
	@Test
	public void mod390GipuzkoaTest() throws Exception{
		Mod39020223Gipuzkoa mod390 = new Mod39020223Gipuzkoa();
		InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Guipuzkoa/4 - MOD 390 FIDCOMMERCE 2022 GIPUZKOA.pdf");
		byte [] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		System.out.println(text);
		
		String nif = mod390.setNif(text);
		assertEquals("B75121103" , nif);
		String name = mod390.setname(text);
		assertEquals("FIDCOMMERCE SL", name);
		String issueDate = mod390.setIssueDate(text);
		assertEquals("31/01/2023", issueDate);
		String amount = mod390.setAmount(text);
		assertEquals("4.685,07" , amount);
		String hacienda = mod390.setHacienda(text);
		assertEquals("Diputacion Foral de Gipuzkoa", hacienda);
		System.out.println(issueDate);
		System.out.println(name);
	}
	
	
	
	
}
