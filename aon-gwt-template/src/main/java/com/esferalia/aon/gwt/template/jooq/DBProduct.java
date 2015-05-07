package com.esferalia.aon.gwt.template.jooq;

import static com.esferalia.aon.jooq.tables.Brand.BRAND;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.ProductTag.PRODUCT_TAG;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.Tax.TAX;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record12;
import org.jooq.Record19;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.Record7;
import org.jooq.Result;

import com.code.aon.config.Tag;
import com.code.aon.config.Tax;
import com.code.aon.product.Brand;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.ProductTag;
import com.esferalia.aon.gwt.template.server.AuditInfo;
import com.esferalia.aon.gwt.template.server.ProductInfo;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.product.Item;

public class DBProduct {

	private static com.esferalia.aon.occam.api.model.product.Item getItem(com.esferalia.aon.occam.api.model.product.Item i,Integer productId, Integer domainId, AONContext ctx){
		Result<Record7<Integer, String, String, String, String, String, Timestamp>> data = ctx.getDslContext().select(ITEM.ID,ITEM.BARCODE,ITEM.DETAIL,ITEM.DETAIL2,ITEM.DETAIL3,ITEM.CREATION_USER,ITEM.CREATION_DATE)
				.from(ITEM)
				.where(ITEM.PRODUCT.eq(productId)).fetch();
		
		String barcode = null;
		Integer itemId = null;
		String details = null;
		String details2 = "";
		if(i.getDetail()!= null) details2 =  details2 + i.getDetail();
		if(i.getDetail2()!= null) details2 =  details2 + i.getDetail2();
		if(i.getDetail3()!= null) details2 =  details2 + i.getDetail3();
		for(Record7<Integer, String, String, String, String, String, Timestamp> i2 : data){
			itemId =  i2.value1();
			if(i2.value2()!= null) barcode = i2.value2();
			details = "";
			if(i2.value3()!= null) details =  details + i2.value3();
			if(i2.value4()!= null) details =  details + i2.value4();
			if(i2.value5()!= null) details =  details + i2.value5();
				
			if((barcode != null && barcode.equals(i.getBarcode())) || (details!= null && details.equals(details2))){
				i.setId(itemId);
				i.setProductId(productId);
				i.setCreationDate(i2.value7());
				i.setCreationUser(i2.value6());
				return i;
			}
		}
		return null;
	}
	
	private static com.esferalia.aon.occam.api.model.product.Product getProduct(com.esferalia.aon.occam.api.model.product.Product p, TemplateInfo ti, Integer domainId, AONContext ctx){
		Record12<Integer, Integer, Integer, Byte, Byte, Integer, Integer, Byte, Byte, Byte, String, Timestamp> data = ctx.getDslContext().select(PRODUCT.ID, PRODUCT.BRAND, PRODUCT.CATEGORY,PRODUCT.INVENTORIABLE,PRODUCT.STATUS, PRODUCT.VAT, PRODUCT.RETENTION,PRODUCT.TYPE, PRODUCT.COMPOSITION, PRODUCT.COMPOSITION_PRICE, PRODUCT.CREATION_USER, PRODUCT.CREATION_DATE)
				.from(PRODUCT)
				.where(PRODUCT.CODE.eq(p.getCode()).and(PRODUCT.DOMAIN.eq(domainId))).fetchOne();
		if(data != null){
			com.esferalia.aon.occam.api.model.product.Product product = new com.esferalia.aon.occam.api.model.product.Product();
			
			product.setId(data.value1());
			product.setDomain(domainId);
			product.setName(p.getName());
			product.setCode(p.getCode());
		
			if(ti.getColumns().contains("Marca")) product.setBrand(p.getBrand());
			else if(data.value2()!= null) product.setBrand(data.value2());
			else product.setBrand(null);
			
			if(ti.getColumns().contains("Categor\u00eda")) product.setCategory(p.getCategory());
			else if(data.value3() != null) product.setCategory(data.value3());
			else product.setCategory(null);
		
			if(ti.getColumns().contains("Inventoriable")) product.setInventoriable(p.isInventoriable());
			else product.setInventoriable(data.value4()==1);
	
			if(ti.getColumns().contains("Estado")) product.setStatus(p.getStatus());
			else product.setStatus(data.value5());
		
			if(ti.getColumns().contains("IVA")) product.setVat(p.getVat());
			else product.setVat(data.value6());
		
			if(ti.getColumns().contains("IRPF")) product.setRetention(p.getRetention());
			else product.setRetention(data.value7()); 
			
			if(ti.getColumns().contains("Tipo")) product.setType(p.getType());
			else product.setType(data.value8());

			if(ti.getColumns().contains("Producto Compuesto")) product.setComposition(p.isComposition());
			else product.setComposition(data.value9() ==1);
		
			if(ti.getColumns().contains("Precio Composici\u00f3n")) product.setCompositionPrice(p.isCompositionPrice());
			else product.setCompositionPrice(data.value10() ==1);		 
			product.setCreationUser(data.value11());
			product.setCreationDate(data.value12());
			return product;	
		}
		return null;
	}
	
