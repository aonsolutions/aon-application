package net.aonsolutions.aon.gwt.document.server;

import javax.servlet.annotation.WebServlet;

import com.code.aon.google.apis.DriveUtils;
import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import net.aonsolutions.aon.google.apis.drive.AonDrive;
import net.aonsolutions.aon.google.apis.drive.SearchFiles;
import net.aonsolutions.aon.gwt.document.client.IDocumental;

@WebServlet(name = "DocumentalGwtServlet", urlPatterns = { "/aon_gwt_aio/gwt_documental",
														   "/aon_gwt_document/gwt_documental"})
public class DocumentalServlet  extends AonRemoteServiceServlet implements IDocumental {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String getAttachLink(Domain domain, Integer id){
		Attach attach = AON.getAttach(domain.getName(), domain.getId(), "", f -> f.getIdProperty().eq(id), AttachType.REGISTRY, false);

		if(attach.getDriveId() != null){
			DomainGserviceaccount g = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), "");
			Drive drive = AonDrive.getInstace().serviceInitialize(g);
			String[] keys = {"fileId", "aontype", "domain"};
			String[] values = {attach.getId() + "", "registry", attach.getDomain().getName()};
			FileList fl = SearchFiles.searchFilesAppProperties(drive, keys, values);
			if(fl.getFiles().size() > 0) {
				if(!fl.getFiles().get(0).getId().equals(attach.getDriveId())) {
					attach.setDriveId(fl.getFiles().get(0).getId());
					AON.updateAttach(domain.getName(), domain.getId(), "", attach);
				}
				if("0".equals(attach.getDparentId())) {
					attach.setDparentId(fl.getFiles().get(0).getSize().toString());
					AON.updateAttach(domain.getName(), domain.getId(), "", attach);
				}
				return fl.getFiles().get(0).getWebViewLink();
			} else {
				File file = DriveUtils.getFile(drive, attach.getDriveId(), "*");
				return file.getWebViewLink();
			}
		}
		return null;
	}
}
