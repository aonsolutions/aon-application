package com.esferalia.aon.gwt.fiscal.client.finance.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.utilities.ActivityIntegrityItem;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class ActivityIntegrityCheck extends OptionBase {
	
	private AonLayoutPanel dockPanel;
	private SimpleLayoutPanel content;
	private ScrollPanel container;
	
	protected ActivityIntegrityCheck(final FinanceUtilitiesModuleOptions options, Domain domain) {
		super(options, domain);
		dockPanel = new  AonLayoutPanel(Unit.PX);
		setContent(dockPanel);
		
		FinanceUtilitiesModule.COMMON_SERVICE.getAonConfiguration(options.getOccam(), new AsyncCallback<AonConfiguration>() {
			@Override
			public void onSuccess(AonConfiguration config) {
				options.setConfiguration(config);
				content = new SimpleLayoutPanel();
				content.setStyleName(AON.CSS.aonBorderTop());
				
				container = new ScrollPanel();
				container.setStyleName(AON.CSS.aonScrollArea());
				content.add(container);
				dockPanel.add(content);
			}
			
			@Override 
			public void onFailure(Throwable caught) {
				dockPanel.showErrorPanel(AON.MSG.loadError( getOptionDescription() ));
			}
		});
		
	}
	
	@Override
	public String getOptionDescription() {
		return AonStringUtils.BULLET + " Integridad del dato actividad en facturas y asientos.";
	}

	@Override
	protected AonToolbar getToolbarPanel() {
		AonToolbar toolbarPanel = super.getToolbarPanel();
		final AonToolbarButton refresh = new AonToolbarButton(AON.MSG.refresh(), AON.CSS.aonIconSearch());
		refresh.addClickHandler(event -> run() );
		toolbarPanel.add(refresh);

		return toolbarPanel;
	}

	public void run() {
		FinanceUtilitiesModule.SERVICE.activityIntegrity(getOptions().getOccam(), getDomain().getId(), new AsyncCallback<FinanceUtilitiesResult>(){

			@Override
			public void onFailure(Throwable caught) {
				openFootPanelIfNeeded();
				showErrorPanel(caught.getMessage());
			}

			@Override
			public void onSuccess(FinanceUtilitiesResult result) {
				cleanErrorPanel();
				container.setWidget( paintResults(result) );
			}
		});
	}

	@Override
	protected Widget paintResults(FinanceUtilitiesResult result) {
		FlowPanel tabContainer = new FlowPanel();
		if (AonCollectionUtils.isNotEmpty(result.getItems())) {
			AonDisplayGrid tab = new  AonDisplayGrid();
			String noActivity = "Sin Actividad";
			tab.addHeaderRow()
				.addCell(new Label(""), AON.CSS.aonWidth30())
				.addCell(new Label("Factura"), AON.CSS.aonWidth100())
				.addCell(new Label("Actividad Factura."), AON.CSS.aonWidth300())
				
				.addCell(new Label("Id Apunte"), AON.CSS.aonWidth100())
				.addCell(new Label("Actividad Apunte."), AON.CSS.aonWidth300())
				.addCell(new Label(""), AON.CSS.aonWidth30())
			;
			AonCollectionUtils.stream(result.getItems())
				.map( it -> (ActivityIntegrityItem) it)
				.forEach( it -> {
					AonTableButton invoiceOK = new AonTableButton( "Modificar APUNTE", AON.CSS.aonIconMoveRight() );
					AonTableButton entryOK = new AonTableButton( "Modificar FACTURA", AON.CSS.aonIconMoveLeft() );
					AonDisplayGridRow row = tab.addRow();
					Label invoiceActLabel = new Label( AonStringUtils.defaultIfBlank(it.getInvoiceActivityRef(), noActivity) ); 
					Label entryActLabel = new Label( AonStringUtils.defaultIfBlank(it.getAccountEntryActivityRef(), noActivity));
					invoiceOK.addClickHandler( e -> change( it, true, invoiceOK, entryOK, invoiceActLabel, entryActLabel) );
					entryOK.addClickHandler( e -> change( it, false, invoiceOK, entryOK, invoiceActLabel, entryActLabel) );
					row
						.addCell(invoiceOK, AON.CSS.aonWidth30())
						.addCell(new Label( it.getInvoiceRef()))
						.addCell(invoiceActLabel)
						
						.addCell(new AonIntegerLabel( it.getAccountEntryId()))
						.addCell(entryActLabel)
						.addCell(entryOK, AON.CSS.aonWidth30())
				;
			});
			tabContainer.add(tab);
		} else {
			Label noData = new Label( AON.MSG.noData());
			noData.setStyleName(AON.AON_CSS.aonTextCenter());
			noData.addStyleName(AON.AON_CSS.aonBold());
			noData.addStyleName(AON.AON_CSS.aonMarginTop());
			tabContainer.add(noData);	
		}
		
		return tabContainer;
	}

	private void change(ActivityIntegrityItem it, boolean useInvoiceActivity, AonTableButton invoiceOK, AonTableButton entryOK
			,Label invoiceLabel, Label entryLabel) {
		invoiceOK.setEnabled(false);
		entryOK.setEnabled(false);
		FinanceUtilitiesModule.SERVICE.activityIntegrityFix(getOptions().getOccam(), it.getInvoiceId(), useInvoiceActivity, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				openFootPanelIfNeeded();
				showErrorPanel(caught.getMessage());
			}
				@Override
			public void onSuccess(Void v) {
				if ( useInvoiceActivity) {
					entryLabel.setText( invoiceLabel.getText());
					entryLabel.setStyleName( AON.CSS.aonBackgroundHighlightedGreen());
				} else {
					invoiceLabel.setText( entryLabel.getText());
					invoiceLabel.setStyleName( AON.CSS.aonBackgroundHighlightedGreen());
				}
			}
		});
	}
}
