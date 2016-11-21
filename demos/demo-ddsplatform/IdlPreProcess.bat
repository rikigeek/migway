@ECHO OFF
REM
REM Generate java classes from IDL
REM
PUSHD %~dp0
echo Processing idl files....
for %%i in (idl\*.idl) do (
  echo "%OSPL_HOME%\bin\idlpp"-S  -I "%OSPL_HOME%\etc\idl" -l java -d src\main\idlpp -j :dds.gva %%i 
  "%OSPL_HOME%\bin\idlpp" -S -I "%OSPL_HOME%\etc\idl" -l java -d src\main\idlpp  -j :dds.gva %%i
  IF NOT %ERRORLEVEL% == 0 (
    ECHO:
    ECHO *** Compilation of %%i failed
    ECHO:
    ECHO %CD%
    SET ERRIDLPP=%%i;%ERRIDLPP%
  ) else (
    ECHO.
    ECHO *** %%id succeeded ***
  )
)
IF "%ERRIDLPP%"=="" (
  ECHO.
  ECHO *** Everything is awesome !!! ***
) ELSE (
  ECHO /!\ /!\ /!\ /!\   FAILURE   /!\ /!\ /!\ /!\
  ECHO /!\ /!\ /!\ /!\ EXTERMINATE /!\ /!\ /!\ /!\
  ECHO %ERRIDLPP%
)
pause
