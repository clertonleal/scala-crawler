import driver.PgDriver
import model.{CinemaMovie, Movie, Cinema, Address}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import collection.JavaConversions._
import PgDriver.simple._


object Hello {
  def main(args: Array[String]): Unit = {
    Database.forURL(url = "jdbc:postgresql://localhost:5432/postgres", driver = "org.postgresql.Driver", user = "postgres", password = "mente1") withSession {
      implicit session =>
        CinemaType.values.map(cinema => saveCinema(cinema.toString))
    }
  }

  def saveCinema(cinemaUrl: String)(implicit session: Session) = {
    val addresses = TableQuery[Address]
    val cinemas = TableQuery[Cinema]
    val document = Jsoup.connect(cinemaUrl).get()
    val cinemaName = document.select("#title span").text()
    val cinemaStreet = document.select("#col_main > div.data_box > div > div.content > ul > li:nth-child(1) > div").text()
    val cinemaCep = document.select("#col_main > div.data_box > div > div.content > ul > li:nth-child(2) > div").text()

    val addressId = addresses returning addresses.map(_.id) += (cinemaStreet, "Fortaleza", "Ceará", cinemaCep)
    val cinemaId = cinemas returning cinemas.map(_.id) += (cinemaName, addressId)

    saveMovies(document, cinemaId)
  }

  def saveMovies(document: Document, cinemaId: Int)(implicit session: Session) = {
    val movies = TableQuery[Movie]
    val cinemaMovies = TableQuery[CinemaMovie]
    val movieTitles = document.select(".no_underline")

    movieTitles.subList(0, movieTitles.size()).foreach(element => {
      val movieId = movies returning movies.map(_.id) += createMovieFromUrl(s"http://www.adorocinema.com${element.attr("href")}")
      cinemaMovies += (cinemaId, movieId)
    })

  }

  def createMovieFromUrl(url: String): (String, String, Int, String, String) = {
    val document = Jsoup.connect(url).get()
    val movieTitle = document.select(".tt_r26").text()
    val movieSynopsis = document.select(".margin_20b > p:nth-child(3)").first().text()
    val durationInMinutes = getDurationInMinutes(document)
    val movieDirector = document.select("[itemprop=director] > a > span").text()
    val movieGenders = getGenders(document)
    (movieTitle, movieSynopsis, durationInMinutes, movieDirector, movieGenders)
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
