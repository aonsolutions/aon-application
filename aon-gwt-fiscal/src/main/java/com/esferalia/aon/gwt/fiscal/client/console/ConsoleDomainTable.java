package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Collection;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridCell;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridHeaderRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

class ConsoleDomainTable extends SimpleLayoutPanel implements HasSelectionHandlers<Domain>{
	
	interface ConsoleDomainTableCallback {
		public Occam getOccam();
		public void onDelete(Domain domain, AsyncCallback<Domain> cbk);
		public void showError(String message);
	}
	
	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainTable.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private SimpleLayoutPanel centerLayoutPanel;
	private ScrollPanel centerPanel;
	private FlowPanel container;
	
	protected ConsoleDomainTable(Collection<Domain> domains, ConsoleDomainTableCallback callback) {
		DockLayoutPanel tableDockLayout = new DockLayoutPanel(Unit.PX);
		centerLayoutPanel = new SimpleLayoutPanel();
		centerPanel = new ScrollPanel();
		centerPanel.setStyleName(AON.CSS.aonScrollArea());
		centerPanel.addStyleName(AON.CSS.aonMarginBottom());
		container = new FlowPanel();
		centerPanel.setWidget(container);
		centerLayoutPanel.setWidget(centerPanel);
		tableDockLayout.add(centerLayoutPanel);
		setWidget(tableDockLayout);
		paint(domains, callback);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Domain> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}


	private enum Columns {
		  CHK(""					, 20 ,AON.CSS.aonTextCenter())
		, TYP(AON.MSG.type()		, 75 ,AON.CSS.aonTextCenter())
	    , STA("Act."				, 20 ,AON.CSS.aonTextCenter())
	    , MNG("Crea"				, 20 ,AON.CSS.aonTextCenter())
	    , PAR("Padre"				, 50 ,AON.CSS.aonTextCenter())
	    , HER("Her"					, 20 ,AON.CSS.aonTextCenter())
	    , AUTO(AON.MSG.name()		, 0  ,AON.CSS.aonTextCenter())
	    , DES(AON.MSG.description() , 150,AON.CSS.aonTextCenter())
		, ACC("\u00FAlt. Acceso"	, 75 ,AON.CSS.aonTextCenter())
		, EXP("Expira"				, 75 ,AON.CSS.aonTextCenter())
		, CMD(""					, 100,AON.CSS.aonTextCenter())
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
	
	private void paint(Collection<Domain> domains, ConsoleDomainTableCallback callback) {
		container.clear();
		if ( domains == null || domains.isEmpty()) {
			FlowPanel line = new FlowPanel();
			InlineLabel label = new InlineLabel(AON.MSG.noData());
			line.add(label);
			container.add(line);
		} else {
			AonDisplayGrid tab = new AonDisplayGrid();
			tab.addStyleName(AON.CSS.aonNoPadding());
			tab.addStyleName(AON.CSS.aonBlockCenter());
			tab.addStyleName(AON.CSS.aonWidthAlmostAll());
			container.add(tab);
			paintHeader( tab );
			domains
				.stream()
				.forEach(d -> paintRow( callback, d , tab.addRow()));
		}
	}

	private void paintHeader(AonDisplayGrid tab) {
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
	}

	private void paintRow(ConsoleDomainTableCallback callback, Domain domain, AonDisplayGridRow row) {
		CheckBox checkBox = new CheckBox();
		checkBox.addClickHandler(e -> SelectionEvent.fire(ConsoleDomainTable.this, domain));
		
		InlineLabel active = new InlineLabel();
		active.setTitle( "Activo" );
		active.setStyleName(AON.CSS.aonIconLabel());
		active.addStyleName( domain.isActive()?AON.CSS.aonIconToggleOn():AON.CSS.aonIconToggleOff() );
		
		InlineLabel domManagement = new InlineLabel();
		domManagement.setTitle( "Puede crear dominios" );
		domManagement.setStyleName(AON.CSS.aonIconLabel());
		domManagement.addStyleName( domain.isDomainManagement()?AON.CSS.aonIconToggleOn():AON.CSS.aonIconToggleOff() );
		
		InlineLabel heredity = new InlineLabel();
		heredity.setTitle( "Herencia de datos" );
		heredity.setStyleName(AON.CSS.aonIconLabel());
		heredity.addStyleName( domain.isEnableHeredity()?AON.CSS.aonIconToggleOn():AON.CSS.aonIconToggleOff() );

		InlineLabel descriptionLabel = new InlineLabel(AonStringUtils.abbreviate(domain.getDescription(), 50));
		descriptionLabel.setTitle(domain.getDescription());
		
		FlowPanel buttons = new FlowPanel();
		AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
		deleteButton.addClickHandler(e -> callback.onDelete( domain , new AsyncCallback<Domain>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.showError( "No se pudo borrar el dominio. ("+ caught.getMessage() +")");
			}

			@Override
			public void onSuccess(Domain result) {
				buttons.setVisible(false);
			}
			
		}));
		buttons.add(deleteButton);
		
		row.addStyleName(AON.CSS.aonClickable());
		row .addCell( checkBox , AON.CSS.aonTextCenter())
			.addCell( new InlineLabel( domain.getDomainType()==null?"":domain.getDomainType().getName() ), AON.CSS.aonTextCenter())
			.addCell( active , AON.CSS.aonTextCenter())
			.addCell( domManagement , AON.CSS.aonTextCenter())
			.addCell( new InlineLabel(AonNumberUtils.toString( domain.getParentId())), AON.CSS.aonTextCenter())
			.addCell( heredity , AON.CSS.aonTextCenter())
			.addCell( new InlineLabel(domain.getName()))
			.addCell( descriptionLabel )
			.addCell( new InlineLabel( domain.getLastAccessDate()==null?"":AON.TIME_FORMAT.format(domain.getLastAccessDate())))
			.addCell( new InlineLabel( domain.getExpirationDate()==null?"":AON.DATE_FORMAT.format(domain.getExpirationDate())))
			.addCell( buttons )
			;
	}
	
}
