package aon.solutions.in.issues.aws.lambda;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import jakarta.mail.internet.MimeMessage;
import solutions.aon.aws.s3.S3;

public class SESRequestHandler implements RequestHandler<Object, String> {
	
	
	private static final String FUNCTION_URL = "https://jkmoqwh5wdc2adjdbyjj2sc6eq0donyi.lambda-url.eu-west-1.on.aws"; 

	static class SESEventObject {

//		{
//			  "Records": [
//			    {
//			      "eventSource": "aws:ses",
//			      "eventVersion": "1.0",			
//			      "ses": {
//			        "mail": {
//			          "commonHeaders": {
//			            "date": "Wed, 7 Oct 2015 12:34:56 -0700",
//			            "from": [
//			              "Jane Doe <janedoe@example.com>"
//			            ],
//			            "messageId": "<0123456789example.com>",
//			            "returnPath": "janedoe@example.com",
//			            "subject": "Test Subject",
//			            "to": [
//			              "johndoe@example.com"
//			            ]
//			          },
//			          "destination": [
//			            "johndoe@example.com"
//			          ],
//			          "headers": [
//			            ... headers data ...
//			          ],
//			          "headersTruncated": false,
//			          "messageId": "o3vrnil0e2ic28trm7dfhrc2v0clambda4nbp0g1",
//			          "source": "janedoe@example.com",
//			          "timestamp": "1970-01-01T00:00:00.000Z"
//			        },
//			        "receipt": {
//			          ... receipt data ...
//			        }			String key = "soporte@aon.solutions/" + sesEventObject.getMessageId();

//			      }
//			    }
//			  ]
//			}
		private String messageId;

		public SESEventObject() {
			super();
		}

		public String getMessageId() {
			return messageId;
		}
		
		public SESEventObject setMessageId(String messageId) {
			this.messageId = messageId;
			return this;
		}

		public void setMessageId(String messageId, SESEventObject s3UploadEventObject) {
			this.messageId = messageId;
		}

		
		public static List<Map<String, ?>> getRecords(Object input) {
			Map<String, ?> map = (Map<String, ?>) input;
			return (List<Map<String, ?>>) map.get("Records");
		}
	 	
	    public static List<SESEventObject> getSESEventObjects(Object input) {
			List<SESEventObject> sesObjects = new ArrayList<>();
		
			List<Map<String, ?>> records = getRecords(input);
			
			for (Map<String, ?> record : records) {
				Map<String, ?> ses = (Map<String, ?>) record.get("ses");
		    
				Map<String, ?> mail = (Map<String, ?>) ses.get("mail");
				String messageId = (String) mail.get("messageId");
		    
				SESEventObject sesObject = new SESEventObject();
				sesObject.setMessageId(messageId, sesObject);
		    	sesObjects.add(sesObject);
			}
			return sesObjects;
	    }
		
	}

	@Override
	public String handleRequest(Object input, Context context) {
		List<SESEventObject> sesEventObjects = SESEventObject.getSESEventObjects(input);
		sesEventObjects.forEach(SESRequestHandler::handleSESEventObject);
		return "That's all folks :-)";
	}

	static void handleSESEventObject(SESEventObject sesEventObject) {
		String bucket = "aon-ses-inbox";
		String key = "soporte@aon.solutions/" + sesEventObject.getMessageId();

		try {
			S3 s3 = S3.getInstance();
			byte[] data = s3.download(bucket, key);
			MimeMessage mimeMessage = MimeMessageUtils.getMimeMessage(data);
			MimeMessage2Task.addTask(mimeMessage, cid -> String.format("%s/%s/%s/%s", FUNCTION_URL, bucket, key, cid));

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error processing task for " + key + ": " + e.getMessage());
		}

	}
	
	//o54dkhi8lm6bnccoq7s3u4aidm62nivuqv35cq01
	
	public static void main(String[] args) {
		handleSESEventObject(new SESEventObject().setMessageId("o54dkhi8lm6bnccoq7s3u4aidm62nivuqv35cq01"));
	}

}
