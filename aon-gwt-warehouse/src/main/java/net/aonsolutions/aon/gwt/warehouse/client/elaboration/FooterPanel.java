package net.aonsolutions.aon.gwt.warehouse.client.elaboration;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.warehouse.JsElaboration;
import com.esferalia.aon.gwt.api.client.warehouse.JsSales;
import com.esferalia.aon.gwt.api.client.warehouse.JsSalesDetail;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class FooterPanel extends Composite {

	interface Binder extends UiBinder<Widget, FooterPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	enum FooterTabs {
		PENDING,
		SOURCE,
		COMMENTS,
		REMARKS;
	}

	private API API;
	
	private MainElaboration parent;
	private JsElaboration jsElaboration;
	
	private TextArea commentsTextArea;
	private TextArea remarksTextArea;

	
	@UiField
	MinimizePanel footerPanel;
	@UiField
	TabLayoutPanel tabPanel;

	/* TABS CONTENT */
	@UiField
	ScrollPanel pendingOrderPanel;
	@UiField
	ScrollPanel sourcePanel;
	@UiField
	ScrollPanel commentsPanel;
	@UiField
	ScrollPanel remarksPanel;

		
	public FooterPanel(MainElaboration parent) {
		this(parent, null);
	}
	
	public FooterPanel(MainElaboration parent, JsElaboration jsElaboration) {
		this.parent = parent;
		this.API = parent.getAPI();
		this.jsElaboration = jsElaboration;
		
		initWidget(binder.createAndBindUi(this));
		
		closeFooterPanel();
		
		if(jsElaboration!=null){
			tabPanel.getTabWidget(FooterTabs.PENDING.ordinal()).getParent().setVisible(false);
			loadSourceTab();
			loadCommetsTab();
			loadRemarksTab();
		} else {
			tabPanel.getTabWidget(FooterTabs.PENDING.ordinal()).getParent().setVisible(false);
			tabPanel.getTabWidget(FooterTabs.SOURCE.ordinal()).getParent().setVisible(false); 
			tabPanel.getTabWidget(FooterTabs.COMMENTS.ordinal()).getParent().setVisible(false); 
			tabPanel.getTabWidget(FooterTabs.REMARKS.ordinal()).getParent().setVisible(false); 
			loadPendingOrderTab();
		}
		
		if(isElaborationCLosed(jsElaboration)){
			commentsTextArea.setEnabled(false);
			remarksTextArea.setEnabled(false);
		}
		
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if(FooterTabs.values()[event.getSelectedItem()]==FooterTabs.PENDING){
					openFooterPanel();
				} else if(FooterTabs.values()[event.getSelectedItem()]==FooterTabs.SOURCE){
					openFooterPanel();
				} else if(FooterTabs.values()[event.getSelectedItem()]==FooterTabs.COMMENTS){
					openFooterPanel();
				} else if(FooterTabs.values()[event.getSelectedItem()]==FooterTabs.REMARKS){
					openFooterPanel();
				}
			}
		});
	}
	
	// TODO loadPendingOrderTab
	protected void loadPendingOrderTab() {
		pendingOrderPanel.add(new Label("Disponible pr\u00F3ximamente"));
	}
	
	protected void loadSourceTab(){
		
		if (jsElaboration.getSource()!=null && jsElaboration.getSourceId()!=null) {
			switch (jsElaboration.getSource().getName()) {
			case "SALES":
				API.getWarehouse().getSalesDetail(jsElaboration.getSourceId(), new AsyncCallback<JSON<JsSalesDetail>>() {
					
					@Override
					public void onSuccess(JSON<JsSalesDetail> result) {
						FlowPanel panel = createSourcePanel(result.getOneData());
						sourcePanel.add(panel);
					}
					
					@Override public void onFailure(Throwable caught) {}
				});
				break;
			case "PURCHASE":
				sourcePanel.add(new Label("Origen Compras: " + jsElaboration.getSource() + ". " + jsElaboration.getSourceId()));
				break;
			default:
				sourcePanel.add(new Label("Origen desconocido. "));
				break;
			}
		} else {
			sourcePanel.add(new Label("La elaboraci\u00F3n se ha creado manualmente, no tiene origen. "));
		}
		
	}
	
	protected void loadCommetsTab(){
		commentsTextArea = new TextArea();
		commentsTextArea.setWidth("95%");
		commentsTextArea.setHeight("100px");
		commentsTextArea.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				parent.setJsElaboration(jsElaboration);
			}
		});
		if (jsElaboration.getComments() != null && !"".equals(jsElaboration.getComments().trim())) {
			commentsTextArea.setValue(jsElaboration.getComments());
			openFooterPanel();
			tabPanel.selectTab(FooterTabs.COMMENTS.ordinal());
		}
		commentsPanel.add(commentsTextArea);
	}
	
	protected void loadRemarksTab(){
		remarksTextArea = new TextArea();
		remarksTextArea.setWidth("95%");
		remarksTextArea.setHeight("100px");
		remarksTextArea.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				parent.setJsElaboration(jsElaboration);
			}
		});
		if (jsElaboration.getRemarks() != null && !"".equals(jsElaboration.getRemarks().trim())) {
			remarksTextArea.setValue(jsElaboration.getRemarks());
			openFooterPanel();
			tabPanel.selectTab(FooterTabs.REMARKS.ordinal());
		}
		remarksPanel.add(remarksTextArea);
	}
	
	
	protected FlowPanel createSourcePanel(JsSalesDetail jsDetail){
		final FlowPanel p = new FlowPanel("pre");
		if(jsDetail!=null && jsDetail.getId()!=null){
			JsSales jsSales = jsDetail.getSales();
			if(jsSales!=null && jsSales.getId()!=null){
				final FlowPanel headerPanel = new FlowPanel("pre");
				headerPanel.setStyleName(AON.AON_CSS.aonFixedFont());
				headerPanel.addStyleName(AON.AON_CSS.aonFontMedium());
				Label header = new Label(" SERIE/NUMERO     " //17
						+ "CLIENTE                          " //33
						+ "FECHA CARGA    "  //17
						+ "FECHA ENTREGA    "  //17
						+ "REF. COMPRA      "  //17
						+ "      ");  //10
				header.setStyleName(AON.AON_CSS.aonBold());
				header.addStyleName(AON.AON_CSS.aonMarginTop());
				header.addStyleName(AON.AON_CSS.aonBorderTop());
				header.addStyleName(AON.AON_CSS.aonBorderBottom());
				headerPanel.add(header);
				p.add(headerPanel);
				
				final FlowPanel panel = new FlowPanel("pre");
				panel.setStyleName(AON.AON_CSS.aonFixedFont());
				panel.addStyleName(AON.AON_CSS.aonFontMedium());
				panel.addStyleName(AON.AON_CSS.aonMarginBottom());
				
//				String issueDate = jsSales.getIssueDate() != null ? Utils
//						.formatDate(Utils.parseDate(jsSales.getIssueDate()))
//						: "";
//				String deliveryDate = jsSales.getDeliveryDate() != null ? Utils
//						.formatDate(Utils.parseDate(jsSales
//								.getDeliveryDate())) : "";
				
				String issueDate = jsSales.getIssueDate();
				String deliveryDate = jsSales.getDeliveryDate();

				final InlineLabel acc = new InlineLabel(AonStringUtils.SPACE
						+ AonStringUtils.rightPad("", 9)
						+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
								AonStringUtils.defaultString(jsSales
										.getSeries()
										+ "/"
										+ jsSales.getNumber()), 16), 17)
						+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
								AonStringUtils.defaultString(jsSales
										.getCustomer().getName()), 32), 33)
						+ AonStringUtils.rightPad(
								AonStringUtils.defaultString(issueDate), 17)
						+ AonStringUtils.rightPad(
								AonStringUtils.defaultString(deliveryDate), 17)
						+ AonStringUtils.rightPad(AonStringUtils
								.defaultString(jsSales.getPurchaseReference()),
								17) + AonStringUtils.rightPad("", 17));
				acc.setTitle("");
				acc.setStyleName(AON.AON_CSS.aonBold());

				panel.add(acc);
				p.add(panel);
			}
			
			final FlowPanel headerPanel2 = new FlowPanel("pre");
			headerPanel2.setStyleName(AON.AON_CSS.aonFixedFont());
			headerPanel2.addStyleName(AON.AON_CSS.aonFontMedium());
			Label header2 = new Label(" LINEA     " //10
					+ "PRODUCTO                                " //40
					+ "CANTIDAD       " //15
					+ "PRECIO         " //15
					+ "DESCUENTO      " //15
					+ "IMPORTE        "); //15 
			header2.setStyleName(AON.AON_CSS.aonBold());
			header2.addStyleName(AON.AON_CSS.aonMarginTop());
			header2.addStyleName(AON.AON_CSS.aonBorderTop());
			header2.addStyleName(AON.AON_CSS.aonBorderBottom());
			headerPanel2.add(header2);
			p.add(headerPanel2);
			
			
			FlowPanel center = new FlowPanel();
			FlowPanel line = new FlowPanel("pre");
			
			line.setStyleName(AON.AON_CSS.aonFixedFont());
			line.addStyleName(AON.AON_CSS.aonFontMedium());
			line.addStyleName(AON.AON_CSS.aonMarginBottom());
			
			Double discount = 1.0;
			discount = jsDetail.getDiscountExpr() != null 
					? Double.parseDouble(jsDetail.getDiscountExpr()) 
					: 1.0;
			Double amount = jsDetail.getPrice() * jsDetail.getQuantity() * (1 - (discount/100));
			InlineLabel d = new InlineLabel(AonStringUtils.SPACE
					+ AonStringUtils.rightPad("",3)
					+ AonStringUtils.rightPad(AonStringUtils.defaultString(jsDetail.getLine() + ""),11)
					+ AonStringUtils.rightPad(AonStringUtils.abbreviate(
							AonStringUtils.defaultString(jsDetail.getDescription()), 39), 44)
							+ AonStringUtils.rightPad(AonStringUtils.defaultString(jsDetail.getQuantity() + ""), 16)
							+ AonStringUtils.rightPad(AonStringUtils.defaultString(jsDetail.getPrice() + ""), 16)
							+ AonStringUtils.rightPad(AonStringUtils.defaultString(jsDetail.getDiscountExpr()), 16)
							+ AonStringUtils.rightPad(AonStringUtils.defaultString( amount + ""), 15)	
					);
			d.setTitle("");
			d.setStyleName(AON.AON_CSS.aonBold());
			
			line.add(d);
			center.add(line);
			p.add(center);
		} else {
			p.add(new Label(" Pedido no encontrado."));
		}
		
		return p;
	}
	
	public String getCommentsValue(){
		return commentsTextArea.getValue();
	}

	public String getRemarksValue(){
		return remarksTextArea.getValue();
	}
	
	protected boolean isElaborationCLosed(JsElaboration elaboration){
		return elaboration!=null && elaboration.getStatus().getId()==new Integer(ElaborationStatus.CLOSED.ordinal());
	}
	
	@UiHandler("footerPanel")
	void onFooterMinimize(MinimizeEvent event) {
		closeFooterPanel();
	}

	@UiHandler("footerPanel")
	void onFooterMaximize(MaximizeEvent event) {
		openFooterPanel();
	}

	public void selectTab(int index){
		tabPanel.selectTab(index);
	}
	
	public void openFooterPanel() {
		Integer clientHeight = Window.getClientHeight();
		parent.changeSouthContentSize(clientHeight.doubleValue() / 3);
	}

	public void closeFooterPanel() {
		parent.changeSouthContentSize(30.0);
	}
	
	public void hideFooterPanel() {
		parent.changeSouthContentSize(0.0);
	}
	
}