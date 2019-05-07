package es.translogia.tedi;

import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.json.JSONArray;
import org.json.JSONObject;

public class TediUser {

	public static final String SRC = "/user";

	private String email;
	private String name;
	private String surname;
	private String document;
	private String birthdate;
	private TediPermissions permissions;
	private String actualCompany;
	private LinkedList<String> companies;
	private LinkedList<String> users;
	
	public TediUser() {}
	
	public TediUser(JSONObject json) {
		this.email = json.getString("email");
		this.name = json.getString("name");
		this.surname = json.getString("surname");
		this.document = json.getString("document");
		this.birthdate = json.getString("birthdate");
		this.permissions = new TediPermissions(json.getJSONArray("permissions"));
		this.actualCompany = json.getString("actual_company");
		this.companies = StreamSupport.stream(json.getJSONArray("companies").spliterator(), false).map(r -> r.toString())
				.collect(Collectors.toCollection(LinkedList::new));
		this.users = StreamSupport.stream(json.getJSONArray("users").spliterator(), false).map(r -> r.toString())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public String getEmail() {
		return email;
	}
	
	public TediUser setEmail(String email) {
		this.email = email;
		return this;
	}
	
	public String getName() {
		return name;
	}
	
	public TediUser setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public TediUser setSurname(String surname) {
		this.surname = surname;
		return this;
	}
	
	public String getDocument() {
		return document;
	}
	
	public TediUser setDocument(String document) {
		this.document = document;
		return this;
	}
	
	public String getBirthdate() {
		return birthdate;
	}
	
	public TediUser setBirthdate(String birthdate) {
		this.birthdate = birthdate;
		return this;
	}
	
	public TediPermissions getPermissions() {
		return permissions;
	}
	
	public TediUser setPermissions(TediPermissions permissions) {
		this.permissions = permissions;
		return this;
	}

	public JSONObject getJSON() {
		JSONArray companiesArray = new JSONArray();
		this.companies.stream().forEach(r -> companiesArray.put(r));
		
		JSONArray usersArray = new JSONArray();
		this.users.stream().forEach(r -> companiesArray.put(r));

		return new JSONObject()
				.put("email", getEmail())
				.put("name", getName())
				.put("surname", getSurname())
				.put("document", getDocument())
				.put("birthdate", getBirthdate())
				.put("actual_company", this.actualCompany)
				.put("companies", companiesArray)
				.put("users", usersArray)
				.put("permissions", getPermissions().getJSON());
	}
}
