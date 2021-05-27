package com.esferalia.aon.gwt.fiscal.client.mod200.e2013;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.BoxLabel;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod200.Model200Table;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.DoubleVariable2013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013Correction;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013CorrectionKey;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013Key;
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

	private ListDataProvider<Mod2002013Correction> dataProvider;
	private NoSelectionModel<Mod2002013Correction> model;	
	
	@UiField
	BoxLabel c500Label;
	@UiField
	DoubleBox c500;
	@UiField
	BoxLabel c301Label;
	@UiField
	DoubleBox c301;
	@UiField
	BoxLabel c302Label;
	@UiField
	DoubleBox c302;
	@UiField
	BoxLabel c501Label;
	@UiField
	DoubleBox c501;
	@UiField
	BoxLabel c417Label;
	@UiField
	DoubleBox c417;
	@UiField
	BoxLabel c418Label;
	@UiField
	DoubleBox c418;
	
	@UiField(provided = true)
	CellTable<Mod2002013Correction> correctionTable;
	
	@UiField(provided=true)
	ListBox correctionType;
	@UiField
	DoubleBox increase;
	@UiField
	DoubleBox decrease;
	
	@UiField
	Button newCorrection;
	
	public Page08() {
		super();
		correctionType = new ListBox();
		
		correctionTable = new CellTable<Mod2002013Correction>(1, Model200Table.TABLE_STYLE);
		
		correctionTable.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		correctionTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		correctionTable.setEmptyTableWidget(new HTML(AON.MSG.noData()));
		dataProvider = new ListDataProvider<Mod2002013Correction>();
		dataProvider.addDataDisplay(correctionTable);
		model = new NoSelectionModel<Mod2002013Correction>();
		model.addSelectionChangeHandler(new SelectionChangeEvent.Handler(){
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				Mod2002013Correction mc = model.getLastSelectedObject();
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
		
		getInputs().put(Mod2002013Key.LQ500, c500);
		getInputs().put(Mod2002013Key.LQ301, c301);
		getInputs().put(Mod2002013Key.LQ302, c302);
		getInputs().put(Mod2002013Key.LQ501, c501);
		getInputs().put(Mod2002013Key.I0417, c417);
		getInputs().put(Mod2002013Key.D0418, c418);
		getLabels().put(Mod2002013Key.LQ500, c500Label);
		getLabels().put(Mod2002013Key.LQ301, c301Label);
		getLabels().put(Mod2002013Key.LQ302, c302Label);
		getLabels().put(Mod2002013Key.LQ501, c501Label);
		getLabels().put(Mod2002013Key.I0417, c417Label);
		getLabels().put(Mod2002013Key.D0418, c418Label);
	}
	
	private void addKeyColumn() {
		Column<Mod2002013Correction, String> keyColumn = new Column<Mod2002013Correction, String>(
				new TextCell()) {
			@Override
			public String getValue(Mod2002013Correction mc) {
				return mc.getKey().getDescription();
			}
		};
		correctionTable.addColumn(keyColumn, AON.MSG.correctionType());
		keyColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
	}

	private void addIncreaseBoxColumn() {
		Column<Mod2002013Correction, String> increaseBoxColumn = new Column<Mod2002013Correction, String>(
				new TextCell()) {
			@Override
			public String getValue(Mod2002013Correction mc) {
				if (mc.getKey().isIncreaseEnabled() && mc.getIncrease() != null) {
					return mc.getKey().getIncrease().getCode(mod200Object.getAdministration());
				}
				return null;
			}
		};
		correctionTable.addColumn(increaseBoxColumn, "");
		correctionTable.setColumnWidth(increaseBoxColumn, 40, Unit.PX);
		increaseBoxColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
		
	}
	
	private void addIncreaseColumn() {
		Column<Mod2002013Correction, String> increaseColumn = new Column<Mod2002013Correction, String>(
				new TextCell()) {
			@Override
			public String getValue(Mod2002013Correction mc) {
				if (mc.getKey().isIncreaseEnabled() && mc.getIncrease() != null) {
					return Double.toString(mc.getIncrease());
				}
				return null;
			}
		};
		correctionTable.addColumn(increaseColumn, AON.MSG.increase());
		correctionTable.setColumnWidth(increaseColumn,150,Unit.PX);
		increaseColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
	}

	private void addDecreaseBoxColumn() {
		Column<Mod2002013Correction, String> decreaseBoxColumn = new Column<Mod2002013Correction, String>(
				new TextCell()) {
			@Override
			public String getValue(Mod2002013Correction mc) {
				if (mc.getKey().isDecreaseEnabled() && mc.getDecrease() != null) {
					return mc.getKey().getDecrease().getCode(mod200Object.getAdministration());
				}
				return null;
			}
		};
		correctionTable.addColumn(decreaseBoxColumn, "");
		correctionTable.setColumnWidth(decreaseBoxColumn, 40, Unit.PX);
		decreaseBoxColumn.setCellStyleNames(AON.AON_CSS.aonTextLeft());
	}

	private void addDecreaseColumn() {
		Column<Mod2002013Correction, String> decreaseColumn = new Column<Mod2002013Correction, String>(
				new TextCell()) {
			@Override
			public String getValue(Mod2002013Correction mc) {
				if (mc.getKey().isDecreaseEnabled() && mc.getDecrease() != null) {
					return Double.toString(mc.getDecrease());
				}
				return null;
			}
		};
		correctionTable.addColumn(decreaseColumn, AON.MSG.decrease());
		correctionTable.setColumnWidth(decreaseColumn,150,Unit.PX);
		decreaseColumn.setCellStyleNames(AON.AON_CSS.aonTextRight());
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
		Column<Mod2002013Correction,String> col = new Column<Mod2002013Correction,String>(removeButton) {
		  public String getValue(Mod2002013Correction object) {
		    return AON.MSG.deleteAction();
		  }
		};
		col.setFieldUpdater(new FieldUpdater<Mod2002013Correction, String>() {
		    public void update(int index, Mod2002013Correction ca, String value) {
		    	if (Window.confirm(AON.MSG.confirmDeleteAction())) {
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
		col.setCellStyleNames(AON.AON_CSS.aonTextCenter());
	}
	
	private void changeKey(Mod2002013Key key, double value) {
		if (key != null) {
			mod200Object.doubleValueChanged(key, value);	
		}
	}

	@UiHandler("newCorrection")
	void onNewCorrection(ClickEvent event) {
		if (correctionType.getSelectedIndex() == 0) {
			Window.alert("Seleccione un tipo de correcci\u00F3n.");
			correctionType.setFocus(true);
		} else if (increase.getValue() == 0 && decrease.getValue() == 0) {
			increase.setFocus(true);
		} else {
			Mod2002013CorrectionKey key = Mod2002013CorrectionKey.values()[correctionType.getSelectedIndex() - 1];
			Mod2002013Correction mc = null;
			for (Mod2002013Correction mod200Correction : dataProvider.getList()) {
				if (key == mod200Correction.getKey()) {
					mc = mod200Correction;
					break;
				}
			}
			if (mc == null) {
				mc = new Mod2002013Correction();
				mc.setKey(key);
				dataProvider.getList().add(mc);
				correctionTable.setPageSize(correctionTable.getPageSize() +1 );
			}
			if (key.isIncreaseEnabled()) {
				changeKey(key.getIncrease(), increase.getValue());
				mc.setIncrease(increase.getValue());
			}
			if (key.isDecreaseEnabled()) {
				changeKey(key.getDecrease(), decrease.getValue());
				mc.setDecrease(decrease.getValue());
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
			Mod2002013CorrectionKey key = Mod2002013CorrectionKey.values()[correctionType.getSelectedIndex() - 1];
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
		onChange(Mod2002013Key.LQ500, c500);
	}
	@UiHandler("c301")
	public void onChangeC301(ChangeEvent event) {
		onChange(Mod2002013Key.LQ301, c301);
	}
	@UiHandler("c302")
	public void onChangeC302(ChangeEvent event) {
		onChange(Mod2002013Key.LQ302, c302);
	}
	@UiHandler("c501")
	public void onChangeC501(ChangeEvent event) {
		onChange(Mod2002013Key.LQ501, c501);
	}
	@UiHandler("c417")
	public void onChangeC417(ChangeEvent event) {
		onChange(Mod2002013Key.I0417, c417);
	}
	@UiHandler("c418")
	public void onChangeC418(ChangeEvent event) {
		onChange(Mod2002013Key.D0418, c418);
	}
	
	private void onChange(Mod2002013Key key, DoubleBox text) {
		text.addStyleName(AON.AON_CSS.aonChanged());
		mod200Object.doubleValueChanged(key, text.getValue());
	}
	
	

	protected void initializeTable() {
		Mod2002013 mod200 = this.mod200Object.getMod200();
		for (int i = 0; i < correctionType.getItemCount(); i++) {
			correctionType.removeItem(i);	
		}
		correctionType.addItem("--------------");
		for (Mod2002013CorrectionKey key : Mod2002013CorrectionKey.values()) {
			correctionType.addItem(
			  (key.isIncreaseEnabled()?key.getIncrease().getCode(mod200Object.getAdministration()):"")
			  + " " + (key.isDecreaseEnabled()?key.getDecrease().getCode(mod200Object.getAdministration()):"")
			  + " "+ key.getDescription()
			);	
		}
		
		for (Mod2002013Key key : getInputs().keySet() ) {
			DoubleVariable2013 d = mod200.getKey(key);
			Double value = 0.0;
			if (d != null && d.getValue() != null) {
				value = d.getValue();
			}
			getInputs().get(key).setValue(value);
		}
		for (Mod2002013CorrectionKey key : Mod2002013CorrectionKey.values()) {
			Mod2002013Correction mc = null;
			Double inc = mod200.getDoubleValue(key.getIncrease());
			if (inc != null && inc != 0) {
				mc = new Mod2002013Correction();
				mc.setKey(key);
				mc.setIncrease(inc);
			}
			Double dec = mod200.getDoubleValue(key.getDecrease());
			if (dec != null && dec != 0) {
				mc = mc!=null?mc:new Mod2002013Correction();
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
//try {
//	Double d = text.getValueOrThrow();
//	text.addStyleName(AON.AON_CSS.aonChanged());
//	mod200Object.doubleValueChanged(key, d);
//} catch (ParseException e) {
//	// nothing.
//}
