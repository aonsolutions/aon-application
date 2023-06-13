package net.aonsolutions.infovox.model;

import java.util.Optional;

public class OCRBox {
	private Integer pageIndex;
	private Integer[] coordinates;
	
	public Optional<Integer >getPageIndex() {
		return Optional.ofNullable(pageIndex);
	}
	public OCRBox setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
		return this;
	}
	
	public Optional<Integer[]> getCoordinates() {
		return Optional.ofNullable(coordinates);
	}
	public OCRBox setCoordinates(Integer[] coordinates) {
		this.coordinates = coordinates;
		return this;
	}
	
}
