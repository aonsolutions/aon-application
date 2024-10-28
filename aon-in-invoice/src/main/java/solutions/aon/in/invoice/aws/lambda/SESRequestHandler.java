package solutions.aon.in.invoice.aws.lambda;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import solutions.aon.aws.s3.S3;

public class SESRequestHandler implements RequestHandler<Object, String> {

	private static final String S3_SES_BUCKET = "aon-ses-inbox";
	private static final String INVOICE_DIR = "facturas@aon.solutions";

	@Override
	public String handleRequest(Object input, Context context) {
		List<String> messageIds = getMessageIds(input);
		for (String messageId : messageIds) {
			try {
				handleMessage(messageId);
			} catch (NoSuchCompanyException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
		return "That's all folks :-)";
	}

	private static void handleMessage(String messageId)
			throws URISyntaxException, InterruptedException, NoSuchCompanyException, IOException, MessagingException {
		MimeMessage mimeMessage = getMimeMessage(messageId);

		Address sender = mimeMessage.getSender();
		String company = getCompany("B01487271");

		Collection<String> paths = new ArrayList<>();
		Multipart multipart = (Multipart) mimeMessage.getContent();
		for (int i = 0; i < multipart.getCount(); i++) {
			BodyPart bodyPart = multipart.getBodyPart(i);
			paths.addAll(getPaths(bodyPart));
		}
	}

	private static String getCif(Address address) {
		return "B01487271";
	}

	private static String getCompany(String cif)
			throws URISyntaxException, IOException, InterruptedException, NoSuchCompanyException {
		JSONObject companies = null; // get("companies", Collections.singletonMap("taxId", cif));
		int count = companies.getInt("count");
		if (count > 0) {
			JSONArray result = companies.getJSONArray("result");
			JSONObject company = result.getJSONObject(0);
			return company.getString("_id");
		}
		throw new NoSuchCompanyException(String.format("No company with taxId '%s'", cif));
	}

	// PDFs: .pdf
	// Compressed files: .zip.
	// Images: .jpeg, .jpg, .png, .tiff, .gif
	private static boolean isValid(BodyPart bodyPart) throws MessagingException {
		String contentType = bodyPart.getContentType();
		if (contentType != null && (contentType.matches(".+/(pdf|zip)") || contentType.matches("image/.+"))) {
			return true;

		}

		String fileName = bodyPart.getFileName();
		if (fileName != null
				&& (fileName.matches(".+\\.(pdf|zip)") || fileName.matches(".+\\.(jpeg|jpg|png|tiff|gif)"))) {
			return true;

		}

		return false;
	}

	private static Collection<String> getPaths(BodyPart bodyPart) throws IOException, MessagingException {
		if (!isValid(bodyPart))
			return Collections.emptySet();
		return Collections.singleton(getPath(bodyPart));
	}

	private static String getPath(BodyPart bodyPart) throws IOException, MessagingException {
		if (bodyPart instanceof MimeBodyPart mimeBodyPart) {
			return mimeBodyPart.getContentID().replaceAll("[<>]", "");
		} else {
			return bodyPart.getFileName();
		}
	}

	private static MimeMessage getMimeMessage(String messageId) throws IOException, MessagingException {
		String key = String.format("%s/%s", INVOICE_DIR, messageId);
		byte[] data = S3.download(S3_SES_BUCKET, key);

		try (InputStream is = new ByteArrayInputStream(data)) {
			Session session = Session.getInstance(System.getProperties());
			return new MimeMessage(session, is);
		}
	}

	private static List<String> getMessageIds(Object input) {

		List<String> messageIds = new ArrayList<>();

		Map<String, ?> map = (Map<String, ?>) input;
		List<Map<String, ?>> records = (List<Map<String, ?>>) map.get("Records");

		for (Map<String, ?> record : records) {
			Map<String, ?> ses = (Map<String, ?>) record.get("ses");
			Map<String, ?> mail = (Map<String, ?>) ses.get("mail");
			messageIds.add((String) mail.get("messageId"));
		}

		return messageIds;
	}
}
