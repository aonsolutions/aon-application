package com.esferalia.aon.gwt.fiscal.client.mod390.e2022;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2022.Model3902022.Model3902022Callback;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902022;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

abstract class PageAbs extends SimpleLayoutPanel {
	
	private Model3902022Callback callback;
	
	PageAbs(Model3902022Callback callback) {
		this.callback = callback;	
	}
	
	Model3902022Callback getCallback() {
		return callback;
	}
	
	Mod3902022 getModel() {
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
}
