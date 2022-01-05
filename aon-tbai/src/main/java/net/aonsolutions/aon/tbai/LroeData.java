package net.aonsolutions.aon.tbai;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.json.JSONObject;

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

import net.aonsolutions.aon.tbai.lroe.LROEInfo;
import net.aonsolutions.aon.tbai.responses.LROEResponse;

public class LroeData {

	private LroeData() {
	
	}
	
	public static DataRequest saveRequest(Domain domain, User user, Invoice invoice, LROEInfo info, byte[] data) {
		JSONObject json = new JSONObject();
		json.put("lroe", info.toJSON());
		json.put("invoice", InvoiceJSON.toJSON(invoice));
		DataRequest request = new DataRequest()
			.setDomain(domain.getId())
			.setDate(new Date())
			.setBlackBox(json.toString())
			.setType(DataRequestType.LROE);
		String md5 = getMd5(request.getDomain() + request.getDate().toString() + request.getBlackBox() + request.getType().value());
		request.setMd5(md5);
		
		request = AON.saveDataRequest(domain.getName(), domain.getId(), user.getLogin(), request);
		
		Attach attach = new Attach()
				.setDomain(domain)
				.setAttachType(AttachType.DATA)
				.setType(DataAttachType.REQUEST.value())
				.setSource(DataAttachSource.LROE.value())
				.setSourceId(request.getId())
				.setMimeType(MimeType.XML)
				.setData(data);
		
		AON.insertAttach(domain.getName(), domain.getId(), user.getLogin(), attach);
		return request;
	}
		
	public static DataResponse saveResponse(Domain domain, User user, Invoice invoice, LROEResponse response, LROEInfo info, DataRequest dataRequest) {
		DataResponse dr = new DataResponse()
				.setDomain(domain.getId())
				.setCode(response.getResponseStatus())
				.setResponseDate(new Date())
				.setSource(DataResponseSource.LROE)
				.setSourceId(invoice.getId())
				.setDataRequest(dataRequest.getId());
		
		dr = AON.insertDataResponse(domain.getName(), domain.getId(), user.getLogin(), dr);
		
		DataResponseDetail drd1 = new DataResponseDetail()
				.setDomain(domain.getId())
				.setDataResponse(dr.getId())
				.setDataVariable("json")
				.setDataValue(response.getJson().toString());
		
		AON.insertDataResponseDetail(domain.getName(), domain.getId(), user.getLogin(), drd1);
	
		DataResponseDetail drd2 = new DataResponseDetail()
				.setDomain(domain.getId())
				.setDataResponse(dr.getId())
				.setDataVariable("info")
				.setDataValue(info.toJSON().toString());
		
		AON.insertDataResponseDetail(domain.getName(), domain.getId(), user.getLogin(), drd2);
		
		if(response.getData() != null) {
			Attach attach = new Attach()
					.setDomain(domain)
					.setAttachType(AttachType.DATA)
					.setType(response.isOk() 
						? DataAttachType.RESPONSE_OK.value() 
						: DataAttachType.RESPONSE_ERROR.value())
					.setSource(DataAttachSource.LROE.value())
					.setSourceId(dr.getId())
					.setMimeType(MimeType.XML)
					.setData(response.getData());
			
			AON.insertAttach(domain.getName(), domain.getId(), user.getLogin(), attach);
		}
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
