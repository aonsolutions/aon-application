package com.esferalia.aon.in.payroll.pdf.maker.warehouse;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.Country;
import com.github.javafaker.Faker;

public class WarehouseTest {

	@Test
	public void test() throws CanNotCreatePdfException, IOException {
		Company registry = new Company();
		registry.setName("CONSERVAS LA BRÚJULA S.L.");
		
		RegistryAddress address = new RegistryAddress();
		address.setAddress("Rúa Central, 8 P.I. Do Salnés");
		address.setAddress2("Puerta 288");
		address.setAddress3("Hemisferio sur");
		address.setCity("Ribadumia");
		address.setZip("36636");
		address.setCountry(Country.ES);
		address.setProvince("Pontevedra");
		address.setMain(true);
		
		LinkedList<RegistryAddress> addresses = new LinkedList<>();
		addresses.add(address);
		
		CompanyFull company = new CompanyFull();
		company.setRegistry(registry)
		.setAddresses(addresses);
		
		Item item = new Item().
				setDescription("SARDINILLA GUISADA RR125 HACENDADO")
				.setSerialDate(new Date()).setSerialNumber("36B19A17");
		
		byte[] logo = WarehouseTest.class.getResourceAsStream("kintama.png").readAllBytes();
		
		try (WarehouseTemplate wt = new WarehouseTemplate(company, item, logo, "08480000230362", 288d, "(02)08480000230362(37)65(15)231231(10)36B19A", "3842644412021000032")) {
			wt.save(new FileOutputStream("./WarehouseTest.pdf"));
		}
		
		
	}
	
	@Test
	public void fakerTest() throws CanNotCreatePdfException, IOException {
		for (int i = 0; i < 10; i++) {
			fakerTestToBeRepeated();
		}
	}
	
	private void fakerTestToBeRepeated() throws CanNotCreatePdfException, IOException {
		Faker faker = Faker.instance(new Locale("es"));
		
		Company registry = new Company();
		registry.setName(possibleNull(faker.company().name(), 30));
		
		RegistryAddress address = new RegistryAddress();
		address.setAddress(possibleNull(faker.address().fullAddress(), 30));
		address.setAddress2(possibleNull(faker.address().secondaryAddress(), 30));
		address.setAddress3(possibleNull(faker.address().secondaryAddress(), 30));
		address.setCity(possibleNull(faker.address().cityName(), 30));
		address.setZip(possibleNull(faker.address().zipCode(), 30));
		address.setCountry(possibleNull(Country.ES, 30));
		address.setProvince(possibleNull(faker.address().state(), 30));
		address.setMain(true);
		
		LinkedList<RegistryAddress> addresses = new LinkedList<>();
		addresses.add(address);
		
		CompanyFull company = new CompanyFull();
		company.setRegistry(registry)
		.setAddresses(addresses);
		
		Item item = new Item().
				setDescription(possibleNull(faker.commerce().productName(), 30))
				.setSerialDate(possibleNull(faker.date().future(1000, TimeUnit.DAYS), 30)).setSerialNumber(possibleNull(faker.idNumber().valid(), 30));
		
		byte[] logo = possibleNull(WarehouseTest.class.getResourceAsStream("aon-logo.jpg").readAllBytes(), 30);
		
		Double quantity = possibleNull((double) faker.number().numberBetween(0, 200), 30);
		String sscc = possibleNull(faker.idNumber().valid(), 30);
		String ean128 = possibleNull(faker.idNumber().valid(), 30);
		String barcode = possibleNull(faker.idNumber().valid(), 30);
		
		try (WarehouseTemplate wt = new WarehouseTemplate(company, item, logo, barcode, quantity, ean128, sscc)) {
			wt.save(/*new FileOutputStream("./WarehouseRandomTest.pdf")*/OutputStream.nullOutputStream());
		}
	}

	private static <T> T possibleNull(T element, double nullProbability) {
		return (Math.random() * 100 < nullProbability) ? null : element;
	}
}
