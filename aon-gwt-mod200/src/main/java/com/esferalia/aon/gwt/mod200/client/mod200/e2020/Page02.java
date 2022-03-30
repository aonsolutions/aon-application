// PARTICIPACIONES
package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ProvinceCountryListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Model2002020.Model200PageCallback;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.ParticipationPanel.ParticipationPanelCallback;
import com.esferalia.aon.occam.api.model.fiscal.MinorEntity;
import com.esferalia.aon.occam.api.model.fiscal.mod200.Mod200CompanyParticipation;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Key;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Province;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;

public class Page02 extends PageAbs {
	
//	interface Page2Binder extends UiBinder<Widget, Page02> {}
//	private static final Page2Binder page2Binder = GWT.create(Page2Binder.class);

//	private ListDataProvider<Mod200CompanyParticipation> dataProviderIn;
//	private ListDataProvider<Mod200CompanyParticipation> dataProviderOut;
//	private ListDataProvider<MinorEntity> dataProviderMinor;
//	
	private ParticipationPanel participationPanel;
//	
//	@UiField(provided = true)
//	CellTable<Mod200CompanyParticipation> tableIn;
//
//	@UiField(provided = true)
//	CellTable<Mod200CompanyParticipation> tableOut;
//	
//	@UiField(provided = true)
//	CellTable<MinorEntity> tableMinor;
//	
//	@UiField
//	Button newParticipationOut;
//	@UiField
//	Button newParticipationIn;
//	@UiField
//	Button newMinor;
//
//	@UiField(provided = true)
//	FlexTable table; // Totales apartado B1
//	@UiField(provided = true)
//	FlexTable table1; // Casillas de porcentajes despues del apartado B2
	
//	private FlowPanel basePanel;

	public Page02( Model200PageCallback callback ) {
		super(callback);
		
//		table  = new FlexTable();
//		table1 = new FlexTable();
//		
		
		participationPanel = new ParticipationPanel( new ParticipationPanelCallback() {
			
			@Override
			public void onCancel() {
//				tableOut.redraw();
				paint();
			}
			
			@Override
			public void onAccept(int index, Mod200CompanyParticipation cp) {
				if (index < 0) {
					callback.getMod200Object().getMod200().getParticipationsOut().add(cp);
				} else {
					callback.getMod200Object().getMod200().getParticipationsOut().set(index,cp);
				}
//				tableOut.redraw();
				paint();
				calculate();
				callback.markAsDirty();
			}
		});
		
//		tableIn = new CellTable<Mod200CompanyParticipation>(25,Model200Table.TABLE_STYLE);
//		
//		tableIn.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
//		tableIn.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
//
//		tableIn.setEmptyTableWidget(new HTML(AON.MSG.noData()));
//		dataProviderIn = new ListDataProvider<Mod200CompanyParticipation>();
//		dataProviderIn.addDataDisplay(tableIn);
//
//		addInDocumentColumn();
//		addInRepresentativeColumn();
//		addInFjoColumn();
//		addInDescriptionColumn();
//		addInProvinceColumn();
//		addInPercentColumn();
//		addInNominalValueColumn();
//		addInRemoveColumn();
//		
//		tableOut = new CellTable<Mod200CompanyParticipation>(25,Model200Table.TABLE_STYLE);
//		
//		tableOut.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE); 
//		tableOut.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
//		
//		tableOut.setEmptyTableWidget(new HTML(AON.MSG.noData()));
//		dataProviderOut = new ListDataProvider<Mod200CompanyParticipation>();
//		dataProviderOut.addDataDisplay(tableOut);
//
//		addOutSelectionColumn();
//		addOutDocumentColumn();
//		addOutDescriptionColumn();
//		addOutPercentColumn();
//		addOutNominalValueColumn();
//		addOutRemoveColumn();
//		
//		tableMinor = new CellTable<MinorEntity>(25,Model200Table.TABLE_STYLE);
//		
//		tableMinor.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE); 
//		tableMinor.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
//		
//		tableMinor.setEmptyTableWidget(new HTML(AON.MSG.noData()));
//		dataProviderMinor = new ListDataProvider<MinorEntity>();
//		dataProviderMinor.addDataDisplay(tableMinor);
//
//		addMinorDocumentColumn();
//		addMinorNameColumn();
//		addMinorRemoveColumn();
//		
//		Widget ui = page2Binder.createAndBindUi(this);
//		initWidget(ui);
		
//		ScrollPanel scroll = new ScrollPanel();
//		basePanel = new FlowPanel();
//		scroll.add(basePanel);
//		initWidget(scroll);
		
		addBasePanel();
		
		initializeTable();
		
	}

