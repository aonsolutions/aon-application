package net.aonsolutions.aon.verifactu;

import static com.esferalia.aon.jooq.tables.Series.SERIES;

import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Series;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SeriesDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import uk.co.jemos.podam.api.PodamUtils;

class VerifactuTestsUtils {

	static final String DATE_FORMAT = "dd-MM-yyyy";
	
	private VerifactuTestsUtils() {
		
	}

	static String series(AONContext ctx) {
		return series(ctx, false);
	}
	static String rectSeries(AONContext ctx) {
		return series(ctx, true);
	}
	static String series(AONContext ctx, boolean rectifier) {
		return series( ctx, rectifier, false);
	}
	static String series(AONContext ctx, boolean rectifier, boolean cancel) {
	    char prefix = switch ((cancel ? 2 : 0) | (rectifier ? 1 : 0)) {
	        case 3 -> 'T'; // cancel + rectifier
	        case 2 -> 'C'; // cancel + !rectifier
	        case 1 -> 'R'; // !cancel + rectifier
	        case 0 -> 'A'; // !cancel + !rectifier
	        default -> 'X';
	    };
	    prefix = onGitHubActions() ? Character.toLowerCase(prefix) : prefix; 
	    return series(ctx, rectifier, prefix);
	}
	
	static boolean onGitHubActions(){
		// Check if the environment variable GITHUB_ACTIONS is set to true
		String githubActions = System.getenv("GITHUB_ACTIONS");
		return githubActions != null && githubActions.equalsIgnoreCase("true");
	}
	
	private static String series(AONContext ctx, boolean rectifier, char prefix) {
		Series series = SeriesDAO.stream(ctx, ctx.getDomainId())
			.filter( Series::isActive )
			.filter( s -> AonStringUtils.startsWith(s.getCode(), Character.toString(prefix)))
			.filter( s -> (!rectifier && !s.isRectification() && s.isInvoice()) 
					   || (rectifier && s.isRectification() && !s.isInvoice()))
			.findFirst()
			.orElseGet(() -> {
				Integer[] scopes = SecurityDAO.getUserScopes(ctx);
				String code = getUniqueSeries(prefix);
				Integer seriesId = ctx.getDslContext().insertInto(SERIES)
					.set(SERIES.DOMAIN,ctx.getDomainId())
					.set(SERIES.SCOPE,scopes[0])
					.set(SERIES.CODE,code)
					.set(SERIES.DESCRIPTION,code)
					.set(SERIES.ACTIVE,(byte) 1)
					.set(SERIES.TAS,(byte)0)
					.set(SERIES.OFFER,(byte)0)
					.set(SERIES.SALES,(byte)0)
					.set(SERIES.DELIVERY,(byte)0)
					.set(SERIES.INVOICE,(byte) (rectifier?0:1))
					.set(SERIES.RECTIFICATION,(byte) (rectifier?1:0))
					.set(SERIES.POS,(byte) 0)
					.set(SERIES.SECURITY_LEVEL,(byte) 0)
					.returning(SERIES.ID)
					.fetchOne()
					.getValue(SERIES.ID)
				;
				return SeriesDAO.stream(ctx, ctx.getDomainId())
					.filter( se -> AonNumberUtils.equals(seriesId, se.getId()))
					.findFirst()
					.orElseThrow( () -> new AonCoreException("Serie no grabada"));
			})
		;
		return series.getCode();
	}
	private static String getUniqueSeries(char prefix) {
		Date issueDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(issueDate);
        int secondOfDay = 
        	  cal.get(Calendar.HOUR_OF_DAY) * 3600
    		+ cal.get(Calendar.MINUTE) * 60
    		+ cal.get(Calendar.SECOND);
        String coded = Integer.toString(secondOfDay, 36).toUpperCase();
        return prefix + String.format("%4s", coded).replace(' ', '0');
	}
	
	public static int number() {
		// Milisegundos desde el inicio del día
		LocalTime now = LocalTime.now();
		return (int) (now.toSecondOfDay() * 1000L + now.getNano() / 1_000_000);
	}
	
	public static String referenceCode(Invoice invoice) {
		return referenceCode(invoice.getSeries(),invoice.getNumber());
	}
	static String referenceCode(String series, int number) {
		String referenceCode = AonStringUtils.leftPad(Integer.toString(number), 6, "0");
		if (!AonStringUtils.isBlank(series)) {
			referenceCode = series + "/" + referenceCode;
		}
		return referenceCode;
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
