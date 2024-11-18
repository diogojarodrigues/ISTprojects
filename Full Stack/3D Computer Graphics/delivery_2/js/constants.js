import * as THREE from 'three';

const STOPPED = 0;
const MOVING = 1;
const MOVING_BACK = 2;
const NO_COLLISION = -1;
const FINISH_ANIMATION = 3;

const orthographicCameraPositions = [
    new THREE.Vector3(0, 0, 35),   // Front view
    new THREE.Vector3(0, 35, 0),   // Top view
    new THREE.Vector3(35, 0, 0),   // Side view
];

const ortographicPlanes = [
    {left: -20, right: 20, top: 25, bottom: -5, near: 1, far: 1000},   //Front View
    {left: -20, right: 20, top: 15, bottom: -15, near: 1, far: 1000},   //Top View
    {left: -20, right: 20, top: 25, bottom: -5, near: 1, far: 1000},   //Side View
];

const otherCameraPositions = new THREE.Vector3(-30, 15, 25);

// CRANE DIMENTIONS

const base_dim = {x: 4, y: 2, z: 4, collision_radius: 7.681};  
const tower_dim = {x: 2, y: 14, z: 2}; 
const cabin_dim = {x: 2.5, y: 4, z: 2.5}; 
const jib_dim = {x: 13, y: 1.6, z: 1.6}; 
const floor_dim = {x: 200, y: 0.01, z: 200}; 
const car_dim = {x: 1, y: 0.3, z: 1}; 
const rope_dim = {radius: 0.1}; 
const hook_dim = {x: 0.4, y: 0.3, z: 0.4, collision_radius: 0.2}; 
const pulley_dim = {radius: 0.05, height:0.6}; 
const pl_dim = {radius: 1.6};
const peso_dim = {x:2, y: 2, z: 2};

// OTHER OBJECT DIMENSIONS

const container_pos = {x: -6.25,  y: -0.75, z:6.25};
const container_dim = {x: 6, y: 3, z: 3.5, collision_radius: 6};
const cubo_dim = {x:1, y:1, z:1, collision_radius:1};
const dodecaedro_dim = {radius: 0.4, collision_radius:0.6};
const icosaedro_dim = {radius: 0.7, collision_radius: 0.7};
const torus_dim = {radius:0.5, tube:0.4, collision_radius: 0.9};
const torusKnot_dim = {radius:0.4, tube:0.4, collision_radius: 1.1};


export { 
    orthographicCameraPositions, otherCameraPositions, ortographicPlanes, 
    STOPPED, MOVING, MOVING_BACK, NO_COLLISION, FINISH_ANIMATION, 
    base_dim, tower_dim, cabin_dim, jib_dim, floor_dim, 
    car_dim, rope_dim, hook_dim, pulley_dim, pl_dim, 
    container_dim, cubo_dim, dodecaedro_dim, icosaedro_dim, torusKnot_dim, torus_dim,
    peso_dim, container_pos
};