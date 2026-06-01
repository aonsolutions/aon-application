import { MSG, CSS, EVENT, TAG } from '../../environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from '../../gwt/gwt.js';
import * as JSF from '../aon-jsf-app.js';
import { AonComunica } from '../../modules/laboral/aon-comunica.js';
import { AonPayrollBeta } from './aon-payroll-beta.js';
import { AonIconButton } from '../../components/aon-icon-button.js';



export class AonPayrollMenu extends AonSuiteMenu {

    AON_MENU;
    AON_HEADER;
    ROOT_PANEL;
    RIGHT_PANEL;

    constructor () {
        super();
		this.laboralInitialize()
    }

    connectedCallback () {
		this.buildDur().then(() => {		
	        this.clear();
	        this.initialize();
	        this.build();
	        //this.setTitle("Opciones de laboral");
		})
    }

    laboralInitialize() {
        this.AON_MENU = 'aonMenu';
        this.AON_HEADER = 'aonHeader';
        this.ROOT_PANEL = 'rootPanel';
        this.RIGHT_PANEL = 'rightPanel';
        this.last = MSG.LAST_CONTRACTS;
        this.new = MSG.NEW_CONTRACT;
        this.cardData={
            title: MSG.ACTIVITY,
            info:[MSG.PAYSHEETS_PENDING, MSG.SETTLEMENTS_PENDING, MSG.LIQUIDATIONS_PENDING]
        };
        this.selectOptions= [{
            title: MSG.IT_PART,
            action: () => alert("description")
        },{
            title: MSG.CONTRACT_PAYROLL,
            action: () => alert("description")
        }];
		
		this.initOptions();
    }
	
