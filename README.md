# ConfigEnvironmentInjector

## Introduction
- You can customize a username and password for each user for your build tasks
- You can also globally set a custom username and password for your build tasks, 
- You can also customize some variables for your build tasks 
- The plugin presets some variables for your build tasks
- If you set the global environment variable of the plugin and the user's environment variable, the environment variable set by the same environment variable user will override the global environment variable. If you set the global environment variable that comes with Jenkins, the global environment variable of the plugin and the user's environment variable will fail, whichever is set by Jenkins
- 您可以为构建任务的每个用户自定义用户名和密码
- 您还可以为构建任务全局设置自定义用户名和密码，
- 您还可以为构建任务自定义一些变量
- 该插件为您的构建任务预设了一些变量
- 如果设置插件的全局环境变量和用户的环境变量，同一个环境变量用户设置的环境变量会覆盖全局环境变量。 如果设置了Jenkins自带的全局环境变量，插件的全局环境变量和用户的环境变量都会失效，以Jenkins设置的为准
## Getting started

###The plugin provides the following environment variables:
###该插件提供以下环境变量：

| Variable                  | Description                           |
| ------------------------  | ----------------------------------    |
| user_name                 |The username you set through the plugin|
| pass_word                 |The password you set through the plugin|
| Build\_Causes             | Build_Causes                          |
| Build\_Page               | Current build page address            |
| Build\_Console            | Current build Console address         |
| Build\_User\_Display\_Name| User DisplayName                      |
| Build\_User\_Full\_Name   | User FullName                         |
| Build\_User\_ID           | User ID                               |
| Build\_User\_Email        | User Email address                    |

| 变量                       | 说明                                   |
| ------------------------  | ----------------------------------     |
| pass_word                 |您通过插件设置的密码                        |
| Build\_Causes             | Build_Causes                           |
| Build\_Page               | 当前构建页面地址                          |
| Build\_Console            | 当前构建控制台地址                         |
| Build\_User\_Display\_Name| 用户显示名                               |
| Build\_User\_Full\_Name   | 用户全名                                 |
| Build\_User\_ID           | 用户名                                   |
| Build\_User\_Email        | 用户电子邮件地址                          |
## Fix coverage issues
- g_key you set the global environment variable
- l_key user configuration environment variable you set
- The final result of key will be replaced by all variables set by Jenkins
- r_key This is the final result of the environment variable you set (the global one you set will be replaced by the user's setting)

- g_key 你设置全局环境变量
- l_key 你设置的用户配置环境变量
- key 最终结果，会被Jenkins设置的全部变量替换
- r_key 这个是你设置的环境变量最终结果（你设置的全局会被用户的设置替换） 

## Usage example
![](docs/images/user1.png)
![](docs/images/user2.png)
![](docs/images/input1.png)
![](docs/images/out1-1.png)
![](docs/images/out1-2.png)
![](docs/images/out2-1.png)
![](docs/images/out2-2.png)
![](docs/images/input2.png)
![](docs/images/out1-3.png)
![](docs/images/out2-3.png)

## If you are using the Generic Webhook Trigger to trigger the project build
- You can set Cause to trigger the acquisition of environment variables of different users. If the value in the brackets in [value] is equal to the environment variable set by a user, the environment variable used for project construction will use the environment variable of the matched player. The above Image instance, where value can be a fixed value or a variable obtained by Generic Webhook Trigger, set with $xxxx, such as [$pushUserName]
## 如果你是用 Generic Webhook Trigger 触发项目构建
- 你可以设置Cause来触发获取不同用户的环境变量，[value]中括号里面的value如果等于某个用户设置的环境变量来，则项目构建用的环境变量会使用匹配到的玩家的环境变量，上面图片实例，其中value 可以是固定的值或者是Generic Webhook Trigger获取的变量，采用$xxxx来设置，比如[$pushUserName]

Licensed under MIT, see [LICENSE](LICENSE.md)

