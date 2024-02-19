package grace.guides.plugins

import grails.plugins.GrailsPlugin
import grails.plugins.descriptors.AbstractModuleDescriptor
import grails.plugins.exceptions.PluginException

class LanguageModuleDescriptor extends AbstractModuleDescriptor {

    String title

    LanguageModuleDescriptor() {
        super()
    }

    @Override
    void init(GrailsPlugin plugin, Map args) throws PluginException {
        super.init(plugin, args)
        this.title = args.title
    }

}
