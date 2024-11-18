import * as THREE from 'three';

import { orthographicCameraPositions, otherCameraPositions, ortographicPlanes} from './constants.js';
import { hook } from './scene.js';

var cameras = []; 
var camera_index = 0;
var camera;

function changeCameraState(index) {
    'use strict'
    
    if (camera_index < 0 || camera_index >= cameras.length ) {
        console.error("Invalid camera index!");
        return;
    }

    camera_index = index
}

function createCameras() {
    'use strict';
    var aux;
    for (let i = 0; i < orthographicCameraPositions.length; i++) {
        aux = new THREE.OrthographicCamera(-20, 20, 20, -20, 1, 1000);
        aux.position.copy(orthographicCameraPositions[i]); 

        updateCameraView(aux, i);

        aux.lookAt(0, 0, 0); 
        cameras.push(aux);
    }

    aux = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 1, 1000);
    aux.position.copy(otherCameraPositions); 
    aux.lookAt(0, 0, 0); 
    cameras.push(aux);

    aux  = new THREE.OrthographicCamera(-25, 25, 25, -8, 1, 1000);
    aux.position.copy(otherCameraPositions); 
    aux.lookAt(0, 0, 0); 

    cameras.push(aux);

    const cam_6 = new THREE.PerspectiveCamera(90, window.innerWidth / (window.innerHeight*1.3), 0.05, 1000);
    cam_6.position.set(hook.position.x, hook.position.y-0.1 , hook.position.z);
    cam_6.lookAt(0, -100, 0); 
    hook.add(cam_6);
    cameras.push(cam_6);

    camera = cameras[0];
    const storedIndex = parseInt(localStorage.getItem('cameraPosition'));;
    if (!isNaN(storedIndex) && storedIndex < cameras.length && storedIndex >= 0) {
        camera = cameras[storedIndex];
    } else {
        camera = cameras[0]; // Set default camera if no valid index is found
    }
    
}

function updateCameraView(aux, i) {
    aux.left = ortographicPlanes[i].left;
    aux.right = ortographicPlanes[i].right;
    aux.top = ortographicPlanes[i].top;
    aux.bottom = ortographicPlanes[i].bottom;
    aux.near = ortographicPlanes[i].near;
    aux.far = ortographicPlanes[i].far;
    aux.updateProjectionMatrix();
}

function changeCamera() {
    'use strict';

    camera = cameras[camera_index];
    localStorage.setItem('cameraPosition', JSON.stringify(camera_index));
}

export { camera, createCameras, changeCamera, changeCameraState };
