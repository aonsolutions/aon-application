package net.aonsolutions.aon.api.servlet.documental;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.task.TaskAttach;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.utils.ZipUtils;
import net.aonsolutions.aon.google.apis.drive.AonDrive;
import net.aonsolutions.aon.google.apis.drive.SearchFiles;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiFileServlet", urlPatterns = { "/ms/api/file/*" })
public class FileServlet extends AonApiHttpServlet{
	
	private static final Logger LOGGER  = Logger.getLogger(FileServlet.class.getName());
	private static final String DOMAIN_NAME = "domain_name";
	private static final String DOMAIN_ID = "domain_id";
	private static final String ATTACH_TYPE = "attach_type";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Aon Api File Servlet - GET METHOD");	
		try {
			AonApiData api = initialize(req, false);
			responseFile(resp, getFile(api, getJson(api, api.getData())));
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Aon Api File Servlet - POST METHOD");
		try {
			AonApiData api = initialize(req);
			responseFile(resp, getFile(api, getJson(api, api.getData())));
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private Attach getFile(AonApiData api, JSONObject json) {
		String domainName = json.optString(DOMAIN_NAME);
		Integer domainId = json.optInt(DOMAIN_ID);
		api.getDomain().setId(domainId);
		api.getDomain().setName(domainName);
		AttachType attachType = AttachType.getAttachType(json.getString(ATTACH_TYPE));
		Integer id = json.getInt(IJsonNames.ID);
		
		Attach attach;
		if(AttachType.RAWDOC == attachType) {
			attach = AON.getRawdocAttach(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
					f -> f.getDomainProperty().eq(api.getDomain().getId())
					.and(f.getIdProperty().eq(id)), attachType);
		} else if(AttachType.TASK == attachType) {
//			TaskAttach ta = AON_SOLUTIONS.getTaskAttach(api.getDomain(), api.getUser(), 
//					f -> f.getDomainProperty().eq(api.getDomain().getId())
//					.and(f.getIdProperty().eq(id)));
			attach = getTaskAttach(api, id);
//					new Attach(AttachType.TASK)
//					.setId(ta.getId())
//					.setDomain(api.getDomain())
//					.setMimeType(ta.getMimetype())
//					.setData(ta.getData())
//					.setDescription("document");
		} else {
			attach = AON.getAttach(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
					f -> f.getDomainProperty().eq(api.getDomain().getId())
						.and(f.getIdProperty().eq(id)) , attachType);
		}
		
		if(attach.getData() == null && attach.getDriveId() != null) {
			getDriveFile(api, attach);
		}
		
		return attach;
	}
	
	private Attach getDriveFile(AonApiData api, Attach attach) {
		DomainGserviceaccount g = AON.getDomainGserviceaccount(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		Drive drive = AonDrive.getInstace().serviceInitialize(g);
		String[] keys = {"fileId", "aontype", "domain"};
		String[] values = {attach.getId() + "", "registry", attach.getDomain().getName()};
		FileList fl = SearchFiles.searchFilesAppProperties(drive, keys, values);
		if(!fl.getFiles().isEmpty()) {
			if(!fl.getFiles().get(0).getId().equals(attach.getDriveId())) {
				attach.setDriveId(fl.getFiles().get(0).getId());
				AON.updateAttach(api.getDomain().getName(), api.getDomain().getId(), "", attach);
			}
			if("0".equals(attach.getDparentId())) {
				attach.setDparentId(fl.getFiles().get(0).getSize().toString());
				AON.updateAttach(api.getDomain().getName(), api.getDomain().getId(), "", attach);
			}
		}
		attach.setData(AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId()));
		return attach;
	}
	
	private Attach getTaskAttach(AonApiData api, Integer id) {
		
		TaskAttach ta = AON_SOLUTIONS.getTaskAttach(api.getDomain(), api.getUser(), 
				f -> f.getDomainProperty().eq(api.getDomain().getId())
				.and(f.getIdProperty().eq(id)));
		String fileName = "document";
		Attach attach = new Attach(AttachType.TASK)
				.setId(ta.getId())
				.setDomain(api.getDomain())
				.setMimeType(ta.getMimetype())
				.setData(ta.getData())
				.setDescription(fileName);
		
		if(ta.getMimetype()!=null && ta.getMimetype().equals(MimeType.ZIP)) {
			try {
				Map<String, byte[]> map = ZipUtils.uncompress(ta.getData());
				if(map.size() ==1) {
					Entry<String, byte[]> entry = map.entrySet().iterator().next();
					Optional<String> extension = getExtensionByStringHandling(entry.getKey());
					if(extension.isPresent()) {
						MimeType mimeType = MimeType.getByExtension(extension.get());
						attach.setData(entry.getValue());
						attach.setMimeType(mimeType);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return attach;
	}
	
	private JSONObject getJson(AonApiData api, JSONObject json) {
		String param = new String(Base64.getDecoder().decode(api.getPath().substring(1)));
		JSONObject data = new JSONObject(param);
		if(data.opt(DOMAIN_NAME) != null && data.opt(DOMAIN_ID) != null) {
			String domainName = data.optString(DOMAIN_NAME);
			Integer domainId = data.optInt(DOMAIN_ID);
			api.setDomain(AON.getDomain(domainName, domainId, ""));
		}
		return json.opt(ATTACH_TYPE) != null && json.opt(IJsonNames.ID) != null
			? json : new JSONObject(param);
	}
	
	public Optional<String> getExtensionByStringHandling(String filename) {
	    return Optional.ofNullable(filename)
	      .filter(f -> f.contains("."))
	      .map(f -> f.substring(filename.lastIndexOf(".") + 1));
	}
}
