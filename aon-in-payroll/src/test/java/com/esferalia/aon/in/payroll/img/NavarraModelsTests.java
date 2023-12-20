package com.esferalia.aon.in.payroll.img;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.modNavarra.ParserUtils;
import com.esferalia.aon.in.payroll.pdf.modNavarra.Mod1302023Navarra;
import com.esferalia.aon.in.payroll.pdf.modNavarra.Mod7152023Navarra;
import com.esferalia.aon.in.payroll.pdf.modNavarra.ModF692023Navarra;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.InputStream;

public class NavarraModelsTests {
	PDFExtracter pdfExtracter = new PDFExtracter();
	ParserUtils pu = new ParserUtils();

	@Test
	public void mod130NavarraTest() throws Exception{
		Mod1302023Navarra mod130 = new Mod1302023Navarra();
		InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Navarra/1 - MODELO 130 3T 2023 OLIVER - NAVARRA.pdf");

		byte[] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		System.out.println(text);
		
		String nif = mod130.setNif(text);
		assertEquals("78773433C" , nif);
		String email = mod130.setEmail(text);
		assertEquals("OLIVERARBIOL1994@GMAIL.COM", email);
		String phoneNumber = mod130.setPhoneNumber(text);
		assertEquals("654825913", phoneNumber);
		String periodAndYear = mod130.setPeriodAndYear(text);
		assertEquals("2023 T3" , periodAndYear);
		String amount = mod130.setAmount(text);
		assertEquals("0,00" , amount);
		String hacienda = mod130.setHacienda(text);
		assertEquals("Hacienda Navarra" , hacienda);	
	}
	
	@Test
	public void mod715NavarraTest() throws Exception{
		Mod7152023Navarra mod715 = new Mod7152023Navarra();
		InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Navarra/MOD 715 3T 2023 BLAS OLIVA.pdf");

		byte[] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		System.out.println(text);
		
		String name = mod715.setSocialReasonName(text);
		assertEquals("BLAS OLIVA SL" , name);
		String registryNumber = mod715.setRegistryNumber(text);
		assertEquals("92677" , registryNumber);
		String nif = mod715.setNif(text);
		assertEquals("B71001309" , nif);
		String email = mod715.setEmail(text);
		assertEquals("BEATRIZGONZALEZ@AYUDATPYMES.COM" , email);
		String phoneNumber = mod715.setPhoneNumber(text);
		assertEquals("691570158",phoneNumber);
		String iban = mod715.setIBAN(text);
		assertEquals("ES52 3008 0192 57 3490616020" , iban);
		String periodAndYear = mod715.setPeriodAndYear(text);
		assertEquals("T3 2023" , periodAndYear);
		String issueDate = mod715.setIssueDate(text);
		assertEquals("18/10/2023", issueDate);
		String sign = mod715.setSign(text);
		assertEquals("2194ECF68A8E936C13C84F5BF8B122A7" , sign);
		String csv = mod715.setCsv(text);
		assertEquals("A0098468DEEE3F41" , csv);
		String amount = mod715.setAmount(text);
		assertEquals("318,84" , amount);
		String hacienda = mod715.setHacienda(text);
		assertEquals("Hacienda Navarra" , hacienda);	
		System.out.println(hacienda);
	}
	
	@Test
	public void modF69NavarraTest() throws Exception{
		ModF692023Navarra modF69 = new ModF692023Navarra();
		InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Navarra/2 - MODELO F69 3T 2023 OLIVER - NAVARRA.pdf");
		byte[] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		System.out.println(text);
		
		String socialReasonName = modF69.setSocialReasonName(text);
		assertEquals("C ARBIOL, LUCIO, OLIVER", socialReasonName);
		String nif = modF69.setNif(text);
		assertEquals("78773433C" , nif);
		String email = modF69.setEmail(text);
		assertEquals("OLIVERARBIOL1994@GMAIL.COM", email);
		String phoneNumber = modF69.setPhoneNumber(text);
		assertEquals("654825913" , phoneNumber);
		String periodAndYear = modF69.setPeriodAndYear(text);
		assertEquals("2023 T3", periodAndYear);
		String amount = modF69.setAmount(text);
		assertEquals("-34,62", amount);
		String hacienda = modF69.setHacienda(text);
		assertEquals("Hacienda Navarra", hacienda);
		String model = modF69.setModel(text);
		assertEquals("F69", model);
	}
	
}
