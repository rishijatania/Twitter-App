#!/bin/bash
set -e
python worker-app.py &
python flask-app.py