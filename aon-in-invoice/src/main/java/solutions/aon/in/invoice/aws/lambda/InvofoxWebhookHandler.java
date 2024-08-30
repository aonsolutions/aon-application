package solutions.aon.in.invoice.aws.lambda;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.esferalia.aon.occam.api.json.TaskWorkflowJSON;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.api.model.task.TaskWorkflowType;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.AonTask;
import net.aonsolutions.aon.api.AonInvofox;

public class InvofoxWebhookHandler implements RequestHandler<Object, String> {

    enum Type {
	
	ticket("Ticket"),
	invoice("Factura"),
	unknown("Desconocido"),
	deliveryNote("Albrán")
	;
	
	private String decription;
	
	private Type(String description) {
	    this.decription = description;
	}
	
	public String getDescription() {
	    return decription;
	}
    }
    
    enum State {
	unknown("Desconocido", "gray"),
	approved("Aprobada", "green"),
	discarded("Descartada", "gray"),
	pendingCorrection("Pte.correción", "orange");
	
	private String color;
	private String decription;
	
	private State(String description, String color) {
	    this.color = color;
	    this.decription = description;
	}
	
	public String getColor() {
	    return color;
	}
	
	public String getDescription() {
	    return decription;
	}
	
    }
    
    enum Confidence {
	low("red"),
	high("green"),
	medium("orange"),
	unknown("gray");
	
	private String color;
	
	private Confidence(String color) {
	    this.color = color;
	}
	
	public String getColor() {
	    return color;
	}
	
    }
    
    enum Severity {
	low("orange"),
	high("red"),
	medium("orange"),
	unknown("gray")
	;
	
	private String color;
	
	private Severity(String color) {
	    this.color = color;
	}
	
	public String getColor() {
	    return color;
	}
	
    }

