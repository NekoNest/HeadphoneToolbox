buildscript {
    val compose_version by extra("1.0.0-beta01")
    repositories {
        google()
        mavenCentral()
        jcenter()
        mavenLocal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.0-alpha11")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.30")
    }
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        google()
        mavenLocal()
        maven {
            url = uri("https://dl.bintray.com/amazon/PublicMaven/")
        }
    }
}

tasks.create("clean", Delete::class.java) {
    delete(rootProject.buildDir)
}