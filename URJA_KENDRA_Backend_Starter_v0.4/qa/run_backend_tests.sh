#!/usr/bin/env bash
set -u
cd "$(dirname "$0")/.."
echo "URJA KENDRA backend test runner"
python --version || exit $?
# Report every missing import separately; this check never installs packages.
missing=()
for module in fastapi sqlalchemy pydantic_settings psycopg jose passlib reportlab pytest httpx; do
  if ! python -c "import ${module}" >/dev/null 2>&1; then
    missing+=("${module}")
  fi
done
if ((${#missing[@]})); then
  echo "ERROR: missing Python modules: ${missing[*]}"
  echo "From this backend directory, activate your virtual environment and run:"
  echo "  python -m pip install -r requirements.txt"
  echo "If package-index access is unavailable, install the requirements from an approved local wheelhouse, then rerun this script."
  exit 2
fi
python -m compileall -q app tests || exit $?
python -m pytest -q tests || exit $?
echo "Backend tests passed."
