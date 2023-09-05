package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.management.ShipmentPeriod;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.type.ShipmentStatus;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.watson.server.AonDateUtils;

public class DeliveryJSON {
	
	private DeliveryJSON() {
	
	}
	
	public static List<Delivery> fromJSON(JSONArray json) {
		LinkedList<Delivery> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static Delivery fromJSON(JSONObject json) {
		return new Delivery()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setProject(ProjectJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.PROJECT)))
			.setSeries(JsonUtils.getString(json, IJsonNames.SERIES))
			.setNumber(JsonUtils.getInt(json, IJsonNames.NUMBER))
			.setCustomer(CustomerJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.CUSTOMER)))
			.setAddress(RegistryAddressJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.ADDRESS)))
			.setDate(JsonUtils.getDate(json, IJsonNames.DATE))
			.setPayMethod(PayMethodJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.PAY_METHOD)))
			.setConfidential(JsonUtils.getboolean(json, IJsonNames.CONFIDENTIAL))
			.setStatus(DeliveryStatus.safeValueOf(JsonUtils.getString(json, IJsonNames.STATUS)))
			.setComments(JsonUtils.getString(json, IJsonNames.COMMENTS))
			.setRemarks(JsonUtils.getString(json, IJsonNames.REMARKS))
			.setWorkplace(WorkplaceJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.WORKPLACE)))
			.setScope(ScopeJSON.fromJSON(JsonUtils.getJSONObject(json, IJsonNames.SCOPE)))
			
			.setNumberOfPymnts(JsonUtils.getShort(json, IJsonNames.NUMBER_OF_PYMNTS))
			.setDaysToFirstPymnt(JsonUtils.getShort(json, IJsonNames.DAYS_TO_FIRST_PYMNT))
			.setDaysBetweenPymnt(JsonUtils.getShort(json, IJsonNames.DAYS_BETWEEN_PYMNTS))
			.setPymntDays(JsonUtils.getString(json, IJsonNames.PYMNT_DAYS))

			.setBankAccount(JsonUtils.getString(json, IJsonNames.BANK_ACCOUNT))
			.setBankAlias(JsonUtils.getString(json, IJsonNames.BANK_ALIAS))
			.setBic(JsonUtils.getString(json, IJsonNames.BIC))
			
			.setCarrier(JsonUtils.getInteger(json, IJsonNames.CARRIER))
			.setCarrierPacking(JsonUtils.getInteger(json, IJsonNames.CARRIER_PACKING))
			.setNumberPlate(JsonUtils.getString(json, IJsonNames.NUMBER_PLATE))
			.setDriver(JsonUtils.getString(json, IJsonNames.DRIVER))
			.setDriverDocument(JsonUtils.getString(json, IJsonNames.DRIVER_DOCUMENT))
			
			.setTotalPackages(JsonUtils.getdouble(json, IJsonNames.TOTAL_PACKAGES))
			.setTotalWeight(JsonUtils.getdouble(json, IJsonNames.TOTAL_WEIGHT))
			.setShippingAlternativeAddress(JsonUtils.getString(json, IJsonNames.SHIPPING_ALTERNATIVE_ADDRESS))
			.setShippingAlternativeAddress2(JsonUtils.getString(json, IJsonNames.SHIPPING_ALTERNATIVE_ADDRESS2))
			.setShippingAlternativeZip(JsonUtils.getString(json, IJsonNames.SHIPPING_ALTERNATIVE_ZIP))
			.setShippingAlternativeCity(JsonUtils.getString(json, IJsonNames.SHIPPING_ALTERNATIVE_CITY))
			.setShippingAlternativePhone(JsonUtils.getString(json, IJsonNames.SHIPPING_ALTERNATIVE_PHONE))
			.setShippingAlternativeRecipient(JsonUtils.getString(json, IJsonNames.SHIPPING_ALTERNATIVE_RECIPIENT))
			.setShippingContact(JsonUtils.getString(json, IJsonNames.SHIPPING_CONTACT))
			.setShippingPeriod(ShipmentPeriod.safeValueOf(JsonUtils.getString(json, IJsonNames.SHIPPING_PERIOD)))
			.setShippingStatus(ShipmentStatus.safeValueOf(JsonUtils.getString(json, IJsonNames.SHIPPING_STATUS)))
			.setStatusModificationDate(JsonUtils.getDate(json, IJsonNames.STATUS_MODIFICATION_DATE))
			.setDetails(DeliveryDetailJSON.fromJSON(JsonUtils.getJSONArray(json, IJsonNames.DETAILS)))
			;
	}
	
	public static JSONArray toJSON(List<Delivery> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<Delivery> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	public static JSONObject toJSON(Delivery object) {
		return new JSONObject()
			.put(IJsonNames.ID, object.getId())
			.put(IJsonNames.DOMAIN, object.getDomain())
			.put(IJsonNames.PROJECT, ProjectJSON.toJSON(object.getProject()))
			.put(IJsonNames.SERIES, object.getSeries())
			.put(IJsonNames.NUMBER, object.getNumber())
			.put(IJsonNames.CUSTOMER, CustomerJSON.toJSON(object.getCustomer()))
			.put(IJsonNames.ADDRESS, RegistryAddressJSON.toJSON(object.getAddress()))
			.put(IJsonNames.DATE, AonDateUtils.format(object.getDate(), AonDateUtils.DATE_TIME_FORMAT))
			.put(IJsonNames.PAY_METHOD, PayMethodJSON.toJSON(object.getPayMethod()))
			.put(IJsonNames.CONFIDENTIAL, object.isConfidential())
			.put(IJsonNames.STATUS, object.getStatus() != null
				? object.getStatus().getName() : DeliveryStatus.PENDING.getName())
			.put(IJsonNames.COMMENTS, object.getComments())
			.put(IJsonNames.REMARKS, object.getRemarks())
			.put(IJsonNames.WORKPLACE, WorkplaceJSON.toJSON(object.getWorkplace()))
			.put(IJsonNames.SCOPE, ScopeJSON.toJSON(object.getScope()))
			
			.put(IJsonNames.NUMBER_OF_PYMNTS, object.getNumberOfPymnts())
			.put(IJsonNames.DAYS_TO_FIRST_PYMNT, object.getDaysToFirstPymnt())
			.put(IJsonNames.DAYS_BETWEEN_PYMNTS, object.getDaysBetweenPymnt())
			.put(IJsonNames.PYMNT_DAYS, object.getPymntDays())

			.put(IJsonNames.BANK_ACCOUNT, object.getBankAccount())
			.put(IJsonNames.BANK_ALIAS, object.getBankAlias())
			.put(IJsonNames.BIC, object.getBic())
			
			.put(IJsonNames.CARRIER, object.getCarrier())
			.put(IJsonNames.CARRIER_PACKING, object.getCarrierPacking())
			.put(IJsonNames.NUMBER_PLATE, object.getNumberPlate())
			.put(IJsonNames.DRIVER, object.getDriver())
			.put(IJsonNames.DRIVER_DOCUMENT, object.getDriverDocument())
			
			.put(IJsonNames.TOTAL_PACKAGES, object.getTotalPackages())
			.put(IJsonNames.TOTAL_WEIGHT, object.getTotalWeight())
			
			.put(IJsonNames.SHIPPING_ALTERNATIVE_ADDRESS, object.getShippingAlternativeAddress())
			.put(IJsonNames.SHIPPING_ALTERNATIVE_ADDRESS2, object.getShippingAlternativeAddress2())
			.put(IJsonNames.SHIPPING_ALTERNATIVE_ZIP, object.getShippingAlternativeZip())
			.put(IJsonNames.SHIPPING_ALTERNATIVE_CITY, object.getShippingAlternativeCity())
			.put(IJsonNames.SHIPPING_ALTERNATIVE_PHONE, object.getShippingAlternativePhone())
			.put(IJsonNames.SHIPPING_ALTERNATIVE_RECIPIENT, object.getShippingAlternativeRecipient())
			.put(IJsonNames.SHIPPING_CONTACT, object.getShippingContact())
			.put(IJsonNames.SHIPPING_PERIOD, object.getShippingPeriod() != null
										? object.getShippingPeriod().name() : null)
			.put(IJsonNames.SHIPPING_STATUS, object.getShippingStatus() != null
										? object.getShippingStatus().name() : null)
			.put(IJsonNames.STATUS_MODIFICATION_DATE, AonDateUtils.format(object.getStatusModificationDate(), AonDateUtils.DATE_TIME_FORMAT))
			.put(IJsonNames.DETAILS, DeliveryDetailJSON.toJSON(object.getDetails()))
			;
	}
}
