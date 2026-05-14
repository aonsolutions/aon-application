package net.aonsolutions.aon.invoice.communication.visitor;

public class InvoiceCommunicationTest {

//	private static final String PASS_1 = "111111";
//	private static final String PASS_2 = "IZDesa2021";
//	
//	private static final String CERT_NAME_1 = "Autonomoa_Autonomo.p12";
//	private static final String NAME_1 = "ALATZ APARICIO DIAZ";
//	private static final String PNAME_1 = "ALATZ";
//	private static final String PSURNAME_1 = "APARICIO";
//	private static final String PSURNAME_1_2 = "DIAZ";
//	private static final String DOCUMENT_1 = "99999976R";
//	
//	private static final String CERT_NAME_2 = "EntitateOrdezkaria_RepresentanteDeEntidad.p12";
//	private static final String NAME_2 = "ALATZ APARICIO DIAZ";
//	private static final String DOCUMENT_2 = "S7836107H";
//
//	private static final String CERT_NAME_3 = "PertsonaFisikoa_PersonaFísica.p12";
//	private static final String NAME_3 = "NUEVOCIUD FICTICIO ACTIVO";
//	private static final String PNAME_3 = "NUEVOCIUD";
//	private static final String PSURNAME_3 = "FICTICIO";
//	private static final String PSURNAME_3_2 = "ACTIVO";
//	private static final String DOCUMENT_3 = "99999972C";
//	
//	@Test
//	@Disabled
//	public void test() throws Exception {
//		Domain domain = new Domain();
//		User user = new User();
//		Invoice invoice = new Invoice();
//		getConfigurations().stream().forEach(config -> {
//			AcceptInvoiceCommunicationTypeVisitor visitor = (AcceptInvoiceCommunicationTypeVisitor) 
//					new AcceptInvoiceCommunicationTypeVisitor(domain, user, invoice)
//					.setConfiguration(config.getConfiguration())
//					.setCompany(config.getCompany())
//					.setPerson(config.getPerson());
//			try {
//				if (config.getConfiguration().isBizkaia())
//					InvoiceCommunicationType.LROE.visit(visitor);
//				else InvoiceCommunicationType.TBAI.visit(visitor);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			
//			ModifyInvoiceCommunicationTypeVisitor visitor2 = (ModifyInvoiceCommunicationTypeVisitor) 
//				new ModifyInvoiceCommunicationTypeVisitor(domain, user, invoice)
//					.setConfiguration(config.getConfiguration())
//					.setCompany(config.getCompany())
//					.setPerson(config.getPerson());
//			try {
//				if (config.getConfiguration().isBizkaia())
//					InvoiceCommunicationType.LROE.visit(visitor2);
//				else InvoiceCommunicationType.TBAI.visit(visitor2);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			CancelInvoiceCommunicationTypeVisitor visitor3 = (CancelInvoiceCommunicationTypeVisitor) 
//				new CancelInvoiceCommunicationTypeVisitor(domain, user, invoice)
//					.setConfiguration(config.getConfiguration())
//					.setCompany(config.getCompany())
//					.setPerson(config.getPerson());
//			try {
//				if (config.getConfiguration().isBizkaia())
//					InvoiceCommunicationType.LROE.visit(visitor3);
//				else InvoiceCommunicationType.TBAI.visit(visitor3);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		});
//	}
//	
//	private Company getCompany1() {
//		Company company = new Company();
//		company.setName(NAME_1);
//		company.setDocument(DOCUMENT_1);
//		return company;
//	}
//	
//	private Person getPerson1() {
//		Person person = new Person();
//		person.setName(NAME_1);
//		person.setFirstName(PNAME_1);
//		person.setFirstSurname(PSURNAME_1);
//		person.setSecondSurname(PSURNAME_1_2);
//		person.setDocument(DOCUMENT_1);
//		return person;
//	}
//	
//	private Company getCompany2() {
//		Company company = new Company();
//		company.setName(NAME_2);
//		company.setDocument(DOCUMENT_2);
//		return company;
//	}
//	
//	private Company getCompany3() {
//		Company company = new Company();
//		company.setName(NAME_3);
//		company.setDocument(DOCUMENT_3);
//		return company;
//	}
//	
//	private Person getPerson3() {
//		Person person = new Person();
//		person.setName(NAME_3);
//		person.setFirstName(PNAME_3);
//		person.setFirstSurname(PSURNAME_3);
//		person.setSecondSurname(PSURNAME_3_2);
//		person.setDocument(DOCUMENT_3);
//		return person;
//	}
//	
//	private List<InvoiceCommunication> getConfigurations() throws IOException {
//		List<InvoiceCommunication> list = new  LinkedList<>();
//		InvoiceCommunicationConfiguration icc =  new InvoiceCommunicationConfiguration()
////			.setTbai(true)
////			.setTbaiTest(true)
//			;
//
//		// CERT 1
//		icc.setCertificate(getCertificate1());			
//		InvoiceCommunication ic = new InvoiceCommunication()
//				.setCompany(getCompany1())
//				.setPerson(getPerson1());
//				
//		list.add(ic.setConfiguration(icc.setAdministration(Administration.ALAVA)));			
//		list.add(ic.setConfiguration(icc.setAdministration(Administration.GIPUZKOA)));
//		list.add(ic.setConfiguration(icc.setAdministration(Administration.BIZKAIA)));
//		
//		// CERT 2
//		InvoiceCommunicationConfiguration icc2 =  new InvoiceCommunicationConfiguration()
////			.setTbai(true)
////			.setTbaiTest(true)
//			;
//		icc2.setCertificate(getCertificate2());			
//		InvoiceCommunication ic2 = new InvoiceCommunication()
//				.setCompany(getCompany2());
//				
//		list.add(ic2.setConfiguration(icc2.setAdministration(Administration.ALAVA)));			
//		list.add(ic2.setConfiguration(icc2.setAdministration(Administration.GIPUZKOA)));
//		list.add(ic2.setConfiguration(icc2.setAdministration(Administration.BIZKAIA)));
//		
//		// CERT 3
//		InvoiceCommunicationConfiguration icc3 =  new InvoiceCommunicationConfiguration()
////			.setTbai(true)
////			.setTbaiTest(true)
//			;
//		icc3.setCertificate(getCertificate3());			
//					
//		InvoiceCommunication ic3 = new InvoiceCommunication()
//				.setCompany(getCompany3())
//				.setPerson(getPerson3());
//				
//		list.add(ic3.setConfiguration(icc3.setAdministration(Administration.ALAVA)));			
//		list.add(ic3.setConfiguration(icc3.setAdministration(Administration.GIPUZKOA)));
//		list.add(ic3.setConfiguration(icc3.setAdministration(Administration.BIZKAIA)));
//
//		return list;
//	}
//	
//	private Certificate getCertificate1() throws IOException {
//		InputStream is = InvoiceCommunicationTest.class.getResourceAsStream(CERT_NAME_1);
//		return new Certificate()
//				.setData(AonIOUtils.toByteArray(is))
//				.setConfidential(new Random().nextBoolean())
//				.setDescription(CERT_NAME_1)
//				.setPassword(PASS_1)
//				.setType(CertificateType.AEAT.name());
//	}
//	
//	private Certificate getCertificate2() throws IOException {
//		InputStream is = InvoiceCommunicationTest.class.getResourceAsStream(CERT_NAME_2);
//		return new Certificate()
//				.setData(AonIOUtils.toByteArray(is))
//				.setConfidential(new Random().nextBoolean())
//				.setDescription(CERT_NAME_2)
//				.setPassword(PASS_2)
//				.setType(CertificateType.AEAT.name());
//	}
//	
//	private Certificate getCertificate3() throws IOException {
//		InputStream is = InvoiceCommunicationTest.class.getResourceAsStream(CERT_NAME_3);
//		return new Certificate()
//				.setData(AonIOUtils.toByteArray(is))
//				.setConfidential(new Random().nextBoolean())
//				.setDescription(CERT_NAME_3)
//				.setPassword(PASS_2)
//				.setType(CertificateType.AEAT.name());
//	}
//	
//	private class InvoiceCommunication {
//		private Company company;
//		private Person person;
//		private InvoiceCommunicationConfiguration icc;
//		
//		public Company getCompany() {
//			return company;
//		}
//		
//		public 	InvoiceCommunication setCompany(Company company) {
//			this.company = company;
//			return this;
//		}
//		
//		public InvoiceCommunicationConfiguration getConfiguration() {
//			return icc;
//		}
//		public InvoiceCommunication setConfiguration(InvoiceCommunicationConfiguration icc) {
//			this.icc = icc;
//			return this;
//		}
//		
//		public Person getPerson() {
//			return person;
//		}
//		public InvoiceCommunication  setPerson(Person person) {
//			this.person = person;
//			return this;
//		}
//	}
}
