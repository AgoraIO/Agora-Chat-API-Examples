import React, { useState, useCallback, useEffect } from "react";
import { ConversationList, Chat, useClient, useConversationContext, Button, Input } from "agora-chat-uikit";
import "agora-chat-uikit/style.css";
import "./App.css";

function App() {
	const [values, setValues] = useState({
		username: "",
		token: "",
	});
	const client = useClient()
	const conversationStore = useConversationContext();

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

	const onLogin = useCallback(() => {
		if (!values.username) {
			return alert("username is required");
		} else if (!values.token) {
			return alert("access token is required");
		}

		client.open({
			user: values.username,
			accessToken: values.token,
		});
	}, [client, values]);


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
						placeholder="User ID"
						className="App-input"
						onChange={handleChange("username")}
						value={values.username}
					></Input>
					<Input
						placeholder="Access token"
						className="App-input"
						onChange={handleChange("token")}
						value={values.token}
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
