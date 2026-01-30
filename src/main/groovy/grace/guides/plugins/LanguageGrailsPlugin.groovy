package grace.guides.plugins

import grails.plugins.*
import grails.util.Environment

class LanguageGrailsPlugin extends DynamicPlugin {

    def grailsVersion = "2023.0.0 > *"
    def version = "1.0.0"
    def providedModules = [LanguageModuleDescriptor]

    Closure doWithSpring() { {->
            // You can also use Spring AutoConfiguration
            // languageManager(DefaultLanguageManager)
        }
    }

    Closure doWithDynamicModules() { { ->
        // Supported Languages
        language(key: 'en_US', title: 'English', i18nNameKey: 'languages.en_US')
        language(key: 'zh_CN', title: 'Chinese (Simplified Chinese)', i18nNameKey: 'languages.zh_CN')
        language(key: 'zh_TW', title: 'Chinese (Traditional Chinese)', i18nNameKey: 'languages.zh_TW', enabled: true)
    }}

}
