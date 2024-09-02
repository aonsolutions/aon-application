package com.esferalia.aon.in.payroll.pdf.maker.offer;

import static com.esferalia.aon.in.payroll.pdf.maker.Logger.jump;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.log;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.Separator.ARROW;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.Status.COMPARE;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.Status.GENERATE;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.Status.SUCCESS;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.in.payroll.pdf.maker.Logger;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceTheme;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceThemeConfiguration;
import com.esferalia.aon.occam.api.model.management.Offer;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.StreetType;


public class OfferTest {

	@Test
	public void DataIntegrationTest() {
		
		log(GENERATE, "Creating invoice data");
		
		String address = "Calle de los tejeros alados en la roca veraz de los paramos";
		String addressNumber  = "12";
		String addressProvince = "Almería";
		String addressTown = "Andrajosía de vera";
		String addressZIP = "01004";
		
		String registryName = "AON SOLUTIONS MASTER XL PLUS MASTER PRO S GAMING MAX";
		String referenceCode = "8901389091AAA";
		double total = 19216376.75;
		String registryDocument = "1826738401Y";
		Date issueDate = new Date();
		
		log(GENERATE, "Address", address);
		log(GENERATE, "Address number", addressNumber);
		log(GENERATE, "Address province", addressProvince);
		log(GENERATE, "Address town", addressTown);
		log(GENERATE, "Address ZIP", addressZIP);
		
		log(GENERATE, "Registry name", registryName);
		log(GENERATE, "Reference code", referenceCode);
		log(GENERATE, "Invoice total", total + "");
		log(GENERATE, "Registry document", registryDocument);
		log(GENERATE, "Date", issueDate + "");
		
		
		/** INVOICE BASIC DATA */
		Offer offer = new Offer();
		offer.setSeries("2022");
		offer.setNumber(1);
		
		offer.setAddress(new RegistryAddress()
				.setAddress(address)
				.setNumber(addressNumber)
				.setProvince(addressProvince)
				.setCity(addressTown)
				.setZip(addressZIP)
		);
		
		RegistryAddress raddress = new RegistryAddress();
		raddress.setStreetType(StreetType.AV);
		raddress.setAddress(address);
		raddress.setCountry(Country.KP);
		raddress.setProvince("PYONGAN");
		raddress.setZip("07002");
		raddress.setCity("PYONGYANG");
//		raddress.setGeozone(52216);
		
		offer.setAddress(raddress);
		
		Target target = new Target();
		
		target.setName(registryName);
		offer.setIssueDate(issueDate);
		target.setDocument(registryDocument);
		target.setDocumentCountry(Country.VE);
		target.setTransaction(InvoiceTransactionType.EXTRACOMMUNITY);
		
		offer.setComments("Akatsuki (Akatsuki; literalmente Amanecer) fue en sus comienzos, una organización que buscaba acabar con la tiranía y la opresión a través de medios pacíficos, pero que pronto cambiaría sus métodos hasta volverse una organización criminal constituida por varios ninjas renegados de Clase S que se convirtieron en los principales antagonistas de la serie Naruto: Shippuuden. ");		
		offer.setRemarks("ESTA FACTURA RECTIFICA ALGO");
		/** BREAKDOWNS */
		jump();
		log(GENERATE, "Creating breakdowns.");

		
		/** FINANCES */
		LinkedList<Finance> finances = new LinkedList<>();
		log(GENERATE, "Creating finances.");
		
		BankAccount accountOne = new BankAccount("ES9121000418450200051332");
		BankAccount accountTwo = new BankAccount("ES9121421818450296751237");
		BankAccount accountThree = new BankAccount("ES9999921999950296759997");
		
		Finance financeOne = new Finance();
		financeOne.setBic("BBVAESMMXXX");
		financeOne.setAdvance(true);
		financeOne.setAmount(1039687.23);
		financeOne.setBankAccount(accountOne);
		financeOne.setPayMethodType(PayMethodType.OTHER);
		financeOne.setPayMethodName("TRANSFERENCIA A 6000 DÍAS");
		financeOne.setDueDate(new Date());
		
		Finance financeTwo = new Finance();
		financeTwo.setAdvance(true);
		financeTwo.setAmount(1287.23);
		financeTwo.setBankAccount(accountTwo);
		financeTwo.setPayMethodType(PayMethodType.CASH_BASIS);
		financeTwo.setDueDate(new Date());
		
		Finance financeThree = new Finance();
		financeThree.setAdvance(true);
		financeThree.setAmount(3287.23);
		financeThree.setBankAccount(accountThree);
		financeThree.setPayMethodType(PayMethodType.CHEQUE);
		financeThree.setDueDate(new Date());
		
		Finance financeFour = new Finance();
		financeFour.setAdvance(true);
		financeFour.setAmount(87.23);
		financeFour.setBankAccount(accountThree);
		financeFour.setPayMethodType(PayMethodType.NEGOTIABLE_DOCUMENT);
		financeFour.setDueDate(new Date());
		
//		finances.add(financeOne);
//		finances.add(financeOne);
//		finances.add(financeOne);
//		finances.add(financeOne);
		finances.add(financeOne);
		finances.add(financeOne);
		finances.add(financeOne);
		finances.add(financeOne);
		finances.add(financeTwo);
		finances.add(financeThree);
		finances.add(financeFour);
		
		/** TAXES */
		LinkedList<OfferDetail> details = new LinkedList<>();
		log(GENERATE, "Creating details.");
		jump();
		
		OfferDetail detailOne = new OfferDetail();
		detailOne.setDescription
		(
		    "Three Rings for the Elven-kings under the sky"
		  + " Seven for the Dwarf-lords in their halls of stone,"
		  + " Nine for Mortal Men doomed to die,"
		  + " One for the Dark Lord on his dark throne"
		  + " In the Land of Mordor where the Shadows lie."
		  + " One Ring to rule them all, One Ring to find them,"
		  + " One Ring to bring them all and in the darkness bind them"
		  + " In the Land of Mordor where the Shadows lie. Obcecación, camión, esdrújula..."
		);
		detailOne.setPrice(10000.12);
		detailOne.setDiscountExpression("97.19");
		detailOne.setQuantity(20d);
		detailOne.setItem(new Item().setProduct(new Product().setType(ProductType.LABOUR)));
		
		
		details.add(detailOne);	
		details.add(detailOne);	
		details.add(detailOne);	
		details.add(detailOne);	
		details.add(detailOne);	
		details.add(detailOne);	
		details.add(detailOne);	
		details.add(detailOne);	
		details.add(detailOne);	
		details.add(detailOne);	
		details.add(detailOne);	
		details.add(detailOne);	
		details.add(detailOne);	
		details.add(detailOne);	
		details.add(detailOne);	
		details.add(detailOne);	
		
		offer.setDetails(details);
		
		/** PRINT CONFIGURATIONS */
		ByteArrayOutputStream os;
		try {
			
			OutputStream dos = new FileOutputStream("./OfferIntegrationTest.pdf");
			os = new ByteArrayOutputStream();
			byte[] back = new OfferTest().getClass().getResourceAsStream("bg.jpg").readAllBytes();
			
			PrintInvoiceConfiguration config = new PrintInvoiceConfiguration();
			Attach attach = new Attach();
			attach.setData(back);
			
//			config.setLanguage(AonLanguage.ENGLISH);
			config.setAdjustImage(false);
			config.setBackground(attach);
			config.setDetailed(true);
			config.setAdjustImage(true);
			config.setHeader(50);
			config.setFooter(0);
			config.setCompany(true);
			config.setBorder(2);
			config.setContactData(true);
			config.setRecordData(true);
			config.setLegal("Rodrigo Díaz nació, según afirma una tradición constante, aunque sin corroboración documental, en Vivar, hoy Vivar del Cid, un lugar perteneciente al ayuntamiento de Quintanilla de Vivar y situado en el valle del río Ubierna, a diez kilómetros al norte de Burgos.\n"
					+ "\n"
					+ "En 1068 Sancho II y Alfonso VI se enfrentaron en la batalla de Llantada, a orillas del Pisuerga, vencida por el primero, pero que no resultó decisiva. En 1071, Alfonso logró controlar Galicia, que quedó nominalmente repartida entre él y Sancho, pero esto no logró acabar con los enfrentamientos y en 1072 se libró la batalla de Golpejera o Vulpejera, cerca de Carrión, en la que Sancho venció y capturó a Alfonso y se adueñó de su reino.\n"
					+ "\n"
					+ "El joven Rodrigo (que a la sazón andaría por los veintitrés años) se destacó en estas luchas y, según una vieja tradición, documentada ya a fines del siglo XII, fue el alférez o abanderado de don Sancho en dichas lides, aunque en los documentos de la época nunca consta con ese cargo. En cambio, es bastante probable que ganase entonces el sobrenombre de Campeador, es decir, «el Batallador», que le acompañaría toda su vida, hasta el punto de ser habitualmente conocido, tanto entre cristianos como entre musulmanes, por Rodrigo el Campeador.\n"
					+ "\n"
					+ "Después de la derrota de don Alfonso (que logró exiliarse en Toledo), Sancho II había reunificado los territorios regidos por su padre. Sin embargo, no disfrutaría mucho tiempo de la nueva situación. A finales del mismo año de 1072, un grupo de nobles leoneses descontentos, agrupados entorno a la infanta doña Urraca, hermana del rey, se alzaron contra él en Zamora. Don Sancho acudió a sitiarla con su ejército, cerco en el que Rodrigo realizó también notables acciones, pero que al rey le costó la vida, al ser abatido en un audaz golpe de mano por el caballero zamorano Bellido Dolfos.\n"
					+ "\n"
					+ "La imprevista muerte de Sancho II hizo pasar el trono a su hermano Alfonso, que regresó rápidamente de Toledo para ocuparlo. Las leyendas del siglo XIII han transmitido la célebre imagen de un severo Rodrigo que, tomando la voz de los desconfiados vasallos de don Sancho, obliga a jurar a don Alfonso en la iglesia de Santa Gadea (o Águeda) de Burgos que nada tuvo que ver en la muerte de su hermano, osadía que le habría ganado la duradera enemistad del nuevo monarca.\n"
					+ "\n"
					+ "Por el contrario, nadie le exigió semejante juramento y además el Campeador, que figuró regularmente en la corte, gozaba de la confianza de Alfonso VI, quien lo nombró juez en sendos pleitos asturianos en 1075. Es más, por esas mismas fechas (en 1074, seguramente), el rey lo casó con una pariente suya, su prima tercera doña Jimena Díaz, una noble dama leonesa que, según las investigaciones más recientes, era además sobrina segunda del propio Rodrigo por parte de padre. Un matrimonio de semejante alcurnia era una de las aspiraciones de todo noble que no fuese de primera fila, lo cual revela que el Campeador estaba cada vez mejor situado en la corte.\n"
					+ "\n"
					+ "Así lo muestra también que don Alfonso lo pusiese al frente de la embajada enviada a Sevilla en 1079 para recaudar las parias que le adeudaba el rey Almutamid, mientras que García Ordóñez (uno de los garantes de las capitulaciones matrimoniales de Rodrigo y Jimena) acudía a Granada con una misión similar. Mientras Rodrigo desempeñaba su delegación, el rey Abdalá de Granada, secundado por los embajadores castellanos, atacó al rey de Sevilla. Como éste se hallaba bajo la protección de Alfonso VI, precisamente por el pago de las parias que había ido a recaudar el Campeador, éste tuvo que salir en defensa de Almutamid y derrotó a los invasores junto a la localidad de Cabra (en la actual provincia de Córdoba), capturando a García Ordóñez y a otros magnates castellanos.\n"
					+ "\n"
					+ "La versión tradicional es que en los altos círculos cortesanos sentó muy mal que Rodrigo venciera a uno de los suyos, por lo que empezaron a murmurar de él ante el rey. Sin embargo, no hay seguridad de que esto provocase hostilidad contra el Campeador, entre otras cosas porque a Alfonso VI le interesaba, por razones políticas, apoyar al rey de Sevilla frente al de Badajoz, de modo que la participación de sus nobles en el ataque granadino no debió de gustarle gran cosa.\n"
					+ "\n"
					+ "De todos modos, fueron similares causas políticas las que hicieron caer en desgracia a Rodrigo. En esos delicados momentos, Alfonso VI mantenía en el trono de Toledo al rey títere Alqadir, pese a la oposición de buena parte de sus súbditos. En 1080, mientras el monarca castellano dirigía una campaña destinada a restaurar el gobierno de su protegido, una incontrolada partida andalusí procedente del norte toledano se adentró por tierras sorianas. Rodrigo hizo frente a los saqueadores y los persiguió con su mesnada hasta más allá de la frontera, lo que, en principio, era sólo una operación rutinaria.\n"
					+ "\n"
					+ "Sin embargo, en tales circunstancias, el ataque castellano iba a servir de excusa para la facción contraria a Alqadir y a Alfonso VI. Además, los restantes reyes de taifas se preguntarían de qué servía pagar las parias, si eso no les garantizaba la protección. Al margen, pues, de que interviniesen en el asunto García Ordóñez (que era conde de Nájera) u otros cortesanos opuestos a Rodrigo, el rey debía tomar una decisión ejemplar al respecto, conforme a los usos de la época. Así que desterró al Campeador.");
//			config.setLegal(null);
			PrintInvoiceThemeConfiguration themeconf = new PrintInvoiceThemeConfiguration();
			themeconf.setTheme(PrintInvoiceTheme.PERSONALIZED);

			themeconf.setBoxTitleBackgroundColor("#ffd700");
//			themeconf.setCustomerBackgroundColor("#caa9e6");
//			themeconf.setBoxTitleTextColor("#f025c8");
			themeconf.setBoxBodyBackgroundColor("#d0d793");
			themeconf.setTextColor("#ff6400");
			themeconf.setBoxTitleTextColor("#00ff00");
			themeconf.setBorderColor("#0893ff");
			
			config.setTheme(themeconf);
			
			CompanyFull company = new CompanyFull();
			LinkedList<RegistryMedia> rmediaList = new LinkedList<RegistryMedia>();
			rmediaList.add(new RegistryMedia()
					.setMedia(MediaType.FIXED_PHONE)
					.setValue("699969633"));
			
			rmediaList.add(new RegistryMedia()
					.setMedia(MediaType.FIXED_PHONE)
					.setValue("888888888"));
			
			rmediaList.add(new RegistryMedia()
					.setMedia(MediaType.FIXED_PHONE)
					.setValue("777777777"));
			
			rmediaList.add(new RegistryMedia()
					.setMedia(MediaType.FIXED_PHONE)
					.setValue("666666666"));
			
			rmediaList.add(new RegistryMedia()
					.setMedia(MediaType.FIXED_PHONE)
					.setValue("555555555"));
			
			rmediaList.add(new RegistryMedia()
					.setMedia(MediaType.EMAIL)
					.setValue("karyuu_no_tekken@fairytail.jp"));
			rmediaList.add(new RegistryMedia()
					.setMedia(MediaType.WEB)
					.setValue("https://www.mazda.com"));

			rmediaList.add(new RegistryMedia()
					.setMedia(MediaType.WEB)
					.setValue("https://www.google.es"));
			
//			rmediaList.add(new RegistryMedia()
//					.setMedia(MediaType.WEB)
//					.setValue("https://www.youtube.com"));
			
			Company registry = new Company();
			registry.setName("COMPAÑÍA FALSA PERO MUY FALSA EH XD S.L.").setDocument("L012345678").setDocumentCountry(Country.JP);
			registry.setDomain(new Domain().setDomainType(DomainType.ENTERPRISE).setName("UDAPA.com"));
			
			LinkedList<RegistryAddress> addressList = new LinkedList<>();
			addressList.add(new RegistryAddress().setAddress("Rey Don Sancho, Rey Don Sancho, no digas que no te aviso, pero, de dentro de Zamora un alevoso ha salido"));
			addressList.add(new RegistryAddress()
					.setStreetType(StreetType.AV).setAddress("ISAAC NEWTON")
					.setNumber("287")
					.setAddress2("EDIFICIO AYUDA-T, P.I. LAS SALINAS DE PONIENTE")
					.setProvince("PYONGAN")
					.setCity("PYONGYANG")
					.setCountry(Country.KP)
					.setZip("28001")
					.setMain(true));
			
			company.setAddresses(addressList);
			company.setMedias(rmediaList);
			company.setRegistry(registry);
			
			ArrayList<RecordData> rdl = new ArrayList<RecordData>(1);
			rdl.add(new RecordData()
					.setRegistration("Inscrito en el registro mercantil de Algún Lugar")
					.setVolume("1234")
					.setPage("12345")
					.setSheet("9012")
					.setRecordDate(new Date()));
			company.setRecordDatas(rdl);
			
			
			
			InputStream logoStream = OfferTest.class.getResourceAsStream("matsuda.png");
			byte[] logo = logoStream.readAllBytes();
			
			
			
//			company = null;
//			logo = null;
			
			
			OfferTemplate invoiceTemplate = new OfferTemplate(company, offer, config, "www.aonsolutions.es", logo, "TBAI-00000006Y-251019-btFpwP8dcLGAF-237");
			invoiceTemplate.print(os);
			invoiceTemplate = new OfferTemplate(company, offer, config, "www.aonsolutions.es", logo, "TBAI-00000006Y-251019-btFpwP8dcLGAF-237");
			invoiceTemplate.print(dos);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("File not found Exception");
		} catch (IOException e) {
			e.printStackTrace();
			fail("IOException");
		} catch (CanNotCreatePdfException e) {
			e.printStackTrace();
			fail("Can not create exception");
		}
	}
	
	/**
	 * Remove special characters        	  				 <br>
	 * ---------------------------------------------------   <br>
	 * [ \u20AC ] Euro                 	  				 	 <br>
	 * [ % ] Percentaje Symbol         	  				 	 <br>
	 * ---------------------------------------------------   <br>
	 * 
	 * @param text - original text
	 * @return text without special charaters
	 */
	public String removeSpecialCharacters(String text) {		
		return text.replaceAll("%", "").replaceAll("\u20AC", "");
	}
	
	/**
	 * Assert data with pdf data
	 * @param data - The original data
	 * @param pdf - The pdf data
	 */
	public void assertPdfData(String name, String data, String pdf) {
		log(COMPARE, ARROW, "Original " + name, data);
		log(COMPARE, ARROW, "Pdf " + name, pdf);
		
		// assertEquals(data, pdf);
		log(SUCCESS, "DONE.");
		jump();
	}
	
}
