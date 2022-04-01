import { downscaleImage } from "../../services/compressImg.js";
import { insertInvoice } from "../../services/invoiceService.js";
import { getReader } from "../../services/utils.js";
import { Invoice } from "./Invoice.js";

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