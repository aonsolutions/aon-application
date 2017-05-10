package com.esferalia.aon.gwt.fiscal.client.mod200.e2016;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.MessageDialog;
import com.esferalia.aon.gwt.common.client.widget.cell.SizableTextInputCell;
import com.esferalia.aon.gwt.common.client.widget.cell.TabCheckboxCell;
import com.esferalia.aon.gwt.common.client.widget.cell.TabSelectionCell;
import com.esferalia.aon.gwt.fiscal.client.mod200.Model200Table;
import com.esferalia.aon.occam.api.model.CompanyAdministrator;
import com.esferalia.aon.occam.api.model.fiscal.LegalRepresentative;
import com.esferalia.aon.occam.api.model.fiscal.Secretary;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class Page01 extends PageAbs {

	interface Page1Binder extends UiBinder<Widget, Page01> {
	}
	private static final Page1Binder page1Binder = GWT
			.create(Page1Binder.class);

	private ListDataProvider<LegalRepresentative> dataProvider1;
	@UiField(provided = true)
	CellTable<LegalRepresentative> table1;

	private ListDataProvider<CompanyAdministrator> dataProvider2;
	@UiField(provided = true)
	CellTable<CompanyAdministrator> table2;

	@UiField
	DocumentTextBox secretaryDocument;
	@UiField
	TextBox secretaryName;
	@UiField
	DateBoxEx irnr;

	@UiField
	Panel fiscalGroupPanel;
	@UiField
	TextBox fiscalGroup;
	@UiField
	DocumentTextBox dominantDocument;
	@UiField
	TextBox dominantIdentificationNumber;
	
	@UiField
	Button newLegalRepresentative;
	@UiField
	Button newAdministrator;

	public Page01() {

		table1 = new CellTable<LegalRepresentative>(50, Model200Table.TABLE_STYLE);
		table1.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		table1.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		table1.setEmptyTableWidget(new HTML(AON.MSG.noData()));
		dataProvider1 = new ListDataProvider<LegalRepresentative>();
		dataProvider1.addDataDisplay(table1);
		addLegalDocumentColumn();
		addLegalNameColumn();
		addLegalNotaryColumn();
		addLegalNotaryDateColumn();
		addLegalRemoveColumn();

		table2 = new CellTable<CompanyAdministrator>(50, Model200Table.TABLE_STYLE);
		table2.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		table2.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
		table2.setEmptyTableWidget(new HTML(AON.MSG.noData()));
		dataProvider2 = new ListDataProvider<CompanyAdministrator>();
		dataProvider2.addDataDisplay(table2);
		addAdmDocumentColumn();
		addAdmRepresentativeColumn();
		addAdmNameColumn();
		addAdmResidenceColumn();
		addAdmProvinceColumn();
		addAdmRemoveColumn();
		
		Widget ui = page1Binder.createAndBindUi(this);
		initWidget(ui);
	}

	public void dump(Mod2002016Object mod200Object) {
		this.mod200Object = mod200Object;
		dataProvider1 = new ListDataProvider<LegalRepresentative>(this.mod200Object.getMod200().getRepresentatives());
		dataProvider1.addDataDisplay(table1);
		table1.redraw();

		dataProvider2 = new ListDataProvider<CompanyAdministrator>(this.mod200Object.getMod200().getAdministrators());
		dataProvider2.addDataDisplay(table2);
		table2.redraw();
		
		
		this.fiscalGroup.setValue( this.mod200Object.getMod200().getFiscalGroup());
		this.fiscalGroup.setEnabled( 
				 this.mod200Object.getMod200().isChecked(Mod2002016Key.C0009) 
			  || this.mod200Object.getMod200().isChecked(Mod2002016Key.C0010));	
		this.dominantDocument.setValue(this.mod200Object.getMod200().getDominantDocument());
		this.dominantDocument.setEnabled( 
				 this.mod200Object.getMod200().isChecked(Mod2002016Key.C0009) 
			  || this.mod200Object.getMod200().isChecked(Mod2002016Key.C0010));	
		this.dominantIdentificationNumber.setValue(this.mod200Object.getMod200().getDominantIdentificationNumber());
		this.dominantIdentificationNumber.setEnabled(this.mod200Object.getMod200().isChecked(Mod2002016Key.C0010));	

		Secretary secretary = this.mod200Object.getMod200().getSecretary();
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
	public void populate(Mod2002016Object obj) {
		Secretary secretary = this.mod200Object.getMod200().getSecretary();
		if (secretary == null) {
			secretary = new Secretary();
			this.mod200Object.getMod200().setSecretary(secretary);	
		}
		secretary.setDocument(this.secretaryDocument.getValue());
		secretary.setName(this.secretaryName.getValue());
		secretary.setIrnr(this.irnr.getValue());
		
		this.mod200Object.getMod200().setFiscalGroup(this.fiscalGroup.getValue());
		this.mod200Object.getMod200().setDominantDocument(this.dominantDocument.getValue());
		this.mod200Object.getMod200().setDominantIdentificationNumber(dominantIdentificationNumber.getValue());
		
		List<LegalRepresentative> list1 = new LinkedList<LegalRepresentative>();
		for (LegalRepresentative lr : dataProvider1.getList()) {
			list1.add(lr);
		}
		this.mod200Object.getMod200().setRepresentatives(list1);
		
		List<CompanyAdministrator> list2 = new LinkedList<CompanyAdministrator>();
		for (CompanyAdministrator cp : dataProvider2.getList()) {
			list2.add(cp);
		}
		this.mod200Object.getMod200().setAdministrators(list2);
	}
	
	private void addLegalDocumentColumn() {
		SizableTextInputCell input = new SizableTextInputCell(8);
		Column<LegalRepresentative, String> col = new Column<LegalRepresentative, String>(
				input) {
			@Override
			public String getValue(LegalRepresentative lr) {
				return lr.getDocument();
			}
		};
		
		col.setFieldUpdater(new FieldUpdater<LegalRepresentative, String>() {
		    public void update(int index, LegalRepresentative lr, String value) {
		    	dataProvider1.getList().get(index).setDocument(value);
		    }
		});		
		table1.addColumn(col, AON.MSG.document());
		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
		table1.setColumnWidth(col, 100, Unit.PX);
	}
	
	private void addLegalNameColumn() {
		SizableTextInputCell input = new SizableTextInputCell(40);
		Column<LegalRepresentative, String> col = new Column<LegalRepresentative, String>(
				input) {
			@Override
			public String getValue(LegalRepresentative lr) {
				return lr.getName();
			}
		};
		col.setFieldUpdater(new FieldUpdater<LegalRepresentative, String>() {
		    public void update(int index, LegalRepresentative lr, String value) {
		    	dataProvider1.getList().get(index).setName(value);
		    }
		});		
		table1.addColumn(col, AON.MSG.companyName());
		col.setCellStyleNames(AON.AON_CSS.aonTextLeft());
	}

	private void addLegalNotaryColumn() {
		SizableTextInputCell input = new SizableTextInputCell(25);
		Column<LegalRepresentative, String> col = new Column<LegalRepresentative, String>(
				input) {
			@Override
			public String getValue(LegalRepresentative lr) {
				return lr.getNotary();
			}
		};
		col.setFieldUpdater(new FieldUpdater<LegalRepresentative, String>() {
		    public void update(int index, LegalRepresentative lr, String value) {
		    	if (!AonStringUtils.isEmpty(value)) {
		    		if (value.length() > 20) {
		    			MessageDialog.error("Este dato admite 20 caracteres de longitud");
		    			value = AonStringUtils.substring(value, 0, 19);
		    		}
		    	}
		    	dataProvider1.getList().get(index).setNotary(value);
		    }
		});		
		table1.addColumn(col, AON.MSG.notary());
		col.setCellStyleNames(AON.AON_CSS.aonTextLeft());
		table1.setColumnWidth(col, 150, Unit.PX);
	}

	private void addLegalNotaryDateColumn() {
		final DateTimeFormat format = DateTimeFormat.getFormat( "dd/MM/yyyy" );
		SizableTextInputCell input = new SizableTextInputCell(10);
		Column<LegalRepresentative, String> col = new Column<LegalRepresentative, String>(
				input) {
			@Override
			public String getValue(LegalRepresentative lr) {
				return (lr.getNotaryDate() == null)?null:
					format.format(lr.getNotaryDate());
			}
		};
		col.setFieldUpdater(new FieldUpdater<LegalRepresentative, String>() {
		    public void update(int index, LegalRepresentative lr, String value) {
		    	Date notaryDate = null;
		    	if (AonStringUtils.isNotEmpty(value)) {
		    		try {
		    			notaryDate = format.parse(value);
		    		} catch (IllegalArgumentException ex) {
		    			MessageDialog.error("Formato de fecha incorrecto (dd/MM/yyyy)");
		    		}
		    	}
		    	dataProvider1.getList().get(index).setNotaryDate(notaryDate);
		    }
		});		
		table1.addColumn(col, AON.MSG.notaryDate());
		col.setCellStyleNames(AON.AON_CSS.aonTextLeft());
		table1.setColumnWidth(col, 120, Unit.PX);
	}

	private void addLegalRemoveColumn() {
		
		ButtonCell removeButton = new ButtonCell( new DeleteButtonSafeHtmlTemplates())  {
			  @Override
			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
			    if (data != null) {
			      sb.append(data);
			    }
			  }
		};
		Column<LegalRepresentative,String> col = new Column<LegalRepresentative,String>(removeButton) {
		  public String getValue(LegalRepresentative object) {
		    return AON.MSG.deleteAction();
		  }
		};
		col.setFieldUpdater(new FieldUpdater<LegalRepresentative, String>() {
		    public void update(int index, LegalRepresentative lr, String value) {
		    	ConfirmDialog cd = new ConfirmDialog();
		    	cd.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {
					
					@Override
					public void onCancel() {}
					
					@Override
					public void onAccept() {
			    		mod200Object.getMod200().getRepresentatives().remove(index);
			    		dataProvider1 = new ListDataProvider<LegalRepresentative>(mod200Object.getMod200().getRepresentatives());
			    		dataProvider1.addDataDisplay(table1);
			    		table1.redraw();
					}
				});
		    }
		});		
		table1.addColumn(col);
		table1.setColumnWidth(col, 20, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
	}

	@UiHandler("newLegalRepresentative")
	void onNewLegalRepresentative(ClickEvent event) {
		dataProvider1.getList().add(new LegalRepresentative());
		table1.redraw();		    		
	}
	

	private void addAdmDocumentColumn() {
		SizableTextInputCell input = new SizableTextInputCell(8);
		Column<CompanyAdministrator, String> col = new Column<CompanyAdministrator, String>(
				input) {
			@Override
			public String getValue(CompanyAdministrator ca) {
				return ca.getDocument();
			}
		};
		
		col.setFieldUpdater(new FieldUpdater<CompanyAdministrator, String>() {
		    public void update(int index, CompanyAdministrator ca, String value) {
		    	dataProvider2.getList().get(index).setDocument(value);
		    }
		});		
		table2.addColumn(col, AON.MSG.document());
		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
		table2.setColumnWidth(col, 100, Unit.PX);
	}

	private void addAdmNameColumn() {
		SizableTextInputCell input = new SizableTextInputCell(40);
		Column<CompanyAdministrator, String> col = new Column<CompanyAdministrator, String>(
				input) {
			@Override
			public String getValue(CompanyAdministrator ca) {
				return ca.getName();
			}
		};
		col.setFieldUpdater(new FieldUpdater<CompanyAdministrator, String>() {
		    public void update(int index, CompanyAdministrator ca, String value) {
		    	dataProvider2.getList().get(index).setName(value);
		    }
		});		
		table2.addColumn(col, AON.MSG.companyName());
		col.setCellStyleNames(AON.AON_CSS.aonTextLeft());
	}

	private void addAdmRepresentativeColumn() {
		Column<CompanyAdministrator, Boolean> col = new Column<CompanyAdministrator, Boolean>(
				new TabCheckboxCell()) {
			@Override
			public Boolean getValue(CompanyAdministrator ca) {
				return ca.isRepresentative();
			}
		};
		col.setFieldUpdater(new FieldUpdater<CompanyAdministrator, Boolean>() {
		    public void update(int index, CompanyAdministrator ca, Boolean value) {
		    	dataProvider2.getList().get(index).setRepresentative(value);
		    }
		});		
		table2.addColumn(col, "Rpte.");
		table2.setColumnWidth(col, 20, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
	}

	private void addAdmResidenceColumn() {
		SizableTextInputCell input = new SizableTextInputCell(20);
		Column<CompanyAdministrator, String> col = new Column<CompanyAdministrator, String>(
				input) {
			@Override
			public String getValue(CompanyAdministrator ca) {
				return ca.getResidence();
			}
		};
		col.setFieldUpdater(new FieldUpdater<CompanyAdministrator, String>() {
		    public void update(int index, CompanyAdministrator ca, String value) {
		    	dataProvider2.getList().get(index).setResidence(value);
		    }
		});		
		table2.addColumn(col, AON.MSG.fiscalAddress());
		table2.setColumnWidth(col, 200, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextLeft());
	}

	private void addAdmProvinceColumn() {
		final List<String> options = new LinkedList<String>();
		for (Province prov : Province.values()) {
			options.add( prov.getName() );
		}
		TabSelectionCell provinceCell = new TabSelectionCell(options);
		Column<CompanyAdministrator, String> col = new Column<CompanyAdministrator, String>(
				provinceCell) {
			@Override
			public String getValue(CompanyAdministrator ca) {
				return Province.values()[ca.getProvince()].getName();
			}
		};
		col.setFieldUpdater(new FieldUpdater<CompanyAdministrator, String>() {
		    public void update(int index, CompanyAdministrator ca, String value) {
		    	Province p = null;
		    	if (AonStringUtils.isNotEmpty(value)) {
		    		p = Province.values()[options.indexOf(value)]; 
		    	}
		    	dataProvider2.getList().get(index).setProvince(p==null?0:p.ordinal());
		    }
		});		
		table2.addColumn(col, AON.MSG.province());
		table2.setColumnWidth(col, 100, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextLeft());
	}
	
	@UiHandler("newAdministrator")
	void onNewAdministrator(ClickEvent event) {
		dataProvider2.getList().add(new CompanyAdministrator());
		table2.redraw();		    		
	}
	
	private void addAdmRemoveColumn() {
		
		ButtonCell removeButton = new ButtonCell( new DeleteButtonSafeHtmlTemplates())  {
			  @Override
			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
			    if (data != null) {
			      sb.append(data);
			    }
			  }
		};
		Column<CompanyAdministrator,String> col = new Column<CompanyAdministrator,String>(removeButton) {
		  public String getValue(CompanyAdministrator object) {
		    return AON.MSG.deleteAction();
		  }
		};
		col.setFieldUpdater(new FieldUpdater<CompanyAdministrator, String>() {
		    public void update(int index, CompanyAdministrator ca, String value) {
		    	ConfirmDialog cd = new ConfirmDialog();
		    	cd.confirm(AON.MSG.confirmDeleteAction(), new ConfirmDialogCallback() {
					
					@Override
					public void onCancel() {}
				
					@Override
					public void onAccept() {
			    		mod200Object.getMod200().getAdministrators().remove(index);
			    		dataProvider2 = new ListDataProvider<CompanyAdministrator>(mod200Object.getMod200().getAdministrators());
			    		dataProvider2.addDataDisplay(table2);
			    		table2.redraw();
					}
		    	});
		    }
	    });
		table2.addColumn(col);
		table2.setColumnWidth(col, 20, Unit.PX);
		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
	}

	@Override
	protected void initializeTable() {
	}
	
}
