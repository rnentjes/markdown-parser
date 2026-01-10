import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("multiplatform") version "2.2.21"
    signing
    id("org.jetbrains.dokka") version "2.0.0"
    id("com.vanniktech.maven.publish") version "0.31.0"
}

group = "nl.astraeus"
version = "1.0.6"

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
    jvmToolchain(11)
    jvm()
    js {
        binaries.library()
        browser {}
    }
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmTest by getting
        val jsTest by getting
    }
}

publishing {
    repositories {
        maven {
            name = "gitea"
            setUrl("https://gitea.astraeus.nl/api/packages/rnentjes/maven")

            credentials {
                val giteaUsername: String? by project
                val giteaPassword: String? by project

                username = giteaUsername
                password = giteaPassword
            }
        }
        mavenLocal()
    }
}

tasks.withType<AbstractPublishToMaven> {
    dependsOn(tasks.withType<Sign>())
}

signing {
    sign(publishing.publications)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(group.toString(), name, version.toString())

    pom {
        name = "markdown-parser"
        description = "Markdown parser"
        inceptionYear = "2025"
        url = "https://gitea.astraeus.nl/rnentjes/markdown-parser"
        licenses {
            license {
                name = "MIT"
                url = "https://gitea.astraeus.nl/rnentjes/markdown-parser"
            }
        }
        developers {
            developer {
                id = "rnentjes"
                name = "Rien Nentjes"
                email = "info@nentjes.com"
            }
        }
        scm {
            url = "https://gitea.astraeus.nl/rnentjes/markdown-parser"
        }
    }
}
