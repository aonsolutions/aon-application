package com.esferalia.aon.gwt.fiscal.client.mod200;

import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.MSG;
import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.RESOURCES;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.widget.SizableTextInputCell;
import com.esferalia.aon.gwt.common.client.widget.TabCheckboxCell;
import com.esferalia.aon.gwt.common.client.widget.TabSelectionCell;
import com.esferalia.aon.gwt.common.shared.CommonEnum.Province;
import com.esferalia.aon.gwt.common.shared.CompanyAdministrator;
import com.esferalia.aon.gwt.fiscal.client.mod200.Model200.DeleteButtonSafeHtmlTemplates;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

public class Page01 extends PageAbs {

	interface Page1Binder extends UiBinder<Widget, Page01> {
	}
	private static final Page1Binder page1Binder = GWT
			.create(Page1Binder.class);

	private Mod200Object mod200Object;
	private ListDataProvider<CompanyAdministrator> dataProvider;

	@UiField(provided = true)
	CellTable<CompanyAdministrator> table1;
	
	@UiField
	Button newAdministrator;

	public Page01() {

		CellTable.Resources tableStyle = GWT.create(Mod200CellTable.class);
		table1 = new CellTable<CompanyAdministrator>(50,tableStyle);
		
		table1.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		table1.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

		table1.setEmptyTableWidget(new HTML(MSG.noData()));
		dataProvider = new ListDataProvider<CompanyAdministrator>();
		dataProvider.addDataDisplay(table1);

		addDocumentColumn();
		addRepresentativeColumn();
		addDescriptionColumn();
		addResidenceColumn();
		addProvinceColumn();
		addRemoveColumn();
		
		Widget ui = page1Binder.createAndBindUi(this);
		initWidget(ui);
	}

	public void dump(Mod200Object mod200Object) {
		this.mod200Object = mod200Object;
		dataProvider = new ListDataProvider<CompanyAdministrator>(this.mod200Object.getMod200().getAdministrators());
		dataProvider.addDataDisplay(table1);
		table1.redraw();
	}

	private void addDocumentColumn() {
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
		    	dataProvider.getList().get(index).setDocument(ca.getDocument());
		    }
		});		
		table1.addColumn(col, MSG.document());
		col.setCellStyleNames(RESOURCES.css().aonTextCenter());
		table1.setColumnWidth(col, 100, Unit.PX);
	}

	private void addDescriptionColumn() {
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
		    	dataProvider.getList().get(index).setName(ca.getName());
		    }
		});		
		table1.addColumn(col, MSG.companyName());
		table1.setColumnWidth(col, "auto");
		col.setCellStyleNames(RESOURCES.css().aonTextLeft());
	}

	private void addRepresentativeColumn() {
		Column<CompanyAdministrator, Boolean> col = new Column<CompanyAdministrator, Boolean>(
				new TabCheckboxCell()) {
			@Override
			public Boolean getValue(CompanyAdministrator ca) {
				return ca.isRepresentative();
			}
		};
		col.setFieldUpdater(new FieldUpdater<CompanyAdministrator, Boolean>() {
		    public void update(int index, CompanyAdministrator ca, Boolean value) {
		    	dataProvider.getList().get(index).setRepresentative(ca.isRepresentative());
		    }
		});		
		table1.addColumn(col, "Rpte.");
		table1.setColumnWidth(col, 20, Unit.PX);
		col.setCellStyleNames(RESOURCES.css().aonTextCenter());
	}

	private void addResidenceColumn() {
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
		    	dataProvider.getList().get(index).setResidence(ca.getResidence());
		    }
		});		
		table1.addColumn(col, MSG.fiscalAddress());
		table1.setColumnWidth(col, 200, Unit.PX);
		col.setCellStyleNames(RESOURCES.css().aonTextLeft());
	}

	private void addProvinceColumn() {
		List<String> options = new LinkedList<String>();
		for (Province prov : Province.values()) {
			options.add( MSG.provinceName(prov) );
		}
		TabSelectionCell provinceCell = new TabSelectionCell(options);
		Column<CompanyAdministrator, String> col = new Column<CompanyAdministrator, String>(
				provinceCell) {
			@Override
			public String getValue(CompanyAdministrator ca) {
				return MSG.provinceName( Province.values()[ca.getProvince()] );
			}
		};
		col.setFieldUpdater(new FieldUpdater<CompanyAdministrator, String>() {
		    public void update(int index, CompanyAdministrator ca, String value) {
		    	dataProvider.getList().get(index).setProvince(ca.getProvince());
		    }
		});		
		table1.addColumn(col, MSG.province());
		table1.setColumnWidth(col, 100, Unit.PX);
		col.setCellStyleNames(RESOURCES.css().aonTextLeft());
	}
	
	@UiHandler("newAdministrator")
	void onNewAdministrator(ClickEvent event) {
		dataProvider.getList().add(new CompanyAdministrator());
		table1.redraw();		    		
	}
	
	private void addRemoveColumn() {
		
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
		    return MSG.deleteAction();
		  }
		};
		col.setFieldUpdater(new FieldUpdater<CompanyAdministrator, String>() {
		    public void update(int index, CompanyAdministrator ca, String value) {
		    	if (Window.confirm(MSG.confirmDeleteAction())) {
		    		dataProvider.getList().remove(index);
		    		table1.redraw();
		    	}
		    }
		});		
		table1.addColumn(col);
		table1.setColumnWidth(col, 20, Unit.PX);
		col.setCellStyleNames(RESOURCES.css().aonTextCenter());
	}

	@Override
	protected void initializeTable() {
	}
	
}

/*
*/