import { changeCameraState } from './camera.js';
import { changeMaterialState, changeCarState, changeHookState, changePulleyState, changeRotationState } from './scene.js';
import { MOVING, MOVING_BACK, STOPPED } from './constants.js';

function onKeyDown(key) {

    document.getElementById(key.code)?.classList.add("active")

    //Control Cameras
    if (key.key >= '1' && key.key <= '6') {
        changeCameraState(key.key - 1)
    }

    // Control Materials
    if (key.key == '7') {
        changeMaterialState();
    }

    // Control Movements
    if (key.code == 'KeyQ') {
        changeRotationState(MOVING);
    } else if (key.code == 'KeyA') {
        changeRotationState(MOVING_BACK);
    } else if (key.code == 'KeyW') {
        changeCarState(MOVING);
    } else if (key.code == 'KeyS') {
        changeCarState(MOVING_BACK);
    } else if (key.code == 'KeyE') {
        changeHookState(MOVING);
    } else if (key.code == 'KeyD') {
        changeHookState(MOVING_BACK);
    } else if (key.code == 'KeyR') {
        changePulleyState(MOVING);
    } else if (key.code == 'KeyF') {
        changePulleyState(MOVING_BACK);
    }
}

function onKeyUp(key) {

    document.getElementById(key.code)?.classList.remove("active")

    if (key.code == 'KeyQ' || key.code == 'KeyA') {
        changeRotationState(STOPPED);
    } else if (key.code == 'KeyW' || key.code == 'KeyS') {
        changeCarState(STOPPED);
    } else if (key.code == 'KeyE' || key.code == 'KeyD') {
        changeHookState(STOPPED);
    } else if (key.code == 'KeyR' || key.code == 'KeyF') {
        changePulleyState(STOPPED);
    }

}

export { onKeyDown, onKeyUp };