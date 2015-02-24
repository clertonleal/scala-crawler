/**
 * User: clertonleal
 * Date: 21/02/15
 * Time: 10:48
 */
class Cinema(cinemaName: String, cinemaAddress: Address, cinemaMovies: List[Movie]) {

  var id: Long = _
  var name: String = cinemaName
  var address = cinemaAddress
  var movies: List[Movie] = cinemaMovies

}
