package com.esferalia.aon.gwt.fiscal.client.tree;

import java.util.ArrayList;

import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.i18n.CommonMessages;
import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.ResultsPanel;
import com.esferalia.aon.gwt.fiscal.client.FiscalService;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class FiscalTree extends MainEntryPoint {
	static CommonServiceAsync COMMON_SERVICE;
	static FiscalServiceAsync FISCAL_SERVICE;
	
	interface FiscalNodeWidget<T> {
		void select( TreeNode<T> node);
	}
	
	interface FiscalTreeBinder extends UiBinder<Widget, FiscalTree> {
	}

	private static final FiscalTreeBinder BINDER = GWT.create(FiscalTreeBinder.class);
	
	protected final static CommonMessages MSG = GWT.create(CommonMessages.class);
	protected final static AonResources AON_RESOURCES = GWT.create(AonResources.class);
	protected final static GWTResources GWT_RESOURCES = GWT.create(GWTResources.class);
	protected static final NumberFormat FMT = NumberFormat.getFormat(MSG.decimalPattern(),MSG.currencyCode());
	public static final int CURRENT_YEAR = 2015;

	Enterprise enterprise;
	
	@UiField
	SplitLayoutPanel splitLayoutPanel;
	@UiField
	ScrollPanel sidebar;
	@UiField
	Tree tree;
	
	@UiField
	ResultsPanel resultsPanel;
	@UiField
	MinimizePanel footPanel;
	
	@UiField 
	Label subtitle;
	
	@UiField(provided = true)
	SuggestBox enterpriseSuggest;
	
	@UiField
	ScrollPanel content;
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	@Override
	public void onModuleLoad() {
		GWT_RESOURCES.css().ensureInjected();
		AON_RESOURCES.css().ensureInjected();

		FiscalServiceAsync fiscalServiceRaw = GWT.create(FiscalService.class);
		FISCAL_SERVICE = new FiscalServiceAsyncDecorator(fiscalServiceRaw);

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		EnterpriseSuggestOracle oracle = new EnterpriseSuggestOracle();
		enterpriseSuggest = new SuggestBox(oracle);
		enterpriseSuggest.setLimit(20);
		enterpriseSuggest.addStyleName(AON_RESOURCES.css().aonFiscalEnterpriseSuggest());
		
		Widget ui = BINDER.createAndBindUi(this);
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
		
		COMMON_SERVICE.getParentEnterprises(getCurrentDomainName(),getCurrentDomain(),"%"
				,new AsyncCallback<ArrayList<Enterprise>>() {
					@Override
					public void onSuccess(ArrayList<Enterprise> result) {
						if (result == null || result.size() == 0) {
							//TODO manage
							Window.alert("ERROR");
						} else if ( result.size() == 1) {
							enterpriseSuggest.setText(result.get(0).toString());
							enterpriseSuggest.setEnabled(false);
							initialize(result.get(0));
						} else {
							enterpriseSuggest.setText(MSG.startTyping());
							enterpriseSuggest.setEnabled(true);
							enterpriseSuggest.getValueBox().selectAll();
							enterpriseSuggest.setFocus(true);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						DialogMessages.alertErrorWidget(MSG.unableToShowData(caught
								.getMessage()));
					}
				});
		
		
	}																																																																																																																																																																																																																																																																																																																																																													

	private void initialize(Enterprise enterprise) {
		this.enterprise = enterprise;
		subtitle.setText(MSG.enterprise());
		TreeNode.renderTree(tree,this,enterprise,true);
	}
	
	@UiHandler("tree")
	void onTreeSelecction(SelectionEvent<TreeItem> event) {
		TreeNode<?> node = (TreeNode<?>) event.getSelectedItem();
		node.select(this);
		sidebar.scrollToLeft();
	}
	
	// -------------------------------------------------------------- UiHandler
	@UiHandler("footPanel")
	void onFootMinimize(MinimizeEvent event) {
		closeFootPanel();
	}
	@UiHandler("footPanel")
	void onFootMaximize(MinimizeEvent event) {
	}

	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 0);
	}

	private void maximizeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 0);
	}
	
	private void showResultsPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 5);
	}

	private boolean isResultsPanelVisible() {
		return splitLayoutPanel.getWidgetSize(footPanel) > 0;
	}
	
	private void cleanErrorMessage() {
		resultsPanel.clearFlowPanel();
		resultsPanel.setWidget(new SimplePanel());
		closeFootPanel();
	}

	private void showErrorMessage(String msg) {
		showResultsPanel();
		addErrorMessage(msg);
	}

	private void addErrorMessage(String msg) {
		SimplePanel panel = new SimplePanel();
		Label label = new Label(msg);
		label.addStyleName("aon-icon-errorwarning");
		label.addStyleName("aon-message-error");
		label.addStyleName("aon-icon");
		panel.add(label);
		resultsPanel.setWidget(panel);
	}

	@UiHandler("enterpriseSuggest")
	void onSelectEnterprise(SelectionEvent<Suggestion> suggestion) {
		EnterpriseSuggestion sugg = (EnterpriseSuggestion) suggestion.getSelectedItem();
		initialize( sugg.getEnterprise() );
	}
	
	class EnterpriseSuggestOracle extends MultiWordSuggestOracle {

		@Override
		public void requestSuggestions(final Request request,
				final Callback callback) {
			String query = '%' + request.getQuery() + '%';
			   
			COMMON_SERVICE.getParentEnterprises(getCurrentDomainName()
					,getCurrentDomain(),
					query
					,new AsyncCallback<ArrayList<Enterprise>>() {

						public void onFailure(Throwable caught) {
							Window.alert("Error while getting suggestions.");
						}

						public void onSuccess(ArrayList<Enterprise> result) {
							ArrayList<Suggestion> suggestions = new ArrayList<Suggestion>();
							if (result != null) {
								for (final Enterprise enterprise : result) {
									suggestions.add(new EnterpriseSuggestion(enterprise));
								}
							}
							Response resp = new Response(suggestions);
							callback.onSuggestionsReady(request, resp);
						}
					});
		}
	}

	public class EnterpriseSuggestion implements Suggestion {
		private Enterprise enterprise;

		public EnterpriseSuggestion(Enterprise enterprise) {
			this.enterprise = enterprise;
		}

		public Enterprise getEnterprise() {
			return enterprise;
		}

		@Override
		public String getDisplayString() {
			return enterprise.toString();
		}

		@Override
		public String getReplacementString() {
			return enterprise.toString();
		}
	}
	
}

