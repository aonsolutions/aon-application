package net.aonsolutions.aon.verifactu;

import java.util.Date;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;

import uk.co.jemos.podam.api.PodamUtils;

class VerifactuTestsUtils {

	static final String DATE_FORMAT = "dd-MM-yyyy";
	
	private VerifactuTestsUtils() {
		
	}
	// ********************************* [INVOICE NUMBER]
	static String referenceCode() {
		return referenceCode("A");
	}
	static String rectifierReferenceCode() {
		return referenceCode("R");
	}
	static String referenceCode(String prefix) {
		Date now = new Date();
		return prefix 
			+ AonDateUtils.getYear(now) 
			+ "/"
			+ now.getTime()
		;
	}
		
	// ******************************************* [DATE]
	public static Date issueDate() {
		return AonDateUtils.today();
	}
	
	// ******************************************* [CUSTOMER]
	
	// Creados en el método net.aonsolutions.aon.verifactu.DomainProviderForTests.insertCustomers() 
	static final String[] CUSTOMER_DOCUMENTS = new String[] {
		"B98351984",	"B95717484",	"15247056B",	"07485941Q",
		"52717592M",	"B66068065",	"E07170327",	"B98465644",
		"75407353J",	"43162588Y",	"B57551251",	"43102210A",
	};
	static String getRandomCustomerDocument() {
		return CUSTOMER_DOCUMENTS[PodamUtils.getIntegerInRange(0, CUSTOMER_DOCUMENTS.length - 1 )];
	}
	
	static CustomerFull getCustomer(AONContext ctx, int domain) {
		return getCustomer( ctx, domain,  getRandomCustomerDocument());
	}
	static CustomerFull getCustomerNoCensado(AONContext ctx, int domain) {
		return getCustomer( ctx, domain,  "X1485566L");
	}
	static CustomerFull getCustomerCedilla(AONContext ctx, int domain) {
		return getCustomer( ctx, domain,  "X3654266A");
	}
		
	
	static final String[] INTR_CUSTOMER_DOCUMENTS = new String[] {
		"12487773327"
	};
	static String getRandomIntrCustomerDocument() {
		return INTR_CUSTOMER_DOCUMENTS[PodamUtils.getIntegerInRange(0, INTR_CUSTOMER_DOCUMENTS.length - 1 )];
	}
	static CustomerFull getIntrCustomer(AONContext ctx, int domain) {
		return getCustomer( ctx, domain,  getRandomIntrCustomerDocument());
	}
	
	static final String[] EXTR_CUSTOMER_DOCUMENTS = new String[] {
		"999999999A"
	};
	static String getRandomExtrCustomerDocument() {
		return EXTR_CUSTOMER_DOCUMENTS[PodamUtils.getIntegerInRange(0, EXTR_CUSTOMER_DOCUMENTS.length - 1 )];
	}
	static CustomerFull getExtrCustomer(AONContext ctx, int domain) {
		return getCustomer( ctx, domain,  getRandomExtrCustomerDocument());
	}

	
	static final String[] CAN_CEU_MEL_CUSTOMER_DOCUMENTS = new String[] {
		"A38025938"
	};
	static String getRandomCanCeuMelCustomerDocument() {
		return CAN_CEU_MEL_CUSTOMER_DOCUMENTS[PodamUtils.getIntegerInRange(0, CAN_CEU_MEL_CUSTOMER_DOCUMENTS.length - 1 )];
	}
	static CustomerFull getCanCeuMelCustomer(AONContext ctx, int domain) {
		return getCustomer( ctx, domain,  getRandomCanCeuMelCustomerDocument());
	}
	
	static CustomerFull getContadoCustomer(AONContext ctx, int domain) {
		return CustomerDAO.getStream(ctx, f -> f.getDocumentProperty().isNull().and(f.getDomainProperty().eq(domain)) )
				.map( c -> CustomerDAO.getFull(ctx, c.getId()) )
				.findFirst()
				.orElseThrow(() -> new AonCoreException("No encuentro el cliente con documento nulo"));
	}
	
	
	static CustomerFull getCustomer(AONContext ctx, int domain, String document) {
		return CustomerDAO.getStream(ctx, f -> f.getDocumentProperty().eq(document).and(f.getDomainProperty().eq(domain)) )
			.map( c -> CustomerDAO.getFull(ctx, c.getId()) )
			.findFirst()
			.orElseThrow(() -> new AonCoreException("No encuentro el cliente con documento " + document));
	}

}
