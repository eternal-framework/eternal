package dev.eternal.launcher.check.lib

import dev.eternal.launcher.check.Check

class FailTestCheck : Check {

    override fun check(): Boolean {
        return false
    }

}