package io.jenkins.plugins.customenvironment;

import hudson.model.Cause;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.User;
import hudson.tasks.Mailer;
import hudson.util.Secret;
import io.jenkins.cli.shaded.org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CustomenvironmentUtils {
    static public String getCause(Run<?, ?> run){
        return run.getCauses().stream().map(Cause::getShortDescription).collect(Collectors.joining());
    }
    static public User getUser(Run<?, ?> run) {
        Cause.UserIdCause userIdCause = run.getCause(Cause.UserIdCause.class);
        // 执行人信息
        User user = null;
        List<CustomenvironmentUserPropertyItem> executorMoreInfo = null;
        List<CustomenvironmentUserPropertyItemPasswd> executorMoreInfoPasswd = null;
        if (userIdCause != null && userIdCause.getUserId() != null) {
            user = User.getById(userIdCause.getUserId(), false);
        }
        if (user == null){
            String executorName = run.getCauses().stream().map(Cause::getShortDescription).collect(Collectors.joining());

            String regex = "\\[[\\w]+\\]";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(executorName);
            if (m.find()) {
                final String executorNameTmp = executorName.substring(m.start() + 1, m.end() - 1);
                final Collection<User> users = User.getAll();
                Iterator<User> iterator = users.iterator();
                while (iterator.hasNext()) {
                    User user1 = iterator.next();
                    CustomenvironmentUserProperty userProperty = user1.getProperty(CustomenvironmentUserProperty.class);
                    if (userProperty.isEnableConfig()) {
                        List<CustomenvironmentUserPropertyItem> moreInfo = userProperty.getEnvItems();
                        List<CustomenvironmentUserPropertyItemPasswd> moreInfoPasswd = userProperty.getEnvItemsPasswd();
                        if (executorNameTmp.equals(user1.getId()) || findValueInMoreInfo(moreInfo, executorNameTmp) || findValueInMoreInfoPasswd(moreInfoPasswd, executorNameTmp)) {
                            user = user1;
                        }
                    }
                }
            }
        }
        return user;
    }
    public static TreeMap<String,String> getMoreInfo(List<CustomenvironmentUserPropertyItem> moreInfo, TreeMap<String,String> result){
        for (int i = 0; i < moreInfo.size(); i++) {
            String key = moreInfo.get(i).getKey();
            String value = moreInfo.get(i).getValue();
            result.put(key,value);
            result.put("l_" + key,value);
            result.put("r_" + key,value);
        }
        return result;
    }

    public static TreeMap<String,String> getMoreInfoPasswd(List<CustomenvironmentUserPropertyItemPasswd> moreInfo, TreeMap<String,String> result){

        for (int i = 0; i < moreInfo.size(); i++) {
            String key = moreInfo.get(i).getKey();
            String value = getPasswd(moreInfo.get(i).getValue());
            result.put(key,value);
            result.put("l_" + key,value);
            result.put("r_" + key,value);
        }
        return result;
    }

    public static boolean findValueInMoreInfo(List<CustomenvironmentUserPropertyItem> moreInfo, final String value){
//        listener.getLogger().println("moreInfo" + moreInfo);
        if (moreInfo == null)
            return false;
        TreeMap<String,String> result = new TreeMap<>();
        getMoreInfo(moreInfo,result);
        final boolean[] isFind = {false};
        result.forEach((k,v)->{
            if(v.equals(value)){
                isFind[0] = true;
            }
        });
        return isFind[0];
    }

    public static String getPasswd(Secret pwd){
        return pwd.getPlainText();
    }
    private static boolean findValueInMoreInfoPasswd(List<CustomenvironmentUserPropertyItemPasswd> moreInfo, final String value){
//        listener.getLogger().println("moreInfo" + moreInfo);
        if (moreInfo == null)
            return false;
        TreeMap<String,String> result = new TreeMap<>();
        getMoreInfoPasswd(moreInfo,result);
        final boolean[] isFind = {false};
        result.forEach((k,v)->{
            if(v.equals(value)){
                isFind[0] = true;
            }
        });
        return isFind[0];
    }


}
