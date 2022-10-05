package io.jenkins.plugins.customenvironment;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.StaplerRequest;

import java.util.ArrayList;
import java.util.List;
@Extension
public class CustomenvironmentGlobalConfig extends GlobalConfiguration{
    public static CustomenvironmentGlobalConfig get() {
        return GlobalConfiguration.all().get(CustomenvironmentGlobalConfig.class);
    }

    private boolean enableConfig;
    private  boolean enableGlobal;
    private List<CustomenvironmentUserPropertyItem> envItems = new ArrayList<>();
    private List<CustomenvironmentUserPropertyItemPasswd> envItemsPasswd = new ArrayList<>();
    public CustomenvironmentGlobalConfig(){
        load();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        try {
            req.bindJSON(this, json);
            save();
        }catch (Exception e){

        }

        return true;
    }
    @DataBoundSetter
    public void setEnvItems(List<CustomenvironmentUserPropertyItem> envItems) {
        this.envItems = envItems;
    }

    @DataBoundSetter
    public void setEnvItemsPasswd(List<CustomenvironmentUserPropertyItemPasswd> envItemsPasswd) {
        this.envItemsPasswd = envItemsPasswd;
    }
    @DataBoundSetter
    public void setEnableConfig(boolean enableConfig){
        this.enableConfig = enableConfig;
    }
    @DataBoundSetter
    public void setEnableGlobal(boolean enableGlobal){ this.enableGlobal = enableGlobal;}
    public List<CustomenvironmentUserPropertyItem> getEnvItems() {
        return envItems;
    }
    public List<CustomenvironmentUserPropertyItemPasswd> getEnvItemsPasswd() {
        return envItemsPasswd;
    }
    public boolean getEnableConfig(){
        return enableConfig;
    }

    public boolean getEnableGlobal(){
        return enableGlobal;
    }

}
