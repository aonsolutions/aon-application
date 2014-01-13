package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.Bonus.Type;

public class Bonus extends
		Item<com.esferalia.aon.gwt.payroll.shared.Bonus.Type> {

	@Override
	public Type getType() {
		return Type.valueOf(Type.class,
				typeListBox.getValue(typeListBox.getSelectedIndex()));
	}
	
	// A bit suboptimal.
	@Override
	public void setType(Type type) {
		typeListBox.setSelectedIndex(type.ordinal());
	}

	@Override
	protected void initTypeListBox() {
		for (Type  type : Type.class.getEnumConstants())
			typeListBox.insertItem(type.getDescription(), type.name(), type.ordinal());
	}


}
