package com.esferalia.aon.gwt.fiscal.client.panel;

import com.esferalia.aon.gwt.common.client.widget.FullDocument;
import com.esferalia.aon.gwt.common.client.widget.MunicipalityListBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.common.client.widget.StreetTypeListBox;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class EnterpriseMatrixPanel extends ResizeComposite {

	interface EnterpriseFormBinder extends
			UiBinder<Widget, EnterpriseMatrixPanel> {
	}

	private static final EnterpriseFormBinder panelBinder = GWT
			.create(EnterpriseFormBinder.class);
	
	String domainName;
	int domainId;

	public EnterpriseMatrixPanel() {
		
		Widget ui = panelBinder.createAndBindUi(this);
		initWidget(ui);
	}

	public void setDomainId(int domainId) {
		this.domainId = domainId;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	public void setEnterprise(Enterprise enterprise) {
		FiscalPanel.COMMON_SERVICE.getEnterprise(domainName, domainId, enterprise.getId()
				,new AsyncCallback<Enterprise>() {

					@Override
					public void onSuccess(Enterprise result) {
						// TODO
					}
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
					}
		});
	}

}
