package aon.solutions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.print.DocFlavor.STRING;

import org.json.JSONArray;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.esferalia.aon.occam.api.json.TaskJSON;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.api.model.task.TaskWorkflowType;
import com.esferalia.aon.watson.server.AonDateUtils;

import jakarta.mail.BodyPart;
import jakarta.mail.Header;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import net.aonsolutions.aon.api.AonAuth;
import net.aonsolutions.aon.api.AonCompany;
import net.aonsolutions.aon.api.AonTask;
import net.aonsolutions.aon.api.AonUser;

public class SESRequestHandler<T> implements RequestHandler<Map<String, T>, APIGatewayProxyResponseEvent> {

	@Override
	public APIGatewayProxyResponseEvent handleRequest(Map<String, T> input, Context context) {

		/*
		 * Map<String, T> records = (Map<String, T>) input.get("Records"); Map<String,
		 * T> ses = (Map<String, T>) records.get("ses"); Map<String, T> mail =
		 * (Map<String, T>) ses.get("mail"); String source = (String)
		 * mail.get("source"); String messageId = (String) mail.get("messageId");
		 * System.out.println(source); System.out.println(messageId);
		 */
		String bucket = "aon-ses-inbox";

		Map<String, ?> map = (Map<String, ?>) input;
		List<Map<String, ?>> records = (List<Map<String, ?>>) map.get("Records");

		List<String> messageIds = new ArrayList<String>();
		List<String> sources = new ArrayList<String>();
		List<String> messageResults = new ArrayList<>();
		List<String> dests = new ArrayList<>();

		for (Map<String, ?> record : records) {
			Map<String, ?> ses = (Map<String, ?>) record.get("ses");
			Map<String, ?> mail = (Map<String, ?>) ses.get("mail");
			String source = (String) mail.get("source");
			String messageId = (String) mail.get("messageId");
			List<String> destinations =  (List<String>) mail.get("destination");
			dests.add(destinations.get(0));
			messageIds.add(messageId);
			sources.add(source);
		}

		for (int i = 0; i < messageIds.size(); i++) {
			String[] emailParts = dests.get(0).split("@");
			System.out.println(emailParts[1]);
			AmazonS3 s3 = AmazonS3ClientBuilder.standard().build();
			String key = String.format("soporte@aon.solutions/%s", messageIds.get(i));
			S3Object s3Object = s3.getObject(new GetObjectRequest(bucket, key));

			try (InputStream is = s3Object.getObjectContent()) {
				messageResults = getHtmlMessage(is, messageIds.get(i));
				handleTask(sources.get(i), emailParts[1], messageResults.get(0), messageResults.get(1), messageResults.get(2));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println(sources);
		System.out.println(messageIds);

		return null;
	}
	
	private static List<String> getHtmlMessage(InputStream is, String messageId) throws MessagingException, IOException {
		
		List<String> attachIdList = new ArrayList<>();
		List<String> result = new ArrayList<>();
		List<String> fileNames = new ArrayList<>();
		Map<String, String> imagesWithURL = new HashMap<>();
		Map<String, String> attachesNames = new HashMap<>();
		String html = "";
		String subject = "";
		Session session = Session.getInstance(System.getProperties());
		MimeMessage mimeMessage = new MimeMessage(session, is);
		String subjectArray[] = mimeMessage.getHeader("Subject");
		if(subjectArray[0].contains("Fwd")) subject = subjectArray[0].replace("Fwd", "Asunto de la tarea");
		else subject =  "Asunto de la tarea: " + subjectArray[0];
		System.out.println(subject);
		Multipart multipart = (Multipart) mimeMessage.getContent();
		for (int i = 0; i < multipart.getCount(); i++) {
			 //System.out.println(multipart.getContentType());
			BodyPart bodyPart = multipart.getBodyPart(i);
			System.out.println(multipart.getBodyPart(i).getContentType());
			if(multipart.getBodyPart(i).getFileName() != null ) fileNames.add(multipart.getBodyPart(i).getFileName());
			attachIdList = getAttachIds(bodyPart, attachIdList, attachesNames);
			if(bodyPart.getContentType().startsWith("multipart/related")) {
				Multipart m = (Multipart) bodyPart.getContent();
				System.out.println(m.getBodyPart(0).getContentType());
				BodyPart b = m.getBodyPart(0);
				Multipart m2 = (Multipart) b.getContent();
				html =  m2.getBodyPart(1).getContent().toString();
				BodyPart bAttach = m.getBodyPart(1);
				attachIdList = getAttachIds(bAttach, attachIdList, attachesNames);
				
			}
			if (bodyPart.getContentType().startsWith("multipart/alternative")) {
				Multipart m = (Multipart) bodyPart.getContent();
				html = m.getBodyPart(1).getContent().toString();
				System.out.println(html);
			}
		}
		
		
		
		StringBuilder newHtml = new StringBuilder(html);
		System.out.println(attachIdList);
		imagesWithURL = formatUrlToImages(messageId, attachIdList, attachesNames);
		String finalHtml = replaceAttachIds(newHtml, imagesWithURL, attachesNames);
		String attaches = attachString(imagesWithURL, attachesNames);
		result.add(finalHtml);
		result.add(subject);
		result.add(attaches);
        
		System.out.println(finalHtml);
		
		return result;

		
	}
	


	private static void handleTask(String from, String to, String message, String subject, String attaches)
			throws URISyntaxException, IOException, InterruptedException {

		Auth auth = AonAuth.getAuth(from, to);
		if (auth.getUuid() != null && auth.getUuid().length() > 0) {
			String token = AonToken.build(auth.getUuid(), AonDateUtils.addMonths(new Date(), 3));
			List<com.esferalia.aon.occam.api.model.AonCompany> companies = AonCompany.getCompanies(token, to);
			for (com.esferalia.aon.occam.api.model.AonCompany company : companies) {
				if (company.getDomain().getName().equals(to)) {

					User user = AonUser.getUser(company.getDomain().getName(), token);
					List<TaskHolder> taskHolderList = AonTask.getTaskHolders(company.getDomain().getName(),
							user.getLogin(), token);
					user.setTaskHolders(taskHolderList);
					List<Workgroup> workgroupList = AonTask.getWorkgroups(company.getDomain().getName(), user.getLogin(), token);
					Task newTask = new Task()
							.setCreationDate(new Date())
							.setCreationUser(user.getLogin())
							.setDomain(company.getDomain())
							.setTitle(subject.replace("Asunto de la tarea:", ""))
							.setTaskHolder(user.getTaskHolders().get(0))
							.setSender(user.getTaskHolders().get(0))
							.setRegistry(user.getRegistry())
							.setStatus(TaskStatus.PENDING)
							.setWorkgroup(workgroupList.get(0));
					
				
						Task task = TaskJSON.fromJSON(AonTask.newTask(company.getDomain().getName(), user.getLogin(), newTask));
						
						  TaskWorkflow subjectWorkflow = new TaskWorkflow();
						    subjectWorkflow.setCreationUser(user.getLogin())
						    .setCreationDate(new Date())
						    .setTask(task.getId())
						    .setTaskHolder(taskHolderList.get(0))
						    .setComment("Enviado por:" + from + "\n" + subject)
						    .setType(TaskWorkflowType.COMMENT)
						    .setEmail(from);
						    
						    AonTask.addTaskWorkflow(company.getDomain().getName(), user.getLogin(), subjectWorkflow);
						
						
						 TaskWorkflow taskWorkflow = new TaskWorkflow();
						    taskWorkflow.setCreationUser(user.getLogin())
						    .setCreationDate(new Date())
						    .setTask(task.getId())
						    .setTaskHolder(taskHolderList.get(0))
						    .setComment(message)
						    .setType(TaskWorkflowType.COMMENT)
						    .setEmail(from);
						    
						    AonTask.addTaskWorkflow(company.getDomain().getName(), user.getLogin(), taskWorkflow);
						    
						    TaskWorkflow attachWorkflow = new TaskWorkflow();
						    attachWorkflow.setCreationUser(user.getLogin())
						    .setCreationDate(new Date())
						    .setTask(task.getId())
						    .setTaskHolder(taskHolderList.get(0))
						    .setComment(attaches)
						    .setType(TaskWorkflowType.COMMENT)
						    .setEmail(from);
						    
					if(!attaches.isEmpty()) AonTask.addTaskWorkflow(company.getDomain().getName(), user.getLogin(), attachWorkflow);
						    
						   

				} else {

					throw new IllegalArgumentException();
				}
			}

		} else
			throw new IllegalArgumentException();

	}

	private static List<String> getAttachIds(BodyPart bodyPart, List<String> attachIds, Map<String, String> attachesNames) throws MessagingException {

		Enumeration<Header> headers = bodyPart.getAllHeaders();
		String fileName = "";
		String attachId = "";
		for (Iterator<Header> iterator = headers.asIterator(); iterator.hasNext();) {
			Header header = iterator.next();
			System.out.println(header.getName() + "=" + header.getValue());
			if(header.getName().equals("Content-Disposition")) {
				String headerName[] = header.getValue().split(";");
				String headerValue = headerName[1].substring(10);
				fileName = headerValue.replaceAll("\"", "");
				System.out.println(fileName);
			}
			if (header.getName().equals("X-Attachment-Id")) {
				attachIds.add(header.getValue());
				attachId  =header.getValue();
			}
	
		}
        
		if(!fileName.isBlank()) attachesNames.put(attachId, fileName);
		
		return attachIds;
	}

	private static String replaceAttachIds(StringBuilder html, Map<String, String> imagesWithURL , Map<String, String> attachesNames) {
        
		List<String> attachToRemove = new ArrayList<>();
		for (Map.Entry<String, String> entry : imagesWithURL.entrySet()) {
			Pattern pattern = Pattern.compile("\"cid:" + entry.getKey() + "\"");
			Matcher matcher = pattern.matcher(html);
			if (matcher.find()) {

				String replaceImage = "\"" + entry.getValue() + "\"";
				int start = matcher.start();
				int end = matcher.end();
				html.replace(start, end, replaceImage);
				attachToRemove.add(entry.getKey());
			}
		}
		
		for (String attach : attachToRemove) {
			
			imagesWithURL.remove(attach);
			attachesNames.remove(attach);
		}

		return html.toString();
	}

	private static String attachString(Map<String, String> imagesWithURL, Map<String, String> attachesNames) {
        
		StringBuilder html = new StringBuilder();
		for (Map.Entry<String, String> entry : imagesWithURL.entrySet()) {
			 
			if(!html.isEmpty()) {
				html.append(","); 
				html.append(" "); 
			}
			
			String script = "<script>\n"
					+ "const cuadrado = document.querySelector('.cuadrado');\n"
					+ "const botonCuadrado = document.querySelector('.boton-cuadrado');\n"
					+ "\n"
					+ "// Agregar un manejador de eventos al cuadrado para el evento mouseover\n"
					+ "cuadrado.addEventListener('mouseover', function() {\n"
					+ "    // Mostrar el botón cambiando su estilo de display\n"
					+ "    botonCuadrado.style.display = 'inline-block';\n"
					+ "});\n"
					+ "\n"
					+ "// Agregar un manejador de eventos al cuadrado para el evento mouseout\n"
					+ "cuadrado.addEventListener('mouseout', function() {\n"
					+ "    // Ocultar el botón cambiando su estilo de display\n"
					+ "    botonCuadrado.style.display = 'none';\n"
					+ "});\n"
					+ "</script>";
			
			
			String html1= "<body>\n"
					+ "   \n"
					+ "\n"
					+ "<div>\n"
					+ "    <a class=\"descarga\" href=\""+entry.getValue() +"\" title=\"" + attachesNames.get(entry.getKey()) + "\" ><span>Descargar</span><span>" + attachesNames.get(entry.getKey()) + "</span></a>\n"
					+ "</div>\n"
					+ "\n"
					+ "</body>";
			
			
            String css ="<style>\n"
            		+ ".descarga {\n"
            		+ "    background: #ffffff;\n"
            		+ "    border: solid 2px #ccc;\n"
            		+ "    border-radius: 2px;\n"
            		+ "    display: inline-block;\n"
            		+ "    height: 100px;\n"
            		+ "    line-height: 100px;\n"
            		+ "    margin: 5px;\n"
            		+ "    position: relative;\n"
            		+ "    text-align: center;\n"
            		+ "    vertical-align: middle;\n"
            		+ "    width: 100px;\n"
            		+ "}\n"
            		+ "\n"
            		+ ".descarga span {\n"
            		+ "    background: #f2594b;\n"
            		+ "    border-radius: 4px;\n"
            		+ "    color: #ffffff;\n"
            		+ "    display: inline-block;\n"
            		+ "    font-size: 11px;\n"
            		+ "    font-weight: 700;\n"
            		+ "    line-height: normal;\n"
            		+ "    padding: 5px 10px;\n"
            		+ "    position: relative;\n"
            		+ "    text-transform: uppercase;\n"
            		+ "    z-index: 1;\n"
            		+ "    top: 45%;\n"
            		+ "    text-align: center;\n"
            		+ "    max-width: 95%;;\n"
            		+ "    text-overflow: ellipsis;\n"
            		+ "    overflow: hidden;\n"
            		+ "    white-space: nowrap;\n"
            		+ "}\n"
            		+ "\n"
            		+ ".descarga span:last-child {\n"
            		+ "    margin: auto;\n"
            		+ "}\n"
            		+ "\n"
            		+ ".descarga:before,\n"
            		+ ".descarga:after {\n"
            		+ "    background: #ffffff;\n"
            		+ "    border: solid 3px #9fb4cc;\n"
            		+ "    border-radius: 4px;\n"
            		+ "    content: '';\n"
            		+ "    display: block;\n"
            		+ "    height: 35px;\n"
            		+ "    left: 50%;\n"
            		+ "    margin: -17px 0 0 -12px;\n"
            		+ "    position: absolute;\n"
            		+ "    top: 50%;\n"
            		+ "    /*transform:translate(-50%,-50%);*/\n"
            		+ "    \n"
            		+ "    width: 25px;\n"
            		+ "}\n"
            		+ "\n"
            		+ ".descarga:hover:before,\n"
            		+ ".descarga:hover:after {\n"
            		+ "    background: #e2e8f0;\n"
            		+ "}\n"
            		+ "/*a:before{transform:translate(-30%,-60%);}*/\n"
            		+ "\n"
            		+ ".descarga:before {\n"
            		+ "    margin: -23px 0 0 -5px;\n"
            		+ "}\n"
            		+ "\n"
            		+ ".descarga:hover {\n"
            		+ "    background: #e2e8f0;\n"
            		+ "    border-color: #9fb4cc;\n"
            		+ "}\n"
            		+ "\n"
            		+ ".descarga:active {\n"
            		+ "    background: #dae0e8;\n"
            		+ "    box-shadow: inset 0 2px 2px rgba(0, 0, 0, .25);\n"
            		+ "}\n"
            		+ "\n"
            		+ ".descarga span:first-child {\n"
            		+ "    display: none;\n"
            		+ "}\n"
            		+ "\n"
            		+ ".descarga:hover span:first-child {\n"
            		+ "    display: inline-block;\n"
            		+ "}\n"
            		+ "\n"
            		+ ".descarga:hover span:last-child {\n"
            		+ "    display: none;\n"
            		+ "}\n"
            		+ "</style>" ;
            
			
			html.append(css);
			html.append(html1);
			//html.append(script);
			
			 //html.append("<a" + " " + "href=\"" + entry.getValue() + "\" class=\"fas fa-file-download\">" + attachesNames.get(entry.getKey()) + "</a>");
			 //html.append("<div" + " " + "class=\""  + attachesNames.get(entry.getKey()) +  "\"> ");
			 //html.append("<i class=\"fas fa-file-download\" aria-hidden=\"true\"></i>");
			 //html.append("<p style=\"display: inline-block\">" + attachesNames.get(entry.getKey()) + "</p>");
		}
        
		System.out.println(html.toString());
		return html.toString();
	}

	private static Map<String, String> formatUrlToImages(String messageId, List<String> attachIds, Map<String, String> attachesNames) {

		HashMap<String, String> imagesWithURL = new HashMap<>();
		for (String attachId : attachIds) {
			imagesWithURL.put(attachId, "https://jkmoqwh5wdc2adjdbyjj2sc6eq0donyi.lambda-url.eu-west-1.on.aws/soporte/"
					+ messageId + "/" + attachId + "/" + attachesNames.get(attachId));
		}

		return imagesWithURL;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException, MessagingException, URISyntaxException, InterruptedException {
		String messageId = "rle89n3aslrm5sn6btc7qg2lpkm9u5fdu74lipo1";
		try (InputStream is = new FileInputStream("/home/asolaun/Descargas/rle89n3aslrm5sn6btc7qg2lpkm9u5fdu74lipo1")) {
			List<String> messageResults = new ArrayList<>();
			messageResults = getHtmlMessage(is, messageId);
			handleTask("anderysalma@gmail.com", "issues-test.aonsolutions.org", messageResults.get(0), messageResults.get(1), messageResults.get(2));
		}

	}

}
