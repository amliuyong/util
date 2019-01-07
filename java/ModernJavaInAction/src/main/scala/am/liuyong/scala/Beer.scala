package am.liuyong.scala

import scala.io.Source

object Beer {
  def main(args: Array[String]) = {
    2 to 6 foreach { n => println(s"Hello ${n} bottles of beer") }
  }

  def collections(): Unit = {
    val authorsToAge = Map("Raoul" -> 23, "Mario" -> 40, "Alan" -> 53)
    val authors = List("Raoul", "Mario", "Alan")
    val numbers = Set(1, 1, 2, 3, 5, 8)
    val newNumbers = numbers + 8
    println(newNumbers)
    println(numbers)

    val raoul = ("Raoul", "+44 7700 700042")
    val alan = ("Alan", "+44 7700 700314")
    val book = (2018, "Modern Java in Action", "Manning")
    val numbers2 = (42, 1337, 0, 3, 14)

    println(book._1)
    println(numbers2._1)

  }

  def fromFile(): Unit = {
    val fileLines = Source.fromFile("data.txt").getLines.toList

    val linesLongUpper = fileLines.filter(l => l.length() > 10).map(l => l.toUpperCase())

    val linesLongUpper2 = fileLines.par filter (_.length() > 10) map (_.toUpperCase())
  }

  /*
    def getCarInsuranceName(person: Option[Person], minAge: Int) =
      person.filter(_.age >= minAge)
        .flatMap(_.car)
        .flatMap(_.insurance)
        .map(_.name)
        .getOrElse("Unknown")
   */


  def test: Unit = {

    val tweets = List(
      "I love the new features in Java",
      "How's it going?",
      "An SQL query walks into a bar, sees two tables and says 'Can I join you?'"
    )

    def isJavaMentioned(tweet: String): Boolean = tweet.contains("Java")

    def isShortTweet(tweet: String): Boolean = tweet.length() < 20

    tweets.filter(isJavaMentioned).foreach(println)
    tweets.filter(isShortTweet).foreach(println)

    val isLongTweet: String => Boolean
    = (tweet: String) => tweet.length() > 60

    val isLongTweet2: String => Boolean
    = new Function1[String, Boolean] {
      def apply(tweet: String): Boolean = tweet.length() > 60
    }

    def multiplyCurry(x :Int)(y : Int) = x * y
    val r = multiplyCurry(2)(10)

  }
}


