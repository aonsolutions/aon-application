import { getReader } from "../../services/utils";


export const uploadDocuments = (files) => {
    for(let i = 0; i < files.length; i++) {
        getReader(files[i]).then(f=>{
            
        });
    }
}