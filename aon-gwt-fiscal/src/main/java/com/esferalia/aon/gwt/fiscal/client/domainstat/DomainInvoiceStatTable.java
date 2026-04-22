package com.esferalia.aon.gwt.fiscal.client.domainstat;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerLabel;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.JsDomainInvoiceStat;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.DomainInvoiceStatParams;
import com.esferalia.aon.watson.mutable.MutableBoolean;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

class DomainInvoiceStatTable extends ScrollPanel {
	private static final Logger LOGGER = Logger.getLogger( DomainInvoiceStatTable.class.getName() );
	private static final String SERVLET = "/aon_gwt_fiscal/roms/DomainInvoiceStatServlet";
																
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableBoolean searchEnabled = new MutableBoolean( true );
	private final FlowPanel container;
	private AonDisplayGrid grid;
	private int lastScrollPos = 0;
	
	DomainInvoiceStatTable(DomainInvoiceStatModuleOptions options, DomainInvoiceStatParams params) {
		this.setStyleName(AON.CSS.aonTextCenter());
		this.addStyleName(AON.CSS.aonScrollArea());
		this.addStyleName(AON.CSS.aonMarginBottom());
		
		container = new FlowPanel();
		this.setWidget(container);
		
		addScrollHandler(event -> {
			// ------------------------------------ Ignore scroll up.
			int oldScrollPos = lastScrollPos;
			lastScrollPos = getVerticalScrollPosition();
			if (oldScrollPos >= lastScrollPos) {
				return;
			}
			// -----------------------------------------------------
			if (isSearchEnabled()) {
				int maxScrollTop = getWidget().getOffsetHeight() - getOffsetHeight();
				if (lastScrollPos >= maxScrollTop) {
					disableSearch();
					doSearch(options,params);
				}
			}
		});
		onSearch(options, params);
	}
	
	private void addHeaderRow() {
		grid.addHeaderRow()
			.addCell( new Label("Documento"), AON.CSS.aonWidth150(),AON.CSS.aonTextLeft())
			.addCell( new Label("Nombre")	, AON.CSS.aonWidthAuto(),AON.CSS.aonTextLeft())
			
			.addCell( new Label("NAC"), AON.CSS.aonWidth80())
			.addCell( new Label("INT"), AON.CSS.aonWidth80())
			.addCell( new Label("EXT"), AON.CSS.aonWidth80())
			.addCell( new Label("CCM"), AON.CSS.aonWidth80())
			.addCell( new Label("ISP"), AON.CSS.aonWidth80())
			.addCell( new Label("RET"), AON.CSS.aonWidth80())
			
			.addCell( new Label("ENV"), AON.CSS.aonWidth80(),AON.CSS.aonBackgroundLigthBlue())
			.addCell( new Label("REC"), AON.CSS.aonWidth80(),AON.CSS.aonBackgroundLigthBlue())
			.addCell( new Label("SIM"), AON.CSS.aonWidth80(),AON.CSS.aonBackgroundLigthBlue())
			
			.addCell( new Label("PFR"), AON.CSS.aonWidth80(), AON.CSS.aonBackgroundLigthYellow())
			.addCell( new Label("ENV"), AON.CSS.aonWidth80(), AON.CSS.aonBackgroundLigthYellow())
			.addCell( new Label("REC"), AON.CSS.aonWidth80(), AON.CSS.aonBackgroundLigthYellow())
			.addCell( new Label("SIM"), AON.CSS.aonWidth80(), AON.CSS.aonBackgroundLigthYellow())
			
			.addCell( new Label("BORR"), AON.CSS.aonWidth80(), AON.CSS.aonBackgroundLigthGray())
			.addCell( new Label("PROC"), AON.CSS.aonWidth80(), AON.CSS.aonBackgroundLigthGray())
			.addCell( new Label("REVI"), AON.CSS.aonWidth80(), AON.CSS.aonBackgroundLigthGray())
			.addCell( new Label("PAPE"), AON.CSS.aonWidth80(), AON.CSS.aonBackgroundLigthGray())
		;
	}

	private boolean isSearchEnabled() {
		return searchEnabled.getValue();
	}
	private void disableSearch() {
		searchEnabled.setValue( false );
	}
	private void enableSearch() {
		searchEnabled.setValue( true );
	}
	private boolean isMoreData() {
		return (moreData.getValue() == 0 );
	}
	private void disableMoreData() {
		moreData.setValue(-1);
	}
	private void enableMoreData() {
		moreData.setValue(0);
	}

