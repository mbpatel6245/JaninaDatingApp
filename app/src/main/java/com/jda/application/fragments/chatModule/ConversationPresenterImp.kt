package com.jda.application.fragments.chatModule

import com.jda.application.base.presenter.BasePresenterImp
import com.jda.application.base.presenter.MVPView
import com.jda.application.base.service.URLs
import com.jda.application.utils.Constants

class ConversationPresenterImp(mvpView: MVPView) : BasePresenterImp(mvpView), ConversationPresenter {
    override fun apiGetChat(param: Map<String, Any>, isPagination: Boolean) {
        doRequest(URLs.APIs.sApiCreateChat, param, ConversationSuccessModel::class.java, Constants.RequestType.Get, pIsPaginatedCall = isPagination)
    }

    override fun apiChatRating(param: ChatRatingRequest) {
        doRequest(URLs.APIs.sApiChatRating, param, ChatRatingResponse::class.java, Constants.RequestType.Post)
    }

    override fun apiMeetRating(param: MeetRatingRequest) {
        doRequest(URLs.APIs.sApiMeetRating, param, MeetRatingResponse::class.java, Constants.RequestType.Post)
    }

    override fun apiRejectChatRating(param: ChatRatingRejectRequest) {
        doRequest(URLs.APIs.sApiRejectChatRating, param, ChatRatingRejectResponse::class.java, Constants.RequestType.Post)
    }
}
