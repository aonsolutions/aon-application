package com.esferalia.aon.gwt.marketing.client.tag;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.marketing.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;

public class TagModule extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(TagModule.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private static CommonServiceAsync COMMON_SERVICE;

	private TagModuleOptions options;

	private DeckLayoutPanel deckLayoutPanel;
	private TagModulePanel tagModulePanel;

	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		options = new TagModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser(),
				new AsyncCallback<AonConfiguration>() {

					@Override
					public void onSuccess(AonConfiguration config) {
						options.setConfiguration(config);
						moduleLoad();
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error al cargar el module");
						moduleLoad();
					}
				});
	}

	public void moduleLoad() {
		AON.ensureInjected();

		deckLayoutPanel = new DeckLayoutPanel();
		
		TagType tagType = AonStringUtils.isBlank(getTagType()) ? null : TagType.safeValueOf(getTagType());

		tagModulePanel = new TagModulePanel(options, tagType);

		deckLayoutPanel.add(tagModulePanel);
		deckLayoutPanel.showWidget(tagModulePanel);

		options.getParentWidget().add(deckLayoutPanel);
		
		// Remove customer from LS
		removeTagType();
	}

}
