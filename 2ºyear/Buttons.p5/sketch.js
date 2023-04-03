// Bakeoff #2 -- Seleção em Interfaces Densas
// IPM 2022-23, Período 3
// Entrega: até dia 31 de Março às 23h59 através do Fenix
// Bake-off: durante os laboratórios da semana de 10 de Abril

// p5.js reference: https://p5js.org/reference/

// Database (CHANGE THESE!)
const GROUP_NUMBER        = 36;      // Add your group number here as an integer (e.g., 2, 3)
const RECORD_TO_FIREBASE  = true;  	 // Set to 'true' to record user results to Firebase


// Ensures important data is loaded before the program starts
function preload() {
	legendas = loadTable('legendas.csv', 'csv', 'header');
 
	
	for (let i = 0; i <= 27 ; i++) {
  		index_image[i] = loadImage(`./assets/${i}.png`);
	}

	for (let i = 58; i <= 79; i++) {
		index_image[i] = loadImage(`./assets/${i}.png`);
	}
	
}

// Runs once at the start
function setup() {
	createCanvas(700, 500);    // window size in px before we go into fullScreen()
	frameRate(60);             // frame rate (DO NOT CHANGE!)
	
	randomizeTrials();         // randomize the trial order at the start of execution
	drawUserIDScreen();        // draws the user start-up screen (student ID and display size)
}

function drawButton(label, mode, x_position) {
	let button_reference = createButton(label);
	button_reference.mouseReleased(() => changeMenus(mode));
	button_reference.position(x_position, 0);
	button_reference.size(width/3, 100);
	button_reference.style('font-size', '40px');
	
	return button_reference;
}

function drawAttemps() {
	textFont("Arial", 16);
	fill(color(255,255,255));
	textAlign(LEFT);
	text("Trial " + (current_trial + 1) + " of " + trials.length, width - 100, height-20);
}

function drawLabel() {
	// Não podemos mexer nesta função (contra as regras do jogo), antes não estava numa função
	textFont("Arial", 20);
	fill(color(255,255,255));
	textAlign(CENTER);
	text(legendas.getString(trials[current_trial],0), width/2, height - 20);
}

function drawTargets(){
	targetsArray.forEach(target => {
		target.draw(target.color, target.blackLabel);
	})
}

// Runs every frame and redraws the screen
function draw()
{
	if (draw_targets && attempt < 2)
	{     
		// The user is interacting with the 6x3 target grid
		background(color(0,0,0));        // sets background to black
		
		menuFruits.draw()
		menuLiquids.draw()
		menuVegetables.draw()

		drawTargets();

		drawLabel()
		drawAttemps()

		buttonFruits = drawButton('FRUITS', "fruta", 0)
		buttonLiquids = drawButton('LIQUIDS', "liquidos", width/3)
		buttonVegetables = drawButton('VEGETABLES', "vegetais", 2*width/3)
	}
}

// Mouse button was pressed - lets test to see if hit was in the correct target
function mousePressed() 
{
	// Only look for mouse releases during the actual test
	// (i.e., during target selections)
	if (draw_targets) {
			
		/*let val = menuFruits.clicked(mouseX, mouseY) + menuLiquids.clicked(mouseX, mouseY) + menuVegetables.clicked(mouseX, mouseY);
		if (val) {
			if (val === 1)
				hits++;
			else if (val === -1)
				misses++;
			current_trial++;              // Move on to the next trial/target
		}*/

		for (var i = 0; i < legendas.getRowCount(); i++){
			// Check if the user clicked over one of the targets
			if (targetsArray[i].clicked(mouseX, mouseY)) 
			{
				// Checks if it was the correct target
				if (targetsArray[i].id === trials[current_trial]) hits++;
				else misses++;
				
				current_trial++;              // Move on to the next trial/target
				break;
			}
		}
    
			
		// Check if the user has completed all trials
		if (current_trial === NUM_OF_TRIALS)
		{
			testEndTime = millis();
			draw_targets = false;          // Stop showing targets and the user performance results
			printAndSavePerformance();     // Print the user's results on-screen and send these to the DB
			attempt++;                      
			
			// If there's an attempt to go create a button to start this
			if (attempt < 2)
			{
				continue_button = createButton('START 2ND ATTEMPT');
				continue_button.mouseReleased(continueTest);
				continue_button.position(width/2 - continue_button.size().width/2, height/2 - continue_button.size().height/2);
			}
		}
		// Check if this was the first selection in an attempt
		else if (current_trial === 1) testStartTime = millis(); 
	}
}


