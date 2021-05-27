package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.Deduction.Type;

public class Deduction extends
		Item<com.esferalia.aon.gwt.payroll.shared.Deduction.Type> {

	@Override
	public Type getType() {
		int index = typeListBox.getSelectedIndex();
		return index == 0 ? null : Type.valueOf(Type.class,
				typeListBox.getValue(index));
	}
	
	// A bit suboptimal.
	@Override
	public void setType(Type type) {
		for (int i = 0; i < typeListBox.getItemCount(); i++) {
			if (type.name().equals(typeListBox.getValue(i))) {
				typeListBox.setSelectedIndex(i);
				return;
			}
		}
	}

	@Override
	protected void initTypeListBox() {
		typeListBox.addItem("-");
		addType(Type.ADVANCE_PAYMENT);
		addType(Type.IN_KIND);
		addType(Type.OTHER);
		addType(Type.EMBARGO);
	}

	private void addType(Type type) {
		typeListBox.addItem(type.getDescription(), type.name());
	}

}
