import { downscaleImage } from "../../services/compressImg.js";
import { insertInvoice } from "../../services/invoiceService.js";
import { getReader } from "../../services/utils.js";
import { Invoice } from "./Invoice.js";


export const uploadInvoices = (el, files) => {
    for(let i = 0; i < files.length; i++) {
        getReader(files[i]).then(f=>{
            uploadInvoice(f);
        });
    }
    el.value = null;
}

export const uploadInvoice = (file) => {
    if (file) {
        const data = {
            file,
            invoice: new Invoice('recibida')
        };
        if (data.file.contentType.indexOf("image") >= 0) {
            //compress 500kB / file, 500kb, quality default 0.9, maxResolution 1280
            downscaleImage(data.file, undefined, undefined, undefined).then(file => {
                data.file = file;
                return insertInvoice(data);
            });
        } else return insertInvoice(data);
    }
}