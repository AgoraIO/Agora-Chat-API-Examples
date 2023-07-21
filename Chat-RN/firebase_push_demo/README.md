# Documentation

How to test push function?

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
