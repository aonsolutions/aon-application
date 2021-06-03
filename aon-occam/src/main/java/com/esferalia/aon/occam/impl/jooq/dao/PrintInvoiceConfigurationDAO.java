package com.esferalia.aon.occam.impl.jooq.dao;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class PrintInvoiceConfigurationDAO {
	
	public static PrintInvoiceConfiguration get(AONContext ctx, Boolean withData) {
		ctx.checkRead();
		ApplicationParameter adjust = AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())	
			.and(f.getNameProperty().eq(AppParam.INVOICE_PRINT_CONFIG_ADJUST.toString())))
			.findFirst().orElse(new ApplicationParameter());
		
		ApplicationParameter detailed = AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())	
			.and(f.getNameProperty().eq(AppParam.INVOICE_PRINT_CONFIG_DETAILED.toString())))
			.findFirst().orElse(new ApplicationParameter());
		
		ApplicationParameter header = AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())	
			.and(f.getNameProperty().eq(AppParam.INVOICE_PRINT_CONFIG_HEADER.toString())))
			.findFirst().orElse(new ApplicationParameter());
		
		
		ApplicationParameter footer = AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())	
			.and(f.getNameProperty().eq(AppParam.INVOICE_PRINT_CONFIG_FOOTER.toString())))
			.findFirst().orElse(new ApplicationParameter());

		Attach attach = AttachmentDAO.getDataAttachStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()).and(f.getSourceTypeProperty().eq(DataAttachSource.INVOICE_PRINT_CONFIGURATION.value())), withData)
				.findFirst().orElse(new Attach());
			

		
		return new PrintInvoiceConfiguration()
				.setAdjustImage(adjust.getValue() != null && (adjust.getValue().equalsIgnoreCase("true") || adjust.getValue().equals("1")))
				.setDetailed(detailed.getValue() != null && (detailed.getValue().equalsIgnoreCase("true") || detailed.getValue().equals("1")))
				.setHeader(AonNumberUtils.toInteger(header.getValue()))
				.setFooter(AonNumberUtils.toInteger(footer.getValue()))
				.setBackground(attach);
		
	}

	public static PrintInvoiceConfiguration save(AONContext ctx, PrintInvoiceConfiguration pic) {
		ctx.checkWrite();	

		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.INVOICE_PRINT_CONFIG_ADJUST.toString(),
				pic.getAdjustImage().toString());
		
		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.INVOICE_PRINT_CONFIG_DETAILED.toString(),
				pic.getDetailed().toString());
		
		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.INVOICE_PRINT_CONFIG_HEADER.toString(),
				pic.getHeader().toString());
		
		AppParamDAO.insertApplicationParameter(ctx,
				AppParam.INVOICE_PRINT_CONFIG_FOOTER.toString(),
				pic.getFooter().toString());
		
		if(pic.getBackground() != null && pic.getBackground().getData() != null) {
			Attach attach = AttachmentDAO.getDataAttachStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()).and(f.getSourceTypeProperty().eq(DataAttachSource.INVOICE_PRINT_CONFIGURATION.value())), false)
					.findFirst().orElse(new Attach());
			pic.getBackground()
				.setId(attach.getId())
				.setDomain(new Domain().setId(ctx.getDomainId()));
			if(attach.getId() != null) {
				AttachmentDAO.updateDataAttach(ctx, pic.getBackground());
			} else AttachmentDAO.insertDataAttach(ctx, pic.getBackground());
		}
		return pic;
	}
	
}




