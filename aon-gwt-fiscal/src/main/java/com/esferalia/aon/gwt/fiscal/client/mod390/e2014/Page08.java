package com.esferalia.aon.gwt.fiscal.client.mod390.e2014;

import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2014.Model3902014.IMod3902014CallBack;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2014.Model3902014.IMod3902014Page;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902014;
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

public class Page08 extends ResizeComposite implements RequiresResize , IMod3902014Page {

	interface PageBinder extends UiBinder<Widget, Page08> {
	}

	private static final PageBinder BINDER = GWT.create(PageBinder.class);

	private Mod3902014 mod390;

	IMod3902014CallBack callback;
	
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
	
	public Page08() {
		Widget ui = BINDER.createAndBindUi(this);
		initWidget(ui);
		box108.setEnabled(false);
	}

	public void setValue(Mod3902014 m390) {
		this.mod390 = m390;
		box99.setValue(this.mod390.getBox99());
		box653.setValue(this.mod390.getBox653());
		box103.setValue(this.mod390.getBox103());
		box104.setValue(this.mod390.getBox104());
		box105.setValue(this.mod390.getBox105());
		box110.setValue(this.mod390.getBox110());
		box112.setValue(this.mod390.getBox112());
		box100.setValue(this.mod390.getBox100());
		box101.setValue(this.mod390.getBox101());
		box102.setValue(this.mod390.getBox102());
		box227.setValue(this.mod390.getBox227());
		box228.setValue(this.mod390.getBox228());
		box106.setValue(this.mod390.getBox106());
		box107.setValue(this.mod390.getBox107());
		box108.setValue(this.mod390.getBox108());
	}

	public void populate(Mod3902014 mod390) {
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
				mod390.setBox99(box99.getValue());
				callback.calculateAndRefresh();
			}
		});
	}

	@UiHandler("box653")
	void onChangeBox653(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox653(box653.getValue());
				callback.calculateAndRefresh();
			}
		});
	}

	@UiHandler("box103")
	void onChangeBox103(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox103(box103.getValue());
				callback.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box104")
	void onChangeBox104(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox104(box104.getValue());
				callback.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box105")
	void onChangeBox105(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox105(box105.getValue());
				callback.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box110")
	void onChangeBox110(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox110(box110.getValue());
				callback.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box112")
	void onChangeBox112(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox112(box112.getValue());
				callback.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box100")
	void onChangeBox100(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox100(box100.getValue());
				callback.calculateAndRefresh();
			}
		});
	}

	@UiHandler("box101")
	void onChangeBox101(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox101(box101.getValue());
				callback.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box102")
	void onChangeBox102(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox102(box102.getValue());
				callback.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box227")
	void onChangeBox227(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox227(box227.getValue());
				callback.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box228")
	void onChangeBox228(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox228(box228.getValue());
				callback.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box106")
	void onChangeBox106(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox106(box106.getValue());
				callback.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box107")
	void onChangeBox107(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox107(box107.getValue());
				callback.calculateAndRefresh();
			}
		});
	}

	public void setCallback(IMod3902014CallBack callback) {
		this.callback = callback;
	}
	
}

