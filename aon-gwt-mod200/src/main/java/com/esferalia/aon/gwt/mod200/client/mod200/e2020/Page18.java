// UTES
package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceCountryListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Model2002020.Model200PageCallback;
import com.esferalia.aon.occam.api.model.UteBase;
import com.esferalia.aon.occam.api.model.UteForeign;
import com.esferalia.aon.occam.api.model.UteParticipation;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Key;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Province;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class Page18 extends PageAbs {

//	private FlexTable table;
//	private FlexTable table1;
//	private FlexTable table3;
//	
//	private ListDataProvider<UteParticipation> participationDataProvider;
//	private CellTable<UteParticipation> participationTable;
//	private Button newParticipation;
//
//	private ListDataProvider<UteForeign> foreignProvider;
//	private CellTable<UteForeign> foreignTable;
//	private Button newForeign;
//	
//	private ListDataProvider<UteBase> baseProvider;
//	private CellTable<UteBase> baseTable;
//	private Button newBase;
//	private FlowPanel groupPanelA;
	
	public Page18( Model200PageCallback callback ) {
		super(callback);
//		table  = new FlexTable();
//		table1 = new FlexTable();
//		table3 = new FlexTable();
//		
//		ScrollPanel container = new ScrollPanel();
//		container.setStyleName(AON.AON_CSS.aonScrollArea());
//		FlowPanel baseContainerPanel = new FlowPanel();
//		baseContainerPanel.setStyleName(AON.AON_CSS.aonFiscalContainer());
//		
//			groupPanelA = new FlowPanel();
//			groupPanelA.setStyleName(AON.AON_CSS.aonGroup());
//			
//				FlowPanel groupHeaderPanel = new FlowPanel();
//				groupHeaderPanel.setStyleName(AON.AON_CSS.aonGroupTitle());
//				groupHeaderPanel.add (new InlineLabel(AON.MSG.ute1())); 
//				groupPanelA.add(groupHeaderPanel);
//				
//				FlowPanel groupBodyPanel = new FlowPanel();
//				groupBodyPanel.setStyleName(AON.AON_CSS.aonGroupBody());
//				groupBodyPanel.add(table);
//				groupPanelA.add(groupBodyPanel);
//				
//			baseContainerPanel.add(groupPanelA);
//			
//			FlowPanel groupPanelB = new FlowPanel();
//			groupPanelB.setStyleName(AON.AON_CSS.aonGroup());
//			
//				FlowPanel groupHeaderPanel2 = new FlowPanel();
//				groupHeaderPanel2.setStyleName(AON.AON_CSS.aonGroupTitle());
//				groupHeaderPanel2.add (new InlineLabel(AON.MSG.ute2())); 
//				groupPanelB.add(groupHeaderPanel2);
//				
//				FlowPanel groupBodyPanel2 = new FlowPanel();
//				groupBodyPanel2.setStyleName(AON.AON_CSS.aonGroupBody());
//				groupBodyPanel2.add(table1);
//
//				FlowPanel groupPanelB6 = new FlowPanel();
//				groupPanelB6.setStyleName(AON.AON_CSS.aonGroup());
//					FlowPanel groupHeaderBase = new FlowPanel();
//					groupHeaderBase.setStyleName(AON.AON_CSS.aonGroupTitle());
//					groupHeaderBase.add (new InlineLabel(AON.MSG.ute31())); 
//					groupPanelB6.add(groupHeaderBase);
//
//					FlowPanel groupBodyPanelBase = new FlowPanel();
//					groupBodyPanelBase.setStyleName(AON.AON_CSS.aonGroupBody());
//					groupBodyPanelBase.addStyleName(AON.AON_CSS.aonWidth300());
//					groupBodyPanelBase.addStyleName(AON.AON_CSS.aonBlockCenter());
//					
//					baseTable = new CellTable<UteBase>(25,Model200Table.TABLE_STYLE);
//					baseTable.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
//					baseTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
//					baseTable.setEmptyTableWidget(new HTML(AON.MSG.noData()));
//					baseProvider = new ListDataProvider<UteBase>();
//					baseProvider.addDataDisplay(baseTable);
//					addBaseBaseColumn();
//					addPercentBaseColumn();
//					addRemoveBaseColumn();
//
//					newBase = new Button();
//					newBase.setStyleName(AON.AON_CSS.aonIconReset());
//					newBase.addStyleName(AON.AON_CSS.aonBorderNone());
//					newBase.addStyleName(AON.AON_CSS.aonMarginTop5());
//					newBase.addClickHandler(new ClickHandler() {
//						
//						@Override
//						public void onClick(ClickEvent event) {
//							baseProvider.getList().add(new UteBase());
//							baseTable.redraw();		    		
//						}
//					});
//					groupBodyPanelBase.add(baseTable);
//					groupBodyPanelBase.add(newBase);
//					groupPanelB6.add(groupBodyPanelBase);
//					
//					
//					groupBodyPanel2.add(groupPanelB6);
//					
//				groupBodyPanel2.add(table3);
//				groupPanelB.add(groupBodyPanel2);
//				
//			
//				participationTable = new CellTable<UteParticipation>(25,Model200Table.TABLE_STYLE);
//				participationTable.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
//				participationTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
//				participationTable.setEmptyTableWidget(new HTML(AON.MSG.noData()));
//				participationDataProvider = new ListDataProvider<UteParticipation>();
//				participationDataProvider.addDataDisplay(participationTable);
//				
//				addDocumentColumn();
//				addRepresentativeColumn();
//				addDescriptionColumn();
//				addProvinceColumn();
//				addBaseColumn();
//				addPercentColumn();
//				addRemoveColumn();
//				
//				newParticipation = new Button();
//				newParticipation.setStyleName(AON.AON_CSS.aonIconReset());
//				newParticipation.addStyleName(AON.AON_CSS.aonBorderNone());
//				newParticipation.addStyleName(AON.AON_CSS.aonMarginTop5());
//				newParticipation.addClickHandler(new ClickHandler() {
//					
//					@Override
//					public void onClick(ClickEvent event) {
//						participationDataProvider.getList().add(new UteParticipation());
//						participationTable.redraw();		    		
//					}
//				});
//				FlowPanel groupPanelB11 = new FlowPanel();
//				groupPanelB11.setStyleName(AON.AON_CSS.aonGroup());
//				
//					FlowPanel groupHeaderPanel3 = new FlowPanel();
//					groupHeaderPanel3.setStyleName(AON.AON_CSS.aonGroupTitle());
//					InlineLabel il = new InlineLabel(AonStringUtils.abbreviate(AON.MSG.ute6(),100));
//					il.setTitle(AON.MSG.ute6());
//					groupHeaderPanel3.add (il); 
//					groupPanelB11.add(groupHeaderPanel3);
//					
//					FlowPanel groupBodyPanel3 = new FlowPanel();
//					groupBodyPanel3.setStyleName(AON.AON_CSS.aonGroupBody());
//					groupBodyPanel3.add(participationTable);
//					groupBodyPanel3.add(newParticipation);
//					groupPanelB11.add(groupBodyPanel3);
//				
//					groupPanelB.add(groupPanelB11);
//			baseContainerPanel.add(groupPanelB);
//
//
//			foreignTable = new CellTable<UteForeign>(25,Model200Table.TABLE_STYLE);
//		    foreignTable = new CellTable<UteForeign>(25);
//			foreignTable.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
//			foreignTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
//			foreignTable.setEmptyTableWidget(new HTML(AON.MSG.noData()));
//			foreignProvider = new ListDataProvider<UteForeign>();
//			foreignProvider.addDataDisplay(foreignTable);
//			
//			addIdentificationColumn();
//			addCountryColumn();
//			addVolumeColumn();
//			addPygColumn();
//			addAdjustColumn();
//			addDeductionColumn();
//			addForeignRemoveColumn();
//			
//			newForeign = new Button();
//			newForeign.setStyleName(AON.AON_CSS.aonIconReset());
//			newForeign.addStyleName(AON.AON_CSS.aonBorderNone());
//			newForeign.addStyleName(AON.AON_CSS.aonMarginTop5());
//			newForeign.addClickHandler(new ClickHandler() {
//				
//				@Override
//				public void onClick(ClickEvent event) {
//					foreignProvider.getList().add(new UteForeign());
//					foreignTable.redraw();		    		
//				}
//			});
//			FlowPanel groupPanel4 = new FlowPanel();
//			groupPanel4.setStyleName(AON.AON_CSS.aonGroup());
//			 
//				FlowPanel groupHeaderPanel4 = new FlowPanel();
//				groupHeaderPanel4.setStyleName(AON.AON_CSS.aonGroupTitle());
//				groupHeaderPanel4.add (new InlineLabel(AON.MSG.utefor())); 
//				groupPanel4.add(groupHeaderPanel4);
//				
//				FlowPanel groupBodyPanel4 = new FlowPanel();
//				groupBodyPanel4.setStyleName(AON.AON_CSS.aonGroupBody());
//				groupBodyPanel4.add(foreignTable);
//				groupBodyPanel4.add(newForeign);
//				groupPanel4.add(groupBodyPanel4);
//				
//			baseContainerPanel.add(groupPanel4);
//
//			
//		container.add(baseContainerPanel);
//		initWidget(container);
		addBasePanel();
		initializeTable();
		
//		callback.getMod200Object().register( new IMod200ChangeListener() {
//			
//			@Override
//			public void mod200Changed(Mod2002020 mod200) {
//				participationDataProvider = new ListDataProvider<UteParticipation>(
//						callback.getMod200Object().getMod200().getUteParticipations());
//				participationDataProvider.addDataDisplay(participationTable);
//				participationTable.redraw();
//			}
//		});
		
	}

	@Override
	protected void initializeTable() {
//		boolean c0013 = callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0013);
//		groupPanelA.setVisible(c0013);
//		if (c0013) {
//			table.setWidth("100%");
//			table.setCellSpacing(0);
//			table.getColumnFormatter().setWidth(1, "200px");
//			paintKey(table, Mod2002020Key.UT060, 0);
//		} 
//		
//		int row = 0;
//		table1.setWidth("100%");
//		table1.setCellSpacing(0);
//		table1.getColumnFormatter().setWidth(1, "200px");
//		paintKey(table1, Mod2002020Key.UT500 , row++);
//		paintKey(table1, Mod2002020Key.UT1227, row++);
//		paintKey(table1, Mod2002020Key.UT1228, row++);
//		paintKey(table1, Mod2002020Key.UT552 , row++);
//		paintKey(table1, Mod2002020Key.UT1330, row++);
//
//		table3.setWidth("100%");
//		table3.setCellSpacing(0);
//		table3.getColumnFormatter().setWidth(1, "200px");
//		paintKey(table3, Mod2002020Key.UTC01, row++);
//		paintDescription(table3, AON.MSG.ute4(), row++, 0, false);
//		paintKey(table3, Mod2002020Key.UTC02, row++);
//		table3.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPadding2Left());
//		paintKey(table3, Mod2002020Key.UTC03, row++);
//		table3.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPadding2Left());
//		paintKey(table3, Mod2002020Key.UT062, row++);
//		paintDescription(table3, AON.MSG.ute5(), row++, 0, false);
//		paintKey(table3, Mod2002020Key.UTC04, row++);
//		table3.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPadding2Left());
//		paintKey(table3, Mod2002020Key.UTC05, row++);
//		table3.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPadding2Left());
		paint();		
		
	}
	
//	private void addDocumentColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(8);
//		Column<UteParticipation, String> documentColumn = new Column<UteParticipation, String>(
//				input) {
//			@Override
//			public String getValue(UteParticipation object) {
//				return object.getDocument();
//			}
//		};
//		documentColumn.setFieldUpdater(new FieldUpdater<UteParticipation, String>() {
//		    public void update(int index, UteParticipation cp, String value) {
//		    	participationDataProvider.getList().get(index).setDocument(value);
//		    }
//		});		
//		participationTable.addColumn(documentColumn, AON.MSG.document());
//		documentColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//		participationTable.setColumnWidth(documentColumn, 100, Unit.PX);
//	}
//
//	private void addDescriptionColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(30);
//		Column<UteParticipation, String> descriptionColumn = new Column<UteParticipation, String>(
//				input) {
//			@Override
//			public String getValue(UteParticipation ca) {
//				return ca.getName();
//			}
//		};
//		descriptionColumn.setFieldUpdater(new FieldUpdater<UteParticipation, String>() {
//		    public void update(int index, UteParticipation cp, String value) {
//		    	participationDataProvider.getList().get(index).setName(value);
//		    }
//		});		
//		participationTable.addColumn(descriptionColumn, AON.MSG.fullName());
//		descriptionColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//		participationTable.setColumnWidth(descriptionColumn, 250, Unit.PX);
//	}
//
//	private void addRepresentativeColumn() {
//		Column<UteParticipation, Boolean> representativeColumn = new Column<UteParticipation, Boolean>(
//				new TabCheckboxCell()) {
//			@Override
//			public Boolean getValue(UteParticipation ca) {
//				return ca.isRepresentative();
//			}
//		};
//		representativeColumn.setFieldUpdater(new FieldUpdater<UteParticipation, Boolean>() {
//		    public void update(int index, UteParticipation cp, Boolean value) {
//		    	participationDataProvider.getList().get(index).setRepresentative(value);
//		    }
//		});		
//		participationTable.addColumn(representativeColumn, "Rpte.");
//		participationTable.setColumnWidth(representativeColumn, 30, Unit.PX);
//		representativeColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//	}
//
//	private void addProvinceColumn() {
//		final LinkedList<String> options = new LinkedList<String>();
//		for (Province prov : Province.values()) {
//			options.add( prov.getName() );
//		}
//		for (Country c : Country.values()) {
//			options.add( c.getName() );
//		}
//		TabSelectionCell provinceCell = new TabSelectionCell(options);
//		Column<UteParticipation, String> provinceColumn = new Column<UteParticipation, String>(
//				provinceCell) {
//			
//			@Override
//			public String getValue(UteParticipation ca) {
//				int idx = ca.getProvince();
//				String country = ca.getCountry();
//				String name = null; 
//				if (idx > 0 && idx < Province.values().length) {
//					name = Province.values()[idx].getName(); 
//				} else {
//					Country c = Country.safeValueOf(country);
//					name = (c==null?null:c.getName());
//				}
//				return name;
//			}
//		};
//		provinceColumn.setFieldUpdater(new FieldUpdater<UteParticipation, String>() {
//		    public void update(int index, UteParticipation cp, String value) {
//		    	Province p = null;
//		    	Country c = null;
//		    	if (AonStringUtils.isNotEmpty(value)) {
//		    		int idx = options.indexOf(value);
//		    		if (idx < (Province.values().length)) {
//		    			p = Province.values()[idx];	
//		    		} else {
//		    			c = Country.values()[idx - Province.values().length];
//		    		}
//		    	} 
//		    	participationDataProvider.getList().get(index).setProvince(p==null?0:p.ordinal());
//		    	participationDataProvider.getList().get(index).setCountry(c==null?null:c.getIso2());
//		    }
//		});		
//		participationTable.addColumn(provinceColumn, AON.MSG.province() + "/" + AON.MSG.country());
//		participationTable.setColumnWidth(provinceColumn, 150, Unit.PX);
//		provinceColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//	}
//
//	private void addPercentColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(5);
//		Column<UteParticipation, String> percentColumn = new Column<UteParticipation, String>(
//				input) {
//			@Override
//			public String getValue(UteParticipation ca) {
//				return Double.toString( ca.getPercent() );
//			}
//		};
//		percentColumn.setFieldUpdater(new FieldUpdater<UteParticipation, String>() {
//		    public void update(int index, UteParticipation cp, String value) {
//		    	try {
//		    		double p = Double.parseDouble(value);
//		    		participationDataProvider.getList().get(index).setPercent(p);
//		    		// Recalcular Base según el porcentaje indicado
//		    		double c1330 = callback.getMod200Object().getMod200().getVariable(Mod2002020Key.UT1330).getValue();
//		    		double base = AonMathUtils.round(c1330 * p / 100); 
//		    		participationDataProvider.getList().get(index).setBase(base);
//		    		participationTable.redraw();
//		    		
//		    	} catch (NumberFormatException e) {
//		    		MessageDialog.error("Porcentaje no v\u00E1lido.");
//		    	}
//		    }
//		});		
//		participationTable.addColumn(percentColumn, "%");
//		participationTable.setColumnWidth(percentColumn, 50, Unit.PX);
//		percentColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//	}
//
//	private void addBaseColumn() {
//		TextCell input = new TextCell(); // Solo mostrar datos, no permite modificar		
//		Column<UteParticipation, String> nominalValueColumn = new Column<UteParticipation, String>(input) {
//			@Override
//			public String getValue(UteParticipation ca) {
//				return Double.toString( ca.getBase() );
//			}
//		};
//		participationTable.addColumn(nominalValueColumn, AON.MSG.nominalValue());
//		participationTable.setColumnWidth(nominalValueColumn, 100, Unit.PX);
//		nominalValueColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());		
//	}
//
//	private void addRemoveColumn() {
//		ButtonCell removeButton = new ButtonCell( new DeleteButtonSafeHtmlTemplates())  {
//			  @Override
//			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
//			    if (data != null) {
//			      sb.append(data);
//			    }
//			  }
//		};
//		Column<UteParticipation,String> col = new Column<UteParticipation,String>(removeButton) {
//		  public String getValue(UteParticipation object) {
//		    return AON.MSG.deleteAction();
//		  }
//		};
//		col.setFieldUpdater(new FieldUpdater<UteParticipation, String>() {
//			
//		    public void update(int index, UteParticipation ca, String value) {
//		    	ConfirmDialog cd = new ConfirmDialog();
//		    	cd.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {
//					
//					@Override
//					public void onCancel() {}
//					
//					@Override
//					public void onAccept() {
//						callback.getMod200Object().getMod200().getUteParticipations().remove(index);
//						participationDataProvider = new ListDataProvider<UteParticipation>(
//								callback.getMod200Object().getMod200().getUteParticipations());
//						participationDataProvider.addDataDisplay(participationTable);
//						participationTable.redraw();
//					}
//				});
//		    }
//		});		
//		participationTable.addColumn(col);
//		participationTable.setColumnWidth(col, 20, Unit.PX);
//		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//	}
//	
//	private void addIdentificationColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(30);
//		Column<UteForeign, String> identificationColumn = new Column<UteForeign, String>(
//				input) {
//			@Override
//			public String getValue(UteForeign ca) {
//				return ca.getIdentification();
//			}
//		};
//		identificationColumn.setFieldUpdater(new FieldUpdater<UteForeign, String>() {
//		    public void update(int index, UteForeign cp, String value) {
//		    	foreignProvider.getList().get(index).setIdentification(value);
//		    }
//		});		
//		foreignTable.addColumn(identificationColumn, AON.MSG.identification());
//		identificationColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//	}
//	
//	private void addCountryColumn() {
//		final LinkedList<String> options = new LinkedList<String>();
//		for (Country c : Country.values()) {
//			options.add( c.getName() );
//		}
//		TabSelectionCell provinceCell = new TabSelectionCell(options);
//		Column<UteForeign, String> countryColumn = new Column<UteForeign, String>(
//				provinceCell) {
//			
//			@Override
//			public String getValue(UteForeign ca) {
//				Country c = Country.safeValueOf(ca.getCountry());
//				return (c==null?null:c.getName());
//			}
//		};
//		countryColumn.setFieldUpdater(new FieldUpdater<UteForeign, String>() {
//		    public void update(int index, UteForeign cp, String value) {
//		    	Country c = null;
//		    	if (AonStringUtils.isNotEmpty(value)) {
//		    		int idx = options.indexOf(value);
//		    		c = Country.values()[idx];
//		    	} 
//		    	foreignProvider.getList().get(index).setCountry(c==null?null:c.getIso2());
//		    }
//		});		
//		foreignTable.addColumn(countryColumn, AON.MSG.utefor1());
//		foreignTable.setColumnWidth(countryColumn, 150, Unit.PX);
//		countryColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//	}
//	
//	private void addVolumeColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(8);
//		Column<UteForeign, String> volumeColumn = new Column<UteForeign, String>(
//				input) {
//			@Override
//			public String getValue(UteForeign ca) {
//				return Double.toString( ca.getVolume() );
//			}
//		};
//		volumeColumn.setFieldUpdater(new FieldUpdater<UteForeign, String>() {
//		    public void update(int index, UteForeign cp, String value) {
//		    	try {
//		    		double p = Double.parseDouble(value);
//		    		foreignProvider.getList().get(index).setVolume(p);
//		    	} catch (NumberFormatException e) {
//		    		MessageDialog.error("Valor no v\u00E1lido.");
//		    	}
//		    }
//		});		
//		foreignTable.addColumn(volumeColumn, AON.MSG.utefor2() );
//		foreignTable.setColumnWidth(volumeColumn, 150, Unit.PX);
//		volumeColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//	}
//	private void addPygColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(8);
//		Column<UteForeign, String> pygColumn = new Column<UteForeign, String>(
//				input) {
//			@Override
//			public String getValue(UteForeign ca) {
//				return Double.toString( ca.getPyg() );
//			}
//		};
//		pygColumn.setFieldUpdater(new FieldUpdater<UteForeign, String>() {
//		    public void update(int index, UteForeign cp, String value) {
//		    	try {
//		    		double p = Double.parseDouble(value);
//		    		foreignProvider.getList().get(index).setPyg(p);
//		    	} catch (NumberFormatException e) {
//		    		MessageDialog.error("Valor no v\u00E1lido.");
//		    	}
//		    }
//		});		
//		foreignTable.addColumn(pygColumn, AON.MSG.utefor3());
//		foreignTable.setColumnWidth(pygColumn, 150, Unit.PX);
//		pygColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//	}
//	private void addAdjustColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(8);
//		Column<UteForeign, String> adjustColumn = new Column<UteForeign, String>(
//				input) {
//			@Override
//			public String getValue(UteForeign ca) {
//				return Double.toString( ca.getAdjust() );
//			}
//		};
//		adjustColumn.setFieldUpdater(new FieldUpdater<UteForeign, String>() {
//		    public void update(int index, UteForeign cp, String value) {
//		    	try {
//		    		double p = Double.parseDouble(value);
//		    		foreignProvider.getList().get(index).setAdjust(p);
//		    	} catch (NumberFormatException e) {
//		    		MessageDialog.error("Valor no v\u00E1lido.");
//		    	}
//		    }
//		});		
//		foreignTable.addColumn(adjustColumn, AON.MSG.utefor4());
//		foreignTable.setColumnWidth(adjustColumn, 150, Unit.PX);
//		adjustColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//	}
//	private void addDeductionColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(8);
//		Column<UteForeign, String> deductionColumn = new Column<UteForeign, String>(
//				input) {
//			@Override
//			public String getValue(UteForeign ca) {
//				return Double.toString( ca.getDeduction() );
//			}
//		};
//		deductionColumn.setFieldUpdater(new FieldUpdater<UteForeign, String>() {
//		    public void update(int index, UteForeign cp, String value) {
//		    	try {
//		    		double p = Double.parseDouble(value);
//		    		foreignProvider.getList().get(index).setDeduction(p);
//		    	} catch (NumberFormatException e) {
//		    		MessageDialog.error("Valor no v\u00E1lido.");
//		    	}
//		    }
//		});		
//		foreignTable.addColumn(deductionColumn, AON.MSG.utefor5());
//		foreignTable.setColumnWidth(deductionColumn, 150, Unit.PX);
//		deductionColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//	}
//
//	private void addForeignRemoveColumn() {
//		ButtonCell removeButton = new ButtonCell( new DeleteButtonSafeHtmlTemplates())  {
//			  @Override
//			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
//			    if (data != null) {
//			      sb.append(data);
//			    }
//			  }
//		};
//		Column<UteForeign,String> col = new Column<UteForeign,String>(removeButton) {
//		  public String getValue(UteForeign object) {
//		    return AON.MSG.deleteAction();
//		  }
//		};
//		col.setFieldUpdater(new FieldUpdater<UteForeign, String>() {
//			
//		    public void update(int index, UteForeign ca, String value) {
//		    	ConfirmDialog cd = new ConfirmDialog();
//		    	cd.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {
//					
//					@Override
//					public void onCancel() {}
//					
//					@Override
//					public void onAccept() {
//						callback.getMod200Object().getMod200().getUteForeign().remove(index);
//						foreignProvider = new ListDataProvider<UteForeign>(
//								callback.getMod200Object().getMod200().getUteForeign());
//						foreignProvider.addDataDisplay(foreignTable);
//						foreignTable.redraw();
//					}
//				});
//		    }
//		});		
//		foreignTable.addColumn(col);
//		foreignTable.setColumnWidth(col, 20, Unit.PX);
//		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//	}

	@Override
	protected void dump() {
		super.dump();
//		participationDataProvider = callback.getMod200Object().getMod200().getUteParticipations() == null
//			?new ListDataProvider<UteParticipation>()
//			:new ListDataProvider<UteParticipation>(callback.getMod200Object().getMod200().getUteParticipations());
//
//		participationDataProvider.addDataDisplay(participationTable);
//		participationTable.redraw();
//		
//		foreignProvider = callback.getMod200Object().getMod200().getUteForeign()  == null
//				?new ListDataProvider<UteForeign>()
//				:new ListDataProvider<UteForeign>(callback.getMod200Object().getMod200().getUteForeign());
//
//		foreignProvider.addDataDisplay(foreignTable);
//		foreignTable.redraw();
//		
//		baseProvider = callback.getMod200Object().getMod200().getUteBases()  == null
//				?new ListDataProvider<UteBase>()
//				:new ListDataProvider<UteBase>(callback.getMod200Object().getMod200().getUteBases());
//
//		baseProvider.addDataDisplay(baseTable);
//		baseTable.redraw();
	}
	
	@Override
	protected void populate() {
//		LinkedList<UteParticipation> listIn = new LinkedList<UteParticipation>();
//		for (UteParticipation cp : participationDataProvider.getList()) {
//			listIn.add(cp);
//		}
//		callback.getMod200Object().getMod200().setUteParticipations(listIn);
//		
//		LinkedList<UteForeign> list2 = new LinkedList<UteForeign>();
//		for (UteForeign cp : foreignProvider.getList()) {
//			list2.add(cp);
//		}
//		callback.getMod200Object().getMod200().setUteForeign(list2);
//		
//		LinkedList<UteBase> list3 = new LinkedList<UteBase>();
//		for (UteBase cp : baseProvider.getList()) {
//			list3.add(cp);
//		}
//		callback.getMod200Object().getMod200().setUteBases(list3);
	}
	
//	private void addBaseBaseColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(8);
//		Column<UteBase, String> baseColumn = new Column<UteBase, String>(input) {
//			@Override
//			public String getValue(UteBase ca) {
//				return Double.toString( ca.getBase() );
//			}
//		};
//		baseColumn.setFieldUpdater(new FieldUpdater<UteBase, String>() {
//		    public void update(int index, UteBase cp, String value) {
//		    	try {
//		    		double p = Double.parseDouble(value);
//		    		baseProvider.getList().get(index).setBase(p);
//		    	} catch (NumberFormatException e) {
//		    		MessageDialog.error("N\u00FAmero no v\u00E1lido.");
//		    	}
//		    }
//		});		
//		baseTable.addColumn(baseColumn, AON.MSG.deductionBase());
//		baseTable.setColumnWidth(baseColumn, 200, Unit.PX);
//		baseColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());		
//	}
//	
//	private void addPercentBaseColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(5);
//		Column<UteBase, String> percentColumn = new Column<UteBase, String>(input) {
//			@Override
//			public String getValue(UteBase ca) {
//				return Double.toString( ca.getPercent() );
//			}
//		};
//		percentColumn.setFieldUpdater(new FieldUpdater<UteBase, String>() {
//		    public void update(int index, UteBase cp, String value) {
//		    	try {
//		    		double p = Double.parseDouble(value);
//		    		baseProvider.getList().get(index).setPercent(p);
//		    	} catch (NumberFormatException e) {
//		    		MessageDialog.error("Porcentaje no v\u00E1lido.");
//		    	}
//		    }
//		});		
//		baseTable.addColumn(percentColumn, "%");
//		baseTable.setColumnWidth(percentColumn, 200, Unit.PX);
//		percentColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());		
//	}
//	
//	private void addRemoveBaseColumn() {
//		ButtonCell removeButton = new ButtonCell( new DeleteButtonSafeHtmlTemplates())  {
//			  @Override
//			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
//			    if (data != null) {
//			      sb.append(data);
//			    }
//			  }
//		};
//		Column<UteBase,String> col = new Column<UteBase,String>(removeButton) {
//		  public String getValue(UteBase object) {
//		    return AON.MSG.deleteAction();
//		  }
//		};
//		col.setFieldUpdater(new FieldUpdater<UteBase, String>() {
//			
//		    public void update(int index, UteBase ca, String value) {
//		    	ConfirmDialog cd = new ConfirmDialog();
//		    	cd.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {
//					
//					@Override
//					public void onCancel() {}
//					
//					@Override
//					public void onAccept() {
//						callback.getMod200Object().getMod200().getUteBases().remove(index);
//						baseProvider = new ListDataProvider<UteBase>(callback.getMod200Object().getMod200().getUteBases());
//						baseProvider.addDataDisplay(baseTable);
//						baseTable.redraw();
//					}
//				});
//		    }
//		});		
//		baseTable.addColumn(col);
//		baseTable.setColumnWidth(col, 20, Unit.PX);
//		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//	}
	
	@Override
	protected boolean isAvailable() {
		boolean av = super.isAvailable()
  		  && (callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0013) 
		   || callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0014));
		return av;
	}
	
	private void paint() {
		
		basePanel.clear();
		
		// A) Porcentaje de imputación de bases imponibles y demás conceptos liquidatorios
		if (callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0013)) {
			paintKey(addTable(AON.MSG.ute1()), Mod2002020Key.UT060, 0);
		}

		// B) Modelo de información (artículo 46 RIS)		
		
		FlexTable table1 = addTable(AON.MSG.ute2());
		int row = 0;
		paintKey(table1, Mod2002020Key.UT500 , row++);
		paintKey(table1, Mod2002020Key.UT1227, row++);
		paintKey(table1, Mod2002020Key.UT1228, row++);
		paintKey(table1, Mod2002020Key.UT552 , row++);
		paintKey(table1, Mod2002020Key.UT1330, row++);
		
		paintDescription(table1, AON.MSG.ute31(), row++, 0, true);
		
		AonDisplayTable tabB6 = new AonDisplayTable();
		tabB6.setWidth("60%");
		tabB6.addStyleName(AON.CSS.aonBlockCenter());
		
		tabB6.addRow()
			.addCell( new Label(AON.MSG.deductionBase()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth100())
			.addCell( new Label("%"),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth40())
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getUteBases().size(); i++) {
			final int idx = i;
			
			AonDoubleBox base = new AonDoubleBox();
			base.setValue(callback.getMod200Object().getMod200().getUteBases().get(idx).getBase());
			base.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteBases().get(idx).setBase(base.getValue());
				callback.markAsDirty();
			});
			
			AonDoubleBox percent = new AonDoubleBox();
			percent.setMaxLength(6);
			percent.setVisibleLength(6);
			percent.setValue(callback.getMod200Object().getMod200().getUteBases().get(idx).getPercent());
			percent.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteBases().get(idx).setPercent(percent.getValue());
				callback.markAsDirty();
			});
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getUteBases().remove(idx);
				paint();
				callback.markAsDirty();
			});

			tabB6.addRow()
				.addCell(base)
				.addCell(percent)
				.addCell(deleteButton);
			
		}
		
		// Botón añadir 
		AonTableButton addButtonB6 = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
