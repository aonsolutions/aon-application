export const extensionsEnums =  {
    "image/jpeg":"jpeg",
    "image/jpg":"jpg",
    "image/png": "png",
    "application/pdf":"pdf",
    "application/msword":"doc",
    "application/zip":"zip",
    "application/vnd.ms-excel": "xls",
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": "xlsx",
    "text/plain": "txt",
    "text/html": "html"
}

export const extensionsType = (type) =>  {
    let contentType = null;
    for (const key in extensionsEnums) {
        const value = extensionsEnums[key];
        if(type === value){
            contentType = key;
            break;
        }
    }
    return contentType;
}