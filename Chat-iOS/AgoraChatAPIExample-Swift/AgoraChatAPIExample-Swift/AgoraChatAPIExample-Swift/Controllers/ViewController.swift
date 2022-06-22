//
//  ViewController.swift
//  AgoraChatAPIExample-Swift
//
//  Created by 朱继超 on 2022/6/20.
//

import UIKit
import ZSwiftBaseLib

final class ViewController: UIViewController,UITableViewDelegate,UITableViewDataSource,AgoraChatClientDelegate {
    
    @UserDefault("AgoraChatUserName", defaultValue: "") var userName
    
    @UserDefault("AgoraChatPassword", defaultValue: "") var passWord
    
    private var data = ["Send text message","Send image message","Join a group","Log out"]
    
    private lazy var functionList: UITableView = {
        UITableView(frame: CGRect(x: 0, y: ZNavgationHeight, width: ScreenWidth, height: ScreenHeight-ZNavgationHeight), style: .plain).delegate(self).dataSource(self).tableFooterView(UIView()).rowHeight(50)
    }()

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        self.view.addSubViews([self.functionList])
        self.view.backgroundColor = .white
        self.refreshHeader()
        AgoraChatClient.shared().add(self, delegateQueue: .main)
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.refreshHeader()
    }
    
    deinit {
        AgoraChatClient.shared().removeDelegate(self)
    }

}

extension ViewController {
    private func refreshHeader() {
        if !(AgoraChatClient.shared().currentUsername ?? "").isEmpty {
            self.functionList.tableHeaderView = UILabel(frame: CGRect(x: 0, y: 0, width: ScreenWidth, height: 30)).text("current user is:\(AgoraChatClient.shared().currentUsername ?? "")").textAlignment(.center)
        }
    }
    //MARK: - UITableViewDelegate&UITableViewDataSource
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        self.data.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        var cell = tableView.dequeueReusableCell(withIdentifier: "666")
        if cell == nil {
            cell = UITableViewCell(style: .default, reuseIdentifier: "666")
        }
        let text = self.data[safe: indexPath.row]
        cell?.textLabel?.text = text
        cell?.accessoryType = (text == "Log out" ? .none:.disclosureIndicator)
        return cell!
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        var VC: UIViewController?
        let text = self.data[safe: indexPath.row]
        switch text {
        case "Log out": self.logoutAction()
        default:
            VC = AgoraChatConversationsViewController.init()
            VC?.title = text;
        }
        if VC != nil {
            VC?.title = self.data[safe: indexPath.row]
            self.navigationController?.pushViewController(VC!, animated: true)
        }
    }
    
    private func logoutAction() {
        ProgressHUD.show()
        AgoraChatClient.shared().logout(true) { error in
            ProgressHUD.dismiss()
            if error == nil {
                self.removeHeader()
            } else {
                ProgressHUD.showError("\(error?.errorDescription ?? "")")
            }
        }
    }
    
    private func removeHeader() {
        DispatchQueue.main.async {
            guard let del = UIApplication.shared.delegate as? AppDelegate else {
                return
            }
            self.functionList.tableHeaderView = nil;
            let vc = AgoraChatLoginViewController.init()
            del.window?.rootViewController = vc
        }
    }
    
    //MARK: - AgoraChatClientDelegate
    func tokenWillExpire(_ aErrorCode: Int32) {
        //Re-login required
        if aErrorCode == AgoraChatErrorCode.tokeWillExpire.rawValue {
            AgoraChatRequest.shared.loginToAppSever(userName: self.userName, passWord: self.passWord) { dic, code in
                if let token = dic["accessToken"] as? String,token.count > 0 {
                    if let error =  AgoraChatClient.shared().renewToken(token) {
                        ProgressHUD.showError(error.errorDescription ?? "")
                    }
                } else {
                    ProgressHUD.showError("renew token failed!")
                }
            }
        }
    }

    func tokenDidExpire(_ aErrorCode: Int32) {
        //Re-login required
        if aErrorCode == AgoraChatErrorCode.tokeWillExpire.rawValue || aErrorCode == AgoraChatErrorCode.fileInvalid.rawValue {
            ProgressHUD.showError("Please log out then relogin!")
        }
    }

}

