import { AonElement } from './AonElement.js';
import { EVENT, CONSTANT, TAG } from "../environments/environments.js";
import Cropper from 'cropperjs';
import 'cropperjs/dist/cropper.min.css';
import '../css/aon-image-editor.css';
import { AonIconButton } from './aon-icon-button.js';

export class AonImageEditor extends AonElement {
    
    IMAGE;
    cropper;

    image;

    get id() {
        return this.getAttribute(CONSTANT.ID);
    }

    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
    }

    connectedCallback() {
        this.initialize();
        this.build();

        if (this.image) {
            this.loadImage(this.image);
        }
    }

    initialize() {
        this.IMAGE = "aonImageEditorImage";
    }

    build() {
        let imageContainer = this.createDiv("image-container", "aonImageEditorImageContainer");
        this.appendChild(imageContainer);

        let cropperContainer = this.createDiv("cropper-container", "aonImageEditorCropperContainer");
        cropperContainer.innerHTML = '<img id="image" />'
        imageContainer.appendChild(cropperContainer);

        let resultContainer = this.createDiv("result-container", "aonImageEditorResultContainer");
        resultContainer.innerHTML = '<h2>Imagen Recortada</h2>';
        this.appendChild(resultContainer);

        let img = this.createElement(TAG.IMG, "cropped-image", "aonImageEditorCroppedImage");
        resultContainer.appendChild(img);    

        let span = this.createSpan();
        span.id = this.id + "FloatSpan";
        span.style.position = "fixed";
        span.style.right = "20px";
        span.style.bottom = this.isSab() ? "80px" : "70px";

        let aonIconButton = new AonIconButton();
        aonIconButton.icon = "check";
        aonIconButton.id = "crop-btn";
        aonIconButton.title = "crop";
        aonIconButton.background = "#f1f1f1";
        span.appendChild(aonIconButton);
        this.appendChild(span);
        
        aonIconButton.addEventListener("click", () => this.cropImage());
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
            const base64 = croppedImageUrl.replace(/^data:image\/?[A-z]*;base64,/);
            this.dispatchEvent(new CustomEvent(EVENT.CROPPER));

            // this.querySelector('#cropped-image').src = croppedImageUrl;
            // this.querySelector('#result-container').style.display = 'block';
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

    getImage() {
        return this.image;
    }

    setImage(image) {
        this.image = image;
    }
}

if (!window.customElements.get(TAG.AON_IMAGE_EDITOR)) {
    window.customElements.define(TAG.AON_IMAGE_EDITOR, AonImageEditor);
}
