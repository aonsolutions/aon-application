package com.esferalia.aon.occam.api.json;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.type.Gender;
import com.esferalia.aon.occam.api.model.type.MaritalStatus;
import com.esferalia.aon.watson.server.AonDateUtils;

public class PersonJSON {
	
	public static Person fromJSON(JSONObject json) {
		return new Person()
			.copy(RegistryJSON.fromJSON(json))
			.setBirthDate(JsonUtils.getDate(json, IJsonNames.BIRTH_DATE))
			.setFirstName(JsonUtils.getString(json, IJsonNames.FIRST_NAME))
			.setFirstSurname(JsonUtils.getString(json, IJsonNames.FIRST_SURNAME))
			.setSecondSurname(JsonUtils.getString(json, IJsonNames.SECOND_SURNAME))
			.setGender(Gender.safeValueOf(JsonUtils.getString(json, IJsonNames.GENDER)))
			.setMaritalStatus(MaritalStatus.safeValueOf(JsonUtils.getString(json, IJsonNames.MARITAL_STATUS)))
			.setSocialSecurityNum(JsonUtils.getString(json, IJsonNames.SOCIAL_SECURITY_NUMBER));	
	}

	public static List<Person> fromJSON(JSONArray json) {
		LinkedList<Person> list = new LinkedList<>();
		for(Integer i = 0; i < json.length(); i++) {
			list.add(fromJSON(json.getJSONObject(i)));
		}
 		return list;
	}
	
	public static JSONArray toJSON(List<Person> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<Person> persons) {
		JSONArray array = new JSONArray();
		persons.forEach(t -> array.put(toJSON(t)));
		return array;
	}
	
	public static JSONObject toJSON(Person person) {
		JSONObject personJson = RegistryJSON.toJSON(person);
		return fill(personJson, person);
	}
	
	private static JSONObject fill(JSONObject personJson, Person person) {
		return personJson
			.put(IJsonNames.BIRTH_DATE, AonDateUtils.format(person.getBirthDate(), AonDateUtils.SIMPLE_DATE_FORMAT4))
			.put(IJsonNames.FIRST_NAME, person.getFirstName())
			.put(IJsonNames.FIRST_SURNAME, person.getFirstSurname())
			.put(IJsonNames.SECOND_SURNAME, person.getSecondSurname())
			.put(IJsonNames.GENDER, person.getGender()==null ? null : person.getGender().name())
			.put(IJsonNames.MARITAL_STATUS, person.getMaritalStatus()==null ? null : person.getMaritalStatus().name())
			.put(IJsonNames.SOCIAL_SECURITY_NUMBER, person.getSocialSecurityNum());
	}
	
}
