package net.aonsolutions.occam.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.config.RegistryAddress;
import net.aonsolutions.occam.api.constants.StreetType;
import net.aonsolutions.watson.client.util.AonCollectionUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class RegistryAddressJSON {
	
	private RegistryAddressJSON() {
	}
	
	public static List<RegistryAddress> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AonJSONUtils.stream(array)
			.map(RegistryAddressJSON::from)
			.toList();		
	}
	
	public static RegistryAddress from(JSONObject json) {
		if (json == null) return null; 
		return new RegistryAddress()
			.setId(AonJSONUtils.getInteger(json, AonNames.ID))
			.setDomain(AonJSONUtils.getInteger(json, AonNames.DOMAIN))
			.setRegistry(AonJSONUtils.getInteger(json, AonNames.REGISTRY))
			.setMain(AonJSONUtils.getBoolean(json, AonNames.MAIN))
			.setRecipient(AonJSONUtils.getString(json, AonNames.RECIPIENT))
			.setStreetType( StreetType.safeValueOf(AonJSONUtils.getString(json, AonNames.STREET_TYPE)).orElse(null) )
			.setAddress(AonJSONUtils.getString(json, AonNames.ADDRESS))
			.setNumber(AonJSONUtils.getString(json, AonNames.NUMBER))
			.setAddress2(AonJSONUtils.getString(json, AonNames.ADDRESS2))
			.setAddress3(AonJSONUtils.getString(json, AonNames.ADDRESS3))
			.setGeozone( GeozoneJSON.from(AonJSONUtils.getObject(json, AonNames.GEOZONE)))
			.setParentGeozone( GeozoneJSON.from(AonJSONUtils.getObject(json, AonNames.PARENT_GEOZONE)))
			.setZip(AonJSONUtils.getString(json, AonNames.ZIP))
			.setCity(AonJSONUtils.getString(json, AonNames.CITY))
			.setAlias(AonJSONUtils.getString(json, AonNames.ALIAS))
			.setMunicipalityCode(AonJSONUtils.getString(json, AonNames.MUNICIPALITY_CODE))
		;
	}
	
	public static JSONArray to(List<RegistryAddress> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<RegistryAddress> stream) {
		return stream
			.map(RegistryAddressJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(RegistryAddress registryAddress) {
		if (registryAddress == null) return null;
		return new JSONObject()
			.put(AonNames.ID, registryAddress.getId())
			.put(AonNames.DOMAIN, registryAddress.getDomain())
			.put(AonNames.REGISTRY, registryAddress.getRegistry())
			.put(AonNames.MAIN, registryAddress.isMain())
			.putOpt(AonNames.RECIPIENT, registryAddress.getRecipient())
			.putOpt(AonNames.STREET_TYPE, AonObjectUtils.ifNotNullDo(registryAddress.getStreetType(), Object::toString ))
			.putOpt(AonNames.ADDRESS, registryAddress.getAddress())
			.putOpt(AonNames.NUMBER, registryAddress.getNumber())
			.putOpt(AonNames.ADDRESS2, registryAddress.getAddress2())
			.putOpt(AonNames.ADDRESS3, registryAddress.getAddress3())
			.putOpt(AonNames.GEOZONE, GeozoneJSON.to(registryAddress.getGeozone().orElse(null)))
			.putOpt(AonNames.PARENT_GEOZONE, GeozoneJSON.to(registryAddress.getParentGeozone().orElse(null)))
			.putOpt(AonNames.ZIP, registryAddress.getZip())
			.putOpt(AonNames.CITY, registryAddress.getCity())
			.putOpt(AonNames.ALIAS, registryAddress.getAlias())
			.putOpt(AonNames.MUNICIPALITY_CODE, registryAddress.getMunicipalityCode())
			;
	}
}
