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
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;

public class SIIDB {
	
	public static SIIDB getInstance() {
		return new SIIDB();
	}
	
	public SIIDB() {
		
	}
	
    protected void insertSuministro(Domain domain, String login, LinkedList<Integer> invoiceList, byte[] requestXml, byte[] responseXml, LinkedList<String> status, LinkedList<VatContext> vatList, Boolean intracomunitaria, Boolean alta, Boolean bienes){
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
    		Integer invoice = invoiceList.get(i);
    		DataResponse di = AON.getDataResponse(domain.getName(), domain.getId(), login,
    				f -> f.getSource2Property().eq(DataResponseSource.SII_INVOICE.value())
    				.and(f.getSourceIdProperty().eq(invoice)));
    		if(di == null){
    			di =  AON.insertDataResponse(domain.getName(), domain.getId(), login, 
    					new DataResponse()
    					.setDomain(domain.getId())
    					.setNumber("SII" + AonDateUtils.format(new Date(), "yyyyMMddHHmm"))
    					.setIssueDate(new Date())
    					.setSource(DataResponseSource.SII_INVOICE)
    					.setSourceId(invoiceList.get(i))
    					.setCreationDate(new Date())
    					.setCreationUser(login)
    					.setModificationDate(new Date())
    					.setModificationUser(login));
    		}
    		Integer diID = di.getId();
    		if(intracomunitaria) {
    			Optional<DataResponseDetail> opt = AON.getDataResponseDetail(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().eq(diID).and(f.getDataVariableProperty().eq("status_intra")));
        		if(opt.isPresent()){
            		DataResponseDetail drdOpt = opt.get().setDataVariable("status_intra_old");
        			AON.updateDataResponseDetail(domain.getName(), domain.getId(), login, drdOpt, f -> f.getIdProperty().eq(drdOpt.getId()));
        		}
        		DataResponseDetail drd = new DataResponseDetail();
        		drd.setDomain(domain.getId())
        			.setDataResponse(di.getId())
        			.setDataVariable("status_intra") 
        			.setValue(status.get(i))
        			.setCreationDate(new Date())
        			.setCreationUser(login)
        			.setModificationDate(new Date())
        			.setModificationUser(login);
        		AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd);
        		
        		Optional<DataResponseDetail> opt2 = AON.getDataResponseDetail(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().eq(diID).and(f.getDataVariableProperty().eq("send_intra")));
    			if(opt2.isPresent()){
    				DataResponseDetail drdOpt = opt2.get().setDataVariable("send_intra_old");
    				AON.updateDataResponseDetail(domain.getName(), domain.getId(), login, drdOpt, f -> f.getIdProperty().eq(drdOpt.getId()));
    			}
    			DataResponseDetail drd2 = new DataResponseDetail();
    			drd2.setDomain(domain.getId())
    				.setDataResponse(di.getId())
    				.setDataVariable("send_intra") 
    				.setValue(dr.getId().toString())
    				.setCreationDate(new Date())
    				.setCreationUser(login)
    				.setModificationDate(new Date())
    				.setModificationUser(login);
    			AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd2);
    		} else if (bienes){
    			Optional<DataResponseDetail> opt = AON.getDataResponseDetail(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().eq(diID).and(f.getDataVariableProperty().eq("status_bienes")));
        		if(opt.isPresent()){
            		DataResponseDetail drdOpt = opt.get().setDataVariable("status_bienes_old");
        			AON.updateDataResponseDetail(domain.getName(), domain.getId(), login, drdOpt, f -> f.getIdProperty().eq(drdOpt.getId()));
        		}
        		DataResponseDetail drd = new DataResponseDetail();
        		drd.setDomain(domain.getId())
        			.setDataResponse(di.getId())
        			.setDataVariable("status_bienes") 
        			.setValue(status.get(i))
        			.setCreationDate(new Date())
        			.setCreationUser(login)
        			.setModificationDate(new Date())
        			.setModificationUser(login);
        		AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd);
        		
        		Optional<DataResponseDetail> opt2 = AON.getDataResponseDetail(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().eq(diID).and(f.getDataVariableProperty().eq("send_bienes")));
    			if(opt2.isPresent()){
    				DataResponseDetail drdOpt = opt2.get().setDataVariable("send_bienes_old");
    				AON.updateDataResponseDetail(domain.getName(), domain.getId(), login, drdOpt, f -> f.getIdProperty().eq(drdOpt.getId()));
    			}
    			DataResponseDetail drd2 = new DataResponseDetail();
    			drd2.setDomain(domain.getId())
    				.setDataResponse(di.getId())
    				.setDataVariable("send_bienes") 
    				.setValue(dr.getId().toString())
    				.setCreationDate(new Date())
    				.setCreationUser(login)
    				.setModificationDate(new Date())
    				.setModificationUser(login);
    			AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd2);

    		} else {
    			Optional<DataResponseDetail> opt = AON.getDataResponseDetail(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().eq(diID).and(f.getDataVariableProperty().eq("status")));
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
    	
    			Optional<DataResponseDetail> opt2 = AON.getDataResponseDetail(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().eq(diID).and(f.getDataVariableProperty().eq("send")));
    			if(opt2.isPresent()){
    				DataResponseDetail drdOpt = opt2.get().setDataVariable("send_old");
    				AON.updateDataResponseDetail(domain.getName(), domain.getId(), login, drdOpt, f -> f.getIdProperty().eq(drdOpt.getId()));
    			}
    			DataResponseDetail drd2 = new DataResponseDetail();
    			drd2.setDomain(domain.getId())
    				.setDataResponse(di.getId())
    				.setDataVariable("send") 
    				.setValue(dr.getId().toString())
    				.setCreationDate(new Date())
    				.setCreationUser(login)
    				.setModificationDate(new Date())
    				.setModificationUser(login);
    			AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd2);
    		
    			if(alta && (status.get(i).equals("Correcto") || status.get(i).equals("AceptadoConErrores"))
    				&& vatList.stream().filter(d -> d.getInvoice().equals(invoice)).map(f -> f.isIntracommunity()).findFirst().orElse(false)){
    				DataResponseDetail drd3 = new DataResponseDetail();
    				drd3.setDomain(domain.getId())
    					.setDataResponse(di.getId())
    					.setDataVariable("status_intra") 
    					.setValue("Pendiente")
    					.setCreationDate(new Date())
    					.setCreationUser(login)
    					.setModificationDate(new Date())
    					.setModificationUser(login);
    				AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd3);
    			}
    			
    			if(alta && (status.get(i).equals("Correcto") || status.get(i).equals("AceptadoConErrores"))
        			&& vatList.stream().filter(d -> d.getInvoice().equals(invoice)).map(f -> f.isVatAccrualRegime()).findFirst().orElse(false)){
        			DataResponseDetail drd3 = new DataResponseDetail();
        			drd3.setDomain(domain.getId())
        				.setDataResponse(di.getId())
        				.setDataVariable("status_cp") 
        				.setValue("Pendiente")
        				.setCreationDate(new Date())
        				.setCreationUser(login)
        				.setModificationDate(new Date())
        				.setModificationUser(login);
        			AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd3);
        		}
    			
    			if(alta && (status.get(i).equals("Correcto") || status.get(i).equals("AceptadoConErrores"))
            			&& vatList.stream().filter(d -> d.getInvoice().equals(invoice)).map(f -> f.isInvestment()).findFirst().orElse(false)){
            			DataResponseDetail drd4 = new DataResponseDetail();
            			drd4.setDomain(domain.getId())
            				.setDataResponse(di.getId())
            				.setDataVariable("status_bienes") 
            				.setValue("Pendiente")
            				.setCreationDate(new Date())
            				.setCreationUser(login)
            				.setModificationDate(new Date())
            				.setModificationUser(login);
            			AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd4);
            		}
    		}	
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
    
    protected void insertSuministroBajas(Domain domain, String login, LinkedList<Integer> invoiceList, byte[] requestXml, byte[] responseXml, HashMap<Integer, String> status, Boolean intra, Boolean bienes){
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
    		Integer invoice = invoiceList.get(i);
    		DataResponse di = AON.getDataResponse(domain.getName(), domain.getId(), login,
    				f -> f.getSource2Property().eq(DataResponseSource.SII_INVOICE.value())
    				.and(f.getSourceIdProperty().eq(invoice)));
    		if(di != null && (status.get(invoice).equals("Correcto") || status.get(invoice).equals("Correcto"))){
    			String estado = "status";
    			if(intra) estado = "status_intra";
    			if(bienes) estado = "status_bienes";
    			String s = estado;
    			Optional<DataResponseDetail> opt = AON.getDataResponseDetail(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().eq(di.getId()).and(f.getDataVariableProperty().eq(s)));
    			if(opt.isPresent()){
    				DataResponseDetail drdOpt = opt.get().setDataVariable(estado + "_old");
    				AON.updateDataResponseDetail(domain.getName(), domain.getId(), login, drdOpt, f -> f.getIdProperty().eq(drdOpt.getId()));
    			}
    			DataResponseDetail drd = new DataResponseDetail();
    			drd.setDomain(domain.getId())
    				.setDataResponse(di.getId())
    				.setDataVariable(estado) 
    				.setValue("Anulada")
    				.setCreationDate(new Date())
    				.setCreationUser(login)
    				.setModificationDate(new Date())
    				.setModificationUser(login);
    			AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd);
    	
    			String send = "send";
    			if(intra) send = "send_intra";
    			if(bienes) send = "send_bienes";
    			String s2 = send;
    			Optional<DataResponseDetail> opt2 = AON.getDataResponseDetail(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().eq(di.getId()).and(f.getDataVariableProperty().eq(s2)));
    			if(opt2.isPresent()){
    				DataResponseDetail drdOpt = opt2.get().setDataVariable(send + "_old");
    				AON.updateDataResponseDetail(domain.getName(), domain.getId(), login, drdOpt, f -> f.getIdProperty().eq(drdOpt.getId()));
    			}
    			DataResponseDetail drd2 = new DataResponseDetail();
    			drd2.setDomain(domain.getId())
    				.setDataResponse(di.getId())
    				.setDataVariable(send) 
    				.setValue(dr.getId().toString())
    				.setCreationDate(new Date())
    				.setCreationUser(login)
    				.setModificationDate(new Date())
    				.setModificationUser(login);
    			AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd2);
    		}
    	}
    }
    
    protected void insertSuministroCobrosPagos(Domain domain, String login, LinkedList<Integer> invoiceList, byte[] requestXml, byte[] responseXml, LinkedList<Finance> financeList, HashMap<Integer, String> status){
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

    	for(Finance f : financeList){
    		if(!status.get(f.getInvoice().getId()).equals("Incorrecto")){
    			DataResponse di = AON.insertDataResponse(domain.getName(), domain.getId(), login, 
					new DataResponse()
					.setDomain(domain.getId())
					.setNumber("SII" + AonDateUtils.format(new Date(), "yyyyMMddHHmm"))
					.setIssueDate(new Date())
					.setSource(DataResponseSource.SII_FINANCE)
					.setSourceId(f.getId())
					.setCreationDate(new Date())
					.setCreationUser(login)
					.setModificationDate(new Date())
					.setModificationUser(login));
    		
    			DataResponseDetail drd2 = new DataResponseDetail();
    			drd2.setDomain(domain.getId())
    				.setDataResponse(di.getId())
    				.setDataVariable("send") 
    				.setValue(dr.getId().toString())
    				.setCreationDate(new Date())
    				.setCreationUser(login)
    				.setModificationDate(new Date())
    				.setModificationUser(login);
    			AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd2);
    		}
    	}
    	
    	for(Integer i : invoiceList){
    		DataResponse di = AON.getDataResponse(domain.getName(), domain.getId(), login,
    				f -> f.getSource2Property().eq(DataResponseSource.SII_INVOICE.value())
    				.and(f.getSourceIdProperty().eq(i)));
    		
    		Optional<DataResponseDetail> opt = AON.getDataResponseDetail(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().eq(di.getId()).and(f.getDataVariableProperty().eq("status_cp")));
    		if(opt.isPresent()){
        		DataResponseDetail drdOpt = opt.get().setDataVariable("status_cp_old");
    			AON.updateDataResponseDetail(domain.getName(), domain.getId(), login, drdOpt, f -> f.getIdProperty().eq(drdOpt.getId()));
    		}
    		Boolean incorrect = status.get(i).equals("Incorrecto");
    		Invoice invoice = AON.getInvoice(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(i));
    		Double paid = financeList.stream().filter(f -> f.getInvoice().getId().equals(i)).mapToDouble(s-> s.getAmount()).sum();
    		DataResponseDetail drd = new DataResponseDetail();
    		drd.setDomain(domain.getId())
    			.setDataResponse(di.getId())
    			.setDataVariable("status_cp") 
    			.setValue(incorrect ? "Incorrecto" : (invoice.getTotal() > paid ? "Parcial" : "Pagado"))
    			.setCreationDate(new Date())
    			.setCreationUser(login)
    			.setModificationDate(new Date())
    			.setModificationUser(login);
    		AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd);
    		
    		Optional<DataResponseDetail> opt2 = AON.getDataResponseDetail(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().eq(di.getId()).and(f.getDataVariableProperty().eq("send_cp")));
			if(opt2.isPresent()){
				DataResponseDetail drdOpt = opt2.get().setDataVariable("send_cp_old");
				AON.updateDataResponseDetail(domain.getName(), domain.getId(), login, drdOpt, f -> f.getIdProperty().eq(drdOpt.getId()));
			}
    		DataResponseDetail drd2 = new DataResponseDetail();
			drd2.setDomain(domain.getId())
				.setDataResponse(di.getId())
				.setDataVariable("send_cp") 
				.setValue(dr.getId().toString())
				.setCreationDate(new Date())
				.setCreationUser(login)
				.setModificationDate(new Date())
				.setModificationUser(login);
			AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd2);
    	}
    	
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