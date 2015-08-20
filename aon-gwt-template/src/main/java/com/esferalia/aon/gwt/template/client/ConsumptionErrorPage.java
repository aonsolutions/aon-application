package com.esferalia.aon.gwt.template.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.template.shared.TemplateInfo;
import com.esferalia.aon.gwt.template.shared.TemplateList;
import com.esferalia.aon.gwt.template.shared.Warehouse;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

public class ConsumptionErrorPage extends ConsumptionPage{

	final ITemplateAsync item = GWT.create(ITemplate.class);

	public ConsumptionErrorPage(Integer domainId, TemplateList templateList) {
		super(domainId, templateList);
		titleLabel.setText(AON.MSG.aggregateCountErrorTemplates());
	}

	//------------------------------ Actions
	
	@Override
	public void download(String type) {
		Integer size = 0;
		TemplateInfo templateInfo = null;
		for (TemplateInfo ti : templateList.getList()) {
			if(ti.getType().equals("Consumo")){
				templateInfo = ti;
				size++;
			}
		}

		String fileDownloadURL = GWT.getModuleBaseURL()+ "/gwt_download_aggregate_consumption/"
            	+ "?id=" + Integer.toString(templateInfo.getId())
            	+ "&domain_id=" + domainId;
		
		Integer i = 0;
		for (Warehouse w : warehouses) {
			fileDownloadURL = fileDownloadURL + "&warehouse" + i + "=" + w.getName()
										+"&warehouse_id"+ i +  "=" + w.getId();
			i++;
		}
		
		
		fileDownloadURL = fileDownloadURL + "&size=" + warehouses.size()
							+ "&detail=" + detail
							+ "&file_type="+type
							+ "&only_negative=1";	
						
		Window.open( fileDownloadURL, "_blank",null);
	}
}
