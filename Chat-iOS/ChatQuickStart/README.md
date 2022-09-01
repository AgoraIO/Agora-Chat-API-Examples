This page shows how to add one-to-one messaging into your app by using the Agora Chat SDK for iOS.  


## Prerequisites

Before proceeding, ensure that your development environment meets the following requirements: 

- Xcode. This page uses Xcode 13.0 as an example.
- A device running iOS 10 or later.

## Project setup

In this section, we prepare the development environment necessary to integrate Agora Chat into your app.

### Create an iOS project

In Xcode, follow the steps to create an iOS app project. 

- Open Xcode and click **Create a new Xcode project**.

- Select **iOS** for the platform type and **App** for the project type and click **Next**.

- Choose **Storyboard** and **Swift** for this example, and create the project.

### Integrate the Agora Chat SDK

Go to **File > Swift Packages > Add Package Dependencies...**, and paste the following URL:

   ```
   https://github.com/AgoraIO/AgoraChat_iOS.git
   ```

In Choose Package Options, specify the Chat SDK version you want to use.

## Implement a one-to-one chat client

### Create the UI

In the interface,you should have a ViewController to create the UI controls you need.
In ViewController.swift, replace any existing content with the following:

```swift
class ViewController: UIViewController {
    // Define UIViews
    var userIdField, passwordField, remoteUserIdField, textField: UITextField!
    var registerButton, loginButton, logoutButton, sendButton: UIButton!
    var logView: UITextView!
    
    func createField(placeholder: String?) -> UITextField {
        let field = UITextField()
        field.layer.borderWidth = 0.5
        field.placeholder = placeholder
        return field
    }
    
    func createButton(title:String?, action: Selector) -> UIButton {
        let button = UIButton(type: .system)
        button.setTitle(title, for: .normal)
        button.addTarget(self, action: action, for: .touchUpInside)
        return button
    }
    
    func createLogView() -> UITextView {
        let logTextView = UITextView()
        logTextView.isEditable = false
        return logTextView
    }
    
    func initViews() {
        // create UI controls
        userIdField = createField(placeholder: "User Id")
        self.view.addSubview(userIdField)
        passwordField = createField(placeholder: "Password")
        self.view.addSubview(passwordField)
        remoteUserIdField = createField(placeholder: "Remote User Id")
        self.view.addSubview(remoteUserIdField)
        textField = createField(placeholder: "Input text message")
        self.view.addSubview(textField)
        registerButton = createButton(title: "Register", action: #selector(registerAction))
        self.view.addSubview(registerButton)
        loginButton = createButton(title: "Login", action: #selector(loginAction))
        self.view.addSubview(loginButton)
        logoutButton = createButton(title: "Logout", action: #selector(logoutAction))
        self.view.addSubview(logoutButton)
        sendButton = createButton(title: "Send", action: #selector(sendAction) )
        self.view.addSubview(sendButton)
        logView = createLogView()
        self.view.addSubview(logView)
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        // Layout UI controls
        let fullWidth = self.view.frame.width
        userIdField.frame = CGRect(x: 30, y: 50, width: fullWidth - 60, height: 30)
        passwordField.frame = CGRect(x: 30, y: 100, width: fullWidth - 60, height: 30)
        registerButton.frame = CGRect(x: 30, y: 150, width: 80, height: 30)
        loginButton.frame = CGRect(x: 130, y: 150, width: 80, height: 30)
        logoutButton.frame = CGRect(x: 230, y: 150, width: 80, height: 30)
        remoteUserIdField.frame = CGRect(x: 30, y: 200, width: fullWidth - 60, height: 30)
        textField.frame = CGRect(x: 30, y: 250, width: fullWidth - 60, height: 30)
        sendButton.frame = CGRect(x: 30, y: 300, width: 80, height: 30)
        logView.frame = CGRect(x: 30, y: 350, width: fullWidth - 60, height: self.view.frame.height - 350 - 50)
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        initViews()
    }

    // Output running log
    func printLog(_ log: Any...) {
        DispatchQueue.main.async {
            self.logView.text.append(
                DateFormatter.localizedString(from: Date.now, dateStyle: .none, timeStyle: .medium)
                + ":  " + String(reflecting: log) + "\r\n"
            )
            self.logView.scrollRangeToVisible(NSRange(location: self.logView.text.count, length: 1))
        }
    }
}
```

