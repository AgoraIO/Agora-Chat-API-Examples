using System;
using System.Collections.Generic;
using System.Windows;
using System.Net.Http;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace windows_example
{
    /// <summary>
    /// MainWindow.xaml  
    /// </summary>
    public partial class MainWindow : Window
    {
        // Initialize the Appkey used by the SDK, the value here is for test, if the formal environment needs to use your application Appkey
        private static readonly string APPKEY = "41117440#383391";

        private readonly System.Windows.Threading.Dispatcher Dip = null;
        private static readonly HttpClient client = new HttpClient();

        public MainWindow()
        {
            InitializeComponent();
            Closed += CloseWindow;
            Dip = System.Windows.Threading.Dispatcher.CurrentDispatcher;
            InitSDK();
            AddChatDelegate();
        }

        // Initialize chat sdk.
        private void InitSDK()
        {

        }

        // add chat delegate
        private void AddChatDelegate()
        {

        }

        // remove chat delegate
        private void RemoveChatDelegate()
        {

        }


        // close window event
        private void CloseWindow(object sender, EventArgs e)
        {
            RemoveChatDelegate();
        }

        // sign in btn click
        private async void SignIn_Click(object sender, RoutedEventArgs e)
        {
            if (UserIdTextBox.Text.Length == 0 || PasswordTextBox.Text.Length == 0)
            {
                AddLogToLogText("username or password is null");
                return;
            }

            // TODO：
        }

        // sign up btn click
        private async void SignUp_Click(object sender, RoutedEventArgs e)
        {
            if (UserIdTextBox.Text.Length == 0 || PasswordTextBox.Text.Length == 0)
            {
                AddLogToLogText("username or password is null");
                return;
            }
            
            // TODO:
        }

        // sign out btn click
        private void SignOut_Click(object sender, RoutedEventArgs e)
        {
            // TODO:
        }

        // send btn click
        private void SendBtn_Click(object sender, RoutedEventArgs e)
        {
            if (SingleChatIdTextBox.Text.Length == 0)
            {
                AddLogToLogText("single chat id is null");
                return;
            }

            if (MessageContentTextBox.Text.Length == 0)
            {
                AddLogToLogText("message content is null !");
                return;
            }
            
            // TODO:

        }

        // add log to log text
        private void AddLogToLogText(string log)
        {
            Dip.InvokeAsync(() =>
            {
                LogTextBox.Text += DateTime.Now + ": " + log + "\n";
                LogTextBox.ScrollToEnd();
            });
        }

        // fetch agora token from app server by username and password.
        private async Task<string> LoginToAppServer(string username, string password)
        {
            Dictionary<string, string> values = new Dictionary<string, string>();
            values.Add("userAccount", username);
            values.Add("userPassword", password);
            string jsonStr = JsonConvert.SerializeObject(values);
            HttpContent content = new StringContent(jsonStr);
            content.Headers.ContentType = new System.Net.Http.Headers.MediaTypeHeaderValue("application/json");
            HttpResponseMessage response = await client.PostAsync("https://a41.easemob.com/app/chat/user/login", content);
            try
            {
                response.EnsureSuccessStatusCode();
                var responseString = await response.Content.ReadAsStringAsync();
                Dictionary<string, string> dict = JsonConvert.DeserializeObject<Dictionary<string, string>>(responseString);
                return dict["accessToken"];
            }
            catch (Exception)
            {
                return null;
            }
        }

        // regist account to user server by username and password.
        private async Task<Boolean> RegisterToAppServer(string username, string password)
        {
    
            Dictionary<string, string> values = new Dictionary<string, string>();
            values.Add("userAccount", username);
            values.Add("userPassword", password);
            string jsonStr = JsonConvert.SerializeObject(values);
            HttpContent content = new StringContent(jsonStr);
            content.Headers.ContentType = new System.Net.Http.Headers.MediaTypeHeaderValue("application/json");
            HttpResponseMessage response = await client.PostAsync("https://a41.easemob.com/app/chat/user/register", content);
            try
            {
                response.EnsureSuccessStatusCode();
                var responseString = await response.Content.ReadAsStringAsync();
                Dictionary<string, string> dict = JsonConvert.DeserializeObject<Dictionary<string, string>>(responseString);
                if (dict["code"] == "RES_OK")
                {
                    return true;
                }
                else {
                    return false;
                }
            }
            catch (Exception)
            {
                return false;
            }
        }

    }
}
