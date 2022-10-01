package io.jenkins.plugins.customenvironment;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Launcher;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.*;
import hudson.tasks.Builder;
import hudson.model.Cause.UserIdCause;
import hudson.util.Secret;
import io.jenkins.cli.shaded.org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import hudson.tasks.Mailer;
import java.io.IOException;
import java.util.List;
import java.util.Collection;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jenkins.tasks.SimpleBuildStep;

public class CustomenvironmentBuilder extends Builder implements SimpleBuildStep {

//    private final String name;
//    private boolean useFrench;

    @DataBoundConstructor
    public CustomenvironmentBuilder() {
    }

//    public String getName() {
//        return name;
//    }
//
//    public boolean isUseFrench() {
//        return useFrench;
//    }

//    @DataBoundSetter
//    public void setUseFrench(boolean useFrench) {
//        this.useFrench = useFrench;
//    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
//        getUser(run,listener,env);
//        if (useFrench) {
//            listener.getLogger().println("Bonjour, " + name + "!");
//        } else {
//            listener.getLogger().println("Hello, " + name + "!");
//        }



    }
    static private void getUser(Run<?, ?> run, TaskListener listener,EnvVars env) {
        UserIdCause userIdCause = run.getCause(UserIdCause.class);
        // 执行人信息
        User user = null;
        final String[] users1 = {null};
        String executorName = null;
        List<CustomenvironmentUserPropertyItem> executorMoreInfo = null;
        List<CustomenvironmentUserPropertyItemPasswd> executorMoreInfoPasswd = null;
        if (userIdCause != null && userIdCause.getUserId() != null) {
            user = User.getById(userIdCause.getUserId(), false);
        }
        env.put(Constants.Build_Causes,run.getCauses().stream().map(Cause::getShortDescription).collect(Collectors.joining()));
        env.put(Constants.Build_Page,run.getParent().getAbsoluteUrl() + run.getId());
        env.put(Constants.Build_Console,run.getParent().getAbsoluteUrl() + run.getId() + "/console");
        if (user == null) {
            executorName = run.getCauses().stream().map(Cause::getShortDescription).collect(Collectors.joining());

            String regex   = "\\[[\\w]+\\]";
            Pattern p  = Pattern.compile(regex);
            Matcher m   = p.matcher(executorName);
            if (m.find())  {
                final String executorNameTmp = executorName.substring(m.start() + 1,  m.end() - 1);
                final Collection<User> users = User.getAll();
                users.forEach(user1 -> {
                    CustomenvironmentUserProperty userProperty =  user1.getProperty(CustomenvironmentUserProperty.class);
                    if(userProperty.isEnableConfig()){
                        List<CustomenvironmentUserPropertyItem> moreInfo = userProperty.getEnvItems();
                        List<CustomenvironmentUserPropertyItemPasswd> moreInfoPasswd = userProperty.getEnvItemsPasswd();
                        if(executorNameTmp.equals(user1.getId()) || findValueInMoreInfo(moreInfo,executorNameTmp,listener) ||findValueInMoreInfoPasswd(moreInfoPasswd,executorNameTmp,listener)){
                            users1[0] = user1.getId();
                        }
                    }

                });
            }
            if(users1[0] != null){
                user = User.getById(users1[0], false);
                executorName = user.getDisplayName();
            }
        } else {
            executorName = user.getDisplayName();
        }
        if(user != null){

            env.put(Constants.Build_User_Display_Name,executorName);
            env.put(Constants.Build_User_Full_Name,user.getFullName());
            env.put(Constants.Build_User_ID,user.getId());
            Mailer.UserProperty prop = user.getProperty(Mailer.UserProperty.class);
            if (null != prop) {
                String adress = StringUtils.trimToEmpty(prop.getAddress());
                env.put(Constants.Build_User_Email, adress);
            }
            if(user.getProperty(CustomenvironmentUserProperty.class).isEnableConfig()){
                executorMoreInfo = user.getProperty(CustomenvironmentUserProperty.class).getEnvItems();
                executorMoreInfoPasswd = user.getProperty(CustomenvironmentUserProperty.class).getEnvItemsPasswd();
                if(executorMoreInfo != null){
                    getMoreInfo(executorMoreInfo,env,listener);
                }
                if(executorMoreInfoPasswd != null){
                    getMoreInfoPasswd(executorMoreInfoPasswd,env,listener);
                }
            }

        }

        return;

    }

    private static TreeMap<String,String> getMoreInfo(List<CustomenvironmentUserPropertyItem> moreInfo, TreeMap<String,String> result, TaskListener listener){
        for (int i = 0; i < moreInfo.size(); i++) {
            String key = moreInfo.get(i).getKey();
            String value = moreInfo.get(i).getValue();
            result.put(key,value);
            result.put("l_" + key,value);
            result.put("r_" + key,value);
        }
        return result;
    }

    private static TreeMap<String,String> getMoreInfoPasswd(List<CustomenvironmentUserPropertyItemPasswd> moreInfo, TreeMap<String,String> result, TaskListener listener){

        for (int i = 0; i < moreInfo.size(); i++) {
            String key = moreInfo.get(i).getKey();
            String value = getPasswd(moreInfo.get(i).getValue());
            result.put(key,value);
            result.put("l_" + key,value);
            result.put("r_" + key,value);
        }
        return result;
    }

    private static boolean findValueInMoreInfo(List<CustomenvironmentUserPropertyItem> moreInfo, final String value, TaskListener listener){
//        listener.getLogger().println("moreInfo" + moreInfo);
        if (moreInfo == null)
            return false;
        TreeMap<String,String> result = new TreeMap<>();
        getMoreInfo(moreInfo,result,null);
        final boolean[] isFind = {false};
        result.forEach((k,v)->{
            if(v.equals(value)){
                isFind[0] = true;
            }
        });
        return isFind[0];
    }

    private static String getPasswd(Secret pwd){
        return pwd.getPlainText();
    }
    private static boolean findValueInMoreInfoPasswd(List<CustomenvironmentUserPropertyItemPasswd> moreInfo, final String value, TaskListener listener){
//        listener.getLogger().println("moreInfo" + moreInfo);
        if (moreInfo == null)
            return false;
        TreeMap<String,String> result = new TreeMap<>();
        getMoreInfoPasswd(moreInfo,result,null);
        final boolean[] isFind = {false};
        result.forEach((k,v)->{
            if(v.equals(value)){
                isFind[0] = true;
            }
        });
        return isFind[0];
    }
