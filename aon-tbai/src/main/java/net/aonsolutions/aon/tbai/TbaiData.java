package net.aonsolutions.aon.tbai;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.DataRequestType;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.AttachmentDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataRequestDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceDataDAO;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.tbai.responses.TbaiResponse;
import ticketbai.emision.TicketBai;

public class TbaiData {

	private static final String TBAIURL = "tbaiUrl";
	
	private final InvoiceCommunicationConfiguration icc;
	
	private TbaiData(InvoiceCommunicationConfiguration icc) {
		this.icc = icc;
	}
	
	public InvoiceCommunicationConfiguration getInvoiceCommunicationConfiguration() {
		return icc;
	}
	
	public static TbaiData getInstance(InvoiceCommunicationConfiguration icc) {
		return new TbaiData(icc);
	}
	
	public boolean isTest() {
		return getInvoiceCommunicationConfiguration().isTest();
	}
	private DataResponseSource getDataResponseSource() {
		return isTest() ? DataResponseSource.TBAI_TEST : DataResponseSource.TBAI;
	}
	
	public TicketBai getTicketBai(Domain domain, User user, Integer invoice, InvoiceCommunicationConfiguration icc, boolean subsanar) throws Exception {
		DataResponseSource source = icc.isTest()
			? DataResponseSource.TBAI_TEST
			: DataResponseSource.TBAI;
		DataResponse dr = AON.getDataResponse(domain.getName(), domain.getId(), user.getLogin(), f -> 
			f.getDomainProperty().eq(domain.getId())
			.and(f.getSourceProperty().eq(source.value()))
			.and(f.getSourceIdProperty().eq(invoice))
			.and(f.getCodeProperty().eq(subsanar ? "error" : "ok")));
		
		Attach requestAttach = AON.getAttach(domain.getName(), domain.getId(), user.getLogin(), f -> f.getSourceTypeProperty().eq(DataAttachSource.TBAI.value())
				.and(f.getTypeProperty().eq(DataAttachType.REQUEST.value()))
				.and(f.getSourceBatchProperty().eq(dr.getDataRequest())), AttachType.DATA, true);
		
		final JAXBContext jaxbContext = JAXBContext.newInstance(TicketBai.class);
		final Unmarshaller jaxbMarshaller = jaxbContext.createUnmarshaller();
		InputStream is = new ByteArrayInputStream(requestAttach.getData());
		return (TicketBai) jaxbMarshaller.unmarshal(is);
	}
	
	public TBAIInformation get(Domain domain, User user, Integer invoice) {
		return get( domain.getName(), domain.getId(), user.getLogin(), invoice);
	}
	public TBAIInformation get(Occam occam, Integer invoice) {
		return get( occam.getDomainName(), occam.getDomain(), occam.getUser(), invoice);	
	}
	
