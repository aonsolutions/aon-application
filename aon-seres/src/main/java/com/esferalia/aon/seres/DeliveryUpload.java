package com.esferalia.aon.seres;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.code.aon.common.AonException;
import com.code.aon.file.format.output.FileOutput;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.seres.SeresInfo;
import com.esferalia.aon.occam.api.model.seres.SeresPath;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryCommunicationStatus;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryCommunicationType;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryInfo;
import com.esferalia.aon.seres.writer.connect2.ConnectDeliveryWriterOccam;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;


public class DeliveryUpload extends Seres {

	private static final long serialVersionUID = 1L;

	public DeliveryUpload(Domain domain, String login, SeresInfo info) {
		super(domain, login, info);
		if(getInfo().getSeresPath() == null)
			info.setSeresPath(SeresPath.ENVIO_DESADV_D96A);
	}
	
	public void uploadDeliveries(List<Delivery> list) {
		list.stream().forEach(this::uploadDelivery);
	}
	
	public void uploadDelivery(Delivery delivery) {
		try {
			String filename = delivery.getSeries()+"_"+delivery.getNumber() + ".edi";
			FileOutput output = getEdiFile(delivery);
			InputStream is = new BufferedInputStream(new ByteArrayInputStream(output.getContent()));
			storeFile(filename, is);
			saveDeliveryInfo(delivery, DeliveryCommunicationStatus.ACCEPTED);
		} catch (AonException | JSchException | SftpException e) {
			e.printStackTrace();
			throw new AonCoreException(e.getMessage(), e);
		}
	}
	
	public FileOutput getEdiFile(Delivery delivery) throws AonException {
		if(AonStringUtils.isBlank(delivery.getPackagingData())){
			String msg = "Secuencia de embalajes NO definida";
			throw new AonException(msg);
		}
		try {
			ConnectDeliveryWriterOccam writer = new ConnectDeliveryWriterOccam(getDomain(), getLogin());
			return writer.createFile(delivery);
		} catch (IOException e) {
			throw new AonException(e.getMessage(), e);
		}
	}
	
	private void saveDeliveryInfo(Delivery delivery, DeliveryCommunicationStatus status) {
		DeliveryInfo di = new DeliveryInfo()
				.setDelivery(delivery.getId())
				.setDomain(delivery.getDomain())
				.setType(DeliveryCommunicationType.SERES)
				.setStatus(status);
		AON.saveDeliveryInfo(getDomain(), new User().setLogin(getLogin()), di);
	}

}
