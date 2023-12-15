package com.esferalia.aon.in.payroll.pdf.modAlava;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class DocumentMainForTests {

	public static void main(String[] args) {
		try {

			ParserUtils pu = new ParserUtils();

			Mod1152023Alava mdA = new Mod1152023Alava();

			Mod1152023AEAT aeat = new Mod1152023AEAT();
			
			Mod3002023Alava mod300 = new Mod3002023Alava();
			
			Mod3492023Alava mod349 = new Mod3492023Alava();

//			InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Alava/MODELO 4T 115 DEPO 2022.pdf");

//			InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/AEAT/MOD 115 3T 2023 LNG.pdf");
//			InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Alava/aonSolutions - M.303 (2023-1T).pdf");
		//	InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Alava/aonSolutions - M.349 (2023-1T).pdf");
			InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/Alava/aonSolutions - M.349 (2023-3T).pdf");

			
			
			
			byte[] bytes = IOUtils.toByteArray(is);

			PDFExtracter pdf = new PDFExtracter();
			String text = pdf.extract(bytes);
			System.out.println(text);

			
			//MODELO 349 ALAVA
			
			String model = mod349.setModel(text);
			System.out.println(model);
			String nif = mod349.setDeclarantNif(text);
			System.out.println(nif);
			String name = mod349.setDeclarantName(text);
			System.out.println(name);
			String exercise = mod349.setExercise(text);
			System.out.println(exercise);
			String period = mod349.setPeriod(text);
			System.out.println(period);
			String result = pu.obtenerTrimestre(period);
			System.out.println(result);
			String issueDate = mod349.setIssueDate(text);
			System.out.println(issueDate);
			String amount = mod349.setAmount(text);
			System.out.println(amount);
			List<String> operatorsNif = mod349.setOperatorsNif(text);
			System.out.println(operatorsNif);
			
			
			
			//MODELO 303 ALAVA
//			String nif = mod300.setDeclarantNif(text);
//			System.out.println(nif);
//			String name = mod300.setDeclarantName(text);
//			System.out.println(name);
//			String presentatorName = mod300.setPresentatorName(text);
//			System.out.println(presentatorName);
//			String presentatorNif = mod300.setPresentatorNif(text);
//			System.out.println(presentatorNif);
//			String model = mod300.setModel(text);
//			System.out.println(model);
//			String period = mod300.setPeriod(text);
//			System.out.println(period);
//			String exercise = mod300.setExercise(text);
//			System.out.println(exercise);
//			String amount = mod300.setAmount(text);
//			System.out.println(amount);
//			String typeOfPayment = mod300.setTypeOfPayment(text);
//			System.out.println(typeOfPayment);
//			String issueDate = mod300.setIssueDate(text);
//			System.out.println(issueDate);
//			
//			  String rangoFechas = "202301-202303";
//		        String resultado = pu.obtenerTrimestre(rangoFechas);
//		        System.out.println(resultado);
			
//			System.out.println(name);
//TODO		FUNCIONES SETMODEL, SETHACIENDA, SETYEARANDPERIOD EN CADA CLASE
//TODO      LIMPIAR CODIGO (funciones, variables, orden)
//TODO      CAMBIAR VOID POR STRING O OBJETO 
//TODO 		CAMBIAR EN REGEX LOS ACENTOS POR NOMENCLATURA CORRESPONDIENTE
//TODO 		QUITAR AVISOS SONARLINT REGEX

			// MODELO 115 ALAVA
//			mdA.setName(text);
//			mdA.setNif(text);
//			mdA.setModel(text);
//			mdA.setPeriod(text);
//			mdA.setExercise(text);
//			mdA.setAmount(text);
//			mdA.setHacienda(text);
//			mdA.setLeases(text);
//			mdA.setWithHoldings(text);
//			mdA.setLessors(text);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
