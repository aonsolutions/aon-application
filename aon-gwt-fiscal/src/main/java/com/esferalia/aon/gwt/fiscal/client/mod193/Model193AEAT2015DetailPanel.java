package com.esferalia.aon.gwt.fiscal.client.mod193;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.fiscal.client.mod193.Model193AEATDetail2015.IModel193DetailCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.esferalia.aon.occam.api.model.type.Mod193Key;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

public class Model193AEAT2015DetailPanel extends SimpleLayoutPanel implements Focusable {
	
	private static class MediumLabel extends InlineLabel {
		private MediumLabel(String label) {
			super(label);
			setStyleName(AON.AON_CSS.aonFontMedium());
		}
	}
	private int tabIndex; 
	private DocumentTextBox document;
	
	public Model193AEAT2015DetailPanel(Mod193Detail detail, IModel193DetailCallback callback) {
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		
		FlexTable tab1 = new FlexTable();
		panel.add(tab1);
		tab1.getColumnFormatter().setWidth(0, "100px");
		tab1.getColumnFormatter().setWidth(1, "100px");
		tab1.getColumnFormatter().setWidth(2, "100px");
		tab1.getColumnFormatter().setWidth(3, "300px");
		tab1.getColumnFormatter().setWidth(4, "auto");
		tab1.setStyleName(AON.AON_CSS.aonWidthAll());
		tab1.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab1.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonBorderBottom());
		tab1.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
		tab1.getFlexCellFormatter().setColSpan(0, 0, 5);
		tab1.setWidget(0, 0, new InlineLabel(AON.MSG.perceptionData()));

 		tab1.setWidget(1, 1, new MediumLabel(AON.MSG.receiverDocument()));
		tab1.setWidget(1, 2, new MediumLabel(AON.MSG.representativeDocument()));
		tab1.setWidget(1, 3, new MediumLabel(AON.MSG.fullName()));
		tab1.setWidget(1, 4, new MediumLabel(AON.MSG.province()));
		
		CheckBox pending = new CheckBox(AON.MSG.pending());
		pending.setValue(detail.isPending());
		pending.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				detail.setPending(pending.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab1.setWidget(2, 0, pending);
		
		document = new DocumentTextBox();
		document.setValue(detail.getDocument());
		document.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setDocument(document.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab1.setWidget(2, 1, document);
		
		DocumentTextBox representativeDocument = new DocumentTextBox();
		representativeDocument.setValue(detail.getRepresentativeDocument());
		representativeDocument.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setRepresentativeDocument(representativeDocument.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab1.setWidget(2, 2, representativeDocument);
		
		TextBox name = new TextBox();
		name.setVisibleLength(40);
		name.setMaxLength(40);
		name.setStyleName(AON.AON_CSS.aonInputText());
		name.setValue(detail.getName());
		name.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setName(name.getValue());
				callback.onNameChanged(detail);
			}
		});
		tab1.setWidget(2, 3, name);
		