	protected void calculate() {
		populate();
		callback.getMod200Object().doubleValueChanged(Mod2002020Key.P1501,
				callback.getMod200Object().getDoubleValue(Mod2002020Key.P1501));
	}

//	private void addInDocumentColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(8);
//		Column<Mod200CompanyParticipation, String> documentColumn = new Column<Mod200CompanyParticipation, String>(
//				input) {
//			@Override
//			public String getValue(Mod200CompanyParticipation object) {
//				return object.getDocument();
//			}
//		};
//		documentColumn.setFieldUpdater(new FieldUpdater<Mod200CompanyParticipation, String>() {
//		    public void update(int index, Mod200CompanyParticipation cp, String value) {
//		    	dataProviderIn.getList().get(index).setDocument(value);
//		    }
//		});		
//		tableIn.addColumn(documentColumn, AON.MSG.document());
//		documentColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//		tableIn.setColumnWidth(documentColumn, 100, Unit.PX);
//	}
//
//	private void addInDescriptionColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(30);
//		Column<Mod200CompanyParticipation, String> descriptionColumn = new Column<Mod200CompanyParticipation, String>(
//				input) {
//			@Override
//			public String getValue(Mod200CompanyParticipation ca) {
//				return ca.getName();
//			}
//		};
//		descriptionColumn.setFieldUpdater(new FieldUpdater<Mod200CompanyParticipation, String>() {
//		    public void update(int index, Mod200CompanyParticipation cp, String value) {
//		    	dataProviderIn.getList().get(index).setName(value);
//		    }
//		});		
//		tableIn.addColumn(descriptionColumn, AON.MSG.companyName());
//		descriptionColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//	}
//
//	private void addInRepresentativeColumn() {
//		Column<Mod200CompanyParticipation, Boolean> representativeColumn = new Column<Mod200CompanyParticipation, Boolean>(
//				new TabCheckboxCell()) {
//			@Override
//			public Boolean getValue(Mod200CompanyParticipation ca) {
//				return ca.isRepresentative();
//			}
//		};
//		representativeColumn.setFieldUpdater(new FieldUpdater<Mod200CompanyParticipation, Boolean>() {
//		    public void update(int index, Mod200CompanyParticipation cp, Boolean value) {
//		    	dataProviderIn.getList().get(index).setRepresentative(value);
//		    }
//		});		
//		tableIn.addColumn(representativeColumn, "Rpte.");
//		tableIn.setColumnWidth(representativeColumn, 30, Unit.PX);
//		representativeColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//	}
//
//	private void addInFjoColumn() {
//		/**
//		 * El campo F/J/Otra modificado en 2017 se almacena en el campo "notary" como String.
//		 * Únicamente para Participaciones de personas/entidades en la declarante.
//		 */
//		SizableTextInputCell input = new SizableTextInputCell(1);
//		Column<Mod200CompanyParticipation, String> fjoColumn = new Column<Mod200CompanyParticipation, String>(input) {
//			@Override
//			public String getValue(Mod200CompanyParticipation ca) {
//				return ca.getNotary();
//			}
//		};
//		fjoColumn.setFieldUpdater(new FieldUpdater<Mod200CompanyParticipation, String>() {
//		    public void update(int index, Mod200CompanyParticipation cp, String value) {
//		    	dataProviderIn.getList().get(index).setNotary(value);
//		    }
//		});		
//		tableIn.addColumn(fjoColumn, "F/J/Otra");
//		fjoColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//	}
//
//	private void addInProvinceColumn() {
//		final LinkedList<String> options = new LinkedList<String>();
//		for (Province prov : Province.values()) {
//			options.add( prov.getName() );
//		}
//		for (Country c : Country.values()) {
//			options.add( c.getName() );
//		}
//		TabSelectionCell provinceCell = new TabSelectionCell(options);
//		Column<Mod200CompanyParticipation, String> provinceColumn = new Column<Mod200CompanyParticipation, String>(
//				provinceCell) {
//			
//			@Override
//			public String getValue(Mod200CompanyParticipation ca) {
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
//		provinceColumn.setFieldUpdater(new FieldUpdater<Mod200CompanyParticipation, String>() {
//		    public void update(int index, Mod200CompanyParticipation cp, String value) {
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
//		    	dataProviderIn.getList().get(index).setProvince(p==null?0:p.ordinal());
//		    	dataProviderIn.getList().get(index).setCountry(c==null?null:c.getIso2());
//		    }
//		});		
//		tableIn.addColumn(provinceColumn, AON.MSG.province() + "/" + AON.MSG.country());
//		tableIn.setColumnWidth(provinceColumn, 150, Unit.PX);
//		provinceColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//	}
//
//	private void addInPercentColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(5);
//		Column<Mod200CompanyParticipation, String> percentColumn = new Column<Mod200CompanyParticipation, String>(
//				input) {
//			@Override
//			public String getValue(Mod200CompanyParticipation ca) {
//				return Double.toString( ca.getPercent() );
//			}
//		};
//		percentColumn.setFieldUpdater(new FieldUpdater<Mod200CompanyParticipation, String>() {
//		    public void update(int index, Mod200CompanyParticipation cp, String value) {
//		    	try {
//		    		double p = Double.parseDouble(value);
//		    		dataProviderIn.getList().get(index).setPercent(p);
//		    	} catch (NumberFormatException e) {
//		    		MessageDialog.error("Porcentaje no v\u00E1lido.");
//		    	}
//		    }
//		});		
//		tableIn.addColumn(percentColumn, "%");
//		tableIn.setColumnWidth(percentColumn, 50, Unit.PX);
//		percentColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//	}
//
//	private void addInNominalValueColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(8);
//		Column<Mod200CompanyParticipation, String> nominalValueColumn = new Column<Mod200CompanyParticipation, String>(
//				input) {
//			@Override
//			public String getValue(Mod200CompanyParticipation ca) {
//				return Double.toString( ca.getNominalValue() );
//			}
//		};
//		nominalValueColumn.setFieldUpdater(new FieldUpdater<Mod200CompanyParticipation, String>() {
//		    public void update(int index, Mod200CompanyParticipation cp, String value) {
//		    	try {
//		    		double p = Double.parseDouble(value);
//		    		dataProviderIn.getList().get(index).setNominalValue(p);
//		    	} catch (NumberFormatException e) {
//		    		MessageDialog.error("N\u00FAmero no v\u00E1lido.");
//		    	}
//		    }
//		});		
//		tableIn.addColumn(nominalValueColumn, AON.MSG.nominalValue());
//		tableIn.setColumnWidth(nominalValueColumn, 100, Unit.PX);
//		nominalValueColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//	}
//
//	private void addInRemoveColumn() {
//		ButtonCell removeButton = new ButtonCell( new DeleteButtonSafeHtmlTemplates())  {
//			  @Override
//			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
//			    if (data != null) {
//			      sb.append(data);
//			    }
//			  }
//		};
//		Column<Mod200CompanyParticipation,String> col = new Column<Mod200CompanyParticipation,String>(removeButton) {
//		  public String getValue(Mod200CompanyParticipation object) {
//		    return AON.MSG.deleteAction();
//		  }
//		};
//		col.setFieldUpdater(new FieldUpdater<Mod200CompanyParticipation, String>() {
//			
//		    public void update(int index, Mod200CompanyParticipation ca, String value) {
//		    	ConfirmDialog cd = new ConfirmDialog();
//		    	cd.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {
//					
//					@Override
//					public void onCancel() {}
//					
//					@Override
//					public void onAccept() {
//						dataProviderIn.getList().remove(index);
//			    		tableIn.redraw();
//			    		calculate();
//					}
//				});
//		    }
//		});		
//		tableIn.addColumn(col);
//		tableIn.setColumnWidth(col, 20, Unit.PX);
//		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//	}
//
//	@UiHandler("newParticipationIn")
//	void onNewParticipationIn(ClickEvent event) {
//		dataProviderIn.getList().add(new Mod200CompanyParticipation());
//		tableIn.redraw();		    		
//	}
	
//	private void addOutSelectionColumn() {
//		ButtonCell selectButton = new ButtonCell( new SelectButtonSafeHtmlTemplates())  {
//			  @Override
//			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
//			    if (data != null) {
//			      sb.append(data);
//			    }
//			  }
//		};
//		Column<Mod200CompanyParticipation,String> col = new Column<Mod200CompanyParticipation,String>(selectButton) {
//		  public String getValue(Mod200CompanyParticipation object) {
//		    return AON.MSG.selectAction();
//		  }
//		};
//		col.setFieldUpdater(new FieldUpdater<Mod200CompanyParticipation, String>() {
//			
//		    public void update(int index, Mod200CompanyParticipation ca, String value) {
//				participationPanel.dump(index,dataProviderOut.getList().get(index));
//				participationPanel.center();
//				participationPanel.show();
//		    }
//		});		
//		tableOut.addColumn(col);
//		tableOut.setColumnWidth(col, 20, Unit.PX);
//		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//	}
//
//	private void addOutDocumentColumn() {
//		Column<Mod200CompanyParticipation, String> documentColumn = new Column<Mod200CompanyParticipation, String>(
//				new TextCell()) {
//			@Override
//			public String getValue(Mod200CompanyParticipation object) {
//				return object.getDocument();
//			}
//		};
//		tableOut.addColumn(documentColumn, AON.MSG.document());
//		documentColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//		tableOut.setColumnWidth(documentColumn, 100, Unit.PX);
//	}
//
//	private void addOutDescriptionColumn() {
//		Column<Mod200CompanyParticipation, String> descriptionColumn = new Column<Mod200CompanyParticipation, String>(
//				new TextCell()) {
//			@Override
//			public String getValue(Mod200CompanyParticipation ca) {
//				return ca.getName();
//			}
//		};
//		tableOut.addColumn(descriptionColumn, AON.MSG.companyName());
//		descriptionColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//	}
//	
//	private void addOutPercentColumn() {
//		Column<Mod200CompanyParticipation, String> percentColumn = new Column<Mod200CompanyParticipation, String>(
//				new TextCell()) {
//			@Override
//			public String getValue(Mod200CompanyParticipation ca) {
//				return Double.toString( ca.getPercent() );
//			}
//		};
//		tableOut.addColumn(percentColumn, "%");
//		tableOut.setColumnWidth(percentColumn, 50, Unit.PX);
//		percentColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//	}
//
//	private void addOutNominalValueColumn() {
//		Column<Mod200CompanyParticipation, String> nominalValueColumn = new Column<Mod200CompanyParticipation, String>(
//				new TextCell()) {
//			@Override
//			public String getValue(Mod200CompanyParticipation ca) {
//				return Double.toString( ca.getNominalValue() );
//			}
//		};
//		tableOut.addColumn(nominalValueColumn, AON.MSG.nominalValue());
//		tableOut.setColumnWidth(nominalValueColumn, 100, Unit.PX);
//		nominalValueColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
//	}
//
//	private void addOutRemoveColumn() {
//		ButtonCell removeButton = new ButtonCell( new DeleteButtonSafeHtmlTemplates())  {
//			  @Override
//			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
//			    if (data != null) {
//			      sb.append(data);
//			    }
//			  }
//		};
//		Column<Mod200CompanyParticipation,String> col = new Column<Mod200CompanyParticipation,String>(removeButton) {
//		  public String getValue(Mod200CompanyParticipation object) {
//		    return AON.MSG.deleteAction();
//		  }
//		};
//		col.setFieldUpdater(new FieldUpdater<Mod200CompanyParticipation, String>() {
//		    public void update(int index, Mod200CompanyParticipation ca, String value) {
//		    	ConfirmDialog cd = new ConfirmDialog();
//		    	cd.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {
//					
//					@Override
//					public void onCancel() {
//					}
//					
//					@Override
//					public void onAccept() {
//						dataProviderOut.getList().remove(index);
//			    		tableOut.redraw();
//			    		calculate();
//					}
//				});
//		    }
//		});		
//		tableOut.addColumn(col);
//		tableOut.setColumnWidth(col, 20, Unit.PX);
//		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//	}
//
//	@UiHandler("newParticipationOut")
//	void onNewParticipationOut(ClickEvent event) {
//		Mod200CompanyParticipation cp = new Mod200CompanyParticipation();
//		participationPanel.dump(-1,cp);
//		participationPanel.center();
//		participationPanel.show();
//	}
	
//	private void addMinorDocumentColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(9);
//		Column<MinorEntity, String> documentColumn = new Column<MinorEntity, String>(
//				input) {
//			@Override
//			public String getValue(MinorEntity object) {
//				return object.getDocument();
//			}
//		};
//		documentColumn.setFieldUpdater(new FieldUpdater<MinorEntity, String>() {
//		    public void update(int index, MinorEntity me, String value) {
//		    	dataProviderMinor.getList().get(index).setDocument(value);
//		    }
//		});		
//		tableMinor.addColumn(documentColumn, AON.MSG.document());
//		documentColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//		tableMinor.setColumnWidth(documentColumn, 100, Unit.PX);
//	}
//
//	private void addMinorNameColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(40);
//		Column<MinorEntity, String> nameColumn = new Column<MinorEntity, String>(
//				input) {
//			@Override
//			public String getValue(MinorEntity me) {
//				return me.getName();
//			}
//		};
//		nameColumn.setFieldUpdater(new FieldUpdater<MinorEntity, String>() {
//		    public void update(int index, MinorEntity me, String value) {
//		    	dataProviderMinor.getList().get(index).setName(value);
//		    }
//		});		
//		tableMinor.addColumn(nameColumn, AON.MSG.nameCompanyName());
//		nameColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//	}
//
//	private void addMinorRemoveColumn() {
//		ButtonCell removeButton = new ButtonCell( new DeleteButtonSafeHtmlTemplates())  {
//			  @Override
//			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
//			    if (data != null) {
//			      sb.append(data);
//			    }
//			  }
//		};
//		Column<MinorEntity,String> col = new Column<MinorEntity,String>(removeButton) {
//		  public String getValue(MinorEntity object) {
//		    return AON.MSG.deleteAction();
//		  }
//		};
//		col.setFieldUpdater(new FieldUpdater<MinorEntity, String>() {
//			
//		    public void update(int index, MinorEntity me, String value) {
//		    	ConfirmDialog cd = new ConfirmDialog();
//		    	cd.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {
//					
//					@Override
//					public void onCancel() {}
//					
//					@Override
//					public void onAccept() {
//						dataProviderMinor.getList().remove(index);
//			    		tableMinor.redraw();
//			    		calculate();
//					}
//				});
//		    }
//		});		
//		tableMinor.addColumn(col);
//		tableMinor.setColumnWidth(col, 20, Unit.PX);
//		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//	}
//
//	@UiHandler("newMinor")
//	void onNewMinor(ClickEvent event) {
//		dataProviderMinor.getList().add(new MinorEntity());
//		tableMinor.redraw();		    		
//	}
	
