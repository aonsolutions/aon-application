package solutions.aon.aws.s3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;

public class S3TestCase {
	
	private static final String TEST_BUCKET = "aon-aws-test-bucket";
	private static final String TEST_AON_TABLE = "aon-aws-test-table";	
	
	@Test
	public void testBucket() {	
		String bucket = TEST_BUCKET + UUID.randomUUID().toString().replace("-", "");
		if(S3.existBucket(bucket)) 
			S3.deleteBucket(bucket);
		
		boolean existBucket = S3.existBucket(bucket);
		assertEquals(false, existBucket);
		
		S3.createBucket(bucket);
		
		boolean existBucket2 = S3.existBucket(bucket);
		assertEquals(true, existBucket2);

		S3.deleteBucket(bucket);
	}
	
	@Test
	public void testBucketAonTable() {
		String aonTable = TEST_AON_TABLE + UUID.randomUUID().toString().replace("-", "");
		if(S3.existBucket(aonTable)) 
			S3.deleteBucket(aonTable);
		
		boolean existBucket = S3.existBucket(aonTable);
		assertEquals(false, existBucket);
		
		String bucket = S3.getAonTableBucket(aonTable);
		
		boolean existBucket2 = S3.existBucket(bucket);
		assertEquals(true, existBucket2);
		
		S3.deleteBucket(aonTable);
	}
	
	@Test
	public void testUploadFile() throws IOException {
		byte[] file = S3TestCase.class.getResourceAsStream("file1.pdf").readAllBytes();

		String bucket = TEST_BUCKET + UUID.randomUUID().toString().replace("-", "");
		String aonTable = TEST_AON_TABLE + UUID.randomUUID().toString().replace("-", "");
		
		if(!S3.existBucket(bucket)) 
			S3.createBucket(bucket);
		String key = S3.upload(bucket, file);
		
		boolean existObject = S3.existObject(bucket, key);
		assertEquals(true, existObject);
	
		String url = S3.getURL(bucket, key).toExternalForm();
		assertNotNull(url);
		assertNotEquals("", url);
		String downloadUrl = S3.getDownloadURL(bucket, key).toExternalForm();
		assertNotNull(downloadUrl);
		assertNotEquals("", downloadUrl);
		
		String aonTableBucket = S3.getAonTableBucket(aonTable);
		
		S3.copy(bucket, aonTableBucket, key, key);
		
		boolean existObject2 = S3.existObject(aonTableBucket, key);
		assertEquals(true, existObject2);
		
		String url2 = S3.getURL(aonTableBucket, key).toExternalForm();
		assertNotNull(url2);
		assertNotEquals("", url2);
		String downloadUrl2 = S3.getDownloadURL(aonTableBucket, key).toExternalForm();
		assertNotNull(downloadUrl2);
		assertNotEquals("", downloadUrl2);
		String downloadUrl3 = S3.getAonTableDownloadURL(aonTable, key).toExternalForm();
		assertNotNull(downloadUrl3);
		assertNotEquals("", downloadUrl3);
		
		S3.delete(bucket, key);
		S3.delete(aonTableBucket, key);
		
		boolean existObject3 = S3.existObject(bucket, key);
		assertEquals(false, existObject3);
		
		boolean existObject4 = S3.existObject(aonTableBucket, key);
		assertEquals(false, existObject4);
		
		S3.deleteBucket(bucket);
		S3.deleteBucket(aonTableBucket);
	}
	
	
}
