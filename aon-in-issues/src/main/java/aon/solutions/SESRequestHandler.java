package aon.solutions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.print.DocFlavor.STRING;
import javax.xml.bind.DatatypeConverter;

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
import aon.solutions.SendSimpleEmail;


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
				handleTask(sources.get(i), dests.get(0), messageResults.get(0), messageResults.get(1), messageResults.get(2));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		

		return null;
	}
	
	private static List<String> getHtmlMessage(InputStream is, String messageId) throws MessagingException, IOException, NoSuchAlgorithmException {
		
		List<String> attachIdList = new ArrayList<>();
		List<String> result = new ArrayList<>();
		List<String> fileNames = new ArrayList<>();
		Map<String, String> imagesWithURL = new HashMap<>();
		Map<String, String> attachesNames = new HashMap<>();
		Map<String, String> base64 = new HashMap<>();
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
			attachIdList = getAttachIds(bodyPart, attachIdList, attachesNames, base64);
			if(bodyPart.getContentType().startsWith("multipart/related")) {
				Multipart m = (Multipart) bodyPart.getContent();
				System.out.println(m.getBodyPart(0).getContentType());
				BodyPart b = m.getBodyPart(0);
				Multipart m2 = (Multipart) b.getContent();
				html =  m2.getBodyPart(1).getContent().toString();
				BodyPart bAttach = m.getBodyPart(1);
				System.out.println(m.getBodyPart(1).getContent());
				attachIdList = getAttachIds(bAttach, attachIdList, attachesNames, base64);
				
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
		String attaches = attachString(imagesWithURL, attachesNames, base64);
		result.add(finalHtml);
		result.add(subject);
		result.add(attaches);
        
		System.out.println(finalHtml);
		
		return result;

		
	}
	


	private static void handleTask(String from, String domainTo, String message, String subject, String attaches)
			throws URISyntaxException, IOException, InterruptedException, ConnectException {
		
		try {
        String[] emailParts = domainTo.split("@");	
        String to = emailParts[1];
        System.out.println(from);
        System.out.println(to);
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

		} else {
			SendSimpleEmail.send(domainTo , from );
		}

		} catch(ConnectException e) {
			
			SendSimpleEmail.send(domainTo , from );
		}
	
	}

	private static List<String> getAttachIds(BodyPart bodyPart, List<String> attachIds, Map<String, String> attachesNames, Map<String, String> base64) throws MessagingException, IOException, NoSuchAlgorithmException {

		Enumeration<Header> headers = bodyPart.getAllHeaders();
		String fileName = "";
		String attachId = "";
		String base64String = "";
		for (Iterator<Header> iterator = headers.asIterator(); iterator.hasNext();) {
			Header header = iterator.next();
			System.out.println(header.getName() + "=" + header.getValue());
			if(header.getName().equals("Content-Disposition")) {
				String headerName[] = header.getValue().split(";");
				String headerValue = headerName[1].substring(10);
				fileName = headerValue.replaceAll("\"", "");
				System.out.println(fileName);
				String md5 = convertToMD5(bodyPart.getInputStream());
				attachIds.add(md5);
				attachId = md5;
			}
			if (header.getName().equals("Content-ID")) {
				attachIds.add(header.getValue().replaceAll("[<>]", ""));
				attachId  = header.getValue().replaceAll("[<>]", "");
				
			}
			
			base64String = convertStreamToString(bodyPart.getInputStream());
			
		}
        
		if(!fileName.isBlank()) attachesNames.put(attachId, fileName);
		if(!fileName.isBlank()) base64.put(attachId, base64String);
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
	
	private static String convertToMD5(InputStream message) throws NoSuchAlgorithmException, IOException {
	    	
	    	MessageDigest md = MessageDigest.getInstance("MD5");
	        byte[] digest = md.digest(message.readAllBytes());
	        String myHash = bytesToHex(digest).toUpperCase();
	    	
	    	return myHash;
	    	
	    }
	
	private  static String convertStreamToString(InputStream is) {
		  
		String result = new BufferedReader(new InputStreamReader(is))
				   .lines().collect(Collectors.joining("\n"));
		return result;
		  
		}
	
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

	private static String attachString(Map<String, String> imagesWithURL, Map<String, String> attachesNames, Map<String, String> base64) {
        
		StringBuilder html = new StringBuilder();
		for (Map.Entry<String, String> entry : imagesWithURL.entrySet()) {
			 
			if(!html.isEmpty()) {
				html.append(","); 
				html.append(" "); 
			}
			
			
			
			
			
			
			if(attachesNames.get(entry.getKey()).endsWith("jpg")) {
			
				
				
				String html1= "<body>\n"
						+ "   \n"
						+ "\n"
						+ "<div>\n"
						+ "    <a class=\"descarga\" href=\"" + entry.getValue() + "\" onmouseover=\"document.getElementById('popupContainer').style.display = 'block'\" onmouseout=\"document.getElementById('popupContainer').style.display = 'none'\" title=\"" + attachesNames.get(entry.getKey()) + "\" ><span>Descargar</span><span>" + attachesNames.get(entry.getKey()) + "</span></a>\n"
						+ "</div>\n"
						+ "<div id=\"popupContainer\" class=\"" + entry.getKey() + "\">\n"
						+ "        <img src=\"" + entry.getValue() + "\" alt=\"Imagen\">\n"
						+ "    </div>"
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
	            		+ "        /* Imagen dentro del contenedor pop-up */\n"
	            		+ "." + entry.getKey() + "{\n"
	            		+ "max-width: 300px;\n"
	            		+ "max-height: 200px;\n"
	            		+ "box-shadow: rgba(0, 0, 0, 0.35) 0px 5px 15px;\n"
	            		+ "margin-top: -100px;\n"
	            		+ "margin-left: 40px;\n"
	            		+ "z-index: 69;\n"
	            		+ "position: relative;"
	            		+ "}"
	            		+ "</style>" ;
				
			
				html.append(html1);
				html.append(css);
				
			} else {
			
			String html1= "<body>\n"
					+ "   \n"
					+ "\n"
					+ "<div>\n"
					+ "    <a class=\"descarga\" target=\"_blank\" href=\""+entry.getValue() +"\" title=\"" + attachesNames.get(entry.getKey()) + "\" ><span>Descargar</span><span>" + attachesNames.get(entry.getKey()) + "</span></a>\n"
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
			
			}
			 
		}
        
		System.out.println(html.toString());
		return html.toString();
	}

	private static Map<String, String> formatUrlToImages(String messageId, List<String> attachIds, Map<String, String> attachesNames) {

		HashMap<String, String> imagesWithURL = new HashMap<>();
		for (String attachId : attachIds) {
			if(attachesNames.get(attachId) != null) {
			imagesWithURL.put(attachId, "https://jkmoqwh5wdc2adjdbyjj2sc6eq0donyi.lambda-url.eu-west-1.on.aws/soporte/"
					+ messageId + "/" + attachId + "/" + attachesNames.get(attachId));
			}
		}

		return imagesWithURL;
	}
	
	private static void checkConn() throws URISyntaxException, IOException, InterruptedException {
    	HttpRequest httpRequest = HttpRequest
    	.newBuilder(new URI("https://issues-test.aonsolutions.org/"))
    	.GET()
    	.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(httpRequest, BodyHandlers.ofString());
	}

	public static void main(String[] args) throws FileNotFoundException, IOException, MessagingException, URISyntaxException, InterruptedException, NoSuchAlgorithmException {
		String messageId = "rtef9svrhhfmo82sahfnobmsg5gd7gosdhqubmo1";
		try (InputStream is = new FileInputStream("/home/asolaun/Descargas/rtef9svrhhfmo82sahfnobmsg5gd7gosdhqubmo1")) {
			List<String> messageResults = new ArrayList<>();
			messageResults = getHtmlMessage(is, messageId);
			handleTask("anderysalma@gmail.com", "soporte@issues-test.aonsolutions.org", messageResults.get(0), messageResults.get(1), messageResults.get(2));
		}

	}

}
