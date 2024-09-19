package solutions.aon.in.invoice.aws.lambda;

import static java.lang.String.format;
import static solutions.aon.aws.s3.S3EventObject.getS3EventObjects;
import static solutions.aon.in.invoice.aws.lambda.InvofoxWebhookHandler.format;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.finance.InvofoxConfiguration;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.api.model.task.TaskWorkflowType;
import com.esferalia.aon.occam.api.model.type.RawdocStatus;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.api.AonInvofox;
import net.aonsolutions.aon.api.AonSecurity;
import net.aonsolutions.aon.api.AonTask;
import net.aonsolutions.aon.in.pdf.maker.exception.CanNotCreatePdfException;
import net.aonsolutions.aon.in.pdf.maker.image.ImageToPdf;
import solutions.aon.aws.s3.S3EventObject;
import solutions.aon.in.invoice.aws.lambda.Invofox.DocumentType;

public class S3RequestHandler implements RequestHandler<Object, String> {

    static final String ID = "id";
    static final String _ID = "_id";
    static final String LOAD_S3 = "loadS3";
    static final String LOAD_TASK = "loadTask";
    static final String LOAD_BATCH = "loadBatch";
    static final String RAWDOC = "rawdoc";
    static final int MAX_SLEEP_TIME = 2000;
    static final String LOAD_BATCH_WAIT_ID = "loadBatchId";
    
    static final String WORKGROUP = "FACTURAS";
    static final String TITLE = "Se han subido documentos de facturas desde '%s'.";
    static final String DESCRIPTION = "La empresa '%s' ha subido facturas para su procesamiento y validación.";


    @Override
    public String handleRequest(Object input, Context context) {
    	List<S3EventObject> s3EventObjects = getS3EventObjects(input);
    	s3EventObjects.forEach( S3RequestHandler::handleS3EventObject );
	
    	return "That's all folks :-)";
    }
    
    static JSONObject newLoadBatchTask(String loadBatchId) {
    	JSONObject loadBatchTaskJSON = new JSONObject();
    	loadBatchTaskJSON.put(LOAD_BATCH, Collections.singletonMap(_ID, loadBatchId));
    	return loadBatchTaskJSON;
    }

    static String getLoadBatchId(JSONObject loadBatchTaskJSON ) {
    	JSONObject loadBatchJSON = loadBatchTaskJSON.getJSONObject(LOAD_BATCH);
    	return loadBatchJSON.getString(_ID);
    }

    static JSONObject newLoadBatchTask(JSONObject loadBatchJSON, JSONObject taskJSON) {
    	JSONObject loadBatchTaskJSON = new JSONObject();
    	loadBatchTaskJSON.put(LOAD_BATCH, loadBatchJSON);
    	loadBatchTaskJSON.put(LOAD_TASK, taskJSON );
    	return loadBatchTaskJSON;
    }

    static void handleS3EventObject(S3EventObject s3EventObject) {
    	try {
    		if(isImage(s3EventObject)) {
    			byte[] image = download(s3EventObject);
    			byte[] pdf = imageToPdf(image);

    			// sign PDF. 
    			// Certificate cert = null; // TODO
    			// byte[] signedPdf = PdfSigner.sign(cert, pdf);
    			
    			// save PDF IN S3.
    			s3EventObject.setKey(s3EventObject.getKey().replace(".jpg", ".pdf"));
    			s3EventObject.setFileName(s3EventObject.getFileName().replace(".jpg", ".pdf"));
    			
    			solutions.aon.aws.s3.S3.upload(s3EventObject.getBucket(), s3EventObject.getKey(), pdf);
    		}    		
    		DomainUserRoles dur = getDomainUserRoles(s3EventObject);
    		if(dur.isInvofox()) {
        		Integer rawdocId = createRawdoc(s3EventObject, RawdocStatus.PROCESSING);
    			InvofoxConfiguration invofoxConfiguration = getInvofoxConfiguration(s3EventObject);
        		String companyId =  getCompanyId(invofoxConfiguration, s3EventObject);
        		String downloadURL = getDowloadURL(s3EventObject); 
        		JSONObject loadBatchTaskJSON = getLoadBatchTask(invofoxConfiguration, companyId, s3EventObject);
        		String loadBatchId = getLoadBatchId(loadBatchTaskJSON);

        		JSONObject clientData = new JSONObject()
       				.put(LOAD_S3, new JSONObject()
       					.put("key", s3EventObject.getKey())
       					.put("user", s3EventObject.getUser())
       					.put("domain", s3EventObject.getDomain())
       					.put("bucket", s3EventObject.getBucket()))
       				.put(LOAD_TASK, new JSONObject()
       					.put("id", loadBatchTaskJSON.getJSONObject(LOAD_TASK).getInt("id")))
       				.put(RAWDOC, rawdocId);
    	    
        		loadDocuments(invofoxConfiguration, DocumentType.INVOICE, companyId, loadBatchId, clientData,  downloadURL);
    	    
        		documentSent(s3EventObject, loadBatchTaskJSON, downloadURL);
    		} else {
        		createRawdoc(s3EventObject, RawdocStatus.INBOX);
    		}
    	} catch (URISyntaxException e) {
    		e.printStackTrace();
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	} catch (NoSuchCompanyException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	} catch (Exception e) {
    		e.printStackTrace();
    	} 
    }

