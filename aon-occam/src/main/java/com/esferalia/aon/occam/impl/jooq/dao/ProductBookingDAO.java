package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.LOGO;
import static com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType.SIGNATURE;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.AonRole;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.payroll.Enterprise;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ProductBooking;
import com.esferalia.aon.occam.api.model.product.ProductBookingType;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryRelationship;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.TaskHolderWorkgroup;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.api.model.security.UserType;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.MediaType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;


public class ProductBookingDAO {
	
	// Private constructor to prevent instantiation
	private ProductBookingDAO() {
	
	}

	public static void updateBookingProduct(CloseableAONContext ctx, String domainName, Integer domainId, String login, Integer customerRelatedRegistry, Optional<Fee> oldFee, ProductBooking product, Fee newFee) {
		Domain siblingOffice = getOfficeSibling(ctx, customerRelatedRegistry);
		
		ProductBooking feeProductBooking = null;
		if(oldFee.isPresent()) {
			Item feeItem = ItemDAO.getFull(ctx, f -> f.getDomainProperty().eq(siblingOffice.getId()).and(f.getIdProperty().eq(oldFee.get().getItem().getId())));
			feeProductBooking =  ProductDAO.getBooking(ctx, f -> f.getDomainProperty().eq(siblingOffice.getId()).and(f.getIdProperty().eq(feeItem.getProduct().getId())));
			
			updateBookingProduct(ctx, domainId, login, siblingOffice, oldFee.get());
			updateEndDatePackFee(ctx, siblingOffice, oldFee.get());
			
		}
		
		createFeeRelatedRegistry(ctx, login, siblingOffice, customerRelatedRegistry, newFee);
		createBookingFee(ctx, login, siblingOffice, customerRelatedRegistry, product);
		
		if(null != product.getProjectType()) {
			Optional<RegistryRelationship> existRelationShip = RegistryRelationshipDAO.get(ctx,  f -> f.getRelatedRegistryProperty().eq(customerRelatedRegistry).and(f.getDomainProperty().eq(siblingOffice.getId())).and(f.getRelationshipProperty().eq(-1)));
			if(!existRelationShip.isPresent()) throw new IllegalArgumentException("Este cliente ya no está vinculado al dominio");
			
			createUpdateProductProject(ctx, existRelationShip.get().getRegistry(), product);
		}
		
		if(null != feeProductBooking)
			sendUnBookingEmail(ctx, siblingOffice, domainId, login, customerRelatedRegistry, feeProductBooking);
		
		sendBookingEmail(ctx, siblingOffice, domainId, login, customerRelatedRegistry, product);

	}

	public static void createBookingProduct(CloseableAONContext ctx, String domainName, Integer domainId, String login, Integer customerRelatedRegistry, ProductBooking product, Fee newFee) {
		Domain siblingOffice = getOfficeSibling(ctx, customerRelatedRegistry);
		
		if(product.getBookingType().equals(ProductBookingType.USER))
			createUserFeeRelatedRegistry(ctx, login, siblingOffice, customerRelatedRegistry, newFee);
		else {
			createFeeRelatedRegistry(ctx, login, siblingOffice, customerRelatedRegistry, newFee);
			createBookingFee(ctx, login, siblingOffice, customerRelatedRegistry, product);
		}
		
		if(null != product.getProjectType()) {
			Optional<RegistryRelationship> existRelationShip = RegistryRelationshipDAO.get(ctx,  f -> f.getRelatedRegistryProperty().eq(customerRelatedRegistry).and(f.getDomainProperty().eq(siblingOffice.getId())).and(f.getRelationshipProperty().eq(-1)));
			if(!existRelationShip.isPresent()) throw new IllegalArgumentException("Este cliente ya no está vinculado al dominio");
			
			createUpdateProductProject(ctx, existRelationShip.get().getRegistry(), product);
		}

//		sendBookingEmail(ctx, siblingOffice, domainId, login, customerRelatedRegistry, product);
	}
	
	public static void removeBookingProduct(CloseableAONContext ctx, String domainName, Integer domainId, String login, Integer customerRelatedRegistry, Optional<Fee> oldFee, ProductBooking product) {
		Domain siblingOffice = getOfficeSibling(ctx, customerRelatedRegistry);
		
		if(oldFee.isPresent()) {
			Item feeItem = ItemDAO.getFull(ctx, f -> f.getDomainProperty().eq(siblingOffice.getId()).and(f.getIdProperty().eq(oldFee.get().getItem().getId())));
			ProductBooking feeProductBooking =  ProductDAO.getBooking(ctx, f -> f.getDomainProperty().eq(siblingOffice.getId()).and(f.getIdProperty().eq(feeItem.getProduct().getId())));
			
			updateBookingProduct(ctx, domainId, login, siblingOffice, oldFee.get());
			updateEndDatePackFee(ctx, siblingOffice, oldFee.get());
			
			sendUnBookingEmail(ctx, siblingOffice, domainId, login, customerRelatedRegistry, feeProductBooking);
		}

	}

	public static void requestBookingInfo(CloseableAONContext ctx, int domainId, String login, ProductBooking product, Integer customerRegistry) {
		Domain officeDomain = DomainDAO.getDomain(ctx, domainId);
		Customer customer = CustomerDAO.get(ctx, customerRegistry);
		sendRequestBookingInfogEmail(ctx, officeDomain, login, customer, product);
	}

	private static Domain getOfficeSibling(CloseableAONContext ctx, Integer customerRelatedRegistry) {
		Optional<RegistryRelationship> rrletationShip = RegistryRelationshipDAO.get(ctx,  f -> f.getRelatedRegistryProperty().eq(customerRelatedRegistry).and(f.getRelationshipProperty().eq(-1)));
		
		Domain officeSiblingDomain = null;
		if(rrletationShip.isPresent())
			officeSiblingDomain = DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(rrletationShip.get().getDomain().getId()));
		else 
			officeSiblingDomain = DomainDAO.getDomain(ctx, f -> f.getTypeProperty().eq(DomainType.OFFICE.value()).and(f.getActiveProperty().eq((byte)1)));
		
