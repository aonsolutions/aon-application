// COMUNICACION IMPORTE NETO CIFRA DE NEGOCIO
package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Model2002020.Model200PageCallback;
import com.esferalia.aon.occam.api.model.GroupEntitie;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Key;
import com.esferalia.aon.occam.api.model.type.Country;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

public class Page19 extends PageAbs {

//	private FlexTable table;
//	private FlexTable table1;
//	private FlexTable table2;
//	
//	private ListDataProvider<GroupEntitie> groupEntitiesProvider;
//	private CellTable<GroupEntitie> groupEntitiesTable;
//	private Button newGroupEntity;
//
//	private ListDataProvider<String> establishmentsProvider;
//	private CellTable<String> establishmentsTable;
//	private Button newEstablishments;
	
	public Page19( Model200PageCallback callback ) {
		super(callback);
//		table  = new FlexTable();
//		table1 = new FlexTable();
//		table2 = new FlexTable();
//		
//		ScrollPanel container = new ScrollPanel();
//		container.setStyleName(AON.AON_CSS.aonScrollArea());
//		FlowPanel baseContainerPanel = new FlowPanel();
//		baseContainerPanel.setStyleName(AON.AON_CSS.aonFiscalContainer());
//		
//			FlowPanel gr1= new FlowPanel();
//			gr1.setStyleName(AON.AON_CSS.aonGroup());
//			
//				FlowPanel grHeader1 = new FlowPanel();
//				grHeader1.setStyleName(AON.AON_CSS.aonGroupTitle());
//				grHeader1.add (new InlineLabel(AON.MSG.bussinessAmount1())); 
//				gr1.add(grHeader1);
//				
//				FlowPanel grBody1 = new FlowPanel();
//				grBody1.setStyleName(AON.AON_CSS.aonGroupBody());
//				grBody1.add(table);
//				
//				
//				FlowPanel grBody1Docs = new FlowPanel();
//				grBody1Docs.setStyleName(AON.AON_CSS.aonGroupBody());
//				grBody1Docs.addStyleName(AON.AON_CSS.aonWidth300());
//				grBody1Docs.addStyleName(AON.AON_CSS.aonBlockCenter());
//				grBody1Docs.addStyleName(AON.AON_CSS.aonMarginTop());				
//
//				groupEntitiesTable = new CellTable<GroupEntitie>(35,Model200Table.TABLE_STYLE);
//				groupEntitiesTable.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
//				groupEntitiesTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
//				groupEntitiesTable.setEmptyTableWidget(new HTML(AON.MSG.noData()));
//				
//				addGroupEntitiesDocumentColumn();
//				addGroupEntitiesCountryColumn();
//				addGroupEntitiesRemoveColumn();
//
//				newGroupEntity = new Button();
//				newGroupEntity.setStyleName(AON.AON_CSS.aonIconReset());
//				newGroupEntity.addStyleName(AON.AON_CSS.aonBorderNone());
//				newGroupEntity.addStyleName(AON.AON_CSS.aonMarginTop());
//				newGroupEntity.addClickHandler(new ClickHandler() {
//					
//					@Override
//					public void onClick(ClickEvent event) {
//						groupEntitiesProvider.getList().add(new GroupEntitie().setCountry("ES"));
//						groupEntitiesTable.redraw();
//					}
//				});
//				grBody1Docs.add(groupEntitiesTable);
//				grBody1Docs.add(newGroupEntity);
//				grBody1.add(grBody1Docs);
//				gr1.add(grBody1);
//		baseContainerPanel.add(gr1);	
//			
//			FlowPanel gr2= new FlowPanel();
//			gr2.setStyleName(AON.AON_CSS.aonGroup());
//			
//				FlowPanel grHeader2 = new FlowPanel();
//				grHeader2.setStyleName(AON.AON_CSS.aonGroupTitle());
//				grHeader2.add (new InlineLabel(AON.MSG.bussinessAmount2())); 
//				gr2.add(grHeader2);
//				
//				FlowPanel grBody2 = new FlowPanel();
//				grBody2.setStyleName(AON.AON_CSS.aonGroupBody());
//				grBody2.add(table1);
//				FlowPanel grBody2Docs = new FlowPanel();
//				grBody2Docs.setStyleName(AON.AON_CSS.aonGroupBody());
//				grBody2Docs.addStyleName(AON.AON_CSS.aonWidth300());
//				grBody2Docs.addStyleName(AON.AON_CSS.aonBlockCenter());
//				grBody2Docs.addStyleName(AON.AON_CSS.aonMarginTop());
//				
//				establishmentsTable = new CellTable<String>(25,Model200Table.TABLE_STYLE);
//				establishmentsTable.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
//				establishmentsTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
//				establishmentsTable.setEmptyTableWidget(new HTML(AON.MSG.noData()));
//				
//				addEstablishmentsColumn();
//				addEstablishmentsRemoveColumn();
//
//				newEstablishments = new Button();
//				newEstablishments.setStyleName(AON.AON_CSS.aonIconReset());
//				newEstablishments.addStyleName(AON.AON_CSS.aonBorderNone());
//				newEstablishments.addStyleName(AON.AON_CSS.aonMarginTop());
//				newEstablishments.addClickHandler(new ClickHandler() {
//					
//					@Override
//					public void onClick(ClickEvent event) {
//						establishmentsProvider.getList().add(new String());
//						establishmentsTable.redraw();		    		
//					}
//				});
//				grBody2Docs.add(establishmentsTable);
//				grBody2Docs.add(newEstablishments);
//				grBody2.add(grBody2Docs);
//				gr2.add(grBody2);
//		baseContainerPanel.add(gr2);	
//			
//			FlowPanel gr3= new FlowPanel();
//			gr3.setStyleName(AON.AON_CSS.aonGroup());
//			
//				FlowPanel grHeader3 = new FlowPanel();
//				grHeader3.setStyleName(AON.AON_CSS.aonGroupTitle());
//				grHeader3.add (new InlineLabel(AON.MSG.bussinessAmount3())); 
//				gr3.add(grHeader3);
//				
//				FlowPanel grBody3 = new FlowPanel();
//				grBody3.setStyleName(AON.AON_CSS.aonGroupBody());
//				grBody3.add(table2);
//				gr3.add(grBody3);
//		baseContainerPanel.add(gr3);	
//			
//		container.add(baseContainerPanel);
//		initWidget(container);
		addBasePanel();
		initializeTable();		
	}

