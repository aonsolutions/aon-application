package net.aonsolutions.aon.api.servlet.registry;

import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.LOGO;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.SIGNATURE;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.Advertising;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.GeoZone;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.PayrollWorkplace;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.AonLanguage;
import com.esferalia.aon.occam.api.model.aonsolutions.AonRole;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.payroll.Enterprise;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.Raddinfo;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryRelationship;
import com.esferalia.aon.occam.api.model.registry.RegistrySeller;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.registry.TargetFull;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.api.model.security.UserToolbar;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.AccountPeriodStatus;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.occam.api.model.type.RegistrySellerStatus;
import com.esferalia.aon.occam.api.model.type.RegistrySellerType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.SalesStatus;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.occam.api.model.type.TargetStatus;
import com.esferalia.aon.occam.impl.jooq.dao.AccountPeriodDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AttachmentDAO;
import com.esferalia.aon.occam.impl.jooq.dao.AuthDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.EnterpriseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.FeeDAO;
import com.esferalia.aon.occam.impl.jooq.dao.GeoZoneDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryMediaDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryRelationshipDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistrySellerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SalesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SellerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TargetDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO;
import com.esferalia.aon.occam.impl.jooq.dao.UserDAO;
import com.esferalia.aon.occam.impl.jooq.dao.WorkplaceDAO;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;
import net.aonsolutions.aon.api.servlet.Utils;
import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

@SuppressWarnings("serial")
@WebServlet(name = "RegistryEnterpriseCreationServlet", urlPatterns = { "/ms/api/registry-creation-enterprise/*" })
public class RegistryEnterpriseCreationServlet extends AonApiHttpServlet {

	private static final Logger LOGGER = Logger.getLogger(RegistryEnterpriseCreationServlet.class.getName());

	public static final String CREATE_ENTERPRISE = "/";
	public static final String SYNC_MASSIVE_CUSTOMER_DOMAIN = "/syncMassiveCustomerDomain";

	// Mail
	private static String urlMail;
	private static String userMail;
	private static String passwordMail;

	private static boolean isLocal = false;

