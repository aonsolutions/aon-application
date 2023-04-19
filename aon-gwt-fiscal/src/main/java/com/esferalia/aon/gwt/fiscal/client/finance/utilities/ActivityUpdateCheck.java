package com.esferalia.aon.gwt.fiscal.client.finance.utilities;

import java.util.function.Supplier;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid.AonDisplayGridRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi.AonInvoiceViewer;
import com.esferalia.aon.gwt.fiscal.client.invoice.InvoiceFilterPanel;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.utilities.FinanceUtilitiesResult;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

class ActivityUpdateCheck extends OptionBase {

	private static final String REPORT_URL = URL.encode(GWT.getModuleBaseURL() + "roms/InvoiceStreamServlet");
	
	private AonLayoutPanel dockPanel;
	private SimpleLayoutPanel content;
	private ScrollPanel container;
	
	private InvoiceFilterPanel<FinanceUtilitiesModuleOptions> filterPanel;
	
	protected ActivityUpdateCheck(final FinanceUtilitiesModuleOptions options, Domain domain) {
		super(options, domain);
		dockPanel = new  AonLayoutPanel(Unit.PX);
		setContent(dockPanel);
		
		FinanceUtilitiesModule.COMMON_SERVICE.getAonConfiguration(options.getOccam(), new AsyncCallback<AonConfiguration>() {
			@Override
			public void onSuccess(AonConfiguration config) {
				options.setConfiguration(config);
				filterPanel = new InvoiceFilterPanel<>(options);
				filterPanel.addValueChangeHandler(e -> run(e.getValue()) );
				SimpleLayoutPanel filterPanelContainer = new SimpleLayoutPanel(  );
				filterPanelContainer.setWidget(filterPanel);
				dockPanel.addNorth( filterPanelContainer, 115);
				
				content = new SimpleLayoutPanel();
				content.setStyleName(AON.CSS.aonBorderTop());
				
				container = new ScrollPanel();
				container.setStyleName(AON.CSS.aonScrollArea());
				content.add(container);
				dockPanel.add(content);
			}
			
			@Override 
			public void onFailure(Throwable caught) {
				dockPanel.showErrorPanel(AON.MSG.loadError( getOptionDescription() ));
			}
		});
		
	}
	
	@Override
	public String getOptionDescription() {
		return AonStringUtils.BULLET + " Modificaci\u00F3n masiva de actividad en facturas.";
	}

	@Override
	protected AonToolbar getToolbarPanel() {
		AonToolbar toolbarPanel = super.getToolbarPanel();
		final AonToolbarButton refresh = new AonToolbarButton(AON.MSG.refresh(), AON.CSS.aonIconSearch());
		refresh.addClickHandler(event -> run( filterPanel.getParams(getOptions())) );
		toolbarPanel.add(refresh);

		return toolbarPanel;
	}

	public void run( AccountingReportParams params ) {
		content.clear();
		filterPanel.setValue( params );
		content.setWidget(new ActivityTypeCheckPanel( getOptions(), params));		

	}

	class ActivityTypeCheckPanel extends ScrollPanel{
		
