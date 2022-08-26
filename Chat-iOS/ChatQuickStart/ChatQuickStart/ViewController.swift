//
//  ViewController.swift
//  ChatQuickStart
//
//  Created by li xiaoming on 2022/8/26.
//

import UIKit
// Import the AgoraChat SDK
import AgoraChat

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

    func createButton(title: String?, action: Selector) -> UIButton {
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
    
    func initChatSDK() {
            // Initialize AgoraChat SDK
            let options = AgoraChatOptions(appkey: "41117440#383391")
            options.isAutoLogin = false // disable auto login
            options.enableConsoleLog = true
            AgoraChatClient.shared.initializeSDK(with: options)
            // add chat delegate to receive messages
            AgoraChatClient.shared.chatManager.add(self, delegateQueue: nil)
        }

    override func viewDidLoad() {
        super.viewDidLoad()
        initViews()
        // call initialize SDK here
        initChatSDK()
    }

    // Output running log
    func printLog(_ log: Any...) {
        DispatchQueue.main.async {
            self.logView.text.append(
                DateFormatter.localizedString(from: Date(), dateStyle: .none, timeStyle: .medium)
                + ":  " + String(reflecting: log) + "\r\n"
            )
            self.logView.scrollRangeToVisible(NSRange(location: self.logView.text.count, length: 1))
        }
    }
}

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
                  let dic: Dictionary<String, AnyHashable> = try? JSONSerialization.jsonObject(with: data) as? Dictionary<String, AnyHashable>,
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