//,MOD111(MSG.mod111(),AON_RESOURCES.css().aonIconM111(),new HTMLPanel("MOD111"),null)
//,MOD115(MSG.mod115(),AON_RESOURCES.css().aonIconM115(),new HTMLPanel("MOD115"),null)
//,MOD123(MSG.mod123(),AON_RESOURCES.css().aonIconM123(),new HTMLPanel("MOD123"),null)
//,MOD130(MSG.mod130(),AON_RESOURCES.css().aonIconM130(),new HTMLPanel("MOD130"),null)
//,MOD131(MSG.mod131(),AON_RESOURCES.css().aonIconM131(),new HTMLPanel("MOD131"),null)
//,MOD140(MSG.mod140(),AON_RESOURCES.css().aonIconM140(),new HTMLPanel("MOD140"),null)
//,MOD180(MSG.mod180(),AON_RESOURCES.css().aonIconM180(),new HTMLPanel("MOD180"),null)
//,MOD184(MSG.mod184(),AON_RESOURCES.css().aonIconM184(),new HTMLPanel("MOD184"),null)
//,MOD190(MSG.mod190(),AON_RESOURCES.css().aonIconM190(),new HTMLPanel("MOD190"),null)
//,MOD193(MSG.mod193(),AON_RESOURCES.css().aonIconM193(),new HTMLPanel("MOD193"),null)
//,MOD200(MSG.mod200(),AON_RESOURCES.css().aonIconM200(),new HTMLPanel("MOD200"),null)
//,MOD303(MSG.mod303(),AON_RESOURCES.css().aonIconM303(),new HTMLPanel("MOD303"),null)
//,MOD340(MSG.mod340(),AON_RESOURCES.css().aonIconM340(),new HTMLPanel("MOD340"),null)
//,MOD347(MSG.mod347(),AON_RESOURCES.css().aonIconM347(),new HTMLPanel("MOD347"),null)
//,MOD349(MSG.mod349(),AON_RESOURCES.css().aonIconM349(),new HTMLPanel("MOD349"),null)
//,MOD390(MSG.mod390(),AON_RESOURCES.css().aonIconM390(),new HTMLPanel("MOD390"),null)

