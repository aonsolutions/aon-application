package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductKind;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.type.ProductType;

public class ProductJSON {

	private ProductJSON() {
		
	}
	
	public static List<Product> fromJSON(JSONArray json) {
		LinkedList<Product> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Product fromJSON(JSONObject json) {
		if(json == null || json.isEmpty()) return null;
		return new Product()
				.setId(JsonUtils.getInteger(json, IJsonNames.ID))
				.setDomain(DomainJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.DOMAIN)))
				.setCode(JsonUtils.getString(json, IJsonNames.CODE))
				.setName(JsonUtils.getString(json, IJsonNames.NAME))
				.setBrand(BrandJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.BRAND)))
				.setCategory(ProductCategoryJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.CATEGORY)))
				.setType(ProductType.safeValueOf(JsonUtils.getString(json, IJsonNames.TYPE)))
				.setKind(ProductKind.safeValueOf(JsonUtils.getString(json, IJsonNames.KIND)))
				.setStatus(ProductStatus.safeValueOf(JsonUtils.getString(json, IJsonNames.STATUS)))
				.setVat(TaxJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.VAT)))
				.setRetention(TaxJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.RETENTION)))
				.setInventoriable(JsonUtils.getboolean(json, IJsonNames.INVENTORIABLE))
				.setSerializable(JsonUtils.getboolean(json, IJsonNames.SERIALIZABLE))
				.setLotable(JsonUtils.getboolean(json, IJsonNames.LOTABLE))
				.setManufactured(JsonUtils.getboolean(json, IJsonNames.MANUFACTURED))
				.setComposition(JsonUtils.getboolean(json, IJsonNames.COMPOSITION))
				.setCompositionPrice(JsonUtils.getboolean(json, IJsonNames.COMPOSITION_PRICE))
				.setPackaged(JsonUtils.getboolean(json, IJsonNames.PACKAGED))
				.setSalesAccount(new Account().setId(JsonUtils.getInteger(json, IJsonNames.SALES_ACCOUNT)))
				.setPurchaseAccount(new Account().setId(JsonUtils.getInteger(json, IJsonNames.PURCHASE_ACCOUNT)))
				.setPerishable(JsonUtils.getboolean(json, IJsonNames.PERISHABLE))
				.setDaysToExpire(JsonUtils.getInteger(json, IJsonNames.DAYS_TO_EXPIRE))
				.setCreationUser(JsonUtils.getString(json, IJsonNames.CREATION_USER2))
				.setCreationDate(JsonUtils.getDate(json, IJsonNames.CREATION_DATE2))
				.setModificationUser(JsonUtils.getString(json, IJsonNames.MODIFICATION_USER2))
				.setModificationDate(JsonUtils.getDate(json, IJsonNames.MODIFICATION_DATE2));
	}
	
	public static JSONArray toJSON(List<Product> products) {
		return toJSON(products.stream());
	}
	
	public static JSONArray toJSON(Stream<Product> products) {
		JSONArray array = new JSONArray();
		products.forEach(tax -> array.put(toJSON(tax)));
		return array;
	}
	
	public static JSONObject toJSON(Product product) {
		if(product == null) return null;
		return new JSONObject()
				.put(IJsonNames.ID, product.getId())
				.put(IJsonNames.DOMAIN, DomainJSON.toJSON(product.getDomain()))
				.put(IJsonNames.CODE, product.getCode())
				.put(IJsonNames.NAME, product.getName())
				.put(IJsonNames.BRAND, BrandJSON.toJSON(product.getBrand()))
				.put(IJsonNames.CATEGORY, ProductCategoryJSON.toJSON(product.getCategory()))
				.put(IJsonNames.TYPE, product.getType().name())
				.put(IJsonNames.KIND, product.getKind().name())
				.put(IJsonNames.STATUS, product.getStatus().name())
				.put(IJsonNames.VAT, TaxJSON.toJSON(product.getVat()))
				.put(IJsonNames.RETENTION, TaxJSON.toJSON(product.getRetention()))
				.put(IJsonNames.INVENTORIABLE, product.isInventoriable())
				.put(IJsonNames.SERIALIZABLE, product.isSerializable())
				.put(IJsonNames.LOTABLE, product.isLotable())
				.put(IJsonNames.MANUFACTURED, product.isManufactured())
				.put(IJsonNames.COMPOSITION, product.isComposition())
				.put(IJsonNames.COMPOSITION_PRICE, product.isCompositionPrice())
				.put(IJsonNames.PACKAGED, product.isPackaged())
				.put(IJsonNames.SALES_ACCOUNT, AccountJSON.toJSON(product.getSalesAccount()))
				.put(IJsonNames.PURCHASE_ACCOUNT, AccountJSON.toJSON(product.getPurchaseAccount()))
				.put(IJsonNames.PERISHABLE, product.isPerishable())
				.put(IJsonNames.DAYS_TO_EXPIRE, product.getDaysToExpire())
				.put(IJsonNames.CREATION_USER2, product.getCreationUser())
				.put(IJsonNames.CREATION_DATE2, product.getCreationDate())
				.put(IJsonNames.MODIFICATION_USER2, product.getModificationUser())
				.put(IJsonNames.MODIFICATION_DATE2, product.getModificationDate())
				;
	}
}
