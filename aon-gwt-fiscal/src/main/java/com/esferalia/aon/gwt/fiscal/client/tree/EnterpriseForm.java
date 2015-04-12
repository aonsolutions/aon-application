package com.esferalia.aon.gwt.fiscal.client.tree;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.FullDocument;
import com.esferalia.aon.gwt.common.client.widget.MunicipalityListBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.common.client.widget.StreetTypeListBox;
import com.esferalia.aon.gwt.fiscal.client.tree.FiscalTree.FiscalNodeWidget;
import com.esferalia.aon.gwt.fiscal.client.tree.TreeNode.TreeNodeCallback;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EnterpriseForm extends ResizeComposite implements FiscalNodeWidget<Enterprise> {

	interface EnterpriseFormBinder extends
			UiBinder<Widget, EnterpriseForm> {
	}

	private static final EnterpriseFormBinder panelBinder = GWT
			.create(EnterpriseFormBinder.class);
	
	TreeNodeCallback<Enterprise> callback;
	Enterprise enterprise;
	
	@UiField
	TextBox name;
	@UiField
	TextBox alias;
	@UiField
	FullDocument fullDocument;
	
	@UiField
	StreetTypeListBox streetType;
	@UiField
	TextBox streetName;
	@UiField
	TextBox streetNumber;
	@UiField
	TextBox address2;
	@UiField
	TextBox address3;
	@UiField
	TextBox zip;
	@UiField
	ProvinceListBox province;
	@UiField
	MunicipalityListBox town;
	@UiField
	TextBox city;
	@UiField
	TextBox phone;
	@UiField
	TextBox fax;
	@UiField
	TextBox email;
	@UiField
	TextBox web;

	public EnterpriseForm() {
		
		Widget ui = panelBinder.createAndBindUi(this);
		initWidget(ui);
		
		name.setEnabled(false);
		alias.setEnabled(false);
		fullDocument.setEnabled(false);
		streetType.setEnabled(false);
		streetName.setEnabled(false);
		streetNumber.setEnabled(false);
		address2.setEnabled(false);
		address3.setEnabled(false);
		zip.setEnabled(false);
		province.setEnabled(false);
		town.setEnabled(false);
		city.setEnabled(false);
		phone.setEnabled(false);
		fax.setEnabled(false);
		email.setEnabled(false);
		web.setEnabled(false);
		
	}

	@Override
	public void select(Enterprise enterprise) {
		this.enterprise = enterprise;
		FiscalTree.COMMON_SERVICE.getEnterprise(FiscalTree.getCurrentDomainName(), 
				FiscalTree.getCurrentDomain(), enterprise.getId()
				,new AsyncCallback<Enterprise>() {

					@Override
					public void onSuccess(Enterprise result) {
						if (result != null) {
							populate(result);
						} else {
							DialogMessages.alertErrorWidget(AON.MSG
									.unableToShowData("No se ha encontrado la empresa"));
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						DialogMessages.alertErrorWidget(AON.MSG
								.unableToShowData(caught.getMessage()));
					}
		});
	}
	
	@Override
	public void setCallback(TreeNodeCallback<Enterprise> callback) {
		this.callback = callback;
	}
	
	private void populate(Enterprise enterprise) {
		name.setValue(enterprise.getName());
		alias.setValue(enterprise.getAlias());
		fullDocument.setValue(enterprise.getDocumentType()
				,enterprise.getDocumentCountry(),enterprise.getDocument());
		streetType.setValue(enterprise.getStreetType());
		streetName.setValue(enterprise.getAddress());
		streetNumber.setValue(enterprise.getNumber());
		address2.setValue(enterprise.getAddress2());
		address3.setValue(enterprise.getAddress3());
		zip.setValue(enterprise.getZip());
		province.setValue(enterprise.getProvince());
		//town.setValue(enterprise.getTown());
		city.setValue(enterprise.getCity());
		phone.setValue(enterprise.getPhone());
		fax.setValue(enterprise.getFax());
		email.setValue(enterprise.getEmail());
		web.setValue(enterprise.getWeb());
	}

}
