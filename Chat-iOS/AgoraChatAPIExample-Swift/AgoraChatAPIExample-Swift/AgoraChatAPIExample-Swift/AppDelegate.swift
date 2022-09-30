//
//  AppDelegate.swift
//  AgoraChatAPIExample-Swift
//
//  Created by 朱继超 on 2022/6/20.
//

import UIKit
@_exported import AgoraChat

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    
    @UserDefault("AgoraChatUserName", defaultValue: "") var userName
    
    @UserDefault("AgoraChatPassword", defaultValue: "") var passWord

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        self.window = UIWindow(frame: UIScreen.main.bounds)
        self.agoraChatInitlize()
        self.window?.backgroundColor = .white
        self.window?.rootViewController = self.chooseEntry()
        self.window?.becomeFirstResponder()
        self.window?.makeKeyAndVisible()
        return true
    }
    
    private func chooseEntry() -> UINavigationController {
        var nav: UINavigationController?
        if !(AgoraChatClient.shared().currentUsername ?? "").isEmpty {
            let VC = ViewController.init();
            VC.title = "AgoraChat Api Example"
            nav =  UINavigationController(rootViewController: VC)
        } else {
            nav = UINavigationController(rootViewController: AgoraChatLoginViewController.init())
        }
        return nav!
    }

    private func agoraChatInitlize() {
        let options = AgoraChatOptions(appkey: AgoraChatRequest.appKey)
        options.enableConsoleLog = true
        options.isAutoLogin = true
        options.pushKitCertName = "com.easemob.enterprise.demo.ui.voip"
        options.apnsCertName = "ChatDemoDevPush"
        AgoraChatClient.shared().initializeSDK(with: options)
    }
}

