package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;

public class CategoryDraft extends AgreementDraft {

	public void setCategoryDraftObject(CategoryDraftObject categoryDraftObject) {
		super.setAgreementDraftObject(categoryDraftObject);
	}

	@Override
	public void setAgreementDraftObject(
			AgreementDraftObject agreementDraftObject) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Widget createWidget4Level(Level level, LevelEditor levelEditor) {
		Widget w = super.createWidget4Level(level, levelEditor);
		if ( !isMyLevel(level))
			return w;
		
		((TextBox)w).setVisibleLength(3);
		w.setStyleName(AON.AON_ICON_EMPLOYEE, true);
		w.getElement().getStyle().setPaddingLeft(16, Unit.PX);
		w.getElement().getStyle().setProperty("width", "auto");
		return w;
	}
	
	@Override
	protected void formatRow(Level level, int row, RowFormatter rowFormatter) {
		if ( isMyLevel(level) )
			rowFormatter.addStyleName(row, "aon-dataTable-row-current");
	}

	protected CategoryDraftObject getCategoryDraftObject() {
		return (CategoryDraftObject) getAgreementDraftObject();
	}
	
	private boolean isMyLevel(Level level) {
		return getCategoryDraftObject().getCategoryDraft().getLevelId() == level.getId();
	}
}
