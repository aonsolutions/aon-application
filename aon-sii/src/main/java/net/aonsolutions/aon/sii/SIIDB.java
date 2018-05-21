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

public class SIIDB {
	
	public static SIIDB getInstance() {
		return new SIIDB();
	}
	
	public SIIDB() {
		
	}
	
    public void insertSuministro(Domain domain, String login, LinkedList<Integer> invoiceList, byte[] requestXml, byte[] responseXml, LinkedList<String> status, LinkedList<VatContext> vatList, SendType sendType){
    	
    	DataResponse dr = AON.insertDataResponse(domain.getName(), domain.getId(), login, 
    			new DataResponse()
    			.setDomain(domain.getId())
    			.setCode(sendType.getDescription())
    			.setResponseDate(new Date())
    			.setSource(DataResponseSource.SII));
    
    	DataResponseDetail drd0= new DataResponseDetail();
		drd0.setDomain(domain.getId())
			.setDataResponse(dr.getId())
			.setDataVariable("type") 
			.setDataValue(Integer.toString(sendType.ordinal()));
		AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd0);

		DataResponseDetail drd01= new DataResponseDetail();
		drd01.setDomain(domain.getId())
			.setDataResponse(dr.getId())
			.setDataVariable("status") 
			.setDataValue("enviado"); // "enviado" || "descargado"
		AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd01);
    	
    	for(Integer i = 0 ; i < invoiceList.size() ; i++){
    		Integer invoice = invoiceList.get(i);
    		DataResponse di = AON.getDataResponse(domain.getName(), domain.getId(), login, DataResponseSource.SII_INVOICE,
    				f -> f.getSourceProperty().eq(DataResponseSource.SII_INVOICE.value())
    				.and(f.getSourceIdProperty().eq(invoice)));
    		if(di == null){
    			di =  AON.insertDataResponse(domain.getName(), domain.getId(), login, 
    					new DataResponse()
    					.setDomain(domain.getId())
    					.setCode("")
    					.setResponseDate(new Date())
    					.setSource(DataResponseSource.SII_INVOICE)
    					.setSourceId(invoiceList.get(i)));
    		}
    		Integer diID = di.getId();
    		if(sendType.isIntracomunitaria()) {
    			Optional<DataResponseDetail> opt = AON.getDataResponseDetail(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().eq(diID).and(f.getDataVariableProperty().eq("status_intra")));
        		if(opt.isPresent()){
            		DataResponseDetail drdOpt = opt.get().setDataVariable("status_intra_old");
        			AON.updateDataResponseDetail(domain.getName(), domain.getId(), login, drdOpt, f -> f.getIdProperty().eq(drdOpt.getId()));
        		}
        		DataResponseDetail drd = new DataResponseDetail();
        		drd.setDomain(domain.getId())
        			.setDataResponse(di.getId())
        			.setDataVariable("status_intra") 
        			.setDataValue(status.get(i));
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
    				.setDataValue(dr.getId().toString());
    			AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd2);
    		} else if(sendType.isInversion()){
    			Optional<DataResponseDetail> opt = AON.getDataResponseDetail(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().eq(diID).and(f.getDataVariableProperty().eq("status_bienes")));
        		if(opt.isPresent()){
            		DataResponseDetail drdOpt = opt.get().setDataVariable("status_bienes_old");
        			AON.updateDataResponseDetail(domain.getName(), domain.getId(), login, drdOpt, f -> f.getIdProperty().eq(drdOpt.getId()));
        		}
        		DataResponseDetail drd = new DataResponseDetail();
        		drd.setDomain(domain.getId())
        			.setDataResponse(di.getId())
        			.setDataVariable("status_bienes") 
        			.setDataValue(status.get(i));
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
    				.setDataValue(dr.getId().toString());
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
    				.setDataValue(status.get(i));
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
    				.setDataValue(dr.getId().toString());
    			AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd2);
    		
    			if(sendType.isAlta() && (status.get(i).equals("Correcto") || status.get(i).equals("AceptadoConErrores"))
    				&& vatList.stream().filter(d -> d.getInvoice().equals(invoice)).map(f -> f.isIntracommunity()).findFirst().orElse(false)){
    				DataResponseDetail drd3 = new DataResponseDetail();
    				drd3.setDomain(domain.getId())
    					.setDataResponse(di.getId())
    					.setDataVariable("status_intra") 
    					.setDataValue("Pendiente");
    				AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd3);
    			}
    			
    			if(sendType.isAlta() && (status.get(i).equals("Correcto") || status.get(i).equals("AceptadoConErrores"))
        			&& vatList.stream().filter(d -> d.getInvoice().equals(invoice)).map(f -> f.isVatAccrualRegime()).findFirst().orElse(false)){
        			DataResponseDetail drd3 = new DataResponseDetail();
        			drd3.setDomain(domain.getId())
        				.setDataResponse(di.getId())
        				.setDataVariable("status_cp") 
        				.setDataValue("Pendiente");
        			AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd3);
        		}
    			
    			if(sendType.isAlta() && (status.get(i).equals("Correcto") || status.get(i).equals("AceptadoConErrores"))
            			&& vatList.stream().filter(d -> d.getInvoice().equals(invoice)).map(f -> f.isInvestment()).findFirst().orElse(false)){
            			DataResponseDetail drd4 = new DataResponseDetail();
            			drd4.setDomain(domain.getId())
            				.setDataResponse(di.getId())
            				.setDataVariable("status_bienes") 
            				.setDataValue("Pendiente");
            			AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd4);
            		}
    		}	
    	};
    	
    	Attach requestAttach = new Attach()
    			.setDomain(domain)
    			.setAttachType(AttachType.DATA)
    			.setSourceType(DataAttachSource.SII.value())
    			.setSourceBatch(dr.getId())
    			.setDescription(dr.getCode())
    			.setData(requestXml)
    			.setType(DataAttachType.REQUEST.value())
    			.setMimeType(MimeType.XML);
    	AON.insertAttach(domain.getName(), domain.getId(), login, requestAttach);
    	
    	Attach responseAttach = new Attach()
    			.setDomain(domain)
    			.setAttachType(AttachType.DATA)
    			.setSourceType(DataAttachSource.SII.value())
    			.setSourceBatch(dr.getId())
    			.setDescription(dr.getCode())
    			.setData(responseXml)
    			.setType(DataAttachType.RESPONSE_OK.value()) // || DataAttachType.RESPONSE_ERROR
    			.setMimeType(MimeType.XML);
    	AON.insertAttach(domain.getName(), domain.getId(), login, responseAttach);
    }
    
    public void insertSuministroBajas(Domain domain, String login, LinkedList<Integer> invoiceList, byte[] requestXml, byte[] responseXml, HashMap<Integer, String> status, SendType sendType){
    	DataResponse dr = AON.insertDataResponse(domain.getName(), domain.getId(), login, 
    			new DataResponse()
    			.setDomain(domain.getId())
    			.setCode(sendType.getDescription())
    			.setResponseDate(new Date())
    			.setSource(DataResponseSource.SII));
    	
    	DataResponseDetail drd0= new DataResponseDetail();
		drd0.setDomain(domain.getId())
			.setDataResponse(dr.getId())
			.setDataVariable("type") 
			.setDataValue(Integer.toString(sendType.ordinal()));
		AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd0);
		
    	for(Integer i = 0 ; i < invoiceList.size() ; i++){
    		Integer invoice = invoiceList.get(i);
    		DataResponse di = AON.getDataResponse(domain.getName(), domain.getId(), login, DataResponseSource.SII_INVOICE,
    				f -> f.getSourceProperty().eq(DataResponseSource.SII_INVOICE.value())
    				.and(f.getSourceIdProperty().eq(invoice)));
    		if(di != null && (status.get(invoice).equals("Correcto") || status.get(invoice).equals("Correcto"))){
    			String estado = "status";
    			if(sendType.isIntracomunitaria()) estado = "status_intra";
    			if(sendType.isInversion()) estado = "status_bienes";
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
    				.setDataValue("Anulada");
    			AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd);
    	
    			String send = "send";
    			if(sendType.isIntracomunitaria()) send = "send_intra";
    			if(sendType.isInversion()) send = "send_bienes";
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
    				.setDataValue(dr.getId().toString());
    			AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd2);
    		}
    	}
    }
    
    public void insertSuministroCobrosPagos(Domain domain, String login, LinkedList<Integer> invoiceList, byte[] requestXml, byte[] responseXml, LinkedList<Finance> financeList, HashMap<Integer, String> status){
    	DataResponse dr = AON.insertDataResponse(domain.getName(), domain.getId(), login, 
    			new DataResponse()
    			.setDomain(domain.getId())
    			.setCode(SendType.COBROS_PAGOS.getDescription())
    			.setResponseDate(new Date())
    			.setSource(DataResponseSource.SII));

    	DataResponseDetail drd0= new DataResponseDetail();
		drd0.setDomain(domain.getId())
			.setDataResponse(dr.getId())
			.setDataVariable("type") 
			.setDataValue(Integer.toString(SendType.COBROS_PAGOS.ordinal()));
		AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd0);
    	
		for(Finance f : financeList){
    		if(!status.get(f.getInvoice().getId()).equals("Incorrecto")){
    			DataResponse di = AON.insertDataResponse(domain.getName(), domain.getId(), login, 
					new DataResponse()
					.setDomain(domain.getId())
					.setCode("")
					.setResponseDate(new Date())
					.setSource(DataResponseSource.SII_FINANCE)
					.setSourceId(f.getId()));
    		
    			DataResponseDetail drd2 = new DataResponseDetail();
    			drd2.setDomain(domain.getId())
    				.setDataResponse(di.getId())
    				.setDataVariable("send") 
    				.setDataValue(dr.getId().toString());
    			AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd2);
    		}
    	}
    	
    	for(Integer i : invoiceList){
    		DataResponse di = AON.getDataResponse(domain.getName(), domain.getId(), login, DataResponseSource.SII_INVOICE,
    				f -> f.getSourceProperty().eq(DataResponseSource.SII_INVOICE.value())
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
    			.setDataValue(incorrect ? "Incorrecto" : (invoice.getTotal() > paid ? "Parcial" : "Pagado"));
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
				.setDataValue(dr.getId().toString());
			AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd2);
    	}
    	
    	Attach requestAttach = new Attach()
    			.setDomain(domain)
    			.setAttachType(AttachType.DATA)
    			.setSourceType(DataAttachSource.SII.value())
    			.setSourceBatch(dr.getId())
    			.setDescription(dr.getCode())
    			.setData(requestXml)
    			.setType(DataAttachType.REQUEST.value())
    			.setMimeType(MimeType.XML);
    	AON.insertAttach(domain.getName(), domain.getId(), login, requestAttach);
    	
    	Attach responseAttach = new Attach()
    			.setDomain(domain)
    			.setAttachType(AttachType.DATA)
    			.setSourceType(DataAttachSource.SII.value())
    			.setSourceBatch(dr.getId())
    			.setDescription(dr.getCode())
    			.setData(responseXml)
    			.setType(DataAttachType.RESPONSE_OK.value()) // || DataAttachType.RESPONSE_ERROR
    			.setMimeType(MimeType.XML);
    	AON.insertAttach(domain.getName(), domain.getId(), login, responseAttach);
    }
    
}