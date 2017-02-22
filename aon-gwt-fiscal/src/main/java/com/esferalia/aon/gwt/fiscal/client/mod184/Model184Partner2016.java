package com.esferalia.aon.gwt.fiscal.client.mod184;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CountryListBox;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod184Partner;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
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

public class Model184Partner2016 extends ResizeComposite {
	
	public static final ProvidesKey<Mod184Partner> MOD184_PARTNER_PROVIDES_KEY = new ProvidesKey<Mod184Partner>() {
		@Override
		public Object getKey(Mod184Partner det) {
			return det == null ?null: det.getId() == null? det.getTempId(): det.getId();
		}
	};


	interface Model184Partner2014Binder extends UiBinder<Widget, Model184Partner2016> {}
	private static Model184Partner2014Binder MODEL184_PARTNER_2014_BINDER 
		= GWT.create(Model184Partner2014Binder.class);

	static interface IPartnerCallBack {
		void redrawList( Mod184Partner partner);
	}
	
	public static enum Mod1842015PartnerKey {
			 A (new String[]{"01","02"})
			,C 
			,D 
			,E 
			,F (new String[]{"01","02"})
			,G (new String[]{"01","02"})
			,I (new String[]{"01","02","03","04","05","06"})
			,J (new String[]{"01","02","03","04"})
			,K (new String[]{"01","02","03","04","05"})
			;
			
			private String[] subkeys;
			
