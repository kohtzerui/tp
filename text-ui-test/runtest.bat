@echo off
setlocal enableextensions
pushd %~dp0

cd ..
call gradlew clean shadowJar

cd build\libs
for /f "tokens=*" %%a in (
    'dir /b *.jar'
) do (
    set jarloc=%%a
)

java -ea -jar %jarloc% < ..\..\text-ui-test\input.txt > ..\..\text-ui-test\ACTUAL.TXT

cd ..\..\text-ui-test

powershell -Command "(Get-Content ACTUAL.TXT) | ForEach-Object { $_.TrimEnd() } | Set-Content -Encoding utf8 ACTUAL.TXT"
powershell -Command "(Get-Content EXPECTED.TXT) | ForEach-Object { $_.TrimEnd() } | Set-Content -Encoding utf8 EXPECTED.TXT"

FC ACTUAL.TXT EXPECTED.TXT >NUL && ECHO Test passed! || Echo Test failed!