	private void onSearch(DomainInvoiceStatModuleOptions options, DomainInvoiceStatParams params) {
		enableMoreData();
		container.clear();
		
		grid = new AonDisplayGrid();
		grid.addStyleName(AON.CSS.aonMarginTop());
		grid.addStyleName(AON.CSS.aonTextRight());
		this.addHeaderRow();
		container.add(grid);

		offset.setValue(0);
		doSearch(options, params);
	}

	private void doSearch(DomainInvoiceStatModuleOptions options,DomainInvoiceStatParams params) {
		if (!isMoreData()) return;
		params.setOffset(offset.getValue());
		
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open(FormPanel.METHOD_POST, SERVLET);
		xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(new DomainInvoiceStatStateChangeHandler() );
		
		StringBuilder requestData = new StringBuilder();
		requestData.append("&"+IRequestParamsNames.DOMAIN_NAME	+"=" + options.getDomainName()  );
		requestData.append("&"+IRequestParamsNames.DOMAIN_ID  	+"=" + options.getDomain() );
		requestData.append("&"+IRequestParamsNames.USER			+"=" + options.getUser() );
		requestData.append("&"+IRequestParamsNames.DOMAIN_PARAMS+"=" + JsonParams.convert( params ));
		xhr.send(requestData.toString());
	}

	private class DomainInvoiceStatStateChangeHandler implements ReadyStateChangeHandler {
		DomainInvoiceStatStateChangeHandler() {
		}
		
		@Override
		public void onReadyStateChange(XMLHttpRequest xhr) {
			int state = xhr.getReadyState();
			if (state == XMLHttpRequest.DONE) {
				String text = xhr.getResponseText();
				try {
					giveResponse( text );
				} catch (IndexOutOfBoundsException e) {
					addMessage( e.getMessage() );
				}
				enableSearch();
			}
		}
		
		private void giveResponse(String text) {
			int count = 0;
			if (!JsonUtils.safeToEval(text)) {
				LOGGER.severe( "ERROR evaluating response" );
				LOGGER.info( text );
				addMessage( "ERROR de evaluaci\u00F3n" );
			}
			JavaScriptObject unk = JsonUtils.safeEval(text);
			JsArray<JsDomainInvoiceStat> array = unk.cast();
			if (array.length() == 0) {
				addMessage( AON.MSG.noData() );
			} else {
				for (; count < array.length(); count++ ) {
					JsDomainInvoiceStat stat = array.get(count);
					grid.addRow()
						.addCell( new Label(stat.getCompanyDocument()),AON.CSS.aonTextLeft())
						.addCell( new Label(stat.getCompanyName()),AON.CSS.aonTextLeft())
						
						.addCell( new AonIntegerLabel(stat.getNational()) )
						.addCell( new AonIntegerLabel(stat.getIntracommunity()) )
						.addCell( new AonIntegerLabel(stat.getExtracommunity()) )
						.addCell( new AonIntegerLabel(stat.getCanCeuMel()) )
						.addCell( new AonIntegerLabel(stat.getOtherISP()) )
						.addCell( new AonIntegerLabel(stat.getWithholding()) )
						
						.addCell( new AonIntegerLabel(stat.getUndeclaredIssued()) , AON.CSS.aonBackgroundLigthBlue())
						.addCell( new AonIntegerLabel(stat.getUndeclaredReceived()) , AON.CSS.aonBackgroundLigthBlue())
						.addCell( new AonIntegerLabel(stat.getUndeclaredSimplified()) , AON.CSS.aonBackgroundLigthBlue())
						
						.addCell( new AonIntegerLabel(stat.getProformas()), AON.CSS.aonBackgroundLigthYellow())
						.addCell( new AonIntegerLabel(stat.getUnrecordedIssued()), AON.CSS.aonBackgroundLigthYellow())
						.addCell( new AonIntegerLabel(stat.getUnrecordedReceived()) , AON.CSS.aonBackgroundLigthYellow())
						.addCell( new AonIntegerLabel(stat.getUnrecordedSimplified()) , AON.CSS.aonBackgroundLigthYellow())
						
						.addCell( new AonIntegerLabel(stat.getDraft()) , AON.CSS.aonBackgroundLigthGray())
						.addCell( new AonIntegerLabel(stat.getInProcess()) , AON.CSS.aonBackgroundLigthGray())
						.addCell( new AonIntegerLabel(stat.getReview()) , AON.CSS.aonBackgroundLigthGray())
						.addCell( new AonIntegerLabel(stat.getTrash()) , AON.CSS.aonBackgroundLigthGray())
					;
				}
				offset.add( count);
				enableMoreData();
			}
		}

		private void addMessage(String message) {
			FlowPanel line = new FlowPanel();
			line.add(new InlineLabel(message));
			container.add(line);
			disableMoreData();
		}
		
	}
}
