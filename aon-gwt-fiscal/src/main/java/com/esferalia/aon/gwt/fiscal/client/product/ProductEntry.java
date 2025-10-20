package com.esferalia.aon.gwt.fiscal.client.product;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCheckBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog.AonCustomDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomMultiSelectBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomNumberBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.product.ItemCompositionPanel.ItemCompositionCallback;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemAddInfo;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.ProductComposition;
import com.esferalia.aon.occam.api.model.product.ProductConsole;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ProductEntry extends AonCustomDockLayout {
	
	// ------------------------------------------------- CommonServiceAsync
	
	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// ------------------------------------------------- Variables
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private final String EMPTY_STRING = "";
	
	private HTMLPanel container;
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private AonCustomCard generalCard = new AonCustomCard("Datos Comerciales");
	private ProductStatusSelect status;
	private AonCustomTextBox code = new AonCustomTextBox("C\u00f3digo Comercial");
	private AonCustomTextBox barcode = new AonCustomTextBox("C\u00f3digo Barras");
	private AonCustomTextBox name = new AonCustomTextBox("Descripci\u00f3n");
	private AonCustomNumberBox price = new AonCustomNumberBox("Precio");
	private AonCustomListBox iva = new AonCustomListBox("IVA");
	private AonCustomNumberBox pvp = new AonCustomNumberBox("P.V.P.");
	private AonCustomIntegerBox limit = new AonCustomIntegerBox("L\u00edmite"); // LIMIT_MAX
	private AonCustomCheckBox limitOverflow = new AonCustomCheckBox("Bloqueo Tras L\u00edmite"); // LIMIT_LOCK_OVERFLOW
	
	private AonCustomCard aditionalCard = new AonCustomCard("Datos Contrataci\u00f3n");
	private ProductConsoleSelect console;
	private AonCustomListBox app = new AonCustomListBox("Servicio");
	private AonCustomMultiSelectBox domainType = new AonCustomMultiSelectBox("Tipo Dominio");
	private AonCustomListBox category = new AonCustomListBox("Categor\u00eda");
	private AonCustomMultiSelectBox tags = new AonCustomMultiSelectBox("Etiquetas");
	private AonCustomCheckBox pack = new AonCustomCheckBox("Pack");
	private AonCustomCheckBox composite = new AonCustomCheckBox("Compuesto");
	private AonCustomIntegerBox trial = new AonCustomIntegerBox("D\u00edas Prueba"); // TRIAL_LIMIT_DAYS
	private AonCustomDateBox trialLimit = new AonCustomDateBox("Fecha L\u00edmite Prueba"); // TRIAL_LIMIT_DATE
	private AonCustomCheckBox trialOverflow = new AonCustomCheckBox("Bloqueo Tras Prueba");  // TRIAL_LIMIT_LOCK_OVERFLOW
	
	private FlowPanel tariffRow = new FlowPanel();
	
	private AonCustomCard tariffCard = new AonCustomCard("Tarifas");
	private SimpleLayoutPanel tariffCenterPanelCard;
	private ItemTariffTable itemTariffTable;
	
	private AonCustomCard compositeCard = new AonCustomCard("Pack Productos");
	private ProductCompositionSelect productComposition;
	private SimpleLayoutPanel compositeCenterPanelCard = new SimpleLayoutPanel();;
	private CompositeItemTable compositeItemTable;
	
	private RegistryModuleOptions options;
	
	private Product product;
	private Item item;
	private List<Tax> taxes;
	private List<ProductCategory> productCategories;
	private List<ItemAddInfo> itemAddInfo;
	
	// Tags
	private List<Tag> tagList;
	
	public ProductEntry(RegistryModuleOptions options) {
		super("SERVICIO AON");
		
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
		
		AonTableButton deleteButton = new AonTableButton("Borrar Producto", AON.CSS.aonIconDelete());
		deleteButton.addClickHandler(event -> {
			event.stopPropagation();
			deleteButton.setEnabled(false);
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Producto",
					new HTML("Se va a proceder a eliminar el producto <b>" + product.getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					deleteButton.setEnabled(true);
				}

				@Override
				public void onAccept() {
					delete(product.getId());
				}
			});
		});
		addToolbarButton(deleteButton);
		
		AonToolbarButton saveButton = new AonToolbarButton("Guardar Producto", AON.CSS.aonIconSave());
		saveButton.addClickHandler(e -> saveProduct());
		addToolbarButton(saveButton);
	}

	public void setProduct(Integer productId) {
		getProduct(productId, dbProduct -> {
			
			remove(container);
			
			tariffRow.clear();
			if(tariffRow.getWidgetCount() > 1) tariffRow.remove(1);
			
			container = new HTMLPanel(EMPTY_STRING);
			container.addStyleName(AON.CSS.aonItemFlex());
			container.addStyleName(AON.CSS.aonFlexColumn());
			container.getElement().getStyle().setProperty("padding", "0 1rem");
			
			messagePanel.addStyleName(AON.CSS.aonWidthAll());
			
			container.add(messagePanel);
			
			FlowPanel cardsRow = createRow(createGeneralCard(), createAditionalCard());
			cardsRow.getElement().getStyle().setProperty("align-items", "start");
			
			tariffRow = createRow(createTariffCard(), product.isComposition() ? createCompositeCard() : null);
			tariffRow.getElement().getStyle().setProperty("align-items", "start");
			
			container.add(cardsRow);
			container.add(tariffRow);
			
			add(container);
			
			Scheduler.get().scheduleDeferred(new Command() {
		        public void execute() {
		        	name.setFocus(true);
		        	
		        	int alturaRestante = calcularAlturaRestante(tariffCenterPanelCard);
		        	tariffCenterPanelCard.setHeight(alturaRestante + "px");
		        	compositeCenterPanelCard.setHeight(alturaRestante + "px");
		        }
		    });
			
		});
	}

	private AonCustomCard createGeneralCard() {
		FlowPanel table = createFlexColumnPanel();
		
		status = new ProductStatusSelect(product.getStatus());
		
		generalCard = new AonCustomCard("Datos Comerciales", status);
		generalCard.setToolbarWidgetShown();
		generalCard.addStyleName(AON.CSS.aonWidthAll());
		generalCard.getElement().getStyle().setProperty("min-width", "36rem");
		generalCard.add(table);
		
		code.getTextBox().getElement().getStyle().setProperty("text-transform", "uppercase");
		code.getTextBox().setMaxLength(15);
		code.setValue(product.getCode());
		code.addValueChangeHandler(e -> product.setCode(code.getValue().trim().toUpperCase()));
		
		category.clearItems();
		category.addItem("-", "");
		getProductCategories(productCategories -> {
			productCategories.forEach(productCategory -> category.addItem(productCategory.getName(), productCategory.getId().toString()));
			category.setValue(null == product.getCategory() || null == product.getCategory().getId() ? "" : product.getCategory().getId().toString());
		});
		category.addChangeHandler(e -> product.setCategory(AonStringUtils.isBlank(category.getValue()) ? null : productCategories.stream().filter(pc -> pc.getId().equals(Integer.parseInt(category.getValue()))).findFirst().get()));
		
		
		table.add(createRow(code, category));
		
		name.setValue(product.getName());
		name.addValueChangeHandler(e -> {
			product.setName(name.getValue());
			item.setDescription(name.getValue());
		});
		
		table.add(createRow(name, null));
		
		price.hideNearBy();
		price.setValue(item.getPrice());
		price.addValueChangeHandler(e -> {
			item.setPrice(price.getValue());
			pvp.setValue(createPVP());
		});
		
		iva.clearItems();
		getTaxTypes(taxTypes -> {
			taxTypes.forEach(taxType -> iva.addItem(taxType.getName(), taxType.getId().toString()));
			iva.setValue(null == product.getVat() ? "" : product.getVat().getId().toString());
			
			pvp.setValue(createPVP());
		});
		iva.addChangeHandler(e -> {
			product.setVat(new Tax().setId(Integer.parseInt(iva.getValue())));
			pvp.setValue(createPVP());
		});
		
		pvp.hideNearBy();
		pvp.setEnable(false);
		
		table.add(createRow(price, iva, pvp));
		
		limit.hideNearBy();
		limit.addValueChangeHandler(e -> {
			Optional<ItemAddInfo> itemAddInfoOpt = itemAddInfo.stream().filter(i -> AonStringUtils.equalsIgnoreCase(i.getAttribute(), "LIMIT_MAX")).findFirst();
			if(itemAddInfoOpt.isEmpty()) {
				itemAddInfo.add(
					new ItemAddInfo()
						.setDomain(product.getDomain().getId())
						.setProduct(product.getId())
						.setItem(item.getId())
						.setAttribute("LIMIT_MAX")
						.setValue(null == limit.getValue() ? null : limit.getValue().toString())
						.setDate(new Date())
				);
			} else itemAddInfoOpt.get().setValue(null == limit.getValue() ? null : limit.getValue().toString());
		});
		Optional<ItemAddInfo> limitOpt = itemAddInfo.stream().filter(i -> AonStringUtils.equalsIgnoreCase(i.getAttribute(), "LIMIT_MAX")).findFirst();
		limit.setValue(limitOpt.isEmpty() ? null : Integer.parseInt(limitOpt.get().getValue()));
		
		limitOverflow.setWidth("10rem");
		limitOverflow.addValueChangeHandler(e -> {
			Optional<ItemAddInfo> itemAddInfoOpt = itemAddInfo.stream().filter(i -> AonStringUtils.equalsIgnoreCase(i.getAttribute(), "LIMIT_LOCK_OVERFLOW")).findFirst();
			if(itemAddInfoOpt.isEmpty()) {
				itemAddInfo.add(
					new ItemAddInfo()
						.setDomain(product.getDomain().getId())
						.setProduct(product.getId())
						.setItem(item.getId())
						.setAttribute("LIMIT_LOCK_OVERFLOW")
						.setValue(!limitOverflow.getValue() ? null : Boolean.toString(limitOverflow.getValue()))
						.setDate(new Date())
				);
			} else itemAddInfoOpt.get().setValue(!limitOverflow.getValue() ? null : Boolean.toString(limitOverflow.getValue()));
		});
		Optional<ItemAddInfo> limitOverflowOpt = itemAddInfo.stream().filter(i -> AonStringUtils.equalsIgnoreCase(i.getAttribute(), "LIMIT_LOCK_OVERFLOW")).findFirst();
		limitOverflow.setValue(limitOverflowOpt.isEmpty() ? false : Boolean.parseBoolean(limitOverflowOpt.get().getValue()));
		
		table.add(createRow(limit, limitOverflow));
		
		return generalCard;
	}

	private AonCustomCard createAditionalCard() {
		FlowPanel table = createFlexColumnPanel();
		
		console = new ProductConsoleSelect(isConsole() ? ProductConsole.CONSOLE : ProductConsole.SELF_CONTRACT);
		console.addBlurHandler(e -> createBarCode());
		
		aditionalCard = new AonCustomCard("Datos Contrataci\u00f3n", console);
		aditionalCard.setToolbarWidgetShown();
		aditionalCard.addStyleName(AON.CSS.aonWidthAll());
		aditionalCard.getElement().getStyle().setProperty("min-width", "36rem");
		aditionalCard.add(table);
		
		app.clearItems();
		app.addItem("-", "--");
		AonApp.getValues().forEach(appIt -> app.addItem(appIt.getDescription(), AonStringUtils.leftPad(appIt.ordinal() + "", 2, "0") ));
		app.setValue(AonStringUtils.leftPad(AonStringUtils.substring(item.getBarcode(), 0, 2), 2, "0"));
		app.addChangeHandler(e -> createBarCode());
		
		barcode.setEnable(product.isComposition());
		barcode.setValue(formatBarCode(item.getBarcode()));
		barcode.addValueChangeHandler(e -> createBarCode());
		
		pack = new AonCustomCheckBox("Pack");
		pack.setWidth("3rem");
		pack.setValue(product.isManufactured());
		pack.addValueChangeHandler(e -> {
			product.setManufactured(pack.getValue());
			saveProduct();
		});
		
		composite = new AonCustomCheckBox("Compuesto");
		composite.setWidth("5rem");
		composite.setValue(product.isComposition());
		composite.addValueChangeHandler(e -> {
			barcode.setEnable(pack.getValue());
			
			product.setComposition(composite.getValue());
			
			if(!composite.getValue()) {
				
				getItemCompositions(itemCompositions -> {
					if(itemCompositions.isEmpty()) saveProduct();
					else {
						AonDialog dialog = new AonDialog("Eliminaci\u00f3n Producto Compuesto",
								new HTML("Se va a proceder a eliminar los productos compuestos.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
						
						dialog.confirm(new AonAcceptDialogCallback() {

							@Override
							public void onCancel() {}

							@Override
							public void onAccept() {
								compositeItemTable.deleteCompositions(end -> 
									getItemCompositions(e -> saveProduct())
								);
							}
						});
					}
				});
				
			} else
				saveProduct();
		});
		
		table.add(createRow(app, barcode, pack, composite));
		
		domainType.setOptions(DomainType.getValues().stream().filter(domainType -> domainType != DomainType.ADMIN).map(domainType -> domainType.getName()).collect(Collectors.toSet()));
		domainType.setSelectedOptions(getSelectedDomainTypes());
		domainType.addBlurHandler(e -> createBarCode());
		
		table.add(createRow(domainType, null));
		
		// TAGS
		tags.getElement().getStyle().setProperty("max-width", "90%");
		getTags(aviableTags -> {
			tags.setOptions(aviableTags.stream().map(aviableTag -> aviableTag.getName()).collect(Collectors.toSet()));
			
			getProductTags(product.getId(), productTags -> {
				Set<String> selectedProductTags = productTags.stream().map(productTag -> productTag.getTag().getName()).collect(Collectors.toSet());
				tags.setSelectedOptions(selectedProductTags);
			});
		});
		
		table.add(createRow(tags, null));
		
		trial.hideNearBy();
		trial.addValueChangeHandler(e -> {
			Optional<ItemAddInfo> itemAddInfoOpt = itemAddInfo.stream().filter(i -> AonStringUtils.equalsIgnoreCase(i.getAttribute(), "TRIAL_LIMIT_DAYS")).findFirst();
			if(itemAddInfoOpt.isEmpty()) {
				itemAddInfo.add(
					new ItemAddInfo()
						.setDomain(product.getDomain().getId())
						.setProduct(product.getId())
						.setItem(item.getId())
						.setAttribute("TRIAL_LIMIT_DAYS")
						.setValue(null == trial.getValue() ? null : trial.getValue().toString())
						.setDate(new Date())
				);
			} else itemAddInfoOpt.get().setValue(null == trial.getValue() ? null : trial.getValue().toString());
		});
		Optional<ItemAddInfo> trialOpt = itemAddInfo.stream().filter(i -> AonStringUtils.equalsIgnoreCase(i.getAttribute(), "TRIAL_LIMIT_DAYS")).findFirst();
		trial.setValue(trialOpt.isEmpty() ? null : Integer.parseInt(trialOpt.get().getValue()));
		
		trialLimit.addValueChangeHandler(e -> {
			Optional<ItemAddInfo> itemAddInfoOpt = itemAddInfo.stream().filter(i -> AonStringUtils.equalsIgnoreCase(i.getAttribute(), "TRIAL_LIMIT_DATE")).findFirst();
			if(itemAddInfoOpt.isEmpty()) {
				itemAddInfo.add(
					new ItemAddInfo()
						.setDomain(product.getDomain().getId())
						.setProduct(product.getId())
						.setItem(item.getId())
						.setAttribute("TRIAL_LIMIT_DATE")
						.setValue(null == trialLimit.getValue() ? null : formatDate.format(trialLimit.getValue()))
						.setDate(new Date())
				);
			} else itemAddInfoOpt.get().setValue(null == trialLimit.getValue() ? null : formatDate.format(trialLimit.getValue()));
		});
		Optional<ItemAddInfo> trialLimitOpt = itemAddInfo.stream().filter(i -> AonStringUtils.equalsIgnoreCase(i.getAttribute(), "TRIAL_LIMIT_DATE")).findFirst();
		trialLimit.setValue(trialLimitOpt.isEmpty() ? null :formatDate.parse(trialLimitOpt.get().getValue()));
		
		trialOverflow.setWidth("21rem");
		trialOverflow.addValueChangeHandler(e -> {
			Optional<ItemAddInfo> itemAddInfoOpt = itemAddInfo.stream().filter(i -> AonStringUtils.equalsIgnoreCase(i.getAttribute(), "TRIAL_LIMIT_LOCK_OVERFLOW")).findFirst();
			if(itemAddInfoOpt.isEmpty()) {
				itemAddInfo.add(
					new ItemAddInfo()
						.setDomain(product.getDomain().getId())
						.setProduct(product.getId())
						.setItem(item.getId())
						.setAttribute("TRIAL_LIMIT_LOCK_OVERFLOW")
						.setValue(!limitOverflow.getValue() ? null : Boolean.toString(trialOverflow.getValue()))
						.setDate(new Date())
				);
			} else itemAddInfoOpt.get().setValue(!trialOverflow.getValue() ? null : Boolean.toString(trialOverflow.getValue()));
		});
		Optional<ItemAddInfo> trialOverflowOpt = itemAddInfo.stream().filter(i -> AonStringUtils.equalsIgnoreCase(i.getAttribute(), "TRIAL_LIMIT_LOCK_OVERFLOW")).findFirst();
		trialOverflow.setValue(trialOverflowOpt.isEmpty() ? false : Boolean.parseBoolean(trialOverflowOpt.get().getValue()));
		
		
		table.add(createRow(trial, trialLimit, trialOverflow));
		
		return aditionalCard;
	}
	
	private Widget createTariffCard() {
		tariffCard = new AonCustomCard("Tarifas");
		tariffCard.addStyleName(AON.CSS.aonWidthAll());
		tariffCard.getElement().getStyle().setProperty("min-width", "36rem");
		
		tariffCenterPanelCard = new SimpleLayoutPanel();
		
		itemTariffTable  = new ItemTariffTable(options, product, item) {
			
			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}
		};
		
		tariffCenterPanelCard.setWidget(itemTariffTable);
		
		tariffCard.add(tariffCenterPanelCard);
		
		return tariffCard;
	}

	private Widget createCompositeCard() {
		FlowPanel addStatusPanel = new FlowPanel();
		addStatusPanel.addStyleName(AON.CSS.aonItemFlex());
		
		AonTableButton newComposition = new AonTableButton("Nuevo producto", AON.CSS.aonIconAdd());
		newComposition.addClickHandler(e -> onCreateItemComposition());
		addStatusPanel.add(newComposition);
		
		productComposition = new ProductCompositionSelect(product.isCompositionPrice() ? ProductComposition.DIVISIBLE : ProductComposition.COMPLETE);
		productComposition.addBlurHandler(e -> product.setCompositionPrice(productComposition.getValue() == ProductComposition.DIVISIBLE));
		addStatusPanel.add(productComposition);
		
		compositeCard = new AonCustomCard("Pack Productos", addStatusPanel);
		compositeCard.addStyleName(AON.CSS.aonWidthAll());
		compositeCard.getElement().getStyle().setProperty("min-width", "36rem");
		compositeCard.setToolbarWidgetShown();
		
		compositeItemTable  = new CompositeItemTable(options, product, item) {
			
			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}
		};
		
		compositeCenterPanelCard.setWidget(compositeItemTable);
		
		compositeCard.add(compositeCenterPanelCard);
		
		return compositeCard;
	}
	
	private void onCreateItemComposition() {
		getItems(itemList -> 
			getItemCompositions(itemCompositions -> {
				AonCustomDialog dialog = new AonCustomDialog();
				dialog.showCloseButton(true);
				dialog.setCaption("Item Compuesto");
				
				ItemCompositionPanel itemTariffPanel = new ItemCompositionPanel(options, itemList, itemCompositions, item, new ItemCompositionCallback() {
					
					@Override
					public void onAccept(ItemComposition itemComposition) {
						dialog.hide();
						compositeItemTable.onSearch();
					}
				});
				
				dialog.add( itemTariffPanel );
				dialog.showLoadedCB(new AonCustomDialogCallback() {
					
					@Override
					public void onEnd() {
						dialog.center();
						dialog.show();
					}
				});
			})
		);
	}

	private Set<String> getSelectedDomainTypes() {
		Set<String> domainTypes = new HashSet<String>();
		if(AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 2, 3))) domainTypes.add("Empresa");
		if(AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 3, 4))) domainTypes.add("Asesor\u00EDa");
		if(AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 4, 5))) domainTypes.add("Garaje");
		if(AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 5, 6))) domainTypes.add("Academ\u00EDa");
		if(AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 6, 7))) domainTypes.add("Hotel");
