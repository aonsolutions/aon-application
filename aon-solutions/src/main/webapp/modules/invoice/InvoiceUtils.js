import { downscaleImage } from "../../services/compressImg.js";
import { insertInvoice } from "../../services/invoiceService.js";
import { getReader } from "../../services/utils.js";
import { Invoice } from "./Invoice.js";
import * as LS from '../../services/localStorageService.js';

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

export const s3UploadInvoices = (el, files, jobId) => {
    let arr = [];
    let data = { uploaded : 0 };
    for (let file of files) {
        s3UploadInvoice(file, jobId, data , undefined, (f) => alert(f.name + ' ok'), (f) => alert(f.name + ' error'));
    }
    el.value = null;
    return arr;
}

export const s3UploadInvoice = (file, jobId, data, invofoxConfiguration, success, error) => {
    let formData = new FormData();
    let xhr = new XMLHttpRequest();
  
  	let fileOrder = `0${data.uploaded}`.slice(-2);
  	data.uploaded += 1;
  	
    formData.append('key', 
        'invoices'
        + `/${LS.getDomainName()}`
        + `/${LS.getDomainDocument()}`
        + `/${LS.getDomainLogin()}`
        + `/${jobId}` 
        + `/${fileOrder}_${file.name}`);
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
          error(file, xhr);
        }
    });
    xhr.send(formData);
}

export const s3UploadRawdoc = (file, success, error) => {
    let key = LS.getDomainId() + crypto.randomUUID();  

    let formData = new FormData();
    let xhr = new XMLHttpRequest();
    

  	formData.append('key', key);
    formData.append('success_action_status', '201');
    formData.append('Content-Type', file.type);
    formData.append('file', file);

    xhr.open('POST', "https://aon-rawdoc.s3.amazonaws.com/", true);
    xhr.addEventListener('readystatechange', (e) => {
        if (xhr.readyState == 4 && xhr.status == 201) {
          // Done. Inform the user
            let invoice = new Invoice().setType('recibida');
            invoice.file = {
                contentType: file.type,
                s3Bucket: 'aon-rawdoc',
                s3Key: key
            };
    
            insertInvoice(data).then(r => success(file))
                    .catch((e) => error(file, e));
         } else if (xhr.readyState == 4 && xhr.status != 200) {
          // Error. Inform the user
          error(file, xhr);
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