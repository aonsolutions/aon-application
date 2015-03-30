package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree.FiscalNodeWidget;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class FiscalModelForm extends ResizeComposite implements FiscalNodeWidget<FiscalModel>{
	
	public static class ActivityInput {
		private String label;
		private Widget widget;
		private Integer infoKey;
		private Integer infoKeyType;
		
		public String getLabel() {
			return this.label;
		}
		public ActivityInput setLabel(String label) {
			this.label = label;
			return this;
		}

		public Widget getWidget() {
			return this.widget;
		}
		public ActivityInput setWidget(Widget widget) {
			this.widget = widget;
			return this;
		}
		public Integer getInfoKey() {
			return infoKey;
		}
		public ActivityInput setInfoKey(Integer infoKey) {
			this.infoKey = infoKey;
			return this;
		}
		public Integer getInfoKeyType() {
			return infoKeyType;
		}
		public ActivityInput setInfoKeyType(Integer infoKeyType) {
			this.infoKeyType = infoKeyType;
			return this;
		}
		public String getValue() {
			if (widget instanceof TextBox) {
				return ((TextBox) widget).getValue();	
			} else if (widget instanceof ListBox) {
				return ((ListBox) widget).getSelectedValue();
			}
			Window.alert("Unknown widget");
			return null;
		}
		public void setValue(String v) {
			if (widget instanceof TextBox) {
				((TextBox) widget).setValue(v);	
			} else if (widget instanceof ListBox) {
				ListBox l = (ListBox) widget;
				for (int i = 0; i < l.getItemCount(); i++) {
					if (AonStringUtils.equals(l.getValue(i),v)) {
						l.setSelectedIndex(i);
						break;
					}
				}
			} else {
				Window.alert("Unknown widget");
			}
		}
	}

	interface FiscalModelFormBinder extends
			UiBinder<Widget, FiscalModelForm> {
	}

	private static final FiscalModelFormBinder panelBinder = GWT
			.create(FiscalModelFormBinder.class);
	
	TreeNode<FiscalModel> node;
	 
	@UiField
	Button saveButton;
	@UiField
	Button calculateButton;
	
	@UiField
	Label year;
	

	public FiscalModelForm() {
		Widget ui = panelBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	@Override
	public void select(TreeNode<FiscalModel> node) {
		this.node = node;
		if ( node.getTreeObject().getId() != null) {
			FiscalTree.FISCAL_SERVICE.getFiscalModel(FiscalTree.getCurrentDomainName(), 
					FiscalTree.getCurrentDomain(), node.getTreeObject().getId()
					,new AsyncCallback<FiscalModel>() {

						@Override
						public void onSuccess(FiscalModel result) {
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

	private void populate(FiscalModel fiscalModel) {
		node.setTreeObject( fiscalModel );
		saveButton.setEnabled(isEnabled(fiscalModel));
		calculateButton.setEnabled(isEnabled(fiscalModel));
		year.setText(AonNumberUtils.toString(fiscalModel.getYear()));
	}
	

	private boolean isEnabled(FiscalModel fiscalModel) {
		return ( fiscalModel.getYear() >= 2015 );
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
