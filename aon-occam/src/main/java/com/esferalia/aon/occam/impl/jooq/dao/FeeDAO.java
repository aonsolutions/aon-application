package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.CustomerFee.CUSTOMER_FEE;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;

import java.sql.Date;
import java.util.Vector;
import java.util.stream.Stream;

import org.jooq.InsertValuesStep1;
import org.jooq.InsertValuesStep17;
import org.jooq.Record1;
import org.jooq.Record17;

import com.esferalia.aon.jooq.tables.records.ProductRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.impl.jooq.validation.FeeValidation;



public class FeeDAO {

	public static Fee getFee(AONContext ctx, Integer id){
		ctx.checkRead();
		
		Record17<Integer, Integer, Integer, Short, Integer, String, Double, Double, String, Date, Date, Date, Short, Byte, Integer, Integer, Integer> record = ctx.getDslContext()
			.select(CUSTOMER_FEE.DOMAIN, CUSTOMER_FEE.PROJECT, CUSTOMER_FEE.CUSTOMER, CUSTOMER_FEE.LINE, CUSTOMER_FEE.ITEM, CUSTOMER_FEE.DESCRIPTION, CUSTOMER_FEE.QUANTITY, CUSTOMER_FEE.PRICE, CUSTOMER_FEE.DISCOUNT_EXPR, CUSTOMER_FEE.INITIAL_DATE, CUSTOMER_FEE.FINAL_DATE, CUSTOMER_FEE.BILLING_DATE, CUSTOMER_FEE.PERIOD, CUSTOMER_FEE.SECURITY_LEVEL, CUSTOMER_FEE.INVOICING_GROUP, CUSTOMER_FEE.SELLER, CUSTOMER_FEE.WORKPLACE)
			.from(CUSTOMER_FEE)
			.where(CUSTOMER_FEE.ID.eq(id))
			.fetchOne();
		if(record != null){
			Fee fee = new Fee();
			fee.setId(id);
			if(record.value1() != null) fee.setDomain(record.value1());
			if(record.value2() != null) fee.setProjectId(record.value2());
			if(record.value3() != null) fee.setClientId(record.value3());
			if(record.value4() != null) fee.setLine(record.value4());
			if(record.value5() != null) fee.setItemId(record.value5());
			if(record.value6() != null) fee.setDescription(record.value6());
			if(record.value7() != null) fee.setQuantity(record.value7());
			if(record.value8() != null) fee.setPrice(record.value8());
			if(record.value9() != null) fee.setDiscountExpr(record.value9());
			if(record.value10() != null) fee.setStartDate(record.value10());
			if(record.value11() != null) fee.setEndDate(record.value11());
			if(record.value12() != null) fee.setBillingDate(record.value12());
			if(record.value13() != null) fee.setPeriod(record.value13());
			if(record.value14() != null) fee.setConfidential(record.value14() == 1);
			if(record.value15() != null) fee.setBillingGroup(record.value15());
			if(record.value16() != null) fee.setSellerId(record.value16());
			if(record.value17() != null) fee.setWorkplaceId(record.value17());
			return fee;
		}
		return null;
	}
	
