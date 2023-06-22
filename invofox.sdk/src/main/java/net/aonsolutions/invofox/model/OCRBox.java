package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.Optional;

public class OCRBox implements Serializable {
	
	private static final long serialVersionUID = 6066950745149021691L;
	
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