The user interface just like this.

   ![img](./start.png)

### Retrieve a token

When fetching a token, your token server may differ slightly from our example backend service logic.

To make this step easier to test, use the temporary token server `"https://a41.chat.agora.io"` provided by Agora in the placeholder below. When you're ready to deploy your own server, swap out your server's URL there, and update any of the POST request logic along with that.

**If you have already got an account and user token, you can ignore this section and go to the next.**

Add a file named `AgoraChatHttpRequest.swift` to your project, and copy the following code to the file.

```swift
import UIKit
class AgoraChatHttpRequest: NSObject {
    static var baseUrl = "<#Developer Token Server#>"
    static var session = URLSession(configuration: URLSessionConfiguration.default)

    // Register userId via app server
    static func register(userId: String, password: String, completion: @escaping (String) -> Void) {
        guard let url = URL(string: AgoraChatHttpRequest.baseUrl + "/app/chat/user/register") else {
            return
        }
        var request = URLRequest(url: url)
        request.allHTTPHeaderFields = ["Content-Type": "application/json"]
        request.httpMethod = "POST"
        let params = ["userAccount": userId, "userPassword": password]
        request.httpBody = try! JSONSerialization.data(withJSONObject: params, options: .prettyPrinted)
        AgoraChatHttpRequest.session.dataTask(with: request) { data, response, err in
            guard let data = data else {
                completion("")
                return
            }
            completion(String(data: data, encoding: .utf8) ?? "")
        }.resume()
    }
    
    // Fetch user token via app server
    static func loginWith(userId: String, password: String, completion: @escaping (String) -> Void) {
        guard let url = URL(string: AgoraChatHttpRequest.baseUrl + "/app/chat/user/login") else {
            return
        }
        var request = URLRequest(url: url)
        request.allHTTPHeaderFields = ["Content-Type": "application/json"]
        request.httpMethod = "POST"
        let params = ["userAccount": userId,"userPassword": password]
        request.httpBody = try! JSONSerialization.data(withJSONObject: params, options: .prettyPrinted)
        AgoraChatHttpRequest.session.dataTask(with: request) { data, response, err in
            guard let data = data else {
                completion("")
                return
            }
            completion(String(data: data, encoding: .utf8) ?? "")
        }.resume()
    }
}
```

### Implement the chat logic

This section shows the logic of initialize chat SDK, creating a user, logging in to Agora Chat, and send/receive a one-to-one text message. 

#### Import and Initialize Chat SDK
Modify the `ViewController.swift` file as follows:

```swift
import UIKit
// Import the AgoraChat SDK
import AgoraChat
class ViewController: UIViewController {
    ...
    func initChatSDK() {
        // Initialize AgoraChat SDK
        let options = AgoraChatOptions(appkey: "<#Agora App Key#>")
        options.isAutoLogin = false // disable auto login
        options.enableConsoleLog = true
        AgoraChatClient.shared.initializeSDK(with: options)
        // add chat delegate to receive messages
        AgoraChatClient.shared.chatManager.add(self, delegateQueue: nil)
    }
    
    override func viewDidLoad() {
        ...
        // call initialize SDK here
        initChatSDK()
    }
}
```


#### Create account && Login with token fetched via AppServer

```swift
// Button action
extension ViewController {

    // register an account via app server
    @objc func registerAction() {
        guard let userId = userIdField.text, let password = passwordField.text else {
            return
        }
        AgoraChatHttpRequest.register(userId: userId, password: password) { result in
            if result.isEmpty {
                self.printLog("register failed")
            } else {
                self.printLog("register result:\(result)")
            }
        }
    }

    // get user token via app server and login
    @objc func loginAction() {
        guard let userId = userIdField.text, let password = passwordField.text else {
            return
        }
        AgoraChatHttpRequest.loginWith(userId: userId, password: password) { result in
            guard let data = result.data(using: .utf8),
                  let dic:Dictionary<String, AnyHashable> = try? JSONSerialization.jsonObject(with: data) as? Dictionary<String, AnyHashable>,
                  let token = dic["accessToken"] else {
                self.printLog("login failed \(result)")
                return
            }
            let err = AgoraChatClient.shared.login(withUsername: userId, agoraToken: token as! String)
            if err == nil {
                self.printLog("login success")
            } else {
                self.printLog("login failed:\(err?.errorDescription ?? "")")
            }
        }
    }

    // logout
    @objc func logoutAction() {
        AgoraChatClient.shared.logout(false) { err in
            if err == nil {
                self.printLog("logout success")
            }
        }
    }
}
```