	// For SIG sales standalog
	private static Domain newStandaloneDomain = null;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());

		try {

			AonApiData api = initialize(req, false);
			Object object = new AonRouting(api)
					.addRoute(SYNC_MASSIVE_CUSTOMER_DOMAIN, RegistryEnterpriseCreationServlet::syncMassiveCustomerDomain)
					.addRoute(CREATE_ENTERPRISE, RegistryEnterpriseCreationServlet::createEnterprise).apply();

			response(req, resp, object);

		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	// ---------------------------------------------------------------------------------------------
	// CREATE ENTERPRISE
	// ---------------------------------------------------------------------------------------------

	public static JSONObject syncMassiveCustomerDomain(AonApiData api) {
		
		try (CloseableAONContext ctx = AONContext.getAONContext(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin())) {
			
			Integer customerId = api.getData().getInt("customer");
			Customer customer = CustomerDAO.get(ctx, customerId);
			
			Integer domainId = api.getData().optIntegerObject("domain");
			
			if(null != domainId) {
				Company company = CompanyDAO.getCompany(ctx, domainId);
				
				Optional<RegistryRelationship> existRRelationshipforCustomer = RegistryRelationshipDAO.get(ctx, f -> f.getRegistryProperty().eq(customer.getId()).and(f.getDomainProperty().eq(customer.getDomain().getId())));
				if(existRRelationshipforCustomer.isPresent())
					return new JSONObject()
							.put("customerId", customerId.toString())
							.put("customerName", customer.getName())
							.put("customerDocument", customer.getDocument())
							.put("enterpriseId", "")
							.put("enterpriseName", "")
							.put("enterpriseDocument", "")
							.put("messageType", "warning")
							.put("message", "El cliente tiene ya se encuentra vinculado con una empresa")
							;
				else {
					Optional<RegistryRelationship> existRRelationshipforCompany = RegistryRelationshipDAO.get(ctx, f -> f.getRelatedRegistryProperty().eq(company.getId()).and(f.getDomainProperty().eq(customer.getDomain().getId())));
					
					if(existRRelationshipforCompany.isPresent())
						return new JSONObject()
								.put("customerId", customerId.toString())
								.put("customerName", customer.getName())
								.put("customerDocument", customer.getDocument())
								.put("enterpriseId", company.getId().toString())
								.put("enterpriseName", company.getName())
								.put("enterpriseDocument", company.getDocument())
								.put("messageType", "warning")
								.put("message", "La empresa ya se encuentra vinculada con un cliente")
								;
					else {
						List<User> domainUsers = UserDAO.getStream(ctx, f -> f.getDomainProperty().eq(company.getDomain().getId()), new Options().setFull(false)).collect(Collectors.toList());
						
						if(!domainUsers.isEmpty()) {
							
							RegistryRelationship rrelationship = new RegistryRelationship();
							rrelationship.setDomain(customer.getDomain());
							rrelationship.setRegistry(customer.getId());
							rrelationship.setRelatedRegistry(company.getId());
							rrelationship.setComments(company.getDomain().getName());
							
							// Check Comments
							if(AonStringUtils.isNotBlank(rrelationship.getComments()) && AonStringUtils.length(rrelationship.getComments()) >  64)
								throw new IllegalArgumentException("La URL de este dominio es demasiado larga. Por favor p\u00f3ngase en contacto con soporte.");

							RegistryRelationshipDAO.save(ctx, rrelationship);
							
							sendEnterpriseSyncMail(api, ctx, customer);
							
							return new JSONObject()
									.put("customerId", customerId.toString())
									.put("customerName", customer.getName())
									.put("customerDocument", customer.getDocument())
									.put("enterpriseId", company.getId().toString())
									.put("enterpriseName", company.getName())
									.put("enterpriseDocument", company.getDocument())
									.put("messageType", "success")
									.put("message", "El cliente se ha vinculado al dominio")
									;
						
						} else {
							
							// Get enterprise
							Optional<RegistryMedia> customerEmail = RegistryMediaDAO.getStream(ctx, f -> f.getRegistryProperty().eq(customerId).and(f.getMediaProperty().eq(MediaType.EMAIL.value()))).findFirst();
							
							Optional<RegistryMedia> companyEmail = RegistryMediaDAO.getStream(ctx, f -> f.getRegistryProperty().eq(company.getId()).and(f.getMediaProperty().eq(MediaType.EMAIL.value()))).findFirst();
							
							String email = null;
							if(companyEmail.isPresent() && AonStringUtils.isNotBlank(companyEmail.get().getValue()))
								email = companyEmail.get().getValue();
							else if(customerEmail.isPresent() && AonStringUtils.isNotBlank(customerEmail.get().getValue()))
								email = customerEmail.get().getValue();
							
							if(AonStringUtils.isBlank(email)) {
								
								return new JSONObject()
										.put("customerId", customerId.toString())
										.put("customerName", customer.getName())
										.put("customerDocument", customer.getDocument())
										.put("enterpriseId", company.getId().toString())
										.put("enterpriseName", company.getName())
										.put("enterpriseDocument", company.getDocument())
										.put("messageType", "warning")
										.put("message", "No existe usuario en el dominio al que se quiere vincular, ni email en el cliente ni en la empresa para poder crear uno por defecto")
										;
								
							} else {
								
								RegistryRelationship rrelationship = new RegistryRelationship();
								rrelationship.setDomain(customer.getDomain());
								rrelationship.setRegistry(customer.getId());
								rrelationship.setRelatedRegistry(company.getId());
								rrelationship.setComments(company.getDomain().getName());
								
								// Check Comments
								if(AonStringUtils.isNotBlank(rrelationship.getComments()) && AonStringUtils.length(rrelationship.getComments()) >  64)
									throw new IllegalArgumentException("La URL de este dominio es demasiado larga. Por favor p\u00f3ngase en contacto con soporte.");

								RegistryRelationshipDAO.save(ctx, rrelationship);
								
								createUserAuth(ctx, company, customer, email);

								urlMail = company.getDomain().getName();
								userMail = email;
								
								sendEnterpriseSyncUserMail(api, ctx, email, customer);
								
								return new JSONObject()
										.put("customerId", customerId.toString())
										.put("customerName", customer.getName())
										.put("customerDocument", customer.getDocument())
										.put("enterpriseId", company.getId().toString())
										.put("enterpriseName", company.getName())
										.put("enterpriseDocument", company.getDocument())
										.put("messageType", "success")
										.put("message", "El cliente se ha vinculado al dominio. Y se ha creado un usuario para el dominio con el mail" + email)
										;
							}
						}
					}
				}
			} else {
				List<Domain> domains = DomainDAO.getCompanyDomains(ctx, customer.getDocument());
				// Filter active domains
				domains = domains.stream().filter(d -> d.isActive()).collect(Collectors.toList());
				
				if(domains.isEmpty())
					return new JSONObject()
							.put("customerId", customerId.toString())
							.put("customerName", customer.getName())
							.put("customerDocument", customer.getDocument())
							.put("enterpriseId", "")
							.put("enterpriseName", "")
							.put("enterpriseDocument", "")
							.put("messageType", "error")
							.put("message", "El cliente no tiene ningun dominio con el mismo documento para vincularse")
							;
							
				else if(domains.size() > 1) 
					return new JSONObject()
							.put("customerId", customerId.toString())
							.put("customerName", customer.getName())
							.put("customerDocument", customer.getDocument())
							.put("enterpriseId", "")
							.put("enterpriseName", "")
							.put("enterpriseDocument", "")
							.put("messageType", "warning")
							.put("message", "El cliente tiene mas de un dominio con el mismo documento para vincularse")
							;
				else {
					domainId = domains.get(0).getId();
					Company company = CompanyDAO.getCompany(ctx, domainId);
					
					Optional<RegistryRelationship> existRRelationshipforCustomer = RegistryRelationshipDAO.get(ctx, f -> f.getRegistryProperty().eq(customer.getId()).and(f.getDomainProperty().eq(customer.getDomain().getId())));
					if(existRRelationshipforCustomer.isPresent())
						return new JSONObject()
								.put("customerId", customerId.toString())
								.put("customerName", customer.getName())
								.put("customerDocument", customer.getDocument())
								.put("enterpriseId", "")
								.put("enterpriseName", "")
								.put("enterpriseDocument", "")
								.put("messageType", "warning")
								.put("message", "El cliente tiene ya se encuentra vinculado con una empresa")
								;
					else {
						Optional<RegistryRelationship> existRRelationshipforCompany = RegistryRelationshipDAO.get(ctx, f -> f.getRelatedRegistryProperty().eq(company.getId()).and(f.getDomainProperty().eq(customer.getDomain().getId())));
						
						if(existRRelationshipforCompany.isPresent())
							return new JSONObject()
									.put("customerId", customerId.toString())
									.put("customerName", customer.getName())
									.put("customerDocument", customer.getDocument())
									.put("enterpriseId", company.getId().toString())
									.put("enterpriseName", company.getName())
									.put("enterpriseDocument", company.getDocument())
									.put("messageType", "warning")
									.put("message", "La empresa ya se encuentra vinculada con un cliente")
									;
						else {
							List<User> domainUsers = UserDAO.getStream(ctx, f -> f.getDomainProperty().eq(company.getDomain().getId()), new Options().setFull(false)).collect(Collectors.toList());
							
							if(!domainUsers.isEmpty()) {
								
								RegistryRelationship rrelationship = new RegistryRelationship();
								rrelationship.setDomain(customer.getDomain());
								rrelationship.setRegistry(customer.getId());
								rrelationship.setRelatedRegistry(company.getId());
								rrelationship.setComments(company.getDomain().getName());

								// Check Comments
								if(AonStringUtils.isNotBlank(rrelationship.getComments()) && AonStringUtils.length(rrelationship.getComments()) >  64)
									throw new IllegalArgumentException("La URL de este dominio es demasiado larga. Por favor p\u00f3ngase en contacto con soporte.");

								RegistryRelationshipDAO.save(ctx, rrelationship);
								
								sendEnterpriseSyncMail(api, ctx, customer);
								
								return new JSONObject()
										.put("customerId", customerId.toString())
										.put("customerName", customer.getName())
										.put("customerDocument", customer.getDocument())
										.put("enterpriseId", company.getId().toString())
										.put("enterpriseName", company.getName())
										.put("enterpriseDocument", company.getDocument())
										.put("messageType", "success")
										.put("message", "El cliente se ha vinculado al dominio")
										;
							
							} else {
								
								// Get enterprise
								Optional<RegistryMedia> customerEmail = RegistryMediaDAO.getStream(ctx, f -> f.getRegistryProperty().eq(customerId).and(f.getMediaProperty().eq(MediaType.EMAIL.value()))).findFirst();
								
								Optional<RegistryMedia> companyEmail = RegistryMediaDAO.getStream(ctx, f -> f.getRegistryProperty().eq(company.getId()).and(f.getMediaProperty().eq(MediaType.EMAIL.value()))).findFirst();
								
								String email = null;
								if(companyEmail.isPresent() && AonStringUtils.isNotBlank(companyEmail.get().getValue()))
									email = companyEmail.get().getValue();
								else if(customerEmail.isPresent() && AonStringUtils.isNotBlank(customerEmail.get().getValue()))
									email = customerEmail.get().getValue();
								
								if(AonStringUtils.isBlank(email)) {
									
									return new JSONObject()
											.put("customerId", customerId.toString())
											.put("customerName", customer.getName())
											.put("customerDocument", customer.getDocument())
											.put("enterpriseId", company.getId().toString())
											.put("enterpriseName", company.getName())
											.put("enterpriseDocument", company.getDocument())
											.put("messageType", "warning")
											.put("message", "No existe usuario en el dominio al que se quiere vincular, ni email en el cliente ni en la empresa para poder crear uno por defecto")
											;
									
								} else {
									
									RegistryRelationship rrelationship = new RegistryRelationship();
									rrelationship.setDomain(customer.getDomain());
									rrelationship.setRegistry(customer.getId());
									rrelationship.setRelatedRegistry(company.getId());
									rrelationship.setComments(company.getDomain().getName());

									// Check Comments
									if(AonStringUtils.isNotBlank(rrelationship.getComments()) && AonStringUtils.length(rrelationship.getComments()) >  64)
										throw new IllegalArgumentException("La URL de este dominio es demasiado larga. Por favor p\u00f3ngase en contacto con soporte.");

									RegistryRelationshipDAO.save(ctx, rrelationship);

									createUserAuth(ctx, company, customer, email);

									urlMail = company.getDomain().getName();
									userMail = email;
									
									sendEnterpriseSyncUserMail(api, ctx, email, customer);
									
									return new JSONObject()
											.put("customerId", customerId.toString())
											.put("customerName", customer.getName())
											.put("customerDocument", customer.getDocument())
											.put("enterpriseId", company.getId().toString())
											.put("enterpriseName", company.getName())
											.put("enterpriseDocument", company.getDocument())
											.put("messageType", "success")
											.put("message", "El cliente se ha vinculado al dominio. Y se ha creado un usuario para el dominio con el mail" + email)
											;
								}
							}
						}
					}
				
				}	
			}
			
			
			
		} catch (Exception e) {
			return new JSONObject().put("error", e.getMessage());
		}
	}
	
	private static void createUserAuth(CloseableAONContext ctx, Company company, Customer customer, String email) {
		String login = ramdonLogin();
		String pass = null;

		Auth auth = AON_SOLUTIONS.getAuth(email);

		if (pass == null)
			pass = Utils.createPasswordHash(email, login);

		passwordMail = login;
		
		if (auth.getUuid() == null) {
			auth = new Auth()
					.setEmail(email)
					.setPassword(pass)
					.setName(customer.getName())
					.setSurname(null)
					.setDocument(customer.getDocument())
					;

			auth = AuthDAO.insertAuth(ctx, auth);
		} else {
			auth.setPassword(pass);
			auth = AuthDAO.updateAuth(ctx, auth);
		}

		if (auth.getAuth() != null) {
			User user = new User().setAuth(auth).setActive(true)
					.setDomain(company.getDomain().getId())
					.setLogin(company.getDocument())
					.setName(company.getDocument())
					.setShared(false)
					.setEnterprise(company.getId())
					.setToolbar(UserToolbar.GOOGLE)
					;

			user = SecurityDAO.save(ctx, user);

			SecurityDAO.updateUserPassword(ctx, user.getId(), auth.getPassword());

			Scope scope = getScope(ctx, company.getDomain(), user);

			if (scope != null) {
				SecurityDAO.insertUserScope(ctx,
						new UserScope().setDomain(company.getDomain().getId()).setScope(scope.getId()).setUserId(user.getId()));
			}

			ApplicationParameter appParam = AppParamDAO.fetchOne(ctx, AppParam.AON_PORTAL);

			if (appParam == null || appParam.getId() == null) {

				appParam = new ApplicationParameter()
						.setDomain(company.getDomain().getId())
						.setValue("288")
						.setName(AppParam.AON_PORTAL.getValue());

				AppParamDAO.insertApplicationParameter(ctx, appParam);
			}
			
			setUserAppRole(ctx, company.getDomain(), user);

			if (company.getDomain().isChild() || company.getDomain().isStandalone())
				createTaskHolder(ctx, company.getDomain(), auth, user);

		}
	}
	
	private static void sendEnterpriseSyncMail(AonApiData api, CloseableAONContext ctx, Customer customer) {
		Domain parent = api.getDomain().isParent() ? api.getDomain()
				: DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(api.getDomain().getParentId()));

		String logoUrl = getLogoUrl(api, ctx);

		String from = getFromMessage(api, ctx, parent);

		if (AonStringUtils.isBlank(from))
			throw new AonApiException("No existe email definido en el entorno para la creaci\u00f3n de empresas");

		SESMessage msg = new SESMessage().setFrom(from)
				.setTo(from)
				.setReplyTo(from)
				.setSubject("Vinculaci\u00f3n Empresa " + customer.getName())
				.setBody(createEnterpriseSyncBody(logoUrl, parent, from, customer.getName()));

		SES.sendEmail(msg);
	}
	
	private static void sendEnterpriseSyncUserMail(AonApiData api, CloseableAONContext ctx, String email, Customer customer) {
		Domain parent = api.getDomain().isParent() ? api.getDomain()
				: DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(api.getDomain().getParentId()));

		String logoUrl = getLogoUrl(api, ctx);

		String from = getFromMessage(api, ctx, parent);

		if (AonStringUtils.isBlank(from))
			throw new AonApiException("No existe email definido en el entorno para la creaci\u00f3n de empresas");

		List<String> bcc = List.of(from);

		SESMessage msg = new SESMessage().setFrom(from)
				.setTo(email)
				.setBcc(bcc)
				.setReplyTo(from)
				.setSubject("Vinculaci\u00f3n Empresa " + customer.getName())
				.setBody(createEnterpriseSyncUserBody(logoUrl, parent, from, customer.getName()));

		SES.sendEmail(msg);
	}

	// ---------------------------------------------------------------------------------------------
	// CREATE ENTERPRISE
	// ---------------------------------------------------------------------------------------------

	public static JSONObject createEnterprise(AonApiData api) {

		/*
		 * Allowed params
		 * 
		 * name : registry name document : registry document
		 * 
		 * streetType address number zip geozoneCode city
		 * 
		 * phone email
		 * 
		 * registry : customer.id / target.id
		 * 
		 * sellerSupport : seller.id support type sellerCommercial : seller.id
		 * commercial type
		 * 
		 * saleId
		 * 
		 * isSig schema
		 * isSig url (new domainName)
		 * isSig domainType
		 * 
		 * feePeriod : 0 sin periodo, 1 mensual, 2 bimesnual, 3 trimestral, 4
		 * cuatrimestral, 5 semestral, 6 anual feeWorkplace : id del workplace
		 * 
		 * source: TARGET, SALE
		 * 
		 */

		// Create Enterprise

		JSONObject result = new JSONObject();

		// Cuando venga del SIG y sea SALE

		// El dominio que sea crea no tiene padre, el Scope debe colgar de ese dominio
		// creado por lo que hay que crear primero el dominio, del cual cuelgan:
		// el scope, el user, el user_scope, domainConfig, accountPeriod (se puede
		// obviar)
		// CustomerFee y Sale cuelgan del depacho del SIG

		JSONObject data = api.getData();

		String source = JsonUtils.getString(data, "source");
		boolean isSig = JsonUtils.getboolean(data, "isSig");

		if (AonStringUtils.equalsIgnoreCase(source, "SALE") && isSig) {

			// Sobre este esquema hay que guardar el dominio
			String schema = JsonUtils.getString(data, "schema");
			String url = JsonUtils.getString(data, "url");
			String domainType = JsonUtils.getString(data, "domainType");

			System.out.println("SCHEMA --------> " + schema);

			System.out.println("-- [Start] Create Enterprise (Schema: " + schema + ") --");

			try (CloseableAONContext ctxSig = AONContext.getAONContext(api.getDomain().getName(),
					api.getDomain().getId(), api.getUser().getLogin())) {

				ctxSig.transaction(t -> {

					System.out.println("----------------------- Check Customer");

					Customer customer = checkCustomer(api, ctxSig);

					try (CloseableAONContext ctx = AONContext.getAONContext(schema)) {

						ctx.transaction(tr -> {

							System.out.println("----------------------- Create Domain");

							newStandaloneDomain = createDomainStandalone(api, ctx, schema, customer.getId(), url, domainType);
							urlMail = newStandaloneDomain.getName();

							System.out.println("----------------------- Create Scope");

							Scope newScope = updateDomainScopeStandalone(api, newStandaloneDomain, ctx);

							System.out.println("----------------------- Create Auth / User");

							createDefaultUserStandalone(api, ctx, newStandaloneDomain, newScope);

							System.out.println("----------------------- Domain Apps / Config");

							insertDomainConfiguration(ctx, newStandaloneDomain);

							System.out.println("----------------------- Account Period");

							insertAccountPeriod(ctx, newStandaloneDomain);

							if (AonStringUtils.equalsIgnoreCase(source, "SALE")) {

								System.out.println("----------------------- Customer RaddInfo");

								createCustomerRaddinfo(api, ctxSig, schema, newStandaloneDomain, customer.getId());

								System.out.println("----------------------- Customer Fees");

								createCustomerFee(api, ctxSig);

								System.out.println("----------------------- Close Sale");

								closeSale(api, ctxSig);

							}

							// Send mail
							sendSigEnterpriseCreatedMail(api, ctxSig);

							System.out
									.println("----------------------- [End] Create Enterprise -----------------------");

							if (AonStringUtils.equalsIgnoreCase(source, "SALE"))
								result.put("message",
										"Pedido procesado, empresa creada correctamente. Se ha enviado un mail con los datos al agente de soporte");
							else if (AonStringUtils.equalsIgnoreCase(source, "TARGET"))
								result.put("message",
										"Empresa creada correctamente. Se ha enviado un mail con los datos al agente de soporte");

							result.put("customer", customer.getId().toString());
						});

					}

				});

			}

		} else {
			try (CloseableAONContext ctx = AONContext.getAONContext(api.getDomain().getName(), api.getDomain().getId(),
					api.getUser().getLogin())) {

				ctx.transaction(t -> {

					System.out.println("----------------------- [Start] Create Enterprise -----------------------");

					System.out.println("----------------------- Check Customer");

					Customer customer = checkCustomer(api, ctx);

					System.out.println("----------------------- Create Scope");

					Scope newScope = createScope(api, ctx);

					System.out.println("----------------------- Create Domain");

					Domain newDomain = createDomain(api, ctx, newScope);
					urlMail = newDomain.getName();

					createRRelationShip(api, ctx, newDomain);

					System.out.println("----------------------- Create Auth / User");

					createDefaultUser(api, ctx, newDomain);

					System.out.println("----------------------- Create User Scope (supportSeller / api.getUser)");

					createUserScope(api, ctx, newDomain, newScope);

					System.out.println("----------------------- Domain Apps / Config");

					insertDomainConfiguration(ctx, newDomain);

					System.out.println("----------------------- Account Period");

					insertAccountPeriod(ctx, newDomain);

					if (AonStringUtils.equalsIgnoreCase(source, "SALE")) {

						System.out.println("----------------------- Customer Fees");

						createCustomerFee(api, ctx);

						System.out.println("----------------------- Close Sale");

						closeSale(api, ctx);

					}

					// Send mail
					sendEnterpriseCreatedMail(api, ctx);

					System.out.println("----------------------- [End] Create Enterprise -----------------------");

					if (AonStringUtils.equalsIgnoreCase(source, "SALE"))
						result.put("message",
								"Pedido procesado, empresa creada correctamente. Se ha enviado un mail con los datos al agente de soporte");
					else if (AonStringUtils.equalsIgnoreCase(source, "TARGET"))
						result.put("message",
								"Empresa creada correctamente. Se ha enviado un mail con los datos al agente de soporte");

					result.put("customer", customer.getId().toString());
				});
			}
		}

		return result;
	}

	// ---------------------------------------------------------------------------------------------
	// AUXILIAR METHODS
	// ---------------------------------------------------------------------------------------------

	private static Customer checkCustomer(AonApiData api, CloseableAONContext ctx) {
		JSONObject data = api.getData();

		Integer registry = JsonUtils.getInteger(data, "registry");

		Customer customer = CustomerDAO.get(ctx, f -> f.getRegistryProperty().eq(registry));
		Optional<Target> targetOpt = TargetDAO.getStream(ctx, f -> f.getIdProperty().eq(registry)).findFirst();

		// Existe target pero no customer
		if (null == customer || null == customer.getId()) {
			TargetFull target = TargetDAO.getFull(ctx, registry);
			Customer newCustomer = new Customer()
					.setTariff(
							null != target.getRegistry().getTariff() ? target.getRegistry().getTariff().getId() : null)
					.setSurcharge(target.getRegistry().getSurcharge())
					.setWithholding(target.getRegistry().getWithholding())
					.setTransaction(target.getRegistry().getTransaction()).setStatus(RegistryStatus.ACTIVE)
					.setScope(target.getRegistry().getScope()).setCreationUser(api.getUser().getName())
					.setCreationDate(new Date());
			newCustomer.setDomain(new Domain().setId(target.getDomain()));
			newCustomer.copy(target.getRegistry());

			customer = CustomerDAO.save(ctx, newCustomer);
			System.out.println("Registry : " + registry + ", New Customer : " + customer.getId());

			// Existe customer pero no target
		} else if (targetOpt.isEmpty()) {
			if (targetOpt.isEmpty()) {
				Target newTarget = new Target().setTariff(new Tariff().setId(customer.getTariff()))
						.setAdvertising(Advertising.ALLOWED).setSurcharge(customer.isSurcharge())
						.setWithholding(customer.isWithholding()).setTransaction(customer.getTransaction())
						.setStatus(TargetStatus.ACTIVE).setScope(customer.getScope())
						.setCreationUser(api.getUser().getLogin()).setCreationDate(new Date());

				newTarget.setDomain(customer.getDomain());
				newTarget.copy(customer);

				TargetDAO.save(ctx, newTarget);
			}
		}

		return customer;

	}

	private static Scope createScope(AonApiData api, CloseableAONContext ctx) throws Exception {

		JSONObject data = api.getData();

		String document = JsonUtils.getString(data, "document");

		Domain parentDomain = null == api.getDomain().getParentId() ? api.getDomain()
				: DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(api.getDomain().getParentId()));

		Stream<Scope> scopes = SecurityDAO.getScopeStream(ctx,
				f -> f.getDescriptionProperty().eq(document).and(f.getDomainProperty().eq(parentDomain.getId())));

		if (scopes.count() != 0)
			throw new AonApiException("Ya existe un ambito en el entorno cuya descripci\u00f3n es " + document);
		else {
			Scope newScope = new Scope().setDomain(parentDomain.getId()).setDescription(document);

			newScope = SecurityDAO.insertScope(ctx, newScope);
			return newScope;
		}

	}

	private static Domain createDomain(AonApiData api, CloseableAONContext ctx, Scope newScope) throws Exception {
		JSONObject data = api.getData();

		String name = JsonUtils.getString(data, "name");
		String document = JsonUtils.getString(data, "document");
		Integer sellerSupport = JsonUtils.getInteger(data, "sellerSupport");

		Company company = new Company();
		company.setName(name);
		company.setDocument(document.toUpperCase());
		company.setLegalPerson(AonDocumentUtil.isValidCIF(document));

		checkCompany(company);

		if (checkExistingDomain(api, ctx))
			throw new AonApiException(
					"La empresa con identificador " + document + " para el cliente " + name + " ya existe");

		Domain parentDomain = null == api.getDomain().getParentId() ? api.getDomain()
				: DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(api.getDomain().getParentId()));

		if (null == parentDomain || null == parentDomain.getId())
			throw new AonApiException("No existe empresa padre desde la que colgar esta empresa");

		// Get owner email

		Stream<RegistryMedia> sellerSupportMedias = RegistryMediaDAO.getStream(ctx,
				f -> f.getRegistryProperty().eq(sellerSupport));
		Optional<RegistryMedia> sellerSupportEmailOpt = sellerSupportMedias
				.filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();

		if (sellerSupportEmailOpt.isEmpty())
			throw new AonApiException("No existe email para el agente de soporte seleccionado");

		String domainNewName = company.getDocument().toLowerCase() + "-"
				+ (AonStringUtils.isBlank(parentDomain.getSubDomainSuffix()) ? parentDomain.getName()
						: parentDomain.getSubDomainSuffix());

		Domain newDomain = new Domain().setName(domainNewName.toLowerCase()).setDescription(company.getName())
				.setOwner(sellerSupportEmailOpt.get().getValue()) // Alguno mas
				.setParentId(parentDomain.getId()).setActive(true).setDomainType(DomainType.ENTERPRISE)
				.setEnableHeredity(true).setDomainManagement(false).setScope(newScope.getId());

		newDomain = DomainDAO.insertDomainWithoutEnterprise(ctx, newDomain, company);
		
		Domain createdDomain = newDomain;
		
		Company newCompany = CompanyDAO.getCompanyStream(ctx, f -> f.getDomainProperty().eq(createdDomain.getId())).findFirst().get();
		
		Scope enterpriseScope = SecurityDAO.insertScope(ctx, new Scope().setDomain(newDomain.getId()).setDescription("GENERAL"));
		
		ctx.getDslContext()
			.insertInto(ENTERPRISE)
			.set(ENTERPRISE.REGISTRY, newCompany.getId())
			.set(ENTERPRISE.DOMAIN, newDomain.getId())
			.set(ENTERPRISE.SCOPE, enterpriseScope.getId())
			.execute();

		createCompanyMedia(api, ctx, newDomain);

		return newDomain;

	}

	private static Domain createDomainStandalone(AonApiData api, CloseableAONContext ctx, String schema,
			Integer customerId, String url, String domainType) throws Exception {
		JSONObject data = api.getData();

		String name = JsonUtils.getString(data, "name");
		String document = JsonUtils.getString(data, "document");
		Integer sellerSupport = JsonUtils.getInteger(data, "sellerSupport");

		Company company = new Company();
		company.setName(name);
		company.setDocument(document);
		company.setLegalPerson(AonDocumentUtil.isValidCIF(document));

		checkCompany(company);

		if (checkExistingDomain(api, ctx))
			throw new AonApiException(
					"La empresa con identificador " + document + " para el cliente " + name + " ya existe");

		// Get owner email (from sig)

		Stream<RegistryMedia> sellerSupportMedias = AON.getRegistryMediaStream(
				new Domain().setName(api.getDomain().getName()).setId(api.getDomain().getId()),
				new User().setLogin(api.getUser().getLogin()), f -> f.getRegistryProperty().eq(sellerSupport));

		Optional<RegistryMedia> sellerSupportEmailOpt = sellerSupportMedias
				.filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();

		if (sellerSupportEmailOpt.isEmpty())
			throw new AonApiException("No existe email para el agente de soporte seleccionado");

		String domainNewName = AonStringUtils.isBlank(url) ? company.getDocument().toLowerCase() + "-aonsolutions.org" : url;

		Domain newDomain = new Domain().setName(domainNewName.toLowerCase()).setDescription(company.getName())
				.setOwner(sellerSupportEmailOpt.get().getValue()).setActive(true)
				.setDomainType(AonStringUtils.isBlank(domainType) ? DomainType.ENTERPRISE : DomainType.safeValueOf(Byte.parseByte(domainType)))
				.setEnableHeredity(false).setDomainManagement(false).setAonCustomer(customerId);

		newDomain = DomainDAO.insertDomainStandalone(ctx, newDomain, company, schema, document);
		
		createCompanyMedia(api, ctx, newDomain);

		return newDomain;

	}

	private static Scope updateDomainScopeStandalone(AonApiData api, Domain newDomain, CloseableAONContext ctx) throws Exception {

		JSONObject data = api.getData();

		String document = JsonUtils.getString(data, "document");

		Optional<Scope> scope = SecurityDAO.getScopeStream(ctx, f -> f.getDescriptionProperty().eq(document))
				.findFirst();

		if (scope.isEmpty())
			throw new AonApiException("No se ha crear un ambito en el entorno cuya descripci\u00f3n es " + document);
		else {
			newDomain.setScope(scope.get().getId());
			DomainDAO.updateDomainScope(ctx, newDomain);

			return scope.get();
		}

	}

	private static void checkCompany(Company company) throws AonApiException {
		if (AonStringUtils.isBlank(company.getDocument())) {
			throw new AonApiException("El documento de la empresa esta vacio.");
		}

		if (!AonDocumentUtil.isValid(company.getDocument().toUpperCase())) {
			throw new AonApiException("El documento de la empresa no es valido");
		}

		if (AonStringUtils.isBlank(company.getName())) {
			throw new AonApiException("El nombre de la empresa esta vacio");
		}
	}

	private static boolean checkExistingDomain(AonApiData api, CloseableAONContext ctx) {
		JSONObject data = api.getData();

		String document = JsonUtils.getString(data, "document");

		Domain parentDomain = null == api.getDomain().getParentId() ? api.getDomain()
				: DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(api.getDomain().getParentId()));

		if (null != parentDomain && null != parentDomain.getId()) {

			Domain domain = DomainDAO.getDomain(ctx, f -> f.getNameProperty().like("%" + document + "%")
					.and(f.getParentProperty().eq(parentDomain.getId())));

			return null != domain && null != domain.getId();

		}

		return false;
	}

	private static void createCompanyMedia(AonApiData api, CloseableAONContext ctx, Domain newDomain) {
		JSONObject data = api.getData();

		String streetType = JsonUtils.getString(data, "streetType");
		String address = JsonUtils.getString(data, "address");
		String number = JsonUtils.getString(data, "number");
		String zip = JsonUtils.getString(data, "zip");
		String geozoneCode = JsonUtils.getString(data, "geozoneCode");
		String city = JsonUtils.getString(data, "city");

		String phone = JsonUtils.getString(data, "phone");
		String email = JsonUtils.getString(data, "email");

		Integer registry = JsonUtils.getInteger(data, "registry");

		Company newCompany = CompanyDAO.getCompanyStream(ctx, f -> f.getDomainProperty().eq(newDomain.getId()))
				.findFirst().get();
		
		Optional<Scope> enterpriseScope = SecurityDAO.getScopeStream(ctx, f -> f.getDomainProperty().eq(newDomain.getId()).and(f.getDescriptionProperty().eq("GENERAL"))).findFirst();
		if(enterpriseScope.isEmpty())
			enterpriseScope = Optional.of(SecurityDAO.insertScope(ctx, new Scope().setDomain(newDomain.getId()).setDescription("GENERAL")));

		Integer comapnyRaddressId = null;

		if (AonStringUtils.isNotBlank(address)) {

			// Conseguir del padre (o del actual si standalone)
			Integer geozoneDomain = null == newDomain.getParentId() ? newDomain.getId() : newDomain.getParentId();

			Optional<GeoZone> geozoneOpt = GeoZoneDAO
					.getStream(ctx,
							f -> f.getDomainProperty().eq(geozoneDomain).and(f.getCodeProperty().eq(geozoneCode)))
					.findFirst();

			RegistryAddress registryAddress = new RegistryAddress().setDomain(newDomain.getId())
					.setRegistry(newCompany.getId()).setMain(true)
					.setStreetType(StreetType.getForAeatCode(streetType, AonLanguage.SPANISH)).setAddress(address)
					.setNumber(number).setZip(zip).setCity(city)
					.setGeozone(geozoneOpt.isPresent() ? geozoneOpt.get().getId() : null).setGeozoneCode(geozoneCode)
					.setGeozoneName(geozoneOpt.isPresent() ? geozoneOpt.get().getName() : null);

			registryAddress = RegistryAddressDAO.save(ctx, registryAddress);
			comapnyRaddressId = registryAddress.getId();
		}

		saveMedia(api, ctx, newDomain.getId(), newCompany.getId(), MediaType.FIXED_PHONE, phone, comapnyRaddressId);
		saveMedia(api, ctx, newDomain.getId(), newCompany.getId(), MediaType.EMAIL, email, comapnyRaddressId);

		if (null != comapnyRaddressId)
			createWorkplace(ctx, newDomain, newCompany.getId(), comapnyRaddressId, geozoneCode, enterpriseScope);

	}

	private static void saveMedia(AonApiData api, CloseableAONContext ctx, Integer domain, Integer registry,
			MediaType mediaType, String value, Integer raddress) {
		if (AonStringUtils.isNotBlank(value)) {
			RegistryMedia registryMedia = new RegistryMedia().setDomain(domain).setRegistry(registry)
					.setMedia(mediaType).setValue(value).setCommercial(true).setRaddress(raddress);

			RegistryMediaDAO.save(ctx, registryMedia);
		}
	}

	private static void createWorkplace(CloseableAONContext ctx, Domain newDomain, Integer companyId,
			Integer raddressId, String geozoneCode, Optional<Scope> enterpriseScope) {
		Workplace workplace = new Workplace().setActive(true)
				.setDescription("PRINCIPAL")
				.setDomain(newDomain.getId())
				.setEnterprise(companyId)
				.setAddress(raddressId)
				.setEconomicAgreement(getEconomicAgreement(geozoneCode))
				.setScope(enterpriseScope.isEmpty() ? null : enterpriseScope.get().getId())
				.setPayrollWorkplace(new PayrollWorkplace().setDomain(newDomain.getId()))
				;

		WorkplaceDAO.save(ctx, workplace);
	}

	private static Administration getEconomicAgreement(String geozoneCode) {
		if (AonStringUtils.isBlank(geozoneCode))
			return Administration.COMMON_TERRITORY;

		switch (geozoneCode) {
		case "01":
			return Administration.ALAVA;
		case "20":
			return Administration.GIPUZKOA;
		case "48":
			return Administration.BIZKAIA;
		case "31":
			return Administration.NAVARRA;
		case "35":
			return Administration.CANARIAS;
		default:
			return Administration.COMMON_TERRITORY;
		}
	}

	private static void createRRelationShip(AonApiData api, CloseableAONContext ctx, Domain newDomain) {
		JSONObject data = api.getData();

		Integer registry = JsonUtils.getInteger(data, "registry"); // target.id / customer.id
		Integer sellerSupport = JsonUtils.getInteger(data, "sellerSupport");
		Integer sellerCommercial = JsonUtils.getInteger(data, "sellerCommercial");
		Integer saleId = JsonUtils.getInteger(data, "saleId");

		Customer customer = CustomerDAO.getStream(ctx, f -> f.getIdProperty().eq(registry)).findFirst().get();

		if (null == customer)
			throw new AonApiException("No ha sido posible encontrar al cliente seleccionado");

		// Create RSeller
		Sales sale = null;
		if (null != saleId)
			sale = SalesDAO.get(ctx, f -> f.getIdProperty().eq(saleId), new Options().setFull(true));

		Stream<RegistrySeller> customerRSellers = RegistrySellerDAO.getStream(ctx,
				f -> f.getRegistryProperty().eq(registry));

		Optional<RegistrySeller> commercialRSeller = customerRSellers
				.filter(rseller -> rseller.getType().equals(RegistrySellerType.COMERCIAL)).findFirst();

		if (null != sellerCommercial) {
			if (commercialRSeller.isEmpty()) {
				Date startDate = null != sale ? sale.getCreationDate() : new Date();

				RegistrySeller rseller = new RegistrySeller().setDomain(api.getDomain()).setRegistry(registry)
						.setSeller(new Seller().setId(sellerCommercial)).setType(RegistrySellerType.COMERCIAL)
						.setStatus(RegistrySellerStatus.ACTIVE).setStartDate(startDate);

				RegistrySellerDAO.save(ctx, rseller);
			} else {
				Date startDate = null != sale ? sale.getCreationDate() : new Date();
				Date endDate = AonDateUtils.addDays(startDate, -1);

				RegistrySeller rseller = commercialRSeller.get();
				rseller.setEndDate(endDate);
				RegistrySellerDAO.save(ctx, rseller);

				RegistrySeller newRseller = new RegistrySeller().setDomain(api.getDomain()).setRegistry(registry)
						.setSeller(new Seller().setId(sellerCommercial)).setType(RegistrySellerType.COMERCIAL)
						.setStatus(RegistrySellerStatus.ACTIVE).setStartDate(startDate);

				RegistrySellerDAO.save(ctx, newRseller);
			}
		}

		customerRSellers = RegistrySellerDAO.getStream(ctx, f -> f.getRegistryProperty().eq(registry));

		Optional<RegistrySeller> supportRSeller = customerRSellers
				.filter(rseller -> rseller.getType().equals(RegistrySellerType.SOPORTE)).findFirst();
		if (supportRSeller.isEmpty()) {
			Date startDate = null != sale ? sale.getCreationDate() : new Date();

			RegistrySeller rseller = new RegistrySeller().setDomain(api.getDomain()).setRegistry(registry)
					.setSeller(new Seller().setId(sellerSupport)).setType(RegistrySellerType.SOPORTE)
					.setStatus(RegistrySellerStatus.ACTIVE).setStartDate(startDate);

			RegistrySellerDAO.save(ctx, rseller);
		} else {
			Date startDate = null != sale ? sale.getCreationDate() : new Date();
			Date endDate = AonDateUtils.addDays(startDate, -1);

			RegistrySeller rseller = supportRSeller.get();
			rseller.setEndDate(endDate);
			RegistrySellerDAO.save(ctx, rseller);

			RegistrySeller newRseller = new RegistrySeller().setDomain(api.getDomain()).setRegistry(registry)
					.setSeller(new Seller().setId(sellerSupport)).setType(RegistrySellerType.SOPORTE)
					.setStatus(RegistrySellerStatus.ACTIVE).setStartDate(startDate);

			RegistrySellerDAO.save(ctx, newRseller);
		}

		// Create Registry Relationship
		Company newCompany = CompanyDAO.getCompanyStream(ctx, f -> f.getDomainProperty().eq(newDomain.getId()))
				.findFirst().get();

		RegistryRelationship rrelationship = new RegistryRelationship();
		rrelationship.setDomain(customer.getDomain());
		rrelationship.setRegistry(customer.getId());
		rrelationship.setRelatedRegistry(newCompany.getId());
		rrelationship.setComments(newCompany.getDomain().getName());

		RegistryRelationshipDAO.save(ctx, rrelationship);
	}

	private static User createDefaultUser(AonApiData api, CloseableAONContext ctx, Domain newDomain) {
		JSONObject data = api.getData();

		Integer registry = JsonUtils.getInteger(data, "registry"); // target.id / customer.id
		String email = JsonUtils.getString(data, "email");
		String phone = JsonUtils.getString(data, "phone");

		Optional<Target> targetOpt = TargetDAO.getStream(ctx, f -> f.getIdProperty().eq(registry)).findFirst();

		if (Utils.isEmail(email)) {

			String login = ramdonLogin();
			String pass = null;

			Auth auth = AON_SOLUTIONS.getAuth(email);

			if (pass == null)
				pass = Utils.createPasswordHash(email, login);
			
			passwordMail = login;

			if (auth.getUuid() == null) {
//				if (pass == null) pass = Utils.createPasswordHash(email, login);

				auth = new Auth().setEmail(email).setPassword(pass).setName(targetOpt.get().getName()).setSurname(null)
						.setDocument(targetOpt.get().getDocument()).setPhone(phone);

//				passwordMail = login;

				auth = AuthDAO.insertAuth(ctx, auth);
			} else {
				auth.setPassword(pass);
				auth = AuthDAO.updateAuth(ctx, auth);
			}

			User user = null;
			if (auth.getAuth() != null) {
				user = createUser(api, ctx, newDomain, auth, login, targetOpt.get().getName());
				setUserAppRole(ctx, newDomain, user);

				if (newDomain.isChild() || newDomain.isStandalone()) {
					createTaskHolder(ctx, newDomain, auth, user);
				}
			}

			userMail = email;

			return user;

		} else {
			throw new AonApiException("El email (" + email + ") no tiene un formato correcto.");
		}

	}

	private static User createDefaultUserStandalone(AonApiData api, CloseableAONContext ctx, Domain newDomain,
			Scope newScope) {
		JSONObject data = api.getData();

		Integer registry = JsonUtils.getInteger(data, "registry"); // target.id / customer.id
		String email = JsonUtils.getString(data, "email");
		String phone = JsonUtils.getString(data, "phone");

		Optional<Target> targetOpt = AON.getTargetStream(api.getDomain().getName(), api.getDomain().getId(),
				api.getUser().getLogin(), f -> f.getIdProperty().eq(registry)).findFirst();

		if (Utils.isEmail(email)) {

//			String login = ramdonLogin();
			String login = "admin";
			String pass = null;

			Auth auth = AON_SOLUTIONS.getAuth(email);

//			if (pass == null)
//				pass = Utils.createPasswordHash(email, login);
			
			if (pass == null)
				pass = Utils.createPasswordHash(email, "admin");

			passwordMail = login;

			if (auth.getUuid() == null) {

				auth = new Auth().setEmail(email).setPassword(pass).setName(targetOpt.get().getName()).setSurname(null)
						.setDocument(targetOpt.get().getDocument()).setPhone(phone);

				auth = AuthDAO.insertAuth(ctx, auth);
			} else {
				auth.setPassword(pass);
				auth = AuthDAO.updateAuth(ctx, auth);
			}

			User user = null;
			if (auth.getAuth() != null) {
				user = createSigUser(api, ctx, newDomain, newScope, auth, login, targetOpt.get().getName());
				setUserAppRole(ctx, newDomain, user);

				if (newDomain.isChild() || newDomain.isStandalone()) {
					createTaskHolder(ctx, newDomain, auth, user);
				}
			}

			userMail = email;

			return user;

		} else {
			throw new AonApiException("El email (" + email + ") no tiene un formato correcto.");
		}

	}

	private static String ramdonLogin() {
		Random rnd = new Random();
		Integer i = rnd.nextInt(100000000 - 10000000 + 1) + 10000000;
		return i.toString();
	}

	private static User createUser(AonApiData api, CloseableAONContext ctx, Domain newDomain, Auth auth,
			String login, String name) {
		Company newCompany = CompanyDAO.getCompanyStream(ctx, f -> f.getDomainProperty().eq(newDomain.getId()))
				.findFirst().get();

		User newUser = new User().setAuth(auth).setActive(true).setDomain(newDomain.getId())
				.setLogin(newCompany.getDocument())
				.setName(AonStringUtils.isNotBlank(name) ? name : newCompany.getDocument()).setShared(false)
				.setEnterprise(newCompany.getId()).setToolbar(UserToolbar.GOOGLE);

		newUser = SecurityDAO.save(ctx, newUser);

		SecurityDAO.updateUserPassword(ctx, newUser.getId(), auth.getPassword());

		Scope scope = getScope(ctx, newDomain, newUser);

		if (scope != null) {
			SecurityDAO.insertUserScope(ctx,
					new UserScope().setDomain(newDomain.getId()).setScope(scope.getId()).setUserId(newUser.getId()));
		}

		ApplicationParameter appParam = AppParamDAO.fetchOne(ctx, AppParam.AON_PORTAL);

		if (appParam == null || appParam.getId() == null) {

			appParam = new ApplicationParameter().setDomain(newDomain.getId()).setValue("288")
					.setName(AppParam.AON_PORTAL.getValue());

			AppParamDAO.insertApplicationParameter(ctx, appParam);
		}

		return newUser;
	}
	
	// DELETE FUTURE
	private static User createSigUser(AonApiData api, CloseableAONContext ctx, Domain newDomain, Scope newScope, Auth auth,
			String login, String name) {
		Company newCompany = CompanyDAO.getCompanyStream(ctx, f -> f.getDomainProperty().eq(newDomain.getId()))
				.findFirst().get();

		User newUser = new User().setAuth(auth).setActive(true).setDomain(newDomain.getId())
				.setLogin(login)
				.setName(AonStringUtils.isNotBlank(name) ? name : newCompany.getDocument()).setShared(false)
				.setEnterprise(newCompany.getId()).setToolbar(UserToolbar.GOOGLE);

		newUser = SecurityDAO.save(ctx, newUser);

		SecurityDAO.updateUserPassword(ctx, newUser.getId(), auth.getPassword());

		Scope scope = getScope(ctx, newDomain, newUser);

		if (scope != null) {
			SecurityDAO.insertUserScope(ctx,
					new UserScope().setDomain(newDomain.getId()).setScope(scope.getId()).setUserId(newUser.getId()));
		}

		ApplicationParameter appParam = AppParamDAO.fetchOne(ctx, AppParam.AON_PORTAL);

		if (appParam == null || appParam.getId() == null) {

			appParam = new ApplicationParameter().setDomain(newDomain.getId()).setValue("288")
					.setName(AppParam.AON_PORTAL.getValue());

			AppParamDAO.insertApplicationParameter(ctx, appParam);
		}

		return newUser;
	}

	private static Scope getScope(CloseableAONContext ctx, Domain newDomain, User newUser) {

		Scope s = SecurityDAO
				.getScopeStream(ctx,
						f -> f.getDomainProperty().eq(newDomain.getId()).and(f.getDescriptionProperty().eq("GENERAL")))
				.findFirst().orElse(null);

		if (s == null && newDomain.getParentId() != null) {
			s = SecurityDAO.getScopeStream(ctx, f -> f.getDomainProperty().eq(newDomain.getParentId())
					.and(f.getDescriptionProperty().eq("GENERAL"))).findFirst().orElse(null);
		}

		if (s == null) {
			s = SecurityDAO.getScopeStream(ctx, f -> f.getDomainProperty().eq(newDomain.getId())).findFirst()
					.orElse(null);
		}

		if (s == null && newDomain.getParentId() != null) {
			s = SecurityDAO.getScopeStream(ctx, f -> f.getDomainProperty().eq(newDomain.getParentId())).findFirst()
					.orElse(null);
		}

		return s;
	}

	private static void setUserAppRole(CloseableAONContext ctx, Domain newDomain, User user) {
		Integer userId = user.getId();

		LinkedList<AonRole> aRoles = SecurityDAO.getUserAppRoleStream(ctx, f -> f.getUserIdProperty().eq(userId))
				.map(r -> r.getRole()).collect(Collectors.toCollection(LinkedList::new));

		LinkedList<AonRole> tRoles = new LinkedList<AonRole>();
		tRoles.add(AonRole.ENTERPRISE);
		tRoles.add(AonRole.INVOICE_PORTAL);
		tRoles.add(AonRole.INVOICE);

		AonRole.stream().forEach(role -> {
			if (aRoles.contains(role) && !tRoles.contains(role)) {
				SecurityDAO.deleteUserAppRole(ctx, f -> f.getDomainProperty().eq(newDomain.getId())
						.and(f.getUserIdProperty().eq(userId)).and(f.getRoleProperty().eq(role.value())));
			}

			if (!aRoles.contains(role) && tRoles.contains(role)) {
				SecurityDAO.insertUserAppRole(ctx,
						new UserAppRole().setApp(null).setDomain(newDomain.getId()).setRole(role).setUser(userId));
			}
		});
		
		SecurityDAO.insertUserApplicationAio(ctx, newDomain.getId(), userId);
	}

	private static void createTaskHolder(CloseableAONContext ctx, Domain newDomain, Auth auth, User newUser) {

		String alias = AonStringUtils.isBlank(auth.getName()) ? newUser.getName()
				: auth.getName().length() > 32 ? auth.getName().substring(0, 31) : auth.getName();

		Registry newRegistry = RegistryDAO.save(ctx, 
				new Registry()
				.setDocument(auth.getDocument())
				.setDocumentType(DocumentType.identify(auth.getDocument()))
				.setName(AonStringUtils.isBlank(auth.getName()) ? newUser.getName() : auth.getName() + (AonStringUtils.isBlank(auth.getSurname()) ? "" : (" " + auth.getSurname())))
				.setAlias(alias).setDomain(newDomain));

		newUser.setRegistry(newRegistry);
		newUser = UserDAO.save(ctx, newUser);

		TaskHolder taskHolder = new TaskHolder().copy(newRegistry).setActive(true).setUserId(newUser.getId());

		taskHolder = TaskHolderDAO.save(ctx, taskHolder);

	}

	private static void createUserScope(AonApiData api, CloseableAONContext ctx, Domain newDomain, Scope newScope) {
		JSONObject data = api.getData();

		Integer sellerSupport = JsonUtils.getInteger(data, "sellerSupport");

		// Actualizar "user_scope": Asignar el scope al agente asignado
		Seller seller = SellerDAO.get(ctx, f -> f.getRegistryProperty().eq(sellerSupport));
		TaskHolder taskHolder = TaskHolderDAO.get(ctx, f -> f.getIdProperty().eq(seller.getTaskHolder().getId()),
				new Options().setFull(true));
		if (null != taskHolder && null != taskHolder.getUserId()) {

			User user = UserDAO.get(ctx, f -> f.getIdProperty().eq(taskHolder.getUserId()),
					new Options().setFull(true));

			SecurityDAO.insertUserScope(ctx, new UserScope().setDomain(user.getDomain().getId())
					.setScope(newScope.getId()).setUserId(user.getId()));

		}

		// Insertar el ambito al usuario que ha creado la empresa
		if (null != api.getUser() && null != api.getUser().getId())
			SecurityDAO.insertUserScope(ctx, new UserScope().setDomain(api.getUser().getDomain().getId())
					.setScope(newScope.getId()).setUserId(api.getUser().getId()));
	}

	private static void insertDomainConfiguration(CloseableAONContext ctx, Domain newDomain) {
		SecurityDAO.saveDomainMaxDefinedUser(ctx, newDomain.getId(), 1);

		DomainApp domainApp = new DomainApp().setDomain(newDomain.getId()).setApp(AonApp.INVOICE).setActive(true);

		SecurityDAO.saveDomainApp(ctx, domainApp, false);

		domainApp = new DomainApp().setDomain(newDomain.getId()).setApp(AonApp.DOCUMENTAL).setActive(true);

		SecurityDAO.saveDomainApp(ctx, domainApp, false);

		domainApp = new DomainApp().setDomain(newDomain.getId()).setApp(AonApp.MESSENGER).setActive(true);

		SecurityDAO.saveDomainApp(ctx, domainApp, false);
	}

	private static void insertAccountPeriod(CloseableAONContext ctx, Domain newDomain) {
		AccountPeriod accPeriod = new AccountPeriod().setDomain(newDomain.getId()).setName("EC")
				.setInitiationDate(AonDateUtils.getYearFirstDay(new Date()))
				.setDeadline(AonDateUtils.getYearLastDay(new Date())).setStatus(AccountPeriodStatus.ACTIVE);

		AccountPeriodDAO.save(ctx, accPeriod);
	}

	private static void createCustomerRaddinfo(AonApiData api, CloseableAONContext ctxSig, String schema,
			Domain newDomain, Integer customer) {
		Raddinfo domainNameRaddInfo = new Raddinfo().setDomain(api.getDomain().getId()).setRegistry(customer)
				.setAttribute("AON_DOMAIN0_NAME").setValue(newDomain.getName()).setValueDate(new Date());

		RegistryDAO.insertAddInfo(ctxSig, domainNameRaddInfo);

		Raddinfo domainIdRaddInfo = new Raddinfo().setDomain(api.getDomain().getId()).setRegistry(customer)
				.setAttribute("AON_DOMAIN0_ID").setValue(newDomain.getId().toString()).setValueDate(new Date());

		RegistryDAO.insertAddInfo(ctxSig, domainIdRaddInfo);

		Raddinfo domainSchemaRaddInfo = new Raddinfo().setDomain(api.getDomain().getId()).setRegistry(customer)
				.setAttribute("AON_DOMAIN0_SCHEMA").setValue(schema).setValueDate(new Date());

		RegistryDAO.insertAddInfo(ctxSig, domainSchemaRaddInfo);

		Raddinfo domainTypeRaddInfo = new Raddinfo().setDomain(api.getDomain().getId()).setRegistry(customer)
				.setAttribute("AON_DOMAIN0_TYPE").setValue(newDomain.getDomainType().getName())
				.setValueDate(new Date());

		RegistryDAO.insertAddInfo(ctxSig, domainTypeRaddInfo);

	}

	private static void createCustomerFee(AonApiData api, CloseableAONContext ctx) {
		JSONObject data = api.getData();

		Integer sellerSupport = JsonUtils.getInteger(data, "sellerSupport");
		Integer saleId = JsonUtils.getInteger(data, "saleId");
		Byte feePeriod = JsonUtils.getByte(data, "feePeriod");
		Integer feeWorkplace = JsonUtils.getInteger(data, "feeWorkplace");

		Sales sale = SalesDAO.get(ctx, f -> f.getIdProperty().eq(saleId), new Options().setFull(true));

		sale.getDetails().forEach(detail -> {

			Date feeStartDate = sale.getDate().before(sale.getDeliveryDate()) ? sale.getDate() : sale.getDeliveryDate();

			Fee fee = new Fee().setDomain(new Domain().setId(sale.getDomain())).setProject(sale.getProject())
					.setCustomer(sale.getCustomer()).setItem(new OldItem().setId(detail.getItem().getId()))
					.setDescription(detail.getDescription()).setQuantity(detail.getQuantity())
					.setPrice(detail.getPrice()).setDiscountExpr(detail.getDiscountExpression().getDiscountExpr())
					.setStartDate(feeStartDate).setEndDate(null)
					.setBillingDate(AonDateUtils.getMonthFirstDay(sale.getDeliveryDate()))
					.setPeriod(BillingPeriod.values()[feePeriod]).setSeller(new Seller().setId(sellerSupport))
					.setWorkplace(new Workplace().setId(feeWorkplace));

			FeeDAO.save(ctx, fee);
		});
	}

	private static void closeSale(AonApiData api, CloseableAONContext ctx) {
		JSONObject data = api.getData();

		Integer saleId = JsonUtils.getInteger(data, "saleId");

		Sales sale = SalesDAO.get(ctx, f -> f.getIdProperty().eq(saleId), new Options().setFull(true));
		sale.setStatus(SalesStatus.CLOSED);

		SalesDAO.save(ctx, sale);
	}

	// ---------------------------------------------------------------------------------------------
	// AUXILIAR METHODS
	// ---------------------------------------------------------------------------------------------
	
	// DELETE FUTURE
	private static void sendSigEnterpriseCreatedMail(AonApiData api, CloseableAONContext ctx) {
		JSONObject data = api.getData();
		Integer sellerSupport = JsonUtils.getInteger(data, "sellerSupport");
		String name = JsonUtils.getString(data, "name");
//		Integer registry = JsonUtils.getInteger(data, "registry");

		boolean isSig = JsonUtils.getboolean(data, "isSig");

		Domain parent = api.getDomain().isParent() ? api.getDomain()
				: DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(api.getDomain().getParentId()));

		String logoUrl = getLogoUrl(api, ctx);

		String from = getFromMessage(api, ctx, parent);

		if (AonStringUtils.isBlank(from) && !isSig)
			throw new AonApiException("No existe email definido en el entorno para la creaci\u00f3n de empresas");

		Stream<RegistryMedia> sellerSupportMedias = RegistryMediaDAO.getStream(ctx,
				f -> f.getRegistryProperty().eq(sellerSupport));
		Optional<RegistryMedia> sellerSupportEmailOpt = sellerSupportMedias
				.filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();

		if (sellerSupportEmailOpt.isEmpty() || AonStringUtils.isBlank(sellerSupportEmailOpt.get().getValue()))
			throw new AonApiException("No existe email para el agente de soporte seleccionado");

		List<String> bcc = new ArrayList<String>();
		if(isSig) bcc = List.of(sellerSupportEmailOpt.get().getValue());
		else bcc = List.of(sellerSupportEmailOpt.get().getValue(), from);

