from chat_server import settings
from users.exceptions import RestClientException
from users.helpers import token_helper
from users.models import AppUserInfo
from users.utils import TokenInfo
from time import time
import requests


def get_agora_chat_user_token_with_account(app_user_info: AppUserInfo) -> TokenInfo:
    agora_chat_user_uuid = app_user_info.agora_chat_user_uuid

    # 1. Use an Agora App ID, App Cert and UUID to get the Agora Chat user token
    user_token = token_helper.build_user_chat_token(
        settings.AGORA_APP_ID,
        settings.AGORA_APP_CERT,
        agora_chat_user_uuid,
        settings.AGORA_TOKEN_EXPIRE_PERIOD
    )

    # 2. Build the token info result
    token_info = TokenInfo()
    token_info.token = user_token
    token_info.chat_username = app_user_info.agora_chat_user_name
    token_info.expire_timestamp = (int(time()) + settings.AGORA_TOKEN_EXPIRE_PERIOD) * 1000  # to millis
    return token_info


def register_agora_chat_user(user_account, user_password):
    """
    Registers a new user to the Agora chat server
    """
    org_name, app_name = settings.AGORA_APP_KEY.split('#')
    url = f"http://{settings.AGORA_REST_API_DOMAIN}/{org_name}/{app_name}/users"
    app_token = token_helper.get_agora_app_token(
        settings.AGORA_APP_ID,
        settings.AGORA_APP_CERT,
        settings.AGORA_TOKEN_EXPIRE_PERIOD
    )

    headers = {
        'Content-Type': "application/json",
        'Accept': "application/json",
        'Authorization': f"Bearer {app_token}"
    }
    body = {
        'username': user_account,
        'password': user_password
    }

    response = requests.post(url, json=body, headers=headers)
    response_json = response.json()
    if response.status_code == 200:
        return response_json['entities'][0]['uuid']
    else:
        raise RestClientException(response.status_code, response_json.get('exception'))
