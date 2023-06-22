export const FileToBase64 = (file: File) =>
new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result ? (reader.result as string).split(',')[1] : reader.result);
    reader.onerror = (error) => reject(error);
});

export const Base64ToFile = (base64: string, fileName: string, fileType: string) => 
new Promise((resolve, reject) => {
  fetch(base64)
    .then(res => res.blob())
    .then(blob => {
      const file = new File([blob], fileName,{ type: fileType })
      resolve(file)
    })
    .catch(error => reject(error))
});

export const b64toBlob = (b64Data: string, contentType='', sliceSize=512) => {
  const byteCharacters = atob(b64Data);
  const byteArrays = [];

  for (let offset = 0; offset < byteCharacters.length; offset += sliceSize) {
    const slice = byteCharacters.slice(offset, offset + sliceSize);

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