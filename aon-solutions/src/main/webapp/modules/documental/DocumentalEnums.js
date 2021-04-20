import { MSG } from '../../environments/environments.js';

export const DocumentalSidenav = {
  DOCUMENTS: {
    id: 'Documents',
    name: MSG.AON_MSG_DOCUMENTS.toUpperCase()
  },
  TYPES: {
    id: 'Types',
    name: MSG.AON_MSG_TYPES.toUpperCase()
  },
  CATEGORIES: {
    id: 'Categories',
    name: MSG.AON_MSG_CATEGORIES.toUpperCase()
  },
  TAGS: {
    id: 'Tags',
    name: MSG.AON_MSG_TAGS.toUpperCase()
  }
}

export const ASESOR_TYPE_OPTION = [
  {value: 'enterprise', name: 'Empresa'},
  {value: 'employee', name: 'Empleado'},
  {value: 'asesor', name: 'Asesor'}
];

export const ENTERPRISE_TYPE_OPTION = [
  {value: 'enterprise', name: 'Empresa'},
  {value: 'employee', name: 'Empleado'}
];

export const EMPLOYEE_TYPE_OPTION = [
  {value: 'employee', name: 'Empleado'}
]
