package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.ProductTag.PRODUCT_TAG;
import static com.esferalia.aon.jooq.tables.Tag.TAG;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.occam.api.AONContext;


public class ProductDAO {
	
	public static List<String> getProductTags(AONContext ctx) {
		ctx.checkRead();
		final List<String> list = new LinkedList<String>();
		ctx.getDslContext()
			.select(TAG.NAME)
			.from(TAG)
			.where(TAG.DOMAIN.equal(ctx.getDomainId()))
			.fetch()
			.stream()
			.forEach( record -> list.add(record.getValue(TAG.NAME) ));
		return list; 
	}

	public static Map<Integer,String[]> getProductTagMap(AONContext ctx) {
		ctx.checkRead();
		final Map<Integer,String[]> map = new HashMap<Integer, String[]>();
		ctx.getDslContext()
			.select(PRODUCT.ID,TAG.NAME)
			.from(PRODUCT)
			.join(PRODUCT_TAG).on(PRODUCT_TAG.PRODUCT.equal(PRODUCT.ID))
			.join(TAG).on(TAG.ID.equal(PRODUCT_TAG.TAG))
			.where(PRODUCT.DOMAIN.equal(ctx.getDomainId()))
			.fetch()
			.stream()
			.forEach( record -> {
				Integer key = record.getValue(PRODUCT.ID); 
				String tag = record.getValue(TAG.NAME);
				String[] tags = map.get(key);
				if (tags == null) {
					tags = new String[1];
					tags[0] = tag;
					map.put(key, tags);
				} else {
					int length = tags.length;
					tags = Arrays.copyOf(tags, length + 1);
					tags[length] = tag;
				}
			});
		return map; 
	}
	
}
