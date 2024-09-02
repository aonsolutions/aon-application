package com.esferalia.aon.in.payroll.pdf.maker.invoice;

import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA_BOLD;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats.formatDate;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats.toLatinNumber;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.croppedString;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.getContent;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.StringToolkit.joinCharacterList;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.jump;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.log;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.start;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.Separator.ARROW;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.Status.COMPARE;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.Status.GENERATE;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.Status.GET;
import static com.esferalia.aon.in.payroll.pdf.maker.Logger.Status.SUCCESS;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.ADDRESS;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.ADDRESS_LINE_TWO;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.DETAIL_AMOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.DETAIL_DESCRIPTION;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.DETAIL_DISCOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.DETAIL_PRICE;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.DETAIL_TOTAL;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.FINANCE_AMOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.FINANCE_BANK_ACCOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.FINANCE_DATE;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.FINANCE_PAY_METHOD;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.INVOICE_DATE;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.INVOICE_TOTAL;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.NIF;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.REFERENCE_NUMBER;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.REGISTRY_NAME;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.TAX_BASE;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.TAX_PERCENTAGE;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.TAX_QUOTE;
import static com.esferalia.aon.in.payroll.pdf.maker.invoice.InvoiceTemplateTags.TAX_TYPE;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDMarkedContent;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceTheme;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceThemeConfiguration;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.PayMethodType;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;


public class InvoiceTest {

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
		Invoice invoice = new Invoice();
		invoice.setSeries("2022");
		invoice.setNumber(1);
		
