package solutions.aon.in.invoice.aws.lambda;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONException;
import org.json.JSONObject;

import solutions.aon.aws.s3.S3;

public class S3Invoice {
    
	private static final String COMPANY_NAME = "company-name";
    
    private static int JSON_INDENT_FACTOR = 1;
      
    public static String getCompanyName(String bucketName, String key) {
    	return S3.getObjectMetadata(bucketName, key).getOrDefault(COMPANY_NAME, "");
    }

    public static JSONObject getLoadBatchTask(String bucketName, String key) throws NoSuchLoadBatchException, IOException{
		byte[] loadBatch = S3.download(bucketName, key);
		String jsonString = new String ( loadBatch, StandardCharsets.UTF_8 );
		return new JSONObject(jsonString);  
    }

    public static void setLoadBatchTask(String bucketName, String key, JSONObject loadBatch ) throws JSONException, IOException {
    	S3.upload(bucketName, key, loadBatch.toString(JSON_INDENT_FACTOR));
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
    	System.out.println(URLDecoder.decode("facturas/translogia.aonsolutions.org/B66941873/jgarcia/20231122122740/00_43PRO+Document.pdf_17841.pdf", "UTF-8"));
    	//System.out.println(getCompanyName("aon-upload-post", "facturas/translogia.aonsolutions.org/B66941873/jgarcia/20231116111148/00_Conforama I.jpg"));
    }
}
