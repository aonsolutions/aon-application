import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
import * as MATERIAL_ICONS from "../../environments/materialIcons.js";


export const TaxType = {
  IVA: 'IVA',
  IRPF: 'IRPF'
}

export const TaxIVAPercentage = [
  {value:21.0, name:'21%'},
  {value:10.0, name:'10%'},
  {value:4.0, name:'4%'},
  {value:0.0, name:'0%'}
];

export const TaxIRPFPercentage = [
  {value:19, name:'19%'},
  {value:15.0, name:'15%'},
  {value:7.0, name:'7%'}
];

export const InvoiceAction = {
  BACK: {
    id: 'Back',
    name: MSG.AON_MSG_BACK,
    icon: 'arrow_back'
  },
  NEXT: {
    id: 'NextInvoice',
    name: MSG.AON_MSG_NEXT,
    icon: 'keyboard_arrow_right'
  },
  PREVIOUS: {
    id: 'PreviousInvoice',
    name: MSG.AON_MSG_PREVIOUS,
    icon: 'keyboard_arrow_left'
  },
  DUPLICATE: {
    id: 'Duplicate',
    name: MSG.AON_MSG_DUPLICATE_INVOICE,
    icon: 'file_copy'
  },
  RECTIFY: {
    id: 'Rectify',
    name: MSG.AON_MSG_RECTIFY_INVOICE,
    icon: 'swap_calls'
  },
  REJECT: {
    id: 'Reject',
    name: MSG.AON_MSG_REJECT_INVOICE,
    icon: 'report'
  },
  RECORD: {
    id: 'Accounting',
    name: MSG.AON_MSG_RECORD_INVOICE,
    icon: MATERIAL_ICONS.ADD_TASK
  },
  DELETE: {
    id: 'Delete',
    name: MSG.AON_MSG_TO_TRASH,
    icon: 'delete'
  },
  RESTORE: {
    id: 'Restore',
    name: MSG.AON_MSG_RESTORE_INVOICE,
    icon: '360'
  },
  DELETE_FOREVER: {
    id: 'DeleteForever',
    name: MSG.AON_MSG_DELETE_FOREVER,
    icon: 'delete_sweep'
  },
  COMMENT: {
    id: CONSTANT.COMMENT.initCap(),
    name: MSG.AON_MSG_ADD_COMMENT,
    icon: MATERIAL_ICONS.COMMENT
  },
  SHOW_FILE: {
    id: 'ShowFile',
    name: MSG.AON_MSG_SHOW_FILE,
    icon: 'visibility'
  },
  ADD_FILE: {
    id: 'AddFile',
    name: MSG.AON_MSG_ADD_FILE,
    icon: 'attach_file'
  },
  ADD_INVOICE: {
    id: 'AddInvoice',
    name: MSG.AON_MSG_ADD_INVOICE,
    icon: 'add'
  },
  DOWNLOAD: {
    id: 'DownloadInvoice',
    name: MSG.AON_MSG_DOWNLOAD_INVOICE,
    icon: 'file_download'
  },
  SEND: {
    id: 'SendInvoice',
    name: MSG.AON_MSG_SEND_INVOICE,
    icon: 'mail'
  }
}
