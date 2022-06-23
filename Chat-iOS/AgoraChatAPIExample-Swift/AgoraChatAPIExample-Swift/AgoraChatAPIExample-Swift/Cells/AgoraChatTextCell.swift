//
//  AgoraChatTextCell.swift
//  AgoraChatAPIExample-Swift
//
//  Created by 朱继超 on 2022/6/20.
//

import UIKit

final class AgoraChatTextCell: UITableViewCell {

    private lazy var avatar: UIImageView = {
        UIImageView(frame: CGRect(x: 15, y: 5, width: 32, height: 32))
    }()
    
    private lazy var userName: UILabel = {
        UILabel(frame: CGRect(x: self.avatar.frame.maxX+8, y: 7, width: ScreenWidth/3.0*2, height: 20)).font(UIFont.systemFont(ofSize: 16, weight: .semibold))
    }()
    
    private lazy var content: UILabel = {
        UILabel(frame: CGRect(x: self.avatar.frame.maxX+8, y: self.userName.frame.maxY+10, width: ScreenWidth/3.0*2, height: 20)).font(UIFont.systemFont(ofSize: 14, weight: .medium)).numberOfLines(0).textColor(.darkText)
    }()

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        self.contentView.addSubViews([self.avatar,self.userName,self.content])
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

}

extension AgoraChatTextCell {
    func updateLayout(message: AgoraChatMessage) {
        let height = Self.contentHeight(message)
        self.avatar.frame = CGRect(x: message.direction.rawValue == 0 ? ScreenWidth-47:15, y: 5, width: 32, height: 32)
        self.userName.frame = CGRect(x: message.direction.rawValue == 0 ? 80:self.avatar.frame.maxX+8, y: 7, width: ScreenWidth/3.0*2, height: 20)
        self.content.frame = CGRect(x: message.direction.rawValue == 0 ? 80:self.avatar.frame.maxX+8, y: self.userName.frame.maxY+10, width: ScreenWidth/3.0*2, height: height > 20 ? height:20)
        self.userName.textAlignment = message.direction.rawValue == 0 ? .right:.left
        self.content.textAlignment = self.userName.textAlignment
        self.userName.textColor = message.direction.rawValue == 0 ? .systemBlue:.black
        self.avatar.image = UIImage(named: message.direction.rawValue == 0 ? "user":"user1")
        self.userName.text = message.from
        self.content.text = Self.contentText(message)
    }
    
    private static func converType(_ type: AgoraChatMessageBodyType) -> String {
        var text = "[unknown type]"
        switch type {
        case .image,.video,.voice,.file:
            text = "[file]"
        default:
            text = "[unknown type]"
        }
        return text
    }
    
    /// Description calculate message content height
    /// - Parameter message: receive message
    /// - Returns: height
    static func contentHeight(_ message: AgoraChatMessage) -> CGFloat {
        return Self.contentText(message).z.sizeWithText(font: UIFont.systemFont(ofSize: 14, weight: .medium), size: CGSize(width: ScreenWidth/3.0*2, height: CGFloat.greatestFiniteMagnitude)).height
    }
    
    /// Description get display text
    /// - Parameter message: message
    /// - Returns: text
    static func contentText(_ message: AgoraChatMessage) -> String {
        var content = "No Message!"
        if message.body.type.rawValue != 1 {
            content = self.converType(message.body.type)
        } else {
            if let body = message.body as? AgoraChatTextMessageBody {
                content = body.text
            }
        }
        return content
    }
}
