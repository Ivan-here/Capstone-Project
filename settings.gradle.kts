pluginManagement {
    plugins {
        kotlin("jvm") version "1.9.24"
    }
}
rootProject.name = "CapstoneProject"
include("listing-service","verification-service","identity-service","api-gateway","profile-service","order_reservation-service","notifications-service" )
include("admin-service")