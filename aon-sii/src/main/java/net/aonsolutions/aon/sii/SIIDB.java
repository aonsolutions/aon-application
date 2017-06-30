package net.aonsolutions.aon.sii;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;

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
	
    protected void insertSuministro(Domain domain, String login, LinkedList<Integer> invoiceList, byte[] requestXml, byte[] responseXml, LinkedList<String> status){

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
    
    	for(Integer i = 0 ; i < invoiceList.size() ; i++){
    		DataResponse di =  AON.insertDataResponse(domain.getName(), domain.getId(), login, 
    			new DataResponse()
        			.setDomain(domain.getId())
        			.setNumber("SII" + AonDateUtils.format(new Date(), "yyyyMMddHHmm"))
        			.setIssueDate(new Date())
        			.setSource(DataResponseSource.SII)
        			.setSourceId(invoiceList.get(i))
        			.setCreationDate(new Date())
        			.setCreationUser(login)
        			.setModificationDate(new Date())
        			.setModificationUser(login));
    		
    		Optional<DataResponseDetail> opt = AON.getDataResponseDetail(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().eq(di.getId()).and(f.getDataVariableProperty().eq("status")));
    		if(opt.isPresent()){
        		DataResponseDetail drdOpt = opt.get().setDataVariable("status_old");
    			AON.updateDataResponseDetail(domain.getName(), domain.getId(), login, drdOpt, f -> f.getIdProperty().eq(drdOpt.getId()));
    		}
    		DataResponseDetail drd = new DataResponseDetail();
    		drd.setDomain(domain.getId())
    			.setDataResponse(di.getId())
    			.setDataVariable("status") 
    			.setValue(status.get(i))
    			.setCreationDate(new Date())
    			.setCreationUser(login)
    			.setModificationDate(new Date())
    			.setModificationUser(login);
    		AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd);
    		
    		Optional<DataResponseDetail> opt2 = AON.getDataResponseDetail(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().eq(di.getId()).and(f.getDataVariableProperty().eq("send")));
    		if(opt.isPresent()){
        		DataResponseDetail drdOpt = opt2.get().setDataVariable("send_old");
    			AON.updateDataResponseDetail(domain.getName(), domain.getId(), login, drdOpt, f -> f.getIdProperty().eq(drdOpt.getId()));
    		}
    		DataResponseDetail drd2 = new DataResponseDetail();
    		drd.setDomain(domain.getId())
    			.setDataResponse(di.getId())
    			.setDataVariable("send") 
    			.setValue(dr.getId().toString())
    			.setCreationDate(new Date())
    			.setCreationUser(login)
    			.setModificationDate(new Date())
    			.setModificationUser(login);
    		AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd2);
    	};
    	
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
    
    protected void insertSuministro(Domain domain, String login, HashMap<Integer, SIIDataVariable> invoiceMap, byte[] requestXml, byte[] responseXml ){

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
    
    	invoiceMap.keySet().stream().forEach(invoice -> {
    		DataResponseDetail drd = new DataResponseDetail();
    		drd.setDomain(domain.getId())
    			.setDataResponse(dr.getId())
    			.setDataVariable(invoiceMap.get(invoice).getVariable()) 
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