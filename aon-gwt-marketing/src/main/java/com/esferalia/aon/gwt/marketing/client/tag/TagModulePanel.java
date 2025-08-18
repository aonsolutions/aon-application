package com.esferalia.aon.gwt.marketing.client.tag;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTagPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTagPanel.AonTagPanelCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.tag.TagParams;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;


public class TagModulePanel extends AonCustomDockLayout {
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private SimplePanel centerPanel;
	
	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	private TagModuleOptions options;
	private TagType tagType;
	
	private TagPanel tagPanel;
	
	private static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	public TagModulePanel(TagModuleOptions options, TagType tagType) {
		super("Estados C. Bloqueados");
		
		initializeCommonService();
		
		this.options = options;
		this.tagType = tagType;
		
		addButtonsToolbar();
		
		setSearchPlaceholder("Busqueda por descripci\u00f3n...");
		
		addKeyUpHandler(e -> {
			String value = getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 2) {
				onSearch();
			} else if(AonStringUtils.isBlank(value)) {
				onSearch();
			}
		});
		
		sort.addItem("Descripci\u00f3n", "description");
		sort.getListBox().addChangeHandler(event -> onSearch());
		
		asc.addItem("Ascendente", "true");
		asc.addItem("Descendete", "false");
		asc.getListBox().addChangeHandler(event -> onSearch());
		
		addSortWidget(sort);
		addSortWidget(asc);
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(messagePanel);
	
		centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight("100%");
		centerPanel.getElement().getStyle().setProperty("margin-left", "1rem");
		
		container.add(centerPanel);
		
		add(container);
		onSearch();
	}

	@Override
	protected void onClearFilter() {
		tagPanel.resetSearchOffset();
		getSearchTextBox().setValue(null, false);
		onSearch();
	}

	private void addButtonsToolbar() {
		AonToolbarButton newButton = new AonToolbarButton( "Nuevo Estado", AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> showTagDialog());
		
		addToolbarButton(newButton);
	}

	private void showTagDialog() {
		new AonTagPanel( options.getDomainName(), options.getDomain(), options.getUser(), this.tagType,  new AonTagPanelCallback() {
				
				@Override
				public void onCancel() {}
				
				@Override
				public void onAccept(Tag tagDB) {
					onSearch();
				}
		});
	}

	public void onSearch() {
		TagParams params = getWidgetParams();
		centerPanel.clear();
		tagPanel = new TagPanel(params) {

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}

			@Override
			protected void onShowSuccessMessage(String successMessage) {
				AonMessagePanel.showSuccess(messagePanel, successMessage);
			}

			@Override
			protected void onShowLoadingMessage(String loadingMessage) {
				AonMessagePanel.showLoading(messagePanel, loadingMessage);
			}
		
		};
		
		centerPanel.setWidget(tagPanel);
	}

	public TagParams getWidgetParams() {
		TagParams params = new TagParams()
			.setDomainName(options.getDomainName())
			.setDomain(options.getDomain())
			.setUser(options.getUser())
			.setDescription(getSearchTextBox().getValue())
			.setTagType(tagType)
			.setOrderBy(sort.getValue())
			.setAsc(Boolean.parseBoolean(asc.getValue()))
			;
		
		return params;
	}
	
	public void showSuccess(String message) {
		AonMessagePanel.showSuccess(messagePanel, message);
	}

}
