package net.aonsolutions.infovox.model;

import java.util.Optional;

public class OCRField {

	private String name;
	private String prefix;
	private Integer index;
	private Integer groupIndex;
	private Integer splitIndex;

	public Optional<String> getName() {
		return Optional.ofNullable(name);
	}
	public OCRField setName(String name) {
		this.name = name;
		return this;
	}

	public Optional<String> getPrefix() {
		return Optional.ofNullable(prefix);
	}
	public OCRField setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public Optional<Integer> getIndex() {
		return Optional.ofNullable(index);
	}
	public OCRField setIndex(Integer index) {
		this.index = index;
		return this;
	}

	public Optional<Integer> getGroupIndex() {
		return Optional.ofNullable(groupIndex);
	}
	public OCRField setGroupIndex(Integer groupIndex) {
		this.groupIndex = groupIndex;
		return this;
	}

	public Optional<Integer> getSplitIndex() {
		return Optional.ofNullable(splitIndex);
	}
	public OCRField setSplitIndex(Integer splitIndex) {
		this.splitIndex = splitIndex;
		return this;
	}
}
