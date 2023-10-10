/*
 * URLS API FOR MESSAGES
 */

export const enum MESSAGE_URL {
    GET_TASK_QUERY_LIST = '/ms/api/task',
    GET_NOTIFICATION_LIST = '/ms/api/notification',
    GET_CAU = '/ms/api/task/cau',
    GET_TASK_HOLDER_ONE = '/ms/api/taskholder',
    GET_WORKGROUP_LIST = '/ms/api/workgroup',
    GET_COUNT_NOTIFICATION = '/ms/api/notification/total-notification',
    GET_MARK_READ_NOTIFICATION = '/ms/api/notification/mark-read-notification',
    GET_ONE_MESSAGE = '/ms/api/task/one',
    SAVE_ONE_MESSAGE = '/ms/api/task',
}

export const enum TAXMODEL_URL {
    GET_TAXMODEL_LIST = '/ms/api/fiscal/models',
    PAY_TAXMODEL = '/ms/api/fiscal/markAsFinished',
}

export const enum INVOICE_URL {
    GET_INVOICE_LIST = '/ms/api/invoice',
    GET_INVOICE__ONE = '/ms/api/invoice',
}