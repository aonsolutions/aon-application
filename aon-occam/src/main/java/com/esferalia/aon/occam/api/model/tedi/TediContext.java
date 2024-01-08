package com.esferalia.aon.occam.api.model.tedi;

import java.io.Serializable;

public class TediContext implements Serializable {

	private static final long serialVersionUID = -1772168986100300984L;

	private TediContextKey key;
	private Integer line;

	public TediContext() {
	}

	public TediContext(TediContextKey key) {
		this(key, null);
	}

	public TediContext(TediContextKey key, Integer line) {
		this.key = key;
		this.line = line;
	}

	public TediContextKey getKey() {
		return key;
	}

	public TediContext setKey(TediContextKey key) {
		this.key = key;
		return this;
	}

	public Integer getLine() {
		return line;
	}

	public TediContext setLine(Integer line) {
		this.line = line;
		return this;
	}

	@Override
	public String toString() {
		return key == null ? "" : key.toString() + (line == null ? "" : (" (" + line + ")"));
	}

}