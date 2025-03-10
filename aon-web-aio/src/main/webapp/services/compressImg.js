const getImage = (dataUrl) => {
    return new Promise((resolve, reject) => {
      const image = new Image();
      image.src = dataUrl;
      image.onload = () => {
        resolve(image);
      };
      image.onerror = (_el, err) => {
        reject(err.error);
      };
    });
  };
  
/**
 * 
 * @param {File} file 
 * @param {Number} maxSize kb
 * @param {Double} quality 
 * @param {Number} maxResolution 
 * @returns File
 */
  export const downscaleImage = async (
      file, 
      maxSize = Infinity,
      quality = 0.9,
      maxResolution = 1024
  ) => {
    let contentBase64 = file.content;
    let fileSize = file.size;
    const imageType = file.contentType;// 'image/jpeg'
    const fileUrl    = `data:${file.contentType};${file.contentEncoding},${file.content}`;
    const image      = await getImage(fileUrl);
    const oldWidth   = image.naturalWidth;
    const oldHeight  = image.naturalHeight;
    const newMaxSize = maxSize*1024;

    const longestDimension = oldWidth > oldHeight ? "width" : "height";
    const currentRes = longestDimension == "width" ? oldWidth : oldHeight;
  
    if (currentRes > maxResolution || (fileSize > newMaxSize) ) {
      console.log("need to resize...");
  
      // Calculate new dimensions
      const newSize   = longestDimension == "width"  ? Math.floor((oldHeight / oldWidth) * maxResolution) : Math.floor((oldWidth / oldHeight) * maxResolution);
      const newWidth  = longestDimension == "width"  ? maxResolution : newSize;
      const newHeight = longestDimension == "height" ? maxResolution : newSize;
  
      // Create a temporary canvas to draw the downscaled image on.
      const canvas  = document.createElement("canvas");
      canvas.width  = newWidth;
      canvas.height = newHeight;

      // console.log(newWidth, newHeight);
      // document.getElementById('dinamicDiv').appendChild(image);
  
      // Draw the downscaled image on the canvas and return the new data URL.
      const ctx = canvas.getContext("2d");

      ctx.drawImage(image, 0, 0, newWidth, newHeight);
     
      const newDataUrl = canvas.toDataURL(imageType, quality);

      contentBase64 = newDataUrl.split(",")[1];
      const sizeNew = atob(contentBase64).length;

      file.content = contentBase64;
      file.size = sizeNew;

      console.log("new width", newWidth, "height", newHeight);
      console.warn(`oldSize:${(fileSize/(1024*1024)).toFixed(2)}mb - newSize:${(sizeNew/(1024*1024)).toFixed(2)}mb`);
    }  else {
      console.log("not need to resize!");
    }

    return file;
};
  