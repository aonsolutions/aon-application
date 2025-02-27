package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.ItemAddinfo.ITEM_ADDINFO;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.ItemAddInfoFilter;
import com.esferalia.aon.occam.api.model.product.ItemAddInfo;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.ItemAddInfoFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.ItemAddInfoPropertiesDAO;
import com.esferalia.aon.watson.server.AonDateUtils;

public class ItemAddInfoDAO {
	
	private static final ItemAddInfoPropertiesDAO INFO_ADDINFO_PROPERTIES = new ItemAddInfoPropertiesDAO();
	
	public static Stream<ItemAddInfo> getStream(AONContext ctx, ItemAddInfoFilter filter){
		return ctx.getDslContext().select()
					.from(ITEM_ADDINFO)
					.where(INFO_ADDINFO_PROPERTIES.getConditions(filter))
					.fetch()
					.stream()
					.map(new ItemAddInfoFiller());
	}
	
	public static ItemAddInfo save(AONContext ctx, ItemAddInfo itemAddInfo) {
		return itemAddInfo.getId() == null ? insert(ctx, itemAddInfo) : update(ctx, itemAddInfo);
	}
	
	public static ItemAddInfo insert(AONContext ctx, ItemAddInfo i) {
		Integer id = ctx.getDslContext()
				.insertInto(
						ITEM_ADDINFO, 
						ITEM_ADDINFO.DOMAIN, 
						ITEM_ADDINFO.PRODUCT, 
						ITEM_ADDINFO.ITEM, 
						ITEM_ADDINFO.ATTRIBUTE, 
						ITEM_ADDINFO.VALUE, 
						ITEM_ADDINFO.VALUE_DATE
				).values(
						i.getDomain(), 
						i.getProduct(), 
						i.getItem(), 
						i.getAttribute(), 
						i.getValue(), 
						AonDateUtils.toSql(i.getDate())
				).returning(ITEM_ADDINFO.ID)
				.fetchOne()
				.getValue(ITEM_ADDINFO.ID);
		
		ctx.log().debug("INSERT ITEM_ADDINFO id: " +id);
		
		i.setId(id);
		
		return i;
	}
	
	public static ItemAddInfo update(AONContext ctx, ItemAddInfo i) {
		ctx.getDslContext()
			.update(ITEM_ADDINFO)
			.set(ITEM_ADDINFO.VALUE, i.getValue())
			.set(ITEM_ADDINFO.VALUE_DATE, AonDateUtils.toSql(i.getDate()))
			.where(ITEM_ADDINFO.ID.eq(i.getId()))
		.execute();
		
		ctx.log().debug("UPDATE ITEM_ADDINFO id:" + i.getId());
		
		return i;
	}
	
	public static void delete(AONContext ctx, Integer id) {
		ctx.getDslContext()
			.delete(ITEM_ADDINFO)
			.where(ITEM_ADDINFO.ID.eq(id))
		.execute();
		
		ctx.log().debug("DELETE ITEM_ADDINFO id:" + id);
	}
	
}
