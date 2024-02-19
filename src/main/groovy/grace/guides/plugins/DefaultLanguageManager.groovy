package grace.guides.plugins

import grails.plugins.GrailsPluginManager
import grails.plugins.PluginManagerAware

class DefaultLanguageManager implements LanguageManager, PluginManagerAware {

    private GrailsPluginManager pluginManager

    DefaultLanguageManager() {
    }

    DefaultLanguageManager(GrailsPluginManager pluginManager) {
        this.pluginManager = pluginManager
    }

    @Override
    List<LanguageModuleDescriptor> getLanguages() {
        this.pluginManager.getEnabledModuleDescriptorsByClass(LanguageModuleDescriptor)
    }

    @Override
    void setPluginManager(GrailsPluginManager pluginManager) {
        this.pluginManager = pluginManager
    }

}
