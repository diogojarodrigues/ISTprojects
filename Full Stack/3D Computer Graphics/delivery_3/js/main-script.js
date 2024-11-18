import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';
import { VRButton } from 'three/addons/webxr/VRButton.js';
import * as Stats from 'three/addons/libs/stats.module.js';
import { GUI } from 'three/addons/libs/lil-gui.module.min.js';
import { ParametricGeometry } from "three/addons/geometries/ParametricGeometry.js";

const MAX_PERFORMANCE = 3;
const NORMAL = 2;
const MAX_QUALITY = 1;
const performance = NORMAL;        // Set to true to disable lights and shadows of objects lights (spotlights), only for performance testing

//////////////////////
/* GLOBAL VARIABLES */
//////////////////////
const TOWER_DIM = {radius:1, height:5}

const MOVING_UP = 1;
const MOVING_DOWN = -1;
const RING_HEIGHT = 1;

const LIGHT_OFF = 0;
const MOBIUS_LIGHT_INTENSITY = 1;           
const MAIN_LIGHT_INTENSITY = 1;             
const OBJECT_LIGHT_INTENSITY = 2;

const texture = new THREE.TextureLoader().load('texture/oskar.jpg');
const mainLight = new THREE.DirectionalLight(0xffffff, MOBIUS_LIGHT_INTENSITY); 
const refCenter = new THREE.Object3D();
const refRings = new THREE.Object3D();

let objectsProperties = [
    {func: cone, size: 1/3.5, offset: -0.7, color: 0xff00ff},
    {func: mobius, size: 1/5.5, offset: -0.5, color: 0x00ff00},
    {func: plane, size: 1/2.5, offset: -0.5, color: 0x123456},
    {func: ellipsoid, size: 1/5, offset: -0.5, color: 0x345599},
    {func: torus, size: 1/6, offset: -0.5, color: 0x654321},
    {func: hyperboloid, size: 1/5, offset: -0.5, color: 0x123456},
    {func: cylinder, size: 1/3, offset: -0.5, color: 0x654321},
    {func: hyperbolicParaboloid, size: 1/5, offset: -0.5, color: 0xff5555},
];

let renderer, controls, scene;
let sphere;

let clock;

let isMobiusLightOn = true;
let isMainLightOn = true;
let isObjectLigthOn = true;

let isTextureOn = true;
let currentTexture = "lambert";
let previousTexture;

let rings = {
    inner : { size: [1, 2], color: 0x345599, height: 2, ref: new THREE.Object3D(), isMoving: false, moving: MOVING_UP },
    middle : { size: [2, 3.5], color: 0x00ff00, height: 1.66, ref: new THREE.Object3D(), isMoving: false, moving: MOVING_UP },
    outer : { size: [3.5, 5], color: 0xff00ff, height: 1.33, ref: new THREE.Object3D(), isMoving: false, moving: MOVING_UP },
}

let cameras = [];
let camera;
let meshs = []
let colors = [];
let objectsList = [];
let mobiusLights = [];
let objectLights = [];

// Functions to parametric geometry

function mobius(u, t, target) {
    u = u - 0.5;
    const v = 2 * Math.PI * t;
    const a = 2;
    const x = Math.cos(v) * (a + u * Math.cos(v / 2));
    const y = Math.sin(v) * (a + u * Math.cos(v / 2));
    const z = u * Math.sin(v / 2);
    target.set(x, y, z);
}

function cone(u, v, target) {
    const r = 1; // Radius of the base
    const h = 2; // Height of the cone
    u *= 2 * Math.PI; // u varies from 0 to 2π
    // Coordinates of the cone
    const x = r * (1 - v) * Math.cos(u);
    const y = r * (1 - v) * Math.sin(u);
    const z = h * v;
    target.set(x, y, z);
}

function ellipsoid(u, v, target) {
    u = 2 * Math.PI * u;
    v = Math.PI * v;
    const a = 1, b = 1.5, c = 2;
    const x = a * Math.sin(v) * Math.cos(u);
    const y = b * Math.sin(v) * Math.sin(u);
    const z = c * Math.cos(v);
    target.set(x, y, z);
}

function plane(u, v, target) {
    // Define the size of the plane
    const width = 2; // Width of the plane
    const height = 2; // Height of the plane

    // Scale u and v to the size of the plane
    const x = (u - 0.5) * width;
    const y = (v - 0.5) * height;
    const z = 0; // Plane lies on the z = 0 plane

    target.set(x, y, z);
}

