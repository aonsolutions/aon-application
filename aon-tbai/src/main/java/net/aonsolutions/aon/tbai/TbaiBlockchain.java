package net.aonsolutions.aon.tbai;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;

public class TbaiBlockchain {

	String date;
	String serie;
	String number;
	String signature;
	
	public String getDate() {
		return date;
	}
	
	public TbaiBlockchain setDate(String date) {
		this.date = date;
		return this;
	}

	public String getSerie() {
		return serie;
	}

	public TbaiBlockchain setSerie(String serie) {
		this.serie = serie;
		return this;
	}

	public String getNumber() {
		return number;
	}

	public TbaiBlockchain setNumber(String number) {
		this.number = number;
		return this;
	}

	public String getSignature() {
		return signature;
	}

	public TbaiBlockchain setSignature(String signature) {
		this.signature = signature;
		return this;
	}	
	
	public JSONObject toJSON() {	
		return new JSONObject()
				.put("date", getDate())
				.put("serie", getSerie())
				.put("number", getNumber())
				.put("signature", getSignature());
	}
	
	public static TbaiBlockchain fromJSON(String str) {
		if(str == null) return new TbaiBlockchain();
		return fromJSON(new JSONObject(str));		
	}
	
	public static TbaiBlockchain fromJSON(JSONObject json ) {
		if(json == null) return new TbaiBlockchain();
		return new TbaiBlockchain()
				.setDate(JsonUtils.getString(json, IJsonNames.DATE))
				.setSerie(JsonUtils.getString(json, IJsonNames.SERIE))
				.setNumber(JsonUtils.getString(json, IJsonNames.NUMBER))
				.setSignature(JsonUtils.getString(json, IJsonNames.SIGNATURE));		
	}
	
	public boolean isEmpty() {
		return getDate() == null && getNumber() == null && getSerie() == null && getSignature() == null;
	}
}
