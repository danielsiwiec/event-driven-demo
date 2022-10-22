dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.31")
    testImplementation("io.cucumber:cucumber-java8:7.2.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("io.rest-assured:rest-assured:4.5.1")
    testImplementation("com.google.code.gson:gson:2.9.0")
    testImplementation("org.awaitility:awaitility:4.2.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val cucumberRuntime by configurations.creating { extendsFrom(configurations["testImplementation"]) }

task("cucumber") {
    dependsOn("assemble", "compileTestJava")
    doLast {
        javaexec {
            mainClass.set("io.cucumber.core.cli.Main")
            classpath = cucumberRuntime + sourceSets.main.get().output + sourceSets.test.get().output
            args = listOf(
                "--plugin",
                "pretty",
                "--plugin",
                "html:build/cucumber-report.html",
                "--glue",
                "com.dansiwiec",
                "src/test/resources"
            )
        }
    }
}