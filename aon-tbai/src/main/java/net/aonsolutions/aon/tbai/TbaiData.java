package net.aonsolutions.aon.tbai;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.DataRequestType;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;

import net.aonsolutions.aon.tbai.responses.TbaiResponse;

public class TbaiData {

	private TbaiData() {
	
	}
	
	public static DataRequest saveRequest(Domain domain, User user, Invoice invoice, byte[] data) {
		DataRequest request = new DataRequest()
				.setDomain(domain.getId())
				.setDate(new Date())
				.setBlackBox(InvoiceJSON.toJSON(invoice).toString())
				.setType(DataRequestType.TBAI);
		String md5 = getMd5(request.getDomain() + request.getDate().toString() + request.getBlackBox() + request.getType().value());
		request.setMd5(md5);
		
		request = AON.saveDataRequest(domain.getName(), domain.getId(), user.getLogin(), request);
		
		Attach attach = new Attach()
				.setDomain(domain)
				.setAttachType(AttachType.DATA)
				.setType(DataAttachType.REQUEST.value())
				.setSource(DataAttachSource.TBAI.value())
				.setSourceId(request.getId())
				.setMimeType(MimeType.XML)
				.setData(data);
		
		AON.insertAttach(domain.getName(), domain.getId(), user.getLogin(), attach);
		return request;
	}
	
	public static TbaiBlockchain getBlockchain(Domain domain, User user) {
		DataResponse dr = AON.getLastDataResponse(domain.getName(), domain.getId(), user.getLogin(), f -> 
			f.getDomainProperty().eq(domain.getId())
			.and(f.getSourceProperty().eq(DataResponseSource.TBAI.value()))
//			.and(f.getCodeProperty().eq("ok").or(f.getCodeProperty().eq("pending")))
			);
		DataResponseDetail drd = AON.getDataResponseDetail(domain.getName(), domain.getId(), user.getLogin(), f -> 
			f.getDomainProperty().eq(domain.getId())
			.and(f.getDataResponseProperty().eq(dr.getId()))
			.and(f.getDataVariableProperty().eq("blockchain"))).orElse(new DataResponseDetail());
		
		return TbaiBlockchain.fromJSON(drd.getDataValue());
	}

	public static String getTbaiId(String domainName, Integer domainId, String login, Integer invoiceId) {
		DataResponse dr = AON.getDataResponse(domainName, domainId, login, DataResponseSource.TBAI, f -> 
		f.getDomainProperty().eq(domainId)
		.and(f.getSourceProperty().eq(DataResponseSource.TBAI.value()))
//		.and(f.getCodeProperty().eq("ok").or(f.getCodeProperty().eq("pending")))
		.and(f.getSourceIdProperty().eq(invoiceId)));
		DataResponseDetail drd = AON.getDataResponseDetail(domainName, domainId, login, f -> 
			f.getDomainProperty().eq(domainId)
			.and(f.getDataResponseProperty().eq(dr.getId()))
			.and(f.getDataVariableProperty().eq("tbaiId"))).orElse(new DataResponseDetail());
	
		return drd.getDataValue();
	}
	
	public static String getTbaiId(Domain domain, User user, Integer invoiceId) {
		return getTbaiId(domain.getName(),  domain.getId(), user.getLogin(), invoiceId);
	}
	
	public static DataResponse saveResponse(Domain domain, User user, TbaiResponse response, DataResponse dr) {
		dr.setCode(response.getResponseStatus());
		AON.updateDataResponse(domain.getName(), domain.getId(), user.getLogin(), dr, f -> f.getIdProperty().eq(dr.getId()));
		
		DataResponseDetail drd = new DataResponseDetail()
				.setDomain(domain.getId())
				.setDataResponse(dr.getId())
				.setDataVariable("response")
				.setDataValue(response.toJSON().toString());
		
		AON.insertDataResponseDetail(domain.getName(), domain.getId(), user.getLogin(), drd);
		
		if(response.getData() != null) {
			Attach attach = new Attach()
					.setDomain(domain)
					.setAttachType(AttachType.DATA)
					.setType(response.isOk() 
						? DataAttachType.RESPONSE_OK.value() 
						: DataAttachType.RESPONSE_ERROR.value())
					.setSource(DataAttachSource.TBAI.value())
					.setSourceId(dr.getId())
					.setMimeType(MimeType.XML)
					.setData(response.getData());
			
			AON.insertAttach(domain.getName(), domain.getId(), user.getLogin(), attach);
		}
		return dr;
	}
	
	public static DataResponse saveResponsePending(Domain domain, User user, Invoice invoice, TbaiResponse response, TbaiBlockchain blockchain, DataRequest dataRequest) {
		DataResponse dr = new DataResponse()
				.setDomain(domain.getId())
				.setCode(response.getResponseStatus())
				.setResponseDate(new Date())
				.setSource(DataResponseSource.TBAI)
				.setSourceId(invoice.getId())
				.setDataRequest(dataRequest.getId());
		
		dr = AON.insertDataResponse(domain.getName(), domain.getId(), user.getLogin(), dr);
		
		DataResponseDetail drd1 = new DataResponseDetail()
				.setDomain(domain.getId())
				.setDataResponse(dr.getId())
				.setDataVariable("tbaiId")
				.setDataValue(response.getTbaiId());
		
		AON.insertDataResponseDetail(domain.getName(), domain.getId(), user.getLogin(), drd1);
		
		DataResponseDetail drd2 = new DataResponseDetail()
				.setDomain(domain.getId())
				.setDataResponse(dr.getId())
				.setDataVariable("blockchain")
				.setDataValue(blockchain.toJSON().toString());

		AON.insertDataResponseDetail(domain.getName(), domain.getId(), user.getLogin(), drd2);
		return dr;
	}
	
	public static String getMd5(String str){
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	    md.update(str.getBytes());
	    byte byteData[] = md.digest();
	    //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < byteData.length; i++) {
	     	sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	    }       
        return sb.toString();
	}
}
