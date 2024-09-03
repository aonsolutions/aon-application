package net.aonsolutions.aon.invoice.communication;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.server.io.AonIOUtils;

import net.aonsolutions.aon.invoice.communication.visitor.AcceptInvoiceCommunicationTypeVisitor;
import net.aonsolutions.aon.invoice.communication.visitor.CancelInvoiceCommunicationTypeVisitor;
import net.aonsolutions.aon.invoice.communication.visitor.ModifyInvoiceCommunicationTypeVisitor;

public class InvoiceCommunicationTest {

	private static final String PASS_1 = "111111";
	private static final String PASS_2 = "IZDesa2021";
	
	private static final String CERT_NAME_1 = "Autonomoa_Autonomo.p12";
	private static final String NAME_1 = "ALATZ APARICIO DIAZ";
	private static final String PNAME_1 = "ALATZ";
	private static final String PSURNAME_1 = "APARICIO";
	private static final String PSURNAME_1_2 = "DIAZ";
	private static final String DOCUMENT_1 = "99999976R";
	
	private static final String CERT_NAME_2 = "EntitateOrdezkaria_RepresentanteDeEntidad.p12";
	private static final String NAME_2 = "ALATZ APARICIO DIAZ";
	private static final String DOCUMENT_2 = "S7836107H";

	private static final String CERT_NAME_3 = "PertsonaFisikoa_PersonaFísica.p12";
	private static final String NAME_3 = "NUEVOCIUD FICTICIO ACTIVO";
	private static final String PNAME_3 = "NUEVOCIUD";
	private static final String PSURNAME_3 = "FICTICIO";
	private static final String PSURNAME_3_2 = "ACTIVO";
	private static final String DOCUMENT_3 = "99999972C";
	
