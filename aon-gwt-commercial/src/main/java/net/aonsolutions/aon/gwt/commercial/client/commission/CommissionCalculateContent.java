package net.aonsolutions.aon.gwt.commercial.client.commission;

import java.util.Date;

import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AonSuggestOracle;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;

public class CommissionCalculateContent extends Composite {

	interface Binder extends UiBinder<Widget, CommissionCalculateContent> {}
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField HorizontalPanel commercialPanel;
	@UiField HorizontalPanel periodPanel;
	@UiField HorizontalPanel seriesPanel;
	@UiField HorizontalPanel numberPanel;
	@UiField HorizontalPanel customerPanel;
	@UiField HorizontalPanel confidentialPanel;
	@UiField HorizontalPanel workplacePanel;
	
	CommissionCalculate parent;
	
	private Integer seller;
	private Integer workplace;
	private String series;
	private Date fromDate;
	private Date toDate;
	private Integer fromNumber;
	private Integer toNumber;
	private Boolean confidential;
	private Integer target;
	
	
	public CommissionCalculateContent(CommissionCalculate parent) {
		this.parent = parent;
		initWidget(binder.createAndBindUi(this));
		buildCommercialPanel();
		buildPeriodPanel();
		buildSeriesPanel();
		buildNumberPanel();
		buildCustomerPanel();
		buildConfidentialPanel();
		buildWorkplacePanel();
	}
	
	private void buildCommercialPanel() {
		parent.getAPI().getRegistry().getSellers(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> r) {
				AonSuggestOracleMap oracle = new AonSuggestOracleMap();
				r.getData().stream().forEach(s -> oracle.add(s.getName(), s.getId()));
				SuggestBox sb = new SuggestBox(oracle);		
				sb.setStyleName(AON.AON_CSS.aonInputText());
				sb.addSelectionHandler(new SelectionHandler<AonSuggestOracle.Suggestion>() {
					
					@Override
					public void onSelection(SelectionEvent<Suggestion> event) {
						Window.alert(oracle.getMap().get(sb.getValue()) + "");
					}
				});
				commercialPanel.add(sb);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				
			}
		});	
	}
	
	private void buildPeriodPanel() {
		Label from = new Label(AON.MSG.from());
		from.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		from.getElement().getStyle().setPaddingRight(10, Unit.PX);
		
		DateBoxEx fromDate = new DateBoxEx();
		fromDate.setStyleName(AON.AON_CSS.aonInputText());
		fromDate.setWidth("75px");
		
		Label to = new Label(AON.MSG.to());
		to.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		to.getElement().getStyle().setPaddingLeft(10, Unit.PX);
		to.getElement().getStyle().setPaddingRight(10, Unit.PX);
		
		DateBoxEx toDate = new DateBoxEx();
		toDate.setStyleName(AON.AON_CSS.aonInputText());
		toDate.setWidth("75px");
		
		periodPanel.add(from);
		periodPanel.add(fromDate);
		periodPanel.add(to);
		periodPanel.add(toDate);
	}

	private void buildSeriesPanel() {
		ListBox lb = new ListBox();
		lb.addItem("-");
		parent.getAPI().getFinance().getInvoiceSeries(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> r) {
				r.getData().stream().forEach(o -> lb.addItem(o.getName(), o.getId()+ ""));
			}
			
			@Override
			public void onFailure(Throwable caught) {
		
			}
		});		
		seriesPanel.add(lb);
	}

	private void buildNumberPanel() {
		Label from = new Label(AON.MSG.from());
		from.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		from.getElement().getStyle().setPaddingRight(10, Unit.PX);
		
		DoubleBox fromNumber = new DoubleBox();
		fromNumber.setStyleName(AON.AON_CSS.aonInputText());
		fromNumber.getElement().getStyle().setPaddingRight(10, Unit.PX);
		fromNumber.setWidth("50px");

		Label to = new Label(AON.MSG.to());
		to.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		to.getElement().getStyle().setPaddingLeft(10, Unit.PX);
		to.getElement().getStyle().setPaddingRight(10, Unit.PX);
		
		DoubleBox toNumber = new DoubleBox();
		toNumber.setStyleName(AON.AON_CSS.aonInputText());
		toNumber.setWidth("50px");
		
		numberPanel.add(from);
		numberPanel.add(fromNumber);
		numberPanel.add(to);
		numberPanel.add(toNumber);
	}

	private void buildCustomerPanel() {
		parent.getAPI().getRegistry().getCustomers(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> r) {
				AonSuggestOracleMap oracle = new AonSuggestOracleMap();
				r.getData().stream().forEach(s -> oracle.add(s.getName(), s.getId()));
				SuggestBox sb = new SuggestBox(oracle);		
				sb.addSelectionHandler(new SelectionHandler<AonSuggestOracle.Suggestion>() {
					
					@Override
					public void onSelection(SelectionEvent<Suggestion> event) {
						Window.alert(oracle.getMap().get(sb.getValue()) + "");
					}
				});
				sb.setStyleName(AON.AON_CSS.aonInputText());
				customerPanel.add(sb);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});	
	}

	private void buildConfidentialPanel() {
		RadioButton yes = new RadioButton("Si","Si");
		RadioButton no = new RadioButton("No", "No");
		
		yes.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				no.setValue(!yes.getValue());
			}
		});
		
		no.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				yes.setValue(!no.getValue());
			}
		});

		confidentialPanel.add(yes);
		confidentialPanel.add(no);
	}

	private void buildWorkplacePanel() {
		ListBox lb = new ListBox();
		lb.addItem("-");
		parent.getAPI().getCommon().getWorkplaces(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> r) {
				r.getData().stream().forEach(o -> lb.addItem(o.getName(), o.getId()+ ""));
			}
			
			@Override
			public void onFailure(Throwable caught) {
		
			}
		});		
		workplacePanel.add(lb);
	}

	public Integer getSeller() {
		return seller;
	}

	public void setSeller(Integer seller) {
		this.seller = seller;
	}

	public Integer getWorkplace() {
		return workplace;
	}

	public void setWorkplace(Integer workplace) {
		this.workplace = workplace;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Integer getFromNumber() {
		return fromNumber;
	}

	public void setFromNumber(Integer fromNumber) {
		this.fromNumber = fromNumber;
	}

	public Integer getToNumber() {
		return toNumber;
	}

	public void setToNumber(Integer toNumber) {
		this.toNumber = toNumber;
	}

	public Boolean getConfidential() {
		return confidential;
	}

	public void setConfidential(Boolean confidential) {
		this.confidential = confidential;
	}

	public Integer getTarget() {
		return target;
	}

	public void setTarget(Integer target) {
		this.target = target;
	}
}
