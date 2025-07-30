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
		return prefix 
			+ AonDateUtils.getYear(issueDate()) 
			+ "/"
			+ issueDate().getTime()
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
		"X3654266A",	"X1485566L",
	};
	static String getRandomCustomerDocument() {
		return CUSTOMER_DOCUMENTS[PodamUtils.getIntegerInRange(0, CUSTOMER_DOCUMENTS.length - 1 )];
	}
	
	static CustomerFull getCustomer(AONContext ctx) {
		return getCustomer( ctx,  getRandomCustomerDocument());
	}
	
	static final String[] INTR_CUSTOMER_DOCUMENTS = new String[] {
		"393356000000"
	};
	static String getRandomIntrCustomerDocument() {
		return INTR_CUSTOMER_DOCUMENTS[PodamUtils.getIntegerInRange(0, INTR_CUSTOMER_DOCUMENTS.length - 1 )];
	}
	static CustomerFull getIntrCustomer(AONContext ctx) {
		return getCustomer( ctx,  getRandomIntrCustomerDocument());
	}
	
	static CustomerFull getCustomer(AONContext ctx, String document) {
		return CustomerDAO.getStream(ctx, f -> f.getDocumentProperty().eq(document) )
			.map( c -> CustomerDAO.getFull(ctx, c.getId()) )
			.findFirst()
			.orElseThrow(() -> new AonCoreException("No encuenrto el cliente con documento " + document));
	}
}
