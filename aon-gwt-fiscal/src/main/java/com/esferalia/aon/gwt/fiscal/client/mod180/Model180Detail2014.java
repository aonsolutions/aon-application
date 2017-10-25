package com.esferalia.aon.gwt.fiscal.client.mod180;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.common.client.widget.StreetTypeListBox;
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180Base.Model180BaseCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
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

public class Model180Detail2014 extends ResizeComposite {

	public static final ProvidesKey<Mod180Detail> MOD180_DETAIL_PROVIDES_KEY = new ProvidesKey<Mod180Detail>() {
		@Override
		public Object getKey(Mod180Detail det) {
			return det == null ?null: det.getId() == null? det.getTempId(): det.getId();
		}
	};

	interface Model180Detail2014Binder extends UiBinder<Widget, Model180Detail2014> {}
	private static Model180Detail2014Binder MODEL180_DETAIL_2014_BINDER 
		= GWT.create(Model180Detail2014Binder.class);

	private Mod180 currentMod180;
	private Mod180DetailDataProvider dataProvider;
	private SingleSelectionModel<Mod180Detail> detailModel;

	@UiField
	Button deleteDetailButton;
	@UiField
	Button restoreDeletedButton;
	@UiField
	Button newDetailButton;


	@UiField(provided=true)
	CellList<Mod180Detail> detailList;
	@UiField
	ScrollPanel detailListPanel;

	@UiField
	DocumentTextBox receiverDocument;
	@UiField
	DocumentTextBox representativeDocument;
	@UiField
	TextBox fullName;
	@UiField
	IntegerBox accrualYear;
	@UiField
	ProvinceListBox province;
	@UiField
	DoubleBox perception;
	@UiField
	DoubleBox retention;
	@UiField
	DoubleBox percent;
	@UiField
	CheckBox inKind;
	
	@UiField
	ListBox location;
	@UiField
	TextBox cadasdralReference;
	@UiField
	StreetTypeListBox streetType;
	@UiField
	TextBox streetName;
	@UiField
	TextBox numberType;
	@UiField
	TextBox number;
	@UiField
	TextBox numberSuffix;
	@UiField
	TextBox block;
	@UiField
	TextBox hall;
	@UiField
	TextBox stair;
	@UiField
	TextBox floor;
	@UiField
	TextBox door;
	@UiField
	TextBox complement;
	@UiField
	TextBox city;
	@UiField
	TextBox town;
	@UiField
	TextBox townCode;
	@UiField
	ProvinceListBox provinceCode;
	@UiField
	TextBox zip;
	
	
	
	public Model180Detail2014( Model180BaseCallback callback ) {
		 
		Mod180DetailCell mod180DetailCell = new Mod180DetailCell();
		detailList = new CellList<Mod180Detail>(mod180DetailCell, MOD180_DETAIL_PROVIDES_KEY);
		detailList.setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
		detailList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);

