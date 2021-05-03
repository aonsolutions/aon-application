package net.aonsolutions.aon.api.request;

import java.util.Date;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

public class SaveImportation extends Thread {
	
	Domain domain;
	User user;
	byte[] data;
	
	public SaveImportation(Domain domain, User user, byte[] data) {
		this.domain = domain;
		this.user = user;
		this.data = data;
	}

	@Override
	public void run() {
		try {
			DataResponse dr = new DataResponse()
				.setDomain(domain.getId())
				.setCode("SELFCONTA")
				.setResponseDate(new Date())
				.setSource(DataResponseSource.IMPORTATION);
			dr = AON.insertDataResponse(getDomain().getName(), domain.getId(), user.getLogin(), dr);
			Attach attach = new Attach(AttachType.DATA)
				.setDomain(domain)
				.setSource(DataAttachSource.IMPORTATION.value()) 
				.setSourceId(dr.getId())
				.setType(DataAttachType.REQUEST.value())
				.setDescription("Importacion Selfconta")
				.setMimeType(MimeType.JSON)
				.setData(data);
			Integer attachId = AON.insertAttach(domain.getName(), domain.getId(), user.getLogin(), attach);
			attach.setId(attachId);
			DomainGserviceaccount d = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), user.getLogin());
			Drive drive = AonDrive.getInstace().serviceInitialize(d);
			AonDrive.getInstace().sync(drive, user, attach, false);
		} finally {
			this.stop();
		}
	}	
	
	public Domain getDomain() {
		return domain;
	}
	
	public SaveImportation setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	
	public User getUser() {
		return user;
	}
	
	public SaveImportation setUser(User user) {
		this.user = user;
		return this;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public SaveImportation setData(byte[] data) {
		this.data = data;
		return this;
	}
	
}