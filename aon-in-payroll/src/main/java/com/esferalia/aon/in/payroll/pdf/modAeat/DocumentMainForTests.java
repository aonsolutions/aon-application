package com.esferalia.aon.in.payroll.pdf.modAeat;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class DocumentMainForTests {

	public static void main(String[] args) {
		try {

			ParserUtils pu = new ParserUtils();

			Mod1152023AEAT aeat = new Mod1152023AEAT();

			InputStream is = new FileInputStream("/home/jmortega/Documentos/Modelos/AEAT/MOD 115 3T 2023 LNG.pdf");

			
			byte[] bytes = IOUtils.toByteArray(is);

			PDFExtracter pdf = new PDFExtracter();
			String text = pdf.extract(bytes);
			System.out.println(text);
			
			String nif = aeat.setNif(text);
			System.out.println(nif);
			String name = aeat.setSocialReason(text);
			System.out.println(name);
			String amount = aeat.setAmount(text);
			System.out.println(amount);
			String hacienda = aeat.setHacienda(text);
			System.out.println(hacienda);
			String period = aeat.setPeriod(text);
			System.out.println(period);
			String exercise = aeat.setExercise(text);
			System.out.println(exercise);
			String model = aeat.setModel(text);
			System.out.println(model);
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