		// Add a selection model so we can select cells.
		detailModel = new SingleSelectionModel<Mod180Detail>(MOD180_DETAIL_PROVIDES_KEY);
		detailModel.addSelectionChangeHandler(new Handler() {
			
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				selectDetail();
			}
			
		});
		detailList.setSelectionModel(detailModel);
		detailList.setEmptyListWidget(new HTML(AON.MSG.noData()));

		dataProvider = new Mod180DetailDataProvider(MOD180_DETAIL_PROVIDES_KEY);
		dataProvider.addDataDisplay(detailList);
		detailList.setVisible(true);
		
		Widget ui = MODEL180_DETAIL_2014_BINDER.createAndBindUi(this);
		initWidget(ui);

		location.addItem( AON.MSG.buildingLocationValue(0) );
		location.addItem( AON.MSG.buildingLocationValue(1) );	
		location.addItem( AON.MSG.buildingLocationValue(2) );
		location.addItem( AON.MSG.buildingLocationValue(3) );
		
		setMod180(callback.getMod180());
	}

	public void setMod180(Mod180 mod180) {
		this.currentMod180 = mod180;

		detailList.setVisibleRangeAndClearData(detailList.getVisibleRange(),true);
	}
	
	private Mod180Detail getDetail() {
		return detailModel.getSelectedObject();
	}
	
	public void selectDetail() {
		receiverDocument.setValue(getDetail().getDocument());
		representativeDocument.setValue(getDetail().getRepresentativeDocument());
		fullName.setValue(getDetail().getName());
		accrualYear.setValue(getDetail().getAccrualYear());
		province.setSelectedIndex(getDetail().getProvince());
		perception.setValue(getDetail().getPerception());
		retention.setValue(getDetail().getRetention());
		percent.setValue(getDetail().getPercent());
		inKind.setValue(getDetail().isInKind());
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
		StreetType st = StreetType.getForIneCode(getDetail().getStreetType());
		streetType.setSelectedIndex(st==null?0:(st.ordinal()+1));
		streetName.setValue(getDetail().getStreetName() );
		numberType.setValue(getDetail().getNumberType() );
		number.setValue(getDetail().getNumber() );
		numberSuffix.setValue(getDetail().getNumberSuffix() );
		block.setValue(getDetail().getBlock() );
		hall.setValue(getDetail().getHall() );
		stair.setValue(getDetail().getStair() );
		floor.setValue(getDetail().getFloor() );
		door.setValue(getDetail().getDoor() );
		complement.setValue(getDetail().getComplement() );
		city.setValue(getDetail().getCity() );
		town.setValue(getDetail().getTown() );
		townCode.setValue(getDetail().getTownCode() );
		if (getDetail().getProvinceCode() != null) {
			try {
				provinceCode.setSelectedIndex( Integer.parseInt(getDetail().getProvinceCode()) );
			} catch (NumberFormatException e) {
				provinceCode.setSelectedIndex( 0 );
			}
		} else {
			provinceCode.setSelectedIndex( 0 );
		}
		zip.setValue(getDetail().getZip() );
		
		restoreDeletedButton.setVisible(getDetail().isDeleted());
		deleteDetailButton.setVisible(!getDetail().isDeleted());
	}

	@UiHandler("receiverDocument")
	void onChangeReceiverDocument(ChangeEvent event) {
		getDetail().setDocument(receiverDocument.getValue());
		markAsDirty();
	}

	@UiHandler("representativeDocument")
	void onChangeRepresentativeDocument(ChangeEvent event) {
		getDetail().setRepresentativeDocument(representativeDocument.getValue());
		markAsDirty();
	}

	@UiHandler("fullName")
	void onChangeFullName(ChangeEvent event) {
		getDetail().setName(fullName.getValue());
		markAsDirty();
		detailList.redraw();
	}

	@UiHandler("accrualYear")
	void onChangeAccrualYear(ChangeEvent event) {
		getDetail().setAccrualYear(accrualYear.getValue());
		markAsDirty();
	}

	@UiHandler("province")
	void onChangeProvince(ChangeEvent event) {
		getDetail().setProvince(province.getSelectedIndex());
		markAsDirty();
	}

	@UiHandler("perception")
	void onChangePerception(ChangeEvent event) {
		getDetail().setPerception(perception.getValue());
		markAsDirty();
	}

	@UiHandler("retention")
	void onChangeRetention(ChangeEvent event) {
		getDetail().setRetention(retention.getValue());
		markAsDirty();
	}

	@UiHandler("percent")
	void onChangePercent(ChangeEvent event) {
		double ret = retention.getValue();
		boolean retChanged = false;
		if (ret == 0) {
			ret = AonMathUtils.round(perception.getValue() * percent.getValue() / 100);
			retention.setValue(ret);
			retChanged = true;
		}
		getDetail().setPercent(percent.getValue());
		if (retChanged) {
			getDetail().setRetention(retention.getValue());
		}
		markAsDirty();
	}

	@UiHandler("inKind")
	void onChangeInKind(ClickEvent event) {
		getDetail().setInKind(inKind.getValue());
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
	@UiHandler("streetType")
	void onChangeStreetType(ChangeEvent event) {
		getDetail().setStreetType(streetType.getValue(streetType.getSelectedIndex()));
		markAsDirty();
	}
	@UiHandler("streetName")
	void onChangeStreetName(ChangeEvent event) {
		getDetail().setStreetName(streetName.getValue());
		markAsDirty();
	}
	@UiHandler("numberType")
	void onChangeNumberType(ChangeEvent event) {
		getDetail().setNumberType(numberType.getValue());
		markAsDirty();
	}
	@UiHandler("number")
	void onChangeNumber(ChangeEvent event) {
		getDetail().setNumber(number.getValue());
		markAsDirty();
	}
	@UiHandler("numberSuffix")
	void onChangeNumberSuffix(ChangeEvent event) {
		getDetail().setNumberSuffix(numberSuffix.getValue());
		markAsDirty();
	}
	@UiHandler("block")
	void onChangeBlock(ChangeEvent event) {
		getDetail().setBlock(block.getValue());
		markAsDirty();
	}
	@UiHandler("hall")
	void onChangeHall(ChangeEvent event) {
		getDetail().setHall(hall.getValue());
		markAsDirty();
	}
	@UiHandler("stair")
	void onChangeStair(ChangeEvent event) {
		getDetail().setStair(stair.getValue());
		markAsDirty();
	}
	@UiHandler("floor")
	void onChangeFloor(ChangeEvent event) {
		getDetail().setFloor(floor.getValue());
		markAsDirty();
	}
	@UiHandler("door")
	void onChangeDoor(ChangeEvent event) {
		getDetail().setDoor(door.getValue());
		markAsDirty();
	}
	@UiHandler("complement")
	void onChangeComplement(ChangeEvent event) {
		getDetail().setComplement(complement.getValue());
		markAsDirty();
	}
	@UiHandler("city")
	void onChangeCity(ChangeEvent event) {
		getDetail().setCity(city.getValue());
		markAsDirty();
	}
	@UiHandler("town")
	void onChangeTown(ChangeEvent event) {
		getDetail().setTown(town.getValue());
		markAsDirty();
	}
	@UiHandler("townCode")
	void onChangeTownCode(ChangeEvent event) {
		getDetail().setTownCode(townCode.getValue());
		markAsDirty();
	}
	@UiHandler("provinceCode")
	void onChangeProvinceCode(ChangeEvent event) {
		getDetail().setProvinceCode(Integer.toString(provinceCode.getSelectedIndex()));
		markAsDirty();
	}
	@UiHandler("zip")
	void onChangeZip(ChangeEvent event) {
		getDetail().setZip(zip.getValue());
		markAsDirty();
	}

	@UiHandler("deleteDetailButton")
	void onDeleteDetailButtonClick(ClickEvent event) {
		getDetail().setDeleted(true);
		restoreDeletedButton.setVisible(true);
		deleteDetailButton.setVisible(false);
		detailList.redraw();
	}
	
	@UiHandler("restoreDeletedButton")
	void onRestoreDeletedButtonClick(ClickEvent event) {
		getDetail().setDeleted(false);
		if (!getDetail().isDirty()) {
			restoreDeletedButton.setVisible(false);
			deleteDetailButton.setVisible(true);
			detailList.redraw();
		}
	};

	@UiHandler("newDetailButton")
	void onNewDetailButtonClick(ClickEvent event) {
		newPerceptor();
	}
	
	private void newPerceptor(){
		currentMod180.getDetails().add(
			new Mod180Detail()
			.setDirty(true)
			.setTempId((currentMod180.getDetails().size() * (-1)))
		);
		detailList.setRowCount(detailList.getRowCount() + 1);
		detailList.setPageSize(detailList.getRowCount());
		detailList.redraw();
		selectInList(currentMod180.getDetails().size() - 1);
		selectDetail();		
	}
	
	private void selectInList(int i) {
		detailModel.setSelected(currentMod180.getDetails().get(i),true);
		detailList.getRowElement(i).scrollIntoView();
		detailListPanel.scrollToLeft();
	}

	class Mod180DetailDataProvider extends AsyncDataProvider<Mod180Detail> {

		public Mod180DetailDataProvider(ProvidesKey<Mod180Detail> detailProvidesKey) {
			super(detailProvidesKey);
		}

		@Override
		protected void onRangeChanged(HasData<Mod180Detail> display) {
			if (currentMod180 != null && currentMod180.getId() != null) {
				if (currentMod180.getDetails().size() == 0) {
					newPerceptor();					
				} else {
					updateRowCount(currentMod180.getDetails().size(), true);
					updateRowData(0, currentMod180.getDetails());
					detailList.setPageSize(currentMod180.getDetails().size());
					selectInList(0);
					selectDetail();
				}
			}
		}
	}
	private void markAsDirty() {
		if (!getDetail().isDirty()) {
			getDetail().setDirty(true);
			detailList.redraw();		
		}
	}

	static class Mod180DetailCell extends AbstractCell<Mod180Detail> {
		@Override
		public void render(Cell.Context context, Mod180Detail value,
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
