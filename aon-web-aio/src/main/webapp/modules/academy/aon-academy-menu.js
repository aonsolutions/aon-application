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
            title: MSG.STUDENT,
            action: () => alert("description")
        },{
            title: MSG.AMORTIZATION_SHEET,
            action: () => alert("description")
        },{
            title: MSG.ENTRY,
            action: () => alert("description")
        }];
		this.options = [{
			title: MSG.ACADEMIC_MANAGEMENT,
			options: [ {
				description: MSG.STUDENTS,
				title: MSG.STUDENTS,
				action: () => alert("Alumnos")
			},{
				description: MSG.GROUPS,
				title: MSG.GROUPS,
				action: () => alert("Grupos")
			},{
				description: MSG.TEACHERS,
				title: MSG.TEACHERS,
				action: () => alert("Profesores")
			},{
				description: MSG.GRADES_QUERY,
				title: MSG.GRADES_QUERY,
				action: () => alert("Consulta Notas")
			},{
				description: MSG.LOANS,
				title: MSG.LOANS,
				action: () => alert("Prestamos")
			}]
		},{
			title: MSG.UTILITIES,
			options: [{
				description: MSG.RECLASSIFY_CUSTOMERS,
				title: MSG.RECLASSIFY_CUSTOMERS,
				action: () => alert("Reclasificar Clientes/Alumnos")
			},{
				description: MSG.GROUP_CLOSURE,
				title: MSG.GROUP_CLOSURE,
				action: () => alert("Cierre de Grupos")
			},{
				description: MSG.DUPLICATE_GROUPS,
				title: MSG.DUPLICATE_GROUPS,
				action: () => alert("Duplicar Grupos")
			},{
				description: MSG.ASSIGN_SKILLS_GROUPS,
				title: MSG.ASSIGN_SKILLS_GROUPS,
				action: () => alert("Asignar Habilidades a Grupos")
			},{
				description: MSG.ASSIGN_FEES_GROUPS,
				title: MSG.ASSIGN_FEES_GROUPS,
				action: () => alert("Asignar Cuotas a Grupos")
			}]
		},{
			title: MSG.AUXILIARIES,
			options: [{
				description: MSG.ENTITY_RELATIONSHIP_TYPES,
				title: MSG.ENTITY_RELATIONSHIP_TYPES,
				action: () => alert("Tipos de relaciones entre entidades")
			},{
				description: MSG.SEGMENTATION,
				title: MSG.SEGMENTATION,
				action: () => alert("Segmentación")
			},{
				description: MSG.ACADEMIC_YEAR,
				title: MSG.ACADEMIC_YEAR,
				action: () => alert("Año Académico")
			},{
				description: MSG.SUBJECTS,
				title: MSG.SUBJECTS,
				action: () => alert("Materias")
			},{
				description: MSG.LEVELS,
				title: MSG.LEVELS,
				action: () => alert("Niveles")
			},{
				description: MSG.SKILLS,
				title: MSG.SKILLS,
				action: () => alert("Habilidades")
			},{
				description: MSG.QUALIFICATIONS,
				title: MSG.QUALIFICATIONS,
				action: () => alert("Calificaciones")
			},{
				description: MSG.OBSERVATIONS,
				title: MSG.OBSERVATIONS,
				action: () => alert("Observaciones")
			},{
				description: MSG.QUALITY_SKILLS,
				title: MSG.QUALITY_SKILLS,
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
