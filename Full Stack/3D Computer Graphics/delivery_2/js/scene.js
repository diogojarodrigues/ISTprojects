import * as THREE from 'three';

import { base_dim, tower_dim, cabin_dim, jib_dim, floor_dim, car_dim, pl_dim, peso_dim,
     rope_dim, hook_dim, pulley_dim, container_dim, cubo_dim, dodecaedro_dim, icosaedro_dim, 
     torus_dim, torusKnot_dim, STOPPED, MOVING, MOVING_BACK, NO_COLLISION, FINISH_ANIMATION, container_pos} from './constants.js';

var scene;

var wireframe = localStorage.getItem('wireframe') === 'true' ? true : false;

// ##################### CONTROL MOVEMENTS #####################

var cabin;
var car;
var rope;
var hook;
var ratio = 4;  //Serve para escolher racio etre pl tras e frente

const center = new THREE.Object3D();
var son;
var grandson;
var ggson;

var car_move = 8;
var hook_size = 2;
var default_hook_size = hook_size;
var pulley_radius = 0;
var hook_velocity = 0.04;
var rotation_speed = 0.5;

var isRotating = STOPPED;
var isCarMoving = STOPPED;
var isHookMoving = STOPPED;
var isPulleyMoving = STOPPED;

function changeRotationState(mode) { isRotating = mode; }
function changeCarState(mode) { isCarMoving = mode; }
function changeHookState(mode) { isHookMoving = mode; }
function changePulleyState(mode) { isPulleyMoving = mode; }
function changeMaterialState() { wireframe = !wireframe; }

let pulleys = [];
let boundaries = [];
let objects = [];
let allMaterials = [];

// ##################### UPDATE SCENE #####################

function rotate() {
    var absolutePosition = new THREE.Vector3();
    son.getWorldPosition(absolutePosition);

    let rotation = 0;

    if (isRotating == MOVING) { 
        rotation = rotation_speed;
    } else if (isRotating == MOVING_BACK) { 
        rotation = -rotation_speed;
    }

    son.rotateY(rotation * Math.PI/180);
}

function moveCar() {
    if (isCarMoving == STOPPED) return;
    if (isCarMoving == MOVING && grandson.position.x < cabin_dim.x/2 + jib_dim.x - car_dim.x/2) {
        car_move += 0.03;
    }
    else if (isCarMoving == MOVING_BACK && grandson.position.x > son.position.x + base_dim.x/1.3 + hook_dim.x/2)  { 
        car_move -= 0.03;
    }

    grandson.position.x = car_move;
}

function moveHook() {
    if (isHookMoving == STOPPED) return;
    
    if (isHookMoving == MOVING && Math.abs(ggson.position.y) < base_dim.y/2 + tower_dim.y + cabin_dim.y- jib_dim.y){// {
        hook_size += hook_velocity;
        
    }
    else if (isHookMoving == MOVING_BACK && grandson.position.y - jib_dim.y - car_dim.y - hook_dim.y/2 > ggson.position.y){ 
        hook_size -= hook_velocity;
    }
    var diff = hook_size - default_hook_size;
    
    rope.scale.set(1, 1.5+diff, 1); //alterar
    
    ggson.position.y = - (1+diff)*2 ;
}

function movePulley() {
    if (isPulleyMoving == STOPPED) return;

    let pulley_move = 0;
    if (isPulleyMoving == MOVING && pulleys[0].rotation.z < Math.PI/4) {
        pulley_move = 0.01;
        pulley_radius += 0.01;
    }
    else if (isPulleyMoving == MOVING_BACK && pulleys[0].rotation.z > -Math.PI/16)  { 
        pulley_move = -0.01;
        pulley_radius -= 0.01;
    }

    const translation_value = 0.05;
    pulleys[0].rotateZ(pulley_move);
    pulleys[0].translateOnAxis(new THREE.Vector3(translation_value, 0, 0), pulley_move);
    pulleys[1].rotateX(-pulley_move);
    pulleys[1].translateOnAxis(new THREE.Vector3(0, 0, translation_value), pulley_move);
    pulleys[2].rotateZ(-pulley_move);
    pulleys[2].translateOnAxis(new THREE.Vector3(-translation_value, 0, 0), pulley_move);
    pulleys[3].rotateX(pulley_move);
    pulleys[3].translateOnAxis(new THREE.Vector3(0, 0, -translation_value), pulley_move);
}

