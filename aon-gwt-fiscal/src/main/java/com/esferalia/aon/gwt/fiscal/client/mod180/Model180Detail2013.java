package com.esferalia.aon.gwt.fiscal.client.mod180;

import static com.esferalia.aon.gwt.fiscal.client.mod180.Model180.AON_RESOURCES;
import static com.esferalia.aon.gwt.fiscal.client.mod180.Model180.MSG;

import java.util.Map;

import com.esferalia.aon.gwt.common.client.i18n.DialogMessages;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerTextBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.esferalia.aon.occam.api.model.Mod180Detail;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Model180Detail2013 extends ResizeComposite {

	interface Model180Detail2013Binder extends UiBinder<Widget, Model180Detail2013> {}
	private static Model180Detail2013Binder MODEL180_DETAIL_2013_BINDER 
		= GWT.create(Model180Detail2013Binder.class);

	static interface ICallBack {
		Map<Integer, Mod180Detail> getModified();
		void redrawList( Mod180Detail detail);
		void delete( Mod180Detail detail);
		void restore( Mod180Detail detail);
	}
	
	Mod180Detail detail;
	private ICallBack callback;
	
	@UiField
	Button deleteDetailButton;
	@UiField
	Button restoreDeletedButton;

	@UiField
	DocumentTextBox receiverDocument;
	@UiField
	DocumentTextBox representativeDocument;
	@UiField
	TextBox fullName;
	@UiField
	IntegerTextBox accrualYear;
	@UiField
	ProvinceListBox province;
	@UiField
	DoubleTextBox perception;
	@UiField
	DoubleTextBox retention;
	@UiField
	DoubleTextBox percent;
	@UiField
	CheckBox inKind;
	
	public Model180Detail2013() {
		Widget ui = MODEL180_DETAIL_2013_BINDER.createAndBindUi(this);
		initWidget(ui);
	}

	public void setCallback(ICallBack callback) {
		this.callback = callback;
	}
	
	public void setDetail(Mod180Detail detail) {
		this.detail = detail;
	}
	
	public void populatePerceptor(Mod180Detail detail) {
		receiverDocument.setValue(detail.getDocument());
		representativeDocument.setValue(detail.getRepresentativeDocument());
		fullName.setValue(detail.getName());
		accrualYear.setValue(detail.getAccrualYear());
		province.setSelectedIndex(detail.getProvince());
		perception.setValue(detail.getPerception());
		retention.setValue(detail.getRetention());
		percent.setValue(detail.getPercent());
		inKind.setValue(detail.isInKind());
		restoreDeletedButton.setVisible(detail.isDeleted());
		deleteDetailButton.setVisible(!detail.isDeleted());
	}

	// -------------------------------------------------------------- UiHandler
	private Mod180Detail getModifiedPerceptor(final IPerceptorChanged pc) {
		if (!callback.getModified().containsKey(detail.getId())) {
			Model180.mod180Service.getMod180Detail(Model180.getCurrentDomainName()
					,Model180.getCurrentDomain(),detail.getId(),
					new AsyncCallback<Mod180Detail>() {
						@Override
						public void onSuccess(Mod180Detail per) {
							if (per == null) {
								DialogMessages.alertErrorWidget(
										MSG.unableToFindMod180Detail(
										MSG.unableToFindPerceptor(detail.getId())));
							} else {
								callback.getModified().put(detail.getId(), per);
								pc.perceptorChanged(per);
								if (callback != null) callback.redrawList(detail);
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							DialogMessages.alertErrorWidget(MSG
									.unableToFindMod180Detail(caught
											.getMessage()));
						}
					});

		} else {
			pc.perceptorChanged(callback.getModified().get(detail.getId()));
			if (callback != null) callback.redrawList(detail);
		}
		return callback.getModified().get(detail.getId());
	}
	
	interface IPerceptorChanged {
		void perceptorChanged(Mod180Detail p);
	}

	@UiHandler("receiverDocument")
	void onChangeReceiverDocument(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod180Detail p) {
				p.setDocument(receiverDocument.getValue());
				p.setDirty(true);
				detail.setDirty(true);
			}
		});
	}

	@UiHandler("representativeDocument")
	void onChangeRepresentativeDocument(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod180Detail p) {
				p.setRepresentativeDocument(representativeDocument.getValue());
				p.setDirty(true);
				detail.setDirty(true);
			}
		});
	}

	@UiHandler("fullName")
	void onChangeFullName(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod180Detail p) {
				p.setName(fullName.getValue());
				p.setDirty(true);
				detail.setDirty(true);
				detail.setName(fullName.getValue());
			}
		});
	}

	@UiHandler("accrualYear")
	void onChangeAccrualYear(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod180Detail p) {
				try {
					p.setAccrualYear(accrualYear.getIntValue());
					p.setDirty(true);
					detail.setDirty(true);
				} catch (NumberFormatException e) {
					accrualYear.addStyleName(AON_RESOURCES.css()
							.aonTextBoxError());
				}
			}
		});
	}

	@UiHandler("province")
	void onChangeProvince(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod180Detail p) {
				p.setProvince(province.getSelectedIndex());
				p.setDirty(true);
				detail.setDirty(true);
			}
		});
	}

	@UiHandler("perception")
	void onChangePerception(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod180Detail p) {
				p.setPerception(perception.getDoubleValue());
				p.setDirty(true);
				detail.setDirty(true);
			}
		});
	}

	@UiHandler("retention")
	void onChangeRetention(ChangeEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod180Detail p) {
				p.setRetention(retention.getDoubleValue());
				p.setDirty(true);
				detail.setDirty(true);
			}
		});
	}

	@UiHandler("percent")
	void onChangePercent(ChangeEvent event) {
		double ret = retention.getDoubleValue();
		boolean retChanged = false;
		if (ret == 0) {
			ret = AonUtil.round(perception.getDoubleValue()
					* percent.getDoubleValue() / 100);
			retention.setValue(ret);
			retChanged = true;
		}
		final boolean retentionChanged = retChanged;		
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod180Detail p) {
				p.setPercent(percent.getDoubleValue());
				if (retentionChanged) {
					p.setRetention(retention.getDoubleValue());
				}
				p.setDirty(true);
				detail.setDirty(true);
			}
		});
	}

	@UiHandler("inKind")
	void onChangeInKind(ClickEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod180Detail p) {
				p.setInKind(inKind.getValue());
				p.setDirty(true);
				detail.setDirty(true);
			}
		});
	}
	
	@UiHandler("deleteDetailButton")
	void onDeleteDetailButtonClick(ClickEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod180Detail p) {
				p.setDeleted(true);
				if (callback != null) callback.delete(detail);
				restoreDeletedButton.setVisible(true);
				deleteDetailButton.setVisible(false);
				if (callback != null) callback.redrawList(detail);
			}
		});
	}

	@UiHandler("restoreDeletedButton")
	void onRestoreDeletedButtonClick(ClickEvent event) {
		getModifiedPerceptor(new IPerceptorChanged() {
			@Override
			public void perceptorChanged(Mod180Detail p) {
				p.setDeleted(false);
				if (!p.isDirty()) {
					if (callback != null) callback.restore(detail);
					restoreDeletedButton.setVisible(false);
					deleteDetailButton.setVisible(true);
					if (callback != null) callback.redrawList(detail);
				}
			}
		});
	}
	
}
