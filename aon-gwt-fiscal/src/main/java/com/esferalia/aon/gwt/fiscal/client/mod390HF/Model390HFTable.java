package com.esferalia.aon.gwt.fiscal.client.mod390HF;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod390HF.Model390HF.Model390HFCallback;
import com.esferalia.aon.gwt.fiscal.client.model.FiscalModelProvidesKey;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.watson.util.AonStringUtils;
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

public class Model390HFTable extends SimpleLayoutPanel implements HasSelectionHandlers<Mod390HF>{
	
	private static class Model390HFCellTable extends CellTable<Mod390HF> {
		private static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);
		

		private NoSelectionModel<Mod390HF> model;
		
		public Model390HFCellTable(ProvidesKey<Mod390HF> providesKey) {
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
			addAmountColumn();
			addDeclarationTypeColumn();
			addFinanceStatusColumn();
			
			model = new NoSelectionModel<Mod390HF>(providesKey);
			this.setSelectionModel(model);
			this.setEmptyTableWidget(new HTML(AON.MSG.noData()));
		}

		private void addSelectorColumn() {
			final Column<Mod390HF, ImageResource> selectorColumn = new Column<Mod390HF, ImageResource>(
					new ImageResourceCell()) {
				@Override
				public ImageResource getValue(Mod390HF model) {
					return AON.AON_RESOURCES.aonIconRowSelector();
				}
			};
			this.addColumn(selectorColumn);
			this.setColumnWidth(selectorColumn, 20, Unit.PX);
		}

		private void addYearColumn() {
			final TextColumn<Mod390HF> yearColumn = new TextColumn<Mod390HF>() {
				@Override
				public String getValue(Mod390HF model) {
					return Integer.toString(model.getYear());
				}
			};
			this.addColumn(yearColumn, AON.MSG.fiscalYear());
			yearColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
			this.setColumnWidth(yearColumn, 50, Unit.PX);
		}

		private void addAdministrationColumn() {
			final Column<Mod390HF, ImageResource> iconColumn = new Column<Mod390HF, ImageResource>(
					new ImageResourceCell()) {
				@Override
				public ImageResource getValue(Mod390HF model) {
					return FiscalModelUtils.getAdministrationIconResource(model.getAdministration());
				}
			};
			this.addColumn(iconColumn, "A" );
			this.setColumnWidth(iconColumn, 20, Unit.PX);
		}
		
		private void addModelColumn() {
			final TextColumn<Mod390HF> modelColumn = new TextColumn<Mod390HF>() {
				@Override
				public String getValue(Mod390HF model) {
					return FiscalModelUtils.getModelName(model);
				}
			};
			this.addColumn(modelColumn, AON.MSG.model());
			modelColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
			this.setColumnWidth(modelColumn, 50, Unit.PX);
		}

		private void addStatusColumn() {
			final Column<Mod390HF, ImageResource> iconColumn = new Column<Mod390HF, ImageResource>(
					new ImageResourceCell()) {
				@Override
				public ImageResource getValue(Mod390HF model) {
					return FiscalModelUtils.getStatusImage(model.getStatus());
				}
			};
			this.addColumn(iconColumn, "E" );
			this.setColumnWidth(iconColumn, 20, Unit.PX);
		}
		
		private void addStatusLabelColumn() {
			final TextColumn<Mod390HF> statusLabelColumn = new TextColumn<Mod390HF>() {
				@Override
				public String getValue(Mod390HF fm) {
					return fm.getStatus().getName();
				}
			};
			this.addColumn(statusLabelColumn, AON.MSG.status() );
			this.setColumnWidth(statusLabelColumn, 80, Unit.PX);
		}

