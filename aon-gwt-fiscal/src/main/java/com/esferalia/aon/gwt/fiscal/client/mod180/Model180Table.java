package com.esferalia.aon.gwt.fiscal.client.mod180;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180.Model180Callback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelProvidesKey;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
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

public class Model180Table extends SimpleLayoutPanel implements HasSelectionHandlers<Mod180>{

	private static class Model180CellTable extends CellTable<Mod180> {
		private static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);
		
		public static final ProvidesKey<Mod180> MOD180_PROVIDES_KEY = new ProvidesKey<Mod180>() {
			@Override
			public Object getKey(Mod180 mod180) {
				return mod180 == null ? null : mod180.getId();
			}
		};
		private NoSelectionModel<Mod180> model;
		
		public Model180CellTable(ProvidesKey<Mod180> providesKey) {
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
			
			model = new NoSelectionModel<Mod180>(MOD180_PROVIDES_KEY);
			this.setSelectionModel(model);
			this.setEmptyTableWidget(new HTML(AON.MSG.noData()));
		}
	
		private void addSelectorColumn() {
			final Column<Mod180, ImageResource> selectorColumn = new Column<Mod180, ImageResource>(
					new ImageResourceCell()) {
				@Override
				public ImageResource getValue(Mod180 mod180) {
					return AON.AON_RESOURCES.aonIconRowSelector();
				}
			};
			this.addColumn(selectorColumn);
			this.setColumnWidth(selectorColumn, 20, Unit.PX);
		}
	
		private void addAdministrationColumn() {
			final Column<Mod180, ImageResource> iconColumn = new Column<Mod180, ImageResource>(
					new ImageResourceCell()) {
				@Override
				public ImageResource getValue(Mod180 mod180) {
					return FiscalModelUtils.getAdministrationIconResource(mod180.getAdministration());
				}
			};
			this.addColumn(iconColumn, "A" );
			this.setColumnWidth(iconColumn, 20, Unit.PX);
		}
		
		private void addYearColumn() {
			final TextColumn<Mod180> yearColumn = new TextColumn<Mod180>() {
				@Override
				public String getValue(Mod180 mod180) {
					return Integer.toString(mod180.getYear());
				}
			};
			this.addColumn(yearColumn, AON.MSG.fiscalYear());
			yearColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
			this.setColumnWidth(yearColumn, 50, Unit.PX);
		}
		
		private void addStatusColumn() {
			final Column<Mod180, ImageResource> iconColumn = new Column<Mod180, ImageResource>(
					new ImageResourceCell()) {
				@Override
				public ImageResource getValue(Mod180 model) {
					return FiscalModelUtils.getStatusImage(model.getStatus());
				}
			};
			this.addColumn(iconColumn, " " );
			this.setColumnWidth(iconColumn, 20, Unit.PX);
		}
	
		private void addStatusLabelColumn() {
			final TextColumn<Mod180> statusLabelColumn = new TextColumn<Mod180>() {
				@Override
				public String getValue(Mod180 mod180) {
					return mod180.getStatus().getName();
				}
			};
			this.addColumn(statusLabelColumn, AON.MSG.status() );
			this.setColumnWidth(statusLabelColumn, 80, Unit.PX);
		}

		private void addReplacementColumn() {
			Column<Mod180, ImageResource> replacementColumn = new Column<Mod180, ImageResource>(
					new ImageResourceCell()) {
				@Override
				public ImageResource getValue(Mod180 mod180) {
					return (mod180.isComplementary() || mod180.isReplacement())  
							? AON.AON_RESOURCES.aonIconChecked()
							: AON.AON_RESOURCES.aonIconCheck();
				}
			};
			this.addColumn(replacementColumn, "C/S" );
			replacementColumn.setCellStyleNames(AON.AON_CSS.aonDataTableIconColumn());
			this.setColumnWidth(replacementColumn, 20, Unit.PX);
		}
	
		private void addDocumentColumn() {
			final TextColumn<Mod180> documentColumn = new TextColumn<Mod180>() {
				@Override
				public String getValue(Mod180 mod180) {
					return mod180.getDocument();
				}
			};
			this.addColumn(documentColumn, AON.MSG.document());
			this.setColumnWidth(documentColumn, 120, Unit.PX);
		}
		
		private void addNameColumn() {
			final TextColumn<Mod180> nameColumn = new TextColumn<Mod180>() {
				@Override
				public String getValue(Mod180 mod180) {
					return mod180.getName();
				}
			};
			this.addColumn(nameColumn, AON.MSG.name());
			this.setColumnWidth(nameColumn, "auto");
		}
	
	
		public Mod180 getSelected() {
			return model.getLastSelectedObject();
		}
	}
	
	private Model180CellTable table;
	
	public Model180Table(Model180ModuleOptions options, Model180Callback cbk) {
		table = new Model180CellTable(new FiscalModelProvidesKey<Mod180>());
		if (table.getSelectionModel() != null) {
			table.getSelectionModel().addSelectionChangeHandler(new com.google.gwt.view.client.SelectionChangeEvent.Handler() {
				
				@Override
				public void onSelectionChange(SelectionChangeEvent event) {
					SelectionEvent.fire(Model180Table.this, table.getSelected());
				}
			});
		}
		table.addRangeChangeHandler( new com.google.gwt.view.client.RangeChangeEvent.Handler() {

			@Override
			public void onRangeChange(RangeChangeEvent event) {
				Model180.SERVICE.getMod180s(options.getDomainName(), options.getUser(), options.getDomain(),
						new AsyncCallback<LinkedList<Mod180>>() {
							@Override
							public void onSuccess(LinkedList<Mod180> result) {
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
		tableDockLayout.addNorth(getToolbarPanel(options,cbk), 25);
		ScrollPanel tablePanel = new ScrollPanel();
		tablePanel.addStyleName(AON.AON_CSS.aonScrollArea());
		tablePanel.add(table);
		tableDockLayout.add(tablePanel);
		setWidget(tableDockLayout);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod180> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public void refresh() {
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
	}
	
	
	private Widget getToolbarPanel(Model180ModuleOptions options, Model180Callback cbk) {
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
		toolbar.setWidget(0, 0, new Label( "Modelo 180"));
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
				cbk.onNew(options);
			}
		});
		buttonContainer.add(newButton);
		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}
	
	
}
