package com.esferalia.aon.gwt.fiscal.client.mod369;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridCell;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridHeaderRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod369.Model369.Model369Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod369;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model369Table extends SimpleLayoutPanel implements HasSelectionHandlers<Mod369>{

	private SimpleLayoutPanel centerLayoutPanel;
	private ScrollPanel centerPanel;
	private FlowPanel container;
	private AonDisplayGrid tab;

	protected Model369Table(Model369Callback cbk) {
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
	
	private Widget getToolbarPanel(Model369Callback cbk) {
		AonToolbar toolbar = new AonToolbar( "Modelo 369");
		
		final AonToolbarButton newButton = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		newButton.addClickHandler( event -> cbk.onNew( ));
		toolbar.add(newButton);
		
		final AonToolbarButton refreshButton = new AonToolbarButton( AON.MSG.refresh(), AON.CSS.aonIconRefresh() );
		refreshButton.addClickHandler( event -> refresh(cbk));
		toolbar.add(refreshButton);

		return toolbar;
	}
	
	public void refresh(Model369Callback cbk) {
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		
		container.clear();
		container.add(getTable());
		Model369.SERVICE.getMod369s(cbk.getOptions().getOccam(),
				new AsyncCallback<LinkedList<Mod369>>() {
			
			@Override
			public void onSuccess(LinkedList<Mod369> result) {
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
	
	private enum Columns {
		  CHK(AON.MSG.model()		, 50 ,AON.CSS.aonTextCenter())
	    , STA("A"					, 20 ,AON.CSS.aonTextCenter())
	    , YER(AON.MSG.fiscalYear()	, 50 ,AON.CSS.aonTextCenter())
		, SEC(AON.MSG.period()		, 75 ,AON.CSS.aonTextCenter())
		, REG("R\u00E9gimen"		, 150,AON.CSS.aonTextCenter())
		, DCT(AON.MSG.status()		, 75 ,AON.CSS.aonTextCenter())
		, DOC("Documento"			, 100,AON.CSS.aonTextLeft())
		, AUTO(AON.MSG.name()		, 0  ,AON.CSS.aonTextLeft())
		, SAC("Sin Act."	 		, 70 ,AON.CSS.aonTextCenter())
		, RST("Resultado"			, 100,AON.CSS.aonTextRight())
		, PAG("Importe pagado"		, 100,AON.CSS.aonTextRight())  
	    , ACT("Tipo de pago"		, 100,AON.CSS.aonTextCenter()) 
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
	
	private void paint(LinkedList<Mod369> result) {
		for (Mod369 mod369 : result) {
			
			InlineLabel admon = new InlineLabel();
			admon.setTitle(mod369.getAdministration().getDescription());
			admon.setStyleName(AON.CSS.aonIconLabel());
			admon.addStyleName(FiscalModelUtils.getAdministrationIconStyle(mod369.getAdministration()));
			
			AonDisplayGridRow row = tab.addRow();
			row.addStyleName(AON.CSS.aonClickable());
			row.addClickHandler( event ->  SelectionEvent.fire(Model369Table.this, mod369));					
			
			row.addCell( new InlineLabel(FiscalModelUtils.getModelName(mod369)), AON.CSS.aonTextCenter())   // Modelo
				.addCell( admon , AON.CSS.aonTextCenter())                                                  // Administracion  
				.addCell( new InlineLabel(AonNumberUtils.toString( mod369.getYear())), AON.CSS.aonTextCenter())  // Ejercicio
				.addCell( new InlineLabel(mod369.getPeriod().getDescription()), AON.CSS.aonTextCenter())   // Periodo
				.addCell( new InlineLabel(mod369.getRegime().getDescription()), AON.CSS.aonTextCenter());  // Régimen 
			
			AonDisplayGridCell statusCell = new AonDisplayGridCell();  // Estado
			statusCell.add(new InlineLabel(mod369.getStatus().getName()));
			statusCell.addStyleName(AON.CSS.aonTextCenter());
			statusCell.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB(mod369.getStatus()) );
			statusCell.getElement().getStyle().setColor(FiscalModelUtils.getStatusFrgColorRGB(mod369.getStatus()) );
			row.add( statusCell );
			
			InlineLabel wact = new InlineLabel();
			wact.setTitle(AON.MSG.withoutActivity());
			wact.setStyleName(AON.CSS.aonIconLabel());
			wact.addStyleName( mod369.isWithoutActivity() ? AON.CSS.aonIconChecked() : AON.CSS.aonIconCheck() );

			InlineLabel declarationResult = new InlineLabel();
			declarationResult.setText(AON.FMT.format(mod369.getResult()));
			
			InlineLabel amountPaid = new InlineLabel();
			if (mod369.getAmountPaid() != null) {
				amountPaid.setText(AON.FMT.format(mod369.getAmountPaid()));
			}
			
			row.addCell( new InlineLabel(mod369.getDocument()))      // NIF
			   .addCell( new InlineLabel(mod369.getName()))          // Nombre
			   .addCell( wact, AON.CSS.aonTextCenter())              // Sin Actividad
			   .addCell( declarationResult, AON.CSS.aonTextRight())  // Resultado
			   .addCell( amountPaid, AON.CSS.aonTextRight())         // Importe pagado
			   .addCell( new InlineLabel(mod369.getPayType() == null ? "" : mod369.getPayType().getDescription())); // Tipo de pago
		}
		
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod369> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}
