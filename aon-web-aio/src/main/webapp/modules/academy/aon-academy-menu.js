import { MSG, CSS, EVENT, TAG } from '../../environments/environments.js'; 
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from '../../gwt/gwt.js';

export class AonAcademyMenu extends AonSuiteMenu {

	AON_MENU;
	AON_HEADER;
	ROOT_PANEL;
	RIGHT_PANEL;

	constructor () {
		super();
		this.accountingInitialize()
	}

	connectedCallback () {
		this.clear();
		this.initialize();
		this.build();
		this.setTitle("Opciones de academia");
	}

	accountingInitialize() {
		this.AON_MENU = 'aonMenu';
		this.AON_HEADER = 'aonHeader';
		this.ROOT_PANEL = 'rootPanel';
		this.RIGHT_PANEL = 'rightPanel';
		this.last = MSG.LAST_STUDENTS;
		this.new = MSG.NEW_ENTRY;
		this.uploadButton = true
		this.selectOptions= [{
            title: "Alumno",
            action: () => alert("description")
        },{
            title: "Ficha de amortización",
            action: () => alert("description")
        },{
            title: MSG.ENTRY,
            action: () => alert("description")
        }];
		this.options = [{
			title: MSG.ACADEMIC_MANAGEMENT,
			options: [ {
				description:"Alumnos",
				title:"Alumnos",
				action: () => alert("Alumnos")
			},{
				description: "Grupos",
				title: "Grupos",
				action: () => alert("Grupos")
			},{
				description: "Profesores",
				title: "Profesores",
				action: () => alert("Profesores")
			},{
				description: "Consulta Notas",
				title: "Consulta Notas",
				action: () => alert("Consulta Notas")
			},{
				description: "Prestamos",
				title: "Prestamos",
				action: () => alert("Prestamos")
			}]
		},{
			title: MSG.UTILITIES,
			options: [{
				description: "Reclasificar Clientes/Alumnos",
				title: "Reclasificar Clientes/Alumnos",
				action: () => alert("Reclasificar Clientes/Alumnos")
			},{
				description: "Cierre de Grupos",
				title: "Cierre de Grupos",
				action: () => alert("Cierre de Grupos")
			},{
				description: "Duplicar Grupos",
				title: "Duplicar Grupos",
				action: () => alert("Duplicar Grupos")
			},{
				description: "Asignar Habilidades a Grupos",
				title: "Asignar Habilidades a Grupos",
				action: () => alert("Asignar Habilidades a Grupos")
			},{
				description: "Asignar Cuotas a Grupos",
				title: "Asignar Cuotas a Grupos",
				action: () => alert("Asignar Cuotas a Grupos")
			}]
		},{
			title: MSG.AUXILIARIES,
			options: [{
				description: "Tipos de relaciones entre entidades",
				title: "Tipos de relaciones entre entidades",
				action: () => alert("Tipos de relaciones entre entidades")
			},{
				description: "Segmentación",
				title: "Segmentación",
				action: () => alert("Segmentación")
			},{
				description: "Año Académico",
				title: "Año Académico",
				action: () => alert("Año Académico")
			},{
				description: "Materias",
				title: "Materias",
				action: () => alert("Materias")
			},{
				description: "Niveles",
				title: "Niveles",
				action: () => alert("Niveles")
			},{
				description: "Habilidades",
				title: "Habilidades",
				action: () => alert("Habilidades")
			},{
				description: "Calificaciones",
				title: "Calificaciones",
				action: () => alert("Calificaciones")
			},{
				description: "Observaciones",
				title: "Observaciones",
				action: () => alert("Observaciones")
			},{
				description: "Aptitudes de Calidad",
				title: "Aptitudes de Calidad",
				action: () => alert("Aptitudes de Calidad")
			}]
		}];
	}

	/*
	build() {
        
	}
	*/
}
if(!window.customElements.get(TAG.AON_ACADEMY_MENU)){
	window.customElements.define(TAG.AON_ACADEMY_MENU, AonAcademyMenu);
}
