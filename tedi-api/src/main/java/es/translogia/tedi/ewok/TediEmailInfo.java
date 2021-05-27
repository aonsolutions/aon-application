package es.translogia.tedi.ewok;

import java.io.Serializable;

public class TediEmailInfo implements Serializable {
	
	private static final long serialVersionUID = 6865210693414641809L;
	
	private String id;
	private String[] from;
	private String fileName;

	public String getId() {
		return id;
	}

	public TediEmailInfo setId(String id) {
		this.id = id;
		return this;
	}

	public String[] getFrom() {
		return from;
	}

	public TediEmailInfo setFrom(String[] from) {
		this.from = from;
		return this;
	}

	public String getFileName() {
		return fileName;
	}

	public TediEmailInfo setFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}

}
