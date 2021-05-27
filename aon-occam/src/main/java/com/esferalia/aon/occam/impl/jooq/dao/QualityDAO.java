package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.DataResponse.DATA_RESPONSE;
import static com.esferalia.aon.jooq.tables.ElaborationDetail.ELABORATION_DETAIL;
import static com.esferalia.aon.jooq.tables.Income.INCOME;
import static com.esferalia.aon.jooq.tables.IncomeDetail.INCOME_DETAIL;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;


import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectForUpdateStep;
import org.jooq.SelectOffsetStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.warehouse.PaturpatQuality;
import com.esferalia.aon.occam.api.model.warehouse.UdapaQuality;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.PaturpatQualityFiller;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.UdapaQualityFiller;
import com.esferalia.aon.watson.server.AonDateUtils;

public class QualityDAO {
	
	
	public static Stream<UdapaQuality> getUdapaQualityStream(AONContext ctx, Map<String, String[]> map){
		SelectConditionStep<Record> a = ctx.getDslContext().select()
				.from(DATA_RESPONSE).join(INCOME_DETAIL)
		.on(DATA_RESPONSE.SOURCE_ID.eq(INCOME_DETAIL.ID).and(DATA_RESPONSE.SOURCE.eq(DataResponseSource.QUALITY.value())))
		.join(INCOME).on(INCOME.ID.eq(INCOME_DETAIL.INCOME))
		.join(REGISTRY).on(INCOME.SUPPLIER.eq(REGISTRY.ID))
		.where(DATA_RESPONSE.DOMAIN.eq(ctx.getDomainId()));
		
		if(map.containsKey("from")) {
			String from = map.get("from")[0];
			Date d = new Date(Long.parseLong(from));
			a = a.and(DATA_RESPONSE.RESPONSE_DATE.ge(AonDateUtils.toSql(d)));
		}
		
		if(map.containsKey("to")){
			String to = map.get("to")[0];
			Date d = new Date(Long.parseLong(to));
			a = a.and(DATA_RESPONSE.RESPONSE_DATE.le(AonDateUtils.toSql(d)));
		}
		
		if(map.containsKey("number")){
			Condition c = DATA_RESPONSE.CODE.eq(map.get("number")[0]);
			for(Integer i = 1; i < map.get("number").length ; i++){
				c = c.or(DATA_RESPONSE.CODE.eq(map.get("number")[i]));
			}
			a = a.and(c);
		} 
		
		if(map.containsKey("code")){
			a =  a.and(DATA_RESPONSE.CODE.like("%" + map.get("code")[0] + "%"));
		} 

		if(map.containsKey("source")){
			Condition c = DATA_RESPONSE.SOURCE_ID.eq(Integer.parseInt(map.get("source")[0]));				
			for(Integer i = 1; i < map.get("source").length ; i++){
				c = c.or(DATA_RESPONSE.SOURCE_ID.eq(Integer.parseInt(map.get("source")[i])));
			}
			a = a.and(c);
		}
		
		if(map.containsKey("supplier")){
			Condition c = INCOME.SUPPLIER.eq(Integer.parseInt(map.get("supplier")[0]));				
			for(Integer i = 1; i < map.get("supplier").length ; i++){
				c = c.or(INCOME.SUPPLIER.eq(Integer.parseInt(map.get("supplier")[i])));
			}
			a = a.and(c);
		}
		a.orderBy(DATA_RESPONSE.ID.desc());
		
		SelectForUpdateStep<Record> b = null;
		SelectOffsetStep<Record> c = null;
		if(map.containsKey("per_page") && map.containsKey("page")) {
			String per_page = map.get("per_page")[0];
			Integer perPage = Integer.parseInt(per_page);
			String page_str = map.get("page")[0];
			Integer page = Integer.parseInt(page_str);
			b = a.limit(perPage).offset(perPage * (page -1));
		} else if(map.containsKey("per_page")){
			String per_page = map.get("per_page")[0];
			Integer perPage = Integer.parseInt(per_page);
			c = a.limit(perPage);
		}
		

		if(c != null) return c.fetch().stream().map(new UdapaQualityFiller());
		else if(b != null) return b.fetch().stream().map(new UdapaQualityFiller());
		else return a.fetch().stream().map(new UdapaQualityFiller());
	}
	
	
	public static Stream<PaturpatQuality> getPaturpatQualityStream(AONContext ctx, Map<String, String[]> map){
		SelectConditionStep<Record> a = ctx.getDslContext().select()
				.from(DATA_RESPONSE).join(ELABORATION_DETAIL)
		.on(DATA_RESPONSE.SOURCE_ID.eq(ELABORATION_DETAIL.ID).and(DATA_RESPONSE.SOURCE.eq(DataResponseSource.PATURPAT_QUALITY.value())))
		.join(ITEM).on(ITEM.ID.eq(ELABORATION_DETAIL.ITEM))
		.join(PRODUCT).on(ITEM.PRODUCT.eq(PRODUCT.ID))
		.where(DATA_RESPONSE.DOMAIN.eq(ctx.getDomainId()));
		
		if(map.containsKey("from")) {
			String from = map.get("from")[0];
			Date d = new Date(Long.parseLong(from));
			a = a.and(DATA_RESPONSE.RESPONSE_DATE.ge(AonDateUtils.toSql(d)));
		}
		
		if(map.containsKey("to")){
			String to = map.get("to")[0];
			Date d = new Date(Long.parseLong(to));
			a = a.and(DATA_RESPONSE.RESPONSE_DATE.le(AonDateUtils.toSql(d)));
		}
		
		if(map.containsKey("number")){
			Condition c = DATA_RESPONSE.CODE.eq(map.get("number")[0]);
			for(Integer i = 1; i < map.get("number").length ; i++){
				c = c.or(DATA_RESPONSE.CODE.eq(map.get("number")[i]));
			}
			a = a.and(c);
		} 
		
		if(map.containsKey("code")){
			a =  a.and(DATA_RESPONSE.CODE.like("%" + map.get("code")[0] + "%"));
		} 

		if(map.containsKey("source")){
			Condition c = DATA_RESPONSE.SOURCE_ID.eq(Integer.parseInt(map.get("source")[0]));				
			for(Integer i = 1; i < map.get("source").length ; i++){
				c = c.or(DATA_RESPONSE.SOURCE_ID.eq(Integer.parseInt(map.get("source")[i])));
			}
			a = a.and(c);
		}
		
		a.orderBy(DATA_RESPONSE.ID.desc());
		
		SelectForUpdateStep<Record> b = null;
		SelectOffsetStep<Record> c = null;
		if(map.containsKey("per_page") && map.containsKey("page")) {
			String per_page = map.get("per_page")[0];
			Integer perPage = Integer.parseInt(per_page);
			String page_str = map.get("page")[0];
			Integer page = Integer.parseInt(page_str);
			b = a.limit(perPage).offset(perPage * (page -1));
		} else if(map.containsKey("per_page")){
			String per_page = map.get("per_page")[0];
			Integer perPage = Integer.parseInt(per_page);
			c = a.limit(perPage);
		}
		
		if(c != null) return c.fetch().stream().map(new PaturpatQualityFiller());
		else if(b != null) return b.fetch().stream().map(new PaturpatQualityFiller());
		else return a.fetch().stream().map(new PaturpatQualityFiller());
	}
}
