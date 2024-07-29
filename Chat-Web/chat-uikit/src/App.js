import React, { useState, useCallback, useEffect } from "react";
import { ConversationList, Chat, useClient, useConversationContext, Button, Input } from "agora-chat-uikit";
import "agora-chat-uikit/style.css";
import "./App.css";

function App() {
	const [values, setValues] = useState({
		username: "",
		password: "",
	});
	const client = useClient()
	const conversationStore = useConversationContext();
	const [authToken, setAuthToken] = useState("");

	useEffect(() => {
		client.addEventHandler('connection_state_change', {
			onConnected: () => {
				alert('login success')
			},
			onDisconnected: () => {
				alert('logout success')
			},
		})
	}, [])

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
					const { accessToken } = res;
					console.log("accessToken", accessToken);
					client.open({
						user: values.username,
						agoraToken: accessToken,
					})
					setAuthToken(accessToken);
				})
				.catch((err) => {
					alert("get token failed");
				});
		};


		getToken(values.username, values.password);
	}, [values]);


	const onClose = () => {
		client.close();
	};
	const createConversation = () => {
		conversationStore.addConversation({
			chatType: 'singleChat',
			conversationId: to,
			lastMessage: {},
		});
	};

	return (
		<div className="App">
			<h2> Agora Chat UIkit Examples </h2>
			<div>
				<div className="form-item">
					<Input
						placeholder="Username"
						className="App-input"
						onChange={handleChange("username")}
						value={values.username}
					></Input>
					<Input
						placeholder="Password"
						className="App-input"
						onChange={handleChange("password")}
						value={values.password}
					></Input>
					<Button
						type="primary"
						className="App-btn"
						onClick={onLogin}
					>
						Login
					</Button>
					<Button
						type="primary"
						className="App-btn"
						onClick={onClose}
					>
						Logout
					</Button>
				</div>
				<div className="form-item">
					<Input
						placeholder="Target User ID"
						className="App-input"
						onChange={handleChangeToValue}
						value={to}
					></Input>
					<Button
						type="primary"
						className="App-btn"
						onClick={createConversation}
					>
						Create Conversation
					</Button>
				</div>
			</div>
			<div className="uikit-container">

				<div className="conversation-container">
					<ConversationList style={{ background: "#10a597" }}></ConversationList>
				</div>
				<div className="chat-container">
					<Chat></Chat>
				</div>
			</div>
		</div>
	);
}

export default App;
