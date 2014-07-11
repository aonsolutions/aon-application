package com.esferalia.aon.gwt.fiscal.client.mod200;

import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.MSG;
import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.RESOURCES;

import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.gwt.common.client.widget.SizableTextInputCell;
import com.esferalia.aon.gwt.common.client.widget.TabCheckboxCell;
import com.esferalia.aon.gwt.common.client.widget.TabSelectionCell;
import com.esferalia.aon.gwt.common.shared.CommonEnum.Country;
import com.esferalia.aon.gwt.common.shared.CommonEnum.Province;
import com.esferalia.aon.gwt.common.shared.CompanyAdministrator;
import com.esferalia.aon.gwt.common.shared.CompanyParticipation;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class Page02 extends PageAbs {

	interface Page1Binder extends UiBinder<Widget, Page02> {
	}

	private static final Page1Binder page1Binder = GWT
			.create(Page1Binder.class);

	private Mod200Object mod200Object;
	private ListDataProvider<CompanyParticipation> dataProviderIn;
	private ListDataProvider<CompanyParticipation> dataProviderOut;
	private SingleSelectionModel<CompanyParticipation> modelOut;
	
	@UiField
	ParticipationPanel participationPanel;
	
	@UiField(provided = true)
	CellTable<CompanyParticipation> tableIn;

	@UiField(provided = true)
	CellTable<CompanyParticipation> tableOut;
	
	@UiField
	Button newParticipationOut;
	@UiField
	Button newParticipationIn;

	public Page02() {
		CellTable.Resources aonTableStyle = GWT.create(AonCellTable.class);
		tableIn = new CellTable<CompanyParticipation>(25,aonTableStyle);
		
		tableIn.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		tableIn.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

		tableIn.setEmptyTableWidget(new HTML(MSG.noData()));
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
		tableOut.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);
		modelOut = new SingleSelectionModel<CompanyParticipation>();
		modelOut.addSelectionChangeHandler(new SelectionChangeEvent.Handler(){
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				participationPanel.dump(modelOut.getSelectedObject());
				participationPanel.center();
				participationPanel.show();
			}
		});
		tableOut.setSelectionModel(modelOut);		
		
		tableOut.setEmptyTableWidget(new HTML(MSG.noData()));
		dataProviderOut = new ListDataProvider<CompanyParticipation>();
		dataProviderOut.addDataDisplay(tableOut);

		addOutDocumentColumn();
		addOutDescriptionColumn();
		addOutPercentColumn();
		addOutNominalValueColumn();
		addOutRemoveColumn();
		
		Widget ui = page1Binder.createAndBindUi(this);
		initWidget(ui);
		participationPanel.setTable( tableOut );		
	}

	public void dump(Mod200Object mod200Object) {
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
		tableIn.addColumn(documentColumn, MSG.document());
		documentColumn.setCellStyleNames(RESOURCES.css().aonTextCenter());
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
		tableIn.addColumn(descriptionColumn, MSG.companyName());
		descriptionColumn.setCellStyleNames(RESOURCES.css().aonTextLeft());
	}

	private void addInRepresentativeColumn() {
		Column<CompanyParticipation, Boolean> representativeColumn = new Column<CompanyParticipation, Boolean>(
				new TabCheckboxCell()) {
			@Override
			public Boolean getValue(CompanyParticipation ca) {
				return ca.isRepresentative();
			}
		};
		tableIn.addColumn(representativeColumn, "Rpte.");
		tableIn.setColumnWidth(representativeColumn, 30, Unit.PX);
		representativeColumn.setCellStyleNames(RESOURCES.css().aonTextCenter());
	}

	private void addInProvinceColumn() {
		List<String> options = new LinkedList<String>();
		for (Province prov : Province.values()) {
			options.add( MSG.provinceName(prov) );
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
				String name = null; 
				if (idx < Province.values().length) {
					name = MSG.provinceName( Province.values()[idx] ); 
				} else {
					name = Country.values()[idx].getName();
				}
				return name;
			}
		};
		tableIn.addColumn(provinceColumn, MSG.province());
		tableIn.setColumnWidth(provinceColumn, 150, Unit.PX);
		provinceColumn.setCellStyleNames(RESOURCES.css().aonTextLeft());
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
		tableIn.addColumn(percentColumn, "%");
		tableIn.setColumnWidth(percentColumn, 50, Unit.PX);
		percentColumn.setCellStyleNames(RESOURCES.css().aonTextLeft());
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
		tableIn.addColumn(nominalValueColumn, MSG.nominalValue());
		tableIn.setColumnWidth(nominalValueColumn, 120, Unit.PX);
		nominalValueColumn.setCellStyleNames(RESOURCES.css().aonTextLeft());
	}

	private void addInRemoveColumn() {
		ButtonCell removeButton = new ButtonCell( new Model200.DeleteButtonSafeHtmlTemplates())  {
			  @Override
			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
			    if (data != null) {
			      sb.append(data);
			    }
			  }
		};
		Column<CompanyParticipation,String> col = new Column<CompanyParticipation,String>(removeButton) {
		  public String getValue(CompanyParticipation object) {
		    return MSG.deleteAction();
		  }
		};
		col.setFieldUpdater(new FieldUpdater<CompanyParticipation, String>() {
		    public void update(int index, CompanyParticipation ca, String value) {
		    	if (Window.confirm(MSG.confirmDeleteAction())) {
		    		dataProviderIn.getList().remove(index);
		    		tableIn.redraw();
		    	}
		    }
		});		
		tableIn.addColumn(col);
		tableIn.setColumnWidth(col, 20, Unit.PX);
		col.setCellStyleNames(RESOURCES.css().aonTextCenter());
	}

	@UiHandler("newParticipationIn")
	void onNewParticipationIn(ClickEvent event) {
		dataProviderIn.getList().add(new CompanyParticipation());
		tableIn.redraw();		    		
	}
	
	private void addOutDocumentColumn() {
		Column<CompanyParticipation, String> documentColumn = new Column<CompanyParticipation, String>(
				new TextCell()) {
			@Override
			public String getValue(CompanyParticipation object) {
				return object.getDocument();
			}
		};
		tableOut.addColumn(documentColumn, MSG.document());
		documentColumn.setCellStyleNames(RESOURCES.css().aonTextCenter());
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
		tableOut.addColumn(descriptionColumn, MSG.companyName());
		descriptionColumn.setCellStyleNames(RESOURCES.css().aonTextLeft());
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
		percentColumn.setCellStyleNames(RESOURCES.css().aonTextLeft());
	}

	private void addOutNominalValueColumn() {
		Column<CompanyParticipation, String> nominalValueColumn = new Column<CompanyParticipation, String>(
				new TextCell()) {
			@Override
			public String getValue(CompanyParticipation ca) {
				return Double.toString( ca.getNominalValue() );
			}
		};
		tableOut.addColumn(nominalValueColumn, MSG.nominalValue());
		tableOut.setColumnWidth(nominalValueColumn, 80, Unit.PX);
		nominalValueColumn.setCellStyleNames(RESOURCES.css().aonTextLeft());
	}

	private void addOutRemoveColumn() {
		ButtonCell removeButton = new ButtonCell( new Model200.DeleteButtonSafeHtmlTemplates())  {
			  @Override
			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
			    if (data != null) {
			      sb.append(data);
			    }
			  }
		};
		Column<CompanyParticipation,String> col = new Column<CompanyParticipation,String>(removeButton) {
		  public String getValue(CompanyParticipation object) {
		    return MSG.deleteAction();
		  }
		};
		col.setFieldUpdater(new FieldUpdater<CompanyParticipation, String>() {
		    public void update(int index, CompanyParticipation ca, String value) {
		    	if (Window.confirm(MSG.confirmDeleteAction())) {
		    		dataProviderOut.getList().remove(index);
		    		tableOut.redraw();
		    	}
		    }
		});		
		tableOut.addColumn(col);
		tableOut.setColumnWidth(col, 20, Unit.PX);
		col.setCellStyleNames(RESOURCES.css().aonTextCenter());
	}

	@UiHandler("newParticipationOut")
	void onNewParticipationOut(ClickEvent event) {
		CompanyParticipation cp = new CompanyParticipation();
		dataProviderOut.getList().add(cp);
		participationPanel.dump(cp);
		participationPanel.center();
		participationPanel.show();
	}
	
	@Override
	protected void initializeTable() {
	}
	
}
