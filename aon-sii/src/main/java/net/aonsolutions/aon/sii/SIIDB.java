package net.aonsolutions.aon.sii;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;

public class SIIDB {
	
	public static SIIDB getInstance() {
		return new SIIDB();
	}
	
	public SIIDB() {
		
	}
	
    protected void insertSuministro(Domain domain, String login, LinkedList<Integer> invoiceList, byte[] requestXml, byte[] responseXml){

    	DataResponse dr = AON.insertDataResponse(domain.getName(), domain.getId(), login, 
    			new DataResponse()
    			.setDomain(domain.getId())
    			.setNumber("SII" + AonDateUtils.format(new Date(), "yyyyMMddHHmm"))
    			.setIssueDate(new Date())
    			.setSource(DataResponseSource.SII)
    			.setCreationDate(new Date())
    			.setCreationUser(login)
    			.setModificationDate(new Date())
    			.setModificationUser(login));
    
    	invoiceList.stream().forEach(invoice -> {
    		DataResponseDetail drd = new DataResponseDetail();
    		drd.setDomain(domain.getId())
    			.setDataResponse(dr.getId())
    			.setDataVariable("invoice_OK") // || invoice_KO
    			.setValue(invoice.toString())
    			.setCreationDate(new Date())
    			.setCreationUser(login)
    			.setModificationDate(new Date())
    			.setModificationUser(login);
    		AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd);
    	});
    	
    	Attach requestAttach = new Attach()
    			.setDomain(domain)
    			.setAttachType(AttachType.DATA)
    			.setSourceType(DataAttachSource.SII.value())
    			.setSourceBatch(dr.getId())
    			.setDescription(dr.getNumber())
    			.setData(requestXml)
    			.setType(DataAttachType.REQUEST.value())
    			.setMimeType(MimeType.XML)
    			.setCreationDate(new Date())	
    			.setCreationUser(login)
    			.setModificationDate(new Date())
    			.setModificationUser(login);
    	AON.insertAttach(domain.getName(), domain.getId(), login, requestAttach);
    	
    	Attach responseAttach = new Attach()
    			.setDomain(domain)
    			.setAttachType(AttachType.DATA)
    			.setSourceType(DataAttachSource.SII.value())
    			.setSourceBatch(dr.getId())
    			.setDescription(dr.getNumber())
    			.setData(responseXml)
    			.setType(DataAttachType.RESPONSE_OK.value()) // || DataAttachType.RESPONSE_ERROR
    			.setMimeType(MimeType.XML)
    			.setCreationDate(new Date())	
    			.setCreationUser(login)
    			.setModificationDate(new Date())
    			.setModificationUser(login);
    	AON.insertAttach(domain.getName(), domain.getId(), login, responseAttach);
    }
    
}