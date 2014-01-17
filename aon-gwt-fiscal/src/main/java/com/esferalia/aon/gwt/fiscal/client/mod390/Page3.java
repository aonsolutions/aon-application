package com.esferalia.aon.gwt.fiscal.client.mod390;

import com.esferalia.aon.gwt.fiscal.client.css.AonResources;
import com.esferalia.aon.gwt.fiscal.client.css.GWTResources;
import com.esferalia.aon.gwt.fiscal.client.widget.ActivityPanel;
import com.esferalia.aon.gwt.fiscal.client.widget.ActivityPanel.SelectionCallBack;
import com.esferalia.aon.gwt.fiscal.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.fiscal.shared.Activity;
import com.esferalia.aon.gwt.fiscal.shared.AonUtil;
import com.esferalia.aon.gwt.fiscal.shared.Mod390;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Page3 extends ResizeComposite {

	interface Page3Binder extends
			UiBinder<Widget, Page3> {
	}

	private static final Page3Binder page3Binder = GWT
			.create(Page3Binder.class);

	private final static AonResources RESOURCES = GWT.create(AonResources.class);
	
	@UiField
	Button mainActivityButton;
	@UiField
	Label mainActivityDescription;
	@UiField
	Label mainActivityKey;
	@UiField
	Label mainActivityEpigraph;
	
	@UiField
	Button activity1Button;
	@UiField
	Label activity1Description;
	@UiField
	Label activity1Key;
	@UiField
	Label activity1Epigraph;

	@UiField
	Button activity2Button;
	@UiField
	Label activity2Description;
	@UiField
	Label activity2Key;
	@UiField
	Label activity2Epigraph;

	@UiField
	Button activity3Button;
	@UiField
	Label activity3Description;
	@UiField
	Label activity3Key;
	@UiField
	Label activity3Epigraph;

	@UiField
	Button activity4Button;
	@UiField
	Label activity4Description;
	@UiField
	Label activity4Key;
	@UiField
	Label activity4Epigraph;

	@UiField
	Button activity5Button;
	@UiField
	Label activity5Description;
	@UiField
	Label activity5Key;
	@UiField
	Label activity5Epigraph;
	
	@UiField(provided=true)
	ActivityPanel activityPanel;
	
	@UiField
	CheckBox mod347;
	
	@UiField
	DocumentTextBox mergedDeclarationDocument;

	@UiField
	TextBox mergedDeclarationName;
	
	
	public Page3() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();
		
		activityPanel = new ActivityPanel();
		
		Widget ui = page3Binder.createAndBindUi(this);
		initWidget(ui);
	}
	
	@UiHandler("mainActivityButton")
	void onMainActivityClick(ClickEvent event) {
		activityPanel.setCallback(new SelectionCallBack() {
			@Override
			public void onSelect(Activity activity) {
				mainActivityDescription.setText( activity.getDescription() );
				mainActivityKey.setText( activity.getKey() );
				mainActivityEpigraph.setText( activity.getEpigraph() );
			}
			
			@Override
			public void onClose() {
				
			}
		});
		activityPanel.center();
		activityPanel.show();
	}
	@UiHandler("activity1Button")
	void onActivity1ButtonClick(ClickEvent event) {
		activityPanel.setCallback(new SelectionCallBack() {
			@Override
			public void onSelect(Activity activity) {
				activity1Description.setText( activity.getDescription() );
				activity1Key.setText( activity.getKey() );
				activity1Epigraph.setText( activity.getEpigraph() );
			}
			
			@Override
			public void onClose() {
				
			}
		});
		activityPanel.center();
		activityPanel.show();
	}
	@UiHandler("activity2Button")
	void onActivity2ButtonClick(ClickEvent event) {
		activityPanel.setCallback(new SelectionCallBack() {
			@Override
			public void onSelect(Activity activity) {
				activity2Description.setText( activity.getDescription() );
				activity2Key.setText( activity.getKey() );
				activity2Epigraph.setText( activity.getEpigraph() );
			}
			
			@Override
			public void onClose() {
				
			}
		});
		activityPanel.center();
		activityPanel.show();
	}
	@UiHandler("activity3Button")
	void onActivity3ButtonClick(ClickEvent event) {
		activityPanel.setCallback(new SelectionCallBack() {
			@Override
			public void onSelect(Activity activity) {
				activity3Description.setText( activity.getDescription() );
				activity3Key.setText( activity.getKey() );
				activity3Epigraph.setText( activity.getEpigraph() );
			}
			
			@Override
			public void onClose() {
				
			}
		});
		activityPanel.center();
		activityPanel.show();
	}
	@UiHandler("activity4Button")
	void onActivity4ButtonClick(ClickEvent event) {
		activityPanel.setCallback(new SelectionCallBack() {
			@Override
			public void onSelect(Activity activity) {
				activity4Description.setText( activity.getDescription() );
				activity4Key.setText( activity.getKey() );
				activity4Epigraph.setText( activity.getEpigraph() );
			}
			
			@Override
			public void onClose() {
				
			}
		});
		activityPanel.center();
		activityPanel.show();
	}
	@UiHandler("activity5Button")
	void onActivity5ButtonClick(ClickEvent event) {
		activityPanel.setCallback(new SelectionCallBack() {
			@Override
			public void onSelect(Activity activity) {
				activity5Description.setText( activity.getDescription() );
				activity5Key.setText( activity.getKey() );
				activity5Epigraph.setText( activity.getEpigraph() );
			}
			
			@Override
			public void onClose() {
				
			}
		});
		activityPanel.center();
		activityPanel.show();
	}

	public void setValue(Mod390 m390) {
		if (m390.getMainActivity() != null) {
			mainActivityDescription.setText(m390.getMainActivity().getDescription());	
			mainActivityKey.setText(m390.getMainActivity().getKey());
			mainActivityEpigraph.setText(m390.getMainActivity().getEpigraph());
		}
		if (m390.getActivity1() != null) {
			activity1Description.setText(m390.getActivity1().getDescription());	
			activity1Key.setText(m390.getActivity1().getKey());
			activity1Epigraph.setText(m390.getActivity1().getEpigraph());
		}
		if (m390.getActivity2() != null) {
			activity2Description.setText(m390.getActivity2().getDescription());	
			activity2Key.setText(m390.getActivity2().getKey());
			activity2Epigraph.setText(m390.getActivity2().getEpigraph());
		}
		if (m390.getActivity3() != null) {
			activity3Description.setText(m390.getActivity3().getDescription());	
			activity3Key.setText(m390.getActivity3().getKey());
			activity3Epigraph.setText(m390.getActivity3().getEpigraph());
		}
		if (m390.getActivity4() != null) {
			activity4Description.setText(m390.getActivity4().getDescription());	
			activity4Key.setText(m390.getActivity4().getKey());
			activity4Epigraph.setText(m390.getActivity4().getEpigraph());
		}
		if (m390.getActivity5() != null) {
			activity5Description.setText(m390.getActivity5().getDescription());	
			activity5Key.setText(m390.getActivity5().getKey());
			activity5Epigraph.setText(m390.getActivity5().getEpigraph());
		}
		mod347.setValue(m390.isMod347());
		mergedDeclarationDocument.setValue(m390.getMergedDeclarationDocument());
		mergedDeclarationName.setValue(m390.getMergedDeclarationName());
	}

	public void populate(Mod390 mod390) {
		if (!AonUtil.isEmpty( mainActivityEpigraph.getText() ) ) {
			Activity mainActivity = new Activity();	
			mainActivity.setKey(mainActivityKey.getText());
			mainActivity.setDescription(mainActivityDescription.getText());
			mainActivity.setEpigraph(mainActivityEpigraph.getText());
			mod390.setMainActivity(mainActivity);
		}
		if (!AonUtil.isEmpty( activity1Epigraph.getText() ) ) {
			Activity activity = new Activity();	
			activity.setKey(activity1Key.getText());
			activity.setDescription(activity1Description.getText());
			activity.setEpigraph(activity1Epigraph.getText());
			mod390.setActivity1(activity);
		}
		if (!AonUtil.isEmpty( activity2Epigraph.getText() ) ) {
			Activity activity = new Activity();	
			activity.setKey(activity2Key.getText());
			activity.setDescription(activity2Description.getText());
			activity.setEpigraph(activity2Epigraph.getText());
			mod390.setActivity2(activity);
		}
		if (!AonUtil.isEmpty( activity3Epigraph.getText() ) ) {
			Activity activity = new Activity();	
			activity.setKey(activity3Key.getText());
			activity.setDescription(activity3Description.getText());
			activity.setEpigraph(activity3Epigraph.getText());
			mod390.setActivity3(activity);
		}
		if (!AonUtil.isEmpty( activity4Epigraph.getText() ) ) {
			Activity activity = new Activity();	
			activity.setKey(activity4Key.getText());
			activity.setDescription(activity4Description.getText());
			activity.setEpigraph(activity4Epigraph.getText());
			mod390.setActivity4(activity);
		}
		if (!AonUtil.isEmpty( activity5Epigraph.getText() ) ) {
			Activity activity = new Activity();	
			activity.setKey(activity5Key.getText());
			activity.setDescription(activity5Description.getText());
			activity.setEpigraph(activity5Epigraph.getText());
			mod390.setActivity5(activity);
		}
		mod390.setMod347(mod347.getValue());
		mod390.setMergedDeclarationDocument(mergedDeclarationDocument.getValue());
		mod390.setMergedDeclarationName(mergedDeclarationName.getValue());
	}
}
