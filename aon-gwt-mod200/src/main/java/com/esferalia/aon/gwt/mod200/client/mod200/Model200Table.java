package com.esferalia.aon.gwt.mod200.client.mod200;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.gwt.common.client.widget.ContextMenu;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridCell;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridHeaderRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.mod200.client.FiscalModelUtils;
import com.esferalia.aon.gwt.mod200.client.mod200.Model200.Model200Callback;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.mod200.api.model.Mod200;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

//public class Model200Table extends CellTable<Mod200> {
//	
//	public static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);
//	
//
//	public Model200Table(ProvidesKey<Mod200> providesKey) {
//		super(1,TABLE_STYLE, providesKey);
//		this.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
//		this.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
//		
//		addSelectorColumn();
//		addAdministrationColumn();
//		addYearColumn();
//		addModelColumn();
//		addStatusColumn();
//		addComplementaryColumn();
//		addDocumentColumn();
//		addNameColumn();
//		addResultTypeColumn();
//		addResultColumn();
//		addFinanceStatusColumn();
//		this.setEmptyTableWidget(new HTML(AON.MSG.noData()));
//	}
//
//	private void addSelectorColumn() {
//		final Column<Mod200, ImageResource> selectorColumn = new Column<Mod200, ImageResource>(
//				new ImageResourceCell()) {
//			@Override
//			public ImageResource getValue(Mod200 model) {
//				return AON.AON_RESOURCES.aonIconRowSelector();
//			}
//		};
//		this.addColumn(selectorColumn);
//		this.setColumnWidth(selectorColumn, 20, Unit.PX);
//	}
//
//	private void addAdministrationColumn() {
//		final Column<Mod200, ImageResource> iconColumn = new Column<Mod200, ImageResource>(
//				new ImageResourceCell()) {
//			@Override
//			public ImageResource getValue(Mod200 model) {
//				return FiscalModelUtils.getAdministrationIconResource(model.getAdministration());
//			}
//		};
//		this.addColumn(iconColumn, "A" );
//		this.setColumnWidth(iconColumn, 20, Unit.PX);
//	}
//	
//	private void addYearColumn() {
//		final TextColumn<Mod200> yearColumn = new TextColumn<Mod200>() {
//			@Override
//			public String getValue(Mod200 model) {
//				return Integer.toString(model.getYear());
//			}
//		};
//		this.addColumn(yearColumn, AON.MSG.fiscalYear());
//		yearColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//		this.setColumnWidth(yearColumn, 80, Unit.PX);
//	}
//
//	private void addModelColumn() {
//		final TextColumn<Mod200> modelColumn = new TextColumn<Mod200>() {
//			@Override
//			public String getValue(Mod200 model) {
//				return FiscalModelUtils.getModelName(model);
//			}
//		};
//		this.addColumn(modelColumn, AON.MSG.model());
//		modelColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//		this.setColumnWidth(modelColumn, 50, Unit.PX);
//	}
//
//	private void addStatusColumn() {
//		final Column<Mod200, ImageResource> iconColumn = new Column<Mod200, ImageResource>(
//				new ImageResourceCell()) {
//			@Override
//			public ImageResource getValue(Mod200 model) {
//				return model.isFinished()
//						?AON.AON_RESOURCES.aonIconLock()
//						:AON.AON_RESOURCES.aonIconUnlock();
//			}
//		};
//		this.addColumn(iconColumn, "E" );
//		this.setColumnWidth(iconColumn, 20, Unit.PX);
//	}
//	
//	private void addComplementaryColumn() {
//		Column<Mod200, ImageResource> complementaryColumn = new Column<Mod200, ImageResource>(
//				new ImageResourceCell()) {
//			@Override
//			public ImageResource getValue(Mod200 model) {
//				return model.isComplementary() 
//					? AON.AON_RESOURCES.aonIconChecked()
//					: AON.AON_RESOURCES.aonIconCheck();
//			}
//		};
//		this.addColumn(complementaryColumn, "C" );
//		complementaryColumn.setCellStyleNames(AON.AON_CSS.aonDataTableIconColumn());
//		this.setColumnWidth(complementaryColumn, 20, Unit.PX);
//	}
//	
//	private void addDocumentColumn() {
//		final TextColumn<Mod200> documentColumn = new TextColumn<Mod200>() {
//			@Override
//			public String getValue(Mod200 model) {
//				return model.getDocument();
//			}
//		};
//		this.addColumn(documentColumn, AON.MSG.document());
//		this.setColumnWidth(documentColumn, 100, Unit.PX);
//	}
//
//	private void addNameColumn() {
//		final TextColumn<Mod200> nameColumn = new TextColumn<Mod200>() {
//			@Override
//			public String getValue(Mod200 model) {
//				return model.getFullName();
//			}
//		};
//		this.addColumn(nameColumn, AON.MSG.name());
//		this.setColumnWidth(nameColumn, "auto");
//	}	
//
//	private void addResultTypeColumn() {
//		final TextColumn<Mod200> nameColumn = new TextColumn<Mod200>() {
//			@Override
//			public String getValue(Mod200 model) {
//				if (model.isDeposit()) return AON.MSG.deposit(); 
//				else if (model.isPayback()) return AON.MSG.payBack();
//				else return AonStringUtils.EMPTY;
//			}
//		};
//		this.addColumn(nameColumn, AON.MSG.result());
//		this.setColumnWidth(nameColumn, 100, Unit.PX);
//	}	
//
//	private void addResultColumn() {
//		final TextColumn<Mod200> amountColumn = new TextColumn<Mod200>() {
//			@Override
//			public String getValue(Mod200 model) {
//				return AON.FMT.format(model.getResult()) ;
//			}
//		};
//		this.addColumn(amountColumn, AON.MSG.result());
//		amountColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
//		this.setColumnWidth(amountColumn, 100, Unit.PX);
//	}	
//
//	private void addFinanceStatusColumn() {
//		final TextColumn<Mod200> financeStatusColumn = new TextColumn<Mod200>() {
//			@Override
//			public String getValue(Mod200 model) {
//				if (model.getFinance() != null && model.getFinance().getFinanceStatus() != null) {
//					return model.getFinance().getFinanceStatus().getDescription();
//				}
//				return AonStringUtils.EMPTY;
//			}
//		};
//		this.addColumn(financeStatusColumn, AON.MSG.financeStatus());
//		financeStatusColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//		this.setColumnWidth(financeStatusColumn, 120, Unit.PX);
//	}	
//
//}