function changeMaterial() {
    'use strict';

    allMaterials.forEach(
        item => {
            item.wireframe = wireframe;
        }
    );

    localStorage.setItem('wireframe', JSON.stringify(wireframe));
}

// ##################### HANDLE COLLISIONS #####################

function handleCollision(index) {
    var absolutePosition = new THREE.Vector3();

    ggson.getWorldPosition(absolutePosition);

    var hook_position = new THREE.Vector3().copy(absolutePosition);  //object
    hook_position.y = 0;
    var container_vec = new THREE.Vector3(container_pos.x, 0, container_pos.z); // Container

    if (container_vec.distanceTo(hook_position) > 1) {  // The object isn't above the container
        //LIFT 
        if (objects[index].position.y < 5) {
            hook_size -= hook_velocity;
            var diff = hook_size - default_hook_size;
            rope.scale.set(1, 1.5+diff, 1); //alterar
            ggson.position.y = - (1+diff)*2 ;
        } else{
            //ROTATE

            if (Math.abs(grandson.position.x) < 8.3) {
                car_move += 0.03;
                grandson.position.x = car_move;

            } else if (Math.abs(grandson.position.x) > 8.4){
                car_move -= 0.03;
                grandson.position.x = car_move;
            } else {
                    
                var dotProduct = container_vec.dot(hook_position);

                var crossProduct = new THREE.Vector3().crossVectors(container_vec, hook_position);

                // Calculate the angle betweenc container and object
                var angleRadians = Math.atan2(crossProduct.length(), dotProduct);

                // Create quaternions for rotation
                var quaternionPositive = new THREE.Quaternion().setFromAxisAngle(new THREE.Vector3(0, 1, 0), angleRadians);
                var quaternionNegative = new THREE.Quaternion().setFromAxisAngle(new THREE.Vector3(0, 1, 0), -angleRadians);

                // Apply rotations to object vector to predict where it will be if rotated in both directions
                var red = hook_position.clone().applyQuaternion(quaternionPositive);
                var blue = hook_position.clone().applyQuaternion(quaternionNegative);

                // Create objects for visualization
                // Object for red
                const object = new THREE.Object3D();
                const dimension = new THREE.BoxGeometry(0.5, 0.5, 0.5);
                var materials = new THREE.MeshBasicMaterial({ color: 0xff0000 , wireframe: wireframe});
                allMaterials.push(materials);
                var mesh = new THREE.Mesh(dimension, materials);
                object.add(mesh);
                object.position.x = red.x;
                object.position.y = red.y;
                object.position.z = red.z;

                // Object for blue
                const object1 = new THREE.Object3D();
                const dimension1 = new THREE.BoxGeometry(0.5, 0.5, 0.5);
                var materials1 = new THREE.MeshBasicMaterial({ color: 0x0000ff , wireframe: wireframe});
                allMaterials.push(materials);
                var mesh1 = new THREE.Mesh(dimension1, materials1);
                object1.add(mesh1);
                object1.position.x = blue.x;
                object1.position.y = blue.y;
                object1.position.z = blue.z;

                // Add objects to the scene
                //center.add(object1);
                //center.add(object);

                if (container_vec.distanceTo(hook_position) > 0.5) {
                    if (red.distanceTo(container_vec) < blue.distanceTo(container_vec)) 
                        son.rotateY(rotation_speed * Math.PI/180);
                    else
                        son.rotateY(-rotation_speed * Math.PI/180);
                
                } 
            }
        }    

    } else if(objects[index].position.y > 0.5){
        hook_size += hook_velocity;
        var diff = hook_size - default_hook_size;
        rope.scale.set(1, 1.5+diff, 1); //alterar
        ggson.position.y = - (1+diff)*2 ;
    } else {
        return FINISH_ANIMATION;
    }
    
    objects[index].position.x = absolutePosition.x;
    objects[index].position.z = absolutePosition.z;
    objects[index].position.y = absolutePosition.y - boundaries[index+2].radius + 0.1;

    return 0;
}

