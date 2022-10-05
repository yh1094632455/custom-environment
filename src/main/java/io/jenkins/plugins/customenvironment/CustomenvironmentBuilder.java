package io.jenkins.plugins.customenvironment;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.EnvVars;
import hudson.Extension;
import hudson.console.ConsoleLogFilter;
import hudson.console.LineTransformationOutputStream;
import hudson.model.*;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.tasks.Mailer;
import hudson.util.Secret;
import io.jenkins.cli.shaded.org.apache.commons.lang.StringUtils;
import jenkins.tasks.SimpleBuildWrapper;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CustomenvironmentBuilder extends SimpleBuildWrapper {
    @DataBoundConstructor
    public CustomenvironmentBuilder(){

    }
    @Override
    public ConsoleLogFilter createLoggerDecorator(@NonNull Run<?, ?> build) {
        CustomenvironmentGlobalConfig config = CustomenvironmentGlobalConfig.get();
        Set<String> allPassword = new HashSet<>();
        List<String> secrets = new ArrayList<>();
        if (config.getEnableConfig()) {
            List<CustomenvironmentUserPropertyItemPasswd> passwds = config.getEnvItemsPasswd();
            if(passwds != null) {
                for (int i = 0; i < passwds.size(); i++) {
                    allPassword.add(passwds.get(i).getValue().getPlainText());
                }
            }
        }
        User user = causeUser;
        if (user != null) {
            if(user.getProperty(CustomenvironmentUserProperty.class).isEnableConfig()) {
                List<CustomenvironmentUserPropertyItemPasswd> passwds = user.getProperty(CustomenvironmentUserProperty.class).getEnvItemsPasswd();
                if (passwds != null) {
                    for (int i = 0; i < passwds.size(); i++) {
                        allPassword.add(passwds.get(i).getValue().getPlainText());
                    }
                }
            }
        }
        Iterator<String> iterator = allPassword.iterator();
        while (iterator.hasNext()) {
            secrets.add(iterator.next());
        }
        return new FilterImpl(secrets, null);

    }

    private User causeUser;
    private String cause;
    @Override
    public void setUp(Context context, Run<?, ?> build, TaskListener listener, EnvVars initialEnvironment) throws IOException, InterruptedException {
//        super.setUp(context, build, listener, initialEnvironment);
//        listener.getLogger().println("setup");
//        listener.getLogger().println(build.getCauses().stream().map(Cause::getShortDescription).collect(Collectors.joining()));
//        Cause.UserIdCause userIdCause = build.getCause(Cause.UserIdCause.class);
//        if(userIdCause != null){
//            listener.getLogger().println("setup123");
//            listener.getLogger().println(userIdCause.getUserId());
//            listener.getLogger().println(userIdCause.getUserName());
//            User u = User.getById(userIdCause.getUserId(), false);
//            if(u != null){
//                listener.getLogger().println("setup1234");
//            }
//        }
        User user = CustomenvironmentUtils.getUser(build);
        cause = build.getCauses().stream().map(Cause::getShortDescription).collect(Collectors.joining());
        causeUser = user;
        if(user != null)
        {
            listener.getLogger().println("test");
        }else {
            listener.getLogger().println("test2");
        }
        CustomenvironmentGlobalConfig config = CustomenvironmentGlobalConfig.get();
        if(config.getEnableConfig() && causeUser != null){
//                    listener.getLogger().println("全局配置已经启用");
            List<CustomenvironmentUserPropertyItem> items = config.getEnvItems();
            if(items != null) {
                for (int i = 0; i < items.size(); i++) {
                    CustomenvironmentUserPropertyItem item = items.get(i);
                    listener.getLogger().println(item.getKey() + item.getValue());
                }
            }
            List<CustomenvironmentUserPropertyItemPasswd> itemsPasswd = config.getEnvItemsPasswd();
            if(itemsPasswd != null) {
                for (int i = 0; i < itemsPasswd.size(); i++) {
                    CustomenvironmentUserPropertyItemPasswd item = itemsPasswd.get(i);
                    listener.getLogger().println(item.getKey() + item.getValue().getPlainText());
                }
            }

        }else{
//                    listener.getLogger().println("全局配置没有启用");
        }
        if(user != null){
            if(user.getProperty(CustomenvironmentUserProperty.class).isEnableConfig()) {
                List<CustomenvironmentUserPropertyItemPasswd> passwds = user.getProperty(CustomenvironmentUserProperty.class).getEnvItemsPasswd();
                List<CustomenvironmentUserPropertyItem> items = user.getProperty(CustomenvironmentUserProperty.class).getEnvItems();
                if(passwds != null){
                    for (int i = 0; i < passwds.size(); i++) {
                        listener.getLogger().println(passwds.get(i).getKey() + passwds.get(i).getValue().getPlainText());
                    }
                }
                if(items != null){
                    for (int i = 0; i < items.size(); i++) {
                        listener.getLogger().println(items.get(i).getKey() + items.get(i).getValue());
                    }
                }
            }
        }
    }

    @Override
    public boolean requiresWorkspace() {
        return false;
    }

    @Override
    public void makeSensitiveBuildVariables(AbstractBuild build, Set<String> sensitiveVariables) {
        User user = causeUser;
//        LOGGER.log("make");
        CustomenvironmentGlobalConfig config = CustomenvironmentGlobalConfig.get();
        if(config.getEnableConfig()){
            List<CustomenvironmentUserPropertyItemPasswd> itemsPasswd = config.getEnvItemsPasswd();
            if(itemsPasswd != null){
                for (int i = 0; i < itemsPasswd.size(); i++) {
                    sensitiveVariables.add(itemsPasswd.get(i).getKey());
                    sensitiveVariables.add("g_" + itemsPasswd.get(i).getKey());
                }
            }
        }
        if(causeUser != null) {
            if(user.getProperty(CustomenvironmentUserProperty.class).isEnableConfig()) {
                List<CustomenvironmentUserPropertyItemPasswd> passwds = user.getProperty(CustomenvironmentUserProperty.class).getEnvItemsPasswd();
                if (passwds != null) {
                    for (int i = 0; i < passwds.size(); i++) {
                        sensitiveVariables.add(passwds.get(i).getKey());
                    }
                }
            }
        }
//        super.makeSensitiveBuildVariables(build, sensitiveVariables);
    }

    @Override
    public void makeBuildVariables(AbstractBuild build, Map<String, String> variables) {
        LOGGER.fine("makeBuildVariables");
        Cause.UserIdCause userIdCause = (Cause.UserIdCause) build.getCause(Cause.UserIdCause.class);
        User user = causeUser;
        CustomenvironmentGlobalConfig config = CustomenvironmentGlobalConfig.get();
        variables.put(Constants.Build_Causes,cause);
        variables.put(Constants.Build_Page,build.getParent().getAbsoluteUrl() + build.getId());
        variables.put(Constants.Build_Console,build.getParent().getAbsoluteUrl() + build.getId() + "/console");
        if(config.getEnableConfig() && causeUser != null){
//                    listener.getLogger().println("全局配置已经启用");
            List<CustomenvironmentUserPropertyItem> items = config.getEnvItems();
            if(items != null) {
                for (int i = 0; i < items.size(); i++) {
                    CustomenvironmentUserPropertyItem item = items.get(i);
                    variables.put("g_" + item.getKey(), item.getValue());
                    variables.put(item.getKey(), item.getValue());
                }
            }
            List<CustomenvironmentUserPropertyItemPasswd> itemsPasswd = config.getEnvItemsPasswd();
            if(itemsPasswd != null) {
                for (int i = 0; i < itemsPasswd.size(); i++) {
                    CustomenvironmentUserPropertyItemPasswd item = itemsPasswd.get(i);
                    variables.put("g_" + item.getKey(), CustomenvironmentUtils.getPasswd(item.getValue()));
                    variables.put(item.getKey(), CustomenvironmentUtils.getPasswd(item.getValue()));
                }
            }

        }else{
//                    listener.getLogger().println("全局配置没有启用");
        }
        if(user != null){
            variables.put(Constants.Build_User_Display_Name,user.getDisplayName());
            variables.put(Constants.Build_User_Full_Name,user.getFullName());
            variables.put(Constants.Build_User_ID,user.getId());
            Mailer.UserProperty prop = user.getProperty(Mailer.UserProperty.class);
            if (null != prop) {
                String adress = StringUtils.trimToEmpty(prop.getAddress());
                variables.put(Constants.Build_User_Email, adress);
            }
            if(user.getProperty(CustomenvironmentUserProperty.class).isEnableConfig()) {
                List<CustomenvironmentUserPropertyItemPasswd> passwds = user.getProperty(CustomenvironmentUserProperty.class).getEnvItemsPasswd();
                List<CustomenvironmentUserPropertyItem> items = user.getProperty(CustomenvironmentUserProperty.class).getEnvItems();
                if(passwds != null){
                    for (int i = 0; i < passwds.size(); i++) {
                        variables.put(passwds.get(i).getKey(), passwds.get(i).getValue().getPlainText());
                    }
                }
                if(items != null){
                    for (int i = 0; i < items.size(); i++) {
                        variables.put(items.get(i).getKey(), items.get(i).getValue());
                    }
                }
            }
        }
        super.makeBuildVariables(build, variables);
    }

    private final class FilterImpl extends ConsoleLogFilter implements Serializable {

        private static final long serialVersionUID = 1L;

        private final List<Secret> allPasswords;
        private final List<String> allRegexes;

        FilterImpl(List<String> allPasswords, List<String> allRegexes) {
            this.allPasswords = new ArrayList<Secret>();
            this.allRegexes = new ArrayList<String>();
            for (String password : allPasswords) {
                this.allPasswords.add(Secret.fromString(password));
            }
            if(allRegexes != null) {
                for (String regex : allRegexes) {
                    this.allRegexes.add(regex);
                }
            }
        }

        @Override
        public OutputStream decorateLogger(Run run, OutputStream logger) {
            List<String> passwords = new ArrayList<String>();
            List<String> regexes = new ArrayList<String>();
            for (Secret password : allPasswords) {
                passwords.add(password.getPlainText());
            }
            for (String regex : allRegexes) {
                regexes.add(regex);
            }
            String runName = run != null ? run.getFullDisplayName() : "";
            return new MaskPasswordsOutputStream(logger, passwords, regexes, runName);
        }

    }
    public class MaskPasswordsOutputStream extends LineTransformationOutputStream {

        private final OutputStream logger;
        private final List<Pattern> passwordsAsPatterns;
        private final String runName;

        /**
         * @param logger The output stream to which this {@link MaskPasswordsOutputStream}
         *               will write to
         * @param passwords A collection of {@link String}s to be masked
         * @param regexes A collection of Regular Expression {@link String}s to be masked
         * @param runName A string representation of the Run/Build the output stream logger is associated with. Used for logging purposes.
         */
        public MaskPasswordsOutputStream(OutputStream logger, @CheckForNull Collection<String> passwords, @CheckForNull Collection<String> regexes, String runName) {
            this.logger = logger;
            this.runName = (runName != null) ? runName : "";
            passwordsAsPatterns = new ArrayList<>();

            if (passwords != null) {
                // Passwords aggregated into single regex which is compiled as a pattern for efficiency
                StringBuilder pwRegex = new StringBuilder().append('(');
                int pwCount = 0;
                for (String pw : passwords) {
                    if (StringUtils.isNotEmpty(pw)) {
                        pwCount++;
                        pwRegex.append(Pattern.quote(pw));
                        pwRegex.append('|');
                        try {
                            String encodedPassword = URLEncoder.encode(pw, "UTF-8");
                            if (!encodedPassword.equals(pw)) {
                                pwRegex.append(Pattern.quote(encodedPassword));
                                pwRegex.append('|');
                            }
                        } catch (UnsupportedEncodingException e) {
                            // ignore any encoding problem => status quo
                        }
                    }
                }
                if (pwCount > 0) {
                    pwRegex.deleteCharAt(pwRegex.length()-1); // removes the last unuseful pipe
                    pwRegex.append(')');
                    passwordsAsPatterns.add(Pattern.compile(pwRegex.toString()));
                }
            }
            if (regexes != null) {
                for (String r: regexes) {
                    passwordsAsPatterns.add(Pattern.compile(r));
                }
            }

        }

        /**
         * @param logger The output stream to which this {@link MaskPasswordsOutputStream}
         *               will write to
         * @param passwords A collection of {@link String}s to be masked
         */
        public MaskPasswordsOutputStream(OutputStream logger, @CheckForNull Collection<String> passwords) {
            this(logger, passwords, null);
        }

        public MaskPasswordsOutputStream(OutputStream logger, @CheckForNull Collection<String> passwords, @CheckForNull Collection<String> regexes) {
            this(logger, passwords, regexes, "");
        }

        public List<String> patternMatch(List<Pattern> ps, String s) {
            List<String> ret = new ArrayList<>();
            for (Pattern p: ps) {
                Matcher m = p.matcher(s);
                while (m.find()) { // Regex matches
                    if (m.groupCount() > 0) { // Regex contains group(s)
                        for (int i = 1; i <= m.groupCount(); i++) {
                            String toAdd = m.group(i);
                            if (toAdd != null) {
                                ret.add(toAdd);
                            }
                        }
                    } else { // Regex doesn't contain groups, match entire Regex string
                        ret.add(m.group(0));
                    }
                }
            }
            return ret;
        }
        public List<String> patternMatch(Pattern p, String s) {
            return patternMatch(Arrays.asList(p), s);
        }

        public String secretsMask(List<String> secrets, String s, String runName) {
            String MASKED_STRING = "********";
            if (secrets != null && secrets.size() > 0) {
                for (String secret: secrets) {
                    s = s.replaceAll(Pattern.quote(secret), MASKED_STRING);
                }
//                LOGGER.info(String.format("Masking Run[%s]'s line: %s", runName, StringUtils.strip(s)));
            }
            return s;
        }
        public  String secretsMaskPatterns(List<Pattern> ps, String s, String runName) {
            return StringUtils.isNotBlank(s) ? secretsMask(patternMatch(ps, s), s, runName) : s;
        }
        // TODO: The logic relies on the default encoding, which may cause issues when master and agent have different encodings
        @SuppressFBWarnings(value = "DM_DEFAULT_ENCODING", justification = "Open TODO item for wider rework")
        @Override
        protected void eol(byte[] bytes, int len) throws IOException {
            String line = new String(bytes, 0, len);
            if(passwordsAsPatterns != null && line != null) {
                line = secretsMaskPatterns(passwordsAsPatterns, line, runName);
            }
            logger.write(line.getBytes());
        }

        /**
         * {@inheritDoc}
         * @throws IOException
         */
        @Override
        public void close() throws IOException {
            super.close();
            logger.close();
        }

        /**
         * {@inheritDoc}
         * @throws IOException
         */
        @Override
        public void flush() throws IOException {
            super.flush();
            logger.flush();
        }
    }
    @Extension(ordinal = 100) // JENKINS-12161, was previously 1000 but that made the system configuration page look weird
    public static final class DescriptorImpl extends BuildWrapperDescriptor {

        public DescriptorImpl() {
            super(CustomenvironmentBuilder.class);
        }
        @Override
        public boolean isApplicable(AbstractProject<?, ?> abstractProject) {
            return true;
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return Messages.CustomenvironmentBuilder_DescriptorImpl_DisplayName();
        }
    }
    private static final Logger LOGGER = Logger.getLogger(CustomenvironmentBuilder.class.getName());
}
