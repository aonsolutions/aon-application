package com.esferalia.aon.gwt.fiscal.client.product;

import java.util.ArrayList;
import java.util.Date;
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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomRichText;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.product.ItemCompositionPanel.ItemCompositionCallback;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemAddInfo;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.api.model.product.ProductBooking;
import com.esferalia.aon.occam.api.model.product.ProductBookingPriceType;
import com.esferalia.aon.occam.api.model.product.ProductBookingType;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.product.ProductComposition;
import com.esferalia.aon.occam.api.model.product.ProductConsole;
import com.esferalia.aon.occam.api.model.product.ProductParams;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.api.model.product.Tax;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
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
	
	private TabLayoutPanel tablayoutPanel;
	
	private AonCustomCard generalCard = new AonCustomCard("Datos Comerciales");
	private ProductStatusSelect status;
	private AonCustomTextBox code = new AonCustomTextBox("C\u00f3digo Comercial");
	private AonCustomTextBox name = new AonCustomTextBox("Nombre");
	private AonCustomRichText comercialDescription = new AonCustomRichText("Descripci\u00f3n Comercial");
	
	private AonCustomCard aditionalCard = new AonCustomCard("Datos Contrataci\u00f3n");
	private ProductConsoleSelect console;
	private AonCustomListBox type = new AonCustomListBox("Tipo");
	private AonCustomMultiSelectBox aonApps = new AonCustomMultiSelectBox("Servicios Aon");
	private AonCustomMultiSelectBox domainType = new AonCustomMultiSelectBox("Tipo Dominio");
	private AonCustomListBox category = new AonCustomListBox("Categor\u00eda");
	private AonCustomMultiSelectBox tags = new AonCustomMultiSelectBox("Etiquetas");
	private AonCustomCheckBox webhook = new AonCustomCheckBox("CRM (Webhook)");
	private AonCustomTextBox webhookProductId = new AonCustomTextBox("ID Servicio");
	
	private AonCustomCheckBox composite = new AonCustomCheckBox("Compuesto");
	private AonCustomIntegerBox trial = new AonCustomIntegerBox("D\u00edas Prueba"); // TRIAL_LIMIT_DAYS
	private AonCustomDateBox trialLimit = new AonCustomDateBox("Fecha L\u00edmite Prueba"); // TRIAL_LIMIT_DATE
	private AonCustomCheckBox trialOverflow = new AonCustomCheckBox("Bloq. Tras Prueba");  // TRIAL_LIMIT_LOCK_OVERFLOW
	private AonCustomCheckBox noBooking = new AonCustomCheckBox("No Contratable");
	
	private AonCustomIntegerBox posititon = new AonCustomIntegerBox("Posici\u00f3n");
	private AonCustomListBox priceType = new AonCustomListBox("T. Precio");
	
	private AonCustomListBox projectType = new AonCustomListBox("T. Expediente");
	private AonCustomListBox workgroup = new AonCustomListBox("G. Trabajo");
	private AonCustomListBox taskHolder = new AonCustomListBox("Operario");
	
	private AonCustomNumberBox price = new AonCustomNumberBox("Precio");
	private AonCustomListBox iva = new AonCustomListBox("IVA");
	private AonCustomNumberBox pvp = new AonCustomNumberBox("P.V.P.");
	private AonCustomIntegerBox limit = new AonCustomIntegerBox("L\u00edmite"); // LIMIT_MAX
	private AonCustomCheckBox limitOverflow = new AonCustomCheckBox("Bloq. Tras L\u00edmite"); // LIMIT_LOCK_OVERFLOW
	
	private FlowPanel tariffRow = new FlowPanel();
	
	private AonCustomCard tariffCard = new AonCustomCard("Tarifas");
	private SimpleLayoutPanel tariffCenterPanelCard;
	private ItemTariffTable itemTariffTable;
	
	private AonCustomCard compositeCard = new AonCustomCard("Pack Productos");
	private ProductCompositionSelect productComposition;
	private SimpleLayoutPanel compositeCenterPanelCard = new SimpleLayoutPanel();;
	private CompositeItemTable compositeItemTable;
	
	private RegistryModuleOptions options;
	
	private ProductBooking product;
	private Item item;
	private List<Tax> taxes;
	private List<ProductCategory> productCategories;
	private List<ItemAddInfo> itemAddInfo;
	
	// Tags
	private List<Tag> tagList;
	
	private List<ProductBooking> products;
	
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
		
		tablayoutPanel = new TabLayoutPanel(25.00, Unit.PX);
		tablayoutPanel.setHeight("100%");
		tablayoutPanel.setWidth("100%");
		container.add(tablayoutPanel);
		
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
		getProducts(productList -> {
			getProduct(productId, dbProduct -> {
				
				tariffRow.clear();
				if(tariffRow.getWidgetCount() > 1) tariffRow.remove(1);
				
				container.clear();
				container.add(messagePanel);
				
				tablayoutPanel.clear();
				container.add(tablayoutPanel);
				
				ScrollPanel generalDataScroll = new ScrollPanel(createGeneralCard());
				generalDataScroll.setHeight("100%");
				
				tablayoutPanel.add(generalDataScroll, "Datos Generales");
				
				ScrollPanel aditionalScroll = new ScrollPanel(createAditionalCard());
				aditionalScroll.setHeight("100%");
				aditionalScroll.getElement().getStyle().setProperty("margin-top", "1rem");
				
				tablayoutPanel.add(aditionalScroll, "Datos Adicionales");
				
				getTariffs(tariffs -> {
					if(!tariffs.isEmpty()) {
						tariffRow = createRow(createTariffCard(), product.isComposition() ? createCompositeCard() : null);
						tariffRow.getElement().getStyle().setProperty("align-items", "start");
						
						tablayoutPanel.add(tariffRow, "Tarifas");
					}
				});
				
				container.add(tablayoutPanel);
				
				add(container);
				
				Scheduler.get().scheduleDeferred(new Command() {
			        public void execute() {
			        	name.setFocus(true);
			        }
			    });
				
			});
		});
	}

	private AonCustomCard createGeneralCard() {
		FlowPanel table = createFlexColumnPanel();
		
		status = new ProductStatusSelect(product.getStatus());
		
		generalCard = new AonCustomCard("Datos Comerciales", status);
		generalCard.setToolbarWidgetShown();
		generalCard.setHeight("100%");
		generalCard.addStyleName(AON.CSS.aonWidthAll());
		generalCard.getElement().getStyle().setProperty("min-width", "33rem");
		generalCard.add(table);
		
		type.clearItems();
		type.setWidth("20rem");
		type.addItem(ProductBookingType.SERVICE.getDescription(), ProductBookingType.SERVICE.name());
		type.addItem(ProductBookingType.PLAN.getDescription(), ProductBookingType.PLAN.name());
		type.addItem(ProductBookingType.USER.getDescription(), ProductBookingType.USER.name());
		type.addItem(ProductBookingType.CONSULTANCY.getDescription(), ProductBookingType.CONSULTANCY.name());
		type.addItem(ProductBookingType.DEFAULT.getDescription(), ProductBookingType.DEFAULT.name());
		type.addChangeHandler(e -> product.setBookingType(ProductBookingType.safeValueOf(type.getValue())));
		type.setValue(product.getBookingType().name());
		
		code.getTextBox().getElement().getStyle().setProperty("text-transform", "uppercase");
		code.getTextBox().setMaxLength(15);
		code.setValue(product.getCode());
		code.addValueChangeHandler(e -> product.setCode(code.getValue().trim().toUpperCase()));
		
		name.setValue(product.getName());
		name.addValueChangeHandler(e -> {
			product.setName(name.getValue());
			item.setDescription(name.getValue());
		});
		
		table.add(createRow(type, code, name));
		
		posititon.setWidth("3rem");
		posititon.hideNearBy();
		posititon.setValue(product.getPosition());
		posititon.addValueChangeHandler(e -> {
			if(null != posititon.getValue()) {
				
				Optional<ProductBooking> sameProductPos = products.stream().filter(pb -> null != pb.getPosition() && posititon.getValue().equals(pb.getPosition())).findAny();
				if(!sameProductPos.isEmpty())
					AonMessagePanel.showWarning(messagePanel, "No puede haber dos productos con la misma posici\u00f3n. Actualmente " + sameProductPos.get().getCode() + " tiene la posici\u00f3n " + posititon.getValue());
				
				product.setPosition(posititon.getValue());
				
			} else posititon.setValue(null);
				
		});
		
		priceType.clearItems();
		priceType.addItem(ProductBookingPriceType.PVP.getDescription(), Byte.toString(ProductBookingPriceType.PVP.value()));
		priceType.addItem(ProductBookingPriceType.PLAN.getDescription(), Byte.toString(ProductBookingPriceType.PLAN.value()));
		priceType.addItem(ProductBookingPriceType.FROM.getDescription(), Byte.toString(ProductBookingPriceType.FROM.value()));
		priceType.addItem(ProductBookingPriceType.HIDE.getDescription(), Byte.toString(ProductBookingPriceType.HIDE.value()));
		priceType.setValue(Byte.toString(product.getBookingPriceType().value()));
		priceType.addChangeHandler(e -> {
			product.setBookingPriceType(ProductBookingPriceType.safeValueOf(Byte.parseByte(priceType.getValue())));
		});
		
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
		
		table.add(createRow(posititon, priceType, price, iva, pvp));
		
		comercialDescription.setMinHeight("15rem");
		comercialDescription.setValue(product.getDescriptionTemplate());
		comercialDescription.addBlurHandler(e -> product.setDescriptionTemplate(comercialDescription.getValue()));
		table.add(createRow(comercialDescription));
		
		return generalCard;
	}

	private AonCustomCard createAditionalCard() {
		FlowPanel table = createFlexColumnPanel();
		
		console = new ProductConsoleSelect(product.isConsole() ? ProductConsole.CONSOLE : ProductConsole.SELF_CONTRACT);
		console.addBlurHandler(e -> onConsoleChange());
		
		aditionalCard = new AonCustomCard("Datos Contrataci\u00f3n", console);
		aditionalCard.setToolbarWidgetShown();
		aditionalCard.setHeight("100%");
		aditionalCard.addStyleName(AON.CSS.aonWidthAll());
		aditionalCard.getElement().getStyle().setProperty("min-width", "33rem");
		aditionalCard.add(table);
		
		aonApps.setOptions(AonApp.getValues().stream().map(app -> app.getDescription()).collect(Collectors.toSet()));
		aonApps.setSelectedOptions(getSelectedAonApps());
		aonApps.addBlurHandler(e -> createAonApps());
		
		domainType.setOptions(DomainType.getValues().stream().filter(domainType -> domainType != DomainType.ADMIN).map(domainType -> domainType.getName()).collect(Collectors.toSet()));
		domainType.setSelectedOptions(product.getDomainTypes().stream().map(dt -> dt.getName()).collect(Collectors.toSet()));
		domainType.addBlurHandler(e -> onDomainTypeChange());
		
		table.add(createRow(aonApps, domainType));
		
		projectType.clearItems();
		projectType.addItem("-", "");
		projectType.addChangeHandler(e -> {
			workgroup.setVisible(AonStringUtils.isNotBlank(projectType.getValue()));
			taskHolder.setVisible(AonStringUtils.isNotBlank(projectType.getValue()));
			
			product.setProjectType(AonStringUtils.isBlank(projectType.getValue())
					? null
					: new ProjectType().setId(Integer.parseInt(projectType.getValue()))
			);
		});
		
		workgroup.clearItems();
		workgroup.addItem("-", "");
		workgroup.addChangeHandler(e -> {
			if(AonStringUtils.isBlank(workgroup.getValue())) {
				taskHolder.getListBox().setSelectedIndex(0);
				taskHolder.setVisible(false);
			} else {
				taskHolder.setVisible(true);	
				getAviableTaskHolders(Integer.parseInt(workgroup.getValue()), taskHolders -> { 
					taskHolder.clearItems();
					taskHolder.addItem("-", "");
					taskHolders.forEach(taskHolderIt -> taskHolder.addItem(taskHolderIt.getName(), taskHolderIt.getRegistry().toString()));
				});
			}
			
			product.setWorkgroup(AonStringUtils.isBlank(workgroup.getValue()) ? null : new Workgroup().setId(Integer.parseInt(workgroup.getValue())));
		});
		
		taskHolder.clearItems();
		taskHolder.addItem("-", "");
		taskHolder.addChangeHandler(e -> {
			if(AonStringUtils.isBlank(taskHolder.getValue()))
				product.setTaskHolder(null);
			else {
				TaskHolder th = new TaskHolder();
				th.setId(Integer.parseInt(taskHolder.getValue()));
				product.setTaskHolder(th);
			}
		});
		
		getAviableProjectTypes(projectTypes -> {
			projectTypes.forEach(projectTypeIt -> projectType.addItem(projectTypeIt.getDescription(), projectTypeIt.getId().toString()));
			projectType.setValue(null != product.getProjectType() ? product.getProjectType().getId().toString() : null);
			
			workgroup.setVisible(null != product.getProjectType());
			taskHolder.setVisible(null != product.getProjectType());
			
			getAviableWorkgroups(workgroups -> {
				workgroups.forEach(workgroupIt -> workgroup.addItem(workgroupIt.getDescription(), workgroupIt.getId().toString()));
				workgroup.setValue(null != product.getProjectType() && null != product.getWorkgroup() ? product.getWorkgroup().getId().toString() : null);
				
				if(null != product.getWorkgroup()) {	
					getAviableTaskHolders(product.getWorkgroup().getId(), taskHolders -> {
						taskHolder.clearItems();
						taskHolder.addItem("-", ""); 
						taskHolders.forEach(taskHolderIt -> taskHolder.addItem(taskHolderIt.getName(), taskHolderIt.getRegistry().toString()));
						taskHolder.setValue(null != product.getProjectType() && null != product.getTaskHolder() ? product.getTaskHolder().getRegistry().toString() : null);
					});
				} else {
					taskHolder.setVisible(false);
				}	
			});
		});
		
		table.add(createRow(projectType, workgroup, taskHolder));
		
		category.clearItems();
		category.addItem("-", "");
		getProductCategories(productCategories -> {
			productCategories.forEach(productCategory -> category.addItem(productCategory.getName(), productCategory.getId().toString()));
			category.setValue(null == product.getCategory() || null == product.getCategory().getId() ? "" : product.getCategory().getId().toString());
		});
		category.addChangeHandler(e -> product.setCategory(AonStringUtils.isBlank(category.getValue()) ? null : productCategories.stream().filter(pc -> pc.getId().equals(Integer.parseInt(category.getValue()))).findFirst().get()));
		
		// TAGS
		tags.getElement().getStyle().setProperty("max-width", "90%");
		getTags(aviableTags -> {
			tags.setOptions(aviableTags.stream().map(aviableTag -> aviableTag.getName()).collect(Collectors.toSet()));
			
			getProductTags(product.getId(), productTags -> {
				Set<String> selectedProductTags = productTags.stream().map(productTag -> productTag.getTag().getName()).collect(Collectors.toSet());
				tags.setSelectedOptions(selectedProductTags);
			});
		});
		
		webhook.setWidth("17rem");
		webhook.setValue(product.isWebhook());
		webhook.addValueChangeHandler(e -> {
			product.setWebhook(e.getValue());
			webhookProductId.setVisible(product.isWebhook());
			if(!product.isWebhook()) {
				webhookProductId.setValue(null);
				product.setWebhookProductId(null);
				webhook.setWidth("17rem");
			} else webhook.setWidth("21rem");
		});
		
		webhookProductId.setWidth("7rem");
		webhookProductId.setVisible(product.isWebhook());
		webhookProductId.setValue(product.getWebhookProductId());
		webhookProductId.addValueChangeHandler(e -> product.setWebhookProductId(webhookProductId.getValue()));
		
		table.add(createRow(category, tags, webhook, webhookProductId));
		
		// LIMITS
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
		
		limitOverflow.setWidth("12rem");
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
		
		noBooking.setWidth("11rem");
		noBooking.setValue(product.isNoBooking());
		noBooking.addValueChangeHandler(e -> product.setNoBooking(e.getValue()));
		
		table.add(createRow(limitOverflow, limit, noBooking));
		
		// TRIAL
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
		
		trialOverflow.setWidth("23rem");
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
		
		composite = new AonCustomCheckBox("Compuesto");
		composite.setWidth("22rem");
		composite.setValue(product.isComposition());
		composite.addValueChangeHandler(e -> {
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
		
		table.add(createRow(trialOverflow, trial, trialLimit, composite));
		
		return aditionalCard;
	}
	
	private void onDomainTypeChange() {
		List<DomainType> selectedDomainTypes = new ArrayList<DomainType>();
		domainType.getSelectedOptions().forEach(dt -> selectedDomainTypes.add(DomainType.getByName(dt)));
		product.setDomainTypes(selectedDomainTypes);
	}

	private void onConsoleChange() {
		product.setConsole(console.getValue().equals(ProductConsole.CONSOLE));
	}

	private Widget createTariffCard() {
		tariffCard = new AonCustomCard("Tarifas");
		tariffCard.addStyleName(AON.CSS.aonWidthAll());
		tariffCard.getElement().getStyle().setProperty("min-width", "33rem");
		
		tariffCenterPanelCard = new SimpleLayoutPanel();
		itemTariffTable  = new ItemTariffTable(options, product, item) {
			
			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}
		};
		
		tariffCenterPanelCard.getElement().getStyle().setProperty("min-height", "15rem");
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
		compositeCard.getElement().getStyle().setProperty("min-width", "33rem");
		compositeCard.setToolbarWidgetShown();
		
		compositeItemTable  = new CompositeItemTable(options, product, item) {
			
			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}
		};
		
		compositeCenterPanelCard.getElement().getStyle().setProperty("min-height", "15rem");
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
	
	private Set<String> getSelectedAonApps() {
		return product.getAonApps().stream().map(app -> app.getDescription()).collect(Collectors.toSet());
	}

	private Double createPVP() {
		// GENERAL
		return item.getPrice() + (item.getPrice() * getVatPercentage() / 100);
	}

	private double getVatPercentage() {
		Optional<Tax> taxOpt = taxes.stream().filter(tax -> AonStringUtils.equalsIgnoreCase(tax.getId().toString(), iva.getValue())).findFirst();
		return taxOpt.isEmpty() ? 0.00 : taxOpt.get().getPercentage();
	}
	
	private void createAonApps() {
		Set<String> selectedAonApps = aonApps.getSelectedOptions();
		List<AonApp> aonApps = new ArrayList<AonApp>();
		
		selectedAonApps.forEach(selectedAonApp -> aonApps.add(AonApp.getByDescription(selectedAonApp)));
		product.setAonApps(aonApps);
		product.setBookingComposition(aonApps.size() > 1);
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
	
//	private int calcularAlturaRestante(Widget widget) {
//		// Posicin del widget desde el inicio del documento
//	    int posicionWidget = widget.getElement().getAbsoluteTop();
//
//	    // Altura del viewport
//	    int alturaViewport = Window.getClientHeight();
//
//	    // Scroll actual (en caso de que la pgina tenga desplazamiento)
//	    int scrollActual = Window.getScrollTop();
//
//	    // Altura restante
//	    return alturaViewport + scrollActual - posicionWidget - 85;
//	}
	
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
		
		if(AonStringUtils.isBlank(projectType.getValue())) {
			product.setWorkgroup(null);
			product.setTaskHolder(null);
		}
		
		item.setStatus(status.getValue());
		item.setBarcode(null);	
		
		if(product.isWebhook() && AonStringUtils.isBlank(product.getWebhookProductId())) {
			AonMessagePanel.showWarning(messagePanel, "Si la notificaci\u00f3n al CRM esta activo es obligatorio indicar el ID Servicio");
			return;
		}
		
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
				
				commonService.saveProductBooking(options.getDomainName(), options.getDomain(), options.getUser(), product, new AsyncCallback<ProductBooking>() {
					
					@Override
					public void onSuccess(ProductBooking productDB) {
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
	
	private void getProduct(Integer productId, Consumer<ProductBooking> success) {
		commonService.getProductBooking(options.getDomainName(), options.getDomain(), options.getUser(), productId, new AsyncCallback<ProductBooking>() {
			
			@Override
			public void onSuccess(ProductBooking productDb) {
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
	
	private void getAviableProjectTypes(Consumer<List<ProjectType>> success) {
		commonService.getAviableProjectTypes(options.getDomainName(), options.getDomain(), options.getUser(), options.getDomain(), new AsyncCallback<List<ProjectType>>() {
			
			@Override
			public void onSuccess(List<ProjectType> projectTypes) {
				success.accept(projectTypes);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo tipos expedientes: " + caught.getMessage());
			}
		});
	}
	
	private void getAviableWorkgroups(Consumer<List<Workgroup>> success) {
		commonService.getAviableWorkgroups(options.getDomainName(), options.getDomain(), options.getUser(), options.getDomain(), new AsyncCallback<List<Workgroup>>() {
			
			@Override
			public void onSuccess(List<Workgroup> workgroups) {
				success.accept(workgroups);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo workgroups: " + caught.getMessage());
			}
		});
	}
	
	private void getAviableTaskHolders(Integer workgroup, Consumer<List<TaskHolder>> success) {
		commonService.getAviableTaskHolders(options.getDomainName(), options.getDomain(), options.getUser(), workgroup, new AsyncCallback<List<TaskHolder>>() {
			
			@Override
			public void onSuccess(List<TaskHolder> taskHolders) {
				success.accept(taskHolders);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo workgroups: " + caught.getMessage());
			}
		});
	}
	
	private void getProducts(Consumer<List<ProductBooking>> success) {
		ProductParams params = new ProductParams()
				.setDomainName(options.getDomainName())
				.setDomain(options.getDomain())
				.setUser(options.getUser())
				.setOffset(0)
				.setLimit(Integer.MAX_VALUE)
				;
		
		commonService.getProductsBooking(params, new AsyncCallback<List<ProductBooking>>() {
			
			@Override
			public void onSuccess(List<ProductBooking> productsDb) {
				products = productsDb;
				success.accept(products);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error productos: " + caught.getMessage());
			}
		});
	}
	


	private void getTariffs(Consumer<List<Tariff>> success) {
		commonService.getTariffs(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<Tariff>>() {
			
			@Override
			public void onSuccess(List<Tariff> tariffsDb) {
				success.accept(tariffsDb);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo tarifas: " + caught.getMessage());
			}
		});
	}
	
	protected abstract void onBackClick();

}
