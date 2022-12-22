package com.jda.application.utils

import android.Manifest

object Constants {
    val INITAIL = 0
    val DEVICE_TYPE_ANDROID = 0
    val UPLOAD_START = 1
    val UPLOAD_END = 2
    val UPLOAD_HIDE = 2

    const val LOG_TAG = "SOMEAPP"

    val VIDEO_CALL = "2"
    val SHOW = true
    val OPTIONAL = 0
    val NO_CONTENT = 200
    val PAGE = 1
    val LIMIT = 20
    val HTTP_UNAUTHORIZED_ACCESS = 401
    val PROFILE_SETUP: Int = 0
    val ANSWER_UPDATE: Int = 1
    val MAX_ANSWER = 5
    var sEmpty_String = ""
    var sSlash = "/"
    var sDot = "."
    const val sDirectroy = ".imageDir"
    const val sProfilePic = "profileImg.jpg"
    const val sNO_MIN = "0"
    const val sNO_MAX = "99999"
    var sSNACK_BAR_MAX_LINES = 3

    const val sSplashTime: Long = 2000
    const val sMaxLength: Int = 30
    const val sMinLength: Int = 15
    const val sPasswordLength: Int = 8
    const val sImageExtension = ".jpg"
    const val sAuthCode = "auth_code"
    const val sProfileData = "profile_data"
    const val sLoginStatus = "loginStatus"
    const val sACCEPTED_REQUEST = 2
    const val sREJECTED_REQUEST = 3
    const val sNULL = "null"
    const val sUploads = "http"
    const val sPathUrl = "uploads/file"
    const val sDeviceToken = "token"
    const val sFileUriKey = "logFileUriKey"
    const val sFilePathKey = "logFilePath"
    var sSubscription: SubscriptionType = SubscriptionType.NoSubscription
    var sIsSubscribed: Boolean = true

    //    var sIsSubscribed: Boolean = true
    const val sPlatform = 1
    var sIsFromChatDialog = false
    var sDialogShowingFirstTime = true


    object Regex {
        val sEmailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        var sPasswordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
    }

    enum class RequestOption {
        REQUEST, ACCEPT
    }

    enum class ErrorType {
        VALIDATION, API, OTHER
    }

    enum class RequestType {
        Post, Get, Put, Delete
    }

    enum class ReviewType {
        CHAT, DATE
    }


    object Logger {
        const val sUrlRequest = "UrlRequest:"
    }

    object ResponseCode {
        const val sSUCCESS = 200
        const val sDATA = "data"
        const val sINTERNAL_SERVER_ERROR = 500
        const val sUNAUTHORIZED_ACCESS = 401
        const val sSOMETHING_WRONG = 10001
        val sFINE_LOC_REQ_CODE = 1001
    }

    object SharedPreferences {
        const val sDEFAULT_INT_VALUE = 0
        const val sDEFAULT_LONG_VALUE = 0
        const val sDEFAULT_FLOAT_VALUE = 0.0f
        const val sDEFAULT_BOOLEAN_VALUE = false
        const val sSHARED_PREFERENCES = "defaultSharedPreferences"
        const val sIsFirstTimeAppOpen = "firstTimeAppOpen"
        const val sIsGameCount = "isGameCount"
        const val sWhichScreen = "whichScreen"
        const val sIsComplete = "isComplete"
        const val sAppData = "app_data"
        const val sAccessToken = "accessToken"
        const val sLoggedState = "loggedState"
        const val sUserTermsAccepted = "userTermsAccepted"
        const val sIsAccountPrivate = "isAccountPrivate"
    }

    enum class SharePreferencesEnum {
        StringType, BooleanType, IntType, FloatType, LongType, ModelType
    }

    enum class FragmentAction {
        ADD, REPLACE, REMOVE
    }

    enum class PicturePermission {
        GALLERY, CAMERA
    }

