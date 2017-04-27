package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.DataResponse.DATA_RESPONSE;
import static com.esferalia.aon.jooq.tables.DataResponseDetail.DATA_RESPONSE_DETAIL;

import java.util.Date;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Filter.DataResponseDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.DataResponseFilter;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DataResponseDetailFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DataResponseFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.DataResponseDetailPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.DataResponsePropertiesDAO;
import com.esferalia.aon.watson.server.AonDateUtils;

public class DataResponseDAO {

	private static final DataResponsePropertiesDAO DATA_RESPONSE_PROPERTIES = new DataResponsePropertiesDAO();
	private static final DataResponseDetailPropertiesDAO DATA_RESPONSE_DETAIL_PROPERTIES = new DataResponseDetailPropertiesDAO();
	
	public static Stream<DataResponse> getDataResponseStream(AONContext ctx, DataResponseFilter filter){	
		return DATA_RESPONSE_PROPERTIES.build( ctx.getDslContext()
				.select().from(DATA_RESPONSE).join(DATA_RESPONSE_DETAIL).on(DATA_RESPONSE.ID.eq(DATA_RESPONSE_DETAIL.DATA_RESPONSE)), filter)
			.fetchInto(DATA_RESPONSE).stream().map(new DataResponseFiller());		
	}
	
	public static DataResponse insertDataResponse(AONContext ctx, DataResponse dataResponse){	
		return ctx.getDslContext().insertInto(DATA_RESPONSE, DATA_RESPONSE.DOMAIN,
				DATA_RESPONSE.CODE, DATA_RESPONSE.RESPONSE_DATE, 
				DATA_RESPONSE.CREATION_DATE, DATA_RESPONSE.CREATION_USER,
				DATA_RESPONSE.MODIFICATION_DATE, DATA_RESPONSE.MODIFICATION_USER)
		.values(dataResponse.getDomain(), dataResponse.getNumber(), AonDateUtils.toSql(dataResponse.getIssueDate()),
				AonDateUtils.toTimestamp(new Date()), ctx.getUser(), AonDateUtils.toTimestamp(new Date()), ctx.getUser())
		.returning().fetch().stream().map(new DataResponseFiller()).findFirst().orElse(dataResponse);
	}
	
	public static DataResponse updateDataResponse(AONContext ctx, DataResponse dataResponse, DataResponseFilter filter){	
		return new DataResponse();
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
	
	public static DataResponseDetail insertDataResponseDetail(AONContext ctx, DataResponseDetail dataResponseDetail){	
		return ctx.getDslContext().insertInto(DATA_RESPONSE_DETAIL, DATA_RESPONSE_DETAIL.DOMAIN,
				DATA_RESPONSE_DETAIL.DATA_RESPONSE, DATA_RESPONSE_DETAIL.DATA_VARIABLE, DATA_RESPONSE_DETAIL.DATA_VALUE, 
				DATA_RESPONSE.CREATION_DATE, DATA_RESPONSE.CREATION_USER,
				DATA_RESPONSE.MODIFICATION_DATE, DATA_RESPONSE.MODIFICATION_USER)
		.values(dataResponseDetail.getDomain(), dataResponseDetail.getDataResponse(),
				dataResponseDetail.getDataVariable(), dataResponseDetail.getValue(),
				AonDateUtils.toTimestamp(new Date()), ctx.getUser(), AonDateUtils.toTimestamp(new Date()), ctx.getUser())
		.returning().fetch().stream().map(new DataResponseDetailFiller()).findFirst().orElse(dataResponseDetail);
	}
	
	public static DataResponseDetail updateDataResponseDetail(AONContext ctx, DataResponseDetail dataResponseDetail, DataResponseDetailFilter filter){	
		ctx.getDslContext().update(DATA_RESPONSE_DETAIL)
			.set(DATA_RESPONSE_DETAIL.DATA_RESPONSE, dataResponseDetail.getDataResponse())
			.set(DATA_RESPONSE_DETAIL.DATA_VARIABLE, dataResponseDetail.getDataVariable())
			.set(DATA_RESPONSE_DETAIL.DOMAIN, dataResponseDetail.getDomain())
			.set(DATA_RESPONSE_DETAIL.DATA_VALUE, dataResponseDetail.getValue())
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
}
