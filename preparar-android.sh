#!/usr/bin/env bash
set -euo pipefail

mkdir -p app/src/main/assets
mkdir -p app/src/main/java/com/livroponto/digital
mkdir -p app/src/main/res/values

cp app-build.gradle app/build.gradle
cp AndroidManifest.xml app/src/main/AndroidManifest.xml
cp MainActivity.java app/src/main/java/com/livroponto/digital/MainActivity.java
cp styles-android.xml app/src/main/res/values/styles.xml

cp styles.css app/src/main/assets/styles.css
cp app.js app/src/main/assets/app.js
cp android-bridge.js app/src/main/assets/android-bridge.js

python3 - <<'PY'
from pathlib import Path
src = Path("index.html").read_text(encoding="utf-8")
needle = '<script src="app.js"></script>'
replacement = '<script src="android-bridge.js"></script>\n  <script src="app.js"></script>'
if needle in src and 'android-bridge.js' not in src:
    src = src.replace(needle, replacement)
elif 'android-bridge.js' not in src:
    src += '\n<script src="android-bridge.js"></script>\n<script src="app.js"></script>\n'
Path("app/src/main/assets/index.html").write_text(src, encoding="utf-8")
PY

echo "Estrutura Android preparada:"
find app/src/main -maxdepth 5 -type f | sort
