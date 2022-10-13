from users.agora_dynamic_key import ChatTokenBuilder2
from users.utils import TokenInfo


def build_user_chat_token(app_id, app_cert, chat_user_uid, expire_period) -> str:
    return ChatTokenBuilder2.ChatTokenBuilder.build_user_token(
        app_id,
        app_cert,
        chat_user_uid,
        expire_period
    )


def get_agora_app_token(app_id, app_cert, expire_period) -> str:
    return ChatTokenBuilder2.ChatTokenBuilder.build_app_token(app_id, app_cert, expire_period)
