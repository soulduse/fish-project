package com.dave.fish.util

/**
 * Created by soul on 2017. 10. 16..
 */
enum class WeatherCategory(val title: String) {
    POP("강수확률"),
    PTY("강수형태"),
    R06("6시간 강수량"),
    REH("습도"),
    S06("6시간 신적설"),
    SKY("하늘상태"),
    T3H("3시간 기온"),
    TMN("아침 최저기온"),
    TMX("낮 최고기온"),
    UUU("풍속(동서성분)"),
    VVV("풍속(남북성분)"),
    WAV("파고"),
    VEC("풍향"),
    WSD("풍속");
}