//		Optional<Target> targetOpt = TargetDAO.getStream(ctx, f -> f.getIdProperty().eq(registry)).findFirst();
//		Stream<RegistryMedia> targetMedias = RegistryMediaDAO.getStream(ctx,
//				f -> f.getRegistryProperty().eq(targetOpt.get().getId()));
//		Optional<RegistryMedia> targetEmailOpt = targetMedias.filter(media -> media.getMedia().equals(MediaType.EMAIL))
//				.findFirst();
//
//		if (targetEmailOpt.isEmpty())
//			throw new AonApiException("No existe email para el cliente potencial seleccionado");

		SESMessage msg = new SESMessage().setFrom(from)
				.setTo(bcc)
				.setReplyTo(from)
//				.setAlias(name)
				.setSubject("Empresa " + name).setBody(createEnterpriseCreatedBody(logoUrl, parent, from, name));

		SES.sendEmail(msg);
	}

	private static void sendEnterpriseCreatedMail(AonApiData api, CloseableAONContext ctx) {
		JSONObject data = api.getData();
		Integer sellerSupport = JsonUtils.getInteger(data, "sellerSupport");
		String name = JsonUtils.getString(data, "name");
		Integer registry = JsonUtils.getInteger(data, "registry");

		boolean isSig = JsonUtils.getboolean(data, "isSig");

		Domain parent = api.getDomain().isParent() ? api.getDomain()
				: DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(api.getDomain().getParentId()));

		String logoUrl = getLogoUrl(api, ctx);

		String from = getFromMessage(api, ctx, parent);

		if (AonStringUtils.isBlank(from) && !isSig)
			throw new AonApiException("No existe email definido en el entorno para la creaci\u00f3n de empresas");

		Stream<RegistryMedia> sellerSupportMedias = RegistryMediaDAO.getStream(ctx,
				f -> f.getRegistryProperty().eq(sellerSupport));
		Optional<RegistryMedia> sellerSupportEmailOpt = sellerSupportMedias
				.filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();

		if (sellerSupportEmailOpt.isEmpty() || AonStringUtils.isBlank(sellerSupportEmailOpt.get().getValue()))
			throw new AonApiException("No existe email para el agente de soporte seleccionado");

		List<String> bcc = new ArrayList<String>();
		if(isSig) bcc = List.of(sellerSupportEmailOpt.get().getValue());
		else bcc = List.of(sellerSupportEmailOpt.get().getValue(), from);

		Optional<Target> targetOpt = TargetDAO.getStream(ctx, f -> f.getIdProperty().eq(registry)).findFirst();
		Stream<RegistryMedia> targetMedias = RegistryMediaDAO.getStream(ctx,
				f -> f.getRegistryProperty().eq(targetOpt.get().getId()));
		Optional<RegistryMedia> targetEmailOpt = targetMedias.filter(media -> media.getMedia().equals(MediaType.EMAIL))
				.findFirst();

		if (targetEmailOpt.isEmpty())
			throw new AonApiException("No existe email para el cliente potencial seleccionado");

		SESMessage msg = new SESMessage().setFrom(from).setTo(targetEmailOpt.get().getValue())
				.setBcc(bcc)
				.setReplyTo(from)
