/**
 * User: clertonleal
 * Date: 21/02/15
 * Time: 11:56
 */
class Address(addressStreet: String, addressCep: String) {

  var id: Long = _
  var street = addressStreet
  var city = "Fortaleza"
  var state = "Ceará"
  var cep = addressCep

}