function cylinder(u, v, target) {
    const radius = 1; // Radius of the cylinder
    const height = 2; // Height of the cylinder
    const angle = 2 * Math.PI * u; // Angle around the cylinder

    const x = radius * Math.cos(angle);
    const y = radius * Math.sin(angle);
    const z = height * (v - 0.5); // Center the cylinder around z = 0

    target.set(x, y, z);
}

function torus(u, v, target) {
    u *= 2 * Math.PI;
    v *= 2 * Math.PI;
    const R = 2, r = 0.5;
    const x = (R + r * Math.cos(v)) * Math.cos(u);
    const y = (R + r * Math.cos(v)) * Math.sin(u);
    const z = r * Math.sin(v);
    target.set(x, y, z);
}

function hyperboloid(u, v, target) {
    u *= 2 * Math.PI;
    v = v * 2 - 1;
    const a = 1, b = 1, c = 1;
    const x = a * Math.cosh(v) * Math.cos(u);
    const y = b * Math.cosh(v) * Math.sin(u);
    const z = c * Math.sinh(v);
    target.set(x, y, z);
}

function hyperbolicParaboloid(u, v, target) {
    u = u * 2 - 1;
    v = v * 2 - 1;
    const a = 1, b = 1;
    const x = u;
    const y = v;
    const z = (u * u / (a * a)) - (v * v / (b * b));false
    target.set(x, y, z);
}

/////////////////////
/* CREATE SCENE(S) */
/////////////////////
function createScene() {
    'use strict';
    scene = new THREE.Scene();
    scene.background = new THREE.Color(0x000000);

    scene.add(refCenter); // Father reference

    createTower();
    createFloor();
    createMobiusStrip();
    createDome();
    createRings();
}

////////////////////////
/* CREATE OBJECT3D(S) */
////////////////////////
function createTower() {
    const tower = new THREE.Object3D();
    const color = 0x80ffff
    const materials = new THREE.MeshLambertMaterial({ color: color  });
    const dimension = new THREE.CylinderGeometry(TOWER_DIM.radius, TOWER_DIM.radius, TOWER_DIM.height);
    const mesh = new THREE.Mesh(dimension, materials);
    if(performance == MAX_QUALITY) mesh.castShadow = true;
    mesh.receiveShadow = true;
    meshs.push(mesh);
    colors.push(color);  
    tower.position.y = TOWER_DIM.height / 2;

    tower.add(mesh);
    refCenter.add(tower);
}

function createFloor() {
    const floor = new THREE.Object3D();
    const color = 0x808080
    const materials = new THREE.MeshLambertMaterial({ color: color });
    const dimension = new THREE.BoxGeometry(1000, 0.05, 1000);
    const mesh = new THREE.Mesh(dimension, materials);
    mesh.receiveShadow = true;
    floor.add(mesh);

    meshs.push(mesh);
    colors.push(color);
    refCenter.add(floor);
}