	@Override
	protected void initializeTable() {
//		table.setWidth("100%");
//		table.setCellSpacing(0);
//		table.getColumnFormatter().setWidth(1, "200px");
//		paintKey(table, Mod2002020Key.CN987, 0);
//		
//		table1.setWidth("100%");
//		table1.setCellSpacing(0);
//		table1.getColumnFormatter().setWidth(1, "200px");
//		paintKey(table1, Mod2002020Key.CN988, 0);
//		paintKey(table1, Mod2002020Key.CNEST, 1);
//		
//		table2.setWidth("100%");
//		table2.setCellSpacing(0);
//		table2.getColumnFormatter().setWidth(1, "200px");
//		paintKey(table2, Mod2002020Key.CN989, 0);
		paint();
	}

//	private void addGroupEntitiesDocumentColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(20);	    
//		Column<GroupEntitie, String> documentColumn = new Column<GroupEntitie, String>(input) {
//			@Override
//			public String getValue(GroupEntitie object) {
//				return object.getDocument();
//			}
//		};
//		documentColumn.setFieldUpdater(new FieldUpdater<GroupEntitie, String>() {
//		    public void update(int index, GroupEntitie ge, String value) {
//		    	groupEntitiesProvider.getList().get(index).setDocument(value);
//		    }
//		});		
//		groupEntitiesTable.addColumn(documentColumn, AON.MSG.bussinessAmount11());
//		documentColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());		
//		groupEntitiesTable.setColumnWidth(documentColumn, 150, Unit.PX);
//	}
//	
//	private void addGroupEntitiesCountryColumn() {
//		final LinkedList<String> options = new LinkedList<String>();
//		for (Country c : Country.values()) {		
//			options.add( c.getName() );
//		}
//		TabSelectionCell countryCell = new TabSelectionCell(options);
//		Column<GroupEntitie, String> countryColumn = new Column<GroupEntitie, String>(
//				countryCell) {
//			
//			@Override
//			public String getValue(GroupEntitie ge) {
//				String country = ge.getCountry();
//				String name = null; 
//				Country c = Country.safeValueOf(country);
//				name = (c==null?null:c.getName());
//				return name;
//			}
//		};
//		countryColumn.setFieldUpdater(new FieldUpdater<GroupEntitie, String>() {
//		    public void update(int index, GroupEntitie ge, String value) {
//		    	Country c = null;
//		    	if (AonStringUtils.isNotEmpty(value)) {
//		    		int idx = options.indexOf(value);
//		    		c = Country.values()[idx];		    		
//		    	}
//		    	groupEntitiesProvider.getList().get(index).setCountry(c==null?null:c.getIso2());
//		    }
//		});		
//		groupEntitiesTable.addColumn(countryColumn, AON.MSG.country());
//		groupEntitiesTable.setColumnWidth(countryColumn, 150, Unit.PX);
//		countryColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//	}
//	
//	private void addGroupEntitiesRemoveColumn() {
//		ButtonCell removeButton = new ButtonCell( new DeleteButtonSafeHtmlTemplates())  {
//			  @Override
//			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
//			    if (data != null) {
//			      sb.append(data);
//			    }
//			  }
//		};
//		Column<GroupEntitie,String> col = new Column<GroupEntitie,String>(removeButton) {
//		  public String getValue(GroupEntitie object) {
//		    return AON.MSG.deleteAction();
//		  }
//		};
//		col.setFieldUpdater(new FieldUpdater<GroupEntitie, String>() {
//			
//		    public void update(int index, GroupEntitie ge, String value) {
//		    	ConfirmDialog cd = new ConfirmDialog();
//		    	cd.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {
//					
//					@Override
//					public void onCancel() {}
//					
//					@Override
//					public void onAccept() {
//						groupEntitiesProvider.getList().remove(index);
//						groupEntitiesTable.redraw();
//					}
//				});
//		    }
//		});		
//		groupEntitiesTable.addColumn(col);
//		groupEntitiesTable.setColumnWidth(col, 20, Unit.PX);
//		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//	}
//
//	private void addEstablishmentsColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(10);
//		Column<String, String> documentColumn = new Column<String, String>(input) {
//			@Override
//			public String getValue(String object) {
//				return object;
//			}
//		};
//		documentColumn.setFieldUpdater(new FieldUpdater<String, String>() {
//		    public void update(int index, String cp, String value) {
//		    	establishmentsProvider.getList().set(index,value);
//		    }
//		});		
//		establishmentsTable.addColumn(documentColumn, AON.MSG.bussinessAmount12());
//		documentColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//		establishmentsTable.setColumnWidth(documentColumn, 100, Unit.PX);
//	}
//
//	private void addEstablishmentsRemoveColumn() {
//		ButtonCell removeButton = new ButtonCell( new DeleteButtonSafeHtmlTemplates())  {
//			  @Override
//			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
//			    if (data != null) {
//			      sb.append(data);
//			    }
//			  }
//		};
//		Column<String,String> col = new Column<String,String>(removeButton) {
//		  public String getValue(String object) {
//		    return AON.MSG.deleteAction();
//		  }
//		};
//		col.setFieldUpdater(new FieldUpdater<String, String>() {
//			
//		    public void update(int index, String ca, String value) {
//		    	ConfirmDialog cd = new ConfirmDialog();
//		    	cd.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {
//					
//					@Override
//					public void onCancel() {}
//					
//					@Override
//					public void onAccept() {
//						establishmentsProvider.getList().remove(index);
//						establishmentsTable.redraw();
//					}
//				});
//		    }
//		});		
//		establishmentsTable.addColumn(col);
//		establishmentsTable.setColumnWidth(col, 20, Unit.PX);
//		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//	}

