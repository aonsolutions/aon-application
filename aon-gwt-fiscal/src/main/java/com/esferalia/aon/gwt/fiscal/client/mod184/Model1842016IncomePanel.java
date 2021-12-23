package com.esferalia.aon.gwt.fiscal.client.mod184;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184Income2016.IModel184IncomeCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Income;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

public class Model1842016IncomePanel extends SimpleLayoutPanel implements Focusable {
	
	private static final String WIDTH_200PX = "200px";
	private static final String WIDTH_150PX = "150px";
	private static final String RENDIMIENTOS_ESPANA = "01 - Rendimientos obtenidos en Espa\u00F1a";
	private static final String RENDIMIENTOS_EXTRANJERO = "02 - Rendimientos obtenidos en el extranjero";

	private enum Mod184IncomeKey {
		 A ("A - Rendimientos del capital mobiliario."
			 ,new String[] {"01","02","03"}
		 	 ,new String[] {
	 			 RENDIMIENTOS_ESPANA
		 		,RENDIMIENTOS_EXTRANJERO
		 		,"03 - Reducciones aplicable"})
		,B ("B - Identificaci\u00F3n de la persona o entidad cesionaria de los capitales propios."
				,null,null)
		,C ("C - Rendimientos del capital inmobiliario."
			,new String[] {"01","02","03"}
		 	 ,new String[] {
	 			 RENDIMIENTOS_ESPANA
	 			,RENDIMIENTOS_EXTRANJERO
	 			,"03 - Reducciones aplicable"})
		,D ("D - Rendimientos de actividades econ\u00F3micas."
			,new String[] {"01","02","03"}
		 	 ,new String[] {
	 			 RENDIMIENTOS_ESPANA
		 		,RENDIMIENTOS_EXTRANJERO
		 		,"03 - Importe del rendimientocon derecho a reducci\u00F3n"})
		,E ("E - Rentas contabilizadas derivadas de la participaci\u00F3n en Instituciones de Inversi\u00F3n Colectiva."
			,null,null)
		,F ("F - Ganancias y p\u00E9rdidas patrimoniales no derivadas de transmisiones de elementos patrimoniales."
			,new String[] {"01","02","03","04"}
			,new String[] {			
				 "01 - Ganancias generadas en Espa\u00F1a"
				,"02 - P\u00E9rdidas generadas en Espa\u00F1a"
				,"03 - Ganancias generadas en el extranjero"
				,"04 - P\u00E9rdidas generadas en el extranjero"})
		,G ("G - Ganancias y p\u00E9rdidas patrimoniales derivadas de transmisiones de elementos patrimoniales."
			,new String[] {"01","02","03","04","05","06","07","08"}
			,new String[] {
				 "01 - Ganancias sin reducci\u00F3n generadas en Espa\u00F1a."
				,"02 - P\u00E9rdidas sin reducci\u00F3n generadas en Espa\u00F1a."
				,"03 - Ganancias sin reducci\u00F3n generadas en el extranjero."
				,"04 - P\u00E9rdidas sin reducci\u00F3n generadas en el extranjero."
				,"05 - Ganancias con reducci\u00F3n, relativas a elementos no afectos a actividades econ\u00F3micas generadas en Espa\u00F1a."
				,"06 - Ganancias con reducci\u00F3n, relativas a elementos no afectos a actividades econ\u00F3micas generadas en el extranjero."
				,"07 - Ganancias con reducci\u00F3n, relativas a elementos afectos a actividades econ\u00F3micas generadas en Espa\u00F1a."
				,"08 - Ganancias con reducci\u00F3n, relativas a elementos afectos a actividades econ\u00F3micas generadas en el extranjero."
					})
		,H ("H - Entidades que determinan la renta atribuible seg\u00FAn el Impuesto sobre Sociedades."
			,null,null)
		,I ("I - Deducciones de la Ley del Impuesto sobre la Renta de las Personas F\u00EDsicas."
			,new String[] {"01","02","03","04","05","06"}
			,new String[] {
				 "01 - Por protecci\u00F3n del Patrimonio Espa\u00F1ol y Mundial."
				,"02 - Por donativos, donaciones y aportaciones a determinadas entidades."
				,"03 - Por rentas obtenidas en Ceuta y Melilla."
				,"04 - Deducciones en actividades Econ\u00F3micas."
				,"05 - Por doble imposici\u00F3n internacional (importe efectivo satisfecho en el extranjero)."
				,"06 - Por inversi\u00F3n en empresas de nueva o reciente creaci\u00F3n."
			})
		,J ("J - Deducciones de la Ley del Impuesto sobre Sociedades."
			,new String[] {"01","02","03","04"}
			,new String[] {
				 "01 - Por doble imposici\u00F3n internacional (importe efectivo satisfecho en el extranjero)."
				,"02 - Deducciones con l\u00EDmite de cuota."
				,"03 - Deducci\u00F3n por donativos a entidades sin fines lucrativos."
				,"04 - Otras deducciones."
			})
		,K ("K - Retenciones e ingresos a cuenta soportados por la entidad."
			,new String[] {"01","02","03","04","05"}
			,new String[] {
				"01 - Por rendimientos del capital mobiliario."
				,"02 - Por arrendamiento de inmuebles urbanos (constituyan o no actividad econ\u00F3mica)."
				,"03 - Por rendimientos de actividades econ\u00F3micas (excepto arrendamientos de inmuebles urbanos)."
				,"04 - Por ganancias patrimoniales."
				,"05 - Por otros conceptos."
			})
		,L ("L - Exceso de rentas negativas obtenidas en pa\u00EDses sin convenio con Espa\u00F1a."
			,new String[] {"A","C","D","E","F","G","M"}
			,new String[] {
				 "A - Rendimientos del capital mobiliario."
				,"C - Rendimientos del capital inmobiliario."
				,"D - Rendimientos de actividades econ\u00F3micas."
				,"E - Rentas contabilizadas derivadas de la participaci\u00F3n en Instituciones de Inversi\u00F3n Colectiva."
				,"F - Ganancias y p\u00E9rdidas patrimoniales no derivadas de transmisiones de elementos patrimoniales."
				,"G - Ganancias y p\u00E9rdidas patrimoniales derivadas de transmisiones de elementos patrimoniales."
			})
		;

