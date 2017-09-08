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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record22;
import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.Result;

import com.esferalia.aon.gwt.template.server.AuditInfo;
import com.esferalia.aon.gwt.template.server.ProductInfo;
import com.esferalia.aon.gwt.template.shared.Error;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.type.ProductType;

public class DBProduct {
	
	private static Item getItem(Domain domain, String login, Item item, Product product){
		LinkedList<Item> itemList = new LinkedList<Item>();
		if(item.getSerialNumber() != null)
			itemList = AON.getItemList(domain.getName(), domain.getId(), login, 
					f -> f.getProductProperty().eq(product.getId()).and(f.getSerialNumberProperty().eq(item.getSerialNumber())));
		else itemList = AON.getItemList(domain.getName(), domain.getId(), login, 
				f -> f.getProductProperty().eq(product.getId()).and(f.getSerialNumberProperty().isNull()));
		String details2 = "";
		String pack2 = "";
		
		if(item.getDetail()!= null) details2 =  details2 + item.getDetail();
		if(item.getDetail2()!= null) details2 =  details2 + item.getDetail2();
		if(item.getDetail3()!= null) details2 =  details2 + item.getDetail3();
		
		if(product.getPackaged()){
			if(item.getPackFormatTag().getId() != null) pack2 = pack2 + item.getPackFormatTag().getId();
			if(item.getPackUnits() != null) pack2 = pack2 + item.getPackUnits();
			if(item.getPackUnitsTag().getId() != null) pack2 = pack2 + item.getPackUnitsTag().getId();
			if(item.getPackMeasurement() != null) pack2 = pack2 + item.getPackMeasurement();
			if(item.getPackUnitsTag().getId() != null) pack2 = pack2 + item.getPackMeasurementTag().getId();
		}
		
		for (Item item2 : itemList) {
			String details = "";
			String pack = "";
			
			if(item2.getDetail()!= null) details =  details + item2.getDetail();
			if(item2.getDetail2()!= null) details =  details + item2.getDetail2();
			if(item2.getDetail3()!= null) details =  details + item2.getDetail3();
			
			if(product.getPackaged()){
				if(item2.getPackFormatTag().getId() != null) pack = pack + item2.getPackFormatTag().getId();
				if(item2.getPackUnits() != null) pack = pack + item2.getPackUnits();
				if(item2.getPackUnitsTag().getId() != null) pack = pack + item2.getPackUnitsTag().getId();
				if(item2.getPackMeasurement() != null) pack = pack + item2.getPackMeasurement();
				if(item2.getPackUnitsTag().getId() != null) pack = pack + item2.getPackMeasurementTag().getId();
			}
			if((item2.getBarcode() != null && item2.getBarcode().equals(item.getBarcode()))
					|| details.equals(details2) && pack.equals(pack2)){
				item2.setPurchasePrice(item.getPurchasePrice());
				item2.setPrice(item.getPrice());
				return item2;
			}
		}
		return new Item();
	}
	
	private static Product getProduct(Domain domain, String login, Product p, TemplateInfo ti){
		Product product = AON.getProduct(domain.getName(), domain.getId(), login, 
				f -> f.getCodeProperty().eq(p.getCode()).and(f.getDomainProperty().eq(domain.getId())));		
		
		if(product.getId() == null) return null;
		
		product.setName(p.getName());
		product.setCode(p.getCode());
		if(ti.getColumns().contains("Marca")) product.setBrand(p.getBrand());
		if(ti.getColumns().contains("Categor\u00eda")) product.setCategory(p.getCategory());
		if(ti.getColumns().contains("Inventariable") 
				|| ti.getColumns().contains("Inventariable")) product.setInventoriable(p.isInventoriable());
		if(ti.getColumns().contains("Estado")) product.setStatus(p.getStatus());
		if(ti.getColumns().contains("IVA")) product.setVat(p.getVat());
		if(ti.getColumns().contains("IRPF")) product.setRetention(p.getRetention());
		if(ti.getColumns().contains("Tipo")) product.setType(p.getType());
		if(ti.getColumns().contains("Producto Compuesto")) product.setComposition(p.isComposition());
		if(ti.getColumns().contains("Precio Composici\u00f3n")) product.setCompositionPrice(p.isCompositionPrice());
		if(ti.getColumns().contains("Serializable")) product.setSerializable(p.isSerializable());
		if(ti.getColumns().contains("Loteable")) product.setLotable(p.isLotable());
		if(ti.getColumns().contains("Envasado")) product.setPackaged(p.getPackaged());
		return product;	
	}
	
