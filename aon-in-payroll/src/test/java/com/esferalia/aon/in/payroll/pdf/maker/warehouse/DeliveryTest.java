package com.esferalia.aon.in.payroll.pdf.maker.warehouse;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;

import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryFull;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.github.javafaker.Faker;

public class DeliveryTest {

	@Test
	public void test() throws CanNotCreatePdfException, IOException {
		
		Delivery delivery = AonFaker.getDelivery();
		
		Company company = new Company();
		company.setName("EMPRESA S.A.");
		
		RegistryAddress address = new RegistryAddress();
		address.setAddress("Rúa Central, 8 P.I. Do Salnés");
		address.setAddress2("Puerta 288");
		address.setAddress3("Hemisferio sur");
		address.setCity("Ribadumia");
		address.setZip("36636");
		address.setCountry(Country.ES);
		address.setProvince("Pontevedra");
		address.setMain(true);
		
		RegistryMedia phone = new RegistryMedia();
		phone.setMedia(MediaType.FIXED_PHONE);
		phone.setValue("956357635");
		
		RegistryMedia email = new RegistryMedia();
		email.setMedia(MediaType.EMAIL);
		email.setValue("email@email.com");
		
		RegistryMedia fax = new RegistryMedia();
		fax.setMedia(MediaType.FAX);
		fax.setValue("956793575");
		
		RegistryMedia web = new RegistryMedia();
		web.setMedia(MediaType.WEB);
		web.setValue("www.web.com");
		
		LinkedList<RegistryAddress> addresses = new LinkedList<>();
		addresses.add(address);
		
		LinkedList<RegistryMedia> medias = new LinkedList<>();
		medias.add(phone);
		medias.add(email);
		medias.add(fax);
		medias.add(web);
		
		CompanyFull companyFull = new CompanyFull();
		companyFull.setRegistry(company)
		.setAddresses(addresses)
		.setMedias(medias);
		
		byte[] logo = WarehouseTest.class.getResourceAsStream("kintama.png").readAllBytes();
		
		
		try (DeliveryTemplate deliveryTemplate = new DeliveryTemplate(delivery, companyFull, logo)) {
			deliveryTemplate.save(new FileOutputStream("./DeliveryTest.pdf"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