function finishAnimation(index) {
    for (var i = 0; i<3; i++){
        hook_size -= 1 * hook_velocity;
        var diff = hook_size - default_hook_size;
        rope.scale.set(1, 1.5+diff, 1); //alterar
        ggson.position.y = - (1+diff)*2 ;
        
    }
    center.remove(objects[index]);
    objects.splice(index, 1);
    boundaries.splice(index+2, 1);
}

function checkCollision() {

    var absolutePosition = new THREE.Vector3();
    ggson.getWorldPosition(absolutePosition);
    
    for (var i = 0; i<objects.length; i++) {
        if (
            Math.abs(absolutePosition.x - objects[i].position.x) < boundaries[i+2].radius &&
            Math.abs(absolutePosition.y - objects[i].position.y) < boundaries[i+2].radius && 
            Math.abs(absolutePosition.z - objects[i].position.z) < boundaries[i+2].radius 
        ) {

            if (pulley_radius < 0.5) { //Pulley is close
                return NO_COLLISION;
            }
            return i;
        }
    }    
    return NO_COLLISION; 
}

function collides(x, z, type) {
    var radius;

    switch(type) {
        case "cubo":
            radius = cubo_dim.radius;
        case "dodecaedro":
            radius = dodecaedro_dim.radius;
        case "icosaedro":
            radius = icosaedro_dim.radius;
        case "torus":
            radius = torus_dim.radius;
        case "torusKnot":
            radius = torusKnot_dim.radius;
        case "gancho":
            radius = hook_dim.collision_radius;
    }

    for (var i=0; i<boundaries.length; i++){
        var dx = x-boundaries[i].x;
        var dz = z-boundaries[i].z;
        var distance = Math.sqrt(dx*dx+dz*dz);
        var sum_radii = radius + boundaries[i].radius;
        if (distance < sum_radii) {
            return i;
        }
    }
    return NO_COLLISION;

}

function createCollisionSphere(hook) {
    const sphere = new THREE.Object3D();
    const materials = new THREE.MeshBasicMaterial({ color: 0x000000 , wireframe: wireframe, opacity : 0,  transparent: true});
    allMaterials.push(materials);
    const dimension = new THREE.SphereGeometry(hook_dim.x/2, 32, 32);
    const mesh = new THREE.Mesh(dimension, materials);

    sphere.position.y = - hook_dim.y/2;
    sphere.add(mesh);
    hook.add(sphere);
}

// ##################### CREATE SCENE #####################

function createScene(){
    'use strict';

    scene = new THREE.Scene(); // Dad reference
    scene.background = new THREE.Color(200/255, 200/255, 200/255);
    scene.add(center);

    // Father reference
    createBase(center);
    createTower(center);
    createSon(center);
    createContainer(center);

    // Other objects
    createFloor(center);
    createObject(center,"cubo", 0x0aa0a0);
    createObject(center,"dodecaedro",0xffffff);
    createObject(center,"icosaedro", 0xf000ff);
    createObject(center,"torus", 0xfff300);
    createObject(center,"torusKnot", 0xfffaaaa);
}

function createSon(center) {

    son = new THREE.Object3D();
    son.position.y = cabin_dim.y/2 + tower_dim.y + base_dim.y/2;

    center.add(son);

    createCabin(son);           // cabine
    
    createPortaLanca(son);      // lanca
    createLanca(son);
    createContraLanca(son);
    createContraPeso(son);

    createTiranteFrente(son);   // tirantes
    createTiranteTraz(son);
    
    createGrandSon(son);
}

