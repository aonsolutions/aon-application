package com.esferalia.aon.in.payroll.pdf.maker.warehouse;


import java.io.FileOutputStream;
import java.io.IOException;
import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;
import com.esferalia.aon.occam.test.faker.AonFaker;


public class DeliveryTemplateTest {

	/**
	 * Tests DeliveryTemplate creating a pdf called DeliveryTest.pdf in /aon.in.payroll/DeliveryTest.pdf
	 * @throws CanNotCreatePdfException
	 * @throws IOException
	 */
	@Test
	public void test() throws CanNotCreatePdfException, IOException {
		
		Delivery delivery = AonFaker.getDelivery();
		
		Company company = AonFaker.getCompany();
		CompanyFull companyFull = AonFaker.getCompanyFull(company);
		
		Customer customer = AonFaker.getCustomer();
		CustomerFull customerFull = AonFaker.getCustomerFull(customer);
		
		byte[] logo = WarehouseTest.class.getResourceAsStream("kintama.png").readAllBytes();
		
		Warehouse warehouse = AonFaker.getWarehouse();
		Workplace workplace = AonFaker.getWorkplace();
		
		try (DeliveryTemplate deliveryTemplate = new DeliveryTemplate(delivery, warehouse, workplace, companyFull, customerFull, logo)) {
			deliveryTemplate.save(new FileOutputStream("./DeliveryTest.pdf"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