//		if(AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 7, 8))) domainTypes.add("Administraci\u00F3n");
		if(AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 8, 9))) domainTypes.add("Despacho");
		if(AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 9, 10))) domainTypes.add("Gen\u00E9rico");
		if(AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 10, 11))) domainTypes.add("Comercio");
		if(AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 11, 12))) domainTypes.add("Kit Digital");
		
		return domainTypes;
	}

	private Double createPVP() {
		// GENERAL
		return item.getPrice() + (item.getPrice() * getVatPercentage() / 100);
	}

	private double getVatPercentage() {
		Optional<Tax> taxOpt = taxes.stream().filter(tax -> AonStringUtils.equalsIgnoreCase(tax.getId().toString(), iva.getValue())).findFirst();
		return taxOpt.isEmpty() ? 0.00 : taxOpt.get().getPercentage();
	}

	private boolean isConsole() {
		return AonStringUtils.isBlank(item.getBarcode()) ? false : AonStringUtils.equalsIgnoreCase("1", AonStringUtils.substring(item.getBarcode(), 7, 8));
	}

	private String createBarCode() {
		String barCode = app.getValue();
		
		if(AonStringUtils.isBlank(barCode)) {
			barcode.setValue(null);
			item.setBarcode(null);
			
			return "";
		}
	
		barCode += domainType.getSelectedOptions().contains("Empresa") ? "1" : "0";
		barCode += domainType.getSelectedOptions().contains("Asesor\u00EDa") ? "1" : "0";
		barCode += domainType.getSelectedOptions().contains("Garaje") ? "1" : "0";
		barCode += domainType.getSelectedOptions().contains("Academ\u00EDa") ? "1" : "0";
		barCode += domainType.getSelectedOptions().contains("Hotel") ? "1" : "0";
		barCode += console.getValue() == ProductConsole.CONSOLE ? "1" : "0";
		barCode += domainType.getSelectedOptions().contains("Despacho") ? "1" : "0";
		barCode += domainType.getSelectedOptions().contains("Gen\u00E9rico") ? "1" : "0";
		barCode += domainType.getSelectedOptions().contains("Comercio") ? "1" : "0";
		barCode += domainType.getSelectedOptions().contains("Kit Digital") ? "1" : "0";
		
		barcode.setValue(formatBarCode(barCode));
		item.setBarcode(barCode);
		
		return barCode;
	}

	private String formatBarCode(String barCode) {
		return AonStringUtils.isBlank(barCode) ? "" 
				: AonStringUtils.substring(barCode, 0, 2) + " / " +  AonStringUtils.substring(barCode, 2, barCode.length() - 1);
	}

	private FlowPanel createFlexColumnPanel() {
        FlowPanel panel = new FlowPanel();
        panel.addStyleName(AON.CSS.aonItemFlex());
        panel.addStyleName(AON.CSS.aonFlexColumn());
        panel.setWidth("100%");
        return panel;
    }
	
	private FlowPanel createRow(Widget ...widgets) {
        FlowPanel row = createFlexPanel();
        for(int i = 0; i < widgets.length; i++) {
        	if(null != widgets[i]) row.add(widgets[i]);
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
	    return alturaViewport + scrollActual - posicionWidget - 85;
	}
	
	private List<ProductTag> getProductTags() {
		Set<String> selectedTags = tags.getSelectedOptions();
		
		List<ProductTag> productTags = new ArrayList<ProductTag>();
		
		selectedTags.forEach(selectedTag -> {
			Optional<Tag> tagOpt = tagList.stream().filter(tag -> AonStringUtils.equalsIgnoreCase(tag.getName(), selectedTag)).findFirst();
			if(tagOpt.isPresent()) {
				productTags.add(
					new ProductTag()
						.setDomain(options.getDomain())
						.setProduct(product.getId())
						.setTag(tagOpt.get())
				);
			}
		});
		
		return productTags;
	}
	
	private void saveProduct() {
		// Update Status (should use handler for this)
		product.setStatus(status.getValue());
		item.setStatus(status.getValue());
		
		AonMessagePanel.showLoading(messagePanel, "Guardando item producto " + product.getName());
		
		commonService.saveItem(options.getDomainName(), options.getDomain(), options.getUser(), item, new AsyncCallback<Item>() {

			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error guardado item: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Item itemDB) {
				item = itemDB;
				
				AonMessagePanel.showLoading(messagePanel, "Guardando producto " + product.getName());
				
				commonService.saveProduct(options.getDomainName(), options.getDomain(), options.getUser(), product, new AsyncCallback<Product>() {
					
					@Override
					public void onSuccess(Product productDB) {
						product = productDB;
						
						AonMessagePanel.showLoading(messagePanel, "Guardando etiquetas producto " + product.getName());
						
						commonService.saveProductTags(options.getDomainName(), options.getDomain(), options.getUser(), product.getId(), getProductTags(), new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								AonMessagePanel.showError(messagePanel, "Error guardado etiquetas producto: " + caught.getMessage());
							}

							@Override
							public void onSuccess(Void arg0) {
								
								AonMessagePanel.showLoading(messagePanel, "Guardando item producto addInfo " + product.getName());
								
								commonService.saveItemAddInfos(options.getDomainName(), options.getDomain(), options.getUser(), itemAddInfo, new AsyncCallback<Void>() {

									@Override
									public void onFailure(Throwable caught) {
										AonMessagePanel.showError(messagePanel, "Error guardado item producto addInfo: " + caught.getMessage());
									}

									@Override
									public void onSuccess(Void arg0) {
										
										AonMessagePanel.showSuccess(messagePanel, "Producto " + product.getName()+ " guardado correctamente");
										
										setProduct(product.getId());
									}
									
								});
							}
							
						});
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error guardado producto: " + caught.getMessage());
					}
				});
			}
		});
	}
	
	private void getProduct(Integer productId, Consumer<Product> success) {
		commonService.getProduct(options.getDomainName(), options.getDomain(), options.getUser(), productId, new AsyncCallback<Product>() {
			
			@Override
			public void onSuccess(Product productDb) {
				product = productDb;
				
				commonService.getItem(options.getDomainName(), options.getDomain(), options.getUser(), productId, new AsyncCallback<Item>() {

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error obteniendo item: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Item itemDb) {
						item = itemDb;
						
						commonService.getItemAddInfos(options.getDomainName(), options.getDomain(), options.getUser(), item.getId(), new AsyncCallback<List<ItemAddInfo>>() {

							@Override
							public void onFailure(Throwable caught) {
								AonMessagePanel.showError(messagePanel, "Error obteniendo item addInfo: " + caught.getMessage());
							}

							@Override
							public void onSuccess(List<ItemAddInfo> itemAddInfoDb) {
								itemAddInfo = itemAddInfoDb;
								success.accept(product);
							}}
						);	
					}}
				);		
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo producto: " + caught.getMessage());
			}
		});
	}
	
	private void delete(Integer productId) {
		commonService.deleteProduct(options.getDomainName(), options.getDomain(), options.getUser(), product.getId(), new AsyncCallback<Void>() {
			
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
	
	private void getProductCategories(Consumer<List<ProductCategory>> success) {
		commonService.getProductCategories(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<ProductCategory>>() {
			
			@Override
			public void onSuccess(List<ProductCategory> productCategoriesDb) {
				productCategories = productCategoriesDb;
				success.accept(productCategories);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo producto: " + caught.getMessage());
			}
		});
	}
	
	private void getTaxTypes(Consumer<List<Tax>> success) {
		commonService.getTaxTypes(options.getDomainName(), options.getDomain(), options.getUser(), TaxType.VAT, new AsyncCallback<List<Tax>>() {
			
			@Override
			public void onSuccess(List<Tax> taxTypes) {
				taxes = taxTypes;
				success.accept(taxTypes);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo producto: " + caught.getMessage());
			}
		});
	}
	
	private void getProductTags(Integer productId, Consumer<List<ProductTag>> success) {
		commonService.getProductTags(options.getDomainName(), options.getDomain(), options.getUser(), productId, new AsyncCallback<List<ProductTag>>() {
			
			@Override
			public void onSuccess(List<ProductTag> productTags) {
				success.accept(productTags);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo producto: " + caught.getMessage());
			}
		});
	}
	
	private void getTags(Consumer<List<Tag>> success) {
		commonService.getTags(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<Tag>>() {
			
			@Override
			public void onSuccess(List<Tag> tags) {
				tagList = tags;
				success.accept(tags);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo producto: " + caught.getMessage());
			}
		});
	}

	private void getItems(Consumer<List<Item>> success) {
		// Allow al item for compisition /*ProductType.AUXILIARY*/
		commonService.getItems(options.getDomainName(), options.getDomain(), options.getUser(), null, new AsyncCallback<List<Item>>() {
			
			@Override
			public void onSuccess(List<Item> itemsDb) {
				success.accept(itemsDb);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo items : " + caught.getMessage());
			}
		});
	}
	
	private void getItemCompositions(Consumer<List<ItemComposition>> success) {
		commonService.getItemCompositions(options.getDomainName(), options.getDomain(), options.getUser(), item.getId(), new AsyncCallback<List<ItemComposition>>() {
			
			@Override
			public void onSuccess(List<ItemComposition> itemCompositionsDb) {
				success.accept(itemCompositionsDb);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo item compuesto : " + caught.getMessage());
			}
		});
	}
	
	protected abstract void onBackClick();

}
