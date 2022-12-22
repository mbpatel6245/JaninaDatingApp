package com.jda.application.fragments.messagesModule

import com.jda.application.base.presenter.BasePresenterImp
import com.jda.application.base.presenter.MVPView
import com.jda.application.base.service.URLs
import com.jda.application.utils.Constants

class ChatPresenterImpl(mvpView: MVPView) : BasePresenterImp(mvpView), ChatPresenter {
    override fun apiGetChatList(param: Map<String, Any>, isPaginationCall: Boolean) {
        doRequest(URLs.APIs.sApiChatList, param, ChatListSuccessModel::class.java, Constants.RequestType.Get, true, isPaginationCall)
    }

    override fun apiDeleteUser(data: DeleteUserRequestModel) {
        doRequest(URLs.APIs.sApiDeleteUserFromChatList, data, DeleteUserSuccessModel::class.java, Constants.RequestType.Post, true)
    }
}