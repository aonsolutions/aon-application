package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree.FiscalNodeWidget;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Model131Form extends ResizeComposite implements FiscalNodeWidget<Mod131>{

	interface Model131FormBinder extends
			UiBinder<Widget, Model131Form> {
	}

	private static final Model131FormBinder panelBinder = GWT.create(Model131FormBinder.class);
	
	TreeNode<Mod131> node;
	 
	@UiField
	Button saveButton;
	@UiField
	Button calculateButton;
	
	@UiField
	TextBox document;
	
	@UiField
	TextBox name;
	
	@UiField
	TextBox surname;
	
	@UiField
	TextBox period;
	
	@UiField
	TextBox year;
	
	@UiField
	TextBox ep1;
	@UiField
	TextBox ny1;
	@UiField
	TextBox pr1;
	@UiField
	TextBox rs1;
	@UiField
	TextBox ep2;
	@UiField
	TextBox ny2;
	@UiField
	TextBox pr2;
	@UiField
	TextBox rs2;
	@UiField
	TextBox ep3;
	@UiField
	TextBox ny3;
	@UiField
	TextBox pr3;
	@UiField
	TextBox rs3;
	@UiField
	TextBox ep4;
	@UiField
	TextBox ny4;
	@UiField
	TextBox pr4;
	@UiField
	TextBox rs4;
	@UiField
	TextBox ep5;
	@UiField
	TextBox ny5;
	@UiField
	TextBox pr5;
	@UiField
	TextBox rs5;

	public Model131Form() {
		Widget ui = panelBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	@Override
	public void select(TreeNode<Mod131> node) {
		this.node = node;
		if ( node.getTreeObject().getId() != null) {
			FiscalTree.FISCAL_SERVICE.getMod131(FiscalTree.getCurrentDomainName(), 
					FiscalTree.getCurrentDomain(), node.getTreeObject().getId()
					,new AsyncCallback<Mod131>() {

						@Override
						public void onSuccess(Mod131 result) {
							populate(result);
						}
						@Override
						public void onFailure(Throwable caught) {
							DialogMessages.alertErrorWidget(caught.getMessage());
						}
			});
		} else {
			populate(node.getTreeObject());
		}
	}

	private void populate(Mod131 mod131) {
		node.setTreeObject( mod131 );
		saveButton.setEnabled(isEnabled(mod131));
		calculateButton.setEnabled(isEnabled(mod131));
	}
	

	private boolean isEnabled(Mod131 mod131) {
		return ( mod131.getYear() >= 2015 );
	}


	@UiHandler("calculateButton")
	void onCalculateButtonClick(ClickEvent event) {
		if (Window.confirm( FiscalTree.MSG.calculateAction())) {
			Window.alert("CALCULATE");
		}
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		if (Window.confirm( FiscalTree.MSG.saveAction())) {
			Window.alert("SAVE");
		}
	}

}
