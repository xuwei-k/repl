(1 to 10).toList
import reflect.macros._
import language.experimental.macros
def hogeImpl(c: Context)(a: c.Expr[Int]) = a
def hoge(a:Int) = macro hogeImpl
hoge(2)
import scalaz._
