from django.urls import path

from users import views

urlpatterns = [
    path('app/user/register', views.user_register, name='user_register'),
    path('app/user/login', views.user_login, name='user_login'),
    path('token', views.get_token, name='get_token'),
]
