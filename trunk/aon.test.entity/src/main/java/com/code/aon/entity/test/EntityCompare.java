package com.code.aon.entity.test;


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.MethodInfo;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
public class EntityCompare {
	
	private int warnings = 0;
	private int errors = 0;
	
	public void execute() throws IOException, NotFoundException, ClassNotFoundException {
		List<String> toCheckClasses = getToCheckClasses();
		warnings = 0;
		errors = 0;
		for (String pojo: toCheckClasses) {
			checkClass(pojo);	
		}
		System.out.println( "\n\n");
		System.out.println("WARNINGS : " + warnings + " ERRORS : " + errors );
	}

	private void checkClass(String className) throws NotFoundException, IOException, ClassNotFoundException {
		System.out.println("\n\n" + className);
		String trunkArtifact = null;
		if (className.startsWith("com.code.aon.account.bridge")) {
			trunkArtifact = "aon.account.bridge";
		} else if (className.startsWith("com.code.aon.account.")) {
			trunkArtifact = "aon.account";
		} else if (className.startsWith("com.code.aon.config")) {
			trunkArtifact = "aon.config";
		} else if (className.startsWith("com.code.aon.audit")) {
			trunkArtifact = "aon.audit";
		} else if (className.startsWith("com.esferalia.aon.calendar")) {
			trunkArtifact = "aon.calendar";
		} else if (className.startsWith("com.code.aon.geozone")) {
			trunkArtifact = "aon.geozone";
		} else if (className.startsWith("com.code.aon.person")) {
			trunkArtifact = "aon.registry";
		} else if (className.startsWith("com.code.aon.registry")) {
			trunkArtifact = "aon.registry";
		} else if (className.startsWith("com.code.aon.company")) {
			trunkArtifact = "aon.company";
		} else if (className.startsWith("com.code.aon.customer")) {
			trunkArtifact = "aon.customer";
		} else if (className.startsWith("com.code.aon.project")) {
			trunkArtifact = "aon.project";
		} else if (className.startsWith("com.code.aon.groupware")) {
			trunkArtifact = "aon.groupware";
		} else if (className.startsWith("com.code.aon.messaging")) {
			trunkArtifact = "aon.messaging";
		} else if (className.startsWith("com.esferalia.aon.payroll")) {
			trunkArtifact = "aon.payroll";
		} else if (className.startsWith("com.code.aon.supplier")) {
			trunkArtifact = "aon.supplier";
		} else if (className.startsWith("com.code.aon.product")) {
			trunkArtifact = "aon.product";
		} else if (className.startsWith("com.code.aon.seller")) {
			trunkArtifact = "aon.seller";
		} else if (className.startsWith("com.code.aon.commercial")) {
			trunkArtifact = "aon.commercial";
		} else if (className.startsWith("com.code.aon.tas")) {
			trunkArtifact = "aon.tas";
		} else if (className.startsWith("com.code.aon.purchase")) {
			trunkArtifact = "aon.purchase";
		} else if (className.startsWith("com.code.aon.sales")) {
			trunkArtifact = "aon.sales";
		} else if (className.startsWith("com.code.aon.warehouse")) {
			trunkArtifact = "aon.warehouse";
		} else if (className.startsWith("com.code.aon.finance")) {
			trunkArtifact = "aon.finance";
		} else if (className.startsWith("com.code.aon.accounting")) {
			trunkArtifact = "aon.accounting";
		} else if (className.startsWith("com.code.aon.fiscal")) {
			trunkArtifact = "aon.fiscal";
		} else if (className.startsWith("com.code.aon.asset")) {
			trunkArtifact = "aon.asset";
		} else if (className.startsWith("com.code.aon.infoweb")) {
			trunkArtifact = "aon.infoweb";
		} else if (className.startsWith("com.code.aon.marketing")) {
			trunkArtifact = "aon.marketing";
		}
		TrunkLoader trunkLoader = new TrunkLoader(trunkArtifact);
		String cn = className;
		if ("com.code.aon.marketing.MarketingCampaign".equals(className)) {
			cn = "com.code.aon.marketing.Campaign"; 
		}
		CtClass trunk = trunkLoader.getClass(cn);
		ClassPool pool = ClassPool.getDefault();
		CtClass entity = pool.get(className);
		checkClass(trunk,entity);
	}
	