	@Override
	protected void dump() {
		super.dump();
//		groupEntitiesProvider = callback.getMod200Object().getMod200().getGroupEntities() == null
//			?new ListDataProvider<GroupEntitie>()
//			:new ListDataProvider<GroupEntitie>(callback.getMod200Object().getMod200().getGroupEntities());
//		groupEntitiesProvider.addDataDisplay(groupEntitiesTable);
//		groupEntitiesTable.redraw();
//		
//		establishmentsProvider = callback.getMod200Object().getMod200().getEstablishments()  == null
//				?new ListDataProvider<String>()
//				:new ListDataProvider<String>(callback.getMod200Object().getMod200().getEstablishments());
//		establishmentsProvider.addDataDisplay(establishmentsTable);
//		establishmentsTable.redraw();
	}
	
	@Override
	protected void populate() {
//		callback.getMod200Object().getMod200().setGroupEntities (new LinkedList<GroupEntitie>(groupEntitiesProvider.getList()));
//		callback.getMod200Object().getMod200().setEstablishments(new LinkedList<String>(establishmentsProvider.getList()));
//		
	}
	
	@Override
	protected boolean isAvailable() {
		boolean av = super.isAvailable()
  		  && (callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0021) 
		   || callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0039))
  		  ;
		return av;
	}
	
	private void paint() {
		
		basePanel.clear();
		
		// Grupos de sociedades, art. 42 código de comercio, incluidas entidades de crédito y aseguradoras
		
		FlexTable tab1 = addTable(AON.MSG.bussinessAmount1());		
		paintKey(tab1, Mod2002020Key.CN987, 0);
		
		AonDisplayTable tab2 = new AonDisplayTable();
		tab2.setWidth("50%");
		tab2.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab2);
		
		tab2.addRow()
			.addCell( new Label(AON.MSG.bussinessAmount11()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.country()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getGroupEntities().size(); i++) {
			final int idx = i;
			
			AonDocumentTextBox document = new AonDocumentTextBox();			
			document.setValue(callback.getMod200Object().getMod200().getGroupEntities().get(idx).getDocument());
			document.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getGroupEntities().get(idx).setDocument(document.getValue());
				callback.markAsDirty();
			});
			
			CountryListBox country = new CountryListBox();
			country.setWidth("140px");
			country.setValue(Country.safeValueOf(callback.getMod200Object().getMod200().getGroupEntities().get(idx).getCountry()));
			country.addChangeHandler(new ChangeHandler() {			
				@Override
				public void onChange(ChangeEvent event) {
					callback.getMod200Object().getMod200().getGroupEntities().get(idx).setCountry(Country.safeIso2(country.getValue()));
					callback.markAsDirty();
				}
			});
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getGroupEntities().remove(idx);
				paint();
				callback.markAsDirty();
			});

			tab2.addRow()
				.addCell(document)
				.addCell(country)
				.addCell(deleteButton);
			
		}
		
		// Botón añadir 
		AonTableButton addButton2 = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
