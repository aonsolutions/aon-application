package net.aonsolutions.aon.tbai;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
import com.esferalia.aon.occam.api.model.finance.InvoiceDataName;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.DataRequestType;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.AttachmentDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataRequestDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceFiscalDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceDataDAO;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.tbai.responses.TbaiResponse;
import ticketbai.emision.TicketBai;

public class TbaiData {

	private static final String TBAIURL = "tbaiUrl";
	private static final String TBAIID = "tbaiId";
	
	private final AONContext ctx;
	private final InvoiceCommunicationConfiguration icc;
	
	private TbaiData(AONContext ctx, InvoiceCommunicationConfiguration icc) {
		this.ctx = ctx;
		this.icc = icc;
	}
	
	public InvoiceCommunicationConfiguration getInvoiceCommunicationConfiguration() {
		return icc;
	}
	
	public AONContext getAONContext() {
		return ctx;
	}
	
	public static TbaiData getInstance(AONContext ctx, InvoiceCommunicationConfiguration icc) {
		return new TbaiData(ctx, icc);
	}
	
	public boolean isTest() {
		return isTest(getInvoiceCommunicationConfiguration());
	}
	public boolean isTest(InvoiceCommunicationConfiguration icc) {
		return getInvoiceCommunicationConfiguration()
			.getTbaiData()
			.map( tb -> tb.isTest())
			.orElse(false)
		;
	}
	private DataResponseSource getDataResponseSource() {
		return isTest() ? DataResponseSource.TBAI_TEST : DataResponseSource.TBAI;
	}
	
