package com.esferalia.aon.gwt.fiscal.client.mod115;

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
import com.esferalia.aon.gwt.fiscal.client.mod115.Model115.Model115Callback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
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

class Model115Table extends SimpleLayoutPanel implements HasSelectionHandlers<Mod115>{
	
	private static final Logger LOGGER = Logger.getLogger(Model115Table.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private SimpleLayoutPanel centerLayoutPanel;
	private ScrollPanel centerPanel;
	private FlowPanel container;
	private AonDisplayGrid tab;
	
	protected Model115Table(Model115Callback cbk) {
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
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod115> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public void refresh(Model115Callback cbk) {
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		
		container.clear();
		container.add(getTable());
		Model115.SERVICE.getMod115s(cbk.getOptions().getOccam(),
				new AsyncCallback<LinkedList<Mod115>>() {
			
			@Override
			public void onSuccess(LinkedList<Mod115> result) {
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
	
	
	private Widget getToolbarPanel(Model115Callback cbk) {
		AonToolbar toolbar = new AonToolbar( AON.MSG.fiscalModelDescriptionlong( FiscalModelType.M115));
		
		if (cbk.getOptions().isBackButtonVisible() && cbk.getOptions().hasExternalCallback()) {
			final AonToolbarButton cancelButton = new AonToolbarButton(AON.MSG.backAction(),AON.CSS.aonIconBack());
			cancelButton.addClickHandler(event ->  cbk.getOptions().getExternalCallback().onExit( null ) );
			toolbar.add(cancelButton);
		}

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
		, SST("S"					, 20 ,AON.CSS.aonTextCenter())
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

	private void paint(LinkedList<Mod115> result) {
		for ( Mod115 mod115 : result) {
			
			InlineLabel admon = new InlineLabel();
			admon.setTitle( mod115.getAdministration().getDescription() );
			admon.setStyleName(AON.CSS.aonIconLabel());
			admon.addStyleName(FiscalModelUtils.getAdministrationIconStyle(mod115.getAdministration()) );
			
			InlineLabel comp = new InlineLabel();
			comp.setTitle( AON.MSG.complementary());
			comp.setStyleName(AON.CSS.aonIconLabel());
			comp.addStyleName( mod115.isComplementary()?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck() );
			
			InlineLabel sust = new InlineLabel();
			sust.setTitle( AON.MSG.replacement());
			sust.setStyleName(AON.CSS.aonIconLabel());
			sust.addStyleName( mod115.isReplacement()?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck() );
			
			AonDisplayGridRow row = tab.addRow();
			row.addStyleName(AON.CSS.aonClickable());
			row.addClickHandler( event ->  SelectionEvent.fire(Model115Table.this, mod115));					
			
			row.addCell( new InlineLabel(FiscalModelUtils.getModelName(mod115)), AON.CSS.aonTextCenter())
				.addCell( admon , AON.CSS.aonTextCenter())
				.addCell( new InlineLabel(AonNumberUtils.toString( mod115.getYear())), AON.CSS.aonTextCenter())
				.addCell( new InlineLabel(mod115.getPeriod().getDescription()), AON.CSS.aonTextCenter());
			
			AonDisplayGridCell statusCell = new AonDisplayGridCell();
			statusCell.add(new InlineLabel(mod115.getStatus().getName()));
			statusCell.addStyleName(AON.CSS.aonTextCenter());
			statusCell.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB(mod115.getStatus()) );
			statusCell.getElement().getStyle().setColor(FiscalModelUtils.getStatusFrgColorRGB(mod115.getStatus()) );
			row.add( statusCell );
			
			InlineLabel acc = new InlineLabel();
			acc.setTitle( AON.MSG.recorded());
			acc.setStyleName(AON.CSS.aonIconLabel());
			acc.addStyleName( mod115.isRecorded()?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck() );
			
			InlineLabel declarationResult = new InlineLabel();
			if (mod115.getDeclarationResult() != null) {
				declarationResult.setText(AON.FMT.format(mod115.getDeclarationResult()));
			}
			row.addCell( comp , AON.CSS.aonTextCenter())
				.addCell( sust , AON.CSS.aonTextCenter())
				.addCell( new InlineLabel(mod115.getDocument()))
				.addCell( new InlineLabel(mod115.getFullName()))
				.addCell( declarationResult, AON.CSS.aonTextRight())
				.addCell( new InlineLabel(mod115.getDeclarationResultType() == null ? "" : mod115.getDeclarationResultType().getDescription()))
				.addCell( new InlineLabel(
						(mod115.getFinance() != null && mod115.getFinance().getFinanceStatus() != null)
							?mod115.getFinance().getFinanceStatus().getDescription()
							:""
						), AON.CSS.aonTextCenter())
				.addCell( acc , AON.CSS.aonTextCenter())
				;
		}
		
	}
	
}
