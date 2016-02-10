package com.esferalia.aon.gwt.viewer.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.annotation.WebServlet;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.viewer.client.IViewer;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.google.api.services.drive.model.File;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
@WebServlet(name = "Viewer Servlet", urlPatterns = { "/aon_gwt_document/Viewer" })
public class ViewerImpl extends AonRemoteServiceServlet implements IViewer {

	private static final String ZOOM_PARAM = "zoom";
	private static final String PAGE_PARAM = "page";
	
	@Override
	public String getAsHTML(Attach attach, int zoom) {
		
		IDocument2HtmlConverter converter;
		if(attach.getDriveId() != null || attach.getData() != null || 
				(attach.getId() != null && attach.getAttachType() != null)){
			Integer attachId = attach.getId();
			if(attach.getDriveId() == null && attach.getData() == null)
				attach = AON.getAttach(attach.getDomain().getName(), attach.getDomain().getId(), "",
						f-> f.getIdProperty().eq(attachId), attach.getAttachType());
			if(attach.getMd5() == null) attach.setMd5(getMd5(attach));
			converter = getDocument2HtmlConverter(attach);
		} else converter = Error2HtmlConverter.INSTANCE; 
	
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			converter.transform(attach, os, zoom);
			os.flush();
			return os.toString();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	private IDocument2HtmlConverter getDocument2HtmlConverter(Attach attach) {
		return DOC2HTML_CONVERTERS.get(attach.getMimeType());
	}
	
	public static final Map<MimeType, IDocument2HtmlConverter> DOC2HTML_CONVERTERS = 
			new HashMap<MimeType, IDocument2HtmlConverter>(){
		private static final long serialVersionUID = 1L;

		{
			put(MimeType.PDF, OpenDocument2HtmlConverter.INSTANCE);
			put(MimeType.MS_WORD, OpenDocument2HtmlConverter.INSTANCE );
			put(MimeType.MS_WORD_2007, OpenDocument2HtmlConverter.INSTANCE );
			put(MimeType.MS_EXCEL, OpenDocument2HtmlConverter.INSTANCE );
			put(MimeType.MS_EXCEL_2007, OpenDocument2HtmlConverter.INSTANCE );
			put(MimeType.MS_POWER_POINT, OpenDocument2HtmlConverter.INSTANCE );
			put(MimeType.MS_POWER_POINT_2007, OpenDocument2HtmlConverter.INSTANCE );
			put(MimeType.HTML, Noop2HtmlConverter.INSTANCE );
			put(MimeType.TXT, Noop2HtmlConverter.INSTANCE );

			put(MimeType.BMP, Image2HtmlConverter.INSTANCE );
			put(MimeType.JPEG, Image2HtmlConverter.INSTANCE );
			put(MimeType.PNG, Image2HtmlConverter.INSTANCE );
			put(MimeType.GIF, Image2HtmlConverter.INSTANCE );
			
			put(MimeType.ZIP, Zip2HtmlConverter.INSTANCE);
		}
	};
	
	private static interface IDocument2HtmlConverter {
		void transform(Attach attach, OutputStream os, int zoom) throws Exception;
	}
	
	private static class Zip2HtmlConverter implements IDocument2HtmlConverter{
		private static IDocument2HtmlConverter INSTANCE = new Zip2HtmlConverter();
		
		@Override
		public void transform(Attach attach, OutputStream os, int zoom)
				throws Exception {
			User user = new User().setLogin("");
			PrintStream printStream = new PrintStream(os);
			InputStream in = null;
			if(attach.getDriveId() != null){ 
				byte[] b = DriveUtils.getByteFile(attach, user);
				in = new ByteArrayInputStream(b);
			}
			else {
				in = new ByteArrayInputStream(attach.getData());
			}
			ZipInputStream zip = new ZipInputStream(in);
			ZipEntry entry;
			String html="<div class='page' style=' width:150%s; background-color:#FFF;'>"
					+ "<table style='padding-top:10px; padding-bottom:5px;'>";
			while (null != (entry=zip.getNextEntry()) ){
				String icon = entry.isDirectory()?"aon-icon-google-drive-folder":"aon-icon-google-drive-unknown";
				if(!entry.getName().substring(0,entry.getName().length()-1).contains("/")){
					if(entry.isDirectory())
						html = html + "<tr><td style='padding-left:5px;'><button onclick='alert(hola);' class='aon-editDataTable-button "+icon+"' style='padding-left: 20px;'>"+entry.getName()+"</button></td></tr>";
					else{
						html = html + "<tr><td style='padding-left:5px;'><span class='"+icon+"' style='padding-left: 20px;'>"+entry.getName()+"</span></td></tr>";
					}
				}
			}
			html = html + "</table></div>";
			printStream.printf(html,"%");
		}
	}
	
	private static class OpenDocument2HtmlConverter implements IDocument2HtmlConverter {
		
		private static  IDocument2HtmlConverter INSTANCE = new OpenDocument2HtmlConverter();

		@Override
		public void transform(Attach attach, OutputStream os, int zoom) throws Exception {

			PrintStream printStream = new PrintStream(os);
			
			PDFFile pdfFile = OpenDocument2ImageServlet.getPDFFile(attach);
			Integer page = pdfFile.getNumPages();
			
			for(Integer i = 1; i<= page; i++){
				PDFPage pdfPage = pdfFile.getPage(i);
				double width =  pdfPage.getBBox().getWidth() * zoom / 100;
				double height = pdfPage.getBBox().getHeight() * zoom / 100;
				
				printStream.printf("<div class='page' style='width:%dpx;height:%dpx;'   ><img src='openDocument2Image/%s.png?%s=%d&%s=%d&id=%d'></img> </div>",
						(long)width,
						(long)height,
						attach.getMd5(),
						PAGE_PARAM, i,
						ZOOM_PARAM, zoom,
						attach.getId());
			}
		}
	}

	private static class Error2HtmlConverter implements IDocument2HtmlConverter {
		private static Error2HtmlConverter INSTANCE = new Error2HtmlConverter();
		
		@Override
		public void transform(Attach attach, OutputStream os, int zoom) throws Exception {
			PrintStream printStream = new PrintStream(os);
			
			String html="<div class='page' style=' width:150%s; background-color:#FFF;'>"
					+ "<table style='padding-top:10px; padding-bottom:5px;'>";
			String icon = "aon-icon-google-drive-unknown";
			html = html + "<tr><td style='padding-left:5px;'><span> No es posible visualizar el archivo </span></td></tr>";
			html = html + "<tr><td style='padding-left:5px;'><span class='"+icon+"' style='padding-left: 20px;'>"+attach.getDescription()+"</span></td></tr>";
		
			html = html + "</table></div>";
			printStream.printf(html,"%");
		}
	}
	
	private static class Noop2HtmlConverter  extends Document2HtmlConverter  {
	
		private static  IDocument2HtmlConverter INSTANCE = new Noop2HtmlConverter();
	
		@Override
		void transform(InputStream is, OutputStream os) throws Exception {
			int read ;
			byte buffer [] = new byte [256];
			while ( ( read = is.read(buffer)) == buffer.length ) {
				os.write(buffer, 0, read);
			}
		}
	}
	
	private static class Image2HtmlConverter implements IDocument2HtmlConverter {
	
		private static  IDocument2HtmlConverter INSTANCE = new Image2HtmlConverter();

		@Override
		public void transform(Attach attach, OutputStream os, int zoom) throws Exception {
		
			PrintStream printStream = new PrintStream(os);
			byte[] data;
			if(attach.getDriveId() != null)
				data = DriveUtils.getByteFile(attach, new User().setLogin(""));
			else data = attach.getData();

			OpenDocumentConverterServlet.IMGS.put(attach.getMd5(), data);
			printStream.printf("<div class='page'  ><img src='openDocumentConverter/%s.%s'></img> </div>",
				attach.getMd5(),
				attach.getMimeType().getExtension());
		}
	}
	private abstract static class Document2HtmlConverter implements IDocument2HtmlConverter{
		
		@Override
		public void transform(Attach attach, OutputStream os, int zoom) throws Exception {
			ByteArrayInputStream is = new ByteArrayInputStream(attach.getData() != null ? attach.getData()
					: DriveUtils.getByteFile(attach, new User().setLogin("")));
			transform(is, os);
		}

		abstract void transform(InputStream is, OutputStream os) throws Exception;
	
	}
	
	public String getMd5(Attach attach){
		if(attach.getDriveId() != null){
        	File file = DriveUtils.getDriveFile(attach, new User().setLogin(""));
        	return file.getMd5Checksum();
		} else if(attach.getData() != null){
			return AonFileUtils.getMD5Checksum(attach.getData());
		}
		return "null";
	}
	
	public String getData(Attach attach){
		byte[] b = "".getBytes();
		if(attach.getDriveId() != null || attach.getData() != null || 
				(attach.getId() != null && attach.getAttachType() != null)){
			Integer attachId = attach.getId();
			if(attach.getDriveId() == null && attach.getData() == null)
				attach = AON.getAttach(attach.getDomain().getName(), attach.getDomain().getId(), "",
						f-> f.getIdProperty().eq(attachId), attach.getAttachType());
			if(attach.getDriveId() != null){
				b = DriveUtils.getByteFile(attach, new User().setLogin(""));
			} else b = attach.getData();
		}
		String md5 = AonFileUtils.getMD5Checksum(b);
		getThreadLocalRequest().getSession().setAttribute(md5, b);
		return md5;
	}
	
	public byte[] getByteArray(Attach attach){
		byte[] b = "".getBytes();
		if(attach.getDriveId() != null || attach.getData() != null || 
				(attach.getId() != null && attach.getAttachType() != null)){
			Integer attachId = attach.getId();
			if(attach.getDriveId() == null && attach.getData() == null)
				attach = AON.getAttach(attach.getDomain().getName(), attach.getDomain().getId(), "",
						f-> f.getIdProperty().eq(attachId), attach.getAttachType());
			if(attach.getDriveId() != null){
				b = DriveUtils.getByteFile(attach, new User().setLogin(""));
			} else b = attach.getData();
		}
		return b;
	}
	
	public void share(String email, Attach attach){	
		try {
			DriveUtils.setPermission(attach.getDomain(), new User().setLogin(""), attach, email);
		} catch (IOException | GeneralSecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void sendGmail(String to, String issue, String message, Attach attach){

	}
	
	public void sendEmail(Domain domain, String from, String to, String issue, String message, Attach attach){
		Integer mailAccountId = Integer.parseInt(from);
		String encode = Base64.getEncoder().encodeToString(getByteArray(attach));
		try {
			JSONObject json = new JSONObject();
			json.put("mailAccountId", mailAccountId)
				.put("recipientsTo", to)
				.put("content", message)
				.put("subject", issue)
				.put("attachName", attach.getDescription())
				.put("mimetype",attach.getMimeType().value())
				.put("md5", encode)
				.put("login", getUser().getLogin())
				.put("domainName", domain.getName())
				.put("domainId", domain.getId());
			
			sendPostHttpClient(domain.getName(),json);			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	protected static void sendPostHttpClient(String domainName, JSONObject json) {
		try{
			String url = "http://"+domainName+"/send_email/";
			HttpClientBuilder base = HttpClientBuilder.create();
			HttpClient client = base.build();
			HttpPost post = new HttpPost(url);
			List<NameValuePair> urlParameters =  new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("details", json.toString()));
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			client.execute(post);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public LinkedList<MailAccount> getMailAccountList(Domain domain) {
		User user = getUser();
		LinkedList<MailAccount> list = AON.getMailAccountList(domain.getName(), domain.getId(), user.getLogin(), 
				f -> (f.getUserIdProperty().isNull().or(f.getUserIdProperty().eq(user.getId()))).and(f.getDomainProperty().eq(user.getDomain())));
		list.stream().forEach(ma -> {
			ma.setSignatureStr(getSignature(domain, user, ma.getSignatureId()));
		});
		return list;
	}
	
	public MailAccount getMailAccount(Domain domain, Integer mailAccountId) {
		User user = getUser();
		return AON.getMailAccount(domain.getName(), domain.getId(), user.getLogin(), 
				f -> f.getIdProperty().eq(mailAccountId));
	}
	
	public static String getSignature(Domain domain, User user, Integer signatureId){
		return AON.getSignature(domain.getName(), domain.getId(), user.getLogin(), signatureId)
				.getSignature();
	}
	
	public User getUser(){
		return new User()
				.setId(getUserID())
				.setLogin(getUserLogin())
				.setDomain(getUserDomainID());
	}
}
