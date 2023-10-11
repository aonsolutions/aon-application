export const FileToBase64 = (file: File) =>
  new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload  = () => resolve(reader.result ? (reader.result as string).split(',')[1] : reader.result);
    reader.onerror = (error) => reject(error);
});

export const Base64ToFile = (base64: string, fileName: string, fileType: string): File | unknown => 
  new Promise((resolve, reject) => {
    fetch(base64)
      .then(res => res.blob())
      .then(blob => {
        const file = new File([blob], fileName,{ type: fileType })
        resolve(file)
      })
      .catch(error => reject(error))
  });

export const Base64toBlob = (b64Data: string, contentType='', sliceSize=512) => {
  const byteCharacters  = atob(b64Data);
  const byteArrays      = [];

  for (let offset = 0; offset < byteCharacters.length; offset += sliceSize) {
    const slice       = byteCharacters.slice(offset, offset + sliceSize);
    const byteNumbers = new Array(slice.length);

    for (let i = 0; i < slice.length; i++) {
      byteNumbers[i] = slice.charCodeAt(i);
    }

    const byteArray = new Uint8Array(byteNumbers);
    byteArrays.push(byteArray);
  }

  const blob = new Blob(byteArrays, {type: contentType});
  return blob;
}

// Convertimos el PDF en base 64 a un formato compatible con ng2-pdfjs-viewer
export const Base64ToArrayBuffer = (base64 : string): Uint8Array => {
  let binary_string = window.atob(base64);
  let len           = binary_string.length;
  let bytes         = new Uint8Array( len );
  for (let i = 0; i < len; i++){
    bytes[i] = binary_string.charCodeAt(i);
  }
  return bytes;
}
