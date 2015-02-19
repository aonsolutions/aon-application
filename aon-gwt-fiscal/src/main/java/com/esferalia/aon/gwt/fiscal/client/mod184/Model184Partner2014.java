package com.esferalia.aon.gwt.fiscal.client.mod184;

import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Partner;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Model184Partner2014 extends ResizeComposite {
	

	interface Model184Partner2014Binder extends UiBinder<Widget, Model184Partner2014> {}
	private static Model184Partner2014Binder MODEL184_PARTNER_2014_BINDER 
		= GWT.create(Model184Partner2014Binder.class);

	static interface IPartnerCallBack {
		void redrawList( Mod184Partner partner);
	}
	
	public static class Mod184PartnerSubKey {
		protected String subkey;
		protected String description;
		
		protected Mod184PartnerSubKey( String subkey, String description) {
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
	
	public static enum Mod184PartnerKey {
			A ("Rendimientos del capital mobiliario.", new Mod184PartnerSubKey[]{
					new Mod184PartnerSubKey("01","Rendimientos del capital mobiliario previstos en los apartados 1, 2 y 3 del artículo 25 de la LIRPF.")
					,new Mod184PartnerSubKey("02","Rendimientos del capital mobiliario previstos en el apartado 4 del artículo 25 de la LIRPF.")})
			,C ("Rendimientos del capital inmobiliario.")
			,D ("Rendimientos de actividades económicas.")
			,E ("Rentas contabilizadas de participaciones de Instituciones de Inversión Colectiva.")
			,F ("Ganancias y pérdidas no derivadas de la transmisión de elementos patrimoniales.", new Mod184PartnerSubKey[]{
					new Mod184PartnerSubKey("01","Ganancias.")
					,new Mod184PartnerSubKey("02","Pérdidas.")})
			,G ("Ganancias y pérdidas patrimoniales derivadas de la transmisión de elementos patrimoniales con periodo de generación superior al año.", new Mod184PartnerSubKey[]{
					new Mod184PartnerSubKey("01","Ganancias.")
					,new Mod184PartnerSubKey("02","Pérdidas.")})
			,H ("Ganancias y pérdidas patrimoniales derivadas de transmisiones de elementos patrimoniales con periodo de generación igual o inferior al año..", new Mod184PartnerSubKey[]{
					new Mod184PartnerSubKey("01","Ganancias.")
					,new Mod184PartnerSubKey("02","Pérdidas.")})
			,I ("Deducciones Ley del IRPF", new Mod184PartnerSubKey[]{
					new Mod184PartnerSubKey("01","Por protección Patrimonio Español y Mundial.")
					,new Mod184PartnerSubKey("02","Por donativos, donaciones y aportaciones a determinadas entidades (Ley IRPF)")
					,new Mod184PartnerSubKey("03","Por rentas obtenidas en Ceuta y Melilla.")
					,new Mod184PartnerSubKey("04","Deducciones en actividades económicas.")
					,new Mod184PartnerSubKey("05","Deducción por doble imposición internacional.")})
			,J ("Deducciones Ley del Impuesto sobre Sociedades.", new Mod184PartnerSubKey[]{
					new Mod184PartnerSubKey("01","Deducción por doble imposición interna")
					,new Mod184PartnerSubKey("02","Deducciones por doble imposición internacional.")
					,new Mod184PartnerSubKey("03","Deducciones con límite sobre cuota.")
					,new Mod184PartnerSubKey("04","Deducción artículo 42.")
					,new Mod184PartnerSubKey("05","Deducción por donativos a entidades sin fines lucrativos")
					,new Mod184PartnerSubKey("06","Otras deducciones. (Ley Impuesto Sociedades).")})
			,K ("Retenciones e ingresos a cuenta.", new Mod184PartnerSubKey[]{		
					new Mod184PartnerSubKey("01","Por rendimientos del capital mobiliario")
					,new Mod184PartnerSubKey("02","Por arrendamiento de inmuebles urbanos")
					,new Mod184PartnerSubKey("03","Por rendimientos de actividades económicas")
					,new Mod184PartnerSubKey("04","Por ganancias patrimoniales")
					,new Mod184PartnerSubKey("05","Por otros conceptos")})
			;
			private String description;
			private Mod184PartnerSubKey[] subkeys;
			
			private Mod184PartnerKey(String description, Mod184PartnerSubKey[] subkeys) {
				this.description = description;
				this.subkeys = subkeys;
			}
			private Mod184PartnerKey(String description) {
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
			public Mod184PartnerSubKey[] getSubkeys() {
				return subkeys;
			}
		}

	 public class PartnerKeyListBox extends ListBox {

		public PartnerKeyListBox() {
			subKey = new ListBox();
			subKey.setWidth("45px");

			setWidth("40px");
			this.addItem("-","");
			for (Mod184PartnerKey key : Mod184PartnerKey.values()) {
				this.addItem(key.getValue(),key.getValue());
			}

			addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					int idx = getSelectedIndex();
					if (idx == 0) {
						getPartner().setKey(null);
						getPartner().setSubKey(null);
						getSubKey().clear();	
					} else {
						Mod184PartnerKey keyEnum = Mod184PartnerKey.values()[idx-1];
						getPartner().setKey(keyEnum.getValue());
						getPartner().setSubKey(null);
						getSubKey().clear();
						if (keyEnum.hasSubkeys()) {
							getSubKey().setEnabled(true);
							getSubKey().addItem("-","");
							for (int i = 0; i < keyEnum.getSubkeys().length; i++) {
								Mod184PartnerSubKey sk = keyEnum.getSubkeys()[i];
								getSubKey().addItem(sk.getSubkey(),sk.getSubkey());
							}
						} else {
							getSubKey().setEnabled(false);
						}
					}
					enableWidgets();
					getPartner().setDirty(true);
				}
			});
		}

		public ListBox getSubKey() {
			return subKey;
		}

		public void setValue(String key, String subkey) {
			if (key != null) {
				Mod184PartnerKey keyEnum = Mod184PartnerKey.valueOf(key);
				setSelectedIndex(keyEnum.ordinal()+1);
				getSubKey().clear();
				if (keyEnum.hasSubkeys()) {
					getSubKey().setEnabled(true);
					getSubKey().addItem("-","");
					for (int i = 0; i < keyEnum.getSubkeys().length; i++) {
						Mod184PartnerSubKey sk = keyEnum.getSubkeys()[i];
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

	Mod184Partner partner;
	private IPartnerCallBack callback;
	
	@UiField
	Button deleteDetailButton;
	@UiField
	Button restoreDeletedButton;

	@UiField(provided = true)
	PartnerKeyListBox key;
	@UiField(provided = true)
	ListBox subKey;
	@UiField
	DocumentTextBox document;
	@UiField
	DocumentTextBox representativeDocument;
	@UiField
	TextBox name;
	@UiField
	ProvinceListBox province;
	@UiField
	CountryListBox country;
	@UiField
	ListBox partType;
	@UiField
	CheckBox memberEndOfYear;
	@UiField
	TextBox memberDays;
	@UiField
	DoubleTextBox partPercent;
	@UiField
	DoubleTextBox amount;
	@UiField
	DoubleTextBox reduction;
	@UiField
	TextBox address;
	
	public Model184Partner2014() {
		key = new PartnerKeyListBox();
		
		Widget ui = MODEL184_PARTNER_2014_BINDER.createAndBindUi(this);
		initWidget(ui);
		
		partType.addItem("-","");
		partType.addItem("1 - Residente","1");
		partType.addItem("2 - No residente SIN estab. perm.","2");
		partType.addItem("3 - No residente CON estab. perm.","3");
		
	}
	private Mod184Partner getPartner() {
		return partner;
	}
	
	public void setCallback(IPartnerCallBack callback) {
		this.callback = callback;
	}
	
	public void setPartner(Mod184Partner partner) {
		this.partner = partner;
		
		key.setValue(partner.getKey(), partner.getSubKey());
		document.setValue(partner.getDocument());
		representativeDocument.setValue(partner.getRepresentativeDocument());
		name.setValue(partner.getName());
		province.setSelectedIndex(partner.getProvince());
		country.setSelectedIndex( 
				AonStringUtils.isBlank( partner.getCountry() )
				? 0 
				: Country.valueOf(partner.getCountry()).ordinal() + 1 );
		partType.setSelectedIndex(partner.getPartType());
		memberEndOfYear.setValue(partner.isMemberEndOfYear());
		memberDays.setValue(partner.getMemberDays()==null?"0":Integer.toString(partner.getMemberDays()));
		partPercent.setValue(partner.getPartPercent());
		amount.setValue(partner.getAmount());
		reduction.setValue(partner.getReduction());
		address.setValue(partner.getAddress());
		
		enableWidgets();
		restoreDeletedButton.setVisible(partner.isDeleted());
		deleteDetailButton.setVisible(!partner.isDeleted());
	}

	@UiHandler("deleteDetailButton")
	void onDeleteDetailButtonClick(ClickEvent event) {
		getPartner().setDeleted(true);
		restoreDeletedButton.setVisible(true);
		deleteDetailButton.setVisible(false);
		callback.redrawList(getPartner());
	}

	@UiHandler("restoreDeletedButton")
	void onRestoreDeletedButtonClick(ClickEvent event) {
		getPartner().setDeleted(false);
		if (!getPartner().isDirty()) {
			restoreDeletedButton.setVisible(false);
			deleteDetailButton.setVisible(true);
			callback.redrawList(getPartner());
		}
	};

	@UiHandler("subKey")
	void onChangeSubKey(ChangeEvent event) {
		String subk = ((key.getSubKey().getSelectedIndex() == -1) ? null : key
				.getSubKey().getValue(key.getSubKey().getSelectedIndex()));
		getPartner().setSubKey(subk);
		enableWidgets();
		getPartner().setDirty(true);
	}
	
	@UiHandler("document")
	void onChangeDocument(ChangeEvent event) {
		getPartner().setDocument(document.getValue());
		getPartner().setDirty(true);
	}
	@UiHandler("representativeDocument")
	void onChangeRepresentativeDocument(ChangeEvent event) {
		getPartner().setRepresentativeDocument(representativeDocument.getValue());
		getPartner().setDirty(true);
	}
	@UiHandler("name")
	void onChangeName(ChangeEvent event) {
		getPartner().setName(name.getValue());
		getPartner().setDirty(true);
	}
	@UiHandler("province")
	void onChangeProvince(ChangeEvent event) {
		getPartner().setProvince(province.getSelectedIndex());
		getPartner().setDirty(true);
	}
	@UiHandler("country")
	void onChangeCountry(ChangeEvent event) {
		getPartner().setCountry(country.getValue(country.getSelectedIndex()));
		getPartner().setDirty(true);
	}
	@UiHandler("partType")
	void onChangePartType(ChangeEvent event) {
		getPartner().setPartType((byte) partType.getSelectedIndex());
		getPartner().setDirty(true);
		enableWidgets();
	}
	
	@UiHandler("memberEndOfYear")
	void onChangeMemberEndOfYear(ClickEvent event) {
		getPartner().setMemberEndOfYear(memberEndOfYear.getValue());
		getPartner().setDirty(true);
	}
	@UiHandler("memberDays")
	void onChangeMemberDays(ChangeEvent event) {
		try {
			getPartner().setMemberDays( Integer.parseInt(memberDays.getValue()) );
		} catch (NumberFormatException e ) {
			getPartner().setMemberDays(0);
		}
		getPartner().setDirty(true);
	}
	
	@UiHandler("partPercent")
	void onChangePartPercent(ChangeEvent event) {
		getPartner().setPartPercent( partPercent.getDoubleValue());
		getPartner().setDirty(true);
	}
	@UiHandler("amount")
	void onChangeAmount(ChangeEvent event) {
		getPartner().setAmount( amount.getDoubleValue());
		getPartner().setDirty(true);
	}
	@UiHandler("reduction")
	void onChangeReduction(ChangeEvent event) {
		getPartner().setAmount( reduction.getDoubleValue());
		getPartner().setDirty(true);
	}
	@UiHandler("address")
	void onChangeAddress(ChangeEvent event) {
		getPartner().setAddress( address.getValue());
		getPartner().setDirty(true);
	}
	
	private void enableWidgets() {
		province.setEnabled( partner.getPartType() == 1);
		country.setEnabled( partner.getPartType() != 1);
		reduction.setEnabled(
				   "A".equals(partner.getKey())
				|| "C".equals(partner.getKey())
				|| "D".equals(partner.getKey())
				);
	}
	
	
}
