# HciTTSDemo
a tts demo use hci TTS ability


1.引入包到libs下
hcicloud-5.0.jar
hcicloud_player-5.0.jar

2.在app/build.gradle中添加依赖
compile files('libs/hcicloud-5.0.jar')
compile files('libs/hcicloud_player-5.0.jar')

3.初始化灵云系统
InitParam initParam = HciTTSManager.getInstance().getInitParam(this);
String strConfig = initParam.getStringConfig();
HciCloudSys.hciInit(strConfig, this);

4.在线语音合成
capkey
tts.cloud.wangjing
tts.cloud.xixi
等等

5.本地语音合成
capkey
tts.local.synth

6.语音合成API  HciTTSManager.getInstance().synth("你需要合成的文字");

7.暂停及继续
HciTTSManager.getInstance().playerPause();
HciTTSManager.getInstance().playerResume();

详情请见：http://www.hcicloud.com/document/dev_show/forward/ability_advance