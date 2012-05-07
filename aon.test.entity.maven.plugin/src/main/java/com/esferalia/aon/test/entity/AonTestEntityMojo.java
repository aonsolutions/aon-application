/**
 * 
 */
package com.esferalia.aon.test.entity;

/********************************************************************
 * Copyright (c) 2011, esferalia NETWORKS S.A
 *
 * The copyright of the computer program herein is the property 
 * of esferalia NETWORKS.
 *********************************************************************
 * The program may be used and/or copied only with the written 
 * permission of esferalia NETWORKS, or in accordance with the 
 * terms and conditions stipulated in the agreement contract 
 * under which the program has been supplied.
 *********************************************************************
 */



import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import com.code.aon.account.Account;
import com.code.aon.accounting.AmortizationType;
import com.code.aon.accounting.Loan;
import com.code.aon.accounting.Period;
import com.code.aon.commercial.CommercialTerm;
import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.OfferTerm;
import com.code.aon.config.BankAccount;
import com.code.aon.config.Domain;
import com.code.aon.config.User;
import com.code.aon.finance.CustomerFee;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.fiscal.Mod347;
import com.code.aon.fiscal.Renting;
import com.code.aon.fiscal.VatTax;
import com.code.aon.groupware.Task;
import com.code.aon.infoweb.WebInfoPage;
import com.code.aon.messaging.Message;
import com.code.aon.product.ItemAlternative;
import com.code.aon.product.ItemComposition;
import com.code.aon.product.ItemSupplier;
import com.code.aon.project.Project;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.registry.IRegistry;
import com.code.aon.sales.SalesDetail;
import com.code.aon.tas.TasItem;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.WarehouseTransfer;
import com.esferalia.aon.calendar.CalendarPeriod;
import com.esferalia.aon.entity.hibernate.tools.AonExporter;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.payroll.GeozoneIrpfDescendant;
import com.esferalia.aon.payroll.GeozoneIrpfHandicap;
import com.esferalia.aon.payroll.IrpfData;
import com.esferalia.aon.payroll.IrpfResult;
import com.esferalia.aon.payroll.SystemCost;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationService;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;


/**
 * @author ecastellano
 * 
 * @goal generate-test-entities
 * @phase generate-test-sources
 * @requiresDependencyResolution compile+runtime
 * @requiresProject
 * 
 */
public class AonTestEntityMojo extends AbstractMojo {
	
	/**
	 * The Maven Project Object
	 * 
	 * @parameter expression="${project}"
	 * @readonly
	 */
	protected MavenProject project;

	/**
	 * The maven project's helper.
	 * 
	 * @component role="org.apache.maven.project.MavenProjectHelper"
	 * @readonly
	 */
	private MavenProjectHelper projectHelper;

	/**
	 * @parameter default-value="${project.build.directory}/generated-sources"
	 */
	private String outputDir;
	
	/**
	 * @parameter default-value="true"
	 */
	private boolean generateHibernateCfg;

	/**
	 * @parameter default-value="${project.build.directory}/classes"
	 */
	private String resourcesOutputDir;

	public MavenProject getProject() {
		return project;
	}

	public void setProject(MavenProject project) {
		this.project = project;
	}

	public MavenProjectHelper getProjectHelper() {
		return projectHelper;
	}

	public void setProjectHelper(MavenProjectHelper projectHelper) {
		this.projectHelper = projectHelper;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}
	
	public String getResourcesOutputDir() {
		return resourcesOutputDir;
	}

	public void setResourcesOutputDir(String resourcesOutputDir) {
		this.resourcesOutputDir = resourcesOutputDir;
	}

	public boolean isGenerateHibernateCfg() {
		return generateHibernateCfg;
	}

