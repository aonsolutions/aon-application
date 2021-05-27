package com.esferalia.aon.occam.api.model.stat;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonNumberUtils;

public class StatFilterItem implements Serializable, Cloneable {

	public static interface IFilterItemVisitor {
		void visit(IStatFilterItemVisitor statFilterItemVisitor,StatFilterItem item);
	}

	public static enum StatFilterType {
		 INVOICE_TYPE("Tipo Factura",new IFilterItemVisitor() {

			@Override
			public void visit(IStatFilterItemVisitor statFilterItemVisitor,StatFilterItem item) {
				statFilterItemVisitor.visitInvoiceTypeCondition(item);
			}
		 })
		,PRODUCT_CATEGORY("Categor\u00EDa",new IFilterItemVisitor() {

			@Override
			public void visit(IStatFilterItemVisitor statFilterItemVisitor,StatFilterItem item) {
				statFilterItemVisitor.visitProductCategoryCondition(item);
			}
		 })
		,PRODUCT_TAG("Etiquetas",new IFilterItemVisitor() {

			@Override
			public void visit(IStatFilterItemVisitor statFilterItemVisitor,StatFilterItem item) {
				statFilterItemVisitor.visitProductTagCondition(item);
			}
		 })
		,PRODUCT_BRAND("Marcas",new IFilterItemVisitor() {

			@Override
			public void visit(IStatFilterItemVisitor statFilterItemVisitor,StatFilterItem item) {
				statFilterItemVisitor.visitProductBrandCondition(item);
			}
		 })
		,SEGMENT("Segmentaci\u00F3n",new IFilterItemVisitor() {

			@Override
			public void visit(IStatFilterItemVisitor statFilterItemVisitor,StatFilterItem item) {
				statFilterItemVisitor.visitSegmentCondition(item);
			}
		 })
		,WORKPLACE("Centro de trabajo",new IFilterItemVisitor() {

			@Override
			public void visit(IStatFilterItemVisitor statFilterItemVisitor,StatFilterItem item) {
				statFilterItemVisitor.visitWorkplaceCondition(item);
			}
		 })
		,SELLER("Agente comercial",new IFilterItemVisitor() {

			@Override
			public void visit(IStatFilterItemVisitor statFilterItemVisitor,StatFilterItem item) {
				statFilterItemVisitor.visitSellerCondition(item);
			}
		 })
		;

		private String name;
		private IFilterItemVisitor filterItemVisitor;  

		private StatFilterType(String name,IFilterItemVisitor filterItemVisitor) {
			this.name = name;
			this.filterItemVisitor = filterItemVisitor;
		}

		public String getName() {
			return name;
		}
		public void visit(IStatFilterItemVisitor statFilterItemVisitor,StatFilterItem item) {
			filterItemVisitor.visit(statFilterItemVisitor,item);
		}
		
		public static StatFilterType safeValueOf( Byte i ) {
			if (i == null) return null;
			return safeValueOf( i.intValue() ); 
		}
		public static StatFilterType safeValueOf( Integer i ) {
			if (i == null) return null;
			if (i < 0 || i >= StatFilterType.values().length) return null;
			return StatFilterType.values()[i];
		}
		
	}

	private static final long serialVersionUID = 8321751053437854437L;

	private StatFilterType type;
	private String id;
	private String label;
	private boolean selected = false;
	
	
	public boolean isSelected() {
		return selected;
	}
	
	public StatFilterItem setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}

	public StatFilterType getType() {
		return type;
	}

	public StatFilterItem setType(StatFilterType type) {
		this.type = type;
		return this;
	}

	public String getId() {
		return id;
	}

	public StatFilterItem setId(String id) {
		this.id = id;
		return this;
	}

	public StatFilterItem setId(Integer id) {
		this.setId(AonNumberUtils.toString(id));
		return this;
	}

	public String getLabel() {
		return label;
	}

	public StatFilterItem setLabel(String label) {
		this.label = label;
		return this;
	}
	
	public StatFilterItem clone() {
		return new StatFilterItem()
			.setType( this.getType())
			.setId(this.getId())
			.setLabel(this.getLabel())
			.setSelected(this.isSelected());
	}
}
