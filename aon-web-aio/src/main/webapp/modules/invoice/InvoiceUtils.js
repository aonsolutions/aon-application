import { CONSTANT } from "../../environments/environments.js";
import { downscaleImage } from "../../services/compressImg.js";
import { insertInvoice } from "../../services/invoiceService.js";
import { getReader } from "../../services/utils.js";
import { Invoice } from "./Invoice.js";
import * as LS from '../../services/localStorageService.js';
import * as OPTION from './InvoiceOptions.js';

export const uploadInvoices = async(el, files) => {
    let arr = [];
    for await (let file of files) {
        let reader = await getReader(file).catch(()=>null);
        if(reader){
            let invoice = await uploadInvoice(reader);
            if(invoice)
                arr.push(invoice);
        }
    }
    el.value = null;
    return arr;
}

export const uploadInvoice = async(file) => {
    if (file) {
        const data = {
            file,
            invoice: new Invoice().setType('recibida')
        };
        if (data.file.contentType.indexOf("image") >= 0) {
            //compress 500kB / file, 500kb, quality default 0.9, maxResolution 1280
            await downscaleImage(data.file, undefined, undefined, undefined).then(file => {
                data.file = file;
            });
        } 
        return await insertInvoice(data).catch((e) => {
            alert(e.message)
            return null;
        });
    }
}

export const uploadInvoice2 = (file, success, error) => {
    if (file) {
        getReader(file).then(f => {
            let data = {
                file: f,
                invoice: new Invoice().setType('recibida')
            };
    
            if (data.file.contentType.indexOf("image") >= 0) {
                 //compress 500kB / file, 500kb, quality default 0.9, maxResolution 1280
                 downscaleImage(data.file, undefined, undefined, undefined).then(img => {
                     data.file = img;
                     insertInvoice(data)
                     .then(r => success(file))
                     .catch((e) => error(file, e));
                 });
            } else {
                insertInvoice(data)
                .then(r => success(file))
                .catch((e) => error(file, e));
            }
        }).catch((e) => error(file, e));
    }
}

export const s3UploadInvoices = (company, el, files, jobId) => {
    let arr = [];
    let data = { uploaded : 0 };
    for (let file of files) {
        s3UploadInvoice(company, file, jobId, data , (f) => alert(f.name + ' ok'), (f) => alert(f.name + ' error'));
    }
    el.value = null;
    return arr;
}

