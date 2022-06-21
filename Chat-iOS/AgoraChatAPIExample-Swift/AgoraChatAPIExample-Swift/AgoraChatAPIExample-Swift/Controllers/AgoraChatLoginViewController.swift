//
//  AgoraChatLoginViewController.swift
//  AgoraChatAPIExample-Swift
//
//  Created by 朱继超 on 2022/6/20.
//
//
//  LoginViewController.swift
//  AgoraChatEThreeExample
//
//  Created by 朱继超 on 2022/4/20.
//

import UIKit
@_exported import ProgressHUD

final class AgoraChatLoginViewController: UIViewController,UITextFieldDelegate {
    
    @UserDefault("AgoraChatUserName", defaultValue: "") var userName
    
    @UserDefault("AgoraChatPassword", defaultValue: "") var passWord
    
    private var token: String = ""
    
    private lazy var logo: UIImageView = {
        UIImageView(frame: CGRect(x: ScreenWidth/3.0, y: ZNavgationHeight+20, width: ScreenWidth/3.0, height: ScreenWidth/3.0)).image(UIImage(named: "login_logo")!).contentMode(.scaleAspectFit)
    }()
    
    private lazy var userNameField: UITextField = {
        UITextField(frame: CGRect(x: 20, y: self.logo.frame.maxY+20, width: ScreenWidth - 40, height: 40)).cornerRadius(5).placeholder("UserName").delegate(self).tag(111).layerProperties(UIColor(0xf5f7f9), 1).leftView(UIView(frame: CGRect(x: 0, y: 0, width: 20, height: 40)), .always)
    }()
    
    private lazy var passWordField: UITextField = {
        UITextField(frame: CGRect(x: 20, y: self.userNameField.frame.maxY+10, width: ScreenWidth - 40, height: 40)).cornerRadius(5).placeholder("PassWord").delegate(self).tag(112).layerProperties(UIColor(0xf5f7f9), 1).leftView(UIView(frame: CGRect(x: 0, y: 0, width: 20, height: 40)), .always)
    }()
    
    private lazy var login: UIButton = {
        UIButton(type: .custom).frame(CGRect(x: 20, y: self.passWordField.frame.maxY+40, width: ScreenWidth - 40, height: 45)).backgroundColor(UIColor(0x0066ff)).cornerRadius(10).title("Login", .normal).font(UIFont.systemFont(ofSize: 18, weight: .semibold)).addTargetFor(self, action: #selector(loginAction), for: .touchUpInside)
    }()
    
    private lazy var register: UIButton = {
        UIButton(type: .custom).frame(CGRect(x: 20, y: self.login.frame.maxY+10, width: ScreenWidth - 40, height: 45)).backgroundColor(UIColor(0x0066ff)).cornerRadius(10).title("Register", .normal).font(UIFont.systemFont(ofSize: 18, weight: .semibold)).addTargetFor(self, action: #selector(registerAction), for: .touchUpInside)
    }()
    
    private lazy var logRecord: UITextView = {
        UITextView(frame: CGRect(x: 20, y: self.register.frame.maxY+20, width: ScreenWidth - 40, height: ScreenHeight - self.register.frame.maxY - 40)).layerProperties(UIColor(0xf5f7f9), 1).cornerRadius(5).font(UIFont.systemFont(ofSize: 18, weight: .semibold)).isEditable(false)
    }()

    override func viewDidLoad() {
        super.viewDidLoad()
        self.view.addSubViews([self.logo,self.userNameField,self.passWordField,self.login,self.register,self.logRecord])
        // Do any additional setup after loading the view.
        self.view.backgroundColor = .white
    }

}

extension AgoraChatLoginViewController {
    
    @objc private func loginAction() {
        guard let userName = self.userNameField.text,let passWord = self.passWordField.text,!userName.isEmpty,!passWord.isEmpty else { return }
        AgoraChatRequest.shared.loginToAppSever(userName: userName.lowercased(), passWord: passWord) { dic, code in
            if let token = dic["accessToken"] as? String,let loginName = dic["chatUserName"] as? String,token.count > 0 {
                self.agoraChatLogin(loginName: loginName, token: token)
            }
        }
    }
    
    @objc private func registerAction() {
        self.view.endEditing(true)
        guard let userName = self.userNameField.text,let passWord = self.passWordField.text,!userName.isEmpty,!passWord.isEmpty else { return }
        self.userName = userName.lowercased()
        self.passWord = passWord
        ProgressHUD.show("Resister user...", interaction: false)
        AgoraChatRequest.shared.registerToAppSever(userName: userName.lowercased(), passWord: passWord) {
            ProgressHUD.dismiss()
            self.loginHandler($0, $1)
        }
    }
    
    private func loginHandler(_ dic: Dictionary<String,Any>,_ code: Int) {
        if code == 200 {
            if let token = dic["accessToken"] as? String,let loginName = dic["chatUserName"] as? String,token.count > 0 {
                self.userName = loginName
                self.token = token
            }
        } else {
            if let code = dic["code"] as? String,code == "RES_USER_ALREADY_EXISTS" {
                guard let userName = self.userNameField.text,let passWord = self.passWordField.text,!userName.isEmpty,!passWord.isEmpty else { return }
                AgoraChatRequest.shared.loginToAppSever(userName: userName.lowercased(), passWord: passWord) { dic, code in
                    if let token = dic["accessToken"] as? String,let loginName = dic["chatUserName"] as? String,token.count > 0 {
                        self.agoraChatLogin(loginName: loginName, token: token)
                    }
                }
                return
            }
            let errorInfo = dic["errorInfo"] ?? ""
            self.logRecord.text! += "\n\(Date().z.dateString("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")):\(errorInfo)"
        }
    }
    
    private func agoraChatLogin(loginName: String,token:String) {
        ProgressHUD.show("Login user...", interaction: false)
        AgoraChatClient.shared().login(withUsername: loginName, agoraToken: token) { reponse, error in
            ProgressHUD.dismiss()
            if error == nil || error?.code.rawValue == 200 {
                self.userName = loginName.lowercased()
                self.logRecord.text! += "\n\(Date().z.dateString("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")):loginSuccess!"
                guard let del = UIApplication.shared.delegate as? AppDelegate else {
                    return
                }
                DispatchQueue.main.async {
                    let vc = ViewController.init()
                    vc.title = "AgoraChat Api Example"
                    del.window?.rootViewController = UINavigationController(rootViewController: vc)
                }
            } else {
                self.logRecord.text! += "\n\(Date().z.dateString("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")):\(error?.errorDescription ?? "")"
            }
        }
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.view.endEditing(true)
    }
    
}

