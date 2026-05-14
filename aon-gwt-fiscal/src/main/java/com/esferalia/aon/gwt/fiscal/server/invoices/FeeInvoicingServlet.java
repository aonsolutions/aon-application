package com.esferalia.aon.gwt.fiscal.server.invoices;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessage;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessageType;
import com.esferalia.aon.occam.api.model.console.ConsoleMessage;
import com.esferalia.aon.occam.api.model.console.ConsoleMessageType;
import com.esferalia.aon.occam.api.model.finance.FeeBillingParams;
import com.esferalia.aon.occam.api.model.finance.InvoiceProcessOutput;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.Month;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleMessageUtils;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleMessageUtils.PrintStreamConsoleLogger;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.invoice.communication.InvoiceCommunicator;

@WebServlet(name = "Fee Invoicing Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/feeInvoicing" })
public class FeeInvoicingServlet extends HttpServlet {

	private static final String MAIN_PROCESS = "FEE_SERVLET";
	private static final long serialVersionUID = -5703828624659508582L;
	private static final Logger LOGGER = Logger.getLogger(FeeInvoicingServlet.class.getName());
	 
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.log(Level.INFO, "FeeInvoicingServlet start!");
		try {
			resp.setContentType(MimeType.JSON.getName());
			PrintStream printStream = new PrintStream(resp.getOutputStream());
			PrintStreamConsoleLogger logger = new PrintStreamConsoleLogger( printStream );
			logger.title(MAIN_PROCESS, "Facturación de cuotas");
			try {
				String feeBillingParams = req.getParameter(IRequestParamsNames.FEE_BILLING_PARAMS);
				String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);;
				int domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
				String user = req.getParameter(IRequestParamsNames.USER);
				Occam occam = new Occam()
						.setDomainName(domainName)
						.setDomain(domainId)
						.setUser(user);
				FeeBillingParams params = parseParams(feeBillingParams);
				InvoiceProcessOutput output = InvoiceCommunicator.feeInvoicing(occam, params, logger);
				ConsoleMessage msg = new ConsoleMessage()
					.setProcessId("END")
					.setType(ConsoleMessageType.CONSOLE_MESSAGE)
					.setMessage("Informaci\00F3n de la facturaci\00F3n de facturas.")
					.setConsoleDomainMessage(
						new ConsoleDomainMessage()
						.setType(ConsoleDomainMessageType.INVOICE_PROCESS_OUTPUT)	
						.setDomainId( params.getDomainId()  )
						.setMessage( getOutputJSON(output) )
					);
				ConsoleMessageUtils.print( printStream, msg );
			} catch (Exception e) {
				logger.error(MAIN_PROCESS, e.getMessage());
				throw e;
			}
			resp.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			} catch (IOException ioException) {
				LOGGER.severe( "START --> CAUSA ORIGINAL" );
				e.printStackTrace();
				LOGGER.severe( "END --> CAUSA ORIGINAL" );
				LOGGER.severe( "START --> EXCEPCION AL ENVIAR EL ERROR A RESPONSE" );
				ioException.printStackTrace();
				LOGGER.severe( "END --> EXCEPCION AL ENVIAR EL ERROR A RESPONSE" );
			}
		}
	}
	
	private String getOutputJSON(InvoiceProcessOutput output) {
		JSONObject json = new JSONObject();
		json.put(IJsonNames.LEVEL, output.getProcessErrorLevel().map( Enum::name ).orElse(""));
		json.put(IJsonNames.MESSAGE, AonStringUtils.defaultIfBlank(output.getProcessMessage()));
		json.put(IJsonNames.FROM_ID, output.getFromId());
		json.put(IJsonNames.TO_ID, output.getToId());
		json.put(IJsonNames.INVOICE_COUNT, output.getInvoicesInfo().getTotalCount());
		json.put(IJsonNames.INVOICE_TOTAL_AMOUNT, output.getInvoicesInfo().getTotalAmount());
		json.put(IJsonNames.INVOICE_TOTAL_VAT, output.getInvoicesInfo().getTotalVAT());
		json.put(IJsonNames.INVOICE_TOTAL_RETENTION, output.getInvoicesInfo().getTotalRetention());
		json.put(IJsonNames.INVOICE_TOTAL_PREPAYMENT_COUNT, output.getInvoicesInfo().getTotalPrepaymentCount());
		json.put(IJsonNames.INVOICE_PROFORMA_COUNT, output.getProformasInfo().getTotalCount());
		json.put(IJsonNames.INVOICE_PROFORMA_TOTAL_AMOUNT, output.getProformasInfo().getTotalAmount());
		json.put(IJsonNames.INVOICE_PROFORMA_TOTAL_VAT, output.getProformasInfo().getTotalVAT());
		json.put(IJsonNames.INVOICE_PROFORMA_TOTAL_RETENTION, output.getProformasInfo().getTotalRetention());
		json.put(IJsonNames.INVOICE_PROFORMA_TOTAL_PREPAYMENT_COUNT, output.getProformasInfo().getTotalPrepaymentCount());
		return json.toString();
	}

	private static class InvalidArgumentException extends Exception {
	    private static final long serialVersionUID = 7594219560785986658L;

		public InvalidArgumentException(String message) {
	        super(message);
	    }
	}

	private static FeeBillingParams parseParams(String feeBillingParams) throws InvalidArgumentException {
		FeeBillingParams params = new FeeBillingParams();
		JSONObject jsonParams =  new JSONObject(feeBillingParams);
		
		// ******************* DOMAIN ******************* 
		Integer dom = JsonUtils.getInteger(jsonParams, IRequestParamsNames.DOMAIN_ID);
		if (dom == null) throw new InvalidArgumentException("El identificador de dominio es un dato requerido");
		params.setDomainId(dom);
		
		// ******************* notDryRun ******************* 
		Boolean dryRun = JsonUtils.getBooleanNumber(jsonParams, IRequestParamsNames.DRY_RUN);
		if (dryRun != null) {
			params.setDryRun(dryRun.booleanValue());
		}
		
		// ******************* saveAsProforma *******************
		Boolean proforma = JsonUtils.getBooleanNumber(jsonParams, IRequestParamsNames.SAVE_AS_PROFORMA);
		if (proforma != null) {
			params.setSaveAsProforma(proforma.booleanValue());
		}
		
		// ******************* communicable *******************
		Boolean communicable = JsonUtils.getBooleanNumber(jsonParams, IRequestParamsNames.COMMUNICABLE);
		if (communicable != null) {
			params.setCommunicable(communicable.booleanValue());
		}
		
		params.setCertId( JsonUtils.getInteger(jsonParams, IRequestParamsNames.CERTIFICATE_ID) );
		params.setInvoicingGroup( JsonUtils.getInteger(jsonParams, IRequestParamsNames.INVOICING_GROUP) );
		params.setCustomer( JsonUtils.getInteger(jsonParams, IRequestParamsNames.CUSTOMER) );
		params.setItem( JsonUtils.getInteger(jsonParams, IRequestParamsNames.ITEM) );
		params.setProductCategory( JsonUtils.getInteger(jsonParams, IRequestParamsNames.PRODUCT_CATEGORY) );
		Month.safeValueOf(JsonUtils.getInteger(jsonParams, IRequestParamsNames.MONTH))
			.ifPresent( month -> params.setMonth(month) );
		params.setYear( JsonUtils.getInteger(jsonParams, IRequestParamsNames.YEAR) ) ;
		Optional.ofNullable( SecurityLevel.safeValueOf( JsonUtils.getInteger(jsonParams, IRequestParamsNames.SECURITY_LEVEL) ))
			.ifPresent( level -> params.setSecurityLevel(level) );
		BillingPeriod.safeValueOf( JsonUtils.getInteger(jsonParams, IRequestParamsNames.BILLING_PERIOD) )
			.ifPresent( period -> params.setPeriod(period) );
		
//		private Integer workplace;
//		private Integer[] scopes;
//		private Integer[] segments;

		params.setInvoiceActivity( JsonUtils.getInteger(jsonParams, IRequestParamsNames.INVOICE_ACTIVITY) );
		params.setInvoiceSeries( JsonUtils.getString(jsonParams, IRequestParamsNames.INVOICE_SERIES ) );
		params.setInvoiceDate( JsonUtils.getDate(jsonParams, IRequestParamsNames.INVOICE_DATE) );
		params.setInvoiceComments( JsonUtils.getString(jsonParams, IRequestParamsNames.INVOICE_COMMENTS ) );

		
		return params;
	}
}