export const s3UploadInvoice = (company, file, jobId, data, success, error) => {
    let formData = new FormData();
    let xhr = new XMLHttpRequest();
    let prefix = data.prefix || '';
  	let fileOrder =  `0${data.uploaded}`.slice(-2);
  	data.uploaded += 1;
   
    let re = /(?:\.([^.]+))?$/;
    let ext = re.exec(file.name)[0];
    
    let filename = ext ? file.name.replace(ext, '') : file.name;

    let name = filename.length > 30 ? filename.substring(0,30) : filename;
    let base64 = name;

    try{
        base64 = btoa(name) + (ext || '');
    } catch(e) {
        base64 = btoa(encodeURIComponent(name)) + (ext || '');
    } 
    base64 = base64.replace(/=/g, '').replace(/\//g, '_').replace(/\+/g, '-');  
    if(data.activity) {
        formData.append('key', 
            'invoices'
            + `/${LS.getDomainName()}`
            + `/${company.document}`
            + `/${data.activity || 'all'}`
            + `/${LS.getDomainLogin()}`
            + `/${jobId}`
            + `/${fileOrder}_${prefix}_${base64}`);
    } else {
        formData.append('key', 
            'invoices'
            + `/${LS.getDomainName()}`
            + `/${company.document}`
            + `/${LS.getDomainLogin()}`
            + `/${jobId}`
            + `/${fileOrder}_${prefix}_${base64}`);
    }

    formData.append('success_action_status', '201');
    formData.append('Content-Type', file.type);
    formData.append('file', file);

    xhr.open('POST', "https://aon-upload-post.s3.amazonaws.com/", true);
    xhr.addEventListener('readystatechange', (e) => {
        if (xhr.readyState == 4 && xhr.status == 201) {
          // Done. Inform the user
          success(file);
        } else if (xhr.readyState == 4 && xhr.status != 200) {
          // Error. Inform the user
          console.log(e);
          error("error inesperado");
        }
    });
    xhr.send(formData);
}

export const generateJobId = () => {
    const date = new Date(Date.now());
    const day = zeros(date.getDate());
    const month = zeros(date.getMonth() + 1);
    const year = date.getFullYear();
    const hours = zeros(date.getHours());
    const minutes = zeros(date.getMinutes());
    const seconds = zeros(date.getSeconds());

    return `${year}${month}${day}${hours}${minutes}${seconds}`;
}

export const zeros = (val) => {
    return val < 10 ? `0${val}` : val;
}

export const getTrashPendingFromOption = (invoice) => {
    invoice = new Invoice(invoice);
    if(invoice.isEmitida()) {
        return OPTION.INVOICE_ISSUED_BETA;
    } else if (invoice.isTicket()) {
        return OPTION.INVOICE_TICKET;
    } else return OPTION.INVOICE_RECEIVED_BETA;
}

export const getFutureTrashPendingFromOption = (invoice) => {
    invoice = new Invoice(invoice);
    if(invoice.isEmitida()) {
        return OPTION.FUTURE_INVOICE_ISSUED_BETA;
    } else if (invoice.isTicket()) {
        return OPTION.FUTURE_INVOICE_TICKET;
    } else return OPTION.FUTURE_INVOICE_RECEIVED_BETA;
}

export const getRejectFromOption = (invoice) => {
    invoice = new Invoice(invoice);
    if(invoice.lastStatus === CONSTANT.PROCESSED || invoice.lastStatus === CONSTANT.PROCESSING) {
        return OPTION.RAWDOC_PROCESSING;
    } else if(invoice.isEmitida()) {
        return OPTION.PROFORMA_INVOICES;
    } else if(invoice.isTicket()) {
        return OPTION.RAWDOC_INBOX_TICKET_NEW;
    } else return OPTION.RAWDOC_INBOX_RECEIVED_NEW;
}

export const getFutureRejectFromOption = (invoice) => {
    invoice = new Invoice(invoice);
    if(invoice.lastStatus === CONSTANT.PROCESSED || invoice.lastStatus === CONSTANT.PROCESSING) {
        return OPTION.FUTURE_RAWDOC_PROCESSING;
    } else if(invoice.isEmitida()) {
        return OPTION.FUTURE_PROFORMA_INVOICES;
    } else if(invoice.isTicket()) {
        return OPTION.FUTURE_RAWDOC_INBOX_TICKET_NEW;
    } else return OPTION.FUTURE_RAWDOC_INBOX_RECEIVED_NEW;
}

export const getRestoreFromOption = (invoice) => {
    invoice = new Invoice(invoice);
    if(invoice.isRejected()) {
        return OPTION.RAWDOC_REJECT;
    } else return OPTION.RAWDOC_TRASH;
}

export const getFutureRestoreFromOption = (invoice) => {
    invoice = new Invoice(invoice);
    if(invoice.isRejected()) {
        return OPTION.FUTURE_RAWDOC_REJECT;
    } else return OPTION.FUTURE_RAWDOC_TRASH;
}
    
export const getRestoreToOption = (invoice) => {
    invoice = new Invoice(invoice);
    if(invoice.lastStatus === CONSTANT.PROCESSED || invoice.lastStatus === CONSTANT.PROCESSING) {
        return OPTION.RAWDOC_PROCESSING;
    } else if(invoice.isEmitida()) {
        return OPTION.PROFORMA_INVOICES;
    } else if(invoice.isTicket()) {
        return OPTION.RAWDOC_INBOX_TICKET_NEW;
    } else return OPTION.RAWDOC_INBOX_RECEIVED_NEW;
}

export const getFutureRestoreToOption = (invoice) => {
    invoice = new Invoice(invoice);
    if(invoice.lastStatus === CONSTANT.PROCESSED || invoice.lastStatus === CONSTANT.PROCESSING) {
        return OPTION.FUTURE_RAWDOC_PROCESSING;
    } else if(invoice.isEmitida()) {
        return OPTION.FUTURE_PROFORMA_INVOICES;
    } else if(invoice.isTicket()) {
        return OPTION.FUTURE_RAWDOC_INBOX_TICKET_NEW;
    } else return OPTION.FUTURE_RAWDOC_INBOX_RECEIVED_NEW;
}