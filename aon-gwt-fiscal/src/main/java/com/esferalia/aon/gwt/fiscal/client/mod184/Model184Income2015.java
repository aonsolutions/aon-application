package com.esferalia.aon.gwt.fiscal.client.mod184;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Income;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

public class Model184Income2015 extends ResizeComposite {
	
	public static final ProvidesKey<Mod184Income> MOD184_INCOME_PROVIDES_KEY = new ProvidesKey<Mod184Income>() {
		@Override
		public Object getKey(Mod184Income det) {
			return det == null ?null: det.getId() == null? det.getTempId(): det.getId();
		}
	};

	interface Model184Income2014Binder extends UiBinder<Widget, Model184Income2015> {}
	private static Model184Income2014Binder MODEL184_INCOME_2014_BINDER 
		= GWT.create(Model184Income2014Binder.class);

	static interface IIncomeCallBack {
		void redrawList( Mod184Income income);
	}

	public static enum Mod1842015IncomeKey {
		 A (new String[] {"01","02","03"})
		,B 
		,C (new String[] {"01","02","03"})
		,D (new String[] {"01","02","03"})
		,E 
		,F (new String[] {"01","02","03","04"})
		,G (new String[] {"01","02","03","04","05","06","07","08"})
		,H 
		,I (new String[] {"01","02","03","04","05","06"})
		,J (new String[] {"01","02","03","04"})
		,K (new String[] {"01","02","03","04","05"})
		,L (new String[] {"A","C","D","E","F","G"})
		;
		
		private String[] subkeys;
		
		private Mod1842015IncomeKey(String[] subkeys) {
			this.subkeys = subkeys;
		}
		private Mod1842015IncomeKey() {
			this(null);
		}
		
		public String getValue() {
			return toString();
		}
		public boolean hasSubkeys() {
			return this.subkeys != null;
		}
		public String[] getSubkeys() {
			return subkeys;
		}
	}

	public class IncomeKeyListBox extends ListBox {

