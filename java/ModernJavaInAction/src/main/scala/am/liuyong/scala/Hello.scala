package am.liuyong.scala

class Hello {
  private[scala] def sayThankYou = println("Thanks for reading our book")
}

class Student(var name: String, var id: Int)


trait Sized {
  var size: Int = 0

  def isEmpty = size == 0
}


class Empty extends Sized

class Box

object Hello {
  def main(args: Array[String]): Unit = {
    val h = new Hello()
    h.sayThankYou

    val s = new Student("Raoul", 1)
    println(s.name)
    s.id = 1337
    println(s.id)

    println(new Empty().isEmpty)


    val b1 = new Box() with Sized
    println(b1.isEmpty)
    val b2 = new Box()
    //b2.isEmpty


  }
}



