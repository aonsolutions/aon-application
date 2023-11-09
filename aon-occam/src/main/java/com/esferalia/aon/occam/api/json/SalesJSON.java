package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.type.SalesStatus;
import com.esferalia.aon.occam.api.model.type.SalesType;
import com.esferalia.aon.watson.server.AonDateUtils;

public class SalesJSON {
	
	private SalesJSON() {
	
	}
	
	public static List<Sales> fromJSON(JSONArray json) {
		LinkedList<Sales> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Sales fromJSON(JSONObject json) {
		return new Sales()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setProject(ProjectJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.PROJECT)))
			.setSeries(JsonUtils.getString(json, IJsonNames.SERIES))
			.setNumber(JsonUtils.getInt(json, IJsonNames.NUMBER))
			.setPurchaseReference(JsonUtils.getString(json, IJsonNames.PURCHASE_REFERENCE))
			.setCustomer(CustomerJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.CUSTOMER)))
			.setSeller(SellerJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.SELLER)))
			.setShippingAddress(RegistryAddressJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.ADDRESS)))
			.setDate(JsonUtils.getDate(json, IJsonNames.DATE))
			.setPayMethod(PayMethodJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.PAY_METHOD)))
			.setConfidential(JsonUtils.getboolean(json, IJsonNames.CONFIDENTIAL))
			.setStatus(SalesStatus.safeValueOf(JsonUtils.getString(json, IJsonNames.STATUS)))
			.setComments(JsonUtils.getString(json, IJsonNames.COMMENTS))
			.setRemarks(JsonUtils.getString(json, IJsonNames.REMARKS))
			.setWorkplace(WorkplaceJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.WORKPLACE)))
			.setScope(ScopeJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.SCOPE)))

			.setDocumentType(SalesType.safeValueOf(JsonUtils.getString(json, IJsonNames.TYPE)))
			.setDiscountExpr(JsonUtils.getString(json, IJsonNames.DISCOUNT))
			.setDeliveryDate(JsonUtils.getDate(json, IJsonNames.DELIVERY_DATE))
			.setNumberOfPymnts(JsonUtils.getShort(json, IJsonNames.NUMBER_OF_PYMNTS))
			.setDaysToFirstPymnt(JsonUtils.getShort(json, IJsonNames.DAYS_TO_FIRST_PYMNT))
			.setDaysBetweenPymnts(JsonUtils.getShort(json, IJsonNames.DAYS_BETWEEN_PYMNTS))
			.setPymntDays(JsonUtils.getString(json, IJsonNames.PYMNT_DAYS))

			.setBankAccount(JsonUtils.getString(json, IJsonNames.BANK_ACCOUNT))
			.setBankAlias(JsonUtils.getString(json, IJsonNames.BANK_ALIAS))
			.setBic(JsonUtils.getString(json, IJsonNames.BIC))
			
			.setCarrier(CarrierJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.CARRIER)))
			.setCarrierPacking(JsonUtils.getInteger(json, IJsonNames.CARRIER_PACKING))
			
			.setShippingAlternativeAddress(JsonUtils.getString(json, IJsonNames.SHIPPING_ALTERNATIVE_ADDRESS))
			.setShippingAlternativeAddress2(JsonUtils.getString(json, IJsonNames.SHIPPING_ALTERNATIVE_ADDRESS2))
			.setShippingAlternativeZip(JsonUtils.getString(json, IJsonNames.SHIPPING_ALTERNATIVE_ZIP))
			.setShippingAlternativeCity(JsonUtils.getString(json, IJsonNames.SHIPPING_ALTERNATIVE_CITY))
			.setShippingAlternativePhone(JsonUtils.getString(json, IJsonNames.SHIPPING_ALTERNATIVE_PHONE))
			.setShippingAlternativeRecipient(JsonUtils.getString(json, IJsonNames.SHIPPING_ALTERNATIVE_RECIPIENT))
			.setShippingContact(JsonUtils.getString(json, IJsonNames.SHIPPING_CONTACT))
			.setDetails(SalesDetailJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.DETAILS)))
			;
	}
	
	public static JSONArray toJSON(List<Sales> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<Sales> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	public static JSONObject toJSON(Sales object) {
		return new JSONObject()
			.put(IJsonNames.ID, object.getId())
			.put(IJsonNames.DOMAIN, object.getDomain())
			.put(IJsonNames.PROJECT, ProjectJSON.toJSON(object.getProject()))
			.put(IJsonNames.SERIES, object.getSeries())
			.put(IJsonNames.NUMBER, object.getNumber())
			.put(IJsonNames.REFERENCE, object.getReferenceCode())
			.put(IJsonNames.PURCHASE_REFERENCE, object.getPurchaseReference())
			.put(IJsonNames.CUSTOMER, CustomerJSON.toJSON(object.getCustomer()))
			.put(IJsonNames.SELLER, SellerJSON.toJSON(object.getSeller()))
			.put(IJsonNames.ADDRESS, RegistryAddressJSON.toJSON(object.getShippingAddress()))
			.put(IJsonNames.DATE, AonDateUtils.format(object.getDate(), AonDateUtils.DATE_TIME_FORMAT))
			.put(IJsonNames.PAY_METHOD, PayMethodJSON.toJSON(object.getPayMethod()))
			.put(IJsonNames.CONFIDENTIAL, object.isConfidential())
			.put(IJsonNames.STATUS, object.getStatus().getName())
			.put(IJsonNames.COMMENTS, object.getComments())
			.put(IJsonNames.REMARKS, object.getRemarks())
			.put(IJsonNames.WORKPLACE, WorkplaceJSON.toJSON(object.getWorkplace()))
			.put(IJsonNames.SCOPE, ScopeJSON.toJSON(object.getScope()))
			.put(IJsonNames.TYPE, object.getDocumentType().getName())
			.put(IJsonNames.NUMBER_OF_PYMNTS, object.getNumberOfPymnts())
			.put(IJsonNames.DAYS_TO_FIRST_PYMNT, object.getDaysToFirstPymnt())
			.put(IJsonNames.DAYS_BETWEEN_PYMNTS, object.getDaysBetweenPymnts())
			.put(IJsonNames.PYMNT_DAYS, object.getPymntDays())
			.put(IJsonNames.BANK_ACCOUNT, object.getBankAccount())
			.put(IJsonNames.BANK_ALIAS, object.getBankAlias())
			.put(IJsonNames.BIC, object.getBic())
		
			.put(IJsonNames.DISCOUNT, object.getDiscountExpr())
			.put(IJsonNames.CARRIER, CarrierJSON.toJSON(object.getCarrier()))
			.put(IJsonNames.CARRIER_PACKING, object.getCarrierPacking())
			.put(IJsonNames.DELIVERY_DATE,  AonDateUtils.format(object.getDeliveryDate(), AonDateUtils.DATE_TIME_FORMAT))
			
			.put(IJsonNames.SHIPPING_ALTERNATIVE_ADDRESS, object.getShippingAlternativeAddress())
			.put(IJsonNames.SHIPPING_ALTERNATIVE_ADDRESS2, object.getShippingAlternativeAddress2())
			.put(IJsonNames.SHIPPING_ALTERNATIVE_ZIP, object.getShippingAlternativeZip())
			.put(IJsonNames.SHIPPING_ALTERNATIVE_CITY, object.getShippingAlternativeCity())
			.put(IJsonNames.SHIPPING_ALTERNATIVE_PHONE, object.getShippingAlternativePhone())
			.put(IJsonNames.SHIPPING_ALTERNATIVE_RECIPIENT, object.getShippingAlternativeRecipient())
			.put(IJsonNames.SHIPPING_CONTACT, object.getShippingContact())
			.put(IJsonNames.SHIPPING_PERIOD, object.getShippingPeriod() != null 
										? object.getShippingPeriod().name() : null)
			.put(IJsonNames.DETAILS, SalesDetailJSON.toJSON(object.getDetails()));
			
	}
}
