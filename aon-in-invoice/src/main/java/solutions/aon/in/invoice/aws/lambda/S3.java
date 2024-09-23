package solutions.aon.in.invoice.aws.lambda;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.json.JSONObject;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3 {
    
    private static final AmazonS3 AMAZON_S3 = AmazonS3ClientBuilder.standard().build();
    
    private static int JSON_INDENT_FACTOR = 1;
    
    private static AmazonS3 getAmazonS3(String bukectName) {
    	return AMAZON_S3;
    }
    
    public static String getCompanyName(String bucketName, String key) {
    	return getAmazonS3(bucketName).getObjectMetadata(bucketName, key).getUserMetadata().getOrDefault("company-name", "");
    }

    public static JSONObject getLoadBatchTask(String bucketName, String key) throws NoSuchLoadBatchException{
	
	try (S3Object s3Object = getAmazonS3(bucketName).getObject(bucketName, key);
		InputStream s3ObjectIn = s3Object.getObjectContent();
	) {
	    String jsonString = new String ( s3ObjectIn.readAllBytes(), StandardCharsets.UTF_8 );
	    return new JSONObject(jsonString);
	} catch (SdkClientException | IOException e) {
	    throw new NoSuchLoadBatchException(key);
	}  
    }

    public static void setLoadBatchTask(String bucketName, String key, JSONObject loadBatch ) {
	// String will be encoded to bytes with UTF-8 encoding.
	getAmazonS3(bucketName).putObject(bucketName, key, loadBatch.toString(JSON_INDENT_FACTOR));
    }
    
    public static URL getDownloadURL (String bucketName, String key) {
	return getAmazonS3(bucketName).getUrl(bucketName, key);
    }
    
    public static Date getFirstModifiedTime(String bucketName, String prefix ) {
	ObjectListing objects = getAmazonS3(bucketName).listObjects(bucketName, prefix);
	return objects.getObjectSummaries().stream().map(S3ObjectSummary::getLastModified).min(Date::compareTo).orElseThrow();
    }
    
    
    public static void main(String[] args) throws UnsupportedEncodingException {
	System.out.println(URLDecoder.decode("facturas/translogia.aonsolutions.org/B66941873/jgarcia/20231122122740/00_43PRO+Document.pdf_17841.pdf", "UTF-8"));
	//System.out.println(getCompanyName("aon-upload-post", "facturas/translogia.aonsolutions.org/B66941873/jgarcia/20231116111148/00_Conforama I.jpg"));
    }
    
}
