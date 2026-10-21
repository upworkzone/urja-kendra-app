@echo off
setlocal
python -m pip install --upgrade pip
if errorlevel 1 exit /b %errorlevel%
python -m pip install -r requirements.txt
if errorlevel 1 exit /b %errorlevel%
python -m pytest -q
exit /b %errorlevel%