	public void setGenerateHibernateCfg(boolean generateHibernateCfg) {
		this.generateHibernateCfg = generateHibernateCfg;
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			Collection<String> classes = AonExporter.map.values();
			for (String clazz:classes) {
				Map<String, Object> additionalContext = new HashMap<String, Object>();
				String aonPackage = ClassUtils.getPackageName(clazz);
				String aonEntity = ClassUtils.getShortClassName(clazz);
				additionalContext.put("aonPackage", aonPackage);
				additionalContext.put("aonEntity", aonEntity);
				additionalContext.put("date", new Date().toString());
				String aonClass = aonPackage + "." + aonEntity;
				Collection<String> methods = getSetters(aonClass);
				additionalContext.put("setters", methods );
				Configuration cfg = new Configuration();
				cfg.setTemplateLoader(new ClassTemplateLoader(this.getClass(),"/"));
				Template tpl = cfg.getTemplate("aon/TestEntity.ftl");
				File packageDir = new File(getOutputDir(), "com/esferalia/aon/test");
				packageDir.mkdirs();
				File file  = new File(packageDir, "TestEntity" + aonEntity + ".java");
				FileWriter output = new FileWriter(file);
				tpl.process(additionalContext, output);			
			}
			if (isGenerateHibernateCfg()) {
				Map<String, Object> additionalContext = new HashMap<String, Object>();
				additionalContext.put("pojos", classes);
				Configuration cfg = new Configuration();
				cfg.setTemplateLoader(new ClassTemplateLoader(this.getClass(),"/"));
				Template tpl = cfg.getTemplate("aon/hibernate.cfg.xml.ftl");
				File dir = new File(getResourcesOutputDir());
				dir.mkdirs();
				File file  = new File(dir, "hibernate.cfg.xml");
				FileWriter output = new FileWriter(file);
				tpl.process(additionalContext, output);			
			}
		}
		catch(Exception e) {
			throw new MojoExecutionException(e.getMessage(),e);
		}		
	}
	
	private Collection<String> getSetters(String aonClass) throws ClassNotFoundException {
		Class<?> clazz = Class.forName(aonClass);
		Method[] methods = clazz.getMethods();
		List<String> list = new LinkedList<String>();
		for (Method method : methods) {
			Class<?>[] parameters = method.getParameterTypes();
			if (isSuitableSetter(clazz,method)) {
				String prefix = "get";
				if (Boolean.class.equals( parameters[0]) || boolean.class.equals( parameters[0])) {
					prefix = "is";
				}
				String getterName = prefix + StringUtils.removeStart(method.getName(), "set");
				int length = 1;
				Method getter = null;
				try {
					getter = clazz.getMethod(getterName);
					Column column = getter.getAnnotation(Column.class);
					length = column != null? column.length() : length;
				} catch (SecurityException e) {
					System.out.println( getterName + " no accesible en " + clazz.getName() );
				} catch (NoSuchMethodException e) {
					System.out.println( getterName + "(" + parameters[0].getName() + ") no existe en " + clazz.getName() );
				}
				if (getter != null) {
					Transient transientAnn = getter.getAnnotation(Transient.class);
					OneToMany oneToManyAnn = getter.getAnnotation(OneToMany.class);
					if (transientAnn == null && oneToManyAnn == null) {
						String specialAttribute = hasSpecialAttribute(clazz,method);
						Lob lobAnn = getter.getAnnotation(Lob.class);
						StringBuffer buf = new StringBuffer();
						if (StringUtils.isNotEmpty(specialAttribute)) {
							buf.append("to.");
							buf.append(method.getName());
							buf.append("(");
							buf.append( specialAttribute );
							buf.append(");");
						} else if (BankAccount.class.equals(parameters[0])) {
							buf.append("com.code.aon.config.BankAccount bankAccount = new com.code.aon.config.BankAccount();\n");
							buf.append("\t\t\tbankAccount.setOffice(\"0182\");\n");
							buf.append("\t\t\tbankAccount.setEntity(\"1111\");\n");
							buf.append("\t\t\tbankAccount.setControl(\"70\");\n");
							buf.append("\t\t\tbankAccount.setAccount(\"1111111111\");\n");
							buf.append("\t\t\tto.");
							buf.append(method.getName());
							buf.append("(bankAccount);");
						} else if (parameters[0].isArray() && byte[].class.equals( parameters[0] )) {
							buf.append("try {\n");
							buf.append("\t\t\t\tjava.io.InputStream in = TestEntity" + clazz.getSimpleName() + ".class.getResourceAsStream(\"/lob/mockBlob.gif\");\n");								
							buf.append("\t\t\t\tjava.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();\n");
							buf.append("\t\t\t\torg.apache.commons.io.IOUtils.copy(in, out);\n");
							buf.append("\t\t\t\tto.");
							buf.append(method.getName());
							buf.append("(out.toByteArray());\n");
							buf.append("\t\t\t} catch (java.io.IOException e) {\n");
							buf.append("\t\t\t\tthrow new IllegalStateException(\"No se pudo añadir el Blob!\");\n");
							buf.append("\t\t\t}");
						} else if (lobAnn != null && String.class.equals(parameters[0]) ) {
							buf.append("try {\n");
							buf.append("\t\t\t\tjava.io.InputStream in = TestEntity" + clazz.getSimpleName() + ".class.getResourceAsStream(\"/lob/clob.txt\");\n");								
							buf.append("\t\t\t\tjava.io.StringWriter out = new java.io.StringWriter();\n");
							buf.append("\t\t\t\torg.apache.commons.io.IOUtils.copy(in, out);\n");
							buf.append("\t\t\t\tto.");
							buf.append(method.getName());
							buf.append("(out.toString());\n");
							buf.append("\t\t\t} catch (java.io.IOException e) {\n");
							buf.append("\t\t\t\tthrow new IllegalStateException(\"No se pudo añadir el Clob!\");\n");
							buf.append("\t\t\t}");
						} else if (IRegistry.class.isAssignableFrom(clazz) && "setRegistry".equals(method.getName())) {
							buf.append("TestEntityRegistry testEntity = new TestEntityRegistry();");
							buf.append("com.code.aon.registry.Registry registry = new com.code.aon.registry.Registry();");
							buf.append("testEntity.fillData(registry);");
							buf.append("to.setRegistry(registry);");
						} else if (clazz.equals(parameters[0]) ) {
							// TODO 
							// Soporte para las entidades que se contienen a si mismas.
						} else {
							buf.append("to.");
							buf.append(method.getName());
							buf.append("(");
							buf.append( TestRandomData.getValueFor(parameters[0], length) );
							buf.append(");");
						}
						list.add(buf.toString());
					}
				}
			}
		}
		return list;
	}

	private String hasSpecialAttribute(Class<?> clazz, Method method) {
		if (Domain.class.equals(clazz) && "setId".equals(method.getName())) return "1";
		if (VatTax.class.equals(clazz) && "setComplementary".equals(method.getName())) return "false"; 
		if (VatTax.class.equals(clazz) && "setReplacement".equals(method.getName())) return "false"; 
		if (Mod347.class.equals(clazz) && "setComplementary".equals(method.getName())) return "false"; 
		if (Mod347.class.equals(clazz) && "setReplacement".equals(method.getName())) return "false"; 
		if (Renting.class.equals(clazz) && "setComplementary".equals(method.getName())) return "false"; 
		if (Renting.class.equals(clazz) && "setReplacement".equals(method.getName())) return "false"; 
		if (User.class.equals(clazz) && "setEnterprise".equals(method.getName())) return "null";
		if (User.class.equals(clazz) && "setRegistry".equals(method.getName())) return "null";
		if (Account.class.equals(clazz) && "setCode".equals(method.getName())) return "\"1\"";
		if (AmortizationType.class.equals(clazz) && "setPercentage".equals(method.getName())) return "20.0";
		if (ItemComposition.class.equals(clazz) && "setSequence".equals(method.getName())) return "0";
		if (SystemCost.class.equals(clazz) && "setType".equals(method.getName())) return "0";
		if (GeozoneIrpfDescendant.class.equals(clazz) && "setDescendant".equals(method.getName())) return "1";
		if (Offer.class.equals(clazz) && "setVersion".equals(method.getName())) return "1";
		if (OfferDetail.class.equals(clazz) && "setLine".equals(method.getName())) return "1";
		if (Task.class.equals(clazz) && "setProcessTask".equals(method.getName())) return "null"; 
		if (Task.class.equals(clazz) && "setPercent".equals(method.getName())) return "0";
		if (Period.class.equals(clazz) && "setStatus".equals(method.getName())) return "com.code.aon.accounting.enumeration.AccountPeriodStatus.ACTIVE";
		if ("setPaymentDays".equals(method.getName())) return "\"10 5\"";
		if ("setDaysBetweenPayments".equals(method.getName())) return "15";
		if ("setDaysToFirstPayment".equals(method.getName())) return "0";
		if ("setNumberOfPayments".equals(method.getName())) return "1";
		if (Invoice.class.equals(clazz) && "setType".equals(method.getName())) return "com.code.aon.finance.enumeration.InvoiceType.SALES";
		if (Invoice.class.equals(clazz) && "setRegistry".equals(method.getName())) return "new TestEntityCustomer().getEntity().getRegistry()";
		if (InvoiceDetail.class.equals(clazz) && "setLine".equals(method.getName())) return "1";
		if (TasItem.class.equals(clazz) && "setAddInfo".equals(method.getName())) return "\"J19qwQIeJzsVv2xdd6pjVHrZi J19qwQIeJzsVv2xdd6pjVHrZi\"";
		if (TasItem.class.equals(clazz) && "setDescription".equals(method.getName())) return "\"J19qwQIeJzsVv2xdd6pjVHrZi J19qwQIeJzsVv2xdd6pjVHrZi\"";
		if (WarehouseTransfer.class.equals(clazz) && "setSourceWarehouse".equals(method.getName())) return "null";
		if (ContractLeaveDetail.class.equals(clazz) && "setConfirmOrder".equals(method.getName())) return "1";
		if (IrpfResult.class.equals(clazz) && "setAscendents33_65Entirely".equals(method.getName())) return "0";		
		if (IrpfResult.class.equals(clazz) && "setAscendents33_65Total".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setAscendents65Entirely".equals(method.getName())) return "0";		
		if (IrpfResult.class.equals(clazz) && "setAscendents65Total".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setAscendentsMayor75Total".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setAscendentsMayor75Entirely".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setAscendentsMinor75Total".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setAscendentsMinor75Entirely".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setAscendentsMovingTotal".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setAscendentsMovingEntirely".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setAscendents65Total".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setAscendents65Entirely".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setDescendentsMinor3Total".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setDescendentsMinor3Entirely".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setDescendentsRemainderTotal".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setDescendentsRemainderEntirely".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setDescendents33_65Total".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setDescendents33_65Entirely".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setDescendentsMovingTotal".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setDescendentsMovingEntirely".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setDescendents65Total".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setDescendents65Entirely".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setDescendentsFirst".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setDescendentsSecond".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setDescendentsThird".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setDescendentsFourthSubsequentTotal".equals(method.getName())) return "0";
		if (IrpfResult.class.equals(clazz) && "setDescendentsFourthSubsequentEntirely".equals(method.getName())) return "0";
		if (WebInfoPage.class.equals(clazz) && "setPosition".equals(method.getName())) return "0";
		if (ContractData.class.equals(clazz) && "setName".equals(method.getName())) return "\"NOMBRE_VARIABLE\"";		
		if (Loan.class.equals(clazz) && "setTerm".equals(method.getName())) return "\"10\"";
		if (CustomerFee.class.equals(clazz) && "setLine".equals(method.getName())) return "1";
		if (IrpfData.class.equals(clazz) && "setDescendientCount".equals(method.getName())) return "1";
		if (SalesDetail.class.equals(clazz) && "setLine".equals(method.getName())) return "1";
		if (DeliveryDetail.class.equals(clazz) && "setLine".equals(method.getName())) return "1";
		if (GeozoneIrpfHandicap.class.equals(clazz) && "setHandicap".equals(method.getName())) return "0";
		if (OfferTerm.class.equals(clazz) && "setLine".equals(method.getName())) return "1";
		if (Message.class.equals(clazz) && "setMessageParts".equals(method.getName())) return "1";
		if (PurchaseDetail.class.equals(clazz) && "setLine".equals(method.getName())) return "1";
		if (IncomeDetail.class.equals(clazz) && "setLine".equals(method.getName())) return "1";
		if (ItemAlternative.class.equals(clazz) && "setPriority".equals(method.getName())) return "1";
		if (CalendarPeriod.class.equals(clazz) && "setEndDay".equals(method.getName())) return "1";
		if (CalendarPeriod.class.equals(clazz) && "setStartDay".equals(method.getName())) return "1";
		if (CommercialTerm.class.equals(clazz) && "setLine".equals(method.getName())) return "1";
		if (ItemSupplier.class.equals(clazz) && "setPriority".equals(method.getName())) return "1";
		if (Project.class.equals(clazz) && "setTas".equals(method.getName())) return "false";
		if (Project.class.equals(clazz) && "setCommercial".equals(method.getName())) return "false";
		if (Project.class.equals(clazz) && "setDossier".equals(method.getName())) return "false";
		if (Project.class.equals(clazz) && "setReservation".equals(method.getName())) return "false";
		if (ProjectReservationGuest.class.equals(clazz) && "setGuestIndex".equals(method.getName())) return "1";
		if (ProjectReservationRoom.class.equals(clazz) && "setRoomIndex".equals(method.getName())) return "1";
		if (ProjectReservationService.class.equals(clazz) && "setServiceIndex".equals(method.getName())) return "1";
		return null;
	}

	private boolean isSuitableSetter(Class<?> clazz,Method method) {
		if (!method.getName().startsWith("set")) return false;
		if ("setId".equals(method.getName()) && (!Domain.class.equals(clazz))) return false;
		if ("setDomain".equals(method.getName())) return false;
		Class<?>[] parameters = method.getParameterTypes();
		if (parameters == null) return false;
		if (parameters.length != 1) return false;
		if ("IDomainPojo".equals(parameters[0].getSimpleName())) return false;
		return true;
	}

	public static void main(String[] args) {
		com.code.aon.config.BankAccount bankAccount = new com.code.aon.config.BankAccount();
		bankAccount.setOffice("0182");
		bankAccount.setEntity("1111");
		bankAccount.setControl("60");
		bankAccount.setAccount("1111111111");
		System.out.println( bankAccount.isValid());
		
	}
	
}