	private static Boolean hasProductParent(AONContext ctx, String code, Integer domain){
		
		Record1<Byte> heredity = ctx.getDslContext().select(DOMAIN.ENABLEHEREDITY)
				.from(DOMAIN)
				.where(DOMAIN.ID.eq(domain))
				.fetchOne();
		if(heredity.value1() == 1) return false;
		
		Integer count = ctx.getDslContext().selectCount()
				.from(PRODUCT).join(DOMAIN).on(PRODUCT.DOMAIN.eq(DOMAIN.PARENT))
				.where(DOMAIN.ID.eq(domain))
				.and(PRODUCT.CODE.eq(code))
				.fetchOne(0,int.class);
		return count > 0;
	}
	
	public static Error insertProducts2(Domain domain, Vector<ProductInfo> products, TemplateInfo templateInfo, AuditInfo ai, String login, String kind){
		long start = System.currentTimeMillis();
		Error error = new Error();
		error.setError(true);
		Vector<String> verror = new Vector<String>();
		verror.add("");
		error.setTextError(verror);
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), login);
			
			Vector<com.esferalia.aon.occam.api.model.product.Product> uproducts = new Vector<com.esferalia.aon.occam.api.model.product.Product>();
			Vector<com.esferalia.aon.occam.api.model.product.Product> iproducts = new Vector<com.esferalia.aon.occam.api.model.product.Product>();
			Vector<com.esferalia.aon.occam.api.model.product.ProductTag> uproductsTag = new Vector<com.esferalia.aon.occam.api.model.product.ProductTag>();
			Vector<com.esferalia.aon.occam.api.model.product.ProductTag> iproductsTag = new Vector<com.esferalia.aon.occam.api.model.product.ProductTag>();
			HashMap<String, LinkedList<com.esferalia.aon.occam.api.model.product.ProductTag>> iNewProductsTag = new HashMap<String, LinkedList<com.esferalia.aon.occam.api.model.product.ProductTag>>();
			Vector<com.esferalia.aon.occam.api.model.product.Item> iitems = new Vector<com.esferalia.aon.occam.api.model.product.Item>();
			Vector<com.esferalia.aon.occam.api.model.product.Item> uitems = new Vector<com.esferalia.aon.occam.api.model.product.Item>();

			AONContext sctx = ctx;
			
			products.stream().forEach(r->{	
				
				if(r.getProduct().isLotable() && !r.getProduct().isSerializable()){
					error.setError(false);
					verror.add("*Fila " + (r.getRow()+1)+": Para ser loteable tiene que ser serializable.");
					error.setTextError(verror);
				}
				com.esferalia.aon.occam.api.model.product.Product product  = getProduct(domain, login, r.getProduct(), templateInfo);	
				if(product != null){
					
					if(hasProductParent(sctx, product.getCode(), domain.getId())){
						error.setError(false);
						verror.add("*Fila " + (r.getRow()+1)+": Es un producto heredado.");
						error.setTextError(verror);
					}
					else if(!product.getKind().equals(0) && !product.getKind().equals(Byte.parseByte(kind)) ){
						error.setError(false);
						verror.add("*Fila " + (r.getRow()+1)+": Tipo de producto erroneo.");
						error.setTextError(verror);
					}
					else{
						r.getTagList().stream().forEach(tag->{
							ProductTag pt = AON.getProductTag(domain.getName(), domain.getId(), login, 
									f-> f.getProductProperty().eq(product.getId())
									.and(f.getTagProperty().eq(tag.getId())));

							pt.setDomain(r.getProduct().getDomain());
							pt.setProduct(product.getId());
							pt.setTag(tag);
							if(pt.getId() != null){
								uproductsTag.add(pt);
							} else iproductsTag.add(pt);
						});
						
						if(r.getItem() != null){	
							r.getItem().stream().forEach(i ->{
								com.esferalia.aon.occam.api.model.product.Item item = getItem(domain, login, i, product);
										//getItem(i, product.getId(), domain.getId(), sctx);
								if(item != null && item.getId() != null){
									if(item.getSerialNumber() != null &&  !product.isSerializable()){
										error.setError(false);
										verror.add("*Fila " + (r.getRow()+1)+": El producto no es serializable.");
										error.setTextError(verror);
									}
									if(!esta(item,uitems)){
										uitems.add(compare(i, item));
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
				}
				else{
					r.getTagList().stream().forEach(tag ->{
						String code = r.getProduct().getCode();
						if(iNewProductsTag.containsKey(code)){
							iNewProductsTag.get(code).add(new ProductTag().setTag(tag));
						} else {
							LinkedList<ProductTag> list = new LinkedList<ProductTag>();
							list.add(new ProductTag().setTag(tag));
							iNewProductsTag.put(code, list);
						}
					});
					
					
					com.esferalia.aon.occam.api.model.product.Product product2 = r.getProduct();
					product2.setKind(Byte.parseByte(kind));
					product2.setCreationUser(ai.getUsername());product2.setModificationUser(ai.getUsername());
					product2.setCreationDate(new Timestamp(ai.getDate().getTime()));product2.setModificationDate(new Timestamp(ai.getDate().getTime()));
					iproducts.add(product2);
				}
				
			});
			
			if(error.getError()){
				uproductsTag.stream().forEach(r -> {
					AON.updateProductTag(sctx, r);
				});
				if(iproductsTag.size() > 0) AON.insertProductTag(sctx, iproductsTag.stream());
			
				if(iproducts.size() > 0){ 
					LinkedList<Product> list = AON.insert(ctx, iproducts.stream());
					for (Product p : list) {
						LinkedList<ProductTag> l = iNewProductsTag.get(p.getCode()) != null
								? iNewProductsTag.get(p.getCode()) : new LinkedList<ProductTag>();
						for (ProductTag pt : l) {
							pt.setDomain(p.getDomain());
							pt.setProduct(p.getId());
						}
						AON.insertProductTag(sctx, l.stream());
					}
				}
				
				uitems.stream().forEach(r->{
					AON.updateItem(sctx, r);
				});
				uproducts.stream().forEach(r->{
					AON.update(sctx, r);
				});
				
				products.stream().filter(p -> p.getProduct().getId() == null).forEach(r->{	
					Integer productId = sctx.getDslContext().select(PRODUCT.ID).from(PRODUCT).where(PRODUCT.DOMAIN.eq(domain.getId())).and(PRODUCT.CODE.eq(r.getProduct().getCode())).fetchOne().value1();
					if(r.getItem() != null){	
						r.getItem().stream().forEach(i ->{
							i.setProductId(productId);
							i.setCreationUser(ai.getUsername());i.setModificationUser(ai.getUsername());
							i.setCreationDate(new Timestamp(ai.getDate().getTime()));i.setModificationDate(new Timestamp(ai.getDate().getTime()));
							iitems.add(i);	
						});
					}
				});
				if(iitems.size() > 0) AON.insertItem(ctx, iitems.stream());
			}
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
	
	public static ProductCategory getCategory(String domainName,Integer domainId, Integer id, String login) {
		return AON.getProductCategory(domainName, domainId, login, id);
	}
	public static ProductCategory getCategory(String domain,Integer domainId, String name, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId, login);
			
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
	
	public static  Vector<ProductCategory> getCategories(String domain, Integer domainId, String login)  {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId, login);
			
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
	
	public static  Vector<com.esferalia.aon.gwt.template.shared.ProductCategory> getCategoriesShared(String domain, Integer domainId, String login)  {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId, login);
			Result<Record2<Integer, String>> data = ctx.getDslContext().select(PCATEGORY.ID,PCATEGORY.NAME)
				.from(PCATEGORY)
				.where(PCATEGORY.DOMAIN.eq(domainId)).fetch();
			
			Result<Record2<Integer, String>> dataSon = ctx.getDslContext().select(PCATEGORY.ID,PCATEGORY.NAME)
					.from(PCATEGORY).join(DOMAIN).on(PCATEGORY.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.PARENT.eq(domainId)).fetch();
			
			Result<Record2<Integer, String>> dataParent = ctx.getDslContext().select(PCATEGORY.ID,PCATEGORY.NAME)
					.from(PCATEGORY).join(DOMAIN).on(PCATEGORY.DOMAIN.eq(DOMAIN.PARENT))
					.where(DOMAIN.ID.eq(domainId)).fetch();
			
			Vector<com.esferalia.aon.gwt.template.shared.ProductCategory> v = new Vector<com.esferalia.aon.gwt.template.shared.ProductCategory>();
			
			for(Record2<Integer, String> r : data){
				com.esferalia.aon.gwt.template.shared.ProductCategory pc = new com.esferalia.aon.gwt.template.shared.ProductCategory();
				pc.setId(r.value1());
				pc.setName(r.value2());
				v.add(pc);
			}
			for(Record2<Integer, String> r : dataSon){
				com.esferalia.aon.gwt.template.shared.ProductCategory pc = new com.esferalia.aon.gwt.template.shared.ProductCategory();
				pc.setId(r.value1());
				pc.setName(r.value2());
				v.add(pc);
			}
			for(Record2<Integer, String> r : dataParent){
				com.esferalia.aon.gwt.template.shared.ProductCategory pc = new com.esferalia.aon.gwt.template.shared.ProductCategory();
				pc.setId(r.value1());
				pc.setName(r.value2());
				v.add(pc);
			}
			return v;
			
		} finally {
			if (ctx != null) ctx.close();
		}
	}
		
	public static Vector<ProductInfo> getProducts(String domain,Integer domainId, Condition condition, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId, login);
			
			Result<Record22<String, String, Integer, Integer, Byte, Integer, Integer, Byte, Byte, Byte, Byte, Double, Double, String, String, String, String, String, Integer, String, Byte, Byte>>
				data =	ctx.getDslContext().selectDistinct(PRODUCT.CODE,PRODUCT.NAME,PRODUCT.CATEGORY
						,PRODUCT.BRAND,PRODUCT.TYPE,PRODUCT.VAT, PRODUCT.RETENTION,PRODUCT.INVENTORIABLE,PRODUCT.COMPOSITION
						,PRODUCT.COMPOSITION_PRICE,PRODUCT.STATUS,ITEM.PURCHASE_PRICE,ITEM.PRICE,ITEM.BARCODE,ITEM.DESCRIPTION
						,ITEM.DETAIL,ITEM.DETAIL2,ITEM.DETAIL3,PRODUCT.ID, ITEM.SERIAL_NUMBER, PRODUCT.SERIALIZABLE, PRODUCT.LOTABLE)
						.from(PRODUCT).join(ITEM).on(PRODUCT.ID.eq(ITEM.PRODUCT))
						.leftOuterJoin(PRODUCT_TAG).on(PRODUCT.ID.eq(PRODUCT_TAG.PRODUCT))
						.where(condition)
						.orderBy(PRODUCT.NAME)
						.fetch();
			
			Vector<ProductInfo> v = new Vector<ProductInfo>();
			
			for(Record22<String, String, Integer, Integer, Byte, Integer, Integer, Byte, Byte, Byte, Byte, Double, Double, String, String, String, String, String, Integer, String, Byte, Byte> r : data){
				ProductInfo pi = new ProductInfo();
				Item i = new Item();
				Product p = new Product();
				i.setCode(r.value1());p.setCode(r.getValue(PRODUCT.CODE));
				i.setName(r.value2());p.setName(r.getValue(PRODUCT.NAME));
				if(r.value3()!=null){
					ProductCategory c = getCategory(domain,domainId, r.value3(), login);
					i.setCategory(c.getName());p.setCategory(c.getId());
				}
				else i.setCategory("");
				
				if(r.value4()!=null){
					Brand brand = getBrand(domain,domainId, r.value4(), login);
					i.setBrand(brand.getName());p.setBrand(brand.getId());
				}
				else i.setBrand("");
				
				if(r.value5()!=null){
					i.setType(com.esferalia.aon.occam.api.model.type.ProductType.values()[r.value5()]);
					p.setType(ProductType.values()[r.getValue(PRODUCT.TYPE)].value());
				}
				if(r.value6()!=null){
					com.esferalia.aon.occam.api.model.product.Tax vat = getTax(domain,domainId, r.value6(), login);
					i.setVat(vat);p.setVat(vat.getId());
				}
				else{
					com.esferalia.aon.occam.api.model.product.Tax t = new com.esferalia.aon.occam.api.model.product.Tax();
					t.setName("");
					i.setVat(t);
				}
				if(r.value7()!=null){
					com.esferalia.aon.occam.api.model.product.Tax retention = getTax(domain,domainId, r.value7(), login);
					i.setRetention(retention);p.setRetention(retention.getId());
				}
				else{
					com.esferalia.aon.occam.api.model.product.Tax t = new com.esferalia.aon.occam.api.model.product.Tax();
					t.setName("");
					i.setRetention(t);
				}
				if(r.value8()!=null){
					i.setInventoriable(r.value8()==1);
					p.setInventoriable(r.getValue(PRODUCT.INVENTORIABLE));
				}
				if(r.value9()!=null) {
					i.setComposition(r.value9()==1);
					p.setComposition(r.getValue(PRODUCT.COMPOSITION));
				}
				if(r.value10()!=null){
					i.setCompositionPrice(r.value10()==1);
					p.setCompositionPrice(r.getValue(PRODUCT.COMPOSITION_PRICE));
				}
				if(r.value11()!=null){
					i.setStatus(r.value11());
					p.setStatus(r.getValue(PRODUCT.STATUS));
				}
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
				if(r.getValue(ITEM.SERIAL_NUMBER) != null) i.setSerialNumber(r.getValue(ITEM.SERIAL_NUMBER));
				if(r.getValue(PRODUCT.SERIALIZABLE) != null) p.setSerializable(r.getValue(PRODUCT.SERIALIZABLE));
				if(r.getValue(PRODUCT.LOTABLE) != null) p.setLotable(r.getValue(PRODUCT.LOTABLE));
				pi.setProduct(p);
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
	
	public static  Vector<ProductTag> getTags(String domain, Integer domainId, String login){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId, login);
			
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
	
	public static  Vector<Brand> getBrands(String domain, Integer domainId, String login) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId, login);
			
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
	
	public static Brand getBrand(String domainName,Integer domainId, Integer id, String login) {
		return AON.getBrand(domainName, domainId, login, id);
	}
	
	public static Brand getBrand(String domainName,Integer domainId, String name, String login) {
		return AON.getBrand(domainName, domainId, login, name);	
	}
	
	public static Tax getTax(String domainName, Integer domainId, Integer id, String login) {
		return AON.getTax(domainName, domainId, login, id);
	}
	
	public static Vector<Tax> getRetentions(String domainName, Integer domainId, String login)  {
		Vector<Tax> v = new Vector<Tax>();
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			
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
	
	public static Vector<Tax> getIVA(String domain, Integer domainId, String login) {
		Vector<Tax> v = new Vector<Tax>();
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId, login);
			
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
	
	public static Tax getIVAName(String domainName, Integer domainId, String name, String login) {
		Domain domain = AON.getDomain(domainName, domainId, login);
		Tax tax = AON.getTax(domainName, domainId, login, f -> f.getDomainProperty().eq(domainId)
				.and(f.getTaxTypeProperty().eq((byte)1)).and(f.getNameProperty().eq(name)));
		if(tax.getId() == null && domain.isEnableHeredity())
			return AON.getTax(domainName, domainId, login, f -> f.getDomainProperty().eq(domain.getParentId())
				.and(f.getTaxTypeProperty().eq((byte)1)).and(f.getNameProperty().eq(name)));
		return tax;
	}
	
	public static Item compare(Item i,Item item){
		item.setBarcode(i.getBarcode() != null ? i.getBarcode() : item.getBarcode());
		item.setDescription(i.getDescription() != null ? i.getDescription() : item.getDescription());
		item.setCategory(i.getCategory() != null ? i.getCategory() : item.getCategory());
		//item.setPrice(i.getPrice() != null ? i.getPrice() : item.getPrice());
		item.setStatus(i.getStatus() != null ? i.getStatus() : item.getStatus());
		//item.setExpensesPercent(i.getExpensesPercent() != null ? i.getExpensesPercent() : item.getExpensesPercent());
		//item.setExpensesFixed(i.getExpensesFixed() != null ? i.getExpensesFixed() : item.getExpensesFixed());
		//item.setProfitPercent(i.getProfitPercent() != null ? i.getProfitPercent() : item.getProfitPercent());
		//item.setPurchasePrice(i.getPurchasePrice() != null ? i.getPurchasePrice() : item.getPurchasePrice());
		item.setPackFormatTag(i.getPackFormatTag() != null ? i.getPackFormatTag() : item.getPackFormatTag());
		item.setPackUnits(i.getPackUnits() != null ? i.getPackUnits() : item.getPackUnits());
		item.setPackUnitsTag(i.getPackUnitsTag() !=  null ? i.getPackUnitsTag() : item.getPackUnitsTag());
		item.setPackMeasurement(i.getPackMeasurement() != null ? i.getPackMeasurement() : item.getPackMeasurement());
		item.setPackMeasurementTag(i.getPackMeasurementTag() != null ? i.getPackMeasurementTag() : item.getPackMeasurementTag());
		item.setStockUnitTag(i.getStockUnitTag() != null ? i.getStockUnitTag() : item.getStockUnitTag());
		return item;
	}
}
