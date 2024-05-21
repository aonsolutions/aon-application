package net.aonsolutions.aon.tbai;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.List;

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
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatchDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.InvoiceTracking;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.DataRequestType;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.tbai.lroe.LROEInfo;
import net.aonsolutions.aon.tbai.lroe.LROEInformation;
import net.aonsolutions.aon.tbai.lroe.LROERequest;
import net.aonsolutions.aon.tbai.responses.LROEResponse;

public class LroeData {

	private LroeData() {
	
	}
	
	public static LROEInformation get(Domain domain, User user, Integer invoice, InvoiceType type) {
		LROEInformation lroe = new LROEInformation();
		AON.getDataResponseStream(domain.getName(), domain.getId(), user.getLogin(),
			DataResponseSource.LROE, f -> 
					f.getDomainProperty().eq(domain.getId())
					.and(f.getSourceProperty().eq(DataResponseSource.LROE.value()))
					.and(f.getSourceIdProperty().eq(invoice))).forEach(r -> {
				LROERequest request = new LROERequest();
				request.setDataResponse(r);
				if(r.getDataRequest() != null) {
					request.setDataRequest(AON.getDataRequest(domain.getName(), domain.getId(), user.getLogin(), f -> f.getIdProperty().eq(r.getDataRequest())));	
				}
				DataResponseDetail info = AON.getDataResponseDetail(domain.getName(), domain.getId(), user.getLogin(), f -> 
					f.getDataResponseProperty().eq(r.getId()).and(f.getDataVariableProperty().eq("info"))).orElse(new DataResponseDetail());
				if(!AonStringUtils.isBlank(info.getDataValue())) {
					JSONObject infoJson = new JSONObject(info.getDataValue());
					request.setInfo(new LROEInfo(infoJson));
				}
				
				DataResponseDetail json = AON.getDataResponseDetail(domain.getName(), domain.getId(), user.getLogin(), f -> 
					f.getDataResponseProperty().eq(r.getId()).and(f.getDataVariableProperty().eq("json"))).orElse(new DataResponseDetail());
				if(!AonStringUtils.isBlank(json.getDataValue())) {
					JSONObject jsonJson = new JSONObject(json.getDataValue());
					request.setResponse(new LROEResponse(jsonJson));
				}
				
				Attach requestAttach = AON.getAttach(domain.getName(), domain.getId(), user.getLogin(), f -> f.getSourceTypeProperty().eq(DataAttachSource.LROE.value())
						.and(f.getTypeProperty().eq(DataAttachType.REQUEST.value()))
						.and(f.getSourceBatchProperty().eq(r.getDataRequest())), AttachType.DATA, false);

				Attach responseAttach = AON.getAttach(domain.getName(), domain.getId(), user.getLogin(), f -> f.getSourceTypeProperty().eq(DataAttachSource.LROE.value())
						.and(f.getTypeProperty().eq(DataAttachType.RESPONSE_OK.value())
							.or(f.getTypeProperty().eq(DataAttachType.RESPONSE_ERROR.value())))
						.and(f.getSourceBatchProperty().eq(r.getId())), AttachType.DATA, false);
					
				JSONObject requestData = new JSONObject();
				requestData.put("domain_name", domain.getName());
				requestData.put("domain_id", domain.getId());
				requestData.put("id", requestAttach.getId());
				requestData.put("attach_type", AttachType.DATA.getName());
				String result = Base64.getEncoder().encodeToString(requestData.toString().getBytes(StandardCharsets.UTF_8));
				request.setRequestUrl("ms/api/file/" +  result);
				
				JSONObject responseData = new JSONObject();
				responseData.put("domain_name", domain.getName());
				responseData.put("domain_id", domain.getId());
				responseData.put("id", responseAttach.getId());
				responseData.put("attach_type", AttachType.DATA.getName());
				String responseResult = Base64.getEncoder().encodeToString(responseData.toString().getBytes(StandardCharsets.UTF_8));
				request.setResponseUrl("ms/api/file/" +  responseResult);

				if(InvoiceType.SALES.equals(type)) {
					lroe.getChapter1().addRequest(request);
				} else lroe.getChapter2().addRequest(request);
				
//				if("1".equals(request.getInfo().getCapitulo())) {
//					lroe.getChapter1().addRequest(request);
//				} else if("2".equals(request.getInfo().getCapitulo())) {
//					lroe.getChapter2().addRequest(request);
//				} else if("3".equals(request.getInfo().getCapitulo())) {
//					lroe.getChapter3().addRequest(request);
//				} else if("4".equals(request.getInfo().getCapitulo())) {
//					lroe.getChapter4().addRequest(request);
//				} else if("5".equals(request.getInfo().getCapitulo())) {
//					lroe.getChapter5().addRequest(request);
//				} else if("6".equals(request.getInfo().getCapitulo())) {
//					lroe.getChapter6().addRequest(request);
//				} else if("7".equals(request.getInfo().getCapitulo())) {
//					lroe.getChapter7().addRequest(request);
//				} else if("8".equals(request.getInfo().getCapitulo())) {
//					lroe.getChapter8().addRequest(request);
//				}
			});
		return lroe;
	}
	
