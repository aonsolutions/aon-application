package com.esferalia.aon.gwt.fiscal.client.mod184;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184.Model184Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelProvidesKey;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
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

public class Model184Table extends SimpleLayoutPanel implements HasSelectionHandlers<Mod184>{

	private static class Model184CellTable extends CellTable<Mod184> {
		private static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);
		
		public static final ProvidesKey<Mod184> MOD184_PROVIDES_KEY = new ProvidesKey<Mod184>() {
			@Override
			public Object getKey(Mod184 mod184) {
				return mod184 == null ? null : mod184.getId();
			}
		};
		private NoSelectionModel<Mod184> model;
		
		public Model184CellTable(ProvidesKey<Mod184> providesKey) {
			super(1,TABLE_STYLE, providesKey);
			this.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
			this.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
			
			addSelectorColumn();
			addAdministrationColumn();
			addYearColumn();
			addReplacementColumn();
			addStatusColumn();
			addStatusLabelColumn();
			addDocumentColumn();
			addNameColumn();
			
			model = new NoSelectionModel<Mod184>(MOD184_PROVIDES_KEY);
			this.setSelectionModel(model);
			this.setEmptyTableWidget(new HTML(AON.MSG.noData()));
		}
	
		private void addSelectorColumn() {
			final Column<Mod184, ImageResource> selectorColumn = new Column<Mod184, ImageResource>(
					new ImageResourceCell()) {
				@Override
				public ImageResource getValue(Mod184 mod184) {
					return AON.AON_RESOURCES.aonIconRowSelector();
				}
			};
			this.addColumn(selectorColumn);
			this.setColumnWidth(selectorColumn, 20, Unit.PX);
		}
	
		private void addAdministrationColumn() {
			final Column<Mod184, ImageResource> iconColumn = new Column<Mod184, ImageResource>(
					new ImageResourceCell()) {
				@Override
				public ImageResource getValue(Mod184 mod184) {
					return FiscalModelUtils.getAdministrationIconResource(mod184.getAdministration());
				}
			};
			this.addColumn(iconColumn, "A" );
			this.setColumnWidth(iconColumn, 20, Unit.PX);
		}
		
		private void addYearColumn() {
			final TextColumn<Mod184> yearColumn = new TextColumn<Mod184>() {
				@Override
				public String getValue(Mod184 mod184) {
					return Integer.toString(mod184.getYear());
				}
			};
			this.addColumn(yearColumn, AON.MSG.fiscalYear());
			yearColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
			this.setColumnWidth(yearColumn, 50, Unit.PX);
		}
		
		private void addStatusColumn() {
			final Column<Mod184, ImageResource> iconColumn = new Column<Mod184, ImageResource>(
					new ImageResourceCell()) {
				@Override
				public ImageResource getValue(Mod184 model) {
					return FiscalModelUtils.getStatusImage(model.getStatus());
				}
			};
			this.addColumn(iconColumn, " " );
			this.setColumnWidth(iconColumn, 20, Unit.PX);
		}
	
		private void addStatusLabelColumn() {
			final TextColumn<Mod184> statusLabelColumn = new TextColumn<Mod184>() {
				@Override
				public String getValue(Mod184 mod184) {
					return mod184.getStatus().getName();
				}
			};
			this.addColumn(statusLabelColumn, AON.MSG.status() );
			this.setColumnWidth(statusLabelColumn, 80, Unit.PX);
		}

		private void addReplacementColumn() {
			Column<Mod184, ImageResource> replacementColumn = new Column<Mod184, ImageResource>(
					new ImageResourceCell()) {
				@Override
				public ImageResource getValue(Mod184 mod184) {
					return (mod184.isComplementary() || mod184.isReplacement())  
							? AON.AON_RESOURCES.aonIconChecked()
							: AON.AON_RESOURCES.aonIconCheck();
				}
			};
			this.addColumn(replacementColumn, "C/S" );
			replacementColumn.setCellStyleNames(AON.AON_CSS.aonDataTableIconColumn());
			this.setColumnWidth(replacementColumn, 20, Unit.PX);
		}
	
		private void addDocumentColumn() {
			final TextColumn<Mod184> documentColumn = new TextColumn<Mod184>() {
				@Override
				public String getValue(Mod184 mod184) {
					return mod184.getDocument();
				}
			};
			this.addColumn(documentColumn, AON.MSG.document());
			this.setColumnWidth(documentColumn, 120, Unit.PX);
		}
		
		private void addNameColumn() {
			final TextColumn<Mod184> nameColumn = new TextColumn<Mod184>() {
				@Override
				public String getValue(Mod184 mod184) {
					return mod184.getName();
				}
			};
			this.addColumn(nameColumn, AON.MSG.name());
			this.setColumnWidth(nameColumn, "auto");
		}
	
	
		public Mod184 getSelected() {
			return model.getLastSelectedObject();
		}
	}
	
	private Model184CellTable table;
	
	public Model184Table(Model184Callback cbk) {
		table = new Model184CellTable(new FiscalModelProvidesKey<Mod184>());
		if (table.getSelectionModel() != null) {
			table.getSelectionModel().addSelectionChangeHandler(new com.google.gwt.view.client.SelectionChangeEvent.Handler() {
				
				@Override
				public void onSelectionChange(SelectionChangeEvent event) {
					SelectionEvent.fire(Model184Table.this, table.getSelected());
				}
			});
		}
		table.addRangeChangeHandler( new com.google.gwt.view.client.RangeChangeEvent.Handler() {

			@Override
			public void onRangeChange(RangeChangeEvent event) {
				Model184.SERVICE.getMod184s(Model184.getCurrentDomainName(), Model184.getCurrentDomain(),
						new AsyncCallback<LinkedList<Mod184>>() {
							@Override
							public void onSuccess(LinkedList<Mod184> result) {
								table.setRowData(result);
							}

							@Override
							public void onFailure(Throwable caught) {
								cbk.showError(AON.MSG.unableToReadDeclaration(caught.getMessage()));
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
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod184> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public void refresh() {
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
	}
	
	
	private Widget getToolbarPanel(Model184Callback cbk) {
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
		toolbar.setWidget(0, 0, new Label( "Modelo 184"));
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
				cbk.onNew();
			}
		});
		buttonContainer.add(newButton);
		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}
	
	
}
