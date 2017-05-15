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
import com.esferalia.aon.gwt.fiscal.client.mod200.e2016.ParticipationPanel.ParticipationPanelCallback;
import com.esferalia.aon.occam.api.model.CompanyParticipation;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class Page02 extends PageAbs {

	
	interface Page2Binder extends UiBinder<Widget, Page02> {}

	private static final Page2Binder page2Binder = GWT.create(Page2Binder.class);

	private ListDataProvider<CompanyParticipation> dataProviderIn;
	private ListDataProvider<CompanyParticipation> dataProviderOut;
	
	ParticipationPanel participationPanel;
	
	@UiField(provided = true)
	CellTable<CompanyParticipation> tableIn;

	@UiField(provided = true)
	CellTable<CompanyParticipation> tableOut;
	
	@UiField
	Button newParticipationOut;
	@UiField
	Button newParticipationIn;

	@UiField(provided = true)
	FlexTable table1;

	public Page02( Model200PageCallback callback ) {
		super(callback);
		table1 = new FlexTable();
		
		participationPanel = new ParticipationPanel( new ParticipationPanelCallback() {
			
			@Override
			public void onCancel() {
				tableOut.redraw();
			}
			
			@Override
			public void onAccept(int index, CompanyParticipation cp) {
				if (index < 0) {
					callback.getMod200Object().getMod200().getParticipationsOut().add(cp);
					dataProviderOut.getList().add(cp);
				} else {
					callback.getMod200Object().getMod200().getParticipationsOut().set(index, cp);
					dataProviderOut.getList().set(index,cp);
				}
				tableOut.redraw();
				callback.getMod200Object().doubleValueChanged(Mod2002016Key.P1501,
						callback.getMod200Object().getDoubleValue(Mod2002016Key.P1501));
			}
		});
		
		tableIn = new CellTable<CompanyParticipation>(25,Model200Table.TABLE_STYLE);
		
		tableIn.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		tableIn.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

		tableIn.setEmptyTableWidget(new HTML(AON.MSG.noData()));
		dataProviderIn = new ListDataProvider<CompanyParticipation>();
		dataProviderIn.addDataDisplay(tableIn);

		addInDocumentColumn();
		addInRepresentativeColumn();
		addInDescriptionColumn();
		addInProvinceColumn();
		addInPercentColumn();
		addInNominalValueColumn();
		addInRemoveColumn();
		
		tableOut = new CellTable<CompanyParticipation>(25,Model200Table.TABLE_STYLE);
		
		tableOut.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE); 
		tableOut.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		
		tableOut.setEmptyTableWidget(new HTML(AON.MSG.noData()));
		dataProviderOut = new ListDataProvider<CompanyParticipation>();
		dataProviderOut.addDataDisplay(tableOut);

		addOutSelectionColumn();
		addOutDocumentColumn();
		addOutDescriptionColumn();
		addOutPercentColumn();
		addOutNominalValueColumn();
		addOutRemoveColumn();
		
		Widget ui = page2Binder.createAndBindUi(this);
		initWidget(ui);
	}

	private void addInDocumentColumn() {
		SizableTextInputCell input = new SizableTextInputCell(8);
		Column<CompanyParticipation, String> documentColumn = new Column<CompanyParticipation, String>(
				input) {
			@Override
			public String getValue(CompanyParticipation object) {
				return object.getDocument();
			}
		};
		documentColumn.setFieldUpdater(new FieldUpdater<CompanyParticipation, String>() {
		    public void update(int index, CompanyParticipation cp, String value) {
		    	dataProviderIn.getList().get(index).setDocument(value);
		    }
		});		
		tableIn.addColumn(documentColumn, AON.MSG.document());
		documentColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
		tableIn.setColumnWidth(documentColumn, 100, Unit.PX);
	}

	private void addInDescriptionColumn() {
		SizableTextInputCell input = new SizableTextInputCell(30);
		Column<CompanyParticipation, String> descriptionColumn = new Column<CompanyParticipation, String>(
				input) {
			@Override
			public String getValue(CompanyParticipation ca) {
				return ca.getName();
			}
		};
		descriptionColumn.setFieldUpdater(new FieldUpdater<CompanyParticipation, String>() {
		    public void update(int index, CompanyParticipation cp, String value) {
		    	dataProviderIn.getList().get(index).setName(value);
		    }
		});		
		tableIn.addColumn(descriptionColumn, AON.MSG.companyName());
		descriptionColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
	}

	private void addInRepresentativeColumn() {
		Column<CompanyParticipation, Boolean> representativeColumn = new Column<CompanyParticipation, Boolean>(
				new TabCheckboxCell()) {
			@Override
			public Boolean getValue(CompanyParticipation ca) {
				return ca.isRepresentative();
			}
		};
		representativeColumn.setFieldUpdater(new FieldUpdater<CompanyParticipation, Boolean>() {
		    public void update(int index, CompanyParticipation cp, Boolean value) {
		    	dataProviderIn.getList().get(index).setRepresentative(value);
		    }
		});		
		tableIn.addColumn(representativeColumn, "Rpte.");
		tableIn.setColumnWidth(representativeColumn, 30, Unit.PX);
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
		Column<CompanyParticipation, String> provinceColumn = new Column<CompanyParticipation, String>(
				provinceCell) {
			
			@Override
			public String getValue(CompanyParticipation ca) {
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
		provinceColumn.setFieldUpdater(new FieldUpdater<CompanyParticipation, String>() {
		    public void update(int index, CompanyParticipation cp, String value) {
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
		    	dataProviderIn.getList().get(index).setProvince(p==null?0:p.ordinal());
		    	dataProviderIn.getList().get(index).setCountry(c==null?null:c.getIso2());
		    }
		});		
		tableIn.addColumn(provinceColumn, AON.MSG.province() + "/" + AON.MSG.country());
		tableIn.setColumnWidth(provinceColumn, 150, Unit.PX);
		provinceColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
	}

	private void addInPercentColumn() {
		SizableTextInputCell input = new SizableTextInputCell(5);
		Column<CompanyParticipation, String> percentColumn = new Column<CompanyParticipation, String>(
				input) {
			@Override
			public String getValue(CompanyParticipation ca) {
				return Double.toString( ca.getPercent() );
			}
		};
		percentColumn.setFieldUpdater(new FieldUpdater<CompanyParticipation, String>() {
		    public void update(int index, CompanyParticipation cp, String value) {
		    	try {
		    		double p = Double.parseDouble(value);
		    		dataProviderIn.getList().get(index).setPercent(p);
		    	} catch (NumberFormatException e) {
		    		MessageDialog.error("Porcentaje no v\u00E1lido.");
		    	}
		    }
		});		
		tableIn.addColumn(percentColumn, "%");
		tableIn.setColumnWidth(percentColumn, 50, Unit.PX);
		percentColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
	}

	private void addInNominalValueColumn() {
		SizableTextInputCell input = new SizableTextInputCell(8);
		Column<CompanyParticipation, String> nominalValueColumn = new Column<CompanyParticipation, String>(
				input) {
			@Override
			public String getValue(CompanyParticipation ca) {
				return Double.toString( ca.getNominalValue() );
			}
		};
		nominalValueColumn.setFieldUpdater(new FieldUpdater<CompanyParticipation, String>() {
		    public void update(int index, CompanyParticipation cp, String value) {
		    	try {
		    		double p = Double.parseDouble(value);
		    		dataProviderIn.getList().get(index).setNominalValue(p);
		    	} catch (NumberFormatException e) {
		    		MessageDialog.error("N\u00FAmero no v\u00E1lido.");
		    	}
		    }
		});		
		tableIn.addColumn(nominalValueColumn, AON.MSG.nominalValue());
		tableIn.setColumnWidth(nominalValueColumn, 100, Unit.PX);
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
		Column<CompanyParticipation,String> col = new Column<CompanyParticipation,String>(removeButton) {
		  public String getValue(CompanyParticipation object) {
		    return AON.MSG.deleteAction();
		  }
		};
		col.setFieldUpdater(new FieldUpdater<CompanyParticipation, String>() {
			
		    public void update(int index, CompanyParticipation ca, String value) {
		    	ConfirmDialog cd = new ConfirmDialog();
		    	cd.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {
					
					@Override
					public void onCancel() {}
					
					@Override
					public void onAccept() {
						callback.getMod200Object().getMod200().getParticipationsIn().remove(index);
						dataProviderIn = new ListDataProvider<CompanyParticipation>(
								callback.getMod200Object().getMod200().getParticipationsIn());
						dataProviderIn.addDataDisplay(tableIn);
			    		tableIn.redraw();
					}
				});
		    }
		});		
		tableIn.addColumn(col);
		tableIn.setColumnWidth(col, 20, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
	}

	@UiHandler("newParticipationIn")
	void onNewParticipationIn(ClickEvent event) {
		dataProviderIn.getList().add(new CompanyParticipation());
		tableIn.redraw();		    		
	}
	
	private void addOutSelectionColumn() {
		ButtonCell selectButton = new ButtonCell( new SelectButtonSafeHtmlTemplates())  {
			  @Override
			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
			    if (data != null) {
			      sb.append(data);
			    }
			  }
		};
		Column<CompanyParticipation,String> col = new Column<CompanyParticipation,String>(selectButton) {
		  public String getValue(CompanyParticipation object) {
		    return AON.MSG.selectAction();
		  }
		};
		col.setFieldUpdater(new FieldUpdater<CompanyParticipation, String>() {
			
		    public void update(int index, CompanyParticipation ca, String value) {
				participationPanel.dump(index,dataProviderOut.getList().get(index));
				participationPanel.center();
				participationPanel.show();
		    }
		});		
		tableOut.addColumn(col);
		tableOut.setColumnWidth(col, 20, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
	}

	private void addOutDocumentColumn() {
		Column<CompanyParticipation, String> documentColumn = new Column<CompanyParticipation, String>(
				new TextCell()) {
			@Override
			public String getValue(CompanyParticipation object) {
				return object.getDocument();
			}
		};
		tableOut.addColumn(documentColumn, AON.MSG.document());
		documentColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
		tableOut.setColumnWidth(documentColumn, 100, Unit.PX);
	}

	private void addOutDescriptionColumn() {
		Column<CompanyParticipation, String> descriptionColumn = new Column<CompanyParticipation, String>(
				new TextCell()) {
			@Override
			public String getValue(CompanyParticipation ca) {
				return ca.getName();
			}
		};
		tableOut.addColumn(descriptionColumn, AON.MSG.companyName());
		descriptionColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
	}
	
	private void addOutPercentColumn() {
		Column<CompanyParticipation, String> percentColumn = new Column<CompanyParticipation, String>(
				new TextCell()) {
			@Override
			public String getValue(CompanyParticipation ca) {
				return Double.toString( ca.getPercent() );
			}
		};
		tableOut.addColumn(percentColumn, "%");
		tableOut.setColumnWidth(percentColumn, 50, Unit.PX);
		percentColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
	}

	private void addOutNominalValueColumn() {
		Column<CompanyParticipation, String> nominalValueColumn = new Column<CompanyParticipation, String>(
				new TextCell()) {
			@Override
			public String getValue(CompanyParticipation ca) {
				return Double.toString( ca.getNominalValue() );
			}
		};
		tableOut.addColumn(nominalValueColumn, AON.MSG.nominalValue());
		tableOut.setColumnWidth(nominalValueColumn, 100, Unit.PX);
		nominalValueColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
	}

	private void addOutRemoveColumn() {
		ButtonCell removeButton = new ButtonCell( new DeleteButtonSafeHtmlTemplates())  {
			  @Override
			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
			    if (data != null) {
			      sb.append(data);
			    }
			  }
		};
		Column<CompanyParticipation,String> col = new Column<CompanyParticipation,String>(removeButton) {
		  public String getValue(CompanyParticipation object) {
		    return AON.MSG.deleteAction();
		  }
		};
		col.setFieldUpdater(new FieldUpdater<CompanyParticipation, String>() {
		    public void update(int index, CompanyParticipation ca, String value) {
		    	ConfirmDialog cd = new ConfirmDialog();
		    	cd.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {
					
					@Override
					public void onCancel() {
					}
					
					@Override
					public void onAccept() {
						callback.getMod200Object().getMod200().getParticipationsOut().remove(index);
						dataProviderOut = new ListDataProvider<CompanyParticipation>(
								callback.getMod200Object().getMod200().getParticipationsOut());
						dataProviderOut.addDataDisplay(tableOut);
			    		tableOut.redraw();
					}
				});
		    }
		});		
		tableOut.addColumn(col);
		tableOut.setColumnWidth(col, 20, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
	}

	@UiHandler("newParticipationOut")
	void onNewParticipationOut(ClickEvent event) {
		CompanyParticipation cp = new CompanyParticipation();
		participationPanel.dump(-1,cp);
		participationPanel.center();
		participationPanel.show();
	}
	
	@Override
	public void dump() {
		super.dump();
		dataProviderIn = callback.getMod200Object().getMod200().getParticipationsIn() == null
			?new ListDataProvider<CompanyParticipation>()
			:new ListDataProvider<CompanyParticipation>(callback.getMod200Object().getMod200().getParticipationsIn());

		dataProviderIn.addDataDisplay(tableIn);
		tableIn.redraw();

		dataProviderOut = callback.getMod200Object().getMod200().getParticipationsOut() == null
			?new ListDataProvider<CompanyParticipation>()
			:new ListDataProvider<CompanyParticipation>(callback.getMod200Object().getMod200().getParticipationsOut());
		dataProviderOut.addDataDisplay(tableOut);
		tableOut.redraw();
	}

	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setWidth(1, "250px");
		int row = 0;
		for (final Mod2002016Key key : Mod2002016Constants.PARTICIPATION_KEYS) {
			if (callback.getMod200Object().isVisible(key)) {
				row = paintKey(key,row);
			}
		}
		
		table1.setWidth("100%");
		table1.setCellSpacing(0);
		table1.getColumnFormatter().setWidth(1, "250px");
		paintKey(table1,Mod2002016Key.POR51,0);
		paintKey(table1,Mod2002016Key.PORES,1);
	}

	@Override
	public void populate() {
		LinkedList<CompanyParticipation> listIn = new LinkedList<CompanyParticipation>();
		for (CompanyParticipation cp : dataProviderIn.getList()) {
			listIn.add(cp);
		}
		callback.getMod200Object().getMod200().setParticipationsIn(listIn);
		
		LinkedList<CompanyParticipation> listOut = new LinkedList<CompanyParticipation>();
		for (CompanyParticipation cp : dataProviderOut.getList()) {
			listOut.add(cp);
		}
		callback.getMod200Object().getMod200().setParticipationsOut(listOut);
	}

}
