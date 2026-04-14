@echo off
setlocal enableextensions
pushd %~dp0

cd ..
call gradlew clean jar

cd build\libs
for /f "tokens=*" %%a in (
    'dir /b *.jar'
) do (
    set jarloc=%%a
)

if exist data rmdir /s /q data
java -ea -jar %jarloc% < ..\..\text-ui-test\input.txt > ..\..\text-ui-test\ACTUAL.TXT

cd ..\..\text-ui-test

powershell -Command "$utf8NoBom = New-Object System.Text.UTF8Encoding($false); [System.IO.File]::WriteAllLines('ACTUAL-WIN.TXT', (Get-Content ACTUAL.TXT | ForEach-Object { $_.TrimEnd() }), $utf8NoBom)"
powershell -Command "$utf8NoBom = New-Object System.Text.UTF8Encoding($false); [System.IO.File]::WriteAllLines('EXPECTED-WIN.TXT', (Get-Content EXPECTED.TXT | ForEach-Object { $_.TrimEnd() }), $utf8NoBom)"

FC ACTUAL-WIN.TXT EXPECTED-WIN.TXT >NUL
set test_result=%errorlevel%
del /q ACTUAL-WIN.TXT EXPECTED-WIN.TXT

if %test_result%==0 (
    ECHO Test passed!
    exit /b 0
) else (
    ECHO Test failed!
    exit /b 1
)
