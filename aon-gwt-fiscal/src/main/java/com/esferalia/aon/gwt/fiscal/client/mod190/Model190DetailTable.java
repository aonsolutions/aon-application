package com.esferalia.aon.gwt.fiscal.client.mod190;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190.Model190Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model190DetailTable extends SimpleLayoutPanel implements HasSelectionHandlers<Mod190Detail> {

	private Mod190 model;
	private ScrollPanel tablePanel;
	private Integer selectionIndex;
	
	public Model190DetailTable(Model190Callback cbk, Mod190 model, Integer selectedIndex) {
		this.model = model;
		DockLayoutPanel tableDockLayout = new DockLayoutPanel(Unit.PX);
		tableDockLayout.setStyleName(AON.CSS.aonBorderRight());
		tableDockLayout.addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		
		tablePanel = new ScrollPanel();
		tablePanel.addStyleName(AON.CSS.aonScrollArea());
		tableDockLayout.add(tablePanel);
		if (model.getDetails() == null || model.getDetails().isEmpty()) {
			refresh();
			newPerceptor( );	
		} else if ( selectedIndex != null) {
			selectionIndex = selectedIndex;
			refresh();
			SelectionEvent.fire(Model190DetailTable.this, getSelected() );			
		} else {
			selectionIndex = 0;
			refresh();
		}
		setWidget(tableDockLayout);
	}
	
	private Mod190Detail getSelected() {
		return model.getDetails().get(selectionIndex);
	}

	private Widget getToolbarPanel() {
		AonToolbar toolbar = new AonToolbar("");
		
		AonToolbarButton newDetailButton = new AonToolbarButton(AON.MSG.newPerceptor(),AON.CSS.aonIconAdd());
		AonToolbarButton deleteDetailButton = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
		AonToolbarButton restoreDeletedButton = new AonToolbarButton(AON.MSG.restoreAction(),AON.CSS.aonIconRestore());

		addSelectionHandler(event -> {
			deleteDetailButton.setVisible(!event.getSelectedItem().isDeleted());
			restoreDeletedButton.setVisible(event.getSelectedItem().isDeleted());
		});

		newDetailButton.addClickHandler(event -> newPerceptor( ));
		toolbar.add(newDetailButton);
		
		restoreDeletedButton.addClickHandler(event -> {
			getSelected().setDeleted(false);
			if (!getSelected().isDirty()) {
				restoreDeletedButton.setVisible(false);
				deleteDetailButton.setVisible(true);
				refresh();
			}
		});
		toolbar.add(restoreDeletedButton);
		
		deleteDetailButton.addClickHandler(event -> {
			getSelected().setDeleted(true);
			restoreDeletedButton.setVisible(true);
			deleteDetailButton.setVisible(false);
			refresh();
		});
		toolbar.add(deleteDetailButton);
		
		return toolbar;
	}
	
	public void styleTable(AonDisplayGrid tab) {
		for ( int i = 0 ; i < tab.getWidgetCount(); i++) {
			if (i == selectionIndex) {
				tab.getWidget(i).addStyleName(AON.CSS.aonBackgroundLigthBlue());
			} else {
				tab.getWidget(i).removeStyleName(AON.CSS.aonBackgroundLigthBlue());
			}
		}
	}

	public void refresh() {
		AonDisplayGrid tab = new AonDisplayGrid();
		tab.addStyleName(AON.CSS.aonNoPadding());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tablePanel.clear();
		tablePanel.setWidget(tab);
		int i = 0;
		for (Mod190Detail detail : this.model.getDetails()) {
			AonDisplayGridRow row = tab.addRow();
			row.addStyleName(AON.CSS.aonClickable());
			if (i == Model190DetailTable.this.selectionIndex) {
				row.addStyleName(AON.CSS.aonBackgroundLigthBlue());
			}
			final int idx = i;
			row.addClickHandler( event ->  {
				Model190DetailTable.this.selectionIndex = idx;
				styleTable( tab );				
				SelectionEvent.fire(Model190DetailTable.this, detail);	
			});
			String name = AonStringUtils.abbreviate(AonStringUtils.defaultIfBlank(detail.getName(), AON.MSG.newPerceptor()) , 30 );
			InlineLabel nameLabel = new InlineLabel( name );
			if (detail.isDirty()) {
				nameLabel.setText("* " + name);
			} else {
				nameLabel.setText(name);
			}
			if (detail.isDeleted()) {
				nameLabel.addStyleName(AON.CSS.aonTextLineThrough());
			} else {
				nameLabel.removeStyleName(AON.CSS.aonTextLineThrough());
			}
			row.addCell( new InlineLabel( detail.getKey() ) , AON.CSS.aonTextCenter(),AON.CSS.aonWidth20() )
			   .addCell( new InlineLabel( AonStringUtils.defaultIfBlank(detail.getSubKey())), AON.CSS.aonTextCenter(),AON.CSS.aonWidth20())
			   .addCell( nameLabel ,AON.CSS.aonWidthAuto());
			i++;
		}
	}
	
	private void newPerceptor() {
		Mod190Detail detail = new Mod190Detail()
				.setDirty(true)
				.setTempId((model.getDetails().size() * (-1)));
		model.getDetails().add(detail);
		selectionIndex = model.getDetails().size() - 1;
		refresh();
		tablePanel.scrollToBottom();
		SelectionEvent.fire(Model190DetailTable.this, detail);
	}

	public Integer getSelectionIndex() {
		return selectionIndex;
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<Mod190Detail> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
	
	
//	private static class Model190DetailCellTable extends CellTable<Mod190Detail> {
//		private static final CellTable.Resources TABLE_STYLE = GWT.create(AonCellTable.class);
//	
//		private SingleSelectionModel<Mod190Detail> model;
//		
//		public Model190DetailCellTable(ProvidesKey<Mod190Detail> providesKey) {
//			super(1,TABLE_STYLE, providesKey);
//			this.setKeyboardPagingPolicy(KeyboardPagingPolicy.CHANGE_PAGE);
//			this.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
//			
//			addNameColumn();
//			
//			model = new SingleSelectionModel<Mod190Detail>(Model190.MOD190_DETAIL_PROVIDES_KEY);
//			this.setSelectionModel(model);
//			this.setEmptyTableWidget(new HTML(AON.MSG.noData()));
//		}
//	
//		private void addNameColumn() {
//			final TextColumn<Mod190Detail> nameColumn = new TextColumn<Mod190Detail>() {
//				@Override
//				public String getValue(Mod190Detail detail) {
//					return AonStringUtils.abbreviate( AonStringUtils.defaultIfBlank(detail.getName(), AON.MSG.newPerceptor()) , 30 )
//							+ " ("+detail.getKey()
//							+ (AonStringUtils.isBlank( detail.getSubKey() )?"":(","+detail.getSubKey()))
//							+")"
//							;
//				}
//				@Override
//				public void render(Context context, Mod190Detail detail, SafeHtmlBuilder sb) {
//					sb.appendHtmlConstant("<div style='");
//					if (detail.isDirty()) {
//						sb.appendHtmlConstant("font-weight:bold;");
//					}
//					if (detail.isDeleted()) {
//						sb.appendHtmlConstant("text-decoration:line-through;");
//					}
//					sb.appendHtmlConstant("font-size: 0.9em;height: auto;overflow: hidden; padding-right: 3px; text-transform: uppercase;width: auto;'>");
//					sb.appendEscaped( getValue(detail) );
//					if (detail.isDirty()) {
//						sb.appendEscaped("*");
//					}
//					sb.appendHtmlConstant("</div>");
//				} 
//				
//			};
//			this.addColumn(nameColumn);
//			this.setColumnWidth(nameColumn, "auto");
//		}
//	
//		public Mod190Detail getSelected() {
//			return model.getSelectedObject();
//		}
//	}
	
//	private Model190DetailCellTable table;
	
//	public Model190DetailTable(Model190Callback cbk, Integer selectedIndex) {
//		table = new Model190DetailCellTable(Model190.MOD190_DETAIL_PROVIDES_KEY);
//		if (table.getSelectionModel() != null) {
//			table.getSelectionModel().addSelectionChangeHandler(new com.google.gwt.view.client.SelectionChangeEvent.Handler() {
//				
//				@Override
//				public void onSelectionChange(SelectionChangeEvent event) {
//					SelectionEvent.fire(Model190DetailTable.this, table.getSelected());
//				}
//			});
//		}
//		table.addRangeChangeHandler( new com.google.gwt.view.client.RangeChangeEvent.Handler() {
//
//			@Override
//			public void onRangeChange(RangeChangeEvent event) {
//				table.setRowData(cbk.getMod190().getDetails());
//			}
//		});
//		
//		DockLayoutPanel tableDockLayout = new DockLayoutPanel(Unit.PX);
//		tableDockLayout.setStyleName(AON.CSS.aonBorderRight());
//		tableDockLayout.addNorth(getToolbarPanel(cbk), 25);
//		tablePanel = new ScrollPanel();
//		tablePanel.addStyleName(AON.CSS.aonScrollArea());
//		tablePanel.add(table);
//		tableDockLayout.add(tablePanel);
//		refresh();
//		if (table.getVisibleItemCount() == 0) {
//			newPerceptor( cbk );	
//		} else if ( selectedIndex != null) {
//			table.getSelectionModel().setSelected(cbk.getMod190().getDetails().get(selectedIndex), true);	
//		} else {
//			table.getSelectionModel().setSelected( table.getVisibleItems().get(0), true);	
//		}
//		setWidget(tableDockLayout);
//	}

//	public void refresh() {
//		table.setVisibleRangeAndClearData(table.getVisibleRange(), true);
//	}
	
	
//	private Widget getToolbarPanel(Model190BaseCallback cbk) {
//		FlowPanel westToolbar = new FlowPanel();
//		westToolbar.setStyleName(AON.CSS.aonFindingTitleToolbar());
//		westToolbar.addStyleName(AON.CSS.aonTextRight());
//		westToolbar.addStyleName(AON.CSS.aonWidthAll());
//		FlexTable toolbar = new FlexTable();
//		toolbar.getColumnFormatter().setWidth(0, "auto");
//		toolbar.getColumnFormatter().setWidth(1, "1px;");
//		toolbar.getColumnFormatter().setWidth(2, "1px;");
//		toolbar.setCellPadding(0);
//		toolbar.setCellSpacing(0);
//		toolbar.setStyleName(AON.CSS.aonWidthAll());
//		FlowPanel titlePanel = new FlowPanel();
//		titlePanel.setStyleName(AON.CSS.aonFindingTitleInternal());
//		titlePanel.add(new Label("Perceptores"));
//		toolbar.setWidget(0, 0, titlePanel);
//		toolbar.getCellFormatter().setStyleName(0,0, AON.CSS.aonFindingTitle());
//		toolbar.getCellFormatter().addStyleName(0,0, AON.CSS.aonBold());
//		toolbar.getCellFormatter().addStyleName(0,0, AON.CSS.aonNowrap());
//		toolbar.setWidget(0, 1, new Label());
//		toolbar.getCellFormatter().setStyleName(0,1, AON.CSS.aonFindingSubtitleIternal());
//		FlowPanel buttonContainer = new FlowPanel();
//		buttonContainer.setStyleName(AON.CSS.aonFindingToolbarItemGroup());
//		toolbar.setWidget(0, 2, buttonContainer);
//		toolbar.getCellFormatter().setStyleName(0,2, AON.CSS.aonFindingToolbar());
//		Button deleteDetailButton = new Button();
//		Button restoreDeletedButton = new Button();
//		Button newDetailButton = new Button();
//		buttonContainer.add(deleteDetailButton);
//		buttonContainer.add(restoreDeletedButton);
//		buttonContainer.add(newDetailButton);
//		westToolbar.add(toolbar);
//		addSelectionHandler(new SelectionHandler<Mod190Detail>() {
//			
//			@Override
//			public void onSelection(SelectionEvent<Mod190Detail> event) {
//				deleteDetailButton.setVisible(!event.getSelectedItem().isDeleted());
//				restoreDeletedButton.setVisible(event.getSelectedItem().isDeleted());
//			}
//		});
//		
//		deleteDetailButton.setTitle(AON.MSG.deleteAction());
//		deleteDetailButton.setStyleName(AON.CSS.aonIconCommandButton());
//		deleteDetailButton.addStyleName(AON.CSS.aonIconDelete());
//		deleteDetailButton.addStyleName(AON.CSS.aonMarginRight());
//		deleteDetailButton.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				table.getSelected().setDeleted(true);
//				restoreDeletedButton.setVisible(true);
//				deleteDetailButton.setVisible(false);
//				table.redraw();
//			}
//		});
//		
//		restoreDeletedButton.setTitle(AON.MSG.restoreAction());
//		restoreDeletedButton.setStyleName(AON.CSS.aonIconCommandButton());
//		restoreDeletedButton.addStyleName(AON.CSS.aonIconRedo());
//		restoreDeletedButton.addStyleName(AON.CSS.aonMarginRight());
//		restoreDeletedButton.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				table.getSelected().setDeleted(false);
//				if (!table.getSelected().isDirty()) {
//					restoreDeletedButton.setVisible(false);
//					deleteDetailButton.setVisible(true);
//					table.redraw();
//				}
//			}
//		});
//		
//		newDetailButton.setTitle(AON.MSG.newPerceptor());
//		newDetailButton.setStyleName(AON.CSS.aonIconCommandButton());
//		newDetailButton.addStyleName(AON.CSS.aonIconReset());
//		newDetailButton.addStyleName(AON.CSS.aonMarginRight());
//		newDetailButton.addClickHandler(new ClickHandler() {
//			
//			@Override
//			public void onClick(ClickEvent event) {
//				newPerceptor( cbk );
//			}
//
//		});
//		
//		return westToolbar;
//	}
	

}
