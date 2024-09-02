package com.esferalia.aon.in.payroll.pdf.maker.timecontrol;


import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.timecontrol.bean.EmployeeData;
import com.github.javafaker.Faker;

public class TimeControlTestCase {
	
	@Test
	public void test() throws FileNotFoundException {
		int repeats = Faker.instance().number().numberBetween(5, 50);
		for (int i=0; i<repeats; i++) {
			singleTest();
		}
	}

	private void singleTest() throws FileNotFoundException {
		Faker faker = Faker.instance(new Locale("es", "ES"));
		int pages = faker.number().numberBetween(5, 20);
		String enterprise = nullProbability(faker.company().name(), 15);
		String cif = nullProbability(String.valueOf(faker.number().numberBetween(1000000000, 999999999)), 15);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		Date from = cal.getTime();
		cal.set(Calendar.MONTH, Calendar.DECEMBER);
		Date period = faker.date().between(from, cal.getTime());
		LinkedList<EmployeeData> employees = new LinkedList<EmployeeData>();
		for (int i=0; i< pages; i++) {
			EmployeeData emp = new EmployeeData();
			emp.setContract(nullProbability(String.valueOf(faker.number().numberBetween(100, 999)), 15));
			emp.setDni(nullProbability(faker.code().isbn10(false), 15));
			emp.setNaf(nullProbability(faker.code().isbn10(false), 15));
			emp.setEmployeeName(nullProbability(faker.gameOfThrones().character(), 15));
			employees.add(emp);
		}
		try (OutputStream os = OutputStream.nullOutputStream()){
//			OutputStream os = new FileOutputStream("/home/igonzalez/Escritorio/pedefes/faker.pdf");
			TimeControlTemplate.print(os, enterprise, cif, employees, period);
		} catch (CanNotCreatePdfException e) {
			fail();
		} catch (IOException e1) {
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
	
}
