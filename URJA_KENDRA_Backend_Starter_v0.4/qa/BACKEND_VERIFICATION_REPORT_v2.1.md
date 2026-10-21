# URJA KENDRA Backend Verification v2.1

## Result summary

- Python syntax compilation: PASS
- Pytest execution: BLOCKED/FAIL
- Pytest exit code: 2
- API end-to-end: NOT VERIFIED
- Android Gradle build: NOT VERIFIED

Syntax compilation only checks that Python files parse; it does not prove runtime correctness.

## Commands executed

### Syntax
`/opt/pyvenv/bin/python -m compileall -q app tests`

Exit code: 0

Output:
```text
Spreadsheet runtime warmup failed during python startup
Traceback (most recent call last):
  File "/tmp/tmp.QlpUkOUI9D/artifact_tool_v2-2.8.66/artifact_tool/patches/warm_spreadsheet_runtime_on_startup.py", line 26, in warm_spreadsheet_runtime_on_startup
  File "/tmp/tmp.QlpUkOUI9D/artifact_tool_v2-2.8.66/artifact_tool/spreadsheet_warmup.py", line 785, in warm_spreadsheet_runtime
  File "/tmp/tmp.QlpUkOUI9D/artifact_tool_v2-2.8.66/artifact_tool/spreadsheet_warmup.py", line 718, in _warm_feature_flows
  File "/tmp/tmp.QlpUkOUI9D/artifact_tool_v2-2.8.66/artifact_tool/spreadsheet_warmup.py", line 509, in _warm_drawing_and_image_flows
  File "/tmp/tmp.QlpUkOUI9D/artifact_tool_v2-2.8.66/artifact_tool/generated/interface/models.py", line 32861, in bring_to_front
  File "/tmp/tmp.QlpUkOUI9D/artifact_tool_v2-2.8.66/artifact_tool/rpc/remote.py", line 764, in __call__
  File "/tmp/tmp.QlpUkOUI9D/artifact_tool_v2-2.8.66/artifact_tool/rpc/client.py", line 175, in call
artifact_tool.rpc.client.RemoteError: Element ordering requires an owning element collection.
```

### Automated tests
`/opt/pyvenv/bin/python -m pytest -q`

Exit code: 2

Output:
```text
==================================== ERRORS ====================================
[31m[1m______________________ ERROR collecting tests/test_api.py ______________________[0m
[31mImportError while importing test module '/mnt/data/URJA_KENDRA_Backend_Verification_v2.1/URJA_KENDRA_Backend_Starter_v0.4/tests/test_api.py'.
Hint: make sure your test modules/packages have valid Python names.
Traceback:
[1m[31m/usr/lib/python3.13/importlib/__init__.py[0m:88: in import_module
    [0m[94mreturn[39;49;00m _bootstrap._gcd_import(name[level:], package, level)[90m[39;49;00m
           ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^[90m[39;49;00m
[1m[31mtests/test_api.py[0m:2: in <module>
    [0m[94mfrom[39;49;00m[90m [39;49;00m[04m[96mapp[39;49;00m[04m[96m.[39;49;00m[04m[96mmain[39;49;00m[90m [39;49;00m[94mimport[39;49;00m app[90m[39;49;00m
[1m[31mapp/main.py[0m:12: in <module>
    [0m[94mfrom[39;49;00m[90m [39;49;00m[04m[96mapp[39;49;00m[04m[96m.[39;49;00m[04m[96mapi[39;49;00m[04m[96m.[39;49;00m[04m[96mauth_routes[39;49;00m[90m [39;49;00m[94mimport[39;49;00m router [94mas[39;49;00m auth_router[90m[39;49;00m
[1m[31mapp/api/auth_routes.py[0m:5: in <module>
    [0m[94mfrom[39;49;00m[90m [39;49;00m[04m[96mapp[39;49;00m[04m[96m.[39;49;00m[04m[96mservices[39;49;00m[04m[96m.[39;49;00m[04m[96mauth[39;49;00m[90m [39;49;00m[94mimport[39;49;00m request_otp, verify_otp[90m[39;49;00m
[1m[31mapp/services/auth.py[0m:8: in <module>
    [0m[94mfrom[39;49;00m[90m [39;49;00m[04m[96mapp[39;49;00m[04m[96m.[39;49;00m[04m[96mservices[39;49;00m[04m[96m.[39;49;00m[04m[96mtokens[39;49;00m[90m [39;49;00m[94mimport[39;49;00m create_access_token[90m[39;49;00m
[1m[31mapp/services/tokens.py[0m:2: in <module>
    [0m[94mfrom[39;49;00m[90m [39;49;00m[04m[96mjose[39;49;00m[90m [39;49;00m[94mimport[39;49;00m jwt[90m[39;49;00m
[1m[31mE   ModuleNotFoundError: No module named 'jose'[0m[0m
[36m[1m=========================== short test summary info ============================[0m
[31mERROR[0m tests/test_api.py
!!!!!!!!!!!!!!!!!!!! Interrupted: 1 error during collection !!!!!!!!!!!!!!!!!!!!
[31m[31m[1m1 error[0m[31m in 0.67s[0m[0m
Spreadsheet runtime warmup failed during python startup
Traceback (most recent call last):
  File "/tmp/tmp.QlpUkOUI9D/artifact_tool_v2-2.8.66/artifact_tool/patches/warm_spreadsheet_runtime_on_startup.py", line 26, in warm_spreadsheet_runtime_on_startup
  File "/tmp/tmp.QlpUkOUI9D/artifact_tool_v2-2.8.66/artifact_tool/spreadsheet_warmup.py", line 785, in warm_spreadsheet_runtime
  File "/tmp/tmp.QlpUkOUI9D/artifact_tool_v2-2.8.66/artifact_tool/spreadsheet_warmup.py", line 718, in _warm_feature_flows
  File "/tmp/tmp.QlpUkOUI9D/artifact_tool_v2-2.8.66/artifact_tool/spreadsheet_warmup.py", line 509, in _warm_drawing_and_image_flows
  File "/tmp/tmp.QlpUkOUI9D/artifact_tool_v2-2.8.66/artifact_tool/generated/interface/models.py", line 32861, in bring_to_front
  File "/tmp/tmp.QlpUkOUI9D/artifact_tool_v2-2.8.66/artifact_tool/rpc/remote.py", line 764, in __call__
  File "/tmp/tmp.QlpUkOUI9D/artifact_tool_v2-2.8.66/artifact_tool/rpc/client.py", line 175, in call
artifact_tool.rpc.client.RemoteError: Element ordering requires an owning element collection.
```

## Interpretation

Do not mark the backend tests as passed. Resolve the exact error shown above, rerun the tests, and record the real result.

No live server or Android app was launched as part of this verification. The security settings still need review, and the backend remains a development scaffold until a real OTP provider, rate limiting, HTTPS deployment, and production configuration are implemented.