    public String documentFinished(Object input, Context context) {
	try {
	    JSONObject body = getBody(input); 
	    JSONObject data = body.getJSONObject("data");
	    return documentFinished(data);
	} catch ( InterruptedException e ) {
	    /* Clean up whatever needs to be handled before interrupting  */
	    Thread.currentThread().interrupt();
	    throw new RuntimeException(e);
	} catch (URISyntaxException | IOException e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    public String handleRequest(Object input, Context context) {
	
	try {
	    JSONObject body = getBody(input); 
	    JSONObject data = body.getJSONObject("data");
	    String type = body.getString("type");
	    switch (type) {
	    case "batch.finished":
		return data.toString(1);
	    case "document.approved","document.processed":
		return documentFinished(data);
	    default:
		return type;
	    }
	} catch ( InterruptedException e ) {
	    /* Clean up whatever needs to be handled before interrupting  */
	    Thread.currentThread().interrupt();
	    throw new RuntimeException(e);
	} catch (URISyntaxException | IOException e) {
	    throw new RuntimeException(e);
	}
    }
    
    private TaskWorkflow getTaskWorkflow(JSONObject data, Predicate<JSONObject> filter ) throws URISyntaxException, IOException, InterruptedException {
	Integer taskId = get(data, "clientData/loadTask/id");
	String userLogin = get(data, "clientData/loadS3/user");
	String domainName = get(data, "clientData/loadS3/domain");

	JSONArray taskWorkflows = AonTask.getTaskWorkflows(domainName, userLogin, taskId);
	for ( int i = 0; i < taskWorkflows.length() ; i++ ) {
	    JSONObject taskWorkflowJSON = taskWorkflows.getJSONObject(i);
	    if ( filter.test(taskWorkflowJSON) ) { 
		return TaskWorkflowJSON.fromJSON(taskWorkflowJSON);
	    }
	}
	
	return null;
	
    }

    private TaskWorkflow getDocumentSent(JSONObject data ) throws URISyntaxException, IOException, InterruptedException {
	String s3Key = get(data, "clientData/loadS3/key");
	return getTaskWorkflow(data, taskWorflowJSON -> AonStringUtils.contains(taskWorflowJSON.getString("comment"), s3Key) );
	
    }
    
    private TaskWorkflow getDocumentProcessed(JSONObject data ) throws URISyntaxException, IOException, InterruptedException {
	String id = get(data, "_id");
	return getTaskWorkflow(data, taskWorflowJSON -> AonStringUtils.contains(taskWorflowJSON.getString("comment"), id) );
	
    }

    private String documentFinished(JSONObject data) throws URISyntaxException, IOException, InterruptedException {
    	//	JSONObject taskWorkflowJSON = getDocumentProcessed(data);
    	//	if ( taskWorkflowJSON != null ) {
    	//	    return taskWorkflowJSON.toString(1);
    	//	}
	
    	TaskWorkflow taskWorkflow = getDocumentSent(data);
    	if ( taskWorkflow == null ) {
    		taskWorkflow = getDocumentProcessed(data);
    	}
    	if ( taskWorkflow == null ) {
    		Integer task = get(data, "clientData/loadTask/id");
    		taskWorkflow = new TaskWorkflow().setTask(task).setType(TaskWorkflowType.COMMENT);
    	}
	
	
    	Map<String, String> params = new HashMap<>();
	
    	String id = get(data, "_id");
    	params.put("id", id);
	
    	String type = get(data, "type"); 
    	params.put("type", valueOf(type, Type.unknown).getDescription());
	
    	String publicState = get(data, "publicState");
    	params.put("publicState", valueOf(publicState, State.unknown).getDescription());
    	params.put("publicStateColor", valueOf(publicState, State.unknown).getColor());
    	
    	JSONArray errors = get(data, "validationInfo/errors");	

    	String errorsHTML = formatErrors(errors);

    	params.put("errorsHTML", errorsHTML);
	
    	String  confidence = get(data, "confidence");
    	params.put("confidenceColor", valueOf(confidence, Confidence.unknown).getColor());
	

    	String s3Key = get(data, "clientData/loadS3/key");
    	String s3Bucket = get(data, "clientData/loadS3/bucket");

    	URL downloadURL = S3.getDownloadURL(s3Bucket, s3Key);
    	params.put("downloadURL", downloadURL.toExternalForm());

    	String documentNumber = get(data, "data/documentNumber/value");
    	params.put("documentNumber", getOrDefault(documentNumber, "-"));
	
    	String issuerName = get(data, "data/issuerName/value"); 
    	params.put("issuerName", getOrDefault(issuerName, ""));
	
    	String recipientName = get(data, "data/recipientName/value");
    	params.put("recipientName", getOrDefault(recipientName, ""));
	
    	Number totalTaxBaseAmount = get(data, "data/totalTaxBaseAmount/value");
    	params.put("totalTaxBaseAmount", format(totalTaxBaseAmount, ""));
	
    	Date issueDate = parse(get(data, "data/issueDate/value"));
    	params.put("issueDate", format(issueDate, "dd/MM/yyyy", ""));
	
    	Date creationDate = parse(get(data, "creation"));
    	params.put("creationDate", format(creationDate, ""));
	
    	String companyName = S3.getCompanyName(s3Bucket, s3Key);
    	params.put("companyName", getOrDefault(companyName, ""));
	
    	params.put("font", "font-family: Karla,sans-serif;font-size: 11px; color: rgb(57,57,57);");
	
    	taskWorkflow.setComment(format(
                """
                <!-- id:"${id}" -->
                <div style="font-family: Karla,sans-serif;font-size: 11px; color: rgb(57,57,57); font-weight:normal;">
                <table style="width:100%;padding: 8px; border-collpase:collapse;">
                <thead style="background-color:#f5f7fa">
                <tr>
                <th style="padding:8px;" >Tipo</th>
                <th style="padding:8px;">Compañia</th>
                <!--th style="padding:8px;">Extracción</th-->
                <th style="padding:8px;">Estado</th>
                <th style="padding:8px;">Núm.factura</th>
                <th style="padding:8px;">Nombre emisor</th>
                <th style="padding:8px;">Nombre receptor</th>
                <th style="padding:8px;" >Base Imponible</th>
                <th style="padding:8px;">Fecha emisión</th>
                <th style="padding:8px;">Fecha de subida</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                <td style="padding:8px;" >${type}</td>
                <td style="padding:8px;">${companyName}</td>
                <!--td style="padding:8px;text-align:center;"><span class="material-icons" style="color:${confidenceColor};" >warning</span></td-->
                <td style="padding:8px;"><span style="background-color:${publicStateColor};padding: 2px 16px; border-radius: 22px; color: white; font-weight: bold;" >${publicState}</span></td>
                <td style="padding:8px;"><a href="${downloadURL}" target="_blank" style="text-decoration:underline;">${documentNumber}</a></td>
                <td style="padding:8px;">${issuerName}</td>
                <td style="padding:8px;">${recipientName}</td>
                <td style="padding:8px;" >${totalTaxBaseAmount}</td>
                <td style="padding:8px;">${issueDate}</td>
                <td style="padding:8px;">${creationDate}</td>
                </tr>
                </tbody>
                </table>
                ${errorsHTML}
                </div>
                """, 
		params));
    	
    	String userLogin = get(data, "clientData/loadS3/user");
    	String domainName = get(data, "clientData/loadS3/domain");

    	taskWorkflow.setCreationDate(new Date());
    	taskWorkflow.setCreationUser(userLogin);
	
    	JSONObject taskWorkflowJSON = AonTask.addTaskWorkflow(domainName, userLogin, taskWorkflow);
    	State state = valueOf(publicState, State.unknown);
    	if(publicState != null && State.approved.equals(state)
    		AonInvofox.acceptInvofoxInvoice(domainName, userLogin, id);
    	else AonInvofox.rawdocInvofoxInvoice(domainName, userLogin, id);
    	
    	return taskWorkflowJSON.toString(1);
    }

    /**
     * @param errors
     * @param errorsHTMLBuilder
     */
    private String  formatErrors(JSONArray errors) {
	if ( errors.isEmpty() ) { 
	    return "";
	}
	
	Map<String, String> errorsParams = new HashMap<>();
	int lowCondfidenceErrors = 0;
	StringBuilder errorsDescriptionBuilder = new StringBuilder(); 
	for ( int i = 0; i < errors.length(); i++) {
	    JSONObject error = errors.getJSONObject(i);
	    
	    System.out.println(error.toString(1));
	    
	    String code = error.getString("code");
	    String severity = error.getString("severity");
	    if ( "ERR_LOW_CONFIDENCE".equalsIgnoreCase(code)) {
		lowCondfidenceErrors++;
		continue;
	    }
	    
	    String errorDescription = getErrorDescription(error);
	    String errorColor = valueOf(severity, Severity.unknown).getColor();
	    
	    Map<String, String> errorParams = new HashMap<>();
	    errorParams.put("errorColor", errorColor);
	    errorParams.put("errorDescription", errorDescription);
	    errorsDescriptionBuilder.append(
	    format(
                """
                <div style="table; width:100%; padding: 8px; font-weight:bold">
                <span class="material-icons" style="display:table-cell; vertical-align:middle; text-align: left;padding: 0px 8px;font-size: 19px; color:${errorColor}">warning</span>
                <span style="display:table-cell; vertical-align:middle; text-align: left; width: 100%;padding: 0px 8px" >${errorDescription}</span>
                </div>
                """
		,errorParams
		)
	    );
	}
	
	if ( lowCondfidenceErrors > 0 ) {
	    
	    Map<String, String> errorParams = new HashMap<>();
	    
	    StringBuilder fieldsDescriptionsBuilder = new StringBuilder();
	    for (int i = 0; i < errors.length(); i++) {
		JSONObject error = errors.getJSONObject(i);
		String code = error.getString("code");
		if (!"ERR_LOW_CONFIDENCE".equalsIgnoreCase(code)) {
		    continue;
		}
		JSONArray fields =  error.getJSONArray("fields");
		for ( int j = 0; j < fields.length(); j++ ) {
		    	JSONObject field = fields.getJSONObject(j);
		    	String fieldName = field.getString("name");
		    	String fieldDescription = getFieldDescription( fieldName );

		    	String fieldPrefix = field.getString("prefix");
		    	if ( "breakdowns".equals(fieldPrefix)) {
			    int fieldIndex = field.getInt("index");
		    	    fieldDescription += " de la línea " + ( fieldIndex + 1 );
		    	}
		    	
        		fieldsDescriptionsBuilder.append(
        		format(
                                """
                                <li style="text-align: left; ">El campo <b>${fieldName}</b> tiene poca confianza.</li>
                                """,
        			Collections.singletonMap("fieldName", fieldDescription))
        		);
		}
		
	    }

	    errorParams.put("errorColor", "orange");
	    errorParams.put("fieldsDescriptions", fieldsDescriptionsBuilder.toString());
	    errorParams.put("errorDescription", String.format("%d campo(s) con poca confianza", lowCondfidenceErrors ));
	    errorsDescriptionBuilder.append(
	    
	    format(
                """
                <div style="table; width:100%; padding: 8px; font-weight:bold">
                <span class="material-icons" style="display:table-cell; vertical-align:middle; text-align: left;padding: 0px 8px;font-size: 19px; color:${errorColor}">warning</span>
                <span style="display:table-cell; vertical-align:middle; text-align: left; width: 100%;padding: 0px 8px" >${errorDescription}</span>
                </br style="font-size: 4px;" >
                <span onclick="this.nextElementSibling.style.display = 'inherit';" style="display:table-cell; vertical-align:middle; text-align: left; width: 100%;padding: 0px 44px; font-weight: normal; cursor:pointer;" >Ver Campos</span>
                <ul style="padding: 0px 54px; font-weight: normal; display: none">
                ${fieldsDescriptions}
                </ul> 
                </div>
                """
		,errorParams
		)
	    );

	    
	}
	
	String errorsDescription = errorsDescriptionBuilder.toString();
	errorsParams.put("errorsDescription", errorsDescription );

	String errorsLabel = 
	format(
            """
            <div style="background-color:#e83151;color:#FFF; display:table; width:100%; padding: 8px">
            <span class="material-icons" style="display:table-cell; vertical-align:middle; text-align: left;padding: 0px 8px;">warning</span>
            <span style="display:table-cell; vertical-align:middle; text-align: left; width: 100%;padding: 0px 8px" >${errorsTitle}</span>
            </div>
            """
	, Collections.singletonMap("errorsTitle",  String.format("%d error(es)", errors.length()) )
	);
	errorsParams.put("errorsLabel", errorsLabel);
	
        return
        format(
        """
        <div style="width:100%;border:1px solid #e83151; border-radius: 6px; display:table;" >
        ${errorsLabel}
        ${errorsDescription}
        </div>
        """
        , errorsParams );
    }
    
    static String getFieldDescription(String fieldName) {
	Map<String, String> fieldNamesDescription = getFieldsDescriptionsMap();
	return fieldNamesDescription.getOrDefault(fieldName, fieldName);
	
    }

    /**
     * @return
     */
    static Map<String, String> getFieldsDescriptionsMap() {
	Map<String, String> fieldNamesDescriptionsMap = new HashMap<String, String>();
	fieldNamesDescriptionsMap.put("currency", "Divisa");
	fieldNamesDescriptionsMap.put("language", "Idioma");
	fieldNamesDescriptionsMap.put("isCreditNote", "isCreditNote");
	fieldNamesDescriptionsMap.put("numberFormat", "numberFormat");
	fieldNamesDescriptionsMap.put("invoiceRef", "Núm. factura");
	fieldNamesDescriptionsMap.put("seriesCode", "Núm. serie");
	fieldNamesDescriptionsMap.put("taxClass", "Tipo impositivo");
	fieldNamesDescriptionsMap.put("issuerCountry", "País emisor");
	fieldNamesDescriptionsMap.put("recipientCountry", "País receptor");
	fieldNamesDescriptionsMap.put("issuerAddressDetails", "issuerAddressDetails");
	fieldNamesDescriptionsMap.put("recipientAddressDetails", "recipientAddressDetails");
	fieldNamesDescriptionsMap.put("documentNumber", "Núm.factura");
	fieldNamesDescriptionsMap.put("issueDate", "Fecha emisión");
	fieldNamesDescriptionsMap.put("issuerName", "Nombre emisor");
	fieldNamesDescriptionsMap.put("issuerTaxId", "CIF emisor");
	fieldNamesDescriptionsMap.put("issuerAddress", "Dirección emisor");
	fieldNamesDescriptionsMap.put("issuerEmail", "Email emisor");
	fieldNamesDescriptionsMap.put("issuerPhoneNumber", "Teléfono emisor");
	fieldNamesDescriptionsMap.put("issuerWebsite", "Web emisor");
	fieldNamesDescriptionsMap.put("recipientName", "Nombbre receptor");
	fieldNamesDescriptionsMap.put("recipientTaxId", "CIF receptor");
	fieldNamesDescriptionsMap.put("recipientAddress", "Dirección receptor");
	fieldNamesDescriptionsMap.put("shippingAddress", "Dirección envío receptor");
	fieldNamesDescriptionsMap.put("recipientEmail", "Email receptor");
	fieldNamesDescriptionsMap.put("recipientWebsite", "Web receptor");
	fieldNamesDescriptionsMap.put("recipientPhoneNumber", "Teléfono receptor");
	fieldNamesDescriptionsMap.put("paymentMethod", "Método de pago");
	fieldNamesDescriptionsMap.put("IBAN", "IBAN");
	fieldNamesDescriptionsMap.put("SWIFT", "SWIFT");
	fieldNamesDescriptionsMap.put("clientCode", "Núm. cliente");
	fieldNamesDescriptionsMap.put("deliveryNoteRef", "Núm. albarán");
	fieldNamesDescriptionsMap.put("orderRef", "Núm. pedido");
	fieldNamesDescriptionsMap.put("contractRef", "Contrato");
	fieldNamesDescriptionsMap.put("incoterms", "InCoTerms");
	fieldNamesDescriptionsMap.put("documentType", "Tipo de documento");
	fieldNamesDescriptionsMap.put("additionalNotes", "Notas adicionales");
	fieldNamesDescriptionsMap.put("legalNotes", "Texto legal");
	fieldNamesDescriptionsMap.put("totalTaxAmount", "Cuota");
	fieldNamesDescriptionsMap.put("totalTaxBaseAmount", "Base imponible");
	fieldNamesDescriptionsMap.put("totalAmount", "Total");
	fieldNamesDescriptionsMap.put("totalGrossAmount", "Total");
	fieldNamesDescriptionsMap.put("totalDueAmount", "Total a pagar");
	fieldNamesDescriptionsMap.put("withholdingTaxRate", "withholdingTaxRate");
	fieldNamesDescriptionsMap.put("withholdingTaxAmount", "Tasas");
	fieldNamesDescriptionsMap.put("totalFeesAmount", "totalFeesAmount");
	fieldNamesDescriptionsMap.put("totalDiscountAmount", "Descuento");
	fieldNamesDescriptionsMap.put("reimbursableExpensesAmount", "reimbursableExpensesAmount");
	fieldNamesDescriptionsMap.put("additionalChargesAmount", "Gastos adicionales");
	fieldNamesDescriptionsMap.put("additionalDiscountsAmount", "Descuentos adicionales");
	fieldNamesDescriptionsMap.put("serviceAddress", "serviceAddress");
	fieldNamesDescriptionsMap.put("supplyNumber", "supplyNumber");
	fieldNamesDescriptionsMap.put("meterNumber", "meterNumber");
	fieldNamesDescriptionsMap.put("totalUsage", "totalUsage");
	fieldNamesDescriptionsMap.put("usageUnitOfMeasurement", "usageUnitOfMeasurement");

	// lines/breakdows
	fieldNamesDescriptionsMap.put("taxRate", "Tipo (%)");
	fieldNamesDescriptionsMap.put("taxAmount", "Cuota");
	fieldNamesDescriptionsMap.put("taxBaseAmount", "Base imponible");
	
	
	return fieldNamesDescriptionsMap;
    }

    static String getSeverityColor(JSONArray errors ) {
	List<String> severities = errors.toList().stream().map( error -> (String) ((Map<?,?>) error).get("severity")).toList();
	if(severities.contains("medium")){
	    return "orange";
	} else if(severities.contains("low")){
	    return "green";
	} else {
	    return "green";
	}
    }
    
    static String format(String pattern, Map<String,String> params) {
	return params.keySet().stream().reduce(pattern, ( str, name)-> str.replace("${"+name+"}", params.get(name)));
    }
    
    static JSONObject getBody(Object input) {
	Map<String, ?> map = (Map<String, ?>) input;
	return new JSONObject((String)map.get("body"));
    }
    
    static <T> T get(JSONObject jsonObject, String path) {
	String [] keys = path.split("/");
	for (int i = 0; i < ( keys.length -1 ); i++) {
	    try {
        	    jsonObject = jsonObject.getJSONObject(keys[i]);
	    } catch (JSONException e ) {
		return null;
	    } catch ( Exception e ) {
		System.out.println(jsonObject.toString(1));
		throw e;
	    }
	}
	
	try {
	    Object obj = jsonObject.get(keys[keys.length - 1]);
	    return JSONObject.NULL.equals(obj) ? null : (T) obj;
	} catch (JSONException e ) {
	    return null;
	}
	
    }

    static <T> T getOrDefault(T t, T defaultValue) {
	return Optional.ofNullable(t).orElse(defaultValue);
    }
    
    
    static Date parse(String str) {
	try {
	    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(str);
	} catch (Exception e) {
	    return null;
	}
    }

    static String format(Date date, String defaultValue) {
	return format(date, "dd/MM/yyyy' 'HH:mm", defaultValue);
    }

    static String format(Date date, String pattern, String defaultValue) {
	try {
	    return new SimpleDateFormat(pattern).format(date);
	} catch (Exception e) {
	    return defaultValue;
	}
    }

    static String format(Number number, String defaultValue) {
	if ( number == null ) {
	    return defaultValue;
	}
	try {
	    return NumberFormat.getCurrencyInstance(new Locale("es","ES")).format(number.doubleValue());
	} catch (IllegalArgumentException e) {
	    return defaultValue;
	}
    }
    
    static String getErrorDescription(JSONObject error) {
	
	switch ( error.getString("code") ) {
	case "ERR_FILE_TOO_BIG":
	    return "El archivo subido es demasiado grande (máximo 50 MB)";
	case "ERR_TOO_MANY_PAGES":
	    return "El documento tiene demasiadas páginas (máximo 10)";
	case "ERR_UNSUPPORTED_FILE_FORMAT":
	    return "El form ato del documento no es compatible.";
	case "ERR_EMPTY_VALUE": {
	    String fieldName = getFieldName(error);
	    return String.format("El campo <b>%s</b> no tiene valor", getFieldDescription(fieldName));
	} 
	case "ERR_INCORRECT_VALUE": {
	    String fieldName = getFieldName(error);
	    int line = getLine(error);
	    return String.format("Valor no permitido para el campo <b>%s</b>", fieldName);
	}
	case "ERR_INVALID_FORMAT":
	    return "El valor extraído tiene un formato no válido";
	case "ERR_LOW_CONFIDENCE":
	    return "El valor extraído tiene poca confianza";
	case "ERR_HANDWRITTEN_DOC":
	    return "El documento está escrito a mano.";
	case "ERR_INVALID_DOC_TYPE":
	    return "Se desconoce el tipo de documento (no factura, ticket, etc.)";
	case "ERR_CROPPED_DOC":
	    return "El documento está recortado, por lo que falta información.";
	case "ERR_MULTI_DOC_PAGE_FOUND":
	    return "El archivo contiene varios documentos.";
	case "ERR_PAGES_MISSING":
	    return "Al documento le faltan páginas, por lo tanto está incompleto.";
	case "ERR_BAD_QUALITY":
	    return "El documento tiene mala calidad.";
	case "ERR_DUPLICATED_DOCUMENT":
	    return "El documento está duplicado.";
	case "ERR_MISSING_INFO":
	    return "Al documento le falta información requerida.";
	case "ERR_BREAKDOWN_AMOUNT_MISSMATCH":
	    int line = getLine(error);
	    return String.format("Las cantidades %s de los desgloses no son  correctas.", line != -1 ? "de la línea " + line : "");
	case "ERR_CLASSIFIER_DISCARD":
	    return "Este documento no tiene un tipo válido.";
	case "WARN_CLASSIFIER_FORCED_DEFTYPE":
	    String forced = valueOf(get( error , "forced"), Type.unknown).getDescription();
	    String detected = valueOf(get(error, "detected"), Type.unknown).getDescription();
	    return String.format("El clasificador detecto %s pero estableció el tipo por defecto %s.", detected, forced);
	       
	case "ERR_OTHER":
	    return "Error generico";
	default : 
	    return "Error desconocido '" + error.getString("code") +"'" ;
	}
	
    }
    
    public static int getLine(JSONObject error ) {
	JSONArray fields =  error.getJSONArray("fields");
	for ( int i = 0; i < fields.length(); i++) {
	    JSONObject field = fields.getJSONObject(i);
	    String prefix = field.getString("prefix");
	    if ( "breakdowns".equalsIgnoreCase(prefix)) {
		int index = field.getInt("index");
		return index + 1 ;
	    }
	}
	return -1;
    }
    
    public static String getFieldName(JSONObject error ) {
	JSONArray fields =  error.getJSONArray("fields");
	for ( int i = 0; i < fields.length(); i++) {
	    JSONObject field = fields.getJSONObject(i);
	    if ( field.has("name")) { 
        	    String fieldName = field.getString("name");
        	    if ( AonStringUtils.isNotBlank(fieldName)) {
        		return fieldName;
        	    }
	    }
	}
	return "";
    }
    
    public static <T extends Enum<T>> T valueOf ( String name, T defaultValue ) {
	try {
	    return Enum.valueOf(defaultValue.getDeclaringClass(), name);
	} catch ( Exception e ) {
	    return defaultValue;
	}
    }
}
