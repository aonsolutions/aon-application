package net.aonsolutions.infovox.model;

import java.util.Optional;

public class OCRReading {
	private OCRNumber previous = null;
	private OCRNumber current = null;
	private OCRNumber usage = null;

	public Optional<OCRNumber> getPrevious() {
		return Optional.ofNullable(previous);
	}

	public OCRReading setPrevious(OCRNumber previous) {
		this.previous = previous;
		return this;
	}

	public Optional<OCRNumber> getCurrent() {
		return Optional.ofNullable(current);
	}

	public OCRReading setCurrent(OCRNumber current) {
		this.current = current;
		return this;
	}

	public Optional<OCRNumber> getUsage() {
		return Optional.ofNullable(usage);
	}

	public OCRReading setUsage(OCRNumber usage) {
		this.usage = usage;
		return this;
	}

}
