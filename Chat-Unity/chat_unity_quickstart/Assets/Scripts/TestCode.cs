using UnityEngine;
using UnityEngine.UI;
using System.Collections.Generic;

public class TestCode : MonoBehaviour
{

    public InputField Username;

    public InputField AgoraToken;

    public InputField SignChatId;

    public InputField MessageContent;

    public Button SignInBtn;
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
        SignOutBtn.onClick.AddListener(SignOutAction);
        SendMsgBtn.onClick.AddListener(SendMessageAction);
    }

    // Init chat sdk
    private void InitSDK()
    {
        //TODO:
    }

    // Add chat delegate
    private void AddChatDelegate()
    {
        //TODO:
    }

    // Remove chat delegate
    private void RemoveChatDelegate()
    {
        //TODO:
    }

    // Click SignIn button
    private void SignInAction()
    {
        if (Username.text.Length == 0 || AgoraToken.text.Length == 0)
        {
            AddLogToLogText("username or token is null");
            return;
        }

        //TODO:
    }

    // Click SignOut button
    private void SignOutAction()
    {
        //TODO:
    }

    // Click Send message button
    private void SendMessageAction()
    {
        if (SignChatId.text.Length == 0 || MessageContent.text.Length == 0)
        {
            AddLogToLogText("Sign chatId or message content is null");
            return;
        }
        //TODO:
    }

    // Add log to app console
    private void AddLogToLogText(string str)
    {
        LogText.text += System.DateTime.Now + ": " + str + "\n";
    }
}