		private String description;
		private String[] subkeys;
		private String[] subkeyDescriptions;
		
		private Mod184IncomeKey(String description, String[] subkeys, String[] subkeyDescriptions) {
			this.description = description;
			this.subkeys = subkeys;
			this.subkeyDescriptions = subkeyDescriptions;
		}
		
		public String getDescription() {
			return description;
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
		public String[] getSubkeyDescriptions() {
			return subkeyDescriptions;
		}
	}

	private int tabIndex; 

	private ListBox key = new ListBox();
	private ListBox subkey = new ListBox();
	private CountryListBox country = new CountryListBox();
	private CheckBox vatAccrualPayment = new CheckBox();
	private DocumentTextBox granteeDocument = new DocumentTextBox();
	private TextBox granteeName = new TextBox();
	private DateBoxEx adqDate = new DateBoxEx();
	private ListBox activityType = new ListBox();
	private IntegerBox epigraph = new IntegerBox();
	private ListBox regime = new ListBox();
	private DoubleBox accountingResult = new DoubleBox();
	private DoubleBox expenses = new DoubleBox();
	private DoubleBox netYield = new DoubleBox();
	private DoubleBox reductionPercent = new DoubleBox();
	private DoubleBox deductionRightRent = new DoubleBox();
	private DoubleBox result = new DoubleBox();
	private DoubleBox decrease = new DoubleBox();
	private DoubleBox increase = new DoubleBox();
	private DoubleBox deductionBase = new DoubleBox();
	private DoubleBox retention = new DoubleBox();