		public IncomeKeyListBox() {
			subKey = new ListBox();
			subKey.setWidth("45px");

			setWidth("40px");
			this.addItem("-","");
			for (Mod1842015IncomeKey key : Mod1842015IncomeKey.values()) {
				this.addItem(key.getValue(),key.getValue());
			}

			addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					int idx = getSelectedIndex();
					if (idx == 0) {
						getDetail().setKey(null);
						getDetail().setSubKey(null);
						subKey.clear();	
					} else {
						Mod1842015IncomeKey keyEnum = Mod1842015IncomeKey.values()[idx-1];
						getDetail().setKey(keyEnum.getValue());
						getDetail().setSubKey(null);
						subKey.clear();
						if (keyEnum.hasSubkeys()) {
							subKey.setEnabled(true);
							subKey.addItem("-","");
							for (int i = 0; i < keyEnum.getSubkeys().length; i++) {
								subKey.addItem(keyEnum.getSubkeys()[i]);
							}
						} else {
							subKey.setEnabled(false);
						}
					}
					enableWidgets();
					markAsDirty();
					incomesList.redraw();
				}
			});
		}

		public ListBox getSubKey() {
			return subKey;
		}

		public void setValue(String key, String subkey) {
			if (key != null) {
				Mod1842015IncomeKey keyEnum = Mod1842015IncomeKey.valueOf(key);
				setSelectedIndex(keyEnum.ordinal()+1);
				getSubKey().clear();
				if (keyEnum.hasSubkeys()) {
					getSubKey().setEnabled(true);
					getSubKey().addItem("-","");
					for (int i = 0; i < keyEnum.getSubkeys().length; i++) {
						String sk = keyEnum.getSubkeys()[i];
						getSubKey().addItem(sk);
						if (keyEnum.getSubkeys()[i].equals(subkey)) {
							getSubKey().setSelectedIndex(i + 1 );
						}
					}
					getSubKey().setEnabled(true);
				}			
			} else {
				setSelectedIndex(0);
				getSubKey().clear();
				getSubKey().setEnabled(false);
			}
		}
	}

	private Mod184 currentMod184;
	private Mod184IncomeDataProvider incomesDataProvider;
	private SingleSelectionModel<Mod184Income> incomesModel;
	
	@UiField
	Button deleteDetailButton;
	@UiField
	Button restoreDeletedButton;
	@UiField
	Button newDetailButton;


	@UiField(provided=true)
	CellList<Mod184Income> incomesList;
	@UiField
	ScrollPanel detailListPanel;
	
	@UiField(provided = true)
	IncomeKeyListBox key;
	@UiField(provided = true)
	ListBox subKey;
	@UiField
	CountryListBox country;
	@UiField
	ListBox regime;
	@UiField
	ListBox activityType;
	@UiField
	TextBox epigraph;
	@UiField
	DocumentTextBox granteeDocument;
	@UiField
	TextBox granteeName;
	@UiField
	DateBoxEx adqDate;
	@UiField
	DoubleBox increase;
	@UiField
	DoubleBox decrease;
	@UiField
	DoubleBox accountingResult;
	@UiField
	DoubleBox expenses;
	@UiField
	DoubleBox netYield;
	@UiField
	DoubleBox reductionPercent;
	@UiField
	DoubleBox deductionRightRent;
	@UiField
	DoubleBox result;
	@UiField
	DoubleBox deductionBase;
	@UiField
	DoubleBox retention;
	@UiField
	ListBox location;
	@UiField
	TextBox cadasdralReference;
	@UiField
	DoubleBox staffExpenses;
	@UiField
	DoubleBox assetAcquisition;
	@UiField
	DoubleBox taxDeduction;
	@UiField
	DoubleBox otherTaxDeduction;
	
	public Model184Income2015() {
		Mod184IncomeCell mod184IncomeCell = new Mod184IncomeCell();
		incomesList = new CellList<Mod184Income>(mod184IncomeCell, MOD184_INCOME_PROVIDES_KEY);
		incomesList.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		incomesList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		// Add a selection model so we can select cells.
		incomesModel = new SingleSelectionModel<Mod184Income>(MOD184_INCOME_PROVIDES_KEY);
		incomesModel.addSelectionChangeHandler(new Handler() {
			
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				selectDetail();
			}
			
		});
		incomesList.setSelectionModel(incomesModel);
		incomesList.setEmptyListWidget(new HTML(AON.MSG.noData()));

		incomesDataProvider = new Mod184IncomeDataProvider(MOD184_INCOME_PROVIDES_KEY);
		incomesDataProvider.addDataDisplay(incomesList);
		incomesList.setVisible(true);
		
		key = new IncomeKeyListBox();
		
		Widget ui = MODEL184_INCOME_2014_BINDER.createAndBindUi(this);
		initWidget(ui);
		
		regime.addItem("-","");
		regime.addItem("1 - Estimaci\u00F3n directa modalidad normal","1");
		regime.addItem("2 - Estimaci\u00F3n directa modalidad simplificada","2");
		regime.addItem("3 - Estimaci\u00F3n objetiva","3");
		regime.setWidth("200px");

		activityType.addItem("-","");
		activityType.addItem("1 - Actividades empresariales de car\u00E1cter mercantil","1");
		activityType.addItem("2 - Actividades agr\u00EDcolas y ganaderas","2");
		activityType.addItem("3 - Otras actividades empresariales de car\u00E1cter no mercantil","3");
		activityType.addItem("4 - Actividades profesionales de car\u00E1cter art\u00EDstico o deportivo","4");
		activityType.addItem("5 - Restantes actividades profesionales","5");
		activityType.setWidth("150px");
		
		netYield.setEnabled(false);
		
		location.addItem( AON.MSG.buildingLocationValue(0) );
		location.addItem( AON.MSG.buildingLocationValue(1) );	
		location.addItem( AON.MSG.buildingLocationValue(2) );
		location.addItem( AON.MSG.buildingLocationValue(3) );

	}
	
	public void setMod184(Mod184 mod184) {
		this.currentMod184 = mod184;

		incomesList.setVisibleRangeAndClearData(incomesList.getVisibleRange(),true);
	}
	
	private Mod184Income getDetail() {
		return incomesModel.getSelectedObject();
	}
	
	public void selectDetail() {
		key.setValue(getDetail().getKey(), getDetail().getSubKey());
		country.setSelectedIndex( 
				AonStringUtils.isBlank( getDetail().getCountry() )
				? 0 
				: Country.valueOf(getDetail().getCountry()).ordinal() + 1 );
		regime.setSelectedIndex(getDetail().getRegime());
		activityType.setSelectedIndex(getDetail().getActivityType());
		epigraph.setValue( getDetail().getEpigraph()==null?null:Integer.toString(getDetail().getEpigraph()) );
		granteeDocument.setValue(getDetail().getGranteeDocument());
		granteeName.setValue(getDetail().getGranteeName());
		adqDate.setValue(getDetail().getAdqDate());
		increase.setValue(getDetail().getIncrease());
		decrease.setValue(getDetail().getDecrease());
		accountingResult.setValue(getDetail().getAccountingResult());
		expenses.setValue(getDetail().getExpenses());
		netYield.setValue(getDetail().getNetYield());
		reductionPercent.setValue(getDetail().getReductionPercent());
		deductionRightRent.setValue(getDetail().getDeductionRightRent());
		result.setValue(getDetail().getResult());
		deductionBase.setValue(getDetail().getDeductionBase());
		retention.setValue(getDetail().getRetention());
		if (getDetail().getLocation() != null) {
			try {
				location.setSelectedIndex( Integer.parseInt(getDetail().getLocation()) );
			} catch (NumberFormatException e) {
				location.setSelectedIndex( 0 );
			}
		} else {
			location.setSelectedIndex( 0 );
		}
		cadasdralReference.setValue(getDetail().getCadasdralReference() );

		staffExpenses.setValue(getDetail().getStaffExpenses());
		assetAcquisition.setValue(getDetail().getAssetAcquisition());
		taxDeduction.setValue(getDetail().getTaxDeduction());
		otherTaxDeduction.setValue(getDetail().getOtherTaxDeduction());
		
		enableWidgets();
		restoreDeletedButton.setVisible(getDetail().isDeleted());
		deleteDetailButton.setVisible(!getDetail().isDeleted());
	}

	@UiHandler("deleteDetailButton")
	void onDeleteDetailButtonClick(ClickEvent event) {
		getDetail().setDeleted(true);
		restoreDeletedButton.setVisible(true);
		deleteDetailButton.setVisible(false);
		incomesList.redraw();
	}

	@UiHandler("restoreDeletedButton")
	void onRestoreDeletedButtonClick(ClickEvent event) {
		getDetail().setDeleted(false);
		if (!getDetail().isDirty()) {
			restoreDeletedButton.setVisible(false);
			deleteDetailButton.setVisible(true);
			incomesList.redraw();
		}
	};
	
	@UiHandler("newDetailButton")
	void onNewDetailButtonClick(ClickEvent event) {
		newPerceptor();
	}
	
	private void newPerceptor(){
		currentMod184.getIncomes().add(
			new Mod184Income()
				.setKey("A")
				.setSubKey("01")				
				.setDirty(true)
				.setTempId((currentMod184.getIncomes().size() * (-1)))
		);
		incomesList.setRowCount(incomesList.getRowCount() + 1);
		incomesList.setPageSize(incomesList.getRowCount());
		incomesList.redraw();
		selectInList(currentMod184.getIncomes().size() - 1);
		selectDetail();		
	}
	
	private void selectInList(int i) {
		incomesModel.setSelected(currentMod184.getIncomes().get(i),true);
		incomesList.getRowElement(i).scrollIntoView();
		detailListPanel.scrollToLeft();
	}

	@UiHandler("subKey")
	void onChangeSubKey(ChangeEvent event) {
		String subk = ((key.getSubKey().getSelectedIndex() == -1) ? null : key
				.getSubKey().getValue(key.getSubKey().getSelectedIndex()));
		getDetail().setSubKey(subk);
		enableWidgets();
		markAsDirty();
		incomesList.redraw();
	}
	@UiHandler("country")
	void onChangeCountry(ChangeEvent event) {
		getDetail().setCountry(country.getValue(country.getSelectedIndex()));
		markAsDirty();
	}
	@UiHandler("regime")
	void onChangeRegime(ChangeEvent event) {
		getDetail().setRegime((byte) regime.getSelectedIndex());
		enableWidgets();
		markAsDirty();
	}
	@UiHandler("activityType")
	void onChangeActivityType(ChangeEvent event) {
		getDetail().setActivityType((byte) activityType.getSelectedIndex());
		markAsDirty();
	}
	@UiHandler("epigraph")
	void onChangeEpigraph(ChangeEvent event) {
		try {
			getDetail().setEpigraph( Integer.parseInt(epigraph.getValue()) );
		} catch (NumberFormatException e ) {
			getDetail().setEpigraph( null );
		}
		markAsDirty();
	}
	@UiHandler("granteeDocument")
	void onChangeGranteeDocument(ChangeEvent event) {
		getDetail().setGranteeDocument(granteeDocument.getValue());
		markAsDirty();
	}
	@UiHandler("granteeName")
	void onChangeGranteeName(ChangeEvent event) {
		getDetail().setGranteeName(granteeName.getValue());
		markAsDirty();
	}
	@UiHandler("adqDate")
	void onChangeAdqDate(ValueChangeEvent<Date> event) {
		getDetail().setAdqDate(adqDate.getValue());
		markAsDirty();
	}
	@UiHandler("increase")
	void onChangeIncrease(ChangeEvent event) {
		getDetail().setIncrease(increase.getValue());
		markAsDirty();
	}
	@UiHandler("decrease")
	void onChangeDecrease(ChangeEvent event) {
		getDetail().setDecrease(decrease.getValue());
		markAsDirty();
	}
	@UiHandler("accountingResult")
	void onChangeAccountingResult(ChangeEvent event) {
		getDetail().setAccountingResult(accountingResult.getValue());
		markAsDirty();
		refreshNetYield();
	}
	@UiHandler("expenses")
	void onChangeExpenses(ChangeEvent event) {
		getDetail().setExpenses(expenses.getValue());
		markAsDirty();
		refreshNetYield();
	}
	private void refreshNetYield() {
		getDetail().setNetYield( AonMathUtils.round(getDetail().getAccountingResult() - getDetail().getExpenses()));
		netYield.setValue(getDetail().getNetYield());
	}
	@UiHandler("netYield")
	void onChangeNetYield(ChangeEvent event) {
		getDetail().setNetYield(netYield.getValue());
		markAsDirty();
	}
	@UiHandler("reductionPercent")
	void onChangeReductionPercent(ChangeEvent event) {
		getDetail().setReductionPercent(reductionPercent.getValue());
		markAsDirty();
	}
	@UiHandler("deductionRightRent")
	void onChangeDeductionRightRent(ChangeEvent event) {
		getDetail().setDeductionRightRent(deductionRightRent.getValue());
		markAsDirty();
	}
	@UiHandler("result")
	void onChangeResult(ChangeEvent event) {
		getDetail().setResult(result.getValue());
		markAsDirty();
	}
	@UiHandler("deductionBase")
	void onChangeDeductionBase(ChangeEvent event) {
		getDetail().setDeductionBase(deductionBase.getValue());
		markAsDirty();
	}

	@UiHandler("retention")
	void onChangeRetention(ChangeEvent event) {
		getDetail().setRetention(retention.getValue());
		markAsDirty();
	}
	@UiHandler("location")
	void onChangeLocation(ChangeEvent event) {
		getDetail().setLocation(Integer.toString( location.getSelectedIndex()));
		getDetail().setDirty(true);
	}
	@UiHandler("cadasdralReference")
	void onChangeCadasdralReference(ChangeEvent event) {
		getDetail().setCadasdralReference(cadasdralReference.getValue());
		markAsDirty();
	}
	
	@UiHandler("staffExpenses")
	void onChangeStaffExpenses(ChangeEvent event) {
		getDetail().setStaffExpenses(staffExpenses.getValue());
		markAsDirty();
	}

	@UiHandler("assetAcquisition")
	void onChangeAssetAcquisition(ChangeEvent event) {
		getDetail().setAssetAcquisition(assetAcquisition.getValue());
		markAsDirty();
	}
	
	@UiHandler("taxDeduction")
	void onChangeTaxDeduction(ChangeEvent event) {
		getDetail().setTaxDeduction(taxDeduction.getValue());
		markAsDirty();
	}
	
	@UiHandler("otherTaxDeduction")
	void onChangeOtherTaxDeduction(ChangeEvent event) {
		getDetail().setOtherTaxDeduction(otherTaxDeduction.getValue());
		markAsDirty();
	}

	private void enableWidgets() {
		country.setEnabled(
			    ("A".equals(getDetail().getKey()) && "02".equals(getDetail().getSubKey()))
			 || ("B".equals(getDetail().getKey()) )
			 || ("C".equals(getDetail().getKey()) && "02".equals(getDetail().getSubKey()))
			 || ("D".equals(getDetail().getKey()) && "02".equals(getDetail().getSubKey()))
			 || ("F".equals(getDetail().getKey()) && "03".equals(getDetail().getSubKey())) 
			 || ("F".equals(getDetail().getKey()) && "04".equals(getDetail().getSubKey()))
			 || ("G".equals(getDetail().getKey()) && "03".equals(getDetail().getSubKey())) 
			 || ("G".equals(getDetail().getKey()) && "04".equals(getDetail().getSubKey())) 
			 || ("G".equals(getDetail().getKey()) && "06".equals(getDetail().getSubKey())) 
			 || ("G".equals(getDetail().getKey()) && "08".equals(getDetail().getSubKey())) 
			 || ("L".equals(getDetail().getKey()) && "A".equals(getDetail().getSubKey()))
			 || ("L".equals(getDetail().getKey()) && "C".equals(getDetail().getSubKey()))
			 || ("L".equals(getDetail().getKey()) && "D".equals(getDetail().getSubKey()))
			 || ("L".equals(getDetail().getKey()) && "E".equals(getDetail().getSubKey()))
			 || ("L".equals(getDetail().getKey()) && "F".equals(getDetail().getSubKey()))
			 || ("L".equals(getDetail().getKey()) && "G".equals(getDetail().getSubKey()))
			 || ("L".equals(getDetail().getKey()) && "M".equals(getDetail().getSubKey()))
			 || ("M".equals(getDetail().getKey()) && "03".equals(getDetail().getSubKey())) 
			 || ("M".equals(getDetail().getKey()) && "04".equals(getDetail().getSubKey())) 
		);
		
		epigraph.setEnabled("D".equals(getDetail().getKey())); 
		granteeDocument.setEnabled("B".equals(getDetail().getKey()) || "E".equals(getDetail().getKey()));
		granteeName.setEnabled("B".equals(getDetail().getKey()) || "E".equals(getDetail().getKey()));
		adqDate.setEnabled("E".equals(getDetail().getKey()));
		increase.setEnabled("E".equals(getDetail().getKey()) || "H".equals(getDetail().getKey()));
		decrease.setEnabled("E".equals(getDetail().getKey()) || "H".equals(getDetail().getKey()));
		accountingResult.setEnabled(
			   ("A".equals(getDetail().getKey()) && "01".equals(getDetail().getSubKey()))
			|| ("A".equals(getDetail().getKey()) && "02".equals(getDetail().getSubKey()))
			|| ("B".equals(getDetail().getKey()) )
			|| ("C".equals(getDetail().getKey()) && "01".equals(getDetail().getSubKey()))
			|| ("C".equals(getDetail().getKey()) && "02".equals(getDetail().getSubKey()))
			|| ("D".equals(getDetail().getKey()) && "01".equals(getDetail().getSubKey()))
			|| ("D".equals(getDetail().getKey()) && "02".equals(getDetail().getSubKey()))
			|| ("H".equals(getDetail().getKey()) )									
				);
		expenses.setEnabled(
				   ("A".equals(getDetail().getKey()) && "01".equals(getDetail().getSubKey()))
				|| ("A".equals(getDetail().getKey()) && "02".equals(getDetail().getSubKey()))
				|| ("C".equals(getDetail().getKey()) && "01".equals(getDetail().getSubKey()))
				|| ("C".equals(getDetail().getKey()) && "02".equals(getDetail().getSubKey()))
				|| ("D".equals(getDetail().getKey()) && "01".equals(getDetail().getSubKey()) && getDetail().getRegime() != 3)
				|| ("D".equals(getDetail().getKey()) && "02".equals(getDetail().getSubKey()) && getDetail().getRegime() != 3)
				|| ("L".equals(getDetail().getKey()) )
				);
		reductionPercent.setEnabled(
				   ("A".equals(getDetail().getKey()) && "03".equals(getDetail().getSubKey()))
				|| ("C".equals(getDetail().getKey()) && "03".equals(getDetail().getSubKey()))
				);
		deductionRightRent.setEnabled(
				   ("A".equals(getDetail().getKey()) && "03".equals(getDetail().getSubKey()))
				|| ("C".equals(getDetail().getKey()) && "03".equals(getDetail().getSubKey()))
				|| ("D".equals(getDetail().getKey()) && "03".equals(getDetail().getSubKey()))
				);
		result.setEnabled(
				   "F".equals(getDetail().getKey())
				|| "G".equals(getDetail().getKey())
				|| "M".equals(getDetail().getKey())
				);
		
		deductionBase.setEnabled(
				   "I".equals(getDetail().getKey())
				|| "J".equals(getDetail().getKey())
				);
		retention.setEnabled(
				   "K".equals(getDetail().getKey())
				);
		location.setEnabled(
				   "C".equals(getDetail().getKey())
				);
		cadasdralReference.setEnabled(
				   "C".equals(getDetail().getKey())
				);
		staffExpenses.setEnabled(
				"D".equals(getDetail().getKey()) 
				&& ("01".equals(getDetail().getSubKey()) || "02".equals(getDetail().getSubKey())) 
				&& getDetail().getRegime() != 3);
		assetAcquisition.setEnabled(
				"D".equals(getDetail().getKey()) 
				&& ("01".equals(getDetail().getSubKey()) || "02".equals(getDetail().getSubKey())) 
				&& getDetail().getRegime() != 3);
		taxDeduction.setEnabled(
				"D".equals(getDetail().getKey()) 
				&& ("01".equals(getDetail().getSubKey()) || "02".equals(getDetail().getSubKey())) 
				&& getDetail().getRegime() != 3);
		otherTaxDeduction.setEnabled(
				"D".equals(getDetail().getKey()) 
				&& ("01".equals(getDetail().getSubKey()) || "02".equals(getDetail().getSubKey())) 
				&& getDetail().getRegime() != 3);
	}
	
	private void markAsDirty() {
		if (!getDetail().isDirty()) {
			getDetail().setDirty(true);
			incomesList.redraw();		
		}
	}
	
	class Mod184IncomeDataProvider extends AsyncDataProvider<Mod184Income> {

		public Mod184IncomeDataProvider(ProvidesKey<Mod184Income> detailProvidesKey) {
			super(detailProvidesKey);
		}

		@Override
		protected void onRangeChanged(HasData<Mod184Income> display) {
			if (currentMod184 != null && currentMod184.getId() != null) {
				if (currentMod184.getIncomes().size() == 0) {
					newPerceptor();					
				} else {
					updateRowCount(currentMod184.getIncomes().size(), true);
					updateRowData(0, currentMod184.getIncomes());
					incomesList.setPageSize(currentMod184.getIncomes().size());
					selectInList(0);
					selectDetail();
				}
			}
		}
	}
	
	static class Mod184IncomeCell extends AbstractCell<Mod184Income> {
		@Override
		public void render(Cell.Context context, Mod184Income value,
				SafeHtmlBuilder sb) {
			if (value == null) {
				return;
			}
			sb.appendHtmlConstant("<div style='");
			if (value.isDirty()) {
				sb.appendHtmlConstant("font-weight:bold;");
			}
			if (value.isDeleted()) {
				sb.appendHtmlConstant("text-decoration:line-through");
			}
			sb.appendHtmlConstant("' class='");
			sb.appendHtmlConstant( AON.AON_CSS.aonLinkListItem());
			sb.appendHtmlConstant("'>");
			sb.appendEscaped(AonStringUtils.isBlank(value.getKey()) 
					? AON.MSG.newPerceptor()
					: value.getKey() + (AonStringUtils.isBlank(value.getSubKey())?"":("-" + value.getSubKey())));
			if (value.isDirty()) {
				sb.appendEscaped(" *");
			}
			sb.appendHtmlConstant("</div>");
		}
	}
}
