package com.esferalia.aon.gwt.payroll.client;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.payroll.client.Employees.Listener;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class MetaData extends ResizeComposite {

	interface Listener {
		void onBonusConceptsSelected();
		void onPaymentConceptsSelected();
		void onDeductionConceptsSelected();
		
	}

	interface Images extends ClientBundle {
		ImageResource concept();

		ImageResource payment();

		ImageResource deduction();

		ImageResource segsocial();
	}

	interface Templates extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template("<img src=\"{0}\"/>")
		SafeHtml img(SafeUri uri);
	}

	public static class Resources {

		private static final Images IMAGES = GWT.create(Images.class);
		private static final Templates TEMPLATES = GWT.create(Templates.class);

		public String concept() {
			return TEMPLATES.img(IMAGES.concept().getSafeUri()).asString();
		}

		public String payment() {
			return TEMPLATES.img(IMAGES.payment().getSafeUri()).asString();
		}

		public String deduction() {
			return TEMPLATES.img(IMAGES.deduction().getSafeUri()).asString();
		}

		public String segsocial() {
			return TEMPLATES.img(IMAGES.segsocial().getSafeUri()).asString();
		}

	}

	interface Binder extends UiBinder<Widget, MetaData> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	Tree tree;
	@UiField
	ScrollPanel scrollPanel;
	@UiField
	Button viewButton;
	@UiField
	Button collapseAllButton;
	
	@UiField
	TreeItem bonusConceptsTreeItem;
	@UiField
	TreeItem paymentConceptsTreeItem;
	@UiField
	TreeItem deductionConceptsTreeItem;

	private List<Listener> listeners;

	public MetaData() {
		listeners = new LinkedList<Listener>();
		initWidget(binder.createAndBindUi(this));
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	// -------------------------------------------------------------- UiHandlers

	@UiHandler("tree")
	void onTreeItemSelected(SelectionEvent<TreeItem> event){
		TreeItem selectedItem = event.getSelectedItem();
		if ( selectedItem == bonusConceptsTreeItem )
			onBonusConceptsTreeItemSelected();
		else if ( selectedItem == paymentConceptsTreeItem )
			onPaymentConceptsTreeItemSelected();
		else if ( selectedItem == deductionConceptsTreeItem )
			onDeductionConceptsTreeItemSelected();
	}
	
	private void onBonusConceptsTreeItemSelected(){
		for (Listener listener : listeners)
			listener.onBonusConceptsSelected();
	}
	
	private void onDeductionConceptsTreeItemSelected(){
		for (Listener listener : listeners)
			listener.onDeductionConceptsSelected();
	}
	
	private void onPaymentConceptsTreeItemSelected(){
		for (Listener listener : listeners)
			listener.onPaymentConceptsSelected();
	}
	
	
	

}
