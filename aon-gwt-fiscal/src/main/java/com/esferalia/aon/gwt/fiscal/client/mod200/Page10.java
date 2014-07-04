package com.esferalia.aon.gwt.fiscal.client.mod200;

import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.MSG;
import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.RESOURCES;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200ConstantsBehaviour.BEHAVIOUR_KEYS_MAP;

import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.fiscal.shared.mod200.IMod200KeysProvider;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BN082Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BN565Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BN570Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BN571Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BN572Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BN573Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BN584Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BN585Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BN588Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BN590Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Constants;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Page10 extends PageAbs {

	interface PageBinder extends
			UiBinder<Widget, Page10> {
	}

	private static final PageBinder pageBinder = GWT
			.create(PageBinder.class);
	
	private static final String[] HEADERS_1 = new String[]{null,
			MSG.pendingDeduction(),
			MSG.taxType(),
			MSG.pendingDeduction2013(),
			MSG.appliedDeduction(),
			MSG.futureDeduction()};
	
	private static final String[] HEADERS_2 = new String[]{null,
			MSG.pendingDeduction(),
			MSG.pendingDeduction2013(),
			MSG.appliedDeduction(),
			MSG.futureDeduction()};

	private static final String[] HEADERS_3 = new String[]{null,
			MSG.pendingDeduction(),
			MSG.appliedDeduction(),
			MSG.futureDeduction()};
	
	private static final String[] HEADERS_4 = new String[]{null,
			MSG.generatedDeduction(),
			MSG.reducedDeduction(),
			MSG.quotableAmount(),
			MSG.pendingDueToQuota()};
	

	@UiField(provided = true)
	FlexTable table1;
	
	public Page10() {
		super();
		table1 = new FlexTable();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().setWidth(1, "250px");
		table1.setWidth("100%");
		table1.setCellSpacing(0);
		table1.getColumnFormatter().setWidth(0, "auto");
		table1.getColumnFormatter().setWidth(1, "250px");
	}
	
	@Override
	protected void initializeTable() {
		int row = 0;
		for (final Mod200Key key : Mod200Constants.LIQUIDATION_III_KEYS_1) {
			row = paintKey(table,key,row);
			if (key == Mod200Key.BN570) {
				row = paintKeyBreakdown(table,row,MSG.doubleContributionNationalPrevious()
						,Mod200BN570Key.values(),HEADERS_1);
			} 
			if (key == Mod200Key.BN571) {
				row = paintKeyBreakdown(table,row,MSG.doubleContributionNational2013()
						,Mod200BN571Key.values(),HEADERS_2);
			} 
			if (key == Mod200Key.BN572) {
				row = paintKeyBreakdown(table,row,MSG.doubleContributionInternationalPrevious()
						,Mod200BN572Key.values(),HEADERS_1);
			} 
			if (key == Mod200Key.BN573) {
				row = paintKeyBreakdown(table,row,MSG.doubleContributionInternational2013()
						,Mod200BN573Key.values(),HEADERS_2);
			} 
		}
		row = 0;
		for (final Mod200Key key : Mod200Constants.LIQUIDATION_III_KEYS_2) {
			row = paintKey(table1,key,row);
			if (key == Mod200Key.BN585) {
				row = paintKeyBreakdown(table1,row,Mod200Key.BN585.getDescription()
						,Mod200BN585Key.values(),HEADERS_3);
			} 
			if (key == Mod200Key.BN584) {
				row = paintKeyBreakdown(table1,row,Mod200Key.BN584.getDescription()
						,Mod200BN584Key.values(),HEADERS_3);
			} 
			if (key == Mod200Key.BN588) {
				row = paintKeyBreakdown(table1,row,Mod200Key.BN588.getDescription()
						,Mod200BN588Key.values(),HEADERS_3);
			} 
			if (key == Mod200Key.BN082) {
				row = paintKeyBreakdown(table1,row,Mod200Key.BN082.getDescription()
						,Mod200BN082Key.values(),HEADERS_4);
			} 
			if (key == Mod200Key.BN565) {
				row = paintKeyBreakdown(table1,row,Mod200Key.BN082.getDescription()
						,Mod200BN565Key.values(),HEADERS_3);
			} 
			if (key == Mod200Key.BN590) {
				row = paintKeyBreakdown(table1,row,Mod200Key.BN590.getDescription()
						,Mod200BN590Key.values(),HEADERS_3);
			} 
		}
	}
	
	private int paintKeyBreakdown(FlexTable tab,int row,final String label, IMod200KeysProvider[] keysProvider,String[] headers) {
		
		FlexTable tableDetail = getFlexTable(tab,row,label,headers);
		int r = 1;
		int col = 0;
		for (IMod200KeysProvider key : keysProvider) {
			Label desc = new Label(key.getDescription() );
			tableDetail.setWidget(r, 0, desc);
			tableDetail.getFlexCellFormatter().setStyleName(r, 0, RESOURCES.css().aonMod200BorderBottom());
			col = 1;
			for (final Mod200Key k : key.getKeys() ) {
				if (k != null){
					Boolean[] behaviour = BEHAVIOUR_KEYS_MAP.get(k.toString());
					boolean disabled = behaviour != null && behaviour[1];
	
					FlowPanel panel = new FlowPanel();
					BoxLabel code = new BoxLabel(k.getCode( mod200Object.getAdministration() ));
					panel.add(code);
					getLabels().put(k, code);
					
					final DoubleTextBox text = new DoubleTextBox(12);
					text.addChangeHandler(new ChangeHandler() {
						@Override
						public void onChange(ChangeEvent event) {
							if (text.isValidValue()) {
								text.addStyleName(RESOURCES.css().aonChanged());
								mod200Object.doubleValueChanged(k, text.getDoubleValue());
							}
						}
					});
					text.setValue(mod200Object.getDoubleValue(k));
					text.addStyleName(RESOURCES.css().aonMod200MarginLeft());
					text.addStyleName(RESOURCES.css().aonMod200PaddingLeft());
					text.setChangeDisplayStyleName(RESOURCES.css().aonValueChanged());
					text.setEnabled(!disabled);
					panel.add(text);
					
					getInputs().put(k, text);
					tableDetail.setWidget(r, col, panel);
					tableDetail.getFlexCellFormatter().addStyleName(r, col, RESOURCES.css().aonTextRight());
					tableDetail.getFlexCellFormatter().addStyleName(r, col, RESOURCES.css().aonNowrap());
				}
				++col;
			}
			++r;
		}
		return ++row;
	}
	
	private FlexTable getFlexTable(FlexTable tab,int row,final String label, String[] mod200CompensationHeaders) {
		FlowPanel container = new FlowPanel();
		container.addStyleName(RESOURCES.css().aonGroup());
		
		FlowPanel titleContainer = new FlowPanel();
		titleContainer.addStyleName(RESOURCES.css().aonGroupTitle());
		container.add(titleContainer);
		final InlineLabel titleLabel = new InlineLabel();
		titleLabel.addStyleName(RESOURCES.css().aonClickable());
		titleContainer.add(titleLabel);
		titleLabel.setText(label + " \u25BA");
		
		final DisclosurePanel bodyContainer = new DisclosurePanel();
		bodyContainer.addStyleName(RESOURCES.css().aonWidthAll());
		titleLabel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				bodyContainer.setOpen(!bodyContainer.isOpen());
			}
		});
		bodyContainer.addOpenHandler(new OpenHandler<DisclosurePanel>() {
			
			@Override
			public void onOpen(OpenEvent<DisclosurePanel> event) {
				titleLabel.setText(label + " \u25BC");
				bodyContainer.addStyleName(RESOURCES.css().aonMod200InnerGroupBody());
			}
		});
		bodyContainer.addCloseHandler(new CloseHandler<DisclosurePanel>() {
			@Override
			public void onClose(CloseEvent<DisclosurePanel> event) {
				titleLabel.setText(label + " \u25BA");
				bodyContainer.removeStyleName(RESOURCES.css().aonMod200InnerGroupBody());
			}
		});
		bodyContainer.addStyleName(RESOURCES.css().aonGroupBody());
		container.add(bodyContainer);
		FlexTable tableDetail = new FlexTable();
		tableDetail.addStyleName(RESOURCES.css().aonWidthAll());
		bodyContainer.add(tableDetail);
		int r = 0;
		int col = 0;
		for (String  headerText : mod200CompensationHeaders ) {
			Label headerLabel = new Label(headerText);
			tableDetail.setWidget(r, col, headerLabel);
			tableDetail.getFlexCellFormatter().addStyleName(r, col, RESOURCES.css().aonBold());
			tableDetail.getFlexCellFormatter().addStyleName(r, col, RESOURCES.css().aonBorderBottom());
			tableDetail.getFlexCellFormatter().addStyleName(r, col, RESOURCES.css().aonTextCenter());
			tableDetail.getColumnFormatter().setWidth(col, col==0?"auto":"160px");
			++col;
		}
		tab.setWidget(row, 0, container);
		tab.getFlexCellFormatter().setColSpan(row, 0, 2);
		return tableDetail;
	}
}
