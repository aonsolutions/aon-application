package com.esferalia.aon.gwt.fiscal.client.mod190;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod190.Model190AEATDetail2025.IModel190DetailCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.type.Mod1902025Key;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model190AEAT2025DetailPanel extends SimpleLayoutPanel implements Focusable {

	private static final String COMPUTADO_POR_MITAD = "2 - Computado por mitad.";
	private static final String COMPUTADO_POR_ENTERO = "1 - Computado por entero.";
	private static final String WIDTH_150PX = "150px";
	private static final String WIDTH_100PX = "100px";
	private static final String WIDTH_200PX = "200px";
	private int tabIndex;
	private AonDocumentTextBox document;

	private static class Province2024ListBox extends ProvinceListBox {
		public Province2024ListBox() {
			super();
			this.setItemText(38, "S.C. Tenerife (excepto Isla de la Palma)");
			this.setItemText(53, "Isla de La Palma" );
		}
	}

	public Model190AEAT2025DetailPanel(Mod190Detail detail, IModel190DetailCallback callback) {
		FlowPanel additionalDataPanel = new FlowPanel();
		FlowPanel additionalDataPanel2 = new FlowPanel();
		FlowPanel ilPanel = new FlowPanel();
		FlowPanel administrationPanel = new FlowPanel();

		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonWidthAll());
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonScrollArea());
		
		// NIF Perceptor / NIF Repr. / Apellidos y nombre o denominación / Provincia

		FlexTable tab1 = new FlexTable();
		panel.add(tab1);
		tab1.getColumnFormatter().setWidth(0, WIDTH_100PX);
		tab1.getColumnFormatter().setWidth(1, WIDTH_100PX);
		tab1.getColumnFormatter().setWidth(2, "300px");
		tab1.getColumnFormatter().setWidth(3, "auto");
		tab1.setStyleName(AON.CSS.aonWidthAll());
		tab1.addStyleName(AON.CSS.aonNowrap());

		tab1.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
		tab1.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
		tab1.getFlexCellFormatter().setColSpan(0, 0, 4);
		tab1.setWidget(0, 0, new InlineLabel(AON.MSG.perceptionData()));

		tab1.setWidget(1, 0, new Model190SmallerLabel(AON.MSG.receiverDocument()));
		tab1.setWidget(1, 1, new Model190SmallerLabel(AON.MSG.representativeDocument()));
		tab1.setWidget(1, 2, new Model190SmallerLabel(AON.MSG.fullName()));
		tab1.setWidget(1, 3, new Model190SmallerLabel(AON.MSG.province()));

		document = new AonDocumentTextBox();
		document.setValue(detail.getDocument());
		document.addValueChangeHandler(event -> {
			detail.setDocument(document.getValue());
			callback.onValueChanged(detail);
		});
		tab1.setWidget(2, 0, document);

		AonDocumentTextBox representativeDocument = new AonDocumentTextBox();
		representativeDocument.setValue(detail.getRepresentativeDocument());
		representativeDocument.addValueChangeHandler(event -> {
			detail.setRepresentativeDocument(representativeDocument.getValue());
			callback.onValueChanged(detail);
		});
		tab1.setWidget(2, 1, representativeDocument);

		AonTextBox name = new AonTextBox();
		name.setVisibleLength(40);
		name.setMaxLength(40);
		name.setValue(detail.getName());
		name.addValueChangeHandler(event -> {
			detail.setName(name.getValue());
			callback.onNameChanged(detail);
		});
		tab1.setWidget(2, 2, name);

		Province2024ListBox province = new Province2024ListBox();
		province.setSelectedIndex(detail.getProvince());
		province.addChangeHandler(event -> {
			detail.setProvince(province.getSelectedIndex());
			callback.onValueChanged(detail);
		});
		tab1.setWidget(2, 3, province);
		
		// Clave / Subclave / Ejercicio Devengo	/ Ceuta o Melilla, Isla de la Palma
		
		tab1.setWidget(3, 0, new Model190SmallerLabel(AON.MSG.key()));
		tab1.setWidget(3, 1, new Model190SmallerLabel(AON.MSG.subkey()));
		tab1.setWidget(3, 2, new Model190SmallerLabel("Ejercicio de Devengo"));
		tab1.setWidget(3, 3, new Model190SmallerLabel("Ceuta o Melilla / Isla de la Palma"));
		
		final ListBox subkey = new ListBox();
		subkey.setWidth("45px");

		final ListBox key = new ListBox();
		key.setWidth("40px");
		for (Mod1902025Key k : Mod1902025Key.values()) {
			key.addItem(k.getDescription(), k.getValue());
		}

		Model190AEAT2025DetailPanel.setValue(key, subkey, detail);

		key.addChangeHandler(event -> {
			subkey.clear();
			Mod1902025Key keyEnum = Mod1902025Key.values()[key.getSelectedIndex()];
			detail.setKey(keyEnum.toString());
			if (keyEnum.hasSubkeys()) {
				subkey.setEnabled(true);
				for (int i = 0; i < keyEnum.getSubKeys().length; i++) {
					subkey.addItem(keyEnum.getSubKeys()[i]);
				}
				detail.setSubKey(keyEnum.getSubKeys()[0]);
			} else {
				subkey.setEnabled(false);
				detail.setSubKey(null);
			}
			Model190AEAT2025DetailPanel.enableOrDisableIlPanel(key, subkey, ilPanel);
			Model190AEAT2025DetailPanel.enableOrDisableAdditionalDataPanel(key, subkey, additionalDataPanel);
			Model190AEAT2025DetailPanel.enableOrDisableAdditionalDataPanel2(key, subkey, additionalDataPanel2);
			Model190AEAT2025DetailPanel.enableOrDisableAdministrationPanel(key, administrationPanel);
			callback.onValueChanged(detail);
		});
		tab1.setWidget(4, 0, key);

		subkey.addChangeHandler(event -> {
			Mod1902025Key keyEnum = Mod1902025Key.values()[key.getSelectedIndex()];
			if (keyEnum.hasSubkeys()) {
				int idx = subkey.getSelectedIndex() == -1 ? 0 : subkey.getSelectedIndex();
				detail.setSubKey(keyEnum.getSubKeys()[idx]);
			} else {
				subkey.setEnabled(false);
				detail.setSubKey(null);
			}
			Model190AEAT2025DetailPanel.enableOrDisableIlPanel(key, subkey, ilPanel);
			Model190AEAT2025DetailPanel.enableOrDisableAdditionalDataPanel(key, subkey, additionalDataPanel);
			Model190AEAT2025DetailPanel.enableOrDisableAdditionalDataPanel2(key, subkey, additionalDataPanel2);
			callback.onValueChanged(detail);
		});
		tab1.setWidget(4, 1, subkey);
		
		AonIntegerBox accrualYear = new AonIntegerBox();
		accrualYear.setMaxLength(4);
		accrualYear.setVisibleLength(4);
		accrualYear.setValue(detail.getAccrualYear());
		accrualYear.addValueChangeHandler(event -> {
			detail.setAccrualYear(accrualYear.getValue());
			callback.onValueChanged(detail);
		});
		tab1.setWidget(4, 2, accrualYear);
		
		ListBox ceutaMelillaPalma = new ListBox();
		ceutaMelillaPalma.setWidth("140px");
		ceutaMelillaPalma.addItem("-");
		ceutaMelillaPalma.addItem("1 - Ceuta o Melilla");
		ceutaMelillaPalma.addItem("2 - Isla de La Palma"); 
		ceutaMelillaPalma.setSelectedIndex(detail.getCeutaMelillaPalma());
		ceutaMelillaPalma.addChangeHandler( event -> {
			detail.setCeutaMelillaPalma((byte) ceutaMelillaPalma.getSelectedIndex());
			callback.onValueChanged(detail);
		});
		tab1.setWidget(4, 3, ceutaMelillaPalma);
		
		// Tipos de prestaciones de la clave B.01 satisfechas en el ejercicio
		
		FlexTable tab12 = new FlexTable();
		panel.add(tab12);
		tab12.getColumnFormatter().setWidth(0, "80px");
		tab12.getColumnFormatter().setWidth(1, "80px");
		tab12.getColumnFormatter().setWidth(2, "80px");
		tab12.getColumnFormatter().setWidth(3, "80px");
		tab12.getColumnFormatter().setWidth(4, "auto");
		tab12.setStyleName(AON.CSS.aonWidthAll());
		tab12.addStyleName(AON.CSS.aonNowrap());
		
		tab12.setWidget(0, 0, new Model190SmallerLabel("Tipos de prestaciones de la clave B.01 satisfechas en el ejercicio:"));
		tab12.getFlexCellFormatter().setColSpan(0, 0, 5);

		CheckBox retirementPension = new CheckBox("01. Jubilaci\u00F3n");
		retirementPension.addStyleName(AON.CSS.aonFontSmaller());
		retirementPension.setValue(detail.isRetirementPension());
		retirementPension.addClickHandler(event -> {
			detail.setRetirementPension(retirementPension.getValue());
			callback.onValueChanged(detail);
		});
		tab12.setWidget(1, 0, retirementPension);
		
		CheckBox widowhoodPension = new CheckBox("02. Viudedad");
		widowhoodPension.addStyleName(AON.CSS.aonFontSmaller());
		widowhoodPension.setValue(detail.isWidowhoodPension());
		widowhoodPension.addClickHandler(event -> {
			detail.setWidowhoodPension(widowhoodPension.getValue());
			callback.onValueChanged(detail);
		});
		tab12.setWidget(1, 1, widowhoodPension);
		
		CheckBox permanentDisabilityPension = new CheckBox("03. Pensi\u00F3n por incapacidad permanente, total o parcial");
		permanentDisabilityPension.addStyleName(AON.CSS.aonFontSmaller());
		permanentDisabilityPension.setValue(detail.isPermanentDisabilityPension());
		permanentDisabilityPension.addClickHandler(event -> {
			detail.setPermanentDisabilityPension(permanentDisabilityPension.getValue());
			callback.onValueChanged(detail);
		});
		tab12.setWidget(1, 2, permanentDisabilityPension);
		
		CheckBox nonContributoryPension = new CheckBox("04. Pensi\u00F3n no contributiva por invalidez o jubilaci\u00F3n");
		nonContributoryPension.addStyleName(AON.CSS.aonFontSmaller());
		nonContributoryPension.setValue(detail.isNonContributoryPension());
		nonContributoryPension.addClickHandler(event -> {
			detail.setNonContributoryPension(nonContributoryPension.getValue());
			callback.onValueChanged(detail);
		});
		tab12.setWidget(1, 3, nonContributoryPension);
		
		CheckBox otherNonExemptPensions = new CheckBox("05. Resto de prestaciones art.17.2.a).1\u00AA Ley IRPF, no exentas, distintas de las anteriores");
		otherNonExemptPensions.addStyleName(AON.CSS.aonFontSmaller());
		otherNonExemptPensions.setValue(detail.isOtherNonExemptPensions());
		otherNonExemptPensions.addClickHandler(event -> {
			detail.setOtherNonExemptPensions(otherNonExemptPensions.getValue());
			callback.onValueChanged(detail);
		});
		tab12.setWidget(1, 4, otherNonExemptPensions);
		
		// Percepciones no derivadas de incapacidad laboral
		
		FlexTable tab2 = new FlexTable();
		panel.add(tab2);
		tab2.getColumnFormatter().setWidth(0, "70px");
		tab2.getColumnFormatter().setWidth(1, WIDTH_150PX);
		tab2.getColumnFormatter().setWidth(2, WIDTH_150PX);
		tab2.getColumnFormatter().setWidth(3, WIDTH_150PX);
		tab2.getColumnFormatter().setWidth(4, "auto");
		tab2.setStyleName(AON.CSS.aonWidthAll());
		tab2.addStyleName(AON.CSS.aonNowrap());
           
		tab2.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
		tab2.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
		tab2.getFlexCellFormatter().setColSpan(0, 0, 5);
		tab2.setWidget(0, 0, new InlineLabel("Percepciones no derivadas de incapacidad laboral"));
		
		tab2.setWidget(1, 0, new Label());
		tab2.setWidget(1, 1, new Model190SmallerLabel(AON.MSG.perception()));
		tab2.setWidget(1, 2, new Model190SmallerLabel(AON.MSG.retention()));
		tab2.setWidget(1, 3, new Label());
		tab2.setWidget(1, 4, new Label());
		
		tab2.setWidget(2, 0, new Model190SmallerLabel(AON.MSG.money()));
		
		AonDoubleBox perception = new AonDoubleBox();
		perception.setValue(detail.getPerception());
		perception.addValueChangeHandler(event -> {
			detail.setPerception(perception.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(2, 1, perception);

		AonDoubleBox retention = new AonDoubleBox();
		retention.setValue(detail.getRetention());
		retention.addValueChangeHandler(event -> {
			detail.setRetention(retention.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(2, 2, retention);

		tab2.setWidget(2, 3, new Label());
		tab2.setWidget(2, 4, new Label());
		
//		CheckBox entrepreneurship = new CheckBox(AON.MSG.entrepreneurship());
//		entrepreneurship.addStyleName(AON.CSS.aonFontSmaller());		
//		entrepreneurship.setValue(detail.isEntrepreneurship());
//		entrepreneurship.addClickHandler(event -> {
//			detail.setEntrepreneurship(entrepreneurship.getValue());
//			callback.onValueChanged(detail);
//		});
//		tab2.setWidget(2, 4, entrepreneurship);
		tab2.setWidget(3, 4, new Label());

		tab2.setWidget(3, 0, new Label());
		tab2.setWidget(3, 1, new Model190SmallerLabel(AON.MSG.inKindPerception()));
		tab2.setWidget(3, 2, new Model190SmallerLabel(AON.MSG.inKindDeposit()));
		tab2.setWidget(3, 3, new Model190SmallerLabel(AON.MSG.inKindOutputDeposit()));
		tab2.setWidget(3, 4, new Label());
		
		tab2.setWidget(4, 0, new Model190SmallerLabel(AON.MSG.inKind()));

		AonDoubleBox inKindPerception = new AonDoubleBox();
		inKindPerception.setValue(detail.getInKindPerception());
		inKindPerception.addValueChangeHandler(event -> {
			detail.setInKindPerception(inKindPerception.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(4, 1, inKindPerception);

		AonDoubleBox inKindDeposit = new AonDoubleBox();
		inKindDeposit.setValue(detail.getInKindDeposit());
		inKindDeposit.addValueChangeHandler(event -> {
			detail.setInKindDeposit(inKindDeposit.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(4, 2, inKindDeposit);

		AonDoubleBox inKindOutputDeposit = new AonDoubleBox();
		inKindOutputDeposit.setValue(detail.getInKindOutputDeposit());
		inKindOutputDeposit.addValueChangeHandler(event -> {
			detail.setInKindOutputDeposit(inKindOutputDeposit.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(4, 3, inKindOutputDeposit);
		
//		CheckBox excesses = new CheckBox("Excesos entrega acciones empresas emergentes");
//		excesses.setValue(detail.isExcesses());
//		excesses.addClickHandler(event -> {
//			detail.setExcesses(excesses.getValue());
//			callback.onValueChanged(detail);
//		});
//		tab2.setWidget(4, 4, excesses);
		tab2.setWidget(4, 4, new Label());
		
		CheckBox entrepreneurship = new CheckBox(AON.MSG.entrepreneurship());
		entrepreneurship.addStyleName(AON.CSS.aonFontSmaller());		
		entrepreneurship.setValue(detail.isEntrepreneurship());
		entrepreneurship.addClickHandler(event -> {
			detail.setEntrepreneurship(entrepreneurship.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(5, 0, entrepreneurship);
		tab2.getFlexCellFormatter().setColSpan(5, 0, 5);
		
		CheckBox excesses = new CheckBox("Excesos entrega acciones empresas emergentes");
		excesses.addStyleName(AON.CSS.aonFontSmaller());
		excesses.setValue(detail.isExcesses());
		excesses.addClickHandler(event -> {
			detail.setExcesses(excesses.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(6, 0, excesses);
		tab2.getFlexCellFormatter().setColSpan(6, 0, 5);
		
		// Percepciones derivadas de incapacidad laboral (sólo para clave A y B.01)

		FlexTable tab3 = new FlexTable();
		tab3.getColumnFormatter().setWidth(0, "70px");
		tab3.getColumnFormatter().setWidth(1, WIDTH_150PX);
		tab3.getColumnFormatter().setWidth(2, WIDTH_150PX);
		tab3.getColumnFormatter().setWidth(3, WIDTH_150PX);
		tab3.getColumnFormatter().setWidth(4, "auto");
		tab3.setStyleName(AON.CSS.aonWidthAll());
		tab3.addStyleName(AON.CSS.aonNowrap());

		tab3.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
		tab3.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
		tab3.getFlexCellFormatter().setColSpan(0, 0, 5);
		tab3.setWidget(0, 0, new InlineLabel("Percepciones derivadas de incapacidad laboral (s\u00F3lo para claves A y B.01)"));

		tab3.setWidget(1, 0, new Label());
		tab3.setWidget(1, 1, new Model190SmallerLabel(AON.MSG.perceptionValoration()));
		tab3.setWidget(1, 2, new Model190SmallerLabel(AON.MSG.retentionIncome()));
		tab3.setWidget(1, 3, new Model190SmallerLabel(AON.MSG.inKindOutputDeposit()));
		tab3.setWidget(1, 4, new Label());

		tab3.setWidget(2, 0, new Model190SmallerLabel(AON.MSG.money()));

		AonDoubleBox perceptionIL = new AonDoubleBox();
		perceptionIL.setValue(detail.getPerceptionIL());
		perceptionIL.addValueChangeHandler(event -> {
			detail.setPerceptionIL(perceptionIL.getValue());
			callback.onValueChanged(detail);
		});
		tab3.setWidget(2, 1, perceptionIL);

		AonDoubleBox retentionIL = new AonDoubleBox();
		retentionIL.setValue(detail.getRetentionIL());
		retentionIL.addValueChangeHandler(event -> {
			detail.setRetentionIL(retentionIL.getValue());
			callback.onValueChanged(detail);
		});
		tab3.setWidget(2, 2, retentionIL);

		tab3.setWidget(3, 0, new Model190SmallerLabel(AON.MSG.inKind()));

		AonDoubleBox inKindPerceptionIL = new AonDoubleBox();
		inKindPerceptionIL.setValue(detail.getInKindPerceptionIL());
		inKindPerceptionIL.addValueChangeHandler(event -> {
			detail.setInKindPerceptionIL(inKindPerceptionIL.getValue());
			callback.onValueChanged(detail);
		});
		tab3.setWidget(3, 1, inKindPerceptionIL);

		AonDoubleBox inKindDepositIL = new AonDoubleBox();
		inKindDepositIL.setValue(detail.getInKindDepositIL());
		inKindDepositIL.addValueChangeHandler(event -> {
			detail.setInKindDepositIL(inKindDepositIL.getValue());
			callback.onValueChanged(detail);
		});
		tab3.setWidget(3, 2, inKindDepositIL);

		AonDoubleBox inKindOutputDepositIL = new AonDoubleBox();
		inKindOutputDepositIL.setValue(detail.getInKindOutputDepositIL());
		inKindOutputDepositIL.addValueChangeHandler(event -> {
			detail.setInKindOutputDepositIL(inKindOutputDepositIL.getValue());
			callback.onValueChanged(detail);
		});
		tab3.setWidget(3, 3, inKindOutputDepositIL);

		Model190AEAT2025DetailPanel.enableOrDisableIlPanel(key, subkey, ilPanel);
		ilPanel.add(tab3);
		panel.add(ilPanel);
		
		// Retenciones e ingresos a cuenta ingresados en el Estado, en las Diputaciones Forales del País Vasco y en la Comunidad Foral de Navarra (sólo en percepciones de la clave E)

		FlexTable tab31 = new FlexTable();
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
						+ "Navarra (s\u00F3lo en percepciones de la clave E)"));
		
		tab31.setWidget(1, 0, new Model190SmallerLabel("Hacienda Estatal"));
		tab31.setWidget(1, 1, new Model190SmallerLabel("Com. Foral Navarra"));
		tab31.setWidget(1, 2, new Model190SmallerLabel("Dip. Foral Araba/\u00C1lava"));
		tab31.setWidget(1, 3, new Model190SmallerLabel("Dip. Foral Gipuzkoa"));
		tab31.setWidget(1, 4, new Model190SmallerLabel("Dip. Foral Bizkaia"));
		tab31.setWidget(1, 5, new Label());
		
		AonDoubleBox commonRetention = new AonDoubleBox();
		commonRetention.setValue(detail.getCommonRetention());
		commonRetention.addValueChangeHandler(event -> {
			detail.setCommonRetention(commonRetention.getValue());
			callback.onValueChanged(detail);
		});
		tab31.setWidget(2, 0, commonRetention);

		AonDoubleBox navarraRetention = new AonDoubleBox();
		navarraRetention.setValue(detail.getNavarraRetention());
		navarraRetention.addValueChangeHandler(event -> {
			detail.setNavarraRetention(navarraRetention.getValue());
			callback.onValueChanged(detail);
		});
		tab31.setWidget(2, 1, navarraRetention);
		
		AonDoubleBox arabaRetention = new AonDoubleBox();
		arabaRetention.setValue(detail.getArabaRetention());
		arabaRetention.addValueChangeHandler(event -> {
			detail.setArabaRetention(arabaRetention.getValue());
			callback.onValueChanged(detail);
		});
		tab31.setWidget(2, 2, arabaRetention);
		
		AonDoubleBox gipuzkoaRetention = new AonDoubleBox();
		gipuzkoaRetention.setValue(detail.getGipuzkoaRetention());
		gipuzkoaRetention.addValueChangeHandler(event -> {
			detail.setGipuzkoaRetention(gipuzkoaRetention.getValue());
			callback.onValueChanged(detail);
		});
		tab31.setWidget(2, 3, gipuzkoaRetention);

		AonDoubleBox bizkaiaRetention = new AonDoubleBox();
		bizkaiaRetention.setValue(detail.getBizkaiaRetention());
		bizkaiaRetention.addValueChangeHandler(event -> {
			detail.setBizkaiaRetention(bizkaiaRetention.getValue());
			callback.onValueChanged(detail);
		});
		tab31.setWidget(2, 4, bizkaiaRetention);

		tab31.setWidget(2, 5, new Label());
		
		Model190AEAT2025DetailPanel.enableOrDisableAdministrationPanel(key, administrationPanel);
		administrationPanel.add(tab31);
		panel.add(administrationPanel);
		
		// Datos adicionales

		FlexTable tab4 = new FlexTable();
		tab4.getColumnFormatter().setWidth(0, "110px");
		tab4.getColumnFormatter().setWidth(1, "80px");
		tab4.getColumnFormatter().setWidth(2, WIDTH_100PX);
		tab4.getColumnFormatter().setWidth(3, "50px");
		tab4.getColumnFormatter().setWidth(4, "80px");
		tab4.getColumnFormatter().setWidth(5, WIDTH_100PX);

		tab4.getColumnFormatter().setWidth(6, WIDTH_100PX);
		tab4.getColumnFormatter().setWidth(7, WIDTH_100PX);
		tab4.getColumnFormatter().setWidth(8, WIDTH_100PX);
		tab4.getColumnFormatter().setWidth(9, WIDTH_100PX);

		tab4.getColumnFormatter().setWidth(10, WIDTH_100PX);
		tab4.getColumnFormatter().setWidth(11, "50px");
		tab4.getColumnFormatter().setWidth(12, "120px");
		tab4.getColumnFormatter().setWidth(13, "auto");
		tab4.setStyleName(AON.CSS.aonWidthAll());
		tab4.addStyleName(AON.CSS.aonNowrap());

		tab4.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
		tab4.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
		tab4.getFlexCellFormatter().setColSpan(0, 0, 13);
		tab4.setWidget(0, 0, new InlineLabel(AON.MSG.additionalData()));

		tab4.setWidget(1, 0, new Model190SmallerLabel(AON.MSG.birthYear()));
		AonIntegerBox birthYear = new AonIntegerBox();
		birthYear.setMaxLength(4);
		birthYear.setVisibleLength(4);
		birthYear.setValue(detail.getBirthYear());
		birthYear.addValueChangeHandler(event -> {
			detail.setBirthYear(birthYear.getValue());
			callback.onValueChanged(detail);
		});
		tab4.setWidget(1, 1, birthYear);

		tab4.setWidget(1, 2, new Model190SmallerLabel(AON.MSG.familySituation()));
		ListBox familySituation = new ListBox();
		familySituation.setWidth("40px");
		familySituation.addItem("-");
		familySituation.addItem("1 - Soltero, viudo, divorciado ... (consulte instrucciones)");
		familySituation.addItem("2 - Casado y no separado legalmente ... (consulte instrucciones)");
		familySituation.addItem("3 - Distinta de las anteriores ... (consulte instrucciones)");
		familySituation.setSelectedIndex(detail.getFamilySituation());
		familySituation.addChangeHandler(event -> {
			detail.setFamilySituation((byte) familySituation.getSelectedIndex());
			callback.onValueChanged(detail);
		});
		tab4.setWidget(1, 3, familySituation);

		tab4.setWidget(1, 4, new Model190SmallerLabel(AON.MSG.spouseDocument()));
		AonDocumentTextBox spouseDocument = new AonDocumentTextBox();
		spouseDocument.setVisibleLength(9);
		spouseDocument.setMaxLength(9);

		spouseDocument.setValue(detail.getSpouseDocument());
		spouseDocument.addValueChangeHandler(event -> {
			detail.setSpouseDocument(spouseDocument.getValue());
			callback.onValueChanged(detail);
		});
		tab4.setWidget(1, 5, spouseDocument);

		tab4.setWidget(1, 6, new Model190SmallerLabel("Tit. unidad conviv."));
		ListBox titConvivivencia = new ListBox();
		titConvivivencia.setWidth("40px");
		titConvivivencia.addItem("----");
		titConvivivencia.addItem("1 - El perceptor es el titular de la unidad de convivencia.");
		titConvivivencia.addItem("2 - El perceptor NO es el titular de la unidad de convivencia.");
		titConvivivencia.setSelectedIndex(detail.getTitConvivencia());
		titConvivivencia.addChangeHandler(event -> {
			detail.setTitConvivencia((byte) titConvivivencia.getSelectedIndex());
			callback.onValueChanged(detail);
		});
		tab4.setWidget(1, 7, titConvivivencia);

		tab4.setWidget(1, 8, new Model190SmallerLabel("Compl. ayuda infancia"));
		ListBox compInfancia = new ListBox();
		compInfancia.setWidth("40px");
		compInfancia.addItem("----");
		compInfancia.addItem("1 - La prestaci\u00F3n incluye cuant\u00EDas complemento de ayuda para la infancia previsto en el IMV");
		compInfancia.addItem("2 - La prestaci\u00F3n NO incluye cuant\u00EDas complemento de ayuda para la infancia previsto en el IMV");
		compInfancia.setSelectedIndex(detail.getCompInfancia());
		compInfancia.addChangeHandler(event -> {
			detail.setCompInfancia((byte) compInfancia.getSelectedIndex());
			callback.onValueChanged(detail);
		});
		tab4.setWidget(1, 9, compInfancia);

		tab4.setWidget(1, 10, new Model190SmallerLabel(AON.MSG.disability()));
		ListBox disability = new ListBox();
		disability.setWidth("40px");
		disability.addItem("0 - No padece ninguna discapacidad o grado de minusval\u00EDa es inferior al 33 por 100.");
		disability.addItem("1 - Grado de minusval\u00EDa >= 33% y <65%");
		disability.addItem("2 - Grado de minusval\u00EDa >= 33% y <65% ...(consulte instrucciones)");
		disability.addItem("3 - Grado de minusval\u00EDa >= 65%.");
		disability.setSelectedIndex(detail.getDisability());
		disability.addChangeHandler(event -> {
			detail.setDisability((byte) disability.getSelectedIndex());
			callback.onValueChanged(detail);
		});
		tab4.setWidget(1, 11, disability);

		tab4.setWidget(1, 12, new Model190SmallerLabel(AON.MSG.contract()));
		ListBox contract = new ListBox();
		contract.setWidth("40px");
		contract.addItem("-");
		contract.addItem("1 - Contrato o relaci\u00F3n de car\u00E1cter general,  ... (consulte instrucciones)");
		contract.addItem("2 - Contrato o relaci\u00F3n de duraci\u00F3n inferior al a\u00F1o ...(consulte instrucciones).");
		contract.addItem("3 - Otras relaciones laborales especiales ... (consulte instrucciones)");
		contract.addItem("4 - Relaci\u00F3n espor\u00E1dica propia de los trabajadores manuales ... (consulte instrucciones)");
		contract.setSelectedIndex(detail.getContract());
		contract.addChangeHandler(event -> {
			detail.setContract((byte) contract.getSelectedIndex());
			callback.onValueChanged(detail);
		});
		tab4.setWidget(1, 13, contract);

		additionalDataPanel.add(tab4);

		FlexTable tab5 = new FlexTable();
		tab5.getColumnFormatter().setWidth(0, WIDTH_150PX);
		tab5.getColumnFormatter().setWidth(1, WIDTH_150PX);
		tab5.getColumnFormatter().setWidth(2, WIDTH_150PX);
		tab5.getColumnFormatter().setWidth(3, "170px");
		tab5.getColumnFormatter().setWidth(4, WIDTH_150PX);
		tab5.getColumnFormatter().setWidth(5, "auto");
		tab5.setStyleName(AON.CSS.aonWidthAll());
		tab5.addStyleName(AON.CSS.aonNowrap());

		CheckBox geographicMobility = new CheckBox(AON.MSG.geographicMobility());
		geographicMobility.addStyleName(AON.CSS.aonFontSmaller());
		geographicMobility.setValue(detail.isGeographicMobility());
		geographicMobility.addClickHandler(event -> {
			detail.setGeographicMobility(geographicMobility.getValue());
			callback.onValueChanged(detail);
		});
		tab5.setWidget(0, 0, geographicMobility);

		tab5.setWidget(0, 1, new Model190SmallerLabel(AON.MSG.applicableReduction()));
		tab5.setWidget(0, 2, new Model190SmallerLabel(AON.MSG.deducibleExpense()));
		tab5.setWidget(0, 3, new Model190SmallerLabel(AON.MSG.compensatoryPension()));
		tab5.setWidget(0, 4, new Model190SmallerLabel(AON.MSG.foodAnnuality()));
		tab5.setWidget(0, 5, new Label());

		CheckBox homeLoanCommunnication = new CheckBox(AON.MSG.homeLoanCommunnication());
		homeLoanCommunnication.addStyleName(AON.CSS.aonFontSmaller());
		homeLoanCommunnication.setValue(detail.isHomeLoanCommunnication());
		homeLoanCommunnication.addClickHandler(event -> {
			detail.setHomeLoanCommunnication(homeLoanCommunnication.getValue());
			callback.onValueChanged(detail);
		});
		tab5.setWidget(1, 0, homeLoanCommunnication);

		AonDoubleBox applicableReduction1 = new AonDoubleBox();
		AonDoubleBox applicableReduction = new AonDoubleBox();
		applicableReduction.setValue(detail.getApplicableReduction());
		applicableReduction.addValueChangeHandler(event -> {
			detail.setApplicableReduction(applicableReduction.getValue());
			applicableReduction1.setValue(applicableReduction.getValue(), false);
			callback.onValueChanged(detail);
		});
		tab5.setWidget(1, 1, applicableReduction);

		AonDoubleBox deducibleExpense = new AonDoubleBox();
		deducibleExpense.setValue(detail.getDeducibleExpense());
		deducibleExpense.addValueChangeHandler(event -> {
			detail.setDeducibleExpense(deducibleExpense.getValue());
			callback.onValueChanged(detail);
		});
		tab5.setWidget(1, 2, deducibleExpense);

		AonDoubleBox compensatoryPension = new AonDoubleBox();
		compensatoryPension.setValue(detail.getCompensatoryPension());
		compensatoryPension.addValueChangeHandler(event -> {
			detail.setCompensatoryPension(compensatoryPension.getValue());
			callback.onValueChanged(detail);
		});
		tab5.setWidget(1, 3, compensatoryPension);

		AonDoubleBox foodAnnuality = new AonDoubleBox();
		foodAnnuality.setValue(detail.getFoodAnnuality());
		foodAnnuality.addValueChangeHandler(event -> {
			detail.setFoodAnnuality(foodAnnuality.getValue());
			callback.onValueChanged(detail);
		});
		tab5.setWidget(1, 4, foodAnnuality);

		additionalDataPanel.add(tab5);

		FlexTable tab6 = new FlexTable();
		tab6.getColumnFormatter().setWidth(0, "220px");
		tab6.getColumnFormatter().setWidth(1, "60px");
		tab6.getColumnFormatter().setWidth(2, "60px");
		tab6.getColumnFormatter().setWidth(3, "50px");
		tab6.getColumnFormatter().setWidth(4, "60px");
		tab6.getColumnFormatter().setWidth(5, "60px");
		tab6.getColumnFormatter().setWidth(6, WIDTH_150PX);
		tab6.getColumnFormatter().setWidth(7, "40px");
		tab6.getColumnFormatter().setWidth(8, "40px");
		tab6.getColumnFormatter().setWidth(9, "auto");
		tab6.setStyleName(AON.CSS.aonWidthAll());
		tab6.addStyleName(AON.CSS.aonNowrap());

		Model190SmallerLabel descendant = new Model190SmallerLabel(AonStringUtils.abbreviate(AON.MSG.descendant(), 34));
		descendant.addStyleName(AON.CSS.aonBold());
		tab6.setWidget(0, 0, descendant);
		tab6.setWidget(0, 1, new Model190SmallerLabel(AON.MSG.total()));
		tab6.setWidget(0, 2, new Model190SmallerLabel(AON.MSG.byInteger()));
		tab6.setWidget(0, 3, new Label());
		tab6.setWidget(0, 4, new Model190SmallerLabel(AON.MSG.total()));
		tab6.setWidget(0, 5, new Model190SmallerLabel(AON.MSG.byInteger()));
		tab6.setWidget(0, 6, new Label());
		tab6.setWidget(0, 7, new Model190SmallerLabel(AON.MSG.first()));
		tab6.setWidget(0, 8, new Model190SmallerLabel(AON.MSG.second()));
		tab6.setWidget(0, 9, new Model190SmallerLabel(AON.MSG.third()));

		tab6.setWidget(1, 0, new Model190SmallerLabel(AON.MSG.lessThan3()));
		tab6.getCellFormatter().setStyleName(1, 0, AON.CSS.aonTextRight());
		AonIntegerBox lessThan3Descendent = new AonIntegerBox();
		lessThan3Descendent.setMaxLength(1);
		lessThan3Descendent.setVisibleLength(1);
		lessThan3Descendent.setValue(detail.getLessThan3Descendent());
		lessThan3Descendent.addValueChangeHandler(event -> {
			detail.setLessThan3Descendent(AonNumberUtils.toByte(lessThan3Descendent.getValue()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(1, 1, lessThan3Descendent);

		AonIntegerBox lessThan3DescendentRatio = new AonIntegerBox();
		lessThan3DescendentRatio.setMaxLength(1);
		lessThan3DescendentRatio.setVisibleLength(1);
		lessThan3DescendentRatio.setValue(detail.getLessThan3DescendentRatio());
		lessThan3DescendentRatio.addValueChangeHandler(event -> {
			detail.setLessThan3DescendentRatio(AonNumberUtils.toByte(lessThan3DescendentRatio.getValue()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(1, 2, lessThan3DescendentRatio);

		tab6.setWidget(1, 3, new Model190SmallerLabel(AON.MSG.remainder()));
		tab6.getCellFormatter().setStyleName(1, 3, AON.CSS.aonTextRight());
		AonIntegerBox otherDescendent = new AonIntegerBox();
		otherDescendent.setMaxLength(2);
		otherDescendent.setVisibleLength(2);
		otherDescendent.setValue(detail.getOtherDescendent());
		otherDescendent.addValueChangeHandler(event -> {
			detail.setOtherDescendent(AonNumberUtils.toByte(otherDescendent.getValue()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(1, 4, otherDescendent);

		AonIntegerBox otherDescendentRatio = new AonIntegerBox();
		otherDescendentRatio.setMaxLength(2);
		otherDescendentRatio.setVisibleLength(2);
		otherDescendentRatio.setValue(detail.getOtherDescendentRatio());
		otherDescendentRatio.addValueChangeHandler(event -> {
			detail.setOtherDescendentRatio(AonNumberUtils.toByte(otherDescendentRatio.getValue()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(1, 5, otherDescendentRatio);

		tab6.setWidget(1, 6, new Model190SmallerLabel(AON.MSG.first3Calculation()));
		ListBox firstChildCalculation = new ListBox();
		firstChildCalculation.setWidth("40px");
		firstChildCalculation.addItem("-");
		firstChildCalculation.addItem(COMPUTADO_POR_ENTERO);
		firstChildCalculation.addItem(COMPUTADO_POR_MITAD);
		firstChildCalculation.setSelectedIndex(detail.getFirstChildCalculation());
		firstChildCalculation.addChangeHandler(event -> {
			detail.setFirstChildCalculation(AonNumberUtils.toByte(firstChildCalculation.getSelectedIndex()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(1, 7, firstChildCalculation);

		ListBox secondChildCalculation = new ListBox();
		secondChildCalculation.setWidth("40px");
		secondChildCalculation.addItem("-");
		secondChildCalculation.addItem(COMPUTADO_POR_ENTERO);
		secondChildCalculation.addItem(COMPUTADO_POR_MITAD);
		secondChildCalculation.setSelectedIndex(detail.getSecondChildCalculation());
		secondChildCalculation.addChangeHandler(event -> {
			detail.setSecondChildCalculation(AonNumberUtils.toByte(secondChildCalculation.getSelectedIndex()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(1, 8, secondChildCalculation);

		ListBox thirdChildCalculation = new ListBox();
		thirdChildCalculation.setWidth("40px");
		thirdChildCalculation.addItem("-");
		thirdChildCalculation.addItem(COMPUTADO_POR_ENTERO);
		thirdChildCalculation.addItem(COMPUTADO_POR_MITAD);
		thirdChildCalculation.setSelectedIndex(detail.getThirdChildCalculation());
		thirdChildCalculation.addChangeHandler(event -> {
			detail.setThirdChildCalculation(AonNumberUtils.toByte(thirdChildCalculation.getSelectedIndex()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(1, 9, thirdChildCalculation);

		Model190SmallerLabel disabilityDescendant = new Model190SmallerLabel(
				AonStringUtils.abbreviate(AON.MSG.disabilityDescendant(), 34));
		disabilityDescendant.addStyleName(AON.CSS.aonBold());
		tab6.setWidget(2, 0, disabilityDescendant);
		tab6.setWidget(2, 1, new Model190SmallerLabel(AON.MSG.total()));
		tab6.setWidget(2, 2, new Model190SmallerLabel(AON.MSG.byInteger()));
		tab6.setWidget(2, 3, new Label());
		tab6.setWidget(2, 4, new Model190SmallerLabel(AON.MSG.total()));
		tab6.setWidget(2, 5, new Model190SmallerLabel(AON.MSG.byInteger()));
		tab6.setWidget(2, 6, new Label());
		tab6.setWidget(2, 7, new Model190SmallerLabel(AON.MSG.total()));
		tab6.setWidget(2, 8, new Model190SmallerLabel(AON.MSG.byInteger()));
		tab6.setWidget(2, 9, new Label());

		tab6.setWidget(3, 0, new Model190SmallerLabel(AON.MSG.moreThan33lessThan65()));
		tab6.getCellFormatter().setStyleName(3, 0, AON.CSS.aonTextRight());
		AonIntegerBox disabilityDescendent33 = new AonIntegerBox();
		disabilityDescendent33.setMaxLength(2);
		disabilityDescendent33.setVisibleLength(2);
		disabilityDescendent33.setValue(detail.getDisabilityDescendent33());
		disabilityDescendent33.addValueChangeHandler(event -> {
			detail.setDisabilityDescendent33(AonNumberUtils.toByte(disabilityDescendent33.getValue()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(3, 1, disabilityDescendent33);

		AonIntegerBox disabilityDescendent33Ratio = new AonIntegerBox();
		disabilityDescendent33Ratio.setMaxLength(2);
		disabilityDescendent33Ratio.setVisibleLength(2);
		disabilityDescendent33Ratio.setValue(detail.getDisabilityDescendent33Ratio());
		disabilityDescendent33Ratio.addValueChangeHandler(event -> {
			detail.setDisabilityDescendent33Ratio(AonNumberUtils.toByte(disabilityDescendent33Ratio.getValue()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(3, 2, disabilityDescendent33Ratio);

		tab6.setWidget(3, 3, new Model190SmallerLabel(AON.MSG.reducedMovilitiy()));
		tab6.getCellFormatter().setStyleName(3, 3, AON.CSS.aonTextRight());
		AonIntegerBox disabilityDescendentDependence = new AonIntegerBox();
		disabilityDescendentDependence.setMaxLength(2);
		disabilityDescendentDependence.setVisibleLength(2);
		disabilityDescendentDependence.setValue(detail.getDisabilityDescendentDependence());
		disabilityDescendentDependence.addValueChangeHandler(event -> {
			detail.setDisabilityDescendentDependence(AonNumberUtils.toByte(disabilityDescendentDependence.getValue()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(3, 4, disabilityDescendentDependence);

		AonIntegerBox disabilityDescendentDependenceRatio = new AonIntegerBox();
		disabilityDescendentDependenceRatio.setMaxLength(2);
		disabilityDescendentDependenceRatio.setVisibleLength(2);
		disabilityDescendentDependenceRatio.setValue(detail.getDisabilityDescendentDependenceRatio());
		disabilityDescendentDependenceRatio.addValueChangeHandler(event -> {
			detail.setDisabilityDescendentDependenceRatio(
					AonNumberUtils.toByte(disabilityDescendentDependenceRatio.getValue()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(3, 5, disabilityDescendentDependenceRatio);

		tab6.setWidget(3, 6, new Model190SmallerLabel(AON.MSG.moreThan65()));
		tab6.getCellFormatter().setStyleName(3, 6, AON.CSS.aonTextRight());
		AonIntegerBox disabilityDescendent65 = new AonIntegerBox();
		disabilityDescendent65.setMaxLength(2);
		disabilityDescendent65.setVisibleLength(2);
		disabilityDescendent65.setValue(detail.getDisabilityDescendent65());
		disabilityDescendent65.addValueChangeHandler(event -> {
			detail.setDisabilityDescendent65(AonNumberUtils.toByte(disabilityDescendent65.getValue()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(3, 7, disabilityDescendent65);

		AonIntegerBox disabilityDescendent65Ratio = new AonIntegerBox();
		disabilityDescendent65Ratio.setMaxLength(2);
		disabilityDescendent65Ratio.setVisibleLength(2);
		disabilityDescendent65Ratio.setValue(detail.getDisabilityDescendent65Ratio());
		disabilityDescendent65Ratio.addValueChangeHandler(event -> {
			detail.setDisabilityDescendent65Ratio(AonNumberUtils.toByte(disabilityDescendent65Ratio.getValue()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(3, 8, disabilityDescendent65Ratio);
		tab6.setWidget(3, 9, new Label());

		Model190SmallerLabel ascendantLabel = new Model190SmallerLabel(
				AonStringUtils.abbreviate(AON.MSG.ascendant(), 34));
		ascendantLabel.addStyleName(AON.CSS.aonBold());
		tab6.setWidget(4, 0, ascendantLabel);
		tab6.setWidget(4, 1, new Model190SmallerLabel(AON.MSG.total()));
		tab6.setWidget(4, 2, new Model190SmallerLabel(AON.MSG.byInteger()));
		tab6.setWidget(4, 3, new Label());
		tab6.setWidget(4, 4, new Model190SmallerLabel(AON.MSG.total()));
		tab6.setWidget(4, 5, new Model190SmallerLabel(AON.MSG.byInteger()));
		tab6.setWidget(4, 6, new Label());
		tab6.setWidget(4, 7, new Label());
		tab6.setWidget(4, 8, new Label());
		tab6.setWidget(4, 9, new Label());

		tab6.setWidget(5, 0, new Model190SmallerLabel(AON.MSG.lessThan75()));
		tab6.getCellFormatter().setStyleName(5, 0, AON.CSS.aonTextRight());
		AonIntegerBox lessThan75Ascendant = new AonIntegerBox();
		lessThan75Ascendant.setMaxLength(1);
		lessThan75Ascendant.setVisibleLength(1);
		lessThan75Ascendant.setValue(detail.getLessThan75Ascendant());
		lessThan75Ascendant.addValueChangeHandler(event -> {
			detail.setLessThan75Ascendant(AonNumberUtils.toByte(lessThan75Ascendant.getValue()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(5, 1, lessThan75Ascendant);

		AonIntegerBox lessThan75AscendantRatio = new AonIntegerBox();
		lessThan75AscendantRatio.setMaxLength(1);
		lessThan75AscendantRatio.setVisibleLength(1);
		lessThan75AscendantRatio.setValue(detail.getLessThan75AscendantRatio());
		lessThan75AscendantRatio.addValueChangeHandler(event -> {
			detail.setLessThan75AscendantRatio(AonNumberUtils.toByte(lessThan75AscendantRatio.getValue()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(5, 2, lessThan75AscendantRatio);

		tab6.setWidget(5, 3, new Model190SmallerLabel(AON.MSG.greatherThan75()));
		tab6.getCellFormatter().setStyleName(5, 3, AON.CSS.aonTextRight());
		AonIntegerBox ascendant = new AonIntegerBox();
		ascendant.setMaxLength(1);
		ascendant.setVisibleLength(1);
		ascendant.setValue(detail.getAscendant());
		ascendant.addValueChangeHandler(event -> {
			detail.setAscendant(AonNumberUtils.toByte(ascendant.getValue()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(5, 4, ascendant);

		AonIntegerBox ascendantRatio = new AonIntegerBox();
		ascendantRatio.setMaxLength(1);
		ascendantRatio.setVisibleLength(1);
		ascendantRatio.setValue(detail.getAscendantRatio());
		ascendantRatio.addValueChangeHandler(event -> {
			detail.setAscendantRatio(AonNumberUtils.toByte(ascendantRatio.getValue()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(5, 5, ascendantRatio);

		tab6.setWidget(5, 6, new Label());
		tab6.setWidget(5, 7, new Label());
		tab6.setWidget(5, 8, new Label());

		Model190SmallerLabel disabilityAscendant = new Model190SmallerLabel(
				AonStringUtils.abbreviate(AON.MSG.disabilityAscendant(), 34));
		disabilityAscendant.addStyleName(AON.CSS.aonBold());
		tab6.setWidget(6, 0, disabilityAscendant);
		tab6.setWidget(6, 1, new Model190SmallerLabel(AON.MSG.total()));
		tab6.setWidget(6, 2, new Model190SmallerLabel(AON.MSG.byInteger()));
		tab6.setWidget(6, 3, new Label());
		tab6.setWidget(6, 4, new Model190SmallerLabel(AON.MSG.total()));
		tab6.setWidget(6, 5, new Model190SmallerLabel(AON.MSG.byInteger()));
		tab6.setWidget(6, 6, new Label());
		tab6.setWidget(6, 7, new Model190SmallerLabel(AON.MSG.total()));
		tab6.setWidget(6, 8, new Model190SmallerLabel(AON.MSG.byInteger()));
		tab6.setWidget(6, 9, new Label());
		additionalDataPanel.add(tab6);

		tab6.setWidget(7, 0, new Model190SmallerLabel(AON.MSG.moreThan33lessThan65()));
		tab6.getCellFormatter().setStyleName(7, 0, AON.CSS.aonTextRight());
		AonIntegerBox disabilityAscendant33 = new AonIntegerBox();
		disabilityAscendant33.setMaxLength(1);
		disabilityAscendant33.setVisibleLength(1);
		disabilityAscendant33.setValue(detail.getDisabilityAscendant33());
		disabilityAscendant33.addValueChangeHandler(event -> {
			detail.setDisabilityAscendant33(AonNumberUtils.toByte(disabilityAscendant33.getValue()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(7, 1, disabilityAscendant33);

		AonIntegerBox disabilityAscendant33Ratio = new AonIntegerBox();
		disabilityAscendant33Ratio.setMaxLength(1);
		disabilityAscendant33Ratio.setVisibleLength(1);
		disabilityAscendant33Ratio.setValue(detail.getDisabilityAscendant33Ratio());
		disabilityAscendant33Ratio.addValueChangeHandler(event -> {
			detail.setDisabilityAscendant33Ratio(AonNumberUtils.toByte(disabilityAscendant33Ratio.getValue()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(7, 2, disabilityAscendant33Ratio);

		tab6.setWidget(7, 3, new Model190SmallerLabel(AON.MSG.reducedMovilitiy()));
		tab6.getCellFormatter().setStyleName(7, 3, AON.CSS.aonTextRight());
		AonIntegerBox disabilityAscendantDependence = new AonIntegerBox();
		disabilityAscendantDependence.setMaxLength(1);
		disabilityAscendantDependence.setVisibleLength(1);
		disabilityAscendantDependence.setValue(detail.getDisabilityAscendantDependence());
		disabilityAscendantDependence.addValueChangeHandler(event -> {
			detail.setDisabilityAscendantDependence(AonNumberUtils.toByte(disabilityAscendantDependence.getValue()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(7, 4, disabilityAscendantDependence);

		AonIntegerBox disabilityAscendantDependenceRatio = new AonIntegerBox();
		disabilityAscendantDependenceRatio.setMaxLength(1);
		disabilityAscendantDependenceRatio.setVisibleLength(1);
		disabilityAscendantDependenceRatio.setValue(detail.getDisabilityAscendantDependenceRatio());
		disabilityAscendantDependenceRatio.addValueChangeHandler(event -> {
			detail.setDisabilityAscendantDependenceRatio(
					AonNumberUtils.toByte(disabilityAscendantDependenceRatio.getValue()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(7, 5, disabilityAscendantDependenceRatio);

		tab6.setWidget(7, 6, new Model190SmallerLabel(AON.MSG.moreThan65()));
		tab6.getCellFormatter().setStyleName(7, 6, AON.CSS.aonTextRight());
		AonIntegerBox disabilityAscendant65 = new AonIntegerBox();
		disabilityAscendant65.setMaxLength(1);
		disabilityAscendant65.setVisibleLength(1);
		disabilityAscendant65.setValue(detail.getDisabilityAscendant65());
		disabilityAscendant65.addValueChangeHandler(event -> {
			detail.setDisabilityAscendant65(AonNumberUtils.toByte(disabilityAscendant65.getValue()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(7, 7, disabilityAscendant65);

		AonIntegerBox disabilityAscendant65Ratio = new AonIntegerBox();
		disabilityAscendant65Ratio.setMaxLength(1);
		disabilityAscendant65Ratio.setVisibleLength(1);
		disabilityAscendant65Ratio.setValue(detail.getDisabilityAscendant65Ratio());
		disabilityAscendant65Ratio.addValueChangeHandler(event -> {
			detail.setDisabilityAscendant65Ratio(AonNumberUtils.toByte(disabilityAscendant65Ratio.getValue()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(7, 8, disabilityAscendant65Ratio);
		tab6.setWidget(7, 9, new Label());
		
		Model190SmallerLabel forecastPlanContributionsLabel = new Model190SmallerLabel("");
		forecastPlanContributionsLabel.setTitle("Contribuciones empresariales a planes de pensiones, planes de previsi\u00F3n social "
	    		+ "empresarial y mutualidades de previsi\u00F3n social, as\u00ED como aportaciones a estos sistemas de previsi\u00F3n social que deriven "
	    		+ "de una decisi\u00F3n del trabajador, que reduzcan la base imponible del IRPF (excepto a seguros colectivos de dependencia)");
		forecastPlanContributionsLabel.setText(AonStringUtils.abbreviate(forecastPlanContributionsLabel.getTitle(), 34));
		forecastPlanContributionsLabel.addStyleName(AON.CSS.aonBold());
		tab6.setWidget(8, 0, forecastPlanContributionsLabel);
		
		AonDoubleBox forecastPlanContributions = new AonDoubleBox();
		forecastPlanContributions.setValue(detail.getForecastPlanContributions());
		forecastPlanContributions.addValueChangeHandler(event -> {
			detail.setForecastPlanContributions(forecastPlanContributions.getValue());
			callback.onValueChanged(detail);			
		});
		tab6.setWidget(8, 1, forecastPlanContributions);

		panel.add(additionalDataPanel);
		enableOrDisableAdditionalDataPanel(key, subkey, additionalDataPanel);
		
		// Panel Datos Adicionales solo para reducciones si clave/subclave es E, F 01 a 06, G 01 a 06 y 08, H, I
		FlexTable tab51 = new FlexTable();
		tab51.getColumnFormatter().setWidth(0, WIDTH_150PX);
		tab51.getColumnFormatter().setWidth(1, "auto");
		tab51.setStyleName(AON.CSS.aonWidthAll());
		tab51.addStyleName(AON.CSS.aonNowrap());
		
		tab51.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
		tab51.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
		tab51.getFlexCellFormatter().setColSpan(0, 0, 2);
		tab51.setWidget(0, 0, new InlineLabel(AON.MSG.additionalData()));

		tab51.setWidget(1, 0, new Model190SmallerLabel(AON.MSG.applicableReduction()));
		tab51.setWidget(1, 1, new Label());

		applicableReduction1.setValue(detail.getApplicableReduction());
		applicableReduction1.addValueChangeHandler(event -> {
			detail.setApplicableReduction(applicableReduction1.getValue());
			applicableReduction.setValue(applicableReduction1.getValue(), false);
			callback.onValueChanged(detail);
		});
		tab51.setWidget(2, 0, applicableReduction1);
		tab51.setWidget(2, 1, new Label());
		additionalDataPanel2.add(tab51);
		
		panel.add(additionalDataPanel2);
		enableOrDisableAdditionalDataPanel2(key, subkey, additionalDataPanel2);
	
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

	private static void setValue(ListBox key, ListBox subKey, Mod190Detail detail) {
		if (AonStringUtils.isBlank(detail.getKey())) {
			detail.setKey(Mod1902025Key.A.toString());
		}
		Mod1902025Key keyEnum = Mod1902025Key.valueOf(detail.getKey());
		key.setSelectedIndex(keyEnum.ordinal());
		subKey.clear();
		if (keyEnum.hasSubkeys()) {
			subKey.setEnabled(true);
			for (int i = 0; i < keyEnum.getSubKeys().length; i++) {
				subKey.addItem(keyEnum.getSubKeys()[i]);
				if (keyEnum.getSubKeys()[i].equals(detail.getSubKey())) {
					subKey.setSelectedIndex(i);
				}
			}
			subKey.setEnabled(true);
		} else {
			subKey.setEnabled(false);
		}
	}

	private static void enableOrDisableAdditionalDataPanel(ListBox key, ListBox subKey, Panel panel) {
		Mod1902025Key keyEnum = Mod1902025Key.values()[key.getSelectedIndex()];
		String subk = ((subKey.getSelectedIndex() == -1) ? null : subKey.getValue(subKey.getSelectedIndex()));
		panel.setVisible(Mod1902025Key.A == keyEnum 
				|| (Mod1902025Key.B == keyEnum && "01".equals(subk))
				|| (Mod1902025Key.B == keyEnum && "03".equals(subk)) 
				|| (Mod1902025Key.B == keyEnum && "04".equals(subk))  
				|| (Mod1902025Key.B == keyEnum && "99".equals(subk))
				|| (Mod1902025Key.C == keyEnum)
				|| (Mod1902025Key.E == keyEnum && "01".equals(subk))
				|| (Mod1902025Key.E == keyEnum && "02".equals(subk))
				|| (Mod1902025Key.L == keyEnum && "29".equals(subk)));
	}
	
	private static void enableOrDisableAdditionalDataPanel2(ListBox key, ListBox subKey, Panel panel) {
		Mod1902025Key keyEnum = Mod1902025Key.values()[key.getSelectedIndex()];
		String subk = ((subKey.getSelectedIndex() == -1) ? null : subKey.getValue(subKey.getSelectedIndex()));
		panel.setVisible((Mod1902025Key.E == keyEnum && ("03".equals(subk) || "04".equals(subk))) 
				|| (Mod1902025Key.F == keyEnum && ("01".equals(subk) || "02".equals(subk) || "03".equals(subk) || "04".equals(subk) || "05".equals(subk) || "06".equals(subk)) )
				|| (Mod1902025Key.G == keyEnum && ("01".equals(subk) || "02".equals(subk) || "03".equals(subk) || "04".equals(subk) || "05".equals(subk) || "06".equals(subk) || "08".equals(subk)))
				|| (Mod1902025Key.H == keyEnum)
				|| (Mod1902025Key.I == keyEnum));
	}

	private static void enableOrDisableIlPanel(ListBox key, ListBox subKey, Panel panel) {
		Mod1902025Key keyEnum = Mod1902025Key.values()[key.getSelectedIndex()];
		String subk = ((subKey.getSelectedIndex() == -1) ? null : subKey.getValue(subKey.getSelectedIndex()));
		panel.setVisible(Mod1902025Key.A == keyEnum || (Mod1902025Key.B == keyEnum && "01".equals(subk)));
	}

	private static void enableOrDisableAdministrationPanel(ListBox key, Panel panel) {
		Mod1902025Key keyEnum = Mod1902025Key.values()[key.getSelectedIndex()];
		panel.setVisible(Mod1902025Key.E == keyEnum);
	}
}
