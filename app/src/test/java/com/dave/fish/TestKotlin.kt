package com.dave.fish

import org.junit.Test

/**
 * Created by soul on 2017. 9. 11..
 */

class TestKotlin {

    enum class ParserType{
        XML, JSON
    }

    @Test
    fun Enum과String테스트하기(){
        println(ParserType.JSON.toString().toLowerCase())
    }


    internal fun test() {
        val a = 0

        if (a in 151..200) {

        } else if (a in 101..150) {

        } else if (a in 51..100) {

        }
    }

    @Test
    fun 공백제거(){
        val a = "0.5 / 1.5"
        val b = " 0.5 / 1.5 "
        val c = "A B C D"
        val d = " A B C D "
        val e = "AB CD"

        println(a.trim())
        println(b.trim())
        println(c.trim())
        println(d.trim())
        println(e.trim())

        println()


        println()

        println(a.replace(" ",""))
        println(b.replace(" ",""))
        println(c.replace(" ",""))
        println(d.replace(" ",""))
        println(e.replace(" ",""))

    }
}