	private ListBox location = new ListBox();
	private TextBox cadasdralReference = new TextBox();
	private DoubleBox staffExpenses = new DoubleBox();
	private DoubleBox assetAcquisition = new DoubleBox();
	private DoubleBox taxDeduction = new DoubleBox();
	private DoubleBox otherTaxDeduction = new DoubleBox();
	

	public Model1842016IncomePanel(Mod184Income income, IModel184IncomeCallback callback) {
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonWidthAll());
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonScrollArea());
		
		FlexTable tab1 = new FlexTable();
		tab1.getColumnFormatter().setWidth(0, "110px");
		tab1.getColumnFormatter().setWidth(1, "110px");
		tab1.getColumnFormatter().setWidth(2, WIDTH_150PX);
		tab1.getColumnFormatter().setWidth(3, "auto");
		
		tab1.setStyleName(AON.CSS.aonWidthAll());
		tab1.addStyleName(AON.CSS.aonNowrap());
		tab1.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
		tab1.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
		tab1.getFlexCellFormatter().setColSpan(0, 0, 4);
		tab1.setWidget(0, 0, new InlineLabel(AON.MSG.entityIncomes()));

 		tab1.setWidget(1, 0, new Model184SmallerLabel(AON.MSG.key()));
		tab1.setWidget(1, 1, new Model184SmallerLabel(AON.MSG.subkey()));
		tab1.setWidget(1, 2, new Model184SmallerLabel(AON.MSG.country()));

		subkey.setWidth("100px");

		key.setWidth("100px");
		for (Mod184IncomeKey k : Mod184IncomeKey.values()) {
			key.addItem(k.getDescription(),k.getValue());
		}
		
		Model1842016IncomePanel.setValue(key, subkey, income);
		
		key.addChangeHandler(event -> {
			subkey.clear();
			Mod184IncomeKey keyEnum = Mod184IncomeKey.values()[key.getSelectedIndex()];
			income.setKey( keyEnum.toString() );
			if (keyEnum.hasSubkeys()) {
				subkey.setEnabled(true);
				for (int i = 0; i < keyEnum.getSubkeys().length; i++) {
					subkey.addItem(keyEnum.getSubkeyDescriptions()[i],keyEnum.getSubkeys()[i]);
				}
				income.setSubKey(keyEnum.getSubkeys()[0]);
			} else {
				subkey.setEnabled(false);
				income.setSubKey(null);
			}
			enableWidgets( income );
			callback.onTableChanged(income);
		});
		tab1.setWidget(2, 0, key);
		
		subkey.addChangeHandler(event -> {
			Mod184IncomeKey keyEnum = Mod184IncomeKey.values()[key.getSelectedIndex()];
			if (keyEnum.hasSubkeys()) {
				int idx = subkey.getSelectedIndex() == -1 ? 0 : subkey.getSelectedIndex();
				income.setSubKey(keyEnum.getSubkeys()[idx]);
			} else {
				subkey.setEnabled(false);
				income.setSubKey(null);
			}
			enableWidgets( income );
			callback.onTableChanged(income);
		});
		tab1.setWidget(2, 1, subkey);
		
		country.setValue( Country.safeValueOf( income.getCountry() ));
		country.addChangeHandler(event -> {
			income.setCountry(country.getSelectedValue());
			callback.onValueChanged(income);
		});
		tab1.setWidget(2, 2, country);
		
		vatAccrualPayment.setText(AON.MSG.vatAccrualPayment184());
		vatAccrualPayment.setStyleName(AON.CSS.aonFontMedium());
		vatAccrualPayment.setValue( income.isVatAccrualPayment() );
		vatAccrualPayment.addClickHandler(event -> {
			income.setVatAccrualPayment(vatAccrualPayment.getValue());
			callback.onValueChanged(income);
		});
		tab1.setWidget(2, 3, vatAccrualPayment);
		

