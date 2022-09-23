package io.jenkins.plugins.customenvironment;

import hudson.Extension;
import hudson.model.User;
import hudson.model.UserProperty;
import hudson.model.UserPropertyDescriptor;
import lombok.Getter;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.kohsuke.stapler.StaplerRequest;
import net.sf.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

public class CustomenvironmentUserProperty extends UserProperty {
    @Getter
    private boolean enableConfig;
    @Getter
    private List<CustomenvironmentUserPropertyItem> envItems = new ArrayList<>();
    @Getter
    private List<CustomenvironmentUserPropertyItemPasswd> envItemsPasswd = new ArrayList<>();
    @DataBoundConstructor
    public CustomenvironmentUserProperty(List<CustomenvironmentUserPropertyItem> envItems,List<CustomenvironmentUserPropertyItemPasswd> envItemsPasswd,boolean enableConfig){
        this.envItems = envItems;
        this.envItemsPasswd = envItemsPasswd;
        this.enableConfig = enableConfig;
    }

    @Extension(ordinal = 1)
    public static  final class CustomenvironmentUserPropertyDescriptor extends UserPropertyDescriptor{
        @Override
        public UserProperty newInstance(User user) {
            return new CustomenvironmentUserProperty(null,null,false);
        }
        @Override
        public UserProperty newInstance(@Nullable StaplerRequest req, @NonNull JSONObject formData) {
            try {
                return req.bindJSON(CustomenvironmentUserProperty.class, formData);
            }catch (Exception e){
                return new CustomenvironmentUserProperty(null,null,false);
            }
        }
        @NonNull
        @Override
        public String getDisplayName() {
            return Messages.CustomenvironmentUserProperty_CustomenvironmentUserPropertyDescriptor_DisplayName();
        }
    }
}
