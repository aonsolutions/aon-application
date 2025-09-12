package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.DataResponse.DATA_RESPONSE;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.DataResponseDetail.DATA_RESPONSE_DETAIL;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import org.jooq.Record;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.SiiConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SiiConfigurationDAO {
	
	private SiiConfigurationDAO() {

	}
	
	public static SiiConfiguration get(AONContext ctx) {
		ctx.checkRead();
		
		ApplicationParameter administration = AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())	
			.and(f.getNameProperty().eq(AppParam.FS_DEFAULT_ADMINISTRATION.toString())))
			.findFirst().orElse(new ApplicationParameter());

		SiiConfiguration sii = new SiiConfiguration();
		sii.setAdministration(administration.getValue() != null
				? Administration.safeValueOf(Integer.parseInt(administration.getValue()))
				: Administration.UNKNOWN);
		
		AppParamDAO.getApplicationParameterStream(ctx, f -> 
				f.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getNameProperty().like("SII_%")))
		.forEach(r -> {
			if(r.getName().equalsIgnoreCase(AppParam.SII_ACTIVE.toString())) {
				sii.setActive(r.getValue() != null && ("true".equalsIgnoreCase(r.getValue()) || "1".equals(r.getValue())));
			}
			
			if(r.getName().equalsIgnoreCase(AppParam.SII_TEST.toString())) {
				sii.setTest(r.getValue() != null && ("true".equalsIgnoreCase(r.getValue()) || "1".equals(r.getValue())));
			}
			
			if(r.getName().equalsIgnoreCase(AppParam.SII_AUTOSEND.toString())) {
				sii.setAutosend(r.getValue() != null && ("true".equalsIgnoreCase(r.getValue()) || "1".equals(r.getValue())));
			}
			
			if(r.getName().equalsIgnoreCase(AppParam.SII_INCLUDE_DATE.toString())) {
				sii.setIncludeDate(AonDateUtils.parse(r.getValue(), "yyyy-MM-dd"));
			}
			
			if(r.getName().equalsIgnoreCase(AppParam.SII_REGISTRY_DATE.toString())) {
				sii.setRegistryDate(r.getValue());
			}
			
			if(r.getName().equalsIgnoreCase(AppParam.SII_PREPARE_NEW_SII.toString())) {
				sii.setPrepareNewSii(r.getValue() != null && ("true".equalsIgnoreCase(r.getValue()) || "1".equals(r.getValue())));
			}
			
		});
		
		ApplicationParameter cfgSii = AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())	
			.and(f.getNameProperty().eq(AppParam.FS_MODEL_CFG_SII.toString())))
				.findFirst().orElse(new ApplicationParameter());
		if(cfgSii != null && !AonStringUtils.isBlank(cfgSii.getValue()) )
			sii.setRegistryDate("R".equalsIgnoreCase(cfgSii.getValue()) ? "audit" : "tax");
	
		if(sii.getIncludeDate() == null) {
    		String defaultDate = Administration.COMMON_TERRITORY.equals(sii.getAdministration()) ? "2017-07-01" : "2018-01-01";
    		sii.setIncludeDate(AonDateUtils.parse(defaultDate, "yyyy-MM-dd"));
		}
		
		return sii;
	}

	public static SiiConfiguration save(AONContext ctx, SiiConfiguration tc) {
		ctx.checkWrite();	

		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.SII_ACTIVE.toString(),
				Boolean.toString(tc.isActive()));
		
		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.SII_TEST.toString(),
				Boolean.toString(tc.isTest()));

		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.SII_AUTOSEND.toString(),
				Boolean.toString(tc.isAutosend()));
		
		if(tc.getIncludeDate() != null) {
			AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.SII_INCLUDE_DATE.toString(),
				AonDateUtils.format(tc.getIncludeDate(), "yyyy-MM-dd"));
		}
		
		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.SII_REGISTRY_DATE.toString(),
				tc.getRegistryDate());
		
		if("audit".equalsIgnoreCase(tc.getRegistryDate()) ) {
			AppParamDAO.insertApplicationParameter(ctx, 
					AppParam.FS_MODEL_CFG_SII.toString(),
					"R");
		}
		return tc;
	}
	
	public static void prepareNewSii(AONContext ctx) {
		ctx.getDslContext()
			.select(DATA_RESPONSE.fields())
			.select(DATA_RESPONSE_DETAIL.fields())
			.from(DATA_RESPONSE)
			.join(INVOICE).on(INVOICE.ID.eq(DATA_RESPONSE.SOURCE_ID))
			.join(DATA_RESPONSE_DETAIL).on(DATA_RESPONSE.ID.eq(DATA_RESPONSE_DETAIL.DATA_RESPONSE))
			.where(DATA_RESPONSE.DOMAIN.eq(ctx.getDomainId()))
			.and(DATA_RESPONSE.SOURCE.eq(DataResponseSource.SII_INVOICE.value()))
			.and(DATA_RESPONSE_DETAIL.DOMAIN.eq(ctx.getDomainId()))
			.and(DATA_RESPONSE_DETAIL.DATA_VARIABLE.eq("status"))
			.fetch()
			.stream()
			.map(new PrepareNewSiiFiller()).forEach( dr -> {
				if(dr.getDetails() != null && !dr.getDetails().isEmpty() && dr.getSourceId() != null) {
					String status = dr.getDetails().get(0).getDataValue();
					InvoiceInfo info = new InvoiceInfo()
						.setDomain(dr.getDomain())
						.setInvoice(dr.getSourceId())
						.setType(InvoiceCommunicationType.SII)
						.setStatus(InvoiceCommunicationStatus.safeValueOf(status));
					InvoiceInfoDAO.save(ctx, info);
				}
			});	
	
		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.SII_PREPARE_NEW_SII.toString(),
				Boolean.toString(true));
	}
	

	public static class PrepareNewSiiFiller extends Filler implements Function<Record, DataResponse> {
		
		@Override
		public DataResponse apply(Record r) {
			DataResponse dataResponse = new DataResponse()
				.setId(getValue(r, DATA_RESPONSE.ID))
				.setDomain(getValue(r, DATA_RESPONSE.DOMAIN))
				.setSource(DataResponseSource.safeValueOf(getByte(r, DATA_RESPONSE.SOURCE)))
				.setSourceId(getValue(r, DATA_RESPONSE.SOURCE_ID))
				.setDataRequest(getValue(r, DATA_RESPONSE.DATA_REQUEST))
				.setCode(getValue(r, DATA_RESPONSE.CODE));
			
			DataResponseDetail dataResponseDetail = new DataResponseDetail()
					.setId(getValue(r, DATA_RESPONSE_DETAIL.ID))
					.setDomain(getValue(r, DATA_RESPONSE_DETAIL.DOMAIN))
					.setDataVariable(getValue(r, DATA_RESPONSE_DETAIL.DATA_VARIABLE))
					.setDataValue(getValue(r, DATA_RESPONSE_DETAIL.DATA_VALUE))
					.setDataResponse(getValue(r, DATA_RESPONSE_DETAIL.DATA_RESPONSE));			
			
			List<DataResponseDetail> list = new LinkedList<>();
			list.add(dataResponseDetail);
			dataResponse.setDetails(list);
			return dataResponse;
		}
	}
}




