package aon.solutions.in.issues.aws.lambda;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.esferalia.aon.occam.api.model.type.MimeType;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import software.amazon.awssdk.services.s3.model.S3Object;
import solutions.aon.aws.s3.S3;
import solutions.aon.aws.s3.S3EventObject;

public class S3RequestHandler implements RequestHandler<Object, String> {
	
	
	private static final String FUNCTION_URL = "https://jkmoqwh5wdc2adjdbyjj2sc6eq0donyi.lambda-url.eu-west-1.on.aws"; 

	@Override
	public String handleRequest(Object input, Context context) {
		List<S3EventObject> s3EventObjects = S3EventObject.getS3EventObjects(input);
		s3EventObjects.forEach(S3RequestHandler::handleS3EventObject);
		return "That's all folks :-)";
	}

	static void handleS3EventObject(S3EventObject s3EventObject) {
//		try {
//			URL url = S3.getURL(s3EventObject.getBucket(), s3EventObject.getKey());
//
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	static Optional<String> getBody(MimeMessage mimeMessage, UnaryOperator<String> cidInlineHandler) {
		return getHtmlBody(mimeMessage, cidInlineHandler ).or( () -> getTextBody(mimeMessage));
	}

	static Optional<String> getTextBody(MimeMessage mimeMessage) {
		try {
			Collection<BodyPart> textParts = MimeMessageUtils.getBodyParts(mimeMessage,
					MimeMessageUtils.isMimeType(MimeType.TXT.getName()));
			for (BodyPart textPart : textParts) {
				Object content = textPart.getContent();
				if (isNotBlank(content)) {
					return Optional.of(content.toString());
				}
			}
		} catch (Exception e) {

		}
		return Optional.empty();
	}

	static Optional<String> getHtmlBody(MimeMessage mimeMessage, UnaryOperator<String> cidInlineHandler) {
		try {
			Collection<BodyPart> htmlParts = MimeMessageUtils.getBodyParts(mimeMessage,
					MimeMessageUtils.isMimeType(MimeType.HTML.getName()));
			for (BodyPart htmlPart : htmlParts) {
				try {
					String html = MimeMessageUtils.getHtml(htmlPart, cidInlineHandler);
					if (isNotBlank(html)) {
						return Optional.of(html);
					}
				} catch (MessagingException e) {
					continue;
				}
			}
		} catch (Exception e) {

		}
		return Optional.empty();
	}

	static boolean isNotBlank(Object content) {
		return content != null && content.toString().trim().length() > 0;
	}

	static MimeMessage getMimeMessage(String bucket, String key) throws IOException, MessagingException {
		try (InputStream is = new ByteArrayInputStream(S3.download(bucket, key))) {
			return MimeMessageUtils.getMimeMessage(is);
		}
	}

	public static void main(String[] args) throws IOException, MessagingException {
		String preffix = args[1];
		String bucket = args[0];
		
		List<S3Object> s3Objects = S3.listObjects(bucket, preffix);
		for (S3Object s3Object : s3Objects) {
			MimeMessage mimeMessage = getMimeMessage(bucket, s3Object.key());
			
			getBody(mimeMessage, cid -> String.format("%s/%s/%s/%s", FUNCTION_URL, bucket, s3Object.key(), cid) ).ifPresent(body -> {
				String outFile = String.format("%s/%s.html", args[2], new File(s3Object.key()).getName() );
				try ( Writer writer = new FileWriter(outFile, StandardCharsets.UTF_8 ) ){
					writer.write(body);
					System.out.print(outFile + " ");
				} catch ( Exception e ) {
					throw new RuntimeException(e);
				}
			});
		}
		System.out.println();
		
	}

}