    object BroadCastCodes {
        const val BROADCAST_CHAT = "BROADCAST_CHAT"
    }

    enum class PreferenceLookingForType {
        Height, Weight, Age, Gender, Ethnicity, LookingForHeight, LookingForWeight, LookingForAge, GenderChoice, EthnicityChoice
    }

    object Permissions {
        const val sWRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
        const val sREAD_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
        const val sRECORD_AUDIO = Manifest.permission.RECORD_AUDIO
        const val sCamera = Manifest.permission.CAMERA
    }

    object RequestCodes {
        const val sWRITE_EXTERNAL_STORAGE = 10002
        const val sCamera = 10003
        const val sTeamNameData = 10004
        const val sREAD_EXTERNAL_STORAGE = 10005
        const val ALL_PERMISSIONS_RESULT = 4884
        const val MULTI_PERMISSIONS_RESULT = 4896
        const val sFACEBOOK_LOGIN = 10008
        const val sGOOGLE_LOGIN = 10009
    }

    object RequestCodesSelfie {
        const val sWRITE_EXTERNAL_STORAGE = 10082
        const val sCamera = 10083
        const val sTeamNameData = 10084
        const val sREAD_EXTERNAL_STORAGE = 10085
        const val sAudioRecord = 10086
    }

    object SocialLoginKeys {
        const val sUniqueID = "uniqueID"
        const val sEmailAddress = "emailAddress"
        const val sFirstName = "firstName"
        const val sLastName = "lastName"
        const val sName = "name"
        const val sImageUrl = "imageUrl"
    }

    object BundleParams {
        const val DATA = "data"
        const val PROFILE_DATA = "profile"
        const val sVideoPath = "videoPath"
        const val sWhichFrame = "whichFrame"
        const val sWhichAngle = "whichAngle"
        const val sIsLocalSave = "localSave"
        const val sphoneNumber = "phoneNumber"
        const val IMAGE_PATH = "image_path"
        const val MOVE_NEXT = "move_next"
        const val sImageURL = "imageUrl"
        const val sUserId = "userID"
        const val sIsMatch = "isMatch"
        const val sProfileData = "profileData"
        const val sChatListData = "chatListData"
        const val sBlindListData = "blindListData"
        const val sProfile = "profile"
        const val sPosition = "adapterPosition"
        const val sConversationId = "conversationId"
        const val sHeight = "height"
        const val sFromHome = "fromHome"
        const val sFromChatRoom = "fromChat"
        const val sUserName = "userName"
        const val sUserImage = "userImage"
        const val sFromSubscription = "fromSubscription"
        const val sURl = "url"
        const val sBeforeLogIn = "beforeLogIn"
    }

    enum class ChoosePhotoAction(val values: Int) {
        GALLERY(4882), CAMERA(4883)
    }

    enum class ApiName {
        ReportUser, BlockUser, VideoRequest, UnBlurRequestList, MatchesRequestAccept, MatchesRequestDecline, GetUserProfile, UploadFile, ChangeProfilePic, EditProfile, SaveAnswers, UpdateAnswer, ChangePassword,
        ChatRequestAccept, ChatRequestDecline, ChatRequestList, ClearChat, BlindDateAccept, BlindDateReject, MatchDelete
    }

    object Socket_id {
        val GIF_ID = 5
        val AUDIO_ID = 2
        val TEXT_ID = 0
        val CHAT_RATE_TYPE = 1

        const val sendMessage = "sendMessage"
        const val eventTyping = "typing"
        const val notTypingEvent = "typingStop"
        const val lastReadEvent = "messageRead"
        const val arrivalMessage = "newMessage"
        const val deleteMessage = "deleteMessage"
        const val unsendMessage = "unsendMessage"

