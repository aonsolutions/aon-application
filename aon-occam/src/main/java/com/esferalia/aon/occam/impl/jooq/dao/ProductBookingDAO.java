package com.esferalia.aon.occam.impl.jooq.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.AonRole;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.aonsolutions.UserAppRole;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ProductBooking;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryRelationship;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserType;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;


public class ProductBookingDAO {
	
	// Private constructor to prevent instantiation
	private ProductBookingDAO() {
	
	}

	public static void updateBookingProduct(CloseableAONContext ctx, String domainName, Integer domainId, String login, Integer customerRelatedRegistry, Optional<Fee> oldFee, ProductBooking product, Fee newFee) {
		Domain siblingOffice = getOfficeSibling(ctx, customerRelatedRegistry);
		
		if(oldFee.isPresent()) {
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

	}
	
	public static void createBookingProduct(CloseableAONContext ctx, String domainName, Integer domainId, String login, Integer customerRelatedRegistry, ProductBooking product, Fee newFee) {
		Domain siblingOffice = getOfficeSibling(ctx, customerRelatedRegistry);
		
		createFeeRelatedRegistry(ctx, login, siblingOffice, customerRelatedRegistry, newFee);
		createBookingFee(ctx, login, siblingOffice, customerRelatedRegistry, product);
		
		if(null != product.getProjectType()) {
			Optional<RegistryRelationship> existRelationShip = RegistryRelationshipDAO.get(ctx,  f -> f.getRelatedRegistryProperty().eq(customerRelatedRegistry).and(f.getDomainProperty().eq(siblingOffice.getId())).and(f.getRelationshipProperty().eq(-1)));
			if(!existRelationShip.isPresent()) throw new IllegalArgumentException("Este cliente ya no está vinculado al dominio");
			
			createUpdateProductProject(ctx, existRelationShip.get().getRegistry(), product);
		}

	}
	
	public static void removeBookingProduct(CloseableAONContext ctx, String domainName, Integer domainId, String login, Integer customerRelatedRegistry, Optional<Fee> oldFee, ProductBooking product) {
		Domain siblingOffice = getOfficeSibling(ctx, customerRelatedRegistry);
		
		if(oldFee.isPresent()) {
			updateBookingProduct(ctx, domainId, login, siblingOffice, oldFee.get());
			updateEndDatePackFee(ctx, siblingOffice, oldFee.get());
		}

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
			SecurityDAO.saveDomainApp(ctx, domainApp.get(), delete);
		
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
		
		SecurityDAO.saveDomainApp(ctx, domainApp, true);
		
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
	}
	
}
