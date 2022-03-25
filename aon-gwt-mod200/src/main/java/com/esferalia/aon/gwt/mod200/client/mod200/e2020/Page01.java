// SECRETARIO, GRUPOS FISCALES, REPRESENTANTES, ADMINISTRADORES.
package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Model2002020.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.Secretary;
import com.esferalia.aon.occam.api.model.fiscal.mod200.Mod200CompanyAdministrator;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Key;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Province;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class Page01 extends PageAbs {

//	interface Page1Binder extends UiBinder<Widget, Page01> {
//	}
//	private static final Page1Binder page1Binder = GWT
//			.create(Page1Binder.class);

//	private ListDataProvider<LegalRepresentative> dataProvider1;
//	@UiField(provided = true)
//	CellTable<LegalRepresentative> table1;
//
//	private ListDataProvider<Mod200CompanyAdministrator> dataProvider2;
//	@UiField(provided = true)
//	CellTable<Mod200CompanyAdministrator> table2;

//	@UiField
	private AonDocumentTextBox secretaryDocument = new AonDocumentTextBox();
//	@UiField
	private AonTextBox secretaryName = new AonTextBox();
//	@UiField
	private AonDateBox irnr = new AonDateBox();

//	@UiField
//	private Panel fiscalGroupPanel;
//	@UiField
	private AonTextBox fiscalGroup = new AonTextBox();
//	@UiField
	private AonDocumentTextBox dominantDocument = new AonDocumentTextBox();
//	@UiField
	private AonTextBox dominantIdentificationNumber = new AonTextBox();
	
//	@UiField
//	private Panel mercantileGroupPanel;
//	@UiField 
	private AonDocumentTextBox ultimateDocument = new AonDocumentTextBox();           
//	@UiField
	private CountryListBox ultimateDocumentCountry = new CountryListBox();
//	@UiField
	private AonTextBox ultimateName = new AonTextBox();;				
//	@UiField
	private CountryListBox ultimateCountry = new CountryListBox(); 

//	@UiField
//	Button newLegalRepresentative;
//	@UiField
//	Button newAdministrator;
	
	private FlowPanel basePanel;	

	public Page01( Model200PageCallback callback ) {
		super(callback);

//		table1 = new CellTable<LegalRepresentative>(50, Model200Table.TABLE_STYLE);
//		table1.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
//		table1.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
//		table1.setEmptyTableWidget(new HTML(AON.MSG.noData()));
//		dataProvider1 = new ListDataProvider<LegalRepresentative>();
//		dataProvider1.addDataDisplay(table1);
//		addLegalDocumentColumn();
//		addLegalNameColumn();
//		addLegalNotaryColumn();
//		addLegalNotaryDateColumn();
//		addLegalRemoveColumn();
//
//		table2 = new CellTable<Mod200CompanyAdministrator>(50, Model200Table.TABLE_STYLE);
//		table2.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
//		table2.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
//		table2.setEmptyTableWidget(new HTML(AON.MSG.noData()));
//		dataProvider2 = new ListDataProvider<Mod200CompanyAdministrator>();
//		dataProvider2.addDataDisplay(table2);
//		addAdmDocumentColumn();
//		addAdmRepresentativeColumn();
//		addAdmNameColumn();
//		addAdmResidenceColumn();
//		addAdmProvinceColumn();
//		addAdmRemoveColumn();
		
//		Widget ui = page1Binder.createAndBindUi(this);
//		initWidget(ui);
		
		ScrollPanel scroll = new ScrollPanel();
		basePanel = new FlowPanel();
		scroll.add(basePanel);
		initWidget(scroll);
		
		initializeTable();
		paint();
	}

	@Override
	public void dump() {
		super.dump();
//		dataProvider1 = new ListDataProvider<LegalRepresentative>(callback.getMod200Object().getMod200().getRepresentatives());
//		dataProvider1.addDataDisplay(table1);
//		table1.redraw();
//
//		dataProvider2 = new ListDataProvider<Mod200CompanyAdministrator>(callback.getMod200Object().getMod200().getAdministrators());
//		dataProvider2.addDataDisplay(table2);
//		table2.redraw();
				
		this.fiscalGroup.setValue( callback.getMod200Object().getMod200().getFiscalGroup());
//		this.fiscalGroup.setEnabled( 
//				callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0009) 
//			  || callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0010));	
		this.dominantDocument.setValue(callback.getMod200Object().getMod200().getDominantDocument());
//		this.dominantDocument.setEnabled( 
//				callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0009) 
//			  || callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0010));	
		this.dominantIdentificationNumber.setValue(callback.getMod200Object().getMod200().getDominantIdentificationNumber());
//		this.dominantIdentificationNumber.setEnabled(callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0010));

		this.ultimateDocument.setValue(callback.getMod200Object().getMod200().getUltimateDocument());
//		this.ultimateDocument.setEnabled(callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0081));
//		
//	    this.ultimateDocumentCountry.setEnabled(callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0081));
//		this.ultimateDocumentCountry.setWidth("240px");
		this.ultimateDocumentCountry.setValue(callback.getMod200Object().getMod200().getUltimateDocumentCountry());
//		this.ultimateDocumentCountry.addChangeHandler(new ChangeHandler() {
//			
//			@Override
//			public void onChange(ChangeEvent event) {
//				callback.getMod200Object().getMod200().setUltimateDocumentCountry(Country.safeValueOf(ultimateDocumentCountry.getSelectedValue()));
//			}
//		});
		
	    this.ultimateName.setValue(callback.getMod200Object().getMod200().getUltimateName());
//	    this.ultimateName.setEnabled(callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0081));
	    
//	    this.ultimateCountry.setEnabled(callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0081));
//		this.ultimateCountry.setWidth("240px");
		this.ultimateCountry.setValue(callback.getMod200Object().getMod200().getUltimateCountry());
//		this.ultimateCountry.addChangeHandler(new ChangeHandler() {
//			
//			@Override
//			public void onChange(ChangeEvent event) {
//				callback.getMod200Object().getMod200().setUltimateCountry(Country.safeValueOf(ultimateCountry.getSelectedValue()));
//			}
//		});
	
		Secretary secretary = callback.getMod200Object().getMod200().getSecretary();
		if (secretary != null) {
			this.secretaryDocument.setValue(secretary.getDocument());
			this.secretaryName.setValue(secretary.getName());
			this.irnr.setValue(secretary.getIrnr());
		} else {
			this.secretaryDocument.setValue(null);
			this.secretaryName.setValue(null);
			this.irnr.setValue(null);
		}
		
	}
	
	@Override
	public void populate() {
		Secretary secretary = callback.getMod200Object().getMod200().getSecretary();
		if (secretary == null) {
			secretary = new Secretary();
			callback.getMod200Object().getMod200().setSecretary(secretary);	
		}
		secretary.setDocument(this.secretaryDocument.getValue());
		secretary.setName(this.secretaryName.getValue());
		secretary.setIrnr(this.irnr.getValue());
		
		callback.getMod200Object().getMod200().setFiscalGroup(this.fiscalGroup.getValue());
		callback.getMod200Object().getMod200().setDominantDocument(this.dominantDocument.getValue());
		callback.getMod200Object().getMod200().setDominantIdentificationNumber(dominantIdentificationNumber.getValue());
		
		callback.getMod200Object().getMod200().setUltimateDocument(this.ultimateDocument.getValue());         
	    callback.getMod200Object().getMod200().setUltimateName(this.ultimateName.getValue());
//	    callback.getMod200Object().getMod200().setUltimateCountry(this.ultimateCountry.getValue());
		
//		LinkedList<LegalRepresentative> list1 = new LinkedList<LegalRepresentative>();
//		for (LegalRepresentative lr : dataProvider1.getList()) {
//			list1.add(lr);
//		}
//		callback.getMod200Object().getMod200().setRepresentatives(list1);
		
//		LinkedList<Mod200CompanyAdministrator> list2 = new LinkedList<Mod200CompanyAdministrator>();
//		for (Mod200CompanyAdministrator cp : dataProvider2.getList()) {
//			list2.add(cp);
//		}
//		callback.getMod200Object().getMod200().setAdministrators(list2);
	}
	
//	private void addLegalDocumentColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(8);
//		Column<LegalRepresentative, String> col = new Column<LegalRepresentative, String>(
//				input) {
//			@Override
//			public String getValue(LegalRepresentative lr) {
//				return lr.getDocument();
//			}
//		};
//		
//		col.setFieldUpdater(new FieldUpdater<LegalRepresentative, String>() {
//		    public void update(int index, LegalRepresentative lr, String value) {
//		    	dataProvider1.getList().get(index).setDocument(value);
//		    }
//		});		
//		table1.addColumn(col, AON.MSG.document());
//		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//		table1.setColumnWidth(col, 100, Unit.PX);
//	}
	
//	private void addLegalNameColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(40);
//		Column<LegalRepresentative, String> col = new Column<LegalRepresentative, String>(
//				input) {
//			@Override
//			public String getValue(LegalRepresentative lr) {
//				return lr.getName();
//			}
//		};
//		col.setFieldUpdater(new FieldUpdater<LegalRepresentative, String>() {
//		    public void update(int index, LegalRepresentative lr, String value) {
//		    	dataProvider1.getList().get(index).setName(value);
//		    }
//		});		
//		table1.addColumn(col, AON.MSG.companyName());
//		col.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//	}

//	private void addLegalNotaryColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(25);
//		Column<LegalRepresentative, String> col = new Column<LegalRepresentative, String>(
//				input) {
//			@Override
//			public String getValue(LegalRepresentative lr) {
//				return lr.getNotary();
//			}
//		};
//		col.setFieldUpdater(new FieldUpdater<LegalRepresentative, String>() {
//		    public void update(int index, LegalRepresentative lr, String value) {
//		    	if (!AonStringUtils.isEmpty(value)) {
//		    		if (value.length() > 20) {
//		    			MessageDialog.error("Este dato admite 20 caracteres de longitud");
//		    			value = AonStringUtils.substring(value, 0, 19);
//		    		}
//		    	}
//		    	dataProvider1.getList().get(index).setNotary(value);
//		    }
//		});		
//		table1.addColumn(col, AON.MSG.notary()+"/Otros");
//		col.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//		table1.setColumnWidth(col, 150, Unit.PX);
//	}

//	private void addLegalNotaryDateColumn() {
//		final DateTimeFormat format = DateTimeFormat.getFormat( "dd/MM/yyyy" );
//		SizableTextInputCell input = new SizableTextInputCell(10);
//		Column<LegalRepresentative, String> col = new Column<LegalRepresentative, String>(
//				input) {
//			@Override
//			public String getValue(LegalRepresentative lr) {
//				return (lr.getNotaryDate() == null)?null:
//					format.format(lr.getNotaryDate());
//			}
//		};
//		col.setFieldUpdater(new FieldUpdater<LegalRepresentative, String>() {
//		    public void update(int index, LegalRepresentative lr, String value) {
//		    	Date notaryDate = null;
//		    	if (AonStringUtils.isNotEmpty(value)) {
//		    		try {
//		    			notaryDate = format.parse(value);
//		    		} catch (IllegalArgumentException ex) {
//		    			MessageDialog.error("Formato de fecha incorrecto (dd/MM/yyyy)");
//		    		}
//		    	}
//		    	dataProvider1.getList().get(index).setNotaryDate(notaryDate);
//		    }
//		});		
//		table1.addColumn(col, AON.MSG.notaryDate());
//		col.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//		table1.setColumnWidth(col, 120, Unit.PX);
//	}

//	private void addLegalRemoveColumn() {
//		
//		ButtonCell removeButton = new ButtonCell( new DeleteButtonSafeHtmlTemplates())  {
//			  @Override
//			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
//			    if (data != null) {
//			      sb.append(data);
//			    }
//			  }
//		};
//		Column<LegalRepresentative,String> col = new Column<LegalRepresentative,String>(removeButton) {
//		  public String getValue(LegalRepresentative object) {
//		    return AON.MSG.deleteAction();
//		  }
//		};
//		col.setFieldUpdater(new FieldUpdater<LegalRepresentative, String>() {
//		    public void update(int index, LegalRepresentative lr, String value) {
//		    	ConfirmDialog cd = new ConfirmDialog();
//		    	cd.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {
//					
//					@Override
//					public void onCancel() {}
//					
//					@Override
//					public void onAccept() {
//						callback.getMod200Object().getMod200().getRepresentatives().remove(index);
//			    		dataProvider1 = new ListDataProvider<LegalRepresentative>(callback.getMod200Object().getMod200().getRepresentatives());
//			    		dataProvider1.addDataDisplay(table1);
//			    		table1.redraw();
//					}
//				});
//		    }
//		});		
//		table1.addColumn(col);
//		table1.setColumnWidth(col, 20, Unit.PX);
//		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//	}

//	@UiHandler("newLegalRepresentative")
//	void onNewLegalRepresentative(ClickEvent event) {
//		dataProvider1.getList().add(new LegalRepresentative());
//		table1.redraw();		    		
//	}	

//	private void addAdmDocumentColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(8);
//		Column<Mod200CompanyAdministrator, String> col = new Column<Mod200CompanyAdministrator, String>(
//				input) {
//			@Override
//			public String getValue(Mod200CompanyAdministrator ca) {
//				return ca.getDocument();
//			}
//		};
//		
//		col.setFieldUpdater(new FieldUpdater<Mod200CompanyAdministrator, String>() {
//		    public void update(int index, Mod200CompanyAdministrator ca, String value) {
//		    	dataProvider2.getList().get(index).setDocument(value);
//		    }
//		});		
//		table2.addColumn(col, AON.MSG.document());
//		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//		table2.setColumnWidth(col, 100, Unit.PX);
//	}

//	private void addAdmNameColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(40);
//		Column<Mod200CompanyAdministrator, String> col = new Column<Mod200CompanyAdministrator, String>(
//				input) {
//			@Override
//			public String getValue(Mod200CompanyAdministrator ca) {
//				return ca.getName();
//			}
//		};
//		col.setFieldUpdater(new FieldUpdater<Mod200CompanyAdministrator, String>() {
//		    public void update(int index, Mod200CompanyAdministrator ca, String value) {
//		    	dataProvider2.getList().get(index).setName(value);
//		    }
//		});		
//		table2.addColumn(col, AON.MSG.companyName());
//		col.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//	}

//	private void addAdmRepresentativeColumn() {
//		Column<Mod200CompanyAdministrator, Boolean> col = new Column<Mod200CompanyAdministrator, Boolean>(
//				new TabCheckboxCell()) {
//			@Override
//			public Boolean getValue(Mod200CompanyAdministrator ca) {
//				return ca.isRepresentative();
//			}
//		};
//		col.setFieldUpdater(new FieldUpdater<Mod200CompanyAdministrator, Boolean>() {
//		    public void update(int index, Mod200CompanyAdministrator ca, Boolean value) {
//		    	dataProvider2.getList().get(index).setRepresentative(value);
//		    }
//		});		
//		table2.addColumn(col, "Rpte.");
//		table2.setColumnWidth(col, 20, Unit.PX);
//		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//	}

//	private void addAdmResidenceColumn() {
//		SizableTextInputCell input = new SizableTextInputCell(20);
//		Column<Mod200CompanyAdministrator, String> col = new Column<Mod200CompanyAdministrator, String>(
//				input) {
//			@Override
//			public String getValue(Mod200CompanyAdministrator ca) {
//				return ca.getResidence();
//			}
//		};
//		col.setFieldUpdater(new FieldUpdater<Mod200CompanyAdministrator, String>() {
//		    public void update(int index, Mod200CompanyAdministrator ca, String value) {
//		    	dataProvider2.getList().get(index).setResidence(value);
//		    }
//		});		
//		table2.addColumn(col, AON.MSG.fiscalAddress());
//		table2.setColumnWidth(col, 200, Unit.PX);
//		col.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//	}

//	private void addAdmProvinceColumn() {
//		final LinkedList<String> options = new LinkedList<String>();
//		for (Province prov : Province.values()) {
//			options.add( prov.getName() );
//		}
//		TabSelectionCell provinceCell = new TabSelectionCell(options);
//		Column<Mod200CompanyAdministrator, String> col = new Column<Mod200CompanyAdministrator, String>(
//				provinceCell) {
//			@Override
//			public String getValue(Mod200CompanyAdministrator ca) {
//				return Province.values()[ca.getProvince()].getName();
//			}
//		};
//		col.setFieldUpdater(new FieldUpdater<Mod200CompanyAdministrator, String>() {
//		    public void update(int index, Mod200CompanyAdministrator ca, String value) {
//		    	Province p = null;
//		    	if (AonStringUtils.isNotEmpty(value)) {
//		    		p = Province.values()[options.indexOf(value)]; 
//		    	}
//		    	dataProvider2.getList().get(index).setProvince(p==null?0:p.ordinal());
//		    }
//		});		
//		table2.addColumn(col, AON.MSG.province());
//		table2.setColumnWidth(col, 100, Unit.PX);
//		col.setCellStyleNames(AON.AON_CSS.aonTextLeft());
//	}
	
//	@UiHandler("newAdministrator")
//	void onNewAdministrator(ClickEvent event) {
//		dataProvider2.getList().add(new Mod200CompanyAdministrator());
//		table2.redraw();		    		
//	}
	
//	private void addAdmRemoveColumn() {
//		
//		ButtonCell removeButton = new ButtonCell( new DeleteButtonSafeHtmlTemplates())  {
//			  @Override
//			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
//			    if (data != null) {
//			      sb.append(data);
//			    }
//			  }
//		};
//		Column<Mod200CompanyAdministrator,String> col = new Column<Mod200CompanyAdministrator,String>(removeButton) {
//		  public String getValue(Mod200CompanyAdministrator object) {
//		    return AON.MSG.deleteAction();
//		  }
//		};
//		col.setFieldUpdater(new FieldUpdater<Mod200CompanyAdministrator, String>() {
//		    public void update(int index, Mod200CompanyAdministrator ca, String value) {
//		    	ConfirmDialog cd = new ConfirmDialog();
//		    	cd.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {
//					
//					@Override
//					public void onCancel() {}
//				
//					@Override
//					public void onAccept() {
//						callback.getMod200Object().getMod200().getAdministrators().remove(index);
//			    		dataProvider2 = new ListDataProvider<Mod200CompanyAdministrator>(callback.getMod200Object().getMod200().getAdministrators());
//			    		dataProvider2.addDataDisplay(table2);
//			    		table2.redraw();
//					}
//		    	});
//		    }
//	    });
//		table2.addColumn(col);
//		table2.setColumnWidth(col, 20, Unit.PX);
//		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
//	}

	@Override
	protected void initializeTable() {
	}
	
	private void paint() {
		
//		Mod2002020 mod200 = callback.getMod200Object().getMod200();
		
//		ScrollPanel scroll = new ScrollPanel();
//		FlowPanel basePanel = new FlowPanel();
//		scroll.add(basePanel);
//		setWidget(scroll);
		
		basePanel.clear();
		
		// SECRETARIO DEL CONSEJO DE ADMINISTRACION

		basePanel.add(getTitle(AON.MSG.secretaryData()));
		
		AonDisplayTable tab1 = new AonDisplayTable();
		tab1.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab1.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab1);
		
		secretaryDocument.addValueChangeHandler(event -> {
			callback.markAsDirty();
		});
		
		secretaryName.setVisibleLength(40);
		secretaryName.setMaxLength(25);
		secretaryName.addValueChangeHandler(event -> {
			callback.markAsDirty();
		});
		
		irnr.addValueChangeHandler(event -> {
			callback.markAsDirty();
		});
		
		tab1.addLabelWidgetRow(AON.MSG.document(), secretaryDocument)
		    .addLabelWidgetRow(AON.MSG.name(), secretaryName)
		    .addLabelWidgetRow(AON.MSG.irnrDate(), irnr);
		
		// GRUPO FISCAL (solo habilitados si caracteres 9 o 10 marcados)
		
		basePanel.add(getTitle(AON.MSG.fiscalGroupLabel()));
		
		AonDisplayTable tab2 = new AonDisplayTable();
		tab2.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab2.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab2);
				
		fiscalGroup.setVisibleLength(7);
		fiscalGroup.setMaxLength(7);
		fiscalGroup.setEnabled(callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0009) || 
				               callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0010));
		fiscalGroup.addValueChangeHandler(event -> {
			callback.markAsDirty();
		});
		
		dominantDocument.setEnabled(fiscalGroup.isEnabled());
		dominantDocument.addValueChangeHandler(event -> {
			callback.markAsDirty();
		});
						
		dominantIdentificationNumber.setVisibleLength(9);
		dominantIdentificationNumber.setMaxLength(9);
		dominantIdentificationNumber.setEnabled(callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0010));		
		dominantIdentificationNumber.addValueChangeHandler(event -> {
			callback.markAsDirty();
		});						
		
		tab2.addRow()
			.addCell(new Label(AON.MSG.fiscalGroup()), AON.CSS.aonWidth400())
			.addCell(fiscalGroup);
		tab2.addRow()
			.addCell(new Label(AON.MSG.groupDocument()), AON.CSS.aonWidth400())
			.addCell(dominantDocument);
		tab2.addRow()
			.addCell(new Label(AON.MSG.dominantIdentificationNumber()), AON.CSS.aonWidth400())
			.addCell(dominantIdentificationNumber);		    
		
		// GRUPO MERCANTIL (solo habilitados si caracter 81 marcado)
		
		basePanel.add(getTitle("Grupo mercantil"));
		
		AonDisplayTable tab3 = new AonDisplayTable();
		tab3.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab3.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab3);
		
		ultimateDocument.setEnabled(callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0081));
		ultimateDocument.addValueChangeHandler(event -> {
			callback.markAsDirty();
		});
		
		ultimateDocumentCountry.setWidth("240px");
		ultimateDocumentCountry.setEnabled(callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0081));
		ultimateDocumentCountry.addChangeHandler(new ChangeHandler() {			
			@Override
			public void onChange(ChangeEvent event) {
				callback.getMod200Object().getMod200().setUltimateDocumentCountry(Country.safeValueOf(ultimateDocumentCountry.getSelectedValue()));
				callback.markAsDirty();
			}
		});
		
		ultimateName.setEnabled(callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0081));
		ultimateName.setVisibleLength(40); 
		ultimateName.setMaxLength(40);
		ultimateName.addValueChangeHandler(event -> {
			callback.markAsDirty();
		});
		
		ultimateCountry.setWidth("240px");
		ultimateCountry.setEnabled(callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0081));
		ultimateCountry.addChangeHandler(new ChangeHandler() {			
			@Override
			public void onChange(ChangeEvent event) {
				callback.getMod200Object().getMod200().setUltimateCountry(Country.safeValueOf(ultimateCountry.getSelectedValue()));
				callback.markAsDirty();
			}
		});
		
		tab3.addRow()
			.addCell(new Label(AON.MSG.ultimateDocument()), AON.CSS.aonWidth400())
			.addCell(ultimateDocument);
		tab3.addRow()
			.addCell(new Label(AON.MSG.ultimateDocumentCountry()), AON.CSS.aonWidth400())
			.addCell(ultimateDocumentCountry);
		tab3.addRow()
			.addCell(new Label(AON.MSG.ultimateName()), AON.CSS.aonWidth400())
			.addCell(ultimateName);
		tab3.addRow()
			.addCell(new Label(AON.MSG.ultimateCountry()), AON.CSS.aonWidth400())
			.addCell(ultimateCountry);
		
		// REPRESENTANTES LEGALES DE LA ENTIDAD
		
		basePanel.add(getTitle(AON.MSG.legalRepresentativeData()));
		
		AonDisplayTable tab4 = new AonDisplayTable();
		tab4.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab4.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab4);
		
		tab4.addRow()
			.addCell( new Label(AON.MSG.document()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth100())
			.addCell( new Label(AON.MSG.companyName()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth300())
			.addCell( new Label(AON.MSG.notary()+"/Otros"),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth200())
			.addCell( new Label(AON.MSG.registrationDate()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth100())
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getRepresentatives().size(); i++) {
			final int idx = i;
			
			AonDocumentTextBox document = new AonDocumentTextBox();			
			document.setValue(callback.getMod200Object().getMod200().getRepresentatives().get(idx).getDocument());
			document.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getRepresentatives().get(idx).setDocument(document.getValue());
				callback.markAsDirty();
			});
			
			AonTextBox name = new AonTextBox();
			name.setMaxLength(45);
			name.setVisibleLength(45);			
			name.setValue(callback.getMod200Object().getMod200().getRepresentatives().get(idx).getName());
			name.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getRepresentatives().get(idx).setName(name.getValue());
				callback.markAsDirty();
			});
			
			AonTextBox notary = new AonTextBox();
			notary.setMaxLength(20);
			notary.setValue(callback.getMod200Object().getMod200().getRepresentatives().get(idx).getNotary());
			notary.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getRepresentatives().get(idx).setNotary(notary.getValue());
				callback.markAsDirty();
			});
			
			AonDateBox notaryDate = new AonDateBox();
			notaryDate.setValue(callback.getMod200Object().getMod200().getRepresentatives().get(idx).getNotaryDate());
			notaryDate.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getRepresentatives().get(idx).setNotaryDate(notaryDate.getValue());
				callback.markAsDirty();
			});
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getRepresentatives().remove(idx);
				paint();
				callback.markAsDirty();
			});

			tab4.addRow()
				.addCell( document )
				.addCell( name )
				.addCell( notary )
				.addCell( notaryDate )				
				.addCell( deleteButton );
		}
		
		// Botón añadir representante legal
		AonTableButton addButton1 = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButton1.addStyleName(AON.CSS.aonMarginTop());
		addButton1.addStyleName(AON.CSS.aonMarginLeft());
		addButton1.addClickHandler(event -> {
			// No puede haber mas de 3 representantes legales
			if (callback.getMod200Object().getMod200().getRepresentatives().size() == 3) {
				AonMessageDialog.warning("No se pueden poner en el modelo mas de tres representantes legales.");
			} else {		
				callback.getMod200Object().getMod200().getRepresentatives().add(new LegalRepresentative());
				paint();
			}
		});
		basePanel.add(addButton1);
		
		// RELACION DE ADMINISTRADORES
		
		basePanel.add(getTitle(AON.MSG.administratorList()));
		
		AonDisplayTable tab5 = new AonDisplayTable();
		tab5.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab5.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab5);
		
		tab5.addRow()
			.addCell( new Label(AON.MSG.document()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth100())
			.addCell( new Label("Rpte."),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth40())			
			.addCell( new Label(AON.MSG.companyName()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth300())
			.addCell( new Label(AON.MSG.fiscalAddress()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth200())
			.addCell( new Label(AON.MSG.province()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth200())
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getAdministrators().size(); i++) {
			final int idx = i;
			
			AonDocumentTextBox document = new AonDocumentTextBox();			
			document.setValue(callback.getMod200Object().getMod200().getAdministrators().get(idx).getDocument());
			document.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getAdministrators().get(idx).setDocument(document.getValue());
				callback.markAsDirty();
			});
			
			CheckBox rep = new CheckBox();
			rep.setValue(callback.getMod200Object().getMod200().getAdministrators().get(idx).isRepresentative());
			rep.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					callback.getMod200Object().getMod200().getAdministrators().get(idx).setRepresentative(rep.getValue());
					callback.markAsDirty();
				}
			});
