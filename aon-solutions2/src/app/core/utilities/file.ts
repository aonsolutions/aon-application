export const FileToBase64 = (file: File) =>
new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result);
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
