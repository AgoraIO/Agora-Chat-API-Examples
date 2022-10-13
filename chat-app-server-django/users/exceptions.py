

class ASNotFoundException(Exception):
    def __init__(self, user_account):
        Exception.__init__(self, f"User account {user_account} does not exist")


class ASPasswordErrorException(Exception):
    def __init__(self):
        Exception.__init__(self, "User password error")


class ASDuplicateUniquePropertyExistsException(Exception):
    def __init__(self, user_account):
        Exception.__init__(self, f"User account {user_account} already exists")


class RestClientException(Exception):
    def __init__(self, status_code, exception):
        Exception.__init__(self, "Register chat user error.")
        self.status_code = status_code
        self.exception = exception


class InvalidParametersException(Exception):
    def __init__(self):
        Exception.__init__(self, "Please make sure all parameters are not blank")


class TokenBuildError(Exception):
    def __init__(self, message):
        Exception.__init__(self, message)
