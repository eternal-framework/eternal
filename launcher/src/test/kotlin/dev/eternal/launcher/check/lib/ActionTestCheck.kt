package dev.eternal.launcher.check.lib

import dev.eternal.launcher.check.Check
import dev.eternal.launcher.check.CheckAction
import java.lang.Exception

class ActionTestCheck : Check, CheckAction {

    override fun check(): Boolean {
        return false
    }

    override fun action() {
        throw TestActionException()
    }

}

class TestActionException : Exception()