package net.aonsolutions.aon.api.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.google.apis.drive.AonDrive;
import net.aonsolutions.aon.google.apis.drive.SearchFiles;

@WebServlet(name = "DownloadServlet", urlPatterns = {"/ms/api/download/*"})
public class DownloadServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER  = Logger.getLogger(DownloadServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("AON API DOWNLOAD SERVLET - GET METHOD");
		
		JSONObject json = new JSONObject(decode(req.getParameter("json").getBytes()));
		
		Integer domainId = JsonUtils.getInteger(json, IJsonNames.DOMAIN_ID);
		String domainName = JsonUtils.getString(json, IJsonNames.DOMAIN_NAME);
		String login = JsonUtils.getString(json, IJsonNames.DOMAIN_LOGIN);
		Domain domain = AON.getDomain(domainName, domainId, login);
		User user = AON.getUser(domainName, domainId, login);
		
		Integer rattach = JsonUtils.getInteger(json, IJsonNames.RATTACH);
		String attachType = JsonUtils.getString(json, IJsonNames.TYPE);
		
		Attach attach = AON.getAttach(domain.getName(), domain.getId(), user.getLogin(), f -> f.getIdProperty().eq(rattach), AttachType.getAttachType(attachType), true);
		
		File file = getDocumentalFile(domain, user, attach);
		
		resp.addHeader("Content-Disposition","attachment; filename=\"" + parseDescription(attach.getDescription()) + "." + attach.getMimeType().getExtension()+"\"");
        resp.setContentType(attach.getMimeType().getName());
		
		ServletOutputStream out = resp.getOutputStream();
		copy(file, out);
        out.flush();
        out.close();
	}
	
	private String parseDescription(String description) {
		if(AonStringUtils.containsIgnoreCase(description, "."))
			return description.split("\\.")[0];
		
		return description;
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
	
	private File getDocumentalFile(Domain domain, User user, Attach attach) {
   		DomainGserviceaccount g = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), user.getLogin());
		Drive drive = AonDrive.getInstace().serviceInitialize(g);
		
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
			return file;
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return null;
	}
	
	private static void copy(File file, OutputStream out) throws IOException {
		InputStream in = new FileInputStream(file);
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