function createMobiusStrip() {
   
    // Opçao1 para mobiusStrip
    const segments = 16;
    const widthSegments = 16;
    const numLights = 8;
    const aux = Math.floor(segments/numLights);
    
    const vertices = [];
    const indices = [];
    const lightPos = [];

    // Define the parametric function for the Möbius strip
    for (let i = 0; i <= segments; i++) {
        const u = i / segments * Math.PI * 2;
        for (let j = 0; j <= widthSegments; j++) {
            const v = j / widthSegments * 2 - 1;

            const x = (1 + (v / 2) * Math.cos(u / 2)) * Math.cos(u);
            const y = (1 + (v / 2) * Math.cos(u / 2)) * Math.sin(u);
            const z = (v / 2) * Math.sin(u / 2);

            vertices.push(x, y, z);
            if (i%aux==0 && j == widthSegments) {
                lightPos.push(x,y,z);
            }
        }
    }

    // Create the faces by defining indices
    for (let i = 0; i < segments; i++) {
        for (let j = 0; j < widthSegments; j++) {
            const a = i * (widthSegments + 1) + j;
            const b = (i + 1) * (widthSegments + 1) + j;
            const c = (i + 1) * (widthSegments + 1) + (j + 1);
            const d = i * (widthSegments + 1) + (j + 1);

            // Two triangles per segment
            indices.push(a, b, d);
            indices.push(b, c, d);
        }
    }

    // Create a BufferGeometry
    const geometry = new THREE.BufferGeometry();
    geometry.setAttribute('position', new THREE.Float32BufferAttribute(vertices, 3));
    geometry.setIndex(indices);
    geometry.computeVertexNormals();
    const color = 0xff3f3f;
    const material = new THREE.MeshLambertMaterial({ color: color, side: THREE.DoubleSide });
    const mesh = new THREE.Mesh(geometry, material);
    if (performance == MAX_QUALITY) mesh.castShadow = true;
    mesh.receiveShadow = true;

    mesh.scale.set(3,3,3);
    mesh.rotateX(1.5);
    mesh.position.set(0, TOWER_DIM.height +3, 0);
    
    for (let i = 1; i<=numLights; i++) {
        const point = new THREE.Vector3(lightPos[i*3], lightPos[i*3+1], lightPos[i*3+2]);
        const euler = new THREE.Euler(1.5, 0, 0, 'XYZ');
        point.applyEuler(euler);
        const light = createMobiusLight(point.x*3, point.y*3+ TOWER_DIM.height + 1.999, point.z*3)
        mobiusLights.push(light);
        refCenter.add(light);
    }

    meshs.push(mesh);
    colors.push(color);
    refCenter.add(mesh);
}

function createDome() {
    const sphereGeometry = new THREE.SphereGeometry(80, 124, 32, 0, Math.PI * 2, 0, Math.PI / 2);

    let sphereMaterial = new THREE.MeshLambertMaterial({ map: texture, side: THREE.BackSide });
    sphere = new THREE.Mesh(sphereGeometry, sphereMaterial);

    refCenter.add(sphere);
}

function createRings() {
    refCenter.add(refRings);
    Object.values(rings).forEach(ring => createRing(ring));
}

function createRing(ring) {
    const outerRing = new THREE.Shape();
    outerRing.absarc(0, 0, ring.size[1], 0, Math.PI * 2, false);

    const innerRing = new THREE.Path();
    innerRing.absarc(0, 0, ring.size[0], 0, Math.PI * 2, true);

    outerRing.holes.push(innerRing);

    const extrudeSettings = {
        depth: 1,
        bevelThickness: 0.001,
        bevelSize: 0,
    };

    const geometry = new THREE.ExtrudeGeometry( outerRing, extrudeSettings );
    const material = new THREE.MeshLambertMaterial( { color: ring.color } );
    const mesh = new THREE.Mesh( geometry, material ) ;

    if (performance == MAX_QUALITY) mesh.castShadow = true;
    mesh.receiveShadow = true;

    ring.ref.rotateX(Math.PI/2);
    ring.ref.position.y = ring.height;
    ring.ref.add(mesh);

    meshs.push(mesh);
    colors.push(ring.color);
    refRings.add(ring.ref);

    createObjects(ring, (ring.size[1] - ring.size[0])/2 + ring.size[0]);
}

function createObjects(ring, radius) {
    const numberOfObjects = objectsProperties.length;
    objectsProperties = objectsProperties.sort(() => Math.random() - 0.5);

    objectsProperties.forEach((o, index) => {

        // CREATE OBJECT
        const object = new THREE.Object3D();
        object.position.x = radius * Math.cos((index / numberOfObjects) * 2 * Math.PI);
        object.position.y = radius * Math.sin((index / numberOfObjects) * 2 * Math.PI);
        object.position.z = o.offset;

        // Create the object geometry and material
        const materials = new THREE.MeshLambertMaterial({ color: o.color, side: THREE.DoubleSide});
        const dimension = new ParametricGeometry(o.func, 8, 8);
        const mesh = new THREE.Mesh(dimension, materials);
        if (performance == MAX_QUALITY) mesh.castShadow = true;
        mesh.receiveShadow = true;
        
        // Set the size of the object
        const max = o.size                                  //Maximum size of the object
        const min = 0.2                                     //Minimum size of the object 
        const size = Math.random() * (max - min) + min;     //Random size of the object between min and max
        object.scale.set(size, size, size);
        object.add(mesh);

        // Set the direction of the object
        object.rotateX(2 * Math.PI * Math.random());
        object.rotateY(2 * Math.PI * Math.random());
        object.rotateZ(2 * Math.PI * Math.random());

        // Add the object to the ring
        meshs.push(mesh);
        colors.push(o.color);
        ring.ref.add(object);
        objectsList.push(object);

        if (performance == MAX_PERFORMANCE) return;
    
        // CREATE LIGHT
        const light = new THREE.SpotLight(0xffffff, OBJECT_LIGHT_INTENSITY);
        light.position.x = radius * Math.cos((index / objectsProperties.length) * 2 * Math.PI);
        light.position.y = radius * Math.sin((index / objectsProperties.length) * 2 * Math.PI);
        light.position.z = 0;
        light.angle = Math.PI / 4;
        light.target = object;  
    
        ring.ref.add(light)
        objectLights.push(light);
    });
}