		FlexTable tab2 = new FlexTable();
		panel.add(tab2);
		tab2.getColumnFormatter().setWidth(0, "60px");
		tab2.getColumnFormatter().setWidth(1, "60px");
		tab2.getColumnFormatter().setWidth(2, "100px");
		tab2.getColumnFormatter().setWidth(3, "120px");
		tab2.getColumnFormatter().setWidth(4, "60px");
		tab2.getColumnFormatter().setWidth(5, "130px");
		tab2.getColumnFormatter().setWidth(6, "auto");
		tab2.setStyleName(AON.AON_CSS.aonWidthAll());
		tab2.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab2.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonBorderBottom());
		tab2.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
		tab2.getFlexCellFormatter().setColSpan(0, 0, 7);
		tab2.setWidget(0, 0, new InlineLabel(AON.MSG.operationData()));

 		tab2.setWidget(1, 0, new MediumLabel(AON.MSG.key()));
		tab2.setWidget(1, 1, new MediumLabel(AON.MSG.nature()));
		tab2.setWidget(1, 2, new Label());
		tab2.setWidget(1, 3, new MediumLabel(AON.MSG.province()));
		tab2.setWidget(1, 4, new MediumLabel(AON.MSG.keyCode()));
		tab2.setWidget(1, 5, new MediumLabel(AON.MSG.issuingCode()));
		tab2.setWidget(1, 6, new MediumLabel(AON.MSG.accrualYear()));

		final ListBox nature = new ListBox();
		nature.setWidth("40px");

		final ListBox key = new ListBox();
		key.setWidth("40px");
		for (Mod193Key k : Mod193Key.values()) {
			key.addItem(AonStringUtils.abbreviate(k.getDescription(),150), k.getValue());
		}
		
		Model193AEAT2015DetailPanel.setValue(key, nature, detail);
		
		key.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				nature.clear();
				Mod193Key keyEnum = Mod193Key.values()[key.getSelectedIndex()];
				detail.setKey( keyEnum.toString() );
				nature.setEnabled(true);
				for (int i = 0; i < keyEnum.getNatures().length; i++) {
					nature.addItem(AonStringUtils.abbreviate(keyEnum.getNatureDescriptions()[i],150), keyEnum.getNatures()[i]);
				}
				detail.setNature(keyEnum.getNatures()[0]);
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(2, 0, key);

		nature.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				Mod193Key keyEnum = Mod193Key.values()[key.getSelectedIndex()];
				int idx = nature.getSelectedIndex() == -1 ? 0 : nature.getSelectedIndex();
				detail.setNature(keyEnum.getNatures()[idx]);
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(2, 1, nature);

		CheckBox intermediaryPayment = new CheckBox(AON.MSG.intermediaryPayment());
		intermediaryPayment.setStyleName(AON.AON_CSS.aonFontMedium());
		intermediaryPayment.setValue(detail.isIntermediaryPayment());
		intermediaryPayment.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				detail.setIntermediaryPayment(intermediaryPayment.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(2, 2, intermediaryPayment);

		ProvinceListBox province = new ProvinceListBox();
		province.setSelectedIndex(detail.getProvince());
		province.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				detail.setProvince(province.getSelectedIndex());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(2, 3, province);
		
		ListBox keyCode = new ListBox();
		keyCode.setWidth("40px");
		keyCode.addItem("-");
		keyCode.addItem("1 - El c\u00F3digo emisor corresponde a un NIF.");
		keyCode.addItem("2 - El c\u00F3digo emisor corresponde a un c\u00F3digo ISIN.");
		keyCode.addItem("3 - El c\u00F3digo emisor corresponde a valores extranjeros que no tienen asignado ISIN.");
		keyCode.setSelectedIndex(detail.getKeyCode());
		keyCode.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				detail.setKeyCode((byte) keyCode.getSelectedIndex());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(2, 4, keyCode);
		
		TextBox issuingCode = new TextBox();
		issuingCode.setVisibleLength(12);
		issuingCode.setMaxLength(12);
		issuingCode.setStyleName(AON.AON_CSS.aonInputText());
		issuingCode.setValue(detail.getIssuingCode());
		issuingCode.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setIssuingCode(issuingCode.getValue());
				callback.onNameChanged(detail);
			}
		});
		tab2.setWidget(2, 5, issuingCode);

		IntegerBox accrualYear = new IntegerBox();
		accrualYear.setMaxLength(4);
		accrualYear.setVisibleLength(4);
		accrualYear.setValue(detail.getAccrualYear());
		accrualYear.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setAccrualYear(accrualYear.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(2, 6, accrualYear);
		
		tab2.setWidget(3, 0, new MediumLabel(AON.MSG.payment()));
		tab2.setWidget(3, 1, new MediumLabel(AON.MSG.codeType()));
		tab2.setWidget(3, 2, new MediumLabel(AON.MSG.ccv()));
		tab2.setWidget(3, 3, new Label());
		tab2.setWidget(3, 4, new MediumLabel(AON.MSG.lenderAmount()));
		tab2.getFlexCellFormatter().setColSpan(3, 4, 2);
		tab2.setWidget(3, 5, new MediumLabel(AON.MSG.reductions()));

		ListBox payment = new ListBox();
		payment.setWidth("40px");
		payment.addItem("-");
		payment.addItem("1 - Como emisor.");
		payment.addItem("2 - Como mediador de valor nacional.");
		payment.addItem("3 - Como mediador de valor extranjero.");
		payment.setSelectedIndex(detail.getPayment());
		payment.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				detail.setPayment((byte) payment.getSelectedIndex());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(4, 0, payment);
		
		ListBox codeType = new ListBox();
		codeType.setWidth("40px");
		codeType.addItem("-","");
		codeType.addItem("C - Identificaci\u00F3n con el C\u00F3digo Cuenta Cliente (C.C.V.).","C");
		codeType.addItem("O - Otra identificaci\u00F3n.","O");
		codeType.addItem("P  -Pr\u00E9stamo de valores.","P");
		if (AonStringUtils.equals("C", detail.getCodeType())) {
			codeType.setSelectedIndex(1);	
		} else if (AonStringUtils.equals("O", detail.getCodeType())) {
			codeType.setSelectedIndex(2);
		} else if (AonStringUtils.equals("P", detail.getCodeType())) {
			codeType.setSelectedIndex(3);
		} else {
			codeType.setSelectedIndex(0);
		}
		codeType.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				detail.setCodeType( codeType.getValue( codeType.getSelectedIndex()) );
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(4, 1, codeType);

		
		TextBox accountCode = new TextBox();
		accountCode.setVisibleLength(12);
		accountCode.setMaxLength(12);
		accountCode.setStyleName(AON.AON_CSS.aonInputText());
		accountCode.setValue(detail.getAccountCode());
		accountCode.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setAccountCode(accountCode.getValue());
				callback.onNameChanged(detail);
			}
		});
		tab2.setWidget(4, 2, accountCode);
				
		CheckBox inKind = new CheckBox(AON.MSG.inKind());
		inKind.setStyleName(AON.AON_CSS.aonFontMedium());
		inKind.setValue(detail.isInKind());
		inKind.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				detail.setInKind(inKind.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(4, 3, inKind);
				

		DoubleBox lenderAmount = new DoubleBox();
		lenderAmount.setValue(detail.getLenderAmount());
		lenderAmount.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setLenderAmount(lenderAmount.getValue());
				callback.onNameChanged(detail);
			}
		});
		tab2.setWidget(4, 4, lenderAmount);
		tab2.getFlexCellFormatter().setColSpan(4, 4, 2);
		
		DoubleBox reduction = new DoubleBox();
		reduction.setValue(detail.getReduction());
		reduction.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setReduction(reduction.getValue());
				callback.onNameChanged(detail);
			}
		});
		tab2.setWidget(4, 5, reduction);
		
		FlexTable tab3 = new FlexTable();
		panel.add(tab3);
		tab3.getColumnFormatter().setWidth(0, "160px");
		tab3.getColumnFormatter().setWidth(1, "160px");
		tab3.getColumnFormatter().setWidth(2, "auto");
		tab3.setStyleName(AON.AON_CSS.aonWidthAll());
		tab3.addStyleName(AON.AON_CSS.aonNowrap());
		
 		tab3.setWidget(1, 0, new MediumLabel(AON.MSG.retentionBase()));
		tab3.setWidget(1, 1, new MediumLabel(AON.MSG.percent()));
		tab3.setWidget(1, 2, new MediumLabel(AON.MSG.retentionAccount()));
		
		DoubleBox retentionBase = new DoubleBox();
		retentionBase.setValue(detail.getRetentionBase());
		retentionBase.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setRetentionBase(retentionBase.getValue());
				callback.onNameChanged(detail);
			}
		});
		tab3.setWidget(2, 0, retentionBase);
		
		DoubleBox percent = new DoubleBox();
		percent.setValue(detail.getPercent());
		percent.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setPercent(percent.getValue());
				callback.onNameChanged(detail);
			}
		});
		tab3.setWidget(2, 1, percent);

		DoubleBox retention = new DoubleBox();
		retention.setValue(detail.getRetention());
		retention.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setRetention(retention.getValue());
				callback.onNameChanged(detail);
			}
		});
		tab3.setWidget(2, 2, retention);
		
		FlexTable tab4 = new FlexTable();
		panel.add(tab4);
		tab4.getColumnFormatter().setWidth(0, "160px");
		tab4.getColumnFormatter().setWidth(1, "160px");
		tab4.getColumnFormatter().setWidth(2, "160px");
		tab4.getColumnFormatter().setWidth(3, "auto");
		tab4.setStyleName(AON.AON_CSS.aonWidthAll());
		tab4.addStyleName(AON.AON_CSS.aonNowrap());
		
 		tab4.setWidget(1, 0, new MediumLabel(AON.MSG.loanStart() + " 2016"));
		tab4.setWidget(1, 1, new MediumLabel(AON.MSG.loanDueStart()));
		tab4.setWidget(1, 2, new MediumLabel(AON.MSG.compensations()));
		tab4.setWidget(1, 3, new MediumLabel(AON.MSG.guarantee()));

		DateBoxEx loanStartDate = new DateBoxEx();
		loanStartDate.setValue(detail.getLoanStartDate());
		loanStartDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				detail.setLoanStartDate(loanStartDate.getValue());
				callback.onNameChanged(detail);
			}
		});
		tab4.setWidget(2, 0, loanStartDate);
		
		DateBoxEx loanDueDate = new DateBoxEx();
		loanDueDate.setValue(detail.getLoanDueDate());
		loanDueDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				detail.setLoanDueDate(loanDueDate.getValue());
				callback.onNameChanged(detail);
			}
		});
		tab4.setWidget(2, 1, loanDueDate);
		
		DoubleBox compensation = new DoubleBox();
		compensation.setValue(detail.getCompensation());
		compensation.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setCompensation(compensation.getValue());
				callback.onNameChanged(detail);
			}
		});
		tab4.setWidget(2, 2, compensation);

		DoubleBox guarantee = new DoubleBox();
		guarantee.setValue(detail.getGuarantee());
		guarantee.addValueChangeHandler(new ValueChangeHandler<Double>() {
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setGuarantee(guarantee.getValue());
				callback.onNameChanged(detail);
			}
		});
		tab4.setWidget(2, 3, guarantee);

		scroll.setWidget(panel);
		setWidget(scroll);
	}

	@Override
	public int getTabIndex() {
		return tabIndex;
	}

	@Override
	public void setAccessKey(char key) {
	}

	@Override
	public void setFocus(boolean focused) {
		document.selectAll();
		document.setFocus(true);
	}

	@Override
	public void setTabIndex(int index) {
		tabIndex = index;
	}


	private static void setValue(ListBox key, ListBox nature, Mod193Detail detail) {
		if (AonStringUtils.isBlank( detail.getKey())) {
			detail.setKey(Mod193Key.A.toString());
		}
		Mod193Key keyEnum = Mod193Key.valueOf(detail.getKey());
		key.setSelectedIndex(keyEnum.ordinal());
		nature.clear();
		nature.setEnabled(true);
		for (int i = 0; i < keyEnum.getNatures().length; i++) {
			nature.addItem(AonStringUtils.abbreviate(keyEnum.getNatureDescriptions()[i],150), keyEnum.getNatures()[i]);
			if (keyEnum.getNatures()[i].equals(detail.getNature())) {
				nature.setSelectedIndex(i);
			}
		}
	}
}
