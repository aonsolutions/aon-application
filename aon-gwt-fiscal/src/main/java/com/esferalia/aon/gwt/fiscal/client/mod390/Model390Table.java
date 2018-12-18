package com.esferalia.aon.gwt.fiscal.client.mod390;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390.Model390Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelProvidesKey;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Model390Table extends SimpleLayoutPanel implements HasSelectionHandlers<Mod390>{
	
	private static class Model390CellTable extends CellTable<Mod390> {
		private static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);
		

		private NoSelectionModel<Mod390> model;
		
		public Model390CellTable(ProvidesKey<Mod390> providesKey) {
			super(1,TABLE_STYLE, providesKey);
			this.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
			this.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
			
			addSelectorColumn();
			addModelColumn();
			addAdministrationColumn();
			addYearColumn();
			addStatusColumn();
			addStatusLabelColumn();
			addComplementaryColumn();
			addDocumentColumn();
			addNameColumn();
			
			model = new NoSelectionModel<Mod390>(providesKey);
			this.setSelectionModel(model);
			this.setEmptyTableWidget(new HTML(AON.MSG.noData()));
		}

		private void addSelectorColumn() {
			final Column<Mod390, ImageResource> selectorColumn = new Column<Mod390, ImageResource>(
					new ImageResourceCell()) {
				@Override
				public ImageResource getValue(Mod390 model) {
					return AON.AON_RESOURCES.aonIconRowSelector();
				}
			};
			this.addColumn(selectorColumn);
			this.setColumnWidth(selectorColumn, 20, Unit.PX);
		}

		private void addYearColumn() {
			final TextColumn<Mod390> yearColumn = new TextColumn<Mod390>() {
				@Override
				public String getValue(Mod390 model) {
					return Integer.toString(model.getYear());
				}
			};
			this.addColumn(yearColumn, AON.MSG.fiscalYear());
			yearColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
			this.setColumnWidth(yearColumn, 50, Unit.PX);
		}

		private void addAdministrationColumn() {
			final Column<Mod390, ImageResource> iconColumn = new Column<Mod390, ImageResource>(
					new ImageResourceCell()) {
				@Override
				public ImageResource getValue(Mod390 model) {
					return FiscalModelUtils.getAdministrationIconResource(model.getAdministration());
				}
			};
			this.addColumn(iconColumn, "A" );
			this.setColumnWidth(iconColumn, 20, Unit.PX);
		}
		
		private void addModelColumn() {
			final TextColumn<Mod390> modelColumn = new TextColumn<Mod390>() {
				@Override
				public String getValue(Mod390 model) {
					return FiscalModelUtils.getModelName(model);
				}
			};
			this.addColumn(modelColumn, AON.MSG.model());
			modelColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
			this.setColumnWidth(modelColumn, 50, Unit.PX);
		}

		private void addStatusColumn() {
			final Column<Mod390, ImageResource> iconColumn = new Column<Mod390, ImageResource>(
					new ImageResourceCell()) {
				@Override
				public ImageResource getValue(Mod390 model) {
					return FiscalModelUtils.getStatusImage(model.getStatus());
				}
			};
			this.addColumn(iconColumn, "E" );
			this.setColumnWidth(iconColumn, 20, Unit.PX);
		}
		
		private void addStatusLabelColumn() {
			final TextColumn<Mod390> statusLabelColumn = new TextColumn<Mod390>() {
				@Override
				public String getValue(Mod390 fm) {
					return fm.getStatus().getName();
				}
			};
			this.addColumn(statusLabelColumn, AON.MSG.status() );
			this.setColumnWidth(statusLabelColumn, 80, Unit.PX);
		}

		private void addComplementaryColumn() {
			Column<Mod390, ImageResource> complementaryColumn = new Column<Mod390, ImageResource>(
					new ImageResourceCell()) {
				@Override
				public ImageResource getValue(Mod390 model) {
					return (model.isComplementary() || model.isReplacement())  
						? AON.AON_RESOURCES.aonIconChecked()
						: AON.AON_RESOURCES.aonIconCheck();
				}
			};
			this.addColumn(complementaryColumn, "C/S" );
			complementaryColumn.setCellStyleNames(AON.AON_CSS.aonDataTableIconColumn());
			this.setColumnWidth(complementaryColumn, 20, Unit.PX);
		}
		
		private void addDocumentColumn() {
			final TextColumn<Mod390> documentColumn = new TextColumn<Mod390>() {
				@Override
				public String getValue(Mod390 model) {
					return model.getDocument();
				}
			};
			this.addColumn(documentColumn, AON.MSG.document());
			this.setColumnWidth(documentColumn, 100, Unit.PX);
		}

		private void addNameColumn() {
			final TextColumn<Mod390> nameColumn = new TextColumn<Mod390>() {
				@Override
				public String getValue(Mod390 model) {
					return model.getFullName();
				}
			};
			this.addColumn(nameColumn, AON.MSG.name());
			this.setColumnWidth(nameColumn, "auto");
		}	

		public Mod390 getSelected() {
			return model.getLastSelectedObject();
		}
	}

	
	
	
	private Model390CellTable table;
	
	public Model390Table(Model390Callback cbk) {
		table = new Model390CellTable(new FiscalModelProvidesKey<Mod390>());
		if (table.getSelectionModel() != null) {
			table.getSelectionModel().addSelectionChangeHandler(new com.google.gwt.view.client.SelectionChangeEvent.Handler() {
				
				@Override
				public void onSelectionChange(SelectionChangeEvent event) {
					SelectionEvent.fire(Model390Table.this, table.getSelected());
				}
			});
		}
		table.addRangeChangeHandler( new com.google.gwt.view.client.RangeChangeEvent.Handler() {
			
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				Model390.MOD390_SERVICE.getMod390s(Model390.getCurrentDomainName(), Model390.getCurrentDomain(), Model390.getCurrentUser(),
						new AsyncCallback<LinkedList<Mod390>>() {
							@Override
							public void onSuccess(LinkedList<Mod390> result) {
								table.setRowData(result);
							}

							@Override
							public void onFailure(Throwable caught) {
								cbk.showError( AON.MSG.unableToReadDeclaration(caught.getMessage()) );
							}
						});
			}
		});
		DockLayoutPanel tableDockLayout = new DockLayoutPanel(Unit.PX);
		tableDockLayout.addNorth(getToolbarPanel(cbk), 25);
		ScrollPanel tablePanel = new ScrollPanel();
		tablePanel.addStyleName(AON.AON_CSS.aonScrollArea());
		tablePanel.add(table);
		tableDockLayout.add(tablePanel);
		setWidget(tableDockLayout);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod390> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public void refresh() {
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
	}
	
	
	private Widget getToolbarPanel(Model390Callback cbk) {
		FlowPanel toolbarPanel = new FlowPanel();
		toolbarPanel.setStyleName(AON.AON_CSS.aonFindingTitleToolbar());
		toolbarPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		FlexTable toolbar = new FlexTable();
		toolbar.setCellPadding(0);
		toolbar.setCellSpacing(0);
		toolbar.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.setStyleName(AON.AON_CSS.aonFindingTitleInternal());
		toolbar.setWidget(0, 0, titlePanel);
		toolbar.setWidget(0, 0, new Label( "IVA. Autoliquidaci\u00F3n."));
		toolbar.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());
		
		final Button newButton = new Button();
		newButton.setText(AON.MSG.newAction());
		newButton.setTitle(newButton.getText());
		newButton.setStyleName(AON.AON_CSS.aonFindingToolbarItem());
		newButton.addStyleName(AON.AON_CSS.aonIconReset());
		newButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cbk.onNew(0);
			}
		});
		buttonContainer.add(newButton);
		
		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}
	
}