        const val connectEvent = "connectOk"
        const val disconnectEvent = "disconnectOk"
        const val likeEvent = "like"
        const val blockUserEvent = "blockUser"
        const val unBlockUserEvent = "unBlockUser"
        const val dislikeEvent = "dislike"
        const val undoRequest = "undoRequest"
        const val typingEvent = "typing"
        const val sendMessageEvent = "sendMessage"
        const val receiveMessageEvent = "recieveMessage"
        const val acceptMatch = "acceptMatch"
        const val rejectMatch = "rejectMatch"
        const val deleteChat = "deleteChat"
        const val userRated = "userRated"
        const val deleteMatchEvent = "deleteMatch"
        const val clearChatEvent = "clearChat"
    }

    object Notification {
        const val sCHANNEL_ID = "1000"
        const val type = "type"
        const val sNewChatId = "newChatId"
    }

    object FacebookPermissionKeys {
        const val sPUBLIC_PROFILE = "public_profile"
        const val sFIELDS = "fields"
        const val sNAME_EMAIL_ID = "name,email,id,first_name,last_name"
        const val sUSER_NAME = "name"
        const val sUSER_FIRST_NAME = "first_name"
        const val sUSER_LAST_NAME = "last_name"
        const val sUSER_EMAIL = "email"
        const val sUSER_ID = "id"
        const val sUSER_PROFILE_LINK = "https://graph.facebook.com/"
        const val sUSER_PROFILE_PIC_TYPE = "/picture?width=50&height=50&redirect=false"
    }

    object LinkedInConstants {
        val CLIENT_ID = "78j8r9cyw4sx3r"
        val CLIENT_SECRET = "vUUMsOhtJq9lTNRy"
        val REDIRECT_URI = "https://www.chicmic.in"
        val SCOPE = "r_liteprofile%20r_emailaddress"

        val AUTHURL = "https://www.linkedin.com/oauth/v2/authorization"
        val TOKENURL = "https://www.linkedin.com/oauth/v2/accessToken"
        const val sGET = "GET"
        const val sPOST = "POST"
        const val sCONTENT_TYPE = "Content-Type"
        const val sCONTENT_TYPE_JSON = "application/x-www-form-urlencoded"
        const val sGRANT_TYPE = "authorization_code"
        const val sACCESS_TOKEN = "access_token"
        const val sEXPIRES_IN = "expires_in"
    }

    object LoginType {
        const val sFACEBOOK_LOGIN: Int = 4
        const val sGOOGLE_LOGIN: Int = 2
        const val sLINKED_LOGIN: Int = 5
    }

    object Params {
        const val sPAGE = "page"
    }

    object QuestionType {
        const val sCHOICE = 1
        const val sTEXT = 2
        const val sNESTED_QUESTIONS = 3
    }

    object NestedQuestionChoice {
        const val sCHOICE_YES = "YES"
        const val sCHOICE_NO = "NO"
    }

    object ScreenStatus {
        const val sLOGGEDIN = 0
        const val sPREFERENCES_SET = 1
        const val sQUESTIONS_ANSWERED = 2
        const val sPROFILE_COMPLETED = 3
    }

    object HashMapParamKeys {
        const val sPAGE = "page"
        const val sLIMIT = "limit"
        const val sUSER_ID = "userId"
        const val sMATCH_ID = "matchId"
        const val sCHAT_ID = "chatId"
        const val sLAST_MESSAGE_ID = "lastMessageId"
        const val sTYPE = "type"
        const val sTEXT = "text"
        const val sOTHER_USER_ID = "otherUserId"
        const val sPRODUCT_ID = "productId"
        const val sPURCHASE_TOKEN = "purchaseToken"
        const val sPACKAGE_NAME = "packageName"
    }

    enum class SubscriptionType(var productId: String) {
        NoSubscription(""), SubscriptionOneMonth("subscription_1_month"), SubscriptionThreeMonths("subscription_3_months"), SubscriptionSixMonths("subscription_6_months")
    }

    enum class MatchListStatus(val values: Int) {
        Match(3), UnMatch(2)
    }
}