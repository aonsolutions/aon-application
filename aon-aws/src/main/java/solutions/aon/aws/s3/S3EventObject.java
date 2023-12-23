package solutions.aon.aws.s3;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class S3EventObject {
    
    
    private int order;

    private String key;
    private Date  time;
    private Integer size;
    private String bucket;
    
    private String job;
    private String user;
    private String domain;
    private String document;
    private String prefix;
    private String fileName;
    
    private static Date getEventTime(Map<String, ?> record) {
	String eventTime =  (String) record.get("eventTime");
	try {
	    return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(eventTime);
	} catch (ParseException e) {
	    return null;
	}
    }

    public static List<S3EventObject> getS3EventObjects(Object input) {
	
	List<S3EventObject> s3Objects = new ArrayList<>();
	
	Map<String, ?> map = (Map<String, ?>) input;
	List<Map<String, ?>> records = (List<Map<String, ?>>) map.get("Records");

	for (Map<String, ?> record : records) {
	    Map<String, ?> s3 = (Map<String, ?>) record.get("s3");
	    
	    Map<String, ?> bucket = (Map<String, ?>) s3.get("bucket");
	    String bucketName = (String) bucket.get("name");
	    
	    Map<String, ?> object = (Map<String, ?>) s3.get("object");
	    
	    String objectKey;
	    try {
		objectKey = URLDecoder.decode((String) object.get("key"), "UTF-8" );
	    } catch (UnsupportedEncodingException e) {
		objectKey = (String) object.get("key");
	    }
	    Integer objectSize = ( Integer ) object.get("size");
	    
	    S3EventObject s3Object = new S3EventObject();
	    s3Object.setKey(objectKey);
	    s3Object.setSize(objectSize);
	    s3Object.setBucket(bucketName);

	    // key = <invoices>/<domain>/<document>/<user>/<job>/<order>_<filename>
	    String [] paths = objectKey.split("/");
	    s3Object.setPrefix(paths[0]);
	    s3Object.setDomain(paths[1]);
	    s3Object.setDocument(paths[2]);
	    s3Object.setUser(paths[3]);
	    s3Object.setJob(paths[4]);
	    s3Object.setFileName(paths[5]);
	    s3Object.setOrder(Integer.parseInt(s3Object.getFileName().substring(0, 2)));
	    
	    
	    Date eventTime = getEventTime(record);
	    s3Object.setTime(eventTime);
	    
	    s3Objects.add(s3Object);
	    
	}

	return s3Objects;
    }

   public String getKey() {
	return key;
    }

    public void setKey(String key) {
	this.key = key;
    }

    public Date getTime() {
	return time;
    }

    public void setTime(Date time) {
	this.time = time;
    }

    public Integer getSize() {
	return size;
    }

    public void setSize(Integer size) {
	this.size = size;
    }

    public String getBucket() {
	return bucket;
    }

    public void setBucket(String bucket) {
	this.bucket = bucket;
    }

    public String getJob() {
	return job;
    }

    public void setJob(String job) {
	this.job = job;
    }

    public String getUser() {
	return user;
    }

    public void setUser(String user) {
	this.user = user;
    }

    public String getDomain() {
	return domain;
    }

    public void setDomain(String domain) {
	this.domain = domain;
    }

    public String getDocument() {
	return document;
    }

    public void setDocument(String document) {
	this.document = document;
    }

    public String getPrefix() {
	return prefix;
    }

    public void setPrefix(String prefix) {
	this.prefix = prefix;
    }

    public String getFileName() {
	return fileName;
    }

    public void setFileName(String fileName) {
	this.fileName = fileName;
    }

    public int getOrder() {
	return order;
    }

    public void setOrder(int order) {
	this.order = order;
    }
}