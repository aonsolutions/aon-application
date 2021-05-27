export class Swipe {
    constructor(elements) {
        this.mouseOrigin = null;
        this.isSwiping = false;
        this.swipeMargin = 20;
        this.elements = elements;
        [...this.elements].map(element=>{
          this.onEventStart(element);
          this.onEventEnd(element);
          this.onEventMove(element);
        });
    }

    onEventStart(element){
        element.addEventListener('mousedown', (ev)=> this.startSwipe(ev)); 
        element.addEventListener('touchstart', (ev)=> this.startSwipe(ev));
    }

    onEventEnd(element){
        element.addEventListener('mouseup',  (ev)=> this.endSwipe(ev));
        element.addEventListener('touchend',  (ev)=> this.endSwipe(ev));
    }

    onEventMove(element){
        element.addEventListener('mousemove',  (ev)=> this.detectMouse(ev));
        element.addEventListener('touchmove',  (ev)=> this.detectMouse(ev));
    }

    startSwipe(evt){ 
        this.mouseOrigin = evt.screenX || evt.touches[0].clientX;
        this.isSwiping = true;
    }

    endSwipe({currentTarget}){
        if(currentTarget){
            if(currentTarget.classList.contains("deleting") ){
                currentTarget.remove();      
                this.onDelete(currentTarget);
            }      
            this.mouseOrigin = null;
            this.isSwiping = false;     
            currentTarget.style.margin = 0;
            currentTarget = null;

        }
    }

    onDelete(callback){
        this.onDelete = callback;
        return this;
    }

    detectMouse(evt){
        let currentMousePosition = evt.screenX ||  evt.touches[0].clientX;
        let swipeDifference = Math.abs(this.mouseOrigin - currentMousePosition)
        if(this.isSwiping && evt.currentTarget && (swipeDifference > this.swipeMargin) ){ 
            if( (swipeDifference-this.swipeMargin) <= this.swipeMargin ){
                //no change, allows user to take no action
                evt.currentTarget.classList.remove("deleting");
                evt.currentTarget.style.margin = 0;
            } else if( this.mouseOrigin > currentMousePosition ){
                evt.currentTarget.classList.add("deleting");
                evt.currentTarget.style.marginLeft = -swipeDifference+"px";
            }
        }
    }  
}