		FlexTable tab2 = new FlexTable();
		tab2.setStyleName(AON.CSS.aonWidthAll());
		tab2.addStyleName(AON.CSS.aonNowrap());
		
		tab2.getColumnFormatter().setWidth(0, WIDTH_150PX);
		tab2.getColumnFormatter().setWidth(1, WIDTH_200PX);
		tab2.getColumnFormatter().setWidth(2, WIDTH_200PX);
		tab2.getColumnFormatter().setWidth(3, "auto");

 		tab2.setWidget(0, 0, new Model184SmallerLabel(AON.MSG.granteeDocument()));
		tab2.setWidget(0, 1, new Model184SmallerLabel(AON.MSG.granteeName()));
		tab2.setWidget(0, 2, new Model184SmallerLabel(AON.MSG.adqDate()));

		
		granteeDocument.setValue(income.getGranteeDocument());
		granteeDocument.addValueChangeHandler(event -> {
			income.setGranteeDocument(granteeDocument.getValue());
			callback.onValueChanged(income);
		});
		tab2.setWidget(1, 0, granteeDocument);
		
		granteeName.setMaxLength(20); 
		granteeName.setVisibleLength(20);
		granteeName.setValue(income.getGranteeName());
		granteeName.addValueChangeHandler(event -> {
			income.setGranteeName(granteeName.getValue());
			callback.onValueChanged(income);
		});
		tab2.setWidget(1, 1, granteeName);
		
		adqDate.setValue(income.getAdqDate());
		adqDate.addValueChangeHandler(event -> {
			income.setAdqDate(adqDate.getValue());
			callback.onValueChanged(income);
		});
		tab2.setWidget(1, 2, adqDate);
		
 		tab2.setWidget(2, 0, new Model184SmallerLabel(AON.MSG.activityType()));
		tab2.setWidget(2, 1, new Model184SmallerLabel(AON.MSG.epigraph()));
		tab2.setWidget(2, 2, new Model184SmallerLabel(AON.MSG.regime()));
		
		activityType.addItem("-","");
		activityType.addItem("1 - Actividades empresariales de car\u00E1cter mercantil","1");
		activityType.addItem("2 - Actividades agr\u00EDcolas y ganaderas","2");
		activityType.addItem("3 - Otras actividades empresariales de car\u00E1cter no mercantil","3");
		activityType.addItem("4 - Actividades profesionales de car\u00E1cter art\u00EDstico o deportivo","4");
		activityType.addItem("5 - Restantes actividades profesionales","5");
		activityType.setWidth(WIDTH_150PX);
		activityType.setSelectedIndex(income.getActivityType());
		activityType.addChangeHandler(event -> {
			income.setActivityType((byte) activityType.getSelectedIndex());
			callback.onValueChanged(income);
		});
		tab2.setWidget(3, 0, activityType);
		
		epigraph.setMaxLength(4); 
		epigraph.setVisibleLength(4);
		epigraph.setValue( income.getEpigraph() );
		epigraph.addValueChangeHandler(event -> {
			income.setEpigraph( epigraph.getValue());
			callback.onValueChanged(income);
		});
		tab2.setWidget(3, 1, epigraph);

		enableWidgets(income);
		panel.add(tab1);
		panel.add(tab2);
		
		regime.addItem("-","");
		regime.addItem("1 - Estimaci\u00F3n directa modalidad normal","1");
		regime.addItem("2 - Estimaci\u00F3n directa modalidad simplificada","2");
		regime.addItem("3 - Estimaci\u00F3n objetiva","3");
		regime.setWidth(WIDTH_200PX);
		regime.setSelectedIndex(income.getRegime());
		regime.addChangeHandler(event -> {
			income.setRegime((byte) regime.getSelectedIndex());
			enableWidgets( income );
			callback.onValueChanged(income);
		});
		tab2.setWidget(3, 2, regime);
		
