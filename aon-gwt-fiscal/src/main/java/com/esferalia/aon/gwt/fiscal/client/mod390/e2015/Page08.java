package com.esferalia.aon.gwt.fiscal.client.mod390.e2015;

import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015.IMod3902015CallBack;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2015.Model3902015.IMod3902015Page;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902015;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Page08 extends ResizeComposite implements RequiresResize , IMod3902015Page {

	interface PageBinder extends UiBinder<Widget, Page08> {
	}

	private static final PageBinder BINDER = GWT.create(PageBinder.class);

	IMod3902015CallBack cbk;
	
	@UiField
	DoubleBox box99;
	
	@UiField
	DoubleBox box653;

	@UiField
	DoubleBox box103;
	
	@UiField
	DoubleBox box104;
	
	@UiField
	DoubleBox box105;
	
	@UiField
	DoubleBox box110;
	
	@UiField
	DoubleBox box112;
	
	@UiField
	DoubleBox box100;
	
	@UiField
	DoubleBox box101;
	
	@UiField
	DoubleBox box102;
	
	@UiField
	DoubleBox box227;
	
	@UiField
	DoubleBox box228;
	
	@UiField
	DoubleBox box106;
	
	@UiField
	DoubleBox box107;
	
	@UiField
	DoubleBox box108;
	
	public Page08(Mod3902015 m390) {
		Widget ui = BINDER.createAndBindUi(this);
		initWidget(ui);
		box108.setEnabled(false);
		setValue(m390);
	}

	private void setValue(Mod3902015 m390) {
		box99.setValue(m390.getBox99());
		box653.setValue(m390.getBox653());
		box103.setValue(m390.getBox103());
		box104.setValue(m390.getBox104());
		box105.setValue(m390.getBox105());
		box110.setValue(m390.getBox110());
		box112.setValue(m390.getBox112());
		box100.setValue(m390.getBox100());
		box101.setValue(m390.getBox101());
		box102.setValue(m390.getBox102());
		box227.setValue(m390.getBox227());
		box228.setValue(m390.getBox228());
		box106.setValue(m390.getBox106());
		box107.setValue(m390.getBox107());
		box108.setValue(m390.getBox108());
	}

	@Override
	public void populate(Mod3902015 mod390) {
		mod390.setBox99(box99.getValue());
		mod390.setBox653(box653.getValue());
		mod390.setBox103(box103.getValue());
		mod390.setBox104(box104.getValue());
		mod390.setBox105(box105.getValue());
		mod390.setBox110(box110.getValue());
		mod390.setBox112(box112.getValue());
		mod390.setBox100(box100.getValue());
		mod390.setBox101(box101.getValue());
		mod390.setBox102(box102.getValue());
		mod390.setBox227(box227.getValue());
		mod390.setBox228(box228.getValue());
		mod390.setBox106(box106.getValue());
		mod390.setBox107(box107.getValue());
		mod390.setBox108(box108.getValue());
	}
	
	@UiHandler("box99")
	void onChangeBox99(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox99(box99.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}

	@UiHandler("box653")
	void onChangeBox653(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox653(box653.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}

	@UiHandler("box103")
	void onChangeBox103(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox103(box103.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box104")
	void onChangeBox104(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox104(box104.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box105")
	void onChangeBox105(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox105(box105.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box110")
	void onChangeBox110(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox110(box110.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box112")
	void onChangeBox112(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox112(box112.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box100")
	void onChangeBox100(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox100(box100.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}

	@UiHandler("box101")
	void onChangeBox101(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox101(box101.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box102")
	void onChangeBox102(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox102(box102.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box227")
	void onChangeBox227(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox227(box227.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box228")
	void onChangeBox228(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox228(box228.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box106")
	void onChangeBox106(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox106(box106.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box107")
	void onChangeBox107(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				cbk.getMod390().setBox107(box107.getValue());
				cbk.calculateAndRefresh();
			}
		});
	}

	@Override
	public void setCallback(IMod3902015CallBack callback) {
		this.cbk = callback;
	}
	
	@Override
	public void refresh(Mod3902015 m390) {
		if (!AonMathUtils.equals(box108.getValue(), m390.getBox108())) 
			box108.setValue(m390.getBox108(),true,true);
	}
}

