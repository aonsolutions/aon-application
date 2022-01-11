package net.aonsolutions.aon.tbai;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
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
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.DataRequestType;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;

import net.aonsolutions.aon.tbai.responses.TbaiResponse;

public class TbaiData {

	TbaiConfiguration tbaiConfiguration;
	
	public TbaiData(TbaiConfiguration tbaiConfiguration) {
		this.tbaiConfiguration = tbaiConfiguration;
	}
	
	public TbaiConfiguration getTbaiConfiguration() {
		return tbaiConfiguration;
	}
	
	public void setTbaiConfiguration(TbaiConfiguration tbaiConfiguration) {
		this.tbaiConfiguration = tbaiConfiguration;
	}
	
	public static TbaiData getInstance(TbaiConfiguration tbaiConfiguration ) {
		return new TbaiData(tbaiConfiguration);
	}
	
	public boolean isTest() {
		return getTbaiConfiguration().isTest();
	}
	
	public DataRequest saveRequest(Domain domain, User user, Invoice invoice, byte[] data) {
		JSONObject json = new JSONObject();
		json.put("tbai", "emision");
		json.put("invoice", InvoiceJSON.toJSON(invoice).toString());
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
	
	public DataRequest saveRequestAnulacion(Domain domain, User user, Invoice invoice, byte[] data) {
		JSONObject json = new JSONObject();
		json.put("tbai", "baja");
		json.put("invoice", InvoiceJSON.toJSON(invoice).toString());
		DataRequest request = new DataRequest()
				.setDomain(domain.getId())
				.setDate(new Date())
				.setBlackBox(json.toString())
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
	
	public TbaiBlockchain getBlockchain(Domain domain, User user) {
		DataResponseSource source = isTest() ? DataResponseSource.TBAI_TEST : DataResponseSource.TBAI;
		DataResponse dr = AON.getLastDataResponse(domain.getName(), domain.getId(), user.getLogin(), f -> 
			f.getDomainProperty().eq(domain.getId())
			.and(f.getSourceProperty().eq(source.value()))
			.and(f.getCodeProperty().ne("baja"))
			);
		
		DataResponseDetail drd = dr.getId() != null ? AON.getDataResponseDetail(domain.getName(), domain.getId(), user.getLogin(), f -> 
			f.getDomainProperty().eq(domain.getId())
			.and(f.getDataResponseProperty().eq(dr.getId()))
			.and(f.getDataVariableProperty().eq("blockchain"))).orElse(new DataResponseDetail()) : new DataResponseDetail();
		
		return TbaiBlockchain.fromJSON(drd.getDataValue());
	}

	public String getTbaiId(String domainName, Integer domainId, String login, Integer invoiceId) {
		DataResponseSource source = isTest() ? DataResponseSource.TBAI_TEST : DataResponseSource.TBAI;
		DataResponse dr = AON.getDataResponse(domainName, domainId, login, source, f -> 
		f.getDomainProperty().eq(domainId)
			.and(f.getSourceProperty().eq(source.value()))
			.and(f.getSourceIdProperty().eq(invoiceId)));

		DataResponseDetail drd = dr != null && dr.getId() != null ? AON.getDataResponseDetail(domainName, domainId, login, f -> 
			f.getDomainProperty().eq(domainId)
			.and(f.getDataResponseProperty().eq(dr.getId()))
			.and(f.getDataVariableProperty().eq("tbaiId"))).orElse(new DataResponseDetail()) : new DataResponseDetail();
	
		return drd.getDataValue();
	}
	
	public byte[] getTbaiRequestFile(Domain domain, String login, Integer invoiceId) {
		DataResponseSource source = isTest() ? DataResponseSource.TBAI_TEST : DataResponseSource.TBAI;
		DataResponse dr = AON.getDataResponse(domain.getName(), domain.getId(), login, source, f -> 
			f.getDomainProperty().eq(domain.getId())
			.and(f.getSourceProperty().eq(source.value()))
			.and(f.getSourceIdProperty().eq(invoiceId)));
		if(dr == null || dr.getId() == null) return null;
		Integer aux = dr.getDataRequest();
		if(aux == null) {
			DataRequest drq = AON.getDataRequestStream(domain.getName(), domain.getId(), login, f -> 
					f.getTypeProperty().eq(DataRequestType.TBAI.value()))
			.filter(r -> {
				JSONObject json = new  JSONObject(r.getBlackBox());
				Integer id = 0;
				if(json.opt("invoice") != null) {
					id = JsonUtils.getInteger(json.getJSONObject("invoice"), IJsonNames.ID);	
				} else {
					id = JsonUtils.getInteger(json, IJsonNames.ID);
				}
				return invoiceId.equals(id);
			}).findFirst().orElse(new DataRequest());
			aux = drq.getId();
			dr.setDataRequest(aux);
			AON.updateDataResponse(domain.getName(), domain.getId(), login, dr, f -> f.getIdProperty().eq(dr.getId()));
		}
		Integer dataRequest = aux;
		Attach attach = AON.getAttach(domain.getName(), domain.getId(), login, f -> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getSourceTypeProperty().eq(DataAttachSource.TBAI.value()))
				.and(f.getSourceBatchProperty().eq(dataRequest))
				.and(f.getTypeProperty().eq(DataAttachType.REQUEST.value()))
			, AttachType.DATA, true);

		return attach.getData();
	}
	
	public String getTbaiUrl(String domainName, Integer domainId, String login, Integer invoiceId) {
		DataResponseSource source = isTest() ? DataResponseSource.TBAI_TEST : DataResponseSource.TBAI;
		DataResponse dr = AON.getDataResponse(domainName, domainId, login, source, f -> 
			f.getDomainProperty().eq(domainId)
			.and(f.getSourceProperty().eq(source.value()))
			.and(f.getSourceIdProperty().eq(invoiceId)));

		DataResponseDetail drd = dr != null && dr.getId() != null ? AON.getDataResponseDetail(domainName, domainId, login, f -> 
			f.getDomainProperty().eq(domainId)
			.and(f.getDataResponseProperty().eq(dr.getId()))
			.and(f.getDataVariableProperty().eq("tbaiUrl"))).orElse(new DataResponseDetail()) : new DataResponseDetail();
	
		return drd.getDataValue();
	}
	
	
	public String getTbaiId(Domain domain, User user, Integer invoiceId) {
		return getTbaiId(domain.getName(),  domain.getId(), user.getLogin(), invoiceId);
	}
	
	public DataResponse saveResponse(Domain domain, User user, TbaiResponse response, DataResponse dr) {
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
	
	public DataResponse saveResponsePending(Domain domain, User user, Invoice invoice, TbaiResponse response, TbaiBlockchain blockchain, DataRequest dataRequest, String tbaiUrl) {
		DataResponseSource source = isTest() ? DataResponseSource.TBAI_TEST : DataResponseSource.TBAI;
		DataResponse dr = new DataResponse()
				.setDomain(domain.getId())
				.setCode(response.getResponseStatus())
				.setResponseDate(new Date())
				.setSource(source)
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

		DataResponseDetail drd3 = new DataResponseDetail()
				.setDomain(domain.getId())
				.setDataResponse(dr.getId())
				.setDataVariable("tbaiUrl")
				.setDataValue(tbaiUrl);
		
		AON.insertDataResponseDetail(domain.getName(), domain.getId(), user.getLogin(), drd3);
		
		return dr;
	}
	
	public DataResponse saveResponseAnulacion(Domain domain, User user, Invoice invoice, TbaiResponse response, DataRequest dataRequest) {
		DataResponse dr = null;
		DataResponseSource source = isTest() ? DataResponseSource.TBAI_TEST : DataResponseSource.TBAI;
		if(response.isOk()) {
			dr = new DataResponse()
				.setDomain(domain.getId())
				.setCode("baja")
				.setResponseDate(new Date())
				.setSource(source)
				.setSourceId(invoice.getId())
				.setDataRequest(dataRequest.getId());
		
			dr = AON.insertDataResponse(domain.getName(), domain.getId(), user.getLogin(), dr);
		
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
		}
		return dr;
	}
	
	public String getMd5(String str){
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