    static DomainUserRoles getDomainUserRoles(S3EventObject s3Object) throws URISyntaxException, IOException, InterruptedException {
    	return AonSecurity.getDomainUserRoles(s3Object.getDomain(), s3Object.getUser());
    }
    
    static InvofoxConfiguration getInvofoxConfiguration(S3EventObject s3Object) throws URISyntaxException, IOException, InterruptedException {
    	return AonInvofox.getInvofoxConfiguration(s3Object.getDomain(), s3Object.getUser());
    }
    
    static boolean isImage(S3EventObject s3EventObject) {
    	String contentType = solutions.aon.aws.s3.S3.getContentType(s3EventObject.getBucket(), s3EventObject.getKey());
    	return contentType.toLowerCase().contains("image");
    }
    
    static byte[] imageToPdf(byte[] image) throws IOException, CanNotCreatePdfException {
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("ref_homologation", "000000");
		metadata.put("software_name", "Aon Solutions");
		metadata.put("software_version", "9.23");
		metadata.put("timestamp", AonDateUtils.format(new Date(), "hh:mm dd/MM/yyyy"));

		try (ImageToPdf imageToPdf = new ImageToPdf(image, metadata)) {
			return imageToPdf.toByteArray();
		}
    }
    
    static String getDowloadURL(S3EventObject s3Object) {
    	return S3.getDownloadURL(s3Object.getBucket(), s3Object.getKey()).toExternalForm();
    }

    static byte[] download(S3EventObject s3Object) throws IOException {
    	return solutions.aon.aws.s3.S3.download(s3Object.getBucket(), s3Object.getKey());
	}
    
    static String getLoadBatchKey(S3EventObject s3Object) {
    	return s3Object.getPrefix() + "/" + s3Object.getDomain() + "/" + s3Object.getDocument() + "/" + s3Object.getUser() + "/" + s3Object.getJob() + "/" + "loadbatch";
    }

    public static String getCompanyId(InvofoxConfiguration invofoxConfiguration, S3EventObject s3EventObject) throws URISyntaxException, IOException, InterruptedException {
    	return getCompanyId(invofoxConfiguration.getApiKey(), invofoxConfiguration.getApiUrl(), s3EventObject);
    }

    public static String getCompanyId(String invofoxApiKey, String invofoxApiUrl, S3EventObject s3EventObject) throws URISyntaxException, IOException, InterruptedException {
	try {
	    return Invofox.getCompanyId(invofoxApiKey, invofoxApiUrl, s3EventObject.getDocument());
	} catch ( NoSuchCompanyException ne) {
	    try {
		String companyName = getCompanyName(s3EventObject);
		return Invofox.newCompany(invofoxApiKey, invofoxApiUrl, s3EventObject.getDocument(), companyName, Collections.emptyMap());
	    } catch ( AlreadyCompanyExistsException ae ) {
		return Invofox.getCompanyId(invofoxApiKey, invofoxApiUrl, s3EventObject.getDocument()); 
	    }
	}
    }

    /**
     * @param s3EventObject
     * @param companyName
     * @return
     */
    static String getCompanyName(S3EventObject s3EventObject) {
	try {
	    return S3.getCompanyName(s3EventObject.getBucket(), s3EventObject.getKey());
	} catch ( Exception e ) {
	    e.printStackTrace();
	    return s3EventObject.getDomain() ;
	}
    }

