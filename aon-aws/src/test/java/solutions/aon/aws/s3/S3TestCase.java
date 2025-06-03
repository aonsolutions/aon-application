package solutions.aon.aws.s3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;

public class S3TestCase {
	
	private static final String TEST_BUCKET = "aon-aws-test-bucket";
	private static final String TEST_AON_TABLE = "aon-aws-test-table";	
	
	@Test
	@Ignore
	public void testBucket() {	
		String bucket = TEST_BUCKET + UUID.randomUUID().toString().replace("-", "");
		S3 s3 = S3.getInstance();
		if(s3.existBucket(bucket)) 
			s3.deleteBucket(bucket);
		
		boolean existBucket = s3.existBucket(bucket);
		assertEquals(false, existBucket);
		
		s3.createBucket(bucket);
		
		boolean existBucket2 = s3.existBucket(bucket);
		assertEquals(true, existBucket2);

		s3.deleteBucket(bucket);
	}
	
	@Test
	@Ignore
	public void testBucketAonTable() {
		String aonTable = TEST_AON_TABLE + UUID.randomUUID().toString().replace("-", "");
		S3 s3 = S3.getInstance();
		if(s3.existBucket(aonTable)) 
			s3.deleteBucket(aonTable);
		
		boolean existBucket = s3.existBucket(aonTable);
		assertEquals(false, existBucket);
		
		String bucket = s3.getAonTableBucket(aonTable);
		
		boolean existBucket2 = s3.existBucket(bucket);
		assertEquals(true, existBucket2);
		
		s3.deleteBucket(aonTable);
	}
	
	@Test
	public void testUploadFile() throws IOException {
		byte[] file = S3TestCase.class.getResourceAsStream("file1.pdf").readAllBytes();

		String bucket = TEST_BUCKET + UUID.randomUUID().toString().replace("-", "");
		String aonTable = TEST_AON_TABLE + UUID.randomUUID().toString().replace("-", "");
		S3 s3 = S3.getInstance();
		if(!s3.existBucket(bucket)) 
			s3.createBucket(bucket);
		String key = s3.upload(bucket, file);
		
		boolean existObject = s3.existObject(bucket, key);
		assertEquals(true, existObject);
	
		String url = s3.getURL(bucket, key).toExternalForm();
		assertNotNull(url);
		assertNotEquals("", url);
		String downloadUrl = s3.getDownloadURL(bucket, key).toExternalForm();
		assertNotNull(downloadUrl);
		assertNotEquals("", downloadUrl);
		
		String aonTableBucket = s3.getAonTableBucket(aonTable);
		
		s3.copy(bucket, aonTableBucket, key, key);
		
		boolean existObject2 = s3.existObject(aonTableBucket, key);
		assertEquals(true, existObject2);
		
		String url2 = s3.getURL(aonTableBucket, key).toExternalForm();
		assertNotNull(url2);
		assertNotEquals("", url2);
		String downloadUrl2 = s3.getDownloadURL(aonTableBucket, key).toExternalForm();
		assertNotNull(downloadUrl2);
		assertNotEquals("", downloadUrl2);
		String downloadUrl3 = s3.getAonTableDownloadURL(aonTable, key).toExternalForm();
		assertNotNull(downloadUrl3);
		assertNotEquals("", downloadUrl3);
		
		s3.delete(bucket, key);
		s3.delete(aonTableBucket, key);
		
		boolean existObject3 = s3.existObject(bucket, key);
		assertEquals(false, existObject3);
		
		boolean existObject4 = s3.existObject(aonTableBucket, key);
		assertEquals(false, existObject4);
		
		s3.deleteBucket(bucket);
		s3.deleteBucket(aonTableBucket);
	}
	
	
}