			private Mod1842015PartnerKey(String[] subkeys) {
				this.subkeys = subkeys;
			}
			private Mod1842015PartnerKey() {
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

	 public class PartnerKeyListBox extends ListBox {

		public PartnerKeyListBox() {
			subKey = new ListBox();
			subKey.setWidth("45px");

			setWidth("40px");
			this.addItem("-","");
			for (Mod1842015PartnerKey key : Mod1842015PartnerKey.values()) {
				this.addItem(key.getValue(),key.getValue());
			}

			addChangeHandler(new ChangeHandler() {

				@Override
				public void onChange(ChangeEvent event) {
					int idx = getSelectedIndex();
					if (idx == 0) {
						getDetail().setKey(null);
						getDetail().setSubKey(null);
						getSubKey().clear();	
					} else {
						Mod1842015PartnerKey keyEnum = Mod1842015PartnerKey.values()[idx-1];
						getDetail().setKey(keyEnum.getValue());
						getDetail().setSubKey(null);
						getSubKey().clear();
						if (keyEnum.hasSubkeys()) {
							getSubKey().setEnabled(true);
							getSubKey().addItem("-","");
							for (int i = 0; i < keyEnum.getSubkeys().length; i++) {
								getSubKey().addItem(keyEnum.getSubkeys()[i]);
							}
						} else {
							getSubKey().setEnabled(false);
						}
					}
					enableWidgets();
					markAsDirty();
				}
			});
		}

		public ListBox getSubKey() {
			return subKey;
		}

		public void setValue(String key, String subkey) {
			if (key != null) {
				Mod1842015PartnerKey keyEnum = Mod1842015PartnerKey.valueOf(key);
				setSelectedIndex(keyEnum.ordinal()+1);
				getSubKey().clear();
				if (keyEnum.hasSubkeys()) {
					getSubKey().setEnabled(true);
					getSubKey().addItem("-","");
					for (int i = 0; i < keyEnum.getSubkeys().length; i++) {
						getSubKey().addItem(keyEnum.getSubkeys()[i]);
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
	private Mod184PartnerDataProvider partnersDataProvider;
	private SingleSelectionModel<Mod184Partner> partnersModel;
	
	@UiField
	Button deleteDetailButton;
	@UiField
	Button restoreDeletedButton;
	@UiField
	Button newDetailButton;


	@UiField(provided=true)
	CellList<Mod184Partner> partnersList;
	@UiField
	ScrollPanel detailListPanel;

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
	IntegerBox memberDays;
	@UiField
	DoubleBox partPercent;
	@UiField
	DoubleBox amount;
	@UiField
	DoubleBox reduction;
	@UiField
	TextBox address;
	@UiField
	DoubleBox expenses;
	@UiField
	ListBox nature;
	@UiField
	ListBox location;
	@UiField
	TextBox cadasdralReference;
	@UiField
	ListBox declaredKey;
	@UiField
	DoubleBox assetPercent;
	
	@UiField
	Button newPartner;
	
	public Model184Partner2016() {
		key = new PartnerKeyListBox();
		
		Mod184PartnerCell mod184PartnerCell = new Mod184PartnerCell();
		partnersList = new CellList<Mod184Partner>(mod184PartnerCell, MOD184_PARTNER_PROVIDES_KEY);
		partnersList.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		partnersList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		// Add a selection model so we can select cells.
		partnersModel = new SingleSelectionModel<Mod184Partner>(MOD184_PARTNER_PROVIDES_KEY);
		partnersModel.addSelectionChangeHandler(new Handler() {
			
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				selectDetail();
			}
			
		});
		partnersList.setSelectionModel(partnersModel);
		partnersList.setEmptyListWidget(new HTML(AON.MSG.noData()));

		partnersDataProvider = new Mod184PartnerDataProvider(MOD184_PARTNER_PROVIDES_KEY);
		partnersDataProvider.addDataDisplay(partnersList);
		partnersList.setVisible(true);
		Widget ui = MODEL184_PARTNER_2014_BINDER.createAndBindUi(this);
		initWidget(ui);
		
		partType.addItem("-","");
		partType.addItem("1 - Residente","1");
		partType.addItem("2 - No residente SIN estab. perm.","2");
		partType.addItem("3 - No residente CON estab. perm.","3");
		partType.setWidth("200px");
		
		nature.addItem("-","");
		nature.addItem("1 - Inmueble urbano","1");
		nature.addItem("2 - Inmueble r\u00FAstico","2");
	
		location.addItem("-","");
		location.addItem("1. Inmueble con referencia catastral situado en cualquier punto del territorio espa\u00F1ol, excepto Pa\u00EDs Vasco y Navarra.","1");
		location.addItem("2. Inmueble situado en la Comunidad Aut\u00F3noma del Pa\u00EDs Vasco.","2");
		location.addItem("3. Inmueble situado en la Comunidad Foral de Navarra.","3");
		location.addItem("4. Inmueble en cualquiera de las situaciones anteriores pero sin referencia catastral.","4");
		location.addItem("5. Inmueble situado en el extranjero.","5");
		location.setWidth("200px");
		
		declaredKey.addItem("-","");
		declaredKey.addItem("Nudo propietario.","N");
		declaredKey.addItem("Titular.","T");
		declaredKey.addItem("Usufructuario.","U");
		declaredKey.addItem("Otro derecho real.","O");
	}

	public void setMod184(Mod184 mod184) {
		this.currentMod184 = mod184;
		partnersList.setVisibleRangeAndClearData(partnersList.getVisibleRange(),true);
	}
	
	private Mod184Partner getDetail() {
		return partnersModel.getSelectedObject();
	}
	
	public void selectDetail() {
		key.setValue(getDetail().getKey(), getDetail().getSubKey());
		document.setValue(getDetail().getDocument());
		representativeDocument.setValue(getDetail().getRepresentativeDocument());
		name.setValue(getDetail().getName());
		province.setSelectedIndex(getDetail().getProvince());
		country.setSelectedIndex( 
				AonStringUtils.isBlank( getDetail().getCountry() )
				? 0 
				: Country.valueOf(getDetail().getCountry()).ordinal() + 1 );
		partType.setSelectedIndex(getDetail().getPartType());
		int natureIndex = getDetail().getNatureIndex();
		if (natureIndex <0 || natureIndex > 2) {
			natureIndex = 0;
		}
		nature.setSelectedIndex(natureIndex);
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
		if ("N".equals(getDetail().getDeclaredKey())) {
			declaredKey.setSelectedIndex(1);
		} else if ("T".equals(getDetail().getDeclaredKey())) {
			declaredKey.setSelectedIndex(2);
		} else if ("U".equals(getDetail().getDeclaredKey())) {
			declaredKey.setSelectedIndex(3);
		} else if ("O".equals(getDetail().getDeclaredKey())) {
			declaredKey.setSelectedIndex(4);
		} else {
			declaredKey.setSelectedIndex(0);
		}
		memberEndOfYear.setValue(getDetail().isMemberEndOfYear());
		memberDays.setValue(getDetail().getMemberDays()==null?0:getDetail().getMemberDays());
		partPercent.setValue(getDetail().getPartPercent());
		amount.setValue(getDetail().getAmount());
		reduction.setValue(getDetail().getReduction());
		address.setValue(getDetail().getAddress());
		expenses.setValue(getDetail().getExpenses());
		assetPercent.setValue(getDetail().getAssetPercent());
		
		restoreDeletedButton.setVisible(getDetail().isDeleted());
		deleteDetailButton.setVisible(!getDetail().isDeleted());
		enableWidgets();
	}

	@UiHandler("deleteDetailButton")
	void onDeleteDetailButtonClick(ClickEvent event) {
		getDetail().setDeleted(true);
		restoreDeletedButton.setVisible(true);
		deleteDetailButton.setVisible(false);
		partnersList.redraw();
	}

	@UiHandler("restoreDeletedButton")
	void onRestoreDeletedButtonClick(ClickEvent event) {
		getDetail().setDeleted(false);
		if (!getDetail().isDirty()) {
			restoreDeletedButton.setVisible(false);
			deleteDetailButton.setVisible(true);
			partnersList.redraw();
		}
	};

	@UiHandler("newDetailButton")
	void onNewDetailButtonClick(ClickEvent event) {
		newPerceptor();
	}
	
	private void newPerceptor(){
		currentMod184.getPartners().add(
			new Mod184Partner()
				.setDirty(true)
				.setKey("A")
				.setSubKey("01")				
				.setTempId((currentMod184.getPartners().size() * (-1)))
		);
		partnersList.setRowCount(partnersList.getRowCount() + 1);
		partnersList.setPageSize(partnersList.getRowCount());
		partnersList.redraw();
		selectInList(currentMod184.getPartners().size() - 1);
		selectDetail();		
	}
	
	private void selectInList(int i) {
		partnersModel.setSelected(currentMod184.getPartners().get(i),true);
		partnersList.getRowElement(i).scrollIntoView();
		detailListPanel.scrollToLeft();
	}

	@UiHandler("subKey")
	void onChangeSubKey(ChangeEvent event) {
		String subk = ((key.getSubKey().getSelectedIndex() == -1) ? null : key
				.getSubKey().getValue(key.getSubKey().getSelectedIndex()));
		getDetail().setSubKey(subk);
		enableWidgets();
		markAsDirty();
	}
	
	@UiHandler("document")
	void onChangeDocument(ChangeEvent event) {
		getDetail().setDocument(document.getValue());
		markAsDirty();
	}
	@UiHandler("representativeDocument")
	void onChangeRepresentativeDocument(ChangeEvent event) {
		getDetail().setRepresentativeDocument(representativeDocument.getValue());
		markAsDirty();
	}
	@UiHandler("name")
	void onChangeName(ChangeEvent event) {
		getDetail().setName(name.getValue());
		markAsDirty();
		partnersList.redraw();
	}
	@UiHandler("province")
	void onChangeProvince(ChangeEvent event) {
		getDetail().setProvince(province.getSelectedIndex());
		markAsDirty();
	}
	@UiHandler("country")
	void onChangeCountry(ChangeEvent event) {
		getDetail().setCountry(country.getValue(country.getSelectedIndex()));
		markAsDirty();
	}
	@UiHandler("partType")
	void onChangePartType(ChangeEvent event) {
		getDetail().setPartType((byte) partType.getSelectedIndex());
		markAsDirty();
		enableWidgets();
	}
	@UiHandler("nature")
	void onChangeNature(ChangeEvent event) {
		getDetail().setNature(nature.getSelectedValue());
		markAsDirty();
		enableWidgets();
	}
	
	@UiHandler("memberEndOfYear")
	void onChangeMemberEndOfYear(ClickEvent event) {
		getDetail().setMemberEndOfYear(memberEndOfYear.getValue());
		markAsDirty();
	}
	@UiHandler("memberDays")
	void onChangeMemberDays(ChangeEvent event) {
		try {
			getDetail().setMemberDays( memberDays.getValue() );
		} catch (NumberFormatException e ) {
			getDetail().setMemberDays(0);
		}
		markAsDirty();
	}
	
	@UiHandler("partPercent")
	void onChangePartPercent(ChangeEvent event) {
		getDetail().setPartPercent( partPercent.getValue());
		markAsDirty();
	}
	@UiHandler("assetPercent")
	void onChangeAssetPercent(ChangeEvent event) {
		getDetail().setAssetPercent( assetPercent.getValue());
		markAsDirty();
	}
	@UiHandler("amount")
	void onChangeAmount(ChangeEvent event) {
		getDetail().setAmount( amount.getValue());
		markAsDirty();
	}
	@UiHandler("reduction")
	void onChangeReduction(ChangeEvent event) {
		getDetail().setReduction( reduction.getValue());
		markAsDirty();
	}
	@UiHandler("address")
	void onChangeAddress(ChangeEvent event) {
		getDetail().setAddress( address.getValue());
		markAsDirty();
	}
	@UiHandler("expenses")
	void onChangeExpenses(ChangeEvent event) {
		getDetail().setExpenses( expenses.getValue());
		markAsDirty();
	}
	@UiHandler("location")
	void onChangeLocation(ChangeEvent event) {
		getDetail().setLocation(Integer.toString( location.getSelectedIndex()));
		enableWidgets();
		markAsDirty();
	}
	@UiHandler("cadasdralReference")
	void onChangeCadasdralReference(ChangeEvent event) {
		getDetail().setCadasdralReference(cadasdralReference.getValue());
		markAsDirty();
	}
	@UiHandler("declaredKey")
	void onChangedeclaredKey(ChangeEvent event) {
		getDetail().setDeclaredKey(declaredKey.getSelectedValue());
		enableWidgets();
		markAsDirty();
	}
	
	@UiHandler("newPartner")
	void onClickNewPartner(ClickEvent event) {
		if (Window.confirm("Crear un registro nuevo con los datos de este socio?")) {
			currentMod184.getPartners().add(
					new Mod184Partner()
						.setDirty(true)
						.setKey("A")
						.setSubKey("01")				
						.setTempId((currentMod184.getPartners().size() * (-1)))
						
						.setDocument(getDetail().getDocument())
						.setRepresentativeDocument(getDetail().getRepresentativeDocument())
						.setName(getDetail().getName())
						.setProvince(getDetail().getProvince())
						.setCountry(getDetail().getCountry())
						.setPartType(getDetail().getPartType())
				);
				partnersList.setRowCount(partnersList.getRowCount() + 1);
				partnersList.setPageSize(partnersList.getRowCount());
				partnersList.redraw();
				selectInList(currentMod184.getPartners().size() - 1);
				selectDetail();		
		}
	}
	
	private void enableWidgets() {
		province.setEnabled( getDetail().getPartType() == 1);
		country.setEnabled( getDetail().getPartType() != 1);
		reduction.setEnabled(
				   "A".equals(getDetail().getKey())
				|| "C".equals(getDetail().getKey())
				|| "D".equals(getDetail().getKey())
				);
		nature.setEnabled("C".equals(getDetail().getKey()));
		assetPercent.setEnabled("C".equals(getDetail().getKey()));
		location.setEnabled("C".equals(getDetail().getKey()));
		cadasdralReference.setEnabled("C".equals(getDetail().getKey()) 
				&& (location.getSelectedIndex() == 1
				 || location.getSelectedIndex() == 2
				 || location.getSelectedIndex() == 3)
				);
		declaredKey.setEnabled(location.getSelectedIndex() != 0);
	}
	
	private void markAsDirty() {
		if (!getDetail().isDirty()) {
			getDetail().setDirty(true);
			partnersList.redraw();		
		}
	}
	
	class Mod184PartnerDataProvider extends AsyncDataProvider<Mod184Partner> {

		public Mod184PartnerDataProvider(ProvidesKey<Mod184Partner> detailProvidesKey) {
			super(detailProvidesKey);
		}

		@Override
		protected void onRangeChanged(HasData<Mod184Partner> display) {
			if (currentMod184 != null && currentMod184.getId() != null) {
				if (currentMod184.getPartners().size() == 0) {
					newPerceptor();					
				} else {
					updateRowCount(currentMod184.getPartners().size(), true);
					updateRowData(0, currentMod184.getPartners());
					partnersList.setPageSize(currentMod184.getPartners().size());
					selectInList(0);
					selectDetail();
				}
			}
		}
	}
	
	static class Mod184PartnerCell extends AbstractCell<Mod184Partner> {
		@Override
		public void render(Cell.Context context, Mod184Partner value,
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
			sb.appendEscaped(AonStringUtils.isEmpty(value.getName()) ?  AON.MSG.newPerceptor() : value.getName());
			if (value.isDirty()) {
				sb.appendEscaped(" *");
			}
			sb.appendHtmlConstant("</div>");
		}
	}
	
}
