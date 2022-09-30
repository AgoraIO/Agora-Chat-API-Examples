//
//  AgoraChatSendImageVideoController.swift
//  AgoraChatAPIExample-Swift
//
//  Created by 朱继超 on 2022/6/20.
//

import UIKit
import Photos
import ZSwiftBaseLib

final class AgoraChatSendImageVideoController: UIViewController,UITableViewDelegate,UITableViewDataSource,AgoraChatClientDelegate, AgoraChatManagerDelegate,UIImagePickerControllerDelegate, UINavigationControllerDelegate {

    private var filePath = ""
        
    private var messages: [AgoraChatMessage] = [AgoraChatMessage]()
    
    private var conversation: AgoraChatConversation?
    
    private lazy var imagePicker: UIImagePickerController = {
        let picker = UIImagePickerController.init()
        picker.modalPresentationStyle = .fullScreen
        picker.delegate = self
        return picker
    }()
        
    private lazy var messagesList: UITableView = {
        UITableView(frame: CGRect(x: 0, y: ZNavgationHeight, width: ScreenWidth, height: ScreenHeight-ZNavgationHeight-40-CGFloat(ZBottombarHeight)), style: .plain).delegate(self).dataSource(self).tableFooterView(UIView()).separatorStyle(.none)
    }()
    
    private lazy var sendImage: UIButton = {
        UIButton(type: .custom).frame(CGRect(x: 10, y: ScreenHeight-40-CGFloat(ZBottombarHeight), width: ScreenWidth - 20, height: 45)).backgroundColor(UIColor(0x0066ff)).cornerRadius(10).title("Send Image", .normal).font(UIFont.systemFont(ofSize: 18, weight: .semibold)).addTargetFor(self, action: #selector(sendImageAction), for: .touchUpInside)
    }()
    
    convenience init(_ conversationId: String) {
        self.init()
        self.conversation = AgoraChatClient.shared().chatManager.getConversationWithConvId(conversationId)
        let messages = self.conversation?.loadMessagesStart(fromId: "", count: 50, searchDirection: AgoraChatMessageSearchDirection.init(rawValue: 0)!) ?? []
        for message in messages {
            if message.body.type == .image {
                self.messages.append(message)
            }
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        self.view.addSubViews([self.messagesList,self.sendImage])
        AgoraChatClient.shared().add(self, delegateQueue: .main)
        AgoraChatClient.shared().chatManager.add(self, delegateQueue: .main)
        // Do any additional setup after loading the view.
    }
    
    deinit {
        AgoraChatClient.shared().removeDelegate(self)
        AgoraChatClient.shared().chatManager.remove(self)
        NotificationCenter.default.removeObserver(self)
    }

}

//MARK: - Private method
extension AgoraChatSendImageVideoController {
    
    @objc private func sendImageAction() {
        PHPhotoLibrary.requestAuthorization { status in
            switch status {
            case .notDetermined,.restricted,.denied:
                ProgressHUD.showError("Access to the album is not authorized")
            case .limited,.authorized: self.openAlbum()
            @unknown default:
                fatalError()
            }
        }
        
    }
    
    private func openAlbum() {
        DispatchQueue.main.async {
            self.imagePicker.sourceType = .photoLibrary
            self.imagePicker.mediaTypes = UIImagePickerController.availableMediaTypes(for: .photoLibrary)!
            self.present(self.imagePicker, animated: true, completion: nil)
        }
    }
    
    //MARK: - UIImagePickerControllerDelegate
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        guard let type = info[.mediaType] as? String else {
            picker.dismiss(animated: true)
            return
        }
//        let assert = AVURLAsset.init(url: url)
//
//        let tracks = assert.tracks(withMediaType: .video)
        
        if !type.hasSuffix("image") {
            
        } else {
            guard let image = info[.originalImage] as? UIImage else {
                picker.dismiss(animated: true)
                return
            }
            let data = image.jpegData(compressionQuality: 0.01)
            let body = AgoraChatImageMessageBody(data: data, displayName: "\(Date().z.dateString)")
            let to = self.conversation?.conversationId ?? ""
            let message = AgoraChatMessage(conversationID: to, from: AgoraChatClient.shared().currentUsername!, to: to, body: body, ext: nil)
            ProgressHUD.show("sending image...")
            AgoraChatClient.shared().chatManager.send(message, progress: nil) { msg, error in
                ProgressHUD.dismiss()
                if error == nil {
                    self.messages.append(msg!)
                    self.messagesList.reloadData()
                } else {
                    ProgressHUD.showError("\(error?.errorDescription ?? "")")
                }
            }
        }
        picker.dismiss(animated: true)
    }
    
    //MARK: - UITableViewDelegate&UITableViewDataSource
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        110
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        self.messages.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let message: AgoraChatMessage = self.messages[safe: indexPath.row]!
        var cell = tableView.dequeueReusableCell(withIdentifier: "AgoraChatImageCell") as? AgoraChatImageCell
        if cell == nil {
            cell = AgoraChatImageCell(style: .subtitle, reuseIdentifier: "AgoraChatImageCell")
        }
        cell?.updateLayout(message: message)
        return cell!
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
    //MARK: - AgoraChatManagerDelegate
    func messagesDidReceive(_ aMessages: [AgoraChatMessage]) {
        self.messages.append(contentsOf: aMessages)
        DispatchQueue.main.async {
            self.messagesList.reloadData()
            self.messagesList.scrollToRow(at: IndexPath(row: self.messages.count - 1, section: 0), at: .bottom, animated: true)
        }
    }
    
}