 		tab2.setWidget(4, 0, new Model184SmallerLabel(AON.MSG.accountingResult()));
		tab2.setWidget(4, 1, new Model184SmallerLabel(AON.MSG.expenses()));
		tab2.setWidget(4, 2, new Model184SmallerLabel(AON.MSG.netYieldExt()));
		
		accountingResult.setValue(income.getAccountingResult());
		accountingResult.addValueChangeHandler(event -> {
			income.setAccountingResult(accountingResult.getValue());
			callback.onValueChanged(income);
		});
		tab2.setWidget(5, 0, accountingResult);
		
		expenses.setValue(income.getExpenses());
		expenses.addValueChangeHandler(event -> {
			income.setExpenses(expenses.getValue());
			callback.onValueChanged(income);
		});
		tab2.setWidget(5, 1, expenses);
		
		netYield.setValue(income.getNetYield());
		netYield.addValueChangeHandler(event -> {
			income.setNetYield(netYield.getValue());
			callback.onValueChanged(income);
		});
		tab2.setWidget(5, 2, netYield);
		
		
 		tab2.setWidget(6, 0, new Model184SmallerLabel(AON.MSG.reductionPercent()));
		tab2.setWidget(6, 1, new Model184SmallerLabel(AON.MSG.deductionRightRent()));
		tab2.setWidget(6, 2, new Model184SmallerLabel(AON.MSG.profitLoss()));

		reductionPercent.setValue(income.getReductionPercent());
		reductionPercent.addValueChangeHandler(event -> {
			income.setReductionPercent(reductionPercent.getValue());
			callback.onValueChanged(income);
		});
		tab2.setWidget(7, 0, reductionPercent);

		deductionRightRent.setValue(income.getDeductionRightRent());
		deductionRightRent.addValueChangeHandler(event -> {
			income.setDeductionRightRent(deductionRightRent.getValue());
			callback.onValueChanged(income);
		});
		tab2.setWidget(7, 1, deductionRightRent);

		result.setValue(income.getResult());
		result.addValueChangeHandler(event -> {
			income.setResult(result.getValue());
			callback.onValueChanged(income);
		});
		tab2.setWidget(7, 2, result);
		
 		tab2.setWidget(8, 0, new Model184SmallerLabel(AON.MSG.adjustDecrease()));
		tab2.setWidget(8, 1, new Model184SmallerLabel(AON.MSG.adjustIncrease()));
		tab2.setWidget(8, 2, new Model184SmallerLabel(AON.MSG.deductionBase()));
		tab2.setWidget(8, 3, new Model184SmallerLabel(AON.MSG.retentionAccountDeposit()));
		
		decrease.setValue(income.getDecrease());
		decrease.addValueChangeHandler(event -> {
			income.setDecrease(decrease.getValue());
			callback.onValueChanged(income);
		});
		tab2.setWidget(9, 0, decrease);

		increase.setValue(income.getIncrease());
		increase.addValueChangeHandler(event -> {
			income.setIncrease(increase.getValue());
			callback.onValueChanged(income);
		});
		tab2.setWidget(9, 1, increase);

		deductionBase.setValue(income.getDeductionBase());
		deductionBase.addValueChangeHandler(event -> {
			income.setDeductionBase(deductionBase.getValue());
			callback.onValueChanged(income);
		});
		tab2.setWidget(9, 2, deductionBase);

		retention.setValue(income.getRetention());
		retention.addValueChangeHandler(event -> {
			income.setRetention(retention.getValue());
			callback.onValueChanged(income);
		});
		tab2.setWidget(9, 3, retention);
		
		tab2.getCellFormatter().setStyleName(10, 0, AON.CSS.aonBorderBottom());
		tab2.getCellFormatter().addStyleName(10, 0, AON.CSS.aonBold());
		tab2.getFlexCellFormatter().setColSpan(10, 0, 4);
		tab2.setWidget(10, 0, new InlineLabel(AON.MSG.buildingData()));

