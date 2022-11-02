import sys
from time import time

from chat_server import settings
from users.agora_dynamic_key import AccessToken2
from users.agora_dynamic_key.AccessToken2 import ServiceChat
from users.exceptions import ASNotFoundException, ASPasswordErrorException, ASDuplicateUniquePropertyExistsException
from users.models import AppUserInfo
from users.services import chat_service
from users.utils import TokenInfo
from random import randrange


def find_by_user_account(user_account: str) -> AppUserInfo or None:
    """
    Returns a user account if present in local database
    """
    return AppUserInfo.objects.filter(user_account=user_account).first()


def save(user_account, user_password, agora_chat_user_name, agora_chat_user_uuid) -> AppUserInfo:
    app_user_info = AppUserInfo()
    app_user_info.user_account = user_account
    app_user_info.user_password = user_password
    app_user_info.agora_chat_user_name = agora_chat_user_name
    app_user_info.agora_chat_user_uuid = agora_chat_user_uuid
    app_user_info.save()
    return app_user_info


def login(user_account: str, user_password: str) -> TokenInfo:
    # 1. Get the user account
    app_user_info = find_by_user_account(user_account)

    # 2. Check if the user account exists
    if app_user_info is not None:
        # 3. Check if the password does match
        # Important: you need to encrypt the stored password in production mode
        if app_user_info.user_password != user_password:
            raise ASPasswordErrorException()
    else:
        raise ASNotFoundException(user_account)

    # 4. Get AgoraChat user token for user account
    return chat_service.get_agora_chat_user_token_with_account(app_user_info)


def register(user_account: str, user_password: str):
    # 1. Get the user account
    app_user_info = find_by_user_account(user_account)
    if app_user_info is None:
        chat_user_uid = chat_service.register_agora_chat_user(user_account, user_password)
        save(user_account, user_password, user_account, chat_user_uid)
    else:
        raise ASDuplicateUniquePropertyExistsException(user_password)


def get_token(user_account, channel_name, publisher_role) -> TokenInfo:
    app_user_info = find_by_user_account(user_account)
    if app_user_info is None:
        raise ASNotFoundException(user_account)
    else:
        chat_user_uuid = app_user_info.agora_chat_user_uuid
        expire_period = settings.AGORA_TOKEN_EXPIRE_PERIOD

        # The random number is used as the agoraUid here, but the uniqueness cannot be guaranteed.
        # In actual development, please use the agoraUid that guarantees the uniqueness.
        agora_uid = str(randrange(1, sys.maxsize))
        service_chat = ServiceChat(chat_user_uuid)
        service_chat.add_privilege(ServiceChat.kPrivilegeUser, expire_period)

        access_token = AccessToken2.AccessToken(
            settings.AGORA_APP_ID,
            settings.AGORA_APP_CERT,
            expire=expire_period
        )
        access_token.add_service(service_chat)

        # Service RTC
        service_rtc = AccessToken2.ServiceRtc(channel_name, agora_uid)
        service_rtc.add_privilege(AccessToken2.ServiceRtc.kPrivilegeJoinChannel, expire_period)

        if publisher_role == 'true':
            service_rtc.add_privilege(AccessToken2.ServiceRtc.kPrivilegePublishAudioStream, expire_period)
            service_rtc.add_privilege(AccessToken2.ServiceRtc.kPrivilegePublishVideoStream, expire_period)
            service_rtc.add_privilege(AccessToken2.ServiceRtc.kPrivilegePublishDataStream, expire_period)

        access_token.add_service(service_rtc)

        token_info = TokenInfo()
        token_info.token = access_token.build()
        token_info.expire_timestamp = (int(time()) + settings.AGORA_TOKEN_EXPIRE_PERIOD) * 1000  # to millis
        token_info.chat_username = app_user_info.agora_chat_user_name
        token_info.agora_uid = agora_uid
        return token_info
