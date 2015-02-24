import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import collection.JavaConversions._

object Hello {
  def main(args: Array[String]): Unit = {
    CinemaType.values.map(cinema => getCinema(cinema.toString)).foreach(printCinema)
  }

  def printCinema(cinema: Cinema): Unit = {
    println(s"Cinema: ${cinema.name}. Rua: ${cinema.address.street}. Cep: ${cinema.address.cep}")
  }

  def getCinema(cinemaUrl: String): Cinema = {
    val document = Jsoup.connect(cinemaUrl).get()
    val cinemaName = document.select("#title span").text()
    val cinemaStreet = document.select("#col_main > div.data_box > div > div.content > ul > li:nth-child(1) > div").text()
    val cinemaCep = document.select("#col_main > div.data_box > div > div.content > ul > li:nth-child(2) > div").text()
    val address = new Address(cinemaStreet, cinemaCep)
    new Cinema(cinemaName, address, getMoviesFromCinema(document))
  }

  def getMoviesFromCinema(document: Document): List[Movie] = {
    val movies: List[Movie] = List()
    val movieTitles = document.select(".no_underline")

    movieTitles.subList(0, movieTitles.size()).foreach(element => {
      movies :+ createMovieFromUrl(s"http://www.adorocinema.com${element.attr("href")}")
    })

    movies
  }

  def createMovieFromUrl(url: String): Movie = {
    val document = Jsoup.connect(url).get()
    val movieTitle = document.select(".tt_r26").text()
    val movieSynopsis = document.select(".margin_20b > p:nth-child(3)").first().text()
    val durationInMinutes = getDurationInMinutes(document)
    val movieDirector = document.select("[itemprop=director] > a > span").text()
    val movieGenders = getGenders(document)
    new Movie(movieTitle, movieSynopsis, durationInMinutes, movieDirector, movieGenders)
  }

  def getGenders(document: Document): String = {
    val elements = document.select("[itemprop=genre]")
    elements.subList(0, elements.size()).map(_.text())reduce(_ + ", " + _)
  }

  def getDurationInMinutes(document: Document): Int = {
    val text = document.select("[itemprop=duration]").text()
    val hours = text.substring(0, text.indexOf('h')).toInt
    val minutes = text.substring(text.indexOf('h') + 1, text.indexOf("min")).toInt
    (hours * 60) + minutes
  }

}