function createGrandSon(son) {

    grandson = new THREE.Object3D();
    grandson.add(new THREE.AxesHelper(5));
    grandson.position.y = cabin_dim.y/2 - jib_dim.y;
    grandson.position.x = car_move;

    son.add(grandson);
    createCar(grandson);
    createRope(grandson);
    createGgson(grandson);

}

function createGgson(grandson) {
    ggson = new THREE.Object3D();


    ggson.add(new THREE.AxesHelper(5));

    ggson.position.y = - (1+0)*2 ;


    grandson.add(ggson);
    createHook(ggson);
}

// Others objects

function createFloor(center) {
    const floor = new THREE.Object3D();

    const materials = new THREE.MeshBasicMaterial({ color: 0x9fc9aa , wireframe: wireframe});
    allMaterials.push(materials);
    const dimension = new THREE.BoxGeometry(floor_dim.x, floor_dim.y, floor_dim.z);
    const mesh = new THREE.Mesh(dimension, materials);
    
    floor.position.y = - base_dim.y/2;

    floor.add(mesh);
    center.add(floor);
}

function createContainer(center) {
    const container = new THREE.Object3D();
    const thickness = 0.25;
  
    container.position.x = container_pos.x;
    container.position.z = container_pos.z;
    container.position.y = container_pos.y;

    const material1 = new THREE.MeshBasicMaterial({ color: 0x8a3a6a, wireframe: wireframe});
    allMaterials.push(material1);
    const material2 = new THREE.MeshBasicMaterial({ color: 0x7f4f5f, wireframe: wireframe});
    allMaterials.push(material2);
    const material3 = new THREE.MeshBasicMaterial({ color: 0x6f5a34, wireframe: wireframe});
    allMaterials.push(material3);

    const side1_face = new THREE.BoxGeometry(container_dim.x- 2*thickness, container_dim.y, thickness);
    const side1_mesh = new THREE.Mesh(side1_face, material1);
    const side2_mesh = new THREE.Mesh(side1_face, material1);
    const side3_face = new THREE.BoxGeometry(thickness, container_dim.y, container_dim.z);
    const side3_mesh = new THREE.Mesh(side3_face, material2);
    const side4_mesh = new THREE.Mesh(side3_face, material2);
    const bottom_face = new THREE.BoxGeometry(container_dim.x, thickness, container_dim.z); 
    const bottom_mesh = new THREE.Mesh(bottom_face, material3);

    side1_mesh.position.y = container_dim.y/2 - thickness/2;
    side1_mesh.position.z = container_dim.z/2 - thickness/2; 
    side2_mesh.position.y = container_dim.y/2 - thickness/2;
    side2_mesh.position.z = -container_dim.z/2 + thickness/2; 
    side3_mesh.position.y = container_dim.y/2 - thickness/2;
    side3_mesh.position.x = -container_dim.x/2 + thickness/2; 
    side4_mesh.position.y = container_dim.y/2 - thickness/2;
    side4_mesh.position.x = container_dim.x/2 - thickness/2; 
    bottom_mesh.position.y = -thickness/2;
    const boundary = {x: container.position.x, z:container.position.z, radius: container_dim.collision_radius};
    boundaries.push(boundary);
    container.rotateY(1.3)
    
    container.add(bottom_mesh,side1_mesh, side2_mesh, side3_mesh, side4_mesh);
    center.add(container);
}

