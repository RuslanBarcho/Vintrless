package pw.vintr.vintrless.tools.exception

class WrongSudoPasswordException : Exception() {

    override val message: String = "Wrong sudo password"
}
