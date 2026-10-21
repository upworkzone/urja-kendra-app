import secrets
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    app_name: str = "URJA KENDRA API"
    environment: str = "development"
    database_url: str = "sqlite:///./urja_dev.db"
    cors_origins: str = "http://localhost:3000,http://localhost:8080"
    auth_secret_key: str = "UNSAFE-DEV-ONLY-CHANGE-ME"
    access_token_minutes: int = 30
    otp_ttl_seconds: int = 300
    otp_max_attempts: int = 5
    otp_max_requests_per_window: int = 3
    otp_request_window_seconds: int = 900
    otp_provider: str = "stub"

    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

    @property
    def is_development(self) -> bool:
        return self.environment.strip().lower() in {"development", "dev", "local"}

    @property
    def parsed_cors_origins(self) -> list[str]:
        return [origin.strip() for origin in self.cors_origins.split(",") if origin.strip()]

    def validate_release_security(self) -> None:
        """Raise on unsafe auth/OTP settings outside local development."""
        if self.is_development:
            return
        if self.auth_secret_key == "UNSAFE-DEV-ONLY-CHANGE-ME" or len(self.auth_secret_key) < 32:
            raise ValueError("AUTH_SECRET_KEY must be a unique secret of at least 32 characters outside development")
        if self.otp_provider.strip().lower() == "stub":
            raise ValueError("OTP_PROVIDER=stub is development-only; configure a real OTP provider")
        if self.otp_ttl_seconds < 60 or self.otp_ttl_seconds > 600:
            raise ValueError("OTP_TTL_SECONDS must be between 60 and 600")
        if self.otp_max_attempts < 1 or self.otp_max_attempts > 5:
            raise ValueError("OTP_MAX_ATTEMPTS must be between 1 and 5")
        if self.otp_max_requests_per_window < 1 or self.otp_max_requests_per_window > 10:
            raise ValueError("OTP_MAX_REQUESTS_PER_WINDOW must be between 1 and 10")
        if self.otp_request_window_seconds < 60 or self.otp_request_window_seconds > 3600:
            raise ValueError("OTP_REQUEST_WINDOW_SECONDS must be between 60 and 3600")


settings = Settings()