function createObject(center, type, _color) {
    const object = new THREE.Object3D();
    const materials = new THREE.MeshBasicMaterial({ color: _color, wireframe: wireframe});
    allMaterials.push(materials);
    var geometry;
    var boundary_radius;
    var created = false;
    var x; var z;

    while (!created) {
        const randomX = Math.random(); x = randomX * 2 -1;
        const randomZ = Math.random(); z = randomZ * 2 -1;

        if (Math.sqrt(x*x + z*z) > 1.3) continue; // The object would be unreachable in these cases

        x = jib_dim.x * (2/3) *x;
        z = jib_dim.x * (2/3) *z;

        if (collides(x,z,type) != NO_COLLISION) continue;
        else created = true;


        switch(type) {
            case "cubo":
                geometry = new THREE.BoxGeometry(cubo_dim.x, cubo_dim.y, cubo_dim.z);
                boundary_radius = cubo_dim.collision_radius;
                object.position.y = -base_dim.y/2 + cubo_dim.y/2;
                break;
            case "dodecaedro":
                geometry = new THREE.DodecahedronGeometry(dodecaedro_dim.radius);
                boundary_radius = dodecaedro_dim.collision_radius;
                object.position.y = -base_dim.y/2 + dodecaedro_dim.radius;
                break;
            case "icosaedro":
                geometry = new THREE.IcosahedronGeometry(icosaedro_dim.radius);
                boundary_radius = icosaedro_dim.collision_radius;
                object.position.y = -base_dim.y/2 + icosaedro_dim.radius - 0.1;
                break;
            case "torus":
                geometry = new THREE.TorusGeometry(torus_dim.radius, torus_dim.tube, 16, 100);
                boundary_radius = torus_dim.collision_radius;
                object.position.y = -base_dim.y/2 + torus_dim.collision_radius;
                break;
            case "torusKnot":
                geometry = new THREE.TorusKnotGeometry(torusKnot_dim.radius, torusKnot_dim.tube, 100, 16);
                boundary_radius = torusKnot_dim.collision_radius;
                object.position.y = -base_dim.y/2 + torusKnot_dim.collision_radius - 0.1;
                break;
        }

    }

    const mesh = new THREE.Mesh(geometry, materials);

    object.position.x =  x;
    object.position.z = z;

    const boundary = { x: object.position.x, z: object.position.z, y: object.position.y, radius: boundary_radius};
    boundaries.push(boundary);
    objects.push(object);

    object.add(mesh);
    center.add(object);
}

// Father reference

function createBase(center) {
    'use struct';

    const base = new THREE.Object3D();

    const materials = new THREE.MeshBasicMaterial({ color: 0x808080 , wireframe: wireframe});
    allMaterials.push(materials);
    const dimension = new THREE.BoxGeometry(base_dim.x, base_dim.y, base_dim.z);
    const mesh = new THREE.Mesh(dimension, materials);

    const boundary = {x: 0, z:0, radius: base_dim.collision_radius};
    boundaries.push(boundary);

    mesh.add(new THREE.AxesHelper(5));
    base.add(mesh);
    center.add(base);
}

function createTower(base) {
    'use struct';

    const tower = new THREE.Object3D();
    
    const materials = new THREE.MeshBasicMaterial({ color: 0xF2B400, wireframe: wireframe});
    allMaterials.push(materials);
    const dimension = new THREE.BoxGeometry(tower_dim.x, tower_dim.y, tower_dim.z);
    const mesh = new THREE.Mesh(dimension, materials);
    mesh.add(new THREE.AxesHelper(5));

    tower.position.y = base_dim.y/2 + tower_dim.y/2;


    tower.add(mesh);
    base.add(tower);
}

// Son reference

function createTiranteFrente(son) {
    const thickness = 0.1;

    const tirante2 = new THREE.Object3D();
    const materials = new THREE.MeshBasicMaterial({ color: 0xaaaaaa, wireframe: wireframe });
    allMaterials.push(materials);
    const cateto1 = jib_dim.x;
    const altura = pl_dim.radius;
    const tir2_y = Math.sqrt((cateto1)**2 - (altura)**2);
    const dimension2 = new THREE.CylinderGeometry(thickness, thickness, tir2_y);
    const mesh2 = new THREE.Mesh(dimension2, materials);
    mesh2.position.x = 0 ;
    mesh2.position.y = tir2_y/2;

    const ref2 = new THREE.Object3D();
    ref2.position.y = cabin_dim.y/2 + altura;

    ref2.add(mesh2);
    ref2.rotateOnAxis(new THREE.Vector3(0, 0, 1), Math.PI*(-4.85/9));
    
    son.add(ref2);
}

