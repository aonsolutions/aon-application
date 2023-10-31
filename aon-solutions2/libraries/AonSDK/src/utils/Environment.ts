/**
 *  Configuration parameters
 */

// true para activar que los datos lleguen desde la api, false para usar datos ficticion locales
export let APIEnvironment  = false;
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
    '0114': {description:'ERROR_SESSION', result:'Error al establecer token'},
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
    '0304': {description:'ERROR_DOCUMENT_RAW', result:'Error al intentar obtener el documento'},
    /* 04XX  MESSAGE ERRORS */
    '0401': {description:'ERROR_MESSAGE_ARCHIVE', result:'Error al intentar archivar el mensaje'},
    '0402': {description:'ERROR_MESSAGE_REOPEN', result:'Error al intentar reabrir el mensaje'},
    '0403': {description:'ERROR_MESSAGE_COUNT', result:'Error al intentar obtener el numero de mensajes'},
    '0404': {description:'ERROR_MESSAGE_READ', result:'Error al intentar marcar como leído el mensaje'},
    /* 05XX  REPORT ERRORS */
    '0501': {description:'ERROR_REPORT_PAYMENTH', result:'Error al intentar recuperar cobros y pagos'},
    '0502': {description:'ERROR_REPORT_SALES', result:'Error al intentar recuperar ventas y gastos'},
    /* 06XX  TAX ERRORS */
    '0601': {description:'ERROR_TAX_MODEL_PAYMENTH', result:'Error al intentar realizar pago mediante NRC'},
    '0602': {description:'ERROR_TAX_MODEL_PAYMENTH', result:'Error al intentar realizar pago mediante cuenta bancaria'},
}
