package aon.solutions.in.issues.aws.lambda;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.internet.ContentType;
import jakarta.mail.internet.MimeMessage;

public class MimeMessageUtils {

	// Content-ID: <7b0a0d2d-ee63-4482-80d4-224232db4db5>
	private static final Pattern _CID_HEADER = Pattern.compile("(\\s*<)?(?<cid>[a-z0-9-]+)(>\\s*)?",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	// cid:7b0a0d2d-ee63-4482-80d4-224232db4db5
	private static final Pattern CID_INLINE = Pattern.compile("cid\\s*:\\s*(?<cid>[a-z0-9-]+)",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

	private static final Pattern ATTACHMENT = Pattern.compile("\\s*attachment.*",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

	private interface ExceptionFunction<T, R, E extends Exception> {
		R apply(T t) throws E;
	}

	public static boolean isAttachment(BodyPart bodyPart) {
		try {
			String[] contentDispositions = bodyPart.getHeader("Content-Disposition");

			return allMatch(contentDispositions, c -> ATTACHMENT.matcher(c).matches());
		} catch (MessagingException e) {
		}
		return false;
	}

	public static Predicate<BodyPart> isMimeType(String... mimeTypes) {
		return bodyPart -> {
			try {
				for (String mimeTye : mimeTypes) {
					if (bodyPart.isMimeType(mimeTye))
						return true;
				}
			} catch (MessagingException e) {
			}
			return false;
		};
	}

	public static String getHtml(BodyPart bodyPart, UnaryOperator<String> cidInlineHandler)
			throws IOException, MessagingException {
		Object content = bodyPart.getContent();
		String htmlContent = content.toString();
		StringBuilder htmlBuilder = new StringBuilder();
		Map<String, String> cidMap = new HashMap<>();

		Matcher cidInlineMatcher = CID_INLINE.matcher(htmlContent);
		int last;
		for (last = 0; cidInlineMatcher.find(); last = cidInlineMatcher.end()) {
			String cid = cidInlineMatcher.group("cid");
			String url = cidMap.computeIfAbsent(cid, cidInlineHandler::apply);

			htmlBuilder.append(htmlContent.substring(last, cidInlineMatcher.start()));
			htmlBuilder.append(url);
		}
		htmlBuilder.append(htmlContent.substring(last));

		return htmlBuilder.toString();
	}

	public static Optional<String> getDataURL(String contentId, Part part) throws MessagingException, IOException {
		Optional<BodyPart> bodyPart = getBodyParts(part, filteringByContentID(contentId)).stream().findFirst();
		return bodyPart.isPresent() ? Optional.ofNullable(getDataURL(bodyPart.get())) : Optional.empty();
	}

	public static String getDataURL(BodyPart bodyPart) throws MessagingException, IOException {
		StringBuilder dataURLBuilder = new StringBuilder().append("data:").append(getMimeType(bodyPart))
				.append(";base64,");
		try (InputStream is = bodyPart.getInputStream()) {
			byte[] bytes = is.readAllBytes();
			String base64 = Base64.getEncoder().encodeToString(bytes);
			dataURLBuilder.append(base64);
		}
		return dataURLBuilder.toString();
	}

	public static String getMimeType(BodyPart bodyPart) throws MessagingException {
		return new ContentType(bodyPart.getContentType()).getBaseType();
	}

	public static MimeMessage getMimeMessage(InputStream is) throws MessagingException {
		Properties properties = new Properties();
		Session session = Session.getDefaultInstance(properties);
		return new MimeMessage(session, is);
	}

	public static MimeMessage getMimeMessage(byte[] data) throws MessagingException, IOException {
		try (InputStream is = new java.io.ByteArrayInputStream(data)) {
			return getMimeMessage(is);
		}
	}


	public static Collection<BodyPart> getBodyParts(Part part, Predicate<BodyPart> filter)
			throws IOException, MessagingException {
		if (part instanceof BodyPart bodyPart) {
			return getBodyParts(bodyPart, filter);
		}
		if (part.getContent() instanceof Multipart multipart) {
			return getBodyParts(multipart, filter);
		}
		return Collections.emptyList();

	}

	public static Collection<BodyPart> getBodyParts(BodyPart bodyPart, Predicate<BodyPart> filter)
			throws IOException, MessagingException {
		if (filter.test(bodyPart)) {
			return Collections.singleton(bodyPart);
		}
		if (bodyPart.getContent() instanceof Multipart multipart) {
			return getBodyParts(multipart, filter);
		}
		return Collections.emptyList();

	}

	public static Collection<BodyPart> getBodyParts(Multipart multiPart, Predicate<BodyPart> filter)
			throws MessagingException, IOException {
		List<BodyPart> bodyParts = new ArrayList<>();
		for (int i = 0; i < multiPart.getCount(); i++) {
			bodyParts.addAll(getBodyParts(multiPart.getBodyPart(i), filter));
		}
		return Collections.unmodifiableCollection(bodyParts);
	}

	public static Optional<String> getContentID(BodyPart bodyPart) throws MessagingException {

		String[] contentIDs = bodyPart.getHeader("Content-ID");
		if (contentIDs != null) {
			for (int i = 0; i < contentIDs.length; i++) {
				String contentID = contentIDs[i];
				contentID = AonStringUtils.trimToEmpty(contentID);
				contentID = AonStringUtils.removeStart(contentID, "<");
				contentID = AonStringUtils.removeEnd(contentID, ">");
				if ( AonStringUtils.isBlank(contentID)) {
					continue;
				}
				return Optional.of(contentID.trim());
			}
		}

		return Optional.empty();

	}

	public static Predicate<BodyPart> filteringByContentID(String contentID) {
		return safeFunction(MimeMessageUtils::getContentID, Optional.empty()).andThen(id -> id.map(Object::toString))
				.andThen(id -> id.map(contentID::equalsIgnoreCase)).andThen(equals -> equals.orElse(false))::apply;
	}

	static <T, R> Function<T, R> safeFunction(ExceptionFunction<T, R, ?> function, R r) {
		return t -> {
			try {
				return function.apply(t);
			} catch (Exception e) {
				return r;
			}
		};
	}

	private static <T> boolean allMatch(T[] array, Predicate<T> predicate) {
		return array != null && Arrays.stream(array).filter(Objects::nonNull).allMatch(predicate);
	}

}
