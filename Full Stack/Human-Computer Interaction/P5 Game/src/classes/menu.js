class Menu {
    //put targets together will align targets until the end of the screen and then skip to the next row like a Word document
    constructor(numSections, listConstValues,rows, cols, distributionMode) {
        this.alreadyInitialized = false;
        this.isActive = false;
        this.sections = [];

        this.rows = rows;                        // número de linhas de menu (quantas linhas de retangulos(seccoes) existem)
        this.cols = cols;                        // número de colunas de menu (quantas colunas de retangulos(seccoes) existem)
        this.numSections = numSections;
        this.listConstValues = listConstValues;
        this.distributionMode = distributionMode;
    }

    init(width, height) {
        if (this.alreadyInitialized)
            return;
        else
            this.alreadyInitialized = true;

        let pos_x = margin_g;
        let pos_y = 100 + margin_g;

        let section;
        if (this.distributionMode == 0){
            for (let i=0; i<this.numSections; i++) {
                pos_x = margin_g + (i%this.cols) * width/2;                        // 20px é a margem da esquerda
                pos_y = 100 + margin_g + (Math.floor(i/this.cols)) * height/this.rows;     // 100px é a margem do topo

                section = new Section(this.listConstValues[i], pos_x, pos_y);
                this.sections.push(section);
                this.sections[i].init();
            }
        } else if (this.distributionMode == 1){
            let size;
            for (let i=0; i<this.listConstValues.length; i++) {

                if ( pos_x + this.listConstValues[i].labels.length*(target_size_g+ margin_g) >= width - margin_g){ // if next section passes over screen width

                    pos_y += target_size_g + margin_g*6.5;                                             // go to next row
                    pos_x = margin_g;
                }
                    size = this.listConstValues[i].labels.length*(target_size_g+ margin_g*5);

                section = new Section(this.listConstValues[i], pos_x, pos_y);
                this.sections.push(section);
                this.sections[i].init();
                pos_x += size;
            }
        }   

    }
    
    show() {
        this.isActive = true;
        this.sections.forEach(section => {
            section.show();
        });
    }

    hide() {
        this.isActive = false;
        this.sections.forEach(section => {
            section.hide();
        });
    }

    draw() {
        if (this.isActive == false)
            return;

        this.sections.forEach(section => {
            section.draw();
        });
    }

    clicked(mouse_x, mouse_y) {
        if (this.isActive == false)
            return 0;

        let val;
        for (let i=0; i<this.sections.length; i++) {
            
            val = this.sections[i].clicked(mouse_x, mouse_y);

            if (val != 0)
                return val;
        }
        return 0;
    }
}