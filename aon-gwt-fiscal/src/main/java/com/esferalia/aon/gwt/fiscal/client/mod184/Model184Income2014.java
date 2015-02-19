package com.esferalia.aon.gwt.fiscal.client.mod184;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Income;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Model184Income2014 extends ResizeComposite {
	

	interface Model184Income2014Binder extends UiBinder<Widget, Model184Income2014> {}
	private static Model184Income2014Binder MODEL184_INCOME_2014_BINDER 
		= GWT.create(Model184Income2014Binder.class);

	static interface IIncomeCallBack {
		void redrawList( Mod184Income income);
	}

	public static class Mod184IncomeSubKey {
		protected String subkey;
		protected String description;
		
		protected Mod184IncomeSubKey( String subkey, String description) {
			this.subkey = subkey;
			this.description = description;
		}
		public String getSubkey() {
			return subkey;
		}
		public String getDescription() {
			return description;
		}
	}
	
	public static enum Mod184IncomeKey {
		 A ("Rendimientos del capital mobiliario.", new Mod184IncomeSubKey[]
			{new Mod184IncomeSubKey("01","Rendimientos obtenidos en Espa\u00F1a")
			,new Mod184IncomeSubKey("02","Rendimientos obtenidos en el extranjero")
			,new Mod184IncomeSubKey("03","Reducciones aplicables")}
		   )
		,B ("Identificaci\u00F3n de la persona o entidad cesionaria de los "
			+ "capitales propios. En el supuesto de que la entidad en "
			+ "r\u00E9gimen de atribuci\u00F3n de rentas obtenga rentas de capit"
			+ "al mobiliario derivadas de la cesi\u00F3n a terceros de capi"
			+ "tales propios y alguno de los miembros de la entidad se"
			+ "a sujeto pasivo del Impuesto sobre Sociedades o contrib"
			+ "uyente por el Impuesto sobre la Renta de no Residentes "
			+ "con establecimiento permanente."
		   )
		,C ("Rendimientos del capital inmobiliario.", new Mod184IncomeSubKey[]
			{new Mod184IncomeSubKey("01","Rendimientos obtenidos en Espa\u00F1a")
			,new Mod184IncomeSubKey("02","Rendimientos obtenidos en el extranjero")
			,new Mod184IncomeSubKey("03","Reducciones aplicables")}
		   )
		,D ("Rendimientos de actividades econ\u00F3micas.", new Mod184IncomeSubKey[]
			{new Mod184IncomeSubKey("01","Rendimientos obtenidos en Espa\u00F1a")
			,new Mod184IncomeSubKey("02","Rendimientos obtenidos en el extranjero")
			,new Mod184IncomeSubKey("03","Importe del rendimiento con derecho a reducci\u00F3n")}
			)
		,E ("Rentas contabilizadas derivadas de la participaci\u00F3n en In"
			+ "stituciones de Inversi\u00F3n Colectiva. Cumplimentar\u00E1n este"
			+ " clave las entidades en r\u00E9gimen de atribuci\u00F3n de rentas"
			+ " que posean acciones o participaciones en Instituciones"
			+ " de Inversi\u00F3n Colectiva, y que cuenten entre sus miembr"
			+ "os con sujetos pasivos del Impuesto sobre Sociedades o "
			+ "contribuyentes por el Impuesto sobre la Renta de no Res"
			+ "identes con establecimiento permanente. Estas rentas se"
			+ " consignar\u00E1n exclusivamente con esta clave en la declar"
			+ "aci\u00F3n.")
		,F ("Ganancias y p\u00E9rdidas patrimoniales no derivadas de transm"
			+ "isiones de elementos patrimoniales. Con esta clave se d"
			+ "eclarar\u00E1 el importe total de las ganancias y p\u00E9rdidas p"
			+ "atrimoniales tanto generadas en Espa\u00F1a como en el extra"
			+ "njero, producidas en el ejercicio y no derivadas de tra"
			+ "nsmisiones de elementos patrimoniales. Se consignar\u00E1n l"
			+ "as ganancias y p\u00E9rdidas patrimoniales que efectivamente"
			+ " se imputen en el ejercicio.", new Mod184IncomeSubKey[]
			{new Mod184IncomeSubKey("01","Ganancias generadas en Espa\u00F1a")
			,new Mod184IncomeSubKey("02","P\u00E9rdidas generadas en Espa\u00F1a")
			,new Mod184IncomeSubKey("03","Ganancias generadas en el extranjero")
			,new Mod184IncomeSubKey("04","P\u00E9rdidas generadas en el extranjero")})
		,G ("Ganancias y p\u00E9rdidas patrimoniales derivadas de transmisi"
			+ "ones de elementos patrimoniales con periodo de generaci"
			+ "\u00F3n superior al a\u00F1o. Con esta clave se declarar\u00E1 el impo"
			+ "rte total de las ganancias y p\u00E9rdidas patrimoniales, ta"
			+ "nto generadas en Espa\u00F1a como en el extranjero, producid"
			+ "as en el ejercicio con ocasi\u00F3n de la transmisi\u00F3n de ele"
			+ "mentos patrimoniales, incluidas, en su caso, las deriva"
			+ "das de elementos patrimoniales afectos a actividades ec"
			+ "on\u00F3micas, que hubieran sido adquiridos con m\u00E1s de una a"
			+ "\u00F1o de antelaci\u00F3n a la fecha de transmisi\u00F3n, as\u00ED como aq"
			+ "uellas que deriven de las mejoras realizadas sobre esto"
			+ "s elementos con la misma antelaci\u00F3n o de derechos de su"
			+ "scripci\u00F3n que correspondan a valores adquiridos igualme"
			+ "nte con m\u00E1s de un a\u00F1o  de antelaci\u00F3n a la fecha de su t"
			+ "ransmisi\u00F3n.Se consignar\u00E1n las ganancias y p\u00E9rdidas patr"
			+ "imoniales que efectivamente se imputen en el ejercicio.", new Mod184IncomeSubKey[]
			{new Mod184IncomeSubKey("01","Ganancias sin reducci\u00F3n generadas en Espa\u00F1a")
			,new Mod184IncomeSubKey("02","P\u00E9rdidas sin reducci\u00F3n generadas en Espa\u00F1a")
			,new Mod184IncomeSubKey("03","Ganancias sin reducci\u00F3n generadas en el extranjero")
			,new Mod184IncomeSubKey("04","P\u00E9rdidas sin reducci\u00F3n generadas en el extranjero")
			,new Mod184IncomeSubKey("05","Ganancias con reducci\u00F3n, relativas a elementos no "
					+ "afectos a actividades econ\u00F3micas generadas en Espa\u00F1a")
			,new Mod184IncomeSubKey("06","Ganancias con reducci\u00F3n, relativas a elementos no "
					+ "afectos a actividades econ\u00F3micas generadas en el extranjero")
			,new Mod184IncomeSubKey("07","Ganancias con reducci\u00F3n, relativas a elementos "
					+ "afectos a actividades econ\u00F3micas generadas en Espa\u00F1a")
			,new Mod184IncomeSubKey("08","Ganancias con reducci\u00F3n, relativas a elementos "
					+ "afectos a actividades econ\u00F3micas generadas en el extranjero")})
		,H ("Entidades que determinan la renta atribuible seg\u00FAn el Imp"
			+ "uesto sobre Sociedades. Cuando todos los miembros de la"
			+ " entidad en r\u00E9gimen de atribuci\u00F3n de rentas sean sujeto"
			+ "s pasivos del Impuesto sobre Sociedades o contribuyente"
			+ "s por el Impuesto sobre la Renta de no Residentes con e"
			+ "stablecimiento permanente, la renta atribuible a los so"
			+ "cios, herederos, comuneros o part\u00EDcipes se determinar\u00E1 "
			+ "de acuerdo con las normas del Impuesto sobre Sociedades.")
		,I ("Deducciones Ley Impuesto Renta Personas F\u00EDsicas. Se  har\u00E1"
			+ " constar el importe  que constituya base de deducci\u00F3n p"
			+ "or alguno de los conceptos previstos en la Ley del IRPF.", new Mod184IncomeSubKey[]
			{new Mod184IncomeSubKey("01","Por protecci\u00F3n Patrimonio Espa\u00F1ol y Mundial")
			,new Mod184IncomeSubKey("02","Por donativos, donaciones y aportaciones a "
					+ "determinadas entidades")
			,new Mod184IncomeSubKey("03","Por rentas obtenidas en Ceuta y Melilla")
			,new Mod184IncomeSubKey("04","Deducciones en actividades econ\u00F3micas")
			,new Mod184IncomeSubKey("05","Por doble imposici\u00F3n internacional (importe "
					+ "efectivo satisfecho en el extranjero)")})
		,J ("Deducciones Ley Impuesto Sociedades. Deber\u00E1n cumplimentar"
			+ " esta clave las entidades en r\u00E9gimen de atribuci\u00F3n de r"
			+ "entas que tengan socios, comuneros, herederos o part\u00EDci"
			+ "pes que sean sujetos pasivos del Impuesto sobre Socieda"
			+ "des o contribuyentes del Impuesto sobre la Renta de no "
			+ "Residentes con establecimiento permanente.", new Mod184IncomeSubKey[]
			{new Mod184IncomeSubKey("01","Por doble imposici\u00F3n interna")
			,new Mod184IncomeSubKey("02","Por doble imposici\u00F3n internacional(importe "
					+ "efectivo satisfecho en el extranjero)")
			,new Mod184IncomeSubKey("03","Deducciones con l\u00EDmite de cuota")
			,new Mod184IncomeSubKey("04","Deducci\u00F3n art\u00EDculo 42")
			,new Mod184IncomeSubKey("05","Deducci\u00F3n por donativos a entidades sin fines lucrativos")
			,new Mod184IncomeSubKey("06","Otras deducciones")})
		,K ("Retenciones e ingresos a cuenta soportados por la entidad"
			+ ". Se consignar\u00E1n los importes de las retenciones e ingr"
			+ "esos a cuenta del Impuesto sobre la Renta de las Person"
			+ "as F\u00EDsicas, del Impuesto sobre Sociedades o del Impuest"
			+ "o sobre la Renta de no Residentes que le hubieran sido "
			+ "practicadas  a la entidad en r\u00E9gimen de atribuci\u00F3n de r"
			+ "entas, durante el periodo objeto de declaraci\u00F3n.", new Mod184IncomeSubKey[]
			{new Mod184IncomeSubKey("01","Por rendimientos del capital mobiliario. Suma "
					+ "de retenciones e ingresos a cuenta")
			,new Mod184IncomeSubKey("02","Por arrendamiento de inmuebles urbanos (constituyan "
					+ "o no actividad econ\u00F3mica) Suma de retenciones e ingresos a cuenta")
			,new Mod184IncomeSubKey("03","Por rendimientos de actividades econ\u00F3micas (excepto "
					+ "arrendamientos de inmuebles urbanos) Suma de retenciones e "
					+ "ingresos a cuenta")
			,new Mod184IncomeSubKey("04","Por ganancias patrimoniales Suma de retenciones "
					+ "e ingresos a cuenta")
			,new Mod184IncomeSubKey("05","Por otros conceptos. Suma de retenciones e "
					+ "ingresos a cuenta")})
		,L ("Exceso de rentas negativas obtenidas en pa\u00EDses sin conven"
			+ "io con Espa\u00F1a. Cuando el pa\u00EDs en el que se obtengan las"
			+ " rentas no tenga suscrito convenio con Espa\u00F1a para evit"
			+ "ar la doble imposici\u00F3n con cl\u00E1usula de intercambio de i"
			+ "nformaci\u00F3n, no se computar\u00E1n las rentas negativas que e"
			+ "xcedan de las positivas siempre que procedan de la mism"
			+ "a fuente y el mismo pa\u00EDs.", new Mod184IncomeSubKey[]
			{new Mod184IncomeSubKey("A","Rendimientos del capital mobiliario")
			,new Mod184IncomeSubKey("C","Rendimientos del capital inmobiliario")
			,new Mod184IncomeSubKey("D","Rendimientos de actividades econ\u00F3micas")
			,new Mod184IncomeSubKey("E","Rentas contabilizadas derivadas de la participaci\u00F3n "
					+ "en Instituciones de Inversi\u00F3n Colectiva")
			,new Mod184IncomeSubKey("F","Ganancias y p\u00E9rdidas patrimoniales no derivadas de "
					+ "transmisiones de elementos patrimoniales")
			,new Mod184IncomeSubKey("G","Ganancias y p\u00E9rdidas patrimoniales derivadas de "
					+ "transmisiones de elementos patrimoniales con periodo de generaci\u00F3n "
					+ "superior al a\u00F1o con periodo de generaci\u00F3n superior al a\u00F1o")
			,new Mod184IncomeSubKey("M","Ganancias y p\u00E9rdidas patrimoniales derivadas de "
					+ "transmisiones de elementos patrimoniales con periodo de generaci\u00F3n "
					+ "igual o inferior al a\u00F1o")})
		,M ("Ganancias y p\u00E9rdidas patrimoniales derivadas de transmisi"
			+ "ones de elementos patrimoniales con periodo de generaci"
			+ "\u00F3n igual o inferior al a\u00F1o. Con esta clave se declarar\u00E1"
			+ " el importe total de las ganancias y p\u00E9rdidas patrimoni"
			+ "ales, tanto generadas en Espa\u00F1a como en el extranjero, "
			+ "producidas en el ejercicio con ocasi\u00F3n de la transmisi\u00F3"
			+ "n de elementos patrimoniales, incluidos los afectos a a"
			+ "ctividades econ\u00F3micas, adquiridos con un a\u00F1o o menos de"
			+ " antelaci\u00F3n a la fecha de transmisi\u00F3n, as\u00ED como aquella"
			+ "s que deriven de las mejoras realizadas sobre estos ele"
			+ "mentos con la misma antelaci\u00F3n o de derechos de suscrip"
			+ "ci\u00F3n que correspondan a valores adquiridos con un a\u00F1o o"
			+ " menos de antelaci\u00F3n. Se consignar\u00E1n las ganancias y p\u00E9"
			+ "rdidas patrimoniales que efectivamente se imputen en el"
			+ " ejercicio.", new Mod184IncomeSubKey[]
			{new Mod184IncomeSubKey("01","Ganancias generadas en Espa\u00F1a")
			,new Mod184IncomeSubKey("02","P\u00E9rdidas generadas en Espa\u00F1a")
			,new Mod184IncomeSubKey("03","Ganancias generadas en el extranjero")
			,new Mod184IncomeSubKey("04","P\u00E9rdidas generadas en el extranjero")})
		;
		
		private String description;
		private Mod184IncomeSubKey[] subkeys;
		
		private Mod184IncomeKey(String description, Mod184IncomeSubKey[] subkeys) {
			this.description = description;
			this.subkeys = subkeys;
		}
		private Mod184IncomeKey(String description) {
			this(description,null);
		}
		
		public String getValue() {
			return toString();
		}
		public String getDescription() {
			return description;
		}
		public boolean hasSubkeys() {
			return this.subkeys != null;
		}
		public Mod184IncomeSubKey[] getSubkeys() {
			return subkeys;
		}
	}

	public class IncomeKeyListBox extends ListBox {

		public IncomeKeyListBox() {
			subKey = new ListBox();
			subKey.setWidth("45px");

			setWidth("40px");
			this.addItem("-","");
			for (Mod184IncomeKey key : Mod184IncomeKey.values()) {
				this.addItem(key.getValue(),key.getValue());
			}

			addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					int idx = getSelectedIndex();
					if (idx == 0) {
						income.setKey(null);
						income.setSubKey(null);
						subKey.clear();	
					} else {
						Mod184IncomeKey keyEnum = Mod184IncomeKey.values()[idx-1];
						income.setKey(keyEnum.getValue());
						income.setSubKey(null);
						subKey.clear();
						if (keyEnum.hasSubkeys()) {
							subKey.setEnabled(true);
							subKey.addItem("-","");
							for (int i = 0; i < keyEnum.getSubkeys().length; i++) {
								Mod184IncomeSubKey sk = keyEnum.getSubkeys()[i];
								subKey.addItem(sk.getSubkey(),sk.getSubkey());
							}
						} else {
							subKey.setEnabled(false);
						}
					}
					enableWidgets();
					income.setDirty(true);
				}
			});
		}

		public ListBox getSubKey() {
			return subKey;
		}

		public void setValue(String key, String subkey) {
			if (key != null) {
				Mod184IncomeKey keyEnum = Mod184IncomeKey.valueOf(key);
				setSelectedIndex(keyEnum.ordinal()+1);
				getSubKey().clear();
				if (keyEnum.hasSubkeys()) {
					getSubKey().setEnabled(true);
					getSubKey().addItem("-","");
					for (int i = 0; i < keyEnum.getSubkeys().length; i++) {
						Mod184IncomeSubKey sk = keyEnum.getSubkeys()[i];
						getSubKey().addItem(sk.getSubkey(),sk.getSubkey());
						if (keyEnum.getSubkeys()[i].subkey.equals(subkey)) {
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

	Mod184Income income;
	private IIncomeCallBack callback;
	
	@UiField
	Button deleteDetailButton;
	@UiField
	Button restoreDeletedButton;

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
	DoubleTextBox increase;
	@UiField
	DoubleTextBox decrease;
	@UiField
	DoubleTextBox accountingResult;
	@UiField
	DoubleTextBox expenses;
	@UiField
	DoubleTextBox netYield;
	@UiField
	DoubleTextBox reductionPercent;
	@UiField
	DoubleTextBox deductionRightRent;
	@UiField
	DoubleTextBox result;
	@UiField
	DoubleTextBox deductionBase;
	@UiField
	DoubleTextBox retention;
	
	
	public Model184Income2014() {
		key = new IncomeKeyListBox();
		
		Widget ui = MODEL184_INCOME_2014_BINDER.createAndBindUi(this);
		initWidget(ui);
		
		regime.addItem("-","");
		regime.addItem("1 - Estimación directa modalidad normal","1");
		regime.addItem("2 - Estimación directa modalidad simplificada","2");
		regime.addItem("3 - Estimación objetiva","3");
		regime.setWidth("100px");

		activityType.addItem("-","");
		activityType.addItem("1 - Actividades empresariales de carácter mercantil","1");
		activityType.addItem("2 - Actividades agrícolas y ganaderas","2");
		activityType.addItem("3 - Otras actividades empresariales de carácter no mercantil","3");
		activityType.addItem("4 - Actividades profesionales de carácter artístico o deportivo","4");
		activityType.addItem("5 - Restantes actividades profesionales","5");
		activityType.setWidth("100px");
		
		netYield.setEnabled(false);

	}

	public void setCallback(IIncomeCallBack callback) {
		this.callback = callback;
	}
	
	public void setIncome(Mod184Income income) {
		this.income = income;
		key.setValue(income.getKey(), income.getSubKey());
		country.setSelectedIndex( 
				AonStringUtils.isBlank( income.getCountry() )
				? 0 
				: Country.valueOf(income.getCountry()).ordinal() + 1 );
		regime.setSelectedIndex(income.getRegime());
		activityType.setSelectedIndex(income.getActivityType());
		epigraph.setValue( income.getEpigraph()==null?null:Integer.toString(income.getEpigraph()) );
		granteeDocument.setValue(income.getGranteeDocument());
		granteeName.setValue(income.getGranteeName());
		adqDate.setValue(income.getAdqDate());
		increase.setValue(income.getIncrease());
		decrease.setValue(income.getDecrease());
		accountingResult.setValue(income.getAccountingResult());
		expenses.setValue(income.getExpenses());
		netYield.setValue(income.getNetYield());
		reductionPercent.setValue(income.getReductionPercent());
		deductionRightRent.setValue(income.getDeductionRightRent());
		result.setValue(income.getResult());
		deductionBase.setValue(income.getDeductionBase());
		retention.setValue(income.getRetention());
		
		enableWidgets();
		restoreDeletedButton.setVisible(income.isDeleted());
		deleteDetailButton.setVisible(!income.isDeleted());
	}

	@UiHandler("deleteDetailButton")
	void onDeleteDetailButtonClick(ClickEvent event) {
		income.setDeleted(true);
		restoreDeletedButton.setVisible(true);
		deleteDetailButton.setVisible(false);
		callback.redrawList(income);
	}

	@UiHandler("restoreDeletedButton")
	void onRestoreDeletedButtonClick(ClickEvent event) {
		income.setDeleted(false);
		if (!income.isDirty()) {
			restoreDeletedButton.setVisible(false);
			deleteDetailButton.setVisible(true);
			callback.redrawList(income);
		}
	};

	@UiHandler("subKey")
	void onChangeSubKey(ChangeEvent event) {
		String subk = ((key.getSubKey().getSelectedIndex() == -1) ? null : key
				.getSubKey().getValue(key.getSubKey().getSelectedIndex()));
		income.setSubKey(subk);
		enableWidgets();
		income.setDirty(true);
	}
	@UiHandler("country")
	void onChangeCountry(ChangeEvent event) {
		income.setCountry(country.getValue(country.getSelectedIndex()));
		income.setDirty(true);
	}
	@UiHandler("regime")
	void onChangeRegime(ChangeEvent event) {
		income.setRegime((byte) regime.getSelectedIndex());
		enableWidgets();
		income.setDirty(true);
	}
	@UiHandler("activityType")
	void onChangeActivityType(ChangeEvent event) {
		income.setActivityType((byte) activityType.getSelectedIndex());
		income.setDirty(true);
	}
	@UiHandler("epigraph")
	void onChangeEpigraph(ChangeEvent event) {
		try {
			income.setEpigraph( Integer.parseInt(epigraph.getValue()) );
		} catch (NumberFormatException e ) {
			income.setEpigraph( null );
		}
		income.setDirty(true);
	}
	@UiHandler("granteeDocument")
	void onChangeGranteeDocument(ChangeEvent event) {
		income.setGranteeDocument(granteeDocument.getValue());
		income.setDirty(true);
	}
	@UiHandler("granteeName")
	void onChangeGranteeName(ChangeEvent event) {
		income.setGranteeName(granteeName.getValue());
		income.setDirty(true);
	}
	@UiHandler("adqDate")
	void onChangeAdqDate(ValueChangeEvent<Date> event) {
		income.setAdqDate(adqDate.getValue());
		income.setDirty(true);
	}
	@UiHandler("increase")
	void onChangeIncrease(ChangeEvent event) {
		income.setIncrease(increase.getDoubleValue());
		income.setDirty(true);
	}
	@UiHandler("decrease")
	void onChangeDecrease(ChangeEvent event) {
		income.setDecrease(decrease.getDoubleValue());
		income.setDirty(true);
	}
	@UiHandler("accountingResult")
	void onChangeAccountingResult(ChangeEvent event) {
		income.setAccountingResult(accountingResult.getDoubleValue());
		income.setDirty(true);
		refreshNetYield();
	}
	@UiHandler("expenses")
	void onChangeExpenses(ChangeEvent event) {
		income.setExpenses(expenses.getDoubleValue());
		income.setDirty(true);
		refreshNetYield();
	}
	private void refreshNetYield() {
		income.setNetYield( AonMathUtils.round(income.getAccountingResult() - income.getExpenses()));
		netYield.setValue(income.getNetYield());
	}
	@UiHandler("netYield")
	void onChangeNetYield(ChangeEvent event) {
		income.setNetYield(netYield.getDoubleValue());
		income.setDirty(true);
	}
	@UiHandler("reductionPercent")
	void onChangeReductionPercent(ChangeEvent event) {
		income.setReductionPercent(reductionPercent.getDoubleValue());
		income.setDirty(true);
	}
	@UiHandler("deductionRightRent")
	void onChangeDeductionRightRent(ChangeEvent event) {
		income.setDeductionRightRent(deductionRightRent.getDoubleValue());
		income.setDirty(true);
	}
	@UiHandler("result")
	void onChangeResult(ChangeEvent event) {
		income.setResult(result.getDoubleValue());
		income.setDirty(true);
	}
	@UiHandler("deductionBase")
	void onChangeDeductionBase(ChangeEvent event) {
		income.setDeductionBase(deductionBase.getDoubleValue());
		income.setDirty(true);
	}

	@UiHandler("retention")
	void onChangeRetention(ChangeEvent event) {
		income.setRetention(retention.getDoubleValue());
		income.setDirty(true);
	}
	
	private void enableWidgets() {
		country.setEnabled(
			    ("A".equals(income.getKey()) 
			    	 && "02".equals(income.getSubKey()))
			 || ("B".equals(income.getKey()) )
			 || ("C".equals(income.getKey()) 
					 && "02".equals(income.getSubKey()))
			 || ("D".equals(income.getKey()) 
					 && "02".equals(income.getSubKey()))
			 || ("F".equals(income.getKey()) 
					 && ("03".equals(income.getSubKey()) 
                      || "04".equals(income.getSubKey())))
			 || ("G".equals(income.getKey()) 
					 && ("03".equals(income.getSubKey()) 
                      || "04".equals(income.getSubKey()) 
                      || "06".equals(income.getSubKey())
                      || "08".equals(income.getSubKey())) )
			 || ("L".equals(income.getKey()) 
					 && ("A".equals(income.getSubKey()) 
	                  || "C".equals(income.getSubKey()) 
	                  || "D".equals(income.getSubKey())
	                  || "E".equals(income.getSubKey())
	                  || "F".equals(income.getSubKey())
	                  || "G".equals(income.getSubKey())
	                  || "M".equals(income.getSubKey())) )
			 || ("M".equals(income.getKey()) 
					 && ("03".equals(income.getSubKey()) 
					  || "04".equals(income.getSubKey())))
				);
		
		epigraph.setEnabled("D".equals(income.getKey())); 
		granteeDocument.setEnabled("B".equals(income.getKey()) || "E".equals(income.getKey()));
		granteeName.setEnabled("B".equals(income.getKey()) || "E".equals(income.getKey()));
		adqDate.setEnabled("E".equals(income.getKey()));
		increase.setEnabled("E".equals(income.getKey()) || "H".equals(income.getKey()));
		decrease.setEnabled("E".equals(income.getKey()) || "H".equals(income.getKey()));
		accountingResult.setEnabled(
			   ("A".equals(income.getKey()) 
					  && ("01".equals(income.getSubKey()) 
					   || "02".equals(income.getSubKey())))
			|| ("B".equals(income.getKey()) )
			|| ("C".equals(income.getKey()) 
						  && ("01".equals(income.getSubKey()) 
						   || "02".equals(income.getSubKey())))
			|| ("D".equals(income.getKey()) && "01".equals(income.getSubKey()) && income.getRegime() == 1)
			|| ("D".equals(income.getKey()) && "01".equals(income.getSubKey()) && income.getRegime() == 2)
			|| ("D".equals(income.getKey()) && "02".equals(income.getSubKey()))
			|| ("H".equals(income.getKey()) )									
				);
		expenses.setEnabled(
				   ("A".equals(income.getKey()) 
							  && ("01".equals(income.getSubKey()) 
							   || "02".equals(income.getSubKey())))
				|| ("C".equals(income.getKey()) 
						  && ("01".equals(income.getSubKey()) 
						   || "02".equals(income.getSubKey())))
				|| ("D".equals(income.getKey()) && "01".equals(income.getSubKey()) && income.getRegime() == 1)
				|| ("D".equals(income.getKey()) && "01".equals(income.getSubKey()) && income.getRegime() == 2)
				|| ("L".equals(income.getKey()) )
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
				|| "M".equals(income.getKey())
				);
		
		deductionBase.setEnabled(
				   "I".equals(income.getKey())
				|| "J".equals(income.getKey())
				);
		retention.setEnabled(
				   "K".equals(income.getKey())
				);
		
	}
	
	
}
