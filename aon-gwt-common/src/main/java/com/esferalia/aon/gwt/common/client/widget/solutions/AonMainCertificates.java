package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCertificateDialog.AonCerticateDialogCallback;

public class AonMainCertificates extends AonCustomDockLayout {

	private Integer domainId;
	private String domainName;
	private String user;
	
	private AonMainCertificatesPanel aonMainCertificatesPanel;
	
	// ------------------------------------------------------ Constructor

	public AonMainCertificates(String domainName, Integer domainId, String user) {
		super("Certificados Digitales");
		
		this.domainName = domainName;
		this.domainId = domainId;
		this.user = user;
		
		addButtonsToolbar();
		
		aonMainCertificatesPanel = new AonMainCertificatesPanel(domainName, domainId, user);
		add(aonMainCertificatesPanel);
	}

	@Override
	protected void onClearFilter() {}

	// ------------------------------------------------------ Toolbar

	private void addButtonsToolbar() {
		AonToolbarButton newButton = new AonToolbarButton("Nuevo certificado", AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> {
			new AonCertificateDialog(domainName, domainId, user, new AonCerticateDialogCallback() {

				@Override
				public void onAccept() {
					aonMainCertificatesPanel.onModuleLoad();
				}

			});
		});

		addToolbarButton(newButton);
		hideSearchWidget();
	}

}
