package com.esferalia.aon.gwt.fiscal.client.invoice.console;

import java.util.HashMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceModuleOptions;
import com.esferalia.aon.occam.api.model.finance.InvoiceCollectionInfo;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceConsoleAnalysis;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

class InvoiceConsoleAnalysisPanel extends SimpleLayoutPanel {

	private FlowPanel container = new FlowPanel();
	 
	public InvoiceConsoleAnalysisPanel() {
		ScrollPanel scroll = new ScrollPanel();
		scroll.setWidget( container );
		setWidget(scroll);
	}

	public void paint(InvoiceModuleOptions opts, InvoiceConsoleAnalysis ica) {
		container.clear();
		
		boolean something = false;
		AonToolbar toolbar = new AonToolbar("Desglose de informaci\u00F3 de la selecci\u00F3n de facturas");
		
		AonDisplayGrid grid = new AonDisplayGrid();
		grid.addStyleName( AON.CSS.aonMarginTop() );
		grid.addStyleName( AON.CSS.aonBlockCenter() );
		
		if ( ica.getInvoiceInfo() != null && ica.getInvoiceInfo().getTotalCount() > 0 ) {
			something = true;
			grid.addRow()
				.addCell( new Label("Facturas Procesadas:"))
				.addCell( new AonIntegerLabel(ica.getInvoiceInfo().getTotalCount()))
				.addCell( new AonDoubleLabel(ica.getInvoiceInfo().getTotalAmount()))
			;
		}
		if ( ica.getProformaInfo() != null && ica.getProformaInfo().getTotalCount() > 0 ) {
			something = true;
			grid.addRow()
				.addCell( new Label("Facturas Proforma:"))
				.addCell( new AonIntegerLabel(ica.getProformaInfo().getTotalCount()))
				.addCell( new AonDoubleLabel(ica.getProformaInfo().getTotalAmount()))
			;
			
		}
		container.add( grid );

		for ( InvoiceCommunicationType type : ica.getTypesInfo().keySet() ) {
			String typeDesc = type == null ? "Sin Comunicaci\u00F3n" : type.getDescription();
			something = true;
			AonDisplayGrid gridType = new AonDisplayGrid();
			gridType.addStyleName( AON.CSS.aonMarginTop() );
			gridType.addStyleName( AON.CSS.aonBlockCenter() );
			gridType.addHeaderRow().addCell( new Label(typeDesc ) );
			HashMap<InvoiceCommunicationStatus, InvoiceCollectionInfo> statusMap = ica.getTypesInfo().get(type);
			AonCollectionUtils.stream(statusMap)
				.forEach( entry -> {
					InvoiceCommunicationStatus status = entry.getKey();
					InvoiceCollectionInfo info = entry.getValue();
					gridType.addRow()
						.addCell( new Label( status == null ? "SIN ESTADO" : status.getDescription() ) )
						.addCell( new AonIntegerLabel( info.getTotalCount() ) )
						.addCell( new AonDoubleLabel( info.getTotalAmount() ) )
					;
				});
			container.add( gridType );	
		}
		
		
		
		if ( !something ) {
			Label noInfo = new Label("No hay informaci\u00F3n disponible para la selecci\u00F3n de facturas.");
			noInfo.setStyleName( AON.CSS.aonBlockCenter() );
			noInfo.addStyleName( AON.CSS.aonMarginTop() );
			noInfo.addStyleName( AON.CSS.aonBold() );
			container.add( noInfo );
		} else {
			container.insert( toolbar, 0 );
		}
		
	}

}