function createTiranteTraz(son) {
    const tirante1 = new THREE.Object3D();
    const materials = new THREE.MeshBasicMaterial({ color: 0xaaaaaa, wireframe: wireframe });
    allMaterials.push(materials);
    const thickness = 0.1;
    const cateto1 = jib_dim.x/ratio
    const altura = pl_dim.radius;
    const tir1_y = Math.sqrt((cateto1)**2 - (altura)**2)+2;
    const dimension1 = new THREE.CylinderGeometry(thickness, thickness, tir1_y);
    //const dimension2 = new THREE.BoxGeometry(thickness, 0.2, thickness);
    const mesh1 = new THREE.Mesh(dimension1, materials);

    tirante1.position.x = -tir1_y/2;

    const ref1 = new THREE.Object3D();

    ref1.position.y = cabin_dim.y/2 + (altura);

    tirante1.rotateOnAxis(new THREE.Vector3(0, 0, 1), 4*Math.PI/8)

    tirante1.add(mesh1);
    ref1.add(tirante1);
    ref1.rotateOnAxis(new THREE.Vector3(0, 0, 1), Math.PI*(1/9));

    son.add(ref1);
}

function createCabin(son) {
    'use struct';

    cabin = new THREE.Object3D();
    const materials = new THREE.MeshBasicMaterial({ color: 0xC69628 , wireframe: wireframe});
    allMaterials.push(materials);
    const dimension = new THREE.BoxGeometry(cabin_dim.x, cabin_dim.y, cabin_dim.z);
    const mesh = new THREE.Mesh(dimension, materials);

    cabin.add(mesh);
    son.add(cabin);
}

function createContraPeso(son) {
    const counterWeight = new THREE.Object3D();
    const materials = new THREE.MeshBasicMaterial({ color: 0x000000, wireframe: wireframe });
    allMaterials.push(materials);
    const dimension = new THREE.BoxGeometry(peso_dim.x, peso_dim.y, peso_dim.z);
    const mesh = new THREE.Mesh(dimension, materials);

    counterWeight.position.x = -cabin_dim.x/2 - (jib_dim.x/ratio) - peso_dim.x/2;
    counterWeight.position.y = cabin_dim.y/2 - peso_dim.y/2;
    
    counterWeight.add(mesh);
    son.add(counterWeight);
}

function createPortaLanca(son) {
    const pl = new THREE.Object3D();
    const materials = new THREE.MeshBasicMaterial({ color:0xC2B400, wireframe: wireframe }); 
    allMaterials.push(materials);

    var vertices = [
        -1, -1, jib_dim.z/2,   // Vertex 0
        0, pl_dim.radius, 0, // Vertex 1
        -1, -1, -jib_dim.z/2, // Vertex 2
        1, -1, 0  // Vertex 3
    ];

    // Define faces of the tetrahedron
    var indices = [
        0, 1, 2,  // Face 0
        0, 3, 1,  // Face 1
        0, 2, 3,  // Face 2
        1, 3, 2   // Face 3
    ];

    const geometry = new THREE.BufferGeometry();
    geometry.setAttribute('position', new THREE.Float32BufferAttribute(vertices, 3));
    geometry.setIndex(indices);
    const mesh = new THREE.Mesh(geometry, materials);

    pl.position.y = cabin_dim.y/2 + pl_dim.radius/2 + 0.2;

    pl.add(mesh);
    son.add(pl);
}

