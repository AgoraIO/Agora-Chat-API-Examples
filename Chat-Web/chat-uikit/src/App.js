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
			postData("https://a41.chat.agora.io/app/chat/user/login", {
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
