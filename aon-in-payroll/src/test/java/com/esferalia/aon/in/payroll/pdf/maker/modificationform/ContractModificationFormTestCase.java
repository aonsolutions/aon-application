package com.esferalia.aon.in.payroll.pdf.maker.modificationform;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.in.payroll.pdf.maker.form.bean.ModificationForm;
import com.esferalia.aon.in.payroll.pdf.maker.form.bean.ModificationForm.ClientData;
import com.esferalia.aon.in.payroll.pdf.maker.form.bean.ModificationForm.CompanyData;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.github.javafaker.Faker;

public class ContractModificationFormTestCase {
	private static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi eu risus diam. Mauris convallis maximus tempor. Fusce quis elit est. Integer auctor leo a metus condimentum suscipit. Vivamus sed scelerisque lacus, nec faucibus tortor. Suspendisse in luctus est, vel tincidunt libero. In ullamcorper semper odio ac blandit. Quisque eget nisi et mauris bibendum scelerisque a non nulla. Vivamus tempus mi ut malesuada facilisis. Vivamus dapibus, felis ut lobortis dignissim, odio odio dictum quam, tincidunt elementum sapien metus sit amet orci. Aenean at orci in dui sollicitudin condimentum nec quis ligula. Quisque semper facilisis turpis, quis eleifend magna lacinia eu.\n" + 
			"\n" + 
			"Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Mauris ipsum elit, facilisis sed ullamcorper ut, sagittis et erat. Nam consequat elit gravida, tempus est non, feugiat ipsum. Duis vulputate, odio ut congue sagittis, ex ligula vestibulum ante, gravida bibendum ex quam quis tellus. Nam vel lacus ante. Donec suscipit posuere leo, at consectetur velit placerat id. Curabitur ac lectus dictum tellus pellentesque interdum eget eget felis. Aliquam vitae rutrum lectus. Nulla vestibulum posuere nibh, vel convallis turpis pharetra et. Phasellus fermentum dui eros, id gravida lectus vestibulum nec.";

	@Test
	public void test() {
		try (FileOutputStream fos = new FileOutputStream("./ContractModificationFormTest.pdf")) {
			
			RegistryAddress rAddress = new RegistryAddress()
					.setAddress("Nombre de calle inventado")
					.setAddress2("Edificio inventado de la monta\u00f1a de la ciudad del norte de la provincia de al lado")
					.setAddress3("Puerta 288")
					.setAlias("Fake Street")
					.setCity("Quintanilla de Vivar")
					.setCountry(Country.ES)
					.setNumber("1")
					.setProvince("Burgos")
					.setStreetType(StreetType.AVDA)
					.setZip("01000");
			
			CompanyData companyData = new ModificationForm.CompanyData()
					.setLogo(getLogo())
					.setSignature(getSignature())
					.setName("NOMBRE DE LA COMPA\u00d1\u00cdA, S.L.")
					.setDocument("B12345678")
					.setCcc("11122534302")
					.setAddress(rAddress);
			ClientData employeeData = new ModificationForm.ClientData()
					.setName("D\u00edaz de Vivar, Rodrigo")
					.setDocument("12345678M")
					.setSocialSecurityNum("555501234")
					.setSeniorityDate(new Date())
					.setProfessionalGroup("Campeador")
					.setQuoteGroup("01");
			ModificationForm form = new ModificationForm()
					.setCompanyData(companyData)
					.setClientData(employeeData)
//					.setModificationTitle("MODIFICACI\u00d3N DEL CONTRATO DE TRABAJO")
					.setSepeId("12345678910111213")
					.setModificationDate(new Date())
					.setContractStartDate(new Date())
					.setClauses(LOREM_IPSUM);
			try (ContractModificationFormTemplate template = new ContractModificationFormTemplate(form)) {				
				template.print(fos);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void repeatedRandomTest() {
		try {			
			IntStream.range(0, 50).forEach(n -> randomTest());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	public void randomTest() {
		try (OutputStream os = OutputStream.nullOutputStream()) {
			double nullProbability = 20;
			Faker faker = new Faker(new Locale("es"));
			RegistryAddress rAddress = new RegistryAddress()
					.setAddress(possibleNull(faker.address().fullAddress(), nullProbability))
					.setAddress2(possibleNull(faker.address().secondaryAddress(), nullProbability))
					.setAddress3(possibleNull(faker.address().buildingNumber(), nullProbability))
					.setAlias(possibleNull(faker.address().stateAbbr(), nullProbability))
					.setCity(possibleNull(faker.address().cityName(), nullProbability))
					.setCountry(possibleNull(Country.ES, nullProbability))
					.setNumber(possibleNull(faker.address().buildingNumber(), nullProbability))
					.setProvince(possibleNull(faker.address().state(), nullProbability))
					.setStreetType(possibleNull(StreetType.AVDA, nullProbability))
					.setZip(possibleNull(faker.address().zipCode(), nullProbability));
			
			CompanyData companyData = new ModificationForm.CompanyData()
					.setLogo(possibleNull(getLogo(), nullProbability))
					.setSignature(possibleNull(getSignature(), nullProbability))
					.setName(possibleNull(faker.company().name(), nullProbability))
					.setDocument(possibleNull(faker.business().creditCardNumber(), nullProbability))
					.setCcc(possibleNull(faker.business().creditCardNumber(), nullProbability))
					.setAddress(possibleNull(rAddress, nullProbability));
			ClientData employeeData = new ModificationForm.ClientData()
					.setName(possibleNull(faker.artist().name(), nullProbability))
					.setDocument(possibleNull(faker.business().creditCardNumber(), nullProbability))
					.setSocialSecurityNum(possibleNull(faker.business().creditCardNumber(), nullProbability))
					.setSeniorityDate(possibleNull(faker.date().past(1000, TimeUnit.DAYS), nullProbability))
					.setProfessionalGroup(possibleNull(faker.job().position(), nullProbability))
					.setQuoteGroup(null);
			ModificationForm form = new ModificationForm()
					.setCompanyData(companyData)
					.setClientData(employeeData)
//					.setModificationTitle("MODIFICACI\u00d3N DEL CONTRATO DE TRABAJO")
					.setSepeId(possibleNull("12345678910111213", nullProbability))
					.setModificationDate(possibleNull(new Date(), nullProbability))
					.setContractStartDate(possibleNull(new Date(), nullProbability))
					.setClauses(possibleNull(faker.lorem().paragraph(), nullProbability));
			try (ContractModificationFormTemplate template = new ContractModificationFormTemplate(form)) {				
				template.print(os);
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
	private static <T> T possibleNull(T element, double nullProbability) {
		return (Math.random() * 100 < nullProbability) ? null : element;
	}
	
	private byte[] getLogo() {
		try (InputStream logoIS = ContractModificationFormTestCase.class.getResourceAsStream("aon-solutions.png")) {
			byte[] logo = logoIS.readAllBytes();
			return logo;
		} catch (Exception e) {
			return null;
		}
	}
	
	private byte[] getSignature() {
		try (InputStream logoIS = ContractModificationFormTestCase.class.getResourceAsStream("eiichiro_firma.png")) {
			byte[] logo = logoIS.readAllBytes();
			return logo;
		} catch (Exception e) {
			return null;
		}
	}
	
}
