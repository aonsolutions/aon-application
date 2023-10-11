import { MATERIAL_ICONS, MSG } from '../../environments/environments.js';

export const ALL_FILES = {
  name: MSG.ALL_FILES,
  icon: 'insert_drive_file'
};

export const ENTERPRISE = {
  name: MSG.ENTERPRISE,
  icon: MATERIAL_ICONS.BUSINESS
}

export const EMPLOYEE = {
  name: MSG.EMPLOYEE,
  icon: MATERIAL_ICONS.PERSON
}

export const ASESOR = {
  name: MSG.ASESOR,
  icon: 'work'
}

export const DOCUMENTS = {
    id: 'Documents',
    name: MSG.DOCUMENTS.toUpperCase(),
    options: [ALL_FILES]
};

export const TYPES = {
    id: 'Types',
    name: MSG.TYPES.toUpperCase(),
};

export const getOptions = (dur) => {
    if(dur.isDocumentalManager()) {
      TYPES.options = [ENTERPRISE, EMPLOYEE, ASESOR];
    } else if(dur.isDocumentalPortal()) {
      TYPES.options = [ENTERPRISE, EMPLOYEE];
    } else if(dur.isDocumental()) {
      TYPES.options = [EMPLOYEE];
    }
    return [DOCUMENTS, TYPES];
}