//////////////////////
/* CREATE CAMERA(S) */
//////////////////////
function createCameras() {
    let aux = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 1, 1000);
    aux.position.set(-10, 5, -10);
    aux.lookAt(0, 0, 0);
    cameras.push(aux);

    const stereoCamera = new THREE.StereoCamera();
    stereoCamera.aspect = 0.5;
    cameras.push(stereoCamera);
    
    camera = cameras[0];
}

/////////////////////
/* CREATE LIGHT(S) */
/////////////////////
function createMainLight() {
    mainLight.position.set(10, 20, 10);
    mainLight.castShadow = true;

    // Configure shadow properties
    mainLight.shadow.mapSize.width = 4096;
    mainLight.shadow.mapSize.height = 4096;
    mainLight.shadow.camera.near = 0.5;
    mainLight.shadow.camera.far = 500;
    mainLight.shadow.camera.left = -50;
    mainLight.shadow.camera.right = 50;
    mainLight.shadow.camera.top = 50;
    mainLight.shadow.camera.bottom = -50;
    mainLight.shadow.bias = -0.001;

    scene.add(mainLight);

    const light = new THREE.AmbientLight( 0xFAC898 ); // soft orange light
    light.intensity = 0.3;
    scene.add(light);
}

function createMobiusLight(x, y, z) {
    const light = new THREE.PointLight(0xffffff, 1, 100);
    light.position.set(x, y, z);
    light.castShadow = true;
    return light;
}

function updateMainLight() {
    mainLight.intensity = isMainLightOn ? MAIN_LIGHT_INTENSITY : LIGHT_OFF;
}

function updateObjectsLight() {
    const intensity = isObjectLigthOn ? OBJECT_LIGHT_INTENSITY : LIGHT_OFF;
    objectLights.forEach(light => light.intensity = intensity);
}

function updateMobiusLight() {
    const intensity = isMobiusLightOn ? MOBIUS_LIGHT_INTENSITY : LIGHT_OFF;
    mobiusLights.forEach(light => light.intensity = intensity);
}

////////////
/* MOVEMENTS */
////////////
function carouselRotation(deltaTime) {
    'use strict';
    refRings.rotation.y += 0.5 * deltaTime;
}

function ringMovement(deltaTime) {
    Object.values(rings).forEach(ring => {
        if (ring.isMoving) {
            if (ring.ref.position.y > TOWER_DIM.height || ring.moving == MOVING_DOWN) {
                ring.moving = MOVING_DOWN;
            } 
            if (ring.ref.position.y < RING_HEIGHT || ring.moving == MOVING_UP) {
                ring.moving = MOVING_UP;
            }
            ring.ref.position.y += ring.moving * deltaTime;
        }
    });
}

function objectRotations(deltaTime) {
    'use strict';
    Object.values(objectsList).forEach(obj => {
        obj.rotation.z += deltaTime * 2.5;
    });
}

////////////
/* UPDATE */
////////////
function update() {
    'use strict';
    const deltaTime = clock.getDelta();

    updateTexture();
    updateMainLight();
    updateObjectsLight();
    updateMobiusLight();

    controls.update();
    ringMovement(deltaTime);
    objectRotations(deltaTime);
    carouselRotation(deltaTime);
}

