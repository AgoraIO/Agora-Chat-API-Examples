//
//  AgoraChatHttpRequest.swift
//  ChatQuickStart
//
//  Created by li xiaoming on 2022/8/26.
//

import UIKit
/*
When fetching a token, your token server may differ slightly from our example backend service logic.

To make this step easier to test, use the temporary token server "https://a41.chat.agora.io" provided by Agora in the placeholder below. When you're ready to deploy your own server, swap out your server's URL there, and update any of the POST request logic along with that.
*/
class AgoraChatHttpRequest: NSObject {
    static var baseUrl = "https://a41.chat.agora.io"
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
