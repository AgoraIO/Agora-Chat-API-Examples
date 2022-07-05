using System.Collections;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.Networking;
using System.Text;
using System;

public class TestCode : MonoBehaviour
{

    static string APPKEY = "41117440#383391";
    static string RegisterURL = "https://a41.easemob.com/app/chat/user/register";
    static string FetchAgoraTokenURL = "https://a41.easemob.com/app/chat/user/login";

    public InputField Username;

    public InputField Password;

    public InputField SignChatId;

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

    // Init chat sdk
    private void InitSDK()
    {

    }

    // Add chat delegate
    private void AddChatDelegate()
    {

    }

    // Remove chat delegate
    private void RemoveChatDelegate()
    {

    }

    // Click SignIn button
    private void SignInAction()
    {
        if (Username.text.Length == 0 || Password.text.Length == 0)
        {
            AddLogToLogText("username or password is null");
            return;
        }
        StartCoroutine(FetchAgoraToken(Username.text, Password.text, 
            (agornToken) => {
                AddLogToLogText(agornToken);
            }, 
            (error) => {
                AddLogToLogText(error);
            }
        ));
    }

    // Click SignUp button  
    private void SignUpAction()
    {
        if (Username.text.Length == 0 || Password.text.Length == 0)
        {
            AddLogToLogText("username or password is null");
            return;
        }

        StartCoroutine(RegisterAgoraAccount(Username.text, Password.text, (error) =>
        {
            if (error != null)
            {
                AddLogToLogText(error);
            }
            else
            {
                AddLogToLogText("register succeed");
            }
        }));
    }

    // Click SignOut button
    private void SignOutAction()
    {

    }

    // Click Send message button
    private void SendMessageAction()
    {
        if (SignChatId.text.Length == 0 || MessageContent.text.Length == 0)
        {
            AddLogToLogText("Sign chatId or message content is null");
            return;
        }
    }

    // Add log to app console
    private void AddLogToLogText(string str)
    {
        LogText.text += System.DateTime.Now + ": " + str + "\n";
    }


    private IEnumerator RegisterAgoraAccount(string username, string password, Action<string> errorCallback)
    {
        Demo.SimpleJSON.JSONObject jo = new Demo.SimpleJSON.JSONObject();
        jo.Add("userAccount", username);
        jo.Add("userPassword", password);
        byte[] databyte = Encoding.UTF8.GetBytes(jo.ToString());
        var www = UnityWebRequest.Post(RegisterURL, UnityWebRequest.kHttpVerbPOST);
        www.uploadHandler = new UploadHandlerRaw(databyte);
        www.SetRequestHeader("Content-Type", "application/json");
        yield return www.SendWebRequest();
        bool done = www.isDone;
        if (www.responseCode == 200)
        {
            errorCallback?.Invoke(null);
        }
        else
        {
            Demo.SimpleJSON.JSONNode jn = Demo.SimpleJSON.JSON.Parse(www.downloadHandler.text);
            string errorInfo = jn["errorInfo"].Value;
            errorCallback?.Invoke(errorInfo);
        }
    }

    private IEnumerator FetchAgoraToken(string username, string password, Action<string> tokenCallback, Action<string> errorCallback)
    {
        
        Demo.SimpleJSON.JSONObject jo = new Demo.SimpleJSON.JSONObject();
        jo.Add("userAccount", username);
        jo.Add("userPassword", password);
        byte[] databyte = Encoding.UTF8.GetBytes(jo.ToString());
        var www = UnityWebRequest.Post(FetchAgoraTokenURL, UnityWebRequest.kHttpVerbPOST);
        www.uploadHandler = new UploadHandlerRaw(databyte);
        www.SetRequestHeader("Content-Type", "application/json");
        yield return www.SendWebRequest();
        Demo.SimpleJSON.JSONNode jn = Demo.SimpleJSON.JSON.Parse(www.downloadHandler.text);
        if (www.result == UnityWebRequest.Result.ConnectionError) {
            errorCallback?.Invoke(www.error);
        }
        else if (www.responseCode == 200)
        {
            string accessToken = jn["accessToken"].Value;
            tokenCallback?.Invoke(accessToken);
        }
        else
        {
            string errorInfo = jn["errorInfo"].Value;
            errorCallback?.Invoke(errorInfo);
        }
    }
}