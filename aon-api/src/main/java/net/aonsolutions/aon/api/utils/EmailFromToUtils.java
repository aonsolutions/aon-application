package net.aonsolutions.aon.api.utils;

import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.security.TaskHolderWorkgroup;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryMediaDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderWorkgroupDAO;
import com.esferalia.aon.watson.util.AonStringUtils;

public class EmailFromToUtils {
	
	private CloseableAONContext ctx;
	private MarketingAction marketingAction;
	
	public EmailFromToUtils(CloseableAONContext ctx) {
		this.ctx = ctx;
	}
	
	public List<String> getEmails(EmailFromToType type){
		
		switch (type) {
			case MARKETING_ACTION_EMAILS: 
				return getMarketingActionEmails();
			default:
				throw new IllegalArgumentException("Unexpected value: " + type);
		}
		
	}
	
	private List<String> getMarketingActionEmails() {
		List<String> emails = new ArrayList<String>();
		
		TaskHolder mkActionTH = marketingAction.getTaskHolder();
		Workgroup mkActionWG = marketingAction.getWorkgroup();
		
		// Si existe responsable se manda una copia del mail
		
		if(null != mkActionTH && null != mkActionTH.getId()) {
			
			RegistryMedia sellerSupportMedia = RegistryMediaDAO.get(ctx, f -> f.getRegistryProperty().eq(mkActionTH.getId()).and(f.getMediaProperty().eq(MediaType.EMAIL.value())));
			
			if(null != sellerSupportMedia.getId() && AonStringUtils.isNotBlank(sellerSupportMedia.getValue()))
				emails.add(sellerSupportMedia.getValue());
		
		// Si no existe responsable se manda una copia a TODOS los integrantes del GT
		
		} else if(null != mkActionWG && null != mkActionWG.getId()){ 
			
			List<TaskHolderWorkgroup> workgroupTHs = TaskHolderWorkgroupDAO.getList(ctx, f -> f.getWorkgroupProperty().eq(mkActionWG.getId()));
			
			workgroupTHs.forEach(workgroupTH -> {
				RegistryMedia sellerSupportMedia = RegistryMediaDAO.get(ctx, f -> f.getRegistryProperty().eq(workgroupTH.getTaskHolder()).and(f.getMediaProperty().eq(MediaType.EMAIL.value())));
				
				if(null != sellerSupportMedia.getId() && AonStringUtils.isNotBlank(sellerSupportMedia.getValue()))
					emails.add(sellerSupportMedia.getValue());
			});
			
		}
		
		return emails;
	}

	public void setMarketingAction(MarketingAction marketingAction) {
		this.marketingAction = marketingAction;
	}
	
}
