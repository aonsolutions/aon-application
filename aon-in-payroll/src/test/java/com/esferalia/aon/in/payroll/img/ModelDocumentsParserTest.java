package com.esferalia.aon.in.payroll.img;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.modAlava.Mod1152023AEAT;
import com.esferalia.aon.in.payroll.pdf.modAlava.Mod1152023Alava;
import com.esferalia.aon.in.payroll.pdf.modBizkaia.Mod1152023Bizkaia;
import com.esferalia.aon.in.payroll.pdf.modGipuzkoa.Mod1152023Gipuzkoa;
import com.esferalia.aon.in.payroll.pdf.modNavarra.Mod7152023Navarra;


public class ModelDocumentsParserTest {

	Mod1152023Bizkaia modeloBizkaia = new Mod1152023Bizkaia();
	Mod1152023Gipuzkoa modeloGipuzkoa = new Mod1152023Gipuzkoa();
	Mod1152023Alava modeloAlava = new Mod1152023Alava();
	Mod7152023Navarra modeloNavarra = new Mod7152023Navarra();
	Mod1152023AEAT modeloAEAT = new Mod1152023AEAT();
	PDFExtracter pdfExtracter = new PDFExtracter();
	
	
	@Test
	public void Model1152023BizkaiaTest() throws Exception {
		InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Bizkaia/MOD 115 2T 2023 LAETITIA FERREIRO.PDF");
		byte [] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		System.out.println(text);
		modeloBizkaia.parser(text);
	}
	
	@Test
	public void Model1152023GipuzkoaTest() throws Exception {
		InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Guipuzkoa/MOD 115 3T 2023 GOENA.pdf");
		byte[] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		System.out.println(text);
		modeloGipuzkoa.parser(text);
	}
	
	@Test
	public void Model115A2023AlavaTest() throws Exception {
		InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Alava/MODELO 4T 115 DEPO 2022.pdf");
		byte[] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		modeloAlava.parser(text);
	}
	
	@Test
	public void Model1152023NavarraTest() throws Exception {
		InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Navarra/MOD 715 3T 2023 BLAS OLIVA.pdf");
		byte [] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		modeloNavarra.parser( text);
		
	}
	
	@Test
	public void Model1152023AEATest() throws Exception{
		InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/AEAT/MOD 115 3T 2023 LNG.pdf");
		byte [] bytes = IOUtils.toByteArray(is);
		String text = pdfExtracter.extract(bytes);
		modeloAEAT.parser(text);
	}
	
	
}
