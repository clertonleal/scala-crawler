/**
 * User: clertonleal
 * Date: 21/02/15
 * Time: 13:35
 */
class Movie(movieTitle: String, movieSynopsis: String, movieDurationInMinutes: Int, movieDirector: String, movieGender: String) {

  var id: Long = _
  var title = movieTitle
  var synopsis = movieSynopsis
  var durationInMinutes = movieDurationInMinutes
  var director = movieDirector
  var gender = movieGender

}
