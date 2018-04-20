package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.MkTemplate.MK_TEMPLATE;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.MailTemplateFilter;
import com.esferalia.aon.occam.api.model.MailTemplate;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.MailTemplateFiller;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.MailTemplatePropertiesDAO;


public class MailDAO {
	
	private static final MailTemplatePropertiesDAO MAIL_TEMPLATE_PROPERTIES = new MailTemplatePropertiesDAO();

	public static MailTemplate getMailTemplate(AONContext ctx, MailTemplateFilter filter) {
		return ctx.getDslContext().select()
				.from(MK_TEMPLATE)
				.where(MAIL_TEMPLATE_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new MailTemplateFiller()).findFirst().orElse(new MailTemplate());
	}
	
}
