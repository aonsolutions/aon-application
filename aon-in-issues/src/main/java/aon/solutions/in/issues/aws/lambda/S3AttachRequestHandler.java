package aon.solutions.in.issues.aws.lambda;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import solutions.aon.aws.s3.S3;

public class S3AttachRequestHandler implements RequestStreamHandler {

	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		try {
			String event = new String(input.readAllBytes());

			List<String> paths = getPaths(event);
			String bucket = paths.getFirst();
			String key = paths.subList(1, paths.size() - 1).stream().collect(Collectors.joining("/"));
			String attachId = paths.getLast();

			byte[] data = S3.download(bucket, key);

			Session session = Session.getDefaultInstance(System.getProperties());
			MimeMessage mimeMessage = new MimeMessage(session, new ByteArrayInputStream(data));

			InputStream attachInput = getAttachContent(mimeMessage, attachId);
			byte[] buffer = new byte[1024];
			for (int read = attachInput.read(buffer); read > 1; read = attachInput.read(buffer)) {
				output.write(buffer, 0, read);
			}

		} catch (MessagingException e) {
			throw new IOException(e);
		} finally {
			output.close();
			input.close();
		}
	}

	private static List<String> getPaths(String input) {
		String path = getParam(input, "path");
		return Arrays.stream(path.split("/")).filter(s -> !s.isBlank()).toList();
	}

	private static String getParam(String input, String param) {
		Pattern pattern = Pattern.compile("\\\"" + param + "\\\"\s*:\s*\\\"(?<" + param + ">[^\\\"]*)\\\"");
		Matcher matcher = pattern.matcher(input);
		matcher.find();
		return matcher.group(param);
	}

	private static InputStream getAttachContent(MimeMessage mimeMessage, String attachId)
			throws IOException, MessagingException {
		Collection<BodyPart> attachParts = MimeMessageUtils.getBodyParts(mimeMessage,
				bodyPart -> isThisBodyPart(bodyPart, attachId));
		for (BodyPart attachPart : attachParts) {
			return attachPart.getInputStream();
		}
		throw new IOException("");
	}

	private static boolean isThisBodyPart(BodyPart bodyPart, String attachId) {
		try {
			// Content-ID || X-Attachment-Id || filename
			return isContentID(bodyPart, attachId) || isXAttachmentId(bodyPart, attachId)
					|| isFilename(bodyPart, attachId);
		} catch (MessagingException e) {
		}
		return false;
	}

	private static boolean isFilename(BodyPart bodyPart, String attachId) throws MessagingException {
		return attachId.equalsIgnoreCase(bodyPart.getFileName());
	}

	private static boolean isContentID(BodyPart bodyPart, String attachId) throws MessagingException {
		return MimeMessageUtils.getContentID(bodyPart).map(contentID -> contentID.equalsIgnoreCase(attachId))
				.orElse(false);
	}

	private static boolean isXAttachmentId(BodyPart bodyPart, String attachId) throws MessagingException {
		String[] xAttachmentIds = bodyPart.getHeader("X-Attachment-Id");
		return xAttachmentIds != null && Arrays.stream(xAttachmentIds).filter(Objects::nonNull)
				.anyMatch(header -> header.equalsIgnoreCase(attachId));
	}

	private static String convertToMD5(InputStream message) throws NoSuchAlgorithmException, IOException {

		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(message.readAllBytes());
		String myHash = bytesToHex(digest).toUpperCase();

		return myHash;

	}

	private static String bytesToHex(byte[] bytes) {
		char[] hexArr = "0123456789ABCDEF".toCharArray();
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArr[v >>> 4];
			hexChars[j * 2 + 1] = hexArr[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static void main(String[] args) throws IOException {

		ByteArrayInputStream input = new ByteArrayInputStream(
				"\"path\": \"aon-ses-inbox/soporte-aonsolutions.eu/1h2cb8hqvd2043eq81g9drpucm22oevtlk9o7f81/image001.jpg@01DB6E5A.B84456C0\""
						.getBytes());

		new S3AttachRequestHandler().handleRequest(input, System.out, null);
	}

}
