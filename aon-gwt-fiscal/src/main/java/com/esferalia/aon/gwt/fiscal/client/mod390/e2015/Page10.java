package com.esferalia.aon.gwt.fiscal.client.mod390.e2015;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCnae2009Panel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015.Model3902015Callback;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Prorrata;
import com.esferalia.aon.occam.api.model.type.CNAE2009;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class Page10 extends PageAbs {

	public Page10(Model3902015Callback callback) {
		super(callback);
		paint();
		setValue();
	}
	
	@Override
	protected void setValue() {
		// Nothing
	}

	private void paint() {
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel basePanel = new FlowPanel();
		scroll.add(basePanel);
		setWidget(scroll);

		basePanel.add(getTitle(AON.MSG.specificOperations()));
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab);
		tab.addRow()
			.addCell( new Label("C.N.A.E."),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth80())
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20())
			.addCell( new Label(AON.MSG.activityDescription()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidthAuto())
			.addCell( new Label("E/G"),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth40())
			.addCell( new Label(AON.MSG.operationsAmount()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonTextCenter(),AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.operationsAmountWithRight()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonTextCenter(),AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.percent()),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth80())
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20())
		;
		
		for ( int i = 0; i < getModel().getProrratas().size(); i++) {
			final int idx = i;
			AonTextBox cnae = new AonTextBox();
			cnae.setValue(getModel().getProrratas().get(idx).getCnae());
			cnae.addValueChangeHandler(event -> {
				getModel().getProrratas().get(idx).setCnae(cnae.getValue());
				markAsDirty();
			});
			cnae.setVisibleLength(8);
			AonTextBox cnaeDescription = new AonTextBox();
			cnaeDescription.setValue(getModel().getProrratas().get(idx).getActivity());
			cnaeDescription.setMaxLength(30);
			cnaeDescription.setVisibleLength(30);
			cnaeDescription.addValueChangeHandler(event -> {
				getModel().getProrratas().get(idx).setActivity(cnaeDescription.getValue());
				markAsDirty();
			});
			AonTableButton cnaeButton = new AonTableButton("CNAE",AON.CSS.aonIconSearch());
			cnaeButton.addClickHandler(event -> {
				AonCnae2009Panel cnaePanel = new AonCnae2009Panel();
				cnaePanel.addSelectionHandler(event1 -> {
					CNAE2009 c = event1.getSelectedItem();
					if (event1.getSelectedItem() != null) {
						getModel().getProrratas().get(idx).setCnae(c.getCode());
						getModel().getProrratas().get(idx).setActivity(c.getDescription());
						cnae.setValue(c.getCode());
						cnaeDescription.setValue(c.getDescription());
						markAsDirty();
					}
				});
				cnaePanel.center();
				cnaePanel.show();
			});
			AonTextBox type  = new AonTextBox();
			type.setValue(getModel().getProrratas().get(idx).getType());
			type.setMaxLength(1);
			type.setVisibleLength(2);
			AonDoubleBox operationsAmount = new AonDoubleBox();
			operationsAmount.setValue(getModel().getProrratas().get(idx).getAmount());
			operationsAmount.addValueChangeHandler(event -> {
				getModel().getProrratas().get(idx).setAmount(operationsAmount.getValue());
				markAsDirty();
			});
			AonDoubleBox operationsAmountWithRight = new AonDoubleBox();
			operationsAmountWithRight.setValue(getModel().getProrratas().get(idx).getAmountWithRight());
			operationsAmountWithRight.addValueChangeHandler(event -> {
				getModel().getProrratas().get(idx).setAmountWithRight(operationsAmountWithRight.getValue());
				markAsDirty();
			});
			AonDoubleBox percent = new AonDoubleBox();
			percent.setValue(getModel().getProrratas().get(idx).getPercent());
			percent.addValueChangeHandler(event -> {
				getModel().getProrratas().get(idx).setPercent(percent.getValue());
				markAsDirty();
			});
			percent.setVisibleLength(6);
			percent.setMaxLength(6);
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				getModel().getProrratas().remove( idx );
				paint();
			});

			tab.addRow()
				.addCell( cnae )
				.addCell( cnaeButton )
				.addCell( cnaeDescription )
				.addCell( type )
				
				.addCell( operationsAmount )
				.addCell( operationsAmountWithRight )
				.addCell( percent )
				.addCell( deleteButton )
			;
		}
		AonTableButton addButton = new AonTableButton(AON.MSG.resetAction(),AON.CSS.aonIconAdd());
		addButton.addStyleName(AON.CSS.aonMarginTop());
		addButton.addStyleName(AON.CSS.aonMarginLeft());
		addButton.addClickHandler(event -> {
			getModel().getProrratas().add(new Prorrata());
			paint();
		});
		basePanel.add(addButton);

	}

}