	public static Error insertProducts2(String domain, Integer domainId,Vector<ProductInfo> products, TemplateInfo templateInfo, AuditInfo ai){
		long start = System.currentTimeMillis();
		Error error = new Error();
		error.setError(true);
		Vector<String> verror = new Vector<String>();
		verror.add("");
		error.setTextError(verror);
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Vector<com.esferalia.aon.occam.api.model.product.Product> uproducts = new Vector<com.esferalia.aon.occam.api.model.product.Product>();
			Vector<com.esferalia.aon.occam.api.model.product.Product> iproducts = new Vector<com.esferalia.aon.occam.api.model.product.Product>();
			Vector<com.esferalia.aon.occam.api.model.product.ProductTag> iproductsTag = new Vector<com.esferalia.aon.occam.api.model.product.ProductTag>();
			Vector<com.esferalia.aon.occam.api.model.product.Item> iitems = new Vector<com.esferalia.aon.occam.api.model.product.Item>();
			Vector<com.esferalia.aon.occam.api.model.product.Item> uitems = new Vector<com.esferalia.aon.occam.api.model.product.Item>();

			AONContext sctx = ctx;
			ctx.deactivateForeignKeys();
			products.stream().forEach(r->{	
				
				com.esferalia.aon.occam.api.model.product.Product product  = getProduct(r.getProduct(), templateInfo, domainId, sctx);	
				if(product != null){
					if(r.getProductTag() != null){	
						iproductsTag.addAll(r.getProductTag());
					}
					if(r.getItem() != null){	
						r.getItem().stream().forEach(i ->{
							com.esferalia.aon.occam.api.model.product.Item item = getItem(i, product.getId(), domainId, sctx);
							if(item != null){
								if(!esta(item,uitems)){
									uitems.add(item);	
								}
								else{
									error.setError(false);
									verror.add("*Fila " + (r.getRow()+1)+": El producto está repetido.");
									error.setTextError(verror);
								}
							}
							else{
								i.setProductId(product.getId());
								i.setCreationUser(ai.getUsername());i.setModificationUser(ai.getUsername());
								i.setCreationDate(new Timestamp(ai.getDate().getTime()));i.setModificationDate(new Timestamp(ai.getDate().getTime()));
								iitems.add(i);	
							}
						});
					}
					product.setModificationDate(new Timestamp(ai.getDate().getTime()));
					product.setModificationUser(ai.getUsername());
					uproducts.add(product);
					r.getProduct().setId(product.getId());
				}
				else{
					com.esferalia.aon.occam.api.model.product.Product product2 = r.getProduct();
					product2.setCreationUser(ai.getUsername());product2.setModificationUser(ai.getUsername());
					product2.setCreationDate(new Timestamp(ai.getDate().getTime()));product2.setModificationDate(new Timestamp(ai.getDate().getTime()));
					iproducts.add(product2);
				}
			});
			AON.deleteProductTag(ctx, iproductsTag.stream());
			AON.deleteItem(ctx, uitems.stream());
			AON.delete(ctx, uproducts.stream());
			AON.insertWithId(ctx, uproducts.stream());
			AON.insert(ctx, iproducts.stream());
			AON.insertItemWithId(ctx, uitems.stream());
			
			products.stream().filter(p -> p.getProduct().getId() == null).forEach(r->{	
				Integer productId = sctx.getDslContext().select(PRODUCT.ID).from(PRODUCT).where(PRODUCT.DOMAIN.eq(domainId)).and(PRODUCT.CODE.eq(r.getProduct().getCode())).fetchOne().value1();
				System.out.println(r.getProduct().getId());
				if(r.getProductTag() != null){	
					r.getProductTag().stream().forEach(pt ->{
						pt.setProduct(productId);
						iproductsTag.add(pt);	
					});
				}
				if(r.getItem() != null){	
					r.getItem().stream().forEach(i ->{
						i.setProductId(productId);
						i.setCreationUser(ai.getUsername());i.setModificationUser(ai.getUsername());
						i.setCreationDate(new Timestamp(ai.getDate().getTime()));i.setModificationDate(new Timestamp(ai.getDate().getTime()));
						iitems.add(i);	
					});
				}
			});
			AON.insertProductTag(ctx, iproductsTag.stream());
			AON.insertItem(ctx, iitems.stream());
			ctx.activateForeignKeys();

		} finally {
			if (ctx != null) ctx.close();	
		}
		long time = System.currentTimeMillis() - start;
		System.out.println("time: " + (time/1000d));
		return error;
	}
	
	private static Boolean esta(com.esferalia.aon.occam.api.model.product.Item code, Vector<com.esferalia.aon.occam.api.model.product.Item> vector) {
		for (com.esferalia.aon.occam.api.model.product.Item item : vector) {
			if(item.getId().equals(code.getId())) return true;
		}
		return false;
	}
	
	private static Boolean esta(String code, Vector<String> vector) {
		for (String string : vector) {
			if(string.equals(code)) return true;
		}
		return false;
	}
	
	private static Boolean esta(Integer id, Vector<Integer> vector) {
		for (Integer integer : vector) {
			if(integer.equals(id)) return true;
		}
		return false;
	}
	
	public static ProductCategory getCategory(String domain,Integer domainId, Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record5<Integer, String, String, String, String>> data = ctx.getDslContext().select(PCATEGORY.ID,PCATEGORY.NAME,PCATEGORY.DETAIL,PCATEGORY.DETAIL2,PCATEGORY.DETAIL3)
				.from(PCATEGORY)
				.where(PCATEGORY.ID.eq(id)).fetch();
			
			ProductCategory c = new ProductCategory();
			c.setId(data.get(0).value1());
			c.setName(data.get(0).value2());
			if(data.get(0).value3() != null) c.setDetail(data.get(0).value3());
			if(data.get(0).value4() != null) c.setDetail2(data.get(0).value4());
			if(data.get(0).value5() != null) c.setDetail3(data.get(0).value5());
			return c;
		} finally {
			if (ctx != null) ctx.close();
		}	
	}
	public static ProductCategory getCategory(String domain,Integer domainId, String name) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record5<Integer, String, String, String, String>> data = ctx.getDslContext().select(PCATEGORY.ID,PCATEGORY.NAME,PCATEGORY.DETAIL,PCATEGORY.DETAIL2,PCATEGORY.DETAIL3)
				.from(PCATEGORY)
				.where(PCATEGORY.NAME.eq(name)).fetch();
			
			ProductCategory c = new ProductCategory();
			c.setId(data.get(0).value1());
			c.setName(data.get(0).value2());
			if(data.get(0).value3() != null) c.setDetail(data.get(0).value3());
			if(data.get(0).value4() != null) c.setDetail2(data.get(0).value4());
			if(data.get(0).value5() != null) c.setDetail3(data.get(0).value5());
			return c;
		} finally {
			if (ctx != null) ctx.close();
		}	
	}
	
	public static  Vector<ProductCategory> getCategories(String domain, Integer domainId)  {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record5<Integer, String, String, String, String>> data = ctx.getDslContext().select(PCATEGORY.ID,PCATEGORY.NAME,PCATEGORY.DETAIL,PCATEGORY.DETAIL2,PCATEGORY.DETAIL3)
				.from(PCATEGORY)
				.where(PCATEGORY.DOMAIN.eq(domainId)).fetch();
			
			Result<Record5<Integer, String, String, String, String>> dataSon = ctx.getDslContext().select(PCATEGORY.ID,PCATEGORY.NAME,PCATEGORY.DETAIL,PCATEGORY.DETAIL2,PCATEGORY.DETAIL3)
					.from(PCATEGORY).join(DOMAIN).on(PCATEGORY.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.PARENT.eq(domainId)).fetch();
			
			Result<Record5<Integer, String, String, String, String>> dataParent = ctx.getDslContext().select(PCATEGORY.ID,PCATEGORY.NAME,PCATEGORY.DETAIL,PCATEGORY.DETAIL2,PCATEGORY.DETAIL3)
					.from(PCATEGORY).join(DOMAIN).on(PCATEGORY.DOMAIN.eq(DOMAIN.PARENT))
					.where(DOMAIN.ID.eq(domainId)).fetch();
			
			Vector<ProductCategory> v = new Vector<ProductCategory>();
			
			for(Record5<Integer, String, String, String, String> r : data){
				ProductCategory pc = new ProductCategory();
				pc.setId(r.value1());
				pc.setName(r.value2());
				if(r.value3() != null) pc.setDetail(r.value3());
				if(r.value4() != null) pc.setDetail2(r.value4());
				if(r.value5() != null) pc.setDetail3(r.value5());
				v.add(pc);
			}
			for(Record5<Integer, String, String, String, String> r : dataSon){
				ProductCategory pc = new ProductCategory();
				pc.setId(r.value1());
				pc.setName(r.value2());
				if(r.value3() != null) pc.setDetail(r.value3());
				if(r.value4() != null) pc.setDetail2(r.value4());
				if(r.value5() != null) pc.setDetail3(r.value5());
				v.add(pc);
			}
			for(Record5<Integer, String, String, String, String> r : dataParent){
				ProductCategory pc = new ProductCategory();
				pc.setId(r.value1());
				pc.setName(r.value2());
				if(r.value3() != null) pc.setDetail(r.value3());
				if(r.value4() != null) pc.setDetail2(r.value4());
				if(r.value5() != null) pc.setDetail3(r.value5());
				v.add(pc);
			}
			return v;
			
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Vector<ProductInfo> getProducts(String domain,Integer domainId, Condition condition) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record19<String, String, Integer, Integer, Byte, Integer, Integer, Byte, Byte, Byte, Byte, Double, Double, String, String, String, String, String, Integer>>
				data =	ctx.getDslContext().select(PRODUCT.CODE,PRODUCT.NAME,PRODUCT.CATEGORY
						,PRODUCT.BRAND,PRODUCT.TYPE,PRODUCT.VAT, PRODUCT.RETENTION,PRODUCT.INVENTORIABLE,PRODUCT.COMPOSITION
						,PRODUCT.COMPOSITION_PRICE,PRODUCT.STATUS,ITEM.PURCHASE_PRICE,ITEM.PRICE,ITEM.BARCODE,ITEM.DESCRIPTION
						,ITEM.DETAIL,ITEM.DETAIL2,ITEM.DETAIL3,PRODUCT.ID)
						.from(PRODUCT).join(ITEM).on(PRODUCT.ID.eq(ITEM.PRODUCT))
						.where(condition)
						.orderBy(PRODUCT.NAME)
						.fetch();
			
			Vector<ProductInfo> v = new Vector<ProductInfo>();
			
			for(Record19<String, String, Integer, Integer, Byte, Integer, Integer, Byte, Byte, Byte, Byte, Double, Double, String, String, String, String, String, Integer> r : data){
				ProductInfo pi = new ProductInfo();
				Item i = new Item();
				i.setCode(r.value1());
				i.setName(r.value2());
				if(r.value3()!=null){
					ProductCategory c = getCategory(domain,domainId, r.value3());
					i.setCategory(c.getName());
				}
				else i.setCategory("");
				
				if(r.value4()!=null){
					Brand brand = getBrand(domain,domainId, r.value4());
					i.setBrand(brand.getName());
				}
				else i.setBrand("");
				
				if(r.value5()!=null){
					i.setType(com.esferalia.aon.occam.api.model.type.ProductType.values()[r.value5()]);
				}
				if(r.value6()!=null){
					com.esferalia.aon.occam.api.model.product.Tax vat = getTax(domain,domainId, r.value6());
					i.setVat(vat);
				}
				else{
					com.esferalia.aon.occam.api.model.product.Tax t = new com.esferalia.aon.occam.api.model.product.Tax();
					t.setName("");
					i.setVat(t);
				}
				if(r.value7()!=null){
					com.esferalia.aon.occam.api.model.product.Tax retention = getTax(domain,domainId, r.value7());
					i.setRetention(retention);
				}
				else{
					com.esferalia.aon.occam.api.model.product.Tax t = new com.esferalia.aon.occam.api.model.product.Tax();
					t.setName("");
					i.setRetention(t);
				}
				if(r.value8()!=null) i.setInventoriable(r.value8()==1);
				if(r.value9()!=null) i.setComposition(r.value9()==1);
				if(r.value10()!=null) i.setCompositionPrice(r.value10()==1);
				if(r.value11()!=null) i.setStatus(r.value11());
				if(r.value12()!=null) i.setPurchasePrice(r.value12());
				if(r.value13()!=null) i.setPrice(r.value13());
				if(r.value14()!=null) i.setBarcode(r.value14());
				else i.setBarcode("");
				if(r.value15()!=null) i.setDescription(r.value15());
				else i.setDescription("");
				if(r.value16()!=null) i.setDetail(r.value16());
				else i.setDetail("");
				if(r.value17()!=null) i.setDetail2(r.value17());
				else i.setDetail2("");
				if(r.value18()!=null) i.setDetail3(r.value18());
				else i.setDetail3("");
				Set<ProductTag> tags = getTags(ctx.getDslContext(), r.value19());
				pi.setTags(tags);
				pi.setDownloadItem(i);
				v.add(pi);
			}	
			return v;

		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Set<ProductTag> getTags(DSLContext dslContext, Integer id ){
		
		Result<Record1<String>> data = dslContext.select(TAG.NAME)
			.from(TAG).join(PRODUCT_TAG).on(TAG.ID.eq(PRODUCT_TAG.TAG))
			.where(PRODUCT_TAG.PRODUCT.eq(id)).fetch();
		
		Set<ProductTag> s = new HashSet<ProductTag>();
		
		for(Record1<String> r : data ){
			ProductTag pt = new ProductTag();
			Tag t = new Tag();
			t.setName(r.value1());
			pt.setTag(t);
			s.add(pt);
		}		
			
		return s;
		
	}
	
	public static  Vector<ProductTag> getTags(String domain, Integer domainId){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record2< Integer, String>> data = ctx.getDslContext().select(TAG.ID,TAG.NAME)
				.from(TAG)
				.where(TAG.DOMAIN.eq(domainId)).fetch();
		
			Result<Record2< Integer, String>> dataSon = ctx.getDslContext().select(TAG.ID,TAG.NAME)
					.from(TAG).join(DOMAIN).on(TAG.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.PARENT.eq(domainId)).fetch();
			
			Result<Record2< Integer, String>> dataParent = ctx.getDslContext().select(TAG.ID,TAG.NAME)
					.from(TAG).join(DOMAIN).on(TAG.DOMAIN.eq(DOMAIN.PARENT))
					.where(DOMAIN.ID.eq(domainId)).fetch();
			
			Vector<ProductTag> v = new Vector<ProductTag>();
			
			for(Record2<Integer, String> r : data){
				ProductTag tag = new ProductTag();
				Tag tag1 = new Tag();
				tag1.setId(r.value1());
				tag1.setName(r.value2());
				tag.setTag(tag1);
				v.add(tag);
			}
			for(Record2<Integer, String> r : dataSon){
				ProductTag tag = new ProductTag();
				Tag tag1 = new Tag();
				tag1.setId(r.value1());
				tag1.setName(r.value2());
				tag.setTag(tag1);
				v.add(tag);
			}
			for(Record2<Integer, String> r : dataParent){
				ProductTag tag = new ProductTag();
				Tag tag1 = new Tag();
				tag1.setId(r.value1());
				tag1.setName(r.value2());
				tag.setTag(tag1);
				v.add(tag);
			}
			
			return v;
			
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static  Vector<Brand> getBrands(String domain, Integer domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record2<Integer, String>> data = ctx.getDslContext().select(BRAND.ID,BRAND.NAME)
				.from(BRAND)
				.where(BRAND.DOMAIN.eq(domainId)).fetch();
			
			Result<Record2<Integer, String>> dataSon = ctx.getDslContext().select(BRAND.ID,BRAND.NAME)
					.from(BRAND).join(DOMAIN).on(BRAND.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.PARENT.eq(domainId)).fetch();
			
			Result<Record2<Integer, String>> dataParent = ctx.getDslContext().select(BRAND.ID,BRAND.NAME)
					.from(BRAND).join(DOMAIN).on(BRAND.DOMAIN.eq(DOMAIN.PARENT))
					.where(DOMAIN.ID.eq(domainId)).fetch();
			
			Vector<Brand> v = new Vector<Brand>();
			
			for(Record2<Integer, String> r : data){
				Brand brand = new Brand();
				brand.setId(r.value1());
				brand.setName(r.value2());
				
				v.add(brand);
			}

			for(Record2<Integer, String> r : dataSon){
				Brand brand = new Brand();
				brand.setId(r.value1());
				brand.setName(r.value2());
				
				v.add(brand);
			}

			for(Record2<Integer, String> r : dataParent){
				Brand brand = new Brand();
				brand.setId(r.value1());
				brand.setName(r.value2());
				
				v.add(brand);
			}
			return v;
			
		} finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Brand getBrand(String domain,Integer domainId, Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record2<Integer, String>> data = ctx.getDslContext().select(BRAND.ID,BRAND.NAME)
				.from(BRAND)
				.where(BRAND.ID.eq(id)).fetch();
			
			Brand brand = new Brand();
			brand.setId(data.get(0).value1());
			brand.setName(data.get(0).value2());
			
			return brand;
		} finally {
			if (ctx != null) ctx.close();
		}	
	}
	
	public static Brand getBrand(String domain,Integer domainId, String name) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record2<Integer, String>> data = ctx.getDslContext().select(BRAND.ID,BRAND.NAME)
				.from(BRAND)
				.where(BRAND.NAME.eq(name)).fetch();
			
			Brand brand = new Brand();
			brand.setId(data.get(0).value1());
			brand.setName(data.get(0).value2());
			
			return brand;
		} finally {
			if (ctx != null) ctx.close();
		}	
	}
	
	public static com.esferalia.aon.occam.api.model.product.Tax getTax(String domain, Integer domainId, Integer id) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record3<Integer,String,Double>>  data = ctx.getDslContext().select(TAX.ID,TAX.NAME,TAX.PERCENTAGE)
				.from(TAX)
				.where(TAX.ID.eq(id)).fetch();
			
			com.esferalia.aon.occam.api.model.product.Tax t = new com.esferalia.aon.occam.api.model.product.Tax();
			t.setId(id);
			if(data.get(0).value2()!=null)t.setName(data.get(0).value2());
			else t.setName("");
			if(data.get(0).value3()!=null)t.setPercentage(data.get(0).value3());
			return t;
		} finally {
			if (ctx != null) ctx.close();
		}	
	}
	
	public static Vector<Tax> getRetentions(String domain, Integer domainId)  {
		Vector<Tax> v = new Vector<Tax>();
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record3<Integer,String,Double>> data = ctx.getDslContext().select(TAX.ID,TAX.NAME,TAX.PERCENTAGE)
					.from(TAX)
					.where(TAX.DOMAIN.eq(domainId).and(TAX.TAX_TYPE.eq((byte)2))).fetch();
			
			Result<Record3<Integer,String,Double>> dataSon = ctx.getDslContext().select(TAX.ID,TAX.NAME,TAX.PERCENTAGE)
					.from(TAX).join(DOMAIN).on(TAX.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.PARENT.eq(domainId).and(TAX.TAX_TYPE.eq((byte)2))).fetch();
			
			Result<Record3<Integer,String,Double>> dataParent = ctx.getDslContext().select(TAX.ID,TAX.NAME,TAX.PERCENTAGE)
					.from(TAX).join(DOMAIN).on(TAX.DOMAIN.eq(DOMAIN.PARENT))
					.where(DOMAIN.ID.eq(domainId).and(TAX.TAX_TYPE.eq((byte)2))).fetch();
			
			data.stream().forEach(r -> {
				Tax tax = new Tax();
				tax.setId(r.value1());
				tax.setName(r.value2());
				tax.setPercentage(r.value3());
				v.add(tax);
			});
			
			dataSon.stream().forEach(r -> {
				Tax tax = new Tax();
				tax.setId(r.value1());
				tax.setName(r.value2());
				tax.setPercentage(r.value3());
				v.add(tax);
			});
			
			dataParent.stream().forEach(r -> {
				Tax tax = new Tax();
				tax.setId(r.value1());
				tax.setName(r.value2());
				tax.setPercentage(r.value3());
				v.add(tax);
			});
			return v;			
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Vector<Tax> getIVA(String domain, Integer domainId) {
		Vector<Tax> v = new Vector<Tax>();
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Result<Record3<Integer,String,Double>> data = ctx.getDslContext().select(TAX.ID,TAX.NAME,TAX.PERCENTAGE)
					.from(TAX)
					.where(TAX.DOMAIN.eq(domainId).and(TAX.TAX_TYPE.eq((byte)1))).fetch();
			
			Result<Record3<Integer,String,Double>> dataSon = ctx.getDslContext().select(TAX.ID,TAX.NAME,TAX.PERCENTAGE)
					.from(TAX).join(DOMAIN).on(TAX.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.PARENT.eq(domainId).and(TAX.TAX_TYPE.eq((byte)1))).fetch();
			
			Result<Record3<Integer,String,Double>> dataParent = ctx.getDslContext().select(TAX.ID,TAX.NAME,TAX.PERCENTAGE)
					.from(TAX).join(DOMAIN).on(TAX.DOMAIN.eq(DOMAIN.PARENT))
					.where(DOMAIN.ID.eq(domainId).and(TAX.TAX_TYPE.eq((byte)1))).fetch();
			
			data.stream().forEach(r -> {
				Tax tax = new Tax();
				tax.setId(r.value1());
				tax.setName(r.value2());
				tax.setPercentage(r.value3());
				v.add(tax);
			});
			
			dataSon.stream().forEach(r -> {
				Tax tax = new Tax();
				tax.setId(r.value1());
				tax.setName(r.value2());
				tax.setPercentage(r.value3());
				v.add(tax);
			});
			
			dataParent.stream().forEach(r -> {
				Tax tax = new Tax();
				tax.setId(r.value1());
				tax.setName(r.value2());
				tax.setPercentage(r.value3());
				v.add(tax);
			});
			return v;			
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public static Tax getIVAName(String domain, Integer domainId, String name) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Record3<Integer, String, Double> data = ctx.getDslContext().select(TAX.ID,TAX.NAME,TAX.PERCENTAGE)
					.from(TAX)
					.where(TAX.DOMAIN.eq(domainId).and(TAX.TAX_TYPE.eq((byte)1)))
					.and(TAX.NAME.eq(name))
					.fetchOne();
			

				Tax tax = new Tax();
				tax.setId(data.value1());
				tax.setName(data.value2());
				tax.setPercentage(data.value3());

			return tax;			
		}finally {
			if (ctx != null) ctx.close();
		}
	}
}
