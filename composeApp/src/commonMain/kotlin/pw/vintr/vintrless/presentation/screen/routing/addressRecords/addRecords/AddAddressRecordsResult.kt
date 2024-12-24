package pw.vintr.vintrless.presentation.screen.routing.addressRecords.addRecords

sealed class AddAddressRecordsResult {

    companion object {
        const val KEY = "add-address-records-result"
    }

    data class RecordsSelected(
        val records: List<String>,
        val replaceCurrent: Boolean,
    ) : AddAddressRecordsResult()

    data object OpenManualInput : AddAddressRecordsResult()
}
