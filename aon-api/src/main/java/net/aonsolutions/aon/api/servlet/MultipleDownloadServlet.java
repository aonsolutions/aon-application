package net.aonsolutions.aon.api.servlet;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.InvoiceAttachmentType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.json.TediInvoiceJSON;
import net.aonsolutions.aon.google.apis.drive.AonDrive;
import net.aonsolutions.aon.google.apis.drive.SearchFiles;

@WebServlet(name = "MultipleDownloadServlet", urlPatterns = {"/ms/api/multiple_download/*"})
public class MultipleDownloadServlet extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER  = Logger.getLogger(MultipleDownloadServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject json = new JSONObject(decode(req.getParameter("json").getBytes()));
		Integer domainId = json.getInt("domain_id");
		String domainName = json.getString("domain_name");
		String login = json.getString("domain_login");
		Domain domain = AON.getDomain(domainName, domainId, login);
		User user = AON.getUser(domainName, domainId, login);
		
		LinkedList<File> list = new LinkedList<>();
		String[] pathInfo = req.getPathInfo()!= null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo().split("/") : null;
		String filename = "";
		if(pathInfo != null) {
			if("invoice".equalsIgnoreCase(pathInfo[1])) {
				list = getInvoiceFiles(domain, user, json);
				filename = "facturas.zip";
			}
			
			if("document".equalsIgnoreCase(pathInfo[1])) {
				list = getDocumentalFiles(domain, user, json);
				filename = "documentos.zip";
			}
		}
		
        resp.addHeader("Content-Disposition","attachment; filename=\""+ filename +"\"");
        resp.setContentType(MimeType.ZIP.getName());
        
		ServletOutputStream out = resp.getOutputStream();
		generateZipFile(list, out);
        out.flush();
        out.close();
	}
	
	public String decode(byte[] value){
		String decode = "";
		try{
			decode = new String(Base64.getDecoder().decode(value), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return decode;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("AON API INVOICE SERVLET - POST METHOD");
		String token = req.getHeader("session_id");
		Integer domainId = AonNumberUtils.toInteger(req.getHeader("domain_id"));
		String domainName = req.getHeader("domain_name");	
		Domain domain = AON.getDomain(domainName, domainId, "");
		User user = AON_SOLUTIONS.getUser(domain, token);
		JSONObject json = Utils.getRequestJSON(req);
		
		LinkedList<File> list = new LinkedList<>();
		String[] pathInfo = req.getPathInfo()!= null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo().split("/") : null;
		String filename = "";
		if(pathInfo != null) {
			if("invoice".equalsIgnoreCase(pathInfo[1])) {
				list = getInvoiceFiles(domain, user, json);
				filename = "facturas.zip";
			}
			
			if("document".equalsIgnoreCase(pathInfo[1])) {
				list = getDocumentalFiles(domain, user, json);
				filename = "documentos.zip";
			}
		}
		
        resp.addHeader("Content-Disposition","attachment; filename=\""+ filename +"\"");
        resp.setContentType(MimeType.ZIP.getName());
        
		ServletOutputStream out = resp.getOutputStream();
		generateZipFile(list, out);
        out.flush();
        out.close();
	}

	
	private LinkedList<File> getInvoiceFiles(Domain domain, User user, JSONObject json) {
   		LinkedList<Integer> ids = toList(json.optJSONArray("ids"));
		Integer[] idsArray = ids.toArray(new Integer[ids.size()]);
		LinkedList<File> list = new LinkedList<>();
		
		InvoiceStatus st = getInvoiceStatus(json.optString("status"));
    	if(InvoiceStatus.SCORED.equals(st)) {
    		AON.getAttachStream(domain.getName(), domain.getId(), user.getLogin(), f -> f.getAttachModuleProperty().in(idsArray)
    				.and(f.getTypeProperty().eq(InvoiceAttachmentType.INVOICE.value())), AttachType.INVOICE, true).forEach(r -> {
				try {
					File file = File.createTempFile(r.getDescription(), "." + r.getMimeType().getExtension());
					AonFileUtils.writeByteArrayToFile(file, r.getData());
					list.add(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
    		});
    	} else { 
    		AON.getRawdocFullStream(domain.getName(), domain.getId(), user.getLogin(), f -> f.getIdProperty().in(idsArray)).forEach(r -> {
    			JSONObject data = new JSONObject(r.getJson());
    			String name = data.opt("reference") != null ? data.optString("reference") : "invoice";
				try {
					if(r.getData() != null) {
						File file = File.createTempFile(name, "." + r.getMimeType().getExtension());
						AonFileUtils.writeByteArrayToFile(file, r.getData());
						list.add(file);
					} else {
						TediInvoice invoice = TediInvoiceJSON.fromJSON(data);
						list.add(InvoicePdfServlet.createPdf(invoice));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
    		});
    	}
		return list;
	}
	
	private LinkedList<File> getDocumentalFiles(Domain domain, User user, JSONObject json) {
		String type = json.opt("type")!= null ? json.optString("type") : "";
		RegistryAttachmentType t = type.equalsIgnoreCase("system") ? RegistryAttachmentType.SYSTEM_MESSAGE : RegistryAttachmentType.CORPORATE_IDENTITY;
   		LinkedList<Integer> ids = toList(json.optJSONArray("ids"));
		Integer[] idsArray = ids.toArray(new Integer[ids.size()]);
		LinkedList<File> list = new LinkedList<>();
			
		DomainGserviceaccount g = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), user.getLogin());
		Drive drive = AonDrive.getInstace().serviceInitialize(g);
		
		AON.getAttachStream(domain.getName(), domain.getId(), user.getLogin(), f -> f.getTypeProperty().eq(t.value()).and(f.getIdProperty().in(idsArray)), AttachType.REGISTRY, true)
		.forEach(r -> {
			if(r.getData() == null && r.getDriveId() != null) {
				String[] keys = {"fileId", "aontype", "domain"};
				String[] values = {r.getId() + "", "registry", r.getDomain().getName()};
				FileList fl = SearchFiles.searchFilesAppProperties(drive, keys, values);
				if(fl.getFiles().size() > 0) {
					if(!fl.getFiles().get(0).getId().equals(r.getDriveId())) {
						r.setDriveId(fl.getFiles().get(0).getId());
						AON.updateAttach(domain.getName(), domain.getId(), "", r);
					}
					if("0".equals(r.getDparentId())) {
						r.setDparentId(fl.getFiles().get(0).getSize().toString());
						AON.updateAttach(domain.getName(), domain.getId(), "", r);
					}
				}
				r.setData(AonDrive.getInstace().downloadFileByteArray(drive, r.getDriveId()));
			} 
			try {
				File file = File.createTempFile(r.getDescription(), "." + r.getMimeType().getExtension());
				AonFileUtils.writeByteArrayToFile(file, r.getData());
				list.add(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		return list;
	}
	
	private static InvoiceStatus getInvoiceStatus(String status) {
		InvoiceStatus st = InvoiceStatus.safeValueOf(status);
		if(st == null) {
			if("inbox".equalsIgnoreCase(status) || "pending".equalsIgnoreCase(status)
					|| "verified".equalsIgnoreCase(status)) {
				st = InvoiceStatus.PENDING;
			} else if("accepted".equalsIgnoreCase(status) || "scored".equalsIgnoreCase(status) || "accounting".equalsIgnoreCase(status)) {
				st = InvoiceStatus.SCORED;
			} else if("refused".equalsIgnoreCase(status) || "rejected".equalsIgnoreCase(status)) {
				st = InvoiceStatus.REFUSED; 
			} else if("trash".equalsIgnoreCase(status) || "draft".equalsIgnoreCase(status)) {
				st = InvoiceStatus.TRASH;
			}
		}
		return st;
	}
	
	public static LinkedList<Integer> toList(JSONArray array) {
	    if(array==null || array.isEmpty())
	        return new LinkedList<>();
	    LinkedList<Integer> list = new LinkedList<>();
	    for(int i=0; i<array.length(); i++) {
	    	list.add(array.optInt(i));
	    }
	    return list;
	}

	public ZipOutputStream generateZipByteArray(LinkedList<byte[]> list, OutputStream os) throws IOException {
		ZipOutputStream zos = new ZipOutputStream(os);
		Closeable res = zos;
		try {
			for (byte[] b : list) {
				String fileName = "file";
				zos.putNextEntry(new ZipEntry(fileName));
				copy(b, zos);
				zos.closeEntry();
			}
		} finally {
			res.close();
		}
		
		return zos;
	}
	
	public ZipOutputStream generateZipFile(LinkedList<File> list, OutputStream os) throws IOException {
		ZipOutputStream zos = new ZipOutputStream(os);
		Closeable res = zos;
		try {
			for (File file : list) {
				String fileName = file.getName();
				zos.putNextEntry(new ZipEntry(fileName));
				copy(file, zos);
				zos.closeEntry();
			}
		} finally {
			res.close();
		}
		
		return zos; 
		
	}
	
	private static void copy(File file, OutputStream out) throws IOException {
		InputStream in = new FileInputStream(file);
	    try {
	    	copy(in, out);
	    } finally {
	    	in.close();
	    }
	}
	
	private static void copy(byte[] b, OutputStream out) throws IOException {
		InputStream in = new ByteArrayInputStream(b);
	    try {
	    	copy(in, out);
	    } finally {
	    	in.close();
	    }
	}

	private static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
	    while (true) {
	    	int readCount = in.read(buffer);
	    	if (readCount < 0) {
	    		break;
	    	}
	    	out.write(buffer, 0, readCount);
	    }
	}
}
