package net.aonsolutions.payroll.report;

import java.util.Collection;
import java.util.stream.Collectors;

import com.esferalia.aon.salary.enumeration.DeductionType;

public class  CompositeDeduction<I extends Deduction> implements Deduction {
	
	private Collection<I> items;
	
	public CompositeDeduction(Collection<I> items) {
		this.items = items;
	}


	@Override
	public String getName() {
		return items.stream()
				.map( i -> i.getName())
				.filter(s -> isNotBlank(s))
				.findFirst()
				.orElse(null);
	}


	@Override
	public double getAmount() {
		
		return items.stream().collect(Collectors.summingDouble(i -> i.getAmount()))
				;
	}

	@Override
	public String getDescription() {
		return items.stream()
				.map( i -> i.getDescription())
				.filter(s -> isNotBlank(s))
				.findFirst()
				.orElse(null);
	}

	@Override
	public String getExpression() {
		return items.stream()
				.map( i -> i.getExpression())
				.filter(s -> isNotBlank(s))
				.findFirst()
				.orElse(null);
	}

	@Override
	public DeductionType getType() {
		return items.stream()
				.map( i -> i.getType())
				.filter(b -> b != null)
				.findFirst()
				.orElse(null);
	}
	
	private boolean isNotBlank(String str) {
		if ( str == null )
			return false;
		
		if ( str.trim().isEmpty() )
			return false;
		
		return true;
	}
	
	
}