	@Override
	public void dump() {
		super.dump();
//		dataProviderIn = callback.getMod200Object().getMod200().getParticipationsIn() == null
//			?new ListDataProvider<Mod200CompanyParticipation>()
//			:new ListDataProvider<Mod200CompanyParticipation>(callback.getMod200Object().getMod200().getParticipationsIn());
//
//		dataProviderIn.addDataDisplay(tableIn);
//		tableIn.redraw();
//
//		dataProviderOut = callback.getMod200Object().getMod200().getParticipationsOut() == null
//			?new ListDataProvider<Mod200CompanyParticipation>()
//			:new ListDataProvider<Mod200CompanyParticipation>(callback.getMod200Object().getMod200().getParticipationsOut());
//		dataProviderOut.addDataDisplay(tableOut);
//		tableOut.redraw();
//		
//		dataProviderMinor = callback.getMod200Object().getMod200().getMinorEntities() == null
//				?new ListDataProvider<MinorEntity>()
//				:new ListDataProvider<MinorEntity>(callback.getMod200Object().getMod200().getMinorEntities());
//		dataProviderMinor.addDataDisplay(tableMinor);
//		tableMinor.redraw();
		
	}

	@Override
	protected void initializeTable() {
//		table.setWidth("100%");
//		table.setCellSpacing(0);
//		
//		ColumnFormatter cf = table.getColumnFormatter();
//		cf.setWidth(1, "250px");
//		int row = 0;
//		for (final Mod2002020Key key : Mod2002020Constants.PARTICIPATION_KEYS) {
//			if (callback.getMod200Object().isVisible(key)) {
//				row = paintKey(table,key,row);
//			}
//		}
//		
//		table1.setWidth("100%");
//		table1.setCellSpacing(0);
//		table1.getColumnFormatter().setWidth(1, "250px");
//		paintKey(table1,Mod2002020Key.POR51,0);
//		paintKey(table1,Mod2002020Key.PORES,1);
		paint();
	}