    public static JSONObject getLoadBatchTask(InvofoxConfiguration invofoxConfiguration, String companyId, S3EventObject s3EventObject) throws URISyntaxException, IOException, InterruptedException {
	return getLoadBatchTask(invofoxConfiguration.getApiKey(), invofoxConfiguration.getApiUrl(), companyId, s3EventObject);
    }

    public static JSONObject getLoadBatchTask(String invofoxApiKey, String invofoxApiUrl, String companyId, S3EventObject s3EventObject) throws URISyntaxException, IOException, InterruptedException {
	JSONObject loadBatchTaskJSON = newLoadBatchTask(LOAD_BATCH_WAIT_ID);
	String loadBatchKey = getLoadBatchKey(s3EventObject);
	
	try {
	    String loadBatchId  = LOAD_BATCH_WAIT_ID;
	    while (LOAD_BATCH_WAIT_ID.equals(loadBatchId)) {
		Thread.sleep(Math.min(s3EventObject.getOrder() * 1000L, MAX_SLEEP_TIME)); 
		loadBatchTaskJSON = S3.getLoadBatchTask(s3EventObject.getBucket(), loadBatchKey);
		loadBatchId = getLoadBatchId(loadBatchTaskJSON);
	    }
	} catch (NoSuchLoadBatchException e) {
	    // Write semaphore, if present other lambdas must wait.  
	    S3.setLoadBatchTask(s3EventObject.getBucket(), loadBatchKey, loadBatchTaskJSON);
	    
	    JSONObject loadBatchJSON = Invofox.newLoadBatch(invofoxApiKey, invofoxApiUrl, companyId);
	    // new issue for this batch, and don't wait for it
	    String companyName = getCompanyName(s3EventObject);
	    Task task = new Task()
		    .setTitle(format(TITLE, companyName))
		    .setDescription(format(DESCRIPTION, companyName ))
		    .setWorkgroup(new Workgroup().setDescription(WORKGROUP));
	    JSONObject taskJSON ;
	    
	    try {
		taskJSON =  AonTask.newTask(s3EventObject.getDomain(), s3EventObject.getUser(), task );
	    } catch ( Exception t ) {
		taskJSON = new JSONObject()
		.put("id", Integer.MAX_VALUE );
	    }
	    
	    loadBatchTaskJSON = newLoadBatchTask(loadBatchJSON, taskJSON);
	    S3.setLoadBatchTask(s3EventObject.getBucket(), loadBatchKey, loadBatchTaskJSON);
	    
	}
	return loadBatchTaskJSON;
    }
    
