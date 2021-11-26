package com.esferalia.aon.gwt.fiscal.client.mod202;

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
import com.esferalia.aon.gwt.fiscal.client.mod202.Model202.Model202Callback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
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

class Model202Table extends SimpleLayoutPanel implements HasSelectionHandlers<Mod202>{
	
	private static final Logger LOGGER = Logger.getLogger(Model202Table.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private SimpleLayoutPanel centerLayoutPanel;
	private ScrollPanel centerPanel;
	private FlowPanel container;
	private AonDisplayGrid tab;
	
	protected Model202Table(Model202Callback cbk) {
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
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod202> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public void refresh(Model202Callback cbk) {
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		
		container.clear();
		container.add(getTable());
		Model202.SERVICE.getMod202s(cbk.getOptions().getOccam(),
				new AsyncCallback<LinkedList<Mod202>>() {
			
			@Override
			public void onSuccess(LinkedList<Mod202> result) {
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
	
	
	private Widget getToolbarPanel(Model202Callback cbk) {
		AonToolbar toolbar = new AonToolbar( AON.MSG.fiscalModelDescriptionlong( FiscalModelType.M202));
		
		final AonToolbarButton newButton = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		newButton.addClickHandler( event -> cbk.onNew());
		toolbar.add(newButton);
		
		return toolbar;
	}

	private enum Columns {
		  CHK(AON.MSG.model()		, 50 ,AON.CSS.aonTextCenter())
	    , STA("A"					, 20 ,AON.CSS.aonTextCenter())
	    , YER(AON.MSG.fiscalYear()	, 50 ,AON.CSS.aonTextCenter())
		, SEC(AON.MSG.period()		, 75 ,AON.CSS.aonTextCenter())
		, DCT(AON.MSG.status()		, 75 ,AON.CSS.aonTextCenter())
		, CMP("C"					, 20 ,AON.CSS.aonTextCenter())
		, SST("S"					, 20 ,AON.CSS.aonTextCenter())
		, DOC("Documento"			, 100,AON.CSS.aonTextLeft())
		, AUTO(AON.MSG.name()		, 0  ,AON.CSS.aonTextLeft())
		, RST(AON.MSG.result()		, 100,AON.CSS.aonTextRight())
	    , ACT(AonStringUtils.EMPTY	, 100,AON.CSS.aonTextCenter())
	    , VST(AON.MSG.financeStatus(),100,AON.CSS.aonTextCenter())
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

	private void paint(LinkedList<Mod202> result) {
		for ( Mod202 mod202 : result) {
			
			InlineLabel admon = new InlineLabel();
			admon.setTitle( mod202.getAdministration().getDescription() );
			admon.setStyleName(AON.CSS.aonIconLabel());
			admon.addStyleName(FiscalModelUtils.getAdministrationIconStyle(mod202.getAdministration()) );
			
			InlineLabel comp = new InlineLabel();
			comp.setTitle( AON.MSG.complementary());
			comp.setStyleName(AON.CSS.aonIconLabel());
			comp.addStyleName( mod202.isComplementary()?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck() );
			
			InlineLabel sust = new InlineLabel();
			sust.setTitle( AON.MSG.replacement());
			sust.setStyleName(AON.CSS.aonIconLabel());
			sust.addStyleName( mod202.isReplacement()?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck() );
			
			AonDisplayGridRow row = tab.addRow();
			row.addStyleName(AON.CSS.aonClickable());
			row.addClickHandler( event ->  SelectionEvent.fire(Model202Table.this, mod202));					
			
			row.addCell( new InlineLabel(FiscalModelUtils.getModelName(mod202)), AON.CSS.aonTextCenter())
				.addCell( admon , AON.CSS.aonTextCenter())
				.addCell( new InlineLabel(AonNumberUtils.toString( mod202.getYear())), AON.CSS.aonTextCenter())
				.addCell( new InlineLabel(mod202.getPeriod().getDescription()), AON.CSS.aonTextCenter());
			
			AonDisplayGridCell statusCell = new AonDisplayGridCell();
			statusCell.add(new InlineLabel(mod202.getStatus().getName()));
			statusCell.addStyleName(AON.CSS.aonTextCenter());
			statusCell.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB(mod202.getStatus()) );
			statusCell.getElement().getStyle().setColor(FiscalModelUtils.getStatusFrgColorRGB(mod202.getStatus()) );
			row.add( statusCell );
			
			row.addCell( comp , AON.CSS.aonTextCenter())
				.addCell( sust , AON.CSS.aonTextCenter())
				.addCell( new InlineLabel(mod202.getDocument()))
				.addCell( new InlineLabel(mod202.getFullName()))
				.addCell( new InlineLabel(AON.FMT.format(mod202.getResult())), AON.CSS.aonTextRight())
				.addCell( new InlineLabel(mod202.getDeclarationType() == null ? "" : mod202.getDeclarationType().getDescription()))
				.addCell( new InlineLabel(
						(mod202.getFinance() != null && mod202.getFinance().getFinanceStatus() != null)
							?mod202.getFinance().getFinanceStatus().getDescription()
							:""
						), AON.CSS.aonTextCenter())
				;
		}
		
	}
	
}
