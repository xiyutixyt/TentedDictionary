package tented.dictionary

infix fun Number.randomTo( to : Number ) : Long = this.toLong() + (Math.random() * (to.toLong() - this.toLong())).toLong()

fun String.isNumber() : Boolean = matches(Regex("-?[0-9]+"))