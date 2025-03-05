package com.esferalia.aon.gwt.fiscal.client.tariff;

import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomNumberBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonStatusSelect;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.tariff.TariffAddInfoPanel.TariffAddInfoCallback;
import com.esferalia.aon.gwt.fiscal.client.tariff.TariffCataloguePanel.TariffCatalogueCallback;
import com.esferalia.aon.occam.api.model.Status;
import com.esferalia.aon.occam.api.model.catalogue.Catalogue;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.tariff.TariffAddInfo;
import com.esferalia.aon.occam.api.model.tariff.TariffCatalogue;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class TariffEntry extends AonCustomDockLayout {
	
	// ------------------------------------------------- CommonServiceAsync
	
	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// ------------------------------------------------- Variables
	
	private final String EMPTY_STRING = "";
	
	private HTMLPanel container;
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private AonCustomCard generalCard = new AonCustomCard("Informaci\u00f3n General");
	private AonStatusSelect status;
	private AonCustomTextBox code = new AonCustomTextBox("C\u00f3digo");
	private AonCustomTextBox name = new AonCustomTextBox("Descripci\u00f3n");
	private AonCustomListBox type = new AonCustomListBox("Tipo");
	private AonCustomNumberBox discount = new AonCustomNumberBox("Descuento");
	
	private AonCustomCard otherDataCard = new AonCustomCard("Otros Datos");
	private SimpleLayoutPanel otherDataCenterPanelCard = new SimpleLayoutPanel();;
	private OtherDataTariffTable otherDataTable;
	
	private AonCustomCard catalogCard = new AonCustomCard("Cat\u00e1logos");
	private SimpleLayoutPanel catalogCenterPanelCard = new SimpleLayoutPanel();;
	private CatalogTariffTable catalogTable;
	
	private RegistryModuleOptions options;
	
	private Tariff tariff;
	
	public TariffEntry(RegistryModuleOptions options) {
		super("TARIFA");
		
		this.options = options;
		initializeCommonService();
		
		addButtonsToolbar();
		hideSearchWidget();
		hideToolbarFilterMessages();
		
		container = new HTMLPanel(EMPTY_STRING);
		container.addStyleName(AON.CSS.aonItemFlex());
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		messagePanel.addStyleName(AON.CSS.aonWidthAll());
		
		container.add(messagePanel);
		
		add(container);
	}
	
	@Override
	protected void onClearFilter() {}
	
	private void addButtonsToolbar() {
		AonToolbarButton backButton = new AonToolbarButton(AON.MSG.backAction(), AON.CSS.aonIconBack());
		backButton.addClickHandler(e -> onBackClick());
		addToolbarButton(backButton);
		
		AonTableButton deleteButton = new AonTableButton("Borrar Tarifa", AON.CSS.aonIconDelete());
		deleteButton.addClickHandler(event -> {
			event.stopPropagation();
			deleteButton.setEnabled(false);
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Tarifa",
					new HTML("Se va a proceder a eliminar la tarifa <b>" + tariff.getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					deleteButton.setEnabled(true);
				}

				@Override
				public void onAccept() {
					delete(tariff.getId());
				}
			});
		});
		addToolbarButton(deleteButton);
		
		AonToolbarButton saveButton = new AonToolbarButton("Guardar Tarifa", AON.CSS.aonIconSave());
		saveButton.addClickHandler(e -> saveTariff());
		addToolbarButton(saveButton);
	}

	public void setTariff(Integer tariffId) {
		getTariff(tariffId, dbTariff -> {
			
			remove(container);
			
			container = new HTMLPanel(EMPTY_STRING);
			container.addStyleName(AON.CSS.aonItemFlex());
			container.addStyleName(AON.CSS.aonFlexColumn());
			container.getElement().getStyle().setProperty("padding", "0 1rem");
			
			messagePanel.addStyleName(AON.CSS.aonWidthAll());
			
			container.add(messagePanel);
			
			FlowPanel cardsRow = createRow(createGeneralCard(), null);
			cardsRow.getElement().getStyle().setProperty("align-items", "start");
			
			FlowPanel cardsRow2 = createRow(createOtherDataCard(), createCatalogCard());
			cardsRow2.getElement().getStyle().setProperty("align-items", "start");
			
			container.add(cardsRow);
			container.add(cardsRow2);
			
			add(container);
			
			Scheduler.get().scheduleDeferred(new Command() {
		        public void execute() {
		        	name.setFocus(true);
		        	
		        	int alturaRestante = calcularAlturaRestante(catalogCenterPanelCard);
		        	catalogCenterPanelCard.setHeight(alturaRestante + "px");
		        	otherDataCenterPanelCard.setHeight(alturaRestante + "px");
		        }
		    });
			
		});
	}

	private AonCustomCard createGeneralCard() {
		FlowPanel table = createFlexColumnPanel();
		
		status = new AonStatusSelect(tariff.isActive() ? Status.ACTIVE : Status.INACTIVE);
		
		generalCard = new AonCustomCard("Informaci\u00f3n General", status);
		generalCard.setToolbarWidgetShown();
		generalCard.addStyleName(AON.CSS.aonWidthAll());
		generalCard.getElement().getStyle().setProperty("min-width", "36rem");
		generalCard.add(table);
		
		code.getTextBox().setMaxLength(8);
		code.setValue(tariff.getCode());
		code.addValueChangeHandler(e -> tariff.setCode(code.getValue().trim()));
		
		name.getTextBox().setMaxLength(32);
		name.setValue(tariff.getName());
		name.addValueChangeHandler(e -> tariff.setName(name.getValue().trim()));

		table.add(createRow(code, name));
		
		type.clearItems();
		type.addItem("Ventas", "0");
		type.addItem("Compras", "1");
		type.setValue(tariff.isPurchase() ? "1" : "0");
		type.addChangeHandler(e -> tariff.setPurchase(AonStringUtils.equalsIgnoreCase(type.getValue(), "1")));
		
		discount.hideNearBy();
		discount.setValue(tariff.getDiscount());
		discount.addValueChangeHandler(e -> tariff.setDiscount(discount.getValue()));
		
		table.add(createRow(type, discount));
		
		return generalCard;
	}
	
	private Widget createOtherDataCard() {
		AonTableButton newTariffAddInfo = new AonTableButton("Nuevo", AON.CSS.aonIconAdd());
		newTariffAddInfo.addClickHandler(e -> onCreateTariffAddInfo());
		
		otherDataCard = new AonCustomCard("Otros Datos", newTariffAddInfo);
		otherDataCard.addStyleName(AON.CSS.aonWidthAll());
		otherDataCard.getElement().getStyle().setProperty("min-width", "36rem");
		
		otherDataCenterPanelCard = new SimpleLayoutPanel();
		
		otherDataTable  = new OtherDataTariffTable(options, tariff) {
			
			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}
		};
		
		otherDataCenterPanelCard.setWidget(otherDataTable);
		
		otherDataCard.add(otherDataCenterPanelCard);
		
		return otherDataCard;
	}
	
	private void onCreateTariffAddInfo() {
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.showCloseButton(true);
		dialog.setCaption("Otro Dato");
		
		TariffAddInfoPanel tariffAddInfoPanel = new TariffAddInfoPanel(options, tariff, new TariffAddInfoCallback() {
			
			@Override
			public void onAccept(TariffAddInfo tariffAddInfo) {
				dialog.hide();
				otherDataTable.onSearch();
			}
		});
		
		dialog.add( tariffAddInfoPanel );
		dialog.showLoaded();
	}
	
	private Widget createCatalogCard() {
		AonTableButton newTariffCatalogue = new AonTableButton("Nuevo", AON.CSS.aonIconAdd());
		newTariffCatalogue.addClickHandler(e -> onCreateTariffCatalogue());
		
		catalogCard = new AonCustomCard("Cat\u00e1logos", newTariffCatalogue);
		catalogCard.addStyleName(AON.CSS.aonWidthAll());
		catalogCard.getElement().getStyle().setProperty("min-width", "36rem");
		
		catalogCenterPanelCard = new SimpleLayoutPanel();
		
		catalogTable = new CatalogTariffTable(options, tariff) {
			
			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}
		};
		
		catalogCenterPanelCard.setWidget(catalogTable);
		
		catalogCard.add(catalogCenterPanelCard);
		
		return catalogCard;
	}
	
	private void onCreateTariffCatalogue() {
		getCatalogueList(catalogues -> {
			AonCustomDialog dialog = new AonCustomDialog();
			dialog.showCloseButton(true);
			dialog.setCaption("Otro Dato");
			
			TariffCataloguePanel tariffAddInfoPanel = new TariffCataloguePanel(options, tariff, catalogues, new TariffCatalogueCallback() {
				
				@Override
				public void onAccept(TariffCatalogue TtriffCatalogue) {
					dialog.hide();
					catalogTable.onSearch();
				}
			});
			
			dialog.add( tariffAddInfoPanel );
			dialog.showLoaded();
		});
	}

	private FlowPanel createFlexColumnPanel() {
        FlowPanel panel = new FlowPanel();
        panel.addStyleName(AON.CSS.aonItemFlex());
        panel.addStyleName(AON.CSS.aonFlexColumn());
        panel.setWidth("100%");
        return panel;
    }
	
	private FlowPanel createRow(Widget widget1, Widget widget2) {
        FlowPanel row = createFlexPanel();
        row.add(widget1);
        if (widget2 != null) {
            row.add(widget2);
        }
        return row;
    }
	
	private FlowPanel createFlexPanel() {
        FlowPanel panel = new FlowPanel();
        panel.addStyleName(AON.CSS.aonItemFlex());
        panel.setWidth("100%");
        return panel;
    }
	
	private int calcularAlturaRestante(Widget widget) {
		// Posicin del widget desde el inicio del documento
	    int posicionWidget = widget.getElement().getAbsoluteTop();

	    // Altura del viewport
	    int alturaViewport = Window.getClientHeight();

	    // Scroll actual (en caso de que la pgina tenga desplazamiento)
	    int scrollActual = Window.getScrollTop();

	    // Altura restante
	    return alturaViewport + scrollActual - posicionWidget - 60;
	}
	
	private void saveTariff() {
		tariff.setActive(status.getValue() == Status.ACTIVE);
		
		AonMessagePanel.showLoading(messagePanel, "Guardando itarifa " + tariff.getName());
		
		commonService.saveTariff(options.getDomainName(), options.getDomain(), options.getUser(), tariff, new AsyncCallback<Tariff>() {

			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error guardado item: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Tariff tariffDb) {
				tariff = tariffDb;
				
				AonMessagePanel.showSuccess(messagePanel, "Tarifa " + tariff.getName()+ " guardada correctamente");
				setTariff(tariff.getId());
			}
		});
	}
	
	private void getTariff(Integer tariffId, Consumer<Tariff> success) {
		commonService.getTariff(options.getDomainName(), options.getDomain(), options.getUser(), tariffId, new AsyncCallback<Tariff>() {
			
			@Override
			public void onSuccess(Tariff tariffDb) {
				tariff = tariffDb;
				success.accept(tariff);	
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo tarifa: " + caught.getMessage());
			}
		});
	}
	
	private void delete(Integer tariffId) {
		commonService.deleteTariff(options.getDomainName(), options.getDomain(), options.getUser(), tariffId, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				onBackClick();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error borrado: " + caught.getMessage());
			}
		});
	}
	
	private void getCatalogueList(Consumer<List<Catalogue>> success) {
		commonService.getCatalogueList(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<Catalogue>>() {
			
			@Override
			public void onSuccess(List<Catalogue> catalogues) {
				success.accept(catalogues);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo tarifas: " + caught.getMessage());
			}
		});
	}
	
	protected abstract void onBackClick();

}
