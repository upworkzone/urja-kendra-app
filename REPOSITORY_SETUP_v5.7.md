# GitHub repository setup — v5.7

The ZIP has an outer wrapper directory for convenient extraction. Do not push that wrapper as a nested folder if you expect GitHub Actions to run automatically.

Correct repository root:
```
.github/workflows/android-build.yml
.github/workflows/backend-tests.yml
URJA_KENDRA_Android_Server_Assessment_v1.5/
URJA_KENDRA_Backend_Starter_v0.4/
README.md
```

After copying the contents, run `python qa_check_repository_layout.py` from the repository root. It checks that the workflow files and referenced project directories are in the expected locations.