	@Override
	public void populate() {
//		LinkedList<Mod200CompanyParticipation> listIn = new LinkedList<Mod200CompanyParticipation>();
//		for (Mod200CompanyParticipation cp : dataProviderIn.getList()) {
//			listIn.add(cp);
//		}
//		callback.getMod200Object().getMod200().setParticipationsIn(listIn);
//		
//		LinkedList<Mod200CompanyParticipation> listOut = new LinkedList<Mod200CompanyParticipation>();
//		for (Mod200CompanyParticipation cp : dataProviderOut.getList()) {
//			listOut.add(cp);
//		}
//		callback.getMod200Object().getMod200().setParticipationsOut(listOut);
//		
//		LinkedList<MinorEntity> listMinor = new LinkedList<MinorEntity>();
//		for (MinorEntity me : dataProviderMinor.getList()) {
//			listMinor.add(me);
//		}
//		callback.getMod200Object().getMod200().setMinorEntities(listMinor);		
	}
	
	private void paint() {
		
		// PARTICIPACIONES DE LA DECLARANTE EN OTRAS SOCIEDADES
		basePanel.clear();
		
		basePanel.add(getTitle(AON.MSG.participationsOut()));
		
		AonDisplayGrid grid = new AonDisplayGrid();
		grid.addStyleName(AON.CSS.aonWidthAlmostAll());
		grid.addStyleName(AON.CSS.aonBlockCenter());
		grid.addStyleName(AON.CSS.aonMarginTop());
		basePanel.add(grid);
		
		grid.addHeaderRow()
			.addCell(new Label(AON.MSG.document()),AON.CSS.aonWidth100())
			.addCell(new Label(AON.MSG.companyName()),AON.CSS.aonWidthAuto())
			.addCell(new Label("%"),AON.CSS.aonWidth40())
			.addCell(new Label(AON.MSG.nominalValue()),AON.CSS.aonWidth100())
			.addCell(new Label(""),AON.CSS.aonWidth20());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getParticipationsOut().size(); i++) {
			final int idx = i;
			
			Label documentLabel = new Label();
			documentLabel.setText(callback.getMod200Object().getMod200().getParticipationsOut().get(idx).getDocument());
			Label nameLabel = new Label();
			nameLabel.setText(callback.getMod200Object().getMod200().getParticipationsOut().get(idx).getName());
			Label percentLabel = new Label();
			percentLabel.setText(AON.FMT.format(callback.getMod200Object().getMod200().getParticipationsOut().get(idx).getPercent()));
			Label nominalLabel = new Label();
			nominalLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			nominalLabel.setText(AON.FMT.format(callback.getMod200Object().getMod200().getParticipationsOut().get(idx).getNominalValue()));
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(), AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getParticipationsOut().remove(idx);
				paint();
				callback.markAsDirty();
			});
			
			grid.addRow()
				.addCell(documentLabel)
				.addCell(nameLabel)
				.addCell(percentLabel)
				.addCell(nominalLabel)				
				.addCell(deleteButton)			
			    .addClickHandler( event -> {					
					participationPanel.dump(idx, callback.getMod200Object().getMod200().getParticipationsOut().get(idx));
					participationPanel.center();
					participationPanel.show();			    
			    });	
		}
		
		// Botón añadir participationOut
		AonTableButton addButton = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButton.addStyleName(AON.CSS.aonMarginTop());
		addButton.addStyleName(AON.CSS.aonMarginLeft());
		addButton.addClickHandler(event -> {
			Mod200CompanyParticipation cp = new Mod200CompanyParticipation();
			participationPanel.dump(-1,cp);
			participationPanel.center();
			participationPanel.show();
		});
		basePanel.add(addButton);
		
		basePanel.add(getSubtitle(AON.MSG.totals()));
		
		FlexTable tab1 = new FlexTable();
		tab1.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab1.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab1);
		
		int row = 0;
		for (final Mod2002020Key key : Mod2002020Constants.PARTICIPATION_KEYS) {
			if (callback.getMod200Object().isVisible(key)) {
				row = paintKey(tab1, key, row);
			}
		}
		
		// PARTICIPACIONES DE PERSONAS O ENTIDADES EN LA DECLARANTE
		
		basePanel.add(getTitle(AON.MSG.participationsIn()));
		
		AonDisplayTable tab2 = new AonDisplayTable();
		tab2.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab2.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab2);
		
		tab2.addRow()
			.addCell( new Label(AON.MSG.document()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth100())
			.addCell( new Label("Rpte."),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20())
			.addCell( new Label("F/J/Otra"),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20())
			.addCell( new Label(AON.MSG.companyName()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth300())
			.addCell( new Label(AON.MSG.province() + "/" + AON.MSG.country()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())
			.addCell( new Label("%"),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth40())
			.addCell( new Label(AON.MSG.nominalValue()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth100())
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getParticipationsIn().size(); i++) {
			final int idx = i;
			
			AonDocumentTextBox document = new AonDocumentTextBox();			
			document.setValue(callback.getMod200Object().getMod200().getParticipationsIn().get(idx).getDocument());
			document.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setDocument(document.getValue());
				callback.markAsDirty();
			});
			
			CheckBox rep = new CheckBox();
			rep.setValue(callback.getMod200Object().getMod200().getParticipationsIn().get(idx).isRepresentative());
			rep.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setRepresentative(rep.getValue());
					callback.markAsDirty();
				}
			});
			
			// El valor de "fjo" se guarda en el campo notary de la tabla 
			AonTextBox fjo = new AonTextBox();
			fjo.setMaxLength(1);
			fjo.setVisibleLength(1);			
			fjo.setValue(callback.getMod200Object().getMod200().getParticipationsIn().get(idx).getNotary());
			fjo.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setNotary(fjo.getValue());
				callback.markAsDirty();
			});
			
			AonTextBox name = new AonTextBox();
			name.setMaxLength(45);
			name.setVisibleLength(45);			
			name.setValue(callback.getMod200Object().getMod200().getParticipationsIn().get(idx).getName());
			name.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setName(name.getValue());
				callback.markAsDirty();
			});
			
			ProvinceCountryListBox provinceCountry = new ProvinceCountryListBox();
			provinceCountry.setSelectedIndex(0);
			Mod200CompanyParticipation ca = callback.getMod200Object().getMod200().getParticipationsIn().get(idx);
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
						callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setProvince(index);
						callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setCountry(null);
					} else {				
						callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setProvince(0);
						callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setCountry(Country.safeIso2(Country.values()[index-Province.values().length]));
					}
					callback.markAsDirty();
				}
			});
			
			AonDoubleBox percent = new AonDoubleBox();
			percent.setMaxLength(6);
			percent.setVisibleLength(6);
			percent.setValue(callback.getMod200Object().getMod200().getParticipationsIn().get(idx).getPercent());
			percent.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setPercent(percent.getValue());
				callback.markAsDirty();
			});
			
			AonDoubleBox nominal = new AonDoubleBox();
			nominal.setValue(callback.getMod200Object().getMod200().getParticipationsIn().get(idx).getNominalValue());
			nominal.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getParticipationsIn().get(idx).setNominalValue(nominal.getValue());
				callback.markAsDirty();
			});
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getParticipationsIn().remove(idx);
				paint();
				callback.markAsDirty();
			});

			tab2.addRow()
				.addCell(document)
				.addCell(rep)
				.addCell(fjo)
				.addCell(name)
				.addCell(provinceCountry)
				.addCell(percent)
				.addCell(nominal)
				.addCell(deleteButton);
		}
		
		// Botón añadir participationIn
		AonTableButton addButton2 = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButton2.addStyleName(AON.CSS.aonMarginTop());
		addButton2.addStyleName(AON.CSS.aonMarginLeft());
		addButton2.addClickHandler(event -> {
			callback.getMod200Object().getMod200().getParticipationsIn().add(new Mod200CompanyParticipation());
			paint();
		});
		basePanel.add(addButton2);
		
		FlexTable tab3 = new FlexTable();
		tab3.setStyleName(AON.CSS.aonMarginTop());
		tab3.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab3.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab3);
		
		paintKey(tab3, Mod2002020Key.POR51, 0);
		paintKey(tab3, Mod2002020Key.PORES, 1);
		
		// ENTIDADES MENORES
		
		basePanel.add(getTitle(AON.MSG.minorEntities()));
		
		AonDisplayTable tab4 = new AonDisplayTable();
		tab4.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab4.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab4);
		
		tab4.addRow()
			.addCell( new Label(AON.MSG.document()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth100())
			.addCell( new Label(AON.MSG.companyName()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth300())
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getMinorEntities().size(); i++) {
			final int idx = i;
			
			AonDocumentTextBox document = new AonDocumentTextBox();			
			document.setValue(callback.getMod200Object().getMod200().getMinorEntities().get(idx).getDocument());
			document.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getMinorEntities().get(idx).setDocument(document.getValue());
				callback.markAsDirty();
			});
			
			AonTextBox name = new AonTextBox();
			name.setMaxLength(40);
			name.setVisibleLength(40);			
			name.setValue(callback.getMod200Object().getMod200().getMinorEntities().get(idx).getName());
			name.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getMinorEntities().get(idx).setName(name.getValue());
				callback.markAsDirty();
			});
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getMinorEntities().remove(idx);
				paint();
				callback.markAsDirty();
			});

			tab4.addRow()
				.addCell(document)
				.addCell(name)
				.addCell(deleteButton);
		}
		
		// Botón añadir minor
		AonTableButton addButton4 = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButton4.addStyleName(AON.CSS.aonMarginTop());
		addButton4.addStyleName(AON.CSS.aonMarginLeft());
		addButton4.addClickHandler(event -> {
			callback.getMod200Object().getMod200().getMinorEntities().add(new MinorEntity());
			paint();
		});
		basePanel.add(addButton4);		
		
	}

}
