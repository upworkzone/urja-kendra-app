# v1.0 — Live catalogue screen integration

The Courses tab now calls `UrjaRepository.getCourses(locale)`, filters unpublished entries, and displays loading, error/retry, and empty states. It reloads when the language changes.

## Known limitation
The current backend `CourseDto` has one `title` and `summary` field, not separate Hindi and English fields. The screen passes the API title into both local display title fields; true bilingual catalogue rendering requires backend responses to return localized values (or both `title_en` and `title_hi`) and DTO mapping to be updated. Course detail still uses the existing local detail screen and sample content. Home and My Learning still contain sample data.

## Test
1. Run backend and check `/health`.
2. Open app and complete OTP login.
3. Tap Courses; confirm network-loaded catalogue, retry after stopping backend, then restart backend.
4. Switch English/Hindi and confirm API is re-requested.
5. Check the course IDs and module counts in the API response.

This package is source only. No Gradle build or emulator run was performed here. Ensure API JSON matches `CourseDto`. Do not use the local HTTP base URL in production.
