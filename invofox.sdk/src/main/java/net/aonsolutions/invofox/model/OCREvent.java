package net.aonsolutions.invofox.model;

import java.util.Arrays;
import java.util.List;

public enum OCREvent {
	
	DOCUMENT_FINISHED("document.finished"),
	DOCUMENT_PROCESSED("document.processed"),
	DOCUMENT_CORRECTED("document.corrected"),
	DOCUMENT_DISCARDED("document.discarded"),
	DOCUMENT_REJECTED("document.rejected"),
	DOCUMENT_APPROVED("document.approved"),
	DOCUMENT_EXPORTED("document.exported"),
	BATCH_FINISHED("batch.finished"),
	BATCH_PROCESSED("batch.processed");

	
	private String name;
	
	private OCREvent(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static List<OCREvent> getValues() {
		return Arrays.asList(values());
	}
}
