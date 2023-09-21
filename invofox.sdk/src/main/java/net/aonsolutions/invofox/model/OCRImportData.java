package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.Optional;

public class OCRImportData implements Serializable {
	
	private static final long serialVersionUID = -4972876800552085971L;
	
	private String ref;
	private String channel;
	private String[] info;
	
	public Optional<String> getRef() {
		return Optional.ofNullable(ref);
	}
	public OCRImportData setRef(String ref) {
		this.ref = ref;
		return this;
	}
	
	public Optional<String> getChannel() {
		return Optional.ofNullable(channel);
	}
	public OCRImportData setChannel(String channel) {
		this.channel = channel;
		return this;
	}
	
	public Optional<String[]> getInfo() {
		return Optional.ofNullable(info);
	}
	public OCRImportData setInfo(String[] info) {
		this.info = info;
		return this;
	}
	
	
}