		ActivityTypeCheckPanel(FinanceUtilitiesModuleOptions options, AccountingReportParams params) {
			setStyleName(AON.CSS.aonScrollArea());
			JsInvoiceGridPanel grid = new JsInvoiceGridPanel();
			grid.addSelectionHandler(event -> showInvoice(options, event.getSelectedItem()));
			setWidget(grid);
			XMLHttpRequest xhr = XMLHttpRequest.create();
			xhr.open(FormPanel.METHOD_POST, REPORT_URL);
			xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
			xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
				
				private int loaded = 0;
				
				@Override
				public void onReadyStateChange(XMLHttpRequest xhr) {
					int state = xhr.getReadyState();
					if (state == XMLHttpRequest.LOADING || state == XMLHttpRequest.DONE) {
						String text = xhr.getResponseText();
						try {
							for (JsInvoiceMinimal invoice = read(text); text != null; invoice = read(text)) {
								grid.addRow(invoice);
							}
						} catch (IndexOutOfBoundsException e) {
							// Nothing
						}
					}
				}

				private JsInvoiceMinimal read(String text) {
					for (int begin = loaded; begin < text.length(); begin++) {
						if (text.charAt(begin) == '{') {
							loaded = findEnd(text, begin + 1) + 1;
							String json = text.substring(begin, loaded);
							return JsonUtils.safeEval(json);
						}
					}
					throw new IndexOutOfBoundsException();
				}

				private int findEnd(String text, int start) {
					for (int end = start; end < text.length(); end++) {
						if (text.charAt(end) == '}') {
							return end;
						} else if (text.charAt(end) == '{') {
							end = findEnd(text, end + 1);
						}
					}
					throw new IndexOutOfBoundsException();
				}
			});
			StringBuilder requestData = new StringBuilder();
			requestData.append("&"+IRequestParamsNames.DOMAIN_NAME+"=" + options.getDomainName()  );
			requestData.append("&"+IRequestParamsNames.DOMAIN_ID+"=" + options.getDomain() );
			requestData.append("&"+IRequestParamsNames.USER+"=" + options.getUser() );
			requestData.append("&"+IRequestParamsNames.ACCOUNT_REPORT_PARAMS+"=" + JsonParams.convert( params ));
			requestData.append("&"+IRequestParamsNames.OFFSET+"=0");
			requestData.append("&"+IRequestParamsNames.LIMIT+"=100");
			xhr.send(requestData.toString());

		}

		private void showInvoice(FinanceUtilitiesModuleOptions options, JsInvoiceMinimal br) {
			int invoiceId = br.getId();
			FinanceUtilitiesModule.SERVICE.getInvoice(options.getOccam(), invoiceId,new AsyncCallback<Invoice>() {
				@Override
				public void onSuccess(Invoice inv) {
					AonCustomPopup dialog = new AonCustomPopup();
					dialog.setWidth((Window.getClientWidth() - 100) + "px");
					dialog.setHeight((Window.getClientHeight() - 100) + "px");
					dialog.setAnimationEnabled(true);
					dialog.setGlassEnabled(true);
					dialog.setModal(true);
					dialog.setCaption(AON.MSG.invoice());
					dialog.add(new AonInvoiceViewer(inv));
					dialog.center();
					dialog.show();
				}

				@Override
				public void onFailure(Throwable caught) {
					Window.alert( AON.MSG.loadError("Error inesperado: " + caught.getMessage()));
				}
			});
		}
	}

	@Override
	protected Widget paintResults(FinanceUtilitiesResult result) {
		return null;
	}
	
	class JsInvoiceGridPanel extends FlowPanel implements HasSelectionHandlers<JsInvoiceMinimal>{
		
		private final Label title;
		private final Label subTitle;
		private final AonDisplayGrid grid;
		
		JsInvoiceGridPanel() {
			title = new Label();
			title.setStyleName(AON.CSS.aonMarginTop());
			title.addStyleName(AON.CSS.aonBold());
			title.addStyleName(AON.CSS.aonWidthAll());
			title.addStyleName(AON.CSS.aonTextCenter());
			title.addStyleName(AON.CSS.aonTextUppercase());
			title.addStyleName(AON.CSS.aonFontLarger());
			add( title );
			subTitle = new Label();	
			subTitle.setStyleName(AON.CSS.aonMarginTop());
			subTitle.addStyleName(AON.CSS.aonBold());
			subTitle.addStyleName(AON.CSS.aonWidthAll());
			subTitle.addStyleName(AON.CSS.aonTextCenter());
			subTitle.addStyleName(AON.CSS.aonFontLarger());
			add( subTitle );
			grid = new AonDisplayGrid();
			grid.addStyleName(AON.CSS.aonMarginTop());
			grid.addStyleName(AON.CSS.aonBlockCenter());
			paintHeader();
			add( grid );
		}
		
		@Override
		public void setTitle( String title) {
			super.setTitle(title);
			this.title.setText(title);	
		}
		public void setSubTitle( String subTitle) {
			this.subTitle.setText(subTitle);	
		}
		
		private void paintHeader() {
			grid.addHeaderRow()
				.addCell(new Label(""),AON.CSS.aonWidth20())
				.addCell(new Label("Tipo"),AON.CSS.aonWidth40())
				.addCell(new Label("Actividiad."),AON.CSS.aonWidth300(), AON.CSS.aonNowrap())
				.addCell(new Label("N\u00BA Documento"),AON.CSS.aonWidth100(), AON.CSS.aonNowrap())
				.addCell(new Label("Doc.Tit."),AON.CSS.aonWidthAuto(), AON.CSS.aonNowrap())
				.addCell(new Label("Nombre/raz\u00F3n social"),AON.CSS.aonWidthAuto())
				.addCell(new Label("Fec. Fac."),AON.CSS.aonWidth80(), AON.CSS.aonNowrap())
				.addCell(new Label("Total"),AON.CSS.aonTextRight(),AON.CSS.aonWidth80(), AON.CSS.aonNowrap())		
				.addCell(new Label("N\u00BA Referencia"),AON.CSS.aonWidth100(), AON.CSS.aonNowrap())
			;
		}

		private String ensure(Object nullable, Supplier<String>  supplier) {
			return ensure(nullable, supplier, "---");
		}
		private <T> T ensure(Object nullable, Supplier<T>  supplier, T defaultValue) {
			return (nullable == null) 
				? defaultValue
				: supplier.get();
		}
		
		
		public final native boolean hasInvoiceId(int invoice ) /*-{
			return !isNaN(invoice) && invoice != 0;
		}-*/;
		
		public void addRow(JsInvoiceMinimal inv) {
			InvoiceType invoiceType = InvoiceType.safeValueOf(inv.getInvoiceType());
			
			AonTableButton selectButton = new AonTableButton(AON.MSG.viewInvoice(), AON.CSS.aonIconSearch());
			selectButton.addClickHandler(event -> SelectionEvent.fire(this, inv));
			
			FlowPanel activityPanel = new FlowPanel(); 
			ListBox activityBox = new ListBox();
			final InlineLabel msg = new InlineLabel("");
			msg.setStyleName(AON.CSS.aonTabIcon());
			msg.addStyleName(AON.CSS.aonMarginLeft());
			activityPanel.add(activityBox);
			activityPanel.add(msg);
			
			activityBox.addStyleName(AON.CSS.aonMarginLeft());
			activityBox.setWidth("290px");
			activityBox.addItem("Ninguna", "");
			activityBox.setSelectedIndex(0);
			getOptions().getConfiguration().getAllActivities()
				.stream()
				.forEach(ea -> activityBox.addItem(
					(ea.getIae().isEmpty()?"":("["+ea.getEpigraph()+"]")) 
					+ (ea.isPrincipal()?"* ":" ") 
					+ ea.getDescription()
				, AonNumberUtils.toString( ea.getId())));
			setActivityBoxValue(getOptions(),activityBox,inv.getActivity() );
			activityBox.addChangeHandler(e -> {
				int idx = activityBox.getSelectedIndex();
				Integer activityId = null;
				if (idx>0) {
					activityId = getOptions().getConfiguration().getAllActivities().get(idx-1).getId();
				}
				FinanceUtilitiesModule.SERVICE.updateActivity(getOptions().getOccam(), inv.getId() , activityId, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						setActivityBoxValue(getOptions(),activityBox,inv.getActivity() );
						AonMessageDialog.error("Modificación no realizada. ["+ caught.getMessage() +"]");
					}

					@Override
					public void onSuccess(Void result) {
						msg.addStyleName(AON.CSS.aonIconValid());
					}
				});
			});
			
			AonDisplayGridRow row = grid.addRow();
			row
				.addCell(selectButton)
				.addCell(new Label(ensure(invoiceType,invoiceType::getAbbrDescription)))
				.addCell(activityPanel)
				.addCell(new Label(ensure(inv.getDocumentNumber(), inv::getDocumentNumber, AonStringUtils.EMPTY)))
				.addCell(new Label(ensure(inv.getRegistryDocument(), inv::getRegistryDocument, AonStringUtils.EMPTY)))
				.addCell(new Label(ensure(inv.getRegistryName(), () -> AonStringUtils.abbreviate(inv.getRegistryName(),25), AonStringUtils.EMPTY)))
				.addCell(new Label(ensure(inv.getIssueDate(), () -> AON.DATE_FORMAT.format(inv.getIssueDate()), AonStringUtils.EMPTY)))
				.addCell(new AonDoubleLabel(inv.getTotal()),AON.CSS.aonTextRight())
				.addCell(new Label(ensure(inv.getReferenceCode(), inv::getReferenceCode, AonStringUtils.EMPTY)))
			;
		}

		private void setActivityBoxValue(FinanceUtilitiesModuleOptions financeUtilitiesModuleOptions, ListBox activityBox, int activityId) {
			int i = 1;
			for (EnterpriseActivity ea : getOptions().getConfiguration().getAllActivities()) {
				if (AonNumberUtils.equals(activityId,ea.getId())) {
					activityBox.setSelectedIndex(i);
				}
				i++;
			}
		}

		public void setReportTitle( String title) {
			this.title.setText(title);
		}

		@Override
		public HandlerRegistration addSelectionHandler(SelectionHandler<JsInvoiceMinimal> handler) {
			return super.addHandler(handler, SelectionEvent.getType());
		}
	}
	
}
