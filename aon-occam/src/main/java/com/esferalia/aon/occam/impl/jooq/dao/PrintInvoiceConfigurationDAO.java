package com.esferalia.aon.occam.impl.jooq.dao;

import java.util.Date;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceTheme;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class PrintInvoiceConfigurationDAO {
	
	private PrintInvoiceConfigurationDAO() {

	}
	
	public static PrintInvoiceConfiguration get(AONContext ctx, Boolean withData) {
		ctx.checkRead();
		PrintInvoiceConfiguration config = new PrintInvoiceConfiguration();
		AppParamDAO.getApplicationParameterStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())	
			.and(f.getNameProperty().like("INVOICE_PRINT_CONFIG%")))
			.forEach(r -> {
				if(AppParam.INVOICE_PRINT_CONFIG_ADJUST.toString().equals(r.getName())) {
					config.setAdjustImage(r.getValue() != null && (r.getValue().equalsIgnoreCase("true") || r.getValue().equals("1")));
				} else if(AppParam.INVOICE_PRINT_CONFIG_DETAILED.toString().equals(r.getName())) {
					config.setDetailed(r.getValue() != null && (r.getValue().equalsIgnoreCase("true") || r.getValue().equals("1")));
				} else if(AppParam.INVOICE_PRINT_CONFIG_HEADER.toString().equals(r.getName())) {
					config.setHeader(AonNumberUtils.toInteger(r.getValue()));
				} else if(AppParam.INVOICE_PRINT_CONFIG_FOOTER.toString().equals(r.getName())) {
					config.setFooter(AonNumberUtils.toInteger(r.getValue()));
				} else if(AppParam.INVOICE_PRINT_CONFIG_LOGO.toString().equals(r.getName())) {
					config.setLogo(r.getValue() != null && (r.getValue().equalsIgnoreCase("true") || r.getValue().equals("1")));
				} else if(AppParam.INVOICE_PRINT_CONFIG_COMPANY.toString().equals(r.getName())) {
					config.setCompany(r.getValue() != null && (r.getValue().equalsIgnoreCase("true") || r.getValue().equals("1")));
				} else if(AppParam.INVOICE_PRINT_CONFIG_RECORD_DATA.toString().equals(r.getName())) {
					config.setRecordData(r.getValue() != null && (r.getValue().equalsIgnoreCase("true") || r.getValue().equals("1")));
				} else if(AppParam.INVOICE_PRINT_CONFIG_CONTACT.toString().equals(r.getName())) {
					config.setContactData(r.getValue() != null && (r.getValue().equalsIgnoreCase("true") || r.getValue().equals("1")));
				} else if(AppParam.INVOICE_PRINT_CONFIG_LANGUAGE.toString().equals(r.getName())) {
					config.setLanguage(AonLanguage.safeValueOf(r.getValue()));
				} else if(AppParam.INVOICE_PRINT_CONFIG_BORDER.toString().equals(r.getName())) {
					config.setBorder(AonNumberUtils.isNumber(r.getValue()) ? Integer.parseInt(r.getValue()) : 0);
				} else if(AppParam.INVOICE_PRINT_CONFIG_THEME.toString().equals(r.getName())) {
					config.getTheme().setTheme(r.getValue() != null ? PrintInvoiceTheme.safeValueOf(Integer.parseInt(r.getValue())) : PrintInvoiceTheme.BLACK_AND_WHITE);
				} else if(AppParam.INVOICE_PRINT_CONFIG_THEME_BTBC.toString().equals(r.getName())) {
					config.getTheme().setBoxTitleBackgroundColor(r.getValue());
				} else if(AppParam.INVOICE_PRINT_CONFIG_THEME_BTTC.toString().equals(r.getName())) {
					config.getTheme().setBoxTitleTextColor(r.getValue());
				} else if(AppParam.INVOICE_PRINT_CONFIG_THEME_BBBC.toString().equals(r.getName())) {
					config.getTheme().setBoxBodyBackgroundColor(r.getValue());
				} else if(AppParam.INVOICE_PRINT_CONFIG_THEME_BBC.toString().equals(r.getName())) {
					config.getTheme().setBorderColor(r.getValue());
				} else if(AppParam.INVOICE_PRINT_CONFIG_THEME_TTC.toString().equals(r.getName())) {
					config.getTheme().setTitleTextColor(r.getValue());
				} else if(AppParam.INVOICE_PRINT_CONFIG_THEME_TC.toString().equals(r.getName())) {
					config.getTheme().setTextColor(r.getValue());
				} else if(AppParam.INVOICE_PRINT_CONFIG_THEME_CBC.toString().equals(r.getName())) {
					config.getTheme().setCustomerBackgroundColor(r.getValue());
				}				
			});
		
		ApplicationParameter saleInvoice = AppParamDAO.getApplicationParameterStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()).and(f.getNameProperty().eq(AppParam.APP_SALE_INVOICE_TEMPLATE_PARAM.name()))).findFirst().orElse(new ApplicationParameter());		
		config.setActive(AonStringUtils.isBlank(saleInvoice.getValue()) || saleInvoice.getValue().equalsIgnoreCase("default"));
		Attach attach = AttachmentDAO.getDataAttachStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()).and(f.getSourceTypeProperty().eq(DataAttachSource.INVOICE_PRINT_CONFIGURATION.value())), withData)
				.findFirst().orElse(new Attach());
		config.setBackground(attach);
		
		Attach legalAttach = AttachmentDAO.getRegistryAttachStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getTypeProperty().eq(RegistryAttachmentType.INVOICE_FOOTER_TEXT.value())),
			true).findFirst().orElse(new Attach());	
			
		if(legalAttach.getData() != null) {
			config.setLegal(new String(legalAttach.getData()));
		}
		
		return config;	
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
		
		AppParamDAO.insertApplicationParameter(ctx,
				AppParam.INVOICE_PRINT_CONFIG_BORDER.toString(),
				Integer.toString(pic.getBorder()));
		
		AppParamDAO.insertApplicationParameter(ctx,
				AppParam.INVOICE_PRINT_CONFIG_THEME.toString(),
				pic.getTheme().getTheme().value().toString());
		
		if(pic.getTheme().isPersonalized()) {
			AppParamDAO.insertApplicationParameter(ctx,
					AppParam.INVOICE_PRINT_CONFIG_THEME_BTBC.toString(),
					pic.getTheme().getBoxTitleBackgroundColorHTML());
			
			AppParamDAO.insertApplicationParameter(ctx,
					AppParam.INVOICE_PRINT_CONFIG_THEME_BTTC.toString(),
					pic.getTheme().getBoxTitleTextColorHTML());
			
			AppParamDAO.insertApplicationParameter(ctx,
					AppParam.INVOICE_PRINT_CONFIG_THEME_BBBC.toString(),
					pic.getTheme().getBoxBodyBackgroundColorHTML());
			
			AppParamDAO.insertApplicationParameter(ctx,
					AppParam.INVOICE_PRINT_CONFIG_THEME_BBC.toString(),
					pic.getTheme().getBorderColorHTML());
			
			AppParamDAO.insertApplicationParameter(ctx,
					AppParam.INVOICE_PRINT_CONFIG_THEME_TTC.toString(),
					pic.getTheme().getTitleTextColorHTML());
		
			AppParamDAO.insertApplicationParameter(ctx,
					AppParam.INVOICE_PRINT_CONFIG_THEME_TC.toString(),
					pic.getTheme().getTextColorHTML());
			
			AppParamDAO.insertApplicationParameter(ctx,
					AppParam.INVOICE_PRINT_CONFIG_THEME_CBC.toString(),
					pic.getTheme().getCustomerBackgroundColorHTML());
		}
		
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
		
		Attach legalAttach = AttachmentDAO.getRegistryAttachStream(ctx, f -> 
			f.getDomainProperty().eq(ctx.getDomainId())
			.and(f.getTypeProperty().eq(RegistryAttachmentType.INVOICE_FOOTER_TEXT.value())),
			false).findFirst().orElse(new Attach());

		legalAttach.setDescription("Texto en pie de F.Venta");
		legalAttach.setData(!AonStringUtils.isBlank(pic.getLegal()) 
				? pic.getLegal().getBytes() : "".getBytes());
		if(legalAttach.getId() != null)
			AttachmentDAO.updateRegistryAttach(ctx, legalAttach);
		else {
			Company company = CompanyDAO.getCompany(ctx, ctx.getDomainId());
			legalAttach.setAttachModule(company.getId());
			legalAttach.setDate(new Date());
			legalAttach.setDomain(new Domain().setId(ctx.getDomainId()));
			legalAttach.setType(RegistryAttachmentType.INVOICE_FOOTER_TEXT.value());
			legalAttach.setMimeType(MimeType.TXT);
			AttachmentDAO.insertRegistryAttach(ctx, legalAttach);
		}
		
		return pic;
	}
	
}




