package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.DataResponse.DATA_RESPONSE;
import static com.esferalia.aon.jooq.tables.DataResponseDetail.DATA_RESPONSE_DETAIL;
import static com.esferalia.aon.jooq.tables.IncomeDetail.INCOME_DETAIL;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.Options;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.DataResponseDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.DataResponseFilter;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelTypeVisitor;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATResponse;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DataResponseDetailFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DataResponseFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.DataResponseDetailPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.DataResponsePropertiesDAO;
import com.esferalia.aon.occam.server.fiscal.AEATJson;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

public class DataResponseDAO {
	
	private DataResponseDAO() {

	}

	private static final DataResponsePropertiesDAO DATA_RESPONSE_PROPERTIES = new DataResponsePropertiesDAO();
	private static final DataResponseDetailPropertiesDAO DATA_RESPONSE_DETAIL_PROPERTIES = new DataResponseDetailPropertiesDAO();
	

	
	public static SelectConditionStep<Record> select(AONContext ctx, DataResponseFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(DATA_RESPONSE)
				.where(DATA_RESPONSE_PROPERTIES.getConditions(filter));
	}
	
	public static DataResponse get(AONContext ctx, DataResponseFilter filter, Options...options) {
		DataResponse dr = select(ctx, filter).orderBy(DATA_RESPONSE.ID.desc()).limit(1)
			.fetch().stream().map(new DataResponseFiller()).findFirst().orElse(new DataResponse());
		if(options.length > 0 && options[0].isFull())
			dr.setDetails(getDataResponseDetailStream(ctx, f -> f.getDataResponseProperty().eq(dr.getId())).collect(Collectors.toCollection(LinkedList::new)));
		return dr;
	}
	
	public static Stream<DataResponse> getStream(AONContext ctx, DataResponseFilter filter){	
		return select(ctx, filter).fetch().stream().map(new DataResponseFiller());
	}
	
	public static Stream<DataResponse> getDataResponseStream(AONContext ctx, DataResponseSource source, DataResponseFilter filter){	
		if(DataResponseSource.QUALITY.equals(source)) {
			return DATA_RESPONSE_PROPERTIES.build( ctx.getDslContext()
					.selectDistinct(DATA_RESPONSE.fields()).from(DATA_RESPONSE)
					.join(INCOME_DETAIL).on(INCOME_DETAIL.ID.eq(DATA_RESPONSE.SOURCE_ID))
					.leftOuterJoin(DATA_RESPONSE_DETAIL).on(DATA_RESPONSE.ID.eq(DATA_RESPONSE_DETAIL.DATA_RESPONSE)), filter)
				.fetchInto(DATA_RESPONSE).stream().map(new DataResponseFiller())
				.sorted((a1, a2) -> a2.getId().compareTo(a1.getId()));
		}
		return DATA_RESPONSE_PROPERTIES.build( ctx.getDslContext()
				.selectDistinct(DATA_RESPONSE.fields()).from(DATA_RESPONSE)
				.leftOuterJoin(DATA_RESPONSE_DETAIL).on(DATA_RESPONSE.ID.eq(DATA_RESPONSE_DETAIL.DATA_RESPONSE)), filter)
			.fetchInto(DATA_RESPONSE).stream().map(new DataResponseFiller())
			.sorted((a1, a2) -> a2.getId().compareTo(a1.getId()));		
	}
	
