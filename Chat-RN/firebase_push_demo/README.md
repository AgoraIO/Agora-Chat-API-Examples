# Documentation

This project mainly demonstrates how to use FCM to implement offline message push notification.
Users need to pay attention to how to integrate FCM components, how to configure ios or android related information, how to obtain FCM senderID and token, and how to send FCM information to the chat server to complete the settings. And these can be found in the example project.
Whether the message is pushed through FCM is mainly determined by the user's online status, the user's device information, and the FCM configuration of the chat server. If the user has sent senderID and token to the chat server, the user is offline (for example: the application is not started, the application is killed) and the chat server has set FCM, the chat server will forward the message to the FCM server. For the application switching to the background, if the connection status is still there, FCM will not be used for message push.

How to test the push function, please see the instructions below.

## Configuration Parameters

Use tools to generate `env.ts` file.

```sh
yarn env
```

In `env.ts`, fill in the necessary parameters.

## Run The Application

1. Initialize the SDK.
2. Log in.
3. Bind the token.

**Note: Make sure the network connection is normal.**
**Note: Make sure firebase console setting is normal.**
**Note: Make sure agora console setting is normal.**

The case of receiving an offline push.

For android apps:

- App in background: [x]
- App is killed: [v]

For ios apps:

- App in background: [x]
- App is killed: [v]

## References

[Please refer to here for the message console.](https://console.agora.io/)
[Please refer to here about FCM.](https://console.firebase.google.com/)
[For help with FCM, please refer to here.](https://rnfirebase.io/)
