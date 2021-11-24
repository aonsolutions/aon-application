package com.esferalia.aon.occam.impl.jooq.dao;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceTheme;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceThemeConfiguration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class PrintInvoiceConfigurationDAO {
	
	private PrintInvoiceConfigurationDAO() {

	}
	
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

		ApplicationParameter logo = AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())	
			.and(f.getNameProperty().eq(AppParam.INVOICE_PRINT_CONFIG_LOGO.toString())))
			.findFirst().orElse(new ApplicationParameter());

		ApplicationParameter company = AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())	
			.and(f.getNameProperty().eq(AppParam.INVOICE_PRINT_CONFIG_COMPANY.toString())))
			.findFirst().orElse(new ApplicationParameter());
		
		ApplicationParameter recordData = AppParamDAO.getApplicationParameterStream(ctx, f -> 
		f.getDomainProperty().eq(ctx.getDomainId())	
		.and(f.getNameProperty().eq(AppParam.INVOICE_PRINT_CONFIG_RECORD_DATA.toString())))
		.findFirst().orElse(new ApplicationParameter());
		
		ApplicationParameter contactData = AppParamDAO.getApplicationParameterStream(ctx, f -> 
		f.getDomainProperty().eq(ctx.getDomainId())	
		.and(f.getNameProperty().eq(AppParam.INVOICE_PRINT_CONFIG_CONTACT.toString())))
		.findFirst().orElse(new ApplicationParameter());
		
		ApplicationParameter language = AppParamDAO.getApplicationParameterStream(ctx, f -> 
		f.getDomainProperty().eq(ctx.getDomainId())	
		.and(f.getNameProperty().eq(AppParam.INVOICE_PRINT_CONFIG_LANGUAGE.toString())))
		.findFirst().orElse(new ApplicationParameter());
		
		ApplicationParameter theme = AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())	
			.and(f.getNameProperty().eq(AppParam.INVOICE_PRINT_CONFIG_THEME.toString())))
			.findFirst().orElse(new ApplicationParameter());
		
		PrintInvoiceTheme t = theme.getValue() != null ? PrintInvoiceTheme.safeValueOf(Integer.parseInt(theme.getValue())) : PrintInvoiceTheme.BLACK_AND_WHITE;
		PrintInvoiceThemeConfiguration tc = new PrintInvoiceThemeConfiguration(t);
		if(tc.isPersonalized()) {
			AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())	
			.and(f.getNameProperty().like("INVOICE_PRINT_CONFIG_THEME%")))
			.forEach(r -> {
				if(AppParam.INVOICE_PRINT_CONFIG_THEME_BTBC.toString().equals(r.getName())) {
					tc.setBoxTitleBackgroundColor(r.getValue());
				}
				if(AppParam.INVOICE_PRINT_CONFIG_THEME_BTTC.toString().equals(r.getName())) {
					tc.setBoxTitleTextColor(r.getValue());
				}
				if(AppParam.INVOICE_PRINT_CONFIG_THEME_BTB.toString().equals(r.getName())) {
					tc.setBoxTitleBorder(r.getValue() != null && (r.getValue().equalsIgnoreCase("true") || r.getValue().equals("1")));
				}
				if(AppParam.INVOICE_PRINT_CONFIG_THEME_BBBC.toString().equals(r.getName())) {
					tc.setBoxBodyBackgroundColor(r.getValue());
				}
				if(AppParam.INVOICE_PRINT_CONFIG_THEME_BBTC.toString().equals(r.getName())) {
					tc.setBoxBodyTextColor(r.getValue());
				}
				if(AppParam.INVOICE_PRINT_CONFIG_THEME_BBB.toString().equals(r.getName())) {
					tc.setBoxBodyBorder(r.getValue() != null && (r.getValue().equalsIgnoreCase("true") || r.getValue().equals("1")));
				}
				if(AppParam.INVOICE_PRINT_CONFIG_THEME_TC.toString().equals(r.getName())) {
					tc.setTextColor(r.getValue());
				}
				if(AppParam.INVOICE_PRINT_CONFIG_THEME_CBC.toString().equals(r.getName())) {
					tc.setCustomerBackgroundColor(r.getValue());
				}
			});
		}
		
		Attach attach = AttachmentDAO.getDataAttachStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()).and(f.getSourceTypeProperty().eq(DataAttachSource.INVOICE_PRINT_CONFIGURATION.value())), withData)
				.findFirst().orElse(new Attach());
			

		
		return new PrintInvoiceConfiguration()
				.setAdjustImage(adjust.getValue() != null && (adjust.getValue().equalsIgnoreCase("true") || adjust.getValue().equals("1")))
				.setDetailed(detailed.getValue() != null && (detailed.getValue().equalsIgnoreCase("true") || detailed.getValue().equals("1")))
				.setHeader(AonNumberUtils.toInteger(header.getValue()))
				.setFooter(AonNumberUtils.toInteger(footer.getValue()))
				.setLogo(logo.getValue() != null && (logo.getValue().equalsIgnoreCase("true") || logo.getValue().equals("1")))
				.setCompany(company.getValue() != null && (company.getValue().equalsIgnoreCase("true") || company.getValue().equals("1")))
				.setBackground(attach)
				.setRecordData(recordData.getValue() != null && (recordData.getValue().equalsIgnoreCase("true") || recordData.getValue().equals("1")))
				.setContactData(contactData.getValue() != null && (contactData.getValue().equalsIgnoreCase("true") || contactData.getValue().equals("1")))
				.setLanguage(AonLanguage.safeValueOf(language.getValue()))
				.setTheme(tc);
		
	}

	public static PrintInvoiceConfiguration save(AONContext ctx, PrintInvoiceConfiguration pic) {
		ctx.checkWrite();	

		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.INVOICE_PRINT_CONFIG_ADJUST.toString(),
				Boolean.toString(pic.getAdjustImage()));
		
		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.INVOICE_PRINT_CONFIG_DETAILED.toString(),
				Boolean.toString(pic.isDetailed()));
		
		AppParamDAO.insertApplicationParameter(ctx, 
				AppParam.INVOICE_PRINT_CONFIG_HEADER.toString(),
				pic.getHeader().toString());
		
		AppParamDAO.insertApplicationParameter(ctx,
				AppParam.INVOICE_PRINT_CONFIG_FOOTER.toString(),
				pic.getFooter().toString());
		
		AppParamDAO.insertApplicationParameter(ctx,
				AppParam.INVOICE_PRINT_CONFIG_LOGO.toString(),
				Boolean.toString(pic.isLogo()));
		
		AppParamDAO.insertApplicationParameter(ctx,
				AppParam.INVOICE_PRINT_CONFIG_COMPANY.toString(),
				Boolean.toString(pic.isCompany()));
		
		AppParamDAO.insertApplicationParameter(ctx,
				AppParam.INVOICE_PRINT_CONFIG_RECORD_DATA.toString(),
				Boolean.toString(pic.isRecordData()));
		
		AppParamDAO.insertApplicationParameter(ctx,
				AppParam.INVOICE_PRINT_CONFIG_CONTACT.toString(),
				Boolean.toString(pic.isContactData()));
		
		
		AppParamDAO.insertApplicationParameter(ctx,
				AppParam.INVOICE_PRINT_CONFIG_LANGUAGE.toString(),
				pic.getLanguage().getLanguage());
		
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




