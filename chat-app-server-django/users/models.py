from django.db import models

# Create your models here.
from django.db.models import UniqueConstraint


class AppUserInfo(models.Model):
    """
    Represents an app user information
    """
    class Meta:
        constraints = [
            models.UniqueConstraint(fields=['id', 'user_account'], name='id_user_account_unique_constraint')]

    user_account = models.CharField(max_length=32)
    user_password = models.CharField(max_length=32, null=True, default=None)
    agora_chat_user_name = models.CharField(max_length=32)
    agora_chat_user_uuid = models.CharField(max_length=36, null=True, default=None)