//			rep.addValueChangeHandler(event -> {
//				callback.getMod200Object().getMod200().getAdministrators().get(idx).setRepresentative(rep.getValue());
//				callback.markAsDirty();
//			});
			
			AonTextBox name = new AonTextBox();
			name.setMaxLength(40);  
			name.setVisibleLength(45);			
			name.setValue(callback.getMod200Object().getMod200().getAdministrators().get(idx).getName());
			name.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getAdministrators().get(idx).setName(name.getValue());
				callback.markAsDirty();
			});
			
			AonTextBox address = new AonTextBox();
			address.setMaxLength(17); // FALTA - LONGITUD  (solo 17???)
			address.setValue(callback.getMod200Object().getMod200().getAdministrators().get(idx).getResidence());
			address.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getAdministrators().get(idx).setResidence(address.getValue());
				callback.markAsDirty();
			});
			
			ProvinceListBox province = new ProvinceListBox();
			province.setValue(Province.safeValueOf(callback.getMod200Object().getMod200().getAdministrators().get(idx).getProvince()));
			province.addChangeHandler(new ChangeHandler() {			
				@Override
				public void onChange(ChangeEvent event) {
					callback.getMod200Object().getMod200().getAdministrators().get(idx).setProvince(province.getSelectedIndex());
					callback.markAsDirty();
				}
			});

