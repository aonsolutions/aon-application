package es.translogia.tedi.ewok;

import java.io.Serializable;

public class TediRegistry implements Serializable {

	private static final long serialVersionUID = 6858816497836464586L;
	
	private String name;
	private String document;
	private String documentCountry;
	private TediAddress address;
	
	public String getName() {
		return name;
	}
	public TediRegistry setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getDocument() {
		return document;
	}
	public TediRegistry setDocument(String document) {
		this.document = document;
		return this;
	}
	
	public String getDocumentCountry() {
		return documentCountry;
	}
	public TediRegistry setDocumentCountry(String documentCountry) {
		this.documentCountry = documentCountry;
		return this;
	}

	public TediAddress getAddress() {
		return address;
	}
	public TediRegistry setAddress(TediAddress address) {
		this.address = address;
		return this;
	}
	
}
