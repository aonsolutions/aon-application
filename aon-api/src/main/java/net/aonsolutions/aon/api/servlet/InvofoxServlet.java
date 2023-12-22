package net.aonsolutions.aon.api.servlet;
import java.math.BigDecimal;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.tedi.invofox.OCRBlankValueException;
import net.aonsolutions.aon.tedi.invofox.OCRInvalidValueException;
import net.aonsolutions.aon.tedi.invofox.OCRInvoiceBuilder;
import net.aonsolutions.aon.tedi.invofox.OCROwnerNotFoundException;
import net.aonsolutions.aon.tedi.invofox.OCRTooManyOwnersException;
import net.aonsolutions.aon.tedi.invofox.OCRUndefinedTypeException;
import net.aonsolutions.aon.tedi.invofox.OCRZeroValueException;
import net.aonsolutions.invofox.OCRCompanyParams;
import net.aonsolutions.invofox.OCRDocumentsParams;
import net.aonsolutions.invofox.OCRInvofox;
import net.aonsolutions.invofox.model.OCRAddress;
import net.aonsolutions.invofox.model.OCRCompaniesResponse;
import net.aonsolutions.invofox.model.OCRCompany;
import net.aonsolutions.invofox.model.OCRDocument;
import net.aonsolutions.invofox.model.OCRDocumentResponse;
import net.aonsolutions.invofox.model.OCRDocumentsResponse;
import net.aonsolutions.invofox.model.OCRInvoice;
import net.aonsolutions.invofox.model.OCRLoginToken;
import net.aonsolutions.invofox.model.OCRType;
import solutions.aon.aws.s3.S3;

