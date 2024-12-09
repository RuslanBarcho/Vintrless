package pw.vintr.vintrless.presentation.screen.routing.addressRecords.addRecords

data class AddAddressRecordsResult(
    val records: List<String>,
    val replaceCurrent: Boolean,
) {

    companion object {
        const val KEY = "album-action-result"
    }
}
