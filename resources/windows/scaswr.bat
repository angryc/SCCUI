@echo off
rem scaswr.bat - a drop target for config files

rem Change directory to the location of the batch script file (%0)...
cd /d "%~dp0"

rem Run scas with a default target filename...
echo Assembling %1...
scas %1 %1.scb
if errorlevel 1 goto end

rem Run scwr with that default filename...
echo:
echo Writing %1.scb...
scwr %1.scb

:end
rem Wait for a keypress so the output can be read...
echo:
pause
