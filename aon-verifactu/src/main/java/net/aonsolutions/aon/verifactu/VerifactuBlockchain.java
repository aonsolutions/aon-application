package net.aonsolutions.aon.verifactu;

public class VerifactuBlockchain {

	private String document;
	private String reference;
	private String date;
	private String huella;

	public String getDocument() {
		return document;
	}
	
	public VerifactuBlockchain setDocument(String document) {
		this.document = document;
		return this;
	}
	
	public String getDate() {
		return date;
	}
	
	public VerifactuBlockchain setDate(String date) {
		this.date = date;
		return this;
	}

	public String getReference() {
		return reference;
	}

	public VerifactuBlockchain setReference(String reference) {
		this.reference = reference;
		return this;
	}

	public String getHuella() {
		return huella;
	}

	public VerifactuBlockchain setHuella(String huella) {
		this.huella = huella;
		return this;
	}	
	
//	public JSONObject toJSON() {	
//		return new JSONObject()
//			.put(IJsonNames.DOCUMENT, getDocument())
//			.put(IJsonNames.REFERENCE, getReference())
//			.put(IJsonNames.DATE, getDate())
//			.put(IJsonNames.HUELLA, getHuella());
//	}
//	
//	public static VerifactuBlockchain fromJSON(String str) {
//		if(str == null) return new VerifactuBlockchain();
//		return fromJSON(new JSONObject(str));		
//	}
//	
//	public static VerifactuBlockchain fromJSON(JSONObject json ) {
//		if(json == null) return new VerifactuBlockchain();
//		return new VerifactuBlockchain()
//				.setDate(JsonUtils.getString(json, IJsonNames.DATE))
//				.setReference(JsonUtils.getString(json, IJsonNames.REFERENCE))
//				.setDocument(JsonUtils.getString(json, IJsonNames.DOCUMENT))
//				.setHuella(JsonUtils.getString(json, IJsonNames.HUELLA));		
//	}
//	
//	public boolean isEmpty() {
//		return AonStringUtils.isBlank(getDocument()) 
//			&& AonStringUtils.isBlank(getDate())
//			&& AonStringUtils.isBlank(getReference())
//			&& AonStringUtils.isBlank(getHuella());
//	}
}
