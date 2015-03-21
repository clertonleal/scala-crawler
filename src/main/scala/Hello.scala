import driver.PgDriver
import model._
import org.joda.time.LocalTime
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import collection.JavaConversions._
import PgDriver.simple._

object Hello {
  val addresses = TableQuery[Address]
  val cinemas = TableQuery[Cinema]
  val movies = TableQuery[Movie]
  val cinemaMovies = TableQuery[CinemaMovie]
  val hours = TableQuery[Hour]

  def main(args: Array[String]) {
    Database.forURL(url = "jdbc:postgresql://localhost:5432/postgres", driver = "org.postgresql.Driver", user = "postgres", password = "mente1") withSession {
      implicit session =>
        deleteData()
        CinemaType.values.map(saveCinema)
    }
  }

  def deleteData()(implicit session: Session) = {
    addresses.delete
    cinemas.delete
    movies.delete
    cinemaMovies.delete
    hours.delete
  }

  def saveCinema(cinemaType: CinemaType.Value)(implicit session: Session) = {
    val document = Jsoup.connect(cinemaType.toString).get()
    val cinemaName = document.select("#title span").text()
    val cinemaStreet = document.select("#col_main > div.data_box > div > div.content > ul > li:nth-child(1) > div").text()
    val cinemaCep = document.select("#col_main > div.data_box > div > div.content > ul > li:nth-child(2) > div").text()

    val addressId = addresses returning addresses.map(_.id) += (cinemaStreet, "Fortaleza", "Ceará", cinemaCep)
    val cinemaId = cinemas returning cinemas.map(_.id) += (cinemaName, addressId)

    saveMovies(document, cinemaId)
  }
  def saveMovies(document: Document, cinemaId: Int)(implicit session: Session) = {
    val movieTitles = document.select(".no_underline")

    val movieIds = movieTitles.
      subList(0, movieTitles.size()).
      map(element => {
        val movieValues = createMovieFromUrl(s"http://www.adorocinema.com${element.attr("href")}")
        val movieId = saveAndResolveMovieId(movieValues)
        cinemaMovies += (cinemaId, movieId)
        movieId
    })

    val hoursContainers = document.select(".list_hours")
    movieIds.foreach(movieId => {
      hoursContainers.
        subList(0, hoursContainers.size()).
        grouped(5).
        map(_.head).
        foreach(element => {
        val hourArray = element.select("li > em").first().text().split(':')
        val movieTime = new LocalTime(getNumberFromString(hourArray(0)), getNumberFromString(hourArray(1)))
        hours += (movieId, cinemaId, movieTime)
      })

    })

  }

  def getNumberFromString(text: String): Int = {
    if (text.startsWith("0")) {
      text.substring(1).toInt
    } else {
      text.toInt
    }
  }

  def saveAndResolveMovieId(movieValues: (String, String, Int, String, String, String))(implicit session: Session): Int = {
    val actualMovieId = movies.filter(_.title === movieValues._1).map(_.id).list
    if (actualMovieId.isEmpty) {
      movies returning movies.map(_.id) += movieValues
    } else {
      actualMovieId(0)
    }
  }

  def createMovieFromUrl(url: String): (String, String, Int, String, String, String) = {
    val document = Jsoup.connect(url).get()
    val movieTitle = document.select(".tt_r26").text()
    val movieSynopsis = getMovieSynopses(document)
    val durationInMinutes = getDurationInMinutes(document)
    val movieDirector = document.select("[itemprop=director] > a > span").text()
    val movieGenders = getGenders(document)
    val coverUrl = document.select(".poster > span > img[itemprop=image]").first().attr("src");
    (movieTitle, movieSynopsis, durationInMinutes, movieDirector, movieGenders, coverUrl)
  }

  def getMovieSynopses(document: Document): String = {
    val synopsis = document.select(".margin_20b > p:nth-child(3)").first().text()
    if (synopsis.isEmpty) {
      document.select(".margin_20b > [itemprop=description]").first().text()
    } else {
      synopsis
    }
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
