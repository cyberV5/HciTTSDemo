package tw.com.chainsea.hcittsdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.sinovoice.hcicloudsdk.api.HciCloudSys;
import com.sinovoice.hcicloudsdk.common.HciErrorCode;
import com.sinovoice.hcicloudsdk.common.InitParam;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String fileName = "AccountInfo.txt";
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);

        loadAccount();

        // 加载信息,返回InitParam, 获得配置参数的字符串
        InitParam initParam = HciTTSManager.getInstance().getInitParam(this);
        String strConfig = initParam.getStringConfig();
        Log.i(TAG,"\nhciInit config:" + strConfig);

        // 初始化
        int errCode = HciCloudSys.hciInit(strConfig, this);
        if (errCode != HciErrorCode.HCI_ERR_NONE && errCode != HciErrorCode.HCI_ERR_SYS_ALREADY_INIT) {
            Toast.makeText(getApplicationContext(), "hciInit error: " + HciCloudSys.hciGetErrorInfo(errCode),Toast.LENGTH_SHORT).show();
        }

        // 获取授权/更新授权文件 :
        errCode = HciTTSManager.getInstance().checkAuthAndUpdateAuth();
        if (errCode != HciErrorCode.HCI_ERR_NONE) {
            // 由于系统已经初始化成功,在结束前需要调用方法hciRelease()进行系统的反初始化
            Toast.makeText(getApplicationContext(), "CheckAuthAndUpdateAuth error: " + HciCloudSys.hciGetErrorInfo(errCode),Toast.LENGTH_SHORT).show();
            HciCloudSys.hciRelease();
        }

        //传入了capKey初始化TTS播发器
        boolean isPlayerInitSuccess = HciTTSManager.getInstance().initPlayer();
        if (!isPlayerInitSuccess) {
            Toast.makeText(this, "播放器初始化失败", Toast.LENGTH_LONG).show();
        }
    }

    private void loadAccount() {
        boolean loadResult = AccountInfo.getInstance().loadAccountInfo(fileName,this);
        if (loadResult) {
            // 加载信息成功进入主界面
            Toast.makeText(getApplicationContext(), "加载灵云账号成功", Toast.LENGTH_SHORT).show();
        } else {
            // 加载信息失败，显示失败界面
            Toast.makeText(getApplicationContext(), "加载灵云账号失败！请在assets/AccountInfo.txt文件中填写正确的灵云账户信息，账户需要从www.hcicloud.com开发者社区上注册申请。",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // 测试按钮 ,播放,停止TTS语音播放
    public void onClick(View v) {
        if (HciTTSManager.getInstance().getPlayer() != null) {
            try {
                switch (v.getId()) {
                    case R.id.bt_play:
                        // 开始合成
                        HciTTSManager.getInstance().synth(editText.getText().toString());
                        break;

                    case R.id.bt_pause:
                        HciTTSManager.getInstance().playerPause();
                        break;

                    case R.id.bt_resume:
                        HciTTSManager.getInstance().playerResume();
                        break;

                    default:
                        break;
                }
            } catch (IllegalStateException ex) {
                Toast.makeText(getBaseContext(), "状态错误", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        HciTTSManager.getInstance().playerRelease();
        HciCloudSys.hciRelease();
    }
}
