package io.jenkins.plugins.customenvironment;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.model.AbstractDescribableImpl;
import hudson.util.Secret;
import org.kohsuke.stapler.DataBoundConstructor;
import hudson.Extension;
import hudson.model.Descriptor;

import java.io.Serializable;

public class CustomenvironmentUserPropertyItemPasswd extends AbstractDescribableImpl<CustomenvironmentUserPropertyItemPasswd> implements Serializable {
    private  String key;
    private Secret value;
    public CustomenvironmentUserPropertyItemPasswd(){

    }

    @DataBoundConstructor
    public CustomenvironmentUserPropertyItemPasswd(String key, Secret value) {
        this.key = key;
        this.value = value;
    }

    public String getKey(){
        return this.key;
    }

    public Secret getValue() {
        return this.value;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<CustomenvironmentUserPropertyItemPasswd> {
        @NonNull
        @Override
        public String getDisplayName() {
            return "user Env";
        }
    }
}
