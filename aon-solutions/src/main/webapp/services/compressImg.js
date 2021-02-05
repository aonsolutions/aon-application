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
  
  //return base64
  export const downscaleImage = async (
      file, 
      maxSize = Infinity,
      quality = 0.9,
      maxResolution = 1024
  ) => {
    let contentBase64 = file.content;
    let fileSize = file.size;
    const imageType = file.contentType;// 'image/jpeg'
    const fileUrl = `data:${file.contentType};${file.contentEncoding},${file.content}`;
    const image = await getImage(fileUrl);
    const oldWidth = image.naturalWidth;
    const oldHeight = image.naturalHeight;
    const newMaxSize = maxSize*1024;

    console.log("dims", oldWidth, oldHeight);
    console.log(fileSize, newMaxSize);

    const longestDimension = oldWidth > oldHeight ? "width" : "height";
    const currentRes = longestDimension == "width" ? oldWidth : oldHeight;
    console.log("longest dim", longestDimension, currentRes);
  
    if (currentRes > maxResolution || (fileSize > newMaxSize) ) {
      console.log("need to resize...");
  
      // Calculate new dimensions
      const newSize = longestDimension == "width" 
          ? Math.floor((oldHeight / oldWidth) * maxResolution)
          : Math.floor((oldWidth / oldHeight) * maxResolution);
          
      const newWidth = longestDimension == "width" ? maxResolution : newSize;
      const newHeight = longestDimension == "height" ? maxResolution : newSize;
      console.log("new width / height", newWidth, newHeight);
  
      // Create a temporary canvas to draw the downscaled image on.
      const canvas = document.createElement("canvas");
      canvas.width = newWidth;
      canvas.height = newHeight;
  
      // Draw the downscaled image on the canvas and return the new data URL.
      const ctx = canvas.getContext("2d");
      ctx.drawImage(image, 0, 0, newWidth, newHeight);
      const newDataUrl = canvas.toDataURL(imageType, quality);
      contentBase64 = newDataUrl.split(",")[1];
      const sizeNew = window.atob(contentBase64).length;
      file.content = contentBase64;
      file.size = sizeNew;

      console.warn(`oldSize:${(fileSize/(1024*1024)).toFixed(2)}mb - newSize:${(sizeNew/(1024*1024)).toFixed(2)}mb`);
    } 

    return file;
};
  