 		tab2.setWidget(11, 0, new Model184SmallerLabel(AON.MSG.buildingLocation()));
 		tab2.getFlexCellFormatter().setColSpan(11, 0, 2);
		tab2.setWidget(11, 1, new Model184SmallerLabel(AON.MSG.cadasdralReference()));
		tab2.getFlexCellFormatter().setColSpan(11, 1, 2);
		
		location.addItem("-","");
		location.addItem("1. Inmueble con referencia catastral situado en cualquier punto del territorio espa\u00F1ol, excepto Pa\u00EDs Vasco y Navarra.","1");
		location.addItem("2. Inmueble situado en la Comunidad Aut\u00F3noma del Pa\u00EDs Vasco.","2");
		location.addItem("3. Inmueble situado en la Comunidad Foral de Navarra.","3");
		location.addItem("4. Inmueble en cualquiera de las situaciones anteriores pero sin referencia catastral.","4");
		location.addItem("5. Inmueble situado en el extranjero.","5");
		location.setWidth(WIDTH_200PX);
		if (income.getLocation() != null) {
			try {
				location.setSelectedIndex( Integer.parseInt(income.getLocation()) );
			} catch (NumberFormatException e) {
				location.setSelectedIndex( 0 );
			}
		} else {
			location.setSelectedIndex( 0 );
		}
		location.addChangeHandler(event -> {
			income.setLocation(Integer.toString( location.getSelectedIndex()));
			callback.onValueChanged(income);
		});
 		tab2.setWidget(12, 0, location);
 		tab2.getFlexCellFormatter().setColSpan(12, 0, 2);

		cadasdralReference.setMaxLength(20); 
		cadasdralReference.setVisibleLength(20);
		cadasdralReference.setValue(income.getCadasdralReference());
		cadasdralReference.addValueChangeHandler(event -> {
			income.setCadasdralReference(cadasdralReference.getValue());
			callback.onValueChanged(income);
		});
 		tab2.setWidget(12, 1, cadasdralReference);
 		tab2.getFlexCellFormatter().setColSpan(12, 1, 2);
 		
		tab2.getCellFormatter().setStyleName(13, 0, AON.CSS.aonBorderBottom());
		tab2.getCellFormatter().addStyleName(13, 0, AON.CSS.aonBold());
		tab2.getFlexCellFormatter().setColSpan(13, 0, 4);
 		tab2.setWidget(13, 0, new Model184SmallerLabel(AON.MSG.expenseDetail()));
 		
		tab2.setWidget(14, 0, new Model184SmallerLabel(AON.MSG.staffExpenses()));
		tab2.setWidget(14, 1, new Model184SmallerLabel(AON.MSG.assetAcquisition()));
 		tab2.setWidget(14, 2, new Model184SmallerLabel(AON.MSG.taxDeduction()));
		tab2.setWidget(14, 3, new Model184SmallerLabel(AON.MSG.otherTaxDeduction()));
		
		staffExpenses.setValue(income.getStaffExpenses());
		staffExpenses.addValueChangeHandler(event -> {
			income.setStaffExpenses(staffExpenses.getValue());
			callback.onValueChanged(income);
		});
		tab2.setWidget(15, 0, staffExpenses);

		assetAcquisition.setValue(income.getAssetAcquisition());
		assetAcquisition.addValueChangeHandler(event -> {
			income.setAssetAcquisition(assetAcquisition.getValue());
			callback.onValueChanged(income);
		});
		tab2.setWidget(15, 1, assetAcquisition);
		
		taxDeduction.setValue(income.getTaxDeduction());
		taxDeduction.addValueChangeHandler(event -> {
			income.setTaxDeduction(taxDeduction.getValue());
			callback.onValueChanged(income);
		});
		tab2.setWidget(15, 2, taxDeduction);
		
