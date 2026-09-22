package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;

public class CarrierPackingJSON {

	private CarrierPackingJSON() {
	
	}

	public static List<CarrierPacking> fromJSON(JSONArray array) {
		LinkedList<CarrierPacking> list = new LinkedList<>();
		for(Integer i = 0; i < array.length(); i++) {
			list.add(fromJSON(array.getJSONObject(i)));
		}
 		return list;
	}
	
	public static CarrierPacking fromJSON(JSONObject json) {
		if(JsonUtils.isEmpty(json)) return null;
		JSONObject carrierJSON = JsonUtils.getJSONObject(json, IJsonNames.CARRIER);
		return new CarrierPacking()
			.setId(JsonUtils.getInteger(json, IJsonNames.ID))
			.setDomain(JsonUtils.getInteger(json, IJsonNames.DOMAIN))
			.setSeries(JsonUtils.getString(json, IJsonNames.SERIES))
			.setNumber(JsonUtils.getInteger(json, IJsonNames.NUMBER))
			.setType(CarrierPackingType.safeValueOf(JsonUtils.getString(json, IJsonNames.TYPE)))
			.setStatus(CarrierPackingStatus.safeValueOf(JsonUtils.getString(json, IJsonNames.STATUS)))
			.setIssueDate(JsonUtils.getDate(json, IJsonNames.ISSUE_DATE))
			.setDeliveryDate(JsonUtils.getDate(json, IJsonNames.DATE) != null 
				? JsonUtils.getDate(json, IJsonNames.DATE) // Serfruit compatibility
				: JsonUtils.getDate(json, IJsonNames.DELIVERY_DATE))
			.setCarrierName(JsonUtils.getString(carrierJSON, IJsonNames.NAME))
			.setCarrierDocument(JsonUtils.getString(carrierJSON, IJsonNames.DOCUMENT))
			.setCarrier(JsonUtils.getInteger(carrierJSON, IJsonNames.ID))
			.setCarrierReference(JsonUtils.getString(json, IJsonNames.CARRIER_REFERENCE))
			.setNumberPlate(JsonUtils.getString(json, IJsonNames.NUMBER_PLATE))
			.setDriverName(JsonUtils.getString(json, IJsonNames.DRIVER_NAME))
			.setDriverDocument(JsonUtils.getString(json, IJsonNames.DRIVER_DOCUMENT))
			.setComments(JsonUtils.getString(json, IJsonNames.COMMENTS))
			
			.setGross(JsonUtils.getDouble(json, IJsonNames.GROSS))
			.setTare(JsonUtils.getDouble(json, IJsonNames.TARE))
			.setAdditionalTare(JsonUtils.getDouble(json, IJsonNames.ADDITIONAL_TARE))
			.setNet(JsonUtils.getDouble(json, IJsonNames.NET))
			.setReceptionStartDate(JsonUtils.getDate(json, IJsonNames.RECEPTION_START_DATE))
			.setReceptionEndDate(JsonUtils.getDate(json, IJsonNames.RECEPTION_END_DATE))
			.setQr(JsonUtils.getString(json, IJsonNames.QR))
			
			.setCreationUser(JsonUtils.getString(json, IJsonNames.CREATION_USER2))
			.setCreationDate(JsonUtils.getDate(json, IJsonNames.CREATION_DATE2))
			.setModificationUser(JsonUtils.getString(json, IJsonNames.MODIFICATION_USER2))
			.setModificationDate(JsonUtils.getDate(json, IJsonNames.MODIFICATION_DATE2))
			;
	}

	public static JSONArray toJSON(List<CarrierPacking> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<CarrierPacking> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	public static JSONObject toJSON(CarrierPacking object) {
		if(object == null) return null;
		return new JSONObject()
			.put(IJsonNames.ID, object.getId())
			.put(IJsonNames.DOMAIN, object.getDomain())
			.put(IJsonNames.SERIES, object.getSeries())
			.put(IJsonNames.NUMBER, object.getNumber())
			.put(IJsonNames.REFERENCE, object.getType() != null ? object.getReferenceCode() : null)
			.put(IJsonNames.TYPE, object.getType() != null ? object.getType().name() : null)
			.put(IJsonNames.STATUS, object.getStatus() != null ? object.getStatus().name() : null)
			.put(IJsonNames.ISSUE_DATE, JsonUtils.getDateTimeJSON(object.getIssueDate()))
			.put(IJsonNames.DELIVERY_DATE, JsonUtils.getDateTimeJSON(object.getDeliveryDate()))
			.put(IJsonNames.CARRIER, carrierToJSON(object))
			.put(IJsonNames.CARRIER_REFERENCE, object.getCarrierReference())
			.put(IJsonNames.NUMBER_PLATE, object.getNumberPlate())
			.put(IJsonNames.DRIVER_NAME, object.getDriverName())
			.put(IJsonNames.DRIVER_DOCUMENT, object.getDriverDocument())
			.put(IJsonNames.COMMENTS, object.getComments())
			.put(IJsonNames.OBSERVATION, object.getObservation())
			.put(IJsonNames.PARAMS, object.getParams())
			
			.put(IJsonNames.GROSS, object.getGross())
			.put(IJsonNames.TARE, object.getTare())
			.put(IJsonNames.ADDITIONAL_TARE, object.getAdditionalTare())
			.put(IJsonNames.NET, object.getNet())
			.put(IJsonNames.RECEPTION_START_DATE, JsonUtils.getDateTimeJSON(object.getReceptionStartDate()))
			.put(IJsonNames.RECEPTION_END_DATE, JsonUtils.getDateTimeJSON(object.getReceptionEndDate()))
			.put(IJsonNames.QR, object.getQr())
			
			.put(IJsonNames.CREATION_USER2, object.getCreationUser())
			.put(IJsonNames.CREATION_DATE2, JsonUtils.getDateTimeJSON(object.getCreationDate()))
			.put(IJsonNames.MODIFICATION_USER2, object.getModificationUser())
			.put(IJsonNames.MODIFICATION_DATE2, JsonUtils.getDateTimeJSON(object.getModificationDate()))
			;
	}
	
	private static JSONObject carrierToJSON(CarrierPacking object) {
		JSONObject json = new JSONObject()
			.put(IJsonNames.ID, object.getCarrier())
			.put(IJsonNames.NAME, object.getCarrierName())
			.put(IJsonNames.DOCUMENT, object.getCarrierDocument());
		return JsonUtils.isNotEmpty(json) ? json : null;
	}
}
