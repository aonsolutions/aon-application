package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Collection;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridCell;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridHeaderRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

class ConsoleDomainTable extends SimpleLayoutPanel implements HasSelectionHandlers<Domain>{
	
	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainTable.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private SimpleLayoutPanel centerLayoutPanel;
	private ScrollPanel centerPanel;
	private FlowPanel container;
	
	protected ConsoleDomainTable(Collection<Domain> domains) {
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
		paint(domains);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Domain> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}


	private enum Columns {
		  CHK(AON.MSG.type()		, 75 ,AON.CSS.aonTextCenter())
	    , STA("Act."				, 20 ,AON.CSS.aonTextCenter())
	    , MNG("Crea"				, 20 ,AON.CSS.aonTextCenter())
	    , PAR("Padre"				, 50 ,AON.CSS.aonTextCenter())
	    , HER("Her"					, 20 ,AON.CSS.aonTextCenter())
	    , AUTO(AON.MSG.name()		, 0  ,AON.CSS.aonTextCenter())
	    , DES(AON.MSG.description() , 150,AON.CSS.aonTextCenter())
		, ACC("\u00FAlt. Acceso"	, 75 ,AON.CSS.aonTextCenter())
		, EXP("Expira"				, 75 ,AON.CSS.aonTextCenter())
		, CMD("	"					, 100,AON.CSS.aonTextCenter())
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
	
	private void paint(Collection<Domain> domains) {
		container.clear();
		
		if ( domains == null || domains.isEmpty()) {
			FlowPanel line = new FlowPanel();
			InlineLabel label = new InlineLabel(AON.MSG.noData());
			line.add(label);
			container.add(line);
			return;
		}
		
		AonDisplayGrid tab = new AonDisplayGrid();
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
		container.add(tab);


		for ( Domain domain : domains) {
			
			InlineLabel active = new InlineLabel();
			active.setTitle( "Activo" );
			active.setStyleName(AON.CSS.aonIconLabel());
			active.addStyleName( domain.isActive()?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck() );
			
			InlineLabel domManagement = new InlineLabel();
			domManagement.setTitle( "Puede crear dominios" );
			domManagement.setStyleName(AON.CSS.aonIconLabel());
			domManagement.addStyleName( domain.isDomainManagement()?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck() );
			
			InlineLabel heredity = new InlineLabel();
			heredity.setTitle( "Herencia de datos" );
			heredity.setStyleName(AON.CSS.aonIconLabel());
			heredity.addStyleName( domain.isEnableHeredity()?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck() );

			InlineLabel descriptionLabel = new InlineLabel(AonStringUtils.abbreviate(domain.getDescription(), 50));
			descriptionLabel.setTitle(domain.getDescription());
			
			AonDisplayGridRow row = tab.addRow();
			row.addStyleName(AON.CSS.aonClickable());
			row.addClickHandler( event ->  SelectionEvent.fire(ConsoleDomainTable.this, domain));
			row .addCell( new InlineLabel( domain.getDomainType()==null?"":domain.getDomainType().getName() ), AON.CSS.aonTextCenter())
				.addCell( active , AON.CSS.aonTextCenter())
				.addCell( domManagement , AON.CSS.aonTextCenter())
				.addCell( new InlineLabel(AonNumberUtils.toString( domain.getParentId())), AON.CSS.aonTextCenter())
				.addCell( heredity , AON.CSS.aonTextCenter())
				.addCell( new InlineLabel(domain.getName()))
				.addCell( descriptionLabel )
				.addCell( new InlineLabel( domain.getLastAccessDate()==null?"":AON.TIME_FORMAT.format(domain.getLastAccessDate())))
				.addCell( new InlineLabel( domain.getExpirationDate()==null?"":AON.DATE_FORMAT.format(domain.getExpirationDate())))
				.addCell( new InlineLabel( ))
				;
		}
		
	}
	
}
