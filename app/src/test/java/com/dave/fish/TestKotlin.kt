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

    @Test
    fun 문자열숫자변환테스트(){
        val str1 = "0000"
        val str2 = "0200"
        val str3 = "1600"
        val str4 = "2000"
        val int1 = 1200
        val int2 = 1800
        val int3 = 2200

        println("------------------")
        println(str1.toInt())
        println(str2.toInt())
        println(str3.toInt())
        println(str4.toInt())
        println("------------------")
        println(str4.drop(0))
        println(str4.drop(2))
        println(str4.take(1))
        println(str2.take(2).toInt())
        println(str1.take(2).toInt())
        println("------------------")
    }

    fun 뒤에00문자없애기(data:String){
        data.drop(1)
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
