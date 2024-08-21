# Grace with Spring Boot

## Creating a new Spring Boot Application

### Creating a new app

```bash
spring init -a=gs-spring-boot -g=grace.guides -n="Grace Guide for Spring Boot" --description="Spring Boot Application with Grace Plugins" --package-name=grace.guides -l=groovy --build=gradle --format=project -t=gradle-project -d=devtools,actuator,web -x
```

### Using Spring Boot 3.3.2

In this guide, I will use Spring Boot `3.3.2`, Grace `2023.0.0` is built upon version `3.0.13`, but we can upgrade it.

```gradle
plugins {
	id 'groovy'
	id 'org.springframework.boot' version '3.3.2'
	id 'io.spring.dependency-management' version '1.1.6'
}

ext['spring-boot.version'] = '3.3.2'
ext['spring-framework.version'] = '6.1.12'

group = 'grace.guides'
version = '0.0.1-SNAPSHOT'

java {
	sourceCompatibility = '11'
}

repositories {
	mavenCentral()
}

configurations {
	developmentOlny
}

dependencies {
	developmentOlny 'org.springframework.boot:spring-boot-starter-devtools'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	implementation 'org.springframework.boot:spring-boot-starter-tomcat'
	implementation 'org.apache.groovy:groovy'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
	useJUnitPlatform()
}

```

### Using Gradle 8.10

```bash
./gradlew wrapper --gradle-version=8.10
```

First, import `grace-bom`, then adding the dependencies,

```gradle
dependencyManagement {
     imports {
          mavenBom 'org.graceframework:grace-bom:2023.0.0'
     }
}

dependencies {
	// Grace dependencies
	implementation 'org.graceframework:grace-boot'
	implementation 'org.graceframework:grace-core'
	implementation 'org.graceframework:grace-plugin-api'
	implementation 'org.graceframework:grace-plugin-core'
	implementation 'org.graceframework:grace-plugin-dynamic-modules'
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
            // Use Spring AutoConfiguration
            // languageManager(DefaultLanguageManager)
        }
    }

    void doWithDynamicModules() { {->
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

 :: Spring Boot ::                (v3.3.2)

2024-08-21T20:43:13.369+08:00  INFO 95610 --- [           main] grace.guides.GraceBootApplication        : Starting GraceBootApplication using Java 17.0.12 with PID 95610 (/Users/rain/Development/github/grace/grace-guides/gs-spring-boot/build/classes/groovy/main started by rain in /Users/rain/Development/github/grace/grace-guides/gs-spring-boot)
2024-08-21T20:43:13.370+08:00  INFO 95610 --- [           main] grace.guides.GraceBootApplication        : No active profile set, falling back to 1 default profile: "default"
2024-08-21T20:43:13.748+08:00  INFO 95610 --- [           main] g.plugins.DefaultGrailsPluginManager     : Total 3 plugins loaded successfully, take in 47 ms
2024-08-21T20:43:13.953+08:00  INFO 95610 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2024-08-21T20:43:13.959+08:00  INFO 95610 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2024-08-21T20:43:13.959+08:00  INFO 95610 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.28]
2024-08-21T20:43:13.982+08:00  INFO 95610 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2024-08-21T20:43:13.982+08:00  INFO 95610 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 595 ms
2024-08-21T20:43:14.212+08:00  WARN 95610 --- [           main] .b.a.g.t.GroovyTemplateAutoConfiguration : Cannot find template location: classpath:/templates/ (please add some templates, check your Groovy configuration, or set spring.groovy.template.check-template-location=false)
2024-08-21T20:43:14.264+08:00  INFO 95610 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 16 endpoints beneath base path '/actuator'
2024-08-21T20:43:14.296+08:00  INFO 95610 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2024-08-21T20:43:14.306+08:00  INFO 95610 --- [           main] grace.guides.GraceBootApplication        : Started GraceBootApplication in 1.063 seconds (process running for 1.357)
2024-08-21T20:43:14.306+08:00 DEBUG 95610 --- [           main] PluginsInfoApplicationContextInitializer :
----------------------------------------------------------------------------------------------
Order      Plugin Name                              Plugin Version                     Enabled
----------------------------------------------------------------------------------------------
    1      Core                                     2023.0.0                                 Y
    2      DynamicModules                           2023.0.0                                 Y
    3      Language                                 1.0.0                                    Y
----------------------------------------------------------------------------------------------

Language: key=en_US, title=English
Language: key=zh_CN, title=Chinese (Simplified Chinese)
Language: key=zh_TW, title=Chinese (Traditional Chinese)
```

### Using `plugins` Endpoint

```bash
➜  gs-spring-boot git:(main) ✗ http :8080/actuator/plugins
HTTP/1.1 200
Connection: keep-alive
Content-Type: application/json
Date: Wed, 21 Aug 2024 12:51:11 GMT
Keep-Alive: timeout=60
Transfer-Encoding: chunked

{
    "plugins": [
        {
            "dependencies": [],
            "name": "core",
            "type": "org.grails.plugins.core.CoreGrailsPlugin",
            "version": "2023.0.0"
        },
        {
            "dependencies": [],
            "name": "dynamicModules",
            "type": "org.grails.plugins.modules.DynamicModulesGrailsPlugin",
            "version": "2023.0.0"
        },
        {
            "dependencies": [],
            "name": "language",
            "type": "grace.guides.plugins.LanguageGrailsPlugin",
            "version": "1.0.0"
        }
    ]
}
```

## Links

- [Grace Framework](https://github.com/graceframework/grace-framework)
- [Grace Guides](https://github.com/grace-guides)