	private void checkClass(CtClass trunk, CtClass entity) throws NotFoundException, ClassNotFoundException {
		checkName(trunk, entity);
		checkInterfaces(trunk, entity);
		checkConstructors(trunk, entity);
		checkFields(trunk, entity);
		checkMethods(trunk,entity);
	}

	private void checkConstructors(CtClass trunk, CtClass entity) throws NotFoundException {
		CtConstructor[] constructors = trunk.getConstructors();
		CtConstructor[] i21 = entity.getConstructors();
		CtConstructor[] i22 = entity.getSuperclass().getConstructors();
		CtConstructor[] entityConstructors= (CtConstructor[]) ArrayUtils.addAll(i21,i22);
		for ( CtConstructor trunkConstructor : constructors ) {
			String trunkSignature = getConstructorSignature(trunkConstructor);
			System.out.println("\tConstructor: " + trunkSignature);
			boolean found = false;
			for ( CtConstructor entityConstructor : entityConstructors ) {
				String entitySignature = getConstructorSignature(entityConstructor);
				if (StringUtils.equals(trunkSignature, entitySignature)) {
					checkConstructorBody(trunkConstructor,entityConstructor);
					found = true;
					break;
				}
			}
			if (!found) {
				error("La clase " + entity.getName() + " no tiene el método " + trunkConstructor.getName());
			}
		}
		
	}

	private void checkName(CtClass trunk, CtClass entity) {
		if (!StringUtils.equals(trunk.getName(), entity.getName())) {
			error("Las clases no se llaman igual. " + trunk.getName() + " -- " + entity.getName());
		}
		System.out.println("\t Nombre ok!");
	}

	private void error(String msg) {
		++errors;
		System.out.println("\n[ERROR] " + msg);
	}
	private void warning(String msg) {
		++warnings;
		System.out.println("\n[WARNING] " + msg);
	}

	private void checkInterfaces(CtClass trunk, CtClass entity) throws NotFoundException {
		CtClass[] trunkInterfaces = trunk.getInterfaces();
		CtClass[] i21 = entity.getInterfaces();
		CtClass[] i22 = entity.getSuperclass().getInterfaces();
		CtClass[] entityInterfaces = (CtClass[]) ArrayUtils.addAll(i21,i22);
		if (trunkInterfaces == null) {
			error("Las clase no implementa ningún interfaz. " + trunk.getName());
		} else if (entityInterfaces == null) {
			error("Las clase no implementa ningún interfaz. " + entity.getName());
		} else {
			for (CtClass trunkInterface: trunkInterfaces) {
				boolean found = false;
				for (CtClass entityInterface: entityInterfaces) {
					if (StringUtils.equals(trunkInterface.getName(), entityInterface.getName())) {
						found = true;
						break;
					}
				}
				if (!found) {
					error("Las clase " + entity.getName() + " no implementa el interfaz " + trunkInterface.getName());
				} else {
					System.out.println("\t Implementa " + trunkInterface.getName() + " ok!");	
				}
			}
		}
	}

	private void checkFields(CtClass trunk, CtClass entity) throws NotFoundException, ClassNotFoundException {
		CtField[] f1 = entity.getDeclaredFields();
		CtField[] f2 = entity.getSuperclass().getDeclaredFields();
		CtField[] entityFields = (CtField[]) ArrayUtils.addAll(f1,f2);
		
		for ( CtField trunkField : trunk.getDeclaredFields() ) {
			System.out.print( "\tCampo: " + trunkField.getName());
			boolean found = false;
			for ( CtField entityField : entityFields ) {
				if (StringUtils.equals(trunkField.getName(), entityField.getName())) {
					System.out.print( "\t " + trunkField.getType().getName());
					checkFieldType(trunkField,entityField);
					checkAnnotation(trunkField,entityField);
					found = true;
					break;
				}
			}
			if (!found) {
				error("La clase " + entity.getName() + " no tiene el campo " + trunkField.getName());
			}
			System.out.println();
		}
	}

