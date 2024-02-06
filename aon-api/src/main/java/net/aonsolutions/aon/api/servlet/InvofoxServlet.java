package net.aonsolutions.aon.api.servlet;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.TediErrorJSON;
import com.esferalia.aon.occam.api.json.invoice.InvofoxConfigurationJSON;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.InvofoxConfiguration;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.tedi.TediContext;
import com.esferalia.aon.occam.api.model.tedi.TediContextKey;
import com.esferalia.aon.occam.api.model.tedi.TediError;
import com.esferalia.aon.occam.api.model.tedi.TediLevel;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.dao.InvofoxConfigurationDAO;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import net.aonsolutions.invofox.json.OCRNames;
import net.aonsolutions.invofox.model.OCRAddress;
import net.aonsolutions.invofox.model.OCRCompaniesResponse;
import net.aonsolutions.invofox.model.OCRCompany;
import net.aonsolutions.invofox.model.OCRDocument;
import net.aonsolutions.invofox.model.OCRDocumentResponse;
import net.aonsolutions.invofox.model.OCRDocumentsResponse;
import net.aonsolutions.invofox.model.OCRError;
import net.aonsolutions.invofox.model.OCRField;
import net.aonsolutions.invofox.model.OCRInfoResponse;
import net.aonsolutions.invofox.model.OCRInvoice;
import net.aonsolutions.invofox.model.OCRLine;
import net.aonsolutions.invofox.model.OCRLoginToken;
import net.aonsolutions.invofox.model.OCRPage;
import net.aonsolutions.invofox.model.OCRSeverity;
import net.aonsolutions.invofox.model.OCRWord;
import solutions.aon.aws.s3.S3;

