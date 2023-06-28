from flask import Flask
from flask import request
from datetime import datetime
import os

app = Flask(__name__)


@app.route('/')
def test_api():
    return 'API is working normally'


@app.route('/data', methods=["POST"])
def post():
    data = request.json
    timestamp = datetime.now().strftime("%Y%m%d%H%M%S")
    gestureName = data["gestureName"]
    acc_data = data["acc"]
    gyro_data = data["gyro"]
    linear_data = data["linear"]
    print(data)
    os.makedirs("data", exist_ok=True)
    os.makedirs(f"data/{gestureName}", exist_ok=True)

    with open(f"data/{gestureName}/{timestamp}_acc.csv", "w") as f:
        f.write(acc_data)
    with open(f"data/{gestureName}/{timestamp}_gyro.csv", "w") as f:
        f.write(gyro_data)
    with open(f"data/{gestureName}/{timestamp}_linear.csv", "w") as f:
        f.write(linear_data)

    return "Gesture recorded successfully!"