	private void checkAnnotation(CtField trunkField, CtField entityField) throws ClassNotFoundException {
		if (trunkField.getAnnotations() != null && trunkField.getAnnotations().length > 0) {
			warning("[WARNING] El campo (trunk) " + entityField.getName() + " de la clase " + 
					entityField.getDeclaringClass().getName() + " tiene Annotations");
		}
		if (entityField.getAnnotations() != null && entityField.getAnnotations().length > 0) {
			warning("[WARNING] El campo (entity) " + entityField.getName() + " de la clase " + 
					entityField.getDeclaringClass().getName() + " tiene Annotations");
		}
		
	}

	private void checkFieldType(CtField trunkField, CtField entityField) throws NotFoundException {
		if (!StringUtils.equals(trunkField.getType().getName(), entityField.getType().getName())) {
			error("El campo " + entityField.getName() + " de la clase " + entityField.getDeclaringClass().getName() + " no tiene el mismo tipo. ["+trunkField.getType().getName() +" - "+ entityField.getType().getName()+ "]");
		}
	}
	
	private void checkMethods(CtClass trunk, CtClass entity) throws ClassNotFoundException, NotFoundException {
		CtMethod[] f1 = entity.getDeclaredMethods();
		CtMethod[] f2 = entity.getSuperclass().getDeclaredMethods();
		CtMethod[] entityMethods = (CtMethod[]) ArrayUtils.addAll(f1,f2);
		for ( CtMethod trunkMethod : trunk.getDeclaredMethods() ) {
			String trunkSignature = getMethodSignature(trunkMethod);
			System.out.println("\tMétodo: " + trunkSignature);
			boolean found = false;
			for ( CtMethod entityMethod : entityMethods ) {
				String entitySignature = getMethodSignature(entityMethod);
				if (StringUtils.equals(trunkSignature, entitySignature)) {
					checkMethodAnnotations(trunkMethod,entityMethod);
					checkMethodBody(trunkMethod,entityMethod);
					found = true;
					break;
				}
			}
			if (!found) {
				error("La clase " + entity.getName() + " no tiene el método " + trunkMethod.getName());
			}
		}
	}

	private void checkConstructorBody(CtConstructor trunkMethod, CtConstructor entityMethod) {
		MethodInfo trunkMethodInfo = trunkMethod.getMethodInfo();
		CodeAttribute trunkCodeAttribute = trunkMethodInfo.getCodeAttribute();
		int trunkLength = trunkCodeAttribute.getCodeLength();
		MethodInfo entityMethodInfo = entityMethod.getMethodInfo();
		CodeAttribute entityCodeAttribute = entityMethodInfo.getCodeAttribute();
		int entityLength = entityCodeAttribute.getCodeLength();
		
		if (entityLength != trunkLength) {
			error("El constructor " + trunkMethod.getName()
					+ " de la clase " + entityMethod.getDeclaringClass().getName()
					+ " tiene un body diferente del de Trunk");
		}
	}

	private void checkMethodBody(CtMethod trunkMethod, CtMethod entityMethod) {
		String methodName =  trunkMethod.getName();
		if (!"equals".equals(methodName) && !"hashCode".equals(methodName) && !"toString".equals(methodName)) {
			ClassFile trunkFile = trunkMethod.getDeclaringClass().getClassFile();
			MethodInfo trunkMethodInfo = trunkFile.getMethod(trunkMethod.getName());
			CodeAttribute trunkCodeAttribute = trunkMethodInfo.getCodeAttribute();
			int trunkLength = trunkCodeAttribute.getCodeLength();
			
			ClassFile entityFile = entityMethod.getDeclaringClass().getClassFile();
			MethodInfo entityMethodInfo = entityFile.getMethod(entityMethod.getName());
			CodeAttribute entityCodeAttribute = entityMethodInfo.getCodeAttribute();
			int entityLength = entityCodeAttribute.getCodeLength();
			
			if (entityLength != trunkLength) {
				warning("El método " + trunkMethod.getName()
						+ " de la clase " + entityMethod.getDeclaringClass().getName()
						+ " tiene un body diferente del de Trunk");
			}
		}
	}

