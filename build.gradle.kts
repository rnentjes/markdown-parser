plugins {
    kotlin("multiplatform") version "2.1.10"
}

group = "nl.astraeus"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://gitea.astraeus.nl/api/packages/rnentjes/maven")
    }
    maven {
        url = uri("https://gitea.astraeus.nl:8443/api/packages/rnentjes/maven")
    }
}

kotlin {
    jvmToolchain(17)
    jvm()
    js {
        binaries.executable()
        browser {
            distribution {
                outputDirectory.set(File("$projectDir/web/"))
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("nl.astraeus:kotlin-simple-logging:1.1.1")
                api("nl.astraeus:kotlin-css-generator:1.0.10")
            }
        }
        val commonTest by getting
        val jvmMain by getting {
            dependencies {
                implementation("io.undertow:undertow-core:2.3.14.Final")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.11.0")

                implementation("org.xerial:sqlite-jdbc:3.32.3.2")
                implementation("com.zaxxer:HikariCP:4.0.3")
                implementation("nl.astraeus:simple-jdbc-stats:1.6.1") {
                    exclude(group = "org.slf4j", module = "slf4j-api")
                }
            }
        }
        val jvmTest by getting {
            dependencies {
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("nl.astraeus:kotlin-komponent:1.2.5")
            }
        }
        val jsTest by getting
    }
}
