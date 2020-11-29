package com.example.dhgas

data class Customer(var surname: String){
    var custsurname = surname
    var custFirstName = ""
    var title = ""
    var phone_1 = ""
    var phone_2 = ""
    var phone_3 = ""
    var email_1 = ""
    var email_2 = ""
    var email_3 = ""
    var billing_address_1 =""
    var billing_address_2 = ""
    var billing_address_3 = ""
    var billing_address_city = ""
    var billing_address_county = ""
    var billing_address_postcode = ""
    var active = true
    var custId = ""
    var jobAddress = false
    var jobAddressId = ""

    fun fullName(): String{
        return "$title $custFirstName $custsurname"
    }

    fun nameAndAddress(): String{
        return "${fullName()}, ${ifBlank(billing_address_1)}${ifBlank(billing_address_2)}${ifBlank(billing_address_3)}" +
                "${ifBlank(billing_address_city)}${ifBlank(billing_address_county)}$billing_address_postcode"
    }
    fun ifBlank(string: String): String{
        if (string == ""){
            return ""
        }else{
            return "$string, "
        }
    }

}