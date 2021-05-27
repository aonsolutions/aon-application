package com.esferalia.aon.gwt.fiscal.client.mod180;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180Base.Model180BaseCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.RangeChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class Model180DetailTable extends SimpleLayoutPanel implements HasSelectionHandlers<Mod180Detail> {

	private static class Model180DetailCellTable extends CellTable<Mod180Detail> {
		private static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);
	
		private SingleSelectionModel<Mod180Detail> model;
		
		public Model180DetailCellTable(ProvidesKey<Mod180Detail> providesKey) {
			super(1,TABLE_STYLE, providesKey);
			this.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
			this.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
			
			addNameColumn();
			
			model = new SingleSelectionModel<Mod180Detail>(Model180.MOD180_DETAIL_PROVIDES_KEY);
			this.setSelectionModel(model);
			this.setEmptyTableWidget(new HTML(AON.MSG.noData()));
		}
	
		private void addNameColumn() {
			final TextColumn<Mod180Detail> nameColumn = new TextColumn<Mod180Detail>() {
				@Override
				public String getValue(Mod180Detail detail) {
					return AonStringUtils.defaultIfBlank(detail.getName(), AON.MSG.newPerceptor());
				}
				@Override
				public void render(Context context, Mod180Detail detail, SafeHtmlBuilder sb) {
					sb.appendHtmlConstant("<div style='");
					if (detail.isDirty()) {
						sb.appendHtmlConstant("font-weight:bold;");
					}
					if (detail.isDeleted()) {
						sb.appendHtmlConstant("text-decoration:line-through;");
					}
					sb.appendHtmlConstant("font-size: 0.9em;height: auto;overflow: hidden; padding-right: 3px; text-transform: uppercase;width: auto;'>");
					sb.appendEscaped( AonStringUtils.abbreviate( getValue(detail), 35 ));
					if (detail.isDirty()) {
						sb.appendEscaped("*");
					}
					sb.appendHtmlConstant("</div>");
				} 
				
			};
			this.addColumn(nameColumn);
			this.setColumnWidth(nameColumn, "auto");
		}
	
		public Mod180Detail getSelected() {
			return model.getSelectedObject();
		}
	}
	
	private Model180DetailCellTable table;
	private ScrollPanel tablePanel;
	
	public Model180DetailTable(Model180BaseCallback cbk, Integer selectedIndex) {
		table = new Model180DetailCellTable(Model180.MOD180_DETAIL_PROVIDES_KEY);
		if (table.getSelectionModel() != null) {
			table.getSelectionModel().addSelectionChangeHandler(new com.google.gwt.view.client.SelectionChangeEvent.Handler() {
				
				@Override
				public void onSelectionChange(SelectionChangeEvent event) {
					SelectionEvent.fire(Model180DetailTable.this, table.getSelected());
				}
			});
		}
		table.addRangeChangeHandler( new com.google.gwt.view.client.RangeChangeEvent.Handler() {

			@Override
			public void onRangeChange(RangeChangeEvent event) {
				table.setRowData(cbk.getMod180().getDetails());
			}
		});
		
		DockLayoutPanel tableDockLayout = new DockLayoutPanel(Unit.PX);
		tableDockLayout.setStyleName(AON.AON_CSS.aonBorderRight());
		tableDockLayout.addNorth(getToolbarPanel(cbk), 25);
		tablePanel = new ScrollPanel();
		tablePanel.addStyleName(AON.AON_CSS.aonScrollArea());
		tablePanel.add(table);
		tableDockLayout.add(tablePanel);
		refresh();
		if (table.getVisibleItemCount() == 0) {
			newPerceptor( cbk );	
		} else if ( selectedIndex != null) {
			table.getSelectionModel().setSelected(cbk.getMod180().getDetails().get(selectedIndex), true);	
		} else {
			table.getSelectionModel().setSelected( table.getVisibleItems().get(0), true);	
		}
		setWidget(tableDockLayout);
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod180Detail> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}

	public void refresh() {
		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
	}
	
	
	private Widget getToolbarPanel(Model180BaseCallback cbk) {
		FlowPanel westToolbar = new FlowPanel();
		westToolbar.setStyleName(AON.AON_CSS.aonFindingTitleToolbar());
		westToolbar.addStyleName(AON.AON_CSS.aonTextRight());
		westToolbar.addStyleName(AON.AON_CSS.aonWidthAll());
		FlexTable toolbar = new FlexTable();
		toolbar.getColumnFormatter().setWidth(0, "auto");
		toolbar.getColumnFormatter().setWidth(1, "1px;");
		toolbar.getColumnFormatter().setWidth(2, "1px;");
		toolbar.setCellPadding(0);
		toolbar.setCellSpacing(0);
		toolbar.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.setStyleName(AON.AON_CSS.aonFindingTitleInternal());
		titlePanel.add(new Label("Perceptores"));
		toolbar.setWidget(0, 0, titlePanel);
		toolbar.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());
		Button deleteDetailButton = new Button();
		Button restoreDeletedButton = new Button();
		Button newDetailButton = new Button();
		buttonContainer.add(deleteDetailButton);
		buttonContainer.add(restoreDeletedButton);
		buttonContainer.add(newDetailButton);
		westToolbar.add(toolbar);
		addSelectionHandler(new SelectionHandler<Mod180Detail>() {
			
			@Override
			public void onSelection(SelectionEvent<Mod180Detail> event) {
				deleteDetailButton.setVisible(!event.getSelectedItem().isDeleted());
				restoreDeletedButton.setVisible(event.getSelectedItem().isDeleted());
			}
		});
		
		deleteDetailButton.setTitle(AON.MSG.deleteAction());
		deleteDetailButton.setStyleName(AON.AON_CSS.aonIconCommandButton());
		deleteDetailButton.addStyleName(AON.AON_CSS.aonIconDelete());
		deleteDetailButton.addStyleName(AON.AON_CSS.aonMarginRight());
		deleteDetailButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				table.getSelected().setDeleted(true);
				restoreDeletedButton.setVisible(true);
				deleteDetailButton.setVisible(false);
				table.redraw();
			}
		});
		
		restoreDeletedButton.setTitle(AON.MSG.restoreAction());
		restoreDeletedButton.setStyleName(AON.AON_CSS.aonIconCommandButton());
		restoreDeletedButton.addStyleName(AON.AON_CSS.aonIconRedo());
		restoreDeletedButton.addStyleName(AON.AON_CSS.aonMarginRight());
		restoreDeletedButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				table.getSelected().setDeleted(false);
				if (!table.getSelected().isDirty()) {
					restoreDeletedButton.setVisible(false);
					deleteDetailButton.setVisible(true);
					table.redraw();
				}
			}
		});
		
		newDetailButton.setTitle(AON.MSG.newPerceptor());
		newDetailButton.setStyleName(AON.AON_CSS.aonIconCommandButton());
		newDetailButton.addStyleName(AON.AON_CSS.aonIconReset());
		newDetailButton.addStyleName(AON.AON_CSS.aonMarginRight());
		newDetailButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				newPerceptor( cbk );
			}

		});
		
		return westToolbar;
	}
	
	private void newPerceptor(Model180BaseCallback cbk) {
		cbk.getMod180().getDetails().add(
				new Mod180Detail()
					.setDirty(true)
					.setTempId((cbk.getMod180().getDetails().size() * (-1)))
			);
		table.setRowData(cbk.getMod180().getDetails());
		table.redraw();
		int i = cbk.getMod180().getDetails().size() - 1;
		table.getSelectionModel().setSelected(cbk.getMod180().getDetails().get(i),true);
		tablePanel.scrollToBottom();
	}

	public Integer getSelectionIndex() {
		if ( table.getSelected() != null) {
			int i = 0;
			for ( Mod180Detail det : table.getVisibleItems()) {
				if ( det == table.getSelected() ) {
					return i;
				}
				i++;
			}
		}
		return null;
	}
	
	
}
