class Section {

    constructor({name, color, blackLabel, numRows, numCols, labels}, section_pos_x, section_pos_y) {
        this.targets = [];

        this.name = name;
        this.color = color;
        this.blackLabel = blackLabel;
        this.numCols = numCols;
        this.numRows = numRows;
        this.labels = labels;
        this.section_pos_x = section_pos_x;
        this.section_pos_y = section_pos_y;

    }

    init() {
        let target_x;
        let target_y;
        let target_l;
        let target_id;
        let target;
    
        let i = 0;
        for (var r = 0; r < this.numRows; r++)
        {
            for (var c = 0; c < this.numCols; c++)
            {
                //      posicao base da seccao + margem a esquerda + (margem + alvo) * numero de alvos
                target_x = this.section_pos_x + margin_g + (margin_g + target_size_g) * c + target_size_g/2;        // give it some margin from the left border
                target_y = this.section_pos_y + margin_g + (margin_g + target_size_g) * r + target_size_g/2 + space_title;
;   // O quarenta é o espaço para o títtulo da seccao

                target_l =  legendas.getString(this.labels[i], 0);
                target_id = legendas.getNum(this.labels[i], 1);
                target = new Target(target_x, target_y, target_id, target_l, this.color, this.blackLabel);
                this.targets.push(target);                  // saves each object in the array
                targetsArray.push(target);

                if (++i >= this.labels.length)
                    return;
            }  
        }
    }

    show() {
        this.targets.forEach(target => {
            target.show();
        });
    }

    hide() {
        this.targets.forEach(target => {
            target.hide();
        });
    }

    draw() {
         
        //Retangulo à volta da seccao
        noFill();
        stroke(255,255,255)
        strokeWeight(5);

        
        rect(
            this.section_pos_x,
            this.section_pos_y + space_title,
            margin_g*(this.numCols+1) + target_size_g * this.numCols,
            margin_g*(this.numRows+1) + target_size_g * this.numRows,
        )
        noStroke();

        //Título da seccao
        textFont("Arial", Math.floor(space_title * 1.3));
        fill(color(255,255,255));
        textAlign(LEFT);
        text(this.name, this.section_pos_x, this.section_pos_y + Math.floor(space_title*0.6));



        //Desenha os alvos
        /*this.targets.forEach(target => {
            target.draw(this.color, this.blackLabel);
        });*/
    }

    clicked(mouse_x, mouse_y) {
        let val;
        for (let i=0; i<this.targets.length; i++) {
            
            val = this.targets[i].clicked(mouse_x, mouse_y);

            if (val != 0)
                return val;
        }
        return 0;
    }
}