		return officeSiblingDomain;
	}
	
	private static void updateEndDatePackFee(CloseableAONContext ctx, Domain siblingOffice, Fee fee) {
		Date currentDate = new Date();
		Date yesterday = AonDateUtils.addDays(currentDate, -1);
		
		if(fee.getStartDate().after(yesterday)) {
			FeeDAO.delete(ctx, fee);
		} else {
			fee.setEndDate(yesterday);
			FeeDAO.save(ctx, fee);
		}
		
		closeProductProject(ctx, siblingOffice, fee);
	}
	
	private static void closeProductProject(CloseableAONContext ctx, Domain siblingOffice, Fee fee) {
		Item feeItem = ItemDAO.getFull(ctx, f -> f.getDomainProperty().eq(siblingOffice.getId()).and(f.getIdProperty().eq(fee.getItem().getId())));
		
		ProductBooking feeProductBooking =  ProductDAO.getBooking(ctx, f -> f.getDomainProperty().eq(siblingOffice.getId()).and(f.getIdProperty().eq(feeItem.getProduct().getId())));
		
		if(null != feeProductBooking.getProjectType())
			closeProductProject(ctx, fee.getCustomer().getId(), feeProductBooking);
	}
	
	private static void closeProductProject(CloseableAONContext ctx, Integer registryId, ProductBooking product) {
		Project project = ProjectDAO.getFull(ctx, f -> f.getDomainProperty().eq(product.getDomain().getId()).and(f.getProjectTypeProperty().eq(product.getProjectType().getId())).and(f.getRegistryProperty().eq(registryId)));
		
		// Inactivar expediente
		if(null != project && null != project.getId()){
			
			project.setActive(false);
			
			if(!project.getProjectHolders().isEmpty()) {
				
				Optional<ProjectHolder> projectHolderOpt = project.getProjectHolders().stream().filter(ph -> null == ph.getEndDate()).findFirst();
				
				if(!projectHolderOpt.isEmpty()) {
					projectHolderOpt.get().setEndDate(new Date());
				}
			}
			
			 ProjectDAO.save(ctx, project);
				
		}
	}
	
	private static void updateBookingProduct(CloseableAONContext ctx, Integer domainId, String login, Domain siblingOffice, Fee oldFee) {
		Date currentDate = new Date();
		Date yesterday = AonDateUtils.addDays(currentDate, -1);
		
		User userDb = SecurityDAO.getUser(ctx, login);
		
		Item feeItem = ItemDAO.getFull(ctx, f -> f.getDomainProperty().eq(siblingOffice.getId()).and(f.getIdProperty().eq(oldFee.getItem().getId())));
		
		ProductBooking feeProductBooking =  ProductDAO.getBooking(ctx, f -> f.getDomainProperty().eq(siblingOffice.getId()).and(f.getIdProperty().eq(feeItem.getProduct().getId())));
		
		feeProductBooking.getAonApps().forEach(aonApp -> {
			updateDomainApp(ctx, domainId, userDb.getId(), aonApp, oldFee.getStartDate().after(yesterday));
		});
	}
	
	private static void updateDomainApp(CloseableAONContext ctx, Integer domainId, Integer userId, AonApp aonApp, boolean delete) {
		Stream<DomainApp> domainApps = SecurityDAO.getDomainAppStream(ctx, f -> f.getDomainProperty().eq(domainId));
		Optional<DomainApp> domainApp = domainApps.filter(da -> da.getApp().equals(aonApp)).findFirst();
		
		if(domainApp.isPresent())
			SecurityDAO.saveDomainApp(ctx, domainApp.get(), false);
		
		updateUserAppRole(ctx, domainId, userId, aonApp);
	}
	
	private static void updateUserAppRole(CloseableAONContext ctx, Integer domainId, Integer userId, AonApp aonApp) {
		List<AonRole> aonRoles = AonApp.getPortalAonRole(aonApp);
		Byte[] arrayAonRoles = aonRoles.stream()
			    .map(ar -> ar.value())
			    .toArray(Byte[]::new);
		
		List<UserAppRole> userAppRoles = SecurityDAO.getUserAppRoleStream(ctx, f -> f.getUserIdProperty().eq(userId).and(f.getRoleProperty().in(arrayAonRoles))).collect(Collectors.toList());
		
		if(!userAppRoles.isEmpty())
			userAppRoles.forEach(userAppRole -> SecurityDAO.deleteUserAppRole(ctx, f -> f.getIdProperty().eq(userAppRole.getId()).and(f.getDomainProperty().eq(domainId))));
		
	}
	
	private static void createFeeRelatedRegistry(CloseableAONContext ctx, String login, Domain siblingOffice, Integer customerRelatedRegistry, Fee fee) throws AonCoreException {
		Optional<RegistryRelationship> existRelationShip = RegistryRelationshipDAO.get(ctx,  f -> f.getRelatedRegistryProperty().eq(customerRelatedRegistry).and(f.getDomainProperty().eq(siblingOffice.getId())).and(f.getRelationshipProperty().eq(-1)));
		if(!existRelationShip.isPresent()) throw new IllegalArgumentException("Este cliente ya no está vinculado al dominio");
		
		Workplace workplace = WorkplaceDAO.getWorkplacesNoScope(ctx, siblingOffice.getId())
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Este cliente ya no está vinculado al dominio"));
		
		fee.setCustomer(new Customer().setId(existRelationShip.get().getRegistry()));
		fee.setWorkplace(workplace);
		
		Item feeItem = ItemDAO.getFull(ctx, f -> f.getDomainProperty().eq(siblingOffice.getId()).and(f.getIdProperty().eq(fee.getItem().getId())));
		
		ProductBooking feeProductBooking =  ProductDAO.getBooking(ctx, f -> f.getDomainProperty().eq(siblingOffice.getId()).and(f.getIdProperty().eq(feeItem.getProduct().getId())));
		
		if(null != feeProductBooking.getProjectType()) {
			Project project = createUpdateProductProject(ctx, siblingOffice, existRelationShip.get().getRegistry(), feeProductBooking);
			fee.setProject(project);
		}
		
		FeeDAO.save(ctx, fee);
	}
	
	private static void createUserFeeRelatedRegistry(CloseableAONContext ctx, String login, Domain siblingOffice, Integer customerRelatedRegistry, Fee fee) throws AonCoreException {
		Optional<RegistryRelationship> existRelationShip = RegistryRelationshipDAO.get(ctx,  f -> f.getRelatedRegistryProperty().eq(customerRelatedRegistry).and(f.getDomainProperty().eq(siblingOffice.getId())).and(f.getRelationshipProperty().eq(-1)));
		if(!existRelationShip.isPresent()) throw new IllegalArgumentException("Este cliente ya no está vinculado al dominio");
		
		Workplace workplace = WorkplaceDAO.getWorkplacesNoScope(ctx, siblingOffice.getId())
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Este cliente ya no está vinculado al dominio"));
		
		fee.setCustomer(new Customer().setId(existRelationShip.get().getRegistry()));
		fee.setWorkplace(workplace);
		
		// Existing fee
		Fee existingUserFee = FeeDAO.getFeeStream(ctx, 
				f -> f.getDomainProperty().eq(siblingOffice.getId())
				.and(f.getCustomerProperty().eq(existRelationShip.get().getRegistry()))
				.and(f.getItemProperty().eq(fee.getItem().getId()))
			).findFirst().orElse(null);
		
		// Existe cuota
		if(null != existingUserFee && null != existingUserFee.getId()) {
			Date firstDayOfMonth = AonDateUtils.getMonthFirstDay(new Date());
			
			Date existFeeBillingDate = existingUserFee.getBillingDate();
			if(existFeeBillingDate.equals(firstDayOfMonth)) {
				
				fee.setStartDate(existingUserFee.getStartDate());
				fee.setQuantity(existingUserFee.getQuantity() + 1.00);
				fee.setBillingDate(getNewBillingDate(existingUserFee.getBillingDate(), existingUserFee.getPeriod()));
				FeeDAO.save(ctx, fee);
				
				existingUserFee.setEndDate(AonDateUtils.getMonthLastDay(existingUserFee.getBillingDate()));
				FeeDAO.save(ctx, existingUserFee);
				
			} else {
				existingUserFee.setQuantity(existingUserFee.getQuantity() + 1.00);
				FeeDAO.save(ctx, existingUserFee);
			}
			
		} 
		// No existe cuota
		else {
			FeeDAO.save(ctx, fee);
		}
	}
	
	private static Date getNewBillingDate(Date billingDate, BillingPeriod period) {
		switch (period) {
		case MONTHLY:
			return AonDateUtils.getMonthLastDay( AonDateUtils.addMonths(billingDate, 1) );
		case BI_MONTHLY:
			return AonDateUtils.getMonthLastDay( AonDateUtils.addMonths(billingDate, 2) );
		case THREE_MONTHLY:
			return AonDateUtils.getMonthLastDay( AonDateUtils.addMonths(billingDate, 3) );
		case FOUR_MONTHLY:
			return AonDateUtils.getMonthLastDay( AonDateUtils.addMonths(billingDate, 4) );
		case SIX_MONTHLY:
			return AonDateUtils.getMonthLastDay( AonDateUtils.addMonths(billingDate, 6) );
		case YEARLY:
			return AonDateUtils.getMonthLastDay( AonDateUtils.addYears(billingDate, 1) );
		default:
			return AonDateUtils.getMonthLastDay( billingDate );
		}
	}

	private static Project createUpdateProductProject(CloseableAONContext ctx, Domain siblingOffice, Integer registryId, ProductBooking product) {
		Project project = ProjectDAO.getFull(ctx, f -> f.getDomainProperty().eq(product.getDomain().getId()).and(f.getProjectTypeProperty().eq(product.getProjectType().getId())).and(f.getRegistryProperty().eq(registryId)));
		// Crear expediente
		if(null == project || null == project.getId()){
			
			Project newProject = new Project();
			newProject.setDomain(product.getDomain());
			newProject.setType(product.getProjectType());
			newProject.setRegistry(new Registry().setId(registryId));
			newProject.setName(product.getCode() + " (AutoContratacion)");
			newProject.setDate(new Date());
			newProject.setActive(true);	
			
			if(null != product.getWorkgroup() || null != product.getTaskHolder()) {
				newProject.setProjectHolders(
						List.of(
							new ProjectHolder()
								.setDomain(product.getDomain().getId())
								.setWorkgroup(product.getWorkgroup())
								.setTaskHolder(product.getTaskHolder())
								.setStartDate(new Date())
						));
			}
			
			
			newProject = ProjectDAO.save(ctx, newProject);
			return newProject;
			
		// Actualizar expediente	
		} else {
			
			project.setActive(true);
			
			if(!project.getProjectHolders().isEmpty()) {
				
				Optional<ProjectHolder> projectHolderOpt = project.getProjectHolders().stream().filter(ph -> null == ph.getEndDate()).findFirst();
				
				if(!projectHolderOpt.isEmpty())
					projectHolderOpt.get().setEndDate(new Date());
				
			}
			
			if(null != product.getWorkgroup() || null != product.getTaskHolder())
				project.getProjectHolders().add(
						new ProjectHolder()
							.setDomain(product.getDomain().getId())
							.setWorkgroup(product.getWorkgroup())
							.setTaskHolder(product.getTaskHolder())
							.setStartDate(new Date())
				);
				
			ProjectDAO.save(ctx, project);
			
		}
		
		return project;
	}

	private static void createBookingFee(CloseableAONContext ctx, String login, Domain siblingOffice, Integer customerRelatedRegistry, ProductBooking product) {
		Optional<RegistryRelationship> existRelationShip = RegistryRelationshipDAO.get(ctx,  f -> f.getRelatedRegistryProperty().eq(customerRelatedRegistry).and(f.getDomainProperty().eq(siblingOffice.getId())).and(f.getRelationshipProperty().eq(-1)));
		if(!existRelationShip.isPresent()) throw new IllegalArgumentException("Este cliente ya no está vinculado al dominio");
		
		User userDb = SecurityDAO.getUser(ctx, login);
		
		// Set Trial false
		ApplicationParameter trialAppParam = AppParamDAO.fetchOne(ctx, AppParam.TRIAL.getValue());
		if(null != trialAppParam.getId()) {
			trialAppParam.setValue("-1");
			AppParamDAO.updateApplicationParameter(ctx, trialAppParam, f -> f.getIdProperty().eq(trialAppParam.getId()).and(f.getDomainProperty().eq(trialAppParam.getDomain())));
		}
		
		// If plan check exist or create admin role
		if(product.getBookingType().equals(ProductBookingType.PLAN) || product.getBookingType().equals(ProductBookingType.CONSULTANCY)) {
			Optional<UserAppRole> adminUserRole = SecurityDAO.getUserAppRoleStream(ctx, f -> f.getUserIdProperty().eq(userDb.getId())).filter(r -> r.getRole().equals(AonRole.ADMIN)).findAny();
			if(adminUserRole.isEmpty()) {
				UserAppRole newUserAppRole = new UserAppRole()
						.setDomain(userDb.getDomain().getId())
						.setApp(null)
						.setUser(userDb.getId())
						.setRole(AonRole.ADMIN);
				
				SecurityDAO.insertUserAppRole(ctx, newUserAppRole);
			}
		}
		
		// Set User normal
		if(!userDb.getType().equals(UserType.NORMAL) || null != userDb.getEnterprise()) {
			userDb.setType(UserType.NORMAL);
			userDb.setEnterprise(null);
			
			SecurityDAO.save(ctx, userDb);
			
			List<AonRole> aonUserAppRoles = List.of(AonRole.ENTERPRISE, AonRole.EMPLOYEE);
			Byte[] arrayAonRoles = aonUserAppRoles.stream()
				    .map(ar -> ar.value())
				    .toArray(Byte[]::new);
			
			List<UserAppRole> userAppRoles = SecurityDAO.getUserAppRoleStream(ctx, f -> f.getUserIdProperty().eq(userDb.getId()).and(f.getRoleProperty().in(arrayAonRoles))).collect(Collectors.toList());
			userAppRoles.forEach(userAppRole -> SecurityDAO.deleteUserAppRole(ctx, f -> f.getIdProperty().eq(userAppRole.getId())));
			
		}
		
		product.getAonApps().forEach(aonApp -> {
			insertDomainApp(ctx, userDb, aonApp);
		});
		
	}
	
	private static void insertDomainApp(CloseableAONContext ctx, User user, AonApp aonApp) {
		DomainApp domainApp = new DomainApp()
				.setDomain(user.getDomain().getId())
				.setActive(true)
				.setApp(aonApp)
				;
		
		SecurityDAO.saveDomainApp(ctx, domainApp, false);
		
		insertUserAppRole(ctx, user, aonApp);
	}
	
	private static void insertUserAppRole(CloseableAONContext ctx, User user, AonApp aonApp) {
		List<AonRole> aonRoles = AonApp.getPortalAonRole(aonApp);
		Byte[] arrayAonRoles = aonRoles.stream()
			    .map(ar -> ar.value())
			    .toArray(Byte[]::new);
		
		List<UserAppRole> userAppRole = SecurityDAO.getUserAppRoleStream(ctx, f -> f.getUserIdProperty().eq(user.getId()).and(f.getRoleProperty().in(arrayAonRoles))).collect(Collectors.toList());
		
		if(userAppRole.isEmpty()) {
			aonRoles.forEach(aonRole -> {
				UserAppRole newUserAppRole = new UserAppRole()
						.setDomain(user.getDomain().getId())
						.setApp(null)
						.setUser(user.getId())
						.setRole(aonRole);
				
				SecurityDAO.insertUserAppRole(ctx, newUserAppRole);
			});
			
		} 
	}
	
	private static void createUpdateProductProject(CloseableAONContext ctx, Integer registryId, ProductBooking product) {
		Project project = ProjectDAO.getFull(ctx, f -> f.getDomainProperty().eq(product.getDomain().getId()).and(f.getProjectTypeProperty().eq(product.getProjectType().getId())).and(f.getRegistryProperty().eq(registryId)));
		
		// Crear expediente
		if(null == project || null == project.getId()){
			
			Project newProject = new Project();
			newProject.setDomain(product.getDomain());
			newProject.setType(product.getProjectType());
			newProject.setRegistry(new Registry().setId(registryId));
			newProject.setName(product.getCode() + " (AutoContratacion)");
			newProject.setDate(new Date());
			newProject.setActive(true);	
			
			if(null != product.getWorkgroup() || null != product.getTaskHolder()) {
				newProject.setProjectHolders(
						List.of(
							new ProjectHolder()
								.setDomain(product.getDomain().getId())
								.setWorkgroup(product.getWorkgroup())
								.setTaskHolder(product.getTaskHolder())
								.setStartDate(new Date())
						));
				
				if(null != product.getTaskHolder() && null != product.getTaskHolder().getId())
					createTaskHolderUserScope(ctx, registryId, product);
			}
			
			ProjectDAO.save(ctx, newProject);
			
		// Actualizar expediente	
		} else {
			
			project.setActive(true);
			
			if(!project.getProjectHolders().isEmpty()) {
				
				Optional<ProjectHolder> projectHolderOpt = project.getProjectHolders().stream().filter(ph -> null == ph.getEndDate()).findFirst();
				
				if(!projectHolderOpt.isEmpty())
					projectHolderOpt.get().setEndDate(new Date());
				
			}
			
			if(null != product.getWorkgroup() || null != product.getTaskHolder()) {
				project.getProjectHolders().add(
						new ProjectHolder()
							.setDomain(product.getDomain().getId())
							.setWorkgroup(product.getWorkgroup())
							.setTaskHolder(product.getTaskHolder())
							.setStartDate(new Date())
				);
				
				if(null != product.getTaskHolder() && null != product.getTaskHolder().getId())
					createTaskHolderUserScope(ctx, registryId, product);
			}
				
			ProjectDAO.save(ctx, project);
		}
	}
	
	private static void createTaskHolderUserScope(CloseableAONContext ctx, Integer registryId, ProductBooking product) {
		Domain domain = product.getDomain();
		Domain parentDomain = DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(domain.getParentId()));

		Registry registry = RegistryDAO.get(ctx, registryId);
		
		List<Scope> scopes = SecurityDAO.getScopeStream(ctx, f -> f.getDescriptionProperty().eq(registry.getDocument()).and(f.getDomainProperty().eq(null == parentDomain.getId() ? domain.getId() : parentDomain.getId()))).collect(Collectors.toList());

		Project project = ProjectDAO.getFull(ctx, f -> f.getDomainProperty().eq(product.getDomain().getId()).and(f.getProjectTypeProperty().eq(product.getProjectType().getId())).and(f.getRegistryProperty().eq(registryId)));
		Optional<ProjectHolder> projectHolder = project.getProjectHolders().stream().filter(ph -> null == ph.getEndDate()).findFirst();
		if(projectHolder.isPresent()) {
			TaskHolder taskHolder = projectHolder.get().getTaskHolder();
			Workgroup workgroup = projectHolder.get().getWorkgroup();
			
			if(null != taskHolder && null != taskHolder.getId() && null != taskHolder.getUser() && null != taskHolder.getUser().getId()) {
				Scope scope = null;
				if (scopes.size() == 0) {
					Scope newScope = new Scope().setDomain(null == parentDomain.getId() ? domain.getId() : parentDomain.getId()).setDescription(registry.getDocument());
					scope = SecurityDAO.insertScope(ctx, newScope);
				} else scope = scopes.stream().findFirst().get();
				
				User user = UserDAO.get(ctx, f -> f.getIdProperty().eq(taskHolder.getUser().getId()),
						new Options().setFull(true));
				
				SecurityDAO.insertUserScope(ctx, new UserScope().setDomain(user.getDomain().getId())
						.setScope(scope.getId()).setUserId(user.getId()));
			
			} else if(null != workgroup && null != workgroup.getId()) {
				List<TaskHolderWorkgroup> taskHolderWorkgroups = TaskHolderWorkgroupDAO.getList(ctx, f -> f.getWorkgroupProperty().eq(workgroup.getId()));
				taskHolderWorkgroups.forEach(taskHolderWorkgroup -> {
					
					TaskHolder taskHolderWG = TaskHolderDAO.get(ctx, taskHolderWorkgroup.getTaskHolder());
					
					if(null != taskHolderWG && null != taskHolderWG.getId() && null != taskHolderWG.getUser() && null != taskHolderWG.getUser().getId()) {
						Scope scope = null;
						if (scopes.size() == 0) {
							Scope newScope = new Scope().setDomain(null == parentDomain.getId() ? domain.getId() : parentDomain.getId()).setDescription(registry.getDocument());
							scope = SecurityDAO.insertScope(ctx, newScope);
						} else scope = scopes.stream().findFirst().get();
						
						User user = UserDAO.get(ctx, f -> f.getIdProperty().eq(taskHolderWG.getUser().getId()),
								new Options().setFull(true));
						
						SecurityDAO.insertUserScope(ctx, new UserScope().setDomain(user.getDomain().getId())
								.setScope(scope.getId()).setUserId(user.getId()));
					}
				});
			}
		}
		
		
		
	}

	private static void sendBookingEmail(CloseableAONContext ctx, Domain siblingOffice, Integer domainId, String login, Integer customerRelatedRegistry, ProductBooking product) {
		Optional<RegistryRelationship> existRelationShip = RegistryRelationshipDAO.get(ctx,  f -> f.getRelatedRegistryProperty().eq(customerRelatedRegistry).and(f.getDomainProperty().eq(siblingOffice.getId())).and(f.getRelationshipProperty().eq(-1)));
		if(!existRelationShip.isPresent()) throw new IllegalArgumentException("Este cliente ya no está vinculado al dominio");
		
		Domain domain = DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(domainId));
		Domain parent = DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(domain.getParentId()));
		
		User userDb = SecurityDAO.getUser(ctx, login);
		
		String logoUrl = getLogoUrl(ctx, parent, userDb);

		String from = getFromMessage(ctx, parent, domain, userDb);
		
		if(!AonStringUtils.isBlank(from)) {
			List<String> bccs = new ArrayList<String>();
			
			if(product.getProjectType() != null && null != product.getProjectType().getId()) {
	 			Project project = ProjectDAO.getFull(ctx, f -> f.getDomainProperty().eq(product.getDomain().getId()).and(f.getProjectTypeProperty().eq(product.getProjectType().getId())).and(f.getRegistryProperty().eq(existRelationShip.get().getRegistry())));
	 			Optional<ProjectHolder> projectHolder = project.getProjectHolders().stream().filter(ph -> null == ph.getEndDate()).findFirst();
	 			if(projectHolder.isPresent()) {
	 				TaskHolder taskHolder = projectHolder.get().getTaskHolder();
	 				if(null != taskHolder && null != taskHolder.getId()) {
	 					Stream<RegistryMedia> taskHolderMedias = RegistryMediaDAO.getStream(ctx, f -> f.getRegistryProperty().eq(taskHolder.get().getId()));
	 					Optional<RegistryMedia> taskHolderEmailOpt = taskHolderMedias.filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();
	 					
	 					if(taskHolderEmailOpt.isPresent() && AonStringUtils.isNotBlank(taskHolderEmailOpt.get().getValue())) {
	 						bccs.add(taskHolderEmailOpt.get().getValue());
	 					}
	 				}
	 				
	 				Workgroup workgroup = projectHolder.get().getWorkgroup();
	 				if(null != workgroup && null != workgroup.getId() && bccs.isEmpty()) {
	 					List<TaskHolderWorkgroup> taskHolderWorkgroups = TaskHolderWorkgroupDAO.getList(ctx, f -> f.getWorkgroupProperty().eq(workgroup.getId()));
	 					taskHolderWorkgroups.forEach(taskHolderWorkgroup -> {
	 						Stream<RegistryMedia> taskHolderMedias = RegistryMediaDAO.getStream(ctx, f -> f.getRegistryProperty().eq(taskHolderWorkgroup.getTaskHolder()));
	 	 					Optional<RegistryMedia> taskHolderEmailOpt = taskHolderMedias.filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();
	 	 					
	 	 					if(taskHolderEmailOpt.isPresent() && AonStringUtils.isNotBlank(taskHolderEmailOpt.get().getValue())) {
	 	 						bccs.add(taskHolderEmailOpt.get().getValue());
	 	 					}
	 					});
	 				}
	 			}
			}
 			
 			String bookingBcc = "booking@aonsolutions.es";
			bccs.add(bookingBcc);
			
			Optional<Target> targetOpt = TargetDAO.getStream(ctx, f -> f.getIdProperty().eq(existRelationShip.get().getRegistry())).findFirst();
			Stream<RegistryMedia> targetMedias = RegistryMediaDAO.getStream(ctx, f -> f.getRegistryProperty().eq(targetOpt.get().getId()));
			Optional<RegistryMedia> targetEmailOpt = targetMedias.filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();
			
			if(targetEmailOpt.isPresent()) {
				
				SESMessage msg = new SESMessage()
						.setAlias(parent.getDescription())
						.setFrom(from)
						.setTo(targetEmailOpt.get().getValue())
						.setBcc(bccs)
						.setReplyTo(from)
						.setSubject("Actualizaciones en la contrataci\u00f3n")
						.setBody(getProductoBookingTemplate(parent, logoUrl, product))
						;

				SES.sendEmail(msg);
				
			}	
		}
			
	}
	
	private static void sendUnBookingEmail(CloseableAONContext ctx, Domain siblingOffice, Integer domainId, String login, Integer customerRelatedRegistry, ProductBooking product) {
		Optional<RegistryRelationship> existRelationShip = RegistryRelationshipDAO.get(ctx,  f -> f.getRelatedRegistryProperty().eq(customerRelatedRegistry).and(f.getDomainProperty().eq(siblingOffice.getId())).and(f.getRelationshipProperty().eq(-1)));
		if(!existRelationShip.isPresent()) throw new IllegalArgumentException("Este cliente ya no está vinculado al dominio");
		
		Domain domain = DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(domainId));
		Domain parent = DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(domain.getParentId()));
		
		User userDb = SecurityDAO.getUser(ctx, login);
		
		String logoUrl = getLogoUrl(ctx, parent, userDb);

		String from = getFromMessage(ctx, parent, domain, userDb);
		
		if(!AonStringUtils.isBlank(from)) {
			List<String> bccs = new ArrayList<String>();
			
			if(product.getProjectType() != null && null != product.getProjectType().getId()) {
	 			Project project = ProjectDAO.getFull(ctx, f -> f.getDomainProperty().eq(product.getDomain().getId()).and(f.getProjectTypeProperty().eq(product.getProjectType().getId())).and(f.getRegistryProperty().eq(existRelationShip.get().getRegistry())));
	 			Optional<ProjectHolder> projectHolder = project.getProjectHolders().stream().filter(ph -> null == ph.getEndDate()).findFirst();
	 			if(projectHolder.isPresent()) {
	 				TaskHolder taskHolder = projectHolder.get().getTaskHolder();
	 				if(null != taskHolder && null != taskHolder.getId()) {
	 					Stream<RegistryMedia> taskHolderMedias = RegistryMediaDAO.getStream(ctx, f -> f.getRegistryProperty().eq(taskHolder.get().getId()));
	 					Optional<RegistryMedia> taskHolderEmailOpt = taskHolderMedias.filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();
	 					
	 					if(taskHolderEmailOpt.isPresent() && AonStringUtils.isNotBlank(taskHolderEmailOpt.get().getValue())) {
	 						bccs.add(taskHolderEmailOpt.get().getValue());
	 					}
	 				}
	 				
	 				Workgroup workgroup = projectHolder.get().getWorkgroup();
	 				if(null != workgroup && null != workgroup.getId() && bccs.isEmpty()) {
	 					List<TaskHolderWorkgroup> taskHolderWorkgroups = TaskHolderWorkgroupDAO.getList(ctx, f -> f.getWorkgroupProperty().eq(workgroup.getId()));
	 					taskHolderWorkgroups.forEach(taskHolderWorkgroup -> {
	 						Stream<RegistryMedia> taskHolderMedias = RegistryMediaDAO.getStream(ctx, f -> f.getRegistryProperty().eq(taskHolderWorkgroup.getTaskHolder()));
	 	 					Optional<RegistryMedia> taskHolderEmailOpt = taskHolderMedias.filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();
	 	 					
	 	 					if(taskHolderEmailOpt.isPresent() && AonStringUtils.isNotBlank(taskHolderEmailOpt.get().getValue())) {
	 	 						bccs.add(taskHolderEmailOpt.get().getValue());
	 	 					}
	 					});
	 				}
	 			}
			}
 			
 			String bookingBcc = "booking@aonsolutions.es";
			bccs.add(bookingBcc);
			
			Optional<Target> targetOpt = TargetDAO.getStream(ctx, f -> f.getIdProperty().eq(existRelationShip.get().getRegistry())).findFirst();
			Stream<RegistryMedia> targetMedias = RegistryMediaDAO.getStream(ctx, f -> f.getRegistryProperty().eq(targetOpt.get().getId()));
			Optional<RegistryMedia> targetEmailOpt = targetMedias.filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();
			
			if(targetEmailOpt.isPresent()) {
				
				SESMessage msg = new SESMessage()
						.setAlias(parent.getDescription())
						.setFrom(from)
						.setTo(targetEmailOpt.get().getValue())
						.setBcc(bccs)
						.setReplyTo(from)
						.setSubject("Actualizaciones en la contrataci\u00f3n")
						.setBody(getProductoUnBookingTemplate(parent, logoUrl, product))
						;

				SES.sendEmail(msg);
				
			}	
		}
			
	}
	
	private static void sendRequestBookingInfogEmail(CloseableAONContext ctx, Domain officeDomain, String login, Customer customer, ProductBooking product) {
		Domain parent = DomainDAO.getDomain(ctx, f -> f.getIdProperty().eq(officeDomain.getParentId()));
		
		User userDb = SecurityDAO.getUser(ctx, login);
		
		String logoUrl = getLogoUrl(ctx, parent, userDb);

		String from = getFromMessage(ctx, parent, officeDomain, userDb);
		
		if(!AonStringUtils.isBlank(from)) {
			List<String> productTaskHolders = new ArrayList<String>();
			
			TaskHolder taskHolder = product.getTaskHolder();
			if(null != taskHolder && null != taskHolder.getId()) {
				Stream<RegistryMedia> taskHolderMedias = RegistryMediaDAO.getStream(ctx, f -> f.getRegistryProperty().eq(taskHolder.get().getId()));
				Optional<RegistryMedia> taskHolderEmailOpt = taskHolderMedias.filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();
				
				if(taskHolderEmailOpt.isPresent() && AonStringUtils.isNotBlank(taskHolderEmailOpt.get().getValue())) {
					productTaskHolders.add(taskHolderEmailOpt.get().getValue());
				}
			}
			
			Workgroup workgroup = product.getWorkgroup();
			if(null != workgroup && null != workgroup.getId() && productTaskHolders.isEmpty()) {
				List<TaskHolderWorkgroup> taskHolderWorkgroups = TaskHolderWorkgroupDAO.getList(ctx, f -> f.getWorkgroupProperty().eq(workgroup.getId()));
				taskHolderWorkgroups.forEach(taskHolderWorkgroup -> {
					Stream<RegistryMedia> taskHolderMedias = RegistryMediaDAO.getStream(ctx, f -> f.getRegistryProperty().eq(taskHolderWorkgroup.getTaskHolder()));
 					Optional<RegistryMedia> taskHolderEmailOpt = taskHolderMedias.filter(media -> media.getMedia().equals(MediaType.EMAIL)).findFirst();
 					
 					if(taskHolderEmailOpt.isPresent() && AonStringUtils.isNotBlank(taskHolderEmailOpt.get().getValue())) {
 						productTaskHolders.add(taskHolderEmailOpt.get().getValue());
 					}
				});
			}
 			
 			String bookingBcc = "booking@aonsolutions.es";
			productTaskHolders.add(bookingBcc);
 			
 			if(!productTaskHolders.isEmpty()) {
				SESMessage msg = new SESMessage()
				.setAlias(parent.getDescription())
				.setFrom(from)
				.setTo(productTaskHolders)
				.setReplyTo(from)
				.setSubject("Solicitud de informaci\u00f3n contrataci\u00f3n")
				.setBody(getRequestBookingInfoTemplate(parent, logoUrl, product, customer.getName()))
				;

				SES.sendEmail(msg);
 			} else 
 				throw new AonCoreException("No existe agente de soporte para este producto");
			
		}
	}
	
	public static String getLogoUrl(CloseableAONContext ctx, Domain parentDomain, User user) {
		String logoUrl = null;
		
		Company company = CompanyDAO.getCompanyStream(ctx, f -> f.getDomainProperty().eq(parentDomain.getId())).findFirst().orElse(null);

		Attach attach = getLogoAttach(ctx, company.getId());

		String str = "domain=" + attach.getDomain().getId() + "&id=" + attach.getId() + "&attach_type=registry";
		String result = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));

		Domain attachDomain = DomainDAO.getDomain(ctx, attach.getDomain().getId());
		logoUrl = "https" + "://" + parentDomain.getName()	+ "/ms/download_attachment/" + attachDomain.getName() + "/" + attach.getCreationUser() + "/" + result;

		return logoUrl;
	}

	public static Attach getLogoAttach(CloseableAONContext ctx, Integer enterpriseId) {
		Attach attach1 = AttachmentDAO.getRegistryAttachStream(ctx, f -> f.getTypeProperty().eq(SIGNATURE.value()).and(f.getAttachModuleProperty().eq(enterpriseId)), true).findFirst().orElse(null);
		
		if (attach1 == null || attach1.getData() == null)
			attach1 = AttachmentDAO.getRegistryAttachStream(ctx, f -> f.getTypeProperty().eq(LOGO.value()).and(f.getAttachModuleProperty().eq(enterpriseId)), true).findFirst().orElse(null);
		
		return attach1;
	}
	
	public static String getFromMessage(CloseableAONContext ctx, Domain parent, Domain domain, User user) {
		DomainUserRoles domainUserRoles = SecurityDAO.getDomainUserRoles(ctx, user.getId()); 
		
		String from = null;

		if (domainUserRoles.hasParentCustomView() || domainUserRoles.hasCustomView()) {
			
			// Ya es el dominio padre el que hay en api.getDomain()
			if (null == domain.getParentId() && (null == parent || null == parent.getId())) {
				
				Enterprise enterprise = EnterpriseDAO.get(ctx, f -> f.getDomainProperty().eq(domain.getId()));
				
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
	
	private static String getProductoBookingTemplate(Domain parent, String logoUrl, ProductBooking product) {
		String template = "";
		
		template += 
				  "<!DOCTYPE html>"
				+ "<html lang=\"es\">"
				+ "<head>"
				+ "    <meta charset=\"UTF-8\">"
				+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
				+ "    <title>Contratación - Actualización de la contratación</title>"
				+ "</head>"
				+ "<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif;\">"
				+ "    <table role=\"presentation\" style=\"width: 100%; border-collapse: collapse;\">"
				+ "        <!-- Header con logo -->"
				+ "        <tr>"
				+ "            <td style=\"padding: 30px 20px; text-align: center;\">"
				+ "                <img src=\"" + logoUrl + "\" alt=\"" + parent.getDescription() + "\" style=\"max-width: 200px; height: auto; border-radius: 10px; display: block; margin: 0 auto;\">"
				+ "            </td>"
				+ "        </tr>"
				+ "        "
				+ "        <!-- Contenido principal -->"
				+ "        <tr>"
				+ "            <td style=\"padding: 30px; color: #333333; line-height: 1.6; font-size: 16px;\">"
				+ "                <p style=\"margin: 0 0 25px 0; font-size: 18px; font-weight: bold;\">"
				+ "                    &#x00A1;Enhorabuena! Acabas de ampliar tu contrataci\u00f3n. Aquí te dejamos el " + (product.getBookingType().equals(ProductBookingType.PLAN) ? "plan" : "servicio") + " contratado:"
				+ "                </p>"
				+ "                "
				+ "                <!-- Datos de acceso -->"
				+ "                <div style=\"border-left: 4px solid #007bff; padding: 20px; margin: 25px 0; border-radius: 4px;\">"
				+ "                    <h3 style=\"margin: 0 0 15px 0; color: #007bff; font-size: 18px;\">" + (product.getBookingType().equals(ProductBookingType.PLAN) ? "Plan" : "Servicio") + " contratado</h3>"
				+ "                    <p style=\"margin: 8px 0; color: #007bff; text-decoration: none;\"><strong>Producto: </strong>" + product.getCode() + "</p>"
				+ "                </div>"
				+ "                "
				+ "                <h3 style=\"margin: 20px; color: #333333; font-size: 18px; border-bottom: 2px solid #e9ecef; padding-bottom: 10px;\">"
				+ "                    Guía rápida con todo lo que puedes hacer"
				+ "                </h3>"
				+ "                "
				+ "                <!-- Primer inicio de sesión -->"
				+ "                <div style=\"margin: 20px 0;\">";
		
		template += product.getDescriptionTemplate();
				
		template += "                </div>"
				+ "                <p style=\"margin: 30px 0 0 0;\">"
				+ "                    Un saludo,<br>"
				+ "                    <strong>" +  parent.getDescription() + "</strong>"
				+ "                </p>"
				+ "            </td>"
				+ "        </tr>"
				+ "        "
				+ "        <!-- Footer opcional -->"
				+ "        <tr>"
				+ "            <td style=\"padding: 20px; text-align: center; color: #666666; font-size: 12px;\">"
				+ "                <p style=\"margin: 0;\">Este es un correo automático, por favor no respondas a este mensaje.</p>"
				+ "            </td>"
				+ "        </tr>"
				+ "    </table>"
				+ "</body>"
				+ "</html>";
		
		return template;
	}
	
	private static String getProductoUnBookingTemplate(Domain parent, String logoUrl, ProductBooking product) {
		String template = "";
		
		template += 
				  "<!DOCTYPE html>"
				+ "<html lang=\"es\">"
				+ "<head>"
				+ "    <meta charset=\"UTF-8\">"
				+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
				+ "    <title>Contratación - Actualización de la contratación</title>"
				+ "</head>"
				+ "<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif;\">"
				+ "    <table role=\"presentation\" style=\"width: 100%; border-collapse: collapse;\">"
				+ "        <!-- Header con logo -->"
				+ "        <tr>"
				+ "            <td style=\"padding: 30px 20px; text-align: center;\">"
				+ "                <img src=\"" + logoUrl + "\" alt=\"" + parent.getDescription() + "\" style=\"max-width: 200px; height: auto; border-radius: 10px; display: block; margin: 0 auto;\">"
				+ "            </td>"
				+ "        </tr>"
				+ "        "
				+ "        <!-- Contenido principal -->"
				+ "        <tr>"
				+ "            <td style=\"padding: 30px; color: #333333; line-height: 1.6; font-size: 16px;\">"
				+ "                <p style=\"margin: 0 0 25px 0; font-size: 18px; font-weight: bold;\">"
				+ "                    Acabas de modificar tu contrataci\u00f3n. Aquí te dejamos el " + (product.getBookingType().equals(ProductBookingType.PLAN) ? "plan" : "servicio") + " descontratado:"
				+ "                </p>"
				+ "                "
				+ "                <!-- Datos de acceso -->"
				+ "                <div style=\"border-left: 4px solid #007bff; padding: 20px; margin: 25px 0; border-radius: 4px;\">"
				+ "                    <h3 style=\"margin: 0 0 15px 0; color: #007bff; font-size: 18px;\">" + (product.getBookingType().equals(ProductBookingType.PLAN) ? "Plan" : "Servicio") + " descontratado</h3>"
				+ "                    <p style=\"margin: 8px 0; color: #007bff; text-decoration: none;\"><strong>Producto: </strong>" + product.getCode() + "</p>"
				+ "                </div>"
				+ "                <p style=\"margin: 30px 0 0 0;\">"
				+ "                    Un saludo,<br>"
				+ "                    <strong>" +  parent.getDescription() + "</strong>"
				+ "                </p>"
				+ "            </td>"
				+ "        </tr>"
				+ "        "
				+ "        <!-- Footer opcional -->"
				+ "        <tr>"
				+ "            <td style=\"padding: 20px; text-align: center; color: #666666; font-size: 12px;\">"
				+ "                <p style=\"margin: 0;\">Este es un correo automático, por favor no respondas a este mensaje.</p>"
				+ "            </td>"
				+ "        </tr>"
				+ "    </table>"
				+ "</body>"
				+ "</html>";
		
		return template;
	}
	
	private static String getRequestBookingInfoTemplate(Domain parent, String logoUrl, ProductBooking product, String customerName) {
		String template = "";
		
		template += 
				  "<!DOCTYPE html>"
				+ "<html lang=\"es\">"
				+ "<head>"
				+ "    <meta charset=\"UTF-8\">"
				+ "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
				+ "    <title>Solicitud - Información de la contratación</title>"
				+ "</head>"
				+ "<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif;\">"
				+ "    <table role=\"presentation\" style=\"width: 100%; border-collapse: collapse;\">"
				+ "        <!-- Header con logo -->"
				+ "        <tr>"
				+ "            <td style=\"padding: 30px 20px; text-align: center;\">"
				+ "                <img src=\"" + logoUrl + "\" alt=\"" + parent.getDescription() + "\" style=\"max-width: 200px; height: auto; border-radius: 10px; display: block; margin: 0 auto;\">"
				+ "            </td>"
				+ "        </tr>"
				+ "        "
				+ "        <!-- Contenido principal -->"
				+ "        <tr>"
				+ "            <td style=\"padding: 30px; color: #333333; line-height: 1.6; font-size: 16px;\">"
				+ "                <p style=\"margin: 0 0 25px 0; font-size: 18px; font-weight: bold;\">"
				+ "                    El cliente <b>" + customerName + "</b> acaba de solicitar informaci\u00f3n relativa a una nueva contrataci\u00f3n."
				+ "                </p>"
				+ "                "
				+ "                <!-- Datos de acceso -->"
				+ "                <div style=\"border-left: 4px solid #007bff; padding: 20px; margin: 25px 0; border-radius: 4px;\">"
				+ "                    <h3 style=\"margin: 0 0 15px 0; color: #007bff; font-size: 18px;\">" + (product.getBookingType().equals(ProductBookingType.PLAN) ? "Plan" : "Servicio") + " solicitado</h3>"
				+ "                    <p style=\"margin: 8px 0; color: #007bff; text-decoration: none;\"><strong>Producto: </strong>" + product.getCode() + "</p>"
				+ "                </div>"
				+ "                <h3 style=\"margin: 20px; color: #333333; font-size: 18px; border-bottom: 2px solid #e9ecef; padding-bottom: 10px;\">"
				+ "                    Guía rápida con todo lo que puedes hacer"
				+ "                </h3>"
				+ "                "
				+ "                <!-- Primer inicio de sesión -->"
				+ "                <div style=\"margin: 20px 0;\">";
		
		template += product.getDescriptionTemplate();
		
		template += 
				"                  <p style=\"margin: 30px 0 0 0;\">"
				+ "                    Un saludo,<br>"
				+ "                    <strong>" +  parent.getDescription() + "</strong>"
				+ "                </p>"
				+ "            </td>"
				+ "        </tr>"
				+ "        "
				+ "        <!-- Footer opcional -->"
				+ "        <tr>"
				+ "            <td style=\"padding: 20px; text-align: center; color: #666666; font-size: 12px;\">"
				+ "                <p style=\"margin: 0;\">Este es un correo automático, por favor no respondas a este mensaje.</p>"
				+ "            </td>"
				+ "        </tr>"
				+ "    </table>"
				+ "</body>"
				+ "</html>";
		
		return template;
	}
	
}
