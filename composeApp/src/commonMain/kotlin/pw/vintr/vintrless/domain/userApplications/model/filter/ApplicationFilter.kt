package pw.vintr.vintrless.domain.userApplications.model.filter

data class ApplicationFilter(
    val mode: ApplicationFilterMode,
    val filterKeys: List<String>
)
