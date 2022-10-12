This page shows how to add one-to-one messaging into your app by using the Agora Chat SDK for iOS.


## Understand the tech

~338e0e30-e568-11ec-8e95-1b7dfd4b7cb0~


## Prerequisites

In order to follow the procedure in this page, you must have the following:

- A valid Agora [account](https://docs.agora.io/en/video-calling/reference/manage-agora-account/#create-an-agora-account)
- An Agora [project](https://docs.agora.io/en/video-calling/reference/manage-agora-account/#create-an-agora-project) with an [App Key](https://docs.agora.io/en/agora-chat/get-started/enable#get-the-information-of-the-chat-project) that has [enabled the Chat service](https://docs.agora.io/en/agora-chat/get-started/enable) 
- Xcode. This page uses Xcode 13.0 as an example.
- A device running iOS 10 or later.


## Token generation

This section introduces how to register a user at Agora Console and generate a temporary token.

### 1. Register a user

To register a user, do the following:

1. On the **Project Management** page, click **Config** for the project that you want to use.

	![](https://web-cdn.agora.io/docs-files/1664531061644)

2. On the **Edit Project** page, click **Config** next to **Chat** below **Features**.

	![](https://web-cdn.agora.io/docs-files/1664531091562)

3. In the left-navigation pane, select **Operation Management** > **User** and click **Create User**.

	![](https://web-cdn.agora.io/docs-files/1664531141100)

<a name="userid"></a>

4. In the **Create User** dialog box, fill in the **User ID**, **Nickname**, and **Password**, and click **Save** to create a user.

	![](https://web-cdn.agora.io/docs-files/1664531162872)


### 2. Generate a user token

To ensure communication security, Agora recommends using tokens to authenticate users who log in to the Agora Chat system.

For testing purposes, Agora Console supports generating temporary tokens for Agora Chat. To generate a user token, do the following:

1. On the **Project Management** page, click **Config** for the project that you want to use.

	![](https://web-cdn.agora.io/docs-files/1664531061644)

2. On the **Edit Project** page, click **Config** next to **Chat** below **Features**.

	![](https://web-cdn.agora.io/docs-files/1664531091562)

3. In the **Data Center** section of the **Application Information** page, enter the [user ID](#userid) in the **Chat User Temp Token** box and click **Generate** to generate a token with user privileges.

	![](https://web-cdn.agora.io/docs-files/1664531214169)

<div class="alert note">Register a user and generate a user token for a sender and a receiver respectively for <a href="https://docs.agora.io/en/agora-chat/get-started/get-started-sdk#test">test use</a> later in this demo.</div>

    
## Project run

In this section, we prepare the development environment necessary to integrate Agora Chat into your app.

### 1. Open project

Open **ChatQuickStart.xcodeproj** with XCode.

### 2. Integrate the Agora Chat SDK

1. Go to **File > Swift Packages > Add Package Dependencies...**, and paste the following URL:

```text
https://github.com/AgoraIO/AgoraChat_iOS.git
```

2. In **Choose Package Options**, specify the Chat SDK version you want to use.

### Replace placeholder

You need replace the placeholder in code with your own config.Include appkey,userId and token.
``` swift
func initViews() {
        ...
        self.userIdField.text = <#Input Your UserId#>
        self.tokenField.text = <#Input Your Token#>
    }
...
func initChatSDK() {
            // Initializes the Agora Chat SDK
            let options = AgoraChatOptions(appkey: "<#Agora App Key#>")
            ...
        }
```


### Run and test the project

Use Xcode to compile and run the project on an iOS device or an simulator. If the project runs properly, the following user interface appears:

![](https://web-cdn.agora.io/docs-files/1665309003658)

<div class="alert note">You can log in to the app by either entering the required fields in the user interface as stated below, or modifying the fields in the <a href="#sign-in"><code>ViewController.swift</code></a> file.</div>

To validate the peer-to-peer messaging you have just integrated into your app using Agora Chat, perform the following operations:

1. Log in  
On your simulator or physical device, enter the user ID (`lxm`) and Agora token of the sender in the **User Id** and **Token** box respectively, and click **Login**.

2. Send a message  
Fill in the user ID of the receiver (`lxm2`) in the **Remote User Id** box, type in the message ("Hello") to send in the **Input text message** box, and click **Send**.  
![](https://web-cdn.agora.io/docs-files/1665309009543)

3. Log out  
Click **Logout** to log out of the sender account.

4. Receive the message  
After signing out, log in with the user ID and Agora token of the receiver (`lxm2`) and receive the message "Hello" sent in step 2.  
![](https://web-cdn.agora.io/docs-files/1665309015042)


## Next steps

For demonstration purposes, Agora Chat uses temporary tokens generated from Agora Console for authentication in this guide. In a production context, the best practice is for you to deploy your own token server, use your own [App Key](./enable_agora_chat?platform=Android#get-the-information-of-the-agora-chat-project) to generate a token, and retrieve the token on the client side to log in to Agora. To see how to implement a server that generates and serves tokens on request, see [Generate a User Token](../Develop/Authentication).