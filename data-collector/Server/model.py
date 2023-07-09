import numpy as np
import pandas as pd
import os
header=["time", "x", "y", "z"]

# create a better name for this function
def interpolate_data(data):
    # copy dataframe
    p_data = data.copy()

    p_data["time"] = p_data["time"] // 10

    # handle duplicate time values
    p_data = p_data.groupby("time").agg(["mean"])
    p_data.columns = p_data.columns.droplevel(0)
    p_data.columns = ["x", "y", "z"]

    # insert rows that are not existing in the dataframe
    p_data = p_data.reindex(range(0, 160), fill_value=np.nan)

    # set x, y, x of time 0 to 0
    p_data.loc[0, "x"] = data.loc[0, "x"]
    p_data.loc[0, "y"] = data.loc[0, "y"]
    p_data.loc[0, "z"] = data.loc[0, "z"]

    # interpolate missing values
    p_data = p_data.interpolate(method='linear', limit_direction='forward')
    

    return engineer_features(p_data)

def engineer_features(data):
    # copy dataframe
    e_data = data.copy()

    # calculate magnitudes
    e_data["magnitude"] = np.sqrt(e_data["x"]**2 + e_data["y"]**2 + e_data["z"]**2)
    e_data["xy"] = np.sqrt(e_data["x"]**2 + e_data["y"]**2)
    e_data["yz"] = np.sqrt(e_data["y"]**2 + e_data["z"]**2)
    e_data["xz"] = np.sqrt(e_data["x"]**2 + e_data["z"]**2)

    return e_data

def preprocess_data(X):
    nsamples, nx, ny = X.shape
    return X.reshape((nsamples,nx*ny))

def merge_sensors(acc, gyro, linear):
    acc = interpolate_data(acc)
    acc.columns = ["acc_"+col for col in acc.columns]
    gyro = interpolate_data(gyro)
    gyro.columns = ["gyro_"+col for col in gyro.columns]
    linear = interpolate_data(linear)
    linear.columns = ["linear_"+col for col in linear.columns]

    merged = acc.merge(gyro, on="time").merge(linear, on="time")
    merged = merged.reset_index()
    merged = merged.drop("time", axis=1)


    return merged

def merge_sensors_3d(acc, gyro, linear):
    acc = interpolate_data(acc)
    acc.columns = ["acc_"+col for col in acc.columns]
    gyro = interpolate_data(gyro)
    gyro.columns = ["gyro_"+col for col in gyro.columns]
    linear = interpolate_data(linear)
    linear.columns = ["linear_"+col for col in linear.columns]

    acc = acc.drop("time", axis=1)
    gyro = gyro.drop("time", axis=1)
    linear = linear.drop("time", axis=1)

    return np.array([acc, gyro, linear])

def get_data_from_string(data):
    data = data.strip().split("\n")
    data = [d.split(",") for d in data]
    data = pd.DataFrame(data[1:], columns=header)
    data["time"] = data["time"].astype(int)
    data["x"] = data["x"].astype(float)
    data["y"] = data["y"].astype(float)
    data["z"] = data["z"].astype(float)
    return data

def merge_sensors_from_file(identifier, gestureName):
    acc = pd.read_csv(f'data\\{gestureName}\\{identifier}_acc.csv', names=header)
    gyro = pd.read_csv(f'data\\{gestureName}\\{identifier}_gyro.csv', names=header)
    linear = pd.read_csv(f'data\\{gestureName}\\{identifier}_linear.csv', names=header)

    return merge_sensors(acc, gyro, linear)

def merge_sensors_3d_from_file(identifier, gestureName):
    # acc = pd.read_csv(f'data\\{gestureName}\\{identifier}_acc.csv', names=header)
    gyro = pd.read_csv(f'data\\{gestureName}\\{identifier}_gyro.csv', names=header)
    linear = pd.read_csv(f'data\\{gestureName}\\{identifier}_linear.csv', names=header)

    return merge_sensors_3d(gyro, linear)



labels = ["circle_in", "circle_out", "left", "right", "up", "down"]

label_to_index = {label: index for index, label in zip(range(len(labels)), labels)}
index_to_label = {index: label  for index, label in zip(range(len(labels)), labels)}

def get_dummy(value):
  dummy = [0 for _ in range(len(label_to_index))]
  index = label_to_index[value]
  dummy[index] = 1
  return dummy

def preprocess_X(X):
    return np.array(X)

def preprocess_y(y):
    y = [get_dummy(label) for label in y]
    y = np.array(y)
    return y

def to_label(y):
    return [index_to_label[np.argmax(yi)] for yi in y]