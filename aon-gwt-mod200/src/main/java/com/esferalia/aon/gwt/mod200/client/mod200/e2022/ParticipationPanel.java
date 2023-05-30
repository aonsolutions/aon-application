package com.esferalia.aon.gwt.mod200.client.mod200.e2022;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ProvinceCountryListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyParticipation;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ParticipationPanel extends AonCustomDialog {	

	public static interface ParticipationPanelCallback {
		void onAccept(int index, Mod200CompanyParticipation cp, boolean modified);
		void onCancel();
		default void onClose() {
			this.onCancel();
		}
	}
	
	private AonDocumentTextBox document;
	private AonTextBox name;
	private ProvinceCountryListBox province;
	private AonDoubleBox percent;
	private AonDoubleBox nominalValue;
	private AonDoubleBox bookValue;
	private AonDoubleBox incomes;
	private AonDoubleBox aValue; // a) Corrección de valor incluida en pérdidas y ganancias del período                        
	private AonDoubleBox bValue; // b) Eliminación del deterioro contable incluido en P y G                                
	private AonDoubleBox cValue; // c) Eliminación del deterioro de valores repr. de partic. en el capital o fondos propios
	private AonDoubleBox dValue; // d) Ajuste por la disminución de valor originada por criterio de valor razonable        
	private AonDoubleBox eValue; // e) Efecto de la corrección valorativa en la BI del ejercicio (= a + b + c + d)         
	private AonDoubleBox fValue; // f) Saldo de correcciones fiscales                                                      
	private AonDoubleBox capital;
	private AonDoubleBox reserve;
	private AonDoubleBox otherAmounts;
	private AonDoubleBox result;
	
	private ParticipationPanelCallback callback;
	private int index;
	private boolean modified; 
	
	private FlowPanel rootPanel = new FlowPanel();

	public ParticipationPanel(ParticipationPanelCallback participationPanelCallback) {
		this.callback = participationPanelCallback;		
		setWidth("900px");
		setVisible(false);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setModal(true);
		setCaption(AON.MSG.participationsOut());
		paint();
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified(boolean modified) {
		this.modified = modified;		
	}

	public void dump(int index, Mod200CompanyParticipation companyParticipation, boolean isEnabled) {
		
		this.index = index;
		setModified(false);
		
		// Asignar valores
		this.document.setValue(companyParticipation.getDocument());
		this.name.setValue(companyParticipation.getName());
		this.province.setSelectedIndex(0);
		int idx = companyParticipation.getProvince();
		if (idx > 0 && idx < Province.values().length) {
			this.province.setSelectedIndex(idx);
		} else {
			Country c = Country.safeValueOf(companyParticipation.getCountry());
			if (c != null) {
				this.province.setSelectedIndex(c.ordinal() + Province.values().length);
			}
		}
		this.percent.setValue(companyParticipation.getPercent());
		this.nominalValue.setValue(companyParticipation.getNominalValue());
		this.bookValue.setValue(companyParticipation.getBookValue());
		this.incomes.setValue(companyParticipation.getIncomes());
		this.aValue.setValue(companyParticipation.getValueCorrection());
		this.bValue.setValue(companyParticipation.getAccountingElimination());
		this.cValue.setValue(companyParticipation.getValuesElimination());
		this.dValue.setValue(companyParticipation.getAdjustmentDecrease());
		this.eValue.setValue(companyParticipation.getCorrectionEffect());
		this.fValue.setValue(companyParticipation.getCorrectionsBalance());
		this.capital.setValue(companyParticipation.getCapital());
		this.reserve.setValue(companyParticipation.getReserve());
		this.otherAmounts.setValue(companyParticipation.getOtherAmounts());
		this.result.setValue(companyParticipation.getResult());
		
		// Habilitar/Deshabilitar 
		this.document.setEnabled(isEnabled);
		this.name.setEnabled(isEnabled);
		this.province.setEnabled(isEnabled);
		this.percent.setEnabled(isEnabled);
		this.nominalValue.setEnabled(isEnabled);
		this.bookValue.setEnabled(isEnabled);
		this.incomes.setEnabled(isEnabled);
		this.aValue.setEnabled(isEnabled);
		this.bValue.setEnabled(isEnabled);
		this.cValue.setEnabled(isEnabled);
		this.dValue.setEnabled(isEnabled);
		this.eValue.setEnabled(false); // e = a + b + c + d
		this.fValue.setEnabled(isEnabled);
		this.capital.setEnabled(isEnabled);
		this.reserve.setEnabled(isEnabled);
		this.otherAmounts.setEnabled(isEnabled);
		this.result.setEnabled(isEnabled);
		
	}

	@Override
	public void onClose() {
		onCancel();	
	}
	
	public void onCancel() {
		
		if (isModified()) {
			AonConfirmDialog cd = new AonConfirmDialog();
			cd.confirm(AON.MSG.cancelAction(), new AonConfirmDialogCallback() {
				
				@Override
				public void onCancel() {}
				
				@Override
				public void onAccept() {
					callback.onCancel();
					hide();
				}
			});
		} else {
			callback.onCancel();
			hide();
		}
		
	}
	
	public void onAccept() {
		Mod200CompanyParticipation companyParticipation = new Mod200CompanyParticipation();
		companyParticipation.setDocument(this.document.getValue());
		companyParticipation.setName(this.name.getValue());
		if (this.province.getSelectedIndex() < Province.values().length) {
			companyParticipation.setProvince(this.province.getSelectedIndex());
		} else {
			Country c = Country.values()[this.province.getSelectedIndex() - Province.values().length];
			companyParticipation.setCountry(c.getIso2());
		}
		companyParticipation.setPercent(this.percent.getValue());
		companyParticipation.setNominalValue(this.nominalValue.getValue());
		companyParticipation.setBookValue(this.bookValue.getValue());
		companyParticipation.setIncomes(this.incomes.getValue());
		
		companyParticipation.setValueCorrection(this.aValue.getValue());
		companyParticipation.setAccountingElimination(this.bValue.getValue());
		companyParticipation.setValuesElimination(this.cValue.getValue());
		companyParticipation.setAdjustmentDecrease(this.dValue.getValue());
		companyParticipation.setCorrectionEffect(this.aValue.getValue()+this.bValue.getValue()+this.cValue.getValue()+this.dValue.getValue());
		companyParticipation.setCorrectionsBalance(this.fValue.getValue());
		
		companyParticipation.setCapital(this.capital.getValue());
		companyParticipation.setReserve(this.reserve.getValue());
		companyParticipation.setOtherAmounts(this.otherAmounts.getValue());
		companyParticipation.setResult(this.result.getValue());
		
		callback.onAccept(index, companyParticipation, isModified());
		this.hide();
	}
	
	private void paint() {
		
		rootPanel.clear();
		
		Label label = new Label("Participaciones que a fin de per\u00EDodo sean igual o superior al 5% del capital o al 1% si se trata de valores que coticen en un mercado secundario organizado.");
		label.setStyleName(AON.CSS.aonMargin());
		label.addStyleName(AON.CSS.aonBold());
		label.addStyleName(AON.CSS.aonWidthAlmostAll());
		label.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(label);
		
		// Datos de la participada
		
		rootPanel.add(getSubtitle(AON.MSG.partMsg2()));
		
		AonDisplayTable tab1 = new AonDisplayTable();
		tab1.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab1.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(tab1);
		
		document = new AonDocumentTextBox();
		document.setVisibleLength(9);
		document.addValueChangeHandler(event -> {
			setModified(true);
		});
		
		name = new AonTextBox();
		name.setVisibleLength(40);
		name.setMaxLength(30);
		name.addValueChangeHandler(event -> {
			setModified(true);
		});
		
		province = new ProvinceCountryListBox();
		province.addChangeHandler(event -> {
			setModified(true);
		});		
		
		addRow(tab1, AON.MSG.nif(), document);
		addRow(tab1, AON.MSG.companyName(), name);
		addRow(tab1, AON.MSG.province() + "/" + AON.MSG.country(), province);
		
		// Datos en los registros de la declarante
		
		rootPanel.add(getSubtitle(AON.MSG.partMsg3()));
		
		AonDisplayTable tab2 = new AonDisplayTable();
		tab2.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab2.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(tab2);
		
		percent = new AonDoubleBox();
		percent.setMaxLength(6);
		percent.addValueChangeHandler(event -> {
			setModified(true);
		});
		
		nominalValue = new AonDoubleBox();
		nominalValue.addValueChangeHandler(event -> {
			setModified(true);
		});
		
		bookValue = new AonDoubleBox();
		bookValue.addValueChangeHandler(event -> {
			setModified(true);
		});
		
		incomes = new AonDoubleBox();
		incomes.addValueChangeHandler(event -> {
			setModified(true);
		});		
		
		addRow(tab2, AON.MSG.partMsg4(), percent);
		addRow(tab2, Mod2002022Key.P1501.getDescription(), nominalValue);
		addRow(tab2, Mod2002022Key.P1502.getDescription(), bookValue);
		addRow(tab2, Mod2002022Key.P1503.getDescription()+" (*)", incomes);
		
		addSmallLabel("(*) Deben incluirse tambi\u00E9n los datos correspondientes a los dividendos de sociedades que a fin de per\u00EDodo no cumplan el m\u00EDnimo de participaci\u00F3n (5% \u00F3 1% si cotizan), pero que s\u00ED lo alcanzaban cuando se percibi\u00F3 el dividendo.");
		
		// Correcciones valorativas por deterioro y cambio de valor razonable
		
		rootPanel.add(getSubtitle(AON.MSG.partMsg8()));
		
		addSmallLabel("(Cumplimente este apartado para todas las participaciones en las que se haya tenido un porcentaje superior al 5% -\u00F3 al 1% si cotizan- a lo largo del per\u00EDodo, y cuyo valor nominal supere los 100.000 euros, incluyendo por tanto las participaciones transmitidas en el ejercicio. DT 16 LIS)");
		
		AonDisplayTable tab3 = new AonDisplayTable();
		tab3.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab3.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(tab3);
		
		aValue = new AonDoubleBox();                         
		aValue.addValueChangeHandler(event -> {
			eValueCompute();
		});
		
		bValue = new AonDoubleBox();                                 
		bValue.addValueChangeHandler(event -> {
			eValueCompute();
		});

		cValue = new AonDoubleBox(); 
		cValue.addValueChangeHandler(event -> {
			eValueCompute();
		});
		
		dValue = new AonDoubleBox();         
		dValue.addValueChangeHandler(event -> {
			eValueCompute();
		});
		
		eValue = new AonDoubleBox();          

		fValue = new AonDoubleBox();                                                       
		fValue.addValueChangeHandler(event -> {
			setModified(true);
		});	
		
		addRow(tab3, Mod2002022Key.P1504.getDescription()+" (**)", aValue);
		addRow(tab3, Mod2002022Key.P1506.getDescription(), bValue);
		addRow(tab3, Mod2002022Key.P1809.getDescription(), cValue);
		addRow(tab3, Mod2002022Key.P1810.getDescription(), dValue);
		addRow(tab3, Mod2002022Key.P1507.getDescription(), eValue);
		addRow(tab3, Mod2002022Key.P1508.getDescription(), fValue);
		
		String text = "(**) Incluya la variaci\u00F3n del deterioro y, en general, los cambios valorativos con efectos sobre el resultado del per\u00EDodo, con el signo con que opere en el c\u00E1lculo del resultado. "
					+ "Incluya tambi\u00E9n, en su caso, el efecto sobre el \"resultado por enajenaci\u00F3n de participaciones\" por la aplicaci\u00F3n de los deterioros acumulados (y de los cambios valorativos acumulados, en general). "
					+ "Ponga el signo con que opere en la cuenta de P y G: (-) = deterioro; (+) = reversi\u00F3n del deterioro o aplicaci\u00F3n del deterioro por transmisi\u00F3n de la participaci\u00F3n. En el caso de cambios de valor positivos, signos opuestos a los indicados.";
		addSmallLabel(text);		
		
		// Datos adicionales de la participada
		
		rootPanel.add(getSubtitle(AON.MSG.partMsg13()));
		addSmallLabel("(S\u00F3lo se deber\u00E1 cumplimentar obligatoriamente este apartado si la entidad participada es extranjera y el deterioro sufrido se determina en relaci\u00F3n al patrimonio neto de la entidad participada)");
		
		AonDisplayTable tab4 = new AonDisplayTable();
		tab4.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab4.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(tab4);
		
		capital = new AonDoubleBox();
		capital.addValueChangeHandler(event -> {
			setModified(true);
		});
		
		reserve = new AonDoubleBox();
		reserve.addValueChangeHandler(event -> {
			setModified(true);
		});		

		otherAmounts = new AonDoubleBox();
		otherAmounts.addValueChangeHandler(event -> {
			setModified(true);
		});		

		result = new AonDoubleBox();
		result.addValueChangeHandler(event -> {
			setModified(true);
		});		
		
		addRow(tab4, AON.MSG.partMsg14(), capital);
		addRow(tab4, AON.MSG.partMsg15(), reserve);
		addRow(tab4, AON.MSG.partMsg16() + " (+,-)", otherAmounts );
		addRow(tab4, AON.MSG.partMsg17()+ " (+,-)", result);
		
		// Botones Aceptar y Cancelar
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonPadding());
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());

		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText(AON.MSG.accept());
		acceptButton.addClickHandler(event -> {
			onAccept();
		});
		
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(event -> {
			onCancel();
		});
		
		buttonsPanel.add(acceptButton);
		buttonsPanel.add(cancelButton);
		rootPanel.add(buttonsPanel);
		
		add(rootPanel);
		
	}
	
	private void eValueCompute() {
		eValue.setValue(aValue.getValue()+bValue.getValue()+cValue.getValue()+dValue.getValue());
		setModified(true);
	}
	
	private Label getSubtitle(String text) {
		Label subtitle = new Label(text);
		subtitle.setStyleName(AON.CSS.aonMarginTop());
		subtitle.addStyleName(AON.CSS.aonBold());
		subtitle.addStyleName(AON.CSS.aonWidthAlmostAll());
		subtitle.addStyleName(AON.CSS.aonBlockCenter());
		subtitle.addStyleName(AON.CSS.aonBorderBottom());
		return subtitle;
	}
	
	private void addRow(AonDisplayTable tab, String label, Widget widget) {
		tab.addRow()
			.addCell(new Label(label), AON.CSS.aonWidth600(), AON.CSS.aonBorderBottom())
			.addCell(widget);
	}
	
    private void addSmallLabel(String text) {
    	Label label = new Label(text);
		label.setWidth("95%");
		label.addStyleName(AON.CSS.aonFontSmaller());
		label.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(label);
    }

}