    protected static JSONObject loadDocuments(InvofoxConfiguration invofoxConfiguration, DocumentType type, String companyId, String loadBatchId, JSONObject clientData , String ...downloadURLs) throws URISyntaxException, IOException, InterruptedException {
    	return Invofox.loadDocuments(invofoxConfiguration.getApiKey(), invofoxConfiguration.getApiUrl(), DocumentType.INVOICE, companyId, loadBatchId, clientData,  downloadURLs);
    }
    
    
    static String documentSent(S3EventObject s3EventObject, JSONObject loadBatchTaskJSON, String downloadURL) throws URISyntaxException, IOException, InterruptedException {
	
	String s3Key = s3EventObject.getKey();
	String s3Bucket = s3EventObject.getBucket();
	String userLogin = s3EventObject.getUser();
	String domainName = s3EventObject.getDomain();


	TaskWorkflow taskWorkflow = new TaskWorkflow();
	
	Integer task = loadBatchTaskJSON.getJSONObject(LOAD_TASK).getInt("id");
	
	taskWorkflow.setTask(task);
	taskWorkflow.setType(TaskWorkflowType.COMMENT);
	
	Map<String, String> params = new HashMap<>();
	
	params.put("s3Key", s3EventObject.getKey());
	
	params.put("publicState", "Procesando");
	params.put("publicStateColor", "darkblue");
	
	params.put("downloadURL", downloadURL);
	params.put("fileName", s3EventObject.getFileName());
	
	params.put("creationDate", format(new Date(), ""));
	
	String companyName = S3.getCompanyName(s3Bucket, s3Key);

	params.put("companyName", InvofoxWebhookHandler.getOrDefault(companyName, "") );
	
	params.put("font", "font-family: Karla,sans-serif;font-size: 11px; color: rgb(57,57,57);");
	
	taskWorkflow.setComment(format(
                """
                <!-- s3Key:"${s3Key}" -->
                <div style="font-family: Karla,sans-serif;font-size: 11px; color: rgb(57,57,57); font-weight:normal;">
                <table style="width:100%;padding: 8px; border-collpase:collapse;">
                <thead style="background-color:#f5f7fa">
                <tr>
                <th style="padding:8px;" >Tipo</th>
                <th style="padding:8px;">Compañia</th>
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
                <td style="padding:8px;" >-</td>
                <td style="padding:8px;">${companyName}</td>
                <td style="padding:8px;"><span style="background-color:${publicStateColor};padding: 2px 16px; border-radius: 22px; color: white; font-weight: bold;" >${publicState}</span></td>
                <td style="padding:8px;"><a href="${downloadURL}" target="_blank" style="text-decoration:underline;">${fileName}</a></td>
                <td style="padding:8px;">-</td>
                <td style="padding:8px;">-</td>
                <td style="padding:8px;" >-</td>
                <td style="padding:8px;">-</td>
                <td style="padding:8px;">${creationDate}</td>
                </tr>
                </tbody>
                </table>
                </div>
                """, 
		params));
	
	
	taskWorkflow.setCreationDate(new Date());
	taskWorkflow.setCreationUser(userLogin);
	
	JSONObject taskWorkflowJSON = AonTask.addTaskWorkflow(domainName, userLogin, taskWorkflow);
	
	return taskWorkflowJSON.toString(1);
    }

//    {
//	  "Records": [
//	    {
//	      "eventVersion": "2.0",
//	      "eventSource": "aws:s3",
//	      "awsRegion": "us-east-1",
//	      "eventTime": "1970-01-01T00:00:00.000Z",
//	      "eventName": "ObjectCreated:Put",
//	      "userIdentity": {
//	        "principalId": "EXAMPLE"
//	      },
//	      "requestParameters": {
//	        "sourceIPAddress": "127.0.0.1"
//	      },
//	      "responseElements": {
//	        "x-amz-request-id": "EXAMPLE123456789",
//	        "x-amz-id-2": "EXAMPLE123/5678abcdefghijklambdaisawesome/mnopqrstuvwxyzABCDEFGH"
//	      },
//	      "s3": {
//	        "s3SchemaVersion": "1.0",
//	        "configurationId": "testConfigRule",
//	        "bucket": {
//	          "name": "example-bucket",
//	          "ownerIdentity": {
//	            "principalId": "EXAMPLE"
//	          },
//	          "arn": "arn:aws:s3:::example-bucket"
//	        },
//	        "object": {
//	          "key": "test%2Fkey",
//	          "size": 1024,
//	          "eTag": "0123456789abcdef0123456789abcdef",
//	          "sequencer": "0A1B2C3D4E5F678901"
//	        }
//	      }
//	    }
//	  ]
//	}    

    private static Integer createRawdoc(S3EventObject s3EventObject, RawdocStatus rawdocStatus) throws URISyntaxException, IOException, InterruptedException  {
    	JSONObject json = new JSONObject();
    	JSONObject file = new JSONObject();
    	file.put("s3Bucket", s3EventObject.getBucket());
    	file.put("s3Key", s3EventObject.getKey()); 
    	file.put("url", getDowloadURL(s3EventObject));
    	file.put("path", getDowloadURL(s3EventObject));
    	String contentType = solutions.aon.aws.s3.S3.getContentType(s3EventObject.getBucket(), s3EventObject.getKey());
    	file.put("content_type", contentType);
    	json.put(IJsonNames.FILE, file);
    	json.put(IJsonNames.STATUS, rawdocStatus.getTediName());
		JSONObject resp = AonInvofox.createRawdoc(s3EventObject.getDomain(), s3EventObject.getUser(), json);
    	return JsonUtils.getInteger(resp, IJsonNames.ID);
	}
    
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, NoSuchCompanyException, ParseException {
//      handleObject("aon-upload-post",  "");
    }
    
}
