package com.esferalia.aon.gwt.fiscal.client.mod421;

import java.util.LinkedList;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridCell;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridHeaderRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod421.Model421.Model421Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class Model421Table extends SimpleLayoutPanel implements HasSelectionHandlers<Mod421>{
	
	private static final Logger LOGGER = Logger.getLogger(Model421Table.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private SimpleLayoutPanel centerLayoutPanel;
	private ScrollPanel centerPanel;
	private FlowPanel container;
	private AonDisplayGrid tab;
	
	protected Model421Table(Model421Callback cbk) {
		DockLayoutPanel tableDockLayout = new DockLayoutPanel(Unit.PX);
		tableDockLayout.addNorth(getToolbarPanel(cbk), AonToolbar.HEIGTH);
		centerLayoutPanel = new SimpleLayoutPanel();
		centerPanel = new ScrollPanel();
		centerPanel.setStyleName(AON.CSS.aonScrollArea());
		centerPanel.addStyleName(AON.CSS.aonMarginBottom());
		container = new FlowPanel();
		centerPanel.setWidget(container);
		centerLayoutPanel.setWidget(centerPanel);
		tableDockLayout.add(centerLayoutPanel);
		setWidget(tableDockLayout);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod421> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public void refresh(Model421Callback cbk) {
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		
		container.clear();
		container.add(getTable());
		Model421.service.getMod421s(cbk.getOptions().getOccam(),
				new AsyncCallback<LinkedList<Mod421>>() {
					@Override
					public void onSuccess(LinkedList<Mod421> result) {
						paint(result);
						popup.hide();					
					}

					@Override
					public void onFailure(Throwable caught) {
						popup.hide();					
						cbk.showError( AON.MSG.unableToReadDeclaration(caught.getMessage()) );
					}
				});
	}
	
	
	private Widget getToolbarPanel(Model421Callback cbk) {
		AonToolbar toolbar = new AonToolbar( "I.G.I.C. R\u00E9gimen Simplificado. Autoliquidaci\u00F3n." );
		
		final AonToolbarButton newButton = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		newButton.addClickHandler( event -> cbk.onNew());
		toolbar.add(newButton);
		
		final AonToolbarButton refreshButton = new AonToolbarButton( AON.MSG.refresh(), AON.CSS.aonIconRefresh() );
		refreshButton.addClickHandler( event -> refresh(cbk));
		toolbar.add(refreshButton);

		return toolbar;
	}

	private enum Columns {
		  CHK(AON.MSG.model()		, 50 ,AON.CSS.aonTextCenter())
	    , STA("A"					, 20 ,AON.CSS.aonTextCenter())
	    , YER(AON.MSG.fiscalYear()	, 50 ,AON.CSS.aonTextCenter())
		, SEC(AON.MSG.period()		, 75 ,AON.CSS.aonTextCenter())
		, DCT(AON.MSG.status()		, 75 ,AON.CSS.aonTextCenter())
		, CMP("C"					, 20 ,AON.CSS.aonTextCenter())
		, DOC("Documento"			, 100,AON.CSS.aonTextLeft())
		, AUTO(AON.MSG.name()		, 0  ,AON.CSS.aonTextLeft())
		, RST(AON.MSG.result()		, 100,AON.CSS.aonTextRight())
	    , ACT(AonStringUtils.EMPTY	, 100,AON.CSS.aonTextCenter())
	    , VST(AON.MSG.financeStatus(),100,AON.CSS.aonTextCenter())
	    , ACC(AON.MSG.recordedAbbr() ,20 ,AON.CSS.aonTextCenter())
		;

		String headerLabel;
		int colWidth;
		String cellStyleClass;

		private Columns(String headerLabel,int colWidth,String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		public int getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getCellStyleClass() {
			return cellStyleClass;
		}
	}
	
	protected AonDisplayGrid getTable() {
		tab = new AonDisplayGrid();
		tab.addStyleName(AON.CSS.aonNoPadding());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		
		AonDisplayGridHeaderRow headerRow = tab.addHeaderRow();
		for ( Columns col : Columns.values()) {
			Label label = new Label( col.getHeaderLabel());
			AonDisplayGridCell headerCell = headerRow.addCell(col.getCellStyleClass());
			if (col == Columns.AUTO ) {
				headerCell.addStyleName(AON.CSS.aonFlexGrow1());
			} else {
				headerCell.setWidth(col.getColWidth()  + "px");
			}
			headerCell.add(label);
		}
		return tab;
	}

	private void paint(LinkedList<Mod421> result) {
		for (Mod421 mod421 : result) {
			
			InlineLabel admon = new InlineLabel();
			admon.setTitle( mod421.getAdministration().getDescription() );
			admon.setStyleName(AON.CSS.aonIconLabel());
			admon.addStyleName(FiscalModelUtils.getAdministrationIconStyle(mod421.getAdministration()) );
			
			InlineLabel comp = new InlineLabel();
			comp.setTitle( AON.MSG.complementary() );
			comp.setStyleName(AON.CSS.aonIconLabel());
			comp.addStyleName( mod421.isComplementary()?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck() );
			
			AonDisplayGridRow row = tab.addRow();
			row.addStyleName(AON.CSS.aonClickable());
			row.addClickHandler(event -> SelectionEvent.fire(Model421Table.this, mod421));					
			
			row.addCell( new InlineLabel(FiscalModelUtils.getModelName(mod421)), AON.CSS.aonTextCenter())
				.addCell( admon , AON.CSS.aonTextCenter())
				.addCell( new InlineLabel(AonNumberUtils.toString(mod421.getYear())), AON.CSS.aonTextCenter())
				.addCell( new InlineLabel(mod421.getPeriod().getDescription()), AON.CSS.aonTextCenter());
			
			AonDisplayGridCell statusCell = new AonDisplayGridCell();
			statusCell.add(new InlineLabel(mod421.getStatus().getName()));
			statusCell.addStyleName(AON.CSS.aonTextCenter());
			statusCell.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB(mod421.getStatus()) );
			statusCell.getElement().getStyle().setColor(FiscalModelUtils.getStatusFrgColorRGB(mod421.getStatus()) );
			row.add( statusCell );
						
			InlineLabel acc = new InlineLabel();
			acc.setTitle( AON.MSG.recorded());
			acc.setStyleName(AON.CSS.aonIconLabel());
			acc.addStyleName( mod421.isRecorded()?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck() );
			
			InlineLabel declarationResult = new InlineLabel();
			if (mod421.getDeclarationResult() != null) {
				declarationResult.setText(AON.FMT.format(mod421.getDeclarationResult()));
			}
			row.addCell( comp , AON.CSS.aonTextCenter())
				.addCell( new InlineLabel(mod421.getDocument()))
				.addCell( new InlineLabel(mod421.getFullName()))
				.addCell( declarationResult, AON.CSS.aonTextRight())
				.addCell( new InlineLabel(mod421.getDeclarationResultType() == null ? "" : mod421.getDeclarationResultType().getDescription()))
				.addCell( new InlineLabel(
						(mod421.getFinance() != null && mod421.getFinance().getFinanceStatus() != null)
							?mod421.getFinance().getFinanceStatus().getDescription()
							:""
						), AON.CSS.aonTextCenter())
				.addCell( acc , AON.CSS.aonTextCenter())
				;
		}
		
	}
	
}
