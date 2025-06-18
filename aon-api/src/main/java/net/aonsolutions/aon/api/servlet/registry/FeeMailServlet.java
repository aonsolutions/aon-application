package net.aonsolutions.aon.api.servlet.registry;

import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.LOGO;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.SIGNATURE;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.payroll.Enterprise;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.impl.jooq.dao.AttachmentDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.EnterpriseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FeeDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryMediaDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

@SuppressWarnings("serial")
@WebServlet(name = "FeeMailServlet", urlPatterns = { "/ms/api/fee-mail/*" })
public class FeeMailServlet extends AonApiHttpServlet {

	private static final Logger LOGGER = Logger.getLogger(FeeMailServlet.class.getName());

	public static final String SEND_FEE_MAIL = "/";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());

		try {

			AonApiData api = initialize(req, false);
			Object object = new AonRouting(api)
					.addRoute(SEND_FEE_MAIL, FeeMailServlet::sendMail)
					.apply();

			response(req, resp, object);

		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	// ---------------------------------------------------------------------------------------------
	// CREATE ENTERPRISE
	// ---------------------------------------------------------------------------------------------

	public static JSONObject sendMail(AonApiData api) {
		JSONObject result = new JSONObject();
		
		try (CloseableAONContext ctx = AONContext.getAONContext(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin())) {
			ctx.transaction(t -> {
				
				JSONObject data = api.getData();
				
				Integer feeId = JsonUtils.getInteger(data, "feeId");
				
				Domain parent = api.getDomain().isParent() 
						? api.getDomain()
						: DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(api.getDomain().getParentId())
					);
				
				String logoUrl = getLogoUrl(api, ctx);

				String from = getFromMessage(api, ctx, parent);
				
				if(AonStringUtils.isBlank(from))
					throw new AonApiException("No existe email definido en el entorno para la creaci\u00f3n de empresas");

				Fee fee = FeeDAO.getFee(ctx, feeId);
				
				Stream<RegistryMedia> feeSellerMedias = RegistryMediaDAO.getStream(ctx, f -> f.getRegistryProperty().eq(fee.getSeller().getId()));
				Optional<RegistryMedia> feeSellerEmailOpt = feeSellerMedias.filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();
				
				if(feeSellerEmailOpt.isEmpty() || AonStringUtils.isBlank(feeSellerEmailOpt.get().getValue()))
					throw new AonApiException("No existe email para el agente de soporte seleccionado");
				
				SESMessage msg = new SESMessage()
						.setFrom(from)
						.setTo(feeSellerEmailOpt.get().getValue())
						.setReplyTo(from)
						.setSubject("Asignac\u00f3n Cuota")
						.setBody(createEnterpriseCreatedBody(logoUrl, parent, from, fee));

				SES.sendEmail(msg);
				
				result.put("message", "Se ha mandado un mail con la cuota al agente asignado");
				
			});
		}
		
		return result;
	}
	
	private static String getLogoUrl(AonApiData api, CloseableAONContext ctx) {
		
		Domain parent = api.getDomain().isParent() 
				? api.getDomain()
				: DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(api.getDomain().getParentId())
			);
		
		Company company = CompanyDAO.getCompanyStream(ctx, f -> f.getDomainProperty().eq(parent.getId())).findFirst().get();
		
		Attach attach = getLogoAttach(api, ctx, company.getId());

		String str = "domain=" + attach.getDomain().getId() + "&id=" + attach.getId() + "&attach_type=registry";
		String result = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
		String logoUrl = null;
		
		Domain attachDomain = DomainDAO.getDomain(ctx, attach.getDomain().getId());
		logoUrl = "https://" + parent.getName() + "/ms/download_attachment/" 
				 	+ attachDomain.getName() + "/" + attach.getCreationUser() + "/" + result;
			
		return logoUrl;
	}

	private static Attach getLogoAttach(AonApiData api, CloseableAONContext ctx, Integer enterpriseId) {
		Optional<Attach> attach1 = AttachmentDAO.getRegistryAttachStream(ctx, f -> f.getTypeProperty().eq(SIGNATURE.value()).and(f.getAttachModuleProperty().eq(enterpriseId)), true).findFirst();

		return attach1.isPresent() ? attach1.get() : AttachmentDAO.getRegistryAttachStream(ctx, f -> f.getTypeProperty().eq(LOGO.value()).and(f.getAttachModuleProperty().eq(enterpriseId)), true).findFirst().get();
	}

	private static String getFromMessage(AonApiData api, CloseableAONContext ctx, Domain parent) {
		DomainUserRoles domainUserRoles = SecurityDAO.getDomainUserRoles(ctx, api.getUser().getId()); 
		
		String from = null;

		if (domainUserRoles.hasParentCustomView() || domainUserRoles.hasCustomView()) {
			// Ya es el dominio padre el que hay en api.getDomain()
			if (null == api.getDomain().getParentId() && (null == parent || null == parent.getId())) {
				Enterprise enterprise = EnterpriseDAO.get(ctx, f -> f.getDomainProperty().eq(api.getDomain().getId()));
				RegistryMedia emailMedia = RegistryMediaDAO.get(ctx, f -> f.getRegistryProperty().eq(enterprise.getId()).and(f.getMediaProperty().eq((byte) 4)));
				if (null != emailMedia && AonStringUtils.isNotBlank(emailMedia.getValue()))
					from = emailMedia.getValue();
				
			// Se busca el dominio padre
			} else if (null != parent && null != parent.getId()) {
				Enterprise enterprise = EnterpriseDAO.get(ctx, f -> f.getDomainProperty().eq(parent.getId()));
				RegistryMedia emailMedia = RegistryMediaDAO.get(ctx, f -> f.getRegistryProperty().eq(enterprise.getId()).and(f.getMediaProperty().eq((byte) 4)));
				if (null != emailMedia && AonStringUtils.isNotBlank(emailMedia.getValue()))
					from = emailMedia.getValue();
			}
		}

		return from;
	}

	private static String createEnterpriseCreatedBody(String logoUrl, Domain parentDomain, String from, Fee fee) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();
		
		VelocityContext context = new VelocityContext();
		context.put("logo", logoUrl);
		context.put("parentName", parentDomain.getDescription());
		
		context.put("customerName", fee.getCustomer().getName());
		context.put("productName", fee.getItem().getProduct().getName());
		context.put("productFullName", fee.getItem().getProduct().getName() + " ( " + fee.getItem().getProduct().getCode() + " )");
		context.put("line", null == fee.getLine() ? "N/D" : fee.getLine().toString());
		context.put("quantity", null == fee.getQuantity() ? "0" : fee.getQuantity().intValue());
		context.put("price", null == fee.getPrice() ? "0.00" : fee.getPrice().toString());
		context.put("discount", AonStringUtils.isBlank(fee.getDiscountExpr()) ? "0.00" : fee.getDiscountExpr());
		context.put("startDate", AonDateUtils.simpleFormat(fee.getStartDate()));
		context.put("endDate", null == fee.getEndDate() ? "" : AonDateUtils.simpleFormat(fee.getEndDate()));
		context.put("chargeDate", AonDateUtils.simpleFormat(fee.getBillingDate()));
		context.put("periocity", null == fee.getPeriod() ? "Sin periodo" : AonStringUtils.isBlank( BillingPeriod.toString(fee.getPeriod()) ) ? "Sin periodo" : BillingPeriod.toString(fee.getPeriod()) );
		
		Template template = engine.getTemplate("/net/aonsolutions/aon/api/servlet/templates/fee_seller_add.vm");

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}

}
