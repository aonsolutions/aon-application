package com.esferalia.aon.gwt.fiscal.client.mod425.e2025;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonBoxLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable.AonDisplayTableRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod425.e2025.Model4252025.Model4252025Callback;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

abstract class PageAbs extends SimpleLayoutPanel {
	
	private Model4252025Callback callback;
	
	PageAbs(Model4252025Callback callback) {
		this.callback = callback;	
	}
	
	Model4252025Callback getCallback() {
		return callback;
	}
	
	Mod4252025 getModel() {
		return callback.getModel();
	}
	void calculateAndRefresh() {
		getModel().calculate();
		refresh();	
	}

	void refresh() {
		setValue();
	}

	void markAsDirty() {
		getCallback().markAsDirty();
	}
	
	protected Label getTitle(String text) {
		Label title = new Label(text);
		title.setStyleName(AON.CSS.aonMarginTop());
		title.addStyleName(AON.CSS.aonBold());
		title.addStyleName(AON.CSS.aonTextUppercase());
		title.addStyleName(AON.CSS.aonFontMedium());
		title.addStyleName(AON.CSS.aonWidthAlmostAll());
		title.addStyleName(AON.CSS.aonBlockCenter());
		title.addStyleName(AON.CSS.aonBorderBottom());
		return title;
	}

	protected Label getSubtitle(String text) {
		Label subtitle = new Label(text);
		subtitle.setStyleName(AON.CSS.aonMarginTop());
		subtitle.addStyleName(AON.CSS.aonBold());
		subtitle.addStyleName(AON.CSS.aonTextUppercase());
		subtitle.addStyleName(AON.CSS.aonWidthAlmostAll());
		subtitle.addStyleName(AON.CSS.aonBlockCenter());
		subtitle.addStyleName(AON.CSS.aonBorderBottom());
		return subtitle;
	}
	
	protected Label getSubsubtitle(String text) {
		Label subsubtitle = new Label(text);
		subsubtitle.setStyleName(AON.CSS.aonMarginTop());
		subsubtitle.addStyleName(AON.CSS.aonBold());
		subsubtitle.addStyleName(AON.CSS.aonWidthAlmostAll());
		subsubtitle.addStyleName(AON.CSS.aonBlockCenter());
		subsubtitle.addStyleName(AON.CSS.aonBorderBottom());
		return subsubtitle;
	}
	
	protected abstract void setValue();
	
	protected AonDisplayTable addTable(FlowPanel basePanel) {
		return addTable(basePanel, null);
	}
	
	protected AonDisplayTable addTable(FlowPanel basePanel, Label title) {
		if (title != null) {
			basePanel.add(title);	
		}

		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab);
		
		return tab;
	}

	protected void addRow(AonDisplayTable tab, String text, int boxNumber, AonDoubleBox... aonDoubleBoxes) {
		int boxCount = boxNumber;
		Label label = new Label(text);
		AonDisplayTableRow row = tab.addRow()
			.addCell(label, AON.CSS.aonBorderBottom(), AON.CSS.aonWidthAuto());
		
		for (AonDoubleBox aonDoubleBox : aonDoubleBoxes) {
			row.addCell(new AonBoxLabel(boxCount++), AON.CSS.aonWidth40())
			   .addCell(aonDoubleBox, AON.CSS.aonWidth120());
			if (!aonDoubleBox.isEnabled()) {
				label.addStyleName(AON.CSS.aonBold());
			}
		}
			
	}
	
}
