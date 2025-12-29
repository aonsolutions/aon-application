import { MATERIAL_ICONS } from "../../environments/environments"

export const IN_REASON = [
	{
		name: 'Presencial',
		value: '0',
		icon: MATERIAL_ICONS.BUSINESS,
		clickable: true
	},
	{
		name: 'Teletrabajo',
		value: '1',
		icon: MATERIAL_ICONS.HOME,
		clickable: true
	},
	{
		name: 'Desplazado',
		value: '2',
		icon: MATERIAL_ICONS.DISTANCE,
		clickable: false
	}
] 

export const PAUSE_REASON = [
	{
		name: 'Descanso Laboral',
		value: '3',
		icon: MATERIAL_ICONS.COFFEE,
		clickable: true
	},
	{
		name: 'Consulta / Pruebas Médicas',
		value: '4',
		icon: MATERIAL_ICONS.STETHOSCOPE,
		clickable: true
	},
	{
		name: 'Hospitalización Familia',
		value: '5',
		icon: MATERIAL_ICONS.HOME_HEALTH,
		clickable: true
	},
	{
		name: 'Func. Legales Inexcusables',
		value: '6',
		icon: MATERIAL_ICONS.BALANCE,
		clickable: true
	},
	{
		name: 'Lactancia / Cuidado bebé',
		value: '7',
		icon: MATERIAL_ICONS.BREASTFEEDING,
		clickable: true
	},
	{
		name: 'Motivos familiares / Urgencias',
		value: '8',
		icon: MATERIAL_ICONS.FAMILY_RESTROOM,
		clickable: true
	},
	{
		name: 'Exámenes / Pruebas Académicas',
		value: '8',
		icon: MATERIAL_ICONS.SCHOOL,
		clickable: true
	},
	{
		name: 'Enfermedad / Accidente Propio',
		value: '9',
		icon: MATERIAL_ICONS.CLINICAL_NOTES,
		clickable: true
	},
	{
		name: 'Otro',
		value: '10',
		icon: MATERIAL_ICONS.UNKNOWN_DOCUMENT,
		clickable: false
	}
] 