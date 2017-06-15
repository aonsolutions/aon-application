package com.esferalia.aon.gwt.fiscal.client.mod200.e2016;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.cell.SizableTextInputCell;
import com.esferalia.aon.gwt.fiscal.client.mod200.Model200Table;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2016.Model2002016.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.ListDataProvider;

public class Page19 extends PageAbs {

	FlexTable table1;
	FlexTable table2;
	FlexTable table3;
	
	private ListDataProvider<String> groupEntitiesProvider;
	private CellTable<String> groupEntitiesTable;
	private Button newGroupEntity;

	private ListDataProvider<String> establishmentsProvider;
	private CellTable<String> establishmentsTable;
	private Button newEstablishments;
	
	public Page19( Model200PageCallback callback ) {
		super(callback);
		
		table1 = new FlexTable();
		table2 = new FlexTable();
		table3 = new FlexTable();
		
		ScrollPanel container = new ScrollPanel();
		container.setStyleName(AON.AON_CSS.aonScrollArea());
		FlowPanel baseContainerPanel = new FlowPanel();
		baseContainerPanel.setStyleName(AON.AON_CSS.aonFiscalContainer());
		
			FlowPanel gr1= new FlowPanel();
			gr1.setStyleName(AON.AON_CSS.aonGroup());
			
				FlowPanel grHeader1 = new FlowPanel();
				grHeader1.setStyleName(AON.AON_CSS.aonGroupTitle());
				grHeader1.add (new InlineLabel(AON.MSG.bussinessAmount1())); 
				gr1.add(grHeader1);
				
				FlowPanel grBody1 = new FlowPanel();
				grBody1.setStyleName(AON.AON_CSS.aonGroupBody());
				grBody1.add(table);
				
				
				FlowPanel grBody1Docs = new FlowPanel();
				grBody1Docs.setStyleName(AON.AON_CSS.aonGroupBody());
				grBody1Docs.addStyleName(AON.AON_CSS.aonWidth300());
				grBody1Docs.addStyleName(AON.AON_CSS.aonBlockCenter());
				grBody1Docs.addStyleName(AON.AON_CSS.aonMarginTop());				
				groupEntitiesTable = new CellTable<String>(25,Model200Table.TABLE_STYLE);
				groupEntitiesTable.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
				groupEntitiesTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
				groupEntitiesTable.setEmptyTableWidget(new HTML(AON.MSG.noData()));
				
				addGroupEntitiesColumn();
				addGroupEntitiesRemoveColumn();

				newGroupEntity = new Button();
				newGroupEntity.setStyleName(AON.AON_CSS.aonIconReset());
				newGroupEntity.addStyleName(AON.AON_CSS.aonBorderNone());
				newGroupEntity.addStyleName(AON.AON_CSS.aonMarginTop());
				newGroupEntity.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						groupEntitiesProvider.getList().add("[nif]");
						groupEntitiesTable.redraw();
					}
				});
				grBody1Docs.add(groupEntitiesTable);
				grBody1Docs.add(newGroupEntity);
				grBody1.add(grBody1Docs);
				gr1.add(grBody1);
		baseContainerPanel.add(gr1);	
			
			FlowPanel gr2= new FlowPanel();
			gr2.setStyleName(AON.AON_CSS.aonGroup());
			
				FlowPanel grHeader2 = new FlowPanel();
				grHeader2.setStyleName(AON.AON_CSS.aonGroupTitle());
				grHeader2.add (new InlineLabel(AON.MSG.bussinessAmount2())); 
				gr2.add(grHeader2);
				
				FlowPanel grBody2 = new FlowPanel();
				grBody2.setStyleName(AON.AON_CSS.aonGroupBody());
				grBody2.add(table1);
				FlowPanel grBody2Docs = new FlowPanel();
				grBody2Docs.setStyleName(AON.AON_CSS.aonGroupBody());
				grBody2Docs.addStyleName(AON.AON_CSS.aonWidth300());
				grBody2Docs.addStyleName(AON.AON_CSS.aonBlockCenter());
				grBody2Docs.addStyleName(AON.AON_CSS.aonMarginTop());
				
				establishmentsTable = new CellTable<String>(25,Model200Table.TABLE_STYLE);
				establishmentsTable.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
				establishmentsTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
				establishmentsTable.setEmptyTableWidget(new HTML(AON.MSG.noData()));
				
				addEstablishmentsColumn();
				addEstablishmentsRemoveColumn();

				newEstablishments = new Button();
				newEstablishments.setStyleName(AON.AON_CSS.aonIconReset());
				newEstablishments.addStyleName(AON.AON_CSS.aonBorderNone());
				newEstablishments.addStyleName(AON.AON_CSS.aonMarginTop());
				newEstablishments.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						establishmentsProvider.getList().add(new String());
						establishmentsTable.redraw();		    		
					}
				});
				grBody2Docs.add(establishmentsTable);
				grBody2Docs.add(newEstablishments);
				grBody2.add(grBody2Docs);
				gr2.add(grBody2);
		baseContainerPanel.add(gr2);	
			
			FlowPanel gr3= new FlowPanel();
			gr3.setStyleName(AON.AON_CSS.aonGroup());
			
				FlowPanel grHeader3 = new FlowPanel();
				grHeader3.setStyleName(AON.AON_CSS.aonGroupTitle());
				grHeader3.add (new InlineLabel(AON.MSG.bussinessAmount3())); 
				gr3.add(grHeader3);
				
				FlowPanel grBody3 = new FlowPanel();
				grBody3.setStyleName(AON.AON_CSS.aonGroupBody());
				grBody3.add(table2);
				gr3.add(grBody3);
		baseContainerPanel.add(gr3);	
			
		container.add(baseContainerPanel);
		initWidget(container);
		initializeTable();		
	}

	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "200px");
		paintKey(table, Mod2002016Key.CN987, 0);
		
		
		table1.setWidth("100%");
		table1.setCellSpacing(0);
		table1.getColumnFormatter().setWidth(1, "200px");
		paintKey(table1, Mod2002016Key.CN988, 0);
		paintKey(table1, Mod2002016Key.CNEST, 1);
		
		table2.setWidth("100%");
		table2.setCellSpacing(0);
		table2.getColumnFormatter().setWidth(1, "200px");
		paintKey(table2, Mod2002016Key.CN989, 0);
	}

	private void addGroupEntitiesColumn() {
		SizableTextInputCell input = new SizableTextInputCell(10);
		Column<String, String> documentColumn = new Column<String, String>(input) {
			@Override
			public String getValue(String object) {
				return object;
			}
		};
		documentColumn.setFieldUpdater(new FieldUpdater<String, String>() {
		    public void update(int index, String cp, String value) {
		    	groupEntitiesProvider.getList().set(index,value);
		    }
		});		
		groupEntitiesTable.addColumn(documentColumn, AON.MSG.bussinessAmount11());
		documentColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
		groupEntitiesTable.setColumnWidth(documentColumn, 100, Unit.PX);
	}
	
	private void addGroupEntitiesRemoveColumn() {
		ButtonCell removeButton = new ButtonCell( new DeleteButtonSafeHtmlTemplates())  {
			  @Override
			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
			    if (data != null) {
			      sb.append(data);
			    }
			  }
		};
		Column<String,String> col = new Column<String,String>(removeButton) {
		  public String getValue(String object) {
		    return AON.MSG.deleteAction();
		  }
		};
		col.setFieldUpdater(new FieldUpdater<String, String>() {
			
		    public void update(int index, String ca, String value) {
		    	ConfirmDialog cd = new ConfirmDialog();
		    	cd.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {
					
					@Override
					public void onCancel() {}
					
					@Override
					public void onAccept() {
						groupEntitiesProvider.getList().remove(index);
						groupEntitiesTable.redraw();
					}
				});
		    }
		});		
		groupEntitiesTable.addColumn(col);
		groupEntitiesTable.setColumnWidth(col, 20, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
	}

	private void addEstablishmentsColumn() {
		SizableTextInputCell input = new SizableTextInputCell(10);
		Column<String, String> documentColumn = new Column<String, String>(input) {
			@Override
			public String getValue(String object) {
				return object;
			}
		};
		documentColumn.setFieldUpdater(new FieldUpdater<String, String>() {
		    public void update(int index, String cp, String value) {
		    	establishmentsProvider.getList().set(index,value);
		    }
		});		
		establishmentsTable.addColumn(documentColumn, AON.MSG.bussinessAmount12());
		documentColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
		establishmentsTable.setColumnWidth(documentColumn, 100, Unit.PX);
	}

	private void addEstablishmentsRemoveColumn() {
		ButtonCell removeButton = new ButtonCell( new DeleteButtonSafeHtmlTemplates())  {
			  @Override
			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
			    if (data != null) {
			      sb.append(data);
			    }
			  }
		};
		Column<String,String> col = new Column<String,String>(removeButton) {
		  public String getValue(String object) {
		    return AON.MSG.deleteAction();
		  }
		};
		col.setFieldUpdater(new FieldUpdater<String, String>() {
			
		    public void update(int index, String ca, String value) {
		    	ConfirmDialog cd = new ConfirmDialog();
		    	cd.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {
					
					@Override
					public void onCancel() {}
					
					@Override
					public void onAccept() {
						establishmentsProvider.getList().remove(index);
						establishmentsTable.redraw();
					}
				});
		    }
		});		
		establishmentsTable.addColumn(col);
		establishmentsTable.setColumnWidth(col, 20, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
	}

	@Override
	protected void dump() {
		super.dump();
		groupEntitiesProvider = callback.getMod200Object().getMod200().getGroupEntities() == null
			?new ListDataProvider<String>()
			:new ListDataProvider<String>(callback.getMod200Object().getMod200().getGroupEntities());
		groupEntitiesProvider.addDataDisplay(groupEntitiesTable);
		groupEntitiesTable.redraw();
		
		establishmentsProvider = callback.getMod200Object().getMod200().getEstablishments()  == null
				?new ListDataProvider<String>()
				:new ListDataProvider<String>(callback.getMod200Object().getMod200().getEstablishments());
		establishmentsProvider.addDataDisplay(establishmentsTable);
		establishmentsTable.redraw();
	}
	
	@Override
	protected void populate() {
		callback.getMod200Object().getMod200().setGroupEntities (new LinkedList<String>(groupEntitiesProvider.getList()));
		callback.getMod200Object().getMod200().setEstablishments(new LinkedList<String>(establishmentsProvider.getList()));
		
	}

}
