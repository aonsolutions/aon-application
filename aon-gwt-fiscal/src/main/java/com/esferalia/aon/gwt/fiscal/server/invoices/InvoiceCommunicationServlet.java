package com.esferalia.aon.gwt.fiscal.server.invoices;

import java.io.IOException;
import java.io.PrintStream;
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
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams;
import com.esferalia.aon.occam.api.model.finance.InvoiceConsoleParams.OrderBy;
import com.esferalia.aon.occam.api.model.finance.InvoiceProcessOutput;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.type.InvoiceSource;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
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

@WebServlet(name = "Invoice Communication Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/invoiceCommunication" })
public class InvoiceCommunicationServlet extends HttpServlet {

	private static final long serialVersionUID = -5703828624659508582L;
	private static final Logger LOGGER = Logger.getLogger(InvoiceCommunicationServlet.class.getName());
	
	private static final String MAIN_PROCESS = "COMMUNICATION_SERVLET";
	private static final String DATE_PATTERN = "dd/MM/yyyy";
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.log(Level.INFO, "InvoiceCommunicationServlet start!");
		try {
			resp.setContentType(MimeType.JSON.getName());
			PrintStream printStream = new PrintStream(resp.getOutputStream());
			PrintStreamConsoleLogger logger = new PrintStreamConsoleLogger( printStream );
			logger.title(MAIN_PROCESS, "Comunicación de facturas");
			try {
				String invoiceConsoleParams = req.getParameter(IRequestParamsNames.INVOICE_PARAMS);
				String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);;
				int domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
				String user = req.getParameter(IRequestParamsNames.USER);
				Occam occam = new Occam()
						.setDomainName(domainName)
						.setDomain(domainId)
						.setUser(user);
				InvoiceConsoleParams params = parseParams(invoiceConsoleParams);
				InvoiceProcessOutput output = InvoiceCommunicator.issue(occam, params, logger);
				
				ConsoleMessage msg = new ConsoleMessage()
					.setProcessId("END")
					.setType(ConsoleMessageType.CONSOLE_MESSAGE)
					.setMessage("Informaci\00F3n de la facturaci\00F3n de facturas.")
					.setConsoleDomainMessage(
						new ConsoleDomainMessage()
						.setType(ConsoleDomainMessageType.INVOICE_PROCESS_OUTPUT)	
						.setDomainId( params.getDomain()  )
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

	private static InvoiceConsoleParams parseParams(String invoiceConsoleParams) throws InvalidArgumentException {
		JSONObject jsonParams =  new JSONObject(invoiceConsoleParams);
		Integer dom = JsonUtils.getInteger(jsonParams, IRequestParamsNames.DOMAIN_ID);
		if (dom == null) throw new InvalidArgumentException("El identificador de dominio es un dato requerido");
		
		return new InvoiceConsoleParams()
			.setDomain(dom)
			.setId( JsonUtils.getInteger(jsonParams, IRequestParamsNames.ID) )
			.setIds( JsonUtils.getIntegerArray(jsonParams, IRequestParamsNames.IDS) )
			.setFromId( JsonUtils.getInteger(jsonParams, IRequestParamsNames.FROM_ID) )
			.setToId( JsonUtils.getInteger(jsonParams, IRequestParamsNames.TO_ID) )
			.setFromDate( JsonUtils.getDateFormat(jsonParams, IRequestParamsNames.FROM_DATE, DATE_PATTERN ))
			.setToDate( JsonUtils.getDateFormat(jsonParams, IRequestParamsNames.TO_DATE, DATE_PATTERN ))
			.setActivity( JsonUtils.getInteger(jsonParams, IRequestParamsNames.ACTIVITY) )
			.setSeries( JsonUtils.getString(jsonParams, IRequestParamsNames.SERIES) )
			.setFromNumber( JsonUtils.getInteger(jsonParams, IRequestParamsNames.FROM_NUMBER) )
			.setToNumber( JsonUtils.getInteger(jsonParams, IRequestParamsNames.TO_NUMBER) )
			.setReferenceCode(JsonUtils.getString(jsonParams, IRequestParamsNames.REFERENCE_CODE) )
			.setRegistry( JsonUtils.getInteger(jsonParams, IRequestParamsNames.REGISTRY) )
			.setOutput( JsonUtils.getBooleanNumber(jsonParams, IRequestParamsNames.OUTPUT) )
			.setAnnulled(JsonUtils.getBooleanNumber(jsonParams, IRequestParamsNames.ANNULLED) )
			.setTransactionType(JsonUtils.getEnumFromOrdinal(jsonParams, IRequestParamsNames.TRANSACTION_TYPE, InvoiceTransactionType.class ) )
			.setRectificationType(JsonUtils.getEnumFromOrdinal(jsonParams, IRequestParamsNames.RECTIFICATION_TYPE, RectificationType.class ) )
			.setSurcharge(JsonUtils.getBooleanNumber(jsonParams, IRequestParamsNames.SURCHARGE) )
			.setFarmerRegime( JsonUtils.getBooleanNumber(jsonParams, IRequestParamsNames.FARMER_REGIME) )
			.setAccrualRegime( JsonUtils.getBooleanNumber(jsonParams, IRequestParamsNames.ACCRUAL_REGIME) )
			.setInvestment( JsonUtils.getBooleanNumber(jsonParams, IRequestParamsNames.INVESTMENT) )
			.setWithholding( JsonUtils.getBooleanNumber(jsonParams, IRequestParamsNames.WITHHOLDING) )
			.setService( JsonUtils.getBooleanNumber(jsonParams, IRequestParamsNames.SERVICE) )
			.setRecorded( JsonUtils.getBooleanNumber(jsonParams, IRequestParamsNames.RECORDED) )
			.setProforma( JsonUtils.getBooleanNumber(jsonParams, IRequestParamsNames.PROFORMA) )
			.setSource( JsonUtils.getEnumFromOrdinal(jsonParams, IRequestParamsNames.INVOICE_SOURCE, InvoiceSource.class ) )
			.setSecurityLevel( JsonUtils.getEnumFromOrdinal(jsonParams, IRequestParamsNames.SECURITY_LEVEL, SecurityLevel.class ) )
			.setCommunicationType( JsonUtils.getEnumFromOrdinal(jsonParams, IRequestParamsNames.COMMUNICATION_TYPE, InvoiceCommunicationType.class ) )
			.setCommunicationStatus( JsonUtils.getEnumFromOrdinal(jsonParams, IRequestParamsNames.COMMUNICATION_STATUS, InvoiceCommunicationStatus.class ) )
			.setOrderBy( JsonUtils.getEnumFromOrdinal(jsonParams, IRequestParamsNames.ORDER_BY, OrderBy.class ) )
			.setDescending( JsonUtils.getBooleanNumber(jsonParams, IRequestParamsNames.DESCENDING) )
			.setOffset( JsonUtils.getInteger(jsonParams, IRequestParamsNames.OFFSET) )
			.setLimit( JsonUtils.getInteger(jsonParams, IRequestParamsNames.LIMIT) )
			.setCertId( JsonUtils.getInteger(jsonParams, IRequestParamsNames.CERTIFICATE_ID) )
		;
		
	}
}