//		addButton2.addStyleName(AON.CSS.aonMarginTop());
//		addButton2.addStyleName(AON.CSS.aonMarginLeft());
		addButton2.addClickHandler(event -> {
			callback.getMod200Object().getMod200().getGroupEntities().add(new GroupEntitie());
			paint();
		});
		tab2.addRow().addCell(addButton2);
		
//		basePanel.add(addButton2);
		
		// No residentes con más de un establecimiento permanente
		
		FlexTable tab3 = addTable(AON.MSG.bussinessAmount2());
		paintKey(tab3, Mod2002020Key.CN988, 0);
		paintKey(tab3, Mod2002020Key.CNEST, 1);
		
		AonDisplayTable tab4 = new AonDisplayTable();
		tab4.setWidth("30%");
		tab4.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab4);
		
		tab4.addRow()
			.addCell( new Label(AON.MSG.bussinessAmount12()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())			
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getEstablishments().size(); i++) {
			final int idx = i;
			
			AonDocumentTextBox document = new AonDocumentTextBox();			
			document.setValue(callback.getMod200Object().getMod200().getEstablishments().get(idx));
			document.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getEstablishments().set(idx, document.getValue());
				callback.markAsDirty();
			});
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getEstablishments().remove(idx);
				paint();
				callback.markAsDirty();
			});

			tab4.addRow()
				.addCell(document)
				.addCell(deleteButton);
			
		}
		
		// Botón añadir 
		AonTableButton addButton4 = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
//		addButton4.addStyleName(AON.CSS.aonMarginTop());
//		addButton4.addStyleName(AON.CSS.aonMarginLeft());
		addButton4.addClickHandler(event -> {
			callback.getMod200Object().getMod200().getEstablishments().add(new String());
			paint();
		});
//		basePanel.add(addButton4);
		tab4.addRow().addCell(addButton4);		
		
	}

}
