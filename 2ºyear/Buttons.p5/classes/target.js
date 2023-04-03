// Target class (position and radius)
class Target
{
	constructor(x, y, id, label, color, blackLabel) {
		this.x = x;
		this.y = y;
		this.id = id;
		this.label = label;
		this.color = color;
		this.fix_x = x;
		this.fix_y = y;
		this.radius  = target_size_g;
		this.blackLabel  =blackLabel;
	}

	//switches position with another target NOT WORKING 
	switchPositions(other){
		let auxx1 = x;
		let auxy1 = y;
		this.x = other.x;
		this.y = other.y;
		other.x = auxx1;
		other.y = auxy1;
	}
	
	hide() {
		this.x = -100;
		this.y = -100;
	}

	show() {
		this.x = this.fix_x;
		this.y = this.fix_y;
	}
	// Checks if a mouse click took place
	// within the target
	clicked(mouse_x, mouse_y) {
		if (dist(this.x, this.y, mouse_x, mouse_y) < this.radius / 2) {
			this.popRadius();
			if (this.id === trials[current_trial])
				return 1;
			return -1;
		}
		return 0;
	}
	
	// Draws the target (i.e., a circle)
	// and its label
	draw(color, blackLabel) {
		// Draw target
		
		if (index_image[this.id])
			image(index_image[this.id], this.x-this.radius/2, this.y-this.radius/2, this.radius, this.radius)
		else {
			fill(color[0], color[1], color[2]);		         
			circle(this.x, this.y, this.radius);
		}
			
		// Draw label
		if (blackLabel)
			fill(0,0,0);
		else
			fill(255,255,255);

		textFont("Arial", 18);
		textAlign(CENTER);
		text(this.label, this.x, this.y);
	}

	// Pops the target so that the user knows it was clicked
	popRadius() {
		this.radius *= 1.1;
		setTimeout(this.depop.bind(this), 75); // Hold for 75ms
	  }
	  depop() {
		this.radius = parseFloat(this.radius) / 1.1;
	  }
}
