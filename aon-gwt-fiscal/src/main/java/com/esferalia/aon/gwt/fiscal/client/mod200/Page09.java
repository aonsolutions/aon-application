package com.esferalia.aon.gwt.fiscal.client.mod200;

import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.MSG;
import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.RESOURCES;
import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200ConstantsBehaviour.BEHAVIOUR_KEYS_MAP;

import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200CompensationKey;
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
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Page09 extends PageAbs {
	
	interface PageBinder extends
			UiBinder<Widget, Page09> {
	}

	private static final PageBinder pageBinder = GWT
			.create(PageBinder.class);

	public Page09() {
		super();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	protected void initializeTable() {
		
		table.setWidth("100%");
		table.setCellSpacing(0);
		
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setWidth(0, "auto");
		cf.setWidth(1, "250px");

		int row = 0;
		for (Mod200Key key : Mod200Constants.LIQUIDATION_II_KEYS) {
			if (mod200Object.isVisible(key)) {
				row = paintKey(key,row);
				if (key == Mod200Key.LQ547) {
					row = paintLq547Breakdown(row);
				}
			}
		}
	}

	private int paintLq547Breakdown(int row) {
		FlowPanel container = new FlowPanel();
		container.addStyleName(RESOURCES.css().aonGroup());
		
		FlowPanel titleContainer = new FlowPanel();
		titleContainer.addStyleName(RESOURCES.css().aonGroupTitle());
		container.add(titleContainer);
		final InlineLabel titleLabel = new InlineLabel();
		titleLabel.addStyleName(RESOURCES.css().aonClickable());
		titleContainer.add(titleLabel);
		titleLabel.setText(MSG.compensationDetail() + " \u25BA");
		
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
				titleLabel.setText(MSG.compensationDetail() + " \u25BC");
				bodyContainer.addStyleName(RESOURCES.css().aonMod200InnerGroupBody());
			}
		});
		bodyContainer.addCloseHandler(new CloseHandler<DisclosurePanel>() {
			@Override
			public void onClose(CloseEvent<DisclosurePanel> event) {
				titleLabel.setText(MSG.compensationDetail() + " \u25BA");
				bodyContainer.removeStyleName(RESOURCES.css().aonMod200InnerGroupBody());
			}
		});
		bodyContainer.addStyleName(RESOURCES.css().aonGroupBody());
		container.add(bodyContainer);
		FlexTable tableDetail = new FlexTable();
		tableDetail.addStyleName(RESOURCES.css().aonWidthAll());
		bodyContainer.add(tableDetail);
		int r = 0;
		String[] mod200CompensationHeaders = new String[]{
				null,MSG.previousPending(),MSG.current(),MSG.futurePending()};
		int col = 0;
		for (String  headerText : mod200CompensationHeaders ) {
			Label headerLabel = new Label(headerText);
			tableDetail.setWidget(r, col, headerLabel);
			tableDetail.getFlexCellFormatter().addStyleName(r, col, RESOURCES.css().aonBold());
			tableDetail.getFlexCellFormatter().addStyleName(r, col, RESOURCES.css().aonBorderBottom());
			tableDetail.getFlexCellFormatter().addStyleName(r, col, RESOURCES.css().aonTextCenter());
			tableDetail.getColumnFormatter().setWidth(col, col==0?"auto":"180px");
			++col;
		}
		r++;
		for (Mod200CompensationKey key : Mod200CompensationKey.values()) {
			Label desc = new Label(key.getDescription() );
			tableDetail.setWidget(r, 0, desc);
			tableDetail.getFlexCellFormatter().setStyleName(r, 0, RESOURCES.css().aonMod200BorderBottom());
			col = 1;
			for (final Mod200Key k : key.getKeys() ) {
				if (k != Mod200Key.LQ547){
					Boolean[] behaviour = BEHAVIOUR_KEYS_MAP.get(k.toString());
					boolean disabled = behaviour != null && behaviour[1];
	
					FlowPanel panel = new FlowPanel();
					Label code = new Label(k.getCode( mod200Object.getAdministration() ));
					code.setStyleName(RESOURCES.css().aonMod200Box());
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
		table.setWidget(row, 0, container);
		table.getFlexCellFormatter().setColSpan(row, 0, 2);
		return ++row;
	}
	
}