	initOptions(){
		this.options = [{
		    title: MSG.PAYSHEETS,
		    options: [ {
				id: "gwt-employees",
		        description: MSG.PAYROLL_INTEGRAL,
		        title: MSG.PAYROLL_INTEGRAL,
		        action: () => GWT.iLoad(GWT.EMPLOYEES)
		    },{
				id: "gwt-main-contrata",
		        description: MSG.CONTRACTS,
		        title: MSG.CONTRACTS,
		        action: () => GWT.iLoad(GWT.MAIN_CONTRATA)
		    },{
				id: "gwt-main-it",
		        description: MSG.IT_PARTS,
		        title: MSG.IT_PARTS,
		        action: () => GWT.iLoad(GWT.MAIN_IT)
		    },{
				id: "gwt-convenios",
		        description: MSG.AGREEMENTS,
		        title: MSG.AGREEMENTS,
		        action: () => GWT.iLoad(GWT.CONVENIOS)
		    },{
				id: "gwt-main-calculator",
		        description: MSG.PAYROLL_CALCULATION,
		        title: MSG.PAYROLL_CALCULATION,
		        action: () => GWT.iLoad(GWT.MAIN_CALCULATOR)
		    }],
			filter: () => this.isNotDomainManagementAvailable()
		},{
		    title: MSG.SOCIAL_SECURITY,
		    options: [{
				id:"gwt-main-creta",
		        description: MSG.CRETA_SYSTEM,
		        title: MSG.CRETA_SYSTEM,
		        action: () => GWT.iLoad(GWT.MAIN_CRETA, this.getApplication().CONTENT),
		        //action: () => GWT.iLoad(GWT.MAIN_CRETA)
		    },{
				id :"gwt-main-cra",
		        description: MSG.CRA,
		        title: MSG.CRA,
		        action: () => GWT.iLoad(GWT.MAIN_CRA, this.getApplication().CONTENT),
		    },
		    /*
		    {
		        description: "AFI - Altas, bajas y modificaciones de trabajadores",
		        title: "AFI - Altas, bajas y modificaciones de trabajadores",
				action: () => this.rootPanel(new JSF.AonJsfContractBatch)
		    },
		    */
		    {
				id: "gwt-main-afi",
		        description: MSG.AFI_AGRICULTURAL,
		        title: MSG.AFI_AGRICULTURAL,
		        action: () => GWT.iLoad(GWT.MAIN_AFI, this.getApplication().CONTENT),
		    },{
				id: "gwt-pension-plan-afi",
		        description: MSG.AFI_PENSION_REDUCTION,
		        title: MSG.AFI_PENSION_REDUCTION,
		        action: () => GWT.iLoad(GWT.PENSION_PLAN_AFI, this.getApplication().CONTENT),
		    }]
		},{
		    title: MSG.PROCESSES,
		    options: [{
				id: "gwt-main-calculator",
		        description: MSG.PAYROLL_CALCULATION,
		        title: MSG.PAYROLL_CALCULATION,
		        action: () => GWT.iLoad(GWT.MAIN_CALCULATOR, this.getApplication().CONTENT),
				filter: () => this.isDomainManagementAvailable()
			},{
				id: "gwt-main-salary-print",
		        description: MSG.PAYSHEETS_PRINT_EMAIL,
		        title: MSG.PAYSHEETS_PRINT_EMAIL,
		        action: () => GWT.iLoad(GWT.MAIN_SALARY_PRINT, this.getApplication().CONTENT),
		    },{
				id: "gwt-main-cost",
		        description: MSG.COST_LIST,
		        title: MSG.COST_LIST,
		        action: () => GWT.iLoad(GWT.MAIN_COST),
				filter: () => this.isNotDomainManagementAvailable()
		    },{
				id: "gwt-activity-summary",
		        description: MSG.ACTIVITY_SUMMARY,
		        title: MSG.ACTIVITY_SUMMARY,
		        action: () => GWT.iLoad(GWT.ACTIVITY_SUMMARY, this.getApplication().CONTENT),
		    },{
				id: "gwt-contract-media",
		        description: MSG.SALARY_STAFF_REPORT,
		        title: MSG.SALARY_STAFF_REPORT,
		        action: () => GWT.iLoad(GWT.CONTRACT_MEDIA),
				filter: () => this.isNotDomainManagementAvailable()
		    },{
				id: "gwt-massive-contracts",
		        description: MSG.BULK_CONTRACT_CHANGE,
		        title: MSG.BULK_CONTRACT_CHANGE,
		        action: () => GWT.iLoad(GWT.MASSIVE_CONTRACTS),
				filter: () => this.isNotDomainManagementAvailable()
		    },{
				id: "gwt-massive-fie",
		        description: MSG.FIE_BULK_IMPORT,
		        title: MSG.FIE_BULK_IMPORT,
		        action: () => GWT.iLoad(GWT.MASSIVE_FIE, this.getApplication().CONTENT),
				filter: () => this.isDomainManagementAvailable()
			}]
		},{
		    title: MSG.MANAGEMENT,
		    options: [{
				id: "gwt-batch-payroll",
		        description: MSG.PAYROLL_BATCH_TRANSFER,
		        title: MSG.PAYROLL_BATCH_TRANSFER,
		        action: () => GWT.iLoad(GWT.BATCH_PAYROLL)
		    },{
				id: "gwt-finance-payroll",
		        description: MSG.PAYSHEETS_EXPIRATIONS,
		        title: MSG.PAYSHEETS_EXPIRATIONS,
		        action: () => GWT.iLoad(GWT.FINANCE_PAYROLL)
		    },{
				id: "gwt-expense-invoice",
		        description: MSG.EXPENSE_INVOICES,
		        title: MSG.EXPENSE_INVOICES,
				action: () => this.rootPanel(new JSF.AonJsfExpenseInvoice)
			}, {
				description: MSG.PAYMENT_BATCH_TEMP,
				title: MSG.PAYMENT_BATCH_TEMP,
				action: () => this.rootPanel(new JSF.AonJsfFBatchPaymentPayroll())
		    }/*,{
		        description: "Vencimientos de Nóminas",
		        title: "Vencimientos de Nóminas",
		        action: () => alert("description")
		    },{
		        description: "Remesa Transferencia de Nóminas",
		        title: "Remesa Transferencia de Nóminas",
		        action: () => alert("description")
		    }*/],
			filter: () => this.isNotDomainManagementAvailable()
		},/*{
		    title: 'SEPE',
		    options: [{
		        description: "Notificaciones Contrat@",
		        title: "Notificaciones Contrat@",
		        action: () => alert("description")
		    },{
		        description: "Notificaciones Certific@",
		        title: "Notificaciones Certific@",
		        action: () => alert("description")
		    }]
		},*/{
		    title: MSG.AUXILIARIES,
		    options: [{
		        description: MSG.AGREEMENTS,
		        title: MSG.AGREEMENTS,
		        action: () => GWT.iLoad(GWT.CONVENIOS, this.getApplication().CONTENT),
			},{
		        description: MSG.CONTRACT_MODELS,
		        title: MSG.CONTRACT_MODELS,
		        action: () => this.getApplication().setContent(new JSF.AonJsfContractOption)
		    },{
		        description: MSG.TRAINING_CENTERS_ACCREDITED,
		        title: MSG.TRAINING_CENTERS_ACCREDITED,
		        action: () => this.getApplication().setContent(new JSF.AonJsfTrainningCenter)
		    },{
		        description: MSG.HOLIDAYS,
		        title: MSG.HOLIDAYS,
		        action: () => this.getApplication().setContent(new JSF.AonJsfHolidays)
		    },{
		        description: MSG.ENVIRONMENT_CALC_VARIABLES,
		        title: MSG.ENVIRONMENT_CALC_VARIABLES,
		        action: () => GWT.iLoad(GWT.DOMAIN_VARIABLES),
		        filter: () => this.getDur().isConsole()
			},{
		        description: MSG.WORKER_CALC_VARIABLES,
		        title: MSG.WORKER_CALC_VARIABLES,
		        action: () => GWT.iLoad(GWT.CONTRACT_VARIABLES, this.getApplication().CONTENT)
		    }],
			filter: () => this.isDomainManagementAvailable()
		},{
		    title: MSG.UTILITIES,
		    options: [{
		        description: MSG.CERTIFICATE_MANAGEMENT,
		        title: MSG.CERTIFICATE_MANAGEMENT,
		        action: () => GWT.iLoad(GWT.MAIN_DIGITAL_CERTIFICATES)
		    },
		    /*{
		        description: "Papelera",
		        title: "Papelera",
		        action: () => GWT.iLoad(GWT.PAYROLL_TRASH)
		    },*/
		    {
		        description: MSG.CCC,
		        title: MSG.CCC,
		        action: () => GWT.iLoad(GWT.MAIN_CCC)
		    },{
		        description: MSG.COMUNICA,
		        title: MSG.COMUNICA,
		        action: () => this.rootPanel(new AonComunica()),
				filter: () => this.isNotDomainManagementAvailable()
		    },
		    {
		        description: MSG.HOLIDAYS,
		        title: MSG.HOLIDAYS,
		        action: () => this.rootPanel(new JSF.AonJsfHolidays),
		        filter: () => this.isNotDomainManagementAvailable()
		    }],
		    filter: () => !this.isDomainManagementAvailable()
		},{
		    title: MSG.TAX_MODELS,
		    options: [{
		        description: MSG.MODEL_145,
		        title: MSG.MODEL_145,
				action: () => this.rootPanel(new JSF.AonJsfIrpfData)
		    },{
		        description: MSG.MODEL_111,
		        title: MSG.MODEL_111,
		        action: () => GWT.iLoad(GWT.MODEL_111)
		    },{
		        description: MSG.MODEL_190,
		        title: MSG.MODEL_190,
		        action: () => GWT.iLoad(GWT.MODEL_190)
		    }],
			filter: () => this.isNotDomainManagementAvailable()
		},/*{
		    title: 'Antiguas Opciones (Obsoletas)',
		    options: [{
		        description: "Contratos",
		        title: "Contratos",
		        action: () => alert("description")
		    },{
		        description: "Personas",
		        title: "Personas",
		        action: () => alert("description")
		    },{
		        description: "Bajas IT",
		        title: "Bajas IT",
		        action: () => alert("description")
		    },{
		        description: "CRA - Conceptos retributivos abonados",
		        title: "CRA - Conceptos retributivos abonados",
		        action: () => alert("description")
		    }]
		}*/];
	}
    /*
    build() {
        
    }
    */
}
if(!window.customElements.get(TAG.AON_PAYROLL_MENU)){
    window.customElements.define(TAG.AON_PAYROLL_MENU, AonPayrollMenu);
}
