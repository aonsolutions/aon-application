/**
 *  Configuration parameters
 */

// true para activar que los datos lleguen desde la api, false para usar datos ficticion locales
export let APIEnvironment  = true;
// true activa unos tests simples para ver que los métodos funcionan correctamente, false para desactivarlos
export let test: boolean   = false;

/**
 *
 * CONSTS
 *
 */

// URL for test environment
export const BASE_URL = 'https://aonsolutions.org';
//export const BASE_URL = 'https://aon.solutions/';

//export const BASE_URL = 'http://localhost:8080';

export const GET_SINGLE = 'singleObjectGet'
export const CREATE_SINGLE = 'singleObjectCreate';
export const UPDATE_SINGLE = 'singleObjectUpdate';
export const DELETE_SINGLE = 'singleObjectDelete';
export const GET_MULTIPLE = 'multipleObjectGet';
export const CREATE_MULTIPLE = 'multipleObjectCreate';
export const UPDATE_MULTIPLE = 'multipleObjectUpdate';
export const DELETE_MULTIPLE = 'multipleObjectDelete';

export const GET_METHOD = 'GET';
export const POST_METHOD = 'POST';
export const PUT_METHOD = 'PUT';
export const DELETE_METHOD = 'DELETE';


/**
 *
 * ERROR DATA
 *
 */

export const ERRORS = {
    /* 00XX */
    '0000' : {description:'OK_OPERATION_SUCCEED', result:''},
    /* 01XX */
    '0101': {description:'ERROR_LOGIN_INVALID_CREDENTIALS', result:'Error al iniciar sesión'},
    '0111': {description:'SESSION_EXPIRED', result:'No existe sesión'},
    '0112': {description:'ERROR_SESSION', result:'No se ha seleccionado empresa'},
    '0113': {description:'ERROR_SESSION', result:'Error al seleccionar la empresa'},
    '0123': {description:'ERROR_MODEL', result:'Error al intentar acceder al modelo'},
    '0124': {description:'ERROR_DENIED', result:'No tiene permiso para acceder al recurso'},
    '0199': {description:'ERROR_NOT_IMPLEMENTED', result:'Paciencia amigo, paciencia'},
    /* 02XX */
    '0201': {description:'ERROR_MODEL_CREATE', result:'Error en la creación'},
    '0202': {description:'ERROR_MODEL_UPDATE', result:'Error en la edición'},
    '0203': {description:'ERROR_MODEL_DELETE', result:'Error al intentar eliminar'},
    '0204': {description:'ERROR_MODEL_FILTER', result:'Los filtros introducidos son incorrectos'},
    '0205': {description:'ERROR_MODEL_ELEMENT', result:'No se encontró ningún elemento'},
    '0206': {description:'ERROR_MODEL_ELEMENT', result:'Error al intentar obtener el elemento'},
    /* 03XX  DOCUMENT ERRORS */
    '0301': {description:'ERROR_DOCUMENT_UPLOAD', result:'Error al intentar subir el documento'},
    '0302': {description:'ERROR_DOCUMENT_UPLOAD', result:'Ruta o nombre del documento incorrectos'},
    '0303': {description:'ERROR_DOCUMENT_UPLOAD', result:'Ya existe un documento con ese nombre'},
}
