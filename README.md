[![Main branch build status](https://github.com/grace-guides/gs-spring-boot/workflows/Grace%20CI/badge.svg?style=flat)](https://github.com/grace-guides/gs-spring-boot/actions?query=workflow%3A%Grace+CI%22)
[![Apache 2.0 license](https://img.shields.io/badge/License-APACHE%202.0-green.svg?logo=APACHE&style=flat)](https://opensource.org/licenses/Apache-2.0)
[![Grace Document](https://img.shields.io/badge/Grace_Document-latest-blue?style=flat&logo=asciidoctor&logoColor=E40046&labelColor=ffffff&color=f49b06)](https://guides.graceframework.org/gs-spring-boot/latest/)
[![Grace on X](https://img.shields.io/twitter/follow/graceframework?style=social)](https://x.com/graceframework)

[![Groovy Version](https://img.shields.io/badge/Groovy-5.0.8-blue?style=flat&color=4298b8)](https://groovy-lang.org/releasenotes/groovy-5.0.html)
[![Grace Version](https://img.shields.io/badge/Grace-2025.0.0-blue?style=flat&color=f49b06)](https://github.com/graceframework/grace-framework/releases/tag/v2025.0.0-M3)
[![Spring Boot Version](https://img.shields.io/badge/Spring_Boot-4.0.9-blue?style=flat&color=6db33f)](https://github.com/spring-projects/spring-boot/releases/tag/v4.0.9)

# Grace with Spring Boot

Using Grace with Spring Boot to develop a Plugin-Based Application.

## Versions

* Grace 2025.0.0-M3
* Groovy 5.0.8
* Spring Boot 4.0.9

## Ducumentation

* [2025.0.x](https://guides.graceframework.org/gs-spring-boot/latest/)

## Creating a new Spring Boot Application

### Creating a new Spring app

```bash
spring init -a=gs-spring-boot -g=grace.guides -n="Grace Guide for Spring Boot" --description="Spring Boot Application with Grace Plugins" --package-name=grace.guides -l=groovy --build=gradle --format=project -t=gradle-project -d=devtools,actuator,web -x
```

### Using Spring Boot 4.0.9

In this guide, I will use Spring Boot `4.0.9`, Grace `2025.0.0-M3` is now built upon version `4.0.9`.

```gradle
plugins {
    id 'groovy'
    id 'org.springframework.boot' version '4.0.9'
    id 'io.spring.dependency-management' version '1.1.7'
    id 'org.graceframework.grace-core' version '2024.1.2'
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
    implementation 'org.graceframework:grace-plugin-management'
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

 :: Spring Boot ::                (v4.0.9)

2026-11-20T16:23:09.675+08:00  INFO 8416 --- [           main] grace.guides.GraceBootApplication        : Starting GraceBootApplication using Java 17.0.20 with PID 8416 (/Users/grace/grace-guides/gs-spring-boot/build/classes/groovy/main started by rain in /Users/grace/grace-guides/gs-spring-boot)
2026-11-20T16:23:09.676+08:00  INFO 8416 --- [           main] grace.guides.GraceBootApplication        : No active profile set, falling back to 1 default profile: "default"
2026-11-20T16:23:10.125+08:00  INFO 8416 --- [           main] g.plugins.DefaultGrailsPluginManager     : Total 3 plugins loaded successfully, take in 66 ms
2026-11-20T16:23:10.299+08:00  INFO 8416 --- [           main] o.s.boot.tomcat.TomcatWebServer          : Tomcat initialized with port 8080 (http)
2026-11-20T16:23:10.307+08:00  INFO 8416 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2026-11-20T16:23:10.307+08:00  INFO 8416 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/11.0.27]
2026-11-20T16:23:10.330+08:00  INFO 8416 --- [           main] b.w.c.s.WebApplicationContextInitializer : Root WebApplicationContext: initialization completed in 632 ms
2026-11-20T16:23:10.562+08:00  INFO 8416 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 15 endpoints beneath base path '/actuator'
2026-11-20T16:23:10.595+08:00  INFO 8416 --- [           main] o.s.boot.tomcat.TomcatWebServer          : Tomcat started on port 8080 (http) with context path '/'
2026-11-20T16:23:10.599+08:00  INFO 8416 --- [           main] grace.guides.GraceBootApplication        : Started GraceBootApplication in 1.076 seconds (process running for 1.311)
2026-11-20T16:23:10.600+08:00 DEBUG 8416 --- [           main] PluginsInfoApplicationContextInitializer :
----------------------------------------------------------------------------------------------------------
Order      Plugin Name                        Plugin Version                                       Enabled
----------------------------------------------------------------------------------------------------------
    1      Core                               2025.0.0-M3                                                Y
    2      DynamicModules                     2025.0.0-M3                                                Y
    3      Language                           1.0.0                                                      Y
----------------------------------------------------------------------------------------------------------

Language: key=en_US, title=English
Language: key=zh_CN, title=Chinese (Simplified Chinese)
Language: key=zh_TW, title=Chinese (Traditional Chinese)
2026-11-20T16:23:16.850+08:00  INFO 8416 --- [nio-8080-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2026-11-20T16:23:16.850+08:00  INFO 8416 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2026-11-20T16:23:16.851+08:00  INFO 8416 --- [nio-8080-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms
```

### Accessing endpoints `plugins` and `info`

```bash
➜  gs-spring-boot git:(main) ✗ http :8080/actuator/plugins
HTTP/1.1 200
Connection: keep-alive
Content-Length: 359
Content-Type: application/vnd.spring-boot.actuator.v3+json
Date: Fri, 20 Nov 2026 08:23:49 GMT
Keep-Alive: timeout=60

{
    "plugins": [
        {
            "dependencies": [],
            "name": "core",
            "type": "org.grails.plugins.core.CoreGrailsPlugin",
            "version": "2025.0.0-M3"
        },
        {
            "dependencies": [],
            "name": "dynamicModules",
            "type": "org.grails.plugins.modules.DynamicModulesGrailsPlugin",
            "version": "2025.0.0-M3"
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
Content-Length: 115
Content-Type: application/vnd.spring-boot.actuator.v3+json
Date: Fri, 20 Nov 2026 08:23:16 GMT
Keep-Alive: timeout=60

{
    "app": {
        "graceVersion": "2025.0.0-M3",
        "name": "gs-spring-boot",
        "servletVersion": "6.1",
        "version": "2025.0.0-SNAPSHOT"
    }
}
```

## Links

- [Grace Framework](https://github.com/graceframework/grace-framework)
- [Grace Guides](https://github.com/grace-guides)
- [Grace Dynamic Modules Plugin](https://github.com/grace-plugins/grace-dynamic-modules)
