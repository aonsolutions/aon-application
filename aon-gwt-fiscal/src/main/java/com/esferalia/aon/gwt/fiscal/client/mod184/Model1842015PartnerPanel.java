package com.esferalia.aon.gwt.fiscal.client.mod184;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod184.Model184Partner2015.IModel184PartnerCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Partner;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model1842015PartnerPanel extends SimpleLayoutPanel implements Focusable {
	
	private static final String WIDTH_150PX = "150px";

	private  enum Mod184PartnerKey {
		 A ("A - Rendimientos del capital mobiliario."
			 ,new String[] {"01","02"}
		 	 ,new String[] {
	 			 "01 - Rendimientos capital mobiliario ap. 1, 2, y 3 art. 25 LIRPF"
		 		,"02 - Rendimientos capital mobiliario ap. 4 art. 25 LIRPF"})
		,C ("C - Rendimientos del capital inmobiliario."		
			,null,null)
		,D ("D - Rendimientos de actividades econ\u00F3micas."	
			,null,null)
		,E ("E - Rentas contabilizadas derivadas de la participaci\u00F3n en Instituciones de Inversi\u00F3n Colectiva."
			,null,null)
		,F ("F - Ganancias y p\u00E9rdidas patrimoniales no derivadas de transmisiones de elementos patrimoniales."
			,new String[] {"01","02"}
			,new String[] {			
				 "01 - Ganancias."
				,"02 - P\u00E9rdidas."})
		,G ("G - Ganancias y p\u00E9rdidas patrimoniales derivadas de transmisiones de elementos patrimoniales."
			,new String[] {"01","02"}
			,new String[] {
				 "01 - Ganancias."
				,"02 - P\u00E9rdidas."})
		,I ("I - Deducciones de la Ley del Impuesto sobre la Renta de las Personas F\u00EDsicas."
			,new String[] {"01","02","03","04","05","06"}
			,new String[] {
				 "01 - Por protecci\u00F3n del Patrimonio Espa\u00F1ol y Mundial."
				,"02 - Por donativos, donaciones y aportaciones a determinadas entidades."
				,"03 - Por rentas obtenidas en Ceuta y Melilla."
				,"04 - Deducciones en actividades Econ\u00F3micas."
				,"05 - Por doble imposici\u00F3n internacional."
				,"06 - Por inversi\u00F3n en empresas de nueva creaci\u00F3n."
			})
		,J ("J - Deducciones de la Ley del Impuesto sobre Sociedades."
			,new String[] {"01","02","03","04"}
			,new String[] {
				 "01 - Por doble imposici\u00F3n interna."
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
		;
		
		private String description;
		private String[] subkeys;
		private String[] subkeyDescriptions;
		
		private Mod184PartnerKey(String description, String[] subkeys, String[] subkeyDescriptions) {
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
	private ListBox partType = new ListBox();
	private ProvinceListBox province = new ProvinceListBox();
	private CountryListBox country = new CountryListBox();
	private AonDocumentTextBox document = new AonDocumentTextBox();
	private AonTextBox name = new AonTextBox();
	private AonDocumentTextBox representativeDocument = new AonDocumentTextBox();
	
	private CheckBox memberEndOfYear = new CheckBox();
	private AonIntegerBox memberDays = new AonIntegerBox();
	private AonDoubleBox partPercent = new AonDoubleBox();
	private AonDoubleBox amount = new AonDoubleBox();
	private AonDoubleBox reduction = new AonDoubleBox();
	private AonTextBox address = new AonTextBox();

	public Model1842015PartnerPanel(Mod184Partner partner, IModel184PartnerCallback callback) {
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonWidthAll());
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonScrollArea());
		
		FlexTable tab1 = new FlexTable();
		tab1.getColumnFormatter().setWidth(0, WIDTH_150PX);
		tab1.getColumnFormatter().setWidth(1, WIDTH_150PX);
		tab1.getColumnFormatter().setWidth(2, "250px");
		tab1.getColumnFormatter().setWidth(3, WIDTH_150PX);
		tab1.getColumnFormatter().setWidth(4, "auto");

		tab1.setStyleName(AON.CSS.aonWidthAll());
		tab1.addStyleName(AON.CSS.aonNowrap());
		tab1.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
		tab1.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
		tab1.getFlexCellFormatter().setColSpan(0, 0, 5);
		tab1.setWidget(0, 0, new InlineLabel(AON.MSG.entityPartners()));

 		tab1.setWidget(1, 0, new Model184SmallerLabel(AON.MSG.key()));
		tab1.setWidget(1, 1, new Model184SmallerLabel(AON.MSG.subkey()));
		tab1.setWidget(1, 2, new Model184SmallerLabel(AON.MSG.partType()));
		tab1.setWidget(1, 3, new Model184SmallerLabel(AON.MSG.province()));
		tab1.setWidget(1, 4, new Model184SmallerLabel(AON.MSG.country()));

		subkey.setWidth("100px");

		key.setWidth("100px");
		for (Mod184PartnerKey k : Mod184PartnerKey.values()) {
			key.addItem(k.getDescription(),k.getValue());
		}
		
		Model1842015PartnerPanel.setValue(key, subkey, partner);
		
		key.addChangeHandler(event -> {
			subkey.clear();
			Mod184PartnerKey keyEnum = Mod184PartnerKey.values()[key.getSelectedIndex()];
			partner.setKey( keyEnum.toString() );
			if (keyEnum.hasSubkeys()) {
				subkey.setEnabled(true);
				for (int i = 0; i < keyEnum.getSubkeys().length; i++) {
					subkey.addItem(keyEnum.getSubkeyDescriptions()[i],keyEnum.getSubkeys()[i]);
				}
				partner.setSubKey(keyEnum.getSubkeys()[0]);
			} else {
				subkey.setEnabled(false);
				partner.setSubKey(null);
			}
			enableWidgets( partner );
			callback.onValueChanged(partner);
		});
		tab1.setWidget(2, 0, key);
		
		subkey.addChangeHandler(event -> {
			Mod184PartnerKey keyEnum = Mod184PartnerKey.values()[key.getSelectedIndex()];
			if (keyEnum.hasSubkeys()) {
				int idx = subkey.getSelectedIndex() == -1 ? 0 : subkey.getSelectedIndex();
				partner.setSubKey(keyEnum.getSubkeys()[idx]);
			} else {
				subkey.setEnabled(false);
				partner.setSubKey(null);
			}
			enableWidgets( partner );
			callback.onValueChanged(partner);
		});
		tab1.setWidget(2, 1, subkey);
		
		partType.addItem("-","");
		partType.addItem("1 - Residente","1");
		partType.addItem("2 - No residente SIN estab. perm.","2");
		partType.addItem("3 - No residente CON estab. perm.","3");
		partType.setWidth("200px");
		partType.setSelectedIndex( partner.getPartType() );
		partType.addChangeHandler(event -> {
			partner.setPartType((byte) partType.getSelectedIndex());
			callback.onValueChanged(partner);
		});
		tab1.setWidget(2, 2, partType);
 
		
		province.setValue( Province.safeValueOf( partner.getProvince() ));
		province.addChangeHandler(event -> {
			partner.setProvince( province.getSelectedIndex() );
			callback.onValueChanged(partner);
		});
		tab1.setWidget(2, 3, province);

		country.setValue( Country.safeValueOf( partner.getCountry() ));
		country.addChangeHandler(event -> {
			partner.setCountry(country.getSelectedValue());
			callback.onValueChanged(partner);
		});
		tab1.setWidget(2, 4, country);
		
		
		
		FlexTable tab2 = new FlexTable();
		tab2.getColumnFormatter().setWidth(0, WIDTH_150PX);
		tab2.getColumnFormatter().setWidth(1, WIDTH_150PX);
		tab2.getColumnFormatter().setWidth(2, "auto");

 		tab2.setWidget(1, 0, new Model184SmallerLabel(AON.MSG.document()));
		tab2.setWidget(1, 1, new Model184SmallerLabel(AON.MSG.representativeDocument()));
		tab2.setWidget(1, 2, new Model184SmallerLabel(AON.MSG.fullName()));

		document.setValue(partner.getDocument());
		document.addValueChangeHandler(event -> {
			partner.setDocument(document.getValue());
			callback.onValueChanged(partner);
		});
		tab2.setWidget(2, 0, document);
		
		representativeDocument.setValue(partner.getRepresentativeDocument());
		representativeDocument.addValueChangeHandler(event -> {
			partner.setRepresentativeDocument(representativeDocument.getValue());
			callback.onValueChanged(partner);
		});
		tab2.setWidget(2, 1, representativeDocument);
		
		name.setVisibleLength(40);
		name.setMaxLength(40);
		name.setValue(partner.getName());
		name.addValueChangeHandler(event -> {
			partner.setName(name.getValue());
			callback.onTableChanged(partner);
		});
		tab2.setWidget(2, 2, name);

		memberEndOfYear.setText(AON.MSG.memberEndOfYear());
		memberEndOfYear.setStyleName(AON.CSS.aonFontMedium());
		memberEndOfYear.addClickHandler(event -> {
			partner.setMemberEndOfYear(memberEndOfYear.getValue());
			callback.onValueChanged(partner);
		});
		tab1.getFlexCellFormatter().setColSpan(3, 0, 2);
		tab1.setWidget(3, 0, memberEndOfYear);
		
		FlowPanel cellContainer = new FlowPanel();
		cellContainer.add(new Model184SmallerLabel(AON.MSG.memberDays()));
		memberDays.addStyleName(AON.CSS.aonMarginLeft());
		memberDays.setValue(partner.getMemberDays());
		memberDays.addValueChangeHandler(event -> {
			partner.setMemberDays(memberDays.getValue());
			callback.onValueChanged(partner);
		});
		cellContainer.add(memberDays);
		tab1.setWidget(3, 1, cellContainer);
		
 		tab2.setWidget(4, 0, new Model184SmallerLabel(AON.MSG.partPercent()));
		tab2.setWidget(4, 1, new Model184SmallerLabel(AON.MSG.amount()));
		tab2.setWidget(4, 2, new Model184SmallerLabel(AON.MSG.reduction()));
		
		partPercent.setValue(partner.getPartPercent());
		partPercent.addValueChangeHandler(event -> {
			partner.setPartPercent(partPercent.getValue());
			callback.onValueChanged(partner);
		});
		tab2.setWidget(5, 0, partPercent);
		
		amount.setValue(partner.getAmount());
		amount.addValueChangeHandler(event -> {
			partner.setAmount(amount.getValue());
			callback.onValueChanged(partner);
		});
		tab2.setWidget(5, 1, amount);

		reduction.setValue(partner.getReduction());
		reduction.addValueChangeHandler(event -> {
			partner.setReduction(reduction.getValue());
			callback.onValueChanged(partner);
		});
		tab2.setWidget(5, 2, reduction);


 		tab2.setWidget(6, 0, new Model184SmallerLabel(AON.MSG.address()));
 		address.setMaxLength(40);
 		address.setVisibleLength(40);
 		address.setValue(partner.getAddress());
 		address.addValueChangeHandler(event -> {
			partner.setAddress(address.getValue());
			callback.onValueChanged(partner);
		});
 		tab2.setWidget(7, 0, address);
 		tab2.getFlexCellFormatter().setColSpan(7, 0, 2);

		panel.add(tab1);
		panel.add(tab2);
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
	
	private static void setValue(ListBox key, ListBox subKey, Mod184Partner detail) {
		if (AonStringUtils.isBlank( detail.getKey())) {
			detail.setKey(Mod184PartnerKey.A.toString());
		}
		Mod184PartnerKey keyEnum = Mod184PartnerKey.valueOf(detail.getKey());
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

	private void enableWidgets(Mod184Partner partner ) {
		province.setEnabled( partner.getPartType() == 1);
		country.setEnabled( partner.getPartType() != 1);
		reduction.setEnabled(
				   "A".equals(partner.getKey())
				|| "C".equals(partner.getKey())
				|| "D".equals(partner.getKey())
				);
	}

}
