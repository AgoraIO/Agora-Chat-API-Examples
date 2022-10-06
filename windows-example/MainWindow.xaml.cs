using System;
using System.Collections.Generic;
using System.Windows;
using System.Net.Http;
using System.Threading.Tasks;

namespace windows_example
{
    /// <summary>
    /// MainWindow.xaml  
    /// </summary>
    public partial class MainWindow : Window
    {
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
            if (UserIdTextBox.Text.Length == 0 || AgoraTokenBox.Text.Length == 0)
            {
                AddLogToLogText("username or password is null");
                return;
            }

            // TODO：
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
    }
}
