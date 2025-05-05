[![Main branch build status](https://github.com/grace-guides/gs-spring-boot/workflows/Grace%20CI/badge.svg?style=flat)](https://github.com/grace-guides/gs-spring-boot/actions?query=workflow%3A%Grace+CI%22)
[![Apache 2.0 license](https://img.shields.io/badge/License-APACHE%202.0-green.svg?logo=APACHE&style=flat)](https://opensource.org/licenses/Apache-2.0)
[![Grace on X](https://img.shields.io/twitter/follow/graceframework?style=social)](https://twitter.com/graceframework)

[![Groovy Version](https://img.shields.io/badge/Groovy-4.0.26-blue?style=flat&color=4298b8)](https://groovy-lang.org/releasenotes/groovy-4.0.html)
[![Grace Version](https://img.shields.io/badge/Grace-2023.3.0-blue?style=flat&color=f49b06)](https://github.com/graceframework/grace-framework/releases/tag/v2023.3.0-RC1)
[![Spring Boot Version](https://img.shields.io/badge/Spring_Boot-3.3.11-blue?style=flat&color=6db33f)](https://github.com/spring-projects/spring-boot/releases/tag/v3.3.11)

# Grace with Spring Boot

## Versions

* Grace 2023.3.0-RC1
* Groovy 4.0.26
* Spring Boot 3.3.11

## Creating a new Spring Boot Application

### Creating a new Spring app

```bash
spring init -a=gs-spring-boot -g=grace.guides -n="Grace Guide for Spring Boot" --description="Spring Boot Application with Grace Plugins" --package-name=grace.guides -l=groovy --build=gradle --format=project -t=gradle-project -d=devtools,actuator,web -x
```

### Using Spring Boot 3.3.11

In this guide, I will use Spring Boot `3.3.11`, Grace `2023.3.0-RC1` is now built upon version `3.3.11`.

```gradle
plugins {
	id 'groovy'
	id 'org.springframework.boot' version '3.3.11'
	id 'io.spring.dependency-management' version '1.1.7'
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
	implementation 'org.graceframework:grace-boot'
	implementation 'org.graceframework:grace-plugin-core'
	implementation 'org.graceframework:grace-plugin-management'
	implementation 'org.graceframework.plugins:dynamic-modules:1.0.0-M1'
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

 :: Spring Boot ::               (v3.3.11)

2025-05-05T21:56:11.205+08:00  INFO 19482 --- [           main] grace.guides.GraceBootApplication        : Starting GraceBootApplication using Java 17.0.15 with PID 19482 (/Users/rain/Development/github/grace/grace-guides/gs-spring-boot/build/classes/groovy/main started by rain in /Users/rain/Development/github/grace/grace-guides/gs-spring-boot)
2025-05-05T21:56:11.206+08:00  INFO 19482 --- [           main] grace.guides.GraceBootApplication        : No active profile set, falling back to 1 default profile: "default"
2025-05-05T21:56:11.595+08:00  INFO 19482 --- [           main] g.plugins.DefaultGrailsPluginManager     : Total 3 plugins loaded successfully, take in 45 ms
2025-05-05T21:56:11.795+08:00  INFO 19482 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2025-05-05T21:56:11.801+08:00  INFO 19482 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2025-05-05T21:56:11.801+08:00  INFO 19482 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.40]
2025-05-05T21:56:11.821+08:00  INFO 19482 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2025-05-05T21:56:11.821+08:00  INFO 19482 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 599 ms
2025-05-05T21:56:12.092+08:00  INFO 19482 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 16 endpoints beneath base path '/actuator'
2025-05-05T21:56:12.128+08:00  INFO 19482 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2025-05-05T21:56:12.137+08:00  INFO 19482 --- [           main] grace.guides.GraceBootApplication        : Started GraceBootApplication in 1.066 seconds (process running for 1.355)
2025-05-05T21:56:12.138+08:00 DEBUG 19482 --- [           main] PluginsInfoApplicationContextInitializer :
----------------------------------------------------------------------------------------------------------
Order      Plugin Name                        Plugin Version                                       Enabled
----------------------------------------------------------------------------------------------------------
    1      Core                               2023.3.0-RC1                                               Y
    2      DynamicModules                     1.0.0-M1                                                   Y
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
Date: Mon, 05 May 2025 13:56:22 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked

{
    "plugins": [
        {
            "dependencies": [],
            "name": "core",
            "type": "org.grails.plugins.core.CoreGrailsPlugin",
            "version": "2023.3.0-RC1"
        },
        {
            "dependencies": [],
            "name": "dynamicModules",
            "type": "org.grails.plugins.modules.DynamicModulesGrailsPlugin",
            "version": "1.0.0-M1"
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
Date: Mon, 05 May 2025 13:56:44 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked

{
    "app": {
        "grailsVersion": "2023.3.0-RC1",
        "name": "grailsApplication",
        "servletVersion": "6.0"
    }
}
```

## Links

- [Grace Framework](https://github.com/graceframework/grace-framework)
- [Grace Guides](https://github.com/grace-guides)
- [Grace Dynamic Modules Plugin](https://github.com/grace-plugins/grace-dynamic-modules)
