package com.esferalia.aon.gwt.fiscal.client.mod200.e2016;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.common.client.widget.cell.SizableTextInputCell;
import com.esferalia.aon.gwt.common.client.widget.cell.TabCheckboxCell;
import com.esferalia.aon.gwt.common.client.widget.cell.TabSelectionCell;
import com.esferalia.aon.gwt.fiscal.client.mod200.Model200Table;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2016.Model2002016.Model200PageCallback;
import com.esferalia.aon.occam.api.model.UteParticipation;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.watson.util.AonStringUtils;
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

public class Page17 extends PageAbs {

	FlexTable table1;
	FlexTable table2;
	FlexTable table3;
	
	private ListDataProvider<UteParticipation> participationDataProvider;
	private CellTable<UteParticipation> participationTable;
	private Button newParticipation;
	
	
	public Page17( Model200PageCallback callback ) {
		super(callback);

		table1 = new FlexTable();
		table2 = new FlexTable();
		table3 = new FlexTable();
		
		ScrollPanel container = new ScrollPanel();
		container.setStyleName(AON.AON_CSS.aonScrollArea());
		FlowPanel baseContainerPanel = new FlowPanel();
		baseContainerPanel.setStyleName(AON.AON_CSS.aonFiscalContainer());
		
			FlowPanel groupPanel = new FlowPanel();
			groupPanel.setStyleName(AON.AON_CSS.aonGroup());
			
				FlowPanel groupHeaderPanel = new FlowPanel();
				groupHeaderPanel.setStyleName(AON.AON_CSS.aonGroupTitle());
				groupHeaderPanel.add (new InlineLabel(AON.MSG.ute1())); 
				groupPanel.add(groupHeaderPanel);
				
				FlowPanel groupBodyPanel = new FlowPanel();
				groupBodyPanel.setStyleName(AON.AON_CSS.aonGroupBody());
				groupBodyPanel.add(table);
				groupPanel.add(groupBodyPanel);
				
			baseContainerPanel.add(groupPanel);
			
			FlowPanel groupPanel2 = new FlowPanel();
			groupPanel2.setStyleName(AON.AON_CSS.aonGroup());
			
				FlowPanel groupHeaderPanel2 = new FlowPanel();
				groupHeaderPanel2.setStyleName(AON.AON_CSS.aonGroupTitle());
				groupHeaderPanel2.add (new InlineLabel(AON.MSG.ute2())); 
				groupPanel2.add(groupHeaderPanel2);
				
				FlowPanel groupBodyPanel2 = new FlowPanel();
				groupBodyPanel2.setStyleName(AON.AON_CSS.aonGroupBody());
				groupBodyPanel2.add(table1);
				groupBodyPanel2.add(table2);
				groupBodyPanel2.add(table3);
				groupPanel2.add(groupBodyPanel2);
				
			baseContainerPanel.add(groupPanel2);
			
			participationTable = new CellTable<UteParticipation>(25,Model200Table.TABLE_STYLE);
			participationTable.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
			participationTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
			participationTable.setEmptyTableWidget(new HTML(AON.MSG.noData()));
			participationDataProvider = new ListDataProvider<UteParticipation>();
			participationDataProvider.addDataDisplay(participationTable);
			
			addInDocumentColumn();
			addInRepresentativeColumn();
			addInDescriptionColumn();
			addInProvinceColumn();
			addInBaseColumn();
			addInPercentColumn();
			addInRemoveColumn();
			
			newParticipation = new Button();
			newParticipation.setStyleName(AON.AON_CSS.aonIconReset());
			newParticipation.addStyleName(AON.AON_CSS.aonBorderNone());
			newParticipation.addStyleName(AON.AON_CSS.aonMarginTop());
			newParticipation.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					participationDataProvider.getList().add(new UteParticipation());
					participationTable.redraw();		    		
				}
			});
			FlowPanel groupPanel3 = new FlowPanel();
			groupPanel3.setStyleName(AON.AON_CSS.aonGroup());
			
				FlowPanel groupHeaderPanel3 = new FlowPanel();
				groupHeaderPanel3.setStyleName(AON.AON_CSS.aonGroupTitle());
				groupHeaderPanel3.add (new InlineLabel(AON.MSG.ute2())); 
				groupPanel3.add(groupHeaderPanel3);
				
				FlowPanel groupBodyPanel3 = new FlowPanel();
				groupBodyPanel3.setStyleName(AON.AON_CSS.aonGroupBody());
				groupBodyPanel3.add(participationTable);
				groupBodyPanel3.add(newParticipation);
				groupPanel3.add(groupBodyPanel3);
				
			baseContainerPanel.add(groupPanel3);

		container.add(baseContainerPanel);
		initWidget(container);
	}

	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "200px");
		paintKey(table, Mod2002016Key.UT060, 0);
		
		int row = 0;
		table1.setWidth("100%");
		table1.setCellSpacing(0);
		table1.getColumnFormatter().setWidth(1, "200px");
		paintKey(table1, Mod2002016Key.UT500 , row++);
		paintKey(table1, Mod2002016Key.UT1227, row++);
		paintKey(table1, Mod2002016Key.UT1228, row++);
		paintKey(table1, Mod2002016Key.UT552 , row++);
		paintKey(table1, Mod2002016Key.UT1330, row++);
		
		table2.setWidth("100%");
		table2.setCellSpacing(0);
		table2.getColumnFormatter().setWidth(1, "200px");
		table2.getColumnFormatter().setWidth(2, "200px");
		paintDescription(table2, AON.MSG.ute31(), row, 0, false);
		paintDescription(table2, AON.MSG.deductionBaseAbbrv(), row, 1, false);
		paintDescription(table2, AON.MSG.partPercent(), row++, 2, false);
		paintKeyField(table2, Mod2002016Key.UTB01, row, 1);
		paintKeyField(table2, Mod2002016Key.UTP01, row++, 2);
		paintKeyField(table2, Mod2002016Key.UTB02, row, 1);
		paintKeyField(table2, Mod2002016Key.UTP02, row++, 2);
		paintKeyField(table2, Mod2002016Key.UTB03, row, 1);
		paintKeyField(table2, Mod2002016Key.UTP03, row++, 2);
		paintKeyField(table2, Mod2002016Key.UTB04, row, 1);
		paintKeyField(table2, Mod2002016Key.UTP04, row++, 2);
		
		table3.setWidth("100%");
		table3.setCellSpacing(0);
		table3.getColumnFormatter().setWidth(1, "200px");
		paintKey(table3, Mod2002016Key.UTC01, row++);
		paintDescription(table3, AON.MSG.ute4(), row++, 0, false);
		paintKey(table3, Mod2002016Key.UTC02, row++);
		table3.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPadding2Left());
		paintKey(table3, Mod2002016Key.UTC03, row++);
		table3.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPadding2Left());
		paintKey(table3, Mod2002016Key.UT062, row++);
		paintDescription(table3, AON.MSG.ute5(), row++, 0, false);
		paintKey(table3, Mod2002016Key.UTC04, row++);
		table3.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPadding2Left());
		paintKey(table3, Mod2002016Key.UTC05, row++);
		table3.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPadding2Left());
		
		
	}
	
	private void addInDocumentColumn() {
		SizableTextInputCell input = new SizableTextInputCell(8);
		Column<UteParticipation, String> documentColumn = new Column<UteParticipation, String>(
				input) {
			@Override
			public String getValue(UteParticipation object) {
				return object.getDocument();
			}
		};
		documentColumn.setFieldUpdater(new FieldUpdater<UteParticipation, String>() {
		    public void update(int index, UteParticipation cp, String value) {
		    	participationDataProvider.getList().get(index).setDocument(value);
		    }
		});		
		participationTable.addColumn(documentColumn, AON.MSG.document());
		documentColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
		participationTable.setColumnWidth(documentColumn, 100, Unit.PX);
	}

	private void addInDescriptionColumn() {
		SizableTextInputCell input = new SizableTextInputCell(30);
		Column<UteParticipation, String> descriptionColumn = new Column<UteParticipation, String>(
				input) {
			@Override
			public String getValue(UteParticipation ca) {
				return ca.getName();
			}
		};
		descriptionColumn.setFieldUpdater(new FieldUpdater<UteParticipation, String>() {
		    public void update(int index, UteParticipation cp, String value) {
		    	participationDataProvider.getList().get(index).setName(value);
		    }
		});		
		participationTable.addColumn(descriptionColumn, AON.MSG.companyName());
		descriptionColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
	}

	private void addInRepresentativeColumn() {
		Column<UteParticipation, Boolean> representativeColumn = new Column<UteParticipation, Boolean>(
				new TabCheckboxCell()) {
			@Override
			public Boolean getValue(UteParticipation ca) {
				return ca.isRepresentative();
			}
		};
		representativeColumn.setFieldUpdater(new FieldUpdater<UteParticipation, Boolean>() {
		    public void update(int index, UteParticipation cp, Boolean value) {
		    	participationDataProvider.getList().get(index).setRepresentative(value);
		    }
		});		
		participationTable.addColumn(representativeColumn, "Rpte.");
		participationTable.setColumnWidth(representativeColumn, 30, Unit.PX);
		representativeColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
	}

	private void addInProvinceColumn() {
		final LinkedList<String> options = new LinkedList<String>();
		for (Province prov : Province.values()) {
			options.add( prov.getName() );
		}
		for (Country c : Country.values()) {
			options.add( c.getName() );
		}
		TabSelectionCell provinceCell = new TabSelectionCell(options);
		Column<UteParticipation, String> provinceColumn = new Column<UteParticipation, String>(
				provinceCell) {
			
			@Override
			public String getValue(UteParticipation ca) {
				int idx = ca.getProvince();
				String country = ca.getCountry();
				String name = null; 
				if (idx > 0 && idx < Province.values().length) {
					name = Province.values()[idx].getName(); 
				} else {
					Country c = Country.safeValueOf(country);
					name = (c==null?null:c.getName());
				}
				return name;
			}
		};
		provinceColumn.setFieldUpdater(new FieldUpdater<UteParticipation, String>() {
		    public void update(int index, UteParticipation cp, String value) {
		    	Province p = null;
		    	Country c = null;
		    	if (AonStringUtils.isNotEmpty(value)) {
		    		int idx = options.indexOf(value);
		    		if (idx < (Province.values().length)) {
		    			p = Province.values()[idx];	
		    		} else {
		    			c = Country.values()[idx - Province.values().length];
		    		}
		    	} 
		    	participationDataProvider.getList().get(index).setProvince(p==null?0:p.ordinal());
		    	participationDataProvider.getList().get(index).setCountry(c==null?null:c.getIso2());
		    }
		});		
		participationTable.addColumn(provinceColumn, AON.MSG.province() + "/" + AON.MSG.country());
		participationTable.setColumnWidth(provinceColumn, 150, Unit.PX);
		provinceColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
	}

	private void addInPercentColumn() {
		SizableTextInputCell input = new SizableTextInputCell(5);
		Column<UteParticipation, String> percentColumn = new Column<UteParticipation, String>(
				input) {
			@Override
			public String getValue(UteParticipation ca) {
				return Double.toString( ca.getPercent() );
			}
		};
		percentColumn.setFieldUpdater(new FieldUpdater<UteParticipation, String>() {
		    public void update(int index, UteParticipation cp, String value) {
		    	try {
		    		double p = Double.parseDouble(value);
		    		participationDataProvider.getList().get(index).setPercent(p);
		    	} catch (NumberFormatException e) {
		    		MessageDialog.error("Porcentaje no v\u00E1lido.");
		    	}
		    }
		});		
		participationTable.addColumn(percentColumn, "%");
		participationTable.setColumnWidth(percentColumn, 50, Unit.PX);
		percentColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
	}

	private void addInBaseColumn() {
		SizableTextInputCell input = new SizableTextInputCell(8);
		Column<UteParticipation, String> nominalValueColumn = new Column<UteParticipation, String>(
				input) {
			@Override
			public String getValue(UteParticipation ca) {
				return Double.toString( ca.getBase() );
			}
		};
		nominalValueColumn.setFieldUpdater(new FieldUpdater<UteParticipation, String>() {
		    public void update(int index, UteParticipation cp, String value) {
		    	try {
		    		double p = Double.parseDouble(value);
		    		participationDataProvider.getList().get(index).setBase(p);
		    	} catch (NumberFormatException e) {
		    		MessageDialog.error("N\u00FAmero no v\u00E1lido.");
		    	}
		    }
		});		
		participationTable.addColumn(nominalValueColumn, AON.MSG.nominalValue());
		participationTable.setColumnWidth(nominalValueColumn, 100, Unit.PX);
		nominalValueColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
	}

	private void addInRemoveColumn() {
		ButtonCell removeButton = new ButtonCell( new DeleteButtonSafeHtmlTemplates())  {
			  @Override
			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
			    if (data != null) {
			      sb.append(data);
			    }
			  }
		};
		Column<UteParticipation,String> col = new Column<UteParticipation,String>(removeButton) {
		  public String getValue(UteParticipation object) {
		    return AON.MSG.deleteAction();
		  }
		};
		col.setFieldUpdater(new FieldUpdater<UteParticipation, String>() {
			
		    public void update(int index, UteParticipation ca, String value) {
		    	ConfirmDialog cd = new ConfirmDialog();
		    	cd.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {
					
					@Override
					public void onCancel() {}
					
					@Override
					public void onAccept() {
						callback.getMod200Object().getMod200().getParticipationsIn().remove(index);
						participationDataProvider = new ListDataProvider<UteParticipation>(
								callback.getMod200Object().getMod200().getUteParticipations());
						participationDataProvider.addDataDisplay(participationTable);
						participationTable.redraw();
					}
				});
		    }
		});		
		participationTable.addColumn(col);
		participationTable.setColumnWidth(col, 20, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
	}
	
	@Override
	protected void dump() {
		super.dump();
		participationDataProvider = callback.getMod200Object().getMod200().getParticipationsIn() == null
			?new ListDataProvider<UteParticipation>()
			:new ListDataProvider<UteParticipation>(callback.getMod200Object().getMod200().getUteParticipations());

		participationDataProvider.addDataDisplay(participationTable);
		participationTable.redraw();
	}
	
	@Override
	protected void populate() {
		LinkedList<UteParticipation> listIn = new LinkedList<UteParticipation>();
		for (UteParticipation cp : participationDataProvider.getList()) {
			listIn.add(cp);
		}
		callback.getMod200Object().getMod200().setUteParticipations(listIn);
		
	}
}
