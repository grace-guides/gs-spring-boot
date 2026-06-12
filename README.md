[![Main branch build status](https://github.com/grace-guides/gs-spring-boot/workflows/Grace%20CI/badge.svg?style=flat)](https://github.com/grace-guides/gs-spring-boot/actions?query=workflow%3A%Grace+CI%22)
[![Apache 2.0 license](https://img.shields.io/badge/License-APACHE%202.0-green.svg?logo=APACHE&style=flat)](https://opensource.org/licenses/Apache-2.0)
[![Grace Document](https://img.shields.io/badge/Grace_Document-latest-blue?style=flat&logo=asciidoctor&logoColor=E40046&labelColor=ffffff&color=f49b06)](https://guides.graceframework.org/gs-spring-boot/2024.0.x/)
[![Grace on X](https://img.shields.io/twitter/follow/graceframework?style=social)](https://x.com/graceframework)

[![Groovy Version](https://img.shields.io/badge/Groovy-4.0.32-blue?style=flat&color=4298b8)](https://groovy-lang.org/releasenotes/groovy-4.0.html)
[![Grace Version](https://img.shields.io/badge/Grace-2024.1.0-RC2-blue?style=flat&color=f49b06)](https://github.com/graceframework/grace-framework/releases/tag/v2024.1.0-RC2)
[![Spring Boot Version](https://img.shields.io/badge/Spring_Boot-3.5.14-blue?style=flat&color=6db33f)](https://github.com/spring-projects/spring-boot/releases/tag/v3.5.14)

# Grace with Spring Boot

Using Grace with Spring Boot to develop a Plugin-Based Application.

## Versions

* Grace 2024.1.0-RC2
* Groovy 4.0.32
* Spring Boot 3.5.14

## Ducumentation

* [2024.1.x](https://guides.graceframework.org/gs-spring-boot/2024.1.x/)

## Creating a new Spring Boot Application

### Creating a new Spring app

```bash
spring init -a=gs-spring-boot -g=grace.guides -n="Grace Guide for Spring Boot" --description="Spring Boot Application with Grace Plugins" --package-name=grace.guides -l=groovy --build=gradle --format=project -t=gradle-project -d=devtools,actuator,web -x
```

### Using Spring Boot 3.5.14

In this guide, I will use Spring Boot `3.5.14`, Grace `2024.1.0-RC2` is now built upon version `3.5.14`.

```gradle
plugins {
    id 'groovy'
    id 'org.springframework.boot' version '3.5.14'
    id 'io.spring.dependency-management' version '1.1.7'
    id 'org.graceframework.grace-core' version '2024.1.0-RC2'
}

group = 'grace.guides'
version = '0.0.1-SNAPSHOT'

java {
    sourceCompatibility = '17'
}

repositories {
    mavenCentral()
}

configurations {
    developmentOlny
}

dependencies {
    developmentOlny 'org.springframework.boot:spring-boot-starter-devtools'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.apache.groovy:groovy'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
    useJUnitPlatform()
}

```

### Adding Grace dependencies and plugins

First, import `grace-bom`, then add the Grace dependencies,

```gradle
dependencyManagement {
    imports {
        mavenBom "org.graceframework:grace-bom:$graceVersion"
    }
}

dependencies {
    // Grace dependencies
    implementation 'org.graceframework:grace-boot-plugin'
    implementation 'org.graceframework:grace-plugin-dynamic-modules'
    ...
}
```

### Enabling Endpoints

`grace-plugin-management` include a built-in endpoint `/plugins`.

```properties
management.endpoints.enabled-by-default=true
management.endpoints.web.exposure.include=*
```

### Creating a Dynamic Plugin

`LanguageGrailsPlugin` is a `DynamicPlugin`, you can register dynamic modules by providing `providedModules`

```groovy
def providedModules = [LanguageModuleDescriptor]
```

```groovy
class LanguageGrailsPlugin extends DynamicPlugin {

    def version = "1.0.0"
    def providedModules = [LanguageModuleDescriptor]

    Closure doWithSpring() { {->
            // You can also use Spring AutoConfiguration
            languageManager(DefaultLanguageManager)
        }
    }

    Closure doWithDynamicModules() { {->
        // Supported Languages
        language(key: 'en_US', title: 'English', i18nNameKey: 'languages.en_US')
        language(key: 'zh_CN', title: 'Chinese (Simplified Chinese)', i18nNameKey: 'languages.zh_CN')
        language(key: 'zh_TW', title: 'Chinese (Traditional Chinese)', i18nNameKey: 'languages.zh_TW', enabled: true)
    }}

}
```

### Using LanguageModuleDescriptor

```groovy
@SpringBootApplication
class GraceBootApplication implements CommandLineRunner {

    @Autowired
    LanguageManager languageManager

    static void main(String[] args) {
        SpringApplication.run(GraceBootApplication, args)
    }

    @Override
    void run(String... args) throws Exception {
        List<LanguageModuleDescriptor> languageModuleDescriptors = this.languageManager.getLanguages()

        languageModuleDescriptors.each { md ->
            println "Language: key=$md.key, title=$md.title"
        }
    }

}
```

### Starting

```bash
➜  gs-spring-boot git:(main) ✗ ./gradlew bootRun

> Task :bootRun

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::               (v3.5.14)

2026-06-12T10:06:42.221+08:00  INFO 7325 --- [           main] grace.guides.GraceBootApplication        : Starting GraceBootApplication using Java 17.0.19 with PID 7325 (/Users/rain/Development/github/grace/grace-guides/gs-spring-boot/build/classes/groovy/main started by rain in /Users/rain/Development/github/grace/grace-guides/gs-spring-boot)
2026-06-12T10:06:42.222+08:00  INFO 7325 --- [           main] grace.guides.GraceBootApplication        : No active profile set, falling back to 1 default profile: "default"
2026-06-12T10:06:42.704+08:00  INFO 7325 --- [           main] g.plugins.DefaultGrailsPluginManager     : Total 3 plugins loaded successfully, take in 60 ms
2026-06-12T10:06:42.861+08:00  INFO 7325 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2026-06-12T10:06:42.866+08:00  INFO 7325 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2026-06-12T10:06:42.866+08:00  INFO 7325 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.54]
2026-06-12T10:06:42.886+08:00  INFO 7325 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2026-06-12T10:06:42.886+08:00  INFO 7325 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 647 ms
2026-06-12T10:06:43.190+08:00  INFO 7325 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 16 endpoints beneath base path '/actuator'
2026-06-12T10:06:43.217+08:00  INFO 7325 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2026-06-12T10:06:43.226+08:00  INFO 7325 --- [           main] grace.guides.GraceBootApplication        : Started GraceBootApplication in 1.158 seconds (process running for 1.383)
2026-06-12T10:06:43.227+08:00 DEBUG 7325 --- [           main] PluginsInfoApplicationContextInitializer :
----------------------------------------------------------------------------------------------------------
Order      Plugin Name                        Plugin Version                                       Enabled
----------------------------------------------------------------------------------------------------------
    1      Core                               2024.1.0-RC2                                               Y
    2      DynamicModules                     2024.1.0-RC2                                               Y
    3      Language                           1.0.0                                                      Y
----------------------------------------------------------------------------------------------------------

Language: key=en_US, title=English
Language: key=zh_CN, title=Chinese (Simplified Chinese)
Language: key=zh_TW, title=Chinese (Traditional Chinese)
```

### Accessing endpoints `plugins` and `info`

```bash
➜  gs-spring-boot git:(main) ✗ http :8080/actuator/plugins
HTTP/1.1 200
Connection: keep-alive
Content-Type: application/json
Date: Fri, 12 Jun 2026 02:06:52 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked

{
    "plugins": [
        {
            "dependencies": [],
            "name": "core",
            "type": "org.grails.plugins.core.CoreGrailsPlugin",
            "version": "2024.1.0-RC2"
        },
        {
            "dependencies": [],
            "name": "dynamicModules",
            "type": "org.grails.plugins.modules.DynamicModulesGrailsPlugin",
            "version": "2024.1.0-RC2"
        },
        {
            "dependencies": [],
            "name": "language",
            "type": "grace.guides.plugins.LanguageGrailsPlugin",
            "version": "1.0.0"
        }
    ]
}

➜  gs-spring-boot git:(main) ✗ http :8080/actuator/info
HTTP/1.1 200
Connection: keep-alive
Content-Type: application/json
Date: Fri, 12 Jun 2026 02:06:55 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked

{
    "app": {
        "graceVersion": "2024.1.0-RC2",
        "name": "gs-spring-boot",
        "servletVersion": "6.0",
        "version": "2024.1.0-SNAPSHOT"
    }
}
```

## Links

- [Grace Framework](https://github.com/graceframework/grace-framework)
- [Grace Guides](https://github.com/grace-guides)
- [Grace Dynamic Modules Plugin](https://github.com/grace-plugins/grace-dynamic-modules)
