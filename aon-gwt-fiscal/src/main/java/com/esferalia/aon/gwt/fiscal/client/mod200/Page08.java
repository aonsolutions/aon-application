package com.esferalia.aon.gwt.fiscal.client.mod200;

import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.MSG;
import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.RESOURCES;

import com.esferalia.aon.gwt.common.client.css.AonCellTable;
import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.fiscal.shared.mod200.DoubleVariable;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Correction;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200CorrectionKey;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;

public class Page08 extends PageAbs {

	interface Page8Binder extends
			UiBinder<Widget, Page08> {
	}

	private static final Page8Binder page8Binder = GWT
			.create(Page8Binder.class);

	private ListDataProvider<Mod200Correction> dataProvider;
	private NoSelectionModel<Mod200Correction> model;	
	
	@UiField
	BoxLabel c500Label;
	@UiField
	DoubleTextBox c500;
	@UiField
	BoxLabel c301Label;
	@UiField
	DoubleTextBox c301;
	@UiField
	BoxLabel c302Label;
	@UiField
	DoubleTextBox c302;
	@UiField
	BoxLabel c501Label;
	@UiField
	DoubleTextBox c501;
	@UiField
	BoxLabel c417Label;
	@UiField
	DoubleTextBox c417;
	@UiField
	BoxLabel c418Label;
	@UiField
	DoubleTextBox c418;
	
	@UiField(provided = true)
	CellTable<Mod200Correction> correctionTable;
	
	@UiField(provided=true)
	ListBox correctionType;
	@UiField
	DoubleTextBox increase;
	@UiField
	DoubleTextBox decrease;
	
	@UiField
	Button newCorrection;
	
	public Page08() {
		super();
		correctionType = new ListBox();
		
		CellTable.Resources aonTableStyle = GWT.create(AonCellTable.class);
		correctionTable = new CellTable<Mod200Correction>(1,aonTableStyle);
		
		correctionTable.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		correctionTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		correctionTable.setEmptyTableWidget(new HTML(MSG.noData()));
		dataProvider = new ListDataProvider<Mod200Correction>();
		dataProvider.addDataDisplay(correctionTable);
		model = new NoSelectionModel<Mod200Correction>();
		model.addSelectionChangeHandler(new SelectionChangeEvent.Handler(){
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				Mod200Correction mc = model.getLastSelectedObject();
				correctionType.setSelectedIndex(mc.getKey().ordinal() + 1);
				increase.setText(mc.getIncrease()==null?null:mc.getIncrease().toString());
				increase.setEnabled(true); 
				decrease.setText(mc.getDecrease()==null?null:mc.getDecrease().toString());
				decrease.setEnabled(true);
			}
		});
		correctionTable.setSelectionModel(model);
		
		addKeyColumn();
		addIncreaseBoxColumn();
		addIncreaseColumn();
		addDecreaseBoxColumn();
		addDecreaseColumn();
		addRemoveColumn();		

		Widget ui = page8Binder.createAndBindUi(this);
		initWidget(ui);
		
