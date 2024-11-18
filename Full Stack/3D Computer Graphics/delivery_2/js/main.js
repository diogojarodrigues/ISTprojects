import * as THREE from 'three';
// import { OrbitControls } from 'three/addons/controls/OrbitControls.js'; //move camera with mouse, uncomment this to move the camera with mouse (1/4)

import { scene, createScene, rotate, moveCar, moveHook, movePulley, handleCollision, checkCollision, finishAnimation, changeMaterial } from './scene.js';
import { camera, changeCamera, createCameras} from './camera.js';
import { onKeyDown, onKeyUp } from './controls.js';
import { FINISH_ANIMATION, NO_COLLISION } from './constants.js';

// var renderer, controls;      //move camera with mouse, uncomment this to move the camera with mouse (2/4)
var renderer;

function update(){
    'use strict';

    const colision = checkCollision();

    changeCamera()
    changeMaterial()

    if (colision != NO_COLLISION) {
        var animation = handleCollision(colision);
        if (animation == FINISH_ANIMATION){
            finishAnimation(colision);
        }
    }
    else{
        rotate();
        moveCar();
        moveHook();
        movePulley();
    }
    
}

function render() {
    'use strict';
    renderer.render(scene, camera);
}

function init() {
    'use strict';

    createScene();
    createCameras();

    // Set up renderer
    renderer = new THREE.WebGLRenderer({ antialias: true });
    renderer.setSize(window.innerWidth, window.innerHeight);
    // controls = new OrbitControls(camera, renderer.domElement);  //move camera with mouse, uncomment this to move the camera with mouse (3/4)

    document.body.appendChild(renderer.domElement);

    window.addEventListener("keydown", onKeyDown);
    window.addEventListener("keyup", onKeyUp);
}

function animate() {
    'use strict';
    // controls.update();          //move camera with mouse, uncomment this to move the camera with mouse (4/4)
    update();
    render();
    requestAnimationFrame(animate);
}

/* RUN */
init();
animate();
