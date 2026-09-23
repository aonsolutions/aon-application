/**
 * Reconoce y normaliza los ficheros antes de subirlos a S3.
 *
 * Hay dos cosas que el navegador no nos da bien:
 *
 * - El tipo del fichero sale de lo que sepa el sistema operativo, y con los HEIC muchas veces no
 *   sabe nada: llega vacio, S3 guarda el objeto como "binary/octet-stream" y la lambda no lo
 *   reconoce como imagen, asi que la factura se queda sin convertir a PDF.
 * - El HEIC en si, que es como guarda las fotos el iPhone y que no lee ni ImageIO ni PDFBox, asi
 *   que hay que pasarlo a JPEG aqui, antes de subirlo.
 *
 * Por eso el tipo se decide mirando, en este orden, lo que dice el navegador, la extension y la
 * firma de los primeros bytes, que es lo unico que no miente.
 */

const HEIC_EXTENSIONS = ['heic', 'heif', 'hif'];

/** Marcas ISO-BMFF que identifican una imagen HEIF o AVIF. */
const HEIF_BRANDS = ['heic', 'heix', 'heim', 'heis', 'hevc', 'hevx', 'hevm', 'hevs', 'mif1', 'msf1'];
const AVIF_BRANDS = ['avif', 'avis'];

const CONTENT_TYPES = {
  jpg: 'image/jpeg', jpeg: 'image/jpeg', jpe: 'image/jpeg',
  png: 'image/png', gif: 'image/gif', bmp: 'image/bmp',
  tif: 'image/tiff', tiff: 'image/tiff', webp: 'image/webp',
  heic: 'image/heic', heif: 'image/heif', hif: 'image/heic', avif: 'image/avif',
  pdf: 'application/pdf'
};

/**
 * Para el atributo accept de los input file de facturas. Se ponen tambien las extensiones porque
 * hay sistemas que no saben que tipo tiene un HEIC y sin ellas no dejarian elegirlo.
 */
export const PDF_OR_IMAGE_ACCEPT =
  'application/pdf,image/*,.pdf,.jpg,.jpeg,.png,.gif,.bmp,.tif,.tiff,.webp,.heic,.heif';

const JPEG_QUALITY = 0.92;
const HEADER_SIZE = 12;

export const getExtension = (name) => {
  const dot = name ? name.lastIndexOf('.') : -1;
  return dot > 0 && dot < name.length - 1 ? name.substring(dot + 1).toLowerCase() : '';
};

export const getContentType = (name) => CONTENT_TYPES[getExtension(name)] || '';

/**
 * El tipo del fichero segun el navegador, y si no lo sabe, segun la extension o la firma de los
 * primeros bytes.
 *
 * @param {File} file
 * @returns {Promise<String>} el content type, o cadena vacia si no hay forma de saberlo.
 */
export const readContentType = async (file) => {
  if (!file) return '';
  if (file.type) return file.type.toLowerCase();

  const byName = getContentType(file.name);
  if (byName) return byName;

  try {
    return sniff(new Uint8Array(await file.slice(0, HEADER_SIZE).arrayBuffer()));
  } catch (e) {
    return '';
  }
};

/**
 * @param {File} file
 * @returns {Promise<Boolean>} true si es un PDF o una imagen, que es lo unico que admitimos como
 *          factura.
 */
export const isPdfOrImage = async (file) => {
  const contentType = await readContentType(file);
  return contentType === 'application/pdf' || contentType.startsWith('image/');
};

/**
 * @param {File} file
 * @returns {Promise<Boolean>} true si es un HEIC/HEIF, mire donde mire.
 */
export const isHeic = async (file) => {
  if (HEIC_EXTENSIONS.includes(getExtension(file.name))) return true;

  const contentType = await readContentType(file);
  return contentType === 'image/heic' || contentType === 'image/heif'
    || contentType === 'image/heic-sequence' || contentType === 'image/heif-sequence';
};

/**
 * Deja el fichero listo para subir: los HEIC pasados a JPEG y el content type siempre puesto.
 *
 * Si algo falla devuelve el fichero original, porque subirlo aunque sea sin convertir siempre es
 * mejor que perder la factura.
 *
 * @param {File} file
 * @returns {Promise<File>}
 */
export const normalizeImageFile = async (file) => {
  if (!file || !file.name) return file;

  try {
    if (await isHeic(file)) return await heicToJpeg(file);
  } catch (e) {
    console.warn('No se ha podido convertir el HEIC, se sube tal cual', e);
    return file;
  }

  return await withContentType(file);
};

/** Devuelve el mismo fichero con el content type puesto, si es que faltaba y sabemos cual es. */
const withContentType = async (file) => {
  if (file.type) return file;

  const contentType = await readContentType(file);
  return contentType ? new File([file], file.name, { type: contentType, lastModified: file.lastModified }) : file;
};

/**
 * libheif compilado a wasm, que pesa lo suyo: se carga con import() dinamico para que solo baje
 * cuando alguien sube de verdad un HEIC.
 */
const heicToJpeg = async (file) => {
  const { default: heic2any } = await import(/* webpackChunkName: "heic2any" */ 'heic2any');
  const converted = await heic2any({ blob: file, toType: 'image/jpeg', quality: JPEG_QUALITY });
  const blob = Array.isArray(converted) ? converted[0] : converted;

  const extension = getExtension(file.name);
  const name = extension ? file.name.substring(0, file.name.length - extension.length) + 'jpg' : file.name + '.jpg';

  return new File([blob], name, { type: 'image/jpeg', lastModified: file.lastModified });
};

/** Reconoce el tipo por la firma de los primeros bytes. */
const sniff = (bytes) => {
  if (starts(bytes, 0x25, 0x50, 0x44, 0x46)) return 'application/pdf'; // %PDF
  if (starts(bytes, 0xFF, 0xD8, 0xFF)) return 'image/jpeg';
  if (starts(bytes, 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)) return 'image/png';
  if (ascii(bytes, 0, 'GIF8')) return 'image/gif';
  if (ascii(bytes, 0, 'BM')) return 'image/bmp';
  if (starts(bytes, 0x49, 0x49, 0x2A, 0x00) || starts(bytes, 0x4D, 0x4D, 0x00, 0x2A)) return 'image/tiff';
  if (ascii(bytes, 0, 'RIFF') && ascii(bytes, 8, 'WEBP')) return 'image/webp';

  // HEIF y AVIF son contenedores ISO-BMFF: "....ftyp" y detras la marca del formato.
  if (ascii(bytes, 4, 'ftyp')) {
    const brand = text(bytes, 8, 4);
    if (HEIF_BRANDS.includes(brand)) return 'image/heic';
    if (AVIF_BRANDS.includes(brand)) return 'image/avif';
  }
  return '';
};

const starts = (bytes, ...signature) =>
  bytes.length >= signature.length && signature.every((byte, i) => bytes[i] === byte);

const ascii = (bytes, offset, expected) => text(bytes, offset, expected.length) === expected;

const text = (bytes, offset, length) =>
  bytes.length >= offset + length ? String.fromCharCode(...bytes.slice(offset, offset + length)) : '';
