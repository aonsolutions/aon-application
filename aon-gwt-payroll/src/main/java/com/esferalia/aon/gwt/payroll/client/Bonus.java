package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.Bonus.Type;

public class Bonus extends
		Item<com.esferalia.aon.gwt.payroll.shared.Bonus.Type> {

	@Override
	public Type getType() {
		int index = typeListBox.getSelectedIndex();
		return index == 0 ? null : Type.valueOf(Type.class,
				typeListBox.getValue(index));
	}

	// A bit suboptimal.
	@Override
	public void setType(Type type) {
		typeListBox.setSelectedIndex(type == null ? 0 : type.ordinal() + 1);
	}

	@Override
	protected void initTypeListBox() {
		typeListBox.insertItem("-", 0);
		for (Type type : Type.class.getEnumConstants())
			typeListBox.insertItem(type.getDescription(), type.name(),
					type.ordinal() + 1);
	}

}
