import json
import traceback

from django.core.exceptions import PermissionDenied
from django.http import JsonResponse, HttpRequest
from django.shortcuts import render

# Create your views here.
from django.views.decorators.csrf import csrf_exempt

from users.exceptions import RestClientException
from users.services import user_service


@csrf_exempt
def user_register(request: HttpRequest):
    """
    This api is used to register a user for your app. User name and password is used in this sample project,
    you can use any other format for your user account ,such as phone number.
    """
    if request.method == 'POST':
        try:
            body = json.loads(request.body.decode('utf-8'))
            user_account = body.get('userAccount', None)
            user_password = body.get('userPassword', None)

            user_service.register(user_account, user_password)

            result = {
                'code': 200
            }
        except RestClientException as e:
            result = {
                'code': e.status_code,
                'message': e.exception
            }
            traceback.print_exc()
        return JsonResponse(result)
    else:
        raise PermissionDenied()


@csrf_exempt
def user_login(request: HttpRequest):
    """
    User login on your app server and get a agora token for chat service.
    """
    if request.method == 'POST':
        body = json.loads(request.body.decode('utf-8'))
        user_account = body.get('userAccount', None)
        user_password = body.get('userPassword', None)

        try:
            token_info = user_service.login(user_account, user_password)
            result = {
                'code': 200,
                'accessToken': token_info.token,
                'expireTimestamp': token_info.expire_timestamp,
                'chatUserName': token_info.chat_username
            }
        except Exception as e:
            result = {
                'code': 400,
                'message': str(e)
            }
            traceback.print_exc()
        return JsonResponse(result)
    else:
        raise PermissionDenied()


def get_token(request: HttpRequest):
    """
    Get a token including user and rtc privileges.
    """
    if request.method == 'GET':
        user_account = request.GET.get('userAccount')
        channel_name = request.GET.get('channelName')
        publisher_role = request.GET.get('publisherRole', 'false')

        try:
            token_info = user_service.get_token(user_account, channel_name, publisher_role)
            result = {
                'code': 200,
                'accessToken': token_info.token,
                'expireTimestamp': token_info.expire_timestamp,
                'chatUserName': token_info.chat_username,
                'agoraUid': token_info.agora_uid
            }
        except Exception as e:
            result = {
                'code': 400,
                'message': str(e)
            }
            traceback.print_exc()
        return JsonResponse(result)
    else:
        raise PermissionDenied()
