package net.aonsolutions.aon.api.servlet;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.in.payroll.pdf.maker.PdfMaker;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.InvoiceAttachmentType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.doc.ExternalStorage.ExternalStorageVisitor;
import com.esferalia.aon.occam.api.model.doc.InvoiceDoc;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceFilter;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonRandomStringUtils;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.google.apis.drive.AonDrive;
import net.aonsolutions.aon.google.apis.drive.SearchFiles;
import net.aonsolutions.aon.tbai.TbaiData;
import solutions.aon.aws.s3.S3;

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
		Integer domainId = JsonUtils.getInteger(json, IJsonNames.DOMAIN_ID);
		String domainName = JsonUtils.getString(json, IJsonNames.DOMAIN_NAME);
		String login = JsonUtils.getString(json, IJsonNames.DOMAIN_LOGIN);
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
		Occam occam = new Occam().setDomain(domain.getId()).setDomainName(domain.getName()).setUser(user.getLogin());
		
		LinkedList<Integer> ids = toList(json.optJSONArray("ids"));
		Integer[] idsArray = ids.toArray(new Integer[ids.size()]);
		LinkedList<File> list = new LinkedList<>();
		
		String status = JsonUtils.getString(json, IJsonNames.STATUS);
		
		if(!AonStringUtils.isBlank(status) && !isRawdoc(status)) {
			if(ids.isEmpty()) {
				InvoiceFilter filter = new InvoiceFilter()
						.setDescription(JsonUtils.getString(json, "description"))
						.setStatus(JsonUtils.getString(json, IConstants.STATUS))
						.setRecorded(InvoiceStatus.safeValueOf2(JsonUtils.getString(json, "recorded")) != null 
								? InvoiceStatus.safeValueOf2(JsonUtils.getString(json, "recorded")).value() : null)
						.setTypes(JsonUtils.has(json, IConstants.TYPE)
							? JsonUtils.getString(json, IConstants.TYPE).split(",") : null)
						.setFrom(JsonUtils.getDate(json, IJsonNames.FROM))
						.setTo(JsonUtils.getDate(json, IJsonNames.TO))
						.setRegistry(JsonUtils.getInteger(json, IJsonNames.REGISTRY));
				AON.getInvoiceStream(occam, f -> InvoiceServlet.invoiceFilter(f, domain.getId(), filter))
					.forEach(i -> {
						File file = getInvoiceFile(domain, user, occam, i.getId(), "Factura " + i.getReferenceCode());
						if(file != null) list.add(file);
					});
			} else {
				ids.stream().forEach(id -> {
    				File file = getInvoiceFile(domain, user, occam, id, null);
    				if(file != null) list.add(file);
    			});
			}
    	} else if(!AonStringUtils.isBlank(status) && isRawdoc(status)) { 
    		AON.getRawdocFullStream(domain.getName(), domain.getId(), user.getLogin(), f -> f.getIdProperty().in(idsArray)).forEach(r -> {
    			JSONObject data = new JSONObject(r.getJson());
    			String name = data.opt("reference") != null && !AonStringUtils.isBlank(data.optString("reference")) && data.optString("reference").length() > 2 
    					? "Factura " + data.optString("reference") : "Factura " + r.getId();
				try {
					if(r.getData() != null) {
						File file = File.createTempFile(name, "." + r.getMimeType().getExtension());
						AonFileUtils.writeByteArrayToFile(file, r.getData());
						list.add(file);
					} else if(AonStringUtils.isNotBlank(r.getS3Bucket()) && AonStringUtils.isNotBlank(r.getS3Key())) {
						byte[] f = S3.getInstance().download(r.getS3Bucket(), r.getS3Key());
						File file = File.createTempFile(name, ".pdf");
						AonFileUtils.writeByteArrayToFile(file, f);
						list.add(file);
					} else {
						CompanyFull company = AON.getCompanyFull(domain.getName(), domain.getId(), user.getLogin());
						Attach logo = new Attach();
						PrintInvoiceConfiguration config = AON_SOLUTIONS.getPrintInvoiceConfiguration(domain.getName(), domain.getId(), user.getLogin(), true);
						if(config.isLogo()) {
							Integer id = company.getRegistry().getId();
							logo = AON.getAttach(domain.getName(), domain.getId(), user.getLogin(), f-> f.getAttachModuleProperty().eq(id)
								.and(f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())), AttachType.REGISTRY);
						}
						
						Invoice invoice = InvoiceJSON.fromJSON(new JSONObject(r.getJson()));
						File file = File.createTempFile(name, ".pdf");
						FileOutputStream out = new FileOutputStream(file);
						PdfMaker.printInvoice(out, company, invoice, config, null, logo.getData(), "");
						list.add(file);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
    		});
    	}
		return list;
	}
	
	private File getInvoiceFile(Domain domain, User user, Occam occam, Integer id, String pfx) {
		InvoiceDoc invoiceDoc = AON.getInvoiceDoc(occam, occam.getDomain(), id).orElse(null);
		if(invoiceDoc != null) {
			return invoiceDoc.getExternalStorage().visit(new ExternalStorageVisitor<File>() {

				@Override
				public File visitAon() {
					Attach attach = AON.getAttach(domain.getName(), domain.getId(), user.getLogin(),  f -> f.getAttachModuleProperty().eq(id)
	        				.and(f.getTypeProperty().eq(InvoiceAttachmentType.INVOICE.value())), AttachType.INVOICE);
	    			if(attach != null && attach.getId() != null) {
	    				String prefix = pfx;
	    				if(AonStringUtils.isBlank(prefix)){
	    					prefix = attach.getDescription() != null && attach.getDescription().length() > 2 
	    							? attach.getDescription() : "invoice";
	    				}
	    				try {
	    					File f = File.createTempFile(prefix, "." + attach.getMimeType().getExtension());
	    					AonFileUtils.writeByteArrayToFile(f, attach.getData());
		    				return f;
		    			} catch (IOException e) {
	    					e.printStackTrace();
	    				}
	    			}
	    			return null;
				}

				@Override
				public File visitDrive() {
					return null;
				}

				@Override
				public File visitAws() {
					try {
						String prefix = pfx;
	    				if(AonStringUtils.isBlank(prefix)){
	    					prefix = invoiceDoc.getDescription() != null && invoiceDoc.getDescription().length() > 2 
    							? invoiceDoc.getDescription() : "invoice";
	    				}
						byte[] data = S3.getInstance().download(invoiceDoc.getS3Bucket(), invoiceDoc.getS3Key());
						File f = File.createTempFile(prefix, "." + invoiceDoc.getMimeType().getExtension());
    					AonFileUtils.writeByteArrayToFile(f, data);
	    				return f;
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				}

				@Override
				public File visitScaleway() {
					return null;
				}

			});
		} else {
			Invoice invoice = AON_SOLUTIONS.getInvoice(domain.getName(), domain.getId(), user.getLogin(), id);
			if(InvoiceType.SALES.equals(invoice.getType())) {
				CompanyFull company = AON.getCompanyFull(domain.getName(), domain.getId(), user.getLogin());
				Attach logo = new Attach();
				PrintInvoiceConfiguration config = AON_SOLUTIONS.getPrintInvoiceConfiguration(domain.getName(), domain.getId(), user.getLogin(), true);
				if(config.isLogo()) {
					Integer regId = company.getRegistry().getId();
					logo = AON.getAttach(domain.getName(), domain.getId(), user.getLogin(), f-> f.getAttachModuleProperty().eq(regId)
						.and(f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())), AttachType.REGISTRY);
				}
				
				try {
					File file = File.createTempFile("Factura " + invoice.getReferenceCode(), ".pdf");
					FileOutputStream out = new FileOutputStream(file);
					
					String qrUrl = "https://" +  domain.getName() + "/dip?d=" + company.getRegistry().getDocument() 
							+ "&f=" + AonDateUtils.simpleFormat(invoice.getIssueDate())
							+ "&s=" + invoice.getSeries()
							+ "&n=" + invoice.getNumber()
							+ "&t=" + invoice.getTotal();  
					TbaiConfiguration tbai = AON.getTbaiConfiguration(domain.getName(), domain.getId(), user.getLogin());
					String tbaiId = "";
					if(tbai.isActive()) {	
						TbaiData tbaiData = TbaiData.getInstance(tbai);
						String tbaiUrl = tbaiData.getTbaiUrl(domain.getName(), domain.getId(), user.getLogin(), invoice.getId());
						qrUrl = AonStringUtils.isBlank(tbaiUrl) ? qrUrl : tbaiUrl;
						tbaiId = tbaiData.getTbaiId(domain.getName(), domain.getId(), user.getLogin(), invoice.getId());
					}
					PdfMaker.printInvoice(out, company, invoice, config, qrUrl, logo.getData(), tbaiId);
					return file;
	    		} catch (IOException e) {
    				e.printStackTrace();
    			}
			}
		}
		return null;
	}
	
	private LinkedList<File> getDocumentalFiles(Domain domain, User user, JSONObject json) {
   		LinkedList<Integer> ids = toList(json.optJSONArray("ids"));
		Integer[] idsArray = ids.toArray(new Integer[ids.size()]);
		LinkedList<File> list = new LinkedList<>();
			
		DomainGserviceaccount g = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), user.getLogin());
		Drive drive = AonDrive.getInstace().serviceInitialize(g);
		
		List<Attach> attachs = AON.getAttachStream(domain.getName(), domain.getId(), user.getLogin(), 
				f -> f.getIdProperty().in(idsArray), 
		AttachType.REGISTRY, true)
			.collect(Collectors.toList());

		for(Attach attach: attachs) {
			if(attach.getData() == null && attach.getDriveId() != null) {
				String[] keys = {"fileId", "aontype", "domain"};
				String[] values = {attach.getId() + "", "registry", attach.getDomain().getName()};
				FileList fl = SearchFiles.searchFilesAppProperties(drive, keys, values);
				if(!fl.getFiles().isEmpty()) {
					if(!fl.getFiles().get(0).getId().equals(attach.getDriveId())) {
							attach.setDriveId(fl.getFiles().get(0).getId());
							AON.updateAttach(domain.getName(), domain.getId(), "", attach);
					}
					if("0".equals(attach.getDparentId())) {
							attach.setDparentId(fl.getFiles().get(0).getSize().toString());
							AON.updateAttach(domain.getName(), domain.getId(), "", attach);
					}
				}
				attach.setData(AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId()));
			} 
			try {
				File file = File.createTempFile(attach.getDescription(), "." + attach.getMimeType().getExtension());
				AonFileUtils.writeByteArrayToFile(file, attach.getData());
				list.add(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
		
		return list;
	}
	private static boolean isRawdoc(String status) {
		return "inbox".equalsIgnoreCase(status)
			|| "draft".equalsIgnoreCase(status) 
			||	"rejected".equalsIgnoreCase(status);
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