@SuppressWarnings("serial")
@WebServlet(name = "InvofoxServlet", urlPatterns = {"/ms/api/invofox/*"})
public class InvofoxServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(InvofoxServlet.class.getName());
	
	public static final String DOCUMENTS = "/";
	public static final String DOCUMENT = "/document";
	public static final String TEXT_CONTENT= "/text_content";
	public static final String CONFIGURATION= "/configuration";
	
	@Override	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		put(req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		put(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(DOCUMENTS, InvofoxServlet::getDocuments)
				.addRoute(DOCUMENT, InvofoxServlet::getDocument)
				.addRoute(TEXT_CONTENT, InvofoxServlet::getTextContent)
				.addRoute(CONFIGURATION, InvofoxServlet::getConfiguration)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void put(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(CONFIGURATION, InvofoxServlet::saveConfiguration)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONObject getDocument(AonApiData api) {
	    JSONObject params = api.getData();
	    String documentId    = params.optString(IJsonNames.ID);
	    AONContext aonContext = AONContext.getAONContext(api.getDomain().getName(), api.getUser().getLogin());
	    InvofoxConfiguration invofoxConfiguration = InvofoxConfigurationDAO.get(aonContext);
	    OCRDocumentResponse response = OCRInvofox.getDocument(invofoxConfiguration.getApiKey(), invofoxConfiguration.getApiUrl(),  documentId);
	    OCRDocument ocrDocument = response.getDocument().orElseThrow(RuntimeException::new);
	    OCRInvoice ocrInvoice = ocrDocument.getData().orElseThrow(RuntimeException::new);
	    String token = OCRInvofox.getLoginToken(invofoxConfiguration.getApiKey(), invofoxConfiguration.getApiUrl()).getLoginToken().orElse(new OCRLoginToken()).getToken().orElse(null);
	    return  response.getDocument()
		    .map(InvofoxServlet::toInvoice)
		    .map(invoice -> fillRegistry(aonContext, ocrInvoice, invoice))
		    .map(invoice -> fillFinances(aonContext, ocrInvoice, invoice))
		    .map(InvoiceJSON::toJSON)
		    .map(invoice -> invoice.put("token", token))
		    .map(invoice -> invoice.put("file", getFileJSON(ocrDocument)) )
		    .map(invoice -> invoice.put("messages", getMessages(ocrDocument)))
		    .map(invoice -> invoice.put("status", toString(ocrDocument.getPublicState().orElse(OCRSeverity.error))))
		    .map(invoice -> invoice.put("insight", new JSONObject().put( "invofoxId", ocrDocument.getId().orElse("") )))
		    .orElseThrow(() -> new AonApiException("No such document"));
	}

	private static JSONObject getTextContent(AonApiData api) {
	    JSONObject params = api.getData();
	    String documentId    = params.optString(IJsonNames.ID);
	    AONContext aonContext = AONContext.getAONContext(api.getDomain().getName(), api.getUser().getLogin());
	    InvofoxConfiguration invofoxConfiguration = InvofoxConfigurationDAO.get(aonContext);
	    OCRInfoResponse ocrInfoResponse = OCRInvofox.getOcrInfo(invofoxConfiguration.getApiKey(), invofoxConfiguration.getApiUrl(),documentId);
	    
	    Stream<OCRPage> ocrPages =Arrays.stream(ocrInfoResponse.getPages().orElse(new OCRPage[0]));
	    
	    List<JSONObject> pages = 
	    ocrPages.map(page -> new JSONObject()
		    .put("page", page.getPage().orElse(0)) 
		    .put("angle", page.getAngle().orElse(BigDecimal.ZERO)) 
		    .put("width", page.getWidth().orElse(BigDecimal.ZERO)) 
		    .put("height", page.getHeight().orElse(BigDecimal.ZERO)) 
		    .put("items" , getTextItems(page) )
		    ).toList();
	    return new JSONObject().put("pages", pages);
	    
	}

	private static JSONArray getDocuments(AonApiData api) {
	    JSONArray array = new JSONArray();
	    Integer page = JsonUtils.getInteger(api.getData(), IJsonNames.PAGE);
	    Integer perPage = JsonUtils.getInteger(api.getData(), IJsonNames.PER_PAGE);
	    
	    JSONArray publicStates = JsonUtils.getJSONArray(api.getData(), IJsonNames.PUBLIC_STATE);
	    if ( publicStates == null ) {
		publicStates = new JSONArray().put(JsonUtils.getString(api.getData(), IJsonNames.PUBLIC_STATE));
	    }
	    
	    AONContext aonContext = AONContext.getAONContext(api.getDomain().getName(), api.getUser().getLogin());
	    InvofoxConfiguration invofoxConfiguration = InvofoxConfigurationDAO.get(aonContext);
	    String token = OCRInvofox.getLoginToken(invofoxConfiguration.getApiKey(), invofoxConfiguration.getApiUrl()).getLoginToken().orElse(new OCRLoginToken()).getToken()
		    .orElse(null);
	    
	    Company cp = AON.getCompany(api.getDomain(), api.getUser(),
		    f -> f.getDomainProperty().eq(api.getDomain().getId()));
	    if (!AonStringUtils.isBlank(cp.getDocument())) {
		OCRCompaniesResponse companiesResponse = OCRInvofox
			.getCompanies(invofoxConfiguration.getApiKey(), invofoxConfiguration.getApiUrl(), OCRCompanyParams.get().withTaxId(cp.getDocument()));
		List<OCRCompany> companies = companiesResponse.getCompanies().orElse(new LinkedList<>());
		if (!companies.isEmpty()) {
		    OCRCompany company = companies.get(0);
		    OCRDocumentsParams ocrDocumentParams = OCRDocumentsParams.get();
		    //ocrDocumentParams.withType(OCRType.invoice);
		    //ocrDocumentParams.withType(OCRType.ticket);
		    ocrDocumentParams.sort(OCRNames.CREATION, OCRDocumentsParams.DESC); 
		    ocrDocumentParams.withCompany(company.getId()).skiping(page * perPage);
		    publicStates.forEach(publicState -> OCRSeverity.safeValueOf((String)publicState).ifPresent(ocrDocumentParams::withPublicState));
		    ocrDocumentParams.limit(perPage); 
		    
		    OCRDocumentsResponse response = OCRInvofox
			    .getDocuments(invofoxConfiguration.getApiKey(), invofoxConfiguration.getApiUrl(), ocrDocumentParams);

		    response.getDocuments().orElse(new LinkedList<>()).stream().forEach(r -> {
			JSONObject json = new JSONObject();
			json.put("id", r.getId().get());
			json.put("reference", r.getData().get().getDocumentNumber().get().getValue().orElse(""));
			json.put("name", r.getData().get().getIssuerName().get().getValue().orElse(""));
			json.put("date", r.getData().get().getIssueDate().get().getValue().orElse(""));
			json.put("total",
				r.getData().get().getTotalAmount().get().getValue().orElse(new BigDecimal(0)));
			json.put("token", token);
			json.put("status", toString(r.getPublicState().orElse(OCRSeverity.error)));
			array.put(json);
		    });
		}
	    }
	    return array;
	}
	
	private static JSONObject getConfiguration(AonApiData api) {
		return InvofoxConfigurationJSON.toJSON(AON.getInvofoxConfiguration(api.getDomain(), api.getUser()));
	}
	
	private static JSONObject saveConfiguration(AonApiData api) {
		InvofoxConfiguration invofoxConfiguration = InvofoxConfigurationJSON.fromJSON(api.getData());
		return InvofoxConfigurationJSON.toJSON(AON.saveInvofoxConfiguration(api.getDomain(), api.getUser(), invofoxConfiguration));
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
	    invoice.setType(InvoiceType.EXPENSES);
	    OCRInvoiceBuilder.fillTtype(ocrDocument, ocrInvoice, null /*default EXPENSES*/, invoice);
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
	
	private static final Invoice fillFinances (AONContext ctx , OCRInvoice ocrInvoice, Invoice invoice ) {
	    OCRInvoiceBuilder.fillFinances(ctx, ocrInvoice, invoice);
	    return invoice;
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
	
        
        private static final JSONArray getMessages(OCRDocument ocrDocument) {
            List<JSONObject> messages = new LinkedList<>();
            ocrDocument.getValidationInfo().ifPresent(validationInfo -> 
        	validationInfo.getErrors().ifPresent(errors -> 
        	    errors.forEach( ocrError -> getMessages(ocrError).forEach(message -> messages.add(TediErrorJSON.toJSON(message)))
        	    )
        	)
            );
            return new JSONArray(messages);
        }
        
        private static final Collection<TediError> getMessages(OCRError ocrError) {
            
            Collection<TediError> messages = 
    	    ocrError.getFields()
    	    .orElse(Collections.emptyList())
    	    .stream()
    	    .map(ocrField -> {
    		TediError tediError = new TediError();
		tediError.setLevel(getTediLevel(ocrError));
		ocrError.getCode().ifPresent(tediError::setCode);
		getTediContext(ocrField).ifPresent(tediError::setContext);
		ocrError.getDescription().ifPresent(tediError::setMessage);
		return tediError;
    	    })
    	    .collect(Collectors.toMap(TediError::getMessage, err -> err, ( err1, err2 ) -> err2 )).values();

            if ( !messages.isEmpty() ) {
		return messages;
	    }
	    
	    TediError tediError = new TediError();
	    tediError.setLevel(getTediLevel(ocrError));
	    ocrError.getCode().ifPresent(tediError::setCode);
	    ocrError.getDescription().ifPresent(tediError::setMessage);
	    return Collections.singletonList(tediError);
	    
        }
        
        private static final TediLevel getTediLevel(OCRError ocrError ) {
            OCRSeverity severity = ocrError.getSeverity().orElse(OCRSeverity.error);
            switch (severity) {
	    case approved:
	    case exported:
	    case processing:
		return TediLevel.INF;
	    case error:
	    case rejected:
	    case discarded:
		return TediLevel.ERR;
	    case pendingDecission:
	    case pendingCorrection:
		return TediLevel.WRN;
	    default:
		return TediLevel.ERR;
	    }
        }

        private static final Optional<TediContext> getTediContext(OCRField ocrField ) {
            TediContextKey tediContextKey =  getTediContextKey(ocrField);
            if ( tediContextKey == null ) { 
        	return Optional.empty();
            }
            
            TediContext tediContext = new TediContext();
            tediContext.setKey(tediContextKey);
            
            ocrField.getIndex().ifPresent(tediContext::setLine);
            
            return Optional.of(tediContext);
        }

        private static final TediContextKey getTediContextKey(OCRField ocrField ) {
            String fieldName = ocrField.getName().orElse("");
            switch (fieldName) {
	    case "documentNumber":
		return TediContextKey.REFERENCE_CODE;
	    case "issueDate":
		return TediContextKey.ISSUE_DATE;
	    case "issuerName":
		return TediContextKey.RNAME;
	    case "issuerTaxId":
		return TediContextKey.RDOCUMENT;
	    case "issuerCountry":
		return TediContextKey.RDOCUMENT_COUNTRY;
	    case "issuerAddress":
	    case "issuerAddressDetails":
		return TediContextKey.ADDRESS;
	    case "invoiceRef":
		return TediContextKey.REFERENCE_CODE;
	    case "seriesCode":
		return TediContextKey.SERIES;
	    case "taxRate":
		return TediContextKey.TAX_RATE;
	    case "taxAmount":
	    case "totalTaxAmount":
		return TediContextKey.TAX_QUOTA;
	    case "taxBaseAmount":
	    case "totalTaxBaseAmount":
		return TediContextKey.TAX_BASE;
	    case "totalAmount":
		return TediContextKey.TOTAL;
	    case "withholdingTaxAmount":
		return TediContextKey.IRPF_QUOTA;
	    case "withholdingTaxRate":
		return TediContextKey.IRPF_RATE;
	    case "paymentMethod":
		return TediContextKey.PAY_METHOD;

	    case "additionalChargesAmount":
	    case "additionalDiscountsAmount":
	    case "additionalNotes":
	    case "clientCode":
	    case "contractRef":
	    case "currency":
	    case "deliveryNoteRef":
	    case "documentType":
	    case "IBAN":
	    case "incoterms":
	    case "isCreditNote":
	    case "issuerEmail":
	    case "issuerPhoneNumber":
	    case "issuerWebsite":
	    case "language":
	    case "legalNotes":
	    case "meterNumber":
	    case "numberFormat":
	    case "orderRef":
	    case "recipientAddress":
	    case "recipientAddressDetails":
	    case "recipientCountry":
	    case "recipientEmail":
	    case "recipientName":
	    case "recipientPhoneNumber":
	    case "recipientTaxId":
	    case "recipientWebsite":
	    case "reimbursableExpensesAmount":
	    case "serviceAddress":
	    case "shippingAddress":
	    case "supplyNumber":
	    case "SWIFT":
	    case "taxClass":
	    case "totalDiscountAmount":
	    case "totalDueAmount":
	    case "totalFeesAmount":
	    case "totalGrossAmount":
	    case "totalUsage":
	    case "usageUnitOfMeasurement":
	    default:
		return null;
	    }
        }
        
	private static String toString(OCRSeverity ocrSeverity) {
	    return "ocr" 
		    + AonStringUtils.substring(ocrSeverity.name(),0, 1 ).toUpperCase()
		    + AonStringUtils.substring(ocrSeverity.name(), 1 );
	}
	
	private static List<JSONObject> getTextItems( OCRPage page ) {
	    Stream<OCRLine> lines = Arrays.stream(page.getLines().orElse(new OCRLine[0]));
	    Stream<OCRWord> words = lines.map(line -> line.getWords().orElse(new OCRWord[0])).flatMap(Arrays::stream);
	    
	    List<JSONObject> items =
		    words.map(word -> {
			JSONObject textItem =  new JSONObject();
			word.getText().ifPresent(text -> textItem.put("str", text));
			word.getBoundingBox().ifPresent(box -> {

			    BigDecimal leftTopX = box[0];
			    BigDecimal leftTopY = box[1];
			
			    BigDecimal rightTopX = box[2];
			    BigDecimal rightTopY = box[3];

			    BigDecimal rightBottomX = box[4];
			    BigDecimal rightBottomY = box[5];

			    BigDecimal leftBottomX = box[6];
			    BigDecimal leftBottomY = box[7];
			    
			    BigDecimal leftHeigth = leftBottomY.subtract(leftTopY);
			    BigDecimal rightHeigth = rightBottomY.subtract(rightTopY);
			    BigDecimal height = leftHeigth.max(rightHeigth);
			    
			    BigDecimal topWidth = rightTopX.subtract(leftTopX);
			    BigDecimal bottomWidth = rightBottomX.subtract(leftBottomX);
			    BigDecimal width = topWidth.max(bottomWidth);

			    textItem.put("width", width );
			    textItem.put("height", height );
			    
			    BigDecimal top = leftTopY.min(rightTopY);
			    textItem.put("top", top );
			    
			    BigDecimal left = leftTopX.min(leftBottomX); 
			    textItem.put("left", left );
			    
			});
			return textItem;
		    }).toList();
	    return items;
	}
}
