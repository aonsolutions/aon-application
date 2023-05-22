package net.aonsolutions.occam.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.config.Booking;
import net.aonsolutions.occam.api.constants.AonStatus;
import net.aonsolutions.watson.client.util.AonCollectionUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class BookingJSON {
	
	private BookingJSON() {
	}
	
	public static List<Booking> from(JSONArray array) {
		if (array == null || array.isEmpty()) return new LinkedList<>();
		return AonJSONUtils.stream(array)
			.map(BookingJSON::from)
			.toList();		
	}
	
	public static Booking from(JSONObject json) {
		if (json == null) return null; 
		return new Booking()
			.setOwner(AonJSONUtils.getString(json, AonNames.OWNER))
			.setExpirationDate(AonJSONUtils.getDate(json, AonNames.EXPIRATION_DATE))
			.setDomainManagement(AonJSONUtils.getBoolean(json, AonNames.DOMAIN_MANAGEMENT))
			.setDisableDomainManagement(AonJSONUtils.getBoolean(json, AonNames.DISABLE_DOMAIN_MANAGEMENT))
			.setMaxDefinedUsers(AonJSONUtils.getInteger(json, AonNames.MAX_DEFINED_USERS))
			.setAonCustomer(AonJSONUtils.getInteger(json, AonNames.AON_CUSTOMER))  
			.setAonStatus( AonStatus.safeValueOf(AonJSONUtils.getString(json, AonNames.AON_STATUS)).orElse(null) )
		;
	}
	
	public static JSONArray to(List<Booking> list) {
		if (AonCollectionUtils.isEmpty(list)) return new JSONArray();
		return to(list.stream());
	}
	
	public static JSONArray to(Stream<Booking> stream) {
		return stream
			.map(BookingJSON::to)
			.collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put));
	}
	
	public static JSONObject to(Booking booking) {
		if (booking == null) return null;
		return new JSONObject()
			.putOpt(AonNames.OWNER, booking.getOwner())
			.putOpt(AonNames.EXPIRATION_DATE, AonJSONUtils.formatDate(booking.getExpirationDate().orElse(null)))
			.putOpt(AonNames.DOMAIN_MANAGEMENT, booking.isDomainManagement())
			.putOpt(AonNames.DISABLE_DOMAIN_MANAGEMENT, booking.isDisableDomainManagement())
			.putOpt(AonNames.MAX_DEFINED_USERS, booking.getMaxDefinedUsers().orElse(null))
			.putOpt(AonNames.AON_STATUS, AonObjectUtils.ifNotNullDo(booking.getAonStatus(), Object::toString) )
			.putOpt(AonNames.AON_CUSTOMER, booking.getAonCustomer().orElse(null))
			;
	}
}
