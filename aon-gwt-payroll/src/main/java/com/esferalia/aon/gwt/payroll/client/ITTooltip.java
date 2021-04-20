package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ITTooltip extends DecoratedPopupPanel {
	
	// --------------------------------------------------- Binder

	interface TooltipUiBinder extends UiBinder<Widget, ITTooltip> {}

	private static TooltipUiBinder uiBinder = GWT.create(TooltipUiBinder.class);
	
	// --------------------------------------------------- UiFields

	@UiField Label fullNameL;
	@UiField Label documentL;
	@UiField Label nafL;
	@UiField HTMLPanel itTypeLeyend;
	@UiField Label itTypeL;
	@UiField Label totalDaysL;
	@UiField Label comunicateStatusL;
	@UiField Label lowDateL;
	@UiField Label lowTypeL;
	@UiField Label highDateL;
	@UiField Label highTypeL;
	
	// --------------------------------------------------- Variables
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");

	private String fullName;
	private String document;
	private String naf;
	private Boolean isComunicate;
	private Byte itType;
	private Byte lowType;
	private Byte highType;
	private Date startDate;
	private Date endDate;
	
	private Integer contractId;
	private Integer itId;
	
	// --------------------------------------------------- Constructor

	public ITTooltip() {
		setGlassEnabled(false);
		setStyleName(AON.AON_TOOLTIP);
		add(uiBinder.createAndBindUi(this));
		setAutoHideEnabled(true);
		addClickHandler();
	}

	// --------------------------------------------------- Show Tooltip

	public void showTooltip(final int clientX, final int clientY) {
		fillITTooltipInfo();
		showToolTip(clientX, clientY);
	}	
	
	// --------------------------------------------------- Show Tooltip.Methods

	private void fillITTooltipInfo() {
		this.fullNameL.setText(this.fullName);
		this.documentL.setText(this.document);
		this.nafL.setText(this.naf);
		
		this.itTypeLeyend.getElement().getStyle().setBackgroundColor(getLeyendColor(this.lowType));
		this.itTypeL.setText(parseLowCause(this.itType));
		this.totalDaysL.setText(getITPartDays());
		
		if(null == this.isComunicate || !this.isComunicate)
			this.comunicateStatusL.setText("Parte IT pediente de comunicar");
		else
			this.comunicateStatusL.setText("Parte IT comunicado");
		
		this.lowTypeL.setText(parseLowCause(this.lowType));
		this.lowDateL.setText(formatDate.format(this.startDate));
		
		this.highTypeL.setText(parseHighCause(this.highType));
		this.highDateL.setText(null == this.endDate ? "" : formatDate.format(this.endDate));
		
	}

	private void showToolTip(final int clientX, final int clientY) {

		try {

			setPopupPositionAndShow(new PopupPanel.PositionCallback() {

				@Override
				public void setPosition(int offsetWidth, int offsetHeight) {

					int windowWidth = Window.getClientWidth();
					int popupX = clientX - offsetWidth / 3;
					int popupY = clientY;

					if (popupX + offsetWidth >= windowWidth - offsetWidth/2)
						popupX -= popupX + offsetWidth*1.2 - windowWidth;

					if (clientY + offsetHeight >= Window.getClientHeight()) {
						popupY = popupY - offsetHeight;
					}

					setPopupPosition(popupX, popupY);
				}
			});

			show();

		} catch (Throwable ex) {
			Window.alert("Error " + ex.getStackTrace() + " " + ex.getMessage());
		}
	}
	
	// --------------------------------------------------- ITTooltip.Setters

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public void setNaf(String naf) {
		this.naf = naf;
	}
	
	public void setComunicationStatus(Boolean isComunicate) {
		this.isComunicate = isComunicate;
	}

	public void setITType(Byte itType) {
		this.itType = itType;
	}

	public void setLowType(Byte lowType) {
		this.lowType = lowType;
	}

	public void setHighType(Byte highType) {
		this.highType = highType;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}
	
	public void setITId(Integer itId) {
		this.itId = itId;
	}
	
	// --------------------------------------------------- Fill Tooltip.Methods
	
	private String parseLowCause(Byte typeLowPart) {
		switch (typeLowPart) {
			case (byte)0:
				return "Enfermedad Com" + String.valueOf("\u00FA") + "n";
			case (byte)1:
				return "Accidente de trabajo";
			case (byte)2:
				return "Maternidad";
			case (byte)3:
				return "Paternidad";
			case (byte)4:
				return "Riesgo para el embarazo";
			case (byte)5:
				return "Riesgo durante la lactancia";
			case (byte)6:
				return "Accidente no laboral";
			case (byte)7:
				return "Enfermedad com" + String.valueOf("\u00FA") + "n periodo de carencia";
			case (byte)8:
				return "Enfermedad com" + String.valueOf("\u00FA") + "n, prestaci" + String.valueOf("\u00F3") + "n profesional (COVID-19)";
			default:
				return "-";
		}
	}
	
	private String getLeyendColor(Byte typeLowPart) {
		switch (typeLowPart) {
		case (byte)0:
			return "#FFA500";
		case (byte)1:
			return "#AA0033";
		case (byte)2:
			return "#FF66CC";
		case (byte)3:
			return "#36C";
		case (byte)4:
			return "#FF66CC";
		case (byte)5:
			return "#FF66CC";
		case (byte)6:
			return "#FFA500";
		case (byte)7:
			return "#FFA500";
		case (byte)8:
			return "#E3DC14";
		default:
			return "white";
		}
	}
	
	private String parseHighCause(Byte typeHighPart) {
		if(null == typeHighPart)
			return "-";
		
		switch (typeHighPart) {
			case (byte)0:
				return "Curaci" + String.valueOf("\u00F3") + "n";
			case (byte)1:
				return "Fallecimiento";
			case (byte)2:
				return "Inspecci" + String.valueOf("\u00F3") + "n m" + String.valueOf("\u00E9") + "dica";
			case (byte)3:
				return "Propuesta incapacidad";
			case (byte)4:
				return "Agotamiento de plazo";
			case (byte)5:
				return "Mejor" + String.valueOf("\u00ED") + "a que permite realizar el trabajo habitual";
			case (byte)6:
				return "Incomparecencia";
			case (byte)7:
				return "Control INSS duraci" + String.valueOf("\u00F3") + "n 12 meses";
			case (byte)8:
				return "Recuperaci" + String.valueOf("\u00F3") + "n capacidad profesional";
			case (byte)9:
				return "Incomparecencia contratos de formaci" + String.valueOf("\u00F3") + "n";
			default:
				return "-";
		}
	}
	
	private String getITPartDays() {
		if(null == this.startDate || null == this.endDate)
			return "";
		
		return "( " + DateUtils.getDaysBetween(this.startDate, this.endDate) + " d\u00EDas de baja )";
	}
	
	// --------------------------------------------------- ClickHandler
	
	private void addClickHandler() {
		ClickHandler clickHandler = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hide();
				onTooltipClick(contractId, itId);
			}
		};
		
		this.addDomHandler(clickHandler, ClickEvent.getType());
	}
	
	// --------------------------------------------------- Abstract Methods
	
		protected abstract void onTooltipClick(Integer contractId, Integer itId);
	
}
