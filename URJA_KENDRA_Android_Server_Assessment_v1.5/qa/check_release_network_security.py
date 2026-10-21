from pathlib import Path
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
gradle = (root / "app/build.gradle.kts").read_text(encoding="utf-8")
manifest = root / "app/src/main/AndroidManifest.xml"
xml = ET.parse(manifest).getroot()
android_ns = "{http://schemas.android.com/apk/res/android}"
app = xml.find("application")
assert app is not None
assert app.attrib.get(android_ns + "usesCleartextTraffic") == "${usesCleartextTraffic}"
assert 'buildConfigField("String", "URJA_API_BASE_URL", "\\\"http://10.0.2.2:8000/\\\"")' in gradle
assert 'providers.gradleProperty("URJA_API_BASE_URL").orElse("").get()' in gradle
assert 'manifestPlaceholders["usesCleartextTraffic"] = "false"' in gradle
assert 'manifestPlaceholders["usesCleartextTraffic"] = "true"' in gradle
assert 'tasks.named("preReleaseBuild").configure { dependsOn(verifyReleaseConfig) }' in gradle
assert 'baseUrl.startsWith("https://")' in gradle
assert 'baseUrl.contains("10.0.2.2")' in gradle
assert 'baseUrl.endsWith("/")' in gradle
print("PASS: release network security source checks (8 assertions)")