		otherTaxDeduction.setValue(income.getOtherTaxDeduction());
		otherTaxDeduction.addValueChangeHandler(event -> {
			income.setOtherTaxDeduction(otherTaxDeduction.getValue());
			callback.onValueChanged(income);
		});
		tab2.setWidget(15, 3, otherTaxDeduction);

		scroll.setWidget(panel);
		setWidget(scroll);
	}

	@Override
	public int getTabIndex() {
		return tabIndex;
	}

	@Override
	public void setAccessKey(char key) {
		// Nothing
	}

	@Override
	public void setFocus(boolean focused) {
		key.setFocus(true);
	}

	@Override
	public void setTabIndex(int index) {
		tabIndex = index;
	}
	
	private static void setValue(ListBox key, ListBox subKey, Mod184Income detail) {
		if (AonStringUtils.isBlank( detail.getKey())) {
			detail.setKey(Mod184IncomeKey.A.toString());
		}
		Mod184IncomeKey keyEnum = Mod184IncomeKey.valueOf(detail.getKey());
		key.setSelectedIndex(keyEnum.ordinal());
		subKey.clear();
		if (keyEnum.hasSubkeys()) {
			subKey.setEnabled(true);
			for (int i = 0; i < keyEnum.getSubkeys().length; i++) {
				subKey.addItem(keyEnum.getSubkeyDescriptions()[i],keyEnum.getSubkeys()[i]);
				if (keyEnum.getSubkeys()[i].equals(detail.getSubKey())) {
					subKey.setSelectedIndex(i);
				}
			}
			subKey.setEnabled(true);
		} else {
			subKey.setEnabled(false);
		}
	}

	private void enableWidgets(Mod184Income income ) {
		country.setEnabled(
			    ("A".equals(income.getKey()) && "02".equals(income.getSubKey()))
			 || ("B".equals(income.getKey()) )
			 || ("C".equals(income.getKey()) && "02".equals(income.getSubKey()))
			 || ("D".equals(income.getKey()) && "02".equals(income.getSubKey()))
			 || ("F".equals(income.getKey()) && "03".equals(income.getSubKey())) 
			 || ("F".equals(income.getKey()) && "04".equals(income.getSubKey()))
			 || ("G".equals(income.getKey()) && "03".equals(income.getSubKey())) 
			 || ("G".equals(income.getKey()) && "04".equals(income.getSubKey())) 
			 || ("G".equals(income.getKey()) && "06".equals(income.getSubKey())) 
			 || ("G".equals(income.getKey()) && "08".equals(income.getSubKey())) 
			 || ("L".equals(income.getKey()) && "A".equals(income.getSubKey()))
			 || ("L".equals(income.getKey()) && "C".equals(income.getSubKey()))
			 || ("L".equals(income.getKey()) && "D".equals(income.getSubKey()))
			 || ("L".equals(income.getKey()) && "E".equals(income.getSubKey()))
			 || ("L".equals(income.getKey()) && "F".equals(income.getSubKey()))
			 || ("L".equals(income.getKey()) && "G".equals(income.getSubKey()))
			 || ("L".equals(income.getKey()) && "M".equals(income.getSubKey()))
		);
		
		granteeDocument.setEnabled( AonStringUtils.equals("B", income.getKey()) || AonStringUtils.equals("E", income.getKey()) );
		granteeName.setEnabled( AonStringUtils.equals("B", income.getKey()) || AonStringUtils.equals("E", income.getKey()) );
		adqDate.setEnabled( AonStringUtils.equals("E", income.getKey()) );
		
		epigraph.setEnabled("D".equals(income.getKey()));
		
		accountingResult.setEnabled(
			   ("A".equals(income.getKey()) && "01".equals(income.getSubKey()))
			|| ("A".equals(income.getKey()) && "02".equals(income.getSubKey()))
			|| ("B".equals(income.getKey()) )
			|| ("C".equals(income.getKey()) && "01".equals(income.getSubKey()))
			|| ("C".equals(income.getKey()) && "02".equals(income.getSubKey()))
			|| ("D".equals(income.getKey()) && "01".equals(income.getSubKey()))
			|| ("D".equals(income.getKey()) && "02".equals(income.getSubKey()))
			|| ("H".equals(income.getKey()) )									
				);
		activityType.setEnabled("D".equals(income.getKey()));
		expenses.setEnabled(
			   ("A".equals(income.getKey()) && "01".equals(income.getSubKey()))
			|| ("A".equals(income.getKey()) && "02".equals(income.getSubKey()))
			|| ("C".equals(income.getKey()) && "01".equals(income.getSubKey()))
			|| ("C".equals(income.getKey()) && "02".equals(income.getSubKey()))
			|| ("D".equals(income.getKey()) && "01".equals(income.getSubKey()) && income.getRegime() != 3)
			|| ("D".equals(income.getKey()) && "02".equals(income.getSubKey()) && income.getRegime() != 3)
			|| ("L".equals(income.getKey()) )
			);
		netYield.setEnabled(
			   ("A".equals(income.getKey()) && "01".equals(income.getSubKey())) 
			|| ("A".equals(income.getKey()) && "02".equals(income.getSubKey()))
			|| ("C".equals(income.getKey()) && "01".equals(income.getSubKey()))
			|| ("C".equals(income.getKey()) && "02".equals(income.getSubKey()))
			|| ("D".equals(income.getKey()) && "01".equals(income.getSubKey()))
			|| ("D".equals(income.getKey()) && "02".equals(income.getSubKey()))
			|| ("E".equals(income.getKey()))
			|| ("H".equals(income.getKey()))
			);
		reductionPercent.setEnabled(
			   ("A".equals(income.getKey()) && "03".equals(income.getSubKey()))
			|| ("C".equals(income.getKey()) && "03".equals(income.getSubKey()))
			);
		deductionRightRent.setEnabled(
			   ("A".equals(income.getKey()) && "03".equals(income.getSubKey()))
			|| ("C".equals(income.getKey()) && "03".equals(income.getSubKey()))
			|| ("D".equals(income.getKey()) && "03".equals(income.getSubKey()))
			);
		result.setEnabled(
			   "F".equals(income.getKey())
			|| "G".equals(income.getKey())
			);

		increase.setEnabled("E".equals(income.getKey()) || "H".equals(income.getKey()));
		decrease.setEnabled("E".equals(income.getKey()) || "H".equals(income.getKey()));
		deductionBase.setEnabled(
			   "I".equals(income.getKey())
			|| "J".equals(income.getKey())
			);
		retention.setEnabled(
			   "K".equals(income.getKey())
			);

		location.setEnabled("C".equals(income.getKey()));
		
		cadasdralReference.setEnabled(
			   "C".equals(income.getKey())
			);
		staffExpenses.setEnabled(
			"D".equals(income.getKey()) 
			&& ("01".equals(income.getSubKey()) || "02".equals(income.getSubKey())) 
			&& income.getRegime() != 3);
		assetAcquisition.setEnabled(
			"D".equals(income.getKey()) 
			&& ("01".equals(income.getSubKey()) || "02".equals(income.getSubKey())) 
			&& income.getRegime() != 3);
		taxDeduction.setEnabled(
			"D".equals(income.getKey()) 
			&& ("01".equals(income.getSubKey()) || "02".equals(income.getSubKey())) 
			&& income.getRegime() != 3);
		otherTaxDeduction.setEnabled(
			"D".equals(income.getKey()) 
			&& ("01".equals(income.getSubKey()) || "02".equals(income.getSubKey())) 
			&& income.getRegime() != 3);
		
	}

}
