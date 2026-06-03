plugins {
    `java-library`
}

dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:3.3.5"))
    api(platform("org.springframework.ai:spring-ai-bom:1.0.0-M3"))
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.ai:spring-ai-openai-spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