/////////////
/* TEXTURE */
/////////////
function updateTexture() {

    // Change Dome texture
    switch (currentTexture) {
        case "lambert":
            sphere.material = new THREE.MeshLambertMaterial({ map: texture, side: THREE.BackSide });
            break;
        case "phong":
            sphere.material = new THREE.MeshPhongMaterial({ map: texture, side: THREE.BackSide });
            break;
        case "cartoon":
            sphere.material = new THREE.MeshToonMaterial({ map: texture, side: THREE.BackSide });
            break;
        case "normal":
            sphere.material = new THREE.MeshNormalMaterial({ side: THREE.BackSide });
            break;
        case "basic":
            sphere.material = new THREE.MeshBasicMaterial({ map: texture, side: THREE.BackSide });
            break;
    }

    // Change other objects textures
    for (let i = 0; i < meshs.length; i++) {
        switch (currentTexture) {
            case "lambert":
                meshs[i].material = new THREE.MeshLambertMaterial({ color: colors[i], side: THREE.DoubleSide});
                break;
            case "phong":
                meshs[i].material = new THREE.MeshPhongMaterial({ color: colors[i], side: THREE.DoubleSide});
                break;
            case "cartoon":
                meshs[i].material = new THREE.MeshToonMaterial({ color: colors[i], side: THREE.DoubleSide});
                break;
            case "normal":
                meshs[i].material = new THREE.MeshNormalMaterial({ side: THREE.DoubleSide});
                break;
            case "basic":
                meshs[i].material = new THREE.MeshBasicMaterial({ color: colors[i], side: THREE.DoubleSide});
                break;
        }
    }
}

function changeTexture(type) {

    if (type == "basic") {
        isTextureOn = !isTextureOn;

        if (!isTextureOn) {
            previousTexture = currentTexture;
            currentTexture = type;
        } else {
            currentTexture = previousTexture;
        }

        return;
    }

    if (!isTextureOn) return;

    previousTexture = currentTexture;
    currentTexture = type;
}

/////////////
/* DISPLAY */
/////////////
function render() {
    'use strict';
    renderer.render(scene, camera);
}

////////////////////////////////
/* INITIALIZE ANIMATION CYCLE */
////////////////////////////////
function init() {
    'use strict';
    scene = new THREE.Scene();
    createScene();
    createCameras();
    createMainLight();

    clock = new THREE.Clock();

    renderer = new THREE.WebGLRenderer({ antialias: true });
    renderer.setSize(window.innerWidth, window.innerHeight);
    renderer.setPixelRatio(window.devicePixelRatio);
    renderer.shadowMap.enabled = true;
    renderer.shadowMap.type = THREE.PCFSoftShadowMap;
    renderer.xr.enabled = true;
    
    document.body.appendChild(renderer.domElement);
    document.body.appendChild( VRButton.createButton( renderer ) );

    // ORBIT CONTROLS
    controls = new OrbitControls(camera, renderer.domElement);
    controls.enableDamping = true;
    controls.dampingFactor = 0.25;
    controls.screenSpacePanning = false;

    window.addEventListener('resize', onResize, false);
    window.addEventListener('keydown', onKeyDown, false);
}

/////////////////////
/* ANIMATION CYCLE */
/////////////////////
function animate() {
    'use strict';
    update();
    render();
    renderer.setAnimationLoop(animate);
}

////////////////////////////
/* RESIZE WINDOW CALLBACK */
////////////////////////////
function onResize() {
    'use strict';
    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();
    renderer.setSize(window.innerWidth, window.innerHeight);
}

///////////////////////
/* KEY DOWN CALLBACK */
///////////////////////
function onKeyDown(e) {
    'use strict';
    if(e.key == '1') { rings.inner.isMoving = !rings.inner.isMoving; }
    if(e.key == '2') { rings.middle.isMoving = !rings.middle.isMoving; }
    if(e.key == '3') { rings.outer.isMoving = !rings.outer.isMoving; }

    if(e.key == 'q' || e.key == 'Q') { changeTexture("lambert"); }                           
    if(e.key == 'w' || e.key == 'W') { changeTexture("phong"); }
    if(e.key == 'e' || e.key == 'E') { changeTexture("cartoon") }
    if(e.key == 'r' || e.key == 'R') { changeTexture("normal"); }
    if(e.key == 't' || e.key == 'T') { changeTexture("basic"); }

    if(e.key == 'p' || e.key == 'P') { isMobiusLightOn = !isMobiusLightOn; }
    if(e.key == 'd' || e.key == 'D') { isMainLightOn = !isMainLightOn; }
    if(e.key == 's' || e.key == 'S') { isObjectLigthOn = !isObjectLigthOn; }
}

init();
animate();
