const NUM_OF_TRIALS       = 12;     // The numbers of trials (i.e., target selections) to be completed

//Fruits Section
const fruitsA = {
    name: "A",
    color: [150, 150, 150],
    blackLabel: false,
    numRows: 1,
    numCols: 2,
    labels: [20, 5]
}

const fruitsB = {
    name: "B",
    color: [150, 150, 150],
    blackLabel: false,
    numRows: 1,
    numCols: 1,
    labels: [6]
}

const fruitsC = {
    name: "C",
    color: [150, 150, 150],
    blackLabel: false,
    numRows: 1,
    numCols: 2,
    labels: [11, 21]
}

const fruitsG = {
    name: "G",
    color: [150, 150, 150],
    blackLabel: false,
    numRows: 1,
    numCols: 3,
    labels: [12, 1, 0]
}

const fruitsK = {
    name: "K",
    color: [150, 150, 150],
    blackLabel: false,
    numRows: 1,
    numCols: 2,
    labels: [22, 7]
}

const fruitsL = {
    name: "L",
    color: [150, 150, 150],
    blackLabel: false,
    numRows: 1,
    numCols: 2,
    labels: [8, 9]
}

const fruitsM = {
    name: "M",
    color: [150, 150, 150],
    blackLabel: false,
    numRows: 1,
    numCols: 2,
    labels: [10, 13]
}

const fruitsN = {
    name: "N",
    color: [150, 150, 150],
    blackLabel: false,
    numRows: 1,
    numCols: 1,
    labels: [15]
}

const fruitsO = {
    name: "O",
    color: [150, 150, 150],
    blackLabel: false,
    numRows: 1,
    numCols: 1,
    labels: [16]
}

const fruitsP = {
    name: "P",
    color: [150, 150, 150],
    blackLabel: false,
    numRows: 1,
    numCols: 7,
    labels: [17,18,19,23,2,24,25]
}

const fruitsR = {
    name: "R",
    color: [150, 150, 150],
    blackLabel: false,
    numRows: 1,
    numCols: 3,
    labels: [3,26,4]
}

const fruitsS = {
    name: "S",
    color: [150, 150, 150],
    blackLabel: false,
    numRows: 1,
    numCols: 1,
    labels: [27]
}

const fruitsW = {
    name: "W",
    color: [150, 150, 150],
    blackLabel: false,
    numRows: 1,
    numCols: 1,
    labels: [14]
}

const lacts = {
    name: "Milks",
    color: [250, 250, 250],
    blackLabel: true,
    numRows: 1,
    numCols: 10,
    labels: [38,37,42,41,50,39,44,47,51,40]
}

const yogurts = {
    name: "Yoghurts",
    color: [255, 236, 195],
    blackLabel: true,
    numRows: 1,
    numCols: 9,
    labels: [53,48,52,55,43,56,49,57,54]
}

const juices = {
    name: "Juices",
    color: [255,167,0],
    blackLabel: true,
    numRows: 1,
    numCols: 8,
    labels: [28,34,33,36,31,29,32,30]
};

const creams = {
    name: "Cream / Smoothie",
    color: [255, 100, 208],
    blackLabel: true,
    numRows: 1,
    numCols: 3,
    labels: [45,46, 35]
};

const vegetsA = {
    name: "A",
    color: [29, 97, 47],
    blackLabel: false,
    numRows: 1,
    numCols: 2,
    labels: [58, 59]
}

const vegetsB = {
    name: "B",
    color: [29, 97, 47],
    blackLabel: false,
    numRows: 1,
    numCols: 2,
    labels: [76, 68]
}

const vegetsC = {
    name: "C",
    color: [29, 97, 47],
    blackLabel: false,
    numRows: 1,
    numCols:3,
    labels: [60, 61, 62]
}

const vegetsG = {
    name: "G",
    color: [29, 97, 47],
    blackLabel: false,
    numRows: 1,
    numCols: 2,
    labels: [63, 64]
}

const vegetsL = {
    name: "L",
    color: [29, 97, 47],
    blackLabel: false,
    numRows: 1,
    numCols: 1,
    labels: [65]
}

const vegetsM = {
    name: "M",
    color: [29, 97, 47],
    blackLabel: false,
    numRows: 1,
    numCols: 2,
    labels: [71, 66]
}

const vegetsP = {
    name: "P",
    color: [29, 97, 47],
    blackLabel: false,
    numRows: 1,
    numCols: 1,
    labels: [70]
}

const vegetsR = {
    name: "R",
    color: [29, 97, 47],
    blackLabel: false,
    numRows: 1,
    numCols: 3,
    labels: [75, 73, 69]
}

const vegetsS = {
    name: "S",
    color: [29, 97, 47],
    blackLabel: false,
    numRows: 1,
    numCols: 1,
    labels: [74]
}

const vegetsT = {
    name: "T",
    color: [29, 97, 47],
    blackLabel: false,
    numRows: 1,
    numCols: 1,
    labels: [77]
}

const vegetsV = {
    name: "V",
    color: [29, 97, 47],
    blackLabel: false,
    numRows: 1,
    numCols: 1,
    labels: [78]
}

const vegetsY = {
    name: "Y",
    color: [29, 97, 47],
    blackLabel: false,
    numRows: 1,
    numCols: 1,
    labels: [67]
}

const vegetsW = {
    name: "W",
    color: [29, 97, 47],
    blackLabel: false,
    numRows: 1,
    numCols: 1,
    labels: [72]
}

const vegetsZ = {
    name: "Z",
    color: [29, 97, 47],
    blackLabel: false,
    numRows: 1,
    numCols: 1,
    labels: [79]
}


//--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

// Pixel density and setup variables (DO NOT CHANGE!)
let PPI, PPCM;
let legendas;                       // The item list from the "legendas" CSV
let index_image 		  = [];     // The image that contains all the items

// Metrics
let testStartTime, testEndTime;     // time between the start and end of one attempt (8 trials)
let hits 			      = 0;      // number of successful selections
let misses 			      = 0;      // number of missed selections (used to calculate accuracy)
let database;                       // Firebase DB  

// Study control parameters
let draw_targets          = false;  // used to control what to show in draw()
let trials;                         // contains the order of targets that activate in the test
let current_trial         = 0;      // the current trial number (indexes into trials array above)
let attempt               = 0;      // users complete each test twice to account for practice (attemps 0 and 1)



//O que é que é isto?
//let setup 				  = true;

let targetsArray               = [];

//Buttons
let buttonFruits;
let buttonLiquids;
let buttonVegetables;
let continue_button;

//Menus
let menuFruits = new Menu(13, [fruitsA, fruitsB, fruitsC, fruitsG, fruitsK,
                    fruitsL, fruitsM, fruitsN, fruitsO, fruitsP, fruitsR,
                    fruitsS, fruitsW], 7, 2, 1);
let menuLiquids = new Menu(4, [lacts, yogurts, juices, creams], 2, 2, 1);
let menuVegetables = new Menu(13, [vegetsA, vegetsB, vegetsC, vegetsG, vegetsL, vegetsM,
                    vegetsP, vegetsR, vegetsS, vegetsT, vegetsV, vegetsW, vegetsY, vegetsZ], 2, 1, 1);

//Sizes (DONT CHANGE, OR YOUR PC WILL EXPLODE)
let target_size_g = 2.5;
//let target_size_g = 1/14;
let margin_g; //podem mexer no denominador (quanto maior denominador = menos margem)
let screen_width_g;
let screen_height_g;
let space_title = 30;  //tambem podem mexer neste valor