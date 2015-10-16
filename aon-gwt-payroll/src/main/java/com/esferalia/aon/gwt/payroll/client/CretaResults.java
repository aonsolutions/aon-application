package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.payroll.shared.CretaService;
import com.esferalia.aon.gwt.payroll.shared.CretaService.JsEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class CretaResults extends Composite {

	interface Binder extends UiBinder<Widget, CretaResults> {

	}

	private static final Binder binder = GWT.create(Binder.class);

	private Images images;

	@UiField
	Tree errorsTree;

	@UiField
	Tree warningsTree;

	private TreeItem errorsItem;
	private TreeItem warningsItem;

	public CretaResults() {
		images = GWT.create(Images.class);

		initWidget(binder.createAndBindUi(this));

		errorsItem = new TreeItem(imageItemHTML(images._error(), "<B>Errores</B>"));
		errorsTree.addItem(errorsItem);

		warningsItem = new TreeItem(imageItemHTML(images.warn(), "<B>Avisos</B>"));
		warningsTree.addItem(warningsItem);
	}

	public void addErrors(CretaService.JsEvent errors []) {
		for (CretaService.JsEvent error : errors)
			errorsItem.addItem(new TreeItem(
					imageItemHTML(images._error(), error.getMessage())));
		errorsItem.setHTML(imageItemHTML(images._error(), "<B>Errores<B> ("+errorsItem.getChildCount() +")"));

		errorsItem.setVisible(errorsItem.getChildCount()>0);
		errorsItem.setState(errorsItem.getChildCount()>0);
	}

	public void addWarnings(CretaService.JsEvent warnings []) {
		for (CretaService.JsEvent warning : warnings)
			warningsItem.addItem(new TreeItem(
					imageItemHTML(images.warn(), warning.getMessage())));
		warningsItem.setHTML(imageItemHTML(images.warn(), "<B>Avisos<B> ("+warningsItem.getChildCount() +")"));
		
		warningsItem.setVisible(warningsItem.getChildCount()>0);
		warningsItem.setState(warningsItem.getChildCount()>0);
	}
	// ------------------------------------------------------------------------

	/**
	 * Generates HTML for a tree item with an attached icon.
	 */
	private static SafeHtml imageItemHTML(ImageResource imageProto,
			String title) {
		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		builder.append(AbstractImagePrototype.create(imageProto).getSafeHtml());
		builder.append(' ');
		builder.appendHtmlConstant(title);
		return builder.toSafeHtml();
	}

}
