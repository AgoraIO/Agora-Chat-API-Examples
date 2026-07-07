import React from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App";
import reportWebVitals from "./reportWebVitals";
import { Provider } from 'agora-chat-uikit'

const root = createRoot(document.getElementById("root"));

root.render(
  <React.StrictMode>
    <Provider
      initConfig={{
        appId: "your appId", // Replace with your Agora Chat SDK App ID
      }}
    >
      <App />
    </Provider>
  </React.StrictMode>,
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
