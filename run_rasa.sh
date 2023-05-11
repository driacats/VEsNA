#!/bin/bash
cd Rasa/Assistant/
echo "[RASA] Loading source"
source ./venv/bin/activate
if ! [ "$(ls ./models/)" ]; then
	echo "[RASA] Training model"
	rasa train
fi
echo "[RASA] Starting rasa"
kitty --title "Rasa Shell" rasa shell &
kitty --title "Rasa Action Server" rasa run actions
