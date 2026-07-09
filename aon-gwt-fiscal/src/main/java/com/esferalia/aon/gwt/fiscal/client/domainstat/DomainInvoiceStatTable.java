package com.esferalia.aon.gwt.fiscal.client.domainstat;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AonToast;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonFlexGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerLabel;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.JsDomainInvoiceStat;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.DomainInvoiceStatParams;
import com.esferalia.aon.watson.mutable.MutableBoolean;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.BorderStyle;
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
	private static final String[] WIDTHS = new String[] {
		"120px","1fr",
		"60px","60px","60px","60px","60px","60px",
		"60px","60px","60px",
		"60px","60px","60px","60px",
		"60px","60px","60px","60px"
	};

	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableBoolean searchEnabled = new MutableBoolean( true );
	private final FlowPanel container;
	private AonFlexGrid grid;
	private int lastScrollPos = 0;
	
	DomainInvoiceStatTable(DomainInvoiceStatModuleOptions options, DomainInvoiceStatParams params) {
		this.setStyleName(AON.CSS.aonScrollArea());
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
	private Label headerLabel( String text, String title ) {
		Label label = new Label(text);
		label.setStyleName(AON.CSS.aonTextRight());
		label.setTitle(title);
		return label;
	}
	
	private void addHeaderRow() {
		FlowPanel cell = grid.addCell( 2, AON.CSS.aonDisplayGridHeaderCell(), AON.CSS.aonBorderNone());
		cell.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		Label clas = new Label("Clasificaci\u00F3n");
		Label undec = new Label("Facturas no declaradas");
		Label unrec = new Label("Facturas sin contabilizar");
		Label docs = new Label("Doc. Pendientes");
		grid
			.addCell( clas, 6, AON.CSS.aonDisplayGridHeaderCell(),AON.CSS.aonJustifyContentCenter())
			.addCell( undec, 3, AON.CSS.aonDisplayGridHeaderCell(), AON.CSS.aonBackgroundLigthBlueImp(),AON.CSS.aonJustifyContentCenter())
			.addCell( unrec, 4, AON.CSS.aonDisplayGridHeaderCell(), AON.CSS.aonBackgroundLigthYellowImp(),AON.CSS.aonJustifyContentCenter())
			.addCell( docs, 4, AON.CSS.aonDisplayGridHeaderCell() , AON.CSS.aonBackgroundLigthGrayImp(),AON.CSS.aonJustifyContentCenter())
		;
		Label nacLabel = headerLabel("Op.I.", "Operaciones Interiores");
		Label intLabel = headerLabel("Intr", "Operaciones Intracomunitarias");
		Label extLabel = headerLabel("Extr", "Operaciones Extracomunitarias");
		Label ccmLabel = headerLabel("C.C.M.", "Operaciones en Canarias, Ceuta y Melilla");
		Label ispLabel = headerLabel("I.S.P.", "Operaciones con ISP");
		Label retLabel = headerLabel("IRPF", "Operaciones con Retenci\u00F3n");
		
		Label undeclaredIssuedLabel = headerLabel("Emit", "Facturas emitidas no declaradas");
		Label undeclaredReceivedLabel = headerLabel("Recib", "Facturas recibidas no declaradas");
		Label undeclaredSimplifiedLabel = headerLabel("Simpl", "Facturas simplificadas no declaradas");
		
		Label proformaLabel = headerLabel("Prof", "Proformas");
		Label unrecordedIssuedLabel = headerLabel("Emit", "Facturas emitidas no contabilizadas");
		Label unrecordedReceivedLabel = headerLabel("Recib", "Facturas recibidas no contabilizadas");
		Label unrecordedSimplifiedLabel = headerLabel("Simpl", "Facturas simplificadas no contabilizadas");
		
		Label draftLabel = headerLabel("Borr", "Borrador");  
		Label reviewLabel = headerLabel("Revi", "Revisi\u00F3n");
		Label inProcessLabel = headerLabel("Tr\u00E1m", "Tr\u00E1mite");
		Label trashLabel = headerLabel("Pap", "Papelera");
		
		grid
			.addCell( new Label("Documento"), AON.CSS.aonDisplayGridHeaderCell())
			.addCell( new Label("Nombre")	, AON.CSS.aonDisplayGridHeaderCell())
			
			.addCell( nacLabel , AON.CSS.aonDisplayGridHeaderCell(),AON.CSS.aonJustifyContentCenter())
			.addCell( intLabel , AON.CSS.aonDisplayGridHeaderCell(),AON.CSS.aonJustifyContentCenter())
			.addCell( extLabel , AON.CSS.aonDisplayGridHeaderCell(),AON.CSS.aonJustifyContentCenter())
			.addCell( ccmLabel , AON.CSS.aonDisplayGridHeaderCell(),AON.CSS.aonJustifyContentCenter())
			.addCell( ispLabel , AON.CSS.aonDisplayGridHeaderCell(),AON.CSS.aonJustifyContentCenter())
			.addCell( retLabel , AON.CSS.aonDisplayGridHeaderCell(),AON.CSS.aonJustifyContentCenter())
			
			.addCell( undeclaredIssuedLabel 	, AON.CSS.aonDisplayGridHeaderCell(),AON.CSS.aonBackgroundLigthBlueImp(),AON.CSS.aonJustifyContentCenter())
			.addCell( undeclaredReceivedLabel 	, AON.CSS.aonDisplayGridHeaderCell(),AON.CSS.aonBackgroundLigthBlueImp(),AON.CSS.aonJustifyContentCenter())
			.addCell( undeclaredSimplifiedLabel , AON.CSS.aonDisplayGridHeaderCell(),AON.CSS.aonBackgroundLigthBlueImp(),AON.CSS.aonJustifyContentCenter())
			
			.addCell( proformaLabel 			, AON.CSS.aonDisplayGridHeaderCell(),AON.CSS.aonBackgroundLigthYellowImp(),AON.CSS.aonJustifyContentCenter())
			.addCell( unrecordedIssuedLabel 	, AON.CSS.aonDisplayGridHeaderCell(),AON.CSS.aonBackgroundLigthYellowImp(),AON.CSS.aonJustifyContentCenter())
			.addCell( unrecordedReceivedLabel 	, AON.CSS.aonDisplayGridHeaderCell(),AON.CSS.aonBackgroundLigthYellowImp(),AON.CSS.aonJustifyContentCenter())
			.addCell( unrecordedSimplifiedLabel , AON.CSS.aonDisplayGridHeaderCell(),AON.CSS.aonBackgroundLigthYellowImp(),AON.CSS.aonJustifyContentCenter())
		
			.addCell( draftLabel 				, AON.CSS.aonDisplayGridHeaderCell(),AON.CSS.aonBackgroundLigthGrayImp(),AON.CSS.aonJustifyContentCenter())
			.addCell( reviewLabel 				, AON.CSS.aonDisplayGridHeaderCell(),AON.CSS.aonBackgroundLigthGrayImp(),AON.CSS.aonJustifyContentCenter())
			.addCell( inProcessLabel 			, AON.CSS.aonDisplayGridHeaderCell(),AON.CSS.aonBackgroundLigthGrayImp(),AON.CSS.aonJustifyContentCenter())
			.addCell( trashLabel 				, AON.CSS.aonDisplayGridHeaderCell(),AON.CSS.aonBackgroundLigthGrayImp(),AON.CSS.aonJustifyContentCenter())
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
		grid = new AonFlexGrid(WIDTHS);
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
		private final AonToast toast;
		DomainInvoiceStatStateChangeHandler() {
			this.toast = new AonToast();
			toast.show( AON.MSG.loading(), AON.MSG.loading() );
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
				toast.hide();
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
					String name = AonStringUtils.abbreviate(stat.getCompanyName(), 40);
					Label nameLabel = new Label(name);
					if ( AonStringUtils.length(stat.getCompanyName()) > 40 ) {
						nameLabel.setTitle(stat.getCompanyName());
					}
					nameLabel.setStyleName(AON.CSS.aonClickableLabel());
					nameLabel.addClickHandler( e -> navigate(stat) );
					
					AonIntegerLabel undeclaredIssuedLabel = new AonIntegerLabel(stat.getUndeclaredIssued(), AonStringUtils.HYPHEN);
					AonIntegerLabel undeclaredReceivedLabel = new AonIntegerLabel(stat.getUndeclaredReceived(), AonStringUtils.HYPHEN);
					AonIntegerLabel undeclaredSimplifiedLabel = new AonIntegerLabel(stat.getUndeclaredSimplified(), AonStringUtils.HYPHEN);
					
					AonIntegerLabel proformaLabel = new AonIntegerLabel(stat.getProformas(), AonStringUtils.HYPHEN);
					AonIntegerLabel unrecordedIssuedLabel = new AonIntegerLabel(stat.getUnrecordedIssued(), AonStringUtils.HYPHEN);
					AonIntegerLabel unrecordedReceivedLabel = new AonIntegerLabel(stat.getUnrecordedReceived(), AonStringUtils.HYPHEN);
					AonIntegerLabel unrecordedSimplifiedLabel = new AonIntegerLabel(stat.getUnrecordedSimplified(), AonStringUtils.HYPHEN);
					
					AonIntegerLabel draftLabel = new AonIntegerLabel(stat.getDraft(), AonStringUtils.HYPHEN);
					AonIntegerLabel reviewLabel = new AonIntegerLabel(stat.getReview(), AonStringUtils.HYPHEN);
					AonIntegerLabel inProcessLabel = new AonIntegerLabel(stat.getInProcess(), AonStringUtils.HYPHEN);
					AonIntegerLabel trashLabel = new AonIntegerLabel(stat.getTrash(), AonStringUtils.HYPHEN);
					String colorStyle = AON.CSS.aonColorInherit();
					if (stat.isExpired()) {
						colorStyle = AON.CSS.aonColorOrange();
					} else 
					if (!stat.isActive()) {
						colorStyle = AON.CSS.aonColorRed();
					}
					grid
						.addCell( new Label(stat.getCompanyDocument()), colorStyle)
						.addCell( nameLabel, colorStyle)
						
						.addCell( new AonIntegerLabel(stat.getNational(), AonStringUtils.HYPHEN) ,AON.CSS.aonJustifyContentCenter())
						.addCell( new AonIntegerLabel(stat.getIntracommunity(), AonStringUtils.HYPHEN) ,AON.CSS.aonJustifyContentCenter())
						.addCell( new AonIntegerLabel(stat.getExtracommunity(), AonStringUtils.HYPHEN) ,AON.CSS.aonJustifyContentCenter())
						.addCell( new AonIntegerLabel(stat.getCanCeuMel(), AonStringUtils.HYPHEN) ,AON.CSS.aonJustifyContentCenter())
						.addCell( new AonIntegerLabel(stat.getOtherISP(), AonStringUtils.HYPHEN) ,AON.CSS.aonJustifyContentCenter())
						.addCell( new AonIntegerLabel(stat.getWithholding(), AonStringUtils.HYPHEN) ,AON.CSS.aonJustifyContentCenter())
						
						.addCell( undeclaredIssuedLabel 	,AON.CSS.aonBackgroundLigthBlueImp(),AON.CSS.aonJustifyContentCenter())
						.addCell( undeclaredReceivedLabel 	,AON.CSS.aonBackgroundLigthBlueImp(),AON.CSS.aonJustifyContentCenter())
						.addCell( undeclaredSimplifiedLabel ,AON.CSS.aonBackgroundLigthBlueImp(),AON.CSS.aonJustifyContentCenter())
						
						.addCell( proformaLabel 			,AON.CSS.aonBackgroundLigthYellowImp(),AON.CSS.aonJustifyContentCenter())
						.addCell( unrecordedIssuedLabel 	,AON.CSS.aonBackgroundLigthYellowImp(),AON.CSS.aonJustifyContentCenter())
						.addCell( unrecordedReceivedLabel 	,AON.CSS.aonBackgroundLigthYellowImp(),AON.CSS.aonJustifyContentCenter())
						.addCell( unrecordedSimplifiedLabel ,AON.CSS.aonBackgroundLigthYellowImp(),AON.CSS.aonJustifyContentCenter())
						
						.addCell( draftLabel 				,AON.CSS.aonBackgroundLigthGrayImp(),AON.CSS.aonJustifyContentCenter())
						.addCell( reviewLabel 				,AON.CSS.aonBackgroundLigthGrayImp(),AON.CSS.aonJustifyContentCenter())
						.addCell( inProcessLabel 			,AON.CSS.aonBackgroundLigthGrayImp(),AON.CSS.aonJustifyContentCenter())
						.addCell( trashLabel 				,AON.CSS.aonBackgroundLigthGrayImp(),AON.CSS.aonJustifyContentCenter())
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
		
		private native void navigate(JsDomainInvoiceStat stat) /*-{
			var header = ($wnd.top && $wnd.top.document) 
				? $wnd.top.document.querySelector('aon-header') 
				: null;
			if (!header || typeof header.companySelectionToPendingAccounting !== 'function') {
				var c = ($wnd.top && $wnd.top.console) 
					? $wnd.top.console
					: $wnd.console;
				if (c) c.error('navigate: aon-header.companySelectionToPendingAccounting no disponible');
				return;
			}
			var company = {
				id : stat.id,
				domain : stat.name,
				name : stat.companyName,
				document : stat.companyDocument,
				parentId : stat.parentId
			};
			header.companySelectionToPendingAccounting(company);
		}-*/;
  
	}
}