	public static DataRequest saveRequest(Domain domain, User user, Invoice invoice, LROEInfo info, byte[] data) {
		JSONObject json = new JSONObject();
		json.put("lroe", info.toJSON());
		json.put("invoice", InvoiceJSON.toJSON(invoice));
		DataRequest request = new DataRequest()
			.setDomain(domain.getId())
			.setDate(new Date())
			.setBlackBox("")
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
	
	public static DataRequest saveRequest(Domain domain, User user, List<Invoice> invoice, LROEInfo info, byte[] data) {
		JSONObject json = new JSONObject();
		json.put("lroe", info.toJSON());
		json.put("invoices", InvoiceJSON.toJSON(invoice));
		DataRequest request = new DataRequest()
			.setDomain(domain.getId())
			.setDate(new Date())
			.setBlackBox("")
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
		
	public static DataResponse saveResponse(Domain domain, User user, Invoice invoice, LROEResponse response, LROEInfo info) {
		DataResponse dr = new DataResponse()
				.setDomain(domain.getId())
				.setCode(response.getResponseStatus())
				.setResponseDate(new Date())
				.setSource(DataResponseSource.LROE)
				.setSourceId(invoice.getId())
				.setDataRequest(response.getDataRequest().getId());
		
		dr = AON.insertDataResponse(domain.getName(), domain.getId(), user.getLogin(), dr);
		if(invoice.getId() != null) {
			InvoiceBatch invoiceBatch = new InvoiceBatch()
				.setDomain(domain.getId())
				.setDate(new Date())
				.setType(info.getCommunicationType())
				.setOperation(info.getCommunicationOperation())
				.setDataResponse(dr.getId())
				.setCreationUser(user.getLogin());
		
			InvoiceBatchDetail invoiceBatchDetail = new InvoiceBatchDetail()
				.setDomain(domain.getId())
				.setInvoice(invoice.getId())
				.setStatus(response.isOk()
					? InvoiceCommunicationStatus.ACCEPTED
					: InvoiceCommunicationStatus.WRONG);
		
			InvoiceTracking invoiceTracking = new InvoiceTracking()
				.setInvoiceBatch(invoiceBatch)
				.setInvoiceBatchDetail(invoiceBatchDetail);
		
			AON.saveInvoiceTracking(domain, user, invoiceTracking);
		
			InvoiceInfo invoiceInfo = AON.getInvoiceInfo(domain, user, f-> f.getInvoiceProperty().eq(invoice.getId())
				.and(f.getTypeProperty().eq(info.getCommunicationType().value())));
			if(invoiceInfo.isEmpty()) invoiceInfo = new InvoiceInfo()
				.setDomain(domain.getId())
				.setInvoice(invoice.getId())
				.setType(info.getCommunicationType());
			if(invoiceBatch.getOperation().isAnnulment() && response.isOk()) {
				invoiceInfo.setStatus(InvoiceCommunicationStatus.CANCELLED);
			} else if(invoiceInfo.getStatus().isPending()) {
				invoiceInfo.setStatus(response.isOk() ? InvoiceCommunicationStatus.ACCEPTED : InvoiceCommunicationStatus.WRONG);
			} else if(invoiceInfo.getStatus().isWrong() && response.isOk()) {
				invoiceInfo.setStatus(InvoiceCommunicationStatus.ACCEPTED);
			}
			AON.saveInvoiceInfo(domain, user, invoiceInfo);
		}
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