	public TBAIInformation get(String domainName, Integer domainId, String user, Integer invoice) {
		TBAIInformation info = new TBAIInformation();
		DataResponseSource source = isTest() ? DataResponseSource.TBAI_TEST : DataResponseSource.TBAI;
		AON.getDataResponseStream(domainName, domainId, user,
			source, f -> 
				f.getDomainProperty().eq(domainId)
				.and(f.getSourceProperty().eq(source.value()))
				.and(f.getSourceIdProperty().eq(invoice))).forEach(r -> {
					TBAIRequest request = new TBAIRequest();
					request.setDataResponse(r);
					if(r.getDataRequest() != null) {
						request.setDataRequest(AON.getDataRequest(domainName, domainId, user, f -> f.getIdProperty().eq(r.getDataRequest())));
					}
					DataResponseDetail response = AON.getDataResponseDetail(domainName, domainId, user, f -> 
						f.getDataResponseProperty().eq(r.getId()).and(f.getDataVariableProperty().eq("response"))).orElse(new DataResponseDetail());
					if(!AonStringUtils.isBlank(response.getDataValue())) {
						JSONObject responseJson = new JSONObject(response.getDataValue());
						TbaiResponse tbaiResponse = new TbaiResponse(responseJson);
						tbaiResponse.setOk("ok".equalsIgnoreCase(r.getCode()));
						request.setResponse(tbaiResponse);
					}
					
					Attach requestAttach = AON.getAttach(domainName, domainId, user, f -> f.getSourceTypeProperty().eq(DataAttachSource.TBAI.value())
							.and(f.getTypeProperty().eq(DataAttachType.REQUEST.value()))
							.and(f.getSourceBatchProperty().eq(r.getDataRequest())), AttachType.DATA, false);

					Attach responseAttach = AON.getAttach(domainName, domainId, user, f -> f.getSourceTypeProperty().eq(DataAttachSource.TBAI.value())
							.and(f.getTypeProperty().eq(DataAttachType.RESPONSE_OK.value())
								.or(f.getTypeProperty().eq(DataAttachType.RESPONSE_ERROR.value())))
							.and(f.getSourceBatchProperty().eq(r.getId())), AttachType.DATA, false);
						
					JSONObject requestData = new JSONObject();
					requestData.put("domain_name", domainName);
					requestData.put("domain_id", domainId);
					requestData.put("id", requestAttach.getId());
					requestData.put("attach_type", AttachType.DATA.getName());
					String result = Base64.getEncoder().encodeToString(requestData.toString().getBytes(StandardCharsets.UTF_8));
					request.setRequestUrl("ms/api/file/" +  result);
					
					JSONObject responseData = new JSONObject();
					responseData.put("domain_name", domainName);
					responseData.put("domain_id", domainId);
					responseData.put("id", responseAttach.getId());
					responseData.put("attach_type", AttachType.DATA.getName());
					String responseResult = Base64.getEncoder().encodeToString(responseData.toString().getBytes(StandardCharsets.UTF_8));
					request.setResponseUrl("ms/api/file/" +  responseResult);
					info.addRequest(request);
			});
		return info;
	}
	
	
	public DataRequest saveRequest(Domain domain, User user, Invoice invoice, byte[] data) {
		JSONObject json = new JSONObject();
		json.put("tbai", "emision");
		json.put("invoice", InvoiceJSON.toJSON(invoice).toString());
		DataRequest request = new DataRequest()
				.setDomain(domain.getId())
				.setDate(new Date())
				.setBlackBox("")
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
				.setBlackBox("")
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
	
	public TbaiBlockchain getBlockchain(Domain domain, User user, Integer actualInvoice) {
		DataResponseSource source = isTest() ? DataResponseSource.TBAI_TEST : DataResponseSource.TBAI;
		DataResponse dr = AON.getLastDataResponse(domain.getName(), domain.getId(), user.getLogin(), f -> 
			f.getDomainProperty().eq(domain.getId())
			.and(f.getSourceProperty().eq(source.value()))
			.and(f.getSourceIdProperty().ne(actualInvoice))
			.and(f.getCodeProperty().ne("baja"))
			);
		
		DataResponseDetail drd = dr.getId() != null ? AON.getDataResponseDetail(domain.getName(), domain.getId(), user.getLogin(), f -> 
			f.getDomainProperty().eq(domain.getId())
			.and(f.getDataResponseProperty().eq(dr.getId()))
			.and(f.getDataVariableProperty().eq("blockchain"))).orElse(new DataResponseDetail()) : new DataResponseDetail();
		
		return TbaiBlockchain.fromJSON(drd.getDataValue());
	}
	
	public TbaiBlockchain getInvoiceBlockchain(Domain domain, User user, Integer invoice, boolean subsanar) {
		DataResponseSource source = isTest() ? DataResponseSource.TBAI_TEST : DataResponseSource.TBAI;
		DataResponse dr = AON.getDataResponse(domain.getName(), domain.getId(), user.getLogin(), f -> 
			f.getDomainProperty().eq(domain.getId())
			.and(f.getSourceProperty().eq(source.value()))
			.and(f.getSourceIdProperty().eq(invoice))
			.and(f.getCodeProperty().eq(subsanar ? "error" : "ok"))
			);
		
		DataResponseDetail drd = dr.getId() != null ? AON.getDataResponseDetail(domain.getName(), domain.getId(), user.getLogin(), f -> 
			f.getDomainProperty().eq(domain.getId())
			.and(f.getDataResponseProperty().eq(dr.getId()))
			.and(f.getDataVariableProperty().eq("blockchain"))).orElse(new DataResponseDetail()) : new DataResponseDetail();
		
		return TbaiBlockchain.fromJSON(drd.getDataValue());
	}

	public String getTbaiId(String domainName, Integer domainId, String login, Integer invoiceId) {
		InvoiceData id = AON.getInvoiceData(new Domain().setName(domainName).setId(domainId), new User().setLogin(login), f -> 
			f.getDomainProperty().eq(domainId)
			.and(f.getInvoiceProperty().eq(invoiceId))
			.and(f.getNameProperty().eq("TBAI_ID")));
		if(id != null && !AonStringUtils.isBlank(id.getValue())) {
			return id.getValue();
		} else {
			DataResponseSource source = isTest() ? DataResponseSource.TBAI_TEST : DataResponseSource.TBAI;
			DataResponse dr = AON.getDataResponse(domainName, domainId, login, f -> 
				f.getDomainProperty().eq(domainId)
				.and(f.getSourceProperty().eq(source.value()))
				.and(f.getSourceIdProperty().eq(invoiceId)));

			DataResponseDetail drd = !dr.isEmpty() ? AON.getDataResponseDetail(domainName, domainId, login, f -> 
				f.getDomainProperty().eq(domainId)
				.and(f.getDataResponseProperty().eq(dr.getId()))
				.and(f.getDataVariableProperty().eq("tbaiId"))).orElse(new DataResponseDetail()) : new DataResponseDetail();
	
			return drd.getDataValue();
		}
	}
	
	public byte[] getTbaiRequestFile(Domain domain, String login, Integer invoiceId) {
		DataResponseSource source = isTest() ? DataResponseSource.TBAI_TEST : DataResponseSource.TBAI;
		DataResponse dr = AON.getDataResponse(domain.getName(), domain.getId(), login, f -> 
			f.getDomainProperty().eq(domain.getId())
			.and(f.getSourceProperty().eq(source.value()))
			.and(f.getSourceIdProperty().eq(invoiceId)));
		if(dr.isEmpty()) return null;
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
	
	public Optional<String> getTbaiUrl(AONContext ctx, Integer domainId, Integer invoiceId) {
		return InvoiceDataDAO.get(ctx, domainId, invoiceId, "TBAI_URL")
			.map(InvoiceData::getValue)
			.or(() -> DataResponseDAO.getDetailValue(ctx, domainId, invoiceId, getDataResponseSource(), TBAIURL))
		;
				
		
	}
	public String getTbaiUrl(String domainName, Integer domainId, String login, Integer invoiceId) {
		InvoiceData id = AON.getInvoiceData(new Domain().setName(domainName).setId(domainId), new User().setLogin(login), f -> 
			f.getDomainProperty().eq(domainId)
			.and(f.getInvoiceProperty().eq(invoiceId))
			.and(f.getNameProperty().eq("TBAI_URL")));
		if(id != null && !AonStringUtils.isBlank(id.getValue())) {
			return id.getValue();
		} else {
			DataResponseSource source = isTest() ? DataResponseSource.TBAI_TEST : DataResponseSource.TBAI;
			DataResponse dr = AON.getDataResponse(domainName, domainId, login, f -> 
				f.getDomainProperty().eq(domainId)
				.and(f.getSourceProperty().eq(source.value()))
				.and(f.getSourceIdProperty().eq(invoiceId)));

			DataResponseDetail drd = !dr.isEmpty() ? AON.getDataResponseDetail(domainName, domainId, login, f -> 
				f.getDomainProperty().eq(domainId)
				.and(f.getDataResponseProperty().eq(dr.getId()))
				.and(f.getDataVariableProperty().eq(TBAIURL))).orElse(new DataResponseDetail()) : new DataResponseDetail();
	
			return drd.getDataValue();
		}
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
	
	public DataResponse saveResponseZuzendu(Domain domain, User user, Invoice invoice, byte[] request, TbaiResponse response, String tbaiUrl) {
		
		JSONObject json = new JSONObject();
		json.put("tbai", "emision");
		json.put("invoice", InvoiceJSON.toJSON(invoice).toString());
		DataRequest dataRequest = new DataRequest()
				.setDomain(domain.getId())
				.setDate(new Date())
				.setBlackBox("")
				.setType(DataRequestType.TBAI);
		String md5 = getMd5(dataRequest.getDomain() + dataRequest.getDate().toString() + dataRequest.getBlackBox() + dataRequest.getType().value());
		dataRequest.setMd5(md5);
		
		dataRequest = AON.saveDataRequest(domain.getName(), domain.getId(), user.getLogin(), dataRequest);
		
		Attach attach = new Attach()
				.setDomain(domain)
				.setAttachType(AttachType.DATA)
				.setType(DataAttachType.REQUEST.value())
				.setSource(DataAttachSource.TBAI.value())
				.setSourceId(dataRequest.getId())
				.setMimeType(MimeType.XML)
				.setData(request);
		
		AON.insertAttach(domain.getName(), domain.getId(), user.getLogin(), attach);
		
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
		
		
		
		DataResponseDetail drd = new DataResponseDetail()
				.setDomain(domain.getId())
				.setDataResponse(dr.getId())
				.setDataVariable("response")
				.setDataValue(response.toJSON().toString());
		
		AON.insertDataResponseDetail(domain.getName(), domain.getId(), user.getLogin(), drd);
		
		DataResponseDetail drd3 = new DataResponseDetail()
				.setDomain(domain.getId())
				.setDataResponse(dr.getId())
				.setDataVariable(TBAIURL)
				.setDataValue(tbaiUrl);
		
		AON.insertDataResponseDetail(domain.getName(), domain.getId(), user.getLogin(), drd3);
		
		if(response.getData() != null) {
			Attach responseAttach = new Attach()
					.setDomain(domain)
					.setAttachType(AttachType.DATA)
					.setType(response.isOk() 
						? DataAttachType.RESPONSE_OK.value() 
						: DataAttachType.RESPONSE_ERROR.value())
					.setSource(DataAttachSource.TBAI.value())
					.setSourceId(dr.getId())
					.setMimeType(MimeType.XML)
					.setData(response.getData());
			
			AON.insertAttach(domain.getName(), domain.getId(), user.getLogin(), responseAttach);
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
				.setDataVariable(TBAIURL)
				.setDataValue(tbaiUrl);
		
		AON.insertDataResponseDetail(domain.getName(), domain.getId(), user.getLogin(), drd3);

		invoice.ensureFiscal().setExpDate(new Date());
		LinkedList<EnterpriseActivity> list = new LinkedList<>();
		list.add(invoice.getActivity());
		AonConfiguration config = new AonConfiguration().setEnterpriseActivities(list);
		AON.saveInvoiceFiscal(domain.getName(), domain.getId(), user.getLogin(), config, invoice);
		
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
	
//	public static void main(String[] args) {
//		JSONObject requestData = new JSONObject();
//		requestData.put("domain_name", "mac.aonsolutions.net");
//		requestData.put("domain_id", 400);//24708); //24649); // 24427) ;// 22287);
//		requestData.put("id", 796969); //791139); //781651); // 780287); //780014); // 780002);
//		requestData.put("attach_type", AttachType.DATA.getName());
//		String result = Base64.getEncoder().encodeToString(requestData.toString().getBytes(StandardCharsets.UTF_8));
//		System.out.println("ms/api/file/" +  result);
//	}
	
	// 	*****************************************************************
	// 	*****************************************************************
	// 	*****************************************************************
	public Optional<TBAIInformation> get(AONContext ctx, Integer domainId, Integer invoiceId) {
		return DataResponseDAO.getLastDataResponse(ctx, domainId, getDataResponseSource(), invoiceId) 
			.filter( r -> r.getDataRequest() != null)
			.map(r -> new TBAIRequest()
				.setDataRequest(DataRequestDAO.get(ctx, r.getDataRequest()).orElse(null))
				.setDataResponse(r)
				.setResponse(DataResponseDAO.getDetail(ctx, domainId, invoiceId, getDataResponseSource(), "response")
					.map(resp -> new TbaiResponse(new JSONObject(resp.getDataValue())).setOk("ok".equalsIgnoreCase(r.getCode())))
					.orElse(null)
				)
				.setRequestUrl ( getRequestUrl(ctx, domainId, r.getDataRequest()).orElse(null))
				.setResponseUrl( getResponseUrl(ctx, domainId, r.getDataRequest()).orElse(null))
			)
			.map(info -> new TBAIInformation().addRequest(info) )
		;
	}

	private Optional<String> getRequestUrl(AONContext ctx, Integer domainId, Integer dataRequestId) {
		return AttachmentDAO.getDataAttachStream(ctx, f 
				-> f.getSourceTypeProperty().eq(DataAttachSource.TBAI.value())
				.and(f.getTypeProperty().eq(DataAttachType.REQUEST.value()))
				.and(f.getSourceBatchProperty().eq(dataRequestId)), false)
		.findFirst()
		.map( attach -> {
			JSONObject requestData = new JSONObject()
				.put("domain_name", ctx.getDomainName())
				.put("domain_id", domainId)
				.put("id", attach.getId())
				.put("attach_type", AttachType.DATA.getName())
			;
			String result = Base64.getEncoder().encodeToString(requestData.toString().getBytes(StandardCharsets.UTF_8));
			return ("ms/api/file/" +  result);
		});
	}
	
	private Optional<String> getResponseUrl(AONContext ctx, Integer domainId, Integer dataResponseId) {
	return AttachmentDAO.getDataAttachStream(ctx, f -> f.getSourceTypeProperty().eq(DataAttachSource.TBAI.value())
			.and(f.getTypeProperty().eq(DataAttachType.RESPONSE_OK.value()).or(f.getTypeProperty().eq(DataAttachType.RESPONSE_ERROR.value())))
			.and(f.getSourceBatchProperty().eq(dataResponseId)), false)
		.findFirst()
		.map( attach -> {
			JSONObject responseData = new JSONObject()
				.put("domain_name", ctx.getDomainName())
				.put("domain_id", domainId)
				.put("id", attach.getId())
				.put("attach_type", AttachType.DATA.getName());
			String responseResult = Base64.getEncoder().encodeToString(responseData.toString().getBytes(StandardCharsets.UTF_8));
			return ("ms/api/file/" +  responseResult);
			
		});
	}
	
}