	public static DataResponse insertDataResponse(AONContext ctx, DataResponse dataResponse){	
		Integer id = ctx.getDslContext().insertInto(DATA_RESPONSE)
				.set(DATA_RESPONSE.DOMAIN, dataResponse.getDomain())
				.set(DATA_RESPONSE.CODE, dataResponse.getCode())
				.set(DATA_RESPONSE.RESPONSE_DATE, AonDateUtils.toSql(dataResponse.getResponseDate()))
				.set(DATA_RESPONSE.SOURCE, dataResponse.getSource().value())
				.set(DATA_RESPONSE.SOURCE_ID, dataResponse.getSourceId())
				.set(DATA_RESPONSE.CREATION_DATE, AonDateUtils.toTimestamp(new Date()))
				.set(DATA_RESPONSE.CREATION_USER, ctx.getUser())
				.set(DATA_RESPONSE.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
				.set(DATA_RESPONSE.MODIFICATION_USER, ctx.getUser())
				.set(DATA_RESPONSE.DATA_REQUEST, dataResponse.getDataRequest())
				.returning(DATA_RESPONSE.ID).fetchOne().getId();
		return dataResponse.setId(id);
	}
	
	public static Integer updateDataResponse(AONContext ctx, DataResponse dataResponse, DataResponseFilter filter){
		return ctx.getDslContext().update(DATA_RESPONSE)
			.set(DATA_RESPONSE.DOMAIN, dataResponse.getDomain())
			.set(DATA_RESPONSE.CODE, dataResponse.getCode())
			.set(DATA_RESPONSE.RESPONSE_DATE, AonDateUtils.toSql(dataResponse.getResponseDate())) 
			.set(DATA_RESPONSE.SOURCE, dataResponse.getSource().value())
			.set(DATA_RESPONSE.SOURCE_ID, dataResponse.getSourceId())
			.set(DATA_RESPONSE.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
			.set(DATA_RESPONSE.MODIFICATION_USER, ctx.getUser())
			.set(DATA_RESPONSE.DATA_REQUEST, dataResponse.getDataRequest())
			.where(DATA_RESPONSE_PROPERTIES.getConditions(filter))
			.execute();
	}
	
	public static DataResponse deleteDataResponse(AONContext ctx, DataResponseFilter filter){	
		ctx.getDslContext().delete(DATA_RESPONSE).where(DATA_RESPONSE_PROPERTIES.getConditions(filter)).execute();
		return new DataResponse();
	}
	
	public static Stream<DataResponseDetail> getDataResponseDetailStream(AONContext ctx, DataResponseDetailFilter filter){	
		return DATA_RESPONSE_DETAIL_PROPERTIES.build( ctx.getDslContext()
				.select().from(DATA_RESPONSE_DETAIL), filter)
			.fetchInto(DATA_RESPONSE_DETAIL).stream().map(new DataResponseDetailFiller());		
	}

	public static DataResponse getLast(AONContext ctx, DataResponseFilter filter){
		return ctx.getDslContext()
			.select().from(DATA_RESPONSE)
			.where(DATA_RESPONSE_PROPERTIES.getConditions(filter))
			.orderBy(DATA_RESPONSE.ID.desc()).limit(1)
			.fetch().stream().map(new DataResponseFiller())
			.findFirst().orElse(new DataResponse());
	}
	
	public static Stream<DataResponseDetail> getLastDataResponseDetailStream(AONContext ctx, DataResponseFilter filter){
		com.esferalia.aon.jooq.tables.DataResponseDetail DATA_RESPONSE_DETAIL_2 = 
		DATA_RESPONSE_DETAIL.as("data_response_detail_2");
		
		return ctx.getDslContext()
			.select(DATA_RESPONSE_DETAIL.asterisk())
			.from(DATA_RESPONSE)
			.innerJoin(DATA_RESPONSE_DETAIL).on(DATA_RESPONSE.ID.eq(DATA_RESPONSE_DETAIL.DATA_RESPONSE))
			.leftOuterJoin(DATA_RESPONSE_DETAIL_2).on(DATA_RESPONSE_DETAIL.DATA_RESPONSE.eq(DATA_RESPONSE_DETAIL_2.DATA_RESPONSE).and(DATA_RESPONSE_DETAIL.ID.lt(DATA_RESPONSE_DETAIL_2.ID )))
			.where(DATA_RESPONSE_PROPERTIES.getConditions(filter))
			.and(DATA_RESPONSE_DETAIL_2.ID.isNull())
			.fetchInto(DATA_RESPONSE_DETAIL).stream().map(new DataResponseDetailFiller());
	}
	
	public static Stream<DataResponseDetail> __getLastDataResponseDetailStream(AONContext ctx, DataResponseFilter filter){
		Select<Record1<Integer>> subQuery = ctx.getDslContext()
			.select(DSL.max(DATA_RESPONSE_DETAIL.ID).as(DATA_RESPONSE_DETAIL.ID))
			.from(DATA_RESPONSE).leftOuterJoin(DATA_RESPONSE_DETAIL).on(DATA_RESPONSE.ID.eq(DATA_RESPONSE_DETAIL.DATA_RESPONSE))
			.where(DATA_RESPONSE_PROPERTIES.getConditions(filter))
			.groupBy(DATA_RESPONSE_DETAIL.DATA_RESPONSE);
		return ctx.getDslContext()
			.select().from(DATA_RESPONSE_DETAIL)
			.where(DATA_RESPONSE_DETAIL.ID.in(subQuery))
			.fetchInto(DATA_RESPONSE_DETAIL).stream().map(new DataResponseDetailFiller());
	}

	public static DataResponseDetail getLastDataResponseDetail(AONContext ctx, Integer dataResponseId){
		return ctx.getDslContext()
			.select().from(DATA_RESPONSE_DETAIL)
			.where(DATA_RESPONSE_DETAIL.DATA_RESPONSE.eq(dataResponseId))
			.orderBy(DATA_RESPONSE_DETAIL.ID.desc())
			.limit(1)
			.fetchInto(DATA_RESPONSE_DETAIL)
			.stream()
			.map(new DataResponseDetailFiller())
			.findFirst()
			.orElse(null);
	}

	public static DataResponseDetail insertDataResponseDetail(AONContext ctx, DataResponseDetail dataResponseDetail){	
		return ctx.getDslContext().insertInto(DATA_RESPONSE_DETAIL, DATA_RESPONSE_DETAIL.DOMAIN,
				DATA_RESPONSE_DETAIL.DATA_RESPONSE, DATA_RESPONSE_DETAIL.DATA_VARIABLE, DATA_RESPONSE_DETAIL.DATA_VALUE, 
				DATA_RESPONSE_DETAIL.CREATION_DATE, DATA_RESPONSE_DETAIL.CREATION_USER,
				DATA_RESPONSE_DETAIL.MODIFICATION_DATE, DATA_RESPONSE_DETAIL.MODIFICATION_USER)
		.values(dataResponseDetail.getDomain(), dataResponseDetail.getDataResponse(),
				dataResponseDetail.getDataVariable(), dataResponseDetail.getDataValue(),
				AonDateUtils.toTimestamp(new Date()), ctx.getUser(), AonDateUtils.toTimestamp(new Date()), ctx.getUser())
		.returning().fetch().stream().map(new DataResponseDetailFiller()).findFirst().orElse(dataResponseDetail);
	}

	public static DataResponseDetail updateDataResponseDetail(AONContext ctx, DataResponseDetail dataResponseDetail, DataResponseDetailFilter filter){	
		ctx.getDslContext().update(DATA_RESPONSE_DETAIL)
			.set(DATA_RESPONSE_DETAIL.DATA_RESPONSE, dataResponseDetail.getDataResponse())
			.set(DATA_RESPONSE_DETAIL.DATA_VARIABLE, dataResponseDetail.getDataVariable())
			.set(DATA_RESPONSE_DETAIL.DOMAIN, dataResponseDetail.getDomain())
			.set(DATA_RESPONSE_DETAIL.DATA_VALUE, dataResponseDetail.getDataValue())
			.set(DATA_RESPONSE_DETAIL.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
			.set(DATA_RESPONSE_DETAIL.MODIFICATION_USER, ctx.getUser())
			.where(DATA_RESPONSE_DETAIL_PROPERTIES.getConditions(filter))
			.execute();
		
		return new DataResponseDetail();
	}
	
	public static DataResponseDetail deleteDataResponseDetail(AONContext ctx, DataResponseDetailFilter filter){	
		ctx.getDslContext().delete(DATA_RESPONSE_DETAIL).where(DATA_RESPONSE_DETAIL_PROPERTIES.getConditions(filter)).execute();
		return new DataResponseDetail();
	}
	
	/// -------------------------------------------------------
	public static DataResponse insertAEATResponse(AONContext ctx, IFiscalModel fm, String aeatResponse ){
		final Pair<DataResponseSource,DataAttachSource> pair = getDataResponseData( fm );
		if (pair.getLeft() == null || pair.getRight() == null) {
			throw new AonCoreException(" Modelo no soportado en la grabación de la respuesta");
		}
		AEATResponse response = AEATJson.toJSON(aeatResponse.getBytes());
		DataResponse dr = DataResponseDAO.insertDataResponse(ctx, 
			new DataResponse()
				.setSource(pair.getLeft())
				.setSourceId( fm.getId() )
				.setCode("Presentación AEAT")
				.setDomain( fm.getDomain() )
				.setResponseDate(new Date()));
		String key = "RESPUESTA AEAT";
		DataResponseDetail drd = new DataResponseDetail()
				.setDomain(dr.getDomain())
				.setDataResponse(dr.getId())
				.setDataVariable(key)
				.setDataValue(aeatResponse);
		DataResponseDAO.insertDataResponseDetail(ctx, drd);
		if(AonStringUtils.isNotBlank(response.getUrlPdf())) {
			AttachmentDAO.insertDataAttach(ctx, new Attach()
				.setSourceType(pair.getRight().value())
				.setSourceBatch( fm.getId() )
				.setType(DataAttachType.RESPONSE_OK.value())
				.setAttachType(AttachType.DATA)
				.setDomain(new Domain().setId(fm.getDomain()))
				.setData(getUrlFile(response.getUrlPdf()))
				.setMimeType(MimeType.PDF)
				.setDescription("Presentacion AEAT"));
		}
		return dr;
	}
	
	// Carga del PDF de la declaración del modelo fiscal, de forma manual por el usuario 
	public static void insertPDFModel(AONContext ctx, IFiscalModel fm, String data) {
		
		final Pair<DataResponseSource,DataAttachSource> pair = getDataResponseData( fm );
		if (pair.getLeft() == null || pair.getRight() == null) {
			throw new AonCoreException("Modelo no soportado en la grabación del PDF presentado");
		}
		
		// Primero borramos el que ya exista previamente, para que se quede solo el último PDF presentado (bien cargado manualmente o grabado por la AEAT)
		deleteAEATResponse(ctx, fm); 
		
		// Ahora añadimos un registro a data_response y otro a data_attach
		String codeDescription = "Carga PDF Manual";
		DataResponseDAO.insertDataResponse(ctx, 
			new DataResponse()
				.setSource(pair.getLeft())
				.setSourceId(fm.getId())
				.setCode(codeDescription)
				.setDomain(fm.getDomain())
				.setResponseDate(new Date()));

		byte[] data64 = Base64.getDecoder().decode(data);
		AttachmentDAO.insertDataAttach(ctx, new Attach()
				.setSourceType(pair.getRight().value())
				.setSourceBatch(fm.getId())
				.setType(DataAttachType.RESPONSE_OK.value())
				.setAttachType(AttachType.DATA)
				.setDomain(new Domain().setId(fm.getDomain()))
				.setData( data64 )
				.setMimeType(MimeType.PDF)
				.setDescription(codeDescription));
		
	}
	
	public static Pair<DataResponseSource, DataAttachSource> getDataResponseData(IFiscalModel fm) {
		final Pair<DataResponseSource,DataAttachSource> pair = new Pair<>(null, null);
		fm.getModel().visit( new IFiscalModelTypeVisitor() {
			@Override
			public void visitM303() {
				pair.setLeft( DataResponseSource.MOD303 ).setRight(DataAttachSource.MOD303);
			}
			@Override 
			public void visitM111() { 
				pair.setLeft( DataResponseSource.MOD111 ).setRight(DataAttachSource.MOD111);
			}
			@Override 
			public void visitM115() { 
				pair.setLeft( DataResponseSource.MOD115 ).setRight(DataAttachSource.MOD115);
			}
			@Override 
			public void visitM123() { 
				pair.setLeft( DataResponseSource.MOD123 ).setRight(DataAttachSource.MOD123);
			}
			@Override 
			public void visitM130() {
				pair.setLeft( DataResponseSource.MOD130 ).setRight(DataAttachSource.MOD130);
			}
			@Override 
			public void visitM131() {
				pair.setLeft( DataResponseSource.MOD131 ).setRight(DataAttachSource.MOD131);
			}
			
			@Override 
			public void visitM202() {
				pair.setLeft( DataResponseSource.MOD202 ).setRight(DataAttachSource.MOD202);
			}
			@Override 
			public void visitM190() { 
				pair.setLeft( DataResponseSource.MOD190 ).setRight(DataAttachSource.MOD190);
			}
			@Override 
			public void visitM390() { 
				pair.setLeft( DataResponseSource.MOD390 ).setRight(DataAttachSource.MOD390);
			}
			@Override public void visitM390HF() { /* nothing */ }
			
			@Override 
			public void visitM349() { 
				pair.setLeft( DataResponseSource.MOD349 ).setRight(DataAttachSource.MOD349); 
			}
			
			@Override 
			public void visitM347() { 
				pair.setLeft( DataResponseSource.MOD347 ).setRight(DataAttachSource.MOD347); 
			}
			
			@Override public void visitM200() { /* nothing */ }
			
			@Override 
			public void visitM193() { 
				pair.setLeft( DataResponseSource.MOD193 ).setRight(DataAttachSource.MOD193); 
			}
			
			@Override 
			public void visitM184() { 
				pair.setLeft( DataResponseSource.MOD184 ).setRight(DataAttachSource.MOD184); 
			}
			
			@Override 
			public void visitM180() { 
				pair.setLeft( DataResponseSource.MOD180 ).setRight(DataAttachSource.MOD180);
			}
			
		});
		return pair;
	}

	public static void deleteAEATResponse(AONContext ctx, IFiscalModel fm) {
		final Pair<DataResponseSource,DataAttachSource> pair = getDataResponseData( fm );
		if (pair.getLeft() == null || pair.getRight() == null) {
			throw new AonCoreException(" Modelo no soportado en la grabación de la respuesta (DataResponseDAO).");
		}
		ctx.getDslContext()
			.select(DATA_RESPONSE.fields())
			.from(DATA_RESPONSE)
			.where(DATA_RESPONSE.DOMAIN.eq(fm.getDomain()))
			.and(DATA_RESPONSE.SOURCE_ID.eq(fm.getId()))
			.and(DATA_RESPONSE.SOURCE.eq(pair.getLeft().value()))
			.fetch()
			.stream()
			.map(new DataResponseFiller())
			.forEach( dr -> {
				ctx.log().info("Deleting dataResponseDetail");
				deleteDataResponseDetail(ctx, f-> f.getDataResponseProperty().eq( dr.getId()).and( f.getDomainProperty().eq(fm.getDomain()) ) );
				ctx.log().info("Deleting dataAttach");
				int count = AttachmentDAO.deleteDataAttach(ctx, f -> 
				 			  f.getSourceTypeProperty().eq(pair.getRight().value())   
						.and( f.getSourceBatchProperty().eq(fm.getId()))
						.and( f.getDomainProperty().eq(fm.getDomain()))
						.and( f.getTypeProperty().eq(DataAttachType.RESPONSE_OK.value()))
						);
				ctx.log().info("Deleting dataResponse {0} filas", count);
				deleteDataResponse(ctx, f-> f.getIdProperty().eq( dr.getId()) );
			});
	}	

	private static synchronized byte[] getUrlFile(String pdfUrl) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = null;
		try {
			URL url = new URL(pdfUrl);
			is = url.openStream();
			AonIOUtils.copy(is, baos);
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			AonIOUtils.closeQuietly(is);
		}
		return null;
	}	
	
}
