package pw.vintr.vintrless.domain.userApplications.model.filter

enum class ApplicationFilterMode {
    BLACKLIST,
    WHITELIST;

    val id: String get() = when (this) {
        BLACKLIST -> "default_blacklist"
        WHITELIST -> "default_whitelist"
    }
}
