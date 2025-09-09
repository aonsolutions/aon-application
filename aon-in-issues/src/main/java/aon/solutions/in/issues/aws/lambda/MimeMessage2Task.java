package aon.solutions.in.issues.aws.lambda;

import static aon.solutions.in.issues.aws.lambda.MimeMessageUtils.getMimeMessage;
import static com.esferalia.aon.watson.server.AonObjectUtils.firstNonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import com.esferalia.aon.occam.api.json.TaskWorkflowJSON;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskSource;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.api.model.task.TaskWorkflowType;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import net.aonsolutions.aon.api.AonCustomer;
import net.aonsolutions.aon.api.AonTask;
import software.amazon.awssdk.services.s3.model.S3Object;
import solutions.aon.aws.s3.S3;

public class MimeMessage2Task {

	private static final Pattern EMAIL_PATTERN = Pattern
			.compile("(?<user>[A-Z0-9._%-]+)@[A-Z0-9.-]+\\.[A-Z]{2,4}", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	private static final String IN_REPLY_TO = "In-Reply-To";
	private static final String LOGIN = "admin";
	private static final String DOMAIN_NAME = "sig.aonsolutions.org";
	private static final String FUNCTION_URL = "https://jkmoqwh5wdc2adjdbyjj2sc6eq0donyi.lambda-url.eu-west-1.on.aws";

	public record EmailCustomer(String email, Customer customer) {
	}

	public static Task newTask(MimeMessage mimeMessage, UnaryOperator<String> cidInlineHandler)
			throws MessagingException, IOException {
		
		Date sendDate = mimeMessage.getSentDate();
		
		
		Task task = new Task()
				.setSource(TaskSource.CAU)
				.setWorkflows(new ArrayList<>())
				.setTitle(mimeMessage.getSubject())
				.setStartDate(mimeMessage.getSentDate())
				.setCreationDate(mimeMessage.getSentDate())
				.setSourceId(mimeMessage.getMessageID().hashCode())
				;

		Optional<String> from = 
				Arrays.stream(mimeMessage.getFrom()).map(MimeMessage2Task::getEmail).findFirst();
		//Optional<String> user = getUser(mimeMessage);
		//user.ifPresent(task::setCreationUser);
		
		Optional<String> plainBody = getPlainBody(mimeMessage);
		plainBody.map(body -> AonStringUtils.substringBefore(body, "\n")).ifPresent(task::setDescription);

		Optional<EmailCustomer> customer = getCustomer(mimeMessage);
		customer.map(EmailCustomer::email).ifPresent(task::setGtaskId);
		customer.map(EmailCustomer::customer).ifPresent(task::setRegistry);
		
		//customer.map(EmailCustomer::email).ifPresent(taskWorkflow::setEmail);
		Optional<String> htmlBody = getHtmlBody(mimeMessage, cidInlineHandler);
		
		
		htmlBody.ifPresentOrElse(
			html -> {
				Document[] comments = getComments(html);
				for (int i = 0; i < comments.length; i++) {
					Document comment = comments[i];
					TaskWorkflow taskWorkflow =
					getTaskWorkflow(comment, i);
					
					if ( i == 0 ) { 
						taskWorkflow.setType(TaskWorkflowType.OPEN);
						task.setStartDate(firstNonNull(taskWorkflow.getCreationDate(), task.getStartDate()));
						task.setCreationDate(firstNonNull(taskWorkflow.getCreationDate(), task.getCreationDate()));
						//task.setCreationUser(firstNonNull(taskWorkflow.getCreationUser(), task.getCreationUser()));
					}else if ( i == ( comments.length - 1)) {
						//taskWorkflow.setType(TaskWorkflowType.CLOSE);
						taskWorkflow.setCreationDate(sendDate);
						from.ifPresent(taskWorkflow::setEmail);
					} else { 
						taskWorkflow.setType(TaskWorkflowType.COMMENT);
					}
					

					task.addWorkflow(taskWorkflow);
				}
			},
			() -> plainBody.ifPresent(plain -> {
					TaskWorkflow taskWorkflow =
					new TaskWorkflow()
					.setComment(plain)
					.setType(TaskWorkflowType.OPEN)
					.setCreationDate(task.getCreationDate())
					;
					from.ifPresent(taskWorkflow::setEmail);
					task.addWorkflow(taskWorkflow);
				})
			);
		
		

//		TaskAttach taskAttach = new TaskAttach()
//				.setData(data)
//				.setMimetype(MimeType.OCTECT_STREAM);
//				;
//		taskWorkflow.setTaskAttachList(Collections.singletonList(taskAttach));

		return task;
	}
	
	private static Document[] getComments(String htmlBody) {
			Parser parser = Parser.htmlParser().setTrackPosition(true);
			Document document = Jsoup.parse(htmlBody, parser );
			//document.outputSettings().prettyPrint(true);
			
			
			return extractComments(document);
	}
	
	private static TaskWorkflow getTaskWorkflow(Document document, int i ) {
		TaskWorkflow  taskWorkflow = new TaskWorkflow();
		taskWorkflow.setEmail("");
		
		
		Element blockQuoteElement = document.selectFirst("* div > div + blockquote, div > div.aon_wrote + div");
		if ( blockQuoteElement != null ) {
			Element emailHeaderElement = blockQuoteElement.previousElementSibling(); 
			emailHeaderElement.remove();
			//<p class="MsoNormal">El mar, 1 jul 2025 a las 8:20, &lt;<a href="mailto:javimartin@ayudatpymes.es" target="_blank">javimartin@ayudatpymes.es</a>&gt; escribió:<o:p></o:p></p>
			String emailHeaderText = emailHeaderElement.text();
			
			try {
				Matcher matcher = Pattern.compile(
						"(\\S{3}\\s*,|El)\\s*(?<date>\\d{1,2}\\s*\\p{IsLatin}{3}\\s*\\d{4})\\D*(?<time>\\d{1,2}:\\d{1,2})",
						Pattern.DOTALL | Pattern.CASE_INSENSITIVE).matcher(emailHeaderText);
				matcher.find();
				Date date = new SimpleDateFormat("d MMM yyyy HH:mm", Locale.of("ES", "es"))
						.parse(new StringBuilder().append(matcher.group("date")).append(" ")
								.append(matcher.group("time")).toString());
				taskWorkflow.setCreationDate(date);
			} catch (Exception e) {
				try {
					Matcher matcher = Pattern.compile(
							"\\S{3}\\s*,\\s*(?<date>\\p{IsLatin}{3}\\s*\\d{1,2}\\s*,\\s*\\d{4})\\D*(?<time>\\d{1,2}:\\d{1,2}).(?<tz>AM|PM)",
							Pattern.DOTALL | Pattern.CASE_INSENSITIVE).matcher(emailHeaderText);
					matcher.find();
					Date date = new SimpleDateFormat("MMM d, yyyy HH:mm a")
							.parse(new StringBuilder().append(matcher.group("date")).append(" ")
									.append(matcher.group("time")).append(" ").append(matcher.group("tz")).toString());
					taskWorkflow.setCreationDate(date);
				} catch ( Exception ex) {
					System.err.println("* div > div + blockquote [date]: " + ex.getMessage() + ", " + emailHeaderText);
				}
			}

			try {
				Matcher matcher = Pattern.compile(".*,\\s*(?<user>.*)(escribi|wrote)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE).matcher(emailHeaderText);
				matcher.find();
				taskWorkflow.setEmail(matcher.group("user"));
				//taskWorkflow.setCreationUser(getUserName(user));
			} catch (Exception e) {
				System.err.println("* div > div + blockquote [user]: " + e.getMessage() + ", " + emailHeaderText );
			}
			
			document = customize(document, Integer.toString(i));			
			taskWorkflow.setComment(document.outerHtml());
			return taskWorkflow;
		} 
		
		
		Element fromElement = document.selectFirst("b.aon_quote:matches(^De:$)");
		if (fromElement != null) {
			Element emailHeadersElement = fromElement.addClass("aon_quote").parent();
			emailHeadersElement.remove();
			
			
			String emailHeadersText = emailHeadersElement.text();
			try {
				//De: Soporte Clientes <soporte@aonsolutions.es> Enviado el:
				Matcher matcher = Pattern.compile(
						"De\\s*:\\s*(?<user>.*)(Enviado|Fecha|Enviat)",
						Pattern.DOTALL | Pattern.CASE_INSENSITIVE).matcher(emailHeadersText);
				matcher.find();
				taskWorkflow.setEmail(matcher.group("user"));
				//taskWorkflow.setCreationUser(getUserName(matcher.group("user")));
			}catch(Exception e) {
				System.err.println("b.aon_quote:matches(^De:$) [user]: " + emailHeadersText + ": " +  e.getMessage());
			}
			
			try {
				//Enviado el: martes, 8 de julio de 2025 12:46 
				Matcher matcher = Pattern.compile(
						"(?:Enviado|Fecha|Enviat)[^:]*:\\s*\\S+\\s*,\\s*(?<date>\\d{1,2}\\s*de\\s*\\p{IsLatin}+\\s*de\\s*\\d{4})\\s*,?\\s*(?<time>\\d{1,2}:\\d{1,2})",
						Pattern.DOTALL | Pattern.CASE_INSENSITIVE).matcher(emailHeadersText);
				matcher.find();
				try {
					Date date = new SimpleDateFormat("d 'de' MMMM 'de' yyyy HH:mm", Locale.of("ES", "es"))
							.parse(new StringBuilder().append(matcher.group("date")).append(" ")
									.append(matcher.group("time")).toString().replace(",", ""));
					taskWorkflow.setCreationDate(date);
				} catch ( Exception esE ) {
					Date date = new SimpleDateFormat("d MMMM 'de' yyyy HH:mm", Locale.of("ca","ES"))
							.parse(new StringBuilder().append(matcher.group("date")).append(" ")
									.append(matcher.group("time")).toString().replace(",", ""));
					taskWorkflow.setCreationDate(date);
					
				}
			} catch (Exception e) {
				System.err.println("b.aon_quote:matches(^De:$)  [date]: " + emailHeadersText + ": " +  e.getMessage());
			}

			document = customize(document, Integer.toString(i));			
			taskWorkflow.setComment(document.outerHtml());
			return taskWorkflow;
		}
		
		
		Element gmailAttrEl = document.selectFirst("* div.gmail_attr");
		
		if (gmailAttrEl != null) {
			gmailAttrEl.remove();
			String gmailAttrText = gmailAttrEl.text();
			
			try {
				Matcher matcher = Pattern.compile(
						"(?<date>\\p{IsLatin}{3}\\s*,\\s*\\d{1,2}\\s*\\p{IsLatin}{3}\\s*\\d{4})\\D*(?<time>\\d{1,2}:\\d{1,2})",
						Pattern.DOTALL | Pattern.CASE_INSENSITIVE).matcher(gmailAttrText);
				matcher.find();
				Date date = new SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.of("ES", "es"))
						.parse(new StringBuilder().append(matcher.group("date")).append(" ")
								.append(matcher.group("time")).toString());
				taskWorkflow.setCreationDate(date);
			} catch (Exception e) {
				try {
					Matcher matcher = Pattern.compile(
							"(?<date>\\p{IsLatin}{3}\\s*,\\s*\\p{IsLatin}{3}\\s*\\d{1,2}\\s*,\\s*\\d{4})\\D*(?<time>\\d{1,2}:\\d{1,2}).(?<tz>AM|PM)",
							Pattern.DOTALL | Pattern.CASE_INSENSITIVE).matcher(gmailAttrText);
					matcher.find();
					Date date = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm a")
							.parse(new StringBuilder().append(matcher.group("date")).append(" ")
									.append(matcher.group("time")).append(" ").append(matcher.group("tz")).toString());
					taskWorkflow.setCreationDate(date);
				} catch (Exception ex) {
					System.err.println("div.gmail_attr [date]: " + ex.getMessage() + ", " + gmailAttrText);
				}
			}

			try {
				Matcher matcher = Pattern.compile("(,|De:|AM|PM)(?<user>[^,^:]*>\\)?)", Pattern.DOTALL | Pattern.CASE_INSENSITIVE).matcher(gmailAttrText);
				matcher.find();
				//String user = matcher.group("user");
				taskWorkflow.setEmail(matcher.group("user"));
				//taskWorkflow.setCreationUser(getUserName(user));
			} catch (Exception e) {
				System.err.println("div.gmail_attr [user]: " + e.getMessage() + ", " + gmailAttrText );
			}

			document = customize(document, Integer.toString(i));			
			taskWorkflow.setComment(document.outerHtml());
			return taskWorkflow;
		}
		
		document = customize(document, Integer.toString(i));			
		taskWorkflow.setComment(document.outerHtml());
		return taskWorkflow;
	}
	
	private static String getUserName(String fullUserName) {
		Matcher emailMatcher = EMAIL_PATTERN
				.matcher(fullUserName);
		if (emailMatcher.find()) {
			return emailMatcher.group("user");
		} else {
			return AonStringUtils.substringBefore(fullUserName, " ");
		}
	}

	private static Document[] extractComments(Document document) {
		LinkedList<Document> documents = new LinkedList<>();
	
		// Split the document into multiple parts based on Gmail quotes
		Element element = document;
		
		while (element != null) {
			
			Element gmailQuoteEl = element.selectFirst("* div.gmail_quote");
			
			Element blockQuoteCiteEl = element.selectFirst("* blockquote[type=cite]");
			
			Element blockQuoteEl = Optional.ofNullable(element.selectFirst("* div > div + blockquote")).map(Element::parent).orElse(null);
			
			Element fromElement = Optional.ofNullable(element.selectFirst("b:matches(^De:$):not(.aon_quote) ")).orElse(null);
			
			Element wroteElement = Optional.ofNullable(element.selectFirst("div:not(.aon_wrote) > br + a[href^=mailto] +  br + br"))
					.map(Element::parentElement).filter( div -> div.text().matches(".*escribi.\\s*:\\s*$")).orElse(null);

			Element emailElement =  Arrays.stream(new Element[] {gmailQuoteEl, blockQuoteCiteEl, blockQuoteEl, fromElement, wroteElement }).filter(Objects::nonNull).sorted((e1, e2) -> e1.sourceRange().startPos() - e2.sourceRange().startPos()).findFirst().orElse(null);
			
			if (emailElement == null) {
				break; 
			}

			if (emailElement == fromElement ) {
				Element divFromElement = fromElement.addClass("aon_quote").parent().parent();
				Collection<Element> blockquotes = new ArrayList<>();
				blockquotes.add( divFromElement);
				blockquotes.addAll(divFromElement.nextElementSiblings());
				emailElement = document.createElement("div").appendChildren(blockquotes);
			} else if (emailElement == gmailQuoteEl) {
			} else if (emailElement == blockQuoteCiteEl) {
			} else if (emailElement == blockQuoteEl) {
			} else if (emailElement == wroteElement) {
				wroteElement.addClass("aon_wrote");
				Collection<Element> msgElements = new ArrayList<>();
				msgElements.add( wroteElement);
				msgElements.addAll(wroteElement.nextElementSiblings());
				emailElement = document.createElement("div").appendChildren(msgElements);
			}
			
			// Remove the quote element
			emailElement.remove();
			documents.push(document.clone());

			element = emailElement;
			document.body().empty(); // Clear the body to avoid accumulating elements
			document.body().appendChild(emailElement); // Append the current quote element to the document
		}
		
		documents.push(document.clone());
		
		return documents.toArray(new Document[documents.size()]);
	}

	private static List<TaskWorkflow> getTaskWorkflows(Integer taskId)
			throws URISyntaxException, IOException, InterruptedException {
		JSONArray jsonArray = AonTask.getTaskWorkflows(DOMAIN_NAME, LOGIN, taskId);
		return TaskWorkflowJSON.fromJSON(jsonArray);
	}

	private static String compact(String html) {
		return html.replaceAll("\\s+", " ").trim();
	}

	private static List<Task> getMessageTasks(String... headers)
			throws MessagingException, URISyntaxException, IOException, InterruptedException {

		if (headers == null || headers.length == 0) {
			return Collections.emptyList();
		}

		Integer[] sourceIds = Arrays.stream(headers).flatMap(header -> Arrays.stream(header.split("\\s+")))
				.map(String::hashCode).toArray(Integer[]::new);

		Map<String, Object> params = new HashMap<>();
		params.put(IJsonNames.SOURCE, TaskSource.CAU.getName());
		params.put(IJsonNames.SOURCE_IDS, new JSONArray(sourceIds));
		return AonTask.getTasks(DOMAIN_NAME, LOGIN, params);

	}

	private static List<TaskHolder> getTaskHolders() throws IOException {
		try {
			return AonTask.getTaskHolders(DOMAIN_NAME, LOGIN, "AONd95770f269e711eb94390242ac130002");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return Collections.emptyList();
		} catch (URISyntaxException | IOException e) {
			return Collections.emptyList();
		}
	}

	private static Optional<TaskHolder> getTaskHolder(MimeMessage mimeMessage) throws MessagingException, IOException {
		Address[] to = mimeMessage.getAllRecipients();
		if (to == null || to.length == 0) {
			return Optional.empty();
		}

		for (Address address : to) {
			Optional<String> personal = getPersonal(address);
			if (personal.isPresent()) {
				String name = personal.get().split("\\W")[0];
				for (TaskHolder taskHolder : getTaskHolders()) {
					if (taskHolder.getUser() != null
							&& AonStringUtils.startsWithIgnoreCase(taskHolder.getUser().getName(), name)) {
						return Optional.of(taskHolder);
					}
				}
			}
		}

		return Optional.empty();

	}

	private static Optional<String> getUser(MimeMessage mimeMessage) throws MessagingException, IOException {
		Address[] from = mimeMessage.getFrom();
		if (from == null || from.length == 0) {
			return Optional.empty();
		}

		for (Address address : from) {
			String email = getEmail(address);
			return Optional.ofNullable(getUserName(email));
		}

		return Optional.empty();

	}

	private static Optional<EmailCustomer> getCustomer(MimeMessage mimeMessage) throws MessagingException, IOException {
		Address[] from = mimeMessage.getFrom();
		if (from == null || from.length == 0) {
			return Optional.empty();
		}

		for (Address address : from) {
			String email = getEmail(address);
			Optional<Customer> customer = getCustomer(email);
			if (customer.isPresent()) {
				return Optional.of(new EmailCustomer(email, customer.get()));
			}
		}

		return Optional.empty();

	}

	private static Optional<Customer> getCustomer(String email) {
		try {
			List<Customer> customers = AonCustomer.getCustomers(DOMAIN_NAME, "AONd95770f269e711eb94390242ac130002",
					email);
			return customers.stream().findFirst();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return Optional.empty();
		} catch (URISyntaxException | IOException e) {
			return Optional.empty();
		}
	}

	private static Optional<String> getPersonal(Address address) {
		if (address instanceof InternetAddress internetAddress) {
			return AonStringUtils.isNotBlank(internetAddress.getPersonal()) ? Optional.of(internetAddress.getPersonal())
					: Optional.empty();
		} else {
			return Optional.empty();
		}
	}

	private static String getEmail(Address address) {
		if (address instanceof InternetAddress internetAddress) {
			return internetAddress.getAddress();
		} else {
			return address.toString();
		}
	}

	private static Optional<String> getHtmlBody(MimeMessage mimeMessage, UnaryOperator<String> cidInlineHandler)
			throws IOException, MessagingException {
		Collection<BodyPart> htmlBodyParts = MimeMessageUtils.getBodyParts(mimeMessage,
				MimeMessageUtils.isMimeType("text/html"));
		for (BodyPart htmlBodyPart : htmlBodyParts) {
			String html = MimeMessageUtils.getHtml(htmlBodyPart, cidInlineHandler);
			if (AonStringUtils.isNotBlank(html)) {
				return Optional.of(html);
			}

		}
		return Optional.empty();

	}

	private static Optional<String> getPlainBody(MimeMessage mimeMessage) throws IOException, MessagingException {
		Collection<BodyPart> plainBodyParts = MimeMessageUtils.getBodyParts(mimeMessage,
				MimeMessageUtils.isMimeType("text/plain"));
		for (BodyPart plainBodyPart : plainBodyParts) {
			String plain = Objects.toString(plainBodyPart.getContent(), "");
			if (AonStringUtils.isNotBlank(plain)) {
				return Optional.of(plain);
			}
		}
		return Optional.empty();

	}

//	private static String[] split(String html) {
//		Document doc = Jsoup.parse(html);
//		
//		
//		return null;
//	}

//	private static Document[] split(Document doc) {
//		List<Document> docs = new ArrayList<>();
//		
//		Element gmailQuoteDiv = doc.selectFirst("div.gmail_quote");
//		if (gmailQuoteDiv == null) {
//			return new Document[] { doc };
//		}
//		// Remove the Gmail quote div
//		gmailQuoteDiv.remove();
//		
//		Document gamilQuoteDoc = doc.clone();
//		
//	}

	private static boolean isCollapsed(String html) {
		return AonStringUtils.indexOf(html, "gmail_collapse_div") != -1;
	}

	private static Document customize(Document document, String id) {
		Element gmailSignatureDiv = document.selectFirst("* div.gmail_signature,* div.x_gmail_signature");
		if (gmailSignatureDiv != null) {
			gmailSignatureDiv.attr("id", String.format("gmail_signature_div_%1$s", id));
			gmailSignatureDiv.attr("style", "display: none");
			gmailSignatureDiv.before(String.format(
					"""
							<div id="gmail_collapse_div_%1$s" style="cursor:pointer;margin-bottom:10px;" onclick="javascript:gmail_signature_div_%1$s.style.display=gmail_signature_div_%1$s.style.display === 'none' ? 'block' : 'none'"><span style="font-weight: bolder;background-color:#eee;padding-left: 9px;padding-right:9px;border-radius:12px;padding-bottom:6px;">...</span></div>
							""",
					id));
		}
		Element lineBreakAtBeginningOfSignature = document.selectFirst("br[id$=\"lineBreakAtBeginningOfSignature\"]");
		if (lineBreakAtBeginningOfSignature != null) {
			Element signatureDiv = lineBreakAtBeginningOfSignature.parentElement();
			signatureDiv.attr("id", String.format("signature_div_%1$s", id));
			signatureDiv.attr("style", "display: none");
			signatureDiv.before(String.format(
					"""
							<div id="collapse_div_%1$s" style="cursor:pointer;margin-bottom:10px;" onclick="javascript:signature_div_%1$s.style.display=signature_div_%1$s.style.display === 'none' ? 'block' : 'none'"><span style="font-weight: bolder;background-color:#eee;padding-left: 9px;padding-right:9px;border-radius:12px;padding-bottom:6px;">...</span></div>
							""",
					id));
		}
		
		document.getElementsByTag("blockquote").forEach( blockQuote -> {
			String style = blockQuote.attr("style");
			// clean border-left 
			// style="....;border-left:1px solid rgb(204,204,204);..." 
			style = style.replaceAll("border-left\\s*:[^;]*", "border-left:none");
			blockQuote.attr("style", style);
		});
		return document;
	}

	private static void addTask(MimeMessage mimeMessage, UnaryOperator<String> cidInlineHandler)
			throws MessagingException, IOException, URISyntaxException, InterruptedException {
		

		String[] replyTo = mimeMessage.getHeader(IN_REPLY_TO);
		replyTo = replyTo != null ? replyTo : new String[] {};
		String[] references = mimeMessage.getHeader("References");
		references = references != null ? references : new String[] {};
		String[] msgId = new String[] { mimeMessage.getMessageID() };
		
		List<String> headers = new ArrayList<>();
		Collections.addAll(headers, msgId);
		Collections.addAll(headers, replyTo);
		Collections.addAll(headers, references);
		
		List<Task> messageTasks = getMessageTasks(headers.toArray(String[]::new));

		Task newTask = newTask(mimeMessage, cidInlineHandler);

		if (!messageTasks.isEmpty()) {
			// Previous task found, add a reply workflow
			Integer taskId = messageTasks.get(0).getId();
			
			List<TaskWorkflow> oldTaskWorkflows = getTaskWorkflows(taskId);
			
			List<TaskWorkflow> newTaskWorkflows = 
			newTask.getWorkflows().stream()
			.map( newTaskWorkflow  -> {
					Optional<TaskWorkflow> oldTaskWorklow =
					oldTaskWorkflows.stream()
					.filter(oldTaskWorkflow -> Objects.equals(oldTaskWorkflow.getCreationDate(), newTaskWorkflow.getCreationDate()))
					.filter(oldTaskWorkflow -> AonStringUtils.equalsIgnoreCase(oldTaskWorkflow.getEmail(), newTaskWorkflow.getEmail()))
					.findFirst();
					
					oldTaskWorklow.map( TaskWorkflow::getId ).ifPresent(newTaskWorkflow::setId);

					return newTaskWorkflow;
					})
			.toList();
			
			
			

			for (TaskWorkflow taskWorkflow : newTaskWorkflows ) {
				taskWorkflow.setTask(taskId);
				// taskWorkflow.setType(TaskWorkflowType.COMMENT);
				//taskWorkflow.setComment(collapse(taskWorkflow.getComment(), workflowIndex++ + ""));
				AonTask.addTaskWorkflow(DOMAIN_NAME, LOGIN, taskWorkflow);
			}
		} else {
			// No previous task found, create a new one
			JSONObject jsonTask = AonTask.newTask(DOMAIN_NAME, LOGIN, newTask);
			Integer taskId = jsonTask.getInt(IJsonNames.ID);
			for (TaskWorkflow taskWorkflow : newTask.getWorkflows()) {
				taskWorkflow.setTask(taskId);
				//taskWorkflow.setType(TaskWorkflowType.OPEN);
				//taskWorkflow.setComment(collapse(taskWorkflow.getComment(), 0 + ""));
				AonTask.addTaskWorkflow(DOMAIN_NAME, LOGIN, taskWorkflow);
			}
		}

		
	}
	public static void main(String[] args) throws IOException, MessagingException, URISyntaxException, InterruptedException, ParseException {
		task(args);
		// comment(args);
	}
	

	public static void m4in(String[] args)
			throws IOException, MessagingException, URISyntaxException, InterruptedException {
		String prefix = args[1];
		String bucket = args[0];

		S3 s3 = S3.getInstance();

		List<S3Object> objs = s3.listObjects(bucket, prefix).stream()
				.sorted((o1, o2) -> o1.lastModified().compareTo(o2.lastModified())).toList();

		for (S3Object obj : objs) {

			byte[] data = s3.download(bucket, obj.key());
			if (data == null || data.length == 0) {
				System.out.println("No data found for " + obj.key());
				continue;
			}
			try {
				addTask(getMimeMessage(data), cid -> String.format("%s/%s/%s/%s", FUNCTION_URL, bucket, obj.key(), cid));
			} catch (Exception e) {
				System.err.println("Error processing task for " + obj.key() + ": " + e.getMessage());
			}
		}
	}

	public static void task(String[] args)
			throws IOException, MessagingException, URISyntaxException, InterruptedException {
		String bucket = args[0];
		
		for ( File file : new File("/var/tmp/soporte-aonsolutions.eu/").listFiles(f -> f.getName().startsWith("soporte-aonsolutions.eu_8amicsr6evvf1im26m7i2lvkilqdv6ioehuurko1")) ) {
			try ( FileInputStream is = new FileInputStream(file) ) {
				MimeMessage mimeMessage = getMimeMessage(is);
				try {
					addTask(mimeMessage, cid -> String.format("%s/%s/%s/%s", FUNCTION_URL, bucket, file.getName().replace("_", "/"), cid));
				} catch (Exception e) {
					System.err.println("Error processing task for " + file.getName() + ": " + e.getMessage());
				}


			} catch (Exception e) {
				System.err.println("Error processing file " + file.getName() + ": " + e.getMessage());
			}
		}

	
	}	

	public static void comment(String[] args) throws IOException {
		for (File file : new File("/var/tmp/html_body/").listFiles()) {
			try {
				Parser parser = Parser.htmlParser().setTrackPosition(true);
				Document document = Jsoup.parse(file, null, "", parser );
				document.outputSettings().prettyPrint(true);
				
				Document[] comments = extractComments(document);
				int i ;
				for( i = 0 ; i < ( comments.length -1 ); i++) {
					try (FileWriter fileWriter = new FileWriter("/var/tmp/task_workflow_comments/" + file.getName().replace(".html", "_" + i + ".html"), StandardCharsets.UTF_8)) {
						fileWriter.write(getTaskWorkflow(comments[i],i).getComment());
					} catch (IOException e) {
						System.err.println("Error writting file " + file.getName() + ": " + e.getMessage());
					}
				}
					
				
				try (FileWriter fileWriter = new FileWriter("/var/tmp/task_workflow_comments/" + (i == 0? "0_" : "") + file.getName().replace(".html",  (i >= 0? "_" + i : "") + ".html"), StandardCharsets.UTF_8)) {
					fileWriter.write(getTaskWorkflow(comments[i],i).getComment());
				} catch (IOException e) {
					System.err.println("Error writting file " + file.getName() + ": " + e.getMessage());
				}
				
				

			} catch (Exception e) {
				System.err.println("Error processing file " + file.getName() + ": " + e.getMessage());
			}
		}
	}
	
	
	public static void htmlBody(String[] args) throws IOException {
		String bucket = args[0];

		for ( File file : new File("/var/tmp/soporte-aonsolutions.eu/").listFiles() ) {
			try ( FileInputStream is = new FileInputStream(file) ) {
				MimeMessage mimeMessage = getMimeMessage(is);
				Optional<String> htmlBody = getHtmlBody(mimeMessage, cid -> String.format("%s/%s/%s/%s", FUNCTION_URL, bucket, file.getName().replace("_", "/"), cid));
				htmlBody.ifPresent(html -> {
					try ( FileWriter fos = new FileWriter("/var/tmp/html_body/" + file.getName() + ".html", StandardCharsets.UTF_8) ) {
						fos.write(html);
					} catch (IOException e) {
						System.err.println("Error writing HTML file for " + file.getName() + ": " + e.getMessage());
					}
				});
			} catch (Exception e) {
				System.err.println("Error processing file " + file.getName() + ": " + e.getMessage());
			}
		}
	}	
	
	
	
}