function changeMenus(mode) {
	menuFruits.hide();
	menuLiquids.hide();
	menuVegetables.hide();

	switch (mode) {
		case "liquidos":
			menuFruits.hide();
			menuVegetables.hide();
			menuLiquids.show();
			break;
		case "vegetais":
			menuFruits.hide();
			menuLiquids.hide();
			menuVegetables.show();
			break;
		default:
			menuLiquids.hide();
			menuVegetables.hide();
			menuFruits.show();
			break;
	}

}

// Creates and positions the UI targets
function createTargets()
{
	menuFruits.init(width, height);
	menuLiquids.init(width, height);
	menuVegetables.init(width, height);


	changeMenus("fruta");
}

// Print and save results at the end of 54 trials
function printAndSavePerformance()
{
	// DO NOT CHANGE THESE!
	//Alturas estão todas com mais 100px para baixo
	let accuracy			= parseFloat(hits * 100) / parseFloat(hits + misses);
	let test_time         = (testEndTime - testStartTime) / 1000;
	let time_per_target   = nf((test_time) / parseFloat(hits + misses), 0, 3);
	let penalty           = constrain((((parseFloat(95) - (parseFloat(hits * 100) / parseFloat(hits + misses))) * 0.2)), 0, 100);
	let target_w_penalty	= nf(((test_time) / parseFloat(hits + misses) + penalty), 0, 3);
	let timestamp         = day() + "/" + month() + "/" + year() + "  " + hour() + ":" + minute() + ":" + second();
	
	textFont("Arial", 18);
	background(color(0,0,0));   // clears screen
	fill(color(255,255,255));   // set text fill color to white
	textAlign(LEFT);
	text(timestamp, 10, 120);    // display time on screen (top-left corner)
	
	textAlign(CENTER);
	text("Attempt " + (attempt + 1) + " out of 2 completed!", width/2, 160); 
	text("Hits: " + hits, width/2, 200);
	text("Misses: " + misses, width/2, 220);
	text("Accuracy: " + accuracy + "%", width/2, 240);
	text("Total time taken: " + test_time + "s", width/2, 260);
	text("Average time per target: " + time_per_target + "s", width/2, 280);
	text("Average time for each target (+ penalty): " + target_w_penalty + "s", width/2, 320);

	// Saves results (DO NOT CHANGE!)
	let attempt_data = 
	{
				project_from:       GROUP_NUMBER,
				assessed_by:        student_ID,
				test_completed_by:  timestamp,
				attempt:            attempt,
				hits:               hits,
				misses:             misses,
				accuracy:           accuracy,
				attempt_duration:   test_time,
				time_per_target:    time_per_target,
				target_w_penalty:   target_w_penalty,
	}
	
	// Send data to DB (DO NOT CHANGE!)
	if (RECORD_TO_FIREBASE)
	{
		// Access the Firebase DB
		if (attempt === 0)
		{
			firebase.initializeApp(firebaseConfig);
			database = firebase.database();
		}
		
		// Add user performance results
		let db_ref = database.ref('G' + GROUP_NUMBER);
		db_ref.push(attempt_data);
	}
}


// Is invoked when the canvas is resized (e.g., when we go fullscreen)
function windowResized() 
{    
	if (fullscreen())
	{
		// DO NOT CHANGE THESE!
		resizeCanvas(windowWidth, windowHeight);
		let display        = new Display({ diagonal: display_size }, window.screen);
		PPI                = display.ppi;                      // calculates pixels per inch
		PPCM               = PPI / 2.54;                       // calculates pixels per cm

		screen_width_g   = width;             // screen width
		screen_height_g  = height;            // screen height
		
		target_size_g *= PPCM;	  // target size in pixels
		margin_g = target_size_g/10;          // margin in pixels

		//createTargets(target_size * PPCM, horizontal_gap * PPCM - 80, vertical_gap * PPCM - 80);
		createTargets()
		draw_targets = true;

	}
}


// Evoked after the user starts its second (and last) attempt
function continueTest()
{
	// Re-randomize the trial order
	randomizeTrials();
	//createTargets(target_size * PPCM, horizontal_gap * PPCM - 80, vertical_gap * PPCM - 80);
	createTargets()


	// Resets performance variables
	hits = 0;
	misses = 0;
	
	current_trial = 0;
	continue_button.remove();
	
	// Shows the targets again
	draw_targets = true; 
}
