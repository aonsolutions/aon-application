package com.esferalia.aon.in.payroll.aws.lambda;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import net.aonsolutions.aon.api.AonTGSS;
import solutions.aon.aws.s3.S3;
import solutions.aon.aws.s3.S3UploadEventObject;

public class S3RequestHandler implements RequestHandler<Object, String> {

    static final String _ID = "_id";

    static final int MAX_SLEEP_TIME = 2000;
    private static int JSON_INDENT_FACTOR = 1;
    static final String LOAD_TASK_WAIT_ID = "loadTaskId";

    static final String WORKGROUP = "SEGURIDAD SOCIAL";
    static final String TITLE = "Se han subido documentos de facturas desde '%s'.";
    static final String DESCRIPTION = "La empresa '%s' ha subido facturas para su procesamiento y validación.";
    
    static class NoSuchLoadTaskException extends Exception {
	
	public NoSuchLoadTaskException(String key) {
	    super(key);
	}
	
    }

    @Override
    public String handleRequest(Object input, Context context) {
	List<S3UploadEventObject> s3EventObjects = S3UploadEventObject.getS3UploadEventObjects(input);
	

	s3EventObjects.forEach( S3RequestHandler::handleS3EventObject );
	
	return "That's all folks :-)";
    }
    
    static void handleS3EventObject(S3UploadEventObject s3UploadEventObject) {
	try {
	    String userLogin = s3UploadEventObject.getUser();
	    String domainName = s3UploadEventObject.getDomain();
	    URL url = S3.getURL(s3UploadEventObject.getBucket(), s3UploadEventObject.getKey());
	    
	    AonTGSS.loadIvlccc(domainName, userLogin, url.toExternalForm());

	} catch (URISyntaxException e) {
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	} 
    }

    static JSONObject getTask(S3UploadEventObject s3UploadEventObject) throws URISyntaxException, IOException, InterruptedException {
	String loadTaskKey = getLoadTaskKey(s3UploadEventObject);
	JSONObject loadTaskJSON = newLoadTask(LOAD_TASK_WAIT_ID);
	try {
	    String loadTaskId  = LOAD_TASK_WAIT_ID;
	    while (LOAD_TASK_WAIT_ID.equals(loadTaskId)) {
		Thread.sleep(Math.min(s3UploadEventObject.getOrder() * 1000L, MAX_SLEEP_TIME)); 
		loadTaskJSON = getLoadTask(s3UploadEventObject.getBucket(), loadTaskKey);
		loadTaskId = getLoadTaskId(loadTaskJSON);
	    }
	} catch (NoSuchLoadTaskException e) {
	    // Write semaphore, if present other lambdas must wait.  
	    setLoadTask(s3UploadEventObject.getBucket(), loadTaskKey, loadTaskJSON);
	    
//	    Task task = new Task()
//		    .setTitle(format(TITLE, companyName))
//		    .setDescription(format(DESCRIPTION, companyName ))
//		    .setWorkgroup(new Workgroup().setDescription(WORKGROUP));
//	    
//	    loadTaskJSON =  AonTask.newTask(s3EventObject.getDomain(), s3EventObject.getUser(), task );
//	    setLoadTask(s3EventObject.getBucket(), loadTaskKey, loadTaskJSON);	    
	    
	}
	return loadTaskJSON;
    }
    
    
    static JSONObject newLoadTask(String loadTaskId) {
	JSONObject loadTaskJSON = new JSONObject();
	loadTaskJSON.put(_ID, loadTaskId);
	return loadTaskJSON;
    }
    
    static String getLoadTaskId(JSONObject loadTaskJSON ) {
	return loadTaskJSON.getString(_ID);
    }
    
    static JSONObject getLoadTask(String bucketName, String key) throws NoSuchLoadTaskException{
	
	try{
	    byte [] allBytes = S3.download(bucketName, key);
	    String jsonString = new String ( allBytes, StandardCharsets.UTF_8 );
	    return new JSONObject(jsonString);
	} catch (Exception e) {
	    throw new NoSuchLoadTaskException(key);
	}  
    }
    
    static void setLoadTask(String bucketName, String key, JSONObject loadTaskJson ) throws JSONException, IOException {
	    S3.upload(bucketName, key, loadTaskJson.toString(JSON_INDENT_FACTOR).getBytes());
    }
    

    static String getLoadTaskKey(S3UploadEventObject s3UploadEventObject) {
	return s3UploadEventObject.getPrefix() + "/" + s3UploadEventObject.getDomain() + "/" + s3UploadEventObject.getDocument() + "/" + s3UploadEventObject.getUser() + "/" + s3UploadEventObject.getJob() + "/" + "loadtask";
    }
    
    
    
    
}