//		addButtonB6.addStyleName(AON.CSS.aonMarginTop());
//		addButtonB6.addStyleName(AON.CSS.aonMarginLeft());
		addButtonB6.addClickHandler(event -> {
			callback.getMod200Object().getMod200().getUteBases().add(new UteBase());
			paint();
		});
		tabB6.addRow().addCell(addButtonB6);
		
		FlowPanel panelB6 = new FlowPanel();
		panelB6.add(tabB6);
//		panelB6.add(addButtonB6);
		
		table1.setWidget(row++, 0, panelB6);

		paintKey(table1, Mod2002020Key.UTC01, row++);
		paintDescription(table1, AON.MSG.ute4(), row++, 0, false);
		paintKey(table1, Mod2002020Key.UTC02, row++);
		table1.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPadding2Left());
		paintKey(table1, Mod2002020Key.UTC03, row++);
		table1.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPadding2Left());
		paintKey(table1, Mod2002020Key.UT062, row++);
		paintDescription(table1, AON.MSG.ute5(), row++, 0, false);
		paintKey(table1, Mod2002020Key.UTC04, row++);
		table1.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPadding2Left());
		paintKey(table1, Mod2002020Key.UTC05, row++);
		table1.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPadding2Left());
		
		paintDescription(table1, AON.MSG.ute6(), row++, 0, true);
		
		AonDisplayTable tabB11 = new AonDisplayTable();
		tabB11.addStyleName(AON.CSS.aonWidthAlmostAll());
		tabB11.addStyleName(AON.CSS.aonBlockCenter());
		
		tabB11.addRow()
			.addCell( new Label(AON.MSG.document()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth100())
			.addCell( new Label("Rpte."),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20())
			.addCell( new Label(AON.MSG.companyName()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth300())
			.addCell( new Label(AON.MSG.province() + "/" + AON.MSG.country()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.nominalValue()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth100())
			.addCell( new Label("%"),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth40())
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());
	
		for (int i = 0; i < callback.getMod200Object().getMod200().getUteParticipations().size(); i++) {
			final int idx = i;
			
			AonDocumentTextBox document = new AonDocumentTextBox();			
			document.setValue(callback.getMod200Object().getMod200().getUteParticipations().get(idx).getDocument());
			document.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteParticipations().get(idx).setDocument(document.getValue());
				callback.markAsDirty();
			});
			
			CheckBox rep = new CheckBox();
			rep.setValue(callback.getMod200Object().getMod200().getUteParticipations().get(idx).isRepresentative());
			rep.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					callback.getMod200Object().getMod200().getUteParticipations().get(idx).setRepresentative(rep.getValue());
					callback.markAsDirty();
				}
			});
			
			AonTextBox name = new AonTextBox();
			name.setMaxLength(30);
			name.setVisibleLength(40);			
			name.setValue(callback.getMod200Object().getMod200().getUteParticipations().get(idx).getName());
			name.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteParticipations().get(idx).setName(name.getValue());
				callback.markAsDirty();
			});
			
			ProvinceCountryListBox provinceCountry = new ProvinceCountryListBox();
			provinceCountry.setSelectedIndex(0);
			UteParticipation ca = callback.getMod200Object().getMod200().getUteParticipations().get(idx);
			int p = ca.getProvince();
			Country c = Country.safeValueOf(ca.getCountry());
			if (p > 0 && p < Province.values().length) {
				provinceCountry.setSelectedIndex(p);				
			} else if (c != null) {				
				provinceCountry.setSelectedIndex(Province.values().length + c.ordinal());
			}			
			provinceCountry.addChangeHandler(new ChangeHandler() {			
				@Override
				public void onChange(ChangeEvent event) {
					int index = provinceCountry.getSelectedIndex();
					if (index < Province.values().length) {
						callback.getMod200Object().getMod200().getUteParticipations().get(idx).setProvince(index);
						callback.getMod200Object().getMod200().getUteParticipations().get(idx).setCountry(null);
					} else {				
						callback.getMod200Object().getMod200().getUteParticipations().get(idx).setProvince(0);
						callback.getMod200Object().getMod200().getUteParticipations().get(idx).setCountry(Country.safeIso2(Country.values()[index-Province.values().length]));
					}
					callback.markAsDirty();
				}
			});
			
			AonDoubleBox nominal = new AonDoubleBox();
			nominal.setValue(callback.getMod200Object().getMod200().getUteParticipations().get(idx).getBase());
			nominal.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteParticipations().get(idx).setBase(nominal.getValue());
				callback.markAsDirty();
			});
			
			AonDoubleBox percent = new AonDoubleBox();
			percent.setMaxLength(6);
			percent.setVisibleLength(6);
			percent.setValue(callback.getMod200Object().getMod200().getUteParticipations().get(idx).getPercent());
			percent.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteParticipations().get(idx).setPercent(percent.getValue());
				callback.markAsDirty();
			});
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getUteParticipations().remove(idx);
				paint();
				callback.markAsDirty();
			});
	
			tabB11.addRow()
				.addCell(document)
				.addCell(rep)				
				.addCell(name)
				.addCell(provinceCountry)
				.addCell(nominal)
				.addCell(percent)				
				.addCell(deleteButton);
		}
		
		// Botón añadir 
		AonTableButton addButtonB11 = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButtonB11.addStyleName(AON.CSS.aonMarginTop());
		addButtonB11.addStyleName(AON.CSS.aonMarginLeft());
		addButtonB11.addClickHandler(event -> {
			callback.getMod200Object().getMod200().getUteParticipations().add(new UteParticipation());
			paint();
		});
		
		FlowPanel panelB11 = new FlowPanel();
		panelB11.add(tabB11);
		panelB11.add(addButtonB11);
		
		table1.getFlexCellFormatter().setColSpan(row, 0, 2);
		table1.setWidget(row, 0, panelB11);
	
		// Información de detalle de EP o UTE que operen en el extranjero y por participación en fórmula de colaboración análoga a UTE
		
		basePanel.add(getTitle(AON.MSG.utefor()));
		
		AonDisplayTable tabForeign = new AonDisplayTable();
		tabForeign.addStyleName(AON.CSS.aonWidthAlmostAll());
		tabForeign.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tabForeign);
		
		tabForeign.addRow()
			.addCell( new Label(AON.MSG.identification()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth200())
			.addCell( new Label(AON.MSG.utefor1()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.utefor2()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.utefor3()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.utefor4()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.utefor5()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getUteForeign().size(); i++) {
			final int idx = i;
			
			AonTextBox identification = new AonTextBox();
			identification.setMaxLength(30);
			identification.setVisibleLength(30);
			identification.setValue(callback.getMod200Object().getMod200().getUteForeign().get(idx).getIdentification());
			identification.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteForeign().get(idx).setIdentification(identification.getValue());
				callback.markAsDirty();
			});			
			
			CountryListBox country = new CountryListBox();
			country.setWidth("140px");
			country.setValue(Country.safeValueOf(callback.getMod200Object().getMod200().getUteForeign().get(idx).getCountry()));
			country.addChangeHandler(new ChangeHandler() {			
				@Override
				public void onChange(ChangeEvent event) {
					callback.getMod200Object().getMod200().getUteForeign().get(idx).setCountry(Country.safeIso2(country.getValue()));
					callback.markAsDirty();
				}
			});
			
			AonDoubleBox volume = new AonDoubleBox();
			volume.setValue(callback.getMod200Object().getMod200().getUteForeign().get(idx).getVolume());
			volume.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteForeign().get(idx).setVolume(volume.getValue());
				callback.markAsDirty();
			});
			
			AonDoubleBox pyg = new AonDoubleBox();
			pyg.setValue(callback.getMod200Object().getMod200().getUteForeign().get(idx).getPyg());
			pyg.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteForeign().get(idx).setPyg(pyg.getValue());
				callback.markAsDirty();
			});
			
			AonDoubleBox adjust = new AonDoubleBox();
			adjust.setValue(callback.getMod200Object().getMod200().getUteForeign().get(idx).getAdjust());
			adjust.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteForeign().get(idx).setAdjust(adjust.getValue());
				callback.markAsDirty();
			});
			
			AonDoubleBox deduction = new AonDoubleBox();
			deduction.setValue(callback.getMod200Object().getMod200().getUteForeign().get(idx).getDeduction());
			deduction.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getUteForeign().get(idx).setDeduction(deduction.getValue());
				callback.markAsDirty();
			});
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getUteForeign().remove(idx);
				paint();
				callback.markAsDirty();
			});
	
			tabForeign.addRow()
				.addCell(identification)
				.addCell(country)				
				.addCell(volume)
				.addCell(pyg)
				.addCell(adjust)
				.addCell(deduction)				
				.addCell(deleteButton);
		}
		
		// Botón añadir 
		AonTableButton addButtonForeign = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButtonForeign.addStyleName(AON.CSS.aonMarginTop());
		addButtonForeign.addStyleName(AON.CSS.aonMarginLeft());
		addButtonForeign.addClickHandler(event -> {
			callback.getMod200Object().getMod200().getUteForeign().add(new UteForeign());
			paint();
		});		
		basePanel.add(addButtonForeign);
		
	}
	
}