		getInputs().put(Mod200Key.LQ500, c500);
		getInputs().put(Mod200Key.LQ301, c301);
		getInputs().put(Mod200Key.LQ302, c302);
		getInputs().put(Mod200Key.LQ501, c501);
		getInputs().put(Mod200Key.I0417, c417);
		getInputs().put(Mod200Key.D0418, c418);
		getLabels().put(Mod200Key.LQ500, c500Label);
		getLabels().put(Mod200Key.LQ301, c301Label);
		getLabels().put(Mod200Key.LQ302, c302Label);
		getLabels().put(Mod200Key.LQ501, c501Label);
		getLabels().put(Mod200Key.I0417, c417Label);
		getLabels().put(Mod200Key.D0418, c418Label);
	}
	
	private void addKeyColumn() {
		Column<Mod200Correction, String> keyColumn = new Column<Mod200Correction, String>(
				new TextCell()) {
			@Override
			public String getValue(Mod200Correction mc) {
				return mc.getKey().getDescription();
			}
		};
		correctionTable.addColumn(keyColumn, MSG.correctionType());
		keyColumn.setCellStyleNames(RESOURCES.css().aonTextLeft());
	}

	private void addIncreaseBoxColumn() {
		Column<Mod200Correction, String> increaseBoxColumn = new Column<Mod200Correction, String>(
				new TextCell()) {
			@Override
			public String getValue(Mod200Correction mc) {
				if (mc.getKey().isIncreaseEnabled() && mc.getIncrease() != null) {
					return mc.getKey().getIncrease().getCode(mod200Object.getAdministration());
				}
				return null;
			}
		};
		correctionTable.addColumn(increaseBoxColumn, "");
		correctionTable.setColumnWidth(increaseBoxColumn, 40, Unit.PX);
		increaseBoxColumn.setCellStyleNames(RESOURCES.css().aonTextLeft());
		
	}
	
	private void addIncreaseColumn() {
		Column<Mod200Correction, String> increaseColumn = new Column<Mod200Correction, String>(
				new TextCell()) {
			@Override
			public String getValue(Mod200Correction mc) {
				if (mc.getKey().isIncreaseEnabled() && mc.getIncrease() != null) {
					return DoubleTextBox.FMT.format(mc.getIncrease());
				}
				return null;
			}
		};
		correctionTable.addColumn(increaseColumn, MSG.increase());
		correctionTable.setColumnWidth(increaseColumn,150,Unit.PX);
		increaseColumn.setCellStyleNames(RESOURCES.css().aonTextRight());
	}

	private void addDecreaseBoxColumn() {
		Column<Mod200Correction, String> decreaseBoxColumn = new Column<Mod200Correction, String>(
				new TextCell()) {
			@Override
			public String getValue(Mod200Correction mc) {
				if (mc.getKey().isDecreaseEnabled() && mc.getDecrease() != null) {
					return mc.getKey().getDecrease().getCode(mod200Object.getAdministration());
				}
				return null;
			}
		};
		correctionTable.addColumn(decreaseBoxColumn, "");
		correctionTable.setColumnWidth(decreaseBoxColumn, 40, Unit.PX);
		decreaseBoxColumn.setCellStyleNames(RESOURCES.css().aonTextLeft());
	}

	private void addDecreaseColumn() {
		Column<Mod200Correction, String> decreaseColumn = new Column<Mod200Correction, String>(
				new TextCell()) {
			@Override
			public String getValue(Mod200Correction mc) {
				if (mc.getKey().isDecreaseEnabled() && mc.getDecrease() != null) {
					return DoubleTextBox.FMT.format(mc.getDecrease());
				}
				return null;
			}
		};
		correctionTable.addColumn(decreaseColumn, MSG.decrease());
		correctionTable.setColumnWidth(decreaseColumn,150,Unit.PX);
		decreaseColumn.setCellStyleNames(RESOURCES.css().aonTextRight());
	}
	
	private void addRemoveColumn() {
		ButtonCell removeButton = new ButtonCell( new Model200.DeleteButtonSafeHtmlTemplates())  {
			  @Override
			  public void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
			    if (data != null) {
			      sb.append(data);
			    }
			  }
		};
		Column<Mod200Correction,String> col = new Column<Mod200Correction,String>(removeButton) {
		  public String getValue(Mod200Correction object) {
		    return MSG.deleteAction();
		  }
		};
		col.setFieldUpdater(new FieldUpdater<Mod200Correction, String>() {
		    public void update(int index, Mod200Correction ca, String value) {
		    	if (Window.confirm(MSG.confirmDeleteAction())) {
		    		changeKey(ca.getKey().getIncrease(), 0);
		    		changeKey(ca.getKey().getDecrease(), 0);
		    		dataProvider.getList().remove(index);
		    		correctionTable.setPageSize(correctionTable.getPageSize() - 1 );
		    		correctionTable.redraw();
		    	}
		    }
		});		
		correctionTable.addColumn(col);
		correctionTable.setColumnWidth(col, 20, Unit.PX);
		col.setCellStyleNames(RESOURCES.css().aonTextCenter());
	}
	
	private void changeKey(Mod200Key key, double value) {
		if (key != null) {
			mod200Object.doubleValueChanged(key, value);	
		}
	}

	@UiHandler("newCorrection")
	void onNewCorrection(ClickEvent event) {
		if (correctionType.getSelectedIndex() == 0) {
			Window.alert("Seleccione un tipo de correcci\u00F3n.");
			correctionType.setFocus(true);
		} else if (increase.getDoubleValue() == 0 && decrease.getDoubleValue() == 0) {
			increase.setFocus(true);
		} else {
			Mod200CorrectionKey key = Mod200CorrectionKey.values()[correctionType.getSelectedIndex() - 1];
			Mod200Correction mc = null;
			for (Mod200Correction mod200Correction : dataProvider.getList()) {
				if (key == mod200Correction.getKey()) {
					mc = mod200Correction;
					break;
				}
			}
			if (mc == null) {
				mc = new Mod200Correction();
				mc.setKey(key);
				dataProvider.getList().add(mc);
				correctionTable.setPageSize(correctionTable.getPageSize() +1 );
			}
			if (key.isIncreaseEnabled()) {
				changeKey(key.getIncrease(), increase.getDoubleValue());
				mc.setIncrease(increase.getDoubleValue());;
			}
			if (key.isDecreaseEnabled()) {
				changeKey(key.getDecrease(), decrease.getDoubleValue());
				mc.setDecrease(decrease.getDoubleValue());;
			}
			correctionTable.redraw();
			correctionType.setSelectedIndex(0);
			increase.setText(null);
			decrease.setText(null);
			increase.setEnabled(false);
			decrease.setEnabled(false);
			correctionType.setFocus(true);
		}
	}
	
	@UiHandler("correctionType")
	public void onChangeCorrectionType(ChangeEvent event) {
		if (correctionType.getSelectedIndex() != 0) {
			Mod200CorrectionKey key = Mod200CorrectionKey.values()[correctionType.getSelectedIndex() - 1];
			increase.setEnabled(key.isIncreaseEnabled());
			decrease.setEnabled(key.isDecreaseEnabled());
			if (key.isIncreaseEnabled()) {
				increase.setFocus(true);	
			} 
			if (key.isDecreaseEnabled()) {
				decrease.setFocus(true);
			}
		} else {
			increase.setEnabled(false);
			decrease.setEnabled(false);
		}
	}
	
	
	@UiHandler("c500")
	public void onChangeC500(ChangeEvent event) {
		onChange(Mod200Key.LQ500, c500);
	}
	@UiHandler("c301")
	public void onChangeC301(ChangeEvent event) {
		onChange(Mod200Key.LQ301, c301);
	}
	@UiHandler("c302")
	public void onChangeC302(ChangeEvent event) {
		onChange(Mod200Key.LQ302, c302);
	}
	@UiHandler("c501")
	public void onChangeC501(ChangeEvent event) {
		onChange(Mod200Key.LQ501, c501);
	}
	@UiHandler("c417")
	public void onChangeC417(ChangeEvent event) {
		onChange(Mod200Key.I0417, c417);
	}
	@UiHandler("c418")
	public void onChangeC418(ChangeEvent event) {
		onChange(Mod200Key.D0418, c418);
	}
	
	private void onChange(Mod200Key key, DoubleTextBox text) {
		if (!text.isValidValue()) {
			text.setValue(0);
		}
		text.addStyleName(RESOURCES.css().aonChanged());
		mod200Object.doubleValueChanged(key, text.getDoubleValue());
	}
	
	

	protected void initializeTable() {
		
		Mod200 mod200 = this.mod200Object.getMod200();
		for (int i = 0; i < correctionType.getItemCount(); i++) {
			correctionType.removeItem(i);	
		}
		correctionType.addItem("--------------");
		for (Mod200CorrectionKey key : Mod200CorrectionKey.values()) {
			correctionType.addItem(
			  (key.isIncreaseEnabled()?key.getIncrease().getCode(mod200Object.getAdministration()):"")
			  + " " + (key.isDecreaseEnabled()?key.getDecrease().getCode(mod200Object.getAdministration()):"")
			  + " "+ key.getDescription()
			);	
		}
		
		for (Mod200Key key : getInputs().keySet() ) {
			DoubleVariable d = mod200.getKey(key);
			if (d != null && d.getValue() != null) {
				getInputs().get(key).setText( DoubleTextBox.FMT.format(d.getValue()));
			}
		}
		for (Mod200CorrectionKey key : Mod200CorrectionKey.values()) {
			Mod200Correction mc = null;
			Double inc = mod200.getDoubleValue(key.getIncrease());
			if (inc != null && inc != 0) {
				mc = new Mod200Correction();
				mc.setKey(key);
				mc.setIncrease(inc);
			}
			Double dec = mod200.getDoubleValue(key.getDecrease());
			if (dec != null && dec != 0) {
				mc = mc!=null?mc:new Mod200Correction();
				mc.setKey(key);
				mc.setDecrease(dec);
			}
			if (mc != null) {
				dataProvider.getList().add(mc);
				correctionTable.setPageSize(correctionTable.getPageSize() +1 );				
			}
		}
		correctionTable.redraw();
	}
	
}
