package solutions.aon.aws.s3;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class S3UploadEventObject extends S3EventObject {
    
    private int order;

    private String job;
    private String user;
    private String domain;
    private String document;
    private String activity;
    private String prefix;
    private String fileName;
    private boolean camera;
    private boolean signed;
        
    public static List<S3UploadEventObject> getS3UploadEventObjects(Object input) {
		List<S3UploadEventObject> s3Objects = new ArrayList<>();
	
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
	    
			S3UploadEventObject s3Object = new S3UploadEventObject();
			s3Object.setKey(objectKey, s3Object);
			s3Object.setSize(objectSize, s3Object);
			s3Object.setBucket(bucketName, s3Object);	

			// key = <invoices>/<domain>/<document>/<user>/<job>/<order>_?<CM>_<filename>
			// key with activity = <invoices>/<domain>/<document>/<activity>/<user>/<job>/<order>_?<CM>_<filename>
			// ?<CM> --> identifica si viene o no de la camara.
			String [] paths = objectKey.split("/");
			boolean withActivity = paths.length == 7;
			s3Object.setPrefix(paths[0]);
			s3Object.setDomain(paths[1]);
			s3Object.setDocument(paths[2]);
			if(withActivity) s3Object.setActivity(paths[3]);
			s3Object.setUser(paths[withActivity ? 4 : 3]);
			s3Object.setJob(paths[withActivity ? 5 :4]);
			s3Object.setFileName(paths[withActivity ? 6 : 5]);
			s3Object.setOrder(Integer.parseInt(s3Object.getFileName().substring(0, 2)));
			String[] array = s3Object.getFileName().split("_");
			s3Object.setCamera(array.length > 2 && "CM".equals(array[1]));
	    	Date eventTime = getEventTime(record);
	    	s3Object.setTime(eventTime, s3Object);
	    	s3Objects.add(s3Object);
		}
		return s3Objects;
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

    public String getActivity() {
		return activity;
	}
    
    public void setActivity(String activity) {
		this.activity = activity;
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
    
    public boolean isCamera() {
		return camera;
	}
    
    public void setCamera(boolean camera) {
		this.camera = camera;
	}
 
    public boolean isSigned() {
		return signed;
	}
    
    public void setSigned(boolean signed) {
		this.signed = signed;
	}
    
    public boolean isBidoq() {
		return getFileName().contains("bidoq");
	}
}