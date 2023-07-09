from flask import Flask
from flask import request
from datetime import datetime
import pickle
import os
from model import preprocess_data,  engineer_features, merge_sensors, get_data_from_string, merge_sensors, interpolate_data, preprocess_X, to_label
import numpy as np

app = Flask(__name__)


@app.route('/')
def test_api():
    return 'API is working normally'


@app.route('/data', methods=["POST"])
def post_data():
    data = request.json
    timestamp = datetime.now().strftime("%Y%m%d%H%M%S")
    gestureName = data["gestureName"]
    acc_data = data["acc"]
    gyro_data = data["gyro"]
    linear_data = data["linear"]
    os.makedirs("data", exist_ok=True)
    os.makedirs(f"data/{gestureName}", exist_ok=True)

    with open(f"data/{gestureName}/{timestamp}_acc.csv", "w") as f:
        f.write(acc_data)
    with open(f"data/{gestureName}/{timestamp}_gyro.csv", "w") as f:
        f.write(gyro_data)
    with open(f"data/{gestureName}/{timestamp}_linear.csv", "w") as f:
        f.write(linear_data)

    return "Gesture recorded successfully!"


@app.route('/prediction', methods=["POST"])
def predict():
    data = request.json
    # load data
    acc_data = get_data_from_string(data["acc"])
    gyro_data = get_data_from_string(data["gyro"])
    linear_data = get_data_from_string(data["linear"])
    merged_data = merge_sensors(acc_data, gyro_data, linear_data)
    preprocessed_data = preprocess_X([merged_data])

    # predict
    model = pickle.load(open("saved_models/gesture_detector.sav", "rb"))
    prediction = model.predict(preprocessed_data)
    labels = to_label(prediction)
    print(labels)
    return labels[0]