	public static void insert(AONContext ctx, Fee f) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			FeeValidation.validate(ctx, f);
			ctx.getDslContext()
				.insertInto(PRODUCT, PRODUCT.DOMAIN, CUSTOMER_FEE.PROJECT, CUSTOMER_FEE.CUSTOMER, CUSTOMER_FEE.LINE, CUSTOMER_FEE.ITEM, CUSTOMER_FEE.DESCRIPTION, CUSTOMER_FEE.QUANTITY, CUSTOMER_FEE.PRICE, CUSTOMER_FEE.DISCOUNT_EXPR, CUSTOMER_FEE.INITIAL_DATE, CUSTOMER_FEE.FINAL_DATE, CUSTOMER_FEE.BILLING_DATE, CUSTOMER_FEE.PERIOD, CUSTOMER_FEE.SECURITY_LEVEL, CUSTOMER_FEE.INVOICING_GROUP, CUSTOMER_FEE.SELLER, CUSTOMER_FEE.WORKPLACE)
				.values(f.getDomain(), f.getProjectId(), f.getClientId(), f.getLine(), f.getItemId(), f.getDescription(), f.getQuantity(), f.getPrice(), f.getDiscountExpr(), new Date(f.getStartDate().getTime()), new Date(f.getEndDate().getTime()), new Date(f.getBillingDate().getTime()), f.getPeriod(), f.getSecurityLevel(), f.getBillingGroup(), f.getSellerId(), f.getWorkplaceId())
				.execute();
		});
	}

	public static void insert(AONContext ctx, Stream<Fee> fs) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			InsertValuesStep17<ProductRecord, Integer, Integer, Integer, Short, Integer, String, Double, Double, String, Date, Date, Date, Short, Byte, Integer, Integer, Integer> insertQuery = ctx.getDslContext().insertInto(PRODUCT, PRODUCT.DOMAIN, CUSTOMER_FEE.PROJECT, CUSTOMER_FEE.CUSTOMER, CUSTOMER_FEE.LINE, CUSTOMER_FEE.ITEM, CUSTOMER_FEE.DESCRIPTION, CUSTOMER_FEE.QUANTITY, CUSTOMER_FEE.PRICE, CUSTOMER_FEE.DISCOUNT_EXPR, CUSTOMER_FEE.INITIAL_DATE, CUSTOMER_FEE.FINAL_DATE, CUSTOMER_FEE.BILLING_DATE, CUSTOMER_FEE.PERIOD, CUSTOMER_FEE.SECURITY_LEVEL, CUSTOMER_FEE.INVOICING_GROUP, CUSTOMER_FEE.SELLER, CUSTOMER_FEE.WORKPLACE);
			fs.forEach(f ->{
				FeeValidation.validate(ctx, f);
				insertQuery.values(f.getDomain(), f.getProjectId(), f.getClientId(), f.getLine(), f.getItemId(), f.getDescription(), f.getQuantity(), f.getPrice(), f.getDiscountExpr(), new Date(f.getStartDate().getTime()), new Date(f.getEndDate().getTime()), new Date(f.getBillingDate().getTime()), f.getPeriod(), f.getSecurityLevel(), f.getBillingGroup(), f.getSellerId(), f.getWorkplaceId());
			});
			insertQuery.execute();
		});
	}

	public static void update(AONContext ctx, Fee f) {
		
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			FeeValidation.validate(ctx, f);
			ctx.getDslContext()
				.update(CUSTOMER_FEE)
					.set(CUSTOMER_FEE.DOMAIN, f.getDomain())
					.set(CUSTOMER_FEE.PROJECT, f.getProjectId())
					.set(CUSTOMER_FEE.CUSTOMER, f.getClientId())
					.set(CUSTOMER_FEE.LINE, f.getLine())
					.set(CUSTOMER_FEE.ITEM, f.getItemId())
					.set(CUSTOMER_FEE.DESCRIPTION, f.getDescription())
					.set(CUSTOMER_FEE.QUANTITY, f.getQuantity())
					.set(CUSTOMER_FEE.PRICE, f.getPrice())
					.set(CUSTOMER_FEE.DISCOUNT_EXPR, f.getDiscountExpr())
					.set(CUSTOMER_FEE.INITIAL_DATE, new Date(f.getStartDate().getTime()))
					.set(CUSTOMER_FEE.FINAL_DATE, new  Date(f.getEndDate().getTime()))
					.set(CUSTOMER_FEE.BILLING_DATE, new  Date(f.getBillingDate().getTime()))
					.set(CUSTOMER_FEE.PERIOD, f.getPeriod())
					.set(CUSTOMER_FEE.SECURITY_LEVEL, f.getSecurityLevel())
					.set(CUSTOMER_FEE.INVOICING_GROUP, f.getBillingGroup())
					.set(CUSTOMER_FEE.SELLER, f.getSellerId())
					.set(CUSTOMER_FEE.WORKPLACE, f.getWorkplaceId())
					.where(CUSTOMER_FEE.ID.equal(f.getId()))
					.execute();
		});
	}

	public static void delete(AONContext ctx, Fee f) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			ctx.getDslContext()
				.delete(CUSTOMER_FEE)
				.where(CUSTOMER_FEE.ID.equal(f.getId())).execute();
		});
		
	}

	public static void delete(AONContext ctx, Stream<Fee> fs) {
		ctx.checkWrite();
		ctx.getDslContext().transaction(configuration -> {
			Vector<Integer> ids = new Vector<Integer>();
			fs.forEach(f ->{
				ids.add(f.getId());
			});
			ctx.getDslContext()
				.delete(CUSTOMER_FEE)
				.where(CUSTOMER_FEE.ID.in(ids)).execute();
		});
		
	}
	
	
	
	
}
