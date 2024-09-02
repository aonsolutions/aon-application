package com.esferalia.aon.in.payroll.pdf.maker.payroll;


import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PartTimeParams;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.bean.PartTimeParams.PartTimeEntry;
import com.github.javafaker.Faker;

public class PartTimeTestCase {
	
	@Test
	public void test() throws FileNotFoundException {
		Faker faker = Faker.instance(new Locale("es", "ES"));
		try (OutputStream os = new FileOutputStream("./PartTime.pdf")){
			PartTimeParams ptp = new PartTimeParams(faker.date().past(100, TimeUnit.DAYS));
			ptp
			.setEnterpriseName(faker.company().name())
			.setEnterpriseCCC(randomCCC(faker))
			.setEnterpriseDocument(randomNIF(faker))
			.setEmployeeName(faker.artist().name())
			.setContractHours(faker.number().randomDouble(2, 0, 8))
			.setEnterpriseSignature(PartTimeTestCase.class.getResourceAsStream("snk.png").readAllBytes())
			.setPaymentDate(nullProbability(faker.date().past(5, TimeUnit.DAYS), 20));
			
			for (int i=1; i<=ptp.getEntries().size(); i++) {
				ptp.addEntry(i, new PartTimeEntry()
						.setOrdinary(nullProbability(faker.number().randomDouble(2, 0, 8), 20))
						.setComplementary(nullProbability(faker.number().randomDouble(2, 0, 8), 20))
				);
			}
			
			PartTimeTemplate.print(os, ptp);
		} catch (IOException e1) {
			fail();
		} catch (CanNotCreatePdfException e) {
			fail();
		}
	}
	
	private static <E> E nullProbability (E item, int percentage) {
		int rand = (int)(Math.random() * 100) + 1;
		if (rand < percentage)
			return null;
		else
			return item;
	}
	
	private static String randomCCC(Faker faker) {
		StringBuilder sb = new StringBuilder("");
		for (int i=0; i<15; i++) {
			sb.append(faker.number().randomDigit());
		}
		return sb.toString();
	}
	private static String randomNIF(Faker faker) {
		StringBuilder sb = new StringBuilder("B");
		for (int i=0; i<8; i++) {
			sb.append(faker.number().randomDigit());
		}
		return sb.toString();
	}
}
