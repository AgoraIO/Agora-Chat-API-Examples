# 使用 Agora Chat UIkit 发送和接收点对点消息

本页面介绍了如何快速集成 Agora Chat UIkit  来实现单聊。

### 前提条件

- 有效的 Agora Chat 开发者账号。
- 创建 Agora Chat 项目并获取 [AppKey](https://console.easemob.com/) 。
- [npm](https://www.npmjs.com/get-npm) 或 [yarn](https://yarnpkg.com/)
- SDK 本身已支持 IE9+、FireFox10+、Chrome54+、Safari6+。

### 操作步骤

### 1、准备开发环境

本节介绍如何创建项目，将 `Agora Chat UIkit` 集成进你的项目中。

#### 新建 Web 项目

​```bash
# 安装 react/cli 工具
npm install create-react-app
# 构建一个 react 项目
npx create-react-app agora-chat-uikit
# 启动项目
cd agora-chat-uikit
HTTPS=true yarn start
```

目录如下：
```josn
├── package.json
├── public                  # 这个是 webpack 的配置的静态目录。
│   ├── favicon.ico
│   ├── index.html          # 默认是单页面应用，这个是最终的 html 的基础模板。
│   └── manifest.json
├── src
│   ├── App.css             # App 根组件的 css。
│   ├── App.js              # App 组件代码。
│   ├── App.test.js
│   ├── index.css           # 启动文件样式。
│   ├── index.js            # 启动的文件。
│   ├── logo.svg
│   └── serviceWorker.js
└── yarn.lock
```

### 2、集成 Uikit

- 安装 `agora-chat-uikit` 。可以使用 `npm` 安装或者使用 `yarn` 安装，具体如下：

  使用 `npm` 安装：

  ```bash
  npm install agora-chat-uikit --save
  ```

  使用 `yarn` 安装：

  ```bash
  yarn add agora-chat-uikit
  ```

- 在 JS 文件中导入 `agora-chat-uikit` 包中的 `EaseApp` 组件：

  ```bash
  import { EaseApp } from 'agora-chat-uikit'
  ```

### 3、登录

```javascript
const onLogin = useCallback(() => {
	if (!values.username) {
		return message.error("username is required");
	} else if (!values.password) {
		return message.error("password is required");
	}
	// 从 app server 获取token
	const getToken = (username, password) => {
		postData("https://a41.easemob.com/app/chat/user/login", {
			userAccount: username,
			userPassword: password,
		}).then((res)=> {
			const { accessToken } = res;
			setAuthToken(accessToken);
		})
	};

	function postData(url, data) {
		return fetch(url, {
			body: JSON.stringify(data),
			cache: "no-cache",
			headers: {
				"content-type": "application/json",
			},
			method: "POST",
			mode: "cors",
			redirect: "follow",
			referrer: "no-referrer",
		}).then((response) => response.json());
	}
	getToken(values.username, values.password);
}, [values]);
```



### 4、创建一个会话

使用 `EaseApp` 组件中 `addConversationItem` 方法创建一个会话，即可收发消息。

```javascript
let conversationItem = {
	conversationType: "singleChat",	 	// 单聊：singleChat， 群聊：groupChat
	conversationId: "userId"，		   // 单聊：好友ID，群聊：群组ID
};
EaseApp.addConversationItem(conversationItem);
```

### 5、导入 `agora-chat-uikit`  并实现用户界面

完整示例如下：
```javascript
// App.js
import React, { useState, useCallback } from "react";
import { EaseApp } from "agora-chat-uikit";
import "./App.css";

function App() {
	const [values, setValues] = useState({
		username: "",
		password: "",
	});
	const [authToken, setAuthToken] = useState("");
	const handleChange = (prop) => (event) => {
		let value = event.target.value;
		if (prop === "username") {
			value = event.target.value.replace(/[^\w\.\/]/gi, "");
		}
		setValues({
			...values,
			[prop]: value,
		});
	};

	const [to, setTo] = useState("");
	const handleChangeToValue = (event) => {
		let toValue = event.target.value;
		setTo(toValue);
	};

	const onLogin = useCallback(() => {
		if (!values.username) {
			return alert("username is required");
		} else if (!values.password) {
			return alert("password is required");
		}

		const getToken = (username, password) => {
			postData("https://a41.easemob.com/app/chat/user/login", {
				userAccount: username,
				userPassword: password,
			})
				.then((res) => {
					console.log("res>>>11", res);
					const { accessToken } = res;
					setAuthToken(accessToken);
				})
				.catch((err) => {
					alert("登陆失败，用户名或密码无效！");
				});
		};

		function postData(url, data) {
			return fetch(url, {
				body: JSON.stringify(data),
				cache: "no-cache",
				headers: {
					"content-type": "application/json",
				},
				method: "POST",
				mode: "cors",
				redirect: "follow",
				referrer: "no-referrer",
			}).then((response) => response.json());
		}
		getToken(values.username, values.password);
	}, [values]);

	const loginSuccessCallback = (e) => {
		const WebIM = EaseApp.getSdk();
		WebIM.conn.addEventHandler("Logout", {
			onDisconnected: () => {
				setAuthToken("");
			},
		});
	};

	const onClose = () => {
		window.WebIM.conn.close();
	};
	const createConversation = () => {
		let conversationItem = {
			conversationType: "singleChat",
			conversationId: to,
		};
		EaseApp.addConversationItem(conversationItem);
		setTo("");
	};

	return (
		<div className="App">
			<h2> Agora Chat UIkit Examples </h2>
			<div>
				<div>
					<label className="App-lable"> Username </label>
					<input
						placeholder="Username"
						className="App-input"
						onChange={handleChange("username")}
						value={values.username}
					></input>
					<label className="App-lable"> Password </label>
					<input
						placeholder="Password"
						className="App-input"
						onChange={handleChange("password")}
						value={values.password}
					></input>
					<button
						type="primary"
						className="App-btn"
						onClick={onLogin}
					>
						Login
					</button>
					<button
						type="primary"
						className="App-btn"
						onClick={onClose}
					>
						Logout
					</button>
				</div>
				<div className="App-to">
					<label className="App-lable">To</label>
					<input
						placeholder="UserID"
						className="App-input"
						onChange={handleChangeToValue}
						value={to}
					></input>
					<button
						type="primary"
						className="App-btn"
						onClick={createConversation}
					>
						CreateConversation
					</button>
				</div>
			</div>
			<div className="container">
				{authToken && (
					<EaseApp
						appkey="41117440#383391"
						username={values.username}
						agoraToken={authToken}
						successLoginCallback={loginSuccessCallback}
					/>
				)}
			</div>
		</div>
	);
}

export default App;
```

```css
/* App.css */
.App {
  text-align: left;
  margin-left: 20px;
  height: 100%;
  width: 100%;
}

.App-to {
  margin-left: 60px;
}

.App-input {
  width: 160px !important;
  height: 20px !important;
  margin: 5px 0 0 5px !important;
}

.App-lable {
  margin-left: 5px;
}

.App-btn {
  margin: 5px 0 0 5px !important;
}

.container {
  height: calc(100% - 135px);
  margin-top: 5px;
  width: 100%;
  position: absolute;
}
```



