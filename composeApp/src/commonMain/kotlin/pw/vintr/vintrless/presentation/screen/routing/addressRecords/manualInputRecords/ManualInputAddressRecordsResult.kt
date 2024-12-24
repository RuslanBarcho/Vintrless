package pw.vintr.vintrless.presentation.screen.routing.addressRecords.manualInputRecords

data class ManualInputAddressRecordsResult(
    val records: List<String>,
    val replaceCurrent: Boolean,
) {
    companion object {
        const val KEY = "manual-input-records-result"
    }
}