//				.setAlias(name)
				.setSubject("Empresa " + name).setBody(createEnterpriseCreatedBody(logoUrl, parent, from, name));

		SES.sendEmail(msg);
	}

	private static String getLogoUrl(AonApiData api, CloseableAONContext ctx) {

		Domain parent = api.getDomain().isParent() ? api.getDomain()
				: DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(api.getDomain().getParentId()));

		Company company = CompanyDAO.getCompanyStream(ctx, f -> f.getDomainProperty().eq(parent.getId())).findFirst()
				.get();

		Attach attach = getLogoAttach(api, ctx, company.getId());

		String str = "domain=" + attach.getDomain().getId() + "&id=" + attach.getId() + "&attach_type=registry";
		String result = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
		String logoUrl = null;

		Domain attachDomain = DomainDAO.getDomain(ctx, attach.getDomain().getId());
		logoUrl = (isLocal ? "http" : "https") + "://" + parent.getName() + (isLocal ? ":8080" : "")
				+ "/ms/download_attachment/" + attachDomain.getName() + "/" + attach.getCreationUser() + "/" + result;

		return logoUrl;
	}

	private static Attach getLogoAttach(AonApiData api, CloseableAONContext ctx, Integer enterpriseId) {
		Optional<Attach> attach1 = AttachmentDAO.getRegistryAttachStream(ctx,
				f -> f.getTypeProperty().eq(SIGNATURE.value()).and(f.getAttachModuleProperty().eq(enterpriseId)), true)
				.findFirst();

		return attach1
				.isPresent()
						? attach1.get()
						: AttachmentDAO
								.getRegistryAttachStream(ctx,
										f -> f.getTypeProperty().eq(LOGO.value())
												.and(f.getAttachModuleProperty().eq(enterpriseId)),
										true)
								.findFirst().get();
	}

	private static String getFromMessage(AonApiData api, CloseableAONContext ctx, Domain parent) {
		DomainUserRoles domainUserRoles = SecurityDAO.getDomainUserRoles(ctx, api.getUser().getId());

		String from = null;

		if (domainUserRoles.hasParentCustomView() || domainUserRoles.hasCustomView()) {
			// Ya es el dominio padre el que hay en api.getDomain()
			if (null == api.getDomain().getParentId() && (null == parent || null == parent.getId())) {
				Enterprise enterprise = EnterpriseDAO.get(ctx, f -> f.getDomainProperty().eq(api.getDomain().getId()));
				RegistryMedia emailMedia = RegistryMediaDAO.get(ctx,
						f -> f.getRegistryProperty().eq(enterprise.getId()).and(f.getMediaProperty().eq((byte) 4)));
				if (null != emailMedia && AonStringUtils.isNotBlank(emailMedia.getValue()))
					from = emailMedia.getValue();

				// Se busca el dominio padre
			} else if (null != parent && null != parent.getId()) {
				Enterprise enterprise = EnterpriseDAO.get(ctx, f -> f.getDomainProperty().eq(parent.getId()));
				RegistryMedia emailMedia = RegistryMediaDAO.get(ctx,
						f -> f.getRegistryProperty().eq(enterprise.getId()).and(f.getMediaProperty().eq((byte) 4)));
				if (null != emailMedia && AonStringUtils.isNotBlank(emailMedia.getValue()))
					from = emailMedia.getValue();
			}
		}

		return from;
	}

	private static String createEnterpriseCreatedBody(String logoUrl, Domain parentDomain, String from, String name) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();

		String url = (isLocal ? "http" : "https") + "://" + parentDomain.getName() + (isLocal ? ":8080" : "") + "/app";

		if (AonStringUtils.equalsIgnoreCase(parentDomain.getName(), "app.leevy.es"))
			url = "https://leevy.aon.solutions";
		else if (AonStringUtils.equalsIgnoreCase(parentDomain.getName(), "infoautonomos.aonsolutions.net"))
			url = "https://infoautonomos.aon.solutions";

		VelocityContext context = new VelocityContext();
		context.put("logo", logoUrl);
		context.put("parentName", parentDomain.getDescription());
		context.put("name", name);
		context.put("url", url);
		context.put("domainName", urlMail);
		context.put("user", userMail);
		context.put("password", passwordMail);
		context.put("contact", AonStringUtils.isBlank(from) ? "booking@aonsolutions.es" : from);

		Template template = engine
				.getTemplate("/net/aonsolutions/aon/api/servlet/templates/registry_enterprise_created.vm");

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
	
	private static String createEnterpriseSyncBody(String logoUrl, Domain parentDomain, String from, String name) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();

		String url = (isLocal ? "http" : "https") + "://" + parentDomain.getName() + (isLocal ? ":8080" : "") + "/app";

		VelocityContext context = new VelocityContext();
		context.put("logo", logoUrl);
		context.put("parentName", parentDomain.getDescription());
		context.put("name", name);
		context.put("url", url);
		context.put("domainName", urlMail);
		context.put("contact", AonStringUtils.isBlank(from) ? "booking@aonsolutions.es" : from);

		Template template = engine
				.getTemplate("/net/aonsolutions/aon/api/servlet/templates/registry_enterprise_sync_ayudat.vm");

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}
	
	private static String createEnterpriseSyncUserBody(String logoUrl, Domain parentDomain, String from, String name) {
		VelocityEngine engine = new VelocityEngine();
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		engine.init();

		String url = (isLocal ? "http" : "https") + "://" + parentDomain.getName() + (isLocal ? ":8080" : "") + "/app";

		VelocityContext context = new VelocityContext();
		context.put("logo", logoUrl);
		context.put("parentName", parentDomain.getDescription());
		context.put("name", name);
		context.put("url", url);
		context.put("domainName", urlMail);
		context.put("user", userMail);
		context.put("password", passwordMail);
		context.put("contact", AonStringUtils.isBlank(from) ? "booking@aonsolutions.es" : from);

		Template template = engine
				.getTemplate("/net/aonsolutions/aon/api/servlet/templates/registry_enterprise_sync_user_ayudat.vm");

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		return writer.toString();
	}

}