#### Send and Receive messages

```swift
extension ViewController: AgoraChatManagerDelegate  {
    // send a text message
    @objc func sendAction() {
        guard let remoteUser = remoteUserIdField.text,
              let text = textField.text,
              let currentUserName = AgoraChatClient.shared.currentUsername else {
            self.printLog("Not login or remoteUser/text is empty")
            return
        }
        let msg = AgoraChatMessage(
            conversationId: remoteUser, from: currentUserName,
            to: remoteUser, body: .text(text), ext: nil
        )
        AgoraChatClient.shared.chatManager.send(msg, progress: nil) { msg, err in
            if let err = err {
                self.printLog("send msg error.\(err.errorDescription)")
            } else {
                self.printLog("send msg success")
            }
        }
    }
    func messagesDidReceive(_ aMessages: [AgoraChatMessage]) {
        for msg in aMessages {
            switch msg.swiftBody {
            case let .text(content):
                self.printLog("receive text msg,content: \(content)")
            default:
                break
            }
        }
    }
}
```

## Run and test the project

Use Xcode to compile and run the project on an iOS device or emulator. 
To try sending and receiving text messages, follow the steps on two clients:

1. Enter a userId (such as `Localuser` and `Remoteuser`) and a password (such as `123456`),and click `Register` on each client to create two Agora Chat users.

2. Log into Agora Chat as `Localuser`, type `Remoteuser` for `Peer username`, and send a text message.

   ![img](./LocalUser.png)

3. Log into Agora Chat as `Remoteuser` and check logs to see whether this message is received.

   ![img](./RemoteUser.png)

## Next Steps

For demonstration purposes, Agora Chat provides an app server that enables you to quickly retrieve a token using the App Key given in this guide. In a production context, the best practice is for you to deploy your own token server, use your own [App Key](./enable_agora_chat?platform=iOS#get-the-information-of-the-agora-chat-project) to generate a token, and retrieve the token on the client side to log in to Agora. To see how to implement a server that generates and serves tokens on request, see [Generate a User Token](./generate_user_tokens?platform=All%20Platforms).


## Reference

### Other approaches to integrate the SDK

#### Method 1: Through CocoaPods

1. Install CocoaPods if you have not. For details, see [Getting Started with CocoaPods](https://guides.cocoapods.org/using/getting-started.html#getting-started).

2. In the Terminal, navigate to the project root directory and run the `pod init` command to create a text file `Podfile` in the project folder.

3. Open the `Podfile` file and add the Agora Chat SDK. Remember to replace `AgoraChatExample` with the target name of your project.

   ```
   platform :ios, '11.0'
   
   target 'Your project target' do
       pod 'Agora_Chat_iOS'
   end
   ```

4. In the project root directory, run the following command to integrate the SDK. When the SDK is installed successfully, you can see `Pod installation complete!` in the Terminal and an `xcworkspace` file in the project folder. 

   ```
   pod install
   ```

5. Open the `xcworkspace` file in Xcode.

#### Through your local storage

1. Download the latest Agora Chat SDK and decompress it.

2. Copy `AgoraChat.framework` in the SDK package to the project folder. `AgoraChat.framework` contains arm64, armv7, and x86_64 instruction sets.

3. Open Xcode and navigate to **TARGETS > Project Name > General > Frameworks, Libraries, and Embedded Content**.

4. Click **+ > Add Other… > Add Files** to add AgoraChat.framework and set the **Embed** property to **Embed & Sign**. Then the project automatically links to the required system library. 
