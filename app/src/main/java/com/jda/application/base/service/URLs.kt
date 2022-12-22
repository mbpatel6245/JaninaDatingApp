package com.jda.application.base.service

object URLs {
    object Retrofit {
        const val sAuthorizationHeader = "Authorization"
        const val sContentType = "Content-type"
        const val sContentTypeJson = "application/json"
        const val sXRequestWith = "X-Requested-With"
        const val sXmlHttpRequest = "XMLHttpRequest"
        const val sUSE_LIVE_SERVER = true // use bit for live or local
        const val sAPI_LOCAL_BASE_URL ="https://someappdatingdomain.link/"
//            "https://b590-122-173-42-95.ngrok.io" //put local server url here

        //        const val sAPI_LIVE_BASE_URL = "http://18.188.34.251:3001/" //put live server url here  not working on 25 feb
        const val sAPI_LIVE_BASE_URL = "https://someappdatingdomain.link/"
//            "https://18.119.3.111:3001/" //put live server url here changed lon 25v feb by Gagan
//        const val sAPI_LIVE_BASE_URL = "https://255b-112-196-113-2.ngrok.io/" //put live server url here

        const val sFacebookProfilePicUrl = "https://graph.facebook.com/"
        const val sTime = 120

        //        const val sAPI_BASE_LIVE_URL_IMAGE = "https://18.119.3.111:3001"
        const val sAPI_BASE_LIVE_URL_IMAGE = "https://someappdatingdomain.link"

        //        const val sAPI_BASE_LIVE_URL_IMAGE = "http://18.188.34.251:3001"
        const val sAPI_BASE_LOCAL_URL_IMAGE = sAPI_LOCAL_BASE_URL
    }


    object APIs {
        const val sSocialLoginApi = "api/v1/user/socialLogin"
        const val sGetQuestionList = "api/v1/user/questions"
        const val sSaveAnswers = "api/v1/user/answers"
        const val sUpdateAnswers = "v1/updateAnswer"
        const val sForgotPassword = "v1/user/forgotPassword"
        const val sSetPreferences = "api/v1/user/preferences"
        const val sRatingGet = "api/v1/user/rating"
        const val sEditProfile = "v1/user/editProfile"
        const val sUploadFile = "api/v1/user/upload"
        const val sGetAnswerList = "api/v1/user/answers"
        const val sChangeProfilePic = "v1/user/changeProfilePic"
        const val sUpdateVideo = "v1/user/videos"
        const val sApiResetPassword = "v1/user/resetPassword"
        const val sChnagePassword = "v1/user/editSecurity"
        const val sVideoRequest = "v1/request/videoRequest"
        const val sGetOtherProfileData = "v1/user/fetchUserbyId"
        const val sListOfVideoRequest = "api/v1/user/matches"
        const val sListOfChatRequest = "v1/request/listOfChatRequest"
        const val sApiHome = "api/v1/user/home"
        const val sApiMatchList = "api/v1/user/matches"
        const val sApiChatRequest = "v1/request/chatRequest"
        const val sApiGetUserProfile = "api/v1/user/profile"
        const val sApiCreateChat = "api/v1/user/chat"
        const val sApiChatRating = "api/v1/user/rate/chat"
        const val sApiRejectChatRating = "api/v1/user/rate/chat/reject"
        const val sApiMeetRating = "api/v1/user/rate/meet"
        const val sApiChatList = "api/v1/user/chats"
        const val sApiDeleteUserFromChatList = "v1/conversation/deleteUserFromChatList"
        const val sApiLogout = "api/v1/user/logout"
        const val sSendSubscriptionApiRequest = "api/v1/user/inApp/verify"
        const val sApiGalleryCall = "api/v1/user/gallery"
        const val sFacebookProfilePic = "/picture"
        const val sLinkedInEmailUrl =
            "https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))&oauth2_access_token="
        const val sLinkedInProfileUrl =
            "https://api.linkedin.com/v2/me?projection=(id,firstName,lastName,profilePicture(displayImage~:playableStreams))&oauth2_access_token="
        const val sMBTI_TYPE_LINK = "https://www.16personalities.com/free-personality-test"
        const val sDeleteAllProfilePics = "api/v1/user/gallery/all"
        const val sEndUserAggrement = "api/v1/user/endUserAgreement"
        const val sTerms = "api/v1/user/terms"
        const val sAboutUs = "api/v1/user/aboutUs"
        const val sPrivacyPolicy = "api/v1/user/privacyPolicy"
        const val sEnDUserAgrrementUrl = Retrofit.sAPI_LIVE_BASE_URL + sEndUserAggrement
        const val sTermsUrl = Retrofit.sAPI_LIVE_BASE_URL + sTerms
        const val sAboutUsUrl = Retrofit.sAPI_LIVE_BASE_URL + sAboutUs
        const val sPrivacyPolicyUrl = Retrofit.sAPI_LIVE_BASE_URL + sPrivacyPolicy
    }

    object ApisParamsName {
        const val sHeight = "height"
        const val sWidth = "width"
        const val sRedirect = "redirect"
    }
}