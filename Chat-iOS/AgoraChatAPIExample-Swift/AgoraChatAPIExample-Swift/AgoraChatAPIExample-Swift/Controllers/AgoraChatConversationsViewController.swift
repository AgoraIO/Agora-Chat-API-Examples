//
//  AgoraChatConversationsViewController.swift
//  AgoraChatAPIExample-Swift
//
//  Created by 朱继超 on 2022/6/20.
//

import UIKit
import ProgressHUD

final class AgoraChatConversationsViewController: UIViewController,UITableViewDelegate,UITableViewDataSource,UITextFieldDelegate,AgoraChatClientDelegate, AgoraChatManagerDelegate {
    
    private var toChatId = ""
    
    private var conversations = [AgoraChatConversation]()
    
    private lazy var conversationList: UITableView = {
        UITableView(frame: CGRect(x: 0, y: ZNavgationHeight, width: ScreenWidth, height: ScreenHeight-ZNavgationHeight), style: .plain).delegate(self).dataSource(self).tableFooterView(UIView()).rowHeight(60).separatorStyle(.none)
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        guard let datas = AgoraChatClient.shared().chatManager.getAllConversations() else { return }
        if self.title == "Join a group" {
            self.conversations = datas.filter({ $0.type == .groupChat })
        } else {
            self.conversations = datas
        }
        // Do any additional setup after loading the view.
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(barButtonSystemItem: .add, target: self, action: #selector(addChat))
        self.view.addSubview(self.conversationList)
        AgoraChatClient.shared().add(self, delegateQueue: .main)
        AgoraChatClient.shared().chatManager.add(self, delegateQueue: .main)
    }
    
    deinit {
        AgoraChatClient.shared().removeDelegate(self)
        AgoraChatClient.shared().chatManager.remove(self)
    }
}

extension AgoraChatConversationsViewController {
    //MARK: - UITableViewDelegate&UITableViewDataSource
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        self.conversations.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let item = self.conversations[safe: indexPath.row]
        var cell = tableView.dequeueReusableCell(withIdentifier: "AgoraChatConversationsCell")
        if cell == nil {
            cell = UITableViewCell(style: .subtitle, reuseIdentifier: "AgoraChatConversationsCell")
        }
        cell?.textLabel?.text = item?.conversationId
        cell?.imageView?.image = UIImage(named: "default_avatar")
        cell?.accessoryType = .disclosureIndicator
        return cell!
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let to = self.conversations[safe: indexPath.row]?.conversationId ?? ""
        var VC: UIViewController?
        VC?.title = to
        if to.isEmpty {
            return
        }
        switch self.title {
        case "Send text message":
            VC = AgoraChatSendTextViewController.init(to)
        case "Send image message":
            VC = AgoraChatSendImageVideoController.init(to)
        default:
            VC = AgoraChatSendTextViewController.init(to)
        }
        if VC != nil {
            self.navigationController?.pushViewController(VC!, animated: true)
        }
    }
    
    @objc private func addChat() {
        var title = "Send to"
        if self.title == "Join a group" {
            title = self.title ?? ""
        }
        let alert = UIAlertController(title: title, message: nil, preferredStyle: .alert).addAlertTextField {
            $0.placeholder = "you want to chat userId or groupId!"
            $0.delegate = self
        }.addAlertAction(UIAlertAction(title: "Cancel", style: .cancel, handler: { _ in
            
        })).addAlertAction(UIAlertAction(title: "Confirm", style: .default, handler: { [weak self] _ in
            self?.createChat()
        }))
        self.navigationController?.present(alert, animated: true, completion: nil)
    }
    
    private func createChat() {
        //AgoraChatConversationType is group,create group use group's owner and id
        if self.toChatId.isEmpty {
            return
        }
        if self.title == "Join a group" {
            AgoraChatClient.shared().groupManager.joinPublicGroup(self.toChatId) { group, error in
                if error == nil {
                    self.createConversation()
                } else {
                    ProgressHUD.showError(error?.errorDescription ?? "")
                }
            }
        } else {
            self.createConversation()
        }
        
    }
    
    private func createConversation() {
        let item: AgoraChatConversation = AgoraChatClient.shared().chatManager.getConversation(self.toChatId, type: AgoraChatConversationType.init(rawValue: 0)!, createIfNotExist: true)!
        let temp = self.conversations.filter { $0.conversationId == item.conversationId
        }
        if temp.count <= 0 {
            self.conversations.append(item)
            self.conversationList.reloadData()
        }
    }
    
    //MARK: - UITextFieldDelegate
    func textFieldDidEndEditing(_ textField: UITextField, reason: UITextField.DidEndEditingReason) {
        self.toChatId = textField.text ?? ""
    }
    
    //MARK: - AgoraChatClientDelegate
    func tokenWillExpire(_ aErrorCode: Int32) {
        //Re-login required
        if aErrorCode == 110 {
            //Call the login related api to log in again than create e3
        }
    }

    func tokenDidExpire(_ aErrorCode: Int32) {
        //Re-login required
        //Call the login related api to log in again than create e3
    }
    //MARK: - AgoraChatManagerDelegate
    func messagesDidReceive(_ aMessages: [AgoraChatMessage]) {
        for message in aMessages {
            let item: AgoraChatConversation = AgoraChatClient.shared().chatManager.getConversation(message.conversationId, type: AgoraChatConversationType.init(rawValue: 0)!, createIfNotExist: true)!
            let temp = self.conversations.filter { $0.conversationId == item.conversationId
            }
            if temp.count <= 0 {
                self.conversations.append(item)
            }
        }
        DispatchQueue.main.async {
            self.conversationList.reloadData()
        }
    }
    
}