//    @Symbol("greet")
//    @Extension
//    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
//
//        @Override
//        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
//            return true;
//        }
//
//        @Override
//        public String getDisplayName() {
//            return Messages.CustomenvironmentBuilder_DescriptorImpl_DisplayName();
//        }
//
//    }

    @Extension
    public static class BuildUserVarsEnvironmentContributor extends EnvironmentContributor {
        @Override
        public void buildEnvironmentFor(@NonNull Run r, @NonNull EnvVars envs, @NonNull TaskListener listener) throws IOException, InterruptedException {
//            super.buildEnvironmentFor(r, envs, listener);

                CustomenvironmentGlobalConfig config = CustomenvironmentGlobalConfig.get();

                if(config.getEnableConfig()){
//                    listener.getLogger().println("全局配置已经启用");
                    List<CustomenvironmentUserPropertyItem> items = config.getEnvItems();
                    for (int i = 0; i < items.size(); i++) {
                        CustomenvironmentUserPropertyItem item = items.get(i);
                        envs.put("g_" + item.getKey(), item.getValue());
                        envs.put("r_" + item.getKey(), item.getValue());
                        envs.put(item.getKey(), item.getValue());
                    }
                    List<CustomenvironmentUserPropertyItemPasswd> itemsPasswd = config.getEnvItemsPasswd();
                    for (int i = 0; i < itemsPasswd.size(); i++) {
                        CustomenvironmentUserPropertyItemPasswd item = itemsPasswd.get(i);
                        envs.put("g_" + item.getKey(), getPasswd(item.getValue()));
                        envs.put("r_" + item.getKey(), getPasswd(item.getValue()));
                        envs.put(item.getKey(), getPasswd(item.getValue()));
                    }

                }else{
//                    listener.getLogger().println("全局配置没有启用");
                }
            getUser(r, listener, envs);

        }
    }

}
