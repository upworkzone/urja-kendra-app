# Local environment setup v2.2

## Option A — Docker Compose (recommended when Docker is installed)

From this directory:

```bash
docker compose build
docker compose up -d
docker compose ps
```

Open `http://localhost:8000/docs` for the development API docs and test
`http://localhost:8000/health`.

Run tests in a one-off container:

```bash
docker compose run --rm --no-deps api python -m pytest -q
```

Stop the API:

```bash
docker compose down
```

The named SQLite volume persists local development data. To reset local test data,
stop the service and remove the volume with `docker compose down -v` (this deletes the
local database).

## Option B — Python virtual environment

```bash
python -m venv .venv
```

Windows PowerShell:
```powershell
.\.venv\Scripts\Activate.ps1
python -m pip install -r requirements.txt
python -m pytest -q
```

macOS/Linux:
```bash
source .venv/bin/activate
python -m pip install -r requirements.txt
python -m pytest -q
```

## Security warning

This configuration is for local development only. It deliberately uses the stub OTP provider
and a local development signing secret. Do not expose it to the internet or use it with real
learner accounts. A deployment environment must set a unique managed signing secret and a real
OTP provider; the v2.0 startup guard should reject the unsafe configuration.

## Current verification record

- Python syntax compilation passed in the prior verification run.
- Pytest was blocked during collection because `python-jose` was not installed.
- Attempting `pip install -r requirements.txt` in the current environment failed because
  network name resolution was unavailable; the dependencies could not be downloaded.
- Docker build, container startup, API smoke tests, and Android build have not been run here.
