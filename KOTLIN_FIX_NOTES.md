# Kotlin source repair

Escaped the Kotlin reserved keyword `in` in package names, imports, and qualified references. Gradle namespace/applicationId are unchanged.

This addresses many syntax errors but **build is not verified**. Rerun GitHub Actions and inspect remaining compiler errors.

Changed files:
- URJA_KENDRA_Android_Server_Assessment_v1.5/app/src/main/java/in/urjakendra/app/MainActivity.kt
- URJA_KENDRA_Android_Server_Assessment_v1.5/app/src/main/java/in/urjakendra/app/ui/LiveLessonScreen.kt
- URJA_KENDRA_Android_Server_Assessment_v1.5/app/src/main/java/in/urjakendra/app/ui/AuthScreen.kt
- URJA_KENDRA_Android_Server_Assessment_v1.5/app/src/main/java/in/urjakendra/app/ui/PilotLessonContent.kt
- URJA_KENDRA_Android_Server_Assessment_v1.5/app/src/main/java/in/urjakendra/app/ui/ServerAssessmentScreen.kt
- URJA_KENDRA_Android_Server_Assessment_v1.5/app/src/main/java/in/urjakendra/app/ui/AuthHost.kt
- URJA_KENDRA_Android_Server_Assessment_v1.5/app/src/main/java/in/urjakendra/app/ui/AuthViewModel.kt
- URJA_KENDRA_Android_Server_Assessment_v1.5/app/src/main/java/in/urjakendra/app/ui/LiveMyLearningScreen.kt
- URJA_KENDRA_Android_Server_Assessment_v1.5/app/src/main/java/in/urjakendra/app/ui/LiveCoursesScreen.kt
- URJA_KENDRA_Android_Server_Assessment_v1.5/app/src/main/java/in/urjakendra/app/data/UrjaRepository.kt
- URJA_KENDRA_Android_Server_Assessment_v1.5/app/src/main/java/in/urjakendra/app/data/SessionStore.kt
- URJA_KENDRA_Android_Server_Assessment_v1.5/app/src/main/java/in/urjakendra/app/data/UrjaApiClient.kt
- URJA_KENDRA_Android_Server_Assessment_v1.5/app/src/main/java/in/urjakendra/app/data/ApiModels.kt
- URJA_KENDRA_Android_Server_Assessment_v1.5/app/src/main/java/in/urjakendra/app/data/UrjaApi.kt
- URJA_KENDRA_Android_Server_Assessment_v1.5/app/src/test/java/in/urjakendra/app/CourseModelTest.kt
