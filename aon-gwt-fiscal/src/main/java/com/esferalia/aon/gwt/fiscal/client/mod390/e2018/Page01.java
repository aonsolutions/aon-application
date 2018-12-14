package com.esferalia.aon.gwt.fiscal.client.mod390.e2018;

import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2018.Model3902018.IMod3902018CallBack;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2018.Model3902018.IMod3902018Page;
import com.esferalia.aon.gwt.fiscal.client.widget.ActivityPanel;
import com.esferalia.aon.gwt.fiscal.client.widget.ActivityPanel.SelectionCallBack;
import com.esferalia.aon.occam.api.model.fiscal.Activity;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018;
import com.esferalia.aon.watson.util.AonStringUtils;
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

public class Page01 extends ResizeComposite implements IMod3902018Page {

	interface PageBinder extends UiBinder<Widget, Page01> {
	}

	private static final PageBinder BINDER = GWT.create(PageBinder.class);

	IMod3902018CallBack callback;
	
	@UiField
	Button mainActivityButton;
	@UiField
	Button mainActivityDeleteButton;
	@UiField
	Label mainActivityDescription;
	@UiField
	Label mainActivityKey;
	@UiField
	Label mainActivityEpigraph;
	
	@UiField
	Button activity1Button;
	@UiField
	Button activity1DeleteButton;
	@UiField
	Label activity1Description;
	@UiField
	Label activity1Key;
	@UiField
	Label activity1Epigraph;

	@UiField
	Button activity2Button;
	@UiField
	Button activity2DeleteButton;
	@UiField
	Label activity2Description;
	@UiField
	Label activity2Key;
	@UiField
	Label activity2Epigraph;

	@UiField
	Button activity3Button;
	@UiField
	Button activity3DeleteButton;
	@UiField
	Label activity3Description;
	@UiField
	Label activity3Key;
	@UiField
	Label activity3Epigraph;

	@UiField
	Button activity4Button;
	@UiField
	Button activity4DeleteButton;
	@UiField
	Label activity4Description;
	@UiField
	Label activity4Key;
	@UiField
	Label activity4Epigraph;

	@UiField
	Button activity5Button;
	@UiField
	Button activity5DeleteButton;
	@UiField
	Label activity5Description;
	@UiField
	Label activity5Key;
	@UiField
	Label activity5Epigraph;
	
	ActivityPanel activityPanel;
	
	@UiField
	CheckBox mod347;
	
	@UiField
	DocumentTextBox mergedDeclarationDocument;