@SuppressWarnings("serial")
@WebServlet(name = "InvofoxServlet", urlPatterns = {"/ms/api/invofox/*"})
public class InvofoxServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(InvofoxServlet.class.getName());
	
	public static final String DOCUMENTS = "/";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON INVOFOX SERVLET GET");
		try {		
			AonApiData api = initialize(req);
			switch (api.getPath()) {
				case "/":
				    	response(req, resp, getDocuments(api));
					break;
				case "/document":
				    	response(req, resp, getDocument(api));
					break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
	}
	
	private static JSONObject getDocument(AonApiData api) {
	    JSONObject params = api.getData();
	    String documentId    = params.optString(IJsonNames.ID);
	    AONContext aonContext = AONContext.getAONContext(api.getDomain().getName(), api.getUser().getLogin());
	    OCRDocumentResponse response = OCRInvofox.getDocument(documentId);
	    OCRDocument ocrDocument = response.getDocument().orElseThrow(RuntimeException::new);
	    OCRInvoice ocrInvoice = ocrDocument.getData().orElseThrow(RuntimeException::new);
	    String token = OCRInvofox.getLoginToken().getLoginToken().orElse(new OCRLoginToken()).getToken().orElse(null);
	    return  response.getDocument()
		    .map(InvofoxServlet::toInvoice)
		    .map(invoice -> fillRegistry(aonContext, ocrInvoice, invoice))
		    .map(InvoiceJSON::toJSON)
		    .map(invoice -> invoice.put("token", token))
		    .map(invoice -> invoice.put("file", getFileJSON(ocrDocument)) )
		    .map(invoice -> invoice.put("remarks", getRemarks(ocrDocument)))
		    .map(invoice -> invoice.put("status", "inbox"))
		    .orElseThrow(() -> new AonApiException("No such document"));
	}

	private static JSONArray getDocuments(AonApiData api) {
	    JSONArray array = new JSONArray();
	    Integer page = JsonUtils.getInteger(api.getData(), IJsonNames.PAGE);
	    Integer perPage = JsonUtils.getInteger(api.getData(), IJsonNames.PER_PAGE);
	    String token = OCRInvofox.getLoginToken().getLoginToken().orElse(new OCRLoginToken()).getToken()
		    .orElse(null);
	    Company cp = AON.getCompany(api.getDomain(), api.getUser(),
		    f -> f.getDomainProperty().eq(api.getDomain().getId()));
	    if (!AonStringUtils.isBlank(cp.getDocument())) {
		OCRCompaniesResponse companiesResponse = OCRInvofox
			.getCompanies(OCRCompanyParams.get().withTaxId(cp.getDocument()));
		List<OCRCompany> companies = companiesResponse.getCompanies().orElse(new LinkedList<>());
		if (!companies.isEmpty()) {
		    OCRCompany company = companies.get(0);
		    OCRDocumentsResponse response = OCRInvofox
			    .getDocuments(OCRDocumentsParams.get().withType(OCRType.invoice)
				    // .withPublicState(OCRSeverity.pendingCorrection)
				    .withCompany(company.getId()).skiping(page * perPage).limit(perPage));

		    response.getDocuments().orElse(new LinkedList<>()).stream().forEach(r -> {
			JSONObject json = new JSONObject();
			json.put("id", r.getId().get());
			json.put("reference", r.getData().get().getDocumentNumber().get().getValue().orElse(""));
			json.put("name", r.getData().get().getIssuerName().get().getValue().orElse(""));
			json.put("date", r.getData().get().getIssueDate().get().getValue().orElse(""));
			json.put("total",
				r.getData().get().getTotalAmount().get().getValue().orElse(new BigDecimal(0)));
			json.put("token", token);
			json.put("status", r.getPublicState().get().name());
			array.put(json);
		    });
		}
	    }
	    return array;
	}
	
	
	
//	INVOICE_DOMAIN
//	.andThen(INVOICE_TRANSACTION)
//	.andThen(INVOICE_ISSUE_DATE)
//	.andThen(INVOICE_TYPE)
//	.andThen(INVOICE_REGISTRY_DOCUMENT)
//	.andThen(INVOICE_REGISTRY)
//	.andThen(INVOICE_REFERENCE_CODE)
//	.andThen(INVOICE_BREAKDOWN)
//	.andThen(INVOICE_WITHOLDING)
//	.andThen(INVOICE_DETAILS)
//	.andThen(INVOICE_TOTAL)
//	.andThen(INVOICE_TAXABLE_BASE)
//	.andThen(INVOICE_VAT_QUOTA)
//	.andThen(INVOICE_RETENTION_QUOTA)
//	.andThen(INVOICE_FINANCES)

	private static Invoice toInvoice ( OCRDocument ocrDocument ) {
	    Invoice invoice = new Invoice();
	    
	    OCRInvoice ocrInvoice = ocrDocument.getData().orElseThrow(RuntimeException::new);
	    
	    OCRInvoiceBuilder.fillTransaction(ocrInvoice, invoice);
	    try {
		OCRInvoiceBuilder.fillIssueDate(ocrInvoice, invoice);
	    } catch (OCRInvalidValueException e) {
	    }
	    OCRInvoiceBuilder.fillTtype(ocrDocument, ocrInvoice, null /*defautl EXPENSES*/, invoice);
	    try {
		OCRInvoiceBuilder.fillRegistryDocument(ocrInvoice, invoice);
	    } catch (OCRUndefinedTypeException e) {
	    }	    
	    try {
		OCRInvoiceBuilder.fillReferenceCode(ocrInvoice, invoice);
	    } catch (OCRZeroValueException | OCRBlankValueException e) {
	    }
	    
	    OCRInvoiceBuilder.fillBreakdown(ocrInvoice, invoice);
	    OCRInvoiceBuilder.fillWithHolding(ocrInvoice, invoice);
	    OCRInvoiceBuilder.fillDetails(ocrInvoice, invoice);

	    OCRInvoiceBuilder.fillTotal(ocrInvoice, invoice);
	    OCRInvoiceBuilder.fillTaxableBase(ocrInvoice, invoice);
	    OCRInvoiceBuilder.fillVatQuota(ocrInvoice, invoice);
	    OCRInvoiceBuilder.fillRetentionQuota(ocrInvoice, invoice);
	    
	    return invoice;
	    
	}

        private static final JSONObject getFileJSON(OCRDocument ocrDocument) {
            JSONObject jsonObject = new JSONObject();
            ocrDocument.getClientData()
            .ifPresent( clientData ->  
            			clientData.getS3Object().ifPresent( 
            				s3Object ->  
            				s3Object.getBucket().ifPresent( 
            						bucketName ->  
                					    s3Object.getKey().ifPresent(key ->  {
                							URL url = S3.getURL(bucketName, key);
                							jsonObject.put("url",url.toExternalForm());
                							jsonObject.put("path",url.toExternalForm());
                							String contentType = S3.getContentType(bucketName, key);
                							jsonObject.put("content_type", contentType);
                					    	}
                					    )   
                					)
                			    )
                	);
            return jsonObject;
        }
	
	private static final Invoice fillRegistry (AONContext ctx , OCRInvoice ocrInvoice, Invoice invoice ) {
	    try {
		OCRInvoiceBuilder.fillRegistry(ctx, invoice);
	    } catch (OCRTooManyOwnersException e) {
	    } catch ( OCROwnerNotFoundException e ) {
		Registry registry = new Registry( )
		.setDocument(invoice.getRegistryDocument())
		.setDocumentType(invoice.getRegistryDocumentType())
		.setDocumentCountry(invoice.getRegistryDocumentCountry());
		invoice.getType().visit(invoice, new IInvoiceTypeVisitor() {
		    
		    @Override
		    public void visitUndeductible(Invoice invoice) {
			visitPurchase(invoice);
		    }
		    
		    @Override
		    public void visitSales(Invoice invoice) {
			ocrInvoice.getRecipientName().ifPresent(name -> name.getValue().ifPresent(registry::setName));
			ocrInvoice.getRecipientCountry().ifPresent(country -> country.getValue()
				.map(Country::safeValueOf).ifPresent(registry::setNationality));
			ocrInvoice.getRecipientAddressDetails().ifPresentOrElse(details -> {
			    RegistryAddress registryAddress = toRegistryAddress(details);
			    invoice.setAddress(registryAddress);
			}, () -> {
			});
			
		    }
		    
		    @Override
		    public void visitPurchase(Invoice invoice) {
			ocrInvoice.getIssuerName().ifPresent(name -> name.getValue().ifPresent(registry::setName));
			ocrInvoice.getIssuerCountry().ifPresent(country -> country.getValue().map(Country::safeValueOf)
				.ifPresent(registry::setNationality));
			ocrInvoice.getIssuerAddressDetails().ifPresentOrElse(details -> {
			    RegistryAddress registryAddress = toRegistryAddress(details);
			    invoice.setAddress(registryAddress);
			}, () -> {

			});
			
		    }

		    
		    @Override
		    public void visitExpenses(Invoice invoice) {
			visitPurchase(invoice);
		    }

		    private RegistryAddress toRegistryAddress(OCRAddress details) {
			RegistryAddress registryAddress= new RegistryAddress();
			
			details.getPostalCode().ifPresent(registryAddress::setZip);
			details.getMunicipality().ifPresent(registryAddress::setCity);
			details.getCountry().map(Country::safeValueOf).ifPresent(registryAddress::setCountry);
			details.getStreet().ifPresent(registryAddress::setAddress);
			details.getAddressNumber().ifPresent(registryAddress::setAddress2);
			details.getNeighborhood().ifPresent(registryAddress::setAddress3);
			details.getRegion().ifPresent(registryAddress::setProvince);
			
			return registryAddress;
		    }
		});
		invoice
//		.setRegistry(ar.getId())
//		.setTransaction(ar.getTransaction())
		.setRegistryData( registry);
		
	    }
	    return invoice;
	}
	
        private static final JSONArray getRemarks(OCRDocument ocrDocument) {
            List<JSONObject> remarks = new LinkedList<>();
            ocrDocument.getValidationInfo().ifPresent(validationInfo -> 
        	validationInfo.getErrors().ifPresent(errors -> 
        	    errors.forEach( error -> 
        		error.getDescription().ifPresent(description -> remarks.add(new JSONObject().put("reason", description)))
        	    )
        	)
            );
            return new JSONArray(remarks);
        }
        
        
	
}
