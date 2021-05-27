package net.aonsolutions.payroll.report;

import java.util.List;
import java.util.stream.Collectors;

public class Values<T extends HasAmount> {
	private List<T> values; 
	
	public Values(List<T> values) {
		super();
		this.values = values;
	}

	public List<T> getValues() {
		return values;
	}
	
	public double getAmount() {
		return values.stream().collect(Collectors.summingDouble(p -> p.getAmount()));
	}
}