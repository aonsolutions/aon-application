import { newComponent } from "../../services/utils.js";

/**
 * Create a row with space between elements inside 
 * @param {object} styles 
 * @returns 
 */
export const createSpaceBetweenRow = (styles) => newComponent({
    classes: ['flexRow', 'flexJustifyBetween', 'flexAlignCenter'],
    styles: styles
});

/**
 * Create a row with elements inside aligned to the end 
 * @param {object} styles 
 * @returns 
 */
 export const createEndJustifiedRow = (styles) => newComponent({
    classes: ['flexRow', 'flexJustifyEnd', 'flexAlignCenter'],
    styles: styles
});

/**
 * Create a row with elements inside aligned to the start 
 * @param {object} styles 
 * @returns 
 */
 export const createStartJustifiedRow = (styles) => newComponent({
    classes: ['flexRow', 'flexJustifyStart', 'flexAlignCenter'],
    styles: styles
});

/**
 * Create a text 
 * @param {object} properties 
 * @returns 
 */
export const createText = (properties) => newComponent({
    text: properties.text,
    styles: {
        color: properties.color,
        fontSize: properties.fontSize ? properties.fontSize : "1em",
        fontFamily: properties.fontFamily ? properties.fontFamily : "Roboto",
        fontWeight: properties.fontWeight ? properties.fontWeight : "500",
    }
});

/**
 * Creates a material icon
 * @param {object} properties 
 * @returns 
 */
export const createMaterialIcon = (properties) => newComponent({
    type: 'i',
    text: properties.name,
    classes: ['material-icons'],
    styles: {
        fontSize: properties.size,
        color: properties.color
    }
});

/**
 * Creates a material icon
 * @param {object} properties 
 * @returns 
 */
 export const createOutlinedMaterialIcon = (properties) => newComponent({
    type: 'i',
    text: properties.name,
    classes: ['material-icons-outlined'],
    styles: {
        fontSize: properties.size ? properties.size : "24px",
        color: properties.color? properties.color : "#404040"
    }
});