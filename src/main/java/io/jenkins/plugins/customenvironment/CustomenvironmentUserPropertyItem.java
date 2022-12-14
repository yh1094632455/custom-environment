package io.jenkins.plugins.customenvironment;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.model.AbstractDescribableImpl;
import org.kohsuke.stapler.DataBoundConstructor;
import hudson.Extension;
import hudson.model.Descriptor;

import java.io.Serializable;

public class CustomenvironmentUserPropertyItem extends AbstractDescribableImpl<CustomenvironmentUserPropertyItem> implements Serializable {
    private  String key;
    private  String value;
    public CustomenvironmentUserPropertyItem(){

    }

    @DataBoundConstructor
    public CustomenvironmentUserPropertyItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey(){
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<CustomenvironmentUserPropertyItem> {
        @NonNull
        @Override
        public String getDisplayName() {
            return "user Env";
        }
    }
}
