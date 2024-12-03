package pw.vintr.vintrless.tools.extensions

import com.eygraber.uri.Uri

fun Uri.getQueryParams(): Map<String, String> {
    return query?.split("&")
        ?.associate { it.split("=").let { (k, v) -> k to v } }
        .orEmpty()
}
