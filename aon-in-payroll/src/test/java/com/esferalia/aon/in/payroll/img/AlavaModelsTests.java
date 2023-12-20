package com.esferalia.aon.in.payroll.img;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.modAlava.Mod1152023Alava;
import com.esferalia.aon.in.payroll.pdf.modAlava.Mod3002023Alava;
import com.esferalia.aon.in.payroll.pdf.modAlava.Mod3492023Alava;
import com.esferalia.aon.in.payroll.pdf.modAlava.ParserUtils;

public class AlavaModelsTests {

	PDFExtracter pdfExtracter = new PDFExtracter();
	ParserUtils pu = new ParserUtils();
	
	
	@Test
	public void mod115AlavaTest() throws Exception {
		
		Mod1152023Alava mod115A = new Mod1152023Alava();
		InputStream is = new FileInputStream(
				"/home/jmortega/Documentos/Modelos/Alava/MODELO 4T 115 DEPO 2022.pdf");	
	
		byte[] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		System.out.println(text);
		
		String nif = mod115A.setNif(text);
		assertEquals("B8586644 0", nif);		
		String period = mod115A.setPeriod(text);
		String periodo = pu.obtenerTrimestre(period);
		assertEquals("4º Trimestre" , periodo);
		String name = mod115A.setName(text);
		assertEquals("DEPOCONSULTING SL", name);
		String exercise = mod115A.setExercise(text);
		assertEquals("2022" , exercise);
		String amount = mod115A.setAmount(text);
		assertEquals("279,40", amount);
		String leases = mod115A.setLeases(text);
		assertEquals("1.470,59" , leases);
		String withHoldings = mod115A.setWithHoldings(text);
		assertEquals("279,40", withHoldings);
		String lessors = mod115A.setLessors(text);
		assertEquals("1", lessors);
		String hacienda = mod115A.setHacienda(text);
		assertEquals("Diputacion Foral de Alava" , hacienda );
		String model = mod115A.setModel(text);
		assertEquals("115A", model);
	}
	
	@Test
	public void mod303AlavaTest() throws Exception{
		
		Mod3002023Alava mod300 = new Mod3002023Alava();
		InputStream is = new FileInputStream(
				"/home/jmortega/Documentos/Modelos/Alava/aonSolutions - M.303 (2023-1T).pdf");	
		byte[] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		System.out.println(text);
		
		String declarantNif = mod300.setDeclarantNif(text);
		assertEquals("B0148727", declarantNif);
		String declarantName = mod300.setDeclarantName(text);
		assertEquals("AON SOLUTIONS SLU" , declarantName);
		String presentatorName = mod300.setPresentatorName(text);
		assertEquals("BK CONSULTING ABOGADOS &", presentatorName);
		String presentatorNif = mod300.setPresentatorNif(text);
		assertEquals("A0131431" , presentatorNif);
		String model = mod300.setModel(text);
		assertEquals("303" ,model);
		String period = mod300.setPeriod(text);
		String periodo = pu.obtenerTrimestre(period);
		assertEquals("1º Trimestre" , periodo);
		String exercise = mod300.setExercise(text);
		assertEquals("2023", exercise);
		String amount = mod300.setAmount(text);
		assertEquals("31.664,82", amount);
		String typeOfPayment = mod300.setTypeOfPayment(text);
		assertEquals("Domiciliación" , typeOfPayment);
		String issueDate = mod300.setIssueDate(text);
		assertEquals("2023-05-02", issueDate);
	}
	
	@Test
	public void mod349AlavaTest() throws Exception{
		Mod3492023Alava mod349 = new Mod3492023Alava();
		InputStream is = new FileInputStream(
				"/home/jmortega/Documentos/Modelos/Alava/aonSolutions - M.349 (2023-2T).pdf");
		byte [] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		System.out.println(text);
		String declarantNif = mod349.setDeclarantNif(text);
		assertEquals("B01487271" , declarantNif);
		String declarantName = mod349.setDeclarantName(text);
		assertEquals("AON SOLUTIONS SLU", declarantName);
		String model = mod349.setModel(text);
		assertEquals("349", model);
		String exercise = mod349.setExercise(text);
		assertEquals("2.023" , exercise);
		String period = mod349.setPeriod(text);
		assertEquals("2T" , period);
		String issueDate = mod349.setIssueDate(text);
		assertEquals("19-07-2023" , issueDate);
		String amount = mod349.setAmount(text);
		assertEquals("2.176" , amount);
		List<String> operatorsNif = mod349.setOperatorsNif(text);
		List<String> operatorsNifTest = new ArrayList<>();
		operatorsNifTest.add("IE-9692928F");
		assertEquals(operatorsNifTest, operatorsNif);
		
	}
}
