//
//  AgoraChatImageCell.swift
//  AgoraChatAPIExample-Swift
//
//  Created by 朱继超 on 2022/6/21.
//

import UIKit

final class AgoraChatImageCell: UITableViewCell {
    
    private lazy var avatar: UIImageView = {
        UIImageView(frame: CGRect(x: 15, y: 5, width: 32, height: 32))
    }()
    
    private lazy var userName: UILabel = {
        UILabel(frame: CGRect(x: self.avatar.frame.maxX+8, y: 7, width: ScreenWidth/3.0*2, height: 20)).font(UIFont.systemFont(ofSize: 16, weight: .semibold))
    }()

    private lazy var picture: UIImageView = {
        UIImageView().contentMode(.scaleAspectFit).backgroundColor(UIColor(0xf5f7f9)).cornerRadius(10)
    }()
    
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        self.contentView.addSubViews([self.avatar,self.userName,self.picture])
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

}

extension AgoraChatImageCell {
    func updateLayout(message: AgoraChatMessage) {
        self.avatar.frame = CGRect(x: message.direction.rawValue == 0 ? ScreenWidth-47:15, y: 5, width: 32, height: 32)
        self.userName.frame = CGRect(x: message.direction.rawValue == 0 ? 80:self.avatar.frame.maxX+8, y: 7, width: ScreenWidth/3.0*2, height: 20)
        self.userName.textAlignment = message.direction.rawValue == 0 ? .right:.left
        self.userName.textColor = message.direction.rawValue == 0 ? .systemBlue:.black
        self.avatar.image = UIImage(named: message.direction.rawValue == 0 ? "user":"user1")
        self.userName.text = message.from
        self.picture.frame =  CGRect(x: message.direction.rawValue == 0 ? ScreenWidth-47-80:25, y: 40, width: 70, height: 70)
        self.picture.contentMode = .scaleAspectFit
        guard let body = message.body as? AgoraChatImageMessageBody else { return }
        if body.localPath.isEmpty,body.thumbnailLocalPath.isEmpty {
            AgoraChatClient.shared().chatManager.downloadMessageAttachment(message, progress: nil) { msg, error in
                if error == nil {
                    self.picture.image = UIImage(contentsOfFile: body.thumbnailLocalPath.isEmpty ? body.localPath:body.thumbnailLocalPath)
                }
            }
        } else {
            self.picture.image = UIImage(contentsOfFile: body.thumbnailLocalPath.isEmpty ? body.localPath:body.thumbnailLocalPath)
        }
        
    }
}
