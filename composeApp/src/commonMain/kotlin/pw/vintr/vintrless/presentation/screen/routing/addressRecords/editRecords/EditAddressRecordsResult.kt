package pw.vintr.vintrless.presentation.screen.routing.addressRecords.editRecords

data class EditAddressRecordsResult(
    val ips: List<String>,
    val domains: List<String>,
) {

    companion object {
        const val KEY = "edit-address-records-result"
    }
}