	public TicketBai getTicketBai(AONContext ctx, Domain domain, Integer invoice, InvoiceCommunicationConfiguration icc, boolean subsanar) throws Exception {
		DataResponseSource source = isTest( icc )
			? DataResponseSource.TBAI_TEST
			: DataResponseSource.TBAI;
		DataResponse dr = DataResponseDAO.get(ctx, f -> 
			f.getDomainProperty().eq(domain.getId())
			.and(f.getSourceProperty().eq(source.value()))
			.and(f.getSourceIdProperty().eq(invoice))
			.and(f.getCodeProperty().eq(subsanar ? "error" : "ok")));
		
		Attach requestAttach = AttachmentDAO.getDataAttach(ctx, f -> f.getSourceTypeProperty().eq(DataAttachSource.TBAI.value())
				.and(f.getTypeProperty().eq(DataAttachType.REQUEST.value()))
				.and(f.getSourceBatchProperty().eq(dr.getDataRequest())), true);
		
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
		DataResponseDAO.getDataResponseStream(getAONContext(), source, f -> 
				f.getDomainProperty().eq(domainId)
				.and(f.getSourceProperty().eq(source.value()))
				.and(f.getSourceIdProperty().eq(invoice))).forEach(r -> {
					TBAIRequest request = new TBAIRequest();
					request.setDataResponse(r);
					if(r.getDataRequest() != null) {
						request.setDataRequest(DataRequestDAO.get(getAONContext(), f -> f.getIdProperty().eq(r.getDataRequest())));
					}
					
					DataResponseDetail response = DataResponseDAO.getDataResponseDetailStream(getAONContext(), f -> 
						f.getDataResponseProperty().eq(r.getId()).and(f.getDataVariableProperty().eq("response")))
						.findFirst().orElse(new DataResponseDetail());
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
	
	public DataRequest saveRequest(AONContext ctx, Domain domain, byte[] data) {
		return InvoiceCommunicationDAO.saveRequest(ctx, domain, InvoiceCommunicationType.TBAI, data);
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
		return InvoiceDataDAO.get(ctx, domainId, invoiceId, InvoiceDataName.TBAI_URL)
			.map(InvoiceData::getValue)
			.or(() -> DataResponseDAO.getDetailValue(ctx, domainId, invoiceId, getDataResponseSource(), TBAIURL))
		;	
	}
	
	public String getTbaiUrl(Integer domain, Integer invoiceId) {
		return getTbaiUrl(getAONContext(), domain, invoiceId).orElse(null);
	}
	
	public Optional<String> getTbaiId(AONContext ctx, Integer domainId, Integer invoiceId) {
		return InvoiceDataDAO.get(ctx, domainId, invoiceId, InvoiceDataName.TBAI_ID)
			.map(InvoiceData::getValue)
			.or(() -> DataResponseDAO.getDetailValue(ctx, domainId, invoiceId, getDataResponseSource(), TBAIID))
		;	
	}
	
	public String getTbaiId(Integer domainId, Integer invoiceId) {
		return getTbaiId(getAONContext(), domainId, invoiceId).orElse(null);
	}
	
	public DataResponse saveResponse(AONContext ctx, Domain domain, Integer invoiceId, TbaiResponse response, DataResponse dr) {
		InvoiceCommunicationStatus status = response.isOk() 
				? InvoiceCommunicationStatus.ACCEPTED 
				: InvoiceCommunicationStatus.WRONG;
		InvoiceCommunicationDAO.saveInvoiceInfo(ctx, domain.getId(), invoiceId, InvoiceCommunicationType.TBAI, status);
		
		dr.setCode(response.getResponseStatus());
		DataResponseDAO.updateDataResponse(ctx, dr, f -> f.getIdProperty().eq(dr.getId()));
		
		DataResponseDetail drd = new DataResponseDetail()
				.setDomain(domain.getId())
				.setDataResponse(dr.getId())
				.setDataVariable("response")
				.setDataValue(response.toJSON().toString());
		
		DataResponseDAO.insertDataResponseDetail(ctx, drd);
		
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
			
			AttachmentDAO.insertDataAttach(ctx, attach);
		}
		return dr;
	}
	
	public DataResponse saveResponseZuzendu(AONContext ctx, Domain domain, Invoice invoice, byte[] request, TbaiResponse response, String tbaiUrl) {
		InvoiceCommunicationStatus status = response.isOk() 
				? InvoiceCommunicationStatus.ACCEPTED 
				: InvoiceCommunicationStatus.WRONG;
		InvoiceCommunicationDAO.saveInvoiceInfo(ctx, domain.getId(), invoice.getId(), InvoiceCommunicationType.TBAI, status);
		
		DataRequest dataRequest = InvoiceCommunicationDAO.saveRequest(ctx, domain, InvoiceCommunicationType.TBAI, request);
		
		DataResponseSource source = isTest() ? DataResponseSource.TBAI_TEST : DataResponseSource.TBAI;
		DataResponse dr = new DataResponse()
				.setDomain(domain.getId())
				.setCode(response.getResponseStatus())
				.setResponseDate(new Date())
				.setSource(source)
				.setSourceId(invoice.getId())
				.setDataRequest(dataRequest.getId());
		
		dr = DataResponseDAO.insertDataResponse(ctx, dr);
		
		DataResponseDetail drd1 = new DataResponseDetail()
				.setDomain(domain.getId())
				.setDataResponse(dr.getId())
				.setDataVariable("tbaiId")
				.setDataValue(response.getTbaiId());
		
		DataResponseDAO.insertDataResponseDetail(ctx, drd1);
		
		DataResponseDetail drd = new DataResponseDetail()
				.setDomain(domain.getId())
				.setDataResponse(dr.getId())
				.setDataVariable("response")
				.setDataValue(response.toJSON().toString());
		
		DataResponseDAO.insertDataResponseDetail(ctx, drd);
		
		DataResponseDetail drd3 = new DataResponseDetail()
				.setDomain(domain.getId())
				.setDataResponse(dr.getId())
				.setDataVariable(TBAIURL)
				.setDataValue(tbaiUrl);
		
		DataResponseDAO.insertDataResponseDetail(ctx, drd3);
		
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
			AttachmentDAO.insertDataAttach(ctx, responseAttach);
		}
		
		return dr;
	}
	
	public DataResponse saveResponsePending(AONContext ctx, Domain domain, Invoice invoice, TbaiResponse response, TbaiBlockchain blockchain, DataRequest dataRequest, String tbaiUrl) {
		DataResponseSource source = isTest() ? DataResponseSource.TBAI_TEST : DataResponseSource.TBAI;
		DataResponse dr = new DataResponse()
				.setDomain(domain.getId())
				.setCode(response.getResponseStatus())
				.setResponseDate(new Date())
				.setSource(source)
				.setSourceId(invoice.getId())
				.setDataRequest(dataRequest.getId());
		
		dr = DataResponseDAO.insertDataResponse(ctx, dr);
		
		DataResponseDetail drd1 = new DataResponseDetail()
				.setDomain(domain.getId())
				.setDataResponse(dr.getId())
				.setDataVariable("tbaiId")
				.setDataValue(response.getTbaiId());
		DataResponseDAO.insertDataResponseDetail(ctx, drd1);
		InvoiceDataDAO.save(ctx, domain.getId(), invoice.getId(), InvoiceDataName.TBAI_ID, response.getTbaiId());	

		DataResponseDetail drd2 = new DataResponseDetail()
				.setDomain(domain.getId())
				.setDataResponse(dr.getId())
				.setDataVariable("blockchain")
				.setDataValue(blockchain.toJSON().toString());
		DataResponseDAO.insertDataResponseDetail(ctx, drd2);
		
		DataResponseDetail drd3 = new DataResponseDetail()
				.setDomain(domain.getId())
				.setDataResponse(dr.getId())
				.setDataVariable(TBAIURL)
				.setDataValue(tbaiUrl);
		DataResponseDAO.insertDataResponseDetail(ctx, drd3);
		InvoiceDataDAO.save(ctx, domain.getId(), invoice.getId(), InvoiceDataName.TBAI_URL, tbaiUrl);

		
		invoice.ensureFiscal().setExpDate(new Date());
		LinkedList<EnterpriseActivity> list = new LinkedList<>();
		list.add(invoice.getActivity());
		AonConfiguration config = new AonConfiguration().setEnterpriseActivities(list);
		InvoiceFiscalDAO.save(ctx, config, invoice);
		return dr;
	}
	
	public DataResponse saveResponseAnulacion(AONContext ctx, Domain domain, Invoice invoice, TbaiResponse response, DataRequest dataRequest) {
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
		
			dr = DataResponseDAO.insertDataResponse(ctx, dr);
		
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
				AttachmentDAO.insertDataAttach(ctx, attach);
			}
		}
		return dr;
	}

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
