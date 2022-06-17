using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;


public class TestCode : MonoBehaviour
{
    // 初始化SDK用的Appkey，此处的值为演示用，如果正式环境需要使用你申请的Appkey
    static string APPKEY = "easemob-demo#easeim";

    // user id控件，获取输入的userId可以调用: Username.text
    public InputField Username;

    // password控件，获取输入的password可以调用: Password.text
    public InputField Password;

    // single chat id控件，获取用户输入的singleChatId可以调用: SignChatId.text
    public InputField SignChatId;

    // message content 控件，获取输入的message content可以调用: MessageContent.text
    public InputField MessageContent;
    
    public Button SignInBtn;
    public Button SignUpBtn;
    public Button SignOutBtn;
    public Button SendMsgBtn;
    
    public Text LogText;


    // Start is called before the first frame update
    void Start()
    {
        SetupUI();
        InitSDK();
        AddChatDelegate();
    }

    // Update is called once per frame
    void Update()
    {

    }

    private void OnDestroy()
    {
        RemoveChatDelegate();
    }

    private void SetupUI()
    {
        SignInBtn.onClick.AddListener(SignInAction);
        SignUpBtn.onClick.AddListener(SignUpAction);
        SignOutBtn.onClick.AddListener(SignOutAction);
        SendMsgBtn.onClick.AddListener(SendMessageAction);
    }

    // 初始化聊天SDK
    private void InitSDK()
    {
 
    }

    // 添加消息监听
    private void AddChatDelegate() {
        
    }

    // 移除消息监听
    private void RemoveChatDelegate() {

    }

    // 点击SignIn按钮
    private void SignInAction() {
        if (Username.text.Length == 0 || Password.text.Length == 0) {
            AddLogToLogText("username or password is null");
            return;
        }

    }

    // 点击SignUp按钮
    private void SignUpAction()
    {
        if (Username.text.Length == 0 || Password.text.Length == 0)
        {
            AddLogToLogText("username or password is null");
            return;
        }

    }

    // 点击SignOut按钮
    private void SignOutAction()
    {
       
    }

    // 点击Send按钮
    private void SendMessageAction ()
    {
        if (SignChatId.text.Length == 0 || MessageContent.text.Length == 0) {
            AddLogToLogText("Sign chatId or message content is null");
            return;
        }

       
    }

    // 添加日志到控制台
    private void AddLogToLogText(string str) {
        LogText.text += System.DateTime.Now +": " + str + "\n";
    }

}