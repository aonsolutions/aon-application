package net.aonsolutions.infovox.model;

public class OCRField {

	private String name;
	private String prefix;
	private Integer index;
	private Integer groupIndex;
	private Integer splitIndex;

	public String getName() {
		return name;
	}
	public OCRField setName(String name) {
		this.name = name;
		return this;
	}

	public String getPrefix() {
		return prefix;
	}
	public OCRField setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public Integer getIndex() {
		return index;
	}
	public OCRField setIndex(Integer index) {
		this.index = index;
		return this;
	}

	public Integer getGroupIndex() {
		return groupIndex;
	}
	public OCRField setGroupIndex(Integer groupIndex) {
		this.groupIndex = groupIndex;
		return this;
	}

	public Integer getSplitIndex() {
		return splitIndex;
	}
	public OCRField setSplitIndex(Integer splitIndex) {
		this.splitIndex = splitIndex;
		return this;
	}
}
