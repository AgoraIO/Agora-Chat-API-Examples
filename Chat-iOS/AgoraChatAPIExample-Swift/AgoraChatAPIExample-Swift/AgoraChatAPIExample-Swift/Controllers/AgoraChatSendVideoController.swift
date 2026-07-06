//
//  AgoraChatSendVideoController.swift
//  AgoraChatAPIExample-Swift
//
//  Created by 朱继超 on 2022/10/8.
//

import UIKit
import AgoraChat
import ZSwiftBaseLib
import Photos

final class AgoraChatSendVideoController: UIViewController,UITableViewDelegate,UITableViewDataSource,AgoraChatClientDelegate, AgoraChatManagerDelegate,UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    
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
        self.conversation = AgoraChatClient.shared().chatManager?.getConversationWithConvId(conversationId)
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
        AgoraChatClient.shared().chatManager?.add(self, delegateQueue: .main)
        // Do any additional setup after loading the view.
    }
    
    deinit {
        AgoraChatClient.shared().removeDelegate(self)
        AgoraChatClient.shared().chatManager?.remove(self)
        NotificationCenter.default.removeObserver(self)
    }

    

}


//MARK: - Private method
extension AgoraChatSendVideoController {
    
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
        
        if !type.hasSuffix("image") || type.hasSuffix("movie") {
//            NSURL *videoURL = info[UIImagePickerControllerMediaURL];
//            // we will convert it to mp4 format
//            NSURL *mp4 = [self _videoConvert2Mp4:videoURL];
//            NSFileManager *fileman = [NSFileManager defaultManager];
//            if ([fileman fileExistsAtPath:videoURL.path]) {
//                NSError *error = nil;
//                [fileman removeItemAtURL:videoURL error:&error];
//                if (error) {
//                    NSLog(@"failed to remove file, error:%@.", error);
//                }
//            }
            guard let url = info[.mediaURL] as? URL else {
                return
            }
        } else {
            
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
//    - (NSURL *)_videoConvert2Mp4:(NSURL *)movUrl
//    {
//        NSURL *mp4Url = nil;
//        AVURLAsset *avAsset = [AVURLAsset URLAssetWithURL:movUrl options:nil];
//        NSArray *compatiblePresets = [AVAssetExportSession exportPresetsCompatibleWithAsset:avAsset];
//
//        if ([compatiblePresets containsObject:AVAssetExportPresetHighestQuality]) {
//            AVAssetExportSession *exportSession = [[AVAssetExportSession alloc]initWithAsset:avAsset presetName:AVAssetExportPresetHighestQuality];
//            NSString *mp4Path = [NSString stringWithFormat:@"%@/%d%d.mp4", [self getAudioOrVideoPath], (int)[[NSDate date] timeIntervalSince1970], arc4random() % 100000];
//            mp4Url = [NSURL fileURLWithPath:mp4Path];
//            exportSession.outputURL = mp4Url;
//            exportSession.shouldOptimizeForNetworkUse = YES;
//            exportSession.outputFileType = AVFileTypeMPEG4;
//            dispatch_semaphore_t wait = dispatch_semaphore_create(0l);
//            [exportSession exportAsynchronouslyWithCompletionHandler:^{
//                switch ([exportSession status]) {
//                    case AVAssetExportSessionStatusFailed: {
//                        NSLog(@"failed, error:%@.", exportSession.error);
//                    } break;
//                    case AVAssetExportSessionStatusCancelled: {
//                        NSLog(@"cancelled.");
//                    } break;
//                    case AVAssetExportSessionStatusCompleted: {
//                        NSLog(@"completed.");
//                    } break;
//                    default: {
//                        NSLog(@"others.");
//                    } break;
//                }
//                dispatch_semaphore_signal(wait);
//            }];
//            long timeout = dispatch_semaphore_wait(wait, DISPATCH_TIME_FOREVER);
//            if (timeout) {
//                NSLog(@"timeout.");
//            }
//
//            if (wait) {
//                //dispatch_release(wait);
//                wait = nil;
//            }
//        }
//
//        return mp4Url;
//    }
    func convertVideoType() -> String {
        ""
    }
    
    func videoPath() -> String {
        let path = String.cachesPath + "AgoraChatApiExampleVideo"
        if !FileManager.default.fileExists(atPath: path) {
            do {
                try FileManager.default.createDirectory(at: URL(string: path)!, withIntermediateDirectories: true)
            } catch {
                assert(false,"\(error.localizedDescription)")
            }
        }
        return path
    }
}
