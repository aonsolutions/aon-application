package net.aonsolutions.aon.api.servlet.documental;

import java.util.Base64;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.google.apis.drive.AonDrive;
import net.aonsolutions.aon.google.apis.drive.SearchFiles;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiFileServlet", urlPatterns = { "/ms/api/file/*" })
public class FileServlet extends AonApiHttpServlet{
	
	private static final Logger LOGGER  = Logger.getLogger(FileServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Aon Api File Servlet - GET METHOD");	
		try {
			super.doGet(req, resp);
			responseFile(req, resp, getFile(getJson(getParams())));
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Aon Api File Servlet - POST METHOD");
		try {
			super.doPost(req, resp);
			responseFile(req, resp, getFile(getJson(getData())));
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private Attach getFile(JSONObject json) {
		AttachType attachType = AttachType.getAttachType(json.getString("attach_type"));
		Integer id = json.getInt("id");
		
		Attach attach = AttachType.RAWDOC == attachType
			? AON.getRawdocAttach(getDomain().getName(), getDomain().getId(), getUser().getLogin(),
					f -> f.getDomainProperty().eq(getDomain().getId())
					.and(f.getIdProperty().eq(id)), attachType)
			: AON.getAttach(getDomain().getName(), getDomain().getId(), getUser().getLogin(), 
				f -> f.getDomainProperty().eq(getDomain().getId())
					.and(f.getIdProperty().eq(id)) , attachType);

		if(attach.getData() == null && attach.getDriveId() != null) {
			attach = getDriveFile(attach);
		}
		
		return attach;
	}
	
	private Attach getDriveFile(Attach attach) {
		DomainGserviceaccount g = AON.getDomainGserviceaccount(getDomain().getName(), getDomain().getId(), getUser().getLogin());
		Drive drive = AonDrive.getInstace().serviceInitialize(g);
		String[] keys = {"fileId", "aontype", "domain"};
		String[] values = {attach.getId() + "", "registry", attach.getDomain().getName()};
		FileList fl = SearchFiles.searchFilesAppProperties(drive, keys, values);
		if(fl.getFiles().size() > 0) {
			if(!fl.getFiles().get(0).getId().equals(attach.getDriveId())) {
				attach.setDriveId(fl.getFiles().get(0).getId());
				AON.updateAttach(getDomain().getName(), getDomain().getId(), "", attach);
			}
			if("0".equals(attach.getDparentId())) {
				attach.setDparentId(fl.getFiles().get(0).getSize().toString());
				AON.updateAttach(getDomain().getName(), getDomain().getId(), "", attach);
			}
		}
		attach.setData(AonDrive.getInstace().downloadFileByteArray(drive, attach.getDriveId()));
		return attach;
	}
	
	private JSONObject getJson(JSONObject json) {
		String param = new String(Base64.getDecoder().decode(getPath().substring(1)));
		JSONObject data = new JSONObject(param);
		if(data.opt("domain_name") != null && data.opt("domain_id") != null) {
			String domainName = data.optString("domain_name");
			Integer domainId = data.optInt("domain_id");
			setDomain(AON.getDomain(domainName, domainId, ""));
		}
		return json.opt("attach_type") != null && json.opt("id") != null
			? json : new JSONObject(param);
	}
	
	@Override
	public Domain getDomain() {
		return super.getDomain();
	}
	
	
}
