package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.InvestAssetRegime;
import com.esferalia.aon.occam.api.model.InvestAssetType;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public class AonInvestAssetPanel extends SimplePanel {
	
	public static interface AonInvestAssetPanelCallback {
		void onAccept();
		void onCancel();
	}

	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}

	private TextBox description = new TextBox();
	private ListBox activity = new ListBox();
	private ListBox type = new ListBox();
	private ListBox regimen = new ListBox();
	private DoubleBox iva = new DoubleBox();
	private DoubleBox retention = new DoubleBox();
	private AonDateBox startDate = new AonDateBox();
	private AonDateBox endDate = new AonDateBox();
	
	public AonInvestAssetPanel(final String domainName,final int domain, final String user,final AonInvestAssetPanelCallback callback) {
		initializeCommonService();
		show(domainName,domain, user,new InvestAsset(),callback);
	}
	
	public AonInvestAssetPanel(final String domainName,final int domain,final String user, Integer id, final AonInvestAssetPanelCallback callback) {
		initializeCommonService();
		commonService.getInvestAsset(domainName, domain, user, id, new AsyncCallback<InvestAsset>() {
			
			@Override
			public void onSuccess(InvestAsset result) {
				if (result == null) result = new InvestAsset();
				show(domainName,domain, user,result,callback);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				show(domainName,domain, user,new InvestAsset(),callback);
			}
		});
	}
	
	public void show(final String domainName,final int domain, final String user,final InvestAsset investAsset, final AonInvestAssetPanelCallback callback) {
		setWidth("500px");
		setHeight("240px");
		
		FlowPanel rootPanel = new FlowPanel();
		
		final AonErrorPanel errorPanel = new AonErrorPanel();
		errorPanel.addStyleName(AON.CSS.aonMarginTop());
		rootPanel.add(errorPanel);
		
		FlowPanel tablePanel = new FlowPanel();
		tablePanel.setStyleName(AON.CSS.aonScrollArea());
		
		KeyUpHandler keyUpHandler = new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					callback.onCancel();	
				}
			}
		};

		FlexTable table = new FlexTable();
		table.setStyleName(AON.CSS.aonTable());
		table.addStyleName(AON.CSS.aonWidthAll());
	
		table.setWidget(0,0,new InlineLabel(AON.MSG.description()));
		table.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTableLabel());
		description.setValue(investAsset.getDescription());
		description.setMaxLength(128);
		description.setStyleName(AON.CSS.aonInputText());
		table.setWidget(0,1,description);
		
		table.setWidget(1,0,new InlineLabel(AON.MSG.activity()));
		table.getCellFormatter().setStyleName(1, 0, AON.CSS.aonTableLabel());
		activity.addItem( "-", "");
		getActivities(domainName, domain, user, activities -> activities.forEach(activityIt -> activity.addItem( activityIt.getDescription(), activityIt.getId().toString())));
		setSelectedValueLB(activity, null != investAsset.getActivity() && null != investAsset.getActivity().getId() ? investAsset.getActivity().getId().toString() : null);
		activity.setStyleName(AON.CSS.aonInputText());
		table.setWidget(1,1,activity);
		
		table.setWidget(2,0,new InlineLabel(AON.MSG.type()));
		table.getCellFormatter().setStyleName(2, 0, AON.CSS.aonTableLabel());
		for(int i=0; i < InvestAssetType.values().length; i++)
			type.addItem(InvestAssetType.values()[i].description(), i + "");
		type.setStyleName(AON.CSS.aonInputText());
		type.setStyleName(AON.CSS.aonInputText());
		setSelectedValueLB(type, null != investAsset.getType() ? investAsset.getType().ordinal() + "" : null);
		table.setWidget(2,1,type);
		
		table.setWidget(3,0,new InlineLabel("Regimen"));
		table.getCellFormatter().setStyleName(3, 0, AON.CSS.aonTableLabel());
		for(int i=0; i < InvestAssetRegime.values().length; i++)
			regimen.addItem(InvestAssetRegime.values()[i].description(), i + "");
		regimen.setStyleName(AON.CSS.aonInputText());
		setSelectedValueLB(type, null != investAsset.getRegime() ? investAsset.getRegime().ordinal() + "" : null);
		table.setWidget(3,1,regimen);
		
		table.setWidget(4,0,new InlineLabel("% IVA"));
		table.getCellFormatter().setStyleName(4, 0, AON.CSS.aonTableLabel());
		iva.setValue(investAsset.getVatPercent());
		iva.setStyleName(AON.CSS.aonInputText());
		table.setWidget(4,1,iva);
		
		table.setWidget(5,0,new InlineLabel("% Imp. Directa"));
		table.getCellFormatter().setStyleName(5, 0, AON.CSS.aonTableLabel());
		retention.setValue(investAsset.getRetentionPercent());
		retention.setStyleName(AON.CSS.aonInputText());
		table.setWidget(5,1,retention);
		
		table.setWidget(6,0,new InlineLabel("F. Inicio"));
		table.getCellFormatter().setStyleName(6, 0, AON.CSS.aonTableLabel());
		startDate.setValue(investAsset.getStartDate());
		startDate.setStyleName(AON.CSS.aonInputText());
		table.setWidget(6,1,startDate);
		
		table.setWidget(7,0,new InlineLabel("F. Fin"));
		table.getCellFormatter().setStyleName(7, 0, AON.CSS.aonTableLabel());
		endDate.setValue(investAsset.getEndDate());
		endDate.setStyleName(AON.CSS.aonInputText());
		table.setWidget(7,1,endDate);
		
		tablePanel.add( table );
		rootPanel.add( tablePanel );
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	
    	final Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addKeyUpHandler( keyUpHandler);
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				
				investAsset.setDescription(description.getValue());
				investAsset.setActivity(activity.getSelectedIndex() == 0 ? null : new EnterpriseActivity().setId(Integer.parseInt(activity.getSelectedValue())));
				investAsset.setType(InvestAssetType.safeValueOf(Integer.parseInt(type.getSelectedValue())));
				investAsset.setRegime(InvestAssetRegime.safeValueOf(Integer.parseInt(regimen.getSelectedValue())));
				investAsset.setVatPercent(iva.getValue());
				investAsset.setRetentionPercent(retention.getValue());
				investAsset.setStartDate(startDate.getValue());
				investAsset.setEndDate(endDate.getValue());
				
				if (investAsset.getId() == null) {
					investAsset.setDomain(domain);
					investAsset.setType(InvestAssetType.safeValueOf(Integer.parseInt(type.getSelectedValue())));
					investAsset.setRegime(InvestAssetRegime.safeValueOf(Integer.parseInt(regimen.getSelectedValue())));
				}
				
				commonService.saveInvestAsset(domainName, domain, user, investAsset, new AsyncCallback<InvestAsset>() {

					@Override
					public void onSuccess(InvestAsset result) {
						callback.onAccept();
					}
					@Override
					public void onFailure(Throwable caught) {
						errorPanel.showError(caught.getMessage());
						okButton.setEnabled(true);
					}
				});
			}
		});
    	
    	buttons.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addKeyUpHandler( keyUpHandler);
    	cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cancelButton.setEnabled(false);
				callback.onCancel();
			}
		});
    	buttons.add(cancelButton);
    	rootPanel.add(buttons);
		setWidget(rootPanel);
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	description.setFocus(true);
	        }
	    });		
		
	}

	private void setSelectedValueLB(ListBox lBox, String str) {
	    String text = str;
	    int indexToFind = 0;
	    for (int i = 0; i < lBox.getItemCount(); i++) {
	        if (lBox.getValue(i).equals(text)) {
	            indexToFind = i;
	            break;
	        }
	    }
	    lBox.setSelectedIndex(indexToFind);
	}
	
	private void getActivities(String domainName, Integer domain, String user, Consumer<List<Activity>> success) {
		commonService.getActivities(domainName, domain, user, new AsyncCallback<List<Activity>>() {
			
			@Override
			public void onSuccess(List<Activity> activities) {
				success.accept(activities);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// Error
			}
		});
	}

}
