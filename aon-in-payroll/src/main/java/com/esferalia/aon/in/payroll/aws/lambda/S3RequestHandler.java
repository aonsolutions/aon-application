package com.esferalia.aon.in.payroll.aws.lambda;

import static java.lang.String.format;
import static solutions.aon.aws.s3.S3EventObject.getS3EventObjects;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.task.Task;

import net.aonsolutions.aon.api.AonTGSS;
import net.aonsolutions.aon.api.AonTask;
import solutions.aon.aws.s3.S3;
import solutions.aon.aws.s3.S3EventObject;

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
	List<S3EventObject> s3EventObjects = getS3EventObjects(input);
	

	s3EventObjects.forEach( S3RequestHandler::handleS3EventObject );
	
	return "That's all folks :-)";
    }
    
    static void handleS3EventObject(S3EventObject s3EventObject) {
	try {
	    String userLogin = s3EventObject.getUser();
	    String domainName = s3EventObject.getDomain();
	    URL url = S3.getURL(s3EventObject.getBucket(), s3EventObject.getKey());
	    
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

    static JSONObject getTask(S3EventObject s3EventObject) throws URISyntaxException, IOException, InterruptedException {
	String loadTaskKey = getLoadTaskKey(s3EventObject);
	JSONObject loadTaskJSON = newLoadTask(LOAD_TASK_WAIT_ID);
	try {
	    String loadTaskId  = LOAD_TASK_WAIT_ID;
	    while (LOAD_TASK_WAIT_ID.equals(loadTaskId)) {
		Thread.sleep(Math.min(s3EventObject.getOrder() * 1000L, MAX_SLEEP_TIME)); 
		loadTaskJSON = getLoadTask(s3EventObject.getBucket(), loadTaskKey);
		loadTaskId = getLoadTaskId(loadTaskJSON);
	    }
	} catch (NoSuchLoadTaskException e) {
	    // Write semaphore, if present other lambdas must wait.  
	    setLoadTask(s3EventObject.getBucket(), loadTaskKey, loadTaskJSON);
	    
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
	} catch (SdkClientException | IOException e) {
	    throw new NoSuchLoadTaskException(key);
	}  
    }
    
    static void setLoadTask(String bucketName, String key, JSONObject loadTaskJson ) throws JSONException, IOException {
	    S3.upload(bucketName, key, loadTaskJson.toString(JSON_INDENT_FACTOR).getBytes());
    }
    

    static String getLoadTaskKey(S3EventObject s3Object) {
	return s3Object.getPrefix() + "/" + s3Object.getDomain() + "/" + s3Object.getDocument() + "/" + s3Object.getUser() + "/" + s3Object.getJob() + "/" + "loadtask";
    }
    
    
    
    
}