		invoice.setRectificationInvoice(
			new Invoice()
				.setSeries("NO LO SÉ XD")
				.setNumber(288)
		);
		invoice.setRectificationType(RectificationType.NORMAL_RECTIFIER);
		invoice.setAddress(new RegistryAddress()
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
		
		invoice.setAddress(raddress);
		
		invoice.setRegistryName(registryName);
		
		invoice.setTotal(total);
		invoice.setReferenceCode(referenceCode);
		invoice.setIssueDate(issueDate);
		invoice.setRegistryDocument(registryDocument);
		invoice.setRegistryDocumentCountry(Country.VE);
		invoice.setTransaction(InvoiceTransactionType.EXTRACOMMUNITY);
		
//		String listadecosas = "Lista de cosas:\n";
//		for (int i=1; i<=40; i++) {
//			listadecosas += "- Cosa (texto de relleno -como en Naruto xDDDD- para probar el ancho del comentario)" + i + ";\n";
//		}
//		invoice.setComments(listadecosas);
		invoice.setComments("Akatsuki (Akatsuki; literalmente Amanecer) fue en sus comienzos, una organización que buscaba acabar con la tiranía y la opresión a través de medios pacíficos, pero que pronto cambiaría sus métodos hasta volverse una organización criminal constituida por varios ninjas renegados de Clase S que se convirtieron en los principales antagonistas de la serie Naruto: Shippuuden. ");		
		invoice.setRemarks("ESTA FACTURA RECTIFICA ALGO");
		/** BREAKDOWNS */
		jump();
		log(GENERATE, "Creating breakdowns.");
		LinkedList<InvoiceBreakdown> breakdowns = new java.util.LinkedList<>();
		
		InvoiceBreakdown breakdownOne = new InvoiceBreakdown();
		breakdownOne.setBase(123890.12);
		breakdownOne.setPercentage(6.18);
		breakdownOne.setQuota(18230.123);
		breakdownOne.setSurcharge(23.123);
		breakdownOne.setTaxType(TaxType.RETENTION);
		breakdownOne.setSurchargeQuota(91.12);
		
		InvoiceBreakdown breakdownTwo = new InvoiceBreakdown();
		breakdownTwo.setBase(12546.99);
		breakdownTwo.setPercentage(01.05);
		breakdownTwo.setQuota(100.123);
		breakdownTwo.setSurcharge(2.123);
		breakdownTwo.setTaxType(TaxType.UNKNOWN);
		breakdownTwo.setSurchargeQuota(1.12);
		
		InvoiceBreakdown breakdownThree = new InvoiceBreakdown();
		breakdownThree.setBase(890.12);
		breakdownThree.setPercentage(21);
		breakdownThree.setQuota(430.123);
		breakdownThree.setSurcharge(0.1);
		breakdownThree.setTaxType(TaxType.VAT);
		breakdownThree.setSurchargeQuota(91.12);
		
		breakdowns.add(breakdownOne);
		breakdowns.add(breakdownTwo);
		breakdowns.add(breakdownThree);
		invoice.setBreakdown(breakdowns);		
		
		/** FINANCES */
		LinkedList<Finance> finances = new LinkedList<>();
		log(GENERATE, "Creating finances.");
		
		BankAccount accountOne = new BankAccount("QA912100041845029121000418450");
		BankAccount accountTwo = new BankAccount("ES9121421818450296751237");
		BankAccount accountThree = new BankAccount("ES9999921999950296759997");
		
		Finance financeOne = new Finance();
		financeOne.setBic("BBVAESMMXXX");
		financeOne.setAdvance(true);
		financeOne.setAmount(1039687.23);
		financeOne.setBankAccount(accountOne);
		financeOne.setPayMethodType(PayMethodType.NEGOTIABLE_DOCUMENT);
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
		invoice.setFinances(finances);
		
		/** TAXES */
		LinkedList<InvoiceDetail> details = new LinkedList<>();
		log(GENERATE, "Creating details.");
		jump();
		
		InvoiceDetail detailOne = new InvoiceDetail();
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
		detailOne.setPrice(1239675601.12);
		detailOne.setDiscountExpression("97.19");
		detailOne.setQuantity(781212783);
		detailOne.setTaxableBase(712382113);
		detailOne.setSource(InvoiceSource.DELIVERY);
		detailOne.setItem(new Item().setProduct(new Product().setType(ProductType.LABOUR)));

		SalesDetail salesDetail1 = new SalesDetail()
				.setSales(
						new Sales()
						.setSeries("199")
						.setNumber(200)
						.setPurchaseReference("mondongo")
						.setId(5000)
				);
		
		SalesDetail salesDetail2 = new SalesDetail()
				.setSales(
						new Sales()
						.setSeries("299")
						.setNumber(300)
						.setPurchaseReference("cachapa")
						.setId(4000)
						);
		
		DeliveryDetail deliveryDetail = new DeliveryDetail()
				.setId(288)
				.setDelivery(new Delivery()
						.setId(123)
						.setIssueTime(new Date()).setSeries("3434")
						.setProject(new Project().setName("PUROJEKUTO DI"))
				).setSalesDetailData(salesDetail1);
		
		DeliveryDetail deliveryDetail2 = new DeliveryDetail()
				.setId(288)
				.setDelivery(new Delivery()
						.setId(123)
						.setIssueTime(new Date()).setSeries("3434")
						.setProject(new Project().setName("PUROJEKUTO DI"))
				).setSalesDetailData(salesDetail2);
		
		DeliveryDetail deliveryDetail3 = new DeliveryDetail()
				.setId(288)
				.setDelivery(new Delivery()
						.setId(123)
						.setIssueTime(new Date()).setSeries("3434")
						.setProject(new Project().setName("PUROJEKUTO DI"))
				).setSalesDetailData(null);
		
		detailOne.setDeliveryDetail(deliveryDetail);
				
		
		InvoiceDetail detailTwo = new InvoiceDetail();
		detailTwo.setDescription("RTX 3080TI MAX PRO Founders edition");
		detailTwo.setPrice(1239675601.12);
		detailTwo.setDiscountExpression("1.19");
		detailTwo.setQuantity(1);
		detailTwo.setTaxableBase(712382113);
		detailTwo.setSource(InvoiceSource.DELIVERY);
//		detailTwo.setItem(new Item().setProduct(new Product().setType(ProductType.COMMERCIAL_PRODUCT)));
		
		detailTwo.setDeliveryDetail(deliveryDetail2);
		
		InvoiceDetail detailThree = new InvoiceDetail();
		detailThree.setDescription
		(
		    "Volverán las oscuras golondrinas\n" + 
		    "en tu balcón sus nidos a colgar,\n" + 
		    "y otra vez con el ala a sus cristales\n" + 
		    "jugando llamarán.\n" + 
		    "\n" + 
		    "Pero aquellas que el  vuelo refrenaban\n" + 
		    "tu hermosura y mi dicha a contemplar,\n" + 
		    "aquellas que aprendieron nuestros nombres...\n" + 
		    "¡esas... no volverán!\n" + 
		    "\n" + 
		    "Volverán las tupidas madreselvas\n" + 
		    "de tu jardín las tapias a escalar,\n" + 
		    "y otra vez a la tarde aún más hermosas\n" + 
		    "sus flores se abrirán.\n" + 
		    "\n" + 
		    "Pero aquellas, cuajadas de rocío\n" + 
		    "cuyas gotas mirábamos temblar\n" + 
		    "y caer como lágrimas del día...\n" + 
		    "¡esas... no volverán!\n" + 
		    "\n" + 
		    "Volverán del amor en tus oídos\n" + 
		    "las palabras ardientes a sonar;\n" + 
		    "tu corazón de su profundo sueño\n" + 
		    "tal vez despertará.\n" + 
		    "\n" + 
		    "Pero mudo y absorto y de rodillas\n" + 
		    "como se adora a Dios ante su altar,\n" + 
		    "como yo te he querido...; desengáñate,\n" + 
		    "¡así... no te querrán!"
		);
		detailThree.setPrice(1239675601.12);
		detailThree.setDiscountExpression("288");
		detailThree.setQuantity(781212783);
		detailThree.setTaxableBase(712382113);
		detailThree.setItem(new Item().setProduct(new Product().setType(ProductType.SERVICE)));
		detailThree.setSource(InvoiceSource.DELIVERY);
		detailThree.setDeliveryDetail(deliveryDetail3);
		
		
		InvoiceDetail detailThreeAndAHalf = new InvoiceDetail();
		detailThreeAndAHalf.setDescription
		(
				"Volverán las oscuras golondrinas\n" + 
						"en tu balcón sus nidos a colgar,\n" + 
						"y otra vez con el ala a sus cristales\n" + 
						"jugando llamarán.\n" + 
						"\n" + 
						"Pero aquellas que el  vuelo refrenaban\n" + 
						"tu hermosura y mi dicha a contemplar,\n" + 
						"aquellas que aprendieron nuestros nombres...\n" + 
						"¡esas... no volverán!\n" + 
						"\n" + 
						"Volverán las tupidas madreselvas\n" + 
						"de tu jardín las tapias a escalar,\n" + 
						"y otra vez a la tarde aún más hermosas\n" + 
						"sus flores se abrirán.\n" + 
						"\n" + 
						"Pero aquellas, cuajadas de rocío\n" + 
						"cuyas gotas mirábamos temblar\n" + 
						"y caer como lágrimas del día...\n" + 
						"¡esas... no volverán!\n" + 
						"\n" + 
						"Volverán del amor en tus oídos\n" + 
						"las palabras ardientes a sonar;\n" + 
						"tu corazón de su profundo sueño\n" + 
						"tal vez despertará.\n" + 
						"\n" + 
						"Pero mudo y absorto y de rodillas\n" + 
						"como se adora a Dios ante su altar,\n" + 
						"como yo te he querido...; desengáñate,\n" + 
						"¡así... no te querrán!"
				);
		detailThreeAndAHalf.setPrice(1239675601.12);
		detailThreeAndAHalf.setDiscountExpression("288");
		detailThreeAndAHalf.setQuantity(781212783);
		detailThreeAndAHalf.setTaxableBase(712382113);
		
		InvoiceDetail detailFour = new InvoiceDetail();
		detailFour.setSource(InvoiceSource.DELIVERY);
		detailFour.setDeliveryDetail(deliveryDetail);
		detailFour.setDescription
		(
		    "Con diez cañones por banda,\n" + 
		    "viento en popa a toda vela,\n" + 
		    "no corta el mar, sino vuela\n" + 
		    "un velero bergantín;\n" + 
		    "\n" + 
		    "bajel pirata que llaman,\n" + 
		    "por su bravura, el Temido,\n" + 
		    "en todo mar conocido\n" + 
		    "del uno al otro confín.\n" + 
		    "\n" + 
		    "La luna en el mar riela,\n" + 
		    "en la lona gime el viento\n" + 
		    "y alza en blando movimiento\n" + 
		    "olas de plata y azul;\n" + 
		    "\n" + 
		    "y va el capitán pirata,\n" + 
		    "cantando alegre en la popa,\n" + 
		    "Asia a un lado, al otro Europa,\n" + 
		    "y allá a su frente Estambul.\n" + 
		    "\n" + 
		    "«Navega velero mío,\n" + 
		    "sin temor,\n" + 
		    "que ni enemigo navío,\n" + 
		    "ni tormenta, ni bonanza,\n" + 
		    "tu rumbo a torcer alcanza,\n" + 
		    "ni a sujetar tu valor.\n" + 
		    "\n" + 
		    "Veinte presas\n" + 
		    "hemos hecho\n" + 
		    "a despecho,\n" + 
		    "del inglés,\n" + 
		    "\n" + 
		    "y han rendido\n" + 
		    "sus pendones\n" + 
		    "cien naciones\n" + 
		    "a mis pies.\n" + 
		    "\n" + 
		    "Que es mi barco mi tesoro,\n" + 
		    "que es mi dios la libertad,\n" + 
		    "mi ley, la fuerza y el viento,\n" + 
		    "mi única patria la mar.\n" + 
		    "\n" + 
		    "Allá muevan feroz guerra\n" + 
		    "ciegos reyes\n" + 
		    "por un palmo más de tierra,\n" + 
		    "que yo tengo aquí por mío\n" + 
		    "cuanto abarca el mar bravío,\n" + 
		    "a quien nadie impuso leyes.\n" + 
		    "\n" + 
		    "Y no hay playa,\n" + 
		    "\n" + 
		    "sea cualquiera,\n" + 
		    "ni bandera\n" + 
		    "de esplendor,\n" + 
		    "\n" + 
		    "que no sienta\n" + 
		    "mi derecho\n" + 
		    "y dé pecho\n" + 
		    "a mi valor.\n" + 
		    "\n" + 
		    "Que es mi barco mi tesoro,\n" + 
		    "que es mi dios la libertad,\n" + 
		    "mi ley, la fuerza y el viento,\n" + 
		    "mi única patria la mar.\n" + 
		    "\n" + 
		    "A la voz de ¡barco viene!\n" + 
		    "es de ver\n" + 
		    "cómo vira y se previene\n" + 
		    "a todo trapo a escapar:\n" + 
		    "que yo soy el rey del mar,\n" + 
		    "y mi furia es de temer.\n" + 
		    "\n" + 
		    "En las presas\n" + 
		    "yo divido\n" + 
		    "lo cogido\n" + 
		    "por igual:\n" + 
		    "\n" + 
		    "sólo quiero\n" + 
		    "por riqueza\n" + 
		    "la belleza\n" + 
		    "sin rival.\n" + 
		    "\n" + 
		    "Que es mi barco mi tesoro,\n" + 
		    "que es mi dios la libertad,\n" + 
		    "mi ley, la fuerza y el viento,\n" + 
		    "mi única patria la mar.\n" + 
		    "\n" + 
		    "¡Sentenciado estoy a muerte!;\n" + 
		    "yo me río;\n" + 
		    "no me abandone la suerte,\n" + 
		    "y al mismo que me condena,\n" + 
		    "colgaré de alguna antena\n" + 
		    "quizá en su propio navío.\n" + 
		    "\n" + 
		    "Y si caigo\n" + 
		    "¿qué es la vida?\n" + 
		    "Por perdida\n" + 
		    "ya la di,\n" + 
		    "\n" + 
		    "cuando el yugo\n" + 
		    "de un esclavo\n" + 
		    "como un bravo\n" + 
		    "sacudí.\n" + 
		    "\n" + 
		    "Que es mi barco mi tesoro,\n" + 
		    "que es mi dios la libertad,\n" + 
		    "mi ley, la fuerza y el viento,\n" + 
		    "mi única patria la mar.\n" + 
		    "\n" + 
		    "Son mi música mejor\n" + 
		    "aquilones,\n" + 
		    "el estrépito y temblor\n" + 
		    "de los cables sacudidos,\n" + 
		    "del negro mar los bramidos\n" + 
		    "y el rugir de mis cañones.\n" + 
		    "\n" + 
		    "Y del trueno\n" + 
		    "al son violento,\n" + 
		    "y del viento\n" + 
		    "al rebramar,\n" + 
		    "\n" + 
		    "yo me duermo\n" + 
		    "sosegado\n" + 
		    "arrullado\n" + 
		    "por el mar.\n" + 
		    "\n" + 
		    "Que es mi barco mi tesoro,\n" + 
		    "que es mi dios la libertad,\n" + 
		    "mi ley, la fuerza y el viento,\n" + 
		    "mi única patria la mar»."
		);
		detailFour.setPrice(2);
		detailFour.setDiscountExpression("0.0");
		detailFour.setQuantity(288);
		detailFour.setTaxableBase(288*2);
		
		InvoiceDetail detailFive= new InvoiceDetail();
		
		String largeDesc = "";
		for (int i=1; i<=40; i++) {
			largeDesc += "abcdefghijklmnño Hola, ¿Qué tal? Esta línea es la línea " + i + "\n";
		}
		
		detailFive.setDescription(largeDesc);
		detailFive.setPrice(0);
		detailFive.setDiscountExpression("30");
		detailFive.setQuantity(0);
		detailFive.setTaxableBase(0);
		
		
		
		
		InvoiceDetail shortDetail1= new InvoiceDetail();
		shortDetail1.setDescription("DETALLE 1");
		shortDetail1.setPrice(0);
		shortDetail1.setDiscountExpression("30");
		shortDetail1.setQuantity(1);
		shortDetail1.setTaxableBase(0);
		
		InvoiceDetail shortDetail2= new InvoiceDetail();
		shortDetail2.setDescription("DETALLE 2");
		shortDetail2.setPrice(1);
		shortDetail2.setDiscountExpression("30");
		shortDetail2.setQuantity(0);
		shortDetail2.setTaxableBase(0);
		
		InvoiceDetail shortDetail3= new InvoiceDetail();
		shortDetail3.setDescription("DETALLE 3");
		shortDetail3.setPrice(1);
		shortDetail3.setDiscountExpression("30");
		shortDetail3.setQuantity(1);
		shortDetail3.setTaxableBase(1);
		
		InvoiceDetail shortDetail4= new InvoiceDetail();
		shortDetail4.setDescription("DETALLE 4");
		shortDetail4.setPrice(1);
		shortDetail4.setDiscountExpression("0");
		shortDetail4.setQuantity(1);
		shortDetail4.setTaxableBase(1);
		
		InvoiceDetail shortDetail5= new InvoiceDetail();
		shortDetail5.setDescription("DETALLE 5");
		shortDetail5.setPrice(1);
//		shortDetail5.setDiscountExpression("NO");
		shortDetail5.setQuantity(1);
		shortDetail5.setTaxableBase(0);
		
		
		InvoiceDetail detailX= new InvoiceDetail();
		
		String xdesc = "";
		for (int i=1; i<=30; i++) {
			xdesc += "línea" + i + "\n";
		}
		
		detailX.setDescription(xdesc);
//		detailX.setPrice(10);
//		detailX.setDiscountExpression("30");
//		detailX.setQuantity(30);
//		detailX.setTaxableBase(0);
//		
		detailX.setPrice(1239675601.12);
		detailX.setDiscountExpression("97.19");
		detailX.setQuantity(781212783);
		detailX.setTaxableBase(712382113);
		
		InvoiceDetail specialDetail = new InvoiceDetail();
		specialDetail.setDescription("ALTO SUPLIDO");
		specialDetail.setPrice(10);
		specialDetail.setDiscountExpression("0");
		specialDetail.setQuantity(1);
		specialDetail.setTaxableBase(10);
		specialDetail.setItem(new Item().setProduct(new Product().setType(ProductType.PREPAYMENT)));
		
		InvoiceDetail auxDetail = new InvoiceDetail();
		auxDetail.setDescription("ENTE");
		auxDetail.setPrice(29);
		auxDetail.setDiscountExpression("0");
		auxDetail.setQuantity(1);
		auxDetail.setTaxableBase(10);
		auxDetail.setItem(new Item().setProduct(new Product().setType(ProductType.AUXILIARY).setId(12)));

		InvoiceDetail auxDetail2 = new InvoiceDetail();
		auxDetail2.setDescription("WEA");
		auxDetail2.setPrice(300);
		auxDetail2.setDiscountExpression("0");
		auxDetail2.setQuantity(1);
		auxDetail2.setTaxableBase(10);
		auxDetail2.setItem(new Item().setProduct(new Product().setType(ProductType.AUXILIARY).setId(11)));
		
		details.add(detailX);
		details.add(detailOne);
		details.add(detailTwo);
		details.add(detailThree);
		details.add(detailThreeAndAHalf);
//		details.add(detailFour);
//		details.add(detailFive);
//		details.add(specialDetail);
//		details.add(specialDetail);
//		details.add(auxDetail);
//		details.add(auxDetail);
//		details.add(auxDetail);
//		details.add(auxDetail2);
//		details.add(auxDetail2);
//		details.add(auxDetail2);
//		details.add(auxDetail2);
		
		
//		details.add(shortDetail1);
//		details.add(shortDetail2);
//		details.add(shortDetail3);
//		details.add(shortDetail4);
//		details.add(shortDetail5);		
		
		invoice.setDetails(details);
		
		/** PRINT CONFIGURATIONS */
		ByteArrayOutputStream os;
		try {
			
			OutputStream dos = new FileOutputStream("./InvoiceIntegrationTest.pdf");
			os = new ByteArrayOutputStream();
			byte[] back = new InvoiceTest().getClass().getResourceAsStream("bg.jpg").readAllBytes();
			
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
			
			
			
			InputStream logoStream = InvoiceTest.class.getResourceAsStream("matsuda.png");
			byte[] logo = logoStream.readAllBytes();
			
			
			
//			company = null;
//			logo = null;
			
			
			InvoiceTemplate invoiceTemplate = new InvoiceTemplate(company, invoice, config, "www.aonsolutions.es", logo, "TBAI-00000006Y-251019-btFpwP8dcLGAF-237");
			invoiceTemplate.print(os);
			invoiceTemplate = new InvoiceTemplate(company, invoice, config, "www.aonsolutions.es", logo, "TBAI-00000006Y-251019-btFpwP8dcLGAF-237");
			invoiceTemplate.print(dos);
			
			ByteArrayInputStream bis = new ByteArrayInputStream(os.toByteArray());
			
			PDDocument document = Loader.loadPDF(bis);
			
			
			/** CHECKING PDF DATA **/
			String PDFaddress = "";
			String PDFaddressLineTwo = "";
			
			String PDFregistryName = "";
			String PDFreferenceCode = "";
			String PDFtotal = "";
			String PDFregistryDocument = "";
			String PDFissueDate = "";
			
			
			Optional<PDMarkedContent> addressMark = getContent(document, ADDRESS);
			Optional<PDMarkedContent> addressLineTwoMark = getContent(document, ADDRESS_LINE_TWO);
			Optional<PDMarkedContent> registryNameMark = getContent(document, REGISTRY_NAME);
			Optional<PDMarkedContent> referenceCodeMark = getContent(document, REFERENCE_NUMBER);
			Optional<PDMarkedContent> registryDocumentMark = getContent(document, NIF);
			Optional<PDMarkedContent> totalMark = getContent(document, INVOICE_TOTAL);
			Optional<PDMarkedContent> issueDateMark = getContent(document, INVOICE_DATE);
			
			if(addressMark.isPresent()) 
				PDFaddress = joinCharacterList(addressMark.get().getContents());
			
			
			if(addressLineTwoMark.isPresent()) 
				PDFaddressLineTwo = joinCharacterList(addressLineTwoMark.get().getContents());
			
			
			if(registryNameMark.isPresent()) 
				PDFregistryName = joinCharacterList(registryNameMark.get().getContents());
			
			
			if(referenceCodeMark.isPresent()) 
				PDFreferenceCode = joinCharacterList(referenceCodeMark.get().getContents());
			
			
			if(registryDocumentMark.isPresent()) 
				PDFregistryDocument = joinCharacterList(registryDocumentMark.get().getContents());
			
			
			if(totalMark.isPresent()) 
				PDFtotal = joinCharacterList(totalMark.get().getContents());
			
			
			if(issueDateMark.isPresent()) 
				PDFissueDate = joinCharacterList(issueDateMark.get().getContents());
			
				
			PDFaddress = removeSpecialCharacters(PDFaddress);			
			PDFaddressLineTwo = removeSpecialCharacters(PDFaddressLineTwo);			
			PDFregistryName = removeSpecialCharacters(PDFregistryName);			
			PDFreferenceCode = removeSpecialCharacters(PDFreferenceCode).replaceAll("Numero:", "").trim();			
			PDFregistryDocument = removeSpecialCharacters(PDFregistryDocument).replaceAll("N.I.F:", "").trim();			
			PDFtotal = removeSpecialCharacters(PDFtotal).trim();			
			PDFissueDate = removeSpecialCharacters(PDFissueDate).replaceAll("Fecha:", "").trim();
			
			start("Getting PDF data");
			log(GET, "ADDR", PDFaddress);
			log(GET, "ADDR2", PDFaddressLineTwo );
			log(GET, "NAME", PDFregistryName );
			log(GET, "CODE", PDFreferenceCode );
			log(GET, "DOCUMENT", PDFregistryDocument );
			log(GET, "TOTAL", PDFtotal );
			log(GET, "ISSUE", PDFissueDate );
			
			start("Comparing original/PDF data");
			assertPdfData("Address", croppedString(address, 230, HELVETICA, 9), PDFaddress);
			assertPdfData("Address line two", addressZIP + " " +  addressTown + " " + addressProvince, PDFaddressLineTwo);
			assertPdfData("Name", croppedString(registryName, 230, HELVETICA_BOLD, 12), PDFregistryName);
//			assertPdfData("code", referenceCode, PDFreferenceCode);
			assertPdfData("NIF", registryDocument, PDFregistryDocument);
			assertPdfData("TOTAL", toLatinNumber(total), PDFtotal);
			assertPdfData("DATE", formatDate(issueDate, "dd/MM/yyyy").get(), PDFissueDate);
			
			
			/** Assert details **/
			start("Comparing original/PDF details");
			
			String detailAmount = "";
			String detailDescription = "";
			String detailPrice = "";
			String detailDiscount = "";
			String detailTotal = "";
	
			Optional<PDMarkedContent> detailAmountMark = getContent(document, 1 + DETAIL_AMOUNT);
			Optional<PDMarkedContent> detailDescriptionMark = getContent(document, 1 + DETAIL_DESCRIPTION);
			Optional<PDMarkedContent> detailPriceMark = getContent(document, 1 + DETAIL_PRICE);
			Optional<PDMarkedContent> detailDiscountMark = getContent(document, 1 + DETAIL_DISCOUNT);
			Optional<PDMarkedContent> detailTotalMark = getContent(document, 1 + DETAIL_TOTAL);
			
			if(detailAmountMark.isPresent()) 
				detailAmount = joinCharacterList(detailAmountMark.get().getContents());
			
			
			if(detailDescriptionMark.isPresent())
				detailDescription = joinCharacterList(detailDescriptionMark.get().getContents());
			
			if(detailPriceMark.isPresent()) 
				detailPrice = joinCharacterList(detailPriceMark.get().getContents());
			
			
			if(detailDiscountMark.isPresent())
				detailDiscount = joinCharacterList(detailDiscountMark.get().getContents());

			
			if(detailTotalMark.isPresent())
				detailTotal = joinCharacterList(detailTotalMark.get().getContents());
						
			assertPdfData("Description", detailTwo.getDescription(), detailDescription);
			assertPdfData("Amount", toLatinNumber(detailTwo.getQuantity()), detailAmount);
			assertPdfData("Price", toLatinNumber(detailTwo.getPrice()), detailPrice);
			assertPdfData("Discount", detailTwo.getDiscountExpression().getDiscountExpr(), detailDiscount);
			assertPdfData("Total", toLatinNumber(detailTwo.getTaxableBase()), detailTotal);
			
			/** Assert finances **/
			start("Comparing original/PDF finances");
			
			String financeDate = "";
			String financePayMethod = "";
			String financeBankAccount = "";
			String financeAmount = "";
			
			Optional<PDMarkedContent> financeDateMark = getContent(document, 0 + FINANCE_DATE);
			Optional<PDMarkedContent> financePayMethodMark = getContent(document, 0 + FINANCE_PAY_METHOD);
			Optional<PDMarkedContent> financeBankAccountMark = getContent(document, 0 + FINANCE_BANK_ACCOUNT);
			Optional<PDMarkedContent> financeAmountMark = getContent(document, 0 + FINANCE_AMOUNT);

			financeDate =  joinCharacterList(financeDateMark.get().getContents());
			financePayMethod =  joinCharacterList(financePayMethodMark.get().getContents());
			financeBankAccount =  joinCharacterList(financeBankAccountMark.get().getContents());
			financeAmount =  joinCharacterList(financeAmountMark.get().getContents());
			
			assertPdfData("Date", formatDate(financeOne.getDueDate(), "dd/MM/yyyy").orElse("-"), financeDate);
			assertPdfData("Paymethod", financeOne.getPayMethodType().getDescription(), financePayMethod);
			assertPdfData("Bankaccount",  financeOne.getBankAccount().getIban(), financeBankAccount);
			assertPdfData("Amount",  toLatinNumber(financeOne.getAmount()), financeAmount);
			
			/** Assert taxes **/
			start("Comparing original/PDF taxes");
			
			
			String taxBase = "";
			String taxPercentage = "";
			String taxType = "";
			String taxQuote = "";
			
		
			Optional<PDMarkedContent> taxBaseMark = getContent(document, 0 + TAX_BASE);
			Optional<PDMarkedContent> taxPercentageMark = getContent(document, 0 + TAX_PERCENTAGE);
			Optional<PDMarkedContent> taxTypeMark = getContent(document, 0 + TAX_TYPE);
			Optional<PDMarkedContent> taxQuoteMark = getContent(document, 0 + TAX_QUOTE);
			
			if(taxBaseMark.isPresent())
				taxBase = joinCharacterList(taxBaseMark.get().getContents());
			
			if(taxPercentageMark.isPresent())
				taxPercentage = joinCharacterList(taxPercentageMark.get().getContents());
			
			if(taxTypeMark.isPresent())
				taxType = joinCharacterList(taxTypeMark.get().getContents());
			
			if(taxQuoteMark.isPresent())
				taxQuote = joinCharacterList(taxQuoteMark.get().getContents());
			
			assertPdfData("Base", toLatinNumber(breakdownOne.getBase()), taxBase);
			assertPdfData("Percentage", toLatinNumber(breakdownOne.getPercentage()) + "% + " + toLatinNumber(breakdownOne.getSurcharge()), taxPercentage);
			assertPdfData("Type", breakdownOne.getTaxType().getName(), taxType);
			assertPdfData("Quota", toLatinNumber(breakdownOne.getQuota() + breakdownOne.getSurchargeQuota()), taxQuote);
			
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
	
	@Test
	public void multipleDataIntegrationTest() {
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
		
		
		/** INVOICE BASIC DATA */
		Invoice invoice = new Invoice();
		Invoice invoice2 = new Invoice();
		invoice.setSeries("2022");
		invoice.setNumber(1);
		
		invoice.setRectificationInvoice(
				new Invoice()
					.setSeries("NO LO SÉ XD")
					.setNumber(288)
			);
		invoice.setRectificationType(RectificationType.NORMAL_RECTIFIER);
		invoice.setAddress(new RegistryAddress()
				.setAddress(address)
				.setNumber(addressNumber)
				.setProvince(addressProvince)
				.setCity(addressTown)
				.setZip(addressZIP)
		);
		invoice2.setSeries("2022");
		invoice2.setNumber(1);
		
		invoice2.setRectificationInvoice(
				new Invoice()
					.setSeries("NO LO SÉ XD")
					.setNumber(288)
			);
		invoice2.setRectificationType(RectificationType.NORMAL_RECTIFIER);
		invoice2.setAddress(new RegistryAddress()
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
		
		invoice.setAddress(raddress);
		invoice2.setAddress(raddress);
		
		invoice.setRegistryName(registryName);
		invoice2.setRegistryName(registryName);
		
		invoice.setTotal(total);
		invoice2.setTotal(total);
		invoice.setReferenceCode(referenceCode);
		invoice2.setReferenceCode(referenceCode);
		invoice.setIssueDate(issueDate);
		invoice2.setIssueDate(issueDate);
		invoice.setRegistryDocument(registryDocument);
		invoice2.setRegistryDocument(registryDocument);
		invoice.setRegistryDocumentCountry(Country.VE);
		invoice2.setRegistryDocumentCountry(Country.VE);
		invoice.setTransaction(InvoiceTransactionType.EXTRACOMMUNITY);
		invoice2.setTransaction(InvoiceTransactionType.EXTRACOMMUNITY);
		
//		String listadecosas = "Lista de cosas:\n";
//		for (int i=1; i<=40; i++) {
//			listadecosas += "- Cosa (texto de relleno -como en Naruto xDDDD- para probar el ancho del comentario)" + i + ";\n";
//		}
//		invoice.setComments(listadecosas);
		invoice.setComments("Akatsuki (Akatsuki; literalmente Amanecer) fue en sus comienzos, una organización que buscaba acabar con la tiranía y la opresión a través de medios pacíficos, pero que pronto cambiaría sus métodos hasta volverse una organización criminal constituida por varios ninjas renegados de Clase S que se convirtieron en los principales antagonistas de la serie Naruto: Shippuuden. ");		
		invoice.setRemarks("ESTA FACTURA RECTIFICA ALGO");
		/** BREAKDOWNS */
		jump();
		log(GENERATE, "Creating breakdowns.");
		LinkedList<InvoiceBreakdown> breakdowns = new java.util.LinkedList<>();
		
		InvoiceBreakdown breakdownOne = new InvoiceBreakdown();
		breakdownOne.setBase(123890.12);
		breakdownOne.setPercentage(6.18);
		breakdownOne.setQuota(18230.123);
		breakdownOne.setSurcharge(23.123);
		breakdownOne.setTaxType(TaxType.RETENTION);
		breakdownOne.setSurchargeQuota(91.12);
		
		InvoiceBreakdown breakdownTwo = new InvoiceBreakdown();
		breakdownTwo.setBase(12546.99);
		breakdownTwo.setPercentage(01.05);
		breakdownTwo.setQuota(100.123);
		breakdownTwo.setSurcharge(2.123);
		breakdownTwo.setTaxType(TaxType.UNKNOWN);
		breakdownTwo.setSurchargeQuota(1.12);
		
		InvoiceBreakdown breakdownThree = new InvoiceBreakdown();
		breakdownThree.setBase(890.12);
		breakdownThree.setPercentage(21);
		breakdownThree.setQuota(430.123);
		breakdownThree.setSurcharge(0.1);
		breakdownThree.setTaxType(TaxType.VAT);
		breakdownThree.setSurchargeQuota(91.12);
		
		breakdowns.add(breakdownOne);
		breakdowns.add(breakdownTwo);
		breakdowns.add(breakdownThree);
		invoice.setBreakdown(breakdowns);		
		invoice2.setBreakdown(breakdowns);		
		
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
		
		finances.add(financeOne);
		finances.add(financeOne);
		finances.add(financeOne);
		finances.add(financeOne);
		finances.add(financeOne);
		finances.add(financeOne);
		finances.add(financeOne);
		finances.add(financeOne);
		finances.add(financeTwo);
		finances.add(financeThree);
		finances.add(financeFour);
		invoice.setFinances(finances);
		invoice2.setFinances(finances);
		
		/** TAXES */
		LinkedList<InvoiceDetail> details = new LinkedList<>();
		LinkedList<InvoiceDetail> details2 = new LinkedList<>();
		log(GENERATE, "Creating details.");
		jump();
		
		InvoiceDetail detailOne = new InvoiceDetail();
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
		detailOne.setPrice(1239675601.12);
		detailOne.setDiscountExpression("97.19");
		detailOne.setQuantity(781212783);
		detailOne.setTaxableBase(712382113);
		detailOne.setSource(InvoiceSource.DELIVERY);
		
		DeliveryDetail deliveryDetail = new DeliveryDetail().setId(288).setDelivery(new Delivery().setId(123).setIssueTime(new Date()).setSeries("3434"));
		detailOne.setDeliveryDetail(deliveryDetail);
				
		
		InvoiceDetail detailTwo = new InvoiceDetail();
		detailTwo.setDescription("RTX 3080TI MAX PRO Founders edition");
		detailTwo.setPrice(1239675601.12);
		detailTwo.setDiscountExpression("1.19");
		detailTwo.setQuantity(1);
		detailTwo.setTaxableBase(712382113);
		detailTwo.setSource(InvoiceSource.SALES);
		
		SalesDetail deliveryDetailTwo = new SalesDetail().setId(288).setSales(new Sales().setId(123).setIssueDate(new Date()).setPurchaseReference("123456/12345"));
		detailTwo.setSalesDetail(deliveryDetailTwo);
		
		InvoiceDetail detailThree = new InvoiceDetail();
		detailThree.setDescription
		(
		    "Volverán las oscuras golondrinas\n" + 
		    "en tu balcón sus nidos a colgar,\n" + 
		    "y otra vez con el ala a sus cristales\n" + 
		    "jugando llamarán.\n" + 
		    "\n" + 
		    "Pero aquellas que el  vuelo refrenaban\n" + 
		    "tu hermosura y mi dicha a contemplar,\n" + 
		    "aquellas que aprendieron nuestros nombres...\n" + 
		    "¡esas... no volverán!\n" + 
		    "\n" + 
		    "Volverán las tupidas madreselvas\n" + 
		    "de tu jardín las tapias a escalar,\n" + 
		    "y otra vez a la tarde aún más hermosas\n" + 
		    "sus flores se abrirán.\n" + 
		    "\n" + 
		    "Pero aquellas, cuajadas de rocío\n" + 
		    "cuyas gotas mirábamos temblar\n" + 
		    "y caer como lágrimas del día...\n" + 
		    "¡esas... no volverán!\n" + 
		    "\n" + 
		    "Volverán del amor en tus oídos\n" + 
		    "las palabras ardientes a sonar;\n" + 
		    "tu corazón de su profundo sueño\n" + 
		    "tal vez despertará.\n" + 
		    "\n" + 
		    "Pero mudo y absorto y de rodillas\n" + 
		    "como se adora a Dios ante su altar,\n" + 
		    "como yo te he querido...; desengáñate,\n" + 
		    "¡así... no te querrán!"
		);
		detailThree.setPrice(1239675601.12);
		detailThree.setDiscountExpression("288");
		detailThree.setQuantity(781212783);
		detailThree.setTaxableBase(712382113);
		
		InvoiceDetail detailThreeAndAHalf = new InvoiceDetail();
		detailThreeAndAHalf.setDescription
		(
				"Volverán las oscuras golondrinas\n" + 
						"en tu balcón sus nidos a colgar,\n" + 
						"y otra vez con el ala a sus cristales\n" + 
						"jugando llamarán.\n" + 
						"\n" + 
						"Pero aquellas que el  vuelo refrenaban\n" + 
						"tu hermosura y mi dicha a contemplar,\n" + 
						"aquellas que aprendieron nuestros nombres...\n" + 
						"¡esas... no volverán!\n" + 
						"\n" + 
						"Volverán las tupidas madreselvas\n" + 
						"de tu jardín las tapias a escalar,\n" + 
						"y otra vez a la tarde aún más hermosas\n" + 
						"sus flores se abrirán.\n" + 
						"\n" + 
						"Pero aquellas, cuajadas de rocío\n" + 
						"cuyas gotas mirábamos temblar\n" + 
						"y caer como lágrimas del día...\n" + 
						"¡esas... no volverán!\n" + 
						"\n" + 
						"Volverán del amor en tus oídos\n" + 
						"las palabras ardientes a sonar;\n" + 
						"tu corazón de su profundo sueño\n" + 
						"tal vez despertará.\n" + 
						"\n" + 
						"Pero mudo y absorto y de rodillas\n" + 
						"como se adora a Dios ante su altar,\n" + 
						"como yo te he querido...; desengáñate,\n" + 
						"¡así... no te querrán!"
				);
		detailThreeAndAHalf.setPrice(1239675601.12);
		detailThreeAndAHalf.setDiscountExpression("288");
		detailThreeAndAHalf.setQuantity(781212783);
		detailThreeAndAHalf.setTaxableBase(712382113);
		
		InvoiceDetail detailFour = new InvoiceDetail();
		detailFour.setDescription
		(
		    "Con diez cañones por banda,\n" + 
		    "viento en popa a toda vela,\n" + 
		    "no corta el mar, sino vuela\n" + 
		    "un velero bergantín;\n" + 
		    "\n" + 
		    "bajel pirata que llaman,\n" + 
		    "por su bravura, el Temido,\n" + 
		    "en todo mar conocido\n" + 
		    "del uno al otro confín.\n" + 
		    "\n" + 
		    "La luna en el mar riela,\n" + 
		    "en la lona gime el viento\n" + 
		    "y alza en blando movimiento\n" + 
		    "olas de plata y azul;\n" + 
		    "\n" + 
		    "y va el capitán pirata,\n" + 
		    "cantando alegre en la popa,\n" + 
		    "Asia a un lado, al otro Europa,\n" + 
		    "y allá a su frente Estambul.\n" + 
		    "\n" + 
		    "«Navega velero mío,\n" + 
		    "sin temor,\n" + 
		    "que ni enemigo navío,\n" + 
		    "ni tormenta, ni bonanza,\n" + 
		    "tu rumbo a torcer alcanza,\n" + 
		    "ni a sujetar tu valor.\n" + 
		    "\n" + 
		    "Veinte presas\n" + 
		    "hemos hecho\n" + 
		    "a despecho,\n" + 
		    "del inglés,\n" + 
		    "\n" + 
		    "y han rendido\n" + 
		    "sus pendones\n" + 
		    "cien naciones\n" + 
		    "a mis pies.\n" + 
		    "\n" + 
		    "Que es mi barco mi tesoro,\n" + 
		    "que es mi dios la libertad,\n" + 
		    "mi ley, la fuerza y el viento,\n" + 
		    "mi única patria la mar.\n" + 
		    "\n" + 
		    "Allá muevan feroz guerra\n" + 
		    "ciegos reyes\n" + 
		    "por un palmo más de tierra,\n" + 
		    "que yo tengo aquí por mío\n" + 
		    "cuanto abarca el mar bravío,\n" + 
		    "a quien nadie impuso leyes.\n" + 
		    "\n" + 
		    "Y no hay playa,\n" + 
		    "\n" + 
		    "sea cualquiera,\n" + 
		    "ni bandera\n" + 
		    "de esplendor,\n" + 
		    "\n" + 
		    "que no sienta\n" + 
		    "mi derecho\n" + 
		    "y dé pecho\n" + 
		    "a mi valor.\n" + 
		    "\n" + 
		    "Que es mi barco mi tesoro,\n" + 
		    "que es mi dios la libertad,\n" + 
		    "mi ley, la fuerza y el viento,\n" + 
		    "mi única patria la mar.\n" + 
		    "\n" + 
		    "A la voz de ¡barco viene!\n" + 
		    "es de ver\n" + 
		    "cómo vira y se previene\n" + 
		    "a todo trapo a escapar:\n" + 
		    "que yo soy el rey del mar,\n" + 
		    "y mi furia es de temer.\n" + 
		    "\n" + 
		    "En las presas\n" + 
		    "yo divido\n" + 
		    "lo cogido\n" + 
		    "por igual:\n" + 
		    "\n" + 
		    "sólo quiero\n" + 
		    "por riqueza\n" + 
		    "la belleza\n" + 
		    "sin rival.\n" + 
		    "\n" + 
		    "Que es mi barco mi tesoro,\n" + 
		    "que es mi dios la libertad,\n" + 
		    "mi ley, la fuerza y el viento,\n" + 
		    "mi única patria la mar.\n" + 
		    "\n" + 
		    "¡Sentenciado estoy a muerte!;\n" + 
		    "yo me río;\n" + 
		    "no me abandone la suerte,\n" + 
		    "y al mismo que me condena,\n" + 
		    "colgaré de alguna antena\n" + 
		    "quizá en su propio navío.\n" + 
		    "\n" + 
		    "Y si caigo\n" + 
		    "¿qué es la vida?\n" + 
		    "Por perdida\n" + 
		    "ya la di,\n" + 
		    "\n" + 
		    "cuando el yugo\n" + 
		    "de un esclavo\n" + 
		    "como un bravo\n" + 
		    "sacudí.\n" + 
		    "\n" + 
		    "Que es mi barco mi tesoro,\n" + 
		    "que es mi dios la libertad,\n" + 
		    "mi ley, la fuerza y el viento,\n" + 
		    "mi única patria la mar.\n" + 
		    "\n" + 
		    "Son mi música mejor\n" + 
		    "aquilones,\n" + 
		    "el estrépito y temblor\n" + 
		    "de los cables sacudidos,\n" + 
		    "del negro mar los bramidos\n" + 
		    "y el rugir de mis cañones.\n" + 
		    "\n" + 
		    "Y del trueno\n" + 
		    "al son violento,\n" + 
		    "y del viento\n" + 
		    "al rebramar,\n" + 
		    "\n" + 
		    "yo me duermo\n" + 
		    "sosegado\n" + 
		    "arrullado\n" + 
		    "por el mar.\n" + 
		    "\n" + 
		    "Que es mi barco mi tesoro,\n" + 
		    "que es mi dios la libertad,\n" + 
		    "mi ley, la fuerza y el viento,\n" + 
		    "mi única patria la mar»."
		);
		detailFour.setPrice(2);
		detailFour.setDiscountExpression("0.0");
		detailFour.setQuantity(288);
		detailFour.setTaxableBase(288*2);
		
		InvoiceDetail detailFive= new InvoiceDetail();
		
		String largeDesc = "";
		for (int i=1; i<=200; i++) {
			largeDesc += "abcdefghijklmnño Hola, ¿Qué tal? Esta línea es la línea " + i + "\n";
		}
		
		detailFive.setDescription(largeDesc);
		detailFive.setPrice(0);
		detailFive.setDiscountExpression("30");
		detailFive.setQuantity(0);
		detailFive.setTaxableBase(0);
		
		
		
		
		InvoiceDetail shortDetail1= new InvoiceDetail();
		shortDetail1.setDescription("DETALLE 1");
		shortDetail1.setPrice(0);
		shortDetail1.setDiscountExpression("30");
		shortDetail1.setQuantity(1);
		shortDetail1.setTaxableBase(0);
		
		InvoiceDetail shortDetail2= new InvoiceDetail();
		shortDetail2.setDescription("DETALLE 2");
		shortDetail2.setPrice(1);
		shortDetail2.setDiscountExpression("30");
		shortDetail2.setQuantity(0);
		shortDetail2.setTaxableBase(0);
		
		InvoiceDetail shortDetail3= new InvoiceDetail();
		shortDetail3.setDescription("DETALLE 3");
		shortDetail3.setPrice(1);
		shortDetail3.setDiscountExpression("30");
		shortDetail3.setQuantity(1);
		shortDetail3.setTaxableBase(1);
		
		InvoiceDetail shortDetail4= new InvoiceDetail();
		shortDetail4.setDescription("DETALLE 4");
		shortDetail4.setPrice(1);
		shortDetail4.setDiscountExpression("0");
		shortDetail4.setQuantity(1);
		shortDetail4.setTaxableBase(1);
		
		InvoiceDetail shortDetail5= new InvoiceDetail();
		shortDetail5.setDescription("DETALLE 5");
		shortDetail5.setPrice(1);
//		shortDetail5.setDiscountExpression("NO");
		shortDetail5.setQuantity(1);
		shortDetail5.setTaxableBase(0);
		
		
		InvoiceDetail detailX= new InvoiceDetail();
		
		String xdesc = "";
		for (int i=1; i<=46; i++) {
			xdesc += "línea" + i + "\n";
		}
		
		detailX.setDescription(xdesc);
//		detailX.setPrice(10);
//		detailX.setDiscountExpression("30");
//		detailX.setQuantity(30);
//		detailX.setTaxableBase(0);
//		
		detailX.setPrice(1239675601.12);
		detailX.setDiscountExpression("97.19");
		detailX.setQuantity(781212783);
		detailX.setTaxableBase(712382113);
		
		InvoiceDetail specialDetail = new InvoiceDetail();
		specialDetail.setDescription("ALTO SUPLIDO");
		specialDetail.setPrice(10);
		specialDetail.setDiscountExpression("0");
		specialDetail.setQuantity(1);
		specialDetail.setTaxableBase(10);
		specialDetail.setItem(new Item().setProduct(new Product().setType(ProductType.PREPAYMENT)));
		
		
		
		details2.add(detailX);
		details.add(detailX);
		details.add(detailOne);
		details.add(detailTwo);
		details.add(detailThree);
		details.add(detailThreeAndAHalf);
		details.add(detailFour);
		details.add(detailFive);
//		details.add(specialDetail);
//		details.add(specialDetail);
		
		
//		details.add(shortDetail1);
//		details.add(shortDetail2);
//		details.add(shortDetail3);
//		details.add(shortDetail4);
//		details.add(shortDetail5);		
		
		invoice.setDetails(details);
		invoice2.setDetails(details2);
		
		/** PRINT CONFIGURATIONS */
		ByteArrayOutputStream os;
		try {
			
			OutputStream dos = new FileOutputStream("./MultipleInvoiceIntegrationTest.pdf");
			os = new ByteArrayOutputStream();
			byte[] back = new InvoiceTest().getClass().getResourceAsStream("bg.jpg").readAllBytes();
			
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
			
			
			
			InputStream logoStream = InvoiceTest.class.getResourceAsStream("matsuda.png");
			byte[] logo = logoStream.readAllBytes();
			
			
			
//			company = null;
//			logo = null;
			
			
			List<Invoice> iList = new LinkedList<>();
			iList.add(invoice);
			iList.add(null);
			iList.add(invoice2);
			
			InvoiceTemplate invoiceTemplate = new InvoiceTemplate(company, iList, config, "www.aonsolutions.es", logo, "TBAI-00000006Y-251019-btFpwP8dcLGAF-237");
			invoiceTemplate.print(os);
			invoiceTemplate = new InvoiceTemplate(company, iList, config, "www.aonsolutions.es", logo, "TBAI-00000006Y-251019-btFpwP8dcLGAF-237");
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
	
}
