package com.example.dcpfm_android_9339.util

class Constants {

    companion object{
        // THIS IS THE MAIN API
        // this api doesnt display error message if credentials are incorrect
        const val BASE_URL = "https://api-test.disastercare.co.uk/api/v1/"

        // this api displays error message (test api)
//        const val BASE_URL = "https://open-api.xyz/api/"

        const val NETWORK_TIMEOUT = 3000L
        const val TESTING_NETWORK_DELAY = 0L // fake network delay for testing
        const val TESTING_CACHE_DELAY = 0L // fake cache delay for testing

    }

}