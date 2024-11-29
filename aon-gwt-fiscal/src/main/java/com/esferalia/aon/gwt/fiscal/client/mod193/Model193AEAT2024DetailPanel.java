package com.esferalia.aon.gwt.fiscal.client.mod193;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod193.Model193AEATDetail2024.IModel193DetailCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod193Detail;
import com.esferalia.aon.occam.api.model.type.Mod1932024Key;
import com.esferalia.aon.watson.util.AonStringUtils;
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

public class Model193AEAT2024DetailPanel extends SimpleLayoutPanel implements Focusable {
	
	private static final String WIDTH_200PX = "200px";
	private static final String WIDTH_160PX = "160px";

	private final class ValueChangeHandlerImplementation2 implements ValueChangeHandler<String> {
		private final Mod193Detail detail;
		private final IModel193DetailCallback callback;
		private final AonTextBox issuingCode;

		private ValueChangeHandlerImplementation2(Mod193Detail detail, IModel193DetailCallback callback, AonTextBox issuingCode) {
			this.detail = detail;
			this.callback = callback;
			this.issuingCode = issuingCode;
		}

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			detail.setIssuingCode(issuingCode.getValue());
			callback.onNameChanged(detail);
		}
	}

	private final class ValueChangeHandlerImplementation implements ValueChangeHandler<String> {
		private final IModel193DetailCallback callback;
		private final Mod193Detail detail;
		private final AonTextBox name;

		private ValueChangeHandlerImplementation(IModel193DetailCallback callback, Mod193Detail detail,	AonTextBox name) {
			this.callback = callback;
			this.detail = detail;
			this.name = name;
		}

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			detail.setName(name.getValue());
			callback.onNameChanged(detail);
		}
	}

	private static final String WIDTH_100PX = "100px";
	private int tabIndex; 
	private AonDocumentTextBox document;
	
	public Model193AEAT2024DetailPanel(Mod193Detail detail, IModel193DetailCallback callback) {
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonWidthAll());
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonScrollArea());
		
		FlexTable tab1 = new FlexTable();
		panel.add(tab1);
		tab1.getColumnFormatter().setWidth(0, WIDTH_100PX);
		tab1.getColumnFormatter().setWidth(1, WIDTH_100PX);
		tab1.getColumnFormatter().setWidth(2, WIDTH_100PX);
		tab1.getColumnFormatter().setWidth(3, "300px");
		tab1.getColumnFormatter().setWidth(4, "auto");
		tab1.setStyleName(AON.CSS.aonWidthAll());
		tab1.addStyleName(AON.CSS.aonNowrap());
		
		tab1.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
		tab1.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
		tab1.getFlexCellFormatter().setColSpan(0, 0, 5);
		tab1.setWidget(0, 0, new InlineLabel(AON.MSG.perceptionData()));

 		tab1.setWidget(1, 1, new Model193SmallerLabel(AON.MSG.receiverDocument()));
		tab1.setWidget(1, 2, new Model193SmallerLabel(AON.MSG.representativeDocument()));
		tab1.setWidget(1, 3, new Model193SmallerLabel(AON.MSG.fullName()));
		
		// Pendiente
		CheckBox pending = new CheckBox(AON.MSG.pending());
		pending.setValue(detail.isPending());
		pending.addClickHandler(event -> {
			detail.setPending(pending.getValue());
			callback.onValueChanged(detail);
		});
		tab1.setWidget(2, 0, pending);
		
		// NIF perceptor
		document = new AonDocumentTextBox();
		document.setValue(detail.getDocument());
		document.addValueChangeHandler(event -> {
			detail.setDocument(document.getValue());
			callback.onValueChanged(detail);
		});
		tab1.setWidget(2, 1, document);
		
		// NIF representante
		AonDocumentTextBox representativeDocument = new AonDocumentTextBox();
		representativeDocument.setValue(detail.getRepresentativeDocument());
		representativeDocument.addValueChangeHandler(event -> {
			detail.setRepresentativeDocument(representativeDocument.getValue());
			callback.onValueChanged(detail);
		});
		tab1.setWidget(2, 2, representativeDocument);
		
		// Apellidos y nombre, razón social o denominación del perceptor
		AonTextBox name = new AonTextBox();
		name.setVisibleLength(40);
		name.setMaxLength(40);
		name.setValue(detail.getName());
		name.addValueChangeHandler(new ValueChangeHandlerImplementation(callback, detail, name));
		tab1.setWidget(2, 3, name);
		
		FlexTable tab2 = new FlexTable();
		panel.add(tab2);
		tab2.getColumnFormatter().setWidth(0, "60px");
		tab2.getColumnFormatter().setWidth(1, "60px");
		tab2.getColumnFormatter().setWidth(2, WIDTH_100PX);
		tab2.getColumnFormatter().setWidth(3, "120px");
		tab2.getColumnFormatter().setWidth(4, "60px");
		tab2.getColumnFormatter().setWidth(5, "130px");
		tab2.getColumnFormatter().setWidth(6, "100px");
		tab2.getColumnFormatter().setWidth(7, "auto");
		tab2.setStyleName(AON.CSS.aonWidthAll());
		tab2.addStyleName(AON.CSS.aonNowrap());
		
		tab2.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
		tab2.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
		tab2.getFlexCellFormatter().setColSpan(0, 0, 7);
		tab2.setWidget(0, 0, new InlineLabel(AON.MSG.operationData()));

 		tab2.setWidget(1, 0, new Model193SmallerLabel(AON.MSG.key()));
		tab2.setWidget(1, 1, new Model193SmallerLabel(AON.MSG.nature()));
		tab2.setWidget(1, 2, new Label());
		tab2.setWidget(1, 3, new Model193SmallerLabel(AON.MSG.province()));
		tab2.setWidget(1, 4, new Model193SmallerLabel(AON.MSG.keyCode()));
		tab2.setWidget(1, 5, new Model193SmallerLabel(AON.MSG.issuingCode()));
		tab2.setWidget(1, 6, new Model193SmallerLabel(AON.MSG.accrualYear()));
		tab2.setWidget(1, 7, new Label());

		// Clave percepción / Naturaleza
		final ListBox nature = new ListBox();
		nature.setWidth("40px");

		final ListBox key = new ListBox();
		key.setWidth("40px");
		for (Mod1932024Key k : Mod1932024Key.values()) {
			key.addItem(AonStringUtils.abbreviate(k.getDescription(),150), k.getValue());
		}
		
		Model193AEAT2024DetailPanel.setValue(key, nature, detail);
		
		key.addChangeHandler(event -> {
			nature.clear();
			Mod1932024Key keyEnum = Mod1932024Key.values()[key.getSelectedIndex()];
			detail.setKey( keyEnum.toString() );
			nature.setEnabled(true);
			for (int i = 0; i < keyEnum.getNatures().length; i++) {
				nature.addItem(AonStringUtils.abbreviate(keyEnum.getNatureDescriptions()[i],150), keyEnum.getNatures()[i]);
			}
			detail.setNature(keyEnum.getNatures()[0]);
			callback.onValueChanged(detail);
		});
		tab2.setWidget(2, 0, key);

		nature.addChangeHandler(event -> {
			Mod1932024Key keyEnum = Mod1932024Key.values()[key.getSelectedIndex()];
			int idx = nature.getSelectedIndex() == -1 ? 0 : nature.getSelectedIndex();
			detail.setNature(keyEnum.getNatures()[idx]);
			callback.onValueChanged(detail);
		});
		tab2.setWidget(2, 1, nature);

		// Perceptor mediador 
		CheckBox intermediaryPayment = new CheckBox("Perceptor mediador");
		intermediaryPayment.setValue(detail.isIntermediaryPayment());
		intermediaryPayment.addClickHandler(event -> {
			detail.setIntermediaryPayment(intermediaryPayment.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(2, 2, intermediaryPayment);

		// FALTA - EN LA LISTA PERMITIDA EN EL DISEÑO DEL REGISTRO ESTAN DE LA 01 A 52 Y ADEMAS 53 ES LA ISLA DE LA PALMA Y NO ESTA "NO RESIDENTE"
		// Provincia (Código)
		ProvinceListBox province = new ProvinceListBox();
		province.setSelectedIndex(detail.getProvince());
		province.addChangeHandler( event -> {
			detail.setProvince(province.getSelectedIndex());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(2, 3, province);
		
		// Clave código
		ListBox keyCode = new ListBox();
		keyCode.setWidth("40px");
		keyCode.addItem("-");
		keyCode.addItem("1 - El c\u00F3digo emisor corresponde a un NIF.");
		keyCode.addItem("2 - El c\u00F3digo emisor corresponde a un c\u00F3digo ISIN.");
		keyCode.addItem("3 - El c\u00F3digo emisor corresponde a valores extranjeros que no tienen asignado ISIN.");
		keyCode.setSelectedIndex(detail.getKeyCode());
		keyCode.addChangeHandler( event -> {
			detail.setKeyCode((byte) keyCode.getSelectedIndex());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(2, 4, keyCode);
		
		// Código emisor
		AonTextBox issuingCode = new AonTextBox();
		issuingCode.setVisibleLength(12);
		issuingCode.setMaxLength(12);
		issuingCode.setValue(detail.getIssuingCode());
		issuingCode.addValueChangeHandler(new ValueChangeHandlerImplementation2(detail, callback, issuingCode));
		tab2.setWidget(2, 5, issuingCode);

		// Ejercicio devengo
		AonIntegerBox accrualYear = new AonIntegerBox();
		accrualYear.setMaxLength(4);
		accrualYear.setVisibleLength(4);
		accrualYear.setValue(detail.getAccrualYear());
		accrualYear.addValueChangeHandler(event -> {
			detail.setAccrualYear(accrualYear.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(2, 6, accrualYear);
		
		// FALTA - ESTE CAMPO NO ES UN CHECK SINO QUE SUS VALORES PERMITIDOS SON 0,1,2 (NO APLICABLE, CEUTA O MELILLA, ISLA DE LA PALMA)
		// Ceuta o Melilla / Isla de la Palma
		CheckBox ceutaMelilla = new CheckBox("Ceuta o Melilla");  // TAL Y COMO ESTA AHORA, SOLO SE PUEDE INDICAR CEUTA O MELILLA
		ceutaMelilla.setValue(detail.isCeutaMelilla());
		ceutaMelilla.addClickHandler(event -> {
			detail.setCeutaMelilla(ceutaMelilla.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(2, 7, ceutaMelilla);
		
		tab2.setWidget(3, 0, new Model193SmallerLabel(AON.MSG.payment()));
		tab2.setWidget(3, 1, new Model193SmallerLabel(AON.MSG.codeType()));
		tab2.setWidget(3, 2, new Model193SmallerLabel(AON.MSG.ccv()));
		tab2.setWidget(3, 3, new Label());
		tab2.setWidget(3, 4, new Model193SmallerLabel(AON.MSG.lenderAmount()));
		tab2.getFlexCellFormatter().setColSpan(3, 4, 2);
		tab2.setWidget(3, 5, new Model193SmallerLabel(AON.MSG.reductions()));
		tab2.getFlexCellFormatter().setColSpan(3, 5, 2);

		// Pago
		ListBox payment = new ListBox();
		payment.setWidth("40px");
		payment.addItem("-");
		payment.addItem("1 - Como emisor");
		payment.addItem("2 - Como mediador de valor nacional");
		payment.addItem("3 - Como mediador de valor extranjero");
		payment.addItem("4 - Como mediador de valor extranjero no retenedor");
		payment.addItem("5 - Como mediador de otro tipo de rendimientos o rentas obtenidas por cesi\u00F3n de capitales consignados con clave B-06");
		
		payment.setSelectedIndex(detail.getPayment());
		payment.addChangeHandler( event -> {
			detail.setPayment((byte) payment.getSelectedIndex());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(4, 0, payment);
		
		// Tipo código
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
		codeType.addChangeHandler( event -> {
			detail.setCodeType( codeType.getValue( codeType.getSelectedIndex()) );
			callback.onValueChanged(detail);
		});
		tab2.setWidget(4, 1, codeType);

		// Código cuenta valores
		AonTextBox accountCode = new AonTextBox();
		accountCode.setVisibleLength(12);
		accountCode.setMaxLength(12);
		accountCode.setStyleName(AON.CSS.aonInputText());
		accountCode.setValue(detail.getAccountCode());
		accountCode.addValueChangeHandler(event -> {
			detail.setAccountCode(accountCode.getValue());
			callback.onNameChanged(detail);
		});
		tab2.setWidget(4, 2, accountCode);
				
		// Tipo de percepcion (En especie)
		CheckBox inKind = new CheckBox(AON.MSG.inKind());
		inKind.setValue(detail.isInKind());
		inKind.addClickHandler(event -> {
			detail.setInKind(inKind.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(4, 3, inKind);
				
        // Importe percepciones/remuneracion al prestamista 
		AonDoubleBox lenderAmount = new AonDoubleBox();
		lenderAmount.setValue(detail.getLenderAmount());
		lenderAmount.addValueChangeHandler(event -> {
			detail.setLenderAmount(lenderAmount.getValue());
			callback.onNameChanged(detail);
		});
		tab2.setWidget(4, 4, lenderAmount);
		tab2.getFlexCellFormatter().setColSpan(4, 4, 2);
		
		// Importe reducciones
		AonDoubleBox reduction = new AonDoubleBox();
		reduction.setValue(detail.getReduction());
		reduction.addValueChangeHandler(event -> {
			detail.setReduction(reduction.getValue());
			callback.onNameChanged(detail);
		});
		tab2.setWidget(4, 5, reduction);
		tab2.getFlexCellFormatter().setColSpan(4, 5, 2);
		
		FlexTable tab3 = new FlexTable();
		panel.add(tab3);
		tab3.getColumnFormatter().setWidth(0, WIDTH_160PX);
		tab3.getColumnFormatter().setWidth(1, WIDTH_160PX);
		tab3.getColumnFormatter().setWidth(2, "auto");
		tab3.setStyleName(AON.CSS.aonWidthAll());
		tab3.addStyleName(AON.CSS.aonNowrap());
		
 		tab3.setWidget(1, 0, new Model193SmallerLabel(AON.MSG.retentionBase()));
		tab3.setWidget(1, 1, new Model193SmallerLabel(AON.MSG.percent()));
		tab3.setWidget(1, 2, new Model193SmallerLabel(AON.MSG.retentionAccount()));
		
		// Base retenciones e ingresos a cuenta
		AonDoubleBox retentionBase = new AonDoubleBox();
		retentionBase.setValue(detail.getRetentionBase());
		retentionBase.addValueChangeHandler(event -> {
			detail.setRetentionBase(retentionBase.getValue());
			callback.onNameChanged(detail);
		});
		tab3.setWidget(2, 0, retentionBase);
		
		// Porcentaje retencion (%)
		AonDoubleBox percent = new AonDoubleBox();
		percent.setValue(detail.getPercent());
		percent.addValueChangeHandler(event -> {
			detail.setPercent(percent.getValue());
			callback.onNameChanged(detail);
		});
		tab3.setWidget(2, 1, percent);

		// Retenciones e ingresos a cuenta
		AonDoubleBox retention = new AonDoubleBox();
		retention.setValue(detail.getRetention());
		retention.addValueChangeHandler(event -> {
			detail.setRetention(retention.getValue());
			callback.onNameChanged(detail);
		});
		tab3.setWidget(2, 2, retention);
		
		FlexTable tab4 = new FlexTable();
		panel.add(tab4);
		tab4.getColumnFormatter().setWidth(0, WIDTH_160PX);
		tab4.getColumnFormatter().setWidth(1, WIDTH_160PX);
		tab4.getColumnFormatter().setWidth(2, WIDTH_160PX);
		tab4.getColumnFormatter().setWidth(3, WIDTH_160PX);
		tab4.getColumnFormatter().setWidth(4, "auto");
		tab4.setStyleName(AON.CSS.aonWidthAll());
		tab4.addStyleName(AON.CSS.aonNowrap());
		
 		tab4.setWidget(1, 0, new Model193SmallerLabel(AON.MSG.loanStart()));
		tab4.setWidget(1, 1, new Model193SmallerLabel(AON.MSG.loanDueStart()));
		tab4.setWidget(1, 2, new Model193SmallerLabel(AON.MSG.penalizations()));
		tab4.setWidget(1, 3, new Model193SmallerLabel(AON.MSG.compensations()));
		tab4.setWidget(1, 4, new Model193SmallerLabel(AON.MSG.guarantee()));

		// Fecha inicio préstamo
		AonDateBox loanStartDate = new AonDateBox();
		loanStartDate.setValue(detail.getLoanStartDate());
		loanStartDate.addValueChangeHandler(event -> {
			detail.setLoanStartDate(loanStartDate.getValue());
			callback.onNameChanged(detail);
		});
		tab4.setWidget(2, 0, loanStartDate);
		
		// Fecha vencimiento préstamo
		AonDateBox loanDueDate = new AonDateBox();
		loanDueDate.setValue(detail.getLoanDueDate());
		loanDueDate.addValueChangeHandler(event -> {
			detail.setLoanDueDate(loanDueDate.getValue());
			callback.onNameChanged(detail);
		});
		tab4.setWidget(2, 1, loanDueDate);
		
		// Penalizaciones
		AonDoubleBox penalization = new AonDoubleBox();
		penalization.setValue(detail.getPenalization());
		penalization.addValueChangeHandler(event -> {
			detail.setPenalization(penalization.getValue());
			callback.onNameChanged(detail);
		});
		tab4.setWidget(2, 2, penalization);

		// Compensaciones
		AonDoubleBox compensation = new AonDoubleBox();
		compensation.setValue(detail.getCompensation());
		compensation.addValueChangeHandler(event -> {
			detail.setCompensation(compensation.getValue());
			callback.onNameChanged(detail);
		});
		tab4.setWidget(2, 3, compensation);

		// Garantías
		AonDoubleBox guarantee = new AonDoubleBox();
		guarantee.setValue(detail.getGuarantee());
		guarantee.addValueChangeHandler(event -> {
			detail.setGuarantee(guarantee.getValue());
			callback.onNameChanged(detail);
		});
		tab4.setWidget(2, 4, guarantee);
		
		FlexTable tab41 = new FlexTable();
		panel.add(tab41);
		tab41.getColumnFormatter().setWidth(0, WIDTH_160PX);
		tab41.getColumnFormatter().setWidth(1, WIDTH_160PX);
		tab41.getColumnFormatter().setWidth(2, "auto");
		tab41.setStyleName(AON.CSS.aonWidthAll());
		tab41.addStyleName(AON.CSS.aonNowrap());
		
 		tab41.setWidget(1, 0, new Model193SmallerLabel("NIF del pagador anterior"));
		tab41.setWidget(1, 1, new Model193SmallerLabel("Fecha de devengo"));
		tab41.setWidget(1, 2, new Model193SmallerLabel("Clave de mercado"));
		
		// NIF del pagador anterior
		AonDocumentTextBox previousPayerDocument = new AonDocumentTextBox();
		previousPayerDocument.setMaxLength(9);
		previousPayerDocument.setValue(detail.getPreviousPayerDocument());
		previousPayerDocument.addValueChangeHandler(event -> {
			detail.setPreviousPayerDocument(previousPayerDocument.getValue());
			callback.onValueChanged(detail);
		});
		tab41.setWidget(2, 0, previousPayerDocument);
		
		// Fecha de devengo
		AonDateBox accrualDate = new AonDateBox();
		accrualDate.setValue(detail.getAccrualDate());
		accrualDate.addValueChangeHandler(event -> {
			detail.setAccrualDate(accrualDate.getValue());
			callback.onNameChanged(detail);
		});
		tab41.setWidget(2, 1, accrualDate);

		// Clave de mercado
		ListBox marketKey = new ListBox();
		marketKey.setWidth("400px");
		marketKey.addItem("-","");
		marketKey.addItem("A - Mercado secundario oficial de valores espa\u00F1ol","A");
		marketKey.addItem("B - Mercado secundario oficial de valores extranjeros de la UE","B");
		marketKey.addItem("C - Otros mercados oficiales extranjeros","C");
		marketKey.addItem("D - Otros","D");
		
		if (AonStringUtils.equals("A", detail.getMarketKey())) {
			marketKey.setSelectedIndex(1);	
		} else if (AonStringUtils.equals("B", detail.getMarketKey())) {
			marketKey.setSelectedIndex(2);
		} else if (AonStringUtils.equals("C", detail.getMarketKey())) {
			marketKey.setSelectedIndex(3);
		} else if (AonStringUtils.equals("D", detail.getMarketKey())) {
			marketKey.setSelectedIndex(4);
		} else {
			marketKey.setSelectedIndex(0);
		}
		marketKey.addChangeHandler( event -> {
			detail.setMarketKey(marketKey.getValue(marketKey.getSelectedIndex()));
			callback.onValueChanged(detail);
		});
		tab41.setWidget(2, 2, marketKey);

		FlexTable tab31 = new FlexTable();
		panel.add(tab31);		
		tab31.getColumnFormatter().setWidth(0, WIDTH_200PX);
		tab31.getColumnFormatter().setWidth(1, WIDTH_200PX);
		tab31.getColumnFormatter().setWidth(2, WIDTH_200PX);
		tab31.getColumnFormatter().setWidth(3, WIDTH_200PX);
		tab31.getColumnFormatter().setWidth(4, WIDTH_200PX);
		tab31.getColumnFormatter().setWidth(5, "auto");
		tab31.setStyleName(AON.CSS.aonWidthAll());
		tab31.addStyleName(AON.CSS.aonNowrap());

		tab31.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
		tab31.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
		tab31.getFlexCellFormatter().setColSpan(0, 0, 6);
		tab31.setWidget(0, 0,
				new InlineLabel("Retenciones e ingresos a cuenta ingresados en el Estado, "
						+ "en las Diputaciones Forales del Pa\u00EDs Vasco y en la Comunidad Foral de "
						+ "Navarra"));
		
		tab31.setWidget(1, 0, new Model193SmallerLabel("Hacienda Estatal"));
		tab31.setWidget(1, 1, new Model193SmallerLabel("Com. Foral Navarra"));
		tab31.setWidget(1, 2, new Model193SmallerLabel("Dip. Foral Araba/\u00C1lava"));
		tab31.setWidget(1, 3, new Model193SmallerLabel("Dip. Foral Gipuzkoa"));
		tab31.setWidget(1, 4, new Model193SmallerLabel("Dip. Foral Bizkaia"));
		tab31.setWidget(1, 5, new Label());

		// Retenciones e ingresos a cuenta - Hacienda Estatal
		AonDoubleBox commonRetention = new AonDoubleBox();
		commonRetention.setValue(detail.getCommonRetention());
		commonRetention.addValueChangeHandler(event -> {
			detail.setCommonRetention(commonRetention.getValue());
			callback.onValueChanged(detail);
		});
		tab31.setWidget(2, 0, commonRetention);

		// Retenciones e ingresos a cuenta - Navarra
		AonDoubleBox navarraRetention = new AonDoubleBox();
		navarraRetention.setValue(detail.getNavarraRetention());
		navarraRetention.addValueChangeHandler(event -> {
			detail.setNavarraRetention(navarraRetention.getValue());
			callback.onValueChanged(detail);
		});
		tab31.setWidget(2, 1, navarraRetention);
		
		// Retenciones e ingresos a cuenta - Araba/Alava
		AonDoubleBox arabaRetention = new AonDoubleBox();
		arabaRetention.setValue(detail.getArabaRetention());
		arabaRetention.addValueChangeHandler(event -> {
			detail.setArabaRetention(arabaRetention.getValue());
			callback.onValueChanged(detail);
		});
		tab31.setWidget(2, 2, arabaRetention);
		
		// Retenciones e ingresos a cuenta - Gipuzkoa
		AonDoubleBox gipuzkoaRetention = new AonDoubleBox();
		gipuzkoaRetention.setValue(detail.getGipuzkoaRetention());
		gipuzkoaRetention.addValueChangeHandler(event -> {
			detail.setGipuzkoaRetention(gipuzkoaRetention.getValue());
			callback.onValueChanged(detail);
		});
		tab31.setWidget(2, 3, gipuzkoaRetention);

		// Retenciones e ingresos a cuenta - Bizkaia
		AonDoubleBox bizkaiaRetention = new AonDoubleBox();
		bizkaiaRetention.setValue(detail.getBizkaiaRetention());
		bizkaiaRetention.addValueChangeHandler(event -> {
			detail.setBizkaiaRetention(bizkaiaRetention.getValue());
			callback.onValueChanged(detail);
		});
		tab31.setWidget(2, 4, bizkaiaRetention);
		tab31.setWidget(2, 5, new Label());
		
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
		document.selectAll();
		document.setFocus(true);
	}

	@Override
	public void setTabIndex(int index) {
		tabIndex = index;
	}


	private static void setValue(ListBox key, ListBox nature, Mod193Detail detail) {
		if (AonStringUtils.isBlank( detail.getKey())) {
			detail.setKey(Mod1932024Key.A.toString());
		}
		Mod1932024Key keyEnum = Mod1932024Key.valueOf(detail.getKey());
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