//			final LinkedList<String> options = new LinkedList<String>();
//			for (Province prov : Province.values()) {
//				options.add( prov.getName() );
//			}
//			TabSelectionCell provinceCell = new TabSelectionCell(options);
//			Column<Mod200CompanyAdministrator, String> col = new Column<Mod200CompanyAdministrator, String>(
//					provinceCell) {
//				@Override
//				public String getValue(Mod200CompanyAdministrator ca) {
//					return Province.values()[ca.getProvince()].getName();
//				}
//			};
//			col.setFieldUpdater(new FieldUpdater<Mod200CompanyAdministrator, String>() {
//			    public void update(int index, Mod200CompanyAdministrator ca, String value) {
//			    	Province p = null;
//			    	if (AonStringUtils.isNotEmpty(value)) {
//			    		p = Province.values()[options.indexOf(value)]; 
//			    	}
//			    	dataProvider2.getList().get(index).setProvince(p==null?0:p.ordinal());
//			    }
//			});		
			
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getAdministrators().remove(idx);
				paint();
				callback.markAsDirty();
			});

			tab5.addRow()
				.addCell(document)
				.addCell(rep)
				.addCell(name)
				.addCell(address)
				.addCell(province)				
				.addCell(deleteButton);
		}
		
		// Botón añadir administrador
		AonTableButton addButton2 = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButton2.addStyleName(AON.CSS.aonMarginTop());
		addButton2.addStyleName(AON.CSS.aonMarginLeft());
		addButton2.addClickHandler(event -> {
			callback.getMod200Object().getMod200().getAdministrators().add(new Mod200CompanyAdministrator());
			paint();
		});
		basePanel.add(addButton2);
		
		
		
	}
	
}
