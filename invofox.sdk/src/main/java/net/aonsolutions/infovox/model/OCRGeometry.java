package net.aonsolutions.infovox.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OCRGeometry {
	private List<OCRPage> pages;
	private Map<String,List<OCRBox>> boxes;
	
	public Optional<List<OCRPage>> getPages() {
		return Optional.ofNullable(pages);
	}
	public OCRGeometry setPages(List<OCRPage> pages) {
		this.pages = pages;
		return this;
	}
	
	public Optional<Map<String,List<OCRBox>>> getBoxes() {
		return Optional.ofNullable(boxes);
	}
	public OCRGeometry setBoxes(Map<String,List<OCRBox>> boxes) {
		this.boxes = boxes;
		return this;
	}
	
}