	@Test
	@Disabled
	public void test() throws Exception {
		Domain domain = new Domain();
		User user = new User();
		Invoice invoice = new Invoice();
		getConfigurations().stream().forEach(config -> {
			AcceptInvoiceCommunicationTypeVisitor visitor = (AcceptInvoiceCommunicationTypeVisitor) 
					new AcceptInvoiceCommunicationTypeVisitor(domain, user, invoice)
					.setTbaiConfiguration(config.getTbaiConfiguration())
					.setCompany(config.getCompany())
					.setPerson(config.getPerson());
			try {
				if (config.getTbaiConfiguration().isBizkaia())
					InvoiceCommunicationType.LROE.visit(visitor);
				else InvoiceCommunicationType.TBAI.visit(visitor);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			ModifyInvoiceCommunicationTypeVisitor visitor2 = (ModifyInvoiceCommunicationTypeVisitor) 
				new AcceptInvoiceCommunicationTypeVisitor(domain, user, invoice)
					.setTbaiConfiguration(config.getTbaiConfiguration())
					.setCompany(config.getCompany())
					.setPerson(config.getPerson());
			try {
				if (config.getTbaiConfiguration().isBizkaia())
					InvoiceCommunicationType.LROE.visit(visitor2);
				else InvoiceCommunicationType.TBAI.visit(visitor2);
			} catch (Exception e) {
				e.printStackTrace();
			}
			CancelInvoiceCommunicationTypeVisitor visitor3 = (CancelInvoiceCommunicationTypeVisitor) 
				new CancelInvoiceCommunicationTypeVisitor(domain, user, invoice)
					.setTbaiConfiguration(config.getTbaiConfiguration())
					.setCompany(config.getCompany())
					.setPerson(config.getPerson());
			try {
				if (config.getTbaiConfiguration().isBizkaia())
					InvoiceCommunicationType.LROE.visit(visitor3);
				else InvoiceCommunicationType.TBAI.visit(visitor3);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	private Company getCompany1() {
		Company company = new Company();
		company.setName(NAME_1);
		company.setDocument(DOCUMENT_1);
		return company;
	}
	
	private Person getPerson1() {
		Person person = new Person();
		person.setName(NAME_1);
		person.setFirstName(PNAME_1);
		person.setFirstSurname(PSURNAME_1);
		person.setSecondSurname(PSURNAME_1_2);
		person.setDocument(DOCUMENT_1);
		return person;
	}
	
	private Company getCompany2() {
		Company company = new Company();
		company.setName(NAME_2);
		company.setDocument(DOCUMENT_2);
		return company;
	}
	
	private Company getCompany3() {
		Company company = new Company();
		company.setName(NAME_3);
		company.setDocument(DOCUMENT_3);
		return company;
	}
	
	private Person getPerson3() {
		Person person = new Person();
		person.setName(NAME_3);
		person.setFirstName(PNAME_3);
		person.setFirstSurname(PSURNAME_3);
		person.setSecondSurname(PSURNAME_3_2);
		person.setDocument(DOCUMENT_3);
		return person;
	}
	
	private List<InvoiceCommunicationConfiguration> getConfigurations() throws IOException {
		List<InvoiceCommunicationConfiguration> list = new  LinkedList<>();
		TbaiConfiguration tbaiConfiguration =  new TbaiConfiguration()
			.setActive(true)
			.setTest(true);

		// CERT 1
		tbaiConfiguration.setCertificate(getCertificate1());			
		InvoiceCommunicationConfiguration ic = new InvoiceCommunicationConfiguration()
				.setCompany(getCompany1())
				.setPerson(getPerson1());
				
		list.add(ic.setTbaiConfiguration(tbaiConfiguration.setAdministration(Administration.ALAVA)));			
		list.add(ic.setTbaiConfiguration(tbaiConfiguration.setAdministration(Administration.GIPUZKOA)));
		list.add(ic.setTbaiConfiguration(tbaiConfiguration.setAdministration(Administration.BIZKAIA)));
		
		// CERT 2
		tbaiConfiguration.setCertificate(getCertificate2());			
		InvoiceCommunicationConfiguration ic2 = new InvoiceCommunicationConfiguration()
				.setCompany(getCompany2());
				
		list.add(ic2.setTbaiConfiguration(tbaiConfiguration.setAdministration(Administration.ALAVA)));			
		list.add(ic2.setTbaiConfiguration(tbaiConfiguration.setAdministration(Administration.GIPUZKOA)));
		list.add(ic2.setTbaiConfiguration(tbaiConfiguration.setAdministration(Administration.BIZKAIA)));
		
		// CERT 3
		tbaiConfiguration.setCertificate(getCertificate3());			
		InvoiceCommunicationConfiguration ic3 = new InvoiceCommunicationConfiguration()
				.setCompany(getCompany3())
				.setPerson(getPerson3());
				
		list.add(ic3.setTbaiConfiguration(tbaiConfiguration.setAdministration(Administration.ALAVA)));			
		list.add(ic3.setTbaiConfiguration(tbaiConfiguration.setAdministration(Administration.GIPUZKOA)));
		list.add(ic3.setTbaiConfiguration(tbaiConfiguration.setAdministration(Administration.BIZKAIA)));

		return list;
	}
	
	private Certificate getCertificate1() throws IOException {
		InputStream is = InvoiceCommunicationTest.class.getResourceAsStream(CERT_NAME_1);
		return new Certificate()
				.setData(AonIOUtils.toByteArray(is))
				.setConfidential(new Random().nextBoolean())
				.setDescription(CERT_NAME_1)
				.setPassword(PASS_1)
				.setType(CertificateType.AEAT.name());
	}
	
	private Certificate getCertificate2() throws IOException {
		InputStream is = InvoiceCommunicationTest.class.getResourceAsStream(CERT_NAME_2);
		return new Certificate()
				.setData(AonIOUtils.toByteArray(is))
				.setConfidential(new Random().nextBoolean())
				.setDescription(CERT_NAME_2)
				.setPassword(PASS_2)
				.setType(CertificateType.AEAT.name());
	}
	
	private Certificate getCertificate3() throws IOException {
		InputStream is = InvoiceCommunicationTest.class.getResourceAsStream(CERT_NAME_3);
		return new Certificate()
				.setData(AonIOUtils.toByteArray(is))
				.setConfidential(new Random().nextBoolean())
				.setDescription(CERT_NAME_3)
				.setPassword(PASS_2)
				.setType(CertificateType.AEAT.name());
	}
	
	public class InvoiceCommunicationConfiguration {
		Company company;
		Person person;
		TbaiConfiguration tbaiConfiguration;
		
		public Company getCompany() {
			return company;
		}
		
		public 	InvoiceCommunicationConfiguration setCompany(Company company) {
			this.company = company;
			return this;
		}
		
		public TbaiConfiguration getTbaiConfiguration() {
			return tbaiConfiguration;
		}
		
		public InvoiceCommunicationConfiguration setTbaiConfiguration(TbaiConfiguration tbaiConfiguration) {
			this.tbaiConfiguration = tbaiConfiguration;
			return this;
		}
		
		public Person getPerson() {
			return person;
		}
		
		public InvoiceCommunicationConfiguration setPerson(Person person) {
			this.person = person;
			return this;
		}
	}
}
