package com.esferalia.aon.gwt.fiscal.client.config;


import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.fiscal.client.widget.AonCreditorBox;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class FiscalConfigPanel extends SimpleLayoutPanel {

	private static final String DEFAULT_YEAR = "Ejercicio en curso";
	private static final String DEFAULT_ADMON = "Administraci\u00F3n por defecto curso";
	private static final String TAX_REFUND_REGISTRY = "Inscrito en el Registro de devoluci\u00F3n mensual";
	private static final String ADMON_CREDITOR = "Acreedor vinculado a los pagos de impuestos (IVA e IRPF)";
	private static final String ADMON_VAT_CREDITOR = "Acreedor vinculado a los pagos de impuestos (IVA)";
	private static final String ADMON_RETENTION_CREDITOR = "Acreedor vinculado a los pagos de impuestos (IRPF)";
	private static final String MOD303_BY_DIFFERENCE_DISABLED = "Modelo 303 - Deshabilitar la confecci\u00F3n "
			+ "de la declaraci\u00F3n por diferencia.";
	private static final String PERM_ADDRESS_CHANGES = "Mod. 130 y 131 - Marque si realiza pagos por "
			+ "pr\u00E9stamos destinados a la adquisici\u00F3n o rehabilitaci\u00F3n de su vivienda habitual.";
	private static final String CONTACT_DATA_INFO = "Informaci\u00F3n para la presentaci\u00F3n tel\u00E1matica";
	private static final String CONCTACT_PERSON = "Persona de contacto";
	private static final String CONCTACT_PHONE = "Tel\u00E9fono de contacto";
	private static final String CONCTACT_CELLULAR = "M\u00F3vil de contacto";
	private static final String CONCTACT_MAIL  = "Correo electr\u00F3nico de contacto";
	private static final String CERTIFICATE_DATA_INFO = "Informaci\u00F3n para la presentaci\u00F3n tel\u00E1matica en la Agencia Tributaria";
	private static final String CERTIFICATE_DOCUMENT= "Certificado digital. Documento.";
	private static final String CERTIFICATE_NAME = "Certificado digital. Nombre o raz\u00F3n social.";
	private static final String AEAT_TEST = "Modo de presentaci\u00F3n de las declaraciones";
	private static final String AEAT_TEST_PRODUCTION = "REAL - Entorno de producci\u00F3n.";
	private static final String AEAT_TEST_TEST 		 = "TEST - Entorno de pruebas.";

	
	private static final int CHANGE_DISPLAY_MILLIS = 1000;
	
	private static final Logger LOGGER = Logger.getLogger(FiscalConfigPanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	protected static final FiscalConfigServiceAsync FISCAL_CONFIG_SERVICE;
	static {
		FiscalConfigServiceAsync fiscalConfigServiceRaw = GWT.create(FiscalConfigService.class);
		FISCAL_CONFIG_SERVICE = new FiscalConfigServiceAsyncDecorator(fiscalConfigServiceRaw);
	}
	private AonLayoutPanel layout;

	public FiscalConfigPanel( FiscalConfigModuleOptions options ) {
		this.layout = new AonLayoutPanel();
		this.layout.addNorth(new AonToolbar(AON.MSG.fiscalParameters()), AonToolbar.HEIGTH);
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(AON.CSS.aonScrollArea());
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonBlockCenter());
		tab.addStyleName(AON.CSS.aonMarginTop());
		
		scrollPanel.setWidget(tab);
		this.layout.add(scrollPanel);
		setWidget(this.layout);
		
		if (!options.getConfiguration().getCompany().getDomain().isParent()) {
			paintDefaultYear(options, tab);
			paintDefaultAdministration(options, tab);
			paintAdmonCreditor(options, tab);
			paintAdmonVatCreditor(options, tab);
			paintAdmonRetentionCreditor(options, tab);
			paintTaxRefundRegime(options, tab);
			paintMod303ByDifferenceDisabled(options, tab);
			paintPermAddressChanges(options, tab);
			paintContactDataInfo(options, tab);
			paintContactPerson(options, tab);
			paintContactPhone(options, tab);
			paintContactCellular(options, tab);
			paintContactMail(options, tab);
		}
		paintCertifiateDataInfo(options, tab);
		paintCertifiateDocument(options, tab);
		paintCertifiateName(options, tab);
		paintCertifiateEnvironment(options, tab);
	}

	private void addRow(AonDisplayTable tab, Label label, Widget widget, Label msg) {
		widget.addStyleName(AON.CSS.aonMarginLeft());
		FlowPanel paramPanel = new FlowPanel();
		paramPanel.setStyleName(AON.CSS.aonDisplayFlex());
		paramPanel.add(widget);
		paramPanel.add(msg);
		tab.addRow()
			.addCell( label, AON.CSS.aonBold(),AON.CSS.aonWidth400() )
			.addCell( paramPanel, AON.CSS.aonFlexGrow1() );
	}
	
	private Label getEmptyLabel() {
		final Label msg = new Label("");
		msg.setStyleName(AON.CSS.aonTabIcon());
		msg.addStyleName(AON.CSS.aonMarginLeft());
		return msg;
	}

	private Label getCheckLabel( boolean value ) {
		final Label check = new Label("");
		check.setStyleName(AON.CSS.aonTabIcon());
		check.addStyleName(AON.CSS.aonMarginLeft());
		check.addStyleName(value ? AON.CSS.aonIconToggleOn() : AON.CSS.aonIconToggleOff());
		check.setTitle(value ? AON.MSG.checked() : AON.MSG.unchecked());
		return check;
	}
	
	private void decorateCheck(Label check, boolean value) {
		if (value) {
			check.setTitle(AON.MSG.checked());
			check.addStyleName(AON.CSS.aonIconToggleOn());
			check.removeStyleName(AON.CSS.aonIconToggleOff());
		} else {
			check.setTitle(AON.MSG.unchecked());
			check.addStyleName(AON.CSS.aonIconToggleOff());
			check.removeStyleName(AON.CSS.aonIconToggleOn());
		}
	}

	private void saveParam(FiscalConfigModuleOptions options, ApplicationParameter ap, Label msg) {
		FISCAL_CONFIG_SERVICE.saveParam(options.getOccam(),ap,
			new AsyncCallback<ApplicationParameter>() {

				@Override
				public void onFailure(Throwable caught) {
					FiscalConfigPanel.this.layout.showErrorPanel(caught.getMessage());
				}

				@Override
				public void onSuccess(ApplicationParameter result) {
					msg.addStyleName(AON.CSS.aonIconValid());
					new Timer() {
						@Override
						public void run() {
							msg.removeStyleName(AON.CSS.aonIconValid());
						}
					}.schedule(CHANGE_DISPLAY_MILLIS);
				}
			});
	}

	private void paintDefaultYear(FiscalConfigModuleOptions options, AonDisplayTable tab) {
		final Label msg = getEmptyLabel();
		AonIntegerBox yearBox = new AonIntegerBox();
		yearBox.setMaxLength(4);
		yearBox.setVisibleLength(5);
		yearBox.setValue(options.getConfiguration().fiscal().getDefaultYear());
		yearBox.addValueChangeHandler( event -> {
			try {
				Integer year = yearBox.getValueOrThrow();
				options.getConfiguration().fiscal().setDefaultYear( year );
				saveParam(options, 
					new ApplicationParameter().setName(AppParam.FS_DEFAULT_YEAR)
					.setValue(AonNumberUtils.toString(year))
					,msg);
			} catch (java.text.ParseException e) {
				// Nothing Box is already marked as rrror
			}
		});
		addRow(tab, new Label(DEFAULT_YEAR),yearBox,msg);
	}
	
	private void paintDefaultAdministration(FiscalConfigModuleOptions options, AonDisplayTable tab) {
		final Label msg = getEmptyLabel();
		ListBox admonBox = new ListBox();
		admonBox.addItem(" ------ ");
		for (Administration adm : Administration.values()) {
			if (adm != Administration.UNKNOWN) {
				admonBox.addItem( adm.getDescription());
			}
			if (adm == options.getConfiguration().fiscal().getAdministration( null )) {
				admonBox.setSelectedIndex(admonBox.getItemCount() - 1);
			}
		}
		admonBox.addChangeHandler( event -> saveParam(options, new ApplicationParameter()
			.setName(AppParam.FS_DEFAULT_ADMINISTRATION)
			.setValue(admonBox.getSelectedIndex() == 0 
				? null 
				: AonNumberUtils.toString((admonBox.getSelectedIndex() - 1)))
			,msg));
		addRow(tab, new Label(DEFAULT_ADMON),admonBox,msg);
	}
	
	private void paintTaxRefundRegime(FiscalConfigModuleOptions options, AonDisplayTable tab) {
		final Label msg = getEmptyLabel();
		final Label switchLabel = getCheckLabel( options.getConfiguration().fiscal().isTaxRefundRegistry() );
		switchLabel.addClickHandler( event -> {
			options.getConfiguration().fiscal().setTaxRefundRegistry(
					!options.getConfiguration().fiscal().isTaxRefundRegistry());
			decorateCheck(switchLabel,  options.getConfiguration().fiscal().isTaxRefundRegistry());
			saveParam(options, 
				new ApplicationParameter().setName(AppParam.FS_TAX_REFUND_REGISTRY)
					.setValue( Boolean.toString(options.getConfiguration().fiscal().isTaxRefundRegistry()))
				,msg);
		});
		addRow(tab, new Label(TAX_REFUND_REGISTRY),switchLabel,msg);
	}
	
	private void paintMod303ByDifferenceDisabled(FiscalConfigModuleOptions options, AonDisplayTable tab) {
		final Label msg = getEmptyLabel();
		final Label switchLabel = getCheckLabel( options.getConfiguration().fiscal().isMod303ByDifferenceDisabled() );
		switchLabel.addClickHandler( event -> {
			options.getConfiguration().fiscal().setMod303ByDifferenceDisabled(
					!options.getConfiguration().fiscal().isMod303ByDifferenceDisabled());
			decorateCheck(switchLabel,  options.getConfiguration().fiscal().isMod303ByDifferenceDisabled());
			saveParam(options, 
				new ApplicationParameter().setName(AppParam.FS_MOD303_BY_DIFFERENCE_DISABLED)
					.setValue( Boolean.toString(options.getConfiguration().fiscal().isMod303ByDifferenceDisabled()))
				,msg);
		});
		addRow(tab, new Label(MOD303_BY_DIFFERENCE_DISABLED),switchLabel,msg);
	}
	
	private void paintPermAddressChanges(FiscalConfigModuleOptions options, AonDisplayTable tab) {
		final Label msg = getEmptyLabel();
		final Label switchLabel = getCheckLabel( options.getConfiguration().fiscal().isPermAddressChanges() );
		switchLabel.addClickHandler( event -> {
			options.getConfiguration().fiscal().setPermAddressChanges(
					!options.getConfiguration().fiscal().isPermAddressChanges());
			decorateCheck(switchLabel,  options.getConfiguration().fiscal().isPermAddressChanges());
			saveParam(options, 
				new ApplicationParameter().setName(AppParam.FS_PERM_ADDRESS_CHANGES)
					.setValue( Boolean.toString(options.getConfiguration().fiscal().isPermAddressChanges()))
				,msg);
		});
		addRow(tab, new Label(PERM_ADDRESS_CHANGES),switchLabel,msg);
	}
	
	private void paintAdmonCreditor(FiscalConfigModuleOptions options, AonDisplayTable tab) {
		final Label msg = getEmptyLabel();
		AonCreditorBox creditor = new AonCreditorBox(options.getOccam());
		creditor.setValue(options.getConfiguration().fiscal().getAdmonCreditor());
		creditor.addSelectionHandler(event -> {
			options.getConfiguration().fiscal().setAdmonCreditor(event.getSelectedItem());
			saveParam(options, 
				new ApplicationParameter().setName(AppParam.FS_ADMON_CREDITOR)
					.setValue( AonNumberUtils.toString(event.getSelectedItem() == null ? null : event.getSelectedItem().getId()))
				,msg);
		});
		addRow(tab, new Label(ADMON_CREDITOR),creditor,msg);
	}
	
	private void paintAdmonVatCreditor(FiscalConfigModuleOptions options, AonDisplayTable tab) {
		final Label msg = getEmptyLabel();
		AonCreditorBox creditor = new AonCreditorBox(options.getOccam());
		creditor.setValue(options.getConfiguration().fiscal().getAdmonVatCreditor());
		creditor.addSelectionHandler(event -> {
			options.getConfiguration().fiscal().setAdmonVatCreditor(event.getSelectedItem());
			saveParam(options, 
				new ApplicationParameter().setName(AppParam.FS_ADMON_VAT_CREDITOR)
					.setValue( AonNumberUtils.toString(event.getSelectedItem() == null ? null : event.getSelectedItem().getId()))
				,msg);
		});
		addRow(tab, new Label(ADMON_VAT_CREDITOR),creditor,msg);
	}

	private void paintAdmonRetentionCreditor(FiscalConfigModuleOptions options, AonDisplayTable tab) {
		final Label msg = getEmptyLabel();
		AonCreditorBox creditor = new AonCreditorBox(options.getOccam());
		creditor.setValue(options.getConfiguration().fiscal().getAdmonRetentionCreditor());
		creditor.addSelectionHandler(event -> {
			options.getConfiguration().fiscal().setAdmonRetentionCreditor(event.getSelectedItem());
			saveParam(options, 
				new ApplicationParameter().setName(AppParam.FS_ADMON_RETENTION_CREDITOR)
					.setValue( AonNumberUtils.toString(event.getSelectedItem() == null ? null : event.getSelectedItem().getId()))
				,msg);
		});
		addRow(tab, new Label(ADMON_RETENTION_CREDITOR),creditor,msg);
	}

	private void paintContactDataInfo(FiscalConfigModuleOptions options, AonDisplayTable tab) {
		final Label msg = getEmptyLabel();
		final Label widget = getEmptyLabel();
		final Label title = new Label(CONTACT_DATA_INFO);
		title.setStyleName(AON.CSS.aonBold());
		title.setStyleName(AON.CSS.aonTextUnderline());
		addRow(tab, title,widget,msg);
	}

	private void paintContactPerson(FiscalConfigModuleOptions options, AonDisplayTable tab) {
		final Label msg = getEmptyLabel();
		AonTextBox box = new AonTextBox();
		box.setMaxLength(40);
		box.setVisibleLength(25);
		box.setValue(options.getConfiguration().fiscal().getContactPerson());
		box.addValueChangeHandler( event -> {
			options.getConfiguration().fiscal().setContactPerson(box.getValue());
			saveParam(options, 
				new ApplicationParameter().setName(AppParam.FS_CONCTACT_PERSON)
				.setValue(box.getValue())
				,msg);
		});
		addRow(tab, new Label(CONCTACT_PERSON),box,msg);
	}
	
	private void paintContactPhone(FiscalConfigModuleOptions options, AonDisplayTable tab) {
		final Label msg = getEmptyLabel();
		AonTextBox box = new AonTextBox();
		box.setMaxLength(9);
		box.setVisibleLength(12);
		box.setValue(options.getConfiguration().fiscal().getContactPhone());
		box.addValueChangeHandler( event -> {
			options.getConfiguration().fiscal().setContactPhone(box.getValue());
			saveParam(options, 
				new ApplicationParameter().setName(AppParam.FS_CONCTACT_PHONE)
				.setValue(box.getValue())
				,msg);
		});
		addRow(tab, new Label(CONCTACT_PHONE),box,msg);
	}

	private void paintContactCellular(FiscalConfigModuleOptions options, AonDisplayTable tab) {
		final Label msg = getEmptyLabel();
		AonTextBox box = new AonTextBox();
		box.setMaxLength(9);
		box.setVisibleLength(12);
		box.setValue(options.getConfiguration().fiscal().getContactCellular());
		box.addValueChangeHandler( event -> {
			options.getConfiguration().fiscal().setContactCellular(box.getValue());
			saveParam(options, 
				new ApplicationParameter().setName(AppParam.FS_CONCTACT_CELLULAR)
				.setValue(box.getValue())
				,msg);
		});
		addRow(tab, new Label(CONCTACT_CELLULAR),box,msg);
	}

	private void paintContactMail(FiscalConfigModuleOptions options, AonDisplayTable tab) {
		final Label msg = getEmptyLabel();
		AonTextBox box = new AonTextBox();
		box.setMaxLength(40);
		box.setVisibleLength(40);
		box.setValue(options.getConfiguration().fiscal().getContactMail());
		box.addValueChangeHandler( event -> {
			options.getConfiguration().fiscal().setContactMail(box.getValue());
			saveParam(options, 
				new ApplicationParameter().setName(AppParam.FS_CONCTACT_MAIL)
				.setValue(box.getValue())
				,msg);
		});
		addRow(tab, new Label(CONCTACT_MAIL),box,msg);
	}
	
	private void paintCertifiateDataInfo(FiscalConfigModuleOptions options, AonDisplayTable tab) {
		final Label msg = getEmptyLabel();
		final Label widget = getEmptyLabel();
		final Label title = new Label(CERTIFICATE_DATA_INFO);
		title.setStyleName(AON.CSS.aonBold());
		title.setStyleName(AON.CSS.aonTextUnderline());
		addRow(tab, title,widget,msg);
	}
	
	private void paintCertifiateDocument(FiscalConfigModuleOptions options, AonDisplayTable tab) {
		final Label msg = getEmptyLabel();
		AonDocumentTextBox box = new AonDocumentTextBox();
		box.setValue(options.getConfiguration().fiscal().getCertificateDocument());
		box.addValueChangeHandler( event -> {
			options.getConfiguration().fiscal().setCertificateDocument(box.getValue());
			saveParam(options, 
				new ApplicationParameter().setName(AppParam.FS_CERT_DOCUMENT)
				.setValue(box.getValue())
				,msg);
		});
		addRow(tab, new Label(CERTIFICATE_DOCUMENT),box,msg);
	}

	private void paintCertifiateName(FiscalConfigModuleOptions options, AonDisplayTable tab) {
		final Label msg = getEmptyLabel();
		AonTextBox box = new AonTextBox();
		box.setMaxLength(40);
		box.setVisibleLength(40);
		box.setValue(options.getConfiguration().fiscal().getCertificateName());
		box.addValueChangeHandler( event -> {
			options.getConfiguration().fiscal().setCertificateName(box.getValue());
			saveParam(options, 
				new ApplicationParameter().setName(AppParam.FS_CERT_NAME)
				.setValue(box.getValue())
				,msg);
		});
		addRow(tab, new Label(CERTIFICATE_NAME),box,msg);
	}
	
	private void paintCertifiateEnvironment(FiscalConfigModuleOptions options, AonDisplayTable tab) {
		final Label msg = getEmptyLabel();
		ListBox box = new ListBox();
		box.addItem(AEAT_TEST_PRODUCTION);
		box.addItem(AEAT_TEST_TEST);
		
		box.setSelectedIndex(options.getConfiguration().fiscal().isTestEnvironment()?1:0);
		box.addChangeHandler( event -> {
			options.getConfiguration().fiscal().setTestEnvironment(box.getSelectedIndex()==1);
			saveParam(options, 
				new ApplicationParameter()
					.setName(AppParam.FS_AEAT_TEST_ENV)
					.setValue(Boolean.toString(options.getConfiguration().fiscal().isTestEnvironment()))
				,msg);
		});
		addRow(tab, new Label(AEAT_TEST),box,msg);
	}
}