public class Model200Table extends SimpleLayoutPanel implements HasSelectionHandlers<Mod200>{
	
	// PARA COMPILAR LOS ANTERIORES LO USABA LA CLASE ErrorPage
	public static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);
	// --------------------

	private SimpleLayoutPanel centerLayoutPanel;
	private ScrollPanel centerPanel;
	private FlowPanel container;
	private AonDisplayGrid tab;

	protected Model200Table(Model200Callback cbk) {
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
	
	private Widget getToolbarPanel(Model200Callback cbk) {
		AonToolbar toolbar = new AonToolbar(AON.MSG.fiscalModelDescriptionlong( FiscalModelType.M200));
		
		final AonToolbarButton newButton = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		newButton.addClickHandler( event -> showMenu(cbk, newButton.getAbsoluteLeft() , newButton.getAbsoluteTop() + newButton.getOffsetHeight()) );
		toolbar.add(newButton);
		
		final AonToolbarButton refreshButton = new AonToolbarButton( AON.MSG.refresh(), AON.CSS.aonIconRefresh() );
		refreshButton.addClickHandler( event -> refresh(cbk) );
		toolbar.add(refreshButton);

		return toolbar;
	}
	
	private void showMenu(Model200Callback cbk, int x, int y) {
		
		ContextMenu menu = new ContextMenu();		
		menu.addStyleName(AON.AON_CSS.aonSelector());
		
		for (int i = 2020; i >= 2013; i--) {
			
			final int year = i;
			
			menu.addItem("200", AON.MSG.newSomething(String.valueOf(year)), new ScheduledCommand() {
				
				@Override
				public void execute() {
					cbk.onNew(year);
				}
			});
		}
						
		menu.setPopupPosition(x, y);
		menu.show();		
		
	}
	
	public void refresh(Model200Callback cbk) {
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		
		container.clear();
		container.add(getTable());
		Model200.MOD200_SERVICE.getMod200s(cbk.getOptions().getOccam(),
				new AsyncCallback<LinkedList<Mod200>>() {
			
			@Override
			public void onSuccess(LinkedList<Mod200> result) {
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
		  CHK(AON.MSG.model()		, 50 ,AON.CSS.aonTextCenter()) // Modelo 
	    , STA("A"					, 20 ,AON.CSS.aonTextCenter()) // Administración
	    , YER(AON.MSG.fiscalYear()	, 50 ,AON.CSS.aonTextCenter()) // Año
		, SEC(AON.MSG.period()		, 75 ,AON.CSS.aonTextCenter()) // Periodo
		// LA COLUMNA DE ESTADO NO SE PONDRÁ HASTA QUE SE DESARROLLE LO DEL ESTADO EN EL MODELO 200
//		, DCT(AON.MSG.status()		, 75 ,AON.CSS.aonTextCenter()) // Estado
		, CMP("C"					, 20 ,AON.CSS.aonTextCenter()) // Complementaria
		, DOC("Documento"			, 100,AON.CSS.aonTextLeft())   // Documento
		, AUTO(AON.MSG.name()		, 0  ,AON.CSS.aonTextLeft())   // Nombre
		, RST(AON.MSG.result()		, 100,AON.CSS.aonTextRight())  // Importe Resultado
	    , ACT(AonStringUtils.EMPTY	, 100,AON.CSS.aonTextCenter()) // Tipo Resultado
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
	
	private void paint(LinkedList<Mod200> result) {
		for ( Mod200 mod200 : result) {
			
			InlineLabel admon = new InlineLabel();
			admon.setTitle( mod200.getAdministration().getDescription() );
			admon.setStyleName(AON.CSS.aonIconLabel());
			admon.addStyleName(FiscalModelUtils.getAdministrationIconStyle(mod200.getAdministration()) );
			
			InlineLabel comp = new InlineLabel();
			comp.setTitle( AON.MSG.complementary());
			comp.setStyleName(AON.CSS.aonIconLabel());
			comp.addStyleName( mod200.isComplementary()?AON.CSS.aonIconChecked():AON.CSS.aonIconCheck() );
			
			AonDisplayGridRow row = tab.addRow();
			row.addStyleName(AON.CSS.aonClickable());
			row.addClickHandler( event ->  SelectionEvent.fire(Model200Table.this, mod200));					
			
			row.addCell( new InlineLabel(FiscalModelUtils.getModelName(mod200)), AON.CSS.aonTextCenter())    
 				.addCell( admon , AON.CSS.aonTextCenter())
 				.addCell( new InlineLabel(AonNumberUtils.toString( mod200.getYear())), AON.CSS.aonTextCenter())
				.addCell( new InlineLabel(mod200.getPeriod().getDescription()), AON.CSS.aonTextCenter());
			
			// El "Estado" se empezará a usar a partir del Modelo 200 - 2021
//			AonDisplayGridCell statusCell = new AonDisplayGridCell();
//			statusCell.add(new InlineLabel(mod200.getYear() < 2021 ? "" : mod200.getStatus().getName()));
//			statusCell.addStyleName(AON.CSS.aonTextCenter());
//			statusCell.getElement().getStyle().setBackgroundColor(FiscalModelUtils.getStatusBckColorRGB(mod200.getStatus()) );
//			statusCell.getElement().getStyle().setColor(FiscalModelUtils.getStatusFrgColorRGB(mod200.getStatus()) );
//			row.add( statusCell );
			
			// Tipo Resultado (Ingreso o Devolucion
			String resultType = "";
			if (mod200.isDeposit()) 
				resultType = AON.MSG.deposit(); 
			else if (mod200.isPayback()) 
				resultType = AON.MSG.payBack();
			
			row.addCell( comp , AON.CSS.aonTextCenter())
				.addCell( new InlineLabel(mod200.getDocument()))
				.addCell( new InlineLabel(mod200.getFullName()))
				.addCell( new InlineLabel(AON.FMT.format(mod200.getResult())), AON.CSS.aonTextRight())
				.addCell( new InlineLabel(resultType));
				;
		}
		
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod200> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}

