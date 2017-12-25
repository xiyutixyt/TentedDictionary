package tented.dictionary

/**
 * Created by Hoshino Tented on 2017/12/23.
 */

@Target(AnnotationTarget.CLASS , AnnotationTarget.FUNCTION , AnnotationTarget.EXPRESSION)
@Retention(AnnotationRetention.SOURCE)
annotation class Version(val value : String)