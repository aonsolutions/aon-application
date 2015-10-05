package com.esferalia.aon.gwt.fiscal.client.mod390;

import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.Model390.Mod390CallBack;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
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

public class Page10 extends ResizeComposite implements RequiresResize {

	interface Page7Binder extends UiBinder<Widget, Page10> {
	}

	private static final Page7Binder page7Binder = GWT
			.create(Page7Binder.class);

	private Mod390 mod390;

	Mod390CallBack callback;
	
	@UiField
	DoubleTextBox box99;
	
	@UiField
	DoubleTextBox box653;

	@UiField
	DoubleTextBox box103;
	
	@UiField
	DoubleTextBox box104;
	
	@UiField
	DoubleTextBox box105;
	
	@UiField
	DoubleTextBox box110;
	
	@UiField
	DoubleTextBox box112;
	
	@UiField
	DoubleTextBox box100;
	
	@UiField
	DoubleTextBox box101;
	
	@UiField
	DoubleTextBox box102;
	
	@UiField
	DoubleTextBox box227;
	
	@UiField
	DoubleTextBox box228;
	
	@UiField
	DoubleTextBox box106;
	
	@UiField
	DoubleTextBox box107;
	
	@UiField
	DoubleTextBox box108;
	
	public Page10() {
		Widget ui = page7Binder.createAndBindUi(this);
		initWidget(ui);
		box108.setEnabled(false);
	}

	public void setValue(Mod390 m390) {
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

	public void populate(Mod390 mod390) {
		mod390.setBox99(box99.getDoubleValue());
		mod390.setBox653(box653.getDoubleValue());
		mod390.setBox103(box103.getDoubleValue());
		mod390.setBox104(box104.getDoubleValue());
		mod390.setBox105(box105.getDoubleValue());
		mod390.setBox110(box110.getDoubleValue());
		mod390.setBox112(box112.getDoubleValue());
		mod390.setBox100(box100.getDoubleValue());
		mod390.setBox101(box101.getDoubleValue());
		mod390.setBox102(box102.getDoubleValue());
		mod390.setBox227(box227.getDoubleValue());
		mod390.setBox228(box228.getDoubleValue());
		mod390.setBox106(box106.getDoubleValue());
		mod390.setBox107(box107.getDoubleValue());
		mod390.setBox108(box108.getDoubleValue());
	}
	
	@UiHandler("box99")
	void onChangeBox99(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox99(box99.getDoubleValue());
				callback.calculateAndRefresh();
			}
		});
	}

	@UiHandler("box653")
	void onChangeBox653(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox653(box653.getDoubleValue());
				callback.calculateAndRefresh();
			}
		});
	}

	@UiHandler("box103")
	void onChangeBox103(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox103(box103.getDoubleValue());
				callback.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box104")
	void onChangeBox104(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox104(box104.getDoubleValue());
				callback.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box105")
	void onChangeBox105(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox105(box105.getDoubleValue());
				callback.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box110")
	void onChangeBox110(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox110(box110.getDoubleValue());
				callback.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box112")
	void onChangeBox112(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox112(box112.getDoubleValue());
				callback.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box100")
	void onChangeBox100(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox100(box100.getDoubleValue());
				callback.calculateAndRefresh();
			}
		});
	}

	@UiHandler("box101")
	void onChangeBox101(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox101(box101.getDoubleValue());
				callback.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box102")
	void onChangeBox102(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox102(box102.getDoubleValue());
				callback.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box227")
	void onChangeBox227(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox227(box227.getDoubleValue());
				callback.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box228")
	void onChangeBox228(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox228(box228.getDoubleValue());
				callback.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box106")
	void onChangeBox106(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox106(box106.getDoubleValue());
				callback.calculateAndRefresh();
			}
		});
	}
	
	@UiHandler("box107")
	void onChangeBox107(ChangeEvent event) {
		Scheduler.get().scheduleDeferred( new ScheduledCommand() {
			@Override
			public void execute() {
				mod390.setBox107(box107.getDoubleValue());
				callback.calculateAndRefresh();
			}
		});
	}

	public void setCallback(Mod390CallBack callback) {
		this.callback = callback;
	}
	
}

