import { AonElement } from 'aonsolutions/components/AonElement.js';
import { CONSTANT, TAG } from "aonsolutions/environments/environments.js";
import Cropper from 'cropperjs';
import 'cropperjs/dist/cropper.min.css';

export class AonImageEditor extends AonElement {
    IMAGE;
    cropper;

    get id() {
        return this.getAttribute(CONSTANT.ID);
    }

    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
    }

    get image() {
        return this.getAttribute(CONSTANT.IMAGE);
    }

    set image(image) {
        this.setAttribute(CONSTANT.IMAGE, image);
    }

    connectedCallback() {
        this.initialize();
        this.build();
        this.addEventListeners();

        if (this.image) {
            this.loadImage(this.image);
        }
    }

    initialize() {
        this.IMAGE = "aonImageEditorImage";
    }

    build() {
        this.innerHTML = `
            <style>
                :host {
                    display: block;
                    font-family: Arial, sans-serif;
                    text-align: center;
                }

                #image-container {
                    display: none;
                    position: fixed;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 100%;
                    background: rgba(0, 0, 0, 0.8);
                    justify-content: center;
                    align-items: center;
                    z-index: 1000;
                }

                #cropper-container {
                    position: relative;
                    background: #fff;
                    max-width: 90%;
                    max-height: 80%;
                    overflow: hidden;
                }

                #controls {
                    position: absolute;
                    bottom: 10px;
                    left: 0;
                    right: 0;
                    display: flex;
                    justify-content: space-between;
                    padding: 10px;
                    background: rgba(255, 255, 255, 0.8);
                    box-shadow: 0 -2px 5px rgba(0, 0, 0, 0.2);
                }

                #controls button {
                    flex: 1;
                    margin: 0 5px;
                    padding: 10px;
                    border: none;
                    border-radius: 5px;
                    background: #007bff;
                    color: #fff;
                    cursor: pointer;
                    font-size: 16px;
                    text-align: center;
                }

                #controls button:hover {
                    background: #0056b3;
                }

                #cropped-image {
                    max-width: 100%;
                    height: auto;
                    display: block;
                    margin: 20px auto;
                }

                #result-container {
                    display: none;
                    text-align: center;
                }
            </style>
            <div id="image-container">
                <div id="cropper-container">
                    <img id="image" />
                </div>
                <div id="controls">
                    <button id="crop-btn">Recortar</button>
                    <button id="close-btn">Cancelar</button>
                </div>
            </div>
            <div id="result-container">
                <h2>Imagen Recortada</h2>
                <img id="cropped-image" />
            </div>
        `;
    }

    addEventListeners() {
        this.querySelector('#crop-btn').addEventListener('click', () => this.cropImage());
        this.querySelector('#close-btn').addEventListener('click', () => this.closeCropper());
    }

    loadBase64Image() {
        const base64Input = this.querySelector('#base64-input').value.trim();
        if (base64Input) {
            if (!base64Input.startsWith('data:image/')) {
                alert('La cadena base64 debe comenzar con "data:image/".');
                return;
            }
            this.loadImage(base64Input);
        }
    }

    loadImage(src) {
        const imageElement = this.querySelector('#image');
        imageElement.src = src;
        imageElement.onload = () => {
            this.querySelector('#image-container').style.display = 'flex';

            if (this.cropper) {
                this.cropper.destroy();
            }

            this.cropper = new Cropper(imageElement, {
                viewMode: 2,
                autoCropArea: 0.5,
                movable: true,
                zoomable: true,
                rotatable: true,
                scalable: true,
                background: true,
                responsive: true,
                modal: true,
                guides: true,
                highlight: true,
                cropBoxMovable: true,
                cropBoxResizable: true,
                toggleDragModeOnDblclick: true,
                minCropBoxWidth: 50,
                minCropBoxHeight: 50,
                ready() {
                    // Ajusta el tamaño del cropBoxData si es necesario
                }
            });
        };
    }

    cropImage() {
        if (this.cropper) {
            const cropBoxData = this.cropper.getCropBoxData();
            const canvas = this.cropper.getCroppedCanvas({
                width: cropBoxData.width,
                height: cropBoxData.height,
            });

            const croppedImageUrl = canvas.toDataURL('image/jpeg');
            this.querySelector('#cropped-image').src = croppedImageUrl;
            this.querySelector('#result-container').style.display = 'block';
            this.closeCropper();
        }
    }

    closeCropper() {
        this.querySelector('#image-container').style.display = 'none';
        if (this.cropper) {
            this.cropper.destroy();
            this.cropper = null;
        }
    }
}

if (!window.customElements.get(TAG.AON_IMAGE_EDITOR)) {
    window.customElements.define(TAG.AON_IMAGE_EDITOR, AonImageEditor);
}