function createLanca(son) {
    const lanca = new THREE.Object3D();
    const materials = new THREE.MeshBasicMaterial({ color: 0xF2B400, wireframe: wireframe });
    allMaterials.push(materials);
    const dimension = new THREE.BoxGeometry(jib_dim.x, jib_dim.y, jib_dim.z);
    const mesh = new THREE.Mesh(dimension, materials);

    lanca.position.y = cabin_dim.y/2 - jib_dim.y/2;
    lanca.position.x = cabin_dim.x/2 + jib_dim.x/2;

    lanca.add(mesh);
    son.add(lanca);
}

function createContraLanca(son) {
    const lanca = new THREE.Object3D();
    const materials = new THREE.MeshBasicMaterial({ color: 0xF2B400, wireframe: wireframe });
    allMaterials.push(materials);
    const dimension = new THREE.BoxGeometry(jib_dim.x/ratio, jib_dim.y, jib_dim.z);
    const mesh = new THREE.Mesh(dimension, materials);

    lanca.position.y = cabin_dim.y/2 - jib_dim.y/2;
    lanca.position.x = -cabin_dim.x/2 - (jib_dim.x/ratio)/2;

    lanca.add(mesh);
    son.add(lanca);
}

// Grandson reference

function createCar(grandson) {
    car = new THREE.Object3D();
    const materials = new THREE.MeshBasicMaterial({ color: 0x808080 , wireframe: wireframe});
    allMaterials.push(materials);
    const dimension = new THREE.BoxGeometry(car_dim.x, car_dim.y, car_dim.z);
    const mesh = new THREE.Mesh(dimension, materials);

    car.position.y = -car_dim.y/2;

    car.add(mesh);
    grandson.add(car);
}

function createRope(grandson) {
    rope = new THREE.Object3D();
    rope.position.y = hook_size/2;

    const materials = new THREE.MeshBasicMaterial({ color: 0x000000 , wireframe: wireframe});
    allMaterials.push(materials);
    const dimension = new THREE.CylinderGeometry(rope_dim.radius, rope_dim.radius, hook_size, 32);
    const mesh = new THREE.Mesh(dimension, materials);

    mesh.position.y = -hook_size/2;
    rope.scale.set(1, 1.5, 1); //alterar
    
    rope.add(mesh);
    grandson.add(rope);
}

// GrandGrandson reference

function createHook(ggson) {
    hook = new THREE.Object3D();
    const materials = new THREE.MeshBasicMaterial({ color: 0x000000 , wireframe: wireframe});
    allMaterials.push(materials);
    const dimension = new THREE.BoxGeometry(hook_dim.x, hook_dim.y, hook_dim.z);
    const mesh = new THREE.Mesh(dimension, materials);

    hook.add(mesh);
    ggson.add(hook);

    createPulley(hook, 0 * Math.PI/4, '#ff0000');
    createPulley(hook, 2 * Math.PI/4, '#A0ff00');
    createPulley(hook, 4 * Math.PI/4, '#0000ff');
    createPulley(hook, 6 * Math.PI/4, '#F2B400');
    createCollisionSphere(hook);

}

function createPulley(hook, angle, color) {
    const second_ref = new THREE.Object3D();

    const materials = new THREE.MeshBasicMaterial({ color: color , wireframe: wireframe});
    allMaterials.push(materials);
    const dimension = new THREE.CylinderGeometry(pulley_dim.radius, 0, pulley_dim.height, 8)
    const mesh = new THREE.Mesh(dimension, materials);


    second_ref.position.y = - hook_dim.y/2 ;
    second_ref.position.x = Math.cos(angle) * hook_dim.x/(2+pulley_dim.radius*20);
    second_ref.position.z = Math.sin(angle) * hook_dim.z/(2+pulley_dim.radius*20);

    mesh.position.y = -pulley_dim.height/3;

    second_ref.add(mesh);
    hook.add(second_ref);
    pulleys.push(second_ref);
}

export { 
    scene, hook, createScene, changeMaterial, 
    changeRotationState, changeCarState, changeHookState, changePulleyState, changeMaterialState,
    rotate, moveCar, moveHook, movePulley, 
    checkCollision, handleCollision, finishAnimation
};