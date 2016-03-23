package com.esferalia.aon.occam.api.model.stat;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonNumberUtils;

public class StatFilterItem implements Serializable {

	public static enum StatFilterType {
		 INVOICE_TYPE("Tipo Factura")
		,PRODUCT_CATEGORY("Categor\u00EDa")
		,WORKPLACE("Centro de trabajo")
		;

		private String name;

		private StatFilterType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
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

}
