plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":backend:core-api-service"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
