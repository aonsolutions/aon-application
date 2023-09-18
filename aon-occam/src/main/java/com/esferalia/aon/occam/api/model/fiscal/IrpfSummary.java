package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.TreeMap;

import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.api.model.type.WithholdingTypeGroup;

public class IrpfSummary implements Serializable {
	
	private static final long serialVersionUID = 8724370961198393545L;
	
	private TreeMap<WithholdingTypeGroup,IrpfSummaryGroup> map = new TreeMap<>();

	public TreeMap<WithholdingTypeGroup, IrpfSummaryGroup> getMap() {
		return map;
	}
	
	public void add(IrpfBreakdown br) {
		map.computeIfAbsent(br.getWithholdingType().getGroup(), k -> new IrpfSummaryGroup().setGroup(k))
			.add( br );
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}
	

	public static class IrpfSummaryGroup implements Serializable {

		private static final long serialVersionUID = 6451561219776158446L;
		
		private WithholdingTypeGroup group;
		private TreeMap<WithholdingType,IrpfSummaryType> map = new TreeMap<>();

		public WithholdingTypeGroup getGroup() {
			return group;
		}

		public IrpfSummaryGroup setGroup(WithholdingTypeGroup group) {
			this.group = group;
			return this;
		}
		
		public TreeMap<WithholdingType,IrpfSummaryType> getMap() {
			return map;
		}
		
		public void add(IrpfBreakdown br) {
			map.computeIfAbsent(br.getWithholdingType(), k -> new IrpfSummaryType().setType(k))
				.add( br );
		}
	}

	public static class IrpfSummaryType implements Serializable {

		private static final long serialVersionUID = 6451561219776158446L;
		
		private WithholdingType type;
		private TreeMap<Double,IrpfSummaryPercent> map = new TreeMap<>();

		public WithholdingType getType() {
			return type;
		}

		public IrpfSummaryType setType(WithholdingType type) {
			this.type = type;
			return this;
		}
		
		public TreeMap<Double, IrpfSummaryPercent> getMap() {
			return map;
		}
		
		public void add(IrpfBreakdown br) {
			map.computeIfAbsent(br.getPercent(), k -> new IrpfSummaryPercent().setPercent(br.getPercent()))
				.add( br );
		}
	}

	public static class IrpfSummaryPercent implements Serializable {

		private static final long serialVersionUID = -3546569623736169471L;
		
		private double percent;
		private IrpfBreakdown output;
		private IrpfBreakdown input;

		public double getPercent() {
			return percent;
		}

		public IrpfSummaryPercent setPercent(double percent) {
			this.percent = percent;
			return this;
		}

		public IrpfBreakdown getInput() {
			return input;
		}

		public IrpfSummaryPercent setInput(IrpfBreakdown input) {
			this.input = input;
			return this;
		}
		
		public IrpfBreakdown getOutput() {
			return output;
		}
		
		public IrpfSummaryPercent setOutput(IrpfBreakdown output) {
			this.output = output;
			return this;
		}
		
		public void add(IrpfBreakdown br) {
			IrpfBreakdown toAdd = br.isSales()?output:input;
			if ( toAdd == null ) {
				toAdd = new IrpfBreakdown()
					.setInvoiceType(br.getInvoiceType())
					.setWithholdingType(br.getWithholdingType())
					.setPercent(percent);
				if (br.isSales()) {
					output = toAdd;
				} else {
					input = toAdd;
				}
			}
			toAdd.setBase( toAdd.getBase() + br.getBase()); 
			toAdd.setQuota( toAdd.getQuota() + br.getQuota());
			toAdd.setDeductibleQuota( toAdd.getDeductibleQuota() + br.getDeductibleQuota());				
		}
	}
}