		private void addComplementaryColumn() {
			Column<Mod390HF, ImageResource> complementaryColumn = new Column<Mod390HF, ImageResource>(
					new ImageResourceCell()) {
				@Override
				public ImageResource getValue(Mod390HF model) {
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
			final TextColumn<Mod390HF> documentColumn = new TextColumn<Mod390HF>() {
				@Override
				public String getValue(Mod390HF model) {
					return model.getDocument();
				}
			};
			this.addColumn(documentColumn, AON.MSG.document());
			this.setColumnWidth(documentColumn, 100, Unit.PX);
		}

		private void addNameColumn() {
			final TextColumn<Mod390HF> nameColumn = new TextColumn<Mod390HF>() {
				@Override
				public String getValue(Mod390HF model) {
					return model.getFullName();
				}
			};
			this.addColumn(nameColumn, AON.MSG.name());
			this.setColumnWidth(nameColumn, "auto");
		}	

		private void addAmountColumn() {
			final TextColumn<Mod390HF> amountColumn = new TextColumn<Mod390HF>() {
				@Override
				public String getValue(Mod390HF model) {
					return AON.FMT.format(model.getResult()) ;
				}
			};
			this.addColumn(amountColumn, AON.MSG.result());
			amountColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
			this.setColumnWidth(amountColumn, 100, Unit.PX);
		}	

		private void addDeclarationTypeColumn() {
			final TextColumn<Mod390HF> decTypeColumn = new TextColumn<Mod390HF>() {
				@Override
				public String getValue(Mod390HF model) {
					return (model.getDeclarationType()!=null?model.getDeclarationType().getDescription():"");
				}
			};
			this.addColumn(decTypeColumn, " ");
			this.setColumnWidth(decTypeColumn, 100, Unit.PX);
		}	

		private void addFinanceStatusColumn() {
			final TextColumn<Mod390HF> financeStatusColumn = new TextColumn<Mod390HF>() {
				@Override
				public String getValue(Mod390HF model) {
					if (model.getFinance() != null && model.getFinance().getFinanceStatus() != null) {
						return model.getFinance().getFinanceStatus().getDescription();
					}
					return AonStringUtils.EMPTY;
				}
			};
			this.addColumn(financeStatusColumn, AON.MSG.financeStatus());
			financeStatusColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
			this.setColumnWidth(financeStatusColumn, 150, Unit.PX);
		}	

		public Mod390HF getSelected() {
			return model.getLastSelectedObject();
		}
	}

	
	
	
	private Model390HFCellTable table;
	
	public Model390HFTable(Model390HFModuleOptions options, Model390HFCallback cbk) {
		table = new Model390HFCellTable(new FiscalModelProvidesKey<Mod390HF>());
		if (table.getSelectionModel() != null) {
			table.getSelectionModel().addSelectionChangeHandler(new com.google.gwt.view.client.SelectionChangeEvent.Handler() {
				
				@Override
				public void onSelectionChange(SelectionChangeEvent event) {
					SelectionEvent.fire(Model390HFTable.this, table.getSelected());
				}
			});
		}
		table.addRangeChangeHandler( new com.google.gwt.view.client.RangeChangeEvent.Handler() {
			
			@Override
			public void onRangeChange(RangeChangeEvent event) {
				Model390HF.MOD_SERVICE.getMod390HFs(options.getDomainName(), options.getDomain(),options.getUser(),
						new AsyncCallback<LinkedList<Mod390HF>>() {
							@Override
							public void onSuccess(LinkedList<Mod390HF> result) {
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
		tableDockLayout.addNorth(getToolbarPanel(cbk, options), 25);
		ScrollPanel tablePanel = new ScrollPanel();
		tablePanel.addStyleName(AON.AON_CSS.aonScrollArea());
		tablePanel.add(table);
		tableDockLayout.add(tablePanel);
		setWidget(tableDockLayout);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod390HF> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public void refresh() {
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
	}
	
	
	private Widget getToolbarPanel(Model390HFCallback cbk, Model390HFModuleOptions options) {
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
				cbk.onNew( options );
			}
		});
		buttonContainer.add(newButton);
		
		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}
	
}