	private String getMethodSignature(CtMethod method) throws NotFoundException {
		StringBuffer buf = new StringBuffer();
		buf.append(method.getReturnType().getName());
		buf.append(" ");
		buf.append(method.getMethodInfo().getName());
		buf.append("(");
		for (CtClass parameter : method.getParameterTypes()) {
			buf.append(parameter.getName());	
		}
		buf.append(")");
		return buf.toString();
	}

	private String getConstructorSignature(CtConstructor method) throws NotFoundException {
		StringBuffer buf = new StringBuffer();
		buf.append(method.getMethodInfo().getName());
		buf.append("(");
		for (CtClass parameter : method.getParameterTypes()) {
			buf.append(parameter.getName());	
		}
		buf.append(")");
		return buf.toString();
	}

	private void checkMethodAnnotations(CtMethod trunkMethod, CtMethod entityMethod) throws ClassNotFoundException {
		Object[] trunkAnnotations = trunkMethod.getAnnotations();
		Object[] entityAnnotations = entityMethod.getAnnotations();
		if (trunkAnnotations != null && trunkAnnotations.length > 0) {
			for (Object trunkAnnotation : trunkAnnotations) {
				String ta = trunkAnnotation.toString();
				if (!StringUtils.contains(ta,"@javax.persistence.GeneratedValue") &&
					!StringUtils.contains(ta,"@org.hibernate.annotations.ForeignKey") &&
					!StringUtils.contains(ta,"@org.hibernate.annotations.Index") &&
				    !StringUtils.contains(ta,"@javax.persistence.Column")) {
					if (StringUtils.contains(ta,"@javax.persistence.ManyToOne")) {
						ta = ta + "(fetch=javax.persistence.FetchType.EAGER)"; 
					}
					boolean found = false;
					for ( Object entityAnnotation: entityAnnotations ) {
						String ea = entityAnnotation.toString(); 
						if (StringUtils.equals(ta, ea)) {
							found = true;
							break;
						}
						if (StringUtils.contains(ea,ta)) {
							warning("El método " + entityMethod.getName() + " de la clase " + entityMethod.getDeclaringClass().getName() + 
									" tiene la anotación [" + ea + "] diferente de [" + ta +"]");
							found = true;
							break;
						}
						
					}
					if (!found) {
						warning("El método " + entityMethod.getName() + " de la clase " + entityMethod.getDeclaringClass().getName() + " no tiene la anotación " + trunkAnnotation);
					} else {
						System.out.println("\t\t" + trunkAnnotation + " ok!");	
					}
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException, NotFoundException, ClassNotFoundException {
		EntityCompare mojo = new EntityCompare();
		mojo.execute();
	}

	private List<String> getToCheckClasses() {
		List<String> classes = new LinkedList<String>();
//		// ACCOUNT
//		classes.add("com.code.aon.account.Account");
//		// CONFIG
//		classes.add("com.code.aon.config.Domain");
//		classes.add("com.code.aon.config.ApplicationParameter");
//		classes.add("com.code.aon.config.Bank");
//		classes.add("com.code.aon.config.CNAE");
//		classes.add("com.code.aon.config.CNAE2009");
//		classes.add("com.code.aon.config.CNAE2009Rate");
//		classes.add("com.code.aon.config.CommissionType");
//		classes.add("com.code.aon.config.PayMethodTypeDetail");
//		classes.add("com.code.aon.config.PayMethod");
//		classes.add("com.code.aon.config.Series");
//		classes.add("com.code.aon.config.Scope");
//		classes.add("com.code.aon.config.Tariff");
//		classes.add("com.code.aon.config.Tax");
//		classes.add("com.code.aon.config.TaxDetail");
//		classes.add("com.code.aon.config.User");
//		classes.add("com.code.aon.config.UserScope");
//		classes.add("com.code.aon.config.UserWorkGroup");
//		classes.add("com.code.aon.config.WorkGroup");
//		// AUDIT
//		classes.add("com.code.aon.audit.Action");
//		classes.add("com.code.aon.audit.ActionDenied");
//		classes.add("com.code.aon.audit.ActionEntry");
//		classes.add("com.code.aon.audit.ActionFavorite");
//		classes.add("com.code.aon.audit.Application");
//		classes.add("com.code.aon.audit.Session");
//		// CALENDAR
//		classes.add("com.esferalia.aon.calendar.Calendar");
//		classes.add("com.esferalia.aon.calendar.CalendarHoliday");
//		classes.add("com.esferalia.aon.calendar.CalendarPeriod");
//		classes.add("com.esferalia.aon.calendar.Holiday");
//		classes.add("com.esferalia.aon.calendar.HolidayDetail");
//		// GEOZONE
//		classes.add("com.code.aon.geozone.GeoZone");
//		classes.add("com.code.aon.geozone.GeoTree");
		// REGISTRY
//		classes.add("com.code.aon.registry.Category");
//		classes.add("com.code.aon.person.Person");
//		classes.add("com.code.aon.registry.RecordData");
//		classes.add("com.code.aon.registry.Registry");
//		classes.add("com.code.aon.registry.RegistryAddInfo");
//		classes.add("com.code.aon.registry.RegistryAddress");
//		classes.add("com.code.aon.registry.RegistryAttachment");
//		classes.add("com.code.aon.registry.RegistryDirStaff");
//		classes.add("com.code.aon.registry.RegistryBank");
//		classes.add("com.code.aon.registry.RegistryMedia");
//		classes.add("com.code.aon.registry.RegistryNote");
//		classes.add("com.code.aon.registry.RegistryPayMethod");
//		classes.add("com.code.aon.registry.RegistryRelationship");
//		classes.add("com.code.aon.registry.RegistrySegment");
//		classes.add("com.code.aon.registry.Relationship");
//		classes.add("com.code.aon.registry.Segment");
		// COMPANY
//		classes.add("com.code.aon.company.Company");
//		classes.add("com.code.aon.company.Enterprise");
//		classes.add("com.code.aon.company.EnterpriseData");
//		classes.add("com.code.aon.company.WorkPlace");
//		classes.add("com.code.aon.company.EnterpriseUser");
//		// CUSTOMER
//		classes.add("com.code.aon.customer.Customer");
//		// PROJECT
//		classes.add("com.code.aon.project.ActivityType");
//		classes.add("com.code.aon.project.Project");
//		classes.add("com.code.aon.project.ProjectActivity");
//		classes.add("com.code.aon.project.ProjectType");
//		// GROUPWARE
//		classes.add("com.code.aon.groupware.Alarm");
//		classes.add("com.code.aon.groupware.Campaign");
//		classes.add("com.code.aon.groupware.CampaignProject");
//		classes.add("com.code.aon.groupware.CampaignType");
//		classes.add("com.code.aon.groupware.CostProfile");
//		classes.add("com.code.aon.groupware.DailyTracking");
//		classes.add("com.code.aon.groupware.Favorite");
//		classes.add("com.code.aon.groupware.FavoriteCategory");
//		classes.add("com.code.aon.groupware.JobType");
//		classes.add("com.code.aon.groupware.Note");
//		classes.add("com.code.aon.groupware.Notice");
//		classes.add("com.code.aon.groupware.Process");
//		classes.add("com.code.aon.groupware.ProcessDetail");
//		classes.add("com.code.aon.groupware.ProcessDetailTransition");
//		classes.add("com.code.aon.groupware.ProcessTask");
//		classes.add("com.code.aon.groupware.ProcessTransitionType");
//		classes.add("com.code.aon.groupware.Task");
//		classes.add("com.code.aon.groupware.TaskHolder");
//		classes.add("com.code.aon.groupware.TaskHolderWorkgroup");
//		//MESSAGING
//		classes.add("com.code.aon.messaging.Message");
//		classes.add("com.code.aon.messaging.MessageContent");
//		//PAYROLL
//		classes.add("com.esferalia.aon.payroll.Agreement");
//		classes.add("com.esferalia.aon.payroll.AgreementData");
//		classes.add("com.esferalia.aon.payroll.AgreementExtra");
//		classes.add("com.esferalia.aon.payroll.AgreementLevel");
//		classes.add("com.esferalia.aon.payroll.AgreementLevelCategory");
//		classes.add("com.esferalia.aon.payroll.AgreementLevelData");
//		classes.add("com.esferalia.aon.payroll.AgreementPayment");
//		classes.add("com.esferalia.aon.payroll.BonusConcept");
//		classes.add("com.esferalia.aon.payroll.Certifica2Batch");
//		classes.add("com.esferalia.aon.payroll.Certifica2BatchAttachment");
//		classes.add("com.esferalia.aon.payroll.Certifica2BatchData");
//		classes.add("com.esferalia.aon.payroll.Certifica2BatchDetail");
//		classes.add("com.esferalia.aon.payroll.CNO");
//		classes.add("com.esferalia.aon.payroll.Contract");
//		classes.add("com.esferalia.aon.payroll.ContractAttachment");
//		classes.add("com.esferalia.aon.payroll.ContractBatch");
//		classes.add("com.esferalia.aon.payroll.ContractBatchAttachment");
//		classes.add("com.esferalia.aon.payroll.ContractBatchDetail");
//		classes.add("com.esferalia.aon.payroll.ContractBonus");
//		classes.add("com.esferalia.aon.payroll.ContractCalendarEvent");
//		classes.add("com.esferalia.aon.payroll.ContractData");
//		classes.add("com.esferalia.aon.payroll.ContractDeduction");
//		classes.add("com.esferalia.aon.payroll.ContractEmbargo");
//		classes.add("com.esferalia.aon.payroll.ContractLeave");
//		classes.add("com.esferalia.aon.payroll.ContractLeaveDetail");
//		classes.add("com.esferalia.aon.payroll.ContractPayment");
//		classes.add("com.esferalia.aon.payroll.DeductionConcept");
//		classes.add("com.esferalia.aon.payroll.EnterpriseActivity");
//		classes.add("com.esferalia.aon.payroll.EnterpriseCCC");
//		classes.add("com.esferalia.aon.payroll.FanBatch");
//		classes.add("com.esferalia.aon.payroll.FanBatchAttachment");
//		classes.add("com.esferalia.aon.payroll.FanBatchDetail");
//		classes.add("com.esferalia.aon.payroll.GeozoneIrpf");
//		classes.add("com.esferalia.aon.payroll.GeozoneIrpfDescendant");
//		classes.add("com.esferalia.aon.payroll.GeozoneIrpfHandicap");
//		classes.add("com.esferalia.aon.payroll.IrpfData");
//		classes.add("com.esferalia.aon.payroll.IrpfDataAscendants");
//		classes.add("com.esferalia.aon.payroll.IrpfDataDescendients");
//		classes.add("com.esferalia.aon.payroll.IrpfRegularization");
//		classes.add("com.esferalia.aon.payroll.IrpfResult");
//		classes.add("com.esferalia.aon.payroll.LeaveBatch");
//		classes.add("com.esferalia.aon.payroll.LeaveBatchAttachment");
//		classes.add("com.esferalia.aon.payroll.LeaveBatchDetail");
//		classes.add("com.esferalia.aon.payroll.Mod145");
//		classes.add("com.esferalia.aon.payroll.Mod145Ascendants");
//		classes.add("com.esferalia.aon.payroll.Mod145Descendients");
//		classes.add("com.esferalia.aon.payroll.PaymentConcept");
//		classes.add("com.esferalia.aon.payroll.PayrollWorkPlace");
//		classes.add("com.esferalia.aon.payroll.Salary");
//		classes.add("com.esferalia.aon.payroll.SalaryBonus");
//		classes.add("com.esferalia.aon.payroll.SalaryData");
//		classes.add("com.esferalia.aon.payroll.SalaryCost");
//		classes.add("com.esferalia.aon.payroll.SalaryDeduction");
//		classes.add("com.esferalia.aon.payroll.SalaryEmbargo");
//		classes.add("com.esferalia.aon.payroll.SalaryPayment");
//		classes.add("com.esferalia.aon.payroll.SystemCost");
//		classes.add("com.esferalia.aon.payroll.SystemData");
//		classes.add("com.esferalia.aon.payroll.SystemDeduction");
//		classes.add("com.esferalia.aon.payroll.SystemPayment");
//		// SUPPLIER
//		classes.add("com.code.aon.supplier.Supplier");
//		// PRODUCT
//		classes.add("com.code.aon.product.Brand");
//		classes.add("com.code.aon.product.Catalogue");
//		classes.add("com.code.aon.product.CatalogueCategory");
//		classes.add("com.code.aon.product.CatalogueItem");
//		classes.add("com.code.aon.product.Item");
//		classes.add("com.code.aon.product.ItemAlternative");
//		classes.add("com.code.aon.product.ItemAttachment");
//		classes.add("com.code.aon.product.ItemComposition");
//		classes.add("com.code.aon.product.ItemSupplier");
//		classes.add("com.code.aon.product.ItemTariff");
//		classes.add("com.code.aon.product.Product");
//		classes.add("com.code.aon.product.ProductCategory");
//		classes.add("com.code.aon.product.ProductCategoryGroup");
//		classes.add("com.code.aon.product.ProductCategoryTree");
//		classes.add("com.code.aon.product.TariffCatalogue");
//		//SELLER
//		classes.add("com.code.aon.seller.Seller");
//		//COMMERCIAL
//		classes.add("com.code.aon.commercial.CommercialActivity");
//		classes.add("com.code.aon.commercial.CommercialTracking");
//		classes.add("com.code.aon.commercial.CommercialTerm");
//		classes.add("com.code.aon.commercial.Commission");
//		classes.add("com.code.aon.commercial.CommissionCategory");
//		classes.add("com.code.aon.commercial.CommissionItem");
//		classes.add("com.code.aon.commercial.CommissionTypeCommission");
//		classes.add("com.code.aon.commercial.Offer");
//		classes.add("com.code.aon.commercial.OfferAttachment");
//		classes.add("com.code.aon.commercial.OfferDetail");
//		classes.add("com.code.aon.commercial.OfferDetailCommission");
//		classes.add("com.code.aon.commercial.OfferTerm");
//		classes.add("com.code.aon.commercial.ProjectCommercial");
//		classes.add("com.code.aon.commercial.Target");
//		classes.add("com.code.aon.commercial.TargetItem");
//		classes.add("com.code.aon.commercial.TargetSeller");
//		classes.add("com.code.aon.commercial.TargetSupplier");
//		//TAS
//		classes.add("com.code.aon.tas.Make");
//		classes.add("com.code.aon.tas.Model");
//		classes.add("com.code.aon.tas.ProjectTas");
//		classes.add("com.code.aon.tas.TasItem");
//		//PURCHASE
//		classes.add("com.code.aon.purchase.Purchase");
//		classes.add("com.code.aon.purchase.PurchaseDetail");
//		//SALES
//		classes.add("com.code.aon.sales.Sales");
//		classes.add("com.code.aon.sales.SalesDetail");
//		//WAREHOUSE
//		classes.add("com.code.aon.warehouse.Delivery");
//		classes.add("com.code.aon.warehouse.DeliveryDetail");
//		classes.add("com.code.aon.warehouse.Income");
//		classes.add("com.code.aon.warehouse.IncomeDetail");
//		classes.add("com.code.aon.warehouse.Inventory");
//		classes.add("com.code.aon.warehouse.InventoryDetail");
//		classes.add("com.code.aon.warehouse.ItemWarehouse");
//		classes.add("com.code.aon.warehouse.Stock");
//		classes.add("com.code.aon.warehouse.Warehouse");
//		classes.add("com.code.aon.warehouse.WarehouseTransfer");
//		classes.add("com.code.aon.warehouse.WarehouseTransferDetail");
//		//FINANCE
//		classes.add("com.code.aon.finance.BankConcept");
//		classes.add("com.code.aon.finance.BankStatement");
//		classes.add("com.code.aon.finance.BankStatementLink");
//		classes.add("com.code.aon.finance.CashFlowForecast");
//		classes.add("com.code.aon.finance.CustomerFee");
//		classes.add("com.code.aon.finance.Creditor");
//		classes.add("com.code.aon.finance.Finance");
//		classes.add("com.code.aon.finance.FinanceBatch");
//		classes.add("com.code.aon.finance.FinanceBatchDetail");
//		classes.add("com.code.aon.finance.FinanceTracking");
//		classes.add("com.code.aon.finance.Invoice");
//		classes.add("com.code.aon.finance.InvoiceAddress");
//		classes.add("com.code.aon.finance.InvoiceAttachment");
//		classes.add("com.code.aon.finance.InvoiceDetail");
//		classes.add("com.code.aon.finance.InvoiceTax");
//		classes.add("com.code.aon.finance.InvoicingGroup");
//		classes.add("com.code.aon.finance.InvoicingGroupDetail");
//		// ACCOUNTING
//		classes.add("com.code.aon.accounting.AccountEntry");
//		classes.add("com.code.aon.accounting.AccountEntryDetail");
//		classes.add("com.code.aon.accounting.AccountHelper");
//		classes.add("com.code.aon.accounting.Amortization");
//		classes.add("com.code.aon.accounting.AmortizationDetail");
//		classes.add("com.code.aon.accounting.AmortizationType");
//		classes.add("com.code.aon.accounting.AutoConcept");
//		classes.add("com.code.aon.accounting.Balance");
//		classes.add("com.code.aon.accounting.BalanceDetail");
//		classes.add("com.code.aon.accounting.Loan");
//		classes.add("com.code.aon.accounting.Period");
//		// ACCOUNT BRIDGE
//		classes.add("com.code.aon.account.bridge.AccountEntryBankStatement");
//		classes.add("com.code.aon.account.bridge.AccountEntryFinanceBatch");
//		classes.add("com.code.aon.account.bridge.AccountEntryFinanceTracking");
//		classes.add("com.code.aon.account.bridge.AccountEntryInvoice");
//		classes.add("com.code.aon.account.bridge.BankConceptAccount");
//		classes.add("com.code.aon.account.bridge.CreditorAccount");
//		classes.add("com.code.aon.account.bridge.CustomerAccount");
//		classes.add("com.code.aon.account.bridge.InvoiceDetailAccount");
//		classes.add("com.code.aon.account.bridge.InvoiceTaxAccount");
//		classes.add("com.code.aon.account.bridge.LoanAccount");
//		classes.add("com.code.aon.account.bridge.PayMethodTypeDetailAccount");
//		classes.add("com.code.aon.account.bridge.ProductAccount");
//		classes.add("com.code.aon.account.bridge.RegistryBankAccount");
//		classes.add("com.code.aon.account.bridge.SupplierAccount");
//		classes.add("com.code.aon.account.bridge.TaxAccount");
//		//FISCAL
//		classes.add("com.code.aon.fiscal.Mod347");
//		classes.add("com.code.aon.fiscal.Mod347Detail");
//		classes.add("com.code.aon.fiscal.ProfessionalRetention");
//		classes.add("com.code.aon.fiscal.Renting");
//		classes.add("com.code.aon.fiscal.RentingDetail");
//		classes.add("com.code.aon.fiscal.VatTax");
//		classes.add("com.code.aon.fiscal.VatTaxDeclaration");
//		classes.add("com.code.aon.fiscal.VatTaxDetail");
//		//ASSET
//		classes.add("com.code.aon.asset.Asset");
//		classes.add("com.code.aon.asset.AssetActivity");
//		//INFOWEB
//		classes.add("com.code.aon.infoweb.WebInfo");
//		classes.add("com.code.aon.infoweb.WebInfoPage");
//		classes.add("com.code.aon.infoweb.WebInfoPageDetail");
//		classes.add("com.code.aon.infoweb.WebInfoPageResource");
//		classes.add("com.code.aon.infoweb.WebInfoStyle");
//		//MARKETING
//		classes.add("com.code.aon.marketing.ActionTarget");
//		classes.add("com.code.aon.marketing.MarketingCampaign");
//		classes.add("com.code.aon.marketing.MarketingAction");
//		classes.add("com.code.aon.marketing.Question");
//		classes.add("com.code.aon.marketing.QuestionValue");
//		classes.add("com.code.aon.marketing.Survey");
//		classes.add("com.code.aon.marketing.SurveyQuestion");
//		classes.add("com.code.aon.marketing.SurveyResponseDetail");
//		classes.add("com.code.aon.marketing.TargetProfile");
		classes.add("com.code.aon.marketing.SurveyResponse");
		classes.add("com.code.aon.marketing.SurveyWorkflow");
		classes.add("com.code.aon.marketing.Template");
		return classes;
	}

}
