package com.esferalia.aon.gwt.payroll.client;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.MainCCCInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class MainCCCObject {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private MainCCCInfo mainCCCInfo;
	
	public MainCCCObject() {
		super();
		this.mainCCCInfo = new MainCCCInfo();
	}
	
	public void getMainCCCInfo(Consumer<MainCCCInfo> success, Consumer<Throwable> failure) {
		impl.getMainCCCInfoDataBase(new AsyncCallback<MainCCCInfo>() {
			
			@Override
			public void onSuccess(MainCCCInfo mainCCCInfoResult) {
				mainCCCInfo = mainCCCInfoResult;
				success.accept(mainCCCInfoResult);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void setMainCCCInfo(Consumer<Void> success, Consumer<Throwable> failure) {
		impl.setMainCCCInfoDataBase(this.mainCCCInfo, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				getMainCCCInfo(s -> {
					success.accept(result);
				}, f -> {});
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public MainCCCInfo getMainCCCInfo() {
		return this.mainCCCInfo;
	}
	
	public Collection<CCCInfo> getCCCs() {
		return this.mainCCCInfo.getCccs().values();
	}
	
	public Set<Entry<Integer, String>> getActivities() {
		return this.mainCCCInfo.getActivities().entrySet();
	}

	public void insertCCC(Integer cccId, int activityId, byte cccRegimeType, String cccRegimeCode, String ccc, String province, String provinceCode) {
		this.mainCCCInfo.insertCCC(cccId, activityId, cccRegimeType, cccRegimeCode, ccc, province, provinceCode);
	}

	public void deleteCCC(Integer cccId) {
		this.mainCCCInfo.deleteCCC(cccId);
	}
		
}
