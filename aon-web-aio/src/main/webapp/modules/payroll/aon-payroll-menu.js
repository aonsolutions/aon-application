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
        this.last = "Últimos contratos";
        this.new = "Nuevo Contrato";
        this.cardData={
            title: "Actividad",
            info:["Nóminas ptes.", "Finiquitos ptes.", "Liquidaciones ptes."]
        };
        this.selectOptions= [{
            title: "Parte IT",
            action: () => alert("description")
        },{
            title: "Contrato",
            action: () => alert("description")
        }];
		
		this.initOptions();
    }
	
	initOptions(){
		this.options = [{
		    title: 'Nóminas',
		    options: [ {
		        description: "Integral de Nominas",
		        title: "Integral de Nominas",
		        action: () => GWT.iLoad(GWT.EMPLOYEES)
		    },{
		        description: "Contratos",
		        title: "Contratos",
		        action: () => GWT.iLoad(GWT.MAIN_CONTRATA)
		    },{
		        description: "Partes IT",
		        title: "Partes IT",
		        action: () => GWT.iLoad(GWT.MAIN_IT)
		    },{
		        description: "Convenios",
		        title: "Convenios",
		        action: () => GWT.iLoad(GWT.CONVENIOS)
		    },{
		        description: "Calculo de Nóminas",
		        title: "Calculo de Nóminas",
		        action: () => GWT.iLoad(GWT.MAIN_CALCULATOR)
		    }],
			filter: () => this.isNotDomainManagementAvailable()		
		},{
		    title: 'Seguridad Social',
		    options: [{
		        description: "Cret@ - Sistema de Liquidación Directa",
		        title: "Cret@ - Sistema de Liquidación Directa",
		        action: () => GWT.iLoad(GWT.MAIN_CRETA, this.getApplication().CONTENT),
		        //action: () => GWT.iLoad(GWT.MAIN_CRETA)
		    },{
		        description: "CRA - Conceptos Retributivos Abonados",
		        title: "CRA - Conceptos Retributivos Abonados",
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
		        description: "AFI - Régimen Especial Agrario Jornadas",
		        title: "AFI - Régimen Especial Agrario Jornadas",
		        action: () => GWT.iLoad(GWT.MAIN_AFI, this.getApplication().CONTENT),
		    },{
		        description: "AFI - Reduc. contribuciones planes de pensiones",
		        title: "AFI - Reduc. contribuciones planes de pensiones",
		        action: () => GWT.iLoad(GWT.PENSION_PLAN_AFI, this.getApplication().CONTENT),
		    }]
		},{
		    title: 'Procesos',
		    options: [{
		        description: "Calculo de Nóminas",
		        title: "Calculo de Nóminas",
		        action: () => GWT.iLoad(GWT.MAIN_CALCULATOR, this.getApplication().CONTENT),
				filter: () => this.isDomainManagementAvailable()
			},{
		        description: "Impresión / eMail de Nóminas",
		        title: "Impresión / eMail de Nóminas",
		        action: () => GWT.iLoad(GWT.MAIN_SALARY_PRINT, this.getApplication().CONTENT),
		    },{
		        description: "Listado de costes",
		        title: "Listado de costes",
		        action: () => GWT.iLoad(GWT.MAIN_COST),
				filter: () => this.isNotDomainManagementAvailable()
		    },{
		        description: "Resumen de actividad",
		        title: "Resumen de actividad",
		        action: () => GWT.iLoad(GWT.ACTIVITY_SUMMARY, this.getApplication().CONTENT),
		    },{
		        description: "Informe de personal asalariado",
		        title: "Informe de personal asalariado",
		        action: () => GWT.iLoad(GWT.CONTRACT_MEDIA),
				filter: () => this.isNotDomainManagementAvailable()
		    },{
		        description: "Cambio masivo contratos",
		        title: "Cambio masivo contratos",
		        action: () => GWT.iLoad(GWT.MASSIVE_CONTRACTS),
				filter: () => this.isNotDomainManagementAvailable()
		    },{
		        description: "FIE - Importación masiva de I.T",
		        title: "FIE - Importación masiva de I.T",
		        action: () => GWT.iLoad(GWT.MASSIVE_FIE, this.getApplication().CONTENT),
				filter: () => this.isDomainManagementAvailable()
			}]
		},{
		    title: 'Gestión',
		    options: [{
		        description: "Remesa Transferencia de Nóminas",
		        title: "Remesa Transferencia de Nóminas",
		        action: () => GWT.iLoad(GWT.BATCH_PAYROLL)
		    },{
		        description: "Vencimientos de Nóminas",
		        title: "Vencimientos de Nóminas",
		        action: () => GWT.iLoad(GWT.FINANCE_PAYROLL)
		    },{
		        description: "Facturas de Gastos",
		        title: "Facturas de Gastos",
				action: () => this.rootPanel(new JSF.AonJsfExpenseInvoice)
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
		    title: 'Auxiliares',
		    options: [{
		        description: "Convenios",
		        title: "Convenios",
		        action: () => GWT.iLoad(GWT.CONVENIOS, this.getApplication().CONTENT),
			},{
		        description: "Modelos de contrato",
		        title: "Modelos de contrato",
		        action: () => this.getApplication().setContent(new JSF.AonJsfContractOption)
		    },{
		        description: "Centros acreditados de formación",
		        title: "Centros acreditados de formación",
		        action: () => this.getApplication().setContent(new JSF.AonJsfTrainningCenter)
		    },{
		        description: "Festivos",
		        title: "Festivos",
		        action: () => this.getApplication().setContent(new JSF.AonJsfHolidays)
		    },{
		        description: "Variables Calculo Entorno",
		        title: "Variables Calculo Entorno",
		        action: () => GWT.iLoad(GWT.DOMAIN_VARIABLES),
		        filter: () => this.getDur().isConsole()
			}/*,{
		        description: "Variables Calculo Trabajadores",
		        title: "Variables Calculo Trabajadores",
		        action: () => alert("description")
		    }*/],
			filter: () => this.isDomainManagementAvailable()
		},{
		    title: 'Utilidades',
		    options: [{
		        description: "Gestión de Certificados",
		        title: "Gestión de Certificados",
		        action: () => GWT.iLoad(GWT.MAIN_DIGITAL_CERTIFICATES)
		    },
		    /*{
		        description: "Papelera",
		        title: "Papelera",
		        action: () => GWT.iLoad(GWT.PAYROLL_TRASH)
		    },*/
		    {
		        description: "CCC",
		        title: "CCC",
		        action: () => GWT.iLoad(GWT.MAIN_CCC)
		    },{
		        description: "Comunic@",
		        title: "Comunic@",
		        action: () => this.rootPanel(new AonComunica()),
				filter: () => this.isNotDomainManagementAvailable()
		    },
		    {
		        description: "Festivos",
		        title: "Festivos",
		        action: () => this.rootPanel(new JSF.AonJsfHolidays),
		        filter: () => this.isNotDomainManagementAvailable()
		    }],
		    filter: () => !this.isDomainManagementAvailable()
		},{
		    title: 'Modelos Tributarios',
		    options: [{
		        description: "Modelo 145",
		        title: "Modelo 145",
				action: () => this.rootPanel(new JSF.AonJsfIrpfData)
		    },{
		        description: "Modelo 111",
		        title: "Modelo 111",
		        action: () => GWT.iLoad(GWT.MODEL_111)
		    },{
		        description: "Modelo 190",
		        title: "Modelo 190",
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
