const videoElement = document.getElementById('video');
const canvasElement = document.getElementById('canvas');
const canvasCtx = canvasElement.getContext('2d');


sounds = {
    0: '../sounds/c3.mp3',
    1: '../sounds/d3.mp3',
    2: '../sounds/e3.mp3',
    3: '../sounds/f3.mp3',
    4: '../sounds/g3.mp3',
    5: '../sounds/a3.mp3',
    6: '../sounds/b3.mp3',
    7: '../sounds/c4.mp3',
    8: '../sounds/d4.mp3',
    9: '../sounds/e4.mp3',
    10: 'none'
}

const preloadedSounds = {};

// Loading sounds into the AudioContext
async function loadSounds() {
    for (const key of Object.keys(sounds)) {
        if (sounds[key] !== 'none') {
            const response = await fetch(sounds[key]);
            const arrayBuffer = await response.arrayBuffer();
            audioContext.decodeAudioData(arrayBuffer, (audioBuffer) => {
                preloadedSounds[key] = audioBuffer; // Store the decoded buffer for later use
            });
        }
    }
}

// Playing a sound from the preloaded buffers
function playSound(key) {
    if (preloadedSounds[key]) {
        const source = audioContext.createBufferSource();
        source.buffer = preloadedSounds[key];
        source.connect(audioContext.destination); // Connect to default destination so the sound is played out loud
        source.connect(destination); // Also connect to the destination for recording
        source.start();
    }
}

loadSounds();


confidence_threshold = 0.95;
past_state = {'Left': -1, 'Right': -1}
t1 = {'Left': 0, 'Right': 0}
t2 = {'Left': 0, 'Right': 0}
time_threshold = 0.1


async function startCamera() {
    if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
        const stream = await navigator.mediaDevices.getUserMedia({ video: true });
        videoElement.srcObject = stream;
        videoElement.play(); // Ensure the video plays

        // Initialize the MediaPipe Hands library
        const hands = new Hands({
            locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/hands/${file}`
        });

        hands.setOptions({
            maxNumHands: 2,
            static_image_mode: false,
            modelComplexity: 1,
            minDetectionConfidence: 0.5,
            minTrackingConfidence: 0.5
        });

        hands.onResults(onResults);

        // start the frame processing loop
        onFrame();
        
        async function onFrame() {
            await hands.send({image: videoElement});
            requestAnimationFrame(onFrame); // Request the next frame
        }
    }
}

async function onResults(results) {
    canvasCtx.save();

    // Clear the canvas
    canvasCtx.clearRect(0, 0, canvasElement.width, canvasElement.height);

    // Flip the canvas context horizontally
    canvasCtx.scale(-1, 1);
    canvasCtx.translate(-canvasElement.width, 0);

    // Draw the mirrored video frame
    canvasCtx.drawImage(videoElement, 0, 0, canvasElement.width, canvasElement.height);

    // Flip context back for drawing landmarks correctly
    canvasCtx.scale(-1, 1);
    canvasCtx.translate(-canvasElement.width, 0);

    if (results.multiHandLandmarks) {
        for (const landmarks of results.multiHandLandmarks) {
            // Adjust landmark positions for the flipped context
            const adjustedLandmarks = landmarks.map(landmark => ({
                ...landmark,
                x: 1 - landmark.x, // Flip x coordinate
            }));

            const handedness = results.multiHandedness[results.multiHandLandmarks.indexOf(landmarks)];

            // If left, then make it right and vice versa. 
            if (handedness.label === 'Left') {
                handedness.label = 'Right';
            } else {
                handedness.label = 'Left';
            }

            drawConnectors(canvasCtx, adjustedLandmarks, HAND_CONNECTIONS, {color: '#00FF00', lineWidth: 5});
            drawLandmarks(canvasCtx, adjustedLandmarks, {color: '#FF0000', lineWidth: 2});
            processLandmarks(adjustedLandmarks, handedness);
        }
    }

    canvasCtx.restore();
}


startCamera();

function processLandmarks(landmarks, handedness) {
    // Log the landmarks to the console and the handedness
    current_hand = handedness.label;
    landmark_list = calc_landmark_list(landmarks);
    pre_processed_landmark = pre_process_landmark(landmark_list);
    const input_tensor = tf.tensor2d([pre_processed_landmark], [1, pre_processed_landmark.length]);
    // Make a prediction
    prediction = model.predict(input_tensor);
    process_sound(prediction);

}

function calc_landmark_list(landmarks) {
    // This function does the following:
    // For each landmark, it calculates the pixel coordinates (x, y) on the image. The coordinates provided by MediaPipe 
    // are normalized (ranging from 0 to 1), so they are scaled to the actual size of the image to get the pixel values.
    // These pixel coordinates represent key points on the hand, such as the tips of the fingers, the joints, and the wrist.
    // The method creates a list (landmark_point) where each item is a pair of coordinates (x, y) for each key point.

    const imageWidth = videoElement.width;
    const imageHeight = videoElement.height;

    const landmarkPoint = [];

    // Keypoint
    landmarks.forEach(landmark => {
        const landmarkX = Math.min(Math.floor(landmark.x * imageWidth), imageWidth - 1);
        const landmarkY = Math.min(Math.floor(landmark.y * imageHeight), imageHeight - 1);

        landmarkPoint.push([landmarkX, landmarkY]);
    });

    return landmarkPoint;
}

function pre_process_landmark(landmarkList) {
    // Clone the landmarkList to avoid mutating the original
    let tempLandmarkList = JSON.parse(JSON.stringify(landmarkList));

    // Convert to relative coordinates
    let baseX = 0, baseY = 0;
    tempLandmarkList.forEach((landmarkPoint, index) => {
        if (index === 0) {
            baseX = landmarkPoint[0];
            baseY = landmarkPoint[1];
        }
        tempLandmarkList[index][0] -= baseX;
        tempLandmarkList[index][1] -= baseY;
    });

    // Flatten the array
    tempLandmarkList = tempLandmarkList.flat();

    // Normalization
    const maxValue = Math.max(...tempLandmarkList.map(value => Math.abs(value)));
    tempLandmarkList = tempLandmarkList.map(n => n / maxValue);

    return tempLandmarkList;
}

function process_sound(predictions) {
    const scores = predictions.dataSync();
    if (Math.max(...scores) > confidence_threshold) {
        const class_index = scores.indexOf(Math.max(...scores));
        console.log(class_index);
        if (class_index < 10 && class_index !== past_state[current_hand]) {
            t1[current_hand] = new Date().getTime() / 1000;
            if (t1[current_hand] - t2[current_hand] > time_threshold) {
                // Use preloaded audio
                // preloadedSounds[class_index].currentTime = 0; // Reset the audio to the start
                // preloadedSounds[class_index].play();
                playSound(class_index);
                t2[current_hand] = t1[current_hand];
            } else {
                t2[current_hand] = t1[current_hand];
            }
        }
        past_state[current_hand] = class_index;
    } else {
        past_state[current_hand] = 10; // none
    }
}


async function load_model() {
    model = await tf.loadLayersModel('model/model.json');
}

load_model().then(() => {
    console.log('Model loaded successfully');
}).catch(error => {
    console.error('Model loading failed', error);
});
