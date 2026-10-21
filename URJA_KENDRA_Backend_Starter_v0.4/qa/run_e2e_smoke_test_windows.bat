@echo off
python qa\run_e2e_smoke_test.py
if errorlevel 1 exit /b %errorlevel%
