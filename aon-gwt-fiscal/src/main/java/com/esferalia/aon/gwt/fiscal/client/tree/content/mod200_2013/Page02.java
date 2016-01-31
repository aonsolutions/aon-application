package com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2013;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.cell.SizableTextInputCell;
import com.esferalia.aon.gwt.common.client.widget.cell.TabCheckboxCell;
import com.esferalia.aon.gwt.common.client.widget.cell.TabSelectionCell;
import com.esferalia.aon.gwt.fiscal.client.tree.node.Mod2002013TreeObject;
import com.esferalia.aon.occam.api.model.CompanyParticipation;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013Key;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Page02 extends PageAbs {

	interface Page2Binder extends UiBinder<Widget, Page02> {}

	private static final Page2Binder page2Binder = GWT.create(Page2Binder.class);

	private ListDataProvider<CompanyParticipation> dataProviderIn;
	private ListDataProvider<CompanyParticipation> dataProviderOut;
	private NoSelectionModel<CompanyParticipation> modelOut;
	
	ParticipationPanel participationPanel;
	
	@UiField(provided = true)
	CellTable<CompanyParticipation> tableIn;

	@UiField(provided = true)
	CellTable<CompanyParticipation> tableOut;
	
	DoubleBox cPor51;
	DoubleBox cPorES;
	
	@UiField
	Button newParticipationOut;
	@UiField
	Button newParticipationIn;

	public Page02() {
		participationPanel = new ParticipationPanel();
		
		CellTable.Resources aonTableStyle = GWT.create(AonCellTable.class);
		tableIn = new CellTable<CompanyParticipation>(25,aonTableStyle);
		
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
		
		tableOut = new CellTable<CompanyParticipation>(25,aonTableStyle);
		
		tableOut.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE); 
		tableOut.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		modelOut = new NoSelectionModel<CompanyParticipation>();
		modelOut.addSelectionChangeHandler(new SelectionChangeEvent.Handler(){
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				participationPanel.dump(modelOut.getLastSelectedObject());
				participationPanel.center();
				participationPanel.show();
			}
		});
		tableOut.setSelectionModel(modelOut);		
		
		tableOut.setEmptyTableWidget(new HTML(AON.MSG.noData()));
		dataProviderOut = new ListDataProvider<CompanyParticipation>();
		dataProviderOut.addDataDisplay(tableOut);

		addOutDocumentColumn();
		addOutDescriptionColumn();
		addOutPercentColumn();
		addOutNominalValueColumn();
		addOutRemoveColumn();
		
		Widget ui = page2Binder.createAndBindUi(this);
		initWidget(ui);
		participationPanel.setTable( tableOut );		
	}

	public void dump(Mod2002013TreeObject mod200Object) {
		this.mod200Object = mod200Object;
		dataProviderIn = this.mod200Object.getMod200().getParticipationsIn() == null
			?new ListDataProvider<CompanyParticipation>()
			:new ListDataProvider<CompanyParticipation>(this.mod200Object.getMod200().getParticipationsIn());

		dataProviderIn.addDataDisplay(tableIn);
		tableIn.redraw();

		dataProviderOut = this.mod200Object.getMod200().getParticipationsOut() == null
			?new ListDataProvider<CompanyParticipation>()
			:new ListDataProvider<CompanyParticipation>(this.mod200Object.getMod200().getParticipationsOut());
		dataProviderOut.addDataDisplay(tableOut);
		tableOut.redraw();
		initializeTable();
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
		final List<String> options = new LinkedList<String>();
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
		    		Window.alert("Porcentaje no v\u00E1lido.");
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
		    		Window.alert("Porcentaje no v\u00E1lido.");
		    	}
		    }
		});		
		tableIn.addColumn(nominalValueColumn, AON.MSG.nominalValue());
		tableIn.setColumnWidth(nominalValueColumn, 120, Unit.PX);
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
		    	if (Window.confirm(AON.MSG.confirmDeleteAction())) {
		    		dataProviderIn.getList().remove(index);
		    		tableIn.redraw();
		    	}
		    }
		});		
		tableIn.addColumn(col);
		tableIn.setColumnWidth(col, 20, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
	}

	@UiHandler("newParticipationIn")
	void onNewParticipationIn(ClickEvent event) {
		if (dataProviderIn.getList().size() < 6) {
			dataProviderIn.getList().add(new CompanyParticipation());
			tableIn.redraw();		    		
		} else {
			Window.alert("La aplicaci\u00F3n no permite m\u00E1s de seis participaciones");
		}
	}
	
	private void addOutDocumentColumn() {
		Column<CompanyParticipation, String> documentColumn = new Column<CompanyParticipation, String>(
				new TextCell()) {
			@Override
			public String getValue(CompanyParticipation object) {
				return object.getDocument();
			}
		};
		documentColumn.setFieldUpdater(new FieldUpdater<CompanyParticipation, String>() {
		    public void update(int index, CompanyParticipation cp, String value) {
		    	dataProviderOut.getList().get(index).setDocument(value);
		    }
		});		
		tableOut.addColumn(documentColumn, AON.MSG.document());
		documentColumn.setCellStyleNames(AON.AON_CSS.aonTextCenter());
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
		descriptionColumn.setFieldUpdater(new FieldUpdater<CompanyParticipation, String>() {
		    public void update(int index, CompanyParticipation cp, String value) {
		    	dataProviderOut.getList().get(index).setName(value);
		    }
		});		
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
		percentColumn.setFieldUpdater(new FieldUpdater<CompanyParticipation, String>() {
		    public void update(int index, CompanyParticipation cp, String value) {
		    	try {
		    		double p = Double.parseDouble(value);
		    		dataProviderOut.getList().get(index).setPercent(p);
		    	} catch (NumberFormatException e) {
		    		Window.alert("Porcentaje no v\u00E1lido.");
		    	}
		    }
		});		
		tableOut.addColumn(percentColumn, "%");
		tableOut.setColumnWidth(percentColumn, 50, Unit.PX);
		percentColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
	}

	private void addOutNominalValueColumn() {
		Column<CompanyParticipation, String> nominalValueColumn = new Column<CompanyParticipation, String>(
				new TextCell()) {
			@Override
			public String getValue(CompanyParticipation ca) {
				return Double.toString( ca.getNominalValue() );
			}
		};
		nominalValueColumn.setFieldUpdater(new FieldUpdater<CompanyParticipation, String>() {
		    public void update(int index, CompanyParticipation cp, String value) {
		    	try {
		    		double p = Double.parseDouble(value);
		    		dataProviderOut.getList().get(index).setNominalValue(p);
		    	} catch (NumberFormatException e) {
		    		Window.alert("Porcentaje no v\u00E1lido.");
		    	}
		    }
		});		
		tableOut.addColumn(nominalValueColumn, AON.MSG.nominalValue());
		tableOut.setColumnWidth(nominalValueColumn, 80, Unit.PX);
		nominalValueColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
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
		    	if (Window.confirm(AON.MSG.confirmDeleteAction())) {
		    		dataProviderOut.getList().remove(index);
		    		tableOut.redraw();
		    	}
		    }
		});		
		tableOut.addColumn(col);
		tableOut.setColumnWidth(col, 20, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
	}

	@UiHandler("newParticipationOut")
	void onNewParticipationOut(ClickEvent event) {
		if (dataProviderOut.getList().size() < 4) {
			CompanyParticipation cp = new CompanyParticipation();
			dataProviderOut.getList().add(cp);
			participationPanel.dump(cp);
			participationPanel.center();
			participationPanel.show();
		} else {
			Window.alert("La aplicaci\u00F3n no permite m\u00E1s de cuatro participaciones");
		}		
	}
	
	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setWidth(1, "250px");
		paintKey(Mod2002013Key.POR51,0);
		paintKey(Mod2002013Key.PORES,1);
	}

	public void populate(Mod2002013TreeObject obj) {
		List<CompanyParticipation> listIn = new LinkedList<CompanyParticipation>();
		for (CompanyParticipation cp : dataProviderIn.getList()) {
			listIn.add(cp);
		}
		this.mod200Object.getMod200().setParticipationsIn(listIn);
		
		List<CompanyParticipation> listOut = new LinkedList<CompanyParticipation>();
		for (CompanyParticipation cp : dataProviderOut.getList()) {
			listOut.add(cp);
		}
		this.mod200Object.getMod200().setParticipationsOut(listOut);
	}

}
