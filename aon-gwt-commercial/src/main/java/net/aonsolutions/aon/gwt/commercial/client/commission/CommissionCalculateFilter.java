package net.aonsolutions.aon.gwt.commercial.client.commission;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesResources;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.type.OfferType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CommissionCalculateFilter extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, CommissionCalculateFilter> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

	public static final AonGwtIssuesCSS ICSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();

    @UiField VerticalPanel panel;
    @UiField InlineLabel categoryLabel;
    @UiField InlineLabel customerLabel;
    @UiField InlineLabel sellerLabel;
    @UiField InlineLabel workplaceLabel;
    @UiField InlineLabel periodLabel;
    
    private CommissionCalculatePrincipal parent;

    private void onChange(String key, LinkedList<String> value) {
    	parent.getFilterMap().put(key, value);
		parent.gridContent();
	}
    private void onClean(){
    	parent.initializeFilterMap();
    	parent.gridContent();
    }
    
    public CommissionCalculateFilter(CommissionCalculatePrincipal parent) {
    	this.parent = parent;
    	initWidget(binder.createAndBindUi(this));       

    	HorizontalPanel hp = new HorizontalPanel();
    	// -------------------- DATE - FROM _____ TO ______
    	HorizontalPanel datePanel = new HorizontalPanel(); 
    	datePanel.addStyleName(AON.AON_CSS.aonMarginTop());  
    	InlineLabel issueLabel = new InlineLabel("Desde");
    	issueLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
    	issueLabel.setWidth("20px");
		datePanel.add(issueLabel);
		
		
		final DateBoxEx issue = new DateBoxEx();
		issue.getElement().getStyle().setBorderColor("#dedede");
		issue.getElement().getStyle().setHeight(16, Unit.PX);;
		issue.setWidth("70px");
		issue.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(Long.toString(issue.getValue().getTime()));
				onChange("from", list);
			}
		});
		datePanel.add(issue);
		
		InlineLabel deliveryLabel = new InlineLabel("Hasta");
		deliveryLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		deliveryLabel.setWidth("20px");
		datePanel.add(deliveryLabel);

		final DateBoxEx delivery = new DateBoxEx();
		delivery.setWidth("70px");
		delivery.getElement().getStyle().setBorderColor("#dedede");
		delivery.getElement().getStyle().setHeight(16, Unit.PX);;

		delivery.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(Long.toString(delivery.getValue().getTime()));
				onChange("to", list);	
			}
		});
		datePanel.add(delivery);
	
		hp.add(datePanel);
		
		// ------------------ FILTER BUTTONS
		FlowPanel fpanel = new FlowPanel(); 
		fpanel.getElement().getStyle().setMarginTop(1, Unit.EM);

		// SERIES - FROM - TO // OR SELECT ONE ON ONNE

		InlineLabel serieLabel = new InlineLabel("Serie");
		serieLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		serieLabel.setWidth("20px");
		fpanel.add(serieLabel);
		
		TextBox serieTextBox = new TextBox();
		serieTextBox.setStyleName(AON.AON_CSS.aonInputText());
		serieTextBox.getElement().getStyle().setBorderWidth(1, Unit.PX);
		serieTextBox.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		serieTextBox.getElement().getStyle().setBorderColor("#dedede");	
		serieTextBox.setWidth("60px");
		serieTextBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(serieTextBox.getValue());
				CommissionCalculateFilter.this.onChange("series", list);	
			}
		});
		fpanel.add(serieTextBox);
		
		InlineLabel serieFromLabel = new InlineLabel("Desde n\u00BA");
		serieFromLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		serieFromLabel.setWidth("20px");
		fpanel.add(serieFromLabel);
		
		DoubleBox serieFromDoubleBox = new DoubleBox();
		serieFromDoubleBox.setWidth("30px");
		serieFromDoubleBox.getElement().getStyle().setBorderWidth(1, Unit.PX);
		serieFromDoubleBox.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		serieFromDoubleBox.getElement().getStyle().setBorderColor("#dedede");	
		serieFromDoubleBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(serieFromDoubleBox.getValue().toString());
				CommissionCalculateFilter.this.onChange("number_from", list);	
			}
		});
		fpanel.add(serieFromDoubleBox);

		InlineLabel serieToLabel = new InlineLabel("Hasta");
		serieToLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		serieToLabel.setWidth("20px");
		fpanel.add(serieToLabel);

		DoubleBox serieToDoubleBox = new DoubleBox();
		serieToDoubleBox.setWidth("30px");
		serieToDoubleBox.getElement().getStyle().setBorderWidth(1, Unit.PX);
		serieToDoubleBox.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		serieToDoubleBox.getElement().getStyle().setBorderColor("#dedede");	
		serieToDoubleBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(serieToDoubleBox.getValue().toString());
				CommissionCalculateFilter.this.onChange("number_to", list);
			}
		});
		fpanel.add(serieToDoubleBox);
		
		hp.add(fpanel);
		
		panel.add(hp);
		
		HorizontalPanel fpanel2 = new HorizontalPanel(); 
		fpanel2.getElement().getStyle().setMarginTop(1, Unit.EM);
		
		if(parent.isInvoice()) {
			HorizontalPanel customerHP = new HorizontalPanel();
			InlineLabel customerLabel = new InlineLabel("Cliente");
			customerLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
			customerLabel.setWidth("20px");
			customerHP.add(customerLabel);
			
			parent.getAPI().getRegistry().getCustomers(new AsyncCallback<JSON<JsObject>>() {
			
				@Override
				public void onSuccess(JSON<JsObject> r) {
					AonSuggestOracleMap oracle = new AonSuggestOracleMap();
					r.getData().stream().forEach(s -> oracle.add(s.getName(), s.getId()));
					SuggestBox sb = new SuggestBox(oracle);		
					sb.setStyleName(AON.AON_CSS.aonInputText());
					sb.getElement().getStyle().setBorderWidth(1, Unit.PX);
					sb.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
					sb.getElement().getStyle().setBorderColor("#dedede");	
					sb.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
						
						@Override
						public void onSelection(SelectionEvent<Suggestion> event) {
							LinkedList<String> list = new LinkedList<>();
							list.add(oracle.getMap().get(sb.getValue()) + "");
							CommissionCalculateFilter.this.onChange("registry", list);
						}
					});
					customerHP.add(sb);
				}
			
				@Override
				public void onFailure(Throwable caught) {
				
				}
			});	
			fpanel2.add(customerHP);
		}
		
		
		HorizontalPanel sellerHP = new HorizontalPanel();

		InlineLabel sellerLabel = new InlineLabel("Comercial");
		sellerLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		sellerLabel.setWidth("20px");
		sellerHP.add(sellerLabel);
		
		parent.getAPI().getRegistry().getSellers(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> r) {
				AonSuggestOracleMap oracle = new AonSuggestOracleMap();
				r.getData().stream().forEach(s -> oracle.add(s.getName(), s.getId()));
				SuggestBox sb = new SuggestBox(oracle);		
				sb.setStyleName(AON.AON_CSS.aonInputText());
				sb.setStyleName(AON.AON_CSS.aonInputText());
				sb.getElement().getStyle().setBorderWidth(1, Unit.PX);
				sb.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
				sb.getElement().getStyle().setBorderColor("#dedede");	
				sb.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
					
					@Override
					public void onSelection(SelectionEvent<Suggestion> event) {
						LinkedList<String> list = new LinkedList<>();
						list.add(oracle.getMap().get(sb.getValue()) + "");
						CommissionCalculateFilter.this.onChange("seller", list);						
					}
				});

				sellerHP.add(sb);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});	
		fpanel2.add(sellerHP);
		
		if(parent.isOffer()) {
			HorizontalPanel supplierHP = new HorizontalPanel();

			InlineLabel supplierLabel = new InlineLabel("Proveedor");
			supplierLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
			supplierLabel.setWidth("20px");
			supplierHP.add(supplierLabel);
		
			parent.getAPI().getRegistry().getSuppliers(new AsyncCallback<JSON<JsObject>>() {
			
				@Override
				public void onSuccess(JSON<JsObject> r) {
					AonSuggestOracleMap oracle = new AonSuggestOracleMap();
					r.getData().stream().forEach(s -> oracle.add(s.getName(), s.getId()));
					SuggestBox sb = new SuggestBox(oracle);		
					sb.setStyleName(AON.AON_CSS.aonInputText());
					sb.setStyleName(AON.AON_CSS.aonInputText());
					sb.getElement().getStyle().setBorderWidth(1, Unit.PX);
					sb.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
					sb.getElement().getStyle().setBorderColor("#dedede");	
					sb.addValueChangeHandler(new ValueChangeHandler<String>() {
					
						@Override
						public void onValueChange(ValueChangeEvent<String> event) {
							LinkedList<String> list = new LinkedList<>();
							list.add(oracle.getMap().get(sb.getValue()) + "");
							CommissionCalculateFilter.this.onChange("supplier", list);
						}
					});
					supplierHP.add(sb);
				}
			
				@Override public void onFailure(Throwable caught) {}
			});	
			fpanel2.add(supplierHP);
		
			HorizontalPanel targetHP = new HorizontalPanel();
			
			InlineLabel targetLabel = new InlineLabel("Cliente Potencial");
			targetLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
			targetLabel.setWidth("20px");
			targetHP.add(targetLabel);
			
			parent.getAPI().getRegistry().getTargets(new AsyncCallback<JSON<JsObject>>() {
			
				@Override
				public void onSuccess(JSON<JsObject> r) {				
					AonSuggestOracleMap oracle = new AonSuggestOracleMap();
					r.getData().stream().forEach(s -> oracle.add(s.getName(), s.getId()));
					SuggestBox sb = new SuggestBox(oracle);
					sb.setStyleName(AON.AON_CSS.aonInputText());
					sb.setStyleName(AON.AON_CSS.aonInputText());
					sb.getElement().getStyle().setBorderWidth(1, Unit.PX);
					sb.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
					sb.getElement().getStyle().setBorderColor("#dedede");
					sb.addValueChangeHandler(new ValueChangeHandler<String>() {
						
						@Override
						public void onValueChange(ValueChangeEvent<String> event) {
							LinkedList<String> list = new LinkedList<>();
							list.add(oracle.getMap().get(sb.getValue()) + "");
							CommissionCalculateFilter.this.onChange("target", list);
						}	
					});
					targetHP.add(sb);
				}	
				
				@Override public void onFailure(Throwable caught) {}
			});	
			fpanel2.add(targetHP);
		}
		
		// Type	
    	InlineLabel typeLabel = new InlineLabel("Tipo");
    	typeLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
    	typeLabel.setWidth("20px");
    	fpanel2.add(typeLabel);
		
		ListBox typeListBox = new ListBox();
		typeListBox.getElement().getStyle().setHeight(16, Unit.PX);;
		typeListBox.getElement().getStyle().setBorderWidth(1, Unit.PX);
		typeListBox.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		typeListBox.getElement().getStyle().setBorderColor("#dedede");	
		typeListBox.setWidth("70px");
		typeListBox.addItem("-");
		for(Integer i = 0 ; i < OfferType.values().length; i++) {
			typeListBox.addItem(OfferType.values()[i].getDescription(), OfferType.values()[i].ordinal() + "");
		}
		typeListBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(typeListBox.getSelectedValue());
				CommissionCalculateFilter.this.onChange("type", list);
			}
		});
		fpanel2.add(typeListBox);
		
		panel.add(fpanel2);
	}
}