	@UiField
	TextBox mergedDeclarationName;

	
	public Page01(Mod3902018 m390) {
		activityPanel = new ActivityPanel();
		Widget ui = BINDER.createAndBindUi(this);
		initWidget(ui);
		setValue(m390);
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
	@UiHandler("mainActivityDeleteButton")
	void onMainActivityDeleteButtonClick(ClickEvent event) {
		mainActivityDescription.setText(null);	
		mainActivityKey.setText(null);
		mainActivityEpigraph.setText(null);
	}
	@UiHandler("activity1DeleteButton")
	void onActivity1DeleteButtonClick(ClickEvent event) {
		activity1Description.setText(null);	
		activity1Key.setText(null);
		activity1Epigraph.setText(null);
	}
	@UiHandler("activity2DeleteButton")
	void onActivity2DeleteButtonClick(ClickEvent event) {
		activity2Description.setText(null);	
		activity2Key.setText(null);
		activity2Epigraph.setText(null);
	}
	@UiHandler("activity3DeleteButton")
	void onActivity3DeleteButtonClick(ClickEvent event) {
		activity3Description.setText(null);	
		activity3Key.setText(null);
		activity3Epigraph.setText(null);
	}
	@UiHandler("activity4DeleteButton")
	void onActivity4DeleteButtonClick(ClickEvent event) {
		activity4Description.setText(null);	
		activity4Key.setText(null);
		activity4Epigraph.setText(null);
	}
	@UiHandler("activity5DeleteButton")
	void onActivity5DeleteButtonClick(ClickEvent event) {
		activity5Description.setText(null);	
		activity5Key.setText(null);
		activity5Epigraph.setText(null);
	}

	private void setValue(Mod3902018 m390) {
		if (m390.getMainActivity() != null) {
			mainActivityDescription.setText(m390.getMainActivity().getDescription());	
			mainActivityKey.setText(m390.getMainActivity().getKey());
			mainActivityEpigraph.setText(m390.getMainActivity().getEpigraph());
		} else {
			mainActivityDescription.setText(null);	
			mainActivityKey.setText(null);
			mainActivityEpigraph.setText(null);
		}
		if (m390.getActivity1() != null) {
			activity1Description.setText(m390.getActivity1().getDescription());	
			activity1Key.setText(m390.getActivity1().getKey());
			activity1Epigraph.setText(m390.getActivity1().getEpigraph());
		} else {
			activity1Description.setText(null);	
			activity1Key.setText(null);
			activity1Epigraph.setText(null);
		}
		if (m390.getActivity2() != null) {
			activity2Description.setText(m390.getActivity2().getDescription());	
			activity2Key.setText(m390.getActivity2().getKey());
			activity2Epigraph.setText(m390.getActivity2().getEpigraph());
		} else {
			activity2Description.setText(null);	
			activity2Key.setText(null);
			activity2Epigraph.setText(null);
		}
		if (m390.getActivity3() != null) {
			activity3Description.setText(m390.getActivity3().getDescription());	
			activity3Key.setText(m390.getActivity3().getKey());
			activity3Epigraph.setText(m390.getActivity3().getEpigraph());
		} else {
			activity3Description.setText(null);	
			activity3Key.setText(null);
			activity3Epigraph.setText(null);
		}
		if (m390.getActivity4() != null) {
			activity4Description.setText(m390.getActivity4().getDescription());	
			activity4Key.setText(m390.getActivity4().getKey());
			activity4Epigraph.setText(m390.getActivity4().getEpigraph());
		} else {
			activity4Description.setText(null);	
			activity4Key.setText(null);
			activity4Epigraph.setText(null);
		}
		if (m390.getActivity5() != null) {
			activity5Description.setText(m390.getActivity5().getDescription());	
			activity5Key.setText(m390.getActivity5().getKey());
			activity5Epigraph.setText(m390.getActivity5().getEpigraph());
		} else {
			activity5Description.setText(null);	
			activity5Key.setText(null);
			activity5Epigraph.setText(null);
		}
		mod347.setValue(m390.isMod347());
		mergedDeclarationDocument.setValue(m390.getMergedDeclarationDocument());
		mergedDeclarationName.setValue(m390.getMergedDeclarationName());
	}

	@Override
	public void populate(Mod3902018 mod390) {
		if (!AonStringUtils.isEmpty( mainActivityKey.getText() ) ) {
			Activity mainActivity = new Activity();
			mainActivity.setKey(mainActivityKey.getText());
			mainActivity.setDescription(mainActivityDescription.getText());
			mainActivity.setEpigraph(mainActivityEpigraph.getText());
			mod390.setMainActivity(mainActivity);
		} else {
			mod390.setMainActivity(null);
		}
		
		if (!AonStringUtils.isEmpty( activity1Key.getText() ) ) {
			Activity activity1 = new Activity();	
			activity1.setKey(activity1Key.getText());
			activity1.setDescription(activity1Description.getText());
			activity1.setEpigraph(activity1Epigraph.getText());
			mod390.setActivity1(activity1);
		} else {
			mod390.setActivity1(null);
		}
		
		if (!AonStringUtils.isEmpty( activity2Key.getText() ) ) {
			Activity activity2 = new Activity();	
			activity2.setKey(activity2Key.getText());
			activity2.setDescription(activity2Description.getText());
			activity2.setEpigraph(activity2Epigraph.getText());
			mod390.setActivity2(activity2);
		} else {
			mod390.setActivity2(null);
		}
		
		if (!AonStringUtils.isEmpty( activity3Key.getText() ) ) {
			Activity activity3 = new Activity();	
			activity3.setKey(activity3Key.getText());
			activity3.setDescription(activity3Description.getText());
			activity3.setEpigraph(activity3Epigraph.getText());
			mod390.setActivity3(activity3);
		} else {
			mod390.setActivity3(null);
		}
		
		if (!AonStringUtils.isEmpty( activity4Key.getText() ) ) {
			Activity activity4 = new Activity();	
			activity4.setKey(activity4Key.getText());
			activity4.setDescription(activity4Description.getText());
			activity4.setEpigraph(activity4Epigraph.getText());
			mod390.setActivity4(activity4);
		} else {
			mod390.setActivity4(null);
		}
		
		if (!AonStringUtils.isEmpty( activity5Key.getText() ) ) {
			Activity activity5 = new Activity();	
			activity5.setKey(activity5Key.getText());
			activity5.setDescription(activity5Description.getText());
			activity5.setEpigraph(activity5Epigraph.getText());
			mod390.setActivity5(activity5);
		} else {
			mod390.setActivity5(null);
		}
		
		mod390.setMod347(mod347.getValue());
		mod390.setMergedDeclarationDocument(mergedDeclarationDocument.getValue());
		mod390.setMergedDeclarationName(mergedDeclarationName.getValue());
	}

	@Override
	public void setCallback(IMod3902018CallBack callback) {
		this.callback = callback;
	}
	@Override
	public void refresh(Mod3902018 m390) {
		setValue(m